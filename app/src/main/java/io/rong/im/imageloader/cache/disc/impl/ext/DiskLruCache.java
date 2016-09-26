/*     */ package io.rong.imageloader.cache.disc.impl.ext;
/*     */ 
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.Closeable;
/*     */ import java.io.EOFException;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Writer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ final class DiskLruCache
/*     */   implements Closeable
/*     */ {
/*     */   static final String JOURNAL_FILE = "journal";
/*     */   static final String JOURNAL_FILE_TEMP = "journal.tmp";
/*     */   static final String JOURNAL_FILE_BACKUP = "journal.bkp";
/*     */   static final String MAGIC = "libcore.io.DiskLruCache";
/*     */   static final String VERSION_1 = "1";
/*     */   static final long ANY_SEQUENCE_NUMBER = -1L;
/*  94 */   static final Pattern LEGAL_KEY_PATTERN = Pattern.compile("[a-z0-9_-]{1,64}");
/*     */   private static final String CLEAN = "CLEAN";
/*     */   private static final String DIRTY = "DIRTY";
/*     */   private static final String REMOVE = "REMOVE";
/*     */   private static final String READ = "READ";
/*     */   private final File directory;
/*     */   private final File journalFile;
/*     */   private final File journalFileTmp;
/*     */   private final File journalFileBackup;
/*     */   private final int appVersion;
/*     */   private long maxSize;
/*     */   private int maxFileCount;
/*     */   private final int valueCount;
/* 148 */   private long size = 0L;
/* 149 */   private int fileCount = 0;
/*     */   private Writer journalWriter;
/* 151 */   private final LinkedHashMap<String, Entry> lruEntries = new LinkedHashMap(0, 0.75F, true);
/*     */   private int redundantOpCount;
/* 160 */   private long nextSequenceNumber = 0L;
/*     */ 
/* 163 */   final ThreadPoolExecutor executorService = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
/*     */ 
/* 165 */   private final Callable<Void> cleanupCallable = new Object() {
/*     */     public Void call() throws Exception {
/* 167 */       synchronized (DiskLruCache.this) {
/* 168 */         if (DiskLruCache.this.journalWriter == null) {
/* 169 */           return null;
/*     */         }
/* 171 */         DiskLruCache.this.trimToSize();
/* 172 */         DiskLruCache.this.trimToFileCount();
/* 173 */         if (DiskLruCache.this.journalRebuildRequired()) {
/* 174 */           DiskLruCache.this.rebuildJournal();
/* 175 */           DiskLruCache.access$502(DiskLruCache.this, 0);
/*     */         }
/*     */       }
/* 178 */       return null;
/*     */     }
/* 165 */   };
/*     */ 
/* 757 */   private static final OutputStream NULL_OUTPUT_STREAM = new OutputStream() {
/*     */     public void write(int b) throws IOException {  } } ;
/*     */ 
/*     */   private DiskLruCache(File directory, int appVersion, int valueCount, long maxSize, int maxFileCount)
/*     */   {
/* 183 */     this.directory = directory;
/* 184 */     this.appVersion = appVersion;
/* 185 */     this.journalFile = new File(directory, "journal");
/* 186 */     this.journalFileTmp = new File(directory, "journal.tmp");
/* 187 */     this.journalFileBackup = new File(directory, "journal.bkp");
/* 188 */     this.valueCount = valueCount;
/* 189 */     this.maxSize = maxSize;
/* 190 */     this.maxFileCount = maxFileCount;
/*     */   }
/*     */ 
/*     */   public static DiskLruCache open(File directory, int appVersion, int valueCount, long maxSize, int maxFileCount)
/*     */     throws IOException
/*     */   {
/* 205 */     if (maxSize <= 0L) {
/* 206 */       throw new IllegalArgumentException("maxSize <= 0");
/*     */     }
/* 208 */     if (maxFileCount <= 0) {
/* 209 */       throw new IllegalArgumentException("maxFileCount <= 0");
/*     */     }
/* 211 */     if (valueCount <= 0) {
/* 212 */       throw new IllegalArgumentException("valueCount <= 0");
/*     */     }
/*     */ 
/* 216 */     File backupFile = new File(directory, "journal.bkp");
/* 217 */     if (backupFile.exists()) {
/* 218 */       File journalFile = new File(directory, "journal");
/*     */ 
/* 220 */       if (journalFile.exists())
/* 221 */         backupFile.delete();
/*     */       else {
/* 223 */         renameTo(backupFile, journalFile, false);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 228 */     DiskLruCache cache = new DiskLruCache(directory, appVersion, valueCount, maxSize, maxFileCount);
/* 229 */     if (cache.journalFile.exists()) {
/*     */       try {
/* 231 */         cache.readJournal();
/* 232 */         cache.processJournal();
/* 233 */         cache.journalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cache.journalFile, true), Util.US_ASCII));
/*     */ 
/* 235 */         return cache;
/*     */       } catch (IOException journalIsCorrupt) {
/* 237 */         System.out.println("DiskLruCache " + directory + " is corrupt: " + journalIsCorrupt.getMessage() + ", removing");
/*     */ 
/* 243 */         cache.delete();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 248 */     directory.mkdirs();
/* 249 */     cache = new DiskLruCache(directory, appVersion, valueCount, maxSize, maxFileCount);
/* 250 */     cache.rebuildJournal();
/* 251 */     return cache;
/*     */   }
/*     */ 
/*     */   private void readJournal() throws IOException {
/* 255 */     StrictLineReader reader = new StrictLineReader(new FileInputStream(this.journalFile), Util.US_ASCII);
/*     */     try {
/* 257 */       String magic = reader.readLine();
/* 258 */       String version = reader.readLine();
/* 259 */       String appVersionString = reader.readLine();
/* 260 */       String valueCountString = reader.readLine();
/* 261 */       String blank = reader.readLine();
/* 262 */       if ((!"libcore.io.DiskLruCache".equals(magic)) || (!"1".equals(version)) || (!Integer.toString(this.appVersion).equals(appVersionString)) || (!Integer.toString(this.valueCount).equals(valueCountString)) || (!"".equals(blank)))
/*     */       {
/* 267 */         throw new IOException("unexpected journal header: [" + magic + ", " + version + ", " + valueCountString + ", " + blank + "]");
/*     */       }
/*     */ 
/* 271 */       int lineCount = 0;
/*     */       try {
/*     */         while (true) {
/* 274 */           readJournalLine(reader.readLine());
/* 275 */           lineCount++;
/*     */         }
/*     */       }
/*     */       catch (EOFException endOfJournal)
/*     */       {
/* 280 */         this.redundantOpCount = (lineCount - this.lruEntries.size());
/*     */ 
/* 282 */         Util.closeQuietly(reader);
/*     */       } } finally { Util.closeQuietly(reader); }
/*     */   }
/*     */ 
/*     */   private void readJournalLine(String line) throws IOException
/*     */   {
/* 287 */     int firstSpace = line.indexOf(32);
/* 288 */     if (firstSpace == -1) {
/* 289 */       throw new IOException("unexpected journal line: " + line);
/*     */     }
/*     */ 
/* 292 */     int keyBegin = firstSpace + 1;
/* 293 */     int secondSpace = line.indexOf(32, keyBegin);
/*     */     String key;
/* 295 */     if (secondSpace == -1) {
/* 296 */       String key = line.substring(keyBegin);
/* 297 */       if ((firstSpace == "REMOVE".length()) && (line.startsWith("REMOVE"))) {
/* 298 */         this.lruEntries.remove(key);
/* 299 */         return;
/*     */       }
/*     */     } else {
/* 302 */       key = line.substring(keyBegin, secondSpace);
/*     */     }
/*     */ 
/* 305 */     Entry entry = (Entry)this.lruEntries.get(key);
/* 306 */     if (entry == null) {
/* 307 */       entry = new Entry(key, null);
/* 308 */       this.lruEntries.put(key, entry);
/*     */     }
/*     */ 
/* 311 */     if ((secondSpace != -1) && (firstSpace == "CLEAN".length()) && (line.startsWith("CLEAN"))) {
/* 312 */       String[] parts = line.substring(secondSpace + 1).split(" ");
/* 313 */       Entry.access$702(entry, true);
/* 314 */       Entry.access$802(entry, null);
/* 315 */       entry.setLengths(parts);
/* 316 */     } else if ((secondSpace == -1) && (firstSpace == "DIRTY".length()) && (line.startsWith("DIRTY"))) {
/* 317 */       Entry.access$802(entry, new Editor(entry, null));
/* 318 */     } else if ((secondSpace != -1) || (firstSpace != "READ".length()) || (!line.startsWith("READ")))
/*     */     {
/* 321 */       throw new IOException("unexpected journal line: " + line);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processJournal()
/*     */     throws IOException
/*     */   {
/* 330 */     deleteIfExists(this.journalFileTmp);
/* 331 */     for (Iterator i = this.lruEntries.values().iterator(); i.hasNext(); ) {
/* 332 */       Entry entry = (Entry)i.next();
/* 333 */       if (entry.currentEditor == null) {
/* 334 */         for (int t = 0; t < this.valueCount; t++) {
/* 335 */           this.size += entry.lengths[t];
/* 336 */           this.fileCount += 1;
/*     */         }
/*     */       } else {
/* 339 */         Entry.access$802(entry, null);
/* 340 */         for (int t = 0; t < this.valueCount; t++) {
/* 341 */           deleteIfExists(entry.getCleanFile(t));
/* 342 */           deleteIfExists(entry.getDirtyFile(t));
/*     */         }
/* 344 */         i.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private synchronized void rebuildJournal()
/*     */     throws IOException
/*     */   {
/* 354 */     if (this.journalWriter != null) {
/* 355 */       this.journalWriter.close();
/*     */     }
/*     */ 
/* 358 */     Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.journalFileTmp), Util.US_ASCII));
/*     */     try
/*     */     {
/* 361 */       writer.write("libcore.io.DiskLruCache");
/* 362 */       writer.write("\n");
/* 363 */       writer.write("1");
/* 364 */       writer.write("\n");
/* 365 */       writer.write(Integer.toString(this.appVersion));
/* 366 */       writer.write("\n");
/* 367 */       writer.write(Integer.toString(this.valueCount));
/* 368 */       writer.write("\n");
/* 369 */       writer.write("\n");
/*     */ 
/* 371 */       for (Entry entry : this.lruEntries.values())
/* 372 */         if (entry.currentEditor != null)
/* 373 */           writer.write("DIRTY " + entry.key + '\n');
/*     */         else
/* 375 */           writer.write("CLEAN " + entry.key + entry.getLengths() + '\n');
/*     */     }
/*     */     finally
/*     */     {
/* 379 */       writer.close();
/*     */     }
/*     */ 
/* 382 */     if (this.journalFile.exists()) {
/* 383 */       renameTo(this.journalFile, this.journalFileBackup, true);
/*     */     }
/* 385 */     renameTo(this.journalFileTmp, this.journalFile, false);
/* 386 */     this.journalFileBackup.delete();
/*     */ 
/* 388 */     this.journalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.journalFile, true), Util.US_ASCII));
/*     */   }
/*     */ 
/*     */   private static void deleteIfExists(File file) throws IOException
/*     */   {
/* 393 */     if ((file.exists()) && (!file.delete()))
/* 394 */       throw new IOException();
/*     */   }
/*     */ 
/*     */   private static void renameTo(File from, File to, boolean deleteDestination) throws IOException
/*     */   {
/* 399 */     if (deleteDestination) {
/* 400 */       deleteIfExists(to);
/*     */     }
/* 402 */     if (!from.renameTo(to))
/* 403 */       throw new IOException();
/*     */   }
/*     */ 
/*     */   public synchronized Snapshot get(String key)
/*     */     throws IOException
/*     */   {
/* 413 */     checkNotClosed();
/* 414 */     validateKey(key);
/* 415 */     Entry entry = (Entry)this.lruEntries.get(key);
/* 416 */     if (entry == null) {
/* 417 */       return null;
/*     */     }
/*     */ 
/* 420 */     if (!entry.readable) {
/* 421 */       return null;
/*     */     }
/*     */ 
/* 427 */     File[] files = new File[this.valueCount];
/* 428 */     InputStream[] ins = new InputStream[this.valueCount];
/*     */     try
/*     */     {
/* 431 */       for (int i = 0; i < this.valueCount; i++) {
/* 432 */         File file = entry.getCleanFile(i);
/* 433 */         files[i] = file;
/* 434 */         ins[i] = new FileInputStream(file);
/*     */       }
/*     */     }
/*     */     catch (FileNotFoundException e) {
/* 438 */       for (int i = 0; (i < this.valueCount) && 
/* 439 */         (ins[i] != null); i++)
/*     */       {
/* 440 */         Util.closeQuietly(ins[i]);
/*     */       }
/*     */ 
/* 445 */       return null;
/*     */     }
/*     */ 
/* 448 */     this.redundantOpCount += 1;
/* 449 */     this.journalWriter.append("READ " + key + '\n');
/* 450 */     if (journalRebuildRequired()) {
/* 451 */       this.executorService.submit(this.cleanupCallable);
/*     */     }
/*     */ 
/* 454 */     return new Snapshot(key, entry.sequenceNumber, files, ins, entry.lengths, null);
/*     */   }
/*     */ 
/*     */   public Editor edit(String key)
/*     */     throws IOException
/*     */   {
/* 462 */     return edit(key, -1L);
/*     */   }
/*     */ 
/*     */   private synchronized Editor edit(String key, long expectedSequenceNumber) throws IOException {
/* 466 */     checkNotClosed();
/* 467 */     validateKey(key);
/* 468 */     Entry entry = (Entry)this.lruEntries.get(key);
/* 469 */     if ((expectedSequenceNumber != -1L) && ((entry == null) || (entry.sequenceNumber != expectedSequenceNumber)))
/*     */     {
/* 471 */       return null;
/*     */     }
/* 473 */     if (entry == null) {
/* 474 */       entry = new Entry(key, null);
/* 475 */       this.lruEntries.put(key, entry);
/* 476 */     } else if (entry.currentEditor != null) {
/* 477 */       return null;
/*     */     }
/*     */ 
/* 480 */     Editor editor = new Editor(entry, null);
/* 481 */     Entry.access$802(entry, editor);
/*     */ 
/* 484 */     this.journalWriter.write("DIRTY " + key + '\n');
/* 485 */     this.journalWriter.flush();
/* 486 */     return editor;
/*     */   }
/*     */ 
/*     */   public File getDirectory()
/*     */   {
/* 491 */     return this.directory;
/*     */   }
/*     */ 
/*     */   public synchronized long getMaxSize()
/*     */   {
/* 499 */     return this.maxSize;
/*     */   }
/*     */ 
/*     */   public synchronized int getMaxFileCount()
/*     */   {
/* 504 */     return this.maxFileCount;
/*     */   }
/*     */ 
/*     */   public synchronized void setMaxSize(long maxSize)
/*     */   {
/* 512 */     this.maxSize = maxSize;
/* 513 */     this.executorService.submit(this.cleanupCallable);
/*     */   }
/*     */ 
/*     */   public synchronized long size()
/*     */   {
/* 522 */     return this.size;
/*     */   }
/*     */ 
/*     */   public synchronized long fileCount()
/*     */   {
/* 531 */     return this.fileCount;
/*     */   }
/*     */ 
/*     */   private synchronized void completeEdit(Editor editor, boolean success) throws IOException {
/* 535 */     Entry entry = editor.entry;
/* 536 */     if (entry.currentEditor != editor) {
/* 537 */       throw new IllegalStateException();
/*     */     }
/*     */ 
/* 541 */     if ((success) && (!entry.readable)) {
/* 542 */       for (int i = 0; i < this.valueCount; i++) {
/* 543 */         if (editor.written[i] == 0) {
/* 544 */           editor.abort();
/* 545 */           throw new IllegalStateException("Newly created entry didn't create value for index " + i);
/*     */         }
/* 547 */         if (!entry.getDirtyFile(i).exists()) {
/* 548 */           editor.abort();
/* 549 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 554 */     for (int i = 0; i < this.valueCount; i++) {
/* 555 */       File dirty = entry.getDirtyFile(i);
/* 556 */       if (success) {
/* 557 */         if (dirty.exists()) {
/* 558 */           File clean = entry.getCleanFile(i);
/* 559 */           dirty.renameTo(clean);
/* 560 */           long oldLength = entry.lengths[i];
/* 561 */           long newLength = clean.length();
/* 562 */           entry.lengths[i] = newLength;
/* 563 */           this.size = (this.size - oldLength + newLength);
/* 564 */           this.fileCount += 1;
/*     */         }
/*     */       }
/* 567 */       else deleteIfExists(dirty);
/*     */ 
/*     */     }
/*     */ 
/* 571 */     this.redundantOpCount += 1;
/* 572 */     Entry.access$802(entry, null);
/* 573 */     if ((entry.readable | success)) {
/* 574 */       Entry.access$702(entry, true);
/* 575 */       this.journalWriter.write("CLEAN " + entry.key + entry.getLengths() + '\n');
/* 576 */       if (success)
/* 577 */         Entry.access$1302(entry, this.nextSequenceNumber++);
/*     */     }
/*     */     else {
/* 580 */       this.lruEntries.remove(entry.key);
/* 581 */       this.journalWriter.write("REMOVE " + entry.key + '\n');
/*     */     }
/* 583 */     this.journalWriter.flush();
/*     */ 
/* 585 */     if ((this.size > this.maxSize) || (this.fileCount > this.maxFileCount) || (journalRebuildRequired()))
/* 586 */       this.executorService.submit(this.cleanupCallable);
/*     */   }
/*     */ 
/*     */   private boolean journalRebuildRequired()
/*     */   {
/* 595 */     int redundantOpCompactThreshold = 2000;
/* 596 */     return (this.redundantOpCount >= 2000) && (this.redundantOpCount >= this.lruEntries.size());
/*     */   }
/*     */ 
/*     */   public synchronized boolean remove(String key)
/*     */     throws IOException
/*     */   {
/* 607 */     checkNotClosed();
/* 608 */     validateKey(key);
/* 609 */     Entry entry = (Entry)this.lruEntries.get(key);
/* 610 */     if ((entry == null) || (entry.currentEditor != null)) {
/* 611 */       return false;
/*     */     }
/*     */ 
/* 614 */     for (int i = 0; i < this.valueCount; i++) {
/* 615 */       File file = entry.getCleanFile(i);
/* 616 */       if ((file.exists()) && (!file.delete())) {
/* 617 */         throw new IOException("failed to delete " + file);
/*     */       }
/* 619 */       this.size -= entry.lengths[i];
/* 620 */       this.fileCount -= 1;
/* 621 */       entry.lengths[i] = 0L;
/*     */     }
/*     */ 
/* 624 */     this.redundantOpCount += 1;
/* 625 */     this.journalWriter.append("REMOVE " + key + '\n');
/* 626 */     this.lruEntries.remove(key);
/*     */ 
/* 628 */     if (journalRebuildRequired()) {
/* 629 */       this.executorService.submit(this.cleanupCallable);
/*     */     }
/*     */ 
/* 632 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized boolean isClosed()
/*     */   {
/* 637 */     return this.journalWriter == null;
/*     */   }
/*     */ 
/*     */   private void checkNotClosed() {
/* 641 */     if (this.journalWriter == null)
/* 642 */       throw new IllegalStateException("cache is closed");
/*     */   }
/*     */ 
/*     */   public synchronized void flush()
/*     */     throws IOException
/*     */   {
/* 648 */     checkNotClosed();
/* 649 */     trimToSize();
/* 650 */     trimToFileCount();
/* 651 */     this.journalWriter.flush();
/*     */   }
/*     */ 
/*     */   public synchronized void close() throws IOException
/*     */   {
/* 656 */     if (this.journalWriter == null) {
/* 657 */       return;
/*     */     }
/* 659 */     for (Entry entry : new ArrayList(this.lruEntries.values())) {
/* 660 */       if (entry.currentEditor != null) {
/* 661 */         entry.currentEditor.abort();
/*     */       }
/*     */     }
/* 664 */     trimToSize();
/* 665 */     trimToFileCount();
/* 666 */     this.journalWriter.close();
/* 667 */     this.journalWriter = null;
/*     */   }
/*     */ 
/*     */   private void trimToSize() throws IOException {
/* 671 */     while (this.size > this.maxSize) {
/* 672 */       Map.Entry toEvict = (Map.Entry)this.lruEntries.entrySet().iterator().next();
/* 673 */       remove((String)toEvict.getKey());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void trimToFileCount() throws IOException {
/* 678 */     while (this.fileCount > this.maxFileCount) {
/* 679 */       Map.Entry toEvict = (Map.Entry)this.lruEntries.entrySet().iterator().next();
/* 680 */       remove((String)toEvict.getKey());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void delete()
/*     */     throws IOException
/*     */   {
/* 690 */     close();
/* 691 */     Util.deleteContents(this.directory);
/*     */   }
/*     */ 
/*     */   private void validateKey(String key) {
/* 695 */     Matcher matcher = LEGAL_KEY_PATTERN.matcher(key);
/* 696 */     if (!matcher.matches())
/* 697 */       throw new IllegalArgumentException("keys must match regex [a-z0-9_-]{1,64}: \"" + key + "\"");
/*     */   }
/*     */ 
/*     */   private static String inputStreamToString(InputStream in) throws IOException
/*     */   {
/* 702 */     return Util.readFully(new InputStreamReader(in, Util.UTF_8));
/*     */   }
/*     */ 
/*     */   private final class Entry
/*     */   {
/*     */     private final String key;
/*     */     private final long[] lengths;
/*     */     private boolean readable;
/*     */     private DiskLruCache.Editor currentEditor;
/*     */     private long sequenceNumber;
/*     */ 
/*     */     private Entry(String key)
/*     */     {
/* 935 */       this.key = key;
/* 936 */       this.lengths = new long[DiskLruCache.this.valueCount];
/*     */     }
/*     */ 
/*     */     public String getLengths() throws IOException {
/* 940 */       StringBuilder result = new StringBuilder();
/* 941 */       for (long size : this.lengths) {
/* 942 */         result.append(' ').append(size);
/*     */       }
/* 944 */       return result.toString();
/*     */     }
/*     */ 
/*     */     private void setLengths(String[] strings) throws IOException
/*     */     {
/* 949 */       if (strings.length != DiskLruCache.this.valueCount) {
/* 950 */         throw invalidLengths(strings);
/*     */       }
/*     */       try
/*     */       {
/* 954 */         for (int i = 0; i < strings.length; i++)
/* 955 */           this.lengths[i] = Long.parseLong(strings[i]);
/*     */       }
/*     */       catch (NumberFormatException e) {
/* 958 */         throw invalidLengths(strings);
/*     */       }
/*     */     }
/*     */ 
/*     */     private IOException invalidLengths(String[] strings) throws IOException {
/* 963 */       throw new IOException(new StringBuilder().append("unexpected journal line: ").append(Arrays.toString(strings)).toString());
/*     */     }
/*     */ 
/*     */     public File getCleanFile(int i) {
/* 967 */       return new File(DiskLruCache.this.directory, new StringBuilder().append(this.key).append(".").append(i).toString());
/*     */     }
/*     */ 
/*     */     public File getDirtyFile(int i) {
/* 971 */       return new File(DiskLruCache.this.directory, new StringBuilder().append(this.key).append(".").append(i).append(".tmp").toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public final class Editor
/*     */   {
/*     */     private final DiskLruCache.Entry entry;
/*     */     private final boolean[] written;
/*     */     private boolean hasErrors;
/*     */     private boolean committed;
/*     */ 
/*     */     private Editor(DiskLruCache.Entry entry)
/*     */     {
/* 772 */       this.entry = entry;
/* 773 */       this.written = (entry.readable ? null : new boolean[DiskLruCache.this.valueCount]);
/*     */     }
/*     */ 
/*     */     public InputStream newInputStream(int index)
/*     */       throws IOException
/*     */     {
/* 781 */       synchronized (DiskLruCache.this) {
/* 782 */         if (this.entry.currentEditor != this) {
/* 783 */           throw new IllegalStateException();
/*     */         }
/* 785 */         if (!this.entry.readable)
/* 786 */           return null;
/*     */         try
/*     */         {
/* 789 */           return new FileInputStream(this.entry.getCleanFile(index));
/*     */         } catch (FileNotFoundException e) {
/* 791 */           return null;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public String getString(int index)
/*     */       throws IOException
/*     */     {
/* 801 */       InputStream in = newInputStream(index);
/* 802 */       return in != null ? DiskLruCache.access$1800(in) : null;
/*     */     }
/*     */ 
/*     */     public OutputStream newOutputStream(int index)
/*     */       throws IOException
/*     */     {
/* 813 */       synchronized (DiskLruCache.this) {
/* 814 */         if (this.entry.currentEditor != this) {
/* 815 */           throw new IllegalStateException();
/*     */         }
/* 817 */         if (!this.entry.readable) {
/* 818 */           this.written[index] = true;
/* 820 */         }File dirtyFile = this.entry.getDirtyFile(index);
/*     */         FileOutputStream outputStream;
/*     */         try { outputStream = new FileOutputStream(dirtyFile);
/*     */         } catch (FileNotFoundException e)
/*     */         {
/* 826 */           DiskLruCache.this.directory.mkdirs();
/*     */           try {
/* 828 */             outputStream = new FileOutputStream(dirtyFile);
/*     */           }
/*     */           catch (FileNotFoundException e2) {
/* 831 */             return DiskLruCache.NULL_OUTPUT_STREAM;
/*     */           }
/*     */         }
/* 834 */         return new FaultHidingOutputStream(outputStream, null);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void set(int index, String value) throws IOException
/*     */     {
/* 840 */       Writer writer = null;
/*     */       try {
/* 842 */         writer = new OutputStreamWriter(newOutputStream(index), Util.UTF_8);
/* 843 */         writer.write(value);
/*     */       } finally {
/* 845 */         Util.closeQuietly(writer);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void commit()
/*     */       throws IOException
/*     */     {
/* 854 */       if (this.hasErrors) {
/* 855 */         DiskLruCache.this.completeEdit(this, false);
/* 856 */         DiskLruCache.this.remove(this.entry.key);
/*     */       } else {
/* 858 */         DiskLruCache.this.completeEdit(this, true);
/*     */       }
/* 860 */       this.committed = true;
/*     */     }
/*     */ 
/*     */     public void abort()
/*     */       throws IOException
/*     */     {
/* 868 */       DiskLruCache.this.completeEdit(this, false);
/*     */     }
/*     */ 
/*     */     public void abortUnlessCommitted() {
/* 872 */       if (!this.committed)
/*     */         try {
/* 874 */           abort();
/*     */         }
/*     */         catch (IOException ignored) {
/*     */         }
/*     */     }
/*     */ 
/*     */     private class FaultHidingOutputStream extends FilterOutputStream {
/*     */       private FaultHidingOutputStream(OutputStream out) {
/* 882 */         super();
/*     */       }
/*     */ 
/*     */       public void write(int oneByte) {
/*     */         try {
/* 887 */           this.out.write(oneByte);
/*     */         } catch (IOException e) {
/* 889 */           DiskLruCache.Editor.access$2402(DiskLruCache.Editor.this, true);
/*     */         }
/*     */       }
/*     */ 
/*     */       public void write(byte[] buffer, int offset, int length) {
/*     */         try {
/* 895 */           this.out.write(buffer, offset, length);
/*     */         } catch (IOException e) {
/* 897 */           DiskLruCache.Editor.access$2402(DiskLruCache.Editor.this, true);
/*     */         }
/*     */       }
/*     */ 
/*     */       public void close() {
/*     */         try {
/* 903 */           this.out.close();
/*     */         } catch (IOException e) {
/* 905 */           DiskLruCache.Editor.access$2402(DiskLruCache.Editor.this, true);
/*     */         }
/*     */       }
/*     */ 
/*     */       public void flush() {
/*     */         try {
/* 911 */           this.out.flush();
/*     */         } catch (IOException e) {
/* 913 */           DiskLruCache.Editor.access$2402(DiskLruCache.Editor.this, true);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public final class Snapshot
/*     */     implements Closeable
/*     */   {
/*     */     private final String key;
/*     */     private final long sequenceNumber;
/*     */     private File[] files;
/*     */     private final InputStream[] ins;
/*     */     private final long[] lengths;
/*     */ 
/*     */     private Snapshot(String key, long sequenceNumber, File[] files, InputStream[] ins, long[] lengths)
/*     */     {
/* 714 */       this.key = key;
/* 715 */       this.sequenceNumber = sequenceNumber;
/* 716 */       this.files = files;
/* 717 */       this.ins = ins;
/* 718 */       this.lengths = lengths;
/*     */     }
/*     */ 
/*     */     public DiskLruCache.Editor edit()
/*     */       throws IOException
/*     */     {
/* 727 */       return DiskLruCache.this.edit(this.key, this.sequenceNumber);
/*     */     }
/*     */ 
/*     */     public File getFile(int index)
/*     */     {
/* 732 */       return this.files[index];
/*     */     }
/*     */ 
/*     */     public InputStream getInputStream(int index)
/*     */     {
/* 737 */       return this.ins[index];
/*     */     }
/*     */ 
/*     */     public String getString(int index) throws IOException
/*     */     {
/* 742 */       return DiskLruCache.access$1800(getInputStream(index));
/*     */     }
/*     */ 
/*     */     public long getLength(int index)
/*     */     {
/* 747 */       return this.lengths[index];
/*     */     }
/*     */ 
/*     */     public void close() {
/* 751 */       for (InputStream in : this.ins)
/* 752 */         Util.closeQuietly(in);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.disc.impl.ext.DiskLruCache
 * JD-Core Version:    0.6.0
 */
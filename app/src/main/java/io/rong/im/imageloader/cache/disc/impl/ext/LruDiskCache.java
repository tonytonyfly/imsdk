/*     */ package io.rong.imageloader.cache.disc.impl.ext;
/*     */ 
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.Bitmap.CompressFormat;
/*     */ import io.rong.imageloader.cache.disc.DiskCache;
/*     */ import io.rong.imageloader.cache.disc.naming.FileNameGenerator;
/*     */ import io.rong.imageloader.utils.IoUtils;
/*     */ import io.rong.imageloader.utils.IoUtils.CopyListener;
/*     */ import io.rong.imageloader.utils.L;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public class LruDiskCache
/*     */   implements DiskCache
/*     */ {
/*     */   public static final int DEFAULT_BUFFER_SIZE = 32768;
/*  43 */   public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
/*     */   public static final int DEFAULT_COMPRESS_QUALITY = 100;
/*     */   private static final String ERROR_ARG_NULL = " argument must be not null";
/*     */   private static final String ERROR_ARG_NEGATIVE = " argument must be positive number";
/*     */   protected DiskLruCache cache;
/*     */   private File reserveCacheDir;
/*     */   protected final FileNameGenerator fileNameGenerator;
/*  55 */   protected int bufferSize = 32768;
/*     */ 
/*  57 */   protected Bitmap.CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
/*  58 */   protected int compressQuality = 100;
/*     */ 
/*     */   public LruDiskCache(File cacheDir, FileNameGenerator fileNameGenerator, long cacheMaxSize)
/*     */     throws IOException
/*     */   {
/*  69 */     this(cacheDir, null, fileNameGenerator, cacheMaxSize, 0);
/*     */   }
/*     */ 
/*     */   public LruDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator, long cacheMaxSize, int cacheMaxFileCount)
/*     */     throws IOException
/*     */   {
/*  84 */     if (cacheDir == null) {
/*  85 */       throw new IllegalArgumentException("cacheDir argument must be not null");
/*     */     }
/*  87 */     if (cacheMaxSize < 0L) {
/*  88 */       throw new IllegalArgumentException("cacheMaxSize argument must be positive number");
/*     */     }
/*  90 */     if (cacheMaxFileCount < 0) {
/*  91 */       throw new IllegalArgumentException("cacheMaxFileCount argument must be positive number");
/*     */     }
/*  93 */     if (fileNameGenerator == null) {
/*  94 */       throw new IllegalArgumentException("fileNameGenerator argument must be not null");
/*     */     }
/*     */ 
/*  97 */     if (cacheMaxSize == 0L) {
/*  98 */       cacheMaxSize = 9223372036854775807L;
/*     */     }
/* 100 */     if (cacheMaxFileCount == 0) {
/* 101 */       cacheMaxFileCount = 2147483647;
/*     */     }
/*     */ 
/* 104 */     this.reserveCacheDir = reserveCacheDir;
/* 105 */     this.fileNameGenerator = fileNameGenerator;
/* 106 */     initCache(cacheDir, reserveCacheDir, cacheMaxSize, cacheMaxFileCount);
/*     */   }
/*     */ 
/*     */   private void initCache(File cacheDir, File reserveCacheDir, long cacheMaxSize, int cacheMaxFileCount) throws IOException
/*     */   {
/*     */     try {
/* 112 */       this.cache = DiskLruCache.open(cacheDir, 1, 1, cacheMaxSize, cacheMaxFileCount);
/*     */     } catch (IOException e) {
/* 114 */       L.e(e);
/* 115 */       if (reserveCacheDir != null) {
/* 116 */         initCache(reserveCacheDir, null, cacheMaxSize, cacheMaxFileCount);
/*     */       }
/* 118 */       if (this.cache == null)
/* 119 */         throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public File getDirectory()
/*     */   {
/* 126 */     return this.cache.getDirectory();
/*     */   }
/*     */ 
/*     */   public File get(String imageUri)
/*     */   {
/* 131 */     DiskLruCache.Snapshot snapshot = null;
/*     */     try {
/* 133 */       snapshot = this.cache.get(getKey(imageUri));
/* 134 */       File localFile = snapshot == null ? null : snapshot.getFile(0);
/*     */       return localFile;
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 136 */       L.e(e);
/* 137 */       Object localObject1 = null;
/*     */       return localObject1;
/*     */     }
/*     */     finally
/*     */     {
/* 139 */       if (snapshot != null)
/* 140 */         snapshot.close(); 
/* 140 */     }throw localObject2;
/*     */   }
/*     */ 
/*     */   public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener)
/*     */     throws IOException
/*     */   {
/* 147 */     DiskLruCache.Editor editor = this.cache.edit(getKey(imageUri));
/* 148 */     if (editor == null) {
/* 149 */       return false;
/*     */     }
/*     */ 
/* 152 */     OutputStream os = new BufferedOutputStream(editor.newOutputStream(0), this.bufferSize);
/* 153 */     boolean copied = false;
/*     */     try {
/* 155 */       copied = IoUtils.copyStream(imageStream, os, listener, this.bufferSize);
/*     */     } finally {
/* 157 */       IoUtils.closeSilently(os);
/* 158 */       if (copied)
/* 159 */         editor.commit();
/*     */       else {
/* 161 */         editor.abort();
/*     */       }
/*     */     }
/* 164 */     return copied;
/*     */   }
/*     */ 
/*     */   public boolean save(String imageUri, Bitmap bitmap) throws IOException
/*     */   {
/* 169 */     DiskLruCache.Editor editor = this.cache.edit(getKey(imageUri));
/* 170 */     if (editor == null) {
/* 171 */       return false;
/*     */     }
/*     */ 
/* 174 */     OutputStream os = new BufferedOutputStream(editor.newOutputStream(0), this.bufferSize);
/* 175 */     boolean savedSuccessfully = false;
/*     */     try {
/* 177 */       savedSuccessfully = bitmap.compress(this.compressFormat, this.compressQuality, os);
/*     */     } finally {
/* 179 */       IoUtils.closeSilently(os);
/*     */     }
/* 181 */     if (savedSuccessfully)
/* 182 */       editor.commit();
/*     */     else {
/* 184 */       editor.abort();
/*     */     }
/* 186 */     return savedSuccessfully;
/*     */   }
/*     */ 
/*     */   public boolean remove(String imageUri)
/*     */   {
/*     */     try {
/* 192 */       return this.cache.remove(getKey(imageUri));
/*     */     } catch (IOException e) {
/* 194 */       L.e(e);
/* 195 */     }return false;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */     try
/*     */     {
/* 202 */       this.cache.close();
/*     */     } catch (IOException e) {
/* 204 */       L.e(e);
/*     */     }
/* 206 */     this.cache = null;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*     */     try {
/* 212 */       this.cache.delete();
/*     */     } catch (IOException e) {
/* 214 */       L.e(e);
/*     */     }
/*     */     try {
/* 217 */       initCache(this.cache.getDirectory(), this.reserveCacheDir, this.cache.getMaxSize(), this.cache.getMaxFileCount());
/*     */     } catch (IOException e) {
/* 219 */       L.e(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getKey(String imageUri) {
/* 224 */     return this.fileNameGenerator.generate(imageUri);
/*     */   }
/*     */ 
/*     */   public void setBufferSize(int bufferSize) {
/* 228 */     this.bufferSize = bufferSize;
/*     */   }
/*     */ 
/*     */   public void setCompressFormat(Bitmap.CompressFormat compressFormat) {
/* 232 */     this.compressFormat = compressFormat;
/*     */   }
/*     */ 
/*     */   public void setCompressQuality(int compressQuality) {
/* 236 */     this.compressQuality = compressQuality;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.disc.impl.ext.LruDiskCache
 * JD-Core Version:    0.6.0
 */
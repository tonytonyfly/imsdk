/*     */ package io.rong.imageloader.utils;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public final class IoUtils
/*     */ {
/*     */   public static final int DEFAULT_BUFFER_SIZE = 32768;
/*     */   public static final int DEFAULT_IMAGE_TOTAL_SIZE = 512000;
/*     */   public static final int CONTINUE_LOADING_PERCENTAGE = 75;
/*     */ 
/*     */   public static boolean copyStream(InputStream is, OutputStream os, CopyListener listener)
/*     */     throws IOException
/*     */   {
/*  52 */     return copyStream(is, os, listener, 32768);
/*     */   }
/*     */ 
/*     */   public static boolean copyStream(InputStream is, OutputStream os, CopyListener listener, int bufferSize)
/*     */     throws IOException
/*     */   {
/*  68 */     int current = 0;
/*  69 */     int total = is.available();
/*  70 */     if (total <= 0) {
/*  71 */       total = 512000;
/*     */     }
/*     */ 
/*  74 */     byte[] bytes = new byte[bufferSize];
/*     */ 
/*  76 */     if (shouldStopLoading(listener, current, total)) return false;
/*     */     int count;
/*  77 */     while ((count = is.read(bytes, 0, bufferSize)) != -1) {
/*  78 */       os.write(bytes, 0, count);
/*  79 */       current += count;
/*  80 */       if (shouldStopLoading(listener, current, total)) return false;
/*     */     }
/*  82 */     os.flush();
/*  83 */     return true;
/*     */   }
/*     */ 
/*     */   private static boolean shouldStopLoading(CopyListener listener, int current, int total) {
/*  87 */     if (listener != null) {
/*  88 */       boolean shouldContinue = listener.onBytesCopied(current, total);
/*  89 */       if ((!shouldContinue) && 
/*  90 */         (100 * current / total < 75)) {
/*  91 */         return true;
/*     */       }
/*     */     }
/*     */ 
/*  95 */     return false;
/*     */   }
/*     */ 
/*     */   public static void readAndCloseStream(InputStream is)
/*     */   {
/* 104 */     byte[] bytes = new byte[32768];
/*     */     try { while (is.read(bytes, 0, 32768) != -1);
/*     */     } catch (IOException ignored) {
/*     */     } finally {
/* 109 */       closeSilently(is);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void closeSilently(Closeable closeable) {
/* 114 */     if (closeable != null)
/*     */       try {
/* 116 */         closeable.close();
/*     */       }
/*     */       catch (Exception ignored)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   public static abstract interface CopyListener
/*     */   {
/*     */     public abstract boolean onBytesCopied(int paramInt1, int paramInt2);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.utils.IoUtils
 * JD-Core Version:    0.6.0
 */
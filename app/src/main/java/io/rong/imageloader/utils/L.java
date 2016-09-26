/*     */ package io.rong.imageloader.utils;
/*     */ 
/*     */ import android.util.Log;
/*     */ import io.rong.imageloader.core.ImageLoader;
/*     */ 
/*     */ public final class L
/*     */ {
/*     */   private static final String LOG_FORMAT = "%1$s\n%2$s";
/*  31 */   private static volatile boolean writeDebugLogs = false;
/*  32 */   private static volatile boolean writeLogs = true;
/*     */ 
/*     */   @Deprecated
/*     */   public static void enableLogging()
/*     */   {
/*  44 */     writeLogs(true);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void disableLogging()
/*     */   {
/*  54 */     writeLogs(false);
/*     */   }
/*     */ 
/*     */   public static void writeDebugLogs(boolean writeDebugLogs)
/*     */   {
/*  64 */     writeDebugLogs = writeDebugLogs;
/*     */   }
/*     */ 
/*     */   public static void writeLogs(boolean writeLogs)
/*     */   {
/*  69 */     writeLogs = writeLogs;
/*     */   }
/*     */ 
/*     */   public static void d(String message, Object[] args) {
/*  73 */     if (writeDebugLogs)
/*  74 */       log(3, null, message, args);
/*     */   }
/*     */ 
/*     */   public static void i(String message, Object[] args)
/*     */   {
/*  79 */     log(4, null, message, args);
/*     */   }
/*     */ 
/*     */   public static void w(String message, Object[] args) {
/*  83 */     log(5, null, message, args);
/*     */   }
/*     */ 
/*     */   public static void e(Throwable ex) {
/*  87 */     log(6, ex, null, new Object[0]);
/*     */   }
/*     */ 
/*     */   public static void e(String message, Object[] args) {
/*  91 */     log(6, null, message, args);
/*     */   }
/*     */ 
/*     */   public static void e(Throwable ex, String message, Object[] args) {
/*  95 */     log(6, ex, message, args);
/*     */   }
/*     */ 
/*     */   private static void log(int priority, Throwable ex, String message, Object[] args) {
/*  99 */     if (!writeLogs) return;
/* 100 */     if (args.length > 0)
/* 101 */       message = String.format(message, args);
/*     */     String log;
/*     */     String log;
/* 105 */     if (ex == null) {
/* 106 */       log = message;
/*     */     } else {
/* 108 */       String logMessage = message == null ? ex.getMessage() : message;
/* 109 */       String logBody = Log.getStackTraceString(ex);
/* 110 */       log = String.format("%1$s\n%2$s", new Object[] { logMessage, logBody });
/*     */     }
/* 112 */     Log.println(priority, ImageLoader.TAG, log);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.utils.L
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.imlib.ipc;
/*    */ 
/*    */ import android.content.Context;
/*    */ import io.rong.common.RLog;
/*    */ import java.io.File;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class RongExceptionHandler
/*    */   implements Thread.UncaughtExceptionHandler
/*    */ {
/*    */   Context mContext;
/*    */ 
/*    */   public RongExceptionHandler(Context context)
/*    */   {
/* 22 */     this.mContext = context;
/*    */   }
/*    */ 
/*    */   public void uncaughtException(Thread thread, Throwable ex)
/*    */   {
/* 28 */     File file = this.mContext.getExternalFilesDir("Crash");
/*    */ 
/* 30 */     if (!file.exists()) {
/* 31 */       file.mkdirs();
/*    */     }
/*    */ 
/* 34 */     File crashFile = new File(file, System.currentTimeMillis() + ".log");
/*    */     try
/*    */     {
/* 37 */       crashFile.createNewFile();
/* 38 */       OutputStream stream = new FileOutputStream(crashFile);
/* 39 */       PrintStream printStream = new PrintStream(stream);
/* 40 */       ex.printStackTrace(printStream);
/* 41 */       printStream.close();
/*    */     } catch (FileNotFoundException e) {
/* 43 */       e.printStackTrace();
/*    */     } catch (IOException e) {
/* 45 */       e.printStackTrace();
/*    */     } catch (SecurityException e) {
/* 47 */       e.printStackTrace();
/*    */     }
/*    */ 
/* 50 */     RLog.e("RongExceptionHandler", "uncaughtException", ex);
/*    */ 
/* 52 */     System.exit(2);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.ipc.RongExceptionHandler
 * JD-Core Version:    0.6.0
 */
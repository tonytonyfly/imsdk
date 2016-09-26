/*    */ package io.rong.photoview.log;
/*    */ 
/*    */ import android.util.Log;
/*    */ 
/*    */ public class LoggerDefault
/*    */   implements Logger
/*    */ {
/*    */   public int v(String tag, String msg)
/*    */   {
/* 29 */     return Log.v(tag, msg);
/*    */   }
/*    */ 
/*    */   public int v(String tag, String msg, Throwable tr)
/*    */   {
/* 34 */     return Log.v(tag, msg, tr);
/*    */   }
/*    */ 
/*    */   public int d(String tag, String msg)
/*    */   {
/* 39 */     return Log.d(tag, msg);
/*    */   }
/*    */ 
/*    */   public int d(String tag, String msg, Throwable tr)
/*    */   {
/* 44 */     return Log.d(tag, msg, tr);
/*    */   }
/*    */ 
/*    */   public int i(String tag, String msg)
/*    */   {
/* 49 */     return Log.i(tag, msg);
/*    */   }
/*    */ 
/*    */   public int i(String tag, String msg, Throwable tr)
/*    */   {
/* 54 */     return Log.i(tag, msg, tr);
/*    */   }
/*    */ 
/*    */   public int w(String tag, String msg)
/*    */   {
/* 59 */     return Log.w(tag, msg);
/*    */   }
/*    */ 
/*    */   public int w(String tag, String msg, Throwable tr)
/*    */   {
/* 64 */     return Log.w(tag, msg, tr);
/*    */   }
/*    */ 
/*    */   public int e(String tag, String msg)
/*    */   {
/* 69 */     return Log.e(tag, msg);
/*    */   }
/*    */ 
/*    */   public int e(String tag, String msg, Throwable tr)
/*    */   {
/* 74 */     return Log.e(tag, msg, tr);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.photoview.log.LoggerDefault
 * JD-Core Version:    0.6.0
 */
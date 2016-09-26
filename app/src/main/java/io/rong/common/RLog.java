/*    */ package io.rong.common;
/*    */ 
/*    */ import android.util.Log;
/*    */ 
/*    */ public class RLog
/*    */ {
/*    */   static final String TAG = "RongLog";
/*    */   public static final int VERBOSE = 2;
/*    */   public static final int DEBUG = 3;
/*    */   public static final int INFO = 4;
/*    */   public static final int WARN = 5;
/*    */   public static final int ERROR = 6;
/* 15 */   private static int level = 2;
/*    */ 
/*    */   public static void setLevel(int value) {
/* 18 */     level = value;
/*    */   }
/*    */ 
/*    */   public static int v(String tag, String msg)
/*    */   {
/* 29 */     if (level > 2) return 0;
/* 30 */     RFLog.write("RongLog", "[ " + tag + " ] " + msg, 2);
/* 31 */     return Log.v("RongLog", "[ " + tag + " ] " + msg);
/*    */   }
/*    */ 
/*    */   public static int d(String tag, String msg)
/*    */   {
/* 42 */     if (level > 3) return 0;
/* 43 */     RFLog.write("RongLog", "[ " + tag + " ] " + msg, 3);
/* 44 */     return Log.d("RongLog", "[ " + tag + " ] " + msg);
/*    */   }
/*    */ 
/*    */   public static int i(String tag, String msg)
/*    */   {
/* 55 */     if (level > 4) return 0;
/* 56 */     RFLog.write("RongLog", "[ " + tag + " ] " + msg, 4);
/* 57 */     return Log.i("RongLog", "[ " + tag + " ] " + msg);
/*    */   }
/*    */ 
/*    */   public static int w(String tag, String msg)
/*    */   {
/* 68 */     if (level > 5) return 0;
/* 69 */     RFLog.write("RongLog", "[ " + tag + " ] " + msg, 5);
/* 70 */     return Log.w("RongLog", "[ " + tag + " ] " + msg);
/*    */   }
/*    */ 
/*    */   public static int e(String tag, String msg)
/*    */   {
/* 81 */     if (level > 6) return 0;
/* 82 */     RFLog.write("RongLog", "[ " + tag + " ] " + msg, 6);
/* 83 */     return Log.e("RongLog", "[ " + tag + " ] " + msg);
/*    */   }
/*    */ 
/*    */   public static int e(String tag, String msg, Throwable tr)
/*    */   {
/* 95 */     if (level > 6) return 0;
/* 96 */     RFLog.write("RongLog", "[ " + tag + " ] " + msg, 6);
/* 97 */     return Log.e("RongLog", "[ " + tag + " ] " + msg, tr);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.common.RLog
 * JD-Core Version:    0.6.0
 */
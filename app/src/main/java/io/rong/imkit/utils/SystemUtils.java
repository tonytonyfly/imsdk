/*    */ package io.rong.imkit.utils;
/*    */ 
/*    */ import android.app.ActivityManager;
/*    */ import android.app.ActivityManager.RunningAppProcessInfo;
/*    */ import android.content.Context;
/*    */ import android.os.Process;
/*    */ 
/*    */ public class SystemUtils
/*    */ {
/*    */   public static String getCurProcessName(Context context)
/*    */   {
/* 11 */     int pid = Process.myPid();
/* 12 */     ActivityManager mActivityManager = (ActivityManager)context.getSystemService("activity");
/*    */ 
/* 14 */     for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses())
/*    */     {
/* 16 */       if (appProcess.pid == pid)
/*    */       {
/* 18 */         return appProcess.processName;
/*    */       }
/*    */     }
/* 21 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.utils.SystemUtils
 * JD-Core Version:    0.6.0
 */
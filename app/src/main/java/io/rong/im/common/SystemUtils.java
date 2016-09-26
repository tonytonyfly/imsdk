/*    */ package io.rong.common;
/*    */ 
/*    */ import android.app.ActivityManager;
/*    */ import android.app.ActivityManager.RunningAppProcessInfo;
/*    */ import android.app.ActivityManager.RunningTaskInfo;
/*    */ import android.content.ComponentName;
/*    */ import android.content.Context;
/*    */ import android.os.Process;
/*    */ import android.text.TextUtils;
/*    */ import java.util.List;
/*    */ 
/*    */ public class SystemUtils
/*    */ {
/*    */   public static boolean isAppRunning(Context context, String name)
/*    */   {
/* 14 */     if (TextUtils.isEmpty(name)) {
/* 15 */       return false;
/*    */     }
/* 17 */     ActivityManager am = (ActivityManager)context.getSystemService("activity");
/* 18 */     List infos = am.getRunningAppProcesses();
/* 19 */     if (infos == null)
/* 20 */       return false;
/* 21 */     for (ActivityManager.RunningAppProcessInfo info : infos) {
/* 22 */       if (info.processName.equals(name)) {
/* 23 */         return true;
/*    */       }
/*    */     }
/* 26 */     return false;
/*    */   }
/*    */ 
/*    */   public static String getCurrentProcessName(Context context) {
/* 30 */     int pid = Process.myPid();
/* 31 */     ActivityManager am = (ActivityManager)context.getSystemService("activity");
/* 32 */     List infos = am.getRunningAppProcesses();
/* 33 */     if (infos == null)
/* 34 */       return null;
/* 35 */     for (ActivityManager.RunningAppProcessInfo info : infos) {
/* 36 */       if (info.pid == pid) {
/* 37 */         return info.processName;
/*    */       }
/*    */     }
/* 40 */     return null;
/*    */   }
/*    */ 
/*    */   public static boolean isAppRunningOnTop(Context context, String name) {
/* 44 */     ActivityManager activityManager = (ActivityManager)context.getSystemService("activity");
/* 45 */     List runningTaskInfo = activityManager.getRunningTasks(1);
/* 46 */     if ((runningTaskInfo == null) || (runningTaskInfo.size() == 0)) {
/* 47 */       return false;
/*    */     }
/*    */ 
/* 50 */     String topAppPackageName = ((ActivityManager.RunningTaskInfo)runningTaskInfo.get(0)).topActivity.getPackageName();
/* 51 */     return (!TextUtils.isEmpty(name)) && (name.equals(topAppPackageName));
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.common.SystemUtils
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.common;
/*    */ 
/*    */ import android.app.AlarmManager;
/*    */ import android.app.PendingIntent;
/*    */ import android.content.Context;
/*    */ import android.content.Intent;
/*    */ import android.os.SystemClock;
/*    */ import io.rong.imlib.HeartbeatReceiver;
/*    */ 
/*    */ public class WakeLockUtils
/*    */ {
/*    */   private static final String TAG = "WakeLockUtils";
/*    */   private static final int HEARTBEAT_SPAN = 150000;
/*    */ 
/*    */   public static void startNextHeartbeat(Context context)
/*    */   {
/* 20 */     RLog.d("WakeLockUtils", "startNextHeartbeat " + context.getPackageName());
/*    */ 
/* 22 */     Intent heartbeatIntent = new Intent(context, HeartbeatReceiver.class);
/* 23 */     heartbeatIntent.setPackage(context.getPackageName());
/* 24 */     PendingIntent intent = PendingIntent.getBroadcast(context, 0, heartbeatIntent, 0);
/* 25 */     AlarmManager alarmManager = (AlarmManager)context.getSystemService("alarm");
/*    */ 
/* 27 */     long time = SystemClock.elapsedRealtime() + 150000L;
/*    */ 
/* 29 */     alarmManager.cancel(intent);
/*    */ 
/* 31 */     alarmManager.set(2, time, intent);
/*    */   }
/*    */ 
/*    */   public static void cancelHeartbeat(Context context) {
/* 35 */     RLog.d("WakeLockUtils", "cancelHeartbeat " + context.getPackageName());
/*    */ 
/* 37 */     Intent heartbeatIntent = new Intent(context, HeartbeatReceiver.class);
/* 38 */     heartbeatIntent.setPackage(context.getPackageName());
/* 39 */     PendingIntent intent = PendingIntent.getBroadcast(context, 0, heartbeatIntent, 0);
/* 40 */     AlarmManager alarmManager = (AlarmManager)context.getSystemService("alarm");
/*    */ 
/* 42 */     alarmManager.cancel(intent);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.common.WakeLockUtils
 * JD-Core Version:    0.6.0
 */
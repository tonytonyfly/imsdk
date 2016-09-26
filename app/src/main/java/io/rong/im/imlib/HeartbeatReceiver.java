/*    */ package io.rong.imlib;
/*    */ 
/*    */ import android.app.AlarmManager;
/*    */ import android.app.PendingIntent;
/*    */ import android.content.BroadcastReceiver;
/*    */ import android.content.Context;
/*    */ import android.content.Intent;
/*    */ import android.os.SystemClock;
/*    */ import io.rong.common.RLog;
/*    */ import io.rong.common.WakeLockUtils;
/*    */ 
/*    */ public class HeartbeatReceiver extends BroadcastReceiver
/*    */ {
/*    */   public void onReceive(Context context, Intent intent)
/*    */   {
/* 19 */     RLog.d("HeartbeatReceiver", "onReceive : " + intent);
/* 20 */     if (NativeClient.nativeObj == null)
/* 21 */       return;
/* 22 */     WakeLockUtils.startNextHeartbeat(context);
/* 23 */     NativeClient.nativeObj.ping();
/*    */   }
/*    */ 
/*    */   private void sendReconnect(Context context)
/*    */   {
/* 28 */     Intent intent = new Intent(context, ConnectChangeReceiver.class);
/* 29 */     intent.setAction("action_reconnect");
/* 30 */     intent.setPackage(context.getPackageName());
/*    */ 
/* 32 */     PendingIntent reconnectIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
/* 33 */     AlarmManager alarmManager = (AlarmManager)context.getSystemService("alarm");
/*    */ 
/* 35 */     long time = SystemClock.elapsedRealtime() + 1000L;
/*    */ 
/* 37 */     alarmManager.cancel(reconnectIntent);
/*    */ 
/* 39 */     alarmManager.set(2, time, reconnectIntent);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.HeartbeatReceiver
 * JD-Core Version:    0.6.0
 */
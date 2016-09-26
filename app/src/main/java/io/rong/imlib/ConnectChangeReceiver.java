/*    */ package io.rong.imlib;
/*    */ 
/*    */ import android.app.AlarmManager;
/*    */ import android.app.PendingIntent;
/*    */ import android.content.Context;
/*    */ import android.content.Intent;
/*    */ import android.net.ConnectivityManager;
/*    */ import android.net.NetworkInfo;
/*    */ import android.os.SystemClock;
/*    */ import io.rong.common.RLog;
/*    */ import io.rong.common.WakefulRongReceiver;
/*    */ 
/*    */ public class ConnectChangeReceiver extends WakefulRongReceiver
/*    */ {
/*    */   private static final String TAG = "ConnectChangeReceiver";
/* 19 */   static int sLastChannel = -1;
/*    */   public static final String RECONNECT_ACTION = "action_reconnect";
/*    */ 
/*    */   public static void setLastConnectNetworkChannel(int type)
/*    */   {
/* 24 */     sLastChannel = type;
/*    */   }
/*    */ 
/*    */   public void onReceive(Context context, Intent intent)
/*    */   {
/* 29 */     if ((intent == null) || (intent.getAction() == null)) {
/* 30 */       return;
/*    */     }
/* 32 */     ConnectivityManager cm = (ConnectivityManager)context.getSystemService("connectivity");
/*    */ 
/* 35 */     NetworkInfo networkInfo = null;
/*    */     try
/*    */     {
/* 38 */       networkInfo = cm.getActiveNetworkInfo();
/* 39 */       RLog.d("ConnectChangeReceiver", new StringBuilder().append("onReceive.network:").append(networkInfo != null ? Boolean.valueOf(networkInfo.isAvailable()) : "null").append(", intent:").append(intent.toString()).toString());
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 43 */       e.printStackTrace();
/*    */     }
/*    */ 
/* 46 */     if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
/* 47 */       if ((networkInfo != null) && (networkInfo.isAvailable()))
/* 48 */         sendReconnect(context);
/*    */     }
/* 50 */     else if (intent.getAction().equals("action_reconnect")) {
/* 51 */       if ((networkInfo != null) && (networkInfo.isAvailable()))
/* 52 */         startWakefulService(context, new Intent(context, ReConnectService.class));
/*    */     }
/* 54 */     else if (intent.getAction().equals("android.intent.action.USER_PRESENT")) {
/* 55 */       RongIMClient.ConnectionStatusListener.ConnectionStatus state = RongIMClient.getInstance().getCurrentConnectionStatus();
/* 56 */       RLog.d("ConnectChangeReceiver", new StringBuilder().append("ACTION_USER_PRESENT state = ").append(state).toString());
/* 57 */       if ((state.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE)) && (networkInfo != null) && (networkInfo.isAvailable()))
/*    */       {
/* 60 */         sendReconnect(context);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   private void sendReconnect(Context context)
/*    */   {
/* 67 */     Intent intent = new Intent(context, ConnectChangeReceiver.class);
/* 68 */     intent.setAction("action_reconnect");
/* 69 */     intent.setPackage(context.getPackageName());
/*    */ 
/* 71 */     PendingIntent reconnectIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
/* 72 */     AlarmManager alarmManager = (AlarmManager)context.getSystemService("alarm");
/*    */ 
/* 74 */     long time = SystemClock.elapsedRealtime();
/*    */ 
/* 76 */     alarmManager.cancel(reconnectIntent);
/*    */ 
/* 78 */     alarmManager.set(2, time, reconnectIntent);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.ConnectChangeReceiver
 * JD-Core Version:    0.6.0
 */
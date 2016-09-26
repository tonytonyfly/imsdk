/*    */ package io.rong.push;
/*    */ 
/*    */ import android.content.BroadcastReceiver;
/*    */ import android.content.Context;
/*    */ import android.content.Intent;
/*    */ import android.content.SharedPreferences;
/*    */ import io.rong.push.common.RLog;
/*    */ 
/*    */ public class PushReceiver extends BroadcastReceiver
/*    */ {
/*    */   private static final String TAG = "PushReceiver";
/*    */ 
/*    */   public void onReceive(Context context, Intent intent)
/*    */   {
/* 20 */     RLog.d("PushReceiver", "onReceive intent = " + intent);
/*    */ 
/* 22 */     if ((intent == null) || (intent.getAction() == null)) {
/* 23 */       RLog.e("PushReceiver", "intent or action is null.");
/* 24 */       return;
/*    */     }
/*    */ 
/* 28 */     SharedPreferences sp = context.getSharedPreferences("RongPush", 0);
/* 29 */     String pushTypeUsing = sp.getString("pushTypeUsing", "");
/* 30 */     if ((pushTypeUsing.equals("GCM")) || (pushTypeUsing.equals("MI")) || (pushTypeUsing.equals("HW"))) {
/* 31 */       RLog.d("PushReceiver", pushTypeUsing + " is 3rd push type, doesn't handle");
/* 32 */       return;
/*    */     }
/*    */     try
/*    */     {
/* 36 */       Intent newIntent = new Intent(context, PushService.class);
/* 37 */       newIntent.setAction(intent.getAction());
/* 38 */       newIntent.putExtra("PING", intent.getStringExtra("PING"));
/* 39 */       context.startService(newIntent);
/*    */     } catch (SecurityException e) {
/* 41 */       RLog.e("PushReceiver", "SecurityException. Failed to start PushService.");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.PushReceiver
 * JD-Core Version:    0.6.0
 */
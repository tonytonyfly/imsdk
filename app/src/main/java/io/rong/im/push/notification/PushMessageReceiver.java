/*    */ package io.rong.push.notification;
/*    */ 
/*    */ import android.content.BroadcastReceiver;
/*    */ import android.content.Context;
/*    */ import android.content.Intent;
/*    */ import io.rong.push.common.RLog;
/*    */ import io.rong.push.core.MessageHandleService;
/*    */ import io.rong.push.core.MessageHandleService.Job;
/*    */ 
/*    */ public abstract class PushMessageReceiver extends BroadcastReceiver
/*    */ {
/*    */   private static final String TAG = "PushMessageReceiver";
/*    */ 
/*    */   public final void onReceive(Context context, Intent intent)
/*    */   {
/* 21 */     MessageHandleService.addJob(new MessageHandleService.Job(intent, this));
/* 22 */     Intent newIntent = new Intent(context, MessageHandleService.class);
/* 23 */     RLog.d("PushMessageReceiver", "onReceive.action:" + intent.getAction());
/*    */     try {
/* 25 */       context.startService(newIntent);
/*    */     } catch (Exception e) {
/* 27 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public abstract boolean onNotificationMessageArrived(Context paramContext, PushNotificationMessage paramPushNotificationMessage);
/*    */ 
/*    */   public abstract boolean onNotificationMessageClicked(Context paramContext, PushNotificationMessage paramPushNotificationMessage);
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.notification.PushMessageReceiver
 * JD-Core Version:    0.6.0
 */
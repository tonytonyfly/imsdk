/*    */ package io.rong.imlib;
/*    */ 
/*    */ import android.app.IntentService;
/*    */ import android.content.Intent;
/*    */ import io.rong.common.RLog;
/*    */ 
/*    */ public class ReConnectService extends IntentService
/*    */ {
/*    */   private static final String TAG = "ReConnectService";
/*    */ 
/*    */   public ReConnectService()
/*    */   {
/* 17 */     super("RONG_ReConnect");
/*    */   }
/*    */ 
/*    */   protected void onHandleIntent(Intent intent)
/*    */   {
/* 23 */     if (intent != null)
/* 24 */       if (RongIMClient.getInstance() == null) {
/* 25 */         ConnectChangeReceiver.completeWakefulIntent(intent);
/*    */       } else {
/* 27 */         RLog.d("ReConnectService", "RECONNECT " + intent.toString());
/* 28 */         RongIMClient.getInstance().reconnect(new RongIMClient.ConnectCallback(intent)
/*    */         {
/*    */           public void onSuccess(String s) {
/* 31 */             ConnectChangeReceiver.completeWakefulIntent(this.val$intent);
/*    */           }
/*    */ 
/*    */           public void onError(RongIMClient.ErrorCode e)
/*    */           {
/* 36 */             ConnectChangeReceiver.completeWakefulIntent(this.val$intent);
/*    */           }
/*    */ 
/*    */           public void onTokenIncorrect()
/*    */           {
/* 41 */             ConnectChangeReceiver.completeWakefulIntent(this.val$intent);
/*    */           }
/*    */         });
/*    */       }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.ReConnectService
 * JD-Core Version:    0.6.0
 */
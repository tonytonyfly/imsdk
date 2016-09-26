/*    */ package io.rong.push.platform;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.content.Intent;
/*    */ import android.content.SharedPreferences;
/*    */ import android.content.SharedPreferences.Editor;
/*    */ import com.xiaomi.mipush.sdk.MiPushCommandMessage;
/*    */ import com.xiaomi.mipush.sdk.MiPushMessage;
/*    */ import com.xiaomi.mipush.sdk.PushMessageReceiver;
/*    */ import io.rong.push.PushService;
/*    */ import io.rong.push.common.RLog;
/*    */ import java.util.List;
/*    */ 
/*    */ public class MiMessageReceiver extends PushMessageReceiver
/*    */ {
/*    */   private static final String TAG = "MiMessageReceiver";
/*    */   private String mRegId;
/*    */ 
/*    */   public void onReceivePassThroughMessage(Context context, MiPushMessage message)
/*    */   {
/* 26 */     RLog.v("MiMessageReceiver", "onReceivePassThroughMessage is called. " + message.toString());
/*    */   }
/*    */ 
/*    */   public void onNotificationMessageClicked(Context context, MiPushMessage message)
/*    */   {
/* 32 */     RLog.v("MiMessageReceiver", "onNotificationMessageClicked is called. " + message.toString());
/*    */ 
/* 34 */     Intent intent = new Intent();
/* 35 */     intent.setAction("io.rong.push.intent.MI_MESSAGE_CLICKED");
/* 36 */     intent.setPackage(context.getPackageName());
/* 37 */     intent.putExtra("message", message);
/* 38 */     context.sendBroadcast(intent);
/*    */   }
/*    */ 
/*    */   public void onNotificationMessageArrived(Context context, MiPushMessage message)
/*    */   {
/* 43 */     RLog.v("MiMessageReceiver", "onNotificationMessageArrived is called. " + message.toString());
/*    */ 
/* 45 */     Intent intent = new Intent();
/* 46 */     intent.setAction("io.rong.push.intent.MI_MESSAGE_ARRIVED");
/* 47 */     intent.setPackage(context.getPackageName());
/* 48 */     intent.putExtra("message", message);
/* 49 */     context.sendBroadcast(intent);
/*    */   }
/*    */ 
/*    */   public void onCommandResult(Context context, MiPushCommandMessage message)
/*    */   {
/* 54 */     RLog.v("MiMessageReceiver", "onCommandResult is called. " + message.toString());
/* 55 */     String command = message.getCommand();
/* 56 */     List arguments = message.getCommandArguments();
/* 57 */     String cmdArg1 = (arguments != null) && (arguments.size() > 0) ? (String)arguments.get(0) : null;
/*    */ 
/* 59 */     if ("register".equals(command))
/* 60 */       if (message.getResultCode() == 0L) {
/* 61 */         this.mRegId = cmdArg1;
/* 62 */         SharedPreferences sp = context.getSharedPreferences("RongPushAppConfig", 0);
/* 63 */         SharedPreferences.Editor editor = sp.edit();
/*    */ 
/* 65 */         if (sp.getString("pushType", "").equals("MI")) {
/* 66 */           String token = sp.getString("token", "");
/* 67 */           if (token.equals(this.mRegId)) {
/* 68 */             return;
/*    */           }
/* 70 */           editor.putString("token", this.mRegId);
/*    */         }
/*    */         else {
/* 73 */           RLog.d("MiMessageReceiver", "write to cache.");
/* 74 */           editor.putString("pushType", "MI");
/* 75 */           editor.putString("token", this.mRegId);
/*    */         }
/* 77 */         editor.apply();
/*    */ 
/* 79 */         RLog.e("MiMessageReceiver", "send to pushService.");
/*    */         try {
/* 81 */           Intent intent = new Intent(context, PushService.class);
/* 82 */           intent.setAction("io.rong.push.intent.action.REGISTRATION_INFO");
/* 83 */           String regInfo = "MI|" + this.mRegId;
/* 84 */           intent.putExtra("regInfo", regInfo);
/* 85 */           context.startService(intent);
/*    */         } catch (SecurityException e) {
/* 87 */           RLog.e("MiMessageReceiver", "SecurityException. Failed to send token to PushService.");
/*    */         }
/*    */       } else {
/* 90 */         RLog.e("MiMessageReceiver", "Failed to get register id from MI." + message.getResultCode());
/*    */       }
/*    */   }
/*    */ 
/*    */   public void onReceiveRegisterResult(Context context, MiPushCommandMessage message)
/*    */   {
/* 97 */     RLog.v("MiMessageReceiver", "onReceiveRegisterResult is called. " + message.toString());
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.platform.MiMessageReceiver
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.push.platform;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.content.Intent;
/*    */ import android.content.SharedPreferences;
/*    */ import android.content.SharedPreferences.Editor;
/*    */ import android.os.Bundle;
/*    */ import com.huawei.android.pushagent.api.PushEventReceiver;
/*    */ import io.rong.push.PushService;
/*    */ import io.rong.push.common.RLog;
/*    */ import io.rong.push.core.PushUtils;
/*    */ import org.json.JSONException;
/*    */ 
/*    */ public class HWReceiver extends PushEventReceiver
/*    */ {
/* 19 */   private final String TAG = "HWReceiver";
/*    */ 
/*    */   public void onToken(Context context, String token, Bundle extras)
/*    */   {
/* 23 */     RLog.d("HWReceiver", "获取token成功， token = " + token);
/* 24 */     String belongId = extras.getString("belongId");
/* 25 */     String content = "获取token和belongId成功，token = " + token + ",belongId = " + belongId;
/*    */ 
/* 27 */     SharedPreferences sp = context.getSharedPreferences("RongPushAppConfig", 0);
/* 28 */     SharedPreferences.Editor editor = sp.edit();
/*    */ 
/* 30 */     if (sp.getString("pushType", "").equals("HW")) {
/* 31 */       String cachedToken = sp.getString("token", "");
/* 32 */       if (cachedToken.equals(token)) {
/* 33 */         return;
/*    */       }
/* 35 */       editor.putString("token", token);
/*    */ 
/* 37 */       editor.apply();
/*    */ 
/* 39 */       RLog.e("HWReceiver", "send to pushService.");
/*    */       try {
/* 41 */         Intent intent = new Intent(context, PushService.class);
/* 42 */         intent.setAction("io.rong.push.intent.action.REGISTRATION_INFO");
/* 43 */         String regInfo = "HW|" + token;
/* 44 */         intent.putExtra("regInfo", regInfo);
/* 45 */         context.startService(intent);
/*    */       } catch (SecurityException e) {
/* 47 */         RLog.e("HWReceiver", "SecurityException. Failed to send token to PushService.");
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean onPushMsg(Context context, byte[] msg, Bundle bundle)
/*    */   {
/* 55 */     String content = "";
/* 56 */     Bundle newBundle = null;
/* 57 */     RLog.d("HWReceiver", "onPushMsg");
/*    */     try {
/* 59 */       content = new String(msg, "UTF-8");
/*    */     } catch (Exception e) {
/* 61 */       e.printStackTrace();
/*    */     }
/* 63 */     RLog.d("HWReceiver", "onPushMsg.message content:" + content);
/* 64 */     Intent intent = new Intent();
/* 65 */     intent.setAction("io.rong.push.intent.MESSAGE_ARRIVED");
/*    */     try {
/* 67 */       newBundle = PushUtils.decode(content);
/*    */     } catch (JSONException e) {
/* 69 */       return false;
/*    */     }
/* 71 */     intent.setPackage(context.getPackageName());
/* 72 */     intent.putExtras(newBundle);
/* 73 */     context.sendBroadcast(intent);
/* 74 */     return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.platform.HWReceiver
 * JD-Core Version:    0.6.0
 */
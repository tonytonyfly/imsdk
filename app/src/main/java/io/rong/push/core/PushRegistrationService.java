/*    */ package io.rong.push.core;
/*    */ 
/*    */ import android.app.IntentService;
/*    */ import android.content.Intent;
/*    */ import android.content.SharedPreferences;
/*    */ import android.content.SharedPreferences.Editor;
/*    */ import android.content.res.Resources;
/*    */ import android.text.TextUtils;
/*    */ import com.google.android.gms.iid.InstanceID;
/*    */ import com.huawei.android.pushagent.PushManager;
/*    */ import com.xiaomi.mipush.sdk.MiPushClient;
/*    */ import io.rong.push.PushService;
/*    */ import io.rong.push.common.RLog;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class PushRegistrationService extends IntentService
/*    */ {
/* 20 */   private final String TAG = "PushRegistrationService";
/*    */ 
/*    */   public PushRegistrationService() {
/* 23 */     super("PushRegistration");
/*    */   }
/*    */ 
/*    */   protected void onHandleIntent(Intent intent)
/*    */   {
/* 28 */     String pushType = "";
/* 29 */     SharedPreferences sp = getSharedPreferences("RongPushAppConfig", 0);
/* 30 */     SharedPreferences.Editor editor = sp.edit();
/* 31 */     RLog.d("PushRegistrationService", "intent is:" + intent);
/* 32 */     if (intent != null) {
/* 33 */       pushType = intent.getStringExtra("pushType");
/* 34 */       if (pushType == null) {
/* 35 */         return;
/*    */       }
/*    */     }
/* 38 */     RLog.d("PushRegistrationService", "pushType is:" + pushType);
/*    */ 
/* 40 */     if (pushType.equals("GCM")) {
/* 41 */       InstanceID instanceID = InstanceID.getInstance(this);
/* 42 */       RLog.i("PushRegistrationService", "before GCM Registration.SendId:" + getResources().getString(getResources().getIdentifier("gcm_defaultSenderId", "string", getPackageName())));
/*    */       try {
/* 44 */         String token = instanceID.getToken(getResources().getString(getResources().getIdentifier("gcm_defaultSenderId", "string", getPackageName())), "GCM", null);
/*    */ 
/* 48 */         if (!TextUtils.isEmpty(token)) {
/* 49 */           editor.putString("token", token);
/* 50 */           Intent newIntent = new Intent(this, PushService.class);
/* 51 */           newIntent.setAction("io.rong.push.intent.action.REGISTRATION_INFO");
/* 52 */           String info = "GCM|" + token;
/* 53 */           newIntent.putExtra("regInfo", info);
/* 54 */           startService(newIntent);
/*    */         }
/*    */       } catch (IOException e) {
/* 57 */         RLog.e("PushRegistrationService", "Failed to get token from google service." + e);
/* 58 */         editor.putString("token", "");
/*    */       }
/* 60 */       editor.putString("pushType", "GCM");
/* 61 */       editor.apply();
/* 62 */     } else if (pushType.equals("MI")) {
/* 63 */       String appKey = sp.getString("MiAppKey", "");
/* 64 */       String appId = sp.getString("MiAppId", "");
/* 65 */       RLog.d("PushRegistrationService", "MiAppKey:" + appKey + ",MiAppId:" + appId);
/* 66 */       MiPushClient.registerPush(this, appId, appKey);
/* 67 */       editor.putString("pushType", "MI");
/* 68 */       editor.apply();
/* 69 */     } else if (pushType.equals("HW")) {
/* 70 */       PushManager.requestToken(this);
/* 71 */       editor.putString("pushType", "HW");
/* 72 */       editor.apply();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.core.PushRegistrationService
 * JD-Core Version:    0.6.0
 */
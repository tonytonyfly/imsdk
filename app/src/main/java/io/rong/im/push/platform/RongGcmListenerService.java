/*    */ package io.rong.push.platform;
/*    */ 
/*    */ import android.content.Intent;
/*    */ import android.os.Bundle;
/*    */ import com.google.android.gms.gcm.GcmListenerService;
/*    */ import io.rong.push.common.RLog;
/*    */ import io.rong.push.core.PushUtils;
/*    */ import org.json.JSONException;
/*    */ 
/*    */ public class RongGcmListenerService extends GcmListenerService
/*    */ {
/*    */   private static final String TAG = "RongGcmListenerService";
/*    */ 
/*    */   public void onMessageReceived(String from, Bundle data)
/*    */   {
/* 28 */     RLog.d("RongGcmListenerService", "onMessageReceived");
/* 29 */     if (data == null)
/* 30 */       return;
/* 31 */     Bundle bundle = null;
/* 32 */     String message = data.getString("message");
/* 33 */     Intent intent = new Intent();
/* 34 */     intent.setAction("io.rong.push.intent.MESSAGE_ARRIVED");
/* 35 */     intent.setPackage(getPackageName());
/*    */     try
/*    */     {
/* 38 */       bundle = PushUtils.decode(message);
/*    */     } catch (JSONException e) {
/* 40 */       return;
/*    */     }
/* 42 */     intent.putExtras(bundle);
/* 43 */     sendBroadcast(intent);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.platform.RongGcmListenerService
 * JD-Core Version:    0.6.0
 */
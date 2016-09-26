/*    */ package io.rong.push.platform;
/*    */ 
/*    */ import android.content.Intent;
/*    */ import com.google.android.gms.iid.InstanceIDListenerService;
/*    */ import io.rong.push.core.PushRegistrationService;
/*    */ 
/*    */ public class RongGCMInstanceIDListenerService extends InstanceIDListenerService
/*    */ {
/*    */   public void onTokenRefresh()
/*    */   {
/* 14 */     Intent intent = new Intent(this, PushRegistrationService.class);
/* 15 */     startService(intent);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.platform.RongGCMInstanceIDListenerService
 * JD-Core Version:    0.6.0
 */
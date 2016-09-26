/*    */ package io.rong.imlib.statistics;
/*    */ 
/*    */ import android.app.Activity;
/*    */ import android.content.Context;
/*    */ import android.util.Log;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ public class MessagingAdapter
/*    */ {
/*    */   private static final String TAG = "MessagingAdapter";
/*    */   private static final String MESSAGING_CLASS_NAME = "ly.count.android.sdk.messaging.CountlyMessaging";
/*    */ 
/*    */   public static boolean isMessagingAvailable()
/*    */   {
/* 14 */     boolean messagingAvailable = false;
/*    */     try {
/* 16 */       Class.forName("ly.count.android.sdk.messaging.CountlyMessaging");
/* 17 */       messagingAvailable = true;
/*    */     } catch (ClassNotFoundException ignored) {
/*    */     }
/* 20 */     return messagingAvailable;
/*    */   }
/*    */ 
/*    */   public static boolean init(Activity activity, Class<? extends Activity> activityClass, String sender, String[] buttonNames) {
/*    */     try {
/* 25 */       Class cls = Class.forName("ly.count.android.sdk.messaging.CountlyMessaging");
/* 26 */       Method method = cls.getMethod("init", new Class[] { Activity.class, Class.class, String.class, [Ljava.lang.String.class });
/* 27 */       method.invoke(null, new Object[] { activity, activityClass, sender, buttonNames });
/* 28 */       return true;
/*    */     }
/*    */     catch (Throwable logged) {
/* 31 */       Log.e("MessagingAdapter", "Couldn't init Statistics Messaging", logged);
/* 32 */     }return false;
/*    */   }
/*    */ 
/*    */   public static boolean storeConfiguration(Context context, String serverURL, String appKey, String deviceID, DeviceId.Type idMode)
/*    */   {
/*    */     try {
/* 38 */       Class cls = Class.forName("ly.count.android.sdk.messaging.CountlyMessaging");
/* 39 */       Method method = cls.getMethod("storeConfiguration", new Class[] { Context.class, String.class, String.class, String.class, DeviceId.Type.class });
/* 40 */       method.invoke(null, new Object[] { context, serverURL, appKey, deviceID, idMode });
/* 41 */       return true;
/*    */     }
/*    */     catch (Throwable logged) {
/* 44 */       Log.e("MessagingAdapter", "Couldn't store configuration in Statistics Messaging", logged);
/* 45 */     }return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.statistics.MessagingAdapter
 * JD-Core Version:    0.6.0
 */
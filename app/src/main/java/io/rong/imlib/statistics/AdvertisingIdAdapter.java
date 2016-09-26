/*    */ package io.rong.imlib.statistics;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.os.Handler;
/*    */ import android.util.Log;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ public class AdvertisingIdAdapter
/*    */ {
/*    */   private static final String TAG = "AdvertisingIdAdapter";
/*    */   private static final String ADVERTISING_ID_CLIENT_CLASS_NAME = "com.google.android.gms.ads.identifier.AdvertisingIdClient";
/*    */   private static Handler handler;
/*    */ 
/*    */   public static boolean isAdvertisingIdAvailable()
/*    */   {
/* 15 */     boolean advertisingIdAvailable = false;
/*    */     try {
/* 17 */       Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient");
/* 18 */       advertisingIdAvailable = true;
/*    */     } catch (ClassNotFoundException ignored) {
/*    */     }
/* 21 */     return advertisingIdAvailable;
/*    */   }
/*    */ 
/*    */   public static void setAdvertisingId(Context context, StatisticsStore store, DeviceId deviceId) {
/* 25 */     new Thread(new Runnable(deviceId, context, store)
/*    */     {
/*    */       public void run() {
/*    */         try {
/* 29 */           this.val$deviceId.setId(DeviceId.Type.ADVERTISING_ID, AdvertisingIdAdapter.access$000(this.val$context));
/*    */         } catch (Throwable t) {
/* 31 */           if ((t.getCause() != null) && (t.getCause().getClass().toString().contains("GooglePlayServicesAvailabilityException")))
/*    */           {
/* 34 */             if (Statistics.sharedInstance().isLoggingEnabled())
/* 35 */               Log.i("AdvertisingIdAdapter", "Advertising ID cannot be determined yet");
/*    */           }
/* 37 */           else if ((t.getCause() != null) && (t.getCause().getClass().toString().contains("GooglePlayServicesNotAvailableException")))
/*    */           {
/* 39 */             if (Statistics.sharedInstance().isLoggingEnabled()) {
/* 40 */               Log.w("AdvertisingIdAdapter", "Advertising ID cannot be determined because Play Services are not available");
/*    */             }
/* 42 */             this.val$deviceId.switchToIdType(DeviceId.Type.OPEN_UDID, this.val$context, this.val$store);
/*    */           }
/*    */           else {
/* 45 */             Log.e("AdvertisingIdAdapter", "Couldn't get advertising ID", t);
/*    */           }
/*    */         }
/*    */       }
/*    */     }).start();
/*    */   }
/*    */ 
/*    */   private static String getAdvertisingId(Context context)
/*    */     throws Throwable
/*    */   {
/* 53 */     Class cls = Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient");
/* 54 */     Method getAdvertisingIdInfo = cls.getMethod("getAdvertisingIdInfo", new Class[] { Context.class });
/* 55 */     Object info = getAdvertisingIdInfo.invoke(null, new Object[] { context });
/* 56 */     if (info != null) {
/* 57 */       Method getId = info.getClass().getMethod("getId", new Class[0]);
/* 58 */       Object id = getId.invoke(info, new Object[0]);
/* 59 */       return (String)id;
/*    */     }
/* 61 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.statistics.AdvertisingIdAdapter
 * JD-Core Version:    0.6.0
 */
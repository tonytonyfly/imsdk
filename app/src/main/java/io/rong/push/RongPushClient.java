/*     */ package io.rong.push;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.Intent;
/*     */ import android.content.SharedPreferences;
/*     */ import android.content.SharedPreferences.Editor;
/*     */ import android.content.pm.ActivityInfo;
/*     */ import android.content.pm.PackageInfo;
/*     */ import android.content.pm.PackageManager;
/*     */ import android.content.pm.PackageManager.NameNotFoundException;
/*     */ import android.content.pm.ResolveInfo;
/*     */ import android.content.pm.ServiceInfo;
/*     */ import android.text.TextUtils;
/*     */ import com.google.android.gms.common.GoogleApiAvailability;
/*     */ import com.google.android.gms.gcm.GcmReceiver;
/*     */ import com.huawei.android.pushagent.PushBootReceiver;
/*     */ import com.huawei.android.pushagent.PushEventReceiver;
/*     */ import com.xiaomi.mipush.sdk.MessageHandleService;
/*     */ import com.xiaomi.mipush.sdk.MiPushClient;
/*     */ import com.xiaomi.mipush.sdk.PushMessageHandler;
/*     */ import com.xiaomi.push.service.receivers.NetworkStatusReceiver;
/*     */ import com.xiaomi.push.service.receivers.PingReceiver;
/*     */ import io.rong.imlib.common.DeviceUtils;
/*     */ import io.rong.imlib.statistics.Statistics;
/*     */ import io.rong.push.common.RLog;
/*     */ import io.rong.push.common.RongException;
/*     */ import io.rong.push.core.PushRegistrationService;
/*     */ import io.rong.push.notification.PushMessageReceiver;
/*     */ import io.rong.push.notification.PushNotificationMessage;
/*     */ import io.rong.push.notification.RongNotificationInterface;
/*     */ import io.rong.push.platform.HWReceiver;
/*     */ import io.rong.push.platform.MiMessageReceiver;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class RongPushClient
/*     */ {
/*     */   private static final String TAG = "RongPushClient";
/*  45 */   private static final ArrayList<String> registeredType = new ArrayList();
/*     */ 
/*     */   public static void registerGCM(Context context)
/*     */     throws RongException
/*     */   {
/*  53 */     SharedPreferences sp = context.getSharedPreferences("RongPushAppConfig", 0);
/*     */     try {
/*  55 */       checkPlayServices(context);
/*  56 */       sp.edit().putBoolean("isGCMEnabled", true).commit();
/*  57 */       registeredType.add("GCM");
/*     */     } catch (Exception e) {
/*  59 */       throw new RongException("Failed registerGCM.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void registerMiPush(Context context, String miAppId, String miAppKey)
/*     */   {
/*  71 */     SharedPreferences sp = context.getSharedPreferences("RongPushAppConfig", 0);
/*  72 */     SharedPreferences.Editor editor = sp.edit();
/*  73 */     if ((TextUtils.isEmpty(miAppKey)) || (TextUtils.isEmpty(miAppId))) {
/*  74 */       throw new IllegalArgumentException("Failed registerMiPush. appKey or appId can't be empty.");
/*     */     }
/*  76 */     registeredType.add("MI");
/*  77 */     editor.putBoolean("isMiEnabled", true);
/*  78 */     editor.putString("MiAppId", miAppId);
/*  79 */     editor.putString("MiAppKey", miAppKey);
/*  80 */     editor.commit();
/*     */   }
/*     */ 
/*     */   public static void registerHWPush(Context context)
/*     */   {
/*  89 */     SharedPreferences sp = context.getSharedPreferences("RongPushAppConfig", 0);
/*  90 */     SharedPreferences.Editor editor = sp.edit();
/*  91 */     registeredType.add("HW");
/*  92 */     editor.putBoolean("isHWEnabled", true);
/*  93 */     editor.commit();
/*     */   }
/*     */ 
/*     */   public static void init(Context context, String appKey)
/*     */   {
/* 103 */     if (TextUtils.isEmpty(appKey)) {
/* 104 */       throw new ExceptionInInitializerError("appKey can't be empty!");
/*     */     }
/*     */ 
/* 107 */     Boolean isConfigChanged = Boolean.valueOf(isConfigChanged(context));
/*     */ 
/* 109 */     SharedPreferences sp = context.getSharedPreferences("RongPushAppConfig", 0);
/* 110 */     String pushTypeUsing = sp.getString("pushType", "");
/* 111 */     RLog.d("RongPushClient", "init. the push type is:" + pushTypeUsing);
/*     */     try {
/* 113 */       if ((TextUtils.isEmpty(pushTypeUsing)) || (pushTypeUsing.equals("RONG")) || (isConfigChanged.booleanValue())) {
/* 114 */         RLog.d("RongPushClient", "send to pushService.");
/* 115 */         String enabledPushTypes = getSupportedPushTypes();
/* 116 */         Intent intent = new Intent(context, PushService.class);
/* 117 */         intent.setAction("io.rong.push.intent.action.INIT");
/* 118 */         intent.putExtra("deviceId", DeviceUtils.getDeviceId(context, appKey));
/* 119 */         intent.putExtra("appKey", appKey);
/* 120 */         intent.putExtra("enabledPushTypes", enabledPushTypes);
/* 121 */         context.startService(intent);
/*     */       } else {
/* 123 */         RLog.e("RongPushClient", "send to PushRegistrationService.");
/* 124 */         Intent intent = new Intent(context, PushRegistrationService.class);
/* 125 */         intent.putExtra("pushType", pushTypeUsing);
/* 126 */         context.startService(intent);
/*     */       }
/*     */     } catch (SecurityException e) {
/* 129 */       RLog.e("RongPushClient", "SecurityException. Failed to start pushService.");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void init(Context context, String appKey, String pushDomain)
/*     */   {
/* 142 */     if ((TextUtils.isEmpty(appKey)) || (TextUtils.isEmpty(pushDomain))) {
/* 143 */       throw new ExceptionInInitializerError("appKey or pushDomain can't be empty!");
/*     */     }
/*     */ 
/* 146 */     Boolean isConfigChanged = Boolean.valueOf(isConfigChanged(context));
/*     */ 
/* 148 */     SharedPreferences sp = context.getSharedPreferences("RongPushAppConfig", 0);
/* 149 */     String pushTypeUsing = sp.getString("pushType", "");
/* 150 */     RLog.d("RongPushClient", "init with domain. the push type is:" + pushTypeUsing);
/*     */     try {
/* 152 */       if ((TextUtils.isEmpty(pushTypeUsing)) || (pushTypeUsing.equals("RONG")) || (isConfigChanged.booleanValue())) {
/* 153 */         RLog.d("RongPushClient", "send to pushService.");
/* 154 */         String enabledPushTypes = getSupportedPushTypes();
/* 155 */         Intent intent = new Intent(context, PushService.class);
/* 156 */         intent.setAction("io.rong.push.intent.action.INIT");
/* 157 */         intent.putExtra("deviceId", DeviceUtils.getDeviceId(context, appKey));
/* 158 */         intent.putExtra("appKey", appKey);
/* 159 */         intent.putExtra("enabledPushTypes", enabledPushTypes);
/* 160 */         intent.putExtra("pushDomain", pushDomain);
/* 161 */         context.startService(intent);
/*     */       } else {
/* 163 */         RLog.e("RongPushClient", "send to PushRegistrationService.");
/* 164 */         Intent intent = new Intent(context, PushRegistrationService.class);
/* 165 */         intent.putExtra("pushType", pushTypeUsing);
/* 166 */         context.startService(intent);
/*     */       }
/*     */     } catch (SecurityException e) {
/* 169 */       RLog.e("RongPushClient", "SecurityException. Failed to start pushService.");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void clearAllNotifications(Context context)
/*     */   {
/* 179 */     RLog.i("RongPushClient", "clearAllNotifications");
/* 180 */     SharedPreferences sp = context.getSharedPreferences("RongPushAppConfig", 0);
/* 181 */     String pushTypeUsing = sp.getString("pushType", "");
/* 182 */     if (pushTypeUsing.equals("MI")) {
/* 183 */       MiPushClient.clearNotification(context);
/*     */     }
/* 185 */     RongNotificationInterface.removeAllNotification(context);
/*     */   }
/*     */ 
/*     */   public static void clearAllPushNotifications(Context context)
/*     */   {
/* 196 */     RLog.i("RongPushClient", "clearAllPushNotifications");
/* 197 */     SharedPreferences sp = context.getSharedPreferences("RongPushAppConfig", 0);
/* 198 */     String pushTypeUsing = sp.getString("pushType", "");
/* 199 */     if (pushTypeUsing.equals("MI")) {
/* 200 */       MiPushClient.clearNotification(context);
/*     */     }
/* 202 */     RongNotificationInterface.removeAllPushNotification(context);
/*     */   }
/*     */ 
/*     */   public static void clearAllPushServiceNotifications(Context context)
/*     */   {
/* 211 */     SharedPreferences sp = context.getSharedPreferences("RongPushAppConfig", 0);
/* 212 */     String pushTypeUsing = sp.getString("pushType", "");
/* 213 */     if (pushTypeUsing.equals("MI")) {
/* 214 */       MiPushClient.clearNotification(context);
/*     */     }
/* 216 */     RongNotificationInterface.removeAllPushServiceNotification(context);
/*     */   }
/*     */ 
/*     */   public static void clearNotificationById(Context context, int notificationId)
/*     */   {
/* 226 */     SharedPreferences sp = context.getSharedPreferences("RongPushAppConfig", 0);
/* 227 */     String pushTypeUsing = sp.getString("pushType", "");
/* 228 */     if (pushTypeUsing.equals("MI")) {
/* 229 */       MiPushClient.clearNotification(context, notificationId);
/*     */     }
/*     */ 
/* 232 */     RongNotificationInterface.removeNotification(context, notificationId);
/*     */   }
/*     */ 
/*     */   public static void recordNotificationEvent(String pushId)
/*     */   {
/* 245 */     Map map = new HashMap();
/*     */ 
/* 247 */     if ((pushId == null) || (pushId.equals(""))) {
/* 248 */       RLog.e("RongPushClient", "pushId can't be null!");
/* 249 */       return;
/*     */     }
/*     */ 
/* 252 */     if (!Statistics.sharedInstance().isInitialized()) {
/* 253 */       RLog.e("RongPushClient", "Statistics should be initialized firstly!");
/* 254 */       return;
/*     */     }
/* 256 */     RLog.i("RongPushClient", "recordNotificationEvent");
/* 257 */     map.put("id", pushId);
/* 258 */     map.put("osName", "Android");
/* 259 */     Statistics.sharedInstance().recordEvent("pushEvent", map);
/*     */   }
/*     */ 
/*     */   public static void stopRongPush(Context context)
/*     */   {
/* 268 */     Intent intent = new Intent(context, PushService.class);
/* 269 */     intent.setAction("io.rong.push.intent.action.STOP_PUSH");
/* 270 */     context.startService(intent);
/*     */   }
/*     */ 
/*     */   public static void sendNotification(Context context, PushNotificationMessage notificationMessage)
/*     */   {
/* 280 */     RongNotificationInterface.sendNotification(context, notificationMessage);
/*     */   }
/*     */ 
/*     */   public static void checkManifest(Context context)
/*     */     throws RongException
/*     */   {
/* 289 */     checkService(context);
/* 290 */     checkReceivers(context);
/*     */   }
/*     */ 
/*     */   private static boolean isConfigChanged(Context context) {
/* 294 */     SharedPreferences sp = context.getSharedPreferences("RongPushAppConfig", 0);
/* 295 */     boolean isChanged = false;
/*     */ 
/* 297 */     if ((sp.getBoolean("isGCMEnabled", false)) && (!registeredType.contains("GCM"))) {
/* 298 */       sp.edit().remove("isGCMEnabled").commit();
/* 299 */       isChanged = true;
/*     */     }
/* 301 */     if ((sp.getBoolean("isMiEnabled", false)) && (!registeredType.contains("MI"))) {
/* 302 */       sp.edit().remove("isMiEnabled").commit();
/* 303 */       isChanged = true;
/*     */     }
/* 305 */     if ((sp.getBoolean("isHWEnabled", false)) && (!registeredType.contains("HW"))) {
/* 306 */       sp.edit().remove("isHWEnabled").commit();
/* 307 */       isChanged = true;
/*     */     }
/*     */ 
/* 310 */     if (isChanged) {
/* 311 */       SharedPreferences pushSp = context.getSharedPreferences("RongPush", 0);
/* 312 */       pushSp.edit().clear().commit();
/*     */     }
/* 314 */     return isChanged;
/*     */   }
/*     */ 
/*     */   private static void checkReceivers(Context context) throws RongException {
/* 318 */     SharedPreferences sp = context.getSharedPreferences("RongPushAppConfig", 0);
/*     */ 
/* 320 */     Intent intent = new Intent("io.rong.push.intent.action.HEART_BEAT");
/* 321 */     intent.setPackage(context.getPackageName());
/* 322 */     findAndCheckReceiverInfo(context.getPackageManager(), intent, PushReceiver.class);
/*     */ 
/* 324 */     intent = new Intent("io.rong.push.intent.MESSAGE_ARRIVED");
/* 325 */     intent.setPackage(context.getPackageName());
/* 326 */     List infoList = context.getPackageManager().queryBroadcastReceivers(intent, 64);
/* 327 */     boolean hasConfiged = false;
/* 328 */     Iterator infoIterator = infoList.iterator();
/*     */ 
/* 330 */     while (infoIterator.hasNext()) {
/* 331 */       ResolveInfo resolveInfo = (ResolveInfo)infoIterator.next();
/* 332 */       ActivityInfo activityInfo = resolveInfo.activityInfo;
/*     */       try
/*     */       {
/* 335 */         hasConfiged = (activityInfo != null) && (!TextUtils.isEmpty(activityInfo.name)) && (PushMessageReceiver.class.isAssignableFrom(Class.forName(activityInfo.name))) && (activityInfo.enabled);
/* 336 */         if (hasConfiged)
/* 337 */           break;
/*     */       }
/*     */       catch (ClassNotFoundException e) {
/* 340 */         hasConfiged = false;
/*     */       }
/*     */     }
/*     */ 
/* 344 */     if (!hasConfiged) {
/* 345 */       throw new RongException("Receiver: none of the subclasses of PushMessageReceiver is enabled or defined.");
/*     */     }
/*     */ 
/* 349 */     if (sp.getBoolean("isGCMEnabled", false)) {
/* 350 */       intent = new Intent("com.google.android.c2dm.intent.RECEIVE");
/* 351 */       intent.setPackage(context.getPackageName());
/* 352 */       findAndCheckReceiverInfo(context.getPackageManager(), intent, GcmReceiver.class);
/*     */     }
/* 354 */     if (sp.getBoolean("isMiEnabled", false)) {
/* 355 */       intent = new Intent("com.xiaomi.mipush.RECEIVE_MESSAGE");
/* 356 */       intent.setPackage(context.getPackageName());
/* 357 */       findAndCheckReceiverInfo(context.getPackageManager(), intent, MiMessageReceiver.class);
/*     */ 
/* 359 */       intent = new Intent("android.net.conn.CONNECTIVITY_CHANGE");
/* 360 */       intent.setPackage(context.getPackageName());
/* 361 */       findAndCheckReceiverInfo(context.getPackageManager(), intent, NetworkStatusReceiver.class);
/*     */ 
/* 363 */       intent = new Intent("com.xiaomi.push.PING_TIMER");
/* 364 */       intent.setPackage(context.getPackageName());
/* 365 */       findAndCheckReceiverInfo(context.getPackageManager(), intent, PingReceiver.class);
/*     */     }
/* 367 */     if (sp.getBoolean("isHWEnabled", false)) {
/* 368 */       intent = new Intent("com.huawei.intent.action.PUSH");
/* 369 */       intent.setPackage(context.getPackageName());
/* 370 */       findAndCheckReceiverInfo(context.getPackageManager(), intent, PushEventReceiver.class);
/*     */ 
/* 372 */       intent = new Intent("com.huawei.android.push.intent.REGISTER");
/* 373 */       intent.setPackage(context.getPackageName());
/* 374 */       findAndCheckReceiverInfo(context.getPackageManager(), intent, PushBootReceiver.class);
/*     */ 
/* 376 */       intent = new Intent("com.huawei.android.push.intent.REGISTRATION");
/* 377 */       intent.setPackage(context.getPackageName());
/* 378 */       findAndCheckReceiverInfo(context.getPackageManager(), intent, HWReceiver.class);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void checkService(Context context) throws RongException {
/* 383 */     SharedPreferences sp = context.getSharedPreferences("RongPushAppConfig", 0);
/* 384 */     ArrayList serviceList = new ArrayList();
/* 385 */     serviceList.add("io.rong.push.core.PushRegistrationService");
/* 386 */     serviceList.add("io.rong.push.PushService");
/* 387 */     serviceList.add("io.rong.push.core.MessageHandleService");
/*     */ 
/* 389 */     if (sp.getBoolean("isGCMEnabled", false)) {
/* 390 */       serviceList.add("io.rong.push.platform.RongGcmListenerService");
/* 391 */       serviceList.add("io.rong.push.platform.RongGCMInstanceIDListenerService");
/*     */     }
/* 393 */     if (sp.getBoolean("isMiEnabled", false)) {
/* 394 */       serviceList.add("com.xiaomi.push.service.XMPushService");
/* 395 */       serviceList.add(PushMessageHandler.class.getCanonicalName());
/* 396 */       serviceList.add(MessageHandleService.class.getCanonicalName());
/*     */     }
/* 398 */     if (sp.getBoolean("isHWEnabled", false))
/* 399 */       serviceList.add("com.huawei.android.pushagent.PushService");
/*     */     PackageInfo packageInfo;
/*     */     try
/*     */     {
/* 404 */       packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 4);
/*     */     } catch (PackageManager.NameNotFoundException e) {
/* 406 */       throw new RongException("can't find packageName.", e);
/*     */     }
/* 408 */     if ((packageInfo != null) && (packageInfo.services != null)) {
/* 409 */       ServiceInfo[] services = packageInfo.services;
/*     */ 
/* 411 */       for (int index = 0; index < services.length; index++) {
/* 412 */         ServiceInfo info = services[index];
/* 413 */         if ((!TextUtils.isEmpty(info.name)) && (serviceList.contains(info.name))) {
/* 414 */           serviceList.remove(info.name);
/*     */ 
/* 416 */           if (serviceList.isEmpty())
/*     */           {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 423 */     if (!serviceList.isEmpty())
/* 424 */       throw new RongException(String.format("<service android:name=\"%1$s\" /> is missing.", new Object[] { serviceList.iterator().next() }));
/*     */   }
/*     */ 
/*     */   private static void findAndCheckReceiverInfo(PackageManager packageManager, Intent intent, Class<?> targetName) throws RongException
/*     */   {
/* 429 */     List resolveInfoList = packageManager.queryBroadcastReceivers(intent, 16384);
/* 430 */     boolean isConfig = false;
/* 431 */     Iterator iterator = resolveInfoList.iterator();
/*     */ 
/* 433 */     while (iterator.hasNext()) {
/* 434 */       ResolveInfo resolveInfo = (ResolveInfo)iterator.next();
/* 435 */       ActivityInfo activityInfo = resolveInfo.activityInfo;
/* 436 */       if ((activityInfo != null) && (targetName.getCanonicalName().equals(activityInfo.name))) {
/* 437 */         isConfig = true;
/* 438 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 442 */     if (!isConfig)
/* 443 */       throw new RongException(String.format("<receiver android:name=\"%1$s\" /> is missing or disabled.", new Object[] { targetName.getCanonicalName() }));
/*     */   }
/*     */ 
/*     */   private static String getSupportedPushTypes()
/*     */   {
/* 448 */     String pushTypes = "";
/* 449 */     for (String typeInfo : registeredType) {
/* 450 */       if (pushTypes.isEmpty()) {
/* 451 */         pushTypes = typeInfo;
/*     */       } else {
/* 453 */         pushTypes = pushTypes + "|";
/* 454 */         pushTypes = pushTypes + typeInfo;
/*     */       }
/*     */     }
/* 457 */     return pushTypes;
/*     */   }
/*     */ 
/*     */   private static boolean checkPlayServices(Context context)
/*     */     throws Exception
/*     */   {
/* 467 */     GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
/* 468 */     int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
/* 469 */     if (resultCode != 0) {
/* 470 */       throw new Exception(Integer.toString(resultCode));
/*     */     }
/* 472 */     return true;
/*     */   }
/*     */ 
/*     */   public static enum ConversationType
/*     */   {
/* 480 */     NONE(0, "none"), 
/*     */ 
/* 484 */     PRIVATE(1, "private"), 
/*     */ 
/* 489 */     DISCUSSION(2, "discussion"), 
/*     */ 
/* 494 */     GROUP(3, "group"), 
/*     */ 
/* 499 */     CHATROOM(4, "chatroom"), 
/*     */ 
/* 504 */     CUSTOMER_SERVICE(5, "customer_service"), 
/*     */ 
/* 509 */     SYSTEM(6, "system"), 
/*     */ 
/* 514 */     APP_PUBLIC_SERVICE(7, "app_public_service"), 
/*     */ 
/* 519 */     PUBLIC_SERVICE(8, "public_service"), 
/*     */ 
/* 524 */     PUSH_SERVICE(9, "push_service");
/*     */ 
/* 527 */     private int value = 1;
/* 528 */     private String name = "";
/*     */ 
/*     */     private ConversationType(int value, String name)
/*     */     {
/* 536 */       this.value = value;
/* 537 */       this.name = name;
/*     */     }
/*     */ 
/*     */     public int getValue()
/*     */     {
/* 546 */       return this.value;
/*     */     }
/*     */ 
/*     */     public String getName()
/*     */     {
/* 555 */       return this.name;
/*     */     }
/*     */ 
/*     */     public static ConversationType setValue(int code)
/*     */     {
/* 565 */       for (ConversationType c : values()) {
/* 566 */         if (code == c.getValue()) {
/* 567 */           return c;
/*     */         }
/*     */       }
/* 570 */       return PRIVATE;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.RongPushClient
 * JD-Core Version:    0.6.0
 */
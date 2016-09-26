/*     */ package io.rong.push.notification;
/*     */ 
/*     */ import android.app.Notification;
/*     */ import android.app.Notification.Builder;
/*     */ import android.app.NotificationManager;
/*     */ import android.app.PendingIntent;
/*     */ import android.content.Context;
/*     */ import android.content.Intent;
/*     */ import android.content.pm.ApplicationInfo;
/*     */ import android.content.pm.PackageManager;
/*     */ import android.content.res.Resources;
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.drawable.BitmapDrawable;
/*     */ import android.media.RingtoneManager;
/*     */ import android.net.Uri;
/*     */ import android.os.Build.VERSION;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.push.RongPushClient.ConversationType;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ 
/*     */ public class RongNotificationInterface
/*     */ {
/*  26 */   private static HashMap<String, List<PushNotificationMessage>> messageCache = new HashMap();
/*  27 */   private static int NOTIFICATION_ID = 1000;
/*  28 */   private static int PUSH_SERVICE_NOTIFICATION_ID = 2000;
/*  29 */   private static int VOIP_NOTIFICATION_ID = 3000;
/*     */   private static final int NEW_NOTIFICATION_LEVEL = 11;
/*     */   private static final int PUSH_REQUEST_CODE = 200;
/*  32 */   private static boolean isInNeglectTime = false;
/*     */   private static final int NEGLECT_TIME = 1000;
/*  34 */   private static Timer timer = new Timer();
/*     */ 
/*     */   public static void sendNotification(Context context, PushNotificationMessage message)
/*     */   {
/*  43 */     if (messageCache == null) {
/*  44 */       messageCache = new HashMap();
/*     */     }
/*     */ 
/*  47 */     RongPushClient.ConversationType conversationType = message.getConversationType();
/*  48 */     String objName = message.getObjectName();
/*     */ 
/*  50 */     String content = "";
/*     */ 
/*  52 */     boolean isMulti = false;
/*  53 */     int requestCode = 200;
/*  54 */     SoundType soundType = SoundType.DEFAULT;
/*     */ 
/*  57 */     if ((TextUtils.isEmpty(objName)) || (conversationType == null)) {
/*  58 */       return;
/*     */     }
/*  60 */     if (isInNeglectTime)
/*  61 */       soundType = SoundType.SILENT;
/*     */     String title;
/*     */     int notificationId;
/*  63 */     if ((conversationType.equals(RongPushClient.ConversationType.SYSTEM)) || (conversationType.equals(RongPushClient.ConversationType.PUSH_SERVICE)))
/*     */     {
/*  65 */       String title = message.getPushTitle();
/*  66 */       if (TextUtils.isEmpty(title)) {
/*  67 */         title = (String)context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
/*     */       }
/*  69 */       content = message.getPushContent();
/*  70 */       int notificationId = PUSH_SERVICE_NOTIFICATION_ID;
/*  71 */       requestCode = 300;
/*  72 */       PUSH_SERVICE_NOTIFICATION_ID += 1;
/*  73 */     } else if ((objName.equals("RC:VCInvite")) || (objName.equals("RC:VCModifyMem")) || (objName.equals("RC:VCHangup"))) {
/*  74 */       if (objName.equals("RC:VCHangup")) {
/*  75 */         removeNotification(context, VOIP_NOTIFICATION_ID);
/*  76 */         return;
/*     */       }
/*  78 */       int notificationId = VOIP_NOTIFICATION_ID;
/*  79 */       soundType = SoundType.VOIP;
/*  80 */       requestCode = 400;
/*  81 */       String title = (String)context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
/*  82 */       content = message.getPushContent();
/*     */     }
/*     */     else {
/*  85 */       List messages = (List)messageCache.get(message.getTargetId());
/*  86 */       if (messages == null) {
/*  87 */         messages = new ArrayList();
/*  88 */         messages.add(message);
/*  89 */         messageCache.put(message.getTargetId(), messages);
/*     */       } else {
/*  91 */         messages.add(message);
/*     */       }
/*  93 */       if (messageCache.size() > 1) {
/*  94 */         isMulti = true;
/*     */       }
/*  96 */       title = getNotificationTitle(context);
/*  97 */       notificationId = NOTIFICATION_ID;
/*     */     }
/*  99 */     PendingIntent intent = createPendingIntent(context, message, requestCode, isMulti);
/* 100 */     Notification notification = createNotification(context, title, intent, content, soundType);
/* 101 */     NotificationManager nm = (NotificationManager)context.getSystemService("notification");
/* 102 */     if (notification != null) {
/* 103 */       nm.notify(notificationId, notification);
/*     */     }
/*     */ 
/* 106 */     if (!isInNeglectTime) {
/* 107 */       timer.schedule(new TimerTask()
/*     */       {
/*     */         public void run() {
/* 110 */           RongNotificationInterface.access$002(false);
/*     */         }
/*     */       }
/*     */       , 1000L);
/*     */ 
/* 113 */       isInNeglectTime = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void removeAllNotification(Context context)
/*     */   {
/* 123 */     messageCache.clear();
/* 124 */     NotificationManager nm = (NotificationManager)context.getSystemService("notification");
/* 125 */     nm.cancelAll();
/* 126 */     NOTIFICATION_ID = 1000;
/*     */   }
/*     */ 
/*     */   public static void removeAllPushNotification(Context context)
/*     */   {
/* 135 */     messageCache.clear();
/* 136 */     NotificationManager nm = (NotificationManager)context.getSystemService("notification");
/* 137 */     nm.cancel(NOTIFICATION_ID);
/* 138 */     nm.cancel(VOIP_NOTIFICATION_ID);
/*     */   }
/*     */ 
/*     */   public static void removeAllPushServiceNotification(Context context)
/*     */   {
/* 147 */     NotificationManager nm = (NotificationManager)context.getSystemService("notification");
/* 148 */     for (int i = PUSH_SERVICE_NOTIFICATION_ID; i >= 1000; i--) {
/* 149 */       nm.cancel(i);
/*     */     }
/* 151 */     PUSH_SERVICE_NOTIFICATION_ID = 2000;
/*     */   }
/*     */ 
/*     */   public static void removeNotification(Context context, int notificationId) {
/* 155 */     if (notificationId < 0) {
/* 156 */       return;
/*     */     }
/* 158 */     if ((notificationId >= NOTIFICATION_ID) && (notificationId < PUSH_SERVICE_NOTIFICATION_ID)) {
/* 159 */       messageCache.clear();
/*     */     }
/*     */ 
/* 162 */     NotificationManager nm = (NotificationManager)context.getSystemService("notification");
/* 163 */     nm.cancel(notificationId);
/*     */   }
/*     */ 
/*     */   private static PendingIntent createPendingIntent(Context context, PushNotificationMessage message, int requestCode, boolean isMulti) {
/* 167 */     Intent intent = new Intent();
/* 168 */     intent.setAction("io.rong.push.intent.MESSAGE_CLICKED");
/* 169 */     intent.putExtra("message", message);
/* 170 */     intent.putExtra("isMulti", isMulti);
/* 171 */     intent.setPackage(context.getPackageName());
/* 172 */     return PendingIntent.getBroadcast(context, requestCode, intent, 134217728);
/*     */   }
/*     */ 
/*     */   private static String getNotificationContent(Context context)
/*     */   {
/* 177 */     String rc_notification_new_msg = context.getResources().getString(context.getResources().getIdentifier("rc_notification_new_msg", "string", context.getPackageName()));
/* 178 */     String rc_notification_new_plural_msg = context.getResources().getString(context.getResources().getIdentifier("rc_notification_new_plural_msg", "string", context.getPackageName()));
/*     */     String content;
/*     */     String content;
/* 180 */     if (messageCache.size() == 1) {
/* 181 */       Collection collection = messageCache.values();
/* 182 */       List msg = (List)collection.iterator().next();
/* 183 */       PushNotificationMessage notificationMessage = (PushNotificationMessage)msg.get(0);
/*     */       String content;
/* 185 */       if (msg.size() == 1)
/* 186 */         content = notificationMessage.getPushContent();
/*     */       else
/* 188 */         content = String.format(rc_notification_new_msg, new Object[] { notificationMessage.getTargetUserName(), Integer.valueOf(msg.size()) });
/*     */     }
/*     */     else {
/* 191 */       int count = 0;
/* 192 */       Collection collection = messageCache.values();
/* 193 */       for (List msg : collection) {
/* 194 */         count += msg.size();
/*     */       }
/* 196 */       content = String.format(rc_notification_new_plural_msg, new Object[] { Integer.valueOf(messageCache.size()), Integer.valueOf(count) });
/*     */     }
/* 198 */     return content;
/*     */   }
/*     */ 
/*     */   private static String getNotificationTitle(Context context)
/*     */   {
/*     */     String title;
/*     */     String title;
/* 203 */     if (messageCache.size() == 1) {
/* 204 */       Collection collection = messageCache.values();
/* 205 */       List msg = (List)collection.iterator().next();
/* 206 */       PushNotificationMessage notificationMessage = (PushNotificationMessage)msg.get(0);
/* 207 */       title = notificationMessage.getTargetUserName();
/*     */     } else {
/* 209 */       title = (String)context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
/*     */     }
/* 211 */     return title;
/*     */   }
/*     */ 
/*     */   private static Notification createNotification(Context context, String title, PendingIntent pendingIntent, String content, SoundType soundType) {
/* 215 */     String tickerText = context.getResources().getString(context.getResources().getIdentifier("rc_notification_ticker_text", "string", context.getPackageName()));
/*     */ 
/* 217 */     if (TextUtils.isEmpty(content))
/* 218 */       content = getNotificationContent(context);
/*     */     Notification notification;
/* 220 */     if (Build.VERSION.SDK_INT < 11)
/*     */     {
/*     */       try {
/* 223 */         notification = new Notification(context.getApplicationInfo().icon, tickerText, System.currentTimeMillis());
/*     */ 
/* 225 */         Class classType = Notification.class;
/* 226 */         Method method = classType.getMethod("setLatestEventInfo", new Class[] { Context.class, CharSequence.class, CharSequence.class, PendingIntent.class });
/* 227 */         method.invoke(notification, new Object[] { context, title, content, pendingIntent });
/*     */ 
/* 229 */         notification.flags = 16;
/* 230 */         notification.defaults = -1;
/*     */       } catch (Exception e) {
/* 232 */         e.printStackTrace();
/* 233 */         return null;
/*     */       }
/*     */     } else {
/* 236 */       boolean isLollipop = Build.VERSION.SDK_INT >= 21;
/* 237 */       int smallIcon = context.getResources().getIdentifier("notification_small_icon", "drawable", context.getPackageName());
/*     */ 
/* 239 */       if ((smallIcon <= 0) || (!isLollipop)) {
/* 240 */         smallIcon = context.getApplicationInfo().icon;
/*     */       }
/*     */ 
/* 243 */       int defaults = -1;
/* 244 */       Uri sound = null;
/* 245 */       if (soundType.equals(SoundType.SILENT)) {
/* 246 */         defaults = 4;
/*     */       }
/* 248 */       if (soundType.equals(SoundType.VOIP)) {
/* 249 */         defaults = 6;
/* 250 */         sound = RingtoneManager.getDefaultUri(1);
/*     */       }
/*     */ 
/* 253 */       BitmapDrawable bitmapDrawable = (BitmapDrawable)context.getApplicationInfo().loadIcon(context.getPackageManager());
/* 254 */       Bitmap appIcon = bitmapDrawable.getBitmap();
/* 255 */       Notification.Builder builder = new Notification.Builder(context);
/* 256 */       builder.setLargeIcon(appIcon);
/* 257 */       builder.setSmallIcon(smallIcon);
/* 258 */       builder.setTicker(tickerText);
/* 259 */       builder.setContentTitle(title);
/* 260 */       builder.setContentText(content);
/* 261 */       builder.setContentIntent(pendingIntent);
/* 262 */       builder.setAutoCancel(true);
/* 263 */       builder.setSound(sound);
/* 264 */       builder.setDefaults(defaults);
/* 265 */       notification = builder.getNotification();
/*     */     }
/* 267 */     return notification;
/*     */   }
/*     */ 
/*     */   private static enum SoundType {
/* 271 */     DEFAULT(0), 
/* 272 */     SILENT(1), 
/* 273 */     VOIP(2);
/*     */ 
/*     */     int value;
/*     */ 
/* 278 */     private SoundType(int v) { this.value = v;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.notification.RongNotificationInterface
 * JD-Core Version:    0.6.0
 */
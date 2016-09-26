/*     */ package io.rong.push.core;
/*     */ 
/*     */ import android.annotation.TargetApi;
/*     */ import android.app.AlarmManager;
/*     */ import android.app.PendingIntent;
/*     */ import android.content.ComponentName;
/*     */ import android.content.Context;
/*     */ import android.content.Intent;
/*     */ import android.content.SharedPreferences;
/*     */ import android.content.SharedPreferences.Editor;
/*     */ import android.content.pm.PackageManager;
/*     */ import android.os.Build.VERSION;
/*     */ import android.os.Bundle;
/*     */ import android.os.Handler;
/*     */ import android.os.Message;
/*     */ import android.os.SystemClock;
/*     */ import android.text.TextUtils;
/*     */ import com.huawei.android.pushagent.PushBootReceiver;
/*     */ import io.rong.imlib.common.DeviceUtils;
/*     */ import io.rong.push.PushReceiver;
/*     */ import io.rong.push.PushService;
/*     */ import io.rong.push.common.RLog;
/*     */ import io.rong.push.common.stateMachine.State;
/*     */ import io.rong.push.common.stateMachine.StateMachine;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class PushConnectivityManager extends StateMachine
/*     */ {
/*     */   private static final String TAG = "PushConnectivityManager";
/*     */   private Context mContext;
/*     */   private PushClient pushClient;
/*     */   private String enabledPushTypes;
/*     */   private String appKey;
/*     */   private String deviceId;
/*     */   private String serverDomain;
/*     */   private static final int EVENT_CONNECT = 1;
/*     */   private static final int EVENT_CONNECTED = 2;
/*     */   private static final int EVENT_DISCONNECT = 3;
/*     */   private static final int EVENT_DISCONNECTED = 4;
/*     */   private static final int EVENT_HEART_BEAT = 5;
/*     */   private static final int EVENT_PING_FAILURE = 6;
/*     */   private static final int EVENT_PING_SUCCESS = 7;
/*     */   private static final int EVENT_USER_OPERATION = 8;
/*     */   private static final int EVENT_SEND_REGISTRATION_INFO = 9;
/*     */   private static final int EVENT_REGET_NAVI = 10;
/*     */   private static final long IP_EXPIRE_TIME = 7200000L;
/*  60 */   private NetworkType networkType = NetworkType.NONE;
/*  61 */   private boolean initialized = false;
/*  62 */   private int ALARM_REQUEST_CODE = 101;
/*  63 */   private int ALARM_PING_REQUEST_CODE = 102;
/*  64 */   private int mNavigationRetryTimes = 1;
/*  65 */   private int mReconnectTimes = 1;
/*     */ 
/* 214 */   private DisconnectedState disconnectedState = new DisconnectedState(null);
/*     */ 
/* 319 */   private ConnectingState connectingState = new ConnectingState(null);
/*     */ 
/* 353 */   PingState pingState = new PingState(null);
/*     */ 
/* 409 */   private ConnectedState connectedState = new ConnectedState(null);
/*     */ 
/*     */   public static PushConnectivityManager getInstance()
/*     */   {
/*  76 */     return Singleton.sInstance;
/*     */   }
/*     */ 
/*     */   protected PushConnectivityManager() {
/*  80 */     super("PushConnectivityManager");
/*     */   }
/*     */ 
/*     */   public boolean isInitialized() {
/*  84 */     return this.initialized;
/*     */   }
/*     */ 
/*     */   public void init(Context context, String deviceId, String appKey, String pushTypes) {
/*  88 */     RLog.d("PushConnectivityManager", "init, initialized = " + this.initialized + ", deviceId = " + deviceId + ", appKey = " + appKey + ",enabledPushTypes:" + pushTypes);
/*  89 */     this.mContext = context;
/*  90 */     this.initialized = true;
/*  91 */     this.enabledPushTypes = pushTypes;
/*  92 */     this.appKey = appKey;
/*  93 */     this.deviceId = deviceId;
/*     */ 
/*  95 */     this.pushClient = new PushClient(DeviceUtils.getPhoneInformation(context), new PushClient.ClientListener(context)
/*     */     {
/*     */       public void onMessageArrived(PushProtocalStack.PublishMessage msg) {
/*  98 */         Bundle bundle = null;
/*  99 */         if ((msg == null) || (msg.getDataAsString() == null)) {
/* 100 */           RLog.e("PushConnectivityManager", "sendNotification, msg = null");
/* 101 */           return;
/*     */         }
/* 103 */         RLog.i("PushConnectivityManager", msg.getDataAsString());
/*     */         try
/*     */         {
/* 106 */           bundle = PushUtils.decode(msg.getDataAsString());
/*     */         } catch (JSONException e) {
/* 108 */           return;
/*     */         }
/*     */ 
/* 111 */         String packageName = bundle.getString("packageName");
/* 112 */         bundle.remove("packageName");
/* 113 */         if (TextUtils.isEmpty(packageName)) {
/* 114 */           RLog.e("PushConnectivityManager", "messageArrived.packageName is null!!!!");
/* 115 */           return;
/*     */         }
/*     */ 
/* 118 */         RLog.e("TAG", "new push message. packageName:" + packageName);
/* 119 */         Intent intent = new Intent();
/* 120 */         intent.setAction("io.rong.push.intent.MESSAGE_ARRIVED");
/* 121 */         intent.setPackage(packageName);
/* 122 */         intent.putExtras(bundle);
/* 123 */         if (Build.VERSION.SDK_INT >= 12)
/* 124 */           intent.setFlags(32);
/* 125 */         this.val$context.sendBroadcast(intent);
/*     */       }
/*     */ 
/*     */       public void onPingSuccess()
/*     */       {
/* 130 */         RLog.d("PushConnectivityManager", "onPingSuccess");
/*     */ 
/* 132 */         PushConnectivityManager.this.getHandler().sendEmptyMessage(7);
/*     */       }
/*     */ 
/*     */       public void onDisConnected()
/*     */       {
/* 137 */         RLog.d("PushConnectivityManager", "onDisConnected");
/* 138 */         PushConnectivityManager.this.getHandler().sendEmptyMessage(4);
/*     */       }
/*     */ 
/*     */       public void onPingFailure()
/*     */       {
/* 143 */         RLog.d("PushConnectivityManager", "onPingFailure");
/* 144 */         PushConnectivityManager.this.getHandler().sendEmptyMessage(6);
/*     */       }
/*     */     });
/* 148 */     addState(this.disconnectedState);
/* 149 */     addState(this.connectingState, this.disconnectedState);
/* 150 */     addState(this.connectedState, this.disconnectedState);
/* 151 */     addState(this.pingState, this.disconnectedState);
/* 152 */     setInitialState(this.disconnectedState);
/*     */ 
/* 154 */     start();
/*     */   }
/*     */ 
/*     */   public void setServerDomain(String domain) {
/* 158 */     RLog.i("PushConnectivityManager", "setServerDomain " + domain);
/* 159 */     this.serverDomain = domain;
/*     */   }
/*     */ 
/*     */   public void connect() {
/* 163 */     if (!isInitialized()) {
/* 164 */       RLog.e("PushConnectivityManager", "connect does not init.");
/* 165 */       return;
/*     */     }
/* 167 */     getHandler().sendEmptyMessage(1);
/*     */   }
/*     */ 
/*     */   public void ping() {
/* 171 */     if (!isInitialized()) {
/* 172 */       RLog.e("PushConnectivityManager", "ping: does not init.");
/* 173 */       return;
/*     */     }
/* 175 */     getHandler().sendEmptyMessage(5);
/*     */   }
/*     */ 
/*     */   public void onPingTimeout() {
/* 179 */     if (!isInitialized()) {
/* 180 */       RLog.e("PushConnectivityManager", "onPingTimeout: does not init.");
/* 181 */       return;
/*     */     }
/* 183 */     getHandler().sendEmptyMessage(6);
/*     */   }
/*     */ 
/*     */   public void sendRegistrationIDToServer(String regInfo) {
/* 187 */     if (!isInitialized()) {
/* 188 */       RLog.e("PushConnectivityManager", "sendRegistrationIDToServer: does not init.");
/* 189 */       return;
/*     */     }
/* 191 */     Message msg = new Message();
/* 192 */     msg.what = 9;
/* 193 */     msg.obj = regInfo;
/* 194 */     getHandler().sendMessage(msg);
/*     */   }
/*     */ 
/*     */   public void disconnect() {
/* 198 */     if (!isInitialized()) {
/* 199 */       RLog.e("PushConnectivityManager", "disconnect does not init.");
/* 200 */       return;
/*     */     }
/* 202 */     cancelHeartbeat();
/* 203 */     getHandler().sendEmptyMessage(3);
/*     */   }
/*     */ 
/*     */   public void setNetworkType(NetworkType networkType) {
/* 207 */     this.networkType = networkType;
/*     */   }
/*     */ 
/*     */   public NetworkType getNetworkType() {
/* 211 */     return this.networkType;
/*     */   }
/*     */ 
/*     */   private void internalConnect()
/*     */   {
/* 248 */     String address = getNavigationAddress();
/* 249 */     if (!TextUtils.isEmpty(address)) {
/* 250 */       String[] str = address.split(":");
/* 251 */       this.pushClient.connect(str[0], Integer.parseInt(str[1]), this.deviceId, new PushClient.ConnectStatusCallback()
/*     */       {
/*     */         public void onConnected() {
/* 254 */           RLog.d("PushConnectivityManager", "onConnected.");
/* 255 */           PushConnectivityManager.this.getHandler().sendEmptyMessage(2);
/*     */ 
/* 257 */           if (!TextUtils.isEmpty(PushConnectivityManager.this.enabledPushTypes)) {
/* 258 */             String packageName = PushConnectivityManager.this.mContext.getPackageName().replace("-", "_");
/*     */ 
/* 260 */             String manufacturer = DeviceUtils.getDeviceManufacturer();
/* 261 */             String information = String.format("%s-%s-%s-%s", new Object[] { PushConnectivityManager.access$600(PushConnectivityManager.this), PushConnectivityManager.access$800(PushConnectivityManager.this), packageName, manufacturer });
/* 262 */             PushConnectivityManager.this.pushClient.query(PushClient.QueryMethod.GET_PUSH_TYPE, information, PushConnectivityManager.this.deviceId, new PushClient.QueryCallback()
/*     */             {
/*     */               public void onSuccess(String pushType) {
/* 265 */                 if ((!TextUtils.isEmpty(pushType)) && ((pushType.equals("MI")) || (pushType.equals("HW")) || (pushType.equals("GCM")))) {
/* 266 */                   SharedPreferences sp = PushConnectivityManager.this.mContext.getSharedPreferences("RongPush", 0);
/* 267 */                   sp.edit().putString("pushTypeUsed", pushType).commit();
/* 268 */                   RLog.d("PushConnectivityManager", "send to registration.");
/* 269 */                   Intent intent = new Intent(PushConnectivityManager.this.mContext, PushRegistrationService.class);
/* 270 */                   intent.putExtra("pushType", pushType);
/* 271 */                   PushConnectivityManager.this.mContext.startService(intent);
/*     */                 }
/*     */ 
/* 275 */                 if ((PushConnectivityManager.this.enabledPushTypes.contains("HW")) && ((pushType == null) || (!pushType.equals("HW")))) {
/* 276 */                   RLog.d("PushConnectivityManager", "setToken. Stop HW.");
/*     */                   try {
/* 278 */                     ComponentName receiver = new ComponentName(PushConnectivityManager.this.mContext, PushBootReceiver.class);
/* 279 */                     PushConnectivityManager.this.mContext.getPackageManager().setComponentEnabledSetting(receiver, 2, 1);
/*     */                   }
/*     */                   catch (Exception e) {
/*     */                   }
/*     */                 }
/* 284 */                 if ((pushType != null) && (!pushType.equals("RONG")))
/*     */                   try {
/* 286 */                     ComponentName receiver = new ComponentName(PushConnectivityManager.this.mContext, PushReceiver.class);
/* 287 */                     PushConnectivityManager.this.mContext.getPackageManager().setComponentEnabledSetting(receiver, 2, 1);
/*     */                   }
/*     */                   catch (Exception e)
/*     */                   {
/*     */                   }
/*     */               }
/*     */ 
/*     */               public void onFailure() {
/* 295 */                 RLog.e("PushConnectivityManager", "Failure when query!");
/*     */               }
/*     */             });
/*     */           }
/*     */         }
/*     */ 
/*     */         public void onError(IOException e) {
/* 303 */           RLog.d("PushConnectivityManager", "connect onError");
/* 304 */           PushConnectivityManager.this.getHandler().sendEmptyMessage(4);
/*     */ 
/* 306 */           if (PushConnectivityManager.this.mReconnectTimes > 0) {
/* 307 */             PushConnectivityManager.access$1010(PushConnectivityManager.this);
/* 308 */             SharedPreferences.Editor sp = PushConnectivityManager.this.mContext.getSharedPreferences("RongPush", 0).edit();
/* 309 */             sp.remove("navigation_ip_value");
/* 310 */             sp.remove("navigation_time");
/* 311 */             sp.commit();
/* 312 */             PushConnectivityManager.this.getHandler().sendEmptyMessageDelayed(1, 5000L);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getNavigationAddress()
/*     */   {
/* 470 */     SharedPreferences sp = this.mContext.getSharedPreferences("RongPush", 0);
/* 471 */     String ip = sp.getString("navigation_ip_value", "");
/* 472 */     String deviceId = sp.getString("deviceId", "");
/* 473 */     long lastTime = sp.getLong("navigation_time", -1L);
/* 474 */     long currentTime = System.currentTimeMillis();
/*     */ 
/* 476 */     if ((TextUtils.isEmpty(ip)) || (currentTime > lastTime + 7200000L)) {
/* 477 */       ip = getNavigationAddress(deviceId);
/*     */     }
/*     */ 
/* 480 */     return ip;
/*     */   }
/*     */ 
/*     */   private String getNavigationAddress(String deviceId) {
/* 484 */     HttpURLConnection conn = null;
/* 485 */     BufferedInputStream responseStream = null;
/* 486 */     String address = "";
/*     */     try
/*     */     {
/*     */       URL url;
/*     */       URL url;
/* 489 */       if (!TextUtils.isEmpty(this.serverDomain))
/* 490 */         url = new URL("http://" + this.serverDomain + "/navipush.json");
/*     */       else {
/* 492 */         url = new URL("http://nav.cn.ronghub.com/navipush.json");
/*     */       }
/* 494 */       RLog.i("PushConnectivityManager", "navigation url : " + url);
/* 495 */       conn = (HttpURLConnection)url.openConnection();
/* 496 */       conn.setConnectTimeout(30000);
/* 497 */       conn.setReadTimeout(30000);
/* 498 */       conn.setUseCaches(false);
/* 499 */       conn.setDoInput(true);
/*     */ 
/* 501 */       conn.setDoOutput(true);
/* 502 */       conn.setRequestMethod("POST");
/* 503 */       OutputStream os = conn.getOutputStream();
/* 504 */       BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
/* 505 */       String param = "deviceId=" + deviceId;
/* 506 */       writer.write(param);
/* 507 */       writer.flush();
/* 508 */       writer.close();
/* 509 */       os.close();
/*     */ 
/* 511 */       conn.connect();
/*     */ 
/* 513 */       int responseCode = conn.getResponseCode();
/* 514 */       if ((responseCode >= 100) && (responseCode <= 300)) {
/* 515 */         responseStream = new BufferedInputStream(conn.getInputStream());
/* 516 */         ByteArrayOutputStream responseData = new ByteArrayOutputStream(256);
/*     */         int c;
/* 518 */         while ((c = responseStream.read()) != -1) {
/* 519 */           responseData.write(c);
/*     */         }
/*     */ 
/* 522 */         JSONObject responseDict = new JSONObject(responseData.toString("UTF-8"));
/* 523 */         boolean success = responseDict.optString("code").equalsIgnoreCase("200");
/* 524 */         if (success) {
/* 525 */           address = responseDict.optString("server");
/* 526 */           RLog.i("PushConnectivityManager", "getNavigationAddress.address:" + address);
/* 527 */           saveNavigationInfo(address, System.currentTimeMillis());
/*     */         }
/*     */       }
/* 530 */       else if (this.mNavigationRetryTimes > 0) {
/* 531 */         getHandler().sendEmptyMessageDelayed(10, 2000L);
/* 532 */         this.mNavigationRetryTimes -= 1;
/*     */       }
/*     */     }
/*     */     catch (Exception ignored) {
/* 536 */       RLog.e("PushConnectivityManager", "Exception when get navigation address.Retry again.");
/* 537 */       getHandler().sendEmptyMessage(3);
/* 538 */       if (this.mNavigationRetryTimes > 0) {
/* 539 */         getHandler().sendEmptyMessageDelayed(10, 2000L);
/* 540 */         this.mNavigationRetryTimes -= 1;
/*     */       }
/*     */     } finally {
/* 543 */       if (responseStream != null)
/*     */         try {
/* 545 */           responseStream.close();
/*     */         }
/*     */         catch (IOException ignored) {
/*     */         }
/* 549 */       if ((conn != null) && ((conn instanceof HttpURLConnection))) {
/* 550 */         conn.disconnect();
/*     */       }
/*     */     }
/*     */ 
/* 554 */     return address;
/*     */   }
/*     */ 
/*     */   private void saveNavigationInfo(String ip, long time) {
/* 558 */     SharedPreferences preferences = this.mContext.getSharedPreferences("RongPush", 0);
/* 559 */     SharedPreferences.Editor editor = preferences.edit();
/* 560 */     editor.putString("navigation_ip_value", ip);
/* 561 */     editor.putLong("navigation_time", time);
/* 562 */     editor.apply();
/*     */   }
/*     */ 
/*     */   public void startPingTimer()
/*     */   {
/* 569 */     RLog.i("PushConnectivityManager", "startPingTimer, 10s");
/*     */ 
/* 571 */     if (!isInitialized()) {
/* 572 */       RLog.e("PushConnectivityManager", "startPingTimer. does not init.");
/* 573 */       return;
/*     */     }
/*     */ 
/* 576 */     Intent intent = new Intent(this.mContext, PushReceiver.class);
/* 577 */     intent.setAction("io.rong.push.intent.action.HEART_BEAT");
/* 578 */     intent.putExtra("PING", "PING");
/* 579 */     PendingIntent pendingIntent = PendingIntent.getBroadcast(this.mContext, this.ALARM_PING_REQUEST_CODE, intent, 1);
/* 580 */     AlarmManager alarmMng = (AlarmManager)this.mContext.getSystemService("alarm");
/* 581 */     alarmMng.cancel(pendingIntent);
/* 582 */     alarmMng.set(2, SystemClock.elapsedRealtime() + 10000L, pendingIntent);
/*     */   }
/*     */ 
/*     */   public void stopPingTimer()
/*     */   {
/* 589 */     RLog.i("PushConnectivityManager", "stopPingTimer");
/*     */ 
/* 591 */     if (!isInitialized()) {
/* 592 */       RLog.e("PushConnectivityManager", "stopPingTimer. does not init.");
/* 593 */       return;
/*     */     }
/*     */ 
/* 596 */     Intent intent = new Intent(this.mContext, PushReceiver.class);
/* 597 */     intent.setAction("io.rong.push.intent.action.HEART_BEAT");
/* 598 */     intent.putExtra("PING", "PING");
/* 599 */     PendingIntent mPendingIntent = PendingIntent.getBroadcast(this.mContext, this.ALARM_PING_REQUEST_CODE, intent, 1);
/* 600 */     AlarmManager mAlarmMng = (AlarmManager)this.mContext.getSystemService("alarm");
/* 601 */     mAlarmMng.cancel(mPendingIntent);
/*     */   }
/*     */ 
/*     */   @TargetApi(23)
/*     */   public void setNextHeartbeat()
/*     */   {
/* 609 */     RLog.i("PushConnectivityManager", "startHeartbeat");
/*     */ 
/* 611 */     if (!isInitialized()) {
/* 612 */       RLog.e("PushConnectivityManager", "setNextHeartbeat. does not init.");
/* 613 */       return;
/*     */     }
/* 615 */     Intent intent = new Intent(this.mContext, PushReceiver.class);
/* 616 */     intent.setAction("io.rong.push.intent.action.HEART_BEAT");
/* 617 */     PendingIntent pendingIntent = PendingIntent.getBroadcast(this.mContext, this.ALARM_REQUEST_CODE, intent, 1);
/* 618 */     AlarmManager alarmManager = (AlarmManager)this.mContext.getSystemService("alarm");
/* 619 */     alarmManager.cancel(pendingIntent);
/*     */ 
/* 621 */     if (Build.VERSION.SDK_INT < 23)
/* 622 */       alarmManager.set(2, SystemClock.elapsedRealtime() + 240000L, pendingIntent);
/*     */     else
/* 624 */       alarmManager.setExactAndAllowWhileIdle(2, SystemClock.elapsedRealtime() + 240000L, pendingIntent);
/*     */   }
/*     */ 
/*     */   public void cancelHeartbeat()
/*     */   {
/* 632 */     RLog.i("PushConnectivityManager", "cancelHeartbeat");
/* 633 */     if (!isInitialized()) {
/* 634 */       RLog.e("PushConnectivityManager", "cancelHeartbeat. does not init.");
/* 635 */       return;
/*     */     }
/* 637 */     Intent intent = new Intent(this.mContext, PushReceiver.class);
/* 638 */     intent.setAction("io.rong.push.intent.action.HEART_BEAT");
/* 639 */     PendingIntent mPendingIntent = PendingIntent.getBroadcast(this.mContext, this.ALARM_REQUEST_CODE, intent, 1);
/*     */ 
/* 641 */     AlarmManager mAlarmMng = (AlarmManager)this.mContext.getSystemService("alarm");
/* 642 */     mAlarmMng.cancel(mPendingIntent);
/*     */ 
/* 644 */     stopPingTimer();
/*     */   }
/*     */ 
/*     */   private class ConnectedState extends State
/*     */   {
/*     */     private ConnectedState()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void enter()
/*     */     {
/* 414 */       RLog.d("PushConnectivityManager", "enter " + getClass().getSimpleName());
/*     */     }
/*     */ 
/*     */     public boolean processMessage(Message msg)
/*     */     {
/* 419 */       RLog.d("PushConnectivityManager", getClass().getSimpleName() + ": process msg = " + msg.what);
/*     */ 
/* 421 */       switch (msg.what) {
/*     */       case 3:
/* 423 */         PushConnectivityManager.this.pushClient.disconnect();
/* 424 */         break;
/*     */       case 5:
/* 426 */         PushConnectivityManager.this.pushClient.ping();
/* 427 */         PushConnectivityManager.this.transitionTo(PushConnectivityManager.this.pingState);
/* 428 */         break;
/*     */       case 6:
/* 430 */         PushConnectivityManager.this.stopPingTimer();
/* 431 */         PushConnectivityManager.this.pushClient.reset();
/* 432 */         PushConnectivityManager.this.transitionTo(PushConnectivityManager.this.disconnectedState);
/* 433 */         PushConnectivityManager.this.getHandler().sendEmptyMessage(1);
/* 434 */         break;
/*     */       case 4:
/* 436 */         PushConnectivityManager.this.pushClient.reset();
/* 437 */         PushConnectivityManager.this.transitionTo(PushConnectivityManager.this.disconnectedState);
/* 438 */         break;
/*     */       case 9:
/* 440 */         String info = (String)msg.obj + "|" + PushConnectivityManager.this.appKey;
/* 441 */         PushConnectivityManager.this.pushClient.query(PushClient.QueryMethod.SET_TOKEN, info, PushConnectivityManager.this.deviceId, new PushClient.QueryCallback()
/*     */         {
/*     */           public void onSuccess(String pushType) {
/* 444 */             RLog.d("PushConnectivityManager", "setToken.onSuccess.");
/* 445 */             SharedPreferences sp = PushConnectivityManager.this.mContext.getSharedPreferences("RongPush", 0);
/* 446 */             sp.edit().putString("pushTypeUsing", pushType).apply();
/* 447 */             PushConnectivityManager.this.cancelHeartbeat();
/* 448 */             PushConnectivityManager.this.getHandler().sendEmptyMessage(3);
/*     */ 
/* 451 */             PushConnectivityManager.this.mContext.stopService(new Intent(PushConnectivityManager.this.mContext, PushService.class));
/*     */           }
/*     */ 
/*     */           public void onFailure()
/*     */           {
/* 456 */             RLog.e("PushConnectivityManager", "setToken.onFailure.");
/*     */           } } );
/*     */       case 7:
/*     */       case 8:
/*     */       }
/* 461 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class PingState extends State
/*     */   {
/*     */     private PingState()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void enter()
/*     */     {
/* 364 */       RLog.d("PushConnectivityManager", "enter " + getClass().getSimpleName());
/* 365 */       PushConnectivityManager.this.startPingTimer();
/*     */     }
/*     */ 
/*     */     public boolean processMessage(Message msg)
/*     */     {
/* 370 */       RLog.d("PushConnectivityManager", getClass().getSimpleName() + ": process msg = " + msg.what);
/*     */ 
/* 372 */       switch (msg.what) {
/*     */       case 2:
/*     */       case 7:
/* 375 */         PushConnectivityManager.this.stopPingTimer();
/* 376 */         PushConnectivityManager.this.setNextHeartbeat();
/* 377 */         PushConnectivityManager.this.transitionTo(PushConnectivityManager.this.connectedState);
/* 378 */         break;
/*     */       case 5:
/* 380 */         PushConnectivityManager.this.pushClient.reset();
/* 381 */         PushConnectivityManager.this.transitionTo(PushConnectivityManager.this.disconnectedState);
/* 382 */         PushConnectivityManager.this.getHandler().sendEmptyMessage(1);
/* 383 */         break;
/*     */       case 6:
/* 385 */         PushConnectivityManager.this.stopPingTimer();
/* 386 */         PushConnectivityManager.this.pushClient.reset();
/* 387 */         PushConnectivityManager.this.getHandler().sendEmptyMessage(1);
/* 388 */         PushConnectivityManager.this.transitionTo(PushConnectivityManager.this.disconnectedState);
/* 389 */         break;
/*     */       case 1:
/* 391 */         PushConnectivityManager.this.internalConnect();
/* 392 */         PushConnectivityManager.this.transitionTo(PushConnectivityManager.this.connectingState);
/* 393 */         break;
/*     */       case 3:
/* 395 */         PushConnectivityManager.this.pushClient.disconnect();
/* 396 */         PushConnectivityManager.this.transitionTo(PushConnectivityManager.this.disconnectedState);
/* 397 */         break;
/*     */       case 4:
/* 399 */         PushConnectivityManager.this.pushClient.reset();
/* 400 */         PushConnectivityManager.this.transitionTo(PushConnectivityManager.this.disconnectedState);
/*     */       case 9:
/* 402 */         PushConnectivityManager.this.deferMessage(msg);
/*     */       case 8:
/*     */       }
/* 405 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ConnectingState extends State
/*     */   {
/*     */     private ConnectingState()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void enter()
/*     */     {
/* 324 */       RLog.d("PushConnectivityManager", "enter " + getClass().getSimpleName());
/*     */     }
/*     */ 
/*     */     public boolean processMessage(Message msg)
/*     */     {
/* 329 */       RLog.d("PushConnectivityManager", getClass().getSimpleName() + ": process msg = " + msg.what);
/*     */ 
/* 331 */       switch (msg.what) {
/*     */       case 2:
/* 333 */         PushConnectivityManager.this.setNextHeartbeat();
/* 334 */         PushConnectivityManager.this.transitionTo(PushConnectivityManager.this.connectedState);
/* 335 */         break;
/*     */       case 1:
/*     */       case 9:
/* 338 */         PushConnectivityManager.this.deferMessage(msg);
/* 339 */         break;
/*     */       case 3:
/*     */       case 4:
/* 342 */         PushConnectivityManager.this.pushClient.reset();
/* 343 */         PushConnectivityManager.this.transitionTo(PushConnectivityManager.this.disconnectedState);
/* 344 */         break;
/*     */       case 7:
/* 346 */         PushConnectivityManager.this.stopPingTimer();
/*     */       case 5:
/*     */       case 6:
/* 349 */       case 8: } return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class DisconnectedState extends State
/*     */   {
/*     */     private DisconnectedState()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void enter()
/*     */     {
/* 219 */       RLog.d("PushConnectivityManager", "enter " + getClass().getSimpleName());
/*     */     }
/*     */ 
/*     */     public boolean processMessage(Message msg)
/*     */     {
/* 224 */       RLog.d("PushConnectivityManager", getClass().getSimpleName() + ": process msg = " + msg.what);
/*     */ 
/* 226 */       switch (msg.what) {
/*     */       case 9:
/* 228 */         PushConnectivityManager.this.deferMessage(msg);
/*     */       case 1:
/*     */       case 5:
/*     */       case 8:
/*     */       case 10:
/* 233 */         PushConnectivityManager.this.internalConnect();
/* 234 */         PushConnectivityManager.this.transitionTo(PushConnectivityManager.this.connectingState);
/* 235 */         break;
/*     */       case 7:
/* 237 */         PushConnectivityManager.this.stopPingTimer();
/* 238 */         break;
/*     */       case 4:
/* 240 */         PushConnectivityManager.this.pushClient.reset();
/*     */       case 2:
/*     */       case 3:
/* 243 */       case 6: } return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class Singleton
/*     */   {
/*  72 */     static PushConnectivityManager sInstance = new PushConnectivityManager();
/*     */   }
/*     */ 
/*     */   public static enum NetworkType
/*     */   {
/*  68 */     NONE, WIFI, MOBILE, ERROR;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.core.PushConnectivityManager
 * JD-Core Version:    0.6.0
 */
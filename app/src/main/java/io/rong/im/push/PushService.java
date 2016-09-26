/*     */ package io.rong.push;
/*     */ 
/*     */ import android.app.Service;
/*     */ import android.content.Intent;
/*     */ import android.content.SharedPreferences;
/*     */ import android.content.SharedPreferences.Editor;
/*     */ import android.net.ConnectivityManager;
/*     */ import android.net.NetworkInfo;
/*     */ import android.net.NetworkInfo.State;
/*     */ import android.os.IBinder;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.push.common.RLog;
/*     */ import io.rong.push.core.PushConnectivityManager;
/*     */ import io.rong.push.core.PushConnectivityManager.NetworkType;
/*     */ 
/*     */ public class PushService extends Service
/*     */ {
/*     */   private static final String TAG = "PushService";
/*     */ 
/*     */   public void onCreate()
/*     */   {
/*  20 */     super.onCreate();
/*  21 */     RLog.d("PushService", "OnCreate");
/*     */   }
/*     */ 
/*     */   public int onStartCommand(Intent intent, int flags, int startId)
/*     */   {
/*  26 */     SharedPreferences sp = getSharedPreferences("RongPush", 0);
/*  27 */     String appKey = sp.getString("appKey", "");
/*  28 */     String deviceId = sp.getString("deviceId", "");
/*  29 */     String pushTypes = sp.getString("enabledPushTypes", "");
/*  30 */     String pushDomain = sp.getString("pushDomain", "");
/*  31 */     if (!TextUtils.isEmpty(pushDomain)) {
/*  32 */       PushConnectivityManager.getInstance().setServerDomain(pushDomain);
/*     */     }
/*     */ 
/*  35 */     if ((!TextUtils.isEmpty(appKey)) && (!TextUtils.isEmpty(deviceId)) && (!PushConnectivityManager.getInstance().isInitialized())) {
/*  36 */       PushConnectivityManager.getInstance().init(this, deviceId, appKey, pushTypes);
/*  37 */       PushConnectivityManager.getInstance().connect();
/*     */     }
/*     */ 
/*  40 */     if ((intent == null) || (intent.getAction() == null)) {
/*  41 */       return 1;
/*     */     }
/*  43 */     RLog.d("PushService", "onStartCommand, action = " + intent.getAction());
/*  44 */     if ((intent.getAction().equals("io.rong.push.intent.action.INIT")) && (!PushConnectivityManager.getInstance().isInitialized())) {
/*  45 */       deviceId = intent.getStringExtra("deviceId");
/*  46 */       pushTypes = intent.getStringExtra("enabledPushTypes");
/*  47 */       appKey = intent.getStringExtra("appKey");
/*  48 */       pushDomain = intent.getStringExtra("pushDomain");
/*  49 */       if (!TextUtils.isEmpty(pushDomain)) {
/*  50 */         PushConnectivityManager.getInstance().setServerDomain(pushDomain);
/*     */       }
/*     */ 
/*  53 */       if ((!TextUtils.isEmpty(appKey)) && (!TextUtils.isEmpty(deviceId))) {
/*  54 */         sp.edit().putString("deviceId", deviceId).apply();
/*  55 */         sp.edit().putString("appKey", appKey).apply();
/*  56 */         sp.edit().putString("enabledPushTypes", pushTypes).apply();
/*     */ 
/*  58 */         PushConnectivityManager.getInstance().init(this, deviceId, appKey, pushTypes);
/*  59 */         PushConnectivityManager.getInstance().connect();
/*     */       } else {
/*  61 */         RLog.e("PushService", "appKey or deviceId is null.");
/*     */       }
/*  63 */     } else if (intent.getAction().equals("io.rong.push.intent.action.REGISTRATION_INFO")) {
/*  64 */       String info = intent.getStringExtra("regInfo");
/*  65 */       String[] infos = info.split("\\|");
/*  66 */       String pushType = getSharedPreferences("RongPush", 0).getString("pushTypeUsed", "");
/*  67 */       RLog.i("PushService", "received info:" + info + ",pushType cached:" + pushType);
/*  68 */       if (infos[0].equals(pushType))
/*  69 */         PushConnectivityManager.getInstance().sendRegistrationIDToServer(info);
/*     */       else
/*  71 */         RLog.e("PushService", "Push type received is different from the one cached. So ignore this event.");
/*     */     }
/*  73 */     else if (intent.getAction().equals("io.rong.push.intent.action.HEART_BEAT")) {
/*  74 */       String extra = intent.getStringExtra("PING");
/*  75 */       if (extra == null)
/*  76 */         PushConnectivityManager.getInstance().ping();
/*  77 */       else if (extra.equals("PING"))
/*  78 */         PushConnectivityManager.getInstance().onPingTimeout();
/*     */     }
/*  80 */     else if (intent.getAction().equals("io.rong.push.intent.action.STOP_PUSH")) {
/*  81 */       PushConnectivityManager.getInstance().disconnect();
/*  82 */     } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
/*  83 */       PushConnectivityManager.NetworkType last = PushConnectivityManager.getInstance().getNetworkType();
/*     */ 
/*  87 */       ConnectivityManager ConnManager = (ConnectivityManager)getSystemService("connectivity");
/*     */       NetworkInfo.State wifi_state;
/*     */       NetworkInfo.State wifi_state;
/*  90 */       if (ConnManager.getNetworkInfo(1) == null)
/*  91 */         wifi_state = null;
/*     */       else
/*  93 */         wifi_state = ConnManager.getNetworkInfo(1).getState();
/*     */       NetworkInfo.State mobile_state;
/*     */       NetworkInfo.State mobile_state;
/*  95 */       if (ConnManager.getNetworkInfo(0) == null)
/*  96 */         mobile_state = null;
/*     */       else {
/*  98 */         mobile_state = ConnManager.getNetworkInfo(0).getState();
/*     */       }
/* 100 */       if ((wifi_state != null) && (wifi_state == NetworkInfo.State.CONNECTED))
/* 101 */         PushConnectivityManager.getInstance().setNetworkType(PushConnectivityManager.NetworkType.WIFI);
/* 102 */       else if ((mobile_state != null) && (mobile_state == NetworkInfo.State.CONNECTED))
/* 103 */         PushConnectivityManager.getInstance().setNetworkType(PushConnectivityManager.NetworkType.MOBILE);
/*     */       else {
/* 105 */         PushConnectivityManager.getInstance().setNetworkType(PushConnectivityManager.NetworkType.ERROR);
/*     */       }
/*     */ 
/* 108 */       PushConnectivityManager.NetworkType current = PushConnectivityManager.getInstance().getNetworkType();
/* 109 */       RLog.d("PushService", "wifi = " + wifi_state + ", mobile = " + mobile_state + ", last = " + last + ", current = " + current);
/*     */ 
/* 118 */       if (current.equals(PushConnectivityManager.NetworkType.ERROR)) {
/* 119 */         PushConnectivityManager.getInstance().disconnect();
/*     */       }
/* 121 */       else if (last.equals(PushConnectivityManager.NetworkType.ERROR)) {
/* 122 */         PushConnectivityManager.getInstance().connect();
/*     */       } else {
/* 124 */         PushConnectivityManager.getInstance().disconnect();
/* 125 */         PushConnectivityManager.getInstance().connect();
/*     */       }
/*     */     }
/* 128 */     else if (("android.intent.action.USER_PRESENT".equals(intent.getAction())) || ("android.intent.action.ACTION_POWER_CONNECTED".equals(intent.getAction())) || ("android.intent.action.ACTION_POWER_DISCONNECTED".equals(intent.getAction())) || ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())))
/*     */     {
/* 132 */       PushConnectivityManager.getInstance().connect();
/*     */     }
/*     */ 
/* 135 */     return 1;
/*     */   }
/*     */ 
/*     */   public IBinder onBind(Intent intent)
/*     */   {
/* 140 */     return null;
/*     */   }
/*     */ 
/*     */   public void onDestroy()
/*     */   {
/* 145 */     RLog.d("PushService", "onDestroy");
/* 146 */     super.onDestroy();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.PushService
 * JD-Core Version:    0.6.0
 */
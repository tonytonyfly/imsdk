/*     */ package io.rong.imlib.statistics;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.pm.PackageInfo;
/*     */ import android.content.pm.PackageManager;
/*     */ import android.content.pm.PackageManager.NameNotFoundException;
/*     */ import android.content.res.Resources;
/*     */ import android.net.ConnectivityManager;
/*     */ import android.net.NetworkInfo;
/*     */ import android.os.Build;
/*     */ import android.os.Build.VERSION;
/*     */ import android.telephony.TelephonyManager;
/*     */ import android.util.DisplayMetrics;
/*     */ import android.util.Log;
/*     */ import android.view.Display;
/*     */ import android.view.WindowManager;
/*     */ import io.rong.imlib.common.BuildVar;
/*     */ import io.rong.imlib.common.DeviceUtils;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ class DeviceInfo
/*     */ {
/*     */   static String getOS()
/*     */   {
/*  56 */     return "Android";
/*     */   }
/*     */ 
/*     */   static String getOSVersion()
/*     */   {
/*  63 */     return Build.VERSION.RELEASE;
/*     */   }
/*     */ 
/*     */   static String getDevice()
/*     */   {
/*  70 */     return Build.MODEL;
/*     */   }
/*     */ 
/*     */   static String getResolution(Context context)
/*     */   {
/*  84 */     String resolution = "";
/*     */     try {
/*  86 */       WindowManager wm = (WindowManager)context.getSystemService("window");
/*  87 */       Display display = wm.getDefaultDisplay();
/*  88 */       DisplayMetrics metrics = new DisplayMetrics();
/*  89 */       display.getMetrics(metrics);
/*  90 */       resolution = metrics.widthPixels + "x" + metrics.heightPixels;
/*     */     } catch (Throwable t) {
/*  92 */       if (Statistics.sharedInstance().isLoggingEnabled()) {
/*  93 */         Log.i("Statistics", "Device resolution cannot be determined");
/*     */       }
/*     */     }
/*  96 */     return resolution;
/*     */   }
/*     */ 
/*     */   static String getDensity(Context context)
/*     */   {
/* 107 */     String densityStr = "";
/* 108 */     int density = context.getResources().getDisplayMetrics().densityDpi;
/* 109 */     switch (density) {
/*     */     case 120:
/* 111 */       densityStr = "LDPI";
/* 112 */       break;
/*     */     case 160:
/* 114 */       densityStr = "MDPI";
/* 115 */       break;
/*     */     case 213:
/* 117 */       densityStr = "TVDPI";
/* 118 */       break;
/*     */     case 240:
/* 120 */       densityStr = "HDPI";
/* 121 */       break;
/*     */     case 320:
/* 123 */       densityStr = "XHDPI";
/* 124 */       break;
/*     */     case 400:
/* 126 */       densityStr = "XMHDPI";
/* 127 */       break;
/*     */     case 480:
/* 129 */       densityStr = "XXHDPI";
/* 130 */       break;
/*     */     case 640:
/* 132 */       densityStr = "XXXHDPI";
/*     */     }
/*     */ 
/* 135 */     return densityStr;
/*     */   }
/*     */ 
/*     */   static String getCarrier(Context context)
/*     */   {
/* 147 */     String carrier = "";
/*     */     try {
/* 149 */       TelephonyManager manager = (TelephonyManager)context.getSystemService("phone");
/* 150 */       if (manager != null)
/* 151 */         carrier = manager.getNetworkOperatorName();
/*     */     }
/*     */     catch (SecurityException e)
/*     */     {
/*     */     }
/* 156 */     if ((carrier == null) || (carrier.length() == 0)) {
/* 157 */       carrier = "";
/* 158 */       if (Statistics.sharedInstance().isLoggingEnabled()) {
/* 159 */         Log.i("Statistics", "No carrier found");
/*     */       }
/*     */     }
/* 162 */     return carrier;
/*     */   }
/*     */ 
/*     */   static String getNetworkType(Context context) {
/* 166 */     String type = "UNKNOWN";
/* 167 */     ConnectivityManager connectMgr = (ConnectivityManager)context.getSystemService("connectivity");
/* 168 */     if (connectMgr != null) {
/* 169 */       NetworkInfo info = connectMgr.getActiveNetworkInfo();
/* 170 */       if (info != null) {
/* 171 */         if (info.getType() == 1)
/* 172 */           type = "WIFI";
/* 173 */         else if (info.getType() == 0)
/* 174 */           type = "MOBILE";
/*     */       }
/*     */     }
/* 177 */     return type;
/*     */   }
/*     */ 
/*     */   static String getLocale()
/*     */   {
/* 184 */     Locale locale = Locale.getDefault();
/* 185 */     return locale.getLanguage() + "_" + locale.getCountry();
/*     */   }
/*     */ 
/*     */   static String getAppVersion(Context context)
/*     */   {
/* 194 */     String result = "1.0";
/*     */     try {
/* 196 */       result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
/*     */     } catch (PackageManager.NameNotFoundException e) {
/* 198 */       if (Statistics.sharedInstance().isLoggingEnabled()) {
/* 199 */         Log.i("Statistics", "No app version found");
/*     */       }
/*     */     }
/* 202 */     return result;
/*     */   }
/*     */ 
/*     */   static String getStore(Context context)
/*     */   {
/* 209 */     String result = "";
/* 210 */     if (Build.VERSION.SDK_INT >= 3) {
/*     */       try {
/* 212 */         result = context.getPackageManager().getInstallerPackageName(context.getPackageName());
/*     */       } catch (Exception e) {
/* 214 */         if (Statistics.sharedInstance().isLoggingEnabled()) {
/* 215 */           Log.i("Statistics", "Can't get Installer package");
/*     */         }
/*     */       }
/* 218 */       if ((result == null) || (result.length() == 0)) {
/* 219 */         result = "";
/* 220 */         if (Statistics.sharedInstance().isLoggingEnabled()) {
/* 221 */           Log.i("Statistics", "No store found");
/*     */         }
/*     */       }
/*     */     }
/* 225 */     return result;
/*     */   }
/*     */ 
/*     */   static String getMetrics(Context context)
/*     */   {
/* 236 */     JSONObject json = new JSONObject();
/*     */ 
/* 238 */     fillJSONIfValuesNotEmpty(json, new String[] { "device", getDevice(), "osName", getOS(), "osVersion", getOSVersion(), "carrier", getCarrier(context), "resolution", getResolution(context), "density", getDensity(context), "locale", getLocale(), "appVersion", getAppVersion(context), "channel", getStore(context), "bundleId", context.getPackageName(), "sdkVersion", BuildVar.SDK_VERSION, "network", getNetworkType(context), "timeZone", TimeZone.getDefault().getDisplayName(false, 0), "imei", DeviceUtils.getDeviceIMEI(context), "imsi", DeviceUtils.getDeviceIMSI(context), "mac", DeviceUtils.getWifiMacAddress(context) });
/*     */ 
/* 256 */     String result = json.toString();
/*     */     try
/*     */     {
/* 259 */       result = URLEncoder.encode(result, "UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException ignored)
/*     */     {
/*     */     }
/* 264 */     return result;
/*     */   }
/*     */ 
/*     */   static void fillJSONIfValuesNotEmpty(JSONObject json, String[] objects)
/*     */   {
/*     */     try
/*     */     {
/* 276 */       if ((objects.length > 0) && (objects.length % 2 == 0))
/* 277 */         for (int i = 0; i < objects.length; i += 2) {
/* 278 */           String key = objects[i];
/* 279 */           String value = objects[(i + 1)];
/* 280 */           if ((value != null) && (value.length() > 0))
/* 281 */             json.put(key, value);
/*     */         }
/*     */     }
/*     */     catch (JSONException ignored)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.statistics.DeviceInfo
 * JD-Core Version:    0.6.0
 */
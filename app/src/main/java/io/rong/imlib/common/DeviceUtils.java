/*     */ package io.rong.imlib.common;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.SharedPreferences;
/*     */ import android.content.SharedPreferences.Editor;
/*     */ import android.net.ConnectivityManager;
/*     */ import android.net.NetworkInfo;
/*     */ import android.net.wifi.WifiInfo;
/*     */ import android.net.wifi.WifiManager;
/*     */ import android.os.Build;
/*     */ import android.os.Build.VERSION;
/*     */ import android.telephony.TelephonyManager;
/*     */ import android.text.TextUtils;
/*     */ import android.util.Base64;
/*     */ import android.util.Log;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.math.BigInteger;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.SecureRandom;
/*     */ 
/*     */ public class DeviceUtils
/*     */ {
/*     */   public static String getDeviceId(Context context, String appKey)
/*     */   {
/*  22 */     SharedPreferences sp = context.getSharedPreferences("Statistics", 0);
/*  23 */     String deviceId = sp.getString("deviceId", "");
/*     */ 
/*  25 */     if (TextUtils.isEmpty(deviceId)) {
/*  26 */       String[] params = new String[3];
/*  27 */       params[0] = getDeviceIMEI(context);
/*  28 */       params[1] = appKey;
/*  29 */       params[2] = context.getPackageName();
/*     */ 
/*  31 */       deviceId = ShortMD5(params);
/*  32 */       SharedPreferences.Editor editor = sp.edit();
/*  33 */       editor.putString("deviceId", deviceId);
/*  34 */       editor.apply();
/*     */     }
/*     */ 
/*  37 */     return deviceId;
/*     */   }
/*     */ 
/*     */   public static String getDeviceIMEI(Context context) {
/*  41 */     SharedPreferences sp = context.getSharedPreferences("Statistics", 0);
/*  42 */     String imei = sp.getString("IMEI", "");
/*     */ 
/*  44 */     if (TextUtils.isEmpty(imei)) {
/*     */       try {
/*  46 */         TelephonyManager tm = (TelephonyManager)context.getSystemService("phone");
/*  47 */         imei = tm.getDeviceId();
/*     */       } catch (SecurityException e) {
/*  49 */         Log.e("DeviceUtils", "SecurityException!!!");
/*     */       }
/*  51 */       if ((TextUtils.isEmpty(imei)) || (imei.equals("000000000000000")) || (imei.equals("000000000000"))) {
/*  52 */         SecureRandom random = new SecureRandom();
/*  53 */         imei = new BigInteger(64, random).toString(16);
/*     */       }
/*  55 */       SharedPreferences.Editor editor = sp.edit();
/*  56 */       editor.putString("IMEI", imei);
/*  57 */       editor.apply();
/*     */     }
/*  59 */     return imei;
/*     */   }
/*     */ 
/*     */   private static String ShortMD5(String[] args) {
/*     */     try {
/*  64 */       StringBuilder builder = new StringBuilder();
/*     */ 
/*  66 */       for (String arg : args) {
/*  67 */         builder.append(arg);
/*     */       }
/*     */ 
/*  70 */       MessageDigest mdInst = MessageDigest.getInstance("MD5");
/*  71 */       mdInst.update(builder.toString().getBytes());
/*  72 */       byte[] mds = mdInst.digest();
/*  73 */       mds = Base64.encode(mds, 0);
/*  74 */       String result = new String(mds);
/*  75 */       result = result.replace("=", "").replace("+", "-").replace("/", "_").replace("\n", "");
/*  76 */       return result; } catch (Exception e) {
/*     */     }
/*  78 */     return "";
/*     */   }
/*     */ 
/*     */   public static String getPhoneInformation(Context context)
/*     */   {
/*  84 */     String MCCMNC = "";
/*     */     try {
/*  86 */       TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService("phone");
/*  87 */       MCCMNC = telephonyManager.getNetworkOperator();
/*     */     } catch (SecurityException e) {
/*  89 */       Log.e("DeviceUtils", "SecurityException!!!");
/*     */     }
/*     */ 
/*  92 */     ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService("connectivity");
/*     */ 
/*  95 */     String network = "";
/*  96 */     if ((connectivityManager != null) && (connectivityManager.getActiveNetworkInfo() != null)) {
/*  97 */       network = connectivityManager.getActiveNetworkInfo().getTypeName();
/*     */     }
/*     */ 
/* 100 */     String manufacturer = Build.MANUFACTURER;
/* 101 */     String model = Build.MODEL;
/*     */ 
/* 103 */     if (manufacturer == null) manufacturer = "";
/* 104 */     if (model == null) model = "";
/*     */ 
/* 106 */     String devInfo = manufacturer;
/* 107 */     devInfo = new StringBuilder().append(devInfo).append("|").toString();
/* 108 */     devInfo = new StringBuilder().append(devInfo).append(model).toString();
/* 109 */     devInfo = new StringBuilder().append(devInfo).append("|").toString();
/* 110 */     devInfo = new StringBuilder().append(devInfo).append(String.valueOf(Build.VERSION.SDK_INT)).toString();
/* 111 */     devInfo = new StringBuilder().append(devInfo).append("|").toString();
/* 112 */     devInfo = new StringBuilder().append(devInfo).append(network).toString();
/* 113 */     devInfo = new StringBuilder().append(devInfo).append("|").toString();
/* 114 */     devInfo = new StringBuilder().append(devInfo).append(MCCMNC).toString();
/* 115 */     devInfo = new StringBuilder().append(devInfo).append("|").toString();
/* 116 */     devInfo = new StringBuilder().append(devInfo).append(context.getPackageName()).toString();
/* 117 */     devInfo = devInfo.replace("-", "_");
/*     */ 
/* 119 */     Log.i("DeviceUtils", new StringBuilder().append("getPhoneInformation.the phone information is: ").append(devInfo).toString());
/*     */ 
/* 121 */     return devInfo;
/*     */   }
/*     */ 
/*     */   public static String getDeviceIMSI(Context context) {
/* 125 */     SharedPreferences sp = context.getSharedPreferences("Statistics", 0);
/* 126 */     String imsi = sp.getString("IMSI", "");
/*     */ 
/* 128 */     if (TextUtils.isEmpty(imsi)) {
/*     */       try {
/* 130 */         TelephonyManager tm = (TelephonyManager)context.getSystemService("phone");
/* 131 */         imsi = tm.getSubscriberId();
/*     */       } catch (SecurityException e) {
/* 133 */         Log.e("DeviceUtils", "SecurityException!!!");
/*     */       }
/* 135 */       Log.i("DeviceUtils", new StringBuilder().append("IMSI is: ").append(imsi).toString());
/* 136 */       if (!TextUtils.isEmpty(imsi)) {
/* 137 */         SharedPreferences.Editor editor = sp.edit();
/* 138 */         editor.putString("IMSI", imsi);
/* 139 */         editor.apply();
/*     */       }
/*     */     }
/* 142 */     return imsi;
/*     */   }
/*     */ 
/*     */   public static String getDeviceManufacturer()
/*     */   {
/* 147 */     String line = "";
/* 148 */     BufferedReader input = null;
/* 149 */     String propName = "ro.miui.ui.version.name";
/*     */     try {
/* 151 */       Process p = Runtime.getRuntime().exec(new StringBuilder().append("getprop ").append(propName).toString());
/* 152 */       input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
/* 153 */       line = input.readLine();
/* 154 */       input.close();
/*     */     } catch (IOException e) {
/* 156 */       Log.e("DeviceUtils", new StringBuilder().append("Unable to read sysprop ").append(propName).toString());
/*     */     } finally {
/* 158 */       if (input != null)
/*     */         try {
/* 160 */           input.close();
/*     */         }
/*     */         catch (IOException e) {
/*     */         }
/*     */     }
/* 165 */     if (TextUtils.isEmpty(line)) {
/* 166 */       String manufacturer = Build.MANUFACTURER.replace("-", "_");
/* 167 */       return manufacturer;
/*     */     }
/* 169 */     return "Xiaomi";
/*     */   }
/*     */ 
/*     */   public static String getWifiMacAddress(Context context)
/*     */   {
/* 174 */     SharedPreferences sp = context.getSharedPreferences("Statistics", 0);
/* 175 */     String macAddr = sp.getString("Mac", "");
/* 176 */     if (TextUtils.isEmpty(macAddr)) {
/*     */       try {
/* 178 */         WifiManager wifiManager = (WifiManager)context.getSystemService("wifi");
/* 179 */         macAddr = wifiManager.getConnectionInfo().getMacAddress();
/*     */       } catch (SecurityException e) {
/* 181 */         Log.e("DeviceUtils", "SecurityException!!!");
/*     */       }
/* 183 */       Log.i("DeviceUtils", new StringBuilder().append("MAC is: ").append(macAddr).toString());
/* 184 */       if (!TextUtils.isEmpty(macAddr)) {
/* 185 */         SharedPreferences.Editor editor = sp.edit();
/* 186 */         editor.putString("Mac", macAddr);
/* 187 */         editor.apply();
/*     */       }
/*     */     }
/* 190 */     return macAddr;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.common.DeviceUtils
 * JD-Core Version:    0.6.0
 */
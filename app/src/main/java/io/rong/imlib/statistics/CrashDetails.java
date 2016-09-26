/*     */ package io.rong.imlib.statistics;
/*     */ 
/*     */ import android.app.ActivityManager;
/*     */ import android.app.ActivityManager.MemoryInfo;
/*     */ import android.content.Context;
/*     */ import android.content.Intent;
/*     */ import android.content.IntentFilter;
/*     */ import android.content.pm.FeatureInfo;
/*     */ import android.content.pm.PackageManager;
/*     */ import android.content.res.Configuration;
/*     */ import android.content.res.Resources;
/*     */ import android.media.AudioManager;
/*     */ import android.net.ConnectivityManager;
/*     */ import android.net.NetworkInfo;
/*     */ import android.os.Build;
/*     */ import android.os.Build.VERSION;
/*     */ import android.os.Environment;
/*     */ import android.os.StatFs;
/*     */ import android.util.Log;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ class CrashDetails
/*     */ {
/*  58 */   private static ArrayList<String> logs = new ArrayList();
/*  59 */   private static int startTime = Statistics.currentTimestamp();
/*  60 */   private static Map<String, String> customSegments = null;
/*  61 */   private static boolean inBackground = true;
/*  62 */   private static long totalMemory = 0L;
/*     */ 
/*     */   private static long getTotalRAM() {
/*  65 */     if (totalMemory == 0L) {
/*  66 */       RandomAccessFile reader = null;
/*  67 */       String load = null;
/*     */       try {
/*  69 */         reader = new RandomAccessFile("/proc/meminfo", "r");
/*  70 */         load = reader.readLine();
/*     */ 
/*  73 */         Pattern p = Pattern.compile("(\\d+)");
/*  74 */         Matcher m = p.matcher(load);
/*  75 */         String value = "";
/*  76 */         while (m.find()) {
/*  77 */           value = m.group(1);
/*     */         }
/*  79 */         reader.close();
/*     */ 
/*  81 */         totalMemory = Long.parseLong(value) / 1024L;
/*     */       } catch (IOException ex) {
/*  83 */         ex.printStackTrace();
/*     */       }
/*     */     }
/*  86 */     return totalMemory;
/*     */   }
/*     */ 
/*     */   static void inForeground()
/*     */   {
/*  93 */     inBackground = false;
/*     */   }
/*     */ 
/*     */   static void inBackground()
/*     */   {
/* 100 */     inBackground = true;
/*     */   }
/*     */ 
/*     */   static String isInBackground()
/*     */   {
/* 107 */     return Boolean.toString(inBackground);
/*     */   }
/*     */ 
/*     */   static void addLog(String record)
/*     */   {
/* 114 */     logs.add(record);
/*     */   }
/*     */ 
/*     */   static String getLogs()
/*     */   {
/* 121 */     String allLogs = "";
/*     */ 
/* 123 */     for (String s : logs)
/*     */     {
/* 125 */       allLogs = allLogs + s + "\n";
/*     */     }
/* 127 */     logs.clear();
/* 128 */     return allLogs;
/*     */   }
/*     */ 
/*     */   static void setCustomSegments(Map<String, String> segments)
/*     */   {
/* 136 */     customSegments = new HashMap();
/* 137 */     customSegments.putAll(segments);
/*     */   }
/*     */ 
/*     */   static JSONObject getCustomSegments()
/*     */   {
/* 144 */     if ((customSegments != null) && (!customSegments.isEmpty())) {
/* 145 */       return new JSONObject(customSegments);
/*     */     }
/* 147 */     return null;
/*     */   }
/*     */ 
/*     */   static String getManufacturer()
/*     */   {
/* 155 */     return Build.MANUFACTURER;
/*     */   }
/*     */ 
/*     */   static String getCpu()
/*     */   {
/* 162 */     if (Build.VERSION.SDK_INT < 21) {
/* 163 */       return Build.CPU_ABI;
/*     */     }
/* 165 */     return Build.SUPPORTED_ABIS[0];
/*     */   }
/*     */ 
/*     */   static String getOpenGL(Context context)
/*     */   {
/* 172 */     PackageManager packageManager = context.getPackageManager();
/* 173 */     FeatureInfo[] featureInfos = packageManager.getSystemAvailableFeatures();
/* 174 */     if ((featureInfos != null) && (featureInfos.length > 0)) {
/* 175 */       for (FeatureInfo featureInfo : featureInfos)
/*     */       {
/* 177 */         if (featureInfo.name == null) {
/* 178 */           if (featureInfo.reqGlEsVersion != 0) {
/* 179 */             return Integer.toString((featureInfo.reqGlEsVersion & 0xFFFF0000) >> 16);
/*     */           }
/* 181 */           return "1";
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 186 */     return "1";
/*     */   }
/*     */ 
/*     */   static String getRamCurrent(Context context)
/*     */   {
/* 193 */     ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
/* 194 */     ActivityManager activityManager = (ActivityManager)context.getSystemService("activity");
/* 195 */     activityManager.getMemoryInfo(mi);
/* 196 */     return Long.toString(getTotalRAM() - mi.availMem / 1048576L);
/*     */   }
/*     */ 
/*     */   static String getRamTotal(Context context)
/*     */   {
/* 203 */     return Long.toString(getTotalRAM());
/*     */   }
/*     */ 
/*     */   static String getDiskCurrent()
/*     */   {
/* 210 */     if (Build.VERSION.SDK_INT < 18) {
/* 211 */       StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
/* 212 */       long total = statFs.getBlockCount() * statFs.getBlockSize();
/* 213 */       long free = statFs.getAvailableBlocks() * statFs.getBlockSize();
/* 214 */       return Long.toString((total - free) / 1048576L);
/*     */     }
/*     */ 
/* 217 */     StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
/* 218 */     long total = statFs.getBlockCountLong() * statFs.getBlockSizeLong();
/* 219 */     long free = statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
/* 220 */     return Long.toString((total - free) / 1048576L);
/*     */   }
/*     */ 
/*     */   static String getDiskTotal()
/*     */   {
/* 228 */     if (Build.VERSION.SDK_INT < 18) {
/* 229 */       StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
/* 230 */       long total = statFs.getBlockCount() * statFs.getBlockSize();
/* 231 */       return Long.toString(total / 1048576L);
/*     */     }
/*     */ 
/* 234 */     StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
/* 235 */     long total = statFs.getBlockCountLong() * statFs.getBlockSizeLong();
/* 236 */     return Long.toString(total / 1048576L);
/*     */   }
/*     */ 
/*     */   static String getBatteryLevel(Context context)
/*     */   {
/*     */     try
/*     */     {
/* 245 */       Intent batteryIntent = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
/* 246 */       int level = batteryIntent.getIntExtra("level", -1);
/* 247 */       int scale = batteryIntent.getIntExtra("scale", -1);
/*     */ 
/* 250 */       if ((level > -1) && (scale > 0))
/* 251 */         return Float.toString(level / scale * 100.0F);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 255 */       if (Statistics.sharedInstance().isLoggingEnabled()) {
/* 256 */         Log.i("Statistics", "Can't get batter level");
/*     */       }
/*     */     }
/*     */ 
/* 260 */     return null;
/*     */   }
/*     */ 
/*     */   static String getRunningTime()
/*     */   {
/* 267 */     return Integer.toString(Statistics.currentTimestamp() - startTime);
/*     */   }
/*     */ 
/*     */   static String getOrientation(Context context)
/*     */   {
/* 274 */     int orientation = context.getResources().getConfiguration().orientation;
/* 275 */     switch (orientation)
/*     */     {
/*     */     case 2:
/* 278 */       return "Landscape";
/*     */     case 1:
/* 280 */       return "Portrait";
/*     */     case 3:
/* 282 */       return "Square";
/*     */     case 0:
/* 284 */       return "Unknown";
/*     */     }
/* 286 */     return null;
/*     */   }
/*     */ 
/*     */   static String isRooted()
/*     */   {
/* 294 */     String[] paths = { "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su" };
/*     */ 
/* 297 */     for (String path : paths) {
/* 298 */       if (new File(path).exists()) return "true";
/*     */     }
/* 300 */     return "false";
/*     */   }
/*     */ 
/*     */   static String isOnline(Context context)
/*     */   {
/*     */     try
/*     */     {
/* 308 */       ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService("connectivity");
/* 309 */       if ((conMgr != null) && (conMgr.getActiveNetworkInfo() != null) && (conMgr.getActiveNetworkInfo().isAvailable()) && (conMgr.getActiveNetworkInfo().isConnected()))
/*     */       {
/* 313 */         return "true";
/*     */       }
/* 315 */       return "false";
/*     */     }
/*     */     catch (Exception e) {
/* 318 */       if (Statistics.sharedInstance().isLoggingEnabled()) {
/* 319 */         Log.w("Statistics", "Got exception determining connectivity", e);
/*     */       }
/*     */     }
/* 322 */     return null;
/*     */   }
/*     */ 
/*     */   static String isMuted(Context context)
/*     */   {
/* 329 */     AudioManager audio = (AudioManager)context.getSystemService("audio");
/* 330 */     switch (audio.getRingerMode()) {
/*     */     case 0:
/* 332 */       return "true";
/*     */     case 1:
/* 334 */       return "true";
/*     */     }
/* 336 */     return "false";
/*     */   }
/*     */ 
/*     */   static String getCrashData(Context context, String error, Boolean nonfatal)
/*     */   {
/* 346 */     JSONObject json = new JSONObject();
/*     */ 
/* 348 */     fillJSONIfValuesNotEmpty(json, new String[] { "_error", error, "_nonfatal", Boolean.toString(nonfatal.booleanValue()), "_logs", getLogs(), "_device", DeviceInfo.getDevice(), "_os", DeviceInfo.getOS(), "_os_version", DeviceInfo.getOSVersion(), "_resolution", DeviceInfo.getResolution(context), "_app_version", DeviceInfo.getAppVersion(context), "_manufacture", getManufacturer(), "_cpu", getCpu(), "_opengl", getOpenGL(context), "_ram_current", getRamCurrent(context), "_ram_total", getRamTotal(context), "_disk_current", getDiskCurrent(), "_disk_total", getDiskTotal(), "_bat", getBatteryLevel(context), "_run", getRunningTime(), "_orientation", getOrientation(context), "_root", isRooted(), "_online", isOnline(context), "_muted", isMuted(context), "_background", isInBackground() });
/*     */     try
/*     */     {
/* 374 */       json.put("_custom", getCustomSegments());
/*     */     }
/*     */     catch (JSONException e) {
/*     */     }
/* 378 */     String result = json.toString();
/*     */     try
/*     */     {
/* 381 */       result = URLEncoder.encode(result, "UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException ignored)
/*     */     {
/*     */     }
/* 386 */     return result;
/*     */   }
/*     */ 
/*     */   static void fillJSONIfValuesNotEmpty(JSONObject json, String[] objects)
/*     */   {
/*     */     try
/*     */     {
/* 397 */       if ((objects.length > 0) && (objects.length % 2 == 0))
/* 398 */         for (int i = 0; i < objects.length; i += 2) {
/* 399 */           String key = objects[i];
/* 400 */           String value = objects[(i + 1)];
/* 401 */           if ((value != null) && (value.length() > 0))
/* 402 */             json.put(key, value);
/*     */         }
/*     */     }
/*     */     catch (JSONException ignored)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.statistics.CrashDetails
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.common;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.SharedPreferences;
/*     */ import android.content.SharedPreferences.Editor;
/*     */ import android.os.Environment;
/*     */ import android.os.Process;
/*     */ import android.text.TextUtils;
/*     */ import android.util.Log;
/*     */ import io.rong.imlib.NativeClient;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.UUID;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipOutputStream;
/*     */ 
/*     */ public class RFLog
/*     */ {
/*     */   private static final String TAG = "RFLog";
/*     */   private static int mode;
/*     */   private static String LOG_FILE;
/*     */   private static String ZIP_FILE;
/*     */   private static final int BUFF_SIZE = 1048576;
/*     */ 
/*     */   public static void uploadIfNeed(Context context)
/*     */   {
/*  41 */     context = context.getApplicationContext();
/*  42 */     LOG_FILE = getCachePath(context, "ronglog") + "/RongLog.log";
/*  43 */     ZIP_FILE = getCachePath(context, "ronglog") + "/RongLog.zip";
/*  44 */     SharedPreferences sp = context.getSharedPreferences("Log2File", 0);
/*  45 */     if (sp == null) {
/*  46 */       return;
/*     */     }
/*     */ 
/*  49 */     mode = sp.getInt("LOG_MODE", 0);
/*  50 */     long start = sp.getLong("LOG_START", 0L);
/*  51 */     long now = System.currentTimeMillis();
/*  52 */     if (now - start > 259200000L) {
/*  53 */       RLog.d("RFLog", "Stop write log");
/*  54 */       mode = 0;
/*  55 */       NativeClient.getInstance().setLogStatus(false);
/*  56 */       return;
/*     */     }
/*     */ 
/*  59 */     if (mode > 0) {
/*  60 */       RLog.d("RFLog", "Start write log");
/*  61 */       NativeClient.getInstance().setLogStatus(true);
/*  62 */       SharedPreferences.Editor editor = sp.edit();
/*  63 */       long last = sp.getLong("LOG_TIME", 0L);
/*  64 */       if (last == 0L) {
/*  65 */         editor.putLong("LOG_TIME", now).apply();
/*  66 */         return;
/*     */       }
/*  68 */       if (now - last > 7200000L) {
/*  69 */         editor.putLong("LOG_TIME", now).apply();
/*  70 */         File file = new File(LOG_FILE);
/*  71 */         if (file.exists())
/*  72 */           new UploadFile(null).start();
/*     */       }
/*     */     }
/*     */     else {
/*  76 */       RLog.d("RFLog", "Stop write log");
/*  77 */       NativeClient.getInstance().setLogStatus(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setMode(Context context, int mode) {
/*  82 */     RLog.d("RFLog", "setMode = " + mode);
/*  83 */     SharedPreferences sp = context.getSharedPreferences("Log2File", 0);
/*  84 */     if (sp != null) {
/*  85 */       SharedPreferences.Editor editor = sp.edit();
/*  86 */       int pre = sp.getInt("LOG_MODE", -1);
/*  87 */       if (mode != pre) {
/*  88 */         long now = System.currentTimeMillis();
/*  89 */         editor.putLong("LOG_START", now);
/*  90 */         editor.putInt("LOG_MODE", mode);
/*  91 */         editor.apply();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void write(String tag, String msg) {
/*  97 */     if (mode == 0) {
/*  98 */       return;
/*     */     }
/*     */ 
/* 101 */     File file = new File(LOG_FILE);
/* 102 */     String datetime = new SimpleDateFormat("MM-dd HH:mm:ss.SS", Locale.getDefault()).format(new Date());
/* 103 */     String message = datetime + " " + Process.myPid() + " " + tag + ": " + msg;
/*     */     try {
/* 105 */       FileWriter filerWriter = new FileWriter(file, true);
/* 106 */       BufferedWriter bufWriter = new BufferedWriter(filerWriter);
/* 107 */       bufWriter.write(message);
/* 108 */       bufWriter.newLine();
/* 109 */       bufWriter.close();
/* 110 */       filerWriter.close();
/*     */     } catch (IOException e) {
/* 112 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void write(String tag, Exception e) {
/* 117 */     if (mode == 0) {
/* 118 */       return;
/*     */     }
/*     */ 
/* 121 */     String expStr = null;
/*     */     try {
/* 123 */       StringWriter sw = new StringWriter();
/* 124 */       PrintWriter pw = new PrintWriter(sw);
/* 125 */       e.printStackTrace(pw);
/* 126 */       expStr = "\r\n" + sw.toString() + "\r\n";
/*     */     } catch (Exception ee) {
/* 128 */       ee.printStackTrace();
/*     */     }
/*     */ 
/* 131 */     if (!TextUtils.isEmpty(expStr))
/* 132 */       write(tag, expStr);
/*     */   }
/*     */ 
/*     */   public static void write(String tag, String msg, int level)
/*     */   {
/* 137 */     if ((mode == 0) || ((mode == 1) && (level < 6))) {
/* 138 */       return;
/*     */     }
/* 140 */     write(tag, msg);
/*     */   }
/*     */ 
/*     */   private static String getCachePath(Context context, String dir)
/*     */   {
/* 152 */     boolean sdCardExist = Environment.getExternalStorageState().equals("mounted");
/* 153 */     File cacheDir = context.getExternalCacheDir();
/* 154 */     if ((!sdCardExist) || (cacheDir == null)) {
/* 155 */       cacheDir = context.getCacheDir();
/*     */     }
/*     */ 
/* 158 */     if (TextUtils.isEmpty(dir)) {
/* 159 */       return cacheDir.getPath();
/*     */     }
/*     */ 
/* 162 */     File tarDir = new File(cacheDir.getPath() + "/" + dir);
/* 163 */     if ((tarDir.exists()) || (tarDir.mkdir())) {
/* 164 */       return tarDir.getPath();
/*     */     }
/* 166 */     return context.getCacheDir().getPath();
/*     */   }
/*     */ 
/*     */   private static class UploadFile extends Thread
/*     */   {
/*     */     public void run()
/*     */     {
/* 173 */       DataOutputStream outStream = null;
/* 174 */       InputStream is = null;
/* 175 */       File logFile = null;
/* 176 */       File zipFile = null;
/*     */       try {
/* 178 */         logFile = new File(RFLog.LOG_FILE);
/* 179 */         if (!logFile.exists()) {
/*     */           return;
/*     */         }
/* 183 */         if (!zipLogFile()) { Log.d("RFLog", "UploadFile log file == null.");
/*     */           return;
/*     */         }
/* 188 */         String boundary = UUID.randomUUID().toString();
/* 189 */         URL uri = new URL("http://feedback.cn.ronghub.com");
/* 190 */         HttpURLConnection conn = (HttpURLConnection)uri.openConnection();
/* 191 */         conn.setReadTimeout(5000);
/* 192 */         conn.setDoInput(true);
/* 193 */         conn.setDoOutput(true);
/* 194 */         conn.setUseCaches(false);
/* 195 */         conn.setRequestMethod("POST");
/* 196 */         conn.setRequestProperty("RC-App-Key", NativeClient.getInstance().getAppKey());
/* 197 */         conn.setRequestProperty("RC-User-Token", NativeClient.getInstance().getToken());
/* 198 */         conn.setRequestProperty("Connection", "keep-alive");
/* 199 */         conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
/*     */ 
/* 201 */         outStream = new DataOutputStream(conn.getOutputStream());
/* 202 */         outStream.writeBytes("--" + boundary + "\r\n");
/* 203 */         outStream.writeBytes("Content-Disposition: form-data; name=\"fileLog\"; filename=\"" + NativeClient.getInstance().getDeviceId() + "_" + Long.toString(System.currentTimeMillis()) + ".zip\"\r\n");
/*     */ 
/* 205 */         outStream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
/*     */ 
/* 207 */         zipFile = new File(RFLog.ZIP_FILE);
/* 208 */         is = new FileInputStream(zipFile);
/* 209 */         byte[] buffer = new byte[1024];
/*     */         int len;
/* 211 */         while ((len = is.read(buffer)) != -1) {
/* 212 */           outStream.write(buffer, 0, len);
/*     */         }
/*     */ 
/* 215 */         outStream.writeBytes("\r\n--" + boundary + "--\r\n");
/* 216 */         outStream.flush();
/* 217 */         Log.d("RFLog", "UploadFile log end. code = " + conn.getResponseCode());
/*     */       } catch (Exception e) {
/* 219 */         e.printStackTrace();
/* 220 */         Log.e("RFLog", "UploadFile log failed.");
/*     */       } finally {
/* 222 */         if (is != null) {
/*     */           try {
/* 224 */             is.close();
/*     */           } catch (IOException e) {
/* 226 */             e.printStackTrace();
/*     */           }
/*     */         }
/* 229 */         if (outStream != null) {
/*     */           try {
/* 231 */             outStream.close();
/*     */           } catch (IOException e) {
/* 233 */             e.printStackTrace();
/*     */           }
/*     */         }
/* 236 */         if (zipFile != null) {
/* 237 */           zipFile.delete();
/*     */         }
/* 239 */         if (logFile != null)
/* 240 */           logFile.delete();
/*     */       }
/*     */     }
/*     */ 
/*     */     private static boolean zipLogFile() throws IOException
/*     */     {
/* 246 */       FileInputStream fi = new FileInputStream(RFLog.LOG_FILE);
/* 247 */       BufferedInputStream origin = new BufferedInputStream(fi, 1048576);
/* 248 */       FileOutputStream dest = new FileOutputStream(RFLog.ZIP_FILE);
/* 249 */       ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
/*     */       try {
/* 251 */         byte[] data = new byte[1048576];
/* 252 */         out.putNextEntry(new ZipEntry(RFLog.LOG_FILE.substring(RFLog.LOG_FILE.lastIndexOf("/") + 1)));
/*     */ 
/* 254 */         while ((count = origin.read(data, 0, 1048576)) != -1)
/* 255 */           out.write(data, 0, count);
/*     */       }
/*     */       catch (IOException e) {
/* 258 */         e.printStackTrace();
/* 259 */         int count = 0;
/*     */         return count;
/*     */       }
/*     */       finally
/*     */       {
/* 261 */         origin.close();
/* 262 */         out.close();
/*     */       }
/* 264 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.common.RFLog
 * JD-Core Version:    0.6.0
 */
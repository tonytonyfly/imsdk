/*     */ package io.rong.push.common;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.Intent;
/*     */ import android.net.Uri;
/*     */ import android.os.Process;
/*     */ import android.util.Log;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ 
/*     */ public class RLog
/*     */ {
/*     */   static final boolean DEBUG = true;
/*  20 */   private static String RongLog = "RongLog-Push";
/*  21 */   private static Boolean IS_WRITE_TO_FILE = Boolean.valueOf(true);
/*  22 */   private static String LOG_PATH = "/sdcard/";
/*  23 */   private static int LOG_FILE_SAVE_DAYS = 1;
/*  24 */   private static String FILE_NAME = "PushLog.txt";
/*  25 */   private static SimpleDateFormat logFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/*     */ 
/*  27 */   private static SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyy-MM-dd");
/*     */ 
/*     */   public static void i(String tag, String msg) {
/*  30 */     log(tag, msg, 'i');
/*     */   }
/*     */ 
/*     */   public static void v(String tag, String msg) {
/*  34 */     log(tag, msg, 'v');
/*     */   }
/*     */ 
/*     */   public static void d(String tag, String msg) {
/*  38 */     log(tag, msg, 'd');
/*     */   }
/*     */ 
/*     */   public static void e(String tag, String msg) {
/*  42 */     log(tag, msg, 'e');
/*     */   }
/*     */ 
/*     */   private static void log(String tag, String msg, char level) {
/*  46 */     tag = RongLog + "[" + tag + "]";
/*     */ 
/*  48 */     if ('e' == level)
/*  49 */       Log.e(tag, msg);
/*  50 */     else if ('w' == level)
/*  51 */       Log.w(tag, msg);
/*  52 */     else if ('d' == level)
/*  53 */       Log.d(tag, msg);
/*  54 */     else if ('i' == level)
/*  55 */       Log.i(tag, msg);
/*     */     else
/*  57 */       Log.v(tag, msg);
/*     */   }
/*     */ 
/*     */   private static void writeLogtoFile(String mylogtype, String tag, String text)
/*     */   {
/*  70 */     Date nowtime = new Date();
/*  71 */     String fileDate = fileNameFormat.format(nowtime);
/*  72 */     String needWriteMessage = logFormat.format(nowtime) + "  " + mylogtype + "  " + Process.myPid() + "    " + tag + "    " + text;
/*     */ 
/*  74 */     File file = new File(LOG_PATH, fileDate + FILE_NAME);
/*     */ 
/*  76 */     delFile();
/*     */     try {
/*  78 */       FileWriter filerWriter = new FileWriter(file, true);
/*  79 */       BufferedWriter bufWriter = new BufferedWriter(filerWriter);
/*  80 */       bufWriter.write(needWriteMessage);
/*  81 */       bufWriter.newLine();
/*  82 */       bufWriter.close();
/*  83 */       filerWriter.close();
/*     */     }
/*     */     catch (IOException e) {
/*  86 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void delFile()
/*     */   {
/*  94 */     String needDelFiel = fileNameFormat.format(getDateBefore());
/*  95 */     File file = new File(LOG_PATH, needDelFiel + FILE_NAME);
/*  96 */     if (file.exists())
/*  97 */       file.delete();
/*     */   }
/*     */ 
/*     */   private static Date getDateBefore()
/*     */   {
/* 105 */     Date nowtime = new Date();
/* 106 */     Calendar now = Calendar.getInstance();
/* 107 */     now.setTime(nowtime);
/* 108 */     now.set(5, now.get(5) - LOG_FILE_SAVE_DAYS);
/*     */ 
/* 110 */     return now.getTime();
/*     */   }
/*     */ 
/*     */   public static void sendLog(Context context, List<String> emails) {
/* 114 */     Uri uri = null;
/*     */     try {
/* 116 */       Date nowtime = new Date();
/* 117 */       String fileDate = fileNameFormat.format(nowtime);
/* 118 */       File file = new File(LOG_PATH, fileDate + FILE_NAME);
/*     */ 
/* 121 */       uri = Uri.fromFile(file);
/*     */     } catch (Exception e) {
/* 123 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 126 */     if (uri != null) {
/* 127 */       Intent intent = new Intent("android.intent.action.SEND");
/* 128 */       intent.setType("message/rfc822");
/* 129 */       intent.putExtra("android.intent.extra.EMAIL", emails.toArray());
/* 130 */       intent.putExtra("android.intent.extra.SUBJECT", "RongCloud log");
/* 131 */       intent.putExtra("android.intent.extra.STREAM", uri);
/* 132 */       context.startActivity(intent);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.common.RLog
 * JD-Core Version:    0.6.0
 */
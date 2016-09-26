/*     */ package io.rong.imkit.utils;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.SharedPreferences;
/*     */ import android.content.SharedPreferences.Editor;
/*     */ import android.content.res.Resources;
/*     */ import android.os.Environment;
/*     */ import android.util.DisplayMetrics;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.model.ConversationInfo;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class CommonUtils
/*     */ {
/*     */   public static void saveNotificationQuietHours(Context mContext, String startTime, int spanMinutes)
/*     */   {
/*  35 */     SharedPreferences mPreferences = null;
/*     */ 
/*  37 */     if (mContext != null) {
/*  38 */       mPreferences = mContext.getSharedPreferences("RONG_SDK", 0);
/*     */     }
/*  40 */     if (mPreferences != null) {
/*  41 */       SharedPreferences.Editor editor = mPreferences.edit();
/*  42 */       editor.putString("QUIET_HOURS_START_TIME", startTime);
/*  43 */       editor.putInt("QUIET_HOURS_SPAN_MINUTES", spanMinutes);
/*  44 */       editor.commit();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getNotificationQuietHoursForStartTime(Context mContext)
/*     */   {
/*  54 */     SharedPreferences mPreferences = null;
/*     */ 
/*  56 */     if ((mPreferences == null) && (mContext != null)) {
/*  57 */       mPreferences = mContext.getSharedPreferences("RONG_SDK", 0);
/*     */     }
/*  59 */     if (mPreferences != null) {
/*  60 */       return mPreferences.getString("QUIET_HOURS_START_TIME", "");
/*     */     }
/*     */ 
/*  63 */     return "";
/*     */   }
/*     */ 
/*     */   public static int getNotificationQuietHoursForSpanMinutes(Context mContext)
/*     */   {
/*  72 */     SharedPreferences mPreferences = null;
/*     */ 
/*  74 */     if ((mPreferences == null) && (mContext != null)) {
/*  75 */       mPreferences = mContext.getSharedPreferences("RONG_SDK", 0);
/*     */     }
/*  77 */     if (mPreferences != null) {
/*  78 */       return mPreferences.getInt("QUIET_HOURS_SPAN_MINUTES", 0);
/*     */     }
/*     */ 
/*  81 */     return 0;
/*     */   }
/*     */ 
/*     */   public static void refreshUserInfoIfNeed(RongContext context, UserInfo userInfo)
/*     */   {
/*  86 */     if (userInfo == null) {
/*  87 */       return;
/*     */     }
/*  89 */     RongUserInfoManager.getInstance().setUserInfo(userInfo);
/*     */   }
/*     */ 
/*     */   public static boolean isInConversationPager(String id, Conversation.ConversationType type) {
/*  93 */     List list = RongContext.getInstance().getCurrentConversationList();
/*     */ 
/*  95 */     Iterator i$ = list.iterator(); if (i$.hasNext()) { ConversationInfo conversationInfo = (ConversationInfo)i$.next();
/*  96 */       return (id.equals(conversationInfo.getTargetId())) && (type == conversationInfo.getConversationType());
/*     */     }
/*  98 */     return false;
/*     */   }
/*     */ 
/*     */   public static int dip2px(Context context, float dpValue)
/*     */   {
/* 105 */     float scale = context.getResources().getDisplayMetrics().density;
/* 106 */     return (int)(dpValue * scale + 0.5F);
/*     */   }
/*     */ 
/*     */   public static String md5(Object object)
/*     */   {
/*     */     byte[] hash;
/*     */     try
/*     */     {
/* 115 */       hash = MessageDigest.getInstance("MD5").digest(toByteArray(object));
/*     */     } catch (NoSuchAlgorithmException e) {
/* 117 */       throw new RuntimeException("Huh, MD5 should be supported?", e);
/*     */     }
/*     */ 
/* 120 */     StringBuilder hex = new StringBuilder(hash.length * 2);
/* 121 */     for (byte b : hash) {
/* 122 */       if ((b & 0xFF) < 16) hex.append("0");
/* 123 */       hex.append(Integer.toHexString(b & 0xFF));
/*     */     }
/* 125 */     return hex.toString();
/*     */   }
/*     */ 
/*     */   public static byte[] toByteArray(Object obj) {
/* 129 */     byte[] bytes = null;
/* 130 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*     */     try {
/* 132 */       ObjectOutputStream oos = new ObjectOutputStream(bos);
/* 133 */       oos.writeObject(obj);
/* 134 */       oos.flush();
/* 135 */       bytes = bos.toByteArray();
/* 136 */       oos.close();
/* 137 */       bos.close();
/*     */     } catch (IOException ex) {
/* 139 */       ex.printStackTrace();
/*     */     }
/* 141 */     return bytes;
/*     */   }
/*     */ 
/*     */   public static String getDataPath(Context context)
/*     */   {
/*     */     String path;
/*     */     String path;
/* 150 */     if (isExistSDcard())
/* 151 */       path = new StringBuilder().append(Environment.getExternalStorageDirectory().getPath()).append("/").append(context.getPackageName()).append("/img_cache").toString();
/*     */     else
/* 153 */       path = new StringBuilder().append(context.getFilesDir().getPath()).append("/").append(context.getPackageName()).append("/img_cache").toString();
/* 154 */     if (!path.endsWith("/"))
/* 155 */       path = new StringBuilder().append(path).append("/").toString();
/* 156 */     return path;
/*     */   }
/*     */ 
/*     */   public static boolean isExistSDcard()
/*     */   {
/* 165 */     String state = Environment.getExternalStorageState();
/* 166 */     return state.equals("mounted");
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.utils.CommonUtils
 * JD-Core Version:    0.6.0
 */
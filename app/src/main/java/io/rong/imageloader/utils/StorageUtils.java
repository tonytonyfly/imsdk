/*     */ package io.rong.imageloader.utils;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.os.Environment;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public final class StorageUtils
/*     */ {
/*     */   private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
/*     */   private static final String INDIVIDUAL_DIR_NAME = "uil-images";
/*     */ 
/*     */   public static File getCacheDirectory(Context context)
/*     */   {
/*  52 */     return getCacheDirectory(context, true);
/*     */   }
/*     */   public static File getCacheDirectory(Context context, boolean preferExternal) {
/*  67 */     File appCacheDir = null;
/*     */     String externalStorageState;
/*     */     try {
/*  70 */       externalStorageState = Environment.getExternalStorageState();
/*     */     } catch (NullPointerException e) {
/*  72 */       externalStorageState = "";
/*     */     } catch (IncompatibleClassChangeError e) {
/*  74 */       externalStorageState = "";
/*     */     }
/*  76 */     if ((preferExternal) && ("mounted".equals(externalStorageState)) && (hasExternalStoragePermission(context))) {
/*  77 */       appCacheDir = getExternalCacheDir(context);
/*     */     }
/*  79 */     if (appCacheDir == null) {
/*  80 */       appCacheDir = context.getCacheDir();
/*     */     }
/*  82 */     if (appCacheDir == null) {
/*  83 */       String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
/*  84 */       L.w("Can't define system cache directory! '%s' will be used.", new Object[] { cacheDirPath });
/*  85 */       appCacheDir = new File(cacheDirPath);
/*     */     }
/*  87 */     return appCacheDir;
/*     */   }
/*     */ 
/*     */   public static File getIndividualCacheDirectory(Context context)
/*     */   {
/*  99 */     return getIndividualCacheDirectory(context, "uil-images");
/*     */   }
/*     */ 
/*     */   public static File getIndividualCacheDirectory(Context context, String cacheDir)
/*     */   {
/* 112 */     File appCacheDir = getCacheDirectory(context);
/* 113 */     File individualCacheDir = new File(appCacheDir, cacheDir);
/* 114 */     if ((!individualCacheDir.exists()) && 
/* 115 */       (!individualCacheDir.mkdir())) {
/* 116 */       individualCacheDir = appCacheDir;
/*     */     }
/*     */ 
/* 119 */     return individualCacheDir;
/*     */   }
/*     */ 
/*     */   public static File getOwnCacheDirectory(Context context, String cacheDir)
/*     */   {
/* 131 */     File appCacheDir = null;
/* 132 */     if (("mounted".equals(Environment.getExternalStorageState())) && (hasExternalStoragePermission(context))) {
/* 133 */       appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
/*     */     }
/* 135 */     if ((appCacheDir == null) || ((!appCacheDir.exists()) && (!appCacheDir.mkdirs()))) {
/* 136 */       appCacheDir = context.getCacheDir();
/*     */     }
/* 138 */     return appCacheDir;
/*     */   }
/*     */ 
/*     */   public static File getOwnCacheDirectory(Context context, String cacheDir, boolean preferExternal)
/*     */   {
/* 150 */     File appCacheDir = null;
/* 151 */     if ((preferExternal) && ("mounted".equals(Environment.getExternalStorageState())) && (hasExternalStoragePermission(context))) {
/* 152 */       appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
/*     */     }
/* 154 */     if ((appCacheDir == null) || ((!appCacheDir.exists()) && (!appCacheDir.mkdirs()))) {
/* 155 */       appCacheDir = context.getCacheDir();
/*     */     }
/* 157 */     return appCacheDir;
/*     */   }
/*     */ 
/*     */   private static File getExternalCacheDir(Context context) {
/* 161 */     File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
/* 162 */     File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
/* 163 */     if (!appCacheDir.exists()) {
/* 164 */       if (!appCacheDir.mkdirs()) {
/* 165 */         L.w("Unable to create external cache directory", new Object[0]);
/* 166 */         return null;
/*     */       }
/*     */       try {
/* 169 */         new File(appCacheDir, ".nomedia").createNewFile();
/*     */       } catch (IOException e) {
/* 171 */         L.i("Can't create \".nomedia\" file in application external cache directory", new Object[0]);
/*     */       }
/*     */     }
/* 174 */     return appCacheDir;
/*     */   }
/*     */ 
/*     */   private static boolean hasExternalStoragePermission(Context context) {
/* 178 */     int perm = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
/* 179 */     return perm == 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.utils.StorageUtils
 * JD-Core Version:    0.6.0
 */
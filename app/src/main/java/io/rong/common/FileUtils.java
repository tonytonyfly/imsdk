/*     */ package io.rong.common;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.res.Resources.NotFoundException;
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.Bitmap.CompressFormat;
/*     */ import android.net.Uri;
/*     */ import android.os.Environment;
/*     */ import android.support.annotation.NonNull;
/*     */ import io.rong.imlib.R.string;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public class FileUtils
/*     */ {
/*  23 */   private static String TAG = "FileUtils";
/*     */ 
/*     */   public static InputStream getFileInputStream(String path) {
/*  26 */     FileInputStream fileInputStream = null;
/*     */     try
/*     */     {
/*  29 */       fileInputStream = new FileInputStream(new File(path));
/*     */     } catch (FileNotFoundException e) {
/*  31 */       e.printStackTrace();
/*     */     }
/*  33 */     return fileInputStream;
/*     */   }
/*     */ 
/*     */   public static byte[] getByteFromUri(Uri uri) {
/*  37 */     InputStream input = getFileInputStream(uri.getPath());
/*     */     try {
/*  39 */       int count = 0;
/*  40 */       while (count == 0) {
/*  41 */         count = input.available();
/*  42 */         if (count == 0) {
/*  43 */           break;
/*     */         }
/*     */       }
/*  46 */       bytes = new byte[count];
/*  47 */       input.read(bytes);
/*     */ 
/*  49 */       byte[] arrayOfByte1 = bytes;
/*     */       return arrayOfByte1;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  51 */       byte[] bytes = null;
/*     */       return bytes;
/*     */     }
/*     */     finally
/*     */     {
/*  53 */       if (input != null)
/*     */         try {
/*  55 */           input.close(); } catch (IOException e) {
/*     */         }
/*     */     }
/*  57 */     throw localObject;
/*     */   }
/*     */ 
/*     */   public static void writeByte(Uri uri, byte[] data)
/*     */   {
/*  63 */     File fileFolder = new File(uri.getPath().substring(0, uri.getPath().lastIndexOf("/")));
/*  64 */     fileFolder.mkdirs();
/*  65 */     File file = new File(uri.getPath());
/*     */     try
/*     */     {
/*  68 */       OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
/*  69 */       os.write(data);
/*  70 */       os.close();
/*     */     }
/*     */     catch (IOException e) {
/*  73 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static File convertBitmap2File(Bitmap bm, String dir, String name) {
/*  78 */     File file = new File(dir);
/*  79 */     if (!file.exists()) {
/*  80 */       RLog.e(TAG, "convertBitmap2File: dir does not exist! -" + file.getAbsolutePath());
/*  81 */       file.mkdirs();
/*     */     }
/*  83 */     file = new File(file.getPath() + File.separator + name);
/*     */     try {
/*  85 */       BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
/*  86 */       bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
/*  87 */       bos.flush();
/*  88 */       bos.close();
/*     */     } catch (IOException e) {
/*  90 */       e.printStackTrace();
/*  91 */       RLog.e(TAG, "convertBitmap2File: Exception!");
/*     */     }
/*     */ 
/*  94 */     return file;
/*     */   }
/*     */ 
/*     */   public static File copyFile(File src, String path, String name) {
/*  98 */     File dest = null;
/*  99 */     if (!src.exists()) {
/* 100 */       RLog.e(TAG, "copyFile: src file does not exist! -" + src.getAbsolutePath());
/* 101 */       return dest;
/*     */     }
/*     */ 
/* 104 */     dest = new File(path);
/* 105 */     if (!dest.exists()) {
/* 106 */       RLog.d(TAG, "copyFile: dir does not exist!");
/* 107 */       dest.mkdirs();
/*     */     }
/* 109 */     dest = new File(path + name);
/*     */     try
/*     */     {
/* 112 */       FileInputStream fis = new FileInputStream(src);
/* 113 */       FileOutputStream fos = new FileOutputStream(dest);
/* 114 */       byte[] buffer = new byte[1024];
/*     */       int length;
/* 116 */       while ((length = fis.read(buffer)) != -1) {
/* 117 */         fos.write(buffer, 0, length);
/*     */       }
/* 119 */       fos.flush();
/* 120 */       fos.close();
/* 121 */       fis.close();
/*     */     } catch (IOException e) {
/* 123 */       e.printStackTrace();
/* 124 */       RLog.e(TAG, "copyFile: Exception!");
/* 125 */       return dest;
/*     */     }
/*     */ 
/* 128 */     return dest;
/*     */   }
/*     */ 
/*     */   public static byte[] file2byte(File file) {
/* 132 */     if (!file.exists()) {
/* 133 */       RLog.e(TAG, "file2byte: src file does not exist! -" + file.getAbsolutePath());
/* 134 */       return null;
/*     */     }
/*     */ 
/* 137 */     byte[] buffer = null;
/*     */     try {
/* 139 */       FileInputStream fis = new FileInputStream(file);
/* 140 */       ByteArrayOutputStream bos = new ByteArrayOutputStream();
/* 141 */       byte[] b = new byte[1024];
/*     */       int n;
/* 143 */       while ((n = fis.read(b)) != -1) {
/* 144 */         bos.write(b, 0, n);
/*     */       }
/* 146 */       fis.close();
/* 147 */       bos.close();
/* 148 */       buffer = bos.toByteArray();
/*     */     } catch (Exception e1) {
/* 150 */       e1.printStackTrace();
/* 151 */       RLog.e(TAG, "file2byte: Exception!");
/*     */     }
/* 153 */     return buffer;
/*     */   }
/*     */ 
/*     */   public static File byte2File(byte[] buf, String filePath, String fileName) {
/* 157 */     BufferedOutputStream bos = null;
/* 158 */     FileOutputStream fos = null;
/* 159 */     File file = null;
/*     */     try {
/* 161 */       File dir = new File(filePath);
/* 162 */       if (!dir.exists()) {
/* 163 */         RLog.d(TAG, "byte2File: dir does not exist!");
/* 164 */         dir.mkdirs();
/*     */       }
/* 166 */       file = new File(dir.getPath() + File.separator + fileName);
/* 167 */       fos = new FileOutputStream(file);
/* 168 */       bos = new BufferedOutputStream(fos);
/* 169 */       bos.write(buf);
/*     */     } catch (Exception e) {
/* 171 */       e.printStackTrace();
/* 172 */       RLog.e(TAG, "byte2File: Exception!");
/*     */     } finally {
/* 174 */       if (bos != null) {
/*     */         try {
/* 176 */           bos.close();
/*     */         } catch (IOException e) {
/* 178 */           e.printStackTrace();
/*     */         }
/*     */       }
/* 181 */       if (fos != null) {
/*     */         try {
/* 183 */           fos.close();
/*     */         } catch (IOException e) {
/* 185 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/* 189 */     return file;
/*     */   }
/*     */ 
/*     */   public static String getCachePath(Context context)
/*     */   {
/* 199 */     return getCachePath(context, "'");
/*     */   }
/*     */ 
/*     */   public static String getCachePath(Context context, @NonNull String dir)
/*     */   {
/* 210 */     boolean sdCardExist = Environment.getExternalStorageState().equals("mounted");
/* 211 */     File cacheDir = context.getExternalCacheDir();
/* 212 */     if ((!sdCardExist) || (cacheDir == null)) {
/* 213 */       cacheDir = context.getCacheDir();
/*     */     }
/*     */ 
/* 216 */     File tarDir = new File(cacheDir.getPath() + File.separator + dir);
/* 217 */     if (!tarDir.exists())
/*     */     {
/* 219 */       tarDir.mkdir();
/*     */     }
/* 221 */     return tarDir.getPath();
/*     */   }
/*     */ 
/*     */   public static String getMediaDownloadDir(Context context) {
/* 225 */     boolean sdCardExist = Environment.getExternalStorageState().equals("mounted");
/* 226 */     String path = "/sdcard";
/* 227 */     if (sdCardExist) {
/* 228 */       File file = Environment.getExternalStorageDirectory();
/* 229 */       path = file.getPath();
/*     */     }
/*     */     try {
/* 232 */       path = path + context.getString(R.string.rc_media_message_default_save_path);
/* 233 */       File file = new File(path);
/* 234 */       if ((!file.exists()) && (!file.mkdirs()))
/* 235 */         path = "/sdcard";
/*     */     }
/*     */     catch (Resources.NotFoundException e) {
/* 238 */       e.printStackTrace();
/* 239 */       path = "/sdcard";
/*     */     }
/*     */ 
/* 242 */     return path;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.common.FileUtils
 * JD-Core Version:    0.6.0
 */
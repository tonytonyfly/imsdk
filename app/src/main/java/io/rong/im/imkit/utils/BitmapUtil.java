/*     */ package io.rong.imkit.utils;
/*     */ 
/*     */ import android.content.ContentResolver;
/*     */ import android.content.Context;
/*     */ import android.database.Cursor;
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.Bitmap.CompressFormat;
/*     */ import android.graphics.BitmapFactory;
/*     */ import android.graphics.BitmapFactory.Options;
/*     */ import android.graphics.Matrix;
/*     */ import android.media.ExifInterface;
/*     */ import android.net.Uri;
/*     */ import android.text.TextUtils;
/*     */ import android.util.Base64;
/*     */ import android.util.Log;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class BitmapUtil
/*     */ {
/*     */   private static final String TAG = "Util";
/*     */ 
/*     */   public static String getBase64FromBitmap(Bitmap bitmap)
/*     */   {
/*  29 */     String base64Str = null;
/*  30 */     ByteArrayOutputStream baos = null;
/*     */     try
/*     */     {
/*  33 */       if (bitmap != null)
/*     */       {
/*  35 */         baos = new ByteArrayOutputStream();
/*  36 */         bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
/*     */ 
/*  38 */         byte[] bitmapBytes = baos.toByteArray();
/*  39 */         base64Str = Base64.encodeToString(bitmapBytes, 2);
/*     */ 
/*  41 */         baos.flush();
/*  42 */         baos.close();
/*     */       }
/*     */     } catch (IOException e) {
/*  45 */       e.printStackTrace();
/*     */     } finally {
/*     */       try {
/*  48 */         if (baos != null) {
/*  49 */           baos.flush();
/*  50 */           baos.close();
/*     */         }
/*     */       } catch (IOException e) {
/*  53 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */ 
/*  57 */     return base64Str;
/*     */   }
/*     */ 
/*     */   public static Bitmap getBitmapFromBase64(String base64Str)
/*     */   {
/*  63 */     if (TextUtils.isEmpty(base64Str)) {
/*  64 */       return null;
/*     */     }
/*     */ 
/*  67 */     byte[] bytes = Base64.decode(base64Str, 2);
/*  68 */     return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
/*     */   }
/*     */ 
/*     */   public static Bitmap getResizedBitmap(Context context, Uri uri, int widthLimit, int heightLimit) throws IOException
/*     */   {
/*  73 */     String path = null;
/*  74 */     Bitmap result = null;
/*     */ 
/*  76 */     if (uri.getScheme().equals("file")) {
/*  77 */       path = uri.getPath();
/*  78 */     } else if (uri.getScheme().equals("content")) {
/*  79 */       Cursor cursor = context.getContentResolver().query(uri, new String[] { "_data" }, null, null, null);
/*  80 */       cursor.moveToFirst();
/*  81 */       path = cursor.getString(0);
/*  82 */       cursor.close();
/*     */     } else {
/*  84 */       return null;
/*     */     }
/*     */ 
/*  87 */     ExifInterface exifInterface = new ExifInterface(path);
/*     */ 
/*  89 */     BitmapFactory.Options options = new BitmapFactory.Options();
/*  90 */     options.inJustDecodeBounds = true;
/*  91 */     BitmapFactory.decodeFile(path, options);
/*     */ 
/*  93 */     int orientation = exifInterface.getAttributeInt("Orientation", 0);
/*     */ 
/*  95 */     if ((orientation == 6) || (orientation == 8) || (orientation == 5) || (orientation == 7))
/*     */     {
/*  99 */       int tmp = widthLimit;
/* 100 */       widthLimit = heightLimit;
/* 101 */       heightLimit = tmp;
/*     */     }
/*     */ 
/* 104 */     int width = options.outWidth;
/* 105 */     int height = options.outHeight;
/* 106 */     int sampleW = 1; int sampleH = 1;
/* 107 */     while (width / 2 > widthLimit) {
/* 108 */       width /= 2;
/* 109 */       sampleW <<= 1;
/*     */     }
/*     */ 
/* 113 */     while (height / 2 > heightLimit) {
/* 114 */       height /= 2;
/* 115 */       sampleH <<= 1;
/*     */     }
/* 117 */     int sampleSize = 1;
/*     */ 
/* 119 */     options = new BitmapFactory.Options();
/* 120 */     if ((widthLimit == 2147483647) || (heightLimit == 2147483647))
/* 121 */       sampleSize = Math.max(sampleW, sampleH);
/*     */     else {
/* 123 */       sampleSize = Math.max(sampleW, sampleH);
/* 125 */     }options.inSampleSize = sampleSize;
/*     */     Bitmap bitmap;
/*     */     try {
/* 129 */       bitmap = BitmapFactory.decodeFile(path, options);
/*     */     } catch (OutOfMemoryError e) {
/* 131 */       e.printStackTrace();
/* 132 */       options.inSampleSize <<= 1;
/* 133 */       bitmap = BitmapFactory.decodeFile(path, options);
/*     */     }
/*     */ 
/* 136 */     Matrix matrix = new Matrix();
/* 137 */     if (bitmap == null) {
/* 138 */       return bitmap;
/*     */     }
/* 140 */     int w = bitmap.getWidth();
/* 141 */     int h = bitmap.getHeight();
/*     */ 
/* 143 */     if ((orientation == 6) || (orientation == 8) || (orientation == 5) || (orientation == 7))
/*     */     {
/* 147 */       int tmp = w;
/* 148 */       w = h;
/* 149 */       h = tmp;
/*     */     }
/* 151 */     switch (orientation) {
/*     */     case 6:
/* 153 */       matrix.setRotate(90.0F, w / 2.0F, h / 2.0F);
/* 154 */       break;
/*     */     case 3:
/* 156 */       matrix.setRotate(180.0F, w / 2.0F, h / 2.0F);
/* 157 */       break;
/*     */     case 8:
/* 159 */       matrix.setRotate(270.0F, w / 2.0F, h / 2.0F);
/* 160 */       break;
/*     */     case 2:
/* 162 */       matrix.preScale(-1.0F, 1.0F);
/* 163 */       break;
/*     */     case 4:
/* 165 */       matrix.preScale(1.0F, -1.0F);
/* 166 */       break;
/*     */     case 5:
/* 168 */       matrix.setRotate(90.0F, w / 2.0F, h / 2.0F);
/* 169 */       matrix.preScale(1.0F, -1.0F);
/* 170 */       break;
/*     */     case 7:
/* 172 */       matrix.setRotate(270.0F, w / 2.0F, h / 2.0F);
/* 173 */       matrix.preScale(1.0F, -1.0F);
/*     */     }
/*     */ 
/* 176 */     float xS = widthLimit / bitmap.getWidth();
/* 177 */     float yS = heightLimit / bitmap.getHeight();
/*     */ 
/* 179 */     matrix.postScale(Math.min(xS, yS), Math.min(xS, yS));
/*     */     try {
/* 181 */       result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
/*     */     } catch (OutOfMemoryError e) {
/* 183 */       e.printStackTrace();
/* 184 */       Log.e("ResourceCompressHandler", "OOMHeight:" + bitmap.getHeight() + "Width:" + bitmap.getHeight() + "matrix:" + xS + " " + yS);
/* 185 */       return null;
/*     */     }
/* 187 */     return result;
/*     */   }
/*     */ 
/*     */   public static Bitmap getRotateBitmap(float degrees, Bitmap bm) {
/* 191 */     int bmpW = bm.getWidth();
/* 192 */     int bmpH = bm.getHeight();
/*     */ 
/* 194 */     Matrix mt = new Matrix();
/*     */ 
/* 199 */     mt.setRotate(degrees);
/* 200 */     return Bitmap.createBitmap(bm, 0, 0, bmpW, bmpH, mt, true);
/*     */   }
/*     */ 
/*     */   private static BitmapFactory.Options decodeBitmapOptionsInfo(Context context, Uri uri) {
/* 204 */     InputStream input = null;
/* 205 */     BitmapFactory.Options opt = new BitmapFactory.Options();
/*     */     try {
/* 207 */       if (uri.getScheme().equals("content"))
/* 208 */         input = context.getContentResolver().openInputStream(uri);
/* 209 */       else if (uri.getScheme().equals("file")) {
/* 210 */         input = new FileInputStream(uri.getPath());
/*     */       }
/* 212 */       opt.inJustDecodeBounds = true;
/* 213 */       BitmapFactory.decodeStream(input, null, opt);
/* 214 */       BitmapFactory.Options localOptions1 = opt;
/*     */       return localOptions1;
/*     */     }
/*     */     catch (FileNotFoundException e)
/*     */     {
/* 217 */       if (input == null) {
/* 218 */         input = getFileInputStream(uri.getPath());
/*     */       }
/* 220 */       opt.inJustDecodeBounds = true;
/* 221 */       BitmapFactory.decodeStream(input, null, opt);
/* 222 */       e = opt;
/*     */       return e;
/*     */     }
/*     */     finally
/*     */     {
/* 225 */       if (null != input)
/*     */         try {
/* 227 */           input.close();
/*     */         }
/*     */         catch (IOException e) {
/*     */         }
/*     */     }
/* 232 */     throw localObject;
/*     */   }
/*     */ 
/*     */   private static Bitmap rotateBitMap(String srcFilePath, Bitmap bitmap)
/*     */   {
/* 247 */     ExifInterface exif = null;
/*     */     try
/*     */     {
/* 250 */       exif = new ExifInterface(srcFilePath);
/*     */     } catch (IOException e) {
/* 252 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 255 */     float degree = 0.0F;
/*     */ 
/* 257 */     if (exif != null) {
/* 258 */       switch (exif.getAttributeInt("Orientation", 0))
/*     */       {
/*     */       case 6:
/* 261 */         degree = 90.0F;
/* 262 */         break;
/*     */       case 3:
/* 264 */         degree = 180.0F;
/* 265 */         break;
/*     */       case 8:
/* 267 */         degree = 270.0F;
/* 268 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 274 */     if (degree != 0.0F) {
/* 275 */       Matrix matrix = new Matrix();
/* 276 */       matrix.setRotate(degree, bitmap.getWidth(), bitmap.getHeight());
/* 277 */       Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
/*     */ 
/* 279 */       if ((b2 != null) && (bitmap != b2)) {
/* 280 */         bitmap.recycle();
/* 281 */         bitmap = b2;
/*     */       }
/*     */     }
/*     */ 
/* 285 */     return bitmap;
/*     */   }
/*     */ 
/*     */   public static InputStream getFileInputStream(String path)
/*     */   {
/* 290 */     FileInputStream fileInputStream = null;
/*     */     try
/*     */     {
/* 293 */       fileInputStream = new FileInputStream(new File(path));
/*     */     } catch (FileNotFoundException e) {
/* 295 */       e.printStackTrace();
/*     */     }
/* 297 */     return fileInputStream;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.utils.BitmapUtil
 * JD-Core Version:    0.6.0
 */
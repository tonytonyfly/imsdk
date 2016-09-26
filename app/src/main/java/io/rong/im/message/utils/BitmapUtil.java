/*     */ package io.rong.message.utils;
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
/*     */ import io.rong.common.RLog;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class BitmapUtil
/*     */ {
/*     */   private static final String TAG = "Util";
/*     */ 
/*     */   public static String getBase64FromBitmap(Bitmap bitmap)
/*     */   {
/*  27 */     String base64Str = null;
/*  28 */     ByteArrayOutputStream baos = null;
/*     */     try
/*     */     {
/*  31 */       if (bitmap != null)
/*     */       {
/*  33 */         baos = new ByteArrayOutputStream();
/*  34 */         bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
/*     */ 
/*  36 */         byte[] bitmapBytes = baos.toByteArray();
/*  37 */         base64Str = Base64.encodeToString(bitmapBytes, 2);
/*  38 */         Log.d("base64Str", "" + base64Str.length());
/*     */ 
/*  40 */         baos.flush();
/*  41 */         baos.close();
/*     */       }
/*     */     } catch (IOException e) {
/*  44 */       e.printStackTrace();
/*     */     } finally {
/*     */       try {
/*  47 */         if (baos != null) {
/*  48 */           baos.flush();
/*  49 */           baos.close();
/*     */         }
/*     */       } catch (IOException e) {
/*  52 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */ 
/*  56 */     return base64Str;
/*     */   }
/*     */ 
/*     */   public static Bitmap getBitmapFromBase64(String base64Str)
/*     */   {
/*  62 */     if (TextUtils.isEmpty(base64Str)) {
/*  63 */       return null;
/*     */     }
/*     */ 
/*  66 */     byte[] bytes = Base64.decode(base64Str, 2);
/*  67 */     return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
/*     */   }
/*     */ 
/*     */   public static Bitmap getResizedBitmap(Context context, Uri uri, int widthLimit, int heightLimit) throws IOException
/*     */   {
/*  72 */     String path = null;
/*  73 */     Bitmap result = null;
/*     */ 
/*  75 */     if (uri.getScheme().equals("file")) {
/*  76 */       path = uri.toString().substring(5);
/*  77 */     } else if (uri.getScheme().equals("content")) {
/*  78 */       Cursor cursor = context.getContentResolver().query(uri, new String[] { "_data" }, null, null, null);
/*  79 */       cursor.moveToFirst();
/*  80 */       path = cursor.getString(0);
/*  81 */       cursor.close();
/*     */     } else {
/*  83 */       return null;
/*     */     }
/*     */ 
/*  86 */     ExifInterface exifInterface = new ExifInterface(path);
/*     */ 
/*  88 */     BitmapFactory.Options options = new BitmapFactory.Options();
/*  89 */     options.inJustDecodeBounds = true;
/*  90 */     BitmapFactory.decodeFile(path, options);
/*     */ 
/*  92 */     int orientation = exifInterface.getAttributeInt("Orientation", 0);
/*     */ 
/*  94 */     if ((orientation == 6) || (orientation == 8) || (orientation == 5) || (orientation == 7))
/*     */     {
/*  98 */       int tmp = widthLimit;
/*  99 */       widthLimit = heightLimit;
/* 100 */       heightLimit = tmp;
/*     */     }
/*     */ 
/* 103 */     int width = options.outWidth;
/* 104 */     int height = options.outHeight;
/* 105 */     int sampleW = 1; int sampleH = 1;
/* 106 */     while (width / 2 > widthLimit) {
/* 107 */       width /= 2;
/* 108 */       sampleW <<= 1;
/*     */     }
/*     */ 
/* 112 */     while (height / 2 > heightLimit) {
/* 113 */       height /= 2;
/* 114 */       sampleH <<= 1;
/*     */     }
/* 116 */     int sampleSize = 1;
/*     */ 
/* 118 */     options = new BitmapFactory.Options();
/* 119 */     if ((widthLimit == 2147483647) || (heightLimit == 2147483647))
/* 120 */       sampleSize = Math.max(sampleW, sampleH);
/*     */     else {
/* 122 */       sampleSize = Math.max(sampleW, sampleH);
/* 124 */     }options.inSampleSize = sampleSize;
/*     */     Bitmap bitmap;
/*     */     try {
/* 128 */       bitmap = BitmapFactory.decodeFile(path, options);
/*     */     } catch (OutOfMemoryError e) {
/* 130 */       e.printStackTrace();
/* 131 */       options.inSampleSize <<= 1;
/* 132 */       bitmap = BitmapFactory.decodeFile(path, options);
/*     */     }
/*     */ 
/* 135 */     Matrix matrix = new Matrix();
/* 136 */     if (bitmap == null) {
/* 137 */       return bitmap;
/*     */     }
/* 139 */     int w = bitmap.getWidth();
/* 140 */     int h = bitmap.getHeight();
/*     */ 
/* 142 */     if ((orientation == 6) || (orientation == 8) || (orientation == 5) || (orientation == 7))
/*     */     {
/* 146 */       int tmp = w;
/* 147 */       w = h;
/* 148 */       h = tmp;
/*     */     }
/* 150 */     switch (orientation) {
/*     */     case 6:
/* 152 */       matrix.setRotate(90.0F, w / 2.0F, h / 2.0F);
/* 153 */       break;
/*     */     case 3:
/* 155 */       matrix.setRotate(180.0F, w / 2.0F, h / 2.0F);
/* 156 */       break;
/*     */     case 8:
/* 158 */       matrix.setRotate(270.0F, w / 2.0F, h / 2.0F);
/* 159 */       break;
/*     */     case 2:
/* 161 */       matrix.preScale(-1.0F, 1.0F);
/* 162 */       break;
/*     */     case 4:
/* 164 */       matrix.preScale(1.0F, -1.0F);
/* 165 */       break;
/*     */     case 5:
/* 167 */       matrix.setRotate(90.0F, w / 2.0F, h / 2.0F);
/* 168 */       matrix.preScale(1.0F, -1.0F);
/* 169 */       break;
/*     */     case 7:
/* 171 */       matrix.setRotate(270.0F, w / 2.0F, h / 2.0F);
/* 172 */       matrix.preScale(1.0F, -1.0F);
/*     */     }
/*     */ 
/* 175 */     float xS = widthLimit / bitmap.getWidth();
/* 176 */     float yS = heightLimit / bitmap.getHeight();
/*     */ 
/* 178 */     matrix.postScale(Math.min(xS, yS), Math.min(xS, yS));
/*     */     try {
/* 180 */       result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
/*     */     } catch (OutOfMemoryError e) {
/* 182 */       e.printStackTrace();
/* 183 */       Log.d("ResourceCompressHandler", "OOMHeight:" + bitmap.getHeight() + "Width:" + bitmap.getHeight() + "matrix:" + xS + " " + yS);
/* 184 */       return null;
/*     */     }
/* 186 */     return result;
/*     */   }
/*     */ 
/*     */   public static Bitmap interceptBitmap(String filePath, int w, int h) {
/* 190 */     Bitmap bitmap = BitmapFactory.decodeFile(filePath);
/* 191 */     int widthOrg = bitmap.getWidth();
/* 192 */     int heightOrg = bitmap.getHeight();
/*     */ 
/* 194 */     int xTopLeft = (widthOrg - w) / 2;
/* 195 */     int yTopLeft = (heightOrg - h) / 2;
/*     */ 
/* 197 */     if ((xTopLeft <= 0) || (yTopLeft <= 0)) {
/* 198 */       RLog.w("Util", "ignore intercept [" + widthOrg + ", " + heightOrg + ":" + w + ", " + h + "]");
/* 199 */       return bitmap;
/*     */     }
/*     */     try
/*     */     {
/* 203 */       Bitmap result = Bitmap.createBitmap(bitmap, xTopLeft, yTopLeft, w, h);
/* 204 */       if (!bitmap.isRecycled())
/* 205 */         bitmap.recycle();
/* 206 */       return result; } catch (OutOfMemoryError e) {
/*     */     }
/* 208 */     return null;
/*     */   }
/*     */ 
/*     */   public static Bitmap getThumbBitmap(Context context, Uri uri, int sizeLimit, int minSize)
/*     */     throws IOException
/*     */   {
/*     */     String path;
/* 217 */     if (uri.getScheme().equals("file")) {
/* 218 */       path = uri.toString().substring(5);
/* 219 */     } else if (uri.getScheme().equals("content")) {
/* 220 */       Cursor cursor = context.getContentResolver().query(uri, new String[] { "_data" }, null, null, null);
/* 221 */       cursor.moveToFirst();
/* 222 */       String path = cursor.getString(0);
/* 223 */       cursor.close();
/*     */     } else {
/* 225 */       return null;
/*     */     }
/*     */     String path;
/* 228 */     ExifInterface exifInterface = new ExifInterface(path);
/*     */ 
/* 230 */     BitmapFactory.Options options = new BitmapFactory.Options();
/* 231 */     options.inJustDecodeBounds = true;
/* 232 */     BitmapFactory.decodeFile(path, options);
/*     */ 
/* 234 */     int orientation = exifInterface.getAttributeInt("Orientation", 0);
/*     */ 
/* 236 */     int width = options.outWidth;
/* 237 */     int height = options.outHeight;
/*     */ 
/* 239 */     int longSide = width > height ? width : height;
/* 240 */     int shortSide = width > height ? height : width;
/* 241 */     float scale = longSide / shortSide;
/* 242 */     int sampleW = 1; int sampleH = 1;
/* 243 */     int sampleSize = 1;
/* 244 */     if (scale > sizeLimit / minSize) {
/* 245 */       while (shortSide / 2 > minSize) {
/* 246 */         shortSide /= 2;
/* 247 */         sampleSize <<= 1;
/*     */       }
/* 249 */       options = new BitmapFactory.Options();
/* 250 */       options.inSampleSize = sampleSize;
/*     */     } else {
/* 252 */       while (width / 2 > sizeLimit) {
/* 253 */         width /= 2;
/* 254 */         sampleW <<= 1;
/*     */       }
/*     */ 
/* 257 */       while (height / 2 > sizeLimit) {
/* 258 */         height /= 2;
/* 259 */         sampleH <<= 1;
/*     */       }
/*     */ 
/* 262 */       options = new BitmapFactory.Options();
/* 263 */       sampleSize = Math.max(sampleW, sampleH);
/* 264 */       options.inSampleSize = sampleSize;
/*     */     }Bitmap bitmap;
/*     */     try {
/* 268 */       bitmap = BitmapFactory.decodeFile(path, options);
/*     */     } catch (OutOfMemoryError e) {
/* 270 */       e.printStackTrace();
/* 271 */       options.inSampleSize <<= 1;
/* 272 */       bitmap = BitmapFactory.decodeFile(path, options);
/*     */     }
/* 274 */     Matrix matrix = new Matrix();
/* 275 */     if (bitmap == null) {
/* 276 */       return bitmap;
/*     */     }
/* 278 */     int w = bitmap.getWidth();
/* 279 */     int h = bitmap.getHeight();
/*     */ 
/* 281 */     if ((orientation == 6) || (orientation == 8) || (orientation == 5) || (orientation == 7))
/*     */     {
/* 285 */       int tmp = w;
/* 286 */       w = h;
/* 287 */       h = tmp;
/*     */     }
/* 289 */     switch (orientation) {
/*     */     case 6:
/* 291 */       matrix.setRotate(90.0F, w / 2.0F, h / 2.0F);
/* 292 */       break;
/*     */     case 3:
/* 294 */       matrix.setRotate(180.0F, w / 2.0F, h / 2.0F);
/* 295 */       break;
/*     */     case 8:
/* 297 */       matrix.setRotate(270.0F, w / 2.0F, h / 2.0F);
/* 298 */       break;
/*     */     case 2:
/* 300 */       matrix.preScale(-1.0F, 1.0F);
/* 301 */       break;
/*     */     case 4:
/* 303 */       matrix.preScale(1.0F, -1.0F);
/* 304 */       break;
/*     */     case 5:
/* 306 */       matrix.setRotate(90.0F, w / 2.0F, h / 2.0F);
/* 307 */       matrix.preScale(1.0F, -1.0F);
/* 308 */       break;
/*     */     case 7:
/* 310 */       matrix.setRotate(270.0F, w / 2.0F, h / 2.0F);
/* 311 */       matrix.preScale(1.0F, -1.0F);
/*     */     }
/*     */ 
/* 314 */     float sS = 0.0F;
/* 315 */     float xS = 0.0F;
/* 316 */     float yS = 0.0F;
/* 317 */     if (scale > sizeLimit / minSize) {
/* 318 */       shortSide = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getHeight() : bitmap.getWidth();
/* 319 */       sS = minSize / shortSide;
/* 320 */       matrix.postScale(sS, sS);
/*     */     } else {
/* 322 */       xS = sizeLimit / bitmap.getWidth();
/* 323 */       yS = sizeLimit / bitmap.getHeight();
/* 324 */       matrix.postScale(Math.min(xS, yS), Math.min(xS, yS)); } int x = 0; int y = 0;
/*     */     Bitmap result;
/*     */     try { if (scale > sizeLimit / minSize) {
/* 329 */         if (bitmap.getWidth() > bitmap.getHeight()) {
/* 330 */           h = bitmap.getHeight();
/* 331 */           w = h * sizeLimit / minSize;
/* 332 */           x = (bitmap.getWidth() - w) / 2;
/* 333 */           y = 0;
/*     */         } else {
/* 335 */           w = bitmap.getWidth();
/* 336 */           h = w * sizeLimit / minSize;
/* 337 */           x = 0;
/* 338 */           y = (bitmap.getHeight() - h) / 2;
/*     */         }
/*     */       } else {
/* 341 */         w = bitmap.getWidth();
/* 342 */         h = bitmap.getHeight();
/*     */       }
/* 344 */       result = Bitmap.createBitmap(bitmap, x, y, w, h, matrix, true);
/*     */     } catch (OutOfMemoryError e) {
/* 346 */       e.printStackTrace();
/* 347 */       Log.d("ResourceCompressHandler", "OOMHeight:" + bitmap.getHeight() + "Width:" + bitmap.getHeight() + "matrix:" + sS + " " + xS + " " + yS);
/* 348 */       if (!bitmap.isRecycled())
/* 349 */         bitmap.recycle();
/* 350 */       return null;
/*     */     }
/* 352 */     if (!bitmap.isRecycled())
/* 353 */       bitmap.recycle();
/* 354 */     return result;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.utils.BitmapUtil
 * JD-Core Version:    0.6.0
 */
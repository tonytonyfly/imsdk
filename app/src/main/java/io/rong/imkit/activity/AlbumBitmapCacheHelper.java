/*     */ package io.rong.imkit.activity;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.Bitmap.CompressFormat;
/*     */ import android.graphics.BitmapFactory;
/*     */ import android.graphics.BitmapFactory.Options;
/*     */ import android.graphics.Matrix;
/*     */ import android.media.ExifInterface;
/*     */ import android.os.Handler;
/*     */ import android.os.Message;
/*     */ import android.support.v4.util.LruCache;
/*     */ import android.util.Log;
/*     */ import android.view.Display;
/*     */ import android.view.WindowManager;
/*     */ import io.rong.common.FileUtils;
/*     */ import io.rong.imkit.utils.CommonUtils;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class AlbumBitmapCacheHelper
/*     */ {
/*     */   private static final String TAG = "AlbumBitmapCacheHelper";
/*  31 */   private static volatile AlbumBitmapCacheHelper instance = null;
/*     */   private LruCache<String, Bitmap> cache;
/*     */   private ArrayList<String> currentShowString;
/*     */   Context mContext;
/* 128 */   ThreadPoolExecutor tpe = new ThreadPoolExecutor(2, 5, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue());
/*     */ 
/*     */   private AlbumBitmapCacheHelper()
/*     */   {
/*  41 */     int memory = (int)(Runtime.getRuntime().maxMemory() / 1024L / 4L);
/*     */ 
/*  43 */     this.cache = new LruCache(memory)
/*     */     {
/*     */       protected int sizeOf(String key, Bitmap value)
/*     */       {
/*  47 */         return value.getRowBytes() * value.getHeight() / 1024;
/*     */       }
/*     */     };
/*  51 */     this.currentShowString = new ArrayList();
/*     */   }
/*     */ 
/*     */   public void releaseAllSizeCache()
/*     */   {
/*  59 */     this.cache.evictAll();
/*  60 */     this.cache.resize(1);
/*     */   }
/*     */ 
/*     */   public void releaseHalfSizeCache() {
/*  64 */     this.cache.resize((int)(Runtime.getRuntime().maxMemory() / 1024L / 8L));
/*     */   }
/*     */ 
/*     */   public void resizeCache() {
/*  68 */     this.cache.resize((int)(Runtime.getRuntime().maxMemory() / 1024L / 4L));
/*     */   }
/*     */ 
/*     */   private void clearCache()
/*     */   {
/*  75 */     this.cache.evictAll();
/*  76 */     this.cache = null;
/*  77 */     this.tpe = null;
/*  78 */     instance = null;
/*     */   }
/*     */ 
/*     */   public static AlbumBitmapCacheHelper getInstance() {
/*  82 */     if (instance == null) {
/*  83 */       synchronized (AlbumBitmapCacheHelper.class) {
/*  84 */         if (instance == null) {
/*  85 */           instance = new AlbumBitmapCacheHelper();
/*     */         }
/*     */       }
/*     */     }
/*  89 */     return instance;
/*     */   }
/*     */ 
/*     */   public static void init(Context context)
/*     */   {
/*  95 */     AlbumBitmapCacheHelper helper = getInstance();
/*  96 */     helper.mContext = context.getApplicationContext();
/*     */   }
/*     */ 
/*     */   public void uninit() {
/* 100 */     this.currentShowString.clear();
/* 101 */     for (Runnable runnable : this.tpe.getQueue()) {
/* 102 */       this.tpe.remove(runnable);
/*     */     }
/* 104 */     clearCache();
/*     */   }
/*     */ 
/*     */   public Bitmap getBitmap(String path, int width, int height, ILoadImageCallback callback, Object[] objects)
/*     */   {
/* 117 */     Bitmap bitmap = getBitmapFromCache(path, width, height);
/*     */ 
/* 119 */     if (bitmap != null)
/* 120 */       Log.e("AlbumBitmapCacheHelper", "getBitmap from cache");
/*     */     else {
/* 122 */       decodeBitmapFromPath(path, width, height, callback, objects);
/*     */     }
/* 124 */     return bitmap;
/*     */   }
/*     */ 
/*     */   private void decodeBitmapFromPath(String path, int width, int height, ILoadImageCallback callback, Object[] objects)
/*     */     throws OutOfMemoryError
/*     */   {
/* 135 */     Handler handler = new Handler(callback, path, objects)
/*     */     {
/*     */       public void handleMessage(Message msg) {
/* 138 */         if (this.val$callback != null)
/* 139 */           this.val$callback.onLoadImageCallBack((Bitmap)msg.obj, this.val$path, this.val$objects);
/*     */       }
/*     */     };
/* 143 */     this.tpe.execute(new Runnable(path, width, height, handler)
/*     */     {
/*     */       public void run() {
/* 146 */         if ((!AlbumBitmapCacheHelper.this.currentShowString.contains(this.val$path)) || (AlbumBitmapCacheHelper.this.cache == null)) {
/* 147 */           return;
/*     */         }
/* 149 */         Bitmap bitmap = null;
/*     */ 
/* 151 */         if ((this.val$width == 0) || (this.val$height == 0)) {
/*     */           try {
/* 153 */             bitmap = AlbumBitmapCacheHelper.this.getBitmap(this.val$path, this.val$width, this.val$height);
/*     */           } catch (OutOfMemoryError e) {
/* 155 */             e.printStackTrace();
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 161 */           String hash = CommonUtils.md5(this.val$path + "_" + this.val$width + "_" + this.val$height);
/*     */ 
/* 163 */           String tempPath = FileUtils.getCachePath(AlbumBitmapCacheHelper.this.mContext, "image") + "/" + hash + ".temp";
/* 164 */           File picFile = new File(this.val$path);
/* 165 */           File tempFile = new File(tempPath);
/*     */ 
/* 167 */           if ((tempFile.exists()) && (picFile.lastModified() <= tempFile.lastModified())) {
/* 168 */             bitmap = BitmapFactory.decodeFile(tempPath);
/*     */           }
/* 170 */           if (bitmap == null) {
/*     */             try {
/* 172 */               bitmap = AlbumBitmapCacheHelper.this.getBitmap(this.val$path, this.val$width, this.val$height);
/*     */             } catch (OutOfMemoryError e) {
/* 174 */               bitmap = null;
/*     */             }
/* 176 */             if ((bitmap != null) && (AlbumBitmapCacheHelper.this.cache != null)) {
/* 177 */               bitmap = AlbumBitmapCacheHelper.centerSquareScaleBitmap(bitmap, bitmap.getWidth() > bitmap.getHeight() ? bitmap.getHeight() : bitmap.getWidth());
/*     */             }
/* 179 */             if (bitmap != null) {
/*     */               try {
/* 181 */                 File file = new File(tempPath);
/* 182 */                 if (!file.exists()) {
/* 183 */                   file.createNewFile();
/*     */                 } else {
/* 185 */                   file.delete();
/* 186 */                   file.createNewFile();
/*     */                 }
/* 188 */                 FileOutputStream fos = new FileOutputStream(file);
/* 189 */                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 190 */                 bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
/* 191 */                 fos.write(baos.toByteArray());
/* 192 */                 fos.flush();
/* 193 */                 fos.close();
/*     */               } catch (FileNotFoundException e) {
/* 195 */                 e.printStackTrace();
/*     */               } catch (IOException e) {
/* 197 */                 e.printStackTrace();
/*     */               }
/*     */             }
/*     */ 
/*     */           }
/* 202 */           else if (AlbumBitmapCacheHelper.this.cache != null) {
/* 203 */             bitmap = AlbumBitmapCacheHelper.centerSquareScaleBitmap(bitmap, bitmap.getWidth() > bitmap.getHeight() ? bitmap.getHeight() : bitmap.getWidth());
/*     */           }
/*     */         }
/*     */ 
/* 207 */         if ((bitmap != null) && (AlbumBitmapCacheHelper.this.cache != null))
/* 208 */           AlbumBitmapCacheHelper.this.cache.put(this.val$path + "_" + this.val$width + "_" + this.val$height, bitmap);
/* 209 */         Message msg = Message.obtain();
/* 210 */         msg.obj = bitmap;
/* 211 */         this.val$handler.sendMessage(msg);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength)
/*     */   {
/* 222 */     if ((null == bitmap) || (edgeLength <= 0)) {
/* 223 */       return null;
/*     */     }
/* 225 */     Bitmap result = bitmap;
/* 226 */     int widthOrg = bitmap.getWidth();
/* 227 */     int heightOrg = bitmap.getHeight();
/*     */ 
/* 230 */     int xTopLeft = (widthOrg - edgeLength) / 2;
/* 231 */     int yTopLeft = (heightOrg - edgeLength) / 2;
/*     */ 
/* 233 */     if ((xTopLeft == 0) && (yTopLeft == 0)) return result;
/*     */     try
/*     */     {
/* 236 */       result = Bitmap.createBitmap(bitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
/* 237 */       if (!bitmap.isRecycled())
/* 238 */         bitmap.recycle();
/*     */     } catch (OutOfMemoryError e) {
/* 240 */       return result;
/*     */     }
/*     */ 
/* 243 */     return result;
/*     */   }
/*     */ 
/*     */   private int computeScale(BitmapFactory.Options options, int width, int height)
/*     */   {
/* 250 */     if (options == null) return 1;
/* 251 */     int widthScale = (int)(options.outWidth / width);
/* 252 */     int heightScale = (int)(options.outHeight / height);
/*     */ 
/* 254 */     int scale = widthScale > heightScale ? widthScale : heightScale;
/* 255 */     if (scale < 1) scale = 1;
/* 256 */     return scale;
/*     */   }
/*     */ 
/*     */   private Bitmap getBitmapFromCache(String path, int width, int height)
/*     */   {
/* 268 */     return (Bitmap)this.cache.get(path + "_" + width + "_" + height);
/*     */   }
/*     */ 
/*     */   public void addPathToShowlist(String path)
/*     */   {
/* 275 */     this.currentShowString.add(path);
/*     */   }
/*     */ 
/*     */   public void removePathFromShowlist(String path)
/*     */   {
/* 282 */     this.currentShowString.remove(path);
/*     */   }
/*     */ 
/*     */   private Bitmap getBitmap(String path, int widthLimit, int heightLimit)
/*     */     throws OutOfMemoryError
/*     */   {
/* 293 */     Bitmap bitmap = null;
/*     */     try
/*     */     {
/* 296 */       BitmapFactory.Options options = new BitmapFactory.Options();
/* 297 */       options.inJustDecodeBounds = true;
/* 298 */       BitmapFactory.decodeFile(path, options);
/*     */ 
/* 300 */       ExifInterface exifInterface = new ExifInterface(path);
/* 301 */       int orientation = exifInterface.getAttributeInt("Orientation", 0);
/* 302 */       int sampleSize = 1;
/* 303 */       if ((widthLimit == 0) && (heightLimit == 0)) {
/* 304 */         sampleSize = computeScale(options, ((WindowManager)(WindowManager)this.mContext.getSystemService("window")).getDefaultDisplay().getWidth(), ((WindowManager)(WindowManager)this.mContext.getSystemService("window")).getDefaultDisplay().getWidth());
/*     */       } else {
/* 306 */         if ((orientation == 6) || (orientation == 8) || (orientation == 5) || (orientation == 7))
/*     */         {
/* 310 */           int tmp = widthLimit;
/* 311 */           widthLimit = heightLimit;
/* 312 */           heightLimit = tmp;
/*     */         }
/*     */ 
/* 315 */         int width = options.outWidth;
/* 316 */         int height = options.outHeight;
/* 317 */         int sampleW = 1; int sampleH = 1;
/* 318 */         while (width / 2 > widthLimit) {
/* 319 */           width /= 2;
/* 320 */           sampleW <<= 1;
/*     */         }
/*     */ 
/* 323 */         while (height / 2 > heightLimit) {
/* 324 */           height /= 2;
/* 325 */           sampleH <<= 1;
/*     */         }
/*     */ 
/* 328 */         if ((widthLimit == 2147483647) || (heightLimit == 2147483647))
/* 329 */           sampleSize = Math.max(sampleW, sampleH);
/*     */         else
/* 331 */           sampleSize = Math.max(sampleW, sampleH);
/*     */       }
/*     */       try
/*     */       {
/* 335 */         options = new BitmapFactory.Options();
/* 336 */         options.inJustDecodeBounds = false;
/* 337 */         options.inSampleSize = sampleSize;
/* 338 */         bitmap = BitmapFactory.decodeFile(path, options);
/*     */       } catch (OutOfMemoryError e) {
/* 340 */         e.printStackTrace();
/* 341 */         options.inSampleSize <<= 1;
/* 342 */         bitmap = BitmapFactory.decodeFile(path, options);
/*     */       }
/* 344 */       Matrix matrix = new Matrix();
/* 345 */       if (bitmap != null) {
/* 346 */         int w = bitmap.getWidth();
/* 347 */         int h = bitmap.getHeight();
/*     */ 
/* 349 */         if ((orientation == 6) || (orientation == 8) || (orientation == 5) || (orientation == 7))
/*     */         {
/* 353 */           int tmp = w;
/* 354 */           w = h;
/* 355 */           h = tmp;
/*     */         }
/* 357 */         switch (orientation) {
/*     */         case 6:
/* 359 */           matrix.setRotate(90.0F, w / 2.0F, h / 2.0F);
/* 360 */           break;
/*     */         case 3:
/* 362 */           matrix.setRotate(180.0F, w / 2.0F, h / 2.0F);
/* 363 */           break;
/*     */         case 8:
/* 365 */           matrix.setRotate(270.0F, w / 2.0F, h / 2.0F);
/* 366 */           break;
/*     */         case 2:
/* 368 */           matrix.preScale(-1.0F, 1.0F);
/* 369 */           break;
/*     */         case 4:
/* 371 */           matrix.preScale(1.0F, -1.0F);
/* 372 */           break;
/*     */         case 5:
/* 374 */           matrix.setRotate(90.0F, w / 2.0F, h / 2.0F);
/* 375 */           matrix.preScale(1.0F, -1.0F);
/* 376 */           break;
/*     */         case 7:
/* 378 */           matrix.setRotate(270.0F, w / 2.0F, h / 2.0F);
/* 379 */           matrix.preScale(1.0F, -1.0F);
/*     */         }
/*     */ 
/* 382 */         if ((widthLimit != 0) && (heightLimit != 0))
/*     */         {
/* 385 */           float xS = widthLimit / bitmap.getWidth();
/* 386 */           float yS = heightLimit / bitmap.getHeight();
/* 387 */           matrix.postScale(Math.min(xS, yS), Math.min(xS, yS));
/*     */         }
/* 389 */         bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
/*     */       }
/*     */     } catch (IOException e) {
/* 392 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 395 */     return bitmap;
/*     */   }
/*     */ 
/*     */   public static abstract interface ILoadImageCallback
/*     */   {
/*     */     public abstract void onLoadImageCallBack(Bitmap paramBitmap, String paramString, Object[] paramArrayOfObject);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.activity.AlbumBitmapCacheHelper
 * JD-Core Version:    0.6.0
 */
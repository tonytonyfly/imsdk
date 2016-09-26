/*     */ package io.rong.imageloader.core.decode;
/*     */ 
/*     */ import android.annotation.TargetApi;
/*     */ import android.graphics.BitmapFactory.Options;
/*     */ import android.os.Build.VERSION;
/*     */ import io.rong.imageloader.core.DisplayImageOptions;
/*     */ import io.rong.imageloader.core.assist.ImageScaleType;
/*     */ import io.rong.imageloader.core.assist.ImageSize;
/*     */ import io.rong.imageloader.core.assist.ViewScaleType;
/*     */ import io.rong.imageloader.core.download.ImageDownloader;
/*     */ 
/*     */ public class ImageDecodingInfo
/*     */ {
/*     */   private final String imageKey;
/*     */   private final String imageUri;
/*     */   private final String originalImageUri;
/*     */   private final ImageSize targetSize;
/*     */   private final ImageScaleType imageScaleType;
/*     */   private final ViewScaleType viewScaleType;
/*     */   private final ImageDownloader downloader;
/*     */   private final Object extraForDownloader;
/*     */   private final boolean considerExifParams;
/*     */   private final BitmapFactory.Options decodingOptions;
/*     */ 
/*     */   public ImageDecodingInfo(String imageKey, String imageUri, String originalImageUri, ImageSize targetSize, ViewScaleType viewScaleType, ImageDownloader downloader, DisplayImageOptions displayOptions)
/*     */   {
/*  52 */     this.imageKey = imageKey;
/*  53 */     this.imageUri = imageUri;
/*  54 */     this.originalImageUri = originalImageUri;
/*  55 */     this.targetSize = targetSize;
/*     */ 
/*  57 */     this.imageScaleType = displayOptions.getImageScaleType();
/*  58 */     this.viewScaleType = viewScaleType;
/*     */ 
/*  60 */     this.downloader = downloader;
/*  61 */     this.extraForDownloader = displayOptions.getExtraForDownloader();
/*     */ 
/*  63 */     this.considerExifParams = displayOptions.isConsiderExifParams();
/*  64 */     this.decodingOptions = new BitmapFactory.Options();
/*  65 */     copyOptions(displayOptions.getDecodingOptions(), this.decodingOptions);
/*     */   }
/*     */ 
/*     */   private void copyOptions(BitmapFactory.Options srcOptions, BitmapFactory.Options destOptions) {
/*  69 */     destOptions.inDensity = srcOptions.inDensity;
/*  70 */     destOptions.inDither = srcOptions.inDither;
/*  71 */     destOptions.inInputShareable = srcOptions.inInputShareable;
/*  72 */     destOptions.inJustDecodeBounds = srcOptions.inJustDecodeBounds;
/*  73 */     destOptions.inPreferredConfig = srcOptions.inPreferredConfig;
/*  74 */     destOptions.inPurgeable = srcOptions.inPurgeable;
/*  75 */     destOptions.inSampleSize = srcOptions.inSampleSize;
/*  76 */     destOptions.inScaled = srcOptions.inScaled;
/*  77 */     destOptions.inScreenDensity = srcOptions.inScreenDensity;
/*  78 */     destOptions.inTargetDensity = srcOptions.inTargetDensity;
/*  79 */     destOptions.inTempStorage = srcOptions.inTempStorage;
/*  80 */     if (Build.VERSION.SDK_INT >= 10) copyOptions10(srcOptions, destOptions);
/*  81 */     if (Build.VERSION.SDK_INT >= 11) copyOptions11(srcOptions, destOptions); 
/*     */   }
/*     */ 
/*     */   @TargetApi(10)
/*     */   private void copyOptions10(BitmapFactory.Options srcOptions, BitmapFactory.Options destOptions) {
/*  86 */     destOptions.inPreferQualityOverSpeed = srcOptions.inPreferQualityOverSpeed;
/*     */   }
/*     */   @TargetApi(11)
/*     */   private void copyOptions11(BitmapFactory.Options srcOptions, BitmapFactory.Options destOptions) {
/*  91 */     destOptions.inBitmap = srcOptions.inBitmap;
/*  92 */     destOptions.inMutable = srcOptions.inMutable;
/*     */   }
/*     */ 
/*     */   public String getImageKey()
/*     */   {
/*  97 */     return this.imageKey;
/*     */   }
/*     */ 
/*     */   public String getImageUri()
/*     */   {
/* 102 */     return this.imageUri;
/*     */   }
/*     */ 
/*     */   public String getOriginalImageUri()
/*     */   {
/* 107 */     return this.originalImageUri;
/*     */   }
/*     */ 
/*     */   public ImageSize getTargetSize()
/*     */   {
/* 115 */     return this.targetSize;
/*     */   }
/*     */ 
/*     */   public ImageScaleType getImageScaleType()
/*     */   {
/* 123 */     return this.imageScaleType;
/*     */   }
/*     */ 
/*     */   public ViewScaleType getViewScaleType()
/*     */   {
/* 128 */     return this.viewScaleType;
/*     */   }
/*     */ 
/*     */   public ImageDownloader getDownloader()
/*     */   {
/* 133 */     return this.downloader;
/*     */   }
/*     */ 
/*     */   public Object getExtraForDownloader()
/*     */   {
/* 138 */     return this.extraForDownloader;
/*     */   }
/*     */ 
/*     */   public boolean shouldConsiderExifParams()
/*     */   {
/* 143 */     return this.considerExifParams;
/*     */   }
/*     */ 
/*     */   public BitmapFactory.Options getDecodingOptions()
/*     */   {
/* 148 */     return this.decodingOptions;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.decode.ImageDecodingInfo
 * JD-Core Version:    0.6.0
 */
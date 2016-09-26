/*     */ package io.rong.imageloader.utils;
/*     */ 
/*     */ import android.opengl.GLES10;
/*     */ import io.rong.imageloader.core.assist.ImageSize;
/*     */ import io.rong.imageloader.core.assist.ViewScaleType;
/*     */ import io.rong.imageloader.core.imageaware.ImageAware;
/*     */ 
/*     */ public final class ImageSizeUtils
/*     */ {
/*     */   private static final int DEFAULT_MAX_BITMAP_DIMENSION = 2048;
/*     */   private static ImageSize maxBitmapSize;
/*     */ 
/*     */   public static ImageSize defineTargetSizeForView(ImageAware imageAware, ImageSize maxImageSize)
/*     */   {
/*  55 */     int width = imageAware.getWidth();
/*  56 */     if (width <= 0) width = maxImageSize.getWidth();
/*     */ 
/*  58 */     int height = imageAware.getHeight();
/*  59 */     if (height <= 0) height = maxImageSize.getHeight();
/*     */ 
/*  61 */     return new ImageSize(width, height);
/*     */   }
/*     */ 
/*     */   public static int computeImageSampleSize(ImageSize srcSize, ImageSize targetSize, ViewScaleType viewScaleType, boolean powerOf2Scale)
/*     */   {
/*  93 */     int srcWidth = srcSize.getWidth();
/*  94 */     int srcHeight = srcSize.getHeight();
/*  95 */     int targetWidth = targetSize.getWidth();
/*  96 */     int targetHeight = targetSize.getHeight();
/*     */ 
/*  98 */     int scale = 1;
/*     */ 
/* 100 */     switch (1.$SwitchMap$io$rong$imageloader$core$assist$ViewScaleType[viewScaleType.ordinal()]) {
/*     */     case 1:
/* 102 */       if (powerOf2Scale) {
/* 103 */         int halfWidth = srcWidth / 2;
/* 104 */         int halfHeight = srcHeight / 2;
/* 105 */         while ((halfWidth / scale > targetWidth) || (halfHeight / scale > targetHeight))
/* 106 */           scale *= 2;
/*     */       }
/*     */       else {
/* 109 */         scale = Math.max(srcWidth / targetWidth, srcHeight / targetHeight);
/*     */       }
/* 111 */       break;
/*     */     case 2:
/* 113 */       if (powerOf2Scale) {
/* 114 */         int halfWidth = srcWidth / 2;
/* 115 */         int halfHeight = srcHeight / 2;
/* 116 */         while ((halfWidth / scale > targetWidth) && (halfHeight / scale > targetHeight))
/* 117 */           scale *= 2;
/*     */       }
/*     */       else {
/* 120 */         scale = Math.min(srcWidth / targetWidth, srcHeight / targetHeight);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 125 */     if (scale < 1) {
/* 126 */       scale = 1;
/*     */     }
/* 128 */     scale = considerMaxTextureSize(srcWidth, srcHeight, scale, powerOf2Scale);
/*     */ 
/* 130 */     return scale;
/*     */   }
/*     */ 
/*     */   private static int considerMaxTextureSize(int srcWidth, int srcHeight, int scale, boolean powerOf2) {
/* 134 */     int maxWidth = maxBitmapSize.getWidth();
/* 135 */     int maxHeight = maxBitmapSize.getHeight();
/* 136 */     while ((srcWidth / scale > maxWidth) || (srcHeight / scale > maxHeight)) {
/* 137 */       if (powerOf2) {
/* 138 */         scale *= 2; continue;
/*     */       }
/* 140 */       scale++;
/*     */     }
/*     */ 
/* 143 */     return scale;
/*     */   }
/*     */ 
/*     */   public static int computeMinImageSampleSize(ImageSize srcSize)
/*     */   {
/* 156 */     int srcWidth = srcSize.getWidth();
/* 157 */     int srcHeight = srcSize.getHeight();
/* 158 */     int targetWidth = maxBitmapSize.getWidth();
/* 159 */     int targetHeight = maxBitmapSize.getHeight();
/*     */ 
/* 161 */     int widthScale = (int)Math.ceil(srcWidth / targetWidth);
/* 162 */     int heightScale = (int)Math.ceil(srcHeight / targetHeight);
/*     */ 
/* 164 */     return Math.max(widthScale, heightScale);
/*     */   }
/*     */ 
/*     */   public static float computeImageScale(ImageSize srcSize, ImageSize targetSize, ViewScaleType viewScaleType, boolean stretch)
/*     */   {
/* 191 */     int srcWidth = srcSize.getWidth();
/* 192 */     int srcHeight = srcSize.getHeight();
/* 193 */     int targetWidth = targetSize.getWidth();
/* 194 */     int targetHeight = targetSize.getHeight();
/*     */ 
/* 196 */     float widthScale = srcWidth / targetWidth;
/* 197 */     float heightScale = srcHeight / targetHeight;
/*     */     int destHeight;
/*     */     int destWidth;
/*     */     int destHeight;
/* 201 */     if (((viewScaleType == ViewScaleType.FIT_INSIDE) && (widthScale >= heightScale)) || ((viewScaleType == ViewScaleType.CROP) && (widthScale < heightScale))) {
/* 202 */       int destWidth = targetWidth;
/* 203 */       destHeight = (int)(srcHeight / widthScale);
/*     */     } else {
/* 205 */       destWidth = (int)(srcWidth / heightScale);
/* 206 */       destHeight = targetHeight;
/*     */     }
/*     */ 
/* 209 */     float scale = 1.0F;
/* 210 */     if (((!stretch) && (destWidth < srcWidth) && (destHeight < srcHeight)) || ((stretch) && (destWidth != srcWidth) && (destHeight != srcHeight))) {
/* 211 */       scale = destWidth / srcWidth;
/*     */     }
/*     */ 
/* 214 */     return scale;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  40 */     int[] maxTextureSize = new int[1];
/*  41 */     GLES10.glGetIntegerv(3379, maxTextureSize, 0);
/*  42 */     int maxBitmapDimension = Math.max(maxTextureSize[0], 2048);
/*  43 */     maxBitmapSize = new ImageSize(maxBitmapDimension, maxBitmapDimension);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.utils.ImageSizeUtils
 * JD-Core Version:    0.6.0
 */
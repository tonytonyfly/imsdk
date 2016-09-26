/*     */ package io.rong.imageloader.core;
/*     */ 
/*     */ import android.content.res.Resources;
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.Bitmap.Config;
/*     */ import android.graphics.BitmapFactory.Options;
/*     */ import android.graphics.Canvas;
/*     */ import android.graphics.drawable.Drawable;
/*     */ import android.os.Handler;
/*     */ import io.rong.imageloader.core.assist.ImageScaleType;
/*     */ import io.rong.imageloader.core.display.BitmapDisplayer;
/*     */ import io.rong.imageloader.core.process.BitmapProcessor;
/*     */ 
/*     */ public final class DisplayImageOptions
/*     */ {
/*     */   private final int imageResOnLoading;
/*     */   private final int imageResForEmptyUri;
/*     */   private final int imageResOnFail;
/*     */   private final Drawable imageOnLoading;
/*     */   private final Drawable imageForEmptyUri;
/*     */   private final Drawable imageOnFail;
/*     */   private final boolean resetViewBeforeLoading;
/*     */   private final boolean cacheInMemory;
/*     */   private final boolean cacheOnDisk;
/*     */   private final ImageScaleType imageScaleType;
/*     */   private final BitmapFactory.Options decodingOptions;
/*     */   private final int delayBeforeLoading;
/*     */   private final boolean considerExifParams;
/*     */   private final Object extraForDownloader;
/*     */   private final BitmapProcessor preProcessor;
/*     */   private final BitmapProcessor postProcessor;
/*     */   private final BitmapDisplayer displayer;
/*     */   private final Handler handler;
/*     */   private final boolean isSyncLoading;
/*     */ 
/*     */   private DisplayImageOptions(Builder builder)
/*     */   {
/*  90 */     this.imageResOnLoading = builder.imageResOnLoading;
/*  91 */     this.imageResForEmptyUri = builder.imageResForEmptyUri;
/*  92 */     this.imageResOnFail = builder.imageResOnFail;
/*  93 */     this.imageOnLoading = builder.imageOnLoading;
/*  94 */     this.imageForEmptyUri = builder.imageForEmptyUri;
/*  95 */     this.imageOnFail = builder.imageOnFail;
/*  96 */     this.resetViewBeforeLoading = builder.resetViewBeforeLoading;
/*  97 */     this.cacheInMemory = builder.cacheInMemory;
/*  98 */     this.cacheOnDisk = builder.cacheOnDisk;
/*  99 */     this.imageScaleType = builder.imageScaleType;
/* 100 */     this.decodingOptions = builder.decodingOptions;
/* 101 */     this.delayBeforeLoading = builder.delayBeforeLoading;
/* 102 */     this.considerExifParams = builder.considerExifParams;
/* 103 */     this.extraForDownloader = builder.extraForDownloader;
/* 104 */     this.preProcessor = builder.preProcessor;
/* 105 */     this.postProcessor = builder.postProcessor;
/* 106 */     this.displayer = builder.displayer;
/* 107 */     this.handler = builder.handler;
/* 108 */     this.isSyncLoading = builder.isSyncLoading;
/*     */   }
/*     */ 
/*     */   public boolean shouldShowImageOnLoading() {
/* 112 */     return (this.imageOnLoading != null) || (this.imageResOnLoading != 0);
/*     */   }
/*     */ 
/*     */   public boolean shouldShowImageForEmptyUri() {
/* 116 */     return (this.imageForEmptyUri != null) || (this.imageResForEmptyUri != 0);
/*     */   }
/*     */ 
/*     */   public Bitmap drawableToBitmap(Drawable drawable)
/*     */   {
/* 121 */     int width = drawable.getIntrinsicWidth();
/* 122 */     int height = drawable.getIntrinsicHeight();
/* 123 */     if ((width <= 0) || (height <= 0)) {
/* 124 */       return null;
/*     */     }
/* 126 */     Bitmap.Config config = drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
/* 127 */     Bitmap bitmap = Bitmap.createBitmap(width, height, config);
/* 128 */     Canvas canvas = new Canvas(bitmap);
/* 129 */     drawable.setBounds(0, 0, width, height);
/* 130 */     drawable.draw(canvas);
/* 131 */     return bitmap;
/*     */   }
/*     */ 
/*     */   public boolean shouldShowImageOnFail() {
/* 135 */     return (this.imageOnFail != null) || (this.imageResOnFail != 0);
/*     */   }
/*     */ 
/*     */   public boolean shouldPreProcess() {
/* 139 */     return this.preProcessor != null;
/*     */   }
/*     */ 
/*     */   public boolean shouldPostProcess() {
/* 143 */     return this.postProcessor != null;
/*     */   }
/*     */ 
/*     */   public boolean shouldDelayBeforeLoading() {
/* 147 */     return this.delayBeforeLoading > 0;
/*     */   }
/*     */ 
/*     */   public Drawable getImageOnLoading(Resources res) {
/* 151 */     return this.imageResOnLoading != 0 ? res.getDrawable(this.imageResOnLoading) : this.imageOnLoading;
/*     */   }
/*     */ 
/*     */   public Drawable getImageForEmptyUri(Resources res) {
/* 155 */     return this.imageResForEmptyUri != 0 ? res.getDrawable(this.imageResForEmptyUri) : this.imageForEmptyUri;
/*     */   }
/*     */ 
/*     */   public Drawable getImageOnFail(Resources res) {
/* 159 */     return this.imageResOnFail != 0 ? res.getDrawable(this.imageResOnFail) : this.imageOnFail;
/*     */   }
/*     */ 
/*     */   public boolean isResetViewBeforeLoading() {
/* 163 */     return this.resetViewBeforeLoading;
/*     */   }
/*     */ 
/*     */   public boolean isCacheInMemory() {
/* 167 */     return this.cacheInMemory;
/*     */   }
/*     */ 
/*     */   public boolean isCacheOnDisk() {
/* 171 */     return this.cacheOnDisk;
/*     */   }
/*     */ 
/*     */   public ImageScaleType getImageScaleType() {
/* 175 */     return this.imageScaleType;
/*     */   }
/*     */ 
/*     */   public BitmapFactory.Options getDecodingOptions() {
/* 179 */     return this.decodingOptions;
/*     */   }
/*     */ 
/*     */   public int getDelayBeforeLoading() {
/* 183 */     return this.delayBeforeLoading;
/*     */   }
/*     */ 
/*     */   public boolean isConsiderExifParams() {
/* 187 */     return this.considerExifParams;
/*     */   }
/*     */ 
/*     */   public Object getExtraForDownloader() {
/* 191 */     return this.extraForDownloader;
/*     */   }
/*     */ 
/*     */   public BitmapProcessor getPreProcessor() {
/* 195 */     return this.preProcessor;
/*     */   }
/*     */ 
/*     */   public BitmapProcessor getPostProcessor() {
/* 199 */     return this.postProcessor;
/*     */   }
/*     */ 
/*     */   public BitmapDisplayer getDisplayer() {
/* 203 */     return this.displayer;
/*     */   }
/*     */ 
/*     */   public Handler getHandler() {
/* 207 */     return this.handler;
/*     */   }
/*     */ 
/*     */   boolean isSyncLoading() {
/* 211 */     return this.isSyncLoading;
/*     */   }
/*     */ 
/*     */   public static DisplayImageOptions createSimple()
/*     */   {
/* 519 */     return new Builder().build();
/*     */   }
/*     */ 
/*     */   public static class Builder
/*     */   {
/* 220 */     private int imageResOnLoading = 0;
/* 221 */     private int imageResForEmptyUri = 0;
/* 222 */     private int imageResOnFail = 0;
/* 223 */     private Drawable imageOnLoading = null;
/* 224 */     private Drawable imageForEmptyUri = null;
/* 225 */     private Drawable imageOnFail = null;
/* 226 */     private boolean resetViewBeforeLoading = false;
/* 227 */     private boolean cacheInMemory = false;
/* 228 */     private boolean cacheOnDisk = false;
/* 229 */     private ImageScaleType imageScaleType = ImageScaleType.IN_SAMPLE_POWER_OF_2;
/* 230 */     private BitmapFactory.Options decodingOptions = new BitmapFactory.Options();
/* 231 */     private int delayBeforeLoading = 0;
/* 232 */     private boolean considerExifParams = false;
/* 233 */     private Object extraForDownloader = null;
/* 234 */     private BitmapProcessor preProcessor = null;
/* 235 */     private BitmapProcessor postProcessor = null;
/* 236 */     private BitmapDisplayer displayer = DefaultConfigurationFactory.createBitmapDisplayer();
/* 237 */     private Handler handler = null;
/* 238 */     private boolean isSyncLoading = false;
/*     */ 
/*     */     @Deprecated
/*     */     public Builder showStubImage(int imageRes)
/*     */     {
/* 249 */       this.imageResOnLoading = imageRes;
/* 250 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder showImageOnLoading(int imageRes)
/*     */     {
/* 260 */       this.imageResOnLoading = imageRes;
/* 261 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder showImageOnLoading(Drawable drawable)
/*     */     {
/* 270 */       this.imageOnLoading = drawable;
/* 271 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder showImageForEmptyUri(int imageRes)
/*     */     {
/* 282 */       this.imageResForEmptyUri = imageRes;
/* 283 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder showImageForEmptyUri(Drawable drawable)
/*     */     {
/* 293 */       this.imageForEmptyUri = drawable;
/* 294 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder showImageOnFail(int imageRes)
/*     */     {
/* 305 */       this.imageResOnFail = imageRes;
/* 306 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder showImageOnFail(Drawable drawable)
/*     */     {
/* 316 */       this.imageOnFail = drawable;
/* 317 */       return this;
/*     */     }
/*     */ 
/*     */     /** @deprecated */
/*     */     public Builder resetViewBeforeLoading()
/*     */     {
/* 327 */       this.resetViewBeforeLoading = true;
/* 328 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder resetViewBeforeLoading(boolean resetViewBeforeLoading)
/*     */     {
/* 336 */       this.resetViewBeforeLoading = resetViewBeforeLoading;
/* 337 */       return this;
/*     */     }
/*     */ 
/*     */     @Deprecated
/*     */     public Builder cacheInMemory()
/*     */     {
/* 347 */       this.cacheInMemory = true;
/* 348 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder cacheInMemory(boolean cacheInMemory)
/*     */     {
/* 353 */       this.cacheInMemory = cacheInMemory;
/* 354 */       return this;
/*     */     }
/*     */ 
/*     */     @Deprecated
/*     */     public Builder cacheOnDisc()
/*     */     {
/* 364 */       return cacheOnDisk(true);
/*     */     }
/*     */ 
/*     */     @Deprecated
/*     */     public Builder cacheOnDisc(boolean cacheOnDisk)
/*     */     {
/* 374 */       return cacheOnDisk(cacheOnDisk);
/*     */     }
/*     */ 
/*     */     public Builder cacheOnDisk(boolean cacheOnDisk)
/*     */     {
/* 379 */       this.cacheOnDisk = cacheOnDisk;
/* 380 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder imageScaleType(ImageScaleType imageScaleType)
/*     */     {
/* 388 */       this.imageScaleType = imageScaleType;
/* 389 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder bitmapConfig(Bitmap.Config bitmapConfig)
/*     */     {
/* 394 */       if (bitmapConfig == null) throw new IllegalArgumentException("bitmapConfig can't be null");
/* 395 */       this.decodingOptions.inPreferredConfig = bitmapConfig;
/* 396 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder decodingOptions(BitmapFactory.Options decodingOptions)
/*     */     {
/* 408 */       if (decodingOptions == null) throw new IllegalArgumentException("decodingOptions can't be null");
/* 409 */       this.decodingOptions = decodingOptions;
/* 410 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder delayBeforeLoading(int delayInMillis)
/*     */     {
/* 415 */       this.delayBeforeLoading = delayInMillis;
/* 416 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder extraForDownloader(Object extra)
/*     */     {
/* 421 */       this.extraForDownloader = extra;
/* 422 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder considerExifParams(boolean considerExifParams)
/*     */     {
/* 427 */       this.considerExifParams = considerExifParams;
/* 428 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder preProcessor(BitmapProcessor preProcessor)
/*     */     {
/* 437 */       this.preProcessor = preProcessor;
/* 438 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder postProcessor(BitmapProcessor postProcessor)
/*     */     {
/* 447 */       this.postProcessor = postProcessor;
/* 448 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder displayer(BitmapDisplayer displayer)
/*     */     {
/* 456 */       if (displayer == null) throw new IllegalArgumentException("displayer can't be null");
/* 457 */       this.displayer = displayer;
/* 458 */       return this;
/*     */     }
/*     */ 
/*     */     Builder syncLoading(boolean isSyncLoading) {
/* 462 */       this.isSyncLoading = isSyncLoading;
/* 463 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder handler(Handler handler)
/*     */     {
/* 471 */       this.handler = handler;
/* 472 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder cloneFrom(DisplayImageOptions options)
/*     */     {
/* 477 */       this.imageResOnLoading = options.imageResOnLoading;
/* 478 */       this.imageResForEmptyUri = options.imageResForEmptyUri;
/* 479 */       this.imageResOnFail = options.imageResOnFail;
/* 480 */       this.imageOnLoading = options.imageOnLoading;
/* 481 */       this.imageForEmptyUri = options.imageForEmptyUri;
/* 482 */       this.imageOnFail = options.imageOnFail;
/* 483 */       this.resetViewBeforeLoading = options.resetViewBeforeLoading;
/* 484 */       this.cacheInMemory = options.cacheInMemory;
/* 485 */       this.cacheOnDisk = options.cacheOnDisk;
/* 486 */       this.imageScaleType = options.imageScaleType;
/* 487 */       this.decodingOptions = options.decodingOptions;
/* 488 */       this.delayBeforeLoading = options.delayBeforeLoading;
/* 489 */       this.considerExifParams = options.considerExifParams;
/* 490 */       this.extraForDownloader = options.extraForDownloader;
/* 491 */       this.preProcessor = options.preProcessor;
/* 492 */       this.postProcessor = options.postProcessor;
/* 493 */       this.displayer = options.displayer;
/* 494 */       this.handler = options.handler;
/* 495 */       this.isSyncLoading = options.isSyncLoading;
/* 496 */       return this;
/*     */     }
/*     */ 
/*     */     public DisplayImageOptions build()
/*     */     {
/* 501 */       return new DisplayImageOptions(this, null);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.DisplayImageOptions
 * JD-Core Version:    0.6.0
 */
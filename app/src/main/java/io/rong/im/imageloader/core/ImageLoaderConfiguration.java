/*     */ package io.rong.imageloader.core;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.res.Resources;
/*     */ import android.util.DisplayMetrics;
/*     */ import io.rong.imageloader.cache.disc.DiskCache;
/*     */ import io.rong.imageloader.cache.disc.naming.FileNameGenerator;
/*     */ import io.rong.imageloader.cache.memory.MemoryCache;
/*     */ import io.rong.imageloader.cache.memory.impl.FuzzyKeyMemoryCache;
/*     */ import io.rong.imageloader.core.assist.FlushedInputStream;
/*     */ import io.rong.imageloader.core.assist.ImageSize;
/*     */ import io.rong.imageloader.core.assist.QueueProcessingType;
/*     */ import io.rong.imageloader.core.decode.ImageDecoder;
/*     */ import io.rong.imageloader.core.download.ImageDownloader;
/*     */ import io.rong.imageloader.core.process.BitmapProcessor;
/*     */ import io.rong.imageloader.utils.L;
/*     */ import io.rong.imageloader.utils.MemoryCacheUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.concurrent.Executor;
/*     */ 
/*     */ public final class ImageLoaderConfiguration
/*     */ {
/*     */   final Resources resources;
/*     */   final int maxImageWidthForMemoryCache;
/*     */   final int maxImageHeightForMemoryCache;
/*     */   final int maxImageWidthForDiskCache;
/*     */   final int maxImageHeightForDiskCache;
/*     */   final BitmapProcessor processorForDiskCache;
/*     */   final Executor taskExecutor;
/*     */   final Executor taskExecutorForCachedImages;
/*     */   final boolean customExecutor;
/*     */   final boolean customExecutorForCachedImages;
/*     */   final int threadPoolSize;
/*     */   final int threadPriority;
/*     */   final QueueProcessingType tasksProcessingType;
/*     */   final MemoryCache memoryCache;
/*     */   final DiskCache diskCache;
/*     */   final ImageDownloader downloader;
/*     */   final ImageDecoder decoder;
/*     */   final DisplayImageOptions defaultDisplayImageOptions;
/*     */   final ImageDownloader networkDeniedDownloader;
/*     */   final ImageDownloader slowNetworkDownloader;
/*     */ 
/*     */   private ImageLoaderConfiguration(Builder builder)
/*     */   {
/*  79 */     this.resources = builder.context.getResources();
/*  80 */     this.maxImageWidthForMemoryCache = builder.maxImageWidthForMemoryCache;
/*  81 */     this.maxImageHeightForMemoryCache = builder.maxImageHeightForMemoryCache;
/*  82 */     this.maxImageWidthForDiskCache = builder.maxImageWidthForDiskCache;
/*  83 */     this.maxImageHeightForDiskCache = builder.maxImageHeightForDiskCache;
/*  84 */     this.processorForDiskCache = builder.processorForDiskCache;
/*  85 */     this.taskExecutor = builder.taskExecutor;
/*  86 */     this.taskExecutorForCachedImages = builder.taskExecutorForCachedImages;
/*  87 */     this.threadPoolSize = builder.threadPoolSize;
/*  88 */     this.threadPriority = builder.threadPriority;
/*  89 */     this.tasksProcessingType = builder.tasksProcessingType;
/*  90 */     this.diskCache = builder.diskCache;
/*  91 */     this.memoryCache = builder.memoryCache;
/*  92 */     this.defaultDisplayImageOptions = builder.defaultDisplayImageOptions;
/*  93 */     this.downloader = builder.downloader;
/*  94 */     this.decoder = builder.decoder;
/*     */ 
/*  96 */     this.customExecutor = builder.customExecutor;
/*  97 */     this.customExecutorForCachedImages = builder.customExecutorForCachedImages;
/*     */ 
/*  99 */     this.networkDeniedDownloader = new NetworkDeniedImageDownloader(this.downloader);
/* 100 */     this.slowNetworkDownloader = new SlowNetworkImageDownloader(this.downloader);
/*     */ 
/* 102 */     L.writeDebugLogs(builder.writeLogs);
/*     */   }
/*     */ 
/*     */   public static ImageLoaderConfiguration createDefault(Context context)
/*     */   {
/* 127 */     return new Builder(context).build();
/*     */   }
/*     */ 
/*     */   ImageSize getMaxImageSize() {
/* 131 */     DisplayMetrics displayMetrics = this.resources.getDisplayMetrics();
/*     */ 
/* 133 */     int width = this.maxImageWidthForMemoryCache;
/* 134 */     if (width <= 0) {
/* 135 */       width = displayMetrics.widthPixels;
/*     */     }
/* 137 */     int height = this.maxImageHeightForMemoryCache;
/* 138 */     if (height <= 0) {
/* 139 */       height = displayMetrics.heightPixels;
/*     */     }
/* 141 */     return new ImageSize(width, height);
/*     */   }
/*     */ 
/*     */   private static class SlowNetworkImageDownloader
/*     */     implements ImageDownloader
/*     */   {
/*     */     private final ImageDownloader wrappedDownloader;
/*     */ 
/*     */     public SlowNetworkImageDownloader(ImageDownloader wrappedDownloader)
/*     */     {
/* 640 */       this.wrappedDownloader = wrappedDownloader;
/*     */     }
/*     */ 
/*     */     public InputStream getStream(String imageUri, Object extra) throws IOException
/*     */     {
/* 645 */       InputStream imageStream = this.wrappedDownloader.getStream(imageUri, extra);
/* 646 */       switch (ImageLoaderConfiguration.1.$SwitchMap$io$rong$imageloader$core$download$ImageDownloader$Scheme[io.rong.imageloader.core.download.ImageDownloader.Scheme.ofUri(imageUri).ordinal()]) {
/*     */       case 1:
/*     */       case 2:
/* 649 */         return new FlushedInputStream(imageStream);
/*     */       }
/* 651 */       return imageStream;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class NetworkDeniedImageDownloader
/*     */     implements ImageDownloader
/*     */   {
/*     */     private final ImageDownloader wrappedDownloader;
/*     */ 
/*     */     public NetworkDeniedImageDownloader(ImageDownloader wrappedDownloader)
/*     */     {
/* 613 */       this.wrappedDownloader = wrappedDownloader;
/*     */     }
/*     */ 
/*     */     public InputStream getStream(String imageUri, Object extra) throws IOException
/*     */     {
/* 618 */       switch (ImageLoaderConfiguration.1.$SwitchMap$io$rong$imageloader$core$download$ImageDownloader$Scheme[io.rong.imageloader.core.download.ImageDownloader.Scheme.ofUri(imageUri).ordinal()]) {
/*     */       case 1:
/*     */       case 2:
/* 621 */         throw new IllegalStateException();
/*     */       }
/* 623 */       return this.wrappedDownloader.getStream(imageUri, extra);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Builder
/*     */   {
/*     */     private static final String WARNING_OVERLAP_DISK_CACHE_PARAMS = "diskCache(), diskCacheSize() and diskCacheFileCount calls overlap each other";
/*     */     private static final String WARNING_OVERLAP_DISK_CACHE_NAME_GENERATOR = "diskCache() and diskCacheFileNameGenerator() calls overlap each other";
/*     */     private static final String WARNING_OVERLAP_MEMORY_CACHE = "memoryCache() and memoryCacheSize() calls overlap each other";
/*     */     private static final String WARNING_OVERLAP_EXECUTOR = "threadPoolSize(), threadPriority() and tasksProcessingOrder() calls can overlap taskExecutor() and taskExecutorForCachedImages() calls.";
/*     */     public static final int DEFAULT_THREAD_POOL_SIZE = 3;
/*     */     public static final int DEFAULT_THREAD_PRIORITY = 3;
/* 162 */     public static final QueueProcessingType DEFAULT_TASK_PROCESSING_TYPE = QueueProcessingType.FIFO;
/*     */     private Context context;
/* 166 */     private int maxImageWidthForMemoryCache = 0;
/* 167 */     private int maxImageHeightForMemoryCache = 0;
/* 168 */     private int maxImageWidthForDiskCache = 0;
/* 169 */     private int maxImageHeightForDiskCache = 0;
/* 170 */     private BitmapProcessor processorForDiskCache = null;
/*     */ 
/* 172 */     private Executor taskExecutor = null;
/* 173 */     private Executor taskExecutorForCachedImages = null;
/* 174 */     private boolean customExecutor = false;
/* 175 */     private boolean customExecutorForCachedImages = false;
/*     */ 
/* 177 */     private int threadPoolSize = 3;
/* 178 */     private int threadPriority = 3;
/* 179 */     private boolean denyCacheImageMultipleSizesInMemory = false;
/* 180 */     private QueueProcessingType tasksProcessingType = DEFAULT_TASK_PROCESSING_TYPE;
/*     */ 
/* 182 */     private int memoryCacheSize = 0;
/* 183 */     private long diskCacheSize = 0L;
/* 184 */     private int diskCacheFileCount = 0;
/*     */ 
/* 186 */     private MemoryCache memoryCache = null;
/* 187 */     private DiskCache diskCache = null;
/* 188 */     private FileNameGenerator diskCacheFileNameGenerator = null;
/* 189 */     private ImageDownloader downloader = null;
/*     */     private ImageDecoder decoder;
/* 191 */     private DisplayImageOptions defaultDisplayImageOptions = null;
/*     */ 
/* 193 */     private boolean writeLogs = false;
/*     */ 
/*     */     public Builder(Context context) {
/* 196 */       this.context = context.getApplicationContext();
/*     */     }
/*     */ 
/*     */     public Builder memoryCacheExtraOptions(int maxImageWidthForMemoryCache, int maxImageHeightForMemoryCache)
/*     */     {
/* 208 */       this.maxImageWidthForMemoryCache = maxImageWidthForMemoryCache;
/* 209 */       this.maxImageHeightForMemoryCache = maxImageHeightForMemoryCache;
/* 210 */       return this;
/*     */     }
/*     */ 
/*     */     @Deprecated
/*     */     public Builder discCacheExtraOptions(int maxImageWidthForDiskCache, int maxImageHeightForDiskCache, BitmapProcessor processorForDiskCache)
/*     */     {
/* 221 */       return diskCacheExtraOptions(maxImageWidthForDiskCache, maxImageHeightForDiskCache, processorForDiskCache);
/*     */     }
/*     */ 
/*     */     public Builder diskCacheExtraOptions(int maxImageWidthForDiskCache, int maxImageHeightForDiskCache, BitmapProcessor processorForDiskCache)
/*     */     {
/* 234 */       this.maxImageWidthForDiskCache = maxImageWidthForDiskCache;
/* 235 */       this.maxImageHeightForDiskCache = maxImageHeightForDiskCache;
/* 236 */       this.processorForDiskCache = processorForDiskCache;
/* 237 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder taskExecutor(Executor executor)
/*     */     {
/* 254 */       if ((this.threadPoolSize != 3) || (this.threadPriority != 3) || (this.tasksProcessingType != DEFAULT_TASK_PROCESSING_TYPE)) {
/* 255 */         L.w("threadPoolSize(), threadPriority() and tasksProcessingOrder() calls can overlap taskExecutor() and taskExecutorForCachedImages() calls.", new Object[0]);
/*     */       }
/*     */ 
/* 258 */       this.taskExecutor = executor;
/* 259 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder taskExecutorForCachedImages(Executor executorForCachedImages)
/*     */     {
/* 281 */       if ((this.threadPoolSize != 3) || (this.threadPriority != 3) || (this.tasksProcessingType != DEFAULT_TASK_PROCESSING_TYPE)) {
/* 282 */         L.w("threadPoolSize(), threadPriority() and tasksProcessingOrder() calls can overlap taskExecutor() and taskExecutorForCachedImages() calls.", new Object[0]);
/*     */       }
/*     */ 
/* 285 */       this.taskExecutorForCachedImages = executorForCachedImages;
/* 286 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder threadPoolSize(int threadPoolSize)
/*     */     {
/* 294 */       if ((this.taskExecutor != null) || (this.taskExecutorForCachedImages != null)) {
/* 295 */         L.w("threadPoolSize(), threadPriority() and tasksProcessingOrder() calls can overlap taskExecutor() and taskExecutorForCachedImages() calls.", new Object[0]);
/*     */       }
/*     */ 
/* 298 */       this.threadPoolSize = threadPoolSize;
/* 299 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder threadPriority(int threadPriority)
/*     */     {
/* 308 */       if ((this.taskExecutor != null) || (this.taskExecutorForCachedImages != null)) {
/* 309 */         L.w("threadPoolSize(), threadPriority() and tasksProcessingOrder() calls can overlap taskExecutor() and taskExecutorForCachedImages() calls.", new Object[0]);
/*     */       }
/*     */ 
/* 312 */       if (threadPriority < 1) {
/* 313 */         this.threadPriority = 1;
/*     */       }
/* 315 */       else if (threadPriority > 10)
/* 316 */         this.threadPriority = 10;
/*     */       else {
/* 318 */         this.threadPriority = threadPriority;
/*     */       }
/*     */ 
/* 321 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder denyCacheImageMultipleSizesInMemory()
/*     */     {
/* 333 */       this.denyCacheImageMultipleSizesInMemory = true;
/* 334 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder tasksProcessingOrder(QueueProcessingType tasksProcessingType)
/*     */     {
/* 342 */       if ((this.taskExecutor != null) || (this.taskExecutorForCachedImages != null)) {
/* 343 */         L.w("threadPoolSize(), threadPriority() and tasksProcessingOrder() calls can overlap taskExecutor() and taskExecutorForCachedImages() calls.", new Object[0]);
/*     */       }
/*     */ 
/* 346 */       this.tasksProcessingType = tasksProcessingType;
/* 347 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder memoryCacheSize(int memoryCacheSize)
/*     */     {
/* 359 */       if (memoryCacheSize <= 0) throw new IllegalArgumentException("memoryCacheSize must be a positive number");
/*     */ 
/* 361 */       if (this.memoryCache != null) {
/* 362 */         L.w("memoryCache() and memoryCacheSize() calls overlap each other", new Object[0]);
/*     */       }
/*     */ 
/* 365 */       this.memoryCacheSize = memoryCacheSize;
/* 366 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder memoryCacheSizePercentage(int availableMemoryPercent)
/*     */     {
/* 379 */       if ((availableMemoryPercent <= 0) || (availableMemoryPercent >= 100)) {
/* 380 */         throw new IllegalArgumentException("availableMemoryPercent must be in range (0 < % < 100)");
/*     */       }
/*     */ 
/* 383 */       if (this.memoryCache != null) {
/* 384 */         L.w("memoryCache() and memoryCacheSize() calls overlap each other", new Object[0]);
/*     */       }
/*     */ 
/* 387 */       long availableMemory = Runtime.getRuntime().maxMemory();
/* 388 */       this.memoryCacheSize = (int)((float)availableMemory * (availableMemoryPercent / 100.0F));
/* 389 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder memoryCache(MemoryCache memoryCache)
/*     */     {
/* 403 */       if (this.memoryCacheSize != 0) {
/* 404 */         L.w("memoryCache() and memoryCacheSize() calls overlap each other", new Object[0]);
/*     */       }
/*     */ 
/* 407 */       this.memoryCache = memoryCache;
/* 408 */       return this;
/*     */     }
/*     */ 
/*     */     @Deprecated
/*     */     public Builder discCacheSize(int maxCacheSize) {
/* 414 */       return diskCacheSize(maxCacheSize);
/*     */     }
/*     */ 
/*     */     public Builder diskCacheSize(int maxCacheSize)
/*     */     {
/* 426 */       if (maxCacheSize <= 0) throw new IllegalArgumentException("maxCacheSize must be a positive number");
/*     */ 
/* 428 */       if (this.diskCache != null) {
/* 429 */         L.w("diskCache(), diskCacheSize() and diskCacheFileCount calls overlap each other", new Object[0]);
/*     */       }
/*     */ 
/* 432 */       this.diskCacheSize = maxCacheSize;
/* 433 */       return this;
/*     */     }
/*     */ 
/*     */     @Deprecated
/*     */     public Builder discCacheFileCount(int maxFileCount) {
/* 439 */       return diskCacheFileCount(maxFileCount);
/*     */     }
/*     */ 
/*     */     public Builder diskCacheFileCount(int maxFileCount)
/*     */     {
/* 451 */       if (maxFileCount <= 0) throw new IllegalArgumentException("maxFileCount must be a positive number");
/*     */ 
/* 453 */       if (this.diskCache != null) {
/* 454 */         L.w("diskCache(), diskCacheSize() and diskCacheFileCount calls overlap each other", new Object[0]);
/*     */       }
/*     */ 
/* 457 */       this.diskCacheFileCount = maxFileCount;
/* 458 */       return this;
/*     */     }
/*     */ 
/*     */     @Deprecated
/*     */     public Builder discCacheFileNameGenerator(FileNameGenerator fileNameGenerator) {
/* 464 */       return diskCacheFileNameGenerator(fileNameGenerator);
/*     */     }
/*     */ 
/*     */     public Builder diskCacheFileNameGenerator(FileNameGenerator fileNameGenerator)
/*     */     {
/* 474 */       if (this.diskCache != null) {
/* 475 */         L.w("diskCache() and diskCacheFileNameGenerator() calls overlap each other", new Object[0]);
/*     */       }
/*     */ 
/* 478 */       this.diskCacheFileNameGenerator = fileNameGenerator;
/* 479 */       return this;
/*     */     }
/*     */ 
/*     */     @Deprecated
/*     */     public Builder discCache(DiskCache diskCache) {
/* 485 */       return diskCache(diskCache);
/*     */     }
/*     */ 
/*     */     public Builder diskCache(DiskCache diskCache)
/*     */     {
/* 503 */       if ((this.diskCacheSize > 0L) || (this.diskCacheFileCount > 0)) {
/* 504 */         L.w("diskCache(), diskCacheSize() and diskCacheFileCount calls overlap each other", new Object[0]);
/*     */       }
/* 506 */       if (this.diskCacheFileNameGenerator != null) {
/* 507 */         L.w("diskCache() and diskCacheFileNameGenerator() calls overlap each other", new Object[0]);
/*     */       }
/*     */ 
/* 510 */       this.diskCache = diskCache;
/* 511 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder imageDownloader(ImageDownloader imageDownloader)
/*     */     {
/* 521 */       this.downloader = imageDownloader;
/* 522 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder imageDecoder(ImageDecoder imageDecoder)
/*     */     {
/* 532 */       this.decoder = imageDecoder;
/* 533 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder defaultDisplayImageOptions(DisplayImageOptions defaultDisplayImageOptions)
/*     */     {
/* 543 */       this.defaultDisplayImageOptions = defaultDisplayImageOptions;
/* 544 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder writeDebugLogs()
/*     */     {
/* 553 */       this.writeLogs = true;
/* 554 */       return this;
/*     */     }
/*     */ 
/*     */     public ImageLoaderConfiguration build()
/*     */     {
/* 559 */       initEmptyFieldsWithDefaultValues();
/* 560 */       return new ImageLoaderConfiguration(this, null);
/*     */     }
/*     */ 
/*     */     private void initEmptyFieldsWithDefaultValues() {
/* 564 */       if (this.taskExecutor == null) {
/* 565 */         this.taskExecutor = DefaultConfigurationFactory.createExecutor(this.threadPoolSize, this.threadPriority, this.tasksProcessingType);
/*     */       }
/*     */       else {
/* 568 */         this.customExecutor = true;
/*     */       }
/* 570 */       if (this.taskExecutorForCachedImages == null) {
/* 571 */         this.taskExecutorForCachedImages = DefaultConfigurationFactory.createExecutor(this.threadPoolSize, this.threadPriority, this.tasksProcessingType);
/*     */       }
/*     */       else {
/* 574 */         this.customExecutorForCachedImages = true;
/*     */       }
/* 576 */       if (this.diskCache == null) {
/* 577 */         if (this.diskCacheFileNameGenerator == null) {
/* 578 */           this.diskCacheFileNameGenerator = DefaultConfigurationFactory.createFileNameGenerator();
/*     */         }
/* 580 */         this.diskCache = DefaultConfigurationFactory.createDiskCache(this.context, this.diskCacheFileNameGenerator, this.diskCacheSize, this.diskCacheFileCount);
/*     */       }
/*     */ 
/* 583 */       if (this.memoryCache == null) {
/* 584 */         this.memoryCache = DefaultConfigurationFactory.createMemoryCache(this.context, this.memoryCacheSize);
/*     */       }
/* 586 */       if (this.denyCacheImageMultipleSizesInMemory) {
/* 587 */         this.memoryCache = new FuzzyKeyMemoryCache(this.memoryCache, MemoryCacheUtils.createFuzzyKeyComparator());
/*     */       }
/* 589 */       if (this.downloader == null) {
/* 590 */         this.downloader = DefaultConfigurationFactory.createImageDownloader(this.context);
/*     */       }
/* 592 */       if (this.decoder == null) {
/* 593 */         this.decoder = DefaultConfigurationFactory.createImageDecoder(this.writeLogs);
/*     */       }
/* 595 */       if (this.defaultDisplayImageOptions == null)
/* 596 */         this.defaultDisplayImageOptions = DisplayImageOptions.createSimple();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.ImageLoaderConfiguration
 * JD-Core Version:    0.6.0
 */
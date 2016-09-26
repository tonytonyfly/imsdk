/*     */ package io.rong.imageloader.core;
/*     */ 
/*     */ import android.graphics.Bitmap;
/*     */ import android.os.Handler;
/*     */ import io.rong.imageloader.cache.disc.DiskCache;
/*     */ import io.rong.imageloader.cache.memory.MemoryCache;
/*     */ import io.rong.imageloader.core.assist.FailReason;
/*     */ import io.rong.imageloader.core.assist.FailReason.FailType;
/*     */ import io.rong.imageloader.core.assist.ImageScaleType;
/*     */ import io.rong.imageloader.core.assist.ImageSize;
/*     */ import io.rong.imageloader.core.assist.LoadedFrom;
/*     */ import io.rong.imageloader.core.assist.ViewScaleType;
/*     */ import io.rong.imageloader.core.decode.ImageDecoder;
/*     */ import io.rong.imageloader.core.decode.ImageDecodingInfo;
/*     */ import io.rong.imageloader.core.display.BitmapDisplayer;
/*     */ import io.rong.imageloader.core.download.ImageDownloader;
/*     */ import io.rong.imageloader.core.download.ImageDownloader.Scheme;
/*     */ import io.rong.imageloader.core.imageaware.ImageAware;
/*     */ import io.rong.imageloader.core.listener.ImageLoadingListener;
/*     */ import io.rong.imageloader.core.listener.ImageLoadingProgressListener;
/*     */ import io.rong.imageloader.core.process.BitmapProcessor;
/*     */ import io.rong.imageloader.utils.IoUtils;
/*     */ import io.rong.imageloader.utils.IoUtils.CopyListener;
/*     */ import io.rong.imageloader.utils.L;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ 
/*     */ final class LoadAndDisplayImageTask
/*     */   implements Runnable, IoUtils.CopyListener
/*     */ {
/*     */   private static final String LOG_WAITING_FOR_RESUME = "ImageLoader is paused. Waiting...  [%s]";
/*     */   private static final String LOG_RESUME_AFTER_PAUSE = ".. Resume loading [%s]";
/*     */   private static final String LOG_DELAY_BEFORE_LOADING = "Delay %d ms before loading...  [%s]";
/*     */   private static final String LOG_START_DISPLAY_IMAGE_TASK = "Start display image task [%s]";
/*     */   private static final String LOG_WAITING_FOR_IMAGE_LOADED = "Image already is loading. Waiting... [%s]";
/*     */   private static final String LOG_GET_IMAGE_FROM_MEMORY_CACHE_AFTER_WAITING = "...Get cached bitmap from memory after waiting. [%s]";
/*     */   private static final String LOG_LOAD_IMAGE_FROM_NETWORK = "Load image from network [%s]";
/*     */   private static final String LOG_LOAD_IMAGE_FROM_DISK_CACHE = "Load image from disk cache [%s]";
/*     */   private static final String LOG_RESIZE_CACHED_IMAGE_FILE = "Resize image in disk cache [%s]";
/*     */   private static final String LOG_PREPROCESS_IMAGE = "PreProcess image before caching in memory [%s]";
/*     */   private static final String LOG_POSTPROCESS_IMAGE = "PostProcess image before displaying [%s]";
/*     */   private static final String LOG_CACHE_IMAGE_IN_MEMORY = "Cache image in memory [%s]";
/*     */   private static final String LOG_CACHE_IMAGE_ON_DISK = "Cache image on disk [%s]";
/*     */   private static final String LOG_PROCESS_IMAGE_BEFORE_CACHE_ON_DISK = "Process image before cache on disk [%s]";
/*     */   private static final String LOG_TASK_CANCELLED_IMAGEAWARE_REUSED = "ImageAware is reused for another image. Task is cancelled. [%s]";
/*     */   private static final String LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED = "ImageAware was collected by GC. Task is cancelled. [%s]";
/*     */   private static final String LOG_TASK_INTERRUPTED = "Task was interrupted [%s]";
/*     */   private static final String ERROR_NO_IMAGE_STREAM = "No stream for image [%s]";
/*     */   private static final String ERROR_PRE_PROCESSOR_NULL = "Pre-processor returned null [%s]";
/*     */   private static final String ERROR_POST_PROCESSOR_NULL = "Post-processor returned null [%s]";
/*     */   private static final String ERROR_PROCESSOR_FOR_DISK_CACHE_NULL = "Bitmap processor for disk cache returned null [%s]";
/*     */   private final ImageLoaderEngine engine;
/*     */   private final ImageLoadingInfo imageLoadingInfo;
/*     */   private final Handler handler;
/*     */   private final ImageLoaderConfiguration configuration;
/*     */   private final ImageDownloader downloader;
/*     */   private final ImageDownloader networkDeniedDownloader;
/*     */   private final ImageDownloader slowNetworkDownloader;
/*     */   private final ImageDecoder decoder;
/*     */   final String uri;
/*     */   private final String memoryCacheKey;
/*     */   final ImageAware imageAware;
/*     */   private final ImageSize targetSize;
/*     */   final DisplayImageOptions options;
/*     */   final ImageLoadingListener listener;
/*     */   final ImageLoadingProgressListener progressListener;
/*     */   private final boolean syncLoading;
/*  96 */   private LoadedFrom loadedFrom = LoadedFrom.NETWORK;
/*     */ 
/*     */   public LoadAndDisplayImageTask(ImageLoaderEngine engine, ImageLoadingInfo imageLoadingInfo, Handler handler) {
/*  99 */     this.engine = engine;
/* 100 */     this.imageLoadingInfo = imageLoadingInfo;
/* 101 */     this.handler = handler;
/*     */ 
/* 103 */     this.configuration = engine.configuration;
/* 104 */     this.downloader = this.configuration.downloader;
/* 105 */     this.networkDeniedDownloader = this.configuration.networkDeniedDownloader;
/* 106 */     this.slowNetworkDownloader = this.configuration.slowNetworkDownloader;
/* 107 */     this.decoder = this.configuration.decoder;
/* 108 */     this.uri = imageLoadingInfo.uri;
/* 109 */     this.memoryCacheKey = imageLoadingInfo.memoryCacheKey;
/* 110 */     this.imageAware = imageLoadingInfo.imageAware;
/* 111 */     this.targetSize = imageLoadingInfo.targetSize;
/* 112 */     this.options = imageLoadingInfo.options;
/* 113 */     this.listener = imageLoadingInfo.listener;
/* 114 */     this.progressListener = imageLoadingInfo.progressListener;
/* 115 */     this.syncLoading = this.options.isSyncLoading();
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 120 */     if (waitIfPaused()) return;
/* 121 */     if (delayIfNeed()) return;
/*     */ 
/* 123 */     ReentrantLock loadFromUriLock = this.imageLoadingInfo.loadFromUriLock;
/* 124 */     L.d("Start display image task [%s]", new Object[] { this.memoryCacheKey });
/* 125 */     if (loadFromUriLock.isLocked()) {
/* 126 */       L.d("Image already is loading. Waiting... [%s]", new Object[] { this.memoryCacheKey });
/* 129 */     }
/*     */ loadFromUriLock.lock();
/*     */     Bitmap bmp;
/*     */     try { checkTaskNotActual();
/*     */ 
/* 134 */       bmp = this.configuration.memoryCache.get(this.memoryCacheKey);
/* 135 */       if ((bmp == null) || (bmp.isRecycled())) {
/* 136 */         bmp = tryLoadBitmap();
/* 137 */         if (bmp == null) return;
/* 139 */         checkTaskNotActual();
/* 140 */         checkTaskInterrupted();
/*     */ 
/* 142 */         if (this.options.shouldPreProcess()) {
/* 143 */           L.d("PreProcess image before caching in memory [%s]", new Object[] { this.memoryCacheKey });
/* 144 */           bmp = this.options.getPreProcessor().process(bmp);
/* 145 */           if (bmp == null) {
/* 146 */             L.e("Pre-processor returned null [%s]", new Object[] { this.memoryCacheKey });
/*     */           }
/*     */         }
/*     */ 
/* 150 */         if ((bmp != null) && (this.options.isCacheInMemory())) {
/* 151 */           L.d("Cache image in memory [%s]", new Object[] { this.memoryCacheKey });
/* 152 */           this.configuration.memoryCache.put(this.memoryCacheKey, bmp);
/*     */         }
/*     */       } else {
/* 155 */         this.loadedFrom = LoadedFrom.MEMORY_CACHE;
/* 156 */         L.d("...Get cached bitmap from memory after waiting. [%s]", new Object[] { this.memoryCacheKey });
/*     */       }
/*     */ 
/* 159 */       if ((bmp != null) && (this.options.shouldPostProcess())) {
/* 160 */         L.d("PostProcess image before displaying [%s]", new Object[] { this.memoryCacheKey });
/* 161 */         bmp = this.options.getPostProcessor().process(bmp);
/* 162 */         if (bmp == null) {
/* 163 */           L.e("Post-processor returned null [%s]", new Object[] { this.memoryCacheKey });
/*     */         }
/*     */       }
/* 166 */       checkTaskNotActual();
/* 167 */       checkTaskInterrupted(); } catch (TaskCancelledException e) { fireCancelEvent();
/*     */       return;
/*     */     } finally {
/* 172 */       loadFromUriLock.unlock();
/*     */     }
/*     */ 
/* 175 */     DisplayBitmapTask displayBitmapTask = new DisplayBitmapTask(bmp, this.imageLoadingInfo, this.engine, this.loadedFrom);
/* 176 */     runTask(displayBitmapTask, this.syncLoading, this.handler, this.engine);
/*     */   }
/*     */ 
/*     */   private boolean waitIfPaused()
/*     */   {
/* 181 */     AtomicBoolean pause = this.engine.getPause();
/* 182 */     if (pause.get()) {
/* 183 */       synchronized (this.engine.getPauseLock()) {
/* 184 */         if (pause.get()) {
/* 185 */           L.d("ImageLoader is paused. Waiting...  [%s]", new Object[] { this.memoryCacheKey });
/*     */           try {
/* 187 */             this.engine.getPauseLock().wait();
/*     */           } catch (InterruptedException e) {
/* 189 */             L.e("Task was interrupted [%s]", new Object[] { this.memoryCacheKey });
/* 190 */             return true;
/*     */           }
/* 192 */           L.d(".. Resume loading [%s]", new Object[] { this.memoryCacheKey });
/*     */         }
/*     */       }
/*     */     }
/* 196 */     return isTaskNotActual();
/*     */   }
/*     */ 
/*     */   private boolean delayIfNeed()
/*     */   {
/* 201 */     if (this.options.shouldDelayBeforeLoading()) {
/* 202 */       L.d("Delay %d ms before loading...  [%s]", new Object[] { Integer.valueOf(this.options.getDelayBeforeLoading()), this.memoryCacheKey });
/*     */       try {
/* 204 */         Thread.sleep(this.options.getDelayBeforeLoading());
/*     */       } catch (InterruptedException e) {
/* 206 */         L.e("Task was interrupted [%s]", new Object[] { this.memoryCacheKey });
/* 207 */         return true;
/*     */       }
/* 209 */       return isTaskNotActual();
/*     */     }
/* 211 */     return false;
/*     */   }
/*     */ 
/*     */   private Bitmap tryLoadBitmap() throws LoadAndDisplayImageTask.TaskCancelledException {
/* 215 */     Bitmap bitmap = null;
/*     */     try {
/* 217 */       File imageFile = this.configuration.diskCache.get(this.uri);
/* 218 */       if ((imageFile != null) && (imageFile.exists()) && (imageFile.length() > 0L)) {
/* 219 */         L.d("Load image from disk cache [%s]", new Object[] { this.memoryCacheKey });
/* 220 */         this.loadedFrom = LoadedFrom.DISC_CACHE;
/*     */ 
/* 222 */         checkTaskNotActual();
/* 223 */         bitmap = decodeImage(ImageDownloader.Scheme.FILE.wrap(imageFile.getAbsolutePath()));
/*     */       }
/* 225 */       if ((bitmap == null) || (bitmap.getWidth() <= 0) || (bitmap.getHeight() <= 0)) {
/* 226 */         L.d("Load image from network [%s]", new Object[] { this.memoryCacheKey });
/* 227 */         this.loadedFrom = LoadedFrom.NETWORK;
/*     */ 
/* 229 */         String imageUriForDecoding = this.uri;
/* 230 */         if ((this.options.isCacheOnDisk()) && (tryCacheImageOnDisk())) {
/* 231 */           imageFile = this.configuration.diskCache.get(this.uri);
/* 232 */           if (imageFile != null) {
/* 233 */             imageUriForDecoding = ImageDownloader.Scheme.FILE.wrap(imageFile.getAbsolutePath());
/*     */           }
/*     */         }
/*     */ 
/* 237 */         checkTaskNotActual();
/* 238 */         bitmap = decodeImage(imageUriForDecoding);
/*     */ 
/* 240 */         if ((bitmap == null) || (bitmap.getWidth() <= 0) || (bitmap.getHeight() <= 0))
/* 241 */           fireFailEvent(FailReason.FailType.DECODING_ERROR, null);
/*     */       }
/*     */     }
/*     */     catch (IllegalStateException e) {
/* 245 */       fireFailEvent(FailReason.FailType.NETWORK_DENIED, null);
/*     */     } catch (TaskCancelledException e) {
/* 247 */       throw e;
/*     */     } catch (IOException e) {
/* 249 */       L.e(e);
/* 250 */       fireFailEvent(FailReason.FailType.IO_ERROR, e);
/*     */     } catch (OutOfMemoryError e) {
/* 252 */       L.e(e);
/* 253 */       fireFailEvent(FailReason.FailType.OUT_OF_MEMORY, e);
/*     */     } catch (Throwable e) {
/* 255 */       L.e(e);
/* 256 */       fireFailEvent(FailReason.FailType.UNKNOWN, e);
/*     */     }
/* 258 */     return bitmap;
/*     */   }
/*     */ 
/*     */   private Bitmap decodeImage(String imageUri) throws IOException {
/* 262 */     ViewScaleType viewScaleType = this.imageAware.getScaleType();
/* 263 */     ImageDecodingInfo decodingInfo = new ImageDecodingInfo(this.memoryCacheKey, imageUri, this.uri, this.targetSize, viewScaleType, getDownloader(), this.options);
/*     */ 
/* 265 */     return this.decoder.decode(decodingInfo);
/*     */   }
/*     */ 
/*     */   private boolean tryCacheImageOnDisk() throws LoadAndDisplayImageTask.TaskCancelledException {
/* 270 */     L.d("Cache image on disk [%s]", new Object[] { this.memoryCacheKey });
/*     */     boolean loaded;
/*     */     try {
/* 274 */       loaded = downloadImage();
/* 275 */       if (loaded) {
/* 276 */         int width = this.configuration.maxImageWidthForDiskCache;
/* 277 */         int height = this.configuration.maxImageHeightForDiskCache;
/* 278 */         if ((width > 0) || (height > 0)) {
/* 279 */           L.d("Resize image in disk cache [%s]", new Object[] { this.memoryCacheKey });
/* 280 */           resizeAndSaveImage(width, height);
/*     */         }
/*     */       }
/*     */     } catch (IOException e) {
/* 284 */       L.e(e);
/* 285 */       loaded = false;
/*     */     }
/* 287 */     return loaded;
/*     */   }
/*     */ 
/*     */   private boolean downloadImage() throws IOException {
/* 291 */     InputStream is = getDownloader().getStream(this.uri, this.options.getExtraForDownloader());
/* 292 */     if (is == null) {
/* 293 */       L.e("No stream for image [%s]", new Object[] { this.memoryCacheKey });
/* 294 */       return false;
/*     */     }
/*     */     try {
/* 297 */       boolean bool = this.configuration.diskCache.save(this.uri, is, this);
/*     */       return bool; } finally { IoUtils.closeSilently(is); } throw localObject;
/*     */   }
/*     */ 
/*     */   private boolean resizeAndSaveImage(int maxWidth, int maxHeight)
/*     */     throws IOException
/*     */   {
/* 307 */     boolean saved = false;
/* 308 */     File targetFile = this.configuration.diskCache.get(this.uri);
/* 309 */     if ((targetFile != null) && (targetFile.exists())) {
/* 310 */       ImageSize targetImageSize = new ImageSize(maxWidth, maxHeight);
/* 311 */       DisplayImageOptions specialOptions = new DisplayImageOptions.Builder().cloneFrom(this.options).imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
/*     */ 
/* 313 */       ImageDecodingInfo decodingInfo = new ImageDecodingInfo(this.memoryCacheKey, ImageDownloader.Scheme.FILE.wrap(targetFile.getAbsolutePath()), this.uri, targetImageSize, ViewScaleType.FIT_INSIDE, getDownloader(), specialOptions);
/*     */ 
/* 316 */       Bitmap bmp = this.decoder.decode(decodingInfo);
/* 317 */       if ((bmp != null) && (this.configuration.processorForDiskCache != null)) {
/* 318 */         L.d("Process image before cache on disk [%s]", new Object[] { this.memoryCacheKey });
/* 319 */         bmp = this.configuration.processorForDiskCache.process(bmp);
/* 320 */         if (bmp == null) {
/* 321 */           L.e("Bitmap processor for disk cache returned null [%s]", new Object[] { this.memoryCacheKey });
/*     */         }
/*     */       }
/* 324 */       if (bmp != null) {
/* 325 */         saved = this.configuration.diskCache.save(this.uri, bmp);
/* 326 */         bmp.recycle();
/*     */       }
/*     */     }
/* 329 */     return saved;
/*     */   }
/*     */ 
/*     */   public boolean onBytesCopied(int current, int total)
/*     */   {
/* 334 */     return (this.syncLoading) || (fireProgressEvent(current, total));
/*     */   }
/*     */ 
/*     */   private boolean fireProgressEvent(int current, int total)
/*     */   {
/* 339 */     if ((isTaskInterrupted()) || (isTaskNotActual())) return false;
/* 340 */     if (this.progressListener != null) {
/* 341 */       Runnable r = new Runnable(current, total)
/*     */       {
/*     */         public void run() {
/* 344 */           LoadAndDisplayImageTask.this.progressListener.onProgressUpdate(LoadAndDisplayImageTask.this.uri, LoadAndDisplayImageTask.this.imageAware.getWrappedView(), this.val$current, this.val$total);
/*     */         }
/*     */       };
/* 347 */       runTask(r, false, this.handler, this.engine);
/*     */     }
/* 349 */     return true;
/*     */   }
/*     */ 
/*     */   private void fireFailEvent(FailReason.FailType failType, Throwable failCause) {
/* 353 */     if ((this.syncLoading) || (isTaskInterrupted()) || (isTaskNotActual())) return;
/* 354 */     Runnable r = new Runnable(failType, failCause)
/*     */     {
/*     */       public void run() {
/* 357 */         if (LoadAndDisplayImageTask.this.options.shouldShowImageOnFail()) {
/* 358 */           if (LoadAndDisplayImageTask.this.options.getDisplayer() != null) {
/* 359 */             Bitmap bitmap = LoadAndDisplayImageTask.this.options.drawableToBitmap(LoadAndDisplayImageTask.this.options.getImageOnFail(LoadAndDisplayImageTask.this.configuration.resources));
/* 360 */             if (bitmap != null)
/* 361 */               LoadAndDisplayImageTask.this.options.getDisplayer().display(bitmap, LoadAndDisplayImageTask.this.imageAware, LoadedFrom.DISC_CACHE);
/*     */           } else {
/* 363 */             LoadAndDisplayImageTask.this.imageAware.setImageDrawable(LoadAndDisplayImageTask.this.options.getImageOnFail(LoadAndDisplayImageTask.this.configuration.resources));
/*     */           }
/*     */         }
/* 366 */         LoadAndDisplayImageTask.this.listener.onLoadingFailed(LoadAndDisplayImageTask.this.uri, LoadAndDisplayImageTask.this.imageAware.getWrappedView(), new FailReason(this.val$failType, this.val$failCause));
/*     */       }
/*     */     };
/* 369 */     runTask(r, false, this.handler, this.engine);
/*     */   }
/*     */ 
/*     */   private void fireCancelEvent() {
/* 373 */     if ((this.syncLoading) || (isTaskInterrupted())) return;
/* 374 */     Runnable r = new Runnable()
/*     */     {
/*     */       public void run() {
/* 377 */         LoadAndDisplayImageTask.this.listener.onLoadingCancelled(LoadAndDisplayImageTask.this.uri, LoadAndDisplayImageTask.this.imageAware.getWrappedView());
/*     */       }
/*     */     };
/* 380 */     runTask(r, false, this.handler, this.engine);
/*     */   }
/*     */ 
/*     */   private ImageDownloader getDownloader()
/*     */   {
/*     */     ImageDownloader d;
/*     */     ImageDownloader d;
/* 385 */     if (this.engine.isNetworkDenied()) {
/* 386 */       d = this.networkDeniedDownloader;
/*     */     }
/*     */     else
/*     */     {
/*     */       ImageDownloader d;
/* 387 */       if (this.engine.isSlowNetwork())
/* 388 */         d = this.slowNetworkDownloader;
/*     */       else
/* 390 */         d = this.downloader;
/*     */     }
/* 392 */     return d;
/*     */   }
/*     */ 
/*     */   private void checkTaskNotActual()
/*     */     throws LoadAndDisplayImageTask.TaskCancelledException
/*     */   {
/* 401 */     checkViewCollected();
/* 402 */     checkViewReused();
/*     */   }
/*     */ 
/*     */   private boolean isTaskNotActual()
/*     */   {
/* 410 */     return (isViewCollected()) || (isViewReused());
/*     */   }
/*     */ 
/*     */   private void checkViewCollected() throws LoadAndDisplayImageTask.TaskCancelledException
/*     */   {
/* 415 */     if (isViewCollected())
/* 416 */       throw new TaskCancelledException();
/*     */   }
/*     */ 
/*     */   private boolean isViewCollected()
/*     */   {
/* 422 */     if (this.imageAware.isCollected()) {
/* 423 */       L.d("ImageAware was collected by GC. Task is cancelled. [%s]", new Object[] { this.memoryCacheKey });
/* 424 */       return true;
/*     */     }
/* 426 */     return false;
/*     */   }
/*     */ 
/*     */   private void checkViewReused() throws LoadAndDisplayImageTask.TaskCancelledException
/*     */   {
/* 431 */     if (isViewReused())
/* 432 */       throw new TaskCancelledException();
/*     */   }
/*     */ 
/*     */   private boolean isViewReused()
/*     */   {
/* 438 */     String currentCacheKey = this.engine.getLoadingUriForView(this.imageAware);
/*     */ 
/* 441 */     boolean imageAwareWasReused = !this.memoryCacheKey.equals(currentCacheKey);
/* 442 */     if (imageAwareWasReused) {
/* 443 */       L.d("ImageAware is reused for another image. Task is cancelled. [%s]", new Object[] { this.memoryCacheKey });
/* 444 */       return true;
/*     */     }
/* 446 */     return false;
/*     */   }
/*     */ 
/*     */   private void checkTaskInterrupted() throws LoadAndDisplayImageTask.TaskCancelledException
/*     */   {
/* 451 */     if (isTaskInterrupted())
/* 452 */       throw new TaskCancelledException();
/*     */   }
/*     */ 
/*     */   private boolean isTaskInterrupted()
/*     */   {
/* 458 */     if (Thread.interrupted()) {
/* 459 */       L.d("Task was interrupted [%s]", new Object[] { this.memoryCacheKey });
/* 460 */       return true;
/*     */     }
/* 462 */     return false;
/*     */   }
/*     */ 
/*     */   String getLoadingUri() {
/* 466 */     return this.uri;
/*     */   }
/*     */ 
/*     */   static void runTask(Runnable r, boolean sync, Handler handler, ImageLoaderEngine engine) {
/* 470 */     if (sync)
/* 471 */       r.run();
/* 472 */     else if (handler == null)
/* 473 */       engine.fireCallback(r);
/*     */     else
/* 475 */       handler.post(r);
/*     */   }
/*     */ 
/*     */   class TaskCancelledException extends Exception
/*     */   {
/*     */     TaskCancelledException()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.LoadAndDisplayImageTask
 * JD-Core Version:    0.6.0
 */
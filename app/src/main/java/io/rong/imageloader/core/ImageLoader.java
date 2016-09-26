/*     */ package io.rong.imageloader.core;
/*     */ 
/*     */ import android.graphics.Bitmap;
/*     */ import android.os.Handler;
/*     */ import android.os.Looper;
/*     */ import android.text.TextUtils;
/*     */ import android.view.View;
/*     */ import android.widget.ImageView;
/*     */ import io.rong.imageloader.cache.disc.DiskCache;
/*     */ import io.rong.imageloader.cache.memory.MemoryCache;
/*     */ import io.rong.imageloader.core.assist.ImageSize;
/*     */ import io.rong.imageloader.core.assist.LoadedFrom;
/*     */ import io.rong.imageloader.core.assist.ViewScaleType;
/*     */ import io.rong.imageloader.core.display.BitmapDisplayer;
/*     */ import io.rong.imageloader.core.imageaware.ImageAware;
/*     */ import io.rong.imageloader.core.imageaware.ImageViewAware;
/*     */ import io.rong.imageloader.core.imageaware.NonViewAware;
/*     */ import io.rong.imageloader.core.listener.ImageLoadingListener;
/*     */ import io.rong.imageloader.core.listener.ImageLoadingProgressListener;
/*     */ import io.rong.imageloader.core.listener.SimpleImageLoadingListener;
/*     */ import io.rong.imageloader.utils.ImageSizeUtils;
/*     */ import io.rong.imageloader.utils.L;
/*     */ import io.rong.imageloader.utils.MemoryCacheUtils;
/*     */ 
/*     */ public class ImageLoader
/*     */ {
/*  51 */   public static final String TAG = ImageLoader.class.getSimpleName();
/*     */   static final String LOG_INIT_CONFIG = "Initialize ImageLoader with configuration";
/*     */   static final String LOG_DESTROY = "Destroy ImageLoader";
/*     */   static final String LOG_LOAD_IMAGE_FROM_MEMORY_CACHE = "Load image from memory cache [%s]";
/*     */   private static final String WARNING_RE_INIT_CONFIG = "Try to initialize ImageLoader which had already been initialized before. To re-init ImageLoader with new configuration call ImageLoader.destroy() at first.";
/*     */   private static final String ERROR_WRONG_ARGUMENTS = "Wrong arguments were passed to displayImage() method (ImageView reference must not be null)";
/*     */   private static final String ERROR_NOT_INIT = "ImageLoader must be init with configuration before using";
/*     */   private static final String ERROR_INIT_CONFIG_WITH_NULL = "ImageLoader configuration can not be initialized with null";
/*     */   private ImageLoaderConfiguration configuration;
/*     */   private ImageLoaderEngine engine;
/*  65 */   private ImageLoadingListener defaultListener = new SimpleImageLoadingListener();
/*     */   private static volatile ImageLoader instance;
/*     */ 
/*     */   public static ImageLoader getInstance()
/*     */   {
/*  71 */     if (instance == null) {
/*  72 */       synchronized (ImageLoader.class) {
/*  73 */         if (instance == null) {
/*  74 */           instance = new ImageLoader();
/*     */         }
/*     */       }
/*     */     }
/*  78 */     return instance;
/*     */   }
/*     */ 
/*     */   public synchronized void init(ImageLoaderConfiguration configuration)
/*     */   {
/*  93 */     if (configuration == null) {
/*  94 */       throw new IllegalArgumentException("ImageLoader configuration can not be initialized with null");
/*     */     }
/*  96 */     if (this.configuration == null) {
/*  97 */       L.d("Initialize ImageLoader with configuration", new Object[0]);
/*  98 */       this.engine = new ImageLoaderEngine(configuration);
/*  99 */       this.configuration = configuration;
/*     */     } else {
/* 101 */       L.w("Try to initialize ImageLoader which had already been initialized before. To re-init ImageLoader with new configuration call ImageLoader.destroy() at first.", new Object[0]);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isInited()
/*     */   {
/* 110 */     return this.configuration != null;
/*     */   }
/*     */ 
/*     */   public void displayImage(String uri, ImageAware imageAware)
/*     */   {
/* 126 */     displayImage(uri, imageAware, null, null, null);
/*     */   }
/*     */ 
/*     */   public void displayImage(String uri, ImageAware imageAware, ImageLoadingListener listener)
/*     */   {
/* 144 */     displayImage(uri, imageAware, null, listener, null);
/*     */   }
/*     */ 
/*     */   public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options)
/*     */   {
/* 162 */     displayImage(uri, imageAware, options, null, null);
/*     */   }
/*     */ 
/*     */   public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options, ImageLoadingListener listener)
/*     */   {
/* 183 */     displayImage(uri, imageAware, options, listener, null);
/*     */   }
/*     */ 
/*     */   public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options, ImageLoadingListener listener, ImageLoadingProgressListener progressListener)
/*     */   {
/* 209 */     displayImage(uri, imageAware, options, null, listener, progressListener);
/*     */   }
/*     */ 
/*     */   public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options, ImageSize targetSize, ImageLoadingListener listener, ImageLoadingProgressListener progressListener)
/*     */   {
/* 236 */     checkConfiguration();
/* 237 */     if (imageAware == null) {
/* 238 */       throw new IllegalArgumentException("Wrong arguments were passed to displayImage() method (ImageView reference must not be null)");
/*     */     }
/* 240 */     if (listener == null) {
/* 241 */       listener = this.defaultListener;
/*     */     }
/* 243 */     if (options == null) {
/* 244 */       options = this.configuration.defaultDisplayImageOptions;
/*     */     }
/*     */ 
/* 247 */     if (TextUtils.isEmpty(uri)) {
/* 248 */       this.engine.cancelDisplayTaskFor(imageAware);
/* 249 */       listener.onLoadingStarted(uri, imageAware.getWrappedView());
/* 250 */       if (options.shouldShowImageForEmptyUri()) {
/* 251 */         if (options.getDisplayer() != null) {
/* 252 */           Bitmap bitmap = options.drawableToBitmap(options.getImageForEmptyUri(this.configuration.resources));
/* 253 */           if (bitmap != null)
/* 254 */             options.getDisplayer().display(bitmap, imageAware, LoadedFrom.DISC_CACHE);
/*     */         } else {
/* 256 */           imageAware.setImageDrawable(options.getImageForEmptyUri(this.configuration.resources));
/*     */         }
/*     */       }
/* 259 */       else imageAware.setImageDrawable(null);
/*     */ 
/* 261 */       listener.onLoadingComplete(uri, imageAware.getWrappedView(), null);
/* 262 */       return;
/*     */     }
/*     */ 
/* 265 */     if (targetSize == null) {
/* 266 */       targetSize = ImageSizeUtils.defineTargetSizeForView(imageAware, this.configuration.getMaxImageSize());
/*     */     }
/* 268 */     String memoryCacheKey = MemoryCacheUtils.generateKey(uri, targetSize);
/* 269 */     this.engine.prepareDisplayTaskFor(imageAware, memoryCacheKey);
/*     */ 
/* 271 */     listener.onLoadingStarted(uri, imageAware.getWrappedView());
/*     */ 
/* 273 */     Bitmap bmp = this.configuration.memoryCache.get(memoryCacheKey);
/* 274 */     if ((bmp != null) && (!bmp.isRecycled())) {
/* 275 */       L.d("Load image from memory cache [%s]", new Object[] { memoryCacheKey });
/*     */ 
/* 277 */       if (options.shouldPostProcess()) {
/* 278 */         ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(uri, imageAware, targetSize, memoryCacheKey, options, listener, progressListener, this.engine.getLockForUri(uri));
/*     */ 
/* 280 */         ProcessAndDisplayImageTask displayTask = new ProcessAndDisplayImageTask(this.engine, bmp, imageLoadingInfo, defineHandler(options));
/*     */ 
/* 282 */         if (options.isSyncLoading())
/* 283 */           displayTask.run();
/*     */         else
/* 285 */           this.engine.submit(displayTask);
/*     */       }
/*     */       else {
/* 288 */         options.getDisplayer().display(bmp, imageAware, LoadedFrom.MEMORY_CACHE);
/* 289 */         listener.onLoadingComplete(uri, imageAware.getWrappedView(), bmp);
/*     */       }
/*     */     } else {
/* 292 */       if (options.shouldShowImageOnLoading()) {
/* 293 */         if (options.getDisplayer() != null) {
/* 294 */           Bitmap bitmap = options.drawableToBitmap(options.getImageOnLoading(this.configuration.resources));
/* 295 */           if (bitmap != null)
/* 296 */             options.getDisplayer().display(bitmap, imageAware, LoadedFrom.DISC_CACHE);
/*     */         } else {
/* 298 */           imageAware.setImageDrawable(options.getImageOnLoading(this.configuration.resources));
/*     */         }
/* 300 */       } else if (options.isResetViewBeforeLoading()) {
/* 301 */         imageAware.setImageDrawable(null);
/*     */       }
/*     */ 
/* 304 */       ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(uri, imageAware, targetSize, memoryCacheKey, options, listener, progressListener, this.engine.getLockForUri(uri));
/*     */ 
/* 306 */       LoadAndDisplayImageTask displayTask = new LoadAndDisplayImageTask(this.engine, imageLoadingInfo, defineHandler(options));
/*     */ 
/* 308 */       if (options.isSyncLoading())
/* 309 */         displayTask.run();
/*     */       else
/* 311 */         this.engine.submit(displayTask);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void displayImage(String uri, ImageView imageView)
/*     */   {
/* 328 */     displayImage(uri, new ImageViewAware(imageView), null, null, null);
/*     */   }
/*     */ 
/*     */   public void displayImage(String uri, ImageView imageView, ImageSize targetImageSize)
/*     */   {
/* 343 */     displayImage(uri, new ImageViewAware(imageView), null, targetImageSize, null, null);
/*     */   }
/*     */ 
/*     */   public void displayImage(String uri, ImageView imageView, DisplayImageOptions options)
/*     */   {
/* 360 */     displayImage(uri, new ImageViewAware(imageView), options, null, null);
/*     */   }
/*     */ 
/*     */   public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener)
/*     */   {
/* 377 */     displayImage(uri, new ImageViewAware(imageView), null, listener, null);
/*     */   }
/*     */ 
/*     */   public void displayImage(String uri, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener)
/*     */   {
/* 397 */     displayImage(uri, imageView, options, listener, null);
/*     */   }
/*     */ 
/*     */   public void displayImage(String uri, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener, ImageLoadingProgressListener progressListener)
/*     */   {
/* 422 */     displayImage(uri, new ImageViewAware(imageView), options, listener, progressListener);
/*     */   }
/*     */ 
/*     */   public void loadImage(String uri, ImageLoadingListener listener)
/*     */   {
/* 437 */     loadImage(uri, null, null, listener, null);
/*     */   }
/*     */ 
/*     */   public void loadImage(String uri, ImageSize targetImageSize, ImageLoadingListener listener)
/*     */   {
/* 457 */     loadImage(uri, targetImageSize, null, listener, null);
/*     */   }
/*     */ 
/*     */   public void loadImage(String uri, DisplayImageOptions options, ImageLoadingListener listener)
/*     */   {
/* 476 */     loadImage(uri, null, options, listener, null);
/*     */   }
/*     */ 
/*     */   public void loadImage(String uri, ImageSize targetImageSize, DisplayImageOptions options, ImageLoadingListener listener)
/*     */   {
/* 501 */     loadImage(uri, targetImageSize, options, listener, null);
/*     */   }
/*     */ 
/*     */   public void loadImage(String uri, ImageSize targetImageSize, DisplayImageOptions options, ImageLoadingListener listener, ImageLoadingProgressListener progressListener)
/*     */   {
/* 531 */     checkConfiguration();
/* 532 */     if (targetImageSize == null) {
/* 533 */       targetImageSize = this.configuration.getMaxImageSize();
/*     */     }
/* 535 */     if (options == null) {
/* 536 */       options = this.configuration.defaultDisplayImageOptions;
/*     */     }
/*     */ 
/* 539 */     NonViewAware imageAware = new NonViewAware(uri, targetImageSize, ViewScaleType.CROP);
/* 540 */     displayImage(uri, imageAware, options, listener, progressListener);
/*     */   }
/*     */ 
/*     */   public Bitmap loadImageSync(String uri)
/*     */   {
/* 555 */     return loadImageSync(uri, null, null);
/*     */   }
/*     */ 
/*     */   public Bitmap loadImageSync(String uri, DisplayImageOptions options)
/*     */   {
/* 571 */     return loadImageSync(uri, null, options);
/*     */   }
/*     */ 
/*     */   public Bitmap loadImageSync(String uri, ImageSize targetImageSize)
/*     */   {
/* 589 */     return loadImageSync(uri, targetImageSize, null);
/*     */   }
/*     */ 
/*     */   public Bitmap loadImageSync(String uri, ImageSize targetImageSize, DisplayImageOptions options)
/*     */   {
/* 608 */     if (options == null) {
/* 609 */       options = this.configuration.defaultDisplayImageOptions;
/*     */     }
/* 611 */     options = new DisplayImageOptions.Builder().cloneFrom(options).syncLoading(true).build();
/*     */ 
/* 613 */     SyncImageLoadingListener listener = new SyncImageLoadingListener(null);
/* 614 */     loadImage(uri, targetImageSize, options, listener);
/* 615 */     return listener.getLoadedBitmap();
/*     */   }
/*     */ 
/*     */   private void checkConfiguration()
/*     */   {
/* 624 */     if (this.configuration == null)
/* 625 */       throw new IllegalStateException("ImageLoader must be init with configuration before using");
/*     */   }
/*     */ 
/*     */   public void setDefaultLoadingListener(ImageLoadingListener listener)
/*     */   {
/* 631 */     this.defaultListener = (listener == null ? new SimpleImageLoadingListener() : listener);
/*     */   }
/*     */ 
/*     */   public MemoryCache getMemoryCache()
/*     */   {
/* 640 */     checkConfiguration();
/* 641 */     return this.configuration.memoryCache;
/*     */   }
/*     */ 
/*     */   public void clearMemoryCache()
/*     */   {
/* 650 */     checkConfiguration();
/* 651 */     this.configuration.memoryCache.clear();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public DiskCache getDiscCache()
/*     */   {
/* 662 */     return getDiskCache();
/*     */   }
/*     */ 
/*     */   public DiskCache getDiskCache()
/*     */   {
/* 671 */     checkConfiguration();
/* 672 */     return this.configuration.diskCache;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void clearDiscCache()
/*     */   {
/* 683 */     clearDiskCache();
/*     */   }
/*     */ 
/*     */   public void clearDiskCache()
/*     */   {
/* 692 */     checkConfiguration();
/* 693 */     this.configuration.diskCache.clear();
/*     */   }
/*     */ 
/*     */   public String getLoadingUriForView(ImageAware imageAware)
/*     */   {
/* 701 */     return this.engine.getLoadingUriForView(imageAware);
/*     */   }
/*     */ 
/*     */   public String getLoadingUriForView(ImageView imageView)
/*     */   {
/* 709 */     return this.engine.getLoadingUriForView(new ImageViewAware(imageView));
/*     */   }
/*     */ 
/*     */   public void cancelDisplayTask(ImageAware imageAware)
/*     */   {
/* 720 */     this.engine.cancelDisplayTaskFor(imageAware);
/*     */   }
/*     */ 
/*     */   public void cancelDisplayTask(ImageView imageView)
/*     */   {
/* 730 */     this.engine.cancelDisplayTaskFor(new ImageViewAware(imageView));
/*     */   }
/*     */ 
/*     */   public void denyNetworkDownloads(boolean denyNetworkDownloads)
/*     */   {
/* 744 */     this.engine.denyNetworkDownloads(denyNetworkDownloads);
/*     */   }
/*     */ 
/*     */   public void handleSlowNetwork(boolean handleSlowNetwork)
/*     */   {
/* 755 */     this.engine.handleSlowNetwork(handleSlowNetwork);
/*     */   }
/*     */ 
/*     */   public void pause()
/*     */   {
/* 764 */     this.engine.pause();
/*     */   }
/*     */ 
/*     */   public void resume()
/*     */   {
/* 769 */     this.engine.resume();
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 780 */     this.engine.stop();
/*     */   }
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 789 */     if (this.configuration != null) L.d("Destroy ImageLoader", new Object[0]);
/* 790 */     stop();
/* 791 */     this.configuration.diskCache.close();
/* 792 */     this.engine = null;
/* 793 */     this.configuration = null;
/*     */   }
/*     */ 
/*     */   private static Handler defineHandler(DisplayImageOptions options) {
/* 797 */     Handler handler = options.getHandler();
/* 798 */     if (options.isSyncLoading())
/* 799 */       handler = null;
/* 800 */     else if ((handler == null) && (Looper.myLooper() == Looper.getMainLooper())) {
/* 801 */       handler = new Handler();
/*     */     }
/* 803 */     return handler;
/*     */   }
/*     */ 
/*     */   private static class SyncImageLoadingListener extends SimpleImageLoadingListener
/*     */   {
/*     */     private Bitmap loadedImage;
/*     */ 
/*     */     public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
/*     */     {
/* 818 */       this.loadedImage = loadedImage;
/*     */     }
/*     */ 
/*     */     public Bitmap getLoadedBitmap() {
/* 822 */       return this.loadedImage;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.ImageLoader
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.imageloader.core;
/*    */ 
/*    */ import android.graphics.Bitmap;
/*    */ import io.rong.imageloader.core.assist.LoadedFrom;
/*    */ import io.rong.imageloader.core.display.BitmapDisplayer;
/*    */ import io.rong.imageloader.core.imageaware.ImageAware;
/*    */ import io.rong.imageloader.core.listener.ImageLoadingListener;
/*    */ import io.rong.imageloader.utils.L;
/*    */ 
/*    */ final class DisplayBitmapTask
/*    */   implements Runnable
/*    */ {
/*    */   private static final String LOG_DISPLAY_IMAGE_IN_IMAGEAWARE = "Display image in ImageAware (loaded from %1$s) [%2$s]";
/*    */   private static final String LOG_TASK_CANCELLED_IMAGEAWARE_REUSED = "ImageAware is reused for another image. Task is cancelled. [%s]";
/*    */   private static final String LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED = "ImageAware was collected by GC. Task is cancelled. [%s]";
/*    */   private final Bitmap bitmap;
/*    */   private final String imageUri;
/*    */   private final ImageAware imageAware;
/*    */   private final String memoryCacheKey;
/*    */   private final BitmapDisplayer displayer;
/*    */   private final ImageLoadingListener listener;
/*    */   private final ImageLoaderEngine engine;
/*    */   private final LoadedFrom loadedFrom;
/*    */ 
/*    */   public DisplayBitmapTask(Bitmap bitmap, ImageLoadingInfo imageLoadingInfo, ImageLoaderEngine engine, LoadedFrom loadedFrom)
/*    */   {
/* 50 */     this.bitmap = bitmap;
/* 51 */     this.imageUri = imageLoadingInfo.uri;
/* 52 */     this.imageAware = imageLoadingInfo.imageAware;
/* 53 */     this.memoryCacheKey = imageLoadingInfo.memoryCacheKey;
/* 54 */     this.displayer = imageLoadingInfo.options.getDisplayer();
/* 55 */     this.listener = imageLoadingInfo.listener;
/* 56 */     this.engine = engine;
/* 57 */     this.loadedFrom = loadedFrom;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 62 */     if (this.imageAware.isCollected()) {
/* 63 */       L.d("ImageAware was collected by GC. Task is cancelled. [%s]", new Object[] { this.memoryCacheKey });
/* 64 */       this.listener.onLoadingCancelled(this.imageUri, this.imageAware.getWrappedView());
/* 65 */     } else if (isViewWasReused()) {
/* 66 */       L.d("ImageAware is reused for another image. Task is cancelled. [%s]", new Object[] { this.memoryCacheKey });
/* 67 */       this.listener.onLoadingCancelled(this.imageUri, this.imageAware.getWrappedView());
/*    */     } else {
/* 69 */       L.d("Display image in ImageAware (loaded from %1$s) [%2$s]", new Object[] { this.loadedFrom, this.memoryCacheKey });
/* 70 */       this.displayer.display(this.bitmap, this.imageAware, this.loadedFrom);
/* 71 */       this.engine.cancelDisplayTaskFor(this.imageAware);
/* 72 */       this.listener.onLoadingComplete(this.imageUri, this.imageAware.getWrappedView(), this.bitmap);
/*    */     }
/*    */   }
/*    */ 
/*    */   private boolean isViewWasReused()
/*    */   {
/* 78 */     String currentCacheKey = this.engine.getLoadingUriForView(this.imageAware);
/* 79 */     return !this.memoryCacheKey.equals(currentCacheKey);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.DisplayBitmapTask
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.imageloader.core;
/*    */ 
/*    */ import io.rong.imageloader.core.assist.ImageSize;
/*    */ import io.rong.imageloader.core.imageaware.ImageAware;
/*    */ import io.rong.imageloader.core.listener.ImageLoadingListener;
/*    */ import io.rong.imageloader.core.listener.ImageLoadingProgressListener;
/*    */ import java.util.concurrent.locks.ReentrantLock;
/*    */ 
/*    */ final class ImageLoadingInfo
/*    */ {
/*    */   final String uri;
/*    */   final String memoryCacheKey;
/*    */   final ImageAware imageAware;
/*    */   final ImageSize targetSize;
/*    */   final DisplayImageOptions options;
/*    */   final ImageLoadingListener listener;
/*    */   final ImageLoadingProgressListener progressListener;
/*    */   final ReentrantLock loadFromUriLock;
/*    */ 
/*    */   public ImageLoadingInfo(String uri, ImageAware imageAware, ImageSize targetSize, String memoryCacheKey, DisplayImageOptions options, ImageLoadingListener listener, ImageLoadingProgressListener progressListener, ReentrantLock loadFromUriLock)
/*    */   {
/* 49 */     this.uri = uri;
/* 50 */     this.imageAware = imageAware;
/* 51 */     this.targetSize = targetSize;
/* 52 */     this.options = options;
/* 53 */     this.listener = listener;
/* 54 */     this.progressListener = progressListener;
/* 55 */     this.loadFromUriLock = loadFromUriLock;
/* 56 */     this.memoryCacheKey = memoryCacheKey;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.ImageLoadingInfo
 * JD-Core Version:    0.6.0
 */
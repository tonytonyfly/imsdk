/*    */ package io.rong.imageloader.core;
/*    */ 
/*    */ import android.graphics.Bitmap;
/*    */ import android.os.Handler;
/*    */ import io.rong.imageloader.core.assist.LoadedFrom;
/*    */ import io.rong.imageloader.core.process.BitmapProcessor;
/*    */ import io.rong.imageloader.utils.L;
/*    */ 
/*    */ final class ProcessAndDisplayImageTask
/*    */   implements Runnable
/*    */ {
/*    */   private static final String LOG_POSTPROCESS_IMAGE = "PostProcess image before displaying [%s]";
/*    */   private final ImageLoaderEngine engine;
/*    */   private final Bitmap bitmap;
/*    */   private final ImageLoadingInfo imageLoadingInfo;
/*    */   private final Handler handler;
/*    */ 
/*    */   public ProcessAndDisplayImageTask(ImageLoaderEngine engine, Bitmap bitmap, ImageLoadingInfo imageLoadingInfo, Handler handler)
/*    */   {
/* 43 */     this.engine = engine;
/* 44 */     this.bitmap = bitmap;
/* 45 */     this.imageLoadingInfo = imageLoadingInfo;
/* 46 */     this.handler = handler;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 51 */     L.d("PostProcess image before displaying [%s]", new Object[] { this.imageLoadingInfo.memoryCacheKey });
/*    */ 
/* 53 */     BitmapProcessor processor = this.imageLoadingInfo.options.getPostProcessor();
/* 54 */     Bitmap processedBitmap = processor.process(this.bitmap);
/* 55 */     DisplayBitmapTask displayBitmapTask = new DisplayBitmapTask(processedBitmap, this.imageLoadingInfo, this.engine, LoadedFrom.MEMORY_CACHE);
/*    */ 
/* 57 */     LoadAndDisplayImageTask.runTask(displayBitmapTask, this.imageLoadingInfo.options.isSyncLoading(), this.handler, this.engine);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.ProcessAndDisplayImageTask
 * JD-Core Version:    0.6.0
 */
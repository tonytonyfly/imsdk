/*    */ package io.rong.imageloader.core.display;
/*    */ 
/*    */ import android.graphics.Bitmap;
/*    */ import android.view.View;
/*    */ import android.view.animation.AlphaAnimation;
/*    */ import android.view.animation.DecelerateInterpolator;
/*    */ import io.rong.imageloader.core.assist.LoadedFrom;
/*    */ import io.rong.imageloader.core.imageaware.ImageAware;
/*    */ 
/*    */ public class FadeInBitmapDisplayer
/*    */   implements BitmapDisplayer
/*    */ {
/*    */   private final int durationMillis;
/*    */   private final boolean animateFromNetwork;
/*    */   private final boolean animateFromDisk;
/*    */   private final boolean animateFromMemory;
/*    */ 
/*    */   public FadeInBitmapDisplayer(int durationMillis)
/*    */   {
/* 44 */     this(durationMillis, true, true, true);
/*    */   }
/*    */ 
/*    */   public FadeInBitmapDisplayer(int durationMillis, boolean animateFromNetwork, boolean animateFromDisk, boolean animateFromMemory)
/*    */   {
/* 55 */     this.durationMillis = durationMillis;
/* 56 */     this.animateFromNetwork = animateFromNetwork;
/* 57 */     this.animateFromDisk = animateFromDisk;
/* 58 */     this.animateFromMemory = animateFromMemory;
/*    */   }
/*    */ 
/*    */   public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom)
/*    */   {
/* 63 */     imageAware.setImageBitmap(bitmap);
/*    */ 
/* 65 */     if (((this.animateFromNetwork) && (loadedFrom == LoadedFrom.NETWORK)) || ((this.animateFromDisk) && (loadedFrom == LoadedFrom.DISC_CACHE)) || ((this.animateFromMemory) && (loadedFrom == LoadedFrom.MEMORY_CACHE)))
/*    */     {
/* 68 */       animate(imageAware.getWrappedView(), this.durationMillis);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void animate(View imageView, int durationMillis)
/*    */   {
/* 79 */     if (imageView != null) {
/* 80 */       AlphaAnimation fadeImage = new AlphaAnimation(0.0F, 1.0F);
/* 81 */       fadeImage.setDuration(durationMillis);
/* 82 */       fadeImage.setInterpolator(new DecelerateInterpolator());
/* 83 */       imageView.startAnimation(fadeImage);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.display.FadeInBitmapDisplayer
 * JD-Core Version:    0.6.0
 */
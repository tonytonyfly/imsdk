/*    */ package io.rong.imageloader.core.display;
/*    */ 
/*    */ import android.graphics.Bitmap;
/*    */ import android.graphics.ComposeShader;
/*    */ import android.graphics.Matrix;
/*    */ import android.graphics.Paint;
/*    */ import android.graphics.PorterDuff.Mode;
/*    */ import android.graphics.RadialGradient;
/*    */ import android.graphics.Rect;
/*    */ import android.graphics.RectF;
/*    */ import android.graphics.Shader.TileMode;
/*    */ import io.rong.imageloader.core.assist.LoadedFrom;
/*    */ import io.rong.imageloader.core.imageaware.ImageAware;
/*    */ import io.rong.imageloader.core.imageaware.ImageViewAware;
/*    */ 
/*    */ public class RoundedVignetteBitmapDisplayer extends RoundedBitmapDisplayer
/*    */ {
/*    */   public RoundedVignetteBitmapDisplayer(int cornerRadiusPixels, int marginPixels)
/*    */   {
/* 41 */     super(cornerRadiusPixels, marginPixels);
/*    */   }
/*    */ 
/*    */   public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom)
/*    */   {
/* 46 */     if (!(imageAware instanceof ImageViewAware)) {
/* 47 */       throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
/*    */     }
/*    */ 
/* 50 */     imageAware.setImageDrawable(new RoundedVignetteDrawable(bitmap, this.cornerRadius, this.margin));
/*    */   }
/*    */ 
/*    */   protected static class RoundedVignetteDrawable extends RoundedBitmapDisplayer.RoundedDrawable
/*    */   {
/*    */     RoundedVignetteDrawable(Bitmap bitmap, int cornerRadius, int margin) {
/* 56 */       super(cornerRadius, margin);
/*    */     }
/*    */ 
/*    */     protected void onBoundsChange(Rect bounds)
/*    */     {
/* 61 */       super.onBoundsChange(bounds);
/* 62 */       RadialGradient vignette = new RadialGradient(this.mRect.centerX(), this.mRect.centerY() * 1.0F / 0.7F, this.mRect.centerX() * 1.3F, new int[] { 0, 0, 2130706432 }, new float[] { 0.0F, 0.7F, 1.0F }, Shader.TileMode.CLAMP);
/*    */ 
/* 67 */       Matrix oval = new Matrix();
/* 68 */       oval.setScale(1.0F, 0.7F);
/* 69 */       vignette.setLocalMatrix(oval);
/*    */ 
/* 71 */       this.paint.setShader(new ComposeShader(this.bitmapShader, vignette, PorterDuff.Mode.SRC_OVER));
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.display.RoundedVignetteBitmapDisplayer
 * JD-Core Version:    0.6.0
 */
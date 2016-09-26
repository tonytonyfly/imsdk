/*     */ package io.rong.imageloader.core.display;
/*     */ 
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.BitmapShader;
/*     */ import android.graphics.Canvas;
/*     */ import android.graphics.ColorFilter;
/*     */ import android.graphics.Matrix;
/*     */ import android.graphics.Matrix.ScaleToFit;
/*     */ import android.graphics.Paint;
/*     */ import android.graphics.Rect;
/*     */ import android.graphics.RectF;
/*     */ import android.graphics.Shader.TileMode;
/*     */ import android.graphics.drawable.Drawable;
/*     */ import io.rong.imageloader.core.assist.LoadedFrom;
/*     */ import io.rong.imageloader.core.imageaware.ImageAware;
/*     */ import io.rong.imageloader.core.imageaware.ImageViewAware;
/*     */ 
/*     */ public class RoundedBitmapDisplayer
/*     */   implements BitmapDisplayer
/*     */ {
/*     */   protected final int cornerRadius;
/*     */   protected final int margin;
/*     */ 
/*     */   public RoundedBitmapDisplayer(int cornerRadiusPixels)
/*     */   {
/*  47 */     this(cornerRadiusPixels, 0);
/*     */   }
/*     */ 
/*     */   public RoundedBitmapDisplayer(int cornerRadiusPixels, int marginPixels) {
/*  51 */     this.cornerRadius = cornerRadiusPixels;
/*  52 */     this.margin = marginPixels;
/*     */   }
/*     */ 
/*     */   public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom)
/*     */   {
/*  57 */     if (!(imageAware instanceof ImageViewAware)) {
/*  58 */       throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
/*     */     }
/*     */ 
/*  61 */     imageAware.setImageDrawable(new RoundedDrawable(bitmap, this.cornerRadius, this.margin)); } 
/*     */   public static class RoundedDrawable extends Drawable { protected final float cornerRadius;
/*     */     protected final int margin;
/*  69 */     protected final RectF mRect = new RectF();
/*     */     protected final RectF mBitmapRect;
/*     */     protected final BitmapShader bitmapShader;
/*     */     protected final Paint paint;
/*     */ 
/*  75 */     public RoundedDrawable(Bitmap bitmap, int cornerRadius, int margin) { this.cornerRadius = cornerRadius;
/*  76 */       this.margin = margin;
/*     */ 
/*  78 */       this.bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
/*  79 */       this.mBitmapRect = new RectF(margin, margin, bitmap.getWidth() - margin, bitmap.getHeight() - margin);
/*     */ 
/*  81 */       this.paint = new Paint();
/*  82 */       this.paint.setAntiAlias(true);
/*  83 */       this.paint.setShader(this.bitmapShader);
/*  84 */       this.paint.setFilterBitmap(true);
/*  85 */       this.paint.setDither(true);
/*     */     }
/*     */ 
/*     */     protected void onBoundsChange(Rect bounds)
/*     */     {
/*  90 */       super.onBoundsChange(bounds);
/*  91 */       this.mRect.set(this.margin, this.margin, bounds.width() - this.margin, bounds.height() - this.margin);
/*     */ 
/*  94 */       Matrix shaderMatrix = new Matrix();
/*  95 */       shaderMatrix.setRectToRect(this.mBitmapRect, this.mRect, Matrix.ScaleToFit.FILL);
/*  96 */       this.bitmapShader.setLocalMatrix(shaderMatrix);
/*     */     }
/*     */ 
/*     */     public void draw(Canvas canvas)
/*     */     {
/* 102 */       canvas.drawRoundRect(this.mRect, this.cornerRadius, this.cornerRadius, this.paint);
/*     */     }
/*     */ 
/*     */     public int getOpacity()
/*     */     {
/* 107 */       return -3;
/*     */     }
/*     */ 
/*     */     public void setAlpha(int alpha)
/*     */     {
/* 112 */       this.paint.setAlpha(alpha);
/*     */     }
/*     */ 
/*     */     public void setColorFilter(ColorFilter cf)
/*     */     {
/* 117 */       this.paint.setColorFilter(cf);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.display.RoundedBitmapDisplayer
 * JD-Core Version:    0.6.0
 */
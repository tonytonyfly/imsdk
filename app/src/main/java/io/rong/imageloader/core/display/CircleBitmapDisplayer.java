/*     */ package io.rong.imageloader.core.display;
/*     */ 
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.BitmapShader;
/*     */ import android.graphics.Canvas;
/*     */ import android.graphics.ColorFilter;
/*     */ import android.graphics.Matrix;
/*     */ import android.graphics.Matrix.ScaleToFit;
/*     */ import android.graphics.Paint;
/*     */ import android.graphics.Paint.Style;
/*     */ import android.graphics.Rect;
/*     */ import android.graphics.RectF;
/*     */ import android.graphics.Shader.TileMode;
/*     */ import android.graphics.drawable.Drawable;
/*     */ import io.rong.imageloader.core.assist.LoadedFrom;
/*     */ import io.rong.imageloader.core.imageaware.ImageAware;
/*     */ import io.rong.imageloader.core.imageaware.ImageViewAware;
/*     */ 
/*     */ public class CircleBitmapDisplayer
/*     */   implements BitmapDisplayer
/*     */ {
/*     */   protected final Integer strokeColor;
/*     */   protected final float strokeWidth;
/*     */ 
/*     */   public CircleBitmapDisplayer()
/*     */   {
/*  51 */     this(null);
/*     */   }
/*     */ 
/*     */   public CircleBitmapDisplayer(Integer strokeColor) {
/*  55 */     this(strokeColor, 0.0F);
/*     */   }
/*     */ 
/*     */   public CircleBitmapDisplayer(Integer strokeColor, float strokeWidth) {
/*  59 */     this.strokeColor = strokeColor;
/*  60 */     this.strokeWidth = strokeWidth;
/*     */   }
/*     */ 
/*     */   public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom)
/*     */   {
/*  65 */     if (!(imageAware instanceof ImageViewAware)) {
/*  66 */       throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
/*     */     }
/*     */ 
/*  69 */     imageAware.setImageDrawable(new CircleDrawable(bitmap, this.strokeColor, this.strokeWidth)); } 
/*     */   public static class CircleDrawable extends Drawable { protected float radius;
/*  76 */     protected final RectF mRect = new RectF();
/*     */     protected final RectF mBitmapRect;
/*     */     protected final BitmapShader bitmapShader;
/*     */     protected final Paint paint;
/*     */     protected final Paint strokePaint;
/*     */     protected final float strokeWidth;
/*     */     protected float strokeRadius;
/*     */ 
/*  85 */     public CircleDrawable(Bitmap bitmap, Integer strokeColor, float strokeWidth) { this.radius = (Math.min(bitmap.getWidth(), bitmap.getHeight()) / 2);
/*     */ 
/*  87 */       this.bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
/*  88 */       this.mBitmapRect = new RectF(0.0F, 0.0F, bitmap.getWidth(), bitmap.getHeight());
/*     */ 
/*  90 */       this.paint = new Paint();
/*  91 */       this.paint.setAntiAlias(true);
/*  92 */       this.paint.setShader(this.bitmapShader);
/*  93 */       this.paint.setFilterBitmap(true);
/*  94 */       this.paint.setDither(true);
/*     */ 
/*  96 */       if (strokeColor == null) {
/*  97 */         this.strokePaint = null;
/*     */       } else {
/*  99 */         this.strokePaint = new Paint();
/* 100 */         this.strokePaint.setStyle(Paint.Style.STROKE);
/* 101 */         this.strokePaint.setColor(strokeColor.intValue());
/* 102 */         this.strokePaint.setStrokeWidth(strokeWidth);
/* 103 */         this.strokePaint.setAntiAlias(true);
/*     */       }
/* 105 */       this.strokeWidth = strokeWidth;
/* 106 */       this.strokeRadius = (this.radius - strokeWidth / 2.0F);
/*     */     }
/*     */ 
/*     */     protected void onBoundsChange(Rect bounds)
/*     */     {
/* 111 */       super.onBoundsChange(bounds);
/* 112 */       this.mRect.set(0.0F, 0.0F, bounds.width(), bounds.height());
/* 113 */       this.radius = (Math.min(bounds.width(), bounds.height()) / 2);
/* 114 */       this.strokeRadius = (this.radius - this.strokeWidth / 2.0F);
/*     */ 
/* 117 */       Matrix shaderMatrix = new Matrix();
/* 118 */       shaderMatrix.setRectToRect(this.mBitmapRect, this.mRect, Matrix.ScaleToFit.FILL);
/* 119 */       this.bitmapShader.setLocalMatrix(shaderMatrix);
/*     */     }
/*     */ 
/*     */     public void draw(Canvas canvas)
/*     */     {
/* 124 */       canvas.drawCircle(this.radius, this.radius, this.radius, this.paint);
/* 125 */       if (this.strokePaint != null)
/* 126 */         canvas.drawCircle(this.radius, this.radius, this.strokeRadius, this.strokePaint);
/*     */     }
/*     */ 
/*     */     public int getOpacity()
/*     */     {
/* 132 */       return -3;
/*     */     }
/*     */ 
/*     */     public void setAlpha(int alpha)
/*     */     {
/* 137 */       this.paint.setAlpha(alpha);
/*     */     }
/*     */ 
/*     */     public void setColorFilter(ColorFilter cf)
/*     */     {
/* 142 */       this.paint.setColorFilter(cf);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.display.CircleBitmapDisplayer
 * JD-Core Version:    0.6.0
 */
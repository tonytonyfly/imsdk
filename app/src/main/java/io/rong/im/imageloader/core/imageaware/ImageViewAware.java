/*     */ package io.rong.imageloader.core.imageaware;
/*     */ 
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.drawable.AnimationDrawable;
/*     */ import android.graphics.drawable.Drawable;
/*     */ import android.view.View;
/*     */ import android.widget.ImageView;
/*     */ import io.rong.imageloader.core.assist.ViewScaleType;
/*     */ import io.rong.imageloader.utils.L;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.reflect.Field;
/*     */ 
/*     */ public class ImageViewAware extends ViewAware
/*     */ {
/*     */   public ImageViewAware(ImageView imageView)
/*     */   {
/*  44 */     super(imageView);
/*     */   }
/*     */ 
/*     */   public ImageViewAware(ImageView imageView, boolean checkActualViewSize)
/*     */   {
/*  65 */     super(imageView, checkActualViewSize);
/*     */   }
/*     */ 
/*     */   public int getWidth()
/*     */   {
/*  75 */     int width = super.getWidth();
/*  76 */     if (width <= 0) {
/*  77 */       ImageView imageView = (ImageView)this.viewRef.get();
/*  78 */       if (imageView != null) {
/*  79 */         width = getImageViewFieldValue(imageView, "mMaxWidth");
/*     */       }
/*     */     }
/*  82 */     return width;
/*     */   }
/*     */ 
/*     */   public int getHeight()
/*     */   {
/*  92 */     int height = super.getHeight();
/*  93 */     if (height <= 0) {
/*  94 */       ImageView imageView = (ImageView)this.viewRef.get();
/*  95 */       if (imageView != null) {
/*  96 */         height = getImageViewFieldValue(imageView, "mMaxHeight");
/*     */       }
/*     */     }
/*  99 */     return height;
/*     */   }
/*     */ 
/*     */   public ViewScaleType getScaleType()
/*     */   {
/* 104 */     ImageView imageView = (ImageView)this.viewRef.get();
/* 105 */     if (imageView != null) {
/* 106 */       return ViewScaleType.fromImageView(imageView);
/*     */     }
/* 108 */     return super.getScaleType();
/*     */   }
/*     */ 
/*     */   public ImageView getWrappedView()
/*     */   {
/* 113 */     return (ImageView)super.getWrappedView();
/*     */   }
/*     */ 
/*     */   protected void setImageDrawableInto(Drawable drawable, View view)
/*     */   {
/* 118 */     ((ImageView)view).setImageDrawable(drawable);
/* 119 */     if ((drawable instanceof AnimationDrawable))
/* 120 */       ((AnimationDrawable)drawable).start();
/*     */   }
/*     */ 
/*     */   protected void setImageBitmapInto(Bitmap bitmap, View view)
/*     */   {
/* 126 */     ((ImageView)view).setImageBitmap(bitmap);
/*     */   }
/*     */ 
/*     */   private static int getImageViewFieldValue(Object object, String fieldName) {
/* 130 */     int value = 0;
/*     */     try {
/* 132 */       Field field = ImageView.class.getDeclaredField(fieldName);
/* 133 */       field.setAccessible(true);
/* 134 */       int fieldValue = ((Integer)field.get(object)).intValue();
/* 135 */       if ((fieldValue > 0) && (fieldValue < 2147483647))
/* 136 */         value = fieldValue;
/*     */     }
/*     */     catch (Exception e) {
/* 139 */       L.e(e);
/*     */     }
/* 141 */     return value;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.imageaware.ImageViewAware
 * JD-Core Version:    0.6.0
 */
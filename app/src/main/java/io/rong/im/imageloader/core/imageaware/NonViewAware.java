/*    */ package io.rong.imageloader.core.imageaware;
/*    */ 
/*    */ import android.graphics.Bitmap;
/*    */ import android.graphics.drawable.Drawable;
/*    */ import android.text.TextUtils;
/*    */ import android.view.View;
/*    */ import io.rong.imageloader.core.assist.ImageSize;
/*    */ import io.rong.imageloader.core.assist.ViewScaleType;
/*    */ 
/*    */ public class NonViewAware
/*    */   implements ImageAware
/*    */ {
/*    */   protected final String imageUri;
/*    */   protected final ImageSize imageSize;
/*    */   protected final ViewScaleType scaleType;
/*    */ 
/*    */   public NonViewAware(ImageSize imageSize, ViewScaleType scaleType)
/*    */   {
/* 41 */     this(null, imageSize, scaleType);
/*    */   }
/*    */ 
/*    */   public NonViewAware(String imageUri, ImageSize imageSize, ViewScaleType scaleType) {
/* 45 */     if (imageSize == null) throw new IllegalArgumentException("imageSize must not be null");
/* 46 */     if (scaleType == null) throw new IllegalArgumentException("scaleType must not be null");
/*    */ 
/* 48 */     this.imageUri = imageUri;
/* 49 */     this.imageSize = imageSize;
/* 50 */     this.scaleType = scaleType;
/*    */   }
/*    */ 
/*    */   public int getWidth()
/*    */   {
/* 55 */     return this.imageSize.getWidth();
/*    */   }
/*    */ 
/*    */   public int getHeight()
/*    */   {
/* 60 */     return this.imageSize.getHeight();
/*    */   }
/*    */ 
/*    */   public ViewScaleType getScaleType()
/*    */   {
/* 65 */     return this.scaleType;
/*    */   }
/*    */ 
/*    */   public View getWrappedView()
/*    */   {
/* 70 */     return null;
/*    */   }
/*    */ 
/*    */   public boolean isCollected()
/*    */   {
/* 75 */     return false;
/*    */   }
/*    */ 
/*    */   public int getId()
/*    */   {
/* 80 */     return TextUtils.isEmpty(this.imageUri) ? super.hashCode() : this.imageUri.hashCode();
/*    */   }
/*    */ 
/*    */   public boolean setImageDrawable(Drawable drawable)
/*    */   {
/* 85 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean setImageBitmap(Bitmap bitmap)
/*    */   {
/* 90 */     return true;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.imageaware.NonViewAware
 * JD-Core Version:    0.6.0
 */
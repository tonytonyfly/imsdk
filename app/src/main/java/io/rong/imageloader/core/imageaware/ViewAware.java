/*     */ package io.rong.imageloader.core.imageaware;
/*     */ 
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.drawable.Drawable;
/*     */ import android.os.Looper;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup.LayoutParams;
/*     */ import io.rong.imageloader.core.assist.ViewScaleType;
/*     */ import io.rong.imageloader.utils.L;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.WeakReference;
/*     */ 
/*     */ public abstract class ViewAware
/*     */   implements ImageAware
/*     */ {
/*     */   public static final String WARN_CANT_SET_DRAWABLE = "Can't set a drawable into view. You should call ImageLoader on UI thread for it.";
/*     */   public static final String WARN_CANT_SET_BITMAP = "Can't set a bitmap into view. You should call ImageLoader on UI thread for it.";
/*     */   protected Reference<View> viewRef;
/*     */   protected boolean checkActualViewSize;
/*     */ 
/*     */   public ViewAware(View view)
/*     */   {
/*  50 */     this(view, true);
/*     */   }
/*     */ 
/*     */   public ViewAware(View view, boolean checkActualViewSize)
/*     */   {
/*  70 */     if (view == null) throw new IllegalArgumentException("view must not be null");
/*     */ 
/*  72 */     this.viewRef = new WeakReference(view);
/*  73 */     this.checkActualViewSize = checkActualViewSize;
/*     */   }
/*     */ 
/*     */   public int getWidth()
/*     */   {
/*  87 */     View view = (View)this.viewRef.get();
/*  88 */     if (view != null) {
/*  89 */       ViewGroup.LayoutParams params = view.getLayoutParams();
/*  90 */       int width = 0;
/*  91 */       if ((this.checkActualViewSize) && (params != null) && (params.width != -2)) {
/*  92 */         width = view.getWidth();
/*     */       }
/*  94 */       if ((width <= 0) && (params != null)) width = params.width;
/*  95 */       return width;
/*     */     }
/*  97 */     return 0;
/*     */   }
/*     */ 
/*     */   public int getHeight()
/*     */   {
/* 111 */     View view = (View)this.viewRef.get();
/* 112 */     if (view != null) {
/* 113 */       ViewGroup.LayoutParams params = view.getLayoutParams();
/* 114 */       int height = 0;
/* 115 */       if ((this.checkActualViewSize) && (params != null) && (params.height != -2)) {
/* 116 */         height = view.getHeight();
/*     */       }
/* 118 */       if ((height <= 0) && (params != null)) height = params.height;
/* 119 */       return height;
/*     */     }
/* 121 */     return 0;
/*     */   }
/*     */ 
/*     */   public ViewScaleType getScaleType()
/*     */   {
/* 126 */     return ViewScaleType.CROP;
/*     */   }
/*     */ 
/*     */   public View getWrappedView()
/*     */   {
/* 131 */     return (View)this.viewRef.get();
/*     */   }
/*     */ 
/*     */   public boolean isCollected()
/*     */   {
/* 136 */     return this.viewRef.get() == null;
/*     */   }
/*     */ 
/*     */   public int getId()
/*     */   {
/* 141 */     View view = (View)this.viewRef.get();
/* 142 */     return view == null ? super.hashCode() : view.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean setImageDrawable(Drawable drawable)
/*     */   {
/* 147 */     if (Looper.myLooper() == Looper.getMainLooper()) {
/* 148 */       View view = (View)this.viewRef.get();
/* 149 */       if (view != null) {
/* 150 */         setImageDrawableInto(drawable, view);
/* 151 */         return true;
/*     */       }
/*     */     } else {
/* 154 */       L.w("Can't set a drawable into view. You should call ImageLoader on UI thread for it.", new Object[0]);
/*     */     }
/* 156 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean setImageBitmap(Bitmap bitmap)
/*     */   {
/* 161 */     if (Looper.myLooper() == Looper.getMainLooper()) {
/* 162 */       View view = (View)this.viewRef.get();
/* 163 */       if (view != null) {
/* 164 */         setImageBitmapInto(bitmap, view);
/* 165 */         return true;
/*     */       }
/*     */     } else {
/* 168 */       L.w("Can't set a bitmap into view. You should call ImageLoader on UI thread for it.", new Object[0]);
/*     */     }
/* 170 */     return false;
/*     */   }
/*     */ 
/*     */   protected abstract void setImageDrawableInto(Drawable paramDrawable, View paramView);
/*     */ 
/*     */   protected abstract void setImageBitmapInto(Bitmap paramBitmap, View paramView);
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.imageaware.ViewAware
 * JD-Core Version:    0.6.0
 */
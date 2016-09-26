/*     */ package io.rong.photoview;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.Matrix;
/*     */ import android.graphics.RectF;
/*     */ import android.graphics.drawable.Drawable;
/*     */ import android.net.Uri;
/*     */ import android.util.AttributeSet;
/*     */ import android.view.GestureDetector.OnDoubleTapListener;
/*     */ import android.view.View.OnLongClickListener;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.ImageView.ScaleType;
/*     */ 
/*     */ public class PhotoView extends ImageView
/*     */   implements IPhotoView
/*     */ {
/*     */   private PhotoViewAttacher mAttacher;
/*     */   private ImageView.ScaleType mPendingScaleType;
/*     */ 
/*     */   public PhotoView(Context context)
/*     */   {
/*  36 */     this(context, null);
/*     */   }
/*     */ 
/*     */   public PhotoView(Context context, AttributeSet attr) {
/*  40 */     this(context, attr, 0);
/*     */   }
/*     */ 
/*     */   public PhotoView(Context context, AttributeSet attr, int defStyle) {
/*  44 */     super(context, attr, defStyle);
/*  45 */     super.setScaleType(ImageView.ScaleType.MATRIX);
/*  46 */     init();
/*     */   }
/*     */ 
/*     */   protected void init() {
/*  50 */     if ((null == this.mAttacher) || (null == this.mAttacher.getImageView())) {
/*  51 */       this.mAttacher = new PhotoViewAttacher(this);
/*     */     }
/*     */ 
/*  54 */     if (null != this.mPendingScaleType) {
/*  55 */       setScaleType(this.mPendingScaleType);
/*  56 */       this.mPendingScaleType = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setPhotoViewRotation(float rotationDegree)
/*     */   {
/*  65 */     this.mAttacher.setRotationTo(rotationDegree);
/*     */   }
/*     */ 
/*     */   public void setRotationTo(float rotationDegree)
/*     */   {
/*  70 */     this.mAttacher.setRotationTo(rotationDegree);
/*     */   }
/*     */ 
/*     */   public void setRotationBy(float rotationDegree)
/*     */   {
/*  75 */     this.mAttacher.setRotationBy(rotationDegree);
/*     */   }
/*     */ 
/*     */   public boolean canZoom()
/*     */   {
/*  80 */     return this.mAttacher.canZoom();
/*     */   }
/*     */ 
/*     */   public RectF getDisplayRect()
/*     */   {
/*  85 */     return this.mAttacher.getDisplayRect();
/*     */   }
/*     */ 
/*     */   public Matrix getDisplayMatrix()
/*     */   {
/*  90 */     return this.mAttacher.getDisplayMatrix();
/*     */   }
/*     */ 
/*     */   public void getDisplayMatrix(Matrix matrix)
/*     */   {
/*  95 */     this.mAttacher.getDisplayMatrix(matrix);
/*     */   }
/*     */ 
/*     */   public boolean setDisplayMatrix(Matrix finalRectangle)
/*     */   {
/* 100 */     return this.mAttacher.setDisplayMatrix(finalRectangle);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public float getMinScale() {
/* 106 */     return getMinimumScale();
/*     */   }
/*     */ 
/*     */   public float getMinimumScale()
/*     */   {
/* 111 */     return this.mAttacher.getMinimumScale();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public float getMidScale() {
/* 117 */     return getMediumScale();
/*     */   }
/*     */ 
/*     */   public float getMediumScale()
/*     */   {
/* 122 */     return this.mAttacher.getMediumScale();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public float getMaxScale() {
/* 128 */     return getMaximumScale();
/*     */   }
/*     */ 
/*     */   public float getMaximumScale()
/*     */   {
/* 133 */     return this.mAttacher.getMaximumScale();
/*     */   }
/*     */ 
/*     */   public float getScale()
/*     */   {
/* 138 */     return this.mAttacher.getScale();
/*     */   }
/*     */ 
/*     */   public ImageView.ScaleType getScaleType()
/*     */   {
/* 143 */     return this.mAttacher.getScaleType();
/*     */   }
/*     */ 
/*     */   public void setAllowParentInterceptOnEdge(boolean allow)
/*     */   {
/* 148 */     this.mAttacher.setAllowParentInterceptOnEdge(allow);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void setMinScale(float minScale) {
/* 154 */     setMinimumScale(minScale);
/*     */   }
/*     */ 
/*     */   public void setMinimumScale(float minimumScale)
/*     */   {
/* 159 */     this.mAttacher.setMinimumScale(minimumScale);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void setMidScale(float midScale) {
/* 165 */     setMediumScale(midScale);
/*     */   }
/*     */ 
/*     */   public void setMediumScale(float mediumScale)
/*     */   {
/* 170 */     this.mAttacher.setMediumScale(mediumScale);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void setMaxScale(float maxScale) {
/* 176 */     setMaximumScale(maxScale);
/*     */   }
/*     */ 
/*     */   public void setMaximumScale(float maximumScale)
/*     */   {
/* 181 */     this.mAttacher.setMaximumScale(maximumScale);
/*     */   }
/*     */ 
/*     */   public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale)
/*     */   {
/* 186 */     this.mAttacher.setScaleLevels(minimumScale, mediumScale, maximumScale);
/*     */   }
/*     */ 
/*     */   public void setImageDrawable(Drawable drawable)
/*     */   {
/* 192 */     super.setImageDrawable(drawable);
/* 193 */     if (null != this.mAttacher)
/* 194 */       this.mAttacher.update();
/*     */   }
/*     */ 
/*     */   public void setImageResource(int resId)
/*     */   {
/* 200 */     super.setImageResource(resId);
/* 201 */     if (null != this.mAttacher)
/* 202 */       this.mAttacher.update();
/*     */   }
/*     */ 
/*     */   public void setImageURI(Uri uri)
/*     */   {
/* 208 */     super.setImageURI(uri);
/* 209 */     if (null != this.mAttacher)
/* 210 */       this.mAttacher.update();
/*     */   }
/*     */ 
/*     */   public void setOnMatrixChangeListener(PhotoViewAttacher.OnMatrixChangedListener listener)
/*     */   {
/* 216 */     this.mAttacher.setOnMatrixChangeListener(listener);
/*     */   }
/*     */ 
/*     */   public void setOnLongClickListener(View.OnLongClickListener l)
/*     */   {
/* 221 */     this.mAttacher.setOnLongClickListener(l);
/*     */   }
/*     */ 
/*     */   public void setOnPhotoTapListener(PhotoViewAttacher.OnPhotoTapListener listener)
/*     */   {
/* 226 */     this.mAttacher.setOnPhotoTapListener(listener);
/*     */   }
/*     */ 
/*     */   public PhotoViewAttacher.OnPhotoTapListener getOnPhotoTapListener()
/*     */   {
/* 231 */     return this.mAttacher.getOnPhotoTapListener();
/*     */   }
/*     */ 
/*     */   public void setOnViewTapListener(PhotoViewAttacher.OnViewTapListener listener)
/*     */   {
/* 236 */     this.mAttacher.setOnViewTapListener(listener);
/*     */   }
/*     */ 
/*     */   public PhotoViewAttacher.OnViewTapListener getOnViewTapListener()
/*     */   {
/* 241 */     return this.mAttacher.getOnViewTapListener();
/*     */   }
/*     */ 
/*     */   public void setScale(float scale)
/*     */   {
/* 246 */     this.mAttacher.setScale(scale);
/*     */   }
/*     */ 
/*     */   public void setScale(float scale, boolean animate)
/*     */   {
/* 251 */     this.mAttacher.setScale(scale, animate);
/*     */   }
/*     */ 
/*     */   public void setScale(float scale, float focalX, float focalY, boolean animate)
/*     */   {
/* 256 */     this.mAttacher.setScale(scale, focalX, focalY, animate);
/*     */   }
/*     */ 
/*     */   public void setScaleType(ImageView.ScaleType scaleType)
/*     */   {
/* 261 */     if (null != this.mAttacher)
/* 262 */       this.mAttacher.setScaleType(scaleType);
/*     */     else
/* 264 */       this.mPendingScaleType = scaleType;
/*     */   }
/*     */ 
/*     */   public void setZoomable(boolean zoomable)
/*     */   {
/* 270 */     this.mAttacher.setZoomable(zoomable);
/*     */   }
/*     */ 
/*     */   public Bitmap getVisibleRectangleBitmap()
/*     */   {
/* 275 */     return this.mAttacher.getVisibleRectangleBitmap();
/*     */   }
/*     */ 
/*     */   public void setZoomTransitionDuration(int milliseconds)
/*     */   {
/* 280 */     this.mAttacher.setZoomTransitionDuration(milliseconds);
/*     */   }
/*     */ 
/*     */   public IPhotoView getIPhotoViewImplementation()
/*     */   {
/* 285 */     return this.mAttacher;
/*     */   }
/*     */ 
/*     */   public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener newOnDoubleTapListener)
/*     */   {
/* 290 */     this.mAttacher.setOnDoubleTapListener(newOnDoubleTapListener);
/*     */   }
/*     */ 
/*     */   public void setOnScaleChangeListener(PhotoViewAttacher.OnScaleChangeListener onScaleChangeListener)
/*     */   {
/* 295 */     this.mAttacher.setOnScaleChangeListener(onScaleChangeListener);
/*     */   }
/*     */ 
/*     */   public void setOnSingleFlingListener(PhotoViewAttacher.OnSingleFlingListener onSingleFlingListener)
/*     */   {
/* 300 */     this.mAttacher.setOnSingleFlingListener(onSingleFlingListener);
/*     */   }
/*     */ 
/*     */   protected void onDetachedFromWindow()
/*     */   {
/* 305 */     this.mAttacher.cleanup();
/* 306 */     super.onDetachedFromWindow();
/*     */   }
/*     */ 
/*     */   protected void onAttachedToWindow()
/*     */   {
/* 311 */     init();
/* 312 */     super.onAttachedToWindow();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.photoview.PhotoView
 * JD-Core Version:    0.6.0
 */
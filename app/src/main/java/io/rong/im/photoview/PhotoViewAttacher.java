/*      */ package io.rong.photoview;
/*      */ 
/*      */ import android.annotation.SuppressLint;
/*      */ import android.content.Context;
/*      */ import android.graphics.Bitmap;
/*      */ import android.graphics.Matrix;
/*      */ import android.graphics.Matrix.ScaleToFit;
/*      */ import android.graphics.RectF;
/*      */ import android.graphics.drawable.Drawable;
/*      */ import android.support.v4.view.MotionEventCompat;
/*      */ import android.util.Log;
/*      */ import android.view.GestureDetector.OnDoubleTapListener;
/*      */ import android.view.GestureDetector.SimpleOnGestureListener;
/*      */ import android.view.MotionEvent;
/*      */ import android.view.View;
/*      */ import android.view.View.OnLongClickListener;
/*      */ import android.view.View.OnTouchListener;
/*      */ import android.view.ViewParent;
/*      */ import android.view.ViewTreeObserver;
/*      */ import android.view.ViewTreeObserver.OnGlobalLayoutListener;
/*      */ import android.view.animation.AccelerateDecelerateInterpolator;
/*      */ import android.view.animation.Interpolator;
/*      */ import android.widget.ImageView;
/*      */ import android.widget.ImageView.ScaleType;
/*      */ import io.rong.photoview.gestures.OnGestureListener;
/*      */ import io.rong.photoview.gestures.VersionedGestureDetector;
/*      */ import io.rong.photoview.log.LogManager;
/*      */ import io.rong.photoview.log.Logger;
/*      */ import io.rong.photoview.scrollerproxy.ScrollerProxy;
/*      */ import java.lang.ref.WeakReference;
/*      */ 
/*      */ public class PhotoViewAttacher
/*      */   implements IPhotoView, View.OnTouchListener, OnGestureListener, ViewTreeObserver.OnGlobalLayoutListener
/*      */ {
/*      */   private static final String LOG_TAG = "PhotoViewAttacher";
/*   60 */   private static final boolean DEBUG = Log.isLoggable("PhotoViewAttacher", 3);
/*      */ 
/*   62 */   static final Interpolator sInterpolator = new AccelerateDecelerateInterpolator();
/*   63 */   int ZOOM_DURATION = 200;
/*      */   static final int EDGE_NONE = -1;
/*      */   static final int EDGE_LEFT = 0;
/*      */   static final int EDGE_RIGHT = 1;
/*      */   static final int EDGE_BOTH = 2;
/*   70 */   static int SINGLE_TOUCH = 1;
/*      */ 
/*   72 */   private float mMinScale = 1.0F;
/*   73 */   private float mMidScale = 1.75F;
/*   74 */   private float mMaxScale = 3.0F;
/*      */ 
/*   76 */   private boolean mAllowParentInterceptOnEdge = true;
/*   77 */   private boolean mBlockParentIntercept = false;
/*      */   private WeakReference<ImageView> mImageView;
/*      */   private android.view.GestureDetector mGestureDetector;
/*      */   private io.rong.photoview.gestures.GestureDetector mScaleDragDetector;
/*  137 */   private final Matrix mBaseMatrix = new Matrix();
/*  138 */   private final Matrix mDrawMatrix = new Matrix();
/*  139 */   private final Matrix mSuppMatrix = new Matrix();
/*  140 */   private final RectF mDisplayRect = new RectF();
/*  141 */   private final float[] mMatrixValues = new float[9];
/*      */   private OnMatrixChangedListener mMatrixChangeListener;
/*      */   private OnPhotoTapListener mPhotoTapListener;
/*      */   private OnViewTapListener mViewTapListener;
/*      */   private View.OnLongClickListener mLongClickListener;
/*      */   private OnScaleChangeListener mScaleChangeListener;
/*      */   private OnSingleFlingListener mSingleFlingListener;
/*      */   private int mIvTop;
/*      */   private int mIvRight;
/*      */   private int mIvBottom;
/*      */   private int mIvLeft;
/*      */   private FlingRunnable mCurrentFlingRunnable;
/*  153 */   private int mScrollEdge = 2;
/*      */   private float mBaseRotation;
/*      */   private boolean mZoomEnabled;
/*  157 */   private ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_CENTER;
/*      */ 
/*      */   private static void checkZoomLevels(float minZoom, float midZoom, float maxZoom)
/*      */   {
/*   81 */     if (minZoom >= midZoom) {
/*   82 */       throw new IllegalArgumentException("MinZoom has to be less than MidZoom");
/*      */     }
/*   84 */     if (midZoom >= maxZoom)
/*   85 */       throw new IllegalArgumentException("MidZoom has to be less than MaxZoom");
/*      */   }
/*      */ 
/*      */   private static boolean hasDrawable(ImageView imageView)
/*      */   {
/*   94 */     return (null != imageView) && (null != imageView.getDrawable());
/*      */   }
/*      */ 
/*      */   private static boolean isSupportedScaleType(ImageView.ScaleType scaleType)
/*      */   {
/*  101 */     if (null == scaleType) {
/*  102 */       return false;
/*      */     }
/*      */ 
/*  105 */     switch (2.$SwitchMap$android$widget$ImageView$ScaleType[scaleType.ordinal()]) {
/*      */     case 1:
/*  107 */       throw new IllegalArgumentException(scaleType.name() + " is not supported in PhotoView");
/*      */     }
/*      */ 
/*  111 */     return true;
/*      */   }
/*      */ 
/*      */   private static void setImageViewScaleTypeMatrix(ImageView imageView)
/*      */   {
/*  123 */     if ((null != imageView) && (!(imageView instanceof ViewTreeObserver.OnGlobalLayoutListener)) && 
/*  124 */       (!ImageView.ScaleType.MATRIX.equals(imageView.getScaleType())))
/*  125 */       imageView.setScaleType(ImageView.ScaleType.MATRIX);
/*      */   }
/*      */ 
/*      */   public PhotoViewAttacher(ImageView imageView)
/*      */   {
/*  160 */     this(imageView, true);
/*      */   }
/*      */ 
/*      */   public PhotoViewAttacher(ImageView imageView, boolean zoomable) {
/*  164 */     this.mImageView = new WeakReference(imageView);
/*      */ 
/*  166 */     imageView.setDrawingCacheEnabled(true);
/*  167 */     imageView.setOnTouchListener(this);
/*      */ 
/*  169 */     ViewTreeObserver observer = imageView.getViewTreeObserver();
/*  170 */     if (null != observer) {
/*  171 */       observer.addOnGlobalLayoutListener(this);
/*      */     }
/*      */ 
/*  174 */     setImageViewScaleTypeMatrix(imageView);
/*      */ 
/*  176 */     if (imageView.isInEditMode()) {
/*  177 */       return;
/*      */     }
/*      */ 
/*  180 */     this.mScaleDragDetector = VersionedGestureDetector.newInstance(imageView.getContext(), this);
/*      */ 
/*  183 */     this.mGestureDetector = new android.view.GestureDetector(imageView.getContext(), new GestureDetector.SimpleOnGestureListener()
/*      */     {
/*      */       public void onLongPress(MotionEvent e)
/*      */       {
/*  189 */         if (null != PhotoViewAttacher.this.mLongClickListener)
/*  190 */           PhotoViewAttacher.this.mLongClickListener.onLongClick(PhotoViewAttacher.this.getImageView());
/*      */       }
/*      */ 
/*      */       public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
/*      */       {
/*  197 */         if (PhotoViewAttacher.this.mSingleFlingListener != null) {
/*  198 */           if (PhotoViewAttacher.this.getScale() > 1.0F) {
/*  199 */             return false;
/*      */           }
/*      */ 
/*  202 */           if ((MotionEventCompat.getPointerCount(e1) > PhotoViewAttacher.SINGLE_TOUCH) || (MotionEventCompat.getPointerCount(e2) > PhotoViewAttacher.SINGLE_TOUCH))
/*      */           {
/*  204 */             return false;
/*      */           }
/*      */ 
/*  207 */           return PhotoViewAttacher.this.mSingleFlingListener.onFling(e1, e2, velocityX, velocityY);
/*      */         }
/*  209 */         return false;
/*      */       }
/*      */     });
/*  213 */     this.mGestureDetector.setOnDoubleTapListener(new DefaultOnDoubleTapListener(this));
/*  214 */     this.mBaseRotation = 0.0F;
/*      */ 
/*  217 */     setZoomable(zoomable);
/*      */   }
/*      */ 
/*      */   public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener newOnDoubleTapListener)
/*      */   {
/*  222 */     if (newOnDoubleTapListener != null)
/*  223 */       this.mGestureDetector.setOnDoubleTapListener(newOnDoubleTapListener);
/*      */     else
/*  225 */       this.mGestureDetector.setOnDoubleTapListener(new DefaultOnDoubleTapListener(this));
/*      */   }
/*      */ 
/*      */   public void setOnScaleChangeListener(OnScaleChangeListener onScaleChangeListener)
/*      */   {
/*  231 */     this.mScaleChangeListener = onScaleChangeListener;
/*      */   }
/*      */ 
/*      */   public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener)
/*      */   {
/*  236 */     this.mSingleFlingListener = onSingleFlingListener;
/*      */   }
/*      */ 
/*      */   public boolean canZoom()
/*      */   {
/*  241 */     return this.mZoomEnabled;
/*      */   }
/*      */ 
/*      */   public void cleanup()
/*      */   {
/*  252 */     if (null == this.mImageView) {
/*  253 */       return;
/*      */     }
/*      */ 
/*  256 */     ImageView imageView = (ImageView)this.mImageView.get();
/*      */ 
/*  258 */     if (null != imageView)
/*      */     {
/*  260 */       ViewTreeObserver observer = imageView.getViewTreeObserver();
/*  261 */       if ((null != observer) && (observer.isAlive())) {
/*  262 */         observer.removeGlobalOnLayoutListener(this);
/*      */       }
/*      */ 
/*  266 */       imageView.setOnTouchListener(null);
/*      */ 
/*  269 */       cancelFling();
/*      */     }
/*      */ 
/*  272 */     if (null != this.mGestureDetector) {
/*  273 */       this.mGestureDetector.setOnDoubleTapListener(null);
/*      */     }
/*      */ 
/*  277 */     this.mMatrixChangeListener = null;
/*  278 */     this.mPhotoTapListener = null;
/*  279 */     this.mViewTapListener = null;
/*      */ 
/*  282 */     this.mImageView = null;
/*      */   }
/*      */ 
/*      */   public RectF getDisplayRect()
/*      */   {
/*  287 */     checkMatrixBounds();
/*  288 */     return getDisplayRect(getDrawMatrix());
/*      */   }
/*      */ 
/*      */   public boolean setDisplayMatrix(Matrix finalMatrix)
/*      */   {
/*  293 */     if (finalMatrix == null) {
/*  294 */       throw new IllegalArgumentException("Matrix cannot be null");
/*      */     }
/*  296 */     ImageView imageView = getImageView();
/*  297 */     if (null == imageView) {
/*  298 */       return false;
/*      */     }
/*  300 */     if (null == imageView.getDrawable()) {
/*  301 */       return false;
/*      */     }
/*  303 */     this.mSuppMatrix.set(finalMatrix);
/*  304 */     setImageViewMatrix(getDrawMatrix());
/*  305 */     checkMatrixBounds();
/*      */ 
/*  307 */     return true;
/*      */   }
/*      */ 
/*      */   public void setBaseRotation(float degrees) {
/*  311 */     this.mBaseRotation = (degrees % 360.0F);
/*  312 */     update();
/*  313 */     setRotationBy(this.mBaseRotation);
/*  314 */     checkAndDisplayMatrix();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setPhotoViewRotation(float degrees)
/*      */   {
/*  322 */     this.mSuppMatrix.setRotate(degrees % 360.0F);
/*  323 */     checkAndDisplayMatrix();
/*      */   }
/*      */ 
/*      */   public void setRotationTo(float degrees)
/*      */   {
/*  328 */     this.mSuppMatrix.setRotate(degrees % 360.0F);
/*  329 */     checkAndDisplayMatrix();
/*      */   }
/*      */ 
/*      */   public void setRotationBy(float degrees)
/*      */   {
/*  334 */     this.mSuppMatrix.postRotate(degrees % 360.0F);
/*  335 */     checkAndDisplayMatrix();
/*      */   }
/*      */ 
/*      */   public ImageView getImageView() {
/*  339 */     ImageView imageView = null;
/*      */ 
/*  341 */     if (null != this.mImageView) {
/*  342 */       imageView = (ImageView)this.mImageView.get();
/*      */     }
/*      */ 
/*  346 */     if (null == imageView) {
/*  347 */       cleanup();
/*  348 */       LogManager.getLogger().i("PhotoViewAttacher", "ImageView no longer exists. You should not use this PhotoViewAttacher any more.");
/*      */     }
/*      */ 
/*  352 */     return imageView;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public float getMinScale() {
/*  358 */     return getMinimumScale();
/*      */   }
/*      */ 
/*      */   public float getMinimumScale()
/*      */   {
/*  363 */     return this.mMinScale;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public float getMidScale() {
/*  369 */     return getMediumScale();
/*      */   }
/*      */ 
/*      */   public float getMediumScale()
/*      */   {
/*  374 */     return this.mMidScale;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public float getMaxScale() {
/*  380 */     return getMaximumScale();
/*      */   }
/*      */ 
/*      */   public float getMaximumScale()
/*      */   {
/*  385 */     return this.mMaxScale;
/*      */   }
/*      */ 
/*      */   public float getScale()
/*      */   {
/*  390 */     return (float)Math.sqrt((float)Math.pow(getValue(this.mSuppMatrix, 0), 2.0D) + (float)Math.pow(getValue(this.mSuppMatrix, 3), 2.0D));
/*      */   }
/*      */ 
/*      */   public ImageView.ScaleType getScaleType()
/*      */   {
/*  395 */     return this.mScaleType;
/*      */   }
/*      */ 
/*      */   public void onDrag(float dx, float dy)
/*      */   {
/*  400 */     if (this.mScaleDragDetector.isScaling()) {
/*  401 */       return;
/*      */     }
/*      */ 
/*  404 */     if (DEBUG) {
/*  405 */       LogManager.getLogger().d("PhotoViewAttacher", String.format("onDrag: dx: %.2f. dy: %.2f", new Object[] { Float.valueOf(dx), Float.valueOf(dy) }));
/*      */     }
/*      */ 
/*  409 */     ImageView imageView = getImageView();
/*  410 */     this.mSuppMatrix.postTranslate(dx, dy);
/*  411 */     checkAndDisplayMatrix();
/*      */ 
/*  422 */     ViewParent parent = imageView.getParent();
/*  423 */     if ((this.mAllowParentInterceptOnEdge) && (!this.mScaleDragDetector.isScaling()) && (!this.mBlockParentIntercept)) {
/*  424 */       if ((this.mScrollEdge == 2) || ((this.mScrollEdge == 0) && (dx >= 1.0F)) || ((this.mScrollEdge == 1) && (dx <= -1.0F)))
/*      */       {
/*  427 */         if (null != parent)
/*  428 */           parent.requestDisallowInterceptTouchEvent(false);
/*      */       }
/*      */     }
/*  431 */     else if (null != parent)
/*  432 */       parent.requestDisallowInterceptTouchEvent(true);
/*      */   }
/*      */ 
/*      */   public void onFling(float startX, float startY, float velocityX, float velocityY)
/*      */   {
/*  440 */     if (DEBUG) {
/*  441 */       LogManager.getLogger().d("PhotoViewAttacher", "onFling. sX: " + startX + " sY: " + startY + " Vx: " + velocityX + " Vy: " + velocityY);
/*      */     }
/*      */ 
/*  446 */     ImageView imageView = getImageView();
/*  447 */     this.mCurrentFlingRunnable = new FlingRunnable(imageView.getContext());
/*  448 */     this.mCurrentFlingRunnable.fling(getImageViewWidth(imageView), getImageViewHeight(imageView), (int)velocityX, (int)velocityY);
/*      */ 
/*  450 */     imageView.post(this.mCurrentFlingRunnable);
/*      */   }
/*      */ 
/*      */   public void onGlobalLayout()
/*      */   {
/*  455 */     ImageView imageView = getImageView();
/*      */ 
/*  457 */     if (null != imageView)
/*  458 */       if (this.mZoomEnabled) {
/*  459 */         int top = imageView.getTop();
/*  460 */         int right = imageView.getRight();
/*  461 */         int bottom = imageView.getBottom();
/*  462 */         int left = imageView.getLeft();
/*      */ 
/*  471 */         if ((top != this.mIvTop) || (bottom != this.mIvBottom) || (left != this.mIvLeft) || (right != this.mIvRight))
/*      */         {
/*  474 */           updateBaseMatrix(imageView.getDrawable());
/*      */ 
/*  477 */           this.mIvTop = top;
/*  478 */           this.mIvRight = right;
/*  479 */           this.mIvBottom = bottom;
/*  480 */           this.mIvLeft = left;
/*      */         }
/*      */       } else {
/*  483 */         updateBaseMatrix(imageView.getDrawable());
/*      */       }
/*      */   }
/*      */ 
/*      */   public void onScale(float scaleFactor, float focusX, float focusY)
/*      */   {
/*  490 */     if (DEBUG) {
/*  491 */       LogManager.getLogger().d("PhotoViewAttacher", String.format("onScale: scale: %.2f. fX: %.2f. fY: %.2f", new Object[] { Float.valueOf(scaleFactor), Float.valueOf(focusX), Float.valueOf(focusY) }));
/*      */     }
/*      */ 
/*  497 */     if (((getScale() < this.mMaxScale) || (scaleFactor < 1.0F)) && ((getScale() > this.mMinScale) || (scaleFactor > 1.0F))) {
/*  498 */       if (null != this.mScaleChangeListener) {
/*  499 */         this.mScaleChangeListener.onScaleChange(scaleFactor, focusX, focusY);
/*      */       }
/*  501 */       this.mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
/*  502 */       checkAndDisplayMatrix();
/*      */     }
/*      */   }
/*      */ 
/*      */   @SuppressLint({"ClickableViewAccessibility"})
/*      */   public boolean onTouch(View v, MotionEvent ev) {
/*  509 */     boolean handled = false;
/*      */ 
/*  511 */     if ((this.mZoomEnabled) && (hasDrawable((ImageView)v))) {
/*  512 */       ViewParent parent = v.getParent();
/*  513 */       switch (ev.getAction())
/*      */       {
/*      */       case 0:
/*  517 */         if (null != parent)
/*  518 */           parent.requestDisallowInterceptTouchEvent(true);
/*      */         else {
/*  520 */           LogManager.getLogger().i("PhotoViewAttacher", "onTouch getParent() returned null");
/*      */         }
/*      */ 
/*  525 */         cancelFling();
/*  526 */         break;
/*      */       case 1:
/*      */       case 3:
/*  532 */         if (getScale() >= this.mMinScale) break;
/*  533 */         RectF rect = getDisplayRect();
/*  534 */         if (null == rect) break;
/*  535 */         v.post(new AnimatedZoomRunnable(getScale(), this.mMinScale, rect.centerX(), rect.centerY()));
/*      */ 
/*  537 */         handled = true;
/*      */       case 2:
/*      */       }
/*      */ 
/*  544 */       if (null != this.mScaleDragDetector) {
/*  545 */         boolean wasScaling = this.mScaleDragDetector.isScaling();
/*  546 */         boolean wasDragging = this.mScaleDragDetector.isDragging();
/*      */ 
/*  548 */         handled = this.mScaleDragDetector.onTouchEvent(ev);
/*      */ 
/*  550 */         boolean didntScale = (!wasScaling) && (!this.mScaleDragDetector.isScaling());
/*  551 */         boolean didntDrag = (!wasDragging) && (!this.mScaleDragDetector.isDragging());
/*      */ 
/*  553 */         this.mBlockParentIntercept = ((didntScale) && (didntDrag));
/*      */       }
/*      */ 
/*  557 */       if ((null != this.mGestureDetector) && (this.mGestureDetector.onTouchEvent(ev))) {
/*  558 */         handled = true;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  563 */     return handled;
/*      */   }
/*      */ 
/*      */   public void setAllowParentInterceptOnEdge(boolean allow)
/*      */   {
/*  568 */     this.mAllowParentInterceptOnEdge = allow;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setMinScale(float minScale) {
/*  574 */     setMinimumScale(minScale);
/*      */   }
/*      */ 
/*      */   public void setMinimumScale(float minimumScale)
/*      */   {
/*  579 */     checkZoomLevels(minimumScale, this.mMidScale, this.mMaxScale);
/*  580 */     this.mMinScale = minimumScale;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setMidScale(float midScale) {
/*  586 */     setMediumScale(midScale);
/*      */   }
/*      */ 
/*      */   public void setMediumScale(float mediumScale)
/*      */   {
/*  591 */     checkZoomLevels(this.mMinScale, mediumScale, this.mMaxScale);
/*  592 */     this.mMidScale = mediumScale;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setMaxScale(float maxScale) {
/*  598 */     setMaximumScale(maxScale);
/*      */   }
/*      */ 
/*      */   public void setMaximumScale(float maximumScale)
/*      */   {
/*  603 */     checkZoomLevels(this.mMinScale, this.mMidScale, maximumScale);
/*  604 */     this.mMaxScale = maximumScale;
/*      */   }
/*      */ 
/*      */   public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale)
/*      */   {
/*  609 */     checkZoomLevels(minimumScale, mediumScale, maximumScale);
/*  610 */     this.mMinScale = minimumScale;
/*  611 */     this.mMidScale = mediumScale;
/*  612 */     this.mMaxScale = maximumScale;
/*      */   }
/*      */ 
/*      */   public void setOnLongClickListener(View.OnLongClickListener listener)
/*      */   {
/*  617 */     this.mLongClickListener = listener;
/*      */   }
/*      */ 
/*      */   public void setOnMatrixChangeListener(OnMatrixChangedListener listener)
/*      */   {
/*  622 */     this.mMatrixChangeListener = listener;
/*      */   }
/*      */ 
/*      */   public void setOnPhotoTapListener(OnPhotoTapListener listener)
/*      */   {
/*  627 */     this.mPhotoTapListener = listener;
/*      */   }
/*      */ 
/*      */   public OnPhotoTapListener getOnPhotoTapListener()
/*      */   {
/*  632 */     return this.mPhotoTapListener;
/*      */   }
/*      */ 
/*      */   public void setOnViewTapListener(OnViewTapListener listener)
/*      */   {
/*  637 */     this.mViewTapListener = listener;
/*      */   }
/*      */ 
/*      */   public OnViewTapListener getOnViewTapListener()
/*      */   {
/*  642 */     return this.mViewTapListener;
/*      */   }
/*      */ 
/*      */   public void setScale(float scale)
/*      */   {
/*  647 */     setScale(scale, false);
/*      */   }
/*      */ 
/*      */   public void setScale(float scale, boolean animate)
/*      */   {
/*  652 */     ImageView imageView = getImageView();
/*      */ 
/*  654 */     if (null != imageView)
/*  655 */       setScale(scale, imageView.getRight() / 2, imageView.getBottom() / 2, animate);
/*      */   }
/*      */ 
/*      */   public void setScale(float scale, float focalX, float focalY, boolean animate)
/*      */   {
/*  665 */     ImageView imageView = getImageView();
/*      */ 
/*  667 */     if (null != imageView)
/*      */     {
/*  669 */       if ((scale < this.mMinScale) || (scale > this.mMaxScale)) {
/*  670 */         LogManager.getLogger().i("PhotoViewAttacher", "Scale must be within the range of minScale and maxScale");
/*      */ 
/*  674 */         return;
/*      */       }
/*      */ 
/*  677 */       if (animate) {
/*  678 */         imageView.post(new AnimatedZoomRunnable(getScale(), scale, focalX, focalY));
/*      */       }
/*      */       else {
/*  681 */         this.mSuppMatrix.setScale(scale, scale, focalX, focalY);
/*  682 */         checkAndDisplayMatrix();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setScaleType(ImageView.ScaleType scaleType)
/*      */   {
/*  689 */     if ((isSupportedScaleType(scaleType)) && (scaleType != this.mScaleType)) {
/*  690 */       this.mScaleType = scaleType;
/*      */ 
/*  693 */       update();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setZoomable(boolean zoomable)
/*      */   {
/*  699 */     this.mZoomEnabled = zoomable;
/*  700 */     update();
/*      */   }
/*      */ 
/*      */   public void update() {
/*  704 */     ImageView imageView = getImageView();
/*      */ 
/*  706 */     if (null != imageView)
/*  707 */       if (this.mZoomEnabled)
/*      */       {
/*  709 */         setImageViewScaleTypeMatrix(imageView);
/*      */ 
/*  712 */         updateBaseMatrix(imageView.getDrawable());
/*      */       }
/*      */       else {
/*  715 */         resetMatrix();
/*      */       }
/*      */   }
/*      */ 
/*      */   public Matrix getDisplayMatrix()
/*      */   {
/*  722 */     return new Matrix(getDrawMatrix());
/*      */   }
/*      */ 
/*      */   public void getDisplayMatrix(Matrix matrix)
/*      */   {
/*  727 */     matrix.set(getDrawMatrix());
/*      */   }
/*      */ 
/*      */   public Matrix getDrawMatrix() {
/*  731 */     this.mDrawMatrix.set(this.mBaseMatrix);
/*  732 */     this.mDrawMatrix.postConcat(this.mSuppMatrix);
/*  733 */     return this.mDrawMatrix;
/*      */   }
/*      */ 
/*      */   private void cancelFling() {
/*  737 */     if (null != this.mCurrentFlingRunnable) {
/*  738 */       this.mCurrentFlingRunnable.cancelFling();
/*  739 */       this.mCurrentFlingRunnable = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkAndDisplayMatrix()
/*      */   {
/*  747 */     if (checkMatrixBounds())
/*  748 */       setImageViewMatrix(getDrawMatrix());
/*      */   }
/*      */ 
/*      */   private void checkImageViewScaleType()
/*      */   {
/*  753 */     ImageView imageView = getImageView();
/*      */ 
/*  759 */     if ((null != imageView) && (!(imageView instanceof ViewTreeObserver.OnGlobalLayoutListener)) && 
/*  760 */       (!ImageView.ScaleType.MATRIX.equals(imageView.getScaleType())))
/*  761 */       throw new IllegalStateException("The ImageView's ScaleType has been changed since attaching a PhotoViewAttacher");
/*      */   }
/*      */ 
/*      */   private boolean checkMatrixBounds()
/*      */   {
/*  768 */     ImageView imageView = getImageView();
/*  769 */     if (null == imageView) {
/*  770 */       return false;
/*      */     }
/*      */ 
/*  773 */     RectF rect = getDisplayRect(getDrawMatrix());
/*  774 */     if (null == rect) {
/*  775 */       return false;
/*      */     }
/*      */ 
/*  778 */     float height = rect.height(); float width = rect.width();
/*  779 */     float deltaX = 0.0F; float deltaY = 0.0F;
/*      */ 
/*  781 */     int viewHeight = getImageViewHeight(imageView);
/*  782 */     if (height <= viewHeight)
/*  783 */       switch (2.$SwitchMap$android$widget$ImageView$ScaleType[this.mScaleType.ordinal()]) {
/*      */       case 2:
/*  785 */         deltaY = -rect.top;
/*  786 */         break;
/*      */       case 3:
/*  788 */         deltaY = viewHeight - height - rect.top;
/*  789 */         break;
/*      */       default:
/*  791 */         deltaY = (viewHeight - height) / 2.0F - rect.top;
/*  792 */         break;
/*      */       }
/*  794 */     else if (rect.top > 0.0F)
/*  795 */       deltaY = -rect.top;
/*  796 */     else if (rect.bottom < viewHeight) {
/*  797 */       deltaY = viewHeight - rect.bottom;
/*      */     }
/*      */ 
/*  800 */     int viewWidth = getImageViewWidth(imageView);
/*  801 */     if (width <= viewWidth) {
/*  802 */       switch (2.$SwitchMap$android$widget$ImageView$ScaleType[this.mScaleType.ordinal()]) {
/*      */       case 2:
/*  804 */         deltaX = -rect.left;
/*  805 */         break;
/*      */       case 3:
/*  807 */         deltaX = viewWidth - width - rect.left;
/*  808 */         break;
/*      */       default:
/*  810 */         deltaX = (viewWidth - width) / 2.0F - rect.left;
/*      */       }
/*      */ 
/*  813 */       this.mScrollEdge = 2;
/*  814 */     } else if (rect.left > 0.0F) {
/*  815 */       this.mScrollEdge = 0;
/*  816 */       deltaX = -rect.left;
/*  817 */     } else if (rect.right < viewWidth) {
/*  818 */       deltaX = viewWidth - rect.right;
/*  819 */       this.mScrollEdge = 1;
/*      */     } else {
/*  821 */       this.mScrollEdge = -1;
/*      */     }
/*      */ 
/*  825 */     this.mSuppMatrix.postTranslate(deltaX, deltaY);
/*  826 */     return true;
/*      */   }
/*      */ 
/*      */   private RectF getDisplayRect(Matrix matrix)
/*      */   {
/*  836 */     ImageView imageView = getImageView();
/*      */ 
/*  838 */     if (null != imageView) {
/*  839 */       Drawable d = imageView.getDrawable();
/*  840 */       if (null != d) {
/*  841 */         this.mDisplayRect.set(0.0F, 0.0F, d.getIntrinsicWidth(), d.getIntrinsicHeight());
/*      */ 
/*  843 */         matrix.mapRect(this.mDisplayRect);
/*  844 */         return this.mDisplayRect;
/*      */       }
/*      */     }
/*  847 */     return null;
/*      */   }
/*      */ 
/*      */   public Bitmap getVisibleRectangleBitmap() {
/*  851 */     ImageView imageView = getImageView();
/*  852 */     return imageView == null ? null : imageView.getDrawingCache();
/*      */   }
/*      */ 
/*      */   public void setZoomTransitionDuration(int milliseconds)
/*      */   {
/*  857 */     if (milliseconds < 0)
/*  858 */       milliseconds = 200;
/*  859 */     this.ZOOM_DURATION = milliseconds;
/*      */   }
/*      */ 
/*      */   public IPhotoView getIPhotoViewImplementation()
/*      */   {
/*  864 */     return this;
/*      */   }
/*      */ 
/*      */   private float getValue(Matrix matrix, int whichValue)
/*      */   {
/*  875 */     matrix.getValues(this.mMatrixValues);
/*  876 */     return this.mMatrixValues[whichValue];
/*      */   }
/*      */ 
/*      */   private void resetMatrix()
/*      */   {
/*  883 */     this.mSuppMatrix.reset();
/*  884 */     setRotationBy(this.mBaseRotation);
/*  885 */     setImageViewMatrix(getDrawMatrix());
/*  886 */     checkMatrixBounds();
/*      */   }
/*      */ 
/*      */   private void setImageViewMatrix(Matrix matrix) {
/*  890 */     ImageView imageView = getImageView();
/*  891 */     if (null != imageView)
/*      */     {
/*  893 */       checkImageViewScaleType();
/*  894 */       imageView.setImageMatrix(matrix);
/*      */ 
/*  897 */       if (null != this.mMatrixChangeListener) {
/*  898 */         RectF displayRect = getDisplayRect(matrix);
/*  899 */         if (null != displayRect)
/*  900 */           this.mMatrixChangeListener.onMatrixChanged(displayRect);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void updateBaseMatrix(Drawable d)
/*      */   {
/*  912 */     ImageView imageView = getImageView();
/*  913 */     if ((null == imageView) || (null == d)) {
/*  914 */       return;
/*      */     }
/*      */ 
/*  917 */     float viewWidth = getImageViewWidth(imageView);
/*  918 */     float viewHeight = getImageViewHeight(imageView);
/*  919 */     int drawableWidth = d.getIntrinsicWidth();
/*  920 */     int drawableHeight = d.getIntrinsicHeight();
/*      */ 
/*  922 */     this.mBaseMatrix.reset();
/*      */ 
/*  924 */     float widthScale = viewWidth / drawableWidth;
/*  925 */     float heightScale = viewHeight / drawableHeight;
/*      */ 
/*  927 */     if (this.mScaleType == ImageView.ScaleType.CENTER) {
/*  928 */       this.mBaseMatrix.postTranslate((viewWidth - drawableWidth) / 2.0F, (viewHeight - drawableHeight) / 2.0F);
/*      */     }
/*  931 */     else if (this.mScaleType == ImageView.ScaleType.CENTER_CROP) {
/*  932 */       float scale = Math.max(widthScale, heightScale);
/*  933 */       this.mBaseMatrix.postScale(scale, scale);
/*  934 */       this.mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2.0F, (viewHeight - drawableHeight * scale) / 2.0F);
/*      */     }
/*  937 */     else if (this.mScaleType == ImageView.ScaleType.CENTER_INSIDE) {
/*  938 */       float scale = Math.min(1.0F, Math.min(widthScale, heightScale));
/*  939 */       this.mBaseMatrix.postScale(scale, scale);
/*  940 */       this.mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2.0F, (viewHeight - drawableHeight * scale) / 2.0F);
/*      */     }
/*      */     else
/*      */     {
/*  944 */       RectF mTempSrc = new RectF(0.0F, 0.0F, drawableWidth, drawableHeight);
/*  945 */       RectF mTempDst = new RectF(0.0F, 0.0F, viewWidth, viewHeight);
/*      */ 
/*  947 */       if ((int)this.mBaseRotation % 180 != 0) {
/*  948 */         mTempSrc = new RectF(0.0F, 0.0F, drawableHeight, drawableWidth);
/*      */       }
/*      */ 
/*  951 */       switch (2.$SwitchMap$android$widget$ImageView$ScaleType[this.mScaleType.ordinal()]) {
/*      */       case 4:
/*  953 */         this.mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.CENTER);
/*      */ 
/*  955 */         break;
/*      */       case 2:
/*  958 */         this.mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.START);
/*  959 */         break;
/*      */       case 3:
/*  962 */         this.mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.END);
/*  963 */         break;
/*      */       case 5:
/*  966 */         this.mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.FILL);
/*  967 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  974 */     resetMatrix();
/*      */   }
/*      */ 
/*      */   private int getImageViewWidth(ImageView imageView) {
/*  978 */     if (null == imageView)
/*  979 */       return 0;
/*  980 */     return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
/*      */   }
/*      */ 
/*      */   private int getImageViewHeight(ImageView imageView) {
/*  984 */     if (null == imageView)
/*  985 */       return 0;
/*  986 */     return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
/*      */   }
/*      */ 
/*      */   private class FlingRunnable
/*      */     implements Runnable
/*      */   {
/*      */     private final ScrollerProxy mScroller;
/*      */     private int mCurrentX;
/*      */     private int mCurrentY;
/*      */ 
/*      */     public FlingRunnable(Context context)
/*      */     {
/* 1134 */       this.mScroller = ScrollerProxy.getScroller(context);
/*      */     }
/*      */ 
/*      */     public void cancelFling() {
/* 1138 */       if (PhotoViewAttacher.DEBUG) {
/* 1139 */         LogManager.getLogger().d("PhotoViewAttacher", "Cancel Fling");
/*      */       }
/* 1141 */       this.mScroller.forceFinished(true);
/*      */     }
/*      */ 
/*      */     public void fling(int viewWidth, int viewHeight, int velocityX, int velocityY)
/*      */     {
/* 1146 */       RectF rect = PhotoViewAttacher.this.getDisplayRect();
/* 1147 */       if (null == rect) {
/* 1148 */         return;
/*      */       }
/*      */ 
/* 1151 */       int startX = Math.round(-rect.left);
/*      */       int maxX;
/*      */       int maxX;
/*      */       int minX;
/* 1154 */       if (viewWidth < rect.width()) {
/* 1155 */         int minX = 0;
/* 1156 */         maxX = Math.round(rect.width() - viewWidth);
/*      */       } else {
/* 1158 */         minX = maxX = startX;
/*      */       }
/*      */ 
/* 1161 */       int startY = Math.round(-rect.top);
/*      */       int maxY;
/*      */       int maxY;
/*      */       int minY;
/* 1162 */       if (viewHeight < rect.height()) {
/* 1163 */         int minY = 0;
/* 1164 */         maxY = Math.round(rect.height() - viewHeight);
/*      */       } else {
/* 1166 */         minY = maxY = startY;
/*      */       }
/*      */ 
/* 1169 */       this.mCurrentX = startX;
/* 1170 */       this.mCurrentY = startY;
/*      */ 
/* 1172 */       if (PhotoViewAttacher.DEBUG) {
/* 1173 */         LogManager.getLogger().d("PhotoViewAttacher", "fling. StartX:" + startX + " StartY:" + startY + " MaxX:" + maxX + " MaxY:" + maxY);
/*      */       }
/*      */ 
/* 1180 */       if ((startX != maxX) || (startY != maxY))
/* 1181 */         this.mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0);
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/* 1188 */       if (this.mScroller.isFinished()) {
/* 1189 */         return;
/*      */       }
/*      */ 
/* 1192 */       ImageView imageView = PhotoViewAttacher.this.getImageView();
/* 1193 */       if ((null != imageView) && (this.mScroller.computeScrollOffset()))
/*      */       {
/* 1195 */         int newX = this.mScroller.getCurrX();
/* 1196 */         int newY = this.mScroller.getCurrY();
/*      */ 
/* 1198 */         if (PhotoViewAttacher.DEBUG) {
/* 1199 */           LogManager.getLogger().d("PhotoViewAttacher", "fling run(). CurrentX:" + this.mCurrentX + " CurrentY:" + this.mCurrentY + " NewX:" + newX + " NewY:" + newY);
/*      */         }
/*      */ 
/* 1206 */         PhotoViewAttacher.this.mSuppMatrix.postTranslate(this.mCurrentX - newX, this.mCurrentY - newY);
/* 1207 */         PhotoViewAttacher.this.setImageViewMatrix(PhotoViewAttacher.this.getDrawMatrix());
/*      */ 
/* 1209 */         this.mCurrentX = newX;
/* 1210 */         this.mCurrentY = newY;
/*      */ 
/* 1213 */         Compat.postOnAnimation(imageView, this);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class AnimatedZoomRunnable
/*      */     implements Runnable
/*      */   {
/*      */     private final float mFocalX;
/*      */     private final float mFocalY;
/*      */     private final long mStartTime;
/*      */     private final float mZoomStart;
/*      */     private final float mZoomEnd;
/*      */ 
/*      */     public AnimatedZoomRunnable(float currentZoom, float targetZoom, float focalX, float focalY)
/*      */     {
/* 1094 */       this.mFocalX = focalX;
/* 1095 */       this.mFocalY = focalY;
/* 1096 */       this.mStartTime = System.currentTimeMillis();
/* 1097 */       this.mZoomStart = currentZoom;
/* 1098 */       this.mZoomEnd = targetZoom;
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/* 1103 */       ImageView imageView = PhotoViewAttacher.this.getImageView();
/* 1104 */       if (imageView == null) {
/* 1105 */         return;
/*      */       }
/*      */ 
/* 1108 */       float t = interpolate();
/* 1109 */       float scale = this.mZoomStart + t * (this.mZoomEnd - this.mZoomStart);
/* 1110 */       float deltaScale = scale / PhotoViewAttacher.this.getScale();
/*      */ 
/* 1112 */       PhotoViewAttacher.this.onScale(deltaScale, this.mFocalX, this.mFocalY);
/*      */ 
/* 1115 */       if (t < 1.0F)
/* 1116 */         Compat.postOnAnimation(imageView, this);
/*      */     }
/*      */ 
/*      */     private float interpolate()
/*      */     {
/* 1121 */       float t = 1.0F * (float)(System.currentTimeMillis() - this.mStartTime) / PhotoViewAttacher.this.ZOOM_DURATION;
/* 1122 */       t = Math.min(1.0F, t);
/* 1123 */       t = PhotoViewAttacher.sInterpolator.getInterpolation(t);
/* 1124 */       return t;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface OnSingleFlingListener
/*      */   {
/*      */     public abstract boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2);
/*      */   }
/*      */ 
/*      */   public static abstract interface OnViewTapListener
/*      */   {
/*      */     public abstract void onViewTap(View paramView, float paramFloat1, float paramFloat2);
/*      */   }
/*      */ 
/*      */   public static abstract interface OnPhotoTapListener
/*      */   {
/*      */     public abstract void onPhotoTap(View paramView, float paramFloat1, float paramFloat2);
/*      */ 
/*      */     public abstract void onOutsidePhotoTap();
/*      */   }
/*      */ 
/*      */   public static abstract interface OnScaleChangeListener
/*      */   {
/*      */     public abstract void onScaleChange(float paramFloat1, float paramFloat2, float paramFloat3);
/*      */   }
/*      */ 
/*      */   public static abstract interface OnMatrixChangedListener
/*      */   {
/*      */     public abstract void onMatrixChanged(RectF paramRectF);
/*      */   }
/*      */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.photoview.PhotoViewAttacher
 * JD-Core Version:    0.6.0
 */
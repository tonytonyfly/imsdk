/*     */ package io.rong.photoview.gestures;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.view.MotionEvent;
/*     */ import android.view.VelocityTracker;
/*     */ import android.view.ViewConfiguration;
/*     */ import io.rong.photoview.log.LogManager;
/*     */ import io.rong.photoview.log.Logger;
/*     */ 
/*     */ public class CupcakeGestureDetector
/*     */   implements GestureDetector
/*     */ {
/*     */   protected OnGestureListener mListener;
/*     */   private static final String LOG_TAG = "CupcakeGestureDetector";
/*     */   float mLastTouchX;
/*     */   float mLastTouchY;
/*     */   final float mTouchSlop;
/*     */   final float mMinimumVelocity;
/*     */   private VelocityTracker mVelocityTracker;
/*     */   private boolean mIsDragging;
/*     */ 
/*     */   public void setOnGestureListener(OnGestureListener listener)
/*     */   {
/*  38 */     this.mListener = listener;
/*     */   }
/*     */ 
/*     */   public CupcakeGestureDetector(Context context) {
/*  42 */     ViewConfiguration configuration = ViewConfiguration.get(context);
/*     */ 
/*  44 */     this.mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
/*  45 */     this.mTouchSlop = configuration.getScaledTouchSlop();
/*     */   }
/*     */ 
/*     */   float getActiveX(MotionEvent ev)
/*     */   {
/*  52 */     return ev.getX();
/*     */   }
/*     */ 
/*     */   float getActiveY(MotionEvent ev) {
/*  56 */     return ev.getY();
/*     */   }
/*     */ 
/*     */   public boolean isScaling() {
/*  60 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isDragging() {
/*  64 */     return this.mIsDragging;
/*     */   }
/*     */ 
/*     */   public boolean onTouchEvent(MotionEvent ev)
/*     */   {
/*  69 */     switch (ev.getAction()) {
/*     */     case 0:
/*  71 */       this.mVelocityTracker = VelocityTracker.obtain();
/*  72 */       if (null != this.mVelocityTracker)
/*  73 */         this.mVelocityTracker.addMovement(ev);
/*     */       else {
/*  75 */         LogManager.getLogger().i("CupcakeGestureDetector", "Velocity tracker is null");
/*     */       }
/*     */ 
/*  78 */       this.mLastTouchX = getActiveX(ev);
/*  79 */       this.mLastTouchY = getActiveY(ev);
/*  80 */       this.mIsDragging = false;
/*  81 */       break;
/*     */     case 2:
/*  85 */       float x = getActiveX(ev);
/*  86 */       float y = getActiveY(ev);
/*  87 */       float dx = x - this.mLastTouchX; float dy = y - this.mLastTouchY;
/*     */ 
/*  89 */       if (!this.mIsDragging)
/*     */       {
/*  92 */         this.mIsDragging = (Math.sqrt(dx * dx + dy * dy) >= this.mTouchSlop);
/*     */       }
/*     */ 
/*  95 */       if (!this.mIsDragging) break;
/*  96 */       this.mListener.onDrag(dx, dy);
/*  97 */       this.mLastTouchX = x;
/*  98 */       this.mLastTouchY = y;
/*     */ 
/* 100 */       if (null == this.mVelocityTracker) break;
/* 101 */       this.mVelocityTracker.addMovement(ev); break;
/*     */     case 3:
/* 109 */       if (null == this.mVelocityTracker) break;
/* 110 */       this.mVelocityTracker.recycle();
/* 111 */       this.mVelocityTracker = null; break;
/*     */     case 1:
/* 117 */       if ((this.mIsDragging) && 
/* 118 */         (null != this.mVelocityTracker)) {
/* 119 */         this.mLastTouchX = getActiveX(ev);
/* 120 */         this.mLastTouchY = getActiveY(ev);
/*     */ 
/* 123 */         this.mVelocityTracker.addMovement(ev);
/* 124 */         this.mVelocityTracker.computeCurrentVelocity(1000);
/*     */ 
/* 126 */         float vX = this.mVelocityTracker.getXVelocity(); float vY = this.mVelocityTracker.getYVelocity();
/*     */ 
/* 131 */         if (Math.max(Math.abs(vX), Math.abs(vY)) >= this.mMinimumVelocity) {
/* 132 */           this.mListener.onFling(this.mLastTouchX, this.mLastTouchY, -vX, -vY);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 139 */       if (null == this.mVelocityTracker) break;
/* 140 */       this.mVelocityTracker.recycle();
/* 141 */       this.mVelocityTracker = null;
/*     */     }
/*     */ 
/* 147 */     return true;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.photoview.gestures.CupcakeGestureDetector
 * JD-Core Version:    0.6.0
 */
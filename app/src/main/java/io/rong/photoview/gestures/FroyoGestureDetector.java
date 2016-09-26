/*    */ package io.rong.photoview.gestures;
/*    */ 
/*    */ import android.annotation.TargetApi;
/*    */ import android.content.Context;
/*    */ import android.view.MotionEvent;
/*    */ import android.view.ScaleGestureDetector;
/*    */ import android.view.ScaleGestureDetector.OnScaleGestureListener;
/*    */ 
/*    */ @TargetApi(8)
/*    */ public class FroyoGestureDetector extends EclairGestureDetector
/*    */ {
/*    */   protected final ScaleGestureDetector mDetector;
/*    */ 
/*    */   public FroyoGestureDetector(Context context)
/*    */   {
/* 31 */     super(context);
/* 32 */     ScaleGestureDetector.OnScaleGestureListener mScaleListener = new ScaleGestureDetector.OnScaleGestureListener()
/*    */     {
/*    */       public boolean onScale(ScaleGestureDetector detector)
/*    */       {
/* 36 */         float scaleFactor = detector.getScaleFactor();
/*    */ 
/* 38 */         if ((Float.isNaN(scaleFactor)) || (Float.isInfinite(scaleFactor))) {
/* 39 */           return false;
/*    */         }
/* 41 */         FroyoGestureDetector.this.mListener.onScale(scaleFactor, detector.getFocusX(), detector.getFocusY());
/*    */ 
/* 43 */         return true;
/*    */       }
/*    */ 
/*    */       public boolean onScaleBegin(ScaleGestureDetector detector)
/*    */       {
/* 48 */         return true;
/*    */       }
/*    */ 
/*    */       public void onScaleEnd(ScaleGestureDetector detector)
/*    */       {
/*    */       }
/*    */     };
/* 56 */     this.mDetector = new ScaleGestureDetector(context, mScaleListener);
/*    */   }
/*    */ 
/*    */   public boolean isScaling()
/*    */   {
/* 61 */     return this.mDetector.isInProgress();
/*    */   }
/*    */ 
/*    */   public boolean onTouchEvent(MotionEvent ev)
/*    */   {
/*    */     try {
/* 67 */       this.mDetector.onTouchEvent(ev);
/* 68 */       return super.onTouchEvent(ev);
/*    */     } catch (IllegalArgumentException e) {
/*    */     }
/* 71 */     return true;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.photoview.gestures.FroyoGestureDetector
 * JD-Core Version:    0.6.0
 */
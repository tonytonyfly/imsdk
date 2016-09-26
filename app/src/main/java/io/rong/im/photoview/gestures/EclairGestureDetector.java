/*    */ package io.rong.photoview.gestures;
/*    */ 
/*    */ import android.annotation.TargetApi;
/*    */ import android.content.Context;
/*    */ import android.view.MotionEvent;
/*    */ import io.rong.photoview.Compat;
/*    */ 
/*    */ @TargetApi(5)
/*    */ public class EclairGestureDetector extends CupcakeGestureDetector
/*    */ {
/*    */   private static final int INVALID_POINTER_ID = -1;
/* 29 */   private int mActivePointerId = -1;
/* 30 */   private int mActivePointerIndex = 0;
/*    */ 
/*    */   public EclairGestureDetector(Context context) {
/* 33 */     super(context);
/*    */   }
/*    */ 
/*    */   float getActiveX(MotionEvent ev)
/*    */   {
/*    */     try {
/* 39 */       return ev.getX(this.mActivePointerIndex); } catch (Exception e) {
/*    */     }
/* 41 */     return ev.getX();
/*    */   }
/*    */ 
/*    */   float getActiveY(MotionEvent ev)
/*    */   {
/*    */     try
/*    */     {
/* 48 */       return ev.getY(this.mActivePointerIndex); } catch (Exception e) {
/*    */     }
/* 50 */     return ev.getY();
/*    */   }
/*    */ 
/*    */   public boolean onTouchEvent(MotionEvent ev)
/*    */   {
/* 56 */     int action = ev.getAction();
/* 57 */     switch (action & 0xFF) {
/*    */     case 0:
/* 59 */       this.mActivePointerId = ev.getPointerId(0);
/* 60 */       break;
/*    */     case 1:
/*    */     case 3:
/* 63 */       this.mActivePointerId = -1;
/* 64 */       break;
/*    */     case 6:
/* 69 */       int pointerIndex = Compat.getPointerIndex(ev.getAction());
/* 70 */       int pointerId = ev.getPointerId(pointerIndex);
/* 71 */       if (pointerId != this.mActivePointerId) {
/*    */         break;
/*    */       }
/* 74 */       int newPointerIndex = pointerIndex == 0 ? 1 : 0;
/* 75 */       this.mActivePointerId = ev.getPointerId(newPointerIndex);
/* 76 */       this.mLastTouchX = ev.getX(newPointerIndex);
/* 77 */       this.mLastTouchY = ev.getY(newPointerIndex);
/*    */     case 2:
/*    */     case 4:
/*    */     case 5:
/*    */     }
/* 82 */     this.mActivePointerIndex = ev.findPointerIndex(this.mActivePointerId != -1 ? this.mActivePointerId : 0);
/*    */     try
/*    */     {
/* 86 */       return super.onTouchEvent(ev);
/*    */     } catch (IllegalArgumentException e) {
/*    */     }
/* 89 */     return true;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.photoview.gestures.EclairGestureDetector
 * JD-Core Version:    0.6.0
 */
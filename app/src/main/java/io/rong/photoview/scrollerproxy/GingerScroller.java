/*    */ package io.rong.photoview.scrollerproxy;
/*    */ 
/*    */ import android.annotation.TargetApi;
/*    */ import android.content.Context;
/*    */ import android.widget.OverScroller;
/*    */ 
/*    */ @TargetApi(9)
/*    */ public class GingerScroller extends ScrollerProxy
/*    */ {
/*    */   protected final OverScroller mScroller;
/* 28 */   private boolean mFirstScroll = false;
/*    */ 
/*    */   public GingerScroller(Context context) {
/* 31 */     this.mScroller = new OverScroller(context);
/*    */   }
/*    */ 
/*    */   public boolean computeScrollOffset()
/*    */   {
/* 38 */     if (this.mFirstScroll) {
/* 39 */       this.mScroller.computeScrollOffset();
/* 40 */       this.mFirstScroll = false;
/*    */     }
/* 42 */     return this.mScroller.computeScrollOffset();
/*    */   }
/*    */ 
/*    */   public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY, int overX, int overY)
/*    */   {
/* 48 */     this.mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, overX, overY);
/*    */   }
/*    */ 
/*    */   public void forceFinished(boolean finished)
/*    */   {
/* 53 */     this.mScroller.forceFinished(finished);
/*    */   }
/*    */ 
/*    */   public boolean isFinished()
/*    */   {
/* 58 */     return this.mScroller.isFinished();
/*    */   }
/*    */ 
/*    */   public int getCurrX()
/*    */   {
/* 63 */     return this.mScroller.getCurrX();
/*    */   }
/*    */ 
/*    */   public int getCurrY()
/*    */   {
/* 68 */     return this.mScroller.getCurrY();
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.photoview.scrollerproxy.GingerScroller
 * JD-Core Version:    0.6.0
 */
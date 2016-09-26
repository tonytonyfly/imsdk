/*    */ package io.rong.photoview.scrollerproxy;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.widget.Scroller;
/*    */ 
/*    */ public class PreGingerScroller extends ScrollerProxy
/*    */ {
/*    */   private final Scroller mScroller;
/*    */ 
/*    */   public PreGingerScroller(Context context)
/*    */   {
/* 28 */     this.mScroller = new Scroller(context);
/*    */   }
/*    */ 
/*    */   public boolean computeScrollOffset()
/*    */   {
/* 33 */     return this.mScroller.computeScrollOffset();
/*    */   }
/*    */ 
/*    */   public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY, int overX, int overY)
/*    */   {
/* 39 */     this.mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
/*    */   }
/*    */ 
/*    */   public void forceFinished(boolean finished)
/*    */   {
/* 44 */     this.mScroller.forceFinished(finished);
/*    */   }
/*    */ 
/*    */   public boolean isFinished() {
/* 48 */     return this.mScroller.isFinished();
/*    */   }
/*    */ 
/*    */   public int getCurrX()
/*    */   {
/* 53 */     return this.mScroller.getCurrX();
/*    */   }
/*    */ 
/*    */   public int getCurrY()
/*    */   {
/* 58 */     return this.mScroller.getCurrY();
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.photoview.scrollerproxy.PreGingerScroller
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.photoview.scrollerproxy;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.os.Build.VERSION;
/*    */ 
/*    */ public abstract class ScrollerProxy
/*    */ {
/*    */   public static ScrollerProxy getScroller(Context context)
/*    */   {
/* 27 */     if (Build.VERSION.SDK_INT < 9)
/* 28 */       return new PreGingerScroller(context);
/* 29 */     if (Build.VERSION.SDK_INT < 14) {
/* 30 */       return new GingerScroller(context);
/*    */     }
/* 32 */     return new IcsScroller(context);
/*    */   }
/*    */ 
/*    */   public abstract boolean computeScrollOffset();
/*    */ 
/*    */   public abstract void fling(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10);
/*    */ 
/*    */   public abstract void forceFinished(boolean paramBoolean);
/*    */ 
/*    */   public abstract boolean isFinished();
/*    */ 
/*    */   public abstract int getCurrX();
/*    */ 
/*    */   public abstract int getCurrY();
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.photoview.scrollerproxy.ScrollerProxy
 * JD-Core Version:    0.6.0
 */
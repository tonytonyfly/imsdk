/*    */ package io.rong.photoview.scrollerproxy;
/*    */ 
/*    */ import android.annotation.TargetApi;
/*    */ import android.content.Context;
/*    */ import android.widget.OverScroller;
/*    */ 
/*    */ @TargetApi(14)
/*    */ public class IcsScroller extends GingerScroller
/*    */ {
/*    */   public IcsScroller(Context context)
/*    */   {
/* 27 */     super(context);
/*    */   }
/*    */ 
/*    */   public boolean computeScrollOffset()
/*    */   {
/* 32 */     return this.mScroller.computeScrollOffset();
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.photoview.scrollerproxy.IcsScroller
 * JD-Core Version:    0.6.0
 */
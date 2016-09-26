/*    */ package io.rong.imkit.widget;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.support.v4.view.ViewPager;
/*    */ import android.util.AttributeSet;
/*    */ import android.view.MotionEvent;
/*    */ 
/*    */ public class HackyViewPager extends ViewPager
/*    */ {
/*    */   public HackyViewPager(Context context, AttributeSet attrs)
/*    */   {
/* 11 */     super(context, attrs);
/*    */   }
/*    */ 
/*    */   public boolean onInterceptTouchEvent(MotionEvent ev)
/*    */   {
/*    */     try {
/* 17 */       return super.onInterceptTouchEvent(ev);
/*    */     } catch (IllegalArgumentException e) {
/* 19 */       e.printStackTrace();
/* 20 */     }return false;
/*    */   }
/*    */ 
/*    */   public boolean onTouchEvent(MotionEvent event)
/*    */   {
/* 26 */     return super.onTouchEvent(event);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.HackyViewPager
 * JD-Core Version:    0.6.0
 */
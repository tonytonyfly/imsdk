/*    */ package io.rong.imkit.model;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.util.AttributeSet;
/*    */ import android.view.MotionEvent;
/*    */ import android.view.View.MeasureSpec;
/*    */ import android.widget.GridView;
/*    */ 
/*    */ public class RongGridView extends GridView
/*    */ {
/*    */   public RongGridView(Context context)
/*    */   {
/* 13 */     super(context);
/*    */   }
/*    */ 
/*    */   public RongGridView(Context context, AttributeSet attrs) {
/* 17 */     super(context, attrs);
/*    */   }
/*    */ 
/*    */   public RongGridView(Context context, AttributeSet attrs, int defStyle) {
/* 21 */     super(context, attrs, defStyle);
/*    */   }
/*    */ 
/*    */   public boolean dispatchTouchEvent(MotionEvent ev)
/*    */   {
/* 27 */     return super.dispatchTouchEvent(ev);
/*    */   }
/*    */ 
/*    */   public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
/*    */   {
/* 32 */     int expandSpec = View.MeasureSpec.makeMeasureSpec(536870911, -2147483648);
/* 33 */     super.onMeasure(widthMeasureSpec, expandSpec);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.model.RongGridView
 * JD-Core Version:    0.6.0
 */
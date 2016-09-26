/*    */ package io.rong.imkit.widget;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.util.AttributeSet;
/*    */ import android.view.View.MeasureSpec;
/*    */ import android.widget.RelativeLayout;
/*    */ 
/*    */ public class SquareLayout extends RelativeLayout
/*    */ {
/*    */   public SquareLayout(Context context, AttributeSet attrs)
/*    */   {
/* 10 */     super(context, attrs);
/*    */   }
/*    */ 
/*    */   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
/*    */   {
/* 15 */     setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
/* 16 */     heightMeasureSpec = widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
/* 17 */     super.onMeasure(widthMeasureSpec, heightMeasureSpec);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.SquareLayout
 * JD-Core Version:    0.6.0
 */
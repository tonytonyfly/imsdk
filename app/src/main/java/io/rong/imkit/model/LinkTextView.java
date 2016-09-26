/*    */ package io.rong.imkit.model;
/*    */ 
/*    */ import android.annotation.SuppressLint;
/*    */ import android.content.Context;
/*    */ import android.util.AttributeSet;
/*    */ import android.view.MotionEvent;
/*    */ import android.widget.TextView;
/*    */ 
/*    */ public class LinkTextView extends TextView
/*    */ {
/*    */   public LinkTextView(Context context)
/*    */   {
/* 12 */     super(context);
/*    */   }
/*    */ 
/*    */   public LinkTextView(Context context, AttributeSet attrs) {
/* 16 */     super(context, attrs);
/*    */   }
/*    */ 
/*    */   public LinkTextView(Context context, AttributeSet attrs, int defStyle) {
/* 20 */     super(context, attrs, defStyle);
/*    */   }
/*    */ 
/*    */   @SuppressLint({"ClickableViewAccessibility"})
/*    */   public boolean onTouchEvent(MotionEvent event) {
/* 26 */     return super.onTouchEvent(event);
/*    */   }
/*    */ 
/*    */   public boolean hasFocusable()
/*    */   {
/* 31 */     return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.model.LinkTextView
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.imkit.widget;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.graphics.drawable.Drawable;
/*    */ import android.util.AttributeSet;
/*    */ import android.widget.FrameLayout;
/*    */ 
/*    */ public class RCMessageFrameLayout extends FrameLayout
/*    */ {
/*    */   private Drawable mOldDrawable;
/*    */ 
/*    */   public RCMessageFrameLayout(Context context)
/*    */   {
/* 12 */     super(context);
/*    */   }
/*    */ 
/*    */   public RCMessageFrameLayout(Context context, AttributeSet attrs) {
/* 16 */     super(context, attrs);
/*    */   }
/*    */ 
/*    */   public RCMessageFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
/* 20 */     super(context, attrs, defStyleAttr);
/*    */   }
/*    */ 
/*    */   public void setBackgroundResource(int resid)
/*    */   {
/* 25 */     super.setBackgroundResource(resid);
/* 26 */     this.mOldDrawable = getBackground();
/* 27 */     setBackgroundDrawable(null);
/* 28 */     setPadding(0, 0, 0, 0);
/*    */   }
/*    */ 
/*    */   public Drawable getBackgroundDrawable() {
/* 32 */     return this.mOldDrawable;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.RCMessageFrameLayout
 * JD-Core Version:    0.6.0
 */
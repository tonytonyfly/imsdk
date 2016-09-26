/*    */ package io.rong.imkit.widget;
/*    */ 
/*    */ import android.text.Layout;
/*    */ import android.text.Spannable;
/*    */ import android.text.method.LinkMovementMethod;
/*    */ import android.text.method.Touch;
/*    */ import android.text.style.ClickableSpan;
/*    */ import android.text.style.URLSpan;
/*    */ import android.view.MotionEvent;
/*    */ import android.view.ViewConfiguration;
/*    */ import android.widget.TextView;
/*    */ 
/*    */ public class LinkTextViewMovementMethod extends LinkMovementMethod
/*    */ {
/*    */   private long mLastActionDownTime;
/*    */   private ILinkClickListener mListener;
/*    */ 
/*    */   public LinkTextViewMovementMethod(ILinkClickListener listener)
/*    */   {
/* 19 */     this.mListener = listener;
/*    */   }
/*    */ 
/*    */   public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event)
/*    */   {
/* 24 */     int action = event.getAction();
/*    */ 
/* 26 */     if ((action == 1) || (action == 0)) {
/* 27 */       int x = (int)event.getX();
/* 28 */       int y = (int)event.getY();
/*    */ 
/* 30 */       x -= widget.getTotalPaddingLeft();
/* 31 */       y -= widget.getTotalPaddingTop();
/*    */ 
/* 33 */       x += widget.getScrollX();
/* 34 */       y += widget.getScrollY();
/*    */ 
/* 36 */       Layout layout = widget.getLayout();
/* 37 */       int line = layout.getLineForVertical(y);
/* 38 */       int off = layout.getOffsetForHorizontal(line, x);
/*    */ 
/* 40 */       ClickableSpan[] link = (ClickableSpan[])buffer.getSpans(off, off, ClickableSpan.class);
/*    */ 
/* 42 */       if (link.length != 0) {
/* 43 */         if (action == 1) {
/* 44 */           long actionUpTime = System.currentTimeMillis();
/* 45 */           if (actionUpTime - this.mLastActionDownTime > ViewConfiguration.getLongPressTimeout()) {
/* 46 */             return true;
/*    */           }
/* 48 */           String url = null;
/* 49 */           if ((link[0] instanceof URLSpan))
/* 50 */             url = ((URLSpan)link[0]).getURL();
/* 51 */           if ((this.mListener != null) && (this.mListener.onLinkClick(url))) {
/* 52 */             return true;
/*    */           }
/* 54 */           link[0].onClick(widget);
/* 55 */         } else if (action == 0) {
/* 56 */           this.mLastActionDownTime = System.currentTimeMillis();
/*    */         }
/* 58 */         return true;
/*    */       }
/* 60 */       Touch.onTouchEvent(widget, buffer, event);
/* 61 */       return false;
/*    */     }
/*    */ 
/* 64 */     return Touch.onTouchEvent(widget, buffer, event);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.LinkTextViewMovementMethod
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.photoview;
/*    */ 
/*    */ import android.graphics.RectF;
/*    */ import android.view.GestureDetector.OnDoubleTapListener;
/*    */ import android.view.MotionEvent;
/*    */ import android.widget.ImageView;
/*    */ 
/*    */ public class DefaultOnDoubleTapListener
/*    */   implements GestureDetector.OnDoubleTapListener
/*    */ {
/*    */   private PhotoViewAttacher photoViewAttacher;
/*    */ 
/*    */   public DefaultOnDoubleTapListener(PhotoViewAttacher photoViewAttacher)
/*    */   {
/* 25 */     setPhotoViewAttacher(photoViewAttacher);
/*    */   }
/*    */ 
/*    */   public void setPhotoViewAttacher(PhotoViewAttacher newPhotoViewAttacher)
/*    */   {
/* 34 */     this.photoViewAttacher = newPhotoViewAttacher;
/*    */   }
/*    */ 
/*    */   public boolean onSingleTapConfirmed(MotionEvent e)
/*    */   {
/* 39 */     if (this.photoViewAttacher == null) {
/* 40 */       return false;
/*    */     }
/* 42 */     ImageView imageView = this.photoViewAttacher.getImageView();
/*    */ 
/* 44 */     if (null != this.photoViewAttacher.getOnPhotoTapListener()) {
/* 45 */       RectF displayRect = this.photoViewAttacher.getDisplayRect();
/*    */ 
/* 47 */       if (null != displayRect) {
/* 48 */         float x = e.getX(); float y = e.getY();
/*    */ 
/* 51 */         if (displayRect.contains(x, y))
/*    */         {
/* 53 */           float xResult = (x - displayRect.left) / displayRect.width();
/*    */ 
/* 55 */           float yResult = (y - displayRect.top) / displayRect.height();
/*    */ 
/* 58 */           this.photoViewAttacher.getOnPhotoTapListener().onPhotoTap(imageView, xResult, yResult);
/* 59 */           return true;
/*    */         }
/* 61 */         this.photoViewAttacher.getOnPhotoTapListener().onOutsidePhotoTap();
/*    */       }
/*    */     }
/*    */ 
/* 65 */     if (null != this.photoViewAttacher.getOnViewTapListener()) {
/* 66 */       this.photoViewAttacher.getOnViewTapListener().onViewTap(imageView, e.getX(), e.getY());
/*    */     }
/*    */ 
/* 69 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean onDoubleTap(MotionEvent ev)
/*    */   {
/* 74 */     if (this.photoViewAttacher == null)
/* 75 */       return false;
/*    */     try
/*    */     {
/* 78 */       float scale = this.photoViewAttacher.getScale();
/* 79 */       float x = ev.getX();
/* 80 */       float y = ev.getY();
/*    */ 
/* 82 */       if (scale < this.photoViewAttacher.getMediumScale())
/* 83 */         this.photoViewAttacher.setScale(this.photoViewAttacher.getMediumScale(), x, y, true);
/* 84 */       else if ((scale >= this.photoViewAttacher.getMediumScale()) && (scale < this.photoViewAttacher.getMaximumScale()))
/* 85 */         this.photoViewAttacher.setScale(this.photoViewAttacher.getMaximumScale(), x, y, true);
/*    */       else {
/* 87 */         this.photoViewAttacher.setScale(this.photoViewAttacher.getMinimumScale(), x, y, true);
/*    */       }
/*    */     }
/*    */     catch (ArrayIndexOutOfBoundsException e)
/*    */     {
/*    */     }
/* 93 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean onDoubleTapEvent(MotionEvent e)
/*    */   {
/* 99 */     return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.photoview.DefaultOnDoubleTapListener
 * JD-Core Version:    0.6.0
 */
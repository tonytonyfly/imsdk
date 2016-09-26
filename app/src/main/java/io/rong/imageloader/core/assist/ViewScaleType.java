/*    */ package io.rong.imageloader.core.assist;
/*    */ 
/*    */ import android.widget.ImageView;
/*    */ 
/*    */ public enum ViewScaleType
/*    */ {
/* 32 */   FIT_INSIDE, 
/*    */ 
/* 37 */   CROP;
/*    */ 
/*    */   public static ViewScaleType fromImageView(ImageView imageView)
/*    */   {
/* 59 */     switch (1.$SwitchMap$android$widget$ImageView$ScaleType[imageView.getScaleType().ordinal()]) {
/*    */     case 1:
/*    */     case 2:
/*    */     case 3:
/*    */     case 4:
/*    */     case 5:
/* 65 */       return FIT_INSIDE;
/*    */     case 6:
/*    */     case 7:
/*    */     case 8:
/*    */     }
/* 70 */     return CROP;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.assist.ViewScaleType
 * JD-Core Version:    0.6.0
 */
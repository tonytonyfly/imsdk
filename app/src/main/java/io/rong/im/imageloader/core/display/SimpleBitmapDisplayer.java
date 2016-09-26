/*    */ package io.rong.imageloader.core.display;
/*    */ 
/*    */ import android.graphics.Bitmap;
/*    */ import io.rong.imageloader.core.assist.LoadedFrom;
/*    */ import io.rong.imageloader.core.imageaware.ImageAware;
/*    */ 
/*    */ public final class SimpleBitmapDisplayer
/*    */   implements BitmapDisplayer
/*    */ {
/*    */   public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom)
/*    */   {
/* 31 */     imageAware.setImageBitmap(bitmap);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.display.SimpleBitmapDisplayer
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.photoview;
/*    */ 
/*    */ import android.annotation.TargetApi;
/*    */ import android.os.Build.VERSION;
/*    */ import android.view.View;
/*    */ 
/*    */ public class Compat
/*    */ {
/*    */   private static final int SIXTY_FPS_INTERVAL = 16;
/*    */ 
/*    */   public static void postOnAnimation(View view, Runnable runnable)
/*    */   {
/* 30 */     if (Build.VERSION.SDK_INT >= 16)
/* 31 */       postOnAnimationJellyBean(view, runnable);
/*    */     else
/* 33 */       view.postDelayed(runnable, 16L);
/*    */   }
/*    */ 
/*    */   @TargetApi(16)
/*    */   private static void postOnAnimationJellyBean(View view, Runnable runnable) {
/* 39 */     view.postOnAnimation(runnable);
/*    */   }
/*    */ 
/*    */   public static int getPointerIndex(int action) {
/* 43 */     if (Build.VERSION.SDK_INT >= 11) {
/* 44 */       return getPointerIndexHoneyComb(action);
/*    */     }
/* 46 */     return getPointerIndexEclair(action);
/*    */   }
/*    */ 
/*    */   @TargetApi(5)
/*    */   private static int getPointerIndexEclair(int action) {
/* 52 */     return (action & 0xFF00) >> 8;
/*    */   }
/*    */   @TargetApi(11)
/*    */   private static int getPointerIndexHoneyComb(int action) {
/* 57 */     return (action & 0xFF00) >> 8;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.photoview.Compat
 * JD-Core Version:    0.6.0
 */
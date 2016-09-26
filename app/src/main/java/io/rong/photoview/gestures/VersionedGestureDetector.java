/*    */ package io.rong.photoview.gestures;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.os.Build.VERSION;
/*    */ 
/*    */ public final class VersionedGestureDetector
/*    */ {
/*    */   public static GestureDetector newInstance(Context context, OnGestureListener listener)
/*    */   {
/* 32 */     int sdkVersion = Build.VERSION.SDK_INT;
/*    */     GestureDetector detector;
/*    */     GestureDetector detector;
/* 35 */     if (sdkVersion < 5) {
/* 36 */       detector = new CupcakeGestureDetector(context);
/*    */     }
/*    */     else
/*    */     {
/*    */       GestureDetector detector;
/* 37 */       if (sdkVersion < 8)
/* 38 */         detector = new EclairGestureDetector(context);
/*    */       else {
/* 40 */         detector = new FroyoGestureDetector(context);
/*    */       }
/*    */     }
/* 43 */     detector.setOnGestureListener(listener);
/*    */ 
/* 45 */     return detector;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.photoview.gestures.VersionedGestureDetector
 * JD-Core Version:    0.6.0
 */
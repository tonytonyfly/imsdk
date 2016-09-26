/*    */ package io.rong.imageloader.cache.memory.impl;
/*    */ 
/*    */ import android.graphics.Bitmap;
/*    */ import io.rong.imageloader.cache.memory.BaseMemoryCache;
/*    */ import java.lang.ref.Reference;
/*    */ import java.lang.ref.WeakReference;
/*    */ 
/*    */ public class WeakMemoryCache extends BaseMemoryCache
/*    */ {
/*    */   protected Reference<Bitmap> createReference(Bitmap value)
/*    */   {
/* 35 */     return new WeakReference(value);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.memory.impl.WeakMemoryCache
 * JD-Core Version:    0.6.0
 */
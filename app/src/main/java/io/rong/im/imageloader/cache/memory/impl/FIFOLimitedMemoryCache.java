/*    */ package io.rong.imageloader.cache.memory.impl;
/*    */ 
/*    */ import android.graphics.Bitmap;
/*    */ import io.rong.imageloader.cache.memory.LimitedMemoryCache;
/*    */ import java.lang.ref.Reference;
/*    */ import java.lang.ref.WeakReference;
/*    */ import java.util.Collections;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class FIFOLimitedMemoryCache extends LimitedMemoryCache
/*    */ {
/* 39 */   private final List<Bitmap> queue = Collections.synchronizedList(new LinkedList());
/*    */ 
/*    */   public FIFOLimitedMemoryCache(int sizeLimit) {
/* 42 */     super(sizeLimit);
/*    */   }
/*    */ 
/*    */   public boolean put(String key, Bitmap value)
/*    */   {
/* 47 */     if (super.put(key, value)) {
/* 48 */       this.queue.add(value);
/* 49 */       return true;
/*    */     }
/* 51 */     return false;
/*    */   }
/*    */ 
/*    */   public Bitmap remove(String key)
/*    */   {
/* 57 */     Bitmap value = super.get(key);
/* 58 */     if (value != null) {
/* 59 */       this.queue.remove(value);
/*    */     }
/* 61 */     return super.remove(key);
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/* 66 */     this.queue.clear();
/* 67 */     super.clear();
/*    */   }
/*    */ 
/*    */   protected int getSize(Bitmap value)
/*    */   {
/* 72 */     return value.getRowBytes() * value.getHeight();
/*    */   }
/*    */ 
/*    */   protected Bitmap removeNext()
/*    */   {
/* 77 */     return (Bitmap)this.queue.remove(0);
/*    */   }
/*    */ 
/*    */   protected Reference<Bitmap> createReference(Bitmap value)
/*    */   {
/* 82 */     return new WeakReference(value);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.memory.impl.FIFOLimitedMemoryCache
 * JD-Core Version:    0.6.0
 */
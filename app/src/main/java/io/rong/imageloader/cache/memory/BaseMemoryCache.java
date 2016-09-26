/*    */ package io.rong.imageloader.cache.memory;
/*    */ 
/*    */ import android.graphics.Bitmap;
/*    */ import java.lang.ref.Reference;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.HashSet;
/*    */ import java.util.Map;
/*    */ 
/*    */ public abstract class BaseMemoryCache
/*    */   implements MemoryCache
/*    */ {
/* 33 */   private final Map<String, Reference<Bitmap>> softMap = Collections.synchronizedMap(new HashMap());
/*    */ 
/*    */   public Bitmap get(String key)
/*    */   {
/* 37 */     Bitmap result = null;
/* 38 */     Reference reference = (Reference)this.softMap.get(key);
/* 39 */     if (reference != null) {
/* 40 */       result = (Bitmap)reference.get();
/*    */     }
/* 42 */     return result;
/*    */   }
/*    */ 
/*    */   public boolean put(String key, Bitmap value)
/*    */   {
/* 47 */     this.softMap.put(key, createReference(value));
/* 48 */     return true;
/*    */   }
/*    */ 
/*    */   public Bitmap remove(String key)
/*    */   {
/* 53 */     Reference bmpRef = (Reference)this.softMap.remove(key);
/* 54 */     return bmpRef == null ? null : (Bitmap)bmpRef.get();
/*    */   }
/*    */ 
/*    */   public Collection<String> keys()
/*    */   {
/* 59 */     synchronized (this.softMap) {
/* 60 */       return new HashSet(this.softMap.keySet());
/*    */     }
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/* 66 */     this.softMap.clear();
/*    */   }
/*    */ 
/*    */   protected abstract Reference<Bitmap> createReference(Bitmap paramBitmap);
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.memory.BaseMemoryCache
 * JD-Core Version:    0.6.0
 */
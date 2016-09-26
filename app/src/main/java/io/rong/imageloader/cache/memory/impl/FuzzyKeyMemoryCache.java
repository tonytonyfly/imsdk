/*    */ package io.rong.imageloader.cache.memory.impl;
/*    */ 
/*    */ import android.graphics.Bitmap;
/*    */ import io.rong.imageloader.cache.memory.MemoryCache;
/*    */ import java.util.Collection;
/*    */ import java.util.Comparator;
/*    */ 
/*    */ public class FuzzyKeyMemoryCache
/*    */   implements MemoryCache
/*    */ {
/*    */   private final MemoryCache cache;
/*    */   private final Comparator<String> keyComparator;
/*    */ 
/*    */   public FuzzyKeyMemoryCache(MemoryCache cache, Comparator<String> keyComparator)
/*    */   {
/* 40 */     this.cache = cache;
/* 41 */     this.keyComparator = keyComparator;
/*    */   }
/*    */ 
/*    */   public boolean put(String key, Bitmap value)
/*    */   {
/* 47 */     synchronized (this.cache) {
/* 48 */       String keyToRemove = null;
/* 49 */       for (String cacheKey : this.cache.keys()) {
/* 50 */         if (this.keyComparator.compare(key, cacheKey) == 0) {
/* 51 */           keyToRemove = cacheKey;
/* 52 */           break;
/*    */         }
/*    */       }
/* 55 */       if (keyToRemove != null) {
/* 56 */         this.cache.remove(keyToRemove);
/*    */       }
/*    */     }
/* 59 */     return this.cache.put(key, value);
/*    */   }
/*    */ 
/*    */   public Bitmap get(String key)
/*    */   {
/* 64 */     return this.cache.get(key);
/*    */   }
/*    */ 
/*    */   public Bitmap remove(String key)
/*    */   {
/* 69 */     return this.cache.remove(key);
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/* 74 */     this.cache.clear();
/*    */   }
/*    */ 
/*    */   public Collection<String> keys()
/*    */   {
/* 79 */     return this.cache.keys();
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.memory.impl.FuzzyKeyMemoryCache
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.imageloader.cache.memory.impl;
/*    */ 
/*    */ import android.graphics.Bitmap;
/*    */ import io.rong.imageloader.cache.memory.MemoryCache;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class LimitedAgeMemoryCache
/*    */   implements MemoryCache
/*    */ {
/*    */   private final MemoryCache cache;
/*    */   private final long maxAge;
/* 40 */   private final Map<String, Long> loadingDates = Collections.synchronizedMap(new HashMap());
/*    */ 
/*    */   public LimitedAgeMemoryCache(MemoryCache cache, long maxAge)
/*    */   {
/* 48 */     this.cache = cache;
/* 49 */     this.maxAge = (maxAge * 1000L);
/*    */   }
/*    */ 
/*    */   public boolean put(String key, Bitmap value)
/*    */   {
/* 54 */     boolean putSuccesfully = this.cache.put(key, value);
/* 55 */     if (putSuccesfully) {
/* 56 */       this.loadingDates.put(key, Long.valueOf(System.currentTimeMillis()));
/*    */     }
/* 58 */     return putSuccesfully;
/*    */   }
/*    */ 
/*    */   public Bitmap get(String key)
/*    */   {
/* 63 */     Long loadingDate = (Long)this.loadingDates.get(key);
/* 64 */     if ((loadingDate != null) && (System.currentTimeMillis() - loadingDate.longValue() > this.maxAge)) {
/* 65 */       this.cache.remove(key);
/* 66 */       this.loadingDates.remove(key);
/*    */     }
/*    */ 
/* 69 */     return this.cache.get(key);
/*    */   }
/*    */ 
/*    */   public Bitmap remove(String key)
/*    */   {
/* 74 */     this.loadingDates.remove(key);
/* 75 */     return this.cache.remove(key);
/*    */   }
/*    */ 
/*    */   public Collection<String> keys()
/*    */   {
/* 80 */     return this.cache.keys();
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/* 85 */     this.cache.clear();
/* 86 */     this.loadingDates.clear();
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.memory.impl.LimitedAgeMemoryCache
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.imkit.userInfoCache;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.Set;
/*    */ 
/*    */ class RongUserCache<K, V>
/*    */ {
/*    */   private final LinkedHashMap<K, V> map;
/*    */   private int size;
/*    */   private int maxSize;
/*    */ 
/*    */   RongUserCache(int maxSize)
/*    */   {
/* 12 */     if (maxSize <= 0) {
/* 13 */       throw new IllegalArgumentException("maxSize <= 0");
/*    */     }
/* 15 */     this.maxSize = maxSize;
/* 16 */     this.map = new LinkedHashMap(0, 0.75F, true);
/*    */   }
/*    */ 
/*    */   void clear() {
/* 20 */     synchronized (this) {
/* 21 */       this.map.clear();
/*    */     }
/*    */   }
/*    */ 
/*    */   V get(K key) {
/* 26 */     if (key == null) {
/* 27 */       return null;
/*    */     }
/*    */ 
/* 30 */     synchronized (this) {
/* 31 */       return this.map.get(key);
/*    */     }
/*    */   }
/*    */ 
/*    */   V put(K key, V value) {
/* 36 */     if ((key == null) || (value == null))
/* 37 */       throw new NullPointerException("key == null || value == null");
/*    */     Object previous;
/* 41 */     synchronized (this) {
/* 42 */       this.size += 1;
/* 43 */       previous = this.map.put(key, value);
/* 44 */       if (previous != null) {
/* 45 */         this.size -= 1;
/*    */       }
/* 47 */       if (this.size > this.maxSize) {
/* 48 */         Map.Entry toEvict = (Map.Entry)this.map.entrySet().iterator().next();
/* 49 */         if (toEvict == null) {
/* 50 */           return previous;
/*    */         }
/* 52 */         this.map.remove(toEvict.getKey());
/* 53 */         this.size -= 1;
/*    */       }
/*    */     }
/* 56 */     return previous;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.userInfoCache.RongUserCache
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.imkit.cache;
/*     */ 
/*     */ import TV;;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ 
/*     */ public class RongCache<K, V>
/*     */ {
/*     */   private final LinkedHashMap<K, V> map;
/*     */   private final LinkedBlockingQueue<K> queue;
/*     */   private int size;
/*     */   private int maxSize;
/*     */   private int putCount;
/*     */   private int createCount;
/*     */   private int evictionCount;
/*     */   private int hitCount;
/*     */   private int missCount;
/*     */ 
/*     */   public RongCache(int maxSize)
/*     */   {
/*  61 */     if (maxSize <= 0) {
/*  62 */       throw new IllegalArgumentException("maxSize <= 0");
/*     */     }
/*  64 */     this.maxSize = maxSize;
/*  65 */     this.map = new LinkedHashMap(0, 0.75F, true);
/*  66 */     this.queue = new LinkedBlockingQueue();
/*     */   }
/*     */ 
/*     */   public final V get(K key)
/*     */   {
/*  76 */     if (key == null)
/*  77 */       return null;
/*     */     Object mapValue;
/*  82 */     synchronized (this) {
/*  83 */       mapValue = this.map.get(key);
/*     */ 
/*  85 */       if (mapValue != null)
/*     */       {
/*  90 */         this.queue.remove(key);
/*  91 */         this.queue.offer(key);
/*     */ 
/*  93 */         this.hitCount += 1;
/*  94 */         this.map.remove(key);
/*  95 */         this.map.put(key, mapValue);
/*  96 */         return mapValue;
/*     */       }
/*  98 */       this.missCount += 1;
/*     */     }
/*     */ 
/* 108 */     Object createdValue = create(key);
/* 109 */     if (createdValue == null) {
/* 110 */       return null;
/*     */     }
/*     */ 
/* 114 */     synchronized (this) {
/* 115 */       this.createCount += 1;
/* 116 */       mapValue = this.map.put(key, createdValue);
/*     */ 
/* 118 */       if (mapValue != null)
/*     */       {
/* 120 */         this.map.put(key, mapValue);
/*     */       }
/*     */       else
/*     */       {
/* 125 */         this.queue.offer(key);
/* 126 */         this.size += safeSizeOf(key, createdValue);
/*     */       }
/*     */     }
/*     */ 
/* 130 */     if (mapValue != null) {
/* 131 */       entryRemoved(false, key, createdValue, mapValue);
/* 132 */       return mapValue;
/*     */     }
/* 134 */     trimToSize(this.maxSize);
/* 135 */     return (TV)createdValue;
/*     */   }
/*     */ 
/*     */   public final V put(K key, V value)
/*     */   {
/* 146 */     if ((key == null) || (value == null))
/* 147 */       throw new NullPointerException("key == null || value == null");
/*     */     Object previous;
/* 151 */     synchronized (this) {
/* 152 */       this.putCount += 1;
/* 153 */       this.size += safeSizeOf(key, value);
/* 154 */       previous = this.map.put(key, value);
/* 155 */       if (previous != null) {
/* 156 */         this.size -= safeSizeOf(key, previous);
/*     */       }
/*     */     }
/*     */ 
/* 160 */     if (previous != null) {
/* 161 */       entryRemoved(false, key, previous, value);
/*     */     }
/*     */     else
/*     */     {
/* 166 */       this.queue.offer(key);
/*     */     }
/*     */ 
/* 169 */     trimToSize(this.maxSize);
/* 170 */     return previous;
/*     */   }
/*     */ 
/*     */   public void trimToSize(int maxSize)
/*     */   {
/*     */     while (true)
/*     */     {
/*     */       Object key;
/*     */       Object value;
/* 184 */       synchronized (this) {
/* 185 */         if ((this.size < 0) || ((this.map.isEmpty()) && (this.size != 0))) {
/* 186 */           throw new IllegalStateException(getClass().getName() + ".sizeOf() is reporting inconsistent results!");
/*     */         }
/*     */ 
/* 190 */         if ((this.size > maxSize) && (this.map.isEmpty()))
/*     */         {
/*     */           break;
/*     */         }
/* 194 */         key = this.queue.poll();
/* 195 */         value = this.map.get(key);
/*     */ 
/* 197 */         this.map.remove(key);
/* 198 */         this.size -= safeSizeOf(key, value);
/* 199 */         this.evictionCount += 1;
/*     */ 
/* 204 */         this.queue.remove(key);
/*     */       }
/*     */ 
/* 208 */       entryRemoved(true, key, value, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final V remove(K key)
/*     */   {
/* 218 */     if (key == null)
/* 219 */       throw new NullPointerException("key == null");
/*     */     Object previous;
/* 223 */     synchronized (this) {
/* 224 */       previous = this.map.remove(key);
/* 225 */       if (previous != null)
/*     */       {
/* 229 */         this.queue.remove(key);
/* 230 */         this.size -= safeSizeOf(key, previous);
/*     */       }
/*     */     }
/*     */ 
/* 234 */     if (previous != null) {
/* 235 */       entryRemoved(false, key, previous, null);
/*     */     }
/*     */ 
/* 238 */     return previous;
/*     */   }
/*     */ 
/*     */   protected void entryRemoved(boolean evicted, K key, V oldValue, V newValue)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected V create(K key)
/*     */   {
/* 275 */     return null;
/*     */   }
/*     */ 
/*     */   private int safeSizeOf(K key, V value) {
/* 279 */     int result = sizeOf(key, value);
/* 280 */     if (result < 0) {
/* 281 */       throw new IllegalStateException("Negative size: " + key + "=" + value);
/*     */     }
/* 283 */     return result;
/*     */   }
/*     */ 
/*     */   protected int sizeOf(K key, V value)
/*     */   {
/* 294 */     return 1;
/*     */   }
/*     */ 
/*     */   public final void evictAll()
/*     */   {
/* 301 */     trimToSize(-1);
/*     */   }
/*     */ 
/*     */   public final synchronized int size()
/*     */   {
/* 310 */     return this.size;
/*     */   }
/*     */ 
/*     */   public final synchronized int maxSize()
/*     */   {
/* 319 */     return this.maxSize;
/*     */   }
/*     */ 
/*     */   public final synchronized int hitCount()
/*     */   {
/* 326 */     return this.hitCount;
/*     */   }
/*     */ 
/*     */   public final synchronized int missCount()
/*     */   {
/* 334 */     return this.missCount;
/*     */   }
/*     */ 
/*     */   public final synchronized int createCount()
/*     */   {
/* 341 */     return this.createCount;
/*     */   }
/*     */ 
/*     */   public final synchronized int putCount()
/*     */   {
/* 348 */     return this.putCount;
/*     */   }
/*     */ 
/*     */   public final synchronized int evictionCount()
/*     */   {
/* 355 */     return this.evictionCount;
/*     */   }
/*     */ 
/*     */   public final synchronized Map<K, V> snapshot()
/*     */   {
/* 363 */     return new LinkedHashMap(this.map);
/*     */   }
/*     */ 
/*     */   public final synchronized String toString()
/*     */   {
/* 368 */     int accesses = this.hitCount + this.missCount;
/* 369 */     int hitPercent = accesses != 0 ? 100 * this.hitCount / accesses : 0;
/* 370 */     return String.format("LruCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]", new Object[] { Integer.valueOf(this.maxSize), Integer.valueOf(this.hitCount), Integer.valueOf(this.missCount), Integer.valueOf(hitPercent) });
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.cache.RongCache
 * JD-Core Version:    0.6.0
 */
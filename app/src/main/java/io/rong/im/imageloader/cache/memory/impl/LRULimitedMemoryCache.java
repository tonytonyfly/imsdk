/*     */ package io.rong.imageloader.cache.memory.impl;
/*     */ 
/*     */ import android.graphics.Bitmap;
/*     */ import io.rong.imageloader.cache.memory.LimitedMemoryCache;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class LRULimitedMemoryCache extends LimitedMemoryCache
/*     */ {
/*     */   private static final int INITIAL_CAPACITY = 10;
/*     */   private static final float LOAD_FACTOR = 1.1F;
/*  45 */   private final Map<String, Bitmap> lruCache = Collections.synchronizedMap(new LinkedHashMap(10, 1.1F, true));
/*     */ 
/*     */   public LRULimitedMemoryCache(int maxSize)
/*     */   {
/*  49 */     super(maxSize);
/*     */   }
/*     */ 
/*     */   public boolean put(String key, Bitmap value)
/*     */   {
/*  54 */     if (super.put(key, value)) {
/*  55 */       this.lruCache.put(key, value);
/*  56 */       return true;
/*     */     }
/*  58 */     return false;
/*     */   }
/*     */ 
/*     */   public Bitmap get(String key)
/*     */   {
/*  64 */     this.lruCache.get(key);
/*  65 */     return super.get(key);
/*     */   }
/*     */ 
/*     */   public Bitmap remove(String key)
/*     */   {
/*  70 */     this.lruCache.remove(key);
/*  71 */     return super.remove(key);
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  76 */     this.lruCache.clear();
/*  77 */     super.clear();
/*     */   }
/*     */ 
/*     */   protected int getSize(Bitmap value)
/*     */   {
/*  82 */     return value.getRowBytes() * value.getHeight();
/*     */   }
/*     */ 
/*     */   protected Bitmap removeNext()
/*     */   {
/*  87 */     Bitmap mostLongUsedValue = null;
/*  88 */     synchronized (this.lruCache) {
/*  89 */       Iterator it = this.lruCache.entrySet().iterator();
/*  90 */       if (it.hasNext()) {
/*  91 */         Map.Entry entry = (Map.Entry)it.next();
/*  92 */         mostLongUsedValue = (Bitmap)entry.getValue();
/*  93 */         it.remove();
/*     */       }
/*     */     }
/*  96 */     return mostLongUsedValue;
/*     */   }
/*     */ 
/*     */   protected Reference<Bitmap> createReference(Bitmap value)
/*     */   {
/* 101 */     return new WeakReference(value);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.memory.impl.LRULimitedMemoryCache
 * JD-Core Version:    0.6.0
 */
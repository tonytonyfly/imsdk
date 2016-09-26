/*     */ package io.rong.imageloader.cache.memory.impl;
/*     */ 
/*     */ import android.graphics.Bitmap;
/*     */ import io.rong.imageloader.cache.memory.MemoryCache;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class LruMemoryCache
/*     */   implements MemoryCache
/*     */ {
/*     */   private final LinkedHashMap<String, Bitmap> map;
/*     */   private final int maxSize;
/*     */   private int size;
/*     */ 
/*     */   public LruMemoryCache(int maxSize)
/*     */   {
/*  32 */     if (maxSize <= 0) {
/*  33 */       throw new IllegalArgumentException("maxSize <= 0");
/*     */     }
/*  35 */     this.maxSize = maxSize;
/*  36 */     this.map = new LinkedHashMap(0, 0.75F, true);
/*     */   }
/*     */ 
/*     */   public final Bitmap get(String key)
/*     */   {
/*  45 */     if (key == null) {
/*  46 */       throw new NullPointerException("key == null");
/*     */     }
/*     */ 
/*  49 */     synchronized (this) {
/*  50 */       return (Bitmap)this.map.get(key);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final boolean put(String key, Bitmap value)
/*     */   {
/*  57 */     if ((key == null) || (value == null)) {
/*  58 */       throw new NullPointerException("key == null || value == null");
/*     */     }
/*     */ 
/*  61 */     synchronized (this) {
/*  62 */       this.size += sizeOf(key, value);
/*  63 */       Bitmap previous = (Bitmap)this.map.put(key, value);
/*  64 */       if (previous != null) {
/*  65 */         this.size -= sizeOf(key, previous);
/*     */       }
/*     */     }
/*     */ 
/*  69 */     trimToSize(this.maxSize);
/*  70 */     return true;
/*     */   }
/*     */ 
/*     */   private void trimToSize(int maxSize)
/*     */   {
/*     */     while (true)
/*  82 */       synchronized (this) {
/*  83 */         if ((this.size < 0) || ((this.map.isEmpty()) && (this.size != 0))) {
/*  84 */           throw new IllegalStateException(getClass().getName() + ".sizeOf() is reporting inconsistent results!");
/*     */         }
/*     */ 
/*  87 */         if ((this.size > maxSize) && (this.map.isEmpty()))
/*     */         {
/*     */           break;
/*     */         }
/*  91 */         Map.Entry toEvict = (Map.Entry)this.map.entrySet().iterator().next();
/*  92 */         if (toEvict == null) {
/*     */           break;
/*     */         }
/*  95 */         String key = (String)toEvict.getKey();
/*  96 */         Bitmap value = (Bitmap)toEvict.getValue();
/*  97 */         this.map.remove(key);
/*  98 */         this.size -= sizeOf(key, value);
/*     */       }
/*     */   }
/*     */ 
/*     */   public final Bitmap remove(String key)
/*     */   {
/* 106 */     if (key == null) {
/* 107 */       throw new NullPointerException("key == null");
/*     */     }
/*     */ 
/* 110 */     synchronized (this) {
/* 111 */       Bitmap previous = (Bitmap)this.map.remove(key);
/* 112 */       if (previous != null) {
/* 113 */         this.size -= sizeOf(key, previous);
/*     */       }
/* 115 */       return previous;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Collection<String> keys()
/*     */   {
/* 121 */     synchronized (this) {
/* 122 */       return new HashSet(this.map.keySet());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 128 */     trimToSize(-1);
/*     */   }
/*     */ 
/*     */   private int sizeOf(String key, Bitmap value)
/*     */   {
/* 137 */     return value.getRowBytes() * value.getHeight();
/*     */   }
/*     */ 
/*     */   public final synchronized String toString()
/*     */   {
/* 142 */     return String.format("LruCache[maxSize=%d]", new Object[] { Integer.valueOf(this.maxSize) });
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.memory.impl.LruMemoryCache
 * JD-Core Version:    0.6.0
 */
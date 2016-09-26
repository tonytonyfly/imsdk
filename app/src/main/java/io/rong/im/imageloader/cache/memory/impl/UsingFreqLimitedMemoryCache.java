/*     */ package io.rong.imageloader.cache.memory.impl;
/*     */ 
/*     */ import android.graphics.Bitmap;
/*     */ import io.rong.imageloader.cache.memory.LimitedMemoryCache;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class UsingFreqLimitedMemoryCache extends LimitedMemoryCache
/*     */ {
/*  46 */   private final Map<Bitmap, Integer> usingCounts = Collections.synchronizedMap(new HashMap());
/*     */ 
/*     */   public UsingFreqLimitedMemoryCache(int sizeLimit) {
/*  49 */     super(sizeLimit);
/*     */   }
/*     */ 
/*     */   public boolean put(String key, Bitmap value)
/*     */   {
/*  54 */     if (super.put(key, value)) {
/*  55 */       this.usingCounts.put(value, Integer.valueOf(0));
/*  56 */       return true;
/*     */     }
/*  58 */     return false;
/*     */   }
/*     */ 
/*     */   public Bitmap get(String key)
/*     */   {
/*  64 */     Bitmap value = super.get(key);
/*     */ 
/*  66 */     if (value != null) {
/*  67 */       Integer usageCount = (Integer)this.usingCounts.get(value);
/*  68 */       if (usageCount != null) {
/*  69 */         this.usingCounts.put(value, Integer.valueOf(usageCount.intValue() + 1));
/*     */       }
/*     */     }
/*  72 */     return value;
/*     */   }
/*     */ 
/*     */   public Bitmap remove(String key)
/*     */   {
/*  77 */     Bitmap value = super.get(key);
/*  78 */     if (value != null) {
/*  79 */       this.usingCounts.remove(value);
/*     */     }
/*  81 */     return super.remove(key);
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  86 */     this.usingCounts.clear();
/*  87 */     super.clear();
/*     */   }
/*     */ 
/*     */   protected int getSize(Bitmap value)
/*     */   {
/*  92 */     return value.getRowBytes() * value.getHeight();
/*     */   }
/*     */ 
/*     */   protected Bitmap removeNext()
/*     */   {
/*  97 */     Integer minUsageCount = null;
/*  98 */     Bitmap leastUsedValue = null;
/*  99 */     Set entries = this.usingCounts.entrySet();
/* 100 */     synchronized (this.usingCounts) {
/* 101 */       for (Map.Entry entry : entries) {
/* 102 */         if (leastUsedValue == null) {
/* 103 */           leastUsedValue = (Bitmap)entry.getKey();
/* 104 */           minUsageCount = (Integer)entry.getValue();
/*     */         } else {
/* 106 */           Integer lastValueUsage = (Integer)entry.getValue();
/* 107 */           if (lastValueUsage.intValue() < minUsageCount.intValue()) {
/* 108 */             minUsageCount = lastValueUsage;
/* 109 */             leastUsedValue = (Bitmap)entry.getKey();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 114 */     this.usingCounts.remove(leastUsedValue);
/* 115 */     return leastUsedValue;
/*     */   }
/*     */ 
/*     */   protected Reference<Bitmap> createReference(Bitmap value)
/*     */   {
/* 120 */     return new WeakReference(value);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.memory.impl.UsingFreqLimitedMemoryCache
 * JD-Core Version:    0.6.0
 */
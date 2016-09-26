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
/*     */ public class LargestLimitedMemoryCache extends LimitedMemoryCache
/*     */ {
/*  46 */   private final Map<Bitmap, Integer> valueSizes = Collections.synchronizedMap(new HashMap());
/*     */ 
/*     */   public LargestLimitedMemoryCache(int sizeLimit) {
/*  49 */     super(sizeLimit);
/*     */   }
/*     */ 
/*     */   public boolean put(String key, Bitmap value)
/*     */   {
/*  54 */     if (super.put(key, value)) {
/*  55 */       this.valueSizes.put(value, Integer.valueOf(getSize(value)));
/*  56 */       return true;
/*     */     }
/*  58 */     return false;
/*     */   }
/*     */ 
/*     */   public Bitmap remove(String key)
/*     */   {
/*  64 */     Bitmap value = super.get(key);
/*  65 */     if (value != null) {
/*  66 */       this.valueSizes.remove(value);
/*     */     }
/*  68 */     return super.remove(key);
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  73 */     this.valueSizes.clear();
/*  74 */     super.clear();
/*     */   }
/*     */ 
/*     */   protected int getSize(Bitmap value)
/*     */   {
/*  79 */     return value.getRowBytes() * value.getHeight();
/*     */   }
/*     */ 
/*     */   protected Bitmap removeNext()
/*     */   {
/*  84 */     Integer maxSize = null;
/*  85 */     Bitmap largestValue = null;
/*  86 */     Set entries = this.valueSizes.entrySet();
/*  87 */     synchronized (this.valueSizes) {
/*  88 */       for (Map.Entry entry : entries) {
/*  89 */         if (largestValue == null) {
/*  90 */           largestValue = (Bitmap)entry.getKey();
/*  91 */           maxSize = (Integer)entry.getValue();
/*     */         } else {
/*  93 */           Integer size = (Integer)entry.getValue();
/*  94 */           if (size.intValue() > maxSize.intValue()) {
/*  95 */             maxSize = size;
/*  96 */             largestValue = (Bitmap)entry.getKey();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 101 */     this.valueSizes.remove(largestValue);
/* 102 */     return largestValue;
/*     */   }
/*     */ 
/*     */   protected Reference<Bitmap> createReference(Bitmap value)
/*     */   {
/* 107 */     return new WeakReference(value);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.memory.impl.LargestLimitedMemoryCache
 * JD-Core Version:    0.6.0
 */
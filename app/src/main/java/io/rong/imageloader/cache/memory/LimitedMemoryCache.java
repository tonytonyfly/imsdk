/*     */ package io.rong.imageloader.cache.memory;
/*     */ 
/*     */ import android.graphics.Bitmap;
/*     */ import io.rong.imageloader.utils.L;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ 
/*     */ public abstract class LimitedMemoryCache extends BaseMemoryCache
/*     */ {
/*     */   private static final int MAX_NORMAL_CACHE_SIZE_IN_MB = 16;
/*     */   private static final int MAX_NORMAL_CACHE_SIZE = 16777216;
/*     */   private final int sizeLimit;
/*     */   private final AtomicInteger cacheSize;
/*  52 */   private final List<Bitmap> hardCache = Collections.synchronizedList(new LinkedList());
/*     */ 
/*     */   public LimitedMemoryCache(int sizeLimit)
/*     */   {
/*  56 */     this.sizeLimit = sizeLimit;
/*  57 */     this.cacheSize = new AtomicInteger();
/*  58 */     if (sizeLimit > 16777216)
/*  59 */       L.w("You set too large memory cache size (more than %1$d Mb)", new Object[] { Integer.valueOf(16) });
/*     */   }
/*     */ 
/*     */   public boolean put(String key, Bitmap value)
/*     */   {
/*  65 */     boolean putSuccessfully = false;
/*     */ 
/*  67 */     int valueSize = getSize(value);
/*  68 */     int sizeLimit = getSizeLimit();
/*  69 */     int curCacheSize = this.cacheSize.get();
/*  70 */     if (valueSize < sizeLimit) {
/*  71 */       while (curCacheSize + valueSize > sizeLimit) {
/*  72 */         Bitmap removedValue = removeNext();
/*  73 */         if (this.hardCache.remove(removedValue)) {
/*  74 */           curCacheSize = this.cacheSize.addAndGet(-getSize(removedValue));
/*     */         }
/*     */       }
/*  77 */       this.hardCache.add(value);
/*  78 */       this.cacheSize.addAndGet(valueSize);
/*     */ 
/*  80 */       putSuccessfully = true;
/*     */     }
/*     */ 
/*  83 */     super.put(key, value);
/*  84 */     return putSuccessfully;
/*     */   }
/*     */ 
/*     */   public Bitmap remove(String key)
/*     */   {
/*  89 */     Bitmap value = super.get(key);
/*  90 */     if ((value != null) && 
/*  91 */       (this.hardCache.remove(value))) {
/*  92 */       this.cacheSize.addAndGet(-getSize(value));
/*     */     }
/*     */ 
/*  95 */     return super.remove(key);
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 100 */     this.hardCache.clear();
/* 101 */     this.cacheSize.set(0);
/* 102 */     super.clear();
/*     */   }
/*     */ 
/*     */   protected int getSizeLimit() {
/* 106 */     return this.sizeLimit;
/*     */   }
/*     */ 
/*     */   protected abstract int getSize(Bitmap paramBitmap);
/*     */ 
/*     */   protected abstract Bitmap removeNext();
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.memory.LimitedMemoryCache
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.imageloader.utils;
/*     */ 
/*     */ import android.graphics.Bitmap;
/*     */ import io.rong.imageloader.cache.memory.MemoryCache;
/*     */ import io.rong.imageloader.core.assist.ImageSize;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ 
/*     */ public final class MemoryCacheUtils
/*     */ {
/*     */   private static final String URI_AND_SIZE_SEPARATOR = "_";
/*     */   private static final String WIDTH_AND_HEIGHT_SEPARATOR = "x";
/*     */ 
/*     */   public static String generateKey(String imageUri, ImageSize targetSize)
/*     */   {
/*  46 */     return imageUri + "_" + targetSize.getWidth() + "x" + targetSize.getHeight();
/*     */   }
/*     */ 
/*     */   public static Comparator<String> createFuzzyKeyComparator() {
/*  50 */     return new Comparator()
/*     */     {
/*     */       public int compare(String key1, String key2) {
/*  53 */         String imageUri1 = key1.substring(0, key1.lastIndexOf("_"));
/*  54 */         String imageUri2 = key2.substring(0, key2.lastIndexOf("_"));
/*  55 */         return imageUri1.compareTo(imageUri2);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static List<Bitmap> findCachedBitmapsForImageUri(String imageUri, MemoryCache memoryCache)
/*     */   {
/*  67 */     List values = new ArrayList();
/*  68 */     for (String key : memoryCache.keys()) {
/*  69 */       if (key.startsWith(imageUri)) {
/*  70 */         values.add(memoryCache.get(key));
/*     */       }
/*     */     }
/*  73 */     return values;
/*     */   }
/*     */ 
/*     */   public static List<String> findCacheKeysForImageUri(String imageUri, MemoryCache memoryCache)
/*     */   {
/*  83 */     List values = new ArrayList();
/*  84 */     for (String key : memoryCache.keys()) {
/*  85 */       if (key.startsWith(imageUri)) {
/*  86 */         values.add(key);
/*     */       }
/*     */     }
/*  89 */     return values;
/*     */   }
/*     */ 
/*     */   public static void removeFromCache(String imageUri, MemoryCache memoryCache)
/*     */   {
/*  99 */     List keysToRemove = new ArrayList();
/* 100 */     for (String key : memoryCache.keys()) {
/* 101 */       if (key.startsWith(imageUri)) {
/* 102 */         keysToRemove.add(key);
/*     */       }
/*     */     }
/* 105 */     for (String keyToRemove : keysToRemove)
/* 106 */       memoryCache.remove(keyToRemove);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.utils.MemoryCacheUtils
 * JD-Core Version:    0.6.0
 */
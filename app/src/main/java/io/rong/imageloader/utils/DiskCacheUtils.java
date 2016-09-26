/*    */ package io.rong.imageloader.utils;
/*    */ 
/*    */ import io.rong.imageloader.cache.disc.DiskCache;
/*    */ import java.io.File;
/*    */ 
/*    */ public final class DiskCacheUtils
/*    */ {
/*    */   public static File findInCache(String imageUri, DiskCache diskCache)
/*    */   {
/* 36 */     File image = diskCache.get(imageUri);
/* 37 */     return (image != null) && (image.exists()) ? image : null;
/*    */   }
/*    */ 
/*    */   public static boolean removeFromCache(String imageUri, DiskCache diskCache)
/*    */   {
/* 46 */     File image = diskCache.get(imageUri);
/* 47 */     return (image != null) && (image.exists()) && (image.delete());
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.utils.DiskCacheUtils
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.imageloader.cache.disc.impl;
/*    */ 
/*    */ import io.rong.imageloader.cache.disc.naming.FileNameGenerator;
/*    */ import java.io.File;
/*    */ 
/*    */ public class UnlimitedDiskCache extends BaseDiskCache
/*    */ {
/*    */   public UnlimitedDiskCache(File cacheDir)
/*    */   {
/* 32 */     super(cacheDir);
/*    */   }
/*    */ 
/*    */   public UnlimitedDiskCache(File cacheDir, File reserveCacheDir)
/*    */   {
/* 40 */     super(cacheDir, reserveCacheDir);
/*    */   }
/*    */ 
/*    */   public UnlimitedDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator)
/*    */   {
/* 50 */     super(cacheDir, reserveCacheDir, fileNameGenerator);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.disc.impl.UnlimitedDiskCache
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.imageloader.cache.disc.impl;
/*     */ 
/*     */ import android.graphics.Bitmap;
/*     */ import io.rong.imageloader.cache.disc.naming.FileNameGenerator;
/*     */ import io.rong.imageloader.core.DefaultConfigurationFactory;
/*     */ import io.rong.imageloader.utils.IoUtils.CopyListener;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class LimitedAgeDiskCache extends BaseDiskCache
/*     */ {
/*     */   private final long maxFileAge;
/*  40 */   private final Map<File, Long> loadingDates = Collections.synchronizedMap(new HashMap());
/*     */ 
/*     */   public LimitedAgeDiskCache(File cacheDir, long maxAge)
/*     */   {
/*  48 */     this(cacheDir, null, DefaultConfigurationFactory.createFileNameGenerator(), maxAge);
/*     */   }
/*     */ 
/*     */   public LimitedAgeDiskCache(File cacheDir, File reserveCacheDir, long maxAge)
/*     */   {
/*  57 */     this(cacheDir, reserveCacheDir, DefaultConfigurationFactory.createFileNameGenerator(), maxAge);
/*     */   }
/*     */ 
/*     */   public LimitedAgeDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator, long maxAge)
/*     */   {
/*  68 */     super(cacheDir, reserveCacheDir, fileNameGenerator);
/*  69 */     this.maxFileAge = (maxAge * 1000L);
/*     */   }
/*     */ 
/*     */   public File get(String imageUri)
/*     */   {
/*  74 */     File file = super.get(imageUri);
/*  75 */     if ((file != null) && (file.exists()))
/*     */     {
/*  77 */       Long loadingDate = (Long)this.loadingDates.get(file);
/*     */       boolean cached;
/*  78 */       if (loadingDate == null) {
/*  79 */         boolean cached = false;
/*  80 */         loadingDate = Long.valueOf(file.lastModified());
/*     */       } else {
/*  82 */         cached = true;
/*     */       }
/*     */ 
/*  85 */       if (System.currentTimeMillis() - loadingDate.longValue() > this.maxFileAge) {
/*  86 */         file.delete();
/*  87 */         this.loadingDates.remove(file);
/*  88 */       } else if (!cached) {
/*  89 */         this.loadingDates.put(file, loadingDate);
/*     */       }
/*     */     }
/*  92 */     return file;
/*     */   }
/*     */ 
/*     */   public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException
/*     */   {
/*  97 */     boolean saved = super.save(imageUri, imageStream, listener);
/*  98 */     rememberUsage(imageUri);
/*  99 */     return saved;
/*     */   }
/*     */ 
/*     */   public boolean save(String imageUri, Bitmap bitmap) throws IOException
/*     */   {
/* 104 */     boolean saved = super.save(imageUri, bitmap);
/* 105 */     rememberUsage(imageUri);
/* 106 */     return saved;
/*     */   }
/*     */ 
/*     */   public boolean remove(String imageUri)
/*     */   {
/* 111 */     this.loadingDates.remove(getFile(imageUri));
/* 112 */     return super.remove(imageUri);
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 117 */     super.clear();
/* 118 */     this.loadingDates.clear();
/*     */   }
/*     */ 
/*     */   private void rememberUsage(String imageUri) {
/* 122 */     File file = getFile(imageUri);
/* 123 */     long currentTime = System.currentTimeMillis();
/* 124 */     file.setLastModified(currentTime);
/* 125 */     this.loadingDates.put(file, Long.valueOf(currentTime));
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.disc.impl.LimitedAgeDiskCache
 * JD-Core Version:    0.6.0
 */
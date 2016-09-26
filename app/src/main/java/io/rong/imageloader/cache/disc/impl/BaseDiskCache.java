/*     */ package io.rong.imageloader.cache.disc.impl;
/*     */ 
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.Bitmap.CompressFormat;
/*     */ import io.rong.imageloader.cache.disc.DiskCache;
/*     */ import io.rong.imageloader.cache.disc.naming.FileNameGenerator;
/*     */ import io.rong.imageloader.core.DefaultConfigurationFactory;
/*     */ import io.rong.imageloader.utils.IoUtils;
/*     */ import io.rong.imageloader.utils.IoUtils.CopyListener;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public abstract class BaseDiskCache
/*     */   implements DiskCache
/*     */ {
/*     */   public static final int DEFAULT_BUFFER_SIZE = 32768;
/*  42 */   public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
/*     */   public static final int DEFAULT_COMPRESS_QUALITY = 100;
/*     */   private static final String ERROR_ARG_NULL = " argument must be not null";
/*     */   private static final String TEMP_IMAGE_POSTFIX = ".tmp";
/*     */   protected final File cacheDir;
/*     */   protected final File reserveCacheDir;
/*     */   protected final FileNameGenerator fileNameGenerator;
/*  54 */   protected int bufferSize = 32768;
/*     */ 
/*  56 */   protected Bitmap.CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
/*  57 */   protected int compressQuality = 100;
/*     */ 
/*     */   public BaseDiskCache(File cacheDir)
/*     */   {
/*  61 */     this(cacheDir, null);
/*     */   }
/*     */ 
/*     */   public BaseDiskCache(File cacheDir, File reserveCacheDir)
/*     */   {
/*  69 */     this(cacheDir, reserveCacheDir, DefaultConfigurationFactory.createFileNameGenerator());
/*     */   }
/*     */ 
/*     */   public BaseDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator)
/*     */   {
/*  79 */     if (cacheDir == null) {
/*  80 */       throw new IllegalArgumentException("cacheDir argument must be not null");
/*     */     }
/*  82 */     if (fileNameGenerator == null) {
/*  83 */       throw new IllegalArgumentException("fileNameGenerator argument must be not null");
/*     */     }
/*     */ 
/*  86 */     this.cacheDir = cacheDir;
/*  87 */     this.reserveCacheDir = reserveCacheDir;
/*  88 */     this.fileNameGenerator = fileNameGenerator;
/*     */   }
/*     */ 
/*     */   public File getDirectory()
/*     */   {
/*  93 */     return this.cacheDir;
/*     */   }
/*     */ 
/*     */   public File get(String imageUri)
/*     */   {
/*  98 */     return getFile(imageUri);
/*     */   }
/*     */ 
/*     */   public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException
/*     */   {
/* 103 */     File imageFile = getFile(imageUri);
/* 104 */     File tmpFile = new File(imageFile.getAbsolutePath() + ".tmp");
/* 105 */     boolean loaded = false;
/*     */     try {
/* 107 */       OutputStream os = new BufferedOutputStream(new FileOutputStream(tmpFile), this.bufferSize);
/*     */       try {
/* 109 */         loaded = IoUtils.copyStream(imageStream, os, listener, this.bufferSize);
/*     */       } finally {
/* 111 */         IoUtils.closeSilently(os);
/*     */       }
/*     */     } finally {
/* 114 */       if ((loaded) && (!tmpFile.renameTo(imageFile))) {
/* 115 */         loaded = false;
/*     */       }
/* 117 */       if (!loaded) {
/* 118 */         tmpFile.delete();
/*     */       }
/*     */     }
/* 121 */     return loaded;
/*     */   }
/*     */ 
/*     */   public boolean save(String imageUri, Bitmap bitmap) throws IOException
/*     */   {
/* 126 */     File imageFile = getFile(imageUri);
/* 127 */     File tmpFile = new File(imageFile.getAbsolutePath() + ".tmp");
/* 128 */     OutputStream os = new BufferedOutputStream(new FileOutputStream(tmpFile), this.bufferSize);
/* 129 */     boolean savedSuccessfully = false;
/*     */     try {
/* 131 */       savedSuccessfully = bitmap.compress(this.compressFormat, this.compressQuality, os);
/*     */     } finally {
/* 133 */       IoUtils.closeSilently(os);
/* 134 */       if ((savedSuccessfully) && (!tmpFile.renameTo(imageFile))) {
/* 135 */         savedSuccessfully = false;
/*     */       }
/* 137 */       if (!savedSuccessfully) {
/* 138 */         tmpFile.delete();
/*     */       }
/*     */     }
/* 141 */     bitmap.recycle();
/* 142 */     return savedSuccessfully;
/*     */   }
/*     */ 
/*     */   public boolean remove(String imageUri)
/*     */   {
/* 147 */     return getFile(imageUri).delete();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 157 */     File[] files = this.cacheDir.listFiles();
/* 158 */     if (files != null)
/* 159 */       for (File f : files)
/* 160 */         f.delete();
/*     */   }
/*     */ 
/*     */   protected File getFile(String imageUri)
/*     */   {
/* 167 */     String fileName = this.fileNameGenerator.generate(imageUri);
/* 168 */     File dir = this.cacheDir;
/* 169 */     if ((!this.cacheDir.exists()) && (!this.cacheDir.mkdirs()) && 
/* 170 */       (this.reserveCacheDir != null) && (
/* 170 */       (this.reserveCacheDir.exists()) || (this.reserveCacheDir.mkdirs()))) {
/* 171 */       dir = this.reserveCacheDir;
/*     */     }
/*     */ 
/* 174 */     return new File(dir, fileName);
/*     */   }
/*     */ 
/*     */   public void setBufferSize(int bufferSize) {
/* 178 */     this.bufferSize = bufferSize;
/*     */   }
/*     */ 
/*     */   public void setCompressFormat(Bitmap.CompressFormat compressFormat) {
/* 182 */     this.compressFormat = compressFormat;
/*     */   }
/*     */ 
/*     */   public void setCompressQuality(int compressQuality) {
/* 186 */     this.compressQuality = compressQuality;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.disc.impl.BaseDiskCache
 * JD-Core Version:    0.6.0
 */
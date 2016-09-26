/*     */ package io.rong.imageloader.core;
/*     */ 
/*     */ import android.annotation.TargetApi;
/*     */ import android.app.ActivityManager;
/*     */ import android.content.Context;
/*     */ import android.content.pm.ApplicationInfo;
/*     */ import android.os.Build.VERSION;
/*     */ import io.rong.imageloader.cache.disc.DiskCache;
/*     */ import io.rong.imageloader.cache.disc.impl.UnlimitedDiskCache;
/*     */ import io.rong.imageloader.cache.disc.impl.ext.LruDiskCache;
/*     */ import io.rong.imageloader.cache.disc.naming.FileNameGenerator;
/*     */ import io.rong.imageloader.cache.disc.naming.HashCodeFileNameGenerator;
/*     */ import io.rong.imageloader.cache.memory.MemoryCache;
/*     */ import io.rong.imageloader.cache.memory.impl.LruMemoryCache;
/*     */ import io.rong.imageloader.core.assist.QueueProcessingType;
/*     */ import io.rong.imageloader.core.assist.deque.LIFOLinkedBlockingDeque;
/*     */ import io.rong.imageloader.core.decode.BaseImageDecoder;
/*     */ import io.rong.imageloader.core.decode.ImageDecoder;
/*     */ import io.rong.imageloader.core.display.BitmapDisplayer;
/*     */ import io.rong.imageloader.core.display.SimpleBitmapDisplayer;
/*     */ import io.rong.imageloader.core.download.BaseImageDownloader;
/*     */ import io.rong.imageloader.core.download.ImageDownloader;
/*     */ import io.rong.imageloader.utils.L;
/*     */ import io.rong.imageloader.utils.StorageUtils;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ 
/*     */ public class DefaultConfigurationFactory
/*     */ {
/*     */   public static Executor createExecutor(int threadPoolSize, int threadPriority, QueueProcessingType tasksProcessingType)
/*     */   {
/*  64 */     boolean lifo = tasksProcessingType == QueueProcessingType.LIFO;
/*  65 */     BlockingQueue taskQueue = (BlockingQueue)(lifo ? new LIFOLinkedBlockingDeque() : new LinkedBlockingQueue());
/*     */ 
/*  67 */     return new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS, taskQueue, createThreadFactory(threadPriority, "uil-pool-"));
/*     */   }
/*     */ 
/*     */   public static Executor createTaskDistributor()
/*     */   {
/*  73 */     return Executors.newCachedThreadPool(createThreadFactory(5, "uil-pool-d-"));
/*     */   }
/*     */ 
/*     */   public static FileNameGenerator createFileNameGenerator()
/*     */   {
/*  78 */     return new HashCodeFileNameGenerator();
/*     */   }
/*     */ 
/*     */   public static DiskCache createDiskCache(Context context, FileNameGenerator diskCacheFileNameGenerator, long diskCacheSize, int diskCacheFileCount)
/*     */   {
/*  86 */     File reserveCacheDir = createReserveDiskCacheDir(context);
/*  87 */     if ((diskCacheSize > 0L) || (diskCacheFileCount > 0)) {
/*  88 */       File individualCacheDir = StorageUtils.getIndividualCacheDirectory(context);
/*     */       try {
/*  90 */         return new LruDiskCache(individualCacheDir, reserveCacheDir, diskCacheFileNameGenerator, diskCacheSize, diskCacheFileCount);
/*     */       }
/*     */       catch (IOException e) {
/*  93 */         L.e(e);
/*     */       }
/*     */     }
/*     */ 
/*  97 */     File cacheDir = StorageUtils.getCacheDirectory(context);
/*  98 */     return new UnlimitedDiskCache(cacheDir, reserveCacheDir, diskCacheFileNameGenerator);
/*     */   }
/*     */ 
/*     */   private static File createReserveDiskCacheDir(Context context)
/*     */   {
/* 103 */     File cacheDir = StorageUtils.getCacheDirectory(context, false);
/* 104 */     File individualDir = new File(cacheDir, "uil-images");
/* 105 */     if ((individualDir.exists()) || (individualDir.mkdir())) {
/* 106 */       cacheDir = individualDir;
/*     */     }
/* 108 */     return cacheDir;
/*     */   }
/*     */ 
/*     */   public static MemoryCache createMemoryCache(Context context, int memoryCacheSize)
/*     */   {
/* 116 */     if (memoryCacheSize == 0) {
/* 117 */       ActivityManager am = (ActivityManager)context.getSystemService("activity");
/* 118 */       int memoryClass = am.getMemoryClass();
/* 119 */       if ((hasHoneycomb()) && (isLargeHeap(context))) {
/* 120 */         memoryClass = getLargeMemoryClass(am);
/*     */       }
/* 122 */       memoryCacheSize = 1048576 * memoryClass / 8;
/*     */     }
/* 124 */     return new LruMemoryCache(memoryCacheSize);
/*     */   }
/*     */ 
/*     */   private static boolean hasHoneycomb() {
/* 128 */     return Build.VERSION.SDK_INT >= 11;
/*     */   }
/*     */   @TargetApi(11)
/*     */   private static boolean isLargeHeap(Context context) {
/* 133 */     return (context.getApplicationInfo().flags & 0x100000) != 0;
/*     */   }
/*     */   @TargetApi(11)
/*     */   private static int getLargeMemoryClass(ActivityManager am) {
/* 138 */     return am.getLargeMemoryClass();
/*     */   }
/*     */ 
/*     */   public static ImageDownloader createImageDownloader(Context context)
/*     */   {
/* 143 */     return new BaseImageDownloader(context);
/*     */   }
/*     */ 
/*     */   public static ImageDecoder createImageDecoder(boolean loggingEnabled)
/*     */   {
/* 148 */     return new BaseImageDecoder(loggingEnabled);
/*     */   }
/*     */ 
/*     */   public static BitmapDisplayer createBitmapDisplayer()
/*     */   {
/* 153 */     return new SimpleBitmapDisplayer();
/*     */   }
/*     */ 
/*     */   private static ThreadFactory createThreadFactory(int threadPriority, String threadNamePrefix)
/*     */   {
/* 158 */     return new DefaultThreadFactory(threadPriority, threadNamePrefix);
/*     */   }
/* 163 */   private static class DefaultThreadFactory implements ThreadFactory { private static final AtomicInteger poolNumber = new AtomicInteger(1);
/*     */     private final ThreadGroup group;
/* 166 */     private final AtomicInteger threadNumber = new AtomicInteger(1);
/*     */     private final String namePrefix;
/*     */     private final int threadPriority;
/*     */ 
/* 171 */     DefaultThreadFactory(int threadPriority, String threadNamePrefix) { this.threadPriority = threadPriority;
/* 172 */       this.group = Thread.currentThread().getThreadGroup();
/* 173 */       this.namePrefix = (threadNamePrefix + poolNumber.getAndIncrement() + "-thread-");
/*     */     }
/*     */ 
/*     */     public Thread newThread(Runnable r)
/*     */     {
/* 178 */       Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
/* 179 */       if (t.isDaemon()) t.setDaemon(false);
/* 180 */       t.setPriority(this.threadPriority);
/* 181 */       return t;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.DefaultConfigurationFactory
 * JD-Core Version:    0.6.0
 */
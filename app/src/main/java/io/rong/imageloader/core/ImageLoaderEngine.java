/*     */ package io.rong.imageloader.core;
/*     */ 
/*     */ import io.rong.imageloader.cache.disc.DiskCache;
/*     */ import io.rong.imageloader.core.imageaware.ImageAware;
/*     */ import java.io.File;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ 
/*     */ class ImageLoaderEngine
/*     */ {
/*     */   final ImageLoaderConfiguration configuration;
/*     */   private Executor taskExecutor;
/*     */   private Executor taskExecutorForCachedImages;
/*     */   private Executor taskDistributor;
/*  48 */   private final Map<Integer, String> cacheKeysForImageAwares = Collections.synchronizedMap(new HashMap());
/*     */ 
/*  50 */   private final Map<String, ReentrantLock> uriLocks = new WeakHashMap();
/*     */ 
/*  52 */   private final AtomicBoolean paused = new AtomicBoolean(false);
/*  53 */   private final AtomicBoolean networkDenied = new AtomicBoolean(false);
/*  54 */   private final AtomicBoolean slowNetwork = new AtomicBoolean(false);
/*     */ 
/*  56 */   private final Object pauseLock = new Object();
/*     */ 
/*     */   ImageLoaderEngine(ImageLoaderConfiguration configuration) {
/*  59 */     this.configuration = configuration;
/*     */ 
/*  61 */     this.taskExecutor = configuration.taskExecutor;
/*  62 */     this.taskExecutorForCachedImages = configuration.taskExecutorForCachedImages;
/*     */ 
/*  64 */     this.taskDistributor = DefaultConfigurationFactory.createTaskDistributor();
/*     */   }
/*     */ 
/*     */   void submit(LoadAndDisplayImageTask task)
/*     */   {
/*  69 */     this.taskDistributor.execute(new Runnable(task)
/*     */     {
/*     */       public void run() {
/*  72 */         File image = ImageLoaderEngine.this.configuration.diskCache.get(this.val$task.getLoadingUri());
/*  73 */         boolean isImageCachedOnDisk = (image != null) && (image.exists());
/*  74 */         ImageLoaderEngine.this.initExecutorsIfNeed();
/*  75 */         if (isImageCachedOnDisk)
/*  76 */           ImageLoaderEngine.this.taskExecutorForCachedImages.execute(this.val$task);
/*     */         else
/*  78 */           ImageLoaderEngine.this.taskExecutor.execute(this.val$task);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   void submit(ProcessAndDisplayImageTask task)
/*     */   {
/*  86 */     initExecutorsIfNeed();
/*  87 */     this.taskExecutorForCachedImages.execute(task);
/*     */   }
/*     */ 
/*     */   private void initExecutorsIfNeed() {
/*  91 */     if ((!this.configuration.customExecutor) && (((ExecutorService)this.taskExecutor).isShutdown())) {
/*  92 */       this.taskExecutor = createTaskExecutor();
/*     */     }
/*  94 */     if ((!this.configuration.customExecutorForCachedImages) && (((ExecutorService)this.taskExecutorForCachedImages).isShutdown()))
/*     */     {
/*  96 */       this.taskExecutorForCachedImages = createTaskExecutor();
/*     */     }
/*     */   }
/*     */ 
/*     */   private Executor createTaskExecutor() {
/* 101 */     return DefaultConfigurationFactory.createExecutor(this.configuration.threadPoolSize, this.configuration.threadPriority, this.configuration.tasksProcessingType);
/*     */   }
/*     */ 
/*     */   String getLoadingUriForView(ImageAware imageAware)
/*     */   {
/* 110 */     return (String)this.cacheKeysForImageAwares.get(Integer.valueOf(imageAware.getId()));
/*     */   }
/*     */ 
/*     */   void prepareDisplayTaskFor(ImageAware imageAware, String memoryCacheKey)
/*     */   {
/* 118 */     this.cacheKeysForImageAwares.put(Integer.valueOf(imageAware.getId()), memoryCacheKey);
/*     */   }
/*     */ 
/*     */   void cancelDisplayTaskFor(ImageAware imageAware)
/*     */   {
/* 128 */     this.cacheKeysForImageAwares.remove(Integer.valueOf(imageAware.getId()));
/*     */   }
/*     */ 
/*     */   void denyNetworkDownloads(boolean denyNetworkDownloads)
/*     */   {
/* 140 */     this.networkDenied.set(denyNetworkDownloads);
/*     */   }
/*     */ 
/*     */   void handleSlowNetwork(boolean handleSlowNetwork)
/*     */   {
/* 151 */     this.slowNetwork.set(handleSlowNetwork);
/*     */   }
/*     */ 
/*     */   void pause()
/*     */   {
/* 159 */     this.paused.set(true);
/*     */   }
/*     */ 
/*     */   void resume()
/*     */   {
/* 164 */     this.paused.set(false);
/* 165 */     synchronized (this.pauseLock) {
/* 166 */       this.pauseLock.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   void stop()
/*     */   {
/* 178 */     if (!this.configuration.customExecutor) {
/* 179 */       ((ExecutorService)this.taskExecutor).shutdownNow();
/*     */     }
/* 181 */     if (!this.configuration.customExecutorForCachedImages) {
/* 182 */       ((ExecutorService)this.taskExecutorForCachedImages).shutdownNow();
/*     */     }
/*     */ 
/* 185 */     this.cacheKeysForImageAwares.clear();
/* 186 */     this.uriLocks.clear();
/*     */   }
/*     */ 
/*     */   void fireCallback(Runnable r) {
/* 190 */     this.taskDistributor.execute(r);
/*     */   }
/*     */ 
/*     */   ReentrantLock getLockForUri(String uri) {
/* 194 */     ReentrantLock lock = (ReentrantLock)this.uriLocks.get(uri);
/* 195 */     if (lock == null) {
/* 196 */       lock = new ReentrantLock();
/* 197 */       this.uriLocks.put(uri, lock);
/*     */     }
/* 199 */     return lock;
/*     */   }
/*     */ 
/*     */   AtomicBoolean getPause() {
/* 203 */     return this.paused;
/*     */   }
/*     */ 
/*     */   Object getPauseLock() {
/* 207 */     return this.pauseLock;
/*     */   }
/*     */ 
/*     */   boolean isNetworkDenied() {
/* 211 */     return this.networkDenied.get();
/*     */   }
/*     */ 
/*     */   boolean isSlowNetwork() {
/* 215 */     return this.slowNetwork.get();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.ImageLoaderEngine
 * JD-Core Version:    0.6.0
 */
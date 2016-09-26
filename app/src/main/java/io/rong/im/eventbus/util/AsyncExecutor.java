/*     */ package io.rong.eventbus.util;
/*     */ 
/*     */ import android.app.Activity;
/*     */ import android.util.Log;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.Executors;
/*     */ 
/*     */ public class AsyncExecutor
/*     */ {
/*     */   private final Executor threadPool;
/*     */   private final Constructor<?> failureEventConstructor;
/*     */   private final EventBus eventBus;
/*     */   private final Object scope;
/*     */ 
/*     */   public static Builder builder()
/*     */   {
/*  85 */     return new Builder(null);
/*     */   }
/*     */ 
/*     */   public static AsyncExecutor create() {
/*  89 */     return new Builder(null).build();
/*     */   }
/*     */ 
/*     */   private AsyncExecutor(Executor threadPool, EventBus eventBus, Class<?> failureEventType, Object scope)
/*     */   {
/*  98 */     this.threadPool = threadPool;
/*  99 */     this.eventBus = eventBus;
/* 100 */     this.scope = scope;
/*     */     try {
/* 102 */       this.failureEventConstructor = failureEventType.getConstructor(new Class[] { Throwable.class });
/*     */     } catch (NoSuchMethodException e) {
/* 104 */       throw new RuntimeException("Failure event class must have a constructor with one parameter of type Throwable", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void execute(RunnableEx runnable)
/*     */   {
/* 111 */     this.threadPool.execute(new Object(runnable)
/*     */     {
/*     */       public void run() {
/*     */         try {
/* 115 */           this.val$runnable.run(); } catch (Exception e) {
/*     */           Object event;
/*     */           try {
/* 119 */             event = AsyncExecutor.this.failureEventConstructor.newInstance(new Object[] { e });
/*     */           } catch (Exception e1) {
/* 121 */             Log.e(EventBus.TAG, "Original exception:", e);
/* 122 */             throw new RuntimeException("Could not create failure event", e1);
/*     */           }
/* 124 */           if ((event instanceof HasExecutionScope)) {
/* 125 */             ((HasExecutionScope)event).setExecutionScope(AsyncExecutor.this.scope);
/*     */           }
/* 127 */           AsyncExecutor.this.eventBus.post(event);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static abstract interface RunnableEx
/*     */   {
/*     */     public abstract void run()
/*     */       throws Exception;
/*     */   }
/*     */ 
/*     */   public static class Builder
/*     */   {
/*     */     private Executor threadPool;
/*     */     private Class<?> failureEventType;
/*     */     private EventBus eventBus;
/*     */ 
/*     */     public Builder threadPool(Executor threadPool)
/*     */     {
/*  43 */       this.threadPool = threadPool;
/*  44 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder failureEventType(Class<?> failureEventType) {
/*  48 */       this.failureEventType = failureEventType;
/*  49 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder eventBus(EventBus eventBus) {
/*  53 */       this.eventBus = eventBus;
/*  54 */       return this;
/*     */     }
/*     */ 
/*     */     public AsyncExecutor build() {
/*  58 */       return buildForScope(null);
/*     */     }
/*     */ 
/*     */     public AsyncExecutor buildForActivityScope(Activity activity) {
/*  62 */       return buildForScope(activity.getClass());
/*     */     }
/*     */ 
/*     */     public AsyncExecutor buildForScope(Object executionContext) {
/*  66 */       if (this.eventBus == null) {
/*  67 */         this.eventBus = EventBus.getDefault();
/*     */       }
/*  69 */       if (this.threadPool == null) {
/*  70 */         this.threadPool = Executors.newCachedThreadPool();
/*     */       }
/*  72 */       if (this.failureEventType == null) {
/*  73 */         this.failureEventType = ThrowableFailureEvent.class;
/*     */       }
/*  75 */       return new AsyncExecutor(this.threadPool, this.eventBus, this.failureEventType, executionContext, null);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.util.AsyncExecutor
 * JD-Core Version:    0.6.0
 */
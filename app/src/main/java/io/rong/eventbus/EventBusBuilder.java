/*     */ package io.rong.eventbus;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ 
/*     */ public class EventBusBuilder
/*     */ {
/*  28 */   private static final ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newCachedThreadPool();
/*     */ 
/*  30 */   boolean logSubscriberExceptions = true;
/*  31 */   boolean logNoSubscriberMessages = true;
/*  32 */   boolean sendSubscriberExceptionEvent = true;
/*  33 */   boolean sendNoSubscriberEvent = true;
/*     */   boolean throwSubscriberException;
/*  35 */   boolean eventInheritance = true;
/*  36 */   ExecutorService executorService = DEFAULT_EXECUTOR_SERVICE;
/*     */   List<Class<?>> skipMethodVerificationForClasses;
/*     */ 
/*     */   public EventBusBuilder logSubscriberExceptions(boolean logSubscriberExceptions)
/*     */   {
/*  44 */     this.logSubscriberExceptions = logSubscriberExceptions;
/*  45 */     return this;
/*     */   }
/*     */ 
/*     */   public EventBusBuilder logNoSubscriberMessages(boolean logNoSubscriberMessages)
/*     */   {
/*  50 */     this.logNoSubscriberMessages = logNoSubscriberMessages;
/*  51 */     return this;
/*     */   }
/*     */ 
/*     */   public EventBusBuilder sendSubscriberExceptionEvent(boolean sendSubscriberExceptionEvent)
/*     */   {
/*  56 */     this.sendSubscriberExceptionEvent = sendSubscriberExceptionEvent;
/*  57 */     return this;
/*     */   }
/*     */ 
/*     */   public EventBusBuilder sendNoSubscriberEvent(boolean sendNoSubscriberEvent)
/*     */   {
/*  62 */     this.sendNoSubscriberEvent = sendNoSubscriberEvent;
/*  63 */     return this;
/*     */   }
/*     */ 
/*     */   public EventBusBuilder throwSubscriberException(boolean throwSubscriberException)
/*     */   {
/*  73 */     this.throwSubscriberException = throwSubscriberException;
/*  74 */     return this;
/*     */   }
/*     */ 
/*     */   public EventBusBuilder eventInheritance(boolean eventInheritance)
/*     */   {
/*  87 */     this.eventInheritance = eventInheritance;
/*  88 */     return this;
/*     */   }
/*     */ 
/*     */   public EventBusBuilder executorService(ExecutorService executorService)
/*     */   {
/*  97 */     this.executorService = executorService;
/*  98 */     return this;
/*     */   }
/*     */ 
/*     */   public EventBusBuilder skipMethodVerificationFor(Class<?> clazz)
/*     */   {
/* 107 */     if (this.skipMethodVerificationForClasses == null) {
/* 108 */       this.skipMethodVerificationForClasses = new ArrayList();
/*     */     }
/* 110 */     this.skipMethodVerificationForClasses.add(clazz);
/* 111 */     return this;
/*     */   }
/*     */ 
/*     */   public EventBus installDefaultEventBus()
/*     */   {
/* 121 */     synchronized (EventBus.class) {
/* 122 */       if (EventBus.defaultInstance != null) {
/* 123 */         throw new EventBusException("Default instance already exists. It may be only set once before it's used the first time to ensure consistent behavior.");
/*     */       }
/*     */ 
/* 126 */       EventBus.defaultInstance = build();
/* 127 */       return EventBus.defaultInstance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public EventBus build()
/*     */   {
/* 133 */     return new EventBus(this);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.EventBusBuilder
 * JD-Core Version:    0.6.0
 */
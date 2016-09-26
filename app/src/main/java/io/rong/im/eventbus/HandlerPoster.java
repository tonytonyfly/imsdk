/*    */ package io.rong.eventbus;
/*    */ 
/*    */ import android.os.Handler;
/*    */ import android.os.Looper;
/*    */ import android.os.Message;
/*    */ import android.os.SystemClock;
/*    */ 
/*    */ final class HandlerPoster extends Handler
/*    */ {
/*    */   private final PendingPostQueue queue;
/*    */   private final int maxMillisInsideHandleMessage;
/*    */   private final EventBus eventBus;
/*    */   private boolean handlerActive;
/*    */ 
/*    */   HandlerPoster(EventBus eventBus, Looper looper, int maxMillisInsideHandleMessage)
/*    */   {
/* 31 */     super(looper);
/* 32 */     this.eventBus = eventBus;
/* 33 */     this.maxMillisInsideHandleMessage = maxMillisInsideHandleMessage;
/* 34 */     this.queue = new PendingPostQueue();
/*    */   }
/*    */ 
/*    */   void enqueue(Subscription subscription, Object event) {
/* 38 */     PendingPost pendingPost = PendingPost.obtainPendingPost(subscription, event);
/* 39 */     synchronized (this) {
/* 40 */       this.queue.enqueue(pendingPost);
/* 41 */       if (!this.handlerActive) {
/* 42 */         this.handlerActive = true;
/* 43 */         if (!sendMessage(obtainMessage()))
/* 44 */           throw new EventBusException("Could not send handler message");
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public void handleMessage(Message msg)
/*    */   {
/* 52 */     boolean rescheduled = false;
/*    */     try {
/* 54 */       long started = SystemClock.uptimeMillis();
/*    */       while (true) {
/* 56 */         PendingPost pendingPost = this.queue.poll();
/* 57 */         if (pendingPost == null)
/* 58 */           synchronized (this)
/*    */           {
/* 60 */             pendingPost = this.queue.poll();
/* 61 */             if (pendingPost == null) {
/* 62 */               this.handlerActive = false;
/*    */ 
/* 78 */               this.handlerActive = rescheduled; return;
/*    */             }
/*    */           }
/* 67 */         this.eventBus.invokeSubscriber(pendingPost);
/* 68 */         long timeInMethod = SystemClock.uptimeMillis() - started;
/* 69 */         if (timeInMethod >= this.maxMillisInsideHandleMessage) {
/* 70 */           if (!sendMessage(obtainMessage())) {
/* 71 */             throw new EventBusException("Could not send handler message");
/* 73 */           }rescheduled = true;
/*    */           return;
/*    */         }
/*    */       }
/*    */     } finally {
/* 78 */       this.handlerActive = rescheduled; } throw localObject2;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.HandlerPoster
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.eventbus;
/*    */ 
/*    */ import android.util.Log;
/*    */ import java.util.concurrent.ExecutorService;
/*    */ 
/*    */ final class BackgroundPoster
/*    */   implements Runnable
/*    */ {
/*    */   private final PendingPostQueue queue;
/*    */   private final EventBus eventBus;
/*    */   private volatile boolean executorRunning;
/*    */ 
/*    */   BackgroundPoster(EventBus eventBus)
/*    */   {
/* 33 */     this.eventBus = eventBus;
/* 34 */     this.queue = new PendingPostQueue();
/*    */   }
/*    */ 
/*    */   public void enqueue(Subscription subscription, Object event) {
/* 38 */     PendingPost pendingPost = PendingPost.obtainPendingPost(subscription, event);
/* 39 */     synchronized (this) {
/* 40 */       this.queue.enqueue(pendingPost);
/* 41 */       if (!this.executorRunning) {
/* 42 */         this.executorRunning = true;
/* 43 */         this.eventBus.getExecutorService().execute(this);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/*    */     try
/*    */     {
/*    */       while (true) {
/* 53 */         PendingPost pendingPost = this.queue.poll(1000);
/* 54 */         if (pendingPost == null)
/* 55 */           synchronized (this)
/*    */           {
/* 57 */             pendingPost = this.queue.poll();
/* 58 */             if (pendingPost == null) {
/* 59 */               this.executorRunning = false;
/*    */ 
/* 70 */               this.executorRunning = false; return;
/*    */             }
/*    */           }
/* 64 */         this.eventBus.invokeSubscriber(pendingPost);
/*    */       }
/*    */     } catch (InterruptedException e) {
/* 67 */       Log.w("Event", Thread.currentThread().getName() + " was interruppted", e);
/*    */     }
/*    */     finally {
/* 70 */       this.executorRunning = false;
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.BackgroundPoster
 * JD-Core Version:    0.6.0
 */
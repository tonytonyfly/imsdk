/*    */ package io.rong.eventbus;
/*    */ 
/*    */ import java.util.concurrent.ExecutorService;
/*    */ 
/*    */ class AsyncPoster
/*    */   implements Runnable
/*    */ {
/*    */   private final PendingPostQueue queue;
/*    */   private final EventBus eventBus;
/*    */ 
/*    */   AsyncPoster(EventBus eventBus)
/*    */   {
/* 30 */     this.eventBus = eventBus;
/* 31 */     this.queue = new PendingPostQueue();
/*    */   }
/*    */ 
/*    */   public void enqueue(Subscription subscription, Object event) {
/* 35 */     PendingPost pendingPost = PendingPost.obtainPendingPost(subscription, event);
/* 36 */     this.queue.enqueue(pendingPost);
/* 37 */     this.eventBus.getExecutorService().execute(this);
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 42 */     PendingPost pendingPost = this.queue.poll();
/* 43 */     if (pendingPost == null) {
/* 44 */       throw new IllegalStateException("No pending post available");
/*    */     }
/* 46 */     this.eventBus.invokeSubscriber(pendingPost);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.AsyncPoster
 * JD-Core Version:    0.6.0
 */
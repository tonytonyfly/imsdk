/*    */ package io.rong.eventbus;
/*    */ 
/*    */ final class PendingPostQueue
/*    */ {
/*    */   private PendingPost head;
/*    */   private PendingPost tail;
/*    */ 
/*    */   synchronized void enqueue(PendingPost pendingPost)
/*    */   {
/*  8 */     if (pendingPost == null) {
/*  9 */       throw new NullPointerException("null cannot be enqueued");
/*    */     }
/* 11 */     if (this.tail != null) {
/* 12 */       this.tail.next = pendingPost;
/* 13 */       this.tail = pendingPost;
/* 14 */     } else if (this.head == null) {
/* 15 */       this.head = (this.tail = pendingPost);
/*    */     } else {
/* 17 */       throw new IllegalStateException("Head present, but no tail");
/*    */     }
/* 19 */     notifyAll();
/*    */   }
/*    */ 
/*    */   synchronized PendingPost poll() {
/* 23 */     PendingPost pendingPost = this.head;
/* 24 */     if (this.head != null) {
/* 25 */       this.head = this.head.next;
/* 26 */       if (this.head == null) {
/* 27 */         this.tail = null;
/*    */       }
/*    */     }
/* 30 */     return pendingPost;
/*    */   }
/*    */ 
/*    */   synchronized PendingPost poll(int maxMillisToWait) throws InterruptedException {
/* 34 */     if (this.head == null) {
/* 35 */       wait(maxMillisToWait);
/*    */     }
/* 37 */     return poll();
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.PendingPostQueue
 * JD-Core Version:    0.6.0
 */
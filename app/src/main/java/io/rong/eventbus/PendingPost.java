/*    */ package io.rong.eventbus;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ final class PendingPost
/*    */ {
/* 22 */   private static final List<PendingPost> pendingPostPool = new ArrayList();
/*    */   Object event;
/*    */   Subscription subscription;
/*    */   PendingPost next;
/*    */ 
/*    */   private PendingPost(Object event, Subscription subscription)
/*    */   {
/* 29 */     this.event = event;
/* 30 */     this.subscription = subscription;
/*    */   }
/*    */ 
/*    */   static PendingPost obtainPendingPost(Subscription subscription, Object event) {
/* 34 */     synchronized (pendingPostPool) {
/* 35 */       int size = pendingPostPool.size();
/* 36 */       if (size > 0) {
/* 37 */         PendingPost pendingPost = (PendingPost)pendingPostPool.remove(size - 1);
/* 38 */         pendingPost.event = event;
/* 39 */         pendingPost.subscription = subscription;
/* 40 */         pendingPost.next = null;
/* 41 */         return pendingPost;
/*    */       }
/*    */     }
/* 44 */     return new PendingPost(event, subscription);
/*    */   }
/*    */ 
/*    */   static void releasePendingPost(PendingPost pendingPost) {
/* 48 */     pendingPost.event = null;
/* 49 */     pendingPost.subscription = null;
/* 50 */     pendingPost.next = null;
/* 51 */     synchronized (pendingPostPool)
/*    */     {
/* 53 */       if (pendingPostPool.size() < 10000)
/* 54 */         pendingPostPool.add(pendingPost);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.PendingPost
 * JD-Core Version:    0.6.0
 */
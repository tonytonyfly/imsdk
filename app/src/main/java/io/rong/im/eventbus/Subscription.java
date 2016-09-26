/*    */ package io.rong.eventbus;
/*    */ 
/*    */ final class Subscription
/*    */ {
/*    */   final Object subscriber;
/*    */   final SubscriberMethod subscriberMethod;
/*    */   final int priority;
/*    */   volatile boolean active;
/*    */ 
/*    */   Subscription(Object subscriber, SubscriberMethod subscriberMethod, int priority)
/*    */   {
/* 29 */     this.subscriber = subscriber;
/* 30 */     this.subscriberMethod = subscriberMethod;
/* 31 */     this.priority = priority;
/* 32 */     this.active = true;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 37 */     if ((other instanceof Subscription)) {
/* 38 */       Subscription otherSubscription = (Subscription)other;
/* 39 */       return (this.subscriber == otherSubscription.subscriber) && (this.subscriberMethod.equals(otherSubscription.subscriberMethod));
/*    */     }
/*    */ 
/* 42 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 48 */     return this.subscriber.hashCode() + this.subscriberMethod.methodString.hashCode();
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.Subscription
 * JD-Core Version:    0.6.0
 */
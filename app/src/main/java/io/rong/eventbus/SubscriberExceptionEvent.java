/*    */ package io.rong.eventbus;
/*    */ 
/*    */ public final class SubscriberExceptionEvent
/*    */ {
/*    */   public final EventBus eventBus;
/*    */   public final Throwable throwable;
/*    */   public final Object causingEvent;
/*    */   public final Object causingSubscriber;
/*    */ 
/*    */   public SubscriberExceptionEvent(EventBus eventBus, Throwable throwable, Object causingEvent, Object causingSubscriber)
/*    */   {
/* 38 */     this.eventBus = eventBus;
/* 39 */     this.throwable = throwable;
/* 40 */     this.causingEvent = causingEvent;
/* 41 */     this.causingSubscriber = causingSubscriber;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.SubscriberExceptionEvent
 * JD-Core Version:    0.6.0
 */
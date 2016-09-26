/*    */ package io.rong.eventbus;
/*    */ 
/*    */ public final class NoSubscriberEvent
/*    */ {
/*    */   public final EventBus eventBus;
/*    */   public final Object originalEvent;
/*    */ 
/*    */   public NoSubscriberEvent(EventBus eventBus, Object originalEvent)
/*    */   {
/* 31 */     this.eventBus = eventBus;
/* 32 */     this.originalEvent = originalEvent;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.NoSubscriberEvent
 * JD-Core Version:    0.6.0
 */
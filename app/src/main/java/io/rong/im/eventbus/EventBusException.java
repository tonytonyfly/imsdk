/*    */ package io.rong.eventbus;
/*    */ 
/*    */ public class EventBusException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = -2912559384646531479L;
/*    */ 
/*    */   public EventBusException(String detailMessage)
/*    */   {
/* 29 */     super(detailMessage);
/*    */   }
/*    */ 
/*    */   public EventBusException(Throwable throwable) {
/* 33 */     super(throwable);
/*    */   }
/*    */ 
/*    */   public EventBusException(String detailMessage, Throwable throwable) {
/* 37 */     super(detailMessage, throwable);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.EventBusException
 * JD-Core Version:    0.6.0
 */
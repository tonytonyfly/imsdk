/*    */ package io.rong.eventbus.util;
/*    */ 
/*    */ public class ThrowableFailureEvent
/*    */   implements HasExecutionScope
/*    */ {
/*    */   protected final Throwable throwable;
/*    */   protected final boolean suppressErrorUi;
/*    */   private Object executionContext;
/*    */ 
/*    */   public ThrowableFailureEvent(Throwable throwable)
/*    */   {
/* 28 */     this.throwable = throwable;
/* 29 */     this.suppressErrorUi = false;
/*    */   }
/*    */ 
/*    */   public ThrowableFailureEvent(Throwable throwable, boolean suppressErrorUi)
/*    */   {
/* 37 */     this.throwable = throwable;
/* 38 */     this.suppressErrorUi = suppressErrorUi;
/*    */   }
/*    */ 
/*    */   public Throwable getThrowable() {
/* 42 */     return this.throwable;
/*    */   }
/*    */ 
/*    */   public boolean isSuppressErrorUi() {
/* 46 */     return this.suppressErrorUi;
/*    */   }
/*    */ 
/*    */   public Object getExecutionScope() {
/* 50 */     return this.executionContext;
/*    */   }
/*    */ 
/*    */   public void setExecutionScope(Object executionContext) {
/* 54 */     this.executionContext = executionContext;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.util.ThrowableFailureEvent
 * JD-Core Version:    0.6.0
 */
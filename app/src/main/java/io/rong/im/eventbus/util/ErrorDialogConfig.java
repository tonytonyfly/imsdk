/*    */ package io.rong.eventbus.util;
/*    */ 
/*    */ import android.content.res.Resources;
/*    */ import android.util.Log;
/*    */ import io.rong.eventbus.EventBus;
/*    */ 
/*    */ public class ErrorDialogConfig
/*    */ {
/*    */   final Resources resources;
/*    */   final int defaultTitleId;
/*    */   final int defaultErrorMsgId;
/*    */   final ExceptionToResourceMapping mapping;
/*    */   EventBus eventBus;
/* 14 */   boolean logExceptions = true;
/*    */   String tagForLoggingExceptions;
/*    */   int defaultDialogIconId;
/*    */   Class<?> defaultEventTypeOnDialogClosed;
/*    */ 
/*    */   public ErrorDialogConfig(Resources resources, int defaultTitleId, int defaultMsgId)
/*    */   {
/* 20 */     this.resources = resources;
/* 21 */     this.defaultTitleId = defaultTitleId;
/* 22 */     this.defaultErrorMsgId = defaultMsgId;
/* 23 */     this.mapping = new ExceptionToResourceMapping();
/*    */   }
/*    */ 
/*    */   public ErrorDialogConfig addMapping(Class<? extends Throwable> clazz, int msgId) {
/* 27 */     this.mapping.addMapping(clazz, msgId);
/* 28 */     return this;
/*    */   }
/*    */ 
/*    */   public int getMessageIdForThrowable(Throwable throwable) {
/* 32 */     Integer resId = this.mapping.mapThrowable(throwable);
/* 33 */     if (resId != null) {
/* 34 */       return resId.intValue();
/*    */     }
/* 36 */     Log.d(EventBus.TAG, "No specific message ressource ID found for " + throwable);
/* 37 */     return this.defaultErrorMsgId;
/*    */   }
/*    */ 
/*    */   public void setDefaultDialogIconId(int defaultDialogIconId)
/*    */   {
/* 42 */     this.defaultDialogIconId = defaultDialogIconId;
/*    */   }
/*    */ 
/*    */   public void setDefaultEventTypeOnDialogClosed(Class<?> defaultEventTypeOnDialogClosed) {
/* 46 */     this.defaultEventTypeOnDialogClosed = defaultEventTypeOnDialogClosed;
/*    */   }
/*    */ 
/*    */   public void disableExceptionLogging() {
/* 50 */     this.logExceptions = false;
/*    */   }
/*    */ 
/*    */   public void setTagForLoggingExceptions(String tagForLoggingExceptions) {
/* 54 */     this.tagForLoggingExceptions = tagForLoggingExceptions;
/*    */   }
/*    */ 
/*    */   public void setEventBus(EventBus eventBus) {
/* 58 */     this.eventBus = eventBus;
/*    */   }
/*    */ 
/*    */   EventBus getEventBus()
/*    */   {
/* 63 */     return this.eventBus != null ? this.eventBus : EventBus.getDefault();
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.util.ErrorDialogConfig
 * JD-Core Version:    0.6.0
 */
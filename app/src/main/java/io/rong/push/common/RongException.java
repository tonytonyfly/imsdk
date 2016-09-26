/*    */ package io.rong.push.common;
/*    */ 
/*    */ public class RongException extends Exception
/*    */ {
/*    */   private Exception innerException;
/*    */   private int errorCode;
/*    */ 
/*    */   public RongException(String msg)
/*    */   {
/*  9 */     super(msg);
/*    */   }
/*    */ 
/*    */   public RongException(String msg, int errorCode) {
/* 13 */     super(msg);
/* 14 */     this.errorCode = errorCode;
/*    */   }
/*    */ 
/*    */   public RongException(String msg, Exception innerException) {
/* 18 */     super(msg);
/* 19 */     this.innerException = innerException;
/*    */   }
/*    */ 
/*    */   public int getErrorCode() {
/* 23 */     return this.errorCode;
/*    */   }
/*    */ 
/*    */   public Exception getInnerException() {
/* 27 */     return this.innerException;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.common.RongException
 * JD-Core Version:    0.6.0
 */
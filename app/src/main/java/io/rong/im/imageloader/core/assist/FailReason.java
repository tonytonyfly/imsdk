/*    */ package io.rong.imageloader.core.assist;
/*    */ 
/*    */ public class FailReason
/*    */ {
/*    */   private final FailType type;
/*    */   private final Throwable cause;
/*    */ 
/*    */   public FailReason(FailType type, Throwable cause)
/*    */   {
/* 31 */     this.type = type;
/* 32 */     this.cause = cause;
/*    */   }
/*    */ 
/*    */   public FailType getType()
/*    */   {
/* 37 */     return this.type;
/*    */   }
/*    */ 
/*    */   public Throwable getCause()
/*    */   {
/* 42 */     return this.cause;
/*    */   }
/*    */ 
/*    */   public static enum FailType
/*    */   {
/* 48 */     IO_ERROR, 
/*    */ 
/* 54 */     DECODING_ERROR, 
/*    */ 
/* 59 */     NETWORK_DENIED, 
/*    */ 
/* 61 */     OUT_OF_MEMORY, 
/*    */ 
/* 63 */     UNKNOWN;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.assist.FailReason
 * JD-Core Version:    0.6.0
 */
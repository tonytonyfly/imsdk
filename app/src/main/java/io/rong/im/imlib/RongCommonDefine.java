/*    */ package io.rong.imlib;
/*    */ 
/*    */ public abstract interface RongCommonDefine
/*    */ {
/*    */   public static enum GetMessageDirection
/*    */   {
/* 12 */     BEHIND(0), 
/*    */ 
/* 16 */     FRONT(1);
/*    */ 
/*    */     int value;
/*    */ 
/* 20 */     private GetMessageDirection(int v) { this.value = v; }
/*    */ 
/*    */     int getValue()
/*    */     {
/* 24 */       return this.value;
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.RongCommonDefine
 * JD-Core Version:    0.6.0
 */
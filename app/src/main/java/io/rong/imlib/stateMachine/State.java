/*    */ package io.rong.imlib.stateMachine;
/*    */ 
/*    */ import android.os.Message;
/*    */ 
/*    */ public class State
/*    */   implements IState
/*    */ {
/*    */   public void enter()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void exit()
/*    */   {
/*    */   }
/*    */ 
/*    */   public boolean processMessage(Message msg)
/*    */   {
/* 53 */     return false;
/*    */   }
/*    */ 
/*    */   public String getName()
/*    */   {
/* 70 */     String name = getClass().getName();
/* 71 */     int lastDollar = name.lastIndexOf('$');
/* 72 */     return name.substring(lastDollar + 1);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.stateMachine.State
 * JD-Core Version:    0.6.0
 */
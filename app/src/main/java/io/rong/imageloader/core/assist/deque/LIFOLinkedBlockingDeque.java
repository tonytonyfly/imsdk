/*    */ package io.rong.imageloader.core.assist.deque;
/*    */ 
/*    */ public class LIFOLinkedBlockingDeque<T> extends LinkedBlockingDeque<T>
/*    */ {
/*    */   private static final long serialVersionUID = -4114786347960826192L;
/*    */ 
/*    */   public boolean offer(T e)
/*    */   {
/* 32 */     return super.offerFirst(e);
/*    */   }
/*    */ 
/*    */   public T remove()
/*    */   {
/* 45 */     return super.removeFirst();
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.assist.deque.LIFOLinkedBlockingDeque
 * JD-Core Version:    0.6.0
 */
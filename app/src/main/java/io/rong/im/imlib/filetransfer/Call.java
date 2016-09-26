/*    */ package io.rong.imlib.filetransfer;
/*    */ 
/*    */ import java.util.concurrent.Future;
/*    */ 
/*    */ public class Call
/*    */ {
/*    */   private final Request request;
/*    */   private final CallDispatcher dispatcher;
/*    */ 
/*    */   private Call(CallDispatcher dispatcher, Request request)
/*    */   {
/* 11 */     this.request = request;
/* 12 */     this.dispatcher = dispatcher;
/*    */   }
/*    */ 
/*    */   public static Call create(CallDispatcher dispatcher, Request request) {
/* 16 */     return new Call(dispatcher, request);
/*    */   }
/*    */ 
/*    */   public void enqueue() {
/* 20 */     AsyncCall asyncCall = new AsyncCall();
/* 21 */     this.dispatcher.enqueue(asyncCall);
/*    */   }
/*    */   class AsyncCall implements Runnable {
/*    */     protected Future future;
/*    */ 
/*    */     AsyncCall() {  }
/*    */ 
/* 28 */     public Object tag() { return Call.this.request.tag; }
/*    */ 
/*    */     public void cancel(CancelCallback cancelCallback)
/*    */     {
/* 32 */       if ((this.future != null) && (!this.future.isDone())) {
/* 33 */         this.future.cancel(true);
/* 34 */         this.future = null;
/*    */       }
/* 36 */       cancelCallback.onCanceled(Call.this.request.tag);
/*    */     }
/*    */ 
/*    */     public void run()
/*    */     {
/* 41 */       Call.this.request.sendRequest();
/* 42 */       Call.this.dispatcher.finish(this);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.filetransfer.Call
 * JD-Core Version:    0.6.0
 */
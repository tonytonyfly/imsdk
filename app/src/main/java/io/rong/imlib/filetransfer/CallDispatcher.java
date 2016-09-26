/*    */ package io.rong.imlib.filetransfer;
/*    */ 
/*    */ import java.util.ArrayDeque;
/*    */ import java.util.Deque;
/*    */ import java.util.Iterator;
/*    */ import java.util.concurrent.ExecutorService;
/*    */ import java.util.concurrent.SynchronousQueue;
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import java.util.concurrent.ThreadPoolExecutor;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ public class CallDispatcher
/*    */ {
/*    */   private static final int MAX_RUNNING_TASK = 4;
/* 16 */   private final Deque<Call.AsyncCall> readyCalls = new ArrayDeque();
/* 17 */   private final Deque<Call.AsyncCall> runningCalls = new ArrayDeque();
/*    */   private ExecutorService executorService;
/*    */ 
/*    */   public synchronized ExecutorService getExecutorService()
/*    */   {
/* 21 */     if (this.executorService == null) {
/* 22 */       this.executorService = new ThreadPoolExecutor(4, 2147483647, 60L, TimeUnit.SECONDS, new SynchronousQueue(), threadFactory("HttpEngine Dispatcher", false));
/*    */     }
/*    */ 
/* 29 */     return this.executorService;
/*    */   }
/*    */ 
/*    */   private ThreadFactory threadFactory(String name, boolean daemon) {
/* 33 */     return new ThreadFactory(name, daemon)
/*    */     {
/*    */       public Thread newThread(Runnable runnable) {
/* 36 */         Thread result = new Thread(runnable, this.val$name);
/* 37 */         result.setDaemon(this.val$daemon);
/* 38 */         return result;
/*    */       } } ;
/*    */   }
/*    */ 
/*    */   public synchronized void enqueue(Call.AsyncCall asyncCall) {
/* 44 */     if (this.runningCalls.size() < 4) {
/* 45 */       asyncCall.future = getExecutorService().submit(asyncCall);
/* 46 */       this.runningCalls.add(asyncCall);
/*    */     } else {
/* 48 */       this.readyCalls.add(asyncCall);
/*    */     }
/*    */   }
/*    */ 
/*    */   public synchronized void cancel(Object tag, CancelCallback callback) {
/* 53 */     for (Call.AsyncCall call : this.readyCalls) {
/* 54 */       if (call.tag().equals(tag)) {
/* 55 */         this.readyCalls.remove(call);
/* 56 */         call.cancel(callback);
/* 57 */         return;
/*    */       }
/*    */     }
/*    */ 
/* 61 */     for (Call.AsyncCall call : this.runningCalls) {
/* 62 */       if (call.tag().equals(tag)) {
/* 63 */         call.cancel(callback);
/* 64 */         return;
/*    */       }
/*    */     }
/*    */ 
/* 68 */     callback.onError(-3);
/*    */   }
/*    */ 
/*    */   public synchronized void finish(Call.AsyncCall runnable) {
/* 72 */     if (!this.runningCalls.remove(runnable)) {
/* 73 */       throw new RuntimeException("Not in running list.");
/*    */     }
/* 75 */     promoteCalls();
/*    */   }
/*    */ 
/*    */   private void promoteCalls() {
/* 79 */     if ((this.runningCalls.size() >= 4) || (this.readyCalls.isEmpty())) {
/* 80 */       return;
/*    */     }
/*    */ 
/* 83 */     for (Iterator i = this.readyCalls.iterator(); i.hasNext(); ) {
/* 84 */       Call.AsyncCall call = (Call.AsyncCall)i.next();
/* 85 */       this.runningCalls.add(call);
/* 86 */       call.future = getExecutorService().submit(call);
/* 87 */       i.remove();
/* 88 */       if (this.runningCalls.size() >= 4)
/* 89 */         return;
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.filetransfer.CallDispatcher
 * JD-Core Version:    0.6.0
 */
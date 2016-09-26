/*     */ package io.rong.imkit.manager;
/*     */ 
/*     */ import android.support.annotation.Nullable;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.SendImageMessageCallback;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.imlib.model.Message.SentStatus;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.SynchronousQueue;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class sendMediaMessageManager
/*     */ {
/*     */   private static final String TAG = "sendMediaMessageManager";
/*     */   private ExecutorService executorService;
/*     */   private UploadController uploadController;
/*     */ 
/*     */   public static sendMediaMessageManager getInstance()
/*     */   {
/*  33 */     return SingletonHolder.sInstance;
/*     */   }
/*     */ 
/*     */   private sendMediaMessageManager() {
/*  37 */     this.executorService = getExecutorService();
/*  38 */     this.uploadController = new UploadController();
/*     */   }
/*     */ 
/*     */   public void sendMediaMessages(List<Message> messageList, boolean isFull)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void cancelSendingImages(Conversation.ConversationType conversationType, String targetId)
/*     */   {
/*  92 */     RLog.d("sendMediaMessageManager", "cancelSendingImages");
/*  93 */     if ((conversationType != null) && (targetId != null) && (this.uploadController != null))
/*  94 */       this.uploadController.cancel(conversationType, targetId);
/*     */   }
/*     */ 
/*     */   public void cancelSendingImage(Conversation.ConversationType conversationType, String targetId, int messageId) {
/*  98 */     RLog.d("sendMediaMessageManager", "cancelSendingImages");
/*  99 */     if ((conversationType != null) && (targetId != null) && (this.uploadController != null) && (messageId > 0))
/* 100 */       this.uploadController.cancel(conversationType, targetId, messageId);
/*     */   }
/*     */ 
/*     */   public void reset() {
/* 104 */     this.uploadController.reset();
/*     */   }
/*     */ 
/*     */   private ExecutorService getExecutorService()
/*     */   {
/* 212 */     if (this.executorService == null) {
/* 213 */       this.executorService = new ThreadPoolExecutor(1, 2147483647, 60L, TimeUnit.SECONDS, new SynchronousQueue(), threadFactory("Rong SendMediaManager", false));
/*     */     }
/*     */ 
/* 220 */     return this.executorService;
/*     */   }
/*     */ 
/*     */   private ThreadFactory threadFactory(String name, boolean daemon) {
/* 224 */     return new ThreadFactory(name, daemon)
/*     */     {
/*     */       public Thread newThread(@Nullable Runnable runnable) {
/* 227 */         Thread result = new Thread(runnable, this.val$name);
/* 228 */         result.setDaemon(this.val$daemon);
/* 229 */         return result;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private class UploadController
/*     */     implements Runnable
/*     */   {
/*     */     final List<Message> pendingMessages;
/*     */     Message executingMessage;
/*     */ 
/*     */     public UploadController()
/*     */     {
/* 112 */       this.pendingMessages = new ArrayList();
/*     */     }
/*     */ 
/*     */     public void execute(Message message) {
/* 116 */       synchronized (this.pendingMessages) {
/* 117 */         this.pendingMessages.add(message);
/* 118 */         if (this.executingMessage == null) {
/* 119 */           this.executingMessage = ((Message)this.pendingMessages.remove(0));
/* 120 */           sendMediaMessageManager.this.executorService.submit(this);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public void reset() {
/* 126 */       RLog.w("sendMediaMessageManager", "Rest Sending Images.");
/* 127 */       synchronized (this.pendingMessages) {
/* 128 */         for (Message message : this.pendingMessages) {
/* 129 */           message.setSentStatus(Message.SentStatus.FAILED);
/* 130 */           RongContext.getInstance().getEventBus().post(message);
/*     */         }
/* 132 */         this.pendingMessages.clear();
/*     */       }
/* 134 */       if (this.executingMessage != null) {
/* 135 */         this.executingMessage.setSentStatus(Message.SentStatus.FAILED);
/* 136 */         RongContext.getInstance().getEventBus().post(this.executingMessage);
/* 137 */         this.executingMessage = null;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void cancel(Conversation.ConversationType conversationType, String targetId) {
/* 142 */       synchronized (this.pendingMessages) {
/* 143 */         int count = this.pendingMessages.size();
/* 144 */         for (int i = 0; i < count; i++) {
/* 145 */           Message msg = (Message)this.pendingMessages.get(i);
/* 146 */           if ((msg.getConversationType().equals(conversationType)) && (msg.getTargetId().equals(targetId))) {
/* 147 */             this.pendingMessages.remove(msg);
/*     */           }
/*     */         }
/* 150 */         if (this.pendingMessages.size() == 0)
/* 151 */           this.executingMessage = null;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void cancel(Conversation.ConversationType conversationType, String targetId, int messageId) {
/* 156 */       synchronized (this.pendingMessages) {
/* 157 */         int count = this.pendingMessages.size();
/* 158 */         for (int i = 0; i < count; i++) {
/* 159 */           Message msg = (Message)this.pendingMessages.get(i);
/* 160 */           if ((!msg.getConversationType().equals(conversationType)) || (!msg.getTargetId().equals(targetId)) || (msg.getMessageId() != messageId)) {
/*     */             continue;
/*     */           }
/* 163 */           this.pendingMessages.remove(msg);
/* 164 */           break;
/*     */         }
/*     */ 
/* 167 */         if (this.pendingMessages.size() == 0)
/* 168 */           this.executingMessage = null;
/*     */       }
/*     */     }
/*     */ 
/*     */     private void polling() {
/* 173 */       synchronized (this.pendingMessages) {
/* 174 */         RLog.d("sendMediaMessageManager", "polling " + this.pendingMessages.size());
/* 175 */         if (this.pendingMessages.size() > 0) {
/* 176 */           this.executingMessage = ((Message)this.pendingMessages.remove(0));
/* 177 */           sendMediaMessageManager.this.executorService.submit(this);
/*     */         } else {
/* 179 */           this.pendingMessages.clear();
/* 180 */           this.executingMessage = null;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 187 */       RongIM.getInstance().sendImageMessage(this.executingMessage, null, null, new RongIMClient.SendImageMessageCallback()
/*     */       {
/*     */         public void onAttached(Message message)
/*     */         {
/*     */         }
/*     */ 
/*     */         public void onError(Message message, RongIMClient.ErrorCode code)
/*     */         {
/* 195 */           sendMediaMessageManager.UploadController.this.polling();
/*     */         }
/*     */ 
/*     */         public void onSuccess(Message message)
/*     */         {
/* 200 */           sendMediaMessageManager.UploadController.this.polling();
/*     */         }
/*     */ 
/*     */         public void onProgress(Message message, int progress)
/*     */         {
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   static class SingletonHolder
/*     */   {
/*  29 */     static sendMediaMessageManager sInstance = new sendMediaMessageManager(null);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.manager.sendMediaMessageManager
 * JD-Core Version:    0.6.0
 */
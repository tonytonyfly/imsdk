/*     */ package io.rong.imkit.manager;
/*     */ 
/*     */ import android.net.Uri;
/*     */ import android.support.annotation.Nullable;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.RongIM.OnSendMessageListener;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.RongIMClient.SendImageMessageCallback;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.imlib.model.Message.SentStatus;
/*     */ import io.rong.message.ImageMessage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.SynchronousQueue;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class SendImageManager
/*     */ {
/*     */   private static final String TAG = "SendImageManager";
/*     */   private ExecutorService executorService;
/*     */   private UploadController uploadController;
/*     */ 
/*     */   public static SendImageManager getInstance()
/*     */   {
/*  34 */     return SingletonHolder.sInstance;
/*     */   }
/*     */ 
/*     */   private SendImageManager() {
/*  38 */     this.executorService = getExecutorService();
/*  39 */     this.uploadController = new UploadController();
/*     */   }
/*     */ 
/*     */   public void sendImages(Conversation.ConversationType conversationType, String targetId, List<Uri> imageList, boolean isFull) {
/*  43 */     RLog.d("SendImageManager", "sendImages " + imageList.size());
/*  44 */     for (Uri image : imageList) {
/*  45 */       ImageMessage content = ImageMessage.obtain(image, image, isFull);
/*  46 */       RongIM.OnSendMessageListener listener = RongContext.getInstance().getOnSendMessageListener();
/*  47 */       if (listener != null) {
/*  48 */         Message message = listener.onSend(Message.obtain(targetId, conversationType, content));
/*  49 */         if (message != null)
/*  50 */           RongIMClient.getInstance().insertMessage(conversationType, targetId, null, message.getContent(), new RongIMClient.ResultCallback()
/*     */           {
/*     */             public void onSuccess(Message message)
/*     */             {
/*  57 */               message.setSentStatus(Message.SentStatus.SENDING);
/*  58 */               RongIMClient.getInstance().setMessageSentStatus(message.getMessageId(), Message.SentStatus.SENDING, null);
/*  59 */               RongContext.getInstance().getEventBus().post(message);
/*  60 */               SendImageManager.this.uploadController.execute(message);
/*     */             }
/*     */ 
/*     */             public void onError(RongIMClient.ErrorCode e)
/*     */             {
/*     */             }
/*     */           });
/*     */       }
/*     */       else {
/*  70 */         RongIMClient.getInstance().insertMessage(conversationType, targetId, null, content, new RongIMClient.ResultCallback()
/*     */         {
/*     */           public void onSuccess(Message message)
/*     */           {
/*  77 */             message.setSentStatus(Message.SentStatus.SENDING);
/*  78 */             RongIMClient.getInstance().setMessageSentStatus(message.getMessageId(), Message.SentStatus.SENDING, null);
/*  79 */             RongContext.getInstance().getEventBus().post(message);
/*  80 */             SendImageManager.this.uploadController.execute(message);
/*     */           }
/*     */ 
/*     */           public void onError(RongIMClient.ErrorCode e)
/*     */           {
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void cancelSendingImages(Conversation.ConversationType conversationType, String targetId) {
/*  93 */     RLog.d("SendImageManager", "cancelSendingImages");
/*  94 */     if ((conversationType != null) && (targetId != null) && (this.uploadController != null))
/*  95 */       this.uploadController.cancel(conversationType, targetId);
/*     */   }
/*     */ 
/*     */   public void cancelSendingImage(Conversation.ConversationType conversationType, String targetId, int messageId) {
/*  99 */     RLog.d("SendImageManager", "cancelSendingImages");
/* 100 */     if ((conversationType != null) && (targetId != null) && (this.uploadController != null) && (messageId > 0))
/* 101 */       this.uploadController.cancel(conversationType, targetId, messageId);
/*     */   }
/*     */ 
/*     */   public void reset() {
/* 105 */     this.uploadController.reset();
/*     */   }
/*     */ 
/*     */   private ExecutorService getExecutorService()
/*     */   {
/* 213 */     if (this.executorService == null) {
/* 214 */       this.executorService = new ThreadPoolExecutor(1, 2147483647, 60L, TimeUnit.SECONDS, new SynchronousQueue(), threadFactory("Rong SendMediaManager", false));
/*     */     }
/*     */ 
/* 221 */     return this.executorService;
/*     */   }
/*     */ 
/*     */   private ThreadFactory threadFactory(String name, boolean daemon) {
/* 225 */     return new ThreadFactory(name, daemon)
/*     */     {
/*     */       public Thread newThread(@Nullable Runnable runnable) {
/* 228 */         Thread result = new Thread(runnable, this.val$name);
/* 229 */         result.setDaemon(this.val$daemon);
/* 230 */         return result;
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
/* 113 */       this.pendingMessages = new ArrayList();
/*     */     }
/*     */ 
/*     */     public void execute(Message message) {
/* 117 */       synchronized (this.pendingMessages) {
/* 118 */         this.pendingMessages.add(message);
/* 119 */         if (this.executingMessage == null) {
/* 120 */           this.executingMessage = ((Message)this.pendingMessages.remove(0));
/* 121 */           SendImageManager.this.executorService.submit(this);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public void reset() {
/* 127 */       RLog.w("SendImageManager", "Rest Sending Images.");
/* 128 */       synchronized (this.pendingMessages) {
/* 129 */         for (Message message : this.pendingMessages) {
/* 130 */           message.setSentStatus(Message.SentStatus.FAILED);
/* 131 */           RongContext.getInstance().getEventBus().post(message);
/*     */         }
/* 133 */         this.pendingMessages.clear();
/*     */       }
/* 135 */       if (this.executingMessage != null) {
/* 136 */         this.executingMessage.setSentStatus(Message.SentStatus.FAILED);
/* 137 */         RongContext.getInstance().getEventBus().post(this.executingMessage);
/* 138 */         this.executingMessage = null;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void cancel(Conversation.ConversationType conversationType, String targetId) {
/* 143 */       synchronized (this.pendingMessages) {
/* 144 */         int count = this.pendingMessages.size();
/* 145 */         for (int i = 0; i < count; i++) {
/* 146 */           Message msg = (Message)this.pendingMessages.get(i);
/* 147 */           if ((msg.getConversationType().equals(conversationType)) && (msg.getTargetId().equals(targetId))) {
/* 148 */             this.pendingMessages.remove(msg);
/*     */           }
/*     */         }
/* 151 */         if (this.pendingMessages.size() == 0)
/* 152 */           this.executingMessage = null;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void cancel(Conversation.ConversationType conversationType, String targetId, int messageId) {
/* 157 */       synchronized (this.pendingMessages) {
/* 158 */         int count = this.pendingMessages.size();
/* 159 */         for (int i = 0; i < count; i++) {
/* 160 */           Message msg = (Message)this.pendingMessages.get(i);
/* 161 */           if ((!msg.getConversationType().equals(conversationType)) || (!msg.getTargetId().equals(targetId)) || (msg.getMessageId() != messageId)) {
/*     */             continue;
/*     */           }
/* 164 */           this.pendingMessages.remove(msg);
/* 165 */           break;
/*     */         }
/*     */ 
/* 168 */         if (this.pendingMessages.size() == 0)
/* 169 */           this.executingMessage = null;
/*     */       }
/*     */     }
/*     */ 
/*     */     private void polling() {
/* 174 */       synchronized (this.pendingMessages) {
/* 175 */         RLog.d("SendImageManager", "polling " + this.pendingMessages.size());
/* 176 */         if (this.pendingMessages.size() > 0) {
/* 177 */           this.executingMessage = ((Message)this.pendingMessages.remove(0));
/* 178 */           SendImageManager.this.executorService.submit(this);
/*     */         } else {
/* 180 */           this.pendingMessages.clear();
/* 181 */           this.executingMessage = null;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 188 */       RongIM.getInstance().sendImageMessage(this.executingMessage, null, null, new RongIMClient.SendImageMessageCallback()
/*     */       {
/*     */         public void onAttached(Message message)
/*     */         {
/*     */         }
/*     */ 
/*     */         public void onError(Message message, RongIMClient.ErrorCode code)
/*     */         {
/* 196 */           SendImageManager.UploadController.this.polling();
/*     */         }
/*     */ 
/*     */         public void onSuccess(Message message)
/*     */         {
/* 201 */           SendImageManager.UploadController.this.polling();
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
/*  30 */     static SendImageManager sInstance = new SendImageManager(null);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.manager.SendImageManager
 * JD-Core Version:    0.6.0
 */
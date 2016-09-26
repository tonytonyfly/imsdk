/*     */ package io.rong.imlib;
/*     */ 
/*     */ import android.net.Uri;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.message.MediaMessageContent;
/*     */ 
/*     */ public abstract interface IRongCallback
/*     */ {
/*     */   public static abstract interface IDownloadMediaMessageCallback
/*     */   {
/*     */     public abstract void onSuccess(Message paramMessage);
/*     */ 
/*     */     public abstract void onProgress(Message paramMessage, int paramInt);
/*     */ 
/*     */     public abstract void onError(Message paramMessage, RongIMClient.ErrorCode paramErrorCode);
/*     */ 
/*     */     public abstract void onCanceled(Message paramMessage);
/*     */   }
/*     */ 
/*     */   public static class MediaMessageUploader
/*     */   {
/*     */     private IRongCallback.ISendMediaMessageCallbackWithUploader callbackWithUploader;
/*     */     private Message message;
/*     */     private String pushContent;
/*     */     private String pushData;
/*     */ 
/*     */     public MediaMessageUploader(Message message, String pushContent, String pushData, IRongCallback.ISendMediaMessageCallbackWithUploader callbackWithUploader)
/*     */     {
/*  77 */       this.callbackWithUploader = callbackWithUploader;
/*  78 */       this.message = message;
/*  79 */       this.pushContent = pushContent;
/*  80 */       this.pushData = pushData;
/*     */     }
/*     */ 
/*     */     public void update(int progress)
/*     */     {
/*  89 */       if (this.callbackWithUploader != null)
/*  90 */         this.callbackWithUploader.onProgress(this.message, progress);
/*     */     }
/*     */ 
/*     */     public void error()
/*     */     {
/*  98 */       if (this.callbackWithUploader != null)
/*  99 */         this.callbackWithUploader.onError(this.message, RongIMClient.ErrorCode.RC_MSG_SEND_FAIL);
/*     */     }
/*     */ 
/*     */     public void success(Uri uploadedUri)
/*     */     {
/* 109 */       if (uploadedUri == null) {
/* 110 */         RLog.e("MediaMessageUploader", "uploadedUri is null.");
/* 111 */         if (this.callbackWithUploader != null) {
/* 112 */           this.callbackWithUploader.onError(this.message, RongIMClient.ErrorCode.RC_MSG_SEND_FAIL);
/*     */         }
/* 114 */         return;
/*     */       }
/* 116 */       MediaMessageContent content = (MediaMessageContent)this.message.getContent();
/* 117 */       content.setMediaUrl(uploadedUri);
/* 118 */       RongIMClient.getInstance().sendMediaMessage(this.message, this.pushContent, this.pushData, new IRongCallback.ISendMediaMessageCallback()
/*     */       {
/*     */         public void onProgress(Message message, int progress)
/*     */         {
/*     */         }
/*     */ 
/*     */         public void onAttached(Message message)
/*     */         {
/*     */         }
/*     */ 
/*     */         public void onSuccess(Message message)
/*     */         {
/*     */         }
/*     */ 
/*     */         public void onError(Message message, RongIMClient.ErrorCode errorCode)
/*     */         {
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface ISendMediaMessageCallbackWithUploader
/*     */   {
/*     */     public abstract void onAttached(Message paramMessage, IRongCallback.MediaMessageUploader paramMediaMessageUploader);
/*     */ 
/*     */     public abstract void onProgress(Message paramMessage, int paramInt);
/*     */ 
/*     */     public abstract void onSuccess(Message paramMessage);
/*     */ 
/*     */     public abstract void onError(Message paramMessage, RongIMClient.ErrorCode paramErrorCode);
/*     */   }
/*     */ 
/*     */   public static abstract interface ISendMediaMessageCallback extends IRongCallback.ISendMessageCallback
/*     */   {
/*     */     public abstract void onProgress(Message paramMessage, int paramInt);
/*     */   }
/*     */ 
/*     */   public static abstract interface ISendMessageCallback
/*     */   {
/*     */     public abstract void onAttached(Message paramMessage);
/*     */ 
/*     */     public abstract void onSuccess(Message paramMessage);
/*     */ 
/*     */     public abstract void onError(Message paramMessage, RongIMClient.ErrorCode paramErrorCode);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.IRongCallback
 * JD-Core Version:    0.6.0
 */
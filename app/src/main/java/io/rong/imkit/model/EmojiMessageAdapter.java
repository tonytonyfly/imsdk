/*     */ package io.rong.imkit.model;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.res.Resources;
/*     */ import android.content.res.Resources.NotFoundException;
/*     */ import android.text.SpannableStringBuilder;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imkit.R.bool;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.utils.AndroidEmoji;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.OperationCallback;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.imlib.model.Message.MessageDirection;
/*     */ import io.rong.imlib.model.ReadReceiptInfo;
/*     */ import io.rong.message.TextMessage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ public class EmojiMessageAdapter
/*     */ {
/*     */   private static final String TAG = "EmojiMessageAdapter";
/*     */   private static EmojiMessageAdapter mLogic;
/*     */ 
/*     */   public static void init(Context context)
/*     */   {
/*  27 */     mLogic = new EmojiMessageAdapter();
/*     */   }
/*     */ 
/*     */   public static EmojiMessageAdapter getInstance() {
/*  31 */     return mLogic;
/*     */   }
/*     */ 
/*     */   public void getHistoryMessages(Conversation.ConversationType conversationType, String targetId, int oldestMessageId, int count, RongIMClient.ResultCallback<List<UIMessage>> callback)
/*     */   {
/*  36 */     RongIM.getInstance().getHistoryMessages(conversationType, targetId, oldestMessageId, count, new RongIMClient.ResultCallback(callback)
/*     */     {
/*     */       public void onSuccess(List<Message> messages)
/*     */       {
/*  40 */         EmojiMessageAdapter.this.sendReadReceiptResponseIfNeeded(messages);
/*  41 */         if (this.val$callback != null)
/*  42 */           this.val$callback.onSuccess(EmojiMessageAdapter.this.emojiMessageToUIMessage(messages));
/*     */       }
/*     */ 
/*     */       public void onError(RongIMClient.ErrorCode e)
/*     */       {
/*  48 */         if (this.val$callback != null)
/*  49 */           this.val$callback.onError(e);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void getRemoteHistoryMessages(Conversation.ConversationType conversationType, String targetId, long dataTime, int count, RongIMClient.ResultCallback<List<UIMessage>> callback) {
/*  56 */     RongIMClient.getInstance().getRemoteHistoryMessages(conversationType, targetId, dataTime, count, new RongIMClient.ResultCallback(callback)
/*     */     {
/*     */       public void onSuccess(List<Message> messages) {
/*  59 */         EmojiMessageAdapter.this.sendReadReceiptResponseIfNeeded(messages);
/*  60 */         if (this.val$callback != null)
/*  61 */           this.val$callback.onSuccess(EmojiMessageAdapter.this.emojiMessageToUIMessage(messages));
/*     */       }
/*     */ 
/*     */       public void onError(RongIMClient.ErrorCode e)
/*     */       {
/*  67 */         if (this.val$callback != null)
/*  68 */           this.val$callback.onError(e);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void getLatestMessages(Conversation.ConversationType conversationType, String targetId, int count, RongIMClient.ResultCallback<List<UIMessage>> callback) {
/*  75 */     RongIM.getInstance().getLatestMessages(conversationType, targetId, count, new RongIMClient.ResultCallback(callback)
/*     */     {
/*     */       public void onSuccess(List<Message> messages)
/*     */       {
/*  79 */         EmojiMessageAdapter.this.sendReadReceiptResponseIfNeeded(messages);
/*  80 */         if (this.val$callback != null) {
/*  81 */           if ((messages != null) && (messages.size() > 0)) {
/*  82 */             Collections.reverse(messages);
/*     */           }
/*  84 */           this.val$callback.onSuccess(EmojiMessageAdapter.this.emojiMessageToUIMessage(messages));
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onError(RongIMClient.ErrorCode e)
/*     */       {
/*  90 */         if (this.val$callback != null)
/*  91 */           this.val$callback.onError(e);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private List<UIMessage> emojiMessageToUIMessage(List<Message> messages) {
/*  98 */     List msgList = new ArrayList();
/*  99 */     if ((messages == null) || (messages.size() == 0)) {
/* 100 */       return msgList;
/*     */     }
/*     */ 
/* 103 */     for (Message message : messages) {
/* 104 */       UIMessage uiMessage = UIMessage.obtain(message);
/* 105 */       if ((message.getContent() instanceof TextMessage)) {
/* 106 */         TextMessage textMessage = (TextMessage)message.getContent();
/* 107 */         if (textMessage.getContent() != null) {
/* 108 */           SpannableStringBuilder spannable = new SpannableStringBuilder(textMessage.getContent());
/* 109 */           AndroidEmoji.ensure(spannable);
/* 110 */           uiMessage.setTextMessageContent(spannable);
/*     */         }
/*     */       }
/* 113 */       msgList.add(uiMessage);
/*     */     }
/* 115 */     return msgList;
/*     */   }
/*     */ 
/*     */   private void sendReadReceiptResponseIfNeeded(List<Message> messages) {
/* 119 */     if (messages == null) {
/* 120 */       return;
/*     */     }
/* 122 */     boolean readRec = false;
/*     */     try {
/* 124 */       readRec = RongContext.getInstance().getResources().getBoolean(R.bool.rc_read_receipt);
/*     */     } catch (Resources.NotFoundException e) {
/* 126 */       RLog.e("EmojiMessageAdapter", "rc_read_receipt not configure in rc_config.xml");
/* 127 */       e.printStackTrace();
/*     */     }
/* 129 */     if (!readRec) {
/* 130 */       return;
/*     */     }
/* 132 */     Conversation.ConversationType type = ((Message)messages.get(0)).getConversationType();
/* 133 */     String targetId = ((Message)messages.get(0)).getTargetId();
/* 134 */     if ((!type.equals(Conversation.ConversationType.GROUP)) && (!type.equals(Conversation.ConversationType.DISCUSSION))) {
/* 135 */       return;
/*     */     }
/* 137 */     if (!RongContext.getInstance().isReadReceiptConversationType(type)) {
/* 138 */       return;
/*     */     }
/* 140 */     List responseMessageList = new ArrayList();
/* 141 */     for (Message message : messages) {
/* 142 */       ReadReceiptInfo readReceiptInfo = message.getReadReceiptInfo();
/* 143 */       if (readReceiptInfo == null) {
/*     */         continue;
/*     */       }
/* 146 */       if ((readReceiptInfo.isReadReceiptMessage()) && (!readReceiptInfo.hasRespond()) && (message.getMessageDirection().equals(Message.MessageDirection.RECEIVE)))
/*     */       {
/* 148 */         responseMessageList.add(message);
/*     */       }
/*     */     }
/* 151 */     if (responseMessageList.size() > 0)
/* 152 */       RongIMClient.getInstance().sendReadReceiptResponse(type, targetId, responseMessageList, new RongIMClient.OperationCallback()
/*     */       {
/*     */         public void onSuccess()
/*     */         {
/*     */         }
/*     */ 
/*     */         public void onError(RongIMClient.ErrorCode errorCode)
/*     */         {
/* 160 */           RLog.e("EmojiMessageAdapter", "sendReadReceiptResponse failed, errorCode = " + errorCode);
/*     */         }
/*     */       });
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.model.EmojiMessageAdapter
 * JD-Core Version:    0.6.0
 */
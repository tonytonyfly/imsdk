/*     */ package io.rong.imkit.model;
/*     */ 
/*     */ import android.text.SpannableStringBuilder;
/*     */ import io.rong.imkit.utils.AndroidEmoji;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import io.rong.message.TextMessage;
/*     */ 
/*     */ public class UIMessage extends Message
/*     */ {
/*     */   private SpannableStringBuilder textMessageContent;
/*     */   private UserInfo mUserInfo;
/*     */   private int mProgress;
/*  19 */   private boolean evaluated = false;
/*  20 */   private boolean isHistoryMessage = true;
/*     */   public boolean continuePlayAudio;
/*     */ 
/*     */   public Message getMessage()
/*     */   {
/*  26 */     Message message = new Message();
/*     */ 
/*  28 */     message.setConversationType(getConversationType());
/*  29 */     message.setTargetId(getTargetId());
/*  30 */     message.setMessageId(getMessageId());
/*  31 */     message.setObjectName(getObjectName());
/*  32 */     message.setContent(getContent());
/*  33 */     message.setSentStatus(getSentStatus());
/*  34 */     message.setSenderUserId(getSenderUserId());
/*  35 */     message.setReceivedStatus(getReceivedStatus());
/*  36 */     message.setMessageDirection(getMessageDirection());
/*  37 */     message.setReceivedTime(getReceivedTime());
/*  38 */     message.setSentTime(getSentTime());
/*  39 */     message.setExtra(getExtra());
/*  40 */     message.setUId(getUId());
/*  41 */     message.setReadReceiptInfo(getReadReceiptInfo());
/*     */ 
/*  43 */     return message;
/*     */   }
/*     */ 
/*     */   public static UIMessage obtain(Message message) {
/*  47 */     UIMessage uiMessage = new UIMessage();
/*     */ 
/*  49 */     uiMessage.setConversationType(message.getConversationType());
/*  50 */     uiMessage.setTargetId(message.getTargetId());
/*  51 */     uiMessage.setMessageId(message.getMessageId());
/*  52 */     uiMessage.setObjectName(message.getObjectName());
/*  53 */     uiMessage.setContent(message.getContent());
/*  54 */     uiMessage.setSentStatus(message.getSentStatus());
/*  55 */     uiMessage.setSenderUserId(message.getSenderUserId());
/*  56 */     uiMessage.setReceivedStatus(message.getReceivedStatus());
/*  57 */     uiMessage.setMessageDirection(message.getMessageDirection());
/*  58 */     uiMessage.setReceivedTime(message.getReceivedTime());
/*  59 */     uiMessage.setSentTime(message.getSentTime());
/*  60 */     uiMessage.setExtra(message.getExtra());
/*  61 */     if (message.getContent() != null) {
/*  62 */       uiMessage.setUserInfo(message.getContent().getUserInfo());
/*     */     }
/*  64 */     uiMessage.setUId(message.getUId());
/*  65 */     uiMessage.continuePlayAudio = false;
/*  66 */     uiMessage.setReadReceiptInfo(message.getReadReceiptInfo());
/*  67 */     return uiMessage;
/*     */   }
/*     */ 
/*     */   public SpannableStringBuilder getTextMessageContent()
/*     */   {
/*  72 */     if (this.textMessageContent == null)
/*     */     {
/*  74 */       if ((getContent() instanceof TextMessage)) {
/*  75 */         TextMessage textMessage = (TextMessage)getContent();
/*  76 */         if (textMessage.getContent() != null) {
/*  77 */           SpannableStringBuilder spannable = new SpannableStringBuilder(textMessage.getContent());
/*  78 */           AndroidEmoji.ensure(spannable);
/*  79 */           setTextMessageContent(spannable);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  84 */     return this.textMessageContent;
/*     */   }
/*     */ 
/*     */   public void setTextMessageContent(SpannableStringBuilder textMessageContent) {
/*  88 */     this.textMessageContent = textMessageContent;
/*     */   }
/*     */ 
/*     */   public UserInfo getUserInfo()
/*     */   {
/*  93 */     return this.mUserInfo;
/*     */   }
/*     */ 
/*     */   public void setUserInfo(UserInfo userInfo) {
/*  97 */     if (getUserInfo() == null)
/*  98 */       this.mUserInfo = userInfo;
/*     */   }
/*     */ 
/*     */   public void setProgress(int progress) {
/* 102 */     this.mProgress = progress;
/*     */   }
/*     */ 
/*     */   public int getProgress() {
/* 106 */     return this.mProgress;
/*     */   }
/*     */ 
/*     */   public void setEvaluated(boolean evaluated) {
/* 110 */     this.evaluated = evaluated;
/*     */   }
/*     */ 
/*     */   public boolean getEvaluated() {
/* 114 */     return this.evaluated;
/*     */   }
/*     */ 
/*     */   public void setIsHistoryMessage(boolean isHistroyMessage) {
/* 118 */     this.isHistoryMessage = isHistroyMessage;
/*     */   }
/*     */ 
/*     */   public boolean getIsHistoryMessage() {
/* 122 */     return this.isHistoryMessage;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.model.UIMessage
 * JD-Core Version:    0.6.0
 */
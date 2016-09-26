/*     */ package io.rong.imkit.model;
/*     */ 
/*     */ import android.content.res.Resources;
/*     */ import android.net.Uri;
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.Spannable;
/*     */ import android.text.SpannableStringBuilder;
/*     */ import android.text.TextUtils;
/*     */ import android.text.style.ForegroundColorSpan;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imkit.R.color;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.widget.provider.IContainerItemProvider.ConversationProvider;
/*     */ import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.model.Conversation;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.MentionedInfo;
/*     */ import io.rong.imlib.model.MentionedInfo.MentionedType;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.imlib.model.Message.MessageDirection;
/*     */ import io.rong.imlib.model.Message.ReceivedStatus;
/*     */ import io.rong.imlib.model.Message.SentStatus;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import io.rong.message.VoiceMessage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class UIConversation
/*     */   implements Parcelable
/*     */ {
/*     */   private String conversationTitle;
/*     */   private Uri portrait;
/*     */   private Spannable conversationContent;
/*     */   private MessageContent messageContent;
/*     */   private long conversationTime;
/*     */   private int unReadMessageCount;
/*     */   private boolean isTop;
/*     */   private Conversation.ConversationType conversationType;
/*     */   private Message.SentStatus sentStatus;
/*     */   private String targetId;
/*     */   private String senderId;
/*     */   private boolean isGathered;
/*     */   private boolean notificationBlockStatus;
/*     */   private String draft;
/*     */   private int latestMessageId;
/*     */   private boolean extraFlag;
/*     */   private boolean isMentioned;
/*     */   private ArrayList<String> nicknameIds;
/* 432 */   private UnreadRemindType mUnreadType = UnreadRemindType.REMIND_WITH_COUNTING;
/*     */ 
/* 467 */   public static final Parcelable.Creator<UIConversation> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public UIConversation createFromParcel(Parcel source)
/*     */     {
/* 471 */       return new UIConversation();
/*     */     }
/*     */ 
/*     */     public UIConversation[] newArray(int size)
/*     */     {
/* 476 */       return new UIConversation[size];
/*     */     }
/* 467 */   };
/*     */ 
/*     */   public boolean getExtraFlag()
/*     */   {
/*  48 */     return this.extraFlag;
/*     */   }
/*     */ 
/*     */   public void setExtraFlag(boolean extraFlag) {
/*  52 */     this.extraFlag = extraFlag;
/*     */   }
/*     */ 
/*     */   public UIConversation()
/*     */   {
/*  58 */     this.nicknameIds = new ArrayList();
/*     */   }
/*     */ 
/*     */   public void setUIConversationTitle(String title)
/*     */   {
/*  63 */     this.conversationTitle = title;
/*     */   }
/*     */ 
/*     */   public String getUIConversationTitle() {
/*  67 */     return this.conversationTitle;
/*     */   }
/*     */ 
/*     */   public void setIconUrl(Uri iconUrl) {
/*  71 */     this.portrait = iconUrl;
/*     */   }
/*     */ 
/*     */   public Uri getIconUrl() {
/*  75 */     return this.portrait;
/*     */   }
/*     */ 
/*     */   public void setConversationContent(Spannable content) {
/*  79 */     this.conversationContent = content;
/*     */   }
/*     */ 
/*     */   public Spannable getConversationContent() {
/*  83 */     return this.conversationContent;
/*     */   }
/*     */ 
/*     */   public void setMessageContent(MessageContent content) {
/*  87 */     this.messageContent = content;
/*     */   }
/*     */ 
/*     */   public MessageContent getMessageContent() {
/*  91 */     return this.messageContent;
/*     */   }
/*     */ 
/*     */   public void setUIConversationTime(long time) {
/*  95 */     this.conversationTime = time;
/*     */   }
/*     */ 
/*     */   public long getUIConversationTime() {
/*  99 */     return this.conversationTime;
/*     */   }
/*     */ 
/*     */   public void setUnReadMessageCount(int count) {
/* 103 */     this.unReadMessageCount = count;
/*     */   }
/*     */ 
/*     */   public int getUnReadMessageCount() {
/* 107 */     return this.unReadMessageCount;
/*     */   }
/*     */ 
/*     */   public void setTop(boolean value) {
/* 111 */     this.isTop = value;
/*     */   }
/*     */ 
/*     */   public boolean isTop() {
/* 115 */     return this.isTop;
/*     */   }
/*     */ 
/*     */   public void setConversationType(Conversation.ConversationType type) {
/* 119 */     this.conversationType = type;
/*     */   }
/*     */ 
/*     */   public Conversation.ConversationType getConversationType() {
/* 123 */     return this.conversationType;
/*     */   }
/*     */ 
/*     */   public void setSentStatus(Message.SentStatus status) {
/* 127 */     this.sentStatus = status;
/*     */   }
/*     */ 
/*     */   public Message.SentStatus getSentStatus() {
/* 131 */     return this.sentStatus;
/*     */   }
/*     */ 
/*     */   public void setConversationTargetId(String id) {
/* 135 */     this.targetId = id;
/*     */   }
/*     */ 
/*     */   public String getConversationTargetId() {
/* 139 */     return this.targetId;
/*     */   }
/*     */ 
/*     */   public void setConversationSenderId(String id) {
/* 143 */     this.senderId = id;
/*     */   }
/*     */ 
/*     */   public String getConversationSenderId() {
/* 147 */     return this.senderId;
/*     */   }
/*     */ 
/*     */   public void setConversationGatherState(boolean state) {
/* 151 */     this.isGathered = state;
/*     */   }
/*     */ 
/*     */   public boolean getConversationGatherState() {
/* 155 */     return this.isGathered;
/*     */   }
/*     */ 
/*     */   public void setNotificationBlockStatus(boolean status) {
/* 159 */     this.notificationBlockStatus = status;
/*     */   }
/*     */ 
/*     */   public boolean getNotificationBlockStatus() {
/* 163 */     return this.notificationBlockStatus;
/*     */   }
/*     */ 
/*     */   public void setDraft(String content) {
/* 167 */     this.draft = content;
/*     */   }
/*     */ 
/*     */   public String getDraft() {
/* 171 */     return this.draft;
/*     */   }
/*     */ 
/*     */   public void setLatestMessageId(int id) {
/* 175 */     this.latestMessageId = id;
/*     */   }
/*     */ 
/*     */   public int getLatestMessageId() {
/* 179 */     return this.latestMessageId;
/*     */   }
/*     */ 
/*     */   public void addNickname(String userId) {
/* 183 */     this.nicknameIds.add(userId);
/*     */   }
/*     */ 
/*     */   public void removeNickName(String userId) {
/* 187 */     this.nicknameIds.remove(userId);
/*     */   }
/*     */ 
/*     */   public boolean hasNickname(String userId) {
/* 191 */     return this.nicknameIds.contains(userId);
/*     */   }
/*     */ 
/*     */   public void setMentionedFlag(boolean flag) {
/* 195 */     this.isMentioned = flag;
/*     */   }
/*     */   public boolean getMentionedFlag() {
/* 198 */     return this.isMentioned;
/*     */   }
/*     */ 
/*     */   public static UIConversation obtain(Conversation conversation, boolean gatherState) {
/* 202 */     if (RongContext.getInstance() == null) {
/* 203 */       throw new ExceptionInInitializerError("RongContext hasn't been initialized !!");
/*     */     }
/* 205 */     if (RongContext.getInstance().getConversationTemplate(conversation.getConversationType().getName()) == null) {
/* 206 */       throw new IllegalArgumentException("the conversation type hasn't been registered! type:" + conversation.getConversationType());
/*     */     }
/*     */ 
/* 209 */     MessageContent msgContent = conversation.getLatestMessage();
/*     */ 
/* 213 */     Uri uri = null;
/* 214 */     if (!TextUtils.isEmpty(conversation.getPortraitUrl()))
/* 215 */       uri = Uri.parse(conversation.getPortraitUrl());
/* 216 */     String title = conversation.getConversationTitle();
/*     */ 
/* 235 */     if ((uri == null) || (title == null)) {
/* 236 */       title = RongContext.getInstance().getConversationTemplate(conversation.getConversationType().getName()).getTitle(conversation.getTargetId());
/*     */ 
/* 238 */       uri = RongContext.getInstance().getConversationTemplate(conversation.getConversationType().getName()).getPortraitUri(conversation.getTargetId());
/*     */     }
/*     */ 
/* 243 */     UIConversation uiConversation = new UIConversation();
/* 244 */     uiConversation.setMessageContent(msgContent);
/* 245 */     uiConversation.setUnReadMessageCount(conversation.getUnreadMessageCount());
/* 246 */     uiConversation.setUIConversationTime(conversation.getSentTime());
/* 247 */     uiConversation.setConversationGatherState(gatherState);
/* 248 */     if ((gatherState) && (RongContext.getInstance() != null)) {
/* 249 */       uiConversation.setUIConversationTitle(RongContext.getInstance().getGatheredConversationTitle(conversation.getConversationType()));
/* 250 */       uiConversation.setIconUrl(null);
/*     */     } else {
/* 252 */       uiConversation.setUIConversationTitle(title);
/* 253 */       uiConversation.setIconUrl(uri);
/*     */     }
/* 255 */     uiConversation.setConversationType(conversation.getConversationType());
/* 256 */     uiConversation.setTop(conversation.isTop());
/* 257 */     uiConversation.setSentStatus(conversation.getSentStatus());
/* 258 */     uiConversation.setConversationTargetId(conversation.getTargetId());
/* 259 */     uiConversation.setConversationSenderId(conversation.getSenderUserId());
/* 260 */     uiConversation.setLatestMessageId(conversation.getLatestMessageId());
/* 261 */     uiConversation.setDraft(conversation.getDraft());
/*     */ 
/* 263 */     if (conversation.getMentionedCount() > 0)
/* 264 */       uiConversation.isMentioned = true;
/*     */     else {
/* 266 */       uiConversation.isMentioned = false;
/*     */     }
/*     */ 
/* 269 */     if (!TextUtils.isEmpty(conversation.getDraft())) {
/* 270 */       uiConversation.setSentStatus(null);
/*     */     }
/* 272 */     uiConversation.setConversationContent(uiConversation.buildConversationContent(uiConversation));
/*     */ 
/* 274 */     return uiConversation;
/*     */   }
/*     */ 
/*     */   public static UIConversation obtain(Message message, boolean gather) {
/* 278 */     String title = "";
/* 279 */     Uri iconUri = null;
/* 280 */     UserInfo userInfo = message.getContent().getUserInfo();
/* 281 */     Conversation.ConversationType conversationType = message.getConversationType();
/*     */ 
/* 283 */     if ((userInfo != null) && (message.getTargetId().equals(userInfo.getUserId())) && ((conversationType.equals(Conversation.ConversationType.PRIVATE)) || (conversationType.equals(Conversation.ConversationType.SYSTEM))))
/*     */     {
/* 287 */       iconUri = userInfo.getPortraitUri();
/* 288 */       title = userInfo.getName();
/* 289 */       RongIMClient.getInstance().updateConversationInfo(message.getConversationType(), message.getTargetId(), title, iconUri != null ? iconUri.toString() : "", null);
/*     */     }
/*     */ 
/* 292 */     if ((RongContext.getInstance() != null) && ((iconUri == null) || (title == null))) {
/* 293 */       title = RongContext.getInstance().getConversationTemplate(message.getConversationType().getName()).getTitle(message.getTargetId());
/*     */ 
/* 295 */       iconUri = RongContext.getInstance().getConversationTemplate(message.getConversationType().getName()).getPortraitUri(message.getTargetId());
/*     */     }
/*     */ 
/* 299 */     MessageTag tag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
/* 300 */     UIConversation tempUIConversation = new UIConversation();
/* 301 */     if ((tag != null) && ((tag.flag() & 0x3) == 3) && (message.getMessageDirection().equals(Message.MessageDirection.RECEIVE))) {
/* 302 */       tempUIConversation.setUnReadMessageCount(1);
/*     */     }
/* 304 */     tempUIConversation.setMessageContent(message.getContent());
/* 305 */     tempUIConversation.setUIConversationTime(message.getSentTime());
/* 306 */     if (gather) {
/* 307 */       tempUIConversation.setUIConversationTitle(RongContext.getInstance().getGatheredConversationTitle(message.getConversationType()));
/* 308 */       tempUIConversation.setIconUrl(null);
/*     */     } else {
/* 310 */       tempUIConversation.setUIConversationTitle(title);
/* 311 */       tempUIConversation.setIconUrl(iconUri);
/*     */     }
/* 313 */     tempUIConversation.setConversationType(message.getConversationType());
/* 314 */     tempUIConversation.setConversationTargetId(message.getTargetId());
/* 315 */     if (message.getMessageDirection() == Message.MessageDirection.SEND)
/* 316 */       tempUIConversation.setConversationSenderId(RongIM.getInstance().getCurrentUserId());
/*     */     else {
/* 318 */       tempUIConversation.setConversationSenderId(message.getSenderUserId());
/*     */     }
/*     */ 
/* 321 */     tempUIConversation.setSentStatus(message.getSentStatus());
/* 322 */     tempUIConversation.setLatestMessageId(message.getMessageId());
/* 323 */     tempUIConversation.setConversationGatherState(gather);
/* 324 */     tempUIConversation.setConversationContent(tempUIConversation.buildConversationContent(tempUIConversation));
/*     */ 
/* 326 */     MentionedInfo mentionedInfo = message.getContent().getMentionedInfo();
/* 327 */     if ((mentionedInfo != null) && ((mentionedInfo.getType().equals(MentionedInfo.MentionedType.ALL)) || ((mentionedInfo.getType().equals(MentionedInfo.MentionedType.PART)) && (mentionedInfo.getMentionedUserIdList() != null) && (mentionedInfo.getMentionedUserIdList().contains(RongIMClient.getInstance().getCurrentUserId())))))
/*     */     {
/* 331 */       tempUIConversation.setMentionedFlag(true);
/*     */     }
/* 333 */     else tempUIConversation.setMentionedFlag(false);
/*     */ 
/* 336 */     return tempUIConversation;
/*     */   }
/*     */ 
/*     */   public SpannableStringBuilder buildConversationContent(UIConversation uiConversation) {
/* 340 */     boolean isGathered = uiConversation.getConversationGatherState();
/* 341 */     String type = uiConversation.getConversationType().getName();
/* 342 */     SpannableStringBuilder builder = new SpannableStringBuilder();
/*     */ 
/* 346 */     if (uiConversation.getMessageContent() == null) {
/* 347 */       builder.append("");
/* 348 */       return builder;
/*     */     }
/*     */ 
/* 351 */     ProviderTag providerTag = RongContext.getInstance().getMessageProviderTag(uiConversation.getMessageContent().getClass());
/* 352 */     IContainerItemProvider.MessageProvider messageProvider = RongContext.getInstance().getMessageTemplate(uiConversation.getMessageContent().getClass());
/*     */ 
/* 354 */     if ((providerTag == null) || (messageProvider == null)) {
/* 355 */       RLog.e("UIMessage", "Can not find ProviderTag");
/* 356 */       builder.append("");
/* 357 */       return builder;
/*     */     }
/* 359 */     Spannable messageData = messageProvider.getContentSummary(uiConversation.getMessageContent());
/* 360 */     boolean isShowName = providerTag.showSummaryWithName();
/* 361 */     if (messageData == null) {
/* 362 */       builder.append("");
/* 363 */       return builder;
/*     */     }
/*     */ 
/* 367 */     if ((uiConversation.getMessageContent() instanceof VoiceMessage)) {
/* 368 */       Conversation conv = RongIM.getInstance().getConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId());
/* 369 */       if (conv != null) {
/* 370 */         boolean isListened = conv.getReceivedStatus().isListened();
/* 371 */         if ((conv.getSenderUserId().equals(RongIMClient.getInstance().getCurrentUserId())) || (isListened))
/* 372 */           messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_text_color_secondary)), 0, messageData.length(), 33);
/*     */         else {
/* 374 */           messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_voice_color)), 0, messageData.length(), 33);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 379 */     if (!isShowName) {
/* 380 */       builder.append(messageData);
/* 381 */       return builder;
/*     */     }
/*     */ 
/* 384 */     if ((Conversation.ConversationType.GROUP.getName().equals(type)) || (Conversation.ConversationType.DISCUSSION.getName().equals(type))) {
/* 385 */       Conversation conv = RongIM.getInstance().getConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId());
/* 386 */       if ((conv != null) && 
/* 387 */         (conv.getSenderUserId().equals(RongIMClient.getInstance().getCurrentUserId()))) {
/* 388 */         return builder.append(messageData);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 394 */     String senderId = uiConversation.getConversationSenderId();
/* 395 */     if (isGathered) {
/* 396 */       String targetName = RongContext.getInstance().getConversationTemplate(type).getTitle(uiConversation.getConversationTargetId());
/*     */ 
/* 398 */       builder.append(targetName == null ? uiConversation.getConversationTargetId() : targetName).append(" : ").append(messageData);
/*     */     }
/* 401 */     else if (Conversation.ConversationType.GROUP.getName().equals(type))
/*     */     {
/* 403 */       GroupUserInfo info = RongUserInfoManager.getInstance().getGroupUserInfo(uiConversation.targetId, senderId);
/*     */       String senderName;
/*     */       String senderName;
/* 404 */       if (info != null) {
/* 405 */         senderName = info.getNickname();
/*     */       } else {
/* 407 */         UserInfo userInfo = uiConversation.getMessageContent().getUserInfo();
/*     */         String senderName;
/* 408 */         if ((userInfo == null) || (userInfo.getName() == null)) {
/* 409 */           senderName = RongContext.getInstance().getConversationTemplate(Conversation.ConversationType.PRIVATE.getName()).getTitle(senderId);
/*     */         }
/*     */         else
/*     */         {
/* 413 */           senderName = userInfo.getName();
/*     */         }
/*     */       }
/* 416 */       builder.append(senderName == null ? senderId : senderId == null ? "" : senderName).append(" : ").append(messageData);
/*     */     }
/* 419 */     else if (Conversation.ConversationType.DISCUSSION.getName().equals(type)) {
/* 420 */       String senderName = RongContext.getInstance().getConversationTemplate(Conversation.ConversationType.PRIVATE.getName()).getTitle(uiConversation.getConversationSenderId());
/*     */ 
/* 423 */       builder.append(senderName == null ? senderId : senderId == null ? "" : senderName).append(" : ").append(messageData);
/*     */     }
/*     */     else
/*     */     {
/* 427 */       return builder.append(messageData);
/*     */     }
/* 429 */     return builder;
/*     */   }
/*     */ 
/*     */   public void setUnreadType(UnreadRemindType type)
/*     */   {
/* 435 */     this.mUnreadType = type;
/*     */   }
/*     */ 
/*     */   public UnreadRemindType getUnReadType() {
/* 439 */     return this.mUnreadType;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 459 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/*     */   }
/*     */ 
/*     */   public static enum UnreadRemindType
/*     */   {
/* 446 */     NO_REMIND, 
/*     */ 
/* 450 */     REMIND_ONLY, 
/*     */ 
/* 454 */     REMIND_WITH_COUNTING;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.model.UIConversation
 * JD-Core Version:    0.6.0
 */
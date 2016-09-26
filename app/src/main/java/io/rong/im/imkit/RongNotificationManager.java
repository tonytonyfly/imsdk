/*     */ package io.rong.imkit;
/*     */ 
/*     */ import android.text.Spannable;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.model.ConversationKey;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Conversation.PublicServiceType;
/*     */ import io.rong.imlib.model.Discussion;
/*     */ import io.rong.imlib.model.Group;
/*     */ import io.rong.imlib.model.MentionedInfo;
/*     */ import io.rong.imlib.model.MentionedInfo.MentionedType;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import io.rong.imlib.model.PublicServiceProfile;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import io.rong.push.RongPushClient;
/*     */ import io.rong.push.RongPushClient.ConversationType;
/*     */ import io.rong.push.notification.PushNotificationMessage;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ public class RongNotificationManager
/*     */ {
/*     */   private static final String TAG = "RongNotificationManager";
/*  30 */   private static RongNotificationManager sS = new RongNotificationManager();
/*     */   RongContext mContext;
/*  27 */   ConcurrentHashMap<String, Message> messageMap = new ConcurrentHashMap();
/*     */ 
/*     */   public void init(RongContext context)
/*     */   {
/*  37 */     this.mContext = context;
/*  38 */     this.messageMap.clear();
/*  39 */     if (!context.getEventBus().isRegistered(this))
/*  40 */       context.getEventBus().register(this);
/*     */   }
/*     */ 
/*     */   public static RongNotificationManager getInstance()
/*     */   {
/*  45 */     if (sS == null) {
/*  46 */       sS = new RongNotificationManager();
/*     */     }
/*  48 */     return sS;
/*     */   }
/*     */ 
/*     */   public void onReceiveMessageFromApp(Message message) {
/*  52 */     Conversation.ConversationType type = message.getConversationType();
/*  53 */     String targetName = null;
/*  54 */     String userName = "";
/*     */ 
/*  57 */     IContainerItemProvider.MessageProvider provider = RongContext.getInstance().getMessageTemplate(message.getContent().getClass());
/*  58 */     if (provider == null) {
/*  59 */       return;
/*     */     }
/*  61 */     Spannable content = provider.getContentSummary(message.getContent());
/*  62 */     ConversationKey targetKey = ConversationKey.obtain(message.getTargetId(), message.getConversationType());
/*  63 */     if (targetKey == null) {
/*  64 */       RLog.e("RongNotificationManager", "onReceiveMessageFromApp targetKey is null");
/*     */     }
/*  66 */     RLog.i("RongNotificationManager", "onReceiveMessageFromApp start");
/*     */ 
/*  68 */     if (content == null) {
/*  69 */       RLog.i("RongNotificationManager", "onReceiveMessageFromApp Content is null. Return directly.");
/*  70 */       return;
/*     */     }
/*     */ 
/*  73 */     if ((type.equals(Conversation.ConversationType.PRIVATE)) || (type.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) || (type.equals(Conversation.ConversationType.CHATROOM)) || (type.equals(Conversation.ConversationType.SYSTEM)))
/*     */     {
/*  75 */       UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getTargetId());
/*  76 */       if (userInfo != null)
/*  77 */         targetName = userInfo.getName();
/*  78 */       if (!TextUtils.isEmpty(targetName)) {
/*  79 */         PushNotificationMessage pushMsg = new PushNotificationMessage();
/*  80 */         pushMsg.setPushContent(content.toString());
/*  81 */         pushMsg.setConversationType(RongPushClient.ConversationType.setValue(message.getConversationType().getValue()));
/*  82 */         pushMsg.setTargetId(message.getTargetId());
/*  83 */         pushMsg.setTargetUserName(targetName);
/*  84 */         pushMsg.setSenderId(message.getTargetId());
/*  85 */         pushMsg.setSenderName(targetName);
/*  86 */         pushMsg.setObjectName(message.getObjectName());
/*  87 */         pushMsg.setPushFlag("false");
/*  88 */         RongPushClient.sendNotification(this.mContext, pushMsg);
/*     */       }
/*  90 */       else if (targetKey != null) {
/*  91 */         this.messageMap.put(targetKey.getKey(), message);
/*     */       }
/*     */     }
/*  94 */     else if (type.equals(Conversation.ConversationType.GROUP)) {
/*  95 */       Group groupInfo = RongUserInfoManager.getInstance().getGroupInfo(message.getTargetId());
/*  96 */       UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
/*  97 */       if (groupInfo != null) {
/*  98 */         targetName = groupInfo.getName();
/*     */       }
/* 100 */       if (userInfo != null) {
/* 101 */         userName = userInfo.getName();
/*     */       }
/* 103 */       if ((!TextUtils.isEmpty(targetName)) && (!TextUtils.isEmpty(userName)))
/*     */       {
/*     */         String notificationContent;
/*     */         String notificationContent;
/* 105 */         if (isMentionedMessage(message))
/*     */         {
/*     */           String notificationContent;
/* 106 */           if (TextUtils.isEmpty(message.getContent().getMentionedInfo().getMentionedContent()))
/* 107 */             notificationContent = this.mContext.getString(R.string.rc_message_content_mentioned) + userName + " : " + content.toString();
/*     */           else
/* 109 */             notificationContent = message.getContent().getMentionedInfo().getMentionedContent();
/*     */         }
/*     */         else {
/* 112 */           notificationContent = userName + " : " + content.toString();
/*     */         }
/* 114 */         PushNotificationMessage pushMsg = new PushNotificationMessage();
/* 115 */         pushMsg.setPushContent(notificationContent);
/* 116 */         pushMsg.setConversationType(RongPushClient.ConversationType.setValue(message.getConversationType().getValue()));
/* 117 */         pushMsg.setTargetId(message.getTargetId());
/* 118 */         pushMsg.setTargetUserName(targetName);
/* 119 */         pushMsg.setObjectName(message.getObjectName());
/* 120 */         pushMsg.setPushFlag("false");
/* 121 */         RongPushClient.sendNotification(this.mContext, pushMsg);
/*     */       } else {
/* 123 */         if ((TextUtils.isEmpty(targetName)) && 
/* 124 */           (targetKey != null)) {
/* 125 */           this.messageMap.put(targetKey.getKey(), message);
/*     */         }
/*     */ 
/* 128 */         if (TextUtils.isEmpty(userName)) {
/* 129 */           ConversationKey senderKey = ConversationKey.obtain(message.getSenderUserId(), type);
/* 130 */           if (senderKey != null)
/* 131 */             this.messageMap.put(senderKey.getKey(), message);
/*     */           else
/* 133 */             RLog.e("RongNotificationManager", "onReceiveMessageFromApp senderKey is null");
/*     */         }
/*     */       }
/*     */     }
/* 137 */     else if (type.equals(Conversation.ConversationType.DISCUSSION)) {
/* 138 */       Discussion discussionInfo = RongUserInfoManager.getInstance().getDiscussionInfo(message.getTargetId());
/* 139 */       UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
/*     */ 
/* 141 */       if (discussionInfo != null) {
/* 142 */         targetName = discussionInfo.getName();
/*     */       }
/* 144 */       if (userInfo != null) {
/* 145 */         userName = userInfo.getName();
/*     */       }
/* 147 */       if ((!TextUtils.isEmpty(targetName)) && (!TextUtils.isEmpty(userName)))
/*     */       {
/*     */         String notificationContent;
/*     */         String notificationContent;
/* 149 */         if (isMentionedMessage(message))
/*     */         {
/*     */           String notificationContent;
/* 150 */           if (TextUtils.isEmpty(message.getContent().getMentionedInfo().getMentionedContent()))
/* 151 */             notificationContent = this.mContext.getString(R.string.rc_message_content_mentioned) + userName + " : " + content.toString();
/*     */           else
/* 153 */             notificationContent = message.getContent().getMentionedInfo().getMentionedContent();
/*     */         }
/*     */         else {
/* 156 */           notificationContent = userName + " : " + content.toString();
/*     */         }
/* 158 */         PushNotificationMessage pushMsg = new PushNotificationMessage();
/* 159 */         pushMsg.setPushContent(notificationContent);
/* 160 */         pushMsg.setConversationType(RongPushClient.ConversationType.setValue(message.getConversationType().getValue()));
/* 161 */         pushMsg.setTargetId(message.getTargetId());
/* 162 */         pushMsg.setTargetUserName(targetName);
/* 163 */         pushMsg.setObjectName(message.getObjectName());
/* 164 */         pushMsg.setPushFlag("false");
/* 165 */         RongPushClient.sendNotification(this.mContext, pushMsg);
/*     */       } else {
/* 167 */         if ((TextUtils.isEmpty(targetName)) && 
/* 168 */           (targetKey != null)) {
/* 169 */           this.messageMap.put(targetKey.getKey(), message);
/*     */         }
/*     */ 
/* 172 */         if (TextUtils.isEmpty(userName)) {
/* 173 */           ConversationKey senderKey = ConversationKey.obtain(message.getSenderUserId(), type);
/* 174 */           if (senderKey != null)
/* 175 */             this.messageMap.put(senderKey.getKey(), message);
/*     */           else
/* 177 */             RLog.e("RongNotificationManager", "onReceiveMessageFromApp senderKey is null");
/*     */         }
/*     */       }
/*     */     }
/* 181 */     else if ((type.getName().equals(Conversation.ConversationType.PUBLIC_SERVICE.getName())) || (type.getName().equals(Conversation.PublicServiceType.APP_PUBLIC_SERVICE.getName())))
/*     */     {
/* 183 */       if (targetKey != null) {
/* 184 */         PublicServiceProfile info = RongContext.getInstance().getPublicServiceInfoFromCache(targetKey.getKey());
/* 185 */         if (info != null) {
/* 186 */           targetName = info.getName();
/*     */         }
/*     */       }
/* 189 */       if (!TextUtils.isEmpty(targetName)) {
/* 190 */         PushNotificationMessage pushMsg = new PushNotificationMessage();
/* 191 */         pushMsg.setPushContent(content.toString());
/* 192 */         pushMsg.setConversationType(RongPushClient.ConversationType.setValue(message.getConversationType().getValue()));
/* 193 */         pushMsg.setTargetId(message.getTargetId());
/* 194 */         pushMsg.setTargetUserName(targetName);
/* 195 */         pushMsg.setObjectName(message.getObjectName());
/* 196 */         pushMsg.setPushFlag("false");
/* 197 */         RongPushClient.sendNotification(this.mContext, pushMsg);
/*     */       }
/* 199 */       else if (targetKey != null) {
/* 200 */         this.messageMap.put(targetKey.getKey(), message);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(UserInfo userInfo)
/*     */   {
/* 210 */     Conversation.ConversationType[] types = { Conversation.ConversationType.PRIVATE, Conversation.ConversationType.GROUP, Conversation.ConversationType.DISCUSSION, Conversation.ConversationType.CUSTOMER_SERVICE, Conversation.ConversationType.CHATROOM, Conversation.ConversationType.SYSTEM };
/*     */ 
/* 216 */     for (Conversation.ConversationType type : types) {
/* 217 */       String key = ConversationKey.obtain(userInfo.getUserId(), type).getKey();
/*     */ 
/* 219 */       if (this.messageMap.containsKey(key)) {
/* 220 */         Message message = (Message)this.messageMap.get(key);
/* 221 */         String targetName = "";
/* 222 */         String notificationContent = "";
/* 223 */         Spannable content = RongContext.getInstance().getMessageTemplate(message.getContent().getClass()).getContentSummary(message.getContent());
/*     */ 
/* 226 */         this.messageMap.remove(key);
/*     */ 
/* 228 */         if (type.equals(Conversation.ConversationType.GROUP)) {
/* 229 */           Group groupInfo = RongUserInfoManager.getInstance().getGroupInfo(message.getTargetId());
/* 230 */           if (groupInfo != null) {
/* 231 */             targetName = groupInfo.getName();
/*     */           }
/*     */ 
/* 234 */           if (isMentionedMessage(message)) {
/* 235 */             if (TextUtils.isEmpty(message.getContent().getMentionedInfo().getMentionedContent()))
/* 236 */               notificationContent = this.mContext.getString(R.string.rc_message_content_mentioned) + userInfo.getName() + " : " + content.toString();
/*     */             else
/* 238 */               notificationContent = message.getContent().getMentionedInfo().getMentionedContent();
/*     */           }
/*     */           else
/* 241 */             notificationContent = userInfo.getName() + " : " + content.toString();
/*     */         }
/* 243 */         else if (type.equals(Conversation.ConversationType.DISCUSSION)) {
/* 244 */           Discussion discussion = RongUserInfoManager.getInstance().getDiscussionInfo(message.getTargetId());
/* 245 */           if (discussion != null) {
/* 246 */             targetName = discussion.getName();
/*     */           }
/* 248 */           if (isMentionedMessage(message)) {
/* 249 */             if (TextUtils.isEmpty(message.getContent().getMentionedInfo().getMentionedContent()))
/* 250 */               notificationContent = this.mContext.getString(R.string.rc_message_content_mentioned) + userInfo.getName() + " : " + content.toString();
/*     */             else
/* 252 */               notificationContent = message.getContent().getMentionedInfo().getMentionedContent();
/*     */           }
/*     */           else
/* 255 */             notificationContent = userInfo.getName() + " : " + content.toString();
/*     */         }
/*     */         else {
/* 258 */           targetName = userInfo.getName();
/* 259 */           notificationContent = content.toString();
/*     */         }
/* 261 */         if (TextUtils.isEmpty(targetName)) {
/* 262 */           return;
/*     */         }
/* 264 */         PushNotificationMessage pushMsg = new PushNotificationMessage();
/* 265 */         pushMsg.setPushContent(notificationContent);
/* 266 */         pushMsg.setConversationType(RongPushClient.ConversationType.setValue(message.getConversationType().getValue()));
/* 267 */         pushMsg.setTargetId(message.getTargetId());
/* 268 */         pushMsg.setTargetUserName(targetName);
/* 269 */         pushMsg.setObjectName(message.getObjectName());
/* 270 */         pushMsg.setPushFlag("false");
/* 271 */         RongPushClient.sendNotification(this.mContext, pushMsg);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Group groupInfo)
/*     */   {
/* 279 */     String key = ConversationKey.obtain(groupInfo.getId(), Conversation.ConversationType.GROUP).getKey();
/*     */ 
/* 281 */     if (this.messageMap.containsKey(key)) {
/* 282 */       Message message = (Message)this.messageMap.get(key);
/* 283 */       String userName = "";
/* 284 */       Spannable content = RongContext.getInstance().getMessageTemplate(message.getContent().getClass()).getContentSummary(message.getContent());
/*     */ 
/* 287 */       this.messageMap.remove(key);
/*     */ 
/* 289 */       UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
/* 290 */       if (userInfo != null) {
/* 291 */         userName = userInfo.getName();
/* 292 */         if (TextUtils.isEmpty(userName)) {
/* 293 */           return;
/*     */         }
/*     */       }
/* 296 */       PushNotificationMessage pushMsg = new PushNotificationMessage();
/* 297 */       pushMsg.setPushContent(userName + " : " + content.toString());
/* 298 */       pushMsg.setConversationType(RongPushClient.ConversationType.setValue(message.getConversationType().getValue()));
/* 299 */       pushMsg.setTargetId(message.getTargetId());
/* 300 */       pushMsg.setTargetUserName(groupInfo.getName());
/* 301 */       pushMsg.setObjectName(message.getObjectName());
/* 302 */       pushMsg.setPushFlag("false");
/* 303 */       RongPushClient.sendNotification(this.mContext, pushMsg);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Discussion discussion)
/*     */   {
/* 311 */     String key = ConversationKey.obtain(discussion.getId(), Conversation.ConversationType.DISCUSSION).getKey();
/* 312 */     if (this.messageMap.containsKey(key)) {
/* 313 */       String userName = "";
/* 314 */       Message message = (Message)this.messageMap.get(key);
/* 315 */       Spannable content = RongContext.getInstance().getMessageTemplate(message.getContent().getClass()).getContentSummary(message.getContent());
/*     */ 
/* 318 */       this.messageMap.remove(key);
/*     */ 
/* 320 */       UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
/* 321 */       if (userInfo != null) {
/* 322 */         userName = userInfo.getName();
/* 323 */         if (TextUtils.isEmpty(userName)) {
/* 324 */           return;
/*     */         }
/*     */       }
/* 327 */       PushNotificationMessage pushMsg = new PushNotificationMessage();
/* 328 */       pushMsg.setPushContent(userName + " : " + content.toString());
/* 329 */       pushMsg.setConversationType(RongPushClient.ConversationType.setValue(message.getConversationType().getValue()));
/* 330 */       pushMsg.setTargetId(message.getTargetId());
/* 331 */       pushMsg.setTargetUserName(discussion.getName());
/* 332 */       pushMsg.setObjectName(message.getObjectName());
/* 333 */       pushMsg.setPushFlag("false");
/* 334 */       RongPushClient.sendNotification(this.mContext, pushMsg);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(PublicServiceProfile info)
/*     */   {
/* 341 */     String key = ConversationKey.obtain(info.getTargetId(), info.getConversationType()).getKey();
/*     */ 
/* 343 */     if (this.messageMap.containsKey(key)) {
/* 344 */       Message message = (Message)this.messageMap.get(key);
/* 345 */       Spannable content = RongContext.getInstance().getMessageTemplate(message.getContent().getClass()).getContentSummary(message.getContent());
/*     */ 
/* 348 */       PushNotificationMessage pushMsg = new PushNotificationMessage();
/* 349 */       pushMsg.setPushContent(content.toString());
/* 350 */       pushMsg.setConversationType(RongPushClient.ConversationType.setValue(message.getConversationType().getValue()));
/* 351 */       pushMsg.setTargetId(message.getTargetId());
/* 352 */       pushMsg.setTargetUserName(info.getName());
/* 353 */       pushMsg.setObjectName(message.getObjectName());
/* 354 */       pushMsg.setPushFlag("false");
/* 355 */       RongPushClient.sendNotification(this.mContext, pushMsg);
/* 356 */       this.messageMap.remove(key);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isMentionedMessage(Message message) {
/* 361 */     MentionedInfo mentionedInfo = message.getContent().getMentionedInfo();
/*     */ 
/* 366 */     return (mentionedInfo != null) && ((mentionedInfo.getType().equals(MentionedInfo.MentionedType.ALL)) || ((mentionedInfo.getType().equals(MentionedInfo.MentionedType.PART)) && (mentionedInfo.getMentionedUserIdList() != null) && (mentionedInfo.getMentionedUserIdList().contains(RongIMClient.getInstance().getCurrentUserId()))));
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.RongNotificationManager
 * JD-Core Version:    0.6.0
 */
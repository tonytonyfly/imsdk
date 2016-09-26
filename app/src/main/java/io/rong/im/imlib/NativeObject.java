/*      */ package io.rong.imlib;
/*      */ 
/*      */ import org.json.JSONObject;
/*      */ 
/*      */ public class NativeObject
/*      */ {
/*      */   NativeObject()
/*      */   {
/*   12 */     setJNIEnv(this);
/*      */   }
/*      */ 
/*      */   protected native void setJNIEnv(NativeObject paramNativeObject);
/*      */ 
/*      */   protected native int InitClient(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5);
/*      */ 
/*      */   protected native void RegisterMessageType(String paramString, int paramInt);
/*      */ 
/*      */   protected native void Connect(String paramString1, String paramString2, int paramInt, ConnectAckCallback paramConnectAckCallback, boolean paramBoolean);
/*      */ 
/*      */   protected native void Disconnect(int paramInt);
/*      */ 
/*      */   protected native boolean DeleteMessages(int[] paramArrayOfInt);
/*      */ 
/*      */   protected native boolean ClearMessages(int paramInt, String paramString, boolean paramBoolean);
/*      */ 
/*      */   protected native boolean ClearUnread(int paramInt, String paramString);
/*      */ 
/*      */   protected native boolean SetMessageExtra(int paramInt, String paramString);
/*      */ 
/*      */   protected native boolean RemoveConversation(int paramInt, String paramString);
/*      */ 
/*      */   protected native boolean SetTextMessageDraft(int paramInt, String paramString1, String paramString2);
/*      */ 
/*      */   protected native boolean SetMessageContent(int paramInt, byte[] paramArrayOfByte, String paramString);
/*      */ 
/*      */   protected native String GetTextMessageDraft(int paramInt, String paramString);
/*      */ 
/*      */   protected native boolean SetIsTop(int paramInt, String paramString, boolean paramBoolean);
/*      */ 
/*      */   protected native int GetTotalUnreadCount();
/*      */ 
/*      */   protected native long GetDeltaTime();
/*      */ 
/*      */   protected native void CreateInviteDiscussion(String paramString, String[] paramArrayOfString, CreateDiscussionCallback paramCreateDiscussionCallback);
/*      */ 
/*      */   protected native void InviteMemberToDiscussion(String paramString, String[] paramArrayOfString, PublishAckListener paramPublishAckListener);
/*      */ 
/*      */   protected native void RemoveMemberFromDiscussion(String paramString1, String paramString2, PublishAckListener paramPublishAckListener);
/*      */ 
/*      */   protected native void QuitDiscussion(String paramString, PublishAckListener paramPublishAckListener);
/*      */ 
/*      */   protected native int SaveMessage(String paramString1, int paramInt1, String paramString2, String paramString3, byte[] paramArrayOfByte, boolean paramBoolean, int paramInt2, int paramInt3, long paramLong);
/*      */ 
/*      */   protected native void SendMessage(String paramString1, int paramInt1, int paramInt2, String paramString2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt3, String[] paramArrayOfString, PublishAckListener paramPublishAckListener, boolean paramBoolean);
/*      */ 
/*      */   protected native UserInfo GetUserInfoExSync(String paramString, int paramInt);
/*      */ 
/*      */   protected native void SetMessageListener(ReceiveMessageListener paramReceiveMessageListener);
/*      */ 
/*      */   protected native boolean SetReadStatus(int paramInt1, int paramInt2);
/*      */ 
/*      */   protected native boolean SetSendStatus(int paramInt1, int paramInt2);
/*      */ 
/*      */   protected native void EnvironmentChangeNotify(int paramInt);
/*      */ 
/*      */   protected native void GetDiscussionInfo(String paramString, DiscussionInfoListener paramDiscussionInfoListener);
/*      */ 
/*      */   protected native DiscussionInfo GetDiscussionInfoSync(String paramString);
/*      */ 
/*      */   protected native void SetExceptionListener(ExceptionListener paramExceptionListener);
/*      */ 
/*      */   protected native void RenameDiscussion(String paramString1, String paramString2, PublishAckListener paramPublishAckListener);
/*      */ 
/*      */   protected native Conversation GetConversationEx(String paramString, int paramInt);
/*      */ 
/*      */   protected native void SetBlockPush(String paramString, int paramInt, boolean paramBoolean, BizAckListener paramBizAckListener);
/*      */ 
/*      */   protected native void GetBlockPush(String paramString, int paramInt, BizAckListener paramBizAckListener);
/*      */ 
/*      */   protected native void SetInviteStatus(String paramString, int paramInt, PublishAckListener paramPublishAckListener);
/*      */ 
/*      */   protected native int GetUnreadCount(String paramString, int paramInt);
/*      */ 
/*      */   protected native int GetMessageCount(String paramString, int paramInt);
/*      */ 
/*      */   protected native Conversation[] GetConversationListEx(int[] paramArrayOfInt);
/*      */ 
/*      */   protected native void SyncGroups(String[] paramArrayOfString1, String[] paramArrayOfString2, PublishAckListener paramPublishAckListener);
/*      */ 
/*      */   protected native void JoinGroup(String paramString1, String paramString2, PublishAckListener paramPublishAckListener);
/*      */ 
/*      */   protected native void QuitGroup(String paramString, PublishAckListener paramPublishAckListener);
/*      */ 
/*      */   protected native int GetCateUnreadCount(int[] paramArrayOfInt);
/*      */ 
/*      */   protected native void JoinChatRoom(String paramString, int paramInt1, int paramInt2, boolean paramBoolean, PublishAckListener paramPublishAckListener);
/*      */ 
/*      */   protected native void JoinExistingChatroom(String paramString, int paramInt1, int paramInt2, PublishAckListener paramPublishAckListener);
/*      */ 
/*      */   protected native void QuitChatRoom(String paramString, int paramInt, PublishAckListener paramPublishAckListener);
/*      */ 
/*      */   protected native boolean ClearConversations(int[] paramArrayOfInt);
/*      */ 
/*      */   protected native void AddToBlacklist(String paramString, PublishAckListener paramPublishAckListener);
/*      */ 
/*      */   protected native void RemoveFromBlacklist(String paramString, PublishAckListener paramPublishAckListener);
/*      */ 
/*      */   protected native void GetBlacklistStatus(String paramString, BizAckListener paramBizAckListener);
/*      */ 
/*      */   protected native void GetBlacklist(SetBlacklistListener paramSetBlacklistListener);
/*      */ 
/*      */   protected native void GetUploadToken(int paramInt, TokenListener paramTokenListener);
/*      */ 
/*      */   protected native void GetDownloadUrl(int paramInt, String paramString1, String paramString2, TokenListener paramTokenListener);
/*      */ 
/*      */   protected native void SubscribeAccount(String paramString, int paramInt, boolean paramBoolean, PublishAckListener paramPublishAckListener);
/*      */ 
/*      */   protected native void SetDeviceInfo(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5);
/*      */ 
/*      */   protected native void SearchAccount(String paramString, int paramInt1, int paramInt2, AccountInfoListener paramAccountInfoListener);
/*      */ 
/*      */   protected native void LoadHistoryMessage(String paramString, int paramInt1, long paramLong, int paramInt2, HistoryMessageListener paramHistoryMessageListener);
/*      */ 
/*      */   protected void ping()
/*      */   {
/* 1007 */     EnvironmentChangeNotify(105);
/*      */   }
/*      */ 
/*      */   protected void networkUnavailable() {
/* 1011 */     EnvironmentChangeNotify(101);
/*      */   }
/*      */ 
/*      */   protected native AccountInfo[] LoadAccountInfo();
/*      */ 
/*      */   protected native void AddPushSetting(String paramString, int paramInt, PublishAckListener paramPublishAckListener);
/*      */ 
/*      */   protected native void RemovePushSetting(PublishAckListener paramPublishAckListener);
/*      */ 
/*      */   protected native void QueryPushSetting(PushSettingListener paramPushSettingListener);
/*      */ 
/*      */   protected native void SetUserData(String paramString, PublishAckListener paramPublishAckListener);
/*      */ 
/*      */   protected native void GetUserData(GetUserDataListener paramGetUserDataListener);
/*      */ 
/*      */   protected native byte[] GetLatestMessagesbyObjectName(String paramString1, int paramInt1, String paramString2, int paramInt2);
/*      */ 
/*      */   protected native Message[] GetHistoryMessagesEx(String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3, boolean paramBoolean);
/*      */ 
/*      */   protected native String GetUserIdByToken(String paramString);
/*      */ 
/*      */   protected native long GetSendTimeByMessageId(int paramInt);
/*      */ 
/*      */   protected native Message GetMessageById(int paramInt);
/*      */ 
/*      */   protected native Message GetMessageByUId(String paramString);
/*      */ 
/*      */   protected native boolean SetReceiptStatus(String paramString1, int paramInt, long paramLong, String paramString2);
/*      */ 
/*      */   protected native ReceiptInfo[] QueryReceiptStatus();
/*      */ 
/*      */   protected native boolean UpdateMessageReceiptStatus(String paramString, int paramInt, long paramLong);
/*      */ 
/*      */   protected native boolean ClearUnreadByReceipt(String paramString, int paramInt, long paramLong);
/*      */ 
/*      */   protected native boolean UpdateConversationInfo(String paramString1, int paramInt, String paramString2, String paramString3);
/*      */ 
/*      */   protected native boolean QueryChatroomInfo(String paramString, int paramInt1, int paramInt2, ChatroomInfoListener paramChatroomInfoListener);
/*      */ 
/*      */   protected native boolean RecallMessage(String paramString);
/*      */ 
/*      */   protected native void GetVoIPKey(int paramInt, String paramString1, String paramString2, TokenListener paramTokenListener);
/*      */ 
/*      */   protected native void GetAuthConfig(TokenListener paramTokenListener);
/*      */ 
/*      */   protected native Message[] GetMentionMessages(String paramString, int paramInt);
/*      */ 
/*      */   protected native void SetLogStatus(boolean paramBoolean);
/*      */ 
/*      */   protected native boolean SetRRReqStatus(String paramString, int paramInt);
/*      */ 
/*      */   protected native int UpdateRRRspUserList(String paramString1, String paramString2);
/*      */ 
/*      */   protected native boolean UpdateReadReceiptRequestInfo(String paramString1, String paramString2);
/*      */ 
/*      */   protected native void RegisterCmdMsgType(String[] paramArrayOfString);
/*      */ 
/*      */   static
/*      */   {
/*    8 */     System.loadLibrary("RongIMLib");
/*      */   }
/*      */ 
/*      */   public static class ReceiptInfo
/*      */   {
/*      */     private byte[] targetId;
/*      */     private long timestamp;
/*      */ 
/*      */     public byte[] getTargetId()
/*      */     {
/*  669 */       return this.targetId;
/*      */     }
/*      */ 
/*      */     public void setTargetId(byte[] targetId) {
/*  673 */       this.targetId = targetId;
/*      */     }
/*      */ 
/*      */     public long getTimestamp() {
/*  677 */       return this.timestamp;
/*      */     }
/*      */ 
/*      */     public void setTimestamp(long timestamp) {
/*  681 */       this.timestamp = timestamp;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class AccountInfo
/*      */   {
/*      */     private byte[] accountId;
/*      */     private byte[] accountName;
/*      */     private byte[] accountUri;
/*      */     private byte[] extra;
/*      */     private int accountType;
/*      */ 
/*      */     public byte[] getAccountId()
/*      */     {
/*  624 */       return this.accountId;
/*      */     }
/*      */ 
/*      */     public void setAccountId(byte[] accountId) {
/*  628 */       this.accountId = accountId;
/*      */     }
/*      */ 
/*      */     public byte[] getAccountName() {
/*  632 */       return this.accountName;
/*      */     }
/*      */ 
/*      */     public void setAccountName(byte[] accountName) {
/*  636 */       this.accountName = accountName;
/*      */     }
/*      */ 
/*      */     public byte[] getAccountUri() {
/*  640 */       return this.accountUri;
/*      */     }
/*      */ 
/*      */     public void setAccountUri(byte[] accountUri) {
/*  644 */       this.accountUri = accountUri;
/*      */     }
/*      */ 
/*      */     public byte[] getExtra() {
/*  648 */       return this.extra;
/*      */     }
/*      */ 
/*      */     public void setExtra(byte[] extra) {
/*  652 */       this.extra = extra;
/*      */     }
/*      */ 
/*      */     public int getAccountType() {
/*  656 */       return this.accountType;
/*      */     }
/*      */ 
/*      */     public void setAccountType(int accountType) {
/*  660 */       this.accountType = accountType;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class DiscussionInfo
/*      */   {
/*      */     private String discussionId;
/*      */     private String discussionName;
/*      */     private String adminId;
/*      */     private String userIds;
/*      */     private int inviteStatus;
/*      */ 
/*      */     public String getDiscussionId()
/*      */     {
/*  575 */       return this.discussionId;
/*      */     }
/*      */ 
/*      */     public void setDiscussionId(String discussionId) {
/*  579 */       this.discussionId = discussionId;
/*      */     }
/*      */ 
/*      */     public String getDiscussionName() {
/*  583 */       return this.discussionName;
/*      */     }
/*      */ 
/*      */     public void setDiscussionName(byte[] data) {
/*  587 */       this.discussionName = new String(data);
/*      */     }
/*      */ 
/*      */     public String getAdminId() {
/*  591 */       return this.adminId;
/*      */     }
/*      */ 
/*      */     public void setAdminId(String adminId) {
/*  595 */       this.adminId = adminId;
/*      */     }
/*      */ 
/*      */     public String getUserIds() {
/*  599 */       return this.userIds;
/*      */     }
/*      */ 
/*      */     public void setUserIds(String userIds) {
/*  603 */       this.userIds = userIds;
/*      */     }
/*      */ 
/*      */     public int getInviteStatus() {
/*  607 */       return this.inviteStatus;
/*      */     }
/*      */ 
/*      */     public void setInviteStatus(int inviteStatus) {
/*  611 */       this.inviteStatus = inviteStatus;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class Conversation
/*      */   {
/*      */     private int conversationType;
/*      */     private String targetId;
/*      */     private String conversationTitle;
/*      */     private boolean isTop;
/*      */     private String draft;
/*      */     private int unreadMessageCount;
/*      */     private String objectName;
/*      */     private int messageId;
/*      */     private int readStatus;
/*      */     private int receiveStatus;
/*      */     private int sentStatus;
/*      */     private long ReceivedTime;
/*      */     private long sentTime;
/*      */     private String senderUserId;
/*      */     private String senderName;
/*      */     private boolean messageDirection;
/*      */     private String messageContent;
/*      */     private boolean blockPush;
/*      */     private long lastTime;
/*      */     private String userId;
/*      */     private String userName;
/*      */     private String userPortrait;
/*      */     private byte[] content;
/*      */     private String extra;
/*      */     private String portraitUrl;
/*      */     private String UId;
/*      */     private int mentionCount;
/*      */ 
/*      */     public Conversation(String jsonObj)
/*      */     {
/*      */     }
/*      */ 
/*      */     public Conversation()
/*      */     {
/*      */     }
/*      */ 
/*      */     public String getUId()
/*      */     {
/*  347 */       return this.UId;
/*      */     }
/*      */ 
/*      */     public void setUId(String UId) {
/*  351 */       this.UId = UId;
/*      */     }
/*      */ 
/*      */     public long getSentTime() {
/*  355 */       return this.sentTime;
/*      */     }
/*      */ 
/*      */     public void setSentTime(long sentTime) {
/*  359 */       this.sentTime = sentTime;
/*      */     }
/*      */ 
/*      */     public String getSenderUserId() {
/*  363 */       return this.senderUserId;
/*      */     }
/*      */ 
/*      */     public void setSenderUserId(String senderUserId) {
/*  367 */       this.senderUserId = senderUserId;
/*      */     }
/*      */ 
/*      */     public boolean isMessageDirection() {
/*  371 */       return this.messageDirection;
/*      */     }
/*      */ 
/*      */     public void setMessageDirection(boolean messageDirection) {
/*  375 */       this.messageDirection = messageDirection;
/*      */     }
/*      */ 
/*      */     public void setIsTop(boolean isTop) {
/*  379 */       this.isTop = isTop;
/*      */     }
/*      */ 
/*      */     public int getConversationType() {
/*  383 */       return this.conversationType;
/*      */     }
/*      */ 
/*      */     public void setConversationType(int conversationType) {
/*  387 */       this.conversationType = conversationType;
/*      */     }
/*      */ 
/*      */     public String getTargetId() {
/*  391 */       return this.targetId;
/*      */     }
/*      */ 
/*      */     public void setTargetId(String targetId) {
/*  395 */       this.targetId = targetId;
/*      */     }
/*      */ 
/*      */     public String getConversationTitle() {
/*  399 */       return this.conversationTitle;
/*      */     }
/*      */ 
/*      */     public void setConversationTitle(byte[] conversationTitle) {
/*  403 */       this.conversationTitle = new String(conversationTitle);
/*      */     }
/*      */ 
/*      */     public boolean isTop() {
/*  407 */       return this.isTop;
/*      */     }
/*      */ 
/*      */     public void setTop(boolean isTop) {
/*  411 */       this.isTop = isTop;
/*      */     }
/*      */ 
/*      */     public String getDraft() {
/*  415 */       return this.draft;
/*      */     }
/*      */ 
/*      */     public void setDraft(String draft) {
/*  419 */       this.draft = draft;
/*      */     }
/*      */ 
/*      */     public int getUnreadMessageCount() {
/*  423 */       return this.unreadMessageCount;
/*      */     }
/*      */ 
/*      */     public void setUnreadMessageCount(int unreadMessageCount) {
/*  427 */       this.unreadMessageCount = unreadMessageCount;
/*      */     }
/*      */ 
/*      */     public String getObjectName() {
/*  431 */       return this.objectName;
/*      */     }
/*      */ 
/*      */     public void setObjectName(String objectName) {
/*  435 */       this.objectName = objectName;
/*      */     }
/*      */ 
/*      */     public int getMessageId() {
/*  439 */       return this.messageId;
/*      */     }
/*      */ 
/*      */     public void setMessageId(int messageId) {
/*  443 */       this.messageId = messageId;
/*      */     }
/*      */ 
/*      */     public int getReceiveStatus() {
/*  447 */       return this.receiveStatus;
/*      */     }
/*      */ 
/*      */     public void setReceiveStatus(int receiveStatus) {
/*  451 */       this.receiveStatus = receiveStatus;
/*      */     }
/*      */ 
/*      */     public int getSentStatus() {
/*  455 */       return this.sentStatus;
/*      */     }
/*      */ 
/*      */     public void setSentStatus(int sentStatus) {
/*  459 */       this.sentStatus = sentStatus;
/*      */     }
/*      */ 
/*      */     public long getReceivedTime() {
/*  463 */       return this.ReceivedTime;
/*      */     }
/*      */ 
/*      */     public void setReceivedTime(long receivedTime) {
/*  467 */       this.ReceivedTime = receivedTime;
/*      */     }
/*      */ 
/*      */     public String getSenderName() {
/*  471 */       return this.senderName;
/*      */     }
/*      */ 
/*      */     public void setSenderName(String senderName) {
/*  475 */       this.senderName = senderName;
/*      */     }
/*      */ 
/*      */     public String getMessageContent() {
/*  479 */       return this.messageContent;
/*      */     }
/*      */ 
/*      */     public void setMessageContent(String messageContent) {
/*  483 */       this.messageContent = messageContent;
/*      */     }
/*      */ 
/*      */     public boolean isBlockPush() {
/*  487 */       return this.blockPush;
/*      */     }
/*      */ 
/*      */     public void setBlockPush(boolean blockPush) {
/*  491 */       this.blockPush = blockPush;
/*      */     }
/*      */ 
/*      */     public long getLastTime() {
/*  495 */       return this.lastTime;
/*      */     }
/*      */ 
/*      */     public void setLastTime(long lastTime) {
/*  499 */       this.lastTime = lastTime;
/*      */     }
/*      */ 
/*      */     public String getUserId() {
/*  503 */       return this.userId;
/*      */     }
/*      */ 
/*      */     public void setUserId(String userId) {
/*  507 */       this.userId = userId;
/*      */     }
/*      */ 
/*      */     public String getUserName() {
/*  511 */       return this.userName;
/*      */     }
/*      */ 
/*      */     public void setUserName(String userName) {
/*  515 */       this.userName = userName;
/*      */     }
/*      */ 
/*      */     public String getUserPortrait() {
/*  519 */       return this.userPortrait;
/*      */     }
/*      */ 
/*      */     public void setUserPortrait(String userPortrait) {
/*  523 */       this.userPortrait = userPortrait;
/*      */     }
/*      */ 
/*      */     public int getReadStatus() {
/*  527 */       return this.readStatus;
/*      */     }
/*      */ 
/*      */     public void setReadStatus(int readStatus) {
/*  531 */       this.readStatus = readStatus;
/*      */     }
/*      */ 
/*      */     public byte[] getContent() {
/*  535 */       return this.content;
/*      */     }
/*      */ 
/*      */     public void setContent(byte[] content) {
/*  539 */       this.content = content;
/*      */     }
/*      */ 
/*      */     public String getExtra() {
/*  543 */       return this.extra;
/*      */     }
/*      */ 
/*      */     public void setExtra(String extra) {
/*  547 */       this.extra = extra;
/*      */     }
/*      */ 
/*      */     public String getPortraitUrl() {
/*  551 */       return this.portraitUrl;
/*      */     }
/*      */ 
/*      */     public void setPortraitUrl(String portraitUrl) {
/*  555 */       this.portraitUrl = portraitUrl;
/*      */     }
/*      */ 
/*      */     public int getMentionCount() {
/*  559 */       return this.mentionCount;
/*      */     }
/*      */ 
/*      */     public void setMentionCount(int mentionCount) {
/*  563 */       this.mentionCount = mentionCount;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class UserInfo
/*      */   {
/*      */     private String userId;
/*      */     private int categoryId;
/*      */     private String userName;
/*      */     private String url;
/*      */     private String accountExtra;
/*      */     private long joinTime;
/*      */ 
/*      */     public String getUserId()
/*      */     {
/*  260 */       return this.userId;
/*      */     }
/*      */ 
/*      */     public void setUserId(String userId) {
/*  264 */       this.userId = userId;
/*      */     }
/*      */ 
/*      */     public int getCategoryId() {
/*  268 */       return this.categoryId;
/*      */     }
/*      */ 
/*      */     public void setCategoryId(int categoryId) {
/*  272 */       this.categoryId = categoryId;
/*      */     }
/*      */ 
/*      */     public String getUserName() {
/*  276 */       return this.userName;
/*      */     }
/*      */ 
/*      */     public void setUserName(String userName) {
/*  280 */       this.userName = userName;
/*      */     }
/*      */ 
/*      */     public String getUrl() {
/*  284 */       return this.url;
/*      */     }
/*      */ 
/*      */     public void setUrl(String url) {
/*  288 */       this.url = url;
/*      */     }
/*      */ 
/*      */     public String getAccountExtra() {
/*  292 */       return this.accountExtra;
/*      */     }
/*      */ 
/*      */     public void setAccountExtra(String accountExtra) {
/*  296 */       this.accountExtra = accountExtra;
/*      */     }
/*      */ 
/*      */     public long getJoinTime() {
/*  300 */       return this.joinTime;
/*      */     }
/*      */ 
/*      */     public void setJoinTime(long joinTime) {
/*  304 */       this.joinTime = joinTime;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class Message
/*      */   {
/*      */     private int conversationType;
/*      */     private String targetId;
/*      */     private int messageId;
/*      */     private boolean messageDirection;
/*      */     private String senderUserId;
/*      */     private int readStatus;
/*      */     private int sentStatus;
/*      */     private long receivedTime;
/*      */     private long sentTime;
/*      */     private String objectName;
/*      */     private byte[] content;
/*      */     private String extra;
/*      */     private String pushContent;
/*      */     private String UId;
/*      */     private String readReceiptInfo;
/*      */ 
/*      */     public Message(JSONObject jsonObj)
/*      */     {
/*  112 */       this.conversationType = jsonObj.optInt("conversation_category");
/*  113 */       this.targetId = jsonObj.optString("target_id");
/*  114 */       this.messageId = jsonObj.optInt("id");
/*  115 */       this.messageDirection = jsonObj.optBoolean("message_direction");
/*  116 */       this.senderUserId = jsonObj.optString("sender_user_id");
/*  117 */       this.readStatus = jsonObj.optInt("read_status");
/*  118 */       this.sentStatus = jsonObj.optInt("send_status");
/*  119 */       this.receivedTime = jsonObj.optLong("receive_time");
/*  120 */       this.sentTime = jsonObj.optLong("send_time");
/*  121 */       this.objectName = jsonObj.optString("object_name");
/*  122 */       this.content = jsonObj.optString("content").getBytes();
/*  123 */       this.extra = jsonObj.optString("extra");
/*  124 */       this.pushContent = jsonObj.optString("push");
/*      */     }
/*      */ 
/*      */     public Message() {
/*      */     }
/*      */ 
/*      */     public String getUId() {
/*  131 */       return this.UId;
/*      */     }
/*      */ 
/*      */     public void setUId(String UId) {
/*  135 */       this.UId = UId;
/*      */     }
/*      */ 
/*      */     public String getPushContent() {
/*  139 */       return this.pushContent;
/*      */     }
/*      */ 
/*      */     public void setPushContent(String pushContent) {
/*  143 */       this.pushContent = pushContent;
/*      */     }
/*      */ 
/*      */     public int getConversationType() {
/*  147 */       return this.conversationType;
/*      */     }
/*      */ 
/*      */     public void setConversationType(int conversationType) {
/*  151 */       this.conversationType = conversationType;
/*      */     }
/*      */ 
/*      */     public String getTargetId() {
/*  155 */       return this.targetId;
/*      */     }
/*      */ 
/*      */     public void setTargetId(String targetId) {
/*  159 */       this.targetId = targetId;
/*      */     }
/*      */ 
/*      */     public int getMessageId() {
/*  163 */       return this.messageId;
/*      */     }
/*      */ 
/*      */     public void setMessageId(int messageId) {
/*  167 */       this.messageId = messageId;
/*      */     }
/*      */ 
/*      */     public boolean getMessageDirection() {
/*  171 */       return this.messageDirection;
/*      */     }
/*      */ 
/*      */     public void setMessageDirection(boolean messageDirection) {
/*  175 */       this.messageDirection = messageDirection;
/*      */     }
/*      */ 
/*      */     public int getReadStatus() {
/*  179 */       return this.readStatus;
/*      */     }
/*      */ 
/*      */     public void setReadStatus(int readStatus) {
/*  183 */       this.readStatus = readStatus;
/*      */     }
/*      */ 
/*      */     public int getSentStatus() {
/*  187 */       return this.sentStatus;
/*      */     }
/*      */ 
/*      */     public void setSentStatus(int sentStatus) {
/*  191 */       this.sentStatus = sentStatus;
/*      */     }
/*      */ 
/*      */     public long getReceivedTime() {
/*  195 */       return this.receivedTime;
/*      */     }
/*      */ 
/*      */     public void setReceivedTime(long receivedTime) {
/*  199 */       this.receivedTime = receivedTime;
/*      */     }
/*      */ 
/*      */     public long getSentTime() {
/*  203 */       return this.sentTime;
/*      */     }
/*      */ 
/*      */     public void setSentTime(long sentTime) {
/*  207 */       this.sentTime = sentTime;
/*      */     }
/*      */ 
/*      */     public String getObjectName() {
/*  211 */       return this.objectName;
/*      */     }
/*      */ 
/*      */     public void setObjectName(String objectName) {
/*  215 */       this.objectName = objectName;
/*      */     }
/*      */ 
/*      */     public byte[] getContent() {
/*  219 */       return this.content;
/*      */     }
/*      */ 
/*      */     public void setContent(byte[] content) {
/*  223 */       this.content = content;
/*      */     }
/*      */ 
/*      */     public String getExtra() {
/*  227 */       return this.extra;
/*      */     }
/*      */ 
/*      */     public void setExtra(String extra) {
/*  231 */       this.extra = extra;
/*      */     }
/*      */ 
/*      */     public String getSenderUserId() {
/*  235 */       return this.senderUserId;
/*      */     }
/*      */ 
/*      */     public void setSenderUserId(String senderUserId) {
/*  239 */       this.senderUserId = senderUserId;
/*      */     }
/*      */ 
/*      */     public String getReadReceiptInfo() {
/*  243 */       return this.readReceiptInfo;
/*      */     }
/*      */ 
/*      */     public void setReadReceiptInfo(String readReceiptInfo) {
/*  247 */       this.readReceiptInfo = readReceiptInfo;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface ChatroomInfoListener
/*      */   {
/*      */     public abstract void OnSuccess(int paramInt, NativeObject.UserInfo[] paramArrayOfUserInfo);
/*      */ 
/*      */     public abstract void OnError(int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface HistoryMessageListener
/*      */   {
/*      */     public abstract void onReceived(NativeObject.Message[] paramArrayOfMessage);
/*      */ 
/*      */     public abstract void onError(int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface GetUserDataListener
/*      */   {
/*      */     public abstract void OnSuccess(String paramString);
/*      */ 
/*      */     public abstract void OnError(int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface AccountInfoListener
/*      */   {
/*      */     public abstract void onReceived(NativeObject.AccountInfo[] paramArrayOfAccountInfo);
/*      */ 
/*      */     public abstract void OnError(int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface TokenListener
/*      */   {
/*      */     public abstract void OnError(int paramInt, String paramString);
/*      */   }
/*      */ 
/*      */   public static abstract interface SetBlacklistListener
/*      */   {
/*      */     public abstract void OnSuccess(String paramString);
/*      */ 
/*      */     public abstract void OnError(int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface BizAckListener
/*      */   {
/*      */     public abstract void operationComplete(int paramInt1, int paramInt2);
/*      */   }
/*      */ 
/*      */   public static abstract interface ExceptionListener
/*      */   {
/*      */     public abstract void onError(int paramInt, String paramString);
/*      */   }
/*      */ 
/*      */   public static abstract interface DiscussionInfoListener
/*      */   {
/*      */     public abstract void onReceived(NativeObject.DiscussionInfo paramDiscussionInfo);
/*      */ 
/*      */     public abstract void OnError(int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface PushSettingListener
/*      */   {
/*      */     public abstract void OnSuccess(String paramString, int paramInt);
/*      */ 
/*      */     public abstract void OnError(int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract class ReceiveMessageListener
/*      */   {
/*      */     public abstract void onReceived(NativeObject.Message paramMessage, int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2);
/*      */ 
/*      */     protected NativeObject.Message getNewMessage()
/*      */     {
/*   37 */       return new NativeObject.Message();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface CreateDiscussionCallback
/*      */   {
/*      */     public abstract void OnSuccess(String paramString);
/*      */ 
/*      */     public abstract void OnError(int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface PublishAckListener
/*      */   {
/*      */     public abstract void operationComplete(int paramInt, String paramString, long paramLong);
/*      */   }
/*      */ 
/*      */   public static abstract interface ConnectAckCallback
/*      */   {
/*      */     public abstract void operationComplete(int paramInt, String paramString);
/*      */   }
/*      */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.NativeObject
 * JD-Core Version:    0.6.0
 */
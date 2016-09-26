/*     */ package io.rong.imkit.model;
/*     */ 
/*     */ import io.rong.imlib.RongIMClient.DiscussionInviteStatus;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.model.Conversation.ConversationNotificationStatus;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Group;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.imlib.model.Message.SentStatus;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import io.rong.message.RecallNotificationMessage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ 
/*     */ public class Event
/*     */ {
/*     */   public static class SyncReadStatusEvent
/*     */   {
/*     */     private Conversation.ConversationType type;
/*     */     private String targetId;
/*     */ 
/*     */     public String getTargetId()
/*     */     {
/* 941 */       return this.targetId;
/*     */     }
/*     */ 
/*     */     public Conversation.ConversationType getConversationType() {
/* 945 */       return this.type;
/*     */     }
/*     */ 
/*     */     public SyncReadStatusEvent(Conversation.ConversationType type, String targetId) {
/* 949 */       this.type = type;
/* 950 */       this.targetId = targetId;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ReadReceiptResponseEvent
/*     */   {
/*     */     private Conversation.ConversationType type;
/*     */     private String targetId;
/*     */     private String messageUId;
/*     */     private HashMap<String, Long> responseUserIdList;
/*     */ 
/*     */     public Conversation.ConversationType getConversationType()
/*     */     {
/* 911 */       return this.type;
/*     */     }
/*     */ 
/*     */     public String getTargetId() {
/* 915 */       return this.targetId;
/*     */     }
/*     */ 
/*     */     public String getMessageUId() {
/* 919 */       return this.messageUId;
/*     */     }
/*     */ 
/*     */     public HashMap<String, Long> getResponseUserIdList() {
/* 923 */       return this.responseUserIdList;
/*     */     }
/*     */ 
/*     */     public ReadReceiptResponseEvent(Conversation.ConversationType type, String targetId, String messageUId, HashMap<String, Long> responseUserIdList) {
/* 927 */       this.type = type;
/* 928 */       this.targetId = targetId;
/* 929 */       this.messageUId = messageUId;
/* 930 */       this.responseUserIdList = responseUserIdList;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ReadReceiptRequestEvent
/*     */   {
/*     */     private Conversation.ConversationType type;
/*     */     private String targetId;
/*     */     private String messageUId;
/*     */ 
/*     */     public Conversation.ConversationType getConversationType()
/*     */     {
/* 887 */       return this.type;
/*     */     }
/*     */ 
/*     */     public String getTargetId() {
/* 891 */       return this.targetId;
/*     */     }
/*     */ 
/*     */     public String getMessageUId() {
/* 895 */       return this.messageUId;
/*     */     }
/*     */ 
/*     */     public ReadReceiptRequestEvent(Conversation.ConversationType type, String targetId, String messageUId) {
/* 899 */       this.type = type;
/* 900 */       this.targetId = targetId;
/* 901 */       this.messageUId = messageUId;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class RemoteMessageRecallEvent
/*     */   {
/*     */     private int mMessageId;
/*     */     private RecallNotificationMessage mRecallNotificationMessage;
/*     */     private boolean mRecallSuccess;
/*     */ 
/*     */     public int getMessageId()
/*     */     {
/* 867 */       return this.mMessageId;
/*     */     }
/*     */ 
/*     */     public RecallNotificationMessage getRecallNotificationMessage() {
/* 871 */       return this.mRecallNotificationMessage;
/*     */     }
/*     */ 
/*     */     public boolean isRecallSuccess() {
/* 875 */       return this.mRecallSuccess;
/*     */     }
/*     */ 
/*     */     public RemoteMessageRecallEvent(int messageId, RecallNotificationMessage recallNotificationMessage, boolean recallSuccess) {
/* 879 */       this.mMessageId = messageId;
/* 880 */       this.mRecallNotificationMessage = recallNotificationMessage;
/* 881 */       this.mRecallSuccess = recallSuccess;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class MessageRecallEvent
/*     */   {
/*     */     private int mMessageId;
/*     */     private RecallNotificationMessage mRecallNotificationMessage;
/*     */     private boolean mRecallSuccess;
/*     */ 
/*     */     public int getMessageId()
/*     */     {
/* 843 */       return this.mMessageId;
/*     */     }
/*     */ 
/*     */     public RecallNotificationMessage getRecallNotificationMessage() {
/* 847 */       return this.mRecallNotificationMessage;
/*     */     }
/*     */ 
/*     */     public boolean isRecallSuccess() {
/* 851 */       return this.mRecallSuccess;
/*     */     }
/*     */ 
/*     */     public MessageRecallEvent(int messageId, RecallNotificationMessage recallNotificationMessage, boolean recallSuccess) {
/* 855 */       this.mMessageId = messageId;
/* 856 */       this.mRecallNotificationMessage = recallNotificationMessage;
/* 857 */       this.mRecallSuccess = recallSuccess;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ClearConversationEvent
/*     */   {
/* 815 */     private List<Conversation.ConversationType> typeList = new ArrayList();
/*     */ 
/*     */     public static ClearConversationEvent obtain(Conversation.ConversationType[] conversationTypes) {
/* 818 */       ClearConversationEvent clearConversationEvent = new ClearConversationEvent();
/* 819 */       clearConversationEvent.setTypes(conversationTypes);
/* 820 */       return clearConversationEvent;
/*     */     }
/*     */ 
/*     */     public void setTypes(Conversation.ConversationType[] types) {
/* 824 */       if ((types == null) || (types.length == 0))
/* 825 */         return;
/* 826 */       this.typeList.clear();
/* 827 */       for (Conversation.ConversationType type : types)
/* 828 */         this.typeList.add(type);
/*     */     }
/*     */ 
/*     */     public List<Conversation.ConversationType> getTypes()
/*     */     {
/* 833 */       return this.typeList;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ReadReceiptEvent
/*     */   {
/*     */     private Message readReceiptMessage;
/*     */ 
/*     */     public ReadReceiptEvent(Message message)
/*     */     {
/* 804 */       this.readReceiptMessage = message;
/*     */     }
/*     */ 
/*     */     public Message getMessage() {
/* 808 */       return this.readReceiptMessage;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class PlayAudioEvent
/*     */   {
/*     */     public MessageContent content;
/*     */     public boolean continuously;
/*     */ 
/*     */     public static PlayAudioEvent obtain()
/*     */     {
/* 798 */       return new PlayAudioEvent();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class GroupUserInfoEvent
/*     */   {
/*     */     private GroupUserInfo userInfo;
/*     */ 
/*     */     public static GroupUserInfoEvent obtain(GroupUserInfo info)
/*     */     {
/* 783 */       GroupUserInfoEvent event = new GroupUserInfoEvent();
/* 784 */       event.userInfo = info;
/* 785 */       return event;
/*     */     }
/*     */ 
/*     */     public GroupUserInfo getUserInfo() {
/* 789 */       return this.userInfo;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ConnectEvent
/*     */   {
/*     */     private boolean isConnectSuccess;
/*     */ 
/*     */     public static ConnectEvent obtain(boolean flag)
/*     */     {
/* 764 */       ConnectEvent event = new ConnectEvent();
/* 765 */       event.setConnectStatus(flag);
/* 766 */       return event;
/*     */     }
/*     */ 
/*     */     public void setConnectStatus(boolean flag) {
/* 770 */       this.isConnectSuccess = flag;
/*     */     }
/*     */ 
/*     */     public boolean getConnectStatus() {
/* 774 */       return this.isConnectSuccess;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class InputViewEvent
/*     */   {
/*     */     private boolean isVisibility;
/*     */ 
/*     */     public static InputViewEvent obtain(boolean isVisibility)
/*     */     {
/* 746 */       InputViewEvent inputViewEvent = new InputViewEvent();
/* 747 */       inputViewEvent.setIsVisibility(isVisibility);
/* 748 */       return inputViewEvent;
/*     */     }
/*     */ 
/*     */     public boolean isVisibility() {
/* 752 */       return this.isVisibility;
/*     */     }
/*     */ 
/*     */     public void setIsVisibility(boolean isVisibility) {
/* 756 */       this.isVisibility = isVisibility;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class NotificationPublicServiceInfoEvent
/*     */   {
/*     */     private String key;
/*     */ 
/*     */     NotificationPublicServiceInfoEvent(String key)
/*     */     {
/* 724 */       setKey(key);
/*     */     }
/*     */ 
/*     */     public static NotificationPublicServiceInfoEvent obtain(String key) {
/* 728 */       return new NotificationPublicServiceInfoEvent(key);
/*     */     }
/*     */ 
/*     */     public String getKey()
/*     */     {
/* 734 */       return this.key;
/*     */     }
/*     */ 
/*     */     public void setKey(String key) {
/* 738 */       this.key = key;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class NotificationDiscussionInfoEvent
/*     */   {
/*     */     private String key;
/*     */ 
/*     */     NotificationDiscussionInfoEvent(String key)
/*     */     {
/* 702 */       setKey(key);
/*     */     }
/*     */ 
/*     */     public static NotificationDiscussionInfoEvent obtain(String key) {
/* 706 */       return new NotificationDiscussionInfoEvent(key);
/*     */     }
/*     */ 
/*     */     public String getKey()
/*     */     {
/* 712 */       return this.key;
/*     */     }
/*     */ 
/*     */     public void setKey(String key) {
/* 716 */       this.key = key;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class NotificationGroupInfoEvent
/*     */   {
/*     */     private String key;
/*     */ 
/*     */     NotificationGroupInfoEvent(String key)
/*     */     {
/* 682 */       setKey(key);
/*     */     }
/*     */ 
/*     */     public static NotificationGroupInfoEvent obtain(String key) {
/* 686 */       return new NotificationGroupInfoEvent(key);
/*     */     }
/*     */ 
/*     */     public String getKey()
/*     */     {
/* 692 */       return this.key;
/*     */     }
/*     */ 
/*     */     public void setKey(String key) {
/* 696 */       this.key = key;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class NotificationUserInfoEvent
/*     */   {
/*     */     private String key;
/*     */ 
/*     */     NotificationUserInfoEvent(String key)
/*     */     {
/* 661 */       setKey(key);
/*     */     }
/*     */ 
/*     */     public static NotificationUserInfoEvent obtain(String key) {
/* 665 */       return new NotificationUserInfoEvent(key);
/*     */     }
/*     */ 
/*     */     public String getKey()
/*     */     {
/* 671 */       return this.key;
/*     */     }
/*     */ 
/*     */     public void setKey(String key) {
/* 675 */       this.key = key;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class AudioListenedEvent extends Event.BaseConversationEvent
/*     */   {
/*     */     private int latestMessageId;
/*     */ 
/*     */     public AudioListenedEvent(Conversation.ConversationType type, String targetId, int messageId)
/*     */     {
/* 644 */       setConversationType(type);
/* 645 */       setTargetId(targetId);
/* 646 */       this.latestMessageId = messageId;
/*     */     }
/*     */ 
/*     */     public void setLatestMessageId(int id) {
/* 650 */       this.latestMessageId = id;
/*     */     }
/*     */ 
/*     */     public int getLatestMessageId() {
/* 654 */       return this.latestMessageId;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class VoiceInputOperationEvent
/*     */   {
/* 617 */     public static int STATUS_DEFAULT = -1;
/* 618 */     public static int STATUS_INPUTING = 0;
/* 619 */     public static int STATUS_INPUT_COMPLETE = 1;
/*     */     private int status;
/*     */ 
/*     */     public VoiceInputOperationEvent(int status)
/*     */     {
/* 624 */       setStatus(status);
/*     */     }
/*     */ 
/*     */     public static VoiceInputOperationEvent obtain(int status) {
/* 628 */       return new VoiceInputOperationEvent(status);
/*     */     }
/*     */ 
/*     */     public int getStatus() {
/* 632 */       return this.status;
/*     */     }
/*     */ 
/*     */     public void setStatus(int status) {
/* 636 */       this.status = status;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class PublicServiceFollowableEvent extends Event.BaseConversationEvent
/*     */   {
/* 593 */     private boolean isFollow = false;
/*     */ 
/*     */     public PublicServiceFollowableEvent(String targetId, Conversation.ConversationType conversationType, boolean isFollow) {
/* 596 */       setTargetId(targetId);
/* 597 */       setConversationType(conversationType);
/* 598 */       setIsFollow(isFollow);
/*     */     }
/*     */ 
/*     */     public static PublicServiceFollowableEvent obtain(String targetId, Conversation.ConversationType conversationType, boolean isFollow) {
/* 602 */       return new PublicServiceFollowableEvent(targetId, conversationType, isFollow);
/*     */     }
/*     */ 
/*     */     public boolean isFollow() {
/* 606 */       return this.isFollow;
/*     */     }
/*     */ 
/*     */     public void setIsFollow(boolean isFollow) {
/* 610 */       this.isFollow = isFollow;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ConversationNotificationEvent extends Event.BaseConversationEvent
/*     */   {
/*     */     private Conversation.ConversationNotificationStatus mStatus;
/*     */ 
/*     */     public ConversationNotificationEvent(String targetId, Conversation.ConversationType conversationType, Conversation.ConversationNotificationStatus conversationNotificationStatus)
/*     */     {
/* 573 */       setTargetId(targetId);
/* 574 */       setConversationType(conversationType);
/* 575 */       setStatus(conversationNotificationStatus);
/*     */     }
/*     */ 
/*     */     public static ConversationNotificationEvent obtain(String targetId, Conversation.ConversationType conversationType, Conversation.ConversationNotificationStatus conversationNotificationStatus) {
/* 579 */       return new ConversationNotificationEvent(targetId, conversationType, conversationNotificationStatus);
/*     */     }
/*     */ 
/*     */     public Conversation.ConversationNotificationStatus getStatus() {
/* 583 */       return this.mStatus;
/*     */     }
/*     */ 
/*     */     public void setStatus(Conversation.ConversationNotificationStatus status) {
/* 587 */       this.mStatus = status;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static class BaseConversationEvent
/*     */   {
/*     */     protected Conversation.ConversationType mConversationType;
/*     */     protected String mTargetId;
/*     */ 
/*     */     public Conversation.ConversationType getConversationType()
/*     */     {
/* 552 */       return this.mConversationType;
/*     */     }
/*     */ 
/*     */     public void setConversationType(Conversation.ConversationType conversationType) {
/* 556 */       this.mConversationType = conversationType;
/*     */     }
/*     */ 
/*     */     public String getTargetId() {
/* 560 */       return this.mTargetId;
/*     */     }
/*     */ 
/*     */     public void setTargetId(String targetId) {
/* 564 */       this.mTargetId = targetId;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class RemoveFromBlacklistEvent
/*     */   {
/*     */     String userId;
/*     */ 
/*     */     public RemoveFromBlacklistEvent(String userId)
/*     */     {
/* 534 */       this.userId = userId;
/*     */     }
/*     */ 
/*     */     public String getUserId() {
/* 538 */       return this.userId;
/*     */     }
/*     */ 
/*     */     public void setUserId(String userId) {
/* 542 */       this.userId = userId;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class AddToBlacklistEvent
/*     */   {
/*     */     String userId;
/*     */ 
/*     */     public AddToBlacklistEvent(String userId)
/*     */     {
/* 518 */       this.userId = userId;
/*     */     }
/*     */ 
/*     */     public String getUserId() {
/* 522 */       return this.userId;
/*     */     }
/*     */ 
/*     */     public void setUserId(String userId) {
/* 526 */       this.userId = userId;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class QuitChatRoomEvent
/*     */   {
/*     */     String chatRoomId;
/*     */ 
/*     */     public QuitChatRoomEvent(String chatRoomId)
/*     */     {
/* 502 */       this.chatRoomId = chatRoomId;
/*     */     }
/*     */ 
/*     */     public String getChatRoomId() {
/* 506 */       return this.chatRoomId;
/*     */     }
/*     */ 
/*     */     public void setChatRoomId(String chatRoomId) {
/* 510 */       this.chatRoomId = chatRoomId;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class JoinChatRoomEvent
/*     */   {
/*     */     String chatRoomId;
/*     */     int defMessageCount;
/*     */ 
/*     */     public JoinChatRoomEvent(String chatRoomId, int defMessageCount)
/*     */     {
/* 477 */       this.chatRoomId = chatRoomId;
/* 478 */       this.defMessageCount = defMessageCount;
/*     */     }
/*     */ 
/*     */     public String getChatRoomId() {
/* 482 */       return this.chatRoomId;
/*     */     }
/*     */ 
/*     */     public void setChatRoomId(String chatRoomId) {
/* 486 */       this.chatRoomId = chatRoomId;
/*     */     }
/*     */ 
/*     */     public int getDefMessageCount() {
/* 490 */       return this.defMessageCount;
/*     */     }
/*     */ 
/*     */     public void setDefMessageCount(int defMessageCount) {
/* 494 */       this.defMessageCount = defMessageCount;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class QuitGroupEvent
/*     */   {
/*     */     String groupId;
/*     */ 
/*     */     public QuitGroupEvent(String groupId)
/*     */     {
/* 460 */       this.groupId = groupId;
/*     */     }
/*     */ 
/*     */     public String getGroupId() {
/* 464 */       return this.groupId;
/*     */     }
/*     */ 
/*     */     public void setGroupId(String groupId) {
/* 468 */       this.groupId = groupId;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class JoinGroupEvent
/*     */   {
/*     */     String groupId;
/*     */     String groupName;
/*     */ 
/*     */     public JoinGroupEvent(String groupId, String groupName)
/*     */     {
/* 435 */       this.groupId = groupId;
/* 436 */       this.groupName = groupName;
/*     */     }
/*     */ 
/*     */     public String getGroupId() {
/* 440 */       return this.groupId;
/*     */     }
/*     */ 
/*     */     public void setGroupId(String groupId) {
/* 444 */       this.groupId = groupId;
/*     */     }
/*     */ 
/*     */     public String getGroupName() {
/* 448 */       return this.groupName;
/*     */     }
/*     */ 
/*     */     public void setGroupName(String groupName) {
/* 452 */       this.groupName = groupName;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class SyncGroupEvent
/*     */   {
/*     */     List<Group> groups;
/*     */ 
/*     */     public SyncGroupEvent(List<Group> groups)
/*     */     {
/* 419 */       this.groups = groups;
/*     */     }
/*     */ 
/*     */     public List<Group> getGroups() {
/* 423 */       return this.groups;
/*     */     }
/*     */ 
/*     */     public void setGroups(List<Group> groups) {
/* 427 */       this.groups = groups;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class DiscussionInviteStatusEvent
/*     */   {
/*     */     String discussionId;
/*     */     RongIMClient.DiscussionInviteStatus status;
/*     */ 
/*     */     public DiscussionInviteStatusEvent(String discussionId, RongIMClient.DiscussionInviteStatus status)
/*     */     {
/* 393 */       this.discussionId = discussionId;
/* 394 */       this.status = status;
/*     */     }
/*     */ 
/*     */     public String getDiscussionId() {
/* 398 */       return this.discussionId;
/*     */     }
/*     */ 
/*     */     public void setDiscussionId(String discussionId) {
/* 402 */       this.discussionId = discussionId;
/*     */     }
/*     */ 
/*     */     public RongIMClient.DiscussionInviteStatus getStatus()
/*     */     {
/* 407 */       return this.status;
/*     */     }
/*     */ 
/*     */     public void setStatus(RongIMClient.DiscussionInviteStatus status) {
/* 411 */       this.status = status;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class QuitDiscussionEvent
/*     */   {
/*     */     String discussionId;
/*     */ 
/*     */     public QuitDiscussionEvent(String discussionId)
/*     */     {
/* 376 */       this.discussionId = discussionId;
/*     */     }
/*     */ 
/*     */     public String getDiscussionId() {
/* 380 */       return this.discussionId;
/*     */     }
/*     */ 
/*     */     public void setDiscussionId(String discussionId) {
/* 384 */       this.discussionId = discussionId;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class RemoveMemberFromDiscussionEvent
/*     */   {
/*     */     String discussionId;
/*     */     String userId;
/*     */ 
/*     */     public RemoveMemberFromDiscussionEvent(String discussionId, String userId)
/*     */     {
/* 351 */       this.discussionId = discussionId;
/* 352 */       this.userId = userId;
/*     */     }
/*     */ 
/*     */     public String getDiscussionId() {
/* 356 */       return this.discussionId;
/*     */     }
/*     */ 
/*     */     public void setDiscussionId(String discussionId) {
/* 360 */       this.discussionId = discussionId;
/*     */     }
/*     */ 
/*     */     public String getUserId() {
/* 364 */       return this.userId;
/*     */     }
/*     */ 
/*     */     public void setUserId(String userId) {
/* 368 */       this.userId = userId;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class AddMemberToDiscussionEvent
/*     */   {
/*     */     String discussionId;
/*     */     List<String> userIdList;
/*     */ 
/*     */     public AddMemberToDiscussionEvent(String discussionId, List<String> userIdList)
/*     */     {
/* 325 */       this.discussionId = discussionId;
/* 326 */       this.userIdList = userIdList;
/*     */     }
/*     */ 
/*     */     public String getDiscussionId() {
/* 330 */       return this.discussionId;
/*     */     }
/*     */ 
/*     */     public void setDiscussionId(String discussionId) {
/* 334 */       this.discussionId = discussionId;
/*     */     }
/*     */ 
/*     */     public List<String> getUserIdList() {
/* 338 */       return this.userIdList;
/*     */     }
/*     */ 
/*     */     public void setUserIdList(List<String> userIdList) {
/* 342 */       this.userIdList = userIdList;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class CreateDiscussionEvent
/*     */   {
/*     */     String discussionId;
/*     */     String discussionName;
/*     */     List<String> userIdList;
/*     */ 
/*     */     public CreateDiscussionEvent(String discussionId, String discussionName, List<String> userIdList)
/*     */     {
/* 290 */       this.discussionId = discussionId;
/* 291 */       this.discussionName = discussionName;
/* 292 */       this.userIdList = userIdList;
/*     */     }
/*     */ 
/*     */     public String getDiscussionId() {
/* 296 */       return this.discussionId;
/*     */     }
/*     */ 
/*     */     public void setDiscussionId(String discussionId) {
/* 300 */       this.discussionId = discussionId;
/*     */     }
/*     */ 
/*     */     public String getDiscussionName() {
/* 304 */       return this.discussionName;
/*     */     }
/*     */ 
/*     */     public void setDiscussionName(String discussionName) {
/* 308 */       this.discussionName = discussionName;
/*     */     }
/*     */ 
/*     */     public List<String> getUserIdList() {
/* 312 */       return this.userIdList;
/*     */     }
/*     */ 
/*     */     public void setUserIdList(List<String> userIdList) {
/* 316 */       this.userIdList = userIdList;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class MessagesClearEvent
/*     */   {
/*     */     Conversation.ConversationType type;
/*     */     String targetId;
/*     */ 
/*     */     public MessagesClearEvent(Conversation.ConversationType type, String targetId)
/*     */     {
/* 263 */       this.type = type;
/* 264 */       this.targetId = targetId;
/*     */     }
/*     */ 
/*     */     public Conversation.ConversationType getType() {
/* 268 */       return this.type;
/*     */     }
/*     */ 
/*     */     public void setType(Conversation.ConversationType type) {
/* 272 */       this.type = type;
/*     */     }
/*     */ 
/*     */     public String getTargetId() {
/* 276 */       return this.targetId;
/*     */     }
/*     */ 
/*     */     public void setTargetId(String targetId) {
/* 280 */       this.targetId = targetId;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class MessageDeleteEvent
/*     */   {
/*     */     List<Integer> messageIds;
/*     */ 
/*     */     public MessageDeleteEvent(int[] ids)
/*     */     {
/* 241 */       if ((ids == null) || (ids.length == 0))
/* 242 */         return;
/* 243 */       this.messageIds = new ArrayList();
/* 244 */       for (int id : ids)
/* 245 */         this.messageIds.add(Integer.valueOf(id));
/*     */     }
/*     */ 
/*     */     public List<Integer> getMessageIds()
/*     */     {
/* 250 */       return this.messageIds;
/*     */     }
/*     */ 
/*     */     public void setMessageIds(List<Integer> messageIds) {
/* 254 */       this.messageIds = messageIds;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class MessageSentStatusEvent
/*     */   {
/*     */     int messageId;
/*     */     Message.SentStatus sentStatus;
/*     */ 
/*     */     public MessageSentStatusEvent(int messageId, Message.SentStatus sentStatus)
/*     */     {
/* 215 */       this.messageId = messageId;
/* 216 */       this.sentStatus = sentStatus;
/*     */     }
/*     */ 
/*     */     public int getMessageId() {
/* 220 */       return this.messageId;
/*     */     }
/*     */ 
/*     */     public void setMessageId(int messageId) {
/* 224 */       this.messageId = messageId;
/*     */     }
/*     */ 
/*     */     public Message.SentStatus getSentStatus() {
/* 228 */       return this.sentStatus;
/*     */     }
/*     */ 
/*     */     public void setSentStatus(Message.SentStatus sentStatus) {
/* 232 */       this.sentStatus = sentStatus;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ConversationRemoveEvent
/*     */   {
/*     */     Conversation.ConversationType type;
/*     */     String targetId;
/*     */ 
/*     */     public ConversationRemoveEvent(Conversation.ConversationType type, String targetId)
/*     */     {
/* 189 */       this.type = type;
/* 190 */       this.targetId = targetId;
/*     */     }
/*     */ 
/*     */     public Conversation.ConversationType getType() {
/* 194 */       return this.type;
/*     */     }
/*     */ 
/*     */     public void setType(Conversation.ConversationType type) {
/* 198 */       this.type = type;
/*     */     }
/*     */ 
/*     */     public String getTargetId() {
/* 202 */       return this.targetId;
/*     */     }
/*     */ 
/*     */     public void setTargetId(String targetId) {
/* 206 */       this.targetId = targetId;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ConversationTopEvent extends Event.BaseConversationEvent
/*     */   {
/*     */     boolean isTop;
/*     */ 
/*     */     public ConversationTopEvent(Conversation.ConversationType type, String targetId, boolean isTop)
/*     */     {
/* 170 */       setConversationType(type);
/* 171 */       setTargetId(targetId);
/* 172 */       this.isTop = isTop;
/*     */     }
/*     */ 
/*     */     public boolean isTop() {
/* 176 */       return this.isTop;
/*     */     }
/*     */ 
/*     */     public void setTop(boolean isTop) {
/* 180 */       this.isTop = isTop;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ConversationUnreadEvent
/*     */   {
/*     */     Conversation.ConversationType type;
/*     */     String targetId;
/*     */ 
/*     */     public ConversationUnreadEvent(Conversation.ConversationType type, String targetId)
/*     */     {
/* 144 */       this.type = type;
/* 145 */       this.targetId = targetId;
/*     */     }
/*     */ 
/*     */     public Conversation.ConversationType getType() {
/* 149 */       return this.type;
/*     */     }
/*     */ 
/*     */     public void setType(Conversation.ConversationType type) {
/* 153 */       this.type = type;
/*     */     }
/*     */ 
/*     */     public String getTargetId() {
/* 157 */       return this.targetId;
/*     */     }
/*     */ 
/*     */     public void setTargetId(String targetId) {
/* 161 */       this.targetId = targetId;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class OnMessageSendErrorEvent
/*     */   {
/*     */     Message message;
/*     */     RongIMClient.ErrorCode errorCode;
/*     */ 
/*     */     public OnMessageSendErrorEvent(Message message, RongIMClient.ErrorCode errorCode)
/*     */     {
/* 118 */       this.message = message;
/* 119 */       this.errorCode = errorCode;
/*     */     }
/*     */ 
/*     */     public Message getMessage() {
/* 123 */       return this.message;
/*     */     }
/*     */ 
/*     */     public void setMessage(Message message) {
/* 127 */       this.message = message;
/*     */     }
/*     */ 
/*     */     public RongIMClient.ErrorCode getErrorCode() {
/* 131 */       return this.errorCode;
/*     */     }
/*     */ 
/*     */     public void setErrorCode(RongIMClient.ErrorCode errorCode) {
/* 135 */       this.errorCode = errorCode;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class OnReceiveMessageProgressEvent
/*     */   {
/*     */     Message message;
/*     */     int progress;
/*     */ 
/*     */     public int getProgress()
/*     */     {
/*  97 */       return this.progress;
/*     */     }
/*     */ 
/*     */     public Message getMessage() {
/* 101 */       return this.message;
/*     */     }
/*     */ 
/*     */     public void setMessage(Message message) {
/* 105 */       this.message = message;
/*     */     }
/*     */ 
/*     */     public void setProgress(int progress) {
/* 109 */       this.progress = progress;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class OnReceiveMessageEvent
/*     */   {
/*     */     Message message;
/*     */     int left;
/*     */ 
/*     */     public OnReceiveMessageEvent(Message message, int left)
/*     */     {
/*  68 */       this.message = message;
/*  69 */       this.left = left;
/*     */     }
/*     */ 
/*     */     public Message getMessage() {
/*  73 */       return this.message;
/*     */     }
/*     */ 
/*     */     public void setMessage(Message message) {
/*  77 */       this.message = message;
/*     */     }
/*     */ 
/*     */     public int getLeft() {
/*  81 */       return this.left;
/*     */     }
/*     */ 
/*     */     public void setLeft(int left) {
/*  85 */       this.left = left;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class FileMessageEvent
/*     */   {
/*     */     Message message;
/*     */     int progress;
/*     */     int callBackType;
/*     */     RongIMClient.ErrorCode errorCode;
/*     */ 
/*     */     public FileMessageEvent(Message message, int progress, int callBackType, RongIMClient.ErrorCode errorCode)
/*     */     {
/*  26 */       this.message = message;
/*  27 */       this.progress = progress;
/*  28 */       this.callBackType = callBackType;
/*  29 */       this.errorCode = errorCode;
/*     */     }
/*     */     public Message getMessage() {
/*  32 */       return this.message;
/*     */     }
/*     */ 
/*     */     public void setMessage(Message message) {
/*  36 */       this.message = message;
/*     */     }
/*     */ 
/*     */     public int getProgress() {
/*  40 */       return this.progress;
/*     */     }
/*     */ 
/*     */     public void setProgress(int progress) {
/*  44 */       this.progress = progress;
/*     */     }
/*     */ 
/*     */     public int getCallBackType() {
/*  48 */       return this.callBackType;
/*     */     }
/*     */ 
/*     */     public void setCallBackType(int callBackType) {
/*  52 */       this.callBackType = callBackType;
/*     */     }
/*     */     public RongIMClient.ErrorCode getErrorCode() {
/*  55 */       return this.errorCode;
/*     */     }
/*     */ 
/*     */     public void setErrorCode(RongIMClient.ErrorCode errorCode) {
/*  59 */       this.errorCode = errorCode;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.model.Event
 * JD-Core Version:    0.6.0
 */
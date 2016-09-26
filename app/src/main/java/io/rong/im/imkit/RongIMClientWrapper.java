/*      */ package io.rong.imkit;
/*      */ 
/*      */ import io.rong.imlib.AnnotationNotFoundException;
/*      */ import io.rong.imlib.RongIMClient.BlacklistStatus;
/*      */ import io.rong.imlib.RongIMClient.ConnectCallback;
/*      */ import io.rong.imlib.RongIMClient.ConnectionStatusListener;
/*      */ import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
/*      */ import io.rong.imlib.RongIMClient.CreateDiscussionCallback;
/*      */ import io.rong.imlib.RongIMClient.DiscussionInviteStatus;
/*      */ import io.rong.imlib.RongIMClient.DownloadMediaCallback;
/*      */ import io.rong.imlib.RongIMClient.GetBlacklistCallback;
/*      */ import io.rong.imlib.RongIMClient.GetNotificationQuietHoursCallback;
/*      */ import io.rong.imlib.RongIMClient.MediaType;
/*      */ import io.rong.imlib.RongIMClient.OnReceiveMessageListener;
/*      */ import io.rong.imlib.RongIMClient.OperationCallback;
/*      */ import io.rong.imlib.RongIMClient.ResultCallback;
/*      */ import io.rong.imlib.RongIMClient.SearchType;
/*      */ import io.rong.imlib.RongIMClient.SendImageMessageCallback;
/*      */ import io.rong.imlib.RongIMClient.SendImageMessageWithUploadListenerCallback;
/*      */ import io.rong.imlib.RongIMClient.SendMessageCallback;
/*      */ import io.rong.imlib.model.Conversation;
/*      */ import io.rong.imlib.model.Conversation.ConversationNotificationStatus;
/*      */ import io.rong.imlib.model.Conversation.ConversationType;
/*      */ import io.rong.imlib.model.Conversation.PublicServiceType;
/*      */ import io.rong.imlib.model.Discussion;
/*      */ import io.rong.imlib.model.Group;
/*      */ import io.rong.imlib.model.Message;
/*      */ import io.rong.imlib.model.Message.ReceivedStatus;
/*      */ import io.rong.imlib.model.Message.SentStatus;
/*      */ import io.rong.imlib.model.MessageContent;
/*      */ import io.rong.imlib.model.PublicServiceProfile;
/*      */ import io.rong.imlib.model.PublicServiceProfileList;
/*      */ import io.rong.imlib.model.UserData;
/*      */ import java.util.List;
/*      */ 
/*      */ public class RongIMClientWrapper
/*      */ {
/*      */   @Deprecated
/*      */   public RongIMClientWrapper connect(String token, RongIMClient.ConnectCallback callback)
/*      */   {
/*   36 */     RongIM.connect(token, callback);
/*   37 */     return this;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static void setConnectionStatusListener(RongIMClient.ConnectionStatusListener listener)
/*      */   {
/*   48 */     RongIM.setConnectionStatusListener(listener);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static void registerMessageType(Class<? extends MessageContent> type)
/*      */     throws AnnotationNotFoundException
/*      */   {
/*   59 */     RongIM.registerMessageType(type);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public RongIMClient.ConnectionStatusListener.ConnectionStatus getCurrentConnectionStatus()
/*      */   {
/*   69 */     return RongIM.getInstance().getCurrentConnectionStatus();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void disconnect()
/*      */   {
/*   77 */     RongIM.getInstance().disconnect();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void disconnect(boolean isReceivePush)
/*      */   {
/*   87 */     RongIM.getInstance().disconnect(isReceivePush);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void logout()
/*      */   {
/*   95 */     RongIM.getInstance().logout();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static void setOnReceiveMessageListener(RongIMClient.OnReceiveMessageListener listener)
/*      */   {
/*  107 */     RongIM.setOnReceiveMessageListener(listener);
/*      */   }
/*      */   @Deprecated
/*      */   public void getConversationList(RongIMClient.ResultCallback<List<Conversation>> callback) {
/*  112 */     RongIM.getInstance().getConversationList(callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public List<Conversation> getConversationList()
/*      */   {
/*  123 */     return RongIM.getInstance().getConversationList();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getConversationList(RongIMClient.ResultCallback<List<Conversation>> callback, Conversation.ConversationType[] types)
/*      */   {
/*  134 */     RongIM.getInstance().getConversationList(callback, types);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public List<Conversation> getConversationList(Conversation.ConversationType[] types)
/*      */   {
/*  146 */     return RongIM.getInstance().getConversationList(types);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getConversation(Conversation.ConversationType type, String targetId, RongIMClient.ResultCallback<Conversation> callback)
/*      */   {
/*  158 */     RongIM.getInstance().getConversation(type, targetId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Conversation getConversation(Conversation.ConversationType type, String targetId)
/*      */   {
/*  170 */     return RongIM.getInstance().getConversation(type, targetId);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void removeConversation(Conversation.ConversationType type, String targetId, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/*  184 */     RongIM.getInstance().removeConversation(type, targetId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean removeConversation(Conversation.ConversationType type, String targetId)
/*      */   {
/*  198 */     return RongIM.getInstance().removeConversation(type, targetId);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setConversationToTop(Conversation.ConversationType type, String id, boolean isTop, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/*  211 */     RongIM.getInstance().setConversationToTop(type, id, isTop, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean setConversationToTop(Conversation.ConversationType conversationType, String targetId, boolean isTop)
/*      */   {
/*  224 */     return RongIM.getInstance().setConversationToTop(conversationType, targetId, isTop);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getTotalUnreadCount(RongIMClient.ResultCallback<Integer> callback)
/*      */   {
/*  234 */     RongIM.getInstance().getTotalUnreadCount(callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getTotalUnreadCount()
/*      */   {
/*  244 */     return RongIM.getInstance().getTotalUnreadCount();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getUnreadCount(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<Integer> callback)
/*      */   {
/*  256 */     RongIM.getInstance().getUnreadCount(conversationType, targetId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getUnreadCount(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/*  268 */     return RongIM.getInstance().getUnreadCount(conversationType, targetId);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getUnreadCount(RongIMClient.ResultCallback<Integer> callback, Conversation.ConversationType[] conversationTypes)
/*      */   {
/*  280 */     RongIM.getInstance().getUnreadCount(callback, conversationTypes);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getUnreadCount(Conversation.ConversationType[] conversationTypes)
/*      */   {
/*  291 */     return RongIM.getInstance().getUnreadCount(conversationTypes);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getUnreadCount(Conversation.ConversationType[] conversationTypes, RongIMClient.ResultCallback<Integer> callback)
/*      */   {
/*  302 */     RongIM.getInstance().getUnreadCount(conversationTypes, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public List<Message> getLatestMessages(Conversation.ConversationType conversationType, String targetId, int count)
/*      */   {
/*  315 */     return RongIM.getInstance().getLatestMessages(conversationType, targetId, count);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getLatestMessages(Conversation.ConversationType conversationType, String targetId, int count, RongIMClient.ResultCallback<List<Message>> callback)
/*      */   {
/*  328 */     RongIM.getInstance().getLatestMessages(conversationType, targetId, count, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public List<Message> getHistoryMessages(Conversation.ConversationType conversationType, String targetId, int oldestMessageId, int count)
/*      */   {
/*  342 */     return RongIM.getInstance().getHistoryMessages(conversationType, targetId, oldestMessageId, count);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public List<Message> getHistoryMessages(Conversation.ConversationType conversationType, String targetId, String objectName, int oldestMessageId, int count)
/*      */   {
/*  357 */     return RongIM.getInstance().getHistoryMessages(conversationType, targetId, objectName, oldestMessageId, count);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getHistoryMessages(Conversation.ConversationType conversationType, String targetId, String objectName, int oldestMessageId, int count, RongIMClient.ResultCallback<List<Message>> callback)
/*      */   {
/*  372 */     RongIM.getInstance().getHistoryMessages(conversationType, targetId, objectName, oldestMessageId, count, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getHistoryMessages(Conversation.ConversationType conversationType, String targetId, int oldestMessageId, int count, RongIMClient.ResultCallback<List<Message>> callback)
/*      */   {
/*  386 */     RongIM.getInstance().getHistoryMessages(conversationType, targetId, oldestMessageId, count, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getRemoteHistoryMessages(Conversation.ConversationType conversationType, String targetId, long dataTime, int count, RongIMClient.ResultCallback<List<Message>> callback)
/*      */   {
/*  401 */     RongIM.getInstance().getRemoteHistoryMessages(conversationType, targetId, dataTime, count, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean deleteMessages(int[] messageIds)
/*      */   {
/*  412 */     return RongIM.getInstance().deleteMessages(messageIds);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void deleteMessages(int[] messageIds, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/*  423 */     RongIM.getInstance().deleteMessages(messageIds, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean clearMessages(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/*  435 */     return RongIM.getInstance().clearMessages(conversationType, targetId);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void clearMessages(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/*  447 */     RongIM.getInstance().clearMessages(conversationType, targetId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean clearMessagesUnreadStatus(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/*  459 */     return RongIM.getInstance().clearMessagesUnreadStatus(conversationType, targetId);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void clearMessagesUnreadStatus(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/*  471 */     RongIM.getInstance().clearMessagesUnreadStatus(conversationType, targetId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean setMessageExtra(int messageId, String value)
/*      */   {
/*  483 */     return RongIM.getInstance().setMessageExtra(messageId, value);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setMessageExtra(int messageId, String value, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/*  495 */     RongIM.getInstance().setMessageExtra(messageId, value, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean setMessageReceivedStatus(int messageId, Message.ReceivedStatus receivedStatus)
/*      */   {
/*  507 */     return RongIM.getInstance().setMessageReceivedStatus(messageId, receivedStatus);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setMessageReceivedStatus(int messageId, Message.ReceivedStatus receivedStatus, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/*  519 */     RongIM.getInstance().setMessageReceivedStatus(messageId, receivedStatus, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean setMessageSentStatus(int messageId, Message.SentStatus sentStatus)
/*      */   {
/*  531 */     return RongIM.getInstance().setMessageSentStatus(messageId, sentStatus);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setMessageSentStatus(int messageId, Message.SentStatus sentStatus, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/*  543 */     RongIM.getInstance().setMessageSentStatus(messageId, sentStatus, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public String getTextMessageDraft(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/*  555 */     return RongIM.getInstance().getTextMessageDraft(conversationType, targetId);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean saveTextMessageDraft(Conversation.ConversationType conversationType, String targetId, String content)
/*      */   {
/*  568 */     return RongIM.getInstance().saveTextMessageDraft(conversationType, targetId, content);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean clearTextMessageDraft(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/*  580 */     return RongIM.getInstance().clearTextMessageDraft(conversationType, targetId);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getTextMessageDraft(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<String> callback)
/*      */   {
/*  592 */     RongIM.getInstance().getTextMessageDraft(conversationType, targetId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void saveTextMessageDraft(Conversation.ConversationType conversationType, String targetId, String content, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/*  605 */     RongIM.getInstance().saveTextMessageDraft(conversationType, targetId, content, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void clearTextMessageDraft(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/*  617 */     RongIM.getInstance().clearTextMessageDraft(conversationType, targetId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getDiscussion(String discussionId, RongIMClient.ResultCallback<Discussion> callback)
/*      */   {
/*  628 */     RongIM.getInstance().getDiscussion(discussionId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setDiscussionName(String discussionId, String name, RongIMClient.OperationCallback callback)
/*      */   {
/*  640 */     RongIM.getInstance().setDiscussionName(discussionId, name, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void createDiscussion(String name, List<String> userIdList, RongIMClient.CreateDiscussionCallback callback)
/*      */   {
/*  652 */     RongIM.getInstance().createDiscussion(name, userIdList, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void addMemberToDiscussion(String discussionId, List<String> userIdList, RongIMClient.OperationCallback callback)
/*      */   {
/*  664 */     RongIM.getInstance().addMemberToDiscussion(discussionId, userIdList, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void removeMemberFromDiscussion(String discussionId, String userId, RongIMClient.OperationCallback callback)
/*      */   {
/*  679 */     RongIM.getInstance().removeMemberFromDiscussion(discussionId, userId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void quitDiscussion(String discussionId, RongIMClient.OperationCallback callback)
/*      */   {
/*  690 */     RongIM.getInstance().quitDiscussion(discussionId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void insertMessage(Conversation.ConversationType type, String targetId, String senderUserId, MessageContent content, RongIMClient.ResultCallback<Message> callback)
/*      */   {
/*  704 */     RongIM.getInstance().insertMessage(type, targetId, senderUserId, content, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Message insertMessage(Conversation.ConversationType type, String targetId, String senderUserId, MessageContent content)
/*      */   {
/*  718 */     return RongIM.getInstance().insertMessage(type, targetId, senderUserId, content);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Message sendMessage(Conversation.ConversationType type, String targetId, MessageContent content, String pushContent, String pushData, RongIMClient.SendMessageCallback callback)
/*      */   {
/*  734 */     return RongIM.getInstance().sendMessage(type, targetId, content, pushContent, pushData, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void sendMessage(Conversation.ConversationType type, String targetId, MessageContent content, String pushContent, String pushData, RongIMClient.SendMessageCallback callback, RongIMClient.ResultCallback<Message> resultCallback)
/*      */   {
/*  753 */     RongIM.getInstance().sendMessage(type, targetId, content, pushContent, pushData, callback, resultCallback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void sendMessage(Message message, String pushContent, String pushData, RongIMClient.SendMessageCallback callback, RongIMClient.ResultCallback<Message> resultCallback)
/*      */   {
/*  768 */     RongIM.getInstance().sendMessage(message, pushContent, pushData, callback, resultCallback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Message sendMessage(Message message, String pushContent, String pushData, RongIMClient.SendMessageCallback callback)
/*      */   {
/*  783 */     return RongIM.getInstance().sendMessage(message, pushContent, pushData, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void sendImageMessage(Conversation.ConversationType type, String targetId, MessageContent content, String pushContent, String pushData, RongIMClient.SendImageMessageCallback callback)
/*      */   {
/*  800 */     RongIM.getInstance().sendImageMessage(type, targetId, content, pushContent, pushData, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void sendImageMessage(Message message, String pushContent, String pushData, RongIMClient.SendImageMessageCallback callback)
/*      */   {
/*  814 */     RongIM.getInstance().sendImageMessage(message, pushContent, pushData, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void sendImageMessage(Message message, String pushContent, String pushData, RongIMClient.SendImageMessageWithUploadListenerCallback callback)
/*      */   {
/*  830 */     RongIM.getInstance().sendImageMessage(message, pushContent, pushData, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void downloadMedia(Conversation.ConversationType conversationType, String targetId, RongIMClient.MediaType mediaType, String imageUrl, RongIMClient.DownloadMediaCallback callback)
/*      */   {
/*  846 */     RongIM.getInstance().downloadMedia(conversationType, targetId, mediaType, imageUrl, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void downloadMedia(String imageUrl, RongIMClient.DownloadMediaCallback callback)
/*      */   {
/*  857 */     RongIM.getInstance().downloadMedia(imageUrl, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getConversationNotificationStatus(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus> callback)
/*      */   {
/*  869 */     RongIM.getInstance().getConversationNotificationStatus(conversationType, targetId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setConversationNotificationStatus(Conversation.ConversationType conversationType, String targetId, Conversation.ConversationNotificationStatus notificationStatus, RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus> callback)
/*      */   {
/*  882 */     RongIM.getInstance().setConversationNotificationStatus(conversationType, targetId, notificationStatus, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setDiscussionInviteStatus(String discussionId, RongIMClient.DiscussionInviteStatus status, RongIMClient.OperationCallback callback)
/*      */   {
/*  894 */     RongIM.getInstance().setDiscussionInviteStatus(discussionId, status, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void syncGroup(List<Group> groups, RongIMClient.OperationCallback callback)
/*      */   {
/*  907 */     RongIM.getInstance().syncGroup(groups, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void joinGroup(String groupId, String groupName, RongIMClient.OperationCallback callback)
/*      */   {
/*  921 */     RongIM.getInstance().joinGroup(groupId, groupName, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void quitGroup(String groupId, RongIMClient.OperationCallback callback)
/*      */   {
/*  934 */     RongIM.getInstance().quitGroup(groupId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public String getCurrentUserId()
/*      */   {
/*  944 */     return RongIM.getInstance().getCurrentUserId();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public long getDeltaTime()
/*      */   {
/*  954 */     return RongIM.getInstance().getDeltaTime();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void joinChatRoom(String chatroomId, int defMessageCount, RongIMClient.OperationCallback callback)
/*      */   {
/*  966 */     RongIM.getInstance().joinChatRoom(chatroomId, defMessageCount, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void joinExistChatRoom(String chatroomId, int defMessageCount, RongIMClient.OperationCallback callback)
/*      */   {
/*  978 */     RongIM.getInstance().joinExistChatRoom(chatroomId, defMessageCount, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void quitChatRoom(String chatroomId, RongIMClient.OperationCallback callback)
/*      */   {
/*  989 */     RongIM.getInstance().quitChatRoom(chatroomId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void clearConversations(RongIMClient.ResultCallback callback, Conversation.ConversationType[] conversationTypes)
/*      */   {
/* 1000 */     RongIM.getInstance().clearConversations(callback, conversationTypes);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean clearConversations(Conversation.ConversationType[] conversationTypes)
/*      */   {
/* 1011 */     return RongIM.getInstance().clearConversations(conversationTypes);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void addToBlacklist(String userId, RongIMClient.OperationCallback callback)
/*      */   {
/* 1022 */     RongIM.getInstance().addToBlacklist(userId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void removeFromBlacklist(String userId, RongIMClient.OperationCallback callback)
/*      */   {
/* 1033 */     RongIM.getInstance().removeFromBlacklist(userId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getBlacklistStatus(String userId, RongIMClient.ResultCallback<RongIMClient.BlacklistStatus> callback)
/*      */   {
/* 1044 */     RongIM.getInstance().getBlacklistStatus(userId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getBlacklist(RongIMClient.GetBlacklistCallback callback)
/*      */   {
/* 1054 */     RongIM.getInstance().getBlacklist(callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void setNotificationQuietHours(String startTime, int spanMinutes, RongIMClient.OperationCallback callback)
/*      */   {
/* 1066 */     RongIM.getInstance().setNotificationQuietHours(startTime, spanMinutes, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void removeNotificationQuietHours(RongIMClient.OperationCallback callback)
/*      */   {
/* 1076 */     RongIM.getInstance().removeNotificationQuietHours(callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getNotificationQuietHours(RongIMClient.GetNotificationQuietHoursCallback callback)
/*      */   {
/* 1086 */     RongIM.getInstance().getNotificationQuietHours(callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getPublicServiceProfile(Conversation.PublicServiceType publicServiceType, String publicServiceId, RongIMClient.ResultCallback<PublicServiceProfile> callback)
/*      */   {
/* 1098 */     RongIM.getInstance().getPublicServiceProfile(publicServiceType, publicServiceId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void searchPublicService(RongIMClient.SearchType searchType, String keywords, RongIMClient.ResultCallback<PublicServiceProfileList> callback)
/*      */   {
/* 1110 */     RongIM.getInstance().searchPublicService(searchType, keywords, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void searchPublicServiceByType(Conversation.PublicServiceType publicServiceType, RongIMClient.SearchType searchType, String keywords, RongIMClient.ResultCallback<PublicServiceProfileList> callback)
/*      */   {
/* 1123 */     RongIM.getInstance().searchPublicServiceByType(publicServiceType, searchType, keywords, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void subscribePublicService(Conversation.PublicServiceType publicServiceType, String publicServiceId, RongIMClient.OperationCallback callback)
/*      */   {
/* 1135 */     RongIM.getInstance().subscribePublicService(publicServiceType, publicServiceId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void unsubscribePublicService(Conversation.PublicServiceType publicServiceType, String publicServiceId, RongIMClient.OperationCallback callback)
/*      */   {
/* 1147 */     RongIM.getInstance().unsubscribePublicService(publicServiceType, publicServiceId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void getPublicServiceList(RongIMClient.ResultCallback<PublicServiceProfileList> callback)
/*      */   {
/* 1157 */     RongIM.getInstance().getPublicServiceList(callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void syncUserData(UserData userData, RongIMClient.OperationCallback callback)
/*      */   {
/* 1169 */     RongIM.getInstance().syncUserData(userData, callback);
/*      */   }
/*      */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.RongIMClientWrapper
 * JD-Core Version:    0.6.0
 */
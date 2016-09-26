/*      */ package io.rong.imlib;
/*      */ 
/*      */ import android.os.Binder;
/*      */ import android.os.IBinder;
/*      */ import android.os.IInterface;
/*      */ import android.os.Parcel;
/*      */ import android.os.Parcelable.Creator;
/*      */ import android.os.RemoteException;
/*      */ import io.rong.imlib.model.Conversation;
/*      */ import io.rong.imlib.model.Group;
/*      */ import io.rong.imlib.model.Message;
/*      */ import io.rong.imlib.model.UserData;
/*      */ import java.util.List;
/*      */ 
/*      */ public abstract interface IHandler extends IInterface
/*      */ {
/*      */   public abstract void connect(String paramString, IStringCallback paramIStringCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void disconnect(boolean paramBoolean, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void registerMessageType(String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract int getTotalUnreadCount()
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract int getUnreadCount(int[] paramArrayOfInt)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract int getUnreadCountById(int paramInt, String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void setOnReceiveMessageListener(OnReceiveMessageListener paramOnReceiveMessageListener)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void setConnectionStatusListener(IConnectionStatusListener paramIConnectionStatusListener)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract Message getMessage(int paramInt)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract Message insertMessage(Message paramMessage)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void sendMessage(Message paramMessage, String paramString1, String paramString2, ISendMessageCallback paramISendMessageCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void sendDirectionalMessage(Message paramMessage, String paramString1, String paramString2, String[] paramArrayOfString, ISendMessageCallback paramISendMessageCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void sendMediaMessage(Message paramMessage, String paramString1, String paramString2, ISendMediaMessageCallback paramISendMediaMessageCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void sendLocationMessage(Message paramMessage, String paramString1, String paramString2, ISendMessageCallback paramISendMessageCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract Message sendStatusMessage(Message paramMessage, ILongCallback paramILongCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract List<Message> getNewestMessages(Conversation paramConversation, int paramInt)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract List<Message> getOlderMessages(Conversation paramConversation, long paramLong, int paramInt)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void getRemoteHistoryMessages(Conversation paramConversation, long paramLong, int paramInt, IResultCallback paramIResultCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract List<Message> getOlderMessagesByObjectName(Conversation paramConversation, String paramString, long paramLong, int paramInt, boolean paramBoolean)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean deleteMessage(int[] paramArrayOfInt)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean deleteConversationMessage(int paramInt, String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean clearMessages(Conversation paramConversation)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean clearMessagesUnreadStatus(Conversation paramConversation)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean setMessageExtra(int paramInt, String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean setMessageReceivedStatus(int paramInt1, int paramInt2)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean setMessageSentStatus(int paramInt1, int paramInt2)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract Message getMessageByUid(String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract List<Conversation> getConversationList()
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract List<Conversation> getConversationListByType(int[] paramArrayOfInt)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract Conversation getConversation(int paramInt, String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean removeConversation(int paramInt, String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean saveConversationDraft(Conversation paramConversation, String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract String getConversationDraft(Conversation paramConversation)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean cleanConversationDraft(Conversation paramConversation)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void getConversationNotificationStatus(int paramInt, String paramString, ILongCallback paramILongCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void setConversationNotificationStatus(int paramInt1, String paramString, int paramInt2, ILongCallback paramILongCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean setConversationTopStatus(int paramInt, String paramString, boolean paramBoolean)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract int getConversationUnreadCount(Conversation paramConversation)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean clearConversations(int[] paramArrayOfInt)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void setNotificationQuietHours(String paramString, int paramInt, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void removeNotificationQuietHours(IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void getNotificationQuietHours(IGetNotificationQuietHoursCallback paramIGetNotificationQuietHoursCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean updateConversationInfo(int paramInt, String paramString1, String paramString2, String paramString3)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void getDiscussion(String paramString, IResultCallback paramIResultCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void setDiscussionName(String paramString1, String paramString2, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void createDiscussion(String paramString, List<String> paramList, IResultCallback paramIResultCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void addMemberToDiscussion(String paramString, List<String> paramList, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void removeDiscussionMember(String paramString1, String paramString2, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void quitDiscussion(String paramString, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void syncGroup(List<Group> paramList, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void joinGroup(String paramString1, String paramString2, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void quitGroup(String paramString, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void getChatRoomInfo(String paramString, int paramInt1, int paramInt2, IResultCallback paramIResultCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void reJoinChatRoom(String paramString, int paramInt, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void joinChatRoom(String paramString, int paramInt, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void joinExistChatRoom(String paramString, int paramInt, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void quitChatRoom(String paramString, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void searchPublicService(String paramString, int paramInt1, int paramInt2, IResultCallback paramIResultCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void subscribePublicService(String paramString, int paramInt, boolean paramBoolean, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void getPublicServiceProfile(String paramString, int paramInt, IResultCallback paramIResultCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void getPublicServiceList(IResultCallback paramIResultCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean validateAuth(String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void uploadMedia(Message paramMessage, IUploadCallback paramIUploadCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void downloadMedia(Conversation paramConversation, int paramInt, String paramString, IDownloadMediaCallback paramIDownloadMediaCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void downloadMediaMessage(Message paramMessage, IDownloadMediaMessageCallback paramIDownloadMediaMessageCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void cancelDownloadMediaMessage(Message paramMessage, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract long getDeltaTime()
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void setDiscussionInviteStatus(String paramString, int paramInt, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void addToBlacklist(String paramString, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void removeFromBlacklist(String paramString, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract String getTextMessageDraft(Conversation paramConversation)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean saveTextMessageDraft(Conversation paramConversation, String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean clearTextMessageDraft(Conversation paramConversation)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void getBlacklist(IStringCallback paramIStringCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void getBlacklistStatus(String paramString, IIntegerCallback paramIIntegerCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void setUserData(UserData paramUserData, IOperationCallback paramIOperationCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract int setupRealTimeLocation(int paramInt, String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract int startRealTimeLocation(int paramInt, String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract int joinRealTimeLocation(int paramInt, String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void quitRealTimeLocation(int paramInt, String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract List<String> getRealTimeLocationParticipants(int paramInt, String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void addRealTimeLocationListener(int paramInt, String paramString, IRealTimeLocationListener paramIRealTimeLocationListener)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract int getRealTimeLocationCurrentState(int paramInt, String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void updateRealTimeLocationStatus(int paramInt, String paramString, double paramDouble1, double paramDouble2)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean updateMessageReceiptStatus(String paramString, int paramInt, long paramLong)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean clearUnreadByReceipt(int paramInt, String paramString, long paramLong)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract long getSendTimeByMessageId(int paramInt)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void getVoIPKey(int paramInt, String paramString1, String paramString2, IStringCallback paramIStringCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract String getVoIPCallInfo()
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract String getCurrentUserId()
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void setServerInfo(String paramString1, String paramString2)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract long getNaviCachedTime()
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract String getCMPServer()
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void getPCAuthConfig(IStringCallback paramIStringCallback)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean setMessageContent(int paramInt, byte[] paramArrayOfByte, String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract List<Message> getUnreadMentionedMessages(int paramInt, String paramString)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract boolean updateReadReceiptRequestInfo(String paramString1, String paramString2)
/*      */     throws RemoteException;
/*      */ 
/*      */   public abstract void registerCmdMsgType(List<String> paramList)
/*      */     throws RemoteException;
/*      */ 
/*      */   public static abstract class Stub extends Binder
/*      */     implements IHandler
/*      */   {
/*      */     private static final String DESCRIPTOR = "io.rong.imlib.IHandler";
/*      */     static final int TRANSACTION_connect = 1;
/*      */     static final int TRANSACTION_disconnect = 2;
/*      */     static final int TRANSACTION_registerMessageType = 3;
/*      */     static final int TRANSACTION_getTotalUnreadCount = 4;
/*      */     static final int TRANSACTION_getUnreadCount = 5;
/*      */     static final int TRANSACTION_getUnreadCountById = 6;
/*      */     static final int TRANSACTION_setOnReceiveMessageListener = 7;
/*      */     static final int TRANSACTION_setConnectionStatusListener = 8;
/*      */     static final int TRANSACTION_getMessage = 9;
/*      */     static final int TRANSACTION_insertMessage = 10;
/*      */     static final int TRANSACTION_sendMessage = 11;
/*      */     static final int TRANSACTION_sendDirectionalMessage = 12;
/*      */     static final int TRANSACTION_sendMediaMessage = 13;
/*      */     static final int TRANSACTION_sendLocationMessage = 14;
/*      */     static final int TRANSACTION_sendStatusMessage = 15;
/*      */     static final int TRANSACTION_getNewestMessages = 16;
/*      */     static final int TRANSACTION_getOlderMessages = 17;
/*      */     static final int TRANSACTION_getRemoteHistoryMessages = 18;
/*      */     static final int TRANSACTION_getOlderMessagesByObjectName = 19;
/*      */     static final int TRANSACTION_deleteMessage = 20;
/*      */     static final int TRANSACTION_deleteConversationMessage = 21;
/*      */     static final int TRANSACTION_clearMessages = 22;
/*      */     static final int TRANSACTION_clearMessagesUnreadStatus = 23;
/*      */     static final int TRANSACTION_setMessageExtra = 24;
/*      */     static final int TRANSACTION_setMessageReceivedStatus = 25;
/*      */     static final int TRANSACTION_setMessageSentStatus = 26;
/*      */     static final int TRANSACTION_getMessageByUid = 27;
/*      */     static final int TRANSACTION_getConversationList = 28;
/*      */     static final int TRANSACTION_getConversationListByType = 29;
/*      */     static final int TRANSACTION_getConversation = 30;
/*      */     static final int TRANSACTION_removeConversation = 31;
/*      */     static final int TRANSACTION_saveConversationDraft = 32;
/*      */     static final int TRANSACTION_getConversationDraft = 33;
/*      */     static final int TRANSACTION_cleanConversationDraft = 34;
/*      */     static final int TRANSACTION_getConversationNotificationStatus = 35;
/*      */     static final int TRANSACTION_setConversationNotificationStatus = 36;
/*      */     static final int TRANSACTION_setConversationTopStatus = 37;
/*      */     static final int TRANSACTION_getConversationUnreadCount = 38;
/*      */     static final int TRANSACTION_clearConversations = 39;
/*      */     static final int TRANSACTION_setNotificationQuietHours = 40;
/*      */     static final int TRANSACTION_removeNotificationQuietHours = 41;
/*      */     static final int TRANSACTION_getNotificationQuietHours = 42;
/*      */     static final int TRANSACTION_updateConversationInfo = 43;
/*      */     static final int TRANSACTION_getDiscussion = 44;
/*      */     static final int TRANSACTION_setDiscussionName = 45;
/*      */     static final int TRANSACTION_createDiscussion = 46;
/*      */     static final int TRANSACTION_addMemberToDiscussion = 47;
/*      */     static final int TRANSACTION_removeDiscussionMember = 48;
/*      */     static final int TRANSACTION_quitDiscussion = 49;
/*      */     static final int TRANSACTION_syncGroup = 50;
/*      */     static final int TRANSACTION_joinGroup = 51;
/*      */     static final int TRANSACTION_quitGroup = 52;
/*      */     static final int TRANSACTION_getChatRoomInfo = 53;
/*      */     static final int TRANSACTION_reJoinChatRoom = 54;
/*      */     static final int TRANSACTION_joinChatRoom = 55;
/*      */     static final int TRANSACTION_joinExistChatRoom = 56;
/*      */     static final int TRANSACTION_quitChatRoom = 57;
/*      */     static final int TRANSACTION_searchPublicService = 58;
/*      */     static final int TRANSACTION_subscribePublicService = 59;
/*      */     static final int TRANSACTION_getPublicServiceProfile = 60;
/*      */     static final int TRANSACTION_getPublicServiceList = 61;
/*      */     static final int TRANSACTION_validateAuth = 62;
/*      */     static final int TRANSACTION_uploadMedia = 63;
/*      */     static final int TRANSACTION_downloadMedia = 64;
/*      */     static final int TRANSACTION_downloadMediaMessage = 65;
/*      */     static final int TRANSACTION_cancelDownloadMediaMessage = 66;
/*      */     static final int TRANSACTION_getDeltaTime = 67;
/*      */     static final int TRANSACTION_setDiscussionInviteStatus = 68;
/*      */     static final int TRANSACTION_addToBlacklist = 69;
/*      */     static final int TRANSACTION_removeFromBlacklist = 70;
/*      */     static final int TRANSACTION_getTextMessageDraft = 71;
/*      */     static final int TRANSACTION_saveTextMessageDraft = 72;
/*      */     static final int TRANSACTION_clearTextMessageDraft = 73;
/*      */     static final int TRANSACTION_getBlacklist = 74;
/*      */     static final int TRANSACTION_getBlacklistStatus = 75;
/*      */     static final int TRANSACTION_setUserData = 76;
/*      */     static final int TRANSACTION_setupRealTimeLocation = 77;
/*      */     static final int TRANSACTION_startRealTimeLocation = 78;
/*      */     static final int TRANSACTION_joinRealTimeLocation = 79;
/*      */     static final int TRANSACTION_quitRealTimeLocation = 80;
/*      */     static final int TRANSACTION_getRealTimeLocationParticipants = 81;
/*      */     static final int TRANSACTION_addRealTimeLocationListener = 82;
/*      */     static final int TRANSACTION_getRealTimeLocationCurrentState = 83;
/*      */     static final int TRANSACTION_updateRealTimeLocationStatus = 84;
/*      */     static final int TRANSACTION_updateMessageReceiptStatus = 85;
/*      */     static final int TRANSACTION_clearUnreadByReceipt = 86;
/*      */     static final int TRANSACTION_getSendTimeByMessageId = 87;
/*      */     static final int TRANSACTION_getVoIPKey = 88;
/*      */     static final int TRANSACTION_getVoIPCallInfo = 89;
/*      */     static final int TRANSACTION_getCurrentUserId = 90;
/*      */     static final int TRANSACTION_setServerInfo = 91;
/*      */     static final int TRANSACTION_getNaviCachedTime = 92;
/*      */     static final int TRANSACTION_getCMPServer = 93;
/*      */     static final int TRANSACTION_getPCAuthConfig = 94;
/*      */     static final int TRANSACTION_setMessageContent = 95;
/*      */     static final int TRANSACTION_getUnreadMentionedMessages = 96;
/*      */     static final int TRANSACTION_updateReadReceiptRequestInfo = 97;
/*      */     static final int TRANSACTION_registerCmdMsgType = 98;
/*      */ 
/*      */     public Stub()
/*      */     {
/*   17 */       attachInterface(this, "io.rong.imlib.IHandler");
/*      */     }
/*      */ 
/*      */     public static IHandler asInterface(IBinder obj)
/*      */     {
/*   25 */       if (obj == null) {
/*   26 */         return null;
/*      */       }
/*   28 */       IInterface iin = obj.queryLocalInterface("io.rong.imlib.IHandler");
/*   29 */       if ((iin != null) && ((iin instanceof IHandler))) {
/*   30 */         return (IHandler)iin;
/*      */       }
/*   32 */       return new Proxy(obj);
/*      */     }
/*      */ 
/*      */     public IBinder asBinder() {
/*   36 */       return this;
/*      */     }
/*      */ 
/*      */     public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
/*   40 */       switch (code)
/*      */       {
/*      */       case 1598968902:
/*   44 */         reply.writeString("io.rong.imlib.IHandler");
/*   45 */         return true;
/*      */       case 1:
/*   49 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*   51 */         String _arg0 = data.readString();
/*      */ 
/*   53 */         IStringCallback _arg1 = IStringCallback.Stub.asInterface(data.readStrongBinder());
/*   54 */         connect(_arg0, _arg1);
/*   55 */         reply.writeNoException();
/*   56 */         return true;
/*      */       case 2:
/*   60 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*   62 */         boolean _arg0 = 0 != data.readInt();
/*      */ 
/*   64 */         IOperationCallback _arg1 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*   65 */         disconnect(_arg0, _arg1);
/*   66 */         reply.writeNoException();
/*   67 */         return true;
/*      */       case 3:
/*   71 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*   73 */         String _arg0 = data.readString();
/*   74 */         registerMessageType(_arg0);
/*   75 */         reply.writeNoException();
/*   76 */         return true;
/*      */       case 4:
/*   80 */         data.enforceInterface("io.rong.imlib.IHandler");
/*   81 */         int _result = getTotalUnreadCount();
/*   82 */         reply.writeNoException();
/*   83 */         reply.writeInt(_result);
/*   84 */         return true;
/*      */       case 5:
/*   88 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*   90 */         int[] _arg0 = data.createIntArray();
/*   91 */         int _result = getUnreadCount(_arg0);
/*   92 */         reply.writeNoException();
/*   93 */         reply.writeInt(_result);
/*   94 */         return true;
/*      */       case 6:
/*   98 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  100 */         int _arg0 = data.readInt();
/*      */ 
/*  102 */         String _arg1 = data.readString();
/*  103 */         int _result = getUnreadCountById(_arg0, _arg1);
/*  104 */         reply.writeNoException();
/*  105 */         reply.writeInt(_result);
/*  106 */         return true;
/*      */       case 7:
/*  110 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  112 */         OnReceiveMessageListener _arg0 = OnReceiveMessageListener.Stub.asInterface(data.readStrongBinder());
/*  113 */         setOnReceiveMessageListener(_arg0);
/*  114 */         reply.writeNoException();
/*  115 */         return true;
/*      */       case 8:
/*  119 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  121 */         IConnectionStatusListener _arg0 = IConnectionStatusListener.Stub.asInterface(data.readStrongBinder());
/*  122 */         setConnectionStatusListener(_arg0);
/*  123 */         reply.writeNoException();
/*  124 */         return true;
/*      */       case 9:
/*  128 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  130 */         int _arg0 = data.readInt();
/*  131 */         Message _result = getMessage(_arg0);
/*  132 */         reply.writeNoException();
/*  133 */         if (_result != null) {
/*  134 */           reply.writeInt(1);
/*  135 */           _result.writeToParcel(reply, 1);
/*      */         }
/*      */         else {
/*  138 */           reply.writeInt(0);
/*      */         }
/*  140 */         return true;
/*      */       case 10:
/*  144 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Message _arg0;
/*      */         Message _arg0;
/*  146 */         if (0 != data.readInt()) {
/*  147 */           _arg0 = (Message)Message.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  150 */           _arg0 = null;
/*      */         }
/*  152 */         Message _result = insertMessage(_arg0);
/*  153 */         reply.writeNoException();
/*  154 */         if (_result != null) {
/*  155 */           reply.writeInt(1);
/*  156 */           _result.writeToParcel(reply, 1);
/*      */         }
/*      */         else {
/*  159 */           reply.writeInt(0);
/*      */         }
/*  161 */         return true;
/*      */       case 11:
/*  165 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Message _arg0;
/*      */         Message _arg0;
/*  167 */         if (0 != data.readInt()) {
/*  168 */           _arg0 = (Message)Message.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  171 */           _arg0 = null;
/*      */         }
/*      */ 
/*  174 */         String _arg1 = data.readString();
/*      */ 
/*  176 */         String _arg2 = data.readString();
/*      */ 
/*  178 */         ISendMessageCallback _arg3 = ISendMessageCallback.Stub.asInterface(data.readStrongBinder());
/*  179 */         sendMessage(_arg0, _arg1, _arg2, _arg3);
/*  180 */         reply.writeNoException();
/*  181 */         return true;
/*      */       case 12:
/*  185 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Message _arg0;
/*      */         Message _arg0;
/*  187 */         if (0 != data.readInt()) {
/*  188 */           _arg0 = (Message)Message.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  191 */           _arg0 = null;
/*      */         }
/*      */ 
/*  194 */         String _arg1 = data.readString();
/*      */ 
/*  196 */         String _arg2 = data.readString();
/*      */ 
/*  198 */         String[] _arg3 = data.createStringArray();
/*      */ 
/*  200 */         ISendMessageCallback _arg4 = ISendMessageCallback.Stub.asInterface(data.readStrongBinder());
/*  201 */         sendDirectionalMessage(_arg0, _arg1, _arg2, _arg3, _arg4);
/*  202 */         reply.writeNoException();
/*  203 */         return true;
/*      */       case 13:
/*  207 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Message _arg0;
/*      */         Message _arg0;
/*  209 */         if (0 != data.readInt()) {
/*  210 */           _arg0 = (Message)Message.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  213 */           _arg0 = null;
/*      */         }
/*      */ 
/*  216 */         String _arg1 = data.readString();
/*      */ 
/*  218 */         String _arg2 = data.readString();
/*      */ 
/*  220 */         ISendMediaMessageCallback _arg3 = ISendMediaMessageCallback.Stub.asInterface(data.readStrongBinder());
/*  221 */         sendMediaMessage(_arg0, _arg1, _arg2, _arg3);
/*  222 */         reply.writeNoException();
/*  223 */         return true;
/*      */       case 14:
/*  227 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Message _arg0;
/*      */         Message _arg0;
/*  229 */         if (0 != data.readInt()) {
/*  230 */           _arg0 = (Message)Message.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  233 */           _arg0 = null;
/*      */         }
/*      */ 
/*  236 */         String _arg1 = data.readString();
/*      */ 
/*  238 */         String _arg2 = data.readString();
/*      */ 
/*  240 */         ISendMessageCallback _arg3 = ISendMessageCallback.Stub.asInterface(data.readStrongBinder());
/*  241 */         sendLocationMessage(_arg0, _arg1, _arg2, _arg3);
/*  242 */         reply.writeNoException();
/*  243 */         return true;
/*      */       case 15:
/*  247 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Message _arg0;
/*      */         Message _arg0;
/*  249 */         if (0 != data.readInt()) {
/*  250 */           _arg0 = (Message)Message.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  253 */           _arg0 = null;
/*      */         }
/*      */ 
/*  256 */         ILongCallback _arg1 = ILongCallback.Stub.asInterface(data.readStrongBinder());
/*  257 */         Message _result = sendStatusMessage(_arg0, _arg1);
/*  258 */         reply.writeNoException();
/*  259 */         if (_result != null) {
/*  260 */           reply.writeInt(1);
/*  261 */           _result.writeToParcel(reply, 1);
/*      */         }
/*      */         else {
/*  264 */           reply.writeInt(0);
/*      */         }
/*  266 */         return true;
/*      */       case 16:
/*  270 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Conversation _arg0;
/*      */         Conversation _arg0;
/*  272 */         if (0 != data.readInt()) {
/*  273 */           _arg0 = (Conversation)Conversation.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  276 */           _arg0 = null;
/*      */         }
/*      */ 
/*  279 */         int _arg1 = data.readInt();
/*  280 */         List _result = getNewestMessages(_arg0, _arg1);
/*  281 */         reply.writeNoException();
/*  282 */         reply.writeTypedList(_result);
/*  283 */         return true;
/*      */       case 17:
/*  287 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Conversation _arg0;
/*      */         Conversation _arg0;
/*  289 */         if (0 != data.readInt()) {
/*  290 */           _arg0 = (Conversation)Conversation.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  293 */           _arg0 = null;
/*      */         }
/*      */ 
/*  296 */         long _arg1 = data.readLong();
/*      */ 
/*  298 */         int _arg2 = data.readInt();
/*  299 */         List _result = getOlderMessages(_arg0, _arg1, _arg2);
/*  300 */         reply.writeNoException();
/*  301 */         reply.writeTypedList(_result);
/*  302 */         return true;
/*      */       case 18:
/*  306 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Conversation _arg0;
/*      */         Conversation _arg0;
/*  308 */         if (0 != data.readInt()) {
/*  309 */           _arg0 = (Conversation)Conversation.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  312 */           _arg0 = null;
/*      */         }
/*      */ 
/*  315 */         long _arg1 = data.readLong();
/*      */ 
/*  317 */         int _arg2 = data.readInt();
/*      */ 
/*  319 */         IResultCallback _arg3 = IResultCallback.Stub.asInterface(data.readStrongBinder());
/*  320 */         getRemoteHistoryMessages(_arg0, _arg1, _arg2, _arg3);
/*  321 */         reply.writeNoException();
/*  322 */         return true;
/*      */       case 19:
/*  326 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Conversation _arg0;
/*      */         Conversation _arg0;
/*  328 */         if (0 != data.readInt()) {
/*  329 */           _arg0 = (Conversation)Conversation.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  332 */           _arg0 = null;
/*      */         }
/*      */ 
/*  335 */         String _arg1 = data.readString();
/*      */ 
/*  337 */         long _arg2 = data.readLong();
/*      */ 
/*  339 */         int _arg3 = data.readInt();
/*      */ 
/*  341 */         boolean _arg4 = 0 != data.readInt();
/*  342 */         List _result = getOlderMessagesByObjectName(_arg0, _arg1, _arg2, _arg3, _arg4);
/*  343 */         reply.writeNoException();
/*  344 */         reply.writeTypedList(_result);
/*  345 */         return true;
/*      */       case 20:
/*  349 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  351 */         int[] _arg0 = data.createIntArray();
/*  352 */         boolean _result = deleteMessage(_arg0);
/*  353 */         reply.writeNoException();
/*  354 */         reply.writeInt(_result ? 1 : 0);
/*  355 */         return true;
/*      */       case 21:
/*  359 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  361 */         int _arg0 = data.readInt();
/*      */ 
/*  363 */         String _arg1 = data.readString();
/*  364 */         boolean _result = deleteConversationMessage(_arg0, _arg1);
/*  365 */         reply.writeNoException();
/*  366 */         reply.writeInt(_result ? 1 : 0);
/*  367 */         return true;
/*      */       case 22:
/*  371 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Conversation _arg0;
/*      */         Conversation _arg0;
/*  373 */         if (0 != data.readInt()) {
/*  374 */           _arg0 = (Conversation)Conversation.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  377 */           _arg0 = null;
/*      */         }
/*  379 */         boolean _result = clearMessages(_arg0);
/*  380 */         reply.writeNoException();
/*  381 */         reply.writeInt(_result ? 1 : 0);
/*  382 */         return true;
/*      */       case 23:
/*  386 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Conversation _arg0;
/*      */         Conversation _arg0;
/*  388 */         if (0 != data.readInt()) {
/*  389 */           _arg0 = (Conversation)Conversation.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  392 */           _arg0 = null;
/*      */         }
/*  394 */         boolean _result = clearMessagesUnreadStatus(_arg0);
/*  395 */         reply.writeNoException();
/*  396 */         reply.writeInt(_result ? 1 : 0);
/*  397 */         return true;
/*      */       case 24:
/*  401 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  403 */         int _arg0 = data.readInt();
/*      */ 
/*  405 */         String _arg1 = data.readString();
/*  406 */         boolean _result = setMessageExtra(_arg0, _arg1);
/*  407 */         reply.writeNoException();
/*  408 */         reply.writeInt(_result ? 1 : 0);
/*  409 */         return true;
/*      */       case 25:
/*  413 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  415 */         int _arg0 = data.readInt();
/*      */ 
/*  417 */         int _arg1 = data.readInt();
/*  418 */         boolean _result = setMessageReceivedStatus(_arg0, _arg1);
/*  419 */         reply.writeNoException();
/*  420 */         reply.writeInt(_result ? 1 : 0);
/*  421 */         return true;
/*      */       case 26:
/*  425 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  427 */         int _arg0 = data.readInt();
/*      */ 
/*  429 */         int _arg1 = data.readInt();
/*  430 */         boolean _result = setMessageSentStatus(_arg0, _arg1);
/*  431 */         reply.writeNoException();
/*  432 */         reply.writeInt(_result ? 1 : 0);
/*  433 */         return true;
/*      */       case 27:
/*  437 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  439 */         String _arg0 = data.readString();
/*  440 */         Message _result = getMessageByUid(_arg0);
/*  441 */         reply.writeNoException();
/*  442 */         if (_result != null) {
/*  443 */           reply.writeInt(1);
/*  444 */           _result.writeToParcel(reply, 1);
/*      */         }
/*      */         else {
/*  447 */           reply.writeInt(0);
/*      */         }
/*  449 */         return true;
/*      */       case 28:
/*  453 */         data.enforceInterface("io.rong.imlib.IHandler");
/*  454 */         List _result = getConversationList();
/*  455 */         reply.writeNoException();
/*  456 */         reply.writeTypedList(_result);
/*  457 */         return true;
/*      */       case 29:
/*  461 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  463 */         int[] _arg0 = data.createIntArray();
/*  464 */         List _result = getConversationListByType(_arg0);
/*  465 */         reply.writeNoException();
/*  466 */         reply.writeTypedList(_result);
/*  467 */         return true;
/*      */       case 30:
/*  471 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  473 */         int _arg0 = data.readInt();
/*      */ 
/*  475 */         String _arg1 = data.readString();
/*  476 */         Conversation _result = getConversation(_arg0, _arg1);
/*  477 */         reply.writeNoException();
/*  478 */         if (_result != null) {
/*  479 */           reply.writeInt(1);
/*  480 */           _result.writeToParcel(reply, 1);
/*      */         }
/*      */         else {
/*  483 */           reply.writeInt(0);
/*      */         }
/*  485 */         return true;
/*      */       case 31:
/*  489 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  491 */         int _arg0 = data.readInt();
/*      */ 
/*  493 */         String _arg1 = data.readString();
/*  494 */         boolean _result = removeConversation(_arg0, _arg1);
/*  495 */         reply.writeNoException();
/*  496 */         reply.writeInt(_result ? 1 : 0);
/*  497 */         return true;
/*      */       case 32:
/*  501 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Conversation _arg0;
/*      */         Conversation _arg0;
/*  503 */         if (0 != data.readInt()) {
/*  504 */           _arg0 = (Conversation)Conversation.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  507 */           _arg0 = null;
/*      */         }
/*      */ 
/*  510 */         String _arg1 = data.readString();
/*  511 */         boolean _result = saveConversationDraft(_arg0, _arg1);
/*  512 */         reply.writeNoException();
/*  513 */         reply.writeInt(_result ? 1 : 0);
/*  514 */         return true;
/*      */       case 33:
/*  518 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Conversation _arg0;
/*      */         Conversation _arg0;
/*  520 */         if (0 != data.readInt()) {
/*  521 */           _arg0 = (Conversation)Conversation.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  524 */           _arg0 = null;
/*      */         }
/*  526 */         String _result = getConversationDraft(_arg0);
/*  527 */         reply.writeNoException();
/*  528 */         reply.writeString(_result);
/*  529 */         return true;
/*      */       case 34:
/*  533 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Conversation _arg0;
/*      */         Conversation _arg0;
/*  535 */         if (0 != data.readInt()) {
/*  536 */           _arg0 = (Conversation)Conversation.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  539 */           _arg0 = null;
/*      */         }
/*  541 */         boolean _result = cleanConversationDraft(_arg0);
/*  542 */         reply.writeNoException();
/*  543 */         reply.writeInt(_result ? 1 : 0);
/*  544 */         return true;
/*      */       case 35:
/*  548 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  550 */         int _arg0 = data.readInt();
/*      */ 
/*  552 */         String _arg1 = data.readString();
/*      */ 
/*  554 */         ILongCallback _arg2 = ILongCallback.Stub.asInterface(data.readStrongBinder());
/*  555 */         getConversationNotificationStatus(_arg0, _arg1, _arg2);
/*  556 */         reply.writeNoException();
/*  557 */         return true;
/*      */       case 36:
/*  561 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  563 */         int _arg0 = data.readInt();
/*      */ 
/*  565 */         String _arg1 = data.readString();
/*      */ 
/*  567 */         int _arg2 = data.readInt();
/*      */ 
/*  569 */         ILongCallback _arg3 = ILongCallback.Stub.asInterface(data.readStrongBinder());
/*  570 */         setConversationNotificationStatus(_arg0, _arg1, _arg2, _arg3);
/*  571 */         reply.writeNoException();
/*  572 */         return true;
/*      */       case 37:
/*  576 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  578 */         int _arg0 = data.readInt();
/*      */ 
/*  580 */         String _arg1 = data.readString();
/*      */ 
/*  582 */         boolean _arg2 = 0 != data.readInt();
/*  583 */         boolean _result = setConversationTopStatus(_arg0, _arg1, _arg2);
/*  584 */         reply.writeNoException();
/*  585 */         reply.writeInt(_result ? 1 : 0);
/*  586 */         return true;
/*      */       case 38:
/*  590 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Conversation _arg0;
/*      */         Conversation _arg0;
/*  592 */         if (0 != data.readInt()) {
/*  593 */           _arg0 = (Conversation)Conversation.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  596 */           _arg0 = null;
/*      */         }
/*  598 */         int _result = getConversationUnreadCount(_arg0);
/*  599 */         reply.writeNoException();
/*  600 */         reply.writeInt(_result);
/*  601 */         return true;
/*      */       case 39:
/*  605 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  607 */         int[] _arg0 = data.createIntArray();
/*  608 */         boolean _result = clearConversations(_arg0);
/*  609 */         reply.writeNoException();
/*  610 */         reply.writeInt(_result ? 1 : 0);
/*  611 */         return true;
/*      */       case 40:
/*  615 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  617 */         String _arg0 = data.readString();
/*      */ 
/*  619 */         int _arg1 = data.readInt();
/*      */ 
/*  621 */         IOperationCallback _arg2 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*  622 */         setNotificationQuietHours(_arg0, _arg1, _arg2);
/*  623 */         reply.writeNoException();
/*  624 */         return true;
/*      */       case 41:
/*  628 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  630 */         IOperationCallback _arg0 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*  631 */         removeNotificationQuietHours(_arg0);
/*  632 */         reply.writeNoException();
/*  633 */         return true;
/*      */       case 42:
/*  637 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  639 */         IGetNotificationQuietHoursCallback _arg0 = IGetNotificationQuietHoursCallback.Stub.asInterface(data.readStrongBinder());
/*  640 */         getNotificationQuietHours(_arg0);
/*  641 */         reply.writeNoException();
/*  642 */         return true;
/*      */       case 43:
/*  646 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  648 */         int _arg0 = data.readInt();
/*      */ 
/*  650 */         String _arg1 = data.readString();
/*      */ 
/*  652 */         String _arg2 = data.readString();
/*      */ 
/*  654 */         String _arg3 = data.readString();
/*  655 */         boolean _result = updateConversationInfo(_arg0, _arg1, _arg2, _arg3);
/*  656 */         reply.writeNoException();
/*  657 */         reply.writeInt(_result ? 1 : 0);
/*  658 */         return true;
/*      */       case 44:
/*  662 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  664 */         String _arg0 = data.readString();
/*      */ 
/*  666 */         IResultCallback _arg1 = IResultCallback.Stub.asInterface(data.readStrongBinder());
/*  667 */         getDiscussion(_arg0, _arg1);
/*  668 */         reply.writeNoException();
/*  669 */         return true;
/*      */       case 45:
/*  673 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  675 */         String _arg0 = data.readString();
/*      */ 
/*  677 */         String _arg1 = data.readString();
/*      */ 
/*  679 */         IOperationCallback _arg2 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*  680 */         setDiscussionName(_arg0, _arg1, _arg2);
/*  681 */         reply.writeNoException();
/*  682 */         return true;
/*      */       case 46:
/*  686 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  688 */         String _arg0 = data.readString();
/*      */ 
/*  690 */         List _arg1 = data.createStringArrayList();
/*      */ 
/*  692 */         IResultCallback _arg2 = IResultCallback.Stub.asInterface(data.readStrongBinder());
/*  693 */         createDiscussion(_arg0, _arg1, _arg2);
/*  694 */         reply.writeNoException();
/*  695 */         return true;
/*      */       case 47:
/*  699 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  701 */         String _arg0 = data.readString();
/*      */ 
/*  703 */         List _arg1 = data.createStringArrayList();
/*      */ 
/*  705 */         IOperationCallback _arg2 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*  706 */         addMemberToDiscussion(_arg0, _arg1, _arg2);
/*  707 */         reply.writeNoException();
/*  708 */         return true;
/*      */       case 48:
/*  712 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  714 */         String _arg0 = data.readString();
/*      */ 
/*  716 */         String _arg1 = data.readString();
/*      */ 
/*  718 */         IOperationCallback _arg2 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*  719 */         removeDiscussionMember(_arg0, _arg1, _arg2);
/*  720 */         reply.writeNoException();
/*  721 */         return true;
/*      */       case 49:
/*  725 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  727 */         String _arg0 = data.readString();
/*      */ 
/*  729 */         IOperationCallback _arg1 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*  730 */         quitDiscussion(_arg0, _arg1);
/*  731 */         reply.writeNoException();
/*  732 */         return true;
/*      */       case 50:
/*  736 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  738 */         List _arg0 = data.createTypedArrayList(Group.CREATOR);
/*      */ 
/*  740 */         IOperationCallback _arg1 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*  741 */         syncGroup(_arg0, _arg1);
/*  742 */         reply.writeNoException();
/*  743 */         return true;
/*      */       case 51:
/*  747 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  749 */         String _arg0 = data.readString();
/*      */ 
/*  751 */         String _arg1 = data.readString();
/*      */ 
/*  753 */         IOperationCallback _arg2 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*  754 */         joinGroup(_arg0, _arg1, _arg2);
/*  755 */         reply.writeNoException();
/*  756 */         return true;
/*      */       case 52:
/*  760 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  762 */         String _arg0 = data.readString();
/*      */ 
/*  764 */         IOperationCallback _arg1 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*  765 */         quitGroup(_arg0, _arg1);
/*  766 */         reply.writeNoException();
/*  767 */         return true;
/*      */       case 53:
/*  771 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  773 */         String _arg0 = data.readString();
/*      */ 
/*  775 */         int _arg1 = data.readInt();
/*      */ 
/*  777 */         int _arg2 = data.readInt();
/*      */ 
/*  779 */         IResultCallback _arg3 = IResultCallback.Stub.asInterface(data.readStrongBinder());
/*  780 */         getChatRoomInfo(_arg0, _arg1, _arg2, _arg3);
/*  781 */         reply.writeNoException();
/*  782 */         return true;
/*      */       case 54:
/*  786 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  788 */         String _arg0 = data.readString();
/*      */ 
/*  790 */         int _arg1 = data.readInt();
/*      */ 
/*  792 */         IOperationCallback _arg2 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*  793 */         reJoinChatRoom(_arg0, _arg1, _arg2);
/*  794 */         reply.writeNoException();
/*  795 */         return true;
/*      */       case 55:
/*  799 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  801 */         String _arg0 = data.readString();
/*      */ 
/*  803 */         int _arg1 = data.readInt();
/*      */ 
/*  805 */         IOperationCallback _arg2 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*  806 */         joinChatRoom(_arg0, _arg1, _arg2);
/*  807 */         reply.writeNoException();
/*  808 */         return true;
/*      */       case 56:
/*  812 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  814 */         String _arg0 = data.readString();
/*      */ 
/*  816 */         int _arg1 = data.readInt();
/*      */ 
/*  818 */         IOperationCallback _arg2 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*  819 */         joinExistChatRoom(_arg0, _arg1, _arg2);
/*  820 */         reply.writeNoException();
/*  821 */         return true;
/*      */       case 57:
/*  825 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  827 */         String _arg0 = data.readString();
/*      */ 
/*  829 */         IOperationCallback _arg1 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*  830 */         quitChatRoom(_arg0, _arg1);
/*  831 */         reply.writeNoException();
/*  832 */         return true;
/*      */       case 58:
/*  836 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  838 */         String _arg0 = data.readString();
/*      */ 
/*  840 */         int _arg1 = data.readInt();
/*      */ 
/*  842 */         int _arg2 = data.readInt();
/*      */ 
/*  844 */         IResultCallback _arg3 = IResultCallback.Stub.asInterface(data.readStrongBinder());
/*  845 */         searchPublicService(_arg0, _arg1, _arg2, _arg3);
/*  846 */         reply.writeNoException();
/*  847 */         return true;
/*      */       case 59:
/*  851 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  853 */         String _arg0 = data.readString();
/*      */ 
/*  855 */         int _arg1 = data.readInt();
/*      */ 
/*  857 */         boolean _arg2 = 0 != data.readInt();
/*      */ 
/*  859 */         IOperationCallback _arg3 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*  860 */         subscribePublicService(_arg0, _arg1, _arg2, _arg3);
/*  861 */         reply.writeNoException();
/*  862 */         return true;
/*      */       case 60:
/*  866 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  868 */         String _arg0 = data.readString();
/*      */ 
/*  870 */         int _arg1 = data.readInt();
/*      */ 
/*  872 */         IResultCallback _arg2 = IResultCallback.Stub.asInterface(data.readStrongBinder());
/*  873 */         getPublicServiceProfile(_arg0, _arg1, _arg2);
/*  874 */         reply.writeNoException();
/*  875 */         return true;
/*      */       case 61:
/*  879 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  881 */         IResultCallback _arg0 = IResultCallback.Stub.asInterface(data.readStrongBinder());
/*  882 */         getPublicServiceList(_arg0);
/*  883 */         reply.writeNoException();
/*  884 */         return true;
/*      */       case 62:
/*  888 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  890 */         String _arg0 = data.readString();
/*  891 */         boolean _result = validateAuth(_arg0);
/*  892 */         reply.writeNoException();
/*  893 */         reply.writeInt(_result ? 1 : 0);
/*  894 */         return true;
/*      */       case 63:
/*  898 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Message _arg0;
/*      */         Message _arg0;
/*  900 */         if (0 != data.readInt()) {
/*  901 */           _arg0 = (Message)Message.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  904 */           _arg0 = null;
/*      */         }
/*      */ 
/*  907 */         IUploadCallback _arg1 = IUploadCallback.Stub.asInterface(data.readStrongBinder());
/*  908 */         uploadMedia(_arg0, _arg1);
/*  909 */         reply.writeNoException();
/*  910 */         return true;
/*      */       case 64:
/*  914 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Conversation _arg0;
/*      */         Conversation _arg0;
/*  916 */         if (0 != data.readInt()) {
/*  917 */           _arg0 = (Conversation)Conversation.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  920 */           _arg0 = null;
/*      */         }
/*      */ 
/*  923 */         int _arg1 = data.readInt();
/*      */ 
/*  925 */         String _arg2 = data.readString();
/*      */ 
/*  927 */         IDownloadMediaCallback _arg3 = IDownloadMediaCallback.Stub.asInterface(data.readStrongBinder());
/*  928 */         downloadMedia(_arg0, _arg1, _arg2, _arg3);
/*  929 */         reply.writeNoException();
/*  930 */         return true;
/*      */       case 65:
/*  934 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Message _arg0;
/*      */         Message _arg0;
/*  936 */         if (0 != data.readInt()) {
/*  937 */           _arg0 = (Message)Message.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  940 */           _arg0 = null;
/*      */         }
/*      */ 
/*  943 */         IDownloadMediaMessageCallback _arg1 = IDownloadMediaMessageCallback.Stub.asInterface(data.readStrongBinder());
/*  944 */         downloadMediaMessage(_arg0, _arg1);
/*  945 */         reply.writeNoException();
/*  946 */         return true;
/*      */       case 66:
/*  950 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Message _arg0;
/*      */         Message _arg0;
/*  952 */         if (0 != data.readInt()) {
/*  953 */           _arg0 = (Message)Message.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/*  956 */           _arg0 = null;
/*      */         }
/*      */ 
/*  959 */         IOperationCallback _arg1 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*  960 */         cancelDownloadMediaMessage(_arg0, _arg1);
/*  961 */         reply.writeNoException();
/*  962 */         return true;
/*      */       case 67:
/*  966 */         data.enforceInterface("io.rong.imlib.IHandler");
/*  967 */         long _result = getDeltaTime();
/*  968 */         reply.writeNoException();
/*  969 */         reply.writeLong(_result);
/*  970 */         return true;
/*      */       case 68:
/*  974 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  976 */         String _arg0 = data.readString();
/*      */ 
/*  978 */         int _arg1 = data.readInt();
/*      */ 
/*  980 */         IOperationCallback _arg2 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*  981 */         setDiscussionInviteStatus(_arg0, _arg1, _arg2);
/*  982 */         reply.writeNoException();
/*  983 */         return true;
/*      */       case 69:
/*  987 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/*  989 */         String _arg0 = data.readString();
/*      */ 
/*  991 */         IOperationCallback _arg1 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/*  992 */         addToBlacklist(_arg0, _arg1);
/*  993 */         reply.writeNoException();
/*  994 */         return true;
/*      */       case 70:
/*  998 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1000 */         String _arg0 = data.readString();
/*      */ 
/* 1002 */         IOperationCallback _arg1 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/* 1003 */         removeFromBlacklist(_arg0, _arg1);
/* 1004 */         reply.writeNoException();
/* 1005 */         return true;
/*      */       case 71:
/* 1009 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Conversation _arg0;
/*      */         Conversation _arg0;
/* 1011 */         if (0 != data.readInt()) {
/* 1012 */           _arg0 = (Conversation)Conversation.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/* 1015 */           _arg0 = null;
/*      */         }
/* 1017 */         String _result = getTextMessageDraft(_arg0);
/* 1018 */         reply.writeNoException();
/* 1019 */         reply.writeString(_result);
/* 1020 */         return true;
/*      */       case 72:
/* 1024 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Conversation _arg0;
/*      */         Conversation _arg0;
/* 1026 */         if (0 != data.readInt()) {
/* 1027 */           _arg0 = (Conversation)Conversation.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/* 1030 */           _arg0 = null;
/*      */         }
/*      */ 
/* 1033 */         String _arg1 = data.readString();
/* 1034 */         boolean _result = saveTextMessageDraft(_arg0, _arg1);
/* 1035 */         reply.writeNoException();
/* 1036 */         reply.writeInt(_result ? 1 : 0);
/* 1037 */         return true;
/*      */       case 73:
/* 1041 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         Conversation _arg0;
/*      */         Conversation _arg0;
/* 1043 */         if (0 != data.readInt()) {
/* 1044 */           _arg0 = (Conversation)Conversation.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/* 1047 */           _arg0 = null;
/*      */         }
/* 1049 */         boolean _result = clearTextMessageDraft(_arg0);
/* 1050 */         reply.writeNoException();
/* 1051 */         reply.writeInt(_result ? 1 : 0);
/* 1052 */         return true;
/*      */       case 74:
/* 1056 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1058 */         IStringCallback _arg0 = IStringCallback.Stub.asInterface(data.readStrongBinder());
/* 1059 */         getBlacklist(_arg0);
/* 1060 */         reply.writeNoException();
/* 1061 */         return true;
/*      */       case 75:
/* 1065 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1067 */         String _arg0 = data.readString();
/*      */ 
/* 1069 */         IIntegerCallback _arg1 = IIntegerCallback.Stub.asInterface(data.readStrongBinder());
/* 1070 */         getBlacklistStatus(_arg0, _arg1);
/* 1071 */         reply.writeNoException();
/* 1072 */         return true;
/*      */       case 76:
/* 1076 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */         UserData _arg0;
/*      */         UserData _arg0;
/* 1078 */         if (0 != data.readInt()) {
/* 1079 */           _arg0 = (UserData)UserData.CREATOR.createFromParcel(data);
/*      */         }
/*      */         else {
/* 1082 */           _arg0 = null;
/*      */         }
/*      */ 
/* 1085 */         IOperationCallback _arg1 = IOperationCallback.Stub.asInterface(data.readStrongBinder());
/* 1086 */         setUserData(_arg0, _arg1);
/* 1087 */         reply.writeNoException();
/* 1088 */         return true;
/*      */       case 77:
/* 1092 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1094 */         int _arg0 = data.readInt();
/*      */ 
/* 1096 */         String _arg1 = data.readString();
/* 1097 */         int _result = setupRealTimeLocation(_arg0, _arg1);
/* 1098 */         reply.writeNoException();
/* 1099 */         reply.writeInt(_result);
/* 1100 */         return true;
/*      */       case 78:
/* 1104 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1106 */         int _arg0 = data.readInt();
/*      */ 
/* 1108 */         String _arg1 = data.readString();
/* 1109 */         int _result = startRealTimeLocation(_arg0, _arg1);
/* 1110 */         reply.writeNoException();
/* 1111 */         reply.writeInt(_result);
/* 1112 */         return true;
/*      */       case 79:
/* 1116 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1118 */         int _arg0 = data.readInt();
/*      */ 
/* 1120 */         String _arg1 = data.readString();
/* 1121 */         int _result = joinRealTimeLocation(_arg0, _arg1);
/* 1122 */         reply.writeNoException();
/* 1123 */         reply.writeInt(_result);
/* 1124 */         return true;
/*      */       case 80:
/* 1128 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1130 */         int _arg0 = data.readInt();
/*      */ 
/* 1132 */         String _arg1 = data.readString();
/* 1133 */         quitRealTimeLocation(_arg0, _arg1);
/* 1134 */         reply.writeNoException();
/* 1135 */         return true;
/*      */       case 81:
/* 1139 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1141 */         int _arg0 = data.readInt();
/*      */ 
/* 1143 */         String _arg1 = data.readString();
/* 1144 */         List _result = getRealTimeLocationParticipants(_arg0, _arg1);
/* 1145 */         reply.writeNoException();
/* 1146 */         reply.writeStringList(_result);
/* 1147 */         return true;
/*      */       case 82:
/* 1151 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1153 */         int _arg0 = data.readInt();
/*      */ 
/* 1155 */         String _arg1 = data.readString();
/*      */ 
/* 1157 */         IRealTimeLocationListener _arg2 = IRealTimeLocationListener.Stub.asInterface(data.readStrongBinder());
/* 1158 */         addRealTimeLocationListener(_arg0, _arg1, _arg2);
/* 1159 */         reply.writeNoException();
/* 1160 */         return true;
/*      */       case 83:
/* 1164 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1166 */         int _arg0 = data.readInt();
/*      */ 
/* 1168 */         String _arg1 = data.readString();
/* 1169 */         int _result = getRealTimeLocationCurrentState(_arg0, _arg1);
/* 1170 */         reply.writeNoException();
/* 1171 */         reply.writeInt(_result);
/* 1172 */         return true;
/*      */       case 84:
/* 1176 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1178 */         int _arg0 = data.readInt();
/*      */ 
/* 1180 */         String _arg1 = data.readString();
/*      */ 
/* 1182 */         double _arg2 = data.readDouble();
/*      */ 
/* 1184 */         double _arg3 = data.readDouble();
/* 1185 */         updateRealTimeLocationStatus(_arg0, _arg1, _arg2, _arg3);
/* 1186 */         reply.writeNoException();
/* 1187 */         return true;
/*      */       case 85:
/* 1191 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1193 */         String _arg0 = data.readString();
/*      */ 
/* 1195 */         int _arg1 = data.readInt();
/*      */ 
/* 1197 */         long _arg2 = data.readLong();
/* 1198 */         boolean _result = updateMessageReceiptStatus(_arg0, _arg1, _arg2);
/* 1199 */         reply.writeNoException();
/* 1200 */         reply.writeInt(_result ? 1 : 0);
/* 1201 */         return true;
/*      */       case 86:
/* 1205 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1207 */         int _arg0 = data.readInt();
/*      */ 
/* 1209 */         String _arg1 = data.readString();
/*      */ 
/* 1211 */         long _arg2 = data.readLong();
/* 1212 */         boolean _result = clearUnreadByReceipt(_arg0, _arg1, _arg2);
/* 1213 */         reply.writeNoException();
/* 1214 */         reply.writeInt(_result ? 1 : 0);
/* 1215 */         return true;
/*      */       case 87:
/* 1219 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1221 */         int _arg0 = data.readInt();
/* 1222 */         long _result = getSendTimeByMessageId(_arg0);
/* 1223 */         reply.writeNoException();
/* 1224 */         reply.writeLong(_result);
/* 1225 */         return true;
/*      */       case 88:
/* 1229 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1231 */         int _arg0 = data.readInt();
/*      */ 
/* 1233 */         String _arg1 = data.readString();
/*      */ 
/* 1235 */         String _arg2 = data.readString();
/*      */ 
/* 1237 */         IStringCallback _arg3 = IStringCallback.Stub.asInterface(data.readStrongBinder());
/* 1238 */         getVoIPKey(_arg0, _arg1, _arg2, _arg3);
/* 1239 */         reply.writeNoException();
/* 1240 */         return true;
/*      */       case 89:
/* 1244 */         data.enforceInterface("io.rong.imlib.IHandler");
/* 1245 */         String _result = getVoIPCallInfo();
/* 1246 */         reply.writeNoException();
/* 1247 */         reply.writeString(_result);
/* 1248 */         return true;
/*      */       case 90:
/* 1252 */         data.enforceInterface("io.rong.imlib.IHandler");
/* 1253 */         String _result = getCurrentUserId();
/* 1254 */         reply.writeNoException();
/* 1255 */         reply.writeString(_result);
/* 1256 */         return true;
/*      */       case 91:
/* 1260 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1262 */         String _arg0 = data.readString();
/*      */ 
/* 1264 */         String _arg1 = data.readString();
/* 1265 */         setServerInfo(_arg0, _arg1);
/* 1266 */         reply.writeNoException();
/* 1267 */         return true;
/*      */       case 92:
/* 1271 */         data.enforceInterface("io.rong.imlib.IHandler");
/* 1272 */         long _result = getNaviCachedTime();
/* 1273 */         reply.writeNoException();
/* 1274 */         reply.writeLong(_result);
/* 1275 */         return true;
/*      */       case 93:
/* 1279 */         data.enforceInterface("io.rong.imlib.IHandler");
/* 1280 */         String _result = getCMPServer();
/* 1281 */         reply.writeNoException();
/* 1282 */         reply.writeString(_result);
/* 1283 */         return true;
/*      */       case 94:
/* 1287 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1289 */         IStringCallback _arg0 = IStringCallback.Stub.asInterface(data.readStrongBinder());
/* 1290 */         getPCAuthConfig(_arg0);
/* 1291 */         reply.writeNoException();
/* 1292 */         return true;
/*      */       case 95:
/* 1296 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1298 */         int _arg0 = data.readInt();
/*      */ 
/* 1300 */         byte[] _arg1 = data.createByteArray();
/*      */ 
/* 1302 */         String _arg2 = data.readString();
/* 1303 */         boolean _result = setMessageContent(_arg0, _arg1, _arg2);
/* 1304 */         reply.writeNoException();
/* 1305 */         reply.writeInt(_result ? 1 : 0);
/* 1306 */         return true;
/*      */       case 96:
/* 1310 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1312 */         int _arg0 = data.readInt();
/*      */ 
/* 1314 */         String _arg1 = data.readString();
/* 1315 */         List _result = getUnreadMentionedMessages(_arg0, _arg1);
/* 1316 */         reply.writeNoException();
/* 1317 */         reply.writeTypedList(_result);
/* 1318 */         return true;
/*      */       case 97:
/* 1322 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1324 */         String _arg0 = data.readString();
/*      */ 
/* 1326 */         String _arg1 = data.readString();
/* 1327 */         boolean _result = updateReadReceiptRequestInfo(_arg0, _arg1);
/* 1328 */         reply.writeNoException();
/* 1329 */         reply.writeInt(_result ? 1 : 0);
/* 1330 */         return true;
/*      */       case 98:
/* 1334 */         data.enforceInterface("io.rong.imlib.IHandler");
/*      */ 
/* 1336 */         List _arg0 = data.createStringArrayList();
/* 1337 */         registerCmdMsgType(_arg0);
/* 1338 */         reply.writeNoException();
/* 1339 */         return true;
/*      */       }
/*      */ 
/* 1342 */       return super.onTransact(code, data, reply, flags);
/*      */     }
/*      */     private static class Proxy implements IHandler {
/*      */       private IBinder mRemote;
/*      */ 
/*      */       Proxy(IBinder remote) {
/* 1349 */         this.mRemote = remote;
/*      */       }
/*      */ 
/*      */       public IBinder asBinder() {
/* 1353 */         return this.mRemote;
/*      */       }
/*      */ 
/*      */       public String getInterfaceDescriptor() {
/* 1357 */         return "io.rong.imlib.IHandler";
/*      */       }
/*      */ 
/*      */       public void connect(String token, IStringCallback callback)
/*      */         throws RemoteException
/*      */       {
/* 1363 */         Parcel _data = Parcel.obtain();
/* 1364 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1366 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1367 */           _data.writeString(token);
/* 1368 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 1369 */           this.mRemote.transact(1, _data, _reply, 0);
/* 1370 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1373 */           _reply.recycle();
/* 1374 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void disconnect(boolean isReceivePush, IOperationCallback callback) throws RemoteException {
/* 1379 */         Parcel _data = Parcel.obtain();
/* 1380 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1382 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1383 */           _data.writeInt(isReceivePush ? 1 : 0);
/* 1384 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 1385 */           this.mRemote.transact(2, _data, _reply, 0);
/* 1386 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1389 */           _reply.recycle();
/* 1390 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void registerMessageType(String className) throws RemoteException {
/* 1395 */         Parcel _data = Parcel.obtain();
/* 1396 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1398 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1399 */           _data.writeString(className);
/* 1400 */           this.mRemote.transact(3, _data, _reply, 0);
/* 1401 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1404 */           _reply.recycle();
/* 1405 */           _data.recycle();
/*      */         }
/*      */       }
/* 1410 */       public int getTotalUnreadCount() throws RemoteException { Parcel _data = Parcel.obtain();
/* 1411 */         Parcel _reply = Parcel.obtain();
/*      */         int _result;
/*      */         try {
/* 1414 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1415 */           this.mRemote.transact(4, _data, _reply, 0);
/* 1416 */           _reply.readException();
/* 1417 */           _result = _reply.readInt();
/*      */         }
/*      */         finally {
/* 1420 */           _reply.recycle();
/* 1421 */           _data.recycle();
/*      */         }
/* 1423 */         return _result; } 
/*      */       public int getUnreadCount(int[] types) throws RemoteException {
/* 1427 */         Parcel _data = Parcel.obtain();
/* 1428 */         Parcel _reply = Parcel.obtain();
/*      */         int _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1432 */           _data.writeIntArray(types);
/* 1433 */           this.mRemote.transact(5, _data, _reply, 0);
/* 1434 */           _reply.readException();
/* 1435 */           _result = _reply.readInt();
/*      */         } finally
/*      */         {
/* 1438 */           _reply.recycle();
/* 1439 */           _data.recycle();
/*      */         }
/* 1441 */         return _result;
/*      */       }
/* 1445 */       public int getUnreadCountById(int type, String targetId) throws RemoteException { Parcel _data = Parcel.obtain();
/* 1446 */         Parcel _reply = Parcel.obtain();
/*      */         int _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1450 */           _data.writeInt(type);
/* 1451 */           _data.writeString(targetId);
/* 1452 */           this.mRemote.transact(6, _data, _reply, 0);
/* 1453 */           _reply.readException();
/* 1454 */           _result = _reply.readInt();
/*      */         } finally
/*      */         {
/* 1457 */           _reply.recycle();
/* 1458 */           _data.recycle();
/*      */         }
/* 1460 */         return _result; }
/*      */ 
/*      */       public void setOnReceiveMessageListener(OnReceiveMessageListener callback) throws RemoteException
/*      */       {
/* 1464 */         Parcel _data = Parcel.obtain();
/* 1465 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1467 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1468 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 1469 */           this.mRemote.transact(7, _data, _reply, 0);
/* 1470 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1473 */           _reply.recycle();
/* 1474 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void setConnectionStatusListener(IConnectionStatusListener callback) throws RemoteException {
/* 1479 */         Parcel _data = Parcel.obtain();
/* 1480 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1482 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1483 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 1484 */           this.mRemote.transact(8, _data, _reply, 0);
/* 1485 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1488 */           _reply.recycle();
/* 1489 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public Message getMessage(int messageId) throws RemoteException {
/* 1496 */         Parcel _data = Parcel.obtain();
/* 1497 */         Parcel _reply = Parcel.obtain();
/*      */         Message _result;
/*      */         try {
/* 1500 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1501 */           _data.writeInt(messageId);
/* 1502 */           this.mRemote.transact(9, _data, _reply, 0);
/* 1503 */           _reply.readException();
/*      */           Message _result;
/* 1504 */           if (0 != _reply.readInt()) {
/* 1505 */             _result = (Message)Message.CREATOR.createFromParcel(_reply);
/*      */           }
/*      */           else
/* 1508 */             _result = null;
/*      */         }
/*      */         finally
/*      */         {
/* 1512 */           _reply.recycle();
/* 1513 */           _data.recycle();
/*      */         }
/* 1515 */         return _result;
/*      */       }
/* 1519 */       public Message insertMessage(Message message) throws RemoteException { Parcel _data = Parcel.obtain();
/* 1520 */         Parcel _reply = Parcel.obtain();
/*      */         Message _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1524 */           if (message != null) {
/* 1525 */             _data.writeInt(1);
/* 1526 */             message.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1529 */             _data.writeInt(0);
/*      */           }
/* 1531 */           this.mRemote.transact(10, _data, _reply, 0);
/* 1532 */           _reply.readException();
/*      */           Message _result;
/* 1533 */           if (0 != _reply.readInt()) {
/* 1534 */             _result = (Message)Message.CREATOR.createFromParcel(_reply);
/*      */           }
/*      */           else
/* 1537 */             _result = null;
/*      */         }
/*      */         finally
/*      */         {
/* 1541 */           _reply.recycle();
/* 1542 */           _data.recycle();
/*      */         }
/* 1544 */         return _result; }
/*      */ 
/*      */       public void sendMessage(Message message, String pushContent, String pushData, ISendMessageCallback callback) throws RemoteException
/*      */       {
/* 1548 */         Parcel _data = Parcel.obtain();
/* 1549 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1551 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1552 */           if (message != null) {
/* 1553 */             _data.writeInt(1);
/* 1554 */             message.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1557 */             _data.writeInt(0);
/*      */           }
/* 1559 */           _data.writeString(pushContent);
/* 1560 */           _data.writeString(pushData);
/* 1561 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 1562 */           this.mRemote.transact(11, _data, _reply, 0);
/* 1563 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1566 */           _reply.recycle();
/* 1567 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void sendDirectionalMessage(Message message, String pushContent, String pushData, String[] userIds, ISendMessageCallback callback) throws RemoteException {
/* 1572 */         Parcel _data = Parcel.obtain();
/* 1573 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1575 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1576 */           if (message != null) {
/* 1577 */             _data.writeInt(1);
/* 1578 */             message.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1581 */             _data.writeInt(0);
/*      */           }
/* 1583 */           _data.writeString(pushContent);
/* 1584 */           _data.writeString(pushData);
/* 1585 */           _data.writeStringArray(userIds);
/* 1586 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 1587 */           this.mRemote.transact(12, _data, _reply, 0);
/* 1588 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1591 */           _reply.recycle();
/* 1592 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void sendMediaMessage(Message message, String pushContent, String pushData, ISendMediaMessageCallback callback) throws RemoteException {
/* 1597 */         Parcel _data = Parcel.obtain();
/* 1598 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1600 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1601 */           if (message != null) {
/* 1602 */             _data.writeInt(1);
/* 1603 */             message.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1606 */             _data.writeInt(0);
/*      */           }
/* 1608 */           _data.writeString(pushContent);
/* 1609 */           _data.writeString(pushData);
/* 1610 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 1611 */           this.mRemote.transact(13, _data, _reply, 0);
/* 1612 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1615 */           _reply.recycle();
/* 1616 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void sendLocationMessage(Message message, String pushContent, String pushData, ISendMessageCallback callback) throws RemoteException {
/* 1621 */         Parcel _data = Parcel.obtain();
/* 1622 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1624 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1625 */           if (message != null) {
/* 1626 */             _data.writeInt(1);
/* 1627 */             message.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1630 */             _data.writeInt(0);
/*      */           }
/* 1632 */           _data.writeString(pushContent);
/* 1633 */           _data.writeString(pushData);
/* 1634 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 1635 */           this.mRemote.transact(14, _data, _reply, 0);
/* 1636 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1639 */           _reply.recycle();
/* 1640 */           _data.recycle();
/*      */         }
/*      */       }
/* 1645 */       public Message sendStatusMessage(Message message, ILongCallback callback) throws RemoteException { Parcel _data = Parcel.obtain();
/* 1646 */         Parcel _reply = Parcel.obtain();
/*      */         Message _result;
/*      */         try {
/* 1649 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1650 */           if (message != null) {
/* 1651 */             _data.writeInt(1);
/* 1652 */             message.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1655 */             _data.writeInt(0);
/*      */           }
/* 1657 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 1658 */           this.mRemote.transact(15, _data, _reply, 0);
/* 1659 */           _reply.readException();
/*      */           Message _result;
/* 1660 */           if (0 != _reply.readInt()) {
/* 1661 */             _result = (Message)Message.CREATOR.createFromParcel(_reply);
/*      */           }
/*      */           else
/* 1664 */             _result = null;
/*      */         }
/*      */         finally
/*      */         {
/* 1668 */           _reply.recycle();
/* 1669 */           _data.recycle();
/*      */         }
/* 1671 */         return _result; } 
/*      */       public List<Message> getNewestMessages(Conversation conversation, int count) throws RemoteException {
/* 1675 */         Parcel _data = Parcel.obtain();
/* 1676 */         Parcel _reply = Parcel.obtain();
/*      */         List _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1680 */           if (conversation != null) {
/* 1681 */             _data.writeInt(1);
/* 1682 */             conversation.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1685 */             _data.writeInt(0);
/*      */           }
/* 1687 */           _data.writeInt(count);
/* 1688 */           this.mRemote.transact(16, _data, _reply, 0);
/* 1689 */           _reply.readException();
/* 1690 */           _result = _reply.createTypedArrayList(Message.CREATOR);
/*      */         } finally
/*      */         {
/* 1693 */           _reply.recycle();
/* 1694 */           _data.recycle();
/*      */         }
/* 1696 */         return _result;
/*      */       }
/* 1700 */       public List<Message> getOlderMessages(Conversation conversation, long flagId, int count) throws RemoteException { Parcel _data = Parcel.obtain();
/* 1701 */         Parcel _reply = Parcel.obtain();
/*      */         List _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1705 */           if (conversation != null) {
/* 1706 */             _data.writeInt(1);
/* 1707 */             conversation.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1710 */             _data.writeInt(0);
/*      */           }
/* 1712 */           _data.writeLong(flagId);
/* 1713 */           _data.writeInt(count);
/* 1714 */           this.mRemote.transact(17, _data, _reply, 0);
/* 1715 */           _reply.readException();
/* 1716 */           _result = _reply.createTypedArrayList(Message.CREATOR);
/*      */         } finally
/*      */         {
/* 1719 */           _reply.recycle();
/* 1720 */           _data.recycle();
/*      */         }
/* 1722 */         return _result; }
/*      */ 
/*      */       public void getRemoteHistoryMessages(Conversation conversation, long dataTime, int count, IResultCallback callback) throws RemoteException
/*      */       {
/* 1726 */         Parcel _data = Parcel.obtain();
/* 1727 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 1729 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1730 */           if (conversation != null) {
/* 1731 */             _data.writeInt(1);
/* 1732 */             conversation.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1735 */             _data.writeInt(0);
/*      */           }
/* 1737 */           _data.writeLong(dataTime);
/* 1738 */           _data.writeInt(count);
/* 1739 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 1740 */           this.mRemote.transact(18, _data, _reply, 0);
/* 1741 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 1744 */           _reply.recycle();
/* 1745 */           _data.recycle();
/*      */         }
/*      */       }
/* 1750 */       public List<Message> getOlderMessagesByObjectName(Conversation conversation, String objectName, long flagId, int count, boolean flag) throws RemoteException { Parcel _data = Parcel.obtain();
/* 1751 */         Parcel _reply = Parcel.obtain();
/*      */         List _result;
/*      */         try {
/* 1754 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1755 */           if (conversation != null) {
/* 1756 */             _data.writeInt(1);
/* 1757 */             conversation.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1760 */             _data.writeInt(0);
/*      */           }
/* 1762 */           _data.writeString(objectName);
/* 1763 */           _data.writeLong(flagId);
/* 1764 */           _data.writeInt(count);
/* 1765 */           _data.writeInt(flag ? 1 : 0);
/* 1766 */           this.mRemote.transact(19, _data, _reply, 0);
/* 1767 */           _reply.readException();
/* 1768 */           _result = _reply.createTypedArrayList(Message.CREATOR);
/*      */         }
/*      */         finally {
/* 1771 */           _reply.recycle();
/* 1772 */           _data.recycle();
/*      */         }
/* 1774 */         return _result; } 
/*      */       public boolean deleteMessage(int[] ids) throws RemoteException {
/* 1778 */         Parcel _data = Parcel.obtain();
/* 1779 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1783 */           _data.writeIntArray(ids);
/* 1784 */           this.mRemote.transact(20, _data, _reply, 0);
/* 1785 */           _reply.readException();
/* 1786 */           _result = 0 != _reply.readInt();
/*      */         } finally
/*      */         {
/* 1789 */           _reply.recycle();
/* 1790 */           _data.recycle();
/*      */         }
/* 1792 */         return _result;
/*      */       }
/* 1796 */       public boolean deleteConversationMessage(int conversationType, String targetId) throws RemoteException { Parcel _data = Parcel.obtain();
/* 1797 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1801 */           _data.writeInt(conversationType);
/* 1802 */           _data.writeString(targetId);
/* 1803 */           this.mRemote.transact(21, _data, _reply, 0);
/* 1804 */           _reply.readException();
/* 1805 */           _result = 0 != _reply.readInt();
/*      */         } finally
/*      */         {
/* 1808 */           _reply.recycle();
/* 1809 */           _data.recycle();
/*      */         }
/* 1811 */         return _result; } 
/*      */       public boolean clearMessages(Conversation conversation) throws RemoteException {
/* 1815 */         Parcel _data = Parcel.obtain();
/* 1816 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1820 */           if (conversation != null) {
/* 1821 */             _data.writeInt(1);
/* 1822 */             conversation.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1825 */             _data.writeInt(0);
/*      */           }
/* 1827 */           this.mRemote.transact(22, _data, _reply, 0);
/* 1828 */           _reply.readException();
/* 1829 */           _result = 0 != _reply.readInt();
/*      */         } finally
/*      */         {
/* 1832 */           _reply.recycle();
/* 1833 */           _data.recycle();
/*      */         }
/* 1835 */         return _result;
/*      */       }
/* 1839 */       public boolean clearMessagesUnreadStatus(Conversation conversation) throws RemoteException { Parcel _data = Parcel.obtain();
/* 1840 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1844 */           if (conversation != null) {
/* 1845 */             _data.writeInt(1);
/* 1846 */             conversation.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 1849 */             _data.writeInt(0);
/*      */           }
/* 1851 */           this.mRemote.transact(23, _data, _reply, 0);
/* 1852 */           _reply.readException();
/* 1853 */           _result = 0 != _reply.readInt();
/*      */         } finally
/*      */         {
/* 1856 */           _reply.recycle();
/* 1857 */           _data.recycle();
/*      */         }
/* 1859 */         return _result; } 
/*      */       public boolean setMessageExtra(int messageId, String values) throws RemoteException {
/* 1863 */         Parcel _data = Parcel.obtain();
/* 1864 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1868 */           _data.writeInt(messageId);
/* 1869 */           _data.writeString(values);
/* 1870 */           this.mRemote.transact(24, _data, _reply, 0);
/* 1871 */           _reply.readException();
/* 1872 */           _result = 0 != _reply.readInt();
/*      */         } finally
/*      */         {
/* 1875 */           _reply.recycle();
/* 1876 */           _data.recycle();
/*      */         }
/* 1878 */         return _result;
/*      */       }
/* 1882 */       public boolean setMessageReceivedStatus(int messageId, int status) throws RemoteException { Parcel _data = Parcel.obtain();
/* 1883 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1887 */           _data.writeInt(messageId);
/* 1888 */           _data.writeInt(status);
/* 1889 */           this.mRemote.transact(25, _data, _reply, 0);
/* 1890 */           _reply.readException();
/* 1891 */           _result = 0 != _reply.readInt();
/*      */         } finally
/*      */         {
/* 1894 */           _reply.recycle();
/* 1895 */           _data.recycle();
/*      */         }
/* 1897 */         return _result; } 
/*      */       public boolean setMessageSentStatus(int messageId, int status) throws RemoteException {
/* 1901 */         Parcel _data = Parcel.obtain();
/* 1902 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1906 */           _data.writeInt(messageId);
/* 1907 */           _data.writeInt(status);
/* 1908 */           this.mRemote.transact(26, _data, _reply, 0);
/* 1909 */           _reply.readException();
/* 1910 */           _result = 0 != _reply.readInt();
/*      */         } finally
/*      */         {
/* 1913 */           _reply.recycle();
/* 1914 */           _data.recycle();
/*      */         }
/* 1916 */         return _result;
/*      */       }
/* 1920 */       public Message getMessageByUid(String uid) throws RemoteException { Parcel _data = Parcel.obtain();
/* 1921 */         Parcel _reply = Parcel.obtain();
/*      */         Message _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1925 */           _data.writeString(uid);
/* 1926 */           this.mRemote.transact(27, _data, _reply, 0);
/* 1927 */           _reply.readException();
/*      */           Message _result;
/* 1928 */           if (0 != _reply.readInt()) {
/* 1929 */             _result = (Message)Message.CREATOR.createFromParcel(_reply);
/*      */           }
/*      */           else
/* 1932 */             _result = null;
/*      */         }
/*      */         finally
/*      */         {
/* 1936 */           _reply.recycle();
/* 1937 */           _data.recycle();
/*      */         }
/* 1939 */         return _result; }
/*      */ 
/*      */       public List<Conversation> getConversationList() throws RemoteException {
/* 1945 */         Parcel _data = Parcel.obtain();
/* 1946 */         Parcel _reply = Parcel.obtain();
/*      */         List _result;
/*      */         try {
/* 1949 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1950 */           this.mRemote.transact(28, _data, _reply, 0);
/* 1951 */           _reply.readException();
/* 1952 */           _result = _reply.createTypedArrayList(Conversation.CREATOR);
/*      */         }
/*      */         finally {
/* 1955 */           _reply.recycle();
/* 1956 */           _data.recycle();
/*      */         }
/* 1958 */         return _result;
/*      */       }
/* 1962 */       public List<Conversation> getConversationListByType(int[] types) throws RemoteException { Parcel _data = Parcel.obtain();
/* 1963 */         Parcel _reply = Parcel.obtain();
/*      */         List _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1967 */           _data.writeIntArray(types);
/* 1968 */           this.mRemote.transact(29, _data, _reply, 0);
/* 1969 */           _reply.readException();
/* 1970 */           _result = _reply.createTypedArrayList(Conversation.CREATOR);
/*      */         } finally
/*      */         {
/* 1973 */           _reply.recycle();
/* 1974 */           _data.recycle();
/*      */         }
/* 1976 */         return _result; } 
/*      */       public Conversation getConversation(int type, String targetId) throws RemoteException {
/* 1980 */         Parcel _data = Parcel.obtain();
/* 1981 */         Parcel _reply = Parcel.obtain();
/*      */         Conversation _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 1985 */           _data.writeInt(type);
/* 1986 */           _data.writeString(targetId);
/* 1987 */           this.mRemote.transact(30, _data, _reply, 0);
/* 1988 */           _reply.readException();
/*      */           Conversation _result;
/* 1989 */           if (0 != _reply.readInt()) {
/* 1990 */             _result = (Conversation)Conversation.CREATOR.createFromParcel(_reply);
/*      */           }
/*      */           else
/* 1993 */             _result = null;
/*      */         }
/*      */         finally
/*      */         {
/* 1997 */           _reply.recycle();
/* 1998 */           _data.recycle();
/*      */         }
/* 2000 */         return _result;
/*      */       }
/* 2004 */       public boolean removeConversation(int type, String targetId) throws RemoteException { Parcel _data = Parcel.obtain();
/* 2005 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2009 */           _data.writeInt(type);
/* 2010 */           _data.writeString(targetId);
/* 2011 */           this.mRemote.transact(31, _data, _reply, 0);
/* 2012 */           _reply.readException();
/* 2013 */           _result = 0 != _reply.readInt();
/*      */         } finally
/*      */         {
/* 2016 */           _reply.recycle();
/* 2017 */           _data.recycle();
/*      */         }
/* 2019 */         return _result; } 
/*      */       public boolean saveConversationDraft(Conversation conversation, String content) throws RemoteException {
/* 2023 */         Parcel _data = Parcel.obtain();
/* 2024 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2028 */           if (conversation != null) {
/* 2029 */             _data.writeInt(1);
/* 2030 */             conversation.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 2033 */             _data.writeInt(0);
/*      */           }
/* 2035 */           _data.writeString(content);
/* 2036 */           this.mRemote.transact(32, _data, _reply, 0);
/* 2037 */           _reply.readException();
/* 2038 */           _result = 0 != _reply.readInt();
/*      */         } finally
/*      */         {
/* 2041 */           _reply.recycle();
/* 2042 */           _data.recycle();
/*      */         }
/* 2044 */         return _result;
/*      */       }
/* 2048 */       public String getConversationDraft(Conversation conversation) throws RemoteException { Parcel _data = Parcel.obtain();
/* 2049 */         Parcel _reply = Parcel.obtain();
/*      */         String _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2053 */           if (conversation != null) {
/* 2054 */             _data.writeInt(1);
/* 2055 */             conversation.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 2058 */             _data.writeInt(0);
/*      */           }
/* 2060 */           this.mRemote.transact(33, _data, _reply, 0);
/* 2061 */           _reply.readException();
/* 2062 */           _result = _reply.readString();
/*      */         } finally
/*      */         {
/* 2065 */           _reply.recycle();
/* 2066 */           _data.recycle();
/*      */         }
/* 2068 */         return _result; } 
/*      */       public boolean cleanConversationDraft(Conversation conversation) throws RemoteException {
/* 2072 */         Parcel _data = Parcel.obtain();
/* 2073 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2077 */           if (conversation != null) {
/* 2078 */             _data.writeInt(1);
/* 2079 */             conversation.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 2082 */             _data.writeInt(0);
/*      */           }
/* 2084 */           this.mRemote.transact(34, _data, _reply, 0);
/* 2085 */           _reply.readException();
/* 2086 */           _result = 0 != _reply.readInt();
/*      */         } finally
/*      */         {
/* 2089 */           _reply.recycle();
/* 2090 */           _data.recycle();
/*      */         }
/* 2092 */         return _result;
/*      */       }
/*      */ 
/*      */       public void getConversationNotificationStatus(int type, String targetId, ILongCallback callback) throws RemoteException {
/* 2096 */         Parcel _data = Parcel.obtain();
/* 2097 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2099 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2100 */           _data.writeInt(type);
/* 2101 */           _data.writeString(targetId);
/* 2102 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2103 */           this.mRemote.transact(35, _data, _reply, 0);
/* 2104 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2107 */           _reply.recycle();
/* 2108 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void setConversationNotificationStatus(int type, String targetId, int status, ILongCallback callback) throws RemoteException {
/* 2113 */         Parcel _data = Parcel.obtain();
/* 2114 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2116 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2117 */           _data.writeInt(type);
/* 2118 */           _data.writeString(targetId);
/* 2119 */           _data.writeInt(status);
/* 2120 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2121 */           this.mRemote.transact(36, _data, _reply, 0);
/* 2122 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2125 */           _reply.recycle();
/* 2126 */           _data.recycle();
/*      */         }
/*      */       }
/* 2131 */       public boolean setConversationTopStatus(int type, String targetId, boolean isTop) throws RemoteException { Parcel _data = Parcel.obtain();
/* 2132 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try {
/* 2135 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2136 */           _data.writeInt(type);
/* 2137 */           _data.writeString(targetId);
/* 2138 */           _data.writeInt(isTop ? 1 : 0);
/* 2139 */           this.mRemote.transact(37, _data, _reply, 0);
/* 2140 */           _reply.readException();
/* 2141 */           _result = 0 != _reply.readInt();
/*      */         }
/*      */         finally {
/* 2144 */           _reply.recycle();
/* 2145 */           _data.recycle();
/*      */         }
/* 2147 */         return _result; } 
/*      */       public int getConversationUnreadCount(Conversation conversation) throws RemoteException {
/* 2151 */         Parcel _data = Parcel.obtain();
/* 2152 */         Parcel _reply = Parcel.obtain();
/*      */         int _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2156 */           if (conversation != null) {
/* 2157 */             _data.writeInt(1);
/* 2158 */             conversation.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 2161 */             _data.writeInt(0);
/*      */           }
/* 2163 */           this.mRemote.transact(38, _data, _reply, 0);
/* 2164 */           _reply.readException();
/* 2165 */           _result = _reply.readInt();
/*      */         } finally
/*      */         {
/* 2168 */           _reply.recycle();
/* 2169 */           _data.recycle();
/*      */         }
/* 2171 */         return _result;
/*      */       }
/* 2175 */       public boolean clearConversations(int[] types) throws RemoteException { Parcel _data = Parcel.obtain();
/* 2176 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2180 */           _data.writeIntArray(types);
/* 2181 */           this.mRemote.transact(39, _data, _reply, 0);
/* 2182 */           _reply.readException();
/* 2183 */           _result = 0 != _reply.readInt();
/*      */         } finally
/*      */         {
/* 2186 */           _reply.recycle();
/* 2187 */           _data.recycle();
/*      */         }
/* 2189 */         return _result; }
/*      */ 
/*      */       public void setNotificationQuietHours(String startTime, int spanMinute, IOperationCallback callback) throws RemoteException
/*      */       {
/* 2193 */         Parcel _data = Parcel.obtain();
/* 2194 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2196 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2197 */           _data.writeString(startTime);
/* 2198 */           _data.writeInt(spanMinute);
/* 2199 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2200 */           this.mRemote.transact(40, _data, _reply, 0);
/* 2201 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2204 */           _reply.recycle();
/* 2205 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void removeNotificationQuietHours(IOperationCallback callback) throws RemoteException {
/* 2210 */         Parcel _data = Parcel.obtain();
/* 2211 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2213 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2214 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2215 */           this.mRemote.transact(41, _data, _reply, 0);
/* 2216 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2219 */           _reply.recycle();
/* 2220 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void getNotificationQuietHours(IGetNotificationQuietHoursCallback callback) throws RemoteException {
/* 2225 */         Parcel _data = Parcel.obtain();
/* 2226 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2228 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2229 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2230 */           this.mRemote.transact(42, _data, _reply, 0);
/* 2231 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2234 */           _reply.recycle();
/* 2235 */           _data.recycle();
/*      */         }
/*      */       }
/* 2240 */       public boolean updateConversationInfo(int type, String targetId, String title, String portait) throws RemoteException { Parcel _data = Parcel.obtain();
/* 2241 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try {
/* 2244 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2245 */           _data.writeInt(type);
/* 2246 */           _data.writeString(targetId);
/* 2247 */           _data.writeString(title);
/* 2248 */           _data.writeString(portait);
/* 2249 */           this.mRemote.transact(43, _data, _reply, 0);
/* 2250 */           _reply.readException();
/* 2251 */           _result = 0 != _reply.readInt();
/*      */         }
/*      */         finally {
/* 2254 */           _reply.recycle();
/* 2255 */           _data.recycle();
/*      */         }
/* 2257 */         return _result;
/*      */       }
/*      */ 
/*      */       public void getDiscussion(String id, IResultCallback callback)
/*      */         throws RemoteException
/*      */       {
/* 2263 */         Parcel _data = Parcel.obtain();
/* 2264 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2266 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2267 */           _data.writeString(id);
/* 2268 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2269 */           this.mRemote.transact(44, _data, _reply, 0);
/* 2270 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2273 */           _reply.recycle();
/* 2274 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void setDiscussionName(String id, String name, IOperationCallback callback) throws RemoteException {
/* 2279 */         Parcel _data = Parcel.obtain();
/* 2280 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2282 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2283 */           _data.writeString(id);
/* 2284 */           _data.writeString(name);
/* 2285 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2286 */           this.mRemote.transact(45, _data, _reply, 0);
/* 2287 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2290 */           _reply.recycle();
/* 2291 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void createDiscussion(String name, List<String> userIds, IResultCallback callback) throws RemoteException {
/* 2296 */         Parcel _data = Parcel.obtain();
/* 2297 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2299 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2300 */           _data.writeString(name);
/* 2301 */           _data.writeStringList(userIds);
/* 2302 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2303 */           this.mRemote.transact(46, _data, _reply, 0);
/* 2304 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2307 */           _reply.recycle();
/* 2308 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void addMemberToDiscussion(String id, List<String> userIds, IOperationCallback callback) throws RemoteException {
/* 2313 */         Parcel _data = Parcel.obtain();
/* 2314 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2316 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2317 */           _data.writeString(id);
/* 2318 */           _data.writeStringList(userIds);
/* 2319 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2320 */           this.mRemote.transact(47, _data, _reply, 0);
/* 2321 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2324 */           _reply.recycle();
/* 2325 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void removeDiscussionMember(String id, String userId, IOperationCallback callback) throws RemoteException {
/* 2330 */         Parcel _data = Parcel.obtain();
/* 2331 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2333 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2334 */           _data.writeString(id);
/* 2335 */           _data.writeString(userId);
/* 2336 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2337 */           this.mRemote.transact(48, _data, _reply, 0);
/* 2338 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2341 */           _reply.recycle();
/* 2342 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void quitDiscussion(String id, IOperationCallback callback) throws RemoteException {
/* 2347 */         Parcel _data = Parcel.obtain();
/* 2348 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2350 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2351 */           _data.writeString(id);
/* 2352 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2353 */           this.mRemote.transact(49, _data, _reply, 0);
/* 2354 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2357 */           _reply.recycle();
/* 2358 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void syncGroup(List<Group> groups, IOperationCallback callback)
/*      */         throws RemoteException
/*      */       {
/* 2365 */         Parcel _data = Parcel.obtain();
/* 2366 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2368 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2369 */           _data.writeTypedList(groups);
/* 2370 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2371 */           this.mRemote.transact(50, _data, _reply, 0);
/* 2372 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2375 */           _reply.recycle();
/* 2376 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void joinGroup(String id, String name, IOperationCallback callback) throws RemoteException {
/* 2381 */         Parcel _data = Parcel.obtain();
/* 2382 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2384 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2385 */           _data.writeString(id);
/* 2386 */           _data.writeString(name);
/* 2387 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2388 */           this.mRemote.transact(51, _data, _reply, 0);
/* 2389 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2392 */           _reply.recycle();
/* 2393 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void quitGroup(String id, IOperationCallback callback) throws RemoteException {
/* 2398 */         Parcel _data = Parcel.obtain();
/* 2399 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2401 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2402 */           _data.writeString(id);
/* 2403 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2404 */           this.mRemote.transact(52, _data, _reply, 0);
/* 2405 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2408 */           _reply.recycle();
/* 2409 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void getChatRoomInfo(String id, int count, int type, IResultCallback callback)
/*      */         throws RemoteException
/*      */       {
/* 2416 */         Parcel _data = Parcel.obtain();
/* 2417 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2419 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2420 */           _data.writeString(id);
/* 2421 */           _data.writeInt(count);
/* 2422 */           _data.writeInt(type);
/* 2423 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2424 */           this.mRemote.transact(53, _data, _reply, 0);
/* 2425 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2428 */           _reply.recycle();
/* 2429 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void reJoinChatRoom(String id, int defMessageCount, IOperationCallback callback) throws RemoteException {
/* 2434 */         Parcel _data = Parcel.obtain();
/* 2435 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2437 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2438 */           _data.writeString(id);
/* 2439 */           _data.writeInt(defMessageCount);
/* 2440 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2441 */           this.mRemote.transact(54, _data, _reply, 0);
/* 2442 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2445 */           _reply.recycle();
/* 2446 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void joinChatRoom(String id, int defMessageCount, IOperationCallback callback) throws RemoteException {
/* 2451 */         Parcel _data = Parcel.obtain();
/* 2452 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2454 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2455 */           _data.writeString(id);
/* 2456 */           _data.writeInt(defMessageCount);
/* 2457 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2458 */           this.mRemote.transact(55, _data, _reply, 0);
/* 2459 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2462 */           _reply.recycle();
/* 2463 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void joinExistChatRoom(String id, int defMessageCount, IOperationCallback callback) throws RemoteException {
/* 2468 */         Parcel _data = Parcel.obtain();
/* 2469 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2471 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2472 */           _data.writeString(id);
/* 2473 */           _data.writeInt(defMessageCount);
/* 2474 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2475 */           this.mRemote.transact(56, _data, _reply, 0);
/* 2476 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2479 */           _reply.recycle();
/* 2480 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void quitChatRoom(String id, IOperationCallback callback) throws RemoteException {
/* 2485 */         Parcel _data = Parcel.obtain();
/* 2486 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2488 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2489 */           _data.writeString(id);
/* 2490 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2491 */           this.mRemote.transact(57, _data, _reply, 0);
/* 2492 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2495 */           _reply.recycle();
/* 2496 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void searchPublicService(String keyWords, int businessType, int searchType, IResultCallback callback)
/*      */         throws RemoteException
/*      */       {
/* 2503 */         Parcel _data = Parcel.obtain();
/* 2504 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2506 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2507 */           _data.writeString(keyWords);
/* 2508 */           _data.writeInt(businessType);
/* 2509 */           _data.writeInt(searchType);
/* 2510 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2511 */           this.mRemote.transact(58, _data, _reply, 0);
/* 2512 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2515 */           _reply.recycle();
/* 2516 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void subscribePublicService(String targetId, int publicServiceType, boolean subscribe, IOperationCallback callback) throws RemoteException {
/* 2521 */         Parcel _data = Parcel.obtain();
/* 2522 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2524 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2525 */           _data.writeString(targetId);
/* 2526 */           _data.writeInt(publicServiceType);
/* 2527 */           _data.writeInt(subscribe ? 1 : 0);
/* 2528 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2529 */           this.mRemote.transact(59, _data, _reply, 0);
/* 2530 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2533 */           _reply.recycle();
/* 2534 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void getPublicServiceProfile(String targetId, int publicServiceType, IResultCallback callback) throws RemoteException {
/* 2539 */         Parcel _data = Parcel.obtain();
/* 2540 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2542 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2543 */           _data.writeString(targetId);
/* 2544 */           _data.writeInt(publicServiceType);
/* 2545 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2546 */           this.mRemote.transact(60, _data, _reply, 0);
/* 2547 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2550 */           _reply.recycle();
/* 2551 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void getPublicServiceList(IResultCallback callback) throws RemoteException {
/* 2556 */         Parcel _data = Parcel.obtain();
/* 2557 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2559 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2560 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2561 */           this.mRemote.transact(61, _data, _reply, 0);
/* 2562 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2565 */           _reply.recycle();
/* 2566 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public boolean validateAuth(String auth) throws RemoteException {
/* 2573 */         Parcel _data = Parcel.obtain();
/* 2574 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try {
/* 2577 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2578 */           _data.writeString(auth);
/* 2579 */           this.mRemote.transact(62, _data, _reply, 0);
/* 2580 */           _reply.readException();
/* 2581 */           _result = 0 != _reply.readInt();
/*      */         }
/*      */         finally {
/* 2584 */           _reply.recycle();
/* 2585 */           _data.recycle();
/*      */         }
/* 2587 */         return _result;
/*      */       }
/*      */ 
/*      */       public void uploadMedia(Message message, IUploadCallback callback)
/*      */         throws RemoteException
/*      */       {
/* 2593 */         Parcel _data = Parcel.obtain();
/* 2594 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2596 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2597 */           if (message != null) {
/* 2598 */             _data.writeInt(1);
/* 2599 */             message.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 2602 */             _data.writeInt(0);
/*      */           }
/* 2604 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2605 */           this.mRemote.transact(63, _data, _reply, 0);
/* 2606 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2609 */           _reply.recycle();
/* 2610 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void downloadMedia(Conversation conversation, int mediaType, String imageUrl, IDownloadMediaCallback callback) throws RemoteException {
/* 2615 */         Parcel _data = Parcel.obtain();
/* 2616 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2618 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2619 */           if (conversation != null) {
/* 2620 */             _data.writeInt(1);
/* 2621 */             conversation.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 2624 */             _data.writeInt(0);
/*      */           }
/* 2626 */           _data.writeInt(mediaType);
/* 2627 */           _data.writeString(imageUrl);
/* 2628 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2629 */           this.mRemote.transact(64, _data, _reply, 0);
/* 2630 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2633 */           _reply.recycle();
/* 2634 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void downloadMediaMessage(Message message, IDownloadMediaMessageCallback callback) throws RemoteException {
/* 2639 */         Parcel _data = Parcel.obtain();
/* 2640 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2642 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2643 */           if (message != null) {
/* 2644 */             _data.writeInt(1);
/* 2645 */             message.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 2648 */             _data.writeInt(0);
/*      */           }
/* 2650 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2651 */           this.mRemote.transact(65, _data, _reply, 0);
/* 2652 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2655 */           _reply.recycle();
/* 2656 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void cancelDownloadMediaMessage(Message message, IOperationCallback callback) throws RemoteException {
/* 2661 */         Parcel _data = Parcel.obtain();
/* 2662 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2664 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2665 */           if (message != null) {
/* 2666 */             _data.writeInt(1);
/* 2667 */             message.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 2670 */             _data.writeInt(0);
/*      */           }
/* 2672 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2673 */           this.mRemote.transact(66, _data, _reply, 0);
/* 2674 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2677 */           _reply.recycle();
/* 2678 */           _data.recycle();
/*      */         }
/*      */       }
/* 2683 */       public long getDeltaTime() throws RemoteException { Parcel _data = Parcel.obtain();
/* 2684 */         Parcel _reply = Parcel.obtain();
/*      */         long _result;
/*      */         try {
/* 2687 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2688 */           this.mRemote.transact(67, _data, _reply, 0);
/* 2689 */           _reply.readException();
/* 2690 */           _result = _reply.readLong();
/*      */         }
/*      */         finally {
/* 2693 */           _reply.recycle();
/* 2694 */           _data.recycle();
/*      */         }
/* 2696 */         return _result; }
/*      */ 
/*      */       public void setDiscussionInviteStatus(String targetId, int status, IOperationCallback callback) throws RemoteException
/*      */       {
/* 2700 */         Parcel _data = Parcel.obtain();
/* 2701 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2703 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2704 */           _data.writeString(targetId);
/* 2705 */           _data.writeInt(status);
/* 2706 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2707 */           this.mRemote.transact(68, _data, _reply, 0);
/* 2708 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2711 */           _reply.recycle();
/* 2712 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void addToBlacklist(String userId, IOperationCallback callback) throws RemoteException {
/* 2717 */         Parcel _data = Parcel.obtain();
/* 2718 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2720 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2721 */           _data.writeString(userId);
/* 2722 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2723 */           this.mRemote.transact(69, _data, _reply, 0);
/* 2724 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2727 */           _reply.recycle();
/* 2728 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void removeFromBlacklist(String userId, IOperationCallback callback) throws RemoteException {
/* 2733 */         Parcel _data = Parcel.obtain();
/* 2734 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2736 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2737 */           _data.writeString(userId);
/* 2738 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2739 */           this.mRemote.transact(70, _data, _reply, 0);
/* 2740 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2743 */           _reply.recycle();
/* 2744 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public String getTextMessageDraft(Conversation conversation) throws RemoteException {
/* 2751 */         Parcel _data = Parcel.obtain();
/* 2752 */         Parcel _reply = Parcel.obtain();
/*      */         String _result;
/*      */         try {
/* 2755 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2756 */           if (conversation != null) {
/* 2757 */             _data.writeInt(1);
/* 2758 */             conversation.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 2761 */             _data.writeInt(0);
/*      */           }
/* 2763 */           this.mRemote.transact(71, _data, _reply, 0);
/* 2764 */           _reply.readException();
/* 2765 */           _result = _reply.readString();
/*      */         }
/*      */         finally {
/* 2768 */           _reply.recycle();
/* 2769 */           _data.recycle();
/*      */         }
/* 2771 */         return _result;
/*      */       }
/* 2775 */       public boolean saveTextMessageDraft(Conversation conversation, String content) throws RemoteException { Parcel _data = Parcel.obtain();
/* 2776 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2780 */           if (conversation != null) {
/* 2781 */             _data.writeInt(1);
/* 2782 */             conversation.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 2785 */             _data.writeInt(0);
/*      */           }
/* 2787 */           _data.writeString(content);
/* 2788 */           this.mRemote.transact(72, _data, _reply, 0);
/* 2789 */           _reply.readException();
/* 2790 */           _result = 0 != _reply.readInt();
/*      */         } finally
/*      */         {
/* 2793 */           _reply.recycle();
/* 2794 */           _data.recycle();
/*      */         }
/* 2796 */         return _result; } 
/*      */       public boolean clearTextMessageDraft(Conversation conversation) throws RemoteException {
/* 2800 */         Parcel _data = Parcel.obtain();
/* 2801 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2805 */           if (conversation != null) {
/* 2806 */             _data.writeInt(1);
/* 2807 */             conversation.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 2810 */             _data.writeInt(0);
/*      */           }
/* 2812 */           this.mRemote.transact(73, _data, _reply, 0);
/* 2813 */           _reply.readException();
/* 2814 */           _result = 0 != _reply.readInt();
/*      */         } finally
/*      */         {
/* 2817 */           _reply.recycle();
/* 2818 */           _data.recycle();
/*      */         }
/* 2820 */         return _result;
/*      */       }
/*      */ 
/*      */       public void getBlacklist(IStringCallback callback) throws RemoteException {
/* 2824 */         Parcel _data = Parcel.obtain();
/* 2825 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2827 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2828 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2829 */           this.mRemote.transact(74, _data, _reply, 0);
/* 2830 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2833 */           _reply.recycle();
/* 2834 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void getBlacklistStatus(String userId, IIntegerCallback callback) throws RemoteException {
/* 2839 */         Parcel _data = Parcel.obtain();
/* 2840 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2842 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2843 */           _data.writeString(userId);
/* 2844 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2845 */           this.mRemote.transact(75, _data, _reply, 0);
/* 2846 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2849 */           _reply.recycle();
/* 2850 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void setUserData(UserData userData, IOperationCallback callback) throws RemoteException {
/* 2855 */         Parcel _data = Parcel.obtain();
/* 2856 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2858 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2859 */           if (userData != null) {
/* 2860 */             _data.writeInt(1);
/* 2861 */             userData.writeToParcel(_data, 0);
/*      */           }
/*      */           else {
/* 2864 */             _data.writeInt(0);
/*      */           }
/* 2866 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 2867 */           this.mRemote.transact(76, _data, _reply, 0);
/* 2868 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2871 */           _reply.recycle();
/* 2872 */           _data.recycle();
/*      */         }
/*      */       }
/*      */ 
/*      */       public int setupRealTimeLocation(int type, String targetId) throws RemoteException {
/* 2879 */         Parcel _data = Parcel.obtain();
/* 2880 */         Parcel _reply = Parcel.obtain();
/*      */         int _result;
/*      */         try {
/* 2883 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2884 */           _data.writeInt(type);
/* 2885 */           _data.writeString(targetId);
/* 2886 */           this.mRemote.transact(77, _data, _reply, 0);
/* 2887 */           _reply.readException();
/* 2888 */           _result = _reply.readInt();
/*      */         }
/*      */         finally {
/* 2891 */           _reply.recycle();
/* 2892 */           _data.recycle();
/*      */         }
/* 2894 */         return _result;
/*      */       }
/* 2898 */       public int startRealTimeLocation(int type, String targetId) throws RemoteException { Parcel _data = Parcel.obtain();
/* 2899 */         Parcel _reply = Parcel.obtain();
/*      */         int _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2903 */           _data.writeInt(type);
/* 2904 */           _data.writeString(targetId);
/* 2905 */           this.mRemote.transact(78, _data, _reply, 0);
/* 2906 */           _reply.readException();
/* 2907 */           _result = _reply.readInt();
/*      */         } finally
/*      */         {
/* 2910 */           _reply.recycle();
/* 2911 */           _data.recycle();
/*      */         }
/* 2913 */         return _result; } 
/*      */       public int joinRealTimeLocation(int type, String targetId) throws RemoteException {
/* 2917 */         Parcel _data = Parcel.obtain();
/* 2918 */         Parcel _reply = Parcel.obtain();
/*      */         int _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2922 */           _data.writeInt(type);
/* 2923 */           _data.writeString(targetId);
/* 2924 */           this.mRemote.transact(79, _data, _reply, 0);
/* 2925 */           _reply.readException();
/* 2926 */           _result = _reply.readInt();
/*      */         } finally
/*      */         {
/* 2929 */           _reply.recycle();
/* 2930 */           _data.recycle();
/*      */         }
/* 2932 */         return _result;
/*      */       }
/*      */ 
/*      */       public void quitRealTimeLocation(int type, String targetId) throws RemoteException {
/* 2936 */         Parcel _data = Parcel.obtain();
/* 2937 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2939 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2940 */           _data.writeInt(type);
/* 2941 */           _data.writeString(targetId);
/* 2942 */           this.mRemote.transact(80, _data, _reply, 0);
/* 2943 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2946 */           _reply.recycle();
/* 2947 */           _data.recycle();
/*      */         }
/*      */       }
/* 2952 */       public List<String> getRealTimeLocationParticipants(int type, String targetId) throws RemoteException { Parcel _data = Parcel.obtain();
/* 2953 */         Parcel _reply = Parcel.obtain();
/*      */         List _result;
/*      */         try {
/* 2956 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2957 */           _data.writeInt(type);
/* 2958 */           _data.writeString(targetId);
/* 2959 */           this.mRemote.transact(81, _data, _reply, 0);
/* 2960 */           _reply.readException();
/* 2961 */           _result = _reply.createStringArrayList();
/*      */         }
/*      */         finally {
/* 2964 */           _reply.recycle();
/* 2965 */           _data.recycle();
/*      */         }
/* 2967 */         return _result; }
/*      */ 
/*      */       public void addRealTimeLocationListener(int type, String targetId, IRealTimeLocationListener listener) throws RemoteException
/*      */       {
/* 2971 */         Parcel _data = Parcel.obtain();
/* 2972 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 2974 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2975 */           _data.writeInt(type);
/* 2976 */           _data.writeString(targetId);
/* 2977 */           _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
/* 2978 */           this.mRemote.transact(82, _data, _reply, 0);
/* 2979 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 2982 */           _reply.recycle();
/* 2983 */           _data.recycle();
/*      */         }
/*      */       }
/* 2988 */       public int getRealTimeLocationCurrentState(int type, String targetId) throws RemoteException { Parcel _data = Parcel.obtain();
/* 2989 */         Parcel _reply = Parcel.obtain();
/*      */         int _result;
/*      */         try {
/* 2992 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 2993 */           _data.writeInt(type);
/* 2994 */           _data.writeString(targetId);
/* 2995 */           this.mRemote.transact(83, _data, _reply, 0);
/* 2996 */           _reply.readException();
/* 2997 */           _result = _reply.readInt();
/*      */         }
/*      */         finally {
/* 3000 */           _reply.recycle();
/* 3001 */           _data.recycle();
/*      */         }
/* 3003 */         return _result; }
/*      */ 
/*      */       public void updateRealTimeLocationStatus(int type, String targetId, double latitude, double longitude) throws RemoteException
/*      */       {
/* 3007 */         Parcel _data = Parcel.obtain();
/* 3008 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 3010 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 3011 */           _data.writeInt(type);
/* 3012 */           _data.writeString(targetId);
/* 3013 */           _data.writeDouble(latitude);
/* 3014 */           _data.writeDouble(longitude);
/* 3015 */           this.mRemote.transact(84, _data, _reply, 0);
/* 3016 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 3019 */           _reply.recycle();
/* 3020 */           _data.recycle();
/*      */         }
/*      */       }
/* 3025 */       public boolean updateMessageReceiptStatus(String targetId, int categoryId, long timestamp) throws RemoteException { Parcel _data = Parcel.obtain();
/* 3026 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try {
/* 3029 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 3030 */           _data.writeString(targetId);
/* 3031 */           _data.writeInt(categoryId);
/* 3032 */           _data.writeLong(timestamp);
/* 3033 */           this.mRemote.transact(85, _data, _reply, 0);
/* 3034 */           _reply.readException();
/* 3035 */           _result = 0 != _reply.readInt();
/*      */         }
/*      */         finally {
/* 3038 */           _reply.recycle();
/* 3039 */           _data.recycle();
/*      */         }
/* 3041 */         return _result; } 
/*      */       public boolean clearUnreadByReceipt(int conversationType, String targetId, long timestamp) throws RemoteException {
/* 3045 */         Parcel _data = Parcel.obtain();
/* 3046 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 3050 */           _data.writeInt(conversationType);
/* 3051 */           _data.writeString(targetId);
/* 3052 */           _data.writeLong(timestamp);
/* 3053 */           this.mRemote.transact(86, _data, _reply, 0);
/* 3054 */           _reply.readException();
/* 3055 */           _result = 0 != _reply.readInt();
/*      */         } finally
/*      */         {
/* 3058 */           _reply.recycle();
/* 3059 */           _data.recycle();
/*      */         }
/* 3061 */         return _result;
/*      */       }
/* 3065 */       public long getSendTimeByMessageId(int messageId) throws RemoteException { Parcel _data = Parcel.obtain();
/* 3066 */         Parcel _reply = Parcel.obtain();
/*      */         long _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 3070 */           _data.writeInt(messageId);
/* 3071 */           this.mRemote.transact(87, _data, _reply, 0);
/* 3072 */           _reply.readException();
/* 3073 */           _result = _reply.readLong();
/*      */         } finally
/*      */         {
/* 3076 */           _reply.recycle();
/* 3077 */           _data.recycle();
/*      */         }
/* 3079 */         return _result; }
/*      */ 
/*      */       public void getVoIPKey(int engineType, String channelName, String extra, IStringCallback callback) throws RemoteException
/*      */       {
/* 3083 */         Parcel _data = Parcel.obtain();
/* 3084 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 3086 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 3087 */           _data.writeInt(engineType);
/* 3088 */           _data.writeString(channelName);
/* 3089 */           _data.writeString(extra);
/* 3090 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 3091 */           this.mRemote.transact(88, _data, _reply, 0);
/* 3092 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 3095 */           _reply.recycle();
/* 3096 */           _data.recycle();
/*      */         }
/*      */       }
/* 3101 */       public String getVoIPCallInfo() throws RemoteException { Parcel _data = Parcel.obtain();
/* 3102 */         Parcel _reply = Parcel.obtain();
/*      */         String _result;
/*      */         try {
/* 3105 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 3106 */           this.mRemote.transact(89, _data, _reply, 0);
/* 3107 */           _reply.readException();
/* 3108 */           _result = _reply.readString();
/*      */         }
/*      */         finally {
/* 3111 */           _reply.recycle();
/* 3112 */           _data.recycle();
/*      */         }
/* 3114 */         return _result; } 
/*      */       public String getCurrentUserId() throws RemoteException {
/* 3118 */         Parcel _data = Parcel.obtain();
/* 3119 */         Parcel _reply = Parcel.obtain();
/*      */         String _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 3123 */           this.mRemote.transact(90, _data, _reply, 0);
/* 3124 */           _reply.readException();
/* 3125 */           _result = _reply.readString();
/*      */         } finally
/*      */         {
/* 3128 */           _reply.recycle();
/* 3129 */           _data.recycle();
/*      */         }
/* 3131 */         return _result;
/*      */       }
/*      */ 
/*      */       public void setServerInfo(String naviServer, String fileServer) throws RemoteException {
/* 3135 */         Parcel _data = Parcel.obtain();
/* 3136 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 3138 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 3139 */           _data.writeString(naviServer);
/* 3140 */           _data.writeString(fileServer);
/* 3141 */           this.mRemote.transact(91, _data, _reply, 0);
/* 3142 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 3145 */           _reply.recycle();
/* 3146 */           _data.recycle();
/*      */         }
/*      */       }
/* 3151 */       public long getNaviCachedTime() throws RemoteException { Parcel _data = Parcel.obtain();
/* 3152 */         Parcel _reply = Parcel.obtain();
/*      */         long _result;
/*      */         try {
/* 3155 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 3156 */           this.mRemote.transact(92, _data, _reply, 0);
/* 3157 */           _reply.readException();
/* 3158 */           _result = _reply.readLong();
/*      */         }
/*      */         finally {
/* 3161 */           _reply.recycle();
/* 3162 */           _data.recycle();
/*      */         }
/* 3164 */         return _result; } 
/*      */       public String getCMPServer() throws RemoteException {
/* 3168 */         Parcel _data = Parcel.obtain();
/* 3169 */         Parcel _reply = Parcel.obtain();
/*      */         String _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 3173 */           this.mRemote.transact(93, _data, _reply, 0);
/* 3174 */           _reply.readException();
/* 3175 */           _result = _reply.readString();
/*      */         } finally
/*      */         {
/* 3178 */           _reply.recycle();
/* 3179 */           _data.recycle();
/*      */         }
/* 3181 */         return _result;
/*      */       }
/*      */ 
/*      */       public void getPCAuthConfig(IStringCallback callback) throws RemoteException {
/* 3185 */         Parcel _data = Parcel.obtain();
/* 3186 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 3188 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 3189 */           _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
/* 3190 */           this.mRemote.transact(94, _data, _reply, 0);
/* 3191 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 3194 */           _reply.recycle();
/* 3195 */           _data.recycle();
/*      */         }
/*      */       }
/* 3200 */       public boolean setMessageContent(int messageId, byte[] messageContent, String objectName) throws RemoteException { Parcel _data = Parcel.obtain();
/* 3201 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try {
/* 3204 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 3205 */           _data.writeInt(messageId);
/* 3206 */           _data.writeByteArray(messageContent);
/* 3207 */           _data.writeString(objectName);
/* 3208 */           this.mRemote.transact(95, _data, _reply, 0);
/* 3209 */           _reply.readException();
/* 3210 */           _result = 0 != _reply.readInt();
/*      */         }
/*      */         finally {
/* 3213 */           _reply.recycle();
/* 3214 */           _data.recycle();
/*      */         }
/* 3216 */         return _result; } 
/*      */       public List<Message> getUnreadMentionedMessages(int conversationType, String targetId) throws RemoteException {
/* 3220 */         Parcel _data = Parcel.obtain();
/* 3221 */         Parcel _reply = Parcel.obtain();
/*      */         List _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 3225 */           _data.writeInt(conversationType);
/* 3226 */           _data.writeString(targetId);
/* 3227 */           this.mRemote.transact(96, _data, _reply, 0);
/* 3228 */           _reply.readException();
/* 3229 */           _result = _reply.createTypedArrayList(Message.CREATOR);
/*      */         } finally
/*      */         {
/* 3232 */           _reply.recycle();
/* 3233 */           _data.recycle();
/*      */         }
/* 3235 */         return _result;
/*      */       }
/* 3239 */       public boolean updateReadReceiptRequestInfo(String msgUId, String info) throws RemoteException { Parcel _data = Parcel.obtain();
/* 3240 */         Parcel _reply = Parcel.obtain();
/*      */         boolean _result;
/*      */         try { _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 3244 */           _data.writeString(msgUId);
/* 3245 */           _data.writeString(info);
/* 3246 */           this.mRemote.transact(97, _data, _reply, 0);
/* 3247 */           _reply.readException();
/* 3248 */           _result = 0 != _reply.readInt();
/*      */         } finally
/*      */         {
/* 3251 */           _reply.recycle();
/* 3252 */           _data.recycle();
/*      */         }
/* 3254 */         return _result; }
/*      */ 
/*      */       public void registerCmdMsgType(List<String> msgTypeList) throws RemoteException
/*      */       {
/* 3258 */         Parcel _data = Parcel.obtain();
/* 3259 */         Parcel _reply = Parcel.obtain();
/*      */         try {
/* 3261 */           _data.writeInterfaceToken("io.rong.imlib.IHandler");
/* 3262 */           _data.writeStringList(msgTypeList);
/* 3263 */           this.mRemote.transact(98, _data, _reply, 0);
/* 3264 */           _reply.readException();
/*      */         }
/*      */         finally {
/* 3267 */           _reply.recycle();
/* 3268 */           _data.recycle();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.IHandler
 * JD-Core Version:    0.6.0
 */
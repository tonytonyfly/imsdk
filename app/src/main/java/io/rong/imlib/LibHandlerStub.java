/*      */ package io.rong.imlib;
/*      */ 
/*      */ import android.content.Context;
/*      */ import android.os.Handler;
/*      */ import android.os.HandlerThread;
/*      */ import android.os.RemoteException;
/*      */ import io.rong.common.RLog;
/*      */ import io.rong.common.WakeLockUtils;
/*      */ import io.rong.imlib.filetransfer.CancelCallback;
/*      */ import io.rong.imlib.filetransfer.FileTransferClient;
/*      */ import io.rong.imlib.location.RealTimeLocationConstant.RealTimeLocationErrorCode;
/*      */ import io.rong.imlib.location.RealTimeLocationConstant.RealTimeLocationStatus;
/*      */ import io.rong.imlib.model.ChatRoomInfo;
/*      */ import io.rong.imlib.model.Conversation;
/*      */ import io.rong.imlib.model.Conversation.ConversationNotificationStatus;
/*      */ import io.rong.imlib.model.Conversation.ConversationType;
/*      */ import io.rong.imlib.model.Discussion;
/*      */ import io.rong.imlib.model.Group;
/*      */ import io.rong.imlib.model.Message;
/*      */ import io.rong.imlib.model.Message.ReceivedStatus;
/*      */ import io.rong.imlib.model.Message.SentStatus;
/*      */ import io.rong.imlib.model.PublicServiceProfile;
/*      */ import io.rong.imlib.model.PublicServiceProfileList;
/*      */ import io.rong.imlib.model.RemoteModelWrap;
/*      */ import io.rong.imlib.model.RongListWrap;
/*      */ import io.rong.imlib.model.UserData;
/*      */ import io.rong.imlib.navigation.NavigationClient;
/*      */ import java.util.List;
/*      */ 
/*      */ public class LibHandlerStub extends IHandler.Stub
/*      */ {
/*      */   private static final String TAG = "LibHandlerStub";
/*      */   HandlerThread mCallbackThread;
/*      */   Handler mCallbackHandler;
/*      */   Context mContext;
/*      */   String mCurrentUserId;
/*      */   NativeClient mClient;
/*      */ 
/*      */   public LibHandlerStub(Context context, String appKey, String deviceId)
/*      */   {
/*   39 */     this.mContext = context;
/*      */ 
/*   41 */     this.mCallbackThread = new HandlerThread("Rong_SDK_Callback");
/*   42 */     this.mCallbackThread.start();
/*   43 */     this.mCallbackHandler = new Handler(this.mCallbackThread.getLooper());
/*   44 */     this.mClient = NativeClient.getInstance();
/*   45 */     this.mClient.init(this.mContext, appKey, deviceId);
/*      */   }
/*      */ 
/*      */   public String getCurrentUserId()
/*      */   {
/*   50 */     return this.mClient.getCurrentUserId();
/*      */   }
/*      */ 
/*      */   public void connect(String token, IStringCallback callback) throws RemoteException
/*      */   {
/*      */     try {
/*   56 */       RLog.i("LibHandlerStub", "connect");
/*   57 */       this.mClient.connect(token, new NativeClient.IResultCallback(callback)
/*      */       {
/*      */         public void onSuccess(String userId)
/*      */         {
/*   61 */           if (this.val$callback == null) {
/*   62 */             return;
/*      */           }
/*   64 */           LibHandlerStub.this.mCurrentUserId = userId;
/*   65 */           WakeLockUtils.startNextHeartbeat(LibHandlerStub.this.mContext);
/*      */ 
/*   67 */           LibHandlerStub.this.mCallbackHandler.post(new Runnable(userId)
/*      */           {
/*      */             public void run() {
/*      */               try {
/*   71 */                 LibHandlerStub.1.this.val$callback.onComplete(this.val$userId);
/*      */               } catch (java.lang.NullPointerException e) {
/*   73 */                 e.printStackTrace();
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */ 
/*      */         public void onError(int code)
/*      */         {
/*   82 */           WakeLockUtils.cancelHeartbeat(LibHandlerStub.this.mContext);
/*   83 */           if (this.val$callback == null)
/*   84 */             return;
/*   85 */           LibHandlerStub.this.mCallbackHandler.post(new Runnable(code)
/*      */           {
/*      */             public void run()
/*      */             {
/*      */               try {
/*   90 */                 LibHandlerStub.1.this.val$callback.onFailure(this.val$code);
/*      */               } catch (RemoteException e) {
/*   92 */                 e.printStackTrace();
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  103 */       e.printStackTrace();
/*  104 */       if (callback == null)
/*  105 */         return;
/*  106 */       this.mCallbackHandler.post(new Runnable(callback)
/*      */       {
/*      */         public void run()
/*      */         {
/*      */           try {
/*  111 */             this.val$callback.onFailure(-1);
/*      */           } catch (RemoteException e) {
/*  113 */             e.printStackTrace();
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */ 
/*      */   public void disconnect(boolean isReceivePush, IOperationCallback callback) throws RemoteException
/*      */   {
/*  123 */     if (this.mClient == null) {
/*  124 */       return;
/*      */     }
/*  126 */     WakeLockUtils.cancelHeartbeat(this.mContext);
/*      */ 
/*  128 */     this.mClient.disconnect(isReceivePush);
/*      */ 
/*  130 */     if (callback != null)
/*      */       try {
/*  132 */         callback.onComplete();
/*      */       } catch (RemoteException e) {
/*  134 */         e.printStackTrace();
/*      */       }
/*      */   }
/*      */ 
/*      */   public void registerMessageType(String className)
/*      */   {
/*  141 */     Class loader = null;
/*      */     try {
/*  143 */       loader = Class.forName(className);
/*      */     } catch (ClassNotFoundException e) {
/*  145 */       RLog.e("LibHandlerStub", "registerMessageType ClassNotFoundException", e);
/*  146 */       e.printStackTrace();
/*      */     }
/*      */     try {
/*  149 */       NativeClient.registerMessageType(loader);
/*      */     } catch (AnnotationNotFoundException e) {
/*  151 */       RLog.e("LibHandlerStub", "registerMessageType AnnotationNotFoundException", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setConnectionStatusListener(IConnectionStatusListener callback)
/*      */   {
/*  157 */     NativeClient.setConnectionStatusListener(new NativeClient.ICodeListener(callback)
/*      */     {
/*      */       public void onChanged(int status) {
/*      */         try {
/*  161 */           RLog.d("LibHandlerStub", "setConnectionStatusListener : onChanged status:" + status);
/*      */ 
/*  163 */           if ((status != 33005) && (status != 0)) {
/*  164 */             WakeLockUtils.cancelHeartbeat(LibHandlerStub.this.mContext);
/*      */           }
/*  166 */           if (this.val$callback != null)
/*  167 */             this.val$callback.onChanged(status);
/*      */         } catch (RemoteException e) {
/*  169 */           e.printStackTrace();
/*  170 */           RLog.e("LibHandlerStub", "setConnectionStatusListener : onChanged RemoteException", e);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public int getTotalUnreadCount() throws RemoteException {
/*  178 */     return this.mClient.getTotalUnreadCount();
/*      */   }
/*      */ 
/*      */   public int getUnreadCount(int[] types)
/*      */   {
/*  183 */     if ((types == null) || (types.length == 0)) {
/*  184 */       return 0;
/*      */     }
/*  186 */     Conversation.ConversationType[] conversationTypes = new Conversation.ConversationType[types.length];
/*      */ 
/*  188 */     int i = 0;
/*  189 */     while (i < types.length) {
/*  190 */       conversationTypes[i] = Conversation.ConversationType.setValue(types[i]);
/*  191 */       i++;
/*      */     }
/*  193 */     return this.mClient.getUnreadCount(conversationTypes);
/*      */   }
/*      */ 
/*      */   public int getUnreadCountById(int type, String id) throws RemoteException
/*      */   {
/*  198 */     Conversation.ConversationType conversationType = Conversation.ConversationType.setValue(type);
/*  199 */     if ((conversationType == null) || (id == null))
/*  200 */       return 0;
/*  201 */     return this.mClient.getUnreadCount(conversationType, id);
/*      */   }
/*      */ 
/*      */   public void setOnReceiveMessageListener(OnReceiveMessageListener listener) {
/*  205 */     if (listener != null) {
/*  206 */       NativeClient.OnReceiveMessageListener receiveMessageListener = new NativeClient.OnReceiveMessageListener(listener)
/*      */       {
/*      */         public void onReceived(Message message, int left, boolean offline, boolean hasMsg, int cmdLeft) {
/*  209 */           RLog.d("LibHandlerStub", "setOnReceiveMessageListener onReceived : " + message.getObjectName());
/*      */           try
/*      */           {
/*  212 */             this.val$listener.onReceived(message, left, offline, cmdLeft);
/*      */           } catch (RemoteException e) {
/*  214 */             e.printStackTrace();
/*      */           }
/*      */         }
/*      */       };
/*  219 */       this.mClient.setOnReceiveMessageListener(receiveMessageListener);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Message insertMessage(Message message) throws RemoteException
/*      */   {
/*  225 */     return this.mClient.insertMessage(message.getConversationType(), message.getTargetId(), message.getSenderUserId(), message.getContent());
/*      */   }
/*      */ 
/*      */   public Message getMessage(int messageId)
/*      */   {
/*  230 */     return this.mClient.getMessage(messageId);
/*      */   }
/*      */ 
/*      */   public Message getMessageByUid(String uid)
/*      */   {
/*  235 */     return this.mClient.getMessageByUid(uid);
/*      */   }
/*      */ 
/*      */   public void sendMessage(Message message, String pushContent, String pushData, ISendMessageCallback callback) throws RemoteException
/*      */   {
/*  240 */     this.mClient.sendMessage(message, pushContent, pushData, null, new NativeClient.ISendMessageCallback(callback)
/*      */     {
/*      */       public void onAttached(Message message)
/*      */       {
/*  244 */         if (this.val$callback != null)
/*  245 */           LibHandlerStub.this.mCallbackHandler.post(new Runnable(message)
/*      */           {
/*      */             public void run() {
/*      */               try {
/*  249 */                 LibHandlerStub.5.this.val$callback.onAttached(this.val$message);
/*      */               } catch (RemoteException e) {
/*  251 */                 e.printStackTrace();
/*      */               }
/*      */             }
/*      */           });
/*      */       }
/*      */ 
/*      */       public void onSuccess(Message message)
/*      */       {
/*  260 */         if (this.val$callback != null)
/*  261 */           LibHandlerStub.this.mCallbackHandler.post(new Runnable(message)
/*      */           {
/*      */             public void run() {
/*      */               try {
/*  265 */                 LibHandlerStub.5.this.val$callback.onSuccess(this.val$message);
/*      */               } catch (RemoteException e) {
/*  267 */                 e.printStackTrace();
/*      */               }
/*      */             }
/*      */           });
/*      */       }
/*      */ 
/*      */       public void onError(Message message, int code)
/*      */       {
/*  276 */         if (this.val$callback != null)
/*  277 */           LibHandlerStub.this.mCallbackHandler.post(new Runnable(message, code)
/*      */           {
/*      */             public void run() {
/*      */               try {
/*  281 */                 LibHandlerStub.5.this.val$callback.onError(this.val$message, this.val$code);
/*      */               } catch (RemoteException e) {
/*  283 */                 e.printStackTrace();
/*      */               }
/*      */             }
/*      */           });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void sendDirectionalMessage(Message message, String pushContent, String pushData, String[] userIds, ISendMessageCallback callback) throws RemoteException {
/*  294 */     this.mClient.sendMessage(message, pushContent, pushData, userIds, new NativeClient.ISendMessageCallback(callback)
/*      */     {
/*      */       public void onAttached(Message message)
/*      */       {
/*  298 */         if (this.val$callback != null)
/*  299 */           LibHandlerStub.this.mCallbackHandler.post(new Runnable(message)
/*      */           {
/*      */             public void run() {
/*      */               try {
/*  303 */                 LibHandlerStub.6.this.val$callback.onAttached(this.val$message);
/*      */               } catch (RemoteException e) {
/*  305 */                 e.printStackTrace();
/*      */               }
/*      */             }
/*      */           });
/*      */       }
/*      */ 
/*      */       public void onSuccess(Message message)
/*      */       {
/*  314 */         if (this.val$callback != null)
/*  315 */           LibHandlerStub.this.mCallbackHandler.post(new Runnable(message)
/*      */           {
/*      */             public void run() {
/*      */               try {
/*  319 */                 LibHandlerStub.6.this.val$callback.onSuccess(this.val$message);
/*      */               } catch (RemoteException e) {
/*  321 */                 e.printStackTrace();
/*      */               }
/*      */             }
/*      */           });
/*      */       }
/*      */ 
/*      */       public void onError(Message message, int code)
/*      */       {
/*  330 */         if (this.val$callback != null)
/*  331 */           LibHandlerStub.this.mCallbackHandler.post(new Runnable(message, code)
/*      */           {
/*      */             public void run() {
/*      */               try {
/*  335 */                 LibHandlerStub.6.this.val$callback.onError(this.val$message, this.val$code);
/*      */               } catch (RemoteException e) {
/*  337 */                 e.printStackTrace();
/*      */               }
/*      */             }
/*      */           });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void sendLocationMessage(Message message, String pushContent, String pushData, ISendMessageCallback callback) throws RemoteException {
/*  348 */     this.mClient.sendLocationMessage(message, pushContent, pushData, new NativeClient.ISendMessageCallback(callback)
/*      */     {
/*      */       public void onAttached(Message message) {
/*  351 */         if (this.val$callback != null)
/*  352 */           LibHandlerStub.this.mCallbackHandler.post(new Runnable(message)
/*      */           {
/*      */             public void run() {
/*      */               try {
/*  356 */                 LibHandlerStub.7.this.val$callback.onAttached(this.val$message);
/*      */               } catch (RemoteException e) {
/*  358 */                 e.printStackTrace();
/*      */               }
/*      */             }
/*      */           });
/*      */       }
/*      */ 
/*      */       public void onSuccess(Message message)
/*      */       {
/*  367 */         if (this.val$callback != null)
/*  368 */           LibHandlerStub.this.mCallbackHandler.post(new Runnable(message)
/*      */           {
/*      */             public void run() {
/*      */               try {
/*  372 */                 LibHandlerStub.7.this.val$callback.onSuccess(this.val$message);
/*      */               } catch (RemoteException e) {
/*  374 */                 e.printStackTrace();
/*      */               }
/*      */             }
/*      */           });
/*      */       }
/*      */ 
/*      */       public void onError(Message message, int code)
/*      */       {
/*  383 */         if (this.val$callback != null)
/*  384 */           LibHandlerStub.this.mCallbackHandler.post(new Runnable(message, code)
/*      */           {
/*      */             public void run() {
/*      */               try {
/*  388 */                 LibHandlerStub.7.this.val$callback.onError(this.val$message, this.val$code);
/*      */               } catch (RemoteException e) {
/*  390 */                 e.printStackTrace();
/*      */               }
/*      */             }
/*      */           });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public Message sendStatusMessage(Message message, ILongCallback callback) throws RemoteException
/*      */   {
/*  402 */     Message result = this.mClient.sendStatusMessage(message.getConversationType(), message.getTargetId(), message.getContent(), 1, new NativeClient.IResultCallback(callback)
/*      */     {
/*      */       public void onSuccess(Integer messageId)
/*      */       {
/*  409 */         if (this.val$callback == null)
/*  410 */           return;
/*  411 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(messageId)
/*      */         {
/*      */           public void run() {
/*      */             try {
/*  415 */               LibHandlerStub.8.this.val$callback.onComplete(this.val$messageId.intValue());
/*      */             } catch (RemoteException e) {
/*  417 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int errorCode)
/*      */       {
/*  426 */         if (this.val$callback == null)
/*  427 */           return;
/*  428 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*      */             try {
/*  432 */               LibHandlerStub.8.this.val$callback.onFailure(this.val$errorCode);
/*      */             } catch (RemoteException e) {
/*  434 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*  441 */     result.setSenderUserId(this.mCurrentUserId);
/*      */ 
/*  443 */     return result;
/*      */   }
/*      */ 
/*      */   public List<Message> getNewestMessages(Conversation conversation, int count) throws RemoteException
/*      */   {
/*  448 */     List list = this.mClient.getLatestMessages(conversation.getConversationType(), conversation.getTargetId(), count);
/*  449 */     if ((list == null) || (list.size() == 0)) {
/*  450 */       return null;
/*      */     }
/*  452 */     return list;
/*      */   }
/*      */ 
/*      */   public List<Message> getOlderMessages(Conversation conversation, long flagId, int count) throws RemoteException
/*      */   {
/*  457 */     List list = this.mClient.getHistoryMessages(conversation.getConversationType(), conversation.getTargetId(), (int)flagId, count);
/*  458 */     if ((list == null) || (list.size() == 0)) {
/*  459 */       return null;
/*      */     }
/*      */ 
/*  462 */     return list;
/*      */   }
/*      */ 
/*      */   public void getRemoteHistoryMessages(Conversation conversation, long dataTime, int count, IResultCallback callback) throws RemoteException
/*      */   {
/*  467 */     this.mClient.getRemoteHistoryMessages(conversation.getConversationType(), conversation.getTargetId(), dataTime, count, new NativeClient.IResultCallback(callback)
/*      */     {
/*      */       public void onError(int code) {
/*  470 */         if (this.val$callback != null)
/*      */           try {
/*  472 */             this.val$callback.onFailure(code);
/*      */           } catch (RemoteException e) {
/*  474 */             e.printStackTrace();
/*      */           }
/*      */       }
/*      */ 
/*      */       public void onSuccess(List<Message> messages)
/*      */       {
/*  481 */         if (this.val$callback != null) {
/*  482 */           RemoteModelWrap result = null;
/*      */           try {
/*  484 */             if ((messages == null) || (messages.size() == 0)) {
/*  485 */               this.val$callback.onComplete(result);
/*      */             } else {
/*  487 */               result = new RemoteModelWrap(RongListWrap.obtain(messages, Message.class));
/*  488 */               this.val$callback.onComplete(result);
/*      */             }
/*      */           } catch (RemoteException e) {
/*  491 */             e.printStackTrace();
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public List<Message> getOlderMessagesByObjectName(Conversation conversation, String objectName, long flagId, int count, boolean flag) throws RemoteException {
/*  500 */     List list = this.mClient.getHistoryMessages(conversation.getConversationType(), conversation.getTargetId(), objectName, (int)flagId, count, flag);
/*  501 */     if ((list == null) || (list.size() == 0)) {
/*  502 */       return null;
/*      */     }
/*  504 */     return list;
/*      */   }
/*      */ 
/*      */   public boolean deleteMessage(int[] ids) throws RemoteException
/*      */   {
/*  509 */     if ((ids == null) || (ids.length == 0))
/*  510 */       return false;
/*  511 */     return this.mClient.deleteMessages(ids);
/*      */   }
/*      */ 
/*      */   public boolean deleteConversationMessage(int conversationType, String targetId) throws RemoteException
/*      */   {
/*  516 */     return this.mClient.deleteMessage(Conversation.ConversationType.setValue(conversationType), targetId);
/*      */   }
/*      */ 
/*      */   public boolean clearMessages(Conversation conversation)
/*      */     throws RemoteException
/*      */   {
/*  522 */     return this.mClient.clearMessages(conversation.getConversationType(), conversation.getTargetId());
/*      */   }
/*      */ 
/*      */   public boolean clearMessagesUnreadStatus(Conversation conversation) throws RemoteException
/*      */   {
/*  527 */     return this.mClient.clearMessagesUnreadStatus(conversation.getConversationType(), conversation.getTargetId());
/*      */   }
/*      */ 
/*      */   public boolean setMessageExtra(int messageId, String values) throws RemoteException
/*      */   {
/*  532 */     return this.mClient.setMessageExtra(messageId, values);
/*      */   }
/*      */ 
/*      */   public boolean setMessageReceivedStatus(int messageId, int status) throws RemoteException
/*      */   {
/*  537 */     return this.mClient.setMessageReceivedStatus(messageId, new Message.ReceivedStatus(status));
/*      */   }
/*      */ 
/*      */   public boolean setMessageSentStatus(int messageId, int status) throws RemoteException
/*      */   {
/*  542 */     return this.mClient.setMessageSentStatus(messageId, Message.SentStatus.setValue(status));
/*      */   }
/*      */ 
/*      */   public List<Conversation> getConversationList() throws RemoteException
/*      */   {
/*  547 */     List list = this.mClient.getConversationList();
/*  548 */     if ((list == null) || (list.size() == 0)) {
/*  549 */       return null;
/*      */     }
/*  551 */     return list;
/*      */   }
/*      */ 
/*      */   public boolean updateConversationInfo(int type, String targetId, String title, String portrait)
/*      */   {
/*  557 */     return this.mClient.updateConversationInfo(Conversation.ConversationType.setValue(type), targetId, title, portrait);
/*      */   }
/*      */ 
/*      */   public List<Conversation> getConversationListByType(int[] types) throws RemoteException
/*      */   {
/*  562 */     List list = this.mClient.getConversationList(types);
/*  563 */     if ((list == null) || (list.size() == 0)) {
/*  564 */       return null;
/*      */     }
/*  566 */     return list;
/*      */   }
/*      */ 
/*      */   public Conversation getConversation(int type, String targetId) throws RemoteException
/*      */   {
/*  571 */     return this.mClient.getConversation(Conversation.ConversationType.setValue(type), targetId);
/*      */   }
/*      */ 
/*      */   public boolean removeConversation(int typeValue, String targetId) throws RemoteException
/*      */   {
/*  576 */     Conversation.ConversationType conversationType = Conversation.ConversationType.setValue(typeValue);
/*  577 */     if (conversationType == null) {
/*  578 */       RLog.i("LibHandlerStub", "removeConversation the conversation type is null");
/*  579 */       return false;
/*      */     }
/*  581 */     return this.mClient.removeConversation(conversationType, targetId);
/*      */   }
/*      */ 
/*      */   public boolean clearConversations(int[] types) throws RemoteException
/*      */   {
/*  586 */     if ((types == null) || (types.length == 0)) {
/*  587 */       return false;
/*      */     }
/*  589 */     Conversation.ConversationType[] conversationTypes = new Conversation.ConversationType[types.length];
/*      */ 
/*  591 */     for (int i = 0; i < types.length; i++) {
/*  592 */       conversationTypes[i] = Conversation.ConversationType.setValue(types[i]);
/*      */     }
/*  594 */     return this.mClient.clearConversations(conversationTypes);
/*      */   }
/*      */ 
/*      */   public boolean saveConversationDraft(Conversation conversation, String content)
/*      */     throws RemoteException
/*      */   {
/*  600 */     RLog.i("LibHandlerStub", "saveConversationDraft " + content);
/*  601 */     return this.mClient.saveTextMessageDraft(conversation.getConversationType(), conversation.getTargetId(), content);
/*      */   }
/*      */ 
/*      */   public String getConversationDraft(Conversation conversation) throws RemoteException
/*      */   {
/*  606 */     return this.mClient.getTextMessageDraft(conversation.getConversationType(), conversation.getTargetId());
/*      */   }
/*      */ 
/*      */   public boolean cleanConversationDraft(Conversation conversation) throws RemoteException
/*      */   {
/*  611 */     return this.mClient.clearTextMessageDraft(conversation.getConversationType(), conversation.getTargetId());
/*      */   }
/*      */ 
/*      */   public void getConversationNotificationStatus(int type, String targetId, ILongCallback callback) throws RemoteException
/*      */   {
/*  616 */     this.mClient.getConversationNotificationStatus(Conversation.ConversationType.setValue(type), targetId, new NativeClient.IResultCallback(callback)
/*      */     {
/*      */       public void onSuccess(Integer status) {
/*  619 */         if (this.val$callback == null)
/*  620 */           return;
/*  621 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(status)
/*      */         {
/*      */           public void run() {
/*      */             try {
/*  625 */               LibHandlerStub.10.this.val$callback.onComplete(this.val$status.intValue());
/*      */             } catch (RemoteException e) {
/*  627 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int errorCode) {
/*  635 */         if (this.val$callback == null)
/*  636 */           return;
/*  637 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*      */             try {
/*  641 */               LibHandlerStub.10.this.val$callback.onFailure(this.val$errorCode);
/*      */             } catch (RemoteException e) {
/*  643 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setConversationNotificationStatus(int type, String targetId, int status, ILongCallback callback) throws RemoteException {
/*  654 */     this.mClient.setConversationNotificationStatus(Conversation.ConversationType.setValue(type), targetId, Conversation.ConversationNotificationStatus.setValue(status), new NativeClient.IResultCallback(callback)
/*      */     {
/*      */       public void onSuccess(Integer status) {
/*  657 */         if (this.val$callback == null)
/*  658 */           return;
/*  659 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(status)
/*      */         {
/*      */           public void run() {
/*      */             try {
/*  663 */               LibHandlerStub.11.this.val$callback.onComplete(this.val$status.intValue());
/*      */             } catch (RemoteException e) {
/*  665 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int errorCode) {
/*  673 */         if (this.val$callback == null)
/*  674 */           return;
/*  675 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*      */             try {
/*  679 */               LibHandlerStub.11.this.val$callback.onFailure(this.val$errorCode);
/*      */             } catch (RemoteException e) {
/*  681 */               e.printStackTrace();
/*      */             }
/*      */           } } );
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public boolean setConversationTopStatus(int typeValue, String targetId, boolean isTop) {
/*  691 */     Conversation.ConversationType conversationType = Conversation.ConversationType.setValue(typeValue);
/*      */ 
/*  693 */     if (conversationType == null) {
/*  694 */       RLog.e("LibHandlerStub", "setConversationTopStatus ConversationType is null");
/*  695 */       return false;
/*      */     }
/*      */ 
/*  698 */     return this.mClient.setConversationToTop(conversationType, targetId, isTop);
/*      */   }
/*      */ 
/*      */   public int getConversationUnreadCount(Conversation conversation)
/*      */   {
/*  703 */     return this.mClient.getUnreadCount(conversation.getConversationType(), conversation.getTargetId());
/*      */   }
/*      */ 
/*      */   public void getDiscussion(String id, IResultCallback callback) throws RemoteException
/*      */   {
/*  708 */     this.mClient.getDiscussion(id, new NativeClient.IResultCallback(callback)
/*      */     {
/*      */       public void onSuccess(Discussion discussion) {
/*  711 */         if (this.val$callback == null) {
/*  712 */           return;
/*      */         }
/*  714 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(discussion)
/*      */         {
/*      */           public void run() {
/*  717 */             RemoteModelWrap result = new RemoteModelWrap(this.val$discussion);
/*      */             try {
/*  719 */               LibHandlerStub.12.this.val$callback.onComplete(result);
/*      */             } catch (RemoteException e) {
/*  721 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int errorCode) {
/*  729 */         if (this.val$callback == null) {
/*  730 */           return;
/*      */         }
/*  732 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*      */             try {
/*  736 */               LibHandlerStub.12.this.val$callback.onFailure(this.val$errorCode);
/*      */             } catch (RemoteException e) {
/*  738 */               e.printStackTrace();
/*      */             }
/*      */           } } );
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setDiscussionName(String id, String name, IOperationCallback callback) throws RemoteException {
/*  748 */     this.mClient.setDiscussionName(id, name, new OperationCallback(callback));
/*      */   }
/*      */ 
/*      */   public void createDiscussion(String name, List<String> userIds, IResultCallback callback) throws RemoteException
/*      */   {
/*  753 */     this.mClient.createDiscussion(name, userIds, new NativeClient.IResultCallback(callback, name, userIds)
/*      */     {
/*      */       public void onSuccess(String discussionId) {
/*  756 */         if (this.val$callback == null) {
/*  757 */           return;
/*      */         }
/*  759 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(discussionId)
/*      */         {
/*      */           public void run() {
/*  762 */             Discussion model = new Discussion(this.val$discussionId, LibHandlerStub.13.this.val$name, LibHandlerStub.this.mCurrentUserId, true, LibHandlerStub.13.this.val$userIds);
/*      */ 
/*  764 */             RemoteModelWrap result = new RemoteModelWrap(model);
/*      */             try
/*      */             {
/*  767 */               LibHandlerStub.13.this.val$callback.onComplete(result);
/*      */             } catch (RemoteException e) {
/*  769 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int errorCode) {
/*  777 */         if (this.val$callback == null) {
/*  778 */           return;
/*      */         }
/*  780 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*      */             try {
/*  784 */               LibHandlerStub.13.this.val$callback.onFailure(this.val$errorCode);
/*      */             } catch (RemoteException e) {
/*  786 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void searchPublicService(String keyWords, int businessType, int searchType, IResultCallback callback) {
/*  797 */     this.mClient.searchPublicService(keyWords, businessType, searchType, new NativeClient.IResultCallback(callback)
/*      */     {
/*      */       public void onSuccess(PublicServiceProfileList publicServiceInfoList)
/*      */       {
/*  801 */         RemoteModelWrap result = new RemoteModelWrap(publicServiceInfoList);
/*      */ 
/*  803 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(result)
/*      */         {
/*      */           public void run() {
/*      */             try {
/*  807 */               LibHandlerStub.14.this.val$callback.onComplete(this.val$result);
/*      */             } catch (RemoteException e) {
/*  809 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int code) {
/*  817 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(code)
/*      */         {
/*      */           public void run() {
/*      */             try {
/*  821 */               LibHandlerStub.14.this.val$callback.onFailure(this.val$code);
/*      */             } catch (RemoteException e) {
/*  823 */               e.printStackTrace();
/*      */             }
/*      */           } } );
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void subscribePublicService(String targetId, int categoryId, boolean subscribe, IOperationCallback callback) {
/*  833 */     this.mClient.subscribePublicService(targetId, categoryId, subscribe, new NativeClient.OperationCallback(callback)
/*      */     {
/*      */       public void onSuccess()
/*      */       {
/*  837 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable()
/*      */         {
/*      */           public void run() {
/*      */             try {
/*  841 */               LibHandlerStub.15.this.val$callback.onComplete();
/*      */             } catch (RemoteException e) {
/*  843 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int errorCode) {
/*  851 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*      */             try {
/*  855 */               LibHandlerStub.15.this.val$callback.onFailure(this.val$errorCode);
/*      */             } catch (RemoteException e) {
/*  857 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getPublicServiceProfile(String targetId, int conversationType, IResultCallback callback) {
/*  868 */     this.mClient.getPublicServiceProfile(targetId, conversationType, new NativeClient.IResultCallback(callback)
/*      */     {
/*      */       public void onSuccess(PublicServiceProfile info)
/*      */       {
/*  873 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(info)
/*      */         {
/*      */           public void run() {
/*  876 */             RemoteModelWrap mModelWrap = null;
/*  877 */             if (this.val$info != null)
/*  878 */               mModelWrap = new RemoteModelWrap(this.val$info);
/*      */             try {
/*  880 */               LibHandlerStub.16.this.val$callback.onComplete(mModelWrap);
/*      */             } catch (RemoteException e) {
/*  882 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int errorCode)
/*      */       {
/*  891 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*      */             try {
/*  895 */               LibHandlerStub.16.this.val$callback.onFailure(this.val$errorCode);
/*      */             } catch (RemoteException e) {
/*  897 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getPublicServiceList(IResultCallback callback) {
/*  908 */     this.mClient.getPublicServiceList(new NativeClient.IResultCallback(callback)
/*      */     {
/*      */       public void onSuccess(PublicServiceProfileList list)
/*      */       {
/*  913 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(list)
/*      */         {
/*      */           public void run() {
/*  916 */             RemoteModelWrap mModelWrap = new RemoteModelWrap(this.val$list);
/*      */             try {
/*  918 */               LibHandlerStub.17.this.val$callback.onComplete(mModelWrap);
/*      */             } catch (RemoteException e) {
/*  920 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int errorCode) {
/*  928 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*      */             try {
/*  932 */               LibHandlerStub.17.this.val$callback.onFailure(this.val$errorCode);
/*      */             } catch (RemoteException e) {
/*  934 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void addMemberToDiscussion(String id, List<String> userIds, IOperationCallback callback) throws RemoteException {
/*  945 */     this.mClient.addMemberToDiscussion(id, userIds, new OperationCallback(callback));
/*      */   }
/*      */ 
/*      */   public void removeDiscussionMember(String id, String userId, IOperationCallback callback) throws RemoteException
/*      */   {
/*  950 */     this.mClient.removeMemberFromDiscussion(id, userId, new OperationCallback(callback));
/*      */   }
/*      */ 
/*      */   public void quitDiscussion(String id, IOperationCallback callback) throws RemoteException
/*      */   {
/*  955 */     this.mClient.quitDiscussion(id, new OperationCallback(callback));
/*      */   }
/*      */ 
/*      */   public void syncGroup(List<Group> groups, IOperationCallback callback) throws RemoteException
/*      */   {
/*  960 */     this.mClient.syncGroup(groups, new OperationCallback(callback));
/*      */   }
/*      */ 
/*      */   public void joinGroup(String id, String name, IOperationCallback callback) throws RemoteException
/*      */   {
/*  965 */     this.mClient.joinGroup(id, name, new OperationCallback(callback));
/*      */   }
/*      */ 
/*      */   public void quitGroup(String id, IOperationCallback callback) throws RemoteException
/*      */   {
/*  970 */     this.mClient.quitGroup(id, new OperationCallback(callback));
/*      */   }
/*      */ 
/*      */   public void getChatRoomInfo(String id, int count, int order, IResultCallback callback)
/*      */   {
/*  975 */     this.mClient.queryChatRoomInfo(id, count, order, new NativeClient.IResultCallback(callback)
/*      */     {
/*      */       public void onSuccess(ChatRoomInfo chatRoomInfo) {
/*      */         try {
/*  979 */           RemoteModelWrap result = new RemoteModelWrap(chatRoomInfo);
/*  980 */           this.val$callback.onComplete(result);
/*      */         } catch (RemoteException e) {
/*  982 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void onError(int code)
/*      */       {
/*      */         try {
/*  989 */           this.val$callback.onFailure(code);
/*      */         } catch (RemoteException e) {
/*  991 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void joinChatRoom(String id, int defMessageCount, IOperationCallback callback) throws RemoteException {
/*  999 */     this.mClient.joinChatRoom(id, defMessageCount, new NativeClient.OperationCallback(callback)
/*      */     {
/*      */       public void onSuccess() {
/* 1002 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable()
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1006 */               LibHandlerStub.19.this.val$callback.onComplete();
/*      */             } catch (RemoteException e) {
/* 1008 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int errorCode) {
/* 1016 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1020 */               LibHandlerStub.19.this.val$callback.onFailure(this.val$errorCode);
/*      */             } catch (RemoteException e) {
/* 1022 */               e.printStackTrace();
/*      */             }
/*      */           } } );
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void reJoinChatRoom(String id, int defMessageCount, IOperationCallback callback) throws RemoteException {
/* 1032 */     this.mClient.reJoinChatRoom(id, defMessageCount, new NativeClient.OperationCallback(callback)
/*      */     {
/*      */       public void onSuccess() {
/* 1035 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable()
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1039 */               LibHandlerStub.20.this.val$callback.onComplete();
/*      */             } catch (RemoteException e) {
/* 1041 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int errorCode) {
/* 1049 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1053 */               LibHandlerStub.20.this.val$callback.onFailure(this.val$errorCode);
/*      */             } catch (RemoteException e) {
/* 1055 */               e.printStackTrace();
/*      */             }
/*      */           } } );
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void joinExistChatRoom(String id, int defMessageCount, IOperationCallback callback) throws RemoteException {
/* 1065 */     this.mClient.joinExistChatRoom(id, defMessageCount, new NativeClient.OperationCallback(callback)
/*      */     {
/*      */       public void onSuccess() {
/* 1068 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable()
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1072 */               LibHandlerStub.21.this.val$callback.onComplete();
/*      */             } catch (RemoteException e) {
/* 1074 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int errorCode) {
/* 1082 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1086 */               LibHandlerStub.21.this.val$callback.onFailure(this.val$errorCode);
/*      */             } catch (RemoteException e) {
/* 1088 */               e.printStackTrace();
/*      */             }
/*      */           } } );
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void quitChatRoom(String id, IOperationCallback callback) throws RemoteException {
/* 1098 */     this.mClient.quitChatRoom(id, new NativeClient.OperationCallback(callback)
/*      */     {
/*      */       public void onSuccess() {
/* 1101 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable()
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1105 */               LibHandlerStub.22.this.val$callback.onComplete();
/*      */             } catch (RemoteException e) {
/* 1107 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int errorCode) {
/* 1115 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1119 */               LibHandlerStub.22.this.val$callback.onFailure(this.val$errorCode);
/*      */             } catch (RemoteException e) {
/* 1121 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setNotificationQuietHours(String startTime, int spanMinutes, IOperationCallback callback) {
/* 1132 */     this.mClient.setNotificationQuietHours(startTime, spanMinutes, new NativeClient.OperationCallback(callback)
/*      */     {
/*      */       public void onSuccess() {
/* 1135 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable()
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1139 */               LibHandlerStub.23.this.val$callback.onComplete();
/*      */             } catch (RemoteException e) {
/* 1141 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int errorCode) {
/* 1149 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1153 */               LibHandlerStub.23.this.val$callback.onFailure(this.val$errorCode);
/*      */             } catch (RemoteException e) {
/* 1155 */               e.printStackTrace();
/*      */             }
/*      */           } } );
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void removeNotificationQuietHours(IOperationCallback callback) {
/* 1165 */     this.mClient.removeNotificationQuietHours(new NativeClient.OperationCallback(callback)
/*      */     {
/*      */       public void onError(int errorCode)
/*      */       {
/* 1169 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1173 */               LibHandlerStub.24.this.val$callback.onFailure(this.val$errorCode);
/*      */             } catch (RemoteException e) {
/* 1175 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onSuccess()
/*      */       {
/* 1184 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable()
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1188 */               LibHandlerStub.24.this.val$callback.onComplete();
/*      */             } catch (RemoteException e) {
/* 1190 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getNotificationQuietHours(IGetNotificationQuietHoursCallback callback)
/*      */   {
/* 1202 */     this.mClient.getNotificationQuietHours(new NativeClient.GetNotificationQuietHoursCallback(callback)
/*      */     {
/*      */       public void onError(int code) {
/* 1205 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(code)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1209 */               LibHandlerStub.25.this.val$callback.onError(this.val$code);
/*      */             } catch (RemoteException e) {
/* 1211 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onSuccess(String start, int min) {
/* 1219 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(start, min)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1223 */               LibHandlerStub.25.this.val$callback.onSuccess(this.val$start, this.val$min);
/*      */             } catch (RemoteException e) {
/* 1225 */               e.printStackTrace();
/*      */             }
/*      */           } } );
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public boolean validateAuth(String auth) {
/* 1235 */     return false;
/*      */   }
/*      */ 
/*      */   public void uploadMedia(Message message, IUploadCallback callback)
/*      */   {
/* 1242 */     this.mClient.uploadMedia(message, new NativeClient.IResultProgressCallback(callback)
/*      */     {
/*      */       public void onProgress(int progress) {
/* 1245 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(progress)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1249 */               LibHandlerStub.26.this.val$callback.onProgress(this.val$progress);
/*      */             } catch (RemoteException e) {
/* 1251 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onSuccess(String uri)
/*      */       {
/* 1260 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(uri)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1264 */               LibHandlerStub.26.this.val$callback.onComplete(this.val$uri);
/*      */             } catch (RemoteException e) {
/* 1266 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int errorCode) {
/* 1274 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1278 */               LibHandlerStub.26.this.val$callback.onFailure(this.val$errorCode);
/*      */             } catch (RemoteException e) {
/* 1280 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void downloadMedia(Conversation conversation, int type, String imageUrl, IDownloadMediaCallback callback) throws RemoteException {
/* 1291 */     this.mClient.downloadMedia(conversation.getConversationType(), conversation.getTargetId(), type, imageUrl, new NativeClient.IResultProgressCallback(callback)
/*      */     {
/*      */       public void onProgress(int progress)
/*      */       {
/* 1295 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(progress)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1299 */               LibHandlerStub.27.this.val$callback.onProgress(this.val$progress);
/*      */             } catch (RemoteException e) {
/* 1301 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onSuccess(String localMediaPath) {
/* 1309 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(localMediaPath)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1313 */               LibHandlerStub.27.this.val$callback.onComplete(this.val$localMediaPath);
/*      */             } catch (RemoteException e) {
/* 1315 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int errorCode) {
/* 1323 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1327 */               LibHandlerStub.27.this.val$callback.onFailure(this.val$errorCode);
/*      */             } catch (RemoteException e) {
/* 1329 */               e.printStackTrace();
/*      */             }
/*      */           } } );
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void downloadMediaMessage(Message message, IDownloadMediaMessageCallback callback) throws RemoteException {
/* 1339 */     this.mClient.downloadMediaMessage(message, new NativeClient.IDownloadMediaMessageCallback(callback)
/*      */     {
/*      */       public void onSuccess(Message message) {
/*      */         try {
/* 1343 */           this.val$callback.onComplete(message);
/*      */         } catch (RemoteException e) {
/* 1345 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void onProgress(int progress)
/*      */       {
/*      */         try {
/* 1352 */           this.val$callback.onProgress(progress);
/*      */         } catch (RemoteException e) {
/* 1354 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void onCanceled()
/*      */       {
/*      */         try {
/* 1361 */           this.val$callback.onCanceled();
/*      */         } catch (RemoteException e) {
/* 1363 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void onError(int code)
/*      */       {
/*      */         try {
/* 1370 */           this.val$callback.onFailure(code);
/*      */         } catch (RemoteException e) {
/* 1372 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void cancelDownloadMediaMessage(Message message, IOperationCallback callback) throws RemoteException {
/* 1380 */     FileTransferClient.getInstance().cancel(message.getMessageId(), new CancelCallback(callback)
/*      */     {
/*      */       public void onCanceled(Object tag) {
/*      */         try {
/* 1384 */           this.val$callback.onComplete();
/*      */         } catch (RemoteException e) {
/* 1386 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void onError(int code)
/*      */       {
/*      */         try {
/* 1393 */           this.val$callback.onFailure(code);
/*      */         } catch (RemoteException e) {
/* 1395 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public long getDeltaTime() {
/* 1403 */     return this.mClient.getDeltaTime();
/*      */   }
/*      */ 
/*      */   public void setDiscussionInviteStatus(String targetId, int status, IOperationCallback callback)
/*      */   {
/* 1408 */     this.mClient.setDiscussionInviteStatus(targetId, status, new NativeClient.OperationCallback(callback)
/*      */     {
/*      */       public void onSuccess()
/*      */       {
/* 1412 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable()
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1416 */               LibHandlerStub.30.this.val$callback.onComplete();
/*      */             } catch (RemoteException e) {
/* 1418 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int code) {
/* 1426 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(code)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1430 */               LibHandlerStub.30.this.val$callback.onFailure(this.val$code);
/*      */             } catch (RemoteException e) {
/* 1432 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void addToBlacklist(String userId, IOperationCallback callback)
/*      */   {
/* 1445 */     this.mClient.addToBlacklist(userId, new NativeClient.OperationCallback(callback)
/*      */     {
/*      */       public void onSuccess()
/*      */       {
/* 1449 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable()
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1453 */               LibHandlerStub.31.this.val$callback.onComplete();
/*      */             } catch (RemoteException e) {
/* 1455 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int code) {
/* 1463 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(code)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1467 */               LibHandlerStub.31.this.val$callback.onFailure(this.val$code);
/*      */             } catch (RemoteException e) {
/* 1469 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void removeFromBlacklist(String userId, IOperationCallback callback)
/*      */   {
/* 1481 */     this.mClient.removeFromBlacklist(userId, new NativeClient.OperationCallback(callback)
/*      */     {
/*      */       public void onSuccess()
/*      */       {
/* 1485 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable()
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1489 */               LibHandlerStub.32.this.val$callback.onComplete();
/*      */             } catch (RemoteException e) {
/* 1491 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int code) {
/* 1499 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(code)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1503 */               LibHandlerStub.32.this.val$callback.onFailure(this.val$code);
/*      */             } catch (RemoteException e) {
/* 1505 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getBlacklistStatus(String userId, IIntegerCallback callback)
/*      */   {
/* 1517 */     this.mClient.getBlacklistStatus(userId, new NativeClient.IResultCallback(callback)
/*      */     {
/*      */       public void onError(int code)
/*      */       {
/* 1521 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(code)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1525 */               LibHandlerStub.33.this.val$callback.onFailure(this.val$code);
/*      */             } catch (RemoteException e) {
/* 1527 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onSuccess(NativeClient.BlacklistStatus blacklistStatus) {
/* 1535 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(blacklistStatus)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1539 */               LibHandlerStub.33.this.val$callback.onComplete(this.val$blacklistStatus.getValue());
/*      */             } catch (RemoteException e) {
/* 1541 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getBlacklist(IStringCallback callback) {
/* 1552 */     this.mClient.getBlacklist(new NativeClient.IResultCallback(callback)
/*      */     {
/*      */       public void onSuccess(String userIds)
/*      */       {
/* 1557 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(userIds)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1561 */               LibHandlerStub.34.this.val$callback.onComplete(this.val$userIds);
/*      */             } catch (RemoteException e) {
/* 1563 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int code) {
/* 1571 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(code)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1575 */               LibHandlerStub.34.this.val$callback.onFailure(this.val$code);
/*      */             } catch (RemoteException e) {
/* 1577 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public String getTextMessageDraft(Conversation conversation)
/*      */   {
/* 1589 */     return this.mClient.getTextMessageDraft(conversation.getConversationType(), conversation.getTargetId());
/*      */   }
/*      */ 
/*      */   public boolean saveTextMessageDraft(Conversation conversation, String content)
/*      */   {
/* 1594 */     return this.mClient.saveTextMessageDraft(conversation.getConversationType(), conversation.getTargetId(), content);
/*      */   }
/*      */ 
/*      */   public boolean clearTextMessageDraft(Conversation conversation)
/*      */   {
/* 1599 */     return this.mClient.clearTextMessageDraft(conversation.getConversationType(), conversation.getTargetId());
/*      */   }
/*      */ 
/*      */   public void setUserData(UserData userData, IOperationCallback callback)
/*      */   {
/* 1604 */     this.mClient.setUserData(userData, new NativeClient.OperationCallback(callback)
/*      */     {
/*      */       public void onSuccess()
/*      */       {
/* 1608 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable()
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1612 */               LibHandlerStub.35.this.val$callback.onComplete();
/*      */             } catch (RemoteException e) {
/* 1614 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int code) {
/* 1622 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(code)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1626 */               LibHandlerStub.35.this.val$callback.onFailure(this.val$code);
/*      */             } catch (RemoteException e) {
/* 1628 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public int setupRealTimeLocation(int conversationType, String targetId)
/*      */   {
/* 1682 */     return this.mClient.setupRealTimeLocation(this.mContext, conversationType, targetId);
/*      */   }
/*      */ 
/*      */   public int startRealTimeLocation(int type, String targetId)
/*      */   {
/* 1687 */     return this.mClient.startRealTimeLocation(type, targetId);
/*      */   }
/*      */ 
/*      */   public int joinRealTimeLocation(int type, String targetId)
/*      */   {
/* 1692 */     return this.mClient.joinRealTimeLocation(type, targetId);
/*      */   }
/*      */ 
/*      */   public void quitRealTimeLocation(int type, String targetId)
/*      */   {
/* 1697 */     this.mClient.quitRealTimeLocation(type, targetId);
/*      */   }
/*      */ 
/*      */   public List<String> getRealTimeLocationParticipants(int type, String targetId)
/*      */   {
/* 1702 */     return this.mClient.getRealTimeLocationParticipants(type, targetId);
/*      */   }
/*      */ 
/*      */   public int getRealTimeLocationCurrentState(int type, String targetId)
/*      */   {
/* 1707 */     RealTimeLocationConstant.RealTimeLocationStatus state = this.mClient.getRealTimeLocationCurrentState(type, targetId);
/*      */ 
/* 1709 */     return state.getValue();
/*      */   }
/*      */ 
/*      */   public void addRealTimeLocationListener(int type, String targetId, IRealTimeLocationListener listener)
/*      */   {
/* 1714 */     NativeClient.RealTimeLocationListener nativeListener = new NativeClient.RealTimeLocationListener(listener)
/*      */     {
/*      */       public void onStatusChange(RealTimeLocationConstant.RealTimeLocationStatus status) {
/*      */         try {
/* 1718 */           this.val$listener.onStatusChange(status.getValue());
/*      */         } catch (RemoteException e) {
/* 1720 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void onReceiveLocation(double latitude, double longitude, String userId)
/*      */       {
/*      */         try {
/* 1727 */           this.val$listener.onReceiveLocation(latitude, longitude, userId);
/*      */         } catch (RemoteException e) {
/* 1729 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void onParticipantsJoin(String userId)
/*      */       {
/*      */         try {
/* 1736 */           this.val$listener.onParticipantsJoin(userId);
/*      */         } catch (RemoteException e) {
/* 1738 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void onParticipantsQuit(String userId)
/*      */       {
/*      */         try {
/* 1745 */           this.val$listener.onParticipantsQuit(userId);
/*      */         } catch (RemoteException e) {
/* 1747 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void onError(RealTimeLocationConstant.RealTimeLocationErrorCode errorCode)
/*      */       {
/*      */         try {
/* 1754 */           this.val$listener.onError(errorCode.getValue());
/*      */         } catch (RemoteException e) {
/* 1756 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     };
/* 1760 */     this.mClient.addListener(type, targetId, nativeListener);
/*      */   }
/*      */ 
/*      */   public void updateRealTimeLocationStatus(int type, String targetId, double latitude, double longitude)
/*      */   {
/* 1765 */     this.mClient.updateRealTimeLocationStatus(type, targetId, latitude, longitude);
/*      */   }
/*      */ 
/*      */   public boolean updateMessageReceiptStatus(String targetId, int categoryId, long timestamp)
/*      */   {
/* 1770 */     return this.mClient.updateMessageReceiptStatus(targetId, categoryId, timestamp);
/*      */   }
/*      */ 
/*      */   public boolean clearUnreadByReceipt(int conversationType, String targetId, long timestamp)
/*      */   {
/* 1775 */     return this.mClient.clearUnreadByReceipt(conversationType, targetId, timestamp);
/*      */   }
/*      */ 
/*      */   public long getSendTimeByMessageId(int messageId)
/*      */   {
/* 1780 */     return this.mClient.getSendTimeByMessageId(messageId);
/*      */   }
/*      */ 
/*      */   public void getVoIPKey(int engineType, String channelName, String extra, IStringCallback callback)
/*      */   {
/* 1785 */     this.mClient.getVoIPKey(engineType, channelName, extra, new NativeClient.IResultCallback(callback)
/*      */     {
/*      */       public void onSuccess(String s) {
/* 1788 */         if (this.val$callback == null) {
/* 1789 */           return;
/*      */         }
/* 1791 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(s)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1795 */               LibHandlerStub.37.this.val$callback.onComplete(this.val$s);
/*      */             } catch (java.lang.NullPointerException e) {
/* 1797 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(int code)
/*      */       {
/* 1806 */         if (this.val$callback == null)
/* 1807 */           return;
/* 1808 */         LibHandlerStub.this.mCallbackHandler.post(new Runnable(code)
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 1812 */               LibHandlerStub.37.this.val$callback.onFailure(this.val$code);
/*      */             } catch (RemoteException e) {
/* 1814 */               e.printStackTrace();
/*      */             }
/*      */           } } );
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public String getVoIPCallInfo() {
/* 1824 */     return this.mClient.getVoIPCallInfo();
/*      */   }
/*      */ 
/*      */   public void setServerInfo(String naviServer, String fileServer)
/*      */   {
/* 1829 */     this.mClient.setServerInfo(naviServer, fileServer);
/*      */   }
/*      */ 
/*      */   public long getNaviCachedTime()
/*      */   {
/* 1834 */     return NavigationClient.getInstance().getLastCachedTime();
/*      */   }
/*      */ 
/*      */   public String getCMPServer()
/*      */   {
/* 1839 */     return NavigationClient.getInstance().getCMPServer();
/*      */   }
/*      */ 
/*      */   public void getPCAuthConfig(IStringCallback callback)
/*      */   {
/* 1849 */     this.mClient.getPCAuthConfig(new NativeClient.IResultCallback(callback)
/*      */     {
/*      */       public void onSuccess(String str) {
/*      */         try {
/* 1853 */           this.val$callback.onComplete(str);
/*      */         } catch (RemoteException e) {
/* 1855 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void onError(int code)
/*      */       {
/*      */         try {
/* 1862 */           this.val$callback.onFailure(code);
/*      */         } catch (RemoteException e) {
/* 1864 */           e.printStackTrace();
/*      */         }
/*      */       } } );
/*      */   }
/*      */ 
/*      */   public boolean setMessageContent(int messageId, byte[] messageContent, String objectName) {
/* 1871 */     return this.mClient.setMessageContent(messageId, messageContent, objectName);
/*      */   }
/*      */ 
/*      */   public List<Message> getUnreadMentionedMessages(int conversationType, String targetId)
/*      */   {
/* 1879 */     List list = this.mClient.getUnreadMentionedMessages(Conversation.ConversationType.setValue(conversationType), targetId);
/*      */ 
/* 1881 */     if ((list == null) || (list.size() == 0)) {
/* 1882 */       return null;
/*      */     }
/* 1884 */     return list;
/*      */   }
/*      */ 
/*      */   public void sendMediaMessage(Message message, String pushContent, String pushData, ISendMediaMessageCallback sendMediaMessageCallback) throws RemoteException
/*      */   {
/* 1889 */     this.mClient.sendMediaMessage(message, pushContent, pushData, new NativeClient.ISendMediaMessageCallback(sendMediaMessageCallback)
/*      */     {
/*      */       public void onAttached(Message message) {
/*      */         try {
/* 1893 */           if (this.val$sendMediaMessageCallback != null)
/* 1894 */             this.val$sendMediaMessageCallback.onAttached(message);
/*      */         }
/*      */         catch (RemoteException e) {
/* 1897 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void onSuccess(Message message)
/*      */       {
/*      */         try {
/* 1904 */           if (this.val$sendMediaMessageCallback != null)
/* 1905 */             this.val$sendMediaMessageCallback.onSuccess(message);
/*      */         }
/*      */         catch (RemoteException e) {
/* 1908 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void onProgress(Message message, int progress)
/*      */       {
/*      */         try {
/* 1915 */           if (this.val$sendMediaMessageCallback != null)
/* 1916 */             this.val$sendMediaMessageCallback.onProgress(message, progress);
/*      */         }
/*      */         catch (RemoteException e) {
/* 1919 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void onError(Message message, int code)
/*      */       {
/*      */         try {
/* 1926 */           if (this.val$sendMediaMessageCallback != null)
/* 1927 */             this.val$sendMediaMessageCallback.onError(message, code);
/*      */         }
/*      */         catch (RemoteException e) {
/* 1930 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public boolean updateReadReceiptRequestInfo(String msgUId, String info) {
/* 1938 */     if ((msgUId == null) || (info == null)) {
/* 1939 */       RLog.d("LibHandlerStub", "updateReadReceiptRequestInfo parameter error");
/* 1940 */       return false;
/*      */     }
/* 1942 */     return this.mClient.updateReadReceiptRequestInfo(msgUId, info);
/*      */   }
/*      */ 
/*      */   public void registerCmdMsgType(List<String> msgTypeList)
/*      */   {
/* 1947 */     if ((msgTypeList == null) || (msgTypeList.size() == 0)) {
/* 1948 */       RLog.d("LibHandlerStub", "registerCmdMsgType parameter error");
/* 1949 */       return;
/*      */     }
/* 1951 */     this.mClient.registerCmdMsgType(msgTypeList);
/*      */   }
/*      */ 
/*      */   private class OperationCallback
/*      */     implements NativeClient.OperationCallback
/*      */   {
/*      */     IOperationCallback callback;
/*      */ 
/*      */     public OperationCallback(IOperationCallback callback)
/*      */     {
/* 1642 */       this.callback = callback;
/*      */     }
/*      */ 
/*      */     public void onSuccess()
/*      */     {
/* 1647 */       if (this.callback == null) {
/* 1648 */         return;
/*      */       }
/* 1650 */       LibHandlerStub.this.mCallbackHandler.post(new Runnable()
/*      */       {
/*      */         public void run() {
/*      */           try {
/* 1654 */             LibHandlerStub.OperationCallback.this.callback.onComplete();
/*      */           } catch (RemoteException e) {
/* 1656 */             e.printStackTrace();
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */ 
/*      */     public void onError(int code) {
/* 1664 */       if (this.callback == null) {
/* 1665 */         return;
/*      */       }
/* 1667 */       LibHandlerStub.this.mCallbackHandler.post(new Runnable(code)
/*      */       {
/*      */         public void run() {
/*      */           try {
/* 1671 */             LibHandlerStub.OperationCallback.this.callback.onFailure(this.val$code);
/*      */           } catch (RemoteException e) {
/* 1673 */             e.printStackTrace();
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.LibHandlerStub
 * JD-Core Version:    0.6.0
 */
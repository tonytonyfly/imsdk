/*      */ package io.rong.imlib;
/*      */ 
/*      */ import android.content.Context;
/*      */ import android.net.ConnectivityManager;
/*      */ import android.net.NetworkInfo;
/*      */ import android.net.Uri;
/*      */ import android.os.Build;
/*      */ import android.os.Build.VERSION;
/*      */ import android.telephony.TelephonyManager;
/*      */ import android.text.TextUtils;
/*      */ import android.util.Base64;
/*      */ import io.rong.common.FileUtils;
/*      */ import io.rong.common.RFLog;
/*      */ import io.rong.common.RLog;
/*      */ import io.rong.imlib.filetransfer.Configuration;
/*      */ import io.rong.imlib.filetransfer.Configuration.Builder;
/*      */ import io.rong.imlib.filetransfer.FileTransferClient;
/*      */ import io.rong.imlib.filetransfer.FtConst.MimeType;
/*      */ import io.rong.imlib.filetransfer.FtConst.ServiceType;
/*      */ import io.rong.imlib.filetransfer.FtUtilities;
/*      */ import io.rong.imlib.filetransfer.RequestCallBack;
/*      */ import io.rong.imlib.filetransfer.RequestOption;
/*      */ import io.rong.imlib.location.RealTimeLocationConstant.RealTimeLocationErrorCode;
/*      */ import io.rong.imlib.location.RealTimeLocationConstant.RealTimeLocationStatus;
/*      */ import io.rong.imlib.location.RealTimeLocationManager;
/*      */ import io.rong.imlib.model.ChatRoomInfo;
/*      */ import io.rong.imlib.model.ChatRoomMemberInfo;
/*      */ import io.rong.imlib.model.Conversation;
/*      */ import io.rong.imlib.model.Conversation.ConversationNotificationStatus;
/*      */ import io.rong.imlib.model.Conversation.ConversationType;
/*      */ import io.rong.imlib.model.Discussion;
/*      */ import io.rong.imlib.model.Group;
/*      */ import io.rong.imlib.model.Message;
/*      */ import io.rong.imlib.model.Message.MessageDirection;
/*      */ import io.rong.imlib.model.Message.ReceivedStatus;
/*      */ import io.rong.imlib.model.Message.SentStatus;
/*      */ import io.rong.imlib.model.MessageContent;
/*      */ import io.rong.imlib.model.PublicServiceProfile;
/*      */ import io.rong.imlib.model.PublicServiceProfileList;
/*      */ import io.rong.imlib.model.UnknownMessage;
/*      */ import io.rong.imlib.model.UserData;
/*      */ import io.rong.imlib.model.UserData.AccountInfo;
/*      */ import io.rong.imlib.model.UserData.ClientInfo;
/*      */ import io.rong.imlib.model.UserData.ContactInfo;
/*      */ import io.rong.imlib.model.UserData.PersonalInfo;
/*      */ import io.rong.imlib.navigation.NavigationCallback;
/*      */ import io.rong.imlib.navigation.NavigationClient;
/*      */ import io.rong.imlib.navigation.NavigationObserver;
/*      */ import io.rong.imlib.navigation.PCAuthConfig;
/*      */ import io.rong.message.DiscussionNotificationMessage;
/*      */ import io.rong.message.FileMessage;
/*      */ import io.rong.message.IHandleMessageListener;
/*      */ import io.rong.message.ImageMessage;
/*      */ import io.rong.message.LocationMessage;
/*      */ import io.rong.message.MediaMessageContent;
/*      */ import io.rong.message.MessageHandler;
/*      */ import io.rong.message.TextMessage;
/*      */ import java.io.File;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.security.MessageDigest;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Timer;
/*      */ import java.util.TimerTask;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.json.JSONException;
/*      */ import org.json.JSONObject;
/*      */ 
/*      */ public final class NativeClient
/*      */ {
/*      */   private static final String TAG = "NativeClient";
/*   73 */   private static NativeClient client = null;
/*   74 */   private Context mContext = null;
/*      */   protected static NativeObject nativeObj;
/*      */   private String mFileServer;
/*      */   private String mNaviServer;
/*      */   private String appKey;
/*      */   private String token;
/*      */   private String deviceId;
/*      */   private String currentUserId;
/*   83 */   private static HashMap<String, Constructor<? extends MessageContent>> constructorMap = new HashMap();
/*   84 */   private static HashMap<String, Constructor<? extends MessageHandler>> messageHandlerMap = new HashMap();
/*      */   private OnReceiveMessageListenerEx mReceiveMessageListenerEx;
/*      */   private OnReceiveMessageListener mReceiveMessageListener;
/*      */   private volatile RealTimeLocationManager mRealTimeLocationManager;
/*      */   Timer timer;
/*      */ 
/*      */   private MessageContent renderMessageContent(String objectName, byte[] content, Message message)
/*      */   {
/*   90 */     Constructor constructor = (Constructor)constructorMap.get(objectName);
/*   91 */     MessageContent result = null;
/*      */ 
/*   93 */     if (constructor == null) {
/*   94 */       return new UnknownMessage(content);
/*      */     }
/*      */     try
/*      */     {
/*   98 */       result = (MessageContent)constructor.newInstance(new Object[] { content });
/*   99 */       Constructor handlerConstructor = (Constructor)messageHandlerMap.get(objectName);
/*  100 */       if (handlerConstructor != null) {
/*  101 */         MessageHandler handler = (MessageHandler)handlerConstructor.newInstance(new Object[] { this.mContext });
/*  102 */         handler.decodeMessage(message, result);
/*      */       } else {
/*  104 */         RLog.e("NativeClient", "renderMessageContent 该消息未注册，请调用registerMessageType方法注册。");
/*      */       }
/*      */     } catch (Exception e) {
/*  107 */       e.printStackTrace();
/*      */     }
/*      */ 
/*  110 */     return result;
/*      */   }
/*      */ 
/*      */   public static NativeClient getInstance()
/*      */   {
/*  118 */     if (client == null)
/*  119 */       client = new NativeClient();
/*  120 */     return client;
/*      */   }
/*      */ 
/*      */   public void init(Context context, String appKey, String deviceId)
/*      */   {
/*  129 */     this.mContext = context.getApplicationContext();
/*  130 */     this.appKey = appKey;
/*  131 */     this.deviceId = deviceId;
/*  132 */     nativeObj = new NativeObject();
/*      */ 
/*  134 */     int result = nativeObj.InitClient(appKey, context.getPackageName(), deviceId, context.getFilesDir().getPath(), FileUtils.getCachePath(context, "ronglog"));
/*  135 */     RLog.d("NativeClient", new StringBuilder().append("init result = ").append(result).toString());
/*      */ 
/*  137 */     this.mRealTimeLocationManager = RealTimeLocationManager.init(context);
/*      */ 
/*  139 */     Configuration.Builder builder = new Configuration.Builder();
/*  140 */     Configuration configuration = builder.serverType(FtConst.ServiceType.QI_NIU).build();
/*  141 */     FileTransferClient.init(configuration);
/*      */   }
/*      */ 
/*      */   public static void registerMessageType(Class<? extends MessageContent> type)
/*      */     throws AnnotationNotFoundException
/*      */   {
/*  151 */     if (nativeObj == null) {
/*  152 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  154 */     if (type == null) {
/*  155 */       throw new IllegalArgumentException("MessageContent 为空！");
/*      */     }
/*  157 */     MessageTag tag = (MessageTag)type.getAnnotation(MessageTag.class);
/*      */ 
/*  159 */     if (tag != null) {
/*  160 */       String objName = tag.value();
/*  161 */       int flag = tag.flag();
/*      */       try
/*      */       {
/*  164 */         Constructor constructor = type.getDeclaredConstructor(new Class[] { [B.class });
/*  165 */         Constructor handlerConstructor = tag.messageHandler().getConstructor(new Class[] { Context.class });
/*  166 */         constructorMap.put(objName, constructor);
/*  167 */         messageHandlerMap.put(objName, handlerConstructor);
/*  168 */         nativeObj.RegisterMessageType(objName, flag);
/*      */       } catch (NoSuchMethodException e) {
/*  170 */         throw new AnnotationNotFoundException();
/*      */       }
/*      */     }
/*      */     else {
/*  174 */       throw new AnnotationNotFoundException();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void connect(String token, IResultCallback<String> callback)
/*      */     throws Exception
/*      */   {
/*  186 */     RLog.i("NativeClient", "connect");
/*  187 */     this.token = token;
/*  188 */     setEnvInfo(this.mContext);
/*  189 */     RFLog.uploadIfNeed(this.mContext);
/*      */ 
/*  191 */     NavigationClient.getInstance().addObserver(new NavigationObserver(token, callback)
/*      */     {
/*      */       public void onSuccess(String cmp) {
/*  194 */         RLog.i("NativeClient", "connect onSuccess - " + cmp);
/*  195 */         String ip = cmp.substring(0, cmp.indexOf(":"));
/*  196 */         String port = cmp.substring(cmp.indexOf(":") + 1);
/*  197 */         NativeClient.this.internalConnect(this.val$token, ip, Integer.parseInt(port), this.val$callback);
/*      */       }
/*      */ 
/*      */       public void onError(String cmp, int errorCode)
/*      */       {
/*  202 */         RLog.e("NativeClient", "connect onError - " + errorCode);
/*      */ 
/*  204 */         if (!TextUtils.isEmpty(cmp)) {
/*  205 */           String ip = cmp.substring(0, cmp.indexOf(":"));
/*  206 */           String port = cmp.substring(cmp.indexOf(":") + 1);
/*  207 */           NativeClient.this.internalConnect(this.val$token, ip, Integer.parseInt(port), this.val$callback);
/*      */         }
/*  209 */         else if (this.val$callback != null) {
/*  210 */           this.val$callback.onError(errorCode);
/*      */         }
/*      */ 
/*  213 */         NavigationClient.getInstance().clearObserver();
/*      */       }
/*      */ 
/*      */       public void onReconnect(String cmp, NavigationCallback navigationCallback)
/*      */       {
/*  218 */         RLog.e("NativeClient", "connect onReconnect - " + cmp);
/*      */ 
/*  220 */         String ip = cmp.substring(0, cmp.indexOf(":"));
/*  221 */         String port = cmp.substring(cmp.indexOf(":") + 1);
/*  222 */         boolean mpOpened = NavigationClient.getInstance().isMPOpened(NativeClient.this.mContext);
/*  223 */         NativeClient.nativeObj.Connect(this.val$token, ip, Integer.parseInt(port), new NativeObject.ConnectAckCallback(navigationCallback)
/*      */         {
/*      */           public void operationComplete(int status, String userId) {
/*  226 */             RLog.d("NativeClient", "reconnect operationComplete : state = " + status);
/*  227 */             if (this.val$navigationCallback != null)
/*  228 */               if (status == 0)
/*  229 */                 this.val$navigationCallback.onSuccess();
/*      */               else
/*  231 */                 this.val$navigationCallback.onError();
/*      */           }
/*      */         }
/*      */         , mpOpened);
/*      */ 
/*  235 */         NavigationClient.getInstance().clearObserver();
/*      */       }
/*      */     });
/*  238 */     NavigationClient.getInstance().getCMPServer(this.mContext, this.appKey, token);
/*      */   }
/*      */ 
/*      */   private void internalConnect(String token, String ip, int port, IResultCallback<String> callback) {
/*  242 */     boolean mpOpened = NavigationClient.getInstance().isMPOpened(this.mContext);
/*  243 */     nativeObj.Connect(token, ip, port, new NativeObject.ConnectAckCallback(callback, token)
/*      */     {
/*      */       public void operationComplete(int status, String userId) {
/*  246 */         RLog.d("NativeClient", "connect operationComplete : state = " + status);
/*  247 */         if ((status == 31006) || (status == 33003)) {
/*  248 */           NavigationClient.getInstance().clearCache(NativeClient.this.mContext);
/*  249 */           RLog.e("NativeClient", "internalConnect status = " + status);
/*      */         }
/*      */ 
/*  252 */         if ((status == 30001) || (status == 30002) || (status == 31000) || (status == 30014) || (status == 30010) || (status == 30011))
/*      */         {
/*  255 */           NavigationClient.getInstance().updateCacheTime(NativeClient.this.mContext);
/*  256 */           RLog.e("NativeClient", "internalConnect status = " + status);
/*      */         }
/*      */ 
/*  259 */         if (status == 0) {
/*  260 */           NativeClient.access$202(NativeClient.this, userId);
/*  261 */           if (this.val$callback != null) {
/*  262 */             this.val$callback.onSuccess(userId);
/*      */           }
/*  264 */           if (!NavigationClient.getInstance().needUpdateCMP(NativeClient.this.mContext, NativeClient.this.appKey, this.val$token))
/*  265 */             NavigationClient.getInstance().clearObserver();
/*      */         }
/*      */         else {
/*  268 */           RLog.e("NativeClient", "internalConnect status = " + status);
/*  269 */           NativeClient.access$202(NativeClient.this, NativeClient.nativeObj.GetUserIdByToken(this.val$token));
/*  270 */           if (this.val$callback != null) {
/*  271 */             this.val$callback.onError(status);
/*      */           }
/*  273 */           NavigationClient.getInstance().clearObserver();
/*      */         }
/*      */       }
/*      */     }
/*      */     , mpOpened);
/*      */   }
/*      */ 
/*      */   public String getCurrentUserId()
/*      */   {
/*  285 */     return this.currentUserId;
/*      */   }
/*      */ 
/*      */   public void disconnect()
/*      */   {
/*  293 */     disconnect(true);
/*      */   }
/*      */ 
/*      */   public void disconnect(boolean isReceivePush)
/*      */   {
/*  302 */     if (nativeObj == null) {
/*  303 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  305 */     RLog.i("NativeClient", new StringBuilder().append("disconnect isReceivePush : ").append(isReceivePush).toString());
/*      */ 
/*  307 */     nativeObj.Disconnect(isReceivePush ? 2 : 4);
/*      */   }
/*      */ 
/*      */   public List<Conversation> getConversationList()
/*      */   {
/*  320 */     int[] conversationTypes = { Conversation.ConversationType.PRIVATE.getValue(), Conversation.ConversationType.DISCUSSION.getValue(), Conversation.ConversationType.GROUP.getValue(), Conversation.ConversationType.SYSTEM.getValue() };
/*      */ 
/*  324 */     if (nativeObj == null) {
/*  325 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  327 */     return getConversationList(conversationTypes);
/*      */   }
/*      */ 
/*      */   public List<Conversation> getGroupConversationList()
/*      */   {
/*  339 */     int[] conversationTypes = { Conversation.ConversationType.GROUP.getValue() };
/*      */ 
/*  341 */     if (nativeObj == null) {
/*  342 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  344 */     return getConversationList(conversationTypes);
/*      */   }
/*      */ 
/*      */   public List<Conversation> getConversationList(int[] conversationTypeValues)
/*      */   {
/*  358 */     NativeObject.Conversation[] conversations = null;
/*      */     try {
/*  360 */       conversations = nativeObj.GetConversationListEx(conversationTypeValues);
/*      */     } catch (Exception e) {
/*  362 */       e.printStackTrace();
/*      */     }
/*  364 */     if (conversations == null) {
/*  365 */       return null;
/*      */     }
/*  367 */     List result = new ArrayList();
/*      */ 
/*  369 */     for (NativeObject.Conversation item : conversations) {
/*  370 */       result.add(renderConversationFromNative(item));
/*      */     }
/*      */ 
/*  373 */     return result;
/*      */   }
/*      */ 
/*      */   private Conversation renderConversationFromNative(NativeObject.Conversation conversation)
/*      */   {
/*  378 */     Conversation result = new Conversation();
/*  379 */     result.setTargetId(conversation.getTargetId());
/*  380 */     result.setLatestMessageId(conversation.getMessageId());
/*  381 */     result.setConversationTitle(conversation.getConversationTitle());
/*  382 */     result.setUnreadMessageCount(conversation.getUnreadMessageCount());
/*  383 */     result.setConversationType(Conversation.ConversationType.setValue(conversation.getConversationType()));
/*  384 */     result.setTop(conversation.isTop());
/*  385 */     result.setObjectName(conversation.getObjectName());
/*  386 */     if (conversation.getMessageId() > 0) {
/*  387 */       Message message = new Message();
/*  388 */       message.setMessageId(conversation.getMessageId());
/*  389 */       message.setSenderUserId(conversation.getSenderUserId());
/*  390 */       result.setLatestMessage(renderMessageContent(conversation.getObjectName(), conversation.getContent(), message));
/*      */     }
/*  392 */     result.setReceivedStatus(new Message.ReceivedStatus(conversation.getReadStatus()));
/*  393 */     result.setReceivedTime(conversation.getReceivedTime());
/*  394 */     result.setSentTime(conversation.getSentTime());
/*  395 */     result.setSenderUserId(conversation.getSenderUserId());
/*  396 */     result.setSentStatus(Message.SentStatus.setValue(conversation.getSentStatus()));
/*  397 */     result.setSenderUserName(conversation.getSenderName());
/*  398 */     result.setDraft(conversation.getDraft());
/*  399 */     result.setPortraitUrl(conversation.getPortraitUrl());
/*  400 */     result.setNotificationStatus(conversation.isBlockPush() ? Conversation.ConversationNotificationStatus.DO_NOT_DISTURB : Conversation.ConversationNotificationStatus.NOTIFY);
/*  401 */     result.setMentionedCount(conversation.getMentionCount());
/*  402 */     return result;
/*      */   }
/*      */ 
/*      */   private Conversation renderConversationFromJson(JSONObject jsonObj) {
/*  406 */     Conversation conversation = new Conversation();
/*  407 */     conversation.setTargetId(jsonObj.optString("target_id"));
/*  408 */     conversation.setLatestMessageId(jsonObj.optInt("last_message_id"));
/*  409 */     conversation.setConversationTitle(jsonObj.optString("conversation_title"));
/*  410 */     conversation.setUnreadMessageCount(jsonObj.optInt("unread_count"));
/*  411 */     conversation.setConversationType(Conversation.ConversationType.setValue(jsonObj.optInt("conversation_category")));
/*  412 */     conversation.setTop(jsonObj.optInt("is_top") == 1);
/*  413 */     conversation.setObjectName(jsonObj.optString("object_name"));
/*      */ 
/*  415 */     if (conversation.getLatestMessageId() > 0) {
/*  416 */       Message message = new Message();
/*  417 */       message.setMessageId(conversation.getLatestMessageId());
/*  418 */       message.setSenderUserId(conversation.getSenderUserId());
/*  419 */       MessageContent content = renderMessageContent(conversation.getObjectName(), jsonObj.optString("content").getBytes(), message);
/*  420 */       conversation.setLatestMessage(content);
/*      */     }
/*  422 */     conversation.setReceivedStatus(new Message.ReceivedStatus(jsonObj.optInt("read_status")));
/*  423 */     conversation.setReceivedTime(jsonObj.optLong("receive_time"));
/*  424 */     conversation.setSentTime(jsonObj.optLong("send_time"));
/*  425 */     conversation.setSentStatus(Message.SentStatus.setValue(jsonObj.optInt("send_status")));
/*  426 */     conversation.setSenderUserId(jsonObj.optString("sender_user_id"));
/*  427 */     conversation.setSenderUserName(jsonObj.optString("sender_user_name"));
/*  428 */     conversation.setDraft(jsonObj.optString("draft_message"));
/*  429 */     conversation.setNotificationStatus(jsonObj.optInt("block_push") == 100 ? Conversation.ConversationNotificationStatus.DO_NOT_DISTURB : Conversation.ConversationNotificationStatus.NOTIFY);
/*      */ 
/*  432 */     return conversation;
/*      */   }
/*      */ 
/*      */   public Conversation getConversation(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/*  443 */     if (nativeObj == null) {
/*  444 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  446 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId))) {
/*  447 */       throw new IllegalArgumentException("ConversationType 和 TargetId 参数异常");
/*      */     }
/*  449 */     NativeObject.Conversation conversation = nativeObj.GetConversationEx(targetId, conversationType.getValue());
/*      */ 
/*  451 */     if (conversation == null) {
/*  452 */       return null;
/*      */     }
/*  454 */     Conversation c = renderConversationFromNative(conversation);
/*  455 */     c.setConversationType(conversationType);
/*  456 */     return c;
/*      */   }
/*      */ 
/*      */   public boolean removeConversation(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/*  469 */     if (nativeObj == null) {
/*  470 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  472 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId)) || (TextUtils.isEmpty(targetId.trim()))) {
/*  473 */       throw new IllegalArgumentException("ConversationType 和 TargetId 参数异常");
/*      */     }
/*  475 */     targetId = targetId.trim();
/*  476 */     return nativeObj.RemoveConversation(conversationType.getValue(), targetId);
/*      */   }
/*      */ 
/*      */   public boolean setConversationToTop(Conversation.ConversationType conversationType, String targetId, boolean isTop)
/*      */   {
/*  488 */     if (nativeObj == null) {
/*  489 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  491 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId))) {
/*  492 */       throw new IllegalArgumentException("ConversationType 或 TargetId 参数异常");
/*      */     }
/*  494 */     return nativeObj.SetIsTop(conversationType.getValue(), targetId, isTop);
/*      */   }
/*      */ 
/*      */   public int getTotalUnreadCount()
/*      */   {
/*  503 */     if (nativeObj == null) {
/*  504 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  506 */     return nativeObj.GetTotalUnreadCount();
/*      */   }
/*      */ 
/*      */   public int getUnreadCount(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/*  517 */     if (nativeObj == null) {
/*  518 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  520 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/*  521 */       throw new IllegalArgumentException("ConversationType 或 TargetId 参数异常");
/*      */     }
/*  523 */     return nativeObj.GetUnreadCount(targetId, conversationType.getValue());
/*      */   }
/*      */ 
/*      */   public int getUnreadCount(Conversation.ConversationType[] conversationTypes)
/*      */   {
/*  534 */     if (nativeObj == null) {
/*  535 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  537 */     if ((conversationTypes == null) || (conversationTypes.length == 0)) {
/*  538 */       throw new IllegalArgumentException("ConversationTypes 参数异常。");
/*      */     }
/*      */ 
/*  541 */     int[] conversationTypeValues = new int[conversationTypes.length];
/*      */ 
/*  543 */     int i = 0;
/*  544 */     for (Conversation.ConversationType conversationType : conversationTypes) {
/*  545 */       conversationTypeValues[i] = conversationType.getValue();
/*  546 */       i++;
/*      */     }
/*      */ 
/*  549 */     return nativeObj.GetCateUnreadCount(conversationTypeValues);
/*      */   }
/*      */ 
/*      */   public List<Message> getLatestMessages(Conversation.ConversationType conversationType, String targetId, int count)
/*      */   {
/*  561 */     if (nativeObj == null) {
/*  562 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  564 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId))) {
/*  565 */       throw new IllegalArgumentException("ConversationTypes 或 targetId 参数异常。");
/*      */     }
/*  567 */     targetId = targetId.trim();
/*  568 */     return getHistoryMessages(conversationType, targetId, -1, count);
/*      */   }
/*      */ 
/*      */   public List<Message> getHistoryMessages(Conversation.ConversationType conversationType, String targetId, int oldestMessageId, int count)
/*      */   {
/*  582 */     if (nativeObj == null) {
/*  583 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  585 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId))) {
/*  586 */       throw new IllegalArgumentException("ConversationTypes 或 targetId 参数异常。");
/*      */     }
/*  588 */     targetId = targetId.trim();
/*      */ 
/*  590 */     NativeObject.Message[] array = nativeObj.GetHistoryMessagesEx(targetId, conversationType.getValue(), "", oldestMessageId, count, true);
/*      */ 
/*  592 */     List list = new ArrayList();
/*      */ 
/*  594 */     if (array == null) {
/*  595 */       return list;
/*      */     }
/*  597 */     for (NativeObject.Message item : array) {
/*  598 */       Message msg = new Message(item);
/*  599 */       msg.setContent(renderMessageContent(item.getObjectName(), item.getContent(), msg));
/*  600 */       list.add(msg);
/*      */     }
/*      */ 
/*  603 */     return list;
/*      */   }
/*      */ 
/*      */   public List<Message> getHistoryMessages(Conversation.ConversationType conversationType, String targetId, String objectName, int oldestMessageId, int count)
/*      */   {
/*  617 */     if (nativeObj == null) {
/*  618 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  620 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId)) || (TextUtils.isEmpty(objectName))) {
/*  621 */       throw new IllegalArgumentException("ConversationTypes, objectName 或 targetId 参数异常。");
/*      */     }
/*  623 */     targetId = targetId.trim();
/*      */ 
/*  625 */     NativeObject.Message[] messages = nativeObj.GetHistoryMessagesEx(targetId, conversationType.getValue(), objectName, oldestMessageId, count, true);
/*  626 */     List list = new ArrayList();
/*      */ 
/*  628 */     if (messages == null) {
/*  629 */       return null;
/*      */     }
/*  631 */     for (NativeObject.Message item : messages) {
/*  632 */       Message msg = new Message(item);
/*  633 */       msg.setContent(renderMessageContent(item.getObjectName(), item.getContent(), msg));
/*  634 */       list.add(msg);
/*      */     }
/*      */ 
/*  637 */     return list;
/*      */   }
/*      */ 
/*      */   public List<Message> getHistoryMessages(Conversation.ConversationType conversationType, String targetId, String objectName, int oldestMessageId, int count, boolean direction)
/*      */   {
/*  656 */     if (nativeObj == null) {
/*  657 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  659 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId)) || (TextUtils.isEmpty(objectName))) {
/*  660 */       throw new IllegalArgumentException("ConversationTypes, objectName 或 targetId 参数异常。");
/*      */     }
/*  662 */     targetId = targetId.trim();
/*      */ 
/*  664 */     NativeObject.Message[] messages = nativeObj.GetHistoryMessagesEx(targetId, conversationType.getValue(), objectName, oldestMessageId, count, direction);
/*  665 */     List list = new ArrayList();
/*      */ 
/*  667 */     if (messages == null) {
/*  668 */       return null;
/*      */     }
/*  670 */     for (NativeObject.Message item : messages) {
/*  671 */       Message msg = new Message(item);
/*  672 */       msg.setContent(renderMessageContent(item.getObjectName(), item.getContent(), msg));
/*  673 */       list.add(msg);
/*      */     }
/*      */ 
/*  676 */     return list;
/*      */   }
/*      */ 
/*      */   public void getRemoteHistoryMessages(Conversation.ConversationType conversationType, String targetId, long dataTime, int count, IResultCallback<List<Message>> callback)
/*      */   {
/*  689 */     RLog.i("NativeClient", "getRemoteHistoryMessages call");
/*      */ 
/*  691 */     if (nativeObj == null) {
/*  692 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  694 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId)) || (callback == null)) {
/*  695 */       throw new IllegalArgumentException("ConversationTypes，callback 或 targetId 参数异常。");
/*      */     }
/*  697 */     targetId = targetId.trim();
/*      */ 
/*  699 */     if (NavigationClient.getInstance().isGetRemoteEnabled(this.mContext))
/*  700 */       nativeObj.LoadHistoryMessage(targetId, conversationType.getValue(), dataTime, count, new NativeObject.HistoryMessageListener(callback)
/*      */       {
/*      */         public void onReceived(NativeObject.Message[] messages) {
/*  703 */           List list = new ArrayList();
/*  704 */           if ((messages != null) && (messages.length > 0)) {
/*  705 */             for (NativeObject.Message item : messages) {
/*  706 */               Message msg = new Message(item);
/*  707 */               msg.setContent(NativeClient.this.renderMessageContent(item.getObjectName(), item.getContent(), msg));
/*  708 */               list.add(msg);
/*      */             }
/*      */           }
/*  711 */           this.val$callback.onSuccess(list);
/*      */         }
/*      */ 
/*      */         public void onError(int status)
/*      */         {
/*  716 */           this.val$callback.onError(status);
/*      */         }
/*      */       });
/*  720 */     else callback.onError(33007);
/*      */   }
/*      */ 
/*      */   public boolean deleteMessages(int[] messageIds)
/*      */   {
/*  732 */     if (nativeObj == null) {
/*  733 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  735 */     if ((messageIds == null) || (messageIds.length == 0)) {
/*  736 */       throw new IllegalArgumentException("MessageIds 参数异常。");
/*      */     }
/*  738 */     return nativeObj.DeleteMessages(messageIds);
/*      */   }
/*      */ 
/*      */   public boolean deleteMessage(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/*  749 */     return nativeObj.ClearMessages(conversationType.getValue(), targetId, true);
/*      */   }
/*      */ 
/*      */   public boolean clearMessages(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/*  760 */     if (nativeObj == null) {
/*  761 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  763 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId))) {
/*  764 */       throw new IllegalArgumentException("conversationType 或 targetId 参数异常。");
/*      */     }
/*  766 */     return nativeObj.ClearMessages(conversationType.getValue(), targetId, false);
/*      */   }
/*      */ 
/*      */   public boolean clearMessagesUnreadStatus(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/*  777 */     if (nativeObj == null) {
/*  778 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  780 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId))) {
/*  781 */       throw new IllegalArgumentException("conversationType 或 targetId 参数异常。");
/*      */     }
/*  783 */     return nativeObj.ClearUnread(conversationType.getValue(), targetId);
/*      */   }
/*      */ 
/*      */   public boolean setMessageExtra(int messageId, String value)
/*      */   {
/*  794 */     if (nativeObj == null) {
/*  795 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  797 */     if (messageId == 0) {
/*  798 */       throw new IllegalArgumentException("messageId 参数异常。");
/*      */     }
/*  800 */     return nativeObj.SetMessageExtra(messageId, value);
/*      */   }
/*      */ 
/*      */   public boolean setMessageReceivedStatus(int messageId, Message.ReceivedStatus receivedStatus)
/*      */   {
/*  811 */     if (nativeObj == null) {
/*  812 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  814 */     if ((receivedStatus == null) || (messageId == 0)) {
/*  815 */       throw new IllegalArgumentException("receivedStatus 或 messageId 参数异常。");
/*      */     }
/*  817 */     return nativeObj.SetReadStatus(messageId, receivedStatus.getFlag());
/*      */   }
/*      */ 
/*      */   public boolean setMessageSentStatus(int messageId, Message.SentStatus sentStatus)
/*      */   {
/*  829 */     if (nativeObj == null) {
/*  830 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  832 */     if ((sentStatus == null) || (messageId == 0)) {
/*  833 */       throw new IllegalArgumentException("sentStatus 或 messageId 参数异常。");
/*      */     }
/*  835 */     return nativeObj.SetSendStatus(messageId, sentStatus.getValue());
/*      */   }
/*      */ 
/*      */   public String getTextMessageDraft(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/*  847 */     if (nativeObj == null) {
/*  848 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  850 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId))) {
/*  851 */       throw new IllegalArgumentException("conversationType 或 targetId 参数异常。");
/*      */     }
/*  853 */     return nativeObj.GetTextMessageDraft(conversationType.getValue(), targetId);
/*      */   }
/*      */ 
/*      */   public boolean saveTextMessageDraft(Conversation.ConversationType conversationType, String targetId, String content)
/*      */   {
/*  865 */     if (nativeObj == null) {
/*  866 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  868 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId))) {
/*  869 */       throw new IllegalArgumentException("conversationType 或 targetId 参数异常。");
/*      */     }
/*  871 */     return nativeObj.SetTextMessageDraft(conversationType.getValue(), targetId, content);
/*      */   }
/*      */ 
/*      */   public boolean clearTextMessageDraft(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/*  882 */     if (nativeObj == null) {
/*  883 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  885 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId))) {
/*  886 */       throw new IllegalArgumentException("conversationType 或 targetId 参数异常。");
/*      */     }
/*  888 */     String draft = getTextMessageDraft(conversationType, targetId);
/*      */ 
/*  890 */     if (!TextUtils.isEmpty(draft)) {
/*  891 */       return saveTextMessageDraft(conversationType, targetId, "");
/*      */     }
/*  893 */     return true;
/*      */   }
/*      */ 
/*      */   public void getDiscussion(String discussionId, IResultCallback<Discussion> callback)
/*      */   {
/*  904 */     if (nativeObj == null) {
/*  905 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  907 */     if (TextUtils.isEmpty(discussionId)) {
/*  908 */       throw new IllegalArgumentException(" discussionId 参数异常。");
/*      */     }
/*  910 */     NativeObject.DiscussionInfo discussionInfo = nativeObj.GetDiscussionInfoSync(discussionId);
/*  911 */     if (discussionInfo != null) {
/*  912 */       Discussion discussion = new Discussion(discussionInfo);
/*  913 */       if ((discussion.getMemberIdList() == null) || (discussion.getMemberIdList().size() == 0)) {
/*  914 */         nativeObj.GetDiscussionInfo(discussionId, new NativeObject.DiscussionInfoListener(callback)
/*      */         {
/*      */           public void onReceived(NativeObject.DiscussionInfo info) {
/*  917 */             if (this.val$callback != null)
/*  918 */               this.val$callback.onSuccess(new Discussion(info));
/*      */           }
/*      */ 
/*      */           public void OnError(int status)
/*      */           {
/*  924 */             if (this.val$callback != null)
/*  925 */               this.val$callback.onError(status);
/*      */           }
/*      */         });
/*      */       }
/*  930 */       else if (callback != null)
/*  931 */         callback.onSuccess(discussion);
/*      */     }
/*      */     else {
/*  934 */       nativeObj.GetDiscussionInfo(discussionId, new NativeObject.DiscussionInfoListener(callback)
/*      */       {
/*      */         public void onReceived(NativeObject.DiscussionInfo info) {
/*  937 */           if (this.val$callback != null)
/*  938 */             this.val$callback.onSuccess(new Discussion(info));
/*      */         }
/*      */ 
/*      */         public void OnError(int status)
/*      */         {
/*  944 */           if (this.val$callback != null)
/*  945 */             this.val$callback.onError(status);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDiscussionName(String discussionId, String name, OperationCallback callback)
/*      */   {
/*  960 */     if (nativeObj == null) {
/*  961 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  963 */     if ((TextUtils.isEmpty(discussionId)) || (TextUtils.isEmpty(discussionId.trim())) || (TextUtils.isEmpty(name)) || (TextUtils.isEmpty(name.trim()))) {
/*  964 */       throw new IllegalArgumentException(" discussionId 或 name 参数异常。");
/*      */     }
/*  966 */     nativeObj.RenameDiscussion(discussionId, name, new NativeObject.PublishAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int code, String msgUId, long timestamp) {
/*  969 */         if (this.val$callback == null) {
/*  970 */           return;
/*      */         }
/*  972 */         if (code == 0)
/*  973 */           this.val$callback.onSuccess();
/*      */         else
/*  975 */           this.val$callback.onError(code);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void createDiscussion(String name, List<String> userIdList, IResultCallback<String> callback)
/*      */   {
/*  990 */     if (nativeObj == null) {
/*  991 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*  993 */     if ((TextUtils.isEmpty(name)) || (userIdList == null) || (userIdList.size() == 0)) {
/*  994 */       throw new IllegalArgumentException("name 或 userIdList 参数异常。");
/*      */     }
/*  996 */     if ((!TextUtils.isEmpty(this.currentUserId)) && (userIdList.contains(this.currentUserId))) {
/*  997 */       userIdList.remove(this.currentUserId);
/*      */     }
/*      */ 
/* 1000 */     String[] ids = new String[userIdList.size()];
/* 1001 */     userIdList.toArray(ids);
/*      */ 
/* 1003 */     nativeObj.CreateInviteDiscussion(name, ids, new NativeObject.CreateDiscussionCallback(callback)
/*      */     {
/*      */       public void OnError(int errorCode)
/*      */       {
/* 1007 */         if (this.val$callback != null)
/* 1008 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */ 
/*      */       public void OnSuccess(String discussionId)
/*      */       {
/* 1015 */         if (this.val$callback != null)
/* 1016 */           this.val$callback.onSuccess(discussionId);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void searchPublicService(String keyWords, int businessType, int searchType, IResultCallback callback)
/*      */   {
/* 1031 */     if (nativeObj == null) {
/* 1032 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 1034 */     if (keyWords == null) {
/* 1035 */       throw new IllegalArgumentException("keyWords 参数异常。");
/*      */     }
/* 1037 */     nativeObj.SearchAccount(keyWords, businessType, searchType, new NativeObject.AccountInfoListener(callback)
/*      */     {
/*      */       public void onReceived(NativeObject.AccountInfo[] info) {
/* 1040 */         ArrayList list = new ArrayList();
/*      */ 
/* 1042 */         for (int index = 0; index < info.length; index++) {
/* 1043 */           PublicServiceProfile item = new PublicServiceProfile();
/* 1044 */           item.setTargetId(new String(info[index].getAccountId()));
/* 1045 */           item.setName(new String(info[index].getAccountName()));
/* 1046 */           item.setPublicServiceType(Conversation.ConversationType.setValue(info[index].getAccountType()));
/* 1047 */           item.setPortraitUri(Uri.parse(new String(info[index].getAccountUri())));
/*      */ 
/* 1049 */           String ss = new String(info[index].getExtra());
/* 1050 */           RLog.i("NativeClient", "getPublicAccountInfoList extra:" + ss);
/* 1051 */           item.setExtra(ss);
/* 1052 */           list.add(item);
/*      */         }
/*      */ 
/* 1055 */         PublicServiceProfileList infoList = new PublicServiceProfileList(list);
/* 1056 */         this.val$callback.onSuccess(infoList);
/*      */       }
/*      */ 
/*      */       public void OnError(int status)
/*      */       {
/* 1061 */         this.val$callback.onError(status);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void subscribePublicService(String targetId, int categoryId, boolean subscribe, OperationCallback callback)
/*      */   {
/* 1075 */     if (nativeObj == null) {
/* 1076 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 1078 */     nativeObj.SubscribeAccount(targetId, categoryId, subscribe, new NativeObject.PublishAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int code, String msgUId, long timestamp)
/*      */       {
/* 1082 */         if (this.val$callback == null) {
/* 1083 */           return;
/*      */         }
/* 1085 */         if (code == 0)
/* 1086 */           this.val$callback.onSuccess();
/*      */         else
/* 1088 */           this.val$callback.onError(code);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getPublicServiceProfile(String targetId, int categoryId, IResultCallback<PublicServiceProfile> callback)
/*      */   {
/* 1102 */     if (nativeObj == null) {
/* 1103 */       throw new RuntimeException("RongIMClient 尚未初始化!");
/*      */     }
/* 1105 */     if (targetId == null) {
/* 1106 */       throw new IllegalArgumentException("targetId 参数异常。");
/*      */     }
/* 1108 */     if (callback != null) {
/* 1109 */       PublicServiceProfile serviceInfo = new PublicServiceProfile();
/* 1110 */       NativeObject.UserInfo info = nativeObj.GetUserInfoExSync(targetId, categoryId);
/* 1111 */       if (info != null) {
/* 1112 */         serviceInfo.setTargetId(targetId);
/* 1113 */         serviceInfo.setName(info.getUserName());
/* 1114 */         if (info.getUrl() != null)
/* 1115 */           serviceInfo.setPortraitUri(Uri.parse(info.getUrl()));
/* 1116 */         serviceInfo.setPublicServiceType(Conversation.ConversationType.setValue(info.getCategoryId()));
/* 1117 */         serviceInfo.setExtra(info.getAccountExtra());
/* 1118 */         callback.onSuccess(serviceInfo);
/*      */       } else {
/* 1120 */         callback.onError(-1);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void getPublicServiceList(IResultCallback<PublicServiceProfileList> callback)
/*      */   {
/* 1132 */     if (nativeObj == null) {
/* 1133 */       throw new RuntimeException("RongIMClient 尚未初始化!");
/*      */     }
/* 1135 */     if (callback != null)
/*      */     {
/* 1137 */       NativeObject.AccountInfo[] info = nativeObj.LoadAccountInfo();
/*      */ 
/* 1139 */       if ((info != null) && (info.length > 0))
/*      */       {
/* 1141 */         ArrayList list = new ArrayList();
/*      */ 
/* 1143 */         for (int index = 0; index < info.length; index++) {
/* 1144 */           PublicServiceProfile item = new PublicServiceProfile();
/* 1145 */           item.setTargetId(new String(info[index].getAccountId()));
/* 1146 */           item.setName(new String(info[index].getAccountName()));
/* 1147 */           item.setPublicServiceType(Conversation.ConversationType.setValue(info[index].getAccountType()));
/* 1148 */           item.setPortraitUri(Uri.parse(new String(info[index].getAccountUri())));
/* 1149 */           String ss = new String(info[index].getExtra());
/* 1150 */           item.setExtra(ss);
/* 1151 */           list.add(item);
/*      */         }
/*      */ 
/* 1154 */         PublicServiceProfileList infoList = new PublicServiceProfileList(list);
/* 1155 */         callback.onSuccess(infoList);
/*      */       } else {
/* 1157 */         callback.onError(-1);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addMemberToDiscussion(String discussionId, List<String> userIdList, OperationCallback callback)
/*      */   {
/* 1172 */     if (nativeObj == null) {
/* 1173 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 1175 */     if ((TextUtils.isEmpty(discussionId)) || (userIdList == null) || (userIdList.size() == 0)) {
/* 1176 */       throw new IllegalArgumentException("discussionId 或 userIdList 参数异常。");
/*      */     }
/* 1178 */     String[] ids = new String[userIdList.size()];
/* 1179 */     userIdList.toArray(ids);
/*      */ 
/* 1181 */     nativeObj.InviteMemberToDiscussion(discussionId, ids, new NativeObject.PublishAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int code, String msgUId, long timestamp) {
/* 1184 */         if (this.val$callback == null) {
/* 1185 */           return;
/*      */         }
/* 1187 */         if (code == 0)
/* 1188 */           this.val$callback.onSuccess();
/*      */         else
/* 1190 */           this.val$callback.onError(code);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void removeMemberFromDiscussion(String discussionId, String userId, OperationCallback callback)
/*      */   {
/* 1209 */     if (nativeObj == null) {
/* 1210 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 1212 */     if ((TextUtils.isEmpty(discussionId)) || (TextUtils.isEmpty(userId))) {
/* 1213 */       throw new IllegalArgumentException("discussionId 或 userId 参数异常。");
/*      */     }
/* 1215 */     nativeObj.RemoveMemberFromDiscussion(discussionId, userId, new NativeObject.PublishAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int code, String msgUId, long timestamp) {
/* 1218 */         if (this.val$callback == null) {
/* 1219 */           return;
/*      */         }
/* 1221 */         if (code == 0)
/* 1222 */           this.val$callback.onSuccess();
/*      */         else
/* 1224 */           this.val$callback.onError(code);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void quitDiscussion(String discussionId, OperationCallback callback)
/*      */   {
/* 1238 */     if (nativeObj == null) {
/* 1239 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 1241 */     if (TextUtils.isEmpty(discussionId)) {
/* 1242 */       throw new IllegalArgumentException("discussionId 参数异常。");
/*      */     }
/* 1244 */     nativeObj.QuitDiscussion(discussionId, new NativeObject.PublishAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int code, String msgUId, long timestamp) {
/* 1247 */         if (this.val$callback == null) {
/* 1248 */           return;
/*      */         }
/* 1250 */         if (code == 0)
/* 1251 */           this.val$callback.onSuccess();
/*      */         else
/* 1253 */           this.val$callback.onError(code);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public Message getMessage(int messageId)
/*      */   {
/* 1267 */     NativeObject.Message nativeMsg = nativeObj.GetMessageById(messageId);
/* 1268 */     Message message = new Message(nativeMsg);
/* 1269 */     MessageContent content = renderMessageContent(nativeMsg.getObjectName(), nativeMsg.getContent(), message);
/* 1270 */     message.setContent(content);
/* 1271 */     return message;
/*      */   }
/*      */ 
/*      */   public void sendMessage(Conversation.ConversationType conversationType, String targetId, MessageContent content, String pushContent, String pushData, ISendMessageCallback<Message> callback)
/*      */   {
/* 1283 */     Message message = Message.obtain(targetId, conversationType, content);
/* 1284 */     sendMessage(message, pushContent, pushData, null, callback);
/*      */   }
/*      */ 
/*      */   public void sendMessage(Message message, String pushContent, String pushData, String[] userIds, ISendMessageCallback<Message> callback) {
/* 1288 */     if (nativeObj == null) {
/* 1289 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*      */ 
/* 1292 */     if ((message.getConversationType() == null) || (TextUtils.isEmpty(message.getTargetId())) || (message.getContent() == null)) {
/* 1293 */       throw new IllegalArgumentException("message, ConversationType 或 TargetId 参数异常。");
/*      */     }
/*      */ 
/* 1296 */     MessageTag msgTag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
/* 1297 */     if (TextUtils.isEmpty(message.getSenderUserId())) {
/* 1298 */       message.setSenderUserId(this.currentUserId);
/*      */     }
/* 1300 */     message.setMessageDirection(Message.MessageDirection.SEND);
/* 1301 */     message.setSentStatus(Message.SentStatus.SENDING);
/* 1302 */     message.setSentTime(System.currentTimeMillis());
/* 1303 */     message.setObjectName(msgTag.value());
/*      */ 
/* 1305 */     byte[] data = new byte[1];
/* 1306 */     if (((msgTag.flag() & 0x1) == 1) && 
/* 1307 */       (message.getMessageId() <= 0)) {
/* 1308 */       int id = nativeObj.SaveMessage(message.getTargetId(), message.getConversationType().getValue(), msgTag.value(), message.getSenderUserId(), data, false, 0, Message.SentStatus.SENDING.getValue(), System.currentTimeMillis());
/*      */ 
/* 1311 */       message.setMessageId(id);
/*      */     }
/*      */ 
/* 1314 */     int type = msgTag.flag() == 16 ? 1 : 3;
/* 1315 */     Constructor constructor = (Constructor)messageHandlerMap.get(msgTag.value());
/* 1316 */     if (constructor == null) {
/* 1317 */       RLog.e("NativeClient", "sendMessage MessageHandler is null");
/* 1318 */       if (callback != null)
/* 1319 */         callback.onError(message, -3);
/* 1320 */       return;
/*      */     }
/*      */     try {
/* 1323 */       MessageHandler handler = (MessageHandler)constructor.newInstance(new Object[] { this.mContext });
/* 1324 */       handler.encodeMessage(message);
/* 1325 */       data = message.getContent().encode();
/* 1326 */       if (callback != null) {
/* 1327 */         callback.onAttached(message);
/*      */       }
/*      */ 
/* 1330 */       boolean isMentioned = message.getContent().getMentionedInfo() != null;
/* 1331 */       nativeObj.SetMessageContent(message.getMessageId(), data, message.getObjectName());
/*      */ 
/* 1333 */       nativeObj.SendMessage(message.getTargetId(), message.getConversationType().getValue(), type, msgTag.value(), data, TextUtils.isEmpty(pushContent) ? null : pushContent.getBytes(), TextUtils.isEmpty(pushData) ? null : pushData.getBytes(), message.getMessageId(), userIds, new NativeObject.PublishAckListener(message, callback)
/*      */       {
/*      */         public void operationComplete(int code, String msgUId, long sendTime)
/*      */         {
/* 1342 */           RLog.d("NativeClient", "sendMessage code = " + code + ", id = " + this.val$message.getMessageId() + ", uid = " + msgUId);
/* 1343 */           if (code == 0) {
/* 1344 */             this.val$message.setSentStatus(Message.SentStatus.SENT);
/* 1345 */             this.val$message.setSentTime(sendTime);
/* 1346 */             this.val$message.setUId(msgUId);
/* 1347 */             if (this.val$callback != null)
/* 1348 */               this.val$callback.onSuccess(this.val$message);
/*      */           } else {
/* 1350 */             this.val$message.setSentStatus(Message.SentStatus.FAILED);
/* 1351 */             if (this.val$callback != null)
/* 1352 */               this.val$callback.onError(this.val$message, code);
/*      */           }
/*      */         }
/*      */       }
/*      */       , isMentioned);
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 1358 */       RLog.e("NativeClient", new StringBuilder().append("sendMessage exception : ").append(e.getMessage()).toString());
/* 1359 */       e.printStackTrace();
/* 1360 */       if (callback != null)
/* 1361 */         callback.onError(message, -3);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void sendLocationMessage(Message message, String pushContent, String pushData, ISendMessageCallback<Message> callback) {
/* 1366 */     MessageTag msgTag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
/* 1367 */     if (TextUtils.isEmpty(message.getSenderUserId())) {
/* 1368 */       message.setSenderUserId(this.currentUserId);
/*      */     }
/* 1370 */     message.setMessageDirection(Message.MessageDirection.SEND);
/* 1371 */     message.setSentTime(System.currentTimeMillis());
/* 1372 */     message.setObjectName(msgTag.value());
/*      */ 
/* 1374 */     byte[] data = message.getContent().encode();
/*      */ 
/* 1376 */     int id = nativeObj.SaveMessage(message.getTargetId(), message.getConversationType().getValue(), msgTag.value(), message.getSenderUserId(), data, false, 0, Message.SentStatus.SENDING.getValue(), System.currentTimeMillis());
/*      */ 
/* 1385 */     message.setMessageId(id);
/* 1386 */     if (callback != null) {
/* 1387 */       message.setSentStatus(Message.SentStatus.SENDING);
/* 1388 */       callback.onAttached(message);
/*      */     }
/* 1390 */     if (message.getMessageId() == 0) {
/* 1391 */       RLog.e("NativeClient", "Location Message saved error");
/* 1392 */       if (callback != null) {
/* 1393 */         message.setSentStatus(Message.SentStatus.FAILED);
/* 1394 */         nativeObj.SetSendStatus(message.getMessageId(), Message.SentStatus.FAILED.getValue());
/* 1395 */         callback.onError(message, -3);
/*      */       }
/* 1397 */       return;
/*      */     }
/*      */ 
/* 1400 */     Constructor constructor = (Constructor)messageHandlerMap.get(msgTag.value());
/* 1401 */     if (constructor == null) {
/* 1402 */       RLog.e("NativeClient", "MessageHandler is null");
/* 1403 */       if (callback != null) {
/* 1404 */         message.setSentStatus(Message.SentStatus.FAILED);
/* 1405 */         nativeObj.SetSendStatus(message.getMessageId(), Message.SentStatus.FAILED.getValue());
/* 1406 */         callback.onError(message, -3);
/*      */       }
/* 1408 */       return;
/*      */     }
/*      */     try {
/* 1411 */       MessageHandler handler = (MessageHandler)constructor.newInstance(new Object[] { this.mContext });
/* 1412 */       handler.setHandleMessageListener(new IHandleMessageListener(msgTag, pushContent, pushData, callback)
/*      */       {
/*      */         public void onHandleResult(Message message, int resultCode) {
/* 1415 */           RLog.d("NativeClient", "onHandleResult " + ((LocationMessage)message.getContent()).getImgUri());
/* 1416 */           if (resultCode == 0) {
/* 1417 */             boolean isMentioned = NativeClient.this.isMentionedMessage(message);
/* 1418 */             byte[] data = message.getContent().encode();
/* 1419 */             NativeClient.nativeObj.SetMessageContent(message.getMessageId(), data, message.getObjectName());
/* 1420 */             NativeClient.nativeObj.SendMessage(message.getTargetId(), message.getConversationType().getValue(), 3, this.val$msgTag.value(), data, TextUtils.isEmpty(this.val$pushContent) ? null : this.val$pushContent.getBytes(), TextUtils.isEmpty(this.val$pushData) ? null : this.val$pushData.getBytes(), message.getMessageId(), null, new NativeObject.PublishAckListener(message)
/*      */             {
/*      */               public void operationComplete(int code, String msgUId, long sendTime)
/*      */               {
/* 1431 */                 RLog.d("NativeClient", "sendLocationMessage code = " + code + ", id = " + this.val$message.getMessageId() + ", uid = " + msgUId);
/* 1432 */                 if (code == 0) {
/* 1433 */                   this.val$message.setSentStatus(Message.SentStatus.SENT);
/* 1434 */                   this.val$message.setSentTime(sendTime);
/* 1435 */                   this.val$message.setUId(msgUId);
/* 1436 */                   if (NativeClient.14.this.val$callback != null)
/* 1437 */                     NativeClient.14.this.val$callback.onSuccess(this.val$message);
/*      */                 } else {
/* 1439 */                   this.val$message.setSentStatus(Message.SentStatus.FAILED);
/* 1440 */                   NativeClient.nativeObj.SetSendStatus(this.val$message.getMessageId(), Message.SentStatus.FAILED.getValue());
/* 1441 */                   if (NativeClient.14.this.val$callback != null)
/* 1442 */                     NativeClient.14.this.val$callback.onError(this.val$message, code);
/*      */                 }
/*      */               }
/*      */             }
/*      */             , isMentioned);
/*      */           }
/*      */           else
/*      */           {
/* 1448 */             message.setSentStatus(Message.SentStatus.FAILED);
/* 1449 */             NativeClient.nativeObj.SetSendStatus(message.getMessageId(), Message.SentStatus.FAILED.getValue());
/* 1450 */             if (this.val$callback != null)
/* 1451 */               this.val$callback.onError(message, 30014);
/*      */           }
/*      */         }
/*      */       });
/* 1455 */       handler.encodeMessage(message);
/*      */     } catch (Exception e) {
/* 1457 */       RLog.e("NativeClient", new StringBuilder().append("sendLocationMessage exception : ").append(e.getMessage()).toString());
/* 1458 */       e.printStackTrace();
/* 1459 */       if (callback != null) {
/* 1460 */         message.setSentStatus(Message.SentStatus.FAILED);
/* 1461 */         callback.onError(message, -3);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public Message sendStatusMessage(Conversation.ConversationType conversationType, String targetId, MessageContent content, int type, IResultCallback<Integer> callback)
/*      */   {
/* 1470 */     if (nativeObj == null) {
/* 1471 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 1473 */     if (targetId == null) {
/* 1474 */       throw new IllegalArgumentException("targetId 参数异常。");
/*      */     }
/* 1476 */     Message message = Message.obtain(targetId, conversationType, content);
/*      */ 
/* 1478 */     MessageTag msgTag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
/*      */ 
/* 1480 */     if (TextUtils.isEmpty(message.getSenderUserId())) {
/* 1481 */       message.setSenderUserId(this.currentUserId);
/*      */     }
/*      */ 
/* 1484 */     message.setMessageDirection(Message.MessageDirection.SEND);
/* 1485 */     message.setSentStatus(Message.SentStatus.SENDING);
/* 1486 */     message.setSentTime(System.currentTimeMillis());
/* 1487 */     message.setObjectName(msgTag.value());
/*      */ 
/* 1489 */     byte[] data = new byte[1];
/*      */ 
/* 1491 */     if ((msgTag.flag() & 0x1) == 1)
/*      */     {
/* 1493 */       if (message.getMessageId() <= 0) {
/* 1494 */         int id = nativeObj.SaveMessage(message.getTargetId(), message.getConversationType().getValue(), msgTag.value(), message.getSenderUserId(), data, false, 0, Message.SentStatus.SENDING.getValue(), System.currentTimeMillis());
/*      */ 
/* 1497 */         message.setMessageId(id);
/*      */       }
/*      */     }
/*      */ 
/* 1501 */     Constructor constructor = (Constructor)messageHandlerMap.get(msgTag.value());
/*      */     try
/*      */     {
/* 1504 */       if (constructor != null) {
/* 1505 */         MessageHandler handler = (MessageHandler)constructor.newInstance(new Object[] { this.mContext });
/* 1506 */         boolean isMentioned = isMentionedMessage(message);
/* 1507 */         handler.encodeMessage(message);
/* 1508 */         data = message.getContent().encode();
/*      */ 
/* 1510 */         nativeObj.SetMessageContent(message.getMessageId(), data, message.getObjectName());
/*      */ 
/* 1512 */         nativeObj.SendMessage(message.getTargetId(), message.getConversationType().getValue(), type, msgTag.value(), data, null, null, message.getMessageId(), null, new NativeObject.PublishAckListener(callback, message)
/*      */         {
/*      */           public void operationComplete(int code, String msgUId, long timestamp)
/*      */           {
/* 1517 */             if (this.val$callback == null) {
/* 1518 */               return;
/*      */             }
/* 1520 */             if (code == 0)
/* 1521 */               this.val$callback.onSuccess(Integer.valueOf(this.val$message.getMessageId()));
/*      */             else
/* 1523 */               this.val$callback.onError(code);
/*      */           }
/*      */         }
/*      */         , isMentioned);
/*      */ 
/* 1528 */         RLog.d("NativeClient", new StringBuilder().append("sendStatusMessage SENDED, id = ").append(message.getMessageId()).toString());
/*      */       } else {
/* 1530 */         RLog.e("NativeClient", "sendStatusMessage 该消息未注册，请调用registerMessageType方法注册。");
/*      */       }
/*      */     }
/*      */     catch (InstantiationException e) {
/* 1534 */       throw new RuntimeException(e);
/*      */     } catch (IllegalAccessException e) {
/* 1536 */       throw new RuntimeException(e);
/*      */     } catch (InvocationTargetException e) {
/* 1538 */       throw new RuntimeException(e);
/*      */     }
/*      */ 
/* 1541 */     return message;
/*      */   }
/*      */ 
/*      */   public void sendMediaMessage(Message message, String pushContent, String pushData, ISendMediaMessageCallback<Message> mediaMessageCallback) {
/* 1545 */     MessageTag msgTag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
/* 1546 */     if (TextUtils.isEmpty(message.getSenderUserId())) {
/* 1547 */       message.setSenderUserId(this.currentUserId);
/*      */     }
/* 1549 */     message.setSentTime(System.currentTimeMillis());
/* 1550 */     message.setObjectName(msgTag.value());
/*      */ 
/* 1552 */     byte[] data = message.getContent().encode();
/* 1553 */     boolean isMentioned = message.getContent().getMentionedInfo() != null;
/* 1554 */     MediaMessageContent mediaMessageContent = (MediaMessageContent)message.getContent();
/* 1555 */     if (message.getMessageId() <= 0) {
/* 1556 */       int id = nativeObj.SaveMessage(message.getTargetId(), message.getConversationType().getValue(), msgTag.value(), message.getSenderUserId(), data, false, 0, Message.SentStatus.SENDING.getValue(), System.currentTimeMillis());
/*      */ 
/* 1565 */       message.setMessageId(id);
/* 1566 */       message.setSentStatus(Message.SentStatus.SENDING);
/* 1567 */       mediaMessageCallback.onAttached(message);
/*      */ 
/* 1569 */       IResultProgressCallback progressCallback = new IResultProgressCallback(mediaMessageContent, message, msgTag, pushContent, pushData, mediaMessageCallback, isMentioned)
/*      */       {
/*      */         public void onSuccess(String s) {
/* 1572 */           Uri localPath = this.val$mediaMessageContent.getLocalPath();
/* 1573 */           this.val$mediaMessageContent.setMediaUrl(Uri.parse(s));
/* 1574 */           this.val$mediaMessageContent.setLocalPath(null);
/* 1575 */           byte[] data = this.val$mediaMessageContent.encode();
/* 1576 */           this.val$mediaMessageContent.setLocalPath(localPath);
/*      */ 
/* 1578 */           NativeClient.nativeObj.SendMessage(this.val$message.getTargetId(), this.val$message.getConversationType().getValue(), 3, this.val$msgTag.value(), data, TextUtils.isEmpty(this.val$pushContent) ? null : this.val$pushContent.getBytes(), TextUtils.isEmpty(this.val$pushData) ? null : this.val$pushData.getBytes(), this.val$message.getMessageId(), null, new NativeObject.PublishAckListener()
/*      */           {
/*      */             public void operationComplete(int code, String msgUId, long sendTime)
/*      */             {
/* 1587 */               RLog.d("NativeClient", "sendMediaMessage code = " + code + ", id = " + NativeClient.16.this.val$message.getMessageId() + ", uid = " + msgUId);
/* 1588 */               if (code == 0) {
/* 1589 */                 NativeClient.16.this.val$message.setSentStatus(Message.SentStatus.SENT);
/* 1590 */                 NativeClient.16.this.val$message.setSentTime(sendTime);
/* 1591 */                 NativeClient.16.this.val$message.setUId(msgUId);
/* 1592 */                 NativeClient.16.this.val$mediaMessageCallback.onSuccess(NativeClient.16.this.val$message);
/*      */               } else {
/* 1594 */                 NativeClient.16.this.val$message.setSentStatus(Message.SentStatus.FAILED);
/* 1595 */                 NativeClient.16.this.val$mediaMessageCallback.onError(NativeClient.16.this.val$message, code);
/*      */               }
/*      */             }
/*      */           }
/*      */           , this.val$isMentioned);
/*      */ 
/* 1601 */           data = this.val$mediaMessageContent.encode();
/* 1602 */           NativeClient.nativeObj.SetMessageContent(this.val$message.getMessageId(), data, this.val$message.getObjectName());
/*      */         }
/*      */ 
/*      */         public void onProgress(int progress)
/*      */         {
/* 1607 */           RLog.i("NativeClient", "upload onProgress " + progress);
/* 1608 */           this.val$mediaMessageCallback.onProgress(this.val$message, progress);
/*      */         }
/*      */ 
/*      */         public void onError(int code)
/*      */         {
/* 1613 */           this.val$message.setSentStatus(Message.SentStatus.FAILED);
/* 1614 */           this.val$mediaMessageCallback.onError(this.val$message, code);
/* 1615 */           NativeClient.nativeObj.SetSendStatus(this.val$message.getMessageId(), Message.SentStatus.FAILED.getValue());
/*      */         }
/*      */       };
/* 1618 */       uploadMedia(message, progressCallback);
/*      */     } else {
/* 1620 */       Uri localPath = mediaMessageContent.getLocalPath();
/* 1621 */       mediaMessageContent.setLocalPath(null);
/* 1622 */       data = mediaMessageContent.encode();
/* 1623 */       mediaMessageContent.setLocalPath(localPath);
/*      */ 
/* 1625 */       nativeObj.SendMessage(message.getTargetId(), message.getConversationType().getValue(), 3, msgTag.value(), data, TextUtils.isEmpty(pushContent) ? null : pushContent.getBytes(), TextUtils.isEmpty(pushData) ? null : pushData.getBytes(), message.getMessageId(), null, new NativeObject.PublishAckListener(message, mediaMessageCallback)
/*      */       {
/*      */         public void operationComplete(int code, String msgUId, long sendTime)
/*      */         {
/* 1634 */           RLog.d("NativeClient", "sendMediaMessage code = " + code + ", id = " + this.val$message.getMessageId() + ", uid = " + msgUId);
/* 1635 */           if (code == 0) {
/* 1636 */             this.val$message.setSentStatus(Message.SentStatus.SENT);
/* 1637 */             this.val$message.setSentTime(sendTime);
/* 1638 */             this.val$message.setUId(msgUId);
/* 1639 */             this.val$mediaMessageCallback.onSuccess(this.val$message);
/*      */           } else {
/* 1641 */             this.val$message.setSentStatus(Message.SentStatus.FAILED);
/* 1642 */             this.val$mediaMessageCallback.onError(this.val$message, code);
/*      */           }
/*      */         }
/*      */       }
/*      */       , isMentioned);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Message insertMessage(Conversation.ConversationType conversationType, String targetId, String senderUserId, MessageContent content)
/*      */   {
/* 1659 */     if (nativeObj == null) {
/* 1660 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 1662 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId)) || (content == null)) {
/* 1663 */       throw new IllegalArgumentException("conversationType 或 targetId 参数异常。");
/*      */     }
/* 1665 */     MessageTag msgTag = (MessageTag)content.getClass().getAnnotation(MessageTag.class);
/* 1666 */     if (msgTag == null) {
/* 1667 */       throw new RuntimeException("自定义消息没有加注解信息。");
/*      */     }
/* 1669 */     if (msgTag.flag() == 16) {
/* 1670 */       RLog.e("NativeClient", "insertMessage MessageTag can not be STATUS.");
/* 1671 */       return null;
/*      */     }
/*      */ 
/* 1674 */     String sender = senderUserId;
/* 1675 */     Message message = new Message();
/* 1676 */     message.setConversationType(conversationType);
/* 1677 */     message.setTargetId(targetId);
/* 1678 */     if (sender == null) {
/* 1679 */       sender = this.currentUserId;
/*      */     }
/*      */ 
/* 1682 */     if (this.currentUserId == null) {
/* 1683 */       message.setMessageDirection(Message.MessageDirection.SEND);
/* 1684 */       message.setSentStatus(Message.SentStatus.SENT);
/*      */     } else {
/* 1686 */       message.setMessageDirection(this.currentUserId.equals(sender) ? Message.MessageDirection.SEND : Message.MessageDirection.RECEIVE);
/* 1687 */       message.setSentStatus(this.currentUserId.equals(sender) ? Message.SentStatus.SENT : Message.SentStatus.RECEIVED);
/*      */     }
/* 1689 */     message.setSenderUserId(sender);
/* 1690 */     message.setReceivedTime(System.currentTimeMillis());
/* 1691 */     message.setSentTime(System.currentTimeMillis());
/* 1692 */     message.setObjectName(msgTag.value());
/* 1693 */     message.setContent(content);
/* 1694 */     byte[] data = new byte[1];
/* 1695 */     if (message.getMessageId() <= 0) {
/* 1696 */       boolean direction = (message.getMessageDirection() != null) && (message.getMessageDirection().equals(Message.MessageDirection.RECEIVE));
/* 1697 */       int id = nativeObj.SaveMessage(message.getTargetId(), message.getConversationType().getValue(), msgTag.value(), sender, data, direction, 0, Message.SentStatus.SENDING.getValue(), System.currentTimeMillis());
/*      */ 
/* 1700 */       message.setMessageId(id);
/*      */     }
/*      */ 
/* 1703 */     Constructor constructor = (Constructor)messageHandlerMap.get(msgTag.value());
/*      */     try {
/* 1705 */       if (constructor != null) {
/* 1706 */         MessageHandler handler = (MessageHandler)constructor.newInstance(new Object[] { this.mContext });
/* 1707 */         handler.encodeMessage(message);
/* 1708 */         data = message.getContent().encode();
/* 1709 */         nativeObj.SetMessageContent(message.getMessageId(), data, message.getObjectName());
/* 1710 */         nativeObj.SetSendStatus(message.getMessageId(), Message.SentStatus.SENT.getValue());
/* 1711 */         RLog.d("NativeClient", new StringBuilder().append("insertMessage Inserted, id = ").append(message.getMessageId()).toString());
/*      */       } else {
/* 1713 */         RLog.e("NativeClient", "insertMessage 该消息未注册，请调用registerMessageType方法注册。");
/*      */       }
/*      */     } catch (InstantiationException e) {
/* 1716 */       throw new RuntimeException(e);
/*      */     } catch (IllegalAccessException e) {
/* 1718 */       throw new RuntimeException(e);
/*      */     } catch (InvocationTargetException e) {
/* 1720 */       throw new RuntimeException(e);
/*      */     }
/* 1722 */     return message;
/*      */   }
/*      */ 
/*      */   public void uploadMedia(Message message, IResultProgressCallback<String> callback)
/*      */   {
/* 1736 */     if ((message == null) || (message.getConversationType() == null) || (TextUtils.isEmpty(message.getTargetId())) || (message.getContent() == null))
/*      */     {
/* 1740 */       RLog.e("NativeClient", "conversation type or targetId or message content can't be null!");
/* 1741 */       if (callback != null) {
/* 1742 */         callback.onError(RongIMClient.ErrorCode.PARAMETER_ERROR.getValue());
/*      */       }
/* 1744 */       return;
/*      */     }
/*      */ 
/* 1747 */     if (nativeObj == null) {
/* 1748 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 1750 */     String localPath = null;
/* 1751 */     int type = 1;
/* 1752 */     if ((message.getContent() instanceof ImageMessage)) {
/* 1753 */       localPath = ((ImageMessage)message.getContent()).getLocalUri().toString();
/* 1754 */     } else if ((message.getContent() instanceof FileMessage)) {
/* 1755 */       type = 4;
/* 1756 */       localPath = ((FileMessage)message.getContent()).getLocalPath().toString();
/*      */     }
/*      */ 
/* 1759 */     if ((TextUtils.isEmpty(localPath)) || (!localPath.startsWith("file://"))) {
/* 1760 */       RLog.e("NativeClient", "local path of the media file can't be empty!");
/* 1761 */       if (callback != null) {
/* 1762 */         callback.onError(RongIMClient.ErrorCode.PARAMETER_ERROR.getValue());
/*      */       }
/* 1764 */       return;
/*      */     }
/*      */ 
/* 1767 */     String filePath = localPath.substring(7);
/* 1768 */     int uploadType = type;
/* 1769 */     File file = new File(filePath);
/* 1770 */     String fileName = file.getName();
/* 1771 */     FtConst.MimeType mimeType = FtUtilities.getMimeType(fileName);
/* 1772 */     nativeObj.GetUploadToken(uploadType, new NativeObject.TokenListener(mimeType, callback, filePath, uploadType, fileName)
/*      */     {
/*      */       public void OnError(int errorCode, String token) {
/* 1775 */         if (errorCode == 0) {
/* 1776 */           String mimeKey = FtUtilities.generateKey(this.val$mimeType.getName());
/*      */ 
/* 1779 */           String ipAddr = NavigationClient.getInstance().getMediaServer(NativeClient.this.mContext);
/* 1780 */           if (!TextUtils.isEmpty(NativeClient.this.mFileServer))
/* 1781 */             ipAddr = NativeClient.this.mFileServer;
/*      */           String serverIp;
/* 1783 */           if (ipAddr != null) {
/* 1784 */             int pos = ipAddr.indexOf(":");
/*      */             String serverIp;
/* 1785 */             if (pos > 0) {
/* 1786 */               String uploadIP = ipAddr.substring(0, pos);
/* 1787 */               String upPort = ipAddr.substring(pos + 1);
/* 1788 */               serverIp = "http://" + uploadIP + ":" + upPort;
/*      */             } else {
/* 1790 */               serverIp = "http://" + ipAddr;
/*      */             }
/*      */           } else {
/* 1793 */             RLog.d("NativeClient", "uploadMedia getMediaServer returns null");
/* 1794 */             this.val$callback.onError(30008);
/* 1795 */             return;
/*      */           }
/*      */           String serverIp;
/* 1797 */           FileTransferClient.getInstance().upload(this.val$filePath, token, new RequestOption(mimeKey, this.val$mimeType, serverIp, new RequestCallBack(mimeKey)
/*      */           {
/*      */             public void onError(int code) {
/* 1800 */               RLog.d("NativeClient", "uploadMedia onError code =" + code);
/* 1801 */               NativeClient.18.this.val$callback.onError(code);
/*      */             }
/*      */ 
/*      */             public void onComplete(String url)
/*      */             {
/* 1806 */               NativeClient.nativeObj.GetDownloadUrl(NativeClient.18.this.val$uploadType, this.val$mimeKey, NativeClient.18.this.val$fileName, new NativeObject.TokenListener()
/*      */               {
/*      */                 public void OnError(int errorCode, String token) {
/* 1809 */                   if (errorCode == 0) {
/* 1810 */                     if (NativeClient.18.this.val$callback != null)
/* 1811 */                       NativeClient.18.this.val$callback.onSuccess(token);
/*      */                   } else {
/* 1813 */                     RLog.d("NativeClient", "GetDownloadUrl onError code =" + errorCode);
/* 1814 */                     if (NativeClient.18.this.val$callback != null)
/* 1815 */                       NativeClient.18.this.val$callback.onError(errorCode);
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */ 
/*      */             public void onProgress(int progress) {
/* 1823 */               if (NativeClient.18.this.val$callback != null)
/* 1824 */                 NativeClient.18.this.val$callback.onProgress(progress);
/*      */             }
/*      */ 
/*      */             public void onCanceled(Object tag)
/*      */             {
/*      */             }
/*      */           }));
/*      */         }
/* 1834 */         else if (this.val$callback != null) {
/* 1835 */           this.val$callback.onError(errorCode);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void downloadMedia(Conversation.ConversationType conversationType, String targetId, int type, String imageUrl, IResultProgressCallback<String> callback)
/*      */   {
/* 1854 */     if (nativeObj == null) {
/* 1855 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 1857 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId)) || (TextUtils.isEmpty(imageUrl))) {
/* 1858 */       throw new IllegalArgumentException("conversationType，imageUrl 或 targetId 参数异常。");
/*      */     }
/* 1860 */     String path = FileUtils.getCachePath(this.mContext, "download");
/* 1861 */     String fileName = FtUtilities.getFileName(path, ShortMD5(new String[] { imageUrl }));
/* 1862 */     FileTransferClient.getInstance().download(-1, imageUrl, new RequestOption(fileName, FtConst.MimeType.setValue(type), new RequestCallBack(callback)
/*      */     {
/*      */       public void onError(int code) {
/* 1865 */         RLog.d("NativeClient", "downloadMedia onError code =" + code);
/* 1866 */         this.val$callback.onError(code);
/*      */       }
/*      */ 
/*      */       public void onComplete(String url)
/*      */       {
/* 1871 */         RLog.d("NativeClient", "downloadMedia onComplete url =" + url);
/* 1872 */         this.val$callback.onSuccess(url);
/*      */       }
/*      */ 
/*      */       public void onProgress(int progress)
/*      */       {
/* 1877 */         if (this.val$callback != null)
/* 1878 */           this.val$callback.onProgress(progress);
/*      */       }
/*      */ 
/*      */       public void onCanceled(Object tag)
/*      */       {
/*      */       }
/*      */     }));
/*      */   }
/*      */ 
/*      */   public void downloadMediaMessage(Message message, IDownloadMediaMessageCallback<Message> callback)
/*      */   {
/* 1898 */     if (nativeObj == null) {
/* 1899 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 1901 */     String fileUrl = null;
/* 1902 */     String name = "";
/* 1903 */     FtConst.MimeType type = FtConst.MimeType.NONE;
/* 1904 */     if (((message.getContent() instanceof ImageMessage)) && (((ImageMessage)message.getContent()).getRemoteUri() != null)) {
/* 1905 */       fileUrl = ((ImageMessage)message.getContent()).getRemoteUri().toString();
/* 1906 */       name = ShortMD5(new String[] { fileUrl });
/* 1907 */       type = FtConst.MimeType.FILE_IMAGE;
/* 1908 */     } else if (((message.getContent() instanceof FileMessage)) && (((FileMessage)message.getContent()).getFileUrl() != null)) {
/* 1909 */       fileUrl = ((FileMessage)message.getContent()).getFileUrl().toString();
/* 1910 */       FileMessage fileMessage = (FileMessage)message.getContent();
/* 1911 */       name = fileMessage.getName();
/* 1912 */       type = FtUtilities.getMimeType(fileMessage.getType());
/*      */     }
/*      */ 
/* 1915 */     if (TextUtils.isEmpty(fileUrl)) {
/* 1916 */       RLog.e("NativeClient", "local path of the media file can't be empty!");
/* 1917 */       if (callback != null) {
/* 1918 */         callback.onError(RongIMClient.ErrorCode.PARAMETER_ERROR.getValue());
/*      */       }
/* 1920 */       return;
/*      */     }
/*      */ 
/* 1923 */     String path = FileUtils.getMediaDownloadDir(this.mContext);
/* 1924 */     String fileName = FtUtilities.getFileName(path, name);
/* 1925 */     FileTransferClient.getInstance().download(message.getMessageId(), fileUrl, new RequestOption(fileName, type, new RequestCallBack(callback, message)
/*      */     {
/*      */       public void onError(int code) {
/* 1928 */         RLog.d("NativeClient", "downloadMediaMessage onError code =" + code);
/* 1929 */         this.val$callback.onError(code);
/*      */       }
/*      */ 
/*      */       public void onComplete(String url)
/*      */       {
/* 1934 */         RLog.d("NativeClient", "downloadMediaMessage onComplete url =" + url);
/* 1935 */         MediaMessageContent mediaMessageContent = (MediaMessageContent)this.val$message.getContent();
/* 1936 */         mediaMessageContent.setLocalPath(Uri.parse(url));
/* 1937 */         this.val$callback.onSuccess(this.val$message);
/* 1938 */         byte[] data = mediaMessageContent.encode();
/* 1939 */         NativeClient.nativeObj.SetMessageContent(this.val$message.getMessageId(), data, this.val$message.getObjectName());
/*      */       }
/*      */ 
/*      */       public void onProgress(int progress)
/*      */       {
/* 1944 */         RLog.i("NativeClient", "download onProgress " + progress);
/* 1945 */         this.val$callback.onProgress(progress);
/*      */       }
/*      */ 
/*      */       public void onCanceled(Object tag)
/*      */       {
/* 1950 */         this.val$callback.onCanceled();
/*      */       }
/*      */     }));
/*      */   }
/*      */ 
/*      */   public void getConversationNotificationStatus(Conversation.ConversationType conversationType, String targetId, IResultCallback<Integer> callback)
/*      */   {
/* 1964 */     if (nativeObj == null) {
/* 1965 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 1967 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId)) || (callback == null)) {
/* 1968 */       throw new IllegalArgumentException("conversationType 或 targetId 参数异常。");
/*      */     }
/* 1970 */     nativeObj.GetBlockPush(targetId, conversationType.getValue(), new NativeObject.BizAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int opStatus, int status)
/*      */       {
/* 1974 */         if (this.val$callback == null) {
/* 1975 */           return;
/*      */         }
/* 1977 */         if (opStatus == 0)
/* 1978 */           this.val$callback.onSuccess(Integer.valueOf(status == 100 ? Conversation.ConversationNotificationStatus.DO_NOT_DISTURB.getValue() : Conversation.ConversationNotificationStatus.NOTIFY.getValue()));
/*      */         else
/* 1980 */           this.val$callback.onError(opStatus);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setConversationNotificationStatus(Conversation.ConversationType conversationType, String targetId, Conversation.ConversationNotificationStatus notificationStatus, IResultCallback<Integer> callback)
/*      */   {
/* 1998 */     if (nativeObj == null) {
/* 1999 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2001 */     if ((conversationType == null) || (notificationStatus == null) || (TextUtils.isEmpty(targetId)) || (callback == null)) {
/* 2002 */       throw new IllegalArgumentException("conversationType, notificationStatus 或 targetId 参数异常。");
/*      */     }
/* 2004 */     nativeObj.SetBlockPush(targetId, conversationType.getValue(), notificationStatus == Conversation.ConversationNotificationStatus.DO_NOT_DISTURB, new NativeObject.BizAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int opStatus, int status) {
/* 2007 */         if (this.val$callback == null) {
/* 2008 */           return;
/*      */         }
/* 2010 */         if (opStatus == 0) {
/* 2011 */           this.val$callback.onSuccess(Integer.valueOf(status == 100 ? Conversation.ConversationNotificationStatus.DO_NOT_DISTURB.getValue() : Conversation.ConversationNotificationStatus.NOTIFY.getValue()));
/*      */         } else {
/* 2013 */           RLog.d("NativeClient", "setConversationNotificationStatus operationComplete: opStatus = " + opStatus);
/* 2014 */           this.val$callback.onError(status);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setDiscussionInviteStatus(String targetId, int status, OperationCallback callback)
/*      */   {
/* 2031 */     if (nativeObj == null) {
/* 2032 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2034 */     if (TextUtils.isEmpty(targetId)) {
/* 2035 */       throw new IllegalArgumentException("targetId 参数异常。");
/*      */     }
/* 2037 */     nativeObj.SetInviteStatus(targetId, status, new NativeObject.PublishAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int statusCode, String msgUId, long timestamp)
/*      */       {
/* 2042 */         if (this.val$callback == null) {
/* 2043 */           return;
/*      */         }
/* 2045 */         if (statusCode == 0)
/* 2046 */           this.val$callback.onSuccess();
/*      */         else
/* 2048 */           this.val$callback.onError(statusCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void syncGroup(List<Group> groups, OperationCallback callback)
/*      */   {
/* 2062 */     if (nativeObj == null) {
/* 2063 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2065 */     if ((groups == null) || (groups.size() == 0)) {
/* 2066 */       throw new IllegalArgumentException(" groups 参数异常。");
/*      */     }
/* 2068 */     String[] ids = new String[groups.size()];
/* 2069 */     String[] names = new String[groups.size()];
/* 2070 */     int i = 0;
/*      */ 
/* 2072 */     for (Group item : groups) {
/* 2073 */       ids[i] = item.getId();
/* 2074 */       names[(i++)] = item.getName();
/*      */     }
/*      */ 
/* 2077 */     nativeObj.SyncGroups(ids, names, new NativeObject.PublishAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int code, String msgUId, long timestamp) {
/* 2080 */         if (this.val$callback == null) {
/* 2081 */           return;
/*      */         }
/* 2083 */         if (code == 0)
/* 2084 */           this.val$callback.onSuccess();
/*      */         else
/* 2086 */           this.val$callback.onError(code);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void joinGroup(String groupId, String groupName, OperationCallback callback)
/*      */   {
/* 2100 */     if (nativeObj == null) {
/* 2101 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2103 */     if ((groupId == null) || (groupName == null)) {
/* 2104 */       throw new IllegalArgumentException("groupId 或 groupName参数异常。");
/*      */     }
/* 2106 */     nativeObj.JoinGroup(groupId, groupName, new NativeObject.PublishAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int code, String msgUId, long timestamp) {
/* 2109 */         if (this.val$callback == null) {
/* 2110 */           return;
/*      */         }
/* 2112 */         if (code == 0)
/* 2113 */           this.val$callback.onSuccess();
/*      */         else
/* 2115 */           this.val$callback.onError(code);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void quitGroup(String groupId, OperationCallback callback)
/*      */   {
/* 2128 */     if (nativeObj == null) {
/* 2129 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2131 */     if (groupId == null) {
/* 2132 */       throw new IllegalArgumentException("groupId 参数异常。");
/*      */     }
/* 2134 */     nativeObj.QuitGroup(groupId, new NativeObject.PublishAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int code, String msgUId, long timestamp) {
/* 2137 */         if (this.val$callback == null) {
/* 2138 */           return;
/*      */         }
/* 2140 */         if (code == 0)
/* 2141 */           this.val$callback.onSuccess();
/*      */         else
/* 2143 */           this.val$callback.onError(code);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setOnReceiveMessageListener(OnReceiveMessageListener listener)
/*      */   {
/* 2157 */     if (nativeObj == null) {
/* 2158 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2160 */     this.mReceiveMessageListener = listener;
/*      */ 
/* 2162 */     nativeObj.SetMessageListener(new NativeObject.ReceiveMessageListener(listener)
/*      */     {
/*      */       public void onReceived(NativeObject.Message nativeMessage, int left, boolean offline, boolean hasMsg, int cmdLeft)
/*      */       {
/* 2166 */         RLog.i("NativeClient", "onReceived: objectName = " + nativeMessage.getObjectName());
/*      */ 
/* 2168 */         Message message = new Message(nativeMessage);
/* 2169 */         MessageContent content = NativeClient.this.renderMessageContent(nativeMessage.getObjectName(), nativeMessage.getContent(), message);
/* 2170 */         message.setContent(content);
/* 2171 */         if ((message.getContent() instanceof DiscussionNotificationMessage))
/*      */         {
/* 2173 */           NativeClient.nativeObj.GetDiscussionInfo(message.getTargetId(), new NativeObject.DiscussionInfoListener()
/*      */           {
/*      */             public void onReceived(NativeObject.DiscussionInfo info)
/*      */             {
/*      */             }
/*      */ 
/*      */             public void OnError(int status) {
/*      */             }
/*      */           });
/*      */         }
/* 2184 */         boolean handled = false;
/* 2185 */         if (NativeClient.this.mReceiveMessageListenerEx != null) {
/* 2186 */           handled = NativeClient.this.mReceiveMessageListenerEx.onReceived(message, left);
/*      */         }
/*      */ 
/* 2189 */         if ((this.val$listener != null) && (!handled))
/* 2190 */           this.val$listener.onReceived(message, left, offline, hasMsg, cmdLeft);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setOnReceiveMessageListenerEx(OnReceiveMessageListenerEx listenerEx) {
/* 2197 */     this.mReceiveMessageListenerEx = listenerEx;
/*      */   }
/*      */ 
/*      */   public OnReceiveMessageListener getOnReceiveMessageListener() {
/* 2201 */     return this.mReceiveMessageListener;
/*      */   }
/*      */ 
/*      */   public static void setConnectionStatusListener(ICodeListener listener)
/*      */   {
/* 2254 */     nativeObj.SetExceptionListener(new NativeObject.ExceptionListener(listener)
/*      */     {
/*      */       public void onError(int status, String desc)
/*      */       {
/* 2258 */         if (this.val$listener != null)
/* 2259 */           this.val$listener.onChanged(status);
/* 2260 */         if ((status != 0) && (NativeClient.client != null))
/* 2261 */           NativeClient.client.stopCRHeartBeat();
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public long getDeltaTime()
/*      */   {
/* 2274 */     return nativeObj.GetDeltaTime();
/*      */   }
/*      */ 
/*      */   public void queryChatRoomInfo(String id, int count, int order, IResultCallback<ChatRoomInfo> callback) {
/* 2278 */     if (nativeObj == null) {
/* 2279 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2281 */     if (TextUtils.isEmpty(id)) {
/* 2282 */       throw new IllegalArgumentException("聊天室 Id 参数异常。");
/*      */     }
/* 2284 */     nativeObj.QueryChatroomInfo(id, count, order, new NativeObject.ChatroomInfoListener(id, callback)
/*      */     {
/*      */       public void OnSuccess(int members, NativeObject.UserInfo[] users) {
/* 2287 */         List list = new ArrayList();
/* 2288 */         if (users != null) {
/* 2289 */           for (int i = 0; i < users.length; i++) {
/* 2290 */             ChatRoomMemberInfo info = new ChatRoomMemberInfo();
/* 2291 */             info.setUserId(users[i].getUserId());
/* 2292 */             info.setJoinTime(users[i].getJoinTime());
/* 2293 */             list.add(info);
/*      */           }
/*      */         }
/* 2296 */         ChatRoomInfo info = new ChatRoomInfo();
/* 2297 */         info.setChatRoomId(this.val$id);
/* 2298 */         info.setTotalMemberCount(members);
/* 2299 */         info.setMemberInfo(list);
/* 2300 */         this.val$callback.onSuccess(info);
/*      */       }
/*      */ 
/*      */       public void OnError(int status)
/*      */       {
/* 2305 */         this.val$callback.onError(status);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void joinChatRoom(String id, int defMessageCount, OperationCallback callback)
/*      */   {
/* 2319 */     if (nativeObj == null) {
/* 2320 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2322 */     if (TextUtils.isEmpty(id)) {
/* 2323 */       throw new IllegalArgumentException("聊天室 Id 参数异常。");
/*      */     }
/* 2325 */     nativeObj.JoinChatRoom(id, Conversation.ConversationType.CHATROOM.getValue(), defMessageCount, false, new NativeObject.PublishAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int code, String msgUId, long timestamp) {
/* 2328 */         if (code == 0) {
/* 2329 */           NativeClient.this.startCRHeartBeatIfNeed();
/* 2330 */           if (this.val$callback != null)
/* 2331 */             this.val$callback.onSuccess();
/*      */         }
/* 2333 */         else if (this.val$callback != null) {
/* 2334 */           this.val$callback.onError(code);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void reJoinChatRoom(String id, int defMessageCount, OperationCallback callback)
/*      */   {
/* 2349 */     if (nativeObj == null) {
/* 2350 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2352 */     if (TextUtils.isEmpty(id)) {
/* 2353 */       throw new IllegalArgumentException("聊天室 Id 参数异常。");
/*      */     }
/* 2355 */     nativeObj.JoinChatRoom(id, Conversation.ConversationType.CHATROOM.getValue(), defMessageCount, true, new NativeObject.PublishAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int code, String msgUId, long timestamp) {
/* 2358 */         if (code == 0) {
/* 2359 */           NativeClient.this.startCRHeartBeatIfNeed();
/* 2360 */           if (this.val$callback != null)
/* 2361 */             this.val$callback.onSuccess();
/*      */         }
/* 2363 */         else if (this.val$callback != null) {
/* 2364 */           this.val$callback.onError(code);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private void startCRHeartBeatIfNeed()
/*      */   {
/* 2373 */     if (this.timer == null) {
/* 2374 */       RLog.d("NativeClient", "startCRHeartBeat");
/* 2375 */       this.timer = new Timer();
/* 2376 */       TimerTask task = new TimerTask()
/*      */       {
/*      */         public void run() {
/* 2379 */           NativeClient.nativeObj.ping();
/* 2380 */           RLog.d("NativeClient", "-heart beat-");
/*      */         }
/*      */       };
/* 2383 */       this.timer.schedule(task, 1000L, 15000L);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void stopCRHeartBeat() {
/* 2388 */     if (this.timer != null) {
/* 2389 */       this.timer.cancel();
/* 2390 */       this.timer = null;
/* 2391 */       RLog.d("NativeClient", "stopCRHeartBeat");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void joinExistChatRoom(String id, int defMessageCount, OperationCallback callback)
/*      */   {
/* 2404 */     if (nativeObj == null) {
/* 2405 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2407 */     if (TextUtils.isEmpty(id)) {
/* 2408 */       throw new IllegalArgumentException("聊天室 Id 参数异常。");
/*      */     }
/* 2410 */     nativeObj.JoinExistingChatroom(id, Conversation.ConversationType.CHATROOM.getValue(), defMessageCount, new NativeObject.PublishAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int code, String msgUId, long timestamp) {
/* 2413 */         if (code == 0) {
/* 2414 */           if (this.val$callback != null)
/* 2415 */             this.val$callback.onSuccess();
/*      */         }
/* 2417 */         else if (this.val$callback != null)
/* 2418 */           this.val$callback.onError(code);
/*      */       }
/*      */     });
/* 2422 */     startCRHeartBeatIfNeed();
/*      */   }
/*      */ 
/*      */   public void quitChatRoom(String id, OperationCallback callback)
/*      */   {
/* 2433 */     if (nativeObj == null) {
/* 2434 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2436 */     if (TextUtils.isEmpty(id)) {
/* 2437 */       throw new IllegalArgumentException("聊天室 Id 参数异常。");
/*      */     }
/* 2439 */     nativeObj.QuitChatRoom(id, Conversation.ConversationType.CHATROOM.getValue(), new NativeObject.PublishAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int code, String msgUId, long timestamp) {
/* 2442 */         if (code == 0) {
/* 2443 */           if (this.val$callback != null)
/* 2444 */             this.val$callback.onSuccess();
/*      */         }
/* 2446 */         else if (this.val$callback != null)
/* 2447 */           this.val$callback.onError(code);
/*      */       }
/*      */     });
/* 2452 */     clearMessages(Conversation.ConversationType.CHATROOM, id);
/* 2453 */     stopCRHeartBeat();
/*      */   }
/*      */ 
/*      */   public boolean clearConversations(Conversation.ConversationType[] conversationTypes)
/*      */   {
/* 2463 */     if (nativeObj == null) {
/* 2464 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2466 */     if ((conversationTypes == null) || (conversationTypes.length == 0)) {
/* 2467 */       new IllegalAccessException("ConversationTypes 参数异常。");
/*      */     }
/*      */ 
/* 2470 */     int[] conversationTypeValues = new int[conversationTypes.length];
/*      */ 
/* 2472 */     int i = 0;
/* 2473 */     for (Conversation.ConversationType conversationType : conversationTypes) {
/* 2474 */       conversationTypeValues[i] = conversationType.getValue();
/* 2475 */       i++;
/*      */     }
/*      */ 
/* 2478 */     return nativeObj.ClearConversations(conversationTypeValues);
/*      */   }
/*      */ 
/*      */   public void addToBlacklist(String userId, OperationCallback callback)
/*      */   {
/* 2543 */     if (nativeObj == null) {
/* 2544 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2546 */     if ((TextUtils.isEmpty(userId)) || (callback == null)) {
/* 2547 */       throw new IllegalArgumentException("参数异常。");
/*      */     }
/* 2549 */     nativeObj.AddToBlacklist(userId, new NativeObject.PublishAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int code, String msgUId, long timestamp)
/*      */       {
/* 2553 */         if (code == 0)
/* 2554 */           this.val$callback.onSuccess();
/*      */         else
/* 2556 */           this.val$callback.onError(code);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void removeFromBlacklist(String userId, OperationCallback callback)
/*      */   {
/* 2571 */     if (nativeObj == null) {
/* 2572 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2574 */     if ((TextUtils.isEmpty(userId)) || (callback == null)) {
/* 2575 */       throw new IllegalArgumentException("用户 Id 参数异常。");
/*      */     }
/* 2577 */     nativeObj.RemoveFromBlacklist(userId, new NativeObject.PublishAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int code, String msgUId, long timestamp)
/*      */       {
/* 2581 */         if (code == 0)
/* 2582 */           this.val$callback.onSuccess();
/*      */         else
/* 2584 */           this.val$callback.onError(code);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getBlacklistStatus(String userId, IResultCallback<BlacklistStatus> callback)
/*      */   {
/* 2599 */     if (nativeObj == null) {
/* 2600 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2602 */     if ((TextUtils.isEmpty(userId)) || (callback == null)) {
/* 2603 */       throw new IllegalArgumentException("用户 Id 参数异常。");
/*      */     }
/* 2605 */     nativeObj.GetBlacklistStatus(userId, new NativeObject.BizAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int opStatus, int status)
/*      */       {
/* 2612 */         if (opStatus == 0)
/*      */         {
/* 2614 */           if (status == 0)
/* 2615 */             this.val$callback.onSuccess(NativeClient.BlacklistStatus.EXIT_BLACK_LIST);
/* 2616 */           else if (status == 101) {
/* 2617 */             this.val$callback.onSuccess(NativeClient.BlacklistStatus.NOT_EXIT_BLACK_LIST);
/*      */           }
/*      */         }
/*      */         else
/* 2621 */           this.val$callback.onError(opStatus);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getBlacklist(IResultCallback<String> callback)
/*      */   {
/* 2635 */     if (nativeObj == null) {
/* 2636 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2638 */     if (callback == null) {
/* 2639 */       throw new IllegalArgumentException("参数异常。");
/*      */     }
/* 2641 */     nativeObj.GetBlacklist(new NativeObject.SetBlacklistListener(callback)
/*      */     {
/*      */       public void OnSuccess(String userIds)
/*      */       {
/* 2645 */         if (!TextUtils.isEmpty(userIds)) {
/* 2646 */           if (this.val$callback != null) {
/* 2647 */             this.val$callback.onSuccess(userIds);
/*      */           }
/*      */         }
/* 2650 */         else if (this.val$callback != null)
/* 2651 */           this.val$callback.onSuccess(null);
/*      */       }
/*      */ 
/*      */       public void OnError(int errorCode)
/*      */       {
/* 2658 */         if (this.val$callback != null)
/* 2659 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setNotificationQuietHours(String startTime, int spanMinutes, OperationCallback callback)
/*      */   {
/* 2674 */     if (nativeObj == null) {
/* 2675 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*      */ 
/* 2678 */     if ((TextUtils.isEmpty(startTime)) || (spanMinutes <= 0) || (spanMinutes >= 1440) || (callback == null)) {
/* 2679 */       throw new IllegalArgumentException("startTime, spanMinutes 或 spanMinutes 参数异常。");
/*      */     }
/* 2681 */     Pattern pattern = Pattern.compile("^(([0-1][0-9])|2[0-3]):[0-5][0-9]:([0-5][0-9])$");
/* 2682 */     Matcher matcher = pattern.matcher(startTime);
/*      */ 
/* 2684 */     if (!matcher.find()) {
/* 2685 */       throw new IllegalArgumentException("startTime 参数异常。");
/*      */     }
/*      */ 
/* 2689 */     nativeObj.AddPushSetting(startTime, spanMinutes, new NativeObject.PublishAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int code, String msgUId, long timestamp)
/*      */       {
/* 2694 */         if (0 == code)
/* 2695 */           this.val$callback.onSuccess();
/*      */         else
/* 2697 */           this.val$callback.onError(code);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void removeNotificationQuietHours(OperationCallback callback)
/*      */   {
/* 2710 */     if (nativeObj == null) {
/* 2711 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2713 */     if (callback == null) {
/* 2714 */       throw new IllegalArgumentException("参数异常。");
/*      */     }
/* 2716 */     nativeObj.RemovePushSetting(new NativeObject.PublishAckListener(callback)
/*      */     {
/*      */       public void operationComplete(int code, String msgUId, long timestamp)
/*      */       {
/* 2721 */         if (0 == code)
/* 2722 */           this.val$callback.onSuccess();
/*      */         else
/* 2724 */           this.val$callback.onError(code);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getNotificationQuietHours(GetNotificationQuietHoursCallback callback)
/*      */   {
/* 2741 */     if (nativeObj == null) {
/* 2742 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2744 */     if (callback == null) {
/* 2745 */       throw new IllegalArgumentException("参数异常。");
/*      */     }
/*      */ 
/* 2748 */     nativeObj.QueryPushSetting(new NativeObject.PushSettingListener(callback)
/*      */     {
/*      */       public void OnError(int status) {
/* 2751 */         this.val$callback.onError(status);
/*      */       }
/*      */ 
/*      */       public void OnSuccess(String startTime, int spanMins)
/*      */       {
/* 2756 */         this.val$callback.onSuccess(startTime, spanMins);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setUserData(UserData userData, OperationCallback callback)
/*      */   {
/* 2765 */     if (nativeObj == null) {
/* 2766 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 2768 */     if (userData == null) {
/* 2769 */       throw new IllegalArgumentException("userData 参数异常。");
/*      */     }
/* 2771 */     JSONObject jsonObj = new JSONObject();
/*      */     try
/*      */     {
/* 2774 */       if (userData.getPersonalInfo() != null) {
/* 2775 */         JSONObject personalInfo = new JSONObject();
/*      */ 
/* 2777 */         personalInfo.putOpt("realName", userData.getPersonalInfo().getRealName());
/* 2778 */         personalInfo.putOpt("sex", userData.getPersonalInfo().getSex());
/* 2779 */         personalInfo.putOpt("age", userData.getPersonalInfo().getAge());
/* 2780 */         personalInfo.putOpt("birthday", userData.getPersonalInfo().getBirthday());
/* 2781 */         personalInfo.putOpt("job", userData.getPersonalInfo().getJob());
/* 2782 */         personalInfo.putOpt("portraitUri", userData.getPersonalInfo().getPortraitUri());
/* 2783 */         personalInfo.putOpt("comment", userData.getPersonalInfo().getComment());
/*      */ 
/* 2786 */         jsonObj.put("personalInfo", personalInfo);
/*      */       }
/*      */ 
/* 2789 */       if (userData.getAccountInfo() != null) {
/* 2790 */         JSONObject accountInfo = new JSONObject();
/* 2791 */         accountInfo.putOpt("appUserId", userData.getAccountInfo().getAppUserId());
/* 2792 */         accountInfo.putOpt("userName", userData.getAccountInfo().getUserName());
/* 2793 */         accountInfo.putOpt("nickName", userData.getAccountInfo().getNickName());
/*      */ 
/* 2795 */         jsonObj.putOpt("accountInfo", accountInfo);
/*      */       }
/*      */ 
/* 2798 */       if (userData.getContactInfo() != null) {
/* 2799 */         JSONObject contactInfo = new JSONObject();
/*      */ 
/* 2801 */         contactInfo.putOpt("tel", userData.getContactInfo().getTel());
/* 2802 */         contactInfo.putOpt("email", userData.getContactInfo().getEmail());
/* 2803 */         contactInfo.putOpt("address", userData.getContactInfo().getAddress());
/* 2804 */         contactInfo.putOpt("qq", userData.getContactInfo().getQQ());
/* 2805 */         contactInfo.putOpt("weibo", userData.getContactInfo().getWeibo());
/* 2806 */         contactInfo.putOpt("weixin", userData.getContactInfo().getWeixin());
/*      */ 
/* 2808 */         jsonObj.putOpt("contactInfo", contactInfo);
/*      */       }
/*      */ 
/* 2811 */       if (userData.getClientInfo() != null) {
/* 2812 */         JSONObject clientInfo = new JSONObject();
/*      */ 
/* 2814 */         clientInfo.putOpt("network", userData.getClientInfo().getNetwork());
/* 2815 */         clientInfo.putOpt("carrier", userData.getClientInfo().getCarrier());
/* 2816 */         clientInfo.putOpt("systemVersion", userData.getClientInfo().getSystemVersion());
/* 2817 */         clientInfo.putOpt("os", userData.getClientInfo().getOs());
/* 2818 */         clientInfo.putOpt("device", userData.getClientInfo().getDevice());
/* 2819 */         clientInfo.putOpt("mobilePhoneManufacturers", userData.getClientInfo().getMobilePhoneManufacturers());
/*      */ 
/* 2821 */         jsonObj.putOpt("clientInfo", clientInfo);
/*      */       }
/*      */ 
/* 2824 */       jsonObj.putOpt("appVersion", userData.getAppVersion());
/* 2825 */       jsonObj.putOpt("extra", userData.getExtra());
/*      */ 
/* 2827 */       String result = jsonObj.toString();
/* 2828 */       RLog.d("NativeClient", new StringBuilder().append("UserData ").append(result).toString());
/*      */ 
/* 2830 */       nativeObj.SetUserData(result, new NativeObject.PublishAckListener(callback)
/*      */       {
/*      */         public void operationComplete(int code, String msgUId, long timestamp) {
/* 2833 */           if (this.val$callback != null)
/* 2834 */             if (code == 0)
/* 2835 */               this.val$callback.onSuccess();
/*      */             else
/* 2837 */               this.val$callback.onError(code);
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (JSONException e)
/*      */     {
/* 2844 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void setEnvInfo(Context context)
/*      */   {
/* 2851 */     String network = "";
/* 2852 */     String MCCMNC = "";
/*      */     try
/*      */     {
/* 2855 */       ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService("connectivity");
/*      */ 
/* 2858 */       if ((connectivityManager != null) && (connectivityManager.getActiveNetworkInfo() != null)) {
/* 2859 */         network = connectivityManager.getActiveNetworkInfo().getTypeName();
/*      */       }
/*      */ 
/* 2862 */       TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService("phone");
/* 2863 */       if (telephonyManager != null)
/* 2864 */         MCCMNC = telephonyManager.getNetworkOperator();
/*      */     }
/*      */     catch (SecurityException e) {
/* 2867 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 2870 */     String manufacturer = Build.MANUFACTURER;
/* 2871 */     String model = Build.MODEL;
/*      */ 
/* 2873 */     if (manufacturer == null) manufacturer = "";
/* 2874 */     if (model == null) model = "";
/*      */ 
/* 2877 */     nativeObj.SetDeviceInfo(manufacturer, model, String.valueOf(Build.VERSION.SDK_INT), network, MCCMNC);
/*      */   }
/*      */ 
/*      */   public Message getMessageByUid(String uid)
/*      */   {
/* 2977 */     NativeObject.Message message = nativeObj.GetMessageByUId(uid);
/*      */ 
/* 2979 */     if (message == null) {
/* 2980 */       return null;
/*      */     }
/* 2982 */     Message msg = new Message(message);
/* 2983 */     MessageContent content = renderMessageContent(message.getObjectName(), message.getContent(), msg);
/* 2984 */     msg.setContent(content);
/* 2985 */     return msg;
/*      */   }
/*      */ 
/*      */   public int setupRealTimeLocation(Context context, int type, String targetId)
/*      */   {
/* 2997 */     if (nativeObj == null) {
/* 2998 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 3000 */     if (this.mRealTimeLocationManager == null) {
/* 3001 */       RLog.e("NativeClient", "setupRealTimeLocation RealTimeLocationManager Not setup!");
/* 3002 */       return -1;
/*      */     }
/*      */ 
/* 3005 */     return this.mRealTimeLocationManager.setupRealTimeLocation(context, Conversation.ConversationType.setValue(type), targetId);
/*      */   }
/*      */ 
/*      */   public int startRealTimeLocation(int type, String targetId)
/*      */   {
/* 3012 */     if (nativeObj == null) {
/* 3013 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 3015 */     if (this.mRealTimeLocationManager == null) {
/* 3016 */       RLog.e("NativeClient", "startRealTimeLocation RealTimeLocationManager Not setup!");
/* 3017 */       return -1;
/*      */     }
/* 3019 */     return this.mRealTimeLocationManager.startRealTimeLocation(Conversation.ConversationType.setValue(type), targetId);
/*      */   }
/*      */ 
/*      */   public int joinRealTimeLocation(int type, String targetId)
/*      */   {
/* 3026 */     if (nativeObj == null) {
/* 3027 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 3029 */     if (this.mRealTimeLocationManager == null) {
/* 3030 */       RLog.e("NativeClient", "joinRealTimeLocation RealTimeLocationManager Not setup!");
/* 3031 */       return -1;
/*      */     }
/* 3033 */     return this.mRealTimeLocationManager.joinRealTimeLocation(Conversation.ConversationType.setValue(type), targetId);
/*      */   }
/*      */ 
/*      */   public void quitRealTimeLocation(int type, String targetId)
/*      */   {
/* 3040 */     if (nativeObj == null) {
/* 3041 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 3043 */     if (this.mRealTimeLocationManager == null) {
/* 3044 */       RLog.e("NativeClient", "quitRealTimeLocation RealTimeLocationManager Not setup!");
/* 3045 */       return;
/*      */     }
/* 3047 */     this.mRealTimeLocationManager.quitRealTimeLocation(Conversation.ConversationType.setValue(type), targetId);
/*      */   }
/*      */ 
/*      */   public List<String> getRealTimeLocationParticipants(int type, String targetId)
/*      */   {
/* 3056 */     if (nativeObj == null) {
/* 3057 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 3059 */     if (this.mRealTimeLocationManager == null) {
/* 3060 */       RLog.e("NativeClient", "getRealTimeLocationParticipants RealTimeLocationManager Not setup!");
/* 3061 */       return null;
/*      */     }
/* 3063 */     return this.mRealTimeLocationManager.getRealTimeLocationParticipants(Conversation.ConversationType.setValue(type), targetId);
/*      */   }
/*      */ 
/*      */   public RealTimeLocationConstant.RealTimeLocationStatus getRealTimeLocationCurrentState(int type, String targetId)
/*      */   {
/* 3072 */     if (nativeObj == null) {
/* 3073 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 3075 */     if (this.mRealTimeLocationManager == null) {
/* 3076 */       RLog.e("NativeClient", "getRealTimeLocationCurrentState RealTimeLocationManager Not setup!");
/* 3077 */       return RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_IDLE;
/*      */     }
/* 3079 */     return this.mRealTimeLocationManager.getRealTimeLocationCurrentState(Conversation.ConversationType.setValue(type), targetId);
/*      */   }
/*      */ 
/*      */   public void addListener(int type, String targetId, RealTimeLocationListener listener)
/*      */   {
/* 3088 */     if (nativeObj == null) {
/* 3089 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 3091 */     if (this.mRealTimeLocationManager == null) {
/* 3092 */       RLog.e("NativeClient", "addListener RealTimeLocationManager Not setup!");
/* 3093 */       return;
/*      */     }
/* 3095 */     this.mRealTimeLocationManager.addListener(Conversation.ConversationType.setValue(type), targetId, listener);
/*      */   }
/*      */ 
/*      */   public void removeListener(int type, String targetId, RealTimeLocationListener listener)
/*      */   {
/* 3104 */     if (nativeObj == null) {
/* 3105 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 3107 */     if (this.mRealTimeLocationManager == null) {
/* 3108 */       RLog.e("NativeClient", "removeListener RealTimeLocationManager Not setup!");
/* 3109 */       return;
/*      */     }
/* 3111 */     this.mRealTimeLocationManager.removeListener(Conversation.ConversationType.setValue(type), targetId, listener);
/*      */   }
/*      */ 
/*      */   public void updateRealTimeLocationStatus(int type, String targetId, double latitude, double longitude) {
/* 3115 */     if (nativeObj == null) {
/* 3116 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 3118 */     if (this.mRealTimeLocationManager == null) {
/* 3119 */       RLog.e("NativeClient", "removeListener RealTimeLocationManager Not setup!");
/* 3120 */       return;
/*      */     }
/* 3122 */     this.mRealTimeLocationManager.updateLocation(Conversation.ConversationType.setValue(type), targetId, latitude, longitude);
/*      */   }
/*      */ 
/*      */   public boolean updateMessageReceiptStatus(String targetId, int categoryId, long timestamp)
/*      */   {
/* 3138 */     if (nativeObj == null) {
/* 3139 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 3141 */     return nativeObj.UpdateMessageReceiptStatus(targetId, categoryId, timestamp);
/*      */   }
/*      */ 
/*      */   public boolean clearUnreadByReceipt(int conversationType, String targetId, long timestamp) {
/* 3145 */     if (nativeObj == null) {
/* 3146 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 3148 */     return nativeObj.ClearUnreadByReceipt(targetId, conversationType, timestamp);
/*      */   }
/*      */ 
/*      */   public long getSendTimeByMessageId(int messageId) {
/* 3152 */     if (nativeObj == null) {
/* 3153 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 3155 */     return nativeObj.GetSendTimeByMessageId(messageId);
/*      */   }
/*      */ 
/*      */   public boolean updateConversationInfo(Conversation.ConversationType conversationType, String targetId, String title, String portrait) {
/* 3159 */     if (nativeObj == null)
/* 3160 */       throw new RuntimeException("NativeClient 尚未初始化!");
/* 3161 */     return nativeObj.UpdateConversationInfo(targetId, conversationType.getValue(), title, portrait);
/*      */   }
/*      */ 
/*      */   public void getVoIPKey(int engineType, String channelName, String extra, IResultCallback<String> callback) {
/* 3165 */     if (nativeObj == null)
/* 3166 */       throw new RuntimeException("NativeClient 尚未初始化!");
/* 3167 */     nativeObj.GetVoIPKey(engineType, channelName, extra, new NativeObject.TokenListener(callback)
/*      */     {
/*      */       public void OnError(int errorCode, String token) {
/* 3170 */         if (errorCode == 0)
/* 3171 */           this.val$callback.onSuccess(token);
/*      */         else
/* 3173 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public String getVoIPCallInfo() {
/* 3180 */     if (nativeObj == null)
/* 3181 */       throw new RuntimeException("NativeClient 尚未初始化!");
/* 3182 */     return NavigationClient.getInstance().getVoIPCallInfo(this.mContext);
/*      */   }
/*      */ 
/*      */   private String ShortMD5(String[] args) {
/*      */     try {
/* 3187 */       StringBuilder builder = new StringBuilder();
/*      */ 
/* 3189 */       for (String arg : args) {
/* 3190 */         builder.append(arg);
/*      */       }
/*      */ 
/* 3193 */       MessageDigest mdInst = MessageDigest.getInstance("MD5");
/* 3194 */       mdInst.update(builder.toString().getBytes());
/* 3195 */       byte[] mds = mdInst.digest();
/* 3196 */       mds = Base64.encode(mds, 2);
/* 3197 */       String result = new String(mds);
/* 3198 */       result = result.replace("=", "").replace("+", "-").replace("/", "_").replace("\n", "");
/* 3199 */       return result;
/*      */     } catch (Exception e) {
/* 3201 */       e.printStackTrace();
/*      */     }
/* 3203 */     return "";
/*      */   }
/*      */ 
/*      */   public void setServerInfo(String naviServer, String fileServer) {
/* 3207 */     this.mNaviServer = naviServer;
/* 3208 */     this.mFileServer = fileServer;
/* 3209 */     NavigationClient.getInstance().setNaviDomain(naviServer);
/*      */   }
/*      */ 
/*      */   public void getPCAuthConfig(IResultCallback<String> resultCallback)
/*      */   {
/* 3218 */     nativeObj.GetAuthConfig(new NativeObject.TokenListener(resultCallback)
/*      */     {
/*      */       public void OnError(int errorCode, String token) {
/* 3221 */         RLog.i("NativeClient", "getPCAuthConfig: " + errorCode + " --- " + token);
/* 3222 */         if ((errorCode == 0) && (token != null)) {
/* 3223 */           String[] result = token.split(";;;");
/* 3224 */           String customId = result[0];
/* 3225 */           String code = result[1];
/* 3226 */           String str = NavigationClient.getInstance().getCMPServer();
/* 3227 */           if (!TextUtils.isEmpty(str)) {
/* 3228 */             String cmpIP = str.split(":")[0];
/* 3229 */             this.val$resultCallback.onSuccess(customId + ":" + code + ":" + cmpIP);
/* 3230 */             PCAuthConfig.getInstance().postConfig(customId, code, NativeClient.this.appKey, NativeClient.this.mNaviServer, cmpIP);
/*      */           }
/*      */         } else {
/* 3233 */           this.val$resultCallback.onError(errorCode);
/*      */         }
/*      */       } } );
/*      */   }
/*      */ 
/*      */   public boolean setMessageContent(int messageId, byte[] messageContent, String objectName) {
/* 3240 */     if (nativeObj == null) {
/* 3241 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/*      */ 
/* 3244 */     return nativeObj.SetMessageContent(messageId, messageContent, objectName);
/*      */   }
/*      */ 
/*      */   public String getToken() {
/* 3248 */     return this.token;
/*      */   }
/*      */ 
/*      */   public String getAppKey() {
/* 3252 */     return this.appKey;
/*      */   }
/*      */ 
/*      */   public String getDeviceId() {
/* 3256 */     return this.deviceId;
/*      */   }
/*      */ 
/*      */   private boolean isMentionedMessage(Message message) {
/* 3260 */     if ((message.getContent() instanceof TextMessage)) {
/* 3261 */       TextMessage textMessage = (TextMessage)message.getContent();
/* 3262 */       if (textMessage.getMentionedInfo() != null)
/* 3263 */         return true;
/*      */     }
/* 3265 */     return false;
/*      */   }
/*      */ 
/*      */   public List<Message> getUnreadMentionedMessages(Conversation.ConversationType conversationType, String targetId) {
/* 3269 */     if (nativeObj == null) {
/* 3270 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 3272 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId))) {
/* 3273 */       throw new IllegalArgumentException("ConversationTypes 或 targetId 参数异常。");
/*      */     }
/* 3275 */     targetId = targetId.trim();
/*      */ 
/* 3277 */     NativeObject.Message[] array = nativeObj.GetMentionMessages(targetId, conversationType.getValue());
/*      */ 
/* 3279 */     List list = new ArrayList();
/*      */ 
/* 3281 */     if (array == null) {
/* 3282 */       return list;
/*      */     }
/* 3284 */     for (NativeObject.Message item : array) {
/* 3285 */       Message msg = new Message(item);
/* 3286 */       msg.setContent(renderMessageContent(item.getObjectName(), item.getContent(), msg));
/* 3287 */       list.add(msg);
/*      */     }
/*      */ 
/* 3290 */     return list;
/*      */   }
/*      */ 
/*      */   public void setLogStatus(boolean flag) {
/* 3294 */     if (nativeObj == null) {
/* 3295 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 3297 */     nativeObj.SetLogStatus(flag);
/*      */   }
/*      */ 
/*      */   boolean updateReadReceiptRequestInfo(String msgUId, String info) {
/* 3301 */     if (nativeObj == null) {
/* 3302 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 3304 */     return nativeObj.UpdateReadReceiptRequestInfo(msgUId, info);
/*      */   }
/*      */ 
/*      */   void registerCmdMsgType(List<String> msgTypeList) {
/* 3308 */     if (nativeObj == null) {
/* 3309 */       throw new RuntimeException("NativeClient 尚未初始化!");
/*      */     }
/* 3311 */     nativeObj.RegisterCmdMsgType((String[])msgTypeList.toArray(new String[msgTypeList.size()]));
/*      */   }
/*      */ 
/*      */   public static abstract interface RealTimeLocationListener
/*      */   {
/*      */     public abstract void onStatusChange(RealTimeLocationConstant.RealTimeLocationStatus paramRealTimeLocationStatus);
/*      */ 
/*      */     public abstract void onReceiveLocation(double paramDouble1, double paramDouble2, String paramString);
/*      */ 
/*      */     public abstract void onParticipantsJoin(String paramString);
/*      */ 
/*      */     public abstract void onParticipantsQuit(String paramString);
/*      */ 
/*      */     public abstract void onError(RealTimeLocationConstant.RealTimeLocationErrorCode paramRealTimeLocationErrorCode);
/*      */   }
/*      */ 
/*      */   public static abstract interface OnReceiveMessageListenerEx
/*      */   {
/*      */     public abstract boolean onReceived(Message paramMessage, int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface OnReceiveMessageListener
/*      */   {
/*      */     public abstract void onReceived(Message paramMessage, int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2);
/*      */   }
/*      */ 
/*      */   public static abstract interface GetNotificationQuietHoursCallback
/*      */   {
/*      */     public abstract void onSuccess(String paramString, int paramInt);
/*      */ 
/*      */     public abstract void onError(int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface IDownloadMediaMessageCallback<T>
/*      */   {
/*      */     public abstract void onSuccess(T paramT);
/*      */ 
/*      */     public abstract void onProgress(int paramInt);
/*      */ 
/*      */     public abstract void onCanceled();
/*      */ 
/*      */     public abstract void onError(int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface ISendMediaMessageCallback<T>
/*      */   {
/*      */     public abstract void onAttached(T paramT);
/*      */ 
/*      */     public abstract void onSuccess(T paramT);
/*      */ 
/*      */     public abstract void onProgress(T paramT, int paramInt);
/*      */ 
/*      */     public abstract void onError(T paramT, int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface IResultProgressCallback<T>
/*      */   {
/*      */     public abstract void onSuccess(T paramT);
/*      */ 
/*      */     public abstract void onProgress(int paramInt);
/*      */ 
/*      */     public abstract void onError(int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface IResultSendMessageCallback<T>
/*      */   {
/*      */     public abstract void onSuccess(T paramT);
/*      */ 
/*      */     public abstract void onError(T paramT, int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface ISendMessageCallback<T>
/*      */   {
/*      */     public abstract void onAttached(T paramT);
/*      */ 
/*      */     public abstract void onSuccess(T paramT);
/*      */ 
/*      */     public abstract void onError(T paramT, int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface IResultCallback<T>
/*      */   {
/*      */     public abstract void onSuccess(T paramT);
/*      */ 
/*      */     public abstract void onError(int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface OperationCallback
/*      */   {
/*      */     public abstract void onSuccess();
/*      */ 
/*      */     public abstract void onError(int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface ICodeListener
/*      */   {
/*      */     public abstract void onChanged(int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface ICodeCallback
/*      */   {
/*      */     public abstract void onResult(int paramInt);
/*      */   }
/*      */ 
/*      */   public static enum BlacklistStatus
/*      */   {
/* 2490 */     EXIT_BLACK_LIST(0), 
/*      */ 
/* 2495 */     NOT_EXIT_BLACK_LIST(1);
/*      */ 
/* 2498 */     private int value = 1;
/*      */ 
/*      */     private BlacklistStatus(int value)
/*      */     {
/* 2506 */       this.value = value;
/*      */     }
/*      */ 
/*      */     public int getValue()
/*      */     {
/* 2515 */       return this.value;
/*      */     }
/*      */ 
/*      */     public static BlacklistStatus setValue(int code)
/*      */     {
/* 2525 */       for (BlacklistStatus c : values()) {
/* 2526 */         if (code == c.getValue()) {
/* 2527 */           return c;
/*      */         }
/*      */       }
/* 2530 */       return NOT_EXIT_BLACK_LIST;
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.NativeClient
 * JD-Core Version:    0.6.0
 */
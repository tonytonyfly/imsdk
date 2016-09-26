/*      */ package io.rong.imkit;
/*      */ 
/*      */ import android.content.Context;
/*      */ import android.content.Intent;
/*      */ import android.content.SharedPreferences;
/*      */ import android.content.SharedPreferences.Editor;
/*      */ import android.content.pm.ApplicationInfo;
/*      */ import android.content.pm.PackageManager;
/*      */ import android.content.pm.PackageManager.NameNotFoundException;
/*      */ import android.content.res.Resources;
/*      */ import android.content.res.Resources.NotFoundException;
/*      */ import android.graphics.Bitmap;
/*      */ import android.net.Uri;
/*      */ import android.net.Uri.Builder;
/*      */ import android.os.Bundle;
/*      */ import android.text.TextUtils;
/*      */ import android.view.View;
/*      */ import io.rong.common.RLog;
/*      */ import io.rong.eventbus.EventBus;
/*      */ import io.rong.imageloader.core.ImageLoader;
/*      */ import io.rong.imageloader.core.assist.FailReason;
/*      */ import io.rong.imageloader.core.listener.ImageLoadingListener;
/*      */ import io.rong.imageloader.core.listener.ImageLoadingProgressListener;
/*      */ import io.rong.imkit.manager.AudioRecordManager;
/*      */ import io.rong.imkit.manager.SendImageManager;
/*      */ import io.rong.imkit.mention.RongMentionManager;
/*      */ import io.rong.imkit.model.ConversationKey;
/*      */ import io.rong.imkit.model.ConversationTypeFilter;
/*      */ import io.rong.imkit.model.Event.AddMemberToDiscussionEvent;
/*      */ import io.rong.imkit.model.Event.AddToBlacklistEvent;
/*      */ import io.rong.imkit.model.Event.ClearConversationEvent;
/*      */ import io.rong.imkit.model.Event.ConnectEvent;
/*      */ import io.rong.imkit.model.Event.ConversationNotificationEvent;
/*      */ import io.rong.imkit.model.Event.ConversationRemoveEvent;
/*      */ import io.rong.imkit.model.Event.ConversationTopEvent;
/*      */ import io.rong.imkit.model.Event.ConversationUnreadEvent;
/*      */ import io.rong.imkit.model.Event.CreateDiscussionEvent;
/*      */ import io.rong.imkit.model.Event.DiscussionInviteStatusEvent;
/*      */ import io.rong.imkit.model.Event.FileMessageEvent;
/*      */ import io.rong.imkit.model.Event.JoinChatRoomEvent;
/*      */ import io.rong.imkit.model.Event.JoinGroupEvent;
/*      */ import io.rong.imkit.model.Event.MessageDeleteEvent;
/*      */ import io.rong.imkit.model.Event.MessageRecallEvent;
/*      */ import io.rong.imkit.model.Event.MessageSentStatusEvent;
/*      */ import io.rong.imkit.model.Event.MessagesClearEvent;
/*      */ import io.rong.imkit.model.Event.OnMessageSendErrorEvent;
/*      */ import io.rong.imkit.model.Event.OnReceiveMessageEvent;
/*      */ import io.rong.imkit.model.Event.OnReceiveMessageProgressEvent;
/*      */ import io.rong.imkit.model.Event.QuitChatRoomEvent;
/*      */ import io.rong.imkit.model.Event.QuitDiscussionEvent;
/*      */ import io.rong.imkit.model.Event.QuitGroupEvent;
/*      */ import io.rong.imkit.model.Event.ReadReceiptEvent;
/*      */ import io.rong.imkit.model.Event.ReadReceiptRequestEvent;
/*      */ import io.rong.imkit.model.Event.ReadReceiptResponseEvent;
/*      */ import io.rong.imkit.model.Event.RemoteMessageRecallEvent;
/*      */ import io.rong.imkit.model.Event.RemoveFromBlacklistEvent;
/*      */ import io.rong.imkit.model.Event.RemoveMemberFromDiscussionEvent;
/*      */ import io.rong.imkit.model.Event.SyncGroupEvent;
/*      */ import io.rong.imkit.model.Event.SyncReadStatusEvent;
/*      */ import io.rong.imkit.model.GroupUserInfo;
/*      */ import io.rong.imkit.model.UIConversation;
/*      */ import io.rong.imkit.notification.MessageCounter;
/*      */ import io.rong.imkit.notification.MessageCounter.Counter;
/*      */ import io.rong.imkit.notification.MessageNotificationManager;
/*      */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*      */ import io.rong.imkit.utils.CommonUtils;
/*      */ import io.rong.imkit.utils.SystemUtils;
/*      */ import io.rong.imkit.widget.provider.DiscussionNotificationMessageItemProvider;
/*      */ import io.rong.imkit.widget.provider.HandshakeMessageItemProvider;
/*      */ import io.rong.imkit.widget.provider.IContainerItemProvider.ConversationProvider;
/*      */ import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
/*      */ import io.rong.imkit.widget.provider.ImageMessageItemProvider;
/*      */ import io.rong.imkit.widget.provider.InfoNotificationMsgItemProvider;
/*      */ import io.rong.imkit.widget.provider.InputProvider.ExtendProvider;
/*      */ import io.rong.imkit.widget.provider.InputProvider.MainInputProvider;
/*      */ import io.rong.imkit.widget.provider.LocationMessageItemProvider;
/*      */ import io.rong.imkit.widget.provider.PublicServiceMultiRichContentMessageProvider;
/*      */ import io.rong.imkit.widget.provider.PublicServiceRichContentMessageProvider;
/*      */ import io.rong.imkit.widget.provider.RecallMessageItemProvider;
/*      */ import io.rong.imkit.widget.provider.RichContentMessageItemProvider;
/*      */ import io.rong.imkit.widget.provider.TextMessageItemProvider;
/*      */ import io.rong.imkit.widget.provider.UnknownMessageItemProvider;
/*      */ import io.rong.imkit.widget.provider.VoiceMessageItemProvider;
/*      */ import io.rong.imlib.AnnotationNotFoundException;
/*      */ import io.rong.imlib.IRongCallback.IDownloadMediaMessageCallback;
/*      */ import io.rong.imlib.IRongCallback.ISendMediaMessageCallback;
/*      */ import io.rong.imlib.IRongCallback.ISendMessageCallback;
/*      */ import io.rong.imlib.MessageTag;
/*      */ import io.rong.imlib.RongIMClient;
/*      */ import io.rong.imlib.RongIMClient.BlacklistStatus;
/*      */ import io.rong.imlib.RongIMClient.ConnectCallback;
/*      */ import io.rong.imlib.RongIMClient.ConnectionStatusListener;
/*      */ import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
/*      */ import io.rong.imlib.RongIMClient.CreateDiscussionCallback;
/*      */ import io.rong.imlib.RongIMClient.DiscussionInviteStatus;
/*      */ import io.rong.imlib.RongIMClient.DownloadMediaCallback;
/*      */ import io.rong.imlib.RongIMClient.ErrorCode;
/*      */ import io.rong.imlib.RongIMClient.GetBlacklistCallback;
/*      */ import io.rong.imlib.RongIMClient.GetNotificationQuietHoursCallback;
/*      */ import io.rong.imlib.RongIMClient.MediaType;
/*      */ import io.rong.imlib.RongIMClient.OnReceiveMessageListener;
/*      */ import io.rong.imlib.RongIMClient.OperationCallback;
/*      */ import io.rong.imlib.RongIMClient.ReadReceiptListener;
/*      */ import io.rong.imlib.RongIMClient.RecallMessageListener;
/*      */ import io.rong.imlib.RongIMClient.ResultCallback;
/*      */ import io.rong.imlib.RongIMClient.ResultCallback.Result;
/*      */ import io.rong.imlib.RongIMClient.SearchType;
/*      */ import io.rong.imlib.RongIMClient.SendImageMessageCallback;
/*      */ import io.rong.imlib.RongIMClient.SendImageMessageWithUploadListenerCallback;
/*      */ import io.rong.imlib.RongIMClient.SendMessageCallback;
/*      */ import io.rong.imlib.RongIMClient.SyncConversationReadStatusListener;
/*      */ import io.rong.imlib.RongIMClient.UploadImageStatusListener;
/*      */ import io.rong.imlib.model.CSCustomServiceInfo;
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
/*      */ import io.rong.imlib.model.UserInfo;
/*      */ import io.rong.message.InformationNotificationMessage;
/*      */ import io.rong.message.LocationMessage;
/*      */ import io.rong.message.RecallNotificationMessage;
/*      */ import io.rong.push.RongPushClient;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.Timer;
/*      */ import java.util.TimerTask;
/*      */ 
/*      */ public class RongIM
/*      */ {
/*   85 */   private static final String TAG = RongIM.class.getSimpleName();
/*      */   private static final int ON_SUCCESS_CALLBACK = 100;
/*      */   private static final int ON_PROGRESS_CALLBACK = 101;
/*      */   private static final int ON_CANCEL_CALLBACK = 102;
/*      */   private static final int ON_ERROR_CALLBACK = 103;
/*      */   private static Context mContext;
/*      */   static RongIMClient.OnReceiveMessageListener sMessageListener;
/*      */   static RongIMClient.ConnectionStatusListener sConnectionStatusListener;
/*      */   private RongIMClientWrapper mClientWrapper;
/*   95 */   private static boolean uiReady = false;
/*      */   private String mAppKey;
/*      */   private static Timer mCallTimer;
/*  304 */   private static RongIMClient.ConnectionStatusListener mConnectionStatusListener = new RongIMClient.ConnectionStatusListener()
/*      */   {
/*      */     public void onChanged(RongIMClient.ConnectionStatusListener.ConnectionStatus status)
/*      */     {
/*  308 */       if (status != null) {
/*  309 */         RLog.d(RongIM.TAG, "ConnectionStatusListener onChanged : " + status.toString());
/*  310 */         if (RongIM.sConnectionStatusListener != null) {
/*  311 */           RongIM.sConnectionStatusListener.onChanged(status);
/*      */         }
/*      */ 
/*  314 */         if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
/*  315 */           SendImageManager.getInstance().reset();
/*  316 */         } else if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
/*  317 */           String userId = RongIMClient.getInstance().getCurrentUserId();
/*  318 */           if (!RongUserInfoManager.getInstance().isInitialized(userId)) {
/*  319 */             RongUserInfoManager.getInstance().init(RongIM.mContext, RongIM.SingletonHolder.sRongIM.mAppKey, userId, new RongUserCacheListener());
/*      */           }
/*      */         }
/*  322 */         RongContext.getInstance().getEventBus().post(status);
/*      */       }
/*      */     }
/*  304 */   };
/*      */ 
/*      */   private RongIM()
/*      */   {
/*  103 */     this.mClientWrapper = new RongIMClientWrapper();
/*      */   }
/*      */ 
/*      */   private static void saveToken(String token) {
/*  107 */     SharedPreferences preferences = mContext.getSharedPreferences("rc_token", 0);
/*  108 */     SharedPreferences.Editor editor = preferences.edit();
/*  109 */     editor.putString("token_value", token);
/*  110 */     editor.commit();
/*      */   }
/*      */ 
/*      */   public static void init(Context context)
/*      */   {
/*  123 */     String current = SystemUtils.getCurProcessName(context);
/*  124 */     String mainProcessName = context.getPackageName();
/*  125 */     if (!mainProcessName.equals(current)) {
/*  126 */       RLog.w(TAG, "Init. Current process : " + current);
/*  127 */       return;
/*      */     }
/*      */ 
/*  130 */     RLog.i(TAG, "init : " + current);
/*      */ 
/*  132 */     mContext = context;
/*  133 */     RongContext.init(context);
/*  134 */     if (TextUtils.isEmpty(SingletonHolder.sRongIM.mAppKey)) {
/*      */       try {
/*  136 */         ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
/*  137 */         if (applicationInfo != null) {
/*  138 */           SingletonHolder.sRongIM.mAppKey = applicationInfo.metaData.getString("RONG_CLOUD_APP_KEY");
/*      */         }
/*  140 */         if (TextUtils.isEmpty(SingletonHolder.sRongIM.mAppKey))
/*  141 */           throw new IllegalArgumentException("can't find RONG_CLOUD_APP_KEY in AndroidManifest.xml.");
/*      */       }
/*      */       catch (PackageManager.NameNotFoundException e)
/*      */       {
/*  145 */         e.printStackTrace();
/*  146 */         throw new ExceptionInInitializerError("can't find packageName!");
/*      */       }
/*      */     }
/*  149 */     RongIMClient.init(context, SingletonHolder.sRongIM.mAppKey);
/*      */ 
/*  151 */     registerMessageTemplate(new TextMessageItemProvider());
/*  152 */     registerMessageTemplate(new ImageMessageItemProvider());
/*  153 */     registerMessageTemplate(new LocationMessageItemProvider());
/*  154 */     registerMessageTemplate(new VoiceMessageItemProvider(context));
/*  155 */     registerMessageTemplate(new DiscussionNotificationMessageItemProvider());
/*  156 */     registerMessageTemplate(new InfoNotificationMsgItemProvider());
/*  157 */     registerMessageTemplate(new RichContentMessageItemProvider());
/*  158 */     registerMessageTemplate(new PublicServiceMultiRichContentMessageProvider());
/*  159 */     registerMessageTemplate(new PublicServiceRichContentMessageProvider());
/*  160 */     registerMessageTemplate(new HandshakeMessageItemProvider());
/*  161 */     registerMessageTemplate(new RecallMessageItemProvider());
/*  162 */     registerMessageTemplate(new UnknownMessageItemProvider());
/*      */ 
/*  164 */     Intent intent = new Intent("io.rong.intent.action.SDK_INIT");
/*  165 */     intent.setPackage(context.getPackageName());
/*  166 */     context.sendBroadcast(intent);
/*      */   }
/*      */ 
/*      */   public static void init(Context context, String appKey)
/*      */   {
/*  176 */     String current = SystemUtils.getCurProcessName(context);
/*  177 */     String mainProcessName = context.getPackageName();
/*  178 */     if (!mainProcessName.equals(current)) {
/*  179 */       RLog.w(TAG, "Init with appKey. Current process : " + current);
/*  180 */       return;
/*      */     }
/*      */ 
/*  183 */     RLog.i(TAG, "init with appKey : " + current);
/*  184 */     SingletonHolder.sRongIM.mAppKey = appKey;
/*  185 */     RongContext.init(context);
/*  186 */     init(context);
/*      */   }
/*      */ 
/*      */   public static void registerMessageType(Class<? extends MessageContent> messageContentClass)
/*      */   {
/*  195 */     if (RongContext.getInstance() != null)
/*      */       try {
/*  197 */         RongIMClient.registerMessageType(messageContentClass);
/*      */       } catch (AnnotationNotFoundException e) {
/*  199 */         e.printStackTrace();
/*      */       }
/*      */   }
/*      */ 
/*      */   public static void registerMessageTemplate(IContainerItemProvider.MessageProvider provider)
/*      */   {
/*  211 */     if (RongContext.getInstance() != null)
/*  212 */       RongContext.getInstance().registerMessageTemplate(provider);
/*      */   }
/*      */ 
/*      */   public void setCurrentUserInfo(UserInfo userInfo)
/*      */   {
/*  225 */     if (RongContext.getInstance() != null)
/*  226 */       RongContext.getInstance().setCurrentUserInfo(userInfo);
/*      */   }
/*      */ 
/*      */   public static RongIM connect(String token, RongIMClient.ConnectCallback callback)
/*      */   {
/*  241 */     if (RongContext.getInstance() == null) {
/*  242 */       RLog.e(TAG, "connect should be called in main process.");
/*  243 */       return SingletonHolder.sRongIM;
/*      */     }
/*      */ 
/*  246 */     saveToken(token);
/*  247 */     initData();
/*      */ 
/*  249 */     RongIMClient.connect(token, new RongIMClient.ConnectCallback(callback)
/*      */     {
/*      */       public void onSuccess(String userId) {
/*  252 */         Intent intent = new Intent("io.rong.intent.action.SDK_CONNECTED");
/*  253 */         intent.setPackage(RongIM.mContext.getPackageName());
/*  254 */         RongIM.mContext.sendBroadcast(intent);
/*  255 */         if (this.val$callback != null) {
/*  256 */           this.val$callback.onSuccess(userId);
/*      */         }
/*      */ 
/*  259 */         if (!RongUserInfoManager.getInstance().isInitialized(userId))
/*  260 */           RongUserInfoManager.getInstance().init(RongIM.mContext, RongIM.SingletonHolder.sRongIM.mAppKey, userId, new RongUserCacheListener());
/*  261 */         RongContext.getInstance().getEventBus().post(Event.ConnectEvent.obtain(true));
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/*  266 */         if (this.val$callback != null) {
/*  267 */           this.val$callback.onError(e);
/*      */         }
/*  269 */         String userId = RongIMClient.getInstance().getCurrentUserId();
/*  270 */         if (!RongUserInfoManager.getInstance().isInitialized(userId))
/*  271 */           RongUserInfoManager.getInstance().init(RongIM.mContext, RongIM.SingletonHolder.sRongIM.mAppKey, userId, new RongUserCacheListener());
/*  272 */         RongContext.getInstance().getEventBus().post(Event.ConnectEvent.obtain(false));
/*      */       }
/*      */ 
/*      */       public void onTokenIncorrect()
/*      */       {
/*  277 */         if (this.val$callback != null)
/*  278 */           this.val$callback.onTokenIncorrect();
/*      */       }
/*      */     });
/*  282 */     if ((!uiReady) && (mCallTimer == null)) {
/*  283 */       mCallTimer = new Timer();
/*  284 */       TimerTask task = new TimerTask()
/*      */       {
/*      */         public void run() {
/*  287 */           if ((RongIM.uiReady) && (RongIM.mCallTimer != null) && (RongIM.mContext != null)) {
/*  288 */             Intent intent = new Intent("io.rong.intent.action.UI_READY");
/*  289 */             intent.setPackage(RongIM.mContext.getPackageName());
/*  290 */             RongIM.mContext.sendBroadcast(intent);
/*  291 */             cancel();
/*  292 */             RongIM.mCallTimer.cancel();
/*  293 */             RongIM.access$402(null);
/*      */           }
/*      */         }
/*      */       };
/*  298 */       mCallTimer.schedule(task, 0L, 1000L);
/*      */     }
/*      */ 
/*  301 */     return SingletonHolder.sRongIM;
/*      */   }
/*      */ 
/*      */   private static void initData()
/*      */   {
/*  329 */     RongIMClient.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener()
/*      */     {
/*      */       public boolean onReceived(Message message, int left) {
/*  332 */         boolean isProcess = false;
/*      */ 
/*  334 */         if (RongIM.sMessageListener != null) {
/*  335 */           isProcess = RongIM.sMessageListener.onReceived(message, left);
/*      */         }
/*  337 */         MessageTag msgTag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
/*      */ 
/*  339 */         if ((msgTag != null) && ((msgTag.flag() == 3) || (msgTag.flag() == 1))) {
/*  340 */           RongContext.getInstance().getEventBus().post(new Event.OnReceiveMessageEvent(message, left));
/*      */ 
/*  343 */           if ((message.getContent() != null) && (message.getContent().getUserInfo() != null)) {
/*  344 */             CommonUtils.refreshUserInfoIfNeed(RongContext.getInstance(), message.getContent().getUserInfo());
/*      */           }
/*      */ 
/*  348 */           if ((isProcess) || (message.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId()))) {
/*  349 */             return true;
/*      */           }
/*      */ 
/*  352 */           MessageNotificationManager.getInstance().notifyIfNeed(RongContext.getInstance(), message, left);
/*      */         }
/*  355 */         else if (message.getMessageId() > 0) {
/*  356 */           RongContext.getInstance().getEventBus().post(new Event.OnReceiveMessageEvent(message, left));
/*      */         }
/*      */ 
/*  359 */         return false;
/*      */       }
/*      */     });
/*  364 */     boolean readRec = false;
/*      */     try {
/*  366 */       readRec = RongContext.getInstance().getResources().getBoolean(R.bool.rc_read_receipt);
/*      */     } catch (Resources.NotFoundException e) {
/*  368 */       RLog.e(TAG, "rc_read_receipt not configure in rc_config.xml");
/*  369 */       e.printStackTrace();
/*      */     }
/*      */ 
/*  372 */     if (readRec) {
/*  373 */       RongIMClient.setReadReceiptListener(new RongIMClient.ReadReceiptListener()
/*      */       {
/*      */         public void onReadReceiptReceived(Message message) {
/*  376 */           RongContext.getInstance().getEventBus().post(new Event.ReadReceiptEvent(message));
/*      */         }
/*      */ 
/*      */         public void onMessageReceiptRequest(Conversation.ConversationType type, String targetId, String messageUId)
/*      */         {
/*  381 */           RongContext.getInstance().getEventBus().post(new Event.ReadReceiptRequestEvent(type, targetId, messageUId));
/*      */         }
/*      */ 
/*      */         public void onMessageReceiptResponse(Conversation.ConversationType type, String targetId, String messageUId, HashMap<String, Long> respondUserIdList)
/*      */         {
/*  386 */           RongContext.getInstance().getEventBus().post(new Event.ReadReceiptResponseEvent(type, targetId, messageUId, respondUserIdList));
/*      */         }
/*      */       });
/*      */     }
/*  391 */     boolean syncReadStatus = false;
/*      */     try {
/*  393 */       syncReadStatus = RongContext.getInstance().getResources().getBoolean(R.bool.rc_enable_sync_read_status);
/*      */     } catch (Resources.NotFoundException e) {
/*  395 */       RLog.e(TAG, "rc_enable_sync_read_status not configure in rc_config.xml");
/*  396 */       e.printStackTrace();
/*      */     }
/*  398 */     if (syncReadStatus) {
/*  399 */       RongIMClient.getInstance().setSyncConversationReadStatusListener(new RongIMClient.SyncConversationReadStatusListener()
/*      */       {
/*      */         public void onSyncConversationReadStatus(Conversation.ConversationType type, String targetId) {
/*  402 */           RongContext.getInstance().getEventBus().post(new Event.SyncReadStatusEvent(type, targetId));
/*      */         }
/*      */       });
/*      */     }
/*      */ 
/*  408 */     RongIMClient.setRecallMessageListener(new RongIMClient.RecallMessageListener()
/*      */     {
/*      */       public void onMessageRecalled(int messageId, RecallNotificationMessage recallNotificationMessage) {
/*  411 */         RongContext.getInstance().getEventBus().post(new Event.RemoteMessageRecallEvent(messageId, recallNotificationMessage, true));
/*      */       }
/*      */     });
/*  415 */     RongIMClient.setConnectionStatusListener(mConnectionStatusListener);
/*      */   }
/*      */ 
/*      */   public static void setOnReceiveMessageListener(RongIMClient.OnReceiveMessageListener listener)
/*      */   {
/*  427 */     RLog.i(TAG, "RongIM setOnReceiveMessageListener");
/*  428 */     sMessageListener = listener;
/*      */   }
/*      */ 
/*      */   public static void setConnectionStatusListener(RongIMClient.ConnectionStatusListener listener)
/*      */   {
/*  437 */     RongIMClient.setConnectionStatusListener(listener);
/*  438 */     sConnectionStatusListener = listener;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public RongIMClientWrapper getRongIMClient()
/*      */   {
/*  447 */     return this.mClientWrapper;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void disconnect(boolean isReceivePush)
/*      */   {
/*  458 */     RongIMClient.getInstance().disconnect(isReceivePush);
/*      */   }
/*      */ 
/*      */   public void logout()
/*      */   {
/*  465 */     RongIMClient.getInstance().logout();
/*      */ 
/*  467 */     if ((RongContext.getInstance() != null) && (RongContext.getInstance().getMessageCounterLogic() != null))
/*  468 */       RongContext.getInstance().getMessageCounterLogic().clearCache();
/*  469 */     RongUserInfoManager.getInstance().uninit();
/*      */   }
/*      */ 
/*      */   public void setGroupMembersProvider(IGroupMembersProvider groupMembersProvider) {
/*  473 */     RongMentionManager.getInstance().setGroupMembersProvider(groupMembersProvider);
/*      */   }
/*      */ 
/*      */   public static void setLocationProvider(LocationProvider locationProvider)
/*      */   {
/*  504 */     if (RongContext.getInstance() != null)
/*  505 */       RongContext.getInstance().setLocationProvider(locationProvider);
/*      */   }
/*      */ 
/*      */   public void disconnect()
/*      */   {
/*  512 */     RongIMClient.getInstance().disconnect();
/*      */   }
/*      */ 
/*      */   public static RongIM getInstance()
/*      */   {
/*  521 */     return SingletonHolder.sRongIM;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void startConversationList(Context context)
/*      */   {
/*  533 */     if (context == null) {
/*  534 */       throw new IllegalArgumentException();
/*      */     }
/*  536 */     if (RongContext.getInstance() == null) {
/*  537 */       throw new ExceptionInInitializerError("RongCloud SDK not init");
/*      */     }
/*  539 */     Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon().appendPath("conversationlist").build();
/*      */ 
/*  542 */     context.startActivity(new Intent("android.intent.action.VIEW", uri));
/*      */   }
/*      */ 
/*      */   public void startConversationList(Context context, Map<String, Boolean> supportedConversation)
/*      */   {
/*  554 */     if (context == null) {
/*  555 */       throw new IllegalArgumentException();
/*      */     }
/*  557 */     if (RongContext.getInstance() == null) {
/*  558 */       throw new ExceptionInInitializerError("RongCloud SDK not init");
/*      */     }
/*  560 */     Uri.Builder builder = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon().appendPath("conversationlist");
/*  561 */     if ((supportedConversation != null) && (supportedConversation.size() > 0)) {
/*  562 */       Set keys = supportedConversation.keySet();
/*  563 */       for (String key : keys) {
/*  564 */         builder.appendQueryParameter(key, ((Boolean)supportedConversation.get(key)).booleanValue() ? "true" : "false");
/*      */       }
/*      */     }
/*      */ 
/*  568 */     context.startActivity(new Intent("android.intent.action.VIEW", builder.build()));
/*      */   }
/*      */ 
/*      */   public void startSubConversationList(Context context, Conversation.ConversationType conversationType)
/*      */   {
/*  579 */     if (context == null) {
/*  580 */       throw new IllegalArgumentException();
/*      */     }
/*  582 */     if (RongContext.getInstance() == null) {
/*  583 */       throw new ExceptionInInitializerError("RongCloud SDK not init");
/*      */     }
/*  585 */     Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon().appendPath("subconversationlist").appendQueryParameter("type", conversationType.getName()).build();
/*      */ 
/*  590 */     context.startActivity(new Intent("android.intent.action.VIEW", uri));
/*      */   }
/*      */ 
/*      */   public static void setConversationBehaviorListener(ConversationBehaviorListener listener)
/*      */   {
/*  599 */     if (RongContext.getInstance() != null)
/*  600 */       RongContext.getInstance().setConversationBehaviorListener(listener);
/*      */   }
/*      */ 
/*      */   public static void setConversationListBehaviorListener(ConversationListBehaviorListener listener)
/*      */   {
/*  611 */     if (RongContext.getInstance() != null)
/*  612 */       RongContext.getInstance().setConversationListBehaviorListener(listener);
/*      */   }
/*      */ 
/*      */   public static void setPublicServiceBehaviorListener(PublicServiceBehaviorListener listener)
/*      */   {
/*  622 */     if (RongContext.getInstance() != null)
/*  623 */       RongContext.getInstance().setPublicServiceBehaviorListener(listener);
/*      */   }
/*      */ 
/*      */   public void startPrivateChat(Context context, String targetUserId, String title)
/*      */   {
/*  669 */     if ((context == null) || (TextUtils.isEmpty(targetUserId))) {
/*  670 */       throw new IllegalArgumentException();
/*      */     }
/*  672 */     if (RongContext.getInstance() == null) {
/*  673 */       throw new ExceptionInInitializerError("RongCloud SDK not init");
/*      */     }
/*  675 */     Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase()).appendQueryParameter("targetId", targetUserId).appendQueryParameter("title", title).build();
/*      */ 
/*  679 */     context.startActivity(new Intent("android.intent.action.VIEW", uri));
/*      */   }
/*      */ 
/*      */   public void startConversation(Context context, Conversation.ConversationType conversationType, String targetId, String title)
/*      */   {
/*  696 */     if ((context == null) || (TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/*  697 */       throw new IllegalArgumentException();
/*      */     }
/*  699 */     Uri uri = Uri.parse("rong://" + context.getApplicationInfo().processName).buildUpon().appendPath("conversation").appendPath(conversationType.getName().toLowerCase()).appendQueryParameter("targetId", targetId).appendQueryParameter("title", title).build();
/*      */ 
/*  703 */     context.startActivity(new Intent("android.intent.action.VIEW", uri));
/*      */   }
/*      */ 
/*      */   public void createDiscussionChat(Context context, List<String> targetUserIds, String title)
/*      */   {
/*  718 */     if ((context == null) || (targetUserIds == null) || (targetUserIds.size() == 0)) {
/*  719 */       throw new IllegalArgumentException();
/*      */     }
/*  721 */     RongIMClient.getInstance().createDiscussion(title, targetUserIds, new RongIMClient.CreateDiscussionCallback(context, targetUserIds, title)
/*      */     {
/*      */       public void onSuccess(String targetId)
/*      */       {
/*  725 */         Uri uri = Uri.parse("rong://" + this.val$context.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.DISCUSSION.getName().toLowerCase()).appendQueryParameter("targetIds", TextUtils.join(",", this.val$targetUserIds)).appendQueryParameter("delimiter", ",").appendQueryParameter("targetId", targetId).appendQueryParameter("title", this.val$title).build();
/*      */ 
/*  731 */         this.val$context.startActivity(new Intent("android.intent.action.VIEW", uri));
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/*  736 */         RLog.d(RongIM.TAG, "createDiscussionChat createDiscussion not success." + e);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void createDiscussionChat(Context context, List<String> targetUserIds, String title, RongIMClient.CreateDiscussionCallback callback)
/*      */   {
/*  752 */     if ((context == null) || (targetUserIds == null) || (targetUserIds.size() == 0)) {
/*  753 */       throw new IllegalArgumentException();
/*      */     }
/*  755 */     RongIMClient.getInstance().createDiscussion(title, targetUserIds, new RongIMClient.CreateDiscussionCallback(context, targetUserIds, title, callback)
/*      */     {
/*      */       public void onSuccess(String targetId)
/*      */       {
/*  759 */         Uri uri = Uri.parse("rong://" + this.val$context.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.DISCUSSION.getName().toLowerCase()).appendQueryParameter("targetIds", TextUtils.join(",", this.val$targetUserIds)).appendQueryParameter("delimiter", ",").appendQueryParameter("targetId", targetId).appendQueryParameter("title", this.val$title).build();
/*      */ 
/*  765 */         this.val$context.startActivity(new Intent("android.intent.action.VIEW", uri));
/*  766 */         if (this.val$callback != null)
/*  767 */           this.val$callback.onSuccess(targetId);
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/*  772 */         RLog.d(RongIM.TAG, "createDiscussionChat createDiscussion not success." + e);
/*  773 */         if (this.val$callback != null)
/*  774 */           this.val$callback.onError(e);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void startDiscussionChat(Context context, String targetDiscussionId, String title)
/*      */   {
/*  789 */     if ((context == null) || (TextUtils.isEmpty(targetDiscussionId))) {
/*  790 */       throw new IllegalArgumentException();
/*      */     }
/*      */ 
/*  793 */     Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.DISCUSSION.getName().toLowerCase()).appendQueryParameter("targetId", targetDiscussionId).appendQueryParameter("title", title).build();
/*      */ 
/*  797 */     context.startActivity(new Intent("android.intent.action.VIEW", uri));
/*      */   }
/*      */ 
/*      */   public void startGroupChat(Context context, String targetGroupId, String title)
/*      */   {
/*  809 */     if ((context == null) || (TextUtils.isEmpty(targetGroupId))) {
/*  810 */       throw new IllegalArgumentException();
/*      */     }
/*  812 */     if (RongContext.getInstance() == null) {
/*  813 */       throw new ExceptionInInitializerError("RongCloud SDK not init");
/*      */     }
/*  815 */     Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.GROUP.getName().toLowerCase()).appendQueryParameter("targetId", targetGroupId).appendQueryParameter("title", title).build();
/*      */ 
/*  819 */     context.startActivity(new Intent("android.intent.action.VIEW", uri));
/*      */   }
/*      */ 
/*      */   public void startChatRoomChat(Context context, String chatRoomId, boolean createIfNotExist)
/*      */   {
/*  838 */     if ((context == null) || (TextUtils.isEmpty(chatRoomId))) {
/*  839 */       throw new IllegalArgumentException();
/*      */     }
/*  841 */     if (RongContext.getInstance() == null) {
/*  842 */       throw new ExceptionInInitializerError("RongCloud SDK not init");
/*      */     }
/*  844 */     Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.CHATROOM.getName().toLowerCase()).appendQueryParameter("targetId", chatRoomId).build();
/*      */ 
/*  848 */     Intent intent = new Intent("android.intent.action.VIEW", uri);
/*  849 */     intent.putExtra("createIfNotExist", createIfNotExist);
/*  850 */     context.startActivity(intent);
/*      */   }
/*      */ 
/*      */   public void startCustomerServiceChat(Context context, String customerServiceId, String title, CSCustomServiceInfo customServiceInfo)
/*      */   {
/*  864 */     if ((context == null) || (TextUtils.isEmpty(customerServiceId))) {
/*  865 */       throw new IllegalArgumentException();
/*      */     }
/*  867 */     if (RongContext.getInstance() == null) {
/*  868 */       throw new ExceptionInInitializerError("RongCloud SDK not init");
/*      */     }
/*  870 */     Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.CUSTOMER_SERVICE.getName().toLowerCase()).appendQueryParameter("targetId", customerServiceId).appendQueryParameter("title", title).build();
/*      */ 
/*  874 */     Intent intent = new Intent("android.intent.action.VIEW", uri);
/*  875 */     intent.putExtra("customServiceInfo", customServiceInfo);
/*  876 */     context.startActivity(intent);
/*      */   }
/*      */ 
/*      */   public static void setUserInfoProvider(UserInfoProvider userInfoProvider, boolean isCacheUserInfo)
/*      */   {
/*  896 */     if (RongContext.getInstance() != null)
/*  897 */       RongContext.getInstance().setGetUserInfoProvider(userInfoProvider, isCacheUserInfo);
/*      */   }
/*      */ 
/*      */   public static void setGroupUserInfoProvider(GroupUserInfoProvider userInfoProvider, boolean isCacheUserInfo)
/*      */   {
/*  916 */     if (RongContext.getInstance() != null)
/*  917 */       RongContext.getInstance().setGroupUserInfoProvider(userInfoProvider, isCacheUserInfo);
/*      */   }
/*      */ 
/*      */   public static void setGroupInfoProvider(GroupInfoProvider groupInfoProvider, boolean isCacheGroupInfo)
/*      */   {
/*  935 */     if (RongContext.getInstance() != null)
/*  936 */       RongContext.getInstance().setGetGroupInfoProvider(groupInfoProvider, isCacheGroupInfo);
/*      */   }
/*      */ 
/*      */   public void refreshDiscussionCache(Discussion discussion)
/*      */   {
/*  947 */     if (discussion == null) {
/*  948 */       return;
/*      */     }
/*  950 */     RongUserInfoManager.getInstance().setDiscussionInfo(discussion);
/*      */   }
/*      */ 
/*      */   public void refreshUserInfoCache(UserInfo userInfo)
/*      */   {
/*  960 */     if (userInfo == null) {
/*  961 */       return;
/*      */     }
/*  963 */     RongUserInfoManager.getInstance().setUserInfo(userInfo);
/*      */   }
/*      */ 
/*      */   public void refreshGroupUserInfoCache(GroupUserInfo groupUserInfo)
/*      */   {
/*  973 */     if (groupUserInfo == null) {
/*  974 */       return;
/*      */     }
/*  976 */     RongUserInfoManager.getInstance().setGroupUserInfo(groupUserInfo);
/*      */   }
/*      */ 
/*      */   public void refreshGroupInfoCache(Group group)
/*      */   {
/*  985 */     if (group == null) {
/*  986 */       return;
/*      */     }
/*  988 */     RongUserInfoManager.getInstance().setGroupInfo(group);
/*      */   }
/*      */ 
/*      */   public void setSendMessageListener(OnSendMessageListener listener)
/*      */   {
/*  998 */     if (listener == null) {
/*  999 */       throw new IllegalArgumentException();
/*      */     }
/* 1001 */     if (RongContext.getInstance() == null) {
/* 1002 */       throw new ExceptionInInitializerError("RongCloud SDK not init");
/*      */     }
/* 1004 */     RongContext.getInstance().setOnSendMessageListener(listener);
/*      */   }
/*      */ 
/*      */   public void setMessageAttachedUserInfo(boolean state)
/*      */   {
/* 1315 */     RongContext.getInstance().setUserInfoAttachedState(state);
/*      */   }
/*      */ 
/*      */   public void setOnReceiveUnreadCountChangedListener(OnReceiveUnreadCountChangedListener listener, Conversation.ConversationType[] conversationTypes)
/*      */   {
/* 1333 */     if ((RongContext.getInstance() != null) && 
/* 1334 */       (listener != null) && (conversationTypes != null) && (conversationTypes.length > 0)) {
/* 1335 */       MessageCounter.Counter mCounter = new MessageCounter.Counter(ConversationTypeFilter.obtain(conversationTypes), listener)
/*      */       {
/*      */         public void onMessageIncreased(int count) {
/* 1338 */           this.val$listener.onMessageIncreased(count);
/*      */         }
/*      */       };
/* 1342 */       RongContext.getInstance().getMessageCounterLogic().registerMessageCounter(mCounter);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void startPublicServiceProfile(Context context, Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 1356 */     if ((context == null) || (conversationType == null) || (TextUtils.isEmpty(targetId))) {
/* 1357 */       throw new IllegalArgumentException();
/*      */     }
/* 1359 */     if (RongContext.getInstance() == null) {
/* 1360 */       throw new ExceptionInInitializerError("RongCloud SDK not init");
/*      */     }
/* 1362 */     Uri uri = Uri.parse("rong://" + context.getApplicationInfo().processName).buildUpon().appendPath("publicServiceProfile").appendPath(conversationType.getName().toLowerCase()).appendQueryParameter("targetId", targetId).build();
/*      */ 
/* 1365 */     Intent intent = new Intent("android.intent.action.VIEW", uri);
/* 1366 */     intent.setFlags(268435456);
/* 1367 */     context.startActivity(intent);
/*      */   }
/*      */ 
/*      */   public static void setPrimaryInputProvider(InputProvider.MainInputProvider provider)
/*      */   {
/* 1376 */     if (RongContext.getInstance() != null) {
/* 1377 */       if (provider == null) {
/* 1378 */         throw new IllegalArgumentException();
/*      */       }
/* 1380 */       RongContext.getInstance().setPrimaryInputProvider(provider);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void setSecondaryInputProvider(InputProvider.MainInputProvider provider)
/*      */   {
/* 1390 */     if (RongContext.getInstance() != null) {
/* 1391 */       if (provider == null) {
/* 1392 */         throw new IllegalArgumentException();
/*      */       }
/* 1394 */       RongContext.getInstance().setSecondaryInputProvider(provider);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void resetInputExtensionProvider(Conversation.ConversationType conversationType, InputProvider.ExtendProvider[] providers)
/*      */   {
/* 1405 */     if (RongContext.getInstance() != null) {
/* 1406 */       if (providers == null) {
/* 1407 */         throw new IllegalArgumentException();
/*      */       }
/* 1409 */       RongContext.getInstance().resetInputExtensionProvider(conversationType, providers);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void addInputExtensionProvider(Conversation.ConversationType conversationType, InputProvider.ExtendProvider[] providers)
/*      */   {
/* 1420 */     if (RongContext.getInstance() != null) {
/* 1421 */       if (providers == null) {
/* 1422 */         throw new IllegalArgumentException();
/*      */       }
/* 1424 */       RongContext.getInstance().addInputExtentionProvider(conversationType, providers);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerConversationTemplate(IContainerItemProvider.ConversationProvider provider)
/*      */   {
/* 1434 */     if (RongContext.getInstance() != null) {
/* 1435 */       if (provider == null) {
/* 1436 */         throw new IllegalArgumentException();
/*      */       }
/* 1438 */       RongContext.getInstance().registerConversationTemplate(provider);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void enableNewComingMessageIcon(boolean state)
/*      */   {
/* 1449 */     if (RongContext.getInstance() != null)
/* 1450 */       RongContext.getInstance().showNewMessageIcon(state);
/*      */   }
/*      */ 
/*      */   public void enableUnreadMessageIcon(boolean state)
/*      */   {
/* 1460 */     if (RongContext.getInstance() != null)
/* 1461 */       RongContext.getInstance().showUnreadMessageIcon(state);
/*      */   }
/*      */ 
/*      */   public void setMaxVoiceDurationg(int sec)
/*      */   {
/* 1471 */     AudioRecordManager.getInstance().setMaxVoiceDuration(sec);
/*      */   }
/*      */ 
/*      */   public RongIMClient.ConnectionStatusListener.ConnectionStatus getCurrentConnectionStatus()
/*      */   {
/* 1480 */     return RongIMClient.getInstance().getCurrentConnectionStatus();
/*      */   }
/*      */ 
/*      */   public void getConversationList(RongIMClient.ResultCallback<List<Conversation>> callback)
/*      */   {
/* 1490 */     uiReady = true;
/* 1491 */     RongIMClient.getInstance().getConversationList(callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public List<Conversation> getConversationList()
/*      */   {
/* 1503 */     uiReady = true;
/* 1504 */     return RongIMClient.getInstance().getConversationList();
/*      */   }
/*      */ 
/*      */   public void getConversationList(RongIMClient.ResultCallback<List<Conversation>> callback, Conversation.ConversationType[] types)
/*      */   {
/* 1514 */     uiReady = true;
/* 1515 */     RongIMClient.getInstance().getConversationList(callback, types);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public List<Conversation> getConversationList(Conversation.ConversationType[] types)
/*      */   {
/* 1527 */     uiReady = true;
/* 1528 */     return RongIMClient.getInstance().getConversationList(types);
/*      */   }
/*      */ 
/*      */   public void getConversation(Conversation.ConversationType type, String targetId, RongIMClient.ResultCallback<Conversation> callback)
/*      */   {
/* 1539 */     uiReady = true;
/* 1540 */     RongIMClient.getInstance().getConversation(type, targetId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Conversation getConversation(Conversation.ConversationType type, String targetId)
/*      */   {
/* 1553 */     uiReady = true;
/* 1554 */     return RongIMClient.getInstance().getConversation(type, targetId);
/*      */   }
/*      */ 
/*      */   public void removeConversation(Conversation.ConversationType type, String targetId, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/* 1567 */     RongIMClient.getInstance().removeConversation(type, targetId, new RongIMClient.ResultCallback(callback, type, targetId)
/*      */     {
/*      */       public void onSuccess(Boolean bool)
/*      */       {
/* 1571 */         if (this.val$callback != null) {
/* 1572 */           this.val$callback.onSuccess(bool);
/*      */         }
/* 1574 */         if (bool.booleanValue())
/* 1575 */           RongContext.getInstance().getEventBus().post(new Event.ConversationRemoveEvent(this.val$type, this.val$targetId));
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 1581 */         if (this.val$callback != null)
/* 1582 */           this.val$callback.onFail(e);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean removeConversation(Conversation.ConversationType type, String targetId)
/*      */   {
/* 1599 */     boolean result = RongIMClient.getInstance().removeConversation(type, targetId);
/*      */ 
/* 1601 */     if (result) {
/* 1602 */       RongContext.getInstance().getEventBus().post(new Event.ConversationRemoveEvent(type, targetId));
/*      */     }
/* 1604 */     return result;
/*      */   }
/*      */ 
/*      */   public void setConversationToTop(Conversation.ConversationType type, String id, boolean isTop, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/* 1616 */     RongIMClient.getInstance().setConversationToTop(type, id, isTop, new RongIMClient.ResultCallback(callback, type, id, isTop)
/*      */     {
/*      */       public void onSuccess(Boolean bool) {
/* 1619 */         if (this.val$callback != null) {
/* 1620 */           this.val$callback.onSuccess(bool);
/*      */         }
/* 1622 */         if (bool.booleanValue())
/* 1623 */           RongContext.getInstance().getEventBus().post(new Event.ConversationTopEvent(this.val$type, this.val$id, this.val$isTop));
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 1628 */         if (this.val$callback != null)
/* 1629 */           this.val$callback.onFail(e);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean setConversationToTop(Conversation.ConversationType conversationType, String targetId, boolean isTop)
/*      */   {
/* 1645 */     boolean result = RongIMClient.getInstance().setConversationToTop(conversationType, targetId, isTop);
/*      */ 
/* 1647 */     if (result) {
/* 1648 */       RongContext.getInstance().getEventBus().post(new Event.ConversationTopEvent(conversationType, targetId, isTop));
/*      */     }
/*      */ 
/* 1651 */     return result;
/*      */   }
/*      */ 
/*      */   public void getTotalUnreadCount(RongIMClient.ResultCallback<Integer> callback)
/*      */   {
/* 1660 */     RongIMClient.getInstance().getTotalUnreadCount(new RongIMClient.ResultCallback(callback)
/*      */     {
/*      */       public void onSuccess(Integer integer) {
/* 1663 */         if (this.val$callback != null)
/* 1664 */           this.val$callback.onSuccess(integer);
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 1669 */         if (this.val$callback != null)
/* 1670 */           this.val$callback.onFail(e);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getTotalUnreadCount()
/*      */   {
/* 1683 */     return RongIMClient.getInstance().getTotalUnreadCount();
/*      */   }
/*      */ 
/*      */   public void getUnreadCount(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<Integer> callback)
/*      */   {
/* 1694 */     RongIMClient.getInstance().getUnreadCount(conversationType, targetId, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getUnreadCount(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 1707 */     return RongIMClient.getInstance().getUnreadCount(conversationType, targetId);
/*      */   }
/*      */ 
/*      */   public void getUnreadCount(RongIMClient.ResultCallback<Integer> callback, Conversation.ConversationType[] conversationTypes)
/*      */   {
/* 1718 */     RongIMClient.getInstance().getUnreadCount(callback, conversationTypes);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getUnreadCount(Conversation.ConversationType[] conversationTypes)
/*      */   {
/* 1730 */     return RongIMClient.getInstance().getUnreadCount(conversationTypes);
/*      */   }
/*      */ 
/*      */   public void getUnreadCount(Conversation.ConversationType[] conversationTypes, RongIMClient.ResultCallback<Integer> callback)
/*      */   {
/* 1740 */     RongIMClient.getInstance().getUnreadCount(conversationTypes, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public List<Message> getLatestMessages(Conversation.ConversationType conversationType, String targetId, int count)
/*      */   {
/* 1754 */     return RongIMClient.getInstance().getLatestMessages(conversationType, targetId, count);
/*      */   }
/*      */ 
/*      */   public void getLatestMessages(Conversation.ConversationType conversationType, String targetId, int count, RongIMClient.ResultCallback<List<Message>> callback)
/*      */   {
/* 1766 */     RongIMClient.getInstance().getLatestMessages(conversationType, targetId, count, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public List<Message> getHistoryMessages(Conversation.ConversationType conversationType, String targetId, int oldestMessageId, int count)
/*      */   {
/* 1781 */     return RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, oldestMessageId, count);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public List<Message> getHistoryMessages(Conversation.ConversationType conversationType, String targetId, String objectName, int oldestMessageId, int count)
/*      */   {
/* 1797 */     return RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, objectName, oldestMessageId, count);
/*      */   }
/*      */ 
/*      */   public void getHistoryMessages(Conversation.ConversationType conversationType, String targetId, String objectName, int oldestMessageId, int count, RongIMClient.ResultCallback<List<Message>> callback)
/*      */   {
/* 1811 */     RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, objectName, oldestMessageId, count, callback);
/*      */   }
/*      */ 
/*      */   public void getHistoryMessages(Conversation.ConversationType conversationType, String targetId, int oldestMessageId, int count, RongIMClient.ResultCallback<List<Message>> callback)
/*      */   {
/* 1824 */     RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, oldestMessageId, count, callback);
/*      */   }
/*      */ 
/*      */   public void getRemoteHistoryMessages(Conversation.ConversationType conversationType, String targetId, long dataTime, int count, RongIMClient.ResultCallback<List<Message>> callback)
/*      */   {
/* 1838 */     RongIMClient.getInstance().getRemoteHistoryMessages(conversationType, targetId, dataTime, count, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean deleteMessages(int[] messageIds)
/*      */   {
/* 1850 */     Boolean bool = Boolean.valueOf(RongIMClient.getInstance().deleteMessages(messageIds));
/*      */ 
/* 1852 */     if (bool.booleanValue()) {
/* 1853 */       RongContext.getInstance().getEventBus().post(new Event.MessageDeleteEvent(messageIds));
/*      */     }
/* 1855 */     return bool.booleanValue();
/*      */   }
/*      */ 
/*      */   public void deleteMessages(int[] messageIds, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/* 1865 */     RongIMClient.getInstance().deleteMessages(messageIds, new RongIMClient.ResultCallback(messageIds, callback)
/*      */     {
/*      */       public void onSuccess(Boolean bool) {
/* 1868 */         if (bool.booleanValue()) {
/* 1869 */           RongContext.getInstance().getEventBus().post(new Event.MessageDeleteEvent(this.val$messageIds));
/*      */         }
/* 1871 */         if (this.val$callback != null)
/* 1872 */           this.val$callback.onCallback(bool);
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 1877 */         if (this.val$callback != null)
/* 1878 */           this.val$callback.onError(e);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void deleteMessages(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/* 1893 */     RongIMClient.getInstance().deleteMessages(conversationType, targetId, new RongIMClient.ResultCallback(conversationType, targetId, callback)
/*      */     {
/*      */       public void onSuccess(Boolean bool) {
/* 1896 */         if (bool.booleanValue()) {
/* 1897 */           RongContext.getInstance().getEventBus().post(new Event.MessagesClearEvent(this.val$conversationType, this.val$targetId));
/*      */         }
/* 1899 */         if (this.val$callback != null)
/* 1900 */           this.val$callback.onCallback(bool);
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 1905 */         if (this.val$callback != null)
/* 1906 */           this.val$callback.onError(e);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean clearMessages(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 1921 */     boolean bool = RongIMClient.getInstance().clearMessages(conversationType, targetId);
/*      */ 
/* 1923 */     if (bool) {
/* 1924 */       RongContext.getInstance().getEventBus().post(new Event.MessagesClearEvent(conversationType, targetId));
/*      */     }
/* 1926 */     return bool;
/*      */   }
/*      */ 
/*      */   public void clearMessages(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/* 1937 */     RongIMClient.getInstance().clearMessages(conversationType, targetId, new RongIMClient.ResultCallback(conversationType, targetId, callback)
/*      */     {
/*      */       public void onSuccess(Boolean bool) {
/* 1940 */         if (bool.booleanValue()) {
/* 1941 */           RongContext.getInstance().getEventBus().post(new Event.MessagesClearEvent(this.val$conversationType, this.val$targetId));
/*      */         }
/* 1943 */         if (this.val$callback != null)
/* 1944 */           this.val$callback.onCallback(bool);
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 1949 */         if (this.val$callback != null)
/* 1950 */           this.val$callback.onError(e);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean clearMessagesUnreadStatus(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 1965 */     boolean result = RongIMClient.getInstance().clearMessagesUnreadStatus(conversationType, targetId);
/* 1966 */     if (result) {
/* 1967 */       RongContext.getInstance().getEventBus().post(new Event.ConversationUnreadEvent(conversationType, targetId));
/*      */     }
/*      */ 
/* 1970 */     return result;
/*      */   }
/*      */ 
/*      */   public void clearMessagesUnreadStatus(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/* 1981 */     RongIMClient.getInstance().clearMessagesUnreadStatus(conversationType, targetId, new RongIMClient.ResultCallback(callback, conversationType, targetId)
/*      */     {
/*      */       public void onSuccess(Boolean bool) {
/* 1984 */         if (this.val$callback != null)
/* 1985 */           this.val$callback.onSuccess(bool);
/* 1986 */         RongContext.getInstance().getEventBus().post(new Event.ConversationUnreadEvent(this.val$conversationType, this.val$targetId));
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 1991 */         if (this.val$callback != null)
/* 1992 */           this.val$callback.onError(e);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean setMessageExtra(int messageId, String value)
/*      */   {
/* 2007 */     return RongIMClient.getInstance().setMessageExtra(messageId, value);
/*      */   }
/*      */ 
/*      */   public void setMessageExtra(int messageId, String value, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/* 2018 */     RongIMClient.getInstance().setMessageExtra(messageId, value, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean setMessageReceivedStatus(int messageId, Message.ReceivedStatus receivedStatus)
/*      */   {
/* 2031 */     return RongIMClient.getInstance().setMessageReceivedStatus(messageId, receivedStatus);
/*      */   }
/*      */ 
/*      */   public void setMessageReceivedStatus(int messageId, Message.ReceivedStatus receivedStatus, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/* 2042 */     RongIMClient.getInstance().setMessageReceivedStatus(messageId, receivedStatus, callback);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean setMessageSentStatus(int messageId, Message.SentStatus sentStatus)
/*      */   {
/* 2055 */     boolean result = RongIMClient.getInstance().setMessageSentStatus(messageId, sentStatus);
/*      */ 
/* 2057 */     if (result) {
/* 2058 */       RongContext.getInstance().getEventBus().post(new Event.MessageSentStatusEvent(messageId, sentStatus));
/*      */     }
/* 2060 */     return result;
/*      */   }
/*      */ 
/*      */   public void setMessageSentStatus(int messageId, Message.SentStatus sentStatus, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/* 2071 */     RongIMClient.getInstance().setMessageSentStatus(messageId, sentStatus, new RongIMClient.ResultCallback(callback, messageId, sentStatus)
/*      */     {
/*      */       public void onSuccess(Boolean bool) {
/* 2074 */         if (this.val$callback != null) {
/* 2075 */           this.val$callback.onSuccess(bool);
/*      */         }
/* 2077 */         if (bool.booleanValue())
/* 2078 */           RongContext.getInstance().getEventBus().post(new Event.MessageSentStatusEvent(this.val$messageId, this.val$sentStatus));
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 2083 */         if (this.val$callback != null)
/* 2084 */           this.val$callback.onError(e);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public String getTextMessageDraft(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 2099 */     return RongIMClient.getInstance().getTextMessageDraft(conversationType, targetId);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean saveTextMessageDraft(Conversation.ConversationType conversationType, String targetId, String content)
/*      */   {
/* 2113 */     return RongIMClient.getInstance().saveTextMessageDraft(conversationType, targetId, content);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean clearTextMessageDraft(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 2126 */     return RongIMClient.getInstance().clearTextMessageDraft(conversationType, targetId);
/*      */   }
/*      */ 
/*      */   public void getTextMessageDraft(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<String> callback)
/*      */   {
/* 2137 */     RongIMClient.getInstance().getTextMessageDraft(conversationType, targetId, callback);
/*      */   }
/*      */ 
/*      */   public void saveTextMessageDraft(Conversation.ConversationType conversationType, String targetId, String content, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/* 2149 */     RongIMClient.getInstance().saveTextMessageDraft(conversationType, targetId, content, callback);
/*      */   }
/*      */ 
/*      */   public void clearTextMessageDraft(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<Boolean> callback)
/*      */   {
/* 2160 */     RongIMClient.getInstance().clearTextMessageDraft(conversationType, targetId, callback);
/*      */   }
/*      */ 
/*      */   public void getDiscussion(String discussionId, RongIMClient.ResultCallback<Discussion> callback)
/*      */   {
/* 2170 */     RongIMClient.getInstance().getDiscussion(discussionId, callback);
/*      */   }
/*      */ 
/*      */   public void setDiscussionName(String discussionId, String name, RongIMClient.OperationCallback callback)
/*      */   {
/* 2181 */     RongIMClient.getInstance().setDiscussionName(discussionId, name, new RongIMClient.OperationCallback(callback, discussionId, name)
/*      */     {
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 2185 */         if (this.val$callback != null)
/* 2186 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */ 
/*      */       public void onSuccess()
/*      */       {
/* 2192 */         if (this.val$callback != null) {
/* 2193 */           RongUserInfoManager.getInstance().setDiscussionInfo(new Discussion(this.val$discussionId, this.val$name));
/* 2194 */           this.val$callback.onSuccess();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void createDiscussion(String name, List<String> userIdList, RongIMClient.CreateDiscussionCallback callback)
/*      */   {
/* 2208 */     RongIMClient.getInstance().createDiscussion(name, userIdList, new RongIMClient.CreateDiscussionCallback(name, userIdList, callback)
/*      */     {
/*      */       public void onSuccess(String discussionId) {
/* 2211 */         RongContext.getInstance().getEventBus().post(new Event.CreateDiscussionEvent(discussionId, this.val$name, this.val$userIdList));
/*      */ 
/* 2213 */         if (this.val$callback != null)
/* 2214 */           this.val$callback.onCallback(discussionId);
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 2219 */         if (this.val$callback != null)
/* 2220 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void addMemberToDiscussion(String discussionId, List<String> userIdList, RongIMClient.OperationCallback callback)
/*      */   {
/* 2233 */     RongIMClient.getInstance().addMemberToDiscussion(discussionId, userIdList, new RongIMClient.OperationCallback(discussionId, userIdList, callback)
/*      */     {
/*      */       public void onSuccess() {
/* 2236 */         RongContext.getInstance().getEventBus().post(new Event.AddMemberToDiscussionEvent(this.val$discussionId, this.val$userIdList));
/*      */ 
/* 2238 */         if (this.val$callback != null)
/* 2239 */           this.val$callback.onSuccess();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 2245 */         if (this.val$callback != null)
/* 2246 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void removeMemberFromDiscussion(String discussionId, String userId, RongIMClient.OperationCallback callback)
/*      */   {
/* 2264 */     RongIMClient.getInstance().removeMemberFromDiscussion(discussionId, userId, new RongIMClient.OperationCallback(discussionId, userId, callback)
/*      */     {
/*      */       public void onSuccess() {
/* 2267 */         RongContext.getInstance().getEventBus().post(new Event.RemoveMemberFromDiscussionEvent(this.val$discussionId, this.val$userId));
/*      */ 
/* 2269 */         if (this.val$callback != null)
/* 2270 */           this.val$callback.onSuccess();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 2275 */         if (this.val$callback != null)
/* 2276 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void quitDiscussion(String discussionId, RongIMClient.OperationCallback callback)
/*      */   {
/* 2288 */     RongIMClient.getInstance().quitDiscussion(discussionId, new RongIMClient.OperationCallback(discussionId, callback)
/*      */     {
/*      */       public void onSuccess() {
/* 2291 */         RongContext.getInstance().getEventBus().post(new Event.QuitDiscussionEvent(this.val$discussionId));
/*      */ 
/* 2293 */         if (this.val$callback != null)
/* 2294 */           this.val$callback.onSuccess();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 2300 */         if (this.val$callback != null)
/* 2301 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void insertMessage(Conversation.ConversationType type, String targetId, String senderUserId, MessageContent content, RongIMClient.ResultCallback<Message> callback)
/*      */   {
/* 2318 */     MessageTag tag = (MessageTag)content.getClass().getAnnotation(MessageTag.class);
/*      */ 
/* 2320 */     if ((tag != null) && ((tag.flag() & 0x1) == 1))
/*      */     {
/* 2322 */       RongIMClient.getInstance().insertMessage(type, targetId, senderUserId, content, new RongIMClient.ResultCallback(callback)
/*      */       {
/*      */         public void onSuccess(Message message)
/*      */         {
/* 2326 */           if (this.val$callback != null) {
/* 2327 */             this.val$callback.onSuccess(message);
/*      */           }
/* 2329 */           RongContext.getInstance().getEventBus().post(message);
/*      */         }
/*      */ 
/*      */         public void onError(RongIMClient.ErrorCode e)
/*      */         {
/* 2335 */           if (this.val$callback != null) {
/* 2336 */             this.val$callback.onError(e);
/*      */           }
/* 2338 */           RongContext.getInstance().getEventBus().post(e);
/*      */         } } );
/*      */     }
/* 2342 */     else RLog.e(TAG, "insertMessage Message is missing MessageTag.ISPERSISTED");
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Message insertMessage(Conversation.ConversationType type, String targetId, String senderUserId, MessageContent content)
/*      */   {
/* 2358 */     MessageTag tag = (MessageTag)content.getClass().getAnnotation(MessageTag.class);
/*      */     Message message;
/*      */     Message message;
/* 2362 */     if ((tag != null) && ((tag.flag() & 0x1) == 1)) {
/* 2363 */       message = RongIMClient.getInstance().insertMessage(type, targetId, senderUserId, content);
/*      */     } else {
/* 2365 */       message = Message.obtain(targetId, type, content);
/* 2366 */       RLog.e(TAG, "insertMessage Message is missing MessageTag.ISPERSISTED");
/*      */     }
/*      */ 
/* 2369 */     RongContext.getInstance().getEventBus().post(message);
/*      */ 
/* 2371 */     return message;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Message sendMessage(Conversation.ConversationType type, String targetId, MessageContent content, String pushContent, String pushData, RongIMClient.SendMessageCallback callback)
/*      */   {
/* 2390 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/*      */ 
/* 2392 */     Message messageTemp = Message.obtain(targetId, type, content);
/*      */ 
/* 2394 */     Message temp = filterSendMessage(messageTemp);
/* 2395 */     if (temp == null) {
/* 2396 */       return null;
/*      */     }
/* 2398 */     if (temp != messageTemp) {
/* 2399 */       messageTemp = temp;
/*      */     }
/* 2401 */     content = messageTemp.getContent();
/*      */ 
/* 2403 */     content = setMessageAttachedUserInfo(content);
/*      */ 
/* 2405 */     Message message = RongIMClient.getInstance().sendMessage(type, targetId, content, pushContent, pushData, new RongIMClient.SendMessageCallback(result, callback)
/*      */     {
/*      */       public void onSuccess(Integer messageId) {
/* 2408 */         if (this.val$result.t == null)
/* 2409 */           return;
/* 2410 */         ((Message)this.val$result.t).setSentStatus(Message.SentStatus.SENT);
/* 2411 */         long tt = RongIMClient.getInstance().getSendTimeByMessageId(messageId.intValue());
/* 2412 */         if (tt != 0L) {
/* 2413 */           ((Message)this.val$result.t).setSentTime(tt);
/*      */         }
/* 2415 */         RongIM.this.filterSentMessage((Message)this.val$result.t, null);
/*      */ 
/* 2418 */         if (this.val$callback != null)
/* 2419 */           this.val$callback.onSuccess(messageId);
/*      */       }
/*      */ 
/*      */       public void onError(Integer messageId, RongIMClient.ErrorCode errorCode)
/*      */       {
/* 2424 */         if (this.val$result.t == null)
/* 2425 */           return;
/* 2426 */         ((Message)this.val$result.t).setSentStatus(Message.SentStatus.FAILED);
/* 2427 */         RongIM.this.filterSentMessage((Message)this.val$result.t, errorCode);
/*      */ 
/* 2430 */         if (this.val$callback != null)
/* 2431 */           this.val$callback.onError(messageId, errorCode);
/*      */       }
/*      */     });
/* 2435 */     MessageTag tag = (MessageTag)content.getClass().getAnnotation(MessageTag.class);
/*      */ 
/* 2437 */     if ((tag != null) && ((tag.flag() & 0x1) == 1)) {
/* 2438 */       RongContext.getInstance().getEventBus().post(message);
/*      */     }
/* 2440 */     result.t = message;
/*      */ 
/* 2442 */     return message;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void sendMessage(Conversation.ConversationType type, String targetId, MessageContent content, String pushContent, String pushData, RongIMClient.SendMessageCallback callback, RongIMClient.ResultCallback<Message> resultCallback)
/*      */   {
/* 2462 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/*      */ 
/* 2464 */     Message message = Message.obtain(targetId, type, content);
/*      */ 
/* 2466 */     Message temp = filterSendMessage(message);
/* 2467 */     if (temp == null) {
/* 2468 */       return;
/*      */     }
/* 2470 */     if (temp != message) {
/* 2471 */       message = temp;
/*      */     }
/* 2473 */     content = message.getContent();
/*      */ 
/* 2475 */     content = setMessageAttachedUserInfo(content);
/* 2476 */     RongIMClient.getInstance().sendMessage(type, targetId, content, pushContent, pushData, new RongIMClient.SendMessageCallback(result, callback)
/*      */     {
/*      */       public void onSuccess(Integer messageId) {
/* 2479 */         if (this.val$result.t == null)
/* 2480 */           return;
/* 2481 */         ((Message)this.val$result.t).setSentStatus(Message.SentStatus.SENT);
/* 2482 */         long tt = RongIMClient.getInstance().getSendTimeByMessageId(messageId.intValue());
/* 2483 */         if (tt != 0L) {
/* 2484 */           ((Message)this.val$result.t).setSentTime(tt);
/*      */         }
/* 2486 */         RongIM.this.filterSentMessage((Message)this.val$result.t, null);
/*      */ 
/* 2490 */         if (this.val$callback != null)
/* 2491 */           this.val$callback.onSuccess(messageId);
/*      */       }
/*      */ 
/*      */       public void onError(Integer messageId, RongIMClient.ErrorCode errorCode)
/*      */       {
/* 2496 */         if (this.val$result.t == null)
/* 2497 */           return;
/* 2498 */         ((Message)this.val$result.t).setSentStatus(Message.SentStatus.FAILED);
/* 2499 */         RongIM.this.filterSentMessage((Message)this.val$result.t, errorCode);
/*      */ 
/* 2503 */         if (this.val$callback != null)
/* 2504 */           this.val$callback.onError(messageId, errorCode);
/*      */       }
/*      */     }
/*      */     , new RongIMClient.ResultCallback(result, resultCallback)
/*      */     {
/*      */       public void onSuccess(Message message)
/*      */       {
/* 2510 */         MessageTag tag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
/*      */ 
/* 2512 */         if ((tag != null) && ((tag.flag() & 0x1) == 1)) {
/* 2513 */           RongContext.getInstance().getEventBus().post(message);
/*      */         }
/*      */ 
/* 2516 */         this.val$result.t = message;
/*      */ 
/* 2518 */         if (this.val$resultCallback != null)
/* 2519 */           this.val$resultCallback.onSuccess(message);
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 2524 */         RongContext.getInstance().getEventBus().post(e);
/*      */ 
/* 2526 */         if (this.val$resultCallback != null)
/* 2527 */           this.val$resultCallback.onError(e);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void sendMessage(Message message, String pushContent, String pushData, RongIMClient.SendMessageCallback callback, RongIMClient.ResultCallback<Message> resultCallback)
/*      */   {
/* 2546 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/*      */ 
/* 2548 */     Message temp = filterSendMessage(message);
/* 2549 */     if (temp == null) {
/* 2550 */       return;
/*      */     }
/* 2552 */     if (temp != message) {
/* 2553 */       message = temp;
/*      */     }
/* 2555 */     message.setContent(setMessageAttachedUserInfo(message.getContent()));
/*      */ 
/* 2557 */     RongIMClient.getInstance().sendMessage(message, pushContent, pushData, new RongIMClient.SendMessageCallback(result, callback)
/*      */     {
/*      */       public void onSuccess(Integer messageId) {
/* 2560 */         if (this.val$result.t == null)
/* 2561 */           return;
/* 2562 */         ((Message)this.val$result.t).setSentStatus(Message.SentStatus.SENT);
/* 2563 */         long tt = RongIMClient.getInstance().getSendTimeByMessageId(messageId.intValue());
/* 2564 */         if (tt != 0L) {
/* 2565 */           ((Message)this.val$result.t).setSentTime(tt);
/*      */         }
/* 2567 */         RongIM.this.filterSentMessage((Message)this.val$result.t, null);
/*      */ 
/* 2569 */         if (this.val$callback != null)
/* 2570 */           this.val$callback.onSuccess(messageId);
/*      */       }
/*      */ 
/*      */       public void onError(Integer messageId, RongIMClient.ErrorCode errorCode)
/*      */       {
/* 2576 */         if (this.val$result.t == null) {
/* 2577 */           return;
/*      */         }
/* 2579 */         ((Message)this.val$result.t).setSentStatus(Message.SentStatus.FAILED);
/* 2580 */         RongIM.this.filterSentMessage((Message)this.val$result.t, errorCode);
/*      */ 
/* 2584 */         if (this.val$callback != null)
/* 2585 */           this.val$callback.onError(messageId, errorCode);
/*      */       }
/*      */     }
/*      */     , new RongIMClient.ResultCallback(result, resultCallback)
/*      */     {
/*      */       public void onSuccess(Message message)
/*      */       {
/* 2591 */         this.val$result.t = message;
/* 2592 */         MessageTag tag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
/*      */ 
/* 2594 */         if ((tag != null) && ((tag.flag() & 0x1) == 1)) {
/* 2595 */           RongContext.getInstance().getEventBus().post(message);
/*      */         }
/*      */ 
/* 2598 */         if (this.val$resultCallback != null)
/* 2599 */           this.val$resultCallback.onSuccess(message);
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 2604 */         RongContext.getInstance().getEventBus().post(e);
/* 2605 */         if (this.val$resultCallback != null)
/* 2606 */           this.val$resultCallback.onError(e);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Message sendMessage(Message message, String pushContent, String pushData, RongIMClient.SendMessageCallback callback)
/*      */   {
/* 2626 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/*      */ 
/* 2628 */     Message temp = filterSendMessage(message);
/*      */ 
/* 2630 */     if (temp == null) {
/* 2631 */       return null;
/*      */     }
/* 2633 */     if (temp != message) {
/* 2634 */       message = temp;
/*      */     }
/* 2636 */     message.setContent(setMessageAttachedUserInfo(message.getContent()));
/*      */ 
/* 2638 */     Message msg = RongIMClient.getInstance().sendMessage(message, pushContent, pushData, new RongIMClient.SendMessageCallback(result, callback)
/*      */     {
/*      */       public void onSuccess(Integer messageId) {
/* 2641 */         if (this.val$result.t == null)
/* 2642 */           return;
/* 2643 */         ((Message)this.val$result.t).setSentStatus(Message.SentStatus.SENT);
/* 2644 */         long tt = RongIMClient.getInstance().getSendTimeByMessageId(messageId.intValue());
/* 2645 */         if (tt != 0L) {
/* 2646 */           ((Message)this.val$result.t).setSentTime(tt);
/*      */         }
/* 2648 */         RongIM.this.filterSentMessage((Message)this.val$result.t, null);
/*      */ 
/* 2650 */         if (this.val$callback != null)
/* 2651 */           this.val$callback.onSuccess(messageId);
/*      */       }
/*      */ 
/*      */       public void onError(Integer messageId, RongIMClient.ErrorCode errorCode)
/*      */       {
/* 2656 */         if (this.val$result.t == null)
/* 2657 */           return;
/* 2658 */         ((Message)this.val$result.t).setSentStatus(Message.SentStatus.FAILED);
/* 2659 */         RongIM.this.filterSentMessage((Message)this.val$result.t, errorCode);
/*      */ 
/* 2663 */         if (this.val$callback != null)
/* 2664 */           this.val$callback.onError(messageId, errorCode);
/*      */       }
/*      */     });
/* 2668 */     MessageTag tag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
/*      */ 
/* 2670 */     if ((tag != null) && ((tag.flag() & 0x1) == 1)) {
/* 2671 */       EventBus.getDefault().post(msg);
/*      */     }
/* 2673 */     result.t = msg;
/*      */ 
/* 2675 */     return msg;
/*      */   }
/*      */ 
/*      */   public void sendMessage(Message message, String pushContent, String pushData, IRongCallback.ISendMessageCallback callback)
/*      */   {
/* 2691 */     Message filterMsg = filterSendMessage(message);
/* 2692 */     if (filterMsg == null) {
/* 2693 */       RLog.w(TAG, "sendMessage: 因在 onSend 中消息被过滤为 null，取消发送。");
/* 2694 */       return;
/*      */     }
/* 2696 */     if (filterMsg != message) {
/* 2697 */       message = filterMsg;
/*      */     }
/* 2699 */     message.setContent(setMessageAttachedUserInfo(message.getContent()));
/* 2700 */     RongIMClient.getInstance().sendMessage(message, pushContent, pushData, new IRongCallback.ISendMessageCallback(callback)
/*      */     {
/*      */       public void onAttached(Message message) {
/* 2703 */         MessageTag tag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
/* 2704 */         if ((tag != null) && ((tag.flag() & 0x1) == 1)) {
/* 2705 */           RongContext.getInstance().getEventBus().post(message);
/*      */         }
/*      */ 
/* 2708 */         if (this.val$callback != null)
/* 2709 */           this.val$callback.onAttached(message);
/*      */       }
/*      */ 
/*      */       public void onSuccess(Message message)
/*      */       {
/* 2715 */         RongIM.this.filterSentMessage(message, null);
/* 2716 */         if (this.val$callback != null)
/* 2717 */           this.val$callback.onSuccess(message);
/*      */       }
/*      */ 
/*      */       public void onError(Message message, RongIMClient.ErrorCode errorCode)
/*      */       {
/* 2723 */         RongIM.this.filterSentMessage(message, errorCode);
/* 2724 */         if (this.val$callback != null)
/* 2725 */           this.val$callback.onError(message, errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void sendLocationMessage(Message message, String pushContent, String pushData, IRongCallback.ISendMessageCallback sendMessageCallback)
/*      */   {
/* 2744 */     Message filterMsg = filterSendMessage(message);
/* 2745 */     if (filterMsg == null) {
/* 2746 */       RLog.w(TAG, "sendLocationMessage: 因在 onSend 中消息被过滤为 null，取消发送。");
/* 2747 */       return;
/*      */     }
/* 2749 */     if (filterMsg != message) {
/* 2750 */       message = filterMsg;
/*      */     }
/* 2752 */     message.setContent(setMessageAttachedUserInfo(message.getContent()));
/* 2753 */     RongIMClient.getInstance().sendLocationMessage(message, pushContent, pushData, new IRongCallback.ISendMessageCallback(sendMessageCallback)
/*      */     {
/*      */       public void onAttached(Message message) {
/* 2756 */         MessageTag tag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
/* 2757 */         if ((tag != null) && ((tag.flag() & 0x1) == 1)) {
/* 2758 */           RongContext.getInstance().getEventBus().post(message);
/*      */         }
/*      */ 
/* 2761 */         if (this.val$sendMessageCallback != null)
/* 2762 */           this.val$sendMessageCallback.onAttached(message);
/*      */       }
/*      */ 
/*      */       public void onSuccess(Message message)
/*      */       {
/* 2768 */         RongIM.this.filterSentMessage(message, null);
/* 2769 */         if (this.val$sendMessageCallback != null)
/* 2770 */           this.val$sendMessageCallback.onSuccess(message);
/*      */       }
/*      */ 
/*      */       public void onError(Message message, RongIMClient.ErrorCode errorCode)
/*      */       {
/* 2776 */         RongIM.this.filterSentMessage(message, errorCode);
/* 2777 */         if (this.val$sendMessageCallback != null)
/* 2778 */           this.val$sendMessageCallback.onError(message, errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void sendImageMessage(Conversation.ConversationType type, String targetId, MessageContent content, String pushContent, String pushData, RongIMClient.SendImageMessageCallback callback)
/*      */   {
/* 2800 */     Message message = Message.obtain(targetId, type, content);
/*      */ 
/* 2802 */     Message temp = filterSendMessage(message);
/* 2803 */     if (temp == null) {
/* 2804 */       return;
/*      */     }
/* 2806 */     if (temp != message) {
/* 2807 */       message = temp;
/*      */     }
/* 2809 */     content = message.getContent();
/*      */ 
/* 2811 */     content = setMessageAttachedUserInfo(content);
/*      */ 
/* 2813 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 2814 */     result.t = new Event.OnReceiveMessageProgressEvent();
/*      */ 
/* 2816 */     RongIMClient.SendImageMessageCallback sendMessageCallback = new RongIMClient.SendImageMessageCallback(callback, result)
/*      */     {
/*      */       public void onAttached(Message message)
/*      */       {
/* 2821 */         RongContext.getInstance().getEventBus().post(message);
/*      */ 
/* 2823 */         if (this.val$callback != null)
/* 2824 */           this.val$callback.onAttached(message);
/*      */       }
/*      */ 
/*      */       public void onProgress(Message message, int progress)
/*      */       {
/* 2829 */         if (this.val$result.t == null)
/* 2830 */           return;
/* 2831 */         ((Event.OnReceiveMessageProgressEvent)this.val$result.t).setMessage(message);
/* 2832 */         ((Event.OnReceiveMessageProgressEvent)this.val$result.t).setProgress(progress);
/* 2833 */         RongContext.getInstance().getEventBus().post(this.val$result.t);
/*      */ 
/* 2835 */         if (this.val$callback != null)
/* 2836 */           this.val$callback.onProgress(message, progress);
/*      */       }
/*      */ 
/*      */       public void onError(Message message, RongIMClient.ErrorCode errorCode)
/*      */       {
/* 2842 */         RongIM.this.filterSentMessage(message, errorCode);
/*      */ 
/* 2844 */         if (this.val$callback != null)
/* 2845 */           this.val$callback.onError(message, errorCode);
/*      */       }
/*      */ 
/*      */       public void onSuccess(Message message)
/*      */       {
/* 2851 */         RongIM.this.filterSentMessage(message, null);
/*      */ 
/* 2853 */         if (this.val$callback != null)
/* 2854 */           this.val$callback.onSuccess(message);
/*      */       }
/*      */     };
/* 2858 */     RongIMClient.getInstance().sendImageMessage(type, targetId, content, pushContent, pushData, sendMessageCallback);
/*      */   }
/*      */ 
/*      */   public void sendImageMessage(Message message, String pushContent, String pushData, RongIMClient.SendImageMessageCallback callback)
/*      */   {
/* 2874 */     Message temp = filterSendMessage(message);
/*      */ 
/* 2876 */     if (temp == null) {
/* 2877 */       return;
/*      */     }
/* 2879 */     if (temp != message) {
/* 2880 */       message = temp;
/*      */     }
/* 2882 */     setMessageAttachedUserInfo(message.getContent());
/*      */ 
/* 2884 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 2885 */     result.t = new Event.OnReceiveMessageProgressEvent();
/*      */ 
/* 2887 */     RongIMClient.SendImageMessageCallback sendMessageCallback = new RongIMClient.SendImageMessageCallback(callback, result)
/*      */     {
/*      */       public void onAttached(Message message) {
/* 2890 */         RongContext.getInstance().getEventBus().post(message);
/*      */ 
/* 2892 */         if (this.val$callback != null)
/* 2893 */           this.val$callback.onAttached(message);
/*      */       }
/*      */ 
/*      */       public void onProgress(Message message, int progress)
/*      */       {
/* 2898 */         if (this.val$result.t == null)
/* 2899 */           return;
/* 2900 */         ((Event.OnReceiveMessageProgressEvent)this.val$result.t).setMessage(message);
/* 2901 */         ((Event.OnReceiveMessageProgressEvent)this.val$result.t).setProgress(progress);
/* 2902 */         RongContext.getInstance().getEventBus().post(this.val$result.t);
/*      */ 
/* 2904 */         if (this.val$callback != null)
/* 2905 */           this.val$callback.onProgress(message, progress);
/*      */       }
/*      */ 
/*      */       public void onError(Message message, RongIMClient.ErrorCode errorCode)
/*      */       {
/* 2911 */         RongIM.this.filterSentMessage(message, errorCode);
/*      */ 
/* 2913 */         if (this.val$callback != null)
/* 2914 */           this.val$callback.onError(message, errorCode);
/*      */       }
/*      */ 
/*      */       public void onSuccess(Message message)
/*      */       {
/* 2920 */         RongIM.this.filterSentMessage(message, null);
/*      */ 
/* 2922 */         if (this.val$callback != null)
/* 2923 */           this.val$callback.onSuccess(message);
/*      */       }
/*      */     };
/* 2927 */     RongIMClient.getInstance().sendImageMessage(message, pushContent, pushData, sendMessageCallback);
/*      */   }
/*      */ 
/*      */   public void sendImageMessage(Message message, String pushContent, String pushData, RongIMClient.SendImageMessageWithUploadListenerCallback callback)
/*      */   {
/* 2950 */     Message temp = filterSendMessage(message);
/*      */ 
/* 2952 */     if (temp == null) {
/* 2953 */       return;
/*      */     }
/* 2955 */     if (temp != message) {
/* 2956 */       message = temp;
/*      */     }
/* 2958 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 2959 */     result.t = new Event.OnReceiveMessageProgressEvent();
/*      */ 
/* 2961 */     RongIMClient.SendImageMessageWithUploadListenerCallback sendMessageCallback = new RongIMClient.SendImageMessageWithUploadListenerCallback(callback, result)
/*      */     {
/*      */       public void onAttached(Message message, RongIMClient.UploadImageStatusListener listener)
/*      */       {
/* 2965 */         RongContext.getInstance().getEventBus().post(message);
/*      */ 
/* 2967 */         if (this.val$callback != null)
/* 2968 */           this.val$callback.onAttached(message, listener);
/*      */       }
/*      */ 
/*      */       public void onProgress(Message message, int progress)
/*      */       {
/* 2973 */         if (this.val$result.t == null)
/* 2974 */           return;
/* 2975 */         ((Event.OnReceiveMessageProgressEvent)this.val$result.t).setMessage(message);
/* 2976 */         ((Event.OnReceiveMessageProgressEvent)this.val$result.t).setProgress(progress);
/* 2977 */         RongContext.getInstance().getEventBus().post(this.val$result.t);
/*      */ 
/* 2979 */         if (this.val$callback != null)
/* 2980 */           this.val$callback.onProgress(message, progress);
/*      */       }
/*      */ 
/*      */       public void onError(Message message, RongIMClient.ErrorCode errorCode)
/*      */       {
/* 2986 */         RongIM.this.filterSentMessage(message, errorCode);
/*      */ 
/* 2988 */         if (this.val$callback != null)
/* 2989 */           this.val$callback.onError(message, errorCode);
/*      */       }
/*      */ 
/*      */       public void onSuccess(Message message)
/*      */       {
/* 2995 */         RongIM.this.filterSentMessage(message, null);
/*      */ 
/* 2997 */         if (this.val$callback != null)
/* 2998 */           this.val$callback.onSuccess(message);
/*      */       }
/*      */     };
/* 3002 */     RongIMClient.getInstance().sendImageMessage(message, pushContent, pushData, sendMessageCallback);
/*      */   }
/*      */ 
/*      */   public void downloadMedia(Conversation.ConversationType conversationType, String targetId, RongIMClient.MediaType mediaType, String imageUrl, RongIMClient.DownloadMediaCallback callback)
/*      */   {
/* 3017 */     RongIMClient.getInstance().downloadMedia(conversationType, targetId, mediaType, imageUrl, callback);
/*      */   }
/*      */ 
/*      */   public void downloadMediaMessage(Message message, IRongCallback.IDownloadMediaMessageCallback callback)
/*      */   {
/* 3029 */     RongIMClient.getInstance().downloadMediaMessage(message, new IRongCallback.IDownloadMediaMessageCallback(callback)
/*      */     {
/*      */       public void onSuccess(Message message) {
/* 3032 */         EventBus.getDefault().post(message);
/* 3033 */         EventBus.getDefault().post(new Event.FileMessageEvent(message, 100, 100, null));
/* 3034 */         if (this.val$callback != null)
/* 3035 */           this.val$callback.onSuccess(message);
/*      */       }
/*      */ 
/*      */       public void onProgress(Message message, int progress)
/*      */       {
/* 3041 */         EventBus.getDefault().post(new Event.FileMessageEvent(message, progress, 101, null));
/* 3042 */         if (this.val$callback != null)
/* 3043 */           this.val$callback.onProgress(message, progress);
/*      */       }
/*      */ 
/*      */       public void onError(Message message, RongIMClient.ErrorCode code)
/*      */       {
/* 3049 */         EventBus.getDefault().post(new Event.FileMessageEvent(message, 0, 103, code));
/* 3050 */         if (this.val$callback != null)
/* 3051 */           this.val$callback.onError(message, code);
/*      */       }
/*      */ 
/*      */       public void onCanceled(Message message)
/*      */       {
/* 3057 */         EventBus.getDefault().post(new Event.FileMessageEvent(message, 0, 102, null));
/* 3058 */         if (this.val$callback != null)
/* 3059 */           this.val$callback.onCanceled(message);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void downloadMedia(String imageUrl, RongIMClient.DownloadMediaCallback callback)
/*      */   {
/* 3073 */     ImageLoader.getInstance().loadImage(imageUrl, null, null, new ImageLoadingListener(callback)
/*      */     {
/*      */       public void onLoadingStarted(String imageUri, View view)
/*      */       {
/*      */       }
/*      */ 
/*      */       public void onLoadingFailed(String imageUri, View view, FailReason failReason)
/*      */       {
/* 3081 */         if (this.val$callback != null)
/* 3082 */           this.val$callback.onFail(RongIMClient.ErrorCode.RC_NET_UNAVAILABLE);
/*      */       }
/*      */ 
/*      */       public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
/*      */       {
/* 3087 */         if (this.val$callback != null)
/* 3088 */           this.val$callback.onCallback(imageUri);
/*      */       }
/*      */ 
/*      */       public void onLoadingCancelled(String imageUri, View view)
/*      */       {
/*      */       }
/*      */     }
/*      */     , new ImageLoadingProgressListener(callback)
/*      */     {
/*      */       public void onProgressUpdate(String imageUri, View view, int current, int total)
/*      */       {
/* 3098 */         if (this.val$callback != null)
/* 3099 */           this.val$callback.onProgress(current * 100 / total);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getConversationNotificationStatus(Conversation.ConversationType conversationType, String targetId, RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus> callback)
/*      */   {
/* 3113 */     RongIMClient.getInstance().getConversationNotificationStatus(conversationType, targetId, new RongIMClient.ResultCallback(targetId, conversationType, callback)
/*      */     {
/*      */       public void onSuccess(Conversation.ConversationNotificationStatus status)
/*      */       {
/* 3117 */         RongContext.getInstance().setConversationNotifyStatusToCache(ConversationKey.obtain(this.val$targetId, this.val$conversationType), status);
/*      */ 
/* 3119 */         if (this.val$callback != null)
/* 3120 */           this.val$callback.onSuccess(status);
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 3127 */         if (this.val$callback != null)
/* 3128 */           this.val$callback.onError(e);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setConversationNotificationStatus(Conversation.ConversationType conversationType, String targetId, Conversation.ConversationNotificationStatus notificationStatus, RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus> callback)
/*      */   {
/* 3143 */     RongIMClient.getInstance().setConversationNotificationStatus(conversationType, targetId, notificationStatus, new RongIMClient.ResultCallback(callback, targetId, conversationType, notificationStatus)
/*      */     {
/*      */       public void onError(RongIMClient.ErrorCode errorCode) {
/* 3146 */         if (this.val$callback != null)
/* 3147 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */ 
/*      */       public void onSuccess(Conversation.ConversationNotificationStatus status)
/*      */       {
/* 3152 */         RongContext.getInstance().getEventBus().post(new Event.ConversationNotificationEvent(this.val$targetId, this.val$conversationType, this.val$notificationStatus));
/* 3153 */         RongContext.getInstance().setConversationNotifyStatusToCache(ConversationKey.obtain(this.val$targetId, this.val$conversationType), status);
/*      */ 
/* 3155 */         if (this.val$callback != null)
/* 3156 */           this.val$callback.onSuccess(status);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setDiscussionInviteStatus(String discussionId, RongIMClient.DiscussionInviteStatus status, RongIMClient.OperationCallback callback)
/*      */   {
/* 3169 */     RongIMClient.getInstance().setDiscussionInviteStatus(discussionId, status, new RongIMClient.OperationCallback(discussionId, status, callback)
/*      */     {
/*      */       public void onSuccess()
/*      */       {
/* 3173 */         RongContext.getInstance().getEventBus().post(new Event.DiscussionInviteStatusEvent(this.val$discussionId, this.val$status));
/*      */ 
/* 3175 */         if (this.val$callback != null)
/* 3176 */           this.val$callback.onSuccess();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 3181 */         if (this.val$callback != null)
/* 3182 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void syncGroup(List<Group> groups, RongIMClient.OperationCallback callback)
/*      */   {
/* 3198 */     RongIMClient.getInstance().syncGroup(groups, new RongIMClient.OperationCallback(groups, callback)
/*      */     {
/*      */       public void onSuccess()
/*      */       {
/* 3202 */         RongContext.getInstance().getEventBus().post(new Event.SyncGroupEvent(this.val$groups));
/*      */ 
/* 3204 */         if (this.val$callback != null)
/* 3205 */           this.val$callback.onSuccess();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 3211 */         if (this.val$callback != null)
/* 3212 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void joinGroup(String groupId, String groupName, RongIMClient.OperationCallback callback)
/*      */   {
/* 3229 */     RongIMClient.getInstance().joinGroup(groupId, groupName, new RongIMClient.OperationCallback(groupId, groupName, callback)
/*      */     {
/*      */       public void onSuccess()
/*      */       {
/* 3233 */         RongContext.getInstance().getEventBus().post(new Event.JoinGroupEvent(this.val$groupId, this.val$groupName));
/*      */ 
/* 3235 */         if (this.val$callback != null)
/* 3236 */           this.val$callback.onSuccess();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 3242 */         if (this.val$callback != null)
/* 3243 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void quitGroup(String groupId, RongIMClient.OperationCallback callback)
/*      */   {
/* 3259 */     RongIMClient.getInstance().quitGroup(groupId, new RongIMClient.OperationCallback(groupId, callback)
/*      */     {
/*      */       public void onSuccess()
/*      */       {
/* 3263 */         RongContext.getInstance().getEventBus().post(new Event.QuitGroupEvent(this.val$groupId));
/*      */ 
/* 3265 */         if (this.val$callback != null)
/* 3266 */           this.val$callback.onSuccess();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 3272 */         if (this.val$callback != null)
/* 3273 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public String getCurrentUserId()
/*      */   {
/* 3284 */     return RongIMClient.getInstance().getCurrentUserId();
/*      */   }
/*      */ 
/*      */   public long getDeltaTime()
/*      */   {
/* 3294 */     return RongIMClient.getInstance().getDeltaTime();
/*      */   }
/*      */ 
/*      */   public void joinChatRoom(String chatroomId, int defMessageCount, RongIMClient.OperationCallback callback)
/*      */   {
/* 3307 */     RongIMClient.getInstance().joinChatRoom(chatroomId, defMessageCount, new RongIMClient.OperationCallback(chatroomId, defMessageCount, callback)
/*      */     {
/*      */       public void onSuccess()
/*      */       {
/* 3311 */         RongContext.getInstance().getEventBus().post(new Event.JoinChatRoomEvent(this.val$chatroomId, this.val$defMessageCount));
/*      */ 
/* 3313 */         if (this.val$callback != null)
/* 3314 */           this.val$callback.onSuccess();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 3320 */         if (this.val$callback != null)
/* 3321 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void joinExistChatRoom(String chatroomId, int defMessageCount, RongIMClient.OperationCallback callback)
/*      */   {
/* 3336 */     RongIMClient.getInstance().joinExistChatRoom(chatroomId, defMessageCount, new RongIMClient.OperationCallback(chatroomId, defMessageCount, callback)
/*      */     {
/*      */       public void onSuccess()
/*      */       {
/* 3340 */         RongContext.getInstance().getEventBus().post(new Event.JoinChatRoomEvent(this.val$chatroomId, this.val$defMessageCount));
/*      */ 
/* 3342 */         if (this.val$callback != null)
/* 3343 */           this.val$callback.onSuccess();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 3349 */         if (this.val$callback != null)
/* 3350 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void quitChatRoom(String chatroomId, RongIMClient.OperationCallback callback)
/*      */   {
/* 3362 */     RongIMClient.getInstance().quitChatRoom(chatroomId, new RongIMClient.OperationCallback(chatroomId, callback)
/*      */     {
/*      */       public void onSuccess()
/*      */       {
/* 3366 */         RongContext.getInstance().getEventBus().post(new Event.QuitChatRoomEvent(this.val$chatroomId));
/*      */ 
/* 3368 */         if (this.val$callback != null)
/* 3369 */           this.val$callback.onSuccess();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 3375 */         if (this.val$callback != null)
/* 3376 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void clearConversations(RongIMClient.ResultCallback callback, Conversation.ConversationType[] conversationTypes)
/*      */   {
/* 3388 */     RongIMClient.getInstance().clearConversations(new RongIMClient.ResultCallback(conversationTypes, callback)
/*      */     {
/*      */       public void onSuccess(Object o) {
/* 3391 */         RongContext.getInstance().getEventBus().post(Event.ClearConversationEvent.obtain(this.val$conversationTypes));
/* 3392 */         if (this.val$callback != null)
/* 3393 */           this.val$callback.onSuccess(o);
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 3398 */         if (this.val$callback != null)
/* 3399 */           this.val$callback.onError(e);
/*      */       }
/*      */     }
/*      */     , conversationTypes);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean clearConversations(Conversation.ConversationType[] conversationTypes)
/*      */   {
/* 3413 */     return RongIMClient.getInstance().clearConversations(conversationTypes);
/*      */   }
/*      */ 
/*      */   public void addToBlacklist(String userId, RongIMClient.OperationCallback callback)
/*      */   {
/* 3425 */     RongIMClient.getInstance().addToBlacklist(userId, new RongIMClient.OperationCallback(userId, callback)
/*      */     {
/*      */       public void onSuccess()
/*      */       {
/* 3429 */         RongContext.getInstance().getEventBus().post(new Event.AddToBlacklistEvent(this.val$userId));
/*      */ 
/* 3431 */         if (this.val$callback != null)
/* 3432 */           this.val$callback.onSuccess();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 3438 */         if (this.val$callback != null)
/* 3439 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void removeFromBlacklist(String userId, RongIMClient.OperationCallback callback)
/*      */   {
/* 3451 */     RongIMClient.getInstance().removeFromBlacklist(userId, new RongIMClient.OperationCallback(userId, callback)
/*      */     {
/*      */       public void onSuccess()
/*      */       {
/* 3455 */         RongContext.getInstance().getEventBus().post(new Event.RemoveFromBlacklistEvent(this.val$userId));
/*      */ 
/* 3457 */         if (this.val$callback != null)
/* 3458 */           this.val$callback.onSuccess();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 3464 */         if (this.val$callback != null)
/* 3465 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getBlacklistStatus(String userId, RongIMClient.ResultCallback<RongIMClient.BlacklistStatus> callback)
/*      */   {
/* 3477 */     RongIMClient.getInstance().getBlacklistStatus(userId, callback);
/*      */   }
/*      */ 
/*      */   public void getBlacklist(RongIMClient.GetBlacklistCallback callback)
/*      */   {
/* 3486 */     RongIMClient.getInstance().getBlacklist(callback);
/*      */   }
/*      */ 
/*      */   public void setNotificationQuietHours(String startTime, int spanMinutes, RongIMClient.OperationCallback callback)
/*      */   {
/* 3497 */     RongIMClient.getInstance().setNotificationQuietHours(startTime, spanMinutes, new RongIMClient.OperationCallback(startTime, spanMinutes, callback)
/*      */     {
/*      */       public void onSuccess()
/*      */       {
/* 3501 */         CommonUtils.saveNotificationQuietHours(RongIM.mContext, this.val$startTime, this.val$spanMinutes);
/*      */ 
/* 3503 */         if (this.val$callback != null)
/* 3504 */           this.val$callback.onSuccess();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 3510 */         if (this.val$callback != null)
/* 3511 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void removeNotificationQuietHours(RongIMClient.OperationCallback callback)
/*      */   {
/* 3523 */     RongIMClient.getInstance().removeNotificationQuietHours(new RongIMClient.OperationCallback(callback)
/*      */     {
/*      */       public void onSuccess() {
/* 3526 */         CommonUtils.saveNotificationQuietHours(RongIM.mContext, "-1", -1);
/*      */ 
/* 3528 */         if (this.val$callback != null)
/* 3529 */           this.val$callback.onSuccess();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 3535 */         if (this.val$callback != null)
/* 3536 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getNotificationQuietHours(RongIMClient.GetNotificationQuietHoursCallback callback)
/*      */   {
/* 3548 */     RongIMClient.getInstance().getNotificationQuietHours(new RongIMClient.GetNotificationQuietHoursCallback(callback)
/*      */     {
/*      */       public void onSuccess(String startTime, int spanMinutes) {
/* 3551 */         CommonUtils.saveNotificationQuietHours(RongIM.mContext, startTime, spanMinutes);
/*      */ 
/* 3553 */         if (this.val$callback != null)
/* 3554 */           this.val$callback.onSuccess(startTime, spanMinutes);
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 3560 */         if (this.val$callback != null)
/* 3561 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getPublicServiceProfile(Conversation.PublicServiceType publicServiceType, String publicServiceId, RongIMClient.ResultCallback<PublicServiceProfile> callback)
/*      */   {
/* 3575 */     RongIMClient.getInstance().getPublicServiceProfile(publicServiceType, publicServiceId, callback);
/*      */   }
/*      */ 
/*      */   public void searchPublicService(RongIMClient.SearchType searchType, String keywords, RongIMClient.ResultCallback<PublicServiceProfileList> callback)
/*      */   {
/* 3586 */     RongIMClient.getInstance().searchPublicService(searchType, keywords, callback);
/*      */   }
/*      */ 
/*      */   public void searchPublicServiceByType(Conversation.PublicServiceType publicServiceType, RongIMClient.SearchType searchType, String keywords, RongIMClient.ResultCallback<PublicServiceProfileList> callback)
/*      */   {
/* 3598 */     RongIMClient.getInstance().searchPublicServiceByType(publicServiceType, searchType, keywords, callback);
/*      */   }
/*      */ 
/*      */   public void subscribePublicService(Conversation.PublicServiceType publicServiceType, String publicServiceId, RongIMClient.OperationCallback callback)
/*      */   {
/* 3609 */     RongIMClient.getInstance().subscribePublicService(publicServiceType, publicServiceId, callback);
/*      */   }
/*      */ 
/*      */   public void unsubscribePublicService(Conversation.PublicServiceType publicServiceType, String publicServiceId, RongIMClient.OperationCallback callback)
/*      */   {
/* 3620 */     RongIMClient.getInstance().unsubscribePublicService(publicServiceType, publicServiceId, callback);
/*      */   }
/*      */ 
/*      */   public void getPublicServiceList(RongIMClient.ResultCallback<PublicServiceProfileList> callback)
/*      */   {
/* 3629 */     RongIMClient.getInstance().getPublicServiceList(callback);
/*      */   }
/*      */ 
/*      */   public void syncUserData(UserData userData, RongIMClient.OperationCallback callback)
/*      */   {
/* 3640 */     RongIMClient.getInstance().syncUserData(userData, new RongIMClient.OperationCallback(callback)
/*      */     {
/*      */       public void onSuccess() {
/* 3643 */         if (this.val$callback != null)
/* 3644 */           this.val$callback.onSuccess();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 3650 */         if (this.val$callback != null)
/* 3651 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setRequestPermissionListener(RequestPermissionsListener listener)
/*      */   {
/* 3664 */     RongContext.getInstance().setRequestPermissionListener(listener);
/*      */   }
/*      */ 
/*      */   public void recordNotificationEvent(String pushId)
/*      */   {
/* 3676 */     RongPushClient.recordNotificationEvent(pushId);
/*      */   }
/*      */ 
/*      */   private MessageContent setMessageAttachedUserInfo(MessageContent content)
/*      */   {
/* 3688 */     if (RongContext.getInstance().getUserInfoAttachedState())
/*      */     {
/* 3690 */       if (content.getUserInfo() == null) {
/* 3691 */         String userId = getInstance().getCurrentUserId();
/*      */ 
/* 3693 */         UserInfo info = RongContext.getInstance().getCurrentUserInfo();
/*      */ 
/* 3695 */         if (info == null) {
/* 3696 */           info = RongUserInfoManager.getInstance().getUserInfo(userId);
/*      */         }
/* 3698 */         if (info != null) {
/* 3699 */           content.setUserInfo(info);
/*      */         }
/*      */       }
/*      */     }
/* 3703 */     return content;
/*      */   }
/*      */ 
/*      */   private Message filterSendMessage(Conversation.ConversationType conversationType, String targetId, MessageContent messageContent)
/*      */   {
/* 3715 */     Message message = new Message();
/* 3716 */     message.setConversationType(conversationType);
/* 3717 */     message.setTargetId(targetId);
/* 3718 */     message.setContent(messageContent);
/*      */ 
/* 3720 */     if (RongContext.getInstance().getOnSendMessageListener() != null) {
/* 3721 */       message = RongContext.getInstance().getOnSendMessageListener().onSend(message);
/*      */     }
/*      */ 
/* 3724 */     return message;
/*      */   }
/*      */ 
/*      */   private Message filterSendMessage(Message message)
/*      */   {
/* 3735 */     if (RongContext.getInstance().getOnSendMessageListener() != null) {
/* 3736 */       message = RongContext.getInstance().getOnSendMessageListener().onSend(message);
/*      */     }
/*      */ 
/* 3739 */     return message;
/*      */   }
/*      */ 
/*      */   private void filterSentMessage(Message message, RongIMClient.ErrorCode errorCode)
/*      */   {
/* 3744 */     SentMessageErrorCode sentMessageErrorCode = null;
/* 3745 */     boolean isExecute = false;
/*      */ 
/* 3747 */     if (RongContext.getInstance().getOnSendMessageListener() != null)
/*      */     {
/* 3749 */       if (errorCode != null) {
/* 3750 */         sentMessageErrorCode = SentMessageErrorCode.setValue(errorCode.getValue());
/*      */       }
/*      */ 
/* 3753 */       isExecute = RongContext.getInstance().getOnSendMessageListener().onSent(message, sentMessageErrorCode);
/*      */     }
/*      */ 
/* 3756 */     if ((errorCode != null) && (!isExecute))
/*      */     {
/* 3758 */       if ((errorCode.equals(RongIMClient.ErrorCode.NOT_IN_DISCUSSION)) || (errorCode.equals(RongIMClient.ErrorCode.NOT_IN_GROUP)) || (errorCode.equals(RongIMClient.ErrorCode.NOT_IN_CHATROOM)) || (errorCode.equals(RongIMClient.ErrorCode.REJECTED_BY_BLACKLIST)) || (errorCode.equals(RongIMClient.ErrorCode.FORBIDDEN_IN_GROUP)) || (errorCode.equals(RongIMClient.ErrorCode.FORBIDDEN_IN_CHATROOM)) || (errorCode.equals(RongIMClient.ErrorCode.KICKED_FROM_CHATROOM)))
/*      */       {
/* 3762 */         InformationNotificationMessage informationMessage = null;
/*      */ 
/* 3764 */         if (errorCode.equals(RongIMClient.ErrorCode.NOT_IN_DISCUSSION))
/* 3765 */           informationMessage = InformationNotificationMessage.obtain(mContext.getString(R.string.rc_info_not_in_discussion));
/* 3766 */         else if (errorCode.equals(RongIMClient.ErrorCode.NOT_IN_GROUP))
/* 3767 */           informationMessage = InformationNotificationMessage.obtain(mContext.getString(R.string.rc_info_not_in_group));
/* 3768 */         else if (errorCode.equals(RongIMClient.ErrorCode.NOT_IN_CHATROOM))
/* 3769 */           informationMessage = InformationNotificationMessage.obtain(mContext.getString(R.string.rc_info_not_in_chatroom));
/* 3770 */         else if (errorCode.equals(RongIMClient.ErrorCode.REJECTED_BY_BLACKLIST))
/* 3771 */           informationMessage = InformationNotificationMessage.obtain(mContext.getString(R.string.rc_rejected_by_blacklist_prompt));
/* 3772 */         else if (errorCode.equals(RongIMClient.ErrorCode.FORBIDDEN_IN_GROUP))
/* 3773 */           informationMessage = InformationNotificationMessage.obtain(mContext.getString(R.string.rc_info_forbidden_to_talk));
/* 3774 */         else if (errorCode.equals(RongIMClient.ErrorCode.FORBIDDEN_IN_CHATROOM))
/* 3775 */           informationMessage = InformationNotificationMessage.obtain(mContext.getString(R.string.rc_forbidden_in_chatroom));
/* 3776 */         else if (errorCode.equals(RongIMClient.ErrorCode.KICKED_FROM_CHATROOM)) {
/* 3777 */           informationMessage = InformationNotificationMessage.obtain(mContext.getString(R.string.rc_kicked_from_chatroom));
/*      */         }
/*      */ 
/* 3780 */         insertMessage(message.getConversationType(), message.getTargetId(), "rong", informationMessage, new RongIMClient.ResultCallback()
/*      */         {
/*      */           public void onSuccess(Message message)
/*      */           {
/*      */           }
/*      */ 
/*      */           public void onError(RongIMClient.ErrorCode e)
/*      */           {
/*      */           }
/*      */         });
/*      */       }
/* 3793 */       MessageContent content = message.getContent();
/* 3794 */       MessageTag tag = (MessageTag)content.getClass().getAnnotation(MessageTag.class);
/*      */ 
/* 3796 */       if ((tag != null) && ((tag.flag() & 0x1) == 1)) {
/* 3797 */         RongContext.getInstance().getEventBus().post(new Event.OnMessageSendErrorEvent(message, errorCode));
/*      */       }
/*      */ 
/*      */     }
/* 3801 */     else if (message != null) {
/* 3802 */       MessageContent content = message.getContent();
/*      */ 
/* 3804 */       MessageTag tag = (MessageTag)content.getClass().getAnnotation(MessageTag.class);
/*      */ 
/* 3806 */       if ((tag != null) && ((tag.flag() & 0x1) == 1))
/* 3807 */         RongContext.getInstance().getEventBus().post(message);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void setServerInfo(String naviServer, String fileServer)
/*      */   {
/* 3821 */     if (TextUtils.isEmpty(naviServer)) {
/* 3822 */       RLog.e(TAG, "setServerInfo naviServer should not be null.");
/* 3823 */       throw new IllegalArgumentException("naviServer should not be null.");
/*      */     }
/* 3825 */     RongIMClient.setServerInfo(naviServer, fileServer);
/*      */   }
/*      */ 
/*      */   public void setPublicServiceMenuClickListener(IPublicServiceMenuClickListener menuClickListener)
/*      */   {
/* 3835 */     if (RongContext.getInstance() != null)
/* 3836 */       RongContext.getInstance().setPublicServiceMenuClickListener(menuClickListener);
/*      */   }
/*      */ 
/*      */   public void recallMessage(Message message)
/*      */   {
/* 3846 */     RongIMClient.getInstance().recallMessage(message, new RongIMClient.ResultCallback(message)
/*      */     {
/*      */       public void onSuccess(RecallNotificationMessage recallNotificationMessage) {
/* 3849 */         RongContext.getInstance().getEventBus().post(new Event.MessageRecallEvent(this.val$message.getMessageId(), recallNotificationMessage, true));
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/* 3854 */         RLog.d(RongIM.TAG, "recallMessage errorCode = " + errorCode.getValue());
/* 3855 */         RongContext.getInstance().getEventBus().post(new Event.MessageRecallEvent(this.val$message.getMessageId(), null, false));
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void sendMediaMessage(Message message, String pushContent, String pushData, IRongCallback.ISendMediaMessageCallback callback)
/*      */   {
/* 3873 */     Message temp = filterSendMessage(message);
/*      */ 
/* 3875 */     if (temp == null) {
/* 3876 */       return;
/*      */     }
/*      */ 
/* 3879 */     if (temp != message) {
/* 3880 */       message = temp;
/*      */     }
/* 3882 */     setMessageAttachedUserInfo(message.getContent());
/*      */ 
/* 3884 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 3885 */     result.t = new Event.OnReceiveMessageProgressEvent();
/*      */ 
/* 3887 */     IRongCallback.ISendMediaMessageCallback sendMessageCallback = new IRongCallback.ISendMediaMessageCallback(result, callback)
/*      */     {
/*      */       public void onProgress(Message message, int progress) {
/* 3890 */         if (this.val$result.t == null)
/* 3891 */           return;
/* 3892 */         ((Event.OnReceiveMessageProgressEvent)this.val$result.t).setMessage(message);
/* 3893 */         ((Event.OnReceiveMessageProgressEvent)this.val$result.t).setProgress(progress);
/* 3894 */         RongContext.getInstance().getEventBus().post(this.val$result.t);
/*      */ 
/* 3896 */         if (this.val$callback != null)
/* 3897 */           this.val$callback.onProgress(message, progress);
/*      */       }
/*      */ 
/*      */       public void onAttached(Message message)
/*      */       {
/* 3902 */         RongContext.getInstance().getEventBus().post(message);
/*      */ 
/* 3904 */         if (this.val$callback != null)
/* 3905 */           this.val$callback.onAttached(message);
/*      */       }
/*      */ 
/*      */       public void onSuccess(Message message)
/*      */       {
/* 3910 */         RongIM.this.filterSentMessage(message, null);
/*      */ 
/* 3912 */         if (this.val$callback != null)
/* 3913 */           this.val$callback.onSuccess(message);
/*      */       }
/*      */ 
/*      */       public void onError(Message message, RongIMClient.ErrorCode errorCode)
/*      */       {
/* 3918 */         RongIM.this.filterSentMessage(message, errorCode);
/*      */ 
/* 3920 */         if (this.val$callback != null)
/* 3921 */           this.val$callback.onError(message, errorCode);
/*      */       }
/*      */     };
/* 3925 */     RongIMClient.getInstance().sendMediaMessage(message, pushContent, pushData, sendMessageCallback);
/*      */   }
/*      */ 
/*      */   public void cancelDownloadMediaMessage(Message message, RongIMClient.OperationCallback callback)
/*      */   {
/* 3934 */     RongIMClient.getInstance().cancelDownloadMediaMessage(message, callback);
/*      */   }
/*      */ 
/*      */   public void setReadReceiptConversationTypeList(Conversation.ConversationType[] types)
/*      */   {
/* 3944 */     if (RongContext.getInstance() != null)
/* 3945 */       RongContext.getInstance().setReadReceiptConversationTypeList(types);
/*      */   }
/*      */ 
/*      */   public static abstract interface RequestPermissionsListener
/*      */   {
/*      */     public abstract void onPermissionRequest(String[] paramArrayOfString, int paramInt);
/*      */   }
/*      */ 
/*      */   public static abstract interface OnReceiveUnreadCountChangedListener
/*      */   {
/*      */     public abstract void onMessageIncreased(int paramInt);
/*      */   }
/*      */ 
/*      */   public static enum SentMessageErrorCode
/*      */   {
/* 1206 */     UNKNOWN(-1, "Unknown error."), 
/*      */ 
/* 1211 */     NOT_IN_DISCUSSION(21406, "not_in_discussion"), 
/*      */ 
/* 1215 */     NOT_IN_GROUP(22406, "not_in_group"), 
/*      */ 
/* 1219 */     FORBIDDEN_IN_GROUP(22408, "forbidden_in_group"), 
/*      */ 
/* 1223 */     NOT_IN_CHATROOM(23406, "not_in_chatroom"), 
/*      */ 
/* 1228 */     REJECTED_BY_BLACKLIST(405, "rejected by blacklist"), 
/*      */ 
/* 1233 */     NOT_FOLLOWED(29106, "not followed");
/*      */ 
/*      */     private int code;
/*      */     private String msg;
/*      */ 
/*      */     private SentMessageErrorCode(int code, String msg)
/*      */     {
/* 1246 */       this.code = code;
/* 1247 */       this.msg = msg;
/*      */     }
/*      */ 
/*      */     public int getValue()
/*      */     {
/* 1256 */       return this.code;
/*      */     }
/*      */ 
/*      */     public String getMessage()
/*      */     {
/* 1265 */       return this.msg;
/*      */     }
/*      */ 
/*      */     public static SentMessageErrorCode setValue(int code)
/*      */     {
/* 1275 */       for (SentMessageErrorCode c : values()) {
/* 1276 */         if (code == c.getValue()) {
/* 1277 */           return c;
/*      */         }
/*      */       }
/*      */ 
/* 1281 */       RLog.d("RongIMClient", "SentMessageErrorCode---ErrorCode---code:" + code);
/*      */ 
/* 1283 */       return UNKNOWN;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface OnSendMessageListener
/*      */   {
/*      */     public abstract Message onSend(Message paramMessage);
/*      */ 
/*      */     public abstract boolean onSent(Message paramMessage, RongIM.SentMessageErrorCode paramSentMessageErrorCode);
/*      */   }
/*      */ 
/*      */   public static abstract interface OnSelectMemberListener
/*      */   {
/*      */     public abstract void startSelectMember(Context paramContext, Conversation.ConversationType paramConversationType, String paramString);
/*      */   }
/*      */ 
/*      */   public static abstract interface GroupInfoProvider
/*      */   {
/*      */     public abstract Group getGroupInfo(String paramString);
/*      */   }
/*      */ 
/*      */   public static abstract interface GroupUserInfoProvider
/*      */   {
/*      */     public abstract GroupUserInfo getGroupUserInfo(String paramString1, String paramString2);
/*      */   }
/*      */ 
/*      */   public static abstract interface UserInfoProvider
/*      */   {
/*      */     public abstract UserInfo getUserInfo(String paramString);
/*      */   }
/*      */ 
/*      */   public static abstract interface ConversationListBehaviorListener
/*      */   {
/*      */     public abstract boolean onConversationPortraitClick(Context paramContext, Conversation.ConversationType paramConversationType, String paramString);
/*      */ 
/*      */     public abstract boolean onConversationPortraitLongClick(Context paramContext, Conversation.ConversationType paramConversationType, String paramString);
/*      */ 
/*      */     public abstract boolean onConversationLongClick(Context paramContext, View paramView, UIConversation paramUIConversation);
/*      */ 
/*      */     public abstract boolean onConversationClick(Context paramContext, View paramView, UIConversation paramUIConversation);
/*      */   }
/*      */ 
/*      */   public static abstract interface ConversationBehaviorListener
/*      */   {
/*      */     public abstract boolean onUserPortraitClick(Context paramContext, Conversation.ConversationType paramConversationType, UserInfo paramUserInfo);
/*      */ 
/*      */     public abstract boolean onUserPortraitLongClick(Context paramContext, Conversation.ConversationType paramConversationType, UserInfo paramUserInfo);
/*      */ 
/*      */     public abstract boolean onMessageClick(Context paramContext, View paramView, Message paramMessage);
/*      */ 
/*      */     public abstract boolean onMessageLinkClick(Context paramContext, String paramString);
/*      */ 
/*      */     public abstract boolean onMessageLongClick(Context paramContext, View paramView, Message paramMessage);
/*      */   }
/*      */ 
/*      */   public static abstract interface PublicServiceBehaviorListener
/*      */   {
/*      */     public abstract boolean onFollowClick(Context paramContext, PublicServiceProfile paramPublicServiceProfile);
/*      */ 
/*      */     public abstract boolean onUnFollowClick(Context paramContext, PublicServiceProfile paramPublicServiceProfile);
/*      */ 
/*      */     public abstract boolean onEnterConversationClick(Context paramContext, PublicServiceProfile paramPublicServiceProfile);
/*      */   }
/*      */ 
/*      */   public static abstract interface LocationProvider
/*      */   {
/*      */     public abstract void onStartLocation(Context paramContext, LocationCallback paramLocationCallback);
/*      */ 
/*      */     public static abstract interface LocationCallback
/*      */     {
/*      */       public abstract void onSuccess(LocationMessage paramLocationMessage);
/*      */ 
/*      */       public abstract void onFailure(String paramString);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface IGroupMemberCallback
/*      */   {
/*      */     public abstract void onGetGroupMembersResult(List<UserInfo> paramList);
/*      */   }
/*      */ 
/*      */   public static abstract interface IGroupMembersProvider
/*      */   {
/*      */     public abstract void getGroupMembers(String paramString, RongIM.IGroupMemberCallback paramIGroupMemberCallback);
/*      */   }
/*      */ 
/*      */   static class SingletonHolder
/*      */   {
/*  114 */     static RongIM sRongIM = new RongIM(null);
/*      */   }
/*      */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.RongIM
 * JD-Core Version:    0.6.0
 */
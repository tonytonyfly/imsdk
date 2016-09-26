/*      */ package io.rong.imlib;
/*      */ 
/*      */ import android.content.ComponentName;
/*      */ import android.content.Context;
/*      */ import android.content.Intent;
/*      */ import android.content.IntentFilter;
/*      */ import android.content.ServiceConnection;
/*      */ import android.content.SharedPreferences;
/*      */ import android.content.SharedPreferences.Editor;
/*      */ import android.content.pm.ApplicationInfo;
/*      */ import android.content.pm.PackageManager;
/*      */ import android.content.pm.PackageManager.NameNotFoundException;
/*      */ import android.content.res.Resources;
/*      */ import android.net.ConnectivityManager;
/*      */ import android.net.NetworkInfo;
/*      */ import android.net.Uri;
/*      */ import android.os.Bundle;
/*      */ import android.os.Handler;
/*      */ import android.os.HandlerThread;
/*      */ import android.os.IBinder;
/*      */ import android.os.Looper;
/*      */ import android.os.RemoteException;
/*      */ import android.text.TextUtils;
/*      */ import android.util.Log;
/*      */ import io.rong.common.RLog;
/*      */ import io.rong.common.SystemUtils;
/*      */ import io.rong.imlib.TypingMessage.TypingMessageManager;
/*      */ import io.rong.imlib.TypingMessage.TypingStatus;
/*      */ import io.rong.imlib.TypingMessage.TypingStatusMessage;
/*      */ import io.rong.imlib.common.DeviceUtils;
/*      */ import io.rong.imlib.ipc.RongService;
/*      */ import io.rong.imlib.location.RealTimeLocationConstant.RealTimeLocationErrorCode;
/*      */ import io.rong.imlib.location.RealTimeLocationConstant.RealTimeLocationStatus;
/*      */ import io.rong.imlib.location.message.RealTimeLocationJoinMessage;
/*      */ import io.rong.imlib.location.message.RealTimeLocationQuitMessage;
/*      */ import io.rong.imlib.location.message.RealTimeLocationStatusMessage;
/*      */ import io.rong.imlib.model.CSCustomServiceInfo;
/*      */ import io.rong.imlib.model.CSCustomServiceInfo.Builder;
/*      */ import io.rong.imlib.model.CSGroupItem;
/*      */ import io.rong.imlib.model.ChatRoomInfo;
/*      */ import io.rong.imlib.model.ChatRoomInfo.ChatRoomMemberOrder;
/*      */ import io.rong.imlib.model.Conversation;
/*      */ import io.rong.imlib.model.Conversation.ConversationNotificationStatus;
/*      */ import io.rong.imlib.model.Conversation.ConversationType;
/*      */ import io.rong.imlib.model.Conversation.PublicServiceType;
/*      */ import io.rong.imlib.model.CustomServiceMode;
/*      */ import io.rong.imlib.model.Discussion;
/*      */ import io.rong.imlib.model.Group;
/*      */ import io.rong.imlib.model.Message;
/*      */ import io.rong.imlib.model.Message.MessageDirection;
/*      */ import io.rong.imlib.model.Message.ReceivedStatus;
/*      */ import io.rong.imlib.model.Message.SentStatus;
/*      */ import io.rong.imlib.model.MessageContent;
/*      */ import io.rong.imlib.model.PublicServiceProfile;
/*      */ import io.rong.imlib.model.PublicServiceProfileList;
/*      */ import io.rong.imlib.model.ReadReceiptInfo;
/*      */ import io.rong.imlib.model.RemoteModelWrap;
/*      */ import io.rong.imlib.model.RongListWrap;
/*      */ import io.rong.imlib.model.UserData;
/*      */ import io.rong.imlib.model.UserInfo;
/*      */ import io.rong.imlib.statistics.Statistics;
/*      */ import io.rong.message.CSChangeModeMessage;
/*      */ import io.rong.message.CSChangeModeResponseMessage;
/*      */ import io.rong.message.CSEvaluateMessage;
/*      */ import io.rong.message.CSEvaluateMessage.Builder;
/*      */ import io.rong.message.CSHandShakeMessage;
/*      */ import io.rong.message.CSHandShakeResponseMessage;
/*      */ import io.rong.message.CSPullEvaluateMessage;
/*      */ import io.rong.message.CSSuspendMessage;
/*      */ import io.rong.message.CSTerminateMessage;
/*      */ import io.rong.message.CSUpdateMessage;
/*      */ import io.rong.message.CommandMessage;
/*      */ import io.rong.message.CommandNotificationMessage;
/*      */ import io.rong.message.ContactNotificationMessage;
/*      */ import io.rong.message.DiscussionNotificationMessage;
/*      */ import io.rong.message.HandshakeMessage;
/*      */ import io.rong.message.HasReceivedNotificationMessage;
/*      */ import io.rong.message.ImageMessage;
/*      */ import io.rong.message.InformationNotificationMessage;
/*      */ import io.rong.message.LocationMessage;
/*      */ import io.rong.message.MediaMessageContent;
/*      */ import io.rong.message.ProfileNotificationMessage;
/*      */ import io.rong.message.PublicServiceCommandMessage;
/*      */ import io.rong.message.PublicServiceMultiRichContentMessage;
/*      */ import io.rong.message.PublicServiceRichContentMessage;
/*      */ import io.rong.message.ReadReceiptMessage;
/*      */ import io.rong.message.ReadReceiptRequestMessage;
/*      */ import io.rong.message.ReadReceiptResponseMessage;
/*      */ import io.rong.message.RecallCommandMessage;
/*      */ import io.rong.message.RecallNotificationMessage;
/*      */ import io.rong.message.RichContentMessage;
/*      */ import io.rong.message.SuspendMessage;
/*      */ import io.rong.message.SyncReadStatusMessage;
/*      */ import io.rong.message.TextMessage;
/*      */ import io.rong.message.VoiceMessage;
/*      */ import io.rong.push.RongPushClient;
/*      */ import java.io.File;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.CountDownLatch;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.json.JSONObject;
/*      */ 
/*      */ public class RongIMClient
/*      */ {
/*      */   private static final String TAG = "RongIMClient";
/*      */   private IHandler mLibHandler;
/*      */   private static Handler mHandler;
/*      */   private final List<String> mRegCache;
/*      */   private Set<String> mChatroomCache;
/*      */   private Context mContext;
/*      */   private String mToken;
/*      */   private String mDeviceId;
/*      */   private String mAppKey;
/*      */   private String mCurrentUserId;
/*      */   private StatusListener mStatusListener;
/*      */   private ConnectChangeReceiver mConnectChangeReceiver;
/*      */   private ConnectRunnable mConnectRunnable;
/*      */   private ReconnectRunnable mReconnectRunnable;
/*      */   private DisconnectRunnable mDisconnectRunnable;
/*  121 */   private RongIMClient.ConnectionStatusListener.ConnectionStatus mConnectionStatus = RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED;
/*      */   private static ConnectionStatusListener sConnectionListener;
/*      */   private static OnReceiveMessageListener sReceiveMessageListener;
/*      */   private static ReadReceiptListener sReadReceiptListener;
/*      */   private static RecallMessageListener sRecallMessageListener;
/*      */   private SyncConversationReadStatusListener mSyncConversationReadStatusListener;
/*      */   private Handler mWorkHandler;
/*      */   private AidlConnection mAidlConnection;
/*      */   private boolean mHasConnect;
/*  131 */   private int mReconnectCount = 0;
/*  132 */   private int[] mReconnectInterval = { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512 };
/*      */   private static final int RECONNECT_INTERVAL = 1000;
/*      */   private static final String URL_STATISTIC = "https://stats.cn.ronghub.com/active.json";
/*  135 */   private static Map<Integer, RongIMClient.ConnectionStatusListener.ConnectionStatus> sStateMap = new HashMap();
/*  136 */   private Map<String, Message> mReadReceiptMap = new HashMap();
/*      */   private static final String CONVERSATION_SEPERATOR = "#@6RONG_CLOUD9@#";
/*      */   private List<String> mCmdObjectNameList;
/*      */   private static List<Integer> reconnectList;
/* 7712 */   private HashMap<String, CustomServiceProfile> customServiceCache = new HashMap();
/*      */   private static String mNaviServer;
/*      */   private static String mFileServer;
/*      */ 
/*      */   private RongIMClient()
/*      */   {
/*  200 */     RLog.i("RongIMClient", "RongIMClient");
/*  201 */     mHandler = new Handler(Looper.getMainLooper());
/*  202 */     this.mRegCache = new ArrayList();
/*  203 */     this.mChatroomCache = new HashSet();
/*  204 */     HandlerThread workThread = new HandlerThread("IPC_WORK");
/*  205 */     workThread.start();
/*  206 */     this.mStatusListener = new StatusListener();
/*  207 */     this.mWorkHandler = new Handler(workThread.getLooper());
/*  208 */     this.mConnectChangeReceiver = new ConnectChangeReceiver();
/*  209 */     this.mAidlConnection = new AidlConnection();
/*  210 */     this.mCmdObjectNameList = new ArrayList();
/*      */   }
/*      */ 
/*      */   public static RongIMClient getInstance()
/*      */   {
/*  223 */     return SingletonHolder.sInstance;
/*      */   }
/*      */ 
/*      */   private void initBindService() {
/*  227 */     Intent intent = new Intent(this.mContext, RongService.class);
/*  228 */     intent.putExtra("appKey", this.mAppKey);
/*  229 */     intent.putExtra("deviceId", this.mDeviceId);
/*      */     try {
/*  231 */       this.mContext.bindService(intent, this.mAidlConnection, 1);
/*      */     } catch (SecurityException e) {
/*  233 */       RLog.e("RongIMClient", "initBindService SecurityException");
/*  234 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   public RongIMClient.ConnectionStatusListener.ConnectionStatus getCurrentConnectionStatus()
/*      */   {
/*  318 */     return this.mConnectionStatus;
/*      */   }
/*      */ 
/*      */   private void registerCmdMsgType() {
/*  322 */     if (this.mLibHandler == null) {
/*  323 */       RLog.e("RongIMClient", "registerCmdMsgType IPC 进程尚未运行。");
/*  324 */       return;
/*      */     }
/*      */ 
/*  327 */     this.mCmdObjectNameList.add(((MessageTag)ReadReceiptMessage.class.getAnnotation(MessageTag.class)).value());
/*  328 */     this.mCmdObjectNameList.add(((MessageTag)ReadReceiptRequestMessage.class.getAnnotation(MessageTag.class)).value());
/*  329 */     this.mCmdObjectNameList.add(((MessageTag)ReadReceiptResponseMessage.class.getAnnotation(MessageTag.class)).value());
/*  330 */     this.mCmdObjectNameList.add(((MessageTag)TypingStatusMessage.class.getAnnotation(MessageTag.class)).value());
/*  331 */     this.mCmdObjectNameList.add(((MessageTag)RecallCommandMessage.class.getAnnotation(MessageTag.class)).value());
/*  332 */     this.mCmdObjectNameList.add(((MessageTag)SyncReadStatusMessage.class.getAnnotation(MessageTag.class)).value());
/*  333 */     this.mCmdObjectNameList.add(((MessageTag)CSEvaluateMessage.class.getAnnotation(MessageTag.class)).value());
/*  334 */     this.mCmdObjectNameList.add(((MessageTag)CSUpdateMessage.class.getAnnotation(MessageTag.class)).value());
/*  335 */     this.mCmdObjectNameList.add(((MessageTag)CSPullEvaluateMessage.class.getAnnotation(MessageTag.class)).value());
/*  336 */     this.mCmdObjectNameList.add(((MessageTag)CSChangeModeResponseMessage.class.getAnnotation(MessageTag.class)).value());
/*  337 */     this.mCmdObjectNameList.add(((MessageTag)CSTerminateMessage.class.getAnnotation(MessageTag.class)).value());
/*  338 */     this.mCmdObjectNameList.add(((MessageTag)CSSuspendMessage.class.getAnnotation(MessageTag.class)).value());
/*  339 */     this.mCmdObjectNameList.add(((MessageTag)RealTimeLocationJoinMessage.class.getAnnotation(MessageTag.class)).value());
/*  340 */     this.mCmdObjectNameList.add(((MessageTag)RealTimeLocationStatusMessage.class.getAnnotation(MessageTag.class)).value());
/*  341 */     this.mCmdObjectNameList.add(((MessageTag)RealTimeLocationQuitMessage.class.getAnnotation(MessageTag.class)).value());
/*  342 */     this.mCmdObjectNameList.add(((MessageTag)CSHandShakeResponseMessage.class.getAnnotation(MessageTag.class)).value());
/*  343 */     this.mCmdObjectNameList.add(((MessageTag)CSHandShakeMessage.class.getAnnotation(MessageTag.class)).value());
/*  344 */     this.mCmdObjectNameList.add(((MessageTag)CSChangeModeMessage.class.getAnnotation(MessageTag.class)).value());
/*  345 */     this.mCmdObjectNameList.add(((MessageTag)HasReceivedNotificationMessage.class.getAnnotation(MessageTag.class)).value());
/*      */     try
/*      */     {
/*  348 */       this.mLibHandler.registerCmdMsgType(this.mCmdObjectNameList);
/*      */     } catch (RemoteException e) {
/*  350 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initStatistics(Context context, String appKey)
/*      */   {
/*  418 */     if (Statistics.sharedInstance().isInitialized()) {
/*  419 */       return;
/*      */     }
/*  421 */     List certificates = new ArrayList();
/*  422 */     certificates.add("rongcloud");
/*  423 */     Statistics.enablePublicKeyPinning(certificates);
/*  424 */     Statistics.sharedInstance().init(context, "https://stats.cn.ronghub.com/active.json", appKey, this.mDeviceId);
/*  425 */     Statistics.sharedInstance().setLoggingEnabled(false);
/*  426 */     Statistics.sharedInstance().onStart();
/*      */   }
/*      */ 
/*      */   public static void init(Context context)
/*      */   {
/*  435 */     if (context == null) {
/*  436 */       throw new IllegalArgumentException("Context异常");
/*      */     }
/*  438 */     String currentProcess = SystemUtils.getCurrentProcessName(context);
/*  439 */     String mainProcess = context.getPackageName();
/*  440 */     RLog.d("RongIMClient", "init : " + currentProcess + ", " + mainProcess);
/*  441 */     if ((currentProcess == null) || (mainProcess == null) || (!mainProcess.equals(currentProcess))) {
/*  442 */       RLog.e("RongIMClient", "SDK should init in main process.");
/*  443 */       return;
/*      */     }
/*      */ 
/*  446 */     SingletonHolder.sInstance.mContext = context.getApplicationContext();
/*  447 */     if (TextUtils.isEmpty(SingletonHolder.sInstance.mAppKey)) {
/*      */       try {
/*  449 */         ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
/*  450 */         if (applicationInfo != null) {
/*  451 */           SingletonHolder.sInstance.mAppKey = applicationInfo.metaData.getString("RONG_CLOUD_APP_KEY");
/*      */         }
/*  453 */         if (TextUtils.isEmpty(SingletonHolder.sInstance.mAppKey))
/*  454 */           throw new IllegalArgumentException("can't find RONG_CLOUD_APP_KEY in AndroidManifest.xml.");
/*      */       }
/*      */       catch (PackageManager.NameNotFoundException e)
/*      */       {
/*  458 */         e.printStackTrace();
/*  459 */         throw new ExceptionInInitializerError("can't find packageName!");
/*      */       }
/*      */     }
/*      */ 
/*  463 */     SingletonHolder.sInstance.mDeviceId = DeviceUtils.getDeviceId(context, SingletonHolder.sInstance.mAppKey);
/*  464 */     SharedPreferences sp = context.getSharedPreferences("Statistics", 0);
/*  465 */     sp.edit().putString("appKey", SingletonHolder.sInstance.mAppKey).apply();
/*      */     try
/*      */     {
/*  468 */       registerMessageType(TextMessage.class);
/*  469 */       registerMessageType(VoiceMessage.class);
/*  470 */       registerMessageType(ImageMessage.class);
/*  471 */       registerMessageType(LocationMessage.class);
/*  472 */       registerMessageType(CommandNotificationMessage.class);
/*  473 */       registerMessageType(ContactNotificationMessage.class);
/*  474 */       registerMessageType(RichContentMessage.class);
/*  475 */       registerMessageType(PublicServiceMultiRichContentMessage.class);
/*  476 */       registerMessageType(PublicServiceRichContentMessage.class);
/*  477 */       registerMessageType(PublicServiceCommandMessage.class);
/*  478 */       registerMessageType(ProfileNotificationMessage.class);
/*  479 */       registerMessageType(HandshakeMessage.class);
/*  480 */       registerMessageType(InformationNotificationMessage.class);
/*  481 */       registerMessageType(DiscussionNotificationMessage.class);
/*  482 */       registerMessageType(SuspendMessage.class);
/*  483 */       registerMessageType(ReadReceiptMessage.class);
/*  484 */       registerMessageType(CommandMessage.class);
/*  485 */       registerMessageType(TypingStatusMessage.class);
/*  486 */       registerMessageType(CSHandShakeMessage.class);
/*  487 */       registerMessageType(CSHandShakeResponseMessage.class);
/*  488 */       registerMessageType(CSChangeModeMessage.class);
/*  489 */       registerMessageType(CSChangeModeResponseMessage.class);
/*  490 */       registerMessageType(CSSuspendMessage.class);
/*  491 */       registerMessageType(CSTerminateMessage.class);
/*  492 */       registerMessageType(CSEvaluateMessage.class);
/*  493 */       registerMessageType(CSUpdateMessage.class);
/*  494 */       registerMessageType(RecallCommandMessage.class);
/*  495 */       registerMessageType(RecallNotificationMessage.class);
/*  496 */       registerMessageType(ReadReceiptRequestMessage.class);
/*  497 */       registerMessageType(ReadReceiptResponseMessage.class);
/*  498 */       registerMessageType(SyncReadStatusMessage.class);
/*      */     } catch (AnnotationNotFoundException e) {
/*  500 */       e.printStackTrace();
/*      */     }
/*      */ 
/*  503 */     SingletonHolder.sInstance.initBindService();
/*  504 */     SingletonHolder.sInstance.initStatistics(context, SingletonHolder.sInstance.mAppKey);
/*  505 */     if (mNaviServer != null)
/*  506 */       RongPushClient.init(context, SingletonHolder.sInstance.mAppKey, mNaviServer);
/*      */     else {
/*  508 */       RongPushClient.init(context, SingletonHolder.sInstance.mAppKey);
/*      */     }
/*  510 */     TypingMessageManager.getInstance().init(context);
/*      */   }
/*      */ 
/*      */   public static void init(Context context, String appKey)
/*      */   {
/*  521 */     SingletonHolder.sInstance.mAppKey = appKey;
/*  522 */     init(context);
/*      */   }
/*      */ 
/*      */   public static RongIMClient connect(String token, ConnectCallback callback)
/*      */   {
/*  535 */     if (TextUtils.isEmpty(token)) {
/*  536 */       if (callback != null)
/*  537 */         callback.onTokenIncorrect();
/*  538 */       RLog.e("RongIMClient", "connect token is incorrect!");
/*  539 */       return SingletonHolder.sInstance;
/*      */     }
/*      */ 
/*  542 */     if (SingletonHolder.sInstance.mConnectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING) {
/*  543 */       RLog.e("RongIMClient", "connect Client is connecting!");
/*  544 */       if (callback != null)
/*  545 */         callback.onError(ErrorCode.RC_CONN_OVERFREQUENCY);
/*  546 */       return SingletonHolder.sInstance;
/*      */     }
/*      */ 
/*  549 */     if (SingletonHolder.sInstance.mReconnectRunnable != null) {
/*  550 */       mHandler.removeCallbacks(SingletonHolder.sInstance.mReconnectRunnable);
/*  551 */       SingletonHolder.sInstance.mReconnectRunnable = null;
/*      */     }
/*      */ 
/*  554 */     SingletonHolder.sInstance.mToken = token;
/*      */ 
/*  556 */     if (SingletonHolder.sInstance.mLibHandler == null) {
/*  557 */       RLog.d("RongIMClient", "connect mLibHandler is null, connect waiting for bind service");
/*  558 */       SingletonHolder.sInstance.mConnectRunnable = new ConnectRunnable(token, callback);
/*      */     } else {
/*  560 */       SingletonHolder.sInstance.mConnectRunnable = null;
/*  561 */       SingletonHolder.sInstance.mStatusListener.onStatusChange(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING);
/*      */       try {
/*  563 */         RLog.d("RongIMClient", "connect service binded, connect");
/*  564 */         SingletonHolder.sInstance.mLibHandler.connect(token, new IStringCallback.Stub(callback)
/*      */         {
/*      */           public void onComplete(String userId) throws RemoteException {
/*  567 */             RLog.d("RongIMClient", "connect callback onComplete");
/*      */ 
/*  569 */             RongIMClient.SingletonHolder.sInstance.mStatusListener.onStatusChange(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED);
/*      */ 
/*  571 */             RongIMClient.access$1902(RongIMClient.SingletonHolder.sInstance, userId);
/*  572 */             RongIMClient.access$102(RongIMClient.SingletonHolder.sInstance, 0);
/*  573 */             RongIMClient.access$902(RongIMClient.SingletonHolder.sInstance, true);
/*      */ 
/*  575 */             SharedPreferences sp = RongIMClient.SingletonHolder.sInstance.mContext.getSharedPreferences("Statistics", 0);
/*  576 */             sp.edit().putString("userId", userId).apply();
/*      */ 
/*  578 */             if (RongIMClient.SingletonHolder.sInstance.mReconnectRunnable != null) {
/*  579 */               RongIMClient.mHandler.removeCallbacks(RongIMClient.SingletonHolder.sInstance.mReconnectRunnable);
/*  580 */               RongIMClient.access$302(RongIMClient.SingletonHolder.sInstance, null);
/*      */             }
/*      */ 
/*  583 */             if (RongIMClient.SingletonHolder.sInstance.mDisconnectRunnable != null) {
/*  584 */               RongIMClient.SingletonHolder.sInstance.mWorkHandler.post(RongIMClient.SingletonHolder.sInstance.mDisconnectRunnable);
/*      */             }
/*      */ 
/*  587 */             if (this.val$callback != null)
/*  588 */               this.val$callback.onCallback(userId);
/*      */           }
/*      */ 
/*      */           public void onFailure(int errorCode)
/*      */             throws RemoteException
/*      */           {
/*  632 */             RLog.d("RongIMClient", "connect callback : onFailure = " + errorCode);
/*      */ 
/*  634 */             RongIMClient.SingletonHolder.sInstance.mStatusListener.onStatusChange((RongIMClient.ConnectionStatusListener.ConnectionStatus)RongIMClient.sStateMap.get(Integer.valueOf(errorCode)));
/*      */ 
/*  636 */             if (RongIMClient.SingletonHolder.sInstance.mDisconnectRunnable != null) {
/*  637 */               RongIMClient.access$602(RongIMClient.SingletonHolder.sInstance, null);
/*      */             }
/*      */ 
/*  640 */             if (errorCode == RongIMClient.ErrorCode.RC_CONN_USER_OR_PASSWD_ERROR.getValue()) {
/*  641 */               if (this.val$callback != null) {
/*  642 */                 this.val$callback.onTokenIncorrect();
/*      */               }
/*  644 */               RongIMClient.access$702(RongIMClient.SingletonHolder.sInstance, null);
/*      */             }
/*  646 */             else if (this.val$callback != null) {
/*  647 */               this.val$callback.onFail(RongIMClient.ErrorCode.valueOf(errorCode));
/*      */             }
/*      */           } } );
/*      */       }
/*      */       catch (RemoteException e) {
/*  653 */         RLog.d("RongIMClient", "connect RemoteException");
/*  654 */         e.printStackTrace();
/*      */       }
/*      */     }
/*  657 */     return SingletonHolder.sInstance;
/*      */   }
/*      */ 
/*      */   public void reconnect(ConnectCallback callback)
/*      */   {
/*  670 */     RLog.d("RongIMClient", "reconnect mConnectionStatus :" + this.mConnectionStatus);
/*  671 */     if (this.mToken == null) {
/*  672 */       if (callback != null) {
/*  673 */         callback.onFail(ErrorCode.RC_CONN_USER_OR_PASSWD_ERROR);
/*      */       }
/*  675 */       return;
/*      */     }
/*  677 */     ConnectivityManager cm = (ConnectivityManager)this.mContext.getSystemService("connectivity");
/*  678 */     NetworkInfo networkInfo = cm.getActiveNetworkInfo();
/*      */ 
/*  680 */     if (SingletonHolder.sInstance.mReconnectRunnable != null) {
/*  681 */       mHandler.removeCallbacks(SingletonHolder.sInstance.mReconnectRunnable);
/*  682 */       SingletonHolder.sInstance.mReconnectRunnable = null;
/*      */     }
/*      */ 
/*  685 */     if (this.mConnectionStatus != RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
/*  686 */       if ((networkInfo != null) && (networkInfo.isAvailable()))
/*  687 */         connect(this.mToken, callback);
/*      */     }
/*      */     else {
/*  690 */       if (networkInfo == null) {
/*  691 */         this.mStatusListener.onStatusChange(RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE);
/*      */       }
/*  693 */       if (callback != null)
/*  694 */         callback.onCallback(SingletonHolder.sInstance.mCurrentUserId);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void disconnect()
/*      */   {
/*  704 */     disconnect(true);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void disconnect(boolean isReceivePush)
/*      */   {
/*  716 */     RLog.d("RongIMClient", "disconnect isReceivePush = " + isReceivePush + ", mConnectionStatus = " + this.mConnectionStatus);
/*  717 */     if (this.mLibHandler == null) {
/*  718 */       RLog.e("RongIMClient", "disconnect IPC service unbind!");
/*  719 */       return;
/*      */     }
/*      */ 
/*  722 */     this.mChatroomCache.clear();
/*  723 */     if (this.mReconnectRunnable != null) {
/*  724 */       mHandler.removeCallbacks(this.mReconnectRunnable);
/*  725 */       this.mReconnectRunnable = null;
/*      */     }
/*  727 */     this.mReconnectCount = 0;
/*      */ 
/*  729 */     if ((this.mConnectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) || (this.mConnectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING))
/*      */     {
/*  732 */       this.mDisconnectRunnable = new DisconnectRunnable(isReceivePush);
/*      */ 
/*  734 */       if (this.mConnectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
/*  735 */         this.mWorkHandler.post(this.mDisconnectRunnable);
/*      */       }
/*      */ 
/*      */     }
/*  739 */     else if (this.mConnectionStatus != RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED) {
/*  740 */       this.mStatusListener.onStatusChange(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*  741 */       this.mToken = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void logout()
/*      */   {
/*  751 */     disconnect(false);
/*      */   }
/*      */ 
/*      */   public static void setConnectionStatusListener(ConnectionStatusListener listener)
/*      */   {
/*  760 */     sConnectionListener = listener;
/*      */   }
/*      */ 
/*      */   public static void setOnReceiveMessageListener(OnReceiveMessageListener listener)
/*      */   {
/*  770 */     sReceiveMessageListener = listener;
/*      */   }
/*      */ 
/*      */   public static void registerMessageType(Class<? extends MessageContent> messageContentClass)
/*      */     throws AnnotationNotFoundException
/*      */   {
/*  780 */     if (messageContentClass == null) {
/*  781 */       throw new IllegalArgumentException("MessageContent 为空！");
/*      */     }
/*  783 */     synchronized (SingletonHolder.sInstance.mRegCache) {
/*  784 */       if (!SingletonHolder.sInstance.mRegCache.contains(messageContentClass.getName())) {
/*  785 */         SingletonHolder.sInstance.mRegCache.add(messageContentClass.getName());
/*      */       }
/*      */     }
/*  788 */     RLog.d("RongIMClient", "registerMessageType " + messageContentClass.toString());
/*  789 */     if (SingletonHolder.sInstance.mLibHandler != null)
/*      */       try {
/*  791 */         SingletonHolder.sInstance.mLibHandler.registerMessageType(messageContentClass.getName());
/*      */       } catch (RemoteException e) {
/*  793 */         RLog.e("RongIMClient", "registerMessageType RemoteException");
/*  794 */         e.printStackTrace();
/*      */       }
/*      */   }
/*      */ 
/*      */   public void getConversationList(ResultCallback<List<Conversation>> callback)
/*      */   {
/*  806 */     this.mWorkHandler.post(new Runnable(callback)
/*      */     {
/*      */       public void run() {
/*  809 */         if (RongIMClient.this.mLibHandler == null) {
/*  810 */           if (this.val$callback != null)
/*  811 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*  812 */           return;
/*      */         }
/*      */         try {
/*  815 */           List conversations = RongIMClient.this.mLibHandler.getConversationList();
/*  816 */           if (this.val$callback != null)
/*  817 */             this.val$callback.onCallback(conversations);
/*      */         } catch (RemoteException e) {
/*  819 */           e.printStackTrace();
/*  820 */           if (this.val$callback != null)
/*  821 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public List<Conversation> getConversationList()
/*      */   {
/*  837 */     CountDownLatch latch = new CountDownLatch(1);
/*      */ 
/*  839 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/*  840 */     getConversationList(new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(List<Conversation> conversations)
/*      */       {
/*  844 */         this.val$result.t = conversations;
/*  845 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode errorCode)
/*      */       {
/*  850 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/*  855 */       latch.await();
/*      */     } catch (InterruptedException e) {
/*  857 */       e.printStackTrace();
/*      */     }
/*      */ 
/*  860 */     return (List)result.t;
/*      */   }
/*      */ 
/*      */   public void getConversationList(ResultCallback<List<Conversation>> callback, Conversation.ConversationType[] conversationTypes)
/*      */   {
/*  871 */     this.mWorkHandler.post(new Runnable(callback, conversationTypes)
/*      */     {
/*      */       public void run() {
/*  874 */         if (RongIMClient.this.mLibHandler == null) {
/*  875 */           if (this.val$callback != null)
/*  876 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*  877 */           return;
/*      */         }
/*  879 */         if (this.val$conversationTypes.length == 0) {
/*  880 */           this.val$callback.onCallback(null);
/*  881 */           return;
/*      */         }
/*      */         try
/*      */         {
/*  885 */           int[] typeValues = new int[this.val$conversationTypes.length];
/*      */ 
/*  887 */           for (int i = 0; i < this.val$conversationTypes.length; i++) {
/*  888 */             typeValues[i] = this.val$conversationTypes[i].getValue();
/*      */           }
/*  890 */           List conversationList = RongIMClient.this.mLibHandler.getConversationListByType(typeValues);
/*  891 */           if (this.val$callback != null)
/*      */           {
/*  893 */             this.val$callback.onCallback(conversationList);
/*      */           }
/*      */         }
/*      */         catch (RemoteException e) {
/*  897 */           e.printStackTrace();
/*  898 */           if (this.val$callback != null)
/*  899 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public List<Conversation> getConversationList(Conversation.ConversationType[] types)
/*      */   {
/*  918 */     CountDownLatch latch = new CountDownLatch(1);
/*  919 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/*      */ 
/*  921 */     getConversationList(new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(List<Conversation> conversationList)
/*      */       {
/*  925 */         this.val$result.t = conversationList;
/*  926 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode code)
/*      */       {
/*  931 */         this.val$latch.countDown();
/*      */       }
/*      */     }
/*      */     , types);
/*      */     try
/*      */     {
/*  936 */       latch.await();
/*      */     } catch (InterruptedException e) {
/*  938 */       e.printStackTrace();
/*      */     }
/*      */ 
/*  941 */     return (List)result.t;
/*      */   }
/*      */ 
/*      */   public void getConversation(Conversation.ConversationType conversationType, String targetId, ResultCallback<Conversation> callback)
/*      */   {
/*  953 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/*  954 */       RLog.e("RongIMClient", "getConversation. the parameter of targetId or ConversationType is error!");
/*  955 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/*  956 */       return;
/*      */     }
/*      */ 
/*  959 */     this.mWorkHandler.post(new Runnable(callback, conversationType, targetId)
/*      */     {
/*      */       public void run() {
/*  962 */         if (RongIMClient.this.mLibHandler == null) {
/*  963 */           if (this.val$callback != null)
/*  964 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*  965 */           return;
/*      */         }
/*      */         try {
/*  968 */           Conversation conversation = RongIMClient.this.mLibHandler.getConversation(this.val$conversationType.getValue(), this.val$targetId);
/*      */ 
/*  970 */           if (this.val$callback != null)
/*  971 */             this.val$callback.onCallback(conversation);
/*      */         }
/*      */         catch (RemoteException e) {
/*  974 */           e.printStackTrace();
/*  975 */           if (this.val$callback != null)
/*  976 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void updateConversationInfo(Conversation.ConversationType conversationType, String targetId, String title, String portrait, ResultCallback callback)
/*      */   {
/*  993 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/*  994 */       RLog.e("RongIMClient", "getConversation. the parameter of targetId or ConversationType is error!");
/*  995 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/*  996 */       return;
/*      */     }
/*      */ 
/*  999 */     this.mWorkHandler.post(new Runnable(callback, conversationType, targetId, title, portrait)
/*      */     {
/*      */       public void run() {
/* 1002 */         if (RongIMClient.this.mLibHandler == null) {
/* 1003 */           if (this.val$callback != null)
/* 1004 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 1005 */           return;
/*      */         }
/*      */         try {
/* 1008 */           boolean result = RongIMClient.this.mLibHandler.updateConversationInfo(this.val$conversationType.getValue(), this.val$targetId, this.val$title, this.val$portrait);
/*      */ 
/* 1010 */           if (this.val$callback != null)
/* 1011 */             this.val$callback.onCallback(Boolean.valueOf(result));
/*      */         }
/*      */         catch (RemoteException e)
/*      */         {
/* 1015 */           e.printStackTrace();
/* 1016 */           if (this.val$callback != null)
/* 1017 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Conversation getConversation(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 1032 */     CountDownLatch latch = new CountDownLatch(1);
/*      */ 
/* 1034 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 1035 */     getConversation(conversationType, targetId, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Conversation conversation) {
/* 1038 */         this.val$result.t = conversation;
/* 1039 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode code)
/*      */       {
/* 1045 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 1050 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 1052 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 1055 */     return (Conversation)result.t;
/*      */   }
/*      */ 
/*      */   public void removeConversation(Conversation.ConversationType conversationType, String targetId, ResultCallback<Boolean> callback)
/*      */   {
/* 1067 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/* 1068 */       RLog.e("RongIMClient", "getConversation. the parameter of targetId or ConversationType is error!");
/* 1069 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 1070 */       return;
/*      */     }
/*      */ 
/* 1073 */     this.mWorkHandler.post(new Runnable(callback, conversationType, targetId)
/*      */     {
/*      */       public void run() {
/* 1076 */         if (RongIMClient.this.mLibHandler == null) {
/* 1077 */           if (this.val$callback != null)
/* 1078 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 1079 */           return;
/*      */         }
/*      */         try {
/* 1082 */           boolean bool = RongIMClient.this.mLibHandler.removeConversation(this.val$conversationType.getValue(), this.val$targetId);
/*      */ 
/* 1084 */           if (this.val$callback != null)
/* 1085 */             this.val$callback.onCallback(Boolean.valueOf(bool));
/*      */         }
/*      */         catch (RemoteException e)
/*      */         {
/* 1089 */           e.printStackTrace();
/* 1090 */           if (this.val$callback != null)
/* 1091 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean removeConversation(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 1108 */     CountDownLatch latch = new CountDownLatch(1);
/*      */ 
/* 1110 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 1111 */     result.t = Boolean.valueOf(false);
/*      */ 
/* 1113 */     removeConversation(conversationType, targetId, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Boolean bool)
/*      */       {
/* 1117 */         if (bool != null)
/* 1118 */           this.val$result.t = bool;
/*      */         else {
/* 1120 */           RLog.e("RongIMClient", "removeConversation removeConversation is failure!");
/*      */         }
/*      */ 
/* 1123 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode code)
/*      */       {
/* 1129 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 1134 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 1136 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 1139 */     return ((Boolean)result.t).booleanValue();
/*      */   }
/*      */ 
/*      */   public void setConversationToTop(Conversation.ConversationType conversationType, String id, boolean isTop, ResultCallback<Boolean> callback)
/*      */   {
/* 1151 */     if ((TextUtils.isEmpty(id)) || (conversationType == null)) {
/* 1152 */       RLog.e("RongIMClient", "getConversation. the parameter of targetId or ConversationType is error!");
/* 1153 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 1154 */       return;
/*      */     }
/*      */ 
/* 1157 */     this.mWorkHandler.post(new Runnable(callback, conversationType, id, isTop)
/*      */     {
/*      */       public void run() {
/* 1160 */         if (RongIMClient.this.mLibHandler == null) {
/* 1161 */           if (this.val$callback != null)
/* 1162 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 1163 */           return;
/*      */         }
/*      */         try {
/* 1166 */           boolean result = RongIMClient.this.mLibHandler.setConversationTopStatus(this.val$conversationType.getValue(), this.val$id, this.val$isTop);
/* 1167 */           if (this.val$callback != null)
/*      */           {
/* 1169 */             this.val$callback.onCallback(Boolean.valueOf(result));
/*      */           }
/*      */         }
/*      */         catch (RemoteException e) {
/* 1173 */           e.printStackTrace();
/* 1174 */           if (this.val$callback != null)
/*      */           {
/* 1176 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean setConversationToTop(Conversation.ConversationType conversationType, String targetId, boolean isTop)
/*      */   {
/* 1194 */     CountDownLatch latch = new CountDownLatch(1);
/* 1195 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 1196 */     result.t = Boolean.valueOf(false);
/*      */ 
/* 1198 */     setConversationToTop(conversationType, targetId, isTop, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Boolean aBoolean)
/*      */       {
/* 1202 */         if (aBoolean != null)
/* 1203 */           this.val$result.t = aBoolean;
/*      */         else {
/* 1205 */           RLog.e("RongIMClient", "setConversationToTop setConversationToTop is failure!");
/*      */         }
/*      */ 
/* 1208 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode code)
/*      */       {
/* 1213 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 1218 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 1220 */       e.printStackTrace();
/*      */     }
/* 1222 */     return ((Boolean)result.t).booleanValue();
/*      */   }
/*      */ 
/*      */   public void getTotalUnreadCount(ResultCallback<Integer> callback)
/*      */   {
/* 1232 */     this.mWorkHandler.post(new Runnable(callback)
/*      */     {
/*      */       public void run() {
/* 1235 */         if (RongIMClient.this.mLibHandler == null) {
/* 1236 */           if (this.val$callback != null)
/* 1237 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 1238 */           return;
/*      */         }
/* 1240 */         if ((!RongIMClient.this.mHasConnect) && (this.val$callback != null)) {
/* 1241 */           RLog.d("RongIMClient", "getTotalUnreadCount Has connect");
/* 1242 */           this.val$callback.onCallback(Integer.valueOf(0));
/* 1243 */           return;
/*      */         }
/*      */         try
/*      */         {
/* 1247 */           int count = RongIMClient.this.mLibHandler.getTotalUnreadCount();
/* 1248 */           if (this.val$callback != null)
/*      */           {
/* 1250 */             this.val$callback.onCallback(Integer.valueOf(count));
/*      */           }
/*      */         }
/*      */         catch (RemoteException e) {
/* 1254 */           if (this.val$callback != null)
/*      */           {
/* 1256 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getTotalUnreadCount()
/*      */   {
/* 1271 */     CountDownLatch latch = new CountDownLatch(1);
/* 1272 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 1273 */     result.t = Integer.valueOf(0);
/*      */ 
/* 1275 */     getTotalUnreadCount(new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Integer count)
/*      */       {
/* 1279 */         if (count != null)
/* 1280 */           this.val$result.t = count;
/*      */         else {
/* 1282 */           RLog.e("RongIMClient", "getTotalUnreadCount getTotalUnreadCount is failure");
/*      */         }
/*      */ 
/* 1285 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 1290 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 1295 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 1297 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 1300 */     return ((Integer)result.t).intValue();
/*      */   }
/*      */ 
/*      */   public void getUnreadCount(Conversation.ConversationType conversationType, String targetId, ResultCallback<Integer> callback)
/*      */   {
/* 1311 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/* 1312 */       RLog.e("RongIMClient", "getConversation. the parameter of targetId or ConversationType is error!");
/* 1313 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 1314 */       return;
/*      */     }
/*      */ 
/* 1317 */     this.mWorkHandler.post(new Runnable(callback, conversationType, targetId)
/*      */     {
/*      */       public void run() {
/* 1320 */         if (RongIMClient.this.mLibHandler == null) {
/* 1321 */           if (this.val$callback != null)
/* 1322 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 1323 */           return;
/*      */         }
/*      */         try {
/* 1326 */           int count = RongIMClient.this.mLibHandler.getUnreadCountById(this.val$conversationType.getValue(), this.val$targetId);
/* 1327 */           if (this.val$callback != null)
/* 1328 */             this.val$callback.onCallback(Integer.valueOf(count));
/*      */         }
/*      */         catch (RemoteException e) {
/* 1331 */           if (this.val$callback != null)
/* 1332 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getUnreadCount(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 1350 */     CountDownLatch latch = new CountDownLatch(1);
/* 1351 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 1352 */     result.t = Integer.valueOf(0);
/*      */ 
/* 1354 */     getUnreadCount(conversationType, targetId, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Integer count) {
/* 1357 */         if (count != null)
/* 1358 */           this.val$result.t = count;
/*      */         else {
/* 1360 */           RLog.e("RongIMClient", "getUnreadCount getUnreadCount is failure!");
/*      */         }
/* 1362 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 1367 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 1372 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 1374 */       e.printStackTrace();
/*      */     }
/* 1376 */     return ((Integer)result.t).intValue();
/*      */   }
/*      */ 
/*      */   public void getUnreadCount(ResultCallback<Integer> callback, Conversation.ConversationType[] conversationTypes)
/*      */   {
/* 1386 */     if ((conversationTypes == null) || (conversationTypes.length == 0)) {
/* 1387 */       Log.i("RongIMClient", "conversationTypes is null. Return directly!!!");
/* 1388 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 1389 */       return;
/*      */     }
/*      */ 
/* 1392 */     this.mWorkHandler.post(new Runnable(callback, conversationTypes)
/*      */     {
/*      */       public void run() {
/* 1395 */         if (RongIMClient.this.mLibHandler == null) {
/* 1396 */           if (this.val$callback != null)
/* 1397 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 1398 */           return;
/*      */         }
/*      */         try {
/* 1401 */           int[] types = new int[this.val$conversationTypes.length];
/*      */ 
/* 1403 */           int i = 0;
/*      */ 
/* 1405 */           for (Conversation.ConversationType type : this.val$conversationTypes) {
/* 1406 */             types[i] = type.getValue();
/* 1407 */             i++;
/*      */           }
/* 1409 */           int count = RongIMClient.this.mLibHandler.getUnreadCount(types);
/*      */ 
/* 1411 */           if (this.val$callback != null)
/* 1412 */             this.val$callback.onCallback(Integer.valueOf(count));
/*      */         }
/*      */         catch (RemoteException e)
/*      */         {
/* 1416 */           if (this.val$callback != null)
/* 1417 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getUnreadCount(Conversation.ConversationType[] conversationTypes, ResultCallback<Integer> callback)
/*      */   {
/* 1433 */     getUnreadCount(callback, conversationTypes);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public int getUnreadCount(Conversation.ConversationType[] conversationTypes)
/*      */   {
/* 1444 */     CountDownLatch latch = new CountDownLatch(1);
/* 1445 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 1446 */     result.t = Integer.valueOf(0);
/*      */ 
/* 1448 */     getUnreadCount(new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Integer count) {
/* 1451 */         if (count != null)
/* 1452 */           this.val$result.t = count;
/*      */         else {
/* 1454 */           RLog.e("RongIMClient", "getUnreadCount getUnreadCount is failure!");
/*      */         }
/*      */ 
/* 1457 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 1462 */         this.val$latch.countDown();
/*      */       }
/*      */     }
/*      */     , conversationTypes);
/*      */     try
/*      */     {
/* 1467 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 1469 */       e.printStackTrace();
/*      */     }
/* 1471 */     return ((Integer)result.t).intValue();
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public List<Message> getLatestMessages(Conversation.ConversationType conversationType, String targetId, int count)
/*      */   {
/* 1487 */     CountDownLatch latch = new CountDownLatch(1);
/*      */ 
/* 1489 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/*      */ 
/* 1491 */     getLatestMessages(conversationType, targetId, count, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(List<Message> messages) {
/* 1494 */         this.val$result.t = messages;
/* 1495 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 1501 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 1506 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 1508 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 1511 */     return (List)result.t;
/*      */   }
/*      */ 
/*      */   public void getLatestMessages(Conversation.ConversationType conversationType, String targetId, int count, ResultCallback<List<Message>> callback)
/*      */   {
/* 1523 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/* 1524 */       RLog.e("RongIMClient", "the parameter of targetId or ConversationType is error!");
/* 1525 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 1526 */       return;
/*      */     }
/*      */ 
/* 1529 */     this.mWorkHandler.post(new Runnable(callback, conversationType, targetId, count)
/*      */     {
/*      */       public void run() {
/* 1532 */         if (RongIMClient.this.mLibHandler == null) {
/* 1533 */           if (this.val$callback != null)
/* 1534 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 1535 */           return;
/*      */         }
/* 1537 */         Conversation conversation = new Conversation();
/* 1538 */         conversation.setConversationType(this.val$conversationType);
/* 1539 */         conversation.setTargetId(this.val$targetId);
/*      */         try {
/* 1541 */           List messages = RongIMClient.this.mLibHandler.getNewestMessages(conversation, this.val$count);
/*      */ 
/* 1543 */           if (this.val$callback != null)
/*      */           {
/* 1545 */             this.val$callback.onCallback(messages);
/*      */           }
/*      */         } catch (RemoteException e) {
/* 1548 */           e.printStackTrace();
/* 1549 */           if (this.val$callback != null)
/*      */           {
/* 1551 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public List<Message> getHistoryMessages(Conversation.ConversationType conversationType, String targetId, int oldestMessageId, int count)
/*      */   {
/* 1574 */     CountDownLatch latch = new CountDownLatch(1);
/*      */ 
/* 1576 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/*      */ 
/* 1578 */     getHistoryMessages(conversationType, targetId, oldestMessageId, count, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(List<Message> messages) {
/* 1581 */         this.val$result.t = messages;
/* 1582 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 1588 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 1593 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 1595 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 1598 */     return (List)result.t;
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public List<Message> getHistoryMessages(Conversation.ConversationType conversationType, String targetId, String objectName, int oldestMessageId, int count)
/*      */   {
/* 1617 */     CountDownLatch latch = new CountDownLatch(1);
/*      */ 
/* 1619 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/*      */ 
/* 1621 */     getHistoryMessages(conversationType, targetId, objectName, oldestMessageId, count, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(List<Message> messages) {
/* 1624 */         this.val$result.t = messages;
/* 1625 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 1631 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 1636 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 1638 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 1641 */     return (List)result.t;
/*      */   }
/*      */ 
/*      */   public void getHistoryMessages(Conversation.ConversationType conversationType, String targetId, String objectName, int oldestMessageId, int count, ResultCallback<List<Message>> callback)
/*      */   {
/* 1656 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/* 1657 */       RLog.e("RongIMClient", "the parameter of targetId or ConversationType is error!");
/* 1658 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 1659 */       return;
/*      */     }
/*      */ 
/* 1662 */     this.mWorkHandler.post(new Runnable(callback, conversationType, targetId, objectName, oldestMessageId, count)
/*      */     {
/*      */       public void run() {
/* 1665 */         if (RongIMClient.this.mLibHandler == null) {
/* 1666 */           if (this.val$callback != null)
/* 1667 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 1668 */           return;
/*      */         }
/* 1670 */         Conversation conversation = new Conversation();
/* 1671 */         conversation.setConversationType(this.val$conversationType);
/* 1672 */         conversation.setTargetId(this.val$targetId);
/*      */         try {
/* 1674 */           List messages = RongIMClient.this.mLibHandler.getOlderMessagesByObjectName(conversation, this.val$objectName, this.val$oldestMessageId, this.val$count, true);
/*      */ 
/* 1676 */           if (this.val$callback != null)
/*      */           {
/* 1678 */             this.val$callback.onCallback(messages);
/*      */           }
/*      */         }
/*      */         catch (RemoteException e) {
/* 1682 */           e.printStackTrace();
/* 1683 */           if (this.val$callback != null)
/*      */           {
/* 1685 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getHistoryMessages(Conversation.ConversationType conversationType, String targetId, String objectName, int baseMessageId, int count, RongCommonDefine.GetMessageDirection direction, ResultCallback<List<Message>> callback)
/*      */   {
/* 1708 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/* 1709 */       RLog.e("RongIMClient", "the parameter of targetId or ConversationType is error!");
/* 1710 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 1711 */       return;
/*      */     }
/* 1713 */     if ((TextUtils.isEmpty(objectName)) || (count <= 0) || (direction == null)) {
/* 1714 */       RLog.e("RongIMClient", "the parameter of objectName, count or direction is error!");
/* 1715 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 1716 */       return;
/*      */     }
/* 1718 */     this.mWorkHandler.post(new Runnable(callback, conversationType, targetId, objectName, baseMessageId, count, direction)
/*      */     {
/*      */       public void run() {
/* 1721 */         if (RongIMClient.this.mLibHandler == null) {
/* 1722 */           if (this.val$callback != null)
/* 1723 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 1724 */           return;
/*      */         }
/* 1726 */         Conversation conversation = new Conversation();
/* 1727 */         conversation.setConversationType(this.val$conversationType);
/* 1728 */         conversation.setTargetId(this.val$targetId);
/*      */         try {
/* 1730 */           List messages = RongIMClient.this.mLibHandler.getOlderMessagesByObjectName(conversation, this.val$objectName, this.val$baseMessageId, this.val$count, this.val$direction.equals(RongCommonDefine.GetMessageDirection.FRONT));
/* 1731 */           if (this.val$callback != null)
/* 1732 */             this.val$callback.onCallback(messages);
/*      */         } catch (RemoteException e) {
/* 1734 */           e.printStackTrace();
/* 1735 */           if (this.val$callback != null)
/* 1736 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getRemoteHistoryMessages(Conversation.ConversationType conversationType, String targetId, long dateTime, int count, ResultCallback<List<Message>> callback)
/*      */   {
/* 1755 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/* 1756 */       RLog.e("RongIMClient", "the parameter of targetId or ConversationType is error!");
/* 1757 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 1758 */       return;
/*      */     }
/*      */ 
/* 1761 */     this.mWorkHandler.post(new Runnable(callback, conversationType, targetId, dateTime, count)
/*      */     {
/*      */       public void run() {
/* 1764 */         if (RongIMClient.this.mLibHandler == null) {
/* 1765 */           if (this.val$callback != null)
/* 1766 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 1767 */           return;
/*      */         }
/* 1769 */         Conversation conversation = new Conversation();
/* 1770 */         conversation.setConversationType(this.val$conversationType);
/* 1771 */         conversation.setTargetId(this.val$targetId);
/*      */         try {
/* 1773 */           RongIMClient.this.mLibHandler.getRemoteHistoryMessages(conversation, this.val$dateTime, this.val$count, new IResultCallback.Stub()
/*      */           {
/*      */             public void onComplete(RemoteModelWrap model)
/*      */               throws RemoteException
/*      */             {
/* 1778 */               if (RongIMClient.25.this.val$callback != null)
/* 1779 */                 if ((model != null) && (model.getContent() != null) && ((model.getContent() instanceof RongListWrap))) {
/* 1780 */                   RongListWrap rongListWrap = (RongListWrap)model.getContent();
/* 1781 */                   RongIMClient.25.this.val$callback.onCallback(rongListWrap.getList());
/*      */                 } else {
/* 1783 */                   RongIMClient.25.this.val$callback.onCallback(null);
/*      */                 }
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 1791 */               if (RongIMClient.25.this.val$callback != null)
/* 1792 */                 RongIMClient.25.this.val$callback.onFail(RongIMClient.ErrorCode.valueOf(errorCode));
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 1797 */           e.printStackTrace();
/* 1798 */           if (this.val$callback != null)
/* 1799 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getHistoryMessages(Conversation.ConversationType conversationType, String targetId, int oldestMessageId, int count, ResultCallback<List<Message>> callback)
/*      */   {
/* 1818 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/* 1819 */       RLog.e("RongIMClient", "the parameter of targetId or ConversationType is error!");
/* 1820 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 1821 */       return;
/*      */     }
/*      */ 
/* 1824 */     this.mWorkHandler.post(new Runnable(callback, conversationType, targetId, oldestMessageId, count)
/*      */     {
/*      */       public void run() {
/* 1827 */         if (RongIMClient.this.mLibHandler == null) {
/* 1828 */           if (this.val$callback != null)
/* 1829 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 1830 */           return;
/*      */         }
/* 1832 */         Conversation conversation = new Conversation();
/* 1833 */         conversation.setConversationType(this.val$conversationType);
/* 1834 */         conversation.setTargetId(this.val$targetId);
/*      */         try {
/* 1836 */           List messages = RongIMClient.this.mLibHandler.getOlderMessages(conversation, this.val$oldestMessageId, this.val$count);
/*      */ 
/* 1838 */           if (this.val$callback != null)
/*      */           {
/* 1840 */             this.val$callback.onCallback(messages);
/*      */           }
/*      */         } catch (RemoteException e) {
/* 1843 */           e.printStackTrace();
/* 1844 */           if (this.val$callback != null)
/*      */           {
/* 1846 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean deleteMessages(int[] messageIds)
/*      */   {
/* 1865 */     CountDownLatch latch = new CountDownLatch(1);
/*      */ 
/* 1867 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 1868 */     result.t = Boolean.valueOf(false);
/*      */ 
/* 1870 */     deleteMessages(messageIds, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Boolean bool) {
/* 1873 */         if (bool != null)
/* 1874 */           this.val$result.t = bool;
/*      */         else {
/* 1876 */           RLog.e("RongIMClient", "deleteMessages deleteMessages is failure!");
/*      */         }
/* 1878 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 1884 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 1889 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 1891 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 1894 */     return ((Boolean)result.t).booleanValue();
/*      */   }
/*      */ 
/*      */   public void deleteMessages(int[] messageIds, ResultCallback<Boolean> callback)
/*      */   {
/* 1904 */     if ((messageIds == null) || (messageIds.length == 0)) {
/* 1905 */       RLog.e("RongIMClient", "the messageIds is null!");
/* 1906 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 1907 */       return;
/*      */     }
/*      */ 
/* 1910 */     for (int id : messageIds) {
/* 1911 */       if (id <= 0) {
/* 1912 */         RLog.e("RongIMClient", "the messageIds contains 0 value!");
/* 1913 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 1914 */         return;
/*      */       }
/*      */     }
/*      */ 
/* 1918 */     this.mWorkHandler.post(new Runnable(callback, messageIds)
/*      */     {
/*      */       public void run() {
/* 1921 */         if (RongIMClient.this.mLibHandler == null) {
/* 1922 */           if (this.val$callback != null)
/* 1923 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 1924 */           return;
/*      */         }
/*      */         try {
/* 1927 */           boolean bool = RongIMClient.this.mLibHandler.deleteMessage(this.val$messageIds);
/* 1928 */           if (this.val$callback != null)
/* 1929 */             this.val$callback.onCallback(Boolean.valueOf(bool));
/*      */         } catch (RemoteException e) {
/* 1931 */           e.printStackTrace();
/* 1932 */           if (this.val$callback != null)
/* 1933 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void deleteMessages(Conversation.ConversationType conversationType, String targetId, ResultCallback<Boolean> callback)
/*      */   {
/* 1949 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/* 1950 */       RLog.e("RongIMClient", "the parameter of targetId or ConversationType is error!");
/* 1951 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 1952 */       return;
/*      */     }
/*      */ 
/* 1955 */     this.mWorkHandler.post(new Runnable(callback, conversationType, targetId)
/*      */     {
/*      */       public void run() {
/* 1958 */         if (RongIMClient.this.mLibHandler == null) {
/* 1959 */           if (this.val$callback != null)
/* 1960 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 1961 */           return;
/*      */         }
/*      */         try {
/* 1964 */           boolean bool = RongIMClient.this.mLibHandler.deleteConversationMessage(this.val$conversationType.getValue(), this.val$targetId);
/* 1965 */           if (this.val$callback != null)
/* 1966 */             this.val$callback.onCallback(Boolean.valueOf(bool));
/*      */         } catch (RemoteException e) {
/* 1968 */           e.printStackTrace();
/* 1969 */           if (this.val$callback != null)
/* 1970 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean clearMessages(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 1987 */     CountDownLatch latch = new CountDownLatch(1);
/*      */ 
/* 1989 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 1990 */     result.t = Boolean.valueOf(false);
/*      */ 
/* 1992 */     clearMessages(conversationType, targetId, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Boolean bool)
/*      */       {
/* 1996 */         if (bool != null)
/* 1997 */           this.val$result.t = bool;
/*      */         else {
/* 1999 */           RLog.e("RongIMClient", "clearMessage clearMessages is failure!");
/*      */         }
/*      */ 
/* 2002 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 2008 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 2013 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 2015 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 2018 */     return ((Boolean)result.t).booleanValue();
/*      */   }
/*      */ 
/*      */   public void clearMessages(Conversation.ConversationType conversationType, String targetId, ResultCallback<Boolean> callback)
/*      */   {
/* 2029 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/* 2030 */       RLog.e("RongIMClient", "the parameter of targetId or ConversationType is error!");
/* 2031 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 2032 */       return;
/*      */     }
/*      */ 
/* 2035 */     this.mWorkHandler.post(new Runnable(callback, conversationType, targetId)
/*      */     {
/*      */       public void run() {
/* 2038 */         if (RongIMClient.this.mLibHandler == null) {
/* 2039 */           if (this.val$callback != null)
/* 2040 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 2041 */           return;
/*      */         }
/* 2043 */         Conversation conversation = new Conversation();
/* 2044 */         conversation.setConversationType(this.val$conversationType);
/* 2045 */         conversation.setTargetId(this.val$targetId);
/*      */         try {
/* 2047 */           boolean bool = RongIMClient.this.mLibHandler.clearMessages(conversation);
/* 2048 */           if (this.val$callback != null)
/* 2049 */             this.val$callback.onCallback(Boolean.valueOf(bool));
/*      */         }
/*      */         catch (RemoteException e) {
/* 2052 */           e.printStackTrace();
/* 2053 */           if (this.val$callback != null)
/* 2054 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean clearMessagesUnreadStatus(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 2072 */     CountDownLatch latch = new CountDownLatch(1);
/*      */ 
/* 2074 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 2075 */     result.t = Boolean.valueOf(false);
/*      */ 
/* 2077 */     clearMessagesUnreadStatus(conversationType, targetId, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Boolean bool) {
/* 2080 */         if (bool != null)
/* 2081 */           this.val$result.t = bool;
/*      */         else {
/* 2083 */           RLog.e("RongIMClient", "clearMessagesUnreadStatus clearMessagesUnreadStatus is failure!");
/*      */         }
/*      */ 
/* 2086 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 2091 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 2096 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 2098 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 2101 */     return ((Boolean)result.t).booleanValue();
/*      */   }
/*      */ 
/*      */   public void clearMessagesUnreadStatus(Conversation.ConversationType conversationType, String targetId, ResultCallback<Boolean> callback)
/*      */   {
/* 2112 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/* 2113 */       RLog.e("RongIMClient", "the parameter of targetId or ConversationType is error!");
/* 2114 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 2115 */       return;
/*      */     }
/*      */ 
/* 2118 */     this.mWorkHandler.post(new Runnable(callback, conversationType, targetId)
/*      */     {
/*      */       public void run() {
/* 2121 */         if (RongIMClient.this.mLibHandler == null) {
/* 2122 */           if (this.val$callback != null)
/* 2123 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 2124 */           return;
/*      */         }
/* 2126 */         Conversation conversation = new Conversation();
/* 2127 */         conversation.setConversationType(this.val$conversationType);
/* 2128 */         conversation.setTargetId(this.val$targetId);
/*      */         try {
/* 2130 */           boolean bool = RongIMClient.this.mLibHandler.clearMessagesUnreadStatus(conversation);
/*      */ 
/* 2132 */           if (this.val$callback != null)
/* 2133 */             this.val$callback.onCallback(Boolean.valueOf(bool));
/*      */         }
/*      */         catch (RemoteException e) {
/* 2136 */           e.printStackTrace();
/*      */ 
/* 2138 */           if (this.val$callback != null)
/* 2139 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean setMessageExtra(int messageId, String value)
/*      */   {
/* 2157 */     CountDownLatch latch = new CountDownLatch(1);
/*      */ 
/* 2159 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 2160 */     result.t = Boolean.valueOf(false);
/*      */ 
/* 2162 */     setMessageExtra(messageId, value, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Boolean bool)
/*      */       {
/* 2166 */         if (bool != null)
/* 2167 */           this.val$result.t = bool;
/*      */         else {
/* 2169 */           RLog.e("RongIMClient", "setMessageExtra setMessageExtra is failure!");
/*      */         }
/*      */ 
/* 2172 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 2178 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 2183 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 2185 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 2188 */     return ((Boolean)result.t).booleanValue();
/*      */   }
/*      */ 
/*      */   public void setMessageExtra(int messageId, String value, ResultCallback<Boolean> callback)
/*      */   {
/* 2199 */     if (messageId == 0) {
/* 2200 */       RLog.e("RongIMClient", "messageId is error!");
/* 2201 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 2202 */       return;
/*      */     }
/*      */ 
/* 2205 */     this.mWorkHandler.post(new Runnable(callback, messageId, value)
/*      */     {
/*      */       public void run() {
/* 2208 */         if (RongIMClient.this.mLibHandler == null) {
/* 2209 */           if (this.val$callback != null)
/* 2210 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 2211 */           return;
/*      */         }
/*      */         try {
/* 2214 */           boolean bool = RongIMClient.this.mLibHandler.setMessageExtra(this.val$messageId, this.val$value);
/*      */ 
/* 2216 */           if (this.val$callback != null)
/* 2217 */             this.val$callback.onCallback(Boolean.valueOf(bool));
/*      */         }
/*      */         catch (RemoteException e) {
/* 2220 */           e.printStackTrace();
/* 2221 */           if (this.val$callback != null)
/* 2222 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean setMessageReceivedStatus(int messageId, Message.ReceivedStatus receivedStatus)
/*      */   {
/* 2240 */     CountDownLatch latch = new CountDownLatch(1);
/*      */ 
/* 2242 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 2243 */     result.t = Boolean.valueOf(false);
/*      */ 
/* 2245 */     setMessageReceivedStatus(messageId, receivedStatus, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Boolean bool)
/*      */       {
/* 2249 */         if (bool != null)
/* 2250 */           this.val$result.t = bool;
/*      */         else {
/* 2252 */           RLog.e("RongIMClient", "setMessageReceivedStatus setMessageReceivedStatus is failure!");
/*      */         }
/*      */ 
/* 2255 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 2261 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 2266 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 2268 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 2271 */     return ((Boolean)result.t).booleanValue();
/*      */   }
/*      */ 
/*      */   public void setMessageReceivedStatus(int messageId, Message.ReceivedStatus receivedStatus, ResultCallback<Boolean> callback)
/*      */   {
/* 2282 */     if (messageId == 0) {
/* 2283 */       RLog.e("RongIMClient", "Error.The messageId can't be 0!");
/* 2284 */       if (callback != null)
/* 2285 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 2286 */       return;
/*      */     }
/*      */ 
/* 2289 */     this.mWorkHandler.post(new Runnable(callback, messageId, receivedStatus)
/*      */     {
/*      */       public void run()
/*      */       {
/* 2293 */         if (RongIMClient.this.mLibHandler == null) {
/* 2294 */           if (this.val$callback != null)
/* 2295 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 2296 */           return;
/*      */         }
/*      */         try {
/* 2299 */           boolean bool = RongIMClient.this.mLibHandler.setMessageReceivedStatus(this.val$messageId, this.val$receivedStatus.getFlag());
/* 2300 */           if (this.val$callback != null)
/* 2301 */             this.val$callback.onCallback(Boolean.valueOf(bool));
/*      */         }
/*      */         catch (RemoteException e) {
/* 2304 */           e.printStackTrace();
/* 2305 */           if (this.val$callback != null)
/* 2306 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean setMessageSentStatus(int messageId, Message.SentStatus sentStatus)
/*      */   {
/* 2323 */     CountDownLatch latch = new CountDownLatch(1);
/*      */ 
/* 2325 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 2326 */     result.t = Boolean.valueOf(false);
/*      */ 
/* 2328 */     setMessageSentStatus(messageId, sentStatus, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Boolean bool)
/*      */       {
/* 2332 */         if (bool != null)
/* 2333 */           this.val$result.t = bool;
/*      */         else {
/* 2335 */           RLog.e("RongIMClient", "setMessageSentStatus setMessageSentStatus is failure!");
/*      */         }
/*      */ 
/* 2338 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 2344 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 2349 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 2351 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 2354 */     return ((Boolean)result.t).booleanValue();
/*      */   }
/*      */ 
/*      */   public void setMessageSentStatus(int messageId, Message.SentStatus sentStatus, ResultCallback<Boolean> callback)
/*      */   {
/* 2365 */     if (messageId == 0) {
/* 2366 */       RLog.e("RongIMClient", "Error.The messageId can't be 0!");
/* 2367 */       if (callback != null)
/* 2368 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 2369 */       return;
/*      */     }
/*      */ 
/* 2372 */     this.mWorkHandler.post(new Runnable(callback, messageId, sentStatus)
/*      */     {
/*      */       public void run() {
/* 2375 */         if (RongIMClient.this.mLibHandler == null) {
/* 2376 */           if (this.val$callback != null)
/* 2377 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 2378 */           return;
/*      */         }
/*      */         try {
/* 2381 */           boolean bool = RongIMClient.this.mLibHandler.setMessageSentStatus(this.val$messageId, this.val$sentStatus.getValue());
/*      */ 
/* 2383 */           if (this.val$callback != null)
/* 2384 */             this.val$callback.onCallback(Boolean.valueOf(bool));
/*      */         }
/*      */         catch (RemoteException e) {
/* 2387 */           e.printStackTrace();
/* 2388 */           if (this.val$callback != null)
/* 2389 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public String getTextMessageDraft(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 2406 */     CountDownLatch latch = new CountDownLatch(1);
/*      */ 
/* 2408 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/*      */ 
/* 2410 */     getTextMessageDraft(conversationType, targetId, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(String txt) {
/* 2413 */         this.val$result.t = txt;
/* 2414 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 2420 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 2425 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 2427 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 2430 */     return (String)result.t;
/*      */   }
/*      */ 
/*      */   public void getTextMessageDraft(Conversation.ConversationType conversationType, String targetId, ResultCallback<String> callback)
/*      */   {
/* 2442 */     Conversation conversation = new Conversation();
/* 2443 */     conversation.setConversationType(conversationType);
/* 2444 */     conversation.setTargetId(targetId);
/*      */ 
/* 2446 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/* 2447 */       RLog.e("RongIMClient", "the value of targetId or ConversationType is error!");
/* 2448 */       if (callback != null)
/* 2449 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 2450 */       return;
/*      */     }
/*      */ 
/* 2453 */     this.mWorkHandler.post(new Runnable(callback, conversation)
/*      */     {
/*      */       public void run() {
/* 2456 */         if (RongIMClient.this.mLibHandler == null) {
/* 2457 */           if (this.val$callback != null)
/* 2458 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 2459 */           return;
/*      */         }
/*      */         try {
/* 2462 */           String content = RongIMClient.this.mLibHandler.getTextMessageDraft(this.val$conversation);
/* 2463 */           if (this.val$callback != null)
/* 2464 */             this.val$callback.onCallback(content);
/*      */         }
/*      */         catch (RemoteException e) {
/* 2467 */           e.printStackTrace();
/* 2468 */           if (this.val$callback != null)
/* 2469 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean saveTextMessageDraft(Conversation.ConversationType conversationType, String targetId, String content)
/*      */   {
/* 2488 */     CountDownLatch latch = new CountDownLatch(1);
/*      */ 
/* 2490 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 2491 */     result.t = Boolean.valueOf(false);
/*      */ 
/* 2493 */     saveTextMessageDraft(conversationType, targetId, content, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Boolean bool)
/*      */       {
/* 2497 */         if (bool != null)
/* 2498 */           this.val$result.t = bool;
/*      */         else {
/* 2500 */           RLog.e("RongIMClient", "saveTextMessageDraft saveTextMessageDraft is failure!");
/*      */         }
/*      */ 
/* 2503 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 2509 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 2514 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 2516 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 2519 */     return ((Boolean)result.t).booleanValue();
/*      */   }
/*      */ 
/*      */   public void saveTextMessageDraft(Conversation.ConversationType conversationType, String targetId, String content, ResultCallback<Boolean> callback)
/*      */   {
/* 2531 */     Conversation conversation = new Conversation();
/* 2532 */     conversation.setConversationType(conversationType);
/* 2533 */     conversation.setTargetId(targetId);
/*      */ 
/* 2535 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/* 2536 */       RLog.e("RongIMClient", "the value of targetId or ConversationType is error!");
/* 2537 */       if (callback != null)
/* 2538 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 2539 */       return;
/*      */     }
/*      */ 
/* 2542 */     this.mWorkHandler.post(new Runnable(callback, conversation, content)
/*      */     {
/*      */       public void run() {
/* 2545 */         if (RongIMClient.this.mLibHandler == null) {
/* 2546 */           if (this.val$callback != null)
/* 2547 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 2548 */           return;
/*      */         }
/*      */         try {
/* 2551 */           boolean bool = RongIMClient.this.mLibHandler.saveTextMessageDraft(this.val$conversation, this.val$content);
/* 2552 */           if (this.val$callback != null)
/* 2553 */             this.val$callback.onCallback(Boolean.valueOf(bool));
/*      */         }
/*      */         catch (RemoteException e) {
/* 2556 */           e.printStackTrace();
/* 2557 */           if (this.val$callback != null)
/* 2558 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean clearTextMessageDraft(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 2576 */     CountDownLatch latch = new CountDownLatch(1);
/*      */ 
/* 2578 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 2579 */     result.t = Boolean.valueOf(false);
/*      */ 
/* 2581 */     clearTextMessageDraft(conversationType, targetId, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Boolean bool)
/*      */       {
/* 2585 */         if (bool != null)
/* 2586 */           this.val$result.t = bool;
/*      */         else {
/* 2588 */           RLog.e("RongIMClient", "clearTextMessageDraft clearTextMessageDraft is failure!");
/*      */         }
/*      */ 
/* 2591 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 2597 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 2602 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 2604 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 2607 */     return ((Boolean)result.t).booleanValue();
/*      */   }
/*      */ 
/*      */   public void clearTextMessageDraft(Conversation.ConversationType conversationType, String targetId, ResultCallback<Boolean> callback)
/*      */   {
/* 2618 */     Conversation conversation = new Conversation();
/* 2619 */     conversation.setConversationType(conversationType);
/* 2620 */     conversation.setTargetId(targetId);
/*      */ 
/* 2622 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/* 2623 */       RLog.e("RongIMClient", "the value of targetId or ConversationType is error!");
/* 2624 */       if (callback != null)
/* 2625 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 2626 */       return;
/*      */     }
/*      */ 
/* 2629 */     this.mWorkHandler.post(new Runnable(callback, conversation)
/*      */     {
/*      */       public void run() {
/* 2632 */         if (RongIMClient.this.mLibHandler == null) {
/* 2633 */           if (this.val$callback != null)
/* 2634 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 2635 */           return;
/*      */         }
/*      */         try {
/* 2638 */           boolean bool = RongIMClient.this.mLibHandler.clearTextMessageDraft(this.val$conversation);
/* 2639 */           if (this.val$callback != null)
/* 2640 */             this.val$callback.onCallback(Boolean.valueOf(bool));
/*      */         }
/*      */         catch (RemoteException e) {
/* 2643 */           e.printStackTrace();
/* 2644 */           if (this.val$callback != null)
/* 2645 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getDiscussion(String discussionId, ResultCallback<Discussion> callback)
/*      */   {
/* 2659 */     if (TextUtils.isEmpty(discussionId)) {
/* 2660 */       RLog.e("RongIMClient", "the discussionId can't be empty!");
/* 2661 */       if (callback != null)
/* 2662 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 2663 */       return;
/*      */     }
/*      */ 
/* 2666 */     this.mWorkHandler.post(new Runnable(callback, discussionId)
/*      */     {
/*      */       public void run() {
/* 2669 */         if (RongIMClient.this.mLibHandler == null) {
/* 2670 */           if (this.val$callback != null)
/* 2671 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 2672 */           return;
/*      */         }
/*      */         try {
/* 2675 */           RongIMClient.this.mLibHandler.getDiscussion(this.val$discussionId, new IResultCallback.Stub()
/*      */           {
/*      */             public void onComplete(RemoteModelWrap model)
/*      */               throws RemoteException
/*      */             {
/* 2680 */               if (RongIMClient.46.this.val$callback != null)
/* 2681 */                 if ((model != null) && (model.getContent() != null) && ((model.getContent() instanceof Discussion)))
/* 2682 */                   RongIMClient.46.this.val$callback.onCallback((Discussion)model.getContent());
/*      */                 else
/* 2684 */                   RongIMClient.46.this.val$callback.onCallback(null);
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 2691 */               if (RongIMClient.46.this.val$callback != null)
/* 2692 */                 RongIMClient.46.this.val$callback.onFail(RongIMClient.ErrorCode.valueOf(errorCode));
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (RemoteException e)
/*      */         {
/* 2699 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setDiscussionName(String discussionId, String name, OperationCallback callback)
/*      */   {
/* 2713 */     if ((TextUtils.isEmpty(discussionId)) || (TextUtils.isEmpty(name))) {
/* 2714 */       RLog.e("RongIMClient", "discussionId or name is null");
/* 2715 */       if (callback != null)
/* 2716 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 2717 */       return;
/*      */     }
/*      */ 
/* 2720 */     this.mWorkHandler.post(new Runnable(callback, name, discussionId)
/*      */     {
/*      */       public void run()
/*      */       {
/* 2724 */         if (RongIMClient.this.mLibHandler == null) {
/* 2725 */           if (this.val$callback != null)
/* 2726 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 2727 */           return;
/*      */         }
/*      */         try {
/* 2730 */           String sub = this.val$name;
/* 2731 */           if ((!TextUtils.isEmpty(this.val$name)) && (this.val$name.length() > 40))
/* 2732 */             sub = this.val$name.substring(0, 39);
/* 2733 */           RongIMClient.this.mLibHandler.setDiscussionName(this.val$discussionId, sub, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException
/*      */             {
/* 2737 */               if (RongIMClient.47.this.val$callback != null)
/* 2738 */                 RongIMClient.47.this.val$callback.onCallback();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 2744 */               if (RongIMClient.47.this.val$callback != null)
/* 2745 */                 RongIMClient.47.this.val$callback.onFail(errorCode);
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (RemoteException e)
/*      */         {
/* 2752 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void createDiscussion(String name, List<String> userIdList, CreateDiscussionCallback callback)
/*      */   {
/* 2766 */     if ((TextUtils.isEmpty(name)) || (userIdList == null) || (userIdList.size() == 0)) {
/* 2767 */       RLog.e("RongIMClient", "name or userIdList is null");
/* 2768 */       if (callback != null)
/* 2769 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 2770 */       return;
/*      */     }
/*      */ 
/* 2773 */     this.mWorkHandler.post(new Runnable(callback, name, userIdList)
/*      */     {
/*      */       public void run()
/*      */       {
/* 2777 */         if (RongIMClient.this.mLibHandler == null) {
/* 2778 */           if (this.val$callback != null)
/* 2779 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 2780 */           return;
/*      */         }
/*      */         try {
/* 2783 */           String sub = this.val$name;
/* 2784 */           if ((!TextUtils.isEmpty(this.val$name)) && (this.val$name.length() > 40)) {
/* 2785 */             sub = this.val$name.substring(0, 39);
/*      */           }
/* 2787 */           RongIMClient.this.mLibHandler.createDiscussion(sub, this.val$userIdList, new IResultCallback.Stub()
/*      */           {
/*      */             public void onComplete(RemoteModelWrap model) throws RemoteException
/*      */             {
/* 2791 */               if (RongIMClient.48.this.val$callback != null)
/*      */               {
/* 2793 */                 if ((model != null) && (model.getContent() != null) && ((model.getContent() instanceof Discussion)))
/* 2794 */                   RongIMClient.48.this.val$callback.onCallback(((Discussion)model.getContent()).getId());
/*      */                 else
/* 2796 */                   RongIMClient.48.this.val$callback.onCallback(null);
/*      */               }
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 2804 */               if (RongIMClient.48.this.val$callback != null)
/* 2805 */                 RongIMClient.48.this.val$callback.onFail(errorCode);
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (RemoteException e)
/*      */         {
/* 2812 */           if (this.val$callback != null)
/* 2813 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void addMemberToDiscussion(String discussionId, List<String> userIdList, OperationCallback callback)
/*      */   {
/* 2827 */     if ((TextUtils.isEmpty(discussionId)) || (userIdList == null) || (userIdList.size() == 0)) {
/* 2828 */       RLog.e("RongIMClient", "discussionId or userIdList is null");
/* 2829 */       if (callback != null)
/* 2830 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 2831 */       return;
/*      */     }
/*      */ 
/* 2834 */     this.mWorkHandler.post(new Runnable(callback, discussionId, userIdList)
/*      */     {
/*      */       public void run()
/*      */       {
/* 2838 */         if (RongIMClient.this.mLibHandler == null) {
/* 2839 */           if (this.val$callback != null)
/* 2840 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 2841 */           return;
/*      */         }
/*      */         try {
/* 2844 */           RongIMClient.this.mLibHandler.addMemberToDiscussion(this.val$discussionId, this.val$userIdList, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException
/*      */             {
/* 2848 */               if (RongIMClient.49.this.val$callback != null)
/* 2849 */                 RongIMClient.49.this.val$callback.onCallback();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 2855 */               if (RongIMClient.49.this.val$callback != null)
/* 2856 */                 RongIMClient.49.this.val$callback.onFail(errorCode);
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (RemoteException e)
/*      */         {
/* 2863 */           if (this.val$callback != null)
/* 2864 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void removeMemberFromDiscussion(String discussionId, String userId, OperationCallback callback)
/*      */   {
/* 2881 */     if ((TextUtils.isEmpty(discussionId)) || (TextUtils.isEmpty(userId))) {
/* 2882 */       RLog.e("RongIMClient", "discussionId or userId is null");
/* 2883 */       if (callback != null)
/* 2884 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 2885 */       return;
/*      */     }
/*      */ 
/* 2888 */     this.mWorkHandler.post(new Runnable(callback, discussionId, userId)
/*      */     {
/*      */       public void run()
/*      */       {
/* 2892 */         if (RongIMClient.this.mLibHandler == null) {
/* 2893 */           if (this.val$callback != null)
/* 2894 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 2895 */           return;
/*      */         }
/*      */         try {
/* 2898 */           RongIMClient.this.mLibHandler.removeDiscussionMember(this.val$discussionId, this.val$userId, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException
/*      */             {
/* 2902 */               if (RongIMClient.50.this.val$callback != null)
/* 2903 */                 RongIMClient.50.this.val$callback.onCallback();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 2909 */               if (RongIMClient.50.this.val$callback != null)
/* 2910 */                 RongIMClient.50.this.val$callback.onFail(errorCode);
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (RemoteException e)
/*      */         {
/* 2917 */           e.printStackTrace();
/* 2918 */           if (this.val$callback != null)
/* 2919 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void quitDiscussion(String discussionId, OperationCallback callback)
/*      */   {
/* 2932 */     if (TextUtils.isEmpty(discussionId)) {
/* 2933 */       RLog.e("RongIMClient", "discussionId is null");
/* 2934 */       if (callback != null)
/* 2935 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 2936 */       return;
/*      */     }
/*      */ 
/* 2939 */     this.mWorkHandler.post(new Runnable(callback, discussionId)
/*      */     {
/*      */       public void run()
/*      */       {
/* 2943 */         if (RongIMClient.this.mLibHandler == null) {
/* 2944 */           if (this.val$callback != null)
/* 2945 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 2946 */           return;
/*      */         }
/*      */         try {
/* 2949 */           RongIMClient.this.mLibHandler.quitDiscussion(this.val$discussionId, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException
/*      */             {
/* 2953 */               if (RongIMClient.51.this.val$callback != null)
/* 2954 */                 RongIMClient.51.this.val$callback.onCallback();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 2960 */               if (RongIMClient.51.this.val$callback != null)
/* 2961 */                 RongIMClient.51.this.val$callback.onFail(errorCode);
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (RemoteException e)
/*      */         {
/* 2968 */           e.printStackTrace();
/* 2969 */           if (this.val$callback != null)
/* 2970 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getMessage(int messageId, ResultCallback<Message> callback)
/*      */   {
/* 2983 */     if (messageId <= 0) {
/* 2984 */       RLog.e("RongIMClient", "Illegal argument of messageId.");
/* 2985 */       if (callback != null)
/* 2986 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 2987 */       return;
/*      */     }
/*      */ 
/* 2990 */     this.mWorkHandler.post(new Runnable(callback, messageId)
/*      */     {
/*      */       public void run() {
/* 2993 */         if (RongIMClient.this.mLibHandler == null) {
/* 2994 */           if (this.val$callback != null)
/* 2995 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 2996 */           return;
/*      */         }
/*      */         try {
/* 2999 */           Message result = RongIMClient.this.mLibHandler.getMessage(this.val$messageId);
/* 3000 */           if (this.val$callback != null)
/* 3001 */             this.val$callback.onCallback(result);
/*      */         }
/*      */         catch (RemoteException e) {
/* 3004 */           e.printStackTrace();
/*      */ 
/* 3006 */           if (this.val$callback != null)
/* 3007 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void insertMessage(Conversation.ConversationType type, String targetId, String senderUserId, MessageContent content, ResultCallback<Message> resultCallback)
/*      */   {
/* 3024 */     if ((type == null) || (TextUtils.isEmpty(targetId))) {
/* 3025 */       RLog.e("RongIMClient", "insertMessage::ConversationType or targetId is null");
/* 3026 */       if (resultCallback != null)
/* 3027 */         resultCallback.onError(ErrorCode.PARAMETER_ERROR);
/* 3028 */       return;
/*      */     }
/*      */ 
/* 3031 */     this.mWorkHandler.post(new Runnable(resultCallback, targetId, type, content, senderUserId)
/*      */     {
/*      */       public void run() {
/* 3034 */         if (RongIMClient.this.mLibHandler == null) {
/* 3035 */           if (this.val$resultCallback != null)
/* 3036 */             this.val$resultCallback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 3037 */           return;
/*      */         }
/* 3039 */         Message message = Message.obtain(this.val$targetId, this.val$type, this.val$content);
/*      */ 
/* 3041 */         if (!TextUtils.isEmpty(this.val$senderUserId))
/* 3042 */           message.setSenderUserId(this.val$senderUserId);
/*      */         else
/* 3044 */           message.setSenderUserId(RongIMClient.this.mCurrentUserId);
/*      */         try
/*      */         {
/* 3047 */           Message result = RongIMClient.this.mLibHandler.insertMessage(message);
/* 3048 */           if (this.val$resultCallback != null)
/* 3049 */             this.val$resultCallback.onCallback(result);
/*      */         }
/*      */         catch (RemoteException e) {
/* 3052 */           e.printStackTrace();
/*      */ 
/* 3054 */           if (this.val$resultCallback != null)
/* 3055 */             this.val$resultCallback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Message insertMessage(Conversation.ConversationType type, String targetId, String senderUserId, MessageContent content)
/*      */   {
/* 3074 */     CountDownLatch latch = new CountDownLatch(1);
/*      */ 
/* 3076 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/*      */ 
/* 3078 */     insertMessage(type, targetId, senderUserId, content, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Message message) {
/* 3081 */         this.val$result.t = message;
/* 3082 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 3087 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 3092 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 3094 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 3097 */     return (Message)result.t;
/*      */   }
/*      */ 
/*      */   public void sendLocationMessage(Message message, String pushContent, String pushData, IRongCallback.ISendMessageCallback sendMessageCallback)
/*      */   {
/* 3113 */     if ((message == null) || (message.getConversationType() == null) || (TextUtils.isEmpty(message.getTargetId())) || (message.getContent() == null) || (!(message.getContent() instanceof LocationMessage)))
/*      */     {
/* 3118 */       RLog.e("RongIMClient", "sendLocationMessage : conversation type or targetId or content can't be null!");
/* 3119 */       if (sendMessageCallback != null) {
/* 3120 */         sendMessageCallback.onError(message, ErrorCode.PARAMETER_ERROR);
/*      */       }
/* 3122 */       return;
/*      */     }
/*      */ 
/* 3125 */     if (TypingMessageManager.getInstance().isShowMessageTyping()) {
/* 3126 */       TypingMessageManager.getInstance().setTypingEnd(message.getConversationType(), message.getTargetId());
/*      */     }
/*      */ 
/* 3129 */     this.mWorkHandler.post(new Runnable(sendMessageCallback, message, pushContent, pushData)
/*      */     {
/*      */       public void run() {
/* 3132 */         if (RongIMClient.this.mLibHandler == null) {
/* 3133 */           RongIMClient.this.runOnUiThread(new Runnable()
/*      */           {
/*      */             public void run() {
/* 3136 */               if (RongIMClient.55.this.val$sendMessageCallback != null)
/* 3137 */                 RongIMClient.55.this.val$sendMessageCallback.onError(RongIMClient.55.this.val$message, RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */             }
/*      */           });
/* 3140 */           return;
/*      */         }
/*      */         try {
/* 3143 */           RongIMClient.this.mLibHandler.sendLocationMessage(this.val$message, this.val$pushContent, this.val$pushData, new ISendMessageCallback.Stub()
/*      */           {
/*      */             public void onAttached(Message msg) throws RemoteException {
/* 3146 */               if (RongIMClient.55.this.val$sendMessageCallback != null)
/* 3147 */                 RongIMClient.this.runOnUiThread(new Runnable(msg)
/*      */                 {
/*      */                   public void run() {
/* 3150 */                     RongIMClient.55.this.val$sendMessageCallback.onAttached(this.val$msg);
/*      */                   }
/*      */                 });
/*      */             }
/*      */ 
/*      */             public void onSuccess(Message msg) throws RemoteException
/*      */             {
/* 3158 */               if (RongIMClient.55.this.val$sendMessageCallback != null)
/* 3159 */                 RongIMClient.this.runOnUiThread(new Runnable(msg)
/*      */                 {
/*      */                   public void run() {
/* 3162 */                     RongIMClient.55.this.val$sendMessageCallback.onSuccess(this.val$msg);
/*      */                   }
/*      */                 });
/*      */             }
/*      */ 
/*      */             public void onError(Message msg, int errorCode) throws RemoteException
/*      */             {
/* 3170 */               if (RongIMClient.55.this.val$sendMessageCallback != null)
/* 3171 */                 RongIMClient.this.runOnUiThread(new Runnable(msg, errorCode)
/*      */                 {
/*      */                   public void run() {
/* 3174 */                     RongIMClient.55.this.val$sendMessageCallback.onError(this.val$msg, RongIMClient.ErrorCode.valueOf(this.val$errorCode));
/*      */                   } } );
/*      */             } } );
/*      */         }
/*      */         catch (Exception e) {
/* 3181 */           RLog.e("RongIMClient", "sendMessage exception : " + e.getMessage());
/* 3182 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Message sendMessage(Conversation.ConversationType type, String targetId, MessageContent content, String pushContent, String pushData, SendMessageCallback callback)
/*      */   {
/* 3205 */     CountDownLatch latch = new CountDownLatch(1);
/* 3206 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/*      */ 
/* 3208 */     sendMessage(type, targetId, content, pushContent, pushData, callback, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Message message) {
/* 3211 */         this.val$result.t = message;
/* 3212 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 3217 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 3222 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 3224 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 3227 */     return (Message)result.t;
/*      */   }
/*      */ 
/*      */   public void sendMessage(Conversation.ConversationType conversationType, String targetId, MessageContent content, String pushContent, String pushData, SendMessageCallback callback, ResultCallback<Message> resultCallback)
/*      */   {
/* 3245 */     Message message = Message.obtain(targetId, conversationType, content);
/* 3246 */     sendMessage(message, pushContent, pushData, callback, resultCallback);
/*      */   }
/*      */ 
/*      */   public void sendMessage(Message message, String pushContent, String pushData, SendMessageCallback callback, ResultCallback<Message> resultCallback)
/*      */   {
/* 3262 */     if ((message == null) || (message.getConversationType() == null) || (TextUtils.isEmpty(message.getTargetId())) || (message.getContent() == null)) {
/* 3263 */       RLog.e("RongIMClient", "sendMessage : conversation type or targetId or content can't be null!");
/* 3264 */       if (callback != null)
/* 3265 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 3266 */       return;
/*      */     }
/* 3268 */     MessageTag msgTag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
/* 3269 */     if (msgTag == null) {
/* 3270 */       RLog.e("RongIMClient", "sendMessage 自定义消息没有加注解信息。");
/* 3271 */       if (callback != null)
/* 3272 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 3273 */       return;
/*      */     }
/*      */ 
/* 3276 */     if (TypingMessageManager.getInstance().isShowMessageTyping()) {
/* 3277 */       MessageContent content = message.getContent();
/* 3278 */       if ((!(content instanceof TypingStatusMessage)) && (!(content instanceof ReadReceiptMessage))) {
/* 3279 */         TypingMessageManager.getInstance().setTypingEnd(message.getConversationType(), message.getTargetId());
/*      */       }
/*      */     }
/*      */ 
/* 3283 */     this.mWorkHandler.post(new Runnable(callback, message, pushContent, pushData, resultCallback)
/*      */     {
/*      */       public void run()
/*      */       {
/* 3287 */         if (RongIMClient.this.mLibHandler == null) {
/* 3288 */           if (this.val$callback != null)
/* 3289 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 3290 */           return;
/*      */         }
/*      */         try {
/* 3293 */           RongIMClient.this.mLibHandler.sendMessage(this.val$message, this.val$pushContent, this.val$pushData, new ISendMessageCallback.Stub()
/*      */           {
/*      */             public void onAttached(Message msg) throws RemoteException
/*      */             {
/* 3297 */               if (RongIMClient.57.this.val$resultCallback != null) {
/* 3298 */                 RLog.d("RongIMClient", "onAttached");
/* 3299 */                 RongIMClient.57.this.val$resultCallback.onCallback(msg);
/*      */               }
/*      */             }
/*      */ 
/*      */             public void onSuccess(Message msg) throws RemoteException
/*      */             {
/* 3305 */               if (RongIMClient.57.this.val$callback != null) {
/* 3306 */                 RongIMClient.57.this.val$callback.onCallback(Integer.valueOf(msg.getMessageId()));
/*      */               }
/*      */ 
/* 3309 */               RongIMClient.access$102(RongIMClient.this, 0);
/*      */             }
/*      */ 
/*      */             public void onError(Message msg, int errorCode) throws RemoteException
/*      */             {
/* 3314 */               if (RongIMClient.57.this.val$callback != null) {
/* 3315 */                 RongIMClient.57.this.val$callback.onFail(Integer.valueOf(msg.getMessageId()), RongIMClient.ErrorCode.valueOf(errorCode));
/*      */               }
/* 3317 */               if (RongIMClient.reconnectList.contains(Integer.valueOf(errorCode))) {
/* 3318 */                 if (RongIMClient.this.mReconnectRunnable != null) {
/* 3319 */                   RongIMClient.mHandler.removeCallbacks(RongIMClient.this.mReconnectRunnable);
/* 3320 */                   RongIMClient.access$302(RongIMClient.this, null);
/*      */                 }
/* 3322 */                 RongIMClient.access$302(RongIMClient.this, new RongIMClient.ReconnectRunnable(RongIMClient.this));
/* 3323 */                 RongIMClient.mHandler.postDelayed(RongIMClient.this.mReconnectRunnable, 1000L);
/*      */               }
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 3329 */           e.printStackTrace();
/*      */ 
/* 3331 */           if (this.val$resultCallback != null)
/* 3332 */             this.val$resultCallback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */         catch (NullPointerException e) {
/* 3335 */           RLog.e("RongIMClient", "sendMessage NullPointerException");
/* 3336 */           e.printStackTrace();
/*      */ 
/* 3338 */           if (this.val$resultCallback != null)
/* 3339 */             this.val$resultCallback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void sendMessage(Conversation.ConversationType type, String targetId, MessageContent content, String pushContent, String pushData, IRongCallback.ISendMessageCallback callback)
/*      */   {
/* 3360 */     Message message = Message.obtain(targetId, type, content);
/* 3361 */     sendMessage(message, pushContent, pushData, callback);
/*      */   }
/*      */ 
/*      */   private void runOnUiThread(Runnable runnable) {
/* 3365 */     mHandler.post(runnable);
/*      */   }
/*      */ 
/*      */   public void sendMessage(Message message, String pushContent, String pushData, IRongCallback.ISendMessageCallback callback)
/*      */   {
/* 3380 */     if ((message == null) || (message.getConversationType() == null) || (TextUtils.isEmpty(message.getTargetId())) || (message.getContent() == null)) {
/* 3381 */       RLog.e("RongIMClient", "sendMessage : conversation type or targetId or content can't be null!");
/* 3382 */       if (callback != null) {
/* 3383 */         callback.onError(message, ErrorCode.PARAMETER_ERROR);
/*      */       }
/* 3385 */       return;
/*      */     }
/* 3387 */     MessageTag msgTag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
/* 3388 */     if (msgTag == null) {
/* 3389 */       RLog.e("RongIMClient", "sendMessage 自定义消息没有加注解信息。");
/* 3390 */       if (callback != null) {
/* 3391 */         callback.onError(message, ErrorCode.PARAMETER_ERROR);
/*      */       }
/* 3393 */       return;
/*      */     }
/*      */ 
/* 3396 */     if (TypingMessageManager.getInstance().isShowMessageTyping()) {
/* 3397 */       MessageContent content = message.getContent();
/* 3398 */       if ((!(content instanceof TypingStatusMessage)) && (!(content instanceof ReadReceiptMessage))) {
/* 3399 */         TypingMessageManager.getInstance().setTypingEnd(message.getConversationType(), message.getTargetId());
/*      */       }
/*      */     }
/*      */ 
/* 3403 */     this.mWorkHandler.post(new Runnable(callback, message, pushContent, pushData)
/*      */     {
/*      */       public void run() {
/* 3406 */         if (RongIMClient.this.mLibHandler == null) {
/* 3407 */           RongIMClient.this.runOnUiThread(new Runnable()
/*      */           {
/*      */             public void run() {
/* 3410 */               if (RongIMClient.58.this.val$callback != null)
/* 3411 */                 RongIMClient.58.this.val$callback.onError(RongIMClient.58.this.val$message, RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */             }
/*      */           });
/* 3414 */           return;
/*      */         }
/*      */         try {
/* 3417 */           RongIMClient.this.mLibHandler.sendMessage(this.val$message, this.val$pushContent, this.val$pushData, new ISendMessageCallback.Stub()
/*      */           {
/*      */             public void onAttached(Message msg) throws RemoteException {
/* 3420 */               if (RongIMClient.58.this.val$callback != null)
/* 3421 */                 RongIMClient.this.runOnUiThread(new Runnable(msg)
/*      */                 {
/*      */                   public void run() {
/* 3424 */                     RongIMClient.58.this.val$callback.onAttached(this.val$msg);
/*      */                   }
/*      */                 });
/*      */             }
/*      */ 
/*      */             public void onSuccess(Message msg) throws RemoteException
/*      */             {
/* 3432 */               if (RongIMClient.58.this.val$callback != null)
/* 3433 */                 RongIMClient.this.runOnUiThread(new Runnable(msg)
/*      */                 {
/*      */                   public void run() {
/* 3436 */                     RongIMClient.58.this.val$callback.onSuccess(this.val$msg);
/*      */                   }
/*      */                 });
/*      */             }
/*      */ 
/*      */             public void onError(Message msg, int errorCode) throws RemoteException
/*      */             {
/* 3444 */               if (RongIMClient.58.this.val$callback != null)
/* 3445 */                 RongIMClient.this.runOnUiThread(new Runnable(msg, errorCode)
/*      */                 {
/*      */                   public void run() {
/* 3448 */                     RongIMClient.58.this.val$callback.onError(this.val$msg, RongIMClient.ErrorCode.valueOf(this.val$errorCode));
/*      */                   } } );
/*      */             } } );
/*      */         }
/*      */         catch (Exception e) {
/* 3455 */           RLog.e("RongIMClient", "sendMessage exception : " + e.getMessage());
/* 3456 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private void sendDirectionalMessage(Conversation.ConversationType type, String targetId, MessageContent content, String[] userIds, String pushContent, String pushData, IRongCallback.ISendMessageCallback callback)
/*      */   {
/* 3477 */     Message message = Message.obtain(targetId, type, content);
/*      */ 
/* 3479 */     if ((type == null) || (TextUtils.isEmpty(targetId)) || (content == null)) {
/* 3480 */       RLog.e("RongIMClient", "sendMessage : conversation type or targetId or content can't be null!");
/* 3481 */       if (callback != null) {
/* 3482 */         callback.onError(message, ErrorCode.PARAMETER_ERROR);
/*      */       }
/* 3484 */       return;
/*      */     }
/* 3486 */     MessageTag msgTag = (MessageTag)content.getClass().getAnnotation(MessageTag.class);
/* 3487 */     if (msgTag == null) {
/* 3488 */       RLog.e("RongIMClient", "sendMessage 自定义消息没有加注解信息。");
/* 3489 */       if (callback != null) {
/* 3490 */         callback.onError(message, ErrorCode.PARAMETER_ERROR);
/*      */       }
/* 3492 */       return;
/*      */     }
/*      */ 
/* 3495 */     if ((TypingMessageManager.getInstance().isShowMessageTyping()) && 
/* 3496 */       (!(content instanceof TypingStatusMessage)) && (!(content instanceof ReadReceiptMessage))) {
/* 3497 */       TypingMessageManager.getInstance().setTypingEnd(message.getConversationType(), message.getTargetId());
/*      */     }
/*      */ 
/* 3501 */     this.mWorkHandler.post(new Runnable(callback, message, pushContent, pushData, userIds)
/*      */     {
/*      */       public void run() {
/* 3504 */         if (RongIMClient.this.mLibHandler == null) {
/* 3505 */           RongIMClient.this.runOnUiThread(new Runnable()
/*      */           {
/*      */             public void run() {
/* 3508 */               if (RongIMClient.59.this.val$callback != null)
/* 3509 */                 RongIMClient.59.this.val$callback.onError(RongIMClient.59.this.val$message, RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */             }
/*      */           });
/* 3512 */           return;
/*      */         }
/*      */         try {
/* 3515 */           RongIMClient.this.mLibHandler.sendDirectionalMessage(this.val$message, this.val$pushContent, this.val$pushData, this.val$userIds, new ISendMessageCallback.Stub()
/*      */           {
/*      */             public void onAttached(Message msg) throws RemoteException {
/* 3518 */               if (RongIMClient.59.this.val$callback != null)
/* 3519 */                 RongIMClient.this.runOnUiThread(new Runnable(msg)
/*      */                 {
/*      */                   public void run() {
/* 3522 */                     RongIMClient.59.this.val$callback.onAttached(this.val$msg);
/*      */                   }
/*      */                 });
/*      */             }
/*      */ 
/*      */             public void onSuccess(Message msg) throws RemoteException
/*      */             {
/* 3530 */               if (RongIMClient.59.this.val$callback != null)
/* 3531 */                 RongIMClient.this.runOnUiThread(new Runnable(msg)
/*      */                 {
/*      */                   public void run() {
/* 3534 */                     RongIMClient.59.this.val$callback.onSuccess(this.val$msg);
/*      */                   }
/*      */                 });
/*      */             }
/*      */ 
/*      */             public void onError(Message msg, int errorCode) throws RemoteException
/*      */             {
/* 3542 */               if (RongIMClient.59.this.val$callback != null)
/* 3543 */                 RongIMClient.this.runOnUiThread(new Runnable(msg, errorCode)
/*      */                 {
/*      */                   public void run() {
/* 3546 */                     RongIMClient.59.this.val$callback.onError(this.val$msg, RongIMClient.ErrorCode.valueOf(this.val$errorCode));
/*      */                   } } );
/*      */             } } );
/*      */         }
/*      */         catch (Exception e) {
/* 3553 */           RLog.e("RongIMClient", "sendMessage exception : " + e.getMessage());
/* 3554 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void sendStatusMessage(Message message, SendMessageCallback callback, ResultCallback<Message> resultCallback)
/*      */   {
/* 3573 */     MessageTag msgTag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
/* 3574 */     if (msgTag == null) {
/* 3575 */       throw new RuntimeException("自定义消息没有加注解信息。");
/*      */     }
/* 3577 */     if (msgTag.flag() != 16) {
/* 3578 */       RLog.e("RongIMClient", "sendStatusMessage MessageTag should be STATUS.");
/* 3579 */       return;
/*      */     }
/*      */ 
/* 3582 */     this.mWorkHandler.post(new Runnable(callback, message, resultCallback)
/*      */     {
/*      */       public void run() {
/* 3585 */         if (RongIMClient.this.mLibHandler == null) {
/* 3586 */           if (this.val$callback != null)
/* 3587 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 3588 */           return;
/*      */         }
/*      */         try {
/* 3591 */           Message msg = RongIMClient.this.mLibHandler.sendStatusMessage(this.val$message, new ILongCallback.Stub()
/*      */           {
/*      */             public void onComplete(long result) throws RemoteException
/*      */             {
/* 3595 */               if (RongIMClient.60.this.val$callback != null)
/* 3596 */                 RongIMClient.60.this.val$callback.onCallback(Integer.valueOf((int)result));
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 3603 */               if (RongIMClient.60.this.val$callback != null)
/* 3604 */                 RongIMClient.60.this.val$callback.onFail(Integer.valueOf(RongIMClient.60.this.val$message.getMessageId()), RongIMClient.ErrorCode.valueOf(errorCode));
/*      */             }
/*      */           });
/* 3608 */           this.val$message.setMessageId(msg.getMessageId());
/*      */ 
/* 3610 */           if (this.val$resultCallback != null)
/* 3611 */             this.val$resultCallback.onCallback(msg);
/*      */         } catch (RemoteException e) {
/* 3613 */           e.printStackTrace();
/* 3614 */           if (this.val$resultCallback != null)
/* 3615 */             this.val$resultCallback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public Message sendMessage(Message message, String pushContent, String pushData, SendMessageCallback callback)
/*      */   {
/* 3636 */     CountDownLatch latch = new CountDownLatch(1);
/*      */ 
/* 3638 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/*      */ 
/* 3640 */     sendMessage(message, pushContent, pushData, callback, new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Message message) {
/* 3643 */         this.val$result.t = message;
/* 3644 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 3649 */         this.val$latch.countDown();
/*      */       }
/*      */     });
/*      */     try {
/* 3654 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 3656 */       e.printStackTrace();
/*      */     }
/* 3658 */     return (Message)result.t;
/*      */   }
/*      */ 
/*      */   public void sendImageMessage(Conversation.ConversationType type, String targetId, MessageContent content, String pushContent, String pushData, SendImageMessageCallback callback)
/*      */   {
/* 3675 */     if ((type == null) || (TextUtils.isEmpty(targetId)) || (content == null)) {
/* 3676 */       Log.i("RongIMClient", "Illegal parameter!");
/* 3677 */       if (callback != null)
/* 3678 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 3679 */       return;
/*      */     }
/*      */ 
/* 3682 */     Message message = Message.obtain(targetId, type, content);
/* 3683 */     sendImageMessage(message, pushContent, pushData, callback);
/*      */   }
/*      */ 
/*      */   public void sendImageMessage(Message message, String pushContent, String pushData, SendImageMessageCallback callback)
/*      */   {
/* 3697 */     if (message == null) {
/* 3698 */       RLog.e("RongIMClient", "sendImageMessage message is null!");
/* 3699 */       if (callback != null)
/* 3700 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 3701 */       return;
/*      */     }
/*      */ 
/* 3704 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 3705 */     result.t = message;
/*      */ 
/* 3707 */     UploadMediaCallback uploadMediaCallback = new UploadMediaCallback(callback, pushContent, pushData)
/*      */     {
/*      */       public void onProgress(Message message, int progress) {
/* 3710 */         if (this.val$callback != null)
/* 3711 */           this.val$callback.onProgressCallback(message, progress);
/*      */       }
/*      */ 
/*      */       public void onSuccess(Message message)
/*      */       {
/* 3716 */         RongIMClient.this.sendMessage(message, this.val$pushContent, this.val$pushData, new IRongCallback.ISendMessageCallback()
/*      */         {
/*      */           public void onAttached(Message message)
/*      */           {
/*      */           }
/*      */ 
/*      */           public void onSuccess(Message message)
/*      */           {
/* 3724 */             if (RongIMClient.62.this.val$callback != null)
/* 3725 */               RongIMClient.62.this.val$callback.onSuccess(message);
/*      */           }
/*      */ 
/*      */           public void onError(Message message, RongIMClient.ErrorCode errorCode)
/*      */           {
/* 3730 */             if (RongIMClient.62.this.val$callback != null)
/* 3731 */               RongIMClient.62.this.val$callback.onError(message, errorCode);
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(Message message, RongIMClient.ErrorCode e) {
/* 3738 */         message.setSentStatus(Message.SentStatus.FAILED);
/* 3739 */         RongIMClient.this.setMessageSentStatus(message.getMessageId(), Message.SentStatus.FAILED, null);
/* 3740 */         if (this.val$callback != null)
/* 3741 */           this.val$callback.onFail(message, e);
/*      */       }
/*      */     };
/* 3745 */     ResultCallback insertCallback = new ResultCallback(result, callback, uploadMediaCallback)
/*      */     {
/*      */       public void onSuccess(Message message)
/*      */       {
/* 3749 */         if (message != null) {
/* 3750 */           this.val$result.t = message;
/* 3751 */           message.setSentStatus(Message.SentStatus.SENDING);
/* 3752 */           RongIMClient.this.setMessageSentStatus(message.getMessageId(), Message.SentStatus.SENDING, null);
/* 3753 */           if (this.val$callback != null) {
/* 3754 */             this.val$callback.onAttachedCallback(message);
/*      */           }
/* 3756 */           RongIMClient.this.uploadMedia(message, this.val$uploadMediaCallback);
/*      */         } else {
/* 3758 */           throw new IllegalArgumentException("Message Content 为空！");
/*      */         }
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 3764 */         if (this.val$callback != null)
/* 3765 */           this.val$callback.onFail(e);
/*      */       }
/*      */     };
/* 3769 */     if (message.getMessageId() <= 0) {
/* 3770 */       insertMessage(message.getConversationType(), message.getTargetId(), null, message.getContent(), insertCallback);
/*      */     } else {
/* 3772 */       message.setSentStatus(Message.SentStatus.SENDING);
/* 3773 */       setMessageSentStatus(message.getMessageId(), Message.SentStatus.SENDING, null);
/* 3774 */       uploadMedia(message, uploadMediaCallback);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void sendImageMessage(Message message, String pushContent, String pushData, SendImageMessageWithUploadListenerCallback callback)
/*      */   {
/* 3798 */     if (message == null) {
/* 3799 */       RLog.e("RongIMClient", "message is null!");
/* 3800 */       if (callback != null)
/* 3801 */         callback.onError(null, ErrorCode.PARAMETER_ERROR);
/* 3802 */       return;
/*      */     }
/* 3804 */     if (this.mLibHandler == null) {
/* 3805 */       RLog.e("RongIMClient", "sendImageMessage IPC 进程尚未运行！");
/* 3806 */       if (callback != null)
/* 3807 */         callback.onError(message, ErrorCode.IPC_DISCONNECT);
/* 3808 */       return;
/*      */     }
/* 3810 */     ResultCallback insertCallback = new ResultCallback(callback, pushContent, pushData, message)
/*      */     {
/*      */       public void onSuccess(Message message)
/*      */       {
/* 3814 */         if (message != null) {
/* 3815 */           message.setSentStatus(Message.SentStatus.SENDING);
/* 3816 */           RongIMClient.this.setMessageSentStatus(message.getMessageId(), Message.SentStatus.SENDING, null);
/* 3817 */           if (this.val$callback != null) {
/* 3818 */             RongIMClient.UploadImageStatusListener watcher = new RongIMClient.UploadImageStatusListener(RongIMClient.this, message, this.val$pushContent, this.val$pushData, this.val$callback);
/* 3819 */             this.val$callback.onAttachedCallback(message, watcher);
/*      */           }
/*      */         } else {
/* 3822 */           throw new IllegalArgumentException("Message Content 为空！");
/*      */         }
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 3828 */         if (this.val$callback != null)
/* 3829 */           this.val$callback.onError(this.val$message, e);
/*      */       }
/*      */     };
/* 3833 */     insertMessage(message.getConversationType(), message.getTargetId(), null, message.getContent(), insertCallback);
/*      */   }
/*      */ 
/*      */   private void uploadMedia(Message message, UploadMediaCallback callback)
/*      */   {
/* 3844 */     if ((message == null) || (message.getConversationType() == null) || (TextUtils.isEmpty(message.getTargetId())) || (message.getContent() == null))
/*      */     {
/* 3847 */       RLog.e("RongIMClient", "conversation type or targetId or message content can't be null!");
/* 3848 */       if (callback != null) {
/* 3849 */         callback.onError(message, ErrorCode.PARAMETER_ERROR);
/*      */       }
/* 3851 */       return;
/*      */     }
/*      */ 
/* 3854 */     Uri localPath = null;
/* 3855 */     if ((message.getContent() instanceof ImageMessage)) {
/* 3856 */       localPath = ((ImageMessage)message.getContent()).getLocalUri();
/*      */     }
/*      */ 
/* 3859 */     if ((localPath == null) || (localPath.getScheme() == null) || (!localPath.getScheme().equals("file"))) {
/* 3860 */       RLog.e("RongIMClient", "uploadMedia Uri :[" + localPath + ", 必须为file://格式");
/* 3861 */       if (callback != null)
/* 3862 */         callback.onError(message, ErrorCode.PARAMETER_ERROR);
/* 3863 */       return;
/*      */     }
/*      */ 
/* 3866 */     File file = new File(localPath.getPath());
/*      */ 
/* 3868 */     if (!file.exists()) {
/* 3869 */       RLog.e("RongIMClient", "uploadMedia Uri 文件不存在。" + localPath);
/* 3870 */       if (callback != null)
/* 3871 */         callback.onError(message, ErrorCode.PARAMETER_ERROR);
/* 3872 */       return;
/*      */     }
/*      */ 
/* 3875 */     this.mWorkHandler.post(new Runnable(callback, message)
/*      */     {
/*      */       public void run() {
/* 3878 */         if (RongIMClient.this.mLibHandler == null) {
/* 3879 */           if (this.val$callback != null)
/* 3880 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 3881 */           return;
/*      */         }
/*      */         try {
/* 3884 */           RongIMClient.this.mLibHandler.uploadMedia(this.val$message, new IUploadCallback.Stub()
/*      */           {
/*      */             public void onComplete(String url) throws RemoteException {
/* 3887 */               RLog.i("RongIMClient", "uploadMedia onComplete url = " + url);
/*      */ 
/* 3889 */               MessageContent content = RongIMClient.65.this.val$message.getContent();
/* 3890 */               if ((content instanceof ImageMessage)) {
/* 3891 */                 ((ImageMessage)content).setRemoteUri(Uri.parse(url));
/*      */               }
/*      */ 
/* 3894 */               if (RongIMClient.65.this.val$callback != null)
/* 3895 */                 RongIMClient.65.this.val$callback.onCallback(RongIMClient.65.this.val$message);
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode) throws RemoteException
/*      */             {
/* 3900 */               RLog.e("RongIMClient", "uploadMedia onFailure: " + errorCode);
/*      */ 
/* 3902 */               if (RongIMClient.65.this.val$callback != null)
/* 3903 */                 RongIMClient.65.this.val$callback.onFail(RongIMClient.65.this.val$message, RongIMClient.ErrorCode.valueOf(errorCode));
/*      */             }
/*      */ 
/*      */             public void onProgress(int progress) throws RemoteException
/*      */             {
/* 3908 */               if (RongIMClient.65.this.val$callback != null)
/* 3909 */                 RongIMClient.65.this.val$callback.onProgressCallback(RongIMClient.65.this.val$message, progress);
/*      */             } } );
/*      */         } catch (RemoteException e) {
/* 3913 */           e.printStackTrace();
/* 3914 */           if (this.val$callback != null)
/* 3915 */             this.val$callback.onFail(this.val$message, RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void downloadMedia(Conversation.ConversationType conversationType, String targetId, MediaType mediaType, String imageUrl, DownloadMediaCallback callback)
/*      */   {
/* 3932 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId)) || (mediaType == null) || (TextUtils.isEmpty(imageUrl))) {
/* 3933 */       RLog.e("RongIMClient", "downloadMedia 参数异常。");
/* 3934 */       if (callback != null)
/* 3935 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 3936 */       return;
/*      */     }
/*      */ 
/* 3939 */     Conversation conversation = new Conversation();
/* 3940 */     conversation.setTargetId(targetId);
/* 3941 */     conversation.setConversationType(conversationType);
/*      */ 
/* 3943 */     this.mWorkHandler.post(new Runnable(callback, conversation, mediaType, imageUrl)
/*      */     {
/*      */       public void run()
/*      */       {
/* 3947 */         if (RongIMClient.this.mLibHandler == null) {
/* 3948 */           if (this.val$callback != null)
/* 3949 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 3950 */           return;
/*      */         }
/*      */         try {
/* 3953 */           RongIMClient.this.mLibHandler.downloadMedia(this.val$conversation, this.val$mediaType.getValue(), this.val$imageUrl, new IDownloadMediaCallback.Stub()
/*      */           {
/*      */             public void onComplete(String url) throws RemoteException
/*      */             {
/* 3957 */               if (RongIMClient.66.this.val$callback != null)
/* 3958 */                 RongIMClient.66.this.val$callback.onCallback(url);
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 3964 */               if (RongIMClient.66.this.val$callback != null)
/* 3965 */                 RongIMClient.66.this.val$callback.onFail(RongIMClient.ErrorCode.valueOf(errorCode));
/*      */             }
/*      */ 
/*      */             public void onProgress(int progress)
/*      */               throws RemoteException
/*      */             {
/* 3971 */               if (RongIMClient.66.this.val$callback != null)
/* 3972 */                 RongIMClient.66.this.val$callback.onProgressCallback(progress);
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 3977 */           e.printStackTrace();
/*      */ 
/* 3979 */           if (this.val$callback != null)
/* 3980 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void downloadMediaMessage(Message message, IRongCallback.IDownloadMediaMessageCallback callback)
/*      */   {
/* 3994 */     if ((message == null) || (message.getConversationType() == null) || (TextUtils.isEmpty(message.getTargetId())) || (message.getContent() == null) || (!(message.getContent() instanceof MediaMessageContent)))
/*      */     {
/* 3999 */       if (callback != null) {
/* 4000 */         callback.onError(message, ErrorCode.PARAMETER_ERROR);
/*      */       }
/* 4002 */       return;
/*      */     }
/*      */ 
/* 4005 */     this.mWorkHandler.post(new Runnable(callback, message)
/*      */     {
/*      */       public void run()
/*      */       {
/* 4009 */         if (RongIMClient.this.mLibHandler == null) {
/* 4010 */           if (this.val$callback != null)
/* 4011 */             this.val$callback.onError(this.val$message, RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4012 */           return;
/*      */         }
/*      */         try {
/* 4015 */           RongIMClient.this.mLibHandler.downloadMediaMessage(this.val$message, new IDownloadMediaMessageCallback.Stub()
/*      */           {
/*      */             public void onComplete(Message message) throws RemoteException
/*      */             {
/* 4019 */               if (RongIMClient.67.this.val$callback != null)
/* 4020 */                 RongIMClient.this.runOnUiThread(new Runnable(message)
/*      */                 {
/*      */                   public void run() {
/* 4023 */                     RongIMClient.67.this.val$callback.onSuccess(this.val$message);
/*      */                   }
/*      */                 });
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode) throws RemoteException
/*      */             {
/* 4031 */               if (RongIMClient.67.this.val$callback != null)
/* 4032 */                 RongIMClient.this.runOnUiThread(new Runnable(errorCode)
/*      */                 {
/*      */                   public void run() {
/* 4035 */                     RongIMClient.67.this.val$callback.onError(RongIMClient.67.this.val$message, RongIMClient.ErrorCode.valueOf(this.val$errorCode));
/*      */                   }
/*      */                 });
/*      */             }
/*      */ 
/*      */             public void onProgress(int progress) throws RemoteException
/*      */             {
/* 4043 */               if (RongIMClient.67.this.val$callback != null)
/* 4044 */                 RongIMClient.this.runOnUiThread(new Runnable(progress)
/*      */                 {
/*      */                   public void run() {
/* 4047 */                     RongIMClient.67.this.val$callback.onProgress(RongIMClient.67.this.val$message, this.val$progress);
/*      */                   }
/*      */                 });
/*      */             }
/*      */ 
/*      */             public void onCanceled() throws RemoteException
/*      */             {
/* 4055 */               if (RongIMClient.67.this.val$callback != null)
/* 4056 */                 RongIMClient.this.runOnUiThread(new Runnable()
/*      */                 {
/*      */                   public void run() {
/* 4059 */                     RongIMClient.67.this.val$callback.onCanceled(RongIMClient.67.this.val$message);
/*      */                   } } );
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 4066 */           e.printStackTrace();
/* 4067 */           if (this.val$callback != null)
/* 4068 */             RongIMClient.this.runOnUiThread(new Runnable()
/*      */             {
/*      */               public void run() {
/* 4071 */                 RongIMClient.67.this.val$callback.onError(RongIMClient.67.this.val$message, RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */               }
/*      */             });
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void cancelDownloadMediaMessage(Message message, OperationCallback callback)
/*      */   {
/* 4086 */     if ((message == null) || (message.getMessageId() <= 0) || (!(message.getContent() instanceof MediaMessageContent)) || (((MediaMessageContent)message.getContent()).getMediaUrl() == null))
/*      */     {
/* 4090 */       RLog.e("RongIMClient", "cancelDownloadMediaMessage 参数异常。");
/* 4091 */       if (callback != null)
/* 4092 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 4093 */       return;
/*      */     }
/*      */ 
/* 4096 */     this.mWorkHandler.post(new Runnable(callback, message)
/*      */     {
/*      */       public void run() {
/* 4099 */         if (RongIMClient.this.mLibHandler == null) {
/* 4100 */           if (this.val$callback != null)
/* 4101 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4102 */           return;
/*      */         }
/*      */         try {
/* 4105 */           RongIMClient.this.mLibHandler.cancelDownloadMediaMessage(this.val$message, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException {
/* 4108 */               if (RongIMClient.68.this.val$callback != null)
/* 4109 */                 RongIMClient.68.this.val$callback.onCallback();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 4115 */               if (RongIMClient.68.this.val$callback != null)
/* 4116 */                 RongIMClient.68.this.val$callback.onFail(RongIMClient.ErrorCode.valueOf(errorCode));
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 4121 */           e.printStackTrace();
/* 4122 */           if (this.val$callback != null)
/* 4123 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getConversationNotificationStatus(Conversation.ConversationType conversationType, String targetId, ResultCallback<Conversation.ConversationNotificationStatus> callback)
/*      */   {
/* 4137 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId))) {
/* 4138 */       RLog.e("RongIMClient", "Parameter is error!");
/* 4139 */       if (callback != null)
/* 4140 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 4141 */       return;
/*      */     }
/*      */ 
/* 4144 */     this.mWorkHandler.post(new Runnable(callback, conversationType, targetId)
/*      */     {
/*      */       public void run() {
/* 4147 */         if (RongIMClient.this.mLibHandler == null) {
/* 4148 */           if (this.val$callback != null)
/* 4149 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4150 */           return;
/*      */         }
/*      */         try {
/* 4153 */           RongIMClient.this.mLibHandler.getConversationNotificationStatus(this.val$conversationType.getValue(), this.val$targetId, new ILongCallback.Stub()
/*      */           {
/*      */             public void onComplete(long result) throws RemoteException {
/* 4156 */               if (RongIMClient.69.this.val$callback != null)
/* 4157 */                 RongIMClient.69.this.val$callback.onCallback(Conversation.ConversationNotificationStatus.setValue((int)result));
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode) throws RemoteException
/*      */             {
/* 4162 */               RLog.i("RongIMClient", "getConversationNotificationStatus-----------------ipc  onFailure--------errorCode:" + errorCode);
/*      */ 
/* 4164 */               if (RongIMClient.69.this.val$callback != null)
/* 4165 */                 RongIMClient.69.this.val$callback.onFail(RongIMClient.ErrorCode.valueOf(errorCode));
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 4170 */           e.printStackTrace();
/* 4171 */           if (this.val$callback != null)
/* 4172 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setConversationNotificationStatus(Conversation.ConversationType conversationType, String targetId, Conversation.ConversationNotificationStatus notificationStatus, ResultCallback<Conversation.ConversationNotificationStatus> callback)
/*      */   {
/* 4187 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null) || (notificationStatus == null)) {
/* 4188 */       RLog.e("RongIMClient", "Parameter is error!");
/* 4189 */       if (callback != null)
/* 4190 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 4191 */       return;
/*      */     }
/*      */ 
/* 4194 */     this.mWorkHandler.post(new Runnable(callback, conversationType, targetId, notificationStatus)
/*      */     {
/*      */       public void run() {
/* 4197 */         if (RongIMClient.this.mLibHandler == null) {
/* 4198 */           if (this.val$callback != null)
/* 4199 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4200 */           return;
/*      */         }
/*      */         try {
/* 4203 */           RongIMClient.this.mLibHandler.setConversationNotificationStatus(this.val$conversationType.getValue(), this.val$targetId, this.val$notificationStatus.getValue(), new ILongCallback.Stub()
/*      */           {
/*      */             public void onComplete(long result) throws RemoteException {
/* 4206 */               if (RongIMClient.70.this.val$callback != null)
/* 4207 */                 RongIMClient.70.this.val$callback.onCallback(Conversation.ConversationNotificationStatus.setValue((int)result));
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 4213 */               if (RongIMClient.70.this.val$callback != null)
/* 4214 */                 RongIMClient.70.this.val$callback.onFail(RongIMClient.ErrorCode.valueOf(errorCode));
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 4219 */           e.printStackTrace();
/* 4220 */           if (this.val$callback != null)
/* 4221 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setDiscussionInviteStatus(String discussionId, DiscussionInviteStatus status, OperationCallback callback)
/*      */   {
/* 4235 */     if ((TextUtils.isEmpty(discussionId)) || (status == null)) {
/* 4236 */       RLog.e("RongIMClient", "Parameter is error!");
/* 4237 */       if (callback != null)
/* 4238 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 4239 */       return;
/*      */     }
/*      */ 
/* 4242 */     this.mWorkHandler.post(new Runnable(callback, discussionId, status)
/*      */     {
/*      */       public void run() {
/* 4245 */         if (RongIMClient.this.mLibHandler == null) {
/* 4246 */           if (this.val$callback != null)
/* 4247 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4248 */           return;
/*      */         }
/*      */         try {
/* 4251 */           RongIMClient.this.mLibHandler.setDiscussionInviteStatus(this.val$discussionId, this.val$status.getValue(), new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException
/*      */             {
/* 4255 */               if (RongIMClient.71.this.val$callback != null)
/* 4256 */                 RongIMClient.71.this.val$callback.onCallback();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 4262 */               if (RongIMClient.71.this.val$callback != null)
/* 4263 */                 RongIMClient.71.this.val$callback.onFail(errorCode);
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (RemoteException e)
/*      */         {
/* 4270 */           e.printStackTrace();
/* 4271 */           if (this.val$callback != null)
/* 4272 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void syncGroup(List<Group> groups, OperationCallback callback)
/*      */   {
/* 4288 */     if ((groups == null) || (groups.size() == 0)) {
/* 4289 */       RLog.e("RongIMClient", "groups is null!");
/* 4290 */       if (callback != null)
/* 4291 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 4292 */       return;
/*      */     }
/*      */ 
/* 4295 */     this.mWorkHandler.post(new Runnable(callback, groups)
/*      */     {
/*      */       public void run() {
/* 4298 */         if (RongIMClient.this.mLibHandler == null) {
/* 4299 */           if (this.val$callback != null)
/* 4300 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4301 */           return;
/*      */         }
/*      */         try {
/* 4304 */           RongIMClient.this.mLibHandler.syncGroup(this.val$groups, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException {
/* 4307 */               if (RongIMClient.72.this.val$callback != null)
/* 4308 */                 RongIMClient.72.this.val$callback.onCallback();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 4314 */               if (RongIMClient.72.this.val$callback != null)
/* 4315 */                 RongIMClient.72.this.val$callback.onFail(errorCode);
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 4320 */           e.printStackTrace();
/* 4321 */           if (this.val$callback != null)
/* 4322 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void joinGroup(String groupId, String groupName, OperationCallback callback)
/*      */   {
/* 4340 */     if ((TextUtils.isEmpty(groupId)) || (TextUtils.isEmpty(groupName))) {
/* 4341 */       RLog.e("RongIMClient", "groupId or groupName is null");
/* 4342 */       if (callback != null)
/* 4343 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 4344 */       return;
/*      */     }
/*      */ 
/* 4347 */     this.mWorkHandler.post(new Runnable(callback, groupId, groupName)
/*      */     {
/*      */       public void run() {
/* 4350 */         if (RongIMClient.this.mLibHandler == null) {
/* 4351 */           if (this.val$callback != null)
/* 4352 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4353 */           return;
/*      */         }
/*      */         try {
/* 4356 */           RongIMClient.this.mLibHandler.joinGroup(this.val$groupId, this.val$groupName, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException {
/* 4359 */               if (RongIMClient.73.this.val$callback != null)
/* 4360 */                 RongIMClient.73.this.val$callback.onCallback();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 4366 */               if (RongIMClient.73.this.val$callback != null)
/* 4367 */                 RongIMClient.73.this.val$callback.onFail(errorCode);
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 4372 */           e.printStackTrace();
/* 4373 */           if (this.val$callback != null)
/* 4374 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void quitGroup(String groupId, OperationCallback callback)
/*      */   {
/* 4393 */     if (TextUtils.isEmpty(groupId)) {
/* 4394 */       RLog.e("RongIMClient", "groupId  is null");
/* 4395 */       if (callback != null)
/* 4396 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 4397 */       return;
/*      */     }
/*      */ 
/* 4400 */     this.mWorkHandler.post(new Runnable(callback, groupId)
/*      */     {
/*      */       public void run() {
/* 4403 */         if (RongIMClient.this.mLibHandler == null) {
/* 4404 */           if (this.val$callback != null)
/* 4405 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4406 */           return;
/*      */         }
/*      */         try {
/* 4409 */           RongIMClient.this.mLibHandler.quitGroup(this.val$groupId, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException {
/* 4412 */               if (RongIMClient.74.this.val$callback != null)
/* 4413 */                 RongIMClient.74.this.val$callback.onCallback();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 4419 */               if (RongIMClient.74.this.val$callback != null)
/* 4420 */                 RongIMClient.74.this.val$callback.onFail(errorCode);
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 4425 */           e.printStackTrace();
/*      */ 
/* 4427 */           if (this.val$callback != null)
/* 4428 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public String getCurrentUserId()
/*      */   {
/*      */     try
/*      */     {
/* 4441 */       if ((TextUtils.isEmpty(SingletonHolder.sInstance.mCurrentUserId)) && (this.mLibHandler != null))
/* 4442 */         SingletonHolder.sInstance.mCurrentUserId = this.mLibHandler.getCurrentUserId();
/*      */     }
/*      */     catch (RemoteException e) {
/* 4445 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 4448 */     return SingletonHolder.sInstance.mCurrentUserId;
/*      */   }
/*      */ 
/*      */   public long getDeltaTime()
/*      */   {
/* 4458 */     if (this.mLibHandler == null) {
/* 4459 */       RLog.e("RongIMClient", "getDeltaTime IPC 进程错误。");
/* 4460 */       return 0L;
/*      */     }
/* 4462 */     long[] deltaTime = { 0L };
/* 4463 */     CountDownLatch countDownLatch = new CountDownLatch(1);
/*      */ 
/* 4465 */     this.mWorkHandler.post(new Runnable(deltaTime, countDownLatch)
/*      */     {
/*      */       public void run()
/*      */       {
/*      */         try {
/* 4470 */           this.val$deltaTime[0] = RongIMClient.access$400(RongIMClient.this).getDeltaTime();
/* 4471 */           this.val$countDownLatch.countDown();
/*      */         } catch (RemoteException e) {
/* 4473 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */     try {
/* 4479 */       countDownLatch.await();
/*      */     } catch (InterruptedException e) {
/* 4481 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 4484 */     return deltaTime[0];
/*      */   }
/*      */ 
/*      */   public void getChatRoomInfo(String chatRoomId, int defMemberCount, ChatRoomInfo.ChatRoomMemberOrder order, ResultCallback<ChatRoomInfo> callback)
/*      */   {
/* 4497 */     if (TextUtils.isEmpty(chatRoomId)) {
/* 4498 */       RLog.e("RongIMClient", "id is null");
/* 4499 */       if (callback != null)
/* 4500 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 4501 */       return;
/*      */     }
/*      */ 
/* 4504 */     this.mWorkHandler.post(new Runnable(callback, chatRoomId, defMemberCount, order)
/*      */     {
/*      */       public void run() {
/* 4507 */         if (RongIMClient.this.mLibHandler == null) {
/* 4508 */           if (this.val$callback != null)
/* 4509 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4510 */           return;
/*      */         }
/*      */         try {
/* 4513 */           RongIMClient.this.mLibHandler.getChatRoomInfo(this.val$chatRoomId, this.val$defMemberCount, this.val$order.getValue(), new IResultCallback.Stub()
/*      */           {
/*      */             public void onComplete(RemoteModelWrap model) throws RemoteException {
/* 4516 */               ChatRoomInfo info = null;
/* 4517 */               if (model != null) {
/* 4518 */                 info = (ChatRoomInfo)model.getContent();
/* 4519 */                 info.setMemberOrder(RongIMClient.76.this.val$order);
/*      */               }
/* 4521 */               if (RongIMClient.76.this.val$callback != null)
/* 4522 */                 RongIMClient.76.this.val$callback.onCallback(info);
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 4528 */               if (RongIMClient.76.this.val$callback != null)
/* 4529 */                 RongIMClient.76.this.val$callback.onFail(errorCode);
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 4534 */           e.printStackTrace();
/* 4535 */           if (this.val$callback != null)
/* 4536 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void joinChatRoom(String chatroomId, int defMessageCount, OperationCallback callback)
/*      */   {
/* 4550 */     if (TextUtils.isEmpty(chatroomId)) {
/* 4551 */       RLog.e("RongIMClient", "id is null");
/* 4552 */       if (callback != null)
/* 4553 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 4554 */       return;
/*      */     }
/* 4556 */     this.mChatroomCache.add(chatroomId);
/* 4557 */     this.mWorkHandler.post(new Runnable(callback, chatroomId, defMessageCount)
/*      */     {
/*      */       public void run() {
/* 4560 */         if (RongIMClient.this.mLibHandler == null) {
/* 4561 */           if (this.val$callback != null)
/* 4562 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4563 */           return;
/*      */         }
/*      */         try {
/* 4566 */           RongIMClient.this.mLibHandler.joinChatRoom(this.val$chatroomId, this.val$defMessageCount, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException {
/* 4569 */               if (RongIMClient.77.this.val$callback != null)
/* 4570 */                 RongIMClient.77.this.val$callback.onCallback();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 4576 */               if (RongIMClient.77.this.val$callback != null)
/* 4577 */                 RongIMClient.77.this.val$callback.onFail(errorCode);
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (RemoteException e) {
/* 4583 */           e.printStackTrace();
/* 4584 */           if (this.val$callback != null)
/* 4585 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private void reJoinChatRoom(String chatroomId, int defMessageCount, OperationCallback callback)
/*      */   {
/* 4599 */     if (TextUtils.isEmpty(chatroomId)) {
/* 4600 */       RLog.e("RongIMClient", "id is null");
/* 4601 */       if (callback != null)
/* 4602 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 4603 */       return;
/*      */     }
/*      */ 
/* 4606 */     this.mWorkHandler.post(new Runnable(callback, chatroomId, defMessageCount)
/*      */     {
/*      */       public void run() {
/* 4609 */         if (RongIMClient.this.mLibHandler == null) {
/* 4610 */           if (this.val$callback != null)
/* 4611 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4612 */           return;
/*      */         }
/*      */         try {
/* 4615 */           RongIMClient.this.mLibHandler.reJoinChatRoom(this.val$chatroomId, this.val$defMessageCount, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException {
/* 4618 */               if (RongIMClient.78.this.val$callback != null)
/* 4619 */                 RongIMClient.78.this.val$callback.onCallback();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 4625 */               if (RongIMClient.78.this.val$callback != null)
/* 4626 */                 RongIMClient.78.this.val$callback.onFail(errorCode);
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (RemoteException e) {
/* 4632 */           e.printStackTrace();
/* 4633 */           if (this.val$callback != null)
/* 4634 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void joinExistChatRoom(String chatroomId, int defMessageCount, OperationCallback callback)
/*      */   {
/* 4648 */     if (TextUtils.isEmpty(chatroomId)) {
/* 4649 */       RLog.e("RongIMClient", "id is null");
/* 4650 */       if (callback != null)
/* 4651 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 4652 */       return;
/*      */     }
/*      */ 
/* 4655 */     this.mChatroomCache.add(chatroomId);
/* 4656 */     this.mWorkHandler.post(new Runnable(callback, chatroomId, defMessageCount)
/*      */     {
/*      */       public void run() {
/* 4659 */         if (RongIMClient.this.mLibHandler == null) {
/* 4660 */           if (this.val$callback != null)
/* 4661 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4662 */           return;
/*      */         }
/*      */         try {
/* 4665 */           RongIMClient.this.mLibHandler.joinExistChatRoom(this.val$chatroomId, this.val$defMessageCount, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException {
/* 4668 */               if (RongIMClient.79.this.val$callback != null)
/* 4669 */                 RongIMClient.79.this.val$callback.onCallback();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 4675 */               if (RongIMClient.79.this.val$callback != null)
/* 4676 */                 RongIMClient.79.this.val$callback.onFail(errorCode);
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (RemoteException e) {
/* 4682 */           e.printStackTrace();
/* 4683 */           if (this.val$callback != null)
/* 4684 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void quitChatRoom(String chatroomId, OperationCallback callback)
/*      */   {
/* 4697 */     if (TextUtils.isEmpty(chatroomId)) {
/* 4698 */       RLog.e("RongIMClient", "id is null!");
/* 4699 */       if (callback != null)
/* 4700 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 4701 */       return;
/*      */     }
/*      */ 
/* 4704 */     this.mChatroomCache.remove(chatroomId);
/* 4705 */     this.mWorkHandler.post(new Runnable(callback, chatroomId)
/*      */     {
/*      */       public void run() {
/* 4708 */         if (RongIMClient.this.mLibHandler == null) {
/* 4709 */           if (this.val$callback != null)
/* 4710 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4711 */           return;
/*      */         }
/*      */         try {
/* 4714 */           RongIMClient.this.mLibHandler.quitChatRoom(this.val$chatroomId, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException {
/* 4717 */               if (RongIMClient.80.this.val$callback != null)
/* 4718 */                 RongIMClient.80.this.val$callback.onCallback();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 4724 */               if (RongIMClient.80.this.val$callback != null)
/* 4725 */                 RongIMClient.80.this.val$callback.onFail(errorCode);
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (RemoteException e) {
/* 4731 */           e.printStackTrace();
/* 4732 */           if (this.val$callback != null)
/* 4733 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void clearConversations(ResultCallback callback, Conversation.ConversationType[] conversationTypes)
/*      */   {
/* 4746 */     if ((conversationTypes == null) || (conversationTypes.length == 0)) {
/* 4747 */       RLog.e("RongIMClient", "conversationTypes is null!");
/* 4748 */       if (callback != null)
/* 4749 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 4750 */       return;
/*      */     }
/*      */ 
/* 4753 */     this.mWorkHandler.post(new Runnable(callback, conversationTypes)
/*      */     {
/*      */       public void run() {
/* 4756 */         if (RongIMClient.this.mLibHandler == null) {
/* 4757 */           if (this.val$callback != null)
/* 4758 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4759 */           return;
/*      */         }
/*      */         try {
/* 4762 */           int[] types = new int[this.val$conversationTypes.length];
/*      */ 
/* 4764 */           int i = 0;
/* 4765 */           for (Conversation.ConversationType type : this.val$conversationTypes) {
/* 4766 */             types[i] = type.getValue();
/* 4767 */             i++;
/*      */           }
/* 4769 */           boolean result = RongIMClient.this.mLibHandler.clearConversations(types);
/*      */ 
/* 4771 */           if (this.val$callback != null)
/* 4772 */             this.val$callback.onCallback(Boolean.valueOf(result));
/*      */         }
/*      */         catch (RemoteException e) {
/* 4775 */           if (this.val$callback != null)
/* 4776 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public boolean clearConversations(Conversation.ConversationType[] conversationTypes)
/*      */   {
/* 4793 */     CountDownLatch latch = new CountDownLatch(1);
/* 4794 */     RongIMClient.ResultCallback.Result result = new RongIMClient.ResultCallback.Result();
/* 4795 */     result.t = Boolean.valueOf(false);
/*      */ 
/* 4797 */     clearConversations(new SyncCallback(result, latch)
/*      */     {
/*      */       public void onSuccess(Boolean isSuccess)
/*      */       {
/* 4801 */         if (isSuccess != null)
/* 4802 */           this.val$result.t = isSuccess;
/*      */         else {
/* 4804 */           RLog.e("RongIMClient", "clearConversations clearConversations is failure!");
/*      */         }
/*      */ 
/* 4807 */         this.val$latch.countDown();
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 4812 */         this.val$latch.countDown();
/*      */       }
/*      */     }
/*      */     , conversationTypes);
/*      */     try
/*      */     {
/* 4817 */       latch.await();
/*      */     } catch (InterruptedException e) {
/* 4819 */       e.printStackTrace();
/*      */     }
/* 4821 */     return ((Boolean)result.t).booleanValue();
/*      */   }
/*      */ 
/*      */   public void addToBlacklist(String userId, OperationCallback callback)
/*      */   {
/* 4833 */     if (TextUtils.isEmpty(userId)) {
/* 4834 */       RLog.e("RongIMClient", "userId  is null!");
/* 4835 */       if (callback != null)
/* 4836 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 4837 */       return;
/*      */     }
/*      */ 
/* 4840 */     this.mWorkHandler.post(new Runnable(callback, userId)
/*      */     {
/*      */       public void run() {
/* 4843 */         if (RongIMClient.this.mLibHandler == null) {
/* 4844 */           if (this.val$callback != null)
/* 4845 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4846 */           return;
/*      */         }
/*      */         try {
/* 4849 */           RongIMClient.this.mLibHandler.addToBlacklist(this.val$userId, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException
/*      */             {
/* 4853 */               if (RongIMClient.83.this.val$callback != null)
/* 4854 */                 RongIMClient.83.this.val$callback.onCallback();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 4861 */               if (RongIMClient.83.this.val$callback != null)
/* 4862 */                 RongIMClient.83.this.val$callback.onFail(errorCode);
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (RemoteException e) {
/* 4868 */           e.printStackTrace();
/* 4869 */           if (this.val$callback != null)
/* 4870 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void removeFromBlacklist(String userId, OperationCallback callback)
/*      */   {
/* 4883 */     if (TextUtils.isEmpty(userId)) {
/* 4884 */       RLog.e("RongIMClient", "userId  is null!");
/* 4885 */       if (callback != null)
/* 4886 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 4887 */       return;
/*      */     }
/*      */ 
/* 4890 */     this.mWorkHandler.post(new Runnable(callback, userId)
/*      */     {
/*      */       public void run() {
/* 4893 */         if (RongIMClient.this.mLibHandler == null) {
/* 4894 */           if (this.val$callback != null)
/* 4895 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4896 */           return;
/*      */         }
/*      */         try {
/* 4899 */           RongIMClient.this.mLibHandler.removeFromBlacklist(this.val$userId, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException {
/* 4902 */               if (RongIMClient.84.this.val$callback != null)
/* 4903 */                 RongIMClient.84.this.val$callback.onCallback();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 4909 */               if (RongIMClient.84.this.val$callback != null)
/* 4910 */                 RongIMClient.84.this.val$callback.onFail(errorCode);
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (RemoteException e) {
/* 4916 */           e.printStackTrace();
/* 4917 */           if (this.val$callback != null)
/* 4918 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getBlacklistStatus(String userId, ResultCallback<BlacklistStatus> callback)
/*      */   {
/* 4931 */     if (TextUtils.isEmpty(userId)) {
/* 4932 */       RLog.e("RongIMClient", "userId  is null!");
/* 4933 */       if (callback != null)
/* 4934 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 4935 */       return;
/*      */     }
/*      */ 
/* 4938 */     this.mWorkHandler.post(new Runnable(callback, userId)
/*      */     {
/*      */       public void run() {
/* 4941 */         if (RongIMClient.this.mLibHandler == null) {
/* 4942 */           if (this.val$callback != null)
/* 4943 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4944 */           return;
/*      */         }
/*      */         try {
/* 4947 */           RongIMClient.this.mLibHandler.getBlacklistStatus(this.val$userId, new IIntegerCallback.Stub()
/*      */           {
/*      */             public void onComplete(int result) throws RemoteException {
/* 4950 */               if (RongIMClient.85.this.val$callback != null)
/* 4951 */                 RongIMClient.85.this.val$callback.onCallback(RongIMClient.BlacklistStatus.setValue(result));
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 4957 */               if (RongIMClient.85.this.val$callback != null)
/* 4958 */                 RongIMClient.85.this.val$callback.onFail(errorCode);
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 4963 */           e.printStackTrace();
/* 4964 */           if (this.val$callback != null)
/* 4965 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getBlacklist(GetBlacklistCallback callback)
/*      */   {
/* 4977 */     this.mWorkHandler.post(new Runnable(callback)
/*      */     {
/*      */       public void run() {
/* 4980 */         if (RongIMClient.this.mLibHandler == null) {
/* 4981 */           if (this.val$callback != null)
/* 4982 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 4983 */           return;
/*      */         }
/*      */         try {
/* 4986 */           RongIMClient.this.mLibHandler.getBlacklist(new IStringCallback.Stub()
/*      */           {
/*      */             public void onComplete(String result) throws RemoteException
/*      */             {
/* 4990 */               if (RongIMClient.86.this.val$callback != null)
/* 4991 */                 if (result == null)
/* 4992 */                   RongIMClient.86.this.val$callback.onCallback(null);
/*      */                 else
/* 4994 */                   RongIMClient.86.this.val$callback.onCallback(result.split("\n"));
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 5001 */               if (RongIMClient.86.this.val$callback != null)
/* 5002 */                 RongIMClient.86.this.val$callback.onFail(errorCode);
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 5007 */           e.printStackTrace();
/* 5008 */           if (this.val$callback != null)
/* 5009 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void searchPublicService(SearchType searchType, String keywords, ResultCallback<PublicServiceProfileList> callback)
/*      */   {
/* 5023 */     if (searchType == null) {
/* 5024 */       RLog.e("RongIMClient", "searchType  is null!");
/* 5025 */       if (callback != null)
/* 5026 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 5027 */       return;
/*      */     }
/*      */ 
/* 5030 */     this.mWorkHandler.post(new Runnable(callback, keywords, searchType)
/*      */     {
/*      */       public void run() {
/* 5033 */         if (RongIMClient.this.mLibHandler == null) {
/* 5034 */           if (this.val$callback != null)
/* 5035 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 5036 */           return;
/*      */         }
/*      */         try
/*      */         {
/* 5040 */           RongIMClient.this.mLibHandler.searchPublicService(this.val$keywords, 0, this.val$searchType.getValue(), new IResultCallback.Stub()
/*      */           {
/*      */             public void onComplete(RemoteModelWrap model)
/*      */               throws RemoteException
/*      */             {
/* 5045 */               if ((RongIMClient.87.this.val$callback != null) && (model != null) && (model.getContent() != null) && ((model.getContent() instanceof PublicServiceProfileList)))
/*      */               {
/* 5047 */                 RongIMClient.87.this.val$callback.onCallback((PublicServiceProfileList)model.getContent());
/*      */               }
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode) throws RemoteException
/*      */             {
/* 5053 */               if (RongIMClient.87.this.val$callback != null)
/* 5054 */                 RongIMClient.87.this.val$callback.onFail(errorCode);
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 5059 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void searchPublicServiceByType(Conversation.PublicServiceType publicServiceType, SearchType searchType, String keywords, ResultCallback<PublicServiceProfileList> callback)
/*      */   {
/* 5074 */     if ((publicServiceType == null) || (searchType == null)) {
/* 5075 */       RLog.e("RongIMClient", "searchType  is null!");
/* 5076 */       if (callback != null)
/* 5077 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 5078 */       return;
/*      */     }
/*      */ 
/* 5081 */     int[] businessType = { 0 };
/* 5082 */     if (publicServiceType == Conversation.PublicServiceType.APP_PUBLIC_SERVICE)
/* 5083 */       businessType[0] = 2;
/* 5084 */     else if (publicServiceType == Conversation.PublicServiceType.PUBLIC_SERVICE) {
/* 5085 */       businessType[0] = 1;
/*      */     }
/*      */ 
/* 5088 */     this.mWorkHandler.post(new Runnable(callback, keywords, businessType, searchType)
/*      */     {
/*      */       public void run()
/*      */       {
/* 5092 */         if (RongIMClient.this.mLibHandler == null) {
/* 5093 */           if (this.val$callback != null)
/* 5094 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 5095 */           return;
/*      */         }
/*      */         try {
/* 5098 */           RongIMClient.this.mLibHandler.searchPublicService(this.val$keywords, this.val$businessType[0], this.val$searchType.getValue(), new IResultCallback.Stub()
/*      */           {
/*      */             public void onComplete(RemoteModelWrap model)
/*      */               throws RemoteException
/*      */             {
/* 5103 */               if ((RongIMClient.88.this.val$callback != null) && (model != null) && (model.getContent() != null) && ((model.getContent() instanceof PublicServiceProfileList)))
/*      */               {
/* 5105 */                 RongIMClient.88.this.val$callback.onCallback((PublicServiceProfileList)model.getContent());
/*      */               }
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode) throws RemoteException
/*      */             {
/* 5111 */               if (RongIMClient.88.this.val$callback != null)
/* 5112 */                 RongIMClient.88.this.val$callback.onFail(errorCode);
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 5117 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void subscribePublicService(Conversation.PublicServiceType publicServiceType, String publicServiceId, OperationCallback callback)
/*      */   {
/* 5131 */     if ((publicServiceType == null) || (TextUtils.isEmpty(publicServiceId))) {
/* 5132 */       RLog.e("RongIMClient", "Parameter  is error!");
/* 5133 */       if (callback != null)
/* 5134 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 5135 */       return;
/*      */     }
/*      */ 
/* 5138 */     this.mWorkHandler.post(new Runnable(callback, publicServiceId, publicServiceType)
/*      */     {
/*      */       public void run() {
/* 5141 */         if (RongIMClient.this.mLibHandler == null) {
/* 5142 */           if (this.val$callback != null)
/* 5143 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 5144 */           return;
/*      */         }
/*      */         try {
/* 5147 */           RongIMClient.this.mLibHandler.subscribePublicService(this.val$publicServiceId, this.val$publicServiceType.getValue(), true, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException {
/* 5150 */               if (RongIMClient.89.this.val$callback != null)
/* 5151 */                 RongIMClient.89.this.val$callback.onCallback();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 5157 */               if (RongIMClient.89.this.val$callback != null)
/* 5158 */                 RongIMClient.89.this.val$callback.onError(RongIMClient.ErrorCode.valueOf(errorCode));
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 5163 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void unsubscribePublicService(Conversation.PublicServiceType publicServiceType, String publicServiceId, OperationCallback callback)
/*      */   {
/* 5177 */     if ((publicServiceType == null) || (TextUtils.isEmpty(publicServiceId))) {
/* 5178 */       RLog.e("RongIMClient", "Parameter  is error!");
/* 5179 */       if (callback != null)
/* 5180 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 5181 */       return;
/*      */     }
/*      */ 
/* 5184 */     this.mWorkHandler.post(new Runnable(callback, publicServiceId, publicServiceType)
/*      */     {
/*      */       public void run() {
/* 5187 */         if (RongIMClient.this.mLibHandler == null) {
/* 5188 */           if (this.val$callback != null)
/* 5189 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 5190 */           return;
/*      */         }
/*      */         try {
/* 5193 */           RongIMClient.this.mLibHandler.subscribePublicService(this.val$publicServiceId, this.val$publicServiceType.getValue(), false, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException
/*      */             {
/* 5197 */               if (RongIMClient.90.this.val$callback != null)
/* 5198 */                 RongIMClient.90.this.val$callback.onCallback();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 5204 */               if (RongIMClient.90.this.val$callback != null)
/* 5205 */                 RongIMClient.90.this.val$callback.onError(RongIMClient.ErrorCode.valueOf(errorCode));
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 5210 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getPublicServiceProfile(Conversation.PublicServiceType publicServiceType, String publicServiceId, ResultCallback<PublicServiceProfile> callback)
/*      */   {
/* 5224 */     if ((publicServiceType == null) || (TextUtils.isEmpty(publicServiceId))) {
/* 5225 */       RLog.e("RongIMClient", "Parameter  is error!");
/* 5226 */       if (callback != null)
/* 5227 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/* 5228 */       return;
/*      */     }
/*      */ 
/* 5231 */     this.mWorkHandler.post(new Runnable(callback, publicServiceId, publicServiceType)
/*      */     {
/*      */       public void run() {
/* 5234 */         if (RongIMClient.this.mLibHandler == null) {
/* 5235 */           if (this.val$callback != null)
/* 5236 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 5237 */           return;
/*      */         }
/*      */         try {
/* 5240 */           RongIMClient.this.mLibHandler.getPublicServiceProfile(this.val$publicServiceId, this.val$publicServiceType.getValue(), new IResultCallback.Stub()
/*      */           {
/*      */             public void onComplete(RemoteModelWrap model) throws RemoteException
/*      */             {
/* 5244 */               if (RongIMClient.91.this.val$callback != null) {
/* 5245 */                 PublicServiceProfile publicServiceProfile = null;
/* 5246 */                 if (model != null)
/* 5247 */                   publicServiceProfile = (PublicServiceProfile)model.getContent();
/* 5248 */                 RongIMClient.91.this.val$callback.onCallback(publicServiceProfile);
/*      */               }
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode) throws RemoteException
/*      */             {
/* 5254 */               if (RongIMClient.91.this.val$callback != null)
/* 5255 */                 RongIMClient.91.this.val$callback.onFail(RongIMClient.ErrorCode.valueOf(errorCode));
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 5260 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getPublicServiceList(ResultCallback<PublicServiceProfileList> callback)
/*      */   {
/* 5273 */     this.mWorkHandler.post(new Runnable(callback)
/*      */     {
/*      */       public void run() {
/* 5276 */         if (RongIMClient.this.mLibHandler == null) {
/* 5277 */           if (this.val$callback != null)
/* 5278 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 5279 */           return;
/*      */         }
/*      */         try {
/* 5282 */           RongIMClient.this.mLibHandler.getPublicServiceList(new IResultCallback.Stub()
/*      */           {
/*      */             public void onComplete(RemoteModelWrap model) throws RemoteException
/*      */             {
/* 5286 */               if (RongIMClient.92.this.val$callback != null) {
/* 5287 */                 PublicServiceProfileList publicServiceInfoList = (PublicServiceProfileList)model.getContent();
/* 5288 */                 RongIMClient.92.this.val$callback.onCallback(publicServiceInfoList);
/*      */               }
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode) throws RemoteException
/*      */             {
/* 5294 */               if (RongIMClient.92.this.val$callback != null)
/* 5295 */                 RongIMClient.92.this.val$callback.onError(RongIMClient.ErrorCode.valueOf(errorCode));
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 5300 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setNotificationQuietHours(String startTime, int spanMinutes, OperationCallback callback)
/*      */   {
/* 5315 */     if ((TextUtils.isEmpty(startTime)) || (spanMinutes <= 0) || (spanMinutes >= 1440)) {
/* 5316 */       RLog.e("RongIMClient", "startTime, spanMinutes 或 spanMinutes 参数异常。");
/* 5317 */       if (callback != null) {
/* 5318 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/*      */       }
/* 5320 */       return;
/*      */     }
/* 5322 */     Pattern pattern = Pattern.compile("^(([0-1][0-9])|2[0-3]):[0-5][0-9]:([0-5][0-9])$");
/* 5323 */     Matcher matcher = pattern.matcher(startTime);
/*      */ 
/* 5325 */     if (!matcher.find()) {
/* 5326 */       RLog.e("RongIMClient", "startTime 参数异常。");
/* 5327 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 5328 */       return;
/*      */     }
/*      */ 
/* 5331 */     this.mWorkHandler.post(new Runnable(callback, startTime, spanMinutes)
/*      */     {
/*      */       public void run() {
/* 5334 */         if (RongIMClient.this.mLibHandler == null) {
/* 5335 */           if (this.val$callback != null)
/* 5336 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 5337 */           return;
/*      */         }
/*      */         try {
/* 5340 */           RongIMClient.this.mLibHandler.setNotificationQuietHours(this.val$startTime, this.val$spanMinutes, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException {
/* 5343 */               RongIMClient.93.this.val$callback.onSuccess();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode) throws RemoteException
/*      */             {
/* 5348 */               RongIMClient.93.this.val$callback.onError(RongIMClient.ErrorCode.valueOf(errorCode));
/*      */             } } );
/*      */         } catch (RemoteException e) {
/* 5352 */           e.printStackTrace();
/*      */ 
/* 5354 */           this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void removeNotificationQuietHours(OperationCallback callback)
/*      */   {
/* 5366 */     this.mWorkHandler.post(new Runnable(callback)
/*      */     {
/*      */       public void run() {
/* 5369 */         if (RongIMClient.this.mLibHandler == null) {
/* 5370 */           if (this.val$callback != null)
/* 5371 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 5372 */           return;
/*      */         }
/*      */         try {
/* 5375 */           RongIMClient.this.mLibHandler.removeNotificationQuietHours(new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException {
/* 5378 */               if (RongIMClient.94.this.val$callback != null)
/* 5379 */                 RongIMClient.94.this.val$callback.onSuccess();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 5385 */               RongIMClient.94.this.val$callback.onError(RongIMClient.ErrorCode.valueOf(errorCode));
/*      */             } } );
/*      */         } catch (RemoteException e) {
/* 5389 */           e.printStackTrace();
/*      */ 
/* 5391 */           if (this.val$callback != null)
/* 5392 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private void updateMessageReceiptStatus(Conversation.ConversationType conversationType, String targetId, long timestamp, OperationCallback callback)
/*      */   {
/* 5407 */     this.mWorkHandler.post(new Runnable(callback, targetId, conversationType, timestamp)
/*      */     {
/*      */       public void run() {
/* 5410 */         if (RongIMClient.this.mLibHandler == null) {
/* 5411 */           if (this.val$callback != null)
/* 5412 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 5413 */           return;
/*      */         }
/*      */         try {
/* 5416 */           if (RongIMClient.this.mLibHandler.updateMessageReceiptStatus(this.val$targetId, this.val$conversationType.getValue(), this.val$timestamp)) {
/* 5417 */             if (this.val$callback != null) {
/* 5418 */               this.val$callback.onSuccess();
/*      */             }
/*      */           }
/* 5421 */           else if (this.val$callback != null)
/* 5422 */             this.val$callback.onFail(RongIMClient.ErrorCode.UNKNOWN);
/*      */         }
/*      */         catch (RemoteException e)
/*      */         {
/* 5426 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void clearMessagesUnreadStatus(Conversation.ConversationType conversationType, String targetId, long timestamp, OperationCallback callback)
/*      */   {
/* 5441 */     this.mWorkHandler.post(new Runnable(callback, conversationType, targetId, timestamp)
/*      */     {
/*      */       public void run() {
/* 5444 */         if (RongIMClient.this.mLibHandler == null) {
/* 5445 */           if (this.val$callback != null)
/* 5446 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 5447 */           return;
/*      */         }
/*      */         try {
/* 5450 */           if (RongIMClient.this.mLibHandler.clearUnreadByReceipt(this.val$conversationType.getValue(), this.val$targetId, this.val$timestamp)) {
/* 5451 */             if (this.val$callback != null) {
/* 5452 */               this.val$callback.onSuccess();
/*      */             }
/*      */           }
/* 5455 */           else if (this.val$callback != null)
/* 5456 */             this.val$callback.onFail(RongIMClient.ErrorCode.UNKNOWN);
/*      */         }
/*      */         catch (RemoteException e)
/*      */         {
/* 5460 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public long getSendTimeByMessageId(int messageId)
/*      */   {
/*      */     try
/*      */     {
/* 5474 */       if (this.mLibHandler == null) {
/* 5475 */         RLog.e("RongIMClient", "getSendTimeByMessageId mLibHandler is null!");
/* 5476 */         return 0L;
/*      */       }
/*      */ 
/* 5479 */       return this.mLibHandler.getSendTimeByMessageId(messageId);
/*      */     } catch (RemoteException e) {
/* 5481 */       e.printStackTrace();
/* 5482 */     }return 0L;
/*      */   }
/*      */ 
/*      */   public void getNotificationQuietHours(GetNotificationQuietHoursCallback callback)
/*      */   {
/* 5492 */     this.mWorkHandler.post(new Runnable(callback)
/*      */     {
/*      */       public void run() {
/* 5495 */         if (RongIMClient.this.mLibHandler == null) {
/* 5496 */           if (this.val$callback != null)
/* 5497 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 5498 */           return;
/*      */         }
/*      */         try {
/* 5501 */           RongIMClient.this.mLibHandler.getNotificationQuietHours(new IGetNotificationQuietHoursCallback.Stub()
/*      */           {
/*      */             public void onSuccess(String startTime, int spanMin) {
/* 5504 */               if (RongIMClient.97.this.val$callback != null)
/* 5505 */                 RongIMClient.97.this.val$callback.onSuccess(startTime, spanMin);
/*      */             }
/*      */ 
/*      */             public void onError(int code)
/*      */             {
/* 5511 */               if (RongIMClient.97.this.val$callback != null)
/* 5512 */                 RongIMClient.97.this.val$callback.onError(RongIMClient.ErrorCode.valueOf(code));
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 5517 */           e.printStackTrace();
/*      */ 
/* 5519 */           if (this.val$callback != null)
/* 5520 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void syncUserData(UserData userData, OperationCallback callback)
/*      */   {
/* 5534 */     if (userData == null) {
/* 5535 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 5536 */       return;
/*      */     }
/* 5538 */     this.mWorkHandler.post(new Runnable(callback, userData)
/*      */     {
/*      */       public void run() {
/* 5541 */         if (RongIMClient.this.mLibHandler == null) {
/* 5542 */           if (this.val$callback != null)
/* 5543 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 5544 */           return;
/*      */         }
/*      */         try {
/* 5547 */           RongIMClient.this.mLibHandler.setUserData(this.val$userData, new IOperationCallback.Stub()
/*      */           {
/*      */             public void onComplete() throws RemoteException {
/* 5550 */               if (RongIMClient.98.this.val$callback != null)
/* 5551 */                 RongIMClient.98.this.val$callback.onSuccess();
/*      */             }
/*      */ 
/*      */             public void onFailure(int errorCode)
/*      */               throws RemoteException
/*      */             {
/* 5557 */               RongIMClient.98.this.val$callback.onError(RongIMClient.ErrorCode.valueOf(errorCode));
/*      */             } } );
/*      */         } catch (RemoteException e) {
/* 5561 */           e.printStackTrace();
/*      */ 
/* 5563 */           if (this.val$callback != null)
/* 5564 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private void handleReadReceiptMessage(Message message)
/*      */   {
/* 5649 */     if (message.getMessageDirection().equals(Message.MessageDirection.SEND))
/* 5650 */       clearMessagesUnreadStatus(message.getConversationType(), message.getTargetId(), ((ReadReceiptMessage)message.getContent()).getLastMessageSendTime(), new OperationCallback(message)
/*      */       {
/*      */         public void onSuccess() {
/* 5653 */           if (RongIMClient.this.mSyncConversationReadStatusListener != null)
/* 5654 */             RongIMClient.this.mSyncConversationReadStatusListener.onSyncConversationReadStatus(this.val$message.getConversationType(), this.val$message.getTargetId());
/*      */         }
/*      */ 
/*      */         public void onError(RongIMClient.ErrorCode errorCode)
/*      */         {
/* 5660 */           RLog.e("RongIMClient", "RongIMClient : clearMessagesUnreadStatus fail");
/*      */         }
/*      */       });
/* 5664 */     else updateMessageReceiptStatus(message.getConversationType(), message.getTargetId(), ((ReadReceiptMessage)message.getContent()).getLastMessageSendTime(), new OperationCallback(message)
/*      */       {
/*      */         public void onSuccess() {
/* 5667 */           if (RongIMClient.sReadReceiptListener != null)
/* 5668 */             RongIMClient.sReadReceiptListener.onReadReceiptReceived(this.val$message);
/*      */         }
/*      */ 
/*      */         public void onError(RongIMClient.ErrorCode errorCode)
/*      */         {
/* 5674 */           RLog.e("RongIMClient", "RongIMClient : updateMessageReceiptStatus fail");
/*      */         }
/*      */       }); 
/*      */   }
/*      */ 
/*      */   private boolean handleCmdMessages(Message message, int left, boolean offline, int cmdLeft)
/*      */   {
/* 5681 */     if ((left == 0) && (this.mReadReceiptMap.size() > 0) && (sReadReceiptListener != null)) {
/* 5682 */       for (Map.Entry entry : this.mReadReceiptMap.entrySet()) {
/* 5683 */         handleReadReceiptMessage((Message)entry.getValue());
/*      */       }
/* 5685 */       this.mReadReceiptMap.clear();
/*      */     }
/*      */ 
/* 5688 */     boolean result = ModuleManager.handleReceivedMessage(message, left, offline, cmdLeft);
/* 5689 */     if (result) {
/* 5690 */       return true;
/*      */     }
/*      */ 
/* 5695 */     if (TypingMessageManager.getInstance().isShowMessageTyping()) {
/* 5696 */       boolean isTypingMessage = TypingMessageManager.getInstance().onReceiveMessage(message);
/* 5697 */       if (isTypingMessage) {
/* 5698 */         return true;
/*      */       }
/*      */     }
/*      */ 
/* 5702 */     MessageTag tag = (MessageTag)ReadReceiptMessage.class.getAnnotation(MessageTag.class);
/* 5703 */     if (message.getObjectName().equals(tag.value())) {
/* 5704 */       if (left > 0) {
/* 5705 */         String key = message.getConversationType().getValue() + "#@6RONG_CLOUD9@#" + message.getTargetId();
/* 5706 */         this.mReadReceiptMap.put(key, message);
/*      */       } else {
/* 5708 */         handleReadReceiptMessage(message);
/*      */       }
/* 5710 */       return true;
/*      */     }
/*      */ 
/* 5713 */     tag = (MessageTag)SyncReadStatusMessage.class.getAnnotation(MessageTag.class);
/* 5714 */     if (message.getObjectName().equals(tag.value())) {
/* 5715 */       if (message.getMessageDirection() == Message.MessageDirection.SEND)
/* 5716 */         clearMessagesUnreadStatus(message.getConversationType(), message.getTargetId(), ((SyncReadStatusMessage)message.getContent()).getLastMessageSendTime(), new OperationCallback(message)
/*      */         {
/*      */           public void onSuccess() {
/* 5719 */             if (RongIMClient.this.mSyncConversationReadStatusListener != null)
/* 5720 */               RongIMClient.this.mSyncConversationReadStatusListener.onSyncConversationReadStatus(this.val$message.getConversationType(), this.val$message.getTargetId());
/*      */           }
/*      */ 
/*      */           public void onError(RongIMClient.ErrorCode errorCode)
/*      */           {
/* 5726 */             RLog.e("RongIMClient", "RongIMClient : clearMessagesUnreadStatus fail");
/*      */           }
/*      */         });
/* 5730 */       return true;
/*      */     }
/*      */ 
/* 5733 */     tag = (MessageTag)ReadReceiptRequestMessage.class.getAnnotation(MessageTag.class);
/* 5734 */     if (message.getObjectName().equals(tag.value())) {
/* 5735 */       if (message.getMessageDirection().equals(Message.MessageDirection.SEND)) {
/* 5736 */         return true;
/*      */       }
/* 5738 */       if ((!message.getConversationType().equals(Conversation.ConversationType.GROUP)) && (!message.getConversationType().equals(Conversation.ConversationType.DISCUSSION))) {
/* 5739 */         return true;
/*      */       }
/* 5741 */       ReadReceiptRequestMessage requestMessage = (ReadReceiptRequestMessage)message.getContent();
/* 5742 */       getMessageByUid(requestMessage.getMessageUId(), new ResultCallback(requestMessage, message)
/*      */       {
/*      */         public void onSuccess(Message msg) {
/* 5745 */           if (msg == null) {
/* 5746 */             return;
/*      */           }
/* 5748 */           ReadReceiptInfo readReceiptInfo = msg.getReadReceiptInfo();
/* 5749 */           if (readReceiptInfo == null) {
/* 5750 */             readReceiptInfo = new ReadReceiptInfo();
/* 5751 */             msg.setReadReceiptInfo(readReceiptInfo);
/*      */           }
/* 5753 */           readReceiptInfo.setIsReadReceiptMessage(true);
/* 5754 */           readReceiptInfo.setHasRespond(false);
/*      */           try {
/* 5756 */             RongIMClient.this.mLibHandler.updateReadReceiptRequestInfo(this.val$requestMessage.getMessageUId(), readReceiptInfo.toJSON().toString());
/*      */           } catch (RemoteException e) {
/* 5758 */             e.printStackTrace();
/* 5759 */             return;
/*      */           }
/* 5761 */           if (RongIMClient.sReadReceiptListener != null)
/* 5762 */             RongIMClient.sReadReceiptListener.onMessageReceiptRequest(this.val$message.getConversationType(), this.val$message.getTargetId(), this.val$requestMessage.getMessageUId());
/*      */         }
/*      */ 
/*      */         public void onError(RongIMClient.ErrorCode e)
/*      */         {
/* 5768 */           RLog.e("RongIMClient", "readReceipt request received, but getMessageByUid failed");
/*      */         }
/*      */       });
/* 5771 */       return true;
/*      */     }
/*      */ 
/* 5774 */     tag = (MessageTag)ReadReceiptResponseMessage.class.getAnnotation(MessageTag.class);
/* 5775 */     if (message.getObjectName().equals(tag.value())) {
/* 5776 */       if (message.getMessageDirection().equals(Message.MessageDirection.SEND)) {
/* 5777 */         return true;
/*      */       }
/* 5779 */       if ((!message.getConversationType().equals(Conversation.ConversationType.GROUP)) && (!message.getConversationType().equals(Conversation.ConversationType.DISCUSSION))) {
/* 5780 */         return true;
/*      */       }
/* 5782 */       ReadReceiptResponseMessage responseMessage = (ReadReceiptResponseMessage)message.getContent();
/* 5783 */       ArrayList messageUIdList = responseMessage.getMessageUIdListBySenderId(getCurrentUserId());
/* 5784 */       String senderUserId = message.getSenderUserId();
/* 5785 */       if (messageUIdList != null) {
/* 5786 */         for (String messageUId : messageUIdList)
/* 5787 */           getMessageByUid(messageUId, new ResultCallback(senderUserId, message, messageUId)
/*      */           {
/*      */             public void onSuccess(Message msg) {
/* 5790 */               if (msg == null) {
/* 5791 */                 return;
/*      */               }
/* 5793 */               ReadReceiptInfo readReceiptInfo = msg.getReadReceiptInfo();
/* 5794 */               if (readReceiptInfo == null) {
/* 5795 */                 readReceiptInfo = new ReadReceiptInfo();
/* 5796 */                 msg.setReadReceiptInfo(readReceiptInfo);
/*      */               }
/* 5798 */               readReceiptInfo.setIsReadReceiptMessage(true);
/* 5799 */               HashMap respondUserIdList = readReceiptInfo.getRespondUserIdList();
/* 5800 */               if (respondUserIdList == null) {
/* 5801 */                 respondUserIdList = new HashMap();
/* 5802 */                 readReceiptInfo.setRespondUserIdList(respondUserIdList);
/*      */               }
/* 5804 */               respondUserIdList.put(this.val$senderUserId, Long.valueOf(this.val$message.getSentTime()));
/*      */               try {
/* 5806 */                 RongIMClient.this.mLibHandler.updateReadReceiptRequestInfo(this.val$messageUId, readReceiptInfo.toJSON().toString());
/*      */               } catch (RemoteException e) {
/* 5808 */                 e.printStackTrace();
/* 5809 */                 return;
/*      */               }
/* 5811 */               if (RongIMClient.sReadReceiptListener != null)
/* 5812 */                 RongIMClient.sReadReceiptListener.onMessageReceiptResponse(this.val$message.getConversationType(), this.val$message.getTargetId(), this.val$messageUId, respondUserIdList);
/*      */             }
/*      */ 
/*      */             public void onError(RongIMClient.ErrorCode e)
/*      */             {
/* 5818 */               RLog.e("RongIMClient", "readReceipt response received, but getMessageByUid failed");
/*      */             }
/*      */           });
/*      */       }
/* 5823 */       return true;
/*      */     }
/*      */ 
/* 5827 */     tag = (MessageTag)RecallCommandMessage.class.getAnnotation(MessageTag.class);
/* 5828 */     if (message.getObjectName().equals(tag.value())) {
/* 5829 */       RecallCommandMessage recallCommandMessage = (RecallCommandMessage)message.getContent();
/* 5830 */       getMessageByUid(recallCommandMessage.getMessageUId(), new ResultCallback(message)
/*      */       {
/*      */         public void onSuccess(Message msg) {
/* 5833 */           if (msg == null) {
/* 5834 */             return;
/*      */           }
/* 5836 */           RecallNotificationMessage recallNotificationMessage = new RecallNotificationMessage(this.val$message.getSenderUserId(), msg.getSentTime(), msg.getObjectName());
/* 5837 */           byte[] data = recallNotificationMessage.encode();
/*      */           try {
/* 5839 */             MessageTag recallNotificationTag = (MessageTag)RecallNotificationMessage.class.getAnnotation(MessageTag.class);
/* 5840 */             RongIMClient.this.mLibHandler.setMessageContent(msg.getMessageId(), data, recallNotificationTag.value());
/*      */           } catch (RemoteException e) {
/* 5842 */             e.printStackTrace();
/* 5843 */             return;
/*      */           }
/* 5845 */           if (RongIMClient.sRecallMessageListener != null)
/* 5846 */             RongIMClient.sRecallMessageListener.onMessageRecalled(msg.getMessageId(), recallNotificationMessage);
/*      */         }
/*      */ 
/*      */         public void onError(RongIMClient.ErrorCode e)
/*      */         {
/* 5852 */           RLog.e("RongIMClient", "recall message received, but getMessageByUid failed");
/*      */         }
/*      */       });
/* 5855 */       return true;
/*      */     }
/*      */ 
/* 5859 */     MessageTag csHSRTag = (MessageTag)CSHandShakeResponseMessage.class.getAnnotation(MessageTag.class);
/* 5860 */     MessageTag csCMRTag = (MessageTag)CSChangeModeResponseMessage.class.getAnnotation(MessageTag.class);
/* 5861 */     MessageTag csTTag = (MessageTag)CSTerminateMessage.class.getAnnotation(MessageTag.class);
/* 5862 */     MessageTag csUpdateTag = (MessageTag)CSUpdateMessage.class.getAnnotation(MessageTag.class);
/* 5863 */     MessageTag csPullEva = (MessageTag)CSPullEvaluateMessage.class.getAnnotation(MessageTag.class);
/*      */ 
/* 5865 */     if (message.getObjectName().equals(csHSRTag.value())) {
/* 5866 */       CSHandShakeResponseMessage csHandShakeResponseMessage = (CSHandShakeResponseMessage)message.getContent();
/* 5867 */       int code = csHandShakeResponseMessage.getCode();
/* 5868 */       String msg = csHandShakeResponseMessage.getMsg();
/* 5869 */       String kefuId = message.getTargetId();
/* 5870 */       CustomServiceProfile profile = (CustomServiceProfile)this.customServiceCache.get(kefuId);
/* 5871 */       if (profile == null) {
/* 5872 */         return true;
/*      */       }
/* 5874 */       if ((code == 0) && (profile.customServiceListener != null)) {
/* 5875 */         String strFail = this.mContext.getResources().getString(this.mContext.getResources().getIdentifier("rc_init_failed", "string", this.mContext.getPackageName()));
/* 5876 */         mHandler.post(new Runnable(profile, code, msg, strFail)
/*      */         {
/*      */           public void run() {
/* 5879 */             if (this.val$profile.customServiceListener != null) {
/* 5880 */               this.val$profile.customServiceListener.onError(this.val$code, TextUtils.isEmpty(this.val$msg) ? this.val$strFail : this.val$msg);
/* 5881 */               this.val$profile.customServiceListener = null;
/*      */             }
/*      */           }
/*      */         });
/* 5885 */         return true;
/*      */       }
/* 5887 */       profile.mode = csHandShakeResponseMessage.getMode();
/* 5888 */       profile.sid = csHandShakeResponseMessage.getSid();
/* 5889 */       profile.uid = csHandShakeResponseMessage.getUid();
/* 5890 */       profile.pid = csHandShakeResponseMessage.getPid();
/* 5891 */       profile.groupList = csHandShakeResponseMessage.getGroupList();
/*      */ 
/* 5893 */       this.customServiceCache.put(kefuId, profile);
/* 5894 */       if (profile.customServiceListener != null) {
/* 5895 */         CustomServiceConfig config = new CustomServiceConfig();
/* 5896 */         config.companyName = csHandShakeResponseMessage.getCompanyName();
/* 5897 */         config.isBlack = csHandShakeResponseMessage.isBlack();
/* 5898 */         config.msg = csHandShakeResponseMessage.getMsg();
/* 5899 */         config.companyIcon = csHandShakeResponseMessage.getCompanyIcon();
/* 5900 */         config.robotSessionNoEva = ((csHandShakeResponseMessage.getRobotSessionNoEva() != null) && (csHandShakeResponseMessage.getRobotSessionNoEva().equals("1")));
/*      */ 
/* 5902 */         config.humanEvaluateList = csHandShakeResponseMessage.getHumanEvaluateList();
/*      */ 
/* 5904 */         mHandler.post(new Runnable(profile, config)
/*      */         {
/*      */           public void run() {
/* 5907 */             if (this.val$profile.customServiceListener != null) {
/* 5908 */               this.val$profile.customServiceListener.onSuccess(this.val$config);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/* 5915 */       String portrait = csHandShakeResponseMessage.getRobotLogo();
/* 5916 */       String name = csHandShakeResponseMessage.getRobotName();
/* 5917 */       String hello = csHandShakeResponseMessage.getRobotHelloWord();
/* 5918 */       profile.welcome = hello;
/* 5919 */       profile.name = name;
/* 5920 */       profile.portrait = portrait;
/* 5921 */       if ((csHandShakeResponseMessage.getMode().equals(CustomServiceMode.CUSTOM_SERVICE_MODE_ROBOT)) || (csHandShakeResponseMessage.getMode().equals(CustomServiceMode.CUSTOM_SERVICE_MODE_ROBOT_FIRST)))
/*      */       {
/* 5923 */         if (!TextUtils.isEmpty(hello)) {
/* 5924 */           TextMessage textMessage = TextMessage.obtain(hello);
/* 5925 */           if (portrait != null) {
/* 5926 */             textMessage.setUserInfo(new UserInfo(kefuId, name, Uri.parse(portrait)));
/*      */           }
/* 5928 */           insertMessage(Conversation.ConversationType.CUSTOMER_SERVICE, kefuId, kefuId, textMessage, new ResultCallback()
/*      */           {
/*      */             public void onSuccess(Message message) {
/* 5931 */               if (RongIMClient.sReceiveMessageListener != null)
/* 5932 */                 RongIMClient.sReceiveMessageListener.onReceived(message, 0);
/*      */             }
/*      */ 
/*      */             public void onError(RongIMClient.ErrorCode e)
/*      */             {
/*      */             }
/*      */           });
/*      */         }
/* 5941 */         if (profile.customServiceListener != null) {
/* 5942 */           mHandler.post(new Runnable(profile, csHandShakeResponseMessage)
/*      */           {
/*      */             public void run() {
/* 5945 */               if (this.val$profile.customServiceListener != null)
/* 5946 */                 this.val$profile.customServiceListener.onModeChanged(this.val$csHandShakeResponseMessage.getMode());
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/* 5952 */       else if (csHandShakeResponseMessage.isRequiredChangMode()) {
/* 5953 */         switchToHumanMode(kefuId);
/*      */       } else {
/* 5955 */         mHandler.post(new Runnable(profile, csHandShakeResponseMessage)
/*      */         {
/*      */           public void run() {
/* 5958 */             if (this.val$profile.customServiceListener != null) {
/* 5959 */               this.val$profile.customServiceListener.onModeChanged(this.val$csHandShakeResponseMessage.getMode());
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/* 5965 */       return true;
/* 5966 */     }if (message.getObjectName().equals(csCMRTag.value())) {
/* 5967 */       CSChangeModeResponseMessage csChangeModeResponseMessage = (CSChangeModeResponseMessage)message.getContent();
/* 5968 */       CustomServiceProfile profile = (CustomServiceProfile)this.customServiceCache.get(message.getTargetId());
/* 5969 */       if ((profile != null) && (profile.customServiceListener != null)) {
/* 5970 */         int code = csChangeModeResponseMessage.getResult();
/* 5971 */         if (code == 1) {
/* 5972 */           switch (csChangeModeResponseMessage.getStatus()) {
/*      */           case 1:
/* 5974 */             profile.mode = CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN;
/* 5975 */             mHandler.post(new Runnable(profile)
/*      */             {
/*      */               public void run() {
/* 5978 */                 if (this.val$profile.customServiceListener != null)
/* 5979 */                   this.val$profile.customServiceListener.onModeChanged(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN);
/*      */               }
/*      */             });
/* 5983 */             break;
/*      */           case 2:
/* 5985 */             if (profile.mode == null) break;
/* 5986 */             if (profile.mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN)) {
/* 5987 */               profile.mode = CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE;
/* 5988 */               mHandler.post(new Runnable(profile)
/*      */               {
/*      */                 public void run() {
/* 5991 */                   if (this.val$profile.customServiceListener != null)
/* 5992 */                     this.val$profile.customServiceListener.onModeChanged(CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE);
/*      */                 } } );
/*      */             } else {
/* 5996 */               if (!profile.mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN_FIRST)) break;
/* 5997 */               profile.mode = CustomServiceMode.CUSTOM_SERVICE_MODE_ROBOT_FIRST;
/* 5998 */               mHandler.post(new Runnable(profile)
/*      */               {
/*      */                 public void run() {
/* 6001 */                   if (this.val$profile.customServiceListener != null)
/* 6002 */                     this.val$profile.customServiceListener.onModeChanged(CustomServiceMode.CUSTOM_SERVICE_MODE_ROBOT_FIRST);
/*      */                 }
/*      */               });
/* 6006 */               if (TextUtils.isEmpty(profile.welcome)) break;
/* 6007 */               TextMessage textMessage = TextMessage.obtain(profile.welcome);
/* 6008 */               if (profile.portrait != null) {
/* 6009 */                 textMessage.setUserInfo(new UserInfo(message.getTargetId(), profile.name, Uri.parse(profile.portrait)));
/*      */               }
/* 6011 */               insertMessage(Conversation.ConversationType.CUSTOMER_SERVICE, message.getTargetId(), message.getTargetId(), textMessage, new ResultCallback()
/*      */               {
/*      */                 public void onSuccess(Message message) {
/* 6014 */                   if (RongIMClient.sReceiveMessageListener != null)
/* 6015 */                     RongIMClient.sReceiveMessageListener.onReceived(message, 0);
/*      */                 }
/*      */ 
/*      */                 public void onError(RongIMClient.ErrorCode e) {
/*      */                 }
/*      */               });
/*      */             }
/* 6023 */             break;
/*      */           case 3:
/* 6028 */             mHandler.post(new Runnable(csChangeModeResponseMessage, profile)
/*      */             {
/*      */               public void run() {
/* 6031 */                 String msg = this.val$csChangeModeResponseMessage.getErrMsg();
/* 6032 */                 if (this.val$profile.customServiceListener != null) {
/* 6033 */                   this.val$profile.customServiceListener.onError(3, msg);
/*      */                 }
/*      */               }
/*      */             });
/*      */           }
/*      */         }
/*      */       }
/* 6041 */       return true;
/* 6042 */     }if (message.getObjectName().equals(csTTag.value())) {
/* 6043 */       CSTerminateMessage csTerminateMessage = (CSTerminateMessage)message.getContent();
/* 6044 */       CustomServiceProfile profile = (CustomServiceProfile)this.customServiceCache.get(message.getTargetId());
/* 6045 */       if ((profile.customServiceListener != null) && (profile != null) && (csTerminateMessage.getsid().equals(profile.sid)))
/*      */       {
/* 6047 */         if (csTerminateMessage.getCode() == 0) {
/* 6048 */           ??? = csTerminateMessage.getMsg();
/* 6049 */           ??? = this.mContext.getResources().getString(this.mContext.getResources().getIdentifier("rc_quit_custom_service", "string", this.mContext.getPackageName()));
/* 6050 */           mHandler.post(new Runnable(profile, ???, ???)
/*      */           {
/*      */             public void run() {
/* 6053 */               if (this.val$profile.customServiceListener != null)
/* 6054 */                 this.val$profile.customServiceListener.onQuit(TextUtils.isEmpty(this.val$msg) ? this.val$strQuit : this.val$msg);
/*      */             } } );
/*      */         }
/*      */         else {
/* 6059 */           profile.mode = CustomServiceMode.CUSTOM_SERVICE_MODE_ROBOT_FIRST;
/* 6060 */           mHandler.post(new Runnable(profile)
/*      */           {
/*      */             public void run() {
/* 6063 */               if (this.val$profile.customServiceListener != null)
/* 6064 */                 this.val$profile.customServiceListener.onModeChanged(CustomServiceMode.CUSTOM_SERVICE_MODE_ROBOT_FIRST);
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/* 6070 */       return true;
/* 6071 */     }if (message.getObjectName().equals(csUpdateTag.value())) {
/* 6072 */       CSUpdateMessage csUpdateMessage = (CSUpdateMessage)message.getContent();
/* 6073 */       CustomServiceProfile profile = (CustomServiceProfile)this.customServiceCache.get(message.getTargetId());
/* 6074 */       if (profile != null) {
/* 6075 */         profile.sid = csUpdateMessage.getSid();
/* 6076 */         switch (csUpdateMessage.getServiceStatus()) {
/*      */         case "1":
/* 6078 */           profile.mode = CustomServiceMode.CUSTOM_SERVICE_MODE_ROBOT;
/* 6079 */           mHandler.post(new Runnable(profile)
/*      */           {
/*      */             public void run() {
/* 6082 */               if (this.val$profile.customServiceListener != null)
/* 6083 */                 this.val$profile.customServiceListener.onModeChanged(CustomServiceMode.CUSTOM_SERVICE_MODE_ROBOT);
/*      */             }
/*      */           });
/* 6086 */           break;
/*      */         case "2":
/* 6088 */           profile.mode = CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN;
/* 6089 */           mHandler.post(new Runnable(profile)
/*      */           {
/*      */             public void run() {
/* 6092 */               if (this.val$profile.customServiceListener != null)
/* 6093 */                 this.val$profile.customServiceListener.onModeChanged(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN);
/*      */             }
/*      */           });
/* 6096 */           break;
/*      */         case "3":
/* 6098 */           profile.mode = CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE;
/* 6099 */           mHandler.post(new Runnable(profile)
/*      */           {
/*      */             public void run() {
/* 6102 */               if (this.val$profile.customServiceListener != null)
/* 6103 */                 this.val$profile.customServiceListener.onModeChanged(CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE);
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/* 6109 */       return true;
/* 6110 */     }if (message.getObjectName().equals(csPullEva.value())) {
/* 6111 */       CSPullEvaluateMessage csPullEvaluateMessage = (CSPullEvaluateMessage)message.getContent();
/* 6112 */       CustomServiceProfile profile = (CustomServiceProfile)this.customServiceCache.get(message.getTargetId());
/* 6113 */       profile.sid = csPullEvaluateMessage.getMsgId();
/* 6114 */       mHandler.post(new Runnable(profile, csPullEvaluateMessage)
/*      */       {
/*      */         public void run() {
/* 6117 */           if (this.val$profile.customServiceListener != null)
/* 6118 */             this.val$profile.customServiceListener.onPullEvaluation(this.val$csPullEvaluateMessage.getMsgId());
/*      */         }
/*      */       });
/* 6121 */       return true;
/*      */     }
/* 6123 */     return this.mCmdObjectNameList.contains(message.getObjectName());
/*      */   }
/*      */ 
/*      */   private void initMessageReceiver() {
/* 6127 */     RLog.i("RongIMClient", "initMessageReceiver");
/*      */     try {
/* 6129 */       this.mLibHandler.setOnReceiveMessageListener(new OnReceiveMessageListener.Stub()
/*      */       {
/*      */         public boolean onReceived(Message message, int left, boolean offline, int cmdLeft) throws RemoteException {
/* 6132 */           RLog.d("RongIMClient", "initMessageReceiver : setOnReceiveMessageListener onReceived");
/* 6133 */           if ((!RongIMClient.this.handleCmdMessages(message, left, offline, cmdLeft)) && (RongIMClient.sReceiveMessageListener != null))
/* 6134 */             RongIMClient.this.runOnUiThread(new Runnable(message, left, cmdLeft)
/*      */             {
/*      */               public void run() {
/* 6137 */                 RongIMClient.sReceiveMessageListener.onReceived(this.val$message, this.val$left - this.val$cmdLeft);
/*      */               }
/*      */             });
/* 6141 */           return false;
/*      */         } } );
/*      */     } catch (RemoteException e) {
/* 6145 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void getMessageByUid(String uid, ResultCallback callback)
/*      */   {
/* 6156 */     if (TextUtils.isEmpty(uid)) {
/* 6157 */       RLog.e("RongIMClient", "getMessageByUid uid is empty!");
/* 6158 */       return;
/*      */     }
/*      */ 
/* 6161 */     this.mWorkHandler.post(new Runnable(callback, uid)
/*      */     {
/*      */       public void run() {
/* 6164 */         if (RongIMClient.this.mLibHandler == null) {
/* 6165 */           if (this.val$callback != null)
/* 6166 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 6167 */           return;
/*      */         }
/*      */         try {
/* 6170 */           if (this.val$callback != null) {
/* 6171 */             Message message = RongIMClient.this.mLibHandler.getMessageByUid(this.val$uid);
/* 6172 */             this.val$callback.onCallback(message);
/*      */           }
/*      */         } catch (RemoteException e) {
/* 6175 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void recordNotificationEvent(String pushId)
/*      */   {
/* 6193 */     RongPushClient.recordNotificationEvent(pushId);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public void clearNotifications()
/*      */   {
/* 6203 */     RongPushClient.clearAllNotifications(this.mContext);
/*      */   }
/*      */ 
/*      */   public RealTimeLocationConstant.RealTimeLocationErrorCode getRealTimeLocation(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 7297 */     if (this.mLibHandler == null) {
/* 7298 */       RLog.e("RongIMClient", "getRealTimeLocation IPC 进程尚未运行。");
/* 7299 */       return RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_NOT_INIT;
/*      */     }
/*      */ 
/* 7302 */     if ((conversationType == null) || (targetId == null)) {
/* 7303 */       RLog.e("RongIMClient", "getRealTimeLocation Type or id is null!");
/* 7304 */       return null;
/*      */     }
/*      */ 
/* 7307 */     int code = -1;
/*      */     try {
/* 7309 */       code = this.mLibHandler.setupRealTimeLocation(conversationType.getValue(), targetId);
/*      */     } catch (RemoteException e) {
/* 7311 */       e.printStackTrace();
/*      */     }
/* 7313 */     return RealTimeLocationConstant.RealTimeLocationErrorCode.valueOf(code);
/*      */   }
/*      */ 
/*      */   public RealTimeLocationConstant.RealTimeLocationErrorCode startRealTimeLocation(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 7324 */     if (this.mLibHandler == null) {
/* 7325 */       RLog.e("RongIMClient", "startRealTimeLocation IPC 进程尚未运行。");
/* 7326 */       return RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_NOT_INIT;
/*      */     }
/*      */ 
/* 7329 */     if ((conversationType == null) || (targetId == null)) {
/* 7330 */       RLog.e("RongIMClient", "startRealTimeLocation Type or id is null!");
/* 7331 */       return null;
/*      */     }
/*      */ 
/* 7334 */     int code = -1;
/*      */     try {
/* 7336 */       code = this.mLibHandler.startRealTimeLocation(conversationType.getValue(), targetId);
/*      */     } catch (RemoteException e) {
/* 7338 */       e.printStackTrace();
/*      */     }
/* 7340 */     return RealTimeLocationConstant.RealTimeLocationErrorCode.valueOf(code);
/*      */   }
/*      */ 
/*      */   public RealTimeLocationConstant.RealTimeLocationErrorCode joinRealTimeLocation(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 7351 */     if (this.mLibHandler == null) {
/* 7352 */       RLog.e("RongIMClient", "joinRealTimeLocation IPC 进程尚未运行。");
/* 7353 */       return RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_NOT_INIT;
/*      */     }
/*      */ 
/* 7356 */     if ((conversationType == null) || (targetId == null)) {
/* 7357 */       RLog.e("RongIMClient", "joinRealTimeLocation Type or id is null!");
/* 7358 */       return null;
/*      */     }
/*      */ 
/* 7361 */     int code = -1;
/*      */     try {
/* 7363 */       code = this.mLibHandler.joinRealTimeLocation(conversationType.getValue(), targetId);
/*      */     } catch (RemoteException e) {
/* 7365 */       e.printStackTrace();
/*      */     }
/* 7367 */     return RealTimeLocationConstant.RealTimeLocationErrorCode.valueOf(code);
/*      */   }
/*      */ 
/*      */   public void quitRealTimeLocation(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 7377 */     if ((conversationType == null) || (targetId == null)) {
/* 7378 */       RLog.e("RongIMClient", "quitRealTimeLocation Type or id is null!");
/* 7379 */       return;
/*      */     }
/* 7381 */     if (this.mLibHandler == null) {
/* 7382 */       RLog.e("RongIMClient", "quitRealTimeLocation IPC 进程尚未运行。");
/* 7383 */       return;
/*      */     }
/* 7385 */     this.mWorkHandler.post(new Runnable(conversationType, targetId)
/*      */     {
/*      */       public void run() {
/*      */         try {
/* 7389 */           RongIMClient.this.mLibHandler.quitRealTimeLocation(this.val$conversationType.getValue(), this.val$targetId);
/*      */         } catch (RemoteException e) {
/* 7391 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public List<String> getRealTimeLocationParticipants(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 7405 */     if (this.mLibHandler == null) {
/* 7406 */       RLog.e("RongIMClient", "getRealTimeLocationParticipants IPC 进程尚未运行。");
/* 7407 */       return null;
/*      */     }
/*      */ 
/* 7410 */     if ((conversationType == null) || (targetId == null)) {
/* 7411 */       RLog.e("RongIMClient", "getRealTimeLocationParticipants Type or id is null!");
/* 7412 */       return null;
/*      */     }
/*      */ 
/* 7415 */     List list = null;
/*      */     try {
/* 7417 */       list = this.mLibHandler.getRealTimeLocationParticipants(conversationType.getValue(), targetId);
/*      */     } catch (RemoteException e) {
/* 7419 */       e.printStackTrace();
/*      */     }
/* 7421 */     return list;
/*      */   }
/*      */ 
/*      */   public RealTimeLocationConstant.RealTimeLocationStatus getRealTimeLocationCurrentState(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 7433 */     if (this.mLibHandler == null) {
/* 7434 */       RLog.e("RongIMClient", "getRealTimeLocationCurrentState IPC 进程尚未运行。");
/* 7435 */       return RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_IDLE;
/*      */     }
/*      */ 
/* 7438 */     if ((conversationType == null) || (targetId == null)) {
/* 7439 */       RLog.e("RongIMClient", "getRealTimeLocationCurrentState Type or id is null!");
/* 7440 */       return RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_IDLE;
/*      */     }
/*      */ 
/* 7443 */     int state = 0;
/*      */     try {
/* 7445 */       state = this.mLibHandler.getRealTimeLocationCurrentState(conversationType.getValue(), targetId);
/*      */     } catch (RemoteException e) {
/* 7447 */       e.printStackTrace();
/*      */     }
/*      */ 
/* 7450 */     return RealTimeLocationConstant.RealTimeLocationStatus.valueOf(state);
/*      */   }
/*      */ 
/*      */   public void addRealTimeLocationListener(Conversation.ConversationType conversationType, String targetId, RealTimeLocationListener listener)
/*      */   {
/* 7461 */     if ((conversationType == null) || (targetId == null)) {
/* 7462 */       RLog.e("RongIMClient", "addRealTimeLocationListener Type or id is null!");
/* 7463 */       return;
/*      */     }
/* 7465 */     this.mWorkHandler.post(new Runnable(conversationType, targetId, listener)
/*      */     {
/*      */       public void run() {
/* 7468 */         if (RongIMClient.this.mLibHandler == null)
/* 7469 */           return;
/*      */         try
/*      */         {
/* 7472 */           RongIMClient.this.mLibHandler.addRealTimeLocationListener(this.val$conversationType.getValue(), this.val$targetId, new IRealTimeLocationListener.Stub()
/*      */           {
/*      */             public void onStatusChange(int status) {
/* 7475 */               if (RongIMClient.124.this.val$listener != null)
/* 7476 */                 RongIMClient.mHandler.post(new Runnable(status)
/*      */                 {
/*      */                   public void run() {
/* 7479 */                     RongIMClient.124.this.val$listener.onStatusChange(RealTimeLocationConstant.RealTimeLocationStatus.valueOf(this.val$status));
/*      */                   }
/*      */                 });
/*      */             }
/*      */ 
/*      */             public void onReceiveLocation(double latitude, double longitude, String userId)
/*      */             {
/* 7487 */               if (RongIMClient.124.this.val$listener != null)
/* 7488 */                 RongIMClient.mHandler.post(new Runnable(latitude, longitude, userId)
/*      */                 {
/*      */                   public void run() {
/* 7491 */                     RongIMClient.124.this.val$listener.onReceiveLocation(this.val$latitude, this.val$longitude, this.val$userId);
/*      */                   }
/*      */                 });
/*      */             }
/*      */ 
/*      */             public void onParticipantsJoin(String userId)
/*      */             {
/* 7499 */               if (RongIMClient.124.this.val$listener != null)
/* 7500 */                 RongIMClient.mHandler.post(new Runnable(userId)
/*      */                 {
/*      */                   public void run() {
/* 7503 */                     RongIMClient.124.this.val$listener.onParticipantsJoin(this.val$userId);
/*      */                   }
/*      */                 });
/*      */             }
/*      */ 
/*      */             public void onParticipantsQuit(String userId)
/*      */             {
/* 7511 */               if (RongIMClient.124.this.val$listener != null)
/* 7512 */                 RongIMClient.mHandler.post(new Runnable(userId)
/*      */                 {
/*      */                   public void run() {
/* 7515 */                     RongIMClient.124.this.val$listener.onParticipantsQuit(this.val$userId);
/*      */                   }
/*      */                 });
/*      */             }
/*      */ 
/*      */             public void onError(int errorCode)
/*      */             {
/* 7523 */               if (RongIMClient.124.this.val$listener != null)
/* 7524 */                 RongIMClient.mHandler.post(new Runnable(errorCode)
/*      */                 {
/*      */                   public void run() {
/* 7527 */                     RongIMClient.124.this.val$listener.onError(RealTimeLocationConstant.RealTimeLocationErrorCode.valueOf(this.val$errorCode));
/*      */                   } } );
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 7534 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void updateRealTimeLocationStatus(Conversation.ConversationType conversationType, String targetId, double latitude, double longitude)
/*      */   {
/* 7550 */     RLog.d("RongIMClient", "updateRealTimeLocationStatus latitude=" + latitude);
/* 7551 */     if ((conversationType == null) || (targetId == null)) {
/* 7552 */       RLog.e("RongIMClient", "updateRealTimeLocationStatus Type or id is null!");
/* 7553 */       return;
/*      */     }
/*      */ 
/* 7556 */     if (this.mLibHandler != null)
/*      */       try {
/* 7558 */         this.mLibHandler.updateRealTimeLocationStatus(conversationType.getValue(), targetId, latitude, longitude);
/*      */       } catch (RemoteException e) {
/* 7560 */         e.printStackTrace();
/*      */       }
/*      */   }
/*      */ 
/*      */   public Collection<TypingStatus> getTypingUserListFromConversation(Conversation.ConversationType conversationType, String targetId)
/*      */   {
/* 7615 */     return TypingMessageManager.getInstance().getTypingUserListFromConversation(conversationType, targetId);
/*      */   }
/*      */ 
/*      */   public void sendTypingStatus(Conversation.ConversationType conversationType, String targetId, String typingContentType)
/*      */   {
/* 7629 */     TypingMessageManager.getInstance().sendTypingMessage(conversationType, targetId, typingContentType);
/*      */   }
/*      */ 
/*      */   public static void setTypingStatusListener(TypingStatusListener listener)
/*      */   {
/* 7654 */     TypingMessageManager.getInstance().setTypingMessageStatusListener(listener);
/*      */   }
/*      */ 
/*      */   public void sendReadReceiptMessage(Conversation.ConversationType conversationType, String targetId, long timestamp)
/*      */   {
/* 7666 */     ReadReceiptMessage readRecMsg = new ReadReceiptMessage(timestamp);
/* 7667 */     sendMessage(conversationType, targetId, readRecMsg, null, null, null, null);
/*      */   }
/*      */ 
/*      */   public static void setReadReceiptListener(ReadReceiptListener listener)
/*      */   {
/* 7708 */     sReadReceiptListener = listener;
/*      */   }
/*      */ 
/*      */   public void startCustomService(String kefuId, ICustomServiceListener listener, CSCustomServiceInfo customServiceInfo)
/*      */   {
/* 7736 */     if (TextUtils.isEmpty(kefuId)) {
/* 7737 */       RLog.e("RongIMClient", "startCustomService kefuId should not be null!");
/* 7738 */       return;
/*      */     }
/*      */ 
/* 7741 */     CustomServiceProfile profile = null;
/* 7742 */     if (this.customServiceCache != null) {
/* 7743 */       profile = (CustomServiceProfile)this.customServiceCache.get(kefuId);
/* 7744 */       if (profile != null) {
/* 7745 */         profile.customServiceListener = null;
/* 7746 */         this.customServiceCache.remove(kefuId);
/*      */       }
/*      */     }
/* 7749 */     if (customServiceInfo == null) {
/* 7750 */       CSCustomServiceInfo.Builder csBuilder = new CSCustomServiceInfo.Builder();
/* 7751 */       customServiceInfo = csBuilder.build();
/*      */     }
/* 7753 */     CSHandShakeMessage csHandShakeMessage = new CSHandShakeMessage();
/* 7754 */     csHandShakeMessage.setCustomInfo(customServiceInfo);
/* 7755 */     CustomServiceProfile profileNew = new CustomServiceProfile(null);
/* 7756 */     profileNew.customServiceListener = listener;
/* 7757 */     if (this.customServiceCache != null) {
/* 7758 */       this.customServiceCache.put(kefuId, profileNew);
/*      */     }
/*      */ 
/* 7761 */     sendMessage(Conversation.ConversationType.CUSTOMER_SERVICE, kefuId, csHandShakeMessage, null, null, new SendMessageCallback(profileNew)
/*      */     {
/*      */       public void onError(Integer messageId, RongIMClient.ErrorCode e)
/*      */       {
/* 7765 */         if (this.val$profileNew.customServiceListener != null) {
/* 7766 */           String strFail = RongIMClient.this.mContext.getResources().getString(RongIMClient.this.mContext.getResources().getIdentifier("rc_init_failed", "string", RongIMClient.this.mContext.getPackageName()));
/* 7767 */           this.val$profileNew.customServiceListener.onError(e.getValue(), strFail);
/* 7768 */           this.val$profileNew.customServiceListener = null;
/*      */         }
/*      */       }
/*      */ 
/*      */       public void onSuccess(Integer integer)
/*      */       {
/*      */       }
/*      */     }
/*      */     , null);
/*      */   }
/*      */ 
/*      */   public void selectCustomServiceGroup(String kefuId, String groupId)
/*      */   {
/* 7785 */     sendChangeModelMessage(kefuId, groupId);
/*      */   }
/*      */ 
/*      */   private void sendChangeModelMessage(String kefuId, String groupId)
/*      */   {
/* 7796 */     CustomServiceProfile profile = (CustomServiceProfile)this.customServiceCache.get(kefuId);
/* 7797 */     CSChangeModeMessage changeModeMessage = CSChangeModeMessage.obtain(profile.sid, profile.uid, profile.pid, groupId);
/* 7798 */     sendMessage(Conversation.ConversationType.CUSTOMER_SERVICE, kefuId, changeModeMessage, null, null, new SendMessageCallback(kefuId)
/*      */     {
/*      */       public void onError(Integer messageId, RongIMClient.ErrorCode e)
/*      */       {
/* 7802 */         InformationNotificationMessage informationNotificationMessage = InformationNotificationMessage.obtain("无人工在线");
/* 7803 */         RongIMClient.this.insertMessage(Conversation.ConversationType.CUSTOMER_SERVICE, this.val$kefuId, "rong", informationNotificationMessage, null);
/*      */       }
/*      */ 
/*      */       public void onSuccess(Integer integer)
/*      */       {
/*      */       }
/*      */     }
/*      */     , null);
/*      */   }
/*      */ 
/*      */   public void switchToHumanMode(String kefuId)
/*      */   {
/* 7823 */     if (TextUtils.isEmpty(kefuId)) {
/* 7824 */       RLog.e("RongIMClient", "switchToHumanMode kefuId should not be null!");
/* 7825 */       return;
/*      */     }
/* 7827 */     if (!this.customServiceCache.containsKey(kefuId)) {
/* 7828 */       RLog.e("RongIMClient", "switchToHumanMode " + kefuId + " is not started yet!");
/* 7829 */       return;
/*      */     }
/* 7831 */     CustomServiceProfile profile = (CustomServiceProfile)this.customServiceCache.get(kefuId);
/* 7832 */     if ((profile.groupList != null) && (profile.groupList.size() > 0))
/* 7833 */       mHandler.post(new Runnable(profile)
/*      */       {
/*      */         public void run() {
/* 7836 */           if ((this.val$profile.customServiceListener != null) && (this.val$profile.groupList != null) && (this.val$profile.groupList.size() > 0))
/* 7837 */             this.val$profile.customServiceListener.onSelectGroup(this.val$profile.groupList);
/*      */         }
/*      */       });
/*      */     else
/* 7842 */       sendChangeModelMessage(kefuId, null);
/*      */   }
/*      */ 
/*      */   public void evaluateCustomService(String kefuId, boolean isRobotResolved, String knowledgeId)
/*      */   {
/* 7860 */     if (TextUtils.isEmpty(kefuId)) {
/* 7861 */       RLog.e("RongIMClient", "evaluateCustomService kefuId should not be null!");
/* 7862 */       return;
/*      */     }
/* 7864 */     if (!this.customServiceCache.containsKey(kefuId)) {
/* 7865 */       RLog.e("RongIMClient", "evaluateCustomService " + kefuId + " is not started yet!");
/* 7866 */       return;
/*      */     }
/* 7868 */     CustomServiceProfile profile = (CustomServiceProfile)this.customServiceCache.get(kefuId);
/* 7869 */     CSEvaluateMessage.Builder csBuilder = new CSEvaluateMessage.Builder();
/* 7870 */     CSEvaluateMessage csEvaluateMessage = csBuilder.sid(TextUtils.isEmpty(knowledgeId) ? profile.sid : knowledgeId).pid(profile.pid).uid(profile.uid).type(0).isRobotResolved(isRobotResolved).build();
/*      */ 
/* 7876 */     sendMessage(Conversation.ConversationType.CUSTOMER_SERVICE, kefuId, csEvaluateMessage, null, null, null, null);
/*      */   }
/*      */ 
/*      */   public void evaluateCustomService(String kefuId, int source, String suggest, String dialogId)
/*      */   {
/* 7890 */     if (TextUtils.isEmpty(kefuId)) {
/* 7891 */       RLog.e("RongIMClient", "evaluateCustomService kefuId should not be null!");
/* 7892 */       return;
/*      */     }
/* 7894 */     if (!this.customServiceCache.containsKey(kefuId)) {
/* 7895 */       RLog.e("RongIMClient", "evaluateCustomService " + kefuId + " is not started yet!");
/* 7896 */       return;
/*      */     }
/* 7898 */     CustomServiceProfile profile = (CustomServiceProfile)this.customServiceCache.get(kefuId);
/* 7899 */     CSEvaluateMessage.Builder csBuilder = new CSEvaluateMessage.Builder();
/* 7900 */     CSEvaluateMessage csEvaluateMessage = csBuilder.sid(TextUtils.isEmpty(dialogId) ? profile.sid : dialogId).pid(profile.pid).uid(profile.uid).source(source).suggest(suggest).type(1).build();
/*      */ 
/* 7907 */     sendMessage(Conversation.ConversationType.CUSTOMER_SERVICE, kefuId, csEvaluateMessage, null, null, null, null);
/*      */   }
/*      */ 
/*      */   public void stopCustomService(String kefuId)
/*      */   {
/* 7916 */     if (TextUtils.isEmpty(kefuId)) {
/* 7917 */       RLog.e("RongIMClient", "stopCustomService kefuId should not be null!");
/* 7918 */       return;
/*      */     }
/* 7920 */     if (!this.customServiceCache.containsKey(kefuId)) {
/* 7921 */       RLog.e("RongIMClient", "stopCustomService " + kefuId + " is not started yet!");
/* 7922 */       return;
/*      */     }
/* 7924 */     CustomServiceProfile profile = (CustomServiceProfile)this.customServiceCache.get(kefuId);
/* 7925 */     CSSuspendMessage csSuspendMessage = CSSuspendMessage.obtain(profile.sid, profile.uid, profile.pid);
/* 7926 */     sendMessage(Conversation.ConversationType.CUSTOMER_SERVICE, kefuId, csSuspendMessage, null, null, null, null);
/* 7927 */     profile.customServiceListener = null;
/* 7928 */     this.customServiceCache.remove(kefuId);
/*      */   }
/*      */ 
/*      */   public static void setServerInfo(String naviServer, String fileServer)
/*      */   {
/* 7943 */     if (TextUtils.isEmpty(naviServer)) {
/* 7944 */       RLog.e("RongIMClient", "setServerInfo naviServer should not be null.");
/* 7945 */       throw new IllegalArgumentException("naviServer should not be null.");
/*      */     }
/* 7947 */     mNaviServer = naviServer;
/* 7948 */     mFileServer = fileServer;
/*      */   }
/*      */ 
/*      */   public void recallMessage(Message message, ResultCallback<RecallNotificationMessage> callback)
/*      */   {
/* 7959 */     if (this.mLibHandler == null) {
/* 7960 */       RLog.e("RongIMClient", "recallMessage IPC 进程尚未运行。");
/* 7961 */       if (callback != null) {
/* 7962 */         callback.onError(ErrorCode.IPC_DISCONNECT);
/*      */       }
/* 7964 */       return;
/*      */     }
/*      */ 
/* 7967 */     RecallCommandMessage recallCommandMessage = new RecallCommandMessage(message.getUId());
/* 7968 */     sendMessage(message.getConversationType(), message.getTargetId(), recallCommandMessage, null, null, new IRongCallback.ISendMessageCallback(message, callback)
/*      */     {
/*      */       public void onAttached(Message msg)
/*      */       {
/*      */       }
/*      */ 
/*      */       public void onSuccess(Message msg) {
/* 7975 */         RecallNotificationMessage recallNotificationMessage = new RecallNotificationMessage(this.val$message.getSenderUserId(), msg.getSentTime(), this.val$message.getObjectName());
/* 7976 */         byte[] data = recallNotificationMessage.encode();
/* 7977 */         MessageTag recallNotificationTag = (MessageTag)RecallNotificationMessage.class.getAnnotation(MessageTag.class);
/*      */         try {
/* 7979 */           RongIMClient.this.mLibHandler.setMessageContent(this.val$message.getMessageId(), data, recallNotificationTag.value());
/*      */         } catch (RemoteException e) {
/* 7981 */           e.printStackTrace();
/*      */ 
/* 7983 */           if (this.val$callback != null) {
/* 7984 */             this.val$callback.onError(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */           }
/*      */         }
/* 7987 */         if (this.val$callback != null)
/* 7988 */           this.val$callback.onSuccess(recallNotificationMessage);
/*      */       }
/*      */ 
/*      */       public void onError(Message msg, RongIMClient.ErrorCode errorCode)
/*      */       {
/* 7994 */         if (this.val$callback != null)
/* 7995 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void getUnreadMentionedMessages(Conversation.ConversationType conversationType, String targetId, ResultCallback<List<Message>> callback)
/*      */   {
/* 8009 */     if ((TextUtils.isEmpty(targetId)) || (conversationType == null)) {
/* 8010 */       RLog.e("RongIMClient", "the parameter of targetId or ConversationType is error!");
/* 8011 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 8012 */       return;
/*      */     }
/*      */ 
/* 8015 */     this.mWorkHandler.post(new Runnable(callback, conversationType, targetId)
/*      */     {
/*      */       public void run() {
/* 8018 */         if (RongIMClient.this.mLibHandler == null) {
/* 8019 */           if (this.val$callback != null)
/* 8020 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/* 8021 */           return;
/*      */         }
/*      */         try
/*      */         {
/* 8025 */           List messages = RongIMClient.this.mLibHandler.getUnreadMentionedMessages(this.val$conversationType.getValue(), this.val$targetId);
/*      */ 
/* 8027 */           if (this.val$callback != null)
/* 8028 */             this.val$callback.onCallback(messages);
/*      */         }
/*      */         catch (RemoteException e)
/*      */         {
/* 8032 */           e.printStackTrace();
/* 8033 */           if (this.val$callback != null)
/* 8034 */             this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public static void setRecallMessageListener(RecallMessageListener listener)
/*      */   {
/* 8060 */     sRecallMessageListener = listener;
/*      */   }
/*      */ 
/*      */   public void sendMediaMessage(Message message, String pushContent, String pushData, IRongCallback.ISendMediaMessageCallback callback)
/*      */   {
/* 8075 */     if ((message == null) || (message.getConversationType() == null) || (TextUtils.isEmpty(message.getTargetId())) || (message.getContent() == null))
/*      */     {
/* 8079 */       RLog.e("RongIMClient", "conversation type or targetId or message content can't be null!");
/* 8080 */       if (callback != null) {
/* 8081 */         callback.onError(message, ErrorCode.PARAMETER_ERROR);
/*      */       }
/* 8083 */       return;
/*      */     }
/* 8085 */     MediaMessageContent mediaMessageContent = (MediaMessageContent)message.getContent();
/* 8086 */     if (mediaMessageContent.getLocalPath() == null) {
/* 8087 */       RLog.e("RongIMClient", "Media file does not exist!");
/* 8088 */       if (callback != null) {
/* 8089 */         callback.onError(message, ErrorCode.PARAMETER_ERROR);
/*      */       }
/* 8091 */       return;
/*      */     }
/* 8093 */     String localPath = mediaMessageContent.getLocalPath().toString();
/* 8094 */     String abPath = localPath.substring(7);
/* 8095 */     if ((!localPath.startsWith("file")) || (!new File(abPath).exists())) {
/* 8096 */       RLog.e("RongIMClient", localPath + " does not exist!");
/* 8097 */       if (callback != null) {
/* 8098 */         callback.onError(message, ErrorCode.PARAMETER_ERROR);
/*      */       }
/* 8100 */       return;
/*      */     }
/*      */ 
/* 8103 */     this.mWorkHandler.post(new Runnable(callback, message, pushContent, pushData)
/*      */     {
/*      */       public void run() {
/* 8106 */         if (RongIMClient.this.mLibHandler == null) {
/* 8107 */           if (this.val$callback != null)
/* 8108 */             RongIMClient.this.runOnUiThread(new Runnable()
/*      */             {
/*      */               public void run() {
/* 8111 */                 RongIMClient.130.this.val$callback.onError(RongIMClient.130.this.val$message, RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */               }
/*      */             });
/* 8115 */           return;
/*      */         }
/*      */         try {
/* 8118 */           RongIMClient.this.mLibHandler.sendMediaMessage(this.val$message, this.val$pushContent, this.val$pushData, new ISendMediaMessageCallback.Stub()
/*      */           {
/*      */             public void onAttached(Message message) throws RemoteException {
/* 8121 */               if (RongIMClient.130.this.val$callback != null)
/* 8122 */                 RongIMClient.this.runOnUiThread(new Runnable(message)
/*      */                 {
/*      */                   public void run() {
/* 8125 */                     RongIMClient.130.this.val$callback.onAttached(this.val$message);
/*      */                   }
/*      */                 });
/*      */             }
/*      */ 
/*      */             public void onProgress(Message message, int progress) throws RemoteException
/*      */             {
/* 8133 */               if (RongIMClient.130.this.val$callback != null)
/* 8134 */                 RongIMClient.this.runOnUiThread(new Runnable(message, progress)
/*      */                 {
/*      */                   public void run() {
/* 8137 */                     RongIMClient.130.this.val$callback.onProgress(this.val$message, this.val$progress);
/*      */                   }
/*      */                 });
/*      */             }
/*      */ 
/*      */             public void onSuccess(Message message) throws RemoteException
/*      */             {
/* 8145 */               if (RongIMClient.130.this.val$callback != null)
/* 8146 */                 RongIMClient.this.runOnUiThread(new Runnable(message)
/*      */                 {
/*      */                   public void run() {
/* 8149 */                     RongIMClient.130.this.val$callback.onSuccess(this.val$message);
/*      */                   }
/*      */                 });
/*      */             }
/*      */ 
/*      */             public void onError(Message message, int errorCode) throws RemoteException
/*      */             {
/* 8157 */               if (RongIMClient.130.this.val$callback != null)
/* 8158 */                 RongIMClient.this.runOnUiThread(new Runnable(message, errorCode)
/*      */                 {
/*      */                   public void run() {
/* 8161 */                     RongIMClient.130.this.val$callback.onError(this.val$message, RongIMClient.ErrorCode.valueOf(this.val$errorCode));
/*      */                   } } );
/*      */             } } );
/*      */         }
/*      */         catch (RemoteException e) {
/* 8168 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void sendMediaMessage(Message message, String pushContent, String pushData, IRongCallback.ISendMediaMessageCallbackWithUploader callback)
/*      */   {
/* 8192 */     if ((message == null) || (message.getConversationType() == null) || (TextUtils.isEmpty(message.getTargetId())) || (message.getContent() == null))
/*      */     {
/* 8196 */       RLog.e("RongIMClient", "conversation type or targetId or message content can't be null!");
/* 8197 */       if (callback != null) {
/* 8198 */         callback.onError(message, ErrorCode.PARAMETER_ERROR);
/*      */       }
/* 8200 */       return;
/*      */     }
/* 8202 */     MediaMessageContent mediaMessageContent = (MediaMessageContent)message.getContent();
/* 8203 */     if (mediaMessageContent.getLocalPath() == null) {
/* 8204 */       RLog.e("RongIMClient", "Media file does not exist!");
/* 8205 */       if (callback != null) {
/* 8206 */         callback.onError(message, ErrorCode.PARAMETER_ERROR);
/*      */       }
/* 8208 */       return;
/*      */     }
/* 8210 */     String localPath = mediaMessageContent.getLocalPath().toString();
/* 8211 */     String abPath = localPath.substring(7);
/* 8212 */     if ((!localPath.startsWith("file")) || (!new File(abPath).exists())) {
/* 8213 */       RLog.e("RongIMClient", localPath + " does not exist!");
/* 8214 */       if (callback != null) {
/* 8215 */         callback.onError(message, ErrorCode.PARAMETER_ERROR);
/*      */       }
/* 8217 */       return;
/*      */     }
/*      */ 
/* 8220 */     insertMessage(message.getConversationType(), message.getTargetId(), this.mCurrentUserId, message.getContent(), new ResultCallback(callback, pushContent, pushData, message)
/*      */     {
/*      */       public void onSuccess(Message message) {
/* 8223 */         if (this.val$callback != null)
/* 8224 */           this.val$callback.onAttached(message, new IRongCallback.MediaMessageUploader(message, this.val$pushContent, this.val$pushData, this.val$callback));
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 8230 */         if (this.val$callback != null)
/* 8231 */           this.val$callback.onError(this.val$message, RongIMClient.ErrorCode.RC_MSG_SEND_FAIL);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void sendReadReceiptRequest(Message message, OperationCallback callback)
/*      */   {
/* 8245 */     if (message == null) {
/* 8246 */       if (callback != null) {
/* 8247 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/*      */       }
/* 8249 */       return;
/*      */     }
/* 8251 */     if ((!Conversation.ConversationType.GROUP.equals(message.getConversationType())) && (!Conversation.ConversationType.DISCUSSION.equals(message.getConversationType()))) {
/* 8252 */       RLog.w("RongIMClient", "only group and discussion could send read receipt request.");
/* 8253 */       if (callback != null) {
/* 8254 */         callback.onError(ErrorCode.PARAMETER_ERROR);
/*      */       }
/* 8256 */       return;
/*      */     }
/* 8258 */     ReadReceiptRequestMessage requestMessage = new ReadReceiptRequestMessage(message.getUId());
/* 8259 */     sendMessage(message.getConversationType(), message.getTargetId(), requestMessage, null, null, new IRongCallback.ISendMessageCallback(callback, message)
/*      */     {
/*      */       public void onAttached(Message msg)
/*      */       {
/*      */       }
/*      */ 
/*      */       public void onSuccess(Message msg)
/*      */       {
/* 8267 */         if (RongIMClient.this.mLibHandler == null) {
/* 8268 */           RLog.d("RongIMClient", "sendReadReceiptRequest mLibHandler is null");
/* 8269 */           if (this.val$callback != null) {
/* 8270 */             this.val$callback.onError(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */           }
/* 8272 */           return;
/*      */         }
/*      */         try {
/* 8275 */           ReadReceiptInfo readReceiptInfo = this.val$message.getReadReceiptInfo();
/* 8276 */           if (readReceiptInfo == null) {
/* 8277 */             readReceiptInfo = new ReadReceiptInfo();
/* 8278 */             this.val$message.setReadReceiptInfo(readReceiptInfo);
/*      */           }
/* 8280 */           readReceiptInfo.setIsReadReceiptMessage(true);
/* 8281 */           RongIMClient.this.mLibHandler.updateReadReceiptRequestInfo(this.val$message.getUId(), readReceiptInfo.toJSON().toString());
/*      */         } catch (RemoteException e) {
/* 8283 */           e.printStackTrace();
/* 8284 */           if (this.val$callback != null) {
/* 8285 */             this.val$callback.onError(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */           }
/* 8287 */           return;
/*      */         }
/* 8289 */         if (this.val$callback != null)
/* 8290 */           this.val$callback.onSuccess();
/*      */       }
/*      */ 
/*      */       public void onError(Message msg, RongIMClient.ErrorCode errorCode)
/*      */       {
/* 8296 */         if (this.val$callback != null)
/* 8297 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void sendReadReceiptResponse(Conversation.ConversationType type, String targetId, List<Message> messageList, OperationCallback callback)
/*      */   {
/* 8312 */     if (((!Conversation.ConversationType.GROUP.equals(type)) && (!Conversation.ConversationType.DISCUSSION.equals(type))) || (messageList == null) || (messageList.size() == 0))
/*      */     {
/* 8314 */       callback.onError(ErrorCode.PARAMETER_ERROR);
/* 8315 */       return;
/*      */     }
/* 8317 */     ReadReceiptResponseMessage responseMessage = new ReadReceiptResponseMessage(messageList);
/* 8318 */     sendDirectionalMessage(type, targetId, responseMessage, (String[])responseMessage.getSenderIdSet().toArray(new String[responseMessage.getSenderIdSet().size()]), null, null, new IRongCallback.ISendMessageCallback(callback, messageList)
/*      */     {
/*      */       public void onAttached(Message message)
/*      */       {
/*      */       }
/*      */ 
/*      */       public void onSuccess(Message message)
/*      */       {
/* 8326 */         if (RongIMClient.this.mLibHandler == null) {
/* 8327 */           if (this.val$callback != null) {
/* 8328 */             this.val$callback.onError(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */           }
/* 8330 */           return;
/*      */         }
/* 8332 */         RongIMClient.this.mWorkHandler.post(new Runnable()
/*      */         {
/*      */           public void run() {
/*      */             try {
/* 8336 */               for (Message msg : RongIMClient.133.this.val$messageList) {
/* 8337 */                 ReadReceiptInfo readReceiptInfo = msg.getReadReceiptInfo();
/* 8338 */                 if (readReceiptInfo == null) {
/* 8339 */                   readReceiptInfo = new ReadReceiptInfo();
/* 8340 */                   msg.setReadReceiptInfo(readReceiptInfo);
/*      */                 }
/* 8342 */                 readReceiptInfo.setHasRespond(true);
/* 8343 */                 RongIMClient.this.mLibHandler.updateReadReceiptRequestInfo(msg.getUId(), readReceiptInfo.toJSON().toString());
/*      */               }
/*      */             } catch (RemoteException e) {
/* 8346 */               e.printStackTrace();
/*      */ 
/* 8348 */               if (RongIMClient.133.this.val$callback != null) {
/* 8349 */                 RongIMClient.133.this.val$callback.onFail(RongIMClient.ErrorCode.IPC_DISCONNECT);
/*      */               }
/* 8351 */               return;
/*      */             }
/* 8353 */             if (RongIMClient.133.this.val$callback != null)
/* 8354 */               RongIMClient.133.this.val$callback.onCallback();
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(Message message, RongIMClient.ErrorCode errorCode)
/*      */       {
/* 8362 */         if (this.val$callback != null)
/* 8363 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void syncConversationReadStatus(Conversation.ConversationType type, String targetId, long timestamp, OperationCallback callback)
/*      */   {
/* 8378 */     SyncReadStatusMessage syncReadStatusMessage = new SyncReadStatusMessage(timestamp);
/* 8379 */     String[] users = { getCurrentUserId() };
/* 8380 */     sendDirectionalMessage(type, targetId, syncReadStatusMessage, users, null, null, new IRongCallback.ISendMessageCallback(callback)
/*      */     {
/*      */       public void onAttached(Message message)
/*      */       {
/*      */       }
/*      */ 
/*      */       public void onSuccess(Message message)
/*      */       {
/* 8388 */         if (this.val$callback != null)
/* 8389 */           this.val$callback.onSuccess();
/*      */       }
/*      */ 
/*      */       public void onError(Message message, RongIMClient.ErrorCode errorCode)
/*      */       {
/* 8395 */         if (this.val$callback != null)
/* 8396 */           this.val$callback.onError(errorCode);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setSyncConversationReadStatusListener(SyncConversationReadStatusListener listener)
/*      */   {
/* 8416 */     this.mSyncConversationReadStatusListener = listener;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  142 */     sStateMap.put(Integer.valueOf(ErrorCode.CONNECTED.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED);
/*  143 */     sStateMap.put(Integer.valueOf(ErrorCode.BIZ_ERROR_RECONNECT_SUCCESS.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED);
/*      */ 
/*  146 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_DISCONN_KICK.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT);
/*  147 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_CONN_USER_OR_PASSWD_ERROR.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.TOKEN_INCORRECT);
/*  148 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_CONN_SERVER_UNAVAILABLE.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.SERVER_INVALID);
/*      */ 
/*  154 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_CONN_PROTO_VERSION_ERROR.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*  155 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_CONN_ID_REJECT.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*  156 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_CONN_NOT_AUTHRORIZED.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*  157 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_CONN_REDIRECTED.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*  158 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_CONN_PACKAGE_NAME_INVALID.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*  159 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_CONN_APP_BLOCKED_OR_DELETED.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*  160 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_CONN_USER_BLOCKED.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*  161 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_DISCONN_EXCEPTION.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*  162 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_QUERY_ACK_NO_DATA.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*  163 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_MSG_DATA_INCOMPLETE.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*  164 */     sStateMap.put(Integer.valueOf(ErrorCode.BIZ_ERROR_CLIENT_NOT_INIT.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*  165 */     sStateMap.put(Integer.valueOf(ErrorCode.BIZ_ERROR_DATABASE_ERROR.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*  166 */     sStateMap.put(Integer.valueOf(ErrorCode.BIZ_ERROR_INVALID_PARAMETER.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*  167 */     sStateMap.put(Integer.valueOf(ErrorCode.BIZ_ERROR_NO_CHANNEL.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*  168 */     sStateMap.put(Integer.valueOf(ErrorCode.BIZ_ERROR_CONNECTING.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*      */ 
/*  171 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_NET_CHANNEL_INVALID.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE);
/*  172 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_NET_UNAVAILABLE.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE);
/*  173 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_MSG_RESP_TIMEOUT.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE);
/*  174 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_HTTP_SEND_FAIL.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE);
/*  175 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_HTTP_REQ_TIMEOUT.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE);
/*  176 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_HTTP_RECV_FAIL.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE);
/*  177 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_NAVI_RESOURCE_ERROR.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE);
/*  178 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_NODE_NOT_FOUND.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE);
/*  179 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_DOMAIN_NOT_RESOLVE.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE);
/*  180 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_SOCKET_NOT_CREATED.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE);
/*  181 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_SOCKET_DISCONNECTED.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE);
/*  182 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_PONG_RECV_FAIL.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE);
/*  183 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_CONN_ACK_TIMEOUT.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE);
/*  184 */     sStateMap.put(Integer.valueOf(ErrorCode.RC_CONN_OVERFREQUENCY.getValue()), RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE);
/*      */ 
/*  187 */     reconnectList = new ArrayList();
/*      */ 
/*  190 */     reconnectList.add(Integer.valueOf(ErrorCode.RC_NET_CHANNEL_INVALID.getValue()));
/*  191 */     reconnectList.add(Integer.valueOf(ErrorCode.RC_NET_UNAVAILABLE.getValue()));
/*  192 */     reconnectList.add(Integer.valueOf(ErrorCode.RC_MSG_RESP_TIMEOUT.getValue()));
/*  193 */     reconnectList.add(Integer.valueOf(ErrorCode.RC_SOCKET_NOT_CREATED.getValue()));
/*  194 */     reconnectList.add(Integer.valueOf(ErrorCode.RC_SOCKET_DISCONNECTED.getValue()));
/*  195 */     reconnectList.add(Integer.valueOf(ErrorCode.RC_CONN_SERVER_UNAVAILABLE.getValue()));
/*  196 */     reconnectList.add(Integer.valueOf(ErrorCode.RC_MSG_SEND_FAIL.getValue()));
/*      */   }
/*      */ 
/*      */   public static abstract interface SyncConversationReadStatusListener
/*      */   {
/*      */     public abstract void onSyncConversationReadStatus(Conversation.ConversationType paramConversationType, String paramString);
/*      */   }
/*      */ 
/*      */   public static abstract interface RecallMessageListener
/*      */   {
/*      */     public abstract void onMessageRecalled(int paramInt, RecallNotificationMessage paramRecallNotificationMessage);
/*      */   }
/*      */ 
/*      */   private class CustomServiceProfile
/*      */   {
/*      */     CustomServiceMode mode;
/*      */     String pid;
/*      */     String uid;
/*      */     String sid;
/*      */     String welcome;
/*      */     String name;
/*      */     String portrait;
/*      */     ArrayList<CSGroupItem> groupList;
/*      */     ICustomServiceListener customServiceListener;
/*      */ 
/*      */     private CustomServiceProfile()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface ReadReceiptListener
/*      */   {
/*      */     public abstract void onReadReceiptReceived(Message paramMessage);
/*      */ 
/*      */     public abstract void onMessageReceiptRequest(Conversation.ConversationType paramConversationType, String paramString1, String paramString2);
/*      */ 
/*      */     public abstract void onMessageReceiptResponse(Conversation.ConversationType paramConversationType, String paramString1, String paramString2, HashMap<String, Long> paramHashMap);
/*      */   }
/*      */ 
/*      */   public static abstract interface TypingStatusListener
/*      */   {
/*      */     public abstract void onTypingStatusChanged(Conversation.ConversationType paramConversationType, String paramString, Collection<TypingStatus> paramCollection);
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
/*      */   public static abstract class GetBlacklistCallback extends RongIMClient.ResultCallback<String[]>
/*      */   {
/*      */   }
/*      */ 
/*      */   public static enum SearchType
/*      */   {
/* 7238 */     EXACT(0), 
/*      */ 
/* 7243 */     FUZZY(1);
/*      */ 
/* 7245 */     private int value = 1;
/*      */ 
/*      */     private SearchType(int value)
/*      */     {
/* 7253 */       this.value = value;
/*      */     }
/*      */ 
/*      */     public int getValue()
/*      */     {
/* 7262 */       return this.value;
/*      */     }
/*      */ 
/*      */     public void setValue(int value)
/*      */     {
/* 7271 */       this.value = value;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class GetNotificationQuietHoursCallback extends RongIMClient.ResultCallback<String>
/*      */   {
/*      */     public abstract void onSuccess(String paramString, int paramInt);
/*      */ 
/*      */     public final void onSuccess(String s)
/*      */     {
/* 7210 */       throw new RuntimeException("not support");
/*      */     }
/*      */ 
/*      */     public abstract void onError(RongIMClient.ErrorCode paramErrorCode);
/*      */ 
/*      */     void onCallback(String startTime, int spanMinutes)
/*      */     {
/* 7221 */       RongIMClient.mHandler.post(new Runnable(startTime, spanMinutes)
/*      */       {
/*      */         public void run() {
/* 7224 */           RongIMClient.GetNotificationQuietHoursCallback.this.onSuccess(this.val$startTime, this.val$spanMinutes);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */ 
/*      */   public static enum BlacklistStatus
/*      */   {
/* 7151 */     IN_BLACK_LIST(0), 
/*      */ 
/* 7156 */     NOT_IN_BLACK_LIST(1);
/*      */ 
/* 7159 */     private int value = 1;
/*      */ 
/*      */     private BlacklistStatus(int value)
/*      */     {
/* 7167 */       this.value = value;
/*      */     }
/*      */ 
/*      */     public int getValue()
/*      */     {
/* 7176 */       return this.value;
/*      */     }
/*      */ 
/*      */     public static BlacklistStatus setValue(int code)
/*      */     {
/* 7186 */       for (BlacklistStatus c : values()) {
/* 7187 */         if (code == c.getValue()) {
/* 7188 */           return c;
/*      */         }
/*      */       }
/* 7191 */       return NOT_IN_BLACK_LIST;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static enum MediaType
/*      */   {
/* 7089 */     IMAGE(1), 
/*      */ 
/* 7094 */     AUDIO(2), 
/*      */ 
/* 7099 */     VIDEO(3), 
/*      */ 
/* 7104 */     FILE(4);
/*      */ 
/* 7107 */     private int value = 1;
/*      */ 
/*      */     private MediaType(int value)
/*      */     {
/* 7115 */       this.value = value;
/*      */     }
/*      */ 
/*      */     public int getValue()
/*      */     {
/* 7124 */       return this.value;
/*      */     }
/*      */ 
/*      */     public static MediaType setValue(int code)
/*      */     {
/* 7134 */       for (MediaType c : values()) {
/* 7135 */         if (code == c.getValue()) {
/* 7136 */           return c;
/*      */         }
/*      */       }
/* 7139 */       return IMAGE;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class DownloadMediaCallback extends RongIMClient.ResultCallback<String>
/*      */   {
/*      */     public abstract void onProgress(int paramInt);
/*      */ 
/*      */     void onProgressCallback(int progress)
/*      */     {
/* 7071 */       RongIMClient.mHandler.post(new Runnable(progress)
/*      */       {
/*      */         public void run() {
/* 7074 */           RongIMClient.DownloadMediaCallback.this.onProgress(this.val$progress);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class UploadMediaCallback extends RongIMClient.ResultCallback<Message>
/*      */   {
/*      */     public abstract void onProgress(Message paramMessage, int paramInt);
/*      */ 
/*      */     public abstract void onError(Message paramMessage, RongIMClient.ErrorCode paramErrorCode);
/*      */ 
/*      */     void onProgressCallback(Message message, int progress)
/*      */     {
/* 7034 */       RongIMClient.mHandler.post(new Runnable(message, progress)
/*      */       {
/*      */         public void run() {
/* 7037 */           RongIMClient.UploadMediaCallback.this.onProgress(this.val$message, this.val$progress);
/*      */         } } );
/*      */     }
/*      */ 
/*      */     void onFail(Message message, RongIMClient.ErrorCode code) {
/* 7043 */       RongIMClient.mHandler.postDelayed(new Runnable(message, code)
/*      */       {
/*      */         public void run() {
/* 7046 */           RongIMClient.UploadMediaCallback.this.onError(this.val$message, this.val$code);
/*      */         }
/*      */       }
/*      */       , 100L);
/*      */     }
/*      */ 
/*      */     public void onError(RongIMClient.ErrorCode e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class SendMediaMessageCallback extends RongIMClient.SendImageMessageCallback
/*      */   {
/*      */   }
/*      */ 
/*      */   public static abstract class SendImageMessageCallback extends RongIMClient.SendMessageCallback
/*      */   {
/*      */     public abstract void onAttached(Message paramMessage);
/*      */ 
/*      */     public abstract void onError(Message paramMessage, RongIMClient.ErrorCode paramErrorCode);
/*      */ 
/*      */     public abstract void onSuccess(Message paramMessage);
/*      */ 
/*      */     public abstract void onProgress(Message paramMessage, int paramInt);
/*      */ 
/*      */     void onProgressCallback(Message message, int progress)
/*      */     {
/* 6967 */       RongIMClient.mHandler.post(new Runnable(message, progress)
/*      */       {
/*      */         public void run() {
/* 6970 */           RongIMClient.SendImageMessageCallback.this.onProgress(this.val$message, this.val$progress);
/*      */         } } );
/*      */     }
/*      */ 
/*      */     void onAttachedCallback(Message message) {
/* 6976 */       RongIMClient.mHandler.post(new Runnable(message)
/*      */       {
/*      */         public void run() {
/* 6979 */           RongIMClient.SendImageMessageCallback.this.onAttached(this.val$message);
/*      */         } } );
/*      */     }
/*      */ 
/*      */     void onFail(Message message, RongIMClient.ErrorCode code) {
/* 6985 */       RongIMClient.mHandler.post(new Runnable(message, code)
/*      */       {
/*      */         public void run() {
/* 6988 */           RongIMClient.SendImageMessageCallback.this.onError(this.val$message, this.val$code);
/*      */         }
/*      */       });
/*      */     }
/*      */ 
/*      */     public void onSuccess(Integer integer)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void onError(Integer messageId, RongIMClient.ErrorCode e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class SendImageMessageWithUploadListenerCallback
/*      */   {
/*      */     public abstract void onAttached(Message paramMessage, RongIMClient.UploadImageStatusListener paramUploadImageStatusListener);
/*      */ 
/*      */     public abstract void onError(Message paramMessage, RongIMClient.ErrorCode paramErrorCode);
/*      */ 
/*      */     public abstract void onSuccess(Message paramMessage);
/*      */ 
/*      */     public abstract void onProgress(Message paramMessage, int paramInt);
/*      */ 
/*      */     void onAttachedCallback(Message message, RongIMClient.UploadImageStatusListener watcher)
/*      */     {
/* 6912 */       RongIMClient.mHandler.post(new Runnable(message, watcher)
/*      */       {
/*      */         public void run() {
/* 6915 */           RongIMClient.SendImageMessageWithUploadListenerCallback.this.onAttached(this.val$message, this.val$watcher);
/*      */         } } );
/*      */     }
/*      */ 
/*      */     void onFail(Message message, RongIMClient.ErrorCode code) {
/* 6921 */       RongIMClient.mHandler.post(new Runnable(message, code)
/*      */       {
/*      */         public void run() {
/* 6924 */           RongIMClient.SendImageMessageWithUploadListenerCallback.this.onError(this.val$message, this.val$code);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */ 
/*      */   public class UploadImageStatusListener
/*      */   {
/*      */     private RongIMClient.SendImageMessageWithUploadListenerCallback callback;
/*      */     private Message message;
/*      */     private String pushContent;
/*      */     private String pushData;
/*      */ 
/*      */     public UploadImageStatusListener(Message message, String pushContent, String pushData, RongIMClient.SendImageMessageWithUploadListenerCallback callback)
/*      */     {
/* 6813 */       this.callback = callback;
/* 6814 */       this.message = message;
/* 6815 */       this.pushContent = pushContent;
/* 6816 */       this.pushData = pushData;
/*      */     }
/*      */ 
/*      */     public void update(int progress)
/*      */     {
/* 6825 */       if (this.callback != null)
/* 6826 */         this.callback.onProgress(this.message, progress);
/*      */     }
/*      */ 
/*      */     public void error()
/*      */     {
/* 6833 */       if (this.callback != null)
/* 6834 */         this.callback.onFail(this.message, RongIMClient.ErrorCode.RC_MSG_SEND_FAIL);
/*      */     }
/*      */ 
/*      */     public void success(Uri uploadedUri)
/*      */     {
/* 6843 */       if (uploadedUri == null) {
/* 6844 */         RLog.e("RongIMClient", "UploadImageStatusListener uri is null.");
/* 6845 */         if (this.callback != null)
/* 6846 */           this.callback.onFail(this.message, RongIMClient.ErrorCode.RC_MSG_SEND_FAIL);
/* 6847 */         return;
/*      */       }
/* 6849 */       MessageContent content = this.message.getContent();
/* 6850 */       if ((content instanceof ImageMessage)) {
/* 6851 */         ((ImageMessage)content).setRemoteUri(uploadedUri);
/*      */       }
/* 6853 */       RongIMClient.this.sendMessage(this.message, this.pushContent, this.pushData, new IRongCallback.ISendMessageCallback()
/*      */       {
/*      */         public void onAttached(Message message)
/*      */         {
/*      */         }
/*      */ 
/*      */         public void onSuccess(Message message)
/*      */         {
/* 6861 */           if (RongIMClient.UploadImageStatusListener.this.callback != null)
/* 6862 */             RongIMClient.UploadImageStatusListener.this.callback.onSuccess(message);
/*      */         }
/*      */ 
/*      */         public void onError(Message message, RongIMClient.ErrorCode errorCode)
/*      */         {
/* 6867 */           if (RongIMClient.UploadImageStatusListener.this.callback != null)
/* 6868 */             RongIMClient.UploadImageStatusListener.this.callback.onError(message, errorCode);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class CreateDiscussionCallback extends RongIMClient.ResultCallback<String>
/*      */   {
/*      */   }
/*      */ 
/*      */   public static abstract class SendMessageCallback extends RongIMClient.ResultCallback<Integer>
/*      */   {
/*      */     public final void onError(RongIMClient.ErrorCode e)
/*      */     {
/*      */     }
/*      */ 
/*      */     public final void onFail(int errorCode)
/*      */     {
/* 6761 */       super.onFail(errorCode);
/*      */     }
/*      */ 
/*      */     public final void onFail(RongIMClient.ErrorCode errorCode)
/*      */     {
/* 6766 */       super.onFail(errorCode);
/*      */     }
/*      */ 
/*      */     public abstract void onError(Integer paramInteger, RongIMClient.ErrorCode paramErrorCode);
/*      */ 
/*      */     public final void onFail(Integer messageId, int errorCode)
/*      */     {
/* 6774 */       RongIMClient.mHandler.postDelayed(new Runnable(messageId, errorCode)
/*      */       {
/*      */         public void run() {
/* 6777 */           RongIMClient.SendMessageCallback.this.onError(this.val$messageId, RongIMClient.ErrorCode.valueOf(this.val$errorCode));
/*      */         }
/*      */       }
/*      */       , 100L);
/*      */     }
/*      */ 
/*      */     public final void onFail(Integer messageId, RongIMClient.ErrorCode errorCode)
/*      */     {
/* 6784 */       RongIMClient.mHandler.postDelayed(new Runnable(messageId, errorCode)
/*      */       {
/*      */         public void run() {
/* 6787 */           RongIMClient.SendMessageCallback.this.onError(this.val$messageId, this.val$errorCode);
/*      */         }
/*      */       }
/*      */       , 100L);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface OnReceiveMessageListener
/*      */   {
/*      */     public abstract boolean onReceived(Message paramMessage, int paramInt);
/*      */   }
/*      */ 
/*      */   public static enum DiscussionInviteStatus
/*      */   {
/* 6692 */     CLOSED(1), 
/*      */ 
/* 6697 */     OPENED(0);
/*      */ 
/* 6699 */     private int value = 0;
/*      */ 
/*      */     private DiscussionInviteStatus(int value)
/*      */     {
/* 6707 */       this.value = value;
/*      */     }
/*      */ 
/*      */     public int getValue()
/*      */     {
/* 6716 */       return this.value;
/*      */     }
/*      */ 
/*      */     public static DiscussionInviteStatus setValue(int code)
/*      */     {
/* 6726 */       for (DiscussionInviteStatus c : values()) {
/* 6727 */         if (code == c.getValue()) {
/* 6728 */           return c;
/*      */         }
/*      */       }
/* 6731 */       return OPENED;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface ConnectionStatusListener
/*      */   {
/*      */     public abstract void onChanged(ConnectionStatus paramConnectionStatus);
/*      */ 
/*      */     public static enum ConnectionStatus
/*      */     {
/* 6613 */       NETWORK_UNAVAILABLE(-1, "Network is unavailable."), 
/*      */ 
/* 6617 */       CONNECTED(0, "Connect Success."), 
/*      */ 
/* 6622 */       CONNECTING(1, "Connecting"), 
/*      */ 
/* 6627 */       DISCONNECTED(2, "Disconnected"), 
/*      */ 
/* 6632 */       KICKED_OFFLINE_BY_OTHER_CLIENT(3, "Login on the other device, and be kicked offline."), 
/*      */ 
/* 6637 */       TOKEN_INCORRECT(4, "Token incorrect."), 
/*      */ 
/* 6642 */       SERVER_INVALID(5, "Server invalid.");
/*      */ 
/*      */       private int code;
/*      */       private String msg;
/*      */ 
/*      */       private ConnectionStatus(int code, String msg)
/*      */       {
/* 6654 */         this.code = code;
/* 6655 */         this.msg = msg;
/*      */       }
/*      */ 
/*      */       public int getValue()
/*      */       {
/* 6664 */         return this.code;
/*      */       }
/*      */ 
/*      */       public String getMessage()
/*      */       {
/* 6673 */         return this.msg;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class OperationCallback extends RongIMClient.Callback
/*      */   {
/*      */   }
/*      */ 
/*      */   public static abstract class ConnectCallback extends RongIMClient.ResultCallback<String>
/*      */   {
/*      */     public abstract void onTokenIncorrect();
/*      */   }
/*      */ 
/*      */   static abstract class SyncCallback<T> extends RongIMClient.ResultCallback<T>
/*      */   {
/*      */     public void onFail(int errorCode)
/*      */     {
/* 6572 */       onError(RongIMClient.ErrorCode.valueOf(errorCode));
/*      */     }
/*      */ 
/*      */     public void onFail(RongIMClient.ErrorCode errorCode)
/*      */     {
/* 6577 */       onError(errorCode);
/*      */     }
/*      */ 
/*      */     public void onCallback(T t)
/*      */     {
/* 6582 */       onSuccess(t);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class ResultCallback<T>
/*      */   {
/*      */     public abstract void onSuccess(T paramT);
/*      */ 
/*      */     public abstract void onError(RongIMClient.ErrorCode paramErrorCode);
/*      */ 
/*      */     public void onFail(int errorCode)
/*      */     {
/* 6541 */       RongIMClient.mHandler.post(new Runnable(errorCode)
/*      */       {
/*      */         public void run() {
/* 6544 */           RongIMClient.ResultCallback.this.onError(RongIMClient.ErrorCode.valueOf(this.val$errorCode));
/*      */         } } );
/*      */     }
/*      */ 
/*      */     public void onFail(RongIMClient.ErrorCode errorCode) {
/* 6550 */       RongIMClient.mHandler.post(new Runnable(errorCode)
/*      */       {
/*      */         public void run() {
/* 6553 */           RongIMClient.ResultCallback.this.onError(this.val$errorCode);
/*      */         } } );
/*      */     }
/*      */ 
/*      */     public void onCallback(T t) {
/* 6559 */       RongIMClient.mHandler.post(new Runnable(t)
/*      */       {
/*      */         public void run() {
/* 6562 */           RongIMClient.ResultCallback.this.onSuccess(this.val$t);
/*      */         }
/*      */       });
/*      */     }
/*      */ 
/*      */     public static class Result<T>
/*      */     {
/*      */       public T t;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class Callback
/*      */   {
/*      */     public void onCallback()
/*      */     {
/* 6479 */       RongIMClient.mHandler.post(new Runnable()
/*      */       {
/*      */         public void run() {
/* 6482 */           RongIMClient.Callback.this.onSuccess();
/*      */         } } );
/*      */     }
/*      */ 
/*      */     public void onFail(int errorCode) {
/* 6488 */       RongIMClient.mHandler.post(new Runnable(errorCode)
/*      */       {
/*      */         public void run() {
/* 6491 */           RongIMClient.Callback.this.onError(RongIMClient.ErrorCode.valueOf(this.val$errorCode));
/*      */         } } );
/*      */     }
/*      */ 
/*      */     public void onFail(RongIMClient.ErrorCode errorCode) {
/* 6497 */       RongIMClient.mHandler.post(new Runnable(errorCode)
/*      */       {
/*      */         public void run() {
/* 6500 */           RongIMClient.Callback.this.onError(this.val$errorCode);
/*      */         }
/*      */       });
/*      */     }
/*      */ 
/*      */     public abstract void onSuccess();
/*      */ 
/*      */     public abstract void onError(RongIMClient.ErrorCode paramErrorCode);
/*      */   }
/*      */ 
/*      */   public static enum ErrorCode
/*      */   {
/* 6214 */     PARAMETER_ERROR(-3, "the parameter is error."), 
/*      */ 
/* 6218 */     IPC_DISCONNECT(-2, "IPC is not connected"), 
/*      */ 
/* 6222 */     UNKNOWN(-1, "unknown"), 
/*      */ 
/* 6226 */     CONNECTED(0, "connected"), 
/*      */ 
/* 6231 */     MSG_ROAMING_SERVICE_UNAVAILABLE(33007, "Message roaming service unavailable"), 
/*      */ 
/* 6235 */     NOT_IN_DISCUSSION(21406, ""), 
/*      */ 
/* 6239 */     NOT_IN_GROUP(22406, ""), 
/*      */ 
/* 6243 */     FORBIDDEN_IN_GROUP(22408, ""), 
/*      */ 
/* 6247 */     NOT_IN_CHATROOM(23406, ""), 
/*      */ 
/* 6251 */     FORBIDDEN_IN_CHATROOM(23408, ""), 
/*      */ 
/* 6255 */     KICKED_FROM_CHATROOM(23409, ""), 
/*      */ 
/* 6260 */     RC_CHATROOM_NOT_EXIST(23410, "Chat room does not exist"), 
/*      */ 
/* 6265 */     RC_CHATROOM_IS_FULL(23411, "Chat room is full"), 
/*      */ 
/* 6270 */     RC_CHATROOM_ILLEGAL_ARGUMENT(23412, "illegal argument."), 
/*      */ 
/* 6274 */     REJECTED_BY_BLACKLIST(405, "rejected by blacklist"), 
/*      */ 
/* 6279 */     RC_NET_CHANNEL_INVALID(30001, "Socket does not exist"), 
/*      */ 
/* 6283 */     RC_NET_UNAVAILABLE(30002, ""), 
/*      */ 
/* 6287 */     RC_MSG_RESP_TIMEOUT(30003, ""), 
/*      */ 
/* 6291 */     RC_HTTP_SEND_FAIL(30004, ""), 
/*      */ 
/* 6295 */     RC_HTTP_REQ_TIMEOUT(30005, ""), 
/*      */ 
/* 6299 */     RC_HTTP_RECV_FAIL(30006, ""), 
/*      */ 
/* 6303 */     RC_NAVI_RESOURCE_ERROR(30007, ""), 
/*      */ 
/* 6307 */     RC_NODE_NOT_FOUND(30008, ""), 
/*      */ 
/* 6311 */     RC_DOMAIN_NOT_RESOLVE(30009, ""), 
/*      */ 
/* 6315 */     RC_SOCKET_NOT_CREATED(30010, ""), 
/*      */ 
/* 6319 */     RC_SOCKET_DISCONNECTED(30011, ""), 
/*      */ 
/* 6323 */     RC_PING_SEND_FAIL(30012, ""), 
/*      */ 
/* 6327 */     RC_PONG_RECV_FAIL(30013, ""), 
/*      */ 
/* 6332 */     RC_MSG_SEND_FAIL(30014, ""), 
/*      */ 
/* 6337 */     RC_CONN_OVERFREQUENCY(30015, "Connect over frequency."), 
/*      */ 
/* 6342 */     RC_CONN_ACK_TIMEOUT(31000, ""), 
/*      */ 
/* 6346 */     RC_CONN_PROTO_VERSION_ERROR(31001, ""), 
/*      */ 
/* 6350 */     RC_CONN_ID_REJECT(31002, ""), 
/*      */ 
/* 6354 */     RC_CONN_SERVER_UNAVAILABLE(31003, ""), 
/*      */ 
/* 6358 */     RC_CONN_USER_OR_PASSWD_ERROR(31004, ""), 
/*      */ 
/* 6362 */     RC_CONN_NOT_AUTHRORIZED(31005, ""), 
/*      */ 
/* 6366 */     RC_CONN_REDIRECTED(31006, ""), 
/*      */ 
/* 6370 */     RC_CONN_PACKAGE_NAME_INVALID(31007, ""), 
/*      */ 
/* 6374 */     RC_CONN_APP_BLOCKED_OR_DELETED(31008, ""), 
/*      */ 
/* 6378 */     RC_CONN_USER_BLOCKED(31009, ""), 
/*      */ 
/* 6382 */     RC_DISCONN_KICK(31010, ""), 
/*      */ 
/* 6386 */     RC_DISCONN_EXCEPTION(31011, ""), 
/*      */ 
/* 6390 */     RC_QUERY_ACK_NO_DATA(32001, ""), 
/*      */ 
/* 6394 */     RC_MSG_DATA_INCOMPLETE(32002, ""), 
/*      */ 
/* 6399 */     BIZ_ERROR_CLIENT_NOT_INIT(33001, ""), 
/*      */ 
/* 6403 */     BIZ_ERROR_DATABASE_ERROR(33002, ""), 
/*      */ 
/* 6407 */     BIZ_ERROR_INVALID_PARAMETER(33003, ""), 
/*      */ 
/* 6411 */     BIZ_ERROR_NO_CHANNEL(33004, ""), 
/*      */ 
/* 6415 */     BIZ_ERROR_RECONNECT_SUCCESS(33005, ""), 
/*      */ 
/* 6419 */     BIZ_ERROR_CONNECTING(33006, ""), 
/*      */ 
/* 6423 */     NOT_FOLLOWED(29106, "");
/*      */ 
/*      */     private int code;
/*      */     private String msg;
/*      */ 
/*      */     private ErrorCode(int code, String msg)
/*      */     {
/* 6435 */       this.code = code;
/* 6436 */       this.msg = msg;
/*      */     }
/*      */ 
/*      */     public int getValue()
/*      */     {
/* 6445 */       return this.code;
/*      */     }
/*      */ 
/*      */     public String getMessage()
/*      */     {
/* 6454 */       return this.msg;
/*      */     }
/*      */ 
/*      */     public static ErrorCode valueOf(int code)
/*      */     {
/* 6464 */       for (ErrorCode c : values()) {
/* 6465 */         if (code == c.getValue()) {
/* 6466 */           return c;
/*      */         }
/*      */       }
/*      */ 
/* 6470 */       Log.d("RongIMClient", "valueOf,ErrorCode:" + code);
/*      */ 
/* 6472 */       return UNKNOWN;
/*      */     }
/*      */   }
/*      */ 
/*      */   class StatusListener extends IConnectionStatusListener.Stub
/*      */   {
/*      */     StatusListener()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void onChanged(int status)
/*      */       throws RemoteException
/*      */     {
/* 5576 */       RLog.d("RongIMClient", "onChanged cur = " + RongIMClient.this.mConnectionStatus + ", to = " + status);
/*      */ 
/* 5579 */       if (RongIMClient.this.mConnectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT) {
/* 5580 */         return;
/*      */       }
/*      */ 
/* 5584 */       if (RongIMClient.this.mConnectionStatus.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING)) {
/* 5585 */         return;
/*      */       }
/*      */ 
/* 5588 */       RongIMClient.ConnectionStatusListener.ConnectionStatus state = (RongIMClient.ConnectionStatusListener.ConnectionStatus)RongIMClient.sStateMap.get(Integer.valueOf(status));
/* 5589 */       onStatusChange(state);
/*      */     }
/*      */ 
/*      */     void onStatusChange(RongIMClient.ConnectionStatusListener.ConnectionStatus status) {
/* 5593 */       RLog.d("RongIMClient", "onStatusChange : cur = " + RongIMClient.this.mConnectionStatus + ", to = " + status + ", retry = " + RongIMClient.this.mReconnectCount);
/*      */ 
/* 5595 */       if (RongIMClient.SingletonHolder.sInstance.mToken == null) {
/* 5596 */         RLog.i("RongIMClient", "onStatusChange Token is null!");
/* 5597 */         return;
/*      */       }
/*      */ 
/* 5600 */       if (status == null) {
/* 5601 */         RLog.e("RongIMClient", "onStatusChange Unknown error!");
/* 5602 */         return;
/*      */       }
/*      */ 
/* 5605 */       if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT)) {
/* 5606 */         RongIMClient.access$702(RongIMClient.SingletonHolder.sInstance, null);
/*      */       }
/*      */ 
/* 5609 */       if ((RongIMClient.sConnectionListener != null) && ((!RongIMClient.this.mConnectionStatus.equals(status)) || (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED))))
/*      */       {
/* 5612 */         RongIMClient.sConnectionListener.onChanged(status);
/*      */       }
/*      */ 
/* 5615 */       if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
/* 5616 */         Object[] list = RongIMClient.this.mChatroomCache.toArray();
/* 5617 */         RLog.d("RongIMClient", "reJoinChatRoom, size = " + RongIMClient.this.mChatroomCache.size());
/* 5618 */         for (int i = 0; i < RongIMClient.this.mChatroomCache.size(); i++) {
/* 5619 */           RongIMClient.SingletonHolder.sInstance.reJoinChatRoom((String)list[i], 0, null);
/*      */         }
/*      */ 
/* 5622 */         if (RongIMClient.this.mReconnectRunnable != null) {
/* 5623 */           RongIMClient.mHandler.removeCallbacks(RongIMClient.this.mReconnectRunnable);
/* 5624 */           RongIMClient.access$302(RongIMClient.this, null);
/*      */         }
/*      */       }
/*      */ 
/* 5628 */       if ((status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE)) && (RongIMClient.this.mReconnectCount < RongIMClient.this.mReconnectInterval.length) && (RongIMClient.this.mConnectionStatus != status))
/*      */       {
/* 5632 */         ConnectivityManager cm = (ConnectivityManager)RongIMClient.this.mContext.getSystemService("connectivity");
/* 5633 */         NetworkInfo networkInfo = cm.getActiveNetworkInfo();
/* 5634 */         if ((networkInfo != null) && (networkInfo.isAvailable())) {
/* 5635 */           RLog.d("RongIMClient", "onStatusChange, Will reconnect after " + RongIMClient.this.mReconnectInterval[RongIMClient.this.mReconnectCount] * 1000);
/* 5636 */           RongIMClient.access$302(RongIMClient.this, new RongIMClient.ReconnectRunnable(RongIMClient.this));
/* 5637 */           RongIMClient.mHandler.postDelayed(RongIMClient.this.mReconnectRunnable, RongIMClient.this.mReconnectInterval[RongIMClient.this.mReconnectCount] * 1000);
/*      */         } else {
/* 5639 */           RLog.e("RongIMClient", "onStatusChange, network unavailable.");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 5644 */       RongIMClient.access$802(RongIMClient.this, status);
/*      */     }
/*      */   }
/*      */ 
/*      */   class AidlConnection
/*      */     implements ServiceConnection
/*      */   {
/*      */     AidlConnection()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void onServiceConnected(ComponentName name, IBinder service)
/*      */     {
/*  358 */       RLog.d("RongIMClient", "onServiceConnected mConnectionStatus = " + RongIMClient.this.mConnectionStatus);
/*  359 */       RongIMClient.access$102(RongIMClient.this, 0);
/*  360 */       RongIMClient.access$902(RongIMClient.this, false);
/*  361 */       RongIMClient.access$402(RongIMClient.this, IHandler.Stub.asInterface(service));
/*      */       try
/*      */       {
/*  364 */         if (!TextUtils.isEmpty(RongIMClient.mNaviServer)) {
/*  365 */           RongIMClient.this.mLibHandler.setServerInfo(RongIMClient.mNaviServer, RongIMClient.mFileServer);
/*      */         }
/*  367 */         RongIMClient.this.mLibHandler.setConnectionStatusListener(RongIMClient.this.mStatusListener);
/*  368 */         ModuleManager.init(RongIMClient.this.mContext, RongIMClient.this.mLibHandler);
/*  369 */         RongIMClient.this.initMessageReceiver();
/*      */ 
/*  371 */         synchronized (RongIMClient.this.mRegCache) {
/*  372 */           for (String item : RongIMClient.this.mRegCache)
/*  373 */             RongIMClient.this.mLibHandler.registerMessageType(item);
/*      */         }
/*      */       }
/*      */       catch (RemoteException e) {
/*  377 */         e.printStackTrace();
/*      */       }
/*      */ 
/*  380 */       RongIMClient.this.registerCmdMsgType();
/*      */ 
/*  382 */       IntentFilter intentFilter = new IntentFilter();
/*  383 */       intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
/*  384 */       intentFilter.addAction("action_reconnect");
/*  385 */       intentFilter.addAction("android.intent.action.USER_PRESENT");
/*  386 */       RongIMClient.this.mContext.registerReceiver(RongIMClient.this.mConnectChangeReceiver, intentFilter);
/*      */ 
/*  388 */       if (RongIMClient.this.mConnectRunnable != null)
/*  389 */         RongIMClient.mHandler.post(RongIMClient.this.mConnectRunnable);
/*  390 */       else if (RongIMClient.this.mToken != null)
/*  391 */         RongIMClient.this.reconnect(null);
/*      */     }
/*      */ 
/*      */     public void onServiceDisconnected(ComponentName name)
/*      */     {
/*  397 */       RongIMClient.access$402(RongIMClient.this, null);
/*  398 */       RongIMClient.access$902(RongIMClient.this, false);
/*      */ 
/*  400 */       RLog.d("RongIMClient", "onServiceDisconnected " + RongIMClient.this.mConnectionStatus + " -> DISCONNECTED");
/*  401 */       RongIMClient.this.mStatusListener.onStatusChange(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*      */       try {
/*  403 */         if (RongIMClient.this.mConnectChangeReceiver != null)
/*  404 */           RongIMClient.this.mContext.unregisterReceiver(RongIMClient.this.mConnectChangeReceiver);
/*      */       }
/*      */       catch (IllegalArgumentException e) {
/*  407 */         e.printStackTrace();
/*      */       }
/*      */ 
/*  410 */       if (SystemUtils.isAppRunning(RongIMClient.this.mContext, RongIMClient.this.mContext.getPackageName())) {
/*  411 */         RLog.d("RongIMClient", "onServiceDisconnected Main process is running.");
/*  412 */         RongIMClient.this.initBindService();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class DisconnectRunnable
/*      */     implements Runnable
/*      */   {
/*      */     boolean isReceivePush;
/*      */ 
/*      */     public DisconnectRunnable(boolean isReceivePush)
/*      */     {
/*  277 */       this.isReceivePush = isReceivePush;
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/*      */       try {
/*  283 */         RLog.d("RongIMClient", "DisconnectRunnable do disconnect!");
/*      */ 
/*  285 */         if (RongIMClient.this.mLibHandler == null) {
/*  286 */           RLog.e("RongIMClient", "DisconnectRunnable mLibHandler is null!");
/*  287 */           return;
/*      */         }
/*      */ 
/*  290 */         RongIMClient.this.mLibHandler.disconnect(this.isReceivePush, new IOperationCallback.Stub()
/*      */         {
/*      */           public void onComplete() throws RemoteException {
/*  293 */             RongIMClient.this.mStatusListener.onStatusChange(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*      */ 
/*  295 */             RongIMClient.access$602(RongIMClient.this, null);
/*  296 */             RongIMClient.access$702(RongIMClient.this, null);
/*      */           }
/*      */ 
/*      */           public void onFailure(int errorCode) throws RemoteException
/*      */           {
/*  301 */             RongIMClient.this.mStatusListener.onStatusChange(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED);
/*  302 */             RongIMClient.access$602(RongIMClient.this, null);
/*  303 */             RongIMClient.access$702(RongIMClient.this, null);
/*      */           } } );
/*      */       } catch (RemoteException e) {
/*  307 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ReconnectRunnable
/*      */     implements Runnable
/*      */   {
/*      */     public ReconnectRunnable()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/*  261 */       RLog.d("RongIMClient", "ReconnectRunnable, count = " + RongIMClient.this.mReconnectCount);
/*      */ 
/*  263 */       Intent intent = new Intent(RongIMClient.this.mContext, ConnectChangeReceiver.class);
/*  264 */       intent.setAction("action_reconnect");
/*  265 */       RongIMClient.this.mContext.sendBroadcast(intent);
/*      */ 
/*  267 */       RongIMClient.access$108(RongIMClient.this);
/*  268 */       RongIMClient.access$302(RongIMClient.this, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class ConnectRunnable
/*      */     implements Runnable
/*      */   {
/*      */     String token;
/*      */     RongIMClient.ConnectCallback connectCallback;
/*      */ 
/*      */     public ConnectRunnable(String token, RongIMClient.ConnectCallback connectCallback)
/*      */     {
/*  243 */       this.token = token;
/*  244 */       this.connectCallback = connectCallback;
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/*  249 */       RLog.d("RongIMClient", "ConnectRunnable do connect!");
/*  250 */       RongIMClient.connect(this.token, this.connectCallback);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class SingletonHolder
/*      */   {
/*  214 */     static RongIMClient sInstance = new RongIMClient(null);
/*      */   }
/*      */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.RongIMClient
 * JD-Core Version:    0.6.0
 */
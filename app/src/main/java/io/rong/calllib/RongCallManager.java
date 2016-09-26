/*      */ package io.rong.calllib;
/*      */ 
/*      */ import android.content.Context;
/*      */ import android.content.SharedPreferences;
/*      */ import android.os.Handler;
/*      */ import android.os.Looper;
/*      */ import android.os.RemoteException;
/*      */ import android.preference.PreferenceManager;
/*      */ import android.text.TextUtils;
/*      */ import android.view.SurfaceView;
/*      */ import io.agora.rtc.RtcEngine;
/*      */ import io.rong.calllib.message.CallAcceptMessage;
/*      */ import io.rong.calllib.message.CallHangupMessage;
/*      */ import io.rong.calllib.message.CallInviteMessage;
/*      */ import io.rong.calllib.message.CallModifyMediaMessage;
/*      */ import io.rong.calllib.message.CallModifyMemberMessage;
/*      */ import io.rong.calllib.message.CallRingingMessage;
/*      */ import io.rong.common.RLog;
/*      */ import io.rong.imlib.IHandler;
/*      */ import io.rong.imlib.ISendMessageCallback.Stub;
/*      */ import io.rong.imlib.IStringCallback.Stub;
/*      */ import io.rong.imlib.RongIMClient;
/*      */ import io.rong.imlib.model.Conversation.ConversationType;
/*      */ import io.rong.imlib.stateMachine.State;
/*      */ import io.rong.imlib.stateMachine.StateMachine;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Timer;
/*      */ import java.util.TimerTask;
/*      */ import org.json.JSONArray;
/*      */ import org.json.JSONException;
/*      */ import org.json.JSONObject;
/*      */ 
/*      */ class RongCallManager extends StateMachine
/*      */ {
/*   43 */   private static final String TAG = RongCallManager.class.getSimpleName();
/*      */   private static final int DEFAULT_VIDEO_PROFILE = 30;
/*      */   private static RongCallManager sInstance;
/*      */   private IRongCallListener callListener;
/*      */   private IRongCallRecordListener mCallRecordListener;
/*      */   private CallSessionImp callSessionImp;
/*      */   private static final int CALL_TIMEOUT_INTERVAL = 60000;
/*      */   private Context context;
/*      */   private Timer timer;
/*      */   private IHandler libStub;
/*      */   private Map<String, TimerTask> timerTasks;
/*      */   private IRongCallEngine voIPEngine;
/*      */   private List<String> unknownMediaIdList;
/*      */   private Handler uiHandler;
/*      */   private static IRongReceivedCallListener receivedCallListener;
/*   61 */   private int videoProfile = 30;
/*      */   private static final int STRATEGY_NONE = 0;
/*      */   private static final int STRATEGY_PRIORITY = 1;
/*   69 */   private IVideoFrameListener videoFrameListener = null;
/*      */ 
/*  336 */   private State mCheckPermissionState = new CheckPermissionState(null);
/*      */ 
/*  441 */   private State mUnInitState = new UnInitState(null);
/*      */ 
/*  575 */   private State mIdleState = new IdleState(null);
/*      */ 
/*  716 */   private State mIncomingState = new IncomingState(null);
/*      */ 
/*  862 */   private State mOutgoingState = new OutgoingState(null);
/*      */ 
/* 1030 */   private State mConnectingState = new ConnectingState(null);
/*      */ 
/* 1193 */   private State mConnectedState = new ConnectedState(null);
/*      */ 
/*      */   private RongCallManager(String name)
/*      */   {
/*   72 */     super(name);
/*      */   }
/*      */ 
/*      */   static RongCallManager getInstance() {
/*   76 */     if (sInstance == null)
/*   77 */       sInstance = new RongCallManager(TAG);
/*   78 */     return sInstance;
/*      */   }
/*      */ 
/*      */   static void setReceivedCallListener(IRongReceivedCallListener listener) {
/*   82 */     RLog.i(TAG, "setReceivedCallListener, listener = " + listener);
/*   83 */     receivedCallListener = new IRongReceivedCallListener(listener)
/*      */     {
/*      */       public void onReceivedCall(RongCallSession callSession) {
/*   86 */         if (RongCallManager.sInstance == null) {
/*   87 */           RLog.e(RongCallManager.TAG, "RongVoIPManager does not init.");
/*   88 */           return;
/*      */         }
/*   90 */         RongCallManager.sInstance.runOnUiThread(new Runnable(callSession)
/*      */         {
/*      */           public void run() {
/*   93 */             RLog.e(RongCallManager.TAG, "onReceivedCall.");
/*   94 */             RongCallManager.1.this.listener.onReceivedCall(this.val$callSession);
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onCheckPermission(RongCallSession callSession) {
/*  101 */         if (RongCallManager.sInstance == null) {
/*  102 */           RLog.e(RongCallManager.TAG, "RongVoIPManager does not init.");
/*  103 */           return;
/*      */         }
/*  105 */         RongCallManager.sInstance.runOnUiThread(new Runnable(callSession)
/*      */         {
/*      */           public void run() {
/*  108 */             RLog.i(RongCallManager.TAG, "onCheckPermission.");
/*  109 */             RongCallManager.1.this.val$listener.onCheckPermission(this.val$callSession);
/*      */           } } );
/*      */       } } ;
/*      */   }
/*      */ 
/*      */   void setCallRecordListener(IRongCallRecordListener recordListener) {
/*  117 */     this.mCallRecordListener = new IRongCallRecordListener(recordListener)
/*      */     {
/*      */       public void onServerStartRecording(RongCallCommon.ServerRecordingErrorCode errorCode) {
/*  120 */         RongCallManager.this.runOnUiThread(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*  123 */             if (RongCallManager.2.this.val$recordListener != null)
/*  124 */               RongCallManager.2.this.val$recordListener.onServerStartRecording(this.val$errorCode);
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onServerStopRecording(RongCallCommon.ServerRecordingErrorCode errorCode) {
/*  131 */         RongCallManager.this.runOnUiThread(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*  134 */             if (RongCallManager.2.this.val$recordListener != null)
/*  135 */               RongCallManager.2.this.val$recordListener.onServerStopRecording(this.val$errorCode);
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onServerFetchRecordingStatus(boolean isRecording, RongCallCommon.ServerRecordingErrorCode errorCode) {
/*  142 */         RongCallManager.this.runOnUiThread(new Runnable(isRecording, errorCode)
/*      */         {
/*      */           public void run() {
/*  145 */             if (RongCallManager.2.this.val$recordListener != null)
/*  146 */               RongCallManager.2.this.val$recordListener.onServerFetchRecordingStatus(this.val$isRecording, this.val$errorCode);
/*      */           } } );
/*      */       } } ;
/*      */   }
/*      */ 
/*      */   void setCallListener(IRongCallListener voIPCallListener) {
/*  154 */     RLog.i(TAG, "setCallListener, listener = " + voIPCallListener);
/*  155 */     this.callListener = new IRongCallListener(voIPCallListener)
/*      */     {
/*      */       public void onCallOutgoing(RongCallSession callProfile, SurfaceView surfaceView) {
/*  158 */         RongCallManager.this.runOnUiThread(new Runnable(callProfile, surfaceView)
/*      */         {
/*      */           public void run() {
/*  161 */             if (RongCallManager.3.this.val$voIPCallListener != null)
/*  162 */               RongCallManager.3.this.val$voIPCallListener.onCallOutgoing(this.val$callProfile, this.val$surfaceView);
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onRemoteUserRinging(String userId) {
/*  169 */         RongCallManager.this.runOnUiThread(new Runnable(userId)
/*      */         {
/*      */           public void run() {
/*  172 */             if (RongCallManager.3.this.val$voIPCallListener != null)
/*  173 */               RongCallManager.3.this.val$voIPCallListener.onRemoteUserRinging(this.val$userId);
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onCallDisconnected(RongCallSession callProfile, RongCallCommon.CallDisconnectedReason reason) {
/*  180 */         RongCallManager.this.runOnUiThread(new Runnable(callProfile, reason)
/*      */         {
/*      */           public void run() {
/*  183 */             if (RongCallManager.3.this.val$voIPCallListener != null)
/*  184 */               RongCallManager.3.this.val$voIPCallListener.onCallDisconnected(this.val$callProfile, this.val$reason);
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onRemoteUserInvited(String userId, RongCallCommon.CallMediaType mediaType) {
/*  191 */         RongCallManager.this.runOnUiThread(new Runnable(userId, mediaType)
/*      */         {
/*      */           public void run() {
/*  194 */             if (RongCallManager.3.this.val$voIPCallListener != null)
/*  195 */               RongCallManager.3.this.val$voIPCallListener.onRemoteUserInvited(this.val$userId, this.val$mediaType);
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onRemoteUserJoined(String userId, RongCallCommon.CallMediaType mediaType, SurfaceView videoView) {
/*  202 */         RongCallManager.this.runOnUiThread(new Runnable(userId, mediaType, videoView)
/*      */         {
/*      */           public void run() {
/*  205 */             if (RongCallManager.3.this.val$voIPCallListener != null)
/*  206 */               RongCallManager.3.this.val$voIPCallListener.onRemoteUserJoined(this.val$userId, this.val$mediaType, this.val$videoView);
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onMediaTypeChanged(String userId, RongCallCommon.CallMediaType mediaType, SurfaceView videoView) {
/*  213 */         RongCallManager.this.runOnUiThread(new Runnable(userId, mediaType, videoView)
/*      */         {
/*      */           public void run() {
/*  216 */             if (RongCallManager.3.this.val$voIPCallListener != null)
/*  217 */               RongCallManager.3.this.val$voIPCallListener.onMediaTypeChanged(this.val$userId, this.val$mediaType, this.val$videoView);
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onError(RongCallCommon.CallErrorCode errorCode) {
/*  224 */         RongCallManager.this.runOnUiThread(new Runnable(errorCode)
/*      */         {
/*      */           public void run() {
/*  227 */             if (RongCallManager.3.this.val$voIPCallListener != null) RongCallManager.3.this.val$voIPCallListener.onError(this.val$errorCode); 
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onRemoteUserLeft(String userId, RongCallCommon.CallDisconnectedReason reason)
/*      */       {
/*  234 */         RongCallManager.this.runOnUiThread(new Runnable(userId, reason)
/*      */         {
/*      */           public void run() {
/*  237 */             if (RongCallManager.3.this.val$voIPCallListener != null)
/*  238 */               RongCallManager.3.this.val$voIPCallListener.onRemoteUserLeft(this.val$userId, this.val$reason);
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onCallConnected(RongCallSession callProfile, SurfaceView localVideo) {
/*  245 */         RongCallManager.this.runOnUiThread(new Runnable(callProfile, localVideo)
/*      */         {
/*      */           public void run() {
/*  248 */             if (RongCallManager.3.this.val$voIPCallListener != null)
/*  249 */               RongCallManager.3.this.val$voIPCallListener.onCallConnected(this.val$callProfile, this.val$localVideo);
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public void onRemoteCameraDisabled(String userId, boolean muted) {
/*  256 */         RongCallManager.this.runOnUiThread(new Runnable(userId, muted)
/*      */         {
/*      */           public void run() {
/*  259 */             if (RongCallManager.3.this.val$voIPCallListener != null)
/*  260 */               RongCallManager.3.this.val$voIPCallListener.onRemoteCameraDisabled(this.val$userId, this.val$muted);
/*      */           } } );
/*      */       } } ;
/*      */   }
/*      */ 
/*      */   void init(Context context, IHandler stub) {
/*  268 */     this.timer = new Timer();
/*  269 */     this.timerTasks = new HashMap();
/*  270 */     this.context = context.getApplicationContext();
/*  271 */     this.unknownMediaIdList = new ArrayList();
/*  272 */     this.libStub = stub;
/*      */ 
/*  274 */     this.uiHandler = new Handler(Looper.getMainLooper());
/*      */ 
/*  279 */     addState(this.mIdleState);
/*  280 */     addState(this.mUnInitState);
/*  281 */     addState(this.mIncomingState, this.mIdleState);
/*  282 */     addState(this.mOutgoingState, this.mIdleState);
/*  283 */     addState(this.mConnectingState, this.mIdleState);
/*  284 */     addState(this.mConnectedState, this.mIdleState);
/*      */ 
/*  286 */     addState(this.mCheckPermissionState);
/*  287 */     setInitialState(this.mUnInitState);
/*      */ 
/*  289 */     start();
/*      */   }
/*      */ 
/*      */   private void runOnUiThread(Runnable runnable) {
/*  293 */     this.uiHandler.post(runnable);
/*      */   }
/*      */ 
/*      */   private boolean initEngine(RongCallCommon.CallEngineType localEngine) {
/*      */     try {
/*  298 */       String info = this.libStub.getVoIPCallInfo();
/*  299 */       if (info == null) {
/*  300 */         RLog.e(TAG, "getVoIPCallInfo returns null while initEngine");
/*  301 */         return false;
/*      */       }
/*  303 */       JSONObject jsonObject = new JSONObject(info);
/*  304 */       int strategy = jsonObject.getInt("strategy");
/*  305 */       JSONArray jsonArray = jsonObject.getJSONArray("callEngine");
/*      */ 
/*  308 */       JSONObject object = jsonArray.getJSONObject(0);
/*  309 */       int engineType = object.optInt("engineType");
/*  310 */       String vendorKey = object.optString("vendorKey");
/*      */ 
/*  312 */       if ((engineType < 0) || (TextUtils.isEmpty(vendorKey)) || (strategy <= 0)) {
/*  313 */         return false;
/*      */       }
/*  315 */       if ((localEngine == null) || (localEngine.getValue() == engineType)) {
/*  316 */         Class engineClass = Class.forName(AgoraEngine.class.getCanonicalName());
/*  317 */         Constructor constructor = engineClass.getConstructor(new Class[0]);
/*  318 */         this.voIPEngine = ((AgoraEngine)constructor.newInstance(new Object[0]));
/*  319 */         Class engineListenerClass = Class.forName(AgoraEngineListener.class.getCanonicalName());
/*  320 */         constructor = engineListenerClass.getConstructor(new Class[] { Handler.class });
/*  321 */         IRongCallEngineListener engineListener = (AgoraEngineListener)constructor.newInstance(new Object[] { getHandler() });
/*  322 */         this.voIPEngine.init(this.context, vendorKey, engineListener);
/*  323 */         if (this.videoFrameListener != null) {
/*  324 */           NativeCallObject.getInstance().registerVideoFrameObserver(this.videoFrameListener);
/*      */         }
/*  326 */         return true;
/*      */       }
/*      */     }
/*      */     catch (Exception e) {
/*  330 */       RLog.e(TAG, "VOIP Init Error!" + e.getMessage());
/*  331 */       e.printStackTrace();
/*      */     }
/*  333 */     return false;
/*      */   }
/*      */ 
/*      */   private String getMyUserId()
/*      */   {
/* 1444 */     String userId = RongIMClient.getInstance().getCurrentUserId();
/* 1445 */     if (TextUtils.isEmpty(userId))
/* 1446 */       userId = PreferenceManager.getDefaultSharedPreferences(this.context).getString("userId", "");
/* 1447 */     return userId;
/*      */   }
/*      */ 
/*      */   private void sendInviteMessage(List<String> userList, SignalCallback callback) {
/* 1451 */     CallInviteMessage inviteMessage = new CallInviteMessage();
/* 1452 */     inviteMessage.setMediaType(this.callSessionImp.getMediaType());
/* 1453 */     inviteMessage.setEngineType(this.callSessionImp.getEngineType());
/* 1454 */     inviteMessage.setInviteUserIds(userList);
/* 1455 */     inviteMessage.setExtra(this.callSessionImp.getExtra());
/* 1456 */     inviteMessage.setCallId(this.callSessionImp.getCallId());
/*      */     try
/*      */     {
/* 1459 */       io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(this.callSessionImp.getTargetId(), this.callSessionImp.getConversationType(), inviteMessage);
/* 1460 */       this.libStub.sendMessage(message, "voip", getPushData(this.callSessionImp.getMediaType(), userList, this.callSessionImp.getCallId()), new ISendMessageCallback.Stub(callback)
/*      */       {
/*      */         public void onAttached(io.rong.imlib.model.Message message)
/*      */           throws RemoteException
/*      */         {
/*      */         }
/*      */ 
/*      */         public void onSuccess(io.rong.imlib.model.Message message)
/*      */           throws RemoteException
/*      */         {
/* 1471 */           if (this.val$callback != null) {
/* 1472 */             String mediaId = RongCallManager.this.getMediaIdBySentTime(message.getSentTime());
/* 1473 */             this.val$callback.onSuccess(message.getSenderUserId(), mediaId);
/*      */           }
/*      */         }
/*      */ 
/*      */         public void onError(io.rong.imlib.model.Message message, int errorCode) throws RemoteException
/*      */         {
/* 1479 */           if (this.val$callback != null)
/* 1480 */             this.val$callback.onError();
/*      */         } } );
/*      */     }
/*      */     catch (RemoteException e) {
/* 1485 */       RLog.e(TAG, "sendModifyMemberMessage exception");
/* 1486 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void sendModifyMemberMessage(List<String> userList, SignalCallback callback) {
/* 1491 */     CallModifyMemberMessage modifyMemberMessage = new CallModifyMemberMessage();
/* 1492 */     modifyMemberMessage.setCallId(this.callSessionImp.getCallId());
/* 1493 */     modifyMemberMessage.setCaller(this.callSessionImp.getCallerUserId());
/* 1494 */     modifyMemberMessage.setInviter(this.callSessionImp.getInviterUserId());
/* 1495 */     modifyMemberMessage.setInvitedList(userList);
/* 1496 */     modifyMemberMessage.setEngineType(this.callSessionImp.getEngineType());
/* 1497 */     modifyMemberMessage.setMediaType(this.callSessionImp.getMediaType());
/* 1498 */     modifyMemberMessage.setModifyMemType(RongCallCommon.CallModifyMemType.MODIFY_MEM_TYPE_ADD);
/* 1499 */     modifyMemberMessage.setExtra(this.callSessionImp.getExtra());
/*      */ 
/* 1501 */     List pushList = new ArrayList();
/* 1502 */     List participantProfileList = this.callSessionImp.getParticipantProfileList();
/* 1503 */     for (CallUserProfile profile : participantProfileList) {
/* 1504 */       pushList.add(profile.getUserId());
/*      */     }
/* 1506 */     for (String id : userList) {
/* 1507 */       CallUserProfile profile = new CallUserProfile();
/* 1508 */       profile.setUserId(id);
/* 1509 */       profile.setMediaType(this.callSessionImp.getMediaType());
/* 1510 */       profile.setCallStatus(RongCallCommon.CallStatus.IDLE);
/* 1511 */       participantProfileList.add(profile);
/* 1512 */       pushList.add(id);
/*      */     }
/*      */ 
/* 1515 */     modifyMemberMessage.setParticipantList(participantProfileList);
/*      */     try
/*      */     {
/* 1518 */       io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(this.callSessionImp.getTargetId(), this.callSessionImp.getConversationType(), modifyMemberMessage);
/* 1519 */       this.libStub.sendMessage(message, "voip", getPushData(this.callSessionImp.getMediaType(), pushList, this.callSessionImp.getCallId()), new ISendMessageCallback.Stub(callback)
/*      */       {
/*      */         public void onAttached(io.rong.imlib.model.Message message)
/*      */           throws RemoteException
/*      */         {
/*      */         }
/*      */ 
/*      */         public void onSuccess(io.rong.imlib.model.Message message)
/*      */           throws RemoteException
/*      */         {
/* 1530 */           if (this.val$callback != null) {
/* 1531 */             String mediaId = RongCallManager.this.getMediaIdBySentTime(message.getSentTime());
/* 1532 */             this.val$callback.onSuccess(message.getSenderUserId(), mediaId);
/*      */           }
/*      */         }
/*      */ 
/*      */         public void onError(io.rong.imlib.model.Message message, int errorCode) throws RemoteException
/*      */         {
/* 1538 */           if (this.val$callback != null)
/* 1539 */             this.val$callback.onError();
/*      */         } } );
/*      */     }
/*      */     catch (RemoteException e) {
/* 1544 */       RLog.e(TAG, "sendModifyMemberMessage exception");
/* 1545 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void sendRingingMessage(String callId) {
/* 1550 */     CallRingingMessage content = new CallRingingMessage();
/* 1551 */     content.setCallId(callId);
/*      */     try
/*      */     {
/* 1554 */       io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(this.callSessionImp.getTargetId(), this.callSessionImp.getConversationType(), content);
/* 1555 */       this.libStub.sendMessage(message, null, null, null);
/*      */     } catch (RemoteException e) {
/* 1557 */       RLog.e(TAG, "sendRingingMessage exception");
/* 1558 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void sendAcceptMessage(String callId, RongCallCommon.CallMediaType type, SignalCallback callback) {
/* 1563 */     CallAcceptMessage content = new CallAcceptMessage();
/* 1564 */     content.setCallId(callId);
/* 1565 */     content.setMediaType(type);
/*      */     try
/*      */     {
/* 1568 */       io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(this.callSessionImp.getTargetId(), this.callSessionImp.getConversationType(), content);
/* 1569 */       this.libStub.sendMessage(message, null, null, new ISendMessageCallback.Stub(callback)
/*      */       {
/*      */         public void onAttached(io.rong.imlib.model.Message message) throws RemoteException
/*      */         {
/*      */         }
/*      */ 
/*      */         public void onSuccess(io.rong.imlib.model.Message message) throws RemoteException
/*      */         {
/* 1577 */           if (this.val$callback != null) {
/* 1578 */             String mediaId = RongCallManager.this.getMediaIdBySentTime(message.getSentTime());
/* 1579 */             this.val$callback.onSuccess(message.getSenderUserId(), mediaId);
/*      */           }
/*      */         }
/*      */ 
/*      */         public void onError(io.rong.imlib.model.Message message, int errorCode) throws RemoteException
/*      */         {
/* 1585 */           if (this.val$callback != null)
/* 1586 */             this.val$callback.onError();
/*      */         } } );
/*      */     }
/*      */     catch (RemoteException e) {
/* 1591 */       RLog.e(TAG, "sendAcceptMessage exception");
/* 1592 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void sendHangupMessage(Conversation.ConversationType conversationType, String targetId, String callId, RongCallCommon.CallDisconnectedReason reason, SignalCallback callback) {
/* 1597 */     CallHangupMessage content = new CallHangupMessage();
/* 1598 */     content.setCallId(callId);
/* 1599 */     content.setHangupReason(reason);
/*      */     try
/*      */     {
/* 1602 */       io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(targetId, conversationType, content);
/* 1603 */       String callEnd = "[结束通话]";
/* 1604 */       this.libStub.sendMessage(message, callEnd, getPushData(null, null, this.callSessionImp.getCallId()), null);
/*      */     } catch (RemoteException e) {
/* 1606 */       RLog.e(TAG, "sendHangupMessage exception");
/* 1607 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   private String getPushData(RongCallCommon.CallMediaType mediaType, List<String> userIds, String callId) {
/* 1612 */     JSONObject jsonObject = new JSONObject();
/*      */     try {
/* 1614 */       if (mediaType != null) {
/* 1615 */         jsonObject.put("mediaType", mediaType.getValue());
/*      */       }
/* 1617 */       jsonObject.put("callId", callId);
/* 1618 */       JSONArray jsonArray = new JSONArray();
/* 1619 */       if (userIds != null) {
/* 1620 */         for (String userId : userIds) {
/* 1621 */           jsonArray.put(userId);
/*      */         }
/* 1623 */         jsonObject.put("userIdList", jsonArray);
/*      */       }
/*      */     } catch (JSONException e) {
/* 1626 */       e.printStackTrace();
/*      */     }
/* 1628 */     return jsonObject.toString();
/*      */   }
/*      */ 
/*      */   private void changeEngineMediaType(String operatorUserId, RongCallCommon.CallMediaType mediaType) {
/* 1632 */     switch (9.$SwitchMap$io$rong$calllib$RongCallCommon$CallMediaType[mediaType.ordinal()]) {
/*      */     case 1:
/* 1634 */       this.voIPEngine.disableVideo();
/* 1635 */       this.voIPEngine.muteLocalVideoStream(true);
/* 1636 */       this.voIPEngine.muteAllRemoteVideoStreams(true);
/* 1637 */       this.callSessionImp.setLocalVideo(null);
/* 1638 */       for (CallUserProfile profile : this.callSessionImp.getParticipantProfileList()) {
/* 1639 */         if (profile.getCallStatus().equals(RongCallCommon.CallStatus.CONNECTED)) {
/* 1640 */           profile.setVideoView(null);
/*      */         }
/*      */       }
/* 1643 */       if (this.callListener == null) break;
/* 1644 */       this.callListener.onMediaTypeChanged(operatorUserId, RongCallCommon.CallMediaType.AUDIO, null); break;
/*      */     case 2:
/* 1648 */       this.voIPEngine.enableVideo();
/* 1649 */       this.voIPEngine.muteLocalVideoStream(false);
/* 1650 */       this.voIPEngine.muteLocalAudioStream(false);
/* 1651 */       this.voIPEngine.muteAllRemoteVideoStreams(false);
/*      */ 
/* 1654 */       if (this.callSessionImp.getLocalVideo() == null) {
/* 1655 */         SurfaceView localVideo = setupLocalVideo();
/* 1656 */         this.callSessionImp.setLocalVideo(localVideo);
/* 1657 */         if (this.callListener != null) {
/* 1658 */           this.callListener.onMediaTypeChanged(operatorUserId, RongCallCommon.CallMediaType.VIDEO, localVideo);
/*      */         }
/*      */       }
/*      */ 
/* 1662 */       for (CallUserProfile profile : this.callSessionImp.getParticipantProfileList()) {
/* 1663 */         if (profile.getCallStatus().equals(RongCallCommon.CallStatus.CONNECTED)) {
/* 1664 */           SurfaceView remoteVideo = profile.getVideoView();
/* 1665 */           reSetupRemoteVideo(profile.getUserId(), remoteVideo);
/* 1666 */           profile.setVideoView(remoteVideo);
/* 1667 */           if (this.callListener != null) {
/* 1668 */             this.callListener.onMediaTypeChanged(profile.getUserId(), RongCallCommon.CallMediaType.VIDEO, remoteVideo);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1673 */     this.callSessionImp.setMediaType(mediaType);
/*      */   }
/*      */ 
/*      */   private void sendChangeMediaTypeMessage(RongCallCommon.CallMediaType mediaType) {
/* 1677 */     CallModifyMediaMessage content = new CallModifyMediaMessage();
/* 1678 */     content.setCallId(this.callSessionImp.getCallId());
/* 1679 */     content.setMediaType(mediaType);
/*      */     try {
/* 1681 */       io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(this.callSessionImp.getTargetId(), this.callSessionImp.getConversationType(), content);
/* 1682 */       this.libStub.sendMessage(message, null, null, null);
/*      */     } catch (RemoteException e) {
/* 1684 */       RLog.e(TAG, "sendChangeMediaTypeMessage exception");
/* 1685 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setupTimerTask(String userId, int event, int interval) {
/* 1690 */     RLog.i(TAG, "setupTimerTask : " + userId);
/*      */ 
/* 1692 */     TimerTask task = new TimerTask(event, userId)
/*      */     {
/*      */       public void run() {
/* 1695 */         android.os.Message msg = android.os.Message.obtain();
/* 1696 */         msg.what = this.val$event;
/* 1697 */         msg.obj = this.val$userId;
/* 1698 */         RongCallManager.this.getHandler().sendMessage(msg);
/*      */       }
/*      */     };
/* 1701 */     this.timerTasks.put(userId, task);
/* 1702 */     this.timer.schedule(task, interval);
/*      */   }
/*      */ 
/*      */   private void cancelTimerTask(String userId) {
/* 1706 */     RLog.i(TAG, "cancelTimerTask : " + userId);
/*      */ 
/* 1708 */     if (this.timerTasks.size() > 0) {
/* 1709 */       TimerTask task = (TimerTask)this.timerTasks.get(userId);
/* 1710 */       if (task != null) {
/* 1711 */         task.cancel();
/* 1712 */         this.timerTasks.remove(userId);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void resetTimer() {
/* 1718 */     if (this.timer != null) {
/* 1719 */       this.timer.cancel();
/* 1720 */       this.timerTasks.clear();
/*      */     }
/* 1722 */     this.timer = new Timer();
/*      */   }
/*      */ 
/*      */   private void updateCallRongLog()
/*      */   {
/*      */   }
/*      */ 
/*      */   private boolean updateParticipantCallStatus(String userId, RongCallCommon.CallStatus status)
/*      */   {
/* 1732 */     List userStatusList = this.callSessionImp.getParticipantProfileList();
/* 1733 */     for (CallUserProfile userStatus : userStatusList) {
/* 1734 */       if (userId.equals(userStatus.getUserId())) {
/* 1735 */         if (status.equals(RongCallCommon.CallStatus.IDLE)) {
/* 1736 */           userStatusList.remove(userStatus);
/* 1737 */           return true;
/*      */         }
/* 1739 */         userStatus.setCallStatus(status);
/* 1740 */         return true;
/*      */       }
/*      */     }
/* 1743 */     return false;
/*      */   }
/*      */ 
/*      */   private void updateParticipantMediaType(String userId, RongCallCommon.CallMediaType type) {
/* 1747 */     List userStatusList = this.callSessionImp.getParticipantProfileList();
/* 1748 */     for (CallUserProfile userStatus : userStatusList)
/* 1749 */       if (userId.equals(userStatus.getUserId())) {
/* 1750 */         userStatus.setMediaType(type);
/* 1751 */         return;
/*      */       }
/*      */   }
/*      */ 
/*      */   private void updateMediaId(String userId, String mediaId)
/*      */   {
/* 1757 */     if ((TextUtils.isEmpty(userId)) || (TextUtils.isEmpty(mediaId))) {
/* 1758 */       return;
/*      */     }
/* 1760 */     List profileList = this.callSessionImp.getParticipantProfileList();
/* 1761 */     for (CallUserProfile profile : profileList)
/* 1762 */       if (userId.equals(profile.getUserId())) {
/* 1763 */         profile.setMediaId(mediaId);
/* 1764 */         return;
/*      */       }
/*      */   }
/*      */ 
/*      */   private void updateParticipantVideo(String userId, SurfaceView surfaceView)
/*      */   {
/* 1770 */     List userStatusList = this.callSessionImp.getParticipantProfileList();
/* 1771 */     for (CallUserProfile userStatus : userStatusList)
/* 1772 */       if (userId.equals(userStatus.getUserId())) {
/* 1773 */         userStatus.setVideoView(surfaceView);
/* 1774 */         return;
/*      */       }
/*      */   }
/*      */ 
/*      */   private void joinChannel(String mediaId)
/*      */   {
/* 1780 */     int uid = Integer.parseInt(mediaId);
/* 1781 */     RLog.i(TAG, "joinChannel, uid = " + uid + ", channelName = " + this.callSessionImp.getCallId());
/*      */     try {
/* 1783 */       this.libStub.getVoIPKey(RongCallCommon.CallEngineType.ENGINE_TYPE_AGORA.getValue(), this.callSessionImp.getCallId(), null, new IStringCallback.Stub(mediaId, uid)
/*      */       {
/*      */         public void onComplete(String result) throws RemoteException {
/* 1786 */           RongCallManager.this.callSessionImp.setDynamicKey(result);
/* 1787 */           RongCallManager.this.voIPEngine.setVideoProfile(RongCallManager.this.videoProfile);
/* 1788 */           RongCallManager.this.voIPEngine.joinChannel(result, RongCallManager.this.callSessionImp.getCallId(), this.val$mediaId, this.val$uid);
/*      */         }
/*      */ 
/*      */         public void onFailure(int errorCode) throws RemoteException
/*      */         {
/* 1793 */           RongCallManager.this.getHandler().sendEmptyMessage(401);
/*      */         } } );
/*      */     } catch (RemoteException e) {
/* 1797 */       RLog.e(TAG, "joinChannel exception");
/* 1798 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void leaveChannel() {
/* 1803 */     if (this.callSessionImp.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO))
/* 1804 */       this.voIPEngine.disableVideo();
/* 1805 */     this.voIPEngine.leaveChannel();
/*      */   }
/*      */ 
/*      */   CallSessionImp getCallSessionImp() {
/* 1809 */     return this.callSessionImp;
/*      */   }
/*      */ 
/*      */   RongCallSession getCallSession() {
/* 1813 */     if (this.callSessionImp == null) {
/* 1814 */       return null;
/*      */     }
/* 1816 */     RongCallSession callSession = new RongCallSession();
/* 1817 */     callSession.setExtra(this.callSessionImp.getExtra());
/* 1818 */     callSession.setMediaType(this.callSessionImp.getMediaType());
/* 1819 */     callSession.setEngineType(this.callSessionImp.getEngineType());
/*      */ 
/* 1821 */     callSession.setConversationType(this.callSessionImp.getConversationType());
/* 1822 */     callSession.setTargetId(this.callSessionImp.getTargetId());
/*      */ 
/* 1824 */     callSession.setInviterUserId(this.callSessionImp.getInviterUserId());
/* 1825 */     callSession.setSelfUserId(this.callSessionImp.getSelfUserId());
/* 1826 */     callSession.setCallId(this.callSessionImp.getCallId());
/* 1827 */     callSession.setCallerUserId(this.callSessionImp.getCallerUserId());
/*      */ 
/* 1829 */     callSession.setActiveTime(this.callSessionImp.getActiveTime());
/* 1830 */     callSession.setEndTime(this.callSessionImp.getEndTime());
/* 1831 */     callSession.setStartTime(this.callSessionImp.getStartTime());
/*      */ 
/* 1833 */     ArrayList profileList = new ArrayList();
/* 1834 */     profileList.addAll(this.callSessionImp.getParticipantProfileList());
/* 1835 */     callSession.setParticipantUserList(profileList);
/* 1836 */     return callSession;
/*      */   }
/*      */ 
/*      */   private void initializeCallSessionFromInvite(io.rong.imlib.model.Message message) {
/* 1840 */     CallInviteMessage inviteMessage = (CallInviteMessage)message.getContent();
/*      */ 
/* 1842 */     this.callSessionImp = new CallSessionImp();
/* 1843 */     this.callSessionImp.setCallId(inviteMessage.getCallId());
/* 1844 */     this.callSessionImp.setMediaType(inviteMessage.getMediaType());
/* 1845 */     this.callSessionImp.setConversationType(message.getConversationType());
/* 1846 */     this.callSessionImp.setEngineType(inviteMessage.getEngineType());
/* 1847 */     this.callSessionImp.setTargetId(message.getTargetId());
/* 1848 */     this.callSessionImp.setCallerUserId(message.getSenderUserId());
/* 1849 */     this.callSessionImp.setInviterUserId(message.getSenderUserId());
/* 1850 */     this.callSessionImp.setSelfUserId(getMyUserId());
/* 1851 */     this.callSessionImp.setExtra(inviteMessage.getExtra());
/*      */ 
/* 1853 */     List participantProfileList = new ArrayList();
/* 1854 */     CallUserProfile profile = new CallUserProfile();
/* 1855 */     profile.setUserId(message.getSenderUserId());
/* 1856 */     profile.setCallStatus(RongCallCommon.CallStatus.IDLE);
/* 1857 */     String mediaId = getMediaIdBySentTime(message.getSentTime());
/* 1858 */     profile.setMediaId(mediaId);
/* 1859 */     profile.setMediaType(inviteMessage.getMediaType());
/* 1860 */     participantProfileList.add(profile);
/*      */ 
/* 1862 */     for (String userId : inviteMessage.getInviteUserIds()) {
/* 1863 */       profile = new CallUserProfile();
/* 1864 */       profile.setUserId(userId);
/* 1865 */       profile.setMediaType(inviteMessage.getMediaType());
/* 1866 */       profile.setCallStatus(RongCallCommon.CallStatus.IDLE);
/* 1867 */       participantProfileList.add(profile);
/*      */     }
/*      */ 
/* 1870 */     this.callSessionImp.setParticipantUserList(participantProfileList);
/*      */   }
/*      */ 
/*      */   private void initializeCallInfoFromModifyMember(io.rong.imlib.model.Message message) {
/* 1874 */     CallModifyMemberMessage modifyMemberMessage = (CallModifyMemberMessage)message.getContent();
/*      */ 
/* 1876 */     this.callSessionImp = new CallSessionImp();
/* 1877 */     this.callSessionImp.setCallId(modifyMemberMessage.getCallId());
/* 1878 */     this.callSessionImp.setConversationType(message.getConversationType());
/* 1879 */     this.callSessionImp.setTargetId(message.getTargetId());
/* 1880 */     this.callSessionImp.setCallerUserId(message.getSenderUserId());
/* 1881 */     this.callSessionImp.setInviterUserId(message.getSenderUserId());
/* 1882 */     this.callSessionImp.setSelfUserId(getMyUserId());
/* 1883 */     this.callSessionImp.setParticipantUserList(modifyMemberMessage.getParticipantList());
/* 1884 */     this.callSessionImp.setMediaType(modifyMemberMessage.getMediaType());
/* 1885 */     this.callSessionImp.setEngineType(modifyMemberMessage.getEngineType());
/* 1886 */     this.callSessionImp.setExtra(modifyMemberMessage.getExtra());
/*      */   }
/*      */ 
/*      */   private boolean shouldTerminateCall(String userId) {
/* 1890 */     List participantProfileList = this.callSessionImp.getParticipantProfileList();
/*      */ 
/* 1892 */     return (participantProfileList == null) || (participantProfileList.size() <= 1);
/*      */   }
/*      */ 
/*      */   private SurfaceView setupRemoteVideo(String userId)
/*      */   {
/* 1899 */     RLog.i(TAG, "setupRemoteVideo, userId = " + userId);
/* 1900 */     SurfaceView remoteVideo = RtcEngine.CreateRendererView(this.context);
/* 1901 */     this.voIPEngine.enableVideo();
/* 1902 */     remoteVideo.setZOrderOnTop(true);
/* 1903 */     remoteVideo.setZOrderMediaOverlay(true);
/* 1904 */     String mediaId = getMediaIdByUserId(userId);
/* 1905 */     if (mediaId != null) {
/* 1906 */       int uid = Integer.parseInt(mediaId);
/* 1907 */       this.voIPEngine.setupRemoteVideo(remoteVideo, uid);
/*      */     }
/* 1909 */     return remoteVideo;
/*      */   }
/*      */ 
/*      */   private SurfaceView reSetupRemoteVideo(String userId, SurfaceView video) {
/* 1913 */     if (video == null) {
/* 1914 */       video = RtcEngine.CreateRendererView(this.context);
/*      */     }
/* 1916 */     this.voIPEngine.enableVideo();
/* 1917 */     video.setZOrderOnTop(true);
/* 1918 */     video.setZOrderMediaOverlay(true);
/* 1919 */     String mediaId = getMediaIdByUserId(userId);
/* 1920 */     if (mediaId != null) {
/* 1921 */       int uid = Integer.parseInt(mediaId);
/* 1922 */       this.voIPEngine.setupRemoteVideo(video, uid);
/* 1923 */       this.voIPEngine.setRemoteRenderMode(uid, 1);
/*      */     }
/* 1925 */     return video;
/*      */   }
/*      */ 
/*      */   private SurfaceView setupLocalVideo() {
/* 1929 */     RLog.i(TAG, "setupLocalVideo");
/* 1930 */     SurfaceView localVideo = RtcEngine.CreateRendererView(this.context);
/* 1931 */     this.voIPEngine.enableVideo();
/* 1932 */     this.voIPEngine.setLocalRenderMode(1);
/* 1933 */     this.voIPEngine.setupLocalVideo(localVideo);
/* 1934 */     return localVideo;
/*      */   }
/*      */ 
/*      */   private String getMediaIdBySentTime(long sentTime) {
/* 1938 */     return (sentTime & 0x7FFFFFFF) + "";
/*      */   }
/*      */ 
/*      */   private String getMediaIdByUserId(String userId) {
/* 1942 */     List participantProfileList = this.callSessionImp.getParticipantProfileList();
/* 1943 */     for (CallUserProfile profile : participantProfileList) {
/* 1944 */       if (profile.getUserId().equals(userId)) {
/* 1945 */         return profile.getMediaId();
/*      */       }
/*      */     }
/* 1948 */     RLog.e(TAG, "getMediaIdByUserId : [userId " + userId + "-> mediaId : null]");
/* 1949 */     return null;
/*      */   }
/*      */ 
/*      */   private String getUserIdByMediaId(String mediaId) {
/* 1953 */     List participantProfileList = this.callSessionImp.getParticipantProfileList();
/* 1954 */     for (CallUserProfile profile : participantProfileList) {
/* 1955 */       if ((profile.getMediaId() != null) && (profile.getMediaId().equals(mediaId))) {
/* 1956 */         return profile.getUserId();
/*      */       }
/*      */     }
/* 1959 */     RLog.e(TAG, "getUserIdByMediaId : [mediaId " + mediaId + "-> userId : null]");
/* 1960 */     return null;
/*      */   }
/*      */ 
/*      */   private RongCallCommon.CallDisconnectedReason transferRemoteReason(RongCallCommon.CallDisconnectedReason reason) {
/* 1964 */     if (reason.equals(RongCallCommon.CallDisconnectedReason.CANCEL))
/* 1965 */       return RongCallCommon.CallDisconnectedReason.REMOTE_CANCEL;
/* 1966 */     if (reason.equals(RongCallCommon.CallDisconnectedReason.REJECT))
/* 1967 */       return RongCallCommon.CallDisconnectedReason.REMOTE_REJECT;
/* 1968 */     if (reason.equals(RongCallCommon.CallDisconnectedReason.HANGUP))
/* 1969 */       return RongCallCommon.CallDisconnectedReason.REMOTE_HANGUP;
/* 1970 */     if (reason.equals(RongCallCommon.CallDisconnectedReason.BUSY_LINE))
/* 1971 */       return RongCallCommon.CallDisconnectedReason.REMOTE_BUSY_LINE;
/* 1972 */     if (reason.equals(RongCallCommon.CallDisconnectedReason.NO_RESPONSE))
/* 1973 */       return RongCallCommon.CallDisconnectedReason.REMOTE_NO_RESPONSE;
/* 1974 */     if (reason.equals(RongCallCommon.CallDisconnectedReason.ENGINE_UNSUPPORTED)) {
/* 1975 */       return RongCallCommon.CallDisconnectedReason.REMOTE_ENGINE_UNSUPPORTED;
/*      */     }
/* 1977 */     return RongCallCommon.CallDisconnectedReason.NETWORK_ERROR;
/*      */   }
/*      */ 
/*      */   void switchCamera()
/*      */   {
/* 1984 */     this.voIPEngine.switchCamera();
/*      */   }
/*      */ 
/*      */   void setEnableLocalVideo(boolean enabled) {
/* 1988 */     this.voIPEngine.muteLocalVideoStream(!enabled);
/*      */   }
/*      */ 
/*      */   void setEnableLocalAudio(boolean enabled) {
/* 1992 */     this.voIPEngine.muteLocalAudioStream(!enabled);
/*      */   }
/*      */ 
/*      */   void setEnableRemoteAudio(String userId, boolean enabled) {
/* 1996 */     String mediaId = getMediaIdByUserId(userId);
/* 1997 */     if (mediaId != null)
/* 1998 */       this.voIPEngine.muteRemoteAudioStream(Integer.parseInt(mediaId), !enabled);
/*      */   }
/*      */ 
/*      */   void setEnableAllRemoteAudio(boolean enabled)
/*      */   {
/* 2003 */     this.voIPEngine.muteAllRemoteAudioStreams(!enabled);
/*      */   }
/*      */ 
/*      */   void setEnableRemoteVideo(String userId, boolean enabled) {
/* 2007 */     String mediaId = getMediaIdByUserId(userId);
/* 2008 */     if (mediaId != null)
/* 2009 */       this.voIPEngine.muteRemoteVideoStream(Integer.parseInt(mediaId), !enabled);
/*      */   }
/*      */ 
/*      */   void setEnableAllRemoteVideo(boolean enabled)
/*      */   {
/* 2014 */     this.voIPEngine.muteAllRemoteVideoStreams(!enabled);
/*      */   }
/*      */ 
/*      */   void setEnableSpeakerphone(boolean enabled) {
/* 2018 */     this.voIPEngine.setEnableSpeakerphone(enabled);
/*      */   }
/*      */ 
/*      */   void setSpeakerPhoneVolume(int level) {
/* 2022 */     this.voIPEngine.setSpeakerphoneVolume(level);
/*      */   }
/*      */ 
/*      */   void switchVideo(String from, String to) {
/* 2026 */     String fromMediaId = getMediaIdByUserId(from);
/* 2027 */     String toMediaId = getMediaIdByUserId(to);
/*      */ 
/* 2029 */     if ((fromMediaId != null) && (toMediaId != null))
/* 2030 */       this.voIPEngine.switchView(Integer.parseInt(fromMediaId), Integer.parseInt(toMediaId));
/*      */   }
/*      */ 
/*      */   protected boolean isAudioCallEnabled(Conversation.ConversationType type)
/*      */   {
/*      */     try {
/* 2036 */       String info = this.libStub.getVoIPCallInfo();
/* 2037 */       JSONObject jsonObject = new JSONObject(info);
/* 2038 */       int strategy = jsonObject.getInt("strategy");
/*      */ 
/* 2040 */       if (strategy == 1) {
/* 2041 */         JSONArray jsonArray = jsonObject.getJSONArray("callEngine");
/* 2042 */         JSONObject object = jsonArray.getJSONObject(0);
/* 2043 */         int engineType = object.optInt("engineType");
/* 2044 */         if (engineType == RongCallCommon.CallEngineType.ENGINE_TYPE_AGORA.getValue())
/* 2045 */           return true;
/* 2046 */         if ((engineType == RongCallCommon.CallEngineType.ENGINE_TYPE_UMCS.getValue()) && 
/* 2047 */           (type.equals(Conversation.ConversationType.PRIVATE)))
/* 2048 */           return true;
/*      */       }
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 2053 */       RLog.e(TAG, "isAudioCallEnabled error!" + e.getMessage());
/* 2054 */       e.printStackTrace();
/*      */     }
/* 2056 */     return false;
/*      */   }
/*      */ 
/*      */   protected boolean isVideoCallEnabled(Conversation.ConversationType type) {
/*      */     try {
/* 2061 */       String info = this.libStub.getVoIPCallInfo();
/* 2062 */       JSONObject jsonObject = new JSONObject(info);
/* 2063 */       int strategy = jsonObject.getInt("strategy");
/*      */ 
/* 2065 */       if (strategy == 1) {
/* 2066 */         JSONArray jsonArray = jsonObject.getJSONArray("callEngine");
/* 2067 */         JSONObject object = jsonArray.getJSONObject(0);
/* 2068 */         int engineType = object.optInt("engineType");
/* 2069 */         if (engineType == RongCallCommon.CallEngineType.ENGINE_TYPE_AGORA.getValue())
/* 2070 */           return true;
/*      */       }
/*      */     }
/*      */     catch (Exception e) {
/* 2074 */       RLog.e(TAG, "isVideoCallEnabled error! " + e.getMessage());
/* 2075 */       e.printStackTrace();
/*      */     }
/* 2077 */     return false;
/*      */   }
/*      */ 
/*      */   boolean isServerRecordingEnabled() {
/*      */     try {
/* 2082 */       String info = this.libStub.getVoIPCallInfo();
/* 2083 */       JSONObject jsonObject = new JSONObject(info);
/* 2084 */       int strategy = jsonObject.getInt("strategy");
/*      */ 
/* 2086 */       if (strategy == 1) {
/* 2087 */         JSONArray jsonArray = jsonObject.getJSONArray("callEngine");
/* 2088 */         JSONObject object = jsonArray.getJSONObject(0);
/* 2089 */         int engineType = object.optInt("engineType");
/* 2090 */         if (engineType == RongCallCommon.CallEngineType.ENGINE_TYPE_AGORA.getValue())
/* 2091 */           return true;
/*      */       }
/*      */     }
/*      */     catch (Exception e) {
/* 2095 */       RLog.e(TAG, "isServerRecordingEnabled error! " + e.getMessage());
/* 2096 */       e.printStackTrace();
/*      */     }
/* 2098 */     return false;
/*      */   }
/*      */ 
/*      */   public void registerVideoFrameListener(IVideoFrameListener listener)
/*      */   {
/* 2107 */     if (this.voIPEngine != null)
/* 2108 */       NativeCallObject.getInstance().registerVideoFrameObserver(listener);
/*      */     else
/* 2110 */       this.videoFrameListener = listener;
/*      */   }
/*      */ 
/*      */   public void unregisterVideoFrameListener()
/*      */   {
/* 2115 */     if (this.voIPEngine != null) {
/* 2116 */       NativeCallObject.getInstance().unregisterVideoFrameObserver();
/*      */     }
/* 2118 */     this.videoFrameListener = null;
/*      */   }
/*      */ 
/*      */   void setVideoProfile(RongCallCommon.CallVideoProfile profile) {
/* 2122 */     this.videoProfile = profile.getValue();
/*      */   }
/*      */ 
/*      */   private static abstract interface SignalCallback
/*      */   {
/*      */     public abstract void onError();
/*      */ 
/*      */     public abstract void onSuccess(String paramString1, String paramString2);
/*      */   }
/*      */ 
/*      */   private class ConnectedState extends State
/*      */   {
/*      */     private ConnectedState()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void enter()
/*      */     {
/* 1198 */       RLog.d(RongCallManager.TAG, "[" + getName() + "] enter");
/* 1199 */       RongCallManager.this.callSessionImp.setActiveTime(System.currentTimeMillis());
/*      */     }
/*      */ 
/*      */     public boolean processMessage(android.os.Message msg)
/*      */     {
/* 1204 */       RLog.d(RongCallManager.TAG, "[" + getName() + "] processMessage : " + msg.what);
/*      */       String userId;
/*      */       io.rong.imlib.model.Message message;
/*      */       CallModifyMemberMessage modifyMemberMessage;
/* 1206 */       switch (msg.what) {
/*      */       case 207:
/* 1208 */         String muteId = RongCallManager.this.getUserIdByMediaId((String)msg.obj);
/* 1209 */         boolean muted = msg.arg1 != 0;
/* 1210 */         if (RongCallManager.this.callListener == null) break;
/* 1211 */         RongCallManager.this.callListener.onRemoteCameraDisabled(muteId, muted); break;
/*      */       case 103:
/* 1214 */         RongCallManager.this.sendHangupMessage(RongCallManager.this.callSessionImp.getConversationType(), RongCallManager.this.callSessionImp.getTargetId(), RongCallManager.this.callSessionImp.getCallId(), RongCallCommon.CallDisconnectedReason.HANGUP, null);
/* 1215 */         if (RongCallManager.this.callListener != null) {
/* 1216 */           RongCallManager.this.callSessionImp.setEndTime(System.currentTimeMillis());
/* 1217 */           RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), RongCallCommon.CallDisconnectedReason.HANGUP);
/*      */         }
/* 1219 */         RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/* 1220 */         break;
/*      */       case 104:
/* 1222 */         List newUserList = (ArrayList)msg.obj;
/* 1223 */         RongCallManager.this.sendModifyMemberMessage(newUserList, new RongCallManager.SignalCallback(newUserList)
/*      */         {
/*      */           public void onError() {
/* 1226 */             for (String userId : this.val$newUserList)
/* 1227 */               RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.IDLE);
/*      */           }
/*      */ 
/*      */           public void onSuccess(String userId, String mediaId)
/*      */           {
/* 1233 */             for (String id : this.val$newUserList) {
/* 1234 */               RongCallManager.this.updateParticipantCallStatus(id, RongCallCommon.CallStatus.INCOMING);
/* 1235 */               RongCallManager.this.setupTimerTask(id, 402, 60000);
/* 1236 */               if (RongCallManager.this.callListener != null)
/* 1237 */                 RongCallManager.this.callListener.onRemoteUserInvited(id, RongCallManager.this.callSessionImp.getMediaType());
/*      */             }
/*      */           }
/*      */         });
/* 1241 */         break;
/*      */       case 402:
/* 1243 */         userId = (String)msg.obj;
/* 1244 */         RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.IDLE);
/* 1245 */         if (RongCallManager.this.shouldTerminateCall(userId)) {
/* 1246 */           RongCallManager.this.sendHangupMessage(RongCallManager.this.callSessionImp.getConversationType(), RongCallManager.this.callSessionImp.getTargetId(), RongCallManager.this.callSessionImp.getCallId(), RongCallCommon.CallDisconnectedReason.REMOTE_NO_RESPONSE, null);
/* 1247 */           if (RongCallManager.this.callListener != null)
/* 1248 */             RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), RongCallCommon.CallDisconnectedReason.REMOTE_NO_RESPONSE);
/* 1249 */           RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/*      */         } else {
/* 1251 */           if (RongCallManager.this.callListener == null) break;
/* 1252 */           RongCallManager.this.callListener.onRemoteUserLeft(userId, RongCallCommon.CallDisconnectedReason.NO_RESPONSE); } break;
/*      */       case 206:
/* 1256 */         RongCallCommon.CallMediaType type = (RongCallCommon.CallMediaType)msg.obj;
/* 1257 */         if (type.equals(RongCallManager.this.callSessionImp.getMediaType())) break;
/* 1258 */         RongCallManager.this.changeEngineMediaType(RongCallManager.this.callSessionImp.getSelfUserId(), type);
/* 1259 */         RongCallManager.this.sendChangeMediaTypeMessage(type); break;
/*      */       case 105:
/* 1263 */         message = (io.rong.imlib.model.Message)msg.obj;
/* 1264 */         CallInviteMessage inviteMessage = (CallInviteMessage)message.getContent();
/*      */ 
/* 1266 */         boolean isInvited = false;
/* 1267 */         for (String newUserId : inviteMessage.getInviteUserIds()) {
/* 1268 */           if (RongCallManager.this.callSessionImp.getSelfUserId().equals(newUserId)) {
/* 1269 */             isInvited = true;
/* 1270 */             break;
/*      */           }
/*      */         }
/* 1273 */         if (!isInvited) break;
/* 1274 */         RongCallManager.this.updateCallRongLog();
/* 1275 */         RongCallManager.this.sendHangupMessage(message.getConversationType(), message.getTargetId(), inviteMessage.getCallId(), RongCallCommon.CallDisconnectedReason.BUSY_LINE, null); break;
/*      */       case 107:
/* 1279 */         message = (io.rong.imlib.model.Message)msg.obj;
/* 1280 */         modifyMemberMessage = (CallModifyMemberMessage)message.getContent();
/*      */ 
/* 1282 */         if (modifyMemberMessage.getCallId().equals(RongCallManager.this.callSessionImp.getCallId()))
/*      */         {
/* 1284 */           for (String id : modifyMemberMessage.getInvitedList()) {
/* 1285 */             CallUserProfile profile = new CallUserProfile();
/* 1286 */             profile.setUserId(id);
/* 1287 */             profile.setMediaType(modifyMemberMessage.getMediaType());
/* 1288 */             profile.setCallStatus(RongCallCommon.CallStatus.INCOMING);
/* 1289 */             RongCallManager.this.setupTimerTask(id, 402, 60000);
/* 1290 */             RongCallManager.this.callSessionImp.getParticipantProfileList().add(profile);
/* 1291 */             if (RongCallManager.this.callListener != null)
/* 1292 */               RongCallManager.this.callListener.onRemoteUserInvited(id, modifyMemberMessage.getMediaType());
/*      */           }
/*      */         }
/*      */         else {
/* 1296 */           for (String id : modifyMemberMessage.getInvitedList()) {
/* 1297 */             if (RongCallManager.this.callSessionImp.getSelfUserId().equals(id)) {
/* 1298 */               RongCallManager.this.updateCallRongLog();
/* 1299 */               RongCallManager.this.sendHangupMessage(message.getConversationType(), message.getTargetId(), modifyMemberMessage.getCallId(), RongCallCommon.CallDisconnectedReason.BUSY_LINE, null);
/*      */             }
/*      */           }
/*      */         }
/* 1303 */         break;
/*      */       case 109:
/* 1305 */         message = (io.rong.imlib.model.Message)msg.obj;
/* 1306 */         CallHangupMessage hangupMessage = (CallHangupMessage)message.getContent();
/* 1307 */         RongCallCommon.CallDisconnectedReason reason = hangupMessage.getHangupReason();
/* 1308 */         reason = RongCallManager.this.transferRemoteReason(reason);
/* 1309 */         if (!hangupMessage.getCallId().equals(RongCallManager.this.callSessionImp.getCallId())) break;
/* 1310 */         userId = message.getSenderUserId();
/*      */ 
/* 1312 */         boolean result = RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.IDLE);
/* 1313 */         if (result) {
/* 1314 */           RongCallManager.this.cancelTimerTask(userId);
/* 1315 */           if (RongCallManager.this.shouldTerminateCall(userId)) {
/* 1316 */             if (RongCallManager.this.callListener != null) {
/* 1317 */               RongCallManager.this.callSessionImp.setEndTime(System.currentTimeMillis());
/* 1318 */               RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), reason);
/*      */             }
/* 1320 */             RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/*      */           }
/* 1322 */           else if (RongCallManager.this.callListener != null) {
/* 1323 */             RongCallManager.this.callListener.onRemoteUserLeft(userId, hangupMessage.getHangupReason());
/*      */           }
/*      */         }
/*      */         else {
/* 1327 */           RLog.e(RongCallManager.TAG, "user : " + userId + " had been deleted when RECEIVED_LEAVE_CHANNEL_ACTION");
/*      */         }
/* 1329 */         break;
/*      */       case 110:
/* 1332 */         message = (io.rong.imlib.model.Message)msg.obj;
/* 1333 */         CallModifyMediaMessage changeMediaTypeMessage = (CallModifyMediaMessage)message.getContent();
/* 1334 */         if (!changeMediaTypeMessage.getCallId().equals(RongCallManager.this.callSessionImp.getCallId())) break;
/* 1335 */         RongCallCommon.CallMediaType mediaType = changeMediaTypeMessage.getMediaType();
/* 1336 */         RongCallManager.this.changeEngineMediaType(message.getSenderUserId(), mediaType);
/* 1337 */         break;
/*      */       case 203:
/* 1340 */         userId = RongCallManager.this.getUserIdByMediaId((String)msg.obj);
/* 1341 */         if (userId != null) {
/* 1342 */           RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.CONNECTED);
/* 1343 */           RongCallManager.this.cancelTimerTask(userId);
/* 1344 */           SurfaceView remoteVideo = null;
/* 1345 */           RongCallCommon.CallMediaType mediaType = RongCallManager.this.callSessionImp.getMediaType();
/* 1346 */           if ((mediaType != null) && (mediaType.equals(RongCallCommon.CallMediaType.VIDEO))) {
/* 1347 */             remoteVideo = RongCallManager.this.setupRemoteVideo(userId);
/* 1348 */             RongCallManager.this.updateParticipantVideo(userId, remoteVideo);
/*      */           }
/* 1350 */           if (RongCallManager.this.callListener != null)
/* 1351 */             RongCallManager.this.callListener.onRemoteUserJoined(userId, mediaType, remoteVideo);
/*      */         } else {
/* 1353 */           RLog.e(RongCallManager.TAG, "can not find userId as " + msg.obj + ", cache it.");
/* 1354 */           RongCallManager.this.unknownMediaIdList.add((String)msg.obj);
/*      */         }
/* 1356 */         break;
/*      */       case 204:
/* 1358 */         userId = RongCallManager.this.getUserIdByMediaId((String)msg.obj);
/*      */ 
/* 1360 */         if (userId != null) {
/* 1361 */           RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.IDLE);
/* 1362 */           if (RongCallManager.this.shouldTerminateCall(userId)) {
/* 1363 */             if (RongCallManager.this.callListener != null) {
/* 1364 */               RongCallManager.this.callSessionImp.setEndTime(System.currentTimeMillis());
/* 1365 */               RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), RongCallCommon.CallDisconnectedReason.REMOTE_HANGUP);
/*      */             }
/* 1367 */             RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/*      */           } else {
/* 1369 */             if (RongCallManager.this.callListener == null) break;
/* 1370 */             RongCallManager.this.callListener.onRemoteUserLeft(userId, RongCallCommon.CallDisconnectedReason.HANGUP);
/*      */           }
/*      */         } else {
/* 1373 */           RLog.e(RongCallManager.TAG, "media : " + msg.obj + " had been deleted when RECEIVED_HANG_UP_MSG");
/*      */         }
/* 1375 */         break;
/*      */       case 108:
/* 1377 */         userId = (String)msg.obj;
/* 1378 */         RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.RINGING);
/* 1379 */         break;
/*      */       case 106:
/* 1381 */         message = (io.rong.imlib.model.Message)msg.obj;
/* 1382 */         String mediaId = RongCallManager.this.getMediaIdBySentTime(message.getSentTime());
/* 1383 */         RongCallManager.this.updateMediaId(message.getSenderUserId(), mediaId);
/* 1384 */         if ((RongCallManager.this.unknownMediaIdList.size() <= 0) || (!RongCallManager.this.unknownMediaIdList.contains(mediaId))) break;
/* 1385 */         RLog.e(RongCallManager.TAG, "handle cached mediaId : " + mediaId);
/* 1386 */         RongCallManager.this.updateParticipantCallStatus(message.getSenderUserId(), RongCallCommon.CallStatus.CONNECTED);
/* 1387 */         RongCallManager.this.cancelTimerTask(message.getSenderUserId());
/* 1388 */         SurfaceView remoteVideo = null;
/* 1389 */         RongCallCommon.CallMediaType mediaType = RongCallManager.this.callSessionImp.getMediaType();
/* 1390 */         if ((mediaType != null) && (mediaType.equals(RongCallCommon.CallMediaType.VIDEO))) {
/* 1391 */           remoteVideo = RongCallManager.this.setupRemoteVideo(message.getSenderUserId());
/* 1392 */           RongCallManager.this.updateParticipantVideo(message.getSenderUserId(), remoteVideo);
/*      */         }
/* 1394 */         if (RongCallManager.this.callListener != null)
/* 1395 */           RongCallManager.this.callListener.onRemoteUserJoined(message.getSenderUserId(), mediaType, remoteVideo);
/* 1396 */         RongCallManager.this.unknownMediaIdList.remove(mediaId);
/* 1397 */         break;
/*      */       case 401:
/*      */       case 403:
/* 1401 */         if (RongCallManager.this.callListener != null) {
/* 1402 */           RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), RongCallCommon.CallDisconnectedReason.NETWORK_ERROR);
/*      */         }
/* 1404 */         RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/* 1405 */         break;
/*      */       case 601:
/* 1407 */         RongCallManager.this.voIPEngine.startServerRecording(RongCallManager.this.callSessionImp.getDynamicKey());
/* 1408 */         break;
/*      */       case 602:
/* 1410 */         RongCallManager.this.voIPEngine.stopServerRecording(RongCallManager.this.callSessionImp.getDynamicKey());
/* 1411 */         break;
/*      */       case 603:
/* 1413 */         RongCallManager.this.voIPEngine.getServerRecordingStatus();
/* 1414 */         break;
/*      */       case 604:
/* 1416 */         if (RongCallManager.this.mCallRecordListener == null) break;
/* 1417 */         RongCallManager.this.mCallRecordListener.onServerStartRecording(RongCallCommon.ServerRecordingErrorCode.valueOf(msg.arg1)); break;
/*      */       case 605:
/* 1421 */         if (RongCallManager.this.mCallRecordListener == null) break;
/* 1422 */         RongCallManager.this.mCallRecordListener.onServerStopRecording(RongCallCommon.ServerRecordingErrorCode.valueOf(msg.arg1)); break;
/*      */       case 606:
/* 1426 */         if (RongCallManager.this.mCallRecordListener == null) break;
/* 1427 */         RongCallManager.this.mCallRecordListener.onServerFetchRecordingStatus((msg.arg1 == 0) && (msg.arg2 == 1), RongCallCommon.ServerRecordingErrorCode.valueOf(msg.arg1)); break;
/*      */       }
/*      */ 
/* 1433 */       return true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ConnectingState extends State
/*      */   {
/*      */     private ConnectingState()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void enter()
/*      */     {
/* 1035 */       RLog.d(RongCallManager.TAG, "[" + getName() + "] enter");
/*      */     }
/*      */ 
/*      */     public boolean processMessage(android.os.Message msg)
/*      */     {
/* 1040 */       RLog.d(RongCallManager.TAG, "[" + getName() + "] processMessage : " + msg.what);
/*      */       String userId;
/*      */       io.rong.imlib.model.Message message;
/*      */       CallInviteMessage inviteMessage;
/*      */       CallModifyMemberMessage modifyMemberMessage;
/* 1043 */       switch (msg.what) {
/*      */       case 402:
/* 1045 */         userId = (String)msg.obj;
/* 1046 */         RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.IDLE);
/* 1047 */         if (RongCallManager.this.shouldTerminateCall(userId)) {
/* 1048 */           RongCallManager.this.sendHangupMessage(RongCallManager.this.callSessionImp.getConversationType(), RongCallManager.this.callSessionImp.getTargetId(), RongCallManager.this.callSessionImp.getCallId(), RongCallCommon.CallDisconnectedReason.NO_RESPONSE, null);
/* 1049 */           if (RongCallManager.this.callListener != null)
/* 1050 */             RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), RongCallCommon.CallDisconnectedReason.NO_RESPONSE);
/* 1051 */           RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/*      */         } else {
/* 1053 */           if (RongCallManager.this.callListener == null) break;
/* 1054 */           RongCallManager.this.callListener.onRemoteUserLeft(userId, RongCallCommon.CallDisconnectedReason.NO_RESPONSE); } break;
/*      */       case 105:
/* 1058 */         message = (io.rong.imlib.model.Message)msg.obj;
/* 1059 */         inviteMessage = (CallInviteMessage)message.getContent();
/*      */ 
/* 1061 */         for (String newUserId : inviteMessage.getInviteUserIds()) {
/* 1062 */           if (RongCallManager.this.callSessionImp.getSelfUserId().equals(newUserId)) {
/* 1063 */             RongCallManager.this.updateCallRongLog();
/* 1064 */             RongCallManager.this.sendHangupMessage(message.getConversationType(), message.getTargetId(), inviteMessage.getCallId(), RongCallCommon.CallDisconnectedReason.BUSY_LINE, null);
/* 1065 */             break;
/*      */           }
/*      */         }
/* 1068 */         break;
/*      */       case 107:
/* 1070 */         message = (io.rong.imlib.model.Message)msg.obj;
/* 1071 */         modifyMemberMessage = (CallModifyMemberMessage)message.getContent();
/*      */ 
/* 1073 */         if (modifyMemberMessage.getCallId().equals(RongCallManager.this.callSessionImp.getCallId()))
/*      */         {
/* 1075 */           for (String id : modifyMemberMessage.getInvitedList()) {
/* 1076 */             CallUserProfile profile = new CallUserProfile();
/* 1077 */             profile.setUserId(id);
/* 1078 */             profile.setMediaType(modifyMemberMessage.getMediaType());
/* 1079 */             profile.setCallStatus(RongCallCommon.CallStatus.INCOMING);
/* 1080 */             RongCallManager.this.callSessionImp.getParticipantProfileList().add(profile);
/* 1081 */             if (RongCallManager.this.callListener != null)
/* 1082 */               RongCallManager.this.callListener.onRemoteUserInvited(id, modifyMemberMessage.getMediaType());
/*      */           }
/*      */         }
/*      */         else {
/* 1086 */           for (String id : modifyMemberMessage.getInvitedList()) {
/* 1087 */             if (RongCallManager.this.callSessionImp.getSelfUserId().equals(id)) {
/* 1088 */               RongCallManager.this.updateCallRongLog();
/* 1089 */               RongCallManager.this.sendHangupMessage(message.getConversationType(), message.getTargetId(), modifyMemberMessage.getCallId(), RongCallCommon.CallDisconnectedReason.BUSY_LINE, null);
/*      */             }
/*      */           }
/*      */         }
/* 1093 */         break;
/*      */       case 109:
/* 1095 */         message = (io.rong.imlib.model.Message)msg.obj;
/* 1096 */         CallHangupMessage hangupMessage = (CallHangupMessage)message.getContent();
/* 1097 */         userId = message.getSenderUserId();
/* 1098 */         RongCallCommon.CallDisconnectedReason reason = hangupMessage.getHangupReason();
/* 1099 */         reason = RongCallManager.this.transferRemoteReason(reason);
/* 1100 */         if (!hangupMessage.getCallId().equals(RongCallManager.this.callSessionImp.getCallId())) break;
/* 1101 */         RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.IDLE);
/* 1102 */         RongCallManager.this.cancelTimerTask(userId);
/* 1103 */         if (RongCallManager.this.shouldTerminateCall(userId)) {
/* 1104 */           if (RongCallManager.this.callListener != null)
/* 1105 */             RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), reason);
/* 1106 */           RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/*      */         } else {
/* 1108 */           if (RongCallManager.this.callListener == null) break;
/* 1109 */           RongCallManager.this.callListener.onRemoteUserLeft(userId, hangupMessage.getHangupReason()); } break;
/*      */       case 201:
/* 1114 */         RongCallManager.this.cancelTimerTask(RongCallManager.this.callSessionImp.getSelfUserId());
/* 1115 */         RongCallManager.this.updateParticipantCallStatus(RongCallManager.this.callSessionImp.getSelfUserId(), RongCallCommon.CallStatus.CONNECTED);
/* 1116 */         RongCallCommon.CallMediaType mediaType = RongCallManager.this.callSessionImp.getMediaType();
/* 1117 */         SurfaceView localVideo = RongCallManager.this.callSessionImp.getLocalVideo();
/* 1118 */         if ((mediaType != null) && (mediaType.equals(RongCallCommon.CallMediaType.VIDEO)) && (localVideo == null)) {
/* 1119 */           localVideo = RongCallManager.this.setupLocalVideo();
/* 1120 */           RongCallManager.this.callSessionImp.setLocalVideo(localVideo);
/*      */         }
/* 1122 */         if (RongCallManager.this.callListener != null)
/* 1123 */           RongCallManager.this.callListener.onCallConnected(RongCallManager.this.getCallSession(), localVideo);
/* 1124 */         RongCallManager.this.transitionTo(RongCallManager.this.mConnectedState);
/* 1125 */         break;
/*      */       case 108:
/* 1127 */         userId = (String)msg.obj;
/* 1128 */         RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.RINGING);
/* 1129 */         break;
/*      */       case 401:
/* 1131 */         RongCallManager.this.updateParticipantCallStatus(RongCallManager.this.callSessionImp.getSelfUserId(), RongCallCommon.CallStatus.IDLE);
/* 1132 */         if (RongCallManager.this.callListener != null)
/* 1133 */           RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), RongCallCommon.CallDisconnectedReason.NETWORK_ERROR);
/* 1134 */         RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/* 1135 */         break;
/*      */       case 403:
/* 1137 */         RongCallManager.this.updateParticipantCallStatus(RongCallManager.this.callSessionImp.getSelfUserId(), RongCallCommon.CallStatus.IDLE);
/* 1138 */         if (RongCallManager.this.callListener != null)
/* 1139 */           RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), RongCallCommon.CallDisconnectedReason.NETWORK_ERROR);
/* 1140 */         RongCallManager.this.sendHangupMessage(RongCallManager.this.callSessionImp.getConversationType(), RongCallManager.this.callSessionImp.getTargetId(), RongCallManager.this.callSessionImp.getCallId(), RongCallCommon.CallDisconnectedReason.NETWORK_ERROR, null);
/* 1141 */         RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/* 1142 */         break;
/*      */       case 103:
/* 1144 */         if (RongCallManager.this.callListener != null)
/* 1145 */           RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), RongCallCommon.CallDisconnectedReason.HANGUP);
/* 1146 */         RongCallManager.this.updateParticipantCallStatus(RongCallManager.this.callSessionImp.getSelfUserId(), RongCallCommon.CallStatus.IDLE);
/* 1147 */         RongCallManager.this.sendHangupMessage(RongCallManager.this.callSessionImp.getConversationType(), RongCallManager.this.callSessionImp.getTargetId(), RongCallManager.this.callSessionImp.getCallId(), RongCallCommon.CallDisconnectedReason.HANGUP, null);
/* 1148 */         RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/* 1149 */         break;
/*      */       case 203:
/* 1152 */         break;
/*      */       case 106:
/* 1154 */         message = (io.rong.imlib.model.Message)msg.obj;
/* 1155 */         String mediaId = RongCallManager.this.getMediaIdBySentTime(message.getSentTime());
/* 1156 */         RongCallManager.this.updateMediaId(message.getSenderUserId(), mediaId);
/* 1157 */         break;
/*      */       case 104:
/* 1159 */         List newUserList = (ArrayList)msg.obj;
/* 1160 */         RongCallManager.this.sendModifyMemberMessage(newUserList, new RongCallManager.SignalCallback(newUserList)
/*      */         {
/*      */           public void onError() {
/* 1163 */             for (String userId : this.val$newUserList) {
/* 1164 */               RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.IDLE);
/* 1165 */               if (RongCallManager.this.callListener != null)
/* 1166 */                 RongCallManager.this.callListener.onRemoteUserLeft(userId, RongCallCommon.CallDisconnectedReason.NETWORK_ERROR);
/*      */             }
/*      */           }
/*      */ 
/*      */           public void onSuccess(String userId, String mediaId)
/*      */           {
/* 1172 */             for (String id : this.val$newUserList) {
/* 1173 */               RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.INCOMING);
/* 1174 */               RongCallManager.this.setupTimerTask(id, 402, 60000);
/*      */             }
/*      */           }
/*      */         });
/* 1178 */         break;
/*      */       case 601:
/*      */       case 602:
/*      */       case 603:
/*      */       case 604:
/*      */       case 605:
/*      */       case 606:
/* 1185 */         return false;
/*      */       }
/*      */ 
/* 1189 */       return true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class OutgoingState extends State
/*      */   {
/*      */     private OutgoingState()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void enter()
/*      */     {
/*  867 */       RLog.d(RongCallManager.TAG, "[" + getName() + "] enter");
/*  868 */       SurfaceView localVideo = null;
/*  869 */       if (RongCallManager.this.callListener != null) {
/*  870 */         if (RongCallManager.this.callSessionImp.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO)) {
/*  871 */           localVideo = RongCallManager.this.setupLocalVideo();
/*  872 */           RongCallManager.this.callSessionImp.setLocalVideo(localVideo);
/*  873 */           RongCallManager.this.voIPEngine.setVideoProfile(RongCallManager.this.videoProfile);
/*  874 */           RongCallManager.this.voIPEngine.startPreview();
/*      */         }
/*  876 */         RongCallManager.this.callListener.onCallOutgoing(RongCallManager.this.getCallSession(), localVideo);
/*      */       }
/*  878 */       RongCallManager.this.callSessionImp.setStartTime(System.currentTimeMillis());
/*      */     }
/*      */ 
/*      */     public boolean processMessage(android.os.Message msg)
/*      */     {
/*  883 */       RLog.d(RongCallManager.TAG, "[" + getName() + "] processMessage : " + msg.what);
/*      */       String userId;
/*      */       io.rong.imlib.model.Message message;
/*      */       CallModifyMemberMessage modifyMemberMessage;
/*  887 */       switch (msg.what) {
/*      */       case 104:
/*  889 */         List newUserList = (ArrayList)msg.obj;
/*  890 */         RongCallManager.this.sendModifyMemberMessage(newUserList, new RongCallManager.SignalCallback(newUserList)
/*      */         {
/*      */           public void onError() {
/*  893 */             for (String userId : this.val$newUserList) {
/*  894 */               RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.IDLE);
/*  895 */               if (RongCallManager.this.callListener != null)
/*  896 */                 RongCallManager.this.callListener.onRemoteUserLeft(userId, RongCallCommon.CallDisconnectedReason.NETWORK_ERROR);
/*      */             }
/*      */           }
/*      */ 
/*      */           public void onSuccess(String userId, String mediaId)
/*      */           {
/*  902 */             for (String id : this.val$newUserList) {
/*  903 */               RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.INCOMING);
/*  904 */               RongCallManager.this.setupTimerTask(id, 402, 60000);
/*      */             }
/*      */           }
/*      */         });
/*  908 */         break;
/*      */       case 108:
/*  910 */         userId = (String)msg.obj;
/*  911 */         RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.RINGING);
/*      */ 
/*  913 */         if (RongCallManager.this.callListener == null) break;
/*  914 */         RongCallManager.this.callListener.onRemoteUserRinging(userId); break;
/*      */       case 106:
/*  917 */         message = (io.rong.imlib.model.Message)msg.obj;
/*  918 */         String mediaId = RongCallManager.this.getMediaIdBySentTime(message.getSentTime());
/*  919 */         RongCallManager.this.updateMediaId(message.getSenderUserId(), mediaId);
/*  920 */         RongCallManager.this.joinChannel(RongCallManager.access$6400(RongCallManager.this, RongCallManager.this.callSessionImp.getSelfUserId()));
/*  921 */         RongCallManager.this.transitionTo(RongCallManager.this.mConnectingState);
/*  922 */         break;
/*      */       case 105:
/*  924 */         message = (io.rong.imlib.model.Message)msg.obj;
/*  925 */         CallInviteMessage inviteMessage = (CallInviteMessage)message.getContent();
/*      */ 
/*  927 */         boolean isInvited = false;
/*  928 */         for (String id : inviteMessage.getInviteUserIds()) {
/*  929 */           if (RongCallManager.this.callSessionImp.getSelfUserId().equals(id)) {
/*  930 */             isInvited = true;
/*  931 */             break;
/*      */           }
/*      */         }
/*  934 */         if (!isInvited) break;
/*  935 */         RongCallManager.this.updateCallRongLog();
/*  936 */         RongCallManager.this.sendHangupMessage(message.getConversationType(), message.getTargetId(), inviteMessage.getCallId(), RongCallCommon.CallDisconnectedReason.BUSY_LINE, null); break;
/*      */       case 107:
/*  940 */         message = (io.rong.imlib.model.Message)msg.obj;
/*  941 */         modifyMemberMessage = (CallModifyMemberMessage)message.getContent();
/*      */ 
/*  943 */         if (modifyMemberMessage.getCallId().equals(RongCallManager.this.callSessionImp.getCallId()))
/*      */         {
/*  945 */           for (String id : modifyMemberMessage.getInvitedList()) {
/*  946 */             CallUserProfile profile = new CallUserProfile();
/*  947 */             profile.setUserId(id);
/*  948 */             profile.setMediaType(modifyMemberMessage.getMediaType());
/*  949 */             profile.setCallStatus(RongCallCommon.CallStatus.INCOMING);
/*  950 */             RongCallManager.this.callSessionImp.getParticipantProfileList().add(profile);
/*  951 */             if (RongCallManager.this.callListener != null)
/*  952 */               RongCallManager.this.callListener.onRemoteUserInvited(id, modifyMemberMessage.getMediaType());
/*      */           }
/*      */         }
/*      */         else {
/*  956 */           for (String id : modifyMemberMessage.getInvitedList()) {
/*  957 */             if (RongCallManager.this.callSessionImp.getSelfUserId().equals(id)) {
/*  958 */               RongCallManager.this.updateCallRongLog();
/*  959 */               RongCallManager.this.sendHangupMessage(message.getConversationType(), message.getTargetId(), modifyMemberMessage.getCallId(), RongCallCommon.CallDisconnectedReason.BUSY_LINE, null);
/*      */             }
/*      */           }
/*      */         }
/*  963 */         break;
/*      */       case 402:
/*  965 */         userId = (String)msg.obj;
/*  966 */         RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.IDLE);
/*  967 */         if (RongCallManager.this.shouldTerminateCall(userId)) {
/*  968 */           RongCallManager.this.voIPEngine.stopPreview();
/*  969 */           RongCallManager.this.sendHangupMessage(RongCallManager.this.callSessionImp.getConversationType(), RongCallManager.this.callSessionImp.getTargetId(), RongCallManager.this.callSessionImp.getCallId(), RongCallCommon.CallDisconnectedReason.REMOTE_NO_RESPONSE, null);
/*  970 */           if (RongCallManager.this.callListener != null)
/*  971 */             RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), RongCallCommon.CallDisconnectedReason.REMOTE_NO_RESPONSE);
/*  972 */           RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/*      */         } else {
/*  974 */           if (RongCallManager.this.callListener == null) break;
/*  975 */           RongCallManager.this.callListener.onRemoteUserLeft(userId, RongCallCommon.CallDisconnectedReason.NO_RESPONSE); } break;
/*      */       case 103:
/*  979 */         RongCallManager.this.voIPEngine.stopPreview();
/*  980 */         RongCallManager.this.sendHangupMessage(RongCallManager.this.callSessionImp.getConversationType(), RongCallManager.this.callSessionImp.getTargetId(), RongCallManager.this.callSessionImp.getCallId(), RongCallCommon.CallDisconnectedReason.CANCEL, null);
/*  981 */         if (RongCallManager.this.callListener != null)
/*  982 */           RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), RongCallCommon.CallDisconnectedReason.CANCEL);
/*  983 */         RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/*  984 */         break;
/*      */       case 109:
/*  986 */         message = (io.rong.imlib.model.Message)msg.obj;
/*  987 */         CallHangupMessage hangupMessage = (CallHangupMessage)message.getContent();
/*  988 */         userId = message.getSenderUserId();
/*  989 */         RongCallCommon.CallDisconnectedReason reason = hangupMessage.getHangupReason();
/*  990 */         reason = RongCallManager.this.transferRemoteReason(reason);
/*  991 */         if (!hangupMessage.getCallId().equals(RongCallManager.this.callSessionImp.getCallId())) break;
/*  992 */         RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.IDLE);
/*  993 */         RongCallManager.this.cancelTimerTask(userId);
/*  994 */         if (RongCallManager.this.shouldTerminateCall(userId)) {
/*  995 */           RongCallManager.this.voIPEngine.stopPreview();
/*  996 */           if (RongCallManager.this.callListener != null)
/*  997 */             RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), reason);
/*  998 */           RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/*      */         } else {
/* 1000 */           if (RongCallManager.this.callListener == null) break;
/* 1001 */           RongCallManager.this.callListener.onRemoteUserLeft(userId, hangupMessage.getHangupReason()); } break;
/*      */       case 401:
/*      */       case 403:
/* 1007 */         RongCallManager.this.voIPEngine.stopPreview();
/* 1008 */         RongCallManager.this.updateParticipantCallStatus(RongCallManager.this.callSessionImp.getSelfUserId(), RongCallCommon.CallStatus.IDLE);
/* 1009 */         if (RongCallManager.this.callListener != null)
/* 1010 */           RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), RongCallCommon.CallDisconnectedReason.NETWORK_ERROR);
/* 1011 */         RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/* 1012 */         break;
/*      */       case 203:
/* 1015 */         break;
/*      */       case 601:
/*      */       case 602:
/*      */       case 603:
/*      */       case 604:
/*      */       case 605:
/*      */       case 606:
/* 1022 */         return false;
/*      */       }
/*      */ 
/* 1026 */       return true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class IncomingState extends State
/*      */   {
/*      */     private IncomingState()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void enter()
/*      */     {
/*  721 */       RLog.d(RongCallManager.TAG, "[" + getName() + "] enter");
/*  722 */       RongCallManager.this.callSessionImp.setStartTime(System.currentTimeMillis());
/*      */     }
/*      */ 
/*      */     public boolean processMessage(android.os.Message msg)
/*      */     {
/*  727 */       RLog.d(RongCallManager.TAG, "[" + getName() + "] processMessage : " + msg.what);
/*      */       io.rong.imlib.model.Message message;
/*      */       CallModifyMemberMessage modifyMemberMessage;
/*      */       String userId;
/*  731 */       switch (msg.what) {
/*      */       case 102:
/*  733 */         RongCallCommon.CallMediaType mediaType = RongCallManager.this.callSessionImp.getMediaType();
/*  734 */         SurfaceView localVideo = RongCallManager.this.callSessionImp.getLocalVideo();
/*  735 */         if ((mediaType != null) && (mediaType.equals(RongCallCommon.CallMediaType.VIDEO)) && (localVideo == null)) {
/*  736 */           localVideo = RongCallManager.this.setupLocalVideo();
/*  737 */           RongCallManager.this.callSessionImp.setLocalVideo(localVideo);
/*      */         }
/*  739 */         RongCallManager.this.sendAcceptMessage(RongCallManager.this.callSessionImp.getCallId(), RongCallManager.this.callSessionImp.getMediaType(), new RongCallManager.SignalCallback()
/*      */         {
/*      */           public void onError() {
/*  742 */             RongCallManager.this.getHandler().sendEmptyMessage(401);
/*      */           }
/*      */ 
/*      */           public void onSuccess(String userId, String mediaId)
/*      */           {
/*  747 */             RongCallManager.this.updateMediaId(userId, mediaId);
/*  748 */             RongCallManager.this.joinChannel(mediaId);
/*      */           }
/*      */         });
/*  751 */         RongCallManager.this.transitionTo(RongCallManager.this.mConnectingState);
/*  752 */         break;
/*      */       case 402:
/*  754 */         if (RongCallManager.this.callListener != null) {
/*  755 */           RongCallManager.this.callSessionImp.setEndTime(System.currentTimeMillis());
/*  756 */           RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), RongCallCommon.CallDisconnectedReason.NO_RESPONSE);
/*      */         }
/*  758 */         RongCallManager.this.sendHangupMessage(RongCallManager.this.callSessionImp.getConversationType(), RongCallManager.this.callSessionImp.getTargetId(), RongCallManager.this.callSessionImp.getCallId(), RongCallCommon.CallDisconnectedReason.NO_RESPONSE, null);
/*  759 */         RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/*  760 */         break;
/*      */       case 105:
/*  762 */         message = (io.rong.imlib.model.Message)msg.obj;
/*  763 */         CallInviteMessage inviteMessage = (CallInviteMessage)message.getContent();
/*      */ 
/*  765 */         boolean isInvited = false;
/*  766 */         for (String userId : inviteMessage.getInviteUserIds()) {
/*  767 */           if (RongCallManager.this.callSessionImp.getSelfUserId().equals(userId)) {
/*  768 */             isInvited = true;
/*  769 */             break;
/*      */           }
/*      */         }
/*  772 */         if (!isInvited) break;
/*  773 */         RongCallManager.this.updateCallRongLog();
/*  774 */         RongCallManager.this.sendHangupMessage(message.getConversationType(), message.getTargetId(), inviteMessage.getCallId(), RongCallCommon.CallDisconnectedReason.BUSY_LINE, null); break;
/*      */       case 107:
/*  778 */         message = (io.rong.imlib.model.Message)msg.obj;
/*  779 */         modifyMemberMessage = (CallModifyMemberMessage)message.getContent();
/*      */ 
/*  781 */         if (modifyMemberMessage.getCallId().equals(RongCallManager.this.callSessionImp.getCallId()))
/*      */         {
/*  784 */           for (String id : modifyMemberMessage.getInvitedList()) {
/*  785 */             CallUserProfile profile = new CallUserProfile();
/*  786 */             profile.setUserId(id);
/*  787 */             profile.setMediaType(modifyMemberMessage.getMediaType());
/*  788 */             profile.setCallStatus(RongCallCommon.CallStatus.INCOMING);
/*  789 */             RongCallManager.this.callSessionImp.getParticipantProfileList().add(profile);
/*  790 */             if (RongCallManager.this.callListener != null)
/*  791 */               RongCallManager.this.callListener.onRemoteUserInvited(id, modifyMemberMessage.getMediaType());
/*      */           }
/*      */         }
/*      */         else {
/*  795 */           for (String id : modifyMemberMessage.getInvitedList()) {
/*  796 */             if (RongCallManager.this.callSessionImp.getSelfUserId().equals(id)) {
/*  797 */               RongCallManager.this.updateCallRongLog();
/*  798 */               RongCallManager.this.sendHangupMessage(message.getConversationType(), message.getTargetId(), modifyMemberMessage.getCallId(), RongCallCommon.CallDisconnectedReason.BUSY_LINE, null);
/*      */             }
/*      */           }
/*      */         }
/*  802 */         break;
/*      */       case 103:
/*  804 */         RongCallManager.this.cancelTimerTask(RongCallManager.this.callSessionImp.getSelfUserId());
/*  805 */         RongCallManager.this.sendHangupMessage(RongCallManager.this.callSessionImp.getConversationType(), RongCallManager.this.callSessionImp.getTargetId(), RongCallManager.this.callSessionImp.getCallId(), RongCallCommon.CallDisconnectedReason.REJECT, null);
/*  806 */         if (RongCallManager.this.callListener != null) {
/*  807 */           RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), RongCallCommon.CallDisconnectedReason.REJECT);
/*      */         }
/*  809 */         RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/*  810 */         break;
/*      */       case 109:
/*  812 */         message = (io.rong.imlib.model.Message)msg.obj;
/*  813 */         CallHangupMessage hangupMessage = (CallHangupMessage)message.getContent();
/*  814 */         RongCallCommon.CallDisconnectedReason reason = hangupMessage.getHangupReason();
/*  815 */         reason = RongCallManager.this.transferRemoteReason(reason);
/*  816 */         userId = message.getSenderUserId();
/*  817 */         if (!hangupMessage.getCallId().equals(RongCallManager.this.callSessionImp.getCallId())) break;
/*  818 */         RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.IDLE);
/*  819 */         if (RongCallManager.this.shouldTerminateCall(userId)) {
/*  820 */           if (RongCallManager.this.callListener != null) {
/*  821 */             RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), reason);
/*      */           }
/*  823 */           RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/*      */         } else {
/*  825 */           if (RongCallManager.this.callListener == null) break;
/*  826 */           RongCallManager.this.callListener.onRemoteUserLeft(userId, hangupMessage.getHangupReason()); } break;
/*      */       case 108:
/*  831 */         userId = (String)msg.obj;
/*  832 */         RongCallManager.this.updateParticipantCallStatus(userId, RongCallCommon.CallStatus.RINGING);
/*  833 */         break;
/*      */       case 401:
/*  835 */         if (RongCallManager.this.callListener != null) {
/*  836 */           RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), RongCallCommon.CallDisconnectedReason.NETWORK_ERROR);
/*      */         }
/*  838 */         RongCallManager.this.updateParticipantCallStatus(RongCallManager.this.callSessionImp.getSelfUserId(), RongCallCommon.CallStatus.IDLE);
/*  839 */         RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/*  840 */         break;
/*      */       case 106:
/*  842 */         message = (io.rong.imlib.model.Message)msg.obj;
/*  843 */         String mediaId = RongCallManager.this.getMediaIdBySentTime(message.getSentTime());
/*  844 */         RongCallManager.this.updateMediaId(message.getSenderUserId(), mediaId);
/*  845 */         break;
/*      */       case 601:
/*      */       case 602:
/*      */       case 603:
/*      */       case 604:
/*      */       case 605:
/*      */       case 606:
/*  853 */         return false;
/*      */       }
/*      */ 
/*  858 */       return true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class IdleState extends State
/*      */   {
/*      */     private IdleState()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void enter()
/*      */     {
/*  580 */       RongCallManager.this.resetTimer();
/*  581 */       RongCallManager.this.unknownMediaIdList.clear();
/*  582 */       if (RongCallManager.this.callSessionImp != null)
/*      */       {
/*  586 */         if (RongCallManager.this.callSessionImp.getCallId() != null)
/*  587 */           RongCallManager.this.leaveChannel();
/*  588 */         RongCallManager.this.callSessionImp.setCallId(null);
/*      */       }
/*      */ 
/*  591 */       RongCallManager.access$502(RongCallManager.this, null);
/*  592 */       RLog.d(RongCallManager.TAG, "[" + getName() + "] enter, myUserId = " + RongCallManager.this.getMyUserId());
/*      */     }
/*      */ 
/*      */     public boolean processMessage(android.os.Message msg)
/*      */     {
/*  597 */       RLog.d(RongCallManager.TAG, "[" + getName() + "] processMessage : " + msg.what);
/*      */       String myUserId;
/*      */       io.rong.imlib.model.Message message;
/*  599 */       switch (msg.what) {
/*      */       case 101:
/*  601 */         myUserId = RongCallManager.this.getMyUserId();
/*  602 */         RongCallManager.access$502(RongCallManager.this, (CallSessionImp)msg.obj);
/*  603 */         RongCallManager.this.callSessionImp.setSelfUserId(myUserId);
/*  604 */         RongCallManager.this.updateParticipantCallStatus(myUserId, RongCallCommon.CallStatus.OUTGOING);
/*  605 */         List userList = new ArrayList();
/*  606 */         for (CallUserProfile profile : RongCallManager.this.callSessionImp.getParticipantProfileList()) {
/*  607 */           if (profile.getUserId().equals(myUserId)) {
/*  608 */             RongCallManager.this.setupTimerTask(myUserId, 402, 60000);
/*      */           } else {
/*  610 */             RongCallManager.this.setupTimerTask(profile.getUserId(), 402, 60000);
/*  611 */             RongCallManager.this.updateParticipantCallStatus(profile.getUserId(), RongCallCommon.CallStatus.INCOMING);
/*  612 */             userList.add(profile.getUserId());
/*      */           }
/*      */         }
/*  615 */         RongCallManager.this.sendInviteMessage(userList, new RongCallManager.SignalCallback()
/*      */         {
/*      */           public void onError() {
/*  618 */             RongCallManager.this.getHandler().sendEmptyMessage(401);
/*      */           }
/*      */ 
/*      */           public void onSuccess(String userId, String mediaId)
/*      */           {
/*  623 */             RongCallManager.this.updateMediaId(userId, mediaId);
/*      */           }
/*      */         });
/*  626 */         RongCallManager.this.transitionTo(RongCallManager.this.mOutgoingState);
/*  627 */         break;
/*      */       case 105:
/*  629 */         message = (io.rong.imlib.model.Message)msg.obj;
/*  630 */         CallInviteMessage inviteMessage = (CallInviteMessage)message.getContent();
/*      */ 
/*  632 */         boolean isInvited = false;
/*  633 */         myUserId = RongCallManager.this.getMyUserId();
/*  634 */         for (String userId : inviteMessage.getInviteUserIds()) {
/*  635 */           if (myUserId.equals(userId)) {
/*  636 */             isInvited = true;
/*  637 */             break;
/*      */           }
/*      */         }
/*      */ 
/*  641 */         if (!isInvited) break;
/*  642 */         RongCallManager.this.initializeCallSessionFromInvite(message);
/*  643 */         for (CallUserProfile profile : RongCallManager.this.callSessionImp.getParticipantProfileList()) {
/*  644 */           if (!profile.getCallStatus().equals(RongCallCommon.CallStatus.CONNECTED)) {
/*  645 */             RongCallManager.this.setupTimerTask(profile.getUserId(), 402, 60000);
/*      */           }
/*      */         }
/*  648 */         RongCallManager.this.sendRingingMessage(RongCallManager.this.callSessionImp.getCallId());
/*  649 */         RongCallManager.this.transitionTo(RongCallManager.this.mIncomingState);
/*  650 */         if (RongCallManager.receivedCallListener == null) break;
/*  651 */         RongCallManager.receivedCallListener.onReceivedCall(RongCallManager.this.getCallSession()); break;
/*      */       case 107:
/*  655 */         message = (io.rong.imlib.model.Message)msg.obj;
/*  656 */         CallModifyMemberMessage modifyMemberMessage = (CallModifyMemberMessage)message.getContent();
/*      */ 
/*  658 */         myUserId = RongCallManager.this.getMyUserId();
/*  659 */         boolean invitedSelf = false;
/*  660 */         for (String id : modifyMemberMessage.getInvitedList()) {
/*  661 */           if (myUserId.equals(id)) {
/*  662 */             invitedSelf = true;
/*  663 */             RongCallManager.this.initializeCallInfoFromModifyMember(message);
/*  664 */             break;
/*      */           }
/*      */         }
/*  667 */         if (!invitedSelf) break;
/*  668 */         for (CallUserProfile profile : RongCallManager.this.callSessionImp.getParticipantProfileList()) {
/*  669 */           if (!profile.getCallStatus().equals(RongCallCommon.CallStatus.CONNECTED)) {
/*  670 */             RongCallManager.this.setupTimerTask(profile.getUserId(), 402, 60000);
/*      */           }
/*      */         }
/*  673 */         RongCallManager.this.sendRingingMessage(RongCallManager.this.callSessionImp.getCallId());
/*  674 */         RongCallManager.this.transitionTo(RongCallManager.this.mIncomingState);
/*  675 */         if (RongCallManager.receivedCallListener == null) break;
/*  676 */         RongCallManager.receivedCallListener.onReceivedCall(RongCallManager.this.getCallSession()); break;
/*      */       case 601:
/*  680 */         if (RongCallManager.this.mCallRecordListener == null) break;
/*  681 */         RongCallManager.this.mCallRecordListener.onServerStartRecording(RongCallCommon.ServerRecordingErrorCode.NOT_IN_CALL); break;
/*      */       case 602:
/*  685 */         if (RongCallManager.this.mCallRecordListener == null) break;
/*  686 */         RongCallManager.this.mCallRecordListener.onServerStopRecording(RongCallCommon.ServerRecordingErrorCode.NOT_IN_CALL); break;
/*      */       case 603:
/*  690 */         if (RongCallManager.this.mCallRecordListener == null) break;
/*  691 */         RongCallManager.this.mCallRecordListener.onServerFetchRecordingStatus(false, RongCallCommon.ServerRecordingErrorCode.NOT_IN_CALL); break;
/*      */       case 604:
/*  695 */         if (RongCallManager.this.mCallRecordListener == null) break;
/*  696 */         RongCallManager.this.mCallRecordListener.onServerStartRecording(RongCallCommon.ServerRecordingErrorCode.NOT_IN_CALL); break;
/*      */       case 605:
/*  700 */         if (RongCallManager.this.mCallRecordListener == null) break;
/*  701 */         RongCallManager.this.mCallRecordListener.onServerStopRecording(RongCallCommon.ServerRecordingErrorCode.NOT_IN_CALL); break;
/*      */       case 606:
/*  705 */         if (RongCallManager.this.mCallRecordListener == null) break;
/*  706 */         RongCallManager.this.mCallRecordListener.onServerStartRecording(RongCallCommon.ServerRecordingErrorCode.NOT_IN_CALL); break;
/*      */       }
/*      */ 
/*  712 */       return true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class UnInitState extends State
/*      */   {
/*      */     private UnInitState()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void enter()
/*      */     {
/*  447 */       super.enter();
/*  448 */       RLog.d(RongCallManager.TAG, "[" + getName() + "] enter");
/*      */     }
/*      */ 
/*      */     public boolean processMessage(android.os.Message msg)
/*      */     {
/*  453 */       RLog.d(RongCallManager.TAG, "[" + getName() + "] processMessage : " + msg.what);
/*      */       io.rong.imlib.model.Message message;
/*      */       String myUserId;
/*      */       boolean isInvited;
/*  455 */       switch (msg.what) {
/*      */       case 101:
/*  457 */         if (RongCallManager.this.initEngine(null)) {
/*  458 */           RongCallManager.this.deferMessage(msg);
/*  459 */           RongCallManager.this.transitionTo(RongCallManager.this.mIdleState); } else {
/*  460 */           if (RongCallManager.this.callListener == null) break;
/*  461 */           RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), RongCallCommon.CallDisconnectedReason.ENGINE_UNSUPPORTED); } break;
/*      */       case 107:
/*  465 */         message = (io.rong.imlib.model.Message)msg.obj;
/*  466 */         CallModifyMemberMessage modifyMemberMessage = (CallModifyMemberMessage)message.getContent();
/*      */ 
/*  468 */         myUserId = RongCallManager.this.getMyUserId();
/*  469 */         isInvited = false;
/*  470 */         for (String id : modifyMemberMessage.getInvitedList()) {
/*  471 */           if (myUserId.equals(id)) {
/*  472 */             isInvited = true;
/*  473 */             break;
/*      */           }
/*      */         }
/*  476 */         if (!isInvited) break;
/*  477 */         RongCallCommon.CallMediaType mediaType = modifyMemberMessage.getMediaType();
/*  478 */         boolean camera = RongCallManager.this.context.checkCallingOrSelfPermission("android.permission.CAMERA") == 0;
/*  479 */         boolean audio = RongCallManager.this.context.checkCallingOrSelfPermission("android.permission.RECORD_AUDIO") == 0;
/*  480 */         boolean needCheckPermissions = false;
/*  481 */         RLog.d(RongCallManager.TAG, "camera permission : " + camera + ", audio permission : " + audio);
/*      */ 
/*  483 */         if ((mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) && (!audio)) {
/*  484 */           needCheckPermissions = true;
/*      */         }
/*  486 */         if ((mediaType.equals(RongCallCommon.CallMediaType.VIDEO)) && ((!camera) || (!audio)))
/*      */         {
/*  488 */           needCheckPermissions = true;
/*      */         }
/*  490 */         if (needCheckPermissions) {
/*  491 */           RongCallManager.this.initializeCallInfoFromModifyMember(message);
/*  492 */           android.os.Message cachedMsg = android.os.Message.obtain();
/*  493 */           cachedMsg.what = msg.what;
/*  494 */           cachedMsg.obj = msg.obj;
/*  495 */           RongCallManager.this.callSessionImp.setCachedMsg(cachedMsg);
/*  496 */           RongCallManager.this.transitionTo(RongCallManager.this.mCheckPermissionState);
/*  497 */         } else if (RongCallManager.this.initEngine(modifyMemberMessage.getEngineType())) {
/*  498 */           RongCallManager.this.deferMessage(msg);
/*  499 */           RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/*      */         } else {
/*  501 */           RongCallManager.this.sendHangupMessage(message.getConversationType(), message.getTargetId(), modifyMemberMessage.getCallId(), RongCallCommon.CallDisconnectedReason.ENGINE_UNSUPPORTED, null);
/*      */         }
/*  503 */         break;
/*      */       case 105:
/*  506 */         message = (io.rong.imlib.model.Message)msg.obj;
/*  507 */         CallInviteMessage inviteMessage = (CallInviteMessage)message.getContent();
/*  508 */         isInvited = false;
/*  509 */         myUserId = RongCallManager.this.getMyUserId();
/*  510 */         for (String userId : inviteMessage.getInviteUserIds()) {
/*  511 */           if (myUserId.equals(userId)) {
/*  512 */             isInvited = true;
/*  513 */             break;
/*      */           }
/*      */         }
/*  516 */         if (!isInvited) break;
/*  517 */         RongCallCommon.CallMediaType mediaType = inviteMessage.getMediaType();
/*  518 */         boolean camera = RongCallManager.this.context.checkCallingOrSelfPermission("android.permission.CAMERA") == 0;
/*  519 */         boolean audio = RongCallManager.this.context.checkCallingOrSelfPermission("android.permission.RECORD_AUDIO") == 0;
/*  520 */         boolean needCheckPermissions = false;
/*  521 */         RLog.d(RongCallManager.TAG, "camera permission : " + camera + ", audio permission : " + audio);
/*      */ 
/*  523 */         if ((mediaType.equals(RongCallCommon.CallMediaType.AUDIO)) && (!audio)) {
/*  524 */           needCheckPermissions = true;
/*      */         }
/*  526 */         if ((mediaType.equals(RongCallCommon.CallMediaType.VIDEO)) && ((!camera) || (!audio)))
/*      */         {
/*  528 */           needCheckPermissions = true;
/*      */         }
/*  530 */         if (needCheckPermissions) {
/*  531 */           RongCallManager.this.initializeCallSessionFromInvite(message);
/*  532 */           android.os.Message cachedMsg = android.os.Message.obtain();
/*  533 */           cachedMsg.what = msg.what;
/*  534 */           cachedMsg.obj = msg.obj;
/*  535 */           RongCallManager.this.callSessionImp.setCachedMsg(cachedMsg);
/*  536 */           RongCallManager.this.transitionTo(RongCallManager.this.mCheckPermissionState);
/*  537 */         } else if (RongCallManager.this.initEngine(inviteMessage.getEngineType())) {
/*  538 */           RongCallManager.this.deferMessage(msg);
/*  539 */           RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/*      */         } else {
/*  541 */           RongCallManager.this.sendHangupMessage(message.getConversationType(), message.getTargetId(), inviteMessage.getCallId(), RongCallCommon.CallDisconnectedReason.ENGINE_UNSUPPORTED, null);
/*      */         }
/*  543 */         break;
/*      */       case 500:
/*  546 */         if (RongCallManager.this.initEngine(RongCallManager.this.callSessionImp.getEngineType())) {
/*  547 */           RongCallManager.this.deferMessage(RongCallManager.this.callSessionImp.getCachedMsg());
/*  548 */           RongCallManager.this.transitionTo(RongCallManager.this.mIdleState);
/*      */         } else {
/*  550 */           RongCallManager.this.sendHangupMessage(RongCallManager.this.callSessionImp.getConversationType(), RongCallManager.this.callSessionImp.getTargetId(), RongCallManager.this.callSessionImp.getCallId(), RongCallCommon.CallDisconnectedReason.ENGINE_UNSUPPORTED, null);
/*  551 */           if (RongCallManager.this.callListener == null) break;
/*  552 */           RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), RongCallCommon.CallDisconnectedReason.ENGINE_UNSUPPORTED); } break;
/*      */       case 601:
/*  556 */         if (RongCallManager.this.mCallRecordListener == null) break;
/*  557 */         RongCallManager.this.mCallRecordListener.onServerStartRecording(RongCallCommon.ServerRecordingErrorCode.NOT_IN_CALL); break;
/*      */       case 602:
/*  561 */         if (RongCallManager.this.mCallRecordListener == null) break;
/*  562 */         RongCallManager.this.mCallRecordListener.onServerStopRecording(RongCallCommon.ServerRecordingErrorCode.NOT_IN_CALL); break;
/*      */       case 603:
/*  566 */         if (RongCallManager.this.mCallRecordListener == null) break;
/*  567 */         RongCallManager.this.mCallRecordListener.onServerFetchRecordingStatus(false, RongCallCommon.ServerRecordingErrorCode.NOT_IN_CALL);
/*      */       }
/*      */ 
/*  571 */       return super.processMessage(msg);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class CheckPermissionState extends State
/*      */   {
/*      */     private CheckPermissionState()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void enter()
/*      */     {
/*  342 */       super.enter();
/*  343 */       RLog.d(RongCallManager.TAG, "[" + getName() + "] enter");
/*  344 */       if (RongCallManager.receivedCallListener != null)
/*  345 */         RongCallManager.receivedCallListener.onCheckPermission(RongCallManager.this.getCallSession());
/*      */     }
/*      */ 
/*      */     public boolean processMessage(android.os.Message msg)
/*      */     {
/*  350 */       RLog.d(RongCallManager.TAG, "[" + getName() + "] processMessage : " + msg.what);
/*      */       io.rong.imlib.model.Message message;
/*      */       boolean isInvited;
/*  352 */       switch (msg.what) {
/*      */       case 107:
/*  354 */         message = (io.rong.imlib.model.Message)msg.obj;
/*  355 */         CallModifyMemberMessage modifyMemberMessage = (CallModifyMemberMessage)message.getContent();
/*      */ 
/*  357 */         isInvited = false;
/*  358 */         for (String userId : modifyMemberMessage.getInvitedList()) {
/*  359 */           if (RongCallManager.this.callSessionImp.getSelfUserId().equals(userId)) {
/*  360 */             isInvited = true;
/*  361 */             break;
/*      */           }
/*      */         }
/*  364 */         if (!isInvited) break;
/*  365 */         RongCallManager.this.updateCallRongLog();
/*  366 */         RongCallManager.this.sendHangupMessage(message.getConversationType(), message.getTargetId(), modifyMemberMessage.getCallId(), RongCallCommon.CallDisconnectedReason.BUSY_LINE, null); break;
/*      */       case 105:
/*  370 */         message = (io.rong.imlib.model.Message)msg.obj;
/*  371 */         CallInviteMessage inviteMessage = (CallInviteMessage)message.getContent();
/*      */ 
/*  373 */         isInvited = false;
/*  374 */         for (String userId : inviteMessage.getInviteUserIds()) {
/*  375 */           if (RongCallManager.this.callSessionImp.getSelfUserId().equals(userId)) {
/*  376 */             isInvited = true;
/*  377 */             break;
/*      */           }
/*      */         }
/*  380 */         if (!isInvited) break;
/*  381 */         RongCallManager.this.updateCallRongLog();
/*  382 */         RongCallManager.this.sendHangupMessage(message.getConversationType(), message.getTargetId(), inviteMessage.getCallId(), RongCallCommon.CallDisconnectedReason.BUSY_LINE, null); break;
/*      */       case 500:
/*  386 */         RongCallManager.this.getHandler().sendEmptyMessage(500);
/*  387 */         RongCallManager.this.transitionTo(RongCallManager.this.mUnInitState);
/*  388 */         break;
/*      */       case 103:
/*      */       case 501:
/*  391 */         RongCallManager.this.sendHangupMessage(RongCallManager.this.callSessionImp.getConversationType(), RongCallManager.this.callSessionImp.getTargetId(), RongCallManager.this.callSessionImp.getCallId(), RongCallCommon.CallDisconnectedReason.REJECT, null);
/*  392 */         if (RongCallManager.this.callListener != null)
/*  393 */           RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), RongCallCommon.CallDisconnectedReason.REJECT);
/*  394 */         RongCallManager.this.transitionTo(RongCallManager.this.mUnInitState);
/*  395 */         break;
/*      */       case 109:
/*  397 */         message = (io.rong.imlib.model.Message)msg.obj;
/*  398 */         CallHangupMessage hangupMessage = (CallHangupMessage)message.getContent();
/*  399 */         RongCallCommon.CallDisconnectedReason reason = hangupMessage.getHangupReason();
/*  400 */         reason = RongCallManager.this.transferRemoteReason(reason);
/*  401 */         if (RongCallManager.this.callListener != null) {
/*  402 */           RongCallManager.this.callListener.onCallDisconnected(RongCallManager.this.getCallSession(), reason);
/*      */         }
/*  404 */         RongCallManager.this.transitionTo(RongCallManager.this.mUnInitState);
/*  405 */         break;
/*      */       case 601:
/*  407 */         if (RongCallManager.this.mCallRecordListener == null) break;
/*  408 */         RongCallManager.this.mCallRecordListener.onServerStartRecording(RongCallCommon.ServerRecordingErrorCode.NOT_IN_CALL); break;
/*      */       case 602:
/*  412 */         if (RongCallManager.this.mCallRecordListener == null) break;
/*  413 */         RongCallManager.this.mCallRecordListener.onServerStopRecording(RongCallCommon.ServerRecordingErrorCode.NOT_IN_CALL); break;
/*      */       case 603:
/*  417 */         if (RongCallManager.this.mCallRecordListener == null) break;
/*  418 */         RongCallManager.this.mCallRecordListener.onServerFetchRecordingStatus(false, RongCallCommon.ServerRecordingErrorCode.NOT_IN_CALL); break;
/*      */       case 604:
/*  422 */         if (RongCallManager.this.mCallRecordListener == null) break;
/*  423 */         RongCallManager.this.mCallRecordListener.onServerStartRecording(RongCallCommon.ServerRecordingErrorCode.NOT_IN_CALL); break;
/*      */       case 605:
/*  427 */         if (RongCallManager.this.mCallRecordListener == null) break;
/*  428 */         RongCallManager.this.mCallRecordListener.onServerStopRecording(RongCallCommon.ServerRecordingErrorCode.NOT_IN_CALL); break;
/*      */       case 606:
/*  432 */         if (RongCallManager.this.mCallRecordListener == null) break;
/*  433 */         RongCallManager.this.mCallRecordListener.onServerStartRecording(RongCallCommon.ServerRecordingErrorCode.NOT_IN_CALL);
/*      */       }
/*      */ 
/*  437 */       return super.processMessage(msg);
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.RongCallManager
 * JD-Core Version:    0.6.0
 */
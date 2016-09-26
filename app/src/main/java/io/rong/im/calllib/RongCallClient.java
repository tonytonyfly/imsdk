/*     */ package io.rong.calllib;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.os.RemoteException;
/*     */ import android.text.TextUtils;
/*     */ import android.util.Base64;
/*     */ import io.rong.calllib.message.CallAcceptMessage;
/*     */ import io.rong.calllib.message.CallHangupMessage;
/*     */ import io.rong.calllib.message.CallInviteMessage;
/*     */ import io.rong.calllib.message.CallModifyMediaMessage;
/*     */ import io.rong.calllib.message.CallModifyMemberMessage;
/*     */ import io.rong.calllib.message.CallRingingMessage;
/*     */ import io.rong.calllib.message.CallSTerminateMessage;
/*     */ import io.rong.calllib.message.CallSummaryMessage;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.AnnotationNotFoundException;
/*     */ import io.rong.imlib.IHandler;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.ModuleManager;
/*     */ import io.rong.imlib.ModuleManager.PreHandleListener;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import java.security.MessageDigest;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class RongCallClient
/*     */ {
/*  31 */   private static final String TAG = RongCallClient.class.getSimpleName();
/*     */   private static final int CALL_TIMEOUT_INTERVAL = 60000;
/*     */   private static RongCallClient sInstance;
/*     */   private boolean initialized;
/*     */   private List<io.rong.imlib.model.Message> inviteMessages;
/*     */   private List<io.rong.imlib.model.Message> hangupMessages;
/*     */   private static List<Class<? extends MessageContent>> VoIPMessageList;
/*     */ 
/*     */   public RongCallClient(Context context, IHandler stub)
/*     */   {
/*  41 */     sInstance = this;
/*  42 */     init(context, stub);
/*     */   }
/*     */ 
/*     */   public static RongCallClient getInstance() {
/*  46 */     return sInstance;
/*     */   }
/*     */ 
/*     */   private void initMessage() {
/*  50 */     VoIPMessageList = new ArrayList();
/*     */ 
/*  52 */     VoIPMessageList.add(CallInviteMessage.class);
/*  53 */     VoIPMessageList.add(CallRingingMessage.class);
/*  54 */     VoIPMessageList.add(CallAcceptMessage.class);
/*  55 */     VoIPMessageList.add(CallHangupMessage.class);
/*  56 */     VoIPMessageList.add(CallSummaryMessage.class);
/*  57 */     VoIPMessageList.add(CallModifyMediaMessage.class);
/*  58 */     VoIPMessageList.add(CallModifyMemberMessage.class);
/*  59 */     VoIPMessageList.add(CallSTerminateMessage.class);
/*     */ 
/*  61 */     for (Class cls : VoIPMessageList)
/*     */       try {
/*  63 */         RongIMClient.registerMessageType(cls);
/*     */       } catch (AnnotationNotFoundException e) {
/*  65 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   private void registerCmdMsgType(IHandler stub)
/*     */   {
/*  71 */     List cmdObjectNameList = new ArrayList();
/*  72 */     cmdObjectNameList.add(((MessageTag)CallAcceptMessage.class.getAnnotation(MessageTag.class)).value());
/*  73 */     cmdObjectNameList.add(((MessageTag)CallHangupMessage.class.getAnnotation(MessageTag.class)).value());
/*  74 */     cmdObjectNameList.add(((MessageTag)CallInviteMessage.class.getAnnotation(MessageTag.class)).value());
/*  75 */     cmdObjectNameList.add(((MessageTag)CallModifyMediaMessage.class.getAnnotation(MessageTag.class)).value());
/*  76 */     cmdObjectNameList.add(((MessageTag)CallModifyMemberMessage.class.getAnnotation(MessageTag.class)).value());
/*  77 */     cmdObjectNameList.add(((MessageTag)CallRingingMessage.class.getAnnotation(MessageTag.class)).value());
/*     */     try
/*     */     {
/*  80 */       stub.registerCmdMsgType(cmdObjectNameList);
/*     */     } catch (RemoteException e) {
/*  82 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void init(Context context, IHandler stub) {
/*  87 */     RLog.i(TAG, new StringBuilder().append("init initialized = ").append(this.initialized).toString());
/*  88 */     if (this.initialized) {
/*  89 */       return;
/*     */     }
/*  91 */     initMessage();
/*  92 */     this.initialized = true;
/*  93 */     this.inviteMessages = new ArrayList();
/*  94 */     this.hangupMessages = new ArrayList();
/*     */ 
/*  96 */     RongCallManager.getInstance().init(context, stub);
/*  97 */     registerCmdMsgType(stub);
/*     */ 
/*  99 */     ModuleManager.addPreHandlerListener(new ModuleManager.PreHandleListener()
/*     */     {
/*     */       public boolean onReceived(io.rong.imlib.model.Message msg, int left, boolean offline, int cmdLeft) {
/* 102 */         RLog.i(RongCallClient.TAG, "onReceived : " + msg.getObjectName() + ", left = " + left + ", offline = " + offline + ", cmdLeft = " + cmdLeft);
/* 103 */         if (RongCallClient.this.getDeltaTime(msg.getSentTime()) < 60000L) {
/* 104 */           RongCallClient.this.handleMessage(msg, left, offline, cmdLeft);
/*     */         }
/* 106 */         return RongCallClient.VoIPMessageList.contains(msg.getContent().getClass());
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private void handleMessage(io.rong.imlib.model.Message msg, int left, boolean offline, int cmdLeft) {
/* 112 */     if ((cmdLeft == 0) || (!offline)) {
/* 113 */       if (this.inviteMessages.size() == 0) {
/* 114 */         transferMessage(msg);
/*     */       } else {
/* 116 */         MessageContent messageContent = msg.getContent();
/* 117 */         if (((messageContent instanceof CallInviteMessage)) || ((messageContent instanceof CallModifyMediaMessage)))
/*     */         {
/* 119 */           this.inviteMessages.add(msg);
/* 120 */         } else if ((messageContent instanceof CallHangupMessage)) {
/* 121 */           this.hangupMessages.add(msg);
/*     */         }
/* 123 */         for (io.rong.imlib.model.Message im : this.inviteMessages) {
/* 124 */           if (this.hangupMessages.size() == 0) {
/* 125 */             transferMessage(im);
/* 126 */             break;
/*     */           }
/*     */           String callId;
/*     */           String callId;
/* 129 */           if ((im.getContent() instanceof CallInviteMessage)) {
/* 130 */             CallInviteMessage inviteMessage = (CallInviteMessage)im.getContent();
/* 131 */             callId = inviteMessage.getCallId();
/*     */           } else {
/* 133 */             CallModifyMemberMessage modifyMemberMessage = (CallModifyMemberMessage)im.getContent();
/* 134 */             callId = modifyMemberMessage.getCallId();
/*     */           }
/* 136 */           int index = -1;
/* 137 */           for (int i = 0; i < this.hangupMessages.size(); i++) {
/* 138 */             CallHangupMessage hangupMessage = (CallHangupMessage)((io.rong.imlib.model.Message)this.hangupMessages.get(i)).getContent();
/* 139 */             if (callId.equals(hangupMessage.getCallId())) {
/* 140 */               index = i;
/* 141 */               break;
/*     */             }
/*     */           }
/* 144 */           if (index == -1) {
/* 145 */             transferMessage(im);
/* 146 */             break;
/*     */           }
/*     */         }
/* 149 */         this.inviteMessages.clear();
/* 150 */         this.hangupMessages.clear();
/*     */       }
/*     */     } else {
/* 153 */       MessageContent messageContent = msg.getContent();
/* 154 */       if (((messageContent instanceof CallInviteMessage)) || ((messageContent instanceof CallModifyMediaMessage)))
/*     */       {
/* 156 */         this.inviteMessages.add(msg);
/* 157 */       } else if ((messageContent instanceof CallHangupMessage))
/* 158 */         this.hangupMessages.add(msg);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void transferMessage(io.rong.imlib.model.Message msg)
/*     */   {
/* 164 */     MessageContent messageContent = msg.getContent();
/* 165 */     if ((messageContent instanceof CallAcceptMessage)) {
/* 166 */       android.os.Message message = android.os.Message.obtain();
/* 167 */       message.what = 106;
/* 168 */       message.obj = msg;
/* 169 */       RongCallManager.getInstance().sendMessage(message);
/* 170 */     } else if ((messageContent instanceof CallInviteMessage)) {
/* 171 */       android.os.Message message = android.os.Message.obtain();
/* 172 */       message.what = 105;
/* 173 */       message.obj = msg;
/* 174 */       RongCallManager.getInstance().sendMessage(message);
/* 175 */     } else if ((messageContent instanceof CallRingingMessage)) {
/* 176 */       android.os.Message message = android.os.Message.obtain();
/* 177 */       message.what = 108;
/* 178 */       message.obj = msg.getSenderUserId();
/* 179 */       RongCallManager.getInstance().sendMessage(message);
/* 180 */     } else if ((messageContent instanceof CallHangupMessage)) {
/* 181 */       android.os.Message message = android.os.Message.obtain();
/* 182 */       message.what = 109;
/* 183 */       message.obj = msg;
/* 184 */       RongCallManager.getInstance().sendMessage(message);
/* 185 */     } else if ((messageContent instanceof CallModifyMediaMessage)) {
/* 186 */       android.os.Message message = android.os.Message.obtain();
/* 187 */       message.what = 110;
/* 188 */       message.obj = msg;
/* 189 */       RongCallManager.getInstance().sendMessage(message);
/* 190 */     } else if ((messageContent instanceof CallModifyMemberMessage)) {
/* 191 */       android.os.Message message = android.os.Message.obtain();
/* 192 */       message.what = 107;
/* 193 */       message.obj = msg;
/* 194 */       RongCallManager.getInstance().sendMessage(message);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setReceivedCallListener(IRongReceivedCallListener listener)
/*     */   {
/* 204 */     RongCallManager.setReceivedCallListener(listener);
/*     */   }
/*     */ 
/*     */   private long getDeltaTime(long sentTime) {
/* 208 */     long deltaTime = RongIMClient.getInstance().getDeltaTime();
/* 209 */     long normalTime = System.currentTimeMillis() - deltaTime;
/*     */ 
/* 211 */     return normalTime - sentTime;
/*     */   }
/*     */ 
/*     */   private String makeCallId(Conversation.ConversationType conversationType, String targetId, List<String> userIds) {
/* 215 */     String str = null;
/* 216 */     for (String id : userIds)
/* 217 */       str = new StringBuilder().append(str).append(id).toString();
/* 218 */     long time = System.currentTimeMillis();
/*     */ 
/* 220 */     return ShortMD5(new String[] { new StringBuilder().append(conversationType.getName()).append(targetId).append(str).append(time).toString() });
/*     */   }
/*     */ 
/*     */   private static String ShortMD5(String[] args) {
/*     */     try {
/* 225 */       StringBuilder builder = new StringBuilder();
/*     */ 
/* 227 */       for (String arg : args) {
/* 228 */         builder.append(arg);
/*     */       }
/*     */ 
/* 231 */       MessageDigest mdInst = MessageDigest.getInstance("MD5");
/* 232 */       mdInst.update(builder.toString().getBytes());
/* 233 */       byte[] mds = mdInst.digest();
/* 234 */       mds = Base64.encode(mds, 2);
/* 235 */       String result = new String(mds);
/* 236 */       result = result.replace("=", "").replace("+", "-").replace("/", "_").replace("\n", "");
/* 237 */       return result;
/*     */     } catch (Exception e) {
/* 239 */       e.printStackTrace();
/*     */     }
/* 241 */     return "";
/*     */   }
/*     */ 
/*     */   public void setVoIPCallListener(IRongCallListener callListener)
/*     */   {
/* 250 */     RongCallManager.getInstance().setCallListener(callListener);
/*     */   }
/*     */ 
/*     */   public void setVoIPCallRecordListener(IRongCallRecordListener recordListener)
/*     */   {
/* 259 */     RongCallManager.getInstance().setCallRecordListener(recordListener);
/*     */   }
/*     */ 
/*     */   public String startCall(Conversation.ConversationType conversationType, String targetId, List<String> userIds, RongCallCommon.CallMediaType mediaType, String extra)
/*     */   {
/* 273 */     if ((conversationType == null) || (TextUtils.isEmpty(targetId)) || (userIds == null) || (userIds.size() == 0)) {
/* 274 */       RLog.e(TAG, "startCall : Illegal Argument.");
/* 275 */       return null;
/*     */     }
/*     */ 
/* 278 */     String callId = makeCallId(conversationType, targetId, userIds);
/* 279 */     String myId = RongIMClient.getInstance().getCurrentUserId();
/*     */ 
/* 281 */     android.os.Message message = android.os.Message.obtain();
/* 282 */     message.what = 101;
/* 283 */     CallSessionImp callInfo = new CallSessionImp();
/* 284 */     callInfo.setExtra(extra);
/* 285 */     callInfo.setConversationType(conversationType);
/* 286 */     callInfo.setEngineType(RongCallCommon.CallEngineType.ENGINE_TYPE_AGORA);
/* 287 */     callInfo.setMediaType(mediaType);
/* 288 */     callInfo.setTargetId(targetId);
/* 289 */     callInfo.setCallId(callId);
/* 290 */     callInfo.setCallerUserId(myId);
/* 291 */     callInfo.setInviterUserId(myId);
/*     */ 
/* 293 */     List list = new ArrayList();
/* 294 */     CallUserProfile state = new CallUserProfile();
/* 295 */     state.setUserId(myId);
/* 296 */     state.setMediaType(mediaType);
/* 297 */     state.setCallStatus(RongCallCommon.CallStatus.IDLE);
/* 298 */     list.add(state);
/*     */ 
/* 300 */     for (String id : userIds) {
/* 301 */       state = new CallUserProfile();
/* 302 */       state.setUserId(id);
/* 303 */       state.setMediaType(mediaType);
/* 304 */       state.setCallStatus(RongCallCommon.CallStatus.IDLE);
/* 305 */       list.add(state);
/*     */     }
/* 307 */     callInfo.setParticipantUserList(list);
/*     */ 
/* 309 */     message.obj = callInfo;
/* 310 */     RongCallManager.getInstance().sendMessage(message);
/* 311 */     return callId;
/*     */   }
/*     */ 
/*     */   public void acceptCall(String callId)
/*     */   {
/* 320 */     if (TextUtils.isEmpty(callId)) {
/* 321 */       RLog.e(TAG, "acceptCall : Illegal Argument.");
/* 322 */       return;
/*     */     }
/*     */ 
/* 325 */     android.os.Message message = android.os.Message.obtain();
/* 326 */     message.what = 102;
/* 327 */     message.obj = callId;
/* 328 */     RongCallManager.getInstance().sendMessage(message);
/*     */   }
/*     */ 
/*     */   public void hangUpCall(String callId)
/*     */   {
/* 337 */     if (TextUtils.isEmpty(callId)) {
/* 338 */       RLog.e(TAG, "hangUpCall : Illegal Argument.");
/* 339 */       return;
/*     */     }
/*     */ 
/* 342 */     android.os.Message message = android.os.Message.obtain();
/* 343 */     message.what = 103;
/* 344 */     message.obj = callId;
/* 345 */     RongCallManager.getInstance().sendMessage(message);
/*     */   }
/*     */ 
/*     */   public RongCallSession getCallSession()
/*     */   {
/* 354 */     return RongCallManager.getInstance().getCallSession();
/*     */   }
/*     */ 
/*     */   public void addParticipants(String callId, List<String> userIds)
/*     */   {
/* 364 */     if ((TextUtils.isEmpty(callId)) || (userIds == null) || (userIds.size() == 0)) {
/* 365 */       RLog.e(TAG, "addParticipants : Illegal Argument.");
/* 366 */       return;
/*     */     }
/*     */ 
/* 369 */     CallSessionImp callInfo = RongCallManager.getInstance().getCallSessionImp();
/* 370 */     if (callInfo.getCallId() == null) {
/* 371 */       RLog.e(TAG, "addParticipants : Call don't start yet.");
/* 372 */       return;
/*     */     }
/*     */ 
/* 375 */     if (!callInfo.getCallId().equals(callId)) {
/* 376 */       RLog.e(TAG, "addParticipants : callId does not exist.");
/* 377 */       return;
/*     */     }
/*     */ 
/* 380 */     android.os.Message message = android.os.Message.obtain();
/* 381 */     message.what = 104;
/* 382 */     message.obj = userIds;
/* 383 */     RongCallManager.getInstance().sendMessage(message);
/*     */   }
/*     */ 
/*     */   public void changeCallMediaType(RongCallCommon.CallMediaType mediaType)
/*     */   {
/* 393 */     if (mediaType == null) {
/* 394 */       RLog.e(TAG, "changeLocalMediaType : Illegal Argument.");
/* 395 */       return;
/*     */     }
/* 397 */     android.os.Message message = android.os.Message.obtain();
/* 398 */     message.what = 206;
/* 399 */     message.obj = mediaType;
/* 400 */     RongCallManager.getInstance().sendMessage(message);
/*     */   }
/*     */ 
/*     */   public void switchCamera()
/*     */   {
/* 407 */     RongCallManager.getInstance().switchCamera();
/*     */   }
/*     */ 
/*     */   public void setEnableLocalVideo(boolean enabled)
/*     */   {
/* 416 */     RongCallManager.getInstance().setEnableLocalVideo(enabled);
/*     */   }
/*     */ 
/*     */   public void setEnableLocalAudio(boolean enabled)
/*     */   {
/* 425 */     RongCallManager.getInstance().setEnableLocalAudio(enabled);
/*     */   }
/*     */ 
/*     */   public void setEnableSpeakerphone(boolean enabled)
/*     */   {
/* 434 */     RongCallManager.getInstance().setEnableSpeakerphone(enabled);
/*     */   }
/*     */ 
/*     */   public void setSpeakerPhoneVolume(int level) {
/* 438 */     if (level <= 0) {
/* 439 */       RLog.e(TAG, "setSpeakerPhoneVolume : Illegal Argument.");
/* 440 */       return;
/*     */     }
/*     */ 
/* 443 */     RongCallManager.getInstance().setSpeakerPhoneVolume(level);
/*     */   }
/*     */ 
/*     */   public boolean isAudioCallEnabled(Conversation.ConversationType type)
/*     */   {
/* 453 */     return RongCallManager.getInstance().isAudioCallEnabled(type);
/*     */   }
/*     */ 
/*     */   public boolean isVideoCallEnabled(Conversation.ConversationType type)
/*     */   {
/* 463 */     return RongCallManager.getInstance().isVideoCallEnabled(type);
/*     */   }
/*     */ 
/*     */   public boolean isServerRecordingEnabled()
/*     */   {
/* 472 */     return RongCallManager.getInstance().isServerRecordingEnabled();
/*     */   }
/*     */ 
/*     */   public void onPermissionGranted()
/*     */   {
/* 483 */     android.os.Message message = android.os.Message.obtain();
/* 484 */     message.what = 500;
/* 485 */     RongCallManager.getInstance().sendMessage(message);
/*     */   }
/*     */ 
/*     */   public void onPermissionDenied()
/*     */   {
/* 496 */     android.os.Message message = android.os.Message.obtain();
/* 497 */     message.what = 501;
/* 498 */     RongCallManager.getInstance().sendMessage(message);
/*     */   }
/*     */ 
/*     */   public void registerVideoFrameListener(IVideoFrameListener listener)
/*     */   {
/* 507 */     RongCallManager.getInstance().registerVideoFrameListener(listener);
/*     */   }
/*     */ 
/*     */   public void unregisterVideoFrameObserver() {
/* 511 */     RongCallManager.getInstance().unregisterVideoFrameListener();
/*     */   }
/*     */ 
/*     */   public void setVideoProfile(RongCallCommon.CallVideoProfile profile)
/*     */   {
/* 520 */     RongCallManager.getInstance().setVideoProfile(profile);
/*     */   }
/*     */ 
/*     */   public void startServerRecording()
/*     */   {
/* 528 */     android.os.Message message = android.os.Message.obtain();
/* 529 */     message.what = 601;
/* 530 */     RongCallManager.getInstance().sendMessage(message);
/*     */   }
/*     */ 
/*     */   public void stopServerRecording()
/*     */   {
/* 538 */     android.os.Message message = android.os.Message.obtain();
/* 539 */     message.what = 602;
/* 540 */     RongCallManager.getInstance().sendMessage(message);
/*     */   }
/*     */ 
/*     */   public void fetchServerRecordingStatus()
/*     */   {
/* 548 */     android.os.Message message = android.os.Message.obtain();
/* 549 */     message.what = 603;
/* 550 */     RongCallManager.getInstance().sendMessage(message);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.RongCallClient
 * JD-Core Version:    0.6.0
 */
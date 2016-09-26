/*     */ package io.rong.imlib.location;
/*     */ 
/*     */ import android.content.BroadcastReceiver;
/*     */ import android.content.Context;
/*     */ import android.content.Intent;
/*     */ import android.content.IntentFilter;
/*     */ import android.net.ConnectivityManager;
/*     */ import android.net.NetworkInfo;
/*     */ import android.os.Handler;
/*     */ import android.util.Log;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.AnnotationNotFoundException;
/*     */ import io.rong.imlib.NativeClient;
/*     */ import io.rong.imlib.NativeClient.OnReceiveMessageListenerEx;
/*     */ import io.rong.imlib.NativeClient.RealTimeLocationListener;
/*     */ import io.rong.imlib.location.message.RealTimeLocationEndMessage;
/*     */ import io.rong.imlib.location.message.RealTimeLocationJoinMessage;
/*     */ import io.rong.imlib.location.message.RealTimeLocationQuitMessage;
/*     */ import io.rong.imlib.location.message.RealTimeLocationStartMessage;
/*     */ import io.rong.imlib.location.message.RealTimeLocationStatusMessage;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Message.MessageDirection;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import io.rong.imlib.navigation.LocationConfig;
/*     */ import io.rong.imlib.navigation.NavigationClient;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class RealTimeLocationManager
/*     */ {
/*  36 */   private static final String TAG = RealTimeLocationManager.class.getSimpleName();
/*     */   private static RealTimeLocationManager sIns;
/*     */   private Context mContext;
/*     */   private HashMap<String, RealTimeLocation> mInsMap;
/*     */   private NetworkStatusReceiver mReceiver;
/*     */   private HashMap<String, OfflineRequest> mOfflineRequest;
/*     */   private int mLastLeft;
/*     */   private HashMap<String, NativeClient.RealTimeLocationListener> mObservers;
/*     */ 
/*     */   private RealTimeLocationManager(Context context)
/*     */   {
/*  48 */     IntentFilter filter = new IntentFilter();
/*  49 */     this.mReceiver = new NetworkStatusReceiver(null);
/*  50 */     filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
/*  51 */     context.registerReceiver(this.mReceiver, filter);
/*     */ 
/*  53 */     this.mContext = context;
/*  54 */     this.mInsMap = new HashMap();
/*  55 */     this.mOfflineRequest = new HashMap();
/*  56 */     this.mObservers = new HashMap();
/*  57 */     this.mLastLeft = 0;
/*     */ 
/*  59 */     NativeClient.OnReceiveMessageListenerEx listener = new NativeClient.OnReceiveMessageListenerEx()
/*     */     {
/*     */       public boolean onReceived(io.rong.imlib.model.Message message, int left) {
/*  62 */         Log.i(RealTimeLocationManager.TAG, "onReceived : " + message.getObjectName() + ", left = " + left + ", sender=" + message.getSenderUserId());
/*     */ 
/*  64 */         MessageContent content = message.getContent();
/*  65 */         if (content == null) {
/*  66 */           return false;
/*     */         }
/*  68 */         boolean result = false;
/*  69 */         if (left > 0) {
/*  70 */           RealTimeLocationManager.access$202(RealTimeLocationManager.this, left);
/*     */         }
/*  72 */         if (RealTimeLocationManager.this.mLastLeft > 0) {
/*  73 */           result = RealTimeLocationManager.this.cacheOfflineRequest(message);
/*  74 */           if (left == 0) {
/*  75 */             RealTimeLocationManager.access$202(RealTimeLocationManager.this, 0);
/*  76 */             result = RealTimeLocationManager.this.handleRequest(message);
/*     */           }
/*     */         } else {
/*  79 */           result = RealTimeLocationManager.this.handleRequest(message);
/*     */         }
/*     */ 
/*  82 */         return result;
/*     */       }
/*     */     };
/*  86 */     NativeClient client = NativeClient.getInstance();
/*  87 */     client.setOnReceiveMessageListenerEx(listener);
/*     */     try
/*     */     {
/*  90 */       NativeClient.registerMessageType(RealTimeLocationStartMessage.class);
/*  91 */       NativeClient.registerMessageType(RealTimeLocationJoinMessage.class);
/*  92 */       NativeClient.registerMessageType(RealTimeLocationQuitMessage.class);
/*  93 */       NativeClient.registerMessageType(RealTimeLocationStatusMessage.class);
/*  94 */       NativeClient.registerMessageType(RealTimeLocationEndMessage.class);
/*     */     } catch (AnnotationNotFoundException e) {
/*  96 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static RealTimeLocationManager init(Context context) {
/* 101 */     if (sIns == null) {
/* 102 */       sIns = new RealTimeLocationManager(context);
/*     */     }
/* 104 */     return sIns;
/*     */   }
/*     */ 
/*     */   private RealTimeLocation getRealTimeLocation(Conversation.ConversationType type, String targetId)
/*     */   {
/* 129 */     RealTimeLocation rl = null;
/* 130 */     String key = type.getName() + targetId;
/* 131 */     if (this.mInsMap.size() > 0)
/* 132 */       rl = (RealTimeLocation)this.mInsMap.get(key);
/* 133 */     if (rl == null) {
/* 134 */       rl = new RealTimeLocation(this.mContext, type, targetId);
/* 135 */       this.mInsMap.put(key, rl);
/*     */     }
/*     */ 
/* 138 */     return rl;
/*     */   }
/*     */ 
/*     */   private boolean cacheOfflineRequest(io.rong.imlib.model.Message message) {
/* 142 */     MessageContent content = message.getContent();
/* 143 */     String key = message.getConversationType() + message.getTargetId();
/* 144 */     OfflineRequest request = null;
/*     */ 
/* 146 */     if ((content instanceof RealTimeLocationStartMessage)) {
/* 147 */       request = (OfflineRequest)this.mOfflineRequest.get(key);
/* 148 */       if (request == null) {
/* 149 */         request = new OfflineRequest(message.getConversationType(), message.getTargetId(), message.getSenderUserId());
/* 150 */         this.mOfflineRequest.put(key, request);
/*     */       }
/* 152 */       request.startInc();
/* 153 */       return false;
/* 154 */     }if ((content instanceof RealTimeLocationJoinMessage)) {
/* 155 */       request = (OfflineRequest)this.mOfflineRequest.get(key);
/* 156 */       if (request == null) {
/* 157 */         request = new OfflineRequest(message.getConversationType(), message.getTargetId(), message.getSenderUserId());
/* 158 */         this.mOfflineRequest.put(key, request);
/*     */       }
/* 160 */       request.joinInc();
/* 161 */       return false;
/* 162 */     }if ((content instanceof RealTimeLocationQuitMessage)) {
/* 163 */       request = (OfflineRequest)this.mOfflineRequest.get(key);
/* 164 */       if (request == null) {
/* 165 */         request = new OfflineRequest(message.getConversationType(), message.getTargetId(), message.getSenderUserId());
/* 166 */         this.mOfflineRequest.put(key, request);
/*     */       }
/* 168 */       request.quitInc();
/* 169 */       return false;
/*     */     }
/* 171 */     return (content instanceof RealTimeLocationStatusMessage);
/*     */   }
/*     */ 
/*     */   private boolean handleRequest(io.rong.imlib.model.Message message)
/*     */   {
/* 177 */     if (this.mOfflineRequest.size() > 0) {
/* 178 */       Collection c = this.mOfflineRequest.values();
/* 179 */       Iterator iterator = c.iterator();
/* 180 */       while (iterator.hasNext()) {
/* 181 */         OfflineRequest request = (OfflineRequest)iterator.next();
/* 182 */         if ((request != null) && (request.shouldHandle())) {
/* 183 */           RealTimeLocation rt = getRealTimeLocation(request.getConversationType(), request.getTargetId());
/* 184 */           android.os.Message msg = android.os.Message.obtain();
/* 185 */           msg.what = 3;
/* 186 */           msg.obj = request.getSender();
/* 187 */           rt.sendMessage(msg);
/*     */         }
/*     */       }
/* 190 */       this.mOfflineRequest.clear();
/* 191 */       this.mLastLeft = 0;
/*     */     }
/*     */     else {
/* 194 */       MessageContent content = message.getContent();
/* 195 */       if (message.getMessageDirection().equals(Message.MessageDirection.SEND)) {
/* 196 */         return false;
/*     */       }
/* 198 */       if ((content instanceof RealTimeLocationStartMessage)) {
/* 199 */         NativeClient.RealTimeLocationListener listener = (NativeClient.RealTimeLocationListener)this.mObservers.get(message.getConversationType().getName() + message.getTargetId());
/* 200 */         RealTimeLocation rt = getRealTimeLocation(message.getConversationType(), message.getTargetId());
/* 201 */         rt.addListener(listener);
/* 202 */         this.mObservers.get(message.getConversationType().getName() + message.getSenderUserId());
/* 203 */         android.os.Message msg = android.os.Message.obtain();
/* 204 */         msg.what = 3;
/* 205 */         msg.obj = message.getSenderUserId();
/* 206 */         rt.sendMessage(msg);
/* 207 */         return false;
/* 208 */       }if ((content instanceof RealTimeLocationJoinMessage)) {
/* 209 */         RealTimeLocation rt = getRealTimeLocation(message.getConversationType(), message.getTargetId());
/*     */ 
/* 211 */         android.os.Message msg = android.os.Message.obtain();
/* 212 */         msg.what = 4;
/* 213 */         msg.obj = message.getSenderUserId();
/* 214 */         rt.sendMessage(msg);
/* 215 */         return false;
/* 216 */       }if ((content instanceof RealTimeLocationQuitMessage)) {
/* 217 */         RealTimeLocation rt = getRealTimeLocation(message.getConversationType(), message.getTargetId());
/*     */ 
/* 219 */         android.os.Message msg = android.os.Message.obtain();
/* 220 */         msg.what = 5;
/* 221 */         msg.obj = message.getSenderUserId();
/* 222 */         rt.sendMessage(msg);
/* 223 */         return false;
/* 224 */       }if ((content instanceof RealTimeLocationStatusMessage)) {
/* 225 */         RealTimeLocation rt = getRealTimeLocation(message.getConversationType(), message.getTargetId());
/*     */ 
/* 227 */         android.os.Message msg = android.os.Message.obtain();
/* 228 */         msg.what = 6;
/* 229 */         msg.obj = message;
/* 230 */         rt.sendMessage(msg);
/* 231 */         return true;
/*     */       }
/*     */     }
/* 234 */     return false;
/*     */   }
/*     */ 
/*     */   public int setupRealTimeLocation(Context context, Conversation.ConversationType conversationType, String targetId)
/*     */   {
/* 246 */     int errorCode = 0;
/*     */ 
/* 248 */     RealTimeLocation rl = null;
/* 249 */     String key = conversationType.getName() + targetId;
/*     */ 
/* 268 */     if ((conversationType.equals(Conversation.ConversationType.PRIVATE)) || (conversationType.equals(Conversation.ConversationType.DISCUSSION)))
/*     */     {
/* 270 */       if (this.mInsMap.size() > 0) {
/* 271 */         rl = (RealTimeLocation)this.mInsMap.get(key);
/*     */       }
/* 273 */       if ((rl != null) && (!rl.getRealTimeLocationCurrentState().equals(RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_IDLE))) {
/* 274 */         errorCode = RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_IS_ON_GOING.getValue();
/*     */       }
/* 276 */       return errorCode;
/*     */     }
/* 278 */     return RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_CONVERSATION_NOT_SUPPORT.getValue();
/*     */   }
/*     */ 
/*     */   public int startRealTimeLocation(Conversation.ConversationType type, String targetId)
/*     */   {
/* 286 */     RealTimeLocation rl = null;
/* 287 */     String key = type.getName() + targetId;
/*     */ 
/* 289 */     if (this.mInsMap.size() > 0) {
/* 290 */       rl = (RealTimeLocation)this.mInsMap.get(key);
/* 291 */       if (rl == null) {
/* 292 */         rl = new RealTimeLocation(this.mContext, type, targetId);
/* 293 */         this.mInsMap.put(key, rl);
/*     */       }
/*     */     } else {
/* 296 */       rl = new RealTimeLocation(this.mContext, type, targetId);
/* 297 */       this.mInsMap.put(key, rl);
/*     */     }
/*     */ 
/* 300 */     rl.addListener((NativeClient.RealTimeLocationListener)this.mObservers.get(key));
/*     */ 
/* 302 */     rl.sendMessage(0);
/*     */ 
/* 304 */     if (!rl.gpsIsAvailable()) {
/* 305 */       return RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_GPS_DISABLED.getValue();
/*     */     }
/* 307 */     return RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_SUCCESS.getValue();
/*     */   }
/*     */ 
/*     */   public int joinRealTimeLocation(Conversation.ConversationType type, String targetId)
/*     */   {
/* 314 */     RealTimeLocation sl = null;
/*     */ 
/* 316 */     if (this.mInsMap.size() == 0) {
/* 317 */       RLog.e(TAG, "joinRealTimeLocation No instance!");
/* 318 */       return RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_NOT_INIT.getValue();
/*     */     }
/*     */ 
/* 321 */     sl = (RealTimeLocation)this.mInsMap.get(type.getName() + targetId);
/* 322 */     if (sl == null) {
/* 323 */       RLog.e(TAG, "joinRealTimeLocation No instance!");
/* 324 */       return RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_NOT_INIT.getValue();
/*     */     }
/*     */ 
/* 327 */     LocationConfig config = NavigationClient.getInstance().getLocationConfig(this.mContext);
/* 328 */     List participants = getRealTimeLocationParticipants(type, targetId);
/* 329 */     if ((config != null) && (participants != null) && (config.getMaxParticipant() < participants.size())) {
/* 330 */       return RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_EXCEED_MAX_PARTICIPANT.getValue();
/*     */     }
/* 332 */     sl.sendMessage(1);
/*     */ 
/* 334 */     if (!sl.gpsIsAvailable()) {
/* 335 */       return RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_GPS_DISABLED.getValue();
/*     */     }
/* 337 */     return RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_SUCCESS.getValue();
/*     */   }
/*     */ 
/*     */   public void quitRealTimeLocation(Conversation.ConversationType type, String targetId)
/*     */   {
/* 344 */     RealTimeLocation sl = null;
/*     */ 
/* 346 */     if (this.mInsMap.size() == 0) {
/* 347 */       RLog.e(TAG, "quitRealTimeLocation No instance!");
/* 348 */       return;
/*     */     }
/*     */ 
/* 351 */     sl = (RealTimeLocation)this.mInsMap.get(type.getName() + targetId);
/* 352 */     if (sl == null) {
/* 353 */       RLog.e(TAG, "quitRealTimeLocation No instance!");
/* 354 */       return;
/*     */     }
/*     */ 
/* 357 */     sl.sendMessage(2);
/*     */   }
/*     */ 
/*     */   public List<String> getRealTimeLocationParticipants(Conversation.ConversationType type, String targetId)
/*     */   {
/* 366 */     RealTimeLocation sl = null;
/*     */ 
/* 368 */     if (this.mInsMap.size() == 0) {
/* 369 */       RLog.e(TAG, "getRealTimeLocationParticipants No instance!");
/* 370 */       return null;
/*     */     }
/*     */ 
/* 373 */     sl = (RealTimeLocation)this.mInsMap.get(type.getName() + targetId);
/* 374 */     if (sl == null) {
/* 375 */       RLog.e(TAG, "getRealTimeLocationParticipants No instance!");
/* 376 */       return null;
/*     */     }
/*     */ 
/* 379 */     return sl.getParticipants();
/*     */   }
/*     */ 
/*     */   public RealTimeLocationConstant.RealTimeLocationStatus getRealTimeLocationCurrentState(Conversation.ConversationType type, String targetId)
/*     */   {
/* 388 */     RealTimeLocation sl = null;
/*     */ 
/* 390 */     if (this.mInsMap.size() == 0) {
/* 391 */       RLog.e(TAG, "getRealTimeLocationCurrentState No instance!");
/* 392 */       return RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_IDLE;
/*     */     }
/*     */ 
/* 395 */     sl = (RealTimeLocation)this.mInsMap.get(type.getName() + targetId);
/* 396 */     if (sl == null) {
/* 397 */       RLog.e(TAG, "getRealTimeLocationCurrentState No instance!");
/* 398 */       return RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_IDLE;
/*     */     }
/*     */ 
/* 401 */     return sl.getRealTimeLocationCurrentState();
/*     */   }
/*     */ 
/*     */   public void addListener(Conversation.ConversationType type, String targetId, NativeClient.RealTimeLocationListener observer)
/*     */   {
/* 410 */     String key = type.getName() + targetId;
/* 411 */     if ((this.mObservers.size() > 0) && (this.mObservers.get(key) != null)) {
/* 412 */       this.mObservers.remove(key);
/* 413 */       this.mObservers.put(key, observer);
/*     */     } else {
/* 415 */       this.mObservers.put(key, observer);
/*     */     }
/* 417 */     if ((this.mInsMap.size() > 0) && (this.mInsMap.get(key) != null))
/* 418 */       ((RealTimeLocation)this.mInsMap.get(key)).addListener(observer);
/*     */   }
/*     */ 
/*     */   public void removeListener(Conversation.ConversationType type, String targetId, NativeClient.RealTimeLocationListener listener)
/*     */   {
/* 427 */     String key = type.getName() + targetId;
/*     */ 
/* 429 */     if ((this.mObservers.size() > 0) && (listener != null)) {
/* 430 */       this.mObservers.remove(key);
/* 431 */       if (this.mInsMap.get(key) != null)
/* 432 */         ((RealTimeLocation)this.mInsMap.get(key)).deleteListener(listener);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateLocation(Conversation.ConversationType type, String targetId, double latitude, double longitude) {
/* 437 */     RealTimeLocation rl = null;
/*     */ 
/* 439 */     if (this.mInsMap.size() == 0) {
/* 440 */       RLog.e(TAG, "getRealTimeLocationCurrentState No instance!");
/* 441 */       return;
/*     */     }
/*     */ 
/* 444 */     rl = (RealTimeLocation)this.mInsMap.get(type.getName() + targetId);
/* 445 */     if (rl == null) {
/* 446 */       RLog.e(TAG, "getRealTimeLocationCurrentState No instance!");
/* 447 */       return;
/*     */     }
/*     */ 
/* 450 */     rl.updateLocation(latitude, longitude); } 
/*     */   private class OfflineRequest { private String targetId;
/*     */     private String sender;
/*     */     private int startCount;
/*     */     private int joinCount;
/*     */     private int quitCount;
/*     */     private Conversation.ConversationType type;
/*     */ 
/* 462 */     public OfflineRequest(Conversation.ConversationType type, String targetId, String senderId) { this.targetId = targetId;
/* 463 */       this.sender = senderId;
/* 464 */       this.type = type; }
/*     */ 
/*     */     public void startInc()
/*     */     {
/* 468 */       this.startCount += 1;
/*     */     }
/*     */ 
/*     */     public void joinInc() {
/* 472 */       this.joinCount += 1;
/*     */     }
/*     */ 
/*     */     public void quitInc() {
/* 476 */       this.quitCount += 1;
/*     */     }
/*     */ 
/*     */     public String getSender() {
/* 480 */       return this.sender;
/*     */     }
/*     */ 
/*     */     public String getTargetId() {
/* 484 */       return this.targetId;
/*     */     }
/*     */ 
/*     */     public Conversation.ConversationType getConversationType() {
/* 488 */       return this.type;
/*     */     }
/*     */ 
/*     */     public boolean shouldHandle() {
/* 492 */       Log.d(RealTimeLocationManager.TAG, new StringBuilder().append("shouldHandle : ").append(this.startCount + this.joinCount > this.quitCount).toString());
/* 493 */       return this.startCount + this.joinCount > this.quitCount;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class NetworkStatusReceiver extends BroadcastReceiver
/*     */   {
/*     */     private NetworkStatusReceiver()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void onReceive(Context context, Intent intent)
/*     */     {
/* 111 */       if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
/* 112 */         ConnectivityManager cm = (ConnectivityManager)context.getSystemService("connectivity");
/* 113 */         NetworkInfo networkInfo = cm.getActiveNetworkInfo();
/* 114 */         if (((networkInfo == null) || (!networkInfo.isConnected())) && 
/* 115 */           (RealTimeLocationManager.this.mInsMap != null) && (RealTimeLocationManager.this.mInsMap.size() > 0)) {
/* 116 */           Collection collection = RealTimeLocationManager.this.mInsMap.values();
/* 117 */           Iterator iterator = collection.iterator();
/* 118 */           while (iterator.hasNext()) {
/* 119 */             RealTimeLocation rtl = (RealTimeLocation)iterator.next();
/* 120 */             rtl.getHandler().sendEmptyMessage(13);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.location.RealTimeLocationManager
 * JD-Core Version:    0.6.0
 */
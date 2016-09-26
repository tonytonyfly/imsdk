/*     */ package io.rong.imlib.location;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.location.Criteria;
/*     */ import android.location.Location;
/*     */ import android.location.LocationListener;
/*     */ import android.location.LocationManager;
/*     */ import android.os.Bundle;
/*     */ import android.os.Handler;
/*     */ import android.util.Log;
/*     */ import io.rong.imlib.NativeClient;
/*     */ import io.rong.imlib.NativeClient.IResultCallback;
/*     */ import io.rong.imlib.NativeClient.ISendMessageCallback;
/*     */ import io.rong.imlib.NativeClient.OnReceiveMessageListener;
/*     */ import io.rong.imlib.NativeClient.RealTimeLocationListener;
/*     */ import io.rong.imlib.location.message.RealTimeLocationJoinMessage;
/*     */ import io.rong.imlib.location.message.RealTimeLocationQuitMessage;
/*     */ import io.rong.imlib.location.message.RealTimeLocationStartMessage;
/*     */ import io.rong.imlib.location.message.RealTimeLocationStatusMessage;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Message.SentStatus;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import io.rong.imlib.navigation.LocationConfig;
/*     */ import io.rong.imlib.navigation.NavigationClient;
/*     */ import io.rong.imlib.stateMachine.State;
/*     */ import io.rong.imlib.stateMachine.StateMachine;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class RealTimeLocation extends StateMachine
/*     */ {
/*  39 */   private static final String TAG = RealTimeLocation.class.getSimpleName();
/*     */   public static final int RC_REAL_TIME_LOCATION_EVENT_START = 0;
/*     */   public static final int RC_REAL_TIME_LOCATION_EVENT_JOIN = 1;
/*     */   public static final int RC_REAL_TIME_LOCATION_EVENT_QUIT = 2;
/*     */   public static final int RC_REAL_TIME_LOCATION_EVENT_PARTICIPANT_START = 3;
/*     */   public static final int RC_REAL_TIME_LOCATION_EVENT_PARTICIPANT_JOIN = 4;
/*     */   public static final int RC_REAL_TIME_LOCATION_EVENT_PARTICIPANT_QUIT = 5;
/*     */   public static final int RC_REAL_TIME_LOCATION_EVENT_RECEIVE_LOCATION_MESSAGE = 6;
/*     */   public static final int RC_REAL_TIME_LOCATION_EVENT_SEND_LOCATION_MESSAGE = 7;
/*     */   public static final int RC_REAL_TIME_LOCATION_EVENT_START_FAILURE = 8;
/*     */   public static final int RC_REAL_TIME_LOCATION_EVENT_JOIN_FAILURE = 9;
/*     */   public static final int RC_REAL_TIME_LOCATION_EVENT_REFRESH_TIME_EXPIRE = 10;
/*     */   public static final int RC_REAL_TIME_LOCATION_EVENT_TERMINAL = 11;
/*     */   public static final int RC_REAL_TIME_LOCATION_EVENT_PARTICIPANT_NO_RESPONSE = 12;
/*     */   public static final int RC_REAL_TIME_LOCATION_EVENT_NETWORK_UNAVAILABLE = 13;
/*     */   public static final int RC_REAL_TIME_LOCATION_EVENT_DISABLE_GPS = 14;
/*     */   private Conversation.ConversationType mConversationType;
/*     */   private String mTargetId;
/*     */   private String mSelfId;
/*  61 */   private int mRefreshInterval = 10000;
/*     */   private Runnable mRefreshRunnable;
/*  63 */   private int mFilterDistance = 5;
/*     */ 
/*  65 */   private double mLatitude = 0.0D;
/*  66 */   private double mLongitude = 0.0D;
/*  67 */   private boolean mGpsEnable = true;
/*     */   private LocationManager mLocationManager;
/*     */   private LocationListener mLocationListener;
/*     */   private ArrayList<String> mParticipants;
/*     */   private HashMap<String, ParticipantWatcher> mWatcher;
/*     */   private NativeClient.RealTimeLocationListener mObservers;
/*     */   private RealTimeLocationConstant.RealTimeLocationStatus mCurrentState;
/*     */   private NativeClient mClient;
/* 277 */   private State mIdleState = new IdleState(null);
/*     */ 
/* 325 */   private State mOutgoingState = new OutgoingState(null);
/*     */ 
/* 380 */   private State mIncomingState = new IncomingState(null);
/*     */ 
/* 457 */   private State mConnectedState = new ConnectedState(null);
/*     */ 
/* 547 */   private State mTerminalState = new TerminalState(null);
/*     */ 
/*     */   public void addListener(NativeClient.RealTimeLocationListener listener)
/*     */   {
/*  83 */     this.mObservers = listener;
/*     */   }
/*     */ 
/*     */   public void deleteListener(NativeClient.RealTimeLocationListener listener)
/*     */   {
/*  92 */     this.mObservers = null;
/*     */   }
/*     */ 
/*     */   public RealTimeLocation(Context context, Conversation.ConversationType type, String targetId)
/*     */   {
/* 103 */     super(new StringBuilder().append(type.getName()).append(targetId).toString());
/*     */ 
/* 105 */     Log.d(TAG, "RealTimeLocation");
/*     */ 
/* 107 */     this.mConversationType = type;
/* 108 */     this.mTargetId = targetId;
/* 109 */     this.mClient = NativeClient.getInstance();
/* 110 */     this.mCurrentState = RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_IDLE;
/* 111 */     this.mParticipants = new ArrayList();
/* 112 */     this.mWatcher = new HashMap();
/*     */ 
/* 114 */     LocationConfig config = NavigationClient.getInstance().getLocationConfig(context);
/* 115 */     if (config != null) {
/* 116 */       this.mFilterDistance = config.getDistanceFilter();
/* 117 */       this.mRefreshInterval = (config.getRefreshInterval() * 1000);
/*     */     }
/*     */ 
/* 120 */     this.mRefreshRunnable = new Runnable()
/*     */     {
/*     */       public void run() {
/* 123 */         RealTimeLocation.this.getHandler().sendEmptyMessage(10);
/* 124 */         RealTimeLocation.this.getHandler().postDelayed(RealTimeLocation.this.mRefreshRunnable, RealTimeLocation.this.mRefreshInterval);
/*     */       }
/*     */     };
/* 127 */     int result = context.checkCallingPermission("android.permission.ACCESS_FINE_LOCATION");
/* 128 */     this.mGpsEnable = (result == 0);
/*     */ 
/* 134 */     addState(this.mIdleState);
/* 135 */     addState(this.mIncomingState, this.mIdleState);
/* 136 */     addState(this.mOutgoingState, this.mIdleState);
/* 137 */     addState(this.mConnectedState, this.mIdleState);
/* 138 */     addState(this.mTerminalState, this.mIdleState);
/* 139 */     setInitialState(this.mIdleState);
/*     */ 
/* 141 */     start();
/*     */   }
/*     */ 
/*     */   private void startTimer()
/*     */   {
/* 153 */     getHandler().removeCallbacks(this.mRefreshRunnable);
/* 154 */     getHandler().postDelayed(this.mRefreshRunnable, this.mRefreshInterval);
/*     */   }
/*     */ 
/*     */   private void stopTimer()
/*     */   {
/* 162 */     getHandler().removeCallbacks(this.mRefreshRunnable);
/*     */   }
/*     */ 
/*     */   public void updateLocation(double latitude, double longitude) {
/* 166 */     this.mLatitude = latitude;
/* 167 */     this.mLongitude = longitude;
/*     */   }
/*     */ 
/*     */   public RealTimeLocationConstant.RealTimeLocationStatus getRealTimeLocationCurrentState()
/*     */   {
/* 176 */     return this.mCurrentState;
/*     */   }
/*     */ 
/*     */   public boolean gpsIsAvailable()
/*     */   {
/* 186 */     return this.mGpsEnable;
/*     */   }
/*     */ 
/*     */   public List<String> getParticipants()
/*     */   {
/* 195 */     return this.mParticipants;
/*     */   }
/*     */ 
/*     */   private void gpsInit(Context context)
/*     */   {
/* 204 */     Log.d(TAG, "gpsInit");
/*     */ 
/* 206 */     this.mLocationManager = ((LocationManager)context.getSystemService("location"));
/* 207 */     if (!this.mLocationManager.isProviderEnabled("gps")) {
/* 208 */       Log.e(TAG, "GSP is disabled");
/* 209 */       return;
/*     */     }
/*     */ 
/* 212 */     this.mGpsEnable = true;
/*     */ 
/* 214 */     this.mLocationListener = new LocationListener()
/*     */     {
/*     */       public void onLocationChanged(Location location) {
/* 217 */         Log.d(RealTimeLocation.TAG, "onLocationChanged");
/* 218 */         if (location != null) {
/* 219 */           RealTimeLocation.access$302(RealTimeLocation.this, location.getLatitude());
/* 220 */           RealTimeLocation.access$402(RealTimeLocation.this, location.getLongitude());
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onStatusChanged(String provider, int status, Bundle extras)
/*     */       {
/* 226 */         Log.d(RealTimeLocation.TAG, "onStatusChanged");
/* 227 */         switch (status) {
/*     */         case 2:
/* 229 */           Log.i(RealTimeLocation.TAG, "当前GPS状态为可见状态");
/* 230 */           break;
/*     */         case 0:
/* 232 */           Log.i(RealTimeLocation.TAG, "当前GPS状态为服务区外状态");
/* 233 */           break;
/*     */         case 1:
/* 235 */           Log.i(RealTimeLocation.TAG, "当前GPS状态为暂停服务状态");
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onProviderEnabled(String provider)
/*     */       {
/* 242 */         RealTimeLocation.access$502(RealTimeLocation.this, true);
/* 243 */         Location location = RealTimeLocation.this.mLocationManager.getLastKnownLocation(provider);
/* 244 */         if (location != null) {
/* 245 */           RealTimeLocation.access$302(RealTimeLocation.this, location.getLatitude());
/* 246 */           RealTimeLocation.access$402(RealTimeLocation.this, location.getLongitude());
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onProviderDisabled(String provider)
/*     */       {
/* 252 */         RealTimeLocation.access$502(RealTimeLocation.this, false);
/* 253 */         RealTimeLocation.this.getHandler().sendEmptyMessage(14);
/*     */       }
/*     */     };
/* 257 */     String bestProvider = this.mLocationManager.getBestProvider(getCriteria(), true);
/* 258 */     Location location = this.mLocationManager.getLastKnownLocation(bestProvider);
/* 259 */     if (location != null) {
/* 260 */       this.mLatitude = location.getLatitude();
/* 261 */       this.mLongitude = location.getLongitude();
/*     */     }
/* 263 */     Log.e(TAG, new StringBuilder().append("gpsInit: location = ").append(location != null ? new StringBuilder().append("[ ").append(this.mLatitude).append(" ").append(this.mLongitude).append(" ]").toString() : "null").toString());
/*     */   }
/*     */ 
/*     */   private Criteria getCriteria() {
/* 267 */     Criteria criteria = new Criteria();
/* 268 */     criteria.setAccuracy(1);
/* 269 */     criteria.setSpeedRequired(false);
/* 270 */     criteria.setCostAllowed(false);
/* 271 */     criteria.setBearingRequired(false);
/* 272 */     criteria.setAltitudeRequired(false);
/* 273 */     criteria.setPowerRequirement(1);
/* 274 */     return criteria;
/*     */   }
/*     */ 
/*     */   private void updateSelfLocation()
/*     */   {
/* 580 */     onReceiveLocation(this.mLatitude, this.mLongitude, this.mSelfId);
/*     */   }
/*     */ 
/*     */   private void sendStartMessage() {
/* 584 */     RealTimeLocationStartMessage start = RealTimeLocationStartMessage.obtain("start real time location.");
/* 585 */     String content = "收到一条位置共享消息";
/* 586 */     this.mClient.sendMessage(this.mConversationType, this.mTargetId, start, content, null, new NativeClient.ISendMessageCallback()
/*     */     {
/*     */       public void onAttached(io.rong.imlib.model.Message message)
/*     */       {
/* 590 */         if (NativeClient.getInstance().getOnReceiveMessageListener() != null) {
/* 591 */           message.setSentStatus(Message.SentStatus.SENT);
/* 592 */           NativeClient.getInstance().getOnReceiveMessageListener().onReceived(message, 0, false, true, 0);
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onSuccess(io.rong.imlib.model.Message message)
/*     */       {
/* 598 */         RealTimeLocation.this.getHandler().sendEmptyMessage(7);
/*     */       }
/*     */ 
/*     */       public void onError(io.rong.imlib.model.Message message, int code)
/*     */       {
/* 603 */         RealTimeLocation.this.getHandler().sendEmptyMessage(8);
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private void sendJoinMessage() {
/* 609 */     RealTimeLocationJoinMessage content = RealTimeLocationJoinMessage.obtain("join real time location.");
/* 610 */     this.mClient.sendMessage(this.mConversationType, this.mTargetId, content, null, null, new NativeClient.ISendMessageCallback()
/*     */     {
/*     */       public void onAttached(io.rong.imlib.model.Message message)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onSuccess(io.rong.imlib.model.Message message)
/*     */       {
/* 619 */         RealTimeLocation.this.getHandler().sendEmptyMessage(7);
/*     */       }
/*     */ 
/*     */       public void onError(io.rong.imlib.model.Message message, int code)
/*     */       {
/* 624 */         RealTimeLocation.this.getHandler().sendEmptyMessage(9);
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private void sendQuitMessage() {
/* 630 */     RealTimeLocationQuitMessage content = RealTimeLocationQuitMessage.obtain("quit real time location.");
/* 631 */     this.mClient.sendMessage(this.mConversationType, this.mTargetId, content, null, null, null);
/*     */   }
/*     */ 
/*     */   private void sendLocationMessage() {
/* 635 */     MessageContent content = RealTimeLocationStatusMessage.obtain(this.mLatitude, this.mLongitude);
/* 636 */     this.mClient.sendStatusMessage(this.mConversationType, this.mTargetId, content, 1, new NativeClient.IResultCallback()
/*     */     {
/*     */       public void onSuccess(Integer integer)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onError(int code)
/*     */       {
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void onStatusChanged(RealTimeLocationConstant.RealTimeLocationStatus state)
/*     */   {
/* 653 */     if (this.mObservers != null)
/* 654 */       this.mObservers.onStatusChange(state);
/*     */   }
/*     */ 
/*     */   private void onParticipantQuit(String id)
/*     */   {
/* 659 */     if (this.mObservers != null)
/* 660 */       this.mObservers.onParticipantsQuit(id);
/*     */   }
/*     */ 
/*     */   private void onParticipantsJoin(String id)
/*     */   {
/* 665 */     if (this.mObservers != null)
/* 666 */       this.mObservers.onParticipantsJoin(id);
/*     */   }
/*     */ 
/*     */   private void onReceiveLocation(double latitude, double longitude, String id)
/*     */   {
/* 671 */     if (this.mObservers != null)
/* 672 */       this.mObservers.onReceiveLocation(latitude, longitude, id);
/*     */   }
/*     */ 
/*     */   private void onError(RealTimeLocationConstant.RealTimeLocationErrorCode errorCode)
/*     */   {
/* 677 */     if (this.mObservers != null)
/* 678 */       this.mObservers.onError(errorCode); 
/*     */   }
/*     */ 
/*     */   private class ParticipantWatcher {
/*     */     Runnable runnable;
/*     */     String id;
/*     */ 
/*     */     public ParticipantWatcher(String id) {
/* 687 */       this.id = id;
/* 688 */       this.runnable = new Runnable(RealTimeLocation.this, id)
/*     */       {
/*     */         public void run() {
/* 691 */           android.os.Message msg = android.os.Message.obtain();
/* 692 */           msg.what = 12;
/* 693 */           msg.obj = this.val$id;
/* 694 */           RealTimeLocation.this.getHandler().sendMessage(msg);
/*     */         } } ;
/*     */     }
/*     */ 
/*     */     public void start() {
/* 700 */       RealTimeLocation.this.getHandler().postDelayed(this.runnable, RealTimeLocation.this.mRefreshInterval * 3);
/*     */     }
/*     */ 
/*     */     public void stop() {
/* 704 */       RealTimeLocation.this.getHandler().removeCallbacks(this.runnable);
/*     */     }
/*     */ 
/*     */     public void update() {
/* 708 */       RealTimeLocation.this.getHandler().removeCallbacks(this.runnable);
/* 709 */       RealTimeLocation.this.getHandler().postDelayed(this.runnable, RealTimeLocation.this.mRefreshInterval * 3);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class TerminalState extends State
/*     */   {
/*     */     private TerminalState()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void enter()
/*     */     {
/* 552 */       Log.d(RealTimeLocation.TAG, "terminal enter : current = " + RealTimeLocation.this.mCurrentState);
/* 553 */       RealTimeLocation.this.mParticipants.clear();
/* 554 */       RealTimeLocation.this.stopTimer();
/* 555 */       if (RealTimeLocation.this.mWatcher.size() > 0) {
/* 556 */         Collection c = RealTimeLocation.this.mWatcher.values();
/* 557 */         Iterator iterator = c.iterator();
/* 558 */         while (iterator.hasNext()) {
/* 559 */           ((RealTimeLocation.ParticipantWatcher)iterator.next()).stop();
/*     */         }
/* 561 */         RealTimeLocation.this.mWatcher.clear();
/*     */       }
/* 563 */       RealTimeLocation.this.getHandler().sendEmptyMessage(11);
/*     */     }
/*     */ 
/*     */     public boolean processMessage(android.os.Message msg)
/*     */     {
/* 568 */       switch (msg.what) {
/*     */       case 11:
/* 570 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mIdleState);
/* 571 */         break;
/*     */       }
/*     */ 
/* 575 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ConnectedState extends State
/*     */   {
/*     */     private ConnectedState()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void enter()
/*     */     {
/* 462 */       RealTimeLocation.access$802(RealTimeLocation.this, RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_CONNECTED);
/* 463 */       RealTimeLocation.this.onStatusChanged(RealTimeLocation.this.mCurrentState);
/* 464 */       RealTimeLocation.this.startTimer();
/* 465 */       Log.d(RealTimeLocation.TAG, "connected enter : current = " + RealTimeLocation.this.mCurrentState);
/*     */     }
/*     */ 
/*     */     public boolean processMessage(android.os.Message msg)
/*     */     {
/* 470 */       String id = null;
/* 471 */       io.rong.imlib.model.Message message = null;
/* 472 */       MessageContent content = null;
/*     */       RealTimeLocation.ParticipantWatcher watcher;
/* 474 */       switch (msg.what) {
/*     */       case 8:
/* 476 */         RealTimeLocation.this.onError(RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_START_FAILURE);
/* 477 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mTerminalState);
/* 478 */         break;
/*     */       case 9:
/* 480 */         RealTimeLocation.this.onError(RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_JOIN_FAILURE);
/* 481 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mTerminalState);
/* 482 */         break;
/*     */       case 4:
/* 484 */         watcher = new RealTimeLocation.ParticipantWatcher(RealTimeLocation.this, id);
/* 485 */         watcher.start();
/*     */ 
/* 487 */         id = (String)(String)msg.obj;
/* 488 */         RealTimeLocation.this.mWatcher.put(id, watcher);
/* 489 */         RealTimeLocation.this.mParticipants.add(id);
/* 490 */         RealTimeLocation.this.onParticipantsJoin(id);
/* 491 */         RealTimeLocation.this.getHandler().sendEmptyMessage(7);
/* 492 */         break;
/*     */       case 5:
/*     */       case 12:
/* 495 */         id = (String)(String)msg.obj;
/* 496 */         RealTimeLocation.this.mParticipants.remove(id);
/* 497 */         ((RealTimeLocation.ParticipantWatcher)RealTimeLocation.this.mWatcher.get(id)).stop();
/* 498 */         RealTimeLocation.this.mWatcher.remove(id);
/* 499 */         RealTimeLocation.this.onParticipantQuit(id);
/* 500 */         if (RealTimeLocation.this.mParticipants.size() != 1) break;
/* 501 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mOutgoingState); break;
/*     */       case 2:
/* 504 */         RealTimeLocation.this.sendQuitMessage();
/* 505 */         RealTimeLocation.this.mParticipants.remove(RealTimeLocation.this.mSelfId);
/* 506 */         if (RealTimeLocation.this.mParticipants.size() == 0)
/* 507 */           RealTimeLocation.this.transitionTo(RealTimeLocation.this.mTerminalState);
/*     */         else
/* 509 */           RealTimeLocation.this.transitionTo(RealTimeLocation.this.mIncomingState);
/* 510 */         break;
/*     */       case 7:
/*     */       case 10:
/* 513 */         RealTimeLocation.this.sendLocationMessage();
/* 514 */         RealTimeLocation.this.updateSelfLocation();
/* 515 */         break;
/*     */       case 6:
/* 517 */         message = (io.rong.imlib.model.Message)(io.rong.imlib.model.Message)msg.obj;
/* 518 */         id = message.getSenderUserId();
/* 519 */         if (RealTimeLocation.this.mWatcher.get(id) == null) {
/* 520 */           watcher = new RealTimeLocation.ParticipantWatcher(RealTimeLocation.this, id);
/* 521 */           watcher.start();
/* 522 */           RealTimeLocation.this.mWatcher.put(id, watcher);
/* 523 */           RealTimeLocation.this.mParticipants.add(id);
/* 524 */           RealTimeLocation.this.onParticipantsJoin(id);
/*     */         } else {
/* 526 */           ((RealTimeLocation.ParticipantWatcher)RealTimeLocation.this.mWatcher.get(id)).update();
/*     */         }
/* 528 */         content = message.getContent();
/* 529 */         RealTimeLocationStatusMessage coor = (RealTimeLocationStatusMessage)content;
/* 530 */         RealTimeLocation.this.onReceiveLocation(coor.getLatitude(), coor.getLongitude(), id);
/* 531 */         break;
/*     */       case 14:
/* 533 */         RealTimeLocation.this.onError(RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_GPS_DISABLED);
/* 534 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mTerminalState);
/* 535 */         break;
/*     */       case 13:
/* 537 */         RealTimeLocation.this.onError(RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_NETWORK_UNAVAILABLE);
/* 538 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mTerminalState);
/* 539 */         break;
/*     */       case 3:
/*     */       case 11:
/*     */       }
/* 543 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class IncomingState extends State
/*     */   {
/*     */     private IncomingState()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void enter()
/*     */     {
/* 385 */       RealTimeLocation.this.stopTimer();
/* 386 */       RealTimeLocation.access$802(RealTimeLocation.this, RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_INCOMING);
/* 387 */       RealTimeLocation.this.onStatusChanged(RealTimeLocation.this.mCurrentState);
/* 388 */       Log.d(RealTimeLocation.TAG, "incoming enter : current = " + RealTimeLocation.this.mCurrentState);
/*     */     }
/*     */ 
/*     */     public boolean processMessage(android.os.Message msg)
/*     */     {
/* 393 */       MessageContent content = null;
/*     */       RealTimeLocation.ParticipantWatcher watcher;
/*     */       String id;
/* 396 */       switch (msg.what) {
/*     */       case 1:
/* 398 */         RealTimeLocation.this.sendJoinMessage();
/* 399 */         RealTimeLocation.access$902(RealTimeLocation.this, RealTimeLocation.this.mClient.getCurrentUserId());
/* 400 */         RealTimeLocation.this.mParticipants.add(RealTimeLocation.this.mSelfId);
/* 401 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mConnectedState);
/* 402 */         break;
/*     */       case 4:
/* 404 */         String userId = (String)msg.obj;
/* 405 */         watcher = new RealTimeLocation.ParticipantWatcher(RealTimeLocation.this, userId);
/* 406 */         watcher.start();
/* 407 */         RealTimeLocation.this.mWatcher.put(userId, watcher);
/* 408 */         RealTimeLocation.this.mParticipants.add(userId);
/* 409 */         RealTimeLocation.this.onParticipantsJoin(userId);
/* 410 */         break;
/*     */       case 5:
/*     */       case 12:
/* 413 */         id = (String)msg.obj;
/* 414 */         if (RealTimeLocation.this.mWatcher.get(id) != null) {
/* 415 */           ((RealTimeLocation.ParticipantWatcher)RealTimeLocation.this.mWatcher.get(id)).stop();
/* 416 */           RealTimeLocation.this.mWatcher.remove(id);
/* 417 */           RealTimeLocation.this.mParticipants.remove(id);
/* 418 */           RealTimeLocation.this.onParticipantQuit(id);
/*     */         }
/* 420 */         if (RealTimeLocation.this.mParticipants.size() != 0) break;
/* 421 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mTerminalState); break;
/*     */       case 14:
/* 425 */         RealTimeLocation.this.onError(RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_GPS_DISABLED);
/* 426 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mTerminalState);
/* 427 */         break;
/*     */       case 13:
/* 429 */         RealTimeLocation.this.onError(RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_NETWORK_UNAVAILABLE);
/* 430 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mTerminalState);
/* 431 */         break;
/*     */       case 6:
/* 433 */         io.rong.imlib.model.Message message = (io.rong.imlib.model.Message)(io.rong.imlib.model.Message)msg.obj;
/* 434 */         id = message.getSenderUserId();
/*     */ 
/* 437 */         if (RealTimeLocation.this.mWatcher.get(id) == null) {
/* 438 */           watcher = new RealTimeLocation.ParticipantWatcher(RealTimeLocation.this, id);
/* 439 */           watcher.start();
/* 440 */           RealTimeLocation.this.mWatcher.put(id, watcher);
/* 441 */           RealTimeLocation.this.mParticipants.add(id);
/* 442 */           RealTimeLocation.this.onParticipantsJoin(id);
/*     */         } else {
/* 444 */           ((RealTimeLocation.ParticipantWatcher)RealTimeLocation.this.mWatcher.get(id)).update();
/*     */         }
/* 446 */         content = message.getContent();
/* 447 */         RealTimeLocationStatusMessage coor = (RealTimeLocationStatusMessage)content;
/* 448 */         RealTimeLocation.this.onReceiveLocation(coor.getLatitude(), coor.getLongitude(), id);
/* 449 */         break;
/*     */       case 2:
/*     */       case 3:
/*     */       case 7:
/*     */       case 8:
/*     */       case 9:
/*     */       case 10:
/* 453 */       case 11: } return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class OutgoingState extends State
/*     */   {
/*     */     private OutgoingState()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void enter()
/*     */     {
/* 330 */       RealTimeLocation.access$802(RealTimeLocation.this, RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_OUTGOING);
/* 331 */       RealTimeLocation.this.onStatusChanged(RealTimeLocation.this.mCurrentState);
/* 332 */       RealTimeLocation.this.startTimer();
/* 333 */       Log.d(RealTimeLocation.TAG, "outgoing enter : current = " + RealTimeLocation.this.mCurrentState);
/*     */     }
/*     */ 
/*     */     public boolean processMessage(android.os.Message msg)
/*     */     {
/* 338 */       io.rong.imlib.model.Message message = null;
/* 339 */       MessageContent content = null;
/*     */ 
/* 341 */       switch (msg.what) {
/*     */       case 8:
/* 343 */         RealTimeLocation.this.onError(RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_START_FAILURE);
/* 344 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mIdleState);
/* 345 */         break;
/*     */       case 4:
/* 347 */         String userId = (String)(String)msg.obj;
/* 348 */         RealTimeLocation.ParticipantWatcher watcher = new RealTimeLocation.ParticipantWatcher(RealTimeLocation.this, userId);
/* 349 */         watcher.start();
/*     */ 
/* 351 */         RealTimeLocation.this.mWatcher.put(userId, watcher);
/* 352 */         RealTimeLocation.this.mParticipants.add(userId);
/* 353 */         RealTimeLocation.this.onParticipantsJoin(userId);
/* 354 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mConnectedState);
/* 355 */         break;
/*     */       case 2:
/* 357 */         RealTimeLocation.this.sendQuitMessage();
/* 358 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mTerminalState);
/* 359 */         break;
/*     */       case 7:
/*     */       case 10:
/* 362 */         RealTimeLocation.this.sendLocationMessage();
/* 363 */         RealTimeLocation.this.updateSelfLocation();
/* 364 */         break;
/*     */       case 14:
/* 366 */         RealTimeLocation.this.onError(RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_GPS_DISABLED);
/* 367 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mTerminalState);
/* 368 */         break;
/*     */       case 13:
/* 370 */         RealTimeLocation.this.onError(RealTimeLocationConstant.RealTimeLocationErrorCode.RC_REAL_TIME_LOCATION_NETWORK_UNAVAILABLE);
/* 371 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mTerminalState);
/* 372 */         break;
/*     */       case 3:
/*     */       case 5:
/*     */       case 6:
/*     */       case 9:
/*     */       case 11:
/* 376 */       case 12: } return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class IdleState extends State
/*     */   {
/*     */     private IdleState()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void enter()
/*     */     {
/* 282 */       RealTimeLocation.access$802(RealTimeLocation.this, RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_IDLE);
/* 283 */       if (RealTimeLocation.this.mSelfId != null)
/* 284 */         RealTimeLocation.this.onStatusChanged(RealTimeLocation.this.mCurrentState);
/* 285 */       Log.d(RealTimeLocation.TAG, "idle enter : current = " + RealTimeLocation.this.mCurrentState);
/*     */     }
/*     */ 
/*     */     public boolean processMessage(android.os.Message msg)
/*     */     {
/*     */       String id;
/*     */       RealTimeLocation.ParticipantWatcher watcher;
/* 293 */       switch (msg.what) {
/*     */       case 0:
/* 295 */         RealTimeLocation.this.sendStartMessage();
/* 296 */         RealTimeLocation.access$902(RealTimeLocation.this, RealTimeLocation.this.mClient.getCurrentUserId());
/* 297 */         RealTimeLocation.this.mParticipants.add(RealTimeLocation.this.mSelfId);
/* 298 */         RealTimeLocation.this.updateSelfLocation();
/* 299 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mOutgoingState);
/* 300 */         break;
/*     */       case 3:
/* 302 */         id = (String)(String)msg.obj;
/* 303 */         watcher = new RealTimeLocation.ParticipantWatcher(RealTimeLocation.this, id);
/* 304 */         watcher.start();
/* 305 */         RealTimeLocation.this.mWatcher.put(id, watcher);
/* 306 */         RealTimeLocation.this.mParticipants.add(id);
/* 307 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mIncomingState);
/* 308 */         break;
/*     */       case 6:
/* 310 */         io.rong.imlib.model.Message message = (io.rong.imlib.model.Message)(io.rong.imlib.model.Message)msg.obj;
/* 311 */         id = message.getSenderUserId();
/* 312 */         watcher = new RealTimeLocation.ParticipantWatcher(RealTimeLocation.this, id);
/* 313 */         watcher.start();
/* 314 */         RealTimeLocation.this.mWatcher.put(id, watcher);
/* 315 */         RealTimeLocation.this.mParticipants.add(id);
/* 316 */         RealTimeLocation.this.transitionTo(RealTimeLocation.this.mIncomingState);
/* 317 */         break;
/*     */       }
/*     */ 
/* 321 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.location.RealTimeLocation
 * JD-Core Version:    0.6.0
 */
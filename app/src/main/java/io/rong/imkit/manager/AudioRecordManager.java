/*     */ package io.rong.imkit.manager;
/*     */ 
/*     */ import android.annotation.TargetApi;
/*     */ import android.content.Context;
/*     */ import android.content.res.Resources;
/*     */ import android.content.res.Resources.NotFoundException;
/*     */ import android.media.AudioManager;
/*     */ import android.media.AudioManager.OnAudioFocusChangeListener;
/*     */ import android.media.MediaRecorder;
/*     */ import android.net.Uri;
/*     */ import android.os.Build.VERSION;
/*     */ import android.os.Handler;
/*     */ import android.os.Handler.Callback;
/*     */ import android.os.SystemClock;
/*     */ import android.telephony.PhoneStateListener;
/*     */ import android.telephony.TelephonyManager;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.PopupWindow;
/*     */ import android.widget.TextView;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.integer;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imlib.IRongCallback.ISendMessageCallback;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.TypingMessage.TypingMessageManager;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.message.VoiceMessage;
/*     */ import java.io.File;
/*     */ 
/*     */ public class AudioRecordManager
/*     */   implements Handler.Callback
/*     */ {
/*     */   private static final String TAG = "AudioRecordManager";
/*  38 */   private int RECORD_INTERVAL = 60;
/*     */   private IAudioState mCurAudioState;
/*     */   private View mRootView;
/*     */   private Context mContext;
/*     */   private Conversation.ConversationType mConversationType;
/*     */   private String mTargetId;
/*     */   private Handler mHandler;
/*     */   private AudioManager mAudioManager;
/*     */   private MediaRecorder mMediaRecorder;
/*     */   private Uri mAudioPath;
/*     */   private long smStartRecTime;
/*     */   private AudioManager.OnAudioFocusChangeListener mAfChangeListener;
/*     */   private PopupWindow mRecordWindow;
/*     */   private ImageView mStateIV;
/*     */   private TextView mStateTV;
/*     */   private TextView mTimerTV;
/*  96 */   IAudioState idleState = new IdleState();
/*     */ 
/* 129 */   IAudioState recordState = new RecordState();
/*     */ 
/* 195 */   IAudioState cancelState = new CancelState();
/*     */ 
/* 241 */   IAudioState timerState = new TimerState();
/*     */ 
/*     */   public static AudioRecordManager getInstance()
/*     */   {
/*  64 */     return SingletonHolder.sInstance;
/*     */   }
/*     */   @TargetApi(21)
/*     */   private AudioRecordManager() {
/*  69 */     RLog.d("AudioRecordManager", "AudioRecordManager");
/*  70 */     if (Build.VERSION.SDK_INT < 21) {
/*     */       try {
/*  72 */         TelephonyManager manager = (TelephonyManager)RongContext.getInstance().getSystemService("phone");
/*  73 */         manager.listen(new PhoneStateListener()
/*     */         {
/*     */           public void onCallStateChanged(int state, String incomingNumber) {
/*  76 */             switch (state) {
/*     */             case 0:
/*  78 */               break;
/*     */             case 1:
/*  80 */               AudioRecordManager.this.sendEmptyMessage(6);
/*  81 */               break;
/*     */             case 2:
/*     */             }
/*     */ 
/*  85 */             super.onCallStateChanged(state, incomingNumber);
/*     */           }
/*     */         }
/*     */         , 32);
/*     */       }
/*     */       catch (SecurityException e)
/*     */       {
/*  89 */         e.printStackTrace();
/*     */       }
/*     */     }
/*  92 */     this.mCurAudioState = this.idleState;
/*  93 */     this.idleState.enter();
/*     */   }
/*     */ 
/*     */   public final boolean handleMessage(android.os.Message msg)
/*     */   {
/* 297 */     RLog.i("AudioRecordManager", "handleMessage " + msg.what);
/*     */     AudioStateMessage m;
/* 298 */     switch (msg.what) {
/*     */     case 7:
/* 300 */       m = AudioStateMessage.obtain();
/* 301 */       m.what = msg.what;
/* 302 */       m.obj = msg.obj;
/* 303 */       sendMessage(m);
/* 304 */       break;
/*     */     case 8:
/* 306 */       m = AudioStateMessage.obtain();
/* 307 */       m.what = 7;
/* 308 */       m.obj = msg.obj;
/* 309 */       sendMessage(m);
/* 310 */       break;
/*     */     case 2:
/* 312 */       sendEmptyMessage(2);
/*     */     }
/*     */ 
/* 315 */     return false;
/*     */   }
/*     */ 
/*     */   private void initView(View root) {
/* 319 */     this.mHandler = new Handler(root.getHandler().getLooper(), this);
/*     */ 
/* 321 */     LayoutInflater inflater = LayoutInflater.from(root.getContext());
/* 322 */     View view = inflater.inflate(R.layout.rc_wi_vo_popup, null);
/*     */ 
/* 324 */     this.mStateIV = ((ImageView)view.findViewById(R.id.rc_audio_state_image));
/* 325 */     this.mStateTV = ((TextView)view.findViewById(R.id.rc_audio_state_text));
/* 326 */     this.mTimerTV = ((TextView)view.findViewById(R.id.rc_audio_timer));
/*     */ 
/* 328 */     this.mRecordWindow = new PopupWindow(view, -1, -1);
/* 329 */     this.mRecordWindow.showAtLocation(root, 17, 0, 0);
/* 330 */     this.mRecordWindow.setFocusable(true);
/* 331 */     this.mRecordWindow.setOutsideTouchable(false);
/* 332 */     this.mRecordWindow.setTouchable(false);
/*     */   }
/*     */ 
/*     */   private void setTimeoutView(int counter) {
/* 336 */     if (this.mRecordWindow != null) {
/* 337 */       this.mStateIV.setVisibility(8);
/* 338 */       this.mStateTV.setVisibility(0);
/* 339 */       this.mStateTV.setText(R.string.rc_voice_rec);
/* 340 */       this.mStateTV.setBackgroundResource(17170445);
/* 341 */       this.mTimerTV.setText(String.format("%s", new Object[] { Integer.valueOf(counter) }));
/* 342 */       this.mTimerTV.setVisibility(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setRecordingView() {
/* 347 */     RLog.d("AudioRecordManager", "setRecordingView");
/*     */ 
/* 349 */     if (this.mRecordWindow != null) {
/* 350 */       this.mStateIV.setVisibility(0);
/* 351 */       this.mStateIV.setImageResource(R.drawable.rc_ic_volume_1);
/* 352 */       this.mStateTV.setVisibility(0);
/* 353 */       this.mStateTV.setText(R.string.rc_voice_rec);
/* 354 */       this.mStateTV.setBackgroundResource(17170445);
/* 355 */       this.mTimerTV.setVisibility(8);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setCancelView() {
/* 360 */     RLog.d("AudioRecordManager", "setCancelView");
/*     */ 
/* 362 */     if (this.mRecordWindow != null) {
/* 363 */       this.mTimerTV.setVisibility(8);
/* 364 */       this.mStateIV.setVisibility(0);
/* 365 */       this.mStateIV.setImageResource(R.drawable.rc_ic_volume_cancel);
/* 366 */       this.mStateTV.setVisibility(0);
/* 367 */       this.mStateTV.setText(R.string.rc_voice_cancel);
/* 368 */       this.mStateTV.setBackgroundResource(R.drawable.rc_corner_voice_style);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void destroyView() {
/* 373 */     RLog.d("AudioRecordManager", "destroyView");
/* 374 */     if (this.mRecordWindow != null) {
/* 375 */       this.mHandler.removeMessages(7);
/* 376 */       this.mHandler.removeMessages(8);
/* 377 */       this.mHandler.removeMessages(2);
/* 378 */       this.mRecordWindow.dismiss();
/* 379 */       this.mRecordWindow = null;
/* 380 */       this.mStateIV = null;
/* 381 */       this.mStateTV = null;
/* 382 */       this.mTimerTV = null;
/* 383 */       this.mHandler = null;
/* 384 */       this.mContext = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setMaxVoiceDuration(int maxVoiceDuration) {
/* 389 */     this.RECORD_INTERVAL = maxVoiceDuration;
/*     */   }
/*     */ 
/*     */   public int getMaxVoiceDuration() {
/* 393 */     return this.RECORD_INTERVAL;
/*     */   }
/*     */ 
/*     */   public void startRecord(View rootView, Conversation.ConversationType conversationType, String targetId) {
/* 397 */     this.mRootView = rootView;
/* 398 */     this.mContext = rootView.getContext().getApplicationContext();
/* 399 */     this.mConversationType = conversationType;
/* 400 */     this.mTargetId = targetId;
/* 401 */     this.mAudioManager = ((AudioManager)this.mContext.getSystemService("audio"));
/*     */ 
/* 403 */     if (this.mAfChangeListener != null) {
/* 404 */       this.mAudioManager.abandonAudioFocus(this.mAfChangeListener);
/* 405 */       this.mAfChangeListener = null;
/*     */     }
/* 407 */     this.mAfChangeListener = new AudioManager.OnAudioFocusChangeListener() {
/*     */       public void onAudioFocusChange(int focusChange) {
/* 409 */         RLog.d("AudioRecordManager", "OnAudioFocusChangeListener " + focusChange);
/* 410 */         if (focusChange == -1) {
/* 411 */           AudioRecordManager.this.mAudioManager.abandonAudioFocus(AudioRecordManager.this.mAfChangeListener);
/* 412 */           AudioRecordManager.access$1802(AudioRecordManager.this, null);
/* 413 */           AudioRecordManager.this.sendEmptyMessage(6);
/*     */         }
/*     */       }
/*     */     };
/* 417 */     sendEmptyMessage(1);
/*     */ 
/* 419 */     if (TypingMessageManager.getInstance().isShowMessageTyping())
/* 420 */       RongIMClient.getInstance().sendTypingStatus(conversationType, targetId, "RC:VcMsg");
/*     */   }
/*     */ 
/*     */   public void willCancelRecord()
/*     */   {
/* 425 */     sendEmptyMessage(3);
/*     */   }
/*     */ 
/*     */   public void continueRecord() {
/* 429 */     sendEmptyMessage(4);
/*     */   }
/*     */ 
/*     */   public void stopRecord() {
/* 433 */     sendEmptyMessage(5);
/*     */   }
/*     */ 
/*     */   void sendMessage(AudioStateMessage message)
/*     */   {
/* 438 */     this.mCurAudioState.handleMessage(message);
/*     */   }
/*     */ 
/*     */   void sendEmptyMessage(int event) {
/* 442 */     AudioStateMessage message = AudioStateMessage.obtain();
/* 443 */     message.what = event;
/* 444 */     this.mCurAudioState.handleMessage(message);
/*     */   }
/*     */ 
/*     */   private void startRec() {
/* 448 */     RLog.d("AudioRecordManager", "startRec");
/*     */     try
/*     */     {
/* 451 */       muteAudioFocus(this.mAudioManager, true);
/* 452 */       this.mAudioManager.setMode(0);
/* 453 */       this.mMediaRecorder = new MediaRecorder();
/*     */       try {
/* 455 */         int bps = this.mContext.getResources().getInteger(R.integer.rc_audio_encoding_bit_rate);
/* 456 */         this.mMediaRecorder.setAudioSamplingRate(8000);
/* 457 */         this.mMediaRecorder.setAudioEncodingBitRate(bps);
/*     */       } catch (Resources.NotFoundException e) {
/* 459 */         e.printStackTrace();
/*     */       }
/* 461 */       this.mMediaRecorder.setAudioChannels(1);
/* 462 */       this.mMediaRecorder.setAudioSource(1);
/* 463 */       this.mMediaRecorder.setOutputFormat(3);
/* 464 */       this.mMediaRecorder.setAudioEncoder(1);
/* 465 */       this.mAudioPath = Uri.fromFile(new File(this.mContext.getCacheDir(), System.currentTimeMillis() + "temp.voice"));
/* 466 */       this.mMediaRecorder.setOutputFile(this.mAudioPath.getPath());
/* 467 */       this.mMediaRecorder.prepare();
/* 468 */       this.mMediaRecorder.start();
/*     */ 
/* 470 */       android.os.Message message = android.os.Message.obtain();
/* 471 */       message.what = 7;
/* 472 */       message.obj = Integer.valueOf(10);
/* 473 */       this.mHandler.sendMessageDelayed(message, this.RECORD_INTERVAL * 1000 - 10000);
/*     */     } catch (Exception e) {
/* 475 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean checkAudioTimeLength() {
/* 480 */     long delta = SystemClock.elapsedRealtime() - this.smStartRecTime;
/* 481 */     return delta < 1000L;
/*     */   }
/*     */ 
/*     */   private void stopRec() {
/* 485 */     RLog.d("AudioRecordManager", "stopRec");
/*     */     try {
/* 487 */       muteAudioFocus(this.mAudioManager, false);
/* 488 */       if (this.mMediaRecorder != null) {
/* 489 */         this.mMediaRecorder.stop();
/* 490 */         this.mMediaRecorder.release();
/* 491 */         this.mMediaRecorder = null;
/*     */       }
/*     */     } catch (Exception e) {
/* 494 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void deleteAudioFile() {
/* 499 */     RLog.d("AudioRecordManager", "deleteAudioFile");
/*     */ 
/* 501 */     if (this.mAudioPath != null) {
/* 502 */       File file = new File(this.mAudioPath.getPath());
/* 503 */       if (file.exists()) file.delete(); 
/*     */     }
/*     */   }
/*     */ 
/*     */   private void sendAudioFile()
/*     */   {
/* 508 */     RLog.d("AudioRecordManager", "sendAudioFile path = " + this.mAudioPath);
/* 509 */     if (this.mAudioPath != null) {
/* 510 */       int duration = (int)(SystemClock.elapsedRealtime() - this.smStartRecTime) / 1000;
/* 511 */       VoiceMessage voiceMessage = VoiceMessage.obtain(this.mAudioPath, duration);
/* 512 */       RongIM.getInstance().sendMessage(io.rong.imlib.model.Message.obtain(this.mTargetId, this.mConversationType, voiceMessage), null, null, new IRongCallback.ISendMessageCallback()
/*     */       {
/*     */         public void onAttached(io.rong.imlib.model.Message message)
/*     */         {
/*     */         }
/*     */ 
/*     */         public void onSuccess(io.rong.imlib.model.Message message)
/*     */         {
/*     */         }
/*     */ 
/*     */         public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode)
/*     */         {
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   private void audioDBChanged()
/*     */   {
/* 532 */     if (this.mMediaRecorder != null) {
/* 533 */       int db = this.mMediaRecorder.getMaxAmplitude() / 600;
/* 534 */       switch (db / 5) {
/*     */       case 0:
/* 536 */         this.mStateIV.setImageResource(R.drawable.rc_ic_volume_1);
/* 537 */         break;
/*     */       case 1:
/* 539 */         this.mStateIV.setImageResource(R.drawable.rc_ic_volume_2);
/* 540 */         break;
/*     */       case 2:
/* 542 */         this.mStateIV.setImageResource(R.drawable.rc_ic_volume_3);
/* 543 */         break;
/*     */       case 3:
/* 545 */         this.mStateIV.setImageResource(R.drawable.rc_ic_volume_4);
/* 546 */         break;
/*     */       case 4:
/* 548 */         this.mStateIV.setImageResource(R.drawable.rc_ic_volume_5);
/* 549 */         break;
/*     */       case 5:
/* 551 */         this.mStateIV.setImageResource(R.drawable.rc_ic_volume_6);
/* 552 */         break;
/*     */       case 6:
/* 554 */         this.mStateIV.setImageResource(R.drawable.rc_ic_volume_7);
/* 555 */         break;
/*     */       default:
/* 557 */         this.mStateIV.setImageResource(R.drawable.rc_ic_volume_8);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void muteAudioFocus(AudioManager audioManager, boolean bMute)
/*     */   {
/* 564 */     if (Build.VERSION.SDK_INT < 8)
/*     */     {
/* 566 */       RLog.d("AudioRecordManager", "muteAudioFocus Android 2.1 and below can not stop music");
/* 567 */       return;
/*     */     }
/* 569 */     if (bMute) {
/* 570 */       audioManager.requestAudioFocus(this.mAfChangeListener, 3, 2);
/*     */     } else {
/* 572 */       audioManager.abandonAudioFocus(this.mAfChangeListener);
/* 573 */       this.mAfChangeListener = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   class TimerState extends IAudioState
/*     */   {
/*     */     TimerState()
/*     */     {
/*     */     }
/*     */ 
/*     */     void handleMessage(AudioStateMessage msg)
/*     */     {
/* 246 */       RLog.d("AudioRecordManager", getClass().getSimpleName() + " handleMessage : " + msg.what);
/* 247 */       switch (msg.what) {
/*     */       case 7:
/* 249 */         int counter = ((Integer)msg.obj).intValue();
/* 250 */         if (counter > 0) {
/* 251 */           android.os.Message message = android.os.Message.obtain();
/* 252 */           message.what = 8;
/* 253 */           message.obj = Integer.valueOf(counter - 1);
/* 254 */           AudioRecordManager.this.mHandler.sendMessageDelayed(message, 1000L);
/* 255 */           AudioRecordManager.this.setTimeoutView(counter);
/*     */         } else {
/* 257 */           AudioRecordManager.this.mHandler.postDelayed(new Runnable()
/*     */           {
/*     */             public void run() {
/* 260 */               AudioRecordManager.this.stopRec();
/* 261 */               AudioRecordManager.this.sendAudioFile();
/* 262 */               AudioRecordManager.this.destroyView();
/*     */             }
/*     */           }
/*     */           , 500L);
/*     */ 
/* 265 */           AudioRecordManager.access$702(AudioRecordManager.this, AudioRecordManager.this.idleState);
/*     */         }
/* 267 */         break;
/*     */       case 5:
/* 269 */         AudioRecordManager.this.mHandler.postDelayed(new Runnable()
/*     */         {
/*     */           public void run() {
/* 272 */             AudioRecordManager.this.stopRec();
/* 273 */             AudioRecordManager.this.sendAudioFile();
/* 274 */             AudioRecordManager.this.destroyView();
/*     */           }
/*     */         }
/*     */         , 500L);
/*     */ 
/* 277 */         AudioRecordManager.access$702(AudioRecordManager.this, AudioRecordManager.this.idleState);
/* 278 */         AudioRecordManager.this.idleState.enter();
/* 279 */         break;
/*     */       case 6:
/* 281 */         AudioRecordManager.this.stopRec();
/* 282 */         AudioRecordManager.this.destroyView();
/* 283 */         AudioRecordManager.this.deleteAudioFile();
/* 284 */         AudioRecordManager.access$702(AudioRecordManager.this, AudioRecordManager.this.idleState);
/* 285 */         AudioRecordManager.this.idleState.enter();
/* 286 */         break;
/*     */       case 3:
/* 288 */         AudioRecordManager.this.setCancelView();
/* 289 */         AudioRecordManager.access$702(AudioRecordManager.this, AudioRecordManager.this.cancelState);
/*     */       case 4:
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   class CancelState extends IAudioState
/*     */   {
/*     */     CancelState()
/*     */     {
/*     */     }
/*     */ 
/*     */     void handleMessage(AudioStateMessage msg)
/*     */     {
/* 200 */       RLog.d("AudioRecordManager", getClass().getSimpleName() + " handleMessage : " + msg.what);
/* 201 */       switch (msg.what) {
/*     */       case 1:
/* 203 */         break;
/*     */       case 4:
/* 205 */         AudioRecordManager.this.setRecordingView();
/* 206 */         AudioRecordManager.access$702(AudioRecordManager.this, AudioRecordManager.this.recordState);
/* 207 */         AudioRecordManager.this.sendEmptyMessage(2);
/* 208 */         break;
/*     */       case 5:
/*     */       case 6:
/* 211 */         AudioRecordManager.this.stopRec();
/* 212 */         AudioRecordManager.this.destroyView();
/* 213 */         AudioRecordManager.this.deleteAudioFile();
/* 214 */         AudioRecordManager.access$702(AudioRecordManager.this, AudioRecordManager.this.idleState);
/* 215 */         AudioRecordManager.this.idleState.enter();
/* 216 */         break;
/*     */       case 7:
/* 218 */         int counter = ((Integer)msg.obj).intValue();
/* 219 */         if (counter > 0) {
/* 220 */           android.os.Message message = android.os.Message.obtain();
/* 221 */           message.what = 8;
/* 222 */           message.obj = Integer.valueOf(counter - 1);
/* 223 */           AudioRecordManager.this.mHandler.sendMessageDelayed(message, 1000L);
/*     */         } else {
/* 225 */           AudioRecordManager.this.mHandler.postDelayed(new Runnable()
/*     */           {
/*     */             public void run() {
/* 228 */               AudioRecordManager.this.stopRec();
/* 229 */               AudioRecordManager.this.sendAudioFile();
/* 230 */               AudioRecordManager.this.destroyView();
/*     */             }
/*     */           }
/*     */           , 500L);
/*     */ 
/* 233 */           AudioRecordManager.access$702(AudioRecordManager.this, AudioRecordManager.this.idleState);
/* 234 */           AudioRecordManager.this.idleState.enter();
/*     */         }
/*     */       case 2:
/*     */       case 3:
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   class RecordState extends IAudioState
/*     */   {
/*     */     RecordState()
/*     */     {
/*     */     }
/*     */ 
/*     */     void handleMessage(AudioStateMessage msg)
/*     */     {
/* 134 */       RLog.d("AudioRecordManager", getClass().getSimpleName() + " handleMessage : " + msg.what);
/* 135 */       switch (msg.what) {
/*     */       case 2:
/* 137 */         AudioRecordManager.this.audioDBChanged();
/* 138 */         AudioRecordManager.this.mHandler.sendEmptyMessageDelayed(2, 150L);
/* 139 */         break;
/*     */       case 3:
/* 141 */         AudioRecordManager.this.setCancelView();
/* 142 */         AudioRecordManager.access$702(AudioRecordManager.this, AudioRecordManager.this.cancelState);
/* 143 */         break;
/*     */       case 5:
/* 145 */         boolean checked = AudioRecordManager.this.checkAudioTimeLength();
/* 146 */         if (checked) {
/* 147 */           AudioRecordManager.this.mStateIV.setImageResource(R.drawable.rc_ic_volume_wraning);
/* 148 */           AudioRecordManager.this.mStateTV.setText(R.string.rc_voice_short);
/*     */         }
/* 150 */         AudioRecordManager.this.mHandler.postDelayed(new Runnable(checked)
/*     */         {
/*     */           public void run() {
/* 153 */             AudioRecordManager.this.stopRec();
/* 154 */             if (!this.val$checked) {
/* 155 */               AudioRecordManager.this.sendAudioFile();
/*     */             }
/* 157 */             AudioRecordManager.this.destroyView();
/*     */           }
/*     */         }
/*     */         , 500L);
/*     */ 
/* 160 */         AudioRecordManager.access$702(AudioRecordManager.this, AudioRecordManager.this.idleState);
/* 161 */         break;
/*     */       case 7:
/* 163 */         int counter = ((Integer)msg.obj).intValue();
/* 164 */         AudioRecordManager.this.setTimeoutView(counter);
/* 165 */         AudioRecordManager.access$702(AudioRecordManager.this, AudioRecordManager.this.timerState);
/*     */ 
/* 167 */         if (counter > 0) {
/* 168 */           android.os.Message message = android.os.Message.obtain();
/* 169 */           message.what = 8;
/* 170 */           message.obj = Integer.valueOf(counter - 1);
/* 171 */           AudioRecordManager.this.mHandler.sendMessageDelayed(message, 1000L);
/*     */         } else {
/* 173 */           AudioRecordManager.this.mHandler.postDelayed(new Runnable()
/*     */           {
/*     */             public void run() {
/* 176 */               AudioRecordManager.this.stopRec();
/* 177 */               AudioRecordManager.this.sendAudioFile();
/* 178 */               AudioRecordManager.this.destroyView();
/*     */             }
/*     */           }
/*     */           , 500L);
/*     */ 
/* 181 */           AudioRecordManager.access$702(AudioRecordManager.this, AudioRecordManager.this.idleState);
/*     */         }
/* 183 */         break;
/*     */       case 6:
/* 185 */         AudioRecordManager.this.stopRec();
/* 186 */         AudioRecordManager.this.destroyView();
/* 187 */         AudioRecordManager.this.deleteAudioFile();
/* 188 */         AudioRecordManager.access$702(AudioRecordManager.this, AudioRecordManager.this.idleState);
/* 189 */         AudioRecordManager.this.idleState.enter();
/*     */       case 4:
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   class IdleState extends IAudioState
/*     */   {
/*     */     public IdleState()
/*     */     {
/* 100 */       RLog.d("AudioRecordManager", "IdleState");
/*     */     }
/*     */ 
/*     */     void enter()
/*     */     {
/* 105 */       super.enter();
/* 106 */       if (AudioRecordManager.this.mHandler != null) {
/* 107 */         AudioRecordManager.this.mHandler.removeMessages(7);
/* 108 */         AudioRecordManager.this.mHandler.removeMessages(8);
/* 109 */         AudioRecordManager.this.mHandler.removeMessages(2);
/*     */       }
/*     */     }
/*     */ 
/*     */     void handleMessage(AudioStateMessage msg)
/*     */     {
/* 115 */       RLog.d("AudioRecordManager", "IdleState handleMessage : " + msg.what);
/* 116 */       switch (msg.what) {
/*     */       case 1:
/* 118 */         AudioRecordManager.this.initView(AudioRecordManager.this.mRootView);
/* 119 */         AudioRecordManager.this.setRecordingView();
/* 120 */         AudioRecordManager.this.startRec();
/* 121 */         AudioRecordManager.access$602(AudioRecordManager.this, SystemClock.elapsedRealtime());
/* 122 */         AudioRecordManager.access$702(AudioRecordManager.this, AudioRecordManager.this.recordState);
/* 123 */         AudioRecordManager.this.sendEmptyMessage(2);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class SingletonHolder
/*     */   {
/*  60 */     static AudioRecordManager sInstance = new AudioRecordManager(null);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.manager.AudioRecordManager
 * JD-Core Version:    0.6.0
 */
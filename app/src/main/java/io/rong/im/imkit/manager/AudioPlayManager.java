/*     */ package io.rong.imkit.manager;
/*     */ 
/*     */ import android.annotation.TargetApi;
/*     */ import android.content.Context;
/*     */ import android.hardware.Sensor;
/*     */ import android.hardware.SensorEvent;
/*     */ import android.hardware.SensorEventListener;
/*     */ import android.hardware.SensorManager;
/*     */ import android.media.AudioManager;
/*     */ import android.media.AudioManager.OnAudioFocusChangeListener;
/*     */ import android.media.MediaPlayer;
/*     */ import android.media.MediaPlayer.OnCompletionListener;
/*     */ import android.net.Uri;
/*     */ import android.os.Build.VERSION;
/*     */ import android.os.PowerManager;
/*     */ import android.os.PowerManager.WakeLock;
/*     */ import io.rong.common.RLog;
/*     */ 
/*     */ public class AudioPlayManager
/*     */   implements SensorEventListener
/*     */ {
/*     */   private static final String TAG = "AudioPlayManager";
/*     */   private MediaPlayer _mediaPlayer;
/*     */   private IAudioPlayListener _playListener;
/*     */   private Uri _playingUri;
/*     */   private Sensor _sensor;
/*     */   private SensorManager _sensorManager;
/*     */   private AudioManager _audioManager;
/*     */   private PowerManager _powerManager;
/*     */   private PowerManager.WakeLock _wakeLock;
/*     */   private AudioManager.OnAudioFocusChangeListener afChangeListener;
/*     */ 
/*     */   public static AudioPlayManager getInstance()
/*     */   {
/*  35 */     return SingletonHolder.sInstance;
/*     */   }
/*     */ 
/*     */   @TargetApi(11)
/*     */   public void onSensorChanged(SensorEvent event) {
/*  41 */     float range = event.values[0];
/*     */ 
/*  43 */     if ((this._mediaPlayer != null) && (this._mediaPlayer.isPlaying())) {
/*  44 */       if (range == this._sensor.getMaximumRange()) {
/*  45 */         this._audioManager.setMode(0);
/*  46 */         this._audioManager.setSpeakerphoneOn(true);
/*  47 */         setScreenOn();
/*     */       } else {
/*  49 */         this._audioManager.setSpeakerphoneOn(false);
/*  50 */         if (Build.VERSION.SDK_INT >= 11)
/*  51 */           this._audioManager.setMode(3);
/*     */         else {
/*  53 */           this._audioManager.setMode(2);
/*     */         }
/*  55 */         setScreenOff();
/*     */ 
/*  57 */         this._mediaPlayer.setVolume(1.0F, 1.0F);
/*  58 */         int maxVolume = this._audioManager.getStreamMaxVolume(3);
/*  59 */         this._audioManager.setStreamVolume(3, maxVolume, 4);
/*     */       }
/*     */     }
/*  62 */     else if (range == this._sensor.getMaximumRange()) {
/*  63 */       this._audioManager.setMode(0);
/*  64 */       this._audioManager.setSpeakerphoneOn(true);
/*  65 */       setScreenOn();
/*     */     }
/*     */   }
/*     */ 
/*     */   @TargetApi(21)
/*     */   private void setScreenOff() {
/*  72 */     if (this._wakeLock == null) {
/*  73 */       if (Build.VERSION.SDK_INT >= 21) {
/*  74 */         this._wakeLock = this._powerManager.newWakeLock(32, "AudioPlayManager");
/*     */       }
/*     */       else {
/*  77 */         RLog.e("AudioPlayManager", "Does not support on level " + Build.VERSION.SDK_INT);
/*     */       }
/*     */     }
/*  80 */     if (this._wakeLock != null)
/*  81 */       this._wakeLock.acquire();
/*     */   }
/*     */ 
/*     */   private void setScreenOn()
/*     */   {
/*  86 */     if (this._wakeLock != null) {
/*  87 */       this._wakeLock.setReferenceCounted(false);
/*  88 */       this._wakeLock.release();
/*  89 */       this._wakeLock = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onAccuracyChanged(Sensor sensor, int accuracy)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void startPlay(Context context, Uri audioUri, IAudioPlayListener playListener)
/*     */   {
/*  99 */     if ((context == null) || (audioUri == null)) {
/* 100 */       RLog.e("AudioPlayManager", "startPlay context or audioUri is null.");
/* 101 */       return;
/*     */     }
/*     */ 
/* 104 */     resetMediaPlayer();
/* 105 */     if (this.afChangeListener != null) {
/* 106 */       AudioManager am = (AudioManager)context.getSystemService("audio");
/* 107 */       am.abandonAudioFocus(this.afChangeListener);
/* 108 */       this.afChangeListener = null;
/*     */     }
/*     */ 
/* 111 */     this.afChangeListener = new AudioManager.OnAudioFocusChangeListener(context) {
/*     */       public void onAudioFocusChange(int focusChange) {
/* 113 */         RLog.d("AudioPlayManager", "OnAudioFocusChangeListener " + focusChange);
/* 114 */         AudioManager am = (AudioManager)this.val$context.getSystemService("audio");
/* 115 */         if (focusChange == -1) {
/* 116 */           am.abandonAudioFocus(AudioPlayManager.this.afChangeListener);
/* 117 */           AudioPlayManager.access$002(AudioPlayManager.this, null);
/* 118 */           AudioPlayManager.this.resetMediaPlayer();
/*     */         }
/*     */       }
/*     */     };
/*     */     try {
/* 124 */       this._powerManager = ((PowerManager)context.getSystemService("power"));
/* 125 */       this._audioManager = ((AudioManager)context.getSystemService("audio"));
/* 126 */       this._sensorManager = ((SensorManager)context.getSystemService("sensor"));
/* 127 */       this._sensor = this._sensorManager.getDefaultSensor(8);
/* 128 */       this._sensorManager.registerListener(this, this._sensor, 3);
/* 129 */       muteAudioFocus(this._audioManager, true);
/*     */ 
/* 131 */       this._playListener = playListener;
/* 132 */       this._playingUri = audioUri;
/* 133 */       this._mediaPlayer = new MediaPlayer();
/* 134 */       this._mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
/*     */       {
/*     */         public void onCompletion(MediaPlayer mp) {
/* 137 */           AudioPlayManager.this.muteAudioFocus(AudioPlayManager.this._audioManager, false);
/* 138 */           mp.stop();
/* 139 */           mp.release();
/* 140 */           AudioPlayManager.access$402(AudioPlayManager.this, null);
/* 141 */           Uri temp = AudioPlayManager.this._playingUri;
/* 142 */           AudioPlayManager.access$502(AudioPlayManager.this, null);
/* 143 */           if (AudioPlayManager.this._playListener != null)
/* 144 */             AudioPlayManager.this._playListener.onComplete(temp);
/*     */         }
/*     */       });
/* 148 */       this._mediaPlayer.setDataSource(context, audioUri);
/* 149 */       this._mediaPlayer.prepare();
/* 150 */       this._mediaPlayer.start();
/* 151 */       if (this._playListener != null)
/* 152 */         this._playListener.onStart(this._playingUri);
/*     */     } catch (Exception e) {
/* 154 */       e.printStackTrace();
/* 155 */       if (playListener != null)
/* 156 */         playListener.onStop(audioUri);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void stopPlay() {
/* 161 */     resetMediaPlayer();
/*     */   }
/*     */ 
/*     */   private void resetMediaPlayer() {
/* 165 */     if (this._mediaPlayer != null) {
/* 166 */       this._mediaPlayer.stop();
/* 167 */       this._mediaPlayer.release();
/* 168 */       muteAudioFocus(this._audioManager, false);
/*     */ 
/* 170 */       if ((this._playListener != null) && (this._playingUri != null)) {
/* 171 */         this._playListener.onStop(this._playingUri);
/*     */       }
/* 173 */       if (this._sensorManager != null) {
/* 174 */         this._sensorManager.unregisterListener(this);
/*     */       }
/*     */     }
/*     */ 
/* 178 */     this._playListener = null;
/* 179 */     this._playingUri = null;
/* 180 */     this._mediaPlayer = null;
/*     */   }
/*     */ 
/*     */   public Uri getPlayingUri() {
/* 184 */     return this._playingUri;
/*     */   }
/*     */   @TargetApi(8)
/*     */   private void muteAudioFocus(AudioManager audioManager, boolean bMute) {
/* 189 */     if (Build.VERSION.SDK_INT < 8)
/*     */     {
/* 191 */       RLog.d("AudioPlayManager", "muteAudioFocus Android 2.1 and below can not stop music");
/* 192 */       return;
/*     */     }
/* 194 */     if (bMute) {
/* 195 */       audioManager.requestAudioFocus(this.afChangeListener, 3, 2);
/*     */     } else {
/* 197 */       audioManager.abandonAudioFocus(this.afChangeListener);
/* 198 */       this.afChangeListener = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class SingletonHolder
/*     */   {
/*  31 */     static AudioPlayManager sInstance = new AudioPlayManager();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.manager.AudioPlayManager
 * JD-Core Version:    0.6.0
 */
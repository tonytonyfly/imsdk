/*     */ package io.rong.calllib;
/*     */ 
/*     */ import io.agora.rtc.IRtcEngineEventHandler;
/*     */ import io.agora.rtc.IRtcEngineEventHandler.AudioVolumeInfo;
/*     */ import io.agora.rtc.IRtcEngineEventHandler.RtcStats;
/*     */ 
/*     */ public class AgoraEventHandler extends IRtcEngineEventHandler
/*     */ {
/*     */   private IRongCallEngineListener engineListener;
/*     */ 
/*     */   public AgoraEventHandler(IRongCallEngineListener listener)
/*     */   {
/*   9 */     this.engineListener = listener;
/*     */   }
/*     */ 
/*     */   public void onJoinChannelSuccess(String channel, int uid, int elapsed)
/*     */   {
/*  14 */     if (this.engineListener != null)
/*  15 */       this.engineListener.onJoinChannelSuccess(channel, uid, elapsed);
/*     */   }
/*     */ 
/*     */   public void onRejoinChannelSuccess(String channel, int uid, int elapsed)
/*     */   {
/*  21 */     if (this.engineListener != null)
/*  22 */       this.engineListener.onRejoinChannelSuccess(channel, uid, elapsed);
/*     */   }
/*     */ 
/*     */   public void onWarning(int warn)
/*     */   {
/*  28 */     if (this.engineListener != null)
/*  29 */       this.engineListener.onWarning(warn);
/*     */   }
/*     */ 
/*     */   public void onError(int err)
/*     */   {
/*  35 */     if (this.engineListener != null)
/*  36 */       this.engineListener.onError(err);
/*     */   }
/*     */ 
/*     */   public void onApiCallExecuted(String api, int error)
/*     */   {
/*  42 */     if (this.engineListener != null)
/*  43 */       this.engineListener.onApiCallExecuted(api, error);
/*     */   }
/*     */ 
/*     */   public void onCameraReady()
/*     */   {
/*  49 */     if (this.engineListener != null)
/*  50 */       this.engineListener.onCameraReady();
/*     */   }
/*     */ 
/*     */   public void onVideoStopped()
/*     */   {
/*  56 */     if (this.engineListener != null)
/*  57 */       this.engineListener.onVideoStopped();
/*     */   }
/*     */ 
/*     */   public void onAudioQuality(int uid, int quality, short delay, short lost)
/*     */   {
/*  63 */     if (this.engineListener != null)
/*  64 */       this.engineListener.onAudioQuality(uid, quality, delay, lost);
/*     */   }
/*     */ 
/*     */   public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats)
/*     */   {
/*  70 */     if (this.engineListener != null)
/*  71 */       this.engineListener.onLeaveChannel(stats);
/*     */   }
/*     */ 
/*     */   public void onRtcStats(IRtcEngineEventHandler.RtcStats stats)
/*     */   {
/*  77 */     if (this.engineListener != null)
/*  78 */       this.engineListener.onRtcStats(stats);
/*     */   }
/*     */ 
/*     */   public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume)
/*     */   {
/*  84 */     if (this.engineListener != null)
/*  85 */       this.engineListener.onAudioVolumeIndication(speakers, totalVolume);
/*     */   }
/*     */ 
/*     */   public void onNetworkQuality(int quality)
/*     */   {
/*  91 */     if (this.engineListener != null)
/*  92 */       this.engineListener.onNetworkQuality(quality);
/*     */   }
/*     */ 
/*     */   public void onUserJoined(int uid, int elapsed)
/*     */   {
/*  98 */     if (this.engineListener != null)
/*  99 */       this.engineListener.onUserJoined(uid, elapsed);
/*     */   }
/*     */ 
/*     */   public void onUserOffline(int uid, int reason)
/*     */   {
/* 105 */     if (this.engineListener != null)
/* 106 */       this.engineListener.onUserOffline(uid, reason);
/*     */   }
/*     */ 
/*     */   public void onUserMuteAudio(int uid, boolean muted)
/*     */   {
/* 112 */     if (this.engineListener != null)
/* 113 */       this.engineListener.onUserMuteAudio(uid, muted);
/*     */   }
/*     */ 
/*     */   public void onUserMuteVideo(int uid, boolean muted)
/*     */   {
/* 119 */     if (this.engineListener != null)
/* 120 */       this.engineListener.onUserMuteVideo(uid, muted);
/*     */   }
/*     */ 
/*     */   public void onRemoteVideoStat(int uid, int delay, int receivedBitrate, int receivedFrameRate)
/*     */   {
/* 126 */     if (this.engineListener != null)
/* 127 */       this.engineListener.onRemoteVideoStat(uid, delay, receivedBitrate, receivedFrameRate);
/*     */   }
/*     */ 
/*     */   public void onLocalVideoStat(int sentBitrate, int sentFrameRate)
/*     */   {
/* 133 */     if (this.engineListener != null)
/* 134 */       this.engineListener.onLocalVideoStat(sentBitrate, sentFrameRate);
/*     */   }
/*     */ 
/*     */   public void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed)
/*     */   {
/* 140 */     if (this.engineListener != null)
/* 141 */       this.engineListener.onFirstRemoteVideoFrame(uid, width, height, elapsed);
/*     */   }
/*     */ 
/*     */   public void onFirstLocalVideoFrame(int width, int height, int elapsed)
/*     */   {
/* 147 */     if (this.engineListener != null)
/* 148 */       this.engineListener.onFirstLocalVideoFrame(width, height, elapsed);
/*     */   }
/*     */ 
/*     */   public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed)
/*     */   {
/* 154 */     if (this.engineListener != null)
/* 155 */       this.engineListener.onFirstRemoteVideoDecoded(uid, width, height, elapsed);
/*     */   }
/*     */ 
/*     */   public void onConnectionLost()
/*     */   {
/* 161 */     if (this.engineListener != null)
/* 162 */       this.engineListener.onConnectionLost();
/*     */   }
/*     */ 
/*     */   public void onConnectionInterrupted()
/*     */   {
/* 168 */     if (this.engineListener != null)
/* 169 */       this.engineListener.onConnectionInterrupted();
/*     */   }
/*     */ 
/*     */   public void onRefreshRecordingServiceStatus(int status)
/*     */   {
/* 175 */     if (this.engineListener != null)
/* 176 */       this.engineListener.onRefreshRecordingServiceStatus(status);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.AgoraEventHandler
 * JD-Core Version:    0.6.0
 */
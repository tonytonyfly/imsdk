/*     */ package io.rong.calllib;
/*     */ 
/*     */ import android.os.Handler;
/*     */ import android.os.Message;
/*     */ import io.agora.rtc.IRtcEngineEventHandler.AudioVolumeInfo;
/*     */ import io.agora.rtc.IRtcEngineEventHandler.RtcStats;
/*     */ import io.rong.common.RLog;
/*     */ 
/*     */ public class AgoraEngineListener
/*     */   implements IRongCallEngineListener
/*     */ {
/*     */   private static final String TAG = "AgoraEngineListener";
/*     */   private Handler handler;
/*     */   private static final String START_SERVER_RECORDING_API = "rtc.api.start_recording_service";
/*     */   private static final String STOP_SERVER_RECORDING_API = "rtc.api.stop_recording_service";
/*     */   private static final String FETCH_SERVER_RECORDING_STATUS_API = "rtc.api.query_recording_service_status";
/*     */ 
/*     */   public AgoraEngineListener(Handler handler)
/*     */   {
/*  18 */     this.handler = handler;
/*     */   }
/*     */ 
/*     */   public void onJoinChannelSuccess(String channel, int uid, int elapsed)
/*     */   {
/*  23 */     RLog.d("AgoraEngineListener", "onJoinChannelSuccess, uid = " + uid);
/*  24 */     Message message = Message.obtain();
/*  25 */     message.what = 201;
/*  26 */     message.obj = Integer.valueOf(uid);
/*  27 */     this.handler.sendMessage(message);
/*     */   }
/*     */ 
/*     */   public void onRejoinChannelSuccess(String channel, int uid, int elapsed)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onWarning(int warn)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onError(int err)
/*     */   {
/*  42 */     RLog.e("AgoraEngineListener", "onError, err = " + err);
/*     */ 
/*  46 */     if (err != 18)
/*  47 */       this.handler.sendEmptyMessage(403);
/*     */   }
/*     */ 
/*     */   public void onApiCallExecuted(String api, int error)
/*     */   {
/*  53 */     if (api.equals("rtc.api.start_recording_service")) {
/*  54 */       RLog.d("AgoraEngineListener", "onApiCallExecuted, api = " + api + ", error = " + error);
/*  55 */       Message message = Message.obtain();
/*  56 */       message.what = 604;
/*  57 */       message.arg1 = error;
/*  58 */       this.handler.sendMessage(message);
/*  59 */     } else if (api.equals("rtc.api.stop_recording_service")) {
/*  60 */       RLog.d("AgoraEngineListener", "onApiCallExecuted, api = " + api + ", error = " + error);
/*  61 */       Message message = Message.obtain();
/*  62 */       message.what = 605;
/*  63 */       message.arg1 = error;
/*  64 */       this.handler.sendMessage(message);
/*  65 */     } else if (api.equals("rtc.api.query_recording_service_status")) {
/*  66 */       RLog.d("AgoraEngineListener", "onApiCallExecuted, api = " + api + ", error = " + error);
/*  67 */       Message message = Message.obtain();
/*  68 */       message.what = 606;
/*  69 */       message.arg1 = error;
/*  70 */       this.handler.sendMessage(message);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onCameraReady()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onVideoStopped()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onAudioQuality(int uid, int quality, short delay, short lost)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats)
/*     */   {
/*  91 */     RLog.d("AgoraEngineListener", "onLeaveChannel");
/*  92 */     RongCallSession callInfo = new RongCallSession();
/*  93 */     Message message = Message.obtain();
/*  94 */     message.what = 202;
/*  95 */     message.obj = callInfo;
/*  96 */     this.handler.sendMessage(message);
/*     */   }
/*     */ 
/*     */   public void onRtcStats(IRtcEngineEventHandler.RtcStats stats)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onNetworkQuality(int quality)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onUserJoined(int uid, int elapsed)
/*     */   {
/* 116 */     RLog.d("AgoraEngineListener", "onUserJoined, uid = " + uid);
/* 117 */     Message message = Message.obtain();
/* 118 */     message.what = 203;
/* 119 */     message.obj = (uid + "");
/* 120 */     this.handler.sendMessage(message);
/*     */   }
/*     */ 
/*     */   public void onUserOffline(int uid, int reason)
/*     */   {
/* 125 */     RLog.d("AgoraEngineListener", "onUserOffline, uid = " + uid);
/* 126 */     Message message = Message.obtain();
/* 127 */     message.what = 204;
/* 128 */     message.obj = (uid + "");
/* 129 */     this.handler.sendMessage(message);
/*     */   }
/*     */ 
/*     */   public void onUserMuteAudio(int uid, boolean muted)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onUserMuteVideo(int uid, boolean muted)
/*     */   {
/* 139 */     RLog.d("AgoraEngineListener", "onUserMuteVideo, uid = " + uid);
/* 140 */     Message message = Message.obtain();
/* 141 */     message.what = 207;
/* 142 */     message.obj = (uid + "");
/* 143 */     message.arg1 = (muted ? 1 : 0);
/* 144 */     this.handler.sendMessage(message);
/*     */   }
/*     */ 
/*     */   public void onRemoteVideoStat(int uid, int delay, int receivedBitrate, int receivedFrameRate)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onLocalVideoStat(int sentBitrate, int sentFrameRate)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onFirstLocalVideoFrame(int width, int height, int elapsed)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onConnectionLost()
/*     */   {
/* 174 */     RLog.d("AgoraEngineListener", "onConnectionLost");
/* 175 */     this.handler.sendEmptyMessage(403);
/*     */   }
/*     */ 
/*     */   public void onConnectionInterrupted()
/*     */   {
/* 180 */     RLog.d("AgoraEngineListener", "onConnectionInterrupted");
/*     */   }
/*     */ 
/*     */   public void onMediaEngineEvent(int code)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onVendorMessage(int uid, byte[] data)
/*     */   {
/* 190 */     RLog.d("AgoraEngineListener", "onVendorMessage : msg = " + new String(data));
/*     */   }
/*     */ 
/*     */   public void onRefreshRecordingServiceStatus(int status)
/*     */   {
/* 195 */     RLog.d("AgoraEngineListener", "onRefreshRecordingServiceStatus, status = " + status);
/* 196 */     Message message = Message.obtain();
/* 197 */     message.what = 606;
/* 198 */     message.arg1 = 0;
/* 199 */     message.arg2 = status;
/* 200 */     this.handler.sendMessage(message);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.AgoraEngineListener
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.calllib;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.view.SurfaceView;
/*     */ import io.agora.rtc.RtcEngine;
/*     */ import io.agora.rtc.video.VideoCanvas;
/*     */ 
/*     */ public class AgoraEngine
/*     */   implements IRongCallEngine
/*     */ {
/*     */   private RtcEngine rtcEngine;
/*     */ 
/*     */   public void init(Context context, String vendorKey, IRongCallEngineListener listener)
/*     */   {
/*  19 */     AgoraEventHandler agoraEventHandler = new AgoraEventHandler(listener);
/*  20 */     this.rtcEngine = RtcEngine.create(context, vendorKey, agoraEventHandler);
/*     */   }
/*     */ 
/*     */   public void setupLocalVideo(SurfaceView localView)
/*     */   {
/*  25 */     this.rtcEngine.setupLocalVideo(new VideoCanvas(localView));
/*     */   }
/*     */ 
/*     */   public void setupRemoteVideo(SurfaceView remoteVideo, int uid)
/*     */   {
/*  30 */     this.rtcEngine.setupRemoteVideo(new VideoCanvas(remoteVideo, 1, uid));
/*     */   }
/*     */ 
/*     */   public int enableVideo()
/*     */   {
/*  35 */     return this.rtcEngine.enableVideo();
/*     */   }
/*     */ 
/*     */   public int disableVideo()
/*     */   {
/*  40 */     return this.rtcEngine.disableVideo();
/*     */   }
/*     */ 
/*     */   public int startPreview()
/*     */   {
/*  45 */     return this.rtcEngine.startPreview();
/*     */   }
/*     */ 
/*     */   public int stopPreview()
/*     */   {
/*  50 */     return this.rtcEngine.stopPreview();
/*     */   }
/*     */ 
/*     */   public int joinChannel(String key, String channelName, String optionalInfo, int optionalUid)
/*     */   {
/*  55 */     this.rtcEngine.joinChannel(key, channelName, optionalInfo, optionalUid);
/*  56 */     return 0;
/*     */   }
/*     */ 
/*     */   public int leaveChannel()
/*     */   {
/*  61 */     return this.rtcEngine.leaveChannel();
/*     */   }
/*     */ 
/*     */   public int renewChannelDynamicKey(String key)
/*     */   {
/*  66 */     return this.rtcEngine.renewDynamicKey(key);
/*     */   }
/*     */ 
/*     */   public int setChannelProfile(int profile)
/*     */   {
/*  71 */     return this.rtcEngine.setChannelProfile(profile);
/*     */   }
/*     */ 
/*     */   public int startEchoTest()
/*     */   {
/*  76 */     return this.rtcEngine.startEchoTest();
/*     */   }
/*     */ 
/*     */   public int stopEchoTest()
/*     */   {
/*  81 */     return this.rtcEngine.stopEchoTest();
/*     */   }
/*     */ 
/*     */   public int enableNetworkTest()
/*     */   {
/*  86 */     return this.rtcEngine.enableNetworkTest();
/*     */   }
/*     */ 
/*     */   public int disableNetworkTest()
/*     */   {
/*  91 */     return this.rtcEngine.disableNetworkTest();
/*     */   }
/*     */ 
/*     */   public int muteLocalAudioStream(boolean muted)
/*     */   {
/*  96 */     return this.rtcEngine.muteLocalAudioStream(muted);
/*     */   }
/*     */ 
/*     */   public int muteAllRemoteAudioStreams(boolean muted)
/*     */   {
/* 101 */     return this.rtcEngine.muteAllRemoteAudioStreams(muted);
/*     */   }
/*     */ 
/*     */   public int muteRemoteAudioStream(int uid, boolean muted)
/*     */   {
/* 106 */     return this.rtcEngine.muteRemoteAudioStream(uid, muted);
/*     */   }
/*     */ 
/*     */   public int setEnableSpeakerphone(boolean enabled)
/*     */   {
/* 111 */     return this.rtcEngine.setEnableSpeakerphone(enabled);
/*     */   }
/*     */ 
/*     */   public int startAudioRecording(String filePath)
/*     */   {
/* 116 */     return this.rtcEngine.startAudioRecording(filePath);
/*     */   }
/*     */ 
/*     */   public int stopAudioRecording()
/*     */   {
/* 121 */     return this.rtcEngine.stopAudioRecording();
/*     */   }
/*     */ 
/*     */   public String getCallId()
/*     */   {
/* 126 */     return this.rtcEngine.getCallId();
/*     */   }
/*     */ 
/*     */   public int rate(String callId, int rating, String description)
/*     */   {
/* 131 */     return this.rtcEngine.rate(callId, rating, description);
/*     */   }
/*     */ 
/*     */   public int complain(String callId, String description)
/*     */   {
/* 136 */     return this.rtcEngine.complain(callId, description);
/*     */   }
/*     */ 
/*     */   public void monitorHeadsetEvent(boolean monitor)
/*     */   {
/* 141 */     this.rtcEngine.monitorHeadsetEvent(monitor);
/*     */   }
/*     */ 
/*     */   public void monitorBluetoothHeadsetEvent(boolean monitor)
/*     */   {
/* 146 */     this.rtcEngine.monitorBluetoothHeadsetEvent(monitor);
/*     */   }
/*     */ 
/*     */   public void monitorConnectionEvent(boolean monitor)
/*     */   {
/* 151 */     this.rtcEngine.monitorConnectionEvent(monitor);
/*     */   }
/*     */ 
/*     */   public boolean isSpeakerphoneEnabled()
/*     */   {
/* 156 */     return this.rtcEngine.isSpeakerphoneEnabled();
/*     */   }
/*     */ 
/*     */   public int setSpeakerphoneVolume(int volume)
/*     */   {
/* 161 */     return this.rtcEngine.setSpeakerphoneVolume(volume);
/*     */   }
/*     */ 
/*     */   public int enableAudioVolumeIndication(int interval, int smooth)
/*     */   {
/* 166 */     return this.rtcEngine.enableAudioVolumeIndication(interval, smooth);
/*     */   }
/*     */ 
/*     */   public int setVideoProfile(int profile)
/*     */   {
/* 171 */     return this.rtcEngine.setVideoProfile(profile);
/*     */   }
/*     */ 
/*     */   public int setupLocalVideo(VideoCanvas local)
/*     */   {
/* 176 */     return this.rtcEngine.setupLocalVideo(local);
/*     */   }
/*     */ 
/*     */   public int setupRemoteVideo(VideoCanvas remote)
/*     */   {
/* 181 */     return this.rtcEngine.setupRemoteVideo(remote);
/*     */   }
/*     */ 
/*     */   public int setLocalRenderMode(int mode)
/*     */   {
/* 186 */     return this.rtcEngine.setLocalRenderMode(mode);
/*     */   }
/*     */ 
/*     */   public int setRemoteRenderMode(int uid, int mode)
/*     */   {
/* 191 */     return this.rtcEngine.setRemoteRenderMode(uid, mode);
/*     */   }
/*     */ 
/*     */   public void switchView(int uid1, int uid2)
/*     */   {
/* 196 */     this.rtcEngine.switchView(uid1, uid2);
/*     */   }
/*     */ 
/*     */   public int switchCamera()
/*     */   {
/* 201 */     return this.rtcEngine.switchCamera();
/*     */   }
/*     */ 
/*     */   public int muteLocalVideoStream(boolean muted)
/*     */   {
/* 206 */     return this.rtcEngine.muteLocalVideoStream(muted);
/*     */   }
/*     */ 
/*     */   public int muteAllRemoteVideoStreams(boolean muted)
/*     */   {
/* 211 */     return this.rtcEngine.muteAllRemoteVideoStreams(muted);
/*     */   }
/*     */ 
/*     */   public int muteRemoteVideoStream(int uid, boolean muted)
/*     */   {
/* 216 */     return this.rtcEngine.muteRemoteVideoStream(uid, muted);
/*     */   }
/*     */ 
/*     */   public int setLogFile(String filePath)
/*     */   {
/* 221 */     return this.rtcEngine.setLogFile(filePath);
/*     */   }
/*     */ 
/*     */   public int setLogFilter(int filter)
/*     */   {
/* 226 */     return this.rtcEngine.setLogFilter(filter);
/*     */   }
/*     */ 
/*     */   public int startServerRecording(String key)
/*     */   {
/* 231 */     return this.rtcEngine.startRecordingService(key);
/*     */   }
/*     */ 
/*     */   public int stopServerRecording(String key)
/*     */   {
/* 236 */     return this.rtcEngine.stopRecordingService(key);
/*     */   }
/*     */ 
/*     */   public int getServerRecordingStatus()
/*     */   {
/* 241 */     return this.rtcEngine.refreshRecordingServiceStatus();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.AgoraEngine
 * JD-Core Version:    0.6.0
 */
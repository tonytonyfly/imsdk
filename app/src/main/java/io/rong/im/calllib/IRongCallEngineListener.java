package io.rong.calllib;

import io.agora.rtc.IRtcEngineEventHandler.AudioVolumeInfo;
import io.agora.rtc.IRtcEngineEventHandler.RtcStats;

public abstract interface IRongCallEngineListener
{
  public abstract void onJoinChannelSuccess(String paramString, int paramInt1, int paramInt2);

  public abstract void onRejoinChannelSuccess(String paramString, int paramInt1, int paramInt2);

  public abstract void onWarning(int paramInt);

  public abstract void onError(int paramInt);

  public abstract void onApiCallExecuted(String paramString, int paramInt);

  public abstract void onCameraReady();

  public abstract void onVideoStopped();

  public abstract void onAudioQuality(int paramInt1, int paramInt2, short paramShort1, short paramShort2);

  public abstract void onLeaveChannel(IRtcEngineEventHandler.RtcStats paramRtcStats);

  public abstract void onRtcStats(IRtcEngineEventHandler.RtcStats paramRtcStats);

  public abstract void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] paramArrayOfAudioVolumeInfo, int paramInt);

  public abstract void onNetworkQuality(int paramInt);

  public abstract void onUserJoined(int paramInt1, int paramInt2);

  public abstract void onUserOffline(int paramInt1, int paramInt2);

  public abstract void onUserMuteAudio(int paramInt, boolean paramBoolean);

  public abstract void onUserMuteVideo(int paramInt, boolean paramBoolean);

  public abstract void onRemoteVideoStat(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract void onLocalVideoStat(int paramInt1, int paramInt2);

  public abstract void onFirstRemoteVideoFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract void onFirstLocalVideoFrame(int paramInt1, int paramInt2, int paramInt3);

  public abstract void onFirstRemoteVideoDecoded(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract void onConnectionLost();

  public abstract void onConnectionInterrupted();

  public abstract void onMediaEngineEvent(int paramInt);

  public abstract void onVendorMessage(int paramInt, byte[] paramArrayOfByte);

  public abstract void onRefreshRecordingServiceStatus(int paramInt);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.IRongCallEngineListener
 * JD-Core Version:    0.6.0
 */
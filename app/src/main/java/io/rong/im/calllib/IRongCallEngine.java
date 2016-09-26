package io.rong.calllib;

import android.content.Context;
import android.view.SurfaceView;
import io.agora.rtc.video.VideoCanvas;

public abstract interface IRongCallEngine
{
  public abstract void init(Context paramContext, String paramString, IRongCallEngineListener paramIRongCallEngineListener);

  public abstract void setupLocalVideo(SurfaceView paramSurfaceView);

  public abstract void setupRemoteVideo(SurfaceView paramSurfaceView, int paramInt);

  public abstract int enableVideo();

  public abstract int disableVideo();

  public abstract int startPreview();

  public abstract int stopPreview();

  public abstract int joinChannel(String paramString1, String paramString2, String paramString3, int paramInt);

  public abstract int leaveChannel();

  public abstract int renewChannelDynamicKey(String paramString);

  public abstract int setChannelProfile(int paramInt);

  public abstract int startEchoTest();

  public abstract int stopEchoTest();

  public abstract int enableNetworkTest();

  public abstract int disableNetworkTest();

  public abstract int muteLocalAudioStream(boolean paramBoolean);

  public abstract int muteAllRemoteAudioStreams(boolean paramBoolean);

  public abstract int muteRemoteAudioStream(int paramInt, boolean paramBoolean);

  public abstract int setEnableSpeakerphone(boolean paramBoolean);

  public abstract int startAudioRecording(String paramString);

  public abstract int stopAudioRecording();

  public abstract String getCallId();

  public abstract int rate(String paramString1, int paramInt, String paramString2);

  public abstract int complain(String paramString1, String paramString2);

  public abstract void monitorHeadsetEvent(boolean paramBoolean);

  public abstract void monitorBluetoothHeadsetEvent(boolean paramBoolean);

  public abstract void monitorConnectionEvent(boolean paramBoolean);

  public abstract boolean isSpeakerphoneEnabled();

  public abstract int setSpeakerphoneVolume(int paramInt);

  public abstract int enableAudioVolumeIndication(int paramInt1, int paramInt2);

  public abstract int setVideoProfile(int paramInt);

  public abstract int setupLocalVideo(VideoCanvas paramVideoCanvas);

  public abstract int setupRemoteVideo(VideoCanvas paramVideoCanvas);

  public abstract int setLocalRenderMode(int paramInt);

  public abstract int setRemoteRenderMode(int paramInt1, int paramInt2);

  public abstract void switchView(int paramInt1, int paramInt2);

  public abstract int switchCamera();

  public abstract int muteLocalVideoStream(boolean paramBoolean);

  public abstract int muteAllRemoteVideoStreams(boolean paramBoolean);

  public abstract int muteRemoteVideoStream(int paramInt, boolean paramBoolean);

  public abstract int setLogFile(String paramString);

  public abstract int setLogFilter(int paramInt);

  public abstract int startServerRecording(String paramString);

  public abstract int stopServerRecording(String paramString);

  public abstract int getServerRecordingStatus();
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.IRongCallEngine
 * JD-Core Version:    0.6.0
 */
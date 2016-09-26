package io.rong.calllib;

import android.view.SurfaceView;

public abstract interface IRongCallListener
{
  public abstract void onCallOutgoing(RongCallSession paramRongCallSession, SurfaceView paramSurfaceView);

  public abstract void onCallConnected(RongCallSession paramRongCallSession, SurfaceView paramSurfaceView);

  public abstract void onCallDisconnected(RongCallSession paramRongCallSession, RongCallCommon.CallDisconnectedReason paramCallDisconnectedReason);

  public abstract void onRemoteUserRinging(String paramString);

  public abstract void onRemoteUserJoined(String paramString, RongCallCommon.CallMediaType paramCallMediaType, SurfaceView paramSurfaceView);

  public abstract void onRemoteUserInvited(String paramString, RongCallCommon.CallMediaType paramCallMediaType);

  public abstract void onRemoteUserLeft(String paramString, RongCallCommon.CallDisconnectedReason paramCallDisconnectedReason);

  public abstract void onMediaTypeChanged(String paramString, RongCallCommon.CallMediaType paramCallMediaType, SurfaceView paramSurfaceView);

  public abstract void onError(RongCallCommon.CallErrorCode paramCallErrorCode);

  public abstract void onRemoteCameraDisabled(String paramString, boolean paramBoolean);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.IRongCallListener
 * JD-Core Version:    0.6.0
 */
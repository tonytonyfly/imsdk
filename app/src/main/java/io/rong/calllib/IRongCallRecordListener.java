package io.rong.calllib;

public abstract interface IRongCallRecordListener
{
  public abstract void onServerStartRecording(RongCallCommon.ServerRecordingErrorCode paramServerRecordingErrorCode);

  public abstract void onServerStopRecording(RongCallCommon.ServerRecordingErrorCode paramServerRecordingErrorCode);

  public abstract void onServerFetchRecordingStatus(boolean paramBoolean, RongCallCommon.ServerRecordingErrorCode paramServerRecordingErrorCode);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.IRongCallRecordListener
 * JD-Core Version:    0.6.0
 */
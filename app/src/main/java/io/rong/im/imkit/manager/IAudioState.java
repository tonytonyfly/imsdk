package io.rong.imkit.manager;

public abstract class IAudioState
{
  void enter()
  {
  }

  abstract void handleMessage(AudioStateMessage paramAudioStateMessage);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.manager.IAudioState
 * JD-Core Version:    0.6.0
 */
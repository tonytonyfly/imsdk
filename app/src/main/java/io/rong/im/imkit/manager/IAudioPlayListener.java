package io.rong.imkit.manager;

import android.net.Uri;

public abstract interface IAudioPlayListener
{
  public abstract void onStart(Uri paramUri);

  public abstract void onStop(Uri paramUri);

  public abstract void onComplete(Uri paramUri);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.manager.IAudioPlayListener
 * JD-Core Version:    0.6.0
 */
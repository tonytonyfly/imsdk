package io.rong.imkit.widget;

import android.net.Uri;

public abstract interface IImageLoadingListener
{
  public abstract void onLoadingComplete(Uri paramUri);

  public abstract void onLoadingFail();
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.IImageLoadingListener
 * JD-Core Version:    0.6.0
 */
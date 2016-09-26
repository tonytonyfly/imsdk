package io.rong.imageloader.core.listener;

import android.graphics.Bitmap;
import android.view.View;
import io.rong.imageloader.core.assist.FailReason;

public abstract interface ImageLoadingListener
{
  public abstract void onLoadingStarted(String paramString, View paramView);

  public abstract void onLoadingFailed(String paramString, View paramView, FailReason paramFailReason);

  public abstract void onLoadingComplete(String paramString, View paramView, Bitmap paramBitmap);

  public abstract void onLoadingCancelled(String paramString, View paramView);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.listener.ImageLoadingListener
 * JD-Core Version:    0.6.0
 */
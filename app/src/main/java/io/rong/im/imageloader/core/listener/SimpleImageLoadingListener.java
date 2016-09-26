package io.rong.imageloader.core.listener;

import android.graphics.Bitmap;
import android.view.View;
import io.rong.imageloader.core.assist.FailReason;

public class SimpleImageLoadingListener
  implements ImageLoadingListener
{
  public void onLoadingStarted(String imageUri, View view)
  {
  }

  public void onLoadingFailed(String imageUri, View view, FailReason failReason)
  {
  }

  public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
  {
  }

  public void onLoadingCancelled(String imageUri, View view)
  {
  }
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.listener.SimpleImageLoadingListener
 * JD-Core Version:    0.6.0
 */
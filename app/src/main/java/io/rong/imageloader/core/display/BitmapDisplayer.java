package io.rong.imageloader.core.display;

import android.graphics.Bitmap;
import io.rong.imageloader.core.assist.LoadedFrom;
import io.rong.imageloader.core.imageaware.ImageAware;

public abstract interface BitmapDisplayer
{
  public abstract void display(Bitmap paramBitmap, ImageAware paramImageAware, LoadedFrom paramLoadedFrom);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.display.BitmapDisplayer
 * JD-Core Version:    0.6.0
 */
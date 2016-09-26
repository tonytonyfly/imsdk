package io.rong.imageloader.core.imageaware;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import io.rong.imageloader.core.assist.ViewScaleType;

public abstract interface ImageAware
{
  public abstract int getWidth();

  public abstract int getHeight();

  public abstract ViewScaleType getScaleType();

  public abstract View getWrappedView();

  public abstract boolean isCollected();

  public abstract int getId();

  public abstract boolean setImageDrawable(Drawable paramDrawable);

  public abstract boolean setImageBitmap(Bitmap paramBitmap);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.imageaware.ImageAware
 * JD-Core Version:    0.6.0
 */
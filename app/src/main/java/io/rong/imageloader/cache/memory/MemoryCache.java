package io.rong.imageloader.cache.memory;

import android.graphics.Bitmap;
import java.util.Collection;

public abstract interface MemoryCache
{
  public abstract boolean put(String paramString, Bitmap paramBitmap);

  public abstract Bitmap get(String paramString);

  public abstract Bitmap remove(String paramString);

  public abstract Collection<String> keys();

  public abstract void clear();
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.memory.MemoryCache
 * JD-Core Version:    0.6.0
 */
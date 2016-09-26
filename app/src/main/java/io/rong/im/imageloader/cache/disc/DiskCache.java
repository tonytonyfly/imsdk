package io.rong.imageloader.cache.disc;

import android.graphics.Bitmap;
import io.rong.imageloader.utils.IoUtils.CopyListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract interface DiskCache
{
  public abstract File getDirectory();

  public abstract File get(String paramString);

  public abstract boolean save(String paramString, InputStream paramInputStream, IoUtils.CopyListener paramCopyListener)
    throws IOException;

  public abstract boolean save(String paramString, Bitmap paramBitmap)
    throws IOException;

  public abstract boolean remove(String paramString);

  public abstract void close();

  public abstract void clear();
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.disc.DiskCache
 * JD-Core Version:    0.6.0
 */
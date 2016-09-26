package io.rong.imageloader.core.decode;

import android.graphics.Bitmap;
import java.io.IOException;

public abstract interface ImageDecoder
{
  public abstract Bitmap decode(ImageDecodingInfo paramImageDecodingInfo)
    throws IOException;
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.decode.ImageDecoder
 * JD-Core Version:    0.6.0
 */
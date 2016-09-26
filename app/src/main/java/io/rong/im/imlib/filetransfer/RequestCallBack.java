package io.rong.imlib.filetransfer;

public abstract interface RequestCallBack
{
  public abstract void onError(int paramInt);

  public abstract void onComplete(String paramString);

  public abstract void onProgress(int paramInt);

  public abstract void onCanceled(Object paramObject);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.filetransfer.RequestCallBack
 * JD-Core Version:    0.6.0
 */
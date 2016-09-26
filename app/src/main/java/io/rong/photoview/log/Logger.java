package io.rong.photoview.log;

public abstract interface Logger
{
  public abstract int v(String paramString1, String paramString2);

  public abstract int v(String paramString1, String paramString2, Throwable paramThrowable);

  public abstract int d(String paramString1, String paramString2);

  public abstract int d(String paramString1, String paramString2, Throwable paramThrowable);

  public abstract int i(String paramString1, String paramString2);

  public abstract int i(String paramString1, String paramString2, Throwable paramThrowable);

  public abstract int w(String paramString1, String paramString2);

  public abstract int w(String paramString1, String paramString2, Throwable paramThrowable);

  public abstract int e(String paramString1, String paramString2);

  public abstract int e(String paramString1, String paramString2, Throwable paramThrowable);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.photoview.log.Logger
 * JD-Core Version:    0.6.0
 */
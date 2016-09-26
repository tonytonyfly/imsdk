package io.rong.imlib.navigation;

public abstract interface NavigationObserver
{
  public abstract void onSuccess(String paramString);

  public abstract void onError(String paramString, int paramInt);

  public abstract void onReconnect(String paramString, NavigationCallback paramNavigationCallback);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.navigation.NavigationObserver
 * JD-Core Version:    0.6.0
 */
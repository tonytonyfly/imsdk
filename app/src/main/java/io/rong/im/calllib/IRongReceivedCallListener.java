package io.rong.calllib;

public abstract interface IRongReceivedCallListener
{
  public abstract void onReceivedCall(RongCallSession paramRongCallSession);

  public abstract void onCheckPermission(RongCallSession paramRongCallSession);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.IRongReceivedCallListener
 * JD-Core Version:    0.6.0
 */
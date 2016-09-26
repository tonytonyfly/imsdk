package io.rong.imlib.stateMachine;

import android.os.Message;

public abstract interface IState
{
  public static final boolean HANDLED = true;
  public static final boolean NOT_HANDLED = false;

  public abstract void enter();

  public abstract void exit();

  public abstract boolean processMessage(Message paramMessage);

  public abstract String getName();
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.stateMachine.IState
 * JD-Core Version:    0.6.0
 */
package io.rong.imlib;

import io.rong.imlib.model.CSGroupItem;
import io.rong.imlib.model.CustomServiceMode;
import java.util.List;

public abstract interface ICustomServiceListener
{
  public abstract void onSuccess(CustomServiceConfig paramCustomServiceConfig);

  public abstract void onError(int paramInt, String paramString);

  public abstract void onModeChanged(CustomServiceMode paramCustomServiceMode);

  public abstract void onQuit(String paramString);

  public abstract void onPullEvaluation(String paramString);

  public abstract void onSelectGroup(List<CSGroupItem> paramList);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.ICustomServiceListener
 * JD-Core Version:    0.6.0
 */
package io.rong.imkit;

import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.PublicServiceMenuItem;

public abstract interface IPublicServiceMenuClickListener
{
  public abstract boolean onClick(Conversation.ConversationType paramConversationType, String paramString, PublicServiceMenuItem paramPublicServiceMenuItem);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.IPublicServiceMenuClickListener
 * JD-Core Version:    0.6.0
 */
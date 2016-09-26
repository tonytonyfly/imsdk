package io.rong.imkit.mention;

import io.rong.imlib.model.Conversation.ConversationType;

public abstract interface IMentionedInputListener
{
  public abstract boolean onMentionedInput(Conversation.ConversationType paramConversationType, String paramString);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.mention.IMentionedInputListener
 * JD-Core Version:    0.6.0
 */
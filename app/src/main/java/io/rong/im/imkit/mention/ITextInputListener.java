package io.rong.imkit.mention;

import android.widget.EditText;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.MentionedInfo;

public abstract interface ITextInputListener
{
  public abstract void onTextEdit(Conversation.ConversationType paramConversationType, String paramString1, int paramInt1, int paramInt2, String paramString2);

  public abstract MentionedInfo onSendButtonClick();

  public abstract void onDeleteClick(Conversation.ConversationType paramConversationType, String paramString, EditText paramEditText, int paramInt);
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.mention.ITextInputListener
 * JD-Core Version:    0.6.0
 */
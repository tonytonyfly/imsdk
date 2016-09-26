package io.rong.imkit.widget.provider;

import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.model.UIConversation;

@ConversationProviderTag(conversationType="customer_service", portraitPosition=1)
public class CustomerServiceConversationProvider extends PrivateConversationProvider
  implements IContainerItemProvider.ConversationProvider<UIConversation>
{
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.CustomerServiceConversationProvider
 * JD-Core Version:    0.6.0
 */
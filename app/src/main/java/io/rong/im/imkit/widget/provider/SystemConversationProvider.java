/*    */ package io.rong.imkit.widget.provider;
/*    */ 
/*    */ import io.rong.imkit.model.ConversationProviderTag;
/*    */ import io.rong.imkit.model.UIConversation;
/*    */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*    */ import io.rong.imlib.model.UserInfo;
/*    */ 
/*    */ @ConversationProviderTag(conversationType="system", portraitPosition=1)
/*    */ public class SystemConversationProvider extends PrivateConversationProvider
/*    */   implements IContainerItemProvider.ConversationProvider<UIConversation>
/*    */ {
/*    */   public String getTitle(String id)
/*    */   {
/*    */     String name;
/*    */     String name;
/* 13 */     if (RongUserInfoManager.getInstance().getUserInfo(id) == null)
/* 14 */       name = id;
/*    */     else {
/* 16 */       name = RongUserInfoManager.getInstance().getUserInfo(id).getName();
/*    */     }
/* 18 */     return name;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.SystemConversationProvider
 * JD-Core Version:    0.6.0
 */
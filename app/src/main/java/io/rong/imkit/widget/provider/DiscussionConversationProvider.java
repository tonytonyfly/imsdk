/*    */ package io.rong.imkit.widget.provider;
/*    */ 
/*    */ import android.content.res.Resources;
/*    */ import android.net.Uri;
/*    */ import io.rong.imkit.R.string;
/*    */ import io.rong.imkit.RongContext;
/*    */ import io.rong.imkit.model.ConversationProviderTag;
/*    */ import io.rong.imkit.model.UIConversation;
/*    */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*    */ import io.rong.imlib.model.Discussion;
/*    */ 
/*    */ @ConversationProviderTag(conversationType="discussion", portraitPosition=1)
/*    */ public class DiscussionConversationProvider extends PrivateConversationProvider
/*    */   implements IContainerItemProvider.ConversationProvider<UIConversation>
/*    */ {
/*    */   public String getTitle(String id)
/*    */   {
/*    */     String name;
/*    */     String name;
/* 15 */     if (RongUserInfoManager.getInstance().getDiscussionInfo(id) == null)
/* 16 */       name = RongContext.getInstance().getResources().getString(R.string.rc_conversation_list_default_discussion_name);
/*    */     else {
/* 18 */       name = RongUserInfoManager.getInstance().getDiscussionInfo(id).getName();
/*    */     }
/* 20 */     return name;
/*    */   }
/*    */ 
/*    */   public Uri getPortraitUri(String id)
/*    */   {
/* 25 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.DiscussionConversationProvider
 * JD-Core Version:    0.6.0
 */
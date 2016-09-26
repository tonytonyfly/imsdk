/*    */ package io.rong.imkit.widget.provider;
/*    */ 
/*    */ import android.net.Uri;
/*    */ import io.rong.imkit.model.ConversationProviderTag;
/*    */ import io.rong.imkit.model.UIConversation;
/*    */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*    */ import io.rong.imlib.model.Group;
/*    */ 
/*    */ @ConversationProviderTag(conversationType="group", portraitPosition=1)
/*    */ public class GroupConversationProvider extends PrivateConversationProvider
/*    */   implements IContainerItemProvider.ConversationProvider<UIConversation>
/*    */ {
/*    */   public String getTitle(String groupId)
/*    */   {
/*    */     String name;
/*    */     String name;
/* 14 */     if (RongUserInfoManager.getInstance().getGroupInfo(groupId) == null)
/* 15 */       name = "";
/*    */     else {
/* 17 */       name = RongUserInfoManager.getInstance().getGroupInfo(groupId).getName();
/*    */     }
/* 19 */     return name;
/*    */   }
/*    */ 
/*    */   public Uri getPortraitUri(String id)
/*    */   {
/*    */     Uri uri;
/*    */     Uri uri;
/* 25 */     if (RongUserInfoManager.getInstance().getGroupInfo(id) == null)
/* 26 */       uri = null;
/*    */     else {
/* 28 */       uri = RongUserInfoManager.getInstance().getGroupInfo(id).getPortraitUri();
/*    */     }
/* 30 */     return uri;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.GroupConversationProvider
 * JD-Core Version:    0.6.0
 */
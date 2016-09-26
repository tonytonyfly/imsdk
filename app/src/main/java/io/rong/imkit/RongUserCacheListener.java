/*    */ package io.rong.imkit;
/*    */ 
/*    */ import io.rong.eventbus.EventBus;
/*    */ import io.rong.imkit.model.GroupUserInfo;
/*    */ import io.rong.imkit.userInfoCache.IRongCacheListener;
/*    */ import io.rong.imlib.model.Discussion;
/*    */ import io.rong.imlib.model.Group;
/*    */ import io.rong.imlib.model.PublicServiceProfile;
/*    */ import io.rong.imlib.model.UserInfo;
/*    */ 
/*    */ public class RongUserCacheListener
/*    */   implements IRongCacheListener
/*    */ {
/*    */   public void onUserInfoUpdated(UserInfo info)
/*    */   {
/* 13 */     if (info != null)
/* 14 */       RongContext.getInstance().getEventBus().post(info);
/*    */   }
/*    */ 
/*    */   public void onGroupUserInfoUpdated(GroupUserInfo info)
/*    */   {
/* 20 */     if (info != null)
/* 21 */       RongContext.getInstance().getEventBus().post(info);
/*    */   }
/*    */ 
/*    */   public void onGroupUpdated(Group group)
/*    */   {
/* 27 */     if (group != null)
/* 28 */       RongContext.getInstance().getEventBus().post(group);
/*    */   }
/*    */ 
/*    */   public void onDiscussionUpdated(Discussion discussion)
/*    */   {
/* 34 */     if (discussion != null)
/* 35 */       RongContext.getInstance().getEventBus().post(discussion);
/*    */   }
/*    */ 
/*    */   public void onPublicServiceProfileUpdated(PublicServiceProfile profile)
/*    */   {
/* 41 */     if (profile != null)
/* 42 */       RongContext.getInstance().getEventBus().post(profile);
/*    */   }
/*    */ 
/*    */   public UserInfo getUserInfo(String id)
/*    */   {
/* 48 */     if (RongContext.getInstance().getUserInfoProvider() != null) {
/* 49 */       return RongContext.getInstance().getUserInfoProvider().getUserInfo(id);
/*    */     }
/* 51 */     return null;
/*    */   }
/*    */ 
/*    */   public GroupUserInfo getGroupUserInfo(String group, String id)
/*    */   {
/* 56 */     if (RongContext.getInstance().getGroupUserInfoProvider() != null) {
/* 57 */       return RongContext.getInstance().getGroupUserInfoProvider().getGroupUserInfo(group, id);
/*    */     }
/* 59 */     return null;
/*    */   }
/*    */ 
/*    */   public Group getGroupInfo(String id)
/*    */   {
/* 64 */     if (RongContext.getInstance().getGroupInfoProvider() != null) {
/* 65 */       return RongContext.getInstance().getGroupInfoProvider().getGroupInfo(id);
/*    */     }
/* 67 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.RongUserCacheListener
 * JD-Core Version:    0.6.0
 */
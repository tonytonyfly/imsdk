/*    */ package io.rong.imkit.model;
/*    */ 
/*    */ public class GroupUserInfo
/*    */ {
/*    */   private String nickname;
/*    */   private String userId;
/*    */   private String groupId;
/*    */ 
/*    */   public GroupUserInfo(String groupId, String userId, String nickname)
/*    */   {
/* 12 */     this.groupId = groupId;
/* 13 */     this.nickname = nickname;
/* 14 */     this.userId = userId;
/*    */   }
/*    */ 
/*    */   public String getGroupId() {
/* 18 */     return this.groupId;
/*    */   }
/*    */ 
/*    */   public void setGroupId(String groupId) {
/* 22 */     this.groupId = groupId;
/*    */   }
/*    */ 
/*    */   public String getNickname() {
/* 26 */     return this.nickname;
/*    */   }
/*    */ 
/*    */   public String getUserId() {
/* 30 */     return this.userId;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.model.GroupUserInfo
 * JD-Core Version:    0.6.0
 */
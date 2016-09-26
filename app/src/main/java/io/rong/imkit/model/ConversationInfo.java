/*    */ package io.rong.imkit.model;
/*    */ 
/*    */ import io.rong.imlib.model.Conversation.ConversationType;
/*    */ 
/*    */ public class ConversationInfo
/*    */ {
/*    */   Conversation.ConversationType conversationType;
/*    */   String targetId;
/*    */ 
/*    */   public static ConversationInfo obtain(Conversation.ConversationType type, String id)
/*    */   {
/* 12 */     ConversationInfo info = new ConversationInfo();
/* 13 */     info.conversationType = type;
/* 14 */     info.targetId = id;
/* 15 */     return info;
/*    */   }
/*    */ 
/*    */   public Conversation.ConversationType getConversationType() {
/* 19 */     return this.conversationType;
/*    */   }
/*    */ 
/*    */   public String getTargetId() {
/* 23 */     return this.targetId;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.model.ConversationInfo
 * JD-Core Version:    0.6.0
 */
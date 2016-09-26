/*    */ package io.rong.imkit.model;
/*    */ 
/*    */ import io.rong.imlib.model.Conversation.ConversationType;
/*    */ 
/*    */ public class DraftMessage
/*    */ {
/*    */   private String content;
/*    */   private Conversation.ConversationType conversationType;
/*    */   private String targetId;
/*    */ 
/*    */   public static DraftMessage obtain(Conversation.ConversationType type, String id, String msgContent)
/*    */   {
/* 13 */     DraftMessage obj = new DraftMessage();
/* 14 */     obj.content = msgContent;
/* 15 */     obj.conversationType = type;
/* 16 */     obj.targetId = id;
/* 17 */     return obj;
/*    */   }
/*    */ 
/*    */   public void setContent(String msg) {
/* 21 */     this.content = msg;
/*    */   }
/*    */ 
/*    */   public String getContent() {
/* 25 */     return this.content;
/*    */   }
/*    */ 
/*    */   public void setTargetId(String id) {
/* 29 */     this.targetId = id;
/*    */   }
/*    */   public String getTargetId() {
/* 32 */     return this.targetId;
/*    */   }
/*    */ 
/*    */   public void setConversationType(Conversation.ConversationType type) {
/* 36 */     this.conversationType = type;
/*    */   }
/*    */ 
/*    */   public Conversation.ConversationType getConversationType() {
/* 40 */     return this.conversationType;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.model.DraftMessage
 * JD-Core Version:    0.6.0
 */
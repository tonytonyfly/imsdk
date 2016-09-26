/*    */ package io.rong.imkit.model;
/*    */ 
/*    */ import android.text.TextUtils;
/*    */ import io.rong.common.RLog;
/*    */ import io.rong.imlib.model.Conversation.ConversationType;
/*    */ 
/*    */ public final class ConversationKey
/*    */ {
/*    */   public static final String SEPARATOR = "#@6RONG_CLOUD9@#";
/*    */   private String key;
/*    */   private String targetId;
/*    */   private Conversation.ConversationType type;
/*    */ 
/*    */   public static ConversationKey obtain(String targetId, Conversation.ConversationType type)
/*    */   {
/* 27 */     if ((!TextUtils.isEmpty(targetId)) && (type != null)) {
/* 28 */       ConversationKey conversationKey = new ConversationKey();
/* 29 */       conversationKey.setTargetId(targetId);
/* 30 */       conversationKey.setType(type);
/* 31 */       conversationKey.setKey(targetId + "#@6RONG_CLOUD9@#" + type.getValue());
/* 32 */       return conversationKey;
/*    */     }
/*    */ 
/* 35 */     return null;
/*    */   }
/*    */ 
/*    */   public static ConversationKey obtain(String key)
/*    */   {
/* 40 */     if ((!TextUtils.isEmpty(key)) && (key.contains("#@6RONG_CLOUD9@#"))) {
/* 41 */       ConversationKey conversationKey = new ConversationKey();
/*    */ 
/* 43 */       if (key.contains("#@6RONG_CLOUD9@#")) {
/* 44 */         String[] array = key.split("#@6RONG_CLOUD9@#");
/* 45 */         conversationKey.setTargetId(array[0]);
/*    */         try
/*    */         {
/* 48 */           conversationKey.setType(Conversation.ConversationType.setValue(Integer.parseInt(array[1])));
/*    */         } catch (NumberFormatException e) {
/* 50 */           RLog.e("ConversationKey ", "NumberFormatException");
/* 51 */           return null;
/*    */         }
/*    */ 
/* 54 */         return conversationKey;
/*    */       }
/*    */     }
/*    */ 
/* 58 */     return null;
/*    */   }
/*    */ 
/*    */   public String getKey()
/*    */   {
/* 63 */     return this.key;
/*    */   }
/*    */ 
/*    */   public void setKey(String key) {
/* 67 */     this.key = key;
/*    */   }
/*    */ 
/*    */   public String getTargetId() {
/* 71 */     return this.targetId;
/*    */   }
/*    */ 
/*    */   public void setTargetId(String targetId) {
/* 75 */     this.targetId = targetId;
/*    */   }
/*    */ 
/*    */   public Conversation.ConversationType getType() {
/* 79 */     return this.type;
/*    */   }
/*    */ 
/*    */   public void setType(Conversation.ConversationType type) {
/* 83 */     this.type = type;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.model.ConversationKey
 * JD-Core Version:    0.6.0
 */
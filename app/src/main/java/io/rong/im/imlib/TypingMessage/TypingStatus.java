/*    */ package io.rong.imlib.TypingMessage;
/*    */ 
/*    */ public class TypingStatus
/*    */ {
/*    */   private String userId;
/*    */   private String typingContentType;
/*    */   private long sentTime;
/*    */ 
/*    */   public TypingStatus(String userId, String typingContentType, long sentTime)
/*    */   {
/*  7 */     setUserId(userId);
/*  8 */     setTypingContentType(typingContentType);
/*  9 */     setSentTime(sentTime);
/*    */   }
/*    */ 
/*    */   public String getUserId() {
/* 13 */     return this.userId;
/*    */   }
/*    */ 
/*    */   public void setUserId(String userId) {
/* 17 */     this.userId = userId;
/*    */   }
/*    */ 
/*    */   public String getTypingContentType() {
/* 21 */     return this.typingContentType;
/*    */   }
/*    */ 
/*    */   public void setTypingContentType(String typingContentType) {
/* 25 */     this.typingContentType = typingContentType;
/*    */   }
/*    */ 
/*    */   public long getSentTime() {
/* 29 */     return this.sentTime;
/*    */   }
/*    */ 
/*    */   public void setSentTime(long sentTime) {
/* 33 */     this.sentTime = sentTime;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.TypingMessage.TypingStatus
 * JD-Core Version:    0.6.0
 */
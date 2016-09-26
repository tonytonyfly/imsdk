/*    */ package io.rong.imkit.model;
/*    */ 
/*    */ import io.rong.imlib.model.Conversation.ConversationType;
/*    */ import io.rong.imlib.model.Message;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.List;
/*    */ 
/*    */ public class ConversationTypeFilter
/*    */ {
/*    */   Level mLevel;
/* 23 */   List<Conversation.ConversationType> mTypes = new ArrayList();
/*    */ 
/*    */   public static ConversationTypeFilter obtain(Conversation.ConversationType[] conversationType)
/*    */   {
/* 27 */     return new ConversationTypeFilter(conversationType);
/*    */   }
/*    */ 
/*    */   public static ConversationTypeFilter obtain(Level level) {
/* 31 */     return new ConversationTypeFilter(level);
/*    */   }
/*    */ 
/*    */   public static ConversationTypeFilter obtain() {
/* 35 */     return new ConversationTypeFilter();
/*    */   }
/*    */ 
/*    */   private ConversationTypeFilter(Conversation.ConversationType[] type)
/*    */   {
/* 40 */     this.mTypes.addAll(Arrays.asList(type));
/* 41 */     this.mLevel = Level.CONVERSATION_TYPE;
/*    */   }
/*    */ 
/*    */   private ConversationTypeFilter() {
/* 45 */     this.mLevel = Level.ALL;
/*    */   }
/*    */ 
/*    */   private ConversationTypeFilter(Level level) {
/* 49 */     this.mLevel = level;
/*    */   }
/*    */ 
/*    */   public Level getLevel() {
/* 53 */     return this.mLevel;
/*    */   }
/*    */ 
/*    */   public List<Conversation.ConversationType> getConversationTypeList() {
/* 57 */     return this.mTypes;
/*    */   }
/*    */ 
/*    */   public boolean hasFilter(Message message)
/*    */   {
/* 62 */     if (this.mLevel == Level.ALL) {
/* 63 */       return true;
/*    */     }
/* 65 */     if (this.mLevel == Level.CONVERSATION_TYPE) {
/* 66 */       return this.mTypes.contains(message.getConversationType());
/*    */     }
/*    */ 
/* 70 */     return false;
/*    */   }
/*    */ 
/*    */   public static enum Level
/*    */   {
/* 18 */     ALL, CONVERSATION_TYPE, NONE;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.model.ConversationTypeFilter
 * JD-Core Version:    0.6.0
 */
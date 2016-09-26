/*    */ package io.rong.imlib.navigation;
/*    */ 
/*    */ public class LocationConfig
/*    */ {
/*    */   private boolean configure;
/*    */   private int[] conversationTypes;
/*    */   private int maxParticipant;
/*    */   private int refreshInterval;
/*    */   private int distanceFilter;
/*    */ 
/*    */   public boolean isConfigure()
/*    */   {
/* 16 */     return this.configure;
/*    */   }
/*    */ 
/*    */   public void setConfigure(boolean configure) {
/* 20 */     this.configure = configure;
/*    */   }
/*    */ 
/*    */   public int[] getConversationTypes() {
/* 24 */     return this.conversationTypes;
/*    */   }
/*    */ 
/*    */   public void setConversationTypes(int[] conversationTypes) {
/* 28 */     this.conversationTypes = conversationTypes;
/*    */   }
/*    */ 
/*    */   public int getMaxParticipant() {
/* 32 */     return this.maxParticipant;
/*    */   }
/*    */ 
/*    */   public void setMaxParticipant(int maxParticipant) {
/* 36 */     this.maxParticipant = maxParticipant;
/*    */   }
/*    */ 
/*    */   public int getRefreshInterval() {
/* 40 */     return this.refreshInterval;
/*    */   }
/*    */ 
/*    */   public void setRefreshInterval(int refreshInterval) {
/* 44 */     this.refreshInterval = refreshInterval;
/*    */   }
/*    */ 
/*    */   public int getDistanceFilter() {
/* 48 */     return this.distanceFilter;
/*    */   }
/*    */ 
/*    */   public void setDistanceFilter(int distanceFilter) {
/* 52 */     this.distanceFilter = distanceFilter;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.navigation.LocationConfig
 * JD-Core Version:    0.6.0
 */
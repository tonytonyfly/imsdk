/*    */ package io.rong.imkit.userInfoCache;
/*    */ 
/*    */ import android.net.Uri;
/*    */ 
/*    */ public class RongConversationInfo
/*    */ {
/*    */   private String conversationType;
/*    */   private String id;
/*    */   private String name;
/*    */   private Uri uri;
/*    */ 
/*    */   public RongConversationInfo(String type, String id, String name, Uri uri)
/*    */   {
/*  7 */     this.conversationType = type;
/*  8 */     this.id = id;
/*  9 */     this.name = name;
/* 10 */     this.uri = uri;
/*    */   }
/*    */ 
/*    */   public String getConversationType() {
/* 14 */     return this.conversationType;
/*    */   }
/*    */ 
/*    */   public void setConversationType(String conversationType) {
/* 18 */     this.conversationType = conversationType;
/*    */   }
/*    */ 
/*    */   public String getId() {
/* 22 */     return this.id;
/*    */   }
/*    */ 
/*    */   public void setId(String id) {
/* 26 */     this.id = id;
/*    */   }
/*    */ 
/*    */   public Uri getUri() {
/* 30 */     return this.uri;
/*    */   }
/*    */ 
/*    */   public void setUri(Uri uri) {
/* 34 */     this.uri = uri;
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 38 */     return this.name;
/*    */   }
/*    */ 
/*    */   public void setName(String name) {
/* 42 */     this.name = name;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.userInfoCache.RongConversationInfo
 * JD-Core Version:    0.6.0
 */
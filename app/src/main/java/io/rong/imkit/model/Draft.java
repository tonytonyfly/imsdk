/*    */ package io.rong.imkit.model;
/*    */ 
/*    */ public class Draft
/*    */ {
/*    */   private String id;
/*    */   private Integer type;
/*    */   private String content;
/*    */   private byte[] ext;
/*    */ 
/*    */   public Draft()
/*    */   {
/*    */   }
/*    */ 
/*    */   public Draft(String id, Integer type)
/*    */   {
/* 18 */     this.id = id;
/* 19 */     this.type = type;
/*    */   }
/*    */ 
/*    */   public Draft(String id, Integer type, String content, byte[] ext) {
/* 23 */     this.id = id;
/* 24 */     this.type = type;
/* 25 */     this.content = content;
/* 26 */     this.ext = ext;
/*    */   }
/*    */ 
/*    */   public String getId() {
/* 30 */     return this.id;
/*    */   }
/*    */ 
/*    */   public void setId(String id) {
/* 34 */     this.id = id;
/*    */   }
/*    */ 
/*    */   public Integer getType() {
/* 38 */     return this.type;
/*    */   }
/*    */ 
/*    */   public void setType(Integer type) {
/* 42 */     this.type = type;
/*    */   }
/*    */ 
/*    */   public String getContent() {
/* 46 */     return this.content;
/*    */   }
/*    */ 
/*    */   public void setContent(String content) {
/* 50 */     this.content = content;
/*    */   }
/*    */ 
/*    */   public byte[] getExt() {
/* 54 */     return this.ext;
/*    */   }
/*    */ 
/*    */   public void setExt(byte[] ext) {
/* 58 */     this.ext = ext;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.model.Draft
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.imlib.filetransfer;
/*    */ 
/*    */ public class Configuration
/*    */ {
/*    */   public final FtConst.ServiceType serviceType;
/*    */   public final int connectTimeout;
/*    */   public final int readTimeout;
/*    */ 
/*    */   private Configuration(Builder builder)
/*    */   {
/* 13 */     this.connectTimeout = builder.connectTimeout;
/* 14 */     this.readTimeout = builder.readTimeout;
/* 15 */     this.serviceType = builder.serviceType;
/*    */   }
/*    */ 
/*    */   public static class Builder
/*    */   {
/*    */     public int connectTimeout;
/*    */     public int readTimeout;
/*    */     public FtConst.ServiceType serviceType;
/*    */ 
/*    */     public Builder serverType(FtConst.ServiceType serviceType) {
/* 28 */       this.serviceType = serviceType;
/* 29 */       return this;
/*    */     }
/*    */ 
/*    */     public Builder connectTimeout(int connectTimeout) {
/* 33 */       this.connectTimeout = connectTimeout;
/* 34 */       return this;
/*    */     }
/*    */ 
/*    */     public Builder readTimeout(int readTimeout) {
/* 38 */       this.readTimeout = readTimeout;
/* 39 */       return this;
/*    */     }
/*    */ 
/*    */     public Configuration build() {
/* 43 */       return new Configuration(this, null);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.filetransfer.Configuration
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.imageloader.cache.disc.naming;
/*    */ 
/*    */ public class HashCodeFileNameGenerator
/*    */   implements FileNameGenerator
/*    */ {
/*    */   public String generate(String imageUri)
/*    */   {
/* 27 */     return String.valueOf(imageUri.hashCode());
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.disc.naming.HashCodeFileNameGenerator
 * JD-Core Version:    0.6.0
 */
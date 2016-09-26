/*    */ package io.rong.imageloader.cache.disc.naming;
/*    */ 
/*    */ import io.rong.imageloader.utils.L;
/*    */ import java.math.BigInteger;
/*    */ import java.security.MessageDigest;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ 
/*    */ public class Md5FileNameGenerator
/*    */   implements FileNameGenerator
/*    */ {
/*    */   private static final String HASH_ALGORITHM = "MD5";
/*    */   private static final int RADIX = 36;
/*    */ 
/*    */   public String generate(String imageUri)
/*    */   {
/* 37 */     byte[] md5 = getMD5(imageUri.getBytes());
/* 38 */     BigInteger bi = new BigInteger(md5).abs();
/* 39 */     return bi.toString(36);
/*    */   }
/*    */ 
/*    */   private byte[] getMD5(byte[] data) {
/* 43 */     byte[] hash = null;
/*    */     try {
/* 45 */       MessageDigest digest = MessageDigest.getInstance("MD5");
/* 46 */       digest.update(data);
/* 47 */       hash = digest.digest();
/*    */     } catch (NoSuchAlgorithmException e) {
/* 49 */       L.e(e);
/*    */     }
/* 51 */     return hash;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.disc.naming.Md5FileNameGenerator
 * JD-Core Version:    0.6.0
 */
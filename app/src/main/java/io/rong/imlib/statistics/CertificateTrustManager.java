/*    */ package io.rong.imlib.statistics;
/*    */ 
/*    */ import android.util.Base64;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.security.KeyStore;
/*    */ import java.security.PublicKey;
/*    */ import java.security.cert.Certificate;
/*    */ import java.security.cert.CertificateException;
/*    */ import java.security.cert.CertificateFactory;
/*    */ import java.security.cert.X509Certificate;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.List;
/*    */ import javax.net.ssl.TrustManager;
/*    */ import javax.net.ssl.TrustManagerFactory;
/*    */ import javax.net.ssl.X509TrustManager;
/*    */ 
/*    */ public final class CertificateTrustManager
/*    */   implements X509TrustManager
/*    */ {
/*    */   private final List<byte[]> keys;
/*    */ 
/*    */   public CertificateTrustManager(List<String> certificates)
/*    */     throws CertificateException
/*    */   {
/* 32 */     if ((certificates == null) || (certificates.size() == 0)) {
/* 33 */       throw new IllegalArgumentException("You must specify non-empty keys list");
/*    */     }
/*    */ 
/* 36 */     this.keys = new ArrayList();
/* 37 */     for (String key : certificates) {
/* 38 */       CertificateFactory cf = CertificateFactory.getInstance("X.509");
/* 39 */       Certificate cert = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(key, 0)));
/* 40 */       this.keys.add(cert.getPublicKey().getEncoded());
/*    */     }
/*    */   }
/*    */ 
/*    */   public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
/* 45 */     if (chain == null) {
/* 46 */       throw new IllegalArgumentException("PublicKeyManager: X509Certificate array is null");
/*    */     }
/*    */ 
/* 49 */     if (chain.length <= 0) {
/* 50 */       throw new IllegalArgumentException("PublicKeyManager: X509Certificate is empty");
/*    */     }
/*    */ 
/* 53 */     if ((null == authType) || (!authType.equalsIgnoreCase("RSA"))) {
/* 54 */       throw new CertificateException("PublicKeyManager: AuthType is not RSA");
/*    */     }
/*    */ 
/*    */     try
/*    */     {
/* 60 */       TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
/* 61 */       tmf.init((KeyStore)null);
/*    */ 
/* 63 */       for (TrustManager trustManager : tmf.getTrustManagers())
/* 64 */         ((X509TrustManager)trustManager).checkServerTrusted(chain, authType);
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 68 */       throw new CertificateException(e);
/*    */     }
/*    */ 
/* 71 */     byte[] server = chain[0].getPublicKey().getEncoded();
/*    */ 
/* 73 */     for (byte[] key : this.keys) {
/* 74 */       if (Arrays.equals(key, server)) {
/* 75 */         return;
/*    */       }
/*    */     }
/*    */ 
/* 79 */     throw new CertificateException("Public keys didn't pass checks");
/*    */   }
/*    */ 
/*    */   public void checkClientTrusted(X509Certificate[] xcs, String string)
/*    */   {
/*    */   }
/*    */ 
/*    */   public X509Certificate[] getAcceptedIssuers()
/*    */   {
/* 90 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.statistics.CertificateTrustManager
 * JD-Core Version:    0.6.0
 */
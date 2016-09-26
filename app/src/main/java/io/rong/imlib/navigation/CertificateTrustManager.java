/*    */ package io.rong.imlib.navigation;
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
/* 28 */     if ((certificates == null) || (certificates.size() == 0)) {
/* 29 */       throw new IllegalArgumentException("You must specify non-empty keys list");
/*    */     }
/*    */ 
/* 32 */     this.keys = new ArrayList();
/* 33 */     for (String key : certificates) {
/* 34 */       CertificateFactory cf = CertificateFactory.getInstance("X.509");
/* 35 */       Certificate cert = cf.generateCertificate(new ByteArrayInputStream(Base64.decode(key, 0)));
/* 36 */       this.keys.add(cert.getPublicKey().getEncoded());
/*    */     }
/*    */   }
/*    */ 
/*    */   public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
/* 41 */     if (chain == null) {
/* 42 */       throw new IllegalArgumentException("PublicKeyManager: X509Certificate array is null");
/*    */     }
/*    */ 
/* 45 */     if (chain.length <= 0) {
/* 46 */       throw new IllegalArgumentException("PublicKeyManager: X509Certificate is empty");
/*    */     }
/*    */ 
/* 49 */     if ((null == authType) || (!authType.equalsIgnoreCase("RSA"))) {
/* 50 */       throw new CertificateException("PublicKeyManager: AuthType is not RSA");
/*    */     }
/*    */ 
/*    */     try
/*    */     {
/* 56 */       TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
/* 57 */       tmf.init((KeyStore)null);
/*    */ 
/* 59 */       for (TrustManager trustManager : tmf.getTrustManagers())
/* 60 */         ((X509TrustManager)trustManager).checkServerTrusted(chain, authType);
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 64 */       throw new CertificateException(e);
/*    */     }
/*    */ 
/* 67 */     byte[] server = chain[0].getPublicKey().getEncoded();
/*    */ 
/* 69 */     for (byte[] key : this.keys) {
/* 70 */       if (Arrays.equals(key, server)) {
/* 71 */         return;
/*    */       }
/*    */     }
/*    */ 
/* 75 */     throw new CertificateException("Public keys didn't pass checks");
/*    */   }
/*    */ 
/*    */   public void checkClientTrusted(X509Certificate[] xcs, String string)
/*    */   {
/*    */   }
/*    */ 
/*    */   public X509Certificate[] getAcceptedIssuers()
/*    */   {
/* 86 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.navigation.CertificateTrustManager
 * JD-Core Version:    0.6.0
 */
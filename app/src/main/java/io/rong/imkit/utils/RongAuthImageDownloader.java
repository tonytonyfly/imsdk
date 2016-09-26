/*     */ package io.rong.imkit.utils;
/*     */ 
/*     */ import android.content.Context;
/*     */ import io.rong.imageloader.core.assist.ContentLengthInputStream;
/*     */ import io.rong.imageloader.core.download.BaseImageDownloader;
/*     */ import io.rong.imageloader.utils.IoUtils;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.security.KeyManagementException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ 
/*     */ public class RongAuthImageDownloader extends BaseImageDownloader
/*     */ {
/*     */   private SSLSocketFactory mSSLSocketFactory;
/*  72 */   final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier()
/*     */   {
/*     */     public boolean verify(String hostname, SSLSession session) {
/*  75 */       return true;
/*     */     }
/*  72 */   };
/*     */ 
/*     */   public RongAuthImageDownloader(Context context)
/*     */   {
/*  29 */     super(context);
/*  30 */     SSLContext sslContext = sslContextForTrustedCertificates();
/*  31 */     this.mSSLSocketFactory = sslContext.getSocketFactory();
/*     */   }
/*     */ 
/*     */   public RongAuthImageDownloader(Context context, int connectTimeout, int readTimeout) {
/*  35 */     super(context, connectTimeout, readTimeout);
/*  36 */     SSLContext sslContext = sslContextForTrustedCertificates();
/*  37 */     this.mSSLSocketFactory = sslContext.getSocketFactory();
/*     */   }
/*     */ 
/*     */   protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException
/*     */   {
/*  42 */     URL url = null;
/*     */     try {
/*  44 */       url = new URL(imageUri);
/*     */     } catch (MalformedURLException e) {
/*  46 */       e.printStackTrace();
/*     */     }
/*  48 */     HttpURLConnection conn = (HttpURLConnection)url.openConnection();
/*  49 */     conn.setConnectTimeout(this.connectTimeout);
/*  50 */     conn.setReadTimeout(this.readTimeout);
/*     */ 
/*  52 */     if ((conn instanceof HttpsURLConnection)) {
/*  53 */       ((HttpsURLConnection)conn).setSSLSocketFactory(this.mSSLSocketFactory);
/*  54 */       ((HttpsURLConnection)conn).setHostnameVerifier(this.DO_NOT_VERIFY);
/*     */     }InputStream imageStream;
/*     */     try {
/*  58 */       imageStream = conn.getInputStream();
/*     */     }
/*     */     catch (IOException e) {
/*  61 */       IoUtils.readAndCloseStream(conn.getErrorStream());
/*  62 */       throw e;
/*     */     }
/*  64 */     if (!shouldBeProcessed(conn)) {
/*  65 */       IoUtils.closeSilently(imageStream);
/*  66 */       throw new IOException("Image request failed with response code " + conn.getResponseCode());
/*     */     }
/*     */ 
/*  69 */     return new ContentLengthInputStream(new BufferedInputStream(imageStream, 32768), conn.getContentLength());
/*     */   }
/*     */ 
/*     */   private SSLContext sslContextForTrustedCertificates()
/*     */   {
/*  80 */     TrustManager[] trustAllCerts = new TrustManager[1];
/*  81 */     TrustManager tm = new miTM();
/*  82 */     trustAllCerts[0] = tm;
/*  83 */     SSLContext sc = null;
/*     */     try {
/*  85 */       sc = SSLContext.getInstance("SSL");
/*  86 */       sc.init(null, trustAllCerts, null);
/*     */       return sc;
/*     */     }
/*     */     catch (NoSuchAlgorithmException e)
/*     */     {
/*  89 */       e.printStackTrace();
/*     */       return sc;
/*     */     }
/*     */     catch (KeyManagementException e)
/*     */     {
/*  91 */       e.printStackTrace();
/*     */       return sc; } finally {  }
/*  93 */     return sc;
/*     */   }
/*     */ 
/*     */   class miTM implements TrustManager, X509TrustManager {
/*     */     miTM() {
/*     */     }
/*     */ 
/*     */     public X509Certificate[] getAcceptedIssuers() {
/* 101 */       return null;
/*     */     }
/*     */ 
/*     */     public boolean isServerTrusted(X509Certificate[] certs)
/*     */     {
/* 106 */       return true;
/*     */     }
/*     */ 
/*     */     public boolean isClientTrusted(X509Certificate[] certs)
/*     */     {
/* 111 */       return true;
/*     */     }
/*     */ 
/*     */     public void checkServerTrusted(X509Certificate[] certs, String authType)
/*     */       throws CertificateException
/*     */     {
/*     */     }
/*     */ 
/*     */     public void checkClientTrusted(X509Certificate[] certs, String authType)
/*     */       throws CertificateException
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.utils.RongAuthImageDownloader
 * JD-Core Version:    0.6.0
 */
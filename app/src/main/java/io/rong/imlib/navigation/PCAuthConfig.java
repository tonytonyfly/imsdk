/*     */ package io.rong.imlib.navigation;
/*     */ 
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.RLog;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.net.URLEncoder;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ 
/*     */ public class PCAuthConfig
/*     */ {
/*     */   private static final String TAG = "AuthConfig";
/*     */   private static final String NAVIGATION_HTTPS_URL = "https://private.cn.ronghub.com/check.json";
/*     */   private ExecutorService executor;
/*     */   private Future<?> connectionFuture;
/*     */ 
/*     */   public static PCAuthConfig getInstance()
/*     */   {
/*  38 */     return SingletonHolder.sIns;
/*     */   }
/*     */ 
/*     */   public void postConfig(String customId, String code, String appKey, String naviIP, String cmpIP) {
/*  42 */     if ((cmpIP != null) && (naviIP != null) && (appKey != null)) {
/*  43 */       if ((this.connectionFuture != null) && (!this.connectionFuture.isDone())) {
/*  44 */         this.connectionFuture.cancel(true);
/*     */       }
/*  46 */       ensureExecutor();
/*  47 */       this.connectionFuture = this.executor.submit(new Runnable(customId, code, appKey, naviIP, cmpIP)
/*     */       {
/*     */         public void run() {
/*  50 */           PCAuthConfig.this.request(this.val$customId, this.val$code, this.val$appKey, this.val$naviIP, this.val$cmpIP);
/*     */         } } );
/*     */     }
/*     */   }
/*     */ 
/*     */   private void request(String customId, String code, String appKey, String naviIP, String cmpIP) {
/*  57 */     RLog.i("AuthConfig", "request : " + customId + "-" + code);
/*     */ 
/*  59 */     if (TextUtils.isEmpty(appKey)) {
/*  60 */       return;
/*     */     }
/*     */ 
/*  63 */     HttpURLConnection connection = null;
/*  64 */     int responseCode = 0;
/*     */     try {
/*  66 */       connection = createConnection(customId, code, appKey, naviIP, cmpIP);
/*  67 */       connection.connect();
/*  68 */       responseCode = connection.getResponseCode();
/*     */     } catch (Exception e) {
/*  70 */       e.printStackTrace();
/*     */     } finally {
/*  72 */       RLog.i("AuthConfig", "request: " + responseCode);
/*  73 */       if (connection != null)
/*  74 */         connection.disconnect();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void ensureExecutor()
/*     */   {
/*  80 */     if (this.executor == null)
/*  81 */       this.executor = Executors.newSingleThreadExecutor();
/*     */   }
/*     */ 
/*     */   private SSLContext initSSL()
/*     */   {
/*  86 */     SSLContext sslContext = null;
/*     */     try {
/*  88 */       TrustManager[] tm = { new X509TrustManager()
/*     */       {
/*     */         public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
/*     */         {
/*     */         }
/*     */ 
/*     */         public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
/*     */         {
/*     */         }
/*     */ 
/*     */         public X509Certificate[] getAcceptedIssuers() {
/*  99 */           return new X509Certificate[0];
/*     */         }
/*     */       }
/*     */        };
/* 103 */       sslContext = SSLContext.getInstance("TLS");
/* 104 */       sslContext.init(null, tm, null);
/*     */     } catch (Throwable e) {
/* 106 */       throw new IllegalStateException(e);
/*     */     }
/* 108 */     return sslContext;
/*     */   }
/*     */ 
/*     */   private HttpURLConnection createConnection(String customId, String code, String appKey, String navi, String cmpIP) throws IOException {
/* 112 */     URL url = new URL("https://private.cn.ronghub.com/check.json");
/* 113 */     SSLContext sslContext = initSSL();
/* 114 */     HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
/* 115 */     conn.setSSLSocketFactory(sslContext.getSocketFactory());
/* 116 */     conn.setConnectTimeout(10000);
/* 117 */     conn.setReadTimeout(10000);
/* 118 */     conn.setUseCaches(false);
/*     */ 
/* 120 */     conn.setRequestMethod("POST");
/* 121 */     conn.setRequestProperty("Connection", "Close");
/* 122 */     String params = "customId=";
/* 123 */     params = params + URLEncoder.encode(customId, "UTF-8");
/* 124 */     params = params + "&code=";
/* 125 */     params = params + URLEncoder.encode(code, "UTF-8");
/* 126 */     params = params + "&appKey=" + appKey;
/* 127 */     params = params + "&nip=" + navi;
/* 128 */     params = params + "&ip=" + cmpIP;
/* 129 */     conn.setRequestProperty("Content-Length", String.valueOf(params.length()));
/*     */ 
/* 131 */     conn.setDoOutput(true);
/* 132 */     conn.setDoInput(true);
/*     */ 
/* 134 */     OutputStream os = conn.getOutputStream();
/* 135 */     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
/* 136 */     writer.write(params);
/* 137 */     writer.flush();
/* 138 */     writer.close();
/* 139 */     os.close();
/*     */ 
/* 141 */     return conn;
/*     */   }
/*     */ 
/*     */   private static class SingletonHolder
/*     */   {
/*  34 */     private static PCAuthConfig sIns = new PCAuthConfig();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.navigation.PCAuthConfig
 * JD-Core Version:    0.6.0
 */
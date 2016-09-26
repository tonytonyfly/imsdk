/*     */ package io.rong.imlib.navigation;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.RFLog;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.common.BuildVar;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.net.URLEncoder;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ 
/*     */ public class NavigationClient
/*     */ {
/*     */   private static final String TAG = "NavigationClient";
/*     */   private static final String NAVIGATION_HTTPS_URL = "https://nav.cn.ronghub.com/navi.xml";
/*  35 */   private static String NAVIGATION_HTTP_URL = "http://nav.cn.ronghub.com/navi.xml";
/*     */   private ExecutorService executor;
/*     */   private Context context;
/*     */   private boolean verifyCertificate;
/*     */   private NavigationObserver navigationObserver;
/*     */ 
/*     */   private NavigationClient()
/*     */   {
/*  44 */     this.executor = Executors.newSingleThreadExecutor();
/*     */   }
/*     */ 
/*     */   public static NavigationClient getInstance()
/*     */   {
/*  52 */     return SingletonHolder.sIns;
/*     */   }
/*     */ 
/*     */   public void enablePublicKeyPinning() {
/*  56 */     this.verifyCertificate = true;
/*     */   }
/*     */ 
/*     */   public void setNaviDomain(String navi) {
/*  60 */     NAVIGATION_HTTP_URL = String.format("http://%s/navi.xml", new Object[] { navi });
/*     */   }
/*     */ 
/*     */   public void addObserver(NavigationObserver observer) {
/*  64 */     this.navigationObserver = observer;
/*     */   }
/*     */ 
/*     */   public void clearObserver() {
/*  68 */     this.navigationObserver = null;
/*     */   }
/*     */ 
/*     */   public String getCMPServer() {
/*  72 */     return NavigationCacheHelper.getCMPServer(this.context);
/*     */   }
/*     */ 
/*     */   public long getLastCachedTime() {
/*  76 */     return NavigationCacheHelper.getCachedTime();
/*     */   }
/*     */ 
/*     */   public void getCMPServer(Context context, String appKey, String token) {
/*  80 */     this.context = context;
/*     */ 
/*  82 */     if (NavigationCacheHelper.isCacheValid(context, appKey, token)) {
/*  83 */       if (this.navigationObserver != null) {
/*  84 */         String cmp = NavigationCacheHelper.getCMPServer(context);
/*  85 */         this.navigationObserver.onSuccess(cmp);
/*     */       }
/*     */     } else {
/*  88 */       NavigationCacheHelper.clearCache(context);
/*  89 */       this.executor.submit(new Runnable(appKey, token)
/*     */       {
/*     */         public void run() {
/*  92 */           NavigationClient.this.request(this.val$appKey, this.val$token, false);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isMPOpened(Context context)
/*     */   {
/* 106 */     return NavigationCacheHelper.isMPOpened(context);
/*     */   }
/*     */ 
/*     */   public boolean needUpdateCMP(Context context, String appKey, String token)
/*     */   {
/* 115 */     boolean updated = false;
/* 116 */     if (NavigationCacheHelper.isCacheTimeout(context)) {
/* 117 */       this.executor.submit(new Runnable(appKey, token)
/*     */       {
/*     */         public void run() {
/* 120 */           NavigationClient.this.request(this.val$appKey, this.val$token, true);
/*     */         }
/*     */       });
/* 123 */       updated = true;
/*     */     }
/* 125 */     return updated;
/*     */   }
/*     */ 
/*     */   public void clearCache(Context context) {
/* 129 */     NavigationCacheHelper.clearCache(context);
/*     */   }
/*     */ 
/*     */   public void updateCacheTime(Context context) {
/* 133 */     NavigationCacheHelper.updateTime(context, 0L);
/*     */   }
/*     */ 
/*     */   public String getVoIPCallInfo(Context context) {
/* 137 */     return NavigationCacheHelper.getVoIPCallInfo(context);
/*     */   }
/*     */ 
/*     */   public String getMediaServer(Context context) {
/* 141 */     return NavigationCacheHelper.getMediaServer(context);
/*     */   }
/*     */ 
/*     */   public boolean isGetRemoteEnabled(Context context) {
/* 145 */     return NavigationCacheHelper.isGetRemoteEnabled(context);
/*     */   }
/*     */ 
/*     */   public LocationConfig getLocationConfig(Context context) {
/* 149 */     return NavigationCacheHelper.getLocationConfig(context);
/*     */   }
/*     */ 
/*     */   private void request(String appKey, String token, boolean forceUpdate) {
/* 153 */     RLog.i("NavigationClient", "request start: " + token);
/* 154 */     HttpURLConnection connection = null;
/* 155 */     BufferedInputStream responseStream = null;
/* 156 */     int responseCode = 0;
/*     */     try
/*     */     {
/* 159 */       connection = createConnection(appKey, token);
/* 160 */       connection.connect();
/*     */ 
/* 162 */       responseCode = connection.getResponseCode();
/*     */       InputStream inputStream;
/*     */       InputStream inputStream;
/* 164 */       if (responseCode != 200)
/* 165 */         inputStream = connection.getErrorStream();
/*     */       else {
/* 167 */         inputStream = connection.getInputStream();
/*     */       }
/*     */ 
/* 170 */       responseStream = new BufferedInputStream(inputStream);
/* 171 */       ByteArrayOutputStream responseData = new ByteArrayOutputStream(512);
/*     */       int c;
/* 173 */       while ((c = responseStream.read()) != -1) {
/* 174 */         responseData.write(c);
/*     */       }
/* 176 */       String data = new String(responseData.toByteArray(), "utf-8").trim();
/* 177 */       if (forceUpdate)
/*     */       {
/* 179 */         String newCmp = NavigationCacheHelper.decode2cmp(this.context, data, responseCode);
/* 180 */         String oldCmp = NavigationCacheHelper.getCMPServer(this.context);
/* 181 */         if ((!TextUtils.isEmpty(newCmp)) && (!newCmp.equals(oldCmp))) {
/* 182 */           if (this.navigationObserver != null)
/* 183 */             this.navigationObserver.onReconnect(newCmp, new NavigationCallback(data, appKey, token)
/*     */             {
/*     */               public void onSuccess() {
/* 186 */                 NavigationCacheHelper.decode2File(NavigationClient.this.context, this.val$data, 200);
/* 187 */                 NavigationCacheHelper.cacheRequest(NavigationClient.this.context, this.val$appKey, this.val$token);
/*     */               }
/*     */ 
/*     */               public void onError()
/*     */               {
/* 192 */                 long gmtTimestamp = System.currentTimeMillis() - TimeZone.getDefault().getRawOffset();
/* 193 */                 NavigationCacheHelper.updateTime(NavigationClient.this.context, gmtTimestamp);
/*     */               } } );
/*     */         }
/*     */         else {
/* 198 */           NavigationCacheHelper.decode2File(this.context, data, responseCode);
/* 199 */           NavigationCacheHelper.cacheRequest(this.context, appKey, token);
/*     */         }
/*     */       }
/*     */       else {
/* 203 */         int result = NavigationCacheHelper.decode2File(this.context, data, responseCode);
/* 204 */         if (result == 0) {
/* 205 */           if (this.navigationObserver != null) {
/* 206 */             String cmp = NavigationCacheHelper.getCMPServer(this.context);
/* 207 */             this.navigationObserver.onSuccess(cmp);
/*     */           }
/* 209 */           NavigationCacheHelper.cacheRequest(this.context, appKey, token);
/*     */         } else {
/* 211 */           if (this.navigationObserver != null)
/* 212 */             this.navigationObserver.onError(null, result);
/* 213 */           RLog.e("NavigationClient", "request failure : " + result + ", data = " + data);
/*     */         }
/*     */       }
/*     */     } catch (Exception ignored) {
/* 217 */       if (this.navigationObserver != null)
/* 218 */         this.navigationObserver.onError(NavigationCacheHelper.getCMPServer(this.context), 30004);
/* 219 */       e.printStackTrace();
/* 220 */       RFLog.write("NavigationClient", e);
/*     */     } finally {
/* 222 */       RLog.i("NavigationClient", "request end: " + responseCode + ", force = " + forceUpdate);
/* 223 */       if (responseStream != null) {
/*     */         try {
/* 225 */           responseStream.close();
/*     */         } catch (IOException ignored) {
/* 227 */           ignored.printStackTrace();
/*     */         }
/*     */       }
/* 230 */       if (connection != null)
/* 231 */         connection.disconnect();
/*     */     }
/*     */   }
/*     */ 
/*     */   private SSLContext initSSL()
/*     */   {
/* 237 */     SSLContext sslContext = null;
/*     */     try
/*     */     {
/* 240 */       TrustManager[] tm = { new X509TrustManager()
/*     */       {
/*     */         public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
/*     */         {
/*     */         }
/*     */ 
/*     */         public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
/*     */         {
/*     */         }
/*     */ 
/*     */         public X509Certificate[] getAcceptedIssuers()
/*     */         {
/* 252 */           return new X509Certificate[0];
/*     */         }
/*     */       }
/*     */        };
/* 256 */       sslContext = SSLContext.getInstance("TLS");
/* 257 */       sslContext.init(null, tm, null);
/*     */     } catch (Throwable e) {
/* 259 */       throw new IllegalStateException(e);
/*     */     }
/* 261 */     return sslContext;
/*     */   }
/*     */ 
/*     */   private HttpURLConnection createConnection(String appKey, String token)
/*     */     throws IOException
/*     */   {
/*     */     HttpURLConnection conn;
/*     */     HttpURLConnection conn;
/* 267 */     if (this.verifyCertificate) {
/* 268 */       URL url = new URL("https://nav.cn.ronghub.com/navi.xml");
/* 269 */       SSLContext sslContext = initSSL();
/* 270 */       HttpsURLConnection c = (HttpsURLConnection)url.openConnection();
/* 271 */       c.setSSLSocketFactory(sslContext.getSocketFactory());
/* 272 */       conn = c;
/*     */     } else {
/* 274 */       URL url = new URL(NAVIGATION_HTTP_URL);
/* 275 */       conn = (HttpURLConnection)url.openConnection();
/*     */     }
/* 277 */     conn.setConnectTimeout(30000);
/* 278 */     conn.setReadTimeout(30000);
/* 279 */     conn.setUseCaches(false);
/*     */ 
/* 281 */     conn.setRequestMethod("POST");
/* 282 */     conn.setRequestProperty("Connection", "Close");
/* 283 */     conn.setRequestProperty("User-Agent", "RongCloud");
/* 284 */     String params = "token=";
/* 285 */     params = params + URLEncoder.encode(token, "UTF-8");
/* 286 */     params = params + "&v=" + BuildVar.SDK_VERSION;
/* 287 */     conn.setRequestProperty("Content-Length", String.valueOf(params.length()));
/* 288 */     conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
/* 289 */     conn.setRequestProperty("appId", appKey);
/*     */ 
/* 291 */     conn.setDoOutput(true);
/* 292 */     conn.setDoInput(true);
/*     */ 
/* 294 */     OutputStream os = conn.getOutputStream();
/* 295 */     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
/* 296 */     writer.write(params);
/* 297 */     writer.flush();
/* 298 */     writer.close();
/* 299 */     os.close();
/*     */ 
/* 301 */     return conn;
/*     */   }
/*     */ 
/*     */   private static class SingletonHolder
/*     */   {
/*  48 */     private static NavigationClient sIns = new NavigationClient(null);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.navigation.NavigationClient
 * JD-Core Version:    0.6.0
 */
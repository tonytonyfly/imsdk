/*     */ package io.rong.imlib.statistics;
/*     */ 
/*     */ import android.content.Context;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ 
/*     */ public class ConnectionQueue
/*     */ {
/*     */   private StatisticsStore store_;
/*     */   private ExecutorService executor_;
/*     */   private String appKey_;
/*     */   private Context context_;
/*     */   private String serverURL_;
/*     */   private Future<?> connectionProcessorFuture_;
/*     */   private DeviceId deviceId_;
/*     */   private SSLContext sslContext_;
/*     */ 
/*     */   String getAppKey()
/*     */   {
/*  60 */     return this.appKey_;
/*     */   }
/*     */ 
/*     */   void setAppKey(String appKey) {
/*  64 */     this.appKey_ = appKey;
/*     */   }
/*     */ 
/*     */   Context getContext() {
/*  68 */     return this.context_;
/*     */   }
/*     */ 
/*     */   void setContext(Context context) {
/*  72 */     this.context_ = context;
/*     */   }
/*     */ 
/*     */   String getServerURL() {
/*  76 */     return this.serverURL_;
/*     */   }
/*     */ 
/*     */   void setServerURL(String serverURL) {
/*  80 */     this.serverURL_ = serverURL;
/*     */ 
/*  82 */     if (Statistics.publicKeyPinCertificates == null) {
/*  83 */       this.sslContext_ = null;
/*     */     }
/*     */     else
/*     */     {
/*     */       try
/*     */       {
/*  89 */         TrustManager[] tm = { new X509TrustManager()
/*     */         {
/*     */           public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
/*     */           {
/*     */           }
/*     */ 
/*     */           public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
/*     */           {
/*     */           }
/*     */ 
/*     */           public X509Certificate[] getAcceptedIssuers() {
/* 100 */             return new X509Certificate[0];
/*     */           }
/*     */         }
/*     */          };
/* 104 */         this.sslContext_ = SSLContext.getInstance("TLS");
/* 105 */         this.sslContext_.init(null, tm, null);
/*     */       } catch (Throwable e) {
/* 107 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   StatisticsStore getCountlyStore() {
/* 113 */     return this.store_;
/*     */   }
/*     */ 
/*     */   void setCountlyStore(StatisticsStore statisticsStore) {
/* 117 */     this.store_ = statisticsStore;
/*     */   }
/*     */ 
/*     */   DeviceId getDeviceId() {
/* 121 */     return this.deviceId_;
/*     */   }
/*     */ 
/*     */   public void setDeviceId(DeviceId deviceId) {
/* 125 */     this.deviceId_ = deviceId;
/*     */   }
/*     */ 
/*     */   void checkInternalState()
/*     */   {
/* 133 */     if (this.context_ == null) {
/* 134 */       throw new IllegalStateException("context has not been set");
/*     */     }
/* 136 */     if ((this.appKey_ == null) || (this.appKey_.length() == 0)) {
/* 137 */       throw new IllegalStateException("app key has not been set");
/*     */     }
/* 139 */     if (this.store_ == null) {
/* 140 */       throw new IllegalStateException("countly store has not been set");
/*     */     }
/* 142 */     if ((this.serverURL_ == null) || (!Statistics.isValidURL(this.serverURL_))) {
/* 143 */       throw new IllegalStateException("server URL is not valid");
/*     */     }
/* 145 */     if ((Statistics.publicKeyPinCertificates != null) && (!this.serverURL_.startsWith("https")))
/* 146 */       throw new IllegalStateException("server must start with https once you specified public keys");
/*     */   }
/*     */ 
/*     */   void beginSession()
/*     */   {
/* 156 */     if (this.store_.uploadIfNeed()) {
/* 157 */       checkInternalState();
/* 158 */       String data = new StringBuilder().append("deviceId=").append(this.deviceId_.getId()).append("&appKey=").append(this.appKey_).append("&timestamp=").append(Statistics.currentTimestamp()).toString();
/*     */ 
/* 161 */       data = new StringBuilder().append(data).append("&deviceInfo=").append(DeviceInfo.getMetrics(this.context_)).toString();
/* 162 */       this.store_.addConnection(data);
/*     */ 
/* 164 */       tick();
/*     */     }
/*     */   }
/*     */ 
/*     */   void updateSession(int duration)
/*     */   {
/* 175 */     checkInternalState();
/* 176 */     if (duration > 0) {
/* 177 */       String data = new StringBuilder().append("deviceId=").append(this.deviceId_.getId()).append("&appKey=").append(this.appKey_).append("&timestamp=").append(Statistics.currentTimestamp()).toString();
/*     */ 
/* 181 */       this.store_.addConnection(data);
/*     */ 
/* 183 */       tick();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void tokenSession(String token, Statistics.CountlyMessagingMode mode) {
/* 188 */     checkInternalState();
/*     */ 
/* 190 */     String data = new StringBuilder().append("app_key=").append(this.appKey_).append("&").append("timestamp=").append(Statistics.currentTimestamp()).append("&").append("token_session=1").append("&").append("android_token=").append(token).append("&").append("test_mode=").append(mode == Statistics.CountlyMessagingMode.TEST ? 2 : 0).append("&").append("locale=").append(DeviceInfo.getLocale()).toString();
/*     */ 
/* 198 */     ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
/* 199 */     worker.schedule(new Runnable(data)
/*     */     {
/*     */       public void run() {
/* 202 */         ConnectionQueue.this.store_.addConnection(this.val$data);
/* 203 */         ConnectionQueue.this.tick();
/*     */       }
/*     */     }
/*     */     , 10L, TimeUnit.SECONDS);
/*     */   }
/*     */ 
/*     */   void endSession(int duration)
/*     */   {
/* 215 */     checkInternalState();
/* 216 */     String data = new StringBuilder().append("app_key=").append(this.appKey_).append("&timestamp=").append(Statistics.currentTimestamp()).append("&end_session=1").toString();
/*     */ 
/* 219 */     if (duration > 0) {
/* 220 */       data = new StringBuilder().append(data).append("&session_duration=").append(duration).toString();
/*     */     }
/*     */ 
/* 223 */     this.store_.addConnection(data);
/*     */ 
/* 225 */     tick();
/*     */   }
/*     */ 
/*     */   void sendUserData()
/*     */   {
/* 233 */     checkInternalState();
/* 234 */     String userdata = UserData.getDataForRequest();
/*     */ 
/* 236 */     if (!userdata.equals("")) {
/* 237 */       String data = new StringBuilder().append("app_key=").append(this.appKey_).append("&timestamp=").append(Statistics.currentTimestamp()).append(userdata).toString();
/*     */ 
/* 240 */       this.store_.addConnection(data);
/*     */ 
/* 242 */       tick();
/*     */     }
/*     */   }
/*     */ 
/*     */   void sendReferrerData(String referrer)
/*     */   {
/* 252 */     checkInternalState();
/*     */ 
/* 254 */     if (referrer != null) {
/* 255 */       String data = new StringBuilder().append("app_key=").append(this.appKey_).append("&timestamp=").append(Statistics.currentTimestamp()).append(referrer).toString();
/*     */ 
/* 258 */       this.store_.addConnection(data);
/*     */ 
/* 260 */       tick();
/*     */     }
/*     */   }
/*     */ 
/*     */   void sendCrashReport(String error, boolean nonfatal)
/*     */   {
/* 269 */     checkInternalState();
/* 270 */     String data = new StringBuilder().append("app_key=").append(this.appKey_).append("&timestamp=").append(Statistics.currentTimestamp()).append("&sdk_version=").append("15.06").append("&crash=").append(CrashDetails.getCrashData(this.context_, error, Boolean.valueOf(nonfatal))).toString();
/*     */ 
/* 275 */     this.store_.addConnection(data);
/*     */ 
/* 277 */     tick();
/*     */   }
/*     */ 
/*     */   void recordEvents(String events)
/*     */   {
/* 286 */     checkInternalState();
/* 287 */     String data = new StringBuilder().append("deviceId=").append(this.deviceId_.getId()).append("&appKey=").append(this.appKey_).append("&timestamp=").append(Statistics.currentTimestamp()).append("&pushEvent=").append(events).toString();
/*     */ 
/* 292 */     this.store_.addConnection(data);
/*     */ 
/* 294 */     tick();
/*     */   }
/*     */ 
/*     */   void recordEvents(String key, String events) {
/* 298 */     checkInternalState();
/* 299 */     String data = new StringBuilder().append("deviceId=").append(this.deviceId_.getId()).append("&appKey=").append(this.appKey_).append("&timestamp=").append(Statistics.currentTimestamp()).append("&").append(key).append("=").append(events).toString();
/*     */ 
/* 304 */     this.store_.addConnection(data);
/*     */ 
/* 306 */     tick();
/*     */   }
/*     */ 
/*     */   void recordLocation(String events)
/*     */   {
/* 315 */     checkInternalState();
/* 316 */     String data = new StringBuilder().append("app_key=").append(this.appKey_).append("&timestamp=").append(Statistics.currentTimestamp()).append("&events=").append(events).toString();
/*     */ 
/* 320 */     this.store_.addConnection(data);
/*     */ 
/* 322 */     tick();
/*     */   }
/*     */ 
/*     */   void ensureExecutor()
/*     */   {
/* 329 */     if (this.executor_ == null)
/* 330 */       this.executor_ = Executors.newSingleThreadExecutor();
/*     */   }
/*     */ 
/*     */   void tick()
/*     */   {
/* 341 */     if ((!this.store_.isEmptyConnections()) && ((this.connectionProcessorFuture_ == null) || (this.connectionProcessorFuture_.isDone()))) {
/* 342 */       ensureExecutor();
/* 343 */       this.connectionProcessorFuture_ = this.executor_.submit(new ConnectionProcessor(this.serverURL_, this.store_, this.deviceId_, this.sslContext_));
/*     */     }
/*     */   }
/*     */ 
/*     */   ExecutorService getExecutor()
/*     */   {
/* 349 */     return this.executor_;
/*     */   }
/*     */   void setExecutor(ExecutorService executor) {
/* 352 */     this.executor_ = executor;
/*     */   }
/*     */   Future<?> getConnectionProcessorFuture() {
/* 355 */     return this.connectionProcessorFuture_;
/*     */   }
/*     */   void setConnectionProcessorFuture(Future<?> connectionProcessorFuture) {
/* 358 */     this.connectionProcessorFuture_ = connectionProcessorFuture;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.statistics.ConnectionQueue
 * JD-Core Version:    0.6.0
 */
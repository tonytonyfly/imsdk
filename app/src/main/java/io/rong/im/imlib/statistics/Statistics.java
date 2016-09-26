/*     */ package io.rong.imlib.statistics;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.util.Log;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class Statistics
/*     */ {
/*     */   public static final String COUNTLY_SDK_VERSION_STRING = "15.06";
/*     */   public static final String DEFAULT_APP_VERSION = "1.0";
/*     */   public static final String TAG = "Statistics";
/*     */   private static final int EVENT_QUEUE_SIZE_THRESHOLD = 10;
/*     */   private static final long TIMER_DELAY_IN_SECONDS = 3600L;
/*     */   protected static List<String> publicKeyPinCertificates;
/*     */   private ConnectionQueue connectionQueue_;
/*     */   private ScheduledExecutorService timerService_;
/*     */   private EventQueue eventQueue_;
/*     */   private DeviceId deviceId_Manager_;
/*     */   private long prevSessionDurationStartTime_;
/*     */   private int activityCount_;
/*     */   private boolean disableUpdateSessionRequests_;
/*     */   private boolean enableLogging_;
/*     */   private CountlyMessagingMode messagingMode_;
/*     */   private Context context_;
/*     */ 
/*     */   public static Statistics sharedInstance()
/*     */   {
/* 105 */     return SingletonHolder.instance;
/*     */   }
/*     */ 
/*     */   Statistics()
/*     */   {
/* 113 */     this.connectionQueue_ = new ConnectionQueue();
/*     */   }
/*     */ 
/*     */   public Statistics init(Context context, String serverURL, String appKey)
/*     */   {
/* 139 */     return init(context, serverURL, appKey, null, OpenUDIDAdapter.isOpenUDIDAvailable() ? DeviceId.Type.OPEN_UDID : DeviceId.Type.ADVERTISING_ID);
/*     */   }
/*     */ 
/*     */   public Statistics init(Context context, String serverURL, String appKey, String deviceID)
/*     */   {
/* 154 */     return init(context, serverURL, appKey, deviceID, null);
/*     */   }
/*     */ 
/*     */   public synchronized Statistics init(Context context, String serverURL, String appKey, String deviceID, DeviceId.Type idMode)
/*     */   {
/* 170 */     if (context == null) {
/* 171 */       throw new IllegalArgumentException("valid context is required");
/*     */     }
/* 173 */     if (!isValidURL(serverURL)) {
/* 174 */       throw new IllegalArgumentException("valid serverURL is required");
/*     */     }
/* 176 */     if ((appKey == null) || (appKey.length() == 0)) {
/* 177 */       throw new IllegalArgumentException("valid appKey is required");
/*     */     }
/* 179 */     if ((deviceID != null) && (deviceID.length() == 0)) {
/* 180 */       throw new IllegalArgumentException("valid deviceID is required");
/*     */     }
/* 182 */     if ((deviceID == null) && (idMode == null)) {
/* 183 */       if (OpenUDIDAdapter.isOpenUDIDAvailable()) idMode = DeviceId.Type.OPEN_UDID;
/* 184 */       else if (AdvertisingIdAdapter.isAdvertisingIdAvailable()) idMode = DeviceId.Type.ADVERTISING_ID;
/*     */     }
/* 186 */     if ((deviceID == null) && (idMode == DeviceId.Type.OPEN_UDID) && (!OpenUDIDAdapter.isOpenUDIDAvailable())) {
/* 187 */       throw new IllegalArgumentException("valid deviceID is required because OpenUDID is not available");
/*     */     }
/* 189 */     if ((deviceID == null) && (idMode == DeviceId.Type.ADVERTISING_ID) && (!AdvertisingIdAdapter.isAdvertisingIdAvailable())) {
/* 190 */       throw new IllegalArgumentException("valid deviceID is required because Advertising ID is not available (you need to include Google Play services 4.0+ into your project)");
/*     */     }
/* 192 */     if ((this.eventQueue_ != null) && ((!this.connectionQueue_.getServerURL().equals(serverURL)) || (!this.connectionQueue_.getAppKey().equals(appKey)) || (!DeviceId.deviceIDEqualsNullSafe(deviceID, idMode, this.connectionQueue_.getDeviceId()))))
/*     */     {
/* 195 */       throw new IllegalStateException("Statistics cannot be reinitialized with different values");
/*     */     }
/*     */ 
/* 200 */     if (MessagingAdapter.isMessagingAvailable()) {
/* 201 */       MessagingAdapter.storeConfiguration(context, serverURL, appKey, deviceID, idMode);
/*     */     }
/*     */ 
/* 206 */     if (this.eventQueue_ == null)
/*     */     {
/*     */       DeviceId deviceIdInstance;
/*     */       DeviceId deviceIdInstance;
/* 208 */       if (deviceID != null)
/* 209 */         deviceIdInstance = new DeviceId(deviceID);
/*     */       else {
/* 211 */         deviceIdInstance = new DeviceId(idMode);
/*     */       }
/*     */ 
/* 214 */       StatisticsStore statisticsStore = new StatisticsStore(context);
/*     */ 
/* 216 */       deviceIdInstance.init(context, statisticsStore, true);
/*     */ 
/* 218 */       this.connectionQueue_.setServerURL(serverURL);
/* 219 */       this.connectionQueue_.setAppKey(appKey);
/* 220 */       this.connectionQueue_.setCountlyStore(statisticsStore);
/* 221 */       this.connectionQueue_.setDeviceId(deviceIdInstance);
/*     */ 
/* 223 */       this.eventQueue_ = new EventQueue(statisticsStore);
/*     */     }
/*     */ 
/* 226 */     this.context_ = context;
/*     */ 
/* 229 */     this.connectionQueue_.setContext(context);
/*     */ 
/* 231 */     return this;
/*     */   }
/*     */ 
/*     */   public synchronized boolean isInitialized()
/*     */   {
/* 239 */     return this.eventQueue_ != null;
/*     */   }
/*     */ 
/*     */   public synchronized void halt()
/*     */   {
/* 250 */     this.eventQueue_ = null;
/* 251 */     StatisticsStore statisticsStore = this.connectionQueue_.getCountlyStore();
/* 252 */     if (statisticsStore != null) {
/* 253 */       statisticsStore.clear();
/*     */     }
/* 255 */     this.connectionQueue_.setContext(null);
/* 256 */     this.connectionQueue_.setServerURL(null);
/* 257 */     this.connectionQueue_.setAppKey(null);
/* 258 */     this.connectionQueue_.setCountlyStore(null);
/* 259 */     this.prevSessionDurationStartTime_ = 0L;
/* 260 */     this.activityCount_ = 0;
/*     */   }
/*     */ 
/*     */   public synchronized void onStart()
/*     */   {
/* 271 */     if (this.eventQueue_ == null) {
/* 272 */       throw new IllegalStateException("init must be called before onStart");
/*     */     }
/*     */ 
/* 275 */     this.activityCount_ += 1;
/* 276 */     if (this.activityCount_ == 1) {
/* 277 */       onStartHelper();
/*     */     }
/*     */ 
/* 281 */     String referrer = ReferrerReceiver.getReferrer(this.context_);
/* 282 */     if (sharedInstance().isLoggingEnabled()) {
/* 283 */       Log.d("Statistics", new StringBuilder().append("Checking referrer: ").append(referrer).toString());
/*     */     }
/* 285 */     if (referrer != null) {
/* 286 */       this.connectionQueue_.sendReferrerData(referrer);
/* 287 */       ReferrerReceiver.deleteReferrer(this.context_);
/*     */     }
/*     */ 
/* 290 */     CrashDetails.inForeground();
/*     */   }
/*     */ 
/*     */   void onStartHelper()
/*     */   {
/* 298 */     this.prevSessionDurationStartTime_ = System.nanoTime();
/* 299 */     this.connectionQueue_.beginSession();
/*     */   }
/*     */ 
/*     */   public synchronized void onStop()
/*     */   {
/* 311 */     if (this.eventQueue_ == null) {
/* 312 */       throw new IllegalStateException("init must be called before onStop");
/*     */     }
/* 314 */     if (this.activityCount_ == 0) {
/* 315 */       throw new IllegalStateException("must call onStart before onStop");
/*     */     }
/*     */ 
/* 318 */     this.activityCount_ -= 1;
/* 319 */     if (this.activityCount_ == 0) {
/* 320 */       onStopHelper();
/*     */     }
/*     */ 
/* 323 */     CrashDetails.inBackground();
/*     */   }
/*     */ 
/*     */   void onStopHelper()
/*     */   {
/* 331 */     this.connectionQueue_.endSession(roundedSecondsSinceLastSessionDurationUpdate());
/* 332 */     this.prevSessionDurationStartTime_ = 0L;
/*     */ 
/* 334 */     if (this.eventQueue_.size() > 0)
/* 335 */       this.connectionQueue_.recordEvents(this.eventQueue_.events());
/*     */   }
/*     */ 
/*     */   public void onRegistrationId(String registrationId)
/*     */   {
/* 343 */     this.connectionQueue_.tokenSession(registrationId, this.messagingMode_);
/*     */   }
/*     */ 
/*     */   public void recordEvent(String key)
/*     */   {
/* 353 */     recordEvent(key, null, 1, 0.0D);
/*     */   }
/*     */ 
/*     */   public void recordEvent(String key, int count)
/*     */   {
/* 364 */     recordEvent(key, null, count, 0.0D);
/*     */   }
/*     */ 
/*     */   public void recordEvent(String key, int count, double sum)
/*     */   {
/* 376 */     recordEvent(key, null, count, sum);
/*     */   }
/*     */ 
/*     */   public void recordEvent(String key, Map<String, String> segmentation, int count)
/*     */   {
/* 388 */     recordEvent(key, segmentation, count, 0.0D);
/*     */   }
/*     */ 
/*     */   public synchronized void recordEvent(String key, Map<String, String> segmentation, int count, double sum)
/*     */   {
/* 402 */     if (!isInitialized()) {
/* 403 */       throw new IllegalStateException("Statistics.sharedInstance().init must be called before recordEvent");
/*     */     }
/* 405 */     if ((key == null) || (key.length() == 0)) {
/* 406 */       throw new IllegalArgumentException("Valid Statistics event key is required");
/*     */     }
/* 408 */     if (count < 1) {
/* 409 */       throw new IllegalArgumentException("Statistics event count should be greater than zero");
/*     */     }
/* 411 */     if (segmentation != null) {
/* 412 */       for (String k : segmentation.keySet()) {
/* 413 */         if ((k == null) || (k.length() == 0)) {
/* 414 */           throw new IllegalArgumentException("Statistics event segmentation key cannot be null or empty");
/*     */         }
/* 416 */         if ((segmentation.get(k) == null) || (((String)segmentation.get(k)).length() == 0)) {
/* 417 */           throw new IllegalArgumentException("Statistics event segmentation value cannot be null or empty");
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 422 */     this.eventQueue_.recordEvent(key, segmentation, count, sum);
/* 423 */     sendEventsIfNeeded();
/*     */   }
/*     */ 
/*     */   public synchronized void recordEvent(String key, Map<String, String> segmentation)
/*     */   {
/* 435 */     String[] data = new String[segmentation.size() * 2];
/* 436 */     int i = 0;
/* 437 */     if (!isInitialized()) {
/* 438 */       throw new IllegalStateException("Countly.sharedInstance().init must be called before recordEvent");
/*     */     }
/* 440 */     if ((key == null) || (key.length() == 0)) {
/* 441 */       throw new IllegalArgumentException("Valid Countly event key is required");
/*     */     }
/*     */ 
/* 444 */     if (segmentation != null) {
/* 445 */       for (String k : segmentation.keySet()) {
/* 446 */         if ((k == null) || (k.length() == 0)) {
/* 447 */           throw new IllegalArgumentException("Countly event segmentation key cannot be null or empty");
/*     */         }
/* 449 */         if ((segmentation.get(k) == null) || (((String)segmentation.get(k)).length() == 0)) {
/* 450 */           throw new IllegalArgumentException("Countly event segmentation value cannot be null or empty");
/*     */         }
/* 452 */         data[i] = k;
/* 453 */         i++; data[i] = ((String)segmentation.get(k));
/* 454 */         i++;
/*     */       }
/*     */     }
/*     */ 
/* 458 */     JSONObject json = new JSONObject();
/*     */ 
/* 460 */     DeviceInfo.fillJSONIfValuesNotEmpty(json, data);
/* 461 */     String result = json.toString();
/*     */     try {
/* 463 */       result = URLEncoder.encode(result, "UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException ignored) {
/*     */     }
/* 467 */     this.connectionQueue_.recordEvents(key, result);
/*     */   }
/*     */ 
/*     */   public synchronized Statistics setUserData(Map<String, String> data)
/*     */   {
/* 504 */     return setUserData(data, null);
/*     */   }
/*     */ 
/*     */   public synchronized Statistics setUserData(Map<String, String> data, Map<String, String> customdata)
/*     */   {
/* 544 */     UserData.setData(data);
/* 545 */     if (customdata != null)
/* 546 */       UserData.setCustomData(customdata);
/* 547 */     this.connectionQueue_.sendUserData();
/* 548 */     return this;
/*     */   }
/*     */ 
/*     */   public synchronized Statistics setCustomUserData(Map<String, String> customdata)
/*     */   {
/* 557 */     if (customdata != null)
/* 558 */       UserData.setCustomData(customdata);
/* 559 */     this.connectionQueue_.sendUserData();
/* 560 */     return this;
/*     */   }
/*     */ 
/*     */   public synchronized Statistics setLocation(double lat, double lon)
/*     */   {
/* 574 */     this.connectionQueue_.getCountlyStore().setLocation(lat, lon);
/*     */ 
/* 576 */     if (this.disableUpdateSessionRequests_) {
/* 577 */       this.connectionQueue_.updateSession(roundedSecondsSinceLastSessionDurationUpdate());
/*     */     }
/*     */ 
/* 580 */     return this;
/*     */   }
/*     */ 
/*     */   public synchronized Statistics setCustomCrashSegments(Map<String, String> segments)
/*     */   {
/* 589 */     if (segments != null)
/* 590 */       CrashDetails.setCustomSegments(segments);
/* 591 */     return this;
/*     */   }
/*     */ 
/*     */   public synchronized Statistics addCrashLog(String record)
/*     */   {
/* 599 */     CrashDetails.addLog(record);
/* 600 */     return this;
/*     */   }
/*     */ 
/*     */   public synchronized Statistics logException(Exception exception)
/*     */   {
/* 608 */     StringWriter sw = new StringWriter();
/* 609 */     PrintWriter pw = new PrintWriter(sw);
/* 610 */     exception.printStackTrace(pw);
/* 611 */     this.connectionQueue_.sendCrashReport(sw.toString(), true);
/* 612 */     return this;
/*     */   }
/*     */ 
/*     */   public synchronized Statistics enableCrashReporting()
/*     */   {
/* 620 */     Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
/*     */ 
/* 622 */     Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler(oldHandler)
/*     */     {
/*     */       public void uncaughtException(Thread t, Throwable e)
/*     */       {
/* 626 */         StringWriter sw = new StringWriter();
/* 627 */         PrintWriter pw = new PrintWriter(sw);
/* 628 */         e.printStackTrace(pw);
/* 629 */         Statistics.this.connectionQueue_.sendCrashReport(sw.toString(), false);
/*     */ 
/* 632 */         if (this.val$oldHandler != null)
/*     */         {
/* 634 */           this.val$oldHandler.uncaughtException(t, e);
/*     */         }
/*     */       }
/*     */     };
/* 639 */     Thread.setDefaultUncaughtExceptionHandler(handler);
/* 640 */     return this;
/*     */   }
/*     */ 
/*     */   public synchronized Statistics setDisableUpdateSessionRequests(boolean disable)
/*     */   {
/* 652 */     this.disableUpdateSessionRequests_ = disable;
/* 653 */     return this;
/*     */   }
/*     */ 
/*     */   public synchronized Statistics setLoggingEnabled(boolean enableLogging)
/*     */   {
/* 662 */     this.enableLogging_ = enableLogging;
/* 663 */     return this;
/*     */   }
/*     */ 
/*     */   public synchronized boolean isLoggingEnabled() {
/* 667 */     return this.enableLogging_;
/*     */   }
/*     */ 
/*     */   void sendEventsIfNeeded()
/*     */   {
/* 674 */     Log.d("Statistics", new StringBuilder().append("sendEventsIfNeeded: queue=").append(this.eventQueue_.size()).toString());
/* 675 */     if (this.eventQueue_.size() >= 10)
/* 676 */       this.connectionQueue_.recordEvents(this.eventQueue_.events());
/*     */   }
/*     */ 
/*     */   synchronized void onTimer()
/*     */   {
/* 685 */     boolean hasActiveSession = this.activityCount_ > 0;
/* 686 */     if (hasActiveSession) {
/* 687 */       Log.d("Statistics", new StringBuilder().append("onTimer: update=").append(!this.disableUpdateSessionRequests_).append(", queue=").append(this.eventQueue_.size()).toString());
/* 688 */       if (!this.disableUpdateSessionRequests_) {
/* 689 */         this.connectionQueue_.updateSession(roundedSecondsSinceLastSessionDurationUpdate());
/*     */       }
/* 691 */       if (this.eventQueue_.size() > 0)
/* 692 */         this.connectionQueue_.recordEvents(this.eventQueue_.events());
/*     */     }
/*     */   }
/*     */ 
/*     */   int roundedSecondsSinceLastSessionDurationUpdate()
/*     */   {
/* 701 */     long currentTimestampInNanoseconds = System.nanoTime();
/* 702 */     long unsentSessionLengthInNanoseconds = currentTimestampInNanoseconds - this.prevSessionDurationStartTime_;
/* 703 */     this.prevSessionDurationStartTime_ = currentTimestampInNanoseconds;
/* 704 */     return (int)Math.round(unsentSessionLengthInNanoseconds / 1000000000.0D);
/*     */   }
/*     */ 
/*     */   static int currentTimestamp()
/*     */   {
/* 711 */     return (int)(System.currentTimeMillis() / 1000L);
/*     */   }
/*     */ 
/*     */   static boolean isValidURL(String urlStr)
/*     */   {
/* 718 */     boolean validURL = false;
/* 719 */     if ((urlStr != null) && (urlStr.length() > 0)) {
/*     */       try {
/* 721 */         new URL(urlStr);
/* 722 */         validURL = true;
/*     */       }
/*     */       catch (MalformedURLException e) {
/* 725 */         validURL = false;
/*     */       }
/*     */     }
/* 728 */     return validURL;
/*     */   }
/*     */ 
/*     */   public static Statistics enablePublicKeyPinning(List<String> certificates)
/*     */   {
/* 740 */     publicKeyPinCertificates = certificates;
/* 741 */     return sharedInstance();
/*     */   }
/*     */ 
/*     */   ConnectionQueue getConnectionQueue()
/*     */   {
/* 746 */     return this.connectionQueue_;
/*     */   }
/*     */   void setConnectionQueue(ConnectionQueue connectionQueue) {
/* 749 */     this.connectionQueue_ = connectionQueue;
/*     */   }
/*     */   ExecutorService getTimerService() {
/* 752 */     return this.timerService_;
/*     */   }
/*     */   EventQueue getEventQueue() {
/* 755 */     return this.eventQueue_;
/*     */   }
/*     */   void setEventQueue(EventQueue eventQueue) {
/* 758 */     this.eventQueue_ = eventQueue;
/*     */   }
/*     */   long getPrevSessionDurationStartTime() {
/* 761 */     return this.prevSessionDurationStartTime_;
/*     */   }
/*     */   void setPrevSessionDurationStartTime(long prevSessionDurationStartTime) {
/* 764 */     this.prevSessionDurationStartTime_ = prevSessionDurationStartTime;
/*     */   }
/*     */   int getActivityCount() {
/* 767 */     return this.activityCount_;
/*     */   }
/*     */   boolean getDisableUpdateSessionRequests() {
/* 770 */     return this.disableUpdateSessionRequests_;
/*     */   }
/*     */ 
/*     */   private static class SingletonHolder
/*     */   {
/*  85 */     static final Statistics instance = new Statistics();
/*     */   }
/*     */ 
/*     */   public static enum CountlyMessagingMode
/*     */   {
/*  79 */     TEST, 
/*  80 */     PRODUCTION;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.statistics.Statistics
 * JD-Core Version:    0.6.0
 */
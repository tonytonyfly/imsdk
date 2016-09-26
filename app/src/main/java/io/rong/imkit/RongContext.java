/*     */ package io.rong.imkit;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.ContextWrapper;
/*     */ import android.content.SharedPreferences;
/*     */ import android.os.Handler;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imageloader.cache.disc.impl.ext.LruDiskCache;
/*     */ import io.rong.imageloader.cache.disc.naming.Md5FileNameGenerator;
/*     */ import io.rong.imageloader.cache.memory.impl.LruMemoryCache;
/*     */ import io.rong.imageloader.core.DisplayImageOptions;
/*     */ import io.rong.imageloader.core.ImageLoader;
/*     */ import io.rong.imageloader.core.ImageLoaderConfiguration;
/*     */ import io.rong.imageloader.core.ImageLoaderConfiguration.Builder;
/*     */ import io.rong.imageloader.utils.L;
/*     */ import io.rong.imageloader.utils.StorageUtils;
/*     */ import io.rong.imkit.cache.RongCache;
/*     */ import io.rong.imkit.cache.RongCacheWrap;
/*     */ import io.rong.imkit.model.ConversationInfo;
/*     */ import io.rong.imkit.model.ConversationKey;
/*     */ import io.rong.imkit.model.ConversationProviderTag;
/*     */ import io.rong.imkit.model.Event.ConversationNotificationEvent;
/*     */ import io.rong.imkit.model.GroupUserInfo;
/*     */ import io.rong.imkit.model.ProviderTag;
/*     */ import io.rong.imkit.notification.MessageCounter;
/*     */ import io.rong.imkit.notification.MessageSounder;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.utils.AndroidEmoji;
/*     */ import io.rong.imkit.utils.RongAuthImageDownloader;
/*     */ import io.rong.imkit.utils.StringUtils;
/*     */ import io.rong.imkit.widget.provider.AppServiceConversationProvider;
/*     */ import io.rong.imkit.widget.provider.CustomerServiceConversationProvider;
/*     */ import io.rong.imkit.widget.provider.DiscussionConversationProvider;
/*     */ import io.rong.imkit.widget.provider.EvaluateTextMessageItemProvider;
/*     */ import io.rong.imkit.widget.provider.GroupConversationProvider;
/*     */ import io.rong.imkit.widget.provider.IContainerItemProvider.ConversationProvider;
/*     */ import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
/*     */ import io.rong.imkit.widget.provider.ImageInputProvider;
/*     */ import io.rong.imkit.widget.provider.InputProvider.ExtendProvider;
/*     */ import io.rong.imkit.widget.provider.InputProvider.MainInputProvider;
/*     */ import io.rong.imkit.widget.provider.LocationInputProvider;
/*     */ import io.rong.imkit.widget.provider.PrivateConversationProvider;
/*     */ import io.rong.imkit.widget.provider.PublicServiceConversationProvider;
/*     */ import io.rong.imkit.widget.provider.PublicServiceMenuInputProvider;
/*     */ import io.rong.imkit.widget.provider.SystemConversationProvider;
/*     */ import io.rong.imkit.widget.provider.TextInputProvider;
/*     */ import io.rong.imkit.widget.provider.VoiceInputProvider;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.model.Conversation.ConversationNotificationStatus;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Conversation.PublicServiceType;
/*     */ import io.rong.imlib.model.Discussion;
/*     */ import io.rong.imlib.model.Group;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import io.rong.imlib.model.PublicServiceProfile;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Vector;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ 
/*     */ public class RongContext extends ContextWrapper
/*     */ {
/*     */   private static final String TAG = "RongContext";
/*     */   private static RongContext sContext;
/*     */   private EventBus mBus;
/*     */   private ExecutorService executorService;
/*     */   private RongIM.ConversationBehaviorListener mConversationBehaviorListener;
/*     */   private RongIM.ConversationListBehaviorListener mConversationListBehaviorListener;
/*     */   private RongIM.PublicServiceBehaviorListener mPublicServiceBehaviorListener;
/*     */   private RongIM.OnSelectMemberListener mMemberSelectListener;
/*     */   private RongIM.OnSendMessageListener mOnSendMessageListener;
/*     */   private RongIM.RequestPermissionsListener mRequestPermissionsListener;
/*     */   private IPublicServiceMenuClickListener mPublicServiceMenuClickListener;
/*     */   private RongIM.UserInfoProvider mUserInfoProvider;
/*     */   private RongIM.GroupInfoProvider mGroupProvider;
/*     */   private RongIM.GroupUserInfoProvider mGroupUserInfoProvider;
/*     */   private Map<Class<? extends MessageContent>, IContainerItemProvider.MessageProvider> mTemplateMap;
/*     */   private IContainerItemProvider.MessageProvider mDefaultTemplate;
/*     */   private Map<Class<? extends MessageContent>, ProviderTag> mProviderMap;
/*     */   private Map<String, IContainerItemProvider.ConversationProvider> mConversationProviderMap;
/*     */   private Map<String, ConversationProviderTag> mConversationTagMap;
/*     */   private Map<String, Boolean> mConversationTypeStateMap;
/*     */   private RongCache<String, Conversation.ConversationNotificationStatus> mNotificationCache;
/*     */   private InputProvider.MainInputProvider mPrimaryProvider;
/*     */   private InputProvider.MainInputProvider mSecondaryProvider;
/*     */   private InputProvider.MainInputProvider mMenuProvider;
/*     */   private List<Conversation.ConversationType> mReadReceiptConversationTypeList;
/*     */   private RongIM.LocationProvider mLocationProvider;
/*     */   private MessageCounter mCounterLogic;
/*     */   private List<String> mCurrentConversationList;
/*     */   private Map<Conversation.ConversationType, List<InputProvider.ExtendProvider>> mExtendProvider;
/*     */   VoiceInputProvider mVoiceInputProvider;
/*     */   ImageInputProvider mImageInputProvider;
/*     */   LocationInputProvider mLocationInputProvider;
/*     */   InputProvider.ExtendProvider mVoIPInputProvider;
/*     */   Handler mHandler;
/*     */   private UserInfo mCurrentUserInfo;
/*     */   private boolean isUserInfoAttached;
/*     */   private boolean isShowUnreadMessageState;
/*     */   private boolean isShowNewMessageState;
/*     */   private EvaluateTextMessageItemProvider evaluateTextMessageItemProvider;
/*     */ 
/*     */   public static void init(Context context)
/*     */   {
/* 124 */     if (sContext == null) {
/* 125 */       sContext = new RongContext(context);
/* 126 */       sContext.initRegister();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static RongContext getInstance()
/*     */   {
/* 132 */     return sContext;
/*     */   }
/*     */ 
/*     */   protected RongContext(Context base) {
/* 136 */     super(base);
/*     */ 
/* 138 */     this.mBus = EventBus.getDefault();
/* 139 */     this.mHandler = new Handler(getMainLooper());
/*     */ 
/* 141 */     this.mTemplateMap = new HashMap();
/*     */ 
/* 143 */     this.mProviderMap = new HashMap();
/*     */ 
/* 145 */     this.mConversationProviderMap = new HashMap();
/*     */ 
/* 147 */     this.mConversationTagMap = new HashMap();
/*     */ 
/* 149 */     this.mConversationTypeStateMap = new HashMap();
/*     */ 
/* 151 */     this.mCounterLogic = new MessageCounter(this);
/*     */ 
/* 153 */     this.mCurrentConversationList = new ArrayList();
/*     */ 
/* 155 */     this.mReadReceiptConversationTypeList = new ArrayList();
/* 156 */     this.mReadReceiptConversationTypeList.add(Conversation.ConversationType.PRIVATE);
/*     */ 
/* 158 */     this.mExtendProvider = new HashMap();
/* 159 */     initCache();
/*     */ 
/* 162 */     this.executorService = Executors.newSingleThreadExecutor();
/*     */ 
/* 164 */     AndroidEmoji.init(getApplicationContext());
/*     */ 
/* 166 */     RongNotificationManager.getInstance().init(this);
/*     */ 
/* 168 */     MessageSounder.init(getApplicationContext());
/*     */ 
/* 170 */     ImageLoader.getInstance().init(getDefaultConfig(getApplicationContext()));
/*     */   }
/* 174 */   private ImageLoaderConfiguration getDefaultConfig(Context context) { int MAX_CACHE_MEMORY_SIZE = (int)(Runtime.getRuntime().maxMemory() / 8L);
/* 175 */     File cacheDir = StorageUtils.getOwnCacheDirectory(context, context.getPackageName() + "/cache/image/");
/*     */     ImageLoaderConfiguration config;
/*     */     try {
/* 179 */       config = new ImageLoaderConfiguration.Builder(context).threadPoolSize(3).threadPriority(3).denyCacheImageMultipleSizesInMemory().memoryCache(new LruMemoryCache(MAX_CACHE_MEMORY_SIZE)).diskCache(new LruDiskCache(cacheDir, new Md5FileNameGenerator(), 0L)).imageDownloader(new RongAuthImageDownloader(this)).defaultDisplayImageOptions(DisplayImageOptions.createSimple()).build();
/*     */ 
/* 189 */       L.writeLogs(false);
/* 190 */       return config;
/*     */     }
/*     */     catch (IOException e) {
/* 193 */       RLog.i("RongContext", "Use default ImageLoader config.");
/* 194 */       config = ImageLoaderConfiguration.createDefault(context);
/*     */     }
/* 196 */     return config;
/*     */   }
/*     */ 
/*     */   private void initRegister()
/*     */   {
/* 201 */     registerDefaultConversationGatherState();
/* 202 */     registerConversationTemplate(new PrivateConversationProvider());
/* 203 */     registerConversationTemplate(new GroupConversationProvider());
/* 204 */     registerConversationTemplate(new DiscussionConversationProvider());
/* 205 */     registerConversationTemplate(new SystemConversationProvider());
/* 206 */     registerConversationTemplate(new CustomerServiceConversationProvider());
/* 207 */     registerConversationTemplate(new AppServiceConversationProvider());
/* 208 */     registerConversationTemplate(new PublicServiceConversationProvider());
/*     */ 
/* 210 */     this.mVoiceInputProvider = new VoiceInputProvider(sContext);
/* 211 */     this.mImageInputProvider = new ImageInputProvider(sContext);
/* 212 */     this.mLocationInputProvider = new LocationInputProvider(sContext);
/*     */ 
/* 214 */     setPrimaryInputProvider(new TextInputProvider(sContext));
/* 215 */     setSecondaryInputProvider(this.mVoiceInputProvider);
/* 216 */     setMenuInputProvider(new PublicServiceMenuInputProvider(sContext));
/*     */ 
/* 218 */     List privateProvider = new ArrayList();
/*     */ 
/* 220 */     privateProvider.add(this.mImageInputProvider);
/* 221 */     privateProvider.add(this.mLocationInputProvider);
/*     */ 
/* 223 */     List chatRoomProvider = new ArrayList();
/* 224 */     chatRoomProvider.add(this.mImageInputProvider);
/* 225 */     chatRoomProvider.add(this.mLocationInputProvider);
/*     */ 
/* 227 */     List groupProvider = new ArrayList();
/* 228 */     groupProvider.add(this.mImageInputProvider);
/* 229 */     groupProvider.add(this.mLocationInputProvider);
/*     */ 
/* 231 */     List customerProvider = new ArrayList();
/* 232 */     customerProvider.add(this.mImageInputProvider);
/* 233 */     customerProvider.add(this.mLocationInputProvider);
/*     */ 
/* 235 */     List discussionProvider = new ArrayList();
/* 236 */     discussionProvider.add(this.mImageInputProvider);
/* 237 */     discussionProvider.add(this.mLocationInputProvider);
/*     */ 
/* 239 */     List publicProvider = new ArrayList();
/* 240 */     publicProvider.add(this.mImageInputProvider);
/* 241 */     publicProvider.add(this.mLocationInputProvider);
/*     */ 
/* 243 */     List publicAppProvider = new ArrayList();
/* 244 */     publicAppProvider.add(this.mImageInputProvider);
/* 245 */     publicAppProvider.add(this.mLocationInputProvider);
/*     */ 
/* 247 */     List systemProvider = new ArrayList();
/* 248 */     systemProvider.add(this.mImageInputProvider);
/* 249 */     systemProvider.add(this.mLocationInputProvider);
/*     */ 
/* 251 */     this.mExtendProvider.put(Conversation.ConversationType.PRIVATE, privateProvider);
/* 252 */     this.mExtendProvider.put(Conversation.ConversationType.CHATROOM, chatRoomProvider);
/* 253 */     this.mExtendProvider.put(Conversation.ConversationType.GROUP, groupProvider);
/* 254 */     this.mExtendProvider.put(Conversation.ConversationType.CUSTOMER_SERVICE, customerProvider);
/* 255 */     this.mExtendProvider.put(Conversation.ConversationType.DISCUSSION, discussionProvider);
/* 256 */     this.mExtendProvider.put(Conversation.ConversationType.APP_PUBLIC_SERVICE, publicAppProvider);
/* 257 */     this.mExtendProvider.put(Conversation.ConversationType.PUBLIC_SERVICE, publicProvider);
/* 258 */     this.mExtendProvider.put(Conversation.ConversationType.SYSTEM, systemProvider);
/*     */   }
/*     */ 
/*     */   public VoiceInputProvider getVoiceInputProvider() {
/* 262 */     return this.mVoiceInputProvider;
/*     */   }
/*     */ 
/*     */   public ImageInputProvider getImageInputProvider() {
/* 266 */     return this.mImageInputProvider;
/*     */   }
/*     */ 
/*     */   public LocationInputProvider getLocationInputProvider() {
/* 270 */     return this.mLocationInputProvider;
/*     */   }
/*     */ 
/*     */   public InputProvider.ExtendProvider getVoIPInputProvider() {
/* 274 */     return this.mVoIPInputProvider;
/*     */   }
/*     */ 
/*     */   private void initCache()
/*     */   {
/* 279 */     this.mNotificationCache = new RongCacheWrap(this, 16) {
/* 280 */       Vector<String> mRequests = new Vector();
/* 281 */       Conversation.ConversationNotificationStatus notificationStatus = null;
/*     */ 
/*     */       public Conversation.ConversationNotificationStatus obtainValue(String key)
/*     */       {
/* 286 */         if (TextUtils.isEmpty(key)) {
/* 287 */           return null;
/*     */         }
/* 289 */         synchronized (this.mRequests) {
/* 290 */           if (this.mRequests.contains(key))
/* 291 */             return null;
/* 292 */           this.mRequests.add(key);
/*     */         }
/*     */ 
/* 295 */         RongContext.this.mHandler.post(new Runnable(key)
/*     */         {
/*     */           public void run()
/*     */           {
/* 300 */             ConversationKey conversationKey = ConversationKey.obtain(this.val$key);
/*     */ 
/* 302 */             if (conversationKey != null)
/*     */             {
/* 304 */               RongIM.getInstance().getConversationNotificationStatus(conversationKey.getType(), conversationKey.getTargetId(), new RongIMClient.ResultCallback(conversationKey)
/*     */               {
/*     */                 public void onSuccess(Conversation.ConversationNotificationStatus status)
/*     */                 {
/* 309 */                   RongContext.1.this.mRequests.remove(RongContext.1.1.this.val$key);
/* 310 */                   RongContext.1.this.put(RongContext.1.1.this.val$key, status);
/* 311 */                   RongContext.1.this.getContext().getEventBus().post(new Event.ConversationNotificationEvent(this.val$conversationKey.getTargetId(), this.val$conversationKey.getType(), RongContext.1.this.notificationStatus));
/*     */                 }
/*     */ 
/*     */                 public void onError(RongIMClient.ErrorCode errorCode)
/*     */                 {
/* 317 */                   RongContext.1.this.mRequests.remove(RongContext.1.1.this.val$key);
/*     */                 }
/*     */               });
/*     */             }
/*     */           }
/*     */         });
/* 325 */         return this.notificationStatus;
/*     */       } } ;
/*     */   }
/*     */ 
/*     */   public List<ConversationInfo> getCurrentConversationList() {
/* 331 */     ArrayList infos = new ArrayList();
/* 332 */     int size = this.mCurrentConversationList.size();
/* 333 */     if (size > 0) {
/* 334 */       for (int i = 0; i < size; i++) {
/* 335 */         ConversationKey key = ConversationKey.obtain((String)this.mCurrentConversationList.get(i));
/* 336 */         ConversationInfo info = ConversationInfo.obtain(key.getType(), key.getTargetId());
/* 337 */         infos.add(info);
/*     */       }
/*     */     }
/* 340 */     return infos;
/*     */   }
/*     */ 
/*     */   public EventBus getEventBus() {
/* 344 */     return this.mBus;
/*     */   }
/*     */ 
/*     */   public MessageCounter getMessageCounterLogic() {
/* 348 */     return this.mCounterLogic;
/*     */   }
/*     */ 
/*     */   public void registerConversationTemplate(IContainerItemProvider.ConversationProvider provider) {
/* 352 */     ConversationProviderTag tag = (ConversationProviderTag)provider.getClass().getAnnotation(ConversationProviderTag.class);
/* 353 */     if (tag == null)
/* 354 */       throw new RuntimeException("No ConversationProviderTag added with your provider!");
/* 355 */     this.mConversationProviderMap.put(tag.conversationType(), provider);
/* 356 */     this.mConversationTagMap.put(tag.conversationType(), tag);
/*     */   }
/*     */ 
/*     */   public IContainerItemProvider.ConversationProvider getConversationTemplate(String conversationType) {
/* 360 */     return (IContainerItemProvider.ConversationProvider)this.mConversationProviderMap.get(conversationType);
/*     */   }
/*     */ 
/*     */   public ConversationProviderTag getConversationProviderTag(String conversationType) {
/* 364 */     if (!this.mConversationProviderMap.containsKey(conversationType)) {
/* 365 */       throw new RuntimeException("the conversation type hasn't been registered!");
/*     */     }
/* 367 */     return (ConversationProviderTag)this.mConversationTagMap.get(conversationType);
/*     */   }
/*     */ 
/*     */   public void registerDefaultConversationGatherState() {
/* 371 */     setConversationGatherState(Conversation.ConversationType.PRIVATE.getName(), Boolean.valueOf(false));
/* 372 */     setConversationGatherState(Conversation.ConversationType.GROUP.getName(), Boolean.valueOf(true));
/* 373 */     setConversationGatherState(Conversation.ConversationType.DISCUSSION.getName(), Boolean.valueOf(false));
/* 374 */     setConversationGatherState(Conversation.ConversationType.CHATROOM.getName(), Boolean.valueOf(false));
/* 375 */     setConversationGatherState(Conversation.ConversationType.CUSTOMER_SERVICE.getName(), Boolean.valueOf(false));
/* 376 */     setConversationGatherState(Conversation.ConversationType.SYSTEM.getName(), Boolean.valueOf(true));
/* 377 */     setConversationGatherState(Conversation.PublicServiceType.APP_PUBLIC_SERVICE.getName(), Boolean.valueOf(false));
/* 378 */     setConversationGatherState(Conversation.ConversationType.PUBLIC_SERVICE.getName(), Boolean.valueOf(false));
/*     */   }
/*     */ 
/*     */   public void setConversationGatherState(String type, Boolean state)
/*     */   {
/* 383 */     if (type == null)
/* 384 */       throw new IllegalArgumentException("The name of the register conversation type can't be null");
/* 385 */     this.mConversationTypeStateMap.put(type, state);
/*     */   }
/*     */ 
/*     */   public Boolean getConversationGatherState(String type) {
/* 389 */     if (!this.mConversationTypeStateMap.containsKey(type)) {
/* 390 */       RLog.e("RongContext", "getConversationGatherState, " + type + " ");
/* 391 */       return Boolean.valueOf(false);
/*     */     }
/* 393 */     return (Boolean)this.mConversationTypeStateMap.get(type);
/*     */   }
/*     */ 
/*     */   public void registerMessageTemplate(IContainerItemProvider.MessageProvider provider)
/*     */   {
/* 398 */     ProviderTag tag = (ProviderTag)provider.getClass().getAnnotation(ProviderTag.class);
/* 399 */     if (tag == null)
/* 400 */       throw new RuntimeException("ProviderTag not def MessageContent type");
/* 401 */     this.mTemplateMap.put(tag.messageContent(), provider);
/* 402 */     this.mProviderMap.put(tag.messageContent(), tag);
/*     */   }
/*     */ 
/*     */   public IContainerItemProvider.MessageProvider getMessageTemplate(Class<? extends MessageContent> type) {
/* 406 */     IContainerItemProvider.MessageProvider provider = (IContainerItemProvider.MessageProvider)this.mTemplateMap.get(type);
/* 407 */     return provider;
/*     */   }
/*     */ 
/*     */   public ProviderTag getMessageProviderTag(Class<? extends MessageContent> type) {
/* 411 */     return (ProviderTag)this.mProviderMap.get(type);
/*     */   }
/*     */ 
/*     */   public EvaluateTextMessageItemProvider getEvaluateProvider() {
/* 415 */     if (this.evaluateTextMessageItemProvider == null) {
/* 416 */       this.evaluateTextMessageItemProvider = new EvaluateTextMessageItemProvider();
/*     */     }
/* 418 */     return this.evaluateTextMessageItemProvider;
/*     */   }
/*     */ 
/*     */   public void executorBackground(Runnable runnable) {
/* 422 */     if (runnable == null) {
/* 423 */       return;
/*     */     }
/* 425 */     this.executorService.execute(runnable);
/*     */   }
/*     */ 
/*     */   public UserInfo getUserInfoFromCache(String userId)
/*     */   {
/* 430 */     if (userId != null) {
/* 431 */       return RongUserInfoManager.getInstance().getUserInfo(userId);
/*     */     }
/* 433 */     return null;
/*     */   }
/*     */ 
/*     */   public Group getGroupInfoFromCache(String groupId)
/*     */   {
/* 438 */     if (groupId != null) {
/* 439 */       return RongUserInfoManager.getInstance().getGroupInfo(groupId);
/*     */     }
/* 441 */     return null;
/*     */   }
/*     */ 
/*     */   public GroupUserInfo getGroupUserInfoFromCache(String groupId, String userId)
/*     */   {
/* 446 */     return RongUserInfoManager.getInstance().getGroupUserInfo(groupId, userId);
/*     */   }
/*     */ 
/*     */   public Discussion getDiscussionInfoFromCache(String discussionId) {
/* 450 */     return RongUserInfoManager.getInstance().getDiscussionInfo(discussionId);
/*     */   }
/*     */ 
/*     */   public PublicServiceProfile getPublicServiceInfoFromCache(String messageKey) {
/* 454 */     String id = StringUtils.getArg1(messageKey);
/* 455 */     String arg2 = StringUtils.getArg2(messageKey);
/* 456 */     int iArg2 = Integer.parseInt(arg2);
/* 457 */     Conversation.PublicServiceType type = null;
/*     */ 
/* 459 */     if (iArg2 == Conversation.PublicServiceType.PUBLIC_SERVICE.getValue())
/* 460 */       type = Conversation.PublicServiceType.PUBLIC_SERVICE;
/* 461 */     else if (iArg2 == Conversation.PublicServiceType.APP_PUBLIC_SERVICE.getValue()) {
/* 462 */       type = Conversation.PublicServiceType.APP_PUBLIC_SERVICE;
/*     */     }
/* 464 */     return RongUserInfoManager.getInstance().getPublicServiceProfile(type, id);
/*     */   }
/*     */ 
/*     */   public Conversation.ConversationNotificationStatus getConversationNotifyStatusFromCache(ConversationKey messageKey) {
/* 468 */     if ((messageKey != null) && (messageKey.getKey() != null)) {
/* 469 */       return (Conversation.ConversationNotificationStatus)this.mNotificationCache.get(messageKey.getKey());
/*     */     }
/* 471 */     return null;
/*     */   }
/*     */ 
/*     */   public void setConversationNotifyStatusToCache(ConversationKey conversationKey, Conversation.ConversationNotificationStatus status) {
/* 475 */     this.mNotificationCache.put(conversationKey.getKey(), status);
/*     */   }
/*     */ 
/*     */   public RongIM.ConversationBehaviorListener getConversationBehaviorListener() {
/* 479 */     return this.mConversationBehaviorListener;
/*     */   }
/*     */ 
/*     */   public void setConversationBehaviorListener(RongIM.ConversationBehaviorListener conversationBehaviorListener) {
/* 483 */     this.mConversationBehaviorListener = conversationBehaviorListener;
/*     */   }
/*     */ 
/*     */   public RongIM.PublicServiceBehaviorListener getPublicServiceBehaviorListener() {
/* 487 */     return this.mPublicServiceBehaviorListener;
/*     */   }
/*     */ 
/*     */   public void setPublicServiceBehaviorListener(RongIM.PublicServiceBehaviorListener publicServiceBehaviorListener) {
/* 491 */     this.mPublicServiceBehaviorListener = publicServiceBehaviorListener;
/*     */   }
/*     */ 
/*     */   public void setOnMemberSelectListener(RongIM.OnSelectMemberListener listener) {
/* 495 */     this.mMemberSelectListener = listener;
/*     */   }
/*     */ 
/*     */   public RongIM.OnSelectMemberListener getMemberSelectListener() {
/* 499 */     return this.mMemberSelectListener;
/*     */   }
/*     */ 
/*     */   public void setGetUserInfoProvider(RongIM.UserInfoProvider provider, boolean isCache)
/*     */   {
/* 504 */     this.mUserInfoProvider = provider;
/* 505 */     RongUserInfoManager.getInstance().setIsCacheUserInfo(isCache);
/*     */   }
/*     */ 
/*     */   void setGetGroupInfoProvider(RongIM.GroupInfoProvider provider, boolean isCacheGroupInfo) {
/* 509 */     this.mGroupProvider = provider;
/* 510 */     RongUserInfoManager.getInstance().setIsCacheGroupInfo(isCacheGroupInfo);
/*     */   }
/*     */ 
/*     */   RongIM.UserInfoProvider getUserInfoProvider() {
/* 514 */     return this.mUserInfoProvider;
/*     */   }
/*     */ 
/*     */   public RongIM.GroupInfoProvider getGroupInfoProvider() {
/* 518 */     return this.mGroupProvider;
/*     */   }
/*     */ 
/*     */   public void setGroupUserInfoProvider(RongIM.GroupUserInfoProvider groupUserInfoProvider, boolean isCache) {
/* 522 */     this.mGroupUserInfoProvider = groupUserInfoProvider;
/* 523 */     RongUserInfoManager.getInstance().setIsCacheGroupUserInfo(isCache);
/*     */   }
/*     */ 
/*     */   public RongIM.GroupUserInfoProvider getGroupUserInfoProvider() {
/* 527 */     return this.mGroupUserInfoProvider;
/*     */   }
/*     */ 
/*     */   public void addInputExtentionProvider(Conversation.ConversationType conversationType, InputProvider.ExtendProvider[] providers) {
/* 531 */     if ((providers == null) || (conversationType == null))
/* 532 */       return;
/* 533 */     if (this.mExtendProvider.containsKey(conversationType))
/* 534 */       for (InputProvider.ExtendProvider p : providers)
/* 535 */         ((List)this.mExtendProvider.get(conversationType)).add(p);
/*     */   }
/*     */ 
/*     */   public void resetInputExtensionProvider(Conversation.ConversationType conversationType, InputProvider.ExtendProvider[] providers)
/*     */   {
/* 541 */     if (conversationType == null)
/* 542 */       return;
/* 543 */     if (this.mExtendProvider.containsKey(conversationType)) {
/* 544 */       ((List)this.mExtendProvider.get(conversationType)).clear();
/* 545 */       if (providers == null)
/* 546 */         return;
/* 547 */       for (InputProvider.ExtendProvider p : providers)
/* 548 */         ((List)this.mExtendProvider.get(conversationType)).add(p);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setPrimaryInputProvider(InputProvider.MainInputProvider provider)
/*     */   {
/* 555 */     this.mPrimaryProvider = provider;
/* 556 */     this.mPrimaryProvider.setIndex(0);
/*     */   }
/*     */ 
/*     */   public void setSecondaryInputProvider(InputProvider.MainInputProvider provider) {
/* 560 */     this.mSecondaryProvider = provider;
/* 561 */     this.mSecondaryProvider.setIndex(1);
/*     */   }
/*     */ 
/*     */   public void setMenuInputProvider(InputProvider.MainInputProvider provider) {
/* 565 */     this.mMenuProvider = provider;
/*     */   }
/*     */ 
/*     */   public InputProvider.MainInputProvider getSecondaryInputProvider() {
/* 569 */     return this.mSecondaryProvider;
/*     */   }
/*     */ 
/*     */   public List<InputProvider.ExtendProvider> getRegisteredExtendProviderList(Conversation.ConversationType conversationType) {
/* 573 */     return (List)this.mExtendProvider.get(conversationType);
/*     */   }
/*     */ 
/*     */   public InputProvider.MainInputProvider getPrimaryInputProvider() {
/* 577 */     return this.mPrimaryProvider;
/*     */   }
/*     */ 
/*     */   public InputProvider.MainInputProvider getMenuInputProvider() {
/* 581 */     return this.mMenuProvider;
/*     */   }
/*     */ 
/*     */   public void registerConversationInfo(ConversationInfo info) {
/* 585 */     if (info != null) {
/* 586 */       ConversationKey key = ConversationKey.obtain(info.getTargetId(), info.getConversationType());
/* 587 */       if ((key != null) && (!this.mCurrentConversationList.contains(key.getKey())))
/* 588 */         this.mCurrentConversationList.add(key.getKey());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unregisterConversationInfo(ConversationInfo info)
/*     */   {
/* 594 */     if (info != null) {
/* 595 */       ConversationKey key = ConversationKey.obtain(info.getTargetId(), info.getConversationType());
/* 596 */       if ((key != null) && (this.mCurrentConversationList.size() > 0))
/* 597 */         this.mCurrentConversationList.remove(key.getKey());
/*     */     }
/*     */   }
/*     */ 
/*     */   public RongIM.LocationProvider getLocationProvider()
/*     */   {
/* 604 */     return this.mLocationProvider;
/*     */   }
/*     */ 
/*     */   public void setLocationProvider(RongIM.LocationProvider locationProvider) {
/* 608 */     this.mLocationProvider = locationProvider;
/*     */   }
/*     */ 
/*     */   public RongIM.OnSendMessageListener getOnSendMessageListener() {
/* 612 */     return this.mOnSendMessageListener;
/*     */   }
/*     */ 
/*     */   public void setOnSendMessageListener(RongIM.OnSendMessageListener onSendMessageListener) {
/* 616 */     this.mOnSendMessageListener = onSendMessageListener;
/*     */   }
/*     */ 
/*     */   public void setCurrentUserInfo(UserInfo userInfo)
/*     */   {
/* 625 */     this.mCurrentUserInfo = userInfo;
/*     */ 
/* 627 */     if ((userInfo != null) && (!TextUtils.isEmpty(userInfo.getUserId())))
/* 628 */       RongUserInfoManager.getInstance().setUserInfo(userInfo);
/*     */   }
/*     */ 
/*     */   public UserInfo getCurrentUserInfo()
/*     */   {
/* 638 */     if (this.mCurrentUserInfo != null) {
/* 639 */       return this.mCurrentUserInfo;
/*     */     }
/* 641 */     return null;
/*     */   }
/*     */ 
/*     */   public String getToken()
/*     */   {
/* 650 */     return getSharedPreferences("rc_token", 0).getString("token_value", "");
/*     */   }
/*     */ 
/*     */   public void setUserInfoAttachedState(boolean state)
/*     */   {
/* 659 */     this.isUserInfoAttached = state;
/*     */   }
/*     */ 
/*     */   public boolean getUserInfoAttachedState()
/*     */   {
/* 668 */     return this.isUserInfoAttached;
/*     */   }
/*     */ 
/*     */   public void setPublicServiceMenuClickListener(IPublicServiceMenuClickListener menuClickListener)
/*     */   {
/* 679 */     this.mPublicServiceMenuClickListener = menuClickListener;
/*     */   }
/*     */ 
/*     */   public IPublicServiceMenuClickListener getPublicServiceMenuClickListener() {
/* 683 */     return this.mPublicServiceMenuClickListener;
/*     */   }
/*     */ 
/*     */   public RongIM.ConversationListBehaviorListener getConversationListBehaviorListener() {
/* 687 */     return this.mConversationListBehaviorListener;
/*     */   }
/*     */ 
/*     */   public void setConversationListBehaviorListener(RongIM.ConversationListBehaviorListener conversationListBehaviorListener) {
/* 691 */     this.mConversationListBehaviorListener = conversationListBehaviorListener;
/*     */   }
/*     */ 
/*     */   public void setRequestPermissionListener(RongIM.RequestPermissionsListener listener) {
/* 695 */     this.mRequestPermissionsListener = listener;
/*     */   }
/*     */ 
/*     */   public RongIM.RequestPermissionsListener getRequestPermissionListener() {
/* 699 */     return this.mRequestPermissionsListener;
/*     */   }
/*     */ 
/*     */   public void showUnreadMessageIcon(boolean state) {
/* 703 */     this.isShowUnreadMessageState = state;
/*     */   }
/*     */ 
/*     */   public void showNewMessageIcon(boolean state) {
/* 707 */     this.isShowNewMessageState = state;
/*     */   }
/*     */ 
/*     */   public boolean getUnreadMessageState() {
/* 711 */     return this.isShowUnreadMessageState;
/*     */   }
/*     */ 
/*     */   public boolean getNewMessageState() {
/* 715 */     return this.isShowNewMessageState;
/*     */   }
/*     */ 
/*     */   public String getGatheredConversationTitle(Conversation.ConversationType type) {
/* 719 */     String title = "";
/* 720 */     switch (2.$SwitchMap$io$rong$imlib$model$Conversation$ConversationType[type.ordinal()]) {
/*     */     case 1:
/* 722 */       title = getString(R.string.rc_conversation_list_my_private_conversation);
/* 723 */       break;
/*     */     case 2:
/* 725 */       title = getString(R.string.rc_conversation_list_my_group);
/* 726 */       break;
/*     */     case 3:
/* 728 */       title = getString(R.string.rc_conversation_list_my_discussion);
/* 729 */       break;
/*     */     case 4:
/* 731 */       title = getString(R.string.rc_conversation_list_my_chatroom);
/* 732 */       break;
/*     */     case 5:
/* 734 */       title = getString(R.string.rc_conversation_list_my_customer_service);
/* 735 */       break;
/*     */     case 6:
/* 737 */       title = getString(R.string.rc_conversation_list_system_conversation);
/* 738 */       break;
/*     */     case 7:
/* 740 */       title = getString(R.string.rc_conversation_list_app_public_service);
/* 741 */       break;
/*     */     case 8:
/* 743 */       title = getString(R.string.rc_conversation_list_public_service);
/* 744 */       break;
/*     */     default:
/* 746 */       System.err.print("It's not the default conversation type!!");
/*     */     }
/*     */ 
/* 749 */     return title;
/*     */   }
/*     */ 
/*     */   void setReadReceiptConversationTypeList(Conversation.ConversationType[] types) {
/* 753 */     if (types == null) {
/* 754 */       RLog.d("RongContext", "setReadReceiptConversationTypeList parameter is null");
/* 755 */       return;
/*     */     }
/* 757 */     this.mReadReceiptConversationTypeList.clear();
/* 758 */     for (Conversation.ConversationType type : types)
/* 759 */       this.mReadReceiptConversationTypeList.add(type);
/*     */   }
/*     */ 
/*     */   public boolean isReadReceiptConversationType(Conversation.ConversationType type)
/*     */   {
/* 764 */     if (this.mReadReceiptConversationTypeList == null) {
/* 765 */       RLog.d("RongContext", "isReadReceiptConversationType mReadReceiptConversationTypeList is null");
/* 766 */       return false;
/*     */     }
/* 768 */     return this.mReadReceiptConversationTypeList.contains(type);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.RongContext
 * JD-Core Version:    0.6.0
 */
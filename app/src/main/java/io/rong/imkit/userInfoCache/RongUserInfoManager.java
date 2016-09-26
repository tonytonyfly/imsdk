/*     */ package io.rong.imkit.userInfoCache;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.net.Uri;
/*     */ import android.os.Handler;
/*     */ import android.os.HandlerThread;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.model.GroupUserInfo;
/*     */ import io.rong.imkit.utils.StringUtils;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Conversation.PublicServiceType;
/*     */ import io.rong.imlib.model.Discussion;
/*     */ import io.rong.imlib.model.Group;
/*     */ import io.rong.imlib.model.PublicServiceProfile;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import java.util.HashSet;
/*     */ 
/*     */ public class RongUserInfoManager
/*     */ {
/*     */   private static final String TAG = "RongUserInfoManager";
/*     */   private RongDatabaseDao mRongDatabaseDao;
/*     */   private RongUserCache<String, UserInfo> mUserInfoCache;
/*     */   private RongUserCache<String, GroupUserInfo> mGroupUserInfoCache;
/*     */   private RongUserCache<String, RongConversationInfo> mGroupCache;
/*     */   private RongUserCache<String, RongConversationInfo> mDiscussionCache;
/*     */   private RongUserCache<String, PublicServiceProfile> mPublicServiceProfileCache;
/*     */   private final HashSet<String> mUserQuerySet;
/*     */   private final HashSet<String> mGroupUserQuerySet;
/*     */   private final HashSet<String> mGroupQuerySet;
/*     */   private final HashSet<String> mDiscussionQuerySet;
/*     */   private IRongCacheListener mCacheListener;
/*  36 */   private boolean mIsCacheUserInfo = true;
/*  37 */   private boolean mIsCacheGroupInfo = true;
/*  38 */   private boolean mIsCacheGroupUserInfo = true;
/*     */   private Handler mWorkHandler;
/*     */   private HandlerThread mWorkThread;
/*     */   private String mAppKey;
/*     */   private String mUserId;
/*     */   private boolean mInitialized;
/*     */ 
/*     */   private RongUserInfoManager()
/*     */   {
/*  50 */     this.mUserInfoCache = new RongUserCache(64);
/*  51 */     this.mGroupUserInfoCache = new RongUserCache(64);
/*  52 */     this.mGroupCache = new RongUserCache(16);
/*  53 */     this.mDiscussionCache = new RongUserCache(16);
/*  54 */     this.mPublicServiceProfileCache = new RongUserCache(64);
/*  55 */     this.mUserQuerySet = new HashSet();
/*  56 */     this.mGroupQuerySet = new HashSet();
/*  57 */     this.mGroupUserQuerySet = new HashSet();
/*  58 */     this.mDiscussionQuerySet = new HashSet();
/*  59 */     this.mWorkThread = new HandlerThread("RongUserInfoManager");
/*  60 */     this.mWorkThread.start();
/*  61 */     this.mWorkHandler = new Handler(this.mWorkThread.getLooper());
/*  62 */     this.mRongDatabaseDao = new RongDatabaseDao();
/*  63 */     this.mInitialized = false;
/*     */   }
/*     */ 
/*     */   public void setIsCacheUserInfo(boolean mIsCacheUserInfo) {
/*  67 */     this.mIsCacheUserInfo = mIsCacheUserInfo;
/*     */   }
/*     */ 
/*     */   public void setIsCacheGroupInfo(boolean mIsCacheGroupInfo) {
/*  71 */     this.mIsCacheGroupInfo = mIsCacheGroupInfo;
/*     */   }
/*     */ 
/*     */   public void setIsCacheGroupUserInfo(boolean mIsCacheGroupUserInfo) {
/*  75 */     this.mIsCacheGroupUserInfo = mIsCacheGroupUserInfo;
/*     */   }
/*     */ 
/*     */   public static RongUserInfoManager getInstance() {
/*  79 */     return SingletonHolder.sInstance;
/*     */   }
/*     */ 
/*     */   public void init(Context context, String appKey, String userId, IRongCacheListener listener) {
/*  83 */     if ((TextUtils.isEmpty(appKey)) || (TextUtils.isEmpty(userId))) {
/*  84 */       RLog.e("RongUserInfoManager", "init, appkey or userId is null.");
/*  85 */       return;
/*     */     }
/*  87 */     if ((!this.mInitialized) || (this.mUserId == null) || (!this.mUserId.equals(userId))) {
/*  88 */       this.mInitialized = true;
/*  89 */       this.mAppKey = appKey;
/*  90 */       this.mUserId = userId;
/*  91 */       this.mCacheListener = listener;
/*  92 */       this.mRongDatabaseDao.open(context, this.mAppKey, userId);
/*     */     }
/*  94 */     RLog.i("RongUserInfoManager", "init : " + this.mUserId + ", " + this.mInitialized);
/*     */   }
/*     */ 
/*     */   public boolean isInitialized(String userId) {
/*  98 */     if (!this.mInitialized) {
/*  99 */       return false;
/*     */     }
/* 101 */     return (TextUtils.isEmpty(userId)) || (TextUtils.isEmpty(this.mUserId)) || (this.mUserId.equals(userId));
/*     */   }
/*     */ 
/*     */   public void uninit()
/*     */   {
/* 107 */     RLog.i("RongUserInfoManager", "uninit " + this.mInitialized);
/* 108 */     if (this.mInitialized) {
/* 109 */       if (this.mUserInfoCache != null) {
/* 110 */         this.mUserInfoCache.clear();
/*     */       }
/* 112 */       if (this.mDiscussionCache != null) {
/* 113 */         this.mDiscussionCache.clear();
/*     */       }
/* 115 */       if (this.mGroupCache != null) {
/* 116 */         this.mGroupCache.clear();
/*     */       }
/* 118 */       if (this.mGroupUserInfoCache != null) {
/* 119 */         this.mGroupUserInfoCache.clear();
/*     */       }
/* 121 */       if (this.mPublicServiceProfileCache != null) {
/* 122 */         this.mPublicServiceProfileCache.clear();
/*     */       }
/* 124 */       this.mCacheListener = null;
/* 125 */       this.mInitialized = false;
/* 126 */       this.mUserId = null;
/* 127 */       this.mAppKey = null;
/* 128 */       this.mRongDatabaseDao.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   private UserInfo putUserInfoInCache(UserInfo info) {
/* 133 */     return (UserInfo)this.mUserInfoCache.put(info.getUserId(), info);
/*     */   }
/*     */ 
/*     */   private void insertUserInfoInDB(UserInfo info) {
/* 137 */     if (this.mRongDatabaseDao != null)
/* 138 */       this.mRongDatabaseDao.insertUserInfo(info);
/*     */   }
/*     */ 
/*     */   private void putUserInfoInDB(UserInfo info)
/*     */   {
/* 143 */     if (this.mRongDatabaseDao != null)
/* 144 */       this.mRongDatabaseDao.putUserInfo(info);
/*     */   }
/*     */ 
/*     */   public UserInfo getUserInfo(String id)
/*     */   {
/* 149 */     if (id == null) {
/* 150 */       return null;
/*     */     }
/* 152 */     UserInfo info = null;
/*     */ 
/* 154 */     if (this.mIsCacheUserInfo) {
/* 155 */       info = (UserInfo)this.mUserInfoCache.get(id);
/* 156 */       if (info == null)
/* 157 */         this.mWorkHandler.post(new Object(id)
/*     */         {
/*     */           public void run() {
/* 160 */             synchronized (RongUserInfoManager.this.mUserQuerySet) {
/* 161 */               if (RongUserInfoManager.this.mUserQuerySet.contains(this.val$id)) {
/* 162 */                 return;
/*     */               }
/* 164 */               RongUserInfoManager.this.mUserQuerySet.add(this.val$id);
/*     */             }
/*     */ 
/* 167 */             UserInfo userInfo = null;
/* 168 */             if (RongUserInfoManager.this.mRongDatabaseDao != null) {
/* 169 */               userInfo = RongUserInfoManager.this.mRongDatabaseDao.getUserInfo(this.val$id);
/*     */             }
/* 171 */             if (userInfo == null) {
/* 172 */               if (RongUserInfoManager.this.mCacheListener != null) {
/* 173 */                 userInfo = RongUserInfoManager.this.mCacheListener.getUserInfo(this.val$id);
/*     */               }
/* 175 */               if (userInfo != null) {
/* 176 */                 RongUserInfoManager.this.putUserInfoInDB(userInfo);
/*     */               }
/*     */             }
/* 179 */             if (userInfo != null) {
/* 180 */               RongUserInfoManager.this.putUserInfoInCache(userInfo);
/* 181 */               if (RongUserInfoManager.this.mCacheListener != null) {
/* 182 */                 RongUserInfoManager.this.mCacheListener.onUserInfoUpdated(userInfo);
/*     */               }
/*     */             }
/* 185 */             RongUserInfoManager.this.mUserQuerySet.remove(this.val$id);
/*     */           }
/*     */         });
/*     */     }
/* 190 */     else if (this.mCacheListener != null) {
/* 191 */       info = this.mCacheListener.getUserInfo(id);
/*     */     }
/*     */ 
/* 194 */     return info;
/*     */   }
/*     */ 
/*     */   public GroupUserInfo getGroupUserInfo(String gId, String id) {
/* 198 */     if ((gId == null) || (id == null)) {
/* 199 */       return null;
/*     */     }
/* 201 */     String key = StringUtils.getKey(gId, id);
/* 202 */     GroupUserInfo info = null;
/* 203 */     if (this.mIsCacheGroupUserInfo) {
/* 204 */       info = (GroupUserInfo)this.mGroupUserInfoCache.get(key);
/* 205 */       if (info == null)
/* 206 */         this.mWorkHandler.post(new Object(key, gId, id)
/*     */         {
/*     */           public void run() {
/* 209 */             synchronized (RongUserInfoManager.this.mGroupUserQuerySet) {
/* 210 */               if (RongUserInfoManager.this.mGroupUserQuerySet.contains(this.val$key)) {
/* 211 */                 return;
/*     */               }
/* 213 */               RongUserInfoManager.this.mGroupUserQuerySet.add(this.val$key);
/*     */             }
/* 215 */             GroupUserInfo groupUserInfo = null;
/* 216 */             if (RongUserInfoManager.this.mRongDatabaseDao != null) {
/* 217 */               groupUserInfo = RongUserInfoManager.this.mRongDatabaseDao.getGroupUserInfo(this.val$gId, this.val$id);
/*     */             }
/* 219 */             if (groupUserInfo == null) {
/* 220 */               if (RongUserInfoManager.this.mCacheListener != null) {
/* 221 */                 groupUserInfo = RongUserInfoManager.this.mCacheListener.getGroupUserInfo(this.val$gId, this.val$id);
/*     */               }
/* 223 */               if ((groupUserInfo != null) && (RongUserInfoManager.this.mRongDatabaseDao != null)) {
/* 224 */                 RongUserInfoManager.this.mRongDatabaseDao.putGroupUserInfo(groupUserInfo);
/*     */               }
/*     */             }
/* 227 */             if (groupUserInfo != null) {
/* 228 */               RongUserInfoManager.this.mGroupUserInfoCache.put(this.val$key, groupUserInfo);
/* 229 */               if (RongUserInfoManager.this.mCacheListener != null) {
/* 230 */                 RongUserInfoManager.this.mCacheListener.onGroupUserInfoUpdated(groupUserInfo);
/*     */               }
/*     */             }
/* 233 */             RongUserInfoManager.this.mGroupUserQuerySet.remove(this.val$key);
/*     */           }
/*     */         });
/*     */     }
/* 238 */     else if (this.mCacheListener != null) {
/* 239 */       info = this.mCacheListener.getGroupUserInfo(gId, id);
/*     */     }
/*     */ 
/* 242 */     return info;
/*     */   }
/*     */ 
/*     */   public Group getGroupInfo(String id) {
/* 246 */     if (id == null) {
/* 247 */       return null;
/*     */     }
/* 249 */     Group groupInfo = null;
/* 250 */     if (this.mIsCacheGroupInfo) {
/* 251 */       RongConversationInfo info = (RongConversationInfo)this.mGroupCache.get(id);
/* 252 */       if (info == null)
/* 253 */         this.mWorkHandler.post(new Object(id)
/*     */         {
/*     */           public void run() {
/* 256 */             synchronized (RongUserInfoManager.this.mGroupQuerySet) {
/* 257 */               if (RongUserInfoManager.this.mGroupQuerySet.contains(this.val$id)) {
/* 258 */                 return;
/*     */               }
/* 260 */               RongUserInfoManager.this.mGroupQuerySet.add(this.val$id);
/*     */             }
/* 262 */             Group group = null;
/* 263 */             if (RongUserInfoManager.this.mRongDatabaseDao != null) {
/* 264 */               group = RongUserInfoManager.this.mRongDatabaseDao.getGroupInfo(this.val$id);
/*     */             }
/* 266 */             if (group == null) {
/* 267 */               if (RongUserInfoManager.this.mCacheListener != null) {
/* 268 */                 group = RongUserInfoManager.this.mCacheListener.getGroupInfo(this.val$id);
/*     */               }
/* 270 */               if ((group != null) && 
/* 271 */                 (RongUserInfoManager.this.mRongDatabaseDao != null)) {
/* 272 */                 RongUserInfoManager.this.mRongDatabaseDao.putGroupInfo(group);
/*     */               }
/*     */             }
/*     */ 
/* 276 */             if (group != null) {
/* 277 */               RongConversationInfo conversationInfo = new RongConversationInfo(Conversation.ConversationType.GROUP.getValue() + "", group.getId(), group.getName(), group.getPortraitUri());
/* 278 */               RongUserInfoManager.this.mGroupCache.put(this.val$id, conversationInfo);
/* 279 */               if (RongUserInfoManager.this.mCacheListener != null) {
/* 280 */                 RongUserInfoManager.this.mCacheListener.onGroupUpdated(group);
/*     */               }
/*     */             }
/* 283 */             RongUserInfoManager.this.mGroupQuerySet.remove(this.val$id);
/*     */           }
/*     */         });
/* 287 */       else groupInfo = new Group(info.getId(), info.getName(), info.getUri());
/*     */ 
/*     */     }
/* 290 */     else if (this.mCacheListener != null) {
/* 291 */       groupInfo = this.mCacheListener.getGroupInfo(id);
/*     */     }
/*     */ 
/* 294 */     return groupInfo;
/*     */   }
/*     */ 
/*     */   public Discussion getDiscussionInfo(String id) {
/* 298 */     if (id == null) {
/* 299 */       return null;
/*     */     }
/* 301 */     Discussion discussionInfo = null;
/* 302 */     RongConversationInfo info = (RongConversationInfo)this.mDiscussionCache.get(id);
/* 303 */     if (info == null)
/* 304 */       this.mWorkHandler.post(new Object(id)
/*     */       {
/*     */         public void run() {
/* 307 */           synchronized (RongUserInfoManager.this.mDiscussionQuerySet) {
/* 308 */             if (RongUserInfoManager.this.mDiscussionQuerySet.contains(this.val$id)) {
/* 309 */               return;
/*     */             }
/* 311 */             RongUserInfoManager.this.mDiscussionQuerySet.add(this.val$id);
/*     */           }
/* 313 */           Discussion discussion = null;
/*     */ 
/* 315 */           if (RongUserInfoManager.this.mRongDatabaseDao != null) {
/* 316 */             discussion = RongUserInfoManager.this.mRongDatabaseDao.getDiscussionInfo(this.val$id);
/*     */           }
/* 318 */           if (discussion != null) {
/* 319 */             RongConversationInfo conversationInfo = new RongConversationInfo(Conversation.ConversationType.DISCUSSION.getValue() + "", discussion.getId(), discussion.getName(), null);
/* 320 */             RongUserInfoManager.this.mDiscussionCache.put(this.val$id, conversationInfo);
/* 321 */             if (RongUserInfoManager.this.mCacheListener != null) {
/* 322 */               RongUserInfoManager.this.mCacheListener.onDiscussionUpdated(discussion);
/*     */             }
/* 324 */             RongUserInfoManager.this.mDiscussionQuerySet.remove(this.val$id);
/*     */           } else {
/* 326 */             RongIM.getInstance().getDiscussion(this.val$id, new RongIMClient.ResultCallback()
/*     */             {
/*     */               public void onSuccess(Discussion discussion) {
/* 329 */                 if (discussion != null) {
/* 330 */                   if (RongUserInfoManager.this.mRongDatabaseDao != null) {
/* 331 */                     RongUserInfoManager.this.mRongDatabaseDao.putDiscussionInfo(discussion);
/*     */                   }
/* 333 */                   RongConversationInfo conversationInfo = new RongConversationInfo(Conversation.ConversationType.DISCUSSION.getValue() + "", discussion.getId(), discussion.getName(), null);
/* 334 */                   RongUserInfoManager.this.mDiscussionCache.put(RongUserInfoManager.4.this.val$id, conversationInfo);
/* 335 */                   if (RongUserInfoManager.this.mCacheListener != null) {
/* 336 */                     RongUserInfoManager.this.mCacheListener.onDiscussionUpdated(discussion);
/*     */                   }
/*     */                 }
/* 339 */                 RongUserInfoManager.this.mDiscussionQuerySet.remove(RongUserInfoManager.4.this.val$id);
/*     */               }
/*     */ 
/*     */               public void onError(RongIMClient.ErrorCode e)
/*     */               {
/* 344 */                 RongUserInfoManager.this.mDiscussionQuerySet.remove(RongUserInfoManager.4.this.val$id);
/*     */               } } );
/*     */           }
/*     */         }
/*     */       });
/* 351 */     else discussionInfo = new Discussion(info.getId(), info.getName());
/*     */ 
/* 353 */     return discussionInfo;
/*     */   }
/*     */ 
/*     */   public PublicServiceProfile getPublicServiceProfile(Conversation.PublicServiceType type, String id) {
/* 357 */     if ((type == null) || (id == null)) {
/* 358 */       return null;
/*     */     }
/* 360 */     String key = StringUtils.getKey(type.getValue() + "", id);
/*     */ 
/* 362 */     PublicServiceProfile info = (PublicServiceProfile)this.mPublicServiceProfileCache.get(key);
/*     */ 
/* 364 */     if (info == null)
/* 365 */       this.mWorkHandler.post(new Runnable(type, id, key)
/*     */       {
/*     */         public void run() {
/* 368 */           RongIM.getInstance().getPublicServiceProfile(this.val$type, this.val$id, new RongIMClient.ResultCallback()
/*     */           {
/*     */             public void onSuccess(PublicServiceProfile result) {
/* 371 */               if (result != null) {
/* 372 */                 RongUserInfoManager.this.mPublicServiceProfileCache.put(RongUserInfoManager.5.this.val$key, result);
/* 373 */                 if (RongUserInfoManager.this.mCacheListener != null)
/* 374 */                   RongUserInfoManager.this.mCacheListener.onPublicServiceProfileUpdated(result);
/*     */               }
/*     */             }
/*     */ 
/*     */             public void onError(RongIMClient.ErrorCode e)
/*     */             {
/*     */             }
/*     */           });
/*     */         }
/*     */       });
/* 386 */     return info;
/*     */   }
/*     */ 
/*     */   public void setUserInfo(UserInfo info) {
/* 390 */     if (this.mIsCacheUserInfo) {
/* 391 */       UserInfo oldInfo = putUserInfoInCache(info);
/* 392 */       if ((oldInfo == null) || ((oldInfo.getName() != null) && (info.getName() != null) && (!oldInfo.getName().equals(info.getName()))) || ((oldInfo.getPortraitUri() != null) && (info.getPortraitUri() != null) && (!oldInfo.getPortraitUri().toString().equals(info.getPortraitUri().toString()))))
/*     */       {
/* 395 */         this.mWorkHandler.post(new Runnable(info)
/*     */         {
/*     */           public void run() {
/* 398 */             RongUserInfoManager.this.putUserInfoInDB(this.val$info);
/* 399 */             if (RongUserInfoManager.this.mCacheListener != null)
/* 400 */               RongUserInfoManager.this.mCacheListener.onUserInfoUpdated(this.val$info);
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/* 406 */     else if (this.mCacheListener != null) {
/* 407 */       this.mCacheListener.onUserInfoUpdated(info);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setGroupUserInfo(GroupUserInfo info)
/*     */   {
/* 413 */     String key = StringUtils.getKey(info.getGroupId(), info.getUserId());
/* 414 */     if (this.mIsCacheGroupUserInfo) {
/* 415 */       GroupUserInfo oldInfo = (GroupUserInfo)this.mGroupUserInfoCache.put(key, info);
/* 416 */       if ((oldInfo == null) || ((oldInfo.getNickname() != null) && (info.getNickname() != null) && (!oldInfo.getNickname().equals(info.getNickname()))))
/*     */       {
/* 418 */         this.mWorkHandler.post(new Runnable(info)
/*     */         {
/*     */           public void run() {
/* 421 */             if (RongUserInfoManager.this.mRongDatabaseDao != null) {
/* 422 */               RongUserInfoManager.this.mRongDatabaseDao.putGroupUserInfo(this.val$info);
/*     */             }
/* 424 */             if (RongUserInfoManager.this.mCacheListener != null)
/* 425 */               RongUserInfoManager.this.mCacheListener.onGroupUserInfoUpdated(this.val$info);
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/* 431 */     else if (this.mCacheListener != null) {
/* 432 */       this.mCacheListener.onGroupUserInfoUpdated(info);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setGroupInfo(Group group)
/*     */   {
/* 438 */     if (this.mIsCacheGroupInfo) {
/* 439 */       RongConversationInfo info = new RongConversationInfo(Conversation.ConversationType.GROUP.getValue() + "", group.getId(), group.getName(), group.getPortraitUri());
/* 440 */       RongConversationInfo oldInfo = (RongConversationInfo)this.mGroupCache.put(info.getId(), info);
/* 441 */       if ((oldInfo == null) || ((oldInfo.getName() != null) && (info.getName() != null) && (!oldInfo.getName().equals(info.getName()))) || ((oldInfo.getUri() != null) && (info.getUri() != null) && (!oldInfo.getUri().toString().equals(info.getUri().toString()))))
/*     */       {
/* 444 */         this.mWorkHandler.post(new Runnable(group)
/*     */         {
/*     */           public void run() {
/* 447 */             if (RongUserInfoManager.this.mRongDatabaseDao != null) {
/* 448 */               RongUserInfoManager.this.mRongDatabaseDao.putGroupInfo(this.val$group);
/*     */             }
/* 450 */             if (RongUserInfoManager.this.mCacheListener != null)
/* 451 */               RongUserInfoManager.this.mCacheListener.onGroupUpdated(this.val$group);
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/* 457 */     else if (this.mCacheListener != null) {
/* 458 */       this.mCacheListener.onGroupUpdated(group);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setDiscussionInfo(Discussion discussion)
/*     */   {
/* 464 */     RongConversationInfo info = new RongConversationInfo(Conversation.ConversationType.DISCUSSION.getValue() + "", discussion.getId(), discussion.getName(), null);
/* 465 */     RongConversationInfo oldInfo = (RongConversationInfo)this.mDiscussionCache.put(info.getId(), info);
/* 466 */     if ((oldInfo == null) || ((oldInfo.getName() != null) && (info.getName() != null) && (!oldInfo.getName().equals(info.getName()))))
/*     */     {
/* 468 */       this.mWorkHandler.post(new Runnable(discussion)
/*     */       {
/*     */         public void run() {
/* 471 */           if (RongUserInfoManager.this.mRongDatabaseDao != null) {
/* 472 */             RongUserInfoManager.this.mRongDatabaseDao.putDiscussionInfo(this.val$discussion);
/*     */           }
/* 474 */           if (RongUserInfoManager.this.mCacheListener != null)
/* 475 */             RongUserInfoManager.this.mCacheListener.onDiscussionUpdated(this.val$discussion);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setPublicServiceProfile(PublicServiceProfile profile) {
/* 483 */     String key = StringUtils.getKey(profile.getConversationType().getValue() + "", profile.getTargetId());
/* 484 */     PublicServiceProfile oldInfo = (PublicServiceProfile)this.mPublicServiceProfileCache.put(key, profile);
/*     */ 
/* 486 */     if ((oldInfo == null) || ((oldInfo.getName() != null) && (profile.getName() != null) && (!oldInfo.getName().equals(profile.getName()))) || ((oldInfo.getPortraitUri() != null) && (profile.getPortraitUri() != null) && (!oldInfo.getPortraitUri().toString().equals(profile.getPortraitUri().toString()))))
/*     */     {
/* 489 */       this.mWorkHandler.post(new Runnable(profile)
/*     */       {
/*     */         public void run() {
/* 492 */           if (RongUserInfoManager.this.mCacheListener != null)
/* 493 */             RongUserInfoManager.this.mCacheListener.onPublicServiceProfileUpdated(this.val$profile);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SingletonHolder
/*     */   {
/*  46 */     static RongUserInfoManager sInstance = new RongUserInfoManager(null);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.userInfoCache.RongUserInfoManager
 * JD-Core Version:    0.6.0
 */
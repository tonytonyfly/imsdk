/*     */ package io.rong.imkit.utils;
/*     */ 
/*     */ import android.os.Handler;
/*     */ import android.os.HandlerThread;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ public class MessageProviderUserInfoHelper
/*     */ {
/*     */   private static final String TAG = "MessageProviderUserInfoHelper";
/*  25 */   private ConcurrentHashMap<MessageContent, List<String>> mMessageIdUserIdsMap = new ConcurrentHashMap();
/*     */   private static MessageProviderUserInfoHelper mHelper;
/*  27 */   ArrayList<String> cacheUserIds = new ArrayList();
/*     */   HandlerThread mWorkThread;
/*     */   Handler mUserInfoHandler;
/*     */ 
/*     */   public static MessageProviderUserInfoHelper getInstance()
/*     */   {
/*  34 */     if (mHelper == null) {
/*  35 */       synchronized (MessageProviderUserInfoHelper.class) {
/*  36 */         if (mHelper == null) {
/*  37 */           mHelper = new MessageProviderUserInfoHelper();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  42 */     return mHelper;
/*     */   }
/*     */ 
/*     */   MessageProviderUserInfoHelper() {
/*  46 */     this.mWorkThread = new HandlerThread("MessageProviderUserInfoHelper");
/*  47 */     this.mWorkThread.start();
/*  48 */     this.mUserInfoHandler = new Handler(this.mWorkThread.getLooper());
/*     */   }
/*     */ 
/*     */   synchronized void setCacheUserId(String userId) {
/*  52 */     if (!this.cacheUserIds.contains(userId))
/*  53 */       this.cacheUserIds.add(userId);
/*     */   }
/*     */ 
/*     */   synchronized void removeCacheUserId(String userId) {
/*  57 */     if (this.cacheUserIds.contains(userId))
/*  58 */       this.cacheUserIds.remove(userId);
/*     */   }
/*     */ 
/*     */   public synchronized boolean isCacheUserId(String userId) {
/*  62 */     return this.cacheUserIds.contains(userId);
/*     */   }
/*     */ 
/*     */   public void registerMessageUserInfo(MessageContent message, String userId)
/*     */   {
/*  71 */     RLog.i("MessageProviderUserInfoHelper", "registerMessageUserInfo userId:" + userId);
/*     */ 
/*  73 */     List userIdList = (List)this.mMessageIdUserIdsMap.get(message);
/*     */ 
/*  75 */     if (userIdList == null) {
/*  76 */       userIdList = new ArrayList();
/*  77 */       this.mMessageIdUserIdsMap.put(message, userIdList);
/*     */     }
/*     */ 
/*  80 */     if (!userIdList.contains(userId)) {
/*  81 */       userIdList.add(userId);
/*     */     }
/*     */ 
/*  84 */     setCacheUserId(userId);
/*     */   }
/*     */ 
/*     */   public void notifyMessageUpdate(String userId)
/*     */   {
/*  93 */     Iterator messageUserIdsIterator = this.mMessageIdUserIdsMap.entrySet().iterator();
/*     */ 
/*  95 */     this.mUserInfoHandler.postDelayed(new Runnable(userId)
/*     */     {
/*     */       public void run() {
/*  98 */         MessageProviderUserInfoHelper.this.removeCacheUserId(this.val$userId);
/*     */       }
/*     */     }
/*     */     , 500L);
/*     */ 
/* 103 */     while (messageUserIdsIterator.hasNext()) {
/* 104 */       Map.Entry userIdMessageEntry = (Map.Entry)messageUserIdsIterator.next();
/* 105 */       List userIdList = (List)userIdMessageEntry.getValue();
/*     */ 
/* 107 */       if (userIdList != null) {
/* 108 */         if (userIdList.contains(userId)) {
/* 109 */           userIdList.remove(userId);
/*     */         }
/*     */ 
/* 112 */         if (userIdList.isEmpty()) {
/* 113 */           RongContext.getInstance().getEventBus().post(userIdMessageEntry.getKey());
/* 114 */           this.mMessageIdUserIdsMap.remove(userIdMessageEntry.getKey());
/* 115 */           RLog.d("MessageProviderUserInfoHelper", "notifyMessageUpdate --notify--" + userIdMessageEntry.getKey().toString());
/*     */         } else {
/* 117 */           RLog.d("MessageProviderUserInfoHelper", "notifyMessageUpdate --wait--" + userId);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isRequestGetUserInfo()
/*     */   {
/* 125 */     return !this.mMessageIdUserIdsMap.isEmpty();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.utils.MessageProviderUserInfoHelper
 * JD-Core Version:    0.6.0
 */
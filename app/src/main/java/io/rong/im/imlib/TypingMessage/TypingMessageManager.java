/*     */ package io.rong.imlib.TypingMessage;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.res.Resources;
/*     */ import android.content.res.Resources.NotFoundException;
/*     */ import android.os.Handler;
/*     */ import android.os.Looper;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.RongIMClient.TypingStatusListener;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ 
/*     */ public class TypingMessageManager
/*     */ {
/*     */   private static final String TAG = "TypingMessageManager";
/*     */   private static final String SEPARATOR = ";;;";
/*  29 */   private static int DISAPPEAR_INTERVAL = 6000;
/*     */   private HashMap<String, LinkedHashMap<String, TypingStatus>> mTypingMap;
/*     */   private HashMap<String, Long> mSendingConversation;
/*     */   private Handler mHandler;
/*     */   private RongIMClient.TypingStatusListener sTypingStatusListener;
/*  58 */   private boolean isShowMessageTyping = false;
/*     */ 
/*     */   private TypingMessageManager() {
/*  61 */     this.mTypingMap = new HashMap();
/*  62 */     this.mSendingConversation = new HashMap();
/*  63 */     this.mHandler = new Handler(Looper.getMainLooper());
/*     */   }
/*     */ 
/*     */   public static TypingMessageManager getInstance()
/*     */   {
/*  71 */     return SingletonHolder.sInstance;
/*     */   }
/*     */ 
/*     */   public void init(Context context) {
/*     */     try {
/*  76 */       Resources resources = context.getResources();
/*  77 */       this.isShowMessageTyping = resources.getBoolean(resources.getIdentifier("rc_typing_status", "bool", context.getPackageName()));
/*     */     } catch (Resources.NotFoundException e) {
/*  79 */       RLog.e("TypingMessageManager", "getTypingStatus rc_typing_status not configure in rc_configuration.xml");
/*  80 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isShowMessageTyping() {
/*  85 */     return this.isShowMessageTyping;
/*     */   }
/*     */ 
/*     */   public Collection<TypingStatus> getTypingUserListFromConversation(Conversation.ConversationType conversationType, String targetId)
/*     */   {
/*  96 */     String key = conversationType.getName() + ";;;" + targetId;
/*     */ 
/*  98 */     return ((LinkedHashMap)this.mTypingMap.get(key)).values();
/*     */   }
/*     */ 
/*     */   public void sendTypingMessage(Conversation.ConversationType conversationType, String targetId, String typingContentType)
/*     */   {
/* 111 */     String key = conversationType.getName() + ";;;" + targetId;
/*     */ 
/* 114 */     if (!conversationType.equals(Conversation.ConversationType.PRIVATE)) {
/* 115 */       return;
/*     */     }
/*     */ 
/* 118 */     if (!this.mSendingConversation.containsKey(key))
/*     */     {
/* 120 */       TypingStatusMessage typingStatusMessage = new TypingStatusMessage(typingContentType, null);
/* 121 */       this.mSendingConversation.put(key, Long.valueOf(0L));
/* 122 */       RongIMClient.getInstance().sendMessage(conversationType, targetId, typingStatusMessage, null, null, null, new RongIMClient.ResultCallback(key)
/*     */       {
/*     */         public void onSuccess(Message message)
/*     */         {
/* 126 */           TypingMessageManager.this.mHandler.postDelayed(new Runnable()
/*     */           {
/*     */             public void run() {
/* 129 */               TypingMessageManager.this.mSendingConversation.remove(TypingMessageManager.1.this.val$key);
/*     */             }
/*     */           }
/*     */           , TypingMessageManager.DISAPPEAR_INTERVAL);
/*     */         }
/*     */ 
/*     */         public void onError(RongIMClient.ErrorCode e)
/*     */         {
/*     */         }
/*     */       });
/*     */     }
/*     */     else
/*     */     {
/* 139 */       RLog.d("TypingMessageManager", "sendTypingStatus typing message in this conversation is sending");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setTypingEnd(Conversation.ConversationType conversationType, String targetId)
/*     */   {
/* 150 */     String key = conversationType.getName() + ";;;" + targetId;
/*     */ 
/* 153 */     if (!conversationType.equals(Conversation.ConversationType.PRIVATE)) {
/* 154 */       return;
/*     */     }
/*     */ 
/* 157 */     if (this.mSendingConversation.containsKey(key))
/* 158 */       this.mSendingConversation.remove(key);
/*     */   }
/*     */ 
/*     */   public void setTypingMessageStatusListener(RongIMClient.TypingStatusListener listener)
/*     */   {
/* 169 */     this.sTypingStatusListener = listener;
/*     */   }
/*     */ 
/*     */   public boolean onReceiveMessage(Message message)
/*     */   {
/* 181 */     if (((message.getContent() instanceof TypingStatusMessage)) && (this.isShowMessageTyping)) {
/* 182 */       getInstance().onReceiveTypingMessage(message);
/* 183 */       return true;
/*     */     }
/* 185 */     getInstance().onReceiveOtherMessage(message);
/* 186 */     return false;
/*     */   }
/*     */ 
/*     */   private void onReceiveTypingMessage(Message message)
/*     */   {
/* 197 */     if (message.getSenderUserId().equals(RongIMClient.getInstance().getCurrentUserId())) {
/* 198 */       return;
/*     */     }
/* 200 */     Conversation.ConversationType conversationType = message.getConversationType();
/* 201 */     String targetId = message.getTargetId();
/* 202 */     TypingStatusMessage typingStatusMessage = (TypingStatusMessage)message.getContent();
/* 203 */     String typingContentType = typingStatusMessage.getTypingContentType();
/* 204 */     if (typingContentType == null)
/* 205 */       return;
/* 206 */     String userId = message.getSenderUserId();
/* 207 */     String key = conversationType.getName() + ";;;" + targetId;
/*     */ 
/* 209 */     if (this.mTypingMap.containsKey(key)) {
/* 210 */       LinkedHashMap map = (LinkedHashMap)this.mTypingMap.get(key);
/* 211 */       if (map.get(userId) == null) {
/* 212 */         TypingStatus typingStatus = new TypingStatus(userId, typingContentType, message.getSentTime());
/*     */ 
/* 214 */         map.put(userId, typingStatus);
/* 215 */         if (this.sTypingStatusListener != null)
/* 216 */           this.sTypingStatusListener.onTypingStatusChanged(conversationType, targetId, map.values());
/*     */       }
/*     */     }
/*     */     else {
/* 220 */       LinkedHashMap map = new LinkedHashMap();
/* 221 */       TypingStatus typingStatus = new TypingStatus(userId, typingContentType, message.getSentTime());
/*     */ 
/* 223 */       map.put(userId, typingStatus);
/* 224 */       if (this.sTypingStatusListener != null) {
/* 225 */         this.sTypingStatusListener.onTypingStatusChanged(conversationType, targetId, map.values());
/*     */       }
/* 227 */       this.mTypingMap.put(key, map);
/* 228 */       this.mHandler.postDelayed(new Runnable(key, userId, conversationType, targetId)
/*     */       {
/*     */         public void run() {
/* 231 */           if (TypingMessageManager.this.mTypingMap.containsKey(this.val$key)) {
/* 232 */             LinkedHashMap map = (LinkedHashMap)TypingMessageManager.this.mTypingMap.get(this.val$key);
/* 233 */             if (map.get(this.val$userId) != null) {
/* 234 */               map.remove(this.val$userId);
/* 235 */               if (TypingMessageManager.this.sTypingStatusListener != null) {
/* 236 */                 TypingMessageManager.this.sTypingStatusListener.onTypingStatusChanged(this.val$conversationType, this.val$targetId, map.values());
/*     */               }
/* 238 */               if (map.isEmpty())
/* 239 */                 TypingMessageManager.this.mTypingMap.remove(this.val$key);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       , DISAPPEAR_INTERVAL);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void onReceiveOtherMessage(Message message)
/*     */   {
/* 255 */     MessageContent content = message.getContent();
/* 256 */     MessageTag tag = (MessageTag)content.getClass().getAnnotation(MessageTag.class);
/* 257 */     if ((tag != null) && ((tag.flag() & 0x1) == 1)) {
/* 258 */       Conversation.ConversationType conversationType = message.getConversationType();
/* 259 */       String targetId = message.getTargetId();
/* 260 */       String userId = message.getSenderUserId();
/* 261 */       String key = conversationType.getName() + ";;;" + targetId;
/*     */ 
/* 263 */       if (this.mTypingMap.containsKey(key)) {
/* 264 */         LinkedHashMap map = (LinkedHashMap)this.mTypingMap.get(key);
/*     */ 
/* 266 */         if (map.get(userId) != null) {
/* 267 */           map.remove(userId);
/* 268 */           if (this.sTypingStatusListener != null) {
/* 269 */             this.sTypingStatusListener.onTypingStatusChanged(conversationType, targetId, map.values());
/*     */           }
/* 271 */           if (map.isEmpty())
/* 272 */             this.mTypingMap.remove(key);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SingletonHolder
/*     */   {
/*  67 */     static TypingMessageManager sInstance = new TypingMessageManager(null);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.TypingMessage.TypingMessageManager
 * JD-Core Version:    0.6.0
 */
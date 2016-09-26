/*     */ package io.rong.imkit.notification;
/*     */ 
/*     */ import android.os.Handler;
/*     */ import android.os.Looper;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.model.ConversationInfo;
/*     */ import io.rong.imkit.model.ConversationTypeFilter;
/*     */ import io.rong.imkit.model.ConversationTypeFilter.Level;
/*     */ import io.rong.imkit.model.Event.ConversationRemoveEvent;
/*     */ import io.rong.imkit.model.Event.ConversationUnreadEvent;
/*     */ import io.rong.imkit.model.Event.OnReceiveMessageEvent;
/*     */ import io.rong.imkit.model.Event.SyncReadStatusEvent;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Message;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class MessageCounter
/*     */ {
/*     */   RongContext mContext;
/*     */   List<Counter> mCounters;
/*     */   Handler mHandler;
/*     */ 
/*     */   public MessageCounter(RongContext context)
/*     */   {
/*  32 */     this.mContext = context;
/*  33 */     this.mCounters = new ArrayList();
/*     */ 
/*  35 */     this.mHandler = new Handler(Looper.getMainLooper());
/*  36 */     context.getEventBus().register(this);
/*     */   }
/*     */ 
/*     */   public void registerMessageCounter(Counter counter)
/*     */   {
/*  66 */     this.mCounters.add(counter);
/*  67 */     if (counter.getFilter().getLevel().equals(ConversationTypeFilter.Level.ALL)) {
/*  68 */       RongIM.getInstance().getTotalUnreadCount(new RongIMClient.ResultCallback(counter) {
/*  69 */         int currentConversationMsgCount = 0;
/*     */ 
/*     */         public void onSuccess(Integer msgCount)
/*     */         {
/*  74 */           List list = RongContext.getInstance().getCurrentConversationList();
/*     */ 
/*  76 */           for (ConversationInfo conversationInfo : list) {
/*  77 */             this.currentConversationMsgCount += RongIM.getInstance().getUnreadCount(conversationInfo.getConversationType(), conversationInfo.getTargetId());
/*     */           }
/*     */ 
/*  80 */           int totalCount = msgCount.intValue() - this.currentConversationMsgCount;
/*  81 */           this.val$counter.mCount = totalCount;
/*  82 */           this.val$counter.onMessageIncreased(totalCount);
/*     */         }
/*     */ 
/*     */         public void onError(RongIMClient.ErrorCode e) {
/*     */         }
/*     */       });
/*     */     }
/*  90 */     else if (counter.getFilter().getLevel().equals(ConversationTypeFilter.Level.CONVERSATION_TYPE))
/*     */     {
/*  92 */       Conversation.ConversationType[] types = (Conversation.ConversationType[])counter.getFilter().getConversationTypeList().toArray(new Conversation.ConversationType[counter.getFilter().getConversationTypeList().size()]);
/*  93 */       RLog.d("registerMessageCounter", "RongIM.getInstance() :" + types.length);
/*     */ 
/*  95 */       RongIM.getInstance().getUnreadCount(types, new RongIMClient.ResultCallback(counter)
/*     */       {
/*     */         public void onSuccess(Integer msgCount)
/*     */         {
/*  99 */           int currentConversationMsgCount = 0;
/* 100 */           List list = RongContext.getInstance().getCurrentConversationList();
/* 101 */           for (ConversationInfo conversationInfo : list) {
/* 102 */             currentConversationMsgCount += RongIM.getInstance().getUnreadCount(conversationInfo.getConversationType(), conversationInfo.getTargetId());
/*     */           }
/*     */ 
/* 105 */           int totalCount = msgCount.intValue() - currentConversationMsgCount;
/* 106 */           this.val$counter.mCount = totalCount;
/* 107 */           this.val$counter.onMessageIncreased(totalCount);
/*     */         }
/*     */ 
/*     */         public void onError(RongIMClient.ErrorCode e)
/*     */         {
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unregisterMessageCounter(MessageCounter counter) {
/* 119 */     this.mCounters.remove(counter);
/*     */   }
/*     */ 
/*     */   public void clearCache() {
/* 123 */     for (Counter messageCounter : this.mCounters) {
/* 124 */       messageCounter.mCount = 0;
/* 125 */       messageCounter.onMessageIncreased(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventBackgroundThread(Event.OnReceiveMessageEvent receiveMessageEvent)
/*     */   {
/* 131 */     Message message = receiveMessageEvent.getMessage();
/* 132 */     List list = RongContext.getInstance().getCurrentConversationList();
/* 133 */     for (ConversationInfo conversationInfo : list) {
/* 134 */       if ((message.getConversationType() == conversationInfo.getConversationType()) && (conversationInfo.getTargetId() != null) && (conversationInfo.getTargetId().equals(message.getTargetId()))) {
/* 135 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 139 */     if (message.getContent() != null) {
/* 140 */       MessageTag msgTag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
/* 141 */       if ((msgTag != null) && ((msgTag.flag() & 0x3) == 3))
/* 142 */         for (Counter counter : this.mCounters)
/* 143 */           if (counter.isCount(message))
/* 144 */             if (receiveMessageEvent.getLeft() != 0) {
/* 145 */               this.mHandler.post(new Runnable(counter)
/*     */               {
/*     */                 public void run() {
/* 148 */                   this.val$counter.onIncreased();
/*     */                 } } );
/*     */             }
/*     */             else {
/* 153 */               Conversation.ConversationType[] types = (Conversation.ConversationType[])counter.getFilter().getConversationTypeList().toArray(new Conversation.ConversationType[counter.getFilter().getConversationTypeList().size()]);
/* 154 */               RongIM.getInstance().getUnreadCount(types, new RongIMClient.ResultCallback(counter)
/*     */               {
/*     */                 public void onSuccess(Integer msgCount)
/*     */                 {
/* 158 */                   int currentConversationMsgCount = 0;
/* 159 */                   List list = RongContext.getInstance().getCurrentConversationList();
/* 160 */                   for (ConversationInfo conversationInfo : list) {
/* 161 */                     currentConversationMsgCount += RongIM.getInstance().getUnreadCount(conversationInfo.getConversationType(), conversationInfo.getTargetId());
/*     */                   }
/*     */ 
/* 164 */                   int totalCount = msgCount.intValue() - currentConversationMsgCount;
/* 165 */                   this.val$counter.mCount = totalCount;
/* 166 */                   this.val$counter.onMessageIncreased(totalCount);
/*     */                 }
/*     */ 
/*     */                 public void onError(RongIMClient.ErrorCode e)
/*     */                 {
/*     */                 }
/*     */               });
/*     */             }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventBackgroundThread(Event.SyncReadStatusEvent event)
/*     */   {
/* 182 */     for (Counter counter : this.mCounters)
/* 183 */       if (counter.getFilter().getLevel().equals(ConversationTypeFilter.Level.ALL)) {
/* 184 */         RongIM.getInstance().getUnreadCount(new RongIMClient.ResultCallback(counter)
/*     */         {
/*     */           public void onSuccess(Integer integer) {
/* 187 */             this.val$counter.mCount = integer.intValue();
/* 188 */             this.val$counter.onMessageIncreased(integer.intValue());
/*     */           }
/*     */ 
/*     */           public void onError(RongIMClient.ErrorCode e)
/*     */           {
/*     */           }
/*     */         }
/*     */         , new Conversation.ConversationType[0]);
/*     */       }
/* 196 */       else if (counter.getFilter().getLevel().equals(ConversationTypeFilter.Level.CONVERSATION_TYPE))
/*     */       {
/* 198 */         Conversation.ConversationType[] types = (Conversation.ConversationType[])counter.getFilter().getConversationTypeList().toArray(new Conversation.ConversationType[counter.getFilter().getConversationTypeList().size()]);
/* 199 */         RongIM.getInstance().getUnreadCount(types, new RongIMClient.ResultCallback(counter)
/*     */         {
/*     */           public void onSuccess(Integer msgCount)
/*     */           {
/* 203 */             int currentConversationMsgCount = 0;
/* 204 */             List list = RongContext.getInstance().getCurrentConversationList();
/* 205 */             for (ConversationInfo conversationInfo : list) {
/* 206 */               currentConversationMsgCount += RongIM.getInstance().getUnreadCount(conversationInfo.getConversationType(), conversationInfo.getTargetId());
/*     */             }
/*     */ 
/* 209 */             int totalCount = msgCount.intValue() - currentConversationMsgCount;
/* 210 */             this.val$counter.mCount = totalCount;
/* 211 */             this.val$counter.onMessageIncreased(totalCount);
/*     */           }
/*     */ 
/*     */           public void onError(RongIMClient.ErrorCode e)
/*     */           {
/*     */           }
/*     */         });
/*     */       }
/*     */   }
/*     */ 
/*     */   public void onEvent(Event.ConversationRemoveEvent event)
/*     */   {
/* 224 */     this.mContext.getEventBus().post(new Event.ConversationUnreadEvent(event.getType(), event.getTargetId()));
/*     */   }
/*     */ 
/*     */   public void onEvent(Event.ConversationUnreadEvent event) {
/* 228 */     for (Counter counter : this.mCounters)
/* 229 */       if (counter.getFilter().getLevel().equals(ConversationTypeFilter.Level.ALL)) {
/* 230 */         RongIM.getInstance().getUnreadCount(new RongIMClient.ResultCallback(counter)
/*     */         {
/*     */           public void onSuccess(Integer integer) {
/* 233 */             this.val$counter.mCount = integer.intValue();
/* 234 */             this.val$counter.onMessageIncreased(integer.intValue());
/*     */           }
/*     */ 
/*     */           public void onError(RongIMClient.ErrorCode e)
/*     */           {
/*     */           }
/*     */         }
/*     */         , new Conversation.ConversationType[0]);
/*     */       }
/* 242 */       else if (counter.getFilter().getLevel().equals(ConversationTypeFilter.Level.CONVERSATION_TYPE))
/*     */       {
/* 244 */         Conversation.ConversationType[] types = (Conversation.ConversationType[])counter.getFilter().getConversationTypeList().toArray(new Conversation.ConversationType[counter.getFilter().getConversationTypeList().size()]);
/* 245 */         RongIM.getInstance().getUnreadCount(types, new RongIMClient.ResultCallback(counter)
/*     */         {
/*     */           public void onSuccess(Integer integer) {
/* 248 */             int currentConversationMsgCount = 0;
/* 249 */             List list = RongContext.getInstance().getCurrentConversationList();
/* 250 */             for (ConversationInfo conversationInfo : list) {
/* 251 */               currentConversationMsgCount += RongIM.getInstance().getUnreadCount(conversationInfo.getConversationType(), conversationInfo.getTargetId());
/*     */             }
/*     */ 
/* 254 */             int totalCount = integer.intValue() - currentConversationMsgCount;
/* 255 */             this.val$counter.mCount = totalCount;
/* 256 */             this.val$counter.onMessageIncreased(totalCount);
/*     */           }
/*     */ 
/*     */           public void onError(RongIMClient.ErrorCode e)
/*     */           {
/*     */           }
/*     */         });
/*     */       }
/*     */   }
/*     */ 
/*     */   public static class Counter
/*     */   {
/*     */     ConversationTypeFilter mFilter;
/*     */     int mCount;
/*     */ 
/*     */     public Counter(ConversationTypeFilter filter)
/*     */     {
/*  44 */       this.mFilter = filter;
/*     */     }
/*     */ 
/*     */     void onIncreased() {
/*  48 */       onMessageIncreased(++this.mCount);
/*     */     }
/*     */ 
/*     */     public void onMessageIncreased(int count)
/*     */     {
/*     */     }
/*     */ 
/*     */     public ConversationTypeFilter getFilter() {
/*  56 */       return this.mFilter;
/*     */     }
/*     */ 
/*     */     boolean isCount(Message message) {
/*  60 */       return this.mFilter.hasFilter(message);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.notification.MessageCounter
 * JD-Core Version:    0.6.0
 */
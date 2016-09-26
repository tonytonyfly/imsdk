/*     */ package io.rong.imkit.notification;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.common.SystemUtils;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.RongNotificationManager;
/*     */ import io.rong.imkit.model.ConversationKey;
/*     */ import io.rong.imkit.utils.CommonUtils;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.model.Conversation.ConversationNotificationStatus;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.MentionedInfo;
/*     */ import io.rong.imlib.model.MentionedInfo.MentionedType;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import java.util.Calendar;
/*     */ import java.util.List;
/*     */ 
/*     */ public class MessageNotificationManager
/*     */ {
/*     */   private static final String TAG = "MessageNotificationManager";
/*     */ 
/*     */   public static MessageNotificationManager getInstance()
/*     */   {
/*  40 */     return SingletonHolder.instance;
/*     */   }
/*     */ 
/*     */   public void notifyIfNeed(Context context, Message message, int left)
/*     */   {
/*  52 */     if (message.getContent().getMentionedInfo() != null) {
/*  53 */       MentionedInfo mentionedInfo = message.getContent().getMentionedInfo();
/*  54 */       if ((mentionedInfo.getType().equals(MentionedInfo.MentionedType.ALL)) || ((mentionedInfo.getType().equals(MentionedInfo.MentionedType.PART)) && (mentionedInfo.getMentionedUserIdList() != null) && (mentionedInfo.getMentionedUserIdList().contains(RongIMClient.getInstance().getCurrentUserId()))))
/*     */       {
/*  58 */         notify(context, message, left);
/*  59 */         return;
/*     */       }
/*     */     }
/*     */ 
/*  63 */     if (isInQuietTime(context)) {
/*  64 */       return;
/*     */     }
/*     */ 
/*  67 */     if (RongContext.getInstance() != null) {
/*  68 */       ConversationKey key = ConversationKey.obtain(message.getTargetId(), message.getConversationType());
/*  69 */       Conversation.ConversationNotificationStatus notificationStatus = RongContext.getInstance().getConversationNotifyStatusFromCache(key);
/*  70 */       if (notificationStatus != null) {
/*  71 */         if (notificationStatus == Conversation.ConversationNotificationStatus.NOTIFY) {
/*  72 */           notify(context, message, left);
/*     */         }
/*  74 */         return;
/*     */       }
/*     */     }
/*     */ 
/*  78 */     RongIM.getInstance().getConversationNotificationStatus(message.getConversationType(), message.getTargetId(), new RongIMClient.ResultCallback(context, message, left)
/*     */     {
/*     */       public void onSuccess(Conversation.ConversationNotificationStatus status)
/*     */       {
/*  82 */         if (Conversation.ConversationNotificationStatus.NOTIFY == status)
/*  83 */           MessageNotificationManager.getInstance().notify(this.val$context, this.val$message, this.val$left);
/*     */       }
/*     */ 
/*     */       public void onError(RongIMClient.ErrorCode errorCode)
/*     */       {
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void notify(Context context, Message message, int left)
/*     */   {
/*  95 */     boolean isInBackground = !SystemUtils.isAppRunningOnTop(context, context.getPackageName());
/*     */ 
/*  97 */     if (message.getConversationType() == Conversation.ConversationType.CHATROOM) {
/*  98 */       return;
/*     */     }
/*     */ 
/* 101 */     if (isInBackground) {
/* 102 */       RongNotificationManager.getInstance().onReceiveMessageFromApp(message);
/* 103 */     } else if (!CommonUtils.isInConversationPager(message.getTargetId(), message.getConversationType())) {
/* 104 */       MessageTag msgTag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
/* 105 */       if ((msgTag != null) && ((msgTag.flag() & 0x3) == 3))
/* 106 */         MessageSounder.getInstance().messageReminder();
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isInQuietTime(Context context)
/*     */   {
/* 113 */     String startTimeStr = CommonUtils.getNotificationQuietHoursForStartTime(context);
/*     */ 
/* 115 */     int hour = -1;
/* 116 */     int minute = -1;
/* 117 */     int second = -1;
/*     */ 
/* 119 */     if ((!TextUtils.isEmpty(startTimeStr)) && (startTimeStr.contains(":"))) {
/* 120 */       String[] time = startTimeStr.split(":");
/*     */       try
/*     */       {
/* 123 */         if (time.length >= 3) {
/* 124 */           hour = Integer.parseInt(time[0]);
/* 125 */           minute = Integer.parseInt(time[1]);
/* 126 */           second = Integer.parseInt(time[2]);
/*     */         }
/*     */       } catch (NumberFormatException e) {
/* 129 */         RLog.e("MessageNotificationManager", "getConversationNotificationStatus NumberFormatException");
/*     */       }
/*     */     }
/*     */ 
/* 133 */     if ((hour == -1) || (minute == -1) || (second == -1)) {
/* 134 */       return false;
/*     */     }
/*     */ 
/* 137 */     Calendar startCalendar = Calendar.getInstance();
/* 138 */     startCalendar.set(11, hour);
/* 139 */     startCalendar.set(12, minute);
/* 140 */     startCalendar.set(13, second);
/*     */ 
/* 143 */     long spanTime = CommonUtils.getNotificationQuietHoursForSpanMinutes(context) * 60;
/* 144 */     long startTime = startCalendar.getTimeInMillis();
/*     */ 
/* 146 */     Calendar endCalendar = Calendar.getInstance();
/* 147 */     endCalendar.setTimeInMillis(startTime + spanTime * 1000L);
/*     */ 
/* 149 */     Calendar currentCalendar = Calendar.getInstance();
/* 150 */     if (currentCalendar.get(5) == endCalendar.get(5))
/*     */     {
/* 152 */       return (currentCalendar.after(startCalendar)) && (currentCalendar.before(endCalendar));
/*     */     }
/*     */ 
/* 155 */     if (currentCalendar.before(startCalendar))
/*     */     {
/* 157 */       endCalendar.set(5, currentCalendar.get(5));
/*     */ 
/* 159 */       return currentCalendar.before(endCalendar);
/*     */     }
/* 161 */     return true;
/*     */   }
/*     */ 
/*     */   private static class SingletonHolder
/*     */   {
/*  36 */     static final MessageNotificationManager instance = new MessageNotificationManager();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.notification.MessageNotificationManager
 * JD-Core Version:    0.6.0
 */
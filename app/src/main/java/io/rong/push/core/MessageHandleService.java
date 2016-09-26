/*     */ package io.rong.push.core;
/*     */ 
/*     */ import android.app.IntentService;
/*     */ import android.content.Intent;
/*     */ import android.content.SharedPreferences;
/*     */ import android.net.Uri;
/*     */ import android.net.Uri.Builder;
/*     */ import android.os.Bundle;
/*     */ import android.os.Process;
/*     */ import android.text.TextUtils;
/*     */ import com.xiaomi.mipush.sdk.MiPushMessage;
/*     */ import io.rong.push.RongPushClient;
/*     */ import io.rong.push.RongPushClient.ConversationType;
/*     */ import io.rong.push.common.RLog;
/*     */ import io.rong.push.notification.PushMessageReceiver;
/*     */ import io.rong.push.notification.PushNotificationMessage;
/*     */ import io.rong.push.notification.RongNotificationInterface;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class MessageHandleService extends IntentService
/*     */ {
/*     */   private static final String TAG = "MsgHandleService";
/*  28 */   private static ConcurrentLinkedQueue<Job> jobQueue = new ConcurrentLinkedQueue();
/*     */ 
/*     */   public static void addJob(Job job) {
/*  31 */     if (job != null)
/*  32 */       jobQueue.add(job);
/*     */   }
/*     */ 
/*     */   public MessageHandleService()
/*     */   {
/*  37 */     super("MessageHandleThread");
/*     */   }
/*     */ 
/*     */   protected void onHandleIntent(Intent intent) {
/*  41 */     if (intent != null) {
/*  42 */       RLog.i("MsgHandleService", "onHandleIntent " + intent);
/*     */ 
/*  44 */       Job job = (Job)jobQueue.poll();
/*  45 */       if (null == job) {
/*  46 */         RLog.e("MsgHandleService", "Can not find receiver job. Current process id is " + Process.myPid());
/*  47 */         return;
/*     */       }
/*  49 */       Intent deliveredIntent = job.getIntent();
/*  50 */       RLog.d("MsgHandleService", "Handle Job deliveredIntent " + deliveredIntent);
/*  51 */       if ((deliveredIntent == null) || (deliveredIntent.getAction() == null)) {
/*  52 */         RLog.e("MsgHandleService", "Can not find intent in job. Current process id is " + Process.myPid());
/*  53 */         return;
/*     */       }
/*     */ 
/*  56 */       PushMessageReceiver receiver = job.getReceiver();
/*  57 */       Bundle bundle = deliveredIntent.getExtras();
/*  58 */       if (deliveredIntent.getAction().equals("io.rong.push.intent.MESSAGE_ARRIVED")) {
/*  59 */         PushNotificationMessage notificationMessage = decodeNotificationMessage(bundle);
/*  60 */         if ((notificationMessage != null) && 
/*  61 */           (!receiver.onNotificationMessageArrived(this, notificationMessage))) {
/*  62 */           RLog.d("MsgHandleService", "sendNotification");
/*  63 */           RongNotificationInterface.sendNotification(this, notificationMessage);
/*     */         }
/*     */       }
/*  66 */       else if (deliveredIntent.getAction().equals("io.rong.push.intent.MI_MESSAGE_ARRIVED")) { MiPushMessage message = (MiPushMessage)deliveredIntent.getSerializableExtra("message");
/*     */         PushNotificationMessage pushNotificationMessage;
/*     */         try { JSONObject json = new JSONObject(message.getContent());
/*  71 */           pushNotificationMessage = transformToPushMessage(json);
/*     */         } catch (JSONException e) {
/*  73 */           e.printStackTrace();
/*  74 */           return;
/*     */         }
/*  76 */         if (pushNotificationMessage != null)
/*  77 */           receiver.onNotificationMessageArrived(this, pushNotificationMessage);
/*     */       }
/*  79 */       else if (deliveredIntent.getAction().equals("io.rong.push.intent.MESSAGE_CLICKED")) {
/*  80 */         PushNotificationMessage message = (PushNotificationMessage)deliveredIntent.getParcelableExtra("message");
/*  81 */         if (message != null) {
/*  82 */           if (!TextUtils.isEmpty(message.getPushId()))
/*  83 */             RongPushClient.recordNotificationEvent(message.getPushId());
/*  84 */           if (!receiver.onNotificationMessageClicked(this, message)) {
/*  85 */             RongPushClient.ConversationType type = message.getConversationType();
/*  86 */             String objName = message.getObjectName();
/*  87 */             String isFromPush = message.getPushFlag();
/*  88 */             if ((type != null) && (type.equals(RongPushClient.ConversationType.PUSH_SERVICE))) {
/*  89 */               startPushServiceActivity(message.getTargetId(), message.getPushContent(), message.getPushData(), message.getPushId(), message.getExtra(), isFromPush);
/*  90 */             } else if ((objName != null) && ((objName.equals("RC:VCInvite")) || (objName.equals("RC:VCModifyMem")))) {
/*  91 */               startConversationListActivity(isFromPush);
/*     */             } else {
/*  93 */               Boolean isMulti = Boolean.valueOf(deliveredIntent.getBooleanExtra("isMulti", false));
/*  94 */               if (isMulti.booleanValue())
/*  95 */                 startConversationListActivity(isFromPush);
/*     */               else
/*  97 */                 startConversationActivity(message.getConversationType(), message.getTargetId(), message.getTargetUserName(), isFromPush);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 102 */       else if (deliveredIntent.getAction().equals("io.rong.push.intent.MI_MESSAGE_CLICKED")) { MiPushMessage message = (MiPushMessage)deliveredIntent.getSerializableExtra("message");
/*     */         PushNotificationMessage pushNotificationMessage;
/*     */         try { JSONObject json = new JSONObject(message.getContent());
/* 107 */           pushNotificationMessage = transformToPushMessage(json);
/*     */         } catch (JSONException e) {
/* 109 */           e.printStackTrace();
/* 110 */           return;
/*     */         }
/*     */ 
/* 113 */         if (pushNotificationMessage != null) {
/* 114 */           if (!TextUtils.isEmpty(pushNotificationMessage.getPushId()))
/* 115 */             RongPushClient.recordNotificationEvent(pushNotificationMessage.getPushId());
/* 116 */           if (!receiver.onNotificationMessageClicked(this, pushNotificationMessage)) {
/* 117 */             RongPushClient.ConversationType type = pushNotificationMessage.getConversationType();
/* 118 */             if ((type != null) && (type.equals(RongPushClient.ConversationType.PUSH_SERVICE))) {
/* 119 */               startPushServiceActivity(pushNotificationMessage.getTargetId(), pushNotificationMessage.getPushContent(), pushNotificationMessage.getPushData(), pushNotificationMessage.getPushId(), pushNotificationMessage.getExtra(), pushNotificationMessage.getPushFlag());
/*     */             }
/*     */             else
/* 122 */               startConversationListActivity(pushNotificationMessage.getPushFlag());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void startConversationListActivity(String isFromPush)
/*     */   {
/* 131 */     Intent intent = new Intent();
/* 132 */     intent.setFlags(268435456);
/* 133 */     Uri.Builder builder = Uri.parse("rong://" + getPackageName()).buildUpon();
/* 134 */     builder.appendPath("conversationlist");
/* 135 */     builder.appendQueryParameter("isFromPush", isFromPush);
/* 136 */     Uri uri = builder.build();
/* 137 */     intent.setData(uri);
/* 138 */     startActivity(intent);
/*     */   }
/*     */ 
/*     */   private void startConversationActivity(RongPushClient.ConversationType type, String targetId, String targetName, String isFromPush)
/*     */   {
/* 143 */     Intent intent = new Intent();
/* 144 */     intent.setFlags(268435456);
/* 145 */     Uri.Builder builder = Uri.parse("rong://" + getPackageName()).buildUpon();
/*     */ 
/* 147 */     builder.appendPath("conversation").appendPath(type.getName()).appendQueryParameter("targetId", targetId).appendQueryParameter("title", targetName).appendQueryParameter("isFromPush", isFromPush);
/*     */ 
/* 151 */     Uri uri = builder.build();
/* 152 */     intent.setData(uri);
/* 153 */     startActivity(intent);
/*     */   }
/*     */ 
/*     */   private void startPushServiceActivity(String targetId, String pushContent, String pushData, String pushId, String extra, String isFromPush)
/*     */   {
/* 164 */     Intent intent = new Intent();
/* 165 */     intent.setFlags(268435456);
/*     */ 
/* 167 */     Uri.Builder uriBuilder = Uri.parse("rong://" + getPackageName()).buildUpon();
/* 168 */     uriBuilder.appendPath("push_message").appendQueryParameter("targetId", targetId).appendQueryParameter("pushContent", pushContent).appendQueryParameter("pushData", pushData).appendQueryParameter("pushId", pushId).appendQueryParameter("extra", extra).appendQueryParameter("isFromPush", isFromPush);
/*     */ 
/* 175 */     Uri uri = uriBuilder.build();
/* 176 */     intent.setData(uri);
/* 177 */     startActivity(intent);
/*     */   }
/*     */ 
/*     */   private PushNotificationMessage decodeNotificationMessage(Bundle bundle) {
/* 181 */     if (bundle.getInt("conversationType") == 0) {
/* 182 */       RLog.e("MsgHandleService", "onReceive, conversationType is 0");
/* 183 */       return null;
/*     */     }
/*     */ 
/* 186 */     SharedPreferences sp = getSharedPreferences("Statistics", 0);
/* 187 */     String userId = sp.getString("userId", "");
/*     */ 
/* 190 */     RongPushClient.ConversationType conversationType = RongPushClient.ConversationType.setValue(bundle.getInt("conversationType"));
/* 191 */     if ((conversationType != null) && (!conversationType.equals(RongPushClient.ConversationType.PUSH_SERVICE)) && (!conversationType.equals(RongPushClient.ConversationType.SYSTEM)) && (
/* 192 */       (TextUtils.isEmpty(userId)) || (!userId.equals(bundle.getString("toId"))))) {
/* 193 */       RLog.e("MsgHandleService", "The userId isn't matched. Return directly!!");
/* 194 */       return null;
/*     */     }
/*     */ 
/* 198 */     PushNotificationMessage msg = new PushNotificationMessage();
/* 199 */     msg.setReceivedTime(bundle.getLong("receivedTime"));
/* 200 */     msg.setConversationType(conversationType);
/* 201 */     msg.setObjectName(bundle.getString("objectName"));
/* 202 */     msg.setSenderId(bundle.getString("senderId"));
/* 203 */     msg.setSenderName(bundle.getString("senderName"));
/* 204 */     msg.setSenderPortrait(TextUtils.isEmpty(bundle.getString("senderUri")) ? null : Uri.parse(bundle.getString("senderUri")));
/* 205 */     msg.setTargetId(bundle.getString("targetId"));
/* 206 */     msg.setTargetUserName(bundle.getString("targetUserName"));
/*     */ 
/* 208 */     msg.setPushId(bundle.getString("pushId"));
/* 209 */     msg.setPushContent(bundle.getString("pushContent"));
/* 210 */     msg.setPushTitle(bundle.getString("pushTitle"));
/* 211 */     msg.setPushData(bundle.getString("pushData"));
/* 212 */     msg.setExtra(bundle.getString("extra"));
/* 213 */     msg.setPushFlag("true");
/* 214 */     return msg;
/*     */   }
/*     */ 
/*     */   private PushNotificationMessage transformToPushMessage(JSONObject jsonObject) {
/* 218 */     if (jsonObject == null)
/* 219 */       return null;
/* 220 */     PushNotificationMessage pushNotificationMessage = new PushNotificationMessage();
/*     */ 
/* 222 */     String channelType = jsonObject.optString("channelType");
/* 223 */     int typeValue = 0;
/* 224 */     if (!TextUtils.isEmpty(channelType)) {
/*     */       try {
/* 226 */         typeValue = Integer.parseInt(channelType);
/*     */       } catch (NumberFormatException e) {
/* 228 */         e.printStackTrace();
/*     */       }
/*     */     }
/* 231 */     RongPushClient.ConversationType conversationType = RongPushClient.ConversationType.setValue(typeValue);
/* 232 */     pushNotificationMessage.setConversationType(conversationType);
/*     */ 
/* 234 */     if ((conversationType.equals(RongPushClient.ConversationType.DISCUSSION)) || (conversationType.equals(RongPushClient.ConversationType.GROUP)) || (conversationType.equals(RongPushClient.ConversationType.CHATROOM)))
/*     */     {
/* 236 */       pushNotificationMessage.setTargetId(jsonObject.optString("channelId"));
/* 237 */       pushNotificationMessage.setTargetUserName(jsonObject.optString("channelName"));
/*     */     } else {
/* 239 */       pushNotificationMessage.setTargetId(jsonObject.optString("fromUserId"));
/* 240 */       pushNotificationMessage.setTargetUserName(jsonObject.optString("fromUserName"));
/*     */     }
/* 242 */     pushNotificationMessage.setReceivedTime(jsonObject.optLong("timeStamp"));
/* 243 */     pushNotificationMessage.setObjectName(jsonObject.optString("objectName"));
/* 244 */     pushNotificationMessage.setSenderId(jsonObject.optString("fromUserId"));
/* 245 */     pushNotificationMessage.setSenderName(jsonObject.optString("fromUserName"));
/* 246 */     pushNotificationMessage.setSenderPortrait(TextUtils.isEmpty(jsonObject.optString("fromUserPo")) ? null : Uri.parse(jsonObject.optString("fromUserPo")));
/* 247 */     pushNotificationMessage.setPushTitle(jsonObject.optString("title"));
/* 248 */     pushNotificationMessage.setPushContent(jsonObject.optString("content"));
/* 249 */     pushNotificationMessage.setPushData(jsonObject.optString("appData"));
/* 250 */     pushNotificationMessage.setPushFlag("true");
/*     */ 
/* 252 */     String toId = "";
/*     */     try {
/* 254 */       JSONObject temp = jsonObject.getJSONObject("rc");
/* 255 */       toId = temp.optString("tId");
/* 256 */       pushNotificationMessage.setPushId(temp.optString("id"));
/* 257 */       if ((temp.has("ext")) && (temp.getJSONObject("ext") != null))
/* 258 */         pushNotificationMessage.setExtra(temp.getJSONObject("ext").toString());
/*     */     }
/*     */     catch (JSONException e) {
/* 261 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 264 */     SharedPreferences sp = getSharedPreferences("Statistics", 0);
/* 265 */     String userId = sp.getString("userId", "");
/*     */ 
/* 268 */     if ((!conversationType.equals(RongPushClient.ConversationType.PUSH_SERVICE)) && (!conversationType.equals(RongPushClient.ConversationType.SYSTEM)) && (
/* 269 */       (TextUtils.isEmpty(userId)) || (!userId.equals(toId)))) {
/* 270 */       RLog.e("MsgHandleService", "The userId isn't matched. Return directly!!");
/* 271 */       return null;
/*     */     }
/*     */ 
/* 275 */     return pushNotificationMessage;
/*     */   }
/*     */   public static class Job {
/*     */     private PushMessageReceiver receiver;
/*     */     private Intent intent;
/*     */ 
/* 283 */     public Job(Intent intent, PushMessageReceiver receiver) { this.receiver = receiver;
/* 284 */       this.intent = intent; }
/*     */ 
/*     */     public PushMessageReceiver getReceiver()
/*     */     {
/* 288 */       return this.receiver;
/*     */     }
/*     */ 
/*     */     public Intent getIntent() {
/* 292 */       return this.intent;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.core.MessageHandleService
 * JD-Core Version:    0.6.0
 */
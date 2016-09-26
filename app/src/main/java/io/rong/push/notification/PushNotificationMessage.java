/*     */ package io.rong.push.notification;
/*     */ 
/*     */ import android.net.Uri;
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.push.RongPushClient.ConversationType;
/*     */ import io.rong.push.common.ParcelUtils;
/*     */ 
/*     */ public class PushNotificationMessage
/*     */   implements Parcelable
/*     */ {
/*     */   private String pushId;
/*     */   private RongPushClient.ConversationType conversationType;
/*     */   private long receivedTime;
/*     */   private String objectName;
/*     */   private String senderId;
/*     */   private String senderName;
/*     */   private Uri senderPortrait;
/*     */   private String targetId;
/*     */   private String targetUserName;
/*     */   private String pushTitle;
/*     */   private String pushContent;
/*     */   private String pushData;
/*     */   private String extra;
/*     */   private String isFromPush;
/* 233 */   public static final Parcelable.Creator<PushNotificationMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public PushNotificationMessage createFromParcel(Parcel source)
/*     */     {
/* 237 */       return new PushNotificationMessage(source);
/*     */     }
/*     */ 
/*     */     public PushNotificationMessage[] newArray(int size)
/*     */     {
/* 242 */       return new PushNotificationMessage[size];
/*     */     }
/* 233 */   };
/*     */ 
/*     */   public PushNotificationMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setPushId(String id)
/*     */   {
/*  31 */     this.pushId = id;
/*     */   }
/*     */ 
/*     */   public void setConversationType(RongPushClient.ConversationType type) {
/*  35 */     this.conversationType = type;
/*     */   }
/*     */ 
/*     */   public void setReceivedTime(long time) {
/*  39 */     this.receivedTime = time;
/*     */   }
/*     */ 
/*     */   public void setObjectName(String objectName) {
/*  43 */     this.objectName = objectName;
/*     */   }
/*     */ 
/*     */   public void setSenderId(String id) {
/*  47 */     this.senderId = id;
/*     */   }
/*     */ 
/*     */   public void setSenderName(String name) {
/*  51 */     this.senderName = name;
/*     */   }
/*     */ 
/*     */   public void setSenderPortrait(Uri uri) {
/*  55 */     this.senderPortrait = uri;
/*     */   }
/*     */ 
/*     */   public void setTargetId(String id) {
/*  59 */     this.targetId = id;
/*     */   }
/*     */ 
/*     */   public void setTargetUserName(String name) {
/*  63 */     this.targetUserName = name;
/*     */   }
/*     */ 
/*     */   public void setPushTitle(String title) {
/*  67 */     this.pushTitle = title;
/*     */   }
/*     */ 
/*     */   public void setPushContent(String content) {
/*  71 */     this.pushContent = content;
/*     */   }
/*     */ 
/*     */   public void setPushData(String appDataContent) {
/*  75 */     this.pushData = appDataContent;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra) {
/*  79 */     this.extra = extra;
/*     */   }
/*     */ 
/*     */   public void setPushFlag(String value) {
/*  83 */     this.isFromPush = value;
/*     */   }
/*     */ 
/*     */   public String getPushId()
/*     */   {
/*  92 */     return this.pushId;
/*     */   }
/*     */ 
/*     */   public RongPushClient.ConversationType getConversationType()
/*     */   {
/*  99 */     return this.conversationType;
/*     */   }
/*     */ 
/*     */   public String getTargetId()
/*     */   {
/* 107 */     return this.targetId;
/*     */   }
/*     */ 
/*     */   public String getTargetUserName()
/*     */   {
/* 115 */     return this.targetUserName;
/*     */   }
/*     */ 
/*     */   public long getReceivedTime()
/*     */   {
/* 122 */     return this.receivedTime;
/*     */   }
/*     */ 
/*     */   public String getObjectName()
/*     */   {
/* 130 */     return this.objectName;
/*     */   }
/*     */ 
/*     */   public String getSenderId()
/*     */   {
/* 137 */     return this.senderId;
/*     */   }
/*     */ 
/*     */   public String getSenderName()
/*     */   {
/* 144 */     return this.senderName;
/*     */   }
/*     */ 
/*     */   public Uri getSenderPortrait()
/*     */   {
/* 151 */     return this.senderPortrait;
/*     */   }
/*     */ 
/*     */   public String getPushTitle()
/*     */   {
/* 158 */     return this.pushTitle;
/*     */   }
/*     */ 
/*     */   public String getPushContent()
/*     */   {
/* 167 */     return this.pushContent;
/*     */   }
/*     */ 
/*     */   public String getPushData()
/*     */   {
/* 175 */     return this.pushData;
/*     */   }
/*     */ 
/*     */   public String getExtra()
/*     */   {
/* 183 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public String getPushFlag()
/*     */   {
/* 190 */     return this.isFromPush;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 195 */     ParcelUtils.writeToParcel(dest, getPushId());
/* 196 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(getConversationType().getValue()));
/* 197 */     ParcelUtils.writeToParcel(dest, Long.valueOf(getReceivedTime()));
/* 198 */     ParcelUtils.writeToParcel(dest, getObjectName());
/* 199 */     ParcelUtils.writeToParcel(dest, getSenderId());
/* 200 */     ParcelUtils.writeToParcel(dest, getSenderName());
/* 201 */     ParcelUtils.writeToParcel(dest, getSenderPortrait());
/* 202 */     ParcelUtils.writeToParcel(dest, getTargetId());
/* 203 */     ParcelUtils.writeToParcel(dest, getTargetUserName());
/* 204 */     ParcelUtils.writeToParcel(dest, getPushTitle());
/* 205 */     ParcelUtils.writeToParcel(dest, getPushContent());
/* 206 */     ParcelUtils.writeToParcel(dest, getPushData());
/* 207 */     ParcelUtils.writeToParcel(dest, getExtra());
/* 208 */     ParcelUtils.writeToParcel(dest, getPushFlag());
/*     */   }
/*     */ 
/*     */   public PushNotificationMessage(Parcel in) {
/* 212 */     setPushId(ParcelUtils.readFromParcel(in));
/* 213 */     setConversationType(RongPushClient.ConversationType.setValue(ParcelUtils.readIntFromParcel(in).intValue()));
/* 214 */     setReceivedTime(ParcelUtils.readLongFromParcel(in).longValue());
/* 215 */     setObjectName(ParcelUtils.readFromParcel(in));
/* 216 */     setSenderId(ParcelUtils.readFromParcel(in));
/* 217 */     setSenderName(ParcelUtils.readFromParcel(in));
/* 218 */     setSenderPortrait((Uri)ParcelUtils.readFromParcel(in, Uri.class));
/* 219 */     setTargetId(ParcelUtils.readFromParcel(in));
/* 220 */     setTargetUserName(ParcelUtils.readFromParcel(in));
/* 221 */     setPushTitle(ParcelUtils.readFromParcel(in));
/* 222 */     setPushContent(ParcelUtils.readFromParcel(in));
/* 223 */     setPushData(ParcelUtils.readFromParcel(in));
/* 224 */     setExtra(ParcelUtils.readFromParcel(in));
/* 225 */     setPushFlag(ParcelUtils.readFromParcel(in));
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 230 */     return 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.notification.PushNotificationMessage
 * JD-Core Version:    0.6.0
 */
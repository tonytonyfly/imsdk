/*     */ package io.rong.imlib.model;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.ParcelUtils;
/*     */ 
/*     */ public class Conversation
/*     */   implements Parcelable
/*     */ {
/*     */   private ConversationType conversationType;
/*     */   private String targetId;
/*     */   private String conversationTitle;
/*     */   private String portraitUrl;
/*     */   private int unreadMessageCount;
/*     */   private boolean isTop;
/*     */   private Message.ReceivedStatus receivedStatus;
/*     */   private Message.SentStatus sentStatus;
/*     */   private long receivedTime;
/*     */   private long sentTime;
/*     */   private String objectName;
/*     */   private String senderUserId;
/*     */   private String senderUserName;
/*     */   private int latestMessageId;
/*     */   private MessageContent latestMessage;
/*     */   private String draft;
/*     */   private ConversationNotificationStatus notificationStatus;
/*     */   private int mentionedCount;
/* 448 */   public static final Parcelable.Creator<Conversation> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public Conversation createFromParcel(Parcel source)
/*     */     {
/* 452 */       return new Conversation(source);
/*     */     }
/*     */ 
/*     */     public Conversation[] newArray(int size)
/*     */     {
/* 457 */       return new Conversation[size];
/*     */     }
/* 448 */   };
/*     */ 
/*     */   public Conversation()
/*     */   {
/*     */   }
/*     */ 
/*     */   public static Conversation obtain(ConversationType type, String id, String title)
/*     */   {
/*  25 */     Conversation model = new Conversation();
/*  26 */     model.setConversationType(type);
/*  27 */     model.setTargetId(id);
/*  28 */     model.setConversationTitle(title);
/*  29 */     return model;
/*     */   }
/*     */ 
/*     */   public String getPortraitUrl()
/*     */   {
/*  53 */     return this.portraitUrl;
/*     */   }
/*     */ 
/*     */   public void setPortraitUrl(String portraitUrl) {
/*  57 */     this.portraitUrl = portraitUrl;
/*     */   }
/*     */ 
/*     */   public ConversationType getConversationType()
/*     */   {
/*  66 */     return this.conversationType;
/*     */   }
/*     */ 
/*     */   public void setConversationType(ConversationType conversationType)
/*     */   {
/*  75 */     this.conversationType = conversationType;
/*     */   }
/*     */ 
/*     */   public String getTargetId()
/*     */   {
/*  86 */     return this.targetId;
/*     */   }
/*     */ 
/*     */   public void setTargetId(String targetId)
/*     */   {
/*  97 */     this.targetId = targetId;
/*     */   }
/*     */ 
/*     */   public String getConversationTitle()
/*     */   {
/* 106 */     return this.conversationTitle;
/*     */   }
/*     */ 
/*     */   public void setConversationTitle(String conversationTitle)
/*     */   {
/* 115 */     this.conversationTitle = conversationTitle;
/*     */   }
/*     */ 
/*     */   public int getUnreadMessageCount()
/*     */   {
/* 124 */     return this.unreadMessageCount;
/*     */   }
/*     */ 
/*     */   public void setUnreadMessageCount(int unreadMessageCount)
/*     */   {
/* 133 */     this.unreadMessageCount = unreadMessageCount;
/*     */   }
/*     */ 
/*     */   public boolean isTop()
/*     */   {
/* 142 */     return this.isTop;
/*     */   }
/*     */ 
/*     */   public void setTop(boolean isTop)
/*     */   {
/* 151 */     this.isTop = isTop;
/*     */   }
/*     */ 
/*     */   public Message.ReceivedStatus getReceivedStatus()
/*     */   {
/* 160 */     return this.receivedStatus;
/*     */   }
/*     */ 
/*     */   public void setReceivedStatus(Message.ReceivedStatus receivedStatus)
/*     */   {
/* 169 */     this.receivedStatus = receivedStatus;
/*     */   }
/*     */ 
/*     */   public Message.SentStatus getSentStatus()
/*     */   {
/* 178 */     return this.sentStatus;
/*     */   }
/*     */ 
/*     */   public void setSentStatus(Message.SentStatus sentStatus)
/*     */   {
/* 187 */     this.sentStatus = sentStatus;
/*     */   }
/*     */ 
/*     */   public long getReceivedTime()
/*     */   {
/* 196 */     return this.receivedTime;
/*     */   }
/*     */ 
/*     */   public void setReceivedTime(long receivedTime)
/*     */   {
/* 205 */     this.receivedTime = receivedTime;
/*     */   }
/*     */ 
/*     */   public long getSentTime()
/*     */   {
/* 214 */     return this.sentTime;
/*     */   }
/*     */ 
/*     */   public void setSentTime(long sentTime)
/*     */   {
/* 223 */     this.sentTime = sentTime;
/*     */   }
/*     */ 
/*     */   public String getDraft()
/*     */   {
/* 232 */     return this.draft;
/*     */   }
/*     */ 
/*     */   public void setDraft(String draft)
/*     */   {
/* 241 */     this.draft = draft;
/*     */   }
/*     */ 
/*     */   public String getObjectName()
/*     */   {
/* 253 */     return this.objectName;
/*     */   }
/*     */ 
/*     */   public void setObjectName(String objectName)
/*     */   {
/* 265 */     this.objectName = objectName;
/*     */   }
/*     */ 
/*     */   public int getLatestMessageId()
/*     */   {
/* 274 */     return this.latestMessageId;
/*     */   }
/*     */ 
/*     */   public void setLatestMessageId(int latestMessageId)
/*     */   {
/* 283 */     this.latestMessageId = latestMessageId;
/*     */   }
/*     */ 
/*     */   public MessageContent getLatestMessage()
/*     */   {
/* 292 */     return this.latestMessage;
/*     */   }
/*     */ 
/*     */   public void setLatestMessage(MessageContent latestMessage)
/*     */   {
/* 301 */     this.latestMessage = latestMessage;
/*     */   }
/*     */ 
/*     */   public String getSenderUserId()
/*     */   {
/* 310 */     return this.senderUserId;
/*     */   }
/*     */ 
/*     */   public void setSenderUserId(String senderUserId)
/*     */   {
/* 319 */     this.senderUserId = senderUserId;
/*     */   }
/*     */ 
/*     */   public String getSenderUserName()
/*     */   {
/* 328 */     return this.senderUserName;
/*     */   }
/*     */ 
/*     */   public void setSenderUserName(String senderUserName)
/*     */   {
/* 337 */     this.senderUserName = senderUserName;
/*     */   }
/*     */ 
/*     */   public ConversationNotificationStatus getNotificationStatus()
/*     */   {
/* 346 */     return this.notificationStatus;
/*     */   }
/*     */ 
/*     */   public void setNotificationStatus(ConversationNotificationStatus notificationStatus)
/*     */   {
/* 356 */     this.notificationStatus = notificationStatus;
/*     */   }
/*     */ 
/*     */   public void setMentionedCount(int id)
/*     */   {
/* 364 */     this.mentionedCount = id;
/*     */   }
/*     */ 
/*     */   public int getMentionedCount()
/*     */   {
/* 371 */     return this.mentionedCount;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 376 */     return 0;
/*     */   }
/*     */ 
/*     */   public Conversation(Parcel in)
/*     */   {
/* 381 */     String className = ParcelUtils.readFromParcel(in);
/*     */ 
/* 384 */     setConversationType(ConversationType.setValue(ParcelUtils.readIntFromParcel(in).intValue()));
/* 385 */     setTargetId(ParcelUtils.readFromParcel(in));
/* 386 */     setConversationTitle(ParcelUtils.readFromParcel(in));
/* 387 */     setUnreadMessageCount(ParcelUtils.readIntFromParcel(in).intValue());
/* 388 */     setTop(ParcelUtils.readIntFromParcel(in).intValue() == 1);
/* 389 */     setLatestMessageId(ParcelUtils.readIntFromParcel(in).intValue());
/* 390 */     setReceivedStatus(new Message.ReceivedStatus(ParcelUtils.readIntFromParcel(in).intValue()));
/* 391 */     setSentStatus(Message.SentStatus.setValue(ParcelUtils.readIntFromParcel(in).intValue()));
/* 392 */     setReceivedTime(ParcelUtils.readLongFromParcel(in).longValue());
/* 393 */     setSentTime(ParcelUtils.readLongFromParcel(in).longValue());
/* 394 */     setObjectName(ParcelUtils.readFromParcel(in));
/* 395 */     setSenderUserId(ParcelUtils.readFromParcel(in));
/* 396 */     setSenderUserName(ParcelUtils.readFromParcel(in));
/*     */ 
/* 398 */     if (!TextUtils.isEmpty(className)) {
/* 399 */       Class loader = null;
/*     */       try {
/* 401 */         loader = Class.forName(className);
/* 402 */         setLatestMessage((MessageContent)ParcelUtils.readFromParcel(in, loader));
/*     */       } catch (ClassNotFoundException e) {
/* 404 */         e.printStackTrace();
/*     */       }
/*     */     } else {
/* 407 */       setLatestMessage((MessageContent)ParcelUtils.readFromParcel(in, MessageContent.class));
/*     */     }
/* 409 */     setDraft(ParcelUtils.readFromParcel(in));
/* 410 */     setPortraitUrl(ParcelUtils.readFromParcel(in));
/* 411 */     int status = ParcelUtils.readIntFromParcel(in).intValue();
/* 412 */     if (status != -1) {
/* 413 */       setNotificationStatus(ConversationNotificationStatus.setValue(status));
/*     */     }
/*     */ 
/* 416 */     int mentionedId = ParcelUtils.readIntFromParcel(in).intValue();
/* 417 */     if (mentionedId > 0)
/* 418 */       setMentionedCount(mentionedId);
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 425 */     ParcelUtils.writeToParcel(dest, getLatestMessage() == null ? null : getLatestMessage().getClass().getName());
/*     */ 
/* 427 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(getConversationType().getValue()));
/* 428 */     ParcelUtils.writeToParcel(dest, getTargetId());
/* 429 */     ParcelUtils.writeToParcel(dest, getConversationTitle());
/* 430 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(getUnreadMessageCount()));
/* 431 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(isTop() ? 1 : 0));
/* 432 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(getLatestMessageId()));
/* 433 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(getReceivedStatus() == null ? 0 : getReceivedStatus().getFlag()));
/* 434 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(getSentStatus() == null ? 0 : getSentStatus().getValue()));
/* 435 */     ParcelUtils.writeToParcel(dest, Long.valueOf(getReceivedTime()));
/* 436 */     ParcelUtils.writeToParcel(dest, Long.valueOf(getSentTime()));
/* 437 */     ParcelUtils.writeToParcel(dest, getObjectName());
/* 438 */     ParcelUtils.writeToParcel(dest, getSenderUserId());
/* 439 */     ParcelUtils.writeToParcel(dest, getSenderUserName());
/* 440 */     ParcelUtils.writeToParcel(dest, getLatestMessage());
/* 441 */     ParcelUtils.writeToParcel(dest, getDraft());
/* 442 */     ParcelUtils.writeToParcel(dest, getPortraitUrl());
/* 443 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(getNotificationStatus() == null ? -1 : getNotificationStatus().getValue()));
/* 444 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(getMentionedCount()));
/*     */   }
/*     */ 
/*     */   public static enum ConversationNotificationStatus
/*     */   {
/* 631 */     DO_NOT_DISTURB(0), 
/*     */ 
/* 636 */     NOTIFY(1);
/*     */ 
/* 638 */     private int value = 1;
/*     */ 
/*     */     private ConversationNotificationStatus(int value)
/*     */     {
/* 646 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public int getValue()
/*     */     {
/* 655 */       return this.value;
/*     */     }
/*     */ 
/*     */     public static ConversationNotificationStatus setValue(int code)
/*     */     {
/* 665 */       for (ConversationNotificationStatus c : values()) {
/* 666 */         if (code == c.getValue()) {
/* 667 */           return c;
/*     */         }
/*     */       }
/* 670 */       return NOTIFY;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum ConversationType
/*     */   {
/* 529 */     NONE(0, "none"), 
/*     */ 
/* 533 */     PRIVATE(1, "private"), 
/*     */ 
/* 538 */     DISCUSSION(2, "discussion"), 
/*     */ 
/* 543 */     GROUP(3, "group"), 
/*     */ 
/* 548 */     CHATROOM(4, "chatroom"), 
/*     */ 
/* 553 */     CUSTOMER_SERVICE(5, "customer_service"), 
/*     */ 
/* 558 */     SYSTEM(6, "system"), 
/*     */ 
/* 563 */     APP_PUBLIC_SERVICE(7, "app_public_service"), 
/*     */ 
/* 568 */     PUBLIC_SERVICE(8, "public_service"), 
/*     */ 
/* 573 */     PUSH_SERVICE(9, "push_service");
/*     */ 
/* 576 */     private int value = 1;
/* 577 */     private String name = "";
/*     */ 
/*     */     private ConversationType(int value, String name)
/*     */     {
/* 585 */       this.value = value;
/* 586 */       this.name = name;
/*     */     }
/*     */ 
/*     */     public int getValue()
/*     */     {
/* 595 */       return this.value;
/*     */     }
/*     */ 
/*     */     public String getName()
/*     */     {
/* 604 */       return this.name;
/*     */     }
/*     */ 
/*     */     public static ConversationType setValue(int code)
/*     */     {
/* 614 */       for (ConversationType c : values()) {
/* 615 */         if (code == c.getValue()) {
/* 616 */           return c;
/*     */         }
/*     */       }
/* 619 */       return PRIVATE;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum PublicServiceType
/*     */   {
/* 469 */     APP_PUBLIC_SERVICE(7, "app_public_service"), 
/*     */ 
/* 474 */     PUBLIC_SERVICE(8, "public_service");
/*     */ 
/* 477 */     private int value = 1;
/* 478 */     private String name = "";
/*     */ 
/*     */     private PublicServiceType(int value, String name)
/*     */     {
/* 486 */       this.value = value;
/* 487 */       this.name = name;
/*     */     }
/*     */ 
/*     */     public int getValue()
/*     */     {
/* 496 */       return this.value;
/*     */     }
/*     */ 
/*     */     public String getName()
/*     */     {
/* 505 */       return this.name;
/*     */     }
/*     */ 
/*     */     public static PublicServiceType setValue(int code)
/*     */     {
/* 515 */       for (PublicServiceType c : values()) {
/* 516 */         if (code == c.getValue()) {
/* 517 */           return c;
/*     */         }
/*     */       }
/* 520 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.Conversation
 * JD-Core Version:    0.6.0
 */
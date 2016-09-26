/*     */ package io.rong.imlib.model;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.imlib.NativeObject.Message;
/*     */ 
/*     */ public class Message
/*     */   implements Parcelable
/*     */ {
/*     */   private Conversation.ConversationType conversationType;
/*     */   private String targetId;
/*     */   private int messageId;
/*     */   private MessageDirection messageDirection;
/*     */   private String senderUserId;
/*     */   private ReceivedStatus receivedStatus;
/*     */   private SentStatus sentStatus;
/*     */   private long receivedTime;
/*     */   private long sentTime;
/*     */   private String objectName;
/*     */   private MessageContent content;
/*     */   private String extra;
/*     */   private ReadReceiptInfo readReceiptInfo;
/*     */   private String UId;
/* 635 */   public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public Message createFromParcel(Parcel source)
/*     */     {
/* 639 */       return new Message(source);
/*     */     }
/*     */ 
/*     */     public Message[] newArray(int size)
/*     */     {
/* 644 */       return new Message[size];
/*     */     }
/* 635 */   };
/*     */ 
/*     */   public String getUId()
/*     */   {
/*  37 */     return this.UId;
/*     */   }
/*     */ 
/*     */   public void setUId(String UId)
/*     */   {
/*  46 */     this.UId = UId;
/*     */   }
/*     */ 
/*     */   public Message()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Message(NativeObject.Message msg)
/*     */   {
/*  64 */     this.conversationType = Conversation.ConversationType.setValue(msg.getConversationType());
/*  65 */     this.targetId = msg.getTargetId();
/*  66 */     this.messageId = msg.getMessageId();
/*  67 */     this.messageDirection = (!msg.getMessageDirection() ? MessageDirection.SEND : MessageDirection.RECEIVE);
/*  68 */     this.senderUserId = msg.getSenderUserId();
/*  69 */     this.receivedStatus = new ReceivedStatus(msg.getReadStatus());
/*  70 */     this.sentStatus = SentStatus.setValue(msg.getSentStatus());
/*  71 */     this.receivedTime = msg.getReceivedTime();
/*  72 */     this.sentTime = msg.getSentTime();
/*  73 */     this.objectName = msg.getObjectName();
/*  74 */     this.UId = msg.getUId();
/*  75 */     this.extra = msg.getExtra();
/*  76 */     this.readReceiptInfo = new ReadReceiptInfo(msg.getReadReceiptInfo());
/*     */   }
/*     */ 
/*     */   public static Message obtain(String targetId, Conversation.ConversationType type, MessageContent content) {
/*  80 */     Message obj = new Message();
/*  81 */     obj.setTargetId(targetId);
/*  82 */     obj.setConversationType(type);
/*  83 */     obj.setContent(content);
/*     */ 
/*  85 */     return obj;
/*     */   }
/*     */ 
/*     */   public Conversation.ConversationType getConversationType()
/*     */   {
/*  95 */     return this.conversationType;
/*     */   }
/*     */ 
/*     */   public void setConversationType(Conversation.ConversationType conversationType)
/*     */   {
/* 104 */     this.conversationType = conversationType;
/*     */   }
/*     */ 
/*     */   public String getTargetId()
/*     */   {
/* 115 */     return this.targetId;
/*     */   }
/*     */ 
/*     */   public void setTargetId(String targetId)
/*     */   {
/* 127 */     this.targetId = targetId;
/*     */   }
/*     */ 
/*     */   public int getMessageId()
/*     */   {
/* 136 */     return this.messageId;
/*     */   }
/*     */ 
/*     */   public void setMessageId(int messageId)
/*     */   {
/* 145 */     this.messageId = messageId;
/*     */   }
/*     */ 
/*     */   public MessageDirection getMessageDirection()
/*     */   {
/* 154 */     return this.messageDirection;
/*     */   }
/*     */ 
/*     */   public void setMessageDirection(MessageDirection messageDirection)
/*     */   {
/* 163 */     this.messageDirection = messageDirection;
/*     */   }
/*     */ 
/*     */   public ReceivedStatus getReceivedStatus()
/*     */   {
/* 172 */     return this.receivedStatus;
/*     */   }
/*     */ 
/*     */   public void setReceivedStatus(ReceivedStatus receivedStatus)
/*     */   {
/* 181 */     this.receivedStatus = receivedStatus;
/*     */   }
/*     */ 
/*     */   public SentStatus getSentStatus()
/*     */   {
/* 190 */     return this.sentStatus;
/*     */   }
/*     */ 
/*     */   public void setSentStatus(SentStatus sentStatus)
/*     */   {
/* 199 */     this.sentStatus = sentStatus;
/*     */   }
/*     */ 
/*     */   public long getReceivedTime()
/*     */   {
/* 210 */     return this.receivedTime;
/*     */   }
/*     */ 
/*     */   public void setReceivedTime(long receivedTime)
/*     */   {
/* 221 */     this.receivedTime = receivedTime;
/*     */   }
/*     */ 
/*     */   public long getSentTime()
/*     */   {
/* 232 */     return this.sentTime;
/*     */   }
/*     */ 
/*     */   public void setSentTime(long sentTime)
/*     */   {
/* 243 */     this.sentTime = sentTime;
/*     */   }
/*     */ 
/*     */   public String getObjectName()
/*     */   {
/* 255 */     return this.objectName;
/*     */   }
/*     */ 
/*     */   public void setObjectName(String objectName)
/*     */   {
/* 267 */     this.objectName = objectName;
/*     */   }
/*     */ 
/*     */   public MessageContent getContent()
/*     */   {
/* 276 */     return this.content;
/*     */   }
/*     */ 
/*     */   public void setContent(MessageContent content)
/*     */   {
/* 285 */     this.content = content;
/*     */   }
/*     */ 
/*     */   public String getExtra()
/*     */   {
/* 294 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra)
/*     */   {
/* 303 */     this.extra = extra;
/*     */   }
/*     */ 
/*     */   public String getSenderUserId()
/*     */   {
/* 312 */     return this.senderUserId;
/*     */   }
/*     */ 
/*     */   public void setSenderUserId(String senderUserId)
/*     */   {
/* 321 */     this.senderUserId = senderUserId;
/*     */   }
/*     */ 
/*     */   public ReadReceiptInfo getReadReceiptInfo() {
/* 325 */     return this.readReceiptInfo;
/*     */   }
/*     */ 
/*     */   public void setReadReceiptInfo(ReadReceiptInfo readReceiptInfo) {
/* 329 */     this.readReceiptInfo = readReceiptInfo;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 582 */     return 0;
/*     */   }
/*     */ 
/*     */   public Message(Parcel in) {
/* 586 */     String className = ParcelUtils.readFromParcel(in);
/*     */ 
/* 588 */     Class loader = null;
/* 589 */     if (className != null) {
/*     */       try {
/* 591 */         loader = Class.forName(className);
/*     */       } catch (ClassNotFoundException e) {
/* 593 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */ 
/* 597 */     setTargetId(ParcelUtils.readFromParcel(in));
/* 598 */     setMessageId(ParcelUtils.readIntFromParcel(in).intValue());
/* 599 */     setSenderUserId(ParcelUtils.readFromParcel(in));
/* 600 */     setReceivedTime(ParcelUtils.readLongFromParcel(in).longValue());
/* 601 */     setSentTime(ParcelUtils.readLongFromParcel(in).longValue());
/* 602 */     setObjectName(ParcelUtils.readFromParcel(in));
/* 603 */     setContent((MessageContent)ParcelUtils.readFromParcel(in, loader));
/* 604 */     setExtra(ParcelUtils.readFromParcel(in));
/* 605 */     setUId(ParcelUtils.readFromParcel(in));
/*     */ 
/* 607 */     setConversationType(Conversation.ConversationType.setValue(ParcelUtils.readIntFromParcel(in).intValue()));
/* 608 */     setMessageDirection(MessageDirection.setValue(ParcelUtils.readIntFromParcel(in).intValue()));
/* 609 */     setReceivedStatus(new ReceivedStatus(ParcelUtils.readIntFromParcel(in).intValue()));
/* 610 */     setSentStatus(SentStatus.setValue(ParcelUtils.readIntFromParcel(in).intValue()));
/* 611 */     setReadReceiptInfo((ReadReceiptInfo)ParcelUtils.readFromParcel(in, ReadReceiptInfo.class));
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 616 */     ParcelUtils.writeToParcel(dest, getContent() != null ? getContent().getClass().getName() : null);
/* 617 */     ParcelUtils.writeToParcel(dest, getTargetId());
/* 618 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(getMessageId()));
/* 619 */     ParcelUtils.writeToParcel(dest, getSenderUserId());
/* 620 */     ParcelUtils.writeToParcel(dest, Long.valueOf(getReceivedTime()));
/* 621 */     ParcelUtils.writeToParcel(dest, Long.valueOf(getSentTime()));
/* 622 */     ParcelUtils.writeToParcel(dest, getObjectName());
/* 623 */     ParcelUtils.writeToParcel(dest, getContent());
/* 624 */     ParcelUtils.writeToParcel(dest, getExtra());
/* 625 */     ParcelUtils.writeToParcel(dest, getUId());
/*     */ 
/* 627 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(getConversationType().getValue()));
/* 628 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(getMessageDirection() == null ? 0 : getMessageDirection().getValue()));
/* 629 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(getReceivedStatus() == null ? 0 : getReceivedStatus().getFlag()));
/* 630 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(getSentStatus() == null ? 0 : getSentStatus().getValue()));
/* 631 */     ParcelUtils.writeToParcel(dest, getReadReceiptInfo());
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 651 */     if (o == null) {
/* 652 */       return false;
/*     */     }
/* 654 */     if ((o instanceof Message)) {
/* 655 */       return this.messageId == ((Message)o).getMessageId();
/*     */     }
/*     */ 
/* 658 */     return super.equals(o);
/*     */   }
/*     */ 
/*     */   public static class ReceivedStatus
/*     */   {
/*     */     private static final int READ = 1;
/*     */     private static final int LISTENED = 2;
/*     */     private static final int DOWNLOADED = 4;
/*     */     private static final int RETRIEVED = 8;
/*     */     private static final int MULTIPLERECEIVE = 16;
/* 464 */     private int flag = 0;
/* 465 */     private boolean isRead = false;
/* 466 */     private boolean isListened = false;
/* 467 */     private boolean isDownload = false;
/* 468 */     private boolean isRetrieved = false;
/* 469 */     private boolean isMultipleReceive = false;
/*     */ 
/*     */     public ReceivedStatus(int flag)
/*     */     {
/* 477 */       this.flag = flag;
/* 478 */       this.isRead = ((flag & 0x1) == 1);
/* 479 */       this.isListened = ((flag & 0x2) == 2);
/* 480 */       this.isDownload = ((flag & 0x4) == 4);
/* 481 */       this.isRetrieved = ((flag & 0x8) == 8);
/* 482 */       this.isMultipleReceive = ((flag & 0x10) == 16);
/*     */     }
/*     */ 
/*     */     public int getFlag()
/*     */     {
/* 491 */       return this.flag;
/*     */     }
/*     */ 
/*     */     public boolean isRead()
/*     */     {
/* 500 */       return this.isRead;
/*     */     }
/*     */ 
/*     */     public void setRead()
/*     */     {
/* 507 */       this.flag |= 1;
/* 508 */       this.isRead = true;
/*     */     }
/*     */ 
/*     */     public boolean isListened()
/*     */     {
/* 517 */       return this.isListened;
/*     */     }
/*     */ 
/*     */     public void setListened()
/*     */     {
/* 524 */       this.flag |= 2;
/* 525 */       this.isListened = true;
/*     */     }
/*     */ 
/*     */     public boolean isDownload()
/*     */     {
/* 534 */       return this.isDownload;
/*     */     }
/*     */ 
/*     */     public void setDownload()
/*     */     {
/* 541 */       this.flag |= 4;
/* 542 */       this.isDownload = true;
/*     */     }
/*     */ 
/*     */     public boolean isRetrieved()
/*     */     {
/* 551 */       return this.isRetrieved;
/*     */     }
/*     */ 
/*     */     public void setRetrieved()
/*     */     {
/* 558 */       this.flag |= 8;
/* 559 */       this.isRetrieved = true;
/*     */     }
/*     */ 
/*     */     public boolean isMultipleReceive()
/*     */     {
/* 568 */       return this.isMultipleReceive;
/*     */     }
/*     */ 
/*     */     public void setMultipleReceive()
/*     */     {
/* 575 */       this.flag |= 16;
/* 576 */       this.isMultipleReceive = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum SentStatus
/*     */   {
/* 390 */     SENDING(10), 
/*     */ 
/* 395 */     FAILED(20), 
/*     */ 
/* 400 */     SENT(30), 
/*     */ 
/* 405 */     RECEIVED(40), 
/*     */ 
/* 410 */     READ(50), 
/*     */ 
/* 415 */     DESTROYED(60);
/*     */ 
/* 417 */     private int value = 1;
/*     */ 
/*     */     private SentStatus(int value)
/*     */     {
/* 425 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public int getValue()
/*     */     {
/* 434 */       return this.value;
/*     */     }
/*     */ 
/*     */     public static SentStatus setValue(int code)
/*     */     {
/* 444 */       for (SentStatus c : values()) {
/* 445 */         if (code == c.getValue()) {
/* 446 */           return c;
/*     */         }
/*     */       }
/* 449 */       return SENDING;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum MessageDirection
/*     */   {
/* 339 */     SEND(1), 
/*     */ 
/* 344 */     RECEIVE(2);
/*     */ 
/* 346 */     private int value = 1;
/*     */ 
/*     */     private MessageDirection(int value)
/*     */     {
/* 354 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public int getValue()
/*     */     {
/* 363 */       return this.value;
/*     */     }
/*     */ 
/*     */     public static MessageDirection setValue(int code)
/*     */     {
/* 373 */       for (MessageDirection c : values()) {
/* 374 */         if (code == c.getValue()) {
/* 375 */           return c;
/*     */         }
/*     */       }
/* 378 */       return SEND;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.Message
 * JD-Core Version:    0.6.0
 */
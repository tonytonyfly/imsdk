/*     */ package io.rong.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag("RC:ReadNtf")
/*     */ public class ReadReceiptMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "ReadReceiptMessage";
/*     */   private long lastMessageSendTime;
/*     */   private String messageUId;
/*     */   private ReadReceiptType type;
/* 191 */   public static final Parcelable.Creator<ReadReceiptMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public ReadReceiptMessage createFromParcel(Parcel source) {
/* 194 */       return new ReadReceiptMessage(source);
/*     */     }
/*     */ 
/*     */     public ReadReceiptMessage[] newArray(int size)
/*     */     {
/* 199 */       return new ReadReceiptMessage[size];
/*     */     }
/* 191 */   };
/*     */ 
/*     */   public long getLastMessageSendTime()
/*     */   {
/*  42 */     return this.lastMessageSendTime;
/*     */   }
/*     */ 
/*     */   public void setLastMessageSendTime(long lastMessageSendTime) {
/*  46 */     this.lastMessageSendTime = lastMessageSendTime;
/*     */   }
/*     */ 
/*     */   public String getMessageUId() {
/*  50 */     return this.messageUId;
/*     */   }
/*     */ 
/*     */   public void setMessageUId(String messageUId) {
/*  54 */     this.messageUId = messageUId;
/*     */   }
/*     */ 
/*     */   public ReadReceiptType getType() {
/*  58 */     return this.type;
/*     */   }
/*     */ 
/*     */   public void setType(ReadReceiptType type) {
/*  62 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public ReadReceiptMessage(long sendTime)
/*     */   {
/*  90 */     setLastMessageSendTime(sendTime);
/*  91 */     setType(ReadReceiptType.SEND_TIME);
/*     */   }
/*     */ 
/*     */   public ReadReceiptMessage(String uId) {
/*  95 */     setMessageUId(uId);
/*  96 */     setType(ReadReceiptType.UID);
/*     */   }
/*     */ 
/*     */   public ReadReceiptMessage(long sendTime, String uId, ReadReceiptType type) {
/* 100 */     setLastMessageSendTime(sendTime);
/* 101 */     setMessageUId(uId);
/* 102 */     setType(type);
/*     */   }
/*     */ 
/*     */   public ReadReceiptMessage(Parcel in) {
/* 106 */     setLastMessageSendTime(ParcelUtils.readLongFromParcel(in).longValue());
/* 107 */     setMessageUId(ParcelUtils.readFromParcel(in));
/* 108 */     setType(ReadReceiptType.setValue(ParcelUtils.readIntFromParcel(in).intValue()));
/*     */   }
/*     */ 
/*     */   public ReadReceiptMessage(byte[] data) {
/* 112 */     String jsonStr = null;
/*     */     try
/*     */     {
/* 115 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 117 */       RLog.e("ReadReceiptMessage", e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 121 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*     */ 
/* 123 */       if (jsonObj.has("lastMessageSendTime")) {
/* 124 */         setLastMessageSendTime(jsonObj.getLong("lastMessageSendTime"));
/*     */       }
/* 126 */       if (jsonObj.has("messageUId")) {
/* 127 */         setMessageUId(jsonObj.getString("messageUId"));
/*     */       }
/* 129 */       if (jsonObj.has("type"))
/* 130 */         setType(ReadReceiptType.setValue(jsonObj.getInt("type")));
/*     */     }
/*     */     catch (JSONException e) {
/* 133 */       RLog.e("ReadReceiptMessage", e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/* 139 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/* 142 */       jsonObj.put("lastMessageSendTime", getLastMessageSendTime());
/* 143 */       if (!TextUtils.isEmpty(getMessageUId())) {
/* 144 */         jsonObj.put("messageUId", getMessageUId());
/*     */       }
/* 146 */       jsonObj.put("type", getType().getValue());
/*     */     } catch (JSONException e) {
/* 148 */       RLog.e("ReadReceiptMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 152 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/* 155 */       e.printStackTrace();
/*     */     }
/* 157 */     return null;
/*     */   }
/*     */ 
/*     */   private ReadReceiptMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public static ReadReceiptMessage obtain(long sendTime)
/*     */   {
/* 170 */     ReadReceiptMessage obj = new ReadReceiptMessage();
/* 171 */     obj.setLastMessageSendTime(sendTime);
/* 172 */     obj.setType(ReadReceiptType.SEND_TIME);
/* 173 */     return obj;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 178 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 183 */     ParcelUtils.writeToParcel(dest, Long.valueOf(getLastMessageSendTime()));
/* 184 */     ParcelUtils.writeToParcel(dest, getMessageUId());
/* 185 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(getType().getValue()));
/*     */   }
/*     */ 
/*     */   public static enum ReadReceiptType
/*     */   {
/*  66 */     SEND_TIME(1), 
/*  67 */     UID(2);
/*     */ 
/*  69 */     private int value = 0;
/*     */ 
/*     */     private ReadReceiptType(int value) {
/*  72 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public int getValue() {
/*  76 */       return this.value;
/*     */     }
/*     */ 
/*     */     public static ReadReceiptType setValue(int code) {
/*  80 */       for (ReadReceiptType c : values()) {
/*  81 */         if (code == c.getValue()) {
/*  82 */           return c;
/*     */         }
/*     */       }
/*  85 */       return SEND_TIME;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.ReadReceiptMessage
 * JD-Core Version:    0.6.0
 */
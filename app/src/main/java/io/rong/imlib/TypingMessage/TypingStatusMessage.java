/*     */ package io.rong.imlib.TypingMessage;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.message.StatusMessage;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:TypSts", flag=16)
/*     */ public class TypingStatusMessage extends StatusMessage
/*     */ {
/*     */   private static final String TAG = "TypingStatusMessage";
/*     */   private String typingContentType;
/*     */   private String data;
/* 123 */   public static final Parcelable.Creator<TypingStatusMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public TypingStatusMessage createFromParcel(Parcel source) {
/* 126 */       return new TypingStatusMessage(source);
/*     */     }
/*     */ 
/*     */     public TypingStatusMessage[] newArray(int size)
/*     */     {
/* 131 */       return new TypingStatusMessage[size];
/*     */     }
/* 123 */   };
/*     */ 
/*     */   public String getTypingContentType()
/*     */   {
/*  27 */     return this.typingContentType;
/*     */   }
/*     */ 
/*     */   public void setTypingContentType(String typingContentType) {
/*  31 */     this.typingContentType = typingContentType;
/*     */   }
/*     */ 
/*     */   public String getData() {
/*  35 */     return this.data;
/*     */   }
/*     */ 
/*     */   public void setData(String data) {
/*  39 */     this.data = data;
/*     */   }
/*     */ 
/*     */   public TypingStatusMessage(String type, String data) {
/*  43 */     setTypingContentType(type);
/*  44 */     setData(data);
/*     */   }
/*     */ 
/*     */   public TypingStatusMessage(byte[] data) {
/*  48 */     String jsonStr = null;
/*     */ 
/*  50 */     if ((data == null) || (data.length == 0))
/*  51 */       return;
/*     */     try
/*     */     {
/*  54 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  56 */       RLog.e("TypingStatusMessage", "TypingStatusMessage " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/*  60 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*     */ 
/*  62 */       if (jsonObj.has("typingContentType")) {
/*  63 */         setTypingContentType(jsonObj.getString("typingContentType"));
/*     */       }
/*  65 */       if (jsonObj.has("data"))
/*  66 */         setData(jsonObj.getString("data"));
/*     */     }
/*     */     catch (JSONException e) {
/*  69 */       RLog.e("TypingStatusMessage", "TypingStatusMessage " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public TypingStatusMessage(Parcel in) {
/*  74 */     setTypingContentType(ParcelUtils.readFromParcel(in));
/*  75 */     setData(ParcelUtils.readFromParcel(in));
/*     */   }
/*     */ 
/*     */   public TypingStatusMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  88 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/*  91 */       jsonObj.put("typingContentType", getTypingContentType());
/*  92 */       if (!TextUtils.isEmpty(getData()))
/*  93 */         jsonObj.put("data", getData());
/*     */     }
/*     */     catch (JSONException e) {
/*  96 */       RLog.e("TypingStatusMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 100 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 102 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 105 */     return null;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 111 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 116 */     ParcelUtils.writeToParcel(dest, getTypingContentType());
/* 117 */     ParcelUtils.writeToParcel(dest, getData());
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.TypingMessage.TypingStatusMessage
 * JD-Core Version:    0.6.0
 */
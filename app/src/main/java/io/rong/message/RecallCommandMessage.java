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
/*     */ @MessageTag("RC:RcCmd")
/*     */ public class RecallCommandMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "RecallCommandMessage";
/*     */   private String messageUId;
/*     */   private String extra;
/* 131 */   public static final Parcelable.Creator<RecallCommandMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public RecallCommandMessage createFromParcel(Parcel source) {
/* 134 */       return new RecallCommandMessage(source);
/*     */     }
/*     */ 
/*     */     public RecallCommandMessage[] newArray(int size)
/*     */     {
/* 139 */       return new RecallCommandMessage[size];
/*     */     }
/* 131 */   };
/*     */ 
/*     */   public String getMessageUId()
/*     */   {
/*  36 */     return this.messageUId;
/*     */   }
/*     */ 
/*     */   public void setMessageUId(String messageUId) {
/*  40 */     this.messageUId = messageUId;
/*     */   }
/*     */ 
/*     */   public String getExtra() {
/*  44 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra) {
/*  48 */     this.extra = extra;
/*     */   }
/*     */ 
/*     */   public RecallCommandMessage(String UId) {
/*  52 */     setMessageUId(UId);
/*     */   }
/*     */ 
/*     */   public RecallCommandMessage(String UId, String ex) {
/*  56 */     setMessageUId(UId);
/*  57 */     setExtra(ex);
/*     */   }
/*     */ 
/*     */   public RecallCommandMessage(byte[] data) {
/*  61 */     String jsonStr = null;
/*     */     try
/*     */     {
/*  64 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  66 */       RLog.e("RecallCommandMessage", e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/*  70 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*     */ 
/*  72 */       if (jsonObj.has("messageUId")) {
/*  73 */         setMessageUId(jsonObj.getString("messageUId"));
/*     */       }
/*  75 */       if (jsonObj.has("extra"))
/*  76 */         setExtra(jsonObj.getString("extra"));
/*     */     }
/*     */     catch (JSONException e) {
/*  79 */       RLog.e("RecallCommandMessage", e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public RecallCommandMessage(Parcel in) {
/*  84 */     setMessageUId(ParcelUtils.readFromParcel(in));
/*  85 */     setExtra(ParcelUtils.readFromParcel(in));
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  95 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/*  98 */       if (!TextUtils.isEmpty(getMessageUId())) {
/*  99 */         jsonObj.put("messageUId", getMessageUId());
/*     */       }
/* 101 */       if (!TextUtils.isEmpty(getExtra()))
/* 102 */         jsonObj.put("extra", getExtra());
/*     */     }
/*     */     catch (JSONException e) {
/* 105 */       RLog.e("RecallCommandMessage", e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 109 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 111 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 114 */     return null;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 119 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 124 */     ParcelUtils.writeToParcel(dest, getMessageUId());
/* 125 */     ParcelUtils.writeToParcel(dest, getExtra());
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.RecallCommandMessage
 * JD-Core Version:    0.6.0
 */
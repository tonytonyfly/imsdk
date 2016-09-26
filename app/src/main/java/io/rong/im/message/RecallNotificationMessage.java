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
/*     */ @MessageTag(value="RC:RcNtf", flag=1)
/*     */ public class RecallNotificationMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "RecallNotificationMessage";
/*     */   private String mOperatorId;
/*     */   private long mRecallTime;
/*     */   private String mOriginalObjectName;
/* 134 */   public static final Parcelable.Creator<RecallNotificationMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public RecallNotificationMessage createFromParcel(Parcel source) {
/* 137 */       return new RecallNotificationMessage(source);
/*     */     }
/*     */ 
/*     */     public RecallNotificationMessage[] newArray(int size)
/*     */     {
/* 142 */       return new RecallNotificationMessage[size];
/*     */     }
/* 134 */   };
/*     */ 
/*     */   public String getOperatorId()
/*     */   {
/*  40 */     return this.mOperatorId;
/*     */   }
/*     */ 
/*     */   public long getRecallTime() {
/*  44 */     return this.mRecallTime;
/*     */   }
/*     */ 
/*     */   public String getOriginalObjectName() {
/*  48 */     return this.mOriginalObjectName;
/*     */   }
/*     */ 
/*     */   public RecallNotificationMessage(String operatorId, long recallTime, String originalObjectName) {
/*  52 */     this.mOperatorId = operatorId;
/*  53 */     this.mRecallTime = recallTime;
/*  54 */     this.mOriginalObjectName = originalObjectName;
/*     */   }
/*     */ 
/*     */   public RecallNotificationMessage(byte[] data) {
/*  58 */     String jsonStr = null;
/*     */     try
/*     */     {
/*  61 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  63 */       RLog.e("RecallNotificationMessage", e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/*  67 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*     */ 
/*  69 */       if (jsonObj.has("operatorId")) {
/*  70 */         this.mOperatorId = jsonObj.getString("operatorId");
/*     */       }
/*  72 */       if (jsonObj.has("recallTime")) {
/*  73 */         this.mRecallTime = jsonObj.getLong("recallTime");
/*     */       }
/*  75 */       if (jsonObj.has("originalObjectName"))
/*  76 */         this.mOriginalObjectName = jsonObj.getString("originalObjectName");
/*     */     }
/*     */     catch (JSONException e) {
/*  79 */       RLog.e("RecallNotificationMessage", e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public RecallNotificationMessage(Parcel in) {
/*  84 */     this.mOperatorId = ParcelUtils.readFromParcel(in);
/*  85 */     this.mRecallTime = ParcelUtils.readLongFromParcel(in).longValue();
/*  86 */     this.mOriginalObjectName = ParcelUtils.readFromParcel(in);
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  96 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/*  99 */       if (!TextUtils.isEmpty(getOperatorId())) {
/* 100 */         jsonObj.put("operatorId", getOperatorId());
/*     */       }
/* 102 */       jsonObj.put("recallTime", getRecallTime());
/* 103 */       if (!TextUtils.isEmpty(getOriginalObjectName()))
/* 104 */         jsonObj.put("originalObjectName", getOriginalObjectName());
/*     */     }
/*     */     catch (JSONException e) {
/* 107 */       RLog.e("RecallNotificationMessage", e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 111 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 113 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 116 */     return null;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 121 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 126 */     ParcelUtils.writeToParcel(dest, getOperatorId());
/* 127 */     ParcelUtils.writeToParcel(dest, Long.valueOf(getRecallTime()));
/* 128 */     ParcelUtils.writeToParcel(dest, getOriginalObjectName());
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.RecallNotificationMessage
 * JD-Core Version:    0.6.0
 */
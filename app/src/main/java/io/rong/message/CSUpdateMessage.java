/*     */ package io.rong.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:CsUpdate", flag=0)
/*     */ public class CSUpdateMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "CSUpdateMessage";
/*     */   private String sid;
/*     */   private String serviceStatus;
/* 122 */   public static final Parcelable.Creator<CSUpdateMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CSUpdateMessage createFromParcel(Parcel source)
/*     */     {
/* 126 */       return new CSUpdateMessage(source);
/*     */     }
/*     */ 
/*     */     public CSUpdateMessage[] newArray(int size)
/*     */     {
/* 131 */       return new CSUpdateMessage[size];
/*     */     }
/* 122 */   };
/*     */ 
/*     */   public CSUpdateMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public String getSid()
/*     */   {
/*  34 */     return this.sid;
/*     */   }
/*     */ 
/*     */   public String getServiceStatus()
/*     */   {
/*  44 */     return this.serviceStatus;
/*     */   }
/*     */ 
/*     */   public CSUpdateMessage(byte[] data) {
/*  48 */     String jsonStr = null;
/*     */     try {
/*  50 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  52 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/*  56 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*  57 */       this.sid = jsonObj.optString("sid");
/*  58 */       this.serviceStatus = jsonObj.optString("serviceStatus");
/*     */     }
/*     */     catch (JSONException e) {
/*  61 */       RLog.e("CSUpdateMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static CSUpdateMessage obtain(String sid, String serviceStatus) {
/*  66 */     CSUpdateMessage message = new CSUpdateMessage();
/*  67 */     message.sid = sid;
/*  68 */     message.serviceStatus = serviceStatus;
/*  69 */     return message;
/*     */   }
/*     */ 
/*     */   public CSUpdateMessage(Parcel in) {
/*  73 */     this.sid = ParcelUtils.readFromParcel(in);
/*  74 */     this.serviceStatus = ParcelUtils.readFromParcel(in);
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/*  84 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/*  95 */     ParcelUtils.writeToParcel(dest, this.sid);
/*  96 */     ParcelUtils.writeToParcel(dest, this.serviceStatus);
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/* 106 */     JSONObject jsonObj = new JSONObject();
/*     */     try {
/* 108 */       jsonObj.put("sid", this.sid);
/* 109 */       jsonObj.put("serviceStatus", this.serviceStatus);
/*     */     } catch (JSONException e) {
/* 111 */       RLog.e("CSUpdateMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 115 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 117 */       e.printStackTrace();
/*     */     }
/* 119 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.CSUpdateMessage
 * JD-Core Version:    0.6.0
 */
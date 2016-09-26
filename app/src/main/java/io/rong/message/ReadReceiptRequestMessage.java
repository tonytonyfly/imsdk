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
/*     */ @MessageTag("RC:RRReqMsg")
/*     */ public class ReadReceiptRequestMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "ReadReceiptRequestMessage";
/*     */   private String mMessageUId;
/*  94 */   public static final Parcelable.Creator<ReadReceiptRequestMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public ReadReceiptRequestMessage createFromParcel(Parcel source) {
/*  97 */       return new ReadReceiptRequestMessage(source);
/*     */     }
/*     */ 
/*     */     public ReadReceiptRequestMessage[] newArray(int size)
/*     */     {
/* 102 */       return new ReadReceiptRequestMessage[size];
/*     */     }
/*  94 */   };
/*     */ 
/*     */   public String getMessageUId()
/*     */   {
/*  24 */     return this.mMessageUId;
/*     */   }
/*     */ 
/*     */   public ReadReceiptRequestMessage(String uId)
/*     */   {
/*  33 */     this.mMessageUId = uId;
/*     */   }
/*     */ 
/*     */   public ReadReceiptRequestMessage(Parcel in) {
/*  37 */     this.mMessageUId = ParcelUtils.readFromParcel(in);
/*     */   }
/*     */ 
/*     */   public ReadReceiptRequestMessage(byte[] data) {
/*  41 */     String jsonStr = null;
/*     */     try
/*     */     {
/*  44 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  46 */       RLog.e("ReadReceiptRequestMessage", e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/*  50 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*     */ 
/*  52 */       if (jsonObj.has("messageUId"))
/*  53 */         this.mMessageUId = jsonObj.getString("messageUId");
/*     */     }
/*     */     catch (JSONException e) {
/*  56 */       RLog.e("ReadReceiptRequestMessage", e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  62 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/*  65 */       if (!TextUtils.isEmpty(this.mMessageUId))
/*  66 */         jsonObj.put("messageUId", this.mMessageUId);
/*     */     }
/*     */     catch (JSONException e) {
/*  69 */       RLog.e("ReadReceiptRequestMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/*  73 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/*  76 */       e.printStackTrace();
/*     */     }
/*  78 */     return null;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/*  83 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/*  88 */     ParcelUtils.writeToParcel(dest, this.mMessageUId);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.ReadReceiptRequestMessage
 * JD-Core Version:    0.6.0
 */
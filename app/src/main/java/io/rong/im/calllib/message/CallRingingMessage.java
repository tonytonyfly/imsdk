/*     */ package io.rong.calllib.message;
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
/*     */ @MessageTag(value="RC:VCRinging", flag=0)
/*     */ public class CallRingingMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "VoIPRingingMessage";
/*     */   private String callId;
/*  92 */   public static final Parcelable.Creator<CallRingingMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CallRingingMessage createFromParcel(Parcel source)
/*     */     {
/*  96 */       return new CallRingingMessage(source);
/*     */     }
/*     */ 
/*     */     public CallRingingMessage[] newArray(int size)
/*     */     {
/* 101 */       return new CallRingingMessage[size];
/*     */     }
/*  92 */   };
/*     */ 
/*     */   public CallRingingMessage(Parcel in)
/*     */   {
/*  22 */     this.callId = ParcelUtils.readFromParcel(in);
/*     */   }
/*     */ 
/*     */   public CallRingingMessage() {
/*     */   }
/*     */ 
/*     */   public CallRingingMessage(byte[] data) {
/*  29 */     String jsonStr = null;
/*     */     try
/*     */     {
/*  32 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  34 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/*  38 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*  39 */       this.callId = jsonObj.optString("callId");
/*     */     } catch (JSONException e) {
/*  41 */       RLog.e("VoIPRingingMessage", "JSONException, " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getCallId() {
/*  46 */     return this.callId;
/*     */   }
/*     */ 
/*     */   public void setCallId(String callId) {
/*  50 */     this.callId = callId;
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  55 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/*  58 */       jsonObj.putOpt("callId", this.callId);
/*     */     } catch (JSONException e) {
/*  60 */       RLog.e("VoIPRingingMessage", "JSONException, " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/*  64 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  66 */       e.printStackTrace();
/*     */     }
/*  68 */     return null;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/*  79 */     ParcelUtils.writeToParcel(dest, this.callId);
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/*  89 */     return 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.message.CallRingingMessage
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.calllib.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.calllib.RongCallCommon.CallDisconnectedReason;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:VCHangup", flag=0)
/*     */ public class CallHangupMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "VoIPHangupMessage";
/*     */   private String callId;
/*     */   private RongCallCommon.CallDisconnectedReason hangupReason;
/* 106 */   public static final Parcelable.Creator<CallHangupMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CallHangupMessage createFromParcel(Parcel source)
/*     */     {
/* 110 */       return new CallHangupMessage(source);
/*     */     }
/*     */ 
/*     */     public CallHangupMessage[] newArray(int size)
/*     */     {
/* 115 */       return new CallHangupMessage[size];
/*     */     }
/* 106 */   };
/*     */ 
/*     */   public CallHangupMessage(Parcel in)
/*     */   {
/*  24 */     this.callId = ParcelUtils.readFromParcel(in);
/*  25 */     this.hangupReason = RongCallCommon.CallDisconnectedReason.valueOf(ParcelUtils.readIntFromParcel(in).intValue());
/*     */   }
/*     */ 
/*     */   public CallHangupMessage() {
/*     */   }
/*     */ 
/*     */   public CallHangupMessage(byte[] data) {
/*  32 */     String jsonStr = null;
/*     */     try
/*     */     {
/*  35 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  37 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/*  41 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*  42 */       this.callId = jsonObj.optString("callId");
/*  43 */       this.hangupReason = RongCallCommon.CallDisconnectedReason.valueOf(jsonObj.optInt("reason"));
/*     */     } catch (JSONException e) {
/*  45 */       RLog.e("VoIPHangupMessage", "JSONException, " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getCallId() {
/*  50 */     return this.callId;
/*     */   }
/*     */ 
/*     */   public void setCallId(String callId) {
/*  54 */     this.callId = callId;
/*     */   }
/*     */ 
/*     */   public RongCallCommon.CallDisconnectedReason getHangupReason() {
/*  58 */     return this.hangupReason;
/*     */   }
/*     */ 
/*     */   public void setHangupReason(RongCallCommon.CallDisconnectedReason hangupReason) {
/*  62 */     this.hangupReason = hangupReason;
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  67 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/*  70 */       jsonObj.putOpt("callId", this.callId);
/*  71 */       jsonObj.putOpt("reason", Integer.valueOf(this.hangupReason.getValue()));
/*     */     } catch (JSONException e) {
/*  73 */       RLog.e("VoIPHangupMessage", "JSONException, " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/*  77 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  79 */       e.printStackTrace();
/*     */     }
/*  81 */     return null;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/*  92 */     ParcelUtils.writeToParcel(dest, this.callId);
/*  93 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.hangupReason.getValue()));
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 103 */     return 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.message.CallHangupMessage
 * JD-Core Version:    0.6.0
 */
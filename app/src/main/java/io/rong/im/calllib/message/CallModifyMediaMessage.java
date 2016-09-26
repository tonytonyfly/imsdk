/*     */ package io.rong.calllib.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.calllib.RongCallCommon.CallMediaType;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:VCModifyMedia", flag=0)
/*     */ public class CallModifyMediaMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "VoIPModifyMediaMessage";
/*     */   private String callId;
/*     */   private RongCallCommon.CallMediaType mediaType;
/* 112 */   public static final Parcelable.Creator<CallModifyMediaMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CallModifyMediaMessage createFromParcel(Parcel source)
/*     */     {
/* 116 */       return new CallModifyMediaMessage(source);
/*     */     }
/*     */ 
/*     */     public CallModifyMediaMessage[] newArray(int size)
/*     */     {
/* 121 */       return new CallModifyMediaMessage[size];
/*     */     }
/* 112 */   };
/*     */ 
/*     */   public CallModifyMediaMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CallModifyMediaMessage(byte[] data)
/*     */   {
/*  30 */     String jsonStr = null;
/*     */     try
/*     */     {
/*  33 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  35 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/*  39 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*  40 */       this.callId = jsonObj.optString("callId");
/*  41 */       this.mediaType = RongCallCommon.CallMediaType.valueOf(jsonObj.optInt("mediaType"));
/*     */     } catch (JSONException e) {
/*  43 */       RLog.e("VoIPModifyMediaMessage", "JSONException, " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getCallId() {
/*  48 */     return this.callId;
/*     */   }
/*     */ 
/*     */   public void setCallId(String callId) {
/*  52 */     this.callId = callId;
/*     */   }
/*     */ 
/*     */   public RongCallCommon.CallMediaType getMediaType()
/*     */   {
/*  58 */     return this.mediaType;
/*     */   }
/*     */ 
/*     */   public void setMediaType(RongCallCommon.CallMediaType mediaType) {
/*  62 */     this.mediaType = mediaType;
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  67 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/*  70 */       jsonObj.putOpt("callId", this.callId);
/*  71 */       jsonObj.putOpt("mediaType", Integer.valueOf(this.mediaType.getValue()));
/*     */     } catch (JSONException e) {
/*  73 */       RLog.e("VoIPModifyMediaMessage", "JSONException, " + e.getMessage());
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
/*     */   public CallModifyMediaMessage(Parcel in)
/*     */   {
/*  86 */     this.callId = ParcelUtils.readFromParcel(in);
/*  87 */     this.mediaType = RongCallCommon.CallMediaType.valueOf(ParcelUtils.readIntFromParcel(in).intValue());
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/*  98 */     ParcelUtils.writeToParcel(dest, this.callId);
/*  99 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.mediaType.getValue()));
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 109 */     return 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.message.CallModifyMediaMessage
 * JD-Core Version:    0.6.0
 */
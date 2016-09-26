/*     */ package io.rong.calllib.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.calllib.RongCallCommon.CallDisconnectedReason;
/*     */ import io.rong.calllib.RongCallCommon.CallMediaType;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:VSTMsg", flag=1)
/*     */ public class CallSTerminateMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "CallSTerminateMessage";
/*     */   private String content;
/*     */   private String direction;
/*     */   private RongCallCommon.CallDisconnectedReason reason;
/*     */   private RongCallCommon.CallMediaType mediaType;
/*     */   protected String extra;
/* 211 */   public static final Parcelable.Creator<CallSTerminateMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CallSTerminateMessage createFromParcel(Parcel source)
/*     */     {
/* 215 */       return new CallSTerminateMessage(source);
/*     */     }
/*     */ 
/*     */     public CallSTerminateMessage[] newArray(int size)
/*     */     {
/* 220 */       return new CallSTerminateMessage[size];
/*     */     }
/* 211 */   };
/*     */ 
/*     */   public String getExtra()
/*     */   {
/*  34 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra)
/*     */   {
/*  43 */     this.extra = extra;
/*     */   }
/*     */ 
/*     */   public RongCallCommon.CallDisconnectedReason getReason() {
/*  47 */     return this.reason;
/*     */   }
/*     */ 
/*     */   public void setReason(RongCallCommon.CallDisconnectedReason reason) {
/*  51 */     this.reason = reason;
/*     */   }
/*     */ 
/*     */   public RongCallCommon.CallMediaType getMediaType() {
/*  55 */     return this.mediaType;
/*     */   }
/*     */ 
/*     */   public void setMediaType(RongCallCommon.CallMediaType mediaType) {
/*  59 */     this.mediaType = mediaType;
/*     */   }
/*     */ 
/*     */   public String getDirection() {
/*  63 */     return this.direction;
/*     */   }
/*     */ 
/*     */   public void setDirection(String direction) {
/*  67 */     this.direction = direction;
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  78 */     JSONObject jsonObj = new JSONObject();
/*     */     try {
/*  80 */       jsonObj.put("content", this.content);
/*  81 */       jsonObj.put("reason", this.reason != null ? this.reason.getValue() : 3);
/*  82 */       jsonObj.put("mediaType", this.mediaType != null ? this.mediaType.getValue() : 1);
/*  83 */       jsonObj.put("direction", this.direction);
/*     */ 
/*  85 */       if (!TextUtils.isEmpty(getExtra())) {
/*  86 */         jsonObj.put("extra", getExtra());
/*     */       }
/*  88 */       if (getJSONUserInfo() != null)
/*  89 */         jsonObj.putOpt("user", getJSONUserInfo());
/*     */     }
/*     */     catch (JSONException e) {
/*  92 */       RLog.e("CallSTerminateMessage", "JSONException, " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/*  96 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  98 */       e.printStackTrace();
/*     */     }
/* 100 */     return null;
/*     */   }
/*     */ 
/*     */   public CallSTerminateMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public static CallSTerminateMessage obtain(String text)
/*     */   {
/* 109 */     CallSTerminateMessage model = new CallSTerminateMessage();
/* 110 */     model.setContent(text);
/* 111 */     return model;
/*     */   }
/*     */ 
/*     */   public CallSTerminateMessage(byte[] data) {
/* 115 */     String jsonStr = null;
/*     */     try {
/* 117 */       jsonStr = new String(data, "UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException e1)
/*     */     {
/*     */     }
/*     */     try {
/* 123 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*     */ 
/* 125 */       this.content = jsonObj.optString("content");
/* 126 */       this.direction = jsonObj.optString("direction");
/* 127 */       this.reason = RongCallCommon.CallDisconnectedReason.valueOf(jsonObj.optInt("reason"));
/* 128 */       this.mediaType = RongCallCommon.CallMediaType.valueOf(jsonObj.optInt("mediaType"));
/* 129 */       if (jsonObj.has("extra")) {
/* 130 */         setExtra(jsonObj.optString("extra"));
/*     */       }
/* 132 */       if (jsonObj.has("user"))
/* 133 */         setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
/*     */     }
/*     */     catch (JSONException e)
/*     */     {
/* 137 */       RLog.e("CallSTerminateMessage", "JSONException, " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setContent(String content)
/*     */   {
/* 148 */     this.content = content;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 157 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 168 */     ParcelUtils.writeToParcel(dest, getExtra());
/* 169 */     ParcelUtils.writeToParcel(dest, this.content);
/* 170 */     ParcelUtils.writeToParcel(dest, getUserInfo());
/* 171 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.reason != null ? this.reason.getValue() : 3));
/* 172 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.mediaType != null ? this.mediaType.getValue() : 1));
/* 173 */     ParcelUtils.writeToParcel(dest, this.direction);
/*     */   }
/*     */ 
/*     */   public CallSTerminateMessage(Parcel in)
/*     */   {
/* 182 */     setExtra(ParcelUtils.readFromParcel(in));
/* 183 */     setContent(ParcelUtils.readFromParcel(in));
/* 184 */     setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
/* 185 */     this.reason = RongCallCommon.CallDisconnectedReason.valueOf(ParcelUtils.readIntFromParcel(in).intValue());
/* 186 */     this.mediaType = RongCallCommon.CallMediaType.valueOf(ParcelUtils.readIntFromParcel(in).intValue());
/* 187 */     this.direction = ParcelUtils.readFromParcel(in);
/*     */   }
/*     */ 
/*     */   public CallSTerminateMessage(String content)
/*     */   {
/* 196 */     setContent(content);
/*     */   }
/*     */ 
/*     */   public String getContent()
/*     */   {
/* 205 */     return this.content;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.message.CallSTerminateMessage
 * JD-Core Version:    0.6.0
 */
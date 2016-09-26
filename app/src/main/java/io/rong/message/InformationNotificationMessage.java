/*     */ package io.rong.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:InfoNtf", flag=1)
/*     */ public class InformationNotificationMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "InformationNotificationMessage";
/*     */   private String message;
/*     */   protected String extra;
/* 154 */   public static final Parcelable.Creator<InformationNotificationMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public InformationNotificationMessage createFromParcel(Parcel source)
/*     */     {
/* 158 */       return new InformationNotificationMessage(source);
/*     */     }
/*     */ 
/*     */     public InformationNotificationMessage[] newArray(int size)
/*     */     {
/* 163 */       return new InformationNotificationMessage[size];
/*     */     }
/* 154 */   };
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  42 */     JSONObject jsonObj = new JSONObject();
/*     */     try {
/*  44 */       if (!TextUtils.isEmpty(getMessage())) {
/*  45 */         jsonObj.put("message", getMessage());
/*     */       }
/*  47 */       if (!TextUtils.isEmpty(getExtra())) {
/*  48 */         jsonObj.put("extra", getExtra());
/*     */       }
/*  50 */       if (getJSONUserInfo() != null)
/*  51 */         jsonObj.putOpt("user", getJSONUserInfo());
/*     */     }
/*     */     catch (JSONException e) {
/*  54 */       RLog.e("InformationNotificationMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/*  58 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/*  61 */       e.printStackTrace();
/*     */     }
/*  63 */     return null;
/*     */   }
/*     */ 
/*     */   protected InformationNotificationMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public static InformationNotificationMessage obtain(String message)
/*     */   {
/*  80 */     InformationNotificationMessage model = new InformationNotificationMessage();
/*  81 */     model.setMessage(message);
/*  82 */     return model;
/*     */   }
/*     */ 
/*     */   public InformationNotificationMessage(byte[] data)
/*     */   {
/*  91 */     String jsonStr = null;
/*     */     try
/*     */     {
/*  94 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  96 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 100 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*     */ 
/* 102 */       if (jsonObj.has("message")) {
/* 103 */         setMessage(jsonObj.optString("message"));
/*     */       }
/* 105 */       if (jsonObj.has("extra")) {
/* 106 */         setExtra(jsonObj.optString("extra"));
/*     */       }
/* 108 */       if (jsonObj.has("user"))
/* 109 */         setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
/*     */     }
/*     */     catch (JSONException e)
/*     */     {
/* 113 */       RLog.e("InformationNotificationMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 124 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 135 */     ParcelUtils.writeToParcel(dest, getMessage());
/* 136 */     ParcelUtils.writeToParcel(dest, getExtra());
/* 137 */     ParcelUtils.writeToParcel(dest, getUserInfo());
/*     */   }
/*     */ 
/*     */   public InformationNotificationMessage(Parcel in)
/*     */   {
/* 146 */     setMessage(ParcelUtils.readFromParcel(in));
/* 147 */     setExtra(ParcelUtils.readFromParcel(in));
/* 148 */     setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
/*     */   }
/*     */ 
/*     */   public InformationNotificationMessage(String message)
/*     */   {
/* 173 */     setMessage(message);
/*     */   }
/*     */ 
/*     */   public String getMessage()
/*     */   {
/* 183 */     return this.message;
/*     */   }
/*     */ 
/*     */   public void setMessage(String message)
/*     */   {
/* 192 */     this.message = message;
/*     */   }
/*     */ 
/*     */   public String getExtra()
/*     */   {
/* 200 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra)
/*     */   {
/* 209 */     this.extra = extra;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.InformationNotificationMessage
 * JD-Core Version:    0.6.0
 */
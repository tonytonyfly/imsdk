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
/*     */ @MessageTag(value="RC:ProfileNtf", flag=1)
/*     */ public class ProfileNotificationMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "ProfileNotificationMessage";
/*     */   private String operation;
/*     */   private String data;
/*     */   private String extra;
/* 181 */   public static final Parcelable.Creator<ProfileNotificationMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public ProfileNotificationMessage createFromParcel(Parcel source)
/*     */     {
/* 185 */       return new ProfileNotificationMessage(source);
/*     */     }
/*     */ 
/*     */     public ProfileNotificationMessage[] newArray(int size)
/*     */     {
/* 190 */       return new ProfileNotificationMessage[size];
/*     */     }
/* 181 */   };
/*     */ 
/*     */   public String getOperation()
/*     */   {
/*  31 */     return this.operation;
/*     */   }
/*     */ 
/*     */   public void setOperation(String operation)
/*     */   {
/*  40 */     this.operation = operation;
/*     */   }
/*     */ 
/*     */   public String getData()
/*     */   {
/*  49 */     return this.data;
/*     */   }
/*     */ 
/*     */   public void setData(String data)
/*     */   {
/*  58 */     this.data = data;
/*     */   }
/*     */ 
/*     */   public String getExtra()
/*     */   {
/*  67 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra)
/*     */   {
/*  76 */     this.extra = extra;
/*     */   }
/*     */ 
/*     */   public ProfileNotificationMessage(Parcel in) {
/*  80 */     this.operation = ParcelUtils.readFromParcel(in);
/*  81 */     this.data = ParcelUtils.readFromParcel(in);
/*  82 */     this.extra = ParcelUtils.readFromParcel(in);
/*  83 */     setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
/*     */   }
/*     */ 
/*     */   public static ProfileNotificationMessage obtain(String operation, String data)
/*     */   {
/*  94 */     ProfileNotificationMessage obj = new ProfileNotificationMessage();
/*  95 */     obj.operation = operation;
/*  96 */     obj.data = data;
/*  97 */     return obj;
/*     */   }
/*     */ 
/*     */   private ProfileNotificationMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/* 106 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/* 110 */       jsonObj.put("operation", this.operation);
/*     */ 
/* 112 */       if (!TextUtils.isEmpty(this.data)) {
/* 113 */         jsonObj.put("data", this.data);
/*     */       }
/* 115 */       if (!TextUtils.isEmpty(getExtra())) {
/* 116 */         jsonObj.put("extra", getExtra());
/*     */       }
/* 118 */       if (getJSONUserInfo() != null)
/* 119 */         jsonObj.putOpt("user", getJSONUserInfo());
/*     */     }
/*     */     catch (JSONException e) {
/* 122 */       RLog.e("ProfileNotificationMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 126 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 128 */       e.printStackTrace();
/*     */     }
/* 130 */     return null;
/*     */   }
/*     */ 
/*     */   public ProfileNotificationMessage(byte[] data)
/*     */   {
/* 135 */     String jsonStr = null;
/*     */     try {
/* 137 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 139 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 143 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*     */ 
/* 145 */       setOperation(jsonObj.optString("operation"));
/* 146 */       setData(jsonObj.optString("data"));
/* 147 */       setExtra(jsonObj.optString("extra"));
/*     */ 
/* 149 */       if (jsonObj.has("user"))
/* 150 */         setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
/*     */     }
/*     */     catch (JSONException e) {
/* 153 */       RLog.e("ProfileNotificationMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 165 */     ParcelUtils.writeToParcel(dest, this.operation);
/* 166 */     ParcelUtils.writeToParcel(dest, this.data);
/* 167 */     ParcelUtils.writeToParcel(dest, this.extra);
/* 168 */     ParcelUtils.writeToParcel(dest, getUserInfo());
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 178 */     return 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.ProfileNotificationMessage
 * JD-Core Version:    0.6.0
 */
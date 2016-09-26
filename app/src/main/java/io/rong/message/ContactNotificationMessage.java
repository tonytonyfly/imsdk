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
/*     */ @MessageTag(value="RC:ContactNtf", flag=1)
/*     */ public class ContactNotificationMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "ContactNotificationMessage";
/*     */   public static final String CONTACT_OPERATION_REQUEST = "Request";
/*     */   public static final String CONTACT_OPERATION_ACCEPT_RESPONSE = "AcceptResponse";
/*     */   public static final String CONTACT_OPERATION_REJECT_RESPONSE = "RejectResponse";
/*     */   private String operation;
/*     */   private String sourceUserId;
/*     */   private String targetUserId;
/*     */   private String message;
/*     */   private String extra;
/* 246 */   public static final Parcelable.Creator<ContactNotificationMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public ContactNotificationMessage createFromParcel(Parcel source)
/*     */     {
/* 250 */       return new ContactNotificationMessage(source);
/*     */     }
/*     */ 
/*     */     public ContactNotificationMessage[] newArray(int size)
/*     */     {
/* 255 */       return new ContactNotificationMessage[size];
/*     */     }
/* 246 */   };
/*     */ 
/*     */   public String getOperation()
/*     */   {
/*  49 */     return this.operation;
/*     */   }
/*     */ 
/*     */   public void setOperation(String operation)
/*     */   {
/*  58 */     this.operation = operation;
/*     */   }
/*     */ 
/*     */   public String getSourceUserId()
/*     */   {
/*  67 */     return this.sourceUserId;
/*     */   }
/*     */ 
/*     */   public void setSourceUserId(String sourceUserId)
/*     */   {
/*  76 */     this.sourceUserId = sourceUserId;
/*     */   }
/*     */ 
/*     */   public String getTargetUserId()
/*     */   {
/*  85 */     return this.targetUserId;
/*     */   }
/*     */ 
/*     */   public void setTargetUserId(String targetUserId)
/*     */   {
/*  94 */     this.targetUserId = targetUserId;
/*     */   }
/*     */ 
/*     */   public String getMessage()
/*     */   {
/* 103 */     return this.message;
/*     */   }
/*     */ 
/*     */   public void setMessage(String message)
/*     */   {
/* 112 */     this.message = message;
/*     */   }
/*     */ 
/*     */   public String getExtra()
/*     */   {
/* 121 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra)
/*     */   {
/* 130 */     this.extra = extra;
/*     */   }
/*     */ 
/*     */   public ContactNotificationMessage(Parcel in) {
/* 134 */     this.operation = ParcelUtils.readFromParcel(in);
/* 135 */     this.sourceUserId = ParcelUtils.readFromParcel(in);
/* 136 */     this.targetUserId = ParcelUtils.readFromParcel(in);
/* 137 */     this.message = ParcelUtils.readFromParcel(in);
/* 138 */     this.extra = ParcelUtils.readFromParcel(in);
/* 139 */     setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
/*     */   }
/*     */ 
/*     */   public static ContactNotificationMessage obtain(String operation, String sourceUserId, String targetUserId, String message)
/*     */   {
/* 152 */     ContactNotificationMessage obj = new ContactNotificationMessage();
/* 153 */     obj.operation = operation;
/* 154 */     obj.sourceUserId = sourceUserId;
/* 155 */     obj.targetUserId = targetUserId;
/* 156 */     obj.message = message;
/* 157 */     return obj;
/*     */   }
/*     */ 
/*     */   private ContactNotificationMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/* 166 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/* 169 */       jsonObj.putOpt("operation", this.operation);
/* 170 */       jsonObj.putOpt("sourceUserId", this.sourceUserId);
/* 171 */       jsonObj.putOpt("targetUserId", this.targetUserId);
/*     */ 
/* 173 */       if (!TextUtils.isEmpty(this.message)) {
/* 174 */         jsonObj.putOpt("message", this.message);
/*     */       }
/* 176 */       if (!TextUtils.isEmpty(getExtra())) {
/* 177 */         jsonObj.putOpt("extra", getExtra());
/*     */       }
/* 179 */       if (getJSONUserInfo() != null)
/* 180 */         jsonObj.putOpt("user", getJSONUserInfo());
/*     */     }
/*     */     catch (JSONException e) {
/* 183 */       RLog.e("ContactNotificationMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 187 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 189 */       e.printStackTrace();
/*     */     }
/* 191 */     return null;
/*     */   }
/*     */ 
/*     */   public ContactNotificationMessage(byte[] data)
/*     */   {
/* 196 */     String jsonStr = null;
/*     */     try {
/* 198 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 200 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 204 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*     */ 
/* 206 */       setOperation(jsonObj.optString("operation"));
/* 207 */       setSourceUserId(jsonObj.optString("sourceUserId"));
/* 208 */       setTargetUserId(jsonObj.optString("targetUserId"));
/* 209 */       setMessage(jsonObj.optString("message"));
/* 210 */       setExtra(jsonObj.optString("extra"));
/*     */ 
/* 212 */       if (jsonObj.has("user"))
/* 213 */         setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
/*     */     }
/*     */     catch (JSONException e) {
/* 216 */       RLog.e("ContactNotificationMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 228 */     ParcelUtils.writeToParcel(dest, this.operation);
/* 229 */     ParcelUtils.writeToParcel(dest, this.sourceUserId);
/* 230 */     ParcelUtils.writeToParcel(dest, this.targetUserId);
/* 231 */     ParcelUtils.writeToParcel(dest, this.message);
/* 232 */     ParcelUtils.writeToParcel(dest, this.extra);
/* 233 */     ParcelUtils.writeToParcel(dest, getUserInfo());
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 243 */     return 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.ContactNotificationMessage
 * JD-Core Version:    0.6.0
 */
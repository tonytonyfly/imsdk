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
/*     */ @MessageTag(value="RC:GrpNtf", flag=1)
/*     */ public class GroupNotificationMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "GroupNotificationMessage";
/*     */   public static final String GROUP_OPERATION_ADD = "Add";
/*     */   public static final String GROUP_OPERATION_QUIT = "Quit";
/*     */   public static final String GROUP_OPERATION_KICKED = "Kicked";
/*     */   public static final String GROUP_OPERATION_RENAME = "Rename";
/*     */   public static final String GROUP_OPERATION_BULLETIN = "Bulletin";
/*     */   private String operatorUserId;
/*     */   private String operation;
/*     */   private String data;
/*     */   private String message;
/*     */   private String extra;
/* 198 */   public static final Parcelable.Creator<GroupNotificationMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public GroupNotificationMessage createFromParcel(Parcel source)
/*     */     {
/* 202 */       return new GroupNotificationMessage(source);
/*     */     }
/*     */ 
/*     */     public GroupNotificationMessage[] newArray(int size)
/*     */     {
/* 207 */       return new GroupNotificationMessage[size];
/*     */     }
/* 198 */   };
/*     */ 
/*     */   public String getOperatorUserId()
/*     */   {
/*  64 */     return this.operatorUserId;
/*     */   }
/*     */ 
/*     */   public void setOperatorUserId(String operatorUserId) {
/*  68 */     this.operatorUserId = operatorUserId;
/*     */   }
/*     */ 
/*     */   public String getOperation() {
/*  72 */     return this.operation;
/*     */   }
/*     */ 
/*     */   public void setOperation(String operation) {
/*  76 */     this.operation = operation;
/*     */   }
/*     */ 
/*     */   public String getData() {
/*  80 */     return this.data;
/*     */   }
/*     */ 
/*     */   public void setData(String data) {
/*  84 */     this.data = data;
/*     */   }
/*     */ 
/*     */   public String getMessage() {
/*  88 */     return this.message;
/*     */   }
/*     */ 
/*     */   public void setMessage(String message) {
/*  92 */     this.message = message;
/*     */   }
/*     */ 
/*     */   public String getExtra() {
/*  96 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra) {
/* 100 */     this.extra = extra;
/*     */   }
/*     */ 
/*     */   public GroupNotificationMessage(Parcel in) {
/* 104 */     this.operatorUserId = ParcelUtils.readFromParcel(in);
/* 105 */     this.operation = ParcelUtils.readFromParcel(in);
/* 106 */     this.data = ParcelUtils.readFromParcel(in);
/* 107 */     this.message = ParcelUtils.readFromParcel(in);
/* 108 */     this.extra = ParcelUtils.readFromParcel(in);
/* 109 */     setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
/*     */   }
/*     */ 
/*     */   public static GroupNotificationMessage obtain(String operatorUserId, String operation, String data, String message) {
/* 113 */     GroupNotificationMessage obj = new GroupNotificationMessage();
/* 114 */     obj.operatorUserId = operatorUserId;
/* 115 */     obj.operation = operation;
/* 116 */     obj.data = data;
/* 117 */     obj.message = message;
/* 118 */     return obj;
/*     */   }
/*     */ 
/*     */   private GroupNotificationMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/* 127 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/* 130 */       jsonObj.put("operatorUserId", this.operatorUserId);
/* 131 */       jsonObj.put("operation", this.operation);
/*     */ 
/* 133 */       if (!TextUtils.isEmpty(this.data)) {
/* 134 */         jsonObj.put("data", this.data);
/*     */       }
/* 136 */       if (!TextUtils.isEmpty(this.message)) {
/* 137 */         jsonObj.put("message", this.message);
/*     */       }
/* 139 */       if (!TextUtils.isEmpty(getExtra())) {
/* 140 */         jsonObj.put("extra", getExtra());
/*     */       }
/* 142 */       if (getJSONUserInfo() != null)
/* 143 */         jsonObj.putOpt("user", getJSONUserInfo());
/*     */     }
/*     */     catch (JSONException e) {
/* 146 */       RLog.e("GroupNotificationMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 150 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 152 */       e.printStackTrace();
/*     */     }
/* 154 */     return null;
/*     */   }
/*     */ 
/*     */   public GroupNotificationMessage(byte[] data)
/*     */   {
/* 159 */     String jsonStr = null;
/*     */     try {
/* 161 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 163 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 167 */       JSONObject jsonObj = new JSONObject(jsonStr);
/* 168 */       setOperatorUserId(jsonObj.optString("operatorUserId"));
/* 169 */       setOperation(jsonObj.optString("operation"));
/* 170 */       setData(jsonObj.optString("data"));
/* 171 */       setMessage(jsonObj.optString("message"));
/* 172 */       setExtra(jsonObj.optString("extra"));
/*     */ 
/* 174 */       if (jsonObj.has("user"))
/* 175 */         setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
/*     */     }
/*     */     catch (JSONException e)
/*     */     {
/* 179 */       RLog.e("GroupNotificationMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 185 */     ParcelUtils.writeToParcel(dest, this.operatorUserId);
/* 186 */     ParcelUtils.writeToParcel(dest, this.operation);
/* 187 */     ParcelUtils.writeToParcel(dest, this.data);
/* 188 */     ParcelUtils.writeToParcel(dest, this.message);
/* 189 */     ParcelUtils.writeToParcel(dest, this.extra);
/* 190 */     ParcelUtils.writeToParcel(dest, getUserInfo());
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 195 */     return 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.GroupNotificationMessage
 * JD-Core Version:    0.6.0
 */
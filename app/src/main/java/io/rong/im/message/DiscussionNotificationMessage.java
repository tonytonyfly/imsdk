/*     */ package io.rong.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.util.Log;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:DizNtf", flag=1)
/*     */ public class DiscussionNotificationMessage extends NotificationMessage
/*     */ {
/*     */   private int type;
/*     */   private String extension;
/*     */   private String operator;
/*     */   private boolean hasReceived;
/*  85 */   public static final Parcelable.Creator<DiscussionNotificationMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public DiscussionNotificationMessage createFromParcel(Parcel source)
/*     */     {
/*  89 */       return new DiscussionNotificationMessage(source);
/*     */     }
/*     */ 
/*     */     public DiscussionNotificationMessage[] newArray(int size)
/*     */     {
/*  94 */       return new DiscussionNotificationMessage[size];
/*     */     }
/*  85 */   };
/*     */ 
/*     */   public DiscussionNotificationMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public DiscussionNotificationMessage(Parcel in)
/*     */   {
/*  32 */     this.type = ParcelUtils.readIntFromParcel(in).intValue();
/*  33 */     this.extension = ParcelUtils.readFromParcel(in);
/*  34 */     this.operator = ParcelUtils.readFromParcel(in);
/*  35 */     setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
/*     */   }
/*     */ 
/*     */   public DiscussionNotificationMessage(byte[] data) {
/*  39 */     String jsonStr = null;
/*     */     try
/*     */     {
/*  42 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  44 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/*  48 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*  49 */       setType(jsonObj.optInt("type"));
/*  50 */       setExtension(jsonObj.optString("extension"));
/*  51 */       setOperator(jsonObj.optString("operator"));
/*     */ 
/*  53 */       if (jsonObj.has("user"))
/*  54 */         setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
/*     */     }
/*     */     catch (JSONException e) {
/*  57 */       Log.e("JSONException", e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/*  68 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/*  79 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.type));
/*  80 */     ParcelUtils.writeToParcel(dest, this.extension);
/*  81 */     ParcelUtils.writeToParcel(dest, this.operator);
/*  82 */     ParcelUtils.writeToParcel(dest, getUserInfo());
/*     */   }
/*     */ 
/*     */   public boolean isHasReceived()
/*     */   {
/* 104 */     return this.hasReceived;
/*     */   }
/*     */ 
/*     */   public void setHasReceived(boolean hasReceived)
/*     */   {
/* 113 */     this.hasReceived = hasReceived;
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/* 125 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/* 128 */       jsonObj.put("type", this.type);
/* 129 */       jsonObj.put("extension", this.extension);
/*     */ 
/* 132 */       if (getJSONUserInfo() != null)
/* 133 */         jsonObj.putOpt("user", getJSONUserInfo());
/*     */     }
/*     */     catch (JSONException e) {
/* 136 */       Log.e("JSONException", e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 140 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/* 143 */       e.printStackTrace();
/*     */     }
/* 145 */     return null;
/*     */   }
/*     */ 
/*     */   public int getType()
/*     */   {
/* 154 */     return this.type;
/*     */   }
/*     */ 
/*     */   public void setType(int type)
/*     */   {
/* 163 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public String getExtension()
/*     */   {
/* 171 */     return this.extension;
/*     */   }
/*     */ 
/*     */   public void setExtension(String extension)
/*     */   {
/* 179 */     this.extension = extension;
/*     */   }
/*     */ 
/*     */   public String getOperator()
/*     */   {
/* 187 */     return this.operator;
/*     */   }
/*     */ 
/*     */   public void setOperator(String operator)
/*     */   {
/* 195 */     this.operator = operator;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.DiscussionNotificationMessage
 * JD-Core Version:    0.6.0
 */
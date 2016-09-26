/*     */ package io.rong.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:CmdMsg", flag=0)
/*     */ public class CommandMessage extends NotificationMessage
/*     */ {
/*     */   private static final String TAG = "CommandMessage";
/*     */   private String name;
/*     */   private String data;
/* 157 */   public static final Parcelable.Creator<CommandMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CommandMessage createFromParcel(Parcel source)
/*     */     {
/* 161 */       return new CommandMessage(source);
/*     */     }
/*     */ 
/*     */     public CommandMessage[] newArray(int size)
/*     */     {
/* 166 */       return new CommandMessage[size];
/*     */     }
/* 157 */   };
/*     */ 
/*     */   public String getName()
/*     */   {
/*  31 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/*  40 */     this.name = name;
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
/*     */   public CommandMessage(Parcel in)
/*     */   {
/*  64 */     this.name = ParcelUtils.readFromParcel(in);
/*  65 */     this.data = ParcelUtils.readFromParcel(in);
/*  66 */     setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
/*     */   }
/*     */ 
/*     */   public static CommandMessage obtain(String name, String data)
/*     */   {
/*  77 */     CommandMessage obj = new CommandMessage();
/*  78 */     obj.name = name;
/*  79 */     obj.data = data;
/*  80 */     return obj;
/*     */   }
/*     */ 
/*     */   private CommandMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  89 */     JSONObject jsonObj = new JSONObject();
/*     */     try {
/*  91 */       jsonObj.put("name", this.name);
/*     */ 
/*  93 */       if (!TextUtils.isEmpty(this.data)) {
/*  94 */         jsonObj.put("data", this.data);
/*     */       }
/*  96 */       if (getJSONUserInfo() != null)
/*  97 */         jsonObj.putOpt("user", getJSONUserInfo());
/*     */     }
/*     */     catch (JSONException e) {
/* 100 */       RLog.e("CommandMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 104 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 106 */       e.printStackTrace();
/*     */     }
/* 108 */     return null;
/*     */   }
/*     */ 
/*     */   public CommandMessage(byte[] data)
/*     */   {
/* 113 */     String jsonStr = null;
/*     */     try {
/* 115 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 117 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 121 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*     */ 
/* 123 */       setName(jsonObj.optString("name"));
/* 124 */       setData(jsonObj.optString("data"));
/*     */ 
/* 126 */       if (jsonObj.has("user"))
/* 127 */         setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
/*     */     }
/*     */     catch (JSONException e) {
/* 130 */       RLog.e("CommandMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 142 */     ParcelUtils.writeToParcel(dest, this.name);
/* 143 */     ParcelUtils.writeToParcel(dest, this.data);
/* 144 */     ParcelUtils.writeToParcel(dest, getUserInfo());
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 154 */     return 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.CommandMessage
 * JD-Core Version:    0.6.0
 */
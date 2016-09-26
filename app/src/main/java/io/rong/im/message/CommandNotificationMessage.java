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
/*     */ @MessageTag(value="RC:CmdNtf", flag=1)
/*     */ public class CommandNotificationMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "CommandNotificationMessage";
/*     */   private String name;
/*     */   private String data;
/* 155 */   public static final Parcelable.Creator<CommandNotificationMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CommandNotificationMessage createFromParcel(Parcel source)
/*     */     {
/* 159 */       return new CommandNotificationMessage(source);
/*     */     }
/*     */ 
/*     */     public CommandNotificationMessage[] newArray(int size)
/*     */     {
/* 164 */       return new CommandNotificationMessage[size];
/*     */     }
/* 155 */   };
/*     */ 
/*     */   public String getName()
/*     */   {
/*  29 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/*  38 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public String getData()
/*     */   {
/*  47 */     return this.data;
/*     */   }
/*     */ 
/*     */   public void setData(String data)
/*     */   {
/*  56 */     this.data = data;
/*     */   }
/*     */ 
/*     */   public CommandNotificationMessage(Parcel in)
/*     */   {
/*  62 */     this.name = ParcelUtils.readFromParcel(in);
/*  63 */     this.data = ParcelUtils.readFromParcel(in);
/*  64 */     setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
/*     */   }
/*     */ 
/*     */   public static CommandNotificationMessage obtain(String name, String data)
/*     */   {
/*  75 */     CommandNotificationMessage obj = new CommandNotificationMessage();
/*  76 */     obj.name = name;
/*  77 */     obj.data = data;
/*  78 */     return obj;
/*     */   }
/*     */ 
/*     */   private CommandNotificationMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  87 */     JSONObject jsonObj = new JSONObject();
/*     */     try {
/*  89 */       jsonObj.put("name", this.name);
/*     */ 
/*  91 */       if (!TextUtils.isEmpty(this.data)) {
/*  92 */         jsonObj.put("data", this.data);
/*     */       }
/*  94 */       if (getJSONUserInfo() != null)
/*  95 */         jsonObj.putOpt("user", getJSONUserInfo());
/*     */     }
/*     */     catch (JSONException e) {
/*  98 */       RLog.e("CommandNotificationMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 102 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 104 */       e.printStackTrace();
/*     */     }
/* 106 */     return null;
/*     */   }
/*     */ 
/*     */   public CommandNotificationMessage(byte[] data)
/*     */   {
/* 111 */     String jsonStr = null;
/*     */     try {
/* 113 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 115 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 119 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*     */ 
/* 121 */       setName(jsonObj.optString("name"));
/* 122 */       setData(jsonObj.optString("data"));
/*     */ 
/* 124 */       if (jsonObj.has("user"))
/* 125 */         setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
/*     */     }
/*     */     catch (JSONException e) {
/* 128 */       RLog.e("CommandNotificationMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 140 */     ParcelUtils.writeToParcel(dest, this.name);
/* 141 */     ParcelUtils.writeToParcel(dest, this.data);
/* 142 */     ParcelUtils.writeToParcel(dest, getUserInfo());
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 152 */     return 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.CommandNotificationMessage
 * JD-Core Version:    0.6.0
 */
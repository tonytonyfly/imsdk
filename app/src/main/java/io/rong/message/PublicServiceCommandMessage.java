/*     */ package io.rong.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import io.rong.imlib.model.PublicServiceMenu.PublicServiceMenuItemType;
/*     */ import io.rong.imlib.model.PublicServiceMenuItem;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag("RC:PSCmd")
/*     */ public class PublicServiceCommandMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "PublicServiceCommandMessage";
/*     */   private String command;
/*     */   private String data;
/*     */   protected String extra;
/* 138 */   public static final Parcelable.Creator<PublicServiceCommandMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public PublicServiceCommandMessage createFromParcel(Parcel source)
/*     */     {
/* 142 */       return new PublicServiceCommandMessage(source);
/*     */     }
/*     */ 
/*     */     public PublicServiceCommandMessage[] newArray(int size)
/*     */     {
/* 147 */       return new PublicServiceCommandMessage[size];
/*     */     }
/* 138 */   };
/*     */ 
/*     */   public String getExtra()
/*     */   {
/*  33 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra)
/*     */   {
/*  41 */     this.extra = extra;
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  52 */     JSONObject jsonObj = new JSONObject();
/*     */     try {
/*  54 */       jsonObj.put("cmd", this.command);
/*  55 */       jsonObj.put("data", this.data);
/*     */ 
/*  57 */       if (!TextUtils.isEmpty(getExtra())) {
/*  58 */         jsonObj.put("extra", getExtra());
/*     */       }
/*  60 */       if (getJSONUserInfo() != null)
/*  61 */         jsonObj.putOpt("user", getJSONUserInfo());
/*     */     }
/*     */     catch (JSONException e) {
/*  64 */       RLog.e("PublicServiceCommandMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/*  68 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  70 */       e.printStackTrace();
/*     */     }
/*  72 */     return null;
/*     */   }
/*     */ 
/*     */   public PublicServiceCommandMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public PublicServiceCommandMessage(byte[] data)
/*     */   {
/*     */   }
/*     */ 
/*     */   public static PublicServiceCommandMessage obtain(PublicServiceMenuItem item) {
/*  84 */     PublicServiceCommandMessage model = new PublicServiceCommandMessage();
/*  85 */     if (item.getType() != null) {
/*  86 */       model.command = item.getType().getMessage();
/*  87 */       model.data = item.getId();
/*     */     }
/*  89 */     return model;
/*     */   }
/*     */ 
/*     */   public void setData(String data) {
/*  93 */     this.data = data;
/*     */   }
/*     */ 
/*     */   public void setCommand(String command) {
/*  97 */     this.command = command;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 106 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 117 */     ParcelUtils.writeToParcel(dest, this.command);
/* 118 */     ParcelUtils.writeToParcel(dest, this.data);
/* 119 */     ParcelUtils.writeToParcel(dest, getExtra());
/* 120 */     ParcelUtils.writeToParcel(dest, getUserInfo());
/*     */   }
/*     */ 
/*     */   public PublicServiceCommandMessage(Parcel in)
/*     */   {
/* 129 */     this.command = ParcelUtils.readFromParcel(in);
/* 130 */     this.data = ParcelUtils.readFromParcel(in);
/* 131 */     setExtra(ParcelUtils.readFromParcel(in));
/* 132 */     setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.PublicServiceCommandMessage
 * JD-Core Version:    0.6.0
 */
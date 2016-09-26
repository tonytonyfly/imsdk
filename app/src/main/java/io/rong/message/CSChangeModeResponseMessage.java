/*     */ package io.rong.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:CsChaR", flag=0)
/*     */ public class CSChangeModeResponseMessage extends MessageContent
/*     */ {
/*     */   private int code;
/*     */   private int status;
/*     */   private String errMsg;
/* 121 */   public static final Parcelable.Creator<CSChangeModeResponseMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CSChangeModeResponseMessage createFromParcel(Parcel source)
/*     */     {
/* 125 */       return new CSChangeModeResponseMessage(source);
/*     */     }
/*     */ 
/*     */     public CSChangeModeResponseMessage[] newArray(int size)
/*     */     {
/* 130 */       return new CSChangeModeResponseMessage[size];
/*     */     }
/* 121 */   };
/*     */ 
/*     */   public CSChangeModeResponseMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public int getResult()
/*     */   {
/*  33 */     return this.code;
/*     */   }
/*     */ 
/*     */   public String getErrMsg()
/*     */   {
/*  41 */     return this.errMsg;
/*     */   }
/*     */ 
/*     */   public int getStatus()
/*     */   {
/*  49 */     return this.status;
/*     */   }
/*     */ 
/*     */   public CSChangeModeResponseMessage(byte[] data) {
/*  53 */     String jsonStr = null;
/*     */     try {
/*  55 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  57 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/*  61 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*  62 */       this.code = jsonObj.optInt("code");
/*  63 */       this.errMsg = jsonObj.optString("msg");
/*  64 */       if (jsonObj.has("data")) {
/*  65 */         jsonObj = jsonObj.getJSONObject("data");
/*  66 */         this.status = jsonObj.getInt("status");
/*     */       }
/*     */     } catch (JSONException e) {
/*  69 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static CSChangeModeResponseMessage obtain()
/*     */   {
/*  79 */     return new CSChangeModeResponseMessage();
/*     */   }
/*     */ 
/*     */   public CSChangeModeResponseMessage(Parcel in) {
/*  83 */     this.code = ParcelUtils.readIntFromParcel(in).intValue();
/*  84 */     this.status = ParcelUtils.readIntFromParcel(in).intValue();
/*  85 */     this.errMsg = ParcelUtils.readFromParcel(in);
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/*  95 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 106 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.code));
/* 107 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.status));
/* 108 */     ParcelUtils.writeToParcel(dest, this.errMsg);
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/* 118 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.CSChangeModeResponseMessage
 * JD-Core Version:    0.6.0
 */
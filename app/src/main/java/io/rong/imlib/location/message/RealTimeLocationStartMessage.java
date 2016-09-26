/*     */ package io.rong.imlib.location.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:RLStart", flag=3)
/*     */ public class RealTimeLocationStartMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "RealTimeLocationStartMessage";
/*  22 */   private String content = "";
/*  23 */   private String extra = "";
/*     */ 
/*  99 */   public static final Parcelable.Creator<RealTimeLocationStartMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public RealTimeLocationStartMessage createFromParcel(Parcel source)
/*     */     {
/* 103 */       return new RealTimeLocationStartMessage(source);
/*     */     }
/*     */ 
/*     */     public RealTimeLocationStartMessage[] newArray(int size)
/*     */     {
/* 108 */       return new RealTimeLocationStartMessage[size];
/*     */     }
/*  99 */   };
/*     */ 
/*     */   public RealTimeLocationStartMessage(String content)
/*     */   {
/*  26 */     this.content = content;
/*     */   }
/*     */ 
/*     */   public RealTimeLocationStartMessage(byte[] data) {
/*  30 */     String jsonStr = null;
/*     */     try {
/*  32 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  34 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/*  38 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*  39 */       if (jsonObj.has("content"))
/*  40 */         this.content = jsonObj.optString("content");
/*  41 */       if (jsonObj.has("extra"))
/*  42 */         this.extra = jsonObj.getString("extra");
/*     */     } catch (JSONException e) {
/*  44 */       RLog.e("RealTimeLocationStartMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static RealTimeLocationStartMessage obtain(String content) {
/*  49 */     return new RealTimeLocationStartMessage(content);
/*     */   }
/*     */ 
/*     */   public String getContent() {
/*  53 */     return this.content;
/*     */   }
/*     */ 
/*     */   public String getExtra() {
/*  57 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra) {
/*  61 */     this.extra = extra;
/*     */   }
/*     */ 
/*     */   public RealTimeLocationStartMessage(Parcel in) {
/*  65 */     this.content = in.readString();
/*  66 */     this.extra = in.readString();
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/*  71 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/*  76 */     dest.writeString(this.content);
/*  77 */     dest.writeString(this.extra);
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  82 */     JSONObject jsonObj = new JSONObject();
/*     */     try {
/*  84 */       jsonObj.put("content", this.content);
/*  85 */       if (this.extra != null)
/*  86 */         jsonObj.put("extra", this.extra);
/*     */     } catch (JSONException e) {
/*  88 */       e.printStackTrace();
/*     */     }
/*     */     try {
/*  91 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  93 */       e.printStackTrace();
/*     */     }
/*     */ 
/*  96 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.location.message.RealTimeLocationStartMessage
 * JD-Core Version:    0.6.0
 */
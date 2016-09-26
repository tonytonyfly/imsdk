/*     */ package io.rong.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:StkMsg", flag=3)
/*     */ public class StickerMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "StickerMessage";
/*     */   private String name;
/*     */   private String category;
/*     */   private boolean isInstalled;
/* 131 */   public static final Parcelable.Creator<StickerMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public StickerMessage createFromParcel(Parcel source)
/*     */     {
/* 135 */       return new StickerMessage(source);
/*     */     }
/*     */ 
/*     */     public StickerMessage[] newArray(int size)
/*     */     {
/* 140 */       return new StickerMessage[size];
/*     */     }
/* 131 */   };
/*     */ 
/*     */   public String getName()
/*     */   {
/*  27 */     return this.name;
/*     */   }
/*     */ 
/*     */   public String getCategory() {
/*  31 */     return this.category;
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  41 */     JSONObject jsonObj = new JSONObject();
/*     */     try {
/*  43 */       jsonObj.put("category", this.category);
/*  44 */       jsonObj.put("name", this.name);
/*  45 */       jsonObj.optBoolean("isInstalled", this.isInstalled);
/*     */     }
/*     */     catch (JSONException e) {
/*  48 */       RLog.e("StickerMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/*  52 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  54 */       e.printStackTrace();
/*     */     }
/*  56 */     return null;
/*     */   }
/*     */ 
/*     */   private StickerMessage(Builder builder) {
/*  60 */     this.category = builder.category;
/*  61 */     this.name = builder.name;
/*  62 */     this.isInstalled = builder.isInstalled;
/*     */   }
/*     */ 
/*     */   public StickerMessage(byte[] data) {
/*  66 */     String jsonStr = null;
/*     */     try {
/*  68 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  70 */       e.printStackTrace();
/*     */     }
/*     */     try {
/*  73 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*  74 */       this.category = jsonObj.optString("category");
/*  75 */       this.name = jsonObj.optString("name");
/*  76 */       this.isInstalled = jsonObj.optBoolean("isInstalled");
/*     */     }
/*     */     catch (JSONException e) {
/*  79 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 117 */     return 0;
/*     */   }
/*     */ 
/*     */   public StickerMessage(Parcel in) {
/* 121 */     this.category = ParcelUtils.readFromParcel(in);
/* 122 */     this.name = ParcelUtils.readFromParcel(in);
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 127 */     ParcelUtils.writeToParcel(dest, this.category);
/* 128 */     ParcelUtils.writeToParcel(dest, this.name);
/*     */   }
/*     */ 
/*     */   public static final class Builder
/*     */   {
/*     */     private String category;
/*     */     private String name;
/*     */     private boolean isInstalled;
/*     */ 
/*     */     public Builder()
/*     */     {
/*  90 */       this.category = null;
/*  91 */       this.name = null;
/*  92 */       this.isInstalled = false;
/*     */     }
/*     */ 
/*     */     public Builder category(String category) {
/*  96 */       this.category = category;
/*  97 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder name(String name) {
/* 101 */       this.name = name;
/* 102 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder isInstalled(boolean isInstalled) {
/* 106 */       this.isInstalled = isInstalled;
/* 107 */       return this;
/*     */     }
/*     */ 
/*     */     public StickerMessage build() {
/* 111 */       return new StickerMessage(this, null);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.StickerMessage
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.util.Log;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class RichContentItem
/*     */   implements Parcelable
/*     */ {
/*     */   private String title;
/*     */   private String digest;
/*     */   private String imageUrl;
/*     */   private String url;
/*  40 */   public static final Parcelable.Creator<RichContentItem> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public RichContentItem createFromParcel(Parcel source) {
/*  43 */       return new RichContentItem(source);
/*     */     }
/*     */ 
/*     */     public RichContentItem[] newArray(int size)
/*     */     {
/*  48 */       return new RichContentItem[0];
/*     */     }
/*  40 */   };
/*     */ 
/*     */   public int describeContents()
/*     */   {
/*  21 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/*  26 */     ParcelUtils.writeToParcel(dest, this.title);
/*  27 */     ParcelUtils.writeToParcel(dest, this.digest);
/*  28 */     ParcelUtils.writeToParcel(dest, this.imageUrl);
/*  29 */     ParcelUtils.writeToParcel(dest, this.url);
/*     */   }
/*     */ 
/*     */   public RichContentItem(Parcel in)
/*     */   {
/*  34 */     setTitle(ParcelUtils.readFromParcel(in));
/*  35 */     setDigest(ParcelUtils.readFromParcel(in));
/*  36 */     setImageUrl(ParcelUtils.readFromParcel(in));
/*  37 */     setUrl(ParcelUtils.readFromParcel(in));
/*     */   }
/*     */ 
/*     */   public RichContentItem(JSONObject jsonObj)
/*     */   {
/*  54 */     if (jsonObj != null) {
/*  55 */       if (jsonObj.has("title")) {
/*  56 */         setTitle(jsonObj.optString("title"));
/*     */       }
/*  58 */       if (jsonObj.has("digest")) {
/*  59 */         setDigest(jsonObj.optString("digest"));
/*     */       }
/*  61 */       if (jsonObj.has("picurl")) {
/*  62 */         setImageUrl(jsonObj.optString("picurl"));
/*     */       }
/*  64 */       if (jsonObj.has("url"))
/*  65 */         setUrl(jsonObj.optString("url"));
/*     */     }
/*     */   }
/*     */ 
/*     */   public RichContentItem(String jsonStr)
/*     */   {
/*     */     try
/*     */     {
/*  73 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*     */ 
/*  75 */       if (jsonObj.has("title")) {
/*  76 */         setTitle(jsonObj.optString("title"));
/*     */       }
/*  78 */       if (jsonObj.has("digest")) {
/*  79 */         setDigest(jsonObj.optString("digest"));
/*     */       }
/*  81 */       if (jsonObj.has("picurl")) {
/*  82 */         setImageUrl(jsonObj.optString("picurl"));
/*     */       }
/*  84 */       if (jsonObj.has("url"))
/*  85 */         setUrl(jsonObj.optString("url"));
/*     */     }
/*     */     catch (JSONException e) {
/*  88 */       Log.e("JSONException", e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getDigest()
/*     */   {
/*  95 */     return this.digest;
/*     */   }
/*     */ 
/*     */   public void setDigest(String digest) {
/*  99 */     this.digest = digest;
/*     */   }
/*     */ 
/*     */   public String getImageUrl() {
/* 103 */     return this.imageUrl;
/*     */   }
/*     */ 
/*     */   public void setImageUrl(String imageUrl) {
/* 107 */     this.imageUrl = imageUrl;
/*     */   }
/*     */ 
/*     */   public String getTitle() {
/* 111 */     return this.title;
/*     */   }
/*     */ 
/*     */   public void setTitle(String title) {
/* 115 */     this.title = title;
/*     */   }
/*     */ 
/*     */   public String getUrl() {
/* 119 */     return this.url;
/*     */   }
/*     */ 
/*     */   public void setUrl(String url) {
/* 123 */     this.url = url;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.RichContentItem
 * JD-Core Version:    0.6.0
 */
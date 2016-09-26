/*     */ package io.rong.message;
/*     */ 
/*     */ import android.net.Uri;
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import android.util.Log;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:LBSMsg", flag=3, messageHandler=LocationMessageHandler.class)
/*     */ public final class LocationMessage extends MessageContent
/*     */ {
/*     */   double mLat;
/*     */   double mLng;
/*     */   String mPoi;
/*     */   String mBase64;
/*     */   Uri mImgUri;
/*     */   protected String extra;
/* 257 */   public static final Parcelable.Creator<LocationMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public LocationMessage createFromParcel(Parcel source)
/*     */     {
/* 261 */       return new LocationMessage(source);
/*     */     }
/*     */ 
/*     */     public LocationMessage[] newArray(int size)
/*     */     {
/* 266 */       return new LocationMessage[size];
/*     */     }
/* 257 */   };
/*     */ 
/*     */   public String getExtra()
/*     */   {
/*  34 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra)
/*     */   {
/*  43 */     this.extra = extra;
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  49 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/*  52 */       if (!TextUtils.isEmpty(this.mBase64)) {
/*  53 */         jsonObj.put("content", this.mBase64);
/*     */       }
/*  57 */       else if (this.mImgUri != null) {
/*  58 */         jsonObj.put("content", this.mImgUri);
/*     */       }
/*     */ 
/*  61 */       jsonObj.put("latitude", this.mLat);
/*  62 */       jsonObj.put("longitude", this.mLng);
/*     */ 
/*  64 */       if (!TextUtils.isEmpty(getExtra())) {
/*  65 */         jsonObj.put("extra", getExtra());
/*     */       }
/*  67 */       if (!TextUtils.isEmpty(this.mPoi)) {
/*  68 */         jsonObj.put("poi", this.mPoi);
/*     */       }
/*  70 */       if (getJSONUserInfo() != null)
/*  71 */         jsonObj.putOpt("user", getJSONUserInfo());
/*     */     }
/*     */     catch (JSONException e)
/*     */     {
/*  75 */       Log.e("JSONException", e.getMessage());
/*     */     }
/*     */ 
/*  78 */     this.mBase64 = null;
/*     */ 
/*  80 */     return jsonObj.toString().getBytes();
/*     */   }
/*     */ 
/*     */   public LocationMessage(byte[] data)
/*     */   {
/*  85 */     String jsonStr = new String(data);
/*     */     try
/*     */     {
/*  88 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*     */ 
/*  90 */       setLat(jsonObj.getDouble("latitude"));
/*  91 */       setLng(jsonObj.getDouble("longitude"));
/*     */ 
/*  93 */       if (jsonObj.has("content")) {
/*  94 */         setBase64(jsonObj.optString("content"));
/*     */       }
/*     */ 
/*  97 */       if (jsonObj.has("extra"))
/*  98 */         setExtra(jsonObj.optString("extra"));
/*  99 */       setPoi(jsonObj.optString("poi"));
/*     */ 
/* 101 */       if (jsonObj.has("user"))
/* 102 */         setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
/*     */     } catch (JSONException e) {
/* 104 */       Log.e("JSONException", e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static LocationMessage obtain(double lat, double lng, String poi, Uri imgUri)
/*     */   {
/* 118 */     return new LocationMessage(lat, lng, poi, imgUri);
/*     */   }
/*     */ 
/*     */   private LocationMessage(double lat, double lng, String poi, Uri imgUri) {
/* 122 */     this.mLat = lat;
/* 123 */     this.mLng = lng;
/* 124 */     this.mPoi = poi;
/* 125 */     this.mImgUri = imgUri;
/*     */   }
/*     */ 
/*     */   public double getLat()
/*     */   {
/* 134 */     return this.mLat;
/*     */   }
/*     */ 
/*     */   public void setLat(double lat)
/*     */   {
/* 143 */     this.mLat = lat;
/*     */   }
/*     */ 
/*     */   public double getLng()
/*     */   {
/* 152 */     return this.mLng;
/*     */   }
/*     */ 
/*     */   public void setLng(double lng)
/*     */   {
/* 161 */     this.mLng = lng;
/*     */   }
/*     */ 
/*     */   public String getPoi()
/*     */   {
/* 170 */     return this.mPoi;
/*     */   }
/*     */ 
/*     */   public void setPoi(String poi)
/*     */   {
/* 179 */     this.mPoi = poi;
/*     */   }
/*     */ 
/*     */   public String getBase64()
/*     */   {
/* 188 */     return this.mBase64;
/*     */   }
/*     */ 
/*     */   public void setBase64(String base64)
/*     */   {
/* 197 */     this.mBase64 = base64;
/*     */   }
/*     */ 
/*     */   public Uri getImgUri()
/*     */   {
/* 206 */     return this.mImgUri;
/*     */   }
/*     */ 
/*     */   public void setImgUri(Uri imgUri)
/*     */   {
/* 215 */     this.mImgUri = imgUri;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 225 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 236 */     ParcelUtils.writeToParcel(dest, this.extra);
/* 237 */     ParcelUtils.writeToParcel(dest, Double.valueOf(this.mLat));
/* 238 */     ParcelUtils.writeToParcel(dest, Double.valueOf(this.mLng));
/* 239 */     ParcelUtils.writeToParcel(dest, this.mPoi);
/* 240 */     ParcelUtils.writeToParcel(dest, this.mImgUri);
/* 241 */     ParcelUtils.writeToParcel(dest, getUserInfo());
/*     */   }
/*     */ 
/*     */   public LocationMessage(Parcel in) {
/* 245 */     this.extra = ParcelUtils.readFromParcel(in);
/* 246 */     this.mLat = ParcelUtils.readDoubleFromParcel(in).doubleValue();
/* 247 */     this.mLng = ParcelUtils.readDoubleFromParcel(in).doubleValue();
/* 248 */     this.mPoi = ParcelUtils.readFromParcel(in);
/* 249 */     this.mImgUri = ((Uri)ParcelUtils.readFromParcel(in, Uri.class));
/* 250 */     setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.LocationMessage
 * JD-Core Version:    0.6.0
 */
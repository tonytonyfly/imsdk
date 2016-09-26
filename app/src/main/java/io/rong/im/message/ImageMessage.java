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
/*     */ @MessageTag(value="RC:ImgMsg", flag=3, messageHandler=ImageMessageHandler.class)
/*     */ public class ImageMessage extends MessageContent
/*     */ {
/*     */   private Uri mThumUri;
/*     */   private Uri mLocalUri;
/*     */   private Uri mRemoteUri;
/*  23 */   private boolean mUpLoadExp = false;
/*     */   private String mBase64;
/*     */   boolean mIsFull;
/*     */   protected String extra;
/* 311 */   public static final Parcelable.Creator<ImageMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public ImageMessage createFromParcel(Parcel source)
/*     */     {
/* 315 */       return new ImageMessage(source);
/*     */     }
/*     */ 
/*     */     public ImageMessage[] newArray(int size)
/*     */     {
/* 320 */       return new ImageMessage[size];
/*     */     }
/* 311 */   };
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
/*     */   public ImageMessage(byte[] data) {
/*  47 */     String jsonStr = new String(data);
/*     */     try
/*     */     {
/*  50 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*     */ 
/*  52 */       if (jsonObj.has("imageUri")) {
/*  53 */         String uri = jsonObj.optString("imageUri");
/*  54 */         if (!TextUtils.isEmpty(uri))
/*  55 */           setRemoteUri(Uri.parse(uri));
/*  56 */         if ((getRemoteUri() != null) && (getRemoteUri().getScheme() != null) && (getRemoteUri().getScheme().equals("file"))) {
/*  57 */           setLocalUri(getRemoteUri());
/*     */         }
/*     */       }
/*     */ 
/*  61 */       if (jsonObj.has("content")) {
/*  62 */         setBase64(jsonObj.optString("content"));
/*     */       }
/*  64 */       if (jsonObj.has("extra")) {
/*  65 */         setExtra(jsonObj.optString("extra"));
/*     */       }
/*  67 */       if (jsonObj.has("exp")) {
/*  68 */         setUpLoadExp(true);
/*     */       }
/*  70 */       if (jsonObj.has("isFull")) {
/*  71 */         setIsFull(jsonObj.optBoolean("isFull"));
/*     */       }
/*  73 */       if (jsonObj.has("user"))
/*  74 */         setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
/*     */     }
/*     */     catch (JSONException e) {
/*  77 */       Log.e("JSONException", e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public ImageMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   private ImageMessage(Uri thumbUri, Uri localUri) {
/*  86 */     this.mThumUri = thumbUri;
/*  87 */     this.mLocalUri = localUri;
/*     */   }
/*     */ 
/*     */   private ImageMessage(Uri thumbUri, Uri localUri, boolean original) {
/*  91 */     this.mThumUri = thumbUri;
/*  92 */     this.mLocalUri = localUri;
/*  93 */     this.mIsFull = original;
/*     */   }
/*     */ 
/*     */   public static ImageMessage obtain(Uri thumUri, Uri localUri)
/*     */   {
/* 104 */     return new ImageMessage(thumUri, localUri);
/*     */   }
/*     */ 
/*     */   public static ImageMessage obtain(Uri thumUri, Uri localUri, boolean isFull)
/*     */   {
/* 116 */     return new ImageMessage(thumUri, localUri, isFull);
/*     */   }
/*     */ 
/*     */   public static ImageMessage obtain()
/*     */   {
/* 125 */     return new ImageMessage();
/*     */   }
/*     */ 
/*     */   public Uri getThumUri()
/*     */   {
/* 134 */     return this.mThumUri;
/*     */   }
/*     */ 
/*     */   public boolean isFull()
/*     */   {
/* 143 */     return this.mIsFull;
/*     */   }
/*     */ 
/*     */   public void setIsFull(boolean isFull)
/*     */   {
/* 152 */     this.mIsFull = isFull;
/*     */   }
/*     */ 
/*     */   public void setThumUri(Uri thumUri)
/*     */   {
/* 161 */     this.mThumUri = thumUri;
/*     */   }
/*     */ 
/*     */   public Uri getLocalUri()
/*     */   {
/* 170 */     return this.mLocalUri;
/*     */   }
/*     */ 
/*     */   public void setLocalUri(Uri localUri)
/*     */   {
/* 179 */     this.mLocalUri = localUri;
/*     */   }
/*     */ 
/*     */   public Uri getRemoteUri()
/*     */   {
/* 188 */     return this.mRemoteUri;
/*     */   }
/*     */ 
/*     */   public void setRemoteUri(Uri remoteUri)
/*     */   {
/* 197 */     this.mRemoteUri = remoteUri;
/*     */   }
/*     */ 
/*     */   public void setBase64(String base64)
/*     */   {
/* 206 */     this.mBase64 = base64;
/*     */   }
/*     */ 
/*     */   public String getBase64()
/*     */   {
/* 215 */     return this.mBase64;
/*     */   }
/*     */ 
/*     */   public boolean isUpLoadExp()
/*     */   {
/* 224 */     return this.mUpLoadExp;
/*     */   }
/*     */ 
/*     */   public void setUpLoadExp(boolean upLoadExp)
/*     */   {
/* 233 */     this.mUpLoadExp = upLoadExp;
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/* 238 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/* 241 */       if (!TextUtils.isEmpty(this.mBase64))
/* 242 */         jsonObj.put("content", this.mBase64);
/*     */       else {
/* 244 */         Log.d("ImageMessage", "base64 is null");
/*     */       }
/*     */ 
/* 247 */       if (this.mRemoteUri != null)
/* 248 */         jsonObj.put("imageUri", this.mRemoteUri.toString());
/* 249 */       else if (getLocalUri() != null) {
/* 250 */         jsonObj.put("imageUri", getLocalUri().toString());
/*     */       }
/*     */ 
/* 253 */       if (this.mUpLoadExp) {
/* 254 */         jsonObj.put("exp", true);
/*     */       }
/* 256 */       jsonObj.put("isFull", this.mIsFull);
/* 257 */       if (!TextUtils.isEmpty(getExtra()))
/* 258 */         jsonObj.put("extra", getExtra());
/* 259 */       if (getJSONUserInfo() != null)
/* 260 */         jsonObj.putOpt("user", getJSONUserInfo());
/*     */     } catch (JSONException e) {
/* 262 */       Log.e("JSONException", e.getMessage());
/*     */     }
/* 264 */     this.mBase64 = null;
/* 265 */     return jsonObj.toString().getBytes();
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 275 */     return 0;
/*     */   }
/*     */ 
/*     */   public ImageMessage(Parcel in)
/*     */   {
/* 284 */     setExtra(ParcelUtils.readFromParcel(in));
/* 285 */     this.mLocalUri = ((Uri)ParcelUtils.readFromParcel(in, Uri.class));
/* 286 */     this.mRemoteUri = ((Uri)ParcelUtils.readFromParcel(in, Uri.class));
/* 287 */     this.mThumUri = ((Uri)ParcelUtils.readFromParcel(in, Uri.class));
/* 288 */     setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
/* 289 */     this.mIsFull = (ParcelUtils.readIntFromParcel(in).intValue() == 1);
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 300 */     ParcelUtils.writeToParcel(dest, getExtra());
/* 301 */     ParcelUtils.writeToParcel(dest, this.mLocalUri);
/* 302 */     ParcelUtils.writeToParcel(dest, this.mRemoteUri);
/* 303 */     ParcelUtils.writeToParcel(dest, this.mThumUri);
/* 304 */     ParcelUtils.writeToParcel(dest, getUserInfo());
/* 305 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.mIsFull ? 1 : 0));
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.ImageMessage
 * JD-Core Version:    0.6.0
 */
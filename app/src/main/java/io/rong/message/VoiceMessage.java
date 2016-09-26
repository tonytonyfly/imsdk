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
/*     */ @MessageTag(value="RC:VcMsg", flag=3, messageHandler=VoiceMessageHandler.class)
/*     */ public class VoiceMessage extends MessageContent
/*     */ {
/*     */   private Uri mUri;
/*     */   private int mDuration;
/*     */   private String mBase64;
/*     */   protected String extra;
/* 187 */   public static final Parcelable.Creator<VoiceMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public VoiceMessage createFromParcel(Parcel source)
/*     */     {
/* 191 */       return new VoiceMessage(source);
/*     */     }
/*     */ 
/*     */     public VoiceMessage[] newArray(int size)
/*     */     {
/* 196 */       return new VoiceMessage[size];
/*     */     }
/* 187 */   };
/*     */ 
/*     */   public String getExtra()
/*     */   {
/*  30 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra)
/*     */   {
/*  38 */     this.extra = extra;
/*     */   }
/*     */ 
/*     */   public VoiceMessage(Parcel in)
/*     */   {
/*  47 */     setExtra(ParcelUtils.readFromParcel(in));
/*  48 */     this.mUri = ((Uri)ParcelUtils.readFromParcel(in, Uri.class));
/*  49 */     this.mDuration = ParcelUtils.readIntFromParcel(in).intValue();
/*  50 */     setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
/*     */   }
/*     */ 
/*     */   public VoiceMessage(byte[] data)
/*     */   {
/*  55 */     String jsonStr = new String(data);
/*     */     try
/*     */     {
/*  58 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*     */ 
/*  60 */       if (jsonObj.has("duration")) {
/*  61 */         setDuration(jsonObj.optInt("duration"));
/*     */       }
/*  63 */       if (jsonObj.has("content")) {
/*  64 */         setBase64(jsonObj.optString("content"));
/*     */       }
/*  66 */       if (jsonObj.has("extra")) {
/*  67 */         setExtra(jsonObj.optString("extra"));
/*     */       }
/*  69 */       if (jsonObj.has("user"))
/*  70 */         setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
/*     */     }
/*     */     catch (JSONException e) {
/*  73 */       Log.e("JSONException", e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   private VoiceMessage(Uri uri, int duration)
/*     */   {
/*  85 */     this.mUri = uri;
/*  86 */     this.mDuration = duration;
/*     */   }
/*     */ 
/*     */   public static VoiceMessage obtain(Uri uri, int duration) {
/*  90 */     return new VoiceMessage(uri, duration);
/*     */   }
/*     */ 
/*     */   public Uri getUri()
/*     */   {
/*  99 */     return this.mUri;
/*     */   }
/*     */ 
/*     */   public void setUri(Uri uri)
/*     */   {
/* 108 */     this.mUri = uri;
/*     */   }
/*     */ 
/*     */   public int getDuration()
/*     */   {
/* 117 */     return this.mDuration;
/*     */   }
/*     */ 
/*     */   public void setDuration(int duration)
/*     */   {
/* 126 */     this.mDuration = duration;
/*     */   }
/*     */ 
/*     */   public String getBase64()
/*     */   {
/* 131 */     return this.mBase64;
/*     */   }
/*     */ 
/*     */   public void setBase64(String base64) {
/* 135 */     this.mBase64 = base64;
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/* 140 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/* 143 */       jsonObj.put("content", this.mBase64);
/* 144 */       jsonObj.put("duration", this.mDuration);
/*     */ 
/* 146 */       if (!TextUtils.isEmpty(getExtra())) {
/* 147 */         jsonObj.put("extra", this.extra);
/*     */       }
/* 149 */       if (getJSONUserInfo() != null)
/* 150 */         jsonObj.putOpt("user", getJSONUserInfo());
/*     */     } catch (JSONException e) {
/* 152 */       Log.e("JSONException", e.getMessage());
/*     */     }
/*     */ 
/* 155 */     this.mBase64 = null;
/*     */ 
/* 157 */     return jsonObj.toString().getBytes();
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 167 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 178 */     ParcelUtils.writeToParcel(dest, this.extra);
/* 179 */     ParcelUtils.writeToParcel(dest, this.mUri);
/* 180 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.mDuration));
/* 181 */     ParcelUtils.writeToParcel(dest, getUserInfo());
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.VoiceMessage
 * JD-Core Version:    0.6.0
 */
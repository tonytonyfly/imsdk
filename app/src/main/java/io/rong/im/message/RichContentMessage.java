/*     */ package io.rong.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.util.Log;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:ImgTextMsg", flag=3)
/*     */ public class RichContentMessage extends MessageContent
/*     */   implements Parcelable
/*     */ {
/*     */   private String title;
/*     */   private String content;
/*     */   private String imgUrl;
/*  25 */   private String url = "";
/*  26 */   private String extra = "";
/*     */ 
/* 196 */   public static final Parcelable.Creator<RichContentMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public RichContentMessage createFromParcel(Parcel source)
/*     */     {
/* 200 */       return new RichContentMessage(source);
/*     */     }
/*     */ 
/*     */     public RichContentMessage[] newArray(int size)
/*     */     {
/* 205 */       return new RichContentMessage[size];
/*     */     }
/* 196 */   };
/*     */ 
/*     */   public RichContentMessage(String title, String content, String imageUrl)
/*     */   {
/*  38 */     this.title = title;
/*  39 */     this.content = content;
/*  40 */     this.imgUrl = imageUrl;
/*     */   }
/*     */ 
/*     */   public RichContentMessage(String title, String content, String imageUrl, String url)
/*     */   {
/*  52 */     this.title = title;
/*  53 */     this.content = content;
/*  54 */     this.imgUrl = imageUrl;
/*  55 */     this.url = url;
/*     */   }
/*     */ 
/*     */   public static RichContentMessage obtain(String title, String content, String imageUrl)
/*     */   {
/*  67 */     return new RichContentMessage(title, content, imageUrl);
/*     */   }
/*     */ 
/*     */   public static RichContentMessage obtain(String title, String content, String imageUrl, String url)
/*     */   {
/*  80 */     return new RichContentMessage(title, content, imageUrl, url);
/*     */   }
/*     */ 
/*     */   protected RichContentMessage(Parcel in)
/*     */   {
/*  89 */     this.title = ParcelUtils.readFromParcel(in);
/*  90 */     this.content = ParcelUtils.readFromParcel(in);
/*  91 */     this.imgUrl = ParcelUtils.readFromParcel(in);
/*  92 */     this.url = ParcelUtils.readFromParcel(in);
/*  93 */     this.extra = ParcelUtils.readFromParcel(in);
/*  94 */     setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
/*     */   }
/*     */ 
/*     */   public RichContentMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public RichContentMessage(byte[] data)
/*     */   {
/* 111 */     String jsonStr = new String(data);
/*     */     try
/*     */     {
/* 115 */       JSONObject jsonObj = new JSONObject(jsonStr);
/* 116 */       this.title = jsonObj.optString("title");
/* 117 */       this.content = jsonObj.optString("content");
/* 118 */       this.imgUrl = jsonObj.optString("imageUri");
/* 119 */       this.url = jsonObj.optString("url");
/* 120 */       this.extra = jsonObj.optString("extra");
/*     */ 
/* 122 */       if (jsonObj.has("user"))
/* 123 */         setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
/*     */     }
/*     */     catch (JSONException e) {
/* 126 */       Log.e("JSONException", e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/* 139 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/* 143 */       jsonObj.put("title", getExpression(getTitle()));
/* 144 */       jsonObj.put("content", getExpression(getContent()));
/* 145 */       jsonObj.put("imageUri", getImgUrl());
/* 146 */       jsonObj.put("url", getUrl());
/* 147 */       jsonObj.put("extra", getExtra());
/*     */ 
/* 149 */       if (getJSONUserInfo() != null)
/* 150 */         jsonObj.putOpt("user", getJSONUserInfo());
/*     */     }
/*     */     catch (JSONException e)
/*     */     {
/* 154 */       Log.e("JSONException", e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 158 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/* 161 */       e.printStackTrace();
/*     */     }
/* 163 */     return new byte[0];
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 173 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 185 */     ParcelUtils.writeToParcel(dest, this.title);
/* 186 */     ParcelUtils.writeToParcel(dest, this.content);
/* 187 */     ParcelUtils.writeToParcel(dest, this.imgUrl);
/* 188 */     ParcelUtils.writeToParcel(dest, this.url);
/* 189 */     ParcelUtils.writeToParcel(dest, this.extra);
/* 190 */     ParcelUtils.writeToParcel(dest, getUserInfo());
/*     */   }
/*     */ 
/*     */   private String getExpression(String content)
/*     */   {
/* 212 */     Pattern pattern = Pattern.compile("\\[/u([0-9A-Fa-f]+)\\]");
/* 213 */     Matcher matcher = pattern.matcher(content);
/*     */ 
/* 215 */     StringBuffer sb = new StringBuffer();
/*     */ 
/* 217 */     while (matcher.find()) {
/* 218 */       matcher.appendReplacement(sb, toExpressionChar(matcher.group(1)));
/*     */     }
/*     */ 
/* 221 */     matcher.appendTail(sb);
/* 222 */     Log.d("getExpression--", sb.toString());
/*     */ 
/* 224 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   private String toExpressionChar(String expChar) {
/* 228 */     int inthex = Integer.parseInt(expChar, 16);
/* 229 */     return String.valueOf(Character.toChars(inthex));
/*     */   }
/*     */ 
/*     */   public String getTitle()
/*     */   {
/* 238 */     return this.title;
/*     */   }
/*     */ 
/*     */   public void setTitle(String title)
/*     */   {
/* 247 */     this.title = title;
/*     */   }
/*     */ 
/*     */   public String getContent()
/*     */   {
/* 256 */     return this.content;
/*     */   }
/*     */ 
/*     */   public void setContent(String content)
/*     */   {
/* 265 */     this.content = content;
/*     */   }
/*     */ 
/*     */   public String getImgUrl()
/*     */   {
/* 274 */     return this.imgUrl;
/*     */   }
/*     */ 
/*     */   public void setImgUrl(String url)
/*     */   {
/* 283 */     this.imgUrl = url;
/*     */   }
/*     */ 
/*     */   public String getUrl()
/*     */   {
/* 292 */     return this.url;
/*     */   }
/*     */ 
/*     */   public void setUrl(String url)
/*     */   {
/* 301 */     this.url = url;
/*     */   }
/*     */ 
/*     */   public String getExtra()
/*     */   {
/* 310 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra)
/*     */   {
/* 319 */     this.extra = extra;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.RichContentMessage
 * JD-Core Version:    0.6.0
 */
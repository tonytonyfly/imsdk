/*     */ package io.rong.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.MentionedInfo;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:TxtMsg", flag=3)
/*     */ public class TextMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "TextMessage";
/*     */   private String content;
/*     */   protected String extra;
/* 185 */   public static final Parcelable.Creator<TextMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public TextMessage createFromParcel(Parcel source)
/*     */     {
/* 189 */       return new TextMessage(source);
/*     */     }
/*     */ 
/*     */     public TextMessage[] newArray(int size)
/*     */     {
/* 194 */       return new TextMessage[size];
/*     */     }
/* 185 */   };
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
/*  54 */       jsonObj.put("content", getEmotion(getContent()));
/*     */ 
/*  56 */       if (!TextUtils.isEmpty(getExtra())) {
/*  57 */         jsonObj.put("extra", getExtra());
/*     */       }
/*  59 */       if (getJSONUserInfo() != null) {
/*  60 */         jsonObj.putOpt("user", getJSONUserInfo());
/*     */       }
/*  62 */       if (getJsonMentionInfo() != null)
/*  63 */         jsonObj.putOpt("mentionedInfo", getJsonMentionInfo());
/*     */     }
/*     */     catch (JSONException e) {
/*  66 */       RLog.e("TextMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/*  70 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/*  73 */       e.printStackTrace();
/*     */     }
/*  75 */     return null;
/*     */   }
/*     */ 
/*     */   private String getEmotion(String content)
/*     */   {
/*  80 */     Pattern pattern = Pattern.compile("\\[/u([0-9A-Fa-f]+)\\]");
/*  81 */     Matcher matcher = pattern.matcher(content);
/*     */ 
/*  83 */     StringBuffer sb = new StringBuffer();
/*     */ 
/*  85 */     while (matcher.find()) {
/*  86 */       int inthex = Integer.parseInt(matcher.group(1), 16);
/*  87 */       matcher.appendReplacement(sb, String.valueOf(Character.toChars(inthex)));
/*     */     }
/*     */ 
/*  90 */     matcher.appendTail(sb);
/*  91 */     RLog.d("TextMessage", "getEmotion--" + sb.toString());
/*     */ 
/*  93 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   protected TextMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public static TextMessage obtain(String text)
/*     */   {
/* 103 */     TextMessage model = new TextMessage();
/* 104 */     model.setContent(text);
/* 105 */     return model;
/*     */   }
/*     */ 
/*     */   public TextMessage(byte[] data) {
/* 109 */     String jsonStr = null;
/*     */     try {
/* 111 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 113 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 117 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*     */ 
/* 119 */       if (jsonObj.has("content")) {
/* 120 */         setContent(jsonObj.optString("content"));
/*     */       }
/* 122 */       if (jsonObj.has("extra")) {
/* 123 */         setExtra(jsonObj.optString("extra"));
/*     */       }
/* 125 */       if (jsonObj.has("user")) {
/* 126 */         setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
/*     */       }
/*     */ 
/* 129 */       if (jsonObj.has("mentionedInfo"))
/* 130 */         setMentionedInfo(parseJsonToMentionInfo(jsonObj.getJSONObject("mentionedInfo")));
/*     */     }
/*     */     catch (JSONException e) {
/* 133 */       RLog.e("TextMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setContent(String content)
/*     */   {
/* 144 */     this.content = content;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 153 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 164 */     ParcelUtils.writeToParcel(dest, getExtra());
/* 165 */     ParcelUtils.writeToParcel(dest, this.content);
/* 166 */     ParcelUtils.writeToParcel(dest, getUserInfo());
/* 167 */     ParcelUtils.writeToParcel(dest, getMentionedInfo());
/*     */   }
/*     */ 
/*     */   public TextMessage(Parcel in)
/*     */   {
/* 176 */     setExtra(ParcelUtils.readFromParcel(in));
/* 177 */     setContent(ParcelUtils.readFromParcel(in));
/* 178 */     setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
/* 179 */     setMentionedInfo((MentionedInfo)ParcelUtils.readFromParcel(in, MentionedInfo.class));
/*     */   }
/*     */ 
/*     */   public TextMessage(String content)
/*     */   {
/* 204 */     setContent(content);
/*     */   }
/*     */ 
/*     */   public String getContent()
/*     */   {
/* 213 */     return this.content;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.TextMessage
 * JD-Core Version:    0.6.0
 */
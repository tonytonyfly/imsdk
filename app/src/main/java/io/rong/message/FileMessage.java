/*     */ package io.rong.message;
/*     */ 
/*     */ import android.net.Uri;
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import java.io.File;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:FileMsg", flag=3)
/*     */ public class FileMessage extends MediaMessageContent
/*     */ {
/*     */   private static final String TAG = "FileMessage";
/*     */   private String mName;
/*     */   private long mSize;
/*     */   private String mType;
/* 209 */   public static final Parcelable.Creator<FileMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public FileMessage createFromParcel(Parcel source)
/*     */     {
/* 213 */       return new FileMessage(source);
/*     */     }
/*     */ 
/*     */     public FileMessage[] newArray(int size)
/*     */     {
/* 218 */       return new FileMessage[size];
/*     */     }
/* 209 */   };
/*     */ 
/*     */   public String getName()
/*     */   {
/*  31 */     return this.mName;
/*     */   }
/*     */ 
/*     */   public void setName(String Name) {
/*  35 */     this.mName = Name;
/*     */   }
/*     */ 
/*     */   public long getSize() {
/*  39 */     return this.mSize;
/*     */   }
/*     */ 
/*     */   public void setSize(long size) {
/*  43 */     this.mSize = size;
/*     */   }
/*     */ 
/*     */   public String getType() {
/*  47 */     return this.mType;
/*     */   }
/*     */ 
/*     */   public void setType(String type) {
/*  51 */     if (!TextUtils.isEmpty(type))
/*  52 */       this.mType = type;
/*     */     else
/*  54 */       this.mType = "bin";
/*     */   }
/*     */ 
/*     */   public Uri getFileUrl()
/*     */   {
/*  59 */     return getMediaUrl();
/*     */   }
/*     */ 
/*     */   public void setFileUrl(Uri uri) {
/*  63 */     setMediaUrl(uri);
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  74 */     JSONObject jsonObj = new JSONObject();
/*     */     try {
/*  76 */       if (!TextUtils.isEmpty(this.mName)) {
/*  77 */         jsonObj.put("name", this.mName);
/*     */       }
/*     */ 
/*  80 */       jsonObj.put("size", this.mSize);
/*     */ 
/*  82 */       if (!TextUtils.isEmpty(this.mType)) {
/*  83 */         jsonObj.put("type", this.mType);
/*     */       }
/*  85 */       if (getLocalPath() != null) {
/*  86 */         jsonObj.put("localPath", getLocalPath().toString());
/*     */       }
/*  88 */       if (getMediaUrl() != null) {
/*  89 */         jsonObj.put("fileUrl", getMediaUrl().toString());
/*     */       }
/*  91 */       if (!TextUtils.isEmpty(getExtra())) {
/*  92 */         jsonObj.put("extra", getExtra());
/*     */       }
/*  94 */       if (getJSONUserInfo() != null)
/*  95 */         jsonObj.putOpt("user", getJSONUserInfo());
/*     */     }
/*     */     catch (JSONException e) {
/*  98 */       RLog.e("FileMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try {
/* 101 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 103 */       e.printStackTrace();
/*     */     }
/* 105 */     return null;
/*     */   }
/*     */ 
/*     */   public FileMessage(byte[] data) {
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
/* 119 */       if (jsonObj.has("name"))
/* 120 */         setName(jsonObj.optString("name"));
/* 121 */       if (jsonObj.has("size"))
/* 122 */         setSize(jsonObj.getLong("size"));
/* 123 */       if (jsonObj.has("type"))
/* 124 */         setType(jsonObj.optString("type"));
/* 125 */       if (jsonObj.has("localPath"))
/* 126 */         setLocalPath(Uri.parse(jsonObj.optString("localPath")));
/* 127 */       if (jsonObj.has("fileUrl"))
/* 128 */         setFileUrl(Uri.parse(jsonObj.optString("fileUrl")));
/* 129 */       if (jsonObj.has("extra"))
/* 130 */         setExtra(jsonObj.optString("extra"));
/* 131 */       if (jsonObj.has("user"))
/* 132 */         setUserInfo(parseJsonToUserInfo(jsonObj.getJSONObject("user")));
/*     */     }
/*     */     catch (JSONException e) {
/* 135 */       RLog.e("FileMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   private FileMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   private FileMessage(File file, Uri localUrl)
/*     */   {
/* 145 */     setLocalPath(localUrl);
/* 146 */     this.mName = file.getName();
/* 147 */     this.mSize = file.length();
/*     */   }
/*     */ 
/*     */   public static FileMessage obtain(Uri localUrl)
/*     */   {
/* 155 */     if ((localUrl == null) || (!localUrl.toString().startsWith("file"))) {
/* 156 */       return null;
/*     */     }
/* 158 */     File file = new File(localUrl.toString().substring(7));
/* 159 */     if ((!file.exists()) || (!file.isFile())) {
/* 160 */       return null;
/*     */     }
/* 162 */     return new FileMessage(file, localUrl);
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 171 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 182 */     ParcelUtils.writeToParcel(dest, getExtra());
/* 183 */     ParcelUtils.writeToParcel(dest, getName());
/* 184 */     ParcelUtils.writeToParcel(dest, Long.valueOf(getSize()));
/* 185 */     ParcelUtils.writeToParcel(dest, getType());
/* 186 */     ParcelUtils.writeToParcel(dest, getLocalPath());
/* 187 */     ParcelUtils.writeToParcel(dest, getFileUrl());
/* 188 */     ParcelUtils.writeToParcel(dest, getUserInfo());
/*     */   }
/*     */ 
/*     */   public FileMessage(Parcel in)
/*     */   {
/* 197 */     setExtra(ParcelUtils.readFromParcel(in));
/* 198 */     setName(ParcelUtils.readFromParcel(in));
/* 199 */     setSize(ParcelUtils.readLongFromParcel(in).longValue());
/* 200 */     setType(ParcelUtils.readFromParcel(in));
/* 201 */     setLocalPath((Uri)ParcelUtils.readFromParcel(in, Uri.class));
/* 202 */     setFileUrl((Uri)ParcelUtils.readFromParcel(in, Uri.class));
/* 203 */     setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.FileMessage
 * JD-Core Version:    0.6.0
 */
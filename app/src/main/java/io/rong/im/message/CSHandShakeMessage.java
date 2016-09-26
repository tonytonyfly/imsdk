/*     */ package io.rong.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.CSCustomServiceInfo;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.List;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:CsHs", flag=0)
/*     */ public class CSHandShakeMessage extends MessageContent
/*     */ {
/*     */   private CSCustomServiceInfo customServiceInfo;
/* 146 */   public static final Parcelable.Creator<CSHandShakeMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CSHandShakeMessage createFromParcel(Parcel source)
/*     */     {
/* 150 */       return new CSHandShakeMessage(source);
/*     */     }
/*     */ 
/*     */     public CSHandShakeMessage[] newArray(int size)
/*     */     {
/* 155 */       return new CSHandShakeMessage[size];
/*     */     }
/* 146 */   };
/*     */ 
/*     */   public CSHandShakeMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setCustomInfo(CSCustomServiceInfo customInfo)
/*     */   {
/*  35 */     this.customServiceInfo = customInfo;
/*     */   }
/*     */ 
/*     */   public CSHandShakeMessage(byte[] data)
/*     */   {
/*     */   }
/*     */ 
/*     */   public static CSHandShakeMessage obtain()
/*     */   {
/*  47 */     return new CSHandShakeMessage();
/*     */   }
/*     */ 
/*     */   public CSHandShakeMessage(Parcel in) {
/*  51 */     this.customServiceInfo = ((CSCustomServiceInfo)ParcelUtils.readFromParcel(in, CSCustomServiceInfo.class));
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/*  61 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/*  72 */     ParcelUtils.writeToParcel(dest, this.customServiceInfo);
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  83 */     JSONObject jsonObj = new JSONObject();
/*  84 */     JSONObject jsonObj_UserInfo = new JSONObject();
/*  85 */     JSONObject jsonObj_ContactInfo = new JSONObject();
/*  86 */     JSONObject jsonObj_requestInfo = new JSONObject();
/*     */     try
/*     */     {
/*  89 */       jsonObj_UserInfo.put("userId", this.customServiceInfo.getUserId());
/*  90 */       jsonObj_UserInfo.put("nickName", this.customServiceInfo.getNickName());
/*  91 */       jsonObj_UserInfo.put("loginName", this.customServiceInfo.getLoginName());
/*  92 */       jsonObj_UserInfo.put("name", this.customServiceInfo.getName());
/*  93 */       jsonObj_UserInfo.put("grade", this.customServiceInfo.getGrade());
/*  94 */       jsonObj_UserInfo.put("gender", this.customServiceInfo.getGender());
/*  95 */       jsonObj_UserInfo.put("birthday", this.customServiceInfo.getBirthday());
/*  96 */       jsonObj_UserInfo.put("age", this.customServiceInfo.getAge());
/*  97 */       jsonObj_UserInfo.put("profession", this.customServiceInfo.getProfession());
/*  98 */       jsonObj_UserInfo.put("portraitUrl", this.customServiceInfo.getPortraitUrl());
/*  99 */       jsonObj_UserInfo.put("province", this.customServiceInfo.getProvince());
/* 100 */       jsonObj_UserInfo.put("city", this.customServiceInfo.getCity());
/* 101 */       jsonObj_UserInfo.put("memo", this.customServiceInfo.getMemo());
/*     */ 
/* 103 */       jsonObj.putOpt("userInfo", jsonObj_UserInfo);
/*     */ 
/* 106 */       jsonObj_ContactInfo.put("mobileNo", this.customServiceInfo.getMobileNo());
/* 107 */       jsonObj_ContactInfo.put("email", this.customServiceInfo.getEmail());
/* 108 */       jsonObj_ContactInfo.put("address", this.customServiceInfo.getAddress());
/* 109 */       jsonObj_ContactInfo.put("QQ", this.customServiceInfo.getQQ());
/* 110 */       jsonObj_ContactInfo.put("weibo", this.customServiceInfo.getWeibo());
/* 111 */       jsonObj_ContactInfo.put("weixin", this.customServiceInfo.getWeixin());
/*     */ 
/* 113 */       jsonObj.putOpt("contactInfo", jsonObj_ContactInfo);
/*     */ 
/* 116 */       jsonObj_requestInfo.put("page", this.customServiceInfo.getPage());
/* 117 */       jsonObj_requestInfo.put("referrer", this.customServiceInfo.getReferrer());
/* 118 */       jsonObj_requestInfo.put("enterUrl", this.customServiceInfo.getEnterUrl());
/* 119 */       jsonObj_requestInfo.put("skillId", this.customServiceInfo.getSkillId());
/* 120 */       JSONArray jsonListUrl = new JSONArray();
/* 121 */       List list = this.customServiceInfo.getListUrl();
/*     */       int i;
/* 122 */       if ((list != null) && (list.size() > 0))
/*     */       {
/* 124 */         i = 0;
/* 125 */         for (String u : list) {
/* 126 */           jsonListUrl.put(i, u);
/* 127 */           i++;
/*     */         }
/*     */       }
/* 130 */       jsonObj_requestInfo.put("listUrl", jsonListUrl);
/* 131 */       jsonObj_requestInfo.put("define", this.customServiceInfo.getDefine());
/*     */ 
/* 133 */       jsonObj.put("requestInfo", jsonObj_requestInfo);
/*     */     } catch (JSONException e) {
/* 135 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 139 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 141 */       e.printStackTrace();
/*     */     }
/* 143 */     return new byte[0];
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.CSHandShakeMessage
 * JD-Core Version:    0.6.0
 */
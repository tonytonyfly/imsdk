/*     */ package io.rong.imlib.model;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class CSCustomServiceInfo
/*     */   implements Parcelable
/*     */ {
/*     */   private static final String TAG = "CSCustomServiceInfo";
/*  19 */   private String userId = "";
/*  20 */   private String nickName = "";
/*  21 */   private String loginName = "";
/*  22 */   private String name = "";
/*  23 */   private String grade = "";
/*  24 */   private String gender = "";
/*  25 */   private String birthday = "";
/*  26 */   private String age = "";
/*  27 */   private String profession = "";
/*  28 */   private String portraitUrl = "";
/*  29 */   private String province = "";
/*  30 */   private String city = "";
/*  31 */   private String memo = "";
/*     */ 
/*  33 */   private String mobileNo = "";
/*  34 */   private String email = "";
/*  35 */   private String address = "";
/*  36 */   private String QQ = "";
/*  37 */   private String weibo = "";
/*  38 */   private String weixin = "";
/*     */ 
/*  40 */   private String page = "";
/*  41 */   private String referrer = "";
/*  42 */   private String enterUrl = "";
/*  43 */   private String skillId = "";
/*  44 */   public List<String> listUrl = new ArrayList();
/*  45 */   private String define = "";
/*     */ 
/* 715 */   public static final Parcelable.Creator<CSCustomServiceInfo> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CSCustomServiceInfo createFromParcel(Parcel source)
/*     */     {
/* 719 */       return new CSCustomServiceInfo(source);
/*     */     }
/*     */ 
/*     */     public CSCustomServiceInfo[] newArray(int size)
/*     */     {
/* 724 */       return new CSCustomServiceInfo[size];
/*     */     }
/* 715 */   };
/*     */ 
/*     */   public CSCustomServiceInfo()
/*     */   {
/*  48 */     if (RongIMClient.getInstance() != null)
/*  49 */       this.nickName = RongIMClient.getInstance().getCurrentUserId();
/*     */     else
/*  51 */       RLog.e("CSCustomServiceInfo", "JSONException CSCustomServiceInfo: RongIMClient.getInstance() is null");
/*     */   }
/*     */ 
/*     */   public CSCustomServiceInfo(Parcel in)
/*     */   {
/* 411 */     this.userId = ParcelUtils.readFromParcel(in);
/* 412 */     this.nickName = ParcelUtils.readFromParcel(in);
/* 413 */     this.loginName = ParcelUtils.readFromParcel(in);
/* 414 */     this.name = ParcelUtils.readFromParcel(in);
/* 415 */     this.grade = ParcelUtils.readFromParcel(in);
/* 416 */     this.gender = ParcelUtils.readFromParcel(in);
/* 417 */     this.birthday = ParcelUtils.readFromParcel(in);
/* 418 */     this.age = ParcelUtils.readFromParcel(in);
/* 419 */     this.profession = ParcelUtils.readFromParcel(in);
/* 420 */     this.portraitUrl = ParcelUtils.readFromParcel(in);
/* 421 */     this.province = ParcelUtils.readFromParcel(in);
/* 422 */     this.city = ParcelUtils.readFromParcel(in);
/* 423 */     this.memo = ParcelUtils.readFromParcel(in);
/*     */ 
/* 425 */     this.mobileNo = ParcelUtils.readFromParcel(in);
/* 426 */     this.email = ParcelUtils.readFromParcel(in);
/* 427 */     this.address = ParcelUtils.readFromParcel(in);
/* 428 */     this.QQ = ParcelUtils.readFromParcel(in);
/* 429 */     this.weibo = ParcelUtils.readFromParcel(in);
/* 430 */     this.weixin = ParcelUtils.readFromParcel(in);
/*     */ 
/* 432 */     this.page = ParcelUtils.readFromParcel(in);
/* 433 */     this.referrer = ParcelUtils.readFromParcel(in);
/* 434 */     this.enterUrl = ParcelUtils.readFromParcel(in);
/* 435 */     this.skillId = ParcelUtils.readFromParcel(in);
/* 436 */     this.listUrl = ParcelUtils.readListFromParcel(in, String.class);
/* 437 */     this.define = ParcelUtils.readFromParcel(in);
/*     */   }
/*     */ 
/*     */   public String getUserId()
/*     */   {
/* 446 */     return this.userId;
/*     */   }
/*     */ 
/*     */   public String getNickName()
/*     */   {
/* 455 */     return this.nickName;
/*     */   }
/*     */ 
/*     */   public String getLoginName()
/*     */   {
/* 464 */     return this.loginName;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 473 */     return this.name;
/*     */   }
/*     */ 
/*     */   public String getGrade()
/*     */   {
/* 482 */     return this.grade;
/*     */   }
/*     */ 
/*     */   public String getGender()
/*     */   {
/* 491 */     return this.gender;
/*     */   }
/*     */ 
/*     */   public String getBirthday()
/*     */   {
/* 500 */     return this.birthday;
/*     */   }
/*     */ 
/*     */   public String getAge()
/*     */   {
/* 509 */     return this.age;
/*     */   }
/*     */ 
/*     */   public String getProfession()
/*     */   {
/* 518 */     return this.profession;
/*     */   }
/*     */ 
/*     */   public String getPortraitUrl()
/*     */   {
/* 527 */     return this.portraitUrl;
/*     */   }
/*     */ 
/*     */   public String getProvince()
/*     */   {
/* 536 */     return this.province;
/*     */   }
/*     */ 
/*     */   public String getCity()
/*     */   {
/* 545 */     return this.city;
/*     */   }
/*     */ 
/*     */   public String getMemo()
/*     */   {
/* 554 */     return this.memo;
/*     */   }
/*     */ 
/*     */   public String getMobileNo()
/*     */   {
/* 563 */     return this.mobileNo;
/*     */   }
/*     */ 
/*     */   public String getEmail()
/*     */   {
/* 572 */     return this.email;
/*     */   }
/*     */ 
/*     */   public String getAddress()
/*     */   {
/* 581 */     return this.address;
/*     */   }
/*     */ 
/*     */   public String getQQ()
/*     */   {
/* 590 */     return this.QQ;
/*     */   }
/*     */ 
/*     */   public String getWeibo()
/*     */   {
/* 599 */     return this.weibo;
/*     */   }
/*     */ 
/*     */   public String getWeixin()
/*     */   {
/* 608 */     return this.weixin;
/*     */   }
/*     */ 
/*     */   public String getPage()
/*     */   {
/* 617 */     return this.page;
/*     */   }
/*     */ 
/*     */   public String getReferrer()
/*     */   {
/* 626 */     return this.referrer;
/*     */   }
/*     */ 
/*     */   public String getEnterUrl()
/*     */   {
/* 635 */     return this.enterUrl;
/*     */   }
/*     */ 
/*     */   public String getSkillId()
/*     */   {
/* 644 */     return this.skillId;
/*     */   }
/*     */ 
/*     */   public String getDefine()
/*     */   {
/* 653 */     return this.define;
/*     */   }
/*     */ 
/*     */   public List<String> getListUrl()
/*     */   {
/* 662 */     return this.listUrl;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 672 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 683 */     ParcelUtils.writeToParcel(dest, this.userId);
/* 684 */     ParcelUtils.writeToParcel(dest, this.nickName);
/* 685 */     ParcelUtils.writeToParcel(dest, this.loginName);
/* 686 */     ParcelUtils.writeToParcel(dest, this.name);
/* 687 */     ParcelUtils.writeToParcel(dest, this.grade);
/*     */ 
/* 689 */     ParcelUtils.writeToParcel(dest, this.gender);
/* 690 */     ParcelUtils.writeToParcel(dest, this.birthday);
/* 691 */     ParcelUtils.writeToParcel(dest, this.age);
/* 692 */     ParcelUtils.writeToParcel(dest, this.profession);
/* 693 */     ParcelUtils.writeToParcel(dest, this.portraitUrl);
/*     */ 
/* 695 */     ParcelUtils.writeToParcel(dest, this.province);
/* 696 */     ParcelUtils.writeToParcel(dest, this.city);
/* 697 */     ParcelUtils.writeToParcel(dest, this.memo);
/*     */ 
/* 699 */     ParcelUtils.writeToParcel(dest, this.mobileNo);
/* 700 */     ParcelUtils.writeToParcel(dest, this.email);
/* 701 */     ParcelUtils.writeToParcel(dest, this.address);
/* 702 */     ParcelUtils.writeToParcel(dest, this.QQ);
/* 703 */     ParcelUtils.writeToParcel(dest, this.weibo);
/* 704 */     ParcelUtils.writeToParcel(dest, this.weixin);
/*     */ 
/* 706 */     ParcelUtils.writeToParcel(dest, this.page);
/* 707 */     ParcelUtils.writeToParcel(dest, this.referrer);
/* 708 */     ParcelUtils.writeToParcel(dest, this.enterUrl);
/* 709 */     ParcelUtils.writeToParcel(dest, this.skillId);
/* 710 */     ParcelUtils.writeToParcel(dest, this.listUrl);
/* 711 */     ParcelUtils.writeToParcel(dest, this.define);
/*     */   }
/*     */ 
/*     */   public static class Builder
/*     */   {
/*     */     private String userId;
/*     */     private String nickName;
/*     */     private String loginName;
/*     */     private String name;
/*     */     private String grade;
/*     */     private String gender;
/*     */     private String birthday;
/*     */     private String age;
/*     */     private String profession;
/*     */     private String portraitUrl;
/*     */     private String province;
/*     */     private String city;
/*     */     private String memo;
/*     */     private String mobileNo;
/*     */     private String email;
/*     */     private String address;
/*     */     private String QQ;
/*     */     private String weibo;
/*     */     private String weixin;
/*  80 */     private String page = "";
/*  81 */     private String referrer = "";
/*  82 */     private String enterUrl = "";
/*  83 */     private String skillId = "";
/*  84 */     private List<String> listUrl = new ArrayList();
/*  85 */     private String define = "";
/*     */ 
/*     */     public CSCustomServiceInfo build() {
/*  88 */       if (RongIMClient.getInstance() == null)
/*  89 */         return null;
/*  90 */       CSCustomServiceInfo message = new CSCustomServiceInfo();
/*  91 */       CSCustomServiceInfo.access$002(message, this.userId != null ? this.userId : "");
/*  92 */       CSCustomServiceInfo.access$102(message, this.nickName != null ? this.nickName : RongIMClient.getInstance().getCurrentUserId());
/*  93 */       CSCustomServiceInfo.access$202(message, this.loginName != null ? this.loginName : "");
/*  94 */       CSCustomServiceInfo.access$302(message, this.name != null ? this.name : "");
/*  95 */       CSCustomServiceInfo.access$402(message, this.grade != null ? this.grade : "");
/*  96 */       CSCustomServiceInfo.access$502(message, this.gender != null ? this.gender : "");
/*  97 */       CSCustomServiceInfo.access$602(message, this.birthday != null ? this.birthday : "");
/*  98 */       CSCustomServiceInfo.access$702(message, this.age != null ? this.age : "");
/*     */ 
/* 100 */       CSCustomServiceInfo.access$802(message, this.profession != null ? this.profession : "");
/* 101 */       CSCustomServiceInfo.access$902(message, this.portraitUrl != null ? this.portraitUrl : "");
/* 102 */       CSCustomServiceInfo.access$1002(message, this.province != null ? this.province : "");
/* 103 */       CSCustomServiceInfo.access$1102(message, this.city != null ? this.city : "");
/* 104 */       CSCustomServiceInfo.access$1202(message, this.memo != null ? this.memo : "");
/*     */ 
/* 106 */       CSCustomServiceInfo.access$1302(message, this.mobileNo != null ? this.mobileNo : "");
/* 107 */       CSCustomServiceInfo.access$1402(message, this.email != null ? this.email : "");
/* 108 */       CSCustomServiceInfo.access$1502(message, this.address != null ? this.address : "");
/* 109 */       CSCustomServiceInfo.access$1602(message, this.QQ != null ? this.QQ : "");
/* 110 */       CSCustomServiceInfo.access$1702(message, this.weibo != null ? this.weibo : "");
/* 111 */       CSCustomServiceInfo.access$1802(message, this.weixin != null ? this.weixin : "");
/*     */ 
/* 113 */       CSCustomServiceInfo.access$1902(message, this.page != null ? this.page : "");
/* 114 */       CSCustomServiceInfo.access$2002(message, this.referrer != null ? this.referrer : "");
/* 115 */       CSCustomServiceInfo.access$2102(message, this.enterUrl != null ? this.enterUrl : "");
/* 116 */       CSCustomServiceInfo.access$2202(message, this.skillId != null ? this.skillId : "");
/* 117 */       message.listUrl = this.listUrl;
/* 118 */       CSCustomServiceInfo.access$2302(message, this.define != null ? this.define : "");
/* 119 */       return message;
/*     */     }
/*     */ 
/*     */     public Builder page(String page)
/*     */     {
/* 129 */       if (!TextUtils.isEmpty(page))
/* 130 */         this.page = page;
/* 131 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder nickName(String nickName)
/*     */     {
/* 141 */       if (!TextUtils.isEmpty(nickName))
/* 142 */         this.nickName = nickName;
/* 143 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder gender(String gender)
/*     */     {
/* 153 */       if (!TextUtils.isEmpty(gender))
/* 154 */         this.gender = gender;
/* 155 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder mobileNo(String mobileNo)
/*     */     {
/* 165 */       if (!TextUtils.isEmpty(mobileNo))
/* 166 */         this.mobileNo = mobileNo;
/* 167 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder memo(String memo)
/*     */     {
/* 177 */       if (!TextUtils.isEmpty(memo))
/* 178 */         this.memo = memo;
/* 179 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder name(String name)
/*     */     {
/* 189 */       if (!TextUtils.isEmpty(name))
/* 190 */         this.name = name;
/* 191 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder grade(String grade)
/*     */     {
/* 201 */       if (!TextUtils.isEmpty(grade))
/* 202 */         this.grade = grade;
/* 203 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder skillId(String skillId)
/*     */     {
/* 213 */       if (!TextUtils.isEmpty(skillId))
/* 214 */         this.skillId = skillId;
/* 215 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder userId(String userId)
/*     */     {
/* 225 */       if (!TextUtils.isEmpty(this.skillId))
/* 226 */         this.userId = userId;
/* 227 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder city(String city)
/*     */     {
/* 237 */       this.city = city;
/* 238 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder referrer(String referrer)
/*     */     {
/* 248 */       this.referrer = referrer;
/* 249 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder enterUrl(String enterUrl)
/*     */     {
/* 259 */       this.enterUrl = enterUrl;
/* 260 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder province(String province)
/*     */     {
/* 270 */       this.province = province;
/* 271 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder loginName(String loginName)
/*     */     {
/* 281 */       this.loginName = loginName;
/* 282 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder define(String define)
/*     */     {
/* 292 */       this.define = define;
/* 293 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder birthday(String birthday)
/*     */     {
/* 303 */       this.birthday = birthday;
/* 304 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder age(String age)
/*     */     {
/* 314 */       this.age = age;
/* 315 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder profession(String profession)
/*     */     {
/* 325 */       this.profession = profession;
/* 326 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder portraitUrl(String portraitUrl)
/*     */     {
/* 336 */       this.portraitUrl = portraitUrl;
/* 337 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder email(String email)
/*     */     {
/* 347 */       this.email = email;
/* 348 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder address(String address)
/*     */     {
/* 358 */       this.address = address;
/* 359 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder QQ(String QQ)
/*     */     {
/* 369 */       this.QQ = QQ;
/* 370 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder weibo(String weibo)
/*     */     {
/* 380 */       this.weibo = weibo;
/* 381 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder weixin(String weixin)
/*     */     {
/* 391 */       this.weixin = weixin;
/* 392 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder listUrl(List<String> listUrl)
/*     */     {
/* 402 */       if ((listUrl != null) && (!listUrl.isEmpty())) {
/* 403 */         for (String u : listUrl)
/* 404 */           this.listUrl.add(u);
/*     */       }
/* 406 */       return this;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.CSCustomServiceInfo
 * JD-Core Version:    0.6.0
 */
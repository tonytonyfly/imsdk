/*     */ package io.rong.imlib.model;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.net.ConnectivityManager;
/*     */ import android.net.NetworkInfo;
/*     */ import android.os.Build;
/*     */ import android.os.Build.VERSION;
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.telephony.TelephonyManager;
/*     */ import io.rong.common.ParcelUtils;
/*     */ 
/*     */ public final class UserData
/*     */   implements Parcelable
/*     */ {
/*     */   PersonalInfo personalInfo;
/*     */   AccountInfo accountInfo;
/*     */   ContactInfo contactInfo;
/*     */   ClientInfo clientInfo;
/*     */   String appVersion;
/*     */   String extra;
/*  97 */   public static final Parcelable.Creator<UserData> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public UserData createFromParcel(Parcel source)
/*     */     {
/* 101 */       return new UserData(source);
/*     */     }
/*     */ 
/*     */     public UserData[] newArray(int size)
/*     */     {
/* 106 */       return new UserData[size];
/*     */     }
/*  97 */   };
/*     */ 
/*     */   public UserData(Context context)
/*     */   {
/*  25 */     this.clientInfo = new ClientInfo(context);
/*     */   }
/*     */ 
/*     */   public UserData(Parcel in)
/*     */   {
/*  30 */     setPersonalInfo((PersonalInfo)ParcelUtils.readFromParcel(in, PersonalInfo.class));
/*  31 */     setAccountInfo((AccountInfo)ParcelUtils.readFromParcel(in, AccountInfo.class));
/*  32 */     setContactInfo((ContactInfo)ParcelUtils.readFromParcel(in, ContactInfo.class));
/*  33 */     this.clientInfo = ((ClientInfo)ParcelUtils.readFromParcel(in, ClientInfo.class));
/*  34 */     setAppVersion(ParcelUtils.readFromParcel(in));
/*  35 */     setExtra(ParcelUtils.readFromParcel(in));
/*     */   }
/*     */ 
/*     */   public PersonalInfo getPersonalInfo() {
/*  39 */     return this.personalInfo;
/*     */   }
/*     */ 
/*     */   public void setPersonalInfo(PersonalInfo personalInfo) {
/*  43 */     this.personalInfo = personalInfo;
/*     */   }
/*     */ 
/*     */   public AccountInfo getAccountInfo() {
/*  47 */     return this.accountInfo;
/*     */   }
/*     */ 
/*     */   public void setAccountInfo(AccountInfo accountInfo) {
/*  51 */     this.accountInfo = accountInfo;
/*     */   }
/*     */ 
/*     */   public ContactInfo getContactInfo() {
/*  55 */     return this.contactInfo;
/*     */   }
/*     */ 
/*     */   public void setContactInfo(ContactInfo contactInfo) {
/*  59 */     this.contactInfo = contactInfo;
/*     */   }
/*     */ 
/*     */   public String getAppVersion() {
/*  63 */     return this.appVersion;
/*     */   }
/*     */ 
/*     */   public void setAppVersion(String appVersion) {
/*  67 */     this.appVersion = appVersion;
/*     */   }
/*     */ 
/*     */   public ClientInfo getClientInfo() {
/*  71 */     return this.clientInfo;
/*     */   }
/*     */ 
/*     */   public String getExtra() {
/*  75 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra) {
/*  79 */     this.extra = extra;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/*  84 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/*  89 */     ParcelUtils.writeToParcel(dest, this.personalInfo);
/*  90 */     ParcelUtils.writeToParcel(dest, this.accountInfo);
/*  91 */     ParcelUtils.writeToParcel(dest, this.contactInfo);
/*  92 */     ParcelUtils.writeToParcel(dest, this.clientInfo);
/*  93 */     ParcelUtils.writeToParcel(dest, this.appVersion);
/*  94 */     ParcelUtils.writeToParcel(dest, this.extra);
/*     */   }
/*     */ 
/*     */   public static class ClientInfo
/*     */     implements Parcelable
/*     */   {
/*     */     String network;
/*     */     String carrier;
/*     */     String systemVersion;
/* 386 */     String os = "Android";
/*     */     String device;
/*     */     String mobilePhoneManufacturers;
/* 488 */     public static final Parcelable.Creator<ClientInfo> CREATOR = new Parcelable.Creator()
/*     */     {
/*     */       public UserData.ClientInfo createFromParcel(Parcel source)
/*     */       {
/* 492 */         return new UserData.ClientInfo(source);
/*     */       }
/*     */ 
/*     */       public UserData.ClientInfo[] newArray(int size)
/*     */       {
/* 497 */         return new UserData.ClientInfo[size];
/*     */       }
/* 488 */     };
/*     */ 
/*     */     public ClientInfo()
/*     */     {
/*     */     }
/*     */ 
/*     */     public ClientInfo(Context context)
/*     */     {
/*     */       try
/*     */       {
/* 395 */         TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService("phone");
/* 396 */         ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService("connectivity");
/*     */ 
/* 399 */         if ((connectivityManager != null) && (connectivityManager.getActiveNetworkInfo() != null)) {
/* 400 */           this.network = connectivityManager.getActiveNetworkInfo().getTypeName();
/*     */         }
/*     */ 
/* 403 */         if (telephonyManager != null)
/* 404 */           this.carrier = telephonyManager.getNetworkOperator();
/*     */       }
/*     */       catch (SecurityException e) {
/* 407 */         e.printStackTrace();
/*     */       }
/*     */ 
/* 410 */       this.mobilePhoneManufacturers = Build.MANUFACTURER;
/* 411 */       this.device = Build.MODEL;
/* 412 */       this.systemVersion = String.valueOf(Build.VERSION.SDK_INT);
/*     */     }
/*     */ 
/*     */     public ClientInfo(Parcel in)
/*     */     {
/* 417 */       setNetwork(ParcelUtils.readFromParcel(in));
/* 418 */       setCarrier(ParcelUtils.readFromParcel(in));
/* 419 */       setSystemVersion(ParcelUtils.readFromParcel(in));
/* 420 */       setOs(ParcelUtils.readFromParcel(in));
/* 421 */       setDevice(ParcelUtils.readFromParcel(in));
/* 422 */       setMobilePhoneManufacturers(ParcelUtils.readFromParcel(in));
/*     */     }
/*     */ 
/*     */     public String getNetwork() {
/* 426 */       return this.network;
/*     */     }
/*     */ 
/*     */     public void setNetwork(String network) {
/* 430 */       this.network = network;
/*     */     }
/*     */ 
/*     */     public String getCarrier() {
/* 434 */       return this.carrier;
/*     */     }
/*     */ 
/*     */     public void setCarrier(String carrier) {
/* 438 */       this.carrier = carrier;
/*     */     }
/*     */ 
/*     */     public String getSystemVersion() {
/* 442 */       return this.systemVersion;
/*     */     }
/*     */ 
/*     */     public void setSystemVersion(String systemVersion) {
/* 446 */       this.systemVersion = systemVersion;
/*     */     }
/*     */ 
/*     */     public String getOs() {
/* 450 */       return this.os;
/*     */     }
/*     */ 
/*     */     public void setOs(String os) {
/* 454 */       this.os = os;
/*     */     }
/*     */ 
/*     */     public String getDevice() {
/* 458 */       return this.device;
/*     */     }
/*     */ 
/*     */     public void setDevice(String device) {
/* 462 */       this.device = device;
/*     */     }
/*     */ 
/*     */     public String getMobilePhoneManufacturers() {
/* 466 */       return this.mobilePhoneManufacturers;
/*     */     }
/*     */ 
/*     */     public void setMobilePhoneManufacturers(String mobilePhoneManufacturers) {
/* 470 */       this.mobilePhoneManufacturers = mobilePhoneManufacturers;
/*     */     }
/*     */ 
/*     */     public int describeContents()
/*     */     {
/* 475 */       return 0;
/*     */     }
/*     */ 
/*     */     public void writeToParcel(Parcel dest, int flags)
/*     */     {
/* 480 */       ParcelUtils.writeToParcel(dest, this.network);
/* 481 */       ParcelUtils.writeToParcel(dest, this.carrier);
/* 482 */       ParcelUtils.writeToParcel(dest, this.systemVersion);
/* 483 */       ParcelUtils.writeToParcel(dest, this.os);
/* 484 */       ParcelUtils.writeToParcel(dest, this.device);
/* 485 */       ParcelUtils.writeToParcel(dest, this.mobilePhoneManufacturers);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ContactInfo
/*     */     implements Parcelable
/*     */   {
/*     */     String tel;
/*     */     String email;
/*     */     String address;
/*     */     String qq;
/*     */     String weibo;
/*     */     String weixin;
/* 367 */     public static final Parcelable.Creator<ContactInfo> CREATOR = new Parcelable.Creator()
/*     */     {
/*     */       public UserData.ContactInfo createFromParcel(Parcel source)
/*     */       {
/* 371 */         return new UserData.ContactInfo(source);
/*     */       }
/*     */ 
/*     */       public UserData.ContactInfo[] newArray(int size)
/*     */       {
/* 376 */         return new UserData.ContactInfo[size];
/*     */       }
/* 367 */     };
/*     */ 
/*     */     public ContactInfo()
/*     */     {
/*     */     }
/*     */ 
/*     */     public ContactInfo(Parcel in)
/*     */     {
/* 296 */       setTel(ParcelUtils.readFromParcel(in));
/* 297 */       setEmail(ParcelUtils.readFromParcel(in));
/* 298 */       setAddress(ParcelUtils.readFromParcel(in));
/* 299 */       setQQ(ParcelUtils.readFromParcel(in));
/* 300 */       setWeibo(ParcelUtils.readFromParcel(in));
/* 301 */       setWeixin(ParcelUtils.readFromParcel(in));
/*     */     }
/*     */ 
/*     */     public String getTel() {
/* 305 */       return this.tel;
/*     */     }
/*     */ 
/*     */     public void setTel(String tel) {
/* 309 */       this.tel = tel;
/*     */     }
/*     */ 
/*     */     public String getEmail() {
/* 313 */       return this.email;
/*     */     }
/*     */ 
/*     */     public void setEmail(String email) {
/* 317 */       this.email = email;
/*     */     }
/*     */ 
/*     */     public String getAddress() {
/* 321 */       return this.address;
/*     */     }
/*     */ 
/*     */     public void setAddress(String address) {
/* 325 */       this.address = address;
/*     */     }
/*     */ 
/*     */     public String getQQ() {
/* 329 */       return this.qq;
/*     */     }
/*     */ 
/*     */     public void setQQ(String qq) {
/* 333 */       this.qq = qq;
/*     */     }
/*     */ 
/*     */     public String getWeibo() {
/* 337 */       return this.weibo;
/*     */     }
/*     */ 
/*     */     public void setWeibo(String weibo) {
/* 341 */       this.weibo = weibo;
/*     */     }
/*     */ 
/*     */     public String getWeixin() {
/* 345 */       return this.weixin;
/*     */     }
/*     */ 
/*     */     public void setWeixin(String weixin) {
/* 349 */       this.weixin = weixin;
/*     */     }
/*     */ 
/*     */     public int describeContents()
/*     */     {
/* 354 */       return 0;
/*     */     }
/*     */ 
/*     */     public void writeToParcel(Parcel dest, int flags)
/*     */     {
/* 359 */       ParcelUtils.writeToParcel(dest, this.tel);
/* 360 */       ParcelUtils.writeToParcel(dest, this.email);
/* 361 */       ParcelUtils.writeToParcel(dest, this.address);
/* 362 */       ParcelUtils.writeToParcel(dest, this.qq);
/* 363 */       ParcelUtils.writeToParcel(dest, this.weibo);
/* 364 */       ParcelUtils.writeToParcel(dest, this.weixin);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class AccountInfo
/*     */     implements Parcelable
/*     */   {
/*     */     String appUserId;
/*     */     String userName;
/*     */     String nickName;
/* 269 */     public static final Parcelable.Creator<AccountInfo> CREATOR = new Parcelable.Creator()
/*     */     {
/*     */       public UserData.AccountInfo createFromParcel(Parcel source)
/*     */       {
/* 273 */         return new UserData.AccountInfo(source);
/*     */       }
/*     */ 
/*     */       public UserData.AccountInfo[] newArray(int size)
/*     */       {
/* 278 */         return new UserData.AccountInfo[size];
/*     */       }
/* 269 */     };
/*     */ 
/*     */     public AccountInfo()
/*     */     {
/*     */     }
/*     */ 
/*     */     public AccountInfo(Parcel in)
/*     */     {
/* 228 */       setAppUserId(ParcelUtils.readFromParcel(in));
/* 229 */       setUserName(ParcelUtils.readFromParcel(in));
/* 230 */       setNickName(ParcelUtils.readFromParcel(in));
/*     */     }
/*     */ 
/*     */     public String getUserName() {
/* 234 */       return this.userName;
/*     */     }
/*     */ 
/*     */     public void setUserName(String userName) {
/* 238 */       this.userName = userName;
/*     */     }
/*     */ 
/*     */     public String getNickName() {
/* 242 */       return this.nickName;
/*     */     }
/*     */ 
/*     */     public void setNickName(String nickName) {
/* 246 */       this.nickName = nickName;
/*     */     }
/*     */ 
/*     */     public String getAppUserId() {
/* 250 */       return this.appUserId;
/*     */     }
/*     */ 
/*     */     public void setAppUserId(String appUserId) {
/* 254 */       this.appUserId = appUserId;
/*     */     }
/*     */ 
/*     */     public int describeContents()
/*     */     {
/* 259 */       return 0;
/*     */     }
/*     */ 
/*     */     public void writeToParcel(Parcel dest, int flags)
/*     */     {
/* 264 */       ParcelUtils.writeToParcel(dest, this.appUserId);
/* 265 */       ParcelUtils.writeToParcel(dest, this.userName);
/* 266 */       ParcelUtils.writeToParcel(dest, this.nickName);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class PersonalInfo
/*     */     implements Parcelable
/*     */   {
/*     */     String realName;
/*     */     String sex;
/*     */     String birthday;
/*     */     String age;
/*     */     String job;
/*     */     String portraitUri;
/*     */     String comment;
/* 205 */     public static final Parcelable.Creator<PersonalInfo> CREATOR = new Parcelable.Creator()
/*     */     {
/*     */       public UserData.PersonalInfo createFromParcel(Parcel source)
/*     */       {
/* 209 */         return new UserData.PersonalInfo(source);
/*     */       }
/*     */ 
/*     */       public UserData.PersonalInfo[] newArray(int size)
/*     */       {
/* 214 */         return new UserData.PersonalInfo[size];
/*     */       }
/* 205 */     };
/*     */ 
/*     */     public PersonalInfo()
/*     */     {
/*     */     }
/*     */ 
/*     */     public PersonalInfo(Parcel in)
/*     */     {
/* 123 */       setRealName(ParcelUtils.readFromParcel(in));
/* 124 */       setSex(ParcelUtils.readFromParcel(in));
/* 125 */       setBirthday(ParcelUtils.readFromParcel(in));
/* 126 */       setAge(ParcelUtils.readFromParcel(in));
/* 127 */       setJob(ParcelUtils.readFromParcel(in));
/* 128 */       setPortraitUri(ParcelUtils.readFromParcel(in));
/* 129 */       setComment(ParcelUtils.readFromParcel(in));
/*     */     }
/*     */ 
/*     */     public String getRealName() {
/* 133 */       return this.realName;
/*     */     }
/*     */ 
/*     */     public void setRealName(String realName) {
/* 137 */       this.realName = realName;
/*     */     }
/*     */ 
/*     */     public String getSex() {
/* 141 */       return this.sex;
/*     */     }
/*     */ 
/*     */     public void setSex(String sex) {
/* 145 */       this.sex = sex;
/*     */     }
/*     */ 
/*     */     public String getBirthday() {
/* 149 */       return this.birthday;
/*     */     }
/*     */ 
/*     */     public void setBirthday(String birthday) {
/* 153 */       this.birthday = birthday;
/*     */     }
/*     */ 
/*     */     public String getAge() {
/* 157 */       return this.age;
/*     */     }
/*     */ 
/*     */     public void setAge(String age) {
/* 161 */       this.age = age;
/*     */     }
/*     */ 
/*     */     public String getJob() {
/* 165 */       return this.job;
/*     */     }
/*     */ 
/*     */     public void setJob(String job) {
/* 169 */       this.job = job;
/*     */     }
/*     */ 
/*     */     public String getPortraitUri() {
/* 173 */       return this.portraitUri;
/*     */     }
/*     */ 
/*     */     public void setPortraitUri(String portraitUri) {
/* 177 */       this.portraitUri = portraitUri;
/*     */     }
/*     */ 
/*     */     public String getComment() {
/* 181 */       return this.comment;
/*     */     }
/*     */ 
/*     */     public void setComment(String comment) {
/* 185 */       this.comment = comment;
/*     */     }
/*     */ 
/*     */     public int describeContents()
/*     */     {
/* 190 */       return 0;
/*     */     }
/*     */ 
/*     */     public void writeToParcel(Parcel dest, int flags)
/*     */     {
/* 195 */       ParcelUtils.writeToParcel(dest, this.realName);
/* 196 */       ParcelUtils.writeToParcel(dest, this.sex);
/* 197 */       ParcelUtils.writeToParcel(dest, this.birthday);
/* 198 */       ParcelUtils.writeToParcel(dest, this.age);
/* 199 */       ParcelUtils.writeToParcel(dest, this.job);
/* 200 */       ParcelUtils.writeToParcel(dest, this.portraitUri);
/* 201 */       ParcelUtils.writeToParcel(dest, this.comment);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.UserData
 * JD-Core Version:    0.6.0
 */
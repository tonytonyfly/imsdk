/*     */ package io.rong.imlib.model;
/*     */ 
/*     */ import android.net.Uri;
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.ParcelUtils;
/*     */ 
/*     */ public class UserInfo
/*     */   implements Parcelable
/*     */ {
/*     */   private String id;
/*     */   private String name;
/*     */   private Uri portraitUri;
/* 115 */   public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public UserInfo createFromParcel(Parcel source)
/*     */     {
/* 119 */       return new UserInfo(source);
/*     */     }
/*     */ 
/*     */     public UserInfo[] newArray(int size)
/*     */     {
/* 124 */       return new UserInfo[size];
/*     */     }
/* 115 */   };
/*     */ 
/*     */   public UserInfo(Parcel in)
/*     */   {
/*  20 */     setUserId(ParcelUtils.readFromParcel(in));
/*  21 */     setName(ParcelUtils.readFromParcel(in));
/*  22 */     setPortraitUri((Uri)ParcelUtils.readFromParcel(in, Uri.class));
/*     */   }
/*     */ 
/*     */   public UserInfo(String id, String name, Uri portraitUri)
/*     */   {
/*  34 */     if (TextUtils.isEmpty(id)) {
/*  35 */       throw new NullPointerException("userId is null");
/*     */     }
/*     */ 
/*  38 */     this.id = id;
/*  39 */     this.name = name;
/*  40 */     this.portraitUri = portraitUri;
/*     */   }
/*     */ 
/*     */   public String getUserId()
/*     */   {
/*  50 */     if (TextUtils.isEmpty(this.id)) {
/*  51 */       throw new NullPointerException("userId  is null");
/*     */     }
/*     */ 
/*  54 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setUserId(String userId)
/*     */   {
/*  63 */     this.id = userId;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  73 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/*  82 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public Uri getPortraitUri()
/*     */   {
/*  91 */     return this.portraitUri;
/*     */   }
/*     */ 
/*     */   public void setPortraitUri(Uri uri)
/*     */   {
/* 100 */     this.portraitUri = uri;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 105 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 110 */     ParcelUtils.writeToParcel(dest, getUserId());
/* 111 */     ParcelUtils.writeToParcel(dest, getName());
/* 112 */     ParcelUtils.writeToParcel(dest, getPortraitUri());
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.UserInfo
 * JD-Core Version:    0.6.0
 */
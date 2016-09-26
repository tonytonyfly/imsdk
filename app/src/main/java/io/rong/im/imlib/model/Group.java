/*     */ package io.rong.imlib.model;
/*     */ 
/*     */ import android.net.Uri;
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.ParcelUtils;
/*     */ 
/*     */ public class Group
/*     */   implements Parcelable
/*     */ {
/*     */   private String id;
/*     */   private String name;
/*     */   private Uri portraitUri;
/* 114 */   public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public Group createFromParcel(Parcel source)
/*     */     {
/* 118 */       return new Group(source);
/*     */     }
/*     */ 
/*     */     public Group[] newArray(int size)
/*     */     {
/* 123 */       return new Group[size];
/*     */     }
/* 114 */   };
/*     */ 
/*     */   public Group(Parcel in)
/*     */   {
/*  23 */     this(ParcelUtils.readFromParcel(in), ParcelUtils.readFromParcel(in), (Uri)ParcelUtils.readFromParcel(in, Uri.class));
/*     */   }
/*     */ 
/*     */   public Group(String id, String name, Uri portraitUri)
/*     */   {
/*  34 */     if ((TextUtils.isEmpty(id)) || (TextUtils.isEmpty(name))) {
/*  35 */       throw new RuntimeException("groupId or name is null");
/*     */     }
/*     */ 
/*  38 */     this.id = id;
/*  39 */     this.name = name;
/*  40 */     this.portraitUri = portraitUri;
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  49 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setId(String id)
/*     */   {
/*  58 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  67 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/*  76 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public Uri getPortraitUri()
/*     */   {
/*  85 */     return this.portraitUri;
/*     */   }
/*     */ 
/*     */   public void setPortraitUri(Uri uri)
/*     */   {
/*  94 */     this.portraitUri = uri;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 104 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 109 */     ParcelUtils.writeToParcel(dest, getId());
/* 110 */     ParcelUtils.writeToParcel(dest, getName());
/* 111 */     ParcelUtils.writeToParcel(dest, getPortraitUri());
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.Group
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.imlib.model;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import android.util.Log;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.imlib.NativeObject.DiscussionInfo;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ 
/*     */ public class Discussion
/*     */   implements Parcelable
/*     */ {
/*     */   private String id;
/*     */   private String name;
/*     */   private String creatorId;
/*  25 */   private boolean isOpen = true;
/*     */   private List<String> memberIdList;
/* 196 */   public static final Parcelable.Creator<Discussion> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public Discussion createFromParcel(Parcel source)
/*     */     {
/* 200 */       return new Discussion(source);
/*     */     }
/*     */ 
/*     */     public Discussion[] newArray(int size)
/*     */     {
/* 205 */       return new Discussion[size];
/*     */     }
/* 196 */   };
/*     */ 
/*     */   public Discussion(NativeObject.DiscussionInfo info)
/*     */   {
/*  34 */     this.id = info.getDiscussionId();
/*  35 */     this.name = info.getDiscussionName();
/*  36 */     this.creatorId = info.getAdminId();
/*  37 */     if (!TextUtils.isEmpty(info.getUserIds())) {
/*  38 */       this.memberIdList = Arrays.asList(info.getUserIds().split("\n"));
/*     */     }
/*  40 */     Log.d("Discussion", "info.getInviteStatus():" + info.getInviteStatus());
/*  41 */     this.isOpen = (info.getInviteStatus() != 1);
/*     */   }
/*     */ 
/*     */   public Discussion(Parcel in) {
/*  45 */     this(ParcelUtils.readFromParcel(in), ParcelUtils.readFromParcel(in), ParcelUtils.readFromParcel(in), ParcelUtils.readIntFromParcel(in).intValue() == 1, ParcelUtils.readListFromParcel(in, String.class));
/*     */   }
/*     */ 
/*     */   public Discussion(String id, String name)
/*     */   {
/*  59 */     this.id = id;
/*  60 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public Discussion(String id, String name, String creatorId, boolean isOpen, List<String> memberIdList)
/*     */   {
/*  74 */     this.id = id;
/*  75 */     this.name = name;
/*  76 */     this.creatorId = creatorId;
/*  77 */     this.isOpen = isOpen;
/*  78 */     this.memberIdList = memberIdList;
/*     */   }
/*     */ 
/*     */   public boolean isOpen()
/*     */   {
/*  87 */     return this.isOpen;
/*     */   }
/*     */ 
/*     */   public void setOpen(boolean isOpen)
/*     */   {
/*  96 */     this.isOpen = isOpen;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 105 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 114 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/* 123 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setId(String id)
/*     */   {
/* 132 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public String getCreatorId()
/*     */   {
/* 141 */     return this.creatorId;
/*     */   }
/*     */ 
/*     */   public void setCreatorId(String creatorId)
/*     */   {
/* 150 */     this.creatorId = creatorId;
/*     */   }
/*     */ 
/*     */   public List<String> getMemberIdList()
/*     */   {
/* 159 */     return this.memberIdList;
/*     */   }
/*     */ 
/*     */   public void setMemberIdList(List<String> memberIdList)
/*     */   {
/* 168 */     this.memberIdList = memberIdList;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 178 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 189 */     ParcelUtils.writeToParcel(dest, getId());
/* 190 */     ParcelUtils.writeToParcel(dest, getName());
/* 191 */     ParcelUtils.writeToParcel(dest, getCreatorId());
/* 192 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(isOpen() ? 1 : 0));
/* 193 */     ParcelUtils.writeToParcel(dest, getMemberIdList());
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.Discussion
 * JD-Core Version:    0.6.0
 */
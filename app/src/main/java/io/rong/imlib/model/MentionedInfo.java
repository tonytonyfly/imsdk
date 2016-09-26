/*     */ package io.rong.imlib.model;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.push.common.ParcelUtils;
/*     */ import java.util.List;
/*     */ 
/*     */ public class MentionedInfo
/*     */   implements Parcelable
/*     */ {
/*     */   private MentionedType type;
/*     */   private List<String> userIdList;
/*     */   private String mentionedContent;
/*  73 */   public static final Parcelable.Creator<MentionedInfo> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public MentionedInfo createFromParcel(Parcel source)
/*     */     {
/*  77 */       return new MentionedInfo(source);
/*     */     }
/*     */ 
/*     */     public MentionedInfo[] newArray(int size)
/*     */     {
/*  82 */       return new MentionedInfo[size];
/*     */     }
/*  73 */   };
/*     */ 
/*     */   public MentionedInfo()
/*     */   {
/*     */   }
/*     */ 
/*     */   public MentionedInfo(Parcel in)
/*     */   {
/*  19 */     setType(MentionedType.valueOf(ParcelUtils.readIntFromParcel(in).intValue()));
/*  20 */     setMentionedUserIdList(ParcelUtils.readListFromParcel(in, String.class));
/*  21 */     setMentionedContent(ParcelUtils.readFromParcel(in));
/*     */   }
/*     */ 
/*     */   public MentionedInfo(MentionedType type, List<String> userIdList, String mentionedContent) {
/*  25 */     if ((type != null) && (type.equals(MentionedType.ALL))) {
/*  26 */       this.userIdList = null;
/*  27 */     } else if ((type != null) && (type.equals(MentionedType.PART))) {
/*  28 */       if (userIdList == null)
/*  29 */         throw new IllegalArgumentException("When mentioned parts of the group memebers, userIdList can't be null!");
/*  30 */       this.userIdList = userIdList;
/*     */     }
/*     */ 
/*  33 */     this.type = type;
/*  34 */     this.mentionedContent = mentionedContent;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/*  39 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/*  44 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(getType().getValue()));
/*  45 */     ParcelUtils.writeToParcel(dest, getMentionedUserIdList());
/*  46 */     ParcelUtils.writeToParcel(dest, getMentionedContent());
/*     */   }
/*     */ 
/*     */   public MentionedType getType() {
/*  50 */     return this.type;
/*     */   }
/*     */ 
/*     */   public List<String> getMentionedUserIdList() {
/*  54 */     return this.userIdList;
/*     */   }
/*     */ 
/*     */   public String getMentionedContent() {
/*  58 */     return this.mentionedContent;
/*     */   }
/*     */ 
/*     */   public void setType(MentionedType type) {
/*  62 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public void setMentionedUserIdList(List<String> userList) {
/*  66 */     this.userIdList = userList;
/*     */   }
/*     */ 
/*     */   public void setMentionedContent(String content) {
/*  70 */     this.mentionedContent = content;
/*     */   }
/*     */ 
/*     */   public static enum MentionedType
/*     */   {
/*  87 */     ALL(1), 
/*  88 */     PART(2);
/*     */ 
/*     */     private int value;
/*     */ 
/*  93 */     private MentionedType(int value) { this.value = value; }
/*     */ 
/*     */     public int getValue()
/*     */     {
/*  97 */       return this.value;
/*     */     }
/*     */ 
/*     */     public static MentionedType valueOf(int value) {
/* 101 */       for (MentionedType type : values()) {
/* 102 */         if (type.getValue() == value)
/* 103 */           return type;
/*     */       }
/* 105 */       return ALL;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.MentionedInfo
 * JD-Core Version:    0.6.0
 */
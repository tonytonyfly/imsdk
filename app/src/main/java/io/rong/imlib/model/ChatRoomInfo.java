/*     */ package io.rong.imlib.model;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ChatRoomInfo
/*     */   implements Parcelable
/*     */ {
/*     */   private String chatRoomId;
/*     */   private ChatRoomMemberOrder order;
/*     */   private List<ChatRoomMemberInfo> memberInfo;
/*     */   private int totalMemberCount;
/*  74 */   public static final Parcelable.Creator<ChatRoomInfo> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public ChatRoomInfo createFromParcel(Parcel source)
/*     */     {
/*  78 */       return new ChatRoomInfo(source);
/*     */     }
/*     */ 
/*     */     public ChatRoomInfo[] newArray(int size)
/*     */     {
/*  83 */       return new ChatRoomInfo[size];
/*     */     }
/*  74 */   };
/*     */ 
/*     */   public ChatRoomInfo()
/*     */   {
/*     */   }
/*     */ 
/*     */   public String getChatRoomId()
/*     */   {
/*  21 */     return this.chatRoomId;
/*     */   }
/*     */ 
/*     */   public void setChatRoomId(String chatRoomId) {
/*  25 */     this.chatRoomId = chatRoomId;
/*     */   }
/*     */ 
/*     */   public ChatRoomMemberOrder getMemberOrder() {
/*  29 */     return this.order;
/*     */   }
/*     */ 
/*     */   public void setMemberOrder(ChatRoomMemberOrder order) {
/*  33 */     this.order = order;
/*     */   }
/*     */ 
/*     */   public void setUsers(List<ChatRoomMemberInfo> users) {
/*  37 */     this.memberInfo = users;
/*     */   }
/*     */ 
/*     */   public void setTotalMemberCount(int totalMemberCount) {
/*  41 */     this.totalMemberCount = totalMemberCount;
/*     */   }
/*     */ 
/*     */   public List<ChatRoomMemberInfo> getMemberInfo() {
/*  45 */     return this.memberInfo;
/*     */   }
/*     */ 
/*     */   public void setMemberInfo(List<ChatRoomMemberInfo> memberInfo) {
/*  49 */     this.memberInfo = memberInfo;
/*     */   }
/*     */ 
/*     */   public int getTotalMemberCount() {
/*  53 */     return this.totalMemberCount;
/*     */   }
/*     */ 
/*     */   public ChatRoomInfo(Parcel in) {
/*  57 */     this.chatRoomId = ParcelUtils.readFromParcel(in);
/*  58 */     this.totalMemberCount = ParcelUtils.readIntFromParcel(in).intValue();
/*  59 */     this.memberInfo = ParcelUtils.readListFromParcel(in, ChatRoomMemberInfo.class);
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/*  64 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/*  69 */     ParcelUtils.writeToParcel(dest, this.chatRoomId);
/*  70 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.totalMemberCount));
/*  71 */     ParcelUtils.writeToParcel(dest, this.memberInfo);
/*     */   }
/*     */ 
/*     */   public static enum ChatRoomMemberOrder
/*     */   {
/*  91 */     RC_CHAT_ROOM_MEMBER_ASC(1), 
/*     */ 
/*  96 */     RC_CHAT_ROOM_MEMBER_DESC(2);
/*     */ 
/*     */     int value;
/*     */ 
/* 100 */     private ChatRoomMemberOrder(int v) { this.value = v; }
/*     */ 
/*     */     public int getValue()
/*     */     {
/* 104 */       return this.value;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.ChatRoomInfo
 * JD-Core Version:    0.6.0
 */
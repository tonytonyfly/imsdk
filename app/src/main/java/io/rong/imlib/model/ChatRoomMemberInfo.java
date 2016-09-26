/*    */ package io.rong.imlib.model;
/*    */ 
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable;
/*    */ import android.os.Parcelable.Creator;
/*    */ import android.text.TextUtils;
/*    */ import io.rong.common.ParcelUtils;
/*    */ 
/*    */ public class ChatRoomMemberInfo
/*    */   implements Parcelable
/*    */ {
/*    */   private String id;
/*    */   private long joinTime;
/* 64 */   public static final Parcelable.Creator<ChatRoomMemberInfo> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public ChatRoomMemberInfo createFromParcel(Parcel source)
/*    */     {
/* 68 */       return new ChatRoomMemberInfo(source);
/*    */     }
/*    */ 
/*    */     public ChatRoomMemberInfo[] newArray(int size)
/*    */     {
/* 73 */       return new ChatRoomMemberInfo[size];
/*    */     }
/* 64 */   };
/*    */ 
/*    */   public ChatRoomMemberInfo()
/*    */   {
/*    */   }
/*    */ 
/*    */   public long getJoinTime()
/*    */   {
/* 17 */     return this.joinTime;
/*    */   }
/*    */ 
/*    */   public void setJoinTime(long joinTime) {
/* 21 */     this.joinTime = joinTime;
/*    */   }
/*    */ 
/*    */   public ChatRoomMemberInfo(Parcel in) {
/* 25 */     setUserId(ParcelUtils.readFromParcel(in));
/* 26 */     setJoinTime(ParcelUtils.readLongFromParcel(in).longValue());
/*    */   }
/*    */ 
/*    */   public String getUserId()
/*    */   {
/* 36 */     if (TextUtils.isEmpty(this.id)) {
/* 37 */       throw new NullPointerException("userId  is null");
/*    */     }
/*    */ 
/* 40 */     return this.id;
/*    */   }
/*    */ 
/*    */   public void setUserId(String userId)
/*    */   {
/* 49 */     this.id = userId;
/*    */   }
/*    */ 
/*    */   public int describeContents()
/*    */   {
/* 55 */     return 0;
/*    */   }
/*    */ 
/*    */   public void writeToParcel(Parcel dest, int flags)
/*    */   {
/* 60 */     ParcelUtils.writeToParcel(dest, getUserId());
/* 61 */     ParcelUtils.writeToParcel(dest, Long.valueOf(getJoinTime()));
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.ChatRoomMemberInfo
 * JD-Core Version:    0.6.0
 */
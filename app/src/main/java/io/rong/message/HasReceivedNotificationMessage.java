/*    */ package io.rong.message;
/*    */ 
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable.Creator;
/*    */ import io.rong.imlib.MessageTag;
/*    */ 
/*    */ @MessageTag("RC:RecNtf")
/*    */ public class HasReceivedNotificationMessage extends NotificationMessage
/*    */ {
/*    */   private boolean hasReceived;
/* 56 */   public static final Parcelable.Creator<HasReceivedNotificationMessage> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public HasReceivedNotificationMessage createFromParcel(Parcel source)
/*    */     {
/* 60 */       return new HasReceivedNotificationMessage(source);
/*    */     }
/*    */ 
/*    */     public HasReceivedNotificationMessage[] newArray(int size)
/*    */     {
/* 65 */       return new HasReceivedNotificationMessage[size];
/*    */     }
/* 56 */   };
/*    */ 
/*    */   public HasReceivedNotificationMessage(Parcel in)
/*    */   {
/*    */   }
/*    */ 
/*    */   public boolean isHasReceived()
/*    */   {
/* 22 */     return this.hasReceived;
/*    */   }
/*    */ 
/*    */   public void setHasReceived(boolean hasReceived)
/*    */   {
/* 31 */     this.hasReceived = hasReceived;
/*    */   }
/*    */ 
/*    */   public byte[] encode()
/*    */   {
/* 43 */     return new byte[0];
/*    */   }
/*    */ 
/*    */   public int describeContents()
/*    */   {
/* 48 */     return 0;
/*    */   }
/*    */ 
/*    */   public void writeToParcel(Parcel dest, int flags)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.HasReceivedNotificationMessage
 * JD-Core Version:    0.6.0
 */
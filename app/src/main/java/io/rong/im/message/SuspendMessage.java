/*    */ package io.rong.message;
/*    */ 
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable.Creator;
/*    */ import io.rong.imlib.MessageTag;
/*    */ import io.rong.imlib.model.MessageContent;
/*    */ 
/*    */ @MessageTag(value="RC:SpMsg", flag=0)
/*    */ public class SuspendMessage extends MessageContent
/*    */ {
/* 38 */   public static final Parcelable.Creator<SuspendMessage> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public SuspendMessage createFromParcel(Parcel source)
/*    */     {
/* 42 */       return new SuspendMessage(source);
/*    */     }
/*    */ 
/*    */     public SuspendMessage[] newArray(int size)
/*    */     {
/* 47 */       return new SuspendMessage[size];
/*    */     }
/* 38 */   };
/*    */ 
/*    */   public SuspendMessage()
/*    */   {
/*    */   }
/*    */ 
/*    */   public SuspendMessage(byte[] data)
/*    */   {
/*    */   }
/*    */ 
/*    */   public SuspendMessage(Parcel in)
/*    */   {
/*    */   }
/*    */ 
/*    */   public int describeContents()
/*    */   {
/* 25 */     return 0;
/*    */   }
/*    */ 
/*    */   public void writeToParcel(Parcel dest, int flags)
/*    */   {
/*    */   }
/*    */ 
/*    */   public byte[] encode()
/*    */   {
/* 35 */     return "{\"type\":1}".getBytes();
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.SuspendMessage
 * JD-Core Version:    0.6.0
 */
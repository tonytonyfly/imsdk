/*    */ package io.rong.imlib.model;
/*    */ 
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable.Creator;
/*    */ import io.rong.common.ParcelUtils;
/*    */ 
/*    */ public class UnknownMessage extends MessageContent
/*    */ {
/* 49 */   public static final Parcelable.Creator<UnknownMessage> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public UnknownMessage createFromParcel(Parcel source)
/*    */     {
/* 53 */       return new UnknownMessage(source);
/*    */     }
/*    */ 
/*    */     public UnknownMessage[] newArray(int size)
/*    */     {
/* 58 */       return new UnknownMessage[size];
/*    */     }
/* 49 */   };
/*    */ 
/*    */   public UnknownMessage(byte[] bytes)
/*    */   {
/*    */   }
/*    */ 
/*    */   public UnknownMessage()
/*    */   {
/*    */   }
/*    */ 
/*    */   public byte[] encode()
/*    */   {
/* 32 */     return new byte[0];
/*    */   }
/*    */ 
/*    */   public int describeContents()
/*    */   {
/* 37 */     return 0;
/*    */   }
/*    */ 
/*    */   public void writeToParcel(Parcel dest, int flags)
/*    */   {
/* 42 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(0));
/*    */   }
/*    */ 
/*    */   public UnknownMessage(Parcel in) {
/* 46 */     ParcelUtils.readIntFromParcel(in);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.UnknownMessage
 * JD-Core Version:    0.6.0
 */
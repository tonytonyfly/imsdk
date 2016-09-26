/*    */ package io.rong.message;
/*    */ 
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable.Creator;
/*    */ import io.rong.imlib.MessageTag;
/*    */ 
/*    */ @MessageTag(value="RC:HsMsg", flag=0)
/*    */ public class HandshakeMessage extends TextMessage
/*    */ {
/*    */   private int type;
/* 60 */   public static final Parcelable.Creator<HandshakeMessage> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public HandshakeMessage createFromParcel(Parcel source)
/*    */     {
/* 64 */       return new HandshakeMessage(source);
/*    */     }
/*    */ 
/*    */     public HandshakeMessage[] newArray(int size)
/*    */     {
/* 69 */       return new HandshakeMessage[size];
/*    */     }
/* 60 */   };
/*    */ 
/*    */   public HandshakeMessage()
/*    */   {
/*    */   }
/*    */ 
/*    */   public HandshakeMessage(byte[] data)
/*    */   {
/*    */   }
/*    */ 
/*    */   public static HandshakeMessage obtain(String text)
/*    */   {
/* 23 */     HandshakeMessage model = new HandshakeMessage();
/* 24 */     model.setContent(text);
/* 25 */     return model;
/*    */   }
/*    */ 
/*    */   public HandshakeMessage(Parcel in) {
/* 29 */     super(in);
/*    */   }
/*    */ 
/*    */   public int describeContents()
/*    */   {
/* 35 */     return 0;
/*    */   }
/*    */ 
/*    */   public void writeToParcel(Parcel dest, int flags)
/*    */   {
/* 40 */     super.writeToParcel(dest, flags);
/*    */   }
/*    */ 
/*    */   public byte[] encode()
/*    */   {
/* 45 */     return ("{\"type\":" + this.type + "}").getBytes();
/*    */   }
/*    */ 
/*    */   public int getType()
/*    */   {
/* 50 */     return this.type;
/*    */   }
/*    */ 
/*    */   public void setType(int type) {
/* 54 */     this.type = type;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.HandshakeMessage
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.imlib.location.message;
/*    */ 
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable.Creator;
/*    */ import io.rong.imlib.MessageTag;
/*    */ import io.rong.imlib.model.MessageContent;
/*    */ 
/*    */ @MessageTag(value="RC:RLEnd", flag=1)
/*    */ public class RealTimeLocationEndMessage extends MessageContent
/*    */ {
/* 15 */   private String content = "";
/* 16 */   private String extra = "";
/*    */ 
/* 52 */   public static final Parcelable.Creator<RealTimeLocationEndMessage> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public RealTimeLocationEndMessage createFromParcel(Parcel source)
/*    */     {
/* 56 */       return new RealTimeLocationEndMessage(source);
/*    */     }
/*    */ 
/*    */     public RealTimeLocationEndMessage[] newArray(int size)
/*    */     {
/* 61 */       return new RealTimeLocationEndMessage[size];
/*    */     }
/* 52 */   };
/*    */ 
/*    */   public RealTimeLocationEndMessage(String content)
/*    */   {
/* 19 */     this.content = content;
/*    */   }
/*    */ 
/*    */   public RealTimeLocationEndMessage(byte[] data)
/*    */   {
/*    */   }
/*    */ 
/*    */   public static RealTimeLocationEndMessage obtain(String content) {
/* 27 */     return new RealTimeLocationEndMessage(content);
/*    */   }
/*    */ 
/*    */   public RealTimeLocationEndMessage(Parcel in) {
/* 31 */     this.content = in.readString();
/*    */   }
/*    */ 
/*    */   public int describeContents()
/*    */   {
/* 36 */     return 0;
/*    */   }
/*    */ 
/*    */   public void writeToParcel(Parcel dest, int flags)
/*    */   {
/* 41 */     dest.writeString(this.content);
/*    */   }
/*    */ 
/*    */   public byte[] encode()
/*    */   {
/* 46 */     return new byte[0];
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.location.message.RealTimeLocationEndMessage
 * JD-Core Version:    0.6.0
 */
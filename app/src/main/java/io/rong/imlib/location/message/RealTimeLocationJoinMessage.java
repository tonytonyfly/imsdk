/*    */ package io.rong.imlib.location.message;
/*    */ 
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable.Creator;
/*    */ import io.rong.imlib.MessageTag;
/*    */ import io.rong.imlib.model.MessageContent;
/*    */ 
/*    */ @MessageTag(value="RC:RLJoin", flag=0)
/*    */ public class RealTimeLocationJoinMessage extends MessageContent
/*    */ {
/* 15 */   private String content = "";
/* 16 */   private String extra = "";
/*    */ 
/* 49 */   public static final Parcelable.Creator<RealTimeLocationJoinMessage> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public RealTimeLocationJoinMessage createFromParcel(Parcel source)
/*    */     {
/* 53 */       return new RealTimeLocationJoinMessage(source);
/*    */     }
/*    */ 
/*    */     public RealTimeLocationJoinMessage[] newArray(int size)
/*    */     {
/* 58 */       return new RealTimeLocationJoinMessage[size];
/*    */     }
/* 49 */   };
/*    */ 
/*    */   public RealTimeLocationJoinMessage(String content)
/*    */   {
/* 19 */     this.content = content;
/*    */   }
/*    */ 
/*    */   public RealTimeLocationJoinMessage(byte[] data)
/*    */   {
/*    */   }
/*    */ 
/*    */   public static RealTimeLocationJoinMessage obtain(String content) {
/* 27 */     return new RealTimeLocationJoinMessage(content);
/*    */   }
/*    */ 
/*    */   public RealTimeLocationJoinMessage(Parcel in) {
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
 * Qualified Name:     io.rong.imlib.location.message.RealTimeLocationJoinMessage
 * JD-Core Version:    0.6.0
 */
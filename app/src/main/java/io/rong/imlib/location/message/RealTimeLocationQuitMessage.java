/*    */ package io.rong.imlib.location.message;
/*    */ 
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable.Creator;
/*    */ import io.rong.imlib.MessageTag;
/*    */ import io.rong.imlib.model.MessageContent;
/*    */ 
/*    */ @MessageTag(value="RC:RLQuit", flag=0)
/*    */ public class RealTimeLocationQuitMessage extends MessageContent
/*    */ {
/* 16 */   private String content = "";
/* 17 */   private String extra = "";
/*    */ 
/* 53 */   public static final Parcelable.Creator<RealTimeLocationQuitMessage> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public RealTimeLocationQuitMessage createFromParcel(Parcel source)
/*    */     {
/* 57 */       return new RealTimeLocationQuitMessage(source);
/*    */     }
/*    */ 
/*    */     public RealTimeLocationQuitMessage[] newArray(int size)
/*    */     {
/* 62 */       return new RealTimeLocationQuitMessage[size];
/*    */     }
/* 53 */   };
/*    */ 
/*    */   public RealTimeLocationQuitMessage(String content)
/*    */   {
/* 20 */     this.content = content;
/*    */   }
/*    */ 
/*    */   public RealTimeLocationQuitMessage(byte[] data)
/*    */   {
/*    */   }
/*    */ 
/*    */   public static RealTimeLocationQuitMessage obtain(String content) {
/* 28 */     return new RealTimeLocationQuitMessage(content);
/*    */   }
/*    */ 
/*    */   public RealTimeLocationQuitMessage(Parcel in) {
/* 32 */     this.content = in.readString();
/*    */   }
/*    */ 
/*    */   public int describeContents()
/*    */   {
/* 37 */     return 0;
/*    */   }
/*    */ 
/*    */   public void writeToParcel(Parcel dest, int flags)
/*    */   {
/* 42 */     dest.writeString(this.content);
/*    */   }
/*    */ 
/*    */   public byte[] encode()
/*    */   {
/* 47 */     return new byte[0];
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.location.message.RealTimeLocationQuitMessage
 * JD-Core Version:    0.6.0
 */
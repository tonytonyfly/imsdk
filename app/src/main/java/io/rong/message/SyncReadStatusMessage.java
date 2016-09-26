/*    */ package io.rong.message;
/*    */ 
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable.Creator;
/*    */ import io.rong.common.ParcelUtils;
/*    */ import io.rong.common.RLog;
/*    */ import io.rong.imlib.MessageTag;
/*    */ import io.rong.imlib.model.MessageContent;
/*    */ import java.io.UnsupportedEncodingException;
/*    */ import org.json.JSONException;
/*    */ import org.json.JSONObject;
/*    */ 
/*    */ @MessageTag("RC:SRSMsg")
/*    */ public class SyncReadStatusMessage extends MessageContent
/*    */ {
/*    */   private static final String TAG = "SyncReadStatusMessage";
/*    */   private long lastMessageSendTime;
/* 85 */   public static final Parcelable.Creator<SyncReadStatusMessage> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public SyncReadStatusMessage createFromParcel(Parcel source) {
/* 88 */       return new SyncReadStatusMessage(source);
/*    */     }
/*    */ 
/*    */     public SyncReadStatusMessage[] newArray(int size)
/*    */     {
/* 93 */       return new SyncReadStatusMessage[size];
/*    */     }
/* 85 */   };
/*    */ 
/*    */   public long getLastMessageSendTime()
/*    */   {
/* 20 */     return this.lastMessageSendTime;
/*    */   }
/*    */ 
/*    */   public SyncReadStatusMessage(long lastMessageSendTime)
/*    */   {
/* 26 */     this.lastMessageSendTime = lastMessageSendTime;
/*    */   }
/*    */ 
/*    */   public SyncReadStatusMessage(Parcel in) {
/* 30 */     this.lastMessageSendTime = ParcelUtils.readLongFromParcel(in).longValue();
/*    */   }
/*    */ 
/*    */   public SyncReadStatusMessage(byte[] data) {
/* 34 */     String jsonStr = null;
/*    */     try
/*    */     {
/* 37 */       jsonStr = new String(data, "UTF-8");
/*    */     } catch (UnsupportedEncodingException e) {
/* 39 */       RLog.e("SyncReadStatusMessage", e.getMessage());
/*    */     }
/*    */     try
/*    */     {
/* 43 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*    */ 
/* 45 */       if (jsonObj.has("lastMessageSendTime"))
/* 46 */         this.lastMessageSendTime = jsonObj.getLong("lastMessageSendTime");
/*    */     }
/*    */     catch (JSONException e) {
/* 49 */       RLog.e("SyncReadStatusMessage", e.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public byte[] encode()
/*    */   {
/* 55 */     JSONObject jsonObj = new JSONObject();
/*    */     try
/*    */     {
/* 58 */       jsonObj.put("lastMessageSendTime", this.lastMessageSendTime);
/*    */     } catch (JSONException e) {
/* 60 */       RLog.e("SyncReadStatusMessage", "JSONException " + e.getMessage());
/*    */     }
/*    */     try
/*    */     {
/* 64 */       return jsonObj.toString().getBytes("UTF-8");
/*    */     }
/*    */     catch (UnsupportedEncodingException e) {
/* 67 */       e.printStackTrace();
/*    */     }
/* 69 */     return null;
/*    */   }
/*    */ 
/*    */   public int describeContents()
/*    */   {
/* 74 */     return 0;
/*    */   }
/*    */ 
/*    */   public void writeToParcel(Parcel dest, int flags)
/*    */   {
/* 79 */     ParcelUtils.writeToParcel(dest, Long.valueOf(this.lastMessageSendTime));
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.SyncReadStatusMessage
 * JD-Core Version:    0.6.0
 */
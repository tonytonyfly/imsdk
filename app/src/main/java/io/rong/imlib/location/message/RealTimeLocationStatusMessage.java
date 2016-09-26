/*    */ package io.rong.imlib.location.message;
/*    */ 
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable.Creator;
/*    */ import android.util.Log;
/*    */ import io.rong.imlib.MessageTag;
/*    */ import io.rong.imlib.model.MessageContent;
/*    */ import org.json.JSONException;
/*    */ import org.json.JSONObject;
/*    */ 
/*    */ @MessageTag(value="RC:RL", flag=0)
/*    */ public class RealTimeLocationStatusMessage extends MessageContent
/*    */ {
/* 19 */   private double latitude = 0.0D;
/* 20 */   private double longitude = 0.0D;
/*    */ 
/* 85 */   public static final Parcelable.Creator<RealTimeLocationStatusMessage> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public RealTimeLocationStatusMessage createFromParcel(Parcel source)
/*    */     {
/* 89 */       return new RealTimeLocationStatusMessage(source);
/*    */     }
/*    */ 
/*    */     public RealTimeLocationStatusMessage[] newArray(int size)
/*    */     {
/* 94 */       return new RealTimeLocationStatusMessage[size];
/*    */     }
/* 85 */   };
/*    */ 
/*    */   public RealTimeLocationStatusMessage()
/*    */   {
/*    */   }
/*    */ 
/*    */   public RealTimeLocationStatusMessage(byte[] data)
/*    */   {
/* 27 */     String jsonStr = new String(data);
/*    */     try {
/* 29 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*    */ 
/* 31 */       if (jsonObj.has("latitude"))
/* 32 */         this.latitude = jsonObj.optDouble("latitude");
/* 33 */       if (jsonObj.has("longitude"))
/* 34 */         this.longitude = jsonObj.optDouble("longitude");
/*    */     }
/*    */     catch (JSONException e) {
/* 37 */       Log.e("JSONException", e.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public static RealTimeLocationStatusMessage obtain(double latitude, double longitude) {
/* 42 */     RealTimeLocationStatusMessage model = new RealTimeLocationStatusMessage();
/* 43 */     model.latitude = latitude;
/* 44 */     model.longitude = longitude;
/* 45 */     return model;
/*    */   }
/*    */ 
/*    */   public RealTimeLocationStatusMessage(Parcel in) {
/* 49 */     this.latitude = in.readDouble();
/* 50 */     this.longitude = in.readDouble();
/*    */   }
/*    */ 
/*    */   public double getLatitude() {
/* 54 */     return this.latitude;
/*    */   }
/*    */ 
/*    */   public double getLongitude() {
/* 58 */     return this.longitude;
/*    */   }
/*    */ 
/*    */   public int describeContents()
/*    */   {
/* 63 */     return 0;
/*    */   }
/*    */ 
/*    */   public void writeToParcel(Parcel dest, int flags)
/*    */   {
/* 68 */     dest.writeDouble(this.latitude);
/* 69 */     dest.writeDouble(this.longitude);
/*    */   }
/*    */ 
/*    */   public byte[] encode()
/*    */   {
/* 74 */     JSONObject jsonObj = new JSONObject();
/*    */     try {
/* 76 */       jsonObj.put("latitude", this.latitude);
/* 77 */       jsonObj.put("longitude", this.longitude);
/*    */     } catch (JSONException e) {
/* 79 */       Log.e("JSONException", e.getMessage());
/*    */     }
/*    */ 
/* 82 */     return jsonObj.toString().getBytes();
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.location.message.RealTimeLocationStatusMessage
 * JD-Core Version:    0.6.0
 */
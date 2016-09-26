/*     */ package io.rong.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:CsEnd", flag=0)
/*     */ public class CSTerminateMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "CSTerminateMessage";
/*     */   private int code;
/*     */   private String msg;
/*     */   private String sid;
/* 116 */   public static final Parcelable.Creator<CSTerminateMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CSTerminateMessage createFromParcel(Parcel source)
/*     */     {
/* 120 */       return new CSTerminateMessage(source);
/*     */     }
/*     */ 
/*     */     public CSTerminateMessage[] newArray(int size)
/*     */     {
/* 125 */       return new CSTerminateMessage[size];
/*     */     }
/* 116 */   };
/*     */ 
/*     */   public CSTerminateMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CSTerminateMessage(byte[] content)
/*     */   {
/*  32 */     String jsonStr = null;
/*     */     try {
/*  34 */       jsonStr = new String(content, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  36 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/*  40 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*  41 */       this.code = jsonObj.optInt("code");
/*  42 */       this.msg = jsonObj.optString("msg");
/*  43 */       this.sid = jsonObj.optString("sid");
/*     */     } catch (JSONException e) {
/*  45 */       RLog.e("CSTerminateMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static CSTerminateMessage obtain() {
/*  50 */     return new CSTerminateMessage();
/*     */   }
/*     */ 
/*     */   public CSTerminateMessage(Parcel in) {
/*  54 */     this.code = ParcelUtils.readIntFromParcel(in).intValue();
/*  55 */     this.msg = ParcelUtils.readFromParcel(in);
/*  56 */     this.sid = ParcelUtils.readFromParcel(in);
/*     */   }
/*     */ 
/*     */   public String getsid()
/*     */   {
/*  64 */     return this.sid;
/*     */   }
/*     */ 
/*     */   public int getCode()
/*     */   {
/*  72 */     return this.code;
/*     */   }
/*     */ 
/*     */   public String getMsg()
/*     */   {
/*  80 */     return this.msg;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/*  90 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 101 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.code));
/* 102 */     ParcelUtils.writeToParcel(dest, this.msg);
/* 103 */     ParcelUtils.writeToParcel(dest, this.sid);
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/* 113 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.CSTerminateMessage
 * JD-Core Version:    0.6.0
 */
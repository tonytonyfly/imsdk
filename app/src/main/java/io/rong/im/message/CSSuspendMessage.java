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
/*     */ @MessageTag(value="RC:CsSp", flag=0)
/*     */ public class CSSuspendMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "CSSuspendMessage";
/*     */   private String uid;
/*     */   private String sid;
/*     */   private String pid;
/* 107 */   public static final Parcelable.Creator<CSSuspendMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CSSuspendMessage createFromParcel(Parcel source)
/*     */     {
/* 111 */       return new CSSuspendMessage(source);
/*     */     }
/*     */ 
/*     */     public CSSuspendMessage[] newArray(int size)
/*     */     {
/* 116 */       return new CSSuspendMessage[size];
/*     */     }
/* 107 */   };
/*     */ 
/*     */   public CSSuspendMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CSSuspendMessage(byte[] data)
/*     */   {
/*     */   }
/*     */ 
/*     */   public static CSSuspendMessage obtain(String sid, String uid, String pid)
/*     */   {
/*  47 */     CSSuspendMessage message = new CSSuspendMessage();
/*  48 */     message.sid = sid;
/*  49 */     message.uid = uid;
/*  50 */     message.pid = pid;
/*  51 */     return message;
/*     */   }
/*     */ 
/*     */   public CSSuspendMessage(Parcel in) {
/*  55 */     this.sid = ParcelUtils.readFromParcel(in);
/*  56 */     this.uid = ParcelUtils.readFromParcel(in);
/*  57 */     this.pid = ParcelUtils.readFromParcel(in);
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/*  67 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/*  78 */     ParcelUtils.writeToParcel(dest, this.sid);
/*  79 */     ParcelUtils.writeToParcel(dest, this.uid);
/*  80 */     ParcelUtils.writeToParcel(dest, this.pid);
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  90 */     JSONObject jsonObj = new JSONObject();
/*     */     try {
/*  92 */       jsonObj.put("uid", this.uid);
/*  93 */       jsonObj.put("sid", this.sid);
/*  94 */       jsonObj.put("pid", this.pid);
/*     */     } catch (JSONException e) {
/*  96 */       RLog.e("CSSuspendMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 100 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 102 */       e.printStackTrace();
/*     */     }
/* 104 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.CSSuspendMessage
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:CSCha", flag=0)
/*     */ public class CSChangeModeMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "CSChangeModeMessage";
/*     */   private String uid;
/*     */   private String sid;
/*     */   private String pid;
/*     */   private String groupid;
/* 117 */   public static final Parcelable.Creator<CSChangeModeMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CSChangeModeMessage createFromParcel(Parcel source)
/*     */     {
/* 121 */       return new CSChangeModeMessage(source);
/*     */     }
/*     */ 
/*     */     public CSChangeModeMessage[] newArray(int size)
/*     */     {
/* 126 */       return new CSChangeModeMessage[size];
/*     */     }
/* 117 */   };
/*     */ 
/*     */   public CSChangeModeMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CSChangeModeMessage(byte[] data)
/*     */   {
/*     */   }
/*     */ 
/*     */   public static CSChangeModeMessage obtain(String sid, String uid, String pid, String groupid)
/*     */   {
/*  51 */     CSChangeModeMessage message = new CSChangeModeMessage();
/*  52 */     message.sid = sid;
/*  53 */     message.uid = uid;
/*  54 */     message.pid = pid;
/*  55 */     message.groupid = groupid;
/*  56 */     return message;
/*     */   }
/*     */ 
/*     */   public CSChangeModeMessage(Parcel in) {
/*  60 */     this.sid = ParcelUtils.readFromParcel(in);
/*  61 */     this.uid = ParcelUtils.readFromParcel(in);
/*  62 */     this.pid = ParcelUtils.readFromParcel(in);
/*  63 */     this.groupid = ParcelUtils.readFromParcel(in);
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/*  73 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/*  84 */     ParcelUtils.writeToParcel(dest, this.sid);
/*  85 */     ParcelUtils.writeToParcel(dest, this.uid);
/*  86 */     ParcelUtils.writeToParcel(dest, this.pid);
/*  87 */     ParcelUtils.writeToParcel(dest, this.groupid);
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  97 */     JSONObject jsonObj = new JSONObject();
/*     */     try {
/*  99 */       jsonObj.put("uid", this.uid);
/* 100 */       jsonObj.put("sid", this.sid);
/* 101 */       jsonObj.put("pid", this.pid);
/* 102 */       if (!TextUtils.isEmpty(this.groupid))
/* 103 */         jsonObj.put("groupid", this.groupid);
/*     */     }
/*     */     catch (JSONException e) {
/* 106 */       RLog.e("CSChangeModeMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 110 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 112 */       e.printStackTrace();
/*     */     }
/* 114 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.CSChangeModeMessage
 * JD-Core Version:    0.6.0
 */
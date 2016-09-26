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
/*     */ @MessageTag(value="RC:CsPullEva", flag=0)
/*     */ public class CSPullEvaluateMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "CSPullEvaluateMessage";
/*     */   private String msgId;
/*  99 */   public static final Parcelable.Creator<CSPullEvaluateMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CSPullEvaluateMessage createFromParcel(Parcel source)
/*     */     {
/* 103 */       return new CSPullEvaluateMessage(source);
/*     */     }
/*     */ 
/*     */     public CSPullEvaluateMessage[] newArray(int size)
/*     */     {
/* 108 */       return new CSPullEvaluateMessage[size];
/*     */     }
/*  99 */   };
/*     */ 
/*     */   public CSPullEvaluateMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CSPullEvaluateMessage(byte[] content)
/*     */   {
/*  30 */     String jsonStr = null;
/*     */     try {
/*  32 */       jsonStr = new String(content, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  34 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/*  38 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*  39 */       this.msgId = jsonObj.getString("msgId");
/*     */     } catch (JSONException e) {
/*  41 */       RLog.e("CSPullEvaluateMessage", "JSONException" + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static CSPullEvaluateMessage obtain()
/*     */   {
/*  51 */     return new CSPullEvaluateMessage();
/*     */   }
/*     */ 
/*     */   public CSPullEvaluateMessage(Parcel in) {
/*  55 */     this.msgId = ParcelUtils.readFromParcel(in);
/*     */   }
/*     */ 
/*     */   public String getMsgId()
/*     */   {
/*  65 */     return this.msgId;
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/*  75 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/*  86 */     ParcelUtils.writeToParcel(dest, this.msgId);
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  96 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.CSPullEvaluateMessage
 * JD-Core Version:    0.6.0
 */
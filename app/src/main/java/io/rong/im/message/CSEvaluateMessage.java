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
/*     */ @MessageTag(value="RC:CsEva", flag=0)
/*     */ public class CSEvaluateMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "CSEvaluateMessage";
/*     */   private String uid;
/*     */   private String sid;
/*     */   private String pid;
/*     */   private int source;
/*     */   private String suggest;
/*     */   private boolean isRobotResolved;
/*     */   private int type;
/* 198 */   public static final Parcelable.Creator<CSEvaluateMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CSEvaluateMessage createFromParcel(Parcel source)
/*     */     {
/* 202 */       return new CSEvaluateMessage(source);
/*     */     }
/*     */ 
/*     */     public CSEvaluateMessage[] newArray(int size)
/*     */     {
/* 207 */       return new CSEvaluateMessage[size];
/*     */     }
/* 198 */   };
/*     */ 
/*     */   private CSEvaluateMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CSEvaluateMessage(byte[] data)
/*     */   {
/*     */   }
/*     */ 
/*     */   public CSEvaluateMessage(Parcel in)
/*     */   {
/* 134 */     this.sid = ParcelUtils.readFromParcel(in);
/* 135 */     this.uid = ParcelUtils.readFromParcel(in);
/* 136 */     this.pid = ParcelUtils.readFromParcel(in);
/* 137 */     this.source = ParcelUtils.readIntFromParcel(in).intValue();
/* 138 */     this.suggest = ParcelUtils.readFromParcel(in);
/* 139 */     this.isRobotResolved = (ParcelUtils.readIntFromParcel(in).intValue() == 1);
/* 140 */     this.type = ParcelUtils.readIntFromParcel(in).intValue();
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 150 */     return 0;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 161 */     ParcelUtils.writeToParcel(dest, this.sid);
/* 162 */     ParcelUtils.writeToParcel(dest, this.uid);
/* 163 */     ParcelUtils.writeToParcel(dest, this.pid);
/* 164 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.source));
/* 165 */     ParcelUtils.writeToParcel(dest, this.suggest);
/* 166 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.isRobotResolved ? 1 : 0));
/* 167 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.type));
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/* 177 */     JSONObject jsonObj = new JSONObject();
/*     */     try {
/* 179 */       jsonObj.put("uid", this.uid);
/* 180 */       jsonObj.put("sid", this.sid);
/* 181 */       jsonObj.put("pid", this.pid);
/* 182 */       jsonObj.put("source", this.source);
/* 183 */       jsonObj.put("suggest", this.suggest);
/* 184 */       jsonObj.put("isresolve", this.isRobotResolved ? 1 : 0);
/* 185 */       jsonObj.put("type", this.type);
/*     */     } catch (JSONException e) {
/* 187 */       RLog.e("CSEvaluateMessage", "JSONException " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 191 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 193 */       e.printStackTrace();
/*     */     }
/* 195 */     return null;
/*     */   }
/*     */ 
/*     */   public static class Builder
/*     */   {
/*     */     private String uid;
/*     */     private String sid;
/*     */     private String pid;
/*     */     private int source;
/*     */     private String suggest;
/*     */     private boolean isRobotResolved;
/*     */     private int type;
/*     */ 
/*     */     public CSEvaluateMessage build()
/*     */     {
/*  53 */       CSEvaluateMessage message = new CSEvaluateMessage(null);
/*  54 */       CSEvaluateMessage.access$102(message, this.sid);
/*  55 */       CSEvaluateMessage.access$202(message, this.pid);
/*  56 */       CSEvaluateMessage.access$302(message, this.uid);
/*  57 */       CSEvaluateMessage.access$402(message, this.source);
/*  58 */       CSEvaluateMessage.access$502(message, this.suggest);
/*  59 */       CSEvaluateMessage.access$602(message, this.isRobotResolved);
/*  60 */       CSEvaluateMessage.access$702(message, this.type);
/*  61 */       return message;
/*     */     }
/*     */ 
/*     */     public Builder sid(String sid)
/*     */     {
/*  70 */       this.sid = sid;
/*  71 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder uid(String uid)
/*     */     {
/*  80 */       this.uid = uid;
/*  81 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder pid(String pid)
/*     */     {
/*  90 */       this.pid = pid;
/*  91 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder source(int source)
/*     */     {
/* 100 */       this.source = source;
/* 101 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder suggest(String suggest)
/*     */     {
/* 109 */       this.suggest = suggest;
/* 110 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder isRobotResolved(boolean isRobotResolved)
/*     */     {
/* 118 */       this.isRobotResolved = isRobotResolved;
/* 119 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder type(int type)
/*     */     {
/* 128 */       this.type = type;
/* 129 */       return this;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.CSEvaluateMessage
 * JD-Core Version:    0.6.0
 */
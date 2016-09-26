/*     */ package io.rong.calllib.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.calllib.RongCallCommon.CallDisconnectedReason;
/*     */ import io.rong.calllib.RongCallCommon.CallMediaType;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ @MessageTag(value="RC:VCSummary", flag=1)
/*     */ public class CallSummaryMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "VoIPSummaryMessage";
/*     */   private String caller;
/*     */   private String inviter;
/*     */   private RongCallCommon.CallMediaType mediaType;
/*     */   private List<String> memberIdList;
/*     */   private long startTime;
/*     */   private long activeTime;
/*     */   private long duration;
/*     */   private RongCallCommon.CallDisconnectedReason reason;
/* 199 */   public static final Parcelable.Creator<CallSummaryMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CallSummaryMessage createFromParcel(Parcel source)
/*     */     {
/* 203 */       return new CallSummaryMessage(source);
/*     */     }
/*     */ 
/*     */     public CallSummaryMessage[] newArray(int size)
/*     */     {
/* 208 */       return new CallSummaryMessage[size];
/*     */     }
/* 199 */   };
/*     */ 
/*     */   public CallSummaryMessage(Parcel in)
/*     */   {
/*  33 */     this.caller = ParcelUtils.readFromParcel(in);
/*  34 */     this.inviter = ParcelUtils.readFromParcel(in);
/*  35 */     this.mediaType = RongCallCommon.CallMediaType.valueOf(ParcelUtils.readIntFromParcel(in).intValue());
/*  36 */     this.memberIdList = ParcelUtils.readListFromParcel(in, String.class);
/*  37 */     this.startTime = ParcelUtils.readLongFromParcel(in).longValue();
/*  38 */     this.activeTime = ParcelUtils.readLongFromParcel(in).longValue();
/*  39 */     this.duration = ParcelUtils.readLongFromParcel(in).longValue();
/*  40 */     this.reason = RongCallCommon.CallDisconnectedReason.valueOf(ParcelUtils.readIntFromParcel(in).intValue());
/*     */   }
/*     */ 
/*     */   public CallSummaryMessage() {
/*     */   }
/*     */ 
/*     */   public CallSummaryMessage(byte[] data) {
/*  47 */     String jsonStr = null;
/*     */     try
/*     */     {
/*  50 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  52 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/*  56 */       JSONObject jsonObj = new JSONObject(jsonStr);
/*  57 */       this.caller = jsonObj.optString("caller");
/*  58 */       this.inviter = jsonObj.optString("inviter");
/*  59 */       this.mediaType = RongCallCommon.CallMediaType.valueOf(jsonObj.optInt("mediaType"));
/*  60 */       this.memberIdList = new ArrayList();
/*  61 */       JSONArray jsonArray = jsonObj.optJSONArray("memberIdList");
/*  62 */       if (jsonArray != null) {
/*  63 */         for (int i = 0; i < jsonArray.length(); i++) {
/*  64 */           this.memberIdList.add((String)jsonArray.get(i));
/*     */         }
/*     */       }
/*  67 */       this.startTime = jsonObj.optLong("startTime");
/*  68 */       this.activeTime = jsonObj.optLong("activeTime");
/*  69 */       this.duration = jsonObj.optLong("duration");
/*  70 */       this.reason = RongCallCommon.CallDisconnectedReason.valueOf(jsonObj.optInt("hangupReason"));
/*     */     } catch (JSONException e) {
/*  72 */       RLog.e("VoIPSummaryMessage", "JSONException, " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getInviter() {
/*  77 */     return this.inviter;
/*     */   }
/*     */ 
/*     */   public void setInviter(String inviter) {
/*  81 */     this.inviter = inviter;
/*     */   }
/*     */ 
/*     */   public long getActiveTime() {
/*  85 */     return this.activeTime;
/*     */   }
/*     */ 
/*     */   public void setActiveTime(long activeTime) {
/*  89 */     this.activeTime = activeTime;
/*     */   }
/*     */ 
/*     */   public String getCaller() {
/*  93 */     return this.caller;
/*     */   }
/*     */ 
/*     */   public void setCaller(String caller) {
/*  97 */     this.caller = caller;
/*     */   }
/*     */ 
/*     */   public RongCallCommon.CallMediaType getMediaType() {
/* 101 */     return this.mediaType;
/*     */   }
/*     */ 
/*     */   public void setMediaType(RongCallCommon.CallMediaType mediaType) {
/* 105 */     this.mediaType = mediaType;
/*     */   }
/*     */ 
/*     */   public List<String> getMemberIdList() {
/* 109 */     return this.memberIdList;
/*     */   }
/*     */ 
/*     */   public void setMemberIdList(List<String> memberIdList) {
/* 113 */     this.memberIdList = memberIdList;
/*     */   }
/*     */ 
/*     */   public long getStartTime() {
/* 117 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   public void setStartTime(long startTime) {
/* 121 */     this.startTime = startTime;
/*     */   }
/*     */ 
/*     */   public long getDuration() {
/* 125 */     return this.duration;
/*     */   }
/*     */ 
/*     */   public void setDuration(long duration) {
/* 129 */     this.duration = duration;
/*     */   }
/*     */ 
/*     */   public RongCallCommon.CallDisconnectedReason getReason() {
/* 133 */     return this.reason;
/*     */   }
/*     */ 
/*     */   public void setReason(RongCallCommon.CallDisconnectedReason reason) {
/* 137 */     this.reason = reason;
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/* 142 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/* 145 */       jsonObj.putOpt("caller", this.caller);
/* 146 */       jsonObj.putOpt("inviter", this.inviter);
/* 147 */       jsonObj.putOpt("mediaType", Integer.valueOf(this.mediaType.getValue()));
/* 148 */       if (this.memberIdList != null) {
/* 149 */         JSONArray jsonArray = new JSONArray();
/* 150 */         for (String userId : this.memberIdList) {
/* 151 */           jsonArray.put(userId);
/*     */         }
/* 153 */         jsonObj.putOpt("memberIdList", jsonArray);
/*     */       }
/* 155 */       jsonObj.putOpt("startTime", Long.valueOf(this.startTime));
/* 156 */       jsonObj.putOpt("activeTime", Long.valueOf(this.activeTime));
/* 157 */       jsonObj.putOpt("duration", Long.valueOf(this.duration));
/* 158 */       jsonObj.putOpt("hangupReason", Integer.valueOf(this.reason.getValue()));
/*     */     } catch (JSONException e) {
/* 160 */       RLog.e("VoIPSummaryMessage", "JSONException, " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 164 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 166 */       e.printStackTrace();
/*     */     }
/* 168 */     return null;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 179 */     ParcelUtils.writeToParcel(dest, this.caller);
/* 180 */     ParcelUtils.writeToParcel(dest, this.inviter);
/* 181 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.mediaType.getValue()));
/* 182 */     ParcelUtils.writeListToParcel(dest, this.memberIdList);
/* 183 */     ParcelUtils.writeToParcel(dest, Long.valueOf(this.startTime));
/* 184 */     ParcelUtils.writeToParcel(dest, Long.valueOf(this.activeTime));
/* 185 */     ParcelUtils.writeToParcel(dest, Long.valueOf(this.duration));
/* 186 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.reason.getValue()));
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 196 */     return 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.message.CallSummaryMessage
 * JD-Core Version:    0.6.0
 */
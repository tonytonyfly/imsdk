/*     */ package io.rong.calllib.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.calllib.RongCallCommon.CallEngineType;
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
/*     */ @MessageTag(value="RC:VCInvite", flag=0)
/*     */ public class CallInviteMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "VoIPInviteMessage";
/*     */   private String callId;
/*     */   private RongCallCommon.CallEngineType engineType;
/*     */   private String extra;
/*     */   private List<String> inviteUserIds;
/*     */   private RongCallCommon.CallMediaType mediaType;
/* 163 */   public static final Parcelable.Creator<CallInviteMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CallInviteMessage createFromParcel(Parcel source)
/*     */     {
/* 167 */       return new CallInviteMessage(source);
/*     */     }
/*     */ 
/*     */     public CallInviteMessage[] newArray(int size)
/*     */     {
/* 172 */       return new CallInviteMessage[size];
/*     */     }
/* 163 */   };
/*     */ 
/*     */   public CallInviteMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public String getCallId()
/*     */   {
/*  37 */     return this.callId;
/*     */   }
/*     */ 
/*     */   public void setCallId(String callId) {
/*  41 */     this.callId = callId;
/*     */   }
/*     */ 
/*     */   public RongCallCommon.CallEngineType getEngineType() {
/*  45 */     return this.engineType;
/*     */   }
/*     */ 
/*     */   public void setEngineType(RongCallCommon.CallEngineType engineType) {
/*  49 */     this.engineType = engineType;
/*     */   }
/*     */ 
/*     */   public String getExtra() {
/*  53 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra) {
/*  57 */     this.extra = extra;
/*     */   }
/*     */ 
/*     */   public List<String> getInviteUserIds() {
/*  61 */     return this.inviteUserIds;
/*     */   }
/*     */ 
/*     */   public void setInviteUserIds(List<String> inviteUserIds) {
/*  65 */     this.inviteUserIds = inviteUserIds;
/*     */   }
/*     */ 
/*     */   public RongCallCommon.CallMediaType getMediaType() {
/*  69 */     return this.mediaType;
/*     */   }
/*     */ 
/*     */   public void setMediaType(RongCallCommon.CallMediaType mediaType) {
/*  73 */     this.mediaType = mediaType;
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/*  78 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/*  81 */       jsonObj.putOpt("callId", this.callId);
/*  82 */       jsonObj.putOpt("channelId", this.callId);
/*  83 */       jsonObj.putOpt("extra", this.extra);
/*  84 */       JSONArray jsonArray = new JSONArray();
/*  85 */       for (String userId : this.inviteUserIds) {
/*  86 */         jsonArray.put(userId);
/*     */       }
/*  88 */       jsonObj.putOpt("inviteUserIds", jsonArray);
/*  89 */       jsonObj.putOpt("engineType", Integer.valueOf(this.engineType.getValue()));
/*  90 */       jsonObj.putOpt("mediaType", Integer.valueOf(this.mediaType.getValue()));
/*     */     } catch (JSONException e) {
/*  92 */       RLog.e("VoIPInviteMessage", "JSONException, " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/*  96 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/*  98 */       e.printStackTrace();
/*     */     }
/* 100 */     return null;
/*     */   }
/*     */ 
/*     */   public CallInviteMessage(byte[] data)
/*     */   {
/* 105 */     String jsonStr = null;
/*     */     try
/*     */     {
/* 108 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 110 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 114 */       JSONObject jsonObj = new JSONObject(jsonStr);
/* 115 */       this.callId = jsonObj.optString("callId");
/* 116 */       this.extra = jsonObj.optString("extra");
/* 117 */       this.inviteUserIds = new ArrayList();
/* 118 */       JSONArray jsonArray = jsonObj.optJSONArray("inviteUserIds");
/* 119 */       for (int i = 0; i < jsonArray.length(); i++) {
/* 120 */         this.inviteUserIds.add((String)jsonArray.get(i));
/*     */       }
/* 122 */       this.engineType = RongCallCommon.CallEngineType.valueOf(jsonObj.optInt("engineType"));
/* 123 */       this.mediaType = RongCallCommon.CallMediaType.valueOf(jsonObj.optInt("mediaType"));
/*     */     } catch (JSONException e) {
/* 125 */       RLog.e("VoIPInviteMessage", "JSONException, " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 137 */     ParcelUtils.writeToParcel(dest, this.callId);
/* 138 */     ParcelUtils.writeToParcel(dest, this.extra);
/* 139 */     ParcelUtils.writeListToParcel(dest, this.inviteUserIds);
/* 140 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.engineType.getValue()));
/* 141 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.mediaType.getValue()));
/*     */   }
/*     */ 
/*     */   public CallInviteMessage(Parcel in)
/*     */   {
/* 146 */     this.callId = ParcelUtils.readFromParcel(in);
/* 147 */     this.extra = ParcelUtils.readFromParcel(in);
/* 148 */     this.inviteUserIds = ParcelUtils.readListFromParcel(in, String.class);
/* 149 */     this.engineType = RongCallCommon.CallEngineType.valueOf(ParcelUtils.readIntFromParcel(in).intValue());
/* 150 */     this.mediaType = RongCallCommon.CallMediaType.valueOf(ParcelUtils.readIntFromParcel(in).intValue());
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 160 */     return 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.message.CallInviteMessage
 * JD-Core Version:    0.6.0
 */
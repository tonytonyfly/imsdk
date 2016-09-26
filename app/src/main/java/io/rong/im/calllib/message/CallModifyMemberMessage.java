/*     */ package io.rong.calllib.message;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.calllib.CallUserProfile;
/*     */ import io.rong.calllib.RongCallCommon.CallEngineType;
/*     */ import io.rong.calllib.RongCallCommon.CallMediaType;
/*     */ import io.rong.calllib.RongCallCommon.CallModifyMemType;
/*     */ import io.rong.calllib.RongCallCommon.CallStatus;
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
/*     */ @MessageTag(value="RC:VCModifyMem", flag=0)
/*     */ public class CallModifyMemberMessage extends MessageContent
/*     */ {
/*     */   private static final String TAG = "VoIPModifyMemberMessage";
/*     */   private String callId;
/*     */   private String caller;
/*     */   private String inviter;
/*     */   private RongCallCommon.CallEngineType engineType;
/*     */   private RongCallCommon.CallMediaType mediaType;
/*     */   private RongCallCommon.CallModifyMemType modifyMemType;
/*     */   private List<String> inviteUserIds;
/*     */   private List<CallUserProfile> participantList;
/*     */   private String extra;
/* 240 */   public static final Parcelable.Creator<CallModifyMemberMessage> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public CallModifyMemberMessage createFromParcel(Parcel source)
/*     */     {
/* 244 */       return new CallModifyMemberMessage(source);
/*     */     }
/*     */ 
/*     */     public CallModifyMemberMessage[] newArray(int size)
/*     */     {
/* 249 */       return new CallModifyMemberMessage[size];
/*     */     }
/* 240 */   };
/*     */ 
/*     */   public CallModifyMemberMessage()
/*     */   {
/*     */   }
/*     */ 
/*     */   public RongCallCommon.CallModifyMemType getModifyMemType()
/*     */   {
/*  42 */     return this.modifyMemType;
/*     */   }
/*     */ 
/*     */   public void setModifyMemType(RongCallCommon.CallModifyMemType modifyMemType) {
/*  46 */     this.modifyMemType = modifyMemType;
/*     */   }
/*     */ 
/*     */   public String getExtra() {
/*  50 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra) {
/*  54 */     this.extra = extra;
/*     */   }
/*     */ 
/*     */   public RongCallCommon.CallEngineType getEngineType() {
/*  58 */     return this.engineType;
/*     */   }
/*     */ 
/*     */   public void setEngineType(RongCallCommon.CallEngineType engineType) {
/*  62 */     this.engineType = engineType;
/*     */   }
/*     */ 
/*     */   public RongCallCommon.CallMediaType getMediaType() {
/*  66 */     return this.mediaType;
/*     */   }
/*     */ 
/*     */   public void setMediaType(RongCallCommon.CallMediaType mediaType) {
/*  70 */     this.mediaType = mediaType;
/*     */   }
/*     */ 
/*     */   public List<String> getInvitedList() {
/*  74 */     return this.inviteUserIds;
/*     */   }
/*     */ 
/*     */   public void setInvitedList(List<String> invitedList) {
/*  78 */     this.inviteUserIds = invitedList;
/*     */   }
/*     */ 
/*     */   public String getCallId() {
/*  82 */     return this.callId;
/*     */   }
/*     */ 
/*     */   public void setCallId(String callId) {
/*  86 */     this.callId = callId;
/*     */   }
/*     */ 
/*     */   public String getCaller() {
/*  90 */     return this.caller;
/*     */   }
/*     */ 
/*     */   public void setCaller(String caller) {
/*  94 */     this.caller = caller;
/*     */   }
/*     */ 
/*     */   public String getInviter() {
/*  98 */     return this.inviter;
/*     */   }
/*     */ 
/*     */   public void setInviter(String inviter) {
/* 102 */     this.inviter = inviter;
/*     */   }
/*     */ 
/*     */   public List<CallUserProfile> getParticipantList() {
/* 106 */     return this.participantList;
/*     */   }
/*     */ 
/*     */   public void setParticipantList(List<CallUserProfile> participantList) {
/* 110 */     this.participantList = participantList;
/*     */   }
/*     */ 
/*     */   public byte[] encode()
/*     */   {
/* 115 */     JSONObject jsonObj = new JSONObject();
/*     */     try
/*     */     {
/* 118 */       jsonObj.putOpt("callId", this.callId);
/* 119 */       jsonObj.putOpt("channelId", this.callId);
/* 120 */       jsonObj.putOpt("caller", this.caller);
/* 121 */       jsonObj.putOpt("inviter", this.inviter);
/* 122 */       JSONArray jsonArray = new JSONArray();
/* 123 */       for (String userId : this.inviteUserIds) {
/* 124 */         jsonArray.put(userId);
/*     */       }
/* 126 */       jsonObj.put("inviteUserIds", jsonArray);
/* 127 */       jsonArray = new JSONArray();
/* 128 */       for (CallUserProfile profile : this.participantList) {
/* 129 */         JSONObject user = new JSONObject();
/* 130 */         user.put("userId", profile.getUserId());
/* 131 */         user.put("mediaId", profile.getMediaId());
/* 132 */         user.put("mediaType", profile.getMediaType().getValue());
/* 133 */         user.put("callStatus", profile.getCallStatus().getValue());
/* 134 */         jsonArray.put(user);
/*     */       }
/*     */ 
/* 137 */       jsonObj.putOpt("existedUserPofiles", jsonArray);
/* 138 */       jsonObj.putOpt("engineType", Integer.valueOf(this.engineType.getValue()));
/* 139 */       jsonObj.putOpt("mediaType", Integer.valueOf(this.mediaType.getValue()));
/* 140 */       jsonObj.putOpt("modifyMemType", Integer.valueOf(this.modifyMemType.getValue()));
/* 141 */       jsonObj.putOpt("extra", this.extra);
/*     */     }
/*     */     catch (JSONException e) {
/* 144 */       RLog.e("VoIPModifyMemberMessage", "JSONException, " + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/* 148 */       return jsonObj.toString().getBytes("UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 150 */       e.printStackTrace();
/*     */     }
/* 152 */     return null;
/*     */   }
/*     */ 
/*     */   public CallModifyMemberMessage(byte[] data)
/*     */   {
/* 157 */     String jsonStr = null;
/*     */     try
/*     */     {
/* 160 */       jsonStr = new String(data, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 162 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/* 166 */       JSONObject jsonObj = new JSONObject(jsonStr);
/* 167 */       this.callId = jsonObj.optString("callId");
/* 168 */       this.caller = jsonObj.optString("caller");
/* 169 */       this.inviter = jsonObj.optString("inviter");
/* 170 */       JSONArray jsonArray = jsonObj.optJSONArray("inviteUserIds");
/* 171 */       this.inviteUserIds = new ArrayList();
/* 172 */       for (int i = 0; i < jsonArray.length(); i++) {
/* 173 */         this.inviteUserIds.add((String)jsonArray.get(i));
/*     */       }
/* 175 */       this.participantList = new ArrayList();
/* 176 */       jsonArray = jsonObj.optJSONArray("existedUserPofiles");
/* 177 */       for (int i = 0; i < jsonArray.length(); i++) {
/* 178 */         JSONObject jsonObject = jsonArray.getJSONObject(i);
/* 179 */         String userId = jsonObject.getString("userId");
/* 180 */         CallUserProfile participantProfile = new CallUserProfile();
/* 181 */         participantProfile.setUserId(userId);
/* 182 */         if (jsonObject.has("mediaId"))
/* 183 */           participantProfile.setMediaId(jsonObject.getString("mediaId"));
/* 184 */         participantProfile.setCallStatus(RongCallCommon.CallStatus.valueOf(jsonObject.getInt("callStatus")));
/* 185 */         participantProfile.setMediaType(RongCallCommon.CallMediaType.valueOf(jsonObject.getInt("mediaType")));
/* 186 */         this.participantList.add(participantProfile);
/*     */       }
/* 188 */       this.engineType = RongCallCommon.CallEngineType.valueOf(jsonObj.optInt("engineType"));
/* 189 */       this.mediaType = RongCallCommon.CallMediaType.valueOf(jsonObj.optInt("mediaType"));
/* 190 */       this.modifyMemType = RongCallCommon.CallModifyMemType.valueOf(jsonObj.optInt("modifyMemType"));
/* 191 */       this.extra = jsonObj.optString("extra");
/*     */     }
/*     */     catch (JSONException e) {
/* 194 */       RLog.e("VoIPModifyMemberMessage", "JSONException, " + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 206 */     ParcelUtils.writeToParcel(dest, this.callId);
/* 207 */     ParcelUtils.writeToParcel(dest, this.caller);
/* 208 */     ParcelUtils.writeToParcel(dest, this.inviter);
/* 209 */     ParcelUtils.writeListToParcel(dest, this.inviteUserIds);
/* 210 */     ParcelUtils.writeListToParcel(dest, this.participantList);
/* 211 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.engineType.getValue()));
/* 212 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.mediaType.getValue()));
/* 213 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.modifyMemType.getValue()));
/* 214 */     ParcelUtils.writeToParcel(dest, this.extra);
/*     */   }
/*     */ 
/*     */   public CallModifyMemberMessage(Parcel in)
/*     */   {
/* 219 */     this.callId = ParcelUtils.readFromParcel(in);
/* 220 */     this.caller = ParcelUtils.readFromParcel(in);
/* 221 */     this.inviter = ParcelUtils.readFromParcel(in);
/* 222 */     this.inviteUserIds = ParcelUtils.readListFromParcel(in, String.class);
/* 223 */     this.participantList = ParcelUtils.readListFromParcel(in, CallUserProfile.class);
/* 224 */     this.engineType = RongCallCommon.CallEngineType.valueOf(ParcelUtils.readIntFromParcel(in).intValue());
/* 225 */     this.mediaType = RongCallCommon.CallMediaType.valueOf(ParcelUtils.readIntFromParcel(in).intValue());
/* 226 */     this.modifyMemType = RongCallCommon.CallModifyMemType.valueOf(ParcelUtils.readIntFromParcel(in).intValue());
/* 227 */     this.extra = ParcelUtils.readFromParcel(in);
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 237 */     return 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.message.CallModifyMemberMessage
 * JD-Core Version:    0.6.0
 */
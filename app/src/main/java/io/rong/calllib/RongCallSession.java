/*     */ package io.rong.calllib;
/*     */ 
/*     */ import android.os.Parcel;
/*     */ import android.os.Parcelable;
/*     */ import android.os.Parcelable.Creator;
/*     */ import io.rong.common.ParcelUtils;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import java.util.List;
/*     */ 
/*     */ public class RongCallSession
/*     */   implements Parcelable
/*     */ {
/*     */   private String callId;
/*     */   private Conversation.ConversationType conversationType;
/*     */   private String targetId;
/*     */   private RongCallCommon.CallMediaType mediaType;
/*     */   private RongCallCommon.CallEngineType engineType;
/*     */   private long startTime;
/*     */   private long activeTime;
/*     */   private long endTime;
/*     */   private String selfUserId;
/*     */   private String inviterUserId;
/*     */   private String callerUserId;
/*     */   private List<CallUserProfile> usersProfileList;
/*     */   private String extra;
/* 177 */   public static final Parcelable.Creator<RongCallSession> CREATOR = new Parcelable.Creator()
/*     */   {
/*     */     public RongCallSession createFromParcel(Parcel source)
/*     */     {
/* 181 */       return new RongCallSession(source);
/*     */     }
/*     */ 
/*     */     public RongCallSession[] newArray(int size)
/*     */     {
/* 186 */       return new RongCallSession[size];
/*     */     }
/* 177 */   };
/*     */ 
/*     */   public RongCallSession()
/*     */   {
/*     */   }
/*     */ 
/*     */   public String getExtra()
/*     */   {
/*  30 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra) {
/*  34 */     this.extra = extra;
/*     */   }
/*     */ 
/*     */   public String getSelfUserId() {
/*  38 */     return this.selfUserId;
/*     */   }
/*     */ 
/*     */   public void setSelfUserId(String selfUserId) {
/*  42 */     this.selfUserId = selfUserId;
/*     */   }
/*     */ 
/*     */   public long getActiveTime() {
/*  46 */     return this.activeTime;
/*     */   }
/*     */ 
/*     */   public void setActiveTime(long activeTime) {
/*  50 */     this.activeTime = activeTime;
/*     */   }
/*     */ 
/*     */   public String getInviterUserId() {
/*  54 */     return this.inviterUserId;
/*     */   }
/*     */ 
/*     */   public void setInviterUserId(String inviterUserId) {
/*  58 */     this.inviterUserId = inviterUserId;
/*     */   }
/*     */ 
/*     */   public String getCallerUserId() {
/*  62 */     return this.callerUserId;
/*     */   }
/*     */ 
/*     */   public void setCallerUserId(String callerUserId) {
/*  66 */     this.callerUserId = callerUserId;
/*     */   }
/*     */ 
/*     */   public List<CallUserProfile> getParticipantProfileList() {
/*  70 */     return this.usersProfileList;
/*     */   }
/*     */ 
/*     */   public void setParticipantUserList(List<CallUserProfile> participantsProfileList) {
/*  74 */     this.usersProfileList = participantsProfileList;
/*     */   }
/*     */ 
/*     */   public RongCallCommon.CallEngineType getEngineType() {
/*  78 */     return this.engineType;
/*     */   }
/*     */ 
/*     */   public void setEngineType(RongCallCommon.CallEngineType engineType) {
/*  82 */     this.engineType = engineType;
/*     */   }
/*     */ 
/*     */   public String getCallId() {
/*  86 */     return this.callId;
/*     */   }
/*     */ 
/*     */   public void setCallId(String callId) {
/*  90 */     this.callId = callId;
/*     */   }
/*     */ 
/*     */   public Conversation.ConversationType getConversationType() {
/*  94 */     return this.conversationType;
/*     */   }
/*     */ 
/*     */   public void setConversationType(Conversation.ConversationType conversationType) {
/*  98 */     this.conversationType = conversationType;
/*     */   }
/*     */ 
/*     */   public String getTargetId() {
/* 102 */     return this.targetId;
/*     */   }
/*     */ 
/*     */   public void setTargetId(String targetId) {
/* 106 */     this.targetId = targetId;
/*     */   }
/*     */ 
/*     */   public RongCallCommon.CallMediaType getMediaType() {
/* 110 */     return this.mediaType;
/*     */   }
/*     */ 
/*     */   public void setMediaType(RongCallCommon.CallMediaType mediaType) {
/* 114 */     this.mediaType = mediaType;
/*     */   }
/*     */ 
/*     */   public long getStartTime() {
/* 118 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   public void setStartTime(long startTime) {
/* 122 */     this.startTime = startTime;
/*     */   }
/*     */ 
/*     */   public long getEndTime() {
/* 126 */     return this.endTime;
/*     */   }
/*     */ 
/*     */   public void setEndTime(long endTime) {
/* 130 */     this.endTime = endTime;
/*     */   }
/*     */ 
/*     */   public void writeToParcel(Parcel dest, int flags)
/*     */   {
/* 135 */     ParcelUtils.writeToParcel(dest, this.callId);
/* 136 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.conversationType.getValue()));
/* 137 */     ParcelUtils.writeToParcel(dest, this.targetId);
/* 138 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.mediaType.getValue()));
/* 139 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.engineType.getValue()));
/* 140 */     ParcelUtils.writeToParcel(dest, Long.valueOf(this.startTime));
/* 141 */     ParcelUtils.writeToParcel(dest, Long.valueOf(this.activeTime));
/* 142 */     ParcelUtils.writeToParcel(dest, Long.valueOf(this.endTime));
/* 143 */     ParcelUtils.writeToParcel(dest, this.selfUserId);
/* 144 */     ParcelUtils.writeToParcel(dest, this.inviterUserId);
/* 145 */     ParcelUtils.writeToParcel(dest, this.callerUserId);
/* 146 */     ParcelUtils.writeListToParcel(dest, this.usersProfileList);
/* 147 */     ParcelUtils.writeToParcel(dest, this.extra);
/*     */   }
/*     */ 
/*     */   public RongCallSession(Parcel in)
/*     */   {
/* 152 */     this.callId = ParcelUtils.readFromParcel(in);
/* 153 */     this.conversationType = Conversation.ConversationType.setValue(ParcelUtils.readIntFromParcel(in).intValue());
/* 154 */     this.targetId = ParcelUtils.readFromParcel(in);
/* 155 */     this.mediaType = RongCallCommon.CallMediaType.valueOf(ParcelUtils.readIntFromParcel(in).intValue());
/* 156 */     this.engineType = RongCallCommon.CallEngineType.valueOf(ParcelUtils.readIntFromParcel(in).intValue());
/* 157 */     this.startTime = ParcelUtils.readLongFromParcel(in).longValue();
/* 158 */     this.activeTime = ParcelUtils.readLongFromParcel(in).longValue();
/* 159 */     this.endTime = ParcelUtils.readLongFromParcel(in).longValue();
/* 160 */     this.selfUserId = ParcelUtils.readFromParcel(in);
/* 161 */     this.inviterUserId = ParcelUtils.readFromParcel(in);
/* 162 */     this.callerUserId = ParcelUtils.readFromParcel(in);
/* 163 */     this.usersProfileList = ParcelUtils.readListFromParcel(in, CallUserProfile.class);
/* 164 */     this.extra = ParcelUtils.readFromParcel(in);
/*     */   }
/*     */ 
/*     */   public int describeContents()
/*     */   {
/* 174 */     return 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.RongCallSession
 * JD-Core Version:    0.6.0
 */
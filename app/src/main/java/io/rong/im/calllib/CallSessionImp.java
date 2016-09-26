/*     */ package io.rong.calllib;
/*     */ 
/*     */ import android.os.Message;
/*     */ import android.view.SurfaceView;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import java.util.List;
/*     */ 
/*     */ public class CallSessionImp
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
/*     */   private Message cachedMsg;
/*     */   private String dynamicKey;
/*     */ 
/*     */   public Message getCachedMsg()
/*     */   {
/*  34 */     return this.cachedMsg;
/*     */   }
/*     */ 
/*     */   public void setCachedMsg(Message cachedMsg) {
/*  38 */     this.cachedMsg = cachedMsg;
/*     */   }
/*     */ 
/*     */   public String getExtra() {
/*  42 */     return this.extra;
/*     */   }
/*     */ 
/*     */   public void setExtra(String extra) {
/*  46 */     this.extra = extra;
/*     */   }
/*     */ 
/*     */   public String getSelfUserId() {
/*  50 */     return this.selfUserId;
/*     */   }
/*     */ 
/*     */   public void setSelfUserId(String selfUserId) {
/*  54 */     this.selfUserId = selfUserId;
/*     */   }
/*     */ 
/*     */   public long getActiveTime() {
/*  58 */     return this.activeTime;
/*     */   }
/*     */ 
/*     */   public void setActiveTime(long activeTime) {
/*  62 */     this.activeTime = activeTime;
/*     */   }
/*     */ 
/*     */   public String getInviterUserId() {
/*  66 */     return this.inviterUserId;
/*     */   }
/*     */ 
/*     */   public void setInviterUserId(String inviterUserId) {
/*  70 */     this.inviterUserId = inviterUserId;
/*     */   }
/*     */ 
/*     */   public String getCallerUserId() {
/*  74 */     return this.callerUserId;
/*     */   }
/*     */ 
/*     */   public void setCallerUserId(String callerUserId) {
/*  78 */     this.callerUserId = callerUserId;
/*     */   }
/*     */ 
/*     */   public List<CallUserProfile> getParticipantProfileList() {
/*  82 */     return this.usersProfileList;
/*     */   }
/*     */ 
/*     */   public SurfaceView getLocalVideo() {
/*  86 */     SurfaceView localVideo = null;
/*  87 */     for (CallUserProfile profile : this.usersProfileList) {
/*  88 */       if (profile.getUserId().equals(this.selfUserId)) {
/*  89 */         localVideo = profile.getVideoView();
/*     */       }
/*     */     }
/*  92 */     return localVideo;
/*     */   }
/*     */ 
/*     */   public void setLocalVideo(SurfaceView localVideo) {
/*  96 */     for (CallUserProfile profile : this.usersProfileList)
/*  97 */       if (profile.getUserId().equals(this.selfUserId))
/*  98 */         profile.setVideoView(localVideo);
/*     */   }
/*     */ 
/*     */   public void setParticipantUserList(List<CallUserProfile> participantsProfileList)
/*     */   {
/* 104 */     this.usersProfileList = participantsProfileList;
/*     */   }
/*     */ 
/*     */   public RongCallCommon.CallEngineType getEngineType() {
/* 108 */     return this.engineType;
/*     */   }
/*     */ 
/*     */   public void setEngineType(RongCallCommon.CallEngineType engineType) {
/* 112 */     this.engineType = engineType;
/*     */   }
/*     */ 
/*     */   public String getCallId() {
/* 116 */     return this.callId;
/*     */   }
/*     */ 
/*     */   public void setCallId(String callId) {
/* 120 */     this.callId = callId;
/*     */   }
/*     */ 
/*     */   public Conversation.ConversationType getConversationType() {
/* 124 */     return this.conversationType;
/*     */   }
/*     */ 
/*     */   public void setConversationType(Conversation.ConversationType conversationType) {
/* 128 */     this.conversationType = conversationType;
/*     */   }
/*     */ 
/*     */   public String getTargetId() {
/* 132 */     return this.targetId;
/*     */   }
/*     */ 
/*     */   public void setTargetId(String targetId) {
/* 136 */     this.targetId = targetId;
/*     */   }
/*     */ 
/*     */   public RongCallCommon.CallMediaType getMediaType() {
/* 140 */     return this.mediaType;
/*     */   }
/*     */ 
/*     */   public void setMediaType(RongCallCommon.CallMediaType mediaType) {
/* 144 */     this.mediaType = mediaType;
/*     */   }
/*     */ 
/*     */   public long getStartTime() {
/* 148 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   public void setStartTime(long startTime) {
/* 152 */     this.startTime = startTime;
/*     */   }
/*     */ 
/*     */   public long getEndTime() {
/* 156 */     return this.endTime;
/*     */   }
/*     */ 
/*     */   public void setEndTime(long endTime) {
/* 160 */     this.endTime = endTime;
/*     */   }
/*     */ 
/*     */   public String getDynamicKey() {
/* 164 */     return this.dynamicKey;
/*     */   }
/*     */ 
/*     */   public void setDynamicKey(String dynamicKey) {
/* 168 */     this.dynamicKey = dynamicKey;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.CallSessionImp
 * JD-Core Version:    0.6.0
 */
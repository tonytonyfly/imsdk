/*     */ package io.rong.imlib.location;
/*     */ 
/*     */ public class RealTimeLocationConstant
/*     */ {
/*     */   public static enum RealTimeLocationErrorCode
/*     */   {
/*  64 */     RC_REAL_TIME_LOCATION_NOT_INIT(-1, "Not init"), 
/*     */ 
/*  69 */     RC_REAL_TIME_LOCATION_SUCCESS(0, "Success"), 
/*     */ 
/*  75 */     RC_REAL_TIME_LOCATION_GPS_DISABLED(1, "GPS disabled"), 
/*     */ 
/*  81 */     RC_REAL_TIME_LOCATION_CONVERSATION_NOT_SUPPORT(2, "Conversation not support"), 
/*     */ 
/*  87 */     RC_REAL_TIME_LOCATION_IS_ON_GOING(3, "Real-Time location is on going"), 
/*     */ 
/*  93 */     RC_REAL_TIME_LOCATION_EXCEED_MAX_PARTICIPANT(4, "Exceed max participants"), 
/*     */ 
/*  99 */     RC_REAL_TIME_LOCATION_JOIN_FAILURE(5, "Join fail"), 
/*     */ 
/* 105 */     RC_REAL_TIME_LOCATION_START_FAILURE(6, "Start fail"), 
/*     */ 
/* 110 */     RC_REAL_TIME_LOCATION_NETWORK_UNAVAILABLE(7, "Network unavailable.");
/*     */ 
/*     */     int code;
/*     */     String msg;
/*     */ 
/* 116 */     private RealTimeLocationErrorCode(int code, String msg) { this.code = code;
/* 117 */       this.msg = msg; }
/*     */ 
/*     */     public String getMessage()
/*     */     {
/* 121 */       return this.msg;
/*     */     }
/*     */ 
/*     */     public int getValue() {
/* 125 */       return this.code;
/*     */     }
/*     */ 
/*     */     public static RealTimeLocationErrorCode valueOf(int value) {
/* 129 */       for (RealTimeLocationErrorCode code : values()) {
/* 130 */         if (code.getValue() == value)
/* 131 */           return code;
/*     */       }
/* 133 */       return RC_REAL_TIME_LOCATION_CONVERSATION_NOT_SUPPORT;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum RealTimeLocationStatus
/*     */   {
/*  13 */     RC_REAL_TIME_LOCATION_STATUS_IDLE(0, "Idle state"), 
/*     */ 
/*  20 */     RC_REAL_TIME_LOCATION_STATUS_INCOMING(1, "Incoming state"), 
/*     */ 
/*  27 */     RC_REAL_TIME_LOCATION_STATUS_OUTGOING(2, "Outgoing state"), 
/*     */ 
/*  33 */     RC_REAL_TIME_LOCATION_STATUS_CONNECTED(3, "Connected state");
/*     */ 
/*     */     int code;
/*     */     String msg;
/*     */ 
/*  39 */     private RealTimeLocationStatus(int code, String msg) { this.code = code;
/*  40 */       this.msg = msg; }
/*     */ 
/*     */     public String getMessage()
/*     */     {
/*  44 */       return this.msg;
/*     */     }
/*     */ 
/*     */     public int getValue() {
/*  48 */       return this.code;
/*     */     }
/*     */ 
/*     */     public static RealTimeLocationStatus valueOf(int value) {
/*  52 */       for (RealTimeLocationStatus code : values()) {
/*  53 */         if (code.getValue() == value)
/*  54 */           return code;
/*     */       }
/*  56 */       return RC_REAL_TIME_LOCATION_STATUS_IDLE;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.location.RealTimeLocationConstant
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.calllib;
/*     */ 
/*     */ public class RongCallCommon
/*     */ {
/*     */   public static enum ServerRecordingErrorCode
/*     */   {
/* 257 */     SUCCESS(0), 
/*     */ 
/* 262 */     FAIL(1), 
/*     */ 
/* 267 */     INVALID_ARGUMENT(2), 
/*     */ 
/* 272 */     NOT_READY(3), 
/*     */ 
/* 277 */     NOT_IN_CALL(4), 
/*     */ 
/* 282 */     NOT_INITIALIZED(7), 
/*     */ 
/* 287 */     TIME_OUT(10);
/*     */ 
/*     */     private int value;
/*     */ 
/* 292 */     private ServerRecordingErrorCode(int value) { this.value = value; }
/*     */ 
/*     */     public int getValue()
/*     */     {
/* 296 */       return this.value;
/*     */     }
/*     */ 
/*     */     public static ServerRecordingErrorCode valueOf(int value) {
/* 300 */       for (ServerRecordingErrorCode v : values()) {
/* 301 */         if (v.value == value)
/* 302 */           return v;
/*     */       }
/* 304 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum CallVideoProfile
/*     */   {
/* 229 */     VIDEO_PROFILE_240P(20), 
/* 230 */     VIDEO_PROFILE_360P(30), 
/* 231 */     VIDEO_PROFILE_480P(40), 
/* 232 */     VIDEO_PROFILE_720P(50);
/*     */ 
/*     */     private int value;
/*     */ 
/* 237 */     private CallVideoProfile(int value) { this.value = value; }
/*     */ 
/*     */     public int getValue()
/*     */     {
/* 241 */       return this.value;
/*     */     }
/*     */ 
/*     */     public static CallVideoProfile valueOf(int value) {
/* 245 */       for (CallVideoProfile v : values()) {
/* 246 */         if (v.value == value)
/* 247 */           return v;
/*     */       }
/* 249 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum CallPermission
/*     */   {
/* 223 */     PERMISSION_AUDIO, 
/* 224 */     PERMISSION_CAMERA, 
/* 225 */     PERMISSION_AUDIO_AND_CAMERA;
/*     */   }
/*     */ 
/*     */   public static enum CallStatus
/*     */   {
/* 197 */     OUTGOING(1), 
/* 198 */     INCOMING(2), 
/* 199 */     RINGING(3), 
/* 200 */     CONNECTED(4), 
/* 201 */     IDLE(5);
/*     */ 
/*     */     private int value;
/*     */ 
/* 206 */     private CallStatus(int value) { this.value = value; }
/*     */ 
/*     */     public int getValue()
/*     */     {
/* 210 */       return this.value;
/*     */     }
/*     */ 
/*     */     public static CallStatus valueOf(int value) {
/* 214 */       for (CallStatus v : values()) {
/* 215 */         if (v.value == value)
/* 216 */           return v;
/*     */       }
/* 218 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum CallEngineType
/*     */   {
/* 172 */     ENGINE_TYPE_NONE(0), 
/* 173 */     ENGINE_TYPE_AGORA(1), 
/* 174 */     ENGINE_TYPE_UMCS(2);
/*     */ 
/*     */     private int value;
/*     */ 
/* 179 */     private CallEngineType(int value) { this.value = value; }
/*     */ 
/*     */     public int getValue()
/*     */     {
/* 183 */       return this.value;
/*     */     }
/*     */ 
/*     */     public static CallEngineType valueOf(int value) {
/* 187 */       for (CallEngineType v : values()) {
/* 188 */         if (v.value == value) {
/* 189 */           return v;
/*     */         }
/*     */       }
/* 192 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum CallModifyMemType
/*     */   {
/* 148 */     MODIFY_MEM_TYPE_ADD(1), 
/* 149 */     MODIFY_MEM_TYPE_REMOVE(2);
/*     */ 
/*     */     private int value;
/*     */ 
/* 154 */     private CallModifyMemType(int value) { this.value = value; }
/*     */ 
/*     */     public int getValue()
/*     */     {
/* 158 */       return this.value;
/*     */     }
/*     */ 
/*     */     public static CallModifyMemType valueOf(int value) {
/* 162 */       for (CallModifyMemType v : values()) {
/* 163 */         if (v.value == value) {
/* 164 */           return v;
/*     */         }
/*     */       }
/* 167 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum CallDisconnectedReason
/*     */   {
/*  60 */     CANCEL(1), 
/*     */ 
/*  65 */     REJECT(2), 
/*     */ 
/*  70 */     HANGUP(3), 
/*     */ 
/*  75 */     BUSY_LINE(4), 
/*     */ 
/*  80 */     NO_RESPONSE(5), 
/*     */ 
/*  85 */     ENGINE_UNSUPPORTED(6), 
/*     */ 
/*  91 */     NETWORK_ERROR(7), 
/*     */ 
/*  96 */     REMOTE_CANCEL(11), 
/*     */ 
/* 101 */     REMOTE_REJECT(12), 
/*     */ 
/* 106 */     REMOTE_HANGUP(13), 
/*     */ 
/* 111 */     REMOTE_BUSY_LINE(14), 
/*     */ 
/* 116 */     REMOTE_NO_RESPONSE(15), 
/*     */ 
/* 121 */     REMOTE_ENGINE_UNSUPPORTED(16), 
/*     */ 
/* 126 */     REMOTE_NETWORK_ERROR(17);
/*     */ 
/*     */     private int value;
/*     */ 
/* 131 */     private CallDisconnectedReason(int value) { this.value = value; }
/*     */ 
/*     */     public int getValue()
/*     */     {
/* 135 */       return this.value;
/*     */     }
/*     */ 
/*     */     public static CallDisconnectedReason valueOf(int value) {
/* 139 */       for (CallDisconnectedReason v : values()) {
/* 140 */         if (v.value == value)
/* 141 */           return v;
/*     */       }
/* 143 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum CallErrorCode
/*     */   {
/*  33 */     ENGINE_ERROR(1), 
/*  34 */     SIGNAL_ERROR(2);
/*     */ 
/*     */     private int value;
/*     */ 
/*  39 */     private CallErrorCode(int value) { this.value = value; }
/*     */ 
/*     */     public int getValue()
/*     */     {
/*  43 */       return this.value;
/*     */     }
/*     */ 
/*     */     public static CallErrorCode valueOf(int value) {
/*  47 */       for (CallErrorCode v : values()) {
/*  48 */         if (v.value == value)
/*  49 */           return v;
/*     */       }
/*  51 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum CallMediaType
/*     */   {
/*   9 */     AUDIO(1), 
/*  10 */     VIDEO(2);
/*     */ 
/*     */     private int value;
/*     */ 
/*  15 */     private CallMediaType(int value) { this.value = value; }
/*     */ 
/*     */     public int getValue()
/*     */     {
/*  19 */       return this.value;
/*     */     }
/*     */ 
/*     */     public static CallMediaType valueOf(int value) {
/*  23 */       for (CallMediaType v : values()) {
/*  24 */         if (v.value == value) {
/*  25 */           return v;
/*     */         }
/*     */       }
/*  28 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.RongCallCommon
 * JD-Core Version:    0.6.0
 */
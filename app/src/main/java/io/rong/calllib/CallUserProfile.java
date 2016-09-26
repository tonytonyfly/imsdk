/*    */ package io.rong.calllib;
/*    */ 
/*    */ import android.os.Parcel;
/*    */ import android.os.Parcelable;
/*    */ import android.os.Parcelable.Creator;
/*    */ import android.view.SurfaceView;
/*    */ import io.rong.common.ParcelUtils;
/*    */ 
/*    */ public class CallUserProfile
/*    */   implements Parcelable
/*    */ {
/*    */   private String userId;
/*    */   private String mediaId;
/*    */   private SurfaceView videoView;
/*    */   private RongCallCommon.CallStatus callStatus;
/*    */   private RongCallCommon.CallMediaType mediaType;
/* 88 */   public static final Parcelable.Creator<CallUserProfile> CREATOR = new Parcelable.Creator()
/*    */   {
/*    */     public CallUserProfile createFromParcel(Parcel source)
/*    */     {
/* 92 */       return new CallUserProfile(source);
/*    */     }
/*    */ 
/*    */     public CallUserProfile[] newArray(int size)
/*    */     {
/* 97 */       return new CallUserProfile[size];
/*    */     }
/* 88 */   };
/*    */ 
/*    */   public CallUserProfile()
/*    */   {
/*    */   }
/*    */ 
/*    */   public String getMediaId()
/*    */   {
/* 23 */     return this.mediaId;
/*    */   }
/*    */ 
/*    */   public void setMediaId(String mediaId) {
/* 27 */     this.mediaId = mediaId;
/*    */   }
/*    */ 
/*    */   public String getUserId() {
/* 31 */     return this.userId;
/*    */   }
/*    */ 
/*    */   public void setUserId(String userId) {
/* 35 */     this.userId = userId;
/*    */   }
/*    */ 
/*    */   public SurfaceView getVideoView() {
/* 39 */     return this.videoView;
/*    */   }
/*    */ 
/*    */   public void setVideoView(SurfaceView videoView) {
/* 43 */     this.videoView = videoView;
/*    */   }
/*    */ 
/*    */   public RongCallCommon.CallStatus getCallStatus() {
/* 47 */     return this.callStatus;
/*    */   }
/*    */ 
/*    */   public void setCallStatus(RongCallCommon.CallStatus callStatus) {
/* 51 */     this.callStatus = callStatus;
/*    */   }
/*    */ 
/*    */   public RongCallCommon.CallMediaType getMediaType() {
/* 55 */     return this.mediaType;
/*    */   }
/*    */ 
/*    */   public void setMediaType(RongCallCommon.CallMediaType mediaType) {
/* 59 */     this.mediaType = mediaType;
/*    */   }
/*    */ 
/*    */   public void writeToParcel(Parcel dest, int flags)
/*    */   {
/* 64 */     ParcelUtils.writeToParcel(dest, this.userId);
/* 65 */     ParcelUtils.writeToParcel(dest, this.mediaId);
/* 66 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.mediaType.getValue()));
/* 67 */     ParcelUtils.writeToParcel(dest, Integer.valueOf(this.callStatus.getValue()));
/*    */   }
/*    */ 
/*    */   public CallUserProfile(Parcel in)
/*    */   {
/* 72 */     this.userId = ParcelUtils.readFromParcel(in);
/* 73 */     this.mediaId = ParcelUtils.readFromParcel(in);
/* 74 */     this.mediaType = RongCallCommon.CallMediaType.valueOf(ParcelUtils.readIntFromParcel(in).intValue());
/* 75 */     this.callStatus = RongCallCommon.CallStatus.valueOf(ParcelUtils.readIntFromParcel(in).intValue());
/*    */   }
/*    */ 
/*    */   public int describeContents()
/*    */   {
/* 85 */     return 0;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.CallUserProfile
 * JD-Core Version:    0.6.0
 */
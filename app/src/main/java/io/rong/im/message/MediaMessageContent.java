/*    */ package io.rong.message;
/*    */ 
/*    */ import android.net.Uri;
/*    */ import io.rong.imlib.model.MessageContent;
/*    */ 
/*    */ public abstract class MediaMessageContent extends MessageContent
/*    */ {
/*    */   private Uri mLocalPath;
/*    */   private Uri mMediaUrl;
/*    */   private String mExtra;
/*    */ 
/*    */   public Uri getLocalPath()
/*    */   {
/* 13 */     return this.mLocalPath;
/*    */   }
/*    */ 
/*    */   public Uri getMediaUrl() {
/* 17 */     return this.mMediaUrl;
/*    */   }
/*    */ 
/*    */   public void setMediaUrl(Uri mMediaUrl) {
/* 21 */     this.mMediaUrl = mMediaUrl;
/*    */   }
/*    */ 
/*    */   public void setLocalPath(Uri mLocalPath) {
/* 25 */     this.mLocalPath = mLocalPath;
/*    */   }
/*    */ 
/*    */   public String getExtra() {
/* 29 */     return this.mExtra;
/*    */   }
/*    */ 
/*    */   public void setExtra(String mExtra) {
/* 33 */     this.mExtra = mExtra;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.MediaMessageContent
 * JD-Core Version:    0.6.0
 */
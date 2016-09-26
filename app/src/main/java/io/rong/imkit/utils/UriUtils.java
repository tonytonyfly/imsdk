/*    */ package io.rong.imkit.utils;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.net.Uri;
/*    */ import android.net.Uri.Builder;
/*    */ import io.rong.imlib.model.Message;
/*    */ 
/*    */ public class UriUtils
/*    */ {
/*    */   public static Uri obtainThumImageUri(Context context, Message message)
/*    */   {
/* 14 */     Uri uri = Uri.parse("rong://" + context.getPackageName()).buildUpon().appendPath("image").appendPath("thum").appendPath(String.valueOf(message.getMessageId())).build();
/* 15 */     return uri;
/*    */   }
/*    */ 
/*    */   public static Uri obtainVoiceUri(Context context, Message message) {
/* 19 */     Uri uri = Uri.parse("rong://" + context.getPackageName()).buildUpon().appendPath("voice").appendPath(String.valueOf(message.getMessageId())).build();
/* 20 */     return uri;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.utils.UriUtils
 * JD-Core Version:    0.6.0
 */
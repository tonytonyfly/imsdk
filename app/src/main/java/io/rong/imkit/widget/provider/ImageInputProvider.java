/*    */ package io.rong.imkit.widget.provider;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.content.Intent;
/*    */ import android.content.res.Resources;
/*    */ import android.graphics.drawable.Drawable;
/*    */ import android.view.View;
/*    */ import io.rong.imkit.R.drawable;
/*    */ import io.rong.imkit.R.string;
/*    */ import io.rong.imkit.RongContext;
/*    */ import io.rong.imkit.activity.PictureSelectorActivity;
/*    */ import io.rong.imkit.manager.SendImageManager;
/*    */ import io.rong.imlib.model.Conversation;
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ public class ImageInputProvider extends InputProvider.ExtendProvider
/*    */ {
/*    */   private static final String TAG = "ImageInputProvider";
/*    */ 
/*    */   public ImageInputProvider(RongContext context)
/*    */   {
/* 22 */     super(context);
/*    */   }
/*    */ 
/*    */   public Drawable obtainPluginDrawable(Context context)
/*    */   {
/* 27 */     return context.getResources().getDrawable(R.drawable.rc_ic_picture);
/*    */   }
/*    */ 
/*    */   public CharSequence obtainPluginTitle(Context context)
/*    */   {
/* 32 */     return context.getString(R.string.rc_plugins_image);
/*    */   }
/*    */ 
/*    */   public void onPluginClick(View view)
/*    */   {
/* 37 */     Intent intent = new Intent();
/* 38 */     intent.setClass(view.getContext(), PictureSelectorActivity.class);
/* 39 */     startActivityForResult(intent, 23);
/*    */   }
/*    */ 
/*    */   public void onActivityResult(int requestCode, int resultCode, Intent data)
/*    */   {
/* 44 */     super.onActivityResult(requestCode, resultCode, data);
/*    */ 
/* 46 */     if (resultCode != -1) {
/* 47 */       return;
/*    */     }
/* 49 */     boolean sendOrigin = data.getBooleanExtra("sendOrigin", false);
/* 50 */     ArrayList list = data.getParcelableArrayListExtra("android.intent.extra.RETURN_RESULT");
/* 51 */     Conversation conversation = getCurrentConversation();
/* 52 */     SendImageManager.getInstance().sendImages(conversation.getConversationType(), conversation.getTargetId(), list, sendOrigin);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.ImageInputProvider
 * JD-Core Version:    0.6.0
 */
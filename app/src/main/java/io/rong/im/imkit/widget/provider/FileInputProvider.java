/*    */ package io.rong.imkit.widget.provider;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.content.Intent;
/*    */ import android.content.res.Resources;
/*    */ import android.graphics.drawable.Drawable;
/*    */ import android.net.Uri;
/*    */ import android.view.View;
/*    */ import io.rong.imkit.R.drawable;
/*    */ import io.rong.imkit.R.string;
/*    */ import io.rong.imkit.RongContext;
/*    */ import io.rong.imkit.RongIM;
/*    */ import io.rong.imkit.activity.FileManagerActivity;
/*    */ import io.rong.imkit.model.FileInfo;
/*    */ import io.rong.imlib.model.Conversation;
/*    */ import io.rong.imlib.model.Message;
/*    */ import io.rong.message.FileMessage;
/*    */ import java.util.HashSet;
/*    */ 
/*    */ public class FileInputProvider extends InputProvider.ExtendProvider
/*    */ {
/*    */   private static final String TAG = "FileInputProvider";
/*    */   private static final int REQUEST_FILE = 100;
/*    */ 
/*    */   public FileInputProvider(RongContext context)
/*    */   {
/* 28 */     super(context);
/*    */   }
/*    */ 
/*    */   public Drawable obtainPluginDrawable(Context context)
/*    */   {
/* 33 */     return context.getResources().getDrawable(R.drawable.rc_ic_files);
/*    */   }
/*    */ 
/*    */   public CharSequence obtainPluginTitle(Context context)
/*    */   {
/* 38 */     return context.getString(R.string.rc_plugins_files);
/*    */   }
/*    */ 
/*    */   public void onPluginClick(View view)
/*    */   {
/* 43 */     Intent intent = new Intent(getContext(), FileManagerActivity.class);
/* 44 */     startActivityForResult(intent, 100);
/*    */   }
/*    */ 
/*    */   public void onActivityResult(int requestCode, int resultCode, Intent data)
/*    */   {
/* 49 */     super.onActivityResult(requestCode, resultCode, data);
/* 50 */     if ((requestCode == 100) && 
/* 51 */       (data != null)) {
/* 52 */       HashSet selectedFileInfos = (HashSet)data.getSerializableExtra("sendSelectedFiles");
/* 53 */       for (FileInfo fileInfo : selectedFileInfos) {
/* 54 */         Uri filePath = Uri.parse("file://" + fileInfo.getFilePath());
/* 55 */         FileMessage fileMessage = FileMessage.obtain(filePath);
/* 56 */         if (fileMessage != null) {
/* 57 */           fileMessage.setType(fileInfo.getSuffix());
/* 58 */           Message message = Message.obtain(getCurrentConversation().getTargetId(), getCurrentConversation().getConversationType(), fileMessage);
/* 59 */           RongIM.getInstance().sendMediaMessage(message, null, null, null);
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.FileInputProvider
 * JD-Core Version:    0.6.0
 */
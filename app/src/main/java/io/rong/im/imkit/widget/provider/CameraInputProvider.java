/*     */ package io.rong.imkit.widget.provider;
/*     */ 
/*     */ import android.content.ContentResolver;
/*     */ import android.content.Context;
/*     */ import android.content.Intent;
/*     */ import android.content.res.Resources;
/*     */ import android.database.Cursor;
/*     */ import android.graphics.drawable.Drawable;
/*     */ import android.net.Uri;
/*     */ import android.view.View;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.SendImageMessageCallback;
/*     */ import io.rong.imlib.model.Conversation;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.message.ImageMessage;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class CameraInputProvider extends InputProvider.ExtendProvider
/*     */ {
/*     */   public CameraInputProvider(RongContext context)
/*     */   {
/*  23 */     super(context);
/*     */   }
/*     */ 
/*     */   public Drawable obtainPluginDrawable(Context context)
/*     */   {
/*  28 */     return context.getResources().getDrawable(R.drawable.rc_ic_camera);
/*     */   }
/*     */ 
/*     */   public CharSequence obtainPluginTitle(Context context)
/*     */   {
/*  33 */     return context.getString(R.string.rc_plugins_camera);
/*     */   }
/*     */ 
/*     */   public void onPluginClick(View view)
/*     */   {
/*  38 */     Intent intent = new Intent();
/*  39 */     intent.setClass(view.getContext(), TakingPicturesActivity.class);
/*  40 */     startActivityForResult(intent, 24);
/*     */   }
/*     */ 
/*     */   public void onActivityResult(int requestCode, int resultCode, Intent data)
/*     */   {
/*  46 */     if ((resultCode != -1) || (data == null)) {
/*  47 */       return;
/*     */     }
/*  49 */     if ((data.getData() != null) && ("content".equals(data.getData().getScheme()))) {
/*  50 */       getContext().executorBackground(new PublishRunnable(data.getData()));
/*  51 */     } else if ((data.getData() != null) && ("file".equals(data.getData().getScheme()))) {
/*  52 */       getContext().executorBackground(new PublicLocationRunnable(data.getData()));
/*  53 */     } else if (data.hasExtra("android.intent.extra.RETURN_RESULT"))
/*     */     {
/*  55 */       ArrayList uris = data.getParcelableArrayListExtra("android.intent.extra.RETURN_RESULT");
/*     */ 
/*  57 */       for (Uri item : uris) {
/*  58 */         getContext().executorBackground(new PublishRunnable(item));
/*     */       }
/*     */     }
/*     */ 
/*  62 */     super.onActivityResult(requestCode, resultCode, data);
/*     */   }
/*     */ 
/*     */   private void sendImage(Uri uri) {
/*  66 */     if (this.mCurrentConversation == null) {
/*  67 */       return;
/*     */     }
/*  69 */     ImageMessage content = ImageMessage.obtain(uri, uri);
/*  70 */     Message message = Message.obtain(this.mCurrentConversation.getTargetId(), this.mCurrentConversation.getConversationType(), content);
/*     */ 
/*  72 */     RongIM.getInstance().sendImageMessage(message, null, null, new RongIMClient.SendImageMessageCallback()
/*     */     {
/*     */       public void onAttached(Message message)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onError(Message message, RongIMClient.ErrorCode code)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onSuccess(Message message)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onProgress(Message message, int progress)
/*     */       {
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   class PublicLocationRunnable
/*     */     implements Runnable
/*     */   {
/*     */     Uri mUri;
/*     */ 
/*     */     public PublicLocationRunnable(Uri uri)
/*     */     {
/* 124 */       this.mUri = uri;
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 129 */       CameraInputProvider.this.sendImage(this.mUri);
/*     */     }
/*     */   }
/*     */ 
/*     */   class PublishRunnable
/*     */     implements Runnable
/*     */   {
/*     */     Uri mUri;
/*     */ 
/*     */     public PublishRunnable(Uri uri)
/*     */     {
/* 100 */       this.mUri = uri;
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 105 */       Cursor cursor = CameraInputProvider.this.getContext().getContentResolver().query(this.mUri, new String[] { "_data" }, null, null, null);
/*     */ 
/* 107 */       if ((cursor == null) || (cursor.getCount() == 0)) {
/* 108 */         cursor.close();
/* 109 */         return;
/*     */       }
/*     */ 
/* 112 */       cursor.moveToFirst();
/* 113 */       Uri uri = Uri.parse("file://" + cursor.getString(0));
/* 114 */       cursor.close();
/*     */ 
/* 116 */       CameraInputProvider.this.sendImage(uri);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.CameraInputProvider
 * JD-Core Version:    0.6.0
 */
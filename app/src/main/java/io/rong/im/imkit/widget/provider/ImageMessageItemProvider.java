/*     */ package io.rong.imkit.widget.provider;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.DialogInterface;
/*     */ import android.content.Intent;
/*     */ import android.content.res.Resources;
/*     */ import android.content.res.Resources.NotFoundException;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.text.Spannable;
/*     */ import android.text.SpannableString;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.TextView;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imkit.R.bool;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.integer;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.activity.PicturePagerActivity;
/*     */ import io.rong.imkit.manager.SendImageManager;
/*     */ import io.rong.imkit.model.ProviderTag;
/*     */ import io.rong.imkit.model.UIMessage;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment.OnArraysDialogItemListener;
/*     */ import io.rong.imkit.widget.AsyncImageView;
/*     */ import io.rong.imlib.model.Message.MessageDirection;
/*     */ import io.rong.imlib.model.Message.SentStatus;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import io.rong.message.ImageMessage;
/*     */ 
/*     */ @ProviderTag(messageContent=ImageMessage.class, showProgress=false)
/*     */ public class ImageMessageItemProvider extends IContainerItemProvider.MessageProvider<ImageMessage>
/*     */ {
/*     */   private static final String TAG = "ImageMessageItemProvider";
/*     */ 
/*     */   public View newView(Context context, ViewGroup group)
/*     */   {
/*  45 */     View view = LayoutInflater.from(context).inflate(R.layout.rc_item_image_message, null);
/*     */ 
/*  47 */     ViewHolder holder = new ViewHolder();
/*     */ 
/*  49 */     holder.message = ((TextView)view.findViewById(R.id.rc_msg));
/*  50 */     holder.img = ((AsyncImageView)view.findViewById(R.id.rc_img));
/*     */ 
/*  52 */     view.setTag(holder);
/*  53 */     return view;
/*     */   }
/*     */ 
/*     */   public void onItemClick(View view, int position, ImageMessage content, UIMessage message)
/*     */   {
/*  58 */     if (content != null) {
/*  59 */       Intent intent = new Intent(view.getContext(), PicturePagerActivity.class);
/*  60 */       intent.putExtra("message", message);
/*  61 */       view.getContext().startActivity(intent);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onItemLongClick(View view, int position, ImageMessage content, UIMessage message)
/*     */   {
/*  67 */     String name = null;
/*     */ 
/*  70 */     if (message.getSenderUserId() != null) {
/*  71 */       UserInfo userInfo = message.getUserInfo();
/*  72 */       if (userInfo == null)
/*  73 */         userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
/*  74 */       if (userInfo != null) {
/*  75 */         name = userInfo.getName();
/*     */       }
/*     */     }
/*  78 */     long deltaTime = RongIM.getInstance().getDeltaTime();
/*  79 */     long normalTime = System.currentTimeMillis() - deltaTime;
/*  80 */     boolean enableMessageRecall = false;
/*  81 */     int messageRecallInterval = -1;
/*  82 */     boolean hasSent = (!message.getSentStatus().equals(Message.SentStatus.SENDING)) && (!message.getSentStatus().equals(Message.SentStatus.FAILED));
/*     */     try
/*     */     {
/*  85 */       enableMessageRecall = RongContext.getInstance().getResources().getBoolean(R.bool.rc_enable_message_recall);
/*  86 */       messageRecallInterval = RongContext.getInstance().getResources().getInteger(R.integer.rc_message_recall_interval);
/*     */     } catch (Resources.NotFoundException e) {
/*  88 */       RLog.e("ImageMessageItemProvider", "rc_message_recall_interval not configure in rc_config.xml");
/*  89 */       e.printStackTrace();
/*     */     }
/*     */     String[] items;
/*     */     String[] items;
/*  92 */     if ((hasSent) && (enableMessageRecall) && (normalTime - message.getSentTime() <= messageRecallInterval * 1000) && (message.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId())))
/*  93 */       items = new String[] { view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete), view.getContext().getResources().getString(R.string.rc_dialog_item_message_recall) };
/*     */     else {
/*  95 */       items = new String[] { view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete) };
/*     */     }
/*     */ 
/*  98 */     ArraysDialogFragment.newInstance(name, items).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener(message)
/*     */     {
/*     */       public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
/* 101 */         if (which == 0) {
/* 102 */           SendImageManager.getInstance().cancelSendingImage(this.val$message.getConversationType(), this.val$message.getTargetId(), this.val$message.getMessageId());
/* 103 */           RongIM.getInstance().deleteMessages(new int[] { this.val$message.getMessageId() }, null);
/* 104 */         } else if (which == 1) {
/* 105 */           RongIM.getInstance().recallMessage(this.val$message);
/*     */         }
/*     */       }
/*     */     }).show(((FragmentActivity)view.getContext()).getSupportFragmentManager());
/*     */   }
/*     */ 
/*     */   public void bindView(View v, int position, ImageMessage content, UIMessage message)
/*     */   {
/* 114 */     ViewHolder holder = (ViewHolder)v.getTag();
/*     */ 
/* 116 */     if (message.getMessageDirection() == Message.MessageDirection.SEND)
/* 117 */       v.setBackgroundResource(R.drawable.rc_ic_bubble_no_right);
/*     */     else {
/* 119 */       v.setBackgroundResource(R.drawable.rc_ic_bubble_no_left);
/*     */     }
/*     */ 
/* 122 */     holder.img.setResource(content.getThumUri());
/*     */ 
/* 124 */     int progress = message.getProgress();
/*     */ 
/* 126 */     Message.SentStatus status = message.getSentStatus();
/*     */ 
/* 128 */     if ((status.equals(Message.SentStatus.SENDING)) && (progress < 100)) {
/* 129 */       holder.message.setText(progress + "%");
/* 130 */       holder.message.setVisibility(0);
/*     */     } else {
/* 132 */       holder.message.setVisibility(8);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Spannable getContentSummary(ImageMessage data)
/*     */   {
/* 138 */     return new SpannableString(RongContext.getInstance().getResources().getString(R.string.rc_message_content_image));
/*     */   }
/*     */ 
/*     */   class ViewHolder
/*     */   {
/*     */     AsyncImageView img;
/*     */     TextView message;
/*     */ 
/*     */     ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.ImageMessageItemProvider
 * JD-Core Version:    0.6.0
 */
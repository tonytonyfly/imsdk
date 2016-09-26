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
/*     */ import android.widget.ImageView;
/*     */ import android.widget.ProgressBar;
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
/*     */ import io.rong.imkit.activity.FilePreviewActivity;
/*     */ import io.rong.imkit.manager.SendImageManager;
/*     */ import io.rong.imkit.model.ProviderTag;
/*     */ import io.rong.imkit.model.UIMessage;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.utils.FileTypeUtils;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment.OnArraysDialogItemListener;
/*     */ import io.rong.imlib.model.Message.MessageDirection;
/*     */ import io.rong.imlib.model.Message.SentStatus;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import io.rong.message.FileMessage;
/*     */ 
/*     */ @ProviderTag(messageContent=FileMessage.class, showProgress=false)
/*     */ public class FileMessageItemProvider extends IContainerItemProvider.MessageProvider<FileMessage>
/*     */ {
/*     */   private static final String TAG = "FileMessageItemProvider";
/*     */   private ViewHolder holder;
/*     */ 
/*     */   public View newView(Context context, ViewGroup group)
/*     */   {
/*  56 */     View view = LayoutInflater.from(context).inflate(R.layout.rc_item_file_message, null);
/*  57 */     ViewHolder holder = new ViewHolder();
/*  58 */     holder.fileTypeImage = ((ImageView)view.findViewById(R.id.rc_msg_iv_file_type_image));
/*  59 */     holder.fileName = ((TextView)view.findViewById(R.id.rc_msg_tv_file_name));
/*  60 */     holder.fileSize = ((TextView)view.findViewById(R.id.rc_msg_tv_file_size));
/*  61 */     holder.fileUploadProgress = ((ProgressBar)view.findViewById(R.id.rc_msg_pb_file_upload_progress));
/*  62 */     view.setTag(holder);
/*  63 */     return view;
/*     */   }
/*     */ 
/*     */   public void bindView(View v, int position, FileMessage content, UIMessage message)
/*     */   {
/*  78 */     this.holder = ((ViewHolder)v.getTag());
/*     */ 
/*  80 */     if (message.getMessageDirection() == Message.MessageDirection.SEND)
/*  81 */       v.setBackgroundResource(R.drawable.rc_ic_bubble_right_file);
/*     */     else {
/*  83 */       v.setBackgroundResource(R.drawable.rc_ic_bubble_left_file);
/*     */     }
/*  85 */     this.holder.fileName.setText(content.getName());
/*  86 */     long fileSizeBytes = content.getSize();
/*  87 */     this.holder.fileSize.setText(FileTypeUtils.getInstance().formatFileSize(fileSizeBytes));
/*  88 */     this.holder.fileTypeImage.setImageResource(FileTypeUtils.getInstance().fileTypeImageId(content.getName()));
/*     */ 
/*  90 */     if ((message.getSentStatus().equals(Message.SentStatus.SENDING)) && (message.getProgress() < 100)) {
/*  91 */       this.holder.fileUploadProgress.setVisibility(0);
/*  92 */       this.holder.fileUploadProgress.setProgress(message.getProgress());
/*     */     } else {
/*  94 */       this.holder.fileUploadProgress.setVisibility(4);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Spannable getContentSummary(FileMessage data)
/*     */   {
/* 107 */     StringBuilder summaryPhrase = new StringBuilder();
/* 108 */     String fileName = data.getName();
/* 109 */     summaryPhrase.append(RongContext.getInstance().getString(R.string.rc_message_content_file)).append(" ").append(fileName);
/*     */ 
/* 112 */     return new SpannableString(summaryPhrase);
/*     */   }
/*     */ 
/*     */   public void onItemClick(View view, int position, FileMessage content, UIMessage message)
/*     */   {
/* 125 */     Intent intent = new Intent(view.getContext(), FilePreviewActivity.class);
/* 126 */     intent.putExtra("FileMessage", content);
/* 127 */     intent.putExtra("Message", message);
/* 128 */     view.getContext().startActivity(intent);
/*     */   }
/*     */ 
/*     */   public void onItemLongClick(View view, int position, FileMessage content, UIMessage message)
/*     */   {
/* 141 */     String name = null;
/*     */ 
/* 144 */     if (message.getSenderUserId() != null) {
/* 145 */       UserInfo userInfo = message.getUserInfo();
/* 146 */       if (userInfo == null)
/* 147 */         userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
/* 148 */       if (userInfo != null) {
/* 149 */         name = userInfo.getName();
/*     */       }
/*     */     }
/* 152 */     long deltaTime = RongIM.getInstance().getDeltaTime();
/* 153 */     long normalTime = System.currentTimeMillis() - deltaTime;
/* 154 */     boolean enableMessageRecall = false;
/* 155 */     int messageRecallInterval = -1;
/* 156 */     boolean hasSent = (!message.getSentStatus().equals(Message.SentStatus.SENDING)) && (!message.getSentStatus().equals(Message.SentStatus.FAILED));
/*     */     try
/*     */     {
/* 159 */       enableMessageRecall = RongContext.getInstance().getResources().getBoolean(R.bool.rc_enable_message_recall);
/* 160 */       messageRecallInterval = RongContext.getInstance().getResources().getInteger(R.integer.rc_message_recall_interval);
/*     */     } catch (Resources.NotFoundException e) {
/* 162 */       RLog.e("FileMessageItemProvider", "rc_message_recall_interval not configure in rc_config.xml");
/* 163 */       e.printStackTrace();
/*     */     }
/* 165 */     if ((message.getSentStatus().equals(Message.SentStatus.SENDING)) && (message.getProgress() < 100))
/* 166 */       return;
/*     */     String[] items;
/*     */     String[] items;
/* 169 */     if ((hasSent) && (enableMessageRecall) && (normalTime - message.getSentTime() <= messageRecallInterval * 1000) && (message.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId())))
/* 170 */       items = new String[] { view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete), view.getContext().getResources().getString(R.string.rc_dialog_item_message_recall) };
/*     */     else {
/* 172 */       items = new String[] { view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete) };
/*     */     }
/*     */ 
/* 175 */     ArraysDialogFragment.newInstance(name, items).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener(message)
/*     */     {
/*     */       public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
/* 178 */         if (which == 0) {
/* 179 */           SendImageManager.getInstance().cancelSendingImage(this.val$message.getConversationType(), this.val$message.getTargetId(), this.val$message.getMessageId());
/* 180 */           RongIM.getInstance().deleteMessages(new int[] { this.val$message.getMessageId() }, null);
/* 181 */         } else if (which == 1) {
/* 182 */           RongIM.getInstance().recallMessage(this.val$message);
/*     */         }
/*     */       }
/*     */     }).show(((FragmentActivity)view.getContext()).getSupportFragmentManager());
/*     */   }
/*     */ 
/*     */   class ViewHolder
/*     */   {
/*     */     TextView fileName;
/*     */     TextView fileSize;
/*     */     ImageView fileTypeImage;
/*     */     ProgressBar fileUploadProgress;
/*     */ 
/*     */     ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.FileMessageItemProvider
 * JD-Core Version:    0.6.0
 */
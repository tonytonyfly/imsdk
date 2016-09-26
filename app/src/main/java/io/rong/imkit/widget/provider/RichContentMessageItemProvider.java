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
/*     */ import android.widget.RelativeLayout;
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
/*     */ import io.rong.imkit.model.ProviderTag;
/*     */ import io.rong.imkit.model.UIMessage;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment.OnArraysDialogItemListener;
/*     */ import io.rong.imkit.widget.AsyncImageView;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Conversation.PublicServiceType;
/*     */ import io.rong.imlib.model.Message.MessageDirection;
/*     */ import io.rong.imlib.model.Message.SentStatus;
/*     */ import io.rong.imlib.model.PublicServiceProfile;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import io.rong.message.RichContentMessage;
/*     */ 
/*     */ @ProviderTag(messageContent=RichContentMessage.class)
/*     */ public class RichContentMessageItemProvider extends IContainerItemProvider.MessageProvider<RichContentMessage>
/*     */ {
/*     */   private static final String TAG = "RichContentMessageItemProvider";
/*     */ 
/*     */   public View newView(Context context, ViewGroup group)
/*     */   {
/*  45 */     View view = LayoutInflater.from(context).inflate(R.layout.rc_item_rich_content_message, null);
/*  46 */     ViewHolder holder = new ViewHolder();
/*  47 */     holder.title = ((TextView)view.findViewById(R.id.rc_title));
/*  48 */     holder.content = ((TextView)view.findViewById(R.id.rc_content));
/*  49 */     holder.img = ((AsyncImageView)view.findViewById(R.id.rc_img));
/*  50 */     holder.mLayout = ((RelativeLayout)view.findViewById(R.id.rc_layout));
/*  51 */     view.setTag(holder);
/*  52 */     return view;
/*     */   }
/*     */ 
/*     */   public void onItemClick(View view, int position, RichContentMessage content, UIMessage message)
/*     */   {
/*  58 */     String action = "io.rong.imkit.intent.action.webview";
/*  59 */     Intent intent = new Intent(action);
/*  60 */     intent.addFlags(268435456);
/*  61 */     intent.putExtra("url", content.getUrl());
/*  62 */     intent.setPackage(view.getContext().getPackageName());
/*  63 */     view.getContext().startActivity(intent);
/*     */   }
/*     */ 
/*     */   public void onItemLongClick(View view, int position, RichContentMessage content, UIMessage message)
/*     */   {
/*  68 */     String name = null;
/*  69 */     if ((message.getConversationType().getName().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName())) || (message.getConversationType().getName().equals(Conversation.ConversationType.PUBLIC_SERVICE.getName())))
/*     */     {
/*  71 */       if (message.getUserInfo() != null) {
/*  72 */         name = message.getUserInfo().getName();
/*     */       } else {
/*  74 */         Conversation.PublicServiceType publicServiceType = Conversation.PublicServiceType.setValue(message.getConversationType().getValue());
/*  75 */         PublicServiceProfile info = RongUserInfoManager.getInstance().getPublicServiceProfile(publicServiceType, message.getTargetId());
/*     */ 
/*  77 */         if (info != null)
/*  78 */           name = info.getName();
/*     */       }
/*     */     } else {
/*  81 */       UserInfo userInfo = message.getUserInfo();
/*  82 */       if (userInfo == null)
/*  83 */         userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
/*  84 */       if (userInfo != null) {
/*  85 */         name = userInfo.getName();
/*     */       }
/*     */     }
/*     */ 
/*  89 */     long deltaTime = RongIM.getInstance().getDeltaTime();
/*  90 */     long normalTime = System.currentTimeMillis() - deltaTime;
/*  91 */     boolean enableMessageRecall = false;
/*  92 */     int messageRecallInterval = -1;
/*  93 */     boolean hasSent = (!message.getSentStatus().equals(Message.SentStatus.SENDING)) && (!message.getSentStatus().equals(Message.SentStatus.FAILED));
/*     */     try
/*     */     {
/*  96 */       enableMessageRecall = RongContext.getInstance().getResources().getBoolean(R.bool.rc_enable_message_recall);
/*  97 */       messageRecallInterval = RongContext.getInstance().getResources().getInteger(R.integer.rc_message_recall_interval);
/*     */     } catch (Resources.NotFoundException e) {
/*  99 */       RLog.e("RichContentMessageItemProvider", "rc_message_recall_interval not configure in rc_config.xml");
/* 100 */       e.printStackTrace();
/*     */     }
/*     */     String[] items;
/*     */     String[] items;
/* 102 */     if ((hasSent) && (enableMessageRecall) && (normalTime - message.getSentTime() <= messageRecallInterval * 1000) && (message.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId())))
/* 103 */       items = new String[] { view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete), view.getContext().getResources().getString(R.string.rc_dialog_item_message_recall) };
/*     */     else {
/* 105 */       items = new String[] { view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete) };
/*     */     }
/*     */ 
/* 108 */     ArraysDialogFragment.newInstance(name, items).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener(message)
/*     */     {
/*     */       public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
/* 111 */         if (which == 0)
/* 112 */           RongIM.getInstance().deleteMessages(new int[] { this.val$message.getMessageId() }, null);
/* 113 */         else if (which == 1)
/* 114 */           RongIM.getInstance().recallMessage(this.val$message);
/*     */       }
/*     */     }).show(((FragmentActivity)view.getContext()).getSupportFragmentManager());
/*     */   }
/*     */ 
/*     */   public void bindView(View v, int position, RichContentMessage content, UIMessage message)
/*     */   {
/* 122 */     ViewHolder holder = (ViewHolder)v.getTag();
/* 123 */     holder.title.setText(content.getTitle());
/* 124 */     holder.content.setText(content.getContent());
/* 125 */     if (content.getImgUrl() != null) {
/* 126 */       holder.img.setResource(content.getImgUrl(), 0);
/*     */     }
/*     */ 
/* 129 */     if (message.getMessageDirection() == Message.MessageDirection.SEND)
/* 130 */       holder.mLayout.setBackgroundResource(R.drawable.rc_ic_bubble_right_file);
/*     */     else
/* 132 */       holder.mLayout.setBackgroundResource(R.drawable.rc_ic_bubble_left_file);
/*     */   }
/*     */ 
/*     */   public Spannable getContentSummary(RichContentMessage data)
/*     */   {
/* 138 */     return new SpannableString(RongContext.getInstance().getResources().getString(R.string.rc_message_content_rich_text));
/*     */   }
/*     */ 
/*     */   class ViewHolder
/*     */   {
/*     */     AsyncImageView img;
/*     */     TextView title;
/*     */     TextView content;
/*     */     RelativeLayout mLayout;
/*     */ 
/*     */     ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.RichContentMessageItemProvider
 * JD-Core Version:    0.6.0
 */
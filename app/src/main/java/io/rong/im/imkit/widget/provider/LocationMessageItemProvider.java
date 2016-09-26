/*     */ package io.rong.imkit.widget.provider;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.DialogInterface;
/*     */ import android.content.res.Resources;
/*     */ import android.content.res.Resources.NotFoundException;
/*     */ import android.net.Uri;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.text.Spannable;
/*     */ import android.text.SpannableString;
/*     */ import android.util.DisplayMetrics;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import android.view.ViewGroup.LayoutParams;
/*     */ import android.widget.FrameLayout;
/*     */ import android.widget.FrameLayout.LayoutParams;
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
/*     */ import io.rong.message.LocationMessage;
/*     */ 
/*     */ @ProviderTag(messageContent=LocationMessage.class)
/*     */ public class LocationMessageItemProvider extends IContainerItemProvider.MessageProvider<LocationMessage>
/*     */ {
/*     */   private static final String TAG = "LocationMessageItemProvider";
/*     */ 
/*     */   public View newView(Context context, ViewGroup group)
/*     */   {
/*  44 */     View view = LayoutInflater.from(context).inflate(R.layout.rc_item_location_message, null);
/*     */ 
/*  46 */     ViewHolder holder = new ViewHolder();
/*     */ 
/*  48 */     holder.img = ((AsyncImageView)view.findViewById(R.id.rc_img));
/*  49 */     holder.title = ((TextView)view.findViewById(R.id.rc_content));
/*  50 */     holder.mLayout = ((FrameLayout)view.findViewById(R.id.rc_layout));
/*     */ 
/*  52 */     view.setTag(holder);
/*  53 */     return view;
/*     */   }
/*     */ 
/*     */   public void onItemClick(View view, int position, LocationMessage content, UIMessage message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onItemLongClick(View view, int position, LocationMessage content, UIMessage message)
/*     */   {
/*  63 */     String name = null;
/*     */ 
/*  65 */     if ((message.getConversationType().getName().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName())) || (message.getConversationType().getName().equals(Conversation.ConversationType.PUBLIC_SERVICE.getName())))
/*     */     {
/*  67 */       if (message.getUserInfo() != null) {
/*  68 */         name = message.getUserInfo().getName();
/*     */       } else {
/*  70 */         Conversation.PublicServiceType publicServiceType = Conversation.PublicServiceType.setValue(message.getConversationType().getValue());
/*  71 */         PublicServiceProfile info = RongUserInfoManager.getInstance().getPublicServiceProfile(publicServiceType, message.getTargetId());
/*     */ 
/*  73 */         if (info != null)
/*  74 */           name = info.getName();
/*     */       }
/*     */     } else {
/*  77 */       UserInfo userInfo = message.getUserInfo();
/*  78 */       if (userInfo == null)
/*  79 */         userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
/*  80 */       if (userInfo != null) {
/*  81 */         name = userInfo.getName();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  86 */     long deltaTime = RongIM.getInstance().getDeltaTime();
/*  87 */     long normalTime = System.currentTimeMillis() - deltaTime;
/*  88 */     boolean enableMessageRecall = false;
/*  89 */     int messageRecallInterval = -1;
/*  90 */     boolean hasSent = (!message.getSentStatus().equals(Message.SentStatus.SENDING)) && (!message.getSentStatus().equals(Message.SentStatus.FAILED));
/*     */     try
/*     */     {
/*  93 */       enableMessageRecall = RongContext.getInstance().getResources().getBoolean(R.bool.rc_enable_message_recall);
/*  94 */       messageRecallInterval = RongContext.getInstance().getResources().getInteger(R.integer.rc_message_recall_interval);
/*     */     } catch (Resources.NotFoundException e) {
/*  96 */       RLog.e("LocationMessageItemProvider", "rc_message_recall_interval not configure in rc_config.xml");
/*  97 */       e.printStackTrace();
/*     */     }
/*     */     String[] items;
/*     */     String[] items;
/* 100 */     if ((hasSent) && (enableMessageRecall) && (normalTime - message.getSentTime() <= messageRecallInterval * 1000) && (message.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId())))
/* 101 */       items = new String[] { view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete), view.getContext().getResources().getString(R.string.rc_dialog_item_message_recall) };
/*     */     else {
/* 103 */       items = new String[] { view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete) };
/*     */     }
/*     */ 
/* 106 */     ArraysDialogFragment.newInstance(name, items).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener(message)
/*     */     {
/*     */       public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
/* 109 */         if (which == 0)
/* 110 */           RongIM.getInstance().deleteMessages(new int[] { this.val$message.getMessageId() }, null);
/* 111 */         else if (which == 1)
/* 112 */           RongIM.getInstance().recallMessage(this.val$message);
/*     */       }
/*     */     }).show(((FragmentActivity)view.getContext()).getSupportFragmentManager());
/*     */   }
/*     */ 
/*     */   public void bindView(View v, int position, LocationMessage content, UIMessage uiMsg)
/*     */   {
/* 121 */     ViewHolder holder = (ViewHolder)v.getTag();
/* 122 */     Uri uri = content.getImgUri();
/* 123 */     RLog.d("LocationMessageItemProvider", "uri = " + uri);
/* 124 */     if ((uri == null) || (!uri.getScheme().equals("file")))
/* 125 */       holder.img.setDefaultDrawable();
/*     */     else {
/* 127 */       holder.img.setResource(uri);
/*     */     }
/*     */ 
/* 130 */     FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(holder.title.getLayoutParams().width, -2);
/* 131 */     params.gravity = 80;
/*     */ 
/* 133 */     if (uiMsg.getMessageDirection() == Message.MessageDirection.SEND) {
/* 134 */       holder.mLayout.setBackgroundResource(R.drawable.rc_ic_bubble_no_right);
/* 135 */       params.leftMargin = 0;
/* 136 */       params.rightMargin = (int)(12.0F * v.getResources().getDisplayMetrics().density);
/* 137 */       holder.title.setLayoutParams(params);
/*     */     } else {
/* 139 */       params.leftMargin = (int)(8.5D * v.getResources().getDisplayMetrics().density);
/* 140 */       params.rightMargin = 0;
/* 141 */       holder.title.setLayoutParams(params);
/* 142 */       holder.mLayout.setBackgroundResource(R.drawable.rc_ic_bubble_no_left);
/*     */     }
/* 144 */     holder.title.setText(content.getPoi());
/*     */   }
/*     */ 
/*     */   public Spannable getContentSummary(LocationMessage data)
/*     */   {
/* 149 */     return new SpannableString(RongContext.getInstance().getResources().getString(R.string.rc_message_content_location));
/*     */   }
/*     */ 
/*     */   class ViewHolder
/*     */   {
/*     */     AsyncImageView img;
/*     */     TextView title;
/*     */     FrameLayout mLayout;
/*     */ 
/*     */     ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.LocationMessageItemProvider
 * JD-Core Version:    0.6.0
 */
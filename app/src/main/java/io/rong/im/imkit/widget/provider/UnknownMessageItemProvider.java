/*    */ package io.rong.imkit.widget.provider;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.content.DialogInterface;
/*    */ import android.content.res.Resources;
/*    */ import android.support.v4.app.FragmentActivity;
/*    */ import android.text.Spannable;
/*    */ import android.text.SpannableString;
/*    */ import android.text.method.LinkMovementMethod;
/*    */ import android.view.LayoutInflater;
/*    */ import android.view.View;
/*    */ import android.view.ViewGroup;
/*    */ import android.widget.TextView;
/*    */ import io.rong.imkit.R.id;
/*    */ import io.rong.imkit.R.layout;
/*    */ import io.rong.imkit.R.string;
/*    */ import io.rong.imkit.RongContext;
/*    */ import io.rong.imkit.RongIM;
/*    */ import io.rong.imkit.model.ProviderTag;
/*    */ import io.rong.imkit.model.UIMessage;
/*    */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*    */ import io.rong.imkit.widget.ArraysDialogFragment;
/*    */ import io.rong.imkit.widget.ArraysDialogFragment.OnArraysDialogItemListener;
/*    */ import io.rong.imlib.model.Conversation.ConversationType;
/*    */ import io.rong.imlib.model.Conversation.PublicServiceType;
/*    */ import io.rong.imlib.model.MessageContent;
/*    */ import io.rong.imlib.model.PublicServiceProfile;
/*    */ import io.rong.imlib.model.UnknownMessage;
/*    */ import io.rong.imlib.model.UserInfo;
/*    */ 
/*    */ @ProviderTag(messageContent=UnknownMessage.class, showPortrait=false, showWarning=false, centerInHorizontal=true, showSummaryWithName=false)
/*    */ public class UnknownMessageItemProvider extends IContainerItemProvider.MessageProvider<MessageContent>
/*    */ {
/*    */   public void bindView(View v, int position, MessageContent content, UIMessage message)
/*    */   {
/* 32 */     ViewHolder viewHolder = (ViewHolder)v.getTag();
/* 33 */     viewHolder.contentTextView.setText(R.string.rc_message_unknown);
/*    */   }
/*    */ 
/*    */   public Spannable getContentSummary(MessageContent data)
/*    */   {
/* 38 */     return new SpannableString(RongContext.getInstance().getResources().getString(R.string.rc_message_unknown));
/*    */   }
/*    */ 
/*    */   public void onItemClick(View view, int position, MessageContent content, UIMessage message)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void onItemLongClick(View view, int position, MessageContent content, UIMessage message)
/*    */   {
/* 48 */     String name = null;
/* 49 */     if ((message.getConversationType().getName().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName())) || (message.getConversationType().getName().equals(Conversation.ConversationType.PUBLIC_SERVICE.getName())))
/*    */     {
/* 51 */       if (message.getUserInfo() != null) {
/* 52 */         name = message.getUserInfo().getName();
/*    */       } else {
/* 54 */         Conversation.PublicServiceType publicServiceType = Conversation.PublicServiceType.setValue(message.getConversationType().getValue());
/* 55 */         PublicServiceProfile info = RongUserInfoManager.getInstance().getPublicServiceProfile(publicServiceType, message.getTargetId());
/*    */ 
/* 57 */         if (info != null)
/* 58 */           name = info.getName();
/*    */       }
/*    */     } else {
/* 61 */       UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
/* 62 */       if (userInfo != null) {
/* 63 */         name = userInfo.getName();
/*    */       }
/*    */     }
/*    */ 
/* 67 */     String[] items = { view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete) };
/*    */ 
/* 69 */     ArraysDialogFragment.newInstance(name, items).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener(message)
/*    */     {
/*    */       public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
/* 72 */         if (which == 0)
/* 73 */           RongIM.getInstance().deleteMessages(new int[] { this.val$message.getMessageId() }, null);
/*    */       }
/*    */     }).show(((FragmentActivity)view.getContext()).getSupportFragmentManager());
/*    */   }
/*    */ 
/*    */   public View newView(Context context, ViewGroup group)
/*    */   {
/* 81 */     View view = LayoutInflater.from(context).inflate(R.layout.rc_item_information_notification_message, null);
/* 82 */     ViewHolder viewHolder = new ViewHolder();
/* 83 */     viewHolder.contentTextView = ((TextView)view.findViewById(R.id.rc_msg));
/* 84 */     viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
/* 85 */     view.setTag(viewHolder);
/*    */ 
/* 87 */     return view;
/*    */   }
/*    */ 
/*    */   class ViewHolder
/*    */   {
/*    */     TextView contentTextView;
/*    */ 
/*    */     ViewHolder()
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.UnknownMessageItemProvider
 * JD-Core Version:    0.6.0
 */
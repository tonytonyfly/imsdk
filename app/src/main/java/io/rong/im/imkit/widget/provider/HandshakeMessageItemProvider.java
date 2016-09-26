/*    */ package io.rong.imkit.widget.provider;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.content.DialogInterface;
/*    */ import android.content.res.Resources;
/*    */ import android.support.v4.app.FragmentActivity;
/*    */ import android.text.Spannable;
/*    */ import android.text.SpannableString;
/*    */ import android.view.View;
/*    */ import android.view.ViewGroup;
/*    */ import io.rong.imkit.R.string;
/*    */ import io.rong.imkit.RongIM;
/*    */ import io.rong.imkit.model.ProviderTag;
/*    */ import io.rong.imkit.model.UIMessage;
/*    */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*    */ import io.rong.imkit.utils.AndroidEmoji;
/*    */ import io.rong.imkit.widget.ArraysDialogFragment;
/*    */ import io.rong.imkit.widget.ArraysDialogFragment.OnArraysDialogItemListener;
/*    */ import io.rong.imlib.model.Conversation.ConversationType;
/*    */ import io.rong.imlib.model.Conversation.PublicServiceType;
/*    */ import io.rong.imlib.model.PublicServiceProfile;
/*    */ import io.rong.imlib.model.UserInfo;
/*    */ import io.rong.message.HandshakeMessage;
/*    */ 
/*    */ @ProviderTag(messageContent=HandshakeMessage.class, showPortrait=false, centerInHorizontal=true, hide=true)
/*    */ public class HandshakeMessageItemProvider extends IContainerItemProvider.MessageProvider<HandshakeMessage>
/*    */ {
/*    */   public View newView(Context context, ViewGroup group)
/*    */   {
/* 29 */     return null;
/*    */   }
/*    */ 
/*    */   public Spannable getContentSummary(HandshakeMessage data)
/*    */   {
/* 34 */     if ((data != null) && (data.getContent() != null))
/* 35 */       return new SpannableString(AndroidEmoji.ensure(data.getContent()));
/* 36 */     return null;
/*    */   }
/*    */ 
/*    */   public void onItemClick(View view, int position, HandshakeMessage content, UIMessage message)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void onItemLongClick(View view, int position, HandshakeMessage content, UIMessage message)
/*    */   {
/* 46 */     String name = null;
/* 47 */     if ((message.getConversationType().getName().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName())) || (message.getConversationType().getName().equals(Conversation.ConversationType.PUBLIC_SERVICE.getName())))
/*    */     {
/* 49 */       if (message.getUserInfo() != null) {
/* 50 */         name = message.getUserInfo().getName();
/*    */       } else {
/* 52 */         Conversation.PublicServiceType publicServiceType = Conversation.PublicServiceType.setValue(message.getConversationType().getValue());
/* 53 */         PublicServiceProfile info = RongUserInfoManager.getInstance().getPublicServiceProfile(publicServiceType, message.getTargetId());
/* 54 */         if (info != null)
/* 55 */           name = info.getName();
/*    */       }
/*    */     } else {
/* 58 */       UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
/* 59 */       if (userInfo != null) {
/* 60 */         name = userInfo.getName();
/*    */       }
/*    */     }
/*    */ 
/* 64 */     String[] items = { view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete) };
/*    */ 
/* 66 */     ArraysDialogFragment.newInstance(name, items).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener(message)
/*    */     {
/*    */       public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
/* 69 */         if (which == 0)
/* 70 */           RongIM.getInstance().deleteMessages(new int[] { this.val$message.getMessageId() }, null);
/*    */       }
/*    */     }).show(((FragmentActivity)view.getContext()).getSupportFragmentManager());
/*    */   }
/*    */ 
/*    */   public void bindView(View v, int position, HandshakeMessage content, UIMessage data)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.HandshakeMessageItemProvider
 * JD-Core Version:    0.6.0
 */
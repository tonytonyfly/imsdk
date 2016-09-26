/*    */ package io.rong.imkit.widget.provider;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.text.Spannable;
/*    */ import android.text.SpannableString;
/*    */ import android.text.TextUtils;
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
/*    */ import io.rong.imlib.model.UserInfo;
/*    */ import io.rong.message.RecallNotificationMessage;
/*    */ 
/*    */ @ProviderTag(messageContent=RecallNotificationMessage.class, showPortrait=false, showProgress=false, showWarning=false, centerInHorizontal=true, showSummaryWithName=false)
/*    */ public class RecallMessageItemProvider extends IContainerItemProvider.MessageProvider<RecallNotificationMessage>
/*    */ {
/*    */   public void onItemClick(View view, int position, RecallNotificationMessage content, UIMessage message)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void bindView(View v, int position, RecallNotificationMessage content, UIMessage message)
/*    */   {
/* 32 */     ViewHolder viewHolder = (ViewHolder)v.getTag();
/*    */ 
/* 34 */     if ((content != null) && (message != null))
/*    */     {
/*    */       String information;
/*    */       String information;
/* 36 */       if (content.getOperatorId().equals(RongIM.getInstance().getCurrentUserId())) {
/* 37 */         information = RongContext.getInstance().getString(R.string.rc_you_recalled_a_message);
/*    */       } else {
/* 39 */         UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(content.getOperatorId());
/*    */         String information;
/* 40 */         if ((userInfo != null) && (userInfo.getName() != null))
/* 41 */           information = userInfo.getName() + RongContext.getInstance().getString(R.string.rc_recalled_a_message);
/*    */         else {
/* 43 */           information = content.getOperatorId() + RongContext.getInstance().getString(R.string.rc_recalled_a_message);
/*    */         }
/*    */       }
/* 46 */       viewHolder.contentTextView.setText(information);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void onItemLongClick(View view, int position, RecallNotificationMessage content, UIMessage message)
/*    */   {
/*    */   }
/*    */ 
/*    */   public Spannable getContentSummary(RecallNotificationMessage data)
/*    */   {
/* 57 */     if ((data != null) && (!TextUtils.isEmpty(data.getOperatorId())))
/*    */     {
/*    */       String information;
/*    */       String information;
/* 60 */       if (data.getOperatorId().equals(RongIM.getInstance().getCurrentUserId())) {
/* 61 */         information = RongContext.getInstance().getString(R.string.rc_you_recalled_a_message);
/*    */       } else {
/* 63 */         UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getOperatorId());
/*    */         String information;
/* 64 */         if ((userInfo != null) && (userInfo.getName() != null))
/* 65 */           information = userInfo.getName() + RongContext.getInstance().getString(R.string.rc_recalled_a_message);
/*    */         else {
/* 67 */           information = data.getOperatorId() + RongContext.getInstance().getString(R.string.rc_recalled_a_message);
/*    */         }
/*    */       }
/* 70 */       return new SpannableString(information);
/*    */     }
/* 72 */     return null;
/*    */   }
/*    */ 
/*    */   public View newView(Context context, ViewGroup group)
/*    */   {
/* 77 */     View view = LayoutInflater.from(context).inflate(R.layout.rc_item_information_notification_message, null);
/* 78 */     ViewHolder viewHolder = new ViewHolder();
/* 79 */     viewHolder.contentTextView = ((TextView)view.findViewById(R.id.rc_msg));
/* 80 */     viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
/* 81 */     view.setTag(viewHolder);
/*    */ 
/* 83 */     return view;
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
 * Qualified Name:     io.rong.imkit.widget.provider.RecallMessageItemProvider
 * JD-Core Version:    0.6.0
 */
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
/*    */ import io.rong.imkit.model.ProviderTag;
/*    */ import io.rong.imkit.model.UIMessage;
/*    */ import io.rong.message.InformationNotificationMessage;
/*    */ 
/*    */ @ProviderTag(messageContent=InformationNotificationMessage.class, showPortrait=false, showProgress=false, showWarning=false, centerInHorizontal=true, showSummaryWithName=false)
/*    */ public class InfoNotificationMsgItemProvider extends IContainerItemProvider.MessageProvider<InformationNotificationMessage>
/*    */ {
/*    */   public void bindView(View v, int position, InformationNotificationMessage content, UIMessage message)
/*    */   {
/* 36 */     ViewHolder viewHolder = (ViewHolder)v.getTag();
/*    */ 
/* 38 */     if ((content != null) && 
/* 39 */       (!TextUtils.isEmpty(content.getMessage())))
/* 40 */       viewHolder.contentTextView.setText(content.getMessage());
/*    */   }
/*    */ 
/*    */   public Spannable getContentSummary(InformationNotificationMessage data)
/*    */   {
/* 47 */     if ((data != null) && (!TextUtils.isEmpty(data.getMessage())))
/* 48 */       return new SpannableString(data.getMessage());
/* 49 */     return null;
/*    */   }
/*    */ 
/*    */   public void onItemClick(View view, int position, InformationNotificationMessage content, UIMessage message)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void onItemLongClick(View view, int position, InformationNotificationMessage content, UIMessage message)
/*    */   {
/*    */   }
/*    */ 
/*    */   public View newView(Context context, ViewGroup group)
/*    */   {
/* 89 */     View view = LayoutInflater.from(context).inflate(R.layout.rc_item_information_notification_message, null);
/* 90 */     ViewHolder viewHolder = new ViewHolder();
/* 91 */     viewHolder.contentTextView = ((TextView)view.findViewById(R.id.rc_msg));
/* 92 */     viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
/* 93 */     view.setTag(viewHolder);
/*    */ 
/* 95 */     return view;
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
 * Qualified Name:     io.rong.imkit.widget.provider.InfoNotificationMsgItemProvider
 * JD-Core Version:    0.6.0
 */
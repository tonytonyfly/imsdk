/*     */ package io.rong.imkit.widget.provider;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.res.Resources;
/*     */ import android.content.res.Resources.NotFoundException;
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.BitmapFactory;
/*     */ import android.graphics.drawable.Drawable;
/*     */ import android.net.Uri;
/*     */ import android.text.Spannable;
/*     */ import android.text.SpannableString;
/*     */ import android.text.SpannableStringBuilder;
/*     */ import android.text.TextUtils;
/*     */ import android.text.style.ForegroundColorSpan;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.TextView;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imkit.R.bool;
/*     */ import io.rong.imkit.R.color;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.model.ConversationKey;
/*     */ import io.rong.imkit.model.ConversationProviderTag;
/*     */ import io.rong.imkit.model.ProviderTag;
/*     */ import io.rong.imkit.model.UIConversation;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.utils.AndroidEmoji;
/*     */ import io.rong.imkit.utils.RongDateUtils;
/*     */ import io.rong.imlib.model.Conversation.ConversationNotificationStatus;
/*     */ import io.rong.imlib.model.Message.SentStatus;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ 
/*     */ @ConversationProviderTag(conversationType="private", portraitPosition=1)
/*     */ public class PrivateConversationProvider
/*     */   implements IContainerItemProvider.ConversationProvider<UIConversation>
/*     */ {
/*     */   private static final String TAG = "PrivateConversationProvider";
/*     */ 
/*     */   public View newView(Context context, ViewGroup viewGroup)
/*     */   {
/*  49 */     View result = LayoutInflater.from(context).inflate(R.layout.rc_item_base_conversation, null);
/*     */ 
/*  51 */     ViewHolder holder = new ViewHolder();
/*  52 */     holder.title = ((TextView)result.findViewById(R.id.rc_conversation_title));
/*  53 */     holder.time = ((TextView)result.findViewById(R.id.rc_conversation_time));
/*  54 */     holder.content = ((TextView)result.findViewById(R.id.rc_conversation_content));
/*  55 */     holder.notificationBlockImage = ((ImageView)result.findViewById(R.id.rc_conversation_msg_block));
/*  56 */     holder.readStatus = ((ImageView)result.findViewById(R.id.rc_conversation_status));
/*  57 */     result.setTag(holder);
/*     */ 
/*  59 */     return result;
/*     */   }
/*     */ 
/*     */   public void bindView(View view, int position, UIConversation data) {
/*  63 */     ViewHolder holder = (ViewHolder)view.getTag();
/*  64 */     ProviderTag tag = null;
/*  65 */     if (data == null) {
/*  66 */       holder.title.setText(null);
/*  67 */       holder.time.setText(null);
/*  68 */       holder.content.setText(null);
/*     */     }
/*     */     else {
/*  71 */       holder.title.setText(data.getUIConversationTitle());
/*     */ 
/*  73 */       String time = RongDateUtils.getConversationListFormatDate(data.getUIConversationTime(), view.getContext());
/*  74 */       holder.time.setText(time);
/*     */ 
/*  77 */       if ((!TextUtils.isEmpty(data.getDraft())) || (data.getMentionedFlag())) {
/*  78 */         SpannableStringBuilder builder = new SpannableStringBuilder();
/*     */ 
/*  80 */         if (data.getMentionedFlag()) {
/*  81 */           SpannableString string = new SpannableString(view.getContext().getString(R.string.rc_message_content_mentioned));
/*  82 */           string.setSpan(new ForegroundColorSpan(view.getContext().getResources().getColor(R.color.rc_mentioned_color)), 0, string.length(), 33);
/*  83 */           builder.append(string).append(" ").append(data.getConversationContent());
/*     */         } else {
/*  85 */           SpannableString string = new SpannableString(view.getContext().getString(R.string.rc_message_content_draft));
/*  86 */           string.setSpan(new ForegroundColorSpan(view.getContext().getResources().getColor(R.color.rc_draft_color)), 0, string.length(), 33);
/*  87 */           builder.append(string).append(" ").append(data.getDraft());
/*     */         }
/*     */ 
/*  90 */         AndroidEmoji.ensure(builder);
/*     */ 
/*  92 */         holder.content.setText(builder);
/*  93 */         holder.readStatus.setVisibility(8);
/*     */       }
/*     */       else
/*     */       {
/*  98 */         boolean readRec = false;
/*     */         try {
/* 100 */           readRec = view.getResources().getBoolean(R.bool.rc_read_receipt);
/*     */         } catch (Resources.NotFoundException e) {
/* 102 */           RLog.e("PrivateConversationProvider", "rc_read_receipt not configure in rc_config.xml");
/* 103 */           e.printStackTrace();
/*     */         }
/*     */ 
/* 106 */         if (readRec) {
/* 107 */           if ((data.getSentStatus() == Message.SentStatus.READ) && (data.getConversationSenderId().equals(RongIM.getInstance().getCurrentUserId())))
/*     */           {
/* 109 */             holder.readStatus.setVisibility(0);
/*     */           }
/* 111 */           else holder.readStatus.setVisibility(8);
/*     */         }
/*     */ 
/* 114 */         holder.content.setText(data.getConversationContent());
/*     */       }
/*     */ 
/* 117 */       if ((RongContext.getInstance() != null) && (data.getMessageContent() != null)) {
/* 118 */         tag = RongContext.getInstance().getMessageProviderTag(data.getMessageContent().getClass());
/*     */       }
/* 120 */       if ((data.getSentStatus() != null) && ((data.getSentStatus() == Message.SentStatus.FAILED) || (data.getSentStatus() == Message.SentStatus.SENDING)) && (tag != null) && (tag.showWarning() == true) && (data.getConversationSenderId() != null) && (data.getConversationSenderId().equals(RongIM.getInstance().getCurrentUserId())))
/*     */       {
/* 123 */         Bitmap bitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.rc_conversation_list_msg_send_failure);
/* 124 */         int width = bitmap.getWidth();
/* 125 */         Drawable drawable = null;
/* 126 */         if ((data.getSentStatus() == Message.SentStatus.FAILED) && (TextUtils.isEmpty(data.getDraft())))
/* 127 */           drawable = view.getContext().getResources().getDrawable(R.drawable.rc_conversation_list_msg_send_failure);
/* 128 */         else if ((data.getSentStatus() == Message.SentStatus.SENDING) && (TextUtils.isEmpty(data.getDraft())))
/* 129 */           drawable = view.getContext().getResources().getDrawable(R.drawable.rc_conversation_list_msg_sending);
/* 130 */         if (drawable != null) {
/* 131 */           drawable.setBounds(0, 0, width, width);
/* 132 */           holder.content.setCompoundDrawablePadding(10);
/* 133 */           holder.content.setCompoundDrawables(drawable, null, null, null);
/*     */         }
/*     */       } else {
/* 136 */         holder.content.setCompoundDrawables(null, null, null, null);
/*     */       }
/*     */ 
/* 139 */       ConversationKey key = ConversationKey.obtain(data.getConversationTargetId(), data.getConversationType());
/* 140 */       Conversation.ConversationNotificationStatus status = RongContext.getInstance().getConversationNotifyStatusFromCache(key);
/* 141 */       if ((status != null) && (status.equals(Conversation.ConversationNotificationStatus.DO_NOT_DISTURB)))
/* 142 */         holder.notificationBlockImage.setVisibility(0);
/*     */       else
/* 144 */         holder.notificationBlockImage.setVisibility(8);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Spannable getSummary(UIConversation data)
/*     */   {
/* 150 */     return null;
/*     */   }
/*     */ 
/*     */   public String getTitle(String userId)
/*     */   {
/*     */     String name;
/*     */     String name;
/* 155 */     if (RongUserInfoManager.getInstance().getUserInfo(userId) == null)
/* 156 */       name = userId;
/*     */     else {
/* 158 */       name = RongUserInfoManager.getInstance().getUserInfo(userId).getName();
/*     */     }
/* 160 */     return name;
/*     */   }
/*     */ 
/*     */   public Uri getPortraitUri(String id)
/*     */   {
/*     */     Uri uri;
/*     */     Uri uri;
/* 166 */     if (RongUserInfoManager.getInstance().getUserInfo(id) == null)
/* 167 */       uri = null;
/*     */     else {
/* 169 */       uri = RongUserInfoManager.getInstance().getUserInfo(id).getPortraitUri();
/*     */     }
/* 171 */     return uri;
/*     */   }
/*     */ 
/*     */   class ViewHolder
/*     */   {
/*     */     TextView title;
/*     */     TextView time;
/*     */     TextView content;
/*     */     ImageView notificationBlockImage;
/*     */     ImageView readStatus;
/*     */ 
/*     */     ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.PrivateConversationProvider
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.imkit.widget.adapter;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.res.Resources;
/*     */ import android.net.Uri;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.TextView;
/*     */ import io.rong.imkit.R.color;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.model.ConversationProviderTag;
/*     */ import io.rong.imkit.model.UIConversation;
/*     */ import io.rong.imkit.model.UIConversation.UnreadRemindType;
/*     */ import io.rong.imkit.widget.AsyncImageView;
/*     */ import io.rong.imkit.widget.ProviderContainerView;
/*     */ import io.rong.imkit.widget.provider.IContainerItemProvider;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ 
/*     */ public class SubConversationListAdapter extends BaseAdapter<UIConversation>
/*     */ {
/*     */   LayoutInflater mInflater;
/*     */   Context mContext;
/*     */ 
/*     */   public long getItemId(int position)
/*     */   {
/*  26 */     UIConversation conversation = (UIConversation)getItem(position);
/*  27 */     if (conversation == null)
/*  28 */       return 0L;
/*  29 */     return conversation.hashCode();
/*     */   }
/*     */ 
/*     */   public SubConversationListAdapter(Context context)
/*     */   {
/*  47 */     this.mContext = context;
/*  48 */     this.mInflater = LayoutInflater.from(this.mContext);
/*     */   }
/*     */ 
/*     */   public int findPosition(Conversation.ConversationType type, String targetId) {
/*  52 */     int index = getCount();
/*  53 */     int position = -1;
/*     */ 
/*  55 */     while (index-- > 0) {
/*  56 */       if ((!((UIConversation)getItem(index)).getConversationType().equals(type)) || (!((UIConversation)getItem(index)).getConversationTargetId().equals(targetId)))
/*     */         continue;
/*  58 */       position = index;
/*     */     }
/*     */ 
/*  63 */     return position;
/*     */   }
/*     */ 
/*     */   protected View newView(Context context, int position, ViewGroup group)
/*     */   {
/*  68 */     View result = this.mInflater.inflate(R.layout.rc_item_conversation, group, false);
/*     */ 
/*  70 */     ViewHolder holder = new ViewHolder();
/*  71 */     holder.layout = findViewById(result, R.id.rc_item_conversation);
/*  72 */     holder.leftImageLayout = findViewById(result, R.id.rc_item1);
/*  73 */     holder.rightImageLayout = findViewById(result, R.id.rc_item2);
/*  74 */     holder.leftImageView = ((AsyncImageView)findViewById(result, R.id.rc_left));
/*  75 */     holder.rightImageView = ((AsyncImageView)findViewById(result, R.id.rc_right));
/*  76 */     holder.contentView = ((ProviderContainerView)findViewById(result, R.id.rc_content));
/*  77 */     holder.unReadMsgCount = ((TextView)findViewById(result, R.id.rc_unread_message));
/*  78 */     holder.unReadMsgCountRight = ((TextView)findViewById(result, R.id.rc_unread_message_right));
/*  79 */     holder.unReadMsgCountIcon = ((ImageView)findViewById(result, R.id.rc_unread_message_icon));
/*  80 */     holder.unReadMsgCountRightIcon = ((ImageView)findViewById(result, R.id.rc_unread_message_icon_right));
/*     */ 
/*  82 */     result.setTag(holder);
/*     */ 
/*  84 */     return result;
/*     */   }
/*     */ 
/*     */   protected void bindView(View v, int position, UIConversation data)
/*     */   {
/*  89 */     ViewHolder holder = (ViewHolder)v.getTag();
/*     */ 
/*  92 */     IContainerItemProvider provider = RongContext.getInstance().getConversationTemplate(data.getConversationType().getName());
/*     */ 
/*  94 */     View view = holder.contentView.inflate(provider);
/*     */ 
/*  96 */     provider.bindView(view, position, data);
/*     */ 
/*  99 */     if (data.isTop())
/* 100 */       holder.layout.setBackgroundColor(this.mContext.getResources().getColor(R.color.rc_conversation_top_bg));
/*     */     else {
/* 102 */       holder.layout.setBackgroundColor(this.mContext.getResources().getColor(R.color.rc_text_color_primary_inverse));
/*     */     }
/* 104 */     ConversationProviderTag tag = RongContext.getInstance().getConversationProviderTag(data.getConversationType().getName());
/*     */ 
/* 107 */     int defaultId = 0;
/* 108 */     if (tag.portraitPosition() == 1) {
/* 109 */       holder.leftImageLayout.setVisibility(0);
/*     */ 
/* 111 */       if (data.getConversationType() == Conversation.ConversationType.GROUP)
/* 112 */         defaultId = R.drawable.rc_default_group_portrait;
/* 113 */       else if (data.getConversationType() == Conversation.ConversationType.DISCUSSION)
/* 114 */         defaultId = R.drawable.rc_default_discussion_portrait;
/*     */       else {
/* 116 */         defaultId = R.drawable.rc_default_portrait;
/*     */       }
/*     */ 
/* 119 */       if (data.getIconUrl() != null)
/* 120 */         holder.leftImageView.setAvatar(data.getIconUrl().toString(), defaultId);
/*     */       else {
/* 122 */         holder.leftImageView.setAvatar(null, defaultId);
/*     */       }
/*     */ 
/* 125 */       if (data.getUnReadMessageCount() > 0) {
/* 126 */         holder.unReadMsgCountIcon.setVisibility(0);
/* 127 */         if (data.getUnReadType().equals(UIConversation.UnreadRemindType.REMIND_WITH_COUNTING)) {
/* 128 */           holder.unReadMsgCount.setVisibility(0);
/* 129 */           if (data.getUnReadMessageCount() > 99)
/* 130 */             holder.unReadMsgCount.setText(this.mContext.getResources().getString(R.string.rc_message_unread_count));
/*     */           else {
/* 132 */             holder.unReadMsgCount.setText(Integer.toString(data.getUnReadMessageCount()));
/*     */           }
/* 134 */           holder.unReadMsgCountIcon.setImageResource(R.drawable.rc_unread_count_bg);
/*     */         } else {
/* 136 */           holder.unReadMsgCount.setVisibility(8);
/* 137 */           holder.unReadMsgCountIcon.setImageResource(R.drawable.rc_unread_remind_without_count);
/*     */         }
/*     */       } else {
/* 140 */         holder.unReadMsgCountIcon.setVisibility(8);
/* 141 */         holder.unReadMsgCount.setVisibility(8);
/*     */       }
/* 143 */       holder.rightImageLayout.setVisibility(8);
/* 144 */     } else if (tag.portraitPosition() == 2) {
/* 145 */       holder.rightImageLayout.setVisibility(0);
/*     */ 
/* 147 */       if (data.getConversationType() == Conversation.ConversationType.GROUP)
/* 148 */         defaultId = R.drawable.rc_default_group_portrait;
/* 149 */       else if (data.getConversationType() == Conversation.ConversationType.DISCUSSION)
/* 150 */         defaultId = R.drawable.rc_default_discussion_portrait;
/*     */       else {
/* 152 */         defaultId = R.drawable.rc_default_portrait;
/*     */       }
/*     */ 
/* 155 */       if (data.getIconUrl() != null)
/* 156 */         holder.rightImageView.setAvatar(data.getIconUrl().toString(), defaultId);
/*     */       else {
/* 158 */         holder.rightImageView.setAvatar(null, defaultId);
/*     */       }
/*     */ 
/* 161 */       if (data.getUnReadMessageCount() > 0) {
/* 162 */         holder.unReadMsgCountRight.setVisibility(0);
/* 163 */         holder.unReadMsgCountIcon.setVisibility(0);
/* 164 */         if (data.getUnReadType().equals(UIConversation.UnreadRemindType.REMIND_WITH_COUNTING)) {
/* 165 */           if (data.getUnReadMessageCount() > 99)
/* 166 */             holder.unReadMsgCountRight.setText(this.mContext.getResources().getString(R.string.rc_message_unread_count));
/*     */           else {
/* 168 */             holder.unReadMsgCountRight.setText(Integer.toString(data.getUnReadMessageCount()));
/*     */           }
/* 170 */           holder.unReadMsgCountIcon.setImageResource(R.drawable.rc_unread_count_bg);
/*     */         } else {
/* 172 */           holder.unReadMsgCountIcon.setImageResource(R.drawable.rc_unread_remind_without_count);
/*     */         }
/*     */       }
/*     */ 
/* 176 */       holder.leftImageLayout.setVisibility(8);
/* 177 */     } else if (tag.portraitPosition() == 3) {
/* 178 */       holder.rightImageLayout.setVisibility(8);
/* 179 */       holder.leftImageLayout.setVisibility(8);
/*     */     } else {
/* 181 */       throw new IllegalArgumentException("the portrait position is wrong!");
/*     */     }
/*     */   }
/*     */ 
/*     */   class ViewHolder
/*     */   {
/*     */     View layout;
/*     */     View leftImageLayout;
/*     */     View rightImageLayout;
/*     */     AsyncImageView leftImageView;
/*     */     AsyncImageView rightImageView;
/*     */     ProviderContainerView contentView;
/*     */     TextView unReadMsgCount;
/*     */     TextView unReadMsgCountRight;
/*     */     ImageView unReadMsgCountRightIcon;
/*     */     ImageView unReadMsgCountIcon;
/*     */ 
/*     */     ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.adapter.SubConversationListAdapter
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.imkit.widget.adapter;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.res.Resources;
/*     */ import android.net.Uri;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.View.OnLongClickListener;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.TextView;
/*     */ import io.rong.common.RLog;
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
/*     */ public class ConversationListAdapter extends BaseAdapter<UIConversation>
/*     */ {
/*     */   private static final String TAG = "ConversationListAdapter";
/*     */   LayoutInflater mInflater;
/*     */   Context mContext;
/*     */   private OnPortraitItemClick mOnPortraitItemClick;
/*     */ 
/*     */   public long getItemId(int position)
/*     */   {
/*  27 */     UIConversation conversation = (UIConversation)getItem(position);
/*  28 */     if (conversation == null)
/*  29 */       return 0L;
/*  30 */     return conversation.hashCode();
/*     */   }
/*     */ 
/*     */   public ConversationListAdapter(Context context)
/*     */   {
/*  49 */     this.mContext = context;
/*  50 */     this.mInflater = LayoutInflater.from(this.mContext);
/*     */   }
/*     */ 
/*     */   public int findGatherPosition(Conversation.ConversationType type)
/*     */   {
/*  55 */     int index = getCount();
/*  56 */     int position = -1;
/*     */ 
/*  58 */     if (RongContext.getInstance().getConversationGatherState(type.getName()).booleanValue())
/*     */     {
/*  60 */       while (index-- > 0) {
/*  61 */         if (((UIConversation)getItem(index)).getConversationType().equals(type)) {
/*  62 */           position = index;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  68 */     return position;
/*     */   }
/*     */ 
/*     */   public int findPosition(Conversation.ConversationType type, String targetId) {
/*  72 */     int index = getCount();
/*  73 */     int position = -1;
/*  74 */     if (RongContext.getInstance().getConversationGatherState(type.getName()).booleanValue()) {
/*     */       do if (index-- <= 0)
/*     */           break; while (!((UIConversation)getItem(index)).getConversationType().equals(type));
/*  77 */       position = index;
/*     */     }
/*     */     else
/*     */     {
/*  82 */       while (index-- > 0) {
/*  83 */         if ((!((UIConversation)getItem(index)).getConversationType().equals(type)) || (!((UIConversation)getItem(index)).getConversationTargetId().equals(targetId)))
/*     */           continue;
/*  85 */         position = index;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  90 */     return position;
/*     */   }
/*     */ 
/*     */   protected View newView(Context context, int position, ViewGroup group)
/*     */   {
/*  95 */     View result = this.mInflater.inflate(R.layout.rc_item_conversation, null);
/*     */ 
/*  97 */     ViewHolder holder = new ViewHolder();
/*  98 */     holder.layout = findViewById(result, R.id.rc_item_conversation);
/*  99 */     holder.leftImageLayout = findViewById(result, R.id.rc_item1);
/* 100 */     holder.rightImageLayout = findViewById(result, R.id.rc_item2);
/* 101 */     holder.leftImageView = ((AsyncImageView)findViewById(result, R.id.rc_left));
/* 102 */     holder.rightImageView = ((AsyncImageView)findViewById(result, R.id.rc_right));
/* 103 */     holder.contentView = ((ProviderContainerView)findViewById(result, R.id.rc_content));
/* 104 */     holder.unReadMsgCount = ((TextView)findViewById(result, R.id.rc_unread_message));
/* 105 */     holder.unReadMsgCountRight = ((TextView)findViewById(result, R.id.rc_unread_message_right));
/* 106 */     holder.unReadMsgCountIcon = ((ImageView)findViewById(result, R.id.rc_unread_message_icon));
/* 107 */     holder.unReadMsgCountRightIcon = ((ImageView)findViewById(result, R.id.rc_unread_message_icon_right));
/* 108 */     result.setTag(holder);
/* 109 */     return result;
/*     */   }
/*     */ 
/*     */   protected void bindView(View v, int position, UIConversation data)
/*     */   {
/* 114 */     ViewHolder holder = (ViewHolder)v.getTag();
/*     */ 
/* 116 */     if (data == null) {
/* 117 */       return;
/*     */     }
/*     */ 
/* 120 */     IContainerItemProvider provider = RongContext.getInstance().getConversationTemplate(data.getConversationType().getName());
/* 121 */     if (provider == null) {
/* 122 */       RLog.e("ConversationListAdapter", "provider is null");
/* 123 */       return;
/*     */     }
/*     */ 
/* 126 */     View view = holder.contentView.inflate(provider);
/* 127 */     provider.bindView(view, position, data);
/*     */ 
/* 130 */     if (data.isTop())
/* 131 */       holder.layout.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.drawable.rc_item_top_list_selector));
/*     */     else {
/* 133 */       holder.layout.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.drawable.rc_item_list_selector));
/*     */     }
/*     */ 
/* 136 */     ConversationProviderTag tag = RongContext.getInstance().getConversationProviderTag(data.getConversationType().getName());
/*     */ 
/* 139 */     int defaultId = 0;
/* 140 */     if (tag.portraitPosition() == 1) {
/* 141 */       holder.leftImageLayout.setVisibility(0);
/*     */ 
/* 143 */       if (data.getConversationType().equals(Conversation.ConversationType.GROUP))
/* 144 */         defaultId = R.drawable.rc_default_group_portrait;
/* 145 */       else if (data.getConversationType().equals(Conversation.ConversationType.DISCUSSION))
/* 146 */         defaultId = R.drawable.rc_default_discussion_portrait;
/*     */       else {
/* 148 */         defaultId = R.drawable.rc_default_portrait;
/*     */       }
/* 150 */       holder.leftImageLayout.setOnClickListener(new View.OnClickListener(data)
/*     */       {
/*     */         public void onClick(View v) {
/* 153 */           if (ConversationListAdapter.this.mOnPortraitItemClick != null)
/* 154 */             ConversationListAdapter.this.mOnPortraitItemClick.onPortraitItemClick(v, this.val$data);
/*     */         }
/*     */       });
/* 157 */       holder.leftImageLayout.setOnLongClickListener(new View.OnLongClickListener(data)
/*     */       {
/*     */         public boolean onLongClick(View v) {
/* 160 */           if (ConversationListAdapter.this.mOnPortraitItemClick != null)
/* 161 */             ConversationListAdapter.this.mOnPortraitItemClick.onPortraitItemLongClick(v, this.val$data);
/* 162 */           return true;
/*     */         }
/*     */       });
/* 165 */       if (data.getConversationGatherState()) {
/* 166 */         holder.leftImageView.setAvatar(null, defaultId);
/*     */       }
/* 168 */       else if (data.getIconUrl() != null)
/* 169 */         holder.leftImageView.setAvatar(data.getIconUrl().toString(), defaultId);
/*     */       else {
/* 171 */         holder.leftImageView.setAvatar(null, defaultId);
/*     */       }
/*     */ 
/* 175 */       if (data.getUnReadMessageCount() > 0) {
/* 176 */         holder.unReadMsgCountIcon.setVisibility(0);
/* 177 */         if (data.getUnReadType().equals(UIConversation.UnreadRemindType.REMIND_WITH_COUNTING)) {
/* 178 */           if (data.getUnReadMessageCount() > 99)
/* 179 */             holder.unReadMsgCount.setText(this.mContext.getResources().getString(R.string.rc_message_unread_count));
/*     */           else {
/* 181 */             holder.unReadMsgCount.setText(Integer.toString(data.getUnReadMessageCount()));
/*     */           }
/* 183 */           holder.unReadMsgCount.setVisibility(0);
/* 184 */           holder.unReadMsgCountIcon.setImageResource(R.drawable.rc_unread_count_bg);
/*     */         } else {
/* 186 */           holder.unReadMsgCount.setVisibility(8);
/* 187 */           holder.unReadMsgCountIcon.setImageResource(R.drawable.rc_unread_remind_list_count);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 192 */         holder.unReadMsgCountIcon.setVisibility(8);
/* 193 */         holder.unReadMsgCount.setVisibility(8);
/*     */       }
/*     */ 
/* 196 */       holder.rightImageLayout.setVisibility(8);
/* 197 */     } else if (tag.portraitPosition() == 2) {
/* 198 */       holder.rightImageLayout.setVisibility(0);
/*     */ 
/* 200 */       holder.rightImageLayout.setOnClickListener(new View.OnClickListener(data)
/*     */       {
/*     */         public void onClick(View v) {
/* 203 */           if (ConversationListAdapter.this.mOnPortraitItemClick != null)
/* 204 */             ConversationListAdapter.this.mOnPortraitItemClick.onPortraitItemClick(v, this.val$data);
/*     */         }
/*     */       });
/* 207 */       holder.rightImageLayout.setOnLongClickListener(new View.OnLongClickListener(data)
/*     */       {
/*     */         public boolean onLongClick(View v) {
/* 210 */           if (ConversationListAdapter.this.mOnPortraitItemClick != null)
/* 211 */             ConversationListAdapter.this.mOnPortraitItemClick.onPortraitItemLongClick(v, this.val$data);
/* 212 */           return true;
/*     */         }
/*     */       });
/* 216 */       if (data.getConversationType().equals(Conversation.ConversationType.GROUP))
/* 217 */         defaultId = R.drawable.rc_default_group_portrait;
/* 218 */       else if (data.getConversationType().equals(Conversation.ConversationType.DISCUSSION))
/* 219 */         defaultId = R.drawable.rc_default_discussion_portrait;
/*     */       else {
/* 221 */         defaultId = R.drawable.rc_default_portrait;
/*     */       }
/*     */ 
/* 224 */       if (data.getConversationGatherState()) {
/* 225 */         holder.rightImageView.setAvatar(null, defaultId);
/*     */       }
/* 227 */       else if (data.getIconUrl() != null)
/* 228 */         holder.rightImageView.setAvatar(data.getIconUrl().toString(), defaultId);
/*     */       else {
/* 230 */         holder.rightImageView.setAvatar(null, defaultId);
/*     */       }
/*     */ 
/* 234 */       if (data.getUnReadMessageCount() > 0) {
/* 235 */         holder.unReadMsgCountRightIcon.setVisibility(0);
/* 236 */         if (data.getUnReadType().equals(UIConversation.UnreadRemindType.REMIND_WITH_COUNTING)) {
/* 237 */           holder.unReadMsgCount.setVisibility(0);
/* 238 */           if (data.getUnReadMessageCount() > 99)
/* 239 */             holder.unReadMsgCountRight.setText(this.mContext.getResources().getString(R.string.rc_message_unread_count));
/*     */           else {
/* 241 */             holder.unReadMsgCountRight.setText(Integer.toString(data.getUnReadMessageCount()));
/*     */           }
/* 243 */           holder.unReadMsgCountRightIcon.setImageResource(R.drawable.rc_unread_count_bg);
/*     */         } else {
/* 245 */           holder.unReadMsgCount.setVisibility(8);
/* 246 */           holder.unReadMsgCountRightIcon.setImageResource(R.drawable.rc_unread_remind_without_count);
/*     */         }
/*     */       } else {
/* 249 */         holder.unReadMsgCountIcon.setVisibility(8);
/* 250 */         holder.unReadMsgCount.setVisibility(8);
/*     */       }
/*     */ 
/* 253 */       holder.leftImageLayout.setVisibility(8);
/* 254 */     } else if (tag.portraitPosition() == 3) {
/* 255 */       holder.rightImageLayout.setVisibility(8);
/* 256 */       holder.leftImageLayout.setVisibility(8);
/*     */     } else {
/* 258 */       throw new IllegalArgumentException("the portrait position is wrong!");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setOnPortraitItemClick(OnPortraitItemClick onPortraitItemClick)
/*     */   {
/* 271 */     this.mOnPortraitItemClick = onPortraitItemClick;
/*     */   }
/*     */ 
/*     */   public static abstract interface OnPortraitItemClick
/*     */   {
/*     */     public abstract void onPortraitItemClick(View paramView, UIConversation paramUIConversation);
/*     */ 
/*     */     public abstract boolean onPortraitItemLongClick(View paramView, UIConversation paramUIConversation);
/*     */   }
/*     */ 
/*     */   class ViewHolder
/*     */   {
/*     */     View layout;
/*     */     View leftImageLayout;
/*     */     View rightImageLayout;
/*     */     AsyncImageView leftImageView;
/*     */     TextView unReadMsgCount;
/*     */     ImageView unReadMsgCountIcon;
/*     */     AsyncImageView rightImageView;
/*     */     TextView unReadMsgCountRight;
/*     */     ImageView unReadMsgCountRightIcon;
/*     */     ProviderContainerView contentView;
/*     */ 
/*     */     ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.adapter.ConversationListAdapter
 * JD-Core Version:    0.6.0
 */
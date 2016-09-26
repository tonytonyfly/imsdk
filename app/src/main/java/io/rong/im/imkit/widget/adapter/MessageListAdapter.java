/*     */ package io.rong.imkit.widget.adapter;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.res.Resources;
/*     */ import android.content.res.Resources.NotFoundException;
/*     */ import android.graphics.drawable.Drawable;
/*     */ import android.net.Uri;
/*     */ import android.text.TextUtils;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.View.OnLongClickListener;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.FrameLayout.LayoutParams;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.ProgressBar;
/*     */ import android.widget.RelativeLayout;
/*     */ import android.widget.TextView;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.R.bool;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM.ConversationBehaviorListener;
/*     */ import io.rong.imkit.mention.IMemberMentionedListener;
/*     */ import io.rong.imkit.model.ConversationKey;
/*     */ import io.rong.imkit.model.Event.InputViewEvent;
/*     */ import io.rong.imkit.model.GroupUserInfo;
/*     */ import io.rong.imkit.model.ProviderTag;
/*     */ import io.rong.imkit.model.UIMessage;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.utils.CommonUtils;
/*     */ import io.rong.imkit.utils.RongDateUtils;
/*     */ import io.rong.imkit.widget.AsyncImageView;
/*     */ import io.rong.imkit.widget.ProviderContainerView;
/*     */ import io.rong.imkit.widget.provider.IContainerItemProvider;
/*     */ import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.OperationCallback;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.imlib.model.Message.MessageDirection;
/*     */ import io.rong.imlib.model.Message.SentStatus;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import io.rong.imlib.model.PublicServiceProfile;
/*     */ import io.rong.imlib.model.ReadReceiptInfo;
/*     */ import io.rong.imlib.model.UnknownMessage;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import io.rong.message.InformationNotificationMessage;
/*     */ import io.rong.message.RecallNotificationMessage;
/*     */ import io.rong.message.TextMessage;
/*     */ import java.util.HashMap;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class MessageListAdapter extends BaseAdapter<UIMessage>
/*     */ {
/*     */   private static final String TAG = "MessageListAdapter";
/*     */   private static final long READ_RECEIPT_REQUEST_INTERVAL = 120L;
/*     */   LayoutInflater mInflater;
/*     */   Context mContext;
/*     */   Drawable mDefaultDrawable;
/*     */   OnItemHandlerListener mOnItemHandlerListener;
/*     */   View subView;
/*  57 */   boolean evaForRobot = false;
/*  58 */   boolean robotMode = true;
/*     */ 
/*  60 */   private boolean timeGone = false;
/*     */ 
/*     */   public MessageListAdapter(Context context)
/*     */   {
/*  80 */     this.mContext = context;
/*  81 */     this.mInflater = LayoutInflater.from(this.mContext);
/*  82 */     this.mDefaultDrawable = context.getResources().getDrawable(R.drawable.rc_ic_def_msg_portrait);
/*     */   }
/*     */ 
/*     */   public void setOnItemHandlerListener(OnItemHandlerListener onItemHandlerListener) {
/*  86 */     this.mOnItemHandlerListener = onItemHandlerListener;
/*     */   }
/*     */ 
/*     */   public long getItemId(int position)
/*     */   {
/*  95 */     Message message = (Message)getItem(position);
/*  96 */     if (message == null)
/*  97 */       return -1L;
/*  98 */     return message.getMessageId();
/*     */   }
/*     */ 
/*     */   protected View newView(Context context, int position, ViewGroup group)
/*     */   {
/* 103 */     View result = this.mInflater.inflate(R.layout.rc_item_message, null);
/*     */ 
/* 105 */     ViewHolder holder = new ViewHolder();
/* 106 */     holder.leftIconView = ((AsyncImageView)findViewById(result, R.id.rc_left));
/* 107 */     holder.rightIconView = ((AsyncImageView)findViewById(result, R.id.rc_right));
/* 108 */     holder.nameView = ((TextView)findViewById(result, R.id.rc_title));
/* 109 */     holder.contentView = ((ProviderContainerView)findViewById(result, R.id.rc_content));
/* 110 */     holder.layout = ((ViewGroup)findViewById(result, R.id.rc_layout));
/* 111 */     holder.progressBar = ((ProgressBar)findViewById(result, R.id.rc_progress));
/* 112 */     holder.warning = ((ImageView)findViewById(result, R.id.rc_warning));
/* 113 */     holder.readReceipt = ((ImageView)findViewById(result, R.id.rc_read_receipt));
/* 114 */     holder.readReceiptRequest = ((ImageView)findViewById(result, R.id.rc_read_receipt_request));
/* 115 */     holder.readReceiptStatus = ((TextView)findViewById(result, R.id.rc_read_receipt_status));
/*     */ 
/* 117 */     holder.time = ((TextView)findViewById(result, R.id.rc_time));
/* 118 */     holder.sentStatus = ((TextView)findViewById(result, R.id.rc_sent_status));
/* 119 */     holder.layoutItem = ((RelativeLayout)findViewById(result, R.id.rc_layout_item_message));
/* 120 */     if (holder.time.getVisibility() == 8)
/* 121 */       this.timeGone = true;
/*     */     else {
/* 123 */       this.timeGone = false;
/*     */     }
/*     */ 
/* 126 */     result.setTag(holder);
/*     */ 
/* 129 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean getNeedEvaluate(UIMessage data) {
/* 133 */     String extra = "";
/* 134 */     String robotEva = "";
/* 135 */     String sid = "";
/* 136 */     if ((data != null) && (data.getConversationType() != null) && (data.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE))) {
/* 137 */       if ((data.getContent() instanceof TextMessage)) {
/* 138 */         extra = ((TextMessage)data.getContent()).getExtra();
/* 139 */         if (TextUtils.isEmpty(extra))
/* 140 */           return false;
/*     */         try {
/* 142 */           JSONObject jsonObj = new JSONObject(extra);
/* 143 */           robotEva = jsonObj.optString("robotEva");
/* 144 */           sid = jsonObj.optString("sid");
/*     */         } catch (JSONException e) {
/*     */         }
/*     */       }
/* 148 */       if ((data.getMessageDirection() == Message.MessageDirection.RECEIVE) && ((data.getContent() instanceof TextMessage)) && (this.evaForRobot) && (this.robotMode) && (!TextUtils.isEmpty(robotEva)) && (!TextUtils.isEmpty(sid)) && (!data.getIsHistoryMessage()))
/*     */       {
/* 155 */         return true;
/*     */       }
/*     */     }
/* 158 */     return false;
/*     */   }
/*     */ 
/*     */   protected void bindView(View v, int position, UIMessage data)
/*     */   {
/* 163 */     if (data == null) {
/* 164 */       return;
/*     */     }
/*     */ 
/* 167 */     ViewHolder holder = (ViewHolder)v.getTag();
/* 168 */     IContainerItemProvider provider = null;
/* 169 */     ProviderTag tag = null;
/*     */ 
/* 171 */     if (getNeedEvaluate(data)) {
/* 172 */       provider = RongContext.getInstance().getEvaluateProvider();
/* 173 */       tag = RongContext.getInstance().getMessageProviderTag(data.getContent().getClass());
/* 174 */     } else if ((RongContext.getInstance() != null) && (data != null) && (data.getContent() != null)) {
/* 175 */       provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
/* 176 */       if (provider == null) {
/* 177 */         provider = RongContext.getInstance().getMessageTemplate(UnknownMessage.class);
/* 178 */         tag = RongContext.getInstance().getMessageProviderTag(UnknownMessage.class);
/*     */       } else {
/* 180 */         tag = RongContext.getInstance().getMessageProviderTag(data.getContent().getClass());
/*     */       }
/* 182 */       if (provider == null) {
/* 183 */         RLog.e("MessageListAdapter", data.getObjectName() + " message provider not found !");
/* 184 */         return;
/*     */       }
/*     */     } else {
/* 187 */       RLog.e("MessageListAdapter", "Message is null !");
/* 188 */       return;
/*     */     }
/*     */ 
/* 191 */     View view = holder.contentView.inflate(provider);
/* 192 */     provider.bindView(view, position, data);
/*     */ 
/* 194 */     this.subView = view;
/*     */ 
/* 196 */     if (tag == null) {
/* 197 */       RLog.e("MessageListAdapter", "Can not find ProviderTag for " + data.getObjectName());
/* 198 */       return;
/*     */     }
/*     */ 
/* 201 */     if (tag.hide()) {
/* 202 */       holder.contentView.setVisibility(8);
/* 203 */       holder.time.setVisibility(8);
/* 204 */       holder.nameView.setVisibility(8);
/* 205 */       holder.leftIconView.setVisibility(8);
/* 206 */       holder.rightIconView.setVisibility(8);
/* 207 */       holder.layoutItem.setVisibility(8);
/* 208 */       holder.layoutItem.setPadding(0, 0, 0, 0);
/*     */     } else {
/* 210 */       holder.contentView.setVisibility(0);
/* 211 */       holder.layoutItem.setVisibility(0);
/* 212 */       holder.layoutItem.setPadding(CommonUtils.dip2px(this.mContext, 8.0F), CommonUtils.dip2px(this.mContext, 6.0F), CommonUtils.dip2px(this.mContext, 8.0F), CommonUtils.dip2px(this.mContext, 6.0F));
/*     */     }
/*     */ 
/* 218 */     if (data.getMessageDirection() == Message.MessageDirection.SEND)
/*     */     {
/* 220 */       if (tag.showPortrait()) {
/* 221 */         holder.rightIconView.setVisibility(0);
/* 222 */         holder.leftIconView.setVisibility(8);
/*     */       } else {
/* 224 */         holder.leftIconView.setVisibility(8);
/* 225 */         holder.rightIconView.setVisibility(8);
/*     */       }
/*     */ 
/* 228 */       if (!tag.centerInHorizontal()) {
/* 229 */         setGravity(holder.layout, 5);
/* 230 */         holder.contentView.containerViewRight();
/* 231 */         holder.nameView.setGravity(5);
/*     */       } else {
/* 233 */         setGravity(holder.layout, 17);
/* 234 */         holder.contentView.containerViewCenter();
/* 235 */         holder.nameView.setGravity(1);
/* 236 */         holder.contentView.setBackgroundColor(0);
/*     */       }
/*     */ 
/* 240 */       boolean readRec = false;
/*     */       try {
/* 242 */         readRec = this.mContext.getResources().getBoolean(R.bool.rc_read_receipt);
/*     */       } catch (Resources.NotFoundException e) {
/* 244 */         RLog.e("MessageListAdapter", "rc_read_receipt not configure in rc_config.xml");
/* 245 */         e.printStackTrace();
/*     */       }
/*     */ 
/* 248 */       if (data.getSentStatus() == Message.SentStatus.SENDING) {
/* 249 */         if (tag.showProgress())
/* 250 */           holder.progressBar.setVisibility(0);
/*     */         else {
/* 252 */           holder.progressBar.setVisibility(8);
/*     */         }
/* 254 */         holder.warning.setVisibility(8);
/* 255 */         holder.readReceipt.setVisibility(8);
/* 256 */       } else if (data.getSentStatus() == Message.SentStatus.FAILED) {
/* 257 */         holder.progressBar.setVisibility(8);
/* 258 */         holder.warning.setVisibility(0);
/* 259 */         holder.readReceipt.setVisibility(8);
/* 260 */       } else if (data.getSentStatus() == Message.SentStatus.SENT) {
/* 261 */         holder.progressBar.setVisibility(8);
/* 262 */         holder.warning.setVisibility(8);
/* 263 */         holder.readReceipt.setVisibility(8);
/* 264 */       } else if ((readRec) && (data.getSentStatus() == Message.SentStatus.READ)) {
/* 265 */         holder.progressBar.setVisibility(8);
/* 266 */         holder.warning.setVisibility(8);
/* 267 */         MessageContent content = data.getMessage().getContent();
/* 268 */         if ((data.getConversationType().equals(Conversation.ConversationType.PRIVATE)) && (!(content instanceof InformationNotificationMessage)) && (!(content instanceof RecallNotificationMessage)) && (!(content instanceof UnknownMessage)))
/*     */         {
/* 272 */           holder.readReceipt.setVisibility(0);
/*     */         }
/* 274 */         else holder.readReceipt.setVisibility(8); 
/*     */       }
/*     */       else
/*     */       {
/* 277 */         holder.progressBar.setVisibility(8);
/* 278 */         holder.warning.setVisibility(8);
/* 279 */         holder.readReceipt.setVisibility(8);
/*     */       }
/*     */ 
/* 282 */       holder.readReceiptRequest.setVisibility(8);
/* 283 */       holder.readReceiptStatus.setVisibility(8);
/* 284 */       if ((readRec) && (RongContext.getInstance().isReadReceiptConversationType(data.getConversationType())) && ((data.getConversationType().equals(Conversation.ConversationType.GROUP)) || (data.getConversationType().equals(Conversation.ConversationType.DISCUSSION))))
/*     */       {
/* 286 */         if (((data.getContent() instanceof TextMessage)) && (!TextUtils.isEmpty(data.getUId())))
/*     */         {
/* 288 */           boolean isLastSentMessage = true;
/* 289 */           for (int i = position + 1; i < getCount(); i++) {
/* 290 */             if (((UIMessage)getItem(i)).getMessageDirection() == Message.MessageDirection.SEND) {
/* 291 */               isLastSentMessage = false;
/* 292 */               break;
/*     */             }
/*     */           }
/*     */ 
/* 296 */           long serverTime = System.currentTimeMillis() - RongIMClient.getInstance().getDeltaTime();
/* 297 */           if ((serverTime - data.getSentTime() < 120000L) && (isLastSentMessage) && ((data.getReadReceiptInfo() == null) || (!data.getReadReceiptInfo().isReadReceiptMessage())))
/*     */           {
/* 300 */             holder.readReceiptRequest.setVisibility(0);
/*     */           }
/*     */         }
/* 303 */         if (((data.getContent() instanceof TextMessage)) && (data.getReadReceiptInfo() != null) && (data.getReadReceiptInfo().isReadReceiptMessage()))
/*     */         {
/* 306 */           if (data.getReadReceiptInfo().getRespondUserIdList() != null)
/* 307 */             holder.readReceiptStatus.setText(String.format(view.getResources().getString(R.string.rc_read_receipt_status), new Object[] { Integer.valueOf(data.getReadReceiptInfo().getRespondUserIdList().size()) }));
/*     */           else {
/* 309 */             holder.readReceiptStatus.setText(String.format(view.getResources().getString(R.string.rc_read_receipt_status), new Object[] { Integer.valueOf(0) }));
/*     */           }
/* 311 */           holder.readReceiptStatus.setVisibility(0);
/*     */         }
/*     */       }
/*     */ 
/* 315 */       if (data.getObjectName().equals("RC:VSTMsg")) {
/* 316 */         holder.readReceipt.setVisibility(8);
/*     */       }
/*     */ 
/* 319 */       holder.nameView.setVisibility(8);
/*     */ 
/* 321 */       holder.readReceiptRequest.setOnClickListener(new View.OnClickListener(data, holder, view)
/*     */       {
/*     */         public void onClick(View v) {
/* 324 */           RongIMClient.getInstance().sendReadReceiptRequest(this.val$data.getMessage(), new RongIMClient.OperationCallback()
/*     */           {
/*     */             public void onSuccess() {
/* 327 */               ReadReceiptInfo readReceiptInfo = MessageListAdapter.1.this.val$data.getReadReceiptInfo();
/* 328 */               if (readReceiptInfo == null) {
/* 329 */                 readReceiptInfo = new ReadReceiptInfo();
/* 330 */                 MessageListAdapter.1.this.val$data.setReadReceiptInfo(readReceiptInfo);
/*     */               }
/* 332 */               readReceiptInfo.setIsReadReceiptMessage(true);
/* 333 */               MessageListAdapter.1.this.val$holder.readReceiptStatus.setText(String.format(MessageListAdapter.1.this.val$view.getResources().getString(R.string.rc_read_receipt_status), new Object[] { Integer.valueOf(0) }));
/* 334 */               MessageListAdapter.1.this.val$holder.readReceiptRequest.setVisibility(8);
/* 335 */               MessageListAdapter.1.this.val$holder.readReceiptStatus.setVisibility(0);
/*     */             }
/*     */ 
/*     */             public void onError(RongIMClient.ErrorCode errorCode)
/*     */             {
/* 340 */               RLog.e("MessageListAdapter", "sendReadReceiptRequest failed, errorCode = " + errorCode);
/*     */             }
/*     */           });
/*     */         }
/*     */       });
/* 346 */       holder.rightIconView.setOnClickListener(new View.OnClickListener(data)
/*     */       {
/*     */         public void onClick(View v) {
/* 349 */           if (RongContext.getInstance().getConversationBehaviorListener() != null) {
/* 350 */             UserInfo userInfo = null;
/* 351 */             if (!TextUtils.isEmpty(this.val$data.getSenderUserId())) {
/* 352 */               userInfo = RongUserInfoManager.getInstance().getUserInfo(this.val$data.getSenderUserId());
/* 353 */               userInfo = userInfo == null ? new UserInfo(this.val$data.getSenderUserId(), null, null) : userInfo;
/*     */             }
/* 355 */             RongContext.getInstance().getConversationBehaviorListener().onUserPortraitClick(MessageListAdapter.this.mContext, this.val$data.getConversationType(), userInfo);
/*     */           }
/*     */         }
/*     */       });
/* 360 */       holder.rightIconView.setOnLongClickListener(new View.OnLongClickListener(data)
/*     */       {
/*     */         public boolean onLongClick(View v)
/*     */         {
/* 365 */           if (RongContext.getInstance().getConversationBehaviorListener() != null) {
/* 366 */             UserInfo userInfo = null;
/* 367 */             if (!TextUtils.isEmpty(this.val$data.getSenderUserId())) {
/* 368 */               userInfo = RongUserInfoManager.getInstance().getUserInfo(this.val$data.getSenderUserId());
/* 369 */               userInfo = userInfo == null ? new UserInfo(this.val$data.getSenderUserId(), null, null) : userInfo;
/*     */             }
/* 371 */             return RongContext.getInstance().getConversationBehaviorListener().onUserPortraitLongClick(MessageListAdapter.this.mContext, this.val$data.getConversationType(), userInfo);
/*     */           }
/*     */ 
/* 374 */           return true;
/*     */         }
/*     */       });
/* 378 */       if (!tag.showWarning()) {
/* 379 */         holder.warning.setVisibility(8);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 384 */       if (tag.showPortrait()) {
/* 385 */         holder.rightIconView.setVisibility(8);
/* 386 */         holder.leftIconView.setVisibility(0);
/*     */       } else {
/* 388 */         holder.leftIconView.setVisibility(8);
/* 389 */         holder.rightIconView.setVisibility(8);
/*     */       }
/*     */ 
/* 392 */       if (!tag.centerInHorizontal()) {
/* 393 */         setGravity(holder.layout, 3);
/* 394 */         holder.contentView.containerViewLeft();
/* 395 */         holder.nameView.setGravity(3);
/*     */       }
/*     */       else {
/* 398 */         setGravity(holder.layout, 17);
/* 399 */         holder.contentView.containerViewCenter();
/* 400 */         holder.nameView.setGravity(1);
/* 401 */         holder.contentView.setBackgroundColor(0);
/*     */       }
/*     */ 
/* 404 */       holder.progressBar.setVisibility(8);
/* 405 */       holder.warning.setVisibility(8);
/* 406 */       holder.readReceipt.setVisibility(8);
/* 407 */       holder.readReceiptRequest.setVisibility(8);
/* 408 */       holder.readReceiptStatus.setVisibility(8);
/*     */ 
/* 410 */       holder.nameView.setVisibility(0);
/*     */ 
/* 412 */       if ((data.getConversationType() == Conversation.ConversationType.PRIVATE) || (!tag.showSummaryWithName()) || (data.getConversationType() == Conversation.ConversationType.PUBLIC_SERVICE) || (data.getConversationType() == Conversation.ConversationType.APP_PUBLIC_SERVICE))
/*     */       {
/* 417 */         holder.nameView.setVisibility(8);
/*     */       }
/* 420 */       else if ((data.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE)) && (data.getUserInfo() != null) && (data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)))
/*     */       {
/* 422 */         UserInfo userInfo = data.getUserInfo();
/* 423 */         holder.nameView.setText(userInfo.getName());
/* 424 */       } else if (data.getConversationType() == Conversation.ConversationType.GROUP) {
/* 425 */         GroupUserInfo groupUserInfo = RongUserInfoManager.getInstance().getGroupUserInfo(data.getTargetId(), data.getSenderUserId());
/* 426 */         if (groupUserInfo != null) {
/* 427 */           holder.nameView.setText(groupUserInfo.getNickname());
/*     */         } else {
/* 429 */           UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
/* 430 */           if (userInfo == null)
/* 431 */             holder.nameView.setText(data.getSenderUserId());
/*     */           else
/* 433 */             holder.nameView.setText(userInfo.getName());
/*     */         }
/*     */       } else {
/* 436 */         UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
/* 437 */         if (userInfo == null)
/* 438 */           holder.nameView.setText(data.getSenderUserId());
/*     */         else {
/* 440 */           holder.nameView.setText(userInfo.getName());
/*     */         }
/*     */       }
/*     */ 
/* 444 */       holder.leftIconView.setOnClickListener(new View.OnClickListener(data)
/*     */       {
/*     */         public void onClick(View v) {
/* 447 */           if (RongContext.getInstance().getConversationBehaviorListener() != null) {
/* 448 */             UserInfo userInfo = null;
/* 449 */             if (!TextUtils.isEmpty(this.val$data.getSenderUserId())) {
/* 450 */               userInfo = RongUserInfoManager.getInstance().getUserInfo(this.val$data.getSenderUserId());
/* 451 */               userInfo = userInfo == null ? new UserInfo(this.val$data.getSenderUserId(), null, null) : userInfo;
/*     */             }
/* 453 */             RongContext.getInstance().getConversationBehaviorListener().onUserPortraitClick(MessageListAdapter.this.mContext, this.val$data.getConversationType(), userInfo);
/*     */           }
/* 455 */           EventBus.getDefault().post(Event.InputViewEvent.obtain(false));
/*     */         }
/*     */       });
/*     */     }
/* 460 */     holder.leftIconView.setOnLongClickListener(new View.OnLongClickListener(data)
/*     */     {
/*     */       public boolean onLongClick(View v)
/*     */       {
/* 464 */         UserInfo userInfo = null;
/* 465 */         if (!TextUtils.isEmpty(this.val$data.getSenderUserId())) {
/* 466 */           userInfo = RongUserInfoManager.getInstance().getUserInfo(this.val$data.getSenderUserId());
/* 467 */           userInfo = userInfo == null ? new UserInfo(this.val$data.getSenderUserId(), null, null) : userInfo;
/*     */         }
/* 469 */         if ((RongContext.getInstance().getConversationBehaviorListener() == null) || (!RongContext.getInstance().getConversationBehaviorListener().onUserPortraitLongClick(MessageListAdapter.this.mContext, this.val$data.getConversationType(), userInfo)))
/*     */         {
/* 471 */           if ((RongContext.getInstance().getResources().getBoolean(R.bool.rc_enable_mentioned_message)) && (MessageListAdapter.this.mMentionMemberSelectListener != null) && ((this.val$data.getConversationType().equals(Conversation.ConversationType.GROUP)) || (this.val$data.getConversationType().equals(Conversation.ConversationType.DISCUSSION))))
/*     */           {
/* 474 */             MessageListAdapter.this.mMentionMemberSelectListener.onMemberMentioned(this.val$data.getSenderUserId());
/* 475 */             return true;
/*     */           }
/* 477 */           return false;
/*     */         }
/*     */ 
/* 480 */         return RongContext.getInstance().getConversationBehaviorListener().onUserPortraitLongClick(MessageListAdapter.this.mContext, this.val$data.getConversationType(), userInfo);
/*     */       }
/*     */     });
/* 486 */     if (holder.rightIconView.getVisibility() == 0)
/*     */     {
/* 489 */       if ((data.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE)) && (data.getUserInfo() != null) && (data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)))
/*     */       {
/* 491 */         UserInfo userInfo = data.getUserInfo();
/* 492 */         Uri portrait = userInfo.getPortraitUri();
/* 493 */         if (portrait != null)
/* 494 */           holder.rightIconView.setAvatar(portrait.toString(), 0);
/*     */       }
/* 496 */       else if (((data.getConversationType().equals(Conversation.ConversationType.PUBLIC_SERVICE)) || (data.getConversationType().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE))) && (data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)))
/*     */       {
/* 500 */         UserInfo userInfo = data.getUserInfo();
/* 501 */         if (userInfo != null) {
/* 502 */           Uri portrait = userInfo.getPortraitUri();
/* 503 */           if (portrait != null) {
/* 504 */             holder.leftIconView.setAvatar(portrait.toString(), 0);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 509 */           ConversationKey mKey = ConversationKey.obtain(data.getTargetId(), data.getConversationType());
/* 510 */           PublicServiceProfile publicServiceProfile = RongContext.getInstance().getPublicServiceInfoFromCache(mKey.getKey());
/* 511 */           Uri portrait = publicServiceProfile.getPortraitUri();
/*     */ 
/* 513 */           if (portrait != null)
/* 514 */             holder.rightIconView.setAvatar(portrait.toString(), 0);
/*     */         }
/*     */       }
/* 517 */       else if (!TextUtils.isEmpty(data.getSenderUserId())) {
/* 518 */         UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
/*     */ 
/* 520 */         if ((userInfo != null) && (userInfo.getPortraitUri() != null))
/* 521 */           holder.rightIconView.setAvatar(userInfo.getPortraitUri().toString(), 0);
/*     */       }
/*     */     }
/* 524 */     else if (holder.leftIconView.getVisibility() == 0)
/*     */     {
/* 527 */       if ((data.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE)) && (data.getUserInfo() != null) && (data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)))
/*     */       {
/* 529 */         UserInfo userInfo = data.getUserInfo();
/* 530 */         Uri portrait = userInfo.getPortraitUri();
/* 531 */         if (portrait != null)
/* 532 */           holder.leftIconView.setAvatar(portrait.toString(), 0);
/*     */       }
/* 534 */       else if (((data.getConversationType().equals(Conversation.ConversationType.PUBLIC_SERVICE)) || (data.getConversationType().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE))) && (data.getMessageDirection().equals(Message.MessageDirection.RECEIVE)))
/*     */       {
/* 538 */         UserInfo userInfo = data.getUserInfo();
/* 539 */         if (userInfo != null) {
/* 540 */           Uri portrait = userInfo.getPortraitUri();
/* 541 */           if (portrait != null)
/* 542 */             holder.leftIconView.setAvatar(portrait.toString(), 0);
/*     */         }
/*     */         else
/*     */         {
/* 546 */           ConversationKey mKey = ConversationKey.obtain(data.getTargetId(), data.getConversationType());
/* 547 */           PublicServiceProfile publicServiceProfile = RongContext.getInstance().getPublicServiceInfoFromCache(mKey.getKey());
/* 548 */           if ((publicServiceProfile != null) && (publicServiceProfile.getPortraitUri() != null))
/* 549 */             holder.leftIconView.setAvatar(publicServiceProfile.getPortraitUri().toString(), 0);
/*     */         }
/*     */       }
/* 552 */       else if (!TextUtils.isEmpty(data.getSenderUserId())) {
/* 553 */         UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
/*     */ 
/* 555 */         if ((userInfo != null) && (userInfo.getPortraitUri() != null)) {
/* 556 */           holder.leftIconView.setAvatar(userInfo.getPortraitUri().toString(), 0);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 561 */     if (view != null) {
/* 562 */       view.setOnClickListener(new View.OnClickListener(data, position)
/*     */       {
/*     */         public void onClick(View v)
/*     */         {
/* 566 */           if ((RongContext.getInstance().getConversationBehaviorListener() != null) && 
/* 567 */             (RongContext.getInstance().getConversationBehaviorListener().onMessageClick(MessageListAdapter.this.mContext, v, this.val$data)))
/* 568 */             return;
/*     */           IContainerItemProvider.MessageProvider provider;
/*     */           IContainerItemProvider.MessageProvider provider;
/* 573 */           if (MessageListAdapter.this.getNeedEvaluate(this.val$data))
/* 574 */             provider = RongContext.getInstance().getEvaluateProvider();
/*     */           else
/* 576 */             provider = RongContext.getInstance().getMessageTemplate(this.val$data.getContent().getClass());
/* 577 */           if (provider != null)
/* 578 */             provider.onItemClick(v, this.val$position, this.val$data.getContent(), this.val$data);
/*     */         }
/*     */       });
/* 582 */       view.setOnLongClickListener(new View.OnLongClickListener(data, position)
/*     */       {
/*     */         public boolean onLongClick(View v) {
/* 585 */           if ((RongContext.getInstance().getConversationBehaviorListener() != null) && 
/* 586 */             (RongContext.getInstance().getConversationBehaviorListener().onMessageLongClick(MessageListAdapter.this.mContext, v, this.val$data)))
/* 587 */             return true;
/*     */           IContainerItemProvider.MessageProvider provider;
/*     */           IContainerItemProvider.MessageProvider provider;
/* 590 */           if (MessageListAdapter.this.getNeedEvaluate(this.val$data))
/* 591 */             provider = RongContext.getInstance().getEvaluateProvider();
/*     */           else
/* 593 */             provider = RongContext.getInstance().getMessageTemplate(this.val$data.getContent().getClass());
/* 594 */           if (provider != null)
/* 595 */             provider.onItemLongClick(v, this.val$position, this.val$data.getContent(), this.val$data);
/* 596 */           return true;
/*     */         }
/*     */       });
/*     */     }
/* 601 */     holder.warning.setOnClickListener(new View.OnClickListener(position, data)
/*     */     {
/*     */       public void onClick(View v) {
/* 604 */         if (MessageListAdapter.this.mOnItemHandlerListener != null)
/* 605 */           MessageListAdapter.this.mOnItemHandlerListener.onWarningViewClick(this.val$position, this.val$data, v);
/*     */       }
/*     */     });
/* 609 */     if (tag.hide()) {
/* 610 */       holder.time.setVisibility(8);
/* 611 */       return;
/*     */     }
/*     */ 
/* 614 */     if (!this.timeGone) {
/* 615 */       String time = RongDateUtils.getConversationFormatDate(data.getSentTime(), view.getContext());
/* 616 */       holder.time.setText(time);
/* 617 */       if (position == 0) {
/* 618 */         holder.time.setVisibility(0);
/*     */       } else {
/* 620 */         Message pre = (Message)getItem(position - 1);
/* 621 */         if (RongDateUtils.isShowChatTime(data.getSentTime(), pre.getSentTime(), 180))
/* 622 */           holder.time.setVisibility(0);
/*     */         else
/* 624 */           holder.time.setVisibility(8);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void setGravity(View view, int gravity)
/*     */   {
/* 631 */     FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)view.getLayoutParams();
/* 632 */     params.gravity = gravity;
/*     */   }
/*     */ 
/*     */   public void setEvaluateForRobot(boolean needEvaluate) {
/* 636 */     this.evaForRobot = needEvaluate;
/*     */   }
/*     */ 
/*     */   public void setRobotMode(boolean robotMode) {
/* 640 */     this.robotMode = robotMode;
/*     */   }
/*     */ 
/*     */   public static abstract interface OnItemHandlerListener
/*     */   {
/*     */     public abstract void onWarningViewClick(int paramInt, Message paramMessage, View paramView);
/*     */   }
/*     */ 
/*     */   class ViewHolder
/*     */   {
/*     */     AsyncImageView leftIconView;
/*     */     AsyncImageView rightIconView;
/*     */     TextView nameView;
/*     */     ProviderContainerView contentView;
/*     */     ProgressBar progressBar;
/*     */     ImageView warning;
/*     */     ImageView readReceipt;
/*     */     ImageView readReceiptRequest;
/*     */     TextView readReceiptStatus;
/*     */     ViewGroup layout;
/*     */     TextView time;
/*     */     TextView sentStatus;
/*     */     RelativeLayout layoutItem;
/*     */ 
/*     */     ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.adapter.MessageListAdapter
 * JD-Core Version:    0.6.0
 */
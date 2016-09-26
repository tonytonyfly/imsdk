/*     */ package io.rong.imkit.fragment;
/*     */ 
/*     */ import android.app.AlertDialog.Builder;
/*     */ import android.content.DialogInterface;
/*     */ import android.content.Intent;
/*     */ import android.content.res.Resources;
/*     */ import android.net.Uri;
/*     */ import android.os.Bundle;
/*     */ import android.os.Handler;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.text.Spannable;
/*     */ import android.text.SpannableStringBuilder;
/*     */ import android.text.style.ForegroundColorSpan;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.AdapterView;
/*     */ import android.widget.AdapterView.OnItemClickListener;
/*     */ import android.widget.AdapterView.OnItemLongClickListener;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.LinearLayout;
/*     */ import android.widget.ListView;
/*     */ import android.widget.TextView;
/*     */ import android.widget.Toast;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.R.bool;
/*     */ import io.rong.imkit.R.color;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.RongIM.ConversationListBehaviorListener;
/*     */ import io.rong.imkit.model.ConversationInfo;
/*     */ import io.rong.imkit.model.Draft;
/*     */ import io.rong.imkit.model.Event.AudioListenedEvent;
/*     */ import io.rong.imkit.model.Event.ClearConversationEvent;
/*     */ import io.rong.imkit.model.Event.ConversationNotificationEvent;
/*     */ import io.rong.imkit.model.Event.ConversationRemoveEvent;
/*     */ import io.rong.imkit.model.Event.ConversationTopEvent;
/*     */ import io.rong.imkit.model.Event.ConversationUnreadEvent;
/*     */ import io.rong.imkit.model.Event.GroupUserInfoEvent;
/*     */ import io.rong.imkit.model.Event.MessageDeleteEvent;
/*     */ import io.rong.imkit.model.Event.MessageRecallEvent;
/*     */ import io.rong.imkit.model.Event.MessagesClearEvent;
/*     */ import io.rong.imkit.model.Event.OnMessageSendErrorEvent;
/*     */ import io.rong.imkit.model.Event.OnReceiveMessageEvent;
/*     */ import io.rong.imkit.model.Event.QuitDiscussionEvent;
/*     */ import io.rong.imkit.model.Event.QuitGroupEvent;
/*     */ import io.rong.imkit.model.Event.ReadReceiptEvent;
/*     */ import io.rong.imkit.model.Event.RemoteMessageRecallEvent;
/*     */ import io.rong.imkit.model.Event.SyncReadStatusEvent;
/*     */ import io.rong.imkit.model.GroupUserInfo;
/*     */ import io.rong.imkit.model.UIConversation;
/*     */ import io.rong.imkit.utils.ConversationListUtils;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment.OnArraysDialogItemListener;
/*     */ import io.rong.imkit.widget.adapter.SubConversationListAdapter;
/*     */ import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.model.Conversation;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Discussion;
/*     */ import io.rong.imlib.model.Group;
/*     */ import io.rong.imlib.model.MentionedInfo;
/*     */ import io.rong.imlib.model.MentionedInfo.MentionedType;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.imlib.model.Message.MessageDirection;
/*     */ import io.rong.imlib.model.Message.ReceivedStatus;
/*     */ import io.rong.imlib.model.Message.SentStatus;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ import io.rong.imlib.model.PublicServiceProfile;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import io.rong.message.ReadReceiptMessage;
/*     */ import io.rong.message.VoiceMessage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class SubConversationListFragment extends UriFragment
/*     */   implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
/*     */ {
/*     */   private static final String TAG = "SubConversationListFragment";
/*     */   private SubConversationListAdapter mAdapter;
/*     */   private Conversation.ConversationType currentType;
/*     */   private LinearLayout mNotificationBar;
/*     */   private TextView mNotificationBarText;
/*     */   private ImageView mNotificationBarImage;
/*     */   private ListView mList;
/*  58 */   RongIMClient.ResultCallback<List<Conversation>> mCallback = new RongIMClient.ResultCallback()
/*     */   {
/*     */     public void onSuccess(List<Conversation> conversations) {
/*  61 */       RLog.d("SubConversationListFragment", "SubConversationListFragment initFragment onSuccess callback");
/*  62 */       if ((conversations == null) || (conversations.size() == 0)) {
/*  63 */         return;
/*     */       }
/*  65 */       List uiConversationList = new ArrayList();
/*  66 */       for (Conversation conversation : conversations) {
/*  67 */         if (SubConversationListFragment.this.mAdapter.getCount() > 0) {
/*  68 */           int pos = SubConversationListFragment.this.mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
/*  69 */           if (pos < 0) {
/*  70 */             UIConversation uiConversation = UIConversation.obtain(conversation, false);
/*  71 */             uiConversationList.add(uiConversation);
/*     */           }
/*     */         } else {
/*  74 */           UIConversation uiConversation = UIConversation.obtain(conversation, false);
/*  75 */           uiConversationList.add(uiConversation);
/*     */         }
/*     */       }
/*     */ 
/*  79 */       SubConversationListFragment.this.mAdapter.addCollection(uiConversationList);
/*     */ 
/*  81 */       if ((SubConversationListFragment.this.mList != null) && (SubConversationListFragment.this.mList.getAdapter() != null))
/*  82 */         SubConversationListFragment.this.mAdapter.notifyDataSetChanged();
/*     */     }
/*     */ 
/*     */     public void onError(RongIMClient.ErrorCode e)
/*     */     {
/*  89 */       RLog.d("SubConversationListFragment", "SubConversationListFragment initFragment onError callback, e=" + e);
/*     */     }
/*  58 */   };
/*     */ 
/*     */   public static ConversationListFragment getInstance()
/*     */   {
/*  94 */     return new ConversationListFragment();
/*     */   }
/*     */ 
/*     */   public void onCreate(Bundle savedInstanceState)
/*     */   {
/*  99 */     super.onCreate(savedInstanceState);
/* 100 */     RongContext.getInstance().getEventBus().register(this);
/*     */ 
/* 102 */     if ((getActivity().getIntent() == null) || (getActivity().getIntent().getData() == null))
/* 103 */       throw new IllegalArgumentException();
/* 104 */     if (this.mAdapter == null)
/* 105 */       this.mAdapter = new SubConversationListAdapter(getActivity());
/*     */   }
/*     */ 
/*     */   public void initFragment(Uri uri)
/*     */   {
/* 110 */     String type = uri.getQueryParameter("type");
/* 111 */     Conversation.ConversationType value = null;
/*     */ 
/* 113 */     RLog.d("SubConversationListFragment", "initFragment uri=" + uri);
/*     */ 
/* 115 */     this.currentType = null;
/*     */ 
/* 117 */     Conversation.ConversationType[] defaultTypes = { Conversation.ConversationType.PRIVATE, Conversation.ConversationType.DISCUSSION, Conversation.ConversationType.GROUP, Conversation.ConversationType.CHATROOM, Conversation.ConversationType.CUSTOMER_SERVICE, Conversation.ConversationType.SYSTEM, Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE };
/*     */ 
/* 121 */     for (Conversation.ConversationType conversationType : defaultTypes) {
/* 122 */       if (conversationType.getName().equals(type)) {
/* 123 */         this.currentType = conversationType;
/* 124 */         value = conversationType;
/* 125 */         break;
/*     */       }
/*     */     }
/* 128 */     if (value != null)
/* 129 */       RongIM.getInstance().getConversationList(this.mCallback, new Conversation.ConversationType[] { value });
/*     */     else
/* 131 */       throw new IllegalArgumentException("Unknown conversation type!!");
/*     */   }
/*     */ 
/*     */   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
/*     */   {
/* 137 */     View view = inflater.inflate(R.layout.rc_fr_conversationlist, null);
/* 138 */     if (getResources().getBoolean(R.bool.rc_is_show_warning_notification)) {
/* 139 */       this.mNotificationBar = ((LinearLayout)findViewById(view, R.id.rc_status_bar));
/* 140 */       this.mNotificationBarImage = ((ImageView)findViewById(view, R.id.rc_status_bar_image));
/* 141 */       this.mNotificationBarText = ((TextView)findViewById(view, R.id.rc_status_bar_text));
/*     */     }
/* 143 */     this.mList = ((ListView)findViewById(view, R.id.rc_list));
/* 144 */     return view;
/*     */   }
/*     */ 
/*     */   public void onViewCreated(View view, Bundle savedInstanceState)
/*     */   {
/* 149 */     this.mList.setAdapter(this.mAdapter);
/* 150 */     this.mList.setOnItemClickListener(this);
/* 151 */     this.mList.setOnItemLongClickListener(this);
/* 152 */     super.onViewCreated(view, savedInstanceState);
/*     */   }
/*     */ 
/*     */   public void onResume()
/*     */   {
/* 157 */     RLog.d("SubConversationListFragment", "SubConversationListFragment onResume");
/* 158 */     super.onResume();
/* 159 */     if (getResources().getBoolean(R.bool.rc_is_show_warning_notification)) {
/* 160 */       RongIMClient.ConnectionStatusListener.ConnectionStatus status = RongIM.getInstance().getCurrentConnectionStatus();
/*     */ 
/* 162 */       setNotificationBarVisibility(status);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.SyncReadStatusEvent event) {
/* 167 */     if (this.mAdapter == null) {
/* 168 */       RLog.d("SubConversationListFragment", "onEventMainThread ReadReceiptEvent adapter is null");
/* 169 */       return;
/*     */     }
/*     */ 
/* 172 */     int originalIndex = this.mAdapter.findPosition(event.getConversationType(), event.getTargetId());
/*     */ 
/* 174 */     if (originalIndex >= 0)
/* 175 */       RongIM.getInstance().getUnreadCount(event.getConversationType(), event.getTargetId(), new RongIMClient.ResultCallback(originalIndex)
/*     */       {
/*     */         public void onSuccess(Integer integer) {
/* 178 */           ((UIConversation)SubConversationListFragment.this.mAdapter.getItem(this.val$originalIndex)).setMentionedFlag(false);
/* 179 */           ((UIConversation)SubConversationListFragment.this.mAdapter.getItem(this.val$originalIndex)).setUnReadMessageCount(integer.intValue());
/* 180 */           SubConversationListFragment.this.mAdapter.getView(this.val$originalIndex, SubConversationListFragment.this.mList.getChildAt(this.val$originalIndex - SubConversationListFragment.this.mList.getFirstVisiblePosition()), SubConversationListFragment.this.mList);
/*     */         }
/*     */ 
/*     */         public void onError(RongIMClient.ErrorCode e)
/*     */         {
/*     */         }
/*     */       });
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.ReadReceiptEvent event)
/*     */   {
/* 193 */     if (this.mAdapter == null) {
/* 194 */       RLog.d("SubConversationListFragment", "onEventMainThread ReadReceiptEvent adapter is null");
/* 195 */       return;
/*     */     }
/*     */ 
/* 198 */     int originalIndex = this.mAdapter.findPosition(event.getMessage().getConversationType(), event.getMessage().getTargetId());
/*     */ 
/* 200 */     if (originalIndex >= 0)
/* 201 */       if (event.getMessage().getMessageDirection().equals(Message.MessageDirection.SEND)) {
/* 202 */         RongIM.getInstance().getUnreadCount(event.getMessage().getConversationType(), event.getMessage().getTargetId(), new RongIMClient.ResultCallback(originalIndex)
/*     */         {
/*     */           public void onSuccess(Integer integer) {
/* 205 */             ((UIConversation)SubConversationListFragment.this.mAdapter.getItem(this.val$originalIndex)).setUnReadMessageCount(integer.intValue());
/* 206 */             SubConversationListFragment.this.mAdapter.getView(this.val$originalIndex, SubConversationListFragment.this.mList.getChildAt(this.val$originalIndex - SubConversationListFragment.this.mList.getFirstVisiblePosition()), SubConversationListFragment.this.mList);
/*     */           }
/*     */ 
/*     */           public void onError(RongIMClient.ErrorCode e) {
/*     */           }
/*     */         });
/*     */       }
/*     */       else {
/* 215 */         UIConversation conversation = (UIConversation)this.mAdapter.getItem(originalIndex);
/* 216 */         ReadReceiptMessage content = (ReadReceiptMessage)event.getMessage().getContent();
/* 217 */         if ((content.getLastMessageSendTime() >= conversation.getUIConversationTime()) && (conversation.getConversationSenderId().equals(RongIMClient.getInstance().getCurrentUserId())))
/*     */         {
/* 219 */           conversation.setSentStatus(Message.SentStatus.READ);
/* 220 */           this.mAdapter.getView(originalIndex, this.mList.getChildAt(originalIndex - this.mList.getFirstVisiblePosition()), this.mList);
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Message message)
/*     */   {
/* 228 */     RLog.d("SubConversationListFragment", "onEventMainThread Message");
/*     */ 
/* 230 */     int originalIndex = this.mAdapter.findPosition(message.getConversationType(), message.getTargetId());
/*     */ 
/* 232 */     if (!message.getConversationType().equals(this.currentType)) {
/* 233 */       return;
/*     */     }
/* 235 */     UIConversation uiConversation = null;
/*     */ 
/* 237 */     if (originalIndex >= 0) {
/* 238 */       uiConversation = makeUiConversation(message, originalIndex);
/*     */ 
/* 240 */       int newPosition = ConversationListUtils.findPositionForNewConversation(uiConversation, this.mAdapter);
/* 241 */       if (newPosition == originalIndex) {
/* 242 */         this.mAdapter.getView(newPosition, this.mList.getChildAt(newPosition - this.mList.getFirstVisiblePosition()), this.mList);
/*     */       } else {
/* 244 */         this.mAdapter.remove(originalIndex);
/* 245 */         this.mAdapter.add(uiConversation, newPosition);
/* 246 */         this.mAdapter.notifyDataSetChanged();
/*     */       }
/*     */     }
/*     */     else {
/* 250 */       uiConversation = UIConversation.obtain(message, false);
/* 251 */       this.mAdapter.add(uiConversation, ConversationListUtils.findPositionForNewConversation(uiConversation, this.mAdapter));
/* 252 */       this.mAdapter.notifyDataSetChanged();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.OnReceiveMessageEvent onReceiveMessageEvent) {
/* 257 */     onEventMainThread(onReceiveMessageEvent.getMessage());
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(MessageContent content)
/*     */   {
/* 262 */     RLog.d("SubConversationListFragment", "onEventMainThread::MessageContent MessageContent");
/*     */ 
/* 264 */     for (int index = this.mList.getFirstVisiblePosition(); index < this.mList.getLastVisiblePosition(); index++) {
/* 265 */       UIConversation tempUIConversation = (UIConversation)this.mAdapter.getItem(index);
/* 266 */       if (tempUIConversation.getMessageContent().equals(content)) {
/* 267 */         tempUIConversation.setMessageContent(content);
/*     */ 
/* 269 */         Spannable messageData = RongContext.getInstance().getMessageTemplate(content.getClass()).getContentSummary(content);
/*     */ 
/* 271 */         if ((tempUIConversation.getMessageContent() instanceof VoiceMessage)) {
/* 272 */           boolean isListened = RongIM.getInstance().getConversation(tempUIConversation.getConversationType(), tempUIConversation.getConversationTargetId()).getReceivedStatus().isListened();
/*     */ 
/* 274 */           if (isListened)
/* 275 */             messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_text_color_secondary)), 0, messageData.length(), 33);
/*     */           else {
/* 277 */             messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_voice_color)), 0, messageData.length(), 33);
/*     */           }
/*     */         }
/* 280 */         tempUIConversation.setConversationContent(messageData);
/* 281 */         this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition()), this.mList);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Draft draft) {
/* 287 */     Conversation.ConversationType curType = Conversation.ConversationType.setValue(draft.getType().intValue());
/* 288 */     if (curType == null) {
/* 289 */       throw new IllegalArgumentException("the type of the draft is unknown!");
/*     */     }
/*     */ 
/* 292 */     int position = this.mAdapter.findPosition(curType, draft.getId());
/*     */ 
/* 294 */     if (position >= 0) {
/* 295 */       UIConversation conversation = (UIConversation)this.mAdapter.getItem(position);
/* 296 */       if (draft.getContent() == null)
/* 297 */         conversation.setDraft("");
/*     */       else {
/* 299 */         conversation.setDraft(draft.getContent());
/*     */       }
/* 301 */       this.mAdapter.getView(position, this.mList.getChildAt(position - this.mList.getFirstVisiblePosition()), this.mList);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Group groupInfo)
/*     */   {
/* 309 */     int count = this.mAdapter.getCount();
/*     */ 
/* 311 */     if (groupInfo.getName() == null) {
/* 312 */       return;
/*     */     }
/*     */ 
/* 315 */     for (int i = 0; i < count; i++) {
/* 316 */       UIConversation temp = (UIConversation)this.mAdapter.getItem(i);
/* 317 */       if (temp.getConversationTargetId().equals(groupInfo.getId())) {
/* 318 */         temp.setUIConversationTitle(groupInfo.getName());
/* 319 */         if (groupInfo.getPortraitUri() != null)
/* 320 */           temp.setIconUrl(groupInfo.getPortraitUri());
/* 321 */         this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.GroupUserInfoEvent event) {
/* 327 */     RLog.d("SubConversationListFragment", "onEvent update GroupUserInfoEvent");
/* 328 */     GroupUserInfo userInfo = event.getUserInfo();
/* 329 */     if ((userInfo == null) || (userInfo.getNickname() == null)) {
/* 330 */       return;
/*     */     }
/*     */ 
/* 333 */     RongContext context = RongContext.getInstance();
/* 334 */     if (context == null) {
/* 335 */       return;
/*     */     }
/*     */ 
/* 338 */     int count = this.mAdapter.getCount();
/* 339 */     for (int i = 0; i < count; i++) {
/* 340 */       UIConversation uiConversation = (UIConversation)this.mAdapter.getItem(i);
/* 341 */       String type = uiConversation.getConversationType().getName();
/* 342 */       MessageContent messageContent = uiConversation.getMessageContent();
/* 343 */       if (messageContent == null) {
/*     */         continue;
/*     */       }
/* 346 */       IContainerItemProvider.MessageProvider provider = context.getMessageTemplate(messageContent.getClass());
/* 347 */       if (provider == null) {
/*     */         continue;
/*     */       }
/* 350 */       Spannable messageData = provider.getContentSummary(messageContent);
/* 351 */       if (messageData == null)
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 356 */       if ((!type.equals(Conversation.ConversationType.GROUP.getName())) || (!uiConversation.getConversationSenderId().equals(userInfo.getUserId())))
/*     */         continue;
/* 358 */       SpannableStringBuilder builder = new SpannableStringBuilder();
/*     */ 
/* 360 */       if ((uiConversation.getMessageContent() instanceof VoiceMessage)) {
/* 361 */         boolean isListened = RongIM.getInstance().getConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId()).getReceivedStatus().isListened();
/*     */ 
/* 363 */         if ((uiConversation.getConversationSenderId().equals(RongIMClient.getInstance().getCurrentUserId())) || (isListened))
/* 364 */           messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_text_color_secondary)), 0, messageData.length(), 33);
/*     */         else {
/* 366 */           messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_voice_color)), 0, messageData.length(), 33);
/*     */         }
/*     */       }
/* 369 */       if (uiConversation.getConversationTargetId().equals(userInfo.getGroupId())) {
/* 370 */         uiConversation.addNickname(userInfo.getUserId());
/* 371 */         builder.append(userInfo.getNickname()).append(" : ").append(messageData);
/* 372 */         uiConversation.setConversationContent(builder);
/*     */       }
/* 374 */       this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(UserInfo userInfo)
/*     */   {
/* 381 */     RLog.d("SubConversationListFragment", "onEvent update userInfo");
/* 382 */     if ((userInfo == null) || (userInfo.getName() == null)) {
/* 383 */       return;
/*     */     }
/*     */ 
/* 386 */     RongContext context = RongContext.getInstance();
/* 387 */     if (context == null) {
/* 388 */       return;
/*     */     }
/*     */ 
/* 391 */     int count = this.mAdapter.getCount();
/* 392 */     for (int i = 0; i < count; i++) {
/* 393 */       UIConversation uiConversation = (UIConversation)this.mAdapter.getItem(i);
/* 394 */       String type = uiConversation.getConversationType().getName();
/* 395 */       MessageContent messageContent = uiConversation.getMessageContent();
/* 396 */       if (uiConversation.hasNickname(userInfo.getUserId())) {
/*     */         continue;
/*     */       }
/* 399 */       if (messageContent == null) {
/*     */         continue;
/*     */       }
/* 402 */       IContainerItemProvider.MessageProvider provider = context.getMessageTemplate(messageContent.getClass());
/* 403 */       if (provider == null) {
/*     */         continue;
/*     */       }
/* 406 */       Spannable messageData = provider.getContentSummary(messageContent);
/* 407 */       if (messageData == null)
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 412 */       if (((type.equals(Conversation.ConversationType.GROUP.getName())) || (type.equals(Conversation.ConversationType.DISCUSSION.getName()))) && (uiConversation.getConversationSenderId().equals(userInfo.getUserId())))
/*     */       {
/* 415 */         SpannableStringBuilder builder = new SpannableStringBuilder();
/* 416 */         if ((uiConversation.getMessageContent() instanceof VoiceMessage)) {
/* 417 */           boolean isListened = RongIM.getInstance().getConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId()).getReceivedStatus().isListened();
/*     */ 
/* 419 */           if ((uiConversation.getConversationSenderId().equals(RongIMClient.getInstance().getCurrentUserId())) || (isListened))
/* 420 */             messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_text_color_secondary)), 0, messageData.length(), 33);
/*     */           else {
/* 422 */             messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_voice_color)), 0, messageData.length(), 33);
/*     */           }
/*     */         }
/* 425 */         builder.append(userInfo.getName()).append(" : ").append(messageData);
/* 426 */         uiConversation.setConversationContent(builder);
/* 427 */       } else if (uiConversation.getConversationTargetId().equals(userInfo.getUserId())) {
/* 428 */         if (type.equals(Conversation.ConversationType.PRIVATE.getName())) {
/* 429 */           uiConversation.setUIConversationTitle(userInfo.getName());
/* 430 */           uiConversation.setIconUrl(userInfo.getPortraitUri());
/*     */         } else {
/* 432 */           SpannableStringBuilder builder = new SpannableStringBuilder();
/* 433 */           if ((uiConversation.getMessageContent() instanceof VoiceMessage)) {
/* 434 */             boolean isListened = RongIM.getInstance().getConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId()).getReceivedStatus().isListened();
/*     */ 
/* 436 */             if ((uiConversation.getConversationSenderId().equals(RongIMClient.getInstance().getCurrentUserId())) || (isListened))
/* 437 */               messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_text_color_secondary)), 0, messageData.length(), 33);
/*     */             else {
/* 439 */               messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_voice_color)), 0, messageData.length(), 33);
/*     */             }
/*     */           }
/* 442 */           builder.append(userInfo.getName()).append(" : ").append(messageData);
/* 443 */           uiConversation.setConversationContent(builder);
/* 444 */           uiConversation.setIconUrl(userInfo.getPortraitUri());
/*     */         }
/*     */       }
/* 447 */       this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(RongIMClient.ConnectionStatusListener.ConnectionStatus status)
/*     */   {
/* 453 */     RLog.d("SubConversationListFragment", "ConnectionStatus = " + status.toString());
/*     */ 
/* 455 */     if ((isResumed()) && (getResources().getBoolean(R.bool.rc_is_show_warning_notification)))
/*     */     {
/* 457 */       setNotificationBarVisibility(status);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setNotificationBarVisibility(RongIMClient.ConnectionStatusListener.ConnectionStatus status) {
/* 462 */     if (!getResources().getBoolean(R.bool.rc_is_show_warning_notification)) {
/* 463 */       RLog.e("SubConversationListFragment", "rc_is_show_warning_notification is disabled.");
/* 464 */       return;
/*     */     }
/*     */ 
/* 467 */     String content = null;
/* 468 */     if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE))
/* 469 */       content = getResources().getString(R.string.rc_notice_network_unavailable);
/* 470 */     else if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT))
/* 471 */       content = getResources().getString(R.string.rc_notice_tick);
/* 472 */     else if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED))
/* 473 */       this.mNotificationBar.setVisibility(8);
/* 474 */     else if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED))
/* 475 */       content = getResources().getString(R.string.rc_notice_network_unavailable);
/* 476 */     else if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING)) {
/* 477 */       content = getResources().getString(R.string.rc_notice_connecting);
/*     */     }
/* 479 */     if (content != null)
/* 480 */       if (this.mNotificationBar.getVisibility() == 8) {
/* 481 */         String text = content;
/* 482 */         getHandler().postDelayed(new Runnable(text)
/*     */         {
/*     */           public void run() {
/* 485 */             if (!RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
/* 486 */               SubConversationListFragment.this.mNotificationBar.setVisibility(0);
/* 487 */               SubConversationListFragment.this.mNotificationBarText.setText(this.val$text);
/* 488 */               if (RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING))
/* 489 */                 SubConversationListFragment.this.mNotificationBarImage.setImageDrawable(SubConversationListFragment.this.getResources().getDrawable(R.drawable.rc_notification_connecting_animated));
/*     */               else
/* 491 */                 SubConversationListFragment.this.mNotificationBarImage.setImageDrawable(SubConversationListFragment.this.getResources().getDrawable(R.drawable.rc_notification_network_available));
/*     */             }
/*     */           }
/*     */         }
/*     */         , 4000L);
/*     */       }
/*     */       else
/*     */       {
/* 497 */         this.mNotificationBarText.setText(content);
/* 498 */         if (RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING))
/* 499 */           this.mNotificationBarImage.setImageDrawable(getResources().getDrawable(R.drawable.rc_notification_connecting_animated));
/*     */         else
/* 501 */           this.mNotificationBarImage.setImageDrawable(getResources().getDrawable(R.drawable.rc_notification_network_available));
/*     */       }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Discussion discussion)
/*     */   {
/* 508 */     int count = this.mAdapter.getCount();
/*     */ 
/* 510 */     for (int i = 0; i < count; i++) {
/* 511 */       UIConversation temp = (UIConversation)this.mAdapter.getItem(i);
/* 512 */       boolean gatherState = RongContext.getInstance().getConversationGatherState(temp.getConversationType().getName()).booleanValue();
/* 513 */       if (temp.getConversationTargetId().equals(discussion.getId())) {
/* 514 */         temp.setUIConversationTitle(discussion.getName());
/* 515 */         this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
/* 516 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(PublicServiceProfile accountInfo) {
/* 522 */     int count = this.mAdapter.getCount();
/*     */ 
/* 524 */     for (int i = 0; i < count; i++)
/* 525 */       if (((UIConversation)this.mAdapter.getItem(i)).getConversationTargetId().equals(accountInfo.getTargetId())) {
/* 526 */         ((UIConversation)this.mAdapter.getItem(i)).setIconUrl(accountInfo.getPortraitUri());
/* 527 */         ((UIConversation)this.mAdapter.getItem(i)).setUIConversationTitle(accountInfo.getName());
/* 528 */         this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
/* 529 */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.ConversationUnreadEvent unreadEvent)
/*     */   {
/* 535 */     int targetIndex = this.mAdapter.findPosition(unreadEvent.getType(), unreadEvent.getTargetId());
/*     */ 
/* 537 */     if (targetIndex >= 0) {
/* 538 */       UIConversation temp = (UIConversation)this.mAdapter.getItem(targetIndex);
/* 539 */       temp.setUnReadMessageCount(0);
/* 540 */       this.mAdapter.getView(targetIndex, this.mList.getChildAt(targetIndex - this.mList.getFirstVisiblePosition()), this.mList);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.ConversationTopEvent setTopEvent) throws IllegalAccessException {
/* 545 */     int originalIndex = this.mAdapter.findPosition(setTopEvent.getConversationType(), setTopEvent.getTargetId());
/*     */ 
/* 547 */     if (originalIndex >= 0) {
/* 548 */       UIConversation temp = (UIConversation)this.mAdapter.getItem(originalIndex);
/* 549 */       boolean originalValue = temp.isTop();
/*     */       int newIndex;
/*     */       int newIndex;
/* 552 */       if (originalValue == true) {
/* 553 */         temp.setTop(false);
/* 554 */         newIndex = ConversationListUtils.findPositionForCancleTop(originalIndex, this.mAdapter);
/*     */       } else {
/* 556 */         temp.setTop(true);
/* 557 */         newIndex = ConversationListUtils.findPositionForSetTop(temp, this.mAdapter);
/*     */       }
/* 559 */       if (originalIndex == newIndex) {
/* 560 */         this.mAdapter.getView(newIndex, this.mList.getChildAt(newIndex - this.mList.getFirstVisiblePosition()), this.mList);
/*     */       } else {
/* 562 */         this.mAdapter.remove(originalIndex);
/* 563 */         this.mAdapter.add(temp, newIndex);
/* 564 */         this.mAdapter.notifyDataSetChanged();
/*     */       }
/*     */     } else {
/* 567 */       throw new IllegalAccessException("the item has already been deleted!");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.ConversationRemoveEvent removeEvent) {
/* 572 */     int originalIndex = this.mAdapter.findPosition(removeEvent.getType(), removeEvent.getTargetId());
/* 573 */     if (originalIndex >= 0) {
/* 574 */       this.mAdapter.remove(originalIndex);
/* 575 */       this.mAdapter.notifyDataSetChanged();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.ClearConversationEvent clearConversationEvent) {
/* 580 */     List typeList = clearConversationEvent.getTypes();
/* 581 */     for (int i = this.mAdapter.getCount() - 1; i >= 0; i--) {
/* 582 */       if (typeList.indexOf(((UIConversation)this.mAdapter.getItem(i)).getConversationType()) >= 0) {
/* 583 */         this.mAdapter.remove(i);
/*     */       }
/*     */     }
/* 586 */     this.mAdapter.notifyDataSetChanged();
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.ConversationNotificationEvent notificationEvent) {
/* 590 */     int originalIndex = this.mAdapter.findPosition(notificationEvent.getConversationType(), notificationEvent.getTargetId());
/*     */ 
/* 592 */     if (originalIndex >= 0)
/* 593 */       this.mAdapter.getView(originalIndex, this.mList.getChildAt(originalIndex - this.mList.getFirstVisiblePosition()), this.mList);
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.MessagesClearEvent clearMessagesEvent)
/*     */   {
/* 598 */     int originalIndex = this.mAdapter.findPosition(clearMessagesEvent.getType(), clearMessagesEvent.getTargetId());
/*     */ 
/* 600 */     if ((clearMessagesEvent != null) && (originalIndex >= 0)) {
/* 601 */       Conversation temp = RongIMClient.getInstance().getConversation(clearMessagesEvent.getType(), clearMessagesEvent.getTargetId());
/* 602 */       UIConversation uiConversation = UIConversation.obtain(temp, false);
/* 603 */       this.mAdapter.remove(originalIndex);
/* 604 */       this.mAdapter.add(UIConversation.obtain(temp, false), ConversationListUtils.findPositionForNewConversation(uiConversation, this.mAdapter));
/* 605 */       this.mAdapter.notifyDataSetChanged();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.MessageDeleteEvent event)
/*     */   {
/* 611 */     int count = this.mAdapter.getCount();
/* 612 */     for (int i = 0; i < count; i++)
/* 613 */       if (event.getMessageIds().contains(Integer.valueOf(((UIConversation)this.mAdapter.getItem(i)).getLatestMessageId()))) {
/* 614 */         int index = i;
/* 615 */         RongIM.getInstance().getConversation(((UIConversation)this.mAdapter.getItem(index)).getConversationType(), ((UIConversation)this.mAdapter.getItem(index)).getConversationTargetId(), new RongIMClient.ResultCallback(index)
/*     */         {
/*     */           public void onSuccess(Conversation conversation)
/*     */           {
/* 619 */             if (conversation == null) {
/* 620 */               RLog.d("SubConversationListFragment", "onEventMainThread getConversation : onSuccess, conversation = null");
/* 621 */               return;
/*     */             }
/* 623 */             UIConversation temp = UIConversation.obtain(conversation, false);
/* 624 */             SubConversationListFragment.this.mAdapter.remove(this.val$index);
/* 625 */             int newPosition = ConversationListUtils.findPositionForNewConversation(temp, SubConversationListFragment.this.mAdapter);
/* 626 */             SubConversationListFragment.this.mAdapter.add(temp, newPosition);
/* 627 */             SubConversationListFragment.this.mAdapter.notifyDataSetChanged();
/*     */           }
/*     */ 
/*     */           public void onError(RongIMClient.ErrorCode e)
/*     */           {
/*     */           }
/*     */         });
/* 634 */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.MessageRecallEvent event)
/*     */   {
/* 640 */     int count = this.mAdapter.getCount();
/* 641 */     for (int i = 0; i < count; i++)
/* 642 */       if (event.getMessageId() == ((UIConversation)this.mAdapter.getItem(i)).getLatestMessageId()) {
/* 643 */         int index = i;
/* 644 */         RongIM.getInstance().getConversation(((UIConversation)this.mAdapter.getItem(index)).getConversationType(), ((UIConversation)this.mAdapter.getItem(index)).getConversationTargetId(), new RongIMClient.ResultCallback(index)
/*     */         {
/*     */           public void onSuccess(Conversation conversation)
/*     */           {
/* 648 */             if (conversation == null) {
/* 649 */               RLog.d("SubConversationListFragment", "onEventMainThread getConversation : onSuccess, conversation = null");
/* 650 */               return;
/*     */             }
/* 652 */             UIConversation temp = UIConversation.obtain(conversation, false);
/* 653 */             SubConversationListFragment.this.mAdapter.remove(this.val$index);
/* 654 */             int newPosition = ConversationListUtils.findPositionForNewConversation(temp, SubConversationListFragment.this.mAdapter);
/* 655 */             SubConversationListFragment.this.mAdapter.add(temp, newPosition);
/* 656 */             SubConversationListFragment.this.mAdapter.notifyDataSetChanged();
/*     */           }
/*     */ 
/*     */           public void onError(RongIMClient.ErrorCode e)
/*     */           {
/*     */           }
/*     */         });
/* 663 */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.RemoteMessageRecallEvent event)
/*     */   {
/* 669 */     int count = this.mAdapter.getCount();
/* 670 */     for (int i = 0; i < count; i++)
/* 671 */       if (event.getMessageId() == ((UIConversation)this.mAdapter.getItem(i)).getLatestMessageId()) {
/* 672 */         int index = i;
/* 673 */         RongIM.getInstance().getConversation(((UIConversation)this.mAdapter.getItem(index)).getConversationType(), ((UIConversation)this.mAdapter.getItem(index)).getConversationTargetId(), new RongIMClient.ResultCallback(index)
/*     */         {
/*     */           public void onSuccess(Conversation conversation)
/*     */           {
/* 677 */             if (conversation == null) {
/* 678 */               RLog.d("SubConversationListFragment", "onEventMainThread getConversation : onSuccess, conversation = null");
/* 679 */               return;
/*     */             }
/* 681 */             UIConversation temp = UIConversation.obtain(conversation, false);
/* 682 */             SubConversationListFragment.this.mAdapter.remove(this.val$index);
/* 683 */             int newPosition = ConversationListUtils.findPositionForNewConversation(temp, SubConversationListFragment.this.mAdapter);
/* 684 */             SubConversationListFragment.this.mAdapter.add(temp, newPosition);
/* 685 */             SubConversationListFragment.this.mAdapter.notifyDataSetChanged();
/*     */           }
/*     */ 
/*     */           public void onError(RongIMClient.ErrorCode e)
/*     */           {
/*     */           }
/*     */         });
/* 692 */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.OnMessageSendErrorEvent sendErrorEvent)
/*     */   {
/* 698 */     int index = this.mAdapter.findPosition(sendErrorEvent.getMessage().getConversationType(), sendErrorEvent.getMessage().getTargetId());
/*     */ 
/* 700 */     if (index >= 0) {
/* 701 */       ((UIConversation)this.mAdapter.getItem(index)).setSentStatus(Message.SentStatus.FAILED);
/* 702 */       this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition()), this.mList);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.QuitDiscussionEvent event) {
/* 707 */     int index = this.mAdapter.findPosition(Conversation.ConversationType.DISCUSSION, event.getDiscussionId());
/*     */ 
/* 709 */     if (index >= 0) {
/* 710 */       this.mAdapter.remove(index);
/* 711 */       this.mAdapter.notifyDataSetChanged();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.QuitGroupEvent event) {
/* 716 */     int index = this.mAdapter.findPosition(Conversation.ConversationType.GROUP, event.getGroupId());
/*     */ 
/* 718 */     if (index >= 0) {
/* 719 */       this.mAdapter.remove(index);
/* 720 */       this.mAdapter.notifyDataSetChanged();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.AudioListenedEvent event) {
/* 725 */     int originalIndex = this.mAdapter.findPosition(event.getConversationType(), event.getTargetId());
/*     */ 
/* 727 */     if (originalIndex >= 0) {
/* 728 */       UIConversation temp = (UIConversation)this.mAdapter.getItem(originalIndex);
/* 729 */       if (temp.getLatestMessageId() == event.getLatestMessageId()) {
/* 730 */         Spannable content = RongContext.getInstance().getMessageTemplate(temp.getMessageContent().getClass()).getContentSummary(temp.getMessageContent());
/* 731 */         boolean isListened = RongIM.getInstance().getConversation(event.getConversationType(), event.getTargetId()).getReceivedStatus().isListened();
/* 732 */         if (isListened)
/* 733 */           content.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_text_color_secondary)), 0, content.length(), 33);
/*     */         else {
/* 735 */           content.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_voice_color)), 0, content.length(), 33);
/*     */         }
/* 737 */         temp.setConversationContent(content);
/*     */       }
/* 739 */       this.mAdapter.getView(originalIndex, this.mList.getChildAt(originalIndex - this.mList.getFirstVisiblePosition()), this.mList);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onItemClick(AdapterView<?> parent, View view, int position, long id)
/*     */   {
/* 745 */     UIConversation uiconversation = (UIConversation)this.mAdapter.getItem(position);
/*     */ 
/* 747 */     if (RongContext.getInstance().getConversationListBehaviorListener() != null) {
/* 748 */       boolean isDefault = RongContext.getInstance().getConversationListBehaviorListener().onConversationClick(getActivity(), view, uiconversation);
/* 749 */       if (isDefault == true) {
/* 750 */         return;
/*     */       }
/*     */     }
/* 753 */     Conversation.ConversationType type = uiconversation.getConversationType();
/* 754 */     uiconversation.setUnReadMessageCount(0);
/* 755 */     RongIM.getInstance().startConversation(getActivity(), type, uiconversation.getConversationTargetId(), uiconversation.getUIConversationTitle());
/*     */   }
/*     */ 
/*     */   public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
/*     */   {
/* 760 */     UIConversation uiConversation = (UIConversation)this.mAdapter.getItem(position);
/* 761 */     String title = uiConversation.getUIConversationTitle();
/*     */ 
/* 763 */     if (RongContext.getInstance().getConversationListBehaviorListener() != null) {
/* 764 */       boolean isDealt = RongContext.getInstance().getConversationListBehaviorListener().onConversationLongClick(getActivity(), view, uiConversation);
/* 765 */       if (isDealt) {
/* 766 */         return true;
/*     */       }
/*     */     }
/* 769 */     AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
/* 770 */     builder.setTitle(title);
/*     */ 
/* 772 */     buildMultiDialog(uiConversation);
/* 773 */     return true;
/*     */   }
/*     */ 
/*     */   private void buildMultiDialog(UIConversation uiConversation) {
/* 777 */     String[] items = new String[2];
/* 778 */     if (uiConversation.isTop())
/* 779 */       items[0] = getActivity().getString(R.string.rc_conversation_list_dialog_cancel_top);
/*     */     else
/* 781 */       items[0] = getActivity().getString(R.string.rc_conversation_list_dialog_set_top);
/* 782 */     items[1] = getActivity().getString(R.string.rc_conversation_list_dialog_remove);
/*     */ 
/* 784 */     ArraysDialogFragment.newInstance(uiConversation.getUIConversationTitle(), items).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener(uiConversation)
/*     */     {
/*     */       public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
/* 787 */         if (which == 0)
/* 788 */           RongIM.getInstance().setConversationToTop(this.val$uiConversation.getConversationType(), this.val$uiConversation.getConversationTargetId(), !this.val$uiConversation.isTop(), new RongIMClient.ResultCallback()
/*     */           {
/*     */             public void onSuccess(Boolean aBoolean)
/*     */             {
/* 792 */               if (SubConversationListFragment.8.this.val$uiConversation.isTop() == true)
/* 793 */                 Toast.makeText(RongContext.getInstance(), SubConversationListFragment.this.getString(R.string.rc_conversation_list_popup_cancel_top), 0).show();
/*     */               else
/* 795 */                 Toast.makeText(RongContext.getInstance(), SubConversationListFragment.this.getString(R.string.rc_conversation_list_dialog_set_top), 0).show();
/*     */             }
/*     */ 
/*     */             public void onError(RongIMClient.ErrorCode e)
/*     */             {
/*     */             }
/*     */           });
/* 804 */         else if (which == 1)
/* 805 */           RongIM.getInstance().removeConversation(this.val$uiConversation.getConversationType(), this.val$uiConversation.getConversationTargetId());
/*     */       }
/*     */     }).show(getFragmentManager());
/*     */   }
/*     */ 
/*     */   public boolean onBackPressed()
/*     */   {
/* 815 */     return false;
/*     */   }
/*     */ 
/*     */   public void onDestroy()
/*     */   {
/* 820 */     RLog.d("SubConversationListFragment", "onDestroy");
/* 821 */     RongContext.getInstance().getEventBus().unregister(this);
/* 822 */     getHandler().removeCallbacksAndMessages(null);
/* 823 */     super.onDestroy();
/*     */   }
/*     */ 
/*     */   public void onPause()
/*     */   {
/* 828 */     RLog.d("SubConversationListFragment", "onPause");
/* 829 */     super.onPause();
/*     */   }
/*     */ 
/*     */   public void setAdapter(SubConversationListAdapter adapter) {
/* 833 */     if (this.mAdapter != null)
/* 834 */       this.mAdapter.clear();
/* 835 */     this.mAdapter = adapter;
/* 836 */     if ((this.mList != null) && (getUri() != null)) {
/* 837 */       this.mList.setAdapter(adapter);
/* 838 */       initFragment(getUri());
/*     */     }
/*     */   }
/*     */ 
/*     */   public SubConversationListAdapter getAdapter() {
/* 843 */     return this.mAdapter;
/*     */   }
/*     */ 
/*     */   private UIConversation makeUiConversation(Message message, int pos) {
/* 847 */     UIConversation uiConversation = null;
/*     */ 
/* 850 */     if (pos >= 0) {
/* 851 */       uiConversation = (UIConversation)this.mAdapter.getItem(pos);
/* 852 */       if (uiConversation != null) {
/* 853 */         uiConversation.setMessageContent(message.getContent());
/* 854 */         if (message.getMessageDirection() == Message.MessageDirection.SEND) {
/* 855 */           uiConversation.setUIConversationTime(message.getSentTime());
/* 856 */           uiConversation.setConversationSenderId(RongIM.getInstance().getCurrentUserId());
/*     */         } else {
/* 858 */           uiConversation.setUIConversationTime(message.getSentTime());
/* 859 */           uiConversation.setConversationSenderId(message.getSenderUserId());
/*     */         }
/* 861 */         uiConversation.setConversationTargetId(message.getTargetId());
/* 862 */         uiConversation.setConversationContent(uiConversation.buildConversationContent(uiConversation));
/* 863 */         uiConversation.setSentStatus(message.getSentStatus());
/* 864 */         uiConversation.setLatestMessageId(message.getMessageId());
/*     */ 
/* 866 */         if (!uiConversation.getMentionedFlag()) {
/* 867 */           MentionedInfo mentionedInfo = message.getContent().getMentionedInfo();
/* 868 */           if ((mentionedInfo != null) && (((mentionedInfo.getType().equals(MentionedInfo.MentionedType.PART)) && (mentionedInfo.getMentionedUserIdList().contains(RongIMClient.getInstance().getCurrentUserId()))) || (mentionedInfo.getType().equals(MentionedInfo.MentionedType.ALL))))
/*     */           {
/* 871 */             uiConversation.setMentionedFlag(true);
/*     */           }
/*     */         }
/*     */ 
/* 875 */         MessageTag tag = (MessageTag)message.getContent().getClass().getAnnotation(MessageTag.class);
/* 876 */         if ((message.getMessageDirection() == Message.MessageDirection.RECEIVE) && ((tag.flag() & 0x3) != 0)) {
/* 877 */           uiConversation.setUnReadMessageCount(uiConversation.getUnReadMessageCount() + 1);
/* 878 */           List infoList = RongContext.getInstance().getCurrentConversationList();
/* 879 */           for (ConversationInfo info : infoList)
/* 880 */             if ((info != null) && (info.getConversationType().equals(message.getConversationType())) && (info.getTargetId().equals(message.getTargetId())))
/* 881 */               uiConversation.setUnReadMessageCount(0);
/*     */         }
/*     */         else {
/* 884 */           uiConversation.setUnReadMessageCount(0);
/*     */         }
/*     */       }
/*     */     }
/* 888 */     return uiConversation;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.fragment.SubConversationListFragment
 * JD-Core Version:    0.6.0
 */
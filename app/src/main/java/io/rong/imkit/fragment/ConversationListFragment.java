/*      */ package io.rong.imkit.fragment;
/*      */ 
/*      */ import android.app.Activity;
/*      */ import android.content.DialogInterface;
/*      */ import android.content.res.Resources;
/*      */ import android.net.Uri;
/*      */ import android.os.Bundle;
/*      */ import android.os.Handler;
/*      */ import android.text.Spannable;
/*      */ import android.text.SpannableStringBuilder;
/*      */ import android.text.TextUtils;
/*      */ import android.text.style.ForegroundColorSpan;
/*      */ import android.view.LayoutInflater;
/*      */ import android.view.View;
/*      */ import android.view.ViewGroup;
/*      */ import android.widget.AdapterView;
/*      */ import android.widget.AdapterView.OnItemClickListener;
/*      */ import android.widget.AdapterView.OnItemLongClickListener;
/*      */ import android.widget.ImageView;
/*      */ import android.widget.LinearLayout;
/*      */ import android.widget.ListView;
/*      */ import android.widget.TextView;
/*      */ import android.widget.Toast;
/*      */ import io.rong.common.RLog;
/*      */ import io.rong.eventbus.EventBus;
/*      */ import io.rong.imkit.R.bool;
/*      */ import io.rong.imkit.R.color;
/*      */ import io.rong.imkit.R.drawable;
/*      */ import io.rong.imkit.R.id;
/*      */ import io.rong.imkit.R.layout;
/*      */ import io.rong.imkit.R.string;
/*      */ import io.rong.imkit.RongContext;
/*      */ import io.rong.imkit.RongIM;
/*      */ import io.rong.imkit.RongIM.ConversationListBehaviorListener;
/*      */ import io.rong.imkit.model.ConversationKey;
/*      */ import io.rong.imkit.model.Draft;
/*      */ import io.rong.imkit.model.Event.AudioListenedEvent;
/*      */ import io.rong.imkit.model.Event.ClearConversationEvent;
/*      */ import io.rong.imkit.model.Event.ConnectEvent;
/*      */ import io.rong.imkit.model.Event.ConversationNotificationEvent;
/*      */ import io.rong.imkit.model.Event.ConversationRemoveEvent;
/*      */ import io.rong.imkit.model.Event.ConversationTopEvent;
/*      */ import io.rong.imkit.model.Event.ConversationUnreadEvent;
/*      */ import io.rong.imkit.model.Event.CreateDiscussionEvent;
/*      */ import io.rong.imkit.model.Event.GroupUserInfoEvent;
/*      */ import io.rong.imkit.model.Event.MessageDeleteEvent;
/*      */ import io.rong.imkit.model.Event.MessageRecallEvent;
/*      */ import io.rong.imkit.model.Event.MessagesClearEvent;
/*      */ import io.rong.imkit.model.Event.OnMessageSendErrorEvent;
/*      */ import io.rong.imkit.model.Event.OnReceiveMessageEvent;
/*      */ import io.rong.imkit.model.Event.PublicServiceFollowableEvent;
/*      */ import io.rong.imkit.model.Event.QuitDiscussionEvent;
/*      */ import io.rong.imkit.model.Event.QuitGroupEvent;
/*      */ import io.rong.imkit.model.Event.ReadReceiptEvent;
/*      */ import io.rong.imkit.model.Event.RemoteMessageRecallEvent;
/*      */ import io.rong.imkit.model.Event.SyncReadStatusEvent;
/*      */ import io.rong.imkit.model.GroupUserInfo;
/*      */ import io.rong.imkit.model.ProviderTag;
/*      */ import io.rong.imkit.model.UIConversation;
/*      */ import io.rong.imkit.utils.ConversationListUtils;
/*      */ import io.rong.imkit.widget.ArraysDialogFragment;
/*      */ import io.rong.imkit.widget.ArraysDialogFragment.OnArraysDialogItemListener;
/*      */ import io.rong.imkit.widget.adapter.ConversationListAdapter;
/*      */ import io.rong.imkit.widget.adapter.ConversationListAdapter.OnPortraitItemClick;
/*      */ import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
/*      */ import io.rong.imlib.RongIMClient;
/*      */ import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
/*      */ import io.rong.imlib.RongIMClient.ErrorCode;
/*      */ import io.rong.imlib.RongIMClient.ResultCallback;
/*      */ import io.rong.imlib.model.Conversation;
/*      */ import io.rong.imlib.model.Conversation.ConversationType;
/*      */ import io.rong.imlib.model.Discussion;
/*      */ import io.rong.imlib.model.Group;
/*      */ import io.rong.imlib.model.MentionedInfo;
/*      */ import io.rong.imlib.model.MentionedInfo.MentionedType;
/*      */ import io.rong.imlib.model.Message;
/*      */ import io.rong.imlib.model.Message.MessageDirection;
/*      */ import io.rong.imlib.model.Message.ReceivedStatus;
/*      */ import io.rong.imlib.model.Message.SentStatus;
/*      */ import io.rong.imlib.model.MessageContent;
/*      */ import io.rong.imlib.model.PublicServiceProfile;
/*      */ import io.rong.imlib.model.UserInfo;
/*      */ import io.rong.message.ReadReceiptMessage;
/*      */ import io.rong.message.RecallNotificationMessage;
/*      */ import io.rong.message.VoiceMessage;
/*      */ import io.rong.push.RongPushClient;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ 
/*      */ public class ConversationListFragment extends UriFragment
/*      */   implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, ConversationListAdapter.OnPortraitItemClick
/*      */ {
/*   55 */   private static String TAG = "ConversationListFragment";
/*      */   private ConversationListAdapter mAdapter;
/*      */   private ListView mList;
/*      */   private LinearLayout mNotificationBar;
/*      */   private ImageView mNotificationBarImage;
/*      */   private TextView mNotificationBarText;
/*   61 */   private boolean isShowWithoutConnected = false;
/*   62 */   private ArrayList<Conversation.ConversationType> mSupportConversationList = new ArrayList();
/*   63 */   private ArrayList<Message> mMessageCache = new ArrayList();
/*      */   private Set<ConversationKey> mConversationKeyList;
/*   70 */   private RongIMClient.ResultCallback<List<Conversation>> mCallback = new RongIMClient.ResultCallback()
/*      */   {
/*      */     public void onSuccess(List<Conversation> conversations) {
/*   73 */       RLog.d(ConversationListFragment.TAG, new StringBuilder().append("ConversationListFragment initFragment onSuccess callback : list = ").append(conversations != null ? Integer.valueOf(conversations.size()) : "null").toString());
/*      */ 
/*   76 */       if ((ConversationListFragment.this.mAdapter != null) && (ConversationListFragment.this.mAdapter.getCount() != 0)) {
/*   77 */         ConversationListFragment.this.mAdapter.clear();
/*      */       }
/*      */ 
/*   80 */       if ((conversations == null) || (conversations.size() == 0)) {
/*   81 */         if (ConversationListFragment.this.mAdapter != null)
/*   82 */           ConversationListFragment.this.mAdapter.notifyDataSetChanged();
/*   83 */         return;
/*      */       }
/*      */ 
/*   86 */       if (ConversationListFragment.this.mAdapter == null) {
/*   87 */         ConversationListFragment.access$102(ConversationListFragment.this, new ConversationListAdapter(RongContext.getInstance()));
/*      */       }
/*   89 */       ConversationListFragment.this.makeUiConversationList(conversations);
/*      */ 
/*   91 */       if ((ConversationListFragment.this.mList != null) && (ConversationListFragment.this.mList.getAdapter() != null))
/*   92 */         ConversationListFragment.this.mAdapter.notifyDataSetChanged();
/*      */     }
/*      */ 
/*      */     public void onError(RongIMClient.ErrorCode e)
/*      */     {
/*   98 */       RLog.d(ConversationListFragment.TAG, new StringBuilder().append("ConversationListFragment initFragment onError callback, e=").append(e).toString());
/*   99 */       if (e.equals(RongIMClient.ErrorCode.IPC_DISCONNECT))
/*  100 */         ConversationListFragment.access$402(ConversationListFragment.this, true);
/*      */     }
/*   70 */   };
/*      */ 
/*      */   public static ConversationListFragment getInstance()
/*      */   {
/*   67 */     return new ConversationListFragment();
/*      */   }
/*      */ 
/*      */   public void onCreate(Bundle savedInstanceState)
/*      */   {
/*  107 */     RLog.d(TAG, "ConversationListFragment onCreate");
/*  108 */     super.onCreate(savedInstanceState);
/*      */ 
/*  110 */     RongPushClient.clearAllPushNotifications(getActivity());
/*  111 */     this.mSupportConversationList.clear();
/*  112 */     this.mConversationKeyList = new HashSet();
/*      */ 
/*  114 */     RongContext.getInstance().getEventBus().register(this);
/*      */   }
/*      */ 
/*      */   public void onAttach(Activity activity)
/*      */   {
/*  119 */     RLog.d(TAG, "ConversationListFragment onAttach");
/*  120 */     super.onAttach(activity);
/*      */   }
/*      */ 
/*      */   protected void initFragment(Uri uri)
/*      */   {
/*  128 */     Conversation.ConversationType[] conversationType = { Conversation.ConversationType.PRIVATE, Conversation.ConversationType.GROUP, Conversation.ConversationType.DISCUSSION, Conversation.ConversationType.SYSTEM, Conversation.ConversationType.CUSTOMER_SERVICE, Conversation.ConversationType.CHATROOM, Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE };
/*      */ 
/*  135 */     RLog.d(TAG, "ConversationListFragment initFragment");
/*      */ 
/*  137 */     if (uri == null) {
/*  138 */       RongIM.getInstance().getConversationList(this.mCallback);
/*  139 */       return;
/*      */     }
/*      */ 
/*  142 */     for (Conversation.ConversationType type : conversationType) {
/*  143 */       if (uri.getQueryParameter(type.getName()) != null) {
/*  144 */         this.mSupportConversationList.add(type);
/*      */ 
/*  146 */         if ("true".equals(uri.getQueryParameter(type.getName())))
/*  147 */           RongContext.getInstance().setConversationGatherState(type.getName(), Boolean.valueOf(true));
/*  148 */         else if ("false".equals(uri.getQueryParameter(type.getName()))) {
/*  149 */           RongContext.getInstance().setConversationGatherState(type.getName(), Boolean.valueOf(false));
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  154 */     if (RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
/*  155 */       RLog.d(TAG, "RongCloud haven't been connected yet, so the conversation list display blank !!!");
/*  156 */       this.isShowWithoutConnected = true;
/*      */     }
/*      */ 
/*  159 */     if (this.mSupportConversationList.size() > 0)
/*  160 */       RongIM.getInstance().getConversationList(this.mCallback, (Conversation.ConversationType[])this.mSupportConversationList.toArray(new Conversation.ConversationType[this.mSupportConversationList.size()]));
/*      */     else
/*  162 */       RongIM.getInstance().getConversationList(this.mCallback);
/*      */   }
/*      */ 
/*      */   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
/*      */   {
/*  167 */     RLog.d(TAG, "onCreateView");
/*  168 */     View view = inflater.inflate(R.layout.rc_fr_conversationlist, container, false);
/*  169 */     this.mNotificationBar = ((LinearLayout)findViewById(view, R.id.rc_status_bar));
/*  170 */     this.mNotificationBar.setVisibility(8);
/*  171 */     this.mNotificationBarImage = ((ImageView)findViewById(view, R.id.rc_status_bar_image));
/*  172 */     this.mNotificationBarText = ((TextView)findViewById(view, R.id.rc_status_bar_text));
/*      */ 
/*  174 */     this.mList = ((ListView)findViewById(view, R.id.rc_list));
/*      */ 
/*  176 */     LinearLayout emptyView = (LinearLayout)findViewById(view, R.id.rc_conversation_list_empty_layout);
/*  177 */     TextView textView = (TextView)findViewById(view, R.id.rc_empty_tv);
/*  178 */     RongIMClient.ConnectionStatusListener.ConnectionStatus status = RongIM.getInstance().getCurrentConnectionStatus();
/*  179 */     if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED))
/*  180 */       textView.setText(RongContext.getInstance().getResources().getString(R.string.rc_conversation_list_not_connected));
/*      */     else {
/*  182 */       textView.setText(RongContext.getInstance().getResources().getString(R.string.rc_conversation_list_empty_prompt));
/*      */     }
/*  184 */     setNotificationBarVisibility(status);
/*      */ 
/*  186 */     this.mList.setEmptyView(emptyView);
/*      */ 
/*  188 */     return view;
/*      */   }
/*      */ 
/*      */   public void onViewCreated(View view, Bundle savedInstanceState)
/*      */   {
/*  193 */     if (this.mAdapter == null)
/*  194 */       this.mAdapter = new ConversationListAdapter(RongContext.getInstance());
/*  195 */     this.mList.setAdapter(this.mAdapter);
/*  196 */     this.mList.setOnItemClickListener(this);
/*  197 */     this.mList.setOnItemLongClickListener(this);
/*  198 */     this.mAdapter.setOnPortraitItemClick(this);
/*  199 */     super.onViewCreated(view, savedInstanceState);
/*      */   }
/*      */ 
/*      */   public void onResume()
/*      */   {
/*  204 */     super.onResume();
/*      */ 
/*  206 */     if (!RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
/*  207 */       RLog.d(TAG, "RongCloud haven't been connected yet, so the conversation list display blank !!!");
/*  208 */       this.isShowWithoutConnected = true;
/*  209 */       return;
/*      */     }
/*  211 */     RLog.d(TAG, "onResume current connect status is:" + RongIM.getInstance().getCurrentConnectionStatus());
/*  212 */     RongPushClient.clearAllPushNotifications(getActivity());
/*  213 */     RongIMClient.ConnectionStatusListener.ConnectionStatus status = RongIM.getInstance().getCurrentConnectionStatus();
/*  214 */     setNotificationBarVisibility(status);
/*      */   }
/*      */ 
/*      */   private void setNotificationBarVisibility(RongIMClient.ConnectionStatusListener.ConnectionStatus status) {
/*  218 */     if (!getResources().getBoolean(R.bool.rc_is_show_warning_notification)) {
/*  219 */       RLog.e(TAG, "rc_is_show_warning_notification is disabled.");
/*  220 */       return;
/*      */     }
/*      */ 
/*  223 */     String content = null;
/*  224 */     if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE))
/*  225 */       content = getResources().getString(R.string.rc_notice_network_unavailable);
/*  226 */     else if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT))
/*  227 */       content = getResources().getString(R.string.rc_notice_tick);
/*  228 */     else if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED))
/*  229 */       this.mNotificationBar.setVisibility(8);
/*  230 */     else if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED))
/*  231 */       content = getResources().getString(R.string.rc_notice_disconnect);
/*  232 */     else if (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING)) {
/*  233 */       content = getResources().getString(R.string.rc_notice_connecting);
/*      */     }
/*  235 */     if (content != null)
/*  236 */       if (this.mNotificationBar.getVisibility() == 8) {
/*  237 */         String text = content;
/*  238 */         getHandler().postDelayed(new Runnable(text)
/*      */         {
/*      */           public void run() {
/*  241 */             if (!RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
/*  242 */               ConversationListFragment.this.mNotificationBar.setVisibility(0);
/*  243 */               ConversationListFragment.this.mNotificationBarText.setText(this.val$text);
/*  244 */               if (RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING))
/*  245 */                 ConversationListFragment.this.mNotificationBarImage.setImageDrawable(ConversationListFragment.this.getResources().getDrawable(R.drawable.rc_notification_connecting_animated));
/*      */               else
/*  247 */                 ConversationListFragment.this.mNotificationBarImage.setImageDrawable(ConversationListFragment.this.getResources().getDrawable(R.drawable.rc_notification_network_available));
/*      */             }
/*      */           }
/*      */         }
/*      */         , 4000L);
/*      */       }
/*      */       else
/*      */       {
/*  253 */         this.mNotificationBarText.setText(content);
/*  254 */         if (RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING))
/*  255 */           this.mNotificationBarImage.setImageDrawable(getResources().getDrawable(R.drawable.rc_notification_connecting_animated));
/*      */         else
/*  257 */           this.mNotificationBarImage.setImageDrawable(getResources().getDrawable(R.drawable.rc_notification_network_available));
/*      */       }
/*      */   }
/*      */ 
/*      */   public void onDestroy()
/*      */   {
/*  265 */     RLog.d(TAG, "onDestroy");
/*  266 */     RongContext.getInstance().getEventBus().unregister(this);
/*  267 */     getHandler().removeCallbacksAndMessages(null);
/*  268 */     super.onDestroy();
/*      */   }
/*      */ 
/*      */   public void onPause()
/*      */   {
/*  273 */     RLog.d(TAG, "onPause");
/*  274 */     super.onPause();
/*      */   }
/*      */ 
/*      */   public boolean onBackPressed()
/*      */   {
/*  279 */     return false;
/*      */   }
/*      */ 
/*      */   public void setAdapter(ConversationListAdapter adapter) {
/*  283 */     if (this.mAdapter != null)
/*  284 */       this.mAdapter.clear();
/*  285 */     this.mAdapter = adapter;
/*  286 */     if (this.mList != null)
/*  287 */       this.mList.setAdapter(adapter);
/*      */   }
/*      */ 
/*      */   public ConversationListAdapter getAdapter()
/*      */   {
/*  292 */     return this.mAdapter;
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.ConnectEvent event) {
/*  296 */     RLog.d(TAG, "onEventMainThread Event.ConnectEvent: isListRetrieved = " + this.isShowWithoutConnected);
/*      */ 
/*  298 */     if (this.isShowWithoutConnected) {
/*  299 */       if (this.mSupportConversationList.size() > 0)
/*  300 */         RongIM.getInstance().getConversationList(this.mCallback, (Conversation.ConversationType[])this.mSupportConversationList.toArray(new Conversation.ConversationType[this.mSupportConversationList.size()]));
/*      */       else {
/*  302 */         RongIM.getInstance().getConversationList(this.mCallback);
/*      */       }
/*  304 */       LinearLayout emptyView = (LinearLayout)this.mList.getEmptyView();
/*  305 */       TextView textView = (TextView)emptyView.findViewById(R.id.rc_empty_tv);
/*  306 */       textView.setText(RongContext.getInstance().getResources().getString(R.string.rc_conversation_list_empty_prompt));
/*      */     } else {
/*  308 */       return;
/*      */     }
/*  310 */     this.isShowWithoutConnected = false;
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.SyncReadStatusEvent event) {
/*  314 */     refreshUnreadCount(event.getConversationType(), event.getTargetId());
/*  315 */     int originalIndex = this.mAdapter.findPosition(event.getConversationType(), event.getTargetId());
/*  316 */     UIConversation conversation = (UIConversation)this.mAdapter.getItem(originalIndex);
/*  317 */     conversation.setMentionedFlag(false);
/*  318 */     this.mAdapter.getView(originalIndex, this.mList.getChildAt(originalIndex - this.mList.getFirstVisiblePosition()), this.mList);
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.ReadReceiptEvent event) {
/*  322 */     if (this.mAdapter == null) {
/*  323 */       RLog.d(TAG, "the conversation list adapter is null.");
/*  324 */       return;
/*      */     }
/*      */ 
/*  327 */     int originalIndex = this.mAdapter.findPosition(event.getMessage().getConversationType(), event.getMessage().getTargetId());
/*  328 */     boolean gatherState = RongContext.getInstance().getConversationGatherState(event.getMessage().getConversationType().getName()).booleanValue();
/*  329 */     if ((!gatherState) && 
/*  330 */       (originalIndex >= 0)) {
/*  331 */       UIConversation conversation = (UIConversation)this.mAdapter.getItem(originalIndex);
/*  332 */       ReadReceiptMessage content = (ReadReceiptMessage)event.getMessage().getContent();
/*  333 */       if ((content.getLastMessageSendTime() >= conversation.getUIConversationTime()) && (conversation.getConversationSenderId().equals(RongIMClient.getInstance().getCurrentUserId())))
/*      */       {
/*  335 */         conversation.setSentStatus(Message.SentStatus.READ);
/*  336 */         this.mAdapter.getView(originalIndex, this.mList.getChildAt(originalIndex - this.mList.getFirstVisiblePosition()), this.mList);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.OnReceiveMessageEvent event)
/*      */   {
/*  344 */     RLog.d(TAG, "Receive MessageEvent: id=" + event.getMessage().getTargetId() + ", type=" + event.getMessage().getConversationType());
/*      */ 
/*  347 */     if (((this.mSupportConversationList.size() != 0) && (!this.mSupportConversationList.contains(event.getMessage().getConversationType()))) || ((this.mSupportConversationList.size() == 0) && ((event.getMessage().getConversationType() == Conversation.ConversationType.CHATROOM) || (event.getMessage().getConversationType() == Conversation.ConversationType.CUSTOMER_SERVICE))))
/*      */     {
/*  350 */       RLog.e(TAG, "Not included in conversation list. Return directly!");
/*  351 */       return;
/*      */     }
/*      */ 
/*  354 */     if (this.mAdapter == null) {
/*  355 */       RLog.d(TAG, "the conversation list adapter is null. Cache the received message firstly!!!");
/*  356 */       this.mMessageCache.add(event.getMessage());
/*  357 */       return;
/*      */     }
/*      */ 
/*  360 */     int originalIndex = this.mAdapter.findPosition(event.getMessage().getConversationType(), event.getMessage().getTargetId());
/*  361 */     UIConversation uiConversation = makeUiConversation(event.getMessage(), originalIndex);
/*  362 */     int newPosition = ConversationListUtils.findPositionForNewConversation(uiConversation, this.mAdapter);
/*  363 */     if (originalIndex < 0) {
/*  364 */       this.mAdapter.add(uiConversation, newPosition);
/*  365 */     } else if (originalIndex != newPosition) {
/*  366 */       this.mAdapter.remove(originalIndex);
/*  367 */       this.mAdapter.add(uiConversation, newPosition);
/*      */     }
/*  369 */     this.mAdapter.notifyDataSetChanged();
/*      */ 
/*  372 */     if (event.getMessage().getMessageId() > 0) {
/*  373 */       if (event.getLeft() > 0) {
/*  374 */         this.mConversationKeyList.add(ConversationKey.obtain(event.getMessage().getTargetId(), event.getMessage().getConversationType()));
/*      */       } else {
/*  376 */         if (this.mConversationKeyList.size() > 0) {
/*  377 */           for (ConversationKey key : this.mConversationKeyList) {
/*  378 */             refreshUnreadCount(key.getType(), key.getTargetId());
/*      */           }
/*  380 */           this.mConversationKeyList.clear();
/*      */         }
/*  382 */         refreshUnreadCount(event.getMessage().getConversationType(), event.getMessage().getTargetId());
/*      */       }
/*      */     }
/*      */ 
/*  386 */     if (RongContext.getInstance().getConversationGatherState(event.getMessage().getConversationType().getName()).booleanValue())
/*  387 */       RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback(event)
/*      */       {
/*      */         public void onSuccess(List<Conversation> conversations) {
/*  390 */           if ((conversations == null) || (conversations.size() == 0))
/*  391 */             return;
/*  392 */           for (Conversation conv : conversations)
/*  393 */             if ((conv.getConversationType().equals(this.val$event.getMessage().getConversationType())) && (conv.getTargetId().equals(this.val$event.getMessage().getTargetId()))) {
/*  394 */               int pos = ConversationListFragment.this.mAdapter.findPosition(conv.getConversationType(), conv.getTargetId());
/*  395 */               if (pos < 0) break;
/*  396 */               ((UIConversation)ConversationListFragment.this.mAdapter.getItem(pos)).setDraft(conv.getDraft());
/*  397 */               if (TextUtils.isEmpty(conv.getDraft()))
/*  398 */                 ((UIConversation)ConversationListFragment.this.mAdapter.getItem(pos)).setSentStatus(null);
/*      */               else
/*  400 */                 ((UIConversation)ConversationListFragment.this.mAdapter.getItem(pos)).setSentStatus(conv.getSentStatus());
/*  401 */               ConversationListFragment.this.mAdapter.getView(pos, ConversationListFragment.this.mList.getChildAt(pos - ConversationListFragment.this.mList.getFirstVisiblePosition()), ConversationListFragment.this.mList); break;
/*      */             }
/*      */         }
/*      */ 
/*      */         public void onError(RongIMClient.ErrorCode e)
/*      */         {
/*      */         }
/*      */       }
/*      */       , new Conversation.ConversationType[] { event.getMessage().getConversationType() });
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.MessageRecallEvent event)
/*      */   {
/*  417 */     int count = this.mAdapter.getCount();
/*  418 */     for (int i = 0; i < count; i++)
/*  419 */       if (event.getMessageId() == ((UIConversation)this.mAdapter.getItem(i)).getLatestMessageId()) {
/*  420 */         boolean gatherState = ((UIConversation)this.mAdapter.getItem(i)).getConversationGatherState();
/*  421 */         if (gatherState) {
/*  422 */           RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback()
/*      */           {
/*      */             public void onSuccess(List<Conversation> conversationList) {
/*  425 */               if ((conversationList == null) || (conversationList.size() == 0))
/*  426 */                 return;
/*  427 */               UIConversation uiConversation = ConversationListFragment.this.makeUIConversationFromList(conversationList);
/*  428 */               int oldPos = ConversationListFragment.this.mAdapter.findPosition(uiConversation.getConversationType(), uiConversation.getConversationTargetId());
/*  429 */               if (oldPos >= 0) {
/*  430 */                 ConversationListFragment.this.mAdapter.remove(oldPos);
/*      */               }
/*  432 */               int newIndex = ConversationListUtils.findPositionForNewConversation(uiConversation, ConversationListFragment.this.mAdapter);
/*  433 */               ConversationListFragment.this.mAdapter.add(uiConversation, newIndex);
/*  434 */               ConversationListFragment.this.mAdapter.notifyDataSetChanged();
/*      */             }
/*      */ 
/*      */             public void onError(RongIMClient.ErrorCode e)
/*      */             {
/*      */             }
/*      */           }
/*      */           , new Conversation.ConversationType[] { ((UIConversation)this.mAdapter.getItem(i)).getConversationType() }); break;
/*      */         }
/*      */ 
/*  444 */         RongIM.getInstance().getConversation(((UIConversation)this.mAdapter.getItem(i)).getConversationType(), ((UIConversation)this.mAdapter.getItem(i)).getConversationTargetId(), new RongIMClient.ResultCallback()
/*      */         {
/*      */           public void onSuccess(Conversation conversation)
/*      */           {
/*  448 */             if (conversation == null) {
/*  449 */               RLog.d(ConversationListFragment.TAG, "onEventMainThread getConversation : onSuccess, conversation = null");
/*  450 */               return;
/*      */             }
/*  452 */             UIConversation temp = UIConversation.obtain(conversation, false);
/*      */ 
/*  454 */             int pos = ConversationListFragment.this.mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
/*  455 */             if (pos >= 0) {
/*  456 */               ConversationListFragment.this.mAdapter.remove(pos);
/*      */             }
/*  458 */             int newPosition = ConversationListUtils.findPositionForNewConversation(temp, ConversationListFragment.this.mAdapter);
/*  459 */             ConversationListFragment.this.mAdapter.add(temp, newPosition);
/*  460 */             ConversationListFragment.this.mAdapter.notifyDataSetChanged();
/*      */           }
/*      */ 
/*      */           public void onError(RongIMClient.ErrorCode e)
/*      */           {
/*      */           }
/*      */         });
/*  468 */         break;
/*      */       }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.RemoteMessageRecallEvent event)
/*      */   {
/*  474 */     int count = this.mAdapter.getCount();
/*  475 */     for (int i = 0; i < count; i++)
/*  476 */       if (event.getMessageId() == ((UIConversation)this.mAdapter.getItem(i)).getLatestMessageId()) {
/*  477 */         boolean gatherState = ((UIConversation)this.mAdapter.getItem(i)).getConversationGatherState();
/*  478 */         if (gatherState) {
/*  479 */           RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback()
/*      */           {
/*      */             public void onSuccess(List<Conversation> conversationList) {
/*  482 */               if ((conversationList == null) || (conversationList.size() == 0))
/*  483 */                 return;
/*  484 */               UIConversation uiConversation = ConversationListFragment.this.makeUIConversationFromList(conversationList);
/*  485 */               int oldPos = ConversationListFragment.this.mAdapter.findPosition(uiConversation.getConversationType(), uiConversation.getConversationTargetId());
/*  486 */               if (oldPos >= 0) {
/*  487 */                 ConversationListFragment.this.mAdapter.remove(oldPos);
/*      */               }
/*  489 */               int newIndex = ConversationListUtils.findPositionForNewConversation(uiConversation, ConversationListFragment.this.mAdapter);
/*  490 */               ConversationListFragment.this.mAdapter.add(uiConversation, newIndex);
/*  491 */               ConversationListFragment.this.mAdapter.notifyDataSetChanged();
/*      */             }
/*      */ 
/*      */             public void onError(RongIMClient.ErrorCode e)
/*      */             {
/*      */             }
/*      */           }
/*      */           , new Conversation.ConversationType[] { ((UIConversation)this.mAdapter.getItem(i)).getConversationType() }); break;
/*      */         }
/*      */ 
/*  501 */         RongIM.getInstance().getConversation(((UIConversation)this.mAdapter.getItem(i)).getConversationType(), ((UIConversation)this.mAdapter.getItem(i)).getConversationTargetId(), new RongIMClient.ResultCallback()
/*      */         {
/*      */           public void onSuccess(Conversation conversation)
/*      */           {
/*  505 */             if (conversation == null) {
/*  506 */               RLog.d(ConversationListFragment.TAG, "onEventMainThread getConversation : onSuccess, conversation = null");
/*  507 */               return;
/*      */             }
/*  509 */             UIConversation temp = UIConversation.obtain(conversation, false);
/*      */ 
/*  511 */             int pos = ConversationListFragment.this.mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
/*  512 */             if (pos >= 0) {
/*  513 */               ConversationListFragment.this.mAdapter.remove(pos);
/*      */             }
/*  515 */             int newPosition = ConversationListUtils.findPositionForNewConversation(temp, ConversationListFragment.this.mAdapter);
/*  516 */             ConversationListFragment.this.mAdapter.add(temp, newPosition);
/*  517 */             ConversationListFragment.this.mAdapter.notifyDataSetChanged();
/*      */           }
/*      */ 
/*      */           public void onError(RongIMClient.ErrorCode e)
/*      */           {
/*      */           }
/*      */         });
/*  525 */         break;
/*      */       }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Message message)
/*      */   {
/*  531 */     RLog.d(TAG, "onEventMainThread Receive Message: name=" + message.getObjectName() + ", type=" + message.getConversationType());
/*      */ 
/*  534 */     if (((this.mSupportConversationList.size() != 0) && (!this.mSupportConversationList.contains(message.getConversationType()))) || ((this.mSupportConversationList.size() == 0) && ((message.getConversationType() == Conversation.ConversationType.CHATROOM) || (message.getConversationType() == Conversation.ConversationType.CUSTOMER_SERVICE))))
/*      */     {
/*  537 */       RLog.d(TAG, "onEventBackgroundThread Not included in conversation list. Return directly!");
/*  538 */       return;
/*      */     }
/*      */ 
/*  541 */     int originalIndex = this.mAdapter.findPosition(message.getConversationType(), message.getTargetId());
/*      */ 
/*  543 */     UIConversation uiConversation = makeUiConversation(message, originalIndex);
/*      */ 
/*  545 */     int newPosition = ConversationListUtils.findPositionForNewConversation(uiConversation, this.mAdapter);
/*      */ 
/*  547 */     if (originalIndex >= 0) {
/*  548 */       if (newPosition == originalIndex) {
/*  549 */         this.mAdapter.getView(newPosition, this.mList.getChildAt(newPosition - this.mList.getFirstVisiblePosition()), this.mList);
/*      */       } else {
/*  551 */         this.mAdapter.remove(originalIndex);
/*  552 */         this.mAdapter.add(uiConversation, newPosition);
/*  553 */         this.mAdapter.notifyDataSetChanged();
/*      */       }
/*      */     } else {
/*  556 */       this.mAdapter.add(uiConversation, newPosition);
/*  557 */       this.mAdapter.notifyDataSetChanged();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(MessageContent content) {
/*  562 */     RLog.d(TAG, "onEventMainThread: MessageContent");
/*      */ 
/*  564 */     for (int index = 0; index < this.mAdapter.getCount(); index++) {
/*  565 */       UIConversation tempUIConversation = (UIConversation)this.mAdapter.getItem(index);
/*      */ 
/*  567 */       if ((content != null) && (tempUIConversation.getMessageContent() != null) && (tempUIConversation.getMessageContent() == content))
/*      */       {
/*  569 */         tempUIConversation.setMessageContent(content);
/*  570 */         tempUIConversation.setConversationContent(tempUIConversation.buildConversationContent(tempUIConversation));
/*      */ 
/*  572 */         if (index >= this.mList.getFirstVisiblePosition())
/*  573 */           this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition()), this.mList);
/*      */       } else {
/*  575 */         RLog.e(TAG, "onEventMainThread MessageContent is null");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(RongIMClient.ConnectionStatusListener.ConnectionStatus status) {
/*  581 */     RLog.d(TAG, "ConnectionStatus, " + status.toString());
/*      */ 
/*  583 */     setNotificationBarVisibility(status);
/*  584 */     if ((this.isShowWithoutConnected) && (status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)))
/*  585 */       this.isShowWithoutConnected = false;
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.CreateDiscussionEvent createDiscussionEvent)
/*      */   {
/*  591 */     RLog.d(TAG, "onEventBackgroundThread: createDiscussionEvent");
/*  592 */     UIConversation conversation = new UIConversation();
/*  593 */     conversation.setConversationType(Conversation.ConversationType.DISCUSSION);
/*  594 */     if (createDiscussionEvent.getDiscussionName() != null)
/*  595 */       conversation.setUIConversationTitle(createDiscussionEvent.getDiscussionName());
/*      */     else {
/*  597 */       conversation.setUIConversationTitle("");
/*      */     }
/*  599 */     conversation.setConversationTargetId(createDiscussionEvent.getDiscussionId());
/*  600 */     conversation.setUIConversationTime(System.currentTimeMillis());
/*      */ 
/*  602 */     boolean isGather = RongContext.getInstance().getConversationGatherState(Conversation.ConversationType.DISCUSSION.getName()).booleanValue();
/*  603 */     conversation.setConversationGatherState(isGather);
/*      */ 
/*  606 */     if (isGather) {
/*  607 */       String name = RongContext.getInstance().getGatheredConversationTitle(conversation.getConversationType());
/*  608 */       conversation.setUIConversationTitle(name);
/*      */     }
/*      */ 
/*  611 */     int gatherPosition = this.mAdapter.findGatherPosition(Conversation.ConversationType.DISCUSSION);
/*      */ 
/*  613 */     if (gatherPosition == -1) {
/*  614 */       this.mAdapter.add(conversation, ConversationListUtils.findPositionForNewConversation(conversation, this.mAdapter));
/*  615 */       this.mAdapter.notifyDataSetChanged();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Draft draft) {
/*  620 */     if (draft == null)
/*  621 */       return;
/*  622 */     Conversation.ConversationType curType = Conversation.ConversationType.setValue(draft.getType().intValue());
/*  623 */     if ((curType == null) || (!this.mSupportConversationList.contains(curType))) {
/*  624 */       RLog.w(TAG, curType + " should not show in conversation list.");
/*  625 */       return;
/*      */     }
/*  627 */     RLog.i(TAG, "Draft ConversationType : " + curType.getName());
/*      */ 
/*  629 */     int position = this.mAdapter.findPosition(curType, draft.getId());
/*  630 */     if (position >= 0) {
/*  631 */       UIConversation conversation = (UIConversation)this.mAdapter.getItem(position);
/*  632 */       if (conversation.getConversationTargetId().equals(draft.getId())) {
/*  633 */         conversation.setDraft(draft.getContent());
/*  634 */         if (!TextUtils.isEmpty(draft.getContent()))
/*  635 */           conversation.setSentStatus(null);
/*  636 */         this.mAdapter.getView(position, this.mList.getChildAt(position - this.mList.getFirstVisiblePosition()), this.mList);
/*      */       }
/*      */     }
/*  639 */     else if (!TextUtils.isEmpty(draft.getContent())) {
/*  640 */       UIConversation temp = new UIConversation();
/*  641 */       temp.setConversationType(Conversation.ConversationType.setValue(draft.getType().intValue()));
/*  642 */       temp.setConversationTargetId(draft.getId());
/*  643 */       UserInfo curUserInfo = RongContext.getInstance().getUserInfoFromCache(draft.getId());
/*  644 */       if (curUserInfo != null) {
/*  645 */         temp.setUIConversationTitle(curUserInfo.getName());
/*  646 */         if (curUserInfo.getPortraitUri() != null)
/*  647 */           temp.setIconUrl(curUserInfo.getPortraitUri());
/*      */       }
/*  649 */       temp.setUIConversationTime(System.currentTimeMillis());
/*  650 */       temp.setConversationSenderId(RongIMClient.getInstance().getCurrentUserId());
/*  651 */       temp.setDraft(draft.getContent());
/*  652 */       int newPosition = ConversationListUtils.findPositionForNewConversation(temp, this.mAdapter);
/*  653 */       if (newPosition >= 0) {
/*  654 */         this.mAdapter.add(temp, newPosition);
/*  655 */         this.mAdapter.notifyDataSetChanged();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Group groupInfo)
/*      */   {
/*  664 */     int count = this.mAdapter.getCount();
/*  665 */     RLog.d(TAG, "onEventMainThread Group: name=" + groupInfo.getName() + ", id=" + groupInfo.getId());
/*  666 */     if (groupInfo.getName() == null) {
/*  667 */       return;
/*      */     }
/*      */ 
/*  670 */     for (int i = 0; i < count; i++) {
/*  671 */       UIConversation item = (UIConversation)this.mAdapter.getItem(i);
/*  672 */       if ((item == null) || (!item.getConversationType().equals(Conversation.ConversationType.GROUP)) || (!item.getConversationTargetId().equals(groupInfo.getId())))
/*      */         continue;
/*  674 */       boolean gatherState = RongContext.getInstance().getConversationGatherState(item.getConversationType().getName()).booleanValue();
/*  675 */       if (gatherState) {
/*  676 */         SpannableStringBuilder builder = new SpannableStringBuilder();
/*  677 */         Spannable messageData = RongContext.getInstance().getMessageTemplate(item.getMessageContent().getClass()).getContentSummary(item.getMessageContent());
/*      */ 
/*  680 */         if ((item.getMessageContent() instanceof VoiceMessage)) {
/*  681 */           boolean isListened = RongIM.getInstance().getConversation(item.getConversationType(), item.getConversationTargetId()).getReceivedStatus().isListened();
/*      */ 
/*  683 */           if ((item.getConversationSenderId().equals(RongIMClient.getInstance().getCurrentUserId())) || (isListened))
/*  684 */             messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_text_color_secondary)), 0, messageData.length(), 33);
/*      */           else {
/*  686 */             messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_voice_color)), 0, messageData.length(), 33);
/*      */           }
/*      */         }
/*  689 */         builder.append(groupInfo.getName()).append(" : ").append(messageData);
/*  690 */         item.setConversationContent(builder);
/*  691 */         if (groupInfo.getPortraitUri() != null)
/*  692 */           item.setIconUrl(groupInfo.getPortraitUri());
/*      */       } else {
/*  694 */         item.setUIConversationTitle(groupInfo.getName());
/*  695 */         if (groupInfo.getPortraitUri() != null)
/*  696 */           item.setIconUrl(groupInfo.getPortraitUri());
/*      */       }
/*  698 */       this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Discussion discussion)
/*      */   {
/*  704 */     int count = this.mAdapter.getCount();
/*  705 */     RLog.d(TAG, "onEventMainThread Discussion: name=" + discussion.getName() + ", id=" + discussion.getId());
/*      */ 
/*  707 */     for (int i = 0; i < count; i++) {
/*  708 */       UIConversation item = (UIConversation)this.mAdapter.getItem(i);
/*  709 */       if ((item == null) || (!item.getConversationType().equals(Conversation.ConversationType.DISCUSSION)) || (!item.getConversationTargetId().equals(discussion.getId())))
/*      */         continue;
/*  711 */       boolean gatherState = RongContext.getInstance().getConversationGatherState(item.getConversationType().getName()).booleanValue();
/*  712 */       if (gatherState) {
/*  713 */         SpannableStringBuilder builder = new SpannableStringBuilder();
/*  714 */         Spannable messageData = RongContext.getInstance().getMessageTemplate(item.getMessageContent().getClass()).getContentSummary(item.getMessageContent());
/*      */ 
/*  718 */         if (messageData != null) {
/*  719 */           if ((item.getMessageContent() instanceof VoiceMessage)) {
/*  720 */             boolean isListened = RongIM.getInstance().getConversation(item.getConversationType(), item.getConversationTargetId()).getReceivedStatus().isListened();
/*      */ 
/*  722 */             if ((item.getConversationSenderId().equals(RongIMClient.getInstance().getCurrentUserId())) || (isListened))
/*  723 */               messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_text_color_secondary)), 0, messageData.length(), 33);
/*      */             else {
/*  725 */               messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_voice_color)), 0, messageData.length(), 33);
/*      */             }
/*      */           }
/*  728 */           builder.append(discussion.getName()).append(" : ").append(messageData);
/*      */         } else {
/*  730 */           builder.append(discussion.getName());
/*      */         }
/*      */ 
/*  733 */         item.setConversationContent(builder);
/*      */       } else {
/*  735 */         item.setUIConversationTitle(discussion.getName());
/*      */       }
/*  737 */       this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.GroupUserInfoEvent event)
/*      */   {
/*  743 */     int count = this.mAdapter.getCount();
/*      */ 
/*  745 */     GroupUserInfo userInfo = event.getUserInfo();
/*      */ 
/*  747 */     if ((userInfo == null) || (userInfo.getNickname() == null)) {
/*  748 */       return;
/*      */     }
/*      */ 
/*  751 */     for (int i = 0; i < count; i++) {
/*  752 */       UIConversation uiConversation = (UIConversation)this.mAdapter.getItem(i);
/*  753 */       Conversation.ConversationType type = uiConversation.getConversationType();
/*      */ 
/*  755 */       boolean gatherState = RongContext.getInstance().getConversationGatherState(uiConversation.getConversationType().getName()).booleanValue();
/*      */       boolean isShowName;
/*      */       boolean isShowName;
/*  757 */       if (uiConversation.getMessageContent() == null) {
/*  758 */         isShowName = false;
/*      */       } else {
/*  760 */         ProviderTag providerTag = RongContext.getInstance().getMessageProviderTag(uiConversation.getMessageContent().getClass());
/*  761 */         isShowName = providerTag != null ? providerTag.showSummaryWithName() : false;
/*      */       }
/*      */ 
/*  764 */       if ((gatherState) || (!isShowName) || (!type.equals(Conversation.ConversationType.GROUP)) || (!uiConversation.getConversationSenderId().equals(userInfo.getUserId())))
/*      */         continue;
/*  766 */       Spannable messageData = RongContext.getInstance().getMessageTemplate(uiConversation.getMessageContent().getClass()).getContentSummary(uiConversation.getMessageContent());
/*  767 */       SpannableStringBuilder builder = new SpannableStringBuilder();
/*  768 */       if ((uiConversation.getMessageContent() instanceof VoiceMessage)) {
/*  769 */         boolean isListened = RongIM.getInstance().getConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId()).getReceivedStatus().isListened();
/*      */ 
/*  771 */         if ((uiConversation.getConversationSenderId().equals(RongIMClient.getInstance().getCurrentUserId())) || (isListened))
/*  772 */           messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_text_color_secondary)), 0, messageData.length(), 33);
/*      */         else {
/*  774 */           messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_voice_color)), 0, messageData.length(), 33);
/*      */         }
/*      */       }
/*  777 */       if (uiConversation.getConversationTargetId().equals(userInfo.getGroupId())) {
/*  778 */         uiConversation.addNickname(userInfo.getUserId());
/*  779 */         builder.append(userInfo.getNickname()).append(" : ").append(messageData);
/*  780 */         uiConversation.setConversationContent(builder);
/*      */       }
/*  782 */       this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(UserInfo userInfo)
/*      */   {
/*  789 */     int count = this.mAdapter.getCount();
/*      */ 
/*  792 */     if (userInfo.getName() == null) {
/*  793 */       return;
/*      */     }
/*      */ 
/*  796 */     for (int i = 0; i < count; i++) {
/*  797 */       UIConversation temp = (UIConversation)this.mAdapter.getItem(i);
/*  798 */       String type = temp.getConversationType().getName();
/*  799 */       boolean gatherState = RongContext.getInstance().getConversationGatherState(temp.getConversationType().getName()).booleanValue();
/*      */ 
/*  801 */       if (temp.hasNickname(userInfo.getUserId()))
/*      */         continue;
/*      */       boolean isShowName;
/*      */       boolean isShowName;
/*  804 */       if ((temp.getMessageContent() == null) || (userInfo.getUserId().equals(RongIM.getInstance().getCurrentUserId()))) {
/*  805 */         isShowName = false;
/*      */       } else {
/*  807 */         ProviderTag providerTag = RongContext.getInstance().getMessageProviderTag(temp.getMessageContent().getClass());
/*  808 */         isShowName = providerTag != null ? providerTag.showSummaryWithName() : false;
/*      */       }
/*      */ 
/*  811 */       if (((temp.getMessageContent() instanceof RecallNotificationMessage)) && (temp.getConversationSenderId().equals(userInfo.getUserId())))
/*      */       {
/*  813 */         Spannable messageData = RongContext.getInstance().getMessageTemplate(temp.getMessageContent().getClass()).getContentSummary(temp.getMessageContent());
/*  814 */         SpannableStringBuilder builder = new SpannableStringBuilder();
/*  815 */         builder.append(messageData);
/*  816 */         temp.setConversationContent(builder);
/*  817 */         this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
/*      */       }
/*      */ 
/*  821 */       if ((!gatherState) && (isShowName) && ((type.equals("group")) || (type.equals("discussion"))) && (temp.getConversationSenderId().equals(userInfo.getUserId())))
/*      */       {
/*  823 */         Spannable messageData = RongContext.getInstance().getMessageTemplate(temp.getMessageContent().getClass()).getContentSummary(temp.getMessageContent());
/*  824 */         SpannableStringBuilder builder = new SpannableStringBuilder();
/*  825 */         if ((temp.getMessageContent() instanceof VoiceMessage)) {
/*  826 */           boolean isListened = RongIM.getInstance().getConversation(temp.getConversationType(), temp.getConversationTargetId()).getReceivedStatus().isListened();
/*      */ 
/*  828 */           if ((temp.getConversationSenderId().equals(RongIMClient.getInstance().getCurrentUserId())) || (isListened))
/*  829 */             messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_text_color_secondary)), 0, messageData.length(), 33);
/*      */           else {
/*  831 */             messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_voice_color)), 0, messageData.length(), 33);
/*      */           }
/*      */         }
/*  834 */         builder.append(userInfo.getName()).append(" : ").append(messageData);
/*  835 */         temp.setConversationContent(builder);
/*  836 */         this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList); } else {
/*  837 */         if (((!type.equals("private")) && (!type.equals("system")) && (!type.equals("customer_service"))) || (!temp.getConversationTargetId().equals(userInfo.getUserId())))
/*      */           continue;
/*  839 */         if (!gatherState) {
/*  840 */           temp.setUIConversationTitle(userInfo.getName());
/*  841 */           temp.setIconUrl(userInfo.getPortraitUri());
/*  842 */         } else if ((gatherState) && (isShowName)) {
/*  843 */           Spannable messageData = RongContext.getInstance().getMessageTemplate(temp.getMessageContent().getClass()).getContentSummary(temp.getMessageContent());
/*  844 */           SpannableStringBuilder builder = new SpannableStringBuilder();
/*  845 */           if (messageData != null) {
/*  846 */             if ((temp.getMessageContent() instanceof VoiceMessage)) {
/*  847 */               boolean isListened = RongIM.getInstance().getConversation(temp.getConversationType(), temp.getConversationTargetId()).getReceivedStatus().isListened();
/*      */ 
/*  849 */               if ((temp.getConversationSenderId().equals(RongIMClient.getInstance().getCurrentUserId())) || (isListened))
/*  850 */                 messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_text_color_secondary)), 0, messageData.length(), 33);
/*      */               else {
/*  852 */                 messageData.setSpan(new ForegroundColorSpan(RongContext.getInstance().getResources().getColor(R.color.rc_voice_color)), 0, messageData.length(), 33);
/*      */               }
/*      */             }
/*      */ 
/*  856 */             builder.append(userInfo.getName()).append(" : ").append(messageData);
/*      */           } else {
/*  858 */             builder.append(userInfo.getName());
/*      */           }
/*  860 */           temp.setConversationContent(builder);
/*      */         }
/*  862 */         this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(PublicServiceProfile accountInfo) {
/*  868 */     int count = this.mAdapter.getCount();
/*  869 */     boolean gatherState = RongContext.getInstance().getConversationGatherState(accountInfo.getConversationType().getName()).booleanValue();
/*  870 */     for (int i = 0; i < count; i++) {
/*  871 */       if ((!((UIConversation)this.mAdapter.getItem(i)).getConversationType().equals(accountInfo.getConversationType())) || (!((UIConversation)this.mAdapter.getItem(i)).getConversationTargetId().equals(accountInfo.getTargetId())) || (gatherState)) {
/*      */         continue;
/*      */       }
/*  874 */       ((UIConversation)this.mAdapter.getItem(i)).setUIConversationTitle(accountInfo.getName());
/*  875 */       ((UIConversation)this.mAdapter.getItem(i)).setIconUrl(accountInfo.getPortraitUri());
/*  876 */       this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
/*  877 */       break;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.PublicServiceFollowableEvent event)
/*      */   {
/*  884 */     if ((event != null) && 
/*  885 */       (!event.isFollow())) {
/*  886 */       int originalIndex = this.mAdapter.findPosition(event.getConversationType(), event.getTargetId());
/*  887 */       if (originalIndex >= 0) {
/*  888 */         this.mAdapter.remove(originalIndex);
/*  889 */         this.mAdapter.notifyDataSetChanged();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.ConversationUnreadEvent unreadEvent)
/*      */   {
/*  896 */     int targetIndex = this.mAdapter.findPosition(unreadEvent.getType(), unreadEvent.getTargetId());
/*  897 */     if (targetIndex >= 0) {
/*  898 */       UIConversation temp = (UIConversation)this.mAdapter.getItem(targetIndex);
/*  899 */       boolean gatherState = temp.getConversationGatherState();
/*  900 */       if (gatherState) {
/*  901 */         RongIM.getInstance().getUnreadCount(new RongIMClient.ResultCallback(unreadEvent)
/*      */         {
/*      */           public void onSuccess(Integer count) {
/*  904 */             int pos = ConversationListFragment.this.mAdapter.findPosition(this.val$unreadEvent.getType(), this.val$unreadEvent.getTargetId());
/*  905 */             if (pos >= 0) {
/*  906 */               ((UIConversation)ConversationListFragment.this.mAdapter.getItem(pos)).setUnReadMessageCount(count.intValue());
/*  907 */               ConversationListFragment.this.mAdapter.getView(pos, ConversationListFragment.this.mList.getChildAt(pos - ConversationListFragment.this.mList.getFirstVisiblePosition()), ConversationListFragment.this.mList);
/*      */             }
/*      */           }
/*      */ 
/*      */           public void onError(RongIMClient.ErrorCode e)
/*      */           {
/*  913 */             System.err.print("Throw exception when get unread message count from ipc remote side!");
/*      */           }
/*      */         }
/*      */         , new Conversation.ConversationType[] { unreadEvent.getType() });
/*      */ 
/*  916 */         RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback(unreadEvent)
/*      */         {
/*      */           public void onSuccess(List<Conversation> conversations) {
/*  919 */             boolean mentionedFlag = false;
/*  920 */             for (Conversation conversation : conversations) {
/*  921 */               if (conversation.getMentionedCount() > 0) {
/*  922 */                 mentionedFlag = true;
/*  923 */                 break;
/*      */               }
/*      */             }
/*  926 */             int pos = ConversationListFragment.this.mAdapter.findPosition(this.val$unreadEvent.getType(), this.val$unreadEvent.getTargetId());
/*  927 */             if (pos >= 0) {
/*  928 */               ((UIConversation)ConversationListFragment.this.mAdapter.getItem(pos)).setMentionedFlag(mentionedFlag);
/*  929 */               ConversationListFragment.this.mAdapter.getView(pos, ConversationListFragment.this.mList.getChildAt(pos - ConversationListFragment.this.mList.getFirstVisiblePosition()), ConversationListFragment.this.mList);
/*      */             }
/*      */           }
/*      */ 
/*      */           public void onError(RongIMClient.ErrorCode e)
/*      */           {
/*      */           }
/*      */         }
/*      */         , new Conversation.ConversationType[] { unreadEvent.getType() });
/*      */       }
/*      */       else
/*      */       {
/*  939 */         temp.setUnReadMessageCount(0);
/*  940 */         temp.setMentionedFlag(false);
/*  941 */         RLog.d(TAG, "onEventMainThread ConversationUnreadEvent: set unRead count to be 0");
/*  942 */         this.mAdapter.getView(targetIndex, this.mList.getChildAt(targetIndex - this.mList.getFirstVisiblePosition()), this.mList);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.ConversationTopEvent setTopEvent) throws IllegalAccessException
/*      */   {
/*  949 */     int originalIndex = this.mAdapter.findPosition(setTopEvent.getConversationType(), setTopEvent.getTargetId());
/*  950 */     if (originalIndex >= 0) {
/*  951 */       UIConversation temp = (UIConversation)this.mAdapter.getItem(originalIndex);
/*  952 */       boolean originalValue = temp.isTop();
/*  953 */       if (originalValue == setTopEvent.isTop()) {
/*  954 */         return;
/*      */       }
/*  956 */       if (temp.getConversationGatherState()) {
/*  957 */         RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback(setTopEvent)
/*      */         {
/*      */           public void onSuccess(List<Conversation> conversations) {
/*  960 */             if ((conversations == null) || (conversations.size() == 0))
/*  961 */               return;
/*  962 */             UIConversation newConversation = ConversationListFragment.this.makeUIConversationFromList(conversations);
/*  963 */             int pos = ConversationListFragment.this.mAdapter.findPosition(this.val$setTopEvent.getConversationType(), this.val$setTopEvent.getTargetId());
/*  964 */             if (pos >= 0)
/*  965 */               ConversationListFragment.this.mAdapter.remove(pos);
/*  966 */             int newIndex = ConversationListUtils.findPositionForNewConversation(newConversation, ConversationListFragment.this.mAdapter);
/*  967 */             ConversationListFragment.this.mAdapter.add(newConversation, newIndex);
/*  968 */             ConversationListFragment.this.mAdapter.notifyDataSetChanged();
/*      */           }
/*      */ 
/*      */           public void onError(RongIMClient.ErrorCode e)
/*      */           {
/*      */           }
/*      */         }
/*      */         , new Conversation.ConversationType[] { temp.getConversationType() });
/*      */       }
/*      */       else
/*      */       {
/*      */         int newIndex;
/*      */         int newIndex;
/*  978 */         if (originalValue == true) {
/*  979 */           temp.setTop(false);
/*  980 */           newIndex = ConversationListUtils.findPositionForCancleTop(originalIndex, this.mAdapter);
/*      */         } else {
/*  982 */           temp.setTop(true);
/*  983 */           newIndex = ConversationListUtils.findPositionForSetTop(temp, this.mAdapter);
/*      */         }
/*      */ 
/*  986 */         if (originalIndex == newIndex) {
/*  987 */           this.mAdapter.getView(newIndex, this.mList.getChildAt(newIndex - this.mList.getFirstVisiblePosition()), this.mList);
/*      */         } else {
/*  989 */           this.mAdapter.remove(originalIndex);
/*  990 */           this.mAdapter.add(temp, newIndex);
/*  991 */           this.mAdapter.notifyDataSetChanged();
/*      */         }
/*      */       }
/*      */     } else {
/*  995 */       throw new IllegalAccessException("the item has already been deleted!");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.ConversationRemoveEvent removeEvent)
/*      */   {
/* 1001 */     int removedIndex = this.mAdapter.findPosition(removeEvent.getType(), removeEvent.getTargetId());
/*      */ 
/* 1003 */     boolean gatherState = RongContext.getInstance().getConversationGatherState(removeEvent.getType().getName()).booleanValue();
/*      */ 
/* 1005 */     if (!gatherState) {
/* 1006 */       if (removedIndex >= 0) {
/* 1007 */         this.mAdapter.remove(removedIndex);
/* 1008 */         this.mAdapter.notifyDataSetChanged();
/*      */       }
/*      */     }
/* 1011 */     else if (removedIndex >= 0)
/* 1012 */       RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback(removeEvent)
/*      */       {
/*      */         public void onSuccess(List<Conversation> conversationList) {
/* 1015 */           int oldPos = ConversationListFragment.this.mAdapter.findPosition(this.val$removeEvent.getType(), this.val$removeEvent.getTargetId());
/* 1016 */           if ((conversationList == null) || (conversationList.size() == 0)) {
/* 1017 */             if (oldPos >= 0)
/* 1018 */               ConversationListFragment.this.mAdapter.remove(oldPos);
/* 1019 */             ConversationListFragment.this.mAdapter.notifyDataSetChanged();
/* 1020 */             return;
/*      */           }
/*      */ 
/* 1023 */           UIConversation newConversation = ConversationListFragment.this.makeUIConversationFromList(conversationList);
/* 1024 */           if (oldPos >= 0) {
/* 1025 */             ConversationListFragment.this.mAdapter.remove(oldPos);
/*      */           }
/* 1027 */           int newIndex = ConversationListUtils.findPositionForNewConversation(newConversation, ConversationListFragment.this.mAdapter);
/* 1028 */           ConversationListFragment.this.mAdapter.add(newConversation, newIndex);
/* 1029 */           ConversationListFragment.this.mAdapter.notifyDataSetChanged();
/*      */         }
/*      */ 
/*      */         public void onError(RongIMClient.ErrorCode e)
/*      */         {
/*      */         }
/*      */       }
/*      */       , new Conversation.ConversationType[] { removeEvent.getType() });
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.ClearConversationEvent clearConversationEvent)
/*      */   {
/* 1042 */     List typeList = clearConversationEvent.getTypes();
/* 1043 */     for (int i = this.mAdapter.getCount() - 1; i >= 0; i--) {
/* 1044 */       if (typeList.indexOf(((UIConversation)this.mAdapter.getItem(i)).getConversationType()) >= 0) {
/* 1045 */         this.mAdapter.remove(i);
/*      */       }
/*      */     }
/* 1048 */     this.mAdapter.notifyDataSetChanged();
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.MessageDeleteEvent event) {
/* 1052 */     int count = this.mAdapter.getCount();
/* 1053 */     for (int i = 0; i < count; i++)
/* 1054 */       if (event.getMessageIds().contains(Integer.valueOf(((UIConversation)this.mAdapter.getItem(i)).getLatestMessageId()))) {
/* 1055 */         boolean gatherState = ((UIConversation)this.mAdapter.getItem(i)).getConversationGatherState();
/* 1056 */         int index = i;
/* 1057 */         if (gatherState) {
/* 1058 */           RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback()
/*      */           {
/*      */             public void onSuccess(List<Conversation> conversationList) {
/* 1061 */               if ((conversationList == null) || (conversationList.size() == 0))
/* 1062 */                 return;
/* 1063 */               UIConversation uiConversation = ConversationListFragment.this.makeUIConversationFromList(conversationList);
/* 1064 */               int oldPos = ConversationListFragment.this.mAdapter.findPosition(uiConversation.getConversationType(), uiConversation.getConversationTargetId());
/* 1065 */               if (oldPos >= 0) {
/* 1066 */                 ConversationListFragment.this.mAdapter.remove(oldPos);
/*      */               }
/* 1068 */               int newIndex = ConversationListUtils.findPositionForNewConversation(uiConversation, ConversationListFragment.this.mAdapter);
/* 1069 */               ConversationListFragment.this.mAdapter.add(uiConversation, newIndex);
/* 1070 */               ConversationListFragment.this.mAdapter.notifyDataSetChanged();
/*      */             }
/*      */ 
/*      */             public void onError(RongIMClient.ErrorCode e)
/*      */             {
/*      */             }
/*      */           }
/*      */           , new Conversation.ConversationType[] { ((UIConversation)this.mAdapter.getItem(index)).getConversationType() }); break;
/*      */         }
/*      */ 
/* 1080 */         RongIM.getInstance().getConversation(((UIConversation)this.mAdapter.getItem(index)).getConversationType(), ((UIConversation)this.mAdapter.getItem(index)).getConversationTargetId(), new RongIMClient.ResultCallback()
/*      */         {
/*      */           public void onSuccess(Conversation conversation)
/*      */           {
/* 1084 */             if (conversation == null) {
/* 1085 */               RLog.d(ConversationListFragment.TAG, "onEventMainThread getConversation : onSuccess, conversation = null");
/* 1086 */               return;
/*      */             }
/* 1088 */             UIConversation temp = UIConversation.obtain(conversation, false);
/*      */ 
/* 1090 */             int pos = ConversationListFragment.this.mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
/* 1091 */             if (pos >= 0) {
/* 1092 */               ConversationListFragment.this.mAdapter.remove(pos);
/*      */             }
/* 1094 */             int newPosition = ConversationListUtils.findPositionForNewConversation(temp, ConversationListFragment.this.mAdapter);
/* 1095 */             ConversationListFragment.this.mAdapter.add(temp, newPosition);
/* 1096 */             ConversationListFragment.this.mAdapter.notifyDataSetChanged();
/*      */           }
/*      */ 
/*      */           public void onError(RongIMClient.ErrorCode e)
/*      */           {
/*      */           }
/*      */         });
/* 1104 */         break;
/*      */       }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.ConversationNotificationEvent notificationEvent)
/*      */   {
/* 1110 */     int originalIndex = this.mAdapter.findPosition(notificationEvent.getConversationType(), notificationEvent.getTargetId());
/*      */ 
/* 1112 */     if (originalIndex >= 0)
/* 1113 */       this.mAdapter.getView(originalIndex, this.mList.getChildAt(originalIndex - this.mList.getFirstVisiblePosition()), this.mList);
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.MessagesClearEvent clearMessagesEvent)
/*      */   {
/* 1119 */     int originalIndex = this.mAdapter.findPosition(clearMessagesEvent.getType(), clearMessagesEvent.getTargetId());
/*      */ 
/* 1121 */     if (originalIndex >= 0) {
/* 1122 */       boolean gatherState = RongContext.getInstance().getConversationGatherState(clearMessagesEvent.getType().getName()).booleanValue();
/* 1123 */       if (gatherState) {
/* 1124 */         RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback()
/*      */         {
/*      */           public void onSuccess(List<Conversation> conversationList) {
/* 1127 */             if ((conversationList == null) || (conversationList.size() == 0))
/* 1128 */               return;
/* 1129 */             UIConversation uiConversation = ConversationListFragment.this.makeUIConversationFromList(conversationList);
/* 1130 */             int pos = ConversationListFragment.this.mAdapter.findPosition(uiConversation.getConversationType(), uiConversation.getConversationTargetId());
/* 1131 */             if (pos >= 0) {
/* 1132 */               ConversationListFragment.this.mAdapter.remove(pos);
/*      */             }
/* 1134 */             int newIndex = ConversationListUtils.findPositionForNewConversation(uiConversation, ConversationListFragment.this.mAdapter);
/* 1135 */             ConversationListFragment.this.mAdapter.add(uiConversation, newIndex);
/* 1136 */             ConversationListFragment.this.mAdapter.notifyDataSetChanged();
/*      */           }
/*      */ 
/*      */           public void onError(RongIMClient.ErrorCode e)
/*      */           {
/*      */           }
/*      */         }
/*      */         , new Conversation.ConversationType[] { Conversation.ConversationType.GROUP });
/*      */       }
/*      */       else
/*      */       {
/* 1145 */         RongIMClient.getInstance().getConversation(clearMessagesEvent.getType(), clearMessagesEvent.getTargetId(), new RongIMClient.ResultCallback()
/*      */         {
/*      */           public void onSuccess(Conversation conversation) {
/* 1148 */             if (conversation == null)
/* 1149 */               return;
/* 1150 */             UIConversation uiConversation = UIConversation.obtain(conversation, false);
/* 1151 */             int pos = ConversationListFragment.this.mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
/* 1152 */             if (pos >= 0) {
/* 1153 */               ConversationListFragment.this.mAdapter.remove(pos);
/*      */             }
/* 1155 */             int newPos = ConversationListUtils.findPositionForNewConversation(uiConversation, ConversationListFragment.this.mAdapter);
/* 1156 */             ConversationListFragment.this.mAdapter.add(uiConversation, newPos);
/* 1157 */             ConversationListFragment.this.mAdapter.notifyDataSetChanged();
/*      */           }
/*      */ 
/*      */           public void onError(RongIMClient.ErrorCode e)
/*      */           {
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.OnMessageSendErrorEvent sendErrorEvent) {
/* 1170 */     int index = this.mAdapter.findPosition(sendErrorEvent.getMessage().getConversationType(), sendErrorEvent.getMessage().getTargetId());
/*      */ 
/* 1172 */     if (index >= 0) {
/* 1173 */       UIConversation temp = (UIConversation)this.mAdapter.getItem(index);
/* 1174 */       temp.setUIConversationTime(sendErrorEvent.getMessage().getSentTime());
/* 1175 */       temp.setMessageContent(sendErrorEvent.getMessage().getContent());
/* 1176 */       temp.setConversationContent(temp.buildConversationContent(temp));
/* 1177 */       temp.setSentStatus(Message.SentStatus.FAILED);
/* 1178 */       this.mAdapter.remove(index);
/* 1179 */       int newPosition = ConversationListUtils.findPositionForNewConversation(temp, this.mAdapter);
/* 1180 */       this.mAdapter.add(temp, newPosition);
/* 1181 */       this.mAdapter.notifyDataSetChanged();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.QuitDiscussionEvent event) {
/* 1186 */     int index = this.mAdapter.findPosition(Conversation.ConversationType.DISCUSSION, event.getDiscussionId());
/*      */ 
/* 1188 */     if (index >= 0) {
/* 1189 */       if (!RongContext.getInstance().getConversationGatherState(Conversation.ConversationType.DISCUSSION.getName()).booleanValue())
/* 1190 */         this.mAdapter.remove(index);
/* 1191 */       this.mAdapter.notifyDataSetChanged();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.QuitGroupEvent event) {
/* 1196 */     int index = this.mAdapter.findPosition(Conversation.ConversationType.GROUP, event.getGroupId());
/* 1197 */     boolean gatherState = RongContext.getInstance().getConversationGatherState(Conversation.ConversationType.GROUP.getName()).booleanValue();
/*      */ 
/* 1199 */     if ((index >= 0) && (gatherState)) {
/* 1200 */       RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback(index)
/*      */       {
/*      */         public void onSuccess(List<Conversation> conversationList) {
/* 1203 */           if ((conversationList == null) || (conversationList.size() == 0)) {
/* 1204 */             if (this.val$index >= 0)
/* 1205 */               ConversationListFragment.this.mAdapter.remove(this.val$index);
/* 1206 */             ConversationListFragment.this.mAdapter.notifyDataSetChanged();
/* 1207 */             return;
/*      */           }
/* 1209 */           UIConversation uiConversation = ConversationListFragment.this.makeUIConversationFromList(conversationList);
/* 1210 */           int pos = ConversationListFragment.this.mAdapter.findPosition(uiConversation.getConversationType(), uiConversation.getConversationTargetId());
/* 1211 */           if (pos >= 0) {
/* 1212 */             ConversationListFragment.this.mAdapter.remove(pos);
/*      */           }
/* 1214 */           int newIndex = ConversationListUtils.findPositionForNewConversation(uiConversation, ConversationListFragment.this.mAdapter);
/* 1215 */           ConversationListFragment.this.mAdapter.add(uiConversation, newIndex);
/* 1216 */           ConversationListFragment.this.mAdapter.notifyDataSetChanged();
/*      */         }
/*      */ 
/*      */         public void onError(RongIMClient.ErrorCode e)
/*      */         {
/*      */         }
/*      */       }
/*      */       , new Conversation.ConversationType[] { Conversation.ConversationType.GROUP });
/*      */     }
/* 1225 */     else if (index >= 0) {
/* 1226 */       this.mAdapter.remove(index);
/* 1227 */       this.mAdapter.notifyDataSetChanged();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.AudioListenedEvent event)
/*      */   {
/* 1233 */     int originalIndex = this.mAdapter.findPosition(event.getConversationType(), event.getTargetId());
/*      */ 
/* 1235 */     if (originalIndex >= 0) {
/* 1236 */       UIConversation temp = (UIConversation)this.mAdapter.getItem(originalIndex);
/* 1237 */       if (temp.getLatestMessageId() == event.getLatestMessageId()) {
/* 1238 */         temp.setConversationContent(temp.buildConversationContent(temp));
/*      */       }
/* 1240 */       this.mAdapter.getView(originalIndex, this.mList.getChildAt(originalIndex - this.mList.getFirstVisiblePosition()), this.mList);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onPortraitItemClick(View v, UIConversation data)
/*      */   {
/* 1247 */     Conversation.ConversationType type = data.getConversationType();
/* 1248 */     if (RongContext.getInstance().getConversationGatherState(type.getName()).booleanValue()) {
/* 1249 */       RongIM.getInstance().startSubConversationList(getActivity(), type);
/*      */     } else {
/* 1251 */       if (RongContext.getInstance().getConversationListBehaviorListener() != null) {
/* 1252 */         boolean isDefault = RongContext.getInstance().getConversationListBehaviorListener().onConversationPortraitClick(getActivity(), type, data.getConversationTargetId());
/* 1253 */         if (isDefault)
/* 1254 */           return;
/*      */       }
/* 1256 */       data.setUnReadMessageCount(0);
/* 1257 */       RongIM.getInstance().startConversation(getActivity(), type, data.getConversationTargetId(), data.getUIConversationTitle());
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean onPortraitItemLongClick(View v, UIConversation data)
/*      */   {
/* 1264 */     Conversation.ConversationType type = data.getConversationType();
/*      */ 
/* 1266 */     if (RongContext.getInstance().getConversationListBehaviorListener() != null) {
/* 1267 */       boolean isDealt = RongContext.getInstance().getConversationListBehaviorListener().onConversationPortraitLongClick(getActivity(), type, data.getConversationTargetId());
/* 1268 */       if (isDealt)
/* 1269 */         return true;
/*      */     }
/* 1271 */     if (!RongContext.getInstance().getConversationGatherState(type.getName()).booleanValue()) {
/* 1272 */       buildMultiDialog(data);
/* 1273 */       return true;
/*      */     }
/* 1275 */     buildSingleDialog(data);
/* 1276 */     return true;
/*      */   }
/*      */ 
/*      */   public void onItemClick(AdapterView<?> parent, View view, int position, long id)
/*      */   {
/* 1282 */     UIConversation uiconversation = (UIConversation)this.mAdapter.getItem(position);
/* 1283 */     Conversation.ConversationType type = uiconversation.getConversationType();
/* 1284 */     if (RongContext.getInstance().getConversationGatherState(type.getName()).booleanValue()) {
/* 1285 */       RongIM.getInstance().startSubConversationList(getActivity(), type);
/*      */     } else {
/* 1287 */       if (RongContext.getInstance().getConversationListBehaviorListener() != null) {
/* 1288 */         boolean isDefault = RongContext.getInstance().getConversationListBehaviorListener().onConversationClick(getActivity(), view, uiconversation);
/* 1289 */         if (isDefault)
/* 1290 */           return;
/*      */       }
/* 1292 */       uiconversation.setUnReadMessageCount(0);
/* 1293 */       RongIM.getInstance().startConversation(getActivity(), type, uiconversation.getConversationTargetId(), uiconversation.getUIConversationTitle());
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
/*      */   {
/* 1299 */     UIConversation uiConversation = (UIConversation)this.mAdapter.getItem(position);
/* 1300 */     String type = uiConversation.getConversationType().getName();
/*      */ 
/* 1302 */     if (RongContext.getInstance().getConversationListBehaviorListener() != null) {
/* 1303 */       boolean isDealt = RongContext.getInstance().getConversationListBehaviorListener().onConversationLongClick(getActivity(), view, uiConversation);
/* 1304 */       if (isDealt)
/* 1305 */         return true;
/*      */     }
/* 1307 */     if (!RongContext.getInstance().getConversationGatherState(type).booleanValue()) {
/* 1308 */       buildMultiDialog(uiConversation);
/* 1309 */       return true;
/*      */     }
/* 1311 */     buildSingleDialog(uiConversation);
/* 1312 */     return true;
/*      */   }
/*      */ 
/*      */   private void buildMultiDialog(UIConversation uiConversation)
/*      */   {
/* 1318 */     String[] items = new String[2];
/*      */ 
/* 1320 */     if (uiConversation.isTop())
/* 1321 */       items[0] = RongContext.getInstance().getString(R.string.rc_conversation_list_dialog_cancel_top);
/*      */     else {
/* 1323 */       items[0] = RongContext.getInstance().getString(R.string.rc_conversation_list_dialog_set_top);
/*      */     }
/* 1325 */     items[1] = RongContext.getInstance().getString(R.string.rc_conversation_list_dialog_remove);
/*      */ 
/* 1327 */     ArraysDialogFragment.newInstance(uiConversation.getUIConversationTitle(), items).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener(uiConversation)
/*      */     {
/*      */       public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
/* 1330 */         if (which == 0)
/* 1331 */           RongIM.getInstance().setConversationToTop(this.val$uiConversation.getConversationType(), this.val$uiConversation.getConversationTargetId(), !this.val$uiConversation.isTop(), new RongIMClient.ResultCallback()
/*      */           {
/*      */             public void onSuccess(Boolean aBoolean)
/*      */             {
/* 1335 */               if (ConversationListFragment.17.this.val$uiConversation.isTop() == true)
/* 1336 */                 Toast.makeText(RongContext.getInstance(), ConversationListFragment.this.getString(R.string.rc_conversation_list_popup_cancel_top), 0).show();
/*      */               else
/* 1338 */                 Toast.makeText(RongContext.getInstance(), ConversationListFragment.this.getString(R.string.rc_conversation_list_dialog_set_top), 0).show();
/*      */             }
/*      */ 
/*      */             public void onError(RongIMClient.ErrorCode e)
/*      */             {
/*      */             }
/*      */           });
/* 1347 */         else if (which == 1)
/* 1348 */           RongIM.getInstance().removeConversation(this.val$uiConversation.getConversationType(), this.val$uiConversation.getConversationTargetId());
/*      */       }
/*      */     }).show(getFragmentManager());
/*      */   }
/*      */ 
/*      */   private void buildSingleDialog(UIConversation uiConversation)
/*      */   {
/* 1359 */     String[] items = new String[1];
/* 1360 */     items[0] = RongContext.getInstance().getString(R.string.rc_conversation_list_dialog_remove);
/*      */ 
/* 1362 */     ArraysDialogFragment.newInstance(uiConversation.getUIConversationTitle(), items).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener(uiConversation)
/*      */     {
/*      */       public void OnArraysDialogItemClick(DialogInterface dialog, int which)
/*      */       {
/* 1367 */         RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback()
/*      */         {
/*      */           public void onSuccess(List<Conversation> conversations) {
/* 1370 */             if ((conversations == null) || (conversations.size() == 0))
/* 1371 */               return;
/* 1372 */             for (Conversation conversation : conversations)
/* 1373 */               RongIM.getInstance().removeConversation(conversation.getConversationType(), conversation.getTargetId());
/*      */           }
/*      */ 
/*      */           public void onError(RongIMClient.ErrorCode errorCode)
/*      */           {
/*      */           }
/*      */         }
/*      */         , new Conversation.ConversationType[] { this.val$uiConversation.getConversationType() });
/*      */       }
/*      */     }).show(getFragmentManager());
/*      */   }
/*      */ 
/*      */   private void makeUiConversationList(List<Conversation> conversationList)
/*      */   {
/* 1396 */     for (Conversation conversation : conversationList) {
/* 1397 */       Conversation.ConversationType conversationType = conversation.getConversationType();
/* 1398 */       boolean gatherState = RongContext.getInstance().getConversationGatherState(conversationType.getName()).booleanValue();
/* 1399 */       int originalIndex = this.mAdapter.findPosition(conversationType, conversation.getTargetId());
/*      */       UIConversation uiCon;
/* 1401 */       if (originalIndex >= 0) {
/* 1402 */         UIConversation uiCon = (UIConversation)this.mAdapter.getItem(originalIndex);
/* 1403 */         if (!uiCon.getMentionedFlag())
/* 1404 */           uiCon.setMentionedFlag(conversation.getMentionedCount() > 0);
/*      */       }
/*      */       else {
/* 1407 */         uiCon = UIConversation.obtain(conversation, gatherState);
/* 1408 */         this.mAdapter.add(uiCon);
/*      */       }
/* 1410 */       refreshUnreadCount(uiCon.getConversationType(), uiCon.getConversationTargetId());
/*      */     }
/*      */   }
/*      */ 
/*      */   private UIConversation makeUiConversation(Message message, int pos)
/*      */   {
/*      */     UIConversation uiConversation;
/* 1418 */     if (pos >= 0) {
/* 1419 */       UIConversation uiConversation = (UIConversation)this.mAdapter.getItem(pos);
/* 1420 */       if (uiConversation != null) {
/* 1421 */         uiConversation.setMessageContent(message.getContent());
/* 1422 */         if (message.getMessageDirection() == Message.MessageDirection.SEND) {
/* 1423 */           uiConversation.setUIConversationTime(message.getSentTime());
/* 1424 */           uiConversation.setConversationSenderId(RongIM.getInstance().getCurrentUserId());
/*      */         } else {
/* 1426 */           uiConversation.setUIConversationTime(message.getSentTime());
/* 1427 */           uiConversation.setConversationSenderId(message.getSenderUserId());
/*      */         }
/* 1429 */         uiConversation.setConversationTargetId(message.getTargetId());
/* 1430 */         uiConversation.setConversationContent(uiConversation.buildConversationContent(uiConversation));
/* 1431 */         uiConversation.setSentStatus(message.getSentStatus());
/* 1432 */         uiConversation.setLatestMessageId(message.getMessageId());
/* 1433 */         if (!uiConversation.getMentionedFlag()) {
/* 1434 */           MentionedInfo mentionedInfo = message.getContent().getMentionedInfo();
/* 1435 */           if ((mentionedInfo != null) && (((mentionedInfo.getType().equals(MentionedInfo.MentionedType.PART)) && (mentionedInfo.getMentionedUserIdList().contains(RongIMClient.getInstance().getCurrentUserId()))) || (mentionedInfo.getType().equals(MentionedInfo.MentionedType.ALL))))
/*      */           {
/* 1438 */             uiConversation.setMentionedFlag(true);
/*      */           }
/*      */         }
/* 1441 */         String title = "";
/* 1442 */         Uri iconUri = null;
/* 1443 */         UserInfo userInfo = message.getContent().getUserInfo();
/* 1444 */         Conversation.ConversationType conversationType = message.getConversationType();
/*      */ 
/* 1448 */         if ((userInfo != null) && (message.getTargetId().equals(userInfo.getUserId())) && ((conversationType.equals(Conversation.ConversationType.PRIVATE)) || (conversationType.equals(Conversation.ConversationType.SYSTEM))) && (((uiConversation.getUIConversationTitle() != null) && (userInfo.getName() != null) && (!userInfo.getName().equals(uiConversation.getUIConversationTitle()))) || ((uiConversation.getIconUrl() != null) && (userInfo.getPortraitUri() != null) && (!userInfo.getPortraitUri().equals(uiConversation.getIconUrl())))))
/*      */         {
/* 1454 */           iconUri = userInfo.getPortraitUri();
/* 1455 */           title = userInfo.getName();
/* 1456 */           RongIMClient.getInstance().updateConversationInfo(message.getConversationType(), message.getTargetId(), title, iconUri != null ? iconUri.toString() : "", null);
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 1461 */       uiConversation = UIConversation.obtain(message, RongContext.getInstance().getConversationGatherState(message.getConversationType().getName()).booleanValue());
/*      */     }
/* 1463 */     return uiConversation;
/*      */   }
/*      */ 
/*      */   private UIConversation makeUIConversationFromList(List<Conversation> conversations)
/*      */   {
/* 1470 */     int unreadCount = 0;
/* 1471 */     boolean topFlag = false;
/* 1472 */     boolean isMentioned = false;
/* 1473 */     Conversation newest = (Conversation)conversations.get(0);
/*      */ 
/* 1475 */     for (Conversation conversation : conversations) {
/* 1476 */       if (newest.isTop()) {
/* 1477 */         if ((conversation.isTop()) && (conversation.getSentTime() > newest.getSentTime())) {
/* 1478 */           newest = conversation;
/*      */         }
/*      */       }
/* 1481 */       else if ((conversation.isTop()) || (conversation.getSentTime() > newest.getSentTime())) {
/* 1482 */         newest = conversation;
/*      */       }
/*      */ 
/* 1485 */       if (conversation.isTop()) {
/* 1486 */         topFlag = true;
/*      */       }
/* 1488 */       if (conversation.getMentionedCount() > 0) {
/* 1489 */         isMentioned = true;
/*      */       }
/*      */ 
/* 1492 */       unreadCount += conversation.getUnreadMessageCount();
/*      */     }
/*      */ 
/* 1495 */     UIConversation uiConversation = UIConversation.obtain(newest, RongContext.getInstance().getConversationGatherState(newest.getConversationType().getName()).booleanValue());
/* 1496 */     uiConversation.setUnReadMessageCount(unreadCount);
/* 1497 */     uiConversation.setTop(topFlag);
/* 1498 */     uiConversation.setMentionedFlag(isMentioned);
/* 1499 */     return uiConversation;
/*      */   }
/*      */ 
/*      */   private void refreshUnreadCount(Conversation.ConversationType type, String targetId) {
/* 1503 */     if (this.mAdapter == null) {
/* 1504 */       RLog.d(TAG, "the conversation list adapter is null.");
/* 1505 */       return;
/*      */     }
/*      */ 
/* 1508 */     if (RongContext.getInstance().getConversationGatherState(type.getName()).booleanValue()) {
/* 1509 */       RongIM.getInstance().getUnreadCount(new RongIMClient.ResultCallback(type, targetId)
/*      */       {
/*      */         public void onSuccess(Integer count) {
/* 1512 */           int curPos = ConversationListFragment.this.mAdapter.findPosition(this.val$type, this.val$targetId);
/* 1513 */           if (curPos >= 0) {
/* 1514 */             ((UIConversation)ConversationListFragment.this.mAdapter.getItem(curPos)).setUnReadMessageCount(count.intValue());
/* 1515 */             ConversationListFragment.this.mAdapter.getView(curPos, ConversationListFragment.this.mList.getChildAt(curPos - ConversationListFragment.this.mList.getFirstVisiblePosition()), ConversationListFragment.this.mList);
/*      */           }
/*      */         }
/*      */ 
/*      */         public void onError(RongIMClient.ErrorCode e)
/*      */         {
/* 1521 */           System.err.print("Throw exception when get unread message count from ipc remote side!");
/*      */         }
/*      */       }
/*      */       , new Conversation.ConversationType[] { type });
/*      */     }
/*      */     else
/*      */     {
/* 1525 */       RongIM.getInstance().getUnreadCount(type, targetId, new RongIMClient.ResultCallback(type, targetId)
/*      */       {
/*      */         public void onSuccess(Integer integer) {
/* 1528 */           int curPos = ConversationListFragment.this.mAdapter.findPosition(this.val$type, this.val$targetId);
/* 1529 */           if (curPos >= 0) {
/* 1530 */             ((UIConversation)ConversationListFragment.this.mAdapter.getItem(curPos)).setUnReadMessageCount(integer.intValue());
/* 1531 */             ConversationListFragment.this.mAdapter.getView(curPos, ConversationListFragment.this.mList.getChildAt(curPos - ConversationListFragment.this.mList.getFirstVisiblePosition()), ConversationListFragment.this.mList);
/*      */           }
/*      */         }
/*      */ 
/*      */         public void onError(RongIMClient.ErrorCode e)
/*      */         {
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.fragment.ConversationListFragment
 * JD-Core Version:    0.6.0
 */
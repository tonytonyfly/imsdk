/*      */ package io.rong.imkit.fragment;
/*      */ 
/*      */ import android.content.res.Resources;
/*      */ import android.content.res.Resources.NotFoundException;
/*      */ import android.net.Uri;
/*      */ import android.os.Bundle;
/*      */ import android.os.Handler;
/*      */ import android.support.v4.app.FragmentActivity;
/*      */ import android.text.TextUtils;
/*      */ import android.view.GestureDetector;
/*      */ import android.view.GestureDetector.SimpleOnGestureListener;
/*      */ import android.view.LayoutInflater;
/*      */ import android.view.MotionEvent;
/*      */ import android.view.View;
/*      */ import android.view.View.OnClickListener;
/*      */ import android.view.View.OnTouchListener;
/*      */ import android.view.ViewGroup;
/*      */ import android.view.animation.AlphaAnimation;
/*      */ import android.view.animation.Animation;
/*      */ import android.view.animation.Animation.AnimationListener;
/*      */ import android.view.animation.AnimationSet;
/*      */ import android.view.animation.TranslateAnimation;
/*      */ import android.widget.AbsListView;
/*      */ import android.widget.AbsListView.OnScrollListener;
/*      */ import android.widget.AdapterView;
/*      */ import android.widget.AdapterView.OnItemClickListener;
/*      */ import android.widget.Button;
/*      */ import android.widget.ImageButton;
/*      */ import android.widget.ListView;
/*      */ import android.widget.TextView;
/*      */ import android.widget.Toast;
/*      */ import io.rong.common.RLog;
/*      */ import io.rong.common.SystemUtils;
/*      */ import io.rong.eventbus.EventBus;
/*      */ import io.rong.imkit.R.bool;
/*      */ import io.rong.imkit.R.id;
/*      */ import io.rong.imkit.R.layout;
/*      */ import io.rong.imkit.R.string;
/*      */ import io.rong.imkit.RongContext;
/*      */ import io.rong.imkit.RongIM;
/*      */ import io.rong.imkit.manager.AudioPlayManager;
/*      */ import io.rong.imkit.model.EmojiMessageAdapter;
/*      */ import io.rong.imkit.model.Event.ConnectEvent;
/*      */ import io.rong.imkit.model.Event.GroupUserInfoEvent;
/*      */ import io.rong.imkit.model.Event.InputViewEvent;
/*      */ import io.rong.imkit.model.Event.MessageDeleteEvent;
/*      */ import io.rong.imkit.model.Event.MessageRecallEvent;
/*      */ import io.rong.imkit.model.Event.MessagesClearEvent;
/*      */ import io.rong.imkit.model.Event.OnMessageSendErrorEvent;
/*      */ import io.rong.imkit.model.Event.OnReceiveMessageEvent;
/*      */ import io.rong.imkit.model.Event.OnReceiveMessageProgressEvent;
/*      */ import io.rong.imkit.model.Event.PlayAudioEvent;
/*      */ import io.rong.imkit.model.Event.PublicServiceFollowableEvent;
/*      */ import io.rong.imkit.model.Event.ReadReceiptEvent;
/*      */ import io.rong.imkit.model.Event.ReadReceiptRequestEvent;
/*      */ import io.rong.imkit.model.Event.ReadReceiptResponseEvent;
/*      */ import io.rong.imkit.model.Event.RemoteMessageRecallEvent;
/*      */ import io.rong.imkit.model.GroupUserInfo;
/*      */ import io.rong.imkit.model.UIMessage;
/*      */ import io.rong.imkit.widget.InputView.Event;
/*      */ import io.rong.imkit.widget.adapter.MessageListAdapter;
/*      */ import io.rong.imkit.widget.adapter.MessageListAdapter.OnItemHandlerListener;
/*      */ import io.rong.imkit.widget.provider.InputProvider.MainInputProvider;
/*      */ import io.rong.imlib.IRongCallback.ISendMessageCallback;
/*      */ import io.rong.imlib.RongIMClient;
/*      */ import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
/*      */ import io.rong.imlib.RongIMClient.ErrorCode;
/*      */ import io.rong.imlib.RongIMClient.OperationCallback;
/*      */ import io.rong.imlib.RongIMClient.ResultCallback;
/*      */ import io.rong.imlib.RongIMClient.SendImageMessageCallback;
/*      */ import io.rong.imlib.model.Conversation;
/*      */ import io.rong.imlib.model.Conversation.ConversationType;
/*      */ import io.rong.imlib.model.Message.MessageDirection;
/*      */ import io.rong.imlib.model.Message.ReceivedStatus;
/*      */ import io.rong.imlib.model.Message.SentStatus;
/*      */ import io.rong.imlib.model.MessageContent;
/*      */ import io.rong.imlib.model.PublicServiceProfile;
/*      */ import io.rong.imlib.model.ReadReceiptInfo;
/*      */ import io.rong.imlib.model.UserInfo;
/*      */ import io.rong.message.FileMessage;
/*      */ import io.rong.message.ImageMessage;
/*      */ import io.rong.message.LocationMessage;
/*      */ import io.rong.message.ReadReceiptMessage;
/*      */ import io.rong.message.RecallNotificationMessage;
/*      */ import io.rong.message.VoiceMessage;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ 
/*      */ public class MessageListFragment extends UriFragment
/*      */   implements AbsListView.OnScrollListener
/*      */ {
/*      */   private static final String TAG = "MessageListFragment";
/*      */   MessageListAdapter mAdapter;
/*      */   GestureDetector mGestureDetector;
/*      */   ListView mList;
/*      */   Conversation mConversation;
/*      */   int mUnreadCount;
/*      */   int mNewMessageCount;
/*      */   int mLastVisiblePosition;
/*      */   Button mUnreadBtn;
/*      */   ImageButton mNewMessageBtn;
/*      */   TextView mNewMessageTextView;
/*      */   boolean isShowUnreadMessageState;
/*      */   boolean isShowNewMessageState;
/*      */   int mMessageleft;
/*      */   boolean needEvaluateForRobot;
/*      */   boolean robotMode;
/*      */   static final int REQ_LIST = 1;
/*      */   static final int RENDER_LIST = 2;
/*      */   static final int REFRESH_LIST_WHILE_RECEIVE_MESSAGE = 3;
/*      */   static final int REFRESH_ITEM = 4;
/*      */   static final int REQ_HISTORY = 5;
/*      */   static final int RENDER_HISTORY = 6;
/*      */   static final int REFRESH_ITEM_READ_RECEIPT = 7;
/*      */   static final int REQ_REMOTE_HISTORY = 8;
/*      */   static final int NOTIFY_LIST = 9;
/*      */   static final int RESET_LIST_STACK = 10;
/*      */   static final int DELETE_MESSAGE = 11;
/*      */   static final int REQ_UNREAD = 12;
/*      */   private static final int LISTVIEW_SHOW_COUNT = 5;
/*      */   View mHeaderView;
/*      */   private boolean isOnClickBtn;
/*      */   private boolean isShowWithoutConnected;
/*      */   private List<io.rong.imlib.model.Message> mUnreadMentionMessages;
/*      */   private boolean mHasReceivedMessage;
/*      */   AbsListView.OnScrollListener onScrollListener;
/*      */   boolean mHasMoreLocalMessages;
/*      */   boolean mHasMoreRemoteMessages;
/*      */   long mLastRemoteMessageTime;
/*      */   boolean isLoading;
/*      */ 
/*      */   public MessageListFragment()
/*      */   {
/*   86 */     this.mMessageleft = -1;
/*      */ 
/*   89 */     this.needEvaluateForRobot = false;
/*      */ 
/*   91 */     this.robotMode = true;
/*      */ 
/*  109 */     this.isShowWithoutConnected = false;
/*      */ 
/*  287 */     this.mHasMoreLocalMessages = true;
/*  288 */     this.mHasMoreRemoteMessages = true;
/*  289 */     this.mLastRemoteMessageTime = 0L;
/*  290 */     this.isLoading = false;
/*      */   }
/*      */ 
/*      */   public void onCreate(Bundle savedInstanceState)
/*      */   {
/*  115 */     super.onCreate(savedInstanceState);
/*  116 */     RongContext.getInstance().getEventBus().register(this);
/*      */ 
/*  118 */     this.isShowUnreadMessageState = RongContext.getInstance().getUnreadMessageState();
/*  119 */     this.isShowNewMessageState = RongContext.getInstance().getNewMessageState();
/*      */ 
/*  121 */     if (EmojiMessageAdapter.getInstance() == null) {
/*  122 */       EmojiMessageAdapter.init(RongContext.getInstance());
/*      */     }
/*  124 */     this.mAdapter = new MessageListAdapter(getActivity());
/*  125 */     this.mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener()
/*      */     {
/*      */       public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
/*      */       {
/*  129 */         if ((distanceY > 0.0F) && (MessageListFragment.this.mNewMessageCount >= 0) && 
/*  130 */           (MessageListFragment.this.mList.getLastVisiblePosition() >= MessageListFragment.this.mList.getCount() - MessageListFragment.this.mNewMessageCount)) {
/*  131 */           MessageListFragment.this.mNewMessageTextView.setText(MessageListFragment.this.mList.getCount() - MessageListFragment.this.mList.getLastVisiblePosition() + "");
/*  132 */           MessageListFragment.this.mNewMessageCount = (MessageListFragment.this.mList.getCount() - MessageListFragment.this.mList.getLastVisiblePosition() - 1);
/*  133 */           if (MessageListFragment.this.mNewMessageCount > 99)
/*  134 */             MessageListFragment.this.mNewMessageTextView.setText("99+");
/*      */           else {
/*  136 */             MessageListFragment.this.mNewMessageTextView.setText(MessageListFragment.this.mNewMessageCount + "");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  142 */         if (MessageListFragment.this.mNewMessageCount == 0) {
/*  143 */           MessageListFragment.this.mNewMessageBtn.setVisibility(8);
/*  144 */           MessageListFragment.this.mNewMessageTextView.setVisibility(8);
/*      */         }
/*  146 */         return super.onScroll(e1, e2, distanceX, distanceY);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener)
/*      */   {
/*  154 */     this.onScrollListener = onScrollListener;
/*      */   }
/*      */ 
/*      */   protected void initFragment(Uri uri)
/*      */   {
/*  159 */     RLog.d("MessageListFragment", "initFragment " + uri);
/*  160 */     String typeStr = uri.getLastPathSegment().toUpperCase();
/*  161 */     Conversation.ConversationType type = Conversation.ConversationType.valueOf(typeStr);
/*      */ 
/*  163 */     String targetId = uri.getQueryParameter("targetId");
/*  164 */     String title = uri.getQueryParameter("title");
/*      */ 
/*  166 */     if ((TextUtils.isEmpty(targetId)) || (type == null)) {
/*  167 */       return;
/*      */     }
/*  169 */     this.mConversation = Conversation.obtain(type, targetId, title);
/*      */ 
/*  171 */     if (this.mAdapter != null) {
/*  172 */       getHandler().post(new Runnable()
/*      */       {
/*      */         public void run() {
/*  175 */           MessageListFragment.this.mAdapter.clear();
/*  176 */           MessageListFragment.this.mAdapter.notifyDataSetChanged();
/*      */         }
/*      */       });
/*      */     }
/*  181 */     this.mNewMessageBtn.setOnClickListener(new View.OnClickListener()
/*      */     {
/*      */       public void onClick(View v)
/*      */       {
/*  185 */         MessageListFragment.this.getHandler().postDelayed(new MessageListFragment.ScrollRunnable(MessageListFragment.this), 500L);
/*      */ 
/*  190 */         MessageListFragment.this.mList.smoothScrollToPosition(MessageListFragment.this.mList.getCount() + 1);
/*      */ 
/*  193 */         MessageListFragment.this.mNewMessageCount = 0;
/*  194 */         MessageListFragment.this.mNewMessageBtn.setVisibility(8);
/*  195 */         MessageListFragment.this.mNewMessageTextView.setVisibility(8);
/*      */       }
/*      */     });
/*  199 */     if (RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
/*  200 */       RLog.e("MessageListFragment", "initFragment Not connected yet.");
/*  201 */       this.isShowWithoutConnected = true;
/*  202 */       return;
/*      */     }
/*  204 */     getConversation();
/*      */   }
/*      */ 
/*      */   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
/*      */   {
/*  209 */     View view = inflater.inflate(R.layout.rc_fr_messagelist, container, false);
/*  210 */     this.mUnreadBtn = ((Button)findViewById(view, R.id.rc_unread_message_count));
/*  211 */     this.mNewMessageBtn = ((ImageButton)findViewById(view, R.id.rc_new_message_count));
/*  212 */     this.mNewMessageTextView = ((TextView)findViewById(view, R.id.rc_new_message_number));
/*  213 */     this.mList = ((ListView)findViewById(view, R.id.rc_list));
/*  214 */     this.mHeaderView = inflater.inflate(R.layout.rc_item_progress, null);
/*  215 */     this.mList.addHeaderView(this.mHeaderView);
/*  216 */     this.mList.setOnScrollListener(this);
/*  217 */     this.mList.setSelectionAfterHeaderView();
/*      */ 
/*  219 */     this.mAdapter.setOnItemHandlerListener(new MessageListAdapter.OnItemHandlerListener()
/*      */     {
/*      */       public void onWarningViewClick(int position, io.rong.imlib.model.Message data, View v)
/*      */       {
/*  223 */         RongIM.getInstance().deleteMessages(new int[] { data.getMessageId() }, new RongIMClient.ResultCallback(data)
/*      */         {
/*      */           public void onSuccess(Boolean aBoolean) {
/*  226 */             if (aBoolean.booleanValue()) {
/*  227 */               this.val$data.setMessageId(0);
/*  228 */               if ((this.val$data.getContent() instanceof ImageMessage))
/*  229 */                 RongIM.getInstance().sendImageMessage(this.val$data, "", "", new RongIMClient.SendImageMessageCallback()
/*      */                 {
/*      */                   public void onAttached(io.rong.imlib.model.Message message)
/*      */                   {
/*      */                   }
/*      */ 
/*      */                   public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode code)
/*      */                   {
/*      */                   }
/*      */ 
/*      */                   public void onSuccess(io.rong.imlib.model.Message message)
/*      */                   {
/*      */                   }
/*      */ 
/*      */                   public void onProgress(io.rong.imlib.model.Message message, int progress)
/*      */                   {
/*      */                   }
/*      */                 });
/*  250 */               else if ((this.val$data.getContent() instanceof LocationMessage))
/*  251 */                 RongIM.getInstance().sendLocationMessage(this.val$data, null, null, null);
/*  252 */               else if ((this.val$data.getContent() instanceof FileMessage))
/*  253 */                 RongIM.getInstance().sendMediaMessage(this.val$data, null, null, null);
/*      */               else
/*  255 */                 RongIM.getInstance().sendMessage(this.val$data, null, null, new IRongCallback.ISendMessageCallback()
/*      */                 {
/*      */                   public void onAttached(io.rong.imlib.model.Message message)
/*      */                   {
/*      */                   }
/*      */ 
/*      */                   public void onSuccess(io.rong.imlib.model.Message message)
/*      */                   {
/*      */                   }
/*      */ 
/*      */                   public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode)
/*      */                   {
/*      */                   }
/*      */                 });
/*      */             }
/*      */           }
/*      */ 
/*      */           public void onError(RongIMClient.ErrorCode e)
/*      */           {
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*  283 */     return view;
/*      */   }
/*      */ 
/*      */   public void onScrollStateChanged(AbsListView view, int scrollState)
/*      */   {
/*  294 */     switch (scrollState) {
/*      */     case 0:
/*  296 */       if ((view.getFirstVisiblePosition() == 0) && (this.mAdapter.getCount() > 0) && (this.mHasMoreLocalMessages) && (!this.isLoading)) {
/*  297 */         this.isLoading = true;
/*  298 */         getHandler().sendEmptyMessage(5); } else {
/*  299 */         if ((view.getFirstVisiblePosition() != 0) || (this.mHasMoreLocalMessages) || (!this.mHasMoreRemoteMessages) || (this.isLoading) || (this.mConversation.getConversationType() == Conversation.ConversationType.CHATROOM) || (this.mConversation.getConversationType() == Conversation.ConversationType.APP_PUBLIC_SERVICE) || (this.mConversation.getConversationType() == Conversation.ConversationType.PUBLIC_SERVICE))
/*      */         {
/*      */           break;
/*      */         }
/*      */ 
/*  306 */         this.isLoading = true;
/*  307 */         this.mLastRemoteMessageTime = ((UIMessage)this.mAdapter.getItem(0)).getSentTime();
/*  308 */         getHandler().sendEmptyMessage(8);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  313 */     if (this.onScrollListener != null)
/*  314 */       this.onScrollListener.onScrollStateChanged(view, scrollState);
/*      */   }
/*      */ 
/*      */   public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
/*      */   {
/*  320 */     if (this.onScrollListener != null) {
/*  321 */       this.onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
/*      */     }
/*  323 */     if (firstVisibleItem + visibleItemCount >= totalItemCount - this.mNewMessageCount) {
/*  324 */       this.mNewMessageCount = 0;
/*  325 */       this.mNewMessageBtn.setVisibility(8);
/*  326 */       this.mNewMessageTextView.setVisibility(8);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onViewCreated(View view, Bundle savedInstanceState)
/*      */   {
/*  332 */     if (getActionBarHandler() != null) {
/*  333 */       getActionBarHandler().onTitleChanged(this.mConversation.getConversationTitle());
/*      */     }
/*      */ 
/*  336 */     this.mList.setOnTouchListener(new View.OnTouchListener()
/*      */     {
/*      */       public boolean onTouch(View v, MotionEvent event) {
/*  339 */         if ((event.getAction() == 2) || (event.getAction() == 0))
/*      */         {
/*  341 */           EventBus.getDefault().post(Event.InputViewEvent.obtain(false));
/*  342 */           if ((event.getAction() == 2) && (MessageListFragment.this.mList.getCount() == 0) && (MessageListFragment.this.mHasMoreRemoteMessages) && (MessageListFragment.this.mConversation.getConversationType() != Conversation.ConversationType.CHATROOM) && (MessageListFragment.this.mConversation.getConversationType() != Conversation.ConversationType.APP_PUBLIC_SERVICE) && (MessageListFragment.this.mConversation.getConversationType() != Conversation.ConversationType.PUBLIC_SERVICE))
/*      */           {
/*  346 */             MessageListFragment.this.isLoading = true;
/*  347 */             MessageListFragment.this.getHandler().sendEmptyMessage(8);
/*      */           }
/*      */         }
/*  350 */         MessageListFragment.this.mGestureDetector.onTouchEvent(event);
/*  351 */         return false;
/*      */       }
/*      */     });
/*  355 */     this.mList.setAdapter(this.mAdapter);
/*      */ 
/*  357 */     this.mList.setOnItemClickListener(new AdapterView.OnItemClickListener()
/*      */     {
/*      */       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/*  360 */         RongContext.getInstance().getPrimaryInputProvider().onInactive(view.getContext());
/*  361 */         RongContext.getInstance().getSecondaryInputProvider().onInactive(view.getContext());
/*      */       }
/*      */     });
/*  365 */     super.onViewCreated(view, savedInstanceState);
/*      */   }
/*      */ 
/*      */   public boolean onBackPressed()
/*      */   {
/*  370 */     return false;
/*      */   }
/*      */ 
/*      */   private List<UIMessage> filterMessage(List<UIMessage> srcList) {
/*  374 */     List destList = null;
/*      */ 
/*  376 */     if (this.mAdapter.getCount() > 0) {
/*  377 */       destList = new ArrayList();
/*  378 */       for (int i = 0; i < this.mAdapter.getCount(); i++)
/*  379 */         for (UIMessage msg : srcList) {
/*  380 */           if (destList.contains(msg)) {
/*      */             continue;
/*      */           }
/*  383 */           if (msg.getMessageId() != ((UIMessage)this.mAdapter.getItem(i)).getMessageId())
/*  384 */             destList.add(msg);
/*      */         }
/*      */     }
/*      */     else {
/*  388 */       destList = srcList;
/*      */     }
/*      */ 
/*  391 */     return destList;
/*      */   }
/*      */ 
/*      */   public boolean handleMessage(android.os.Message msg)
/*      */   {
/*  396 */     RLog.d("MessageListFragment", "MessageListFragment msg : " + msg.what);
/*      */     UIMessage message;
/*  397 */     switch (msg.what) {
/*      */     case 2:
/*  399 */       if ((msg.obj != null) && ((msg.obj instanceof List))) {
/*  400 */         List list = (List)msg.obj;
/*  401 */         this.mAdapter.clear();
/*  402 */         this.mAdapter.addCollection(filterMessage(list));
/*      */ 
/*  404 */         if (list.size() <= 5) {
/*  405 */           this.mList.setStackFromBottom(false);
/*  406 */           this.mList.setTranscriptMode(2);
/*      */         } else {
/*  408 */           this.mList.setStackFromBottom(true);
/*  409 */           this.mList.setTranscriptMode(2);
/*      */         }
/*  411 */         this.mAdapter.notifyDataSetChanged();
/*  412 */         getHandler().sendEmptyMessage(10);
/*      */       }
/*      */ 
/*  416 */       if ((this.mUnreadMentionMessages != null) && (this.mUnreadMentionMessages.size() > 0)) {
/*  417 */         UIMessage uiMessage = UIMessage.obtain((io.rong.imlib.model.Message)this.mUnreadMentionMessages.get(0));
/*  418 */         int position = this.mAdapter.findPosition(uiMessage);
/*  419 */         this.mList.smoothScrollToPosition(position);
/*      */       }
/*      */ 
/*  423 */       if (this.mUnreadCount < 10) break;
/*  424 */       TranslateAnimation animation = new TranslateAnimation(300.0F, 0.0F, 0.0F, 0.0F);
/*  425 */       AlphaAnimation animation1 = new AlphaAnimation(0.0F, 1.0F);
/*  426 */       animation.setDuration(1000L);
/*  427 */       animation1.setDuration(2000L);
/*  428 */       AnimationSet set = new AnimationSet(true);
/*  429 */       set.addAnimation(animation);
/*  430 */       set.addAnimation(animation1);
/*  431 */       this.mUnreadBtn.setVisibility(0);
/*  432 */       this.mUnreadBtn.startAnimation(set);
/*  433 */       set.setAnimationListener(new Animation.AnimationListener()
/*      */       {
/*      */         public void onAnimationStart(Animation animation)
/*      */         {
/*      */         }
/*      */ 
/*      */         public void onAnimationEnd(Animation animation)
/*      */         {
/*  441 */           MessageListFragment.this.getHandler().postDelayed(new Runnable()
/*      */           {
/*      */             public void run() {
/*  444 */               if (!MessageListFragment.this.isOnClickBtn) {
/*  445 */                 TranslateAnimation animation = new TranslateAnimation(0.0F, 700.0F, 0.0F, 0.0F);
/*  446 */                 animation.setDuration(700L);
/*  447 */                 animation.setFillAfter(true);
/*  448 */                 MessageListFragment.this.mUnreadBtn.startAnimation(animation);
/*      */               }
/*      */             }
/*      */           }
/*      */           , 4000L);
/*      */         }
/*      */ 
/*      */         public void onAnimationRepeat(Animation animation)
/*      */         {
/*      */         }
/*      */       });
/*  462 */       break;
/*      */     case 6:
/*  465 */       if (!(msg.obj instanceof List)) break;
/*  466 */       List list = (List)msg.obj;
/*      */ 
/*  468 */       for (UIMessage item : list) {
/*  469 */         this.mAdapter.add(item, 0);
/*      */       }
/*      */ 
/*  472 */       this.mList.setTranscriptMode(0);
/*  473 */       this.mList.setStackFromBottom(false);
/*      */ 
/*  475 */       int index = this.mList.getFirstVisiblePosition();
/*  476 */       this.mAdapter.notifyDataSetChanged();
/*      */ 
/*  478 */       if (index == 0) {
/*  479 */         this.mList.setSelection(list.size());
/*      */       }
/*  481 */       break;
/*      */     case 1:
/*  484 */       this.mAdapter.clear();
/*  485 */       this.mAdapter.notifyDataSetChanged();
/*  486 */       EmojiMessageAdapter.getInstance().getLatestMessages(this.mConversation.getConversationType(), this.mConversation.getTargetId(), 30, new RongIMClient.ResultCallback()
/*      */       {
/*      */         public void onSuccess(List<UIMessage> messages)
/*      */         {
/*  490 */           RLog.d("MessageListFragment", "getLatestMessages, onSuccess " + messages.size());
/*  491 */           MessageListFragment.this.mHasMoreLocalMessages = (messages.size() == 30);
/*  492 */           MessageListFragment.this.mList.removeHeaderView(MessageListFragment.this.mHeaderView);
/*  493 */           MessageListFragment.this.isLoading = false;
/*  494 */           MessageListFragment.this.getHandler().obtainMessage(2, messages).sendToTarget();
/*      */         }
/*      */ 
/*      */         public void onError(RongIMClient.ErrorCode e)
/*      */         {
/*  499 */           RLog.e("MessageListFragment", "getLatestMessages, " + e.toString());
/*  500 */           MessageListFragment.this.mHasMoreLocalMessages = false;
/*  501 */           MessageListFragment.this.isLoading = false;
/*  502 */           MessageListFragment.this.mList.removeHeaderView(MessageListFragment.this.mHeaderView);
/*      */         }
/*      */       });
/*  505 */       break;
/*      */     case 10:
/*  507 */       resetListViewStack();
/*  508 */       this.mAdapter.notifyDataSetChanged();
/*  509 */       break;
/*      */     case 4:
/*  511 */       int position = ((Integer)msg.obj).intValue();
/*      */ 
/*  513 */       if ((position < this.mList.getFirstVisiblePosition()) || (position > this.mList.getLastVisiblePosition())) break;
/*  514 */       RLog.d("MessageListFragment", "REFRESH_ITEM Index:" + position);
/*  515 */       this.mAdapter.getView(position, this.mList.getChildAt(position - this.mList.getFirstVisiblePosition() + this.mList.getHeaderViewsCount()), this.mList); break;
/*      */     case 7:
/*  521 */       int pos = ((Integer)msg.obj).intValue();
/*      */ 
/*  523 */       if ((pos < this.mList.getFirstVisiblePosition()) || (pos > this.mList.getLastVisiblePosition())) break;
/*  524 */       RLog.d("MessageListFragment", "REFRESH_ITEM Index:" + pos);
/*  525 */       this.mAdapter.getView(pos, this.mList.getChildAt(pos - this.mList.getFirstVisiblePosition() + this.mList.getHeaderViewsCount()), this.mList); break;
/*      */     case 5:
/*  529 */       message = (UIMessage)this.mAdapter.getItem(0);
/*  530 */       this.mList.addHeaderView(this.mHeaderView);
/*  531 */       EmojiMessageAdapter.getInstance().getHistoryMessages(this.mConversation.getConversationType(), this.mConversation.getTargetId(), message.getMessageId(), 30, new RongIMClient.ResultCallback()
/*      */       {
/*      */         public void onSuccess(List<UIMessage> messages) {
/*  534 */           RLog.d("MessageListFragment", "getHistoryMessages, onSuccess " + messages.size());
/*  535 */           MessageListFragment.this.mHasMoreLocalMessages = (messages.size() == 30);
/*  536 */           MessageListFragment.this.mList.removeHeaderView(MessageListFragment.this.mHeaderView);
/*  537 */           MessageListFragment.this.isLoading = false;
/*  538 */           MessageListFragment.this.getHandler().obtainMessage(6, messages).sendToTarget();
/*      */         }
/*      */ 
/*      */         public void onError(RongIMClient.ErrorCode e)
/*      */         {
/*  543 */           MessageListFragment.this.mHasMoreLocalMessages = false;
/*  544 */           MessageListFragment.this.mList.removeHeaderView(MessageListFragment.this.mHeaderView);
/*  545 */           MessageListFragment.this.isLoading = false;
/*  546 */           RLog.e("MessageListFragment", "getHistoryMessages, " + e.toString());
/*      */         }
/*      */       });
/*  549 */       break;
/*      */     case 8:
/*  551 */       this.mList.addHeaderView(this.mHeaderView);
/*  552 */       EmojiMessageAdapter.getInstance().getRemoteHistoryMessages(this.mConversation.getConversationType(), this.mConversation.getTargetId(), this.mLastRemoteMessageTime, 10, new RongIMClient.ResultCallback()
/*      */       {
/*      */         public void onSuccess(List<UIMessage> uiMessages) {
/*  555 */           MessageListFragment.this.mList.removeHeaderView(MessageListFragment.this.mHeaderView);
/*  556 */           if ((uiMessages == null) || (uiMessages.size() == 0)) {
/*  557 */             MessageListFragment.this.mHasMoreRemoteMessages = false;
/*      */           } else {
/*  559 */             RLog.d("MessageListFragment", "getRemoteHistoryMessages, onSuccess " + uiMessages.size());
/*  560 */             MessageListFragment.this.mHasMoreRemoteMessages = (uiMessages.size() >= 10);
/*  561 */             List filterMsg = new ArrayList();
/*  562 */             for (UIMessage m : uiMessages) {
/*  563 */               String uid = m.getUId();
/*  564 */               int count = MessageListFragment.this.mAdapter.getCount();
/*  565 */               boolean result = true;
/*  566 */               for (int i = 0; i < count; i++) {
/*  567 */                 UIMessage item = (UIMessage)MessageListFragment.this.mAdapter.getItem(i);
/*  568 */                 String targetUid = item.getUId();
/*  569 */                 if ((uid != null) && (targetUid != null) && (uid.equals(targetUid))) {
/*  570 */                   result = false;
/*  571 */                   break;
/*      */                 }
/*      */               }
/*  574 */               if (result) {
/*  575 */                 filterMsg.add(m);
/*      */               }
/*      */             }
/*  578 */             RLog.d("MessageListFragment", "getRemoteHistoryMessages, src: " + uiMessages.size() + " dest: " + filterMsg.size());
/*  579 */             MessageListFragment.this.getHandler().obtainMessage(6, filterMsg).sendToTarget();
/*      */           }
/*  581 */           MessageListFragment.this.isLoading = false;
/*      */         }
/*      */ 
/*      */         public void onError(RongIMClient.ErrorCode e)
/*      */         {
/*  586 */           MessageListFragment.this.mHasMoreRemoteMessages = false;
/*  587 */           MessageListFragment.this.mList.removeHeaderView(MessageListFragment.this.mHeaderView);
/*  588 */           MessageListFragment.this.isLoading = false;
/*  589 */           RLog.e("MessageListFragment", "getRemoteHistoryMessages, " + e.toString());
/*      */         }
/*      */       });
/*  592 */       break;
/*      */     case 12:
/*  596 */       message = (UIMessage)this.mAdapter.getItem(0);
/*      */ 
/*  598 */       EmojiMessageAdapter.getInstance().getHistoryMessages(this.mConversation.getConversationType(), this.mConversation.getTargetId(), message.getMessageId(), this.mUnreadCount - 29, new RongIMClient.ResultCallback()
/*      */       {
/*      */         public void onSuccess(List<UIMessage> messages) {
/*  601 */           RLog.d("MessageListFragment", "getHistoryMessages unread, onSuccess " + messages.size());
/*  602 */           MessageListFragment.this.mHasMoreLocalMessages = (messages.size() == MessageListFragment.this.mUnreadCount - 29);
/*  603 */           MessageListFragment.this.mList.removeHeaderView(MessageListFragment.this.mHeaderView);
/*      */ 
/*  605 */           for (UIMessage item : messages) {
/*  606 */             MessageListFragment.this.mAdapter.add(item, 0);
/*      */           }
/*  608 */           MessageListFragment.this.mAdapter.notifyDataSetChanged();
/*      */ 
/*  610 */           MessageListFragment.this.mList.setStackFromBottom(false);
/*      */ 
/*  613 */           MessageListFragment.this.mList.smoothScrollToPosition(0);
/*  614 */           MessageListFragment.this.isLoading = false;
/*      */         }
/*      */ 
/*      */         public void onError(RongIMClient.ErrorCode e)
/*      */         {
/*  619 */           RLog.e("MessageListFragment", "getHistoryMessages, " + e.toString());
/*  620 */           MessageListFragment.this.mHasMoreLocalMessages = false;
/*  621 */           MessageListFragment.this.mList.removeHeaderView(MessageListFragment.this.mHeaderView);
/*  622 */           MessageListFragment.this.isLoading = false;
/*      */         }
/*      */       });
/*  625 */       break;
/*      */     case 9:
/*  627 */       if (this.mAdapter == null) break;
/*  628 */       this.mAdapter.notifyDataSetChanged(); break;
/*      */     case 11:
/*  631 */       this.mAdapter.notifyDataSetChanged();
/*  632 */       getHandler().post(new Runnable()
/*      */       {
/*      */         public void run()
/*      */         {
/*  636 */           if (MessageListFragment.this.mList.getCount() > 0)
/*      */           {
/*  638 */             View firstView = MessageListFragment.this.mList.getChildAt(MessageListFragment.this.mList.getFirstVisiblePosition());
/*  639 */             View lastView = MessageListFragment.this.mList.getChildAt(MessageListFragment.this.mList.getLastVisiblePosition());
/*      */ 
/*  641 */             if ((firstView != null) && (lastView != null)) {
/*  642 */               int listViewPadding = MessageListFragment.this.mList.getListPaddingBottom() + MessageListFragment.this.mList.getListPaddingTop();
/*  643 */               int childViewsHeight = lastView.getBottom() - (firstView.getTop() == -1 ? 0 : firstView.getTop());
/*  644 */               int listViewHeight = MessageListFragment.this.mList.getBottom() - listViewPadding;
/*      */ 
/*  646 */               if (childViewsHeight < listViewHeight) {
/*  647 */                 MessageListFragment.this.mList.setTranscriptMode(2);
/*  648 */                 MessageListFragment.this.mList.setStackFromBottom(false);
/*      */               } else {
/*  650 */                 MessageListFragment.this.mList.setTranscriptMode(1);
/*      */               }
/*      */ 
/*  653 */               MessageListFragment.this.mAdapter.notifyDataSetChanged();
/*      */             }
/*      */           }
/*      */         } } );
/*      */     case 3:
/*      */     }
/*  660 */     return false;
/*      */   }
/*      */ 
/*      */   private void resetListViewStack()
/*      */   {
/*  665 */     int count = this.mList.getChildCount();
/*  666 */     View firstView = this.mList.getChildAt(0);
/*  667 */     View lastView = this.mList.getChildAt(count - 1);
/*      */ 
/*  669 */     if ((firstView != null) && (lastView != null)) {
/*  670 */       int listViewPadding = this.mList.getListPaddingBottom() + this.mList.getListPaddingTop();
/*  671 */       int childViewsHeight = lastView.getBottom() - (firstView.getTop() == -1 ? 0 : firstView.getTop());
/*  672 */       int listViewHeight = this.mList.getBottom() - listViewPadding;
/*      */ 
/*  674 */       if (childViewsHeight < listViewHeight) {
/*  675 */         this.mList.setTranscriptMode(2);
/*  676 */         this.mList.setStackFromBottom(false);
/*      */       } else {
/*  678 */         this.mList.setTranscriptMode(2);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.ReadReceiptEvent event) {
/*  684 */     if (!RongContext.getInstance().isReadReceiptConversationType(event.getMessage().getConversationType())) {
/*  685 */       return;
/*      */     }
/*  687 */     if ((this.mConversation != null) && (this.mConversation.getTargetId().equals(event.getMessage().getTargetId())) && (this.mConversation.getConversationType() == event.getMessage().getConversationType()))
/*      */     {
/*  691 */       if (event.getMessage().getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
/*  692 */         if (!this.mConversation.getConversationType().equals(Conversation.ConversationType.PRIVATE)) {
/*  693 */           return;
/*      */         }
/*  695 */         ReadReceiptMessage content = (ReadReceiptMessage)event.getMessage().getContent();
/*  696 */         long ntfTime = content.getLastMessageSendTime();
/*  697 */         for (int i = this.mAdapter.getCount() - 1; (i >= 0) && 
/*  698 */           (((UIMessage)this.mAdapter.getItem(i)).getSentStatus() != Message.SentStatus.READ); i--)
/*      */         {
/*  700 */           if ((((UIMessage)this.mAdapter.getItem(i)).getSentStatus() != Message.SentStatus.SENT) || 
/*  701 */             (!((UIMessage)this.mAdapter.getItem(i)).getMessageDirection().equals(Message.MessageDirection.SEND)) || (ntfTime < ((UIMessage)this.mAdapter.getItem(i)).getSentTime()))
/*      */             continue;
/*  703 */           ((UIMessage)this.mAdapter.getItem(i)).setSentStatus(Message.SentStatus.READ);
/*  704 */           getHandler().obtainMessage(7, Integer.valueOf(i)).sendToTarget();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void refreshListWhileReceiveMessage(UIMessage model)
/*      */   {
/*  713 */     model.setIsHistoryMessage(false);
/*  714 */     this.mAdapter.setEvaluateForRobot(this.needEvaluateForRobot);
/*  715 */     this.mAdapter.setRobotMode(this.robotMode);
/*  716 */     this.mAdapter.add(model);
/*      */ 
/*  719 */     if ((this.isShowNewMessageState) && (this.mList.getLastVisiblePosition() < this.mList.getCount() - 1) && (Message.MessageDirection.SEND != model.getMessageDirection()) && (SystemUtils.isAppRunningOnTop(RongContext.getInstance(), RongContext.getInstance().getPackageName())))
/*      */     {
/*  723 */       if ((model.getConversationType() != Conversation.ConversationType.CHATROOM) && (model.getConversationType() != Conversation.ConversationType.CUSTOMER_SERVICE) && (model.getConversationType() != Conversation.ConversationType.APP_PUBLIC_SERVICE) && (model.getConversationType() != Conversation.ConversationType.PUBLIC_SERVICE))
/*      */       {
/*  728 */         this.mNewMessageCount += 1;
/*  729 */         if (this.mNewMessageCount > 0) {
/*  730 */           this.mNewMessageBtn.setVisibility(0);
/*  731 */           this.mNewMessageTextView.setVisibility(0);
/*      */         }
/*  733 */         if (this.mNewMessageCount > 99)
/*  734 */           this.mNewMessageTextView.setText("99+");
/*      */         else {
/*  736 */           this.mNewMessageTextView.setText(this.mNewMessageCount + "");
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  741 */     int last = this.mList.getLastVisiblePosition();
/*  742 */     int count = this.mList.getCount();
/*  743 */     if (last == count - 1)
/*  744 */       this.mList.setTranscriptMode(2);
/*  745 */     else if (last < this.mList.getCount() - 1) {
/*  746 */       this.mList.setTranscriptMode(1);
/*      */     }
/*      */ 
/*  749 */     this.mAdapter.notifyDataSetChanged();
/*  750 */     if (last == count - 1) {
/*  751 */       this.mNewMessageBtn.setVisibility(8);
/*  752 */       this.mNewMessageTextView.setVisibility(8);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(io.rong.imlib.model.Message msg) {
/*  757 */     UIMessage message = UIMessage.obtain(msg);
/*  758 */     boolean readRec = false;
/*  759 */     boolean syncReadStatus = false;
/*      */     try {
/*  761 */       readRec = getResources().getBoolean(R.bool.rc_read_receipt);
/*  762 */       syncReadStatus = getResources().getBoolean(R.bool.rc_enable_sync_read_status);
/*      */     } catch (Resources.NotFoundException e) {
/*  764 */       RLog.e("MessageListFragment", "rc_read_receipt not configure in rc_config.xml");
/*  765 */       e.printStackTrace();
/*      */     }
/*  767 */     RLog.d("MessageListFragment", "onEventMainThread message : " + message.getMessageId() + " " + message.getSentStatus());
/*      */ 
/*  769 */     if ((this.mConversation != null) && (this.mConversation.getTargetId().equals(message.getTargetId())) && (this.mConversation.getConversationType() == message.getConversationType())) {
/*  770 */       int position = this.mAdapter.findPosition(message.getMessageId());
/*  771 */       if (message.getMessageId() > 0) {
/*  772 */         Message.ReceivedStatus status = message.getReceivedStatus();
/*  773 */         status.setRead();
/*  774 */         message.setReceivedStatus(status);
/*  775 */         RongIMClient.getInstance().setMessageReceivedStatus(msg.getMessageId(), status, null);
/*      */       }
/*  777 */       if (position == -1) {
/*  778 */         if ((this.mMessageleft <= 0) && 
/*  779 */           (message.getConversationType() == Conversation.ConversationType.PRIVATE) && (RongContext.getInstance().isReadReceiptConversationType(Conversation.ConversationType.PRIVATE)))
/*      */         {
/*  781 */           if (message.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
/*  782 */             if (readRec)
/*  783 */               RongIMClient.getInstance().sendReadReceiptMessage(message.getConversationType(), message.getTargetId(), message.getSentTime());
/*  784 */             else if (syncReadStatus) {
/*  785 */               RongIMClient.getInstance().syncConversationReadStatus(message.getConversationType(), message.getTargetId(), message.getSentTime(), null);
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  790 */         this.mHasReceivedMessage = true;
/*  791 */         this.mConversation.setSentTime(message.getSentTime());
/*  792 */         this.mConversation.setSenderUserId(message.getSenderUserId());
/*  793 */         refreshListWhileReceiveMessage(message);
/*      */       } else {
/*  795 */         ((UIMessage)this.mAdapter.getItem(position)).setSentStatus(message.getSentStatus());
/*  796 */         ((UIMessage)this.mAdapter.getItem(position)).setExtra(message.getExtra());
/*  797 */         ((UIMessage)this.mAdapter.getItem(position)).setSentTime(message.getSentTime());
/*  798 */         ((UIMessage)this.mAdapter.getItem(position)).setUId(message.getUId());
/*  799 */         ((UIMessage)this.mAdapter.getItem(position)).setContent(message.getContent());
/*  800 */         getHandler().obtainMessage(4, Integer.valueOf(position)).sendToTarget();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.GroupUserInfoEvent event) {
/*  806 */     GroupUserInfo userInfo = event.getUserInfo();
/*  807 */     if ((userInfo == null) || (userInfo.getNickname() == null)) {
/*  808 */       return;
/*      */     }
/*  810 */     if ((this.mList != null) && (isResumed())) {
/*  811 */       int first = this.mList.getFirstVisiblePosition() - this.mList.getHeaderViewsCount();
/*  812 */       int last = this.mList.getLastVisiblePosition() - this.mList.getHeaderViewsCount();
/*  813 */       int index = first - 1;
/*      */       while (true) {
/*  815 */         index++; if ((index > last) || (index < 0) || (index >= this.mAdapter.getCount())) break;
/*  816 */         io.rong.imlib.model.Message message = (io.rong.imlib.model.Message)this.mAdapter.getItem(index);
/*  817 */         if ((message != null) && ((TextUtils.isEmpty(message.getSenderUserId())) || (userInfo.getUserId().equals(message.getSenderUserId()))))
/*  818 */           this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition() + this.mList.getHeaderViewsCount()), this.mList);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.OnMessageSendErrorEvent event)
/*      */   {
/*  825 */     io.rong.imlib.model.Message msg = event.getMessage();
/*  826 */     onEventMainThread(msg);
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.OnReceiveMessageEvent event) {
/*  830 */     this.mMessageleft = event.getLeft();
/*  831 */     onEventMainThread(event.getMessage());
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(MessageContent messageContent)
/*      */   {
/*  836 */     if ((this.mList != null) && (isResumed())) {
/*  837 */       int first = this.mList.getFirstVisiblePosition() - this.mList.getHeaderViewsCount();
/*  838 */       int last = this.mList.getLastVisiblePosition() - this.mList.getHeaderViewsCount();
/*      */ 
/*  840 */       int index = first - 1;
/*      */       while (true) {
/*  842 */         index++; if ((index > last) || (index < 0) || (index >= this.mAdapter.getCount())) break;
/*  843 */         if (((UIMessage)this.mAdapter.getItem(index)).getContent().equals(messageContent))
/*  844 */           this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition() + this.mList.getHeaderViewsCount()), this.mList);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.PlayAudioEvent event)
/*      */   {
/*  852 */     if ((this.mList != null) && (isResumed())) {
/*  853 */       int first = this.mList.getFirstVisiblePosition() - this.mList.getHeaderViewsCount();
/*  854 */       int last = this.mList.getLastVisiblePosition() - this.mList.getHeaderViewsCount();
/*  855 */       int index = first;
/*      */ 
/*  857 */       while ((index <= last) && (index >= 0) && (index < this.mAdapter.getCount())) {
/*  858 */         UIMessage uiMessage = (UIMessage)this.mAdapter.getItem(index);
/*  859 */         if (uiMessage.getContent().equals(event.content)) {
/*  860 */           uiMessage.continuePlayAudio = false;
/*  861 */           break;
/*      */         }
/*  863 */         index++;
/*      */       }
/*  865 */       index++;
/*  866 */       if (event.continuously)
/*  867 */         while ((index <= last) && (index >= 0) && (index < this.mAdapter.getCount())) {
/*  868 */           UIMessage uiMessage = (UIMessage)this.mAdapter.getItem(index);
/*  869 */           if (((uiMessage.getContent() instanceof VoiceMessage)) && (uiMessage.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) && (!uiMessage.getReceivedStatus().isListened()))
/*      */           {
/*  871 */             uiMessage.continuePlayAudio = true;
/*  872 */             this.mAdapter.getView(index, this.mList.getChildAt(index - first), this.mList);
/*  873 */             break;
/*      */           }
/*  875 */           index++;
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.OnReceiveMessageProgressEvent event)
/*      */   {
/*  883 */     if ((this.mList != null) && (isResumed())) {
/*  884 */       int first = this.mList.getFirstVisiblePosition() - this.mList.getHeaderViewsCount();
/*  885 */       int last = this.mList.getLastVisiblePosition() - this.mList.getHeaderViewsCount();
/*      */ 
/*  887 */       int index = first - 1;
/*      */       while (true) {
/*  889 */         index++; if ((index > last) || (index < 0) || (index >= this.mAdapter.getCount())) break;
/*  890 */         UIMessage uiMessage = (UIMessage)this.mAdapter.getItem(index);
/*  891 */         if (uiMessage.getMessageId() == event.getMessage().getMessageId()) {
/*  892 */           uiMessage.setProgress(event.getProgress());
/*  893 */           this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition() + this.mList.getHeaderViewsCount()), this.mList);
/*      */ 
/*  898 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(InputView.Event event) {
/*  905 */     if (this.mAdapter == null) {
/*  906 */       return;
/*      */     }
/*  908 */     if (event == InputView.Event.ACTION)
/*  909 */       getHandler().sendEmptyMessage(10);
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(UserInfo userInfo)
/*      */   {
/*  915 */     if (this.mList != null) {
/*  916 */       int first = this.mList.getFirstVisiblePosition() - this.mList.getHeaderViewsCount();
/*  917 */       int last = this.mList.getLastVisiblePosition() - this.mList.getHeaderViewsCount();
/*      */ 
/*  919 */       int index = first - 1;
/*      */       while (true) {
/*  921 */         index++; if ((index > last) || (index < 0) || (index >= this.mAdapter.getCount()))
/*      */           break;
/*  923 */         UIMessage uiMessage = (UIMessage)this.mAdapter.getItem(index);
/*      */ 
/*  925 */         if ((uiMessage != null) && ((TextUtils.isEmpty(uiMessage.getSenderUserId())) || (userInfo.getUserId().equals(uiMessage.getSenderUserId())))) {
/*  926 */           uiMessage.setUserInfo(userInfo);
/*  927 */           this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition() + this.mList.getHeaderViewsCount()), this.mList);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(PublicServiceProfile publicServiceProfile)
/*      */   {
/*  936 */     if ((this.mList != null) && (isResumed()) && (this.mAdapter != null)) {
/*  937 */       int first = this.mList.getFirstVisiblePosition() - this.mList.getHeaderViewsCount();
/*  938 */       int last = this.mList.getLastVisiblePosition() - this.mList.getHeaderViewsCount();
/*      */ 
/*  940 */       int index = first - 1;
/*      */       while (true) {
/*  942 */         index++; if ((index > last) || (index < 0) || (index >= this.mAdapter.getCount()))
/*      */           break;
/*  944 */         io.rong.imlib.model.Message message = (io.rong.imlib.model.Message)this.mAdapter.getItem(index);
/*      */ 
/*  946 */         if ((message != null) && ((TextUtils.isEmpty(message.getTargetId())) || (publicServiceProfile.getTargetId().equals(message.getTargetId()))))
/*  947 */           this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition() + this.mList.getHeaderViewsCount()), this.mList);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void getConversation()
/*      */   {
/*  954 */     RongIM.getInstance().getConversation(this.mConversation.getConversationType(), this.mConversation.getTargetId(), new RongIMClient.ResultCallback()
/*      */     {
/*      */       public void onSuccess(Conversation conversation) {
/*  957 */         if (conversation != null) {
/*  958 */           if (!TextUtils.isEmpty(MessageListFragment.this.mConversation.getConversationTitle())) {
/*  959 */             conversation.setConversationTitle(MessageListFragment.this.mConversation.getConversationTitle());
/*      */           }
/*  961 */           MessageListFragment.this.mConversation = conversation;
/*      */ 
/*  964 */           if ((MessageListFragment.this.isShowUnreadMessageState) && (conversation.getConversationType() != Conversation.ConversationType.APP_PUBLIC_SERVICE) && (conversation.getConversationType() != Conversation.ConversationType.PUBLIC_SERVICE) && (conversation.getConversationType() != Conversation.ConversationType.CUSTOMER_SERVICE) && (conversation.getConversationType() != Conversation.ConversationType.CHATROOM))
/*      */           {
/*  969 */             MessageListFragment.this.mUnreadCount = MessageListFragment.this.mConversation.getUnreadMessageCount();
/*      */           }
/*  971 */           if (MessageListFragment.this.mUnreadCount > 150)
/*  972 */             MessageListFragment.this.mUnreadBtn.setText("150+" + MessageListFragment.this.getResources().getString(R.string.rc_new_messages));
/*      */           else {
/*  974 */             MessageListFragment.this.mUnreadBtn.setText(MessageListFragment.this.mUnreadCount + MessageListFragment.this.getResources().getString(R.string.rc_new_messages));
/*      */           }
/*      */ 
/*  977 */           MessageListFragment.this.sendReadReceiptAndSyncUnreadStatus(conversation.getConversationType(), conversation.getTargetId(), conversation.getSentTime());
/*      */ 
/*  979 */           MessageListFragment.access$202(MessageListFragment.this, false);
/*      */ 
/*  982 */           MessageListFragment.this.mUnreadBtn.setOnClickListener(new View.OnClickListener()
/*      */           {
/*      */             public void onClick(View v) {
/*  985 */               MessageListFragment.access$002(MessageListFragment.this, true);
/*  986 */               MessageListFragment.this.mUnreadBtn.setClickable(false);
/*  987 */               TranslateAnimation animation = new TranslateAnimation(0.0F, 500.0F, 0.0F, 0.0F);
/*  988 */               animation.setDuration(500L);
/*      */ 
/*  990 */               MessageListFragment.this.mUnreadBtn.startAnimation(animation);
/*  991 */               animation.setFillAfter(true);
/*  992 */               animation.setAnimationListener(new Animation.AnimationListener()
/*      */               {
/*      */                 public void onAnimationStart(Animation animation)
/*      */                 {
/*      */                 }
/*      */ 
/*      */                 public void onAnimationEnd(Animation animation)
/*      */                 {
/* 1000 */                   MessageListFragment.this.mUnreadBtn.setVisibility(8);
/* 1001 */                   if (MessageListFragment.this.mUnreadCount <= 30)
/*      */                   {
/* 1003 */                     if (MessageListFragment.this.mList.getCount() < 30)
/* 1004 */                       MessageListFragment.this.mList.smoothScrollToPosition(MessageListFragment.this.mList.getCount() - MessageListFragment.this.mUnreadCount);
/*      */                     else
/* 1006 */                       MessageListFragment.this.mList.smoothScrollToPosition(30 - MessageListFragment.this.mUnreadCount);
/*      */                   }
/* 1008 */                   else if (MessageListFragment.this.mUnreadCount >= 30)
/*      */                   {
/* 1010 */                     MessageListFragment.this.getHandler().sendEmptyMessage(12);
/*      */                   }
/*      */                 }
/*      */ 
/*      */                 public void onAnimationRepeat(Animation animation)
/*      */                 {
/*      */                 }
/*      */               });
/*      */             }
/*      */           });
/* 1022 */           if (MessageListFragment.this.mConversation.getMentionedCount() > 0)
/* 1023 */             RongIMClient.getInstance().getUnreadMentionedMessages(MessageListFragment.this.mConversation.getConversationType(), MessageListFragment.this.mConversation.getTargetId(), new RongIMClient.ResultCallback()
/*      */             {
/*      */               public void onSuccess(List<io.rong.imlib.model.Message> messages) {
/* 1026 */                 MessageListFragment.access$302(MessageListFragment.this, messages);
/*      */               }
/*      */ 
/*      */               public void onError(RongIMClient.ErrorCode e)
/*      */               {
/*      */               }
/*      */             });
/* 1036 */           if ((MessageListFragment.this.mConversation != null) && (MessageListFragment.this.mConversation.getConversationType() != Conversation.ConversationType.CHATROOM)) {
/* 1037 */             RongIM.getInstance().clearMessagesUnreadStatus(MessageListFragment.this.mConversation.getConversationType(), MessageListFragment.this.mConversation.getTargetId(), null);
/*      */           }
/*      */         }
/* 1040 */         MessageListFragment.this.getHandler().sendEmptyMessage(1);
/*      */       }
/*      */ 
/*      */       public void onError(RongIMClient.ErrorCode e)
/*      */       {
/* 1045 */         RLog.e("MessageListFragment", "fail, " + e.toString());
/*      */       } } );
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.ConnectEvent event) {
/* 1051 */     RLog.d("MessageListFragment", "onEventMainThread Event.ConnectEvent: isListRetrieved = " + this.isShowWithoutConnected);
/* 1052 */     if (this.isShowWithoutConnected) {
/* 1053 */       getConversation();
/* 1054 */       if (this.mConversation.getConversationType() != Conversation.ConversationType.CHATROOM)
/* 1055 */         RongIM.getInstance().clearMessagesUnreadStatus(this.mConversation.getConversationType(), this.mConversation.getTargetId(), null);
/*      */     }
/*      */     else {
/* 1058 */       return;
/*      */     }
/* 1060 */     this.isShowWithoutConnected = false;
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.ReadReceiptRequestEvent event) {
/* 1064 */     if ((!this.mConversation.getConversationType().equals(Conversation.ConversationType.GROUP)) && (!this.mConversation.getConversationType().equals(Conversation.ConversationType.DISCUSSION))) {
/* 1065 */       return;
/*      */     }
/* 1067 */     if (!RongContext.getInstance().isReadReceiptConversationType(event.getConversationType())) {
/* 1068 */       return;
/*      */     }
/* 1070 */     if ((event.getConversationType().equals(this.mConversation.getConversationType())) && (event.getTargetId().equals(this.mConversation.getTargetId())))
/* 1071 */       for (int i = 0; i < this.mAdapter.getCount(); i++)
/* 1072 */         if (((UIMessage)this.mAdapter.getItem(i)).getUId().equals(event.getMessageUId())) {
/* 1073 */           UIMessage uiMessage = (UIMessage)this.mAdapter.getItem(i);
/* 1074 */           ReadReceiptInfo readReceiptInfo = uiMessage.getReadReceiptInfo();
/* 1075 */           if (readReceiptInfo == null) {
/* 1076 */             readReceiptInfo = new ReadReceiptInfo();
/* 1077 */             uiMessage.setReadReceiptInfo(readReceiptInfo);
/*      */           }
/* 1079 */           if ((readReceiptInfo.isReadReceiptMessage()) && (readReceiptInfo.hasRespond())) {
/* 1080 */             return;
/*      */           }
/* 1082 */           readReceiptInfo.setIsReadReceiptMessage(true);
/* 1083 */           readReceiptInfo.setHasRespond(false);
/* 1084 */           List messageList = new ArrayList();
/* 1085 */           messageList.add(((UIMessage)this.mAdapter.getItem(i)).getMessage());
/* 1086 */           RongIMClient.getInstance().sendReadReceiptResponse(event.getConversationType(), event.getTargetId(), messageList, new RongIMClient.OperationCallback(uiMessage)
/*      */           {
/*      */             public void onSuccess() {
/* 1089 */               this.val$uiMessage.getReadReceiptInfo().setHasRespond(true);
/*      */             }
/*      */ 
/*      */             public void onError(RongIMClient.ErrorCode errorCode)
/*      */             {
/* 1094 */               RLog.e("MessageListFragment", "sendReadReceiptResponse failed, errorCode = " + errorCode);
/*      */             }
/*      */           });
/* 1097 */           return;
/*      */         }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.ReadReceiptResponseEvent event)
/*      */   {
/* 1104 */     if ((!this.mConversation.getConversationType().equals(Conversation.ConversationType.GROUP)) && (!this.mConversation.getConversationType().equals(Conversation.ConversationType.DISCUSSION))) {
/* 1105 */       return;
/*      */     }
/* 1107 */     if (!RongContext.getInstance().isReadReceiptConversationType(event.getConversationType())) {
/* 1108 */       return;
/*      */     }
/* 1110 */     if ((event.getConversationType().equals(this.mConversation.getConversationType())) && (event.getTargetId().equals(this.mConversation.getTargetId())))
/* 1111 */       for (int i = 0; i < this.mAdapter.getCount(); i++)
/* 1112 */         if (((UIMessage)this.mAdapter.getItem(i)).getUId().equals(event.getMessageUId())) {
/* 1113 */           UIMessage uiMessage = (UIMessage)this.mAdapter.getItem(i);
/* 1114 */           ReadReceiptInfo readReceiptInfo = uiMessage.getReadReceiptInfo();
/* 1115 */           if (readReceiptInfo == null) {
/* 1116 */             readReceiptInfo = new ReadReceiptInfo();
/* 1117 */             readReceiptInfo.setIsReadReceiptMessage(true);
/* 1118 */             uiMessage.setReadReceiptInfo(readReceiptInfo);
/*      */           }
/* 1120 */           readReceiptInfo.setRespondUserIdList(event.getResponseUserIdList());
/* 1121 */           getHandler().obtainMessage(4, Integer.valueOf(i)).sendToTarget();
/* 1122 */           return;
/*      */         }
/*      */   }
/*      */ 
/*      */   public void onPause()
/*      */   {
/* 1130 */     super.onPause();
/* 1131 */     RongContext.getInstance().getEventBus().post(InputView.Event.DESTROY);
/*      */   }
/*      */ 
/*      */   public void onResume()
/*      */   {
/* 1136 */     super.onResume();
/* 1137 */     if (RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
/* 1138 */       this.isShowWithoutConnected = true;
/* 1139 */       RLog.e("MessageListFragment", "onResume Not connected yet.");
/*      */     }
/*      */ 
/* 1142 */     if (this.mList.getLastVisiblePosition() == this.mList.getCount() - 1) {
/* 1143 */       this.mNewMessageCount = 0;
/* 1144 */       this.mNewMessageTextView.setVisibility(8);
/* 1145 */       this.mNewMessageBtn.setVisibility(8);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.MessageDeleteEvent deleteEvent) {
/* 1150 */     if (deleteEvent.getMessageIds() != null) {
/* 1151 */       boolean hasChanged = false;
/* 1152 */       int position = 0;
/*      */ 
/* 1154 */       for (Iterator i$ = deleteEvent.getMessageIds().iterator(); i$.hasNext(); ) { long item = ((Integer)i$.next()).intValue();
/* 1155 */         position = this.mAdapter.findPosition(item);
/* 1156 */         if (position >= 0) {
/* 1157 */           this.mAdapter.remove(position);
/* 1158 */           hasChanged = true;
/*      */         }
/*      */       }
/*      */ 
/* 1162 */       if (hasChanged)
/*      */       {
/* 1166 */         this.mAdapter.notifyDataSetChanged();
/* 1167 */         getHandler().obtainMessage(11).sendToTarget();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.PublicServiceFollowableEvent event)
/*      */   {
/* 1176 */     if ((event != null) && (!event.isFollow()))
/* 1177 */       getActivity().finish();
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.MessagesClearEvent clearEvent)
/*      */   {
/* 1182 */     if ((clearEvent.getTargetId().equals(this.mConversation.getTargetId())) && (clearEvent.getType().equals(this.mConversation.getConversationType()))) {
/* 1183 */       this.mAdapter.removeAll();
/* 1184 */       getHandler().post(new Runnable()
/*      */       {
/*      */         public void run() {
/* 1187 */           MessageListFragment.this.mList.setTranscriptMode(1);
/* 1188 */           MessageListFragment.this.mList.setStackFromBottom(false);
/* 1189 */           MessageListFragment.this.mAdapter.notifyDataSetChanged();
/*      */         }
/*      */       });
/* 1192 */       this.mAdapter.notifyDataSetChanged();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.MessageRecallEvent event) {
/* 1197 */     if (event.isRecallSuccess()) {
/* 1198 */       RecallNotificationMessage recallNotificationMessage = event.getRecallNotificationMessage();
/* 1199 */       int position = this.mAdapter.findPosition(event.getMessageId());
/* 1200 */       if (position != -1) {
/* 1201 */         ((UIMessage)this.mAdapter.getItem(position)).setContent(recallNotificationMessage);
/* 1202 */         getHandler().obtainMessage(4, Integer.valueOf(position)).sendToTarget();
/*      */       }
/*      */     } else {
/* 1205 */       Toast.makeText(getActivity(), R.string.rc_recall_failed, 0).show();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onEventMainThread(Event.RemoteMessageRecallEvent event) {
/* 1210 */     if (event.isRecallSuccess()) {
/* 1211 */       RecallNotificationMessage recallNotificationMessage = event.getRecallNotificationMessage();
/*      */ 
/* 1213 */       if (AudioPlayManager.getInstance().getPlayingUri() != null) {
/* 1214 */         String uri = AudioPlayManager.getInstance().getPlayingUri().toString();
/* 1215 */         int i = uri.lastIndexOf('/');
/* 1216 */         int j = uri.lastIndexOf('.');
/* 1217 */         String sub = null;
/* 1218 */         int matchId = 0;
/*      */         try {
/* 1220 */           sub = uri.substring(i + 1, j);
/* 1221 */           matchId = Integer.parseInt(sub);
/* 1222 */           if (matchId == event.getMessageId())
/* 1223 */             AudioPlayManager.getInstance().stopPlay();
/*      */         }
/*      */         catch (Exception e) {
/* 1226 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */ 
/* 1230 */       int position = this.mAdapter.findPosition(event.getMessageId());
/* 1231 */       if (position != -1) {
/* 1232 */         ((UIMessage)this.mAdapter.getItem(position)).setContent(recallNotificationMessage);
/* 1233 */         getHandler().obtainMessage(4, Integer.valueOf(position)).sendToTarget();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void onDestroy()
/*      */   {
/* 1277 */     RongContext.getInstance().getEventBus().unregister(this);
/* 1278 */     if (this.mConversation.getConversationType() != Conversation.ConversationType.CHATROOM) {
/* 1279 */       RongIM.getInstance().clearMessagesUnreadStatus(this.mConversation.getConversationType(), this.mConversation.getTargetId(), null);
/*      */     }
/*      */ 
/* 1283 */     if (this.mConversation != null) {
/* 1284 */       boolean syncReadStatus = false;
/*      */       try {
/* 1286 */         syncReadStatus = getResources().getBoolean(R.bool.rc_enable_sync_read_status);
/*      */       } catch (Resources.NotFoundException e) {
/* 1288 */         RLog.e("MessageListFragment", "rc_enable_sync_unread_status not configure in rc_config.xml");
/* 1289 */         e.printStackTrace();
/*      */       }
/* 1291 */       if ((syncReadStatus) && 
/* 1292 */         ((this.mConversation.getConversationType() == Conversation.ConversationType.GROUP) || (this.mConversation.getConversationType() == Conversation.ConversationType.DISCUSSION)) && 
/* 1293 */         (this.mHasReceivedMessage)) {
/* 1294 */         RongIMClient.getInstance().syncConversationReadStatus(this.mConversation.getConversationType(), this.mConversation.getTargetId(), this.mConversation.getSentTime(), null);
/*      */       }
/*      */ 
/* 1298 */       this.mHasReceivedMessage = false;
/*      */     }
/* 1300 */     super.onDestroy();
/*      */   }
/*      */ 
/*      */   public void setAdapter(MessageListAdapter adapter) {
/* 1304 */     if (this.mAdapter != null)
/* 1305 */       this.mAdapter.clear();
/* 1306 */     this.mAdapter = adapter;
/* 1307 */     if ((this.mList != null) && (getUri() != null)) {
/* 1308 */       this.mList.setAdapter(adapter);
/* 1309 */       initFragment(getUri());
/*      */     }
/*      */   }
/*      */ 
/*      */   public MessageListAdapter getAdapter() {
/* 1314 */     return this.mAdapter;
/*      */   }
/*      */ 
/*      */   public void setNeedEvaluateForRobot(boolean needEvaluate) {
/* 1318 */     this.needEvaluateForRobot = needEvaluate;
/*      */   }
/*      */   public void setRobotMode(boolean robotMode) {
/* 1321 */     this.robotMode = robotMode;
/*      */   }
/*      */ 
/*      */   private void sendReadReceiptAndSyncUnreadStatus(Conversation.ConversationType type, String targetId, long timeStamp) {
/* 1325 */     boolean readRec = false;
/* 1326 */     boolean syncReadStatus = false;
/*      */     try {
/* 1328 */       readRec = getResources().getBoolean(R.bool.rc_read_receipt);
/* 1329 */       syncReadStatus = getResources().getBoolean(R.bool.rc_enable_sync_read_status);
/*      */     } catch (Resources.NotFoundException e) {
/* 1331 */       RLog.e("MessageListFragment", "rc_read_receipt not configure in rc_config.xml");
/* 1332 */       e.printStackTrace();
/*      */     }
/* 1334 */     if (type == Conversation.ConversationType.PRIVATE) {
/* 1335 */       if ((readRec) && (RongContext.getInstance().isReadReceiptConversationType(Conversation.ConversationType.PRIVATE))) {
/* 1336 */         if (this.mConversation.getUnreadMessageCount() > 0)
/* 1337 */           RongIMClient.getInstance().sendReadReceiptMessage(type, targetId, timeStamp);
/*      */       }
/* 1339 */       else if ((syncReadStatus) && 
/* 1340 */         (this.mConversation.getUnreadMessageCount() > 0)) {
/* 1341 */         RongIMClient.getInstance().syncConversationReadStatus(type, targetId, timeStamp, null);
/*      */       }
/*      */     }
/* 1344 */     else if (((type == Conversation.ConversationType.GROUP) || (type == Conversation.ConversationType.DISCUSSION)) && 
/* 1345 */       (syncReadStatus) && (this.mConversation.getUnreadMessageCount() > 0))
/* 1346 */       RongIMClient.getInstance().syncConversationReadStatus(type, targetId, timeStamp, null);
/*      */   }
/*      */ 
/*      */   public static class Builder
/*      */   {
/*      */     private Conversation.ConversationType conversationType;
/*      */     private String targetId;
/*      */     private Uri uri;
/*      */ 
/*      */     public Conversation.ConversationType getConversationType()
/*      */     {
/* 1258 */       return this.conversationType;
/*      */     }
/*      */ 
/*      */     public void setConversationType(Conversation.ConversationType conversationType) {
/* 1262 */       this.conversationType = conversationType;
/*      */     }
/*      */ 
/*      */     public String getTargetId() {
/* 1266 */       return this.targetId;
/*      */     }
/*      */ 
/*      */     public void setTargetId(String targetId) {
/* 1270 */       this.targetId = targetId;
/*      */     }
/*      */   }
/*      */ 
/*      */   public class ScrollRunnable
/*      */     implements Runnable
/*      */   {
/*      */     public ScrollRunnable()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/* 1242 */       if (MessageListFragment.this.mList.getLastVisiblePosition() < MessageListFragment.this.mList.getCount() - 1)
/*      */       {
/* 1244 */         MessageListFragment.this.mList.setSelection(MessageListFragment.this.mList.getLastVisiblePosition() + 10);
/* 1245 */         MessageListFragment.this.getHandler().postDelayed(new ScrollRunnable(MessageListFragment.this), 100L);
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.fragment.MessageListFragment
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.imkit.fragment;
/*     */ 
/*     */ import android.app.AlertDialog;
/*     */ import android.app.AlertDialog.Builder;
/*     */ import android.content.DialogInterface;
/*     */ import android.content.DialogInterface.OnClickListener;
/*     */ import android.content.Intent;
/*     */ import android.content.res.Resources;
/*     */ import android.content.res.Resources.NotFoundException;
/*     */ import android.net.Uri;
/*     */ import android.os.Bundle;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.support.v4.app.FragmentManager;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.ViewGroup;
/*     */ import android.view.Window;
/*     */ import android.view.inputmethod.InputMethodManager;
/*     */ import android.widget.AbsListView;
/*     */ import android.widget.AbsListView.OnScrollListener;
/*     */ import android.widget.LinearLayout;
/*     */ import android.widget.TextView;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.R.bool;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.integer;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.manager.AudioPlayManager;
/*     */ import io.rong.imkit.manager.AudioRecordManager;
/*     */ import io.rong.imkit.manager.SendImageManager;
/*     */ import io.rong.imkit.mention.RongMentionManager;
/*     */ import io.rong.imkit.model.ConversationInfo;
/*     */ import io.rong.imkit.widget.InputView.IInputBoardListener;
/*     */ import io.rong.imkit.widget.InputView.OnInfoButtonClick;
/*     */ import io.rong.imkit.widget.SingleChoiceDialog;
/*     */ import io.rong.imlib.CustomServiceConfig;
/*     */ import io.rong.imlib.ICustomServiceListener;
/*     */ import io.rong.imlib.IRongCallback.ISendMessageCallback;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.OperationCallback;
/*     */ import io.rong.imlib.model.CSCustomServiceInfo;
/*     */ import io.rong.imlib.model.CSGroupItem;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.CustomServiceMode;
/*     */ import io.rong.imlib.model.PublicServiceMenu.PublicServiceMenuItemType;
/*     */ import io.rong.message.PublicServiceCommandMessage;
/*     */ import io.rong.push.RongPushClient;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ConversationFragment extends DispatchResultFragment
/*     */   implements AbsListView.OnScrollListener
/*     */ {
/*     */   private static final String TAG = "ConversationFragment";
/*     */   MessageListFragment mListFragment;
/*     */   MessageInputFragment mInputFragment;
/*     */   Conversation.ConversationType mConversationType;
/*     */   String mTargetId;
/*     */   private CSCustomServiceInfo mCustomUserInfo;
/*     */   ConversationInfo mCurrentConversationInfo;
/*     */   private InputView.OnInfoButtonClick onInfoButtonClick;
/*     */   private InputView.IInputBoardListener inputBoardListener;
/* 253 */   private boolean robotType = true;
/* 254 */   private int source = 0;
/* 255 */   private boolean resolved = true;
/* 256 */   private boolean committing = false;
/*     */   private long enterTime;
/* 258 */   private boolean evaluate = true;
/*     */ 
/* 260 */   ICustomServiceListener customServiceListener = new ICustomServiceListener()
/*     */   {
/*     */     public void onSuccess(CustomServiceConfig config) {
/* 263 */       if (config.isBlack) {
/* 264 */         ConversationFragment.this.onCustomServiceWarning(ConversationFragment.this.getString(R.string.rc_blacklist_prompt), false);
/*     */       }
/* 266 */       if (config.robotSessionNoEva) {
/* 267 */         ConversationFragment.access$102(ConversationFragment.this, false);
/* 268 */         if (ConversationFragment.this.mListFragment != null)
/* 269 */           ConversationFragment.this.mListFragment.setNeedEvaluateForRobot(true);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void onError(int code, String msg)
/*     */     {
/* 276 */       ConversationFragment.this.onCustomServiceWarning(msg, false);
/*     */     }
/*     */ 
/*     */     public void onModeChanged(CustomServiceMode mode)
/*     */     {
/* 281 */       ConversationFragment.this.mInputFragment.setInputProviderType(mode);
/* 282 */       if ((mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN)) || (mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN_FIRST)))
/*     */       {
/* 284 */         ConversationFragment.access$202(ConversationFragment.this, false);
/* 285 */         ConversationFragment.access$102(ConversationFragment.this, true);
/* 286 */       } else if (mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE)) {
/* 287 */         ConversationFragment.access$102(ConversationFragment.this, false);
/*     */       }
/* 289 */       if (ConversationFragment.this.mListFragment != null)
/* 290 */         ConversationFragment.this.mListFragment.setRobotMode(ConversationFragment.this.robotType);
/*     */     }
/*     */ 
/*     */     public void onQuit(String msg)
/*     */     {
/* 296 */       if (!ConversationFragment.this.committing)
/* 297 */         ConversationFragment.this.onCustomServiceWarning(msg, true);
/*     */     }
/*     */ 
/*     */     public void onPullEvaluation(String dialogId)
/*     */     {
/* 303 */       if (!ConversationFragment.this.committing)
/* 304 */         ConversationFragment.this.onCustomServiceEvaluation(true, dialogId, ConversationFragment.this.robotType, ConversationFragment.this.evaluate);
/*     */     }
/*     */ 
/*     */     public void onSelectGroup(List<CSGroupItem> groups)
/*     */     {
/* 310 */       ConversationFragment.this.showSingleSelectDialog(groups);
/*     */     }
/* 260 */   };
/*     */ 
/*     */   public void onCreate(Bundle savedInstanceState)
/*     */   {
/*  64 */     super.onCreate(savedInstanceState);
/*  65 */     RongPushClient.clearAllPushNotifications(getActivity());
/*     */   }
/*     */ 
/*     */   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
/*     */   {
/*  70 */     View view = inflater.inflate(R.layout.rc_fr_conversation, container, false);
/*  71 */     return view;
/*     */   }
/*     */ 
/*     */   public void onScrollStateChanged(AbsListView view, int scrollState)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onResume()
/*     */   {
/*  86 */     RongPushClient.clearAllPushNotifications(getActivity());
/*  87 */     super.onResume();
/*     */   }
/*     */ 
/*     */   public void setOnInfoButtonClick(InputView.OnInfoButtonClick onInfoButtonClick)
/*     */   {
/*  94 */     this.onInfoButtonClick = onInfoButtonClick;
/*     */ 
/*  96 */     if (this.mInputFragment != null)
/*  97 */       this.mInputFragment.setOnInfoButtonClick(onInfoButtonClick);
/*     */   }
/*     */ 
/*     */   public void setInputBoardListener(InputView.IInputBoardListener inputBoardListener)
/*     */   {
/* 102 */     this.inputBoardListener = inputBoardListener;
/* 103 */     if (this.mInputFragment != null)
/* 104 */       this.mInputFragment.setInputBoardListener(inputBoardListener);
/*     */   }
/*     */ 
/*     */   protected void initFragment(Uri uri)
/*     */   {
/* 110 */     RLog.d("ConversationFragment", "initFragment : " + uri + ",this=" + this);
/* 111 */     if (uri != null) {
/* 112 */       String typeStr = uri.getLastPathSegment().toUpperCase();
/* 113 */       this.mConversationType = Conversation.ConversationType.valueOf(typeStr);
/* 114 */       this.mTargetId = uri.getQueryParameter("targetId");
/* 115 */       if ((this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) && (getActivity() != null) && (getActivity().getIntent() != null) && (getActivity().getIntent().getData() != null))
/*     */       {
/* 119 */         this.mCustomUserInfo = ((CSCustomServiceInfo)getActivity().getIntent().getParcelableExtra("customServiceInfo"));
/*     */       }
/*     */ 
/* 122 */       this.mCurrentConversationInfo = ConversationInfo.obtain(this.mConversationType, this.mTargetId);
/* 123 */       RongContext.getInstance().registerConversationInfo(this.mCurrentConversationInfo);
/*     */ 
/* 125 */       this.mListFragment = ((MessageListFragment)getChildFragmentManager().findFragmentById(16908298));
/* 126 */       this.mInputFragment = ((MessageInputFragment)getChildFragmentManager().findFragmentById(16908311));
/*     */ 
/* 128 */       if (this.mListFragment == null) {
/* 129 */         this.mListFragment = new MessageListFragment();
/*     */       }
/*     */ 
/* 132 */       if (this.mInputFragment == null) {
/* 133 */         this.mInputFragment = new MessageInputFragment();
/*     */       }
/*     */ 
/* 136 */       this.mListFragment.setUri(uri);
/* 137 */       this.mInputFragment.setUri(uri);
/*     */ 
/* 139 */       this.mListFragment.setOnScrollListener(this);
/*     */ 
/* 141 */       if (this.mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
/* 142 */         boolean createIfNotExist = (getActivity() != null) && (getActivity().getIntent().getBooleanExtra("createIfNotExist", true));
/* 143 */         int pullCount = getResources().getInteger(R.integer.rc_chatroom_first_pull_message_count);
/* 144 */         if (createIfNotExist)
/* 145 */           RongIMClient.getInstance().joinChatRoom(this.mTargetId, pullCount, new RongIMClient.OperationCallback()
/*     */           {
/*     */             public void onSuccess() {
/* 148 */               RLog.i("ConversationFragment", "joinChatRoom onSuccess : " + ConversationFragment.this.mTargetId);
/*     */             }
/*     */ 
/*     */             public void onError(RongIMClient.ErrorCode errorCode)
/*     */             {
/* 153 */               RLog.e("ConversationFragment", "joinChatRoom onError : " + errorCode);
/* 154 */               if (ConversationFragment.this.getActivity() != null)
/* 155 */                 ConversationFragment.this.csWarning(ConversationFragment.this.getString(R.string.rc_join_chatroom_failure));
/*     */             }
/*     */           });
/*     */         else
/* 160 */           RongIMClient.getInstance().joinExistChatRoom(this.mTargetId, pullCount, new RongIMClient.OperationCallback()
/*     */           {
/*     */             public void onSuccess() {
/* 163 */               RLog.i("ConversationFragment", "joinExistChatRoom onSuccess : " + ConversationFragment.this.mTargetId);
/*     */             }
/*     */ 
/*     */             public void onError(RongIMClient.ErrorCode errorCode)
/*     */             {
/* 168 */               RLog.e("ConversationFragment", "joinExistChatRoom onError : " + errorCode);
/* 169 */               if (ConversationFragment.this.getActivity() != null)
/* 170 */                 ConversationFragment.this.csWarning(ConversationFragment.this.getString(R.string.rc_join_chatroom_failure));
/*     */             } } );
/*     */       }
/* 174 */       else if ((this.mConversationType == Conversation.ConversationType.APP_PUBLIC_SERVICE) || (this.mConversationType == Conversation.ConversationType.PUBLIC_SERVICE))
/*     */       {
/* 176 */         PublicServiceCommandMessage msg = new PublicServiceCommandMessage();
/* 177 */         msg.setCommand(PublicServiceMenu.PublicServiceMenuItemType.Entry.getMessage());
/* 178 */         io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(this.mTargetId, this.mConversationType, msg);
/* 179 */         RongIMClient.getInstance().sendMessage(message, null, null, new IRongCallback.ISendMessageCallback()
/*     */         {
/*     */           public void onAttached(io.rong.imlib.model.Message message)
/*     */           {
/*     */           }
/*     */ 
/*     */           public void onSuccess(io.rong.imlib.model.Message message)
/*     */           {
/*     */           }
/*     */ 
/*     */           public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode)
/*     */           {
/*     */           }
/*     */         });
/*     */       }
/* 195 */       else if (this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
/* 196 */         this.enterTime = System.currentTimeMillis();
/* 197 */         this.mInputFragment.setOnRobotSwitcherListener(new View.OnClickListener()
/*     */         {
/*     */           public void onClick(View v) {
/* 200 */             RongIMClient.getInstance().switchToHumanMode(ConversationFragment.this.mTargetId);
/*     */           }
/*     */         });
/* 203 */         RongIMClient.getInstance().startCustomService(this.mTargetId, this.customServiceListener, this.mCustomUserInfo);
/*     */       }
/*     */       try
/*     */       {
/* 207 */         if ((RongContext.getInstance().getResources().getBoolean(R.bool.rc_enable_mentioned_message)) && ((this.mConversationType.equals(Conversation.ConversationType.DISCUSSION)) || (this.mConversationType.equals(Conversation.ConversationType.GROUP))))
/*     */         {
/* 210 */           RongMentionManager.getInstance().createInstance(this.mConversationType, this.mTargetId, this.mListFragment.getAdapter(), this.mInputFragment.getMentionInputProvider());
/*     */         }
/*     */       } catch (Resources.NotFoundException e) {
/* 213 */         RLog.e("ConversationFragment", "Resource not found!");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void showSingleSelectDialog(List<CSGroupItem> groupList) {
/* 219 */     if (getActivity() == null) {
/* 220 */       return;
/*     */     }
/*     */ 
/* 223 */     List singleDataList = new ArrayList();
/* 224 */     singleDataList.clear();
/* 225 */     for (int i = 0; i < groupList.size(); i++) {
/* 226 */       if (((CSGroupItem)groupList.get(i)).getOnline()) {
/* 227 */         singleDataList.add(((CSGroupItem)groupList.get(i)).getName());
/*     */       }
/*     */     }
/* 230 */     if (singleDataList.size() == 0) {
/* 231 */       RongIMClient.getInstance().selectCustomServiceGroup(this.mTargetId, null);
/* 232 */       return;
/*     */     }
/* 234 */     SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(getActivity(), singleDataList);
/* 235 */     singleChoiceDialog.setTitle(getActivity().getResources().getString(R.string.rc_cs_select_group));
/* 236 */     singleChoiceDialog.setOnOKButtonListener(new DialogInterface.OnClickListener(singleChoiceDialog, groupList)
/*     */     {
/*     */       public void onClick(DialogInterface dialog, int which) {
/* 239 */         int selItem = this.val$singleChoiceDialog.getSelectItem();
/* 240 */         RongIMClient.getInstance().selectCustomServiceGroup(ConversationFragment.this.mTargetId, ((CSGroupItem)this.val$groupList.get(selItem)).getId());
/*     */       }
/*     */     });
/* 244 */     singleChoiceDialog.setOnCancelButtonListener(new DialogInterface.OnClickListener()
/*     */     {
/*     */       public void onClick(DialogInterface dialog, int which) {
/* 247 */         RongIMClient.getInstance().selectCustomServiceGroup(ConversationFragment.this.mTargetId, null);
/*     */       }
/*     */     });
/* 250 */     singleChoiceDialog.show();
/*     */   }
/*     */ 
/*     */   public void onViewCreated(View view, Bundle savedInstanceState)
/*     */   {
/* 316 */     super.onViewCreated(view, savedInstanceState);
/* 317 */     this.mInputFragment = ((MessageInputFragment)getChildFragmentManager().findFragmentById(16908311));
/* 318 */     if (this.mInputFragment != null) {
/* 319 */       this.mInputFragment.setOnInfoButtonClick(this.onInfoButtonClick);
/* 320 */       this.mInputFragment.setInputBoardListener(this.inputBoardListener);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onDestroyView()
/*     */   {
/* 326 */     RongContext.getInstance().unregisterConversationInfo(this.mCurrentConversationInfo);
/* 327 */     super.onDestroyView();
/*     */   }
/*     */ 
/*     */   public void onDestroy()
/*     */   {
/* 332 */     RongContext.getInstance().getEventBus().unregister(this);
/*     */ 
/* 334 */     if (this.mConversationType != null) {
/* 335 */       if (this.mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
/* 336 */         SendImageManager.getInstance().cancelSendingImages(this.mConversationType, this.mTargetId);
/* 337 */         RongIM.getInstance().quitChatRoom(this.mTargetId, new RongIMClient.OperationCallback()
/*     */         {
/*     */           public void onSuccess() {
/*     */           }
/*     */ 
/*     */           public void onError(RongIMClient.ErrorCode errorCode) {
/*     */           }
/*     */         });
/*     */       }
/* 347 */       if (this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
/* 348 */         boolean needToQuit = true;
/*     */         try {
/* 350 */           needToQuit = RongContext.getInstance().getResources().getBoolean(R.bool.rc_stop_custom_service_when_quit);
/*     */         } catch (Resources.NotFoundException e) {
/* 352 */           e.printStackTrace();
/*     */         }
/* 354 */         if (needToQuit)
/* 355 */           RongIMClient.getInstance().stopCustomService(this.mTargetId);
/*     */       }
/*     */     }
/* 358 */     AudioPlayManager.getInstance().stopPlay();
/* 359 */     AudioRecordManager.getInstance().stopRecord();
/*     */     try {
/* 361 */       if ((RongContext.getInstance().getResources().getBoolean(R.bool.rc_enable_mentioned_message)) && ((this.mConversationType.equals(Conversation.ConversationType.DISCUSSION)) || (this.mConversationType.equals(Conversation.ConversationType.GROUP))))
/*     */       {
/* 364 */         RongMentionManager.getInstance().destroyInstance();
/*     */       }
/*     */     }
/*     */     catch (Resources.NotFoundException e) {
/*     */     }
/* 369 */     super.onDestroy();
/*     */   }
/*     */ 
/*     */   public boolean onBackPressed()
/*     */   {
/* 374 */     if ((this.mConversationType != null) && (this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE))) {
/* 375 */       return onCustomServiceEvaluation(false, "", this.robotType, this.evaluate);
/*     */     }
/* 377 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean handleMessage(android.os.Message msg)
/*     */   {
/* 382 */     return false;
/*     */   }
/*     */ 
/*     */   private void csWarning(String msg) {
/* 386 */     if (getActivity() == null) {
/* 387 */       return;
/*     */     }
/* 389 */     AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
/* 390 */     builder.setCancelable(false);
/* 391 */     AlertDialog alertDialog = builder.create();
/* 392 */     alertDialog.show();
/* 393 */     Window window = alertDialog.getWindow();
/* 394 */     window.setContentView(R.layout.rc_cs_alert_warning);
/* 395 */     TextView tv = (TextView)window.findViewById(R.id.rc_cs_msg);
/* 396 */     tv.setText(msg);
/*     */ 
/* 398 */     window.findViewById(R.id.rc_btn_ok).setOnClickListener(new View.OnClickListener(alertDialog)
/*     */     {
/*     */       public void onClick(View v) {
/* 401 */         this.val$alertDialog.dismiss();
/* 402 */         FragmentManager fm = ConversationFragment.this.getChildFragmentManager();
/* 403 */         if (fm.getBackStackEntryCount() > 0)
/* 404 */           fm.popBackStack();
/*     */         else
/* 406 */           ConversationFragment.this.getActivity().finish();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void onCustomServiceWarning(String msg, boolean evaluate)
/*     */   {
/* 420 */     if (getActivity() == null)
/* 421 */       return;
/* 422 */     AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
/* 423 */     builder.setCancelable(false);
/* 424 */     AlertDialog alertDialog = builder.create();
/* 425 */     alertDialog.show();
/* 426 */     Window window = alertDialog.getWindow();
/* 427 */     window.setContentView(R.layout.rc_cs_alert_warning);
/* 428 */     TextView tv = (TextView)window.findViewById(R.id.rc_cs_msg);
/* 429 */     tv.setText(msg);
/*     */ 
/* 431 */     window.findViewById(R.id.rc_btn_ok).setOnClickListener(new View.OnClickListener(alertDialog, evaluate)
/*     */     {
/*     */       public void onClick(View v) {
/* 434 */         this.val$alertDialog.dismiss();
/* 435 */         if (this.val$evaluate) {
/* 436 */           ConversationFragment.this.onCustomServiceEvaluation(false, "", ConversationFragment.this.robotType, this.val$evaluate);
/*     */         } else {
/* 438 */           FragmentManager fm = ConversationFragment.this.getChildFragmentManager();
/* 439 */           if (fm.getBackStackEntryCount() > 0)
/* 440 */             fm.popBackStack();
/*     */           else
/* 442 */             ConversationFragment.this.getActivity().finish();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public boolean onCustomServiceEvaluation(boolean isPullEva, String dialogId, boolean robotType, boolean evaluate)
/*     */   {
/* 461 */     if (!evaluate) {
/* 462 */       return false;
/*     */     }
/* 464 */     if (getActivity() == null) {
/* 465 */       return false;
/*     */     }
/* 467 */     long currentTime = System.currentTimeMillis();
/* 468 */     int interval = 60;
/*     */     try {
/* 470 */       interval = RongContext.getInstance().getResources().getInteger(R.integer.rc_custom_service_evaluation_interval);
/*     */     } catch (Resources.NotFoundException e) {
/* 472 */       e.printStackTrace();
/*     */     }
/* 474 */     if ((currentTime - this.enterTime < interval * 1000) && (!isPullEva)) {
/* 475 */       InputMethodManager imm = (InputMethodManager)getActivity().getSystemService("input_method");
/* 476 */       if ((imm != null) && (imm.isActive()) && (getActivity().getCurrentFocus() != null) && 
/* 477 */         (getActivity().getCurrentFocus().getWindowToken() != null)) {
/* 478 */         imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 2);
/*     */       }
/*     */ 
/* 481 */       FragmentManager fm = getChildFragmentManager();
/* 482 */       if (fm.getBackStackEntryCount() > 0)
/* 483 */         fm.popBackStack();
/*     */       else {
/* 485 */         getActivity().finish();
/*     */       }
/*     */ 
/* 488 */       return false;
/*     */     }
/* 490 */     this.committing = true;
/* 491 */     AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
/* 492 */     builder.setCancelable(false);
/* 493 */     AlertDialog alertDialog = builder.create();
/* 494 */     alertDialog.show();
/* 495 */     Window window = alertDialog.getWindow();
/* 496 */     if (robotType) {
/* 497 */       window.setContentView(R.layout.rc_cs_alert_robot_evaluation);
/* 498 */       LinearLayout linearLayout = (LinearLayout)window.findViewById(R.id.rc_cs_yes_no);
/* 499 */       if (this.resolved) {
/* 500 */         linearLayout.getChildAt(0).setSelected(true);
/* 501 */         linearLayout.getChildAt(1).setSelected(false);
/*     */       } else {
/* 503 */         linearLayout.getChildAt(0).setSelected(false);
/* 504 */         linearLayout.getChildAt(1).setSelected(true);
/*     */       }
/* 506 */       for (int i = 0; i < linearLayout.getChildCount(); i++) {
/* 507 */         View child = linearLayout.getChildAt(i);
/* 508 */         child.setOnClickListener(new View.OnClickListener(linearLayout)
/*     */         {
/*     */           public void onClick(View v) {
/* 511 */             v.setSelected(true);
/* 512 */             int index = this.val$linearLayout.indexOfChild(v);
/* 513 */             if (index == 0) {
/* 514 */               this.val$linearLayout.getChildAt(1).setSelected(false);
/* 515 */               ConversationFragment.access$502(ConversationFragment.this, true);
/*     */             } else {
/* 517 */               ConversationFragment.access$502(ConversationFragment.this, false);
/* 518 */               this.val$linearLayout.getChildAt(0).setSelected(false);
/*     */             }
/*     */           } } );
/*     */       }
/*     */     } else {
/* 524 */       window.setContentView(R.layout.rc_cs_alert_human_evaluation);
/* 525 */       LinearLayout linearLayout = (LinearLayout)window.findViewById(R.id.rc_cs_stars);
/* 526 */       for (int i = 0; i < linearLayout.getChildCount(); i++) {
/* 527 */         View child = linearLayout.getChildAt(i);
/* 528 */         if (i < this.source) {
/* 529 */           child.setSelected(true);
/*     */         }
/* 531 */         child.setOnClickListener(new View.OnClickListener(linearLayout)
/*     */         {
/*     */           public void onClick(View v) {
/* 534 */             int index = this.val$linearLayout.indexOfChild(v);
/* 535 */             int count = this.val$linearLayout.getChildCount();
/* 536 */             ConversationFragment.access$602(ConversationFragment.this, index + 1);
/* 537 */             if (!v.isSelected()) {
/* 538 */               while (index >= 0) {
/* 539 */                 this.val$linearLayout.getChildAt(index).setSelected(true);
/* 540 */                 index--;
/*     */               }
/*     */             }
/* 543 */             index++;
/* 544 */             while (index < count) {
/* 545 */               this.val$linearLayout.getChildAt(index).setSelected(false);
/* 546 */               index++;
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */ 
/* 554 */     window.findViewById(R.id.rc_btn_cancel).setOnClickListener(new View.OnClickListener(alertDialog)
/*     */     {
/*     */       public void onClick(View v) {
/* 557 */         ConversationFragment.access$302(ConversationFragment.this, false);
/* 558 */         this.val$alertDialog.dismiss();
/* 559 */         FragmentManager fm = ConversationFragment.this.getChildFragmentManager();
/* 560 */         if (fm.getBackStackEntryCount() > 0)
/* 561 */           fm.popBackStack();
/*     */         else
/* 563 */           ConversationFragment.this.getActivity().finish();
/*     */       }
/*     */     });
/* 568 */     window.findViewById(R.id.rc_btn_ok).setOnClickListener(new View.OnClickListener(robotType, dialogId, alertDialog)
/*     */     {
/*     */       public void onClick(View v) {
/* 571 */         if (this.val$robotType) {
/* 572 */           RongIMClient.getInstance().evaluateCustomService(ConversationFragment.this.mTargetId, ConversationFragment.this.resolved, "");
/*     */         }
/* 574 */         else if (ConversationFragment.this.source > 0) {
/* 575 */           RongIMClient.getInstance().evaluateCustomService(ConversationFragment.this.mTargetId, ConversationFragment.this.source, null, this.val$dialogId);
/*     */         }
/*     */ 
/* 578 */         this.val$alertDialog.dismiss();
/* 579 */         ConversationFragment.access$302(ConversationFragment.this, false);
/* 580 */         FragmentManager fm = ConversationFragment.this.getChildFragmentManager();
/* 581 */         if (fm.getBackStackEntryCount() > 0)
/* 582 */           fm.popBackStack();
/*     */         else
/* 584 */           ConversationFragment.this.getActivity().finish();
/*     */       }
/*     */     });
/* 588 */     return true;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.fragment.ConversationFragment
 * JD-Core Version:    0.6.0
 */
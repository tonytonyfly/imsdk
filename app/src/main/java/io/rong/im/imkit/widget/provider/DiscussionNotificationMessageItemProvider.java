/*     */ package io.rong.imkit.widget.provider;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.DialogInterface;
/*     */ import android.content.res.Resources;
/*     */ import android.os.Handler;
/*     */ import android.os.HandlerThread;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.text.Spannable;
/*     */ import android.text.SpannableString;
/*     */ import android.text.TextUtils;
/*     */ import android.text.method.LinkMovementMethod;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.TextView;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.model.ProviderTag;
/*     */ import io.rong.imkit.model.UIMessage;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.utils.MessageProviderUserInfoHelper;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment.OnArraysDialogItemListener;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Conversation.PublicServiceType;
/*     */ import io.rong.imlib.model.PublicServiceProfile;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import io.rong.message.DiscussionNotificationMessage;
/*     */ 
/*     */ @ProviderTag(messageContent=DiscussionNotificationMessage.class, showPortrait=false, centerInHorizontal=true, showSummaryWithName=false)
/*     */ public class DiscussionNotificationMessageItemProvider extends IContainerItemProvider.MessageProvider<DiscussionNotificationMessage>
/*     */ {
/*     */   private static final String TAG = "DiscussionNotificationMessageItemProvider";
/*     */   HandlerThread mWorkThread;
/*     */   Handler mDownloadHandler;
/*     */   private static final int DISCUSSION_ADD_MEMBER = 1;
/*     */   private static final int DISCUSSION_EXIT = 2;
/*     */   private static final int DISCUSSION_RENAME = 3;
/*     */   private static final int DISCUSSION_REMOVE = 4;
/*     */   private static final int DISCUSSION_MEMBER_INVITE = 5;
/*     */ 
/*     */   public DiscussionNotificationMessageItemProvider()
/*     */   {
/*  63 */     RongContext.getInstance().getEventBus().register(this);
/*  64 */     this.mWorkThread = new HandlerThread("DiscussionNotificationMessageItemProvider");
/*  65 */     this.mWorkThread.start();
/*  66 */     this.mDownloadHandler = new Handler(this.mWorkThread.getLooper());
/*     */   }
/*     */ 
/*     */   public void bindView(View v, int position, DiscussionNotificationMessage content, UIMessage message)
/*     */   {
/*  71 */     ViewHolder viewHolder = (ViewHolder)v.getTag();
/*  72 */     Spannable spannable = getContentSummary(content);
/*     */ 
/*  74 */     if ((spannable != null) && (spannable.length() > 0)) {
/*  75 */       viewHolder.contentTextView.setVisibility(0);
/*  76 */       viewHolder.contentTextView.setText(spannable);
/*     */     } else {
/*  78 */       viewHolder.contentTextView.setVisibility(8);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Spannable getContentSummary(DiscussionNotificationMessage data)
/*     */   {
/*  86 */     if (data == null) {
/*  87 */       RLog.e("DiscussionNotificationMessageItemProvider", "getContentSummary DiscussionNotificationMessage is null;");
/*  88 */       return new SpannableString("");
/*     */     }
/*  90 */     RLog.i("DiscussionNotificationMessageItemProvider", "getContentSummary call getContentSummary()  method ");
/*     */ 
/*  93 */     return new SpannableString(getWrapContent(RongContext.getInstance(), data));
/*     */   }
/*     */ 
/*     */   public void onItemClick(View view, int position, DiscussionNotificationMessage content, UIMessage message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public View newView(Context context, ViewGroup group)
/*     */   {
/* 105 */     View view = LayoutInflater.from(context).inflate(R.layout.rc_item_discussion_notification_message, null);
/*     */ 
/* 107 */     ViewHolder viewHolder = new ViewHolder();
/* 108 */     viewHolder.contentTextView = ((TextView)view.findViewById(R.id.rc_msg));
/* 109 */     viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
/* 110 */     view.setTag(viewHolder);
/*     */ 
/* 112 */     return view;
/*     */   }
/*     */ 
/*     */   private final String getWrapContent(Context context, DiscussionNotificationMessage discussionNotificationMessage)
/*     */   {
/* 127 */     if (discussionNotificationMessage == null) {
/* 128 */       return "";
/*     */     }
/* 130 */     String[] operatedUserIds = null;
/* 131 */     String extension = discussionNotificationMessage.getExtension();
/* 132 */     String operatorId = discussionNotificationMessage.getOperator();
/* 133 */     String currentUserId = "";
/* 134 */     String content = "";
/* 135 */     int operatedUserIdsLength = 0;
/*     */ 
/* 137 */     if (!TextUtils.isEmpty(extension))
/*     */     {
/* 139 */       if (extension.indexOf(",") != -1)
/* 140 */         operatedUserIds = extension.split(",");
/*     */       else {
/* 142 */         operatedUserIds = new String[] { extension };
/*     */       }
/* 144 */       operatedUserIdsLength = operatedUserIds.length;
/*     */     }
/*     */ 
/* 147 */     currentUserId = RongIM.getInstance().getCurrentUserId();
/*     */ 
/* 149 */     if (TextUtils.isEmpty(currentUserId)) {
/* 150 */       return "";
/*     */     }
/* 152 */     int operatorType = discussionNotificationMessage.getType();
/*     */ 
/* 161 */     switch (operatorType)
/*     */     {
/*     */     case 1:
/* 170 */       if (operatedUserIds == null)
/*     */         break;
/* 172 */       if (currentUserId.equals(operatorId)) {
/* 173 */         String you = context.getResources().getString(R.string.rc_discussion_nt_msg_for_you);
/*     */ 
/* 175 */         if (operatedUserIdsLength == 1)
/*     */         {
/* 177 */           String userId = operatedUserIds[0];
/* 178 */           UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
/*     */ 
/* 180 */           if (userInfo != null) {
/* 181 */             String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_added);
/* 182 */             content = String.format(formatString, new Object[] { you, userInfo.getName() });
/*     */           } else {
/* 184 */             MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, userId);
/*     */           }
/*     */         }
/*     */         else {
/* 188 */           String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_add);
/* 189 */           content = String.format(formatString, new Object[] { you, Integer.valueOf(operatedUserIdsLength) });
/*     */         }
/*     */ 
/*     */       }
/* 194 */       else if (operatedUserIdsLength == 1) {
/* 195 */         String userId = operatedUserIds[0];
/* 196 */         UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
/* 197 */         UserInfo operator = RongUserInfoManager.getInstance().getUserInfo(operatorId);
/*     */ 
/* 199 */         if ((userInfo != null) && (operator != null)) {
/* 200 */           String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_added);
/* 201 */           content = String.format(formatString, new Object[] { operator.getName(), userInfo.getName() });
/*     */         }
/*     */         else {
/* 204 */           if (userInfo == null) {
/* 205 */             MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, userId);
/*     */           }
/* 207 */           if (operator == null)
/* 208 */             MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
/*     */         }
/*     */       }
/*     */       else {
/* 212 */         UserInfo operator = RongUserInfoManager.getInstance().getUserInfo(operatorId);
/*     */ 
/* 214 */         if (operator != null) {
/* 215 */           String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_add);
/* 216 */           content = String.format(formatString, new Object[] { operator.getName(), Integer.valueOf(operatedUserIdsLength) });
/*     */         } else {
/* 218 */           MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
/*     */         }
/*     */       }
/* 220 */       break;
/*     */     case 2:
/* 232 */       UserInfo operator = RongUserInfoManager.getInstance().getUserInfo(operatorId);
/*     */ 
/* 234 */       if (operator != null) {
/* 235 */         String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_exit);
/* 236 */         content = String.format(formatString, new Object[] { operator.getName() });
/*     */       } else {
/* 238 */         MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
/*     */       }
/*     */ 
/* 241 */       break;
/*     */     case 3:
/* 249 */       if (currentUserId.equals(operatorId)) {
/* 250 */         String you = context.getResources().getString(R.string.rc_discussion_nt_msg_for_you);
/* 251 */         String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_rename);
/* 252 */         content = String.format(formatString, new Object[] { you, extension });
/*     */       } else {
/* 254 */         UserInfo operatorUserInfo = RongUserInfoManager.getInstance().getUserInfo(operatorId);
/*     */ 
/* 256 */         if (operatorUserInfo != null) {
/* 257 */           String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_rename);
/* 258 */           content = String.format(formatString, new Object[] { operatorUserInfo.getName(), extension });
/*     */         } else {
/* 260 */           MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
/*     */         }
/*     */       }
/*     */ 
/* 264 */       break;
/*     */     case 4:
/* 272 */       String operatedUserId = operatedUserIds[0];
/*     */ 
/* 274 */       if (currentUserId.equals(operatorId)) {
/* 275 */         UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(operatedUserId);
/*     */ 
/* 277 */         if (userInfo != null) {
/* 278 */           String you = context.getResources().getString(R.string.rc_discussion_nt_msg_for_you);
/* 279 */           String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_who_removed);
/* 280 */           content = String.format(formatString, new Object[] { userInfo.getName(), you });
/*     */         } else {
/* 282 */           MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
/*     */         }
/*     */ 
/*     */       }
/* 286 */       else if (currentUserId.equals(operatedUserId)) {
/* 287 */         UserInfo operatorUserInfo = RongUserInfoManager.getInstance().getUserInfo(operatorId);
/*     */ 
/* 289 */         if (operatorUserInfo != null) {
/* 290 */           String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_removed);
/* 291 */           content = String.format(formatString, new Object[] { operatorUserInfo.getName() });
/*     */         } else {
/* 293 */           MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 298 */         UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(operatedUserId);
/* 299 */         UserInfo operatorUserInfo = RongUserInfoManager.getInstance().getUserInfo(operatorId);
/*     */ 
/* 301 */         if ((userInfo != null) && (operatorUserInfo != null)) {
/* 302 */           String formatString = context.getResources().getString(R.string.rc_discussion_nt_msg_for_who_removed);
/* 303 */           content = String.format(formatString, new Object[] { userInfo.getName(), operatorUserInfo.getName() });
/*     */         }
/*     */         else {
/* 306 */           if (operatorUserInfo == null) {
/* 307 */             MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
/*     */           }
/* 309 */           if (userInfo == null) {
/* 310 */             MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatedUserId);
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 316 */       break;
/*     */     case 5:
/* 323 */       if (currentUserId.equals(operatorId)) {
/* 324 */         String you = context.getResources().getString(R.string.rc_discussion_nt_msg_for_you);
/*     */ 
/* 326 */         if ("1".equals(extension)) {
/* 327 */           String closeFormat = context.getResources().getString(R.string.rc_discussion_nt_msg_for_is_open_invite_close);
/* 328 */           content = String.format(closeFormat, new Object[] { you });
/* 329 */         } else if ("0".equals(extension)) {
/* 330 */           String openFormat = context.getResources().getString(R.string.rc_discussion_nt_msg_for_is_open_invite_open);
/* 331 */           content = String.format(openFormat, new Object[] { you });
/*     */         }
/*     */       } else {
/* 334 */         UserInfo operatorUserInfo = RongUserInfoManager.getInstance().getUserInfo(operatorId);
/*     */ 
/* 336 */         if (operatorUserInfo != null) {
/* 337 */           if ("1".equals(extension)) {
/* 338 */             String closeFormat = context.getResources().getString(R.string.rc_discussion_nt_msg_for_is_open_invite_close);
/* 339 */             content = String.format(closeFormat, new Object[] { operatorUserInfo.getName() });
/* 340 */           } else if ("0".equals(extension)) {
/* 341 */             String openFormat = context.getResources().getString(R.string.rc_discussion_nt_msg_for_is_open_invite_open);
/* 342 */             content = String.format(openFormat, new Object[] { operatorUserInfo.getName() });
/*     */           }
/*     */         }
/* 345 */         else MessageProviderUserInfoHelper.getInstance().registerMessageUserInfo(discussionNotificationMessage, operatorId);
/*     */ 
/*     */       }
/*     */ 
/* 349 */       break;
/*     */     default:
/* 352 */       content = "";
/*     */     }
/*     */ 
/* 356 */     RLog.i("DiscussionNotificationMessageItemProvider", "content return " + content);
/*     */ 
/* 358 */     return content;
/*     */   }
/*     */ 
/*     */   public void onItemLongClick(View view, int position, DiscussionNotificationMessage content, UIMessage message)
/*     */   {
/* 363 */     String name = null;
/* 364 */     if ((message.getConversationType().getName().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName())) || (message.getConversationType().getName().equals(Conversation.ConversationType.PUBLIC_SERVICE.getName())))
/*     */     {
/* 366 */       if (message.getUserInfo() != null) {
/* 367 */         name = message.getUserInfo().getName();
/*     */       } else {
/* 369 */         Conversation.PublicServiceType publicServiceType = Conversation.PublicServiceType.setValue(message.getConversationType().getValue());
/* 370 */         PublicServiceProfile info = RongUserInfoManager.getInstance().getPublicServiceProfile(publicServiceType, message.getTargetId());
/* 371 */         if (info != null)
/* 372 */           name = info.getName();
/*     */       }
/*     */     } else {
/* 375 */       UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
/* 376 */       if (userInfo != null) {
/* 377 */         name = userInfo.getName();
/*     */       }
/*     */     }
/*     */ 
/* 381 */     String[] items = { view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete) };
/*     */ 
/* 383 */     ArraysDialogFragment.newInstance(name, items).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener(message)
/*     */     {
/*     */       public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
/* 386 */         if (which == 0)
/* 387 */           RongIM.getInstance().deleteMessages(new int[] { this.val$message.getMessageId() }, null);
/*     */       }
/*     */     }).show(((FragmentActivity)view.getContext()).getSupportFragmentManager());
/*     */   }
/*     */ 
/*     */   public void onEventBackgroundThread(UserInfo userInfo)
/*     */   {
/* 395 */     if (userInfo.getName() == null) {
/* 396 */       return;
/*     */     }
/* 398 */     this.mDownloadHandler.postDelayed(new Runnable(userInfo)
/*     */     {
/*     */       public void run()
/*     */       {
/* 403 */         if (MessageProviderUserInfoHelper.getInstance().isCacheUserId(this.val$userInfo.getUserId()))
/* 404 */           MessageProviderUserInfoHelper.getInstance().notifyMessageUpdate(this.val$userInfo.getUserId());
/*     */       }
/*     */     }
/*     */     , 500L);
/*     */   }
/*     */ 
/*     */   class ViewHolder
/*     */   {
/*     */     TextView contentTextView;
/*     */ 
/*     */     ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.DiscussionNotificationMessageItemProvider
 * JD-Core Version:    0.6.0
 */
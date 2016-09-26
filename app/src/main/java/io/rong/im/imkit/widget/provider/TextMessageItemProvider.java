/*     */ package io.rong.imkit.widget.provider;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.DialogInterface;
/*     */ import android.content.res.Resources;
/*     */ import android.content.res.Resources.NotFoundException;
/*     */ import android.os.Handler;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.text.ClipboardManager;
/*     */ import android.text.Selection;
/*     */ import android.text.Spannable;
/*     */ import android.text.SpannableString;
/*     */ import android.text.SpannableStringBuilder;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.TextView;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imkit.R.bool;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.integer;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.RongIM.ConversationBehaviorListener;
/*     */ import io.rong.imkit.model.LinkTextView;
/*     */ import io.rong.imkit.model.ProviderTag;
/*     */ import io.rong.imkit.model.UIMessage;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.utils.AndroidEmoji;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment.OnArraysDialogItemListener;
/*     */ import io.rong.imkit.widget.ILinkClickListener;
/*     */ import io.rong.imkit.widget.LinkTextViewMovementMethod;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Conversation.PublicServiceType;
/*     */ import io.rong.imlib.model.Message.MessageDirection;
/*     */ import io.rong.imlib.model.Message.SentStatus;
/*     */ import io.rong.imlib.model.PublicServiceProfile;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import io.rong.message.TextMessage;
/*     */ 
/*     */ @ProviderTag(messageContent=TextMessage.class)
/*     */ public class TextMessageItemProvider extends IContainerItemProvider.MessageProvider<TextMessage>
/*     */ {
/*     */   private static final String TAG = "TextMessageItemProvider";
/*     */ 
/*     */   public View newView(Context context, ViewGroup group)
/*     */   {
/*  48 */     View view = LayoutInflater.from(context).inflate(R.layout.rc_item_text_message, null);
/*     */ 
/*  50 */     ViewHolder holder = new ViewHolder();
/*  51 */     holder.message = ((LinkTextView)view.findViewById(16908308));
/*  52 */     view.setTag(holder);
/*  53 */     return view;
/*     */   }
/*     */ 
/*     */   public Spannable getContentSummary(TextMessage data)
/*     */   {
/*  58 */     if (data == null) {
/*  59 */       return null;
/*     */     }
/*  61 */     String content = data.getContent();
/*  62 */     if (content != null) {
/*  63 */       if (content.length() > 100) {
/*  64 */         content = content.substring(0, 100);
/*     */       }
/*  66 */       return new SpannableString(AndroidEmoji.ensure(content));
/*     */     }
/*  68 */     return null;
/*     */   }
/*     */ 
/*     */   public void onItemClick(View view, int position, TextMessage content, UIMessage message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onItemLongClick(View view, int position, TextMessage content, UIMessage message)
/*     */   {
/*  78 */     ViewHolder holder = (ViewHolder)view.getTag();
/*  79 */     holder.longClick = true;
/*  80 */     if ((view instanceof TextView)) {
/*  81 */       CharSequence text = ((TextView)view).getText();
/*  82 */       if ((text != null) && ((text instanceof Spannable))) {
/*  83 */         Selection.removeSelection((Spannable)text);
/*     */       }
/*     */     }
/*  86 */     String name = null;
/*  87 */     if ((message.getConversationType().getName().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName())) || (message.getConversationType().getName().equals(Conversation.ConversationType.PUBLIC_SERVICE.getName())))
/*     */     {
/*  89 */       if (message.getUserInfo() != null) {
/*  90 */         name = message.getUserInfo().getName();
/*     */       } else {
/*  92 */         Conversation.PublicServiceType publicServiceType = Conversation.PublicServiceType.setValue(message.getConversationType().getValue());
/*  93 */         PublicServiceProfile info = RongUserInfoManager.getInstance().getPublicServiceProfile(publicServiceType, message.getTargetId());
/*     */ 
/*  95 */         if (info != null)
/*  96 */           name = info.getName();
/*     */       }
/*     */     }
/*  99 */     else if (message.getSenderUserId() != null) {
/* 100 */       UserInfo userInfo = message.getUserInfo();
/* 101 */       if ((userInfo == null) || (userInfo.getName() == null)) {
/* 102 */         userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
/*     */       }
/* 104 */       if (userInfo != null) {
/* 105 */         name = userInfo.getName();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 110 */     long deltaTime = RongIM.getInstance().getDeltaTime();
/* 111 */     long normalTime = System.currentTimeMillis() - deltaTime;
/* 112 */     boolean enableMessageRecall = false;
/* 113 */     int messageRecallInterval = -1;
/* 114 */     boolean hasSent = (!message.getSentStatus().equals(Message.SentStatus.SENDING)) && (!message.getSentStatus().equals(Message.SentStatus.FAILED));
/*     */     try
/*     */     {
/* 117 */       enableMessageRecall = RongContext.getInstance().getResources().getBoolean(R.bool.rc_enable_message_recall);
/* 118 */       messageRecallInterval = RongContext.getInstance().getResources().getInteger(R.integer.rc_message_recall_interval);
/*     */     } catch (Resources.NotFoundException e) {
/* 120 */       RLog.e("TextMessageItemProvider", "rc_message_recall_interval not configure in rc_config.xml");
/* 121 */       e.printStackTrace();
/*     */     }
/*     */     String[] items;
/*     */     String[] items;
/* 123 */     if ((hasSent) && (enableMessageRecall) && (normalTime - message.getSentTime() <= messageRecallInterval * 1000) && (message.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId())))
/* 124 */       items = new String[] { view.getContext().getResources().getString(R.string.rc_dialog_item_message_copy), view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete), view.getContext().getResources().getString(R.string.rc_dialog_item_message_recall) };
/*     */     else {
/* 126 */       items = new String[] { view.getContext().getResources().getString(R.string.rc_dialog_item_message_copy), view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete) };
/*     */     }
/*     */ 
/* 129 */     ArraysDialogFragment.newInstance(name, items).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener(view, content, message)
/*     */     {
/*     */       public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
/* 132 */         if (which == 0)
/*     */         {
/* 134 */           ClipboardManager clipboard = (ClipboardManager)this.val$view.getContext().getSystemService("clipboard");
/* 135 */           clipboard.setText(this.val$content.getContent());
/* 136 */         } else if (which == 1) {
/* 137 */           RongIM.getInstance().deleteMessages(new int[] { this.val$message.getMessageId() }, null);
/* 138 */         } else if (which == 2) {
/* 139 */           RongIM.getInstance().recallMessage(this.val$message);
/*     */         }
/*     */       }
/*     */     }).show(((FragmentActivity)view.getContext()).getSupportFragmentManager());
/*     */   }
/*     */ 
/*     */   public void bindView(View v, int position, TextMessage content, UIMessage data)
/*     */   {
/* 147 */     ViewHolder holder = (ViewHolder)v.getTag();
/*     */ 
/* 149 */     if (data.getMessageDirection() == Message.MessageDirection.SEND)
/* 150 */       holder.message.setBackgroundResource(R.drawable.rc_ic_bubble_right);
/*     */     else {
/* 152 */       holder.message.setBackgroundResource(R.drawable.rc_ic_bubble_left);
/*     */     }
/*     */ 
/* 155 */     TextView textView = holder.message;
/* 156 */     if (data.getTextMessageContent() != null) {
/* 157 */       int len = data.getTextMessageContent().length();
/* 158 */       if ((v.getHandler() != null) && (len > 500)) {
/* 159 */         v.getHandler().postDelayed(new Runnable(textView, data)
/*     */         {
/*     */           public void run() {
/* 162 */             this.val$textView.setText(this.val$data.getTextMessageContent());
/*     */           }
/*     */         }
/*     */         , 50L);
/*     */       }
/*     */       else
/*     */       {
/* 166 */         textView.setText(data.getTextMessageContent());
/*     */       }
/*     */     }
/*     */ 
/* 170 */     holder.message.setMovementMethod(new LinkTextViewMovementMethod(new ILinkClickListener(v)
/*     */     {
/*     */       public boolean onLinkClick(String link) {
/* 173 */         RongIM.ConversationBehaviorListener listener = RongContext.getInstance().getConversationBehaviorListener();
/* 174 */         if (listener != null) {
/* 175 */           return listener.onMessageLinkClick(this.val$v.getContext(), link);
/*     */         }
/* 177 */         return false;
/*     */       }
/*     */     }));
/*     */   }
/*     */ 
/*     */   class ViewHolder
/*     */   {
/*     */     LinkTextView message;
/*     */     boolean longClick;
/*     */ 
/*     */     ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.TextMessageItemProvider
 * JD-Core Version:    0.6.0
 */
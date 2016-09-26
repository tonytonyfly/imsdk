/*     */ package io.rong.imkit.widget.provider;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.DialogInterface;
/*     */ import android.content.res.Resources;
/*     */ import android.os.Handler;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.text.ClipboardManager;
/*     */ import android.text.Selection;
/*     */ import android.text.Spannable;
/*     */ import android.text.SpannableString;
/*     */ import android.text.SpannableStringBuilder;
/*     */ import android.text.TextUtils;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.View.OnLongClickListener;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.RelativeLayout;
/*     */ import android.widget.TextView;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.RongIM.ConversationBehaviorListener;
/*     */ import io.rong.imkit.RongIMClientWrapper;
/*     */ import io.rong.imkit.model.LinkTextView;
/*     */ import io.rong.imkit.model.UIMessage;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.utils.AndroidEmoji;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment.OnArraysDialogItemListener;
/*     */ import io.rong.imkit.widget.ILinkClickListener;
/*     */ import io.rong.imkit.widget.LinkTextViewMovementMethod;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Conversation.PublicServiceType;
/*     */ import io.rong.imlib.model.Message.MessageDirection;
/*     */ import io.rong.imlib.model.PublicServiceProfile;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import io.rong.message.TextMessage;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class EvaluateTextMessageItemProvider extends IContainerItemProvider.MessageProvider<TextMessage>
/*     */ {
/*     */   public View newView(Context context, ViewGroup group)
/*     */   {
/*  56 */     View view = LayoutInflater.from(context).inflate(R.layout.rc_item_text_message_evaluate, null);
/*     */ 
/*  58 */     ViewHolder holder = new ViewHolder();
/*  59 */     holder.message = ((LinkTextView)view.findViewById(R.id.evaluate_text));
/*  60 */     holder.tv_prompt = ((TextView)view.findViewById(R.id.tv_prompt));
/*  61 */     holder.iv_yes = ((ImageView)view.findViewById(R.id.iv_yes));
/*  62 */     holder.iv_no = ((ImageView)view.findViewById(R.id.iv_no));
/*  63 */     holder.iv_complete = ((ImageView)view.findViewById(R.id.iv_complete));
/*  64 */     holder.layout_praise = ((RelativeLayout)view.findViewById(R.id.layout_praise));
/*  65 */     view.setTag(holder);
/*  66 */     return view;
/*     */   }
/*     */ 
/*     */   public Spannable getContentSummary(TextMessage data)
/*     */   {
/*  71 */     if (data == null) {
/*  72 */       return null;
/*     */     }
/*  74 */     String content = data.getContent();
/*  75 */     if (content != null) {
/*  76 */       if (content.length() > 100) {
/*  77 */         content = content.substring(0, 100);
/*     */       }
/*  79 */       return new SpannableString(AndroidEmoji.ensure(content));
/*     */     }
/*  81 */     return null;
/*     */   }
/*     */ 
/*     */   public void onItemClick(View view, int position, TextMessage content, UIMessage message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onItemLongClick(View view, int position, TextMessage content, UIMessage message)
/*     */   {
/*  91 */     ViewHolder holder = (ViewHolder)view.getTag();
/*  92 */     holder.longClick = true;
/*  93 */     if ((view instanceof TextView)) {
/*  94 */       CharSequence text = ((TextView)view).getText();
/*  95 */       if ((text != null) && ((text instanceof Spannable))) {
/*  96 */         Selection.removeSelection((Spannable)text);
/*     */       }
/*     */     }
/*  99 */     String name = null;
/* 100 */     if ((message.getConversationType().getName().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName())) || (message.getConversationType().getName().equals(Conversation.ConversationType.PUBLIC_SERVICE.getName())))
/*     */     {
/* 102 */       Conversation.PublicServiceType publicServiceType = Conversation.PublicServiceType.setValue(message.getConversationType().getValue());
/* 103 */       PublicServiceProfile info = RongUserInfoManager.getInstance().getPublicServiceProfile(publicServiceType, message.getTargetId());
/*     */ 
/* 105 */       if (info != null)
/* 106 */         name = info.getName();
/*     */     }
/* 108 */     else if (message.getSenderUserId() != null) {
/* 109 */       UserInfo userInfo = message.getUserInfo();
/* 110 */       if ((userInfo == null) || (userInfo.getName() == null)) {
/* 111 */         userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
/*     */       }
/* 113 */       if (userInfo != null) {
/* 114 */         name = userInfo.getName();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 119 */     String[] items = { view.getContext().getResources().getString(R.string.rc_dialog_item_message_copy), view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete) };
/*     */ 
/* 121 */     ArraysDialogFragment.newInstance(name, items).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener(view, content, message)
/*     */     {
/*     */       public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
/* 124 */         if (which == 0)
/*     */         {
/* 126 */           ClipboardManager clipboard = (ClipboardManager)this.val$view.getContext().getSystemService("clipboard");
/* 127 */           clipboard.setText(this.val$content.getContent());
/* 128 */         } else if (which == 1) {
/* 129 */           RongIM.getInstance().getRongIMClient().deleteMessages(new int[] { this.val$message.getMessageId() }, null);
/*     */         }
/*     */       }
/*     */     }).show(((FragmentActivity)view.getContext()).getSupportFragmentManager());
/*     */   }
/*     */ 
/*     */   public void bindView(View v, int position, TextMessage content, UIMessage data)
/*     */   {
/* 138 */     ViewHolder holder = (ViewHolder)v.getTag();
/* 139 */     if (data.getMessageDirection() == Message.MessageDirection.SEND)
/* 140 */       v.setBackgroundResource(R.drawable.rc_ic_bubble_right);
/*     */     else {
/* 142 */       v.setBackgroundResource(R.drawable.rc_ic_bubble_left);
/*     */     }
/* 144 */     if (data.getEvaluated())
/*     */     {
/* 146 */       holder.iv_yes.setVisibility(8);
/* 147 */       holder.iv_no.setVisibility(8);
/* 148 */       holder.iv_complete.setVisibility(0);
/* 149 */       holder.tv_prompt.setText("感谢您的评价");
/*     */     }
/*     */     else
/*     */     {
/* 153 */       holder.iv_yes.setVisibility(0);
/* 154 */       holder.iv_no.setVisibility(0);
/* 155 */       holder.iv_complete.setVisibility(8);
/* 156 */       holder.tv_prompt.setText("您对我的回答");
/*     */     }
/* 158 */     holder.iv_yes.setOnClickListener(new View.OnClickListener(data, holder)
/*     */     {
/*     */       public void onClick(View v)
/*     */       {
/* 162 */         String extra = ((TextMessage)this.val$data.getContent()).getExtra();
/* 163 */         String knowledgeId = "";
/* 164 */         if (!TextUtils.isEmpty(extra))
/*     */           try {
/* 166 */             JSONObject jsonObj = new JSONObject(extra);
/* 167 */             knowledgeId = jsonObj.optString("sid");
/*     */           }
/*     */           catch (JSONException e)
/*     */           {
/*     */           }
/* 172 */         RongIMClient.getInstance().evaluateCustomService(this.val$data.getSenderUserId(), true, knowledgeId);
/* 173 */         this.val$holder.iv_complete.setVisibility(0);
/* 174 */         this.val$holder.iv_yes.setVisibility(8);
/* 175 */         this.val$holder.iv_no.setVisibility(8);
/* 176 */         this.val$holder.tv_prompt.setText("感谢您的评价");
/* 177 */         this.val$data.setEvaluated(true);
/*     */       }
/*     */     });
/* 181 */     holder.iv_no.setOnClickListener(new View.OnClickListener(data, holder)
/*     */     {
/*     */       public void onClick(View v) {
/* 184 */         String extra = ((TextMessage)this.val$data.getContent()).getExtra();
/* 185 */         String knowledgeId = "";
/* 186 */         if (!TextUtils.isEmpty(extra))
/*     */           try {
/* 188 */             JSONObject jsonObj = new JSONObject(extra);
/* 189 */             knowledgeId = jsonObj.optString("sid");
/*     */           }
/*     */           catch (JSONException e)
/*     */           {
/*     */           }
/* 194 */         RongIMClient.getInstance().evaluateCustomService(this.val$data.getSenderUserId(), false, knowledgeId);
/* 195 */         this.val$holder.iv_complete.setVisibility(0);
/* 196 */         this.val$holder.iv_yes.setVisibility(8);
/* 197 */         this.val$holder.iv_no.setVisibility(8);
/* 198 */         this.val$holder.tv_prompt.setText("感谢您的评价");
/* 199 */         this.val$data.setEvaluated(true);
/*     */       }
/*     */     });
/* 202 */     TextView textView = holder.message;
/* 203 */     if (data.getTextMessageContent() != null) {
/* 204 */       int len = data.getTextMessageContent().length();
/* 205 */       if ((v.getHandler() != null) && (len > 500)) {
/* 206 */         v.getHandler().postDelayed(new Runnable(textView, data)
/*     */         {
/*     */           public void run() {
/* 209 */             this.val$textView.setText(this.val$data.getTextMessageContent());
/*     */           }
/*     */         }
/*     */         , 50L);
/*     */       }
/*     */       else
/*     */       {
/* 213 */         textView.setText(data.getTextMessageContent());
/*     */       }
/*     */     }
/*     */ 
/* 217 */     holder.message.setClickable(true);
/* 218 */     holder.message.setOnClickListener(new View.OnClickListener()
/*     */     {
/*     */       public void onClick(View v)
/*     */       {
/*     */       }
/*     */     });
/* 223 */     holder.message.setOnLongClickListener(new View.OnLongClickListener(v, position, content, data)
/*     */     {
/*     */       public boolean onLongClick(View v1) {
/* 226 */         EvaluateTextMessageItemProvider.this.onItemLongClick(this.val$v, this.val$position, this.val$content, this.val$data);
/* 227 */         return false;
/*     */       }
/*     */     });
/* 231 */     holder.message.setMovementMethod(new LinkTextViewMovementMethod(new ILinkClickListener(v)
/*     */     {
/*     */       public boolean onLinkClick(String link) {
/* 234 */         RongIM.ConversationBehaviorListener listener = RongContext.getInstance().getConversationBehaviorListener();
/* 235 */         if (listener != null) {
/* 236 */           return listener.onMessageLinkClick(this.val$v.getContext(), link);
/*     */         }
/* 238 */         return false;
/*     */       }
/*     */     }));
/*     */   }
/*     */ 
/*     */   class ViewHolder
/*     */   {
/*     */     LinkTextView message;
/*     */     TextView tv_prompt;
/*     */     ImageView iv_yes;
/*     */     ImageView iv_no;
/*     */     ImageView iv_complete;
/*     */     RelativeLayout layout_praise;
/*     */     boolean longClick;
/*     */ 
/*     */     ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.EvaluateTextMessageItemProvider
 * JD-Core Version:    0.6.0
 */
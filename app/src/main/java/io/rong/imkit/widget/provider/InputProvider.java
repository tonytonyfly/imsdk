/*     */ package io.rong.imkit.widget.provider;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.Intent;
/*     */ import android.graphics.drawable.Drawable;
/*     */ import android.text.TextUtils;
/*     */ import android.util.Log;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.fragment.MessageInputFragment;
/*     */ import io.rong.imkit.widget.InputView;
/*     */ import io.rong.imlib.IRongCallback.ISendMessageCallback;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.TypingMessage.TypingMessageManager;
/*     */ import io.rong.imlib.model.Conversation;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.imlib.model.MessageContent;
/*     */ 
/*     */ public abstract class InputProvider
/*     */ {
/*     */   private static final String TAG = "InputProvider";
/*     */   RongContext mContext;
/*     */   MessageInputFragment mFragment;
/*     */   int index;
/*     */   InputView mCurrentView;
/*     */   Conversation mCurrentConversation;
/*     */ 
/*     */   public int getIndex()
/*     */   {
/*  38 */     return this.index;
/*     */   }
/*     */ 
/*     */   public void setIndex(int index) {
/*  42 */     this.index = index;
/*     */   }
/*     */ 
/*     */   public RongContext getContext()
/*     */   {
/*  53 */     return this.mContext;
/*     */   }
/*     */ 
/*     */   public InputProvider(RongContext context)
/*     */   {
/*  62 */     this.mContext = context;
/*     */   }
/*     */ 
/*     */   public void setCurrentConversation(Conversation conversation)
/*     */   {
/*  72 */     this.mCurrentConversation = conversation;
/*     */   }
/*     */ 
/*     */   public Conversation getCurrentConversation()
/*     */   {
/*  81 */     return this.mCurrentConversation;
/*     */   }
/*     */ 
/*     */   public void onActivityResult(int requestCode, int resultCode, Intent data)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void startActivityForResult(Intent intent, int requestCode)
/*     */   {
/* 110 */     if (this.mFragment != null)
/* 111 */       this.mFragment.startActivityFromProvider(this, intent, requestCode);
/*     */   }
/*     */ 
/*     */   public MessageInputFragment getCurrentFragment()
/*     */   {
/* 121 */     return this.mFragment;
/*     */   }
/*     */ 
/*     */   public void publish(MessageContent content)
/*     */   {
/* 131 */     publish(content, null);
/*     */   }
/*     */ 
/*     */   public void publish(MessageContent content, RongIMClient.ResultCallback<Message> callback)
/*     */   {
/* 141 */     if (content == null) {
/* 142 */       RLog.w("InputProvider", "publish content is null");
/* 143 */       return;
/*     */     }
/*     */ 
/* 146 */     if ((this.mCurrentConversation == null) || (TextUtils.isEmpty(this.mCurrentConversation.getTargetId())) || (this.mCurrentConversation.getConversationType() == null)) {
/* 147 */       Log.e("InputProvider", "the conversation hasn't been created yet!!!");
/* 148 */       return;
/*     */     }
/*     */ 
/* 151 */     Message message = Message.obtain(this.mCurrentConversation.getTargetId(), this.mCurrentConversation.getConversationType(), content);
/* 152 */     RongIM.getInstance().sendMessage(message, null, null, new IRongCallback.ISendMessageCallback()
/*     */     {
/*     */       public void onAttached(Message message)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onSuccess(Message message)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onError(Message message, RongIMClient.ErrorCode errorCode)
/*     */       {
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void onTypingMessage(String objectName)
/*     */   {
/* 174 */     if ((TypingMessageManager.getInstance().isShowMessageTyping()) && 
/* 175 */       (RongIMClient.getInstance() != null))
/* 176 */       RongIMClient.getInstance().sendTypingStatus(this.mCurrentConversation.getConversationType(), this.mCurrentConversation.getTargetId(), objectName);
/*     */   }
/*     */ 
/*     */   public void onAttached(MessageInputFragment fragment, InputView inputView)
/*     */   {
/* 189 */     this.mFragment = fragment;
/* 190 */     this.mCurrentView = inputView;
/*     */   }
/*     */ 
/*     */   public void onDetached()
/*     */   {
/* 197 */     this.mFragment = null;
/* 198 */     this.mCurrentView = null;
/*     */   }
/*     */ 
/*     */   public InputView getInputView()
/*     */   {
/* 207 */     return this.mCurrentView;
/*     */   }
/*     */ 
/*     */   public static abstract class ExtendProvider extends InputProvider
/*     */   {
/*     */     public ExtendProvider(RongContext context)
/*     */     {
/* 291 */       super();
/*     */     }
/*     */ 
/*     */     public abstract Drawable obtainPluginDrawable(Context paramContext);
/*     */ 
/*     */     public abstract CharSequence obtainPluginTitle(Context paramContext);
/*     */ 
/*     */     public abstract void onPluginClick(View paramView);
/*     */   }
/*     */ 
/*     */   public static abstract class MainInputProvider extends InputProvider
/*     */   {
/*     */     public MainInputProvider(RongContext context)
/*     */     {
/* 221 */       super();
/*     */     }
/*     */ 
/*     */     public abstract Drawable obtainSwitchDrawable(Context paramContext);
/*     */ 
/*     */     public abstract View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, InputView paramInputView);
/*     */ 
/*     */     public abstract void onActive(Context paramContext);
/*     */ 
/*     */     public abstract void onInactive(Context paramContext);
/*     */ 
/*     */     public abstract void onSwitch(Context paramContext);
/*     */ 
/*     */     public void onInputResume(InputView inputView, Conversation conversation)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void onInputPause()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.InputProvider
 * JD-Core Version:    0.6.0
 */
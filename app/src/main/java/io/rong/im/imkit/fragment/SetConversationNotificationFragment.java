/*     */ package io.rong.imkit.fragment;
/*     */ 
/*     */ import android.os.Message;
/*     */ import android.text.TextUtils;
/*     */ import android.view.View;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.model.Event.ConversationNotificationEvent;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.model.Conversation.ConversationNotificationStatus;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ 
/*     */ public class SetConversationNotificationFragment extends BaseSettingFragment
/*     */ {
/*     */   private static final String TAG = "SetConversationNotificationFragment";
/*     */ 
/*     */   public static SetConversationNotificationFragment newInstance()
/*     */   {
/*  21 */     return new SetConversationNotificationFragment();
/*     */   }
/*     */ 
/*     */   protected void initData()
/*     */   {
/*  28 */     if (RongContext.getInstance() != null) {
/*  29 */       RongContext.getInstance().getEventBus().register(this);
/*     */     }
/*  31 */     RongIM.getInstance().getConversationNotificationStatus(getConversationType(), getTargetId(), new RongIMClient.ResultCallback()
/*     */     {
/*     */       public void onSuccess(Conversation.ConversationNotificationStatus notificationStatus)
/*     */       {
/*  36 */         if (notificationStatus != null)
/*  37 */           SetConversationNotificationFragment.this.setSwitchBtnStatus(notificationStatus != Conversation.ConversationNotificationStatus.DO_NOT_DISTURB);
/*     */       }
/*     */ 
/*     */       public void onError(RongIMClient.ErrorCode errorCode)
/*     */       {
/*  44 */         SetConversationNotificationFragment.this.setSwitchBtnStatus(!SetConversationNotificationFragment.this.getSwitchBtnStatus());
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   protected boolean setSwitchButtonEnabled()
/*     */   {
/*  53 */     return true;
/*     */   }
/*     */ 
/*     */   protected String setTitle()
/*     */   {
/*  58 */     return getString(R.string.rc_setting_conversation_notify);
/*     */   }
/*     */ 
/*     */   public boolean handleMessage(Message msg)
/*     */   {
/*  63 */     return false;
/*     */   }
/*     */ 
/*     */   protected void onSettingItemClick(View v)
/*     */   {
/*  68 */     RLog.i("SetConversationNotificationFragment", "onSettingItemClick, " + v.toString());
/*     */   }
/*     */ 
/*     */   protected int setSwitchBtnVisibility()
/*     */   {
/*  74 */     return 0;
/*     */   }
/*     */ 
/*     */   protected void toggleSwitch(boolean toggle)
/*     */   {
/*     */     Conversation.ConversationNotificationStatus status;
/*     */     Conversation.ConversationNotificationStatus status;
/*  82 */     if (toggle)
/*  83 */       status = Conversation.ConversationNotificationStatus.NOTIFY;
/*     */     else {
/*  85 */       status = Conversation.ConversationNotificationStatus.DO_NOT_DISTURB;
/*     */     }
/*     */ 
/*  88 */     if ((getConversationType() != null) && (!TextUtils.isEmpty(getTargetId()))) {
/*  89 */       RongIM.getInstance().setConversationNotificationStatus(getConversationType(), getTargetId(), status, new RongIMClient.ResultCallback()
/*     */       {
/*     */         public void onSuccess(Conversation.ConversationNotificationStatus status)
/*     */         {
/*  93 */           RLog.i("SetConversationNotificationFragment", "SetConversationNotificationFragment onSuccess--");
/*     */         }
/*     */ 
/*     */         public void onError(RongIMClient.ErrorCode errorCode)
/*     */         {
/*  98 */           SetConversationNotificationFragment.this.setSwitchBtnStatus(!SetConversationNotificationFragment.this.getSwitchBtnStatus());
/*     */         }
/*     */       });
/*     */     }
/*     */     else
/* 104 */       RLog.e("SetConversationNotificationFragment", "SetConversationNotificationFragment Arguments is null");
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.ConversationNotificationEvent event)
/*     */   {
/* 110 */     if ((event != null) && (event.getTargetId().equals(getTargetId())) && (event.getConversationType().getValue() == getConversationType().getValue()))
/* 111 */       setSwitchBtnStatus(event.getStatus() == Conversation.ConversationNotificationStatus.NOTIFY);
/*     */   }
/*     */ 
/*     */   public void onDestroy()
/*     */   {
/* 118 */     if (RongContext.getInstance() != null) {
/* 119 */       RongContext.getInstance().getEventBus().unregister(this);
/*     */     }
/* 121 */     super.onDestroy();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.fragment.SetConversationNotificationFragment
 * JD-Core Version:    0.6.0
 */
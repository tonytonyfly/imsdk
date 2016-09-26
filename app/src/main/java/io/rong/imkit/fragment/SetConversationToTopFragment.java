/*    */ package io.rong.imkit.fragment;
/*    */ 
/*    */ import android.os.Message;
/*    */ import android.text.TextUtils;
/*    */ import android.view.View;
/*    */ import io.rong.common.RLog;
/*    */ import io.rong.eventbus.EventBus;
/*    */ import io.rong.imkit.R.string;
/*    */ import io.rong.imkit.RongContext;
/*    */ import io.rong.imkit.RongIM;
/*    */ import io.rong.imkit.model.Event.ConversationTopEvent;
/*    */ import io.rong.imlib.RongIMClient.ErrorCode;
/*    */ import io.rong.imlib.RongIMClient.ResultCallback;
/*    */ import io.rong.imlib.model.Conversation;
/*    */ import io.rong.imlib.model.Conversation.ConversationType;
/*    */ 
/*    */ public class SetConversationToTopFragment extends BaseSettingFragment
/*    */ {
/* 20 */   private static String TAG = SetConversationToTopFragment.class.getSimpleName();
/*    */ 
/*    */   protected void initData()
/*    */   {
/* 26 */     if (RongContext.getInstance() != null) {
/* 27 */       RongContext.getInstance().getEventBus().register(this);
/*    */     }
/* 29 */     RongIM.getInstance().getConversation(getConversationType(), getTargetId(), new RongIMClient.ResultCallback()
/*    */     {
/*    */       public void onSuccess(Conversation conversation)
/*    */       {
/* 33 */         if (conversation != null)
/* 34 */           SetConversationToTopFragment.this.setSwitchBtnStatus(conversation.isTop());
/*    */       }
/*    */ 
/*    */       public void onError(RongIMClient.ErrorCode e)
/*    */       {
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   protected boolean setSwitchButtonEnabled()
/*    */   {
/* 46 */     return true;
/*    */   }
/*    */ 
/*    */   protected String setTitle()
/*    */   {
/* 51 */     return getString(R.string.rc_setting_set_top);
/*    */   }
/*    */ 
/*    */   public boolean handleMessage(Message msg)
/*    */   {
/* 56 */     return false;
/*    */   }
/*    */ 
/*    */   protected void onSettingItemClick(View v)
/*    */   {
/* 61 */     RLog.i(TAG, "onSettingItemClick, " + v.toString());
/*    */   }
/*    */ 
/*    */   protected int setSwitchBtnVisibility()
/*    */   {
/* 66 */     return 0;
/*    */   }
/*    */ 
/*    */   protected void toggleSwitch(boolean toggle)
/*    */   {
/* 72 */     if ((getConversationType() != null) && (!TextUtils.isEmpty(getTargetId())))
/* 73 */       RongIM.getInstance().setConversationToTop(getConversationType(), getTargetId(), toggle, null);
/*    */     else
/* 75 */       RLog.e(TAG, "toggleSwitch() args is null");
/*    */   }
/*    */ 
/*    */   public void onEventMainThread(Event.ConversationTopEvent conversationTopEvent)
/*    */   {
/* 81 */     if ((conversationTopEvent != null) && (conversationTopEvent.getTargetId().equals(getTargetId())) && (conversationTopEvent.getConversationType().getValue() == getConversationType().getValue()))
/* 82 */       setSwitchBtnStatus(conversationTopEvent.isTop());
/*    */   }
/*    */ 
/*    */   public void onDestroy()
/*    */   {
/* 89 */     if (RongContext.getInstance() != null) {
/* 90 */       RongContext.getInstance().getEventBus().unregister(this);
/*    */     }
/* 92 */     super.onDestroy();
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.fragment.SetConversationToTopFragment
 * JD-Core Version:    0.6.0
 */
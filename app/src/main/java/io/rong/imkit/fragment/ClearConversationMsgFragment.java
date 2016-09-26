/*    */ package io.rong.imkit.fragment;
/*    */ 
/*    */ import android.os.Message;
/*    */ import android.view.View;
/*    */ import android.widget.Toast;
/*    */ import io.rong.imkit.R.string;
/*    */ import io.rong.imkit.RongIM;
/*    */ import io.rong.imkit.widget.AlterDialogFragment;
/*    */ import io.rong.imkit.widget.AlterDialogFragment.AlterDialogBtnListener;
/*    */ import io.rong.imlib.RongIMClient.ErrorCode;
/*    */ import io.rong.imlib.RongIMClient.ResultCallback;
/*    */ import io.rong.imlib.model.Conversation;
/*    */ 
/*    */ public class ClearConversationMsgFragment extends BaseSettingFragment
/*    */   implements AlterDialogFragment.AlterDialogBtnListener
/*    */ {
/*    */   private Conversation conversation;
/*    */ 
/*    */   protected void initData()
/*    */   {
/*    */   }
/*    */ 
/*    */   protected String setTitle()
/*    */   {
/* 28 */     return getString(R.string.rc_setting_clear_msg_name);
/*    */   }
/*    */ 
/*    */   protected boolean setSwitchButtonEnabled()
/*    */   {
/* 33 */     return false;
/*    */   }
/*    */ 
/*    */   protected int setSwitchBtnVisibility()
/*    */   {
/* 38 */     return 8;
/*    */   }
/*    */ 
/*    */   protected void onSettingItemClick(View v)
/*    */   {
/* 43 */     this.conversation = new Conversation();
/* 44 */     this.conversation.setConversationType(getConversationType());
/* 45 */     this.conversation.setTargetId(getTargetId());
/*    */ 
/* 47 */     AlterDialogFragment dialogFragment = AlterDialogFragment.newInstance(getString(R.string.rc_setting_name), getString(R.string.rc_setting_clear_msg_prompt), getString(R.string.rc_dialog_cancel), getString(R.string.rc_dialog_ok));
/* 48 */     dialogFragment.setOnAlterDialogBtnListener(this);
/* 49 */     dialogFragment.show(getFragmentManager());
/*    */   }
/*    */ 
/*    */   public void onDialogNegativeClick(AlterDialogFragment dialog)
/*    */   {
/* 56 */     dialog.dismiss();
/*    */   }
/*    */ 
/*    */   public void onDialogPositiveClick(AlterDialogFragment dialog)
/*    */   {
/* 61 */     if (this.conversation == null) {
/* 62 */       return;
/*    */     }
/* 64 */     RongIM.getInstance().clearMessages(this.conversation.getConversationType(), this.conversation.getTargetId(), new RongIMClient.ResultCallback()
/*    */     {
/*    */       public void onSuccess(Boolean aBoolean)
/*    */       {
/* 68 */         Toast.makeText(ClearConversationMsgFragment.this.getActivity(), ClearConversationMsgFragment.this.getString(R.string.rc_setting_clear_msg_success), 0).show();
/*    */       }
/*    */ 
/*    */       public void onError(RongIMClient.ErrorCode e)
/*    */       {
/* 73 */         Toast.makeText(ClearConversationMsgFragment.this.getActivity(), ClearConversationMsgFragment.this.getString(R.string.rc_setting_clear_msg_fail), 0).show();
/*    */       }
/*    */     });
/* 76 */     RongIM.getInstance().clearTextMessageDraft(this.conversation.getConversationType(), this.conversation.getTargetId(), null);
/*    */   }
/*    */ 
/*    */   protected void toggleSwitch(boolean toggle)
/*    */   {
/*    */   }
/*    */ 
/*    */   public boolean handleMessage(Message msg)
/*    */   {
/* 86 */     return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.fragment.ClearConversationMsgFragment
 * JD-Core Version:    0.6.0
 */
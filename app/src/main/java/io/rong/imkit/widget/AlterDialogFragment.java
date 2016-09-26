/*    */ package io.rong.imkit.widget;
/*    */ 
/*    */ import android.app.AlertDialog.Builder;
/*    */ import android.app.Dialog;
/*    */ import android.content.DialogInterface;
/*    */ import android.content.DialogInterface.OnClickListener;
/*    */ import android.os.Bundle;
/*    */ import android.support.annotation.NonNull;
/*    */ import android.support.v4.app.FragmentManager;
/*    */ import android.text.TextUtils;
/*    */ 
/*    */ public class AlterDialogFragment extends BaseDialogFragment
/*    */ {
/*    */   private static final String ARGS_TITLE = "args_title";
/*    */   private static final String ARGS_MESSAGE = "args_message";
/*    */   private static final String ARGS_CANCEL_BTN_TXT = "args_cancel_button_text";
/*    */   private static final String ARGS_OK_BTN_TXT = "args_ok_button_text";
/*    */   private AlterDialogBtnListener mAlterDialogBtnListener;
/*    */ 
/*    */   public static AlterDialogFragment newInstance(String title, String message, String cancelBtnText, String okBtnText)
/*    */   {
/* 27 */     AlterDialogFragment dialogFragment = new AlterDialogFragment();
/* 28 */     Bundle args = new Bundle();
/* 29 */     args.putString("args_title", title);
/* 30 */     args.putString("args_message", message);
/* 31 */     args.putString("args_cancel_button_text", cancelBtnText);
/* 32 */     args.putString("args_ok_button_text", okBtnText);
/* 33 */     dialogFragment.setArguments(args);
/*    */ 
/* 35 */     return dialogFragment;
/*    */   }
/*    */ 
/*    */   @NonNull
/*    */   public Dialog onCreateDialog(Bundle savedInstanceState)
/*    */   {
/* 43 */     String title = getArguments().getString("args_title");
/* 44 */     String message = getArguments().getString("args_message");
/* 45 */     String cancelBtnText = getArguments().getString("args_cancel_button_text");
/* 46 */     String okBtnText = getArguments().getString("args_ok_button_text");
/*    */ 
/* 48 */     AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
/*    */ 
/* 50 */     if (!TextUtils.isEmpty(title)) {
/* 51 */       builder.setTitle(title);
/*    */     }
/*    */ 
/* 54 */     if (!TextUtils.isEmpty(message)) {
/* 55 */       builder.setMessage(message);
/*    */     }
/*    */ 
/* 58 */     if (!TextUtils.isEmpty(okBtnText)) {
/* 59 */       builder.setPositiveButton(okBtnText, new DialogInterface.OnClickListener() {
/*    */         public void onClick(DialogInterface dialog, int id) {
/* 61 */           if (AlterDialogFragment.this.mAlterDialogBtnListener != null) {
/* 62 */             AlterDialogFragment.this.mAlterDialogBtnListener.onDialogPositiveClick(AlterDialogFragment.this);
/*    */           }
/*    */         }
/*    */       });
/*    */     }
/* 68 */     if (!TextUtils.isEmpty(cancelBtnText)) {
/* 69 */       builder.setNegativeButton(cancelBtnText, new DialogInterface.OnClickListener() {
/*    */         public void onClick(DialogInterface dialog, int id) {
/* 71 */           if (AlterDialogFragment.this.mAlterDialogBtnListener != null) {
/* 72 */             AlterDialogFragment.this.mAlterDialogBtnListener.onDialogNegativeClick(AlterDialogFragment.this);
/*    */           }
/*    */         }
/*    */       });
/*    */     }
/* 78 */     return builder.create();
/*    */   }
/*    */ 
/*    */   public void show(FragmentManager manager)
/*    */   {
/* 89 */     show(manager, "AlterDialogFragment");
/*    */   }
/*    */ 
/*    */   public void setOnAlterDialogBtnListener(AlterDialogBtnListener alterDialogListener)
/*    */   {
/* 94 */     this.mAlterDialogBtnListener = alterDialogListener;
/*    */   }
/*    */ 
/*    */   public static abstract interface AlterDialogBtnListener
/*    */   {
/*    */     public abstract void onDialogPositiveClick(AlterDialogFragment paramAlterDialogFragment);
/*    */ 
/*    */     public abstract void onDialogNegativeClick(AlterDialogFragment paramAlterDialogFragment);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.AlterDialogFragment
 * JD-Core Version:    0.6.0
 */
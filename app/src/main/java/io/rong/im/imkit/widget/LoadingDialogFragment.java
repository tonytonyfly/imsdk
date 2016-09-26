/*    */ package io.rong.imkit.widget;
/*    */ 
/*    */ import android.app.Dialog;
/*    */ import android.app.ProgressDialog;
/*    */ import android.os.Bundle;
/*    */ import android.support.annotation.NonNull;
/*    */ import android.support.v4.app.FragmentManager;
/*    */ import android.text.TextUtils;
/*    */ 
/*    */ public class LoadingDialogFragment extends BaseDialogFragment
/*    */ {
/*    */   private static final String ARGS_TITLE = "args_title";
/*    */   private static final String ARGS_MESSAGE = "args_message";
/*    */ 
/*    */   public static LoadingDialogFragment newInstance(String title, String message)
/*    */   {
/* 23 */     LoadingDialogFragment dialogFragment = new LoadingDialogFragment();
/* 24 */     Bundle args = new Bundle();
/* 25 */     args.putString("args_title", title);
/* 26 */     args.putString("args_message", message);
/* 27 */     dialogFragment.setArguments(args);
/*    */ 
/* 29 */     return dialogFragment;
/*    */   }
/*    */ 
/*    */   public void onCreate(Bundle savedInstanceState)
/*    */   {
/* 34 */     super.onCreate(savedInstanceState);
/*    */   }
/*    */ 
/*    */   @NonNull
/*    */   public Dialog onCreateDialog(Bundle savedInstanceState)
/*    */   {
/* 41 */     ProgressDialog dialog = new ProgressDialog(getActivity());
/* 42 */     String title = getArguments().getString("args_title");
/* 43 */     String message = getArguments().getString("args_message");
/*    */ 
/* 45 */     dialog.setIndeterminate(true);
/* 46 */     dialog.setProgressStyle(0);
/*    */ 
/* 48 */     if (!TextUtils.isEmpty(title)) {
/* 49 */       dialog.setTitle(title);
/*    */     }
/* 51 */     if (!TextUtils.isEmpty(message)) {
/* 52 */       dialog.setMessage(message);
/*    */     }
/* 54 */     return dialog;
/*    */   }
/*    */ 
/*    */   public void show(FragmentManager manager) {
/* 58 */     show(manager, "LoadingDialogFragment");
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.LoadingDialogFragment
 * JD-Core Version:    0.6.0
 */
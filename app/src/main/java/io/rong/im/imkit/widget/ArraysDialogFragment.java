/*    */ package io.rong.imkit.widget;
/*    */ 
/*    */ import android.app.AlertDialog.Builder;
/*    */ import android.app.Dialog;
/*    */ import android.content.DialogInterface;
/*    */ import android.content.DialogInterface.OnClickListener;
/*    */ import android.os.Bundle;
/*    */ import android.support.annotation.NonNull;
/*    */ import android.support.v4.app.FragmentManager;
/*    */ import android.support.v4.app.FragmentTransaction;
/*    */ 
/*    */ public class ArraysDialogFragment extends BaseDialogFragment
/*    */ {
/*    */   private static final String ARGS_TITLE = "args_title";
/*    */   private static final String ARGS_ARRAYS = "args_arrays";
/*    */   private OnArraysDialogItemListener mItemListener;
/*    */   private int count;
/*    */ 
/*    */   public static ArraysDialogFragment newInstance(String title, String[] arrays)
/*    */   {
/* 24 */     ArraysDialogFragment dialogFragment = new ArraysDialogFragment();
/* 25 */     Bundle bundle = new Bundle();
/* 26 */     bundle.putString("args_title", title);
/* 27 */     bundle.putStringArray("args_arrays", arrays);
/* 28 */     dialogFragment.setArguments(bundle);
/* 29 */     return dialogFragment;
/*    */   }
/*    */ 
/*    */   @NonNull
/*    */   public Dialog onCreateDialog(Bundle savedInstanceState) {
/* 35 */     String title = getArguments().getString("args_title");
/* 36 */     String[] arrays = getArguments().getStringArray("args_arrays");
/*    */ 
/* 38 */     AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
/* 39 */     builder.setTitle(title);
/*    */ 
/* 41 */     if ((arrays.length > 0) && (arrays[0] != null)) {
/* 42 */       setCount(arrays.length);
/*    */ 
/* 44 */       builder.setItems(arrays, new DialogInterface.OnClickListener()
/*    */       {
/*    */         public void onClick(DialogInterface dialog, int which) {
/* 47 */           if (ArraysDialogFragment.this.mItemListener != null) {
/* 48 */             ArraysDialogFragment.this.mItemListener.OnArraysDialogItemClick(dialog, which);
/*    */           }
/*    */         }
/*    */       });
/*    */     }
/* 54 */     return builder.create();
/*    */   }
/*    */ 
/*    */   public int getCount() {
/* 58 */     return this.count;
/*    */   }
/*    */ 
/*    */   public void setCount(int count) {
/* 62 */     this.count = count;
/*    */   }
/*    */ 
/*    */   public ArraysDialogFragment setArraysDialogItemListener(OnArraysDialogItemListener mItemListener) {
/* 66 */     this.mItemListener = mItemListener;
/* 67 */     return this;
/*    */   }
/*    */ 
/*    */   public void show(FragmentManager manager)
/*    */   {
/* 75 */     FragmentTransaction ft = manager.beginTransaction();
/* 76 */     ft.add(this, "ArraysDialogFragment");
/* 77 */     ft.commitAllowingStateLoss();
/*    */   }
/*    */ 
/*    */   public static abstract interface OnArraysDialogItemListener
/*    */   {
/*    */     public abstract void OnArraysDialogItemClick(DialogInterface paramDialogInterface, int paramInt);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.ArraysDialogFragment
 * JD-Core Version:    0.6.0
 */
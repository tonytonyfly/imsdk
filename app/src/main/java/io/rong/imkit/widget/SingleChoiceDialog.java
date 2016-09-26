/*     */ package io.rong.imkit.widget;
/*     */ 
/*     */ import android.app.Dialog;
/*     */ import android.content.Context;
/*     */ import android.content.DialogInterface.OnClickListener;
/*     */ import android.graphics.drawable.ColorDrawable;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.ViewGroup.LayoutParams;
/*     */ import android.view.Window;
/*     */ import android.view.WindowManager.LayoutParams;
/*     */ import android.widget.AdapterView;
/*     */ import android.widget.AdapterView.OnItemClickListener;
/*     */ import android.widget.Button;
/*     */ import android.widget.ListAdapter;
/*     */ import android.widget.ListView;
/*     */ import android.widget.TextView;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import java.util.List;
/*     */ 
/*     */ public class SingleChoiceDialog extends Dialog
/*     */ {
/*     */   protected Context mContext;
/*     */   protected View mRootView;
/*     */   protected TextView mTVTitle;
/*     */   protected TextView mButtonOK;
/*     */   protected TextView mButtonCancel;
/*     */   protected ListView mListView;
/*     */   protected List<String> mList;
/*     */   protected DialogInterface.OnClickListener mOkClickListener;
/*     */   protected DialogInterface.OnClickListener mCancelClickListener;
/*     */   private SingleChoiceAdapter<String> mSingleChoiceAdapter;
/*     */ 
/*     */   public SingleChoiceDialog(Context context, List<String> list)
/*     */   {
/*  41 */     super(context);
/*     */ 
/*  44 */     this.mContext = context;
/*  45 */     this.mList = list;
/*     */ 
/*  47 */     initView(this.mContext);
/*  48 */     initData();
/*     */   }
/*     */ 
/*     */   protected void initView(Context context) {
/*  52 */     requestWindowFeature(1);
/*  53 */     setContentView(R.layout.rc_cs_single_choice_layout);
/*  54 */     this.mRootView = findViewById(R.id.rc_cs_rootView);
/*  55 */     this.mRootView.setBackgroundDrawable(new ColorDrawable(0));
/*  56 */     this.mTVTitle = ((TextView)findViewById(R.id.rc_cs_tv_title));
/*  57 */     this.mButtonOK = ((Button)findViewById(R.id.rc_cs_btn_ok));
/*  58 */     this.mButtonOK.setOnClickListener(new View.OnClickListener()
/*     */     {
/*     */       public void onClick(View v) {
/*  61 */         SingleChoiceDialog.this.onButtonOK();
/*     */       }
/*     */     });
/*  64 */     this.mButtonCancel = ((Button)findViewById(R.id.rc_cs_btn_cancel));
/*  65 */     this.mButtonCancel.setOnClickListener(new View.OnClickListener()
/*     */     {
/*     */       public void onClick(View v) {
/*  68 */         SingleChoiceDialog.this.onButtonCancel();
/*     */       }
/*     */     });
/*  72 */     this.mListView = ((ListView)findViewById(R.id.rc_cs_group_dialog_listView));
/*     */ 
/*  74 */     Window dialogWindow = getWindow();
/*  75 */     WindowManager.LayoutParams lp = dialogWindow.getAttributes();
/*     */ 
/*  77 */     ColorDrawable dw = new ColorDrawable(0);
/*  78 */     dialogWindow.setBackgroundDrawable(dw);
/*     */   }
/*     */ 
/*     */   public void setTitle(String title) {
/*  82 */     this.mTVTitle.setText(title);
/*     */   }
/*     */ 
/*     */   public void setOnOKButtonListener(DialogInterface.OnClickListener onClickListener) {
/*  86 */     this.mOkClickListener = onClickListener;
/*     */   }
/*     */ 
/*     */   public void setOnCancelButtonListener(DialogInterface.OnClickListener onClickListener) {
/*  90 */     this.mCancelClickListener = onClickListener;
/*     */   }
/*     */ 
/*     */   protected void onButtonOK() {
/*  94 */     dismiss();
/*  95 */     if (this.mOkClickListener != null)
/*  96 */       this.mOkClickListener.onClick(this, 0);
/*     */   }
/*     */ 
/*     */   protected void onButtonCancel()
/*     */   {
/* 101 */     dismiss();
/* 102 */     if (this.mCancelClickListener != null)
/* 103 */       this.mCancelClickListener.onClick(this, 0);
/*     */   }
/*     */ 
/*     */   protected void initData()
/*     */   {
/* 108 */     this.mSingleChoiceAdapter = new SingleChoiceAdapter(this.mContext, this.mList, R.drawable.rc_cs_group_checkbox_selector);
/*     */ 
/* 111 */     this.mListView.setAdapter(this.mSingleChoiceAdapter);
/* 112 */     this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
/*     */     {
/*     */       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/* 115 */         if (position != SingleChoiceDialog.this.mSingleChoiceAdapter.getSelectItem()) {
/* 116 */           if (!SingleChoiceDialog.this.mButtonOK.isEnabled()) {
/* 117 */             SingleChoiceDialog.this.mButtonOK.setEnabled(true);
/*     */           }
/* 119 */           SingleChoiceDialog.this.mSingleChoiceAdapter.setSelectItem(position);
/* 120 */           SingleChoiceDialog.this.mSingleChoiceAdapter.notifyDataSetChanged();
/*     */         }
/*     */       }
/*     */     });
/* 125 */     setListViewHeightBasedOnChildren(this.mListView);
/*     */   }
/*     */ 
/*     */   public int getSelectItem()
/*     */   {
/* 130 */     return this.mSingleChoiceAdapter.getSelectItem();
/*     */   }
/*     */ 
/*     */   private void setListViewHeightBasedOnChildren(ListView listView) {
/* 134 */     ListAdapter listAdapter = listView.getAdapter();
/* 135 */     if (listAdapter == null)
/*     */     {
/* 137 */       return;
/*     */     }
/*     */ 
/* 140 */     int totalHeight = 0;
/* 141 */     for (int i = 0; i < listAdapter.getCount(); i++) {
/* 142 */       View listItem = listAdapter.getView(i, null, listView);
/* 143 */       listItem.measure(0, 0);
/* 144 */       totalHeight += listItem.getMeasuredHeight();
/*     */     }
/* 146 */     totalHeight += 10;
/* 147 */     ViewGroup.LayoutParams params = listView.getLayoutParams();
/* 148 */     params.height = (totalHeight + listView.getDividerHeight() * (listAdapter.getCount() - 1));
/* 149 */     listView.setLayoutParams(params);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.SingleChoiceDialog
 * JD-Core Version:    0.6.0
 */
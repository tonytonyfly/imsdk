/*     */ package io.rong.imkit.fragment;
/*     */ 
/*     */ import android.content.Intent;
/*     */ import android.net.Uri;
/*     */ import android.os.Bundle;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.CheckBox;
/*     */ import android.widget.RelativeLayout;
/*     */ import android.widget.TextView;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ 
/*     */ public abstract class BaseSettingFragment extends BaseFragment
/*     */   implements View.OnClickListener
/*     */ {
/*     */   TextView mTextView;
/*     */   CheckBox mCheckBox;
/*     */   RelativeLayout mSettingItem;
/*     */   String mTargetId;
/*     */   Conversation.ConversationType mConversationType;
/*     */ 
/*     */   public void onCreate(Bundle savedInstanceState)
/*     */   {
/*  31 */     super.onCreate(savedInstanceState);
/*     */ 
/*  33 */     Intent intent = null;
/*     */ 
/*  35 */     if (getActivity() != null)
/*     */     {
/*  37 */       intent = getActivity().getIntent();
/*     */ 
/*  39 */       if (intent.getData() != null)
/*     */       {
/*  41 */         this.mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase());
/*     */ 
/*  44 */         this.mTargetId = intent.getData().getQueryParameter("targetId");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
/*     */   {
/*  52 */     View view = inflater.inflate(R.layout.rc_fragment_base_setting, container, false);
/*  53 */     this.mTextView = ((TextView)view.findViewById(R.id.rc_title));
/*  54 */     this.mCheckBox = ((CheckBox)view.findViewById(R.id.rc_checkbox));
/*  55 */     this.mSettingItem = ((RelativeLayout)view.findViewById(R.id.rc_setting_item));
/*     */ 
/*  57 */     return view;
/*     */   }
/*     */ 
/*     */   public void onActivityCreated(Bundle savedInstanceState)
/*     */   {
/*  63 */     this.mTextView.setText(setTitle());
/*  64 */     this.mCheckBox.setEnabled(setSwitchButtonEnabled());
/*     */ 
/*  66 */     if (8 == setSwitchBtnVisibility())
/*  67 */       this.mCheckBox.setVisibility(8);
/*  68 */     else if (0 == setSwitchBtnVisibility()) {
/*  69 */       this.mCheckBox.setVisibility(0);
/*     */     }
/*  71 */     this.mCheckBox.setOnClickListener(this);
/*  72 */     this.mSettingItem.setOnClickListener(this);
/*     */ 
/*  74 */     initData();
/*     */ 
/*  76 */     super.onActivityCreated(savedInstanceState);
/*     */   }
/*     */ 
/*     */   public void onClick(View v)
/*     */   {
/*  82 */     if (v == this.mSettingItem)
/*  83 */       onSettingItemClick(v);
/*  84 */     else if (v == this.mCheckBox)
/*  85 */       toggleSwitch(this.mCheckBox.isChecked());
/*     */   }
/*     */ 
/*     */   public void onDestroy()
/*     */   {
/*  92 */     super.onDestroy();
/*  93 */     RongContext.getInstance().getEventBus().unregister(this);
/*     */   }
/*     */ 
/*     */   protected Conversation.ConversationType getConversationType()
/*     */   {
/*  98 */     return this.mConversationType;
/*     */   }
/*     */ 
/*     */   protected String getTargetId() {
/* 102 */     return this.mTargetId; } 
/*     */   protected abstract String setTitle();
/*     */ 
/*     */   protected abstract boolean setSwitchButtonEnabled();
/*     */ 
/*     */   protected abstract int setSwitchBtnVisibility();
/*     */ 
/*     */   protected abstract void onSettingItemClick(View paramView);
/*     */ 
/*     */   protected abstract void toggleSwitch(boolean paramBoolean);
/*     */ 
/*     */   protected abstract void initData();
/*     */ 
/* 119 */   protected void setSwitchBtnStatus(boolean status) { this.mCheckBox.setChecked(status); }
/*     */ 
/*     */   protected boolean getSwitchBtnStatus()
/*     */   {
/* 123 */     return this.mCheckBox.isChecked();
/*     */   }
/*     */ 
/*     */   public boolean onBackPressed()
/*     */   {
/* 129 */     return false;
/*     */   }
/*     */ 
/*     */   public void onRestoreUI()
/*     */   {
/* 134 */     initData();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.fragment.BaseSettingFragment
 * JD-Core Version:    0.6.0
 */
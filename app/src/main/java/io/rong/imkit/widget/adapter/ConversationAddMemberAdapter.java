/*     */ package io.rong.imkit.widget.adapter;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.net.Uri;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.TextView;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.widget.AsyncImageView;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ 
/*     */ public class ConversationAddMemberAdapter extends BaseAdapter<UserInfo>
/*     */ {
/*     */   LayoutInflater mInflater;
/*  17 */   Boolean isDeleteState = Boolean.valueOf(false);
/*  18 */   String mCreatorId = null;
/*     */   private OnDeleteIconListener mDeleteIconListener;
/*     */ 
/*     */   public ConversationAddMemberAdapter(Context context)
/*     */   {
/*  30 */     this.mInflater = LayoutInflater.from(context);
/*  31 */     this.isDeleteState = Boolean.valueOf(false);
/*     */   }
/*     */ 
/*     */   protected View newView(Context context, int position, ViewGroup group)
/*     */   {
/*  36 */     View result = this.mInflater.inflate(R.layout.rc_item_conversation_member, null);
/*     */ 
/*  38 */     ViewHolder holder = new ViewHolder();
/*  39 */     holder.mMemberIcon = ((AsyncImageView)findViewById(result, 16908294));
/*  40 */     holder.mMemberName = ((TextView)findViewById(result, 16908308));
/*  41 */     holder.mDeleteIcon = ((ImageView)findViewById(result, 16908295));
/*  42 */     holder.mMemberDeIcon = ((ImageView)findViewById(result, 16908296));
/*     */ 
/*  44 */     result.setTag(holder);
/*  45 */     return result;
/*     */   }
/*     */ 
/*     */   protected void bindView(View v, int position, UserInfo data)
/*     */   {
/*  50 */     ViewHolder holder = (ViewHolder)v.getTag();
/*     */ 
/*  52 */     if ((data.getUserId().equals("RongAddBtn")) || (data.getUserId().equals("RongDelBtn"))) {
/*  53 */       holder.mMemberIcon.setVisibility(4);
/*  54 */       holder.mMemberDeIcon.setVisibility(0);
/*  55 */       if (data.getUserId().equals("RongAddBtn"))
/*  56 */         holder.mMemberDeIcon.setImageResource(R.drawable.rc_ic_setting_friends_add);
/*     */       else
/*  58 */         holder.mMemberDeIcon.setImageResource(R.drawable.rc_ic_setting_friends_delete);
/*  59 */       holder.mMemberName.setVisibility(4);
/*  60 */       holder.mDeleteIcon.setVisibility(8);
/*     */     }
/*     */     else {
/*  63 */       holder.mMemberIcon.setVisibility(0);
/*  64 */       holder.mMemberDeIcon.setVisibility(8);
/*  65 */       if (data.getPortraitUri() != null) {
/*  66 */         holder.mMemberIcon.setResource(data.getPortraitUri().toString(), R.drawable.rc_default_portrait);
/*     */       }
/*  68 */       if (data.getName() != null)
/*  69 */         holder.mMemberName.setText(data.getName());
/*     */       else {
/*  71 */         holder.mMemberName.setText("");
/*     */       }
/*  73 */       if ((isDeleteState()) && (!data.getUserId().equals(getCreatorId()))) {
/*  74 */         holder.mDeleteIcon.setVisibility(0);
/*  75 */         holder.mDeleteIcon.setOnClickListener(new View.OnClickListener(position)
/*     */         {
/*     */           public void onClick(View v) {
/*  78 */             if (ConversationAddMemberAdapter.this.mDeleteIconListener != null)
/*  79 */               ConversationAddMemberAdapter.this.mDeleteIconListener.onDeleteIconClick(v, this.val$position);
/*     */           } } );
/*     */       }
/*     */       else {
/*  84 */         holder.mDeleteIcon.setVisibility(4);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getItemId(int position) {
/*  90 */     UserInfo info = (UserInfo)getItem(position);
/*  91 */     if (info == null)
/*  92 */       return 0L;
/*  93 */     return info.hashCode();
/*     */   }
/*     */ 
/*     */   public void setDeleteState(boolean state) {
/*  97 */     this.isDeleteState = Boolean.valueOf(state);
/*     */   }
/*     */ 
/*     */   public boolean isDeleteState() {
/* 101 */     return this.isDeleteState.booleanValue();
/*     */   }
/*     */ 
/*     */   public void setCreatorId(String id) {
/* 105 */     this.mCreatorId = id;
/*     */   }
/*     */ 
/*     */   public String getCreatorId() {
/* 109 */     return this.mCreatorId;
/*     */   }
/*     */ 
/*     */   public void setDeleteIconListener(OnDeleteIconListener listener) {
/* 113 */     this.mDeleteIconListener = listener;
/*     */   }
/*     */ 
/*     */   public static abstract interface OnDeleteIconListener
/*     */   {
/*     */     public abstract void onDeleteIconClick(View paramView, int paramInt);
/*     */   }
/*     */ 
/*     */   class ViewHolder
/*     */   {
/*     */     AsyncImageView mMemberIcon;
/*     */     TextView mMemberName;
/*     */     ImageView mDeleteIcon;
/*     */     ImageView mMemberDeIcon;
/*     */ 
/*     */     ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.adapter.ConversationAddMemberAdapter
 * JD-Core Version:    0.6.0
 */
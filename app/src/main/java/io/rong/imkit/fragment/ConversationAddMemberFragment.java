/*     */ package io.rong.imkit.fragment;
/*     */ 
/*     */ import android.content.Intent;
/*     */ import android.net.Uri;
/*     */ import android.os.Bundle;
/*     */ import android.os.Handler;
/*     */ import android.os.Message;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.MotionEvent;
/*     */ import android.view.View;
/*     */ import android.view.View.OnTouchListener;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.AdapterView;
/*     */ import android.widget.AdapterView.OnItemClickListener;
/*     */ import android.widget.GridView;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.RongIM.OnSelectMemberListener;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.widget.adapter.ConversationAddMemberAdapter;
/*     */ import io.rong.imkit.widget.adapter.ConversationAddMemberAdapter.OnDeleteIconListener;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.OperationCallback;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Discussion;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ConversationAddMemberFragment extends BaseFragment
/*     */   implements AdapterView.OnItemClickListener, ConversationAddMemberAdapter.OnDeleteIconListener
/*     */ {
/*     */   static final int PREPARE_LIST = 1;
/*     */   static final int REMOVE_ITEM = 2;
/*     */   static final int SHOW_TOAST = 3;
/*     */   private Conversation.ConversationType mConversationType;
/*     */   private String mTargetId;
/*     */   private ConversationAddMemberAdapter mAdapter;
/*  34 */   private List<String> mIdList = new ArrayList();
/*  35 */   private ArrayList<UserInfo> mMembers = new ArrayList();
/*     */   private GridView mGridList;
/*     */ 
/*     */   public void onCreate(Bundle savedInstanceState)
/*     */   {
/*  41 */     super.onCreate(savedInstanceState);
/*     */ 
/*  44 */     RongContext.getInstance().getEventBus().register(this);
/*     */ 
/*  46 */     if (getActivity() != null)
/*     */     {
/*  48 */       Intent intent = getActivity().getIntent();
/*     */ 
/*  50 */       if (intent.getData() != null)
/*     */       {
/*  52 */         this.mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase());
/*     */ 
/*  55 */         this.mTargetId = intent.getData().getQueryParameter("targetId");
/*     */       }
/*     */     }
/*  58 */     this.mAdapter = new ConversationAddMemberAdapter(getActivity());
/*  59 */     this.mAdapter.setDeleteIconListener(this);
/*  60 */     initData();
/*     */   }
/*     */ 
/*     */   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
/*     */   {
/*  65 */     View view = inflater.inflate(R.layout.rc_fr_conversation_member_list, null);
/*  66 */     this.mGridList = ((GridView)findViewById(view, R.id.rc_list));
/*  67 */     return view;
/*     */   }
/*     */ 
/*     */   public void onViewCreated(View view, Bundle savedInstanceState) {
/*  71 */     super.onViewCreated(view, savedInstanceState);
/*  72 */     this.mGridList.setAdapter(this.mAdapter);
/*  73 */     this.mGridList.setOnItemClickListener(this);
/*     */ 
/*  75 */     this.mGridList.setOnTouchListener(new View.OnTouchListener()
/*     */     {
/*     */       public boolean onTouch(View v, MotionEvent event)
/*     */       {
/*  79 */         if ((1 == event.getAction()) && (ConversationAddMemberFragment.this.mAdapter.isDeleteState())) {
/*  80 */           UserInfo addBtn = new UserInfo("RongAddBtn", null, null);
/*  81 */           ConversationAddMemberFragment.this.mAdapter.add(addBtn);
/*     */ 
/*  83 */           String curUserId = RongIM.getInstance().getCurrentUserId();
/*  84 */           if ((ConversationAddMemberFragment.this.mAdapter.getCreatorId() != null) && (ConversationAddMemberFragment.this.mConversationType.equals(Conversation.ConversationType.DISCUSSION)) && (curUserId.equals(ConversationAddMemberFragment.this.mAdapter.getCreatorId()))) {
/*  85 */             UserInfo deleteBtn = new UserInfo("RongDelBtn", null, null);
/*  86 */             ConversationAddMemberFragment.this.mAdapter.add(deleteBtn);
/*     */           }
/*     */ 
/*  90 */           ConversationAddMemberFragment.this.mAdapter.setDeleteState(false);
/*  91 */           ConversationAddMemberFragment.this.mAdapter.notifyDataSetChanged();
/*  92 */           return true;
/*     */         }
/*  94 */         return false;
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private void initData() {
/* 100 */     if (this.mConversationType.equals(Conversation.ConversationType.DISCUSSION)) {
/* 101 */       RongIM.getInstance().getDiscussion(this.mTargetId, new RongIMClient.ResultCallback()
/*     */       {
/*     */         public void onSuccess(Discussion discussion) {
/* 104 */           ConversationAddMemberFragment.access$202(ConversationAddMemberFragment.this, discussion.getMemberIdList());
/* 105 */           ConversationAddMemberFragment.this.mAdapter.setCreatorId(discussion.getCreatorId());
/* 106 */           Message msg = new Message();
/* 107 */           msg.what = 1;
/* 108 */           msg.obj = ConversationAddMemberFragment.this.mIdList;
/* 109 */           ConversationAddMemberFragment.this.getHandler().sendMessage(msg);
/*     */         }
/*     */ 
/*     */         public void onError(RongIMClient.ErrorCode errorCode)
/*     */         {
/* 114 */           ConversationAddMemberFragment.this.getHandler().sendEmptyMessage(3);
/*     */         } } );
/* 117 */     } else if (this.mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
/* 118 */       this.mIdList.add(this.mTargetId);
/* 119 */       Message msg = new Message();
/* 120 */       msg.what = 1;
/* 121 */       msg.obj = this.mIdList;
/* 122 */       getHandler().sendMessage(msg);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(UserInfo userInfo)
/*     */   {
/* 128 */     int count = this.mAdapter.getCount();
/*     */ 
/* 130 */     for (int i = 0; i < count; i++) {
/* 131 */       UserInfo temp = (UserInfo)this.mAdapter.getItem(i);
/* 132 */       if (userInfo.getUserId().equals(temp.getUserId())) {
/* 133 */         temp.setName(userInfo.getName());
/* 134 */         temp.setPortraitUri(userInfo.getPortraitUri());
/* 135 */         this.mAdapter.getView(i, this.mGridList.getChildAt(i - this.mGridList.getFirstVisiblePosition()), this.mGridList);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onItemClick(AdapterView<?> parent, View view, int position, long id)
/*     */   {
/* 142 */     UserInfo userInfo = (UserInfo)this.mAdapter.getItem(position);
/* 143 */     if (userInfo.getUserId().equals("RongDelBtn")) {
/* 144 */       this.mAdapter.setDeleteState(true);
/* 145 */       int count = this.mAdapter.getCount();
/* 146 */       this.mAdapter.remove(count - 1);
/* 147 */       this.mAdapter.remove(count - 2);
/* 148 */       this.mAdapter.notifyDataSetChanged();
/* 149 */     } else if (userInfo.getUserId().equals("RongAddBtn")) {
/* 150 */       if (RongContext.getInstance().getMemberSelectListener() == null) {
/* 151 */         throw new ExceptionInInitializerError("The OnMemberSelectListener hasn't been set!");
/*     */       }
/* 153 */       RongContext.getInstance().getMemberSelectListener().startSelectMember(getActivity(), this.mConversationType, this.mTargetId);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onDeleteIconClick(View view, int position)
/*     */   {
/* 159 */     UserInfo temp = (UserInfo)this.mAdapter.getItem(position);
/* 160 */     RongIM.getInstance().removeMemberFromDiscussion(this.mTargetId, temp.getUserId(), new RongIMClient.OperationCallback(position)
/*     */     {
/*     */       public void onSuccess() {
/* 163 */         Message msg = new Message();
/* 164 */         msg.what = 2;
/* 165 */         msg.obj = Integer.valueOf(this.val$position);
/* 166 */         ConversationAddMemberFragment.this.getHandler().sendMessage(msg);
/*     */       }
/*     */ 
/*     */       public void onError(RongIMClient.ErrorCode errorCode)
/*     */       {
/* 171 */         ConversationAddMemberFragment.this.getHandler().sendEmptyMessage(3);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public boolean handleMessage(Message msg) {
/* 178 */     switch (msg.what) {
/*     */     case 1:
/* 180 */       List mMemberInfo = (List)msg.obj;
/* 181 */       int i = 0;
/* 182 */       for (String id : mMemberInfo) {
/* 183 */         if (i >= 50) break;
/* 184 */         UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(id);
/* 185 */         if (userInfo == null)
/* 186 */           this.mMembers.add(new UserInfo(id, null, null));
/*     */         else {
/* 188 */           this.mMembers.add(userInfo);
/*     */         }
/*     */ 
/* 193 */         i++;
/*     */       }
/* 195 */       UserInfo addBtn = new UserInfo("RongAddBtn", null, null);
/* 196 */       this.mMembers.add(addBtn);
/*     */ 
/* 198 */       String curUserId = RongIM.getInstance().getCurrentUserId();
/* 199 */       if ((this.mAdapter.getCreatorId() != null) && (this.mConversationType.equals(Conversation.ConversationType.DISCUSSION)) && (curUserId.equals(this.mAdapter.getCreatorId()))) {
/* 200 */         UserInfo deleteBtn = new UserInfo("RongDelBtn", null, null);
/* 201 */         this.mMembers.add(deleteBtn);
/*     */       }
/*     */ 
/* 204 */       this.mAdapter.addCollection(this.mMembers);
/* 205 */       this.mAdapter.notifyDataSetChanged();
/* 206 */       break;
/*     */     case 2:
/* 208 */       int position = ((Integer)msg.obj).intValue();
/* 209 */       this.mAdapter.remove(position);
/* 210 */       this.mAdapter.notifyDataSetChanged();
/* 211 */       break;
/*     */     case 3:
/*     */     }
/*     */ 
/* 215 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean onBackPressed()
/*     */   {
/* 220 */     return false;
/*     */   }
/*     */ 
/*     */   public void onRestoreUI()
/*     */   {
/* 225 */     initData();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.fragment.ConversationAddMemberFragment
 * JD-Core Version:    0.6.0
 */
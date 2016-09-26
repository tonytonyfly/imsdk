/*     */ package io.rong.imkit.fragment;
/*     */ 
/*     */ import android.content.Intent;
/*     */ import android.content.res.Resources;
/*     */ import android.net.Uri;
/*     */ import android.os.Bundle;
/*     */ import android.os.Message;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.support.v4.app.FragmentManager;
/*     */ import android.support.v4.app.FragmentTransaction;
/*     */ import android.text.TextUtils;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.Button;
/*     */ import android.widget.TextView;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.RongIM.PublicServiceBehaviorListener;
/*     */ import io.rong.imkit.model.Event.PublicServiceFollowableEvent;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.widget.AsyncImageView;
/*     */ import io.rong.imkit.widget.LoadingDialogFragment;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.OperationCallback;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Conversation.PublicServiceType;
/*     */ import io.rong.imlib.model.PublicServiceProfile;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class PublicServiceProfileFragment extends DispatchResultFragment
/*     */ {
/*     */   public static final String AGS_PUBLIC_ACCOUNT_INFO = "arg_public_account_info";
/*     */   PublicServiceProfile mPublicAccountInfo;
/*     */   private AsyncImageView mPortraitIV;
/*     */   private TextView mNameTV;
/*     */   private TextView mAccountTV;
/*     */   private TextView mDescriptionTV;
/*     */   private Button mEnterBtn;
/*     */   private Button mFollowBtn;
/*     */   private Button mUnfollowBtn;
/*     */   private String mTargetId;
/*     */   private Conversation.ConversationType mConversationType;
/*     */   private String name;
/*     */   private LoadingDialogFragment mLoadingDialogFragment;
/*     */ 
/*     */   protected void initFragment(Uri uri)
/*     */   {
/*  52 */     if (getActivity().getIntent() != null) {
/*  53 */       this.mPublicAccountInfo = ((PublicServiceProfile)getActivity().getIntent().getParcelableExtra("arg_public_account_info"));
/*     */     }
/*     */ 
/*  56 */     if (uri != null)
/*  57 */       if (this.mPublicAccountInfo == null) {
/*  58 */         String typeStr = !TextUtils.isEmpty(uri.getLastPathSegment()) ? uri.getLastPathSegment().toUpperCase() : "";
/*  59 */         this.mConversationType = Conversation.ConversationType.valueOf(typeStr);
/*  60 */         this.mTargetId = uri.getQueryParameter("targetId");
/*  61 */         this.name = uri.getQueryParameter("name");
/*     */       } else {
/*  63 */         this.mConversationType = this.mPublicAccountInfo.getConversationType();
/*  64 */         this.mTargetId = this.mPublicAccountInfo.getTargetId();
/*  65 */         this.name = this.mPublicAccountInfo.getName();
/*     */       }
/*     */   }
/*     */ 
/*     */   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
/*     */   {
/*  74 */     View view = inflater.inflate(R.layout.rc_fr_public_service_inf, container, false);
/*     */ 
/*  76 */     this.mPortraitIV = ((AsyncImageView)view.findViewById(R.id.portrait));
/*  77 */     this.mNameTV = ((TextView)view.findViewById(R.id.name));
/*  78 */     this.mAccountTV = ((TextView)view.findViewById(R.id.account));
/*  79 */     this.mDescriptionTV = ((TextView)view.findViewById(R.id.description));
/*  80 */     this.mEnterBtn = ((Button)view.findViewById(R.id.enter));
/*  81 */     this.mFollowBtn = ((Button)view.findViewById(R.id.follow));
/*  82 */     this.mUnfollowBtn = ((Button)view.findViewById(R.id.unfollow));
/*     */ 
/*  84 */     return view;
/*     */   }
/*     */ 
/*     */   public void onViewCreated(View view, Bundle savedInstanceState)
/*     */   {
/*  89 */     super.onViewCreated(view, savedInstanceState);
/*  90 */     this.mLoadingDialogFragment = LoadingDialogFragment.newInstance("", getResources().getString(R.string.rc_notice_data_is_loading));
/*     */ 
/*  92 */     if (this.mPublicAccountInfo != null) {
/*  93 */       initData(this.mPublicAccountInfo);
/*     */     }
/*  95 */     else if (!TextUtils.isEmpty(this.mTargetId)) {
/*  96 */       Conversation.PublicServiceType publicServiceType = null;
/*  97 */       if (this.mConversationType == Conversation.ConversationType.APP_PUBLIC_SERVICE)
/*  98 */         publicServiceType = Conversation.PublicServiceType.APP_PUBLIC_SERVICE;
/*  99 */       else if (this.mConversationType == Conversation.ConversationType.PUBLIC_SERVICE)
/* 100 */         publicServiceType = Conversation.PublicServiceType.PUBLIC_SERVICE;
/*     */       else {
/* 102 */         System.err.print("the public service type is error!!");
/*     */       }
/* 104 */       RongIM.getInstance().getPublicServiceProfile(publicServiceType, this.mTargetId, new RongIMClient.ResultCallback()
/*     */       {
/*     */         public void onSuccess(PublicServiceProfile info) {
/* 107 */           if (info != null) {
/* 108 */             PublicServiceProfileFragment.this.initData(info);
/* 109 */             RongUserInfoManager.getInstance().setPublicServiceProfile(info);
/*     */           }
/*     */         }
/*     */ 
/*     */         public void onError(RongIMClient.ErrorCode e)
/*     */         {
/* 115 */           RLog.e("PublicServiceProfileFragment", "Failure to get data!!!");
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initData(PublicServiceProfile info)
/*     */   {
/* 127 */     if (info != null)
/*     */     {
/* 129 */       this.mPortraitIV.setResource(info.getPortraitUri());
/* 130 */       this.mNameTV.setText(info.getName());
/* 131 */       this.mAccountTV.setText(String.format(getResources().getString(R.string.rc_pub_service_info_account), new Object[] { info.getTargetId() }));
/* 132 */       this.mDescriptionTV.setText(info.getIntroduction());
/*     */ 
/* 134 */       boolean isFollow = info.isFollow();
/* 135 */       boolean isGlobal = info.isGlobal();
/*     */ 
/* 137 */       if (isGlobal) {
/* 138 */         FragmentTransaction ft = getFragmentManager().beginTransaction();
/* 139 */         ft.add(R.id.rc_layout, SetConversationNotificationFragment.newInstance());
/* 140 */         ft.commitAllowingStateLoss();
/*     */ 
/* 142 */         this.mFollowBtn.setVisibility(8);
/* 143 */         this.mEnterBtn.setVisibility(0);
/* 144 */         this.mUnfollowBtn.setVisibility(8);
/*     */       }
/* 147 */       else if (isFollow) {
/* 148 */         FragmentTransaction ft = getFragmentManager().beginTransaction();
/* 149 */         ft.add(R.id.rc_layout, SetConversationNotificationFragment.newInstance());
/* 150 */         ft.commitAllowingStateLoss();
/*     */ 
/* 152 */         this.mFollowBtn.setVisibility(8);
/* 153 */         this.mEnterBtn.setVisibility(0);
/* 154 */         this.mUnfollowBtn.setVisibility(0);
/*     */       } else {
/* 156 */         this.mFollowBtn.setVisibility(0);
/* 157 */         this.mEnterBtn.setVisibility(8);
/* 158 */         this.mUnfollowBtn.setVisibility(8);
/*     */       }
/*     */ 
/* 163 */       this.mEnterBtn.setOnClickListener(new View.OnClickListener(info)
/*     */       {
/*     */         public void onClick(View v) {
/* 166 */           RongIM.PublicServiceBehaviorListener listener = RongContext.getInstance().getPublicServiceBehaviorListener();
/* 167 */           if ((listener != null) && (listener.onEnterConversationClick(v.getContext(), this.val$info))) {
/* 168 */             return;
/*     */           }
/* 170 */           PublicServiceProfileFragment.this.getActivity().finish();
/* 171 */           RongIM.getInstance().startConversation(PublicServiceProfileFragment.this.getActivity(), this.val$info.getConversationType(), this.val$info.getTargetId(), this.val$info.getName());
/*     */         }
/*     */       });
/* 176 */       this.mFollowBtn.setOnClickListener(new View.OnClickListener(info)
/*     */       {
/*     */         public void onClick(View v) {
/* 179 */           Conversation.PublicServiceType publicServiceType = null;
/* 180 */           if (PublicServiceProfileFragment.this.mConversationType == Conversation.ConversationType.APP_PUBLIC_SERVICE)
/* 181 */             publicServiceType = Conversation.PublicServiceType.APP_PUBLIC_SERVICE;
/* 182 */           else if (PublicServiceProfileFragment.this.mConversationType == Conversation.ConversationType.PUBLIC_SERVICE)
/* 183 */             publicServiceType = Conversation.PublicServiceType.PUBLIC_SERVICE;
/*     */           else {
/* 185 */             System.err.print("the public service type is error!!");
/*     */           }
/* 187 */           RongIM.getInstance().subscribePublicService(publicServiceType, this.val$info.getTargetId(), new RongIMClient.OperationCallback(v)
/*     */           {
/*     */             public void onSuccess() {
/* 190 */               PublicServiceProfileFragment.this.mLoadingDialogFragment.dismiss();
/* 191 */               PublicServiceProfileFragment.this.mFollowBtn.setVisibility(8);
/* 192 */               PublicServiceProfileFragment.this.mEnterBtn.setVisibility(0);
/* 193 */               PublicServiceProfileFragment.this.mUnfollowBtn.setVisibility(0);
/*     */ 
/* 195 */               RongUserInfoManager.getInstance().setPublicServiceProfile(PublicServiceProfileFragment.3.this.val$info);
/* 196 */               RongContext.getInstance().getEventBus().post(Event.PublicServiceFollowableEvent.obtain(PublicServiceProfileFragment.3.this.val$info.getTargetId(), PublicServiceProfileFragment.3.this.val$info.getConversationType(), true));
/* 197 */               RongIM.PublicServiceBehaviorListener listener = RongContext.getInstance().getPublicServiceBehaviorListener();
/* 198 */               if ((listener != null) && (listener.onFollowClick(this.val$v.getContext(), PublicServiceProfileFragment.3.this.val$info) == true)) {
/* 199 */                 return;
/*     */               }
/* 201 */               PublicServiceProfileFragment.this.getActivity().finish();
/* 202 */               RongIM.getInstance().startConversation(PublicServiceProfileFragment.this.getActivity(), PublicServiceProfileFragment.3.this.val$info.getConversationType(), PublicServiceProfileFragment.3.this.val$info.getTargetId(), PublicServiceProfileFragment.3.this.val$info.getName());
/*     */             }
/*     */ 
/*     */             public void onError(RongIMClient.ErrorCode errorCode)
/*     */             {
/* 208 */               PublicServiceProfileFragment.this.mLoadingDialogFragment.dismiss();
/*     */             }
/*     */           });
/* 211 */           PublicServiceProfileFragment.this.mLoadingDialogFragment.show(PublicServiceProfileFragment.this.getFragmentManager());
/*     */         }
/*     */       });
/* 215 */       this.mUnfollowBtn.setOnClickListener(new View.OnClickListener(info)
/*     */       {
/*     */         public void onClick(View v)
/*     */         {
/* 219 */           Conversation.PublicServiceType publicServiceType = null;
/* 220 */           if (PublicServiceProfileFragment.this.mConversationType == Conversation.ConversationType.APP_PUBLIC_SERVICE)
/* 221 */             publicServiceType = Conversation.PublicServiceType.APP_PUBLIC_SERVICE;
/* 222 */           else if (PublicServiceProfileFragment.this.mConversationType == Conversation.ConversationType.PUBLIC_SERVICE)
/* 223 */             publicServiceType = Conversation.PublicServiceType.PUBLIC_SERVICE;
/*     */           else {
/* 225 */             System.err.print("the public service type is error!!");
/*     */           }
/* 227 */           RongIM.getInstance().unsubscribePublicService(publicServiceType, this.val$info.getTargetId(), new RongIMClient.OperationCallback(v)
/*     */           {
/*     */             public void onSuccess() {
/* 230 */               PublicServiceProfileFragment.this.mFollowBtn.setVisibility(0);
/* 231 */               PublicServiceProfileFragment.this.mEnterBtn.setVisibility(8);
/* 232 */               PublicServiceProfileFragment.this.mUnfollowBtn.setVisibility(8);
/*     */ 
/* 234 */               RongContext.getInstance().getEventBus().post(Event.PublicServiceFollowableEvent.obtain(PublicServiceProfileFragment.4.this.val$info.getTargetId(), PublicServiceProfileFragment.4.this.val$info.getConversationType(), false));
/* 235 */               RongIM.PublicServiceBehaviorListener listener = RongContext.getInstance().getPublicServiceBehaviorListener();
/* 236 */               if ((listener != null) && (listener.onUnFollowClick(this.val$v.getContext(), PublicServiceProfileFragment.4.this.val$info))) {
/* 237 */                 return;
/*     */               }
/* 239 */               PublicServiceProfileFragment.this.getActivity().finish();
/*     */             }
/*     */ 
/*     */             public void onError(RongIMClient.ErrorCode errorCode)
/*     */             {
/*     */             }
/*     */           });
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean handleMessage(Message msg)
/*     */   {
/* 255 */     return false;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.fragment.PublicServiceProfileFragment
 * JD-Core Version:    0.6.0
 */
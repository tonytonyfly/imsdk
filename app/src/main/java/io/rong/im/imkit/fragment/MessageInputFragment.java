/*     */ package io.rong.imkit.fragment;
/*     */ 
/*     */ import android.content.Intent;
/*     */ import android.net.Uri;
/*     */ import android.os.Bundle;
/*     */ import android.os.Handler;
/*     */ import android.os.Message;
/*     */ import android.support.v4.app.Fragment;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.ViewGroup;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.model.ConversationKey;
/*     */ import io.rong.imkit.model.Event.InputViewEvent;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.widget.InputView;
/*     */ import io.rong.imkit.widget.InputView.IInputBoardListener;
/*     */ import io.rong.imkit.widget.InputView.OnInfoButtonClick;
/*     */ import io.rong.imkit.widget.provider.InputProvider;
/*     */ import io.rong.imkit.widget.provider.InputProvider.ExtendProvider;
/*     */ import io.rong.imkit.widget.provider.InputProvider.MainInputProvider;
/*     */ import io.rong.imkit.widget.provider.TextInputProvider;
/*     */ import io.rong.imkit.widget.provider.VoiceInputProvider;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.model.Conversation;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Conversation.PublicServiceType;
/*     */ import io.rong.imlib.model.CustomServiceMode;
/*     */ import io.rong.imlib.model.PublicServiceMenu;
/*     */ import io.rong.imlib.model.PublicServiceProfile;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class MessageInputFragment extends UriFragment
/*     */   implements View.OnClickListener
/*     */ {
/*     */   private static final String TAG = "MessageInputFragment";
/*     */   private static final String IS_SHOW_EXTEND_INPUTS = "isShowExtendInputs";
/*     */   Conversation mConversation;
/*     */   InputView mInput;
/*     */ 
/*     */   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
/*     */   {
/*  43 */     View view = inflater.inflate(R.layout.rc_fr_messageinput, container, false);
/*  44 */     this.mInput = ((InputView)view.findViewById(R.id.rc_input));
/*  45 */     EventBus.getDefault().register(this);
/*  46 */     return view;
/*     */   }
/*     */ 
/*     */   public void onViewCreated(View view, Bundle savedInstanceState)
/*     */   {
/*  51 */     super.onViewCreated(view, savedInstanceState);
/*     */ 
/*  53 */     if (RongContext.getInstance().getPrimaryInputProvider() == null) {
/*  54 */       throw new RuntimeException("MainInputProvider must not be null.");
/*     */     }
/*     */ 
/*  57 */     if (getUri() != null)
/*     */     {
/*  59 */       String isShowExtendInputs = getUri().getQueryParameter("isShowExtendInputs");
/*     */ 
/*  61 */       if ((isShowExtendInputs != null) && (("true".equals(isShowExtendInputs)) || ("1".equals(isShowExtendInputs)))) {
/*  62 */         getHandler().postDelayed(new Runnable()
/*     */         {
/*     */           public void run() {
/*  65 */             MessageInputFragment.this.mInput.setExtendInputsVisibility(0);
/*     */           }
/*     */         }
/*     */         , 500L);
/*     */       }
/*     */       else
/*     */       {
/*  70 */         getHandler().postDelayed(new Runnable()
/*     */         {
/*     */           public void run() {
/*  73 */             MessageInputFragment.this.mInput.setExtendInputsVisibility(8);
/*     */           }
/*     */         }
/*     */         , 500L);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onPause()
/*     */   {
/*  83 */     super.onPause();
/*  84 */     if ((RongContext.getInstance() != null) && (RongContext.getInstance().getPrimaryInputProvider() != null))
/*  85 */       RongContext.getInstance().getPrimaryInputProvider().onInputPause();
/*     */   }
/*     */ 
/*     */   public void onResume()
/*     */   {
/*  90 */     super.onResume();
/*  91 */     if (RongContext.getInstance() != null) {
/*  92 */       if (RongContext.getInstance().getPrimaryInputProvider() != null) {
/*  93 */         RongContext.getInstance().getPrimaryInputProvider().onInputResume(this.mInput, this.mConversation);
/*     */       }
/*  95 */       if (RongContext.getInstance().getVoiceInputProvider() != null)
/*  96 */         RongContext.getInstance().getVoiceInputProvider().onInputResume(this.mInput, this.mConversation);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setOnInfoButtonClick(InputView.OnInfoButtonClick onInfoButtonClick)
/*     */   {
/* 102 */     this.mInput.setOnInfoButtonClickListener(onInfoButtonClick);
/*     */   }
/*     */ 
/*     */   public void setInputBoardListener(InputView.IInputBoardListener inputBoardListener) {
/* 106 */     this.mInput.setInputBoardListener(inputBoardListener);
/*     */   }
/*     */ 
/*     */   private void setCurrentConversation(Conversation conversation)
/*     */   {
/* 111 */     RongContext.getInstance().getPrimaryInputProvider().setCurrentConversation(conversation);
/*     */ 
/* 113 */     if (RongContext.getInstance().getSecondaryInputProvider() != null) {
/* 114 */       RongContext.getInstance().getSecondaryInputProvider().setCurrentConversation(conversation);
/*     */     }
/*     */ 
/* 117 */     if (RongContext.getInstance().getMenuInputProvider() != null) {
/* 118 */       RongContext.getInstance().getMenuInputProvider().setCurrentConversation(conversation);
/*     */     }
/*     */ 
/* 121 */     for (InputProvider provider : RongContext.getInstance().getRegisteredExtendProviderList(this.mConversation.getConversationType())) {
/* 122 */       provider.setCurrentConversation(conversation);
/*     */     }
/*     */ 
/* 125 */     this.mInput.setExtendProvider(RongContext.getInstance().getRegisteredExtendProviderList(this.mConversation.getConversationType()), this.mConversation.getConversationType());
/*     */ 
/* 127 */     for (InputProvider provider : RongContext.getInstance().getRegisteredExtendProviderList(this.mConversation.getConversationType())) {
/* 128 */       provider.onAttached(this, this.mInput);
/*     */     }
/*     */ 
/* 131 */     if ((conversation.getConversationType().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)) || (conversation.getConversationType().equals(Conversation.ConversationType.PUBLIC_SERVICE)))
/*     */     {
/* 134 */       ConversationKey key = ConversationKey.obtain(conversation.getTargetId(), conversation.getConversationType());
/* 135 */       PublicServiceProfile info = RongContext.getInstance().getPublicServiceInfoFromCache(key.getKey());
/* 136 */       if (info == null) {
/* 137 */         Conversation.PublicServiceType type = Conversation.PublicServiceType.setValue(conversation.getConversationType().getValue());
/* 138 */         RongIM.getInstance().getPublicServiceProfile(type, conversation.getTargetId(), new RongIMClient.ResultCallback()
/*     */         {
/*     */           public void onSuccess(PublicServiceProfile publicServiceProfile) {
/* 141 */             RongUserInfoManager.getInstance().setPublicServiceProfile(publicServiceProfile);
/* 142 */             PublicServiceMenu menu = publicServiceProfile.getMenu();
/* 143 */             if ((menu != null) && (menu.getMenuItems() != null) && (menu.getMenuItems().size() > 0)) {
/* 144 */               MessageInputFragment.this.mInput.setInputProviderEx(RongContext.getInstance().getPrimaryInputProvider(), RongContext.getInstance().getSecondaryInputProvider(), RongContext.getInstance().getMenuInputProvider());
/*     */             }
/*     */             else
/*     */             {
/* 148 */               MessageInputFragment.this.mInput.setInputProvider(RongContext.getInstance().getPrimaryInputProvider(), RongContext.getInstance().getSecondaryInputProvider());
/*     */             }
/*     */           }
/*     */ 
/*     */           public void onError(RongIMClient.ErrorCode e)
/*     */           {
/* 155 */             MessageInputFragment.this.mInput.setInputProvider(RongContext.getInstance().getPrimaryInputProvider(), RongContext.getInstance().getSecondaryInputProvider());
/*     */           } } );
/*     */       }
/*     */       else {
/* 160 */         PublicServiceMenu menu = info.getMenu();
/* 161 */         if ((menu != null) && (menu.getMenuItems() != null) && (menu.getMenuItems().size() > 0)) {
/* 162 */           this.mInput.setInputProviderEx(RongContext.getInstance().getPrimaryInputProvider(), RongContext.getInstance().getSecondaryInputProvider(), RongContext.getInstance().getMenuInputProvider());
/*     */         }
/*     */         else
/*     */         {
/* 166 */           this.mInput.setInputProvider(RongContext.getInstance().getPrimaryInputProvider(), RongContext.getInstance().getSecondaryInputProvider());
/*     */         }
/*     */       }
/*     */     }
/* 170 */     else if (conversation.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
/* 171 */       this.mInput.setInputProviderForCS(RongContext.getInstance().getPrimaryInputProvider(), RongContext.getInstance().getSecondaryInputProvider());
/*     */     }
/*     */     else {
/* 174 */       this.mInput.setInputProvider(RongContext.getInstance().getPrimaryInputProvider(), RongContext.getInstance().getSecondaryInputProvider());
/*     */     }
/*     */ 
/* 178 */     RongContext.getInstance().getPrimaryInputProvider().onAttached(this, this.mInput);
/*     */ 
/* 180 */     if (RongContext.getInstance().getSecondaryInputProvider() != null)
/* 181 */       RongContext.getInstance().getSecondaryInputProvider().onAttached(this, this.mInput);
/*     */   }
/*     */ 
/*     */   public void setInputProviderType(CustomServiceMode type)
/*     */   {
/* 186 */     switch (4.$SwitchMap$io$rong$imlib$model$CustomServiceMode[type.ordinal()]) {
/*     */     case 1:
/* 188 */       this.mInput.setOnlyRobotInputType();
/* 189 */       break;
/*     */     case 2:
/* 191 */       this.mInput.setPriorRobotInputType();
/* 192 */       break;
/*     */     case 3:
/*     */     case 4:
/* 195 */       this.mInput.setOnlyAdminInputType();
/* 196 */       break;
/*     */     case 5:
/* 198 */       this.mInput.setNoServiceType();
/* 199 */       break;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setOnRobotSwitcherListener(View.OnClickListener listener)
/*     */   {
/* 206 */     this.mInput.setOnSwitcherListener(listener);
/*     */   }
/*     */ 
/*     */   protected void initFragment(Uri uri)
/*     */   {
/* 211 */     String typeStr = uri.getLastPathSegment().toUpperCase();
/* 212 */     Conversation.ConversationType type = Conversation.ConversationType.valueOf(typeStr);
/*     */ 
/* 214 */     String targetId = uri.getQueryParameter("targetId");
/*     */ 
/* 216 */     String title = uri.getQueryParameter("title");
/*     */ 
/* 218 */     if (type == null) {
/* 219 */       return;
/*     */     }
/* 221 */     this.mConversation = Conversation.obtain(type, targetId, title);
/*     */ 
/* 223 */     if (this.mConversation != null)
/* 224 */       setCurrentConversation(this.mConversation);
/*     */   }
/*     */ 
/*     */   public TextInputProvider getMentionInputProvider()
/*     */   {
/* 230 */     InputProvider.MainInputProvider provider = RongContext.getInstance().getPrimaryInputProvider();
/* 231 */     return (TextInputProvider)provider;
/*     */   }
/*     */ 
/*     */   public void onClick(View v)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onDestroyView()
/*     */   {
/* 241 */     RLog.d("MessageInputFragment", "onDestroyView the primary input provider is:" + RongContext.getInstance().getPrimaryInputProvider());
/*     */ 
/* 243 */     RongContext.getInstance().getPrimaryInputProvider().onDetached();
/*     */ 
/* 245 */     if (RongContext.getInstance().getSecondaryInputProvider() != null) {
/* 246 */       RongContext.getInstance().getSecondaryInputProvider().onDetached();
/*     */     }
/*     */ 
/* 249 */     EventBus.getDefault().unregister(this);
/*     */ 
/* 251 */     super.onDestroyView();
/*     */   }
/*     */ 
/*     */   public boolean onBackPressed()
/*     */   {
/* 256 */     return false;
/*     */   }
/*     */ 
/*     */   private DispatchResultFragment getDispatchFragment(Fragment fragment)
/*     */   {
/* 261 */     if ((fragment instanceof DispatchResultFragment)) {
/* 262 */       return (DispatchResultFragment)fragment;
/*     */     }
/* 264 */     if (fragment.getParentFragment() == null) {
/* 265 */       throw new RuntimeException(fragment.getClass().getName() + " must has a parent fragment instance of DispatchFragment.");
/*     */     }
/* 267 */     return getDispatchFragment(fragment.getParentFragment());
/*     */   }
/*     */ 
/*     */   public boolean handleMessage(Message msg)
/*     */   {
/* 272 */     return false;
/*     */   }
/*     */ 
/*     */   public void startActivityFromProvider(InputProvider provider, Intent intent, int requestCode) {
/* 276 */     if (requestCode == -1) {
/* 277 */       startActivityForResult(intent, -1);
/* 278 */       return;
/*     */     }
/* 280 */     if ((requestCode & 0xFFFFFF80) != 0) {
/* 281 */       throw new IllegalArgumentException("Can only use lower 7 bits for requestCode");
/*     */     }
/*     */ 
/* 284 */     getDispatchFragment(this).startActivityForResult(this, intent, (provider.getIndex() + 1 << 7) + (requestCode & 0x7F));
/*     */   }
/*     */ 
/*     */   public void onActivityResult(int requestCode, int resultCode, Intent data)
/*     */   {
/* 291 */     int index = requestCode >> 7;
/* 292 */     if (index != 0) {
/* 293 */       index--;
/* 294 */       if (index > RongContext.getInstance().getRegisteredExtendProviderList(this.mConversation.getConversationType()).size() + 1) {
/* 295 */         RLog.w("MessageInputFragment", "onActivityResult Activity result provider index out of range: 0x" + Integer.toHexString(requestCode));
/*     */ 
/* 297 */         return;
/*     */       }
/*     */ 
/* 300 */       if (index == 0)
/* 301 */         RongContext.getInstance().getPrimaryInputProvider().onActivityResult(requestCode & 0x7F, resultCode, data);
/* 302 */       else if (index == 1)
/* 303 */         RongContext.getInstance().getSecondaryInputProvider().onActivityResult(requestCode & 0x7F, resultCode, data);
/*     */       else {
/* 305 */         ((InputProvider.ExtendProvider)RongContext.getInstance().getRegisteredExtendProviderList(this.mConversation.getConversationType()).get(index - 2)).onActivityResult(requestCode & 0x7F, resultCode, data);
/*     */       }
/*     */ 
/* 308 */       return;
/*     */     }
/*     */ 
/* 311 */     super.onActivityResult(requestCode, resultCode, data);
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.InputViewEvent event)
/*     */   {
/* 317 */     if (event.isVisibility())
/* 318 */       this.mInput.setExtendInputsVisibility(0);
/*     */     else
/* 320 */       this.mInput.setExtendInputsVisibility(8);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.fragment.MessageInputFragment
 * JD-Core Version:    0.6.0
 */
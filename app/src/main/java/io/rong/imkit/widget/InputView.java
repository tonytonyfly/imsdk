/*     */ package io.rong.imkit.widget;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.res.Resources;
/*     */ import android.content.res.TypedArray;
/*     */ import android.util.AttributeSet;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.ViewGroup;
/*     */ import android.view.animation.Animation;
/*     */ import android.view.animation.AnimationSet;
/*     */ import android.view.animation.TranslateAnimation;
/*     */ import android.widget.FrameLayout;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.LinearLayout;
/*     */ import android.widget.RelativeLayout;
/*     */ import android.widget.RelativeLayout.LayoutParams;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.styleable;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.widget.provider.InputProvider;
/*     */ import io.rong.imkit.widget.provider.InputProvider.ExtendProvider;
/*     */ import io.rong.imkit.widget.provider.InputProvider.MainInputProvider;
/*     */ import io.rong.imkit.widget.provider.VoiceInputProvider;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.CustomServiceMode;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class InputView extends LinearLayout
/*     */ {
/*     */   private static final String TAG = "InputView";
/*     */   volatile InputProvider.MainInputProvider mMainProvider;
/*     */   volatile InputProvider.MainInputProvider mSlaveProvider;
/*     */   volatile InputProvider.MainInputProvider mMenuProvider;
/*     */   List<InputProvider> mProviderList;
/*     */   int mStyle;
/*     */   RelativeLayout mInputLayout;
/*     */   LinearLayout mSwitcherLayout;
/*     */   LinearLayout mCustomMenuLayout;
/*     */   ImageView mMenuSwitcher1;
/*     */   ImageView mMenuSwitcher2;
/*     */   LinearLayout mInputMenuLayout;
/*     */   LinearLayout mInputMenuSwitchLayout;
/*     */   FrameLayout mCustomLayout;
/*     */   FrameLayout mWidgetLayout;
/*     */   FrameLayout mExtendLayout;
/*     */   FrameLayout mToggleLayout;
/*     */   ImageView mIcon1;
/*     */   ImageView mIcon2;
/*     */   LinearLayout mPluginsLayout;
/*     */   View mView;
/*     */   int left;
/*     */   int center;
/*     */   int right;
/*     */   private OnInfoButtonClick onInfoButtonClick;
/*     */   private CustomServiceMode currentType;
/*     */   private View.OnClickListener switcherListener;
/* 714 */   boolean collapsed = true;
/* 715 */   int originalTop = 0;
/* 716 */   int originalBottom = 0;
/*     */   IInputBoardListener inputBoardListener;
/*     */ 
/*     */   public InputView(Context context, AttributeSet attrs)
/*     */   {
/*  80 */     super(context, attrs);
/*  81 */     setOrientation(1);
/*  82 */     View view = inflate(context, R.layout.rc_wi_input, this);
/*  83 */     this.mView = view;
/*     */ 
/*  85 */     TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InputView);
/*  86 */     this.mStyle = a.getInt(R.styleable.InputView_RCStyle, 291);
/*  87 */     a.recycle();
/*     */ 
/*  89 */     this.mProviderList = new ArrayList();
/*     */ 
/*  91 */     this.mSwitcherLayout = ((LinearLayout)view.findViewById(R.id.rc_switcher));
/*  92 */     this.mInputMenuSwitchLayout = ((LinearLayout)view.findViewById(R.id.rc_menu_switch));
/*  93 */     this.mMenuSwitcher1 = ((ImageView)view.findViewById(R.id.rc_switcher1));
/*  94 */     this.mMenuSwitcher2 = ((ImageView)view.findViewById(R.id.rc_switcher2));
/*  95 */     this.mInputMenuLayout = ((LinearLayout)view.findViewById(R.id.rc_input_menu));
/*  96 */     this.mInputLayout = ((RelativeLayout)view.findViewById(16908297));
/*  97 */     this.mCustomLayout = ((FrameLayout)view.findViewById(16908331));
/*  98 */     this.mWidgetLayout = ((FrameLayout)view.findViewById(16908312));
/*  99 */     this.mExtendLayout = ((FrameLayout)view.findViewById(R.id.rc_ext));
/* 100 */     this.mToggleLayout = ((FrameLayout)view.findViewById(16908311));
/*     */ 
/* 102 */     this.mCustomMenuLayout = ((LinearLayout)view.findViewById(R.id.rc_input_custom_menu));
/*     */ 
/* 104 */     this.mIcon1 = ((ImageView)view.findViewById(16908295));
/* 105 */     this.mIcon2 = ((ImageView)view.findViewById(16908296));
/*     */ 
/* 107 */     this.mPluginsLayout = ((LinearLayout)view.findViewById(R.id.rc_plugins));
/*     */ 
/* 109 */     this.left = ((this.mStyle >> 8) % 16);
/* 110 */     this.center = ((this.mStyle >> 4) % 16);
/* 111 */     this.right = (this.mStyle % 16);
/*     */ 
/* 113 */     this.mIcon2.setImageDrawable(getResources().getDrawable(R.drawable.rc_ic_extend));
/* 114 */     this.mIcon2.setOnClickListener(new ExtendClickListener());
/*     */   }
/*     */ 
/*     */   public void setOnInfoButtonClickListener(OnInfoButtonClick clickListener)
/*     */   {
/* 140 */     this.onInfoButtonClick = clickListener;
/*     */   }
/*     */ 
/*     */   public void setExtendInputsVisibility(int visibility) {
/* 144 */     onProviderInactive(getContext());
/* 145 */     setPluginsLayoutVisibility(visibility);
/*     */   }
/*     */ 
/*     */   public void setPluginsLayoutVisibility(int visibility) {
/* 149 */     this.mPluginsLayout.setVisibility(visibility);
/*     */   }
/*     */ 
/*     */   public void setExtendLayoutVisibility(int visibility) {
/* 153 */     this.mExtendLayout.setVisibility(visibility);
/*     */   }
/*     */ 
/*     */   public void setWidgetLayoutVisibility(int visibility) {
/* 157 */     this.mWidgetLayout.setVisibility(visibility);
/*     */   }
/*     */ 
/*     */   private final void changeMainProvider(View view, InputProvider.MainInputProvider main, InputProvider.MainInputProvider slave) {
/* 161 */     this.mMainProvider.onSwitch(view.getContext());
/*     */ 
/* 163 */     this.mPluginsLayout.setVisibility(8);
/* 164 */     this.mExtendLayout.setVisibility(8);
/*     */ 
/* 166 */     setInputProvider(this.mSlaveProvider, this.mMainProvider);
/*     */   }
/*     */ 
/*     */   private void setSCE() {
/* 170 */     RelativeLayout.LayoutParams leftParams = new RelativeLayout.LayoutParams(-2, -2);
/*     */ 
/* 172 */     leftParams.addRule(15, -1);
/* 173 */     leftParams.addRule(9, -1);
/*     */ 
/* 175 */     this.mSwitcherLayout.setLayoutParams(leftParams);
/*     */ 
/* 177 */     RelativeLayout.LayoutParams rightParams = new RelativeLayout.LayoutParams(-2, -2);
/*     */ 
/* 179 */     rightParams.addRule(15, -1);
/* 180 */     rightParams.addRule(11, -1);
/*     */ 
/* 182 */     this.mToggleLayout.setLayoutParams(rightParams);
/* 183 */     this.mToggleLayout.setVisibility(0);
/*     */ 
/* 185 */     RelativeLayout.LayoutParams centerParams = new RelativeLayout.LayoutParams(-1, -2);
/*     */ 
/* 187 */     centerParams.addRule(15, -1);
/* 188 */     centerParams.addRule(0, this.mToggleLayout.getId());
/* 189 */     centerParams.addRule(1, this.mSwitcherLayout.getId());
/*     */ 
/* 191 */     this.mCustomLayout.setLayoutParams(centerParams);
/*     */   }
/*     */ 
/*     */   private void setECS()
/*     */   {
/* 196 */     RelativeLayout.LayoutParams leftParams = new RelativeLayout.LayoutParams(-2, -2);
/*     */ 
/* 198 */     leftParams.addRule(15, -1);
/* 199 */     leftParams.addRule(9, -1);
/*     */ 
/* 201 */     this.mToggleLayout.setLayoutParams(leftParams);
/*     */ 
/* 203 */     RelativeLayout.LayoutParams rightParams = new RelativeLayout.LayoutParams(-2, -2);
/*     */ 
/* 205 */     rightParams.addRule(15, -1);
/* 206 */     rightParams.addRule(11, -1);
/*     */ 
/* 208 */     this.mSwitcherLayout.setLayoutParams(rightParams);
/*     */ 
/* 210 */     RelativeLayout.LayoutParams centerParams = new RelativeLayout.LayoutParams(-1, -2);
/*     */ 
/* 212 */     centerParams.addRule(15, -1);
/* 213 */     centerParams.addRule(0, this.mSwitcherLayout.getId());
/* 214 */     centerParams.addRule(1, this.mToggleLayout.getId());
/*     */ 
/* 216 */     this.mCustomLayout.setLayoutParams(centerParams);
/*     */   }
/*     */ 
/*     */   private void setCES() {
/* 220 */     RelativeLayout.LayoutParams rightParams = new RelativeLayout.LayoutParams(-2, -2);
/*     */ 
/* 222 */     rightParams.addRule(15, -1);
/* 223 */     rightParams.addRule(11, -1);
/*     */ 
/* 225 */     this.mSwitcherLayout.setLayoutParams(rightParams);
/*     */ 
/* 227 */     RelativeLayout.LayoutParams centerParams = new RelativeLayout.LayoutParams(-2, -2);
/*     */ 
/* 229 */     centerParams.addRule(15, -1);
/* 230 */     centerParams.addRule(0, this.mSwitcherLayout.getId());
/*     */ 
/* 232 */     this.mToggleLayout.setLayoutParams(centerParams);
/*     */ 
/* 235 */     RelativeLayout.LayoutParams leftParams = new RelativeLayout.LayoutParams(-1, -2);
/*     */ 
/* 237 */     leftParams.addRule(15, -1);
/* 238 */     leftParams.addRule(0, this.mToggleLayout.getId());
/*     */ 
/* 240 */     this.mCustomLayout.setLayoutParams(leftParams);
/*     */   }
/*     */ 
/*     */   private void setCSE()
/*     */   {
/* 245 */     RelativeLayout.LayoutParams rightParams = new RelativeLayout.LayoutParams(-2, -2);
/*     */ 
/* 247 */     rightParams.addRule(15, -1);
/* 248 */     rightParams.addRule(11, -1);
/*     */ 
/* 250 */     this.mToggleLayout.setLayoutParams(rightParams);
/*     */ 
/* 252 */     RelativeLayout.LayoutParams centerParams = new RelativeLayout.LayoutParams(-2, -2);
/*     */ 
/* 254 */     centerParams.addRule(15, -1);
/* 255 */     centerParams.addRule(0, this.mToggleLayout.getId());
/*     */ 
/* 257 */     this.mSwitcherLayout.setLayoutParams(centerParams);
/*     */ 
/* 260 */     RelativeLayout.LayoutParams leftParams = new RelativeLayout.LayoutParams(-1, -2);
/*     */ 
/* 262 */     leftParams.addRule(15, -1);
/* 263 */     leftParams.addRule(0, this.mSwitcherLayout.getId());
/*     */ 
/* 265 */     this.mCustomLayout.setLayoutParams(leftParams);
/*     */   }
/*     */ 
/*     */   private void setSC() {
/* 269 */     RelativeLayout.LayoutParams leftParams = new RelativeLayout.LayoutParams(-2, -2);
/*     */ 
/* 271 */     leftParams.addRule(15, -1);
/* 272 */     leftParams.addRule(9, -1);
/*     */ 
/* 274 */     this.mSwitcherLayout.setLayoutParams(leftParams);
/*     */ 
/* 276 */     this.mToggleLayout.setVisibility(8);
/*     */ 
/* 278 */     RelativeLayout.LayoutParams centerParams = new RelativeLayout.LayoutParams(-1, -2);
/*     */ 
/* 280 */     centerParams.addRule(15, -1);
/* 281 */     centerParams.addRule(0, this.mToggleLayout.getId());
/* 282 */     centerParams.addRule(1, this.mSwitcherLayout.getId());
/*     */ 
/* 284 */     this.mCustomLayout.setLayoutParams(centerParams);
/*     */   }
/*     */ 
/*     */   private void setCS() {
/* 288 */     RelativeLayout.LayoutParams rightParams = new RelativeLayout.LayoutParams(-2, -2);
/*     */ 
/* 290 */     rightParams.addRule(15, -1);
/* 291 */     rightParams.addRule(11, -1);
/*     */ 
/* 293 */     this.mSwitcherLayout.setLayoutParams(rightParams);
/*     */ 
/* 295 */     this.mToggleLayout.setVisibility(8);
/*     */ 
/* 298 */     RelativeLayout.LayoutParams centerParams = new RelativeLayout.LayoutParams(-1, -2);
/*     */ 
/* 300 */     centerParams.addRule(15, -1);
/* 301 */     centerParams.addRule(1, this.mToggleLayout.getId());
/* 302 */     centerParams.addRule(0, this.mSwitcherLayout.getId());
/*     */ 
/* 304 */     this.mCustomLayout.setLayoutParams(centerParams);
/*     */   }
/*     */ 
/*     */   private void setEC() {
/* 308 */     RelativeLayout.LayoutParams leftParams = new RelativeLayout.LayoutParams(-2, -2);
/*     */ 
/* 310 */     leftParams.addRule(15, -1);
/* 311 */     leftParams.addRule(9, -1);
/*     */ 
/* 313 */     this.mToggleLayout.setLayoutParams(leftParams);
/*     */ 
/* 315 */     this.mSwitcherLayout.setVisibility(8);
/*     */ 
/* 317 */     RelativeLayout.LayoutParams centerParams = new RelativeLayout.LayoutParams(-1, -2);
/*     */ 
/* 319 */     centerParams.addRule(15, -1);
/* 320 */     centerParams.addRule(1, this.mToggleLayout.getId());
/*     */ 
/* 322 */     this.mCustomLayout.setLayoutParams(centerParams);
/*     */   }
/*     */ 
/*     */   private void setCE() {
/* 326 */     RelativeLayout.LayoutParams rightParams = new RelativeLayout.LayoutParams(-2, -2);
/*     */ 
/* 328 */     rightParams.addRule(15, -1);
/* 329 */     rightParams.addRule(11, -1);
/*     */ 
/* 331 */     this.mToggleLayout.setVisibility(0);
/* 332 */     this.mToggleLayout.setLayoutParams(rightParams);
/*     */ 
/* 334 */     this.mSwitcherLayout.setVisibility(8);
/*     */ 
/* 336 */     RelativeLayout.LayoutParams centerParams = new RelativeLayout.LayoutParams(-1, -2);
/*     */ 
/* 338 */     centerParams.addRule(15, -1);
/* 339 */     centerParams.addRule(0, this.mToggleLayout.getId());
/*     */ 
/* 341 */     this.mCustomLayout.setLayoutParams(centerParams);
/*     */   }
/*     */ 
/*     */   public void setOnlyRobotInputType()
/*     */   {
/* 346 */     this.currentType = CustomServiceMode.CUSTOM_SERVICE_MODE_ROBOT;
/* 347 */     setC();
/*     */   }
/*     */ 
/*     */   public void setPriorRobotInputType() {
/* 351 */     if ((this.currentType == null) || (this.currentType != CustomServiceMode.CUSTOM_SERVICE_MODE_ROBOT_FIRST)) {
/* 352 */       this.currentType = CustomServiceMode.CUSTOM_SERVICE_MODE_ROBOT_FIRST;
/* 353 */       this.mIcon1.setImageResource(R.drawable.rc_ic_admin_selector);
/* 354 */       this.mIcon1.setOnClickListener(new View.OnClickListener()
/*     */       {
/*     */         public void onClick(View v) {
/* 357 */           if (InputView.this.switcherListener != null)
/* 358 */             InputView.this.switcherListener.onClick(v);
/*     */         }
/*     */       });
/* 361 */       this.mIcon1.setVisibility(8);
/* 362 */       setSC();
/* 363 */     } else if (this.currentType == CustomServiceMode.CUSTOM_SERVICE_MODE_ROBOT_FIRST) {
/* 364 */       this.mIcon1.setVisibility(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setOnlyAdminInputType() {
/* 369 */     this.currentType = CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN;
/* 370 */     if (this.mSlaveProvider != null) {
/* 371 */       this.mIcon1.setVisibility(0);
/* 372 */       this.mIcon1.setImageDrawable(this.mSlaveProvider.obtainSwitchDrawable(getContext()));
/* 373 */       this.mIcon1.setOnClickListener(new View.OnClickListener()
/*     */       {
/*     */         public void onClick(View v) {
/* 376 */           InputView.this.changeMainProvider(v, InputView.this.mSlaveProvider, InputView.this.mMainProvider);
/*     */         } } );
/*     */     }
/* 380 */     setSCE();
/*     */   }
/*     */ 
/*     */   public void setNoServiceType() {
/* 384 */     this.currentType = CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE;
/* 385 */     if (this.mSlaveProvider != null) {
/* 386 */       this.mIcon1.setVisibility(0);
/* 387 */       this.mIcon1.setImageDrawable(this.mSlaveProvider.obtainSwitchDrawable(getContext()));
/* 388 */       this.mIcon1.setOnClickListener(new View.OnClickListener()
/*     */       {
/*     */         public void onClick(View v) {
/* 391 */           InputView.this.changeMainProvider(v, InputView.this.mSlaveProvider, InputView.this.mMainProvider);
/*     */         } } );
/*     */     }
/* 395 */     setSCE();
/*     */   }
/*     */ 
/*     */   public void setOnSwitcherListener(View.OnClickListener listener)
/*     */   {
/* 400 */     this.switcherListener = listener;
/*     */   }
/*     */ 
/*     */   private void setC() {
/* 404 */     this.mSwitcherLayout.setVisibility(8);
/* 405 */     this.mToggleLayout.setVisibility(8);
/*     */ 
/* 407 */     RelativeLayout.LayoutParams centerParams = new RelativeLayout.LayoutParams(-1, -2);
/*     */ 
/* 409 */     centerParams.addRule(15, -1);
/*     */ 
/* 411 */     this.mCustomLayout.setLayoutParams(centerParams);
/*     */   }
/*     */ 
/*     */   public void setInputProvider(InputProvider.MainInputProvider mainProvider, InputProvider.MainInputProvider slaveProvider)
/*     */   {
/* 416 */     this.mMainProvider = mainProvider;
/* 417 */     this.mSlaveProvider = slaveProvider;
/*     */ 
/* 419 */     if (this.mMenuProvider == null) {
/* 420 */       this.mInputMenuSwitchLayout.setVisibility(8);
/*     */     }
/* 422 */     this.mCustomLayout.removeAllViews();
/*     */ 
/* 424 */     View leftView = null;
/* 425 */     View rightView = null;
/* 426 */     View centerView = null;
/*     */ 
/* 429 */     switch (this.mStyle)
/*     */     {
/*     */     case 291:
/* 432 */       setSCE();
/* 433 */       break;
/*     */     case 801:
/* 436 */       setECS();
/* 437 */       break;
/*     */     case 561:
/* 440 */       setCES();
/* 441 */       break;
/*     */     case 531:
/* 444 */       setCSE();
/* 445 */       break;
/*     */     case 288:
/* 448 */       setSC();
/* 449 */       break;
/*     */     case 33:
/* 452 */       setCS();
/* 453 */       break;
/*     */     case 800:
/* 456 */       setEC();
/* 457 */       break;
/*     */     case 35:
/* 460 */       setCE();
/* 461 */       break;
/*     */     case 32:
/* 464 */       setC();
/*     */     }
/*     */ 
/* 468 */     if (this.mSlaveProvider != null) {
/* 469 */       this.mIcon1.setImageDrawable(this.mSlaveProvider.obtainSwitchDrawable(getContext()));
/* 470 */       this.mIcon1.setOnClickListener(new View.OnClickListener()
/*     */       {
/*     */         public void onClick(View v) {
/* 473 */           if ((InputView.this.currentType != null) && (InputView.this.currentType.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_ROBOT_FIRST))) {
/* 474 */             if (InputView.this.switcherListener != null)
/* 475 */               InputView.this.switcherListener.onClick(v);
/*     */           }
/* 477 */           else if (InputView.this.onInfoButtonClick != null)
/* 478 */             InputView.this.onInfoButtonClick.onClick(v);
/*     */           else {
/* 480 */             InputView.this.changeMainProvider(v, InputView.this.mSlaveProvider, InputView.this.mMainProvider);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/* 486 */     this.mMainProvider.onCreateView(LayoutInflater.from(getContext()), this.mCustomLayout, this);
/*     */   }
/*     */ 
/*     */   public void setInputProviderForCS(InputProvider.MainInputProvider mainProvider, InputProvider.MainInputProvider slaveProvider) {
/* 490 */     this.mMainProvider = mainProvider;
/* 491 */     this.mSlaveProvider = slaveProvider;
/*     */ 
/* 493 */     if (this.mMenuProvider == null) {
/* 494 */       this.mInputMenuSwitchLayout.setVisibility(8);
/*     */     }
/* 496 */     this.mCustomLayout.removeAllViews();
/*     */ 
/* 498 */     setPriorRobotInputType();
/*     */ 
/* 500 */     if (this.mSlaveProvider != null) {
/* 501 */       this.mIcon1.setImageResource(R.drawable.rc_ic_admin_selector);
/* 502 */       this.mIcon1.setOnClickListener(new View.OnClickListener()
/*     */       {
/*     */         public void onClick(View v) {
/* 505 */           if ((InputView.this.currentType != null) && (InputView.this.currentType.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_ROBOT_FIRST))) {
/* 506 */             if (InputView.this.switcherListener != null)
/* 507 */               InputView.this.switcherListener.onClick(v);
/*     */           }
/* 509 */           else if (InputView.this.onInfoButtonClick != null)
/* 510 */             InputView.this.onInfoButtonClick.onClick(v);
/*     */           else {
/* 512 */             InputView.this.changeMainProvider(v, InputView.this.mSlaveProvider, InputView.this.mMainProvider);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/* 518 */     this.mMainProvider.onCreateView(LayoutInflater.from(getContext()), this.mCustomLayout, this);
/*     */   }
/*     */ 
/*     */   private Animation createPopupAnimIn(Context context) {
/* 522 */     AnimationSet animationSet = new AnimationSet(context, null);
/* 523 */     animationSet.setFillAfter(true);
/*     */ 
/* 525 */     TranslateAnimation translateAnim = new TranslateAnimation(0.0F, 0.0F, 150.0F, 0.0F);
/* 526 */     translateAnim.setDuration(300L);
/* 527 */     animationSet.addAnimation(translateAnim);
/*     */ 
/* 529 */     return animationSet;
/*     */   }
/*     */ 
/*     */   private Animation createPopupAnimOut(Context context) {
/* 533 */     AnimationSet animationSet = new AnimationSet(context, null);
/* 534 */     animationSet.setFillAfter(true);
/*     */ 
/* 536 */     TranslateAnimation translateAnim = new TranslateAnimation(0.0F, 0.0F, 0.0F, 150.0F);
/* 537 */     translateAnim.setDuration(300L);
/* 538 */     animationSet.addAnimation(translateAnim);
/*     */ 
/* 540 */     return animationSet;
/*     */   }
/*     */ 
/*     */   public void setInputProviderEx(InputProvider.MainInputProvider mainProvider, InputProvider.MainInputProvider slaveProvider, InputProvider.MainInputProvider menuProvider)
/*     */   {
/* 546 */     this.mMenuProvider = menuProvider;
/* 547 */     setInputProvider(mainProvider, slaveProvider);
/*     */ 
/* 549 */     if ((menuProvider != null) && (this.mMenuSwitcher1 != null)) {
/* 550 */       this.mInputMenuSwitchLayout.setVisibility(0);
/* 551 */       menuProvider.onCreateView(LayoutInflater.from(getContext()), this.mCustomMenuLayout, this);
/* 552 */       this.mInputMenuSwitchLayout.setOnClickListener(new InputClickListener());
/* 553 */       this.mMenuSwitcher2.setOnClickListener(new InputMenuClickListener());
/*     */ 
/* 555 */       this.mMainProvider.onSwitch(getContext());
/* 556 */       this.mPluginsLayout.setVisibility(8);
/* 557 */       this.mExtendLayout.setVisibility(8);
/* 558 */       this.mInputLayout.setVisibility(8);
/* 559 */       this.mInputMenuLayout.setVisibility(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setExtendProvider(List<InputProvider.ExtendProvider> providers, Conversation.ConversationType conversationType)
/*     */   {
/* 609 */     this.mProviderList.clear();
/* 610 */     for (InputProvider.ExtendProvider provider : providers) {
/* 611 */       this.mProviderList.add(provider);
/*     */     }
/* 613 */     int i = 1;
/* 614 */     for (InputProvider.ExtendProvider provider : providers) {
/* 615 */       i++; provider.setIndex(i);
/*     */     }
/*     */ 
/* 618 */     new RongPluginPager(conversationType, this.mPluginsLayout);
/*     */   }
/*     */ 
/*     */   public ViewGroup getExtendLayout()
/*     */   {
/* 623 */     return this.mExtendLayout;
/*     */   }
/*     */ 
/*     */   public FrameLayout getToggleLayout() {
/* 627 */     return this.mToggleLayout;
/*     */   }
/*     */   public ImageView getIcon1() {
/* 630 */     return this.mIcon1;
/*     */   }
/*     */ 
/*     */   public void onProviderActive(Context context) {
/* 634 */     if (this.mMainProvider != null) {
/* 635 */       this.mMainProvider.onActive(context);
/*     */     }
/* 637 */     if (this.mSlaveProvider != null) {
/* 638 */       this.mSlaveProvider.onActive(context);
/*     */     }
/* 640 */     if (this.mPluginsLayout.getVisibility() == 0) {
/* 641 */       this.mPluginsLayout.setVisibility(8);
/*     */     }
/*     */ 
/* 644 */     if (this.mExtendLayout.getVisibility() == 0) {
/* 645 */       this.mExtendLayout.setVisibility(8);
/*     */     }
/*     */ 
/* 648 */     RongContext.getInstance().getEventBus().post(Event.ACTION);
/*     */   }
/*     */ 
/*     */   public void onProviderInactive(Context context) {
/* 652 */     if (this.mMainProvider != null) {
/* 653 */       this.mMainProvider.onInactive(context);
/*     */     }
/* 655 */     if (this.mSlaveProvider != null) {
/* 656 */       this.mSlaveProvider.onInactive(context);
/*     */     }
/* 658 */     if (this.mPluginsLayout.getVisibility() == 0) {
/* 659 */       this.mPluginsLayout.setVisibility(8);
/*     */     }
/*     */ 
/* 662 */     if (this.mExtendLayout.getVisibility() == 0) {
/* 663 */       this.mExtendLayout.setVisibility(8);
/*     */     }
/*     */ 
/* 666 */     RongContext.getInstance().getEventBus().post(Event.INACTION);
/*     */   }
/*     */ 
/*     */   public void onExtendProviderActive(Context context) {
/* 670 */     if (this.mMainProvider != null) {
/* 671 */       this.mMainProvider.onInactive(context);
/*     */     }
/* 673 */     if (this.mSlaveProvider != null) {
/* 674 */       this.mSlaveProvider.onInactive(context);
/*     */     }
/* 676 */     if (this.mPluginsLayout.getVisibility() == 0) {
/* 677 */       this.mPluginsLayout.setVisibility(8);
/*     */     }
/*     */ 
/* 680 */     if (this.mExtendLayout.getVisibility() == 0) {
/* 681 */       this.mExtendLayout.setVisibility(8);
/*     */     }
/*     */ 
/* 684 */     RongContext.getInstance().getEventBus().post(Event.ACTION);
/*     */   }
/*     */ 
/*     */   public void onEmojiProviderActive(Context context) {
/* 688 */     if (this.mMainProvider != null) {
/* 689 */       this.mMainProvider.onInactive(context);
/*     */     }
/* 691 */     if (this.mSlaveProvider != null) {
/* 692 */       this.mSlaveProvider.onInactive(context);
/*     */     }
/* 694 */     if (this.mPluginsLayout.getVisibility() == 0) {
/* 695 */       this.mPluginsLayout.setVisibility(8);
/*     */     }
/*     */ 
/* 698 */     if (this.mExtendLayout.getVisibility() == 0) {
/* 699 */       this.mExtendLayout.setVisibility(8);
/*     */     }
/*     */ 
/* 702 */     RongContext.getInstance().getEventBus().post(Event.ACTION);
/*     */   }
/*     */ 
/*     */   public void setInputBoardListener(IInputBoardListener inputBoardListener) {
/* 706 */     this.inputBoardListener = inputBoardListener;
/*     */   }
/*     */ 
/*     */   protected void onLayout(boolean changed, int l, int t, int r, int b)
/*     */   {
/* 721 */     super.onLayout(changed, l, t, r, b);
/* 722 */     if (this.originalTop != 0) {
/* 723 */       if (this.originalTop > t) {
/* 724 */         if ((this.originalBottom > b) && (this.inputBoardListener != null) && (this.collapsed)) {
/* 725 */           this.collapsed = false;
/* 726 */           this.inputBoardListener.onBoardExpanded(this.originalBottom - t);
/* 727 */         } else if ((this.collapsed) && (this.inputBoardListener != null)) {
/* 728 */           this.collapsed = false;
/* 729 */           this.inputBoardListener.onBoardExpanded(b - t);
/*     */         }
/*     */       }
/* 732 */       else if ((!this.collapsed) && (this.inputBoardListener != null)) {
/* 733 */         this.collapsed = true;
/* 734 */         this.inputBoardListener.onBoardCollapsed();
/*     */       }
/*     */     }
/*     */ 
/* 738 */     if (this.originalTop == 0) {
/* 739 */       this.originalTop = t;
/* 740 */       this.originalBottom = b;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface IInputBoardListener
/*     */   {
/*     */     public abstract void onBoardExpanded(int paramInt);
/*     */ 
/*     */     public abstract void onBoardCollapsed();
/*     */   }
/*     */ 
/*     */   class InputMenuClickListener
/*     */     implements View.OnClickListener
/*     */   {
/*     */     InputMenuClickListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void onClick(View v)
/*     */     {
/* 591 */       RLog.d("InputView", "InputMenuClickListener change to input");
/*     */ 
/* 593 */       InputView.this.mInputMenuLayout.startAnimation(InputView.this.createPopupAnimOut(v.getContext()));
/*     */ 
/* 595 */       InputView.this.mInputLayout.postDelayed(new Runnable(v)
/*     */       {
/*     */         public void run() {
/* 598 */           InputView.this.mInputMenuLayout.clearAnimation();
/* 599 */           InputView.this.mInputMenuLayout.setVisibility(8);
/*     */ 
/* 601 */           InputView.this.mInputLayout.startAnimation(InputView.this.createPopupAnimIn(this.val$v.getContext()));
/* 602 */           InputView.this.mInputLayout.setVisibility(0);
/*     */         }
/*     */       }
/*     */       , 310L);
/*     */     }
/*     */   }
/*     */ 
/*     */   class InputClickListener
/*     */     implements View.OnClickListener
/*     */   {
/*     */     InputClickListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void onClick(View v)
/*     */     {
/* 567 */       RLog.d("InputView", "InputClickListener change to input menu");
/*     */ 
/* 569 */       InputView.this.mMainProvider.onSwitch(v.getContext());
/* 570 */       InputView.this.mPluginsLayout.setVisibility(8);
/* 571 */       InputView.this.mExtendLayout.setVisibility(8);
/*     */ 
/* 573 */       InputView.this.mInputLayout.startAnimation(InputView.this.createPopupAnimOut(v.getContext()));
/* 574 */       InputView.this.mInputMenuLayout.postDelayed(new Runnable(v)
/*     */       {
/*     */         public void run() {
/* 577 */           InputView.this.mInputLayout.clearAnimation();
/* 578 */           InputView.this.mInputLayout.setVisibility(8);
/*     */ 
/* 580 */           InputView.this.mInputMenuLayout.startAnimation(InputView.this.createPopupAnimIn(this.val$v.getContext()));
/* 581 */           InputView.this.mInputMenuLayout.setVisibility(0);
/*     */         }
/*     */       }
/*     */       , 310L);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface OnInfoButtonClick
/*     */   {
/*     */     public abstract void onClick(View paramView);
/*     */   }
/*     */ 
/*     */   class ExtendClickListener
/*     */     implements View.OnClickListener
/*     */   {
/*     */     ExtendClickListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void onClick(View v)
/*     */     {
/* 120 */       if ((InputView.this.mPluginsLayout.getVisibility() == 8) || (InputView.this.mExtendLayout.getVisibility() == 0)) {
/* 121 */         InputView.this.onExtendProviderActive(v.getContext());
/* 122 */         InputView.this.mPluginsLayout.setVisibility(0);
/* 123 */         if ((InputView.this.mMainProvider instanceof VoiceInputProvider))
/* 124 */           InputView.this.setInputProvider(InputView.this.mSlaveProvider, InputView.this.mMainProvider);
/*     */       }
/* 126 */       else if (InputView.this.mPluginsLayout.getVisibility() == 0) {
/* 127 */         InputView.this.onProviderInactive(v.getContext());
/*     */       }
/*     */ 
/* 130 */       RongContext.getInstance().getEventBus().post(InputView.Event.CLICK);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum Event
/*     */   {
/*  62 */     ACTION, INACTION, DESTROY, CLICK;
/*     */   }
/*     */ 
/*     */   static enum Style
/*     */   {
/*  42 */     SCE(291), 
/*  43 */     ECS(801), 
/*  44 */     CES(561), 
/*  45 */     CSE(531), 
/*  46 */     SC(288), 
/*  47 */     CS(33), 
/*  48 */     EC(800), 
/*  49 */     CE(35), 
/*  50 */     C(32);
/*     */ 
/*  52 */     private int value = 0;
/*     */ 
/*     */     private Style(int value) {
/*  55 */       this.value = value;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.InputView
 * JD-Core Version:    0.6.0
 */
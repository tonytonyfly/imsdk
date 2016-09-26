/*     */ package io.rong.imkit.widget.provider;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.res.Resources;
/*     */ import android.graphics.drawable.Drawable;
/*     */ import android.text.Editable;
/*     */ import android.text.TextUtils;
/*     */ import android.text.TextWatcher;
/*     */ import android.view.KeyEvent;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.View.OnFocusChangeListener;
/*     */ import android.view.View.OnKeyListener;
/*     */ import android.view.View.OnLongClickListener;
/*     */ import android.view.ViewGroup;
/*     */ import android.view.inputmethod.InputMethodManager;
/*     */ import android.widget.Button;
/*     */ import android.widget.EditText;
/*     */ import android.widget.FrameLayout;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.TextView;
/*     */ import android.widget.TextView.OnEditorActionListener;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.fragment.MessageInputFragment;
/*     */ import io.rong.imkit.mention.ITextInputListener;
/*     */ import io.rong.imkit.model.Draft;
/*     */ import io.rong.imkit.utils.AndroidEmoji;
/*     */ import io.rong.imkit.widget.InputView;
/*     */ import io.rong.imkit.widget.InputView.Event;
/*     */ import io.rong.imkit.widget.RongEmojiPager;
/*     */ import io.rong.imkit.widget.RongEmojiPager.OnEmojiClickListener;
/*     */ import io.rong.imlib.MessageTag;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.TypingMessage.TypingMessageManager;
/*     */ import io.rong.imlib.model.Conversation;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.MentionedInfo;
/*     */ import io.rong.message.TextMessage;
/*     */ 
/*     */ public class TextInputProvider extends InputProvider.MainInputProvider
/*     */   implements TextWatcher, View.OnClickListener, View.OnFocusChangeListener, View.OnLongClickListener, View.OnKeyListener
/*     */ {
/*     */   private static final String TAG = "TextInputProvider";
/*     */   volatile InputView mInputView;
/*     */   private TextWatcher mTextWatcher;
/*     */   private ITextInputListener mTextInputListener;
/*  42 */   private boolean flag = true;
/*     */ 
/*     */   public void onInputResume(InputView inputView, Conversation conversation)
/*     */   {
/*  55 */     this.mInputView = inputView;
/*  56 */     setCurrentConversation(conversation);
/*     */   }
/*     */ 
/*     */   public void onInputPause()
/*     */   {
/*     */   }
/*     */ 
/*     */   public TextInputProvider(RongContext context) {
/*  64 */     super(context);
/*  65 */     RLog.d("TextInputProvider", "TextInputProvider");
/*     */   }
/*     */ 
/*     */   public void onAttached(MessageInputFragment fragment, InputView view)
/*     */   {
/*  70 */     RLog.d("TextInputProvider", "onAttached");
/*  71 */     super.onAttached(fragment, view);
/*     */   }
/*     */ 
/*     */   public void onDetached()
/*     */   {
/*  76 */     RLog.d("TextInputProvider", "Detached");
/*  77 */     if (this.mInputView == null) {
/*  78 */       RLog.e("TextInputProvider", "inputView is null!");
/*  79 */       return;
/*     */     }
/*  81 */     ViewHolder holder = (ViewHolder)this.mInputView.getTag();
/*  82 */     Conversation.ConversationType conversationType = getCurrentConversation().getConversationType();
/*  83 */     String targetId = getCurrentConversation().getTargetId();
/*  84 */     if ((holder != null) && (holder.mEdit != null) && (!TextUtils.isEmpty(holder.mEdit.getText()))) {
/*  85 */       String text = holder.mEdit.getText().toString();
/*  86 */       RongIMClient.getInstance().saveTextMessageDraft(conversationType, targetId, text, new RongIMClient.ResultCallback(targetId, conversationType, text)
/*     */       {
/*     */         public void onSuccess(Boolean aBoolean) {
/*  89 */           Draft draft = new Draft(this.val$targetId, Integer.valueOf(this.val$conversationType.getValue()), this.val$text, null);
/*  90 */           TextInputProvider.this.getContext().getEventBus().post(draft);
/*     */         }
/*     */ 
/*     */         public void onError(RongIMClient.ErrorCode e) {
/*     */         } } );
/*     */     }
/*     */     else {
/*  98 */       RongIMClient.getInstance().clearTextMessageDraft(conversationType, targetId, new RongIMClient.ResultCallback(targetId, conversationType)
/*     */       {
/*     */         public void onSuccess(Boolean aBoolean) {
/* 101 */           Draft draft = new Draft(this.val$targetId, Integer.valueOf(this.val$conversationType.getValue()), null, null);
/* 102 */           TextInputProvider.this.getContext().getEventBus().post(draft);
/*     */         }
/*     */ 
/*     */         public void onError(RongIMClient.ErrorCode e) {
/*     */         }
/*     */       });
/*     */     }
/* 110 */     if (holder != null) {
/* 111 */       holder.mEmojiPager = null;
/*     */     }
/* 113 */     RongContext.getInstance().getEventBus().unregister(this);
/* 114 */     super.onDetached();
/*     */   }
/*     */ 
/*     */   public Drawable obtainSwitchDrawable(Context context)
/*     */   {
/* 119 */     return context.getResources().getDrawable(R.drawable.rc_ic_keyboard);
/*     */   }
/*     */ 
/*     */   public View onCreateView(LayoutInflater inflater, ViewGroup parent, InputView inputView)
/*     */   {
/* 125 */     RLog.d("TextInputProvider", "onCreateView");
/* 126 */     View view = inflater.inflate(R.layout.rc_wi_txt_provider, parent);
/*     */ 
/* 129 */     ViewHolder holder = (ViewHolder)inputView.getTag();
/* 130 */     if (holder == null) {
/* 131 */       holder = new ViewHolder();
/*     */     }
/*     */ 
/* 134 */     holder.mSmile = ((ImageView)view.findViewById(16908294));
/* 135 */     holder.mEdit = ((EditText)view.findViewById(16908291));
/* 136 */     holder.mBack = ((FrameLayout)view.findViewById(R.id.rc_frame));
/* 137 */     inputView.setTag(holder);
/*     */ 
/* 139 */     if (inputView.getToggleLayout().getVisibility() == 0) {
/* 140 */       holder.mButton = ((Button)inflater.inflate(R.layout.rc_wi_text_btn, inputView.getToggleLayout(), false));
/* 141 */       inputView.getToggleLayout().addView(holder.mButton);
/*     */     }
/*     */ 
/* 144 */     if ((inputView.getToggleLayout().getVisibility() != 0) || (holder.mButton == null)) {
/* 145 */       holder.mButton = ((Button)view.findViewById(16908313));
/*     */     }
/*     */ 
/* 148 */     holder.mEdit.addTextChangedListener(this);
/* 149 */     holder.mEdit.setOnFocusChangeListener(this);
/* 150 */     holder.mSmile.setOnClickListener(this);
/* 151 */     holder.mEdit.setOnClickListener(this);
/* 152 */     holder.mEdit.setOnLongClickListener(this);
/* 153 */     holder.mEdit.setOnEditorActionListener(new TextView.OnEditorActionListener()
/*     */     {
/*     */       public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
/* 156 */         if (actionId == 6) {
/* 157 */           InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService("input_method");
/* 158 */           imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
/* 159 */           return true;
/*     */         }
/* 161 */         return false;
/*     */       }
/*     */     });
/* 164 */     holder.mEdit.setOnKeyListener(this);
/* 165 */     holder.mBack.setOnClickListener(this);
/* 166 */     if (this.mTextWatcher != null) {
/* 167 */       holder.mExtraTextWatcher = this.mTextWatcher;
/*     */     }
/* 169 */     this.mInputView = inputView;
/* 170 */     holder.mButton.setOnClickListener(this);
/*     */ 
/* 172 */     if (!RongContext.getInstance().getEventBus().isRegistered(this)) {
/* 173 */       RongContext.getInstance().getEventBus().register(this);
/*     */     }
/* 175 */     RongIMClient.getInstance().getTextMessageDraft(getCurrentConversation().getConversationType(), getCurrentConversation().getTargetId(), new RongIMClient.ResultCallback()
/*     */     {
/*     */       public void onSuccess(String s) {
/* 178 */         if (TextInputProvider.this.mInputView == null) {
/* 179 */           RLog.e("TextInputProvider", "inputView is null!");
/* 180 */           return;
/*     */         }
/* 182 */         TextInputProvider.ViewHolder holder = (TextInputProvider.ViewHolder)TextInputProvider.this.mInputView.getTag();
/* 183 */         if ((s != null) && (holder != null) && (holder.mEdit != null)) {
/* 184 */           TextInputProvider.access$002(TextInputProvider.this, false);
/* 185 */           holder.mEdit.setText(s);
/* 186 */           holder.mEdit.setSelection(s.length());
/*     */         }
/*     */       }
/*     */ 
/*     */       public void onError(RongIMClient.ErrorCode e)
/*     */       {
/*     */       }
/*     */     });
/* 194 */     return view;
/*     */   }
/*     */ 
/*     */   public void onClick(View v)
/*     */   {
/* 199 */     if (this.mInputView == null) {
/* 200 */       RLog.e("TextInputProvider", "inputView is null!");
/* 201 */       return;
/*     */     }
/* 203 */     ViewHolder holder = (ViewHolder)this.mInputView.getTag();
/* 204 */     if (holder == null) {
/* 205 */       RLog.e("TextInputProvider", "holder is null!");
/* 206 */       return;
/*     */     }
/* 208 */     if (holder.mSmile.equals(v)) {
/* 209 */       if (holder.mEmojiPager == null) {
/* 210 */         holder.mEmojiPager = new RongEmojiPager(this.mInputView.getExtendLayout());
/* 211 */         holder.mEmojiPager.setOnEmojiClickListener(new RongEmojiPager.OnEmojiClickListener(holder)
/*     */         {
/*     */           public void onEmojiClick(String key) {
/* 214 */             if (key.equals("/DEL")) {
/* 215 */               this.val$holder.mEdit.dispatchKeyEvent(new KeyEvent(0, 67));
/*     */             } else {
/* 217 */               int start = this.val$holder.mEdit.getSelectionStart();
/* 218 */               this.val$holder.mEdit.getText().insert(start, key);
/*     */             }
/*     */           }
/*     */         });
/* 223 */         if (holder.mEdit != null) {
/* 224 */           holder.mEdit.requestFocus();
/*     */         }
/* 226 */         this.mInputView.onEmojiProviderActive(getContext());
/* 227 */         this.mInputView.setExtendLayoutVisibility(0);
/* 228 */         if (this.mInputView.getExtendLayout().getVisibility() == 0) {
/* 229 */           holder.mSmile.setImageResource(R.drawable.rc_ic_smiley_selected);
/* 230 */           holder.mBack.setBackgroundResource(R.drawable.rc_bg_text_hover);
/*     */         }
/* 232 */       } else if (this.mInputView.getExtendLayout().getVisibility() == 8) {
/* 233 */         this.mInputView.onEmojiProviderActive(getContext());
/* 234 */         this.mInputView.setExtendLayoutVisibility(0);
/* 235 */         if (this.mInputView.getExtendLayout().getVisibility() == 0) {
/* 236 */           holder.mSmile.setImageResource(R.drawable.rc_ic_smiley_selected);
/* 237 */           holder.mBack.setBackgroundResource(R.drawable.rc_bg_text_hover);
/*     */         }
/* 239 */       } else if (this.mInputView.getExtendLayout().getVisibility() == 0) {
/* 240 */         holder.mBack.setBackgroundResource(R.drawable.rc_bg_text_hover);
/* 241 */         holder.mSmile.setImageResource(R.drawable.rc_ic_smiley_selected);
/*     */       } else {
/* 243 */         this.mInputView.onProviderInactive(getContext());
/*     */       }
/* 245 */     } else if (v.equals(holder.mButton)) {
/* 246 */       if (TextUtils.isEmpty(holder.mEdit.getText().toString().trim())) {
/* 247 */         holder.mEdit.getText().clear();
/* 248 */         holder.mEdit.setText("");
/* 249 */         return;
/*     */       }
/*     */ 
/* 252 */       TextMessage textMessage = TextMessage.obtain(holder.mEdit.getText().toString());
/* 253 */       if (this.mTextInputListener != null) {
/* 254 */         MentionedInfo mentionedInfo = this.mTextInputListener.onSendButtonClick();
/* 255 */         if (mentionedInfo != null) {
/* 256 */           textMessage.setMentionedInfo(mentionedInfo);
/*     */         }
/*     */       }
/* 259 */       publish(textMessage);
/* 260 */       holder.mEdit.getText().clear();
/* 261 */       holder.mEdit.setText("");
/* 262 */     } else if (holder.mEdit.equals(v)) {
/* 263 */       holder.mBack.setBackgroundResource(R.drawable.rc_bg_text_hover);
/* 264 */       holder.mSmile.setImageResource(R.drawable.rc_ic_smiley);
/* 265 */       this.mInputView.onProviderActive(getContext());
/* 266 */     } else if (v.equals(holder.mBack)) {
/* 267 */       holder.mBack.setBackgroundResource(R.drawable.rc_bg_text_hover);
/* 268 */       holder.mSmile.setImageResource(R.drawable.rc_ic_smiley);
/* 269 */       this.mInputView.onProviderActive(getContext());
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean onLongClick(View v)
/*     */   {
/* 276 */     if (this.mInputView == null) {
/* 277 */       RLog.e("TextInputProvider", "inputView is null!");
/* 278 */       return false;
/*     */     }
/*     */ 
/* 281 */     if ((this.mInputView != null) && (this.mInputView.getExtendLayout().getVisibility() == 0)) {
/* 282 */       this.mInputView.onProviderInactive(getContext());
/* 283 */       this.mInputView.setExtendLayoutVisibility(8);
/*     */     }
/* 285 */     return false;
/*     */   }
/*     */ 
/*     */   public void onActive(Context context)
/*     */   {
/* 290 */     if (this.mInputView == null) {
/* 291 */       RLog.e("TextInputProvider", "inputView is null!");
/* 292 */       return;
/*     */     }
/* 294 */     ViewHolder holder = (ViewHolder)this.mInputView.getTag();
/* 295 */     if ((holder == null) || (holder.mEdit == null)) {
/* 296 */       return;
/*     */     }
/*     */ 
/* 299 */     holder.mEdit.requestFocus();
/* 300 */     InputMethodManager imm = (InputMethodManager)context.getSystemService("input_method");
/* 301 */     imm.showSoftInput(holder.mEdit, 0);
/*     */   }
/*     */ 
/*     */   public void onInactive(Context context)
/*     */   {
/* 307 */     if (this.mInputView == null) {
/* 308 */       RLog.e("TextInputProvider", "inputView is null!");
/* 309 */       return;
/*     */     }
/* 311 */     ViewHolder holder = (ViewHolder)this.mInputView.getTag();
/* 312 */     if ((holder == null) || (holder.mEdit == null)) {
/* 313 */       return;
/*     */     }
/*     */ 
/* 316 */     if (this.mInputView.getExtendLayout().getVisibility() == 8) {
/* 317 */       holder.mSmile.setImageResource(R.drawable.rc_ic_smiley);
/* 318 */       if (holder.mEdit.hasFocus())
/* 319 */         holder.mBack.setBackgroundResource(R.drawable.rc_bg_text_hover);
/*     */       else {
/* 321 */         holder.mBack.setBackgroundResource(R.drawable.rc_bg_text_normal);
/*     */       }
/*     */     }
/*     */ 
/* 325 */     InputMethodManager imm = (InputMethodManager)context.getSystemService("input_method");
/* 326 */     imm.hideSoftInputFromWindow(holder.mEdit.getWindowToken(), 0);
/*     */   }
/*     */ 
/*     */   public void onSwitch(Context context)
/*     */   {
/* 331 */     if (this.mInputView == null) {
/* 332 */       RLog.e("TextInputProvider", "inputView is null!");
/* 333 */       return;
/*     */     }
/* 335 */     ViewHolder holder = (ViewHolder)this.mInputView.getTag();
/* 336 */     if (holder == null) {
/* 337 */       return;
/*     */     }
/* 339 */     holder.mButton.setVisibility(8);
/* 340 */     onInactive(context);
/*     */   }
/*     */ 
/*     */   public void onFocusChange(View v, boolean hasFocus)
/*     */   {
/* 345 */     if (this.mInputView == null) {
/* 346 */       RLog.e("TextInputProvider", "inputView is null!");
/* 347 */       return;
/*     */     }
/* 349 */     if ((this.mInputView != null) && (hasFocus))
/* 350 */       this.mInputView.setExtendInputsVisibility(8);
/*     */   }
/*     */ 
/*     */   public void beforeTextChanged(CharSequence s, int start, int count, int after)
/*     */   {
/* 357 */     if (this.mInputView == null) {
/* 358 */       RLog.e("TextInputProvider", "inputView is null!");
/* 359 */       return;
/*     */     }
/* 361 */     ViewHolder holder = (ViewHolder)this.mInputView.getTag();
/* 362 */     if ((holder != null) && (holder.mExtraTextWatcher != null))
/* 363 */       holder.mExtraTextWatcher.beforeTextChanged(s, start, count, after);
/*     */   }
/*     */ 
/*     */   public void onTextChanged(CharSequence s, int start, int before, int count)
/*     */   {
/* 369 */     if (this.mInputView == null) {
/* 370 */       RLog.e("TextInputProvider", "inputView is null!");
/* 371 */       return;
/*     */     }
/* 373 */     ViewHolder holder = (ViewHolder)this.mInputView.getTag();
/* 374 */     if (holder == null) {
/* 375 */       return;
/*     */     }
/* 377 */     if (holder.mExtraTextWatcher != null) {
/* 378 */       holder.mExtraTextWatcher.onTextChanged(s, start, before, count);
/*     */     }
/*     */ 
/* 381 */     Conversation.ConversationType conversationType = getCurrentConversation().getConversationType();
/* 382 */     String targetId = getCurrentConversation().getTargetId();
/* 383 */     if ((this.mTextInputListener != null) && (this.flag) && ((conversationType.equals(Conversation.ConversationType.GROUP)) || (conversationType.equals(Conversation.ConversationType.DISCUSSION))))
/*     */     {
/* 385 */       if (count == 0)
/* 386 */         this.mTextInputListener.onTextEdit(conversationType, targetId, start + before, -before, s.toString());
/*     */       else {
/* 388 */         this.mTextInputListener.onTextEdit(conversationType, targetId, start, count, s.toString());
/*     */       }
/*     */     }
/* 391 */     this.flag = true;
/*     */ 
/* 393 */     if (holder.mButton != null)
/* 394 */       if (TextUtils.isEmpty(s)) {
/* 395 */         holder.mButton.setVisibility(8);
/*     */       }
/* 397 */       else if (this.mInputView.getToggleLayout().getVisibility() == 0) {
/* 398 */         View view = this.mInputView.getToggleLayout().findViewById(16908313);
/* 399 */         if (view == null) {
/* 400 */           if (holder.mButton != null)
/* 401 */             holder.mButton.setVisibility(8);
/* 402 */           holder.mButton = ((Button)LayoutInflater.from(getContext()).inflate(R.layout.rc_wi_text_btn, this.mInputView.getToggleLayout(), false));
/* 403 */           this.mInputView.getToggleLayout().addView(holder.mButton);
/* 404 */           holder.mButton.setOnClickListener(this);
/*     */         }
/* 406 */         holder.mButton.setVisibility(0);
/*     */       } else {
/* 408 */         holder.mButton.setVisibility(0);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void afterTextChanged(Editable s)
/*     */   {
/* 416 */     if (this.mInputView == null) {
/* 417 */       RLog.e("TextInputProvider", "inputView is null!");
/* 418 */       return;
/*     */     }
/* 420 */     ViewHolder holder = (ViewHolder)this.mInputView.getTag();
/* 421 */     if (holder == null) {
/* 422 */       return;
/*     */     }
/*     */ 
/* 425 */     if ((s.toString().length() > 0) && 
/* 426 */       (TypingMessageManager.getInstance().isShowMessageTyping())) {
/* 427 */       MessageTag tag = (MessageTag)TextMessage.class.getAnnotation(MessageTag.class);
/* 428 */       onTypingMessage(tag.value());
/*     */     }
/*     */ 
/* 432 */     if (AndroidEmoji.isEmoji(s.toString())) {
/* 433 */       int start = holder.mEdit.getSelectionStart();
/* 434 */       int end = holder.mEdit.getSelectionEnd();
/* 435 */       holder.mEdit.removeTextChangedListener(this);
/* 436 */       holder.mEdit.setText(AndroidEmoji.ensure(s.toString()));
/* 437 */       holder.mEdit.addTextChangedListener(this);
/* 438 */       holder.mEdit.setSelection(start, end);
/*     */     }
/* 440 */     if (holder.mExtraTextWatcher != null) {
/* 441 */       holder.mExtraTextWatcher.afterTextChanged(s);
/*     */     }
/* 443 */     RLog.d("TextInputProvider", "afterTextChanged " + s.toString());
/*     */   }
/*     */ 
/*     */   public void setEditTextContent(CharSequence content)
/*     */   {
/* 452 */     if (this.mInputView == null) {
/* 453 */       RLog.e("TextInputProvider", "inputView is null!");
/* 454 */       return;
/*     */     }
/* 456 */     ViewHolder holder = (ViewHolder)this.mInputView.getTag();
/* 457 */     if (holder == null) {
/* 458 */       return;
/*     */     }
/* 460 */     if ((holder.mEdit != null) && (content != null)) {
/* 461 */       holder.mEdit.setText(content);
/* 462 */       holder.mEdit.setSelection(content.length());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setEditTextChangedListener(TextWatcher listener) {
/* 467 */     this.mTextWatcher = listener;
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(InputView.Event event) {
/* 471 */     if (this.mInputView == null) {
/* 472 */       RLog.e("TextInputProvider", "inputView is null!");
/* 473 */       return;
/*     */     }
/* 475 */     ViewHolder holder = (ViewHolder)this.mInputView.getTag();
/* 476 */     if (holder == null) {
/* 477 */       return;
/*     */     }
/* 479 */     if ((event == InputView.Event.CLICK) && 
/* 480 */       (this.mInputView.getExtendLayout().getVisibility() == 8)) {
/* 481 */       holder.mSmile.setImageResource(R.drawable.rc_ic_smiley);
/* 482 */       holder.mBack.setBackgroundResource(R.drawable.rc_bg_text_normal);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean onKey(View v, int keyCode, KeyEvent event)
/*     */   {
/* 489 */     if ((event.getKeyCode() == 67) && (event.getAction() == 0)) {
/* 490 */       ViewHolder holder = (ViewHolder)this.mInputView.getTag();
/* 491 */       if ((holder != null) && (holder.mEdit != null) && (this.mTextInputListener != null)) {
/* 492 */         int cursorPos = holder.mEdit.getSelectionStart();
/* 493 */         this.mTextInputListener.onDeleteClick(getCurrentConversation().getConversationType(), getCurrentConversation().getTargetId(), holder.mEdit, cursorPos);
/*     */       }
/*     */     }
/* 496 */     return false;
/*     */   }
/*     */ 
/*     */   public EditText getEditText()
/*     */   {
/* 501 */     EditText editText = null;
/* 502 */     if (this.mInputView != null) {
/* 503 */       ViewHolder holder = (ViewHolder)this.mInputView.getTag();
/* 504 */       if (holder != null) {
/* 505 */         editText = holder.mEdit;
/*     */       }
/*     */     }
/* 508 */     return editText;
/*     */   }
/*     */ 
/*     */   public void setTextInputListener(ITextInputListener listener) {
/* 512 */     this.mTextInputListener = listener;
/*     */   }
/*     */ 
/*     */   public class ViewHolder
/*     */   {
/*     */     public ImageView mSmile;
/*     */     public Button mButton;
/*     */     public RongEmojiPager mEmojiPager;
/*     */     public EditText mEdit;
/*     */     public TextWatcher mExtraTextWatcher;
/*     */     public FrameLayout mBack;
/*     */ 
/*     */     public ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.TextInputProvider
 * JD-Core Version:    0.6.0
 */
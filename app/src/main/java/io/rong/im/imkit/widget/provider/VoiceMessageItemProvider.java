/*     */ package io.rong.imkit.widget.provider;
/*     */ 
/*     */ import android.annotation.TargetApi;
/*     */ import android.content.Context;
/*     */ import android.content.DialogInterface;
/*     */ import android.content.res.Resources;
/*     */ import android.content.res.Resources.NotFoundException;
/*     */ import android.graphics.drawable.AnimationDrawable;
/*     */ import android.media.AudioManager;
/*     */ import android.net.Uri;
/*     */ import android.os.Build.VERSION;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.text.Spannable;
/*     */ import android.text.SpannableString;
/*     */ import android.util.DisplayMetrics;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.ImageView.ScaleType;
/*     */ import android.widget.TextView;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.R.bool;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.integer;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.manager.AudioPlayManager;
/*     */ import io.rong.imkit.manager.AudioRecordManager;
/*     */ import io.rong.imkit.manager.IAudioPlayListener;
/*     */ import io.rong.imkit.model.Event.AudioListenedEvent;
/*     */ import io.rong.imkit.model.Event.PlayAudioEvent;
/*     */ import io.rong.imkit.model.ProviderTag;
/*     */ import io.rong.imkit.model.UIMessage;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment.OnArraysDialogItemListener;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Conversation.PublicServiceType;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.imlib.model.Message.MessageDirection;
/*     */ import io.rong.imlib.model.Message.ReceivedStatus;
/*     */ import io.rong.imlib.model.Message.SentStatus;
/*     */ import io.rong.imlib.model.PublicServiceProfile;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import io.rong.message.VoiceMessage;
/*     */ 
/*     */ @ProviderTag(messageContent=VoiceMessage.class)
/*     */ public class VoiceMessageItemProvider extends IContainerItemProvider.MessageProvider<VoiceMessage>
/*     */ {
/*     */   private static final String TAG = "VoiceMessageItemProvider";
/*     */ 
/*     */   public VoiceMessageItemProvider(Context context)
/*     */   {
/*     */   }
/*     */ 
/*     */   public View newView(Context context, ViewGroup group)
/*     */   {
/*  56 */     View view = LayoutInflater.from(context).inflate(R.layout.rc_item_voice_message, null);
/*  57 */     ViewHolder holder = new ViewHolder();
/*  58 */     holder.left = ((TextView)view.findViewById(R.id.rc_left));
/*  59 */     holder.right = ((TextView)view.findViewById(R.id.rc_right));
/*  60 */     holder.img = ((ImageView)view.findViewById(R.id.rc_img));
/*  61 */     holder.unread = ((ImageView)view.findViewById(R.id.rc_voice_unread));
/*  62 */     view.setTag(holder);
/*  63 */     return view;
/*     */   }
/*     */ 
/*     */   public void bindView(View v, int position, VoiceMessage content, UIMessage message)
/*     */   {
/*  68 */     ViewHolder holder = (ViewHolder)v.getTag();
/*  69 */     if (message.continuePlayAudio) {
/*  70 */       Uri playingUri = AudioPlayManager.getInstance().getPlayingUri();
/*  71 */       if ((playingUri == null) || (!playingUri.equals(content.getUri()))) {
/*  72 */         boolean listened = message.getMessage().getReceivedStatus().isListened();
/*  73 */         AudioPlayManager.getInstance().startPlay(v.getContext(), content.getUri(), new IAudioPlayListener(message, v, holder, content, listened)
/*     */         {
/*     */           public void onStart(Uri uri) {
/*  76 */             RLog.d("VoiceMessageItemProvider", "onStart " + uri);
/*  77 */             this.val$message.getReceivedStatus().setListened();
/*  78 */             this.val$message.continuePlayAudio = false;
/*  79 */             RongIMClient.getInstance().setMessageReceivedStatus(this.val$message.getMessageId(), this.val$message.getReceivedStatus(), null);
/*  80 */             VoiceMessageItemProvider.this.setLayout(this.val$v.getContext(), this.val$holder, this.val$message, true);
/*  81 */             EventBus.getDefault().post(new Event.AudioListenedEvent(this.val$message.getConversationType(), this.val$message.getTargetId(), this.val$message.getMessageId()));
/*     */           }
/*     */ 
/*     */           public void onStop(Uri uri)
/*     */           {
/*  86 */             RLog.d("VoiceMessageItemProvider", "onStop " + uri);
/*  87 */             VoiceMessageItemProvider.this.setLayout(this.val$v.getContext(), this.val$holder, this.val$message, false);
/*     */           }
/*     */ 
/*     */           public void onComplete(Uri uri)
/*     */           {
/*  92 */             RLog.d("VoiceMessageItemProvider", "onComplete " + uri);
/*  93 */             VoiceMessageItemProvider.this.setLayout(this.val$v.getContext(), this.val$holder, this.val$message, false);
/*  94 */             Event.PlayAudioEvent event = Event.PlayAudioEvent.obtain();
/*  95 */             event.content = this.val$content;
/*     */ 
/*  97 */             if (!this.val$listened) {
/*     */               try {
/*  99 */                 event.continuously = RongContext.getInstance().getResources().getBoolean(R.bool.rc_play_audio_continuous);
/*     */               } catch (Resources.NotFoundException e) {
/* 101 */                 e.printStackTrace();
/*     */               }
/*     */             }
/* 104 */             if (event.continuously)
/* 105 */               EventBus.getDefault().post(event);
/*     */           } } );
/*     */       }
/*     */     }
/*     */     else {
/* 111 */       setLayout(v.getContext(), holder, message, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onItemClick(View view, int position, VoiceMessage content, UIMessage message)
/*     */   {
/* 117 */     RLog.d("VoiceMessageItemProvider", "Item index:" + position);
/* 118 */     ViewHolder holder = (ViewHolder)view.getTag();
/* 119 */     holder.unread.setVisibility(8);
/* 120 */     Uri playingUri = AudioPlayManager.getInstance().getPlayingUri();
/* 121 */     if ((playingUri != null) && (playingUri.equals(content.getUri()))) {
/* 122 */       AudioPlayManager.getInstance().stopPlay();
/*     */     } else {
/* 124 */       boolean listened = message.getMessage().getReceivedStatus().isListened();
/* 125 */       AudioPlayManager.getInstance().startPlay(view.getContext(), content.getUri(), new IAudioPlayListener(message, view, holder, content, listened)
/*     */       {
/*     */         public void onStart(Uri uri) {
/* 128 */           this.val$message.getReceivedStatus().setListened();
/* 129 */           this.val$message.continuePlayAudio = false;
/* 130 */           RongIMClient.getInstance().setMessageReceivedStatus(this.val$message.getMessageId(), this.val$message.getReceivedStatus(), null);
/* 131 */           VoiceMessageItemProvider.this.setLayout(this.val$view.getContext(), this.val$holder, this.val$message, true);
/* 132 */           EventBus.getDefault().post(new Event.AudioListenedEvent(this.val$message.getConversationType(), this.val$message.getTargetId(), this.val$message.getMessageId()));
/*     */         }
/*     */ 
/*     */         public void onStop(Uri uri)
/*     */         {
/* 137 */           VoiceMessageItemProvider.this.setLayout(this.val$view.getContext(), this.val$holder, this.val$message, false);
/*     */         }
/*     */ 
/*     */         public void onComplete(Uri uri)
/*     */         {
/* 142 */           VoiceMessageItemProvider.this.setLayout(this.val$view.getContext(), this.val$holder, this.val$message, false);
/* 143 */           Event.PlayAudioEvent event = Event.PlayAudioEvent.obtain();
/* 144 */           event.content = this.val$content;
/*     */ 
/* 146 */           if ((!this.val$listened) && (this.val$message.getMessageDirection().equals(Message.MessageDirection.RECEIVE))) {
/*     */             try {
/* 148 */               event.continuously = RongContext.getInstance().getResources().getBoolean(R.bool.rc_play_audio_continuous);
/*     */             } catch (Resources.NotFoundException e) {
/* 150 */               e.printStackTrace();
/*     */             }
/*     */           }
/* 153 */           if (event.continuously)
/* 154 */             EventBus.getDefault().post(event);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onItemLongClick(View view, int position, VoiceMessage content, UIMessage message)
/*     */   {
/* 163 */     String name = null;
/* 164 */     if ((message.getConversationType().getName().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName())) || (message.getConversationType().getName().equals(Conversation.ConversationType.PUBLIC_SERVICE.getName())))
/*     */     {
/* 166 */       if (message.getUserInfo() != null) {
/* 167 */         name = message.getUserInfo().getName();
/*     */       } else {
/* 169 */         Conversation.PublicServiceType publicServiceType = Conversation.PublicServiceType.setValue(message.getConversationType().getValue());
/* 170 */         PublicServiceProfile info = RongUserInfoManager.getInstance().getPublicServiceProfile(publicServiceType, message.getTargetId());
/* 171 */         if (info != null)
/* 172 */           name = info.getName();
/*     */       }
/*     */     } else {
/* 175 */       UserInfo userInfo = message.getUserInfo();
/* 176 */       if (userInfo == null)
/* 177 */         userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
/* 178 */       if (userInfo != null) {
/* 179 */         name = userInfo.getName();
/*     */       }
/*     */     }
/*     */ 
/* 183 */     long deltaTime = RongIM.getInstance().getDeltaTime();
/* 184 */     long normalTime = System.currentTimeMillis() - deltaTime;
/* 185 */     boolean enableMessageRecall = false;
/* 186 */     int messageRecallInterval = -1;
/* 187 */     boolean hasSent = (!message.getSentStatus().equals(Message.SentStatus.SENDING)) && (!message.getSentStatus().equals(Message.SentStatus.FAILED));
/*     */     try
/*     */     {
/* 190 */       enableMessageRecall = RongContext.getInstance().getResources().getBoolean(R.bool.rc_enable_message_recall);
/* 191 */       messageRecallInterval = RongContext.getInstance().getResources().getInteger(R.integer.rc_message_recall_interval);
/*     */     } catch (Resources.NotFoundException e) {
/* 193 */       RLog.e("VoiceMessageItemProvider", "rc_message_recall_interval not configure in rc_config.xml");
/* 194 */       e.printStackTrace();
/*     */     }
/*     */     String[] items;
/*     */     String[] items;
/* 196 */     if ((hasSent) && (enableMessageRecall) && (normalTime - message.getSentTime() <= messageRecallInterval * 1000) && (message.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId())))
/* 197 */       items = new String[] { view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete), view.getContext().getResources().getString(R.string.rc_dialog_item_message_recall) };
/*     */     else {
/* 199 */       items = new String[] { view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete) };
/*     */     }
/*     */ 
/* 202 */     ArraysDialogFragment.newInstance(name, items).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener(message)
/*     */     {
/*     */       public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
/* 205 */         if (which == 0) {
/* 206 */           AudioPlayManager.getInstance().stopPlay();
/* 207 */           RongIM.getInstance().deleteMessages(new int[] { this.val$message.getMessageId() }, null);
/* 208 */         } else if (which == 1) {
/* 209 */           if (AudioPlayManager.getInstance().getPlayingUri() != null) {
/* 210 */             AudioPlayManager.getInstance().stopPlay();
/*     */           }
/* 212 */           RongIM.getInstance().recallMessage(this.val$message);
/*     */         }
/*     */       }
/*     */     }).show(((FragmentActivity)view.getContext()).getSupportFragmentManager());
/*     */   }
/*     */ 
/*     */   private void setLayout(Context context, ViewHolder holder, UIMessage message, boolean playing)
/*     */   {
/* 219 */     VoiceMessage content = (VoiceMessage)message.getContent();
/* 220 */     int minLength = 57;
/* 221 */     int duration = AudioRecordManager.getInstance().getMaxVoiceDuration();
/* 222 */     holder.img.getLayoutParams().width = (int)((content.getDuration() * (180 / duration) + minLength) * context.getResources().getDisplayMetrics().density);
/*     */ 
/* 224 */     if (message.getMessageDirection() == Message.MessageDirection.SEND) {
/* 225 */       holder.left.setText(String.format("%s\"", new Object[] { Integer.valueOf(content.getDuration()) }));
/* 226 */       holder.left.setVisibility(0);
/* 227 */       holder.right.setVisibility(8);
/* 228 */       holder.unread.setVisibility(8);
/* 229 */       holder.img.setScaleType(ImageView.ScaleType.FIT_END);
/* 230 */       holder.img.setBackgroundResource(R.drawable.rc_ic_bubble_right);
/* 231 */       AnimationDrawable animationDrawable = (AnimationDrawable)context.getResources().getDrawable(R.drawable.rc_an_voice_sent);
/* 232 */       if (playing) {
/* 233 */         holder.img.setImageDrawable(animationDrawable);
/* 234 */         if (animationDrawable != null)
/* 235 */           animationDrawable.start();
/*     */       } else {
/* 237 */         holder.img.setImageDrawable(holder.img.getResources().getDrawable(R.drawable.rc_ic_voice_sent));
/* 238 */         if (animationDrawable != null)
/* 239 */           animationDrawable.stop();
/*     */       }
/*     */     } else {
/* 242 */       holder.right.setText(String.format("%s\"", new Object[] { Integer.valueOf(content.getDuration()) }));
/* 243 */       holder.right.setVisibility(0);
/* 244 */       holder.left.setVisibility(8);
/* 245 */       if (!message.getReceivedStatus().isListened())
/* 246 */         holder.unread.setVisibility(0);
/*     */       else
/* 248 */         holder.unread.setVisibility(8);
/* 249 */       holder.img.setBackgroundResource(R.drawable.rc_ic_bubble_left);
/* 250 */       AnimationDrawable animationDrawable = (AnimationDrawable)context.getResources().getDrawable(R.drawable.rc_an_voice_receive);
/* 251 */       if (playing) {
/* 252 */         holder.img.setImageDrawable(animationDrawable);
/* 253 */         if (animationDrawable != null)
/* 254 */           animationDrawable.start();
/*     */       } else {
/* 256 */         holder.img.setImageDrawable(holder.img.getResources().getDrawable(R.drawable.rc_ic_voice_receive));
/* 257 */         if (animationDrawable != null)
/* 258 */           animationDrawable.stop();
/*     */       }
/* 260 */       holder.img.setScaleType(ImageView.ScaleType.FIT_START);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Spannable getContentSummary(VoiceMessage data)
/*     */   {
/* 266 */     return new SpannableString(RongContext.getInstance().getString(R.string.rc_message_content_voice));
/*     */   }
/*     */   @TargetApi(8)
/*     */   private boolean muteAudioFocus(Context context, boolean bMute) {
/* 271 */     if (context == null) {
/* 272 */       RLog.d("VoiceMessageItemProvider", "muteAudioFocus context is null.");
/* 273 */       return false;
/*     */     }
/* 275 */     if (Build.VERSION.SDK_INT < 8)
/*     */     {
/* 277 */       RLog.d("VoiceMessageItemProvider", "muteAudioFocus Android 2.1 and below can not stop music");
/* 278 */       return false;
/*     */     }
/* 280 */     boolean bool = false;
/* 281 */     AudioManager am = (AudioManager)context.getSystemService("audio");
/* 282 */     if (bMute) {
/* 283 */       int result = am.requestAudioFocus(null, 3, 2);
/* 284 */       bool = result == 1;
/*     */     } else {
/* 286 */       int result = am.abandonAudioFocus(null);
/* 287 */       bool = result == 1;
/*     */     }
/* 289 */     RLog.d("VoiceMessageItemProvider", "muteAudioFocus pauseMusic bMute=" + bMute + " result=" + bool);
/* 290 */     return bool;
/*     */   }
/*     */ 
/*     */   class ViewHolder
/*     */   {
/*     */     ImageView img;
/*     */     TextView left;
/*     */     TextView right;
/*     */     ImageView unread;
/*     */ 
/*     */     ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.VoiceMessageItemProvider
 * JD-Core Version:    0.6.0
 */
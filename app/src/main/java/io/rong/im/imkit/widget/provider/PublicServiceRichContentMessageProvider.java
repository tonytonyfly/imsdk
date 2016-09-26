/*     */ package io.rong.imkit.widget.provider;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.DialogInterface;
/*     */ import android.content.Intent;
/*     */ import android.content.res.Resources;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.text.Spannable;
/*     */ import android.text.SpannableString;
/*     */ import android.view.Display;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import android.view.WindowManager;
/*     */ import android.widget.LinearLayout.LayoutParams;
/*     */ import android.widget.TextView;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.model.ProviderTag;
/*     */ import io.rong.imkit.model.UIMessage;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment.OnArraysDialogItemListener;
/*     */ import io.rong.imkit.widget.AsyncImageView;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Conversation.PublicServiceType;
/*     */ import io.rong.imlib.model.PublicServiceProfile;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import io.rong.message.PublicServiceRichContentMessage;
/*     */ import io.rong.message.RichContentItem;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ 
/*     */ @ProviderTag(messageContent=PublicServiceRichContentMessage.class, showPortrait=false, centerInHorizontal=true, showSummaryWithName=false)
/*     */ public class PublicServiceRichContentMessageProvider extends IContainerItemProvider.MessageProvider<PublicServiceRichContentMessage>
/*     */ {
/*     */   private Context mContext;
/*     */   private int width;
/*     */   private int height;
/*     */ 
/*     */   public View newView(Context context, ViewGroup group)
/*     */   {
/*  41 */     this.mContext = context;
/*  42 */     ViewHolder holder = new ViewHolder(null);
/*  43 */     View view = LayoutInflater.from(context).inflate(R.layout.rc_item_public_service_rich_content_message, null);
/*     */ 
/*  45 */     holder.title = ((TextView)view.findViewById(R.id.rc_title));
/*  46 */     holder.time = ((TextView)view.findViewById(R.id.rc_time));
/*  47 */     holder.description = ((TextView)view.findViewById(R.id.rc_content));
/*  48 */     holder.imageView = ((AsyncImageView)view.findViewById(R.id.rc_img));
/*     */ 
/*  50 */     WindowManager m = (WindowManager)view.getContext().getSystemService("window");
/*  51 */     int w = m.getDefaultDisplay().getWidth() - 35;
/*  52 */     view.setLayoutParams(new LinearLayout.LayoutParams(w, -2));
/*  53 */     this.width = (w - 100);
/*  54 */     this.height = 800;
/*  55 */     view.setTag(holder);
/*  56 */     return view;
/*     */   }
/*     */ 
/*     */   public void bindView(View v, int position, PublicServiceRichContentMessage content, UIMessage message)
/*     */   {
/*  61 */     ViewHolder holder = (ViewHolder)v.getTag();
/*     */ 
/*  63 */     PublicServiceRichContentMessage msg = (PublicServiceRichContentMessage)message.getContent();
/*     */ 
/*  65 */     holder.title.setText(msg.getMessage().getTitle());
/*  66 */     holder.description.setText(msg.getMessage().getDigest());
/*     */ 
/*  68 */     int w = this.width;
/*  69 */     int h = this.height;
/*     */ 
/*  71 */     holder.imageView.setResource(msg.getMessage().getImageUrl(), 0);
/*  72 */     String time = formatDate(message.getReceivedTime(), "MM月dd日 HH:mm");
/*  73 */     holder.time.setText(time);
/*     */   }
/*     */ 
/*     */   private String formatDate(long timeMillis, String format) {
/*  77 */     SimpleDateFormat sdf = new SimpleDateFormat(format);
/*  78 */     return sdf.format(new Date(timeMillis));
/*     */   }
/*     */ 
/*     */   public Spannable getContentSummary(PublicServiceRichContentMessage data)
/*     */   {
/*  83 */     return new SpannableString(data.getMessage().getTitle());
/*     */   }
/*     */ 
/*     */   public void onItemClick(View view, int position, PublicServiceRichContentMessage content, UIMessage message)
/*     */   {
/*  89 */     String url = content.getMessage().getUrl();
/*  90 */     String action = "io.rong.imkit.intent.action.webview";
/*  91 */     Intent intent = new Intent(action);
/*  92 */     intent.setPackage(this.mContext.getPackageName());
/*  93 */     intent.putExtra("url", url);
/*  94 */     this.mContext.startActivity(intent);
/*     */   }
/*     */ 
/*     */   public void onItemLongClick(View view, int position, PublicServiceRichContentMessage content, UIMessage message)
/*     */   {
/*  99 */     String name = null;
/* 100 */     if ((message.getConversationType().getName().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName())) || (message.getConversationType().getName().equals(Conversation.ConversationType.PUBLIC_SERVICE.getName())))
/*     */     {
/* 102 */       if (message.getUserInfo() != null) {
/* 103 */         name = message.getUserInfo().getName();
/*     */       } else {
/* 105 */         Conversation.PublicServiceType publicServiceType = Conversation.PublicServiceType.setValue(message.getConversationType().getValue());
/* 106 */         PublicServiceProfile info = RongUserInfoManager.getInstance().getPublicServiceProfile(publicServiceType, message.getTargetId());
/*     */ 
/* 108 */         if (info != null)
/* 109 */           name = info.getName();
/*     */       }
/*     */     } else {
/* 112 */       UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
/* 113 */       if (userInfo != null) {
/* 114 */         name = userInfo.getName();
/*     */       }
/*     */     }
/*     */ 
/* 118 */     String[] items = { view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete) };
/*     */ 
/* 120 */     ArraysDialogFragment.newInstance(name, items).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener(message)
/*     */     {
/*     */       public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
/* 123 */         if (which == 0)
/* 124 */           RongIM.getInstance().deleteMessages(new int[] { this.val$message.getMessageId() }, null);
/*     */       }
/*     */     }).show(((FragmentActivity)view.getContext()).getSupportFragmentManager());
/*     */   }
/*     */ 
/*     */   private class ViewHolder
/*     */   {
/*     */     TextView title;
/*     */     AsyncImageView imageView;
/*     */     TextView time;
/*     */     TextView description;
/*     */ 
/*     */     private ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.PublicServiceRichContentMessageProvider
 * JD-Core Version:    0.6.0
 */
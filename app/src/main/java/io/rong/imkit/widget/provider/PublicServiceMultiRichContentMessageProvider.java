/*     */ package io.rong.imkit.widget.provider;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.DialogInterface;
/*     */ import android.content.Intent;
/*     */ import android.content.res.Resources;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.text.Spannable;
/*     */ import android.text.SpannableString;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.View.MeasureSpec;
/*     */ import android.view.ViewGroup;
/*     */ import android.view.ViewGroup.LayoutParams;
/*     */ import android.widget.AdapterView;
/*     */ import android.widget.AdapterView.OnItemClickListener;
/*     */ import android.widget.BaseAdapter;
/*     */ import android.widget.ListAdapter;
/*     */ import android.widget.ListView;
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
/*     */ import io.rong.message.PublicServiceMultiRichContentMessage;
/*     */ import io.rong.message.RichContentItem;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ @ProviderTag(messageContent=PublicServiceMultiRichContentMessage.class, showPortrait=false, centerInHorizontal=true, showSummaryWithName=false)
/*     */ public class PublicServiceMultiRichContentMessageProvider extends IContainerItemProvider.MessageProvider<PublicServiceMultiRichContentMessage>
/*     */ {
/*     */   private PublicAccountMsgAdapter mAdapter;
/*     */   private Context mContext;
/*     */ 
/*     */   public void bindView(View v, int position, PublicServiceMultiRichContentMessage content, UIMessage message)
/*     */   {
/*  46 */     ViewHolder vh = (ViewHolder)v.getTag();
/*  47 */     ArrayList msgList = content.getMessages();
/*     */ 
/*  49 */     if (msgList.size() > 0) {
/*  50 */       vh.tv.setText(((RichContentItem)msgList.get(0)).getTitle());
/*  51 */       vh.iv.setResource(((RichContentItem)msgList.get(0)).getImageUrl(), 0);
/*     */     }
/*     */ 
/*  54 */     int height = 0;
/*  55 */     ViewGroup.LayoutParams params = v.getLayoutParams();
/*     */ 
/*  57 */     this.mAdapter = new PublicAccountMsgAdapter(this.mContext, msgList);
/*  58 */     vh.lv.setAdapter(this.mAdapter);
/*     */ 
/*  60 */     vh.lv.setOnItemClickListener(new AdapterView.OnItemClickListener(msgList)
/*     */     {
/*     */       public void onItemClick(AdapterView<?> parent, View view, int position, long id)
/*     */       {
/*  65 */         RichContentItem item = (RichContentItem)this.val$msgList.get(position + 1);
/*  66 */         String url = item.getUrl();
/*  67 */         String action = "io.rong.imkit.intent.action.webview";
/*  68 */         Intent intent = new Intent(action);
/*  69 */         intent.setPackage(PublicServiceMultiRichContentMessageProvider.this.mContext.getPackageName());
/*  70 */         intent.putExtra("url", url);
/*  71 */         PublicServiceMultiRichContentMessageProvider.this.mContext.startActivity(intent);
/*     */       }
/*     */     });
/*  75 */     height = getListViewHeight(vh.lv) + vh.height;
/*  76 */     params.height = height;
/*     */ 
/*  78 */     v.setLayoutParams(params);
/*  79 */     v.requestLayout();
/*     */   }
/*     */ 
/*     */   private int getListViewHeight(ListView list) {
/*  83 */     int totalHeight = 0;
/*  84 */     View item = null;
/*     */ 
/*  86 */     ListAdapter adapter = list.getAdapter();
/*  87 */     for (int i = 0; i < adapter.getCount(); i++) {
/*  88 */       item = adapter.getView(i, null, list);
/*  89 */       item.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
/*     */ 
/*  91 */       totalHeight = totalHeight + item.getMeasuredHeight() + 2;
/*     */     }
/*     */ 
/*  94 */     return totalHeight;
/*     */   }
/*     */ 
/*     */   public Spannable getContentSummary(PublicServiceMultiRichContentMessage data)
/*     */   {
/*  99 */     List list = data.getMessages();
/* 100 */     if (list.size() > 0) {
/* 101 */       return new SpannableString(((RichContentItem)data.getMessages().get(0)).getTitle());
/*     */     }
/* 103 */     return null;
/*     */   }
/*     */ 
/*     */   public void onItemClick(View view, int position, PublicServiceMultiRichContentMessage content, UIMessage message)
/*     */   {
/* 109 */     if (content.getMessages().size() == 0) {
/* 110 */       return;
/*     */     }
/*     */ 
/* 117 */     String url = ((RichContentItem)content.getMessages().get(0)).getUrl();
/* 118 */     String action = "io.rong.imkit.intent.action.webview";
/* 119 */     Intent intent = new Intent(action);
/* 120 */     intent.setPackage(this.mContext.getPackageName());
/* 121 */     intent.putExtra("url", url);
/* 122 */     this.mContext.startActivity(intent);
/*     */   }
/*     */ 
/*     */   public void onItemLongClick(View view, int position, PublicServiceMultiRichContentMessage content, UIMessage message)
/*     */   {
/* 127 */     String name = null;
/* 128 */     if ((message.getConversationType().getName().equals(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName())) || (message.getConversationType().getName().equals(Conversation.ConversationType.PUBLIC_SERVICE.getName())))
/*     */     {
/* 130 */       if (message.getUserInfo() != null) {
/* 131 */         name = message.getUserInfo().getName();
/*     */       } else {
/* 133 */         Conversation.PublicServiceType publicServiceType = Conversation.PublicServiceType.setValue(message.getConversationType().getValue());
/* 134 */         PublicServiceProfile info = RongUserInfoManager.getInstance().getPublicServiceProfile(publicServiceType, message.getTargetId());
/*     */ 
/* 136 */         if (info != null)
/* 137 */           name = info.getName();
/*     */       }
/*     */     } else {
/* 140 */       UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
/* 141 */       if (userInfo != null) {
/* 142 */         name = userInfo.getName();
/*     */       }
/*     */     }
/*     */ 
/* 146 */     String[] items = { view.getContext().getResources().getString(R.string.rc_dialog_item_message_delete) };
/*     */ 
/* 148 */     ArraysDialogFragment.newInstance(name, items).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener(message)
/*     */     {
/*     */       public void OnArraysDialogItemClick(DialogInterface dialog, int which) {
/* 151 */         if (which == 0)
/* 152 */           RongIM.getInstance().deleteMessages(new int[] { this.val$message.getMessageId() }, null);
/*     */       }
/*     */     }).show(((FragmentActivity)view.getContext()).getSupportFragmentManager());
/*     */   }
/*     */ 
/*     */   public View newView(Context context, ViewGroup group)
/*     */   {
/* 168 */     this.mContext = context;
/* 169 */     ViewHolder holder = new ViewHolder(null);
/*     */ 
/* 171 */     View view = LayoutInflater.from(context).inflate(R.layout.rc_item_public_service_multi_rich_content_message, null);
/* 172 */     holder.lv = ((ListView)view.findViewById(R.id.rc_list));
/* 173 */     holder.iv = ((AsyncImageView)view.findViewById(R.id.rc_img));
/* 174 */     holder.tv = ((TextView)view.findViewById(R.id.rc_txt));
/* 175 */     view.measure(0, 0);
/* 176 */     holder.height = view.getMeasuredHeight();
/* 177 */     view.setTag(holder);
/*     */ 
/* 179 */     return view;
/*     */   }
/*     */   private class PublicAccountMsgAdapter extends BaseAdapter {
/*     */     LayoutInflater inflater;
/*     */     ArrayList<RichContentItem> itemList;
/*     */     int itemCount;
/*     */ 
/* 189 */     public PublicAccountMsgAdapter(ArrayList<RichContentItem> context) { this.inflater = LayoutInflater.from(context);
/* 190 */       this.itemList = new ArrayList();
/* 191 */       this.itemList.addAll(msgList);
/* 192 */       this.itemCount = (msgList.size() - 1);
/*     */     }
/*     */ 
/*     */     public int getCount()
/*     */     {
/* 197 */       return this.itemCount;
/*     */     }
/*     */ 
/*     */     public RichContentItem getItem(int position)
/*     */     {
/* 202 */       if (this.itemList.size() == 0) {
/* 203 */         return null;
/*     */       }
/* 205 */       return (RichContentItem)this.itemList.get(position + 1);
/*     */     }
/*     */ 
/*     */     public long getItemId(int position)
/*     */     {
/* 210 */       return 0L;
/*     */     }
/*     */ 
/*     */     public View getView(int position, View convertView, ViewGroup parent)
/*     */     {
/* 215 */       convertView = this.inflater.inflate(R.layout.rc_item_public_service_message, null);
/*     */ 
/* 217 */       AsyncImageView iv = (AsyncImageView)convertView.findViewById(R.id.rc_img);
/* 218 */       TextView tv = (TextView)convertView.findViewById(R.id.rc_txt);
/*     */ 
/* 220 */       if (this.itemList.size() == 0) {
/* 221 */         return null;
/*     */       }
/* 223 */       String title = ((RichContentItem)this.itemList.get(position + 1)).getTitle();
/* 224 */       if (title != null) {
/* 225 */         tv.setText(title);
/*     */       }
/* 227 */       iv.setResource(((RichContentItem)this.itemList.get(position + 1)).getImageUrl(), 0);
/* 228 */       return convertView;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ViewHolder
/*     */   {
/*     */     int height;
/*     */     TextView tv;
/*     */     AsyncImageView iv;
/*     */     ListView lv;
/*     */ 
/*     */     private ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.PublicServiceMultiRichContentMessageProvider
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.imkit.widget.provider;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.Intent;
/*     */ import android.content.res.Resources;
/*     */ import android.graphics.drawable.ColorDrawable;
/*     */ import android.graphics.drawable.Drawable;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.MotionEvent;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.View.OnTouchListener;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.LinearLayout;
/*     */ import android.widget.LinearLayout.LayoutParams;
/*     */ import android.widget.PopupWindow;
/*     */ import android.widget.TextView;
/*     */ import io.rong.imkit.IPublicServiceMenuClickListener;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.model.ConversationKey;
/*     */ import io.rong.imkit.widget.InputView;
/*     */ import io.rong.imlib.IRongCallback.ISendMessageCallback;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.model.Conversation;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.imlib.model.PublicServiceMenu;
/*     */ import io.rong.imlib.model.PublicServiceMenu.PublicServiceMenuItemType;
/*     */ import io.rong.imlib.model.PublicServiceMenuItem;
/*     */ import io.rong.imlib.model.PublicServiceProfile;
/*     */ import io.rong.message.PublicServiceCommandMessage;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class PublicServiceMenuInputProvider extends InputProvider.MainInputProvider
/*     */   implements View.OnTouchListener
/*     */ {
/*     */   Context mContext;
/*     */   Conversation conversation;
/*     */ 
/*     */   public PublicServiceMenuInputProvider(RongContext context)
/*     */   {
/*  42 */     super(context);
/*  43 */     this.mContext = context;
/*     */   }
/*     */ 
/*     */   public Drawable obtainSwitchDrawable(Context context)
/*     */   {
/*  48 */     return context.getResources().getDrawable(R.drawable.rc_ic_voice);
/*     */   }
/*     */ 
/*     */   public View onCreateView(LayoutInflater inflater, ViewGroup parent, InputView inputView)
/*     */   {
/*  54 */     this.conversation = getCurrentConversation();
/*  55 */     ConversationKey key = ConversationKey.obtain(this.conversation.getTargetId(), this.conversation.getConversationType());
/*  56 */     PublicServiceProfile info = RongContext.getInstance().getPublicServiceInfoFromCache(key.getKey());
/*     */ 
/*  58 */     if (info == null) {
/*  59 */       return parent;
/*     */     }
/*  61 */     PublicServiceMenu menu = info.getMenu();
/*  62 */     ArrayList items = menu.getMenuItems();
/*  63 */     if ((items == null) || (items.size() == 0)) {
/*  64 */       return parent;
/*     */     }
/*  66 */     parent.removeAllViews();
/*  67 */     for (PublicServiceMenuItem item : items) {
/*  68 */       LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.rc_item_public_service_input_menu, null);
/*  69 */       LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1, 1.0F);
/*  70 */       layout.setLayoutParams(lp);
/*     */ 
/*  72 */       TextView title = (TextView)layout.findViewById(R.id.rc_title);
/*  73 */       title.setText(item.getName());
/*  74 */       ImageView iv = (ImageView)layout.findViewById(R.id.rc_icon);
/*  75 */       if (item.getSubMenuItems().size() > 0) {
/*  76 */         iv.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.rc_ic_trangle));
/*     */       }
/*  78 */       layout.setOnClickListener(new View.OnClickListener(item)
/*     */       {
/*     */         public void onClick(View v)
/*     */         {
/*  82 */           if ((this.val$item.getSubMenuItems() == null) || (this.val$item.getSubMenuItems().size() == 0)) {
/*  83 */             PublicServiceMenuInputProvider.this.onMenuItemSelect(this.val$item);
/*     */           } else {
/*  85 */             PublicServiceMenuInputProvider.PopupMenu custom = new PublicServiceMenuInputProvider.PopupMenu(PublicServiceMenuInputProvider.this, PublicServiceMenuInputProvider.this.mContext, this.val$item.getSubMenuItems());
/*  86 */             custom.showAtLocation(v);
/*     */           }
/*     */         }
/*     */       });
/*  90 */       parent.addView(layout);
/*     */     }
/*  92 */     return parent;
/*     */   }
/*     */ 
/*     */   public void onActive(Context context)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onInactive(Context context)
/*     */   {
/*     */   }
/*     */ 
/*     */   private void onMenuItemSelect(PublicServiceMenuItem item)
/*     */   {
/* 106 */     if (item.getType().equals(PublicServiceMenu.PublicServiceMenuItemType.View)) {
/* 107 */       IPublicServiceMenuClickListener menuClickListener = RongContext.getInstance().getPublicServiceMenuClickListener();
/* 108 */       if ((menuClickListener == null) || (!menuClickListener.onClick(this.conversation.getConversationType(), this.conversation.getTargetId(), item))) {
/* 109 */         String action = "io.rong.imkit.intent.action.webview";
/* 110 */         Intent intent = new Intent(action);
/* 111 */         intent.setPackage(this.mContext.getPackageName());
/* 112 */         intent.addFlags(268435456);
/* 113 */         intent.putExtra("url", item.getUrl());
/* 114 */         this.mContext.startActivity(intent);
/*     */       }
/*     */     }
/*     */ 
/* 118 */     PublicServiceCommandMessage msg = PublicServiceCommandMessage.obtain(item);
/* 119 */     RongIMClient.getInstance().sendMessage(this.conversation.getConversationType(), this.conversation.getTargetId(), msg, null, null, new IRongCallback.ISendMessageCallback()
/*     */     {
/*     */       public void onAttached(Message message)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onSuccess(Message message)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onError(Message message, RongIMClient.ErrorCode errorCode)
/*     */       {
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void onSwitch(Context context)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean onTouch(View v, MotionEvent event)
/*     */   {
/* 217 */     return false;
/*     */   }
/*     */ 
/*     */   private class PopupMenu
/*     */   {
/*     */     PopupWindow popupWindow;
/*     */     View container;
/*     */     ArrayList<PublicServiceMenuItem> list;
/*     */ 
/*     */     public PopupMenu(ArrayList<PublicServiceMenuItem> context)
/*     */     {
/* 145 */       this.list = list;
/* 146 */       this.container = LayoutInflater.from(context).inflate(R.layout.rc_item_public_service_input_menus, null);
/* 147 */       LinearLayout group = (LinearLayout)this.container.findViewById(R.id.rc_layout);
/* 148 */       LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1, 1.0F);
/* 149 */       this.container.setLayoutParams(lp);
/*     */ 
/* 151 */       group.setClickable(true);
/* 152 */       group.setFocusable(true);
/*     */ 
/* 154 */       setupMenu(group);
/*     */ 
/* 156 */       this.popupWindow = new PopupWindow(this.container, -2, -2);
/*     */     }
/*     */ 
/*     */     public void showAtLocation(View parent) {
/* 160 */       this.popupWindow.setBackgroundDrawable(new ColorDrawable());
/* 161 */       this.container.measure(0, 0);
/*     */ 
/* 163 */       int[] location = new int[2];
/* 164 */       int w = this.container.getMeasuredWidth();
/* 165 */       parent.getLocationOnScreen(location);
/* 166 */       int x = location[0] + (parent.getWidth() - w) / 2;
/* 167 */       int y = parent.getHeight() + 10;
/*     */ 
/* 169 */       this.popupWindow.showAtLocation(parent, 83, x, y);
/* 170 */       this.popupWindow.setOutsideTouchable(true);
/* 171 */       this.popupWindow.setFocusable(true);
/* 172 */       this.popupWindow.update();
/*     */     }
/*     */ 
/*     */     public void dismiss() {
/* 176 */       this.popupWindow.dismiss();
/*     */     }
/*     */ 
/*     */     void setupMenu(LinearLayout group) {
/* 180 */       group.removeAllViews();
/* 181 */       for (int i = 0; i < this.list.size(); i++) {
/* 182 */         LinearLayout layoutItem = (LinearLayout)((LayoutInflater)PublicServiceMenuInputProvider.this.mContext.getSystemService("layout_inflater")).inflate(R.layout.rc_item_public_service_input_menu_item, null);
/* 183 */         LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2, 1.0F);
/* 184 */         this.container.setLayoutParams(lp);
/* 185 */         layoutItem.setFocusable(true);
/* 186 */         layoutItem.setTag(this.list.get(i));
/*     */ 
/* 188 */         TextView tv = (TextView)layoutItem.findViewById(R.id.rc_menu_item_text);
/* 189 */         View pop_item_line = layoutItem.findViewById(R.id.rc_menu_line);
/* 190 */         if (i + 1 == this.list.size()) {
/* 191 */           pop_item_line.setVisibility(8);
/*     */         }
/* 193 */         tv.setText(((PublicServiceMenuItem)this.list.get(i)).getName());
/* 194 */         layoutItem.setOnClickListener(new View.OnClickListener()
/*     */         {
/*     */           public void onClick(View v)
/*     */           {
/* 198 */             PublicServiceMenuItem item = (PublicServiceMenuItem)v.getTag();
/* 199 */             PublicServiceMenuInputProvider.this.onMenuItemSelect(item);
/* 200 */             PublicServiceMenuInputProvider.PopupMenu.this.dismiss();
/*     */           }
/*     */         });
/* 203 */         group.addView(layoutItem);
/*     */       }
/* 205 */       group.setVisibility(0);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.PublicServiceMenuInputProvider
 * JD-Core Version:    0.6.0
 */
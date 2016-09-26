/*     */ package io.rong.imkit.fragment;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.DialogInterface;
/*     */ import android.net.Uri;
/*     */ import android.os.Bundle;
/*     */ import android.os.Message;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.AdapterView;
/*     */ import android.widget.AdapterView.OnItemClickListener;
/*     */ import android.widget.AdapterView.OnItemLongClickListener;
/*     */ import android.widget.ListView;
/*     */ import android.widget.TextView;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.model.Event.PublicServiceFollowableEvent;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment;
/*     */ import io.rong.imkit.widget.ArraysDialogFragment.OnArraysDialogItemListener;
/*     */ import io.rong.imkit.widget.AsyncImageView;
/*     */ import io.rong.imkit.widget.adapter.BaseAdapter;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.OperationCallback;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Conversation.PublicServiceType;
/*     */ import io.rong.imlib.model.PublicServiceProfile;
/*     */ import io.rong.imlib.model.PublicServiceProfileList;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class PublicServiceSubscribeListFragment extends DispatchResultFragment
/*     */ {
/*     */   private ListView mListView;
/*     */   private PublicServiceListAdapter mAdapter;
/*     */ 
/*     */   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
/*     */   {
/*  38 */     View view = inflater.inflate(R.layout.rc_fr_public_service_sub_list, container, false);
/*  39 */     return view;
/*     */   }
/*     */ 
/*     */   public void onViewCreated(View view, Bundle savedInstanceState)
/*     */   {
/*  44 */     this.mListView = ((ListView)view.findViewById(R.id.rc_list));
/*     */ 
/*  46 */     this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
/*     */     {
/*     */       public void onItemClick(AdapterView<?> parent, View view, int position, long id)
/*     */       {
/*  50 */         PublicServiceProfile info = PublicServiceSubscribeListFragment.this.mAdapter.getItem(position);
/*     */ 
/*  52 */         RongIM.getInstance().startConversation(PublicServiceSubscribeListFragment.this.getActivity(), info.getConversationType(), info.getTargetId(), info.getName());
/*     */       }
/*     */     });
/*  56 */     this.mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
/*     */     {
/*     */       public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
/*     */       {
/*  60 */         String[] item = new String[1];
/*  61 */         PublicServiceProfile info = PublicServiceSubscribeListFragment.this.mAdapter.getItem(position);
/*  62 */         if (info.getConversationType() == Conversation.ConversationType.PUBLIC_SERVICE)
/*  63 */           item[0] = PublicServiceSubscribeListFragment.this.getActivity().getString(R.string.rc_pub_service_info_unfollow);
/*  64 */         ArraysDialogFragment.newInstance(info.getName(), item).setArraysDialogItemListener(new ArraysDialogFragment.OnArraysDialogItemListener(info, position)
/*     */         {
/*     */           public void OnArraysDialogItemClick(DialogInterface dialog, int which)
/*     */           {
/*  68 */             Conversation.PublicServiceType publicServiceType = null;
/*  69 */             if (this.val$info.getConversationType() == Conversation.ConversationType.APP_PUBLIC_SERVICE)
/*  70 */               publicServiceType = Conversation.PublicServiceType.APP_PUBLIC_SERVICE;
/*  71 */             else if (this.val$info.getConversationType() == Conversation.ConversationType.PUBLIC_SERVICE)
/*  72 */               publicServiceType = Conversation.PublicServiceType.PUBLIC_SERVICE;
/*     */             else {
/*  74 */               System.err.print("the public service type is error!!");
/*     */             }
/*  76 */             RongIMClient.getInstance().unsubscribePublicService(publicServiceType, this.val$info.getTargetId(), new RongIMClient.OperationCallback()
/*     */             {
/*     */               public void onSuccess() {
/*  79 */                 PublicServiceSubscribeListFragment.this.mAdapter.remove(PublicServiceSubscribeListFragment.2.1.this.val$position);
/*  80 */                 PublicServiceSubscribeListFragment.this.mAdapter.notifyDataSetChanged();
/*     */               }
/*     */ 
/*     */               public void onError(RongIMClient.ErrorCode errorCode)
/*     */               {
/*     */               }
/*     */             });
/*     */           }
/*     */         }).show(PublicServiceSubscribeListFragment.this.getFragmentManager());
/*     */ 
/*  90 */         return true;
/*     */       }
/*     */     });
/*  94 */     this.mAdapter = new PublicServiceListAdapter(getActivity());
/*  95 */     this.mListView.setAdapter(this.mAdapter);
/*     */ 
/*  97 */     getDBData();
/*     */ 
/*  99 */     RongContext.getInstance().getEventBus().register(this);
/*     */   }
/*     */ 
/*     */   private void getDBData()
/*     */   {
/* 105 */     RongIM.getInstance().getPublicServiceList(new RongIMClient.ResultCallback()
/*     */     {
/*     */       public void onSuccess(PublicServiceProfileList infoList) {
/* 108 */         for (PublicServiceProfile info : infoList.getPublicServiceData()) {
/* 109 */           RongUserInfoManager.getInstance().setPublicServiceProfile(info);
/*     */         }
/*     */ 
/* 112 */         PublicServiceSubscribeListFragment.this.mAdapter.clear();
/* 113 */         PublicServiceSubscribeListFragment.this.mAdapter.addCollection(infoList.getPublicServiceData());
/* 114 */         PublicServiceSubscribeListFragment.this.mAdapter.notifyDataSetChanged();
/*     */       }
/*     */ 
/*     */       public void onError(RongIMClient.ErrorCode e)
/*     */       {
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   protected void initFragment(Uri uri)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean handleMessage(Message msg)
/*     */   {
/* 131 */     return false;
/*     */   }
/*     */ 
/*     */   public void onEvent(Event.PublicServiceFollowableEvent event)
/*     */   {
/* 190 */     if (event != null)
/* 191 */       getDBData();
/*     */   }
/*     */ 
/*     */   public void onDestroyView()
/*     */   {
/* 198 */     RongContext.getInstance().getEventBus().unregister(this);
/* 199 */     super.onDestroyView();
/*     */   }
/*     */ 
/*     */   private class PublicServiceListAdapter extends BaseAdapter<PublicServiceProfile>
/*     */   {
/*     */     LayoutInflater mInflater;
/*     */ 
/*     */     public PublicServiceListAdapter(Context context)
/*     */     {
/* 139 */       this.mInflater = LayoutInflater.from(context);
/*     */     }
/*     */ 
/*     */     protected View newView(Context context, int position, ViewGroup group)
/*     */     {
/* 144 */       View view = this.mInflater.inflate(R.layout.rc_item_public_service_list, null);
/*     */ 
/* 146 */       ViewHolder viewHolder = new ViewHolder();
/* 147 */       viewHolder.portrait = ((AsyncImageView)view.findViewById(R.id.portrait));
/* 148 */       viewHolder.name = ((TextView)view.findViewById(R.id.name));
/* 149 */       viewHolder.introduction = ((TextView)view.findViewById(R.id.introduction));
/* 150 */       view.setTag(viewHolder);
/*     */ 
/* 152 */       return view;
/*     */     }
/*     */ 
/*     */     protected void bindView(View v, int position, PublicServiceProfile data)
/*     */     {
/* 157 */       ViewHolder viewHolder = (ViewHolder)v.getTag();
/*     */ 
/* 159 */       if (data != null) {
/* 160 */         viewHolder.portrait.setResource(data.getPortraitUri());
/* 161 */         viewHolder.name.setText(data.getName());
/* 162 */         viewHolder.introduction.setText(data.getIntroduction());
/*     */       }
/*     */     }
/*     */ 
/*     */     public int getCount()
/*     */     {
/* 168 */       return super.getCount();
/*     */     }
/*     */ 
/*     */     public PublicServiceProfile getItem(int position)
/*     */     {
/* 173 */       return (PublicServiceProfile)super.getItem(position);
/*     */     }
/*     */ 
/*     */     public long getItemId(int position)
/*     */     {
/* 178 */       return 0L;
/*     */     }
/*     */ 
/*     */     class ViewHolder
/*     */     {
/*     */       AsyncImageView portrait;
/*     */       TextView name;
/*     */       TextView introduction;
/*     */ 
/*     */       ViewHolder()
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.fragment.PublicServiceSubscribeListFragment
 * JD-Core Version:    0.6.0
 */
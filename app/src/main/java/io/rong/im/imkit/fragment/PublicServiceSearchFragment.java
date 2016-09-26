/*     */ package io.rong.imkit.fragment;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.Intent;
/*     */ import android.content.pm.ApplicationInfo;
/*     */ import android.content.res.Resources;
/*     */ import android.net.Uri;
/*     */ import android.net.Uri.Builder;
/*     */ import android.os.Bundle;
/*     */ import android.os.Message;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.AdapterView;
/*     */ import android.widget.AdapterView.OnItemClickListener;
/*     */ import android.widget.Button;
/*     */ import android.widget.EditText;
/*     */ import android.widget.ListView;
/*     */ import android.widget.TextView;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.model.Event.PublicServiceFollowableEvent;
/*     */ import io.rong.imkit.widget.AsyncImageView;
/*     */ import io.rong.imkit.widget.LoadingDialogFragment;
/*     */ import io.rong.imkit.widget.adapter.BaseAdapter;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.RongIMClient.SearchType;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.PublicServiceProfile;
/*     */ import io.rong.imlib.model.PublicServiceProfileList;
/*     */ 
/*     */ public class PublicServiceSearchFragment extends DispatchResultFragment
/*     */ {
/*     */   private static final String TAG = "PublicServiceSearchFragment";
/*     */   private EditText mEditText;
/*     */   private Button mSearchBtn;
/*     */   private ListView mListView;
/*     */   private PublicServiceListAdapter mAdapter;
/*     */   LoadingDialogFragment mLoadingDialogFragment;
/*     */ 
/*     */   protected void initFragment(Uri uri)
/*     */   {
/*     */   }
/*     */ 
/*     */   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
/*     */   {
/*  49 */     View view = inflater.inflate(R.layout.rc_fr_public_service_search, container, false);
/*     */ 
/*  51 */     this.mEditText = ((EditText)view.findViewById(R.id.rc_search_ed));
/*  52 */     this.mSearchBtn = ((Button)view.findViewById(R.id.rc_search_btn));
/*  53 */     this.mListView = ((ListView)view.findViewById(R.id.rc_search_list));
/*  54 */     RongContext.getInstance().getEventBus().register(this);
/*     */ 
/*  56 */     return view;
/*     */   }
/*     */ 
/*     */   public boolean handleMessage(Message msg)
/*     */   {
/*  61 */     return false;
/*     */   }
/*     */ 
/*     */   public void onViewCreated(View view, Bundle savedInstanceState)
/*     */   {
/*  66 */     super.onViewCreated(view, savedInstanceState);
/*     */ 
/*  68 */     this.mLoadingDialogFragment = LoadingDialogFragment.newInstance("", getResources().getString(R.string.rc_notice_data_is_loading));
/*     */ 
/*  70 */     this.mSearchBtn.setOnClickListener(new View.OnClickListener()
/*     */     {
/*     */       public void onClick(View v) {
/*  73 */         PublicServiceSearchFragment.this.mLoadingDialogFragment.show(PublicServiceSearchFragment.this.getFragmentManager());
/*     */ 
/*  75 */         RongIM.getInstance().searchPublicService(RongIMClient.SearchType.EXACT, PublicServiceSearchFragment.this.mEditText.getText().toString(), new RongIMClient.ResultCallback()
/*     */         {
/*     */           public void onError(RongIMClient.ErrorCode e)
/*     */           {
/*  79 */             PublicServiceSearchFragment.this.mLoadingDialogFragment.dismiss();
/*     */           }
/*     */ 
/*     */           public void onSuccess(PublicServiceProfileList list)
/*     */           {
/*  84 */             PublicServiceSearchFragment.this.mAdapter.clear();
/*  85 */             PublicServiceSearchFragment.this.mAdapter.addCollection(list.getPublicServiceData());
/*  86 */             PublicServiceSearchFragment.this.mAdapter.notifyDataSetChanged();
/*  87 */             PublicServiceSearchFragment.this.mLoadingDialogFragment.dismiss();
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*  93 */     this.mAdapter = new PublicServiceListAdapter(getActivity());
/*  94 */     this.mListView.setAdapter(this.mAdapter);
/*     */ 
/*  96 */     this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
/*     */     {
/*     */       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/*  99 */         PublicServiceProfile info = PublicServiceSearchFragment.this.mAdapter.getItem(position);
/*     */ 
/* 101 */         if (info.isFollow()) {
/* 102 */           RongIM.getInstance().startConversation(PublicServiceSearchFragment.this.getActivity(), info.getConversationType(), info.getTargetId(), info.getName());
/*     */         } else {
/* 104 */           Uri uri = Uri.parse("rong://" + view.getContext().getApplicationInfo().packageName).buildUpon().appendPath("publicServiceProfile").appendPath(info.getConversationType().getName().toLowerCase()).appendQueryParameter("targetId", info.getTargetId()).build();
/*     */ 
/* 107 */           Intent intent = new Intent("android.intent.action.VIEW", uri);
/*     */ 
/* 109 */           intent.putExtra("arg_public_account_info", info);
/* 110 */           PublicServiceSearchFragment.this.startActivity(intent);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void onDestroy() {
/* 118 */     RongContext.getInstance().getEventBus().unregister(this);
/* 119 */     super.onDestroy();
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.PublicServiceFollowableEvent event)
/*     */   {
/* 174 */     RLog.d("PublicServiceSearchFragment", "onEventMainThread PublicAccountIsFollowEvent, follow=" + event.isFollow());
/* 175 */     if (event != null)
/* 176 */       getActivity().finish();
/*     */   }
/*     */ 
/*     */   private class PublicServiceListAdapter extends BaseAdapter<PublicServiceProfile>
/*     */   {
/*     */     LayoutInflater mInflater;
/*     */ 
/*     */     public PublicServiceListAdapter(Context context)
/*     */     {
/* 127 */       this.mInflater = LayoutInflater.from(context);
/*     */     }
/*     */ 
/*     */     protected View newView(Context context, int position, ViewGroup group)
/*     */     {
/* 132 */       View view = this.mInflater.inflate(R.layout.rc_item_public_service_search, null);
/*     */ 
/* 134 */       ViewHolder viewHolder = new ViewHolder();
/* 135 */       viewHolder.portrait = ((AsyncImageView)view.findViewById(R.id.portrait));
/* 136 */       viewHolder.name = ((TextView)view.findViewById(R.id.name));
/* 137 */       view.setTag(viewHolder);
/*     */ 
/* 139 */       return view;
/*     */     }
/*     */ 
/*     */     protected void bindView(View v, int position, PublicServiceProfile data)
/*     */     {
/* 144 */       ViewHolder viewHolder = (ViewHolder)v.getTag();
/*     */ 
/* 146 */       if (data != null) {
/* 147 */         viewHolder.portrait.setResource(data.getPortraitUri());
/* 148 */         viewHolder.name.setText(data.getName());
/*     */       }
/*     */     }
/*     */ 
/*     */     public int getCount()
/*     */     {
/* 154 */       return super.getCount();
/*     */     }
/*     */ 
/*     */     public PublicServiceProfile getItem(int position)
/*     */     {
/* 159 */       return (PublicServiceProfile)super.getItem(position);
/*     */     }
/*     */ 
/*     */     public long getItemId(int position)
/*     */     {
/* 164 */       return 0L;
/*     */     }
/*     */ 
/*     */     class ViewHolder
/*     */     {
/*     */       AsyncImageView portrait;
/*     */       TextView name;
/*     */ 
/*     */       ViewHolder()
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.fragment.PublicServiceSearchFragment
 * JD-Core Version:    0.6.0
 */
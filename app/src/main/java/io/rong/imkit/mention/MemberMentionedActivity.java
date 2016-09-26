/*     */ package io.rong.imkit.mention;
/*     */ 
/*     */ import android.app.Activity;
/*     */ import android.content.Intent;
/*     */ import android.os.Bundle;
/*     */ import android.os.Handler;
/*     */ import android.text.Editable;
/*     */ import android.text.TextUtils;
/*     */ import android.text.TextWatcher;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.AdapterView;
/*     */ import android.widget.AdapterView.OnItemClickListener;
/*     */ import android.widget.BaseAdapter;
/*     */ import android.widget.EditText;
/*     */ import android.widget.ListView;
/*     */ import android.widget.SectionIndexer;
/*     */ import android.widget.TextView;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.RongIM.IGroupMemberCallback;
/*     */ import io.rong.imkit.RongIM.IGroupMembersProvider;
/*     */ import io.rong.imkit.tools.CharacterParser;
/*     */ import io.rong.imkit.userInfoCache.RongUserInfoManager;
/*     */ import io.rong.imkit.widget.AsyncImageView;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Discussion;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class MemberMentionedActivity extends Activity
/*     */ {
/*     */   private ListView mListView;
/*     */   private List<MemberInfo> mAllMemberList;
/*     */   private MembersAdapter mAdapter;
/*     */ 
/*     */   protected void onCreate(Bundle savedInstanceState)
/*     */   {
/*  42 */     super.onCreate(savedInstanceState);
/*  43 */     requestWindowFeature(1);
/*  44 */     setContentView(R.layout.rc_mention_members);
/*     */ 
/*  46 */     EditText searchBar = (EditText)findViewById(R.id.rc_edit_text);
/*  47 */     this.mListView = ((ListView)findViewById(R.id.rc_list));
/*  48 */     SideBar mSideBar = (SideBar)findViewById(R.id.rc_sidebar);
/*  49 */     TextView letterPopup = (TextView)findViewById(R.id.rc_popup_bg);
/*  50 */     mSideBar.setTextView(letterPopup);
/*     */ 
/*  52 */     this.mAdapter = new MembersAdapter();
/*  53 */     this.mListView.setAdapter(this.mAdapter);
/*  54 */     this.mAllMemberList = new ArrayList();
/*     */ 
/*  56 */     String targetId = getIntent().getStringExtra("targetId");
/*  57 */     Conversation.ConversationType conversationType = Conversation.ConversationType.setValue(getIntent().getIntExtra("conversationType", 0));
/*     */ 
/*  59 */     RongIM.IGroupMembersProvider groupMembersProvider = RongMentionManager.getInstance().getGroupMembersProvider();
/*  60 */     if ((conversationType.equals(Conversation.ConversationType.GROUP)) && (groupMembersProvider != null))
/*  61 */       groupMembersProvider.getGroupMembers(targetId, new RongIM.IGroupMemberCallback()
/*     */       {
/*     */         public void onGetGroupMembersResult(List<UserInfo> members) {
/*  64 */           if ((members != null) && (members.size() > 0) && (MemberMentionedActivity.this.mListView.getHandler() != null))
/*  65 */             MemberMentionedActivity.this.mListView.getHandler().post(new Runnable(members)
/*     */             {
/*     */               public void run() {
/*  68 */                 for (int i = 0; i < this.val$members.size(); i++) {
/*  69 */                   UserInfo userInfo = (UserInfo)this.val$members.get(i);
/*  70 */                   if ((userInfo != null) && (!userInfo.getUserId().equals(RongIMClient.getInstance().getCurrentUserId()))) {
/*  71 */                     MemberMentionedActivity.MemberInfo memberInfo = new MemberMentionedActivity.MemberInfo(MemberMentionedActivity.this, userInfo);
/*  72 */                     String sortString = "#";
/*     */ 
/*  74 */                     String pinyin = CharacterParser.getInstance().getSelling(userInfo.getName());
/*  75 */                     if (pinyin.length() > 0) {
/*  76 */                       sortString = pinyin.substring(0, 1).toUpperCase();
/*     */                     }
/*     */ 
/*  80 */                     if (sortString.matches("[A-Z]"))
/*  81 */                       memberInfo.setLetter(sortString.toUpperCase());
/*     */                     else {
/*  83 */                       memberInfo.setLetter("#");
/*     */                     }
/*  85 */                     MemberMentionedActivity.this.mAllMemberList.add(memberInfo);
/*     */                   }
/*     */                 }
/*  88 */                 Collections.sort(MemberMentionedActivity.this.mAllMemberList, MemberMentionedActivity.PinyinComparator.getInstance());
/*  89 */                 MemberMentionedActivity.this.mAdapter.setData(MemberMentionedActivity.this.mAllMemberList);
/*  90 */                 MemberMentionedActivity.this.mAdapter.notifyDataSetChanged();
/*     */               } } );
/*     */         }
/*     */       });
/*  96 */     else if (conversationType.equals(Conversation.ConversationType.DISCUSSION))
/*  97 */       RongIMClient.getInstance().getDiscussion(targetId, new RongIMClient.ResultCallback()
/*     */       {
/*     */         public void onSuccess(Discussion discussion) {
/* 100 */           List memeberIds = discussion.getMemberIdList();
/* 101 */           for (String id : memeberIds) {
/* 102 */             UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(id);
/* 103 */             if ((userInfo != null) && (!userInfo.getUserId().equals(RongIMClient.getInstance().getCurrentUserId()))) {
/* 104 */               MemberMentionedActivity.MemberInfo memberInfo = new MemberMentionedActivity.MemberInfo(MemberMentionedActivity.this, userInfo);
/*     */ 
/* 106 */               String pinyin = CharacterParser.getInstance().getSelling(userInfo.getName());
/* 107 */               String sortString = pinyin.substring(0, 1).toUpperCase();
/*     */ 
/* 110 */               if (sortString.matches("[A-Z]"))
/* 111 */                 memberInfo.setLetter(sortString.toUpperCase());
/*     */               else {
/* 113 */                 memberInfo.setLetter("#");
/*     */               }
/* 115 */               MemberMentionedActivity.this.mAllMemberList.add(memberInfo);
/*     */             }
/*     */           }
/*     */ 
/* 119 */           Collections.sort(MemberMentionedActivity.this.mAllMemberList, MemberMentionedActivity.PinyinComparator.getInstance());
/* 120 */           MemberMentionedActivity.this.mAdapter.setData(MemberMentionedActivity.this.mAllMemberList);
/* 121 */           MemberMentionedActivity.this.mAdapter.notifyDataSetChanged();
/*     */         }
/*     */ 
/*     */         public void onError(RongIMClient.ErrorCode e)
/*     */         {
/*     */         }
/*     */       });
/* 131 */     this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
/*     */     {
/*     */       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/* 134 */         MemberMentionedActivity.this.finish();
/* 135 */         MemberMentionedActivity.MemberInfo item = MemberMentionedActivity.this.mAdapter.getItem(position);
/* 136 */         if ((item != null) && (item.userInfo != null))
/* 137 */           RongMentionManager.getInstance().mentionMember(item.userInfo);
/*     */       }
/*     */     });
/* 143 */     mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener()
/*     */     {
/*     */       public void onTouchingLetterChanged(String s)
/*     */       {
/* 147 */         int position = MemberMentionedActivity.this.mAdapter.getPositionForSection(s.charAt(0));
/* 148 */         if (position != -1)
/* 149 */           MemberMentionedActivity.this.mListView.setSelection(position);
/*     */       }
/*     */     });
/* 154 */     searchBar.addTextChangedListener(new TextWatcher()
/*     */     {
/*     */       public void beforeTextChanged(CharSequence s, int start, int count, int after)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onTextChanged(CharSequence s, int start, int before, int count)
/*     */       {
/* 163 */         List filterDataList = new ArrayList();
/*     */ 
/* 165 */         if (TextUtils.isEmpty(s.toString())) {
/* 166 */           filterDataList = MemberMentionedActivity.this.mAllMemberList;
/*     */         } else {
/* 168 */           filterDataList.clear();
/* 169 */           for (MemberMentionedActivity.MemberInfo member : MemberMentionedActivity.this.mAllMemberList) {
/* 170 */             String name = member.userInfo.getName();
/* 171 */             if ((name.contains(s)) || (CharacterParser.getInstance().getSelling(name).startsWith(s.toString()))) {
/* 172 */               filterDataList.add(member);
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 177 */         Collections.sort(filterDataList, MemberMentionedActivity.PinyinComparator.getInstance());
/* 178 */         MemberMentionedActivity.this.mAdapter.setData(filterDataList);
/* 179 */         MemberMentionedActivity.this.mAdapter.notifyDataSetChanged();
/*     */       }
/*     */ 
/*     */       public void afterTextChanged(Editable s)
/*     */       {
/*     */       }
/*     */     });
/* 188 */     findViewById(R.id.rc_btn_cancel).setOnClickListener(new View.OnClickListener()
/*     */     {
/*     */       public void onClick(View v) {
/* 191 */         MemberMentionedActivity.this.finish();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static class PinyinComparator
/*     */     implements Comparator<MemberMentionedActivity.MemberInfo>
/*     */   {
/* 300 */     public static PinyinComparator instance = null;
/*     */ 
/*     */     public static PinyinComparator getInstance() {
/* 303 */       if (instance == null) {
/* 304 */         instance = new PinyinComparator();
/*     */       }
/* 306 */       return instance;
/*     */     }
/*     */ 
/*     */     public int compare(MemberMentionedActivity.MemberInfo o1, MemberMentionedActivity.MemberInfo o2) {
/* 310 */       if ((o1.getLetter().equals("@")) || (o2.getLetter().equals("#")))
/* 311 */         return -1;
/* 312 */       if ((o1.getLetter().equals("#")) || (o2.getLetter().equals("@"))) {
/* 313 */         return 1;
/*     */       }
/* 315 */       return o1.getLetter().compareTo(o2.getLetter());
/*     */     }
/*     */   }
/*     */ 
/*     */   private class MemberInfo
/*     */   {
/*     */     UserInfo userInfo;
/*     */     String letter;
/*     */ 
/*     */     MemberInfo(UserInfo userInfo)
/*     */     {
/* 285 */       this.userInfo = userInfo;
/*     */     }
/*     */ 
/*     */     public void setLetter(String letter) {
/* 289 */       this.letter = letter;
/*     */     }
/*     */ 
/*     */     public String getLetter() {
/* 293 */       return this.letter;
/*     */     }
/*     */   }
/*     */ 
/*     */   class ViewHolder
/*     */   {
/*     */     AsyncImageView portrait;
/*     */     TextView name;
/*     */     TextView letter;
/*     */ 
/*     */     ViewHolder()
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   class MembersAdapter extends BaseAdapter
/*     */     implements SectionIndexer
/*     */   {
/* 197 */     private List<MemberMentionedActivity.MemberInfo> mList = new ArrayList();
/*     */ 
/*     */     MembersAdapter() {  }
/*     */ 
/* 200 */     public void setData(List<MemberMentionedActivity.MemberInfo> list) { this.mList = list;
/*     */     }
/*     */ 
/*     */     public int getCount()
/*     */     {
/* 205 */       return this.mList.size();
/*     */     }
/*     */ 
/*     */     public MemberMentionedActivity.MemberInfo getItem(int position)
/*     */     {
/* 210 */       return (MemberMentionedActivity.MemberInfo)this.mList.get(position);
/*     */     }
/*     */ 
/*     */     public long getItemId(int position)
/*     */     {
/* 215 */       return 0L;
/*     */     }
/*     */ 
/*     */     public View getView(int position, View convertView, ViewGroup parent)
/*     */     {
/*     */       MemberMentionedActivity.ViewHolder viewHolder;
/* 221 */       if (convertView == null) {
/* 222 */         MemberMentionedActivity.ViewHolder viewHolder = new MemberMentionedActivity.ViewHolder(MemberMentionedActivity.this);
/* 223 */         convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_mention_list_item, null);
/* 224 */         viewHolder.name = ((TextView)convertView.findViewById(R.id.rc_user_name));
/* 225 */         viewHolder.portrait = ((AsyncImageView)convertView.findViewById(R.id.rc_user_portrait));
/* 226 */         viewHolder.letter = ((TextView)convertView.findViewById(R.id.letter));
/* 227 */         convertView.setTag(viewHolder);
/*     */       } else {
/* 229 */         viewHolder = (MemberMentionedActivity.ViewHolder)convertView.getTag();
/*     */       }
/* 231 */       UserInfo userInfo = ((MemberMentionedActivity.MemberInfo)this.mList.get(position)).userInfo;
/* 232 */       if (userInfo != null) {
/* 233 */         viewHolder.name.setText(userInfo.getName());
/* 234 */         viewHolder.portrait.setAvatar(userInfo.getPortraitUri());
/*     */       }
/*     */ 
/* 238 */       int section = getSectionForPosition(position);
/*     */ 
/* 240 */       if (position == getPositionForSection(section)) {
/* 241 */         viewHolder.letter.setVisibility(0);
/* 242 */         viewHolder.letter.setText(((MemberMentionedActivity.MemberInfo)this.mList.get(position)).getLetter());
/*     */       } else {
/* 244 */         viewHolder.letter.setVisibility(8);
/*     */       }
/*     */ 
/* 247 */       return convertView;
/*     */     }
/*     */ 
/*     */     public Object[] getSections()
/*     */     {
/* 252 */       return new Object[0];
/*     */     }
/*     */ 
/*     */     public int getPositionForSection(int sectionIndex)
/*     */     {
/* 257 */       for (int i = 0; i < getCount(); i++) {
/* 258 */         String sortStr = ((MemberMentionedActivity.MemberInfo)this.mList.get(i)).getLetter();
/* 259 */         char firstChar = sortStr.toUpperCase().charAt(0);
/* 260 */         if (firstChar == sectionIndex) {
/* 261 */           return i;
/*     */         }
/*     */       }
/*     */ 
/* 265 */       return -1;
/*     */     }
/*     */ 
/*     */     public int getSectionForPosition(int position)
/*     */     {
/* 270 */       return ((MemberMentionedActivity.MemberInfo)this.mList.get(position)).getLetter().charAt(0);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.mention.MemberMentionedActivity
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.imkit.widget.adapter;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import io.rong.imkit.mention.IMemberMentionedListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ 
/*     */ public abstract class BaseAdapter<T> extends android.widget.BaseAdapter
/*     */ {
/*     */   private Context mContext;
/*     */   private List<T> mList;
/*     */   protected IMemberMentionedListener mMentionMemberSelectListener;
/*     */ 
/*     */   public BaseAdapter()
/*     */   {
/*  22 */     this.mList = new ArrayList();
/*     */   }
/*     */ 
/*     */   public BaseAdapter(Context context) {
/*  26 */     this.mContext = context;
/*  27 */     this.mList = new ArrayList();
/*     */   }
/*     */ 
/*     */   public void setOnItemClickListener(IMemberMentionedListener mentionMemberSelectListener) {
/*  31 */     this.mMentionMemberSelectListener = mentionMemberSelectListener;
/*     */   }
/*     */ 
/*     */   protected <T extends View> T findViewById(View view, int id)
/*     */   {
/*  36 */     return view.findViewById(id);
/*     */   }
/*     */ 
/*     */   public int findPosition(T message) {
/*  40 */     int index = getCount();
/*  41 */     int position = -1;
/*  42 */     while (index-- > 0) {
/*  43 */       if (message.equals(getItem(index))) {
/*  44 */         position = index;
/*     */       }
/*     */     }
/*     */ 
/*  48 */     return position;
/*     */   }
/*     */ 
/*     */   public int findPosition(long id) {
/*  52 */     int index = getCount();
/*  53 */     int position = -1;
/*  54 */     while (index-- > 0) {
/*  55 */       if (getItemId(index) == id) {
/*  56 */         position = index;
/*     */       }
/*     */     }
/*     */ 
/*  60 */     return position;
/*     */   }
/*     */ 
/*     */   public void addCollection(Collection<T> collection) {
/*  64 */     this.mList.addAll(collection);
/*     */   }
/*     */ 
/*     */   public void addCollection(T[] collection)
/*     */   {
/*  69 */     for (Object t : collection)
/*  70 */       this.mList.add(t);
/*     */   }
/*     */ 
/*     */   public void add(T t)
/*     */   {
/*  75 */     this.mList.add(t);
/*     */   }
/*     */ 
/*     */   public void add(T t, int position) {
/*  79 */     this.mList.add(position, t);
/*     */   }
/*     */ 
/*     */   public void remove(int position) {
/*  83 */     this.mList.remove(position);
/*     */   }
/*     */ 
/*     */   public void removeAll() {
/*  87 */     this.mList.clear();
/*     */   }
/*     */ 
/*     */   public void clear() {
/*  91 */     this.mList.clear();
/*     */   }
/*     */ 
/*     */   public int getCount()
/*     */   {
/*  96 */     if (this.mList == null) {
/*  97 */       return 0;
/*     */     }
/*  99 */     return this.mList.size();
/*     */   }
/*     */ 
/*     */   public T getItem(int position)
/*     */   {
/* 104 */     if (this.mList == null) {
/* 105 */       return null;
/*     */     }
/* 107 */     if (position >= this.mList.size()) {
/* 108 */       return null;
/*     */     }
/* 110 */     return this.mList.get(position);
/*     */   }
/*     */ 
/*     */   public View getView(int position, View convertView, ViewGroup parent)
/*     */   {
/*     */     View view;
/*     */     View view;
/* 116 */     if (convertView != null)
/* 117 */       view = convertView;
/*     */     else {
/* 119 */       view = newView(this.mContext, position, parent);
/*     */     }
/* 121 */     bindView(view, position, getItem(position));
/* 122 */     return view;
/*     */   }
/*     */ 
/*     */   protected abstract View newView(Context paramContext, int paramInt, ViewGroup paramViewGroup);
/*     */ 
/*     */   protected abstract void bindView(View paramView, int paramInt, T paramT);
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.adapter.BaseAdapter
 * JD-Core Version:    0.6.0
 */
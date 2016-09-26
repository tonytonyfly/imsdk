/*     */ package io.rong.imkit.widget;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.BaseAdapter;
/*     */ import android.widget.CheckBox;
/*     */ import android.widget.TextView;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class SingleChoiceAdapter<T> extends BaseAdapter
/*     */ {
/*     */   private Context mContext;
/*  21 */   private List<T> mObjects = new ArrayList();
/*  22 */   private int mCheckBoxResourceID = 0;
/*  23 */   private int mSelectItem = -1;
/*     */   private LayoutInflater mInflater;
/*     */ 
/*     */   public SingleChoiceAdapter(Context context, int checkBoxResourceId)
/*     */   {
/*  28 */     init(context, checkBoxResourceId);
/*     */   }
/*     */ 
/*     */   public SingleChoiceAdapter(Context context, List<T> objects, int checkBoxResourceId)
/*     */   {
/*  33 */     init(context, checkBoxResourceId);
/*  34 */     if (objects != null)
/*  35 */       this.mObjects = objects;
/*     */   }
/*     */ 
/*     */   private void init(Context context, int checkBoResourceId)
/*     */   {
/*  41 */     this.mContext = context;
/*  42 */     this.mInflater = ((LayoutInflater)context.getSystemService("layout_inflater"));
/*     */ 
/*  44 */     this.mCheckBoxResourceID = checkBoResourceId;
/*     */   }
/*     */ 
/*     */   public void refreshData(List<T> objects) {
/*  48 */     if (objects != null) {
/*  49 */       this.mObjects = objects;
/*  50 */       setSelectItem(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setSelectItem(int selectItem) {
/*  55 */     if ((selectItem >= 0) && (selectItem < this.mObjects.size())) {
/*  56 */       this.mSelectItem = selectItem;
/*  57 */       notifyDataSetChanged();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getSelectItem()
/*     */   {
/*  63 */     return this.mSelectItem;
/*     */   }
/*     */ 
/*     */   public void clear() {
/*  67 */     this.mObjects.clear();
/*  68 */     notifyDataSetChanged();
/*     */   }
/*     */ 
/*     */   public int getCount() {
/*  72 */     return this.mObjects.size();
/*     */   }
/*     */ 
/*     */   public T getItem(int position) {
/*  76 */     return this.mObjects.get(position);
/*     */   }
/*     */ 
/*     */   public int getPosition(T item) {
/*  80 */     return this.mObjects.indexOf(item);
/*     */   }
/*     */ 
/*     */   public long getItemId(int position) {
/*  84 */     return position;
/*     */   }
/*     */ 
/*     */   public View getView(int position, View convertView, ViewGroup parent)
/*     */   {
/*     */     ViewHolder viewHolder;
/*  91 */     if (convertView == null) {
/*  92 */       convertView = this.mInflater.inflate(R.layout.rc_cs_item_single_choice, null);
/*     */ 
/*  94 */       ViewHolder viewHolder = new ViewHolder();
/*  95 */       viewHolder.mTextView = ((TextView)convertView.findViewById(R.id.rc_cs_tv_group_name));
/*     */ 
/*  97 */       viewHolder.mCheckBox = ((CheckBox)convertView.findViewById(R.id.rc_cs_group_checkBox));
/*     */ 
/*  99 */       convertView.setTag(viewHolder);
/*     */ 
/* 101 */       if (this.mCheckBoxResourceID != 0)
/* 102 */         viewHolder.mCheckBox.setButtonDrawable(this.mCheckBoxResourceID);
/*     */     }
/*     */     else
/*     */     {
/* 106 */       viewHolder = (ViewHolder)convertView.getTag();
/*     */     }
/*     */ 
/* 109 */     viewHolder.mCheckBox.setChecked(this.mSelectItem == position);
/*     */ 
/* 111 */     Object item = getItem(position);
/* 112 */     if ((item instanceof CharSequence))
/* 113 */       viewHolder.mTextView.setText((CharSequence)item);
/*     */     else {
/* 115 */       viewHolder.mTextView.setText(item.toString());
/*     */     }
/*     */ 
/* 118 */     return convertView;
/*     */   }
/*     */ 
/*     */   public static class ViewHolder
/*     */   {
/*     */     public TextView mTextView;
/*     */     public CheckBox mCheckBox;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.SingleChoiceAdapter
 * JD-Core Version:    0.6.0
 */
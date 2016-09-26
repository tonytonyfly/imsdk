/*     */ package io.rong.imkit.widget;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.graphics.drawable.ColorDrawable;
/*     */ import android.support.v4.view.PagerAdapter;
/*     */ import android.support.v4.view.ViewPager;
/*     */ import android.support.v4.view.ViewPager.OnPageChangeListener;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.AdapterView;
/*     */ import android.widget.AdapterView.OnItemClickListener;
/*     */ import android.widget.BaseAdapter;
/*     */ import android.widget.GridView;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.LinearLayout;
/*     */ import android.widget.LinearLayout.LayoutParams;
/*     */ import android.widget.TextView;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.widget.provider.InputProvider.ExtendProvider;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import java.util.List;
/*     */ 
/*     */ public class RongPluginPager
/*     */ {
/*     */   public static final int PLUGIN_PER_PAGE = 8;
/*     */   private ViewPager mViewPager;
/*     */   private LinearLayout mIndicator;
/*     */   private int mPageCount;
/*     */   private int mSelectedPage;
/*     */   private Conversation.ConversationType conversationType;
/* 105 */   private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener()
/*     */   {
/*     */     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/* 108 */       List extendProviders = RongContext.getInstance().getRegisteredExtendProviderList(RongPluginPager.this.conversationType);
/* 109 */       InputProvider.ExtendProvider provider = (InputProvider.ExtendProvider)extendProviders.get(position + RongPluginPager.this.mSelectedPage * 8);
/* 110 */       provider.onPluginClick(view);
/*     */     }
/* 105 */   };
/*     */ 
/*     */   public RongPluginPager(Conversation.ConversationType conversationType, ViewGroup viewGroup)
/*     */   {
/*  36 */     this.conversationType = conversationType;
/*  37 */     initView(viewGroup.getContext(), viewGroup);
/*  38 */     initData(viewGroup);
/*  39 */     initIndicator(this.mPageCount, this.mIndicator);
/*  40 */     this.mViewPager.setCurrentItem(0, false);
/*     */   }
/*     */ 
/*     */   private void initView(Context context, ViewGroup viewGroup) {
/*  44 */     View view = LayoutInflater.from(context).inflate(R.layout.rc_input_pager_layout, viewGroup);
/*  45 */     this.mViewPager = ((ViewPager)view.findViewById(R.id.rc_view_pager));
/*  46 */     this.mIndicator = ((LinearLayout)view.findViewById(R.id.rc_indicator));
/*  47 */     this.mViewPager.setAdapter(new PluginViewPagerAdapter(null));
/*  48 */     this.mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
/*     */     {
/*     */       public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onPageSelected(int position)
/*     */       {
/*  56 */         RongPluginPager.this.onIndicatorChanged(RongPluginPager.this.mSelectedPage, position);
/*  57 */         RongPluginPager.access$102(RongPluginPager.this, position);
/*     */       }
/*     */ 
/*     */       public void onPageScrollStateChanged(int state)
/*     */       {
/*     */       }
/*     */     });
/*  65 */     this.mViewPager.setOffscreenPageLimit(1);
/*     */   }
/*     */ 
/*     */   private void initData(ViewGroup viewGroup)
/*     */   {
/*  70 */     List extendProviders = RongContext.getInstance().getRegisteredExtendProviderList(this.conversationType);
/*  71 */     this.mPageCount = (int)Math.ceil(extendProviders.size() / 8.0F);
/*     */ 
/*  73 */     this.mViewPager.getAdapter().notifyDataSetChanged();
/*     */   }
/*     */ 
/*     */   private void initIndicator(int pages, LinearLayout indicator) {
/*  77 */     for (int i = 0; i < pages; i++) {
/*  78 */       ImageView imageView = new ImageView(indicator.getContext());
/*  79 */       LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(16, 16);
/*  80 */       layoutParams.gravity = 17;
/*  81 */       layoutParams.setMargins(0, 0, 20, 0);
/*  82 */       imageView.setLayoutParams(layoutParams);
/*  83 */       imageView.setImageResource(R.drawable.rc_indicator);
/*  84 */       indicator.addView(imageView);
/*     */     }
/*  86 */     if (pages < 2)
/*  87 */       indicator.setVisibility(4);
/*     */   }
/*     */ 
/*     */   private void onIndicatorChanged(int pre, int cur)
/*     */   {
/*  92 */     int count = this.mIndicator.getChildCount();
/*  93 */     if ((count > 0) && (pre < count) && (cur < count)) {
/*  94 */       if (pre >= 0) {
/*  95 */         ImageView preView = (ImageView)this.mIndicator.getChildAt(pre);
/*  96 */         preView.setImageResource(R.drawable.rc_indicator);
/*     */       }
/*  98 */       if (cur >= 0) {
/*  99 */         ImageView curView = (ImageView)this.mIndicator.getChildAt(cur);
/* 100 */         curView.setImageResource(R.drawable.rc_indicator_hover);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class PluginItemAdapter extends BaseAdapter
/*     */   {
/*     */     int startIndex;
/*     */     List<InputProvider.ExtendProvider> extendProviders;
/*     */ 
/*     */     public PluginItemAdapter(int startIndex)
/*     */     {
/* 152 */       this.startIndex = startIndex;
/* 153 */       this.extendProviders = RongContext.getInstance().getRegisteredExtendProviderList(RongPluginPager.this.conversationType);
/*     */     }
/*     */ 
/*     */     public int getCount()
/*     */     {
/* 158 */       int count = this.extendProviders.size() - this.startIndex;
/* 159 */       count = Math.min(count, 8);
/* 160 */       return count;
/*     */     }
/*     */ 
/*     */     public Object getItem(int position)
/*     */     {
/* 166 */       List extendProviders = RongContext.getInstance().getRegisteredExtendProviderList(RongPluginPager.this.conversationType);
/* 167 */       return extendProviders.get(position + this.startIndex);
/*     */     }
/*     */ 
/*     */     public View getView(int position, View convertView, ViewGroup parent)
/*     */     {
/* 172 */       if (convertView == null) {
/* 173 */         convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_wi_plugins, null);
/*     */       }
/* 175 */       ImageView imageView = (ImageView)convertView.findViewById(16908294);
/* 176 */       TextView textView = (TextView)convertView.findViewById(16908310);
/* 177 */       if (this.startIndex + position < this.extendProviders.size()) {
/* 178 */         InputProvider.ExtendProvider provider = (InputProvider.ExtendProvider)this.extendProviders.get(this.startIndex + position);
/* 179 */         imageView.setImageDrawable(provider.obtainPluginDrawable(parent.getContext()));
/* 180 */         textView.setText(provider.obtainPluginTitle(parent.getContext()));
/*     */       }
/* 182 */       return convertView;
/*     */     }
/*     */ 
/*     */     public long getItemId(int position)
/*     */     {
/* 187 */       return this.startIndex + position;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class PluginViewPagerAdapter extends PagerAdapter
/*     */   {
/*     */     private PluginViewPagerAdapter()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int getCount()
/*     */     {
/* 117 */       return RongPluginPager.this.mPageCount == 0 ? 1 : RongPluginPager.this.mPageCount;
/*     */     }
/*     */ 
/*     */     public Object instantiateItem(ViewGroup container, int position)
/*     */     {
/* 122 */       GridView gridView = (GridView)LayoutInflater.from(container.getContext()).inflate(R.layout.rc_plugin_gridview, null);
/* 123 */       gridView.setAdapter(new RongPluginPager.PluginItemAdapter(RongPluginPager.this, position * 8));
/* 124 */       gridView.setOnItemClickListener(RongPluginPager.this.itemClickListener);
/* 125 */       gridView.setSelector(new ColorDrawable(0));
/* 126 */       container.addView(gridView);
/* 127 */       return gridView;
/*     */     }
/*     */ 
/*     */     public void destroyItem(ViewGroup container, int position, Object object)
/*     */     {
/* 132 */       View layout = (View)object;
/* 133 */       container.removeView(layout);
/*     */     }
/*     */ 
/*     */     public boolean isViewFromObject(View view, Object object)
/*     */     {
/* 138 */       return view == object;
/*     */     }
/*     */ 
/*     */     public int getItemPosition(Object object)
/*     */     {
/* 143 */       return -2;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.RongPluginPager
 * JD-Core Version:    0.6.0
 */
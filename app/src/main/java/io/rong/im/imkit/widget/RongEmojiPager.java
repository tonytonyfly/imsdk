/*     */ package io.rong.imkit.widget;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.support.v4.view.PagerAdapter;
/*     */ import android.support.v4.view.ViewPager;
/*     */ import android.support.v4.view.ViewPager.OnPageChangeListener;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.AdapterView;
/*     */ import android.widget.AdapterView.OnItemClickListener;
/*     */ import android.widget.GridView;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.LinearLayout;
/*     */ import android.widget.LinearLayout.LayoutParams;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.model.Emoji;
/*     */ import io.rong.imkit.utils.AndroidEmoji;
/*     */ import io.rong.imkit.widget.adapter.EmojiAdapter;
/*     */ import java.util.List;
/*     */ 
/*     */ public class RongEmojiPager
/*     */ {
/*     */   public static final int EMOJI_PER_PAGE = 20;
/*     */   private Context mContext;
/*     */   private ViewPager mViewPager;
/*     */   private LinearLayout mIndicator;
/*     */   private int mPageCount;
/*     */   private int mSelectedPage;
/*  62 */   private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener()
/*     */   {
/*     */     public void onItemClick(AdapterView<?> parent, View view, int position, long id)
/*     */     {
/*  66 */       String key = null;
/*  67 */       int index = position + RongEmojiPager.this.mSelectedPage * 20;
/*  68 */       if (position == 20) {
/*  69 */         key = "/DEL";
/*     */       } else {
/*  71 */         List emojis = AndroidEmoji.getEmojiList();
/*  72 */         if (index >= emojis.size()) {
/*  73 */           if (RongEmojiPager.this.mSelectedPage == RongEmojiPager.this.mPageCount - 1)
/*  74 */             key = "/DEL";
/*     */         }
/*     */         else {
/*  77 */           int code = ((Emoji)emojis.get(index)).getCode();
/*  78 */           char[] chars = Character.toChars(code);
/*  79 */           key = Character.toString(chars[0]);
/*  80 */           for (int i = 1; i < chars.length; i++) {
/*  81 */             key = key + Character.toString(chars[i]);
/*     */           }
/*     */         }
/*     */       }
/*  85 */       if (RongEmojiPager.this.clickListener != null)
/*  86 */         RongEmojiPager.this.clickListener.onEmojiClick(key);
/*     */     }
/*  62 */   };
/*     */   private OnEmojiClickListener clickListener;
/*     */ 
/*     */   public RongEmojiPager(ViewGroup viewGroup)
/*     */   {
/*  32 */     this.mContext = viewGroup.getContext();
/*  33 */     View view = LayoutInflater.from(this.mContext).inflate(R.layout.rc_input_pager_layout, viewGroup);
/*  34 */     this.mViewPager = ((ViewPager)view.findViewById(R.id.rc_view_pager));
/*  35 */     this.mIndicator = ((LinearLayout)view.findViewById(R.id.rc_indicator));
/*     */ 
/*  37 */     this.mPageCount = (int)Math.ceil(AndroidEmoji.getEmojiList().size() / 20.0F);
/*  38 */     this.mViewPager.setAdapter(new EmoticonViewPagerAdapter(null));
/*  39 */     this.mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
/*     */     {
/*     */       public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onPageSelected(int position)
/*     */       {
/*  47 */         RongEmojiPager.this.onIndicatorChanged(RongEmojiPager.this.mSelectedPage, position);
/*  48 */         RongEmojiPager.access$102(RongEmojiPager.this, position);
/*     */       }
/*     */ 
/*     */       public void onPageScrollStateChanged(int state)
/*     */       {
/*     */       }
/*     */     });
/*  56 */     this.mViewPager.setCurrentItem(0, false);
/*  57 */     this.mViewPager.setOffscreenPageLimit(1);
/*  58 */     initIndicator(this.mPageCount, this.mIndicator);
/*  59 */     onIndicatorChanged(-1, 0);
/*     */   }
/*     */ 
/*     */   public void setOnEmojiClickListener(OnEmojiClickListener clickListener)
/*     */   {
/*  93 */     this.clickListener = clickListener;
/*     */   }
/*     */ 
/*     */   private void initIndicator(int pages, LinearLayout indicator)
/*     */   {
/* 136 */     for (int i = 0; i < pages; i++) {
/* 137 */       ImageView imageView = new ImageView(this.mContext);
/* 138 */       LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(16, 16);
/* 139 */       layoutParams.gravity = 17;
/* 140 */       layoutParams.setMargins(0, 0, 20, 0);
/* 141 */       imageView.setLayoutParams(layoutParams);
/* 142 */       imageView.setImageResource(R.drawable.rc_indicator);
/* 143 */       indicator.addView(imageView);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void onIndicatorChanged(int pre, int cur) {
/* 148 */     int count = this.mIndicator.getChildCount();
/* 149 */     if ((count > 0) && (pre < count) && (cur < count)) {
/* 150 */       if (pre >= 0) {
/* 151 */         ImageView preView = (ImageView)this.mIndicator.getChildAt(pre);
/* 152 */         preView.setImageResource(R.drawable.rc_indicator);
/*     */       }
/* 154 */       if (cur >= 0) {
/* 155 */         ImageView curView = (ImageView)this.mIndicator.getChildAt(cur);
/* 156 */         curView.setImageResource(R.drawable.rc_indicator_hover);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class EmoticonViewPagerAdapter extends PagerAdapter
/*     */   {
/*     */     private EmoticonViewPagerAdapter()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Object instantiateItem(ViewGroup container, int position)
/*     */     {
/* 105 */       RongEmojiPager.this.mIndicator.setVisibility(0);
/* 106 */       GridView gridView = (GridView)LayoutInflater.from(container.getContext()).inflate(R.layout.rc_emoji_gridview, null);
/* 107 */       gridView.setOnItemClickListener(RongEmojiPager.this.onItemClickListener);
/* 108 */       gridView.setAdapter(new EmojiAdapter(RongEmojiPager.this.mContext, position * 20));
/* 109 */       container.addView(gridView);
/* 110 */       return gridView;
/*     */     }
/*     */ 
/*     */     public int getItemPosition(Object object)
/*     */     {
/* 115 */       return -2;
/*     */     }
/*     */ 
/*     */     public int getCount()
/*     */     {
/* 120 */       return RongEmojiPager.this.mPageCount == 0 ? 1 : RongEmojiPager.this.mPageCount;
/*     */     }
/*     */ 
/*     */     public boolean isViewFromObject(View view, Object object)
/*     */     {
/* 125 */       return view == object;
/*     */     }
/*     */ 
/*     */     public void destroyItem(ViewGroup container, int position, Object object)
/*     */     {
/* 130 */       View layout = (View)object;
/* 131 */       container.removeView(layout);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface OnEmojiClickListener
/*     */   {
/*     */     public abstract void onEmojiClick(String paramString);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.RongEmojiPager
 * JD-Core Version:    0.6.0
 */
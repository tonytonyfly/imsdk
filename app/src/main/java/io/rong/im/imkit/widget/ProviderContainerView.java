/*     */ package io.rong.imkit.widget;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.util.AttributeSet;
/*     */ import android.view.View;
/*     */ import android.widget.FrameLayout;
/*     */ import android.widget.FrameLayout.LayoutParams;
/*     */ import io.rong.imkit.widget.provider.IContainerItemProvider;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ 
/*     */ public class ProviderContainerView extends FrameLayout
/*     */ {
/*     */   Map<Class<? extends IContainerItemProvider>, AtomicInteger> mViewCounterMap;
/*     */   Map<Class<? extends IContainerItemProvider>, View> mContentViewMap;
/*     */   View mInflateView;
/*  22 */   int mMaxContainSize = 3;
/*     */ 
/*     */   public ProviderContainerView(Context context, AttributeSet attrs) {
/*  25 */     super(context, attrs);
/*     */ 
/*  27 */     if (!isInEditMode())
/*  28 */       init(attrs);
/*     */   }
/*     */ 
/*     */   private void init(AttributeSet attrs)
/*     */   {
/*  33 */     this.mViewCounterMap = new HashMap();
/*  34 */     this.mContentViewMap = new HashMap();
/*     */   }
/*     */ 
/*     */   public <T extends IContainerItemProvider> View inflate(T t)
/*     */   {
/*  39 */     View result = null;
/*     */ 
/*  41 */     if (this.mInflateView != null) {
/*  42 */       this.mInflateView.setVisibility(8);
/*     */     }
/*  44 */     if (this.mContentViewMap.containsKey(t.getClass())) {
/*  45 */       result = (View)this.mContentViewMap.get(t.getClass());
/*  46 */       this.mInflateView = result;
/*  47 */       ((AtomicInteger)this.mViewCounterMap.get(t.getClass())).incrementAndGet();
/*     */     }
/*     */ 
/*  50 */     if (result != null) {
/*  51 */       if (result.getVisibility() == 8) {
/*  52 */         result.setVisibility(0);
/*     */       }
/*  54 */       return result;
/*     */     }
/*     */ 
/*  57 */     recycle();
/*     */ 
/*  59 */     result = t.newView(getContext(), this);
/*     */ 
/*  61 */     if (result != null) {
/*  62 */       super.addView(result);
/*  63 */       this.mContentViewMap.put(t.getClass(), result);
/*  64 */       this.mViewCounterMap.put(t.getClass(), new AtomicInteger());
/*     */     }
/*     */ 
/*  68 */     this.mInflateView = result;
/*     */ 
/*  71 */     return result;
/*     */   }
/*     */ 
/*     */   public View getCurrentInflateView() {
/*  75 */     return this.mInflateView;
/*     */   }
/*     */ 
/*     */   public void containerViewLeft() {
/*  79 */     if (this.mInflateView == null)
/*  80 */       return;
/*  81 */     FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)this.mInflateView.getLayoutParams();
/*  82 */     params.gravity = 19;
/*     */   }
/*     */ 
/*     */   public void containerViewRight() {
/*  86 */     if (this.mInflateView == null)
/*  87 */       return;
/*  88 */     FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)this.mInflateView.getLayoutParams();
/*  89 */     params.gravity = 21;
/*     */   }
/*     */ 
/*     */   public void containerViewCenter() {
/*  93 */     if (this.mInflateView == null) {
/*  94 */       return;
/*     */     }
/*  96 */     FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)this.mInflateView.getLayoutParams();
/*  97 */     params.gravity = 17;
/*     */   }
/*     */ 
/*     */   private void recycle() {
/* 101 */     if (this.mInflateView == null) {
/* 102 */       return;
/*     */     }
/* 104 */     int count = getChildCount();
/* 105 */     if (count >= this.mMaxContainSize) {
/* 106 */       Map.Entry min = null;
/*     */ 
/* 108 */       for (Map.Entry item : this.mViewCounterMap.entrySet()) {
/* 109 */         if (min == null) {
/* 110 */           min = item;
/*     */         }
/* 112 */         min = ((AtomicInteger)min.getValue()).get() > ((AtomicInteger)item.getValue()).get() ? item : min;
/*     */       }
/*     */ 
/* 115 */       this.mViewCounterMap.remove(min.getKey());
/* 116 */       View view = (View)this.mContentViewMap.remove(min.getKey());
/* 117 */       removeView(view);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.ProviderContainerView
 * JD-Core Version:    0.6.0
 */
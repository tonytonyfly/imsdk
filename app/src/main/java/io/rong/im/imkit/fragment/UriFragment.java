/*     */ package io.rong.imkit.fragment;
/*     */ 
/*     */ import android.content.Intent;
/*     */ import android.net.Uri;
/*     */ import android.os.Bundle;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.view.View;
/*     */ import io.rong.common.RLog;
/*     */ 
/*     */ public abstract class UriFragment extends BaseFragment
/*     */ {
/*     */   private static final String TAG = "UriFragment";
/*     */   private Uri mUri;
/*     */   public static final String RONG_URI = "RONG_URI";
/*     */   IActionBarHandler mBarHandler;
/*     */ 
/*     */   protected Bundle obtainUriBundle(Uri uri)
/*     */   {
/*  19 */     Bundle args = new Bundle();
/*  20 */     args.putParcelable("RONG_URI", uri);
/*  21 */     return args;
/*     */   }
/*     */ 
/*     */   public void onCreate(Bundle savedInstanceState)
/*     */   {
/*  33 */     super.onCreate(savedInstanceState);
/*     */ 
/*  35 */     if (this.mUri == null)
/*  36 */       if (savedInstanceState == null)
/*  37 */         this.mUri = getActivity().getIntent().getData();
/*     */       else
/*  39 */         this.mUri = ((Uri)savedInstanceState.getParcelable("RONG_URI"));
/*     */   }
/*     */ 
/*     */   public void onViewCreated(View view, Bundle savedInstanceState)
/*     */   {
/*  46 */     super.onViewCreated(view, savedInstanceState);
/*  47 */     if (getUri() != null)
/*  48 */       initFragment(getUri());
/*     */   }
/*     */ 
/*     */   public void onResume()
/*     */   {
/*  53 */     super.onResume();
/*     */   }
/*     */ 
/*     */   public void onPause()
/*     */   {
/*  58 */     super.onPause();
/*     */   }
/*     */ 
/*     */   public void onViewStateRestored(Bundle savedInstanceState)
/*     */   {
/*  63 */     RLog.d("UriFragment", "onViewStateRestored");
/*  64 */     super.onViewStateRestored(savedInstanceState);
/*     */   }
/*     */ 
/*     */   public void onSaveInstanceState(Bundle outState)
/*     */   {
/*  69 */     outState.putParcelable("RONG_URI", getUri());
/*  70 */     super.onSaveInstanceState(outState);
/*     */   }
/*     */ 
/*     */   public void onRestoreUI()
/*     */   {
/*  75 */     if (getUri() != null)
/*  76 */       initFragment(getUri());
/*     */   }
/*     */ 
/*     */   public void setActionBarHandler(IActionBarHandler mBarHandler) {
/*  80 */     this.mBarHandler = mBarHandler;
/*     */   }
/*     */ 
/*     */   protected IActionBarHandler getActionBarHandler() {
/*  84 */     return this.mBarHandler;
/*     */   }
/*     */ 
/*     */   public Uri getUri() {
/*  88 */     return this.mUri;
/*     */   }
/*     */ 
/*     */   public void setUri(Uri uri) {
/*  92 */     this.mUri = uri;
/*     */ 
/*  94 */     if ((this.mUri != null) && (isVisible()))
/*  95 */       initFragment(this.mUri);
/*     */   }
/*     */ 
/*     */   protected abstract void initFragment(Uri paramUri);
/*     */ 
/*     */   public boolean onBackPressed() {
/* 102 */     return false;
/*     */   }
/*     */ 
/*     */   protected static abstract interface IActionBarHandler
/*     */   {
/*     */     public abstract void onTitleChanged(CharSequence paramCharSequence);
/*     */ 
/*     */     public abstract void onUnreadCountChanged(int paramInt);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.fragment.UriFragment
 * JD-Core Version:    0.6.0
 */
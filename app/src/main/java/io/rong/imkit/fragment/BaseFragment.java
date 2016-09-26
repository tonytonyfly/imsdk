/*     */ package io.rong.imkit.fragment;
/*     */ 
/*     */ import android.graphics.drawable.Drawable;
/*     */ import android.os.Bundle;
/*     */ import android.os.Handler;
/*     */ import android.os.Handler.Callback;
/*     */ import android.os.Message;
/*     */ import android.support.v4.app.Fragment;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.TextView;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.RongIMClient.ConnectCallback;
/*     */ import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ 
/*     */ public abstract class BaseFragment extends Fragment
/*     */   implements Handler.Callback
/*     */ {
/*     */   private static final String TAG = "BaseFragment";
/*     */   public static final String TOKEN = "RONG_TOKEN";
/*     */   public static final int UI_RESTORE = 1;
/*     */   private Handler mHandler;
/*     */   Thread mThread;
/*     */   private LayoutInflater mInflater;
/*     */ 
/*     */   public void onCreate(Bundle savedInstanceState)
/*     */   {
/*  29 */     String token = null;
/*     */ 
/*  31 */     this.mHandler = new Handler(this);
/*  32 */     this.mThread = Thread.currentThread();
/*     */ 
/*  34 */     if (savedInstanceState != null) {
/*  35 */       token = savedInstanceState.getString("RONG_TOKEN");
/*     */     }
/*  37 */     if ((token != null) && (!RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED))) {
/*  38 */       RLog.i("BaseFragment", "onCreate auto reconnect");
/*  39 */       RongIM.connect(token, new RongIMClient.ConnectCallback()
/*     */       {
/*     */         public void onSuccess(String s) {
/*  42 */           BaseFragment.this.mHandler.sendEmptyMessage(1);
/*     */         }
/*     */ 
/*     */         public void onError(RongIMClient.ErrorCode e)
/*     */         {
/*  47 */           RLog.e("BaseFragment", "onError(...) ErrorCode:" + e);
/*     */         }
/*     */ 
/*     */         public void onTokenIncorrect()
/*     */         {
/*  52 */           RLog.e("BaseFragment", "onTokenIncorrect() onTokenIncorrect");
/*     */         }
/*     */       });
/*     */     }
/*  57 */     super.onCreate(savedInstanceState);
/*     */   }
/*     */ 
/*     */   public void onViewCreated(View view, Bundle savedInstanceState)
/*     */   {
/*  62 */     this.mInflater = LayoutInflater.from(view.getContext());
/*     */ 
/*  64 */     super.onViewCreated(view, savedInstanceState);
/*     */   }
/*     */ 
/*     */   protected <T extends View> T findViewById(View view, int id)
/*     */   {
/*  70 */     return view.findViewById(id);
/*     */   }
/*     */ 
/*     */   public void onSaveInstanceState(Bundle outState)
/*     */   {
/*  75 */     outState.putString("RONG_TOKEN", RongContext.getInstance().getToken());
/*  76 */     super.onSaveInstanceState(outState);
/*     */   }
/*     */ 
/*     */   public void onDestroy()
/*     */   {
/*  81 */     super.onDestroy();
/*     */   }
/*     */ 
/*     */   protected Handler getHandler() {
/*  85 */     return this.mHandler;
/*     */   }
/*     */   public abstract boolean onBackPressed();
/*     */ 
/*     */   public abstract void onRestoreUI();
/*     */ 
/*  93 */   private View obtainView(LayoutInflater inflater, int color, Drawable drawable, CharSequence notice) { View view = inflater.inflate(R.layout.rc_wi_notice, null);
/*  94 */     ((TextView)view.findViewById(16908299)).setText(notice);
/*  95 */     ((ImageView)view.findViewById(16908294)).setImageDrawable(drawable);
/*  96 */     if (color > 0) {
/*  97 */       view.setBackgroundColor(color);
/*     */     }
/*  99 */     return view; }
/*     */ 
/*     */   private View obtainView(LayoutInflater inflater, int color, int res, CharSequence notice)
/*     */   {
/* 103 */     View view = inflater.inflate(R.layout.rc_wi_notice, null);
/* 104 */     ((TextView)view.findViewById(16908299)).setText(notice);
/* 105 */     ((ImageView)view.findViewById(16908294)).setImageResource(res);
/*     */ 
/* 107 */     view.setBackgroundColor(color);
/* 108 */     return view;
/*     */   }
/*     */ 
/*     */   public boolean handleMessage(Message msg)
/*     */   {
/* 114 */     switch (msg.what) {
/*     */     case 1:
/* 116 */       onRestoreUI();
/* 117 */       break;
/*     */     }
/*     */ 
/* 121 */     return true;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.fragment.BaseFragment
 * JD-Core Version:    0.6.0
 */
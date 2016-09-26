/*     */ package io.rong.imkit.tools;
/*     */ 
/*     */ import android.app.Activity;
/*     */ import android.content.Intent;
/*     */ import android.net.Uri;
/*     */ import android.os.Bundle;
/*     */ import android.view.KeyEvent;
/*     */ import android.webkit.WebChromeClient;
/*     */ import android.webkit.WebSettings;
/*     */ import android.webkit.WebView;
/*     */ import android.webkit.WebViewClient;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.ProgressBar;
/*     */ import android.widget.RelativeLayout;
/*     */ import android.widget.TextView;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.common.RongWebView;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ 
/*     */ public class RongWebviewActivity extends Activity
/*     */ {
/*     */   private static final String TAG = "RongWebviewActivity";
/*     */   private String mPrevUrl;
/*     */   private RongWebView mWebView;
/*     */   private ProgressBar mProgressBar;
/*     */   protected RelativeLayout mWebViewTitle;
/*     */   protected TextView mLeftBack;
/*     */   protected ImageView mLeftImage;
/*     */ 
/*     */   protected void onCreate(Bundle savedInstanceState)
/*     */   {
/*  34 */     super.onCreate(savedInstanceState);
/*  35 */     setContentView(R.layout.rc_ac_webview);
/*  36 */     Intent intent = getIntent();
/*  37 */     this.mWebView = ((RongWebView)findViewById(R.id.rc_webview));
/*  38 */     this.mProgressBar = ((ProgressBar)findViewById(R.id.rc_web_progressbar));
/*  39 */     this.mWebViewTitle = ((RelativeLayout)findViewById(R.id.rc_webview_title));
/*  40 */     this.mLeftBack = ((TextView)findViewById(R.id.rc_left_textview));
/*  41 */     this.mLeftImage = ((ImageView)findViewById(R.id.rc_left_image));
/*  42 */     this.mWebView.setVerticalScrollbarOverlay(true);
/*  43 */     this.mWebView.getSettings().setLoadWithOverviewMode(true);
/*  44 */     this.mWebView.getSettings().setJavaScriptEnabled(true);
/*  45 */     this.mWebView.getSettings().setUseWideViewPort(true);
/*  46 */     this.mWebView.getSettings().setBuiltInZoomControls(true);
/*  47 */     this.mWebView.getSettings().setSupportZoom(false);
/*  48 */     this.mWebView.getSettings().setUseWideViewPort(true);
/*  49 */     this.mWebView.setWebViewClient(new RongWebviewClient(null));
/*  50 */     this.mWebView.setWebChromeClient(new RongWebChromeClient(null));
/*     */ 
/*  52 */     String url = intent.getStringExtra("url");
/*  53 */     Uri data = intent.getData();
/*  54 */     if (url != null) {
/*  55 */       this.mPrevUrl = url;
/*  56 */       this.mWebView.loadUrl(url);
/*  57 */     } else if (data != null) {
/*  58 */       this.mPrevUrl = data.toString();
/*  59 */       this.mWebView.loadUrl(data.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean onKeyDown(int keyCode, KeyEvent event)
/*     */   {
/* 114 */     if ((keyCode == 4) && (this.mWebView.canGoBack())) {
/* 115 */       this.mWebView.goBack();
/* 116 */       return true;
/*     */     }
/* 118 */     return super.onKeyDown(keyCode, event);
/*     */   }
/*     */ 
/*     */   private class RongWebChromeClient extends WebChromeClient
/*     */   {
/*     */     private RongWebChromeClient()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void onProgressChanged(WebView view, int newProgress)
/*     */     {
/* 100 */       if (newProgress == 100) {
/* 101 */         RongWebviewActivity.this.mProgressBar.setVisibility(8);
/*     */       } else {
/* 103 */         if (RongWebviewActivity.this.mProgressBar.getVisibility() == 8) {
/* 104 */           RongWebviewActivity.this.mProgressBar.setVisibility(0);
/*     */         }
/* 106 */         RongWebviewActivity.this.mProgressBar.setProgress(newProgress);
/*     */       }
/* 108 */       super.onProgressChanged(view, newProgress);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class RongWebviewClient extends WebViewClient
/*     */   {
/*     */     private RongWebviewClient()
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean shouldOverrideUrlLoading(WebView view, String url)
/*     */     {
/*  67 */       if (RongWebviewActivity.this.mPrevUrl != null) {
/*  68 */         if (!RongWebviewActivity.this.mPrevUrl.equals(url)) {
/*  69 */           if ((!url.toLowerCase().startsWith("http://")) && (!url.toLowerCase().startsWith("https://"))) {
/*  70 */             Intent intent = new Intent("android.intent.action.VIEW");
/*  71 */             Uri content_url = Uri.parse(url);
/*  72 */             intent.setData(content_url);
/*  73 */             intent.setFlags(268435456);
/*  74 */             intent.setFlags(536870912);
/*     */             try {
/*  76 */               RongWebviewActivity.this.startActivity(intent);
/*     */             } catch (Exception e) {
/*  78 */               RLog.e("RongWebviewActivity", "not apps install for this intent =" + e.toString());
/*  79 */               e.printStackTrace();
/*     */             }
/*  81 */             return true;
/*     */           }
/*  83 */           RongWebviewActivity.access$202(RongWebviewActivity.this, url);
/*  84 */           RongWebviewActivity.this.mWebView.loadUrl(url);
/*  85 */           return true;
/*     */         }
/*  87 */         return false;
/*     */       }
/*     */ 
/*  90 */       RongWebviewActivity.access$202(RongWebviewActivity.this, url);
/*  91 */       RongWebviewActivity.this.mWebView.loadUrl(url);
/*  92 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.tools.RongWebviewActivity
 * JD-Core Version:    0.6.0
 */
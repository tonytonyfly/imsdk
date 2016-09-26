/*     */ package io.rong.imkit.widget.provider;
/*     */ 
/*     */ import android.app.Activity;
/*     */ import android.content.Intent;
/*     */ import android.content.res.Configuration;
/*     */ import android.net.Uri;
/*     */ import android.os.Bundle;
/*     */ import android.os.Environment;
/*     */ import android.util.Log;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.widget.Button;
/*     */ import android.widget.ImageView;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.message.utils.BitmapUtil;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class TakingPicturesActivity extends Activity
/*     */   implements View.OnClickListener
/*     */ {
/*     */   private static final String TAG = "TakingPicturesActivity";
/*     */   private static final int REQUEST_CAMERA = 2;
/*     */   private ImageView mImage;
/*     */   private Uri mSavedPicUri;
/*     */ 
/*     */   protected void onCreate(Bundle savedInstanceState)
/*     */   {
/*  34 */     super.onCreate(savedInstanceState);
/*  35 */     requestWindowFeature(1);
/*  36 */     setContentView(R.layout.rc_ac_camera);
/*  37 */     Button cancel = (Button)findViewById(R.id.rc_back);
/*  38 */     Button send = (Button)findViewById(R.id.rc_send);
/*  39 */     this.mImage = ((ImageView)findViewById(R.id.rc_img));
/*  40 */     cancel.setOnClickListener(this);
/*  41 */     send.setOnClickListener(this);
/*     */ 
/*  43 */     RLog.d("TakingPicturesActivity", "onCreate savedInstanceState : " + savedInstanceState);
/*     */ 
/*  45 */     if (savedInstanceState == null) {
/*  46 */       startCamera();
/*     */     } else {
/*  48 */       String str = savedInstanceState.getString("photo_uri");
/*  49 */       if (str != null) {
/*  50 */         this.mSavedPicUri = Uri.parse(str);
/*     */         try {
/*  52 */           this.mImage.setImageBitmap(BitmapUtil.getResizedBitmap(this, this.mSavedPicUri, 960, 960));
/*     */         } catch (IOException e) {
/*  54 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onConfigurationChanged(Configuration newConfig)
/*     */   {
/*  62 */     super.onConfigurationChanged(newConfig);
/*     */   }
/*     */ 
/*     */   public void onClick(View v)
/*     */   {
/*  67 */     File file = new File(this.mSavedPicUri.getPath());
/*     */ 
/*  69 */     if (!file.exists()) {
/*  70 */       finish();
/*     */     }
/*     */ 
/*  73 */     if (v.getId() == R.id.rc_send) {
/*  74 */       if (this.mSavedPicUri != null) {
/*  75 */         Intent data = new Intent();
/*  76 */         data.setData(this.mSavedPicUri);
/*  77 */         setResult(-1, data);
/*     */       }
/*  79 */       finish();
/*  80 */     } else if (v.getId() == R.id.rc_back) {
/*  81 */       finish();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void startCamera() {
/*  86 */     Intent intent = new Intent();
/*  87 */     intent.setAction("android.media.action.IMAGE_CAPTURE");
/*  88 */     File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
/*  89 */     if (!path.exists())
/*  90 */       path.mkdirs();
/*  91 */     String name = System.currentTimeMillis() + ".jpg";
/*     */ 
/*  93 */     File file = new File(path, name);
/*  94 */     this.mSavedPicUri = Uri.fromFile(file);
/*  95 */     RLog.d("TakingPicturesActivity", "startCamera output pic uri =" + this.mSavedPicUri);
/*     */ 
/*  97 */     intent.putExtra("output", this.mSavedPicUri);
/*  98 */     intent.addCategory("android.intent.category.DEFAULT");
/*     */     try
/*     */     {
/* 101 */       startActivityForResult(intent, 2);
/*     */     } catch (SecurityException e) {
/* 103 */       Log.e("TakingPicturesActivity", "REQUEST_CAMERA SecurityException!!!");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onActivityResult(int requestCode, int resultCode, Intent data)
/*     */   {
/* 110 */     RLog.d("TakingPicturesActivity", "onActivityResult resultCode = " + resultCode + ", intent=" + data);
/*     */ 
/* 112 */     if (resultCode != -1) {
/* 113 */       finish();
/* 114 */       return;
/*     */     }
/*     */ 
/* 117 */     switch (requestCode) {
/*     */     case 2:
/* 119 */       if (resultCode == 0) {
/* 120 */         finish();
/* 121 */         Log.e("TakingPicturesActivity", "RESULT_CANCELED");
/*     */       }
/*     */ 
/* 124 */       if ((this.mSavedPicUri == null) || (resultCode != -1)) break;
/*     */       try {
/* 126 */         this.mImage.setImageBitmap(BitmapUtil.getResizedBitmap(this, this.mSavedPicUri, 960, 960));
/*     */       } catch (IOException e) {
/* 128 */         e.printStackTrace();
/*     */       }
/*     */ 
/*     */     default:
/* 133 */       return;
/*     */     }
/*     */ 
/* 136 */     super.onActivityResult(requestCode, resultCode, data);
/*     */   }
/*     */ 
/*     */   protected void onRestoreInstanceState(Bundle savedInstanceState)
/*     */   {
/* 142 */     Log.e("TakingPicturesActivity", "onRestoreInstanceState");
/* 143 */     this.mSavedPicUri = Uri.parse(savedInstanceState.getString("photo_uri"));
/* 144 */     super.onRestoreInstanceState(savedInstanceState);
/*     */   }
/*     */ 
/*     */   public void onSaveInstanceState(Bundle outState)
/*     */   {
/* 150 */     Log.e("TakingPicturesActivity", "onSaveInstanceState");
/* 151 */     outState.putString("photo_uri", this.mSavedPicUri.toString());
/* 152 */     super.onSaveInstanceState(outState);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.TakingPicturesActivity
 * JD-Core Version:    0.6.0
 */
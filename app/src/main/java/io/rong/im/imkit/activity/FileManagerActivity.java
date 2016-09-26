/*     */ package io.rong.imkit.activity;
/*     */ 
/*     */ import android.app.Activity;
/*     */ import android.content.Intent;
/*     */ import android.os.Bundle;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.Window;
/*     */ import android.widget.LinearLayout;
/*     */ import android.widget.TextView;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.utils.FileTypeUtils;
/*     */ import java.util.HashSet;
/*     */ 
/*     */ public class FileManagerActivity extends Activity
/*     */   implements View.OnClickListener
/*     */ {
/*     */   private static final int REQUEST_FOR_SELECTED_FILES = 730;
/*     */   private static final int RESULT_SELECTED_FILES_TO_SEND = 731;
/*     */   private static final int ALL_FILE_FILES = 1;
/*     */   private static final int ALL_VIDEO_FILES = 2;
/*     */   private static final int ALL_AUDIO_FILES = 3;
/*     */   private static final int ALL_OTHER_FILES = 4;
/*     */   private static final int ALL_RAM_FILES = 5;
/*     */   private static final int ALL_SD_FILES = 6;
/*     */   private static final int ROOT_DIR = 100;
/*     */   private static final int SD_CARD_ROOT_DIR = 101;
/*     */   private static final int FILE_TRAVERSE_TYPE_ONE = 200;
/*     */   private static final int FILE_TRAVERSE_TYPE_TWO = 201;
/*     */   private LinearLayout mGoBackLinearLayout;
/*     */   private TextView mFileTextView;
/*     */   private TextView mVideoTextView;
/*     */   private TextView mAudioTextView;
/*     */   private TextView mOtherTextView;
/*     */   private TextView mMobileMemoryTextView;
/*     */   private TextView mSDCardTextView;
/*     */   private LinearLayout mSDCardLinearLayout;
/*     */   private String mSDCardPath;
/*     */ 
/*     */   protected void onCreate(Bundle savedInstanceState)
/*     */   {
/*  51 */     super.onCreate(savedInstanceState);
/*  52 */     getWindow().setFlags(2048, 2048);
/*     */ 
/*  54 */     requestWindowFeature(1);
/*  55 */     setContentView(R.layout.rc_ac_file_manager);
/*     */ 
/*  57 */     this.mGoBackLinearLayout = ((LinearLayout)findViewById(R.id.rc_ac_ll_go_back));
/*  58 */     this.mFileTextView = ((TextView)findViewById(R.id.rc_ac_tv_file_manager_file));
/*  59 */     this.mVideoTextView = ((TextView)findViewById(R.id.rc_ac_tv_file_manager_video));
/*  60 */     this.mAudioTextView = ((TextView)findViewById(R.id.rc_ac_tv_file_manager_audio));
/*  61 */     this.mOtherTextView = ((TextView)findViewById(R.id.rc_ac_tv_file_manager_picture));
/*  62 */     this.mMobileMemoryTextView = ((TextView)findViewById(R.id.rc_ac_tv_file_manager_mobile_memory));
/*  63 */     this.mSDCardTextView = ((TextView)findViewById(R.id.rc_ac_tv_file_manager_SD_card));
/*  64 */     this.mSDCardLinearLayout = ((LinearLayout)findViewById(R.id.rc_ac_ll_sd_card));
/*     */ 
/*  66 */     this.mGoBackLinearLayout.setOnClickListener(this);
/*  67 */     this.mFileTextView.setOnClickListener(this);
/*  68 */     this.mVideoTextView.setOnClickListener(this);
/*  69 */     this.mAudioTextView.setOnClickListener(this);
/*  70 */     this.mOtherTextView.setOnClickListener(this);
/*  71 */     this.mMobileMemoryTextView.setOnClickListener(this);
/*  72 */     this.mSDCardTextView.setOnClickListener(this);
/*     */ 
/*  74 */     this.mSDCardPath = FileTypeUtils.getInstance().getSDCardPath();
/*  75 */     if (this.mSDCardPath != null)
/*  76 */       this.mSDCardLinearLayout.setVisibility(0);
/*     */   }
/*     */ 
/*     */   public void onClick(View v)
/*     */   {
/*  82 */     if (v == this.mGoBackLinearLayout) {
/*  83 */       finish();
/*     */     } else {
/*  85 */       Intent intent = new Intent(this, FileListActivity.class);
/*  86 */       if (v == this.mFileTextView) {
/*  87 */         intent.putExtra("rootDirType", 100);
/*  88 */         intent.putExtra("fileFilterType", 1);
/*  89 */         intent.putExtra("fileTraverseType", 200);
/*     */       }
/*  91 */       if (v == this.mVideoTextView) {
/*  92 */         intent.putExtra("rootDirType", 100);
/*  93 */         intent.putExtra("fileFilterType", 2);
/*  94 */         intent.putExtra("fileTraverseType", 200);
/*     */       }
/*  96 */       if (v == this.mAudioTextView) {
/*  97 */         intent.putExtra("rootDirType", 100);
/*  98 */         intent.putExtra("fileFilterType", 3);
/*  99 */         intent.putExtra("fileTraverseType", 200);
/*     */       }
/* 101 */       if (v == this.mOtherTextView) {
/* 102 */         intent.putExtra("rootDirType", 100);
/* 103 */         intent.putExtra("fileFilterType", 4);
/* 104 */         intent.putExtra("fileTraverseType", 200);
/*     */       }
/*     */ 
/* 107 */       if (v == this.mMobileMemoryTextView) {
/* 108 */         intent.putExtra("rootDirType", 100);
/* 109 */         intent.putExtra("fileFilterType", 5);
/* 110 */         intent.putExtra("fileTraverseType", 201);
/*     */       }
/* 112 */       if (v == this.mSDCardTextView) {
/* 113 */         intent.putExtra("rootDirType", 101);
/* 114 */         intent.putExtra("fileFilterType", 6);
/* 115 */         intent.putExtra("fileTraverseType", 201);
/*     */       }
/*     */ 
/* 118 */       startActivityForResult(intent, 730);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void onActivityResult(int requestCode, int resultCode, Intent data)
/*     */   {
/* 124 */     if ((requestCode == 730) && 
/* 125 */       (data != null)) {
/* 126 */       HashSet selectedFileInfos = (HashSet)data.getSerializableExtra("selectedFiles");
/* 127 */       Intent intent = new Intent();
/* 128 */       intent.putExtra("sendSelectedFiles", selectedFileInfos);
/* 129 */       setResult(731, intent);
/* 130 */       finish();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.activity.FileManagerActivity
 * JD-Core Version:    0.6.0
 */
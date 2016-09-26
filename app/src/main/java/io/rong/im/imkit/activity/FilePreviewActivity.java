/*     */ package io.rong.imkit.activity;
/*     */ 
/*     */ import android.app.Activity;
/*     */ import android.content.Intent;
/*     */ import android.net.Uri;
/*     */ import android.os.Bundle;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.Window;
/*     */ import android.widget.Button;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.LinearLayout;
/*     */ import android.widget.ProgressBar;
/*     */ import android.widget.TextView;
/*     */ import android.widget.Toast;
/*     */ import io.rong.eventbus.EventBus;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.RongIM;
/*     */ import io.rong.imkit.model.Event.FileMessageEvent;
/*     */ import io.rong.imkit.utils.FileTypeUtils;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.message.FileMessage;
/*     */ import java.io.File;
/*     */ 
/*     */ public class FilePreviewActivity extends Activity
/*     */   implements View.OnClickListener
/*     */ {
/*     */   private static final int NOT_DOWNLOAD = 0;
/*     */   private static final int DOWNLOADED = 1;
/*     */   private static final int DOWNLOADING = 2;
/*     */   private static final int DELETED = 3;
/*     */   private static final int DOWNLOAD_ERROR = 4;
/*     */   private static final int DOWNLOAD_CANCEL = 5;
/*     */   private static final int DOWNLOAD_SUCCESS = 6;
/*     */   private static final int ON_SUCCESS_CALLBACK = 100;
/*     */   private static final int ON_PROGRESS_CALLBACK = 101;
/*     */   private static final int ON_CANCEL_CALLBACK = 102;
/*     */   private static final int ON_ERROR_CALLBACK = 103;
/*     */   private FileDownloadInfo mFileDownloadInfo;
/*     */   private LinearLayout mPreviewDownloadFileLinearLayout;
/*     */   private ImageView mFileTypeImage;
/*     */   private TextView mFileNameView;
/*     */   private TextView mFileSizeView;
/*     */   private Button mFileButton;
/*     */   private ProgressBar mFileDownloadProgressBar;
/*     */   private LinearLayout mDownloadProgressView;
/*     */   private TextView mDownloadProgressTextView;
/*     */   private ImageView mCancel;
/*     */   private FileMessage mFileMessage;
/*     */   private Message mMessage;
/*     */   private String mFileName;
/*     */   private long mFileSize;
/*     */ 
/*     */   protected void onCreate(Bundle savedInstanceState)
/*     */   {
/*  63 */     super.onCreate(savedInstanceState);
/*  64 */     getWindow().setFlags(2048, 2048);
/*     */ 
/*  66 */     requestWindowFeature(1);
/*  67 */     setContentView(R.layout.rc_ac_file_download);
/*     */ 
/*  69 */     this.mFileMessage = ((FileMessage)getIntent().getParcelableExtra("FileMessage"));
/*  70 */     this.mMessage = ((Message)getIntent().getParcelableExtra("Message"));
/*  71 */     initView();
/*  72 */     initData();
/*  73 */     getFileDownloadInfo();
/*     */   }
/*     */ 
/*     */   private void initData() {
/*  77 */     this.mFileName = this.mFileMessage.getName();
/*  78 */     this.mFileTypeImage.setImageResource(FileTypeUtils.getInstance().fileTypeImageId(this.mFileName));
/*  79 */     this.mFileNameView.setText(this.mFileName);
/*  80 */     this.mFileSize = this.mFileMessage.getSize();
/*  81 */     this.mFileSizeView.setText(FileTypeUtils.getInstance().formatFileSize(this.mFileSize));
/*     */ 
/*  83 */     this.mFileDownloadInfo = new FileDownloadInfo();
/*     */ 
/*  85 */     this.mPreviewDownloadFileLinearLayout.setOnClickListener(this);
/*  86 */     this.mFileButton.setOnClickListener(this);
/*  87 */     this.mCancel.setOnClickListener(this);
/*     */ 
/*  89 */     RongContext.getInstance().getEventBus().register(this);
/*     */   }
/*     */ 
/*     */   private void initView() {
/*  93 */     this.mPreviewDownloadFileLinearLayout = ((LinearLayout)findViewById(R.id.rc_ac_ll_file_download));
/*  94 */     this.mFileTypeImage = ((ImageView)findViewById(R.id.rc_ac_iv_file_type_image));
/*  95 */     this.mFileNameView = ((TextView)findViewById(R.id.rc_ac_tv_file_name));
/*  96 */     this.mFileSizeView = ((TextView)findViewById(R.id.rc_ac_tv_file_size));
/*  97 */     this.mFileButton = ((Button)findViewById(R.id.rc_ac_btn_download_button));
/*  98 */     this.mDownloadProgressView = ((LinearLayout)findViewById(R.id.rc_ac_ll_progress_view));
/*  99 */     this.mCancel = ((ImageView)findViewById(R.id.rc_btn_cancel));
/* 100 */     this.mFileDownloadProgressBar = ((ProgressBar)findViewById(R.id.rc_ac_pb_download_progress));
/* 101 */     this.mDownloadProgressTextView = ((TextView)findViewById(R.id.rc_ac_tv_download_progress));
/*     */   }
/*     */ 
/*     */   public void onClick(View v)
/*     */   {
/* 106 */     if (v == this.mFileButton) {
/* 107 */       switch (this.mFileDownloadInfo.state) {
/*     */       case 0:
/*     */       case 2:
/*     */       case 3:
/*     */       case 4:
/*     */       case 5:
/* 113 */         this.mFileButton.setVisibility(8);
/* 114 */         this.mDownloadProgressView.setVisibility(0);
/* 115 */         this.mDownloadProgressTextView.setText(getString(R.string.rc_ac_file_download_progress_tv, new Object[] { FileTypeUtils.getInstance().formatFileSize(0L), FileTypeUtils.getInstance().formatFileSize(this.mFileSize) }));
/* 116 */         downloadFile();
/*     */ 
/* 118 */         break;
/*     */       case 1:
/*     */       case 6:
/* 121 */         openFile(this.mFileName, this.mFileMessage.getLocalPath().getPath());
/*     */       }
/*     */     }
/* 124 */     else if (v == this.mCancel) {
/* 125 */       RongIM.getInstance().cancelDownloadMediaMessage(this.mMessage, null);
/*     */     }
/* 127 */     if (v == this.mPreviewDownloadFileLinearLayout)
/* 128 */       finish();
/*     */   }
/*     */ 
/*     */   private void openFile(String fileName, String fileSavePath)
/*     */   {
/* 133 */     Intent intent = FileTypeUtils.getInstance().getOpenFileIntent(fileName, fileSavePath);
/* 134 */     if (intent != null)
/* 135 */       startActivity(intent);
/*     */     else
/* 137 */       Toast.makeText(this, getString(R.string.rc_ac_file_preview_can_not_open_file), 0).show();
/*     */   }
/*     */ 
/*     */   private void downloadFile()
/*     */   {
/* 142 */     RongIM.getInstance().downloadMediaMessage(this.mMessage, null);
/*     */   }
/*     */   private void getFileDownloadInfo() {
/* 145 */     if (this.mFileMessage.getLocalPath() != null) {
/* 146 */       File file = new File(this.mFileMessage.getLocalPath().getPath());
/* 147 */       if (file.exists())
/* 148 */         this.mFileDownloadInfo.state = 1;
/*     */       else
/* 150 */         this.mFileDownloadInfo.state = 3;
/*     */     }
/*     */     else {
/* 153 */       this.mFileDownloadInfo.state = 0;
/*     */     }
/* 155 */     refreshDownloadState();
/*     */   }
/*     */   private void refreshDownloadState() {
/* 158 */     switch (this.mFileDownloadInfo.state) {
/*     */     case 0:
/* 160 */       this.mFileButton.setText(getString(R.string.rc_ac_file_preview_begin_download));
/* 161 */       break;
/*     */     case 2:
/* 163 */       this.mFileButton.setVisibility(8);
/* 164 */       this.mDownloadProgressView.setVisibility(0);
/* 165 */       this.mFileDownloadProgressBar.setProgress(this.mFileDownloadInfo.progress);
/* 166 */       long downloadedFileLength = ()(this.mFileMessage.getSize() * (this.mFileDownloadInfo.progress / 100.0D) + 0.5D);
/* 167 */       this.mDownloadProgressTextView.setText(getString(R.string.rc_ac_file_download_progress_tv, new Object[] { FileTypeUtils.getInstance().formatFileSize(downloadedFileLength), FileTypeUtils.getInstance().formatFileSize(this.mFileSize) }));
/* 168 */       break;
/*     */     case 1:
/* 170 */       this.mFileButton.setText(getString(R.string.rc_ac_file_download_open_file_btn));
/* 171 */       break;
/*     */     case 6:
/* 173 */       this.mDownloadProgressView.setVisibility(8);
/* 174 */       this.mFileButton.setVisibility(0);
/* 175 */       this.mFileButton.setText(getString(R.string.rc_ac_file_download_open_file_btn));
/* 176 */       Toast.makeText(this, getString(R.string.rc_ac_file_preview_downloaded) + this.mFileDownloadInfo.path, 0).show();
/* 177 */       break;
/*     */     case 4:
/* 179 */       this.mDownloadProgressView.setVisibility(8);
/* 180 */       this.mFileButton.setVisibility(0);
/* 181 */       this.mFileButton.setText(getString(R.string.rc_ac_file_preview_begin_download));
/* 182 */       Toast.makeText(this, getString(R.string.rc_ac_file_preview_download_error), 0).show();
/* 183 */       break;
/*     */     case 5:
/* 185 */       this.mDownloadProgressView.setVisibility(8);
/* 186 */       this.mFileDownloadProgressBar.setProgress(0);
/* 187 */       this.mFileButton.setVisibility(0);
/* 188 */       Toast.makeText(this, getString(R.string.rc_ac_file_preview_download_cancel), 0).show();
/* 189 */       break;
/*     */     case 3:
/* 191 */       this.mFileButton.setText(getString(R.string.rc_ac_file_preview_begin_download));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onEventMainThread(Event.FileMessageEvent event)
/*     */   {
/* 197 */     if (this.mMessage.getMessageId() == event.getMessage().getMessageId())
/* 198 */       switch (event.getCallBackType()) {
/*     */       case 100:
/* 200 */         if ((event.getMessage() == null) || (event.getMessage().getContent() == null))
/* 201 */           return;
/* 202 */         FileMessage fileMessage = (FileMessage)event.getMessage().getContent();
/* 203 */         this.mFileMessage.setLocalPath(Uri.parse(fileMessage.getLocalPath().toString()));
/* 204 */         this.mFileDownloadInfo.state = 6;
/* 205 */         this.mFileDownloadInfo.path = fileMessage.getLocalPath().toString();
/* 206 */         refreshDownloadState();
/* 207 */         break;
/*     */       case 101:
/* 209 */         this.mFileDownloadInfo.state = 2;
/* 210 */         this.mFileDownloadInfo.progress = event.getProgress();
/* 211 */         refreshDownloadState();
/* 212 */         break;
/*     */       case 103:
/* 214 */         this.mFileDownloadInfo.state = 4;
/* 215 */         refreshDownloadState();
/* 216 */         break;
/*     */       case 102:
/* 218 */         this.mFileDownloadInfo.state = 5;
/* 219 */         refreshDownloadState();
/*     */       }
/*     */   }
/*     */ 
/*     */   protected void onResume()
/*     */   {
/* 227 */     super.onResume();
/* 228 */     getFileDownloadInfo();
/*     */   }
/*     */ 
/*     */   protected void onStop()
/*     */   {
/* 233 */     super.onStop();
/*     */   }
/*     */ 
/*     */   protected void onDestroy()
/*     */   {
/* 238 */     RongContext.getInstance().getEventBus().unregister(this);
/* 239 */     super.onDestroy();
/*     */   }
/*     */ 
/*     */   class FileDownloadInfo
/*     */   {
/*     */     int state;
/*     */     int progress;
/*     */     String path;
/*     */ 
/*     */     FileDownloadInfo()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.activity.FilePreviewActivity
 * JD-Core Version:    0.6.0
 */
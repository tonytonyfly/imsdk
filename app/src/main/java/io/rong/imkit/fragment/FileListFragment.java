/*     */ package io.rong.imkit.fragment;
/*     */ 
/*     */ import android.annotation.TargetApi;
/*     */ import android.content.Intent;
/*     */ import android.content.res.Resources;
/*     */ import android.os.AsyncTask;
/*     */ import android.os.Build.VERSION;
/*     */ import android.os.Bundle;
/*     */ import android.os.Environment;
/*     */ import android.support.annotation.Nullable;
/*     */ import android.support.v4.app.Fragment;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.AdapterView;
/*     */ import android.widget.AdapterView.OnItemClickListener;
/*     */ import android.widget.LinearLayout;
/*     */ import android.widget.ListView;
/*     */ import android.widget.TextView;
/*     */ import android.widget.Toast;
/*     */ import io.rong.imkit.R.color;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.activity.FileListActivity;
/*     */ import io.rong.imkit.model.FileInfo;
/*     */ import io.rong.imkit.utils.FileTypeUtils;
/*     */ import io.rong.imkit.utils.FileTypeUtils.FileNameComparator;
/*     */ import io.rong.imkit.widget.adapter.FileListAdapter;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ 
/*     */ public class FileListFragment extends Fragment
/*     */   implements AdapterView.OnItemClickListener, View.OnClickListener
/*     */ {
/*     */   private static final String MOBILE_DIR = "directory";
/*     */   private static final int RESULT_SELECTED_FILES = 1000;
/*     */   private static final int ALL_FILE_FILES = 1;
/*     */   private static final int ALL_VIDEO_FILES = 2;
/*     */   private static final int ALL_AUDIO_FILES = 3;
/*     */   private static final int ALL_other_FILES = 4;
/*     */   private static final int ALL_RAM_FILES = 5;
/*     */   private static final int ALL_SD_FILES = 6;
/*     */   private static final int ROOT_DIR = 100;
/*     */   private static final int SD_CARD_ROOT_DIR = 101;
/*     */   private static final int FILE_TRAVERSE_TYPE_ONE = 200;
/*     */   private static final int FILE_TRAVERSE_TYPE_TWO = 201;
/*     */   private LinearLayout mFileListTitleLinearLayout;
/*     */   private TextView mFilesCategoryTitleTextView;
/*     */   private TextView mFileSelectStateTextView;
/*     */   private ListView mFilesListView;
/*     */   private LinearLayout mFileLoadingLinearLayout;
/*     */   private TextView mNoFileMessageTextView;
/*     */   private FileListAdapter mFileListAdapter;
/*     */   private AsyncTask mLoadFilesTask;
/*     */   private List<FileInfo> mFilesList;
/*  60 */   private HashSet<FileInfo> mSelectedFiles = new HashSet();
/*     */   private File currentDir;
/*     */   private File startDir;
/*     */   private String mFileInfoMessage;
/*     */   private int fileTraverseType;
/*     */   private int fileFilterType;
/*     */ 
/*     */   public void onCreate(Bundle savedInstanceState)
/*     */   {
/*  73 */     super.onCreate(savedInstanceState);
/*  74 */     Intent intent = getActivity().getIntent();
/*  75 */     int rootDirType = intent.getIntExtra("rootDirType", -1);
/*  76 */     this.fileFilterType = intent.getIntExtra("fileFilterType", -1);
/*  77 */     this.fileTraverseType = intent.getIntExtra("fileTraverseType", -1);
/*     */ 
/*  79 */     Bundle arguments = getArguments();
/*  80 */     if ((arguments != null) && (arguments.containsKey("directory"))) {
/*  81 */       this.currentDir = new File(arguments.getString("directory"));
/*     */     }
/*  83 */     else if (rootDirType == 100) {
/*  84 */       String path = Environment.getExternalStorageDirectory().getPath();
/*  85 */       this.currentDir = new File(path);
/*  86 */     } else if (rootDirType == 101) {
/*  87 */       String path = FileTypeUtils.getInstance().getSDCardPath();
/*  88 */       this.currentDir = new File(path);
/*     */     }
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
/*     */   {
/*  96 */     View view = inflater.inflate(R.layout.rc_fr_file_list, container, false);
/*  97 */     this.mFileListTitleLinearLayout = ((LinearLayout)view.findViewById(R.id.rc_ad_ll_file_list_title));
/*  98 */     this.mFilesCategoryTitleTextView = ((TextView)view.findViewById(R.id.rc_ad_tv_file_list_title));
/*  99 */     this.mFileSelectStateTextView = ((TextView)view.findViewById(R.id.rc_ad_tv_file_list_select_state));
/*     */ 
/* 101 */     this.mFilesListView = ((ListView)view.findViewById(R.id.rc_fm_lv_storage_folder_list_files));
/* 102 */     this.mFileLoadingLinearLayout = ((LinearLayout)view.findViewById(R.id.rc_fm_ll_storage_folder_list_load));
/* 103 */     this.mNoFileMessageTextView = ((TextView)view.findViewById(R.id.rc_fm_tv_no_file_message));
/* 104 */     return view;
/*     */   }
/*     */ 
/*     */   public void onViewCreated(View view, Bundle savedInstanceState)
/*     */   {
/* 110 */     super.onViewCreated(view, savedInstanceState);
/* 111 */     loadFileList();
/* 112 */     String text = "";
/* 113 */     switch (this.fileFilterType) {
/*     */     case 1:
/* 115 */       text = getString(R.string.rc_fr_file_category_title_text);
/* 116 */       break;
/*     */     case 2:
/* 118 */       text = getString(R.string.rc_fr_file_category_title_video);
/* 119 */       break;
/*     */     case 3:
/* 121 */       text = getString(R.string.rc_fr_file_category_title_audio);
/* 122 */       break;
/*     */     case 4:
/* 124 */       text = getString(R.string.rc_fr_file_category_title_other);
/* 125 */       break;
/*     */     case 5:
/* 127 */       text = getString(R.string.rc_fr_file_category_title_ram);
/* 128 */       break;
/*     */     case 6:
/* 130 */       text = getString(R.string.rc_fr_file_category_title_sd);
/*     */     }
/*     */ 
/* 133 */     this.mFilesCategoryTitleTextView.setText(text);
/* 134 */     this.mFilesListView.setOnItemClickListener(this);
/* 135 */     this.mFileListTitleLinearLayout.setOnClickListener(this);
/* 136 */     this.mFileSelectStateTextView.setOnClickListener(this);
/* 137 */     this.mFileSelectStateTextView.setClickable(false);
/* 138 */     this.mFileSelectStateTextView.setTextColor(getResources().getColor(R.color.rc_ad_file_list_no_select_file_text_state));
/*     */   }
/*     */ 
/*     */   public void onDestroyView()
/*     */   {
/* 143 */     this.mFilesListView = null;
/* 144 */     super.onDestroyView();
/*     */   }
/*     */ 
/*     */   public void onDestroy()
/*     */   {
/* 149 */     if (this.mLoadFilesTask != null)
/* 150 */       this.mLoadFilesTask.cancel(true);
/* 151 */     super.onDestroy();
/*     */   }
/*     */   @TargetApi(11)
/*     */   private void loadFileList() {
/* 156 */     if (this.mLoadFilesTask != null) {
/* 157 */       return;
/*     */     }
/*     */ 
/* 160 */     if (Build.VERSION.SDK_INT >= 11) {
/* 161 */       this.mLoadFilesTask = new AsyncTask()
/*     */       {
/*     */         protected void onPreExecute() {
/* 164 */           if (FileListFragment.this.fileTraverseType == 200) {
/* 165 */             FileListFragment.this.showLoadingFileView();
/*     */           }
/* 167 */           super.onPreExecute();
/*     */         }
/*     */ 
/*     */         protected List<FileInfo> doInBackground(File[] params)
/*     */         {
/* 172 */           FileListFragment.access$202(FileListFragment.this, "");
/*     */           try {
/* 174 */             List fileInfos = new ArrayList();
/* 175 */             if (FileListFragment.this.fileTraverseType == 201) {
/* 176 */               File[] files = params[0].listFiles(FileTypeUtils.ALL_FOLDER_AND_FILES_FILTER);
/* 177 */               fileInfos = FileTypeUtils.getInstance().getFileInfosFromFileArray(files);
/* 178 */             } else if (FileListFragment.this.fileTraverseType == 200) {
/* 179 */               FileListFragment.access$302(FileListFragment.this, new File(Environment.getExternalStorageDirectory().getPath()));
/* 180 */               switch (FileListFragment.this.fileFilterType) {
/*     */               case 1:
/* 182 */                 fileInfos = FileTypeUtils.getInstance().getTextFilesInfo(FileListFragment.this.startDir);
/* 183 */                 FileListFragment.access$202(FileListFragment.this, FileListFragment.this.getString(R.string.rc_fr_file_category_title_text));
/* 184 */                 break;
/*     */               case 2:
/* 186 */                 fileInfos = FileTypeUtils.getInstance().getVideoFilesInfo(FileListFragment.this.startDir);
/* 187 */                 FileListFragment.access$202(FileListFragment.this, FileListFragment.this.getString(R.string.rc_fr_file_category_title_video));
/* 188 */                 break;
/*     */               case 3:
/* 190 */                 fileInfos = FileTypeUtils.getInstance().getAudioFilesInfo(FileListFragment.this.startDir);
/* 191 */                 FileListFragment.access$202(FileListFragment.this, FileListFragment.this.getString(R.string.rc_fr_file_category_title_audio));
/* 192 */                 break;
/*     */               case 4:
/* 194 */                 fileInfos = FileTypeUtils.getInstance().getOtherFilesInfo(FileListFragment.this.startDir);
/* 195 */                 FileListFragment.access$202(FileListFragment.this, FileListFragment.this.getString(R.string.rc_fr_file_category_title_other));
/*     */               }
/*     */             }
/*     */ 
/* 199 */             if (fileInfos == null) {
/* 200 */               return new ArrayList();
/*     */             }
/* 202 */             if (isCancelled()) {
/* 203 */               return new ArrayList();
/*     */             }
/* 205 */             Collections.sort(fileInfos, new FileTypeUtils.FileNameComparator());
/* 206 */             return fileInfos; } catch (Exception e) {
/*     */           }
/* 208 */           return new ArrayList();
/*     */         }
/*     */ 
/*     */         protected void onCancelled()
/*     */         {
/* 214 */           FileListFragment.access$502(FileListFragment.this, null);
/* 215 */           super.onCancelled();
/*     */         }
/*     */ 
/*     */         protected void onPostExecute(List<FileInfo> fileInfos)
/*     */         {
/* 220 */           FileListFragment.this.mFileLoadingLinearLayout.setVisibility(8);
/* 221 */           FileListFragment.this.mFilesListView.setVisibility(0);
/* 222 */           FileListFragment.access$502(FileListFragment.this, null);
/*     */           try {
/* 224 */             FileListFragment.access$802(FileListFragment.this, fileInfos);
/* 225 */             if (FileListFragment.this.mFilesList.isEmpty()) {
/* 226 */               FileListFragment.this.showNoFileMessage(FileListFragment.this.mFileInfoMessage);
/* 227 */               return;
/*     */             }
/* 229 */             FileListFragment.access$1002(FileListFragment.this, new FileListAdapter(FileListFragment.this.getActivity(), FileListFragment.this.mFilesList, FileListFragment.this.mSelectedFiles));
/* 230 */             FileListFragment.this.setListViewAdapter(FileListFragment.this.mFileListAdapter);
/*     */           } catch (Exception e) {
/* 232 */             FileListFragment.this.showNoFileMessage(e.getMessage());
/*     */           }
/* 234 */           super.onPostExecute(fileInfos);
/*     */         }
/*     */       }
/* 161 */       .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new File[] { this.currentDir });
/*     */     }
/*     */     else
/*     */     {
/* 238 */       this.mLoadFilesTask = new AsyncTask()
/*     */       {
/*     */         protected void onPreExecute() {
/* 241 */           if (FileListFragment.this.fileTraverseType == 200) {
/* 242 */             FileListFragment.this.showLoadingFileView();
/*     */           }
/* 244 */           super.onPreExecute();
/*     */         }
/*     */ 
/*     */         protected List<FileInfo> doInBackground(File[] params)
/*     */         {
/* 249 */           FileListFragment.access$202(FileListFragment.this, "");
/*     */           try {
/* 251 */             List fileInfos = new ArrayList();
/* 252 */             if (FileListFragment.this.fileTraverseType == 201) {
/* 253 */               File[] files = params[0].listFiles(FileTypeUtils.ALL_FOLDER_AND_FILES_FILTER);
/* 254 */               fileInfos = FileTypeUtils.getInstance().getFileInfosFromFileArray(files);
/* 255 */             } else if (FileListFragment.this.fileTraverseType == 200) {
/* 256 */               switch (FileListFragment.this.fileFilterType) {
/*     */               case 1:
/* 258 */                 fileInfos = FileTypeUtils.getInstance().getTextFilesInfo(FileListFragment.this.startDir);
/* 259 */                 FileListFragment.access$202(FileListFragment.this, FileListFragment.this.getString(R.string.rc_fr_file_category_title_text));
/* 260 */                 break;
/*     */               case 2:
/* 262 */                 fileInfos = FileTypeUtils.getInstance().getVideoFilesInfo(FileListFragment.this.startDir);
/* 263 */                 FileListFragment.access$202(FileListFragment.this, FileListFragment.this.getString(R.string.rc_fr_file_category_title_video));
/* 264 */                 break;
/*     */               case 3:
/* 266 */                 fileInfos = FileTypeUtils.getInstance().getAudioFilesInfo(FileListFragment.this.startDir);
/* 267 */                 FileListFragment.access$202(FileListFragment.this, FileListFragment.this.getString(R.string.rc_fr_file_category_title_audio));
/* 268 */                 break;
/*     */               case 4:
/* 270 */                 fileInfos = FileTypeUtils.getInstance().getOtherFilesInfo(FileListFragment.this.startDir);
/* 271 */                 FileListFragment.access$202(FileListFragment.this, FileListFragment.this.getString(R.string.rc_fr_file_category_title_other));
/*     */               }
/*     */             }
/*     */ 
/* 275 */             if (fileInfos == null) {
/* 276 */               return new ArrayList();
/*     */             }
/* 278 */             if (isCancelled()) {
/* 279 */               return new ArrayList();
/*     */             }
/* 281 */             Collections.sort(fileInfos, new FileTypeUtils.FileNameComparator());
/* 282 */             return fileInfos; } catch (Exception e) {
/*     */           }
/* 284 */           return new ArrayList();
/*     */         }
/*     */ 
/*     */         protected void onCancelled()
/*     */         {
/* 290 */           FileListFragment.access$502(FileListFragment.this, null);
/* 291 */           super.onCancelled();
/*     */         }
/*     */ 
/*     */         protected void onPostExecute(List<FileInfo> fileInfos)
/*     */         {
/* 296 */           FileListFragment.this.mFileLoadingLinearLayout.setVisibility(8);
/* 297 */           FileListFragment.this.mFilesListView.setVisibility(0);
/* 298 */           FileListFragment.access$502(FileListFragment.this, null);
/*     */           try {
/* 300 */             FileListFragment.access$802(FileListFragment.this, fileInfos);
/* 301 */             if (FileListFragment.this.mFilesList.isEmpty()) {
/* 302 */               FileListFragment.this.showNoFileMessage(FileListFragment.this.mFileInfoMessage);
/* 303 */               return;
/*     */             }
/* 305 */             FileListFragment.access$1002(FileListFragment.this, new FileListAdapter(FileListFragment.this.getActivity(), FileListFragment.this.mFilesList, FileListFragment.this.mSelectedFiles));
/* 306 */             FileListFragment.this.setListViewAdapter(FileListFragment.this.mFileListAdapter);
/*     */           } catch (Exception e) {
/* 308 */             FileListFragment.this.showNoFileMessage(e.getMessage());
/*     */           }
/* 310 */           super.onPostExecute(fileInfos);
/*     */         }
/*     */       }
/* 238 */       .execute(new File[] { this.currentDir });
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setListViewAdapter(FileListAdapter fileListAdapter)
/*     */   {
/* 317 */     this.mFileListAdapter = fileListAdapter;
/* 318 */     if (this.mFilesListView != null)
/* 319 */       this.mFilesListView.setAdapter(fileListAdapter);
/*     */   }
/*     */ 
/*     */   private void showLoadingFileView()
/*     */   {
/* 324 */     this.mFilesListView.setVisibility(8);
/* 325 */     this.mNoFileMessageTextView.setVisibility(8);
/* 326 */     this.mFileLoadingLinearLayout.setVisibility(0);
/*     */   }
/*     */ 
/*     */   private void showNoFileMessage(String message) {
/* 330 */     this.mFilesListView.setVisibility(8);
/* 331 */     this.mFileLoadingLinearLayout.setVisibility(8);
/* 332 */     this.mNoFileMessageTextView.setVisibility(0);
/* 333 */     this.mNoFileMessageTextView.setText(getResources().getString(R.string.rc_fr_no_file_message, new Object[] { message }));
/*     */   }
/*     */ 
/*     */   private void navigateTo(File folder) {
/* 337 */     FileListActivity activity = (FileListActivity)getActivity();
/* 338 */     FileListFragment fragment = new FileListFragment();
/* 339 */     Bundle args = new Bundle();
/* 340 */     args.putString("directory", folder.getAbsolutePath());
/* 341 */     fragment.setArguments(args);
/* 342 */     activity.showFragment(fragment);
/*     */   }
/*     */ 
/*     */   public void onItemClick(AdapterView<?> parent, View view, int position, long id)
/*     */   {
/* 348 */     Object selectedObject = parent.getItemAtPosition(position);
/* 349 */     if ((selectedObject instanceof FileInfo)) {
/* 350 */       FileInfo selectedFile = (FileInfo)selectedObject;
/*     */ 
/* 352 */       if (selectedFile.isDirectory()) {
/* 353 */         navigateTo(new File(selectedFile.getFilePath()));
/*     */       } else {
/* 355 */         if (this.mSelectedFiles.contains(selectedFile)) {
/* 356 */           this.mSelectedFiles.remove(selectedFile);
/* 357 */           this.mFileListAdapter.notifyDataSetChanged();
/* 358 */         } else if ((!view.isSelected()) && (this.mSelectedFiles.size() < 20)) {
/* 359 */           this.mSelectedFiles.add(selectedFile);
/* 360 */           this.mFileListAdapter.notifyDataSetChanged();
/*     */         } else {
/* 362 */           Toast.makeText(getActivity(), getResources().getString(R.string.rc_fr_file_list_most_selected_files), 0).show();
/*     */         }
/*     */ 
/* 365 */         if (this.mSelectedFiles.size() > 0) {
/* 366 */           this.mFileSelectStateTextView.setClickable(true);
/* 367 */           this.mFileSelectStateTextView.setTextColor(getResources().getColor(R.color.rc_ad_file_list_select_file_text_state));
/* 368 */           this.mFileSelectStateTextView.setText(getResources().getString(R.string.rc_ad_send_file_select_file, new Object[] { Integer.valueOf(this.mSelectedFiles.size()) }));
/*     */         } else {
/* 370 */           this.mFileSelectStateTextView.setClickable(false);
/* 371 */           this.mFileSelectStateTextView.setText(getResources().getString(R.string.rc_ad_send_file_no_select_file));
/* 372 */           this.mFileSelectStateTextView.setTextColor(getResources().getColor(R.color.rc_ad_file_list_no_select_file_text_state));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onClick(View v)
/*     */   {
/* 380 */     if (v == this.mFileSelectStateTextView) {
/* 381 */       Intent intent = new Intent();
/* 382 */       intent.putExtra("selectedFiles", this.mSelectedFiles);
/* 383 */       getActivity().setResult(1000, intent);
/* 384 */       getActivity().finish();
/*     */     }
/* 386 */     if (v == this.mFileListTitleLinearLayout)
/* 387 */       getActivity().finish();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.fragment.FileListFragment
 * JD-Core Version:    0.6.0
 */
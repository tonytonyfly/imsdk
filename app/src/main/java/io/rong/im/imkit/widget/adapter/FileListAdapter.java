/*    */ package io.rong.imkit.widget.adapter;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.view.LayoutInflater;
/*    */ import android.view.View;
/*    */ import android.view.ViewGroup;
/*    */ import android.widget.BaseAdapter;
/*    */ import android.widget.ImageView;
/*    */ import android.widget.TextView;
/*    */ import io.rong.imkit.R.drawable;
/*    */ import io.rong.imkit.R.id;
/*    */ import io.rong.imkit.R.layout;
/*    */ import io.rong.imkit.R.string;
/*    */ import io.rong.imkit.RongContext;
/*    */ import io.rong.imkit.model.FileInfo;
/*    */ import io.rong.imkit.utils.FileTypeUtils;
/*    */ import java.util.HashSet;
/*    */ import java.util.List;
/*    */ 
/*    */ public class FileListAdapter extends BaseAdapter
/*    */ {
/*    */   private List<FileInfo> mFileList;
/*    */   private HashSet<FileInfo> mSelectedFiles;
/*    */   private Context mContext;
/*    */ 
/*    */   public FileListAdapter(Context context, List<FileInfo> mFileList, HashSet<FileInfo> mSelectedFiles)
/*    */   {
/* 28 */     this.mFileList = mFileList;
/* 29 */     this.mContext = context;
/* 30 */     this.mSelectedFiles = mSelectedFiles;
/*    */   }
/*    */ 
/*    */   public int getCount()
/*    */   {
/* 35 */     if (this.mFileList != null) return this.mFileList.size();
/* 36 */     return 0;
/*    */   }
/*    */ 
/*    */   public Object getItem(int position)
/*    */   {
/* 41 */     if (this.mFileList == null) {
/* 42 */       return null;
/*    */     }
/* 44 */     if (position >= this.mFileList.size()) {
/* 45 */       return null;
/*    */     }
/* 47 */     return this.mFileList.get(position);
/*    */   }
/*    */ 
/*    */   public long getItemId(int position)
/*    */   {
/* 52 */     return position;
/*    */   }
/*    */ 
/*    */   public View getView(int position, View convertView, ViewGroup parent)
/*    */   {
/* 58 */     View view = LayoutInflater.from(this.mContext).inflate(R.layout.rc_wi_file_list_adapter, null);
/* 59 */     ViewHolder viewHolder = new ViewHolder(null);
/* 60 */     viewHolder.fileCheckStateImageView = ((ImageView)view.findViewById(R.id.rc_wi_ad_iv_file_check_state));
/* 61 */     viewHolder.fileIconImageView = ((ImageView)view.findViewById(R.id.rc_wi_ad_iv_file_icon));
/* 62 */     viewHolder.fileNameTextView = ((TextView)view.findViewById(R.id.rc_wi_ad_tv_file_name));
/* 63 */     viewHolder.fileDetailsTextView = ((TextView)view.findViewById(R.id.rc_wi_ad_tv_file_details));
/*    */ 
/* 65 */     FileInfo file = (FileInfo)this.mFileList.get(position);
/* 66 */     viewHolder.fileNameTextView.setText(file.getFileName());
/* 67 */     if (file.isDirectory()) {
/* 68 */       int filesNumber = FileTypeUtils.getNumFilesInFolder(file);
/* 69 */       if (filesNumber == 0)
/* 70 */         viewHolder.fileDetailsTextView.setText(RongContext.getInstance().getString(R.string.rc_ad_folder_no_files));
/*    */       else
/* 72 */         viewHolder.fileDetailsTextView.setText(RongContext.getInstance().getString(R.string.rc_ad_folder_files_number, new Object[] { Integer.valueOf(filesNumber) }));
/* 73 */       viewHolder.fileIconImageView.setImageResource(FileTypeUtils.getInstance().getFileIconResource(file));
/*    */     } else {
/* 75 */       if (this.mSelectedFiles.contains(file))
/* 76 */         viewHolder.fileCheckStateImageView.setImageResource(R.drawable.rc_ad_list_file_checked);
/*    */       else {
/* 78 */         viewHolder.fileCheckStateImageView.setImageResource(R.drawable.rc_ad_list_file_unchecked);
/*    */       }
/* 80 */       viewHolder.fileDetailsTextView.setText(RongContext.getInstance().getString(R.string.rc_ad_file_size, new Object[] { FileTypeUtils.getInstance().formatFileSize(file.getFileSize()) }));
/* 81 */       viewHolder.fileIconImageView.setImageResource(FileTypeUtils.getInstance().getFileIconResource(file));
/*    */     }
/* 83 */     return view;
/*    */   }
/*    */ 
/*    */   private class ViewHolder
/*    */   {
/*    */     ImageView fileCheckStateImageView;
/*    */     ImageView fileIconImageView;
/*    */     TextView fileNameTextView;
/*    */     TextView fileDetailsTextView;
/*    */ 
/*    */     private ViewHolder()
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.adapter.FileListAdapter
 * JD-Core Version:    0.6.0
 */
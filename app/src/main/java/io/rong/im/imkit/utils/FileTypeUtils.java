/*     */ package io.rong.imkit.utils;
/*     */ 
/*     */ import android.content.Intent;
/*     */ import android.content.res.Resources;
/*     */ import android.net.Uri;
/*     */ import android.os.Environment;
/*     */ import io.rong.imkit.R.array;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.RongContext;
/*     */ import io.rong.imkit.model.FileInfo;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class FileTypeUtils
/*     */ {
/*     */   private List<FileInfo> textFilesInfo;
/*     */   private List<FileInfo> videoFilesInfo;
/*     */   private List<FileInfo> audioFilesInfo;
/*     */   private List<FileInfo> otherFilesInfo;
/*     */   private static FileTypeUtils singleInstance;
/* 129 */   public static final FileFilter ALL_FOLDER_AND_FILES_FILTER = new FileFilter()
/*     */   {
/*     */     public boolean accept(File pathname)
/*     */     {
/* 133 */       return !pathname.isHidden();
/*     */     }
/* 129 */   };
/*     */ 
/* 305 */   public final int KILOBYTE = 1024; public final int MEGABYTE = 1048576; public final int GIGABYTE = 1073741824; public final int MAX_BYTE_SIZE = 512; public final int MAX_KILOBYTE_SIZE = 524288; public final int MAX_MEGABYTE_SIZE = 536870912;
/*     */ 
/*     */   public static FileTypeUtils getInstance()
/*     */   {
/*  35 */     if (singleInstance == null) {
/*  36 */       synchronized (FileTypeUtils.class) {
/*  37 */         if (singleInstance == null) {
/*  38 */           singleInstance = new FileTypeUtils();
/*     */         }
/*     */       }
/*     */     }
/*  42 */     return singleInstance;
/*     */   }
/*     */ 
/*     */   public int fileTypeImageId(String fileName)
/*     */   {
/*     */     int id;
/*     */     int id;
/*  47 */     if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_image_file_suffix))) {
/*  48 */       id = R.drawable.rc_file_icon_picture;
/*     */     }
/*     */     else
/*     */     {
/*     */       int id;
/*  49 */       if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_file_file_suffix))) {
/*  50 */         id = R.drawable.rc_file_icon_file;
/*     */       }
/*     */       else
/*     */       {
/*     */         int id;
/*  51 */         if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_video_file_suffix))) {
/*  52 */           id = R.drawable.rc_file_icon_video;
/*     */         }
/*     */         else
/*     */         {
/*     */           int id;
/*  53 */           if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_audio_file_suffix))) {
/*  54 */             id = R.drawable.rc_file_icon_audio;
/*     */           }
/*     */           else
/*     */           {
/*     */             int id;
/*  55 */             if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_word_file_suffix))) {
/*  56 */               id = R.drawable.rc_file_icon_word;
/*     */             }
/*     */             else
/*     */             {
/*     */               int id;
/*  57 */               if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_excel_file_suffix)))
/*  58 */                 id = R.drawable.rc_file_icon_excel;
/*     */               else
/*  60 */                 id = R.drawable.rc_file_icon_else; 
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*  61 */     return id;
/*     */   }
/*     */ 
/*     */   private boolean checkSuffix(String fileName, String[] fileSuffix)
/*     */   {
/*  66 */     for (String suffix : fileSuffix) {
/*  67 */       if ((fileName != null) && 
/*  68 */         (fileName.toLowerCase().endsWith(suffix))) {
/*  69 */         return true;
/*     */       }
/*     */     }
/*     */ 
/*  73 */     return false;
/*     */   }
/*     */ 
/*     */   public Intent getOpenFileIntent(String fileName, String fileSavePath) {
/*  77 */     Intent intent = null;
/*  78 */     if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_image_file_suffix))) {
/*  79 */       intent = new Intent("android.intent.action.VIEW");
/*  80 */       intent.addCategory("android.intent.category.DEFAULT");
/*  81 */       intent.addFlags(268435456);
/*  82 */       Uri uri = Uri.fromFile(new File(fileSavePath));
/*  83 */       intent.setDataAndType(uri, "image/*");
/*     */     }
/*  85 */     if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_file_file_suffix))) {
/*  86 */       intent = new Intent("android.intent.action.VIEW");
/*  87 */       intent.addCategory("android.intent.category.DEFAULT");
/*  88 */       intent.addFlags(268435456);
/*  89 */       Uri uri = Uri.fromFile(new File(fileSavePath));
/*  90 */       intent.setDataAndType(uri, "text/plain");
/*     */     }
/*  92 */     if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_video_file_suffix))) {
/*  93 */       intent = new Intent("android.intent.action.VIEW");
/*  94 */       intent.addFlags(67108864);
/*  95 */       intent.putExtra("oneshot", 0);
/*  96 */       intent.putExtra("configchange", 0);
/*  97 */       Uri uri = Uri.fromFile(new File(fileSavePath));
/*  98 */       intent.setDataAndType(uri, "video/*");
/*     */     }
/* 100 */     if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_audio_file_suffix))) {
/* 101 */       intent = new Intent("android.intent.action.VIEW");
/* 102 */       intent.addFlags(67108864);
/* 103 */       intent.putExtra("oneshot", 0);
/* 104 */       intent.putExtra("configchange", 0);
/* 105 */       Uri uri = Uri.fromFile(new File(fileSavePath));
/* 106 */       intent.setDataAndType(uri, "audio/*");
/*     */     }
/* 108 */     if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_word_file_suffix))) {
/* 109 */       intent = new Intent("android.intent.action.VIEW");
/* 110 */       intent.addCategory("android.intent.category.DEFAULT");
/* 111 */       intent.addFlags(268435456);
/* 112 */       Uri uri = Uri.fromFile(new File(fileSavePath));
/* 113 */       intent.setDataAndType(uri, "application/msword");
/*     */     }
/* 115 */     if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_excel_file_suffix))) {
/* 116 */       intent = new Intent("android.intent.action.VIEW");
/* 117 */       intent.addCategory("android.intent.category.DEFAULT");
/* 118 */       intent.addFlags(268435456);
/* 119 */       Uri uri = Uri.fromFile(new File(fileSavePath));
/* 120 */       intent.setDataAndType(uri, "application/vnd.ms-excel");
/*     */     }
/* 122 */     return intent;
/*     */   }
/*     */ 
/*     */   public List<FileInfo> getTextFilesInfo(File fileDir)
/*     */   {
/* 138 */     this.textFilesInfo = new ArrayList();
/* 139 */     textFilesInfo(fileDir);
/* 140 */     return this.textFilesInfo;
/*     */   }
/*     */ 
/*     */   public void textFilesInfo(File fileDir) {
/* 144 */     File[] listFiles = fileDir.listFiles(ALL_FOLDER_AND_FILES_FILTER);
/* 145 */     if (listFiles != null)
/* 146 */       for (File file : listFiles)
/* 147 */         if (file.isDirectory()) {
/* 148 */           textFilesInfo(file);
/*     */         }
/* 150 */         else if (checkSuffix(file.getName(), RongContext.getInstance().getResources().getStringArray(R.array.rc_file_file_suffix))) {
/* 151 */           FileInfo fileInfo = getFileInfoFromFile(file);
/* 152 */           this.textFilesInfo.add(fileInfo);
/*     */         }
/*     */   }
/*     */ 
/*     */   public List<FileInfo> getVideoFilesInfo(File fileDir)
/*     */   {
/* 160 */     this.videoFilesInfo = new ArrayList();
/* 161 */     videoFilesInfo(fileDir);
/* 162 */     return this.videoFilesInfo;
/*     */   }
/*     */ 
/*     */   public void videoFilesInfo(File fileDir) {
/* 166 */     File[] listFiles = fileDir.listFiles(ALL_FOLDER_AND_FILES_FILTER);
/* 167 */     if (listFiles != null)
/* 168 */       for (File file : listFiles)
/* 169 */         if (file.isDirectory()) {
/* 170 */           videoFilesInfo(file);
/*     */         }
/* 172 */         else if (checkSuffix(file.getName(), RongContext.getInstance().getResources().getStringArray(R.array.rc_video_file_suffix))) {
/* 173 */           FileInfo fileInfo = getFileInfoFromFile(file);
/* 174 */           this.videoFilesInfo.add(fileInfo);
/*     */         }
/*     */   }
/*     */ 
/*     */   public List<FileInfo> getAudioFilesInfo(File fileDir)
/*     */   {
/* 182 */     this.audioFilesInfo = new ArrayList();
/* 183 */     audioFilesInfo(fileDir);
/* 184 */     return this.audioFilesInfo;
/*     */   }
/*     */ 
/*     */   public void audioFilesInfo(File fileDir) {
/* 188 */     File[] listFiles = fileDir.listFiles(ALL_FOLDER_AND_FILES_FILTER);
/* 189 */     if (listFiles != null)
/* 190 */       for (File file : listFiles)
/* 191 */         if (file.isDirectory()) {
/* 192 */           audioFilesInfo(file);
/*     */         }
/* 194 */         else if (checkSuffix(file.getName(), RongContext.getInstance().getResources().getStringArray(R.array.rc_audio_file_suffix))) {
/* 195 */           FileInfo fileInfo = getFileInfoFromFile(file);
/* 196 */           this.audioFilesInfo.add(fileInfo);
/*     */         }
/*     */   }
/*     */ 
/*     */   public List<FileInfo> getOtherFilesInfo(File fileDir)
/*     */   {
/* 204 */     this.otherFilesInfo = new ArrayList();
/* 205 */     otherFilesInfo(fileDir);
/* 206 */     return this.otherFilesInfo;
/*     */   }
/*     */ 
/*     */   public void otherFilesInfo(File fileDir) {
/* 210 */     File[] listFiles = fileDir.listFiles(ALL_FOLDER_AND_FILES_FILTER);
/* 211 */     if (listFiles != null)
/* 212 */       for (File file : listFiles)
/* 213 */         if (file.isDirectory()) {
/* 214 */           otherFilesInfo(file);
/*     */         }
/* 216 */         else if (checkSuffix(file.getName(), RongContext.getInstance().getResources().getStringArray(R.array.rc_other_file_suffix))) {
/* 217 */           FileInfo fileInfo = getFileInfoFromFile(file);
/* 218 */           this.otherFilesInfo.add(fileInfo);
/*     */         }
/*     */   }
/*     */ 
/*     */   public List<FileInfo> getFileInfosFromFileArray(File[] files)
/*     */   {
/* 226 */     List fileInfos = new ArrayList();
/* 227 */     for (File file : files) {
/* 228 */       FileInfo fileInfo = getFileInfoFromFile(file);
/* 229 */       fileInfos.add(fileInfo);
/*     */     }
/* 231 */     return fileInfos;
/*     */   }
/*     */ 
/*     */   public FileInfo getFileInfoFromFile(File file) {
/* 235 */     FileInfo fileInfo = new FileInfo();
/* 236 */     fileInfo.setFileName(file.getName());
/* 237 */     fileInfo.setFilePath(file.getPath());
/* 238 */     fileInfo.setFileSize(file.length());
/* 239 */     fileInfo.setDirectory(file.isDirectory());
/* 240 */     int lastDotIndex = file.getName().lastIndexOf(".");
/* 241 */     if (lastDotIndex > 0) {
/* 242 */       String fileSuffix = file.getName().substring(lastDotIndex + 1);
/* 243 */       fileInfo.setSuffix(fileSuffix);
/*     */     }
/* 245 */     return fileInfo;
/*     */   }
/*     */ 
/*     */   public static int getNumFilesInFolder(FileInfo fileInfo)
/*     */   {
/* 275 */     if (!fileInfo.isDirectory()) return 0;
/* 276 */     File[] files = new File(fileInfo.getFilePath()).listFiles(ALL_FOLDER_AND_FILES_FILTER);
/* 277 */     if (files == null) return 0;
/* 278 */     return files.length;
/*     */   }
/*     */ 
/*     */   public int getFileIconResource(FileInfo file)
/*     */   {
/* 285 */     if (file.isDirectory()) {
/* 286 */       return R.drawable.rc_ad_list_folder_icon;
/*     */     }
/* 288 */     return getFileTypeImageId(file.getFileName());
/*     */   }
/*     */ 
/*     */   private int getFileTypeImageId(String fileName)
/*     */   {
/*     */     int id;
/*     */     int id;
/* 294 */     if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_file_file_suffix))) {
/* 295 */       id = R.drawable.rc_ad_list_file_icon;
/*     */     }
/*     */     else
/*     */     {
/*     */       int id;
/* 296 */       if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_video_file_suffix))) {
/* 297 */         id = R.drawable.rc_ad_list_video_icon;
/*     */       }
/*     */       else
/*     */       {
/*     */         int id;
/* 298 */         if (checkSuffix(fileName, RongContext.getInstance().getResources().getStringArray(R.array.rc_audio_file_suffix)))
/* 299 */           id = R.drawable.rc_ad_list_audio_icon;
/*     */         else
/* 301 */           id = R.drawable.rc_ad_list_other_icon; 
/*     */       }
/*     */     }
/* 302 */     return id;
/*     */   }
/*     */ 
/*     */   public String formatFileSize(long size)
/*     */   {
/* 317 */     if (size < 524288L)
/* 318 */       return String.format("%.2f K", new Object[] { Float.valueOf((float)size / 1024.0F) });
/* 319 */     if (size < 536870912L) {
/* 320 */       return String.format("%.2f M", new Object[] { Float.valueOf((float)size / 1048576.0F) });
/*     */     }
/* 322 */     return String.format("%.2f G", new Object[] { Float.valueOf((float)size / 1.073742E+009F) });
/*     */   }
/*     */ 
/*     */   public String getSDCardPath() {
/* 326 */     String SDCardPath = null;
/* 327 */     String SDCardDefaultPath = Environment.getExternalStorageDirectory().getAbsolutePath();
/*     */ 
/* 329 */     if (SDCardDefaultPath.endsWith("/"))
/* 330 */       SDCardDefaultPath = SDCardDefaultPath.substring(0, SDCardDefaultPath.length() - 1);
/*     */     try
/*     */     {
/* 333 */       Runtime runtime = Runtime.getRuntime();
/* 334 */       Process process = runtime.exec("mount");
/* 335 */       InputStream inputStream = process.getInputStream();
/* 336 */       InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
/*     */ 
/* 338 */       BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
/*     */       String line;
/* 339 */       while ((line = bufferedReader.readLine()) != null)
/* 340 */         if ((line.toLowerCase().contains("sdcard")) && (line.contains(".android_secure"))) {
/* 341 */           String[] array = line.split(" ");
/* 342 */           if ((array != null) && (array.length > 1)) {
/* 343 */             String temp = array[1].replace("/.android_secure", "");
/* 344 */             if (!SDCardDefaultPath.equals(temp))
/* 345 */               SDCardPath = temp;
/*     */           }
/*     */         }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 351 */       e.printStackTrace();
/*     */     }
/* 353 */     return SDCardPath;
/*     */   }
/*     */ 
/*     */   public static class FileNameComparator
/*     */     implements Comparator<FileInfo>
/*     */   {
/*     */     protected static final int FIRST = -1;
/*     */     protected static final int SECOND = 1;
/*     */ 
/*     */     public int compare(FileInfo lhs, FileInfo rhs)
/*     */     {
/* 258 */       if ((lhs.isDirectory()) || (rhs.isDirectory())) {
/* 259 */         if (lhs.isDirectory() == rhs.isDirectory())
/* 260 */           return lhs.getFileName().compareToIgnoreCase(rhs.getFileName());
/* 261 */         if (lhs.isDirectory()) return -1;
/* 262 */         return 1;
/*     */       }
/* 264 */       return lhs.getFileName().compareToIgnoreCase(rhs.getFileName());
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.utils.FileTypeUtils
 * JD-Core Version:    0.6.0
 */
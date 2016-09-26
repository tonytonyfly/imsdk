/*     */ package io.rong.imlib.filetransfer;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class FtUtilities
/*     */ {
/*  12 */   private static String TAG = "FtUtilities";
/*     */ 
/*     */   public static FtConst.MimeType getMimeType(String fileName)
/*     */   {
/*     */     FtConst.MimeType mimeType;
/*     */     FtConst.MimeType mimeType;
/*  16 */     if (isImageFile(fileName)) {
/*  17 */       mimeType = FtConst.MimeType.FILE_IMAGE;
/*     */     }
/*     */     else
/*     */     {
/*     */       FtConst.MimeType mimeType;
/*  18 */       if (isAudioFile(fileName)) {
/*  19 */         mimeType = FtConst.MimeType.FILE_AUDIO;
/*     */       }
/*     */       else
/*     */       {
/*     */         FtConst.MimeType mimeType;
/*  20 */         if (isVideoFile(fileName))
/*  21 */           mimeType = FtConst.MimeType.FILE_VIDEO;
/*     */         else
/*  23 */           mimeType = FtConst.MimeType.FILE_TEXT_PLAIN; 
/*     */       }
/*     */     }
/*  25 */     return mimeType;
/*     */   }
/*     */ 
/*     */   private static boolean isImageFile(String fileName) {
/*  29 */     String[] imageSuffixs = { ".bmp", ".cod", ".gif", ".ief", ".jpe", ".jpeg", ".jpg", ".jfif", ".svg", ".tif", ".tiff", ".ras", ".ico", ".pnm", ".pbm", ".pgm", ".ppm", ".xbm", ".xpm", ".xwd", ".rgb" };
/*     */ 
/*  32 */     for (String sufix : imageSuffixs) {
/*  33 */       if (fileName.toLowerCase().endsWith(sufix)) {
/*  34 */         return true;
/*     */       }
/*     */     }
/*  37 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean isAudioFile(String fileName) {
/*  41 */     String[] imageSuffixs = { ".au", ".snd", ".mid", ".rmi", ".aif", ".aifc", ".aiff", ".m3u", ".ra", ".ram", ".wav" };
/*     */ 
/*  44 */     for (String sufix : imageSuffixs) {
/*  45 */       if (fileName.toLowerCase().endsWith(sufix)) {
/*  46 */         return true;
/*     */       }
/*     */     }
/*  49 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean isVideoFile(String fileName) {
/*  53 */     String[] imageSuffixs = { ".rmvb", ".avi", ".mp4", ".mp2", ".mpa", ".mpe", ".mpeg", ".mpg", ".mpv2", ".mov", ".qt", ".lsf", ".lsx", ".asf", ".asr", ".asx", ".avi", ".movie" };
/*     */ 
/*  56 */     for (String sufix : imageSuffixs) {
/*  57 */       if (fileName.toLowerCase().endsWith(sufix)) {
/*  58 */         return true;
/*     */       }
/*     */     }
/*  61 */     return false;
/*     */   }
/*     */ 
/*     */   public static String generateKey(String mimeType) {
/*  65 */     String szKey = mimeType;
/*  66 */     szKey = szKey + "__RC-";
/*  67 */     long time = System.currentTimeMillis();
/*  68 */     SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
/*  69 */     Date mDate = new Date(time);
/*  70 */     szKey = szKey + format.format(mDate);
/*  71 */     int random = (int)(Math.random() * 1000.0D) % 10000;
/*  72 */     szKey = szKey + "_" + random;
/*  73 */     szKey = szKey + "_" + time;
/*  74 */     return szKey;
/*     */   }
/*     */ 
/*     */   public static String getMediaDir(int fileType) {
/*  78 */     String mediaPath = "image";
/*  79 */     switch (fileType) {
/*     */     case 1:
/*  81 */       mediaPath = "image";
/*  82 */       break;
/*     */     case 2:
/*  84 */       mediaPath = "audio";
/*  85 */       break;
/*     */     case 3:
/*  87 */       mediaPath = "video";
/*  88 */       break;
/*     */     case 4:
/*  90 */       mediaPath = "file";
/*     */     }
/*     */ 
/*  93 */     return mediaPath;
/*     */   }
/*     */ 
/*     */   public static String getCateDir(int categoryId) {
/*  97 */     String categoryPath = "private";
/*  98 */     switch (categoryId) {
/*     */     case 1:
/* 100 */       categoryPath = "private";
/* 101 */       break;
/*     */     case 2:
/* 103 */       categoryPath = "discussion";
/* 104 */       break;
/*     */     case 3:
/* 106 */       categoryPath = "group";
/* 107 */       break;
/*     */     case 4:
/* 109 */       categoryPath = "chatroom";
/* 110 */       break;
/*     */     case 5:
/* 112 */       categoryPath = "reception";
/* 113 */       break;
/*     */     }
/*     */ 
/* 117 */     return categoryPath;
/*     */   }
/*     */ 
/*     */   public static boolean isFileExist(String fileName) {
/* 121 */     File file = new File(fileName);
/* 122 */     return file.exists();
/*     */   }
/*     */ 
/*     */   public static String getFileKey(String fileUri) {
/* 126 */     String fileKey = fileUri;
/* 127 */     int pos = fileKey.indexOf("?");
/* 128 */     if (pos != -1) {
/* 129 */       fileKey = fileKey.substring(0, pos);
/*     */     }
/* 131 */     pos = fileKey.lastIndexOf("/");
/* 132 */     if (pos != -1) {
/* 133 */       fileKey = fileKey.substring(pos + 1);
/*     */     }
/* 135 */     fileKey = fileKey.replaceAll("%2F", "_");
/* 136 */     return fileKey;
/*     */   }
/*     */ 
/*     */   public static String getFileName(String cachePath, String fileName)
/*     */   {
/* 141 */     String suffix = null;
/*     */     String name;
/*     */     String name;
/* 142 */     if (fileName.lastIndexOf(".") > 0) {
/* 143 */       suffix = fileName.substring(fileName.lastIndexOf("."));
/* 144 */       name = fileName.substring(0, fileName.lastIndexOf("."));
/*     */     } else {
/* 146 */       name = fileName;
/*     */     }
/*     */ 
/* 149 */     File file = new File(cachePath, name + suffix);
/* 150 */     int n = 0;
/* 151 */     while (file.exists()) {
/* 152 */       n++;
/* 153 */       if (suffix != null)
/* 154 */         name = name + "(" + n + ")" + suffix;
/*     */       else
/* 156 */         name = name + "(" + n + ")";
/* 157 */       file = new File(cachePath, name + suffix);
/*     */     }
/* 159 */     return file.getPath();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.filetransfer.FtUtilities
 * JD-Core Version:    0.6.0
 */
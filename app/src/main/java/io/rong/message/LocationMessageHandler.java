/*     */ package io.rong.message;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.Bitmap.CompressFormat;
/*     */ import android.net.Uri;
/*     */ import android.text.TextUtils;
/*     */ import android.util.Base64;
/*     */ import io.rong.common.FileUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.NativeClient;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.message.utils.BitmapUtil;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ 
/*     */ public class LocationMessageHandler extends MessageHandler<LocationMessage>
/*     */ {
/*     */   private static final String TAG = "LocationMessageHandler";
/*     */   private static final int THUMB_WIDTH = 408;
/*     */   private static final int THUMB_HEIGHT = 240;
/*     */   private static final int THUMB_COMPRESSED_QUALITY = 30;
/*     */   private static final String LOCATION_PATH = "/location/";
/*     */ 
/*     */   public LocationMessageHandler(Context context)
/*     */   {
/*  32 */     super(context);
/*     */   }
/*     */ 
/*     */   public void decodeMessage(Message message, LocationMessage content)
/*     */   {
/*  37 */     String name = message.getMessageId() + "";
/*  38 */     if (message.getMessageId() == 0) {
/*  39 */       name = message.getSentTime() + "";
/*     */     }
/*  41 */     Uri uri = obtainLocationUri(getContext());
/*  42 */     File file = new File(uri.toString() + name);
/*  43 */     if (file.exists()) {
/*  44 */       content.setImgUri(Uri.fromFile(file));
/*  45 */       return;
/*     */     }
/*  47 */     if (content != null) {
/*  48 */       String base64 = content.getBase64();
/*  49 */       if (!TextUtils.isEmpty(base64))
/*  50 */         if (base64.startsWith("http")) {
/*  51 */           content.setImgUri(Uri.parse(base64));
/*  52 */           content.setBase64(null);
/*     */         } else {
/*     */           try {
/*  55 */             byte[] audio = Base64.decode(content.getBase64(), 2);
/*  56 */             file = FileUtils.byte2File(audio, uri.toString(), name + "");
/*  57 */             if (content.getImgUri() == null)
/*  58 */               if ((file != null) && (file.exists()))
/*  59 */                 content.setImgUri(Uri.fromFile(file));
/*     */               else
/*  61 */                 RLog.e("LocationMessageHandler", "getImgUri is null");
/*     */           }
/*     */           catch (IllegalArgumentException e)
/*     */           {
/*  65 */             RLog.e("LocationMessageHandler", "Not Base64 Content!");
/*  66 */             e.printStackTrace();
/*     */           }
/*  68 */           message.setContent(content);
/*  69 */           content.setBase64(null);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void encodeMessage(Message message)
/*     */   {
/*  77 */     LocationMessage content = (LocationMessage)message.getContent();
/*  78 */     if (content.getImgUri() == null) {
/*  79 */       RLog.w("LocationMessageHandler", "No thumbnail uri.");
/*  80 */       if (this.mHandleMessageListener != null) {
/*  81 */         this.mHandleMessageListener.onHandleResult(message, 0);
/*     */       }
/*  83 */       return;
/*     */     }
/*     */ 
/*  86 */     Uri uri = obtainLocationUri(getContext());
/*     */     String thumbnailPath;
/*     */     String thumbnailPath;
/*  88 */     if (content.getImgUri().getScheme().toLowerCase().equals("file")) {
/*  89 */       thumbnailPath = content.getImgUri().getPath();
/*     */     } else {
/*  91 */       File file = loadLocationThumbnail(content, message.getSentTime() + "");
/*  92 */       thumbnailPath = file != null ? file.getPath() : null;
/*     */     }
/*  94 */     if (thumbnailPath == null) {
/*  95 */       RLog.e("LocationMessageHandler", "load thumbnailPath null!");
/*  96 */       if (this.mHandleMessageListener != null) {
/*  97 */         this.mHandleMessageListener.onHandleResult(message, -1);
/*     */       }
/*  99 */       return;
/*     */     }
/*     */     try {
/* 102 */       Bitmap bitmap = BitmapUtil.interceptBitmap(thumbnailPath, 408, 240);
/* 103 */       if (bitmap != null) {
/* 104 */         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
/* 105 */         bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
/* 106 */         byte[] data = outputStream.toByteArray();
/* 107 */         outputStream.close();
/*     */ 
/* 109 */         String base64 = Base64.encodeToString(data, 2);
/* 110 */         content.setBase64(base64);
/* 111 */         File file = FileUtils.byte2File(data, uri.toString(), message.getMessageId() + "");
/* 112 */         if ((file != null) && (file.exists())) {
/* 113 */           content.setImgUri(Uri.fromFile(file));
/*     */         }
/* 115 */         if (!bitmap.isRecycled())
/* 116 */           bitmap.recycle();
/* 117 */         if (this.mHandleMessageListener != null)
/* 118 */           this.mHandleMessageListener.onHandleResult(message, 0);
/*     */       }
/*     */       else {
/* 121 */         RLog.e("LocationMessageHandler", "get null bitmap!");
/* 122 */         if (this.mHandleMessageListener != null)
/* 123 */           this.mHandleMessageListener.onHandleResult(message, -1);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 127 */       RLog.e("LocationMessageHandler", "Not Base64 Content!");
/* 128 */       e.printStackTrace();
/* 129 */       if (this.mHandleMessageListener != null)
/* 130 */         this.mHandleMessageListener.onHandleResult(message, -1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Uri obtainLocationUri(Context context)
/*     */   {
/* 136 */     File file = context.getFilesDir();
/* 137 */     String userId = NativeClient.getInstance().getCurrentUserId();
/* 138 */     String path = file.getPath() + File.separator + userId + "/location/";
/* 139 */     file = new File(path);
/* 140 */     if (!file.exists())
/* 141 */       file.mkdirs();
/* 142 */     return Uri.parse(path);
/*     */   }
/*     */ 
/*     */   private File loadLocationThumbnail(LocationMessage content, String name) {
/* 146 */     File file = null;
/* 147 */     HttpURLConnection conn = null;
/* 148 */     int responseCode = 0;
/*     */     try {
/* 150 */       Uri uri = content.getImgUri();
/* 151 */       URL url = new URL(uri.toString());
/* 152 */       conn = (HttpURLConnection)url.openConnection();
/* 153 */       conn.setRequestMethod("GET");
/* 154 */       conn.setReadTimeout(3000);
/* 155 */       conn.connect();
/*     */ 
/* 157 */       responseCode = conn.getResponseCode();
/* 158 */       if ((responseCode >= 200) && (responseCode < 300)) {
/* 159 */         String path = FileUtils.getCachePath(getContext(), "location");
/* 160 */         file = new File(path);
/* 161 */         if (!file.exists()) {
/* 162 */           file.mkdirs();
/*     */         }
/* 164 */         file = new File(path, name);
/* 165 */         InputStream is = conn.getInputStream();
/* 166 */         FileOutputStream os = new FileOutputStream(file);
/* 167 */         byte[] buffer = new byte[1024];
/*     */         int len;
/* 169 */         while ((len = is.read(buffer)) != -1) {
/* 170 */           os.write(buffer, 0, len);
/*     */         }
/* 172 */         is.close();
/* 173 */         os.close();
/*     */       }
/*     */     } catch (Exception e) {
/* 176 */       e.printStackTrace();
/*     */     } finally {
/* 178 */       if (conn != null) {
/* 179 */         conn.disconnect();
/*     */       }
/* 181 */       RLog.d("LocationMessageHandler", "loadLocationThumbnail result : " + responseCode);
/*     */     }
/* 183 */     return file;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.LocationMessageHandler
 * JD-Core Version:    0.6.0
 */
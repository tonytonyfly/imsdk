/*     */ package io.rong.message;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.res.Resources;
/*     */ import android.content.res.Resources.NotFoundException;
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.Bitmap.CompressFormat;
/*     */ import android.graphics.BitmapFactory;
/*     */ import android.graphics.BitmapFactory.Options;
/*     */ import android.net.Uri;
/*     */ import android.text.TextUtils;
/*     */ import android.util.Base64;
/*     */ import io.rong.common.FileUtils;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imlib.NativeClient;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.message.utils.BitmapUtil;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class ImageMessageHandler extends MessageHandler<ImageMessage>
/*     */ {
/*     */   private static final String TAG = "ImageMessageHandler";
/*  26 */   private static int COMPRESSED_SIZE = 960;
/*  27 */   private static int COMPRESSED_QUALITY = 85;
/*     */ 
/*  29 */   private static int THUMB_COMPRESSED_SIZE = 240;
/*  30 */   private static int THUMB_COMPRESSED_MIN_SIZE = 100;
/*  31 */   private static int THUMB_COMPRESSED_QUALITY = 30;
/*     */   private static final String IMAGE_LOCAL_PATH = "/image/local/";
/*     */   private static final String IMAGE_THUMBNAIL_PATH = "/image/thumbnail/";
/*     */ 
/*     */   public ImageMessageHandler(Context context)
/*     */   {
/*  36 */     super(context);
/*     */   }
/*     */ 
/*     */   public void decodeMessage(Message message, ImageMessage model)
/*     */   {
/*  41 */     Uri uri = obtainImageUri(getContext());
/*  42 */     String name = message.getMessageId() + ".jpg";
/*  43 */     if (message.getMessageId() == 0) {
/*  44 */       name = message.getSentTime() + ".jpg";
/*     */     }
/*     */ 
/*  47 */     String thumb = uri.toString() + "/image/thumbnail/";
/*  48 */     String local = uri.toString() + "/image/local/";
/*     */ 
/*  50 */     File localFile = new File(local + name);
/*  51 */     if (localFile.exists()) {
/*  52 */       model.setLocalUri(Uri.parse("file://" + local + name));
/*     */     }
/*     */ 
/*  55 */     File thumbFile = new File(thumb + name);
/*     */ 
/*  57 */     if ((!TextUtils.isEmpty(model.getBase64())) && (!thumbFile.exists())) {
/*  58 */       byte[] data = null;
/*     */       try {
/*  60 */         data = Base64.decode(model.getBase64(), 2);
/*     */       } catch (IllegalArgumentException e) {
/*  62 */         RLog.e("ImageMessageHandler", "afterDecodeMessage Not Base64 Content!");
/*  63 */         e.printStackTrace();
/*     */       }
/*     */ 
/*  66 */       if (!isImageFile(data)) {
/*  67 */         RLog.e("ImageMessageHandler", "afterDecodeMessage Not Image File!");
/*  68 */         return;
/*     */       }
/*  70 */       FileUtils.byte2File(data, thumb, name);
/*     */     }
/*  72 */     model.setThumUri(Uri.parse("file://" + thumb + name));
/*     */ 
/*  74 */     model.setBase64(null);
/*     */   }
/*     */ 
/*     */   public void encodeMessage(Message message)
/*     */   {
/*  79 */     ImageMessage model = (ImageMessage)message.getContent();
/*  80 */     Uri uri = obtainImageUri(getContext());
/*  81 */     String name = message.getMessageId() + ".jpg";
/*     */ 
/*  83 */     BitmapFactory.Options options = new BitmapFactory.Options();
/*  84 */     options.inJustDecodeBounds = true;
/*  85 */     Resources resources = getContext().getResources();
/*     */     try {
/*  87 */       COMPRESSED_QUALITY = resources.getInteger(resources.getIdentifier("rc_image_quality", "integer", getContext().getPackageName()));
/*  88 */       COMPRESSED_SIZE = resources.getInteger(resources.getIdentifier("rc_image_size", "integer", getContext().getPackageName()));
/*     */     } catch (Resources.NotFoundException e) {
/*  90 */       e.printStackTrace();
/*     */     }
/*  92 */     if ((model.getThumUri() != null) && (model.getThumUri().getScheme() != null) && (model.getThumUri().getScheme().equals("file")))
/*     */     {
/*  97 */       File file = new File(uri.toString() + "/image/thumbnail/" + name);
/*  98 */       if (file.exists()) {
/*  99 */         model.setThumUri(Uri.parse("file://" + uri.toString() + "/image/thumbnail/" + name));
/* 100 */         byte[] data = FileUtils.file2byte(file);
/* 101 */         if (data != null)
/* 102 */           model.setBase64(Base64.encodeToString(data, 2));
/*     */       } else {
/*     */         try {
/* 105 */           String thumbPath = model.getThumUri().toString().substring(5);
/* 106 */           RLog.d("ImageMessageHandler", "beforeEncodeMessage Thumbnail not save yet! " + thumbPath);
/* 107 */           BitmapFactory.decodeFile(thumbPath, options);
/* 108 */           if ((options.outWidth > THUMB_COMPRESSED_SIZE) || (options.outHeight > THUMB_COMPRESSED_SIZE)) {
/* 109 */             Bitmap bitmap = BitmapUtil.getThumbBitmap(getContext(), model.getThumUri(), THUMB_COMPRESSED_SIZE, THUMB_COMPRESSED_MIN_SIZE);
/*     */ 
/* 113 */             if (bitmap != null) {
/* 114 */               ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
/* 115 */               bitmap.compress(Bitmap.CompressFormat.JPEG, THUMB_COMPRESSED_QUALITY, outputStream);
/* 116 */               byte[] data = outputStream.toByteArray();
/* 117 */               model.setBase64(Base64.encodeToString(data, 2));
/* 118 */               outputStream.close();
/* 119 */               FileUtils.byte2File(data, uri.toString() + "/image/thumbnail/", name);
/* 120 */               model.setThumUri(Uri.parse("file://" + uri.toString() + "/image/thumbnail/" + name));
/* 121 */               if (!bitmap.isRecycled())
/* 122 */                 bitmap.recycle();
/*     */             }
/*     */           } else {
/* 125 */             File src = new File(thumbPath);
/* 126 */             byte[] data = FileUtils.file2byte(src);
/* 127 */             if (data != null) {
/* 128 */               model.setBase64(Base64.encodeToString(data, 2));
/* 129 */               String path = uri.toString() + "/image/thumbnail/";
/* 130 */               if (FileUtils.copyFile(src, path, name) != null)
/* 131 */                 model.setThumUri(Uri.parse("file://" + path + name));
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (IllegalArgumentException e) {
/* 136 */           e.printStackTrace();
/* 137 */           RLog.e("ImageMessageHandler", "beforeEncodeMessage Not Base64 Content!");
/*     */         } catch (IOException e) {
/* 139 */           e.printStackTrace();
/* 140 */           RLog.e("ImageMessageHandler", "beforeEncodeMessage IOException");
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 145 */     if ((model.getLocalUri() != null) && (model.getLocalUri().getScheme() != null) && (model.getLocalUri().getScheme().equals("file")))
/*     */     {
/* 149 */       File file = new File(uri.toString() + "/image/local/" + name);
/* 150 */       if (file.exists())
/* 151 */         model.setLocalUri(Uri.parse("file://" + uri.toString() + "/image/local/" + name));
/*     */       else
/*     */         try {
/* 154 */           String localPath = model.getLocalUri().toString().substring(5);
/* 155 */           BitmapFactory.decodeFile(localPath, options);
/* 156 */           if (((options.outWidth > COMPRESSED_SIZE) || (options.outHeight > COMPRESSED_SIZE)) && (!model.isFull())) {
/* 157 */             Bitmap bitmap = BitmapUtil.getResizedBitmap(getContext(), model.getLocalUri(), COMPRESSED_SIZE, COMPRESSED_SIZE);
/*     */ 
/* 161 */             if (bitmap != null) {
/* 162 */               String dir = uri.toString() + "/image/local/";
/* 163 */               file = new File(dir);
/* 164 */               if (!file.exists())
/* 165 */                 file.mkdirs();
/* 166 */               file = new File(dir + name);
/*     */ 
/* 168 */               BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
/* 169 */               bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSED_QUALITY, bos);
/* 170 */               bos.close();
/* 171 */               model.setLocalUri(Uri.parse("file://" + dir + name));
/* 172 */               if (!bitmap.isRecycled())
/* 173 */                 bitmap.recycle();
/*     */             }
/*     */           }
/* 176 */           else if (FileUtils.copyFile(new File(localPath), uri.toString() + "/image/local/", name) != null) {
/* 177 */             model.setLocalUri(Uri.parse("file://" + uri.toString() + "/image/local/" + name));
/*     */           }
/*     */         }
/*     */         catch (IOException e) {
/* 181 */           e.printStackTrace();
/* 182 */           RLog.e("ImageMessageHandler", "beforeEncodeMessage IOException");
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Uri obtainImageUri(Context context)
/*     */   {
/* 189 */     File file = context.getFilesDir();
/* 190 */     String path = file.getAbsolutePath();
/* 191 */     String userId = NativeClient.getInstance().getCurrentUserId();
/* 192 */     return Uri.parse(path + File.separator + userId);
/*     */   }
/*     */ 
/*     */   private static boolean isImageFile(byte[] data) {
/* 196 */     if ((data == null) || (data.length == 0)) {
/* 197 */       return false;
/*     */     }
/* 199 */     BitmapFactory.Options options = new BitmapFactory.Options();
/* 200 */     options.inJustDecodeBounds = true;
/* 201 */     BitmapFactory.decodeByteArray(data, 0, data.length, options);
/* 202 */     return options.outWidth != -1;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.ImageMessageHandler
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.imageloader.core.download;
/*     */ 
/*     */ import android.annotation.TargetApi;
/*     */ import android.content.ContentResolver;
/*     */ import android.content.Context;
/*     */ import android.content.res.AssetManager;
/*     */ import android.content.res.Resources;
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.Bitmap.CompressFormat;
/*     */ import android.media.ThumbnailUtils;
/*     */ import android.net.Uri;
/*     */ import android.os.Build.VERSION;
/*     */ import android.provider.ContactsContract.Contacts;
/*     */ import android.provider.MediaStore.Video.Thumbnails;
/*     */ import android.webkit.MimeTypeMap;
/*     */ import io.rong.imageloader.core.assist.ContentLengthInputStream;
/*     */ import io.rong.imageloader.utils.IoUtils;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ 
/*     */ public class BaseImageDownloader
/*     */   implements ImageDownloader
/*     */ {
/*     */   public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5000;
/*     */   public static final int DEFAULT_HTTP_READ_TIMEOUT = 20000;
/*     */   protected static final int BUFFER_SIZE = 32768;
/*     */   protected static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
/*     */   protected static final int MAX_REDIRECT_COUNT = 5;
/*     */   protected static final String CONTENT_CONTACTS_URI_PREFIX = "content://com.android.contacts/";
/*     */   private static final String ERROR_UNSUPPORTED_SCHEME = "UIL doesn't support scheme(protocol) by default [%s]. You should implement this support yourself (BaseImageDownloader.getStreamFromOtherSource(...))";
/*     */   protected final Context context;
/*     */   protected final int connectTimeout;
/*     */   protected final int readTimeout;
/*     */ 
/*     */   public BaseImageDownloader(Context context)
/*     */   {
/*  74 */     this(context, 5000, 20000);
/*     */   }
/*     */ 
/*     */   public BaseImageDownloader(Context context, int connectTimeout, int readTimeout) {
/*  78 */     this.context = context.getApplicationContext();
/*  79 */     this.connectTimeout = connectTimeout;
/*  80 */     this.readTimeout = readTimeout;
/*     */   }
/*     */ 
/*     */   public InputStream getStream(String imageUri, Object extra) throws IOException
/*     */   {
/*  85 */     switch (1.$SwitchMap$io$rong$imageloader$core$download$ImageDownloader$Scheme[ImageDownloader.Scheme.ofUri(imageUri).ordinal()]) {
/*     */     case 1:
/*     */     case 2:
/*  88 */       return getStreamFromNetwork(imageUri, extra);
/*     */     case 3:
/*  90 */       return getStreamFromFile(imageUri, extra);
/*     */     case 4:
/*  92 */       return getStreamFromContent(imageUri, extra);
/*     */     case 5:
/*  94 */       return getStreamFromAssets(imageUri, extra);
/*     */     case 6:
/*  96 */       return getStreamFromDrawable(imageUri, extra);
/*     */     case 7:
/*     */     }
/*  99 */     return getStreamFromOtherSource(imageUri, extra);
/*     */   }
/*     */ 
/*     */   protected InputStream getStreamFromNetwork(String imageUri, Object extra)
/*     */     throws IOException
/*     */   {
/* 114 */     HttpURLConnection conn = createConnection(imageUri, extra);
/*     */ 
/* 116 */     int redirectCount = 0;
/* 117 */     while ((conn.getResponseCode() / 100 == 3) && (redirectCount < 5)) {
/* 118 */       conn = createConnection(conn.getHeaderField("Location"), extra);
/* 119 */       redirectCount++;
/*     */     }
/*     */     InputStream imageStream;
/*     */     try {
/* 124 */       imageStream = conn.getInputStream();
/*     */     }
/*     */     catch (IOException e) {
/* 127 */       IoUtils.readAndCloseStream(conn.getErrorStream());
/* 128 */       throw e;
/*     */     }
/* 130 */     if (!shouldBeProcessed(conn)) {
/* 131 */       IoUtils.closeSilently(imageStream);
/* 132 */       throw new IOException("Image request failed with response code " + conn.getResponseCode());
/*     */     }
/*     */ 
/* 135 */     return new ContentLengthInputStream(new BufferedInputStream(imageStream, 32768), conn.getContentLength());
/*     */   }
/*     */ 
/*     */   protected boolean shouldBeProcessed(HttpURLConnection conn)
/*     */     throws IOException
/*     */   {
/* 145 */     return conn.getResponseCode() == 200;
/*     */   }
/*     */ 
/*     */   protected HttpURLConnection createConnection(String url, Object extra)
/*     */     throws IOException
/*     */   {
/* 159 */     String encodedUrl = Uri.encode(url, "@#&=*+-_.,:!?()/~'%");
/* 160 */     HttpURLConnection conn = (HttpURLConnection)new URL(encodedUrl).openConnection();
/* 161 */     conn.setConnectTimeout(this.connectTimeout);
/* 162 */     conn.setReadTimeout(this.readTimeout);
/* 163 */     return conn;
/*     */   }
/*     */ 
/*     */   protected InputStream getStreamFromFile(String imageUri, Object extra)
/*     */     throws IOException
/*     */   {
/* 176 */     String filePath = ImageDownloader.Scheme.FILE.crop(imageUri);
/* 177 */     if (isVideoFileUri(imageUri)) {
/* 178 */       return getVideoThumbnailStream(filePath);
/*     */     }
/* 180 */     BufferedInputStream imageStream = new BufferedInputStream(new FileInputStream(filePath), 32768);
/* 181 */     return new ContentLengthInputStream(imageStream, (int)new File(filePath).length());
/*     */   }
/*     */ 
/*     */   @TargetApi(8)
/*     */   private InputStream getVideoThumbnailStream(String filePath) {
/* 187 */     if (Build.VERSION.SDK_INT >= 8) {
/* 188 */       Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, 2);
/*     */ 
/* 190 */       if (bitmap != null) {
/* 191 */         ByteArrayOutputStream bos = new ByteArrayOutputStream();
/* 192 */         bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
/* 193 */         return new ByteArrayInputStream(bos.toByteArray());
/*     */       }
/*     */     }
/* 196 */     return null;
/*     */   }
/*     */ 
/*     */   protected InputStream getStreamFromContent(String imageUri, Object extra)
/*     */     throws FileNotFoundException
/*     */   {
/* 209 */     ContentResolver res = this.context.getContentResolver();
/*     */ 
/* 211 */     Uri uri = Uri.parse(imageUri);
/* 212 */     if (isVideoContentUri(uri)) {
/* 213 */       Long origId = Long.valueOf(uri.getLastPathSegment());
/* 214 */       Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(res, origId.longValue(), 1, null);
/*     */ 
/* 216 */       if (bitmap != null) {
/* 217 */         ByteArrayOutputStream bos = new ByteArrayOutputStream();
/* 218 */         bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
/* 219 */         return new ByteArrayInputStream(bos.toByteArray());
/*     */       }
/* 221 */     } else if (imageUri.startsWith("content://com.android.contacts/")) {
/* 222 */       return getContactPhotoStream(uri);
/*     */     }
/*     */ 
/* 225 */     return res.openInputStream(uri);
/*     */   }
/*     */   @TargetApi(14)
/*     */   protected InputStream getContactPhotoStream(Uri uri) {
/* 230 */     ContentResolver res = this.context.getContentResolver();
/* 231 */     if (Build.VERSION.SDK_INT >= 14) {
/* 232 */       return ContactsContract.Contacts.openContactPhotoInputStream(res, uri, true);
/*     */     }
/* 234 */     return ContactsContract.Contacts.openContactPhotoInputStream(res, uri);
/*     */   }
/*     */ 
/*     */   protected InputStream getStreamFromAssets(String imageUri, Object extra)
/*     */     throws IOException
/*     */   {
/* 248 */     String filePath = ImageDownloader.Scheme.ASSETS.crop(imageUri);
/* 249 */     return this.context.getAssets().open(filePath);
/*     */   }
/*     */ 
/*     */   protected InputStream getStreamFromDrawable(String imageUri, Object extra)
/*     */   {
/* 261 */     String drawableIdString = ImageDownloader.Scheme.DRAWABLE.crop(imageUri);
/* 262 */     int drawableId = Integer.parseInt(drawableIdString);
/* 263 */     return this.context.getResources().openRawResource(drawableId);
/*     */   }
/*     */ 
/*     */   protected InputStream getStreamFromOtherSource(String imageUri, Object extra)
/*     */     throws IOException
/*     */   {
/* 280 */     throw new UnsupportedOperationException(String.format("UIL doesn't support scheme(protocol) by default [%s]. You should implement this support yourself (BaseImageDownloader.getStreamFromOtherSource(...))", new Object[] { imageUri }));
/*     */   }
/*     */ 
/*     */   private boolean isVideoContentUri(Uri uri) {
/* 284 */     String mimeType = this.context.getContentResolver().getType(uri);
/* 285 */     return (mimeType != null) && (mimeType.startsWith("video/"));
/*     */   }
/*     */ 
/*     */   private boolean isVideoFileUri(String uri) {
/* 289 */     String extension = MimeTypeMap.getFileExtensionFromUrl(uri);
/* 290 */     String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
/* 291 */     return (mimeType != null) && (mimeType.startsWith("video/"));
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.download.BaseImageDownloader
 * JD-Core Version:    0.6.0
 */
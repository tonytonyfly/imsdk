/*     */ package io.rong.imageloader.core.decode;
/*     */ 
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.BitmapFactory;
/*     */ import android.graphics.BitmapFactory.Options;
/*     */ import android.graphics.Matrix;
/*     */ import android.media.ExifInterface;
/*     */ import io.rong.imageloader.core.assist.ImageScaleType;
/*     */ import io.rong.imageloader.core.assist.ImageSize;
/*     */ import io.rong.imageloader.core.download.ImageDownloader;
/*     */ import io.rong.imageloader.core.download.ImageDownloader.Scheme;
/*     */ import io.rong.imageloader.utils.ImageSizeUtils;
/*     */ import io.rong.imageloader.utils.IoUtils;
/*     */ import io.rong.imageloader.utils.L;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class BaseImageDecoder
/*     */   implements ImageDecoder
/*     */ {
/*     */   protected static final String LOG_SUBSAMPLE_IMAGE = "Subsample original image (%1$s) to %2$s (scale = %3$d) [%4$s]";
/*     */   protected static final String LOG_SCALE_IMAGE = "Scale subsampled image (%1$s) to %2$s (scale = %3$.5f) [%4$s]";
/*     */   protected static final String LOG_ROTATE_IMAGE = "Rotate image on %1$d° [%2$s]";
/*     */   protected static final String LOG_FLIP_IMAGE = "Flip image horizontally [%s]";
/*     */   protected static final String ERROR_NO_IMAGE_STREAM = "No stream for image [%s]";
/*     */   protected static final String ERROR_CANT_DECODE_IMAGE = "Image can't be decoded [%s]";
/*     */   protected final boolean loggingEnabled;
/*     */ 
/*     */   public BaseImageDecoder(boolean loggingEnabled)
/*     */   {
/*  57 */     this.loggingEnabled = loggingEnabled;
/*     */   }
/*     */ 
/*     */   public Bitmap decode(ImageDecodingInfo decodingInfo)
/*     */     throws IOException
/*     */   {
/*  74 */     InputStream imageStream = getImageStream(decodingInfo);
/*  75 */     if (imageStream == null) {
/*  76 */       L.e("No stream for image [%s]", new Object[] { decodingInfo.getImageKey() });
/*  77 */       return null; } ImageFileInfo imageInfo;
/*     */     Bitmap decodedBitmap;
/*     */     try { imageInfo = defineImageSizeAndRotation(imageStream, decodingInfo);
/*  81 */       imageStream = resetStream(imageStream, decodingInfo);
/*  82 */       BitmapFactory.Options decodingOptions = prepareDecodingOptions(imageInfo.imageSize, decodingInfo);
/*  83 */       decodedBitmap = BitmapFactory.decodeStream(imageStream, null, decodingOptions);
/*     */     } finally {
/*  85 */       IoUtils.closeSilently(imageStream);
/*     */     }
/*     */ 
/*  88 */     if (decodedBitmap == null)
/*  89 */       L.e("Image can't be decoded [%s]", new Object[] { decodingInfo.getImageKey() });
/*     */     else {
/*  91 */       decodedBitmap = considerExactScaleAndOrientatiton(decodedBitmap, decodingInfo, imageInfo.exif.rotation, imageInfo.exif.flipHorizontal);
/*     */     }
/*     */ 
/*  94 */     return decodedBitmap;
/*     */   }
/*     */ 
/*     */   protected InputStream getImageStream(ImageDecodingInfo decodingInfo) throws IOException {
/*  98 */     return decodingInfo.getDownloader().getStream(decodingInfo.getImageUri(), decodingInfo.getExtraForDownloader());
/*     */   }
/*     */ 
/*     */   protected ImageFileInfo defineImageSizeAndRotation(InputStream imageStream, ImageDecodingInfo decodingInfo) throws IOException
/*     */   {
/* 103 */     BitmapFactory.Options options = new BitmapFactory.Options();
/* 104 */     options.inJustDecodeBounds = true;
/* 105 */     BitmapFactory.decodeStream(imageStream, null, options);
/*     */ 
/* 108 */     String imageUri = decodingInfo.getImageUri();
/*     */     ExifInfo exif;
/*     */     ExifInfo exif;
/* 109 */     if ((decodingInfo.shouldConsiderExifParams()) && (canDefineExifParams(imageUri, options.outMimeType)))
/* 110 */       exif = defineExifOrientation(imageUri);
/*     */     else {
/* 112 */       exif = new ExifInfo();
/*     */     }
/* 114 */     return new ImageFileInfo(new ImageSize(options.outWidth, options.outHeight, exif.rotation), exif);
/*     */   }
/*     */ 
/*     */   private boolean canDefineExifParams(String imageUri, String mimeType) {
/* 118 */     return ("image/jpeg".equalsIgnoreCase(mimeType)) && (ImageDownloader.Scheme.ofUri(imageUri) == ImageDownloader.Scheme.FILE);
/*     */   }
/*     */ 
/*     */   protected ExifInfo defineExifOrientation(String imageUri) {
/* 122 */     int rotation = 0;
/* 123 */     boolean flip = false;
/*     */     try {
/* 125 */       ExifInterface exif = new ExifInterface(ImageDownloader.Scheme.FILE.crop(imageUri));
/* 126 */       int exifOrientation = exif.getAttributeInt("Orientation", 1);
/* 127 */       switch (exifOrientation) {
/*     */       case 2:
/* 129 */         flip = true;
/*     */       case 1:
/* 131 */         rotation = 0;
/* 132 */         break;
/*     */       case 7:
/* 134 */         flip = true;
/*     */       case 6:
/* 136 */         rotation = 90;
/* 137 */         break;
/*     */       case 4:
/* 139 */         flip = true;
/*     */       case 3:
/* 141 */         rotation = 180;
/* 142 */         break;
/*     */       case 5:
/* 144 */         flip = true;
/*     */       case 8:
/* 146 */         rotation = 270;
/*     */       }
/*     */     }
/*     */     catch (IOException e) {
/* 150 */       L.w("Can't read EXIF tags from file [%s]", new Object[] { imageUri });
/*     */     }
/* 152 */     return new ExifInfo(rotation, flip);
/*     */   }
/*     */ 
/*     */   protected BitmapFactory.Options prepareDecodingOptions(ImageSize imageSize, ImageDecodingInfo decodingInfo) {
/* 156 */     ImageScaleType scaleType = decodingInfo.getImageScaleType();
/*     */     int scale;
/*     */     int scale;
/* 158 */     if (scaleType == ImageScaleType.NONE) {
/* 159 */       scale = 1;
/*     */     }
/*     */     else
/*     */     {
/*     */       int scale;
/* 160 */       if (scaleType == ImageScaleType.NONE_SAFE) {
/* 161 */         scale = ImageSizeUtils.computeMinImageSampleSize(imageSize);
/*     */       } else {
/* 163 */         ImageSize targetSize = decodingInfo.getTargetSize();
/* 164 */         boolean powerOf2 = scaleType == ImageScaleType.IN_SAMPLE_POWER_OF_2;
/* 165 */         scale = ImageSizeUtils.computeImageSampleSize(imageSize, targetSize, decodingInfo.getViewScaleType(), powerOf2);
/*     */       }
/*     */     }
/* 167 */     if ((scale > 1) && (this.loggingEnabled)) {
/* 168 */       L.d("Subsample original image (%1$s) to %2$s (scale = %3$d) [%4$s]", new Object[] { imageSize, imageSize.scaleDown(scale), Integer.valueOf(scale), decodingInfo.getImageKey() });
/*     */     }
/*     */ 
/* 171 */     BitmapFactory.Options decodingOptions = decodingInfo.getDecodingOptions();
/* 172 */     decodingOptions.inSampleSize = scale;
/* 173 */     return decodingOptions;
/*     */   }
/*     */ 
/*     */   protected InputStream resetStream(InputStream imageStream, ImageDecodingInfo decodingInfo) throws IOException {
/* 177 */     if (imageStream.markSupported())
/*     */       try {
/* 179 */         imageStream.reset();
/* 180 */         return imageStream;
/*     */       }
/*     */       catch (IOException ignored) {
/*     */       }
/* 184 */     IoUtils.closeSilently(imageStream);
/* 185 */     return getImageStream(decodingInfo);
/*     */   }
/*     */ 
/*     */   protected Bitmap considerExactScaleAndOrientatiton(Bitmap subsampledBitmap, ImageDecodingInfo decodingInfo, int rotation, boolean flipHorizontal)
/*     */   {
/* 190 */     Matrix m = new Matrix();
/*     */ 
/* 192 */     ImageScaleType scaleType = decodingInfo.getImageScaleType();
/* 193 */     if ((scaleType == ImageScaleType.EXACTLY) || (scaleType == ImageScaleType.EXACTLY_STRETCHED)) {
/* 194 */       ImageSize srcSize = new ImageSize(subsampledBitmap.getWidth(), subsampledBitmap.getHeight(), rotation);
/* 195 */       float scale = ImageSizeUtils.computeImageScale(srcSize, decodingInfo.getTargetSize(), decodingInfo.getViewScaleType(), scaleType == ImageScaleType.EXACTLY_STRETCHED);
/*     */ 
/* 197 */       if (Float.compare(scale, 1.0F) != 0) {
/* 198 */         m.setScale(scale, scale);
/*     */ 
/* 200 */         if (this.loggingEnabled) {
/* 201 */           L.d("Scale subsampled image (%1$s) to %2$s (scale = %3$.5f) [%4$s]", new Object[] { srcSize, srcSize.scale(scale), Float.valueOf(scale), decodingInfo.getImageKey() });
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 206 */     if (flipHorizontal) {
/* 207 */       m.postScale(-1.0F, 1.0F);
/*     */ 
/* 209 */       if (this.loggingEnabled) L.d("Flip image horizontally [%s]", new Object[] { decodingInfo.getImageKey() });
/*     */     }
/*     */ 
/* 212 */     if (rotation != 0) {
/* 213 */       m.postRotate(rotation);
/*     */ 
/* 215 */       if (this.loggingEnabled) L.d("Rotate image on %1$d° [%2$s]", new Object[] { Integer.valueOf(rotation), decodingInfo.getImageKey() });
/*     */     }
/*     */ 
/* 218 */     Bitmap finalBitmap = Bitmap.createBitmap(subsampledBitmap, 0, 0, subsampledBitmap.getWidth(), subsampledBitmap.getHeight(), m, true);
/*     */ 
/* 220 */     if (finalBitmap != subsampledBitmap) {
/* 221 */       subsampledBitmap.recycle();
/*     */     }
/* 223 */     return finalBitmap;
/*     */   }
/*     */ 
/*     */   protected static class ImageFileInfo
/*     */   {
/*     */     public final ImageSize imageSize;
/*     */     public final BaseImageDecoder.ExifInfo exif;
/*     */ 
/*     */     protected ImageFileInfo(ImageSize imageSize, BaseImageDecoder.ExifInfo exif)
/*     */     {
/* 248 */       this.imageSize = imageSize;
/* 249 */       this.exif = exif;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static class ExifInfo
/*     */   {
/*     */     public final int rotation;
/*     */     public final boolean flipHorizontal;
/*     */ 
/*     */     protected ExifInfo()
/*     */     {
/* 232 */       this.rotation = 0;
/* 233 */       this.flipHorizontal = false;
/*     */     }
/*     */ 
/*     */     protected ExifInfo(int rotation, boolean flipHorizontal) {
/* 237 */       this.rotation = rotation;
/* 238 */       this.flipHorizontal = flipHorizontal;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.decode.BaseImageDecoder
 * JD-Core Version:    0.6.0
 */
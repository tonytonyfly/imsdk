/*     */ package io.rong.imkit.widget;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.res.Resources;
/*     */ import android.content.res.Resources.NotFoundException;
/*     */ import android.content.res.TypedArray;
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.Bitmap.Config;
/*     */ import android.graphics.BitmapFactory;
/*     */ import android.graphics.BitmapFactory.Options;
/*     */ import android.graphics.Canvas;
/*     */ import android.graphics.Paint;
/*     */ import android.graphics.PorterDuff.Mode;
/*     */ import android.graphics.PorterDuffXfermode;
/*     */ import android.graphics.Rect;
/*     */ import android.graphics.drawable.BitmapDrawable;
/*     */ import android.graphics.drawable.Drawable;
/*     */ import android.graphics.drawable.NinePatchDrawable;
/*     */ import android.net.Uri;
/*     */ import android.util.AttributeSet;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup.LayoutParams;
/*     */ import android.widget.ImageView;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imageloader.cache.disc.DiskCache;
/*     */ import io.rong.imageloader.core.DisplayImageOptions;
/*     */ import io.rong.imageloader.core.DisplayImageOptions.Builder;
/*     */ import io.rong.imageloader.core.ImageLoader;
/*     */ import io.rong.imageloader.core.assist.FailReason;
/*     */ import io.rong.imageloader.core.assist.ImageSize;
/*     */ import io.rong.imageloader.core.assist.LoadedFrom;
/*     */ import io.rong.imageloader.core.display.BitmapDisplayer;
/*     */ import io.rong.imageloader.core.display.CircleBitmapDisplayer;
/*     */ import io.rong.imageloader.core.display.RoundedBitmapDisplayer;
/*     */ import io.rong.imageloader.core.display.SimpleBitmapDisplayer;
/*     */ import io.rong.imageloader.core.imageaware.ImageViewAware;
/*     */ import io.rong.imageloader.core.listener.ImageLoadingListener;
/*     */ import io.rong.imageloader.core.process.BitmapProcessor;
/*     */ import io.rong.imkit.R.styleable;
/*     */ import io.rong.imkit.utils.CommonUtils;
/*     */ import java.io.File;
/*     */ import java.lang.ref.WeakReference;
/*     */ 
/*     */ public class AsyncImageView extends ImageView
/*     */ {
/*     */   private static final String TAG = "AsyncImageView";
/*     */   private boolean isCircle;
/*  45 */   private float minShortSideSize = 0.0F;
/*  46 */   private int mCornerRadius = 0;
/*     */   private static final int AVATAR_SIZE = 80;
/*     */   private Drawable mDefaultDrawable;
/*     */   private WeakReference<Bitmap> mWeakBitmap;
/*     */   private WeakReference<Bitmap> mShardWeakBitmap;
/*     */   private boolean mHasMask;
/*     */ 
/*     */   public AsyncImageView(Context context)
/*     */   {
/*  55 */     super(context);
/*     */   }
/*     */ 
/*     */   public AsyncImageView(Context context, AttributeSet attrs) {
/*  59 */     super(context, attrs);
/*  60 */     if (isInEditMode()) return;
/*  61 */     TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AsyncImageView);
/*  62 */     int resId = a.getResourceId(R.styleable.AsyncImageView_RCDefDrawable, 0);
/*  63 */     this.isCircle = (a.getInt(R.styleable.AsyncImageView_RCShape, 0) == 1);
/*  64 */     this.minShortSideSize = a.getDimension(R.styleable.AsyncImageView_RCMinShortSideSize, 0.0F);
/*  65 */     this.mCornerRadius = (int)a.getDimension(R.styleable.AsyncImageView_RCCornerRadius, 0.0F);
/*  66 */     this.mHasMask = a.getBoolean(R.styleable.AsyncImageView_RCMask, false);
/*     */ 
/*  68 */     if (resId != 0) {
/*  69 */       this.mDefaultDrawable = getResources().getDrawable(resId);
/*     */     }
/*  71 */     a.recycle();
/*     */ 
/*  73 */     if (this.mDefaultDrawable != null) {
/*  74 */       DisplayImageOptions options = createDisplayImageOptions(resId, false);
/*  75 */       Drawable drawable = options.getImageForEmptyUri(null);
/*  76 */       Bitmap bitmap = drawableToBitmap(drawable);
/*  77 */       ImageViewAware imageViewAware = new ImageViewAware(this);
/*  78 */       options.getDisplayer().display(bitmap, imageViewAware, LoadedFrom.DISC_CACHE);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void onDraw(Canvas canvas)
/*     */   {
/*  84 */     if (this.mHasMask) {
/*  85 */       Bitmap bitmap = this.mWeakBitmap == null ? null : (Bitmap)this.mWeakBitmap.get();
/*  86 */       Drawable drawable = getDrawable();
/*  87 */       RCMessageFrameLayout parent = (RCMessageFrameLayout)getParent();
/*  88 */       Drawable background = parent.getBackgroundDrawable();
/*     */ 
/*  90 */       if ((bitmap == null) || (bitmap.isRecycled())) {
/*  91 */         int width = getWidth();
/*  92 */         int height = getHeight();
/*  93 */         if ((width <= 0) || (height <= 0))
/*  94 */           return;
/*     */         try {
/*  96 */           bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
/*     */         } catch (OutOfMemoryError e) {
/*  98 */           RLog.e("AsyncImageView", "onDraw OutOfMemoryError");
/*  99 */           e.printStackTrace();
/* 100 */           System.gc();
/*     */         }
/* 102 */         if (bitmap != null) {
/* 103 */           Canvas rCanvas = new Canvas(bitmap);
/* 104 */           if (drawable != null) {
/* 105 */             drawable.setBounds(0, 0, width, height);
/* 106 */             drawable.draw(rCanvas);
/* 107 */             if ((background != null) && ((background instanceof NinePatchDrawable))) {
/* 108 */               NinePatchDrawable patchDrawable = (NinePatchDrawable)background;
/* 109 */               patchDrawable.setBounds(0, 0, width, height);
/* 110 */               Paint maskPaint = patchDrawable.getPaint();
/* 111 */               maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
/* 112 */               patchDrawable.draw(rCanvas);
/*     */             }
/* 114 */             this.mWeakBitmap = new WeakReference(bitmap);
/*     */           }
/* 116 */           canvas.drawBitmap(bitmap, 0.0F, 0.0F, null);
/* 117 */           getShardImage(background, bitmap, canvas);
/*     */         }
/*     */       } else {
/* 120 */         canvas.drawBitmap(bitmap, 0.0F, 0.0F, null);
/* 121 */         getShardImage(background, bitmap, canvas);
/*     */       }
/*     */     } else {
/* 124 */       super.onDraw(canvas);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void getShardImage(Drawable drawable_bg, Bitmap bp, Canvas canvas) {
/* 129 */     int width = bp.getWidth();
/* 130 */     int height = bp.getHeight();
/* 131 */     Bitmap bitmap = this.mShardWeakBitmap == null ? null : (Bitmap)this.mShardWeakBitmap.get();
/*     */ 
/* 133 */     if ((width <= 0) || (height <= 0))
/* 134 */       return;
/* 135 */     if ((bitmap == null) || (bitmap.isRecycled())) {
/*     */       try {
/* 137 */         bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
/*     */       } catch (OutOfMemoryError e) {
/* 139 */         RLog.e("AsyncImageView", "getShardImage OutOfMemoryError");
/* 140 */         e.printStackTrace();
/* 141 */         System.gc();
/*     */       }
/*     */ 
/* 144 */       if (bitmap != null) {
/* 145 */         Canvas rCanvas = new Canvas(bitmap);
/* 146 */         Paint paint = new Paint();
/* 147 */         paint.setAntiAlias(true);
/* 148 */         Rect rect = new Rect(0, 0, width, height);
/* 149 */         Rect rectF = new Rect(1, 1, width - 1, height - 1);
/*     */ 
/* 151 */         BitmapDrawable drawable_in = new BitmapDrawable(bp);
/* 152 */         drawable_in.setBounds(rectF);
/* 153 */         drawable_in.draw(rCanvas);
/* 154 */         if ((drawable_bg instanceof NinePatchDrawable)) {
/* 155 */           NinePatchDrawable patchDrawable = (NinePatchDrawable)drawable_bg;
/* 156 */           patchDrawable.setBounds(rect);
/* 157 */           Paint maskPaint = patchDrawable.getPaint();
/* 158 */           maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
/* 159 */           patchDrawable.draw(rCanvas);
/*     */         }
/* 161 */         this.mShardWeakBitmap = new WeakReference(bitmap);
/* 162 */         canvas.drawBitmap(bitmap, 0.0F, 0.0F, paint);
/*     */       }
/*     */     } else {
/* 165 */       canvas.drawBitmap(bitmap, 0.0F, 0.0F, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void onDetachedFromWindow()
/*     */   {
/* 171 */     if (this.mWeakBitmap != null) {
/* 172 */       Bitmap bitmap = (Bitmap)this.mWeakBitmap.get();
/* 173 */       if ((bitmap != null) && (!bitmap.isRecycled()))
/* 174 */         bitmap.recycle();
/* 175 */       this.mWeakBitmap = null;
/*     */     }
/* 177 */     if (this.mShardWeakBitmap != null) {
/* 178 */       Bitmap bitmap = (Bitmap)this.mShardWeakBitmap.get();
/* 179 */       if ((bitmap != null) && (!bitmap.isRecycled()))
/* 180 */         bitmap.recycle();
/* 181 */       this.mShardWeakBitmap = null;
/*     */     }
/* 183 */     super.onDetachedFromWindow();
/*     */   }
/*     */ 
/*     */   public void invalidate()
/*     */   {
/* 188 */     if (this.mWeakBitmap != null) {
/* 189 */       Bitmap bitmap = (Bitmap)this.mWeakBitmap.get();
/* 190 */       if ((bitmap != null) && (!bitmap.isRecycled()))
/* 191 */         bitmap.recycle();
/* 192 */       this.mWeakBitmap = null;
/*     */     }
/* 194 */     if (this.mShardWeakBitmap != null) {
/* 195 */       Bitmap bitmap = (Bitmap)this.mShardWeakBitmap.get();
/* 196 */       if ((bitmap != null) && (!bitmap.isRecycled()))
/* 197 */         bitmap.recycle();
/* 198 */       this.mShardWeakBitmap = null;
/*     */     }
/* 200 */     super.invalidate();
/*     */   }
/*     */ 
/*     */   public void setDefaultDrawable()
/*     */   {
/* 207 */     if (this.mDefaultDrawable != null) {
/* 208 */       DisplayImageOptions options = createDisplayImageOptions(0, false);
/* 209 */       Bitmap bitmap = drawableToBitmap(this.mDefaultDrawable);
/* 210 */       ImageViewAware imageViewAware = new ImageViewAware(this);
/* 211 */       options.getDisplayer().display(bitmap, imageViewAware, LoadedFrom.DISC_CACHE);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setResource(Uri imageUri)
/*     */   {
/* 221 */     DisplayImageOptions options = createDisplayImageOptions(0, true);
/* 222 */     if ((this.minShortSideSize > 0.0F) && (imageUri != null)) {
/* 223 */       File file = new File(imageUri.getPath());
/* 224 */       if (!file.exists()) {
/* 225 */         ImageViewAware imageViewAware = new ImageViewAware(this);
/* 226 */         ImageLoader.getInstance().displayImage(imageUri.toString(), imageViewAware, options, null, null);
/*     */       } else {
/* 228 */         Bitmap bitmap = getBitmap(imageUri);
/* 229 */         if (bitmap != null) {
/* 230 */           setLayoutParam(bitmap);
/* 231 */           setImageBitmap(bitmap);
/*     */         } else {
/* 233 */           setImageBitmap(null);
/* 234 */           ViewGroup.LayoutParams params = getLayoutParams();
/* 235 */           params.height = CommonUtils.dip2px(getContext(), 80.0F);
/* 236 */           params.width = CommonUtils.dip2px(getContext(), 110.0F);
/* 237 */           setLayoutParams(params);
/*     */         }
/*     */       }
/*     */     }
/* 241 */     else if (imageUri != null) {
/* 242 */       ImageLoader.getInstance().displayImage(imageUri.toString(), this, options);
/*     */     } else {
/* 244 */       setImageBitmap(null);
/* 245 */       ViewGroup.LayoutParams params = getLayoutParams();
/* 246 */       params.height = CommonUtils.dip2px(getContext(), 80.0F);
/* 247 */       params.width = CommonUtils.dip2px(getContext(), 110.0F);
/* 248 */       setLayoutParams(params);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setLocationResource(Uri imageUri, int defRes, int w, int h, IImageLoadingListener loadingListener)
/*     */   {
/* 259 */     DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
/* 260 */     DisplayImageOptions options = builder.resetViewBeforeLoading(false).cacheInMemory(false).cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888).showImageOnLoading(defRes).preProcessor(new BitmapProcessor(w, h)
/*     */     {
/*     */       public Bitmap process(Bitmap bitmap)
/*     */       {
/* 268 */         int widthOrg = bitmap.getWidth();
/* 269 */         int heightOrg = bitmap.getHeight();
/* 270 */         int xTopLeft = (widthOrg - this.val$w) / 2;
/* 271 */         int yTopLeft = (heightOrg - this.val$h) / 2;
/*     */ 
/* 273 */         if ((xTopLeft <= 0) || (yTopLeft <= 0)) {
/* 274 */           return bitmap;
/*     */         }
/*     */         try
/*     */         {
/* 278 */           Bitmap result = Bitmap.createBitmap(bitmap, xTopLeft, yTopLeft, this.val$w, this.val$h);
/* 279 */           if (!bitmap.isRecycled())
/* 280 */             bitmap.recycle();
/* 281 */           return result; } catch (OutOfMemoryError e) {
/*     */         }
/* 283 */         return null;
/*     */       }
/*     */     }).build();
/*     */ 
/* 288 */     ImageLoader.getInstance().displayImage(imageUri == null ? null : imageUri.toString(), this, options, new ImageLoadingListener(loadingListener)
/*     */     {
/*     */       public void onLoadingStarted(String imageUri, View view)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onLoadingFailed(String imageUri, View view, FailReason failReason)
/*     */       {
/* 296 */         this.val$loadingListener.onLoadingFail();
/*     */       }
/*     */ 
/*     */       public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
/*     */       {
/* 301 */         File file = ImageLoader.getInstance().getDiskCache().get(imageUri);
/* 302 */         if ((file != null) && (file.exists()))
/* 303 */           this.val$loadingListener.onLoadingComplete(Uri.fromFile(file));
/*     */         else
/* 305 */           this.val$loadingListener.onLoadingFail();
/*     */       }
/*     */ 
/*     */       public void onLoadingCancelled(String imageUri, View view)
/*     */       {
/* 310 */         this.val$loadingListener.onLoadingFail();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void setResource(String imageUri, int defaultResId)
/*     */   {
/* 323 */     if ((imageUri == null) && (defaultResId <= 0)) {
/* 324 */       return;
/*     */     }
/*     */ 
/* 327 */     DisplayImageOptions options = createDisplayImageOptions(defaultResId, true);
/* 328 */     ImageLoader.getInstance().displayImage(imageUri, this, options);
/*     */   }
/*     */ 
/*     */   private Bitmap drawableToBitmap(Drawable drawable) {
/* 332 */     int width = drawable.getIntrinsicWidth();
/* 333 */     int height = drawable.getIntrinsicHeight();
/* 334 */     Bitmap.Config config = drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
/* 335 */     Bitmap bitmap = Bitmap.createBitmap(width, height, config);
/* 336 */     Canvas canvas = new Canvas(bitmap);
/* 337 */     drawable.setBounds(0, 0, width, height);
/* 338 */     drawable.draw(canvas);
/* 339 */     return bitmap;
/*     */   }
/*     */ 
/*     */   public void setAvatar(String imageUri, int defaultResId)
/*     */   {
/* 351 */     ImageViewAware imageViewAware = new ImageViewAware(this);
/* 352 */     ImageSize imageSize = new ImageSize(80, 80);
/* 353 */     DisplayImageOptions options = createDisplayImageOptions(defaultResId, true);
/* 354 */     ImageLoader.getInstance().displayImage(imageUri, imageViewAware, options, imageSize, null, null);
/*     */   }
/*     */ 
/*     */   public void setAvatar(Uri imageUri)
/*     */   {
/* 367 */     if (imageUri != null) {
/* 368 */       ImageViewAware imageViewAware = new ImageViewAware(this);
/* 369 */       ImageSize imageSize = new ImageSize(80, 80);
/* 370 */       DisplayImageOptions options = createDisplayImageOptions(0, true);
/* 371 */       ImageLoader.getInstance().displayImage(imageUri.toString(), imageViewAware, options, imageSize, null, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Bitmap getBitmap(Uri uri)
/*     */   {
/* 377 */     Bitmap bitmap = null;
/* 378 */     BitmapFactory.Options options = new BitmapFactory.Options();
/* 379 */     options.inJustDecodeBounds = true;
/* 380 */     options = new BitmapFactory.Options();
/*     */     try
/*     */     {
/* 383 */       bitmap = BitmapFactory.decodeFile(uri.getPath(), options);
/*     */     } catch (Exception e) {
/* 385 */       RLog.e("AsyncImageView", "getBitmap Exception : " + uri);
/* 386 */       e.printStackTrace();
/*     */     }
/* 388 */     return bitmap;
/*     */   }
/*     */ 
/*     */   private DisplayImageOptions createDisplayImageOptions(int defaultResId, boolean cacheInMemory) {
/* 392 */     DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
/* 393 */     Drawable defaultDrawable = this.mDefaultDrawable;
/* 394 */     if (defaultResId > 0) {
/*     */       try {
/* 396 */         defaultDrawable = getContext().getResources().getDrawable(defaultResId);
/*     */       } catch (Resources.NotFoundException e) {
/* 398 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */ 
/* 402 */     if (defaultDrawable != null) {
/* 403 */       builder.showImageOnLoading(defaultDrawable);
/* 404 */       builder.showImageForEmptyUri(defaultDrawable);
/* 405 */       builder.showImageOnFail(defaultDrawable);
/*     */     }
/*     */ 
/* 408 */     if (this.isCircle)
/* 409 */       builder.displayer(new CircleBitmapDisplayer());
/* 410 */     else if (this.mCornerRadius > 0)
/* 411 */       builder.displayer(new RoundedBitmapDisplayer(this.mCornerRadius));
/*     */     else {
/* 413 */       builder.displayer(new SimpleBitmapDisplayer());
/*     */     }
/*     */ 
/* 416 */     DisplayImageOptions options = builder.resetViewBeforeLoading(false).cacheInMemory(cacheInMemory).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
/*     */ 
/* 421 */     return options;
/*     */   }
/*     */ 
/*     */   private void setLayoutParam(Bitmap bitmap) {
/* 425 */     float width = bitmap.getWidth();
/* 426 */     float height = bitmap.getHeight();
/*     */ 
/* 429 */     int minSize = 100;
/*     */ 
/* 431 */     if (this.minShortSideSize > 0.0F)
/* 432 */       if ((width <= this.minShortSideSize) || (height <= this.minShortSideSize)) {
/* 433 */         float scale = width / height;
/*     */         int finalWidth;
/*     */         int finalHeight;
/*     */         int finalWidth;
/* 435 */         if (scale > 1.0F) {
/* 436 */           int finalHeight = (int)(this.minShortSideSize / scale);
/* 437 */           if (finalHeight < minSize) {
/* 438 */             finalHeight = minSize;
/*     */           }
/* 440 */           finalWidth = (int)this.minShortSideSize;
/*     */         } else {
/* 442 */           finalHeight = (int)this.minShortSideSize;
/* 443 */           finalWidth = (int)(this.minShortSideSize * scale);
/* 444 */           if (finalWidth < minSize) {
/* 445 */             finalWidth = minSize;
/*     */           }
/*     */         }
/*     */ 
/* 449 */         ViewGroup.LayoutParams params = getLayoutParams();
/* 450 */         params.height = finalHeight;
/* 451 */         params.width = finalWidth;
/* 452 */         setLayoutParams(params);
/*     */       } else {
/* 454 */         ViewGroup.LayoutParams params = getLayoutParams();
/* 455 */         params.height = (int)height;
/* 456 */         params.width = (int)width;
/* 457 */         setLayoutParams(params);
/*     */       }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.AsyncImageView
 * JD-Core Version:    0.6.0
 */
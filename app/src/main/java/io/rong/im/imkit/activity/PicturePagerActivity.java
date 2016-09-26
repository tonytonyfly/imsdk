/*     */ package io.rong.imkit.activity;
/*     */ 
/*     */ import android.app.Activity;
/*     */ import android.content.Context;
/*     */ import android.content.Intent;
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.Bitmap.Config;
/*     */ import android.graphics.drawable.Drawable;
/*     */ import android.net.Uri;
/*     */ import android.os.Bundle;
/*     */ import android.os.Handler;
/*     */ import android.support.v4.view.PagerAdapter;
/*     */ import android.support.v4.view.ViewPager.OnPageChangeListener;
/*     */ import android.text.TextUtils;
/*     */ import android.view.LayoutInflater;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.View.OnLongClickListener;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.ProgressBar;
/*     */ import android.widget.TextView;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imageloader.cache.disc.DiskCache;
/*     */ import io.rong.imageloader.core.DisplayImageOptions;
/*     */ import io.rong.imageloader.core.DisplayImageOptions.Builder;
/*     */ import io.rong.imageloader.core.ImageLoader;
/*     */ import io.rong.imageloader.core.assist.FailReason;
/*     */ import io.rong.imageloader.core.imageaware.ImageAware;
/*     */ import io.rong.imageloader.core.imageaware.ImageViewAware;
/*     */ import io.rong.imageloader.core.listener.ImageLoadingListener;
/*     */ import io.rong.imageloader.core.listener.ImageLoadingProgressListener;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.widget.HackyViewPager;
/*     */ import io.rong.imkit.widget.PicturePopupWindow;
/*     */ import io.rong.imlib.RongCommonDefine.GetMessageDirection;
/*     */ import io.rong.imlib.RongIMClient;
/*     */ import io.rong.imlib.RongIMClient.ErrorCode;
/*     */ import io.rong.imlib.RongIMClient.ResultCallback;
/*     */ import io.rong.imlib.model.Conversation.ConversationType;
/*     */ import io.rong.imlib.model.Message;
/*     */ import io.rong.message.ImageMessage;
/*     */ import io.rong.photoview.PhotoView;
/*     */ import io.rong.photoview.PhotoViewAttacher.OnPhotoTapListener;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ public class PicturePagerActivity extends Activity
/*     */ {
/*     */   private static final String TAG = "PicturePagerActivity";
/*     */   private static final int IMAGE_MESSAGE_COUNT = 10;
/*     */   private HackyViewPager mViewPager;
/*     */   private ImageMessage mCurrentImageMessage;
/*     */   private Conversation.ConversationType mConversationType;
/*     */   private int mCurrentMessageId;
/*     */   private String mTargetId;
/*     */   private int mCurrentIndex;
/*     */   private ImageAware mDownloadingImageAware;
/*     */   private ImageAdapter mImageAdapter;
/*     */   private boolean isFirstTime;
/*     */   private ViewPager.OnPageChangeListener mPageChangeListener;
/*     */ 
/*     */   public PicturePagerActivity()
/*     */   {
/*  51 */     this.mTargetId = null;
/*  52 */     this.mCurrentIndex = 0;
/*     */ 
/*  55 */     this.isFirstTime = false;
/*  56 */     this.mPageChangeListener = new ViewPager.OnPageChangeListener()
/*     */     {
/*     */       public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onPageSelected(int position) {
/*  63 */         RLog.i("PicturePagerActivity", "onPageSelected. position:" + position);
/*  64 */         PicturePagerActivity.access$002(PicturePagerActivity.this, position);
/*  65 */         View view = PicturePagerActivity.this.mViewPager.findViewById(position);
/*  66 */         if (view != null)
/*  67 */           PicturePagerActivity.this.mImageAdapter.updatePhotoView(position, view);
/*  68 */         if (position == PicturePagerActivity.this.mImageAdapter.getCount() - 1)
/*  69 */           PicturePagerActivity.this.getConversationImageUris(PicturePagerActivity.this.mImageAdapter.getItem(position).getMessageId(), RongCommonDefine.GetMessageDirection.BEHIND);
/*  70 */         else if (position == 0)
/*  71 */           PicturePagerActivity.this.getConversationImageUris(PicturePagerActivity.this.mImageAdapter.getItem(position).getMessageId(), RongCommonDefine.GetMessageDirection.FRONT);
/*     */       }
/*     */ 
/*     */       public void onPageScrollStateChanged(int state)
/*     */       {
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected void onCreate(Bundle savedInstanceState)
/*     */   {
/*  83 */     super.onCreate(savedInstanceState);
/*  84 */     setContentView(R.layout.rc_fr_photo);
/*  85 */     Message currentMessage = (Message)getIntent().getParcelableExtra("message");
/*  86 */     this.mCurrentImageMessage = ((ImageMessage)currentMessage.getContent());
/*  87 */     this.mConversationType = currentMessage.getConversationType();
/*  88 */     this.mCurrentMessageId = currentMessage.getMessageId();
/*  89 */     this.mTargetId = currentMessage.getTargetId();
/*     */ 
/*  91 */     this.mViewPager = ((HackyViewPager)findViewById(R.id.viewpager));
/*  92 */     this.mViewPager.setOnPageChangeListener(this.mPageChangeListener);
/*  93 */     this.mImageAdapter = new ImageAdapter(null);
/*  94 */     this.isFirstTime = true;
/*  95 */     AlbumBitmapCacheHelper.init(this);
/*     */ 
/*  97 */     getConversationImageUris(this.mCurrentMessageId, RongCommonDefine.GetMessageDirection.FRONT);
/*  98 */     getConversationImageUris(this.mCurrentMessageId, RongCommonDefine.GetMessageDirection.BEHIND);
/*     */   }
/*     */ 
/*     */   private void getConversationImageUris(int mesageId, RongCommonDefine.GetMessageDirection direction) {
/* 102 */     if ((this.mConversationType != null) && (!TextUtils.isEmpty(this.mTargetId)))
/* 103 */       RongIMClient.getInstance().getHistoryMessages(this.mConversationType, this.mTargetId, "RC:ImgMsg", mesageId, 10, direction, new RongIMClient.ResultCallback(direction)
/*     */       {
/*     */         public void onSuccess(List<Message> messages)
/*     */         {
/* 107 */           ArrayList lists = new ArrayList();
/* 108 */           if (messages != null) {
/* 109 */             if (this.val$direction.equals(RongCommonDefine.GetMessageDirection.FRONT))
/* 110 */               Collections.reverse(messages);
/* 111 */             for (int i = 0; i < messages.size(); i++) {
/* 112 */               Message message = (Message)messages.get(i);
/* 113 */               if ((message.getContent() instanceof ImageMessage)) {
/* 114 */                 ImageMessage imageMessage = (ImageMessage)message.getContent();
/* 115 */                 Uri largeImageUri = imageMessage.getLocalUri() == null ? imageMessage.getRemoteUri() : imageMessage.getLocalUri();
/*     */ 
/* 117 */                 if ((imageMessage.getThumUri() != null) && (largeImageUri != null)) {
/* 118 */                   lists.add(new PicturePagerActivity.ImageInfo(PicturePagerActivity.this, message.getMessageId(), imageMessage.getThumUri(), largeImageUri));
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/* 123 */           if ((this.val$direction.equals(RongCommonDefine.GetMessageDirection.FRONT)) && (PicturePagerActivity.this.isFirstTime)) {
/* 124 */             lists.add(new PicturePagerActivity.ImageInfo(PicturePagerActivity.this, PicturePagerActivity.this.mCurrentMessageId, PicturePagerActivity.this.mCurrentImageMessage.getThumUri(), PicturePagerActivity.this.mCurrentImageMessage.getLocalUri() == null ? PicturePagerActivity.this.mCurrentImageMessage.getRemoteUri() : PicturePagerActivity.this.mCurrentImageMessage.getLocalUri()));
/*     */ 
/* 126 */             PicturePagerActivity.this.mImageAdapter.addData(lists, this.val$direction.equals(RongCommonDefine.GetMessageDirection.FRONT));
/* 127 */             PicturePagerActivity.this.mViewPager.setAdapter(PicturePagerActivity.this.mImageAdapter);
/* 128 */             PicturePagerActivity.access$602(PicturePagerActivity.this, false);
/* 129 */             PicturePagerActivity.this.mViewPager.setCurrentItem(lists.size() - 1);
/* 130 */             PicturePagerActivity.access$002(PicturePagerActivity.this, lists.size() - 1);
/* 131 */           } else if (lists.size() > 0) {
/* 132 */             PicturePagerActivity.this.mImageAdapter.addData(lists, this.val$direction.equals(RongCommonDefine.GetMessageDirection.FRONT));
/* 133 */             PicturePagerActivity.this.mImageAdapter.notifyDataSetChanged();
/* 134 */             if (this.val$direction.equals(RongCommonDefine.GetMessageDirection.FRONT)) {
/* 135 */               PicturePagerActivity.this.mViewPager.setCurrentItem(lists.size());
/* 136 */               PicturePagerActivity.access$002(PicturePagerActivity.this, lists.size());
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/*     */         public void onError(RongIMClient.ErrorCode e)
/*     */         {
/*     */         }
/*     */       });
/*     */   }
/*     */ 
/*     */   protected void onPause()
/*     */   {
/* 151 */     super.onPause();
/* 152 */     if (isFinishing())
/* 153 */       AlbumBitmapCacheHelper.getInstance().uninit();
/*     */   }
/*     */ 
/*     */   protected void onDestroy()
/*     */   {
/* 159 */     super.onDestroy();
/*     */   }
/*     */ 
/*     */   private class ImageInfo
/*     */   {
/*     */     private int messageId;
/*     */     private Uri thumbUri;
/*     */     private Uri largeImageUri;
/*     */ 
/*     */     ImageInfo(int messageId, Uri thumbnail, Uri largeImageUri)
/*     */     {
/* 390 */       this.messageId = messageId;
/* 391 */       this.thumbUri = thumbnail;
/* 392 */       this.largeImageUri = largeImageUri;
/*     */     }
/*     */ 
/*     */     public int getMessageId() {
/* 396 */       return this.messageId;
/*     */     }
/*     */ 
/*     */     public Uri getLargeImageUri() {
/* 400 */       return this.largeImageUri;
/*     */     }
/*     */ 
/*     */     public Uri getThumbUri() {
/* 404 */       return this.thumbUri;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ImageAdapter extends PagerAdapter
/*     */   {
/* 163 */     private ArrayList<PicturePagerActivity.ImageInfo> mImageList = new ArrayList();
/*     */     private PicturePopupWindow menuWindow;
/* 172 */     private View.OnClickListener onMenuWindowClick = new View.OnClickListener()
/*     */     {
/*     */       public void onClick(View v) {
/* 175 */         if (v.getId() == R.id.rc_content);
/* 178 */         PicturePagerActivity.ImageAdapter.this.menuWindow.dismiss();
/*     */       }
/* 172 */     };
/*     */ 
/*     */     private ImageAdapter()
/*     */     {
/*     */     }
/*     */ 
/*     */     private View newView(Context context, PicturePagerActivity.ImageInfo imageInfo)
/*     */     {
/* 183 */       View result = LayoutInflater.from(context).inflate(R.layout.rc_fr_image, null);
/*     */ 
/* 185 */       ViewHolder holder = new ViewHolder();
/* 186 */       holder.progressBar = ((ProgressBar)result.findViewById(R.id.rc_progress));
/* 187 */       holder.progressText = ((TextView)result.findViewById(R.id.rc_txt));
/* 188 */       holder.photoView = ((PhotoView)result.findViewById(R.id.rc_photoView));
/* 189 */       holder.photoView.setOnLongClickListener(new View.OnLongClickListener(imageInfo)
/*     */       {
/*     */         public boolean onLongClick(View v) {
/* 192 */           Uri uri = this.val$imageInfo.getLargeImageUri();
/* 193 */           File file = null;
/* 194 */           if (uri != null) {
/* 195 */             if ((uri.getScheme().startsWith("http")) || (uri.getScheme().startsWith("https")))
/* 196 */               file = ImageLoader.getInstance().getDiskCache().get(uri.toString());
/*     */             else
/* 198 */               file = new File(uri.getPath());
/*     */           }
/* 200 */           PicturePagerActivity.ImageAdapter.access$902(PicturePagerActivity.ImageAdapter.this, new PicturePopupWindow(PicturePagerActivity.this, file));
/* 201 */           PicturePagerActivity.ImageAdapter.this.menuWindow.showAtLocation(v, 81, 0, 0);
/* 202 */           PicturePagerActivity.ImageAdapter.this.menuWindow.setOutsideTouchable(false);
/* 203 */           return false;
/*     */         }
/*     */       });
/* 206 */       holder.photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener()
/*     */       {
/*     */         public void onPhotoTap(View view, float x, float y) {
/* 209 */           PicturePagerActivity.this.finish();
/*     */         }
/*     */ 
/*     */         public void onOutsidePhotoTap()
/*     */         {
/*     */         }
/*     */       });
/* 217 */       result.setTag(holder);
/*     */ 
/* 219 */       return result;
/*     */     }
/*     */ 
/*     */     public void addData(ArrayList<PicturePagerActivity.ImageInfo> newImages, boolean direction) {
/* 223 */       if ((newImages == null) || (newImages.size() == 0))
/* 224 */         return;
/* 225 */       if (this.mImageList.size() == 0) {
/* 226 */         this.mImageList.addAll(newImages);
/* 227 */       } else if ((direction) && (!PicturePagerActivity.this.isFirstTime) && (!isDuplicate(((PicturePagerActivity.ImageInfo)newImages.get(0)).getMessageId()))) {
/* 228 */         ArrayList temp = new ArrayList();
/* 229 */         temp.addAll(this.mImageList);
/* 230 */         this.mImageList.clear();
/* 231 */         this.mImageList.addAll(newImages);
/* 232 */         this.mImageList.addAll(this.mImageList.size(), temp);
/* 233 */       } else if ((!PicturePagerActivity.this.isFirstTime) && (!isDuplicate(((PicturePagerActivity.ImageInfo)newImages.get(0)).getMessageId()))) {
/* 234 */         this.mImageList.addAll(this.mImageList.size(), newImages);
/*     */       }
/*     */     }
/*     */ 
/*     */     private boolean isDuplicate(int messageId) {
/* 239 */       for (PicturePagerActivity.ImageInfo info : this.mImageList) {
/* 240 */         if (info.getMessageId() == messageId)
/* 241 */           return true;
/*     */       }
/* 243 */       return false;
/*     */     }
/*     */ 
/*     */     public PicturePagerActivity.ImageInfo getItem(int index) {
/* 247 */       return (PicturePagerActivity.ImageInfo)this.mImageList.get(index);
/*     */     }
/*     */ 
/*     */     public int getItemPosition(Object object)
/*     */     {
/* 252 */       return -2;
/*     */     }
/*     */ 
/*     */     public int getCount()
/*     */     {
/* 257 */       return this.mImageList.size();
/*     */     }
/*     */ 
/*     */     public boolean isViewFromObject(View view, Object object)
/*     */     {
/* 262 */       return view == object;
/*     */     }
/*     */ 
/*     */     public Object instantiateItem(ViewGroup container, int position)
/*     */     {
/* 267 */       RLog.i("PicturePagerActivity", "instantiateItem.position:" + position);
/*     */ 
/* 269 */       View imageView = newView(container.getContext(), (PicturePagerActivity.ImageInfo)this.mImageList.get(position));
/* 270 */       updatePhotoView(position, imageView);
/* 271 */       imageView.setId(position);
/* 272 */       container.addView(imageView);
/* 273 */       return imageView;
/*     */     }
/*     */ 
/*     */     public void destroyItem(ViewGroup container, int position, Object object)
/*     */     {
/* 278 */       RLog.i("PicturePagerActivity", "destroyItem.position:" + position);
/* 279 */       ViewHolder holder = (ViewHolder)container.findViewById(position).getTag();
/* 280 */       holder.photoView.setImageURI(null);
/* 281 */       container.removeView((View)object);
/*     */     }
/*     */ 
/*     */     private void updatePhotoView(int position, View view)
/*     */     {
/* 286 */       ViewHolder holder = (ViewHolder)view.getTag();
/* 287 */       Uri originalUri = ((PicturePagerActivity.ImageInfo)this.mImageList.get(position)).getLargeImageUri();
/* 288 */       Uri thumbUri = ((PicturePagerActivity.ImageInfo)this.mImageList.get(position)).getThumbUri();
/*     */ 
/* 290 */       if ((originalUri == null) || (thumbUri == null)) {
/* 291 */         RLog.e("PicturePagerActivity", "large uri and thumbnail uri of the image should not be null.");
/* 292 */         return;
/*     */       }
/*     */       File file;
/*     */       File file;
/* 295 */       if ((originalUri.getScheme().startsWith("http")) || (originalUri.getScheme().startsWith("https")))
/*     */       {
/* 297 */         file = ImageLoader.getInstance().getDiskCache().get(originalUri.toString());
/*     */       }
/* 299 */       else file = new File(originalUri.getPath());
/*     */ 
/* 301 */       if ((file != null) && (file.exists())) {
/* 302 */         AlbumBitmapCacheHelper.getInstance().addPathToShowlist(file.getAbsolutePath());
/* 303 */         Bitmap bitmap = AlbumBitmapCacheHelper.getInstance().getBitmap(file.getAbsolutePath(), 0, 0, new AlbumBitmapCacheHelper.ILoadImageCallback(holder)
/*     */         {
/*     */           public void onLoadImageCallBack(Bitmap bitmap, String p, Object[] objects) {
/* 306 */             if (bitmap == null) {
/* 307 */               return;
/*     */             }
/* 309 */             this.val$holder.photoView.setImageBitmap(bitmap);
/*     */           }
/*     */         }
/*     */         , new Object[] { Integer.valueOf(position) });
/*     */ 
/* 312 */         if (bitmap != null) {
/* 313 */           holder.photoView.setImageBitmap(bitmap);
/*     */         } else {
/* 315 */           Drawable drawable = Drawable.createFromPath(thumbUri.getPath());
/* 316 */           holder.photoView.setImageDrawable(drawable);
/*     */         }
/* 318 */       } else if (position != PicturePagerActivity.this.mCurrentIndex) {
/* 319 */         Drawable drawable = Drawable.createFromPath(thumbUri.getPath());
/* 320 */         holder.photoView.setImageDrawable(drawable);
/*     */       } else {
/* 322 */         ImageAware imageAware = new ImageViewAware(holder.photoView);
/* 323 */         if (PicturePagerActivity.this.mDownloadingImageAware != null) {
/* 324 */           ImageLoader.getInstance().cancelDisplayTask(PicturePagerActivity.this.mDownloadingImageAware);
/*     */         }
/* 326 */         ImageLoader.getInstance().displayImage(originalUri.toString(), imageAware, createDisplayImageOptions(thumbUri), new ImageLoadingListener(holder)
/*     */         {
/*     */           public void onLoadingStarted(String imageUri, View view) {
/* 329 */             this.val$holder.progressText.setVisibility(0);
/* 330 */             this.val$holder.progressBar.setVisibility(0);
/* 331 */             this.val$holder.progressText.setText("0%");
/*     */           }
/*     */ 
/*     */           public void onLoadingFailed(String imageUri, View view, FailReason failReason)
/*     */           {
/* 336 */             this.val$holder.progressText.setVisibility(8);
/* 337 */             this.val$holder.progressBar.setVisibility(8);
/*     */           }
/*     */ 
/*     */           public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
/*     */           {
/* 342 */             this.val$holder.progressText.setVisibility(8);
/* 343 */             this.val$holder.progressBar.setVisibility(8);
/*     */           }
/*     */ 
/*     */           public void onLoadingCancelled(String imageUri, View view)
/*     */           {
/* 348 */             this.val$holder.progressText.setVisibility(8);
/* 349 */             this.val$holder.progressText.setVisibility(8);
/*     */           }
/*     */         }
/*     */         , new ImageLoadingProgressListener(holder)
/*     */         {
/*     */           public void onProgressUpdate(String imageUri, View view, int current, int total)
/*     */           {
/* 355 */             this.val$holder.progressText.setText(current * 100 / total + "%");
/* 356 */             if (current == total) {
/* 357 */               this.val$holder.progressText.setVisibility(8);
/* 358 */               this.val$holder.progressBar.setVisibility(8);
/*     */             } else {
/* 360 */               this.val$holder.progressText.setVisibility(0);
/* 361 */               this.val$holder.progressBar.setVisibility(0);
/*     */             }
/*     */           }
/*     */         });
/* 365 */         PicturePagerActivity.access$1002(PicturePagerActivity.this, imageAware);
/*     */       }
/*     */     }
/*     */ 
/*     */     private DisplayImageOptions createDisplayImageOptions(Uri uri) {
/* 370 */       DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
/* 371 */       Drawable drawable = Drawable.createFromPath(uri.getPath());
/* 372 */       return builder.resetViewBeforeLoading(false).cacheInMemory(false).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).showImageForEmptyUri(drawable).showImageOnFail(drawable).showImageOnLoading(drawable).handler(new Handler()).build();
/*     */     }
/*     */ 
/*     */     public class ViewHolder
/*     */     {
/*     */       ProgressBar progressBar;
/*     */       TextView progressText;
/*     */       PhotoView photoView;
/*     */ 
/*     */       public ViewHolder()
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.activity.PicturePagerActivity
 * JD-Core Version:    0.6.0
 */
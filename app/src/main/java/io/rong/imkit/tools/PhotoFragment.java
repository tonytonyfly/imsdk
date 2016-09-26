/*     */ package io.rong.imkit.tools;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.Bitmap.Config;
/*     */ import android.graphics.drawable.Drawable;
/*     */ import android.net.Uri;
/*     */ import android.os.Bundle;
/*     */ import android.os.Handler;
/*     */ import android.support.v4.app.FragmentActivity;
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
/*     */ import io.rong.imkit.activity.AlbumBitmapCacheHelper;
/*     */ import io.rong.imkit.activity.AlbumBitmapCacheHelper.ILoadImageCallback;
/*     */ import io.rong.imkit.fragment.BaseFragment;
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
/*     */ public class PhotoFragment extends BaseFragment
/*     */ {
/*     */   private static final String TAG = "PhotoFragment";
/*     */   private static final int IMAGE_MESSAGE_COUNT = 10;
/*     */   private HackyViewPager mViewPager;
/*     */   private ImageMessage mCurrentImageMessage;
/*     */   private Conversation.ConversationType mConversationType;
/*     */   private int mCurrentMessageId;
/*     */   private String mTargetId;
/*     */   private int mCurrentIndex;
/*     */   private PhotoDownloadListener mDownloadListener;
/*     */   private ImageAware mDownloadingImageAware;
/*     */   private ImageAdapter mImageAdapter;
/*     */   private boolean isFirstTime;
/*     */   private ViewPager.OnPageChangeListener mPageChangeListener;
/*     */ 
/*     */   public PhotoFragment()
/*     */   {
/*  53 */     this.mTargetId = null;
/*  54 */     this.mCurrentIndex = 0;
/*     */ 
/*  58 */     this.isFirstTime = false;
/*  59 */     this.mPageChangeListener = new ViewPager.OnPageChangeListener()
/*     */     {
/*     */       public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onPageSelected(int position) {
/*  66 */         RLog.i("PhotoFragment", "onPageSelected. position:" + position);
/*  67 */         PhotoFragment.access$002(PhotoFragment.this, position);
/*  68 */         View view = PhotoFragment.this.mViewPager.findViewById(position);
/*  69 */         if (view != null)
/*  70 */           PhotoFragment.this.mImageAdapter.updatePhotoView(position, view, PhotoFragment.this.mDownloadListener);
/*  71 */         if (position == PhotoFragment.this.mImageAdapter.getCount() - 1)
/*  72 */           PhotoFragment.this.getConversationImageUris(PhotoFragment.this.mImageAdapter.getItem(position).getMessageId(), RongCommonDefine.GetMessageDirection.BEHIND);
/*  73 */         else if (position == 0)
/*  74 */           PhotoFragment.this.getConversationImageUris(PhotoFragment.this.mImageAdapter.getItem(position).getMessageId(), RongCommonDefine.GetMessageDirection.FRONT);
/*     */       }
/*     */ 
/*     */       public void onPageScrollStateChanged(int state)
/*     */       {
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public void onCreate(Bundle savedInstanceState)
/*     */   {
/*  87 */     super.onCreate(savedInstanceState);
/*  88 */     AlbumBitmapCacheHelper.init(getActivity());
/*     */   }
/*     */ 
/*     */   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
/*     */   {
/*  93 */     View view = inflater.inflate(R.layout.rc_fr_photo, container, true);
/*  94 */     this.mViewPager = ((HackyViewPager)view.findViewById(R.id.viewpager));
/*  95 */     return view;
/*     */   }
/*     */ 
/*     */   public void initPhoto(Message currentMessage, PhotoDownloadListener downloadListener) {
/*  99 */     if (currentMessage == null)
/* 100 */       return;
/* 101 */     this.mCurrentImageMessage = ((ImageMessage)currentMessage.getContent());
/* 102 */     this.mConversationType = currentMessage.getConversationType();
/* 103 */     this.mCurrentMessageId = currentMessage.getMessageId();
/* 104 */     this.mTargetId = currentMessage.getTargetId();
/* 105 */     this.mDownloadListener = downloadListener;
/* 106 */     if (this.mCurrentMessageId < 0) {
/* 107 */       RLog.e("PhotoFragment", "The value of messageId is wrong!");
/* 108 */       return;
/*     */     }
/*     */ 
/* 111 */     this.mImageAdapter = new ImageAdapter(null);
/* 112 */     this.isFirstTime = true;
/* 113 */     this.mViewPager.setOnPageChangeListener(this.mPageChangeListener);
/*     */ 
/* 115 */     getConversationImageUris(this.mCurrentMessageId, RongCommonDefine.GetMessageDirection.FRONT);
/* 116 */     getConversationImageUris(this.mCurrentMessageId, RongCommonDefine.GetMessageDirection.BEHIND);
/*     */   }
/*     */ 
/*     */   private void getConversationImageUris(int mesageId, RongCommonDefine.GetMessageDirection direction) {
/* 120 */     if ((this.mConversationType != null) && (!TextUtils.isEmpty(this.mTargetId)))
/* 121 */       RongIMClient.getInstance().getHistoryMessages(this.mConversationType, this.mTargetId, "RC:ImgMsg", mesageId, 10, direction, new RongIMClient.ResultCallback(direction)
/*     */       {
/*     */         public void onSuccess(List<Message> messages)
/*     */         {
/* 125 */           ArrayList lists = new ArrayList();
/* 126 */           if (messages != null) {
/* 127 */             if (this.val$direction.equals(RongCommonDefine.GetMessageDirection.FRONT))
/* 128 */               Collections.reverse(messages);
/* 129 */             for (int i = 0; i < messages.size(); i++) {
/* 130 */               Message message = (Message)messages.get(i);
/* 131 */               if ((message.getContent() instanceof ImageMessage)) {
/* 132 */                 ImageMessage imageMessage = (ImageMessage)message.getContent();
/* 133 */                 Uri largeImageUri = imageMessage.getLocalUri() == null ? imageMessage.getRemoteUri() : imageMessage.getLocalUri();
/*     */ 
/* 135 */                 if ((imageMessage.getThumUri() != null) && (largeImageUri != null)) {
/* 136 */                   lists.add(new PhotoFragment.ImageInfo(PhotoFragment.this, message.getMessageId(), imageMessage.getThumUri(), largeImageUri));
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/* 141 */           if ((this.val$direction.equals(RongCommonDefine.GetMessageDirection.FRONT)) && (PhotoFragment.this.isFirstTime)) {
/* 142 */             lists.add(new PhotoFragment.ImageInfo(PhotoFragment.this, PhotoFragment.this.mCurrentMessageId, PhotoFragment.this.mCurrentImageMessage.getThumUri(), PhotoFragment.this.mCurrentImageMessage.getLocalUri() == null ? PhotoFragment.this.mCurrentImageMessage.getRemoteUri() : PhotoFragment.this.mCurrentImageMessage.getLocalUri()));
/*     */ 
/* 144 */             PhotoFragment.this.mImageAdapter.addData(lists, this.val$direction.equals(RongCommonDefine.GetMessageDirection.FRONT));
/* 145 */             PhotoFragment.this.mViewPager.setAdapter(PhotoFragment.this.mImageAdapter);
/* 146 */             PhotoFragment.access$702(PhotoFragment.this, false);
/* 147 */             PhotoFragment.this.mViewPager.setCurrentItem(lists.size() - 1);
/* 148 */             PhotoFragment.access$002(PhotoFragment.this, lists.size() - 1);
/* 149 */           } else if (lists.size() > 0) {
/* 150 */             PhotoFragment.this.mImageAdapter.addData(lists, this.val$direction.equals(RongCommonDefine.GetMessageDirection.FRONT));
/* 151 */             PhotoFragment.this.mImageAdapter.notifyDataSetChanged();
/* 152 */             if (this.val$direction.equals(RongCommonDefine.GetMessageDirection.FRONT)) {
/* 153 */               PhotoFragment.this.mViewPager.setCurrentItem(lists.size());
/* 154 */               PhotoFragment.access$002(PhotoFragment.this, lists.size());
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
/*     */   public void onViewCreated(View view, Bundle savedInstanceState)
/*     */   {
/* 169 */     super.onViewCreated(view, savedInstanceState);
/*     */   }
/*     */ 
/*     */   public boolean onBackPressed()
/*     */   {
/* 174 */     return false;
/*     */   }
/*     */ 
/*     */   public void onRestoreUI()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onDestroy()
/*     */   {
/* 190 */     super.onDestroy();
/* 191 */     AlbumBitmapCacheHelper.getInstance().uninit();
/*     */   }
/*     */ 
/*     */   public void onSaveInstanceState(Bundle outState)
/*     */   {
/* 196 */     super.onSaveInstanceState(outState);
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
/* 433 */       this.messageId = messageId;
/* 434 */       this.thumbUri = thumbnail;
/* 435 */       this.largeImageUri = largeImageUri;
/*     */     }
/*     */ 
/*     */     public int getMessageId() {
/* 439 */       return this.messageId;
/*     */     }
/*     */ 
/*     */     public Uri getLargeImageUri() {
/* 443 */       return this.largeImageUri;
/*     */     }
/*     */ 
/*     */     public Uri getThumbUri() {
/* 447 */       return this.thumbUri;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ImageAdapter extends PagerAdapter
/*     */   {
/* 200 */     private ArrayList<PhotoFragment.ImageInfo> mImageList = new ArrayList();
/*     */     private PicturePopupWindow menuWindow;
/* 209 */     private View.OnClickListener onMenuWindowClick = new View.OnClickListener()
/*     */     {
/*     */       public void onClick(View v) {
/* 212 */         if (v.getId() == R.id.rc_content);
/* 215 */         PhotoFragment.ImageAdapter.this.menuWindow.dismiss();
/*     */       }
/* 209 */     };
/*     */ 
/*     */     private ImageAdapter()
/*     */     {
/*     */     }
/*     */ 
/*     */     private View newView(Context context, PhotoFragment.ImageInfo imageInfo)
/*     */     {
/* 220 */       View result = LayoutInflater.from(context).inflate(R.layout.rc_fr_image, null);
/*     */ 
/* 222 */       ViewHolder holder = new ViewHolder();
/* 223 */       holder.progressBar = ((ProgressBar)result.findViewById(R.id.rc_progress));
/* 224 */       holder.progressText = ((TextView)result.findViewById(R.id.rc_txt));
/* 225 */       holder.photoView = ((PhotoView)result.findViewById(R.id.rc_photoView));
/* 226 */       holder.photoView.setOnLongClickListener(new View.OnLongClickListener(imageInfo)
/*     */       {
/*     */         public boolean onLongClick(View v) {
/* 229 */           Uri uri = this.val$imageInfo.getLargeImageUri();
/* 230 */           File file = null;
/* 231 */           if (uri != null) {
/* 232 */             if ((uri.getScheme().startsWith("http")) || (uri.getScheme().startsWith("https")))
/* 233 */               file = ImageLoader.getInstance().getDiskCache().get(uri.toString());
/*     */             else
/* 235 */               file = new File(uri.getPath());
/*     */           }
/* 237 */           PhotoFragment.ImageAdapter.access$1002(PhotoFragment.ImageAdapter.this, new PicturePopupWindow(PhotoFragment.this.getContext(), file));
/* 238 */           PhotoFragment.ImageAdapter.this.menuWindow.showAtLocation(v, 81, 0, 0);
/* 239 */           PhotoFragment.ImageAdapter.this.menuWindow.setOutsideTouchable(false);
/* 240 */           return false;
/*     */         }
/*     */       });
/* 243 */       holder.photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener()
/*     */       {
/*     */         public void onPhotoTap(View view, float x, float y) {
/* 246 */           PhotoFragment.this.getActivity().finish();
/*     */         }
/*     */ 
/*     */         public void onOutsidePhotoTap()
/*     */         {
/*     */         }
/*     */       });
/* 254 */       result.setTag(holder);
/*     */ 
/* 256 */       return result;
/*     */     }
/*     */ 
/*     */     public void addData(ArrayList<PhotoFragment.ImageInfo> newImages, boolean direction) {
/* 260 */       if ((newImages == null) || (newImages.size() == 0))
/* 261 */         return;
/* 262 */       if (this.mImageList.size() == 0) {
/* 263 */         this.mImageList.addAll(newImages);
/* 264 */       } else if ((direction) && (!PhotoFragment.this.isFirstTime) && (!isDuplicate(((PhotoFragment.ImageInfo)newImages.get(0)).getMessageId()))) {
/* 265 */         ArrayList temp = new ArrayList();
/* 266 */         temp.addAll(this.mImageList);
/* 267 */         this.mImageList.clear();
/* 268 */         this.mImageList.addAll(newImages);
/* 269 */         this.mImageList.addAll(this.mImageList.size(), temp);
/* 270 */       } else if ((!PhotoFragment.this.isFirstTime) && (!isDuplicate(((PhotoFragment.ImageInfo)newImages.get(0)).getMessageId()))) {
/* 271 */         this.mImageList.addAll(this.mImageList.size(), newImages);
/*     */       }
/*     */     }
/*     */ 
/*     */     private boolean isDuplicate(int messageId) {
/* 276 */       for (PhotoFragment.ImageInfo info : this.mImageList) {
/* 277 */         if (info.getMessageId() == messageId)
/* 278 */           return true;
/*     */       }
/* 280 */       return false;
/*     */     }
/*     */ 
/*     */     public PhotoFragment.ImageInfo getItem(int index) {
/* 284 */       return (PhotoFragment.ImageInfo)this.mImageList.get(index);
/*     */     }
/*     */ 
/*     */     public int getItemPosition(Object object)
/*     */     {
/* 289 */       return -2;
/*     */     }
/*     */ 
/*     */     public int getCount()
/*     */     {
/* 294 */       return this.mImageList.size();
/*     */     }
/*     */ 
/*     */     public boolean isViewFromObject(View view, Object object)
/*     */     {
/* 299 */       return view == object;
/*     */     }
/*     */ 
/*     */     public Object instantiateItem(ViewGroup container, int position)
/*     */     {
/* 304 */       RLog.i("PhotoFragment", "instantiateItem.position:" + position);
/*     */ 
/* 306 */       View imageView = newView(container.getContext(), (PhotoFragment.ImageInfo)this.mImageList.get(position));
/* 307 */       updatePhotoView(position, imageView, PhotoFragment.this.mDownloadListener);
/* 308 */       imageView.setId(position);
/* 309 */       container.addView(imageView);
/* 310 */       return imageView;
/*     */     }
/*     */ 
/*     */     public void destroyItem(ViewGroup container, int position, Object object)
/*     */     {
/* 315 */       RLog.i("PhotoFragment", "destroyItem.position:" + position);
/* 316 */       ViewHolder holder = (ViewHolder)container.findViewById(position).getTag();
/* 317 */       holder.photoView.setImageURI(null);
/* 318 */       container.removeView((View)object);
/*     */     }
/*     */ 
/*     */     private void updatePhotoView(int position, View view, PhotoFragment.PhotoDownloadListener downloadListener)
/*     */     {
/* 323 */       ViewHolder holder = (ViewHolder)view.getTag();
/* 324 */       Uri originalUri = ((PhotoFragment.ImageInfo)this.mImageList.get(position)).getLargeImageUri();
/* 325 */       Uri thumbUri = ((PhotoFragment.ImageInfo)this.mImageList.get(position)).getThumbUri();
/*     */ 
/* 327 */       if ((originalUri == null) || (thumbUri == null)) {
/* 328 */         RLog.e("PhotoFragment", "large uri and thumbnail uri of the image should not be null.");
/* 329 */         return;
/*     */       }
/*     */       File file;
/*     */       File file;
/* 332 */       if ((originalUri.getScheme().startsWith("http")) || (originalUri.getScheme().startsWith("https")))
/*     */       {
/* 334 */         file = ImageLoader.getInstance().getDiskCache().get(originalUri.toString());
/*     */       }
/* 336 */       else file = new File(originalUri.getPath());
/*     */ 
/* 338 */       if ((file != null) && (file.exists())) {
/* 339 */         if (PhotoFragment.this.mDownloadListener != null)
/* 340 */           PhotoFragment.this.mDownloadListener.onDownloaded(originalUri);
/* 341 */         AlbumBitmapCacheHelper.getInstance().addPathToShowlist(file.getAbsolutePath());
/* 342 */         Bitmap bitmap = AlbumBitmapCacheHelper.getInstance().getBitmap(file.getAbsolutePath(), 0, 0, new AlbumBitmapCacheHelper.ILoadImageCallback(holder)
/*     */         {
/*     */           public void onLoadImageCallBack(Bitmap bitmap, String p, Object[] objects) {
/* 345 */             if (bitmap == null) {
/* 346 */               return;
/*     */             }
/* 348 */             this.val$holder.photoView.setImageBitmap(bitmap);
/*     */           }
/*     */         }
/*     */         , new Object[] { Integer.valueOf(position) });
/*     */ 
/* 351 */         if (bitmap != null) {
/* 352 */           holder.photoView.setImageBitmap(bitmap);
/*     */         } else {
/* 354 */           Drawable drawable = Drawable.createFromPath(thumbUri.getPath());
/* 355 */           holder.photoView.setImageDrawable(drawable);
/*     */         }
/* 357 */       } else if (position != PhotoFragment.this.mCurrentIndex) {
/* 358 */         Drawable drawable = Drawable.createFromPath(thumbUri.getPath());
/* 359 */         holder.photoView.setImageDrawable(drawable);
/*     */       } else {
/* 361 */         ImageAware imageAware = new ImageViewAware(holder.photoView);
/* 362 */         if (PhotoFragment.this.mDownloadingImageAware != null) {
/* 363 */           ImageLoader.getInstance().cancelDisplayTask(PhotoFragment.this.mDownloadingImageAware);
/*     */         }
/* 365 */         ImageLoader.getInstance().displayImage(originalUri.toString(), imageAware, createDisplayImageOptions(thumbUri), new ImageLoadingListener(holder, downloadListener)
/*     */         {
/*     */           public void onLoadingStarted(String imageUri, View view) {
/* 368 */             this.val$holder.progressText.setVisibility(0);
/* 369 */             this.val$holder.progressBar.setVisibility(0);
/* 370 */             this.val$holder.progressText.setText("0%");
/*     */           }
/*     */ 
/*     */           public void onLoadingFailed(String imageUri, View view, FailReason failReason)
/*     */           {
/* 375 */             if (this.val$downloadListener != null)
/* 376 */               this.val$downloadListener.onDownloadError();
/* 377 */             this.val$holder.progressText.setVisibility(8);
/* 378 */             this.val$holder.progressBar.setVisibility(8);
/*     */           }
/*     */ 
/*     */           public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
/*     */           {
/* 383 */             if (this.val$downloadListener != null)
/* 384 */               this.val$downloadListener.onDownloaded(Uri.parse(imageUri));
/* 385 */             this.val$holder.progressText.setVisibility(8);
/* 386 */             this.val$holder.progressBar.setVisibility(8);
/*     */           }
/*     */ 
/*     */           public void onLoadingCancelled(String imageUri, View view)
/*     */           {
/* 391 */             this.val$holder.progressText.setVisibility(8);
/* 392 */             this.val$holder.progressText.setVisibility(8);
/*     */           }
/*     */         }
/*     */         , new ImageLoadingProgressListener(holder)
/*     */         {
/*     */           public void onProgressUpdate(String imageUri, View view, int current, int total)
/*     */           {
/* 398 */             this.val$holder.progressText.setText(current * 100 / total + "%");
/* 399 */             if (current == total) {
/* 400 */               this.val$holder.progressText.setVisibility(8);
/* 401 */               this.val$holder.progressBar.setVisibility(8);
/*     */             } else {
/* 403 */               this.val$holder.progressText.setVisibility(0);
/* 404 */               this.val$holder.progressBar.setVisibility(0);
/*     */             }
/*     */           }
/*     */         });
/* 408 */         PhotoFragment.access$1102(PhotoFragment.this, imageAware);
/*     */       }
/*     */     }
/*     */ 
/*     */     private DisplayImageOptions createDisplayImageOptions(Uri uri) {
/* 413 */       DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
/* 414 */       Drawable drawable = Drawable.createFromPath(uri.getPath());
/* 415 */       return builder.resetViewBeforeLoading(false).cacheInMemory(false).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).showImageForEmptyUri(drawable).showImageOnFail(drawable).showImageOnLoading(drawable).handler(new Handler()).build();
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
/*     */ 
/*     */   public static abstract interface PhotoDownloadListener
/*     */   {
/*     */     public abstract void onDownloaded(Uri paramUri);
/*     */ 
/*     */     public abstract void onDownloadError();
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.tools.PhotoFragment
 * JD-Core Version:    0.6.0
 */
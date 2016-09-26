/*     */ package io.rong.imkit.activity;
/*     */ 
/*     */ import android.annotation.TargetApi;
/*     */ import android.app.Activity;
/*     */ import android.content.Context;
/*     */ import android.content.Intent;
/*     */ import android.content.res.Resources;
/*     */ import android.graphics.Bitmap;
/*     */ import android.net.Uri;
/*     */ import android.os.Build.VERSION;
/*     */ import android.os.Bundle;
/*     */ import android.support.annotation.DrawableRes;
/*     */ import android.support.annotation.Nullable;
/*     */ import android.support.v4.view.PagerAdapter;
/*     */ import android.support.v4.view.ViewPager.OnPageChangeListener;
/*     */ import android.view.KeyEvent;
/*     */ import android.view.View;
/*     */ import android.view.View.OnClickListener;
/*     */ import android.view.ViewGroup;
/*     */ import android.view.Window;
/*     */ import android.widget.Button;
/*     */ import android.widget.ImageButton;
/*     */ import android.widget.ImageView;
/*     */ import android.widget.RelativeLayout.LayoutParams;
/*     */ import android.widget.TextView;
/*     */ import android.widget.Toast;
/*     */ import io.rong.imkit.R.drawable;
/*     */ import io.rong.imkit.R.id;
/*     */ import io.rong.imkit.R.layout;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.widget.HackyViewPager;
/*     */ import io.rong.photoview.PhotoView;
/*     */ import io.rong.photoview.PhotoViewAttacher.OnViewTapListener;
/*     */ import java.io.File;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class PicturePreviewActivity extends Activity
/*     */ {
/*     */   public static final int RESULT_SEND = 1;
/*     */   private TextView mIndexTotal;
/*     */   private View mWholeView;
/*     */   private View mToolbarTop;
/*     */   private View mToolbarBottom;
/*     */   private ImageButton mBtnBack;
/*     */   private Button mBtnSend;
/*     */   private CheckButton mUseOrigin;
/*     */   private CheckButton mSelectBox;
/*     */   private HackyViewPager mViewPager;
/*     */   private ArrayList<PictureSelectorActivity.PicItem> mItemList;
/*     */   private int mCurrentIndex;
/*     */   private boolean mFullScreen;
/*     */ 
/*     */   protected void onCreate(Bundle savedInstanceState)
/*     */   {
/*  58 */     super.onCreate(savedInstanceState);
/*  59 */     requestWindowFeature(1);
/*  60 */     setContentView(R.layout.rc_picprev_activity);
/*  61 */     initView();
/*     */ 
/*  63 */     this.mUseOrigin.setChecked(getIntent().getBooleanExtra("sendOrigin", false));
/*  64 */     this.mCurrentIndex = getIntent().getIntExtra("index", 0);
/*  65 */     this.mItemList = PictureSelectorActivity.PicItemHolder.itemList;
/*  66 */     this.mIndexTotal.setText(String.format("%d/%d", new Object[] { Integer.valueOf(this.mCurrentIndex + 1), Integer.valueOf(this.mItemList.size()) }));
/*     */ 
/*  68 */     if (Build.VERSION.SDK_INT >= 11) {
/*  69 */       this.mWholeView.setSystemUiVisibility(1024);
/*  70 */       int margin = getSmartBarHeight(this);
/*  71 */       if (margin > 0) {
/*  72 */         RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)this.mToolbarBottom.getLayoutParams();
/*  73 */         lp.setMargins(0, 0, 0, margin);
/*  74 */         this.mToolbarBottom.setLayoutParams(lp);
/*     */       }
/*     */     }
/*     */ 
/*  78 */     int result = 0;
/*  79 */     int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
/*  80 */     if (resourceId > 0) {
/*  81 */       result = getResources().getDimensionPixelSize(resourceId);
/*     */     }
/*     */ 
/*  84 */     RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(this.mToolbarTop.getLayoutParams());
/*  85 */     lp.setMargins(0, result, 0, 0);
/*  86 */     this.mToolbarTop.setLayoutParams(lp);
/*     */ 
/*  88 */     this.mBtnBack.setOnClickListener(new View.OnClickListener()
/*     */     {
/*     */       public void onClick(View v) {
/*  91 */         Intent intent = new Intent();
/*  92 */         intent.putExtra("sendOrigin", PicturePreviewActivity.this.mUseOrigin.getChecked());
/*  93 */         PicturePreviewActivity.this.setResult(-1, intent);
/*  94 */         PicturePreviewActivity.this.finish();
/*     */       }
/*     */     });
/*  97 */     this.mBtnSend.setOnClickListener(new View.OnClickListener()
/*     */     {
/*     */       public void onClick(View v) {
/* 100 */         Intent data = new Intent();
/* 101 */         ArrayList list = new ArrayList();
/* 102 */         for (PictureSelectorActivity.PicItem item : PicturePreviewActivity.this.mItemList) {
/* 103 */           if (item.selected) {
/* 104 */             list.add(Uri.parse("file://" + item.uri));
/*     */           }
/*     */         }
/*     */ 
/* 108 */         if (list.size() == 0) {
/* 109 */           PicturePreviewActivity.this.mSelectBox.setChecked(true);
/* 110 */           list.add(Uri.parse("file://" + ((PictureSelectorActivity.PicItem)PicturePreviewActivity.this.mItemList.get(PicturePreviewActivity.this.mCurrentIndex)).uri));
/*     */         }
/* 112 */         data.putExtra("sendOrigin", PicturePreviewActivity.this.mUseOrigin.getChecked());
/* 113 */         data.putExtra("android.intent.extra.RETURN_RESULT", list);
/* 114 */         PicturePreviewActivity.this.setResult(1, data);
/* 115 */         PicturePreviewActivity.this.finish();
/*     */       }
/*     */     });
/* 119 */     this.mUseOrigin.setText(R.string.rc_picprev_origin);
/* 120 */     this.mUseOrigin.setOnClickListener(new View.OnClickListener()
/*     */     {
/*     */       public void onClick(View v) {
/* 123 */         PicturePreviewActivity.this.mUseOrigin.setChecked(!PicturePreviewActivity.this.mUseOrigin.getChecked());
/* 124 */         if ((PicturePreviewActivity.this.mUseOrigin.getChecked()) && (PicturePreviewActivity.this.getTotalSelectedNum() == 0)) {
/* 125 */           PicturePreviewActivity.this.mSelectBox.setChecked(!PicturePreviewActivity.this.mSelectBox.getChecked());
/* 126 */           ((PictureSelectorActivity.PicItem)PicturePreviewActivity.this.mItemList.get(PicturePreviewActivity.this.mCurrentIndex)).selected = PicturePreviewActivity.this.mSelectBox.getChecked();
/* 127 */           PicturePreviewActivity.this.updateToolbar();
/*     */         }
/*     */       }
/*     */     });
/* 131 */     this.mSelectBox.setText(R.string.rc_picprev_select);
/* 132 */     this.mSelectBox.setChecked(((PictureSelectorActivity.PicItem)this.mItemList.get(this.mCurrentIndex)).selected);
/* 133 */     this.mSelectBox.setOnClickListener(new View.OnClickListener()
/*     */     {
/*     */       public void onClick(View v) {
/* 136 */         if ((!PicturePreviewActivity.this.mSelectBox.getChecked()) && (PicturePreviewActivity.this.getTotalSelectedNum() == 9)) {
/* 137 */           Toast.makeText(PicturePreviewActivity.this, R.string.rc_picsel_selected_max, 0).show();
/* 138 */           return;
/*     */         }
/*     */ 
/* 141 */         PicturePreviewActivity.this.mSelectBox.setChecked(!PicturePreviewActivity.this.mSelectBox.getChecked());
/* 142 */         ((PictureSelectorActivity.PicItem)PicturePreviewActivity.this.mItemList.get(PicturePreviewActivity.this.mCurrentIndex)).selected = PicturePreviewActivity.this.mSelectBox.getChecked();
/* 143 */         PicturePreviewActivity.this.updateToolbar();
/*     */       }
/*     */     });
/* 147 */     this.mViewPager.setAdapter(new PreviewAdapter(null));
/* 148 */     this.mViewPager.setCurrentItem(this.mCurrentIndex);
/* 149 */     this.mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
/*     */     {
/*     */       public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void onPageSelected(int position) {
/* 156 */         PicturePreviewActivity.access$302(PicturePreviewActivity.this, position);
/* 157 */         PicturePreviewActivity.this.mIndexTotal.setText(String.format("%d/%d", new Object[] { Integer.valueOf(position + 1), Integer.valueOf(PicturePreviewActivity.access$100(PicturePreviewActivity.this).size()) }));
/* 158 */         PicturePreviewActivity.this.mSelectBox.setChecked(((PictureSelectorActivity.PicItem)PicturePreviewActivity.this.mItemList.get(position)).selected);
/*     */       }
/*     */ 
/*     */       public void onPageScrollStateChanged(int state)
/*     */       {
/*     */       }
/*     */     });
/* 166 */     updateToolbar();
/*     */   }
/*     */ 
/*     */   private void initView() {
/* 170 */     this.mToolbarTop = findViewById(R.id.toolbar_top);
/* 171 */     this.mIndexTotal = ((TextView)findViewById(R.id.index_total));
/* 172 */     this.mBtnBack = ((ImageButton)findViewById(R.id.back));
/* 173 */     this.mBtnSend = ((Button)findViewById(R.id.send));
/*     */ 
/* 175 */     this.mWholeView = findViewById(R.id.whole_layout);
/* 176 */     this.mViewPager = ((HackyViewPager)findViewById(R.id.viewpager));
/*     */ 
/* 178 */     this.mToolbarBottom = findViewById(R.id.toolbar_bottom);
/* 179 */     this.mUseOrigin = new CheckButton(findViewById(R.id.origin_check), R.drawable.rc_origin_check_nor, R.drawable.rc_origin_check_sel);
/* 180 */     this.mSelectBox = new CheckButton(findViewById(R.id.select_check), R.drawable.select_check_nor, R.drawable.select_check_sel);
/*     */   }
/*     */ 
/*     */   protected void onResume()
/*     */   {
/* 186 */     super.onResume();
/*     */   }
/*     */ 
/*     */   public boolean onKeyDown(int keyCode, KeyEvent event)
/*     */   {
/* 191 */     if (keyCode == 4) {
/* 192 */       Intent intent = new Intent();
/* 193 */       intent.putExtra("sendOrigin", this.mUseOrigin.getChecked());
/* 194 */       setResult(-1, intent);
/*     */     }
/* 196 */     return super.onKeyDown(keyCode, event);
/*     */   }
/*     */ 
/*     */   private int getTotalSelectedNum() {
/* 200 */     int sum = 0;
/* 201 */     for (int i = 0; i < this.mItemList.size(); i++) {
/* 202 */       if (((PictureSelectorActivity.PicItem)this.mItemList.get(i)).selected) {
/* 203 */         sum++;
/*     */       }
/*     */     }
/* 206 */     return sum;
/*     */   }
/*     */ 
/*     */   private String getTotalSelectedSize() {
/* 210 */     float size = 0.0F;
/* 211 */     for (int i = 0; i < this.mItemList.size(); i++)
/* 212 */       if (((PictureSelectorActivity.PicItem)this.mItemList.get(i)).selected) {
/* 213 */         File file = new File(((PictureSelectorActivity.PicItem)this.mItemList.get(i)).uri);
/* 214 */         size += (float)(file.length() / 1024L);
/*     */       }
/*     */     String totalSize;
/*     */     String totalSize;
/* 219 */     if (size < 1024.0F)
/* 220 */       totalSize = String.format("%.0fK", new Object[] { Float.valueOf(size) });
/*     */     else {
/* 222 */       totalSize = String.format("%.1fM", new Object[] { Float.valueOf(size / 1024.0F) });
/*     */     }
/* 224 */     return totalSize;
/*     */   }
/*     */ 
/*     */   private void updateToolbar() {
/* 228 */     int selNum = getTotalSelectedNum();
/* 229 */     if ((this.mItemList.size() == 1) && (selNum == 0)) {
/* 230 */       this.mBtnSend.setText(R.string.rc_picsel_toolbar_send);
/* 231 */       this.mUseOrigin.setText(R.string.rc_picprev_origin);
/* 232 */       return;
/*     */     }
/*     */ 
/* 235 */     if (selNum == 0) {
/* 236 */       this.mBtnSend.setText(R.string.rc_picsel_toolbar_send);
/* 237 */       this.mUseOrigin.setText(R.string.rc_picprev_origin);
/* 238 */     } else if (selNum <= 9) {
/* 239 */       this.mBtnSend.setText(String.format(getResources().getString(R.string.rc_picsel_toolbar_send_num), new Object[] { Integer.valueOf(selNum) }));
/* 240 */       this.mUseOrigin.setText(String.format(getResources().getString(R.string.rc_picprev_origin_size), new Object[] { getTotalSelectedSize() }));
/*     */     }
/*     */   }
/*     */ 
/*     */   @TargetApi(11)
/*     */   public static int getSmartBarHeight(Context context) {
/*     */     try {
/* 247 */       Class c = Class.forName("com.android.internal.R$dimen");
/* 248 */       Object obj = c.newInstance();
/* 249 */       Field field = c.getField("mz_action_button_min_height");
/* 250 */       int height = Integer.parseInt(field.get(obj).toString());
/* 251 */       return context.getResources().getDimensionPixelSize(height);
/*     */     } catch (Exception e) {
/* 253 */       e.printStackTrace();
/*     */     }
/* 255 */     return 0;
/*     */   }
/*     */ 
/*     */   private class CheckButton
/*     */   {
/*     */     private View rootView;
/*     */     private ImageView image;
/*     */     private TextView text;
/* 337 */     private boolean checked = false;
/*     */     private int nor_resId;
/*     */     private int sel_resId;
/*     */ 
/*     */     public package io.rong.imkit.activity;
/*     */ 
/*     */     import android.annotation.TargetApi;
/*     */     import android.app.Activity;
/*     */     import android.app.AlertDialog;
/*     */     import android.app.AlertDialog.Builder;
/*     */     import android.content.ContentResolver;
/*     */     import android.content.Context;
/*     */     import android.content.DialogInterface;
/*     */     import android.content.DialogInterface.OnClickListener;
/*     */     import android.content.Intent;
/*     */     import android.content.res.Resources;
/*     */     import android.database.Cursor;
/*     */     import android.graphics.Bitmap;
/*     */     import android.graphics.drawable.BitmapDrawable;
/*     */     import android.media.MediaScannerConnection;
/*     */     import android.media.MediaScannerConnection.OnScanCompletedListener;
/*     */     import android.net.Uri;
/*     */     import android.os.Build.VERSION;
/*     */     import android.os.Bundle;
/*     */     import android.os.Environment;
/*     */     import android.provider.MediaStore.Images.Media;
/*     */     import android.support.v4.util.ArrayMap;
/*     */     import android.util.AttributeSet;
/*     */     import android.view.Display;
/*     */     import android.view.KeyEvent;
/*     */     import android.view.LayoutInflater;
/*     */     import android.view.MotionEvent;
/*     */     import android.view.View;
/*     */     import android.view.View.OnClickListener;
/*     */     import android.view.View.OnTouchListener;
/*     */     import android.view.ViewGroup;
/*     */     import android.view.WindowManager;
/*     */     import android.widget.AdapterView;
/*     */     import android.widget.AdapterView.OnItemClickListener;
/*     */     import android.widget.BaseAdapter;
/*     */     import android.widget.Button;
/*     */     import android.widget.GridView;
/*     */     import android.widget.ImageButton;
/*     */     import android.widget.ImageView;
/*     */     import android.widget.LinearLayout;
/*     */     import android.widget.ListView;
/*     */     import android.widget.TextView;
/*     */     import android.widget.Toast;
/*     */     import io.rong.imkit.R.color;
/*     */     import io.rong.imkit.R.drawable;
/*     */     import io.rong.imkit.R.id;
/*     */     import io.rong.imkit.R.layout;
/*     */     import io.rong.imkit.R.string;
/*     */     import io.rong.imkit.utils.CommonUtils;
/*     */     import java.io.File;
/*     */     import java.util.ArrayList;
/*     */     import java.util.Collection;
/*     */     import java.util.List;
/*     */     import java.util.Map;
/*     */ 
/*     */     public class PictureSelectorActivity extends Activity
/*     */     {
/*     */       public static final int REQUEST_PREVIEW = 0;
/*     */       public static final int REQUEST_CAMERA = 1;
/*     */       public static final int REQUEST_CODE_ASK_PERMISSIONS = 100;
/*     */       private GridView mGridView;
/*     */       private ImageButton mBtnBack;
/*     */       private Button mBtnSend;
/*     */       private PicTypeBtn mPicType;
/*     */       private PreviewBtn mPreviewBtn;
/*     */       private View mCatalogView;
/*     */       private ListView mCatalogListView;
/*     */       private List<PicItem> mAllItemList;
/*     */       private Map<String, List<PicItem>> mItemMap;
/*     */       private List<String> mCatalogList;
/*     */       private String mCurrentCatalog;
/*     */       private Uri mTakePictureUri;
/*     */       private boolean mSendOrigin;
/*     */       private int perWidth;
/*     */ 
/*     */       public PictureSelectorActivity()
/*     */       {
/*  66 */         this.mCurrentCatalog = "";
/*     */ 
/*  68 */         this.mSendOrigin = false;
/*     */       }
/*     */ 
/*     */       @TargetApi(23)
/*     */       protected void onCreate(Bundle savedInstanceState) {
/*  74 */         requestWindowFeature(1);
/*  75 */         super.onCreate(savedInstanceState);
/*  76 */         setContentView(R.layout.rc_picsel_activity);
/*     */ 
/*  78 */         this.mGridView = ((GridView)findViewById(R.id.gridlist));
/*  79 */         this.mBtnBack = ((ImageButton)findViewById(R.id.back));
/*  80 */         this.mBtnBack.setOnClickListener(new View.OnClickListener()
/*     */         {
/*     */           public void onClick(View v) {
/*  83 */             PictureSelectorActivity.this.finish();
/*     */           }
/*     */         });
/*  87 */         this.mBtnSend = ((Button)findViewById(R.id.send));
/*  88 */         this.mPicType = ((PicTypeBtn)findViewById(R.id.pic_type));
/*  89 */         this.mPicType.init(this);
/*  90 */         this.mPicType.setEnabled(false);
/*     */ 
/*  92 */         this.mPreviewBtn = ((PreviewBtn)findViewById(R.id.preview));
/*  93 */         this.mPreviewBtn.init(this);
/*  94 */         this.mPreviewBtn.setEnabled(false);
/*  95 */         this.mCatalogView = findViewById(R.id.catalog_window);
/*  96 */         this.mCatalogListView = ((ListView)findViewById(R.id.catalog_listview));
/*     */ 
/*  98 */         if (Build.VERSION.SDK_INT >= 23) {
/*  99 */           int checkPermission = checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE");
/* 100 */           if (checkPermission != 0) {
/* 101 */             if (shouldShowRequestPermissionRationale("android.permission.READ_EXTERNAL_STORAGE"))
/* 102 */               requestPermissions(new String[] { "android.permission.READ_EXTERNAL_STORAGE" }, 100);
/*     */             else {
/* 104 */               new AlertDialog.Builder(this).setMessage("您需要在设置里打开存储空间权限。").setPositiveButton("确认", new DialogInterface.OnClickListener()
/*     */               {
/*     */                 public void onClick(DialogInterface dialog, int which)
/*     */                 {
/* 109 */                   PictureSelectorActivity.this.requestPermissions(new String[] { "android.permission.READ_EXTERNAL_STORAGE" }, 100);
/*     */                 }
/*     */               }).setNegativeButton("取消", null).create().show();
/*     */             }
/*     */ 
/* 115 */             return;
/*     */           }
/*     */         }
/* 118 */         initView();
/*     */       }
/*     */ 
/*     */       private void initView() {
/* 122 */         updatePictureItems();
/*     */ 
/* 124 */         this.mGridView.setAdapter(new GridViewAdapter());
/* 125 */         this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
/*     */         {
/*     */           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/* 128 */             if (position == 0) {
/* 129 */               return;
/*     */             }
/*     */ 
/* 132 */             PictureSelectorActivity.PicItemHolder.itemList = new ArrayList();
/* 133 */             if (PictureSelectorActivity.this.mCurrentCatalog.isEmpty())
/* 134 */               PictureSelectorActivity.PicItemHolder.itemList.addAll(PictureSelectorActivity.this.mAllItemList);
/*     */             else {
/* 136 */               PictureSelectorActivity.PicItemHolder.itemList.addAll((Collection)PictureSelectorActivity.this.mItemMap.get(PictureSelectorActivity.this.mCurrentCatalog));
/*     */             }
/* 138 */             Intent intent = new Intent(PictureSelectorActivity.this, PicturePreviewActivity.class);
/* 139 */             intent.putExtra("index", position - 1);
/* 140 */             intent.putExtra("sendOrigin", PictureSelectorActivity.this.mSendOrigin);
/* 141 */             PictureSelectorActivity.this.startActivityForResult(intent, 0);
/*     */           }
/*     */         });
/* 145 */         this.mBtnSend.setOnClickListener(new View.OnClickListener()
/*     */         {
/*     */           public void onClick(View v) {
/* 148 */             Intent data = new Intent();
/* 149 */             ArrayList list = new ArrayList();
/* 150 */             for (String key : PictureSelectorActivity.this.mItemMap.keySet()) {
/* 151 */               for (PictureSelectorActivity.PicItem item : (List)PictureSelectorActivity.this.mItemMap.get(key)) {
/* 152 */                 if (item.selected) {
/* 153 */                   list.add(Uri.parse("file://" + item.uri));
/*     */                 }
/*     */               }
/*     */             }
/* 157 */             data.putExtra("sendOrigin", PictureSelectorActivity.this.mSendOrigin);
/* 158 */             data.putExtra("android.intent.extra.RETURN_RESULT", list);
/* 159 */             PictureSelectorActivity.this.setResult(-1, data);
/* 160 */             PictureSelectorActivity.this.finish();
/*     */           }
/*     */         });
/* 164 */         this.mPicType.setEnabled(true);
/* 165 */         this.mPicType.setTextColor(getResources().getColor(R.color.rc_picsel_toolbar_send_text_normal));
/* 166 */         this.mPicType.setOnClickListener(new View.OnClickListener()
/*     */         {
/*     */           public void onClick(View v) {
/* 169 */             PictureSelectorActivity.this.mCatalogView.setVisibility(0);
/*     */           }
/*     */         });
/* 173 */         this.mPreviewBtn.setOnClickListener(new View.OnClickListener()
/*     */         {
/*     */           public void onClick(View v) {
/* 176 */             PictureSelectorActivity.PicItemHolder.itemList = new ArrayList();
/* 177 */             for (String key : PictureSelectorActivity.this.mItemMap.keySet()) {
/* 178 */               for (PictureSelectorActivity.PicItem item : (List)PictureSelectorActivity.this.mItemMap.get(key)) {
/* 179 */                 if (item.selected) {
/* 180 */                   PictureSelectorActivity.PicItemHolder.itemList.add(item);
/*     */                 }
/*     */               }
/*     */             }
/* 184 */             Intent intent = new Intent(PictureSelectorActivity.this, PicturePreviewActivity.class);
/* 185 */             intent.putExtra("sendOrigin", PictureSelectorActivity.this.mSendOrigin);
/* 186 */             PictureSelectorActivity.this.startActivityForResult(intent, 0);
/*     */           }
/*     */         });
/* 190 */         this.mCatalogView.setOnTouchListener(new View.OnTouchListener()
/*     */         {
/*     */           public boolean onTouch(View v, MotionEvent event) {
/* 193 */             if ((event.getAction() == 1) && (PictureSelectorActivity.this.mCatalogView.getVisibility() == 0)) {
/* 194 */               PictureSelectorActivity.this.mCatalogView.setVisibility(8);
/*     */             }
/* 196 */             return true;
/*     */           }
/*     */         });
/* 200 */         this.mCatalogListView.setAdapter(new CatalogAdapter());
/* 201 */         this.mCatalogListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
/*     */         {
/*     */           public void onItemClick(AdapterView<?> parent, View view, int position, long id)
/*     */           {
/*     */             String catalog;
/*     */             String catalog;
/* 205 */             if (position == 0)
/* 206 */               catalog = "";
/*     */             else {
/* 208 */               catalog = (String)PictureSelectorActivity.this.mCatalogList.get(position - 1);
/*     */             }
/* 210 */             if (catalog.equals(PictureSelectorActivity.this.mCurrentCatalog)) {
/* 211 */               PictureSelectorActivity.this.mCatalogView.setVisibility(8);
/* 212 */               return;
/*     */             }
/*     */ 
/* 215 */             PictureSelectorActivity.access$002(PictureSelectorActivity.this, catalog);
/* 216 */             TextView textView = (TextView)view.findViewById(R.id.name);
/* 217 */             PictureSelectorActivity.this.mPicType.setText(textView.getText().toString());
/* 218 */             PictureSelectorActivity.this.mCatalogView.setVisibility(8);
/* 219 */             ((PictureSelectorActivity.CatalogAdapter)PictureSelectorActivity.this.mCatalogListView.getAdapter()).notifyDataSetChanged();
/* 220 */             ((PictureSelectorActivity.GridViewAdapter)PictureSelectorActivity.this.mGridView.getAdapter()).notifyDataSetChanged();
/*     */           }
/*     */         });
/* 224 */         AlbumBitmapCacheHelper.init(this);
/* 225 */         this.perWidth = ((((WindowManager)(WindowManager)getSystemService("window")).getDefaultDisplay().getWidth() - CommonUtils.dip2px(this, 4.0F)) / 3);
/*     */       }
/*     */ 
/*     */       protected void onActivityResult(int requestCode, int resultCode, Intent data)
/*     */       {
/* 230 */         super.onActivityResult(requestCode, resultCode, data);
/* 231 */         if (resultCode == 0)
/* 232 */           return;
/* 233 */         if (resultCode == 1) {
/* 234 */           setResult(-1, data);
/* 235 */           finish();
/* 236 */           return;
/*     */         }
/*     */ 
/* 239 */         switch (requestCode) {
/*     */         case 0:
/* 241 */           this.mSendOrigin = data.getBooleanExtra("sendOrigin", false);
/* 242 */           ArrayList list = PicItemHolder.itemList;
/* 243 */           for (PicItem it : list) {
/* 244 */             PicItem item = findByUri(it.uri);
/* 245 */             if (item != null) {
/* 246 */               item.selected = it.selected;
/*     */             }
/*     */           }
/* 249 */           ((GridViewAdapter)this.mGridView.getAdapter()).notifyDataSetChanged();
/* 250 */           ((CatalogAdapter)this.mCatalogListView.getAdapter()).notifyDataSetChanged();
/* 251 */           updateToolbar();
/*     */ 
/* 253 */           break;
/*     */         case 1:
/* 255 */           if (this.mTakePictureUri == null) {
/*     */             break;
/*     */           }
/* 258 */           PicItemHolder.itemList = new ArrayList();
/* 259 */           PicItem item = new PicItem();
/* 260 */           item.uri = this.mTakePictureUri.getPath();
/* 261 */           PicItemHolder.itemList.add(item);
/*     */ 
/* 263 */           Intent intent = new Intent(this, PicturePreviewActivity.class);
/* 264 */           startActivityForResult(intent, 0);
/*     */ 
/* 266 */           MediaScannerConnection.scanFile(this, new String[] { this.mTakePictureUri.getPath() }, null, new MediaScannerConnection.OnScanCompletedListener()
/*     */           {
/*     */             public void onScanCompleted(String path, Uri uri) {
/* 269 */               PictureSelectorActivity.this.updatePictureItems();
/*     */             }
/*     */           });
/* 273 */           break;
/*     */         }
/*     */       }
/*     */ 
/*     */       public boolean onKeyDown(int keyCode, KeyEvent event)
/*     */       {
/* 280 */         if ((keyCode == 4) && 
/* 281 */           (this.mCatalogView != null) && (this.mCatalogView.getVisibility() == 0)) {
/* 282 */           this.mCatalogView.setVisibility(8);
/* 283 */           return true;
/*     */         }
/*     */ 
/* 286 */         return super.onKeyDown(keyCode, event);
/*     */       }
/*     */ 
/*     */       protected void requestCamera() {
/* 290 */         File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
/* 291 */         if (!path.exists())
/* 292 */           path.mkdirs();
/* 293 */         String name = System.currentTimeMillis() + ".jpg";
/* 294 */         File file = new File(path, name);
/* 295 */         this.mTakePictureUri = Uri.fromFile(file);
/* 296 */         Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
/* 297 */         intent.putExtra("output", this.mTakePictureUri);
/* 298 */         startActivityForResult(intent, 1);
/*     */       }
/*     */ 
/*     */       private void updatePictureItems() {
/* 302 */         String[] projection = { "_data", "date_added" };
/* 303 */         String orderBy = "datetaken DESC";
/* 304 */         Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, orderBy);
/*     */ 
/* 307 */         this.mAllItemList = new ArrayList();
/* 308 */         this.mCatalogList = new ArrayList();
/* 309 */         this.mItemMap = new ArrayMap();
/* 310 */         if (cursor != null) {
/* 311 */           if (cursor.moveToFirst()) {
/*     */             do {
/* 313 */               PicItem item = new PicItem();
/* 314 */               item.uri = cursor.getString(0);
/* 315 */               this.mAllItemList.add(item);
/*     */ 
/* 317 */               int last = item.uri.lastIndexOf("/");
/*     */               String catalog;
/*     */               String catalog;
/* 319 */               if (last == 0) {
/* 320 */                 catalog = "/";
/*     */               } else {
/* 322 */                 int secondLast = item.uri.lastIndexOf("/", last - 1);
/* 323 */                 catalog = item.uri.substring(secondLast + 1, last);
/*     */               }
/*     */ 
/* 327 */               if (this.mItemMap.containsKey(catalog)) {
/* 328 */                 ((List)this.mItemMap.get(catalog)).add(item);
/*     */               } else {
/* 330 */                 ArrayList itemList = new ArrayList();
/* 331 */                 itemList.add(item);
/* 332 */                 this.mItemMap.put(catalog, itemList);
/* 333 */                 this.mCatalogList.add(catalog);
/*     */               }
/*     */             }
/* 335 */             while (cursor.moveToNext());
/*     */           }
/* 337 */           cursor.close();
/*     */         }
/*     */       }
/*     */ 
/*     */       private int getTotalSelectedNum() {
/* 342 */         int sum = 0;
/* 343 */         for (String key : this.mItemMap.keySet()) {
/* 344 */           for (PicItem item : (List)this.mItemMap.get(key)) {
/* 345 */             if (item.selected) {
/* 346 */               sum++;
/*     */             }
/*     */           }
/*     */         }
/* 350 */         return sum;
/*     */       }
/*     */ 
/*     */       private void updateToolbar() {
/* 354 */         int sum = getTotalSelectedNum();
/* 355 */         if (sum == 0) {
/* 356 */           this.mBtnSend.setEnabled(false);
/* 357 */           this.mBtnSend.setTextColor(getResources().getColor(R.color.rc_picsel_toolbar_send_text_disable));
/* 358 */           this.mBtnSend.setText(R.string.rc_picsel_toolbar_send);
/*     */ 
/* 360 */           this.mPreviewBtn.setEnabled(false);
/* 361 */           this.mPreviewBtn.setText(R.string.rc_picsel_toolbar_preview);
/* 362 */         } else if (sum <= 9) {
/* 363 */           this.mBtnSend.setEnabled(true);
/* 364 */           this.mBtnSend.setTextColor(getResources().getColor(R.color.rc_picsel_toolbar_send_text_normal));
/* 365 */           this.mBtnSend.setText(String.format(getResources().getString(R.string.rc_picsel_toolbar_send_num), new Object[] { Integer.valueOf(sum) }));
/*     */ 
/* 367 */           this.mPreviewBtn.setEnabled(true);
/* 368 */           this.mPreviewBtn.setText(String.format(getResources().getString(R.string.rc_picsel_toolbar_preview_num), new Object[] { Integer.valueOf(sum) }));
/*     */         }
/*     */       }
/*     */ 
/*     */       private PicItem getItemAt(int index) {
/* 373 */         int sum = 0;
/* 374 */         for (String key : this.mItemMap.keySet()) {
/* 375 */           for (PicItem item : (List)this.mItemMap.get(key)) {
/* 376 */             if (sum == index) {
/* 377 */               return item;
/*     */             }
/* 379 */             sum++;
/*     */           }
/*     */         }
/* 382 */         return null;
/*     */       }
/*     */ 
/*     */       private PicItem getItemAt(String catalog, int index) {
/* 386 */         if (!this.mItemMap.containsKey(catalog)) {
/* 387 */           return null;
/*     */         }
/* 389 */         int sum = 0;
/* 390 */         for (PicItem item : (List)this.mItemMap.get(catalog)) {
/* 391 */           if (sum == index) {
/* 392 */             return item;
/*     */           }
/* 394 */           sum++;
/*     */         }
/* 396 */         return null;
/*     */       }
/*     */ 
/*     */       private PicItem findByUri(String uri) {
/* 400 */         for (String key : this.mItemMap.keySet()) {
/* 401 */           for (PicItem item : (List)this.mItemMap.get(key)) {
/* 402 */             if (item.uri.equals(uri)) {
/* 403 */               return item;
/*     */             }
/*     */           }
/*     */         }
/* 407 */         return null;
/*     */       }
/*     */ 
/*     */       public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
/*     */       {
/* 771 */         switch (requestCode) {
/*     */         case 100:
/* 773 */           if (grantResults[0] != 0)
/*     */             break;
/* 775 */           if (permissions[0].equals("android.permission.READ_EXTERNAL_STORAGE")) {
/* 776 */             initView(); } else {
/* 777 */             if (!permissions[0].equals("android.permission.CAMERA")) break;
/* 778 */             requestCamera(); } break;
/*     */         default:
/* 783 */           super.onRequestPermissionsResult(requestCode, permissions, grantResults);
/*     */         }
/*     */       }
/*     */ 
/*     */       protected void onDestroy()
/*     */       {
/* 808 */         AlbumBitmapCacheHelper.getInstance().uninit();
/* 809 */         PicItemHolder.itemList = null;
/* 810 */         super.onDestroy();
/*     */       }
/*     */ 
/*     */       public static class PicItemHolder
/*     */       {
/*     */         public static ArrayList<PictureSelectorActivity.PicItem> itemList;
/*     */       }
/*     */ 
/*     */       public static class SelectBox extends ImageView
/*     */       {
/*     */         private boolean mIsChecked;
/*     */ 
/*     */         public SelectBox(Context context, AttributeSet attrs)
/*     */         {
/* 792 */           super(attrs);
/* 793 */           setImageResource(R.drawable.select_check_nor);
/*     */         }
/*     */ 
/*     */         public void setChecked(boolean check) {
/* 797 */           this.mIsChecked = check;
/* 798 */           setImageResource(this.mIsChecked ? R.drawable.select_check_sel : R.drawable.select_check_nor);
/*     */         }
/*     */ 
/*     */         public boolean getChecked() {
/* 802 */           return this.mIsChecked;
/*     */         }
/*     */       }
/*     */ 
/*     */       public static class PreviewBtn extends LinearLayout
/*     */       {
/*     */         private TextView mText;
/*     */ 
/*     */         public PreviewBtn(Context context, AttributeSet attrs)
/*     */         {
/* 728 */           super(attrs);
/*     */         }
/*     */ 
/*     */         public void init(Activity root) {
/* 732 */           this.mText = ((TextView)root.findViewById(R.id.preview_text));
/*     */         }
/*     */ 
/*     */         public void setText(int id) {
/* 736 */           this.mText.setText(id);
/*     */         }
/*     */ 
/*     */         public void setText(String text) {
/* 740 */           this.mText.setText(text);
/*     */         }
/*     */ 
/*     */         public void setEnabled(boolean enabled)
/*     */         {
/* 745 */           super.setEnabled(enabled);
/* 746 */           int color = enabled ? R.color.rc_picsel_toolbar_send_text_normal : R.color.rc_picsel_toolbar_send_text_disable;
/*     */ 
/* 748 */           this.mText.setTextColor(getResources().getColor(color));
/*     */         }
/*     */ 
/*     */         public boolean onTouchEvent(MotionEvent event)
/*     */         {
/* 753 */           if (isEnabled()) {
/* 754 */             switch (event.getAction()) {
/*     */             case 0:
/* 756 */               this.mText.setVisibility(4);
/* 757 */               break;
/*     */             case 1:
/* 759 */               this.mText.setVisibility(0);
/* 760 */               break;
/*     */             }
/*     */           }
/*     */ 
/* 764 */           return super.onTouchEvent(event);
/*     */         }
/*     */       }
/*     */ 
/*     */       public static class PicTypeBtn extends LinearLayout
/*     */       {
/*     */         TextView mText;
/*     */ 
/*     */         public PicTypeBtn(Context context, AttributeSet attrs)
/*     */         {
/* 691 */           super(attrs);
/*     */         }
/*     */ 
/*     */         public void init(Activity root) {
/* 695 */           this.mText = ((TextView)root.findViewById(R.id.type_text));
/*     */         }
/*     */ 
/*     */         public void setText(String text) {
/* 699 */           this.mText.setText(text);
/*     */         }
/*     */ 
/*     */         public void setTextColor(int color) {
/* 703 */           this.mText.setTextColor(color);
/*     */         }
/*     */ 
/*     */         public boolean onTouchEvent(MotionEvent event)
/*     */         {
/* 708 */           if (isEnabled()) {
/* 709 */             switch (event.getAction()) {
/*     */             case 0:
/* 711 */               this.mText.setVisibility(4);
/* 712 */               break;
/*     */             case 1:
/* 714 */               this.mText.setVisibility(0);
/* 715 */               break;
/*     */             }
/*     */           }
/*     */ 
/* 719 */           return super.onTouchEvent(event);
/*     */         }
/*     */       }
/*     */ 
/*     */       public static class PicItem
/*     */       {
/*     */         String uri;
/*     */         boolean selected;
/*     */       }
/*     */ 
/*     */       private class CatalogAdapter extends BaseAdapter
/*     */       {
/*     */         private LayoutInflater mInflater;
/*     */ 
/*     */         public CatalogAdapter()
/*     */         {
/* 563 */           this.mInflater = PictureSelectorActivity.this.getLayoutInflater();
/*     */         }
/*     */ 
/*     */         public int getCount()
/*     */         {
/* 568 */           return PictureSelectorActivity.this.mItemMap.size() + 1;
/*     */         }
/*     */ 
/*     */         public Object getItem(int position)
/*     */         {
/* 573 */           return null;
/*     */         }
/*     */ 
/*     */         public long getItemId(int position)
/*     */         {
/* 578 */           return position;
/*     */         }
/*     */ 
/*     */         public View getView(int position, View convertView, ViewGroup parent)
/*     */         {
/* 584 */           View view = convertView;
/*     */           ViewHolder holder;
/* 585 */           if (view == null) {
/* 586 */             view = this.mInflater.inflate(R.layout.rc_picsel_catalog_listview, parent, false);
/* 587 */             ViewHolder holder = new ViewHolder(null);
/* 588 */             holder.image = ((ImageView)view.findViewById(R.id.image));
/* 589 */             holder.name = ((TextView)view.findViewById(R.id.name));
/* 590 */             holder.number = ((TextView)view.findViewById(R.id.number));
/* 591 */             holder.selected = ((ImageView)view.findViewById(R.id.selected));
/* 592 */             view.setTag(holder);
/*     */           } else {
/* 594 */             holder = (ViewHolder)view.getTag();
/*     */           }
/*     */ 
/* 597 */           if (holder.image.getTag() != null) {
/* 598 */             String path = (String)holder.image.getTag();
/* 599 */             AlbumBitmapCacheHelper.getInstance().removePathFromShowlist(path);
/*     */           }
/*     */ 
/* 604 */           int num = 0;
/* 605 */           boolean showSelected = false;
/*     */           String name;
/* 606 */           if (position == 0) {
/* 607 */             if (PictureSelectorActivity.this.mItemMap.size() == 0) {
/* 608 */               holder.image.setImageResource(R.drawable.rc_picsel_empty_pic);
/*     */             } else {
/* 610 */               String path = ((PictureSelectorActivity.PicItem)((List)PictureSelectorActivity.this.mItemMap.get(PictureSelectorActivity.this.mCatalogList.get(0))).get(0)).uri;
/* 611 */               AlbumBitmapCacheHelper.getInstance().addPathToShowlist(path);
/* 612 */               holder.image.setTag(path);
/* 613 */               Bitmap bitmap = AlbumBitmapCacheHelper.getInstance().getBitmap(path, PictureSelectorActivity.this.perWidth, PictureSelectorActivity.this.perWidth, new AlbumBitmapCacheHelper.ILoadImageCallback()
/*     */               {
/*     */                 public void onLoadImageCallBack(Bitmap bitmap, String path1, Object[] objects) {
/* 616 */                   if (bitmap == null) {
/* 617 */                     return;
/*     */                   }
/* 619 */                   BitmapDrawable bd = new BitmapDrawable(PictureSelectorActivity.this.getResources(), bitmap);
/* 620 */                   View v = PictureSelectorActivity.this.mGridView.findViewWithTag(path1);
/* 621 */                   if (v != null) {
/* 622 */                     v.setBackgroundDrawable(bd);
/* 623 */                     PictureSelectorActivity.CatalogAdapter.this.notifyDataSetChanged();
/*     */                   }
/*     */                 }
/*     */               }
/*     */               , new Object[] { Integer.valueOf(position) });
/*     */ 
/* 627 */               if (bitmap != null) {
/* 628 */                 BitmapDrawable bd = new BitmapDrawable(PictureSelectorActivity.this.getResources(), bitmap);
/* 629 */                 holder.image.setBackgroundDrawable(bd);
/*     */               } else {
/* 631 */                 holder.image.setBackgroundResource(R.drawable.rc_grid_image_default);
/*     */               }
/*     */             }
/* 634 */             String name = PictureSelectorActivity.this.getResources().getString(R.string.rc_picsel_catalog_allpic);
/* 635 */             holder.number.setVisibility(8);
/* 636 */             showSelected = PictureSelectorActivity.this.mCurrentCatalog.isEmpty();
/*     */           } else {
/* 638 */             String path = ((PictureSelectorActivity.PicItem)((List)PictureSelectorActivity.this.mItemMap.get(PictureSelectorActivity.this.mCatalogList.get(position - 1))).get(0)).uri;
/* 639 */             name = (String)PictureSelectorActivity.this.mCatalogList.get(position - 1);
/* 640 */             num = ((List)PictureSelectorActivity.this.mItemMap.get(PictureSelectorActivity.this.mCatalogList.get(position - 1))).size();
/* 641 */             holder.number.setVisibility(0);
/* 642 */             showSelected = name.equals(PictureSelectorActivity.this.mCurrentCatalog);
/*     */ 
/* 644 */             AlbumBitmapCacheHelper.getInstance().addPathToShowlist(path);
/* 645 */             holder.image.setTag(path);
/* 646 */             Bitmap bitmap = AlbumBitmapCacheHelper.getInstance().getBitmap(path, PictureSelectorActivity.this.perWidth, PictureSelectorActivity.this.perWidth, new AlbumBitmapCacheHelper.ILoadImageCallback()
/*     */             {
/*     */               public void onLoadImageCallBack(Bitmap bitmap, String path1, Object[] objects) {
/* 649 */                 if (bitmap == null) {
/* 650 */                   return;
/*     */                 }
/* 652 */                 BitmapDrawable bd = new BitmapDrawable(PictureSelectorActivity.this.getResources(), bitmap);
/* 653 */                 View v = PictureSelectorActivity.this.mGridView.findViewWithTag(path1);
/* 654 */                 if (v != null) {
/* 655 */                   v.setBackgroundDrawable(bd);
/* 656 */                   PictureSelectorActivity.CatalogAdapter.this.notifyDataSetChanged();
/*     */                 }
/*     */               }
/*     */             }
/*     */             , new Object[] { Integer.valueOf(position) });
/*     */ 
/* 660 */             if (bitmap != null) {
/* 661 */               BitmapDrawable bd = new BitmapDrawable(PictureSelectorActivity.this.getResources(), bitmap);
/* 662 */               holder.image.setBackgroundDrawable(bd);
/*     */             } else {
/* 664 */               holder.image.setBackgroundResource(R.drawable.rc_grid_image_default);
/*     */             }
/*     */           }
/* 667 */           holder.name.setText(name);
/* 668 */           holder.number.setText(String.format(PictureSelectorActivity.this.getResources().getString(R.string.rc_picsel_catalog_number), new Object[] { Integer.valueOf(num) }));
/* 669 */           holder.selected.setVisibility(showSelected ? 0 : 4);
/* 670 */           return view;
/*     */         }
/*     */ 
/*     */         private class ViewHolder
/*     */         {
/*     */           ImageView image;
/*     */           TextView name;
/*     */           TextView number;
/*     */           ImageView selected;
/*     */ 
/*     */           private ViewHolder()
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */       private class GridViewAdapter extends BaseAdapter
/*     */       {
/*     */         private LayoutInflater mInflater;
/*     */ 
/*     */         public GridViewAdapter()
/*     */         {
/* 415 */           this.mInflater = PictureSelectorActivity.this.getLayoutInflater();
/*     */         }
/*     */ 
/*     */         public int getCount()
/*     */         {
/* 420 */           int sum = 1;
/* 421 */           if (PictureSelectorActivity.this.mCurrentCatalog.isEmpty()) {
/* 422 */             for (String key : PictureSelectorActivity.this.mItemMap.keySet())
/* 423 */               sum += ((List)PictureSelectorActivity.this.mItemMap.get(key)).size();
/*     */           }
/*     */           else {
/* 426 */             sum += ((List)PictureSelectorActivity.this.mItemMap.get(PictureSelectorActivity.this.mCurrentCatalog)).size();
/*     */           }
/* 428 */           return sum;
/*     */         }
/*     */ 
/*     */         public Object getItem(int position)
/*     */         {
/* 433 */           return null;
/*     */         }
/*     */ 
/*     */         public long getItemId(int position)
/*     */         {
/* 438 */           return position;
/*     */         }
/*     */ 
/*     */         @TargetApi(23)
/*     */         public View getView(int position, View convertView, ViewGroup parent) {
/* 444 */           if (position == 0) {
/* 445 */             View view = this.mInflater.inflate(R.layout.rc_picsel_grid_camera, parent, false);
/* 446 */             ImageButton mask = (ImageButton)view.findViewById(R.id.camera_mask);
/* 447 */             mask.setOnClickListener(new View.OnClickListener()
/*     */             {
/*     */               public void onClick(View v) {
/* 450 */                 if (Build.VERSION.SDK_INT >= 23) {
/* 451 */                   int checkPermission = v.getContext().checkSelfPermission("android.permission.CAMERA");
/* 452 */                   if (checkPermission != 0) {
/* 453 */                     if (PictureSelectorActivity.this.shouldShowRequestPermissionRationale("android.permission.CAMERA"))
/* 454 */                       PictureSelectorActivity.this.requestPermissions(new String[] { "android.permission.CAMERA" }, 100);
/*     */                     else {
/* 456 */                       new AlertDialog.Builder(PictureSelectorActivity.this).setMessage("您需要在设置里打开相机权限。").setPositiveButton("确认", new DialogInterface.OnClickListener()
/*     */                       {
/*     */                         public void onClick(DialogInterface dialog, int which)
/*     */                         {
/* 461 */                           PictureSelectorActivity.this.requestPermissions(new String[] { "android.permission.CAMERA" }, 100);
/*     */                         }
/*     */                       }).setNegativeButton("取消", null).create().show();
/*     */                     }
/*     */ 
/* 467 */                     return;
/*     */                   }
/*     */                 }
/* 470 */                 PictureSelectorActivity.this.requestCamera();
/*     */               }
/*     */             });
/* 473 */             return view;
/*     */           }
/*     */           PictureSelectorActivity.PicItem item;
/*     */           PictureSelectorActivity.PicItem item;
/* 477 */           if (PictureSelectorActivity.this.mCurrentCatalog.isEmpty())
/* 478 */             item = (PictureSelectorActivity.PicItem)PictureSelectorActivity.this.mAllItemList.get(position - 1);
/*     */           else {
/* 480 */             item = PictureSelectorActivity.this.getItemAt(PictureSelectorActivity.this.mCurrentCatalog, position - 1);
/*     */           }
/*     */ 
/* 483 */           View view = convertView;
/*     */           ViewHolder holder;
/* 485 */           if ((view == null) || (view.getTag() == null)) {
/* 486 */             view = this.mInflater.inflate(R.layout.rc_picsel_grid_item, parent, false);
/* 487 */             ViewHolder holder = new ViewHolder(null);
/* 488 */             holder.image = ((ImageView)view.findViewById(R.id.image));
/* 489 */             holder.mask = view.findViewById(R.id.mask);
/* 490 */             holder.checkBox = ((PictureSelectorActivity.SelectBox)view.findViewById(R.id.checkbox));
/* 491 */             view.setTag(holder);
/*     */           } else {
/* 493 */             holder = (ViewHolder)view.getTag();
/*     */           }
/*     */ 
/* 496 */           if (holder.image.getTag() != null) {
/* 497 */             String path = (String)holder.image.getTag();
/* 498 */             AlbumBitmapCacheHelper.getInstance().removePathFromShowlist(path);
/*     */           }
/*     */ 
/* 501 */           String path = item.uri;
/* 502 */           AlbumBitmapCacheHelper.getInstance().addPathToShowlist(path);
/* 503 */           holder.image.setTag(path);
/* 504 */           Bitmap bitmap = AlbumBitmapCacheHelper.getInstance().getBitmap(path, PictureSelectorActivity.this.perWidth, PictureSelectorActivity.this.perWidth, new AlbumBitmapCacheHelper.ILoadImageCallback()
/*     */           {
/*     */             public void onLoadImageCallBack(Bitmap bitmap, String path1, Object[] objects) {
/* 507 */               if (bitmap == null) {
/* 508 */                 return;
/*     */               }
/* 510 */               BitmapDrawable bd = new BitmapDrawable(PictureSelectorActivity.this.getResources(), bitmap);
/* 511 */               View v = PictureSelectorActivity.this.mGridView.findViewWithTag(path1);
/* 512 */               if (v != null)
/* 513 */                 v.setBackgroundDrawable(bd);
/*     */             }
/*     */           }
/*     */           , new Object[] { Integer.valueOf(position) });
/*     */ 
/* 516 */           if (bitmap != null) {
/* 517 */             BitmapDrawable bd = new BitmapDrawable(PictureSelectorActivity.this.getResources(), bitmap);
/* 518 */             holder.image.setBackgroundDrawable(bd);
/*     */           } else {
/* 520 */             holder.image.setBackgroundResource(R.drawable.rc_grid_image_default);
/*     */           }
/*     */ 
/* 523 */           holder.checkBox.setChecked(item.selected);
/* 524 */           holder.checkBox.setOnClickListener(new View.OnClickListener(holder, item)
/*     */           {
/*     */             public void onClick(View v) {
/* 527 */               if ((!this.val$holder.checkBox.getChecked()) && (PictureSelectorActivity.this.getTotalSelectedNum() == 9)) {
/* 528 */                 Toast.makeText(PictureSelectorActivity.this.getApplicationContext(), R.string.rc_picsel_selected_max, 0).show();
/* 529 */                 return;
/*     */               }
/*     */ 
/* 532 */               this.val$holder.checkBox.setChecked(!this.val$holder.checkBox.getChecked());
/* 533 */               this.val$item.selected = this.val$holder.checkBox.getChecked();
/* 534 */               if (this.val$item.selected)
/* 535 */                 this.val$holder.mask.setBackgroundColor(PictureSelectorActivity.this.getResources().getColor(R.color.rc_picsel_grid_mask_pressed));
/*     */               else {
/* 537 */                 this.val$holder.mask.setBackgroundDrawable(PictureSelectorActivity.this.getResources().getDrawable(R.drawable.rc_sp_grid_mask));
/*     */               }
/* 539 */               PictureSelectorActivity.this.updateToolbar();
/*     */             }
/*     */           });
/* 542 */           if (item.selected)
/* 543 */             holder.mask.setBackgroundColor(PictureSelectorActivity.this.getResources().getColor(R.color.rc_picsel_grid_mask_pressed));
/*     */           else {
/* 545 */             holder.mask.setBackgroundDrawable(PictureSelectorActivity.this.getResources().getDrawable(R.drawable.rc_sp_grid_mask));
/*     */           }
/*     */ 
/* 548 */           return view;
/*     */         }
/*     */ 
/*     */         private class ViewHolder
/*     */         {
/*     */           ImageView image;
/*     */           View mask;
/*     */           PictureSelectorActivity.SelectBox checkBox;
/*     */ 
/*     */           private ViewHolder()
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */     }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.activity.PictureSelectorActivity
 * JD-Core Version:    0.6.0
 */
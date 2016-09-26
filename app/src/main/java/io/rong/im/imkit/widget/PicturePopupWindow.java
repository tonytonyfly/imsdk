/*    */ package io.rong.imkit.widget;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.graphics.drawable.ColorDrawable;
/*    */ import android.media.MediaScannerConnection;
/*    */ import android.os.Environment;
/*    */ import android.view.LayoutInflater;
/*    */ import android.view.View;
/*    */ import android.view.View.OnClickListener;
/*    */ import android.widget.Button;
/*    */ import android.widget.PopupWindow;
/*    */ import android.widget.Toast;
/*    */ import io.rong.common.FileUtils;
/*    */ import io.rong.imkit.R.id;
/*    */ import io.rong.imkit.R.layout;
/*    */ import io.rong.imkit.R.string;
/*    */ import java.io.File;
/*    */ 
/*    */ public class PicturePopupWindow extends PopupWindow
/*    */ {
/*    */   private Button btn_save_pic;
/*    */   private Button btn_cancel;
/*    */ 
/*    */   public PicturePopupWindow(Context context, File imageFile)
/*    */   {
/* 24 */     super(context);
/* 25 */     LayoutInflater inflater = (LayoutInflater)context.getSystemService("layout_inflater");
/* 26 */     View menuView = inflater.inflate(R.layout.rc_pic_popup_window, null);
/* 27 */     menuView.setOnClickListener(new View.OnClickListener()
/*    */     {
/*    */       public void onClick(View v) {
/* 30 */         PicturePopupWindow.this.dismiss();
/*    */       }
/*    */     });
/* 34 */     this.btn_save_pic = ((Button)menuView.findViewById(R.id.rc_content));
/* 35 */     this.btn_save_pic.setOnClickListener(new View.OnClickListener(context, imageFile)
/*    */     {
/*    */       public void onClick(View v) {
/* 38 */         File path = Environment.getExternalStorageDirectory();
/* 39 */         String defaultPath = this.val$context.getString(R.string.rc_image_default_saved_path);
/* 40 */         File dir = new File(path, defaultPath);
/* 41 */         if (!dir.exists()) {
/* 42 */           dir.mkdirs();
/*    */         }
/*    */ 
/* 45 */         if ((this.val$imageFile != null) && (this.val$imageFile.exists())) {
/* 46 */           String name = System.currentTimeMillis() + ".jpg";
/* 47 */           FileUtils.copyFile(this.val$imageFile, dir.getPath() + File.separator, name);
/* 48 */           MediaScannerConnection.scanFile(this.val$context, new String[] { dir.getPath() + File.separator + name }, null, null);
/* 49 */           Toast.makeText(this.val$context, String.format(this.val$context.getString(R.string.rc_save_picture_at), new Object[] { dir.getPath() + File.separator + name }), 0).show();
/*    */         } else {
/* 51 */           Toast.makeText(this.val$context, this.val$context.getString(R.string.rc_src_file_not_found), 0).show();
/*    */         }
/* 53 */         PicturePopupWindow.this.dismiss();
/*    */       }
/*    */     });
/* 56 */     this.btn_cancel = ((Button)menuView.findViewById(R.id.rc_btn_cancel));
/* 57 */     this.btn_cancel.setOnClickListener(new View.OnClickListener() {
/*    */       public void onClick(View v) {
/* 59 */         PicturePopupWindow.this.dismiss();
/*    */       }
/*    */     });
/* 63 */     setContentView(menuView);
/* 64 */     setWidth(-1);
/* 65 */     setHeight(-2);
/* 66 */     setFocusable(true);
/* 67 */     ColorDrawable dw = new ColorDrawable(-1342177280);
/* 68 */     setBackgroundDrawable(dw);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.PicturePopupWindow
 * JD-Core Version:    0.6.0
 */
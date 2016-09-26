/*    */ package io.rong.imkit.activity;
/*    */ 
/*    */ import android.os.Bundle;
/*    */ import android.support.v4.app.Fragment;
/*    */ import android.support.v4.app.FragmentActivity;
/*    */ import android.support.v4.app.FragmentManager;
/*    */ import android.support.v4.app.FragmentTransaction;
/*    */ import android.view.Window;
/*    */ import io.rong.imkit.R.id;
/*    */ import io.rong.imkit.R.layout;
/*    */ import io.rong.imkit.fragment.FileListFragment;
/*    */ import java.util.List;
/*    */ 
/*    */ public class FileListActivity extends FragmentActivity
/*    */ {
/* 19 */   private int fragmentCount = 0;
/*    */ 
/*    */   protected void onCreate(Bundle savedInstanceState)
/*    */   {
/* 23 */     super.onCreate(savedInstanceState);
/* 24 */     getWindow().setFlags(2048, 2048);
/*    */ 
/* 26 */     requestWindowFeature(1);
/* 27 */     setContentView(R.layout.rc_ac_file_list);
/* 28 */     if (getSupportFragmentManager().findFragmentById(R.id.rc_ac_fl_storage_folder_list_fragment) == null) {
/* 29 */       FileListFragment fileListFragment = new FileListFragment();
/* 30 */       showFragment(fileListFragment);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void showFragment(Fragment fragment) {
/* 35 */     this.fragmentCount += 1;
/* 36 */     getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.rc_ac_fl_storage_folder_list_fragment, fragment).commitAllowingStateLoss();
/*    */   }
/*    */ 
/*    */   public void onBackPressed()
/*    */   {
/* 45 */     if (--this.fragmentCount == 0) {
/* 46 */       List fragments = getSupportFragmentManager().getFragments();
/* 47 */       if ((fragments != null) && (fragments.size() != 0)) {
/* 48 */         for (Fragment fragment : fragments) {
/* 49 */           if (fragment != null) {
/* 50 */             fragment.onDestroy();
/*    */           }
/*    */         }
/* 53 */         finish();
/*    */       }
/*    */     } else {
/* 56 */       super.onBackPressed();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.activity.FileListActivity
 * JD-Core Version:    0.6.0
 */
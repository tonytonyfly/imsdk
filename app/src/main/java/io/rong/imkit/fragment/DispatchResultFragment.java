/*    */ package io.rong.imkit.fragment;
/*    */ 
/*    */ import android.content.Intent;
/*    */ import android.support.v4.app.Fragment;
/*    */ import android.support.v4.app.FragmentManager;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ 
/*    */ public abstract class DispatchResultFragment extends UriFragment
/*    */ {
/*    */   public void onActivityResult(int requestCode, int resultCode, Intent data)
/*    */   {
/* 13 */     int index = requestCode >> 12;
/* 14 */     if (index != 0) {
/* 15 */       index--;
/*    */ 
/* 17 */       Fragment fragment = getOffsetFragment(index, this);
/*    */ 
/* 19 */       if (fragment != null) {
/* 20 */         fragment.onActivityResult(requestCode & 0xFFF, resultCode, data);
/*    */       }
/*    */ 
/* 23 */       return;
/*    */     }
/*    */ 
/* 26 */     super.onActivityResult(requestCode, resultCode, data);
/*    */   }
/*    */ 
/*    */   public void startActivityForResult(Fragment fragment, Intent intent, int requestCode)
/*    */   {
/* 31 */     int index = getFragmentOffset(0, fragment, this);
/*    */ 
/* 33 */     if (index > 15) {
/* 34 */       throw new RuntimeException("DispatchFragment only support 16 fragments。");
/*    */     }
/*    */ 
/* 37 */     if (requestCode == -1) {
/* 38 */       startActivityForResult(intent, -1);
/* 39 */       return;
/*    */     }
/*    */ 
/* 42 */     if ((requestCode & 0xFFFFF000) != 0) {
/* 43 */       throw new IllegalArgumentException("Can only use lower 12 bits for requestCode");
/*    */     }
/*    */ 
/* 46 */     startActivityForResult(intent, (index + 1 << 12) + (requestCode & 0xFFF));
/*    */   }
/*    */ 
/*    */   private int getFragmentOffset(int offset, Fragment targetFragment, Fragment parentFragment)
/*    */   {
/* 51 */     if ((parentFragment == null) || (parentFragment.getChildFragmentManager() == null) || (parentFragment.getChildFragmentManager().getFragments() == null))
/*    */     {
/* 53 */       return 0;
/*    */     }
/* 55 */     Iterator i$ = parentFragment.getChildFragmentManager().getFragments().iterator(); if (i$.hasNext()) { Fragment item = (Fragment)i$.next();
/* 56 */       offset++;
/* 57 */       if (targetFragment == item) {
/* 58 */         return offset;
/*    */       }
/* 60 */       return getFragmentOffset(offset, targetFragment, item);
/*    */     }
/*    */ 
/* 64 */     return 0;
/*    */   }
/*    */ 
/*    */   private Fragment getOffsetFragment(int offset, Fragment fragment)
/*    */   {
/* 69 */     if (offset == 0) {
/* 70 */       return fragment;
/*    */     }
/* 72 */     for (Fragment item : getChildFragmentManager().getFragments()) {
/* 73 */       offset--; if (offset == 0) {
/* 74 */         return item;
/*    */       }
/* 76 */       if ((item.getChildFragmentManager().getFragments() != null) && (item.getChildFragmentManager().getFragments().size() > 0)) {
/* 77 */         return getOffsetFragment(offset, item);
/*    */       }
/*    */     }
/*    */ 
/* 81 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.fragment.DispatchResultFragment
 * JD-Core Version:    0.6.0
 */
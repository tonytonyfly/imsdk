/*    */ package io.rong.imkit.widget;
/*    */ 
/*    */ import android.support.v4.app.DialogFragment;
/*    */ import android.view.View;
/*    */ 
/*    */ public class BaseDialogFragment extends DialogFragment
/*    */ {
/*    */   protected <T extends View> T getView(View view, int id)
/*    */   {
/* 13 */     return view.findViewById(id);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.BaseDialogFragment
 * JD-Core Version:    0.6.0
 */
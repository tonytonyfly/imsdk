/*    */ package io.rong.eventbus.util;
/*    */ 
/*    */ import android.annotation.TargetApi;
/*    */ import android.app.Activity;
/*    */ import android.app.AlertDialog.Builder;
/*    */ import android.app.Dialog;
/*    */ import android.content.Context;
/*    */ import android.content.DialogInterface;
/*    */ import android.content.DialogInterface.OnClickListener;
/*    */ import android.os.Bundle;
/*    */ import io.rong.eventbus.EventBus;
/*    */ 
/*    */ public class ErrorDialogFragments
/*    */ {
/* 17 */   public static int ERROR_DIALOG_ICON = 0;
/*    */   public static Class<?> EVENT_TYPE_ON_CLICK;
/*    */ 
/*    */   public static Dialog createDialog(Context context, Bundle arguments, DialogInterface.OnClickListener onClickListener)
/*    */   {
/* 23 */     AlertDialog.Builder builder = new AlertDialog.Builder(context);
/* 24 */     builder.setTitle(arguments.getString("de.greenrobot.eventbus.errordialog.title"));
/* 25 */     builder.setMessage(arguments.getString("de.greenrobot.eventbus.errordialog.message"));
/* 26 */     if (ERROR_DIALOG_ICON != 0) {
/* 27 */       builder.setIcon(ERROR_DIALOG_ICON);
/*    */     }
/* 29 */     builder.setPositiveButton(17039370, onClickListener);
/* 30 */     return builder.create();
/*    */   }
/*    */ 
/*    */   public static void handleOnClick(DialogInterface dialog, int which, Activity activity, Bundle arguments) {
/* 34 */     if (EVENT_TYPE_ON_CLICK != null) {
/*    */       Object event;
/*    */       try { event = EVENT_TYPE_ON_CLICK.newInstance();
/*    */       } catch (Exception e) {
/* 39 */         throw new RuntimeException("Event cannot be constructed", e);
/*    */       }
/* 41 */       EventBus eventBus = ErrorDialogManager.factory.config.getEventBus();
/* 42 */       eventBus.post(event);
/*    */     }
/* 44 */     boolean finish = arguments.getBoolean("de.greenrobot.eventbus.errordialog.finish_after_dialog", false);
/* 45 */     if ((finish) && (activity != null))
/* 46 */       activity.finish();
/*    */   }
/*    */ 
/*    */   public static class Support extends android.support.v4.app.DialogFragment
/*    */     implements DialogInterface.OnClickListener
/*    */   {
/*    */     public Dialog onCreateDialog(Bundle savedInstanceState)
/*    */     {
/* 66 */       return ErrorDialogFragments.createDialog(getActivity(), getArguments(), this);
/*    */     }
/*    */ 
/*    */     public void onClick(DialogInterface dialog, int which)
/*    */     {
/* 71 */       ErrorDialogFragments.handleOnClick(dialog, which, getActivity(), getArguments());
/*    */     }
/*    */   }
/*    */ 
/*    */   @TargetApi(11)
/*    */   public static class Honeycomb extends android.app.DialogFragment
/*    */     implements DialogInterface.OnClickListener
/*    */   {
/*    */     public Dialog onCreateDialog(Bundle savedInstanceState)
/*    */     {
/* 54 */       return ErrorDialogFragments.createDialog(getActivity(), getArguments(), this);
/*    */     }
/*    */ 
/*    */     public void onClick(DialogInterface dialog, int which)
/*    */     {
/* 59 */       ErrorDialogFragments.handleOnClick(dialog, which, getActivity(), getArguments());
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.util.ErrorDialogFragments
 * JD-Core Version:    0.6.0
 */
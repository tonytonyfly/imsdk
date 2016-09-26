/*    */ package io.rong.eventbus.util;
/*    */ 
/*    */ import android.annotation.TargetApi;
/*    */ import android.content.res.Resources;
/*    */ import android.os.Bundle;
/*    */ 
/*    */ public abstract class ErrorDialogFragmentFactory<T>
/*    */ {
/*    */   protected final ErrorDialogConfig config;
/*    */ 
/*    */   protected ErrorDialogFragmentFactory(ErrorDialogConfig config)
/*    */   {
/* 16 */     this.config = config;
/*    */   }
/*    */ 
/*    */   protected T prepareErrorFragment(ThrowableFailureEvent event, boolean finishAfterDialog, Bundle argumentsForErrorDialog)
/*    */   {
/* 24 */     if (event.isSuppressErrorUi())
/*    */     {
/* 26 */       return null;
/*    */     }
/*    */     Bundle bundle;
/*    */     Bundle bundle;
/* 29 */     if (argumentsForErrorDialog != null)
/* 30 */       bundle = (Bundle)argumentsForErrorDialog.clone();
/*    */     else {
/* 32 */       bundle = new Bundle();
/*    */     }
/*    */ 
/* 35 */     if (!bundle.containsKey("de.greenrobot.eventbus.errordialog.title")) {
/* 36 */       String title = getTitleFor(event, bundle);
/* 37 */       bundle.putString("de.greenrobot.eventbus.errordialog.title", title);
/*    */     }
/* 39 */     if (!bundle.containsKey("de.greenrobot.eventbus.errordialog.message")) {
/* 40 */       String message = getMessageFor(event, bundle);
/* 41 */       bundle.putString("de.greenrobot.eventbus.errordialog.message", message);
/*    */     }
/* 43 */     if (!bundle.containsKey("de.greenrobot.eventbus.errordialog.finish_after_dialog")) {
/* 44 */       bundle.putBoolean("de.greenrobot.eventbus.errordialog.finish_after_dialog", finishAfterDialog);
/*    */     }
/* 46 */     if ((!bundle.containsKey("de.greenrobot.eventbus.errordialog.event_type_on_close")) && (this.config.defaultEventTypeOnDialogClosed != null))
/*    */     {
/* 48 */       bundle.putSerializable("de.greenrobot.eventbus.errordialog.event_type_on_close", this.config.defaultEventTypeOnDialogClosed);
/*    */     }
/* 50 */     if ((!bundle.containsKey("de.greenrobot.eventbus.errordialog.icon_id")) && (this.config.defaultDialogIconId != 0)) {
/* 51 */       bundle.putInt("de.greenrobot.eventbus.errordialog.icon_id", this.config.defaultDialogIconId);
/*    */     }
/* 53 */     return createErrorFragment(event, bundle);
/*    */   }
/*    */ 
/*    */   protected abstract T createErrorFragment(ThrowableFailureEvent paramThrowableFailureEvent, Bundle paramBundle);
/*    */ 
/*    */   protected String getTitleFor(ThrowableFailureEvent event, Bundle arguments)
/*    */   {
/* 61 */     return this.config.resources.getString(this.config.defaultTitleId);
/*    */   }
/*    */ 
/*    */   protected String getMessageFor(ThrowableFailureEvent event, Bundle arguments)
/*    */   {
/* 66 */     int msgResId = this.config.getMessageIdForThrowable(event.throwable);
/* 67 */     return this.config.resources.getString(msgResId);
/*    */   }
/*    */ 
/*    */   @TargetApi(11)
/*    */   public static class Honeycomb extends ErrorDialogFragmentFactory<android.app.Fragment>
/*    */   {
/*    */     public Honeycomb(ErrorDialogConfig config)
/*    */     {
/* 88 */       super();
/*    */     }
/*    */ 
/*    */     protected android.app.Fragment createErrorFragment(ThrowableFailureEvent event, Bundle arguments) {
/* 92 */       ErrorDialogFragments.Honeycomb errorFragment = new ErrorDialogFragments.Honeycomb();
/* 93 */       errorFragment.setArguments(arguments);
/* 94 */       return errorFragment;
/*    */     }
/*    */   }
/*    */ 
/*    */   public static class Support extends ErrorDialogFragmentFactory<android.support.v4.app.Fragment>
/*    */   {
/*    */     public Support(ErrorDialogConfig config)
/*    */     {
/* 73 */       super();
/*    */     }
/*    */ 
/*    */     protected android.support.v4.app.Fragment createErrorFragment(ThrowableFailureEvent event, Bundle arguments) {
/* 77 */       ErrorDialogFragments.Support errorFragment = new ErrorDialogFragments.Support();
/* 78 */       errorFragment.setArguments(arguments);
/* 79 */       return errorFragment;
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.util.ErrorDialogFragmentFactory
 * JD-Core Version:    0.6.0
 */
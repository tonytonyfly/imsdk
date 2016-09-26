/*     */ package io.rong.eventbus.util;
/*     */ 
/*     */ import android.annotation.TargetApi;
/*     */ import android.app.Activity;
/*     */ import android.os.Build.VERSION;
/*     */ import android.os.Bundle;
/*     */ import android.support.v4.app.FragmentActivity;
/*     */ import android.util.Log;
/*     */ import io.rong.eventbus.EventBus;
/*     */ 
/*     */ public class ErrorDialogManager
/*     */ {
/*     */   public static ErrorDialogFragmentFactory<?> factory;
/*     */   protected static final String TAG_ERROR_DIALOG = "de.greenrobot.eventbus.error_dialog";
/*     */   protected static final String TAG_ERROR_DIALOG_MANAGER = "de.greenrobot.eventbus.error_dialog_manager";
/*     */   public static final String KEY_TITLE = "de.greenrobot.eventbus.errordialog.title";
/*     */   public static final String KEY_MESSAGE = "de.greenrobot.eventbus.errordialog.message";
/*     */   public static final String KEY_FINISH_AFTER_DIALOG = "de.greenrobot.eventbus.errordialog.finish_after_dialog";
/*     */   public static final String KEY_ICON_ID = "de.greenrobot.eventbus.errordialog.icon_id";
/*     */   public static final String KEY_EVENT_TYPE_ON_CLOSE = "de.greenrobot.eventbus.errordialog.event_type_on_close";
/*     */ 
/*     */   public static void attachTo(Activity activity)
/*     */   {
/* 174 */     attachTo(activity, false, null);
/*     */   }
/*     */ 
/*     */   public static void attachTo(Activity activity, boolean finishAfterDialog)
/*     */   {
/* 179 */     attachTo(activity, finishAfterDialog, null);
/*     */   }
/*     */ 
/*     */   public static void attachTo(Activity activity, boolean finishAfterDialog, Bundle argumentsForErrorDialog)
/*     */   {
/* 184 */     Object executionScope = activity.getClass();
/* 185 */     attachTo(activity, executionScope, finishAfterDialog, argumentsForErrorDialog);
/*     */   }
/*     */ 
/*     */   public static void attachTo(Activity activity, Object executionScope, boolean finishAfterDialog, Bundle argumentsForErrorDialog) {
/* 189 */     if (factory == null) {
/* 190 */       throw new RuntimeException("You must set the static factory field to configure error dialogs for your app.");
/*     */     }
/* 192 */     if (isSupportActivity(activity))
/* 193 */       SupportManagerFragment.attachTo(activity, executionScope, finishAfterDialog, argumentsForErrorDialog);
/*     */     else
/* 195 */       HoneycombManagerFragment.attachTo(activity, executionScope, finishAfterDialog, argumentsForErrorDialog);
/*     */   }
/*     */ 
/*     */   private static boolean isSupportActivity(Activity activity)
/*     */   {
/* 200 */     boolean isSupport = false;
/* 201 */     for (Class c = activity.getClass().getSuperclass(); ; c = c.getSuperclass()) {
/* 202 */       if (c == null) {
/* 203 */         throw new RuntimeException("Illegal activity type: " + activity.getClass());
/*     */       }
/* 205 */       String name = c.getName();
/* 206 */       if (name.equals("android.support.v4.app.FragmentActivity")) {
/* 207 */         isSupport = true;
/* 208 */         break;
/* 209 */       }if ((name.startsWith("com.actionbarsherlock.app")) && ((name.endsWith(".SherlockActivity")) || (name.endsWith(".SherlockListActivity")) || (name.endsWith(".SherlockPreferenceActivity"))))
/*     */       {
/* 212 */         throw new RuntimeException("Please use SherlockFragmentActivity. Illegal activity: " + name);
/* 213 */       }if (name.equals("android.app.Activity")) {
/* 214 */         if (Build.VERSION.SDK_INT >= 11) break;
/* 215 */         throw new RuntimeException("Illegal activity without fragment support. Either use Android 3.0+ or android.support.v4.app.FragmentActivity.");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 221 */     return isSupport;
/*     */   }
/*     */ 
/*     */   protected static void checkLogException(ThrowableFailureEvent event) {
/* 225 */     if (factory.config.logExceptions) {
/* 226 */       String tag = factory.config.tagForLoggingExceptions;
/* 227 */       if (tag == null) {
/* 228 */         tag = EventBus.TAG;
/*     */       }
/* 230 */       Log.i(tag, "Error dialog manager received exception", event.throwable);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean isInExecutionScope(Object executionScope, ThrowableFailureEvent event) {
/* 235 */     if (event != null) {
/* 236 */       Object eventExecutionScope = event.getExecutionScope();
/* 237 */       if ((eventExecutionScope != null) && (!eventExecutionScope.equals(executionScope)))
/*     */       {
/* 239 */         return false;
/*     */       }
/*     */     }
/* 242 */     return true;
/*     */   }
/*     */ 
/*     */   @TargetApi(11)
/*     */   public static class HoneycombManagerFragment extends android.app.Fragment
/*     */   {
/*     */     protected boolean finishAfterDialog;
/*     */     protected Bundle argumentsForErrorDialog;
/*     */     private EventBus eventBus;
/*     */     private Object executionScope;
/*     */ 
/*     */     public void onResume()
/*     */     {
/* 110 */       super.onResume();
/* 111 */       this.eventBus = ErrorDialogManager.factory.config.getEventBus();
/* 112 */       this.eventBus.register(this);
/*     */     }
/*     */ 
/*     */     public void onPause()
/*     */     {
/* 117 */       this.eventBus.unregister(this);
/* 118 */       super.onPause();
/*     */     }
/*     */ 
/*     */     public void onEventMainThread(ThrowableFailureEvent event) {
/* 122 */       if (!ErrorDialogManager.access$000(this.executionScope, event)) {
/* 123 */         return;
/*     */       }
/* 125 */       ErrorDialogManager.checkLogException(event);
/*     */ 
/* 128 */       android.app.FragmentManager fm = getFragmentManager();
/* 129 */       fm.executePendingTransactions();
/*     */ 
/* 131 */       android.app.DialogFragment existingFragment = (android.app.DialogFragment)fm.findFragmentByTag("de.greenrobot.eventbus.error_dialog");
/*     */ 
/* 133 */       if (existingFragment != null)
/*     */       {
/* 135 */         existingFragment.dismiss();
/*     */       }
/*     */ 
/* 138 */       android.app.DialogFragment errorFragment = (android.app.DialogFragment)ErrorDialogManager.factory.prepareErrorFragment(event, this.finishAfterDialog, this.argumentsForErrorDialog);
/*     */ 
/* 140 */       if (errorFragment != null)
/* 141 */         errorFragment.show(fm, "de.greenrobot.eventbus.error_dialog");
/*     */     }
/*     */ 
/*     */     public static void attachTo(Activity activity, Object executionScope, boolean finishAfterDialog, Bundle argumentsForErrorDialog)
/*     */     {
/* 146 */       android.app.FragmentManager fm = activity.getFragmentManager();
/* 147 */       HoneycombManagerFragment fragment = (HoneycombManagerFragment)fm.findFragmentByTag("de.greenrobot.eventbus.error_dialog_manager");
/*     */ 
/* 149 */       if (fragment == null) {
/* 150 */         fragment = new HoneycombManagerFragment();
/* 151 */         fm.beginTransaction().add(fragment, "de.greenrobot.eventbus.error_dialog_manager").commit();
/* 152 */         fm.executePendingTransactions();
/*     */       }
/* 154 */       fragment.finishAfterDialog = finishAfterDialog;
/* 155 */       fragment.argumentsForErrorDialog = argumentsForErrorDialog;
/* 156 */       fragment.executionScope = executionScope;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class SupportManagerFragment extends android.support.v4.app.Fragment
/*     */   {
/*     */     protected boolean finishAfterDialog;
/*     */     protected Bundle argumentsForErrorDialog;
/*     */     private EventBus eventBus;
/*     */     private boolean skipRegisterOnNextResume;
/*     */     private Object executionScope;
/*     */ 
/*     */     public void onCreate(Bundle savedInstanceState)
/*     */     {
/*  40 */       super.onCreate(savedInstanceState);
/*  41 */       this.eventBus = ErrorDialogManager.factory.config.getEventBus();
/*  42 */       this.eventBus.register(this);
/*  43 */       this.skipRegisterOnNextResume = true;
/*     */     }
/*     */ 
/*     */     public void onResume()
/*     */     {
/*  48 */       super.onResume();
/*  49 */       if (this.skipRegisterOnNextResume)
/*     */       {
/*  51 */         this.skipRegisterOnNextResume = false;
/*     */       } else {
/*  53 */         this.eventBus = ErrorDialogManager.factory.config.getEventBus();
/*  54 */         this.eventBus.register(this);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void onPause()
/*     */     {
/*  60 */       this.eventBus.unregister(this);
/*  61 */       super.onPause();
/*     */     }
/*     */ 
/*     */     public void onEventMainThread(ThrowableFailureEvent event) {
/*  65 */       if (!ErrorDialogManager.access$000(this.executionScope, event)) {
/*  66 */         return;
/*     */       }
/*  68 */       ErrorDialogManager.checkLogException(event);
/*     */ 
/*  70 */       android.support.v4.app.FragmentManager fm = getFragmentManager();
/*  71 */       fm.executePendingTransactions();
/*     */ 
/*  73 */       android.support.v4.app.DialogFragment existingFragment = (android.support.v4.app.DialogFragment)fm.findFragmentByTag("de.greenrobot.eventbus.error_dialog");
/*  74 */       if (existingFragment != null)
/*     */       {
/*  76 */         existingFragment.dismiss();
/*     */       }
/*     */ 
/*  79 */       android.support.v4.app.DialogFragment errorFragment = (android.support.v4.app.DialogFragment)ErrorDialogManager.factory.prepareErrorFragment(event, this.finishAfterDialog, this.argumentsForErrorDialog);
/*     */ 
/*  81 */       if (errorFragment != null)
/*  82 */         errorFragment.show(fm, "de.greenrobot.eventbus.error_dialog");
/*     */     }
/*     */ 
/*     */     public static void attachTo(Activity activity, Object executionScope, boolean finishAfterDialog, Bundle argumentsForErrorDialog)
/*     */     {
/*  88 */       android.support.v4.app.FragmentManager fm = ((FragmentActivity)activity).getSupportFragmentManager();
/*  89 */       SupportManagerFragment fragment = (SupportManagerFragment)fm.findFragmentByTag("de.greenrobot.eventbus.error_dialog_manager");
/*  90 */       if (fragment == null) {
/*  91 */         fragment = new SupportManagerFragment();
/*  92 */         fm.beginTransaction().add(fragment, "de.greenrobot.eventbus.error_dialog_manager").commit();
/*  93 */         fm.executePendingTransactions();
/*     */       }
/*  95 */       fragment.finishAfterDialog = finishAfterDialog;
/*  96 */       fragment.argumentsForErrorDialog = argumentsForErrorDialog;
/*  97 */       fragment.executionScope = executionScope;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.util.ErrorDialogManager
 * JD-Core Version:    0.6.0
 */
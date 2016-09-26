/*    */ package io.rong.imkit.widget.provider;
/*    */ 
/*    */ import android.annotation.TargetApi;
/*    */ import android.content.Context;
/*    */ import android.content.res.Resources;
/*    */ import android.graphics.drawable.Drawable;
/*    */ import android.os.Build.VERSION;
/*    */ import android.support.v4.app.FragmentActivity;
/*    */ import android.util.DisplayMetrics;
/*    */ import android.view.LayoutInflater;
/*    */ import android.view.MotionEvent;
/*    */ import android.view.View;
/*    */ import android.view.View.OnTouchListener;
/*    */ import android.view.ViewGroup;
/*    */ import android.widget.Button;
/*    */ import io.rong.imkit.R.drawable;
/*    */ import io.rong.imkit.R.layout;
/*    */ import io.rong.imkit.RongContext;
/*    */ import io.rong.imkit.RongIM.RequestPermissionsListener;
/*    */ import io.rong.imkit.fragment.MessageInputFragment;
/*    */ import io.rong.imkit.manager.AudioPlayManager;
/*    */ import io.rong.imkit.manager.AudioRecordManager;
/*    */ import io.rong.imkit.widget.InputView;
/*    */ import io.rong.imlib.model.Conversation;
/*    */ 
/*    */ public class VoiceInputProvider extends InputProvider.MainInputProvider
/*    */   implements View.OnTouchListener
/*    */ {
/*    */   float lastTouchY;
/*    */   boolean upDirection;
/*    */   float mOffsetLimit;
/*    */ 
/*    */   public void onAttached(MessageInputFragment fragment, InputView inputView)
/*    */   {
/* 30 */     super.onAttached(fragment, inputView);
/* 31 */     this.mOffsetLimit = (70.0F * fragment.getActivity().getResources().getDisplayMetrics().density);
/*    */   }
/*    */ 
/*    */   public void onSwitch(Context context)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void onDetached()
/*    */   {
/* 41 */     super.onDetached();
/*    */   }
/*    */ 
/*    */   public void onInputResume(InputView inputView, Conversation conversation)
/*    */   {
/* 46 */     setCurrentConversation(conversation);
/*    */   }
/*    */ 
/*    */   public VoiceInputProvider(RongContext context) {
/* 50 */     super(context);
/*    */   }
/*    */ 
/*    */   public Drawable obtainSwitchDrawable(Context context)
/*    */   {
/* 55 */     return context.getResources().getDrawable(R.drawable.rc_ic_voice);
/*    */   }
/*    */ 
/*    */   public View onCreateView(LayoutInflater inflater, ViewGroup parent, InputView inputView)
/*    */   {
/* 60 */     View view = inflater.inflate(R.layout.rc_wi_vo_provider, parent);
/* 61 */     Button inputBtn = (Button)view.findViewById(16908313);
/* 62 */     inputBtn.setOnTouchListener(this);
/* 63 */     return view;
/*    */   }
/*    */ 
/*    */   @TargetApi(23)
/*    */   public boolean onTouch(View v, MotionEvent event) {
/* 69 */     if (event == null)
/* 70 */       return false;
/* 71 */     if (Build.VERSION.SDK_INT >= 23) {
/* 72 */       int checkPermission = v.getContext().checkSelfPermission("android.permission.RECORD_AUDIO");
/* 73 */       if ((checkPermission != 0) && (RongContext.getInstance().getRequestPermissionListener() != null)) {
/* 74 */         if (event.getAction() == 0)
/* 75 */           RongContext.getInstance().getRequestPermissionListener().onPermissionRequest(new String[] { "android.permission.RECORD_AUDIO" }, 100);
/* 76 */         return false;
/*    */       }
/*    */     }
/*    */ 
/* 80 */     if (event.getAction() == 0) {
/* 81 */       AudioPlayManager.getInstance().stopPlay();
/* 82 */       AudioRecordManager.getInstance().startRecord(v.getRootView(), this.mCurrentConversation.getConversationType(), this.mCurrentConversation.getTargetId());
/* 83 */       this.lastTouchY = event.getY();
/* 84 */       this.upDirection = false;
/* 85 */     } else if (event.getAction() == 2) {
/* 86 */       if ((this.lastTouchY - event.getY() > this.mOffsetLimit) && (!this.upDirection)) {
/* 87 */         AudioRecordManager.getInstance().willCancelRecord();
/* 88 */         this.upDirection = true;
/* 89 */       } else if ((event.getY() - this.lastTouchY > -this.mOffsetLimit) && (this.upDirection)) {
/* 90 */         AudioRecordManager.getInstance().continueRecord();
/* 91 */         this.upDirection = false;
/*    */       }
/* 93 */     } else if ((event.getAction() == 1) || (event.getAction() == 3)) {
/* 94 */       AudioRecordManager.getInstance().stopRecord();
/*    */     }
/* 96 */     return false;
/*    */   }
/*    */ 
/*    */   public void onActive(Context context)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void onInactive(Context context)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.widget.provider.VoiceInputProvider
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.imlib.ipc;
/*    */ 
/*    */ import android.app.Service;
/*    */ import android.content.Intent;
/*    */ import android.os.IBinder;
/*    */ import android.os.Process;
/*    */ import io.rong.common.RLog;
/*    */ import io.rong.imlib.LibHandlerStub;
/*    */ 
/*    */ public class RongService extends Service
/*    */ {
/* 12 */   private final String TAG = RongService.class.getSimpleName();
/*    */ 
/*    */   public void onCreate()
/*    */   {
/* 16 */     super.onCreate();
/* 17 */     RLog.d(this.TAG, "onCreate, pid=" + Process.myPid());
/*    */   }
/*    */ 
/*    */   public IBinder onBind(Intent intent)
/*    */   {
/* 28 */     RLog.d(this.TAG, "onBind, pid=" + Process.myPid());
/* 29 */     String appKey = intent.getStringExtra("appKey");
/* 30 */     String deviceId = intent.getStringExtra("deviceId");
/* 31 */     return new LibHandlerStub(this, appKey, deviceId);
/*    */   }
/*    */ 
/*    */   public boolean onUnbind(Intent intent)
/*    */   {
/* 36 */     RLog.d(this.TAG, "onUnbind, pid=" + Process.myPid());
/* 37 */     return super.onUnbind(intent);
/*    */   }
/*    */ 
/*    */   public void onDestroy()
/*    */   {
/* 42 */     RLog.d(this.TAG, "onDestroy, pid=" + Process.myPid());
/* 43 */     Process.killProcess(Process.myPid());
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.ipc.RongService
 * JD-Core Version:    0.6.0
 */
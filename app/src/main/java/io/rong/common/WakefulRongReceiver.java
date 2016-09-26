/*     */ package io.rong.common;
/*     */ 
/*     */ import android.content.BroadcastReceiver;
/*     */ import android.content.ComponentName;
/*     */ import android.content.Context;
/*     */ import android.content.Intent;
/*     */ import android.os.PowerManager;
/*     */ import android.os.PowerManager.WakeLock;
/*     */ import android.util.Log;
/*     */ import android.util.SparseArray;
/*     */ 
/*     */ public abstract class WakefulRongReceiver extends BroadcastReceiver
/*     */ {
/*     */   private static final String EXTRA_WAKE_LOCK_ID = "android.support.content.wakelockid";
/*  62 */   private static final SparseArray<PowerManager.WakeLock> mActiveWakeLocks = new SparseArray();
/*     */ 
/*  64 */   private static int mNextId = 1;
/*     */ 
/*     */   public static ComponentName startWakefulService(Context context, Intent intent)
/*     */   {
/*  81 */     synchronized (mActiveWakeLocks) {
/*  82 */       int id = mNextId;
/*  83 */       mNextId += 1;
/*  84 */       if (mNextId <= 0) {
/*  85 */         mNextId = 1;
/*     */       }
/*     */ 
/*  88 */       intent.putExtra("android.support.content.wakelockid", id);
/*     */ 
/*  90 */       ComponentName comp = null;
/*     */       try {
/*  92 */         comp = context.startService(intent);
/*     */       } catch (SecurityException e) {
/*  94 */         e.printStackTrace();
/*     */       }
/*     */ 
/*  97 */       if (comp == null) {
/*  98 */         return null;
/*     */       }
/*     */ 
/* 101 */       PowerManager pm = (PowerManager)context.getSystemService("power");
/* 102 */       PowerManager.WakeLock wl = pm.newWakeLock(1, "wake:" + comp.flattenToShortString());
/*     */ 
/* 104 */       wl.setReferenceCounted(false);
/* 105 */       wl.acquire(60000L);
/* 106 */       mActiveWakeLocks.put(id, wl);
/* 107 */       Log.i("WakefulRongReceiver", "require wakelock. id:" + id);
/* 108 */       return comp;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean completeWakefulIntent(Intent intent)
/*     */   {
/* 121 */     int id = intent.getIntExtra("android.support.content.wakelockid", 0);
/* 122 */     Log.w("WakefulRongReceiver", "completeWakefulIntent id #" + id);
/* 123 */     if (id == 0) {
/* 124 */       return false;
/*     */     }
/* 126 */     synchronized (mActiveWakeLocks) {
/* 127 */       PowerManager.WakeLock wl = (PowerManager.WakeLock)mActiveWakeLocks.get(id);
/* 128 */       if (wl != null) {
/* 129 */         wl.release();
/* 130 */         mActiveWakeLocks.remove(id);
/* 131 */         return true;
/*     */       }
/*     */ 
/* 139 */       Log.w("WakefulRongReceiver", "No active wake lock id #" + id);
/* 140 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.common.WakefulRongReceiver
 * JD-Core Version:    0.6.0
 */
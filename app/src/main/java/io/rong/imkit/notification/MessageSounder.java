/*     */ package io.rong.imkit.notification;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.media.AudioManager;
/*     */ import android.media.MediaPlayer;
/*     */ import android.media.MediaPlayer.OnCompletionListener;
/*     */ import android.media.MediaPlayer.OnPreparedListener;
/*     */ import android.media.RingtoneManager;
/*     */ import android.net.Uri;
/*     */ import android.os.Handler;
/*     */ import android.os.Vibrator;
/*     */ import android.util.Log;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class MessageSounder
/*     */ {
/*     */   private static Context mContext;
/*     */   static MessageSounder mRoundMgr;
/*     */   Handler mHandler;
/*     */   NewMessageReminderRunnable mLastReminderRunnable;
/*     */ 
/*     */   public static void init(Context context)
/*     */   {
/*  26 */     mContext = context;
/*  27 */     mRoundMgr = new MessageSounder();
/*     */   }
/*     */ 
/*     */   MessageSounder() {
/*  31 */     this.mHandler = new Handler();
/*     */   }
/*     */ 
/*     */   public static MessageSounder getInstance() {
/*  35 */     return mRoundMgr;
/*     */   }
/*     */ 
/*     */   public void messageReminder() {
/*  39 */     if (this.mLastReminderRunnable == null) {
/*  40 */       this.mLastReminderRunnable = new NewMessageReminderRunnable();
/*  41 */       this.mHandler.post(this.mLastReminderRunnable);
/*     */     } else {
/*  43 */       this.mHandler.removeCallbacks(this.mLastReminderRunnable);
/*  44 */       this.mHandler.postDelayed(this.mLastReminderRunnable, 500L);
/*     */     }
/*     */   }
/*     */ 
/*     */   private int getMobileRingerMode()
/*     */   {
/*  81 */     AudioManager audioManager = (AudioManager)mContext.getSystemService("audio");
/*  82 */     return audioManager.getRingerMode();
/*     */   }
/*     */ 
/*     */   private void playSound(Uri uri)
/*     */   {
/*     */     try
/*     */     {
/*  89 */       MediaPlayer mMediaPlayer = new MediaPlayer();
/*     */ 
/*  91 */       mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
/*     */       {
/*     */         public void onPrepared(MediaPlayer mp)
/*     */         {
/*  95 */           mp.start();
/*     */         }
/*     */       });
/*  99 */       mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
/*     */       {
/*     */         public void onCompletion(MediaPlayer mp)
/*     */         {
/* 103 */           Log.d("RongIMService", "playNewMessageSound---onCompletion");
/* 104 */           mp.reset();
/* 105 */           mp.release();
/* 106 */           mp = null;
/*     */         }
/*     */       });
/* 110 */       mMediaPlayer.setDataSource(mContext, uri);
/* 111 */       mMediaPlayer.prepare();
/*     */     }
/*     */     catch (IllegalStateException e)
/*     */     {
/* 115 */       e.printStackTrace();
/*     */     } catch (IOException e) {
/* 117 */       e.printStackTrace();
/*     */     } catch (NullPointerException e) {
/* 119 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   class NewMessageReminderRunnable
/*     */     implements Runnable
/*     */   {
/*     */     NewMessageReminderRunnable()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*  53 */       AudioManager audioManager = (AudioManager)MessageSounder.mContext.getSystemService("audio");
/*     */ 
/*  55 */       switch (audioManager.getRingerMode()) {
/*     */       case 0:
/*  57 */         break;
/*     */       case 1:
/*  59 */         Vibrator vibrator = (Vibrator)MessageSounder.mContext.getSystemService("vibrator");
/*  60 */         vibrator.vibrate(200L);
/*  61 */         break;
/*     */       case 2:
/*  63 */         Uri alert = null;
/*     */ 
/*  68 */         alert = RingtoneManager.getDefaultUri(2);
/*     */ 
/*  71 */         if (alert == null) break;
/*  72 */         MessageSounder.this.playSound(alert);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.notification.MessageSounder
 * JD-Core Version:    0.6.0
 */
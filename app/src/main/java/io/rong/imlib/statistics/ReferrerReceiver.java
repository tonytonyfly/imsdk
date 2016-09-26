/*    */ package io.rong.imlib.statistics;
/*    */ 
/*    */ import android.content.BroadcastReceiver;
/*    */ import android.content.Context;
/*    */ import android.content.Intent;
/*    */ import android.content.SharedPreferences;
/*    */ import android.content.SharedPreferences.Editor;
/*    */ import android.util.Log;
/*    */ import java.net.URLDecoder;
/*    */ 
/*    */ public class ReferrerReceiver extends BroadcastReceiver
/*    */ {
/* 18 */   private static String key = "referrer";
/*    */ 
/*    */   public static String getReferrer(Context context)
/*    */   {
/* 23 */     return context.getSharedPreferences(key, 0).getString(key, null);
/*    */   }
/*    */ 
/*    */   public static void deleteReferrer(Context context)
/*    */   {
/* 29 */     context.getSharedPreferences(key, 0).edit().remove(key).commit();
/*    */   }
/*    */ 
/*    */   public void onReceive(Context context, Intent intent)
/*    */   {
/*    */     try
/*    */     {
/* 42 */       if ((null != intent) && (intent.getAction().equals("com.android.vending.INSTALL_REFERRER")))
/*    */       {
/* 45 */         String rawReferrer = intent.getStringExtra(key);
/* 46 */         if (null != rawReferrer)
/*    */         {
/* 49 */           String referrer = URLDecoder.decode(rawReferrer, "UTF-8");
/*    */ 
/* 52 */           Log.d("Statistics", "Referrer: " + referrer);
/*    */ 
/* 54 */           String[] parts = referrer.split("&");
/* 55 */           String cid = null;
/* 56 */           String uid = null;
/* 57 */           for (int i = 0; i < parts.length; i++) {
/* 58 */             if (parts[i].startsWith("countly_cid"))
/* 59 */               cid = parts[i].replace("countly_cid=", "").trim();
/* 60 */             if (parts[i].startsWith("countly_cuid"))
/* 61 */               uid = parts[i].replace("countly_cuid=", "").trim();
/*    */           }
/* 63 */           String res = "";
/* 64 */           if (cid != null)
/* 65 */             res = res + "&campaign_id=" + cid;
/* 66 */           if (uid != null) {
/* 67 */             res = res + "&campaign_user=" + uid;
/*    */           }
/* 69 */           Log.d("Statistics", "Processed: " + res);
/*    */ 
/* 71 */           if (!res.equals(""))
/* 72 */             context.getSharedPreferences(key, 0).edit().putString(key, res).commit();
/*    */         }
/*    */       }
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 78 */       Log.d("Statistics", e.toString());
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.statistics.ReferrerReceiver
 * JD-Core Version:    0.6.0
 */
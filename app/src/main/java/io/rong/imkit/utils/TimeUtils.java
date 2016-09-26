/*    */ package io.rong.imkit.utils;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.content.res.Resources;
/*    */ import io.rong.imkit.R.string;
/*    */ import io.rong.imkit.RongContext;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ 
/*    */ public class TimeUtils
/*    */ {
/*    */   public static String formatData(long timeMillis)
/*    */   {
/* 19 */     if (timeMillis == 0L) {
/* 20 */       return "";
/*    */     }
/* 22 */     String result = null;
/*    */ 
/* 24 */     int targetDay = (int)(timeMillis / 86400000L);
/* 25 */     int nowDay = (int)(System.currentTimeMillis() / 86400000L);
/*    */ 
/* 27 */     if (targetDay == nowDay) {
/* 28 */       result = fromatDate(timeMillis, "HH:mm");
/* 29 */     } else if (targetDay + 1 == nowDay) {
/* 30 */       Context context = RongContext.getInstance().getBaseContext();
/* 31 */       String formatString = context.getResources().getString(R.string.rc_yesterday_format);
/* 32 */       result = String.format(formatString, new Object[] { fromatDate(timeMillis, "HH:mm") });
/*    */     } else {
/* 34 */       result = fromatDate(timeMillis, "yyyy-MM-dd");
/*    */     }
/*    */ 
/* 38 */     return result;
/*    */   }
/*    */ 
/*    */   public static String formatTime(long timeMillis)
/*    */   {
/* 45 */     if (timeMillis == 0L) {
/* 46 */       return "";
/*    */     }
/* 48 */     String result = null;
/*    */ 
/* 50 */     int targetDay = (int)(timeMillis / 86400000L);
/* 51 */     int nowDay = (int)(System.currentTimeMillis() / 86400000L);
/*    */ 
/* 53 */     if (targetDay == nowDay) {
/* 54 */       result = fromatDate(timeMillis, "HH:mm");
/* 55 */     } else if (targetDay + 1 == nowDay) {
/* 56 */       Context context = RongContext.getInstance().getBaseContext();
/* 57 */       String formatString = context.getResources().getString(R.string.rc_yesterday_format);
/* 58 */       result = String.format(formatString, new Object[] { fromatDate(timeMillis, "HH:mm") });
/*    */     } else {
/* 60 */       result = fromatDate(timeMillis, "yyyy-MM-dd HH:mm");
/*    */     }
/*    */ 
/* 64 */     return result;
/*    */   }
/*    */ 
/*    */   private static String fromatDate(long timeMillis, String fromat)
/*    */   {
/* 70 */     SimpleDateFormat sdf = new SimpleDateFormat(fromat);
/* 71 */     return sdf.format(new Date(timeMillis));
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.utils.TimeUtils
 * JD-Core Version:    0.6.0
 */
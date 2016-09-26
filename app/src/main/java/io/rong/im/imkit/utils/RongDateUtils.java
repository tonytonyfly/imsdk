/*     */ package io.rong.imkit.utils;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.res.Configuration;
/*     */ import android.content.res.Resources;
/*     */ import android.provider.Settings.System;
/*     */ import io.rong.imkit.R.string;
/*     */ import io.rong.imkit.RongContext;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ 
/*     */ public class RongDateUtils
/*     */ {
/*     */   private static final int OTHER = 2014;
/*     */   private static final int TODAY = 6;
/*     */   private static final int YESTERDAY = 15;
/*     */ 
/*     */   public static int judgeDate(Date date)
/*     */   {
/*  20 */     Calendar calendarToday = Calendar.getInstance();
/*  21 */     calendarToday.set(11, 0);
/*  22 */     calendarToday.set(12, 0);
/*  23 */     calendarToday.set(13, 0);
/*  24 */     calendarToday.set(14, 0);
/*     */ 
/*  26 */     Calendar calendarYesterday = Calendar.getInstance();
/*  27 */     calendarYesterday.add(5, -1);
/*  28 */     calendarYesterday.set(11, 0);
/*  29 */     calendarYesterday.set(12, 0);
/*  30 */     calendarYesterday.set(13, 0);
/*  31 */     calendarYesterday.set(14, 0);
/*     */ 
/*  33 */     Calendar calendarTomorrow = Calendar.getInstance();
/*  34 */     calendarTomorrow.add(5, 1);
/*  35 */     calendarTomorrow.set(11, 0);
/*  36 */     calendarTomorrow.set(12, 0);
/*  37 */     calendarTomorrow.set(13, 0);
/*  38 */     calendarTomorrow.set(14, 0);
/*     */ 
/*  41 */     Calendar calendarTarget = Calendar.getInstance();
/*  42 */     calendarTarget.setTime(date);
/*     */ 
/*  44 */     if (calendarTarget.before(calendarYesterday))
/*  45 */       return 2014;
/*  46 */     if (calendarTarget.before(calendarToday))
/*  47 */       return 15;
/*  48 */     if (calendarTarget.before(calendarTomorrow)) {
/*  49 */       return 6;
/*     */     }
/*  51 */     return 2014;
/*     */   }
/*     */ 
/*     */   private static String getWeekDay(int dayInWeek)
/*     */   {
/*  56 */     String weekDay = "";
/*  57 */     switch (dayInWeek) {
/*     */     case 1:
/*  59 */       weekDay = RongContext.getInstance().getResources().getString(R.string.rc_sunsay_format);
/*  60 */       break;
/*     */     case 2:
/*  62 */       weekDay = RongContext.getInstance().getResources().getString(R.string.rc_monday_format);
/*  63 */       break;
/*     */     case 3:
/*  65 */       weekDay = RongContext.getInstance().getResources().getString(R.string.rc_tuesday_format);
/*  66 */       break;
/*     */     case 4:
/*  68 */       weekDay = RongContext.getInstance().getResources().getString(R.string.rc_wednesday_format);
/*  69 */       break;
/*     */     case 5:
/*  71 */       weekDay = RongContext.getInstance().getResources().getString(R.string.rc_thuresday_format);
/*  72 */       break;
/*     */     case 6:
/*  74 */       weekDay = RongContext.getInstance().getResources().getString(R.string.rc_friday_format);
/*  75 */       break;
/*     */     case 7:
/*  77 */       weekDay = RongContext.getInstance().getResources().getString(R.string.rc_saturday_format);
/*  78 */       break;
/*     */     }
/*     */ 
/*  82 */     return weekDay;
/*     */   }
/*     */ 
/*     */   public static boolean isTime24Hour(Context context) {
/*  86 */     String timeFormat = Settings.System.getString(context.getContentResolver(), "time_12_24");
/*     */ 
/*  90 */     return (timeFormat != null) && (timeFormat.equals("24"));
/*     */   }
/*     */ 
/*     */   private static String getTimeString(long dateMillis, Context context)
/*     */   {
/*  98 */     if (dateMillis <= 0L) {
/*  99 */       return "";
/*     */     }
/*     */ 
/* 102 */     Date date = new Date(dateMillis);
/* 103 */     String formatTime = null;
/* 104 */     if (isTime24Hour(context)) {
/* 105 */       formatTime = formatDate(date, "HH:mm");
/*     */     } else {
/* 107 */       Calendar calendarTime = Calendar.getInstance();
/* 108 */       calendarTime.setTimeInMillis(dateMillis);
/* 109 */       int hour = calendarTime.get(10);
/* 110 */       if (calendarTime.get(9) == 0) {
/* 111 */         if (hour < 6) {
/* 112 */           if (hour == 0) {
/* 113 */             hour = 12;
/*     */           }
/* 115 */           formatTime = RongContext.getInstance().getResources().getString(R.string.rc_daybreak_format);
/* 116 */         } else if ((hour >= 6) && (hour < 12)) {
/* 117 */           formatTime = RongContext.getInstance().getResources().getString(R.string.rc_morning_format);
/*     */         }
/*     */       }
/* 120 */       else if (hour == 0) {
/* 121 */         formatTime = RongContext.getInstance().getResources().getString(R.string.rc_noon_format);
/* 122 */         hour = 12;
/* 123 */       } else if ((hour >= 1) && (hour <= 5)) {
/* 124 */         formatTime = RongContext.getInstance().getResources().getString(R.string.rc_afternoon_format);
/* 125 */       } else if ((hour >= 6) && (hour <= 11)) {
/* 126 */         formatTime = RongContext.getInstance().getResources().getString(R.string.rc_night_format);
/*     */       }
/*     */ 
/* 130 */       int minuteInt = calendarTime.get(12);
/* 131 */       String minuteStr = Integer.toString(minuteInt);
/* 132 */       String timeStr = null;
/* 133 */       if (minuteInt < 10) {
/* 134 */         minuteStr = "0" + minuteStr;
/*     */       }
/* 136 */       timeStr = Integer.toString(hour) + ":" + minuteStr;
/*     */ 
/* 138 */       if (context.getResources().getConfiguration().locale.getCountry().equals("CN"))
/* 139 */         formatTime = formatTime + timeStr;
/*     */       else {
/* 141 */         formatTime = timeStr + " " + formatTime;
/*     */       }
/*     */     }
/* 144 */     return formatTime;
/*     */   }
/*     */ 
/*     */   private static String getDateTimeString(long dateMillis, boolean showTime, Context context) {
/* 148 */     if (dateMillis <= 0L) {
/* 149 */       return "";
/*     */     }
/*     */ 
/* 152 */     String formatDate = null;
/*     */ 
/* 154 */     Date date = new Date(dateMillis);
/* 155 */     int type = judgeDate(date);
/* 156 */     long time = System.currentTimeMillis();
/* 157 */     Calendar calendarCur = Calendar.getInstance();
/* 158 */     Calendar calendardate = Calendar.getInstance();
/* 159 */     calendardate.setTimeInMillis(dateMillis);
/* 160 */     calendarCur.setTimeInMillis(time);
/* 161 */     int month = calendardate.get(2);
/* 162 */     int year = calendardate.get(1);
/* 163 */     int weekInMonth = calendardate.get(8);
/* 164 */     int monthCur = calendarCur.get(2);
/* 165 */     int yearCur = calendarCur.get(1);
/* 166 */     int weekInMonthCur = calendarCur.get(8);
/*     */ 
/* 168 */     switch (type) {
/*     */     case 6:
/* 170 */       formatDate = getTimeString(dateMillis, context);
/* 171 */       break;
/*     */     case 15:
/* 174 */       String formatString = RongContext.getInstance().getResources().getString(R.string.rc_yesterday_format);
/* 175 */       if (showTime)
/* 176 */         formatDate = formatString + " " + getTimeString(dateMillis, context);
/*     */       else {
/* 178 */         formatDate = formatString;
/*     */       }
/* 180 */       break;
/*     */     case 2014:
/* 183 */       if (year == yearCur) {
/* 184 */         if ((month == monthCur) && (weekInMonth == weekInMonthCur)) {
/* 185 */           formatDate = getWeekDay(calendardate.get(7));
/*     */         }
/* 187 */         else if (context.getResources().getConfiguration().locale.getCountry().equals("CN")) {
/* 188 */           formatDate = formatDate(date, "M" + RongContext.getInstance().getResources().getString(R.string.rc_month_format) + "d" + RongContext.getInstance().getResources().getString(R.string.rc_day_format));
/*     */         }
/*     */         else {
/* 191 */           formatDate = formatDate(date, "M/d");
/*     */         }
/*     */ 
/*     */       }
/* 195 */       else if (context.getResources().getConfiguration().locale.getCountry().equals("CN")) {
/* 196 */         formatDate = formatDate(date, "yyyy" + RongContext.getInstance().getResources().getString(R.string.rc_year_format) + "M" + RongContext.getInstance().getResources().getString(R.string.rc_month_format) + "d" + RongContext.getInstance().getResources().getString(R.string.rc_day_format));
/*     */       }
/*     */       else
/*     */       {
/* 200 */         formatDate = formatDate(date, "M/d/yy");
/*     */       }
/*     */ 
/* 204 */       if (!showTime) break;
/* 205 */       formatDate = formatDate + " " + getTimeString(dateMillis, context); break;
/*     */     }
/*     */ 
/* 212 */     return formatDate;
/*     */   }
/*     */ 
/*     */   public static String getConversationListFormatDate(long dateMillis, Context context)
/*     */   {
/* 217 */     String formatDate = getDateTimeString(dateMillis, false, context);
/* 218 */     return formatDate;
/*     */   }
/*     */ 
/*     */   public static String getConversationFormatDate(long dateMillis, Context context) {
/* 222 */     String formatDate = getDateTimeString(dateMillis, true, context);
/* 223 */     return formatDate;
/*     */   }
/*     */ 
/*     */   public static boolean isShowChatTime(long currentTime, long preTime, int interval)
/*     */   {
/* 234 */     int typeCurrent = judgeDate(new Date(currentTime));
/* 235 */     int typePre = judgeDate(new Date(preTime));
/*     */ 
/* 237 */     if (typeCurrent == typePre)
/*     */     {
/* 240 */       return currentTime - preTime > interval * 1000;
/*     */     }
/*     */ 
/* 246 */     return true;
/*     */   }
/*     */ 
/*     */   public static String formatDate(Date date, String fromat)
/*     */   {
/* 255 */     SimpleDateFormat sdf = new SimpleDateFormat(fromat);
/* 256 */     return sdf.format(date);
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.utils.RongDateUtils
 * JD-Core Version:    0.6.0
 */
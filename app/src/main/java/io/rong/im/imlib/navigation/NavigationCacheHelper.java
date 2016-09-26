/*     */ package io.rong.imlib.navigation;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.SharedPreferences;
/*     */ import android.content.SharedPreferences.Editor;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.RFLog;
/*     */ import io.rong.common.RLog;
/*     */ import java.util.TimeZone;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.regex.PatternSyntaxException;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class NavigationCacheHelper
/*     */ {
/*     */   private static final String TAG = "NavigationCacheHelper";
/*     */   private static final String NAVIGATION_PREFERENCE = "RongNavigation";
/*     */   private static final long TIME_OUT = 7200000L;
/*     */   private static final String NAVI_TAG = "navi";
/*     */   private static final String CODE = "code";
/*     */   private static final String CMP_SERVER = "server";
/*     */   private static final String VOIP_SERVER = "voipServer";
/*     */   private static final String MEDIA_SERVER = "uploadServer";
/*     */   private static final String LOCATION_CONFIG = "location";
/*     */   private static final String CACHED_TIME = "cached_time";
/*     */   private static final String APP_KEY = "appKey";
/*     */   private static final String TOKEN = "token";
/*     */   private static final String VOIP_CALL_INFO = "voipCallInfo";
/*     */   private static final String LOG_MONITOR = "monitor";
/*     */   private static final String OPEN_MP = "openMp";
/*     */   private static final String GET_REMOTE_SERVICE = "historyMsg";
/*  47 */   private static long sCacheTime = 0L;
/*     */ 
/*     */   public static boolean isCacheValid(Context context, String appKey, String token) {
/*  50 */     SharedPreferences sharedPreferences = context.getSharedPreferences("RongNavigation", 0);
/*  51 */     String cachedKey = sharedPreferences.getString("appKey", null);
/*  52 */     String cachedToken = sharedPreferences.getString("token", null);
/*  53 */     String cachedCMP = sharedPreferences.getString("server", null);
/*  54 */     sCacheTime = sharedPreferences.getLong("cached_time", 0L);
/*  55 */     return (cachedKey != null) && (cachedKey.equals(appKey)) && (cachedToken != null) && (cachedToken.equals(token)) && (cachedCMP != null);
/*     */   }
/*     */ 
/*     */   public static boolean isMPOpened(Context context)
/*     */   {
/*  67 */     SharedPreferences sharedPreferences = context.getSharedPreferences("RongNavigation", 0);
/*  68 */     return sharedPreferences.getBoolean("openMp", true);
/*     */   }
/*     */ 
/*     */   public static boolean isCacheTimeout(Context context) {
/*  72 */     SharedPreferences sharedPreferences = context.getSharedPreferences("RongNavigation", 0);
/*  73 */     long cachedTime = sharedPreferences.getLong("cached_time", 0L);
/*  74 */     long currentTime = System.currentTimeMillis() - TimeZone.getDefault().getRawOffset();
/*  75 */     return currentTime - cachedTime > 7200000L;
/*     */   }
/*     */ 
/*     */   public static long getCachedTime() {
/*  79 */     return sCacheTime;
/*     */   }
/*     */ 
/*     */   public static void clearCache(Context context) {
/*  83 */     SharedPreferences sharedPreferences = context.getSharedPreferences("RongNavigation", 0);
/*  84 */     sharedPreferences.edit().clear().apply();
/*     */   }
/*     */ 
/*     */   public static String getCMPServer(Context context) {
/*  88 */     SharedPreferences sharedPreferences = context.getSharedPreferences("RongNavigation", 0);
/*  89 */     return sharedPreferences.getString("server", null);
/*     */   }
/*     */ 
/*     */   public static void updateTime(Context context, long time) {
/*  93 */     SharedPreferences sharedPreferences = context.getSharedPreferences("RongNavigation", 0);
/*  94 */     SharedPreferences.Editor editor = sharedPreferences.edit();
/*  95 */     editor.putLong("cached_time", time);
/*  96 */     editor.apply();
/*     */   }
/*     */ 
/*     */   public static String getVoIPCallInfo(Context context) {
/* 100 */     SharedPreferences sharedPreferences = context.getSharedPreferences("RongNavigation", 0);
/* 101 */     return sharedPreferences.getString("voipCallInfo", null);
/*     */   }
/*     */ 
/*     */   public static String getVoIPAddress(Context context) {
/* 105 */     SharedPreferences sharedPreferences = context.getSharedPreferences("RongNavigation", 0);
/* 106 */     return sharedPreferences.getString("voipServer", null);
/*     */   }
/*     */ 
/*     */   public static String getMediaServer(Context context) {
/* 110 */     SharedPreferences sharedPreferences = context.getSharedPreferences("RongNavigation", 0);
/* 111 */     return sharedPreferences.getString("uploadServer", null);
/*     */   }
/*     */ 
/*     */   public static boolean isGetRemoteEnabled(Context context) {
/* 115 */     SharedPreferences sharedPreferences = context.getSharedPreferences("RongNavigation", 0);
/* 116 */     return sharedPreferences.getBoolean("historyMsg", false);
/*     */   }
/*     */ 
/*     */   public static LocationConfig getLocationConfig(Context context) {
/* 120 */     SharedPreferences sharedPreferences = context.getSharedPreferences("RongNavigation", 0);
/* 121 */     String value = sharedPreferences.getString("location", null);
/* 122 */     if (!TextUtils.isEmpty(value)) {
/*     */       try {
/* 124 */         LocationConfig config = new LocationConfig();
/* 125 */         JSONObject jsonObj = new JSONObject(value);
/* 126 */         config.setConfigure(jsonObj.optBoolean("configure"));
/*     */ 
/* 128 */         if (jsonObj.has("conversationTypes")) {
/* 129 */           JSONArray array = jsonObj.optJSONArray("conversationTypes");
/* 130 */           int[] types = new int[array.length()];
/* 131 */           for (int j = 0; j < array.length(); j++) {
/* 132 */             types[j] = array.optInt(j);
/*     */           }
/* 134 */           config.setConversationTypes(types);
/*     */         }
/*     */ 
/* 137 */         config.setMaxParticipant(jsonObj.optInt("maxParticipant"));
/* 138 */         config.setDistanceFilter(jsonObj.optInt("distanceFilter"));
/* 139 */         config.setRefreshInterval(jsonObj.optInt("refreshInterval"));
/* 140 */         return config;
/*     */       } catch (JSONException e) {
/* 142 */         e.printStackTrace();
/*     */       }
/*     */     }
/* 145 */     return null;
/*     */   }
/*     */ 
/*     */   public static void cacheRequest(Context context, String appKey, String token) {
/* 149 */     SharedPreferences sharedPreferences = context.getSharedPreferences("RongNavigation", 0);
/* 150 */     SharedPreferences.Editor editor = sharedPreferences.edit();
/* 151 */     long gmtTimestamp = System.currentTimeMillis() - TimeZone.getDefault().getRawOffset();
/* 152 */     editor.putLong("cached_time", gmtTimestamp);
/* 153 */     editor.putString("appKey", appKey);
/* 154 */     editor.putString("token", token);
/* 155 */     editor.apply();
/*     */   }
/*     */ 
/*     */   private static boolean isValidCmp(String cmp) {
/*     */     try {
/* 160 */       String pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
/* 161 */       Pattern pat = Pattern.compile(pattern);
/* 162 */       Matcher mat = pat.matcher(cmp);
/* 163 */       return mat.find();
/*     */     } catch (PatternSyntaxException e) {
/* 165 */       RLog.e("NavigationCacheHelper", "isValidCmp : " + cmp);
/* 166 */       e.printStackTrace();
/* 167 */     }return false;
/*     */   }
/*     */ 
/*     */   public static int decode2File(Context context, String data, int httpCode)
/*     */   {
/* 172 */     int DATA_ERROR = 30008;
/* 173 */     int TOKEN_ERROR = 31004;
/* 174 */     int RESOURCE_ERROR = 30007;
/*     */ 
/* 176 */     if (TextUtils.isEmpty(data)) {
/* 177 */       RLog.e("NavigationCacheHelper", "decode2File : navi data is empty.");
/* 178 */       return 30008;
/*     */     }
/*     */ 
/* 181 */     if (!data.contains("code")) {
/* 182 */       RLog.e("NavigationCacheHelper", "decode2File : code is empty.");
/* 183 */       return 30008;
/*     */     }
/*     */ 
/* 186 */     if (data.contains("code")) {
/* 187 */       SharedPreferences sharedPreferences = context.getSharedPreferences("RongNavigation", 0);
/* 188 */       SharedPreferences.Editor editor = sharedPreferences.edit();
/*     */ 
/* 190 */       String value = decode(data, "<code>", "</code>");
/* 191 */       if (TextUtils.isEmpty(value)) {
/* 192 */         RLog.e("NavigationCacheHelper", "decode2File : code is empty.");
/* 193 */         return 30008;
/*     */       }
/*     */       try {
/* 196 */         int code = Integer.parseInt(value);
/* 197 */         if (code != 200) {
/* 198 */           RLog.w("NavigationCacheHelper", "decode2File : code & httpCode " + code + "-" + httpCode);
/* 199 */           if (((code == 401) && (httpCode == 403)) || ((code == 403) && (httpCode == 401)))
/*     */           {
/* 201 */             return 31004;
/*     */           }
/* 203 */           return 30007;
/*     */         }
/*     */       } catch (NumberFormatException e) {
/* 206 */         e.printStackTrace();
/* 207 */         RLog.w("NavigationCacheHelper", "decode2File : NumberFormatException " + e.getMessage());
/* 208 */         return 30007;
/*     */       }
/*     */ 
/* 211 */       value = decode(data, "<server>", "</server>");
/* 212 */       if (TextUtils.isEmpty(value)) {
/* 213 */         RLog.e("NavigationCacheHelper", "decode2File : cmp is invalid");
/* 214 */         return 30008;
/*     */       }
/* 216 */       editor.putString("server", value);
/*     */ 
/* 218 */       value = decode(data, "<uploadServer>", "</uploadServer>");
/* 219 */       if (!TextUtils.isEmpty(value)) {
/* 220 */         editor.putString("uploadServer", value);
/*     */       }
/*     */ 
/* 223 */       value = decode(data, "<location>", "</location>");
/* 224 */       if (!TextUtils.isEmpty(value)) {
/* 225 */         editor.putString("location", value.replaceAll("&quot;", "\""));
/*     */       }
/*     */ 
/* 228 */       value = decode(data, "<voipCallInfo>", "</voipCallInfo>");
/* 229 */       if (!TextUtils.isEmpty(value)) {
/* 230 */         editor.putString("voipCallInfo", value.replaceAll("&quot;", "\""));
/*     */       }
/*     */ 
/* 233 */       value = decode(data, "<historyMsg>", "</historyMsg>");
/* 234 */       boolean opened = false;
/* 235 */       if (!TextUtils.isEmpty(value)) {
/* 236 */         opened = value.equals("true");
/*     */       }
/* 238 */       editor.putBoolean("historyMsg", opened);
/*     */ 
/* 240 */       value = decode(data, "<openMp>", "</openMp>");
/* 241 */       opened = true;
/* 242 */       if (!TextUtils.isEmpty(value)) {
/* 243 */         opened = Integer.parseInt(value) == 1;
/*     */       }
/* 245 */       editor.putBoolean("openMp", opened);
/*     */ 
/* 247 */       value = decode(data, "<monitor>", "</monitor>");
/* 248 */       if (!TextUtils.isEmpty(value)) {
/* 249 */         RFLog.setMode(context, Integer.valueOf(value).intValue());
/*     */       }
/*     */ 
/* 252 */       editor.apply();
/* 253 */       return 0;
/*     */     }
/* 255 */     return 30008;
/*     */   }
/*     */ 
/*     */   private static String decode(String data, String key1, String key2) {
/* 259 */     int start = data.indexOf(key1) + key1.length();
/* 260 */     int end = data.indexOf(key2);
/* 261 */     if ((start >= end) || (end == 0)) {
/* 262 */       return null;
/*     */     }
/* 264 */     return data.substring(start, end);
/*     */   }
/*     */ 
/*     */   public static String decode2cmp(Context context, String data, int httpCode) {
/* 268 */     if (TextUtils.isEmpty(data)) {
/* 269 */       RLog.e("NavigationCacheHelper", "decode2cmp : navi data invalid");
/* 270 */       return null;
/*     */     }
/*     */ 
/* 273 */     if ((!data.contains("server")) || (!data.contains("code"))) {
/* 274 */       RLog.e("NavigationCacheHelper", "decode2cmp : data - " + data);
/* 275 */       RLog.e("NavigationCacheHelper", "decode2cmp : cmp or code invalid");
/* 276 */       return null;
/*     */     }
/*     */ 
/* 279 */     if (data.contains("code")) {
/* 280 */       String cmp = "";
/* 281 */       String value = decode(data, "<code>", "</code>");
/* 282 */       if (TextUtils.isEmpty(value)) {
/* 283 */         RLog.e("NavigationCacheHelper", "decode2cmp : code invalid");
/* 284 */         return null;
/*     */       }
/*     */       try
/*     */       {
/* 288 */         int code = Integer.parseInt(value);
/* 289 */         if (code != 200) {
/* 290 */           RLog.w("NavigationCacheHelper", "decode2cmp : code & httpCode " + code + "-" + httpCode);
/* 291 */           if (((code == 401) && (httpCode == 403)) || ((code == 403) && (httpCode == 401)))
/*     */           {
/* 293 */             return null;
/*     */           }
/* 295 */           return null;
/*     */         }
/*     */       } catch (NumberFormatException e) {
/* 298 */         e.printStackTrace();
/* 299 */         RLog.w("NavigationCacheHelper", "decode2cmp : NumberFormatException " + e.getMessage());
/* 300 */         return null;
/*     */       }
/*     */ 
/* 303 */       value = decode(data, "<server>", "</server>");
/* 304 */       if (TextUtils.isEmpty(value)) {
/* 305 */         RLog.e("NavigationCacheHelper", "decode2cmp : data - " + data);
/* 306 */         RLog.e("NavigationCacheHelper", "decode2cmp : cmp invalid - " + value);
/* 307 */         return null;
/*     */       }
/* 309 */       return value;
/*     */     }
/* 311 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.navigation.NavigationCacheHelper
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.imlib.statistics;
/*     */ 
/*     */ import android.util.Log;
/*     */ import java.io.File;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URL;
/*     */ import java.net.URLDecoder;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public class UserData
/*     */ {
/*     */   public static final String NAME_KEY = "name";
/*     */   public static final String USERNAME_KEY = "username";
/*     */   public static final String EMAIL_KEY = "email";
/*     */   public static final String ORG_KEY = "organization";
/*     */   public static final String PHONE_KEY = "phone";
/*     */   public static final String PICTURE_KEY = "picture";
/*     */   public static final String PICTURE_PATH_KEY = "picturePath";
/*     */   public static final String GENDER_KEY = "gender";
/*     */   public static final String BYEAR_KEY = "byear";
/*     */   public static final String CUSTOM_KEY = "custom";
/*     */   public static String name;
/*     */   public static String username;
/*     */   public static String email;
/*     */   public static String org;
/*     */   public static String phone;
/*     */   public static String picture;
/*     */   public static String picturePath;
/*     */   public static String gender;
/*     */   public static Map<String, String> custom;
/*  38 */   public static int byear = 0;
/*  39 */   public static boolean isSynced = true;
/*     */ 
/*     */   static void setData(Map<String, String> data)
/*     */   {
/*  47 */     if (data.containsKey("name"))
/*  48 */       name = (String)data.get("name");
/*  49 */     if (data.containsKey("username"))
/*  50 */       username = (String)data.get("username");
/*  51 */     if (data.containsKey("email"))
/*  52 */       email = (String)data.get("email");
/*  53 */     if (data.containsKey("organization"))
/*  54 */       org = (String)data.get("organization");
/*  55 */     if (data.containsKey("phone"))
/*  56 */       phone = (String)data.get("phone");
/*  57 */     if (data.containsKey("picturePath"))
/*  58 */       picturePath = (String)data.get("picturePath");
/*  59 */     if (picturePath != null) {
/*  60 */       File sourceFile = new File(picturePath);
/*  61 */       if (!sourceFile.isFile()) {
/*  62 */         if (Statistics.sharedInstance().isLoggingEnabled()) {
/*  63 */           Log.w("Statistics", "Provided file " + picturePath + " can not be opened");
/*     */         }
/*  65 */         picturePath = null;
/*     */       }
/*     */     }
/*  68 */     if (data.containsKey("picture"))
/*  69 */       picture = (String)data.get("picture");
/*  70 */     if (data.containsKey("gender"))
/*  71 */       gender = (String)data.get("gender");
/*  72 */     if (data.containsKey("byear")) {
/*     */       try {
/*  74 */         byear = Integer.parseInt((String)data.get("byear"));
/*     */       }
/*     */       catch (NumberFormatException e) {
/*  77 */         if (Statistics.sharedInstance().isLoggingEnabled()) {
/*  78 */           Log.w("Statistics", "Incorrect byear number format");
/*     */         }
/*  80 */         byear = 0;
/*     */       }
/*     */     }
/*  83 */     isSynced = false;
/*     */   }
/*     */ 
/*     */   static void setCustomData(Map<String, String> data)
/*     */   {
/*  91 */     custom = new HashMap();
/*  92 */     custom.putAll(data);
/*  93 */     isSynced = false;
/*     */   }
/*     */ 
/*     */   static String getDataForRequest()
/*     */   {
/* 101 */     if (!isSynced) {
/* 102 */       isSynced = true;
/* 103 */       JSONObject json = toJSON();
/* 104 */       if (json != null) {
/* 105 */         String result = json.toString();
/*     */         try
/*     */         {
/* 108 */           result = URLEncoder.encode(result, "UTF-8");
/*     */ 
/* 110 */           if ((result != null) && (!result.equals(""))) {
/* 111 */             result = "&user_details=" + result;
/* 112 */             if (picturePath != null)
/* 113 */               result = result + "&picturePath=" + URLEncoder.encode(picturePath, "UTF-8");
/*     */           }
/*     */           else {
/* 116 */             result = "";
/* 117 */             if (picturePath != null)
/* 118 */               result = result + "&user_details&picturePath=" + URLEncoder.encode(picturePath, "UTF-8");
/*     */           }
/*     */         }
/*     */         catch (UnsupportedEncodingException ignored)
/*     */         {
/*     */         }
/* 124 */         if (result != null)
/* 125 */           return result;
/*     */       }
/*     */     }
/* 128 */     return "";
/*     */   }
/*     */ 
/*     */   static JSONObject toJSON()
/*     */   {
/* 136 */     JSONObject json = new JSONObject();
/*     */     try
/*     */     {
/* 139 */       if (name != null)
/* 140 */         if (name == "")
/* 141 */           json.put("name", JSONObject.NULL);
/*     */         else
/* 143 */           json.put("name", name);
/* 144 */       if (username != null)
/* 145 */         if (username == "")
/* 146 */           json.put("username", JSONObject.NULL);
/*     */         else
/* 148 */           json.put("username", username);
/* 149 */       if (email != null)
/* 150 */         if (email == "")
/* 151 */           json.put("email", JSONObject.NULL);
/*     */         else
/* 153 */           json.put("email", email);
/* 154 */       if (org != null)
/* 155 */         if (org == "")
/* 156 */           json.put("organization", JSONObject.NULL);
/*     */         else
/* 158 */           json.put("organization", org);
/* 159 */       if (phone != null)
/* 160 */         if (phone == "")
/* 161 */           json.put("phone", JSONObject.NULL);
/*     */         else
/* 163 */           json.put("phone", phone);
/* 164 */       if (picture != null)
/* 165 */         if (picture == "")
/* 166 */           json.put("picture", JSONObject.NULL);
/*     */         else
/* 168 */           json.put("picture", picture);
/* 169 */       if (gender != null)
/* 170 */         if (gender == "")
/* 171 */           json.put("gender", JSONObject.NULL);
/*     */         else
/* 173 */           json.put("gender", gender);
/* 174 */       if (byear != 0)
/* 175 */         if (byear > 0)
/* 176 */           json.put("byear", byear);
/*     */         else
/* 178 */           json.put("byear", JSONObject.NULL);
/* 179 */       if (custom != null)
/* 180 */         if (custom.isEmpty())
/* 181 */           json.put("custom", JSONObject.NULL);
/*     */         else
/* 183 */           json.put("custom", new JSONObject(custom));
/*     */     }
/*     */     catch (JSONException e)
/*     */     {
/* 187 */       if (Statistics.sharedInstance().isLoggingEnabled()) {
/* 188 */         Log.w("Statistics", "Got exception converting an UserData to JSON", e);
/*     */       }
/*     */     }
/*     */ 
/* 192 */     return json;
/*     */   }
/*     */ 
/*     */   static void fromJSON(JSONObject json)
/*     */   {
/* 200 */     if (json != null) {
/* 201 */       name = json.optString("name", null);
/* 202 */       username = json.optString("username", null);
/* 203 */       email = json.optString("email", null);
/* 204 */       org = json.optString("organization", null);
/* 205 */       phone = json.optString("phone", null);
/* 206 */       picture = json.optString("picture", null);
/* 207 */       gender = json.optString("gender", null);
/* 208 */       byear = json.optInt("byear", 0);
/* 209 */       if (!json.isNull("custom"))
/*     */         try
/*     */         {
/* 212 */           JSONObject customJson = json.getJSONObject("custom");
/* 213 */           HashMap custom = new HashMap(customJson.length());
/* 214 */           Iterator nameItr = customJson.keys();
/* 215 */           while (nameItr.hasNext()) {
/* 216 */             String key = (String)nameItr.next();
/* 217 */             if (!customJson.isNull(key))
/* 218 */               custom.put(key, customJson.getString(key));
/*     */           }
/*     */         }
/*     */         catch (JSONException e) {
/* 222 */           if (Statistics.sharedInstance().isLoggingEnabled())
/* 223 */             Log.w("Statistics", "Got exception converting an Custom Json to Custom User data", e);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getPicturePathFromQuery(URL url)
/*     */   {
/* 232 */     String query = url.getQuery();
/* 233 */     if (query == null) {
/* 234 */       return "";
/*     */     }
/* 236 */     String[] pairs = query.split("&");
/* 237 */     String ret = "";
/* 238 */     if (url.getQuery().contains("picturePath")) {
/* 239 */       for (String pair : pairs) {
/* 240 */         int idx = pair.indexOf("=");
/* 241 */         if (!pair.substring(0, idx).equals("picturePath")) continue;
/*     */         try {
/* 243 */           ret = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
/*     */         } catch (UnsupportedEncodingException e) {
/* 245 */           ret = "";
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 251 */     return ret;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.statistics.UserData
 * JD-Core Version:    0.6.0
 */
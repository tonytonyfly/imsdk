/*    */ package io.rong.push.core;
/*    */ 
/*    */ import android.os.Bundle;
/*    */ import android.text.TextUtils;
/*    */ import org.json.JSONException;
/*    */ import org.json.JSONObject;
/*    */ 
/*    */ public class PushUtils
/*    */ {
/*    */   public static Bundle decode(String str)
/*    */     throws JSONException
/*    */   {
/* 21 */     Bundle bundle = null;
/*    */     try {
/* 23 */       JSONObject json = new JSONObject(str);
/* 24 */       bundle = new Bundle();
/* 25 */       bundle.putLong("receivedTime", json.optLong("timestamp"));
/* 26 */       bundle.putString("objectName", json.optString("objectName"));
/* 27 */       bundle.putString("senderId", json.optString("fromUserId"));
/* 28 */       bundle.putString("senderName", json.optString("fromUserName"));
/* 29 */       bundle.putString("senderUri", json.optString("fromUserPo"));
/* 30 */       bundle.putString("pushTitle", json.optString("title"));
/* 31 */       bundle.putString("pushContent", json.optString("content"));
/* 32 */       bundle.putString("pushData", json.optString("appData"));
/*    */ 
/* 34 */       String channelType = json.optString("channelType");
/* 35 */       int conversationType = 0;
/* 36 */       if (!TextUtils.isEmpty(channelType)) {
/*    */         try {
/* 38 */           conversationType = Integer.parseInt(channelType);
/*    */         } catch (NumberFormatException e) {
/* 40 */           e.printStackTrace();
/*    */         }
/*    */       }
/* 43 */       bundle.putInt("conversationType", conversationType);
/*    */ 
/* 46 */       if ((conversationType == 2) || (conversationType == 3) || (conversationType == 4)) {
/* 47 */         bundle.putString("targetId", json.optString("channelId"));
/* 48 */         bundle.putString("targetUserName", json.optString("channelName"));
/*    */       } else {
/* 50 */         bundle.putString("targetId", json.optString("fromUserId"));
/* 51 */         bundle.putString("targetUserName", json.optString("fromUserName"));
/*    */       }
/*    */ 
/* 55 */       bundle.putString("packageName", json.optString("packageName"));
/*    */ 
/* 57 */       JSONObject temp = json.getJSONObject("rc");
/* 58 */       bundle.putString("toId", temp.optString("tId"));
/* 59 */       bundle.putString("pushId", temp.optString("id"));
/* 60 */       if ((temp.has("ext")) && (temp.getJSONObject("ext") != null))
/* 61 */         bundle.putString("extra", temp.getJSONObject("ext").toString());
/*    */     }
/*    */     catch (JSONException e) {
/* 64 */       e.printStackTrace();
/* 65 */       throw new JSONException("decode failed!");
/*    */     }
/* 67 */     return bundle;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.core.PushUtils
 * JD-Core Version:    0.6.0
 */
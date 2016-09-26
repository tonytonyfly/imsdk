/*     */ package io.rong.imlib.model;
/*     */ 
/*     */ import android.net.Uri;
/*     */ import android.os.Parcelable;
/*     */ import android.text.TextUtils;
/*     */ import io.rong.common.RLog;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONObject;
/*     */ 
/*     */ public abstract class MessageContent
/*     */   implements Parcelable
/*     */ {
/*     */   private static final String TAG = "MessageContent";
/*     */   private UserInfo userInfo;
/*     */   private MentionedInfo mentionedInfo;
/*     */ 
/*     */   protected MessageContent()
/*     */   {
/*     */   }
/*     */ 
/*     */   public MessageContent(byte[] data)
/*     */   {
/*     */   }
/*     */ 
/*     */   public UserInfo getUserInfo()
/*     */   {
/*  43 */     return this.userInfo;
/*     */   }
/*     */ 
/*     */   public void setUserInfo(UserInfo info) {
/*  47 */     this.userInfo = info;
/*     */   }
/*     */ 
/*     */   public MentionedInfo getMentionedInfo() {
/*  51 */     return this.mentionedInfo;
/*     */   }
/*     */ 
/*     */   public void setMentionedInfo(MentionedInfo info) {
/*  55 */     this.mentionedInfo = info;
/*     */   }
/*     */ 
/*     */   public JSONObject getJSONUserInfo()
/*     */   {
/*  61 */     if ((getUserInfo() == null) || (getUserInfo().getUserId() == null)) {
/*  62 */       return null;
/*     */     }
/*  64 */     JSONObject jsonObject = new JSONObject();
/*     */     try {
/*  66 */       jsonObject.put("id", getUserInfo().getUserId());
/*  67 */       if (!TextUtils.isEmpty(getUserInfo().getName()))
/*  68 */         jsonObject.put("name", getUserInfo().getName());
/*  69 */       if (getUserInfo().getPortraitUri() != null)
/*  70 */         jsonObject.put("portrait", getUserInfo().getPortraitUri());
/*     */     } catch (JSONException e) {
/*  72 */       RLog.e("MessageContent", "JSONException " + e.getMessage());
/*     */     }
/*  74 */     return jsonObject;
/*     */   }
/*     */ 
/*     */   public UserInfo parseJsonToUserInfo(JSONObject jsonObj) {
/*  78 */     UserInfo info = null;
/*  79 */     String id = jsonObj.optString("id");
/*  80 */     String name = jsonObj.optString("name");
/*  81 */     String icon = jsonObj.optString("portrait");
/*     */ 
/*  83 */     if (TextUtils.isEmpty(icon)) {
/*  84 */       icon = jsonObj.optString("icon");
/*     */     }
/*     */ 
/*  87 */     if ((!TextUtils.isEmpty(id)) && (!TextUtils.isEmpty(name))) {
/*  88 */       Uri portrait = icon != null ? Uri.parse(icon) : null;
/*  89 */       info = new UserInfo(id, name, portrait);
/*     */     }
/*  91 */     return info;
/*     */   }
/*     */ 
/*     */   public JSONObject getJsonMentionInfo() {
/*  95 */     if (getMentionedInfo() == null)
/*  96 */       return null;
/*  97 */     JSONObject jsonObject = new JSONObject();
/*     */     try {
/*  99 */       jsonObject.put("type", getMentionedInfo().getType().getValue());
/* 100 */       if (getMentionedInfo().getMentionedUserIdList() == null) {
/* 101 */         jsonObject.put("userIdList", null);
/*     */       } else {
/* 103 */         JSONArray jsonArray = new JSONArray();
/* 104 */         for (String userId : getMentionedInfo().getMentionedUserIdList()) {
/* 105 */           jsonArray.put(userId);
/*     */         }
/* 107 */         jsonObject.put("userIdList", jsonArray);
/*     */       }
/* 109 */       jsonObject.put("mentionedContent", getMentionedInfo().getMentionedContent());
/*     */     } catch (JSONException e) {
/* 111 */       RLog.e("MessageContent", "JSONException " + e.getMessage());
/*     */     }
/* 113 */     return jsonObject;
/*     */   }
/*     */ 
/*     */   public MentionedInfo parseJsonToMentionInfo(JSONObject jsonObject)
/*     */   {
/* 119 */     MentionedInfo.MentionedType type = MentionedInfo.MentionedType.valueOf(jsonObject.optInt("type"));
/* 120 */     JSONArray userList = jsonObject.optJSONArray("userIdList");
/* 121 */     String mentionContent = jsonObject.optString("mentionedContent");
/*     */     MentionedInfo mentionedInfo;
/*     */     MentionedInfo mentionedInfo;
/* 122 */     if (type.equals(MentionedInfo.MentionedType.ALL)) {
/* 123 */       mentionedInfo = new MentionedInfo(type, null, mentionContent);
/*     */     } else {
/* 125 */       List list = new ArrayList();
/*     */       try {
/* 127 */         for (int i = 0; i < userList.length(); i++)
/* 128 */           list.add((String)userList.get(i));
/*     */       }
/*     */       catch (JSONException e) {
/* 131 */         e.printStackTrace();
/*     */       }
/*     */ 
/* 134 */       mentionedInfo = new MentionedInfo(type, list, mentionContent);
/*     */     }
/* 136 */     return mentionedInfo;
/*     */   }
/*     */ 
/*     */   public abstract byte[] encode();
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.MessageContent
 * JD-Core Version:    0.6.0
 */
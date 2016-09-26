/*     */ package io.rong.imkit.userInfoCache;
/*     */ 
/*     */ import android.content.ContentValues;
/*     */ import android.content.Context;
/*     */ import android.database.Cursor;
/*     */ import android.database.sqlite.SQLiteDatabase;
/*     */ import android.database.sqlite.SQLiteException;
/*     */ import android.net.Uri;
/*     */ import io.rong.common.RLog;
/*     */ import io.rong.imkit.model.GroupUserInfo;
/*     */ import io.rong.imlib.model.Discussion;
/*     */ import io.rong.imlib.model.Group;
/*     */ import io.rong.imlib.model.UserInfo;
/*     */ 
/*     */ class RongDatabaseDao
/*     */ {
/*     */   private static final String TAG = "RongDatabaseDao";
/*     */   private RongUserCacheDatabaseHelper rongUserCacheDatabaseHelper;
/*     */   private SQLiteDatabase db;
/*  20 */   private final String usersTable = "users";
/*  21 */   private final String groupUsersTable = "group_users";
/*  22 */   private final String groupsTable = "groups";
/*  23 */   private final String discussionsTable = "discussions";
/*     */ 
/*     */   void open(Context context, String appKey, String currentUserId)
/*     */   {
/*  30 */     RongUserCacheDatabaseHelper.setDbPath(context, appKey, currentUserId);
/*     */     try {
/*  32 */       this.rongUserCacheDatabaseHelper = new RongUserCacheDatabaseHelper(context);
/*  33 */       this.db = this.rongUserCacheDatabaseHelper.getReadableDatabase();
/*     */     } catch (SQLiteException e) {
/*  35 */       RLog.e("RongDatabaseDao", "SQLiteException occur");
/*  36 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   void close() {
/*  41 */     if (this.db != null) {
/*  42 */       this.db.close();
/*  43 */       this.db = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void finalize() throws Throwable
/*     */   {
/*  49 */     if (this.db != null) {
/*  50 */       this.db.close();
/*     */     }
/*  52 */     super.finalize();
/*     */   }
/*     */ 
/*     */   UserInfo getUserInfo(String userId) {
/*  56 */     if (userId == null) {
/*  57 */       RLog.w("RongDatabaseDao", "getUserInfo userId is invalid");
/*  58 */       return null;
/*     */     }
/*  60 */     if (this.db == null) {
/*  61 */       RLog.w("RongDatabaseDao", "getUserInfo db is invalid");
/*  62 */       return null;
/*     */     }
/*     */ 
/*  65 */     Cursor c = this.db.query("users", null, "id = ?", new String[] { userId }, null, null, null);
/*  66 */     UserInfo info = null;
/*  67 */     if (c.moveToFirst()) {
/*  68 */       String id = c.getString(c.getColumnIndex("id"));
/*  69 */       String name = c.getString(c.getColumnIndex("name"));
/*  70 */       String portrait = c.getString(c.getColumnIndex("portrait"));
/*  71 */       info = new UserInfo(id, name, Uri.parse(portrait));
/*     */     }
/*  73 */     c.close();
/*  74 */     return info;
/*     */   }
/*     */ 
/*     */   synchronized void insertUserInfo(UserInfo userInfo) {
/*  78 */     if ((userInfo == null) || (userInfo.getUserId() == null)) {
/*  79 */       RLog.w("RongDatabaseDao", "insertUserInfo userId is invalid");
/*  80 */       return;
/*     */     }
/*  82 */     if (this.db == null) {
/*  83 */       RLog.w("RongDatabaseDao", "insertUserInfo db is invalid");
/*  84 */       return;
/*     */     }
/*     */ 
/*  87 */     ContentValues cv = new ContentValues();
/*  88 */     cv.put("id", userInfo.getUserId());
/*  89 */     cv.put("name", userInfo.getName());
/*  90 */     cv.put("portrait", userInfo.getPortraitUri() + "");
/*  91 */     this.db.insert("users", null, cv);
/*     */   }
/*     */ 
/*     */   synchronized void updateUserInfo(UserInfo userInfo) {
/*  95 */     if ((userInfo == null) || (userInfo.getUserId() == null)) {
/*  96 */       RLog.w("RongDatabaseDao", "updateUserInfo userId is invalid");
/*  97 */       return;
/*     */     }
/*  99 */     if (this.db == null) {
/* 100 */       RLog.w("RongDatabaseDao", "updateUserInfo db is invalid");
/* 101 */       return;
/*     */     }
/*     */ 
/* 104 */     ContentValues cv = new ContentValues();
/* 105 */     cv.put("id", userInfo.getUserId());
/* 106 */     cv.put("name", userInfo.getName());
/* 107 */     cv.put("portrait", userInfo.getPortraitUri() + "");
/* 108 */     this.db.update("users", cv, "id = ?", new String[] { userInfo.getUserId() });
/*     */   }
/*     */ 
/*     */   synchronized void putUserInfo(UserInfo userInfo) {
/* 112 */     if ((userInfo == null) || (userInfo.getUserId() == null)) {
/* 113 */       RLog.w("RongDatabaseDao", "putUserInfo userId is invalid");
/* 114 */       return;
/*     */     }
/* 116 */     if (this.db == null) {
/* 117 */       RLog.w("RongDatabaseDao", "putUserInfo db is invalid");
/* 118 */       return;
/*     */     }
/*     */ 
/* 121 */     this.db.execSQL("replace into users (id, name, portrait) values (?, ?, ?)", new String[] { userInfo.getUserId(), userInfo.getName(), userInfo.getPortraitUri() + "" });
/*     */   }
/*     */ 
/*     */   GroupUserInfo getGroupUserInfo(String groupId, String userId) {
/* 125 */     if ((userId == null) || (groupId == null)) {
/* 126 */       RLog.w("RongDatabaseDao", "getGroupUserInfo parameter is invalid");
/* 127 */       return null;
/*     */     }
/* 129 */     if (this.db == null) {
/* 130 */       RLog.w("RongDatabaseDao", "getGroupUserInfo db is invalid");
/* 131 */       return null;
/*     */     }
/*     */ 
/* 134 */     Cursor c = this.db.query("group_users", null, "group_id = ? and user_id = ?", new String[] { groupId, userId }, null, null, null);
/* 135 */     GroupUserInfo info = null;
/* 136 */     if (c.moveToFirst()) {
/* 137 */       String gId = c.getString(c.getColumnIndex("group_id"));
/* 138 */       String uId = c.getString(c.getColumnIndex("user_id"));
/* 139 */       String nickname = c.getString(c.getColumnIndex("nickname"));
/* 140 */       info = new GroupUserInfo(gId, uId, nickname);
/*     */     }
/* 142 */     c.close();
/* 143 */     return info;
/*     */   }
/*     */ 
/*     */   synchronized void insertGroupUserInfo(GroupUserInfo userInfo) {
/* 147 */     if ((userInfo == null) || (userInfo.getGroupId() == null) || (userInfo.getUserId() == null)) {
/* 148 */       RLog.w("RongDatabaseDao", "insertGroupUserInfo parameter is invalid");
/* 149 */       return;
/*     */     }
/* 151 */     if (this.db == null) {
/* 152 */       RLog.w("RongDatabaseDao", "insertGroupUserInfo db is invalid");
/* 153 */       return;
/*     */     }
/*     */ 
/* 156 */     ContentValues cv = new ContentValues();
/* 157 */     cv.put("group_id", userInfo.getGroupId());
/* 158 */     cv.put("user_id", userInfo.getUserId());
/* 159 */     cv.put("nickname", userInfo.getNickname());
/* 160 */     this.db.insert("group_users", null, cv);
/*     */   }
/*     */ 
/*     */   synchronized void updateGroupUserInfo(GroupUserInfo userInfo) {
/* 164 */     if ((userInfo == null) || (userInfo.getGroupId() == null) || (userInfo.getUserId() == null)) {
/* 165 */       RLog.w("RongDatabaseDao", "updateGroupUserInfo parameter is invalid");
/* 166 */       return;
/*     */     }
/* 168 */     if (this.db == null) {
/* 169 */       RLog.w("RongDatabaseDao", "updateGroupUserInfo db is invalid");
/* 170 */       return;
/*     */     }
/*     */ 
/* 173 */     ContentValues cv = new ContentValues();
/* 174 */     cv.put("group_id", userInfo.getGroupId());
/* 175 */     cv.put("user_id", userInfo.getUserId());
/* 176 */     cv.put("nickname", userInfo.getNickname());
/* 177 */     this.db.update("group_users", cv, "group_id=? and user_id=?", new String[] { userInfo.getGroupId(), userInfo.getUserId() });
/*     */   }
/*     */ 
/*     */   synchronized void putGroupUserInfo(GroupUserInfo userInfo) {
/* 181 */     if ((userInfo == null) || (userInfo.getGroupId() == null) || (userInfo.getUserId() == null)) {
/* 182 */       RLog.w("RongDatabaseDao", "putGroupUserInfo parameter is invalid");
/* 183 */       return;
/*     */     }
/* 185 */     if (this.db == null) {
/* 186 */       RLog.w("RongDatabaseDao", "putGroupUserInfo db is invalid");
/* 187 */       return;
/*     */     }
/*     */ 
/* 190 */     this.db.execSQL("delete from group_users where group_id=? and user_id=?", new String[] { userInfo.getGroupId(), userInfo.getUserId() });
/* 191 */     this.db.execSQL("insert into group_users (group_id, user_id, nickname) values (?, ?, ?)", new String[] { userInfo.getGroupId(), userInfo.getUserId(), userInfo.getNickname() });
/*     */   }
/*     */ 
/*     */   Group getGroupInfo(String groupId) {
/* 195 */     if (groupId == null) {
/* 196 */       RLog.w("RongDatabaseDao", "getGroupInfo parameter is invalid");
/* 197 */       return null;
/*     */     }
/* 199 */     if (this.db == null) {
/* 200 */       RLog.w("RongDatabaseDao", "getGroupInfo db is invalid");
/* 201 */       return null;
/*     */     }
/*     */ 
/* 204 */     Cursor c = this.db.query("groups", null, "id = ?", new String[] { groupId }, null, null, null);
/* 205 */     Group group = null;
/* 206 */     if (c.moveToFirst()) {
/* 207 */       String id = c.getString(c.getColumnIndex("id"));
/* 208 */       String name = c.getString(c.getColumnIndex("name"));
/* 209 */       String portrait = c.getString(c.getColumnIndex("portrait"));
/* 210 */       group = new Group(id, name, Uri.parse(portrait));
/*     */     }
/* 212 */     c.close();
/*     */ 
/* 214 */     return group;
/*     */   }
/*     */ 
/*     */   synchronized void insertGroupInfo(Group group) {
/* 218 */     if ((group == null) || (group.getId() == null)) {
/* 219 */       RLog.w("RongDatabaseDao", "insertGroupInfo parameter is invalid");
/* 220 */       return;
/*     */     }
/* 222 */     if (this.db == null) {
/* 223 */       RLog.w("RongDatabaseDao", "insertGroupInfo db is invalid");
/* 224 */       return;
/*     */     }
/*     */ 
/* 227 */     ContentValues cv = new ContentValues();
/* 228 */     cv.put("id", group.getId());
/* 229 */     cv.put("name", group.getName());
/* 230 */     cv.put("portrait", group.getPortraitUri() + "");
/* 231 */     this.db.insert("groups", null, cv);
/*     */   }
/*     */ 
/*     */   synchronized void updateGroupInfo(Group group) {
/* 235 */     if ((group == null) || (group.getId() == null)) {
/* 236 */       RLog.w("RongDatabaseDao", "updateGroupInfo parameter is invalid");
/* 237 */       return;
/*     */     }
/* 239 */     if (this.db == null) {
/* 240 */       RLog.w("RongDatabaseDao", "updateGroupInfo db is invalid");
/* 241 */       return;
/*     */     }
/*     */ 
/* 244 */     ContentValues cv = new ContentValues();
/* 245 */     cv.put("id", group.getId());
/* 246 */     cv.put("name", group.getName());
/* 247 */     cv.put("portrait", group.getPortraitUri() + "");
/* 248 */     this.db.update("groups", cv, "id = ?", new String[] { group.getId() });
/*     */   }
/*     */ 
/*     */   synchronized void putGroupInfo(Group group) {
/* 252 */     if ((group == null) || (group.getId() == null)) {
/* 253 */       RLog.w("RongDatabaseDao", "putGroupInfo parameter is invalid");
/* 254 */       return;
/*     */     }
/* 256 */     if (this.db == null) {
/* 257 */       RLog.w("RongDatabaseDao", "putGroupInfo db is invalid");
/* 258 */       return;
/*     */     }
/*     */ 
/* 261 */     this.db.execSQL("replace into groups (id, name, portrait) values (?, ?, ?)", new String[] { group.getId(), group.getName(), group.getPortraitUri() + "" });
/*     */   }
/*     */ 
/*     */   Discussion getDiscussionInfo(String discussionId) {
/* 265 */     if (discussionId == null) {
/* 266 */       RLog.w("RongDatabaseDao", "getDiscussionInfo parameter is invalid");
/* 267 */       return null;
/*     */     }
/* 269 */     if (this.db == null) {
/* 270 */       RLog.w("RongDatabaseDao", "getDiscussionInfo db is invalid");
/* 271 */       return null;
/*     */     }
/*     */ 
/* 274 */     Cursor c = this.db.query("discussions", null, "id = ?", new String[] { discussionId }, null, null, null);
/* 275 */     Discussion discussion = null;
/* 276 */     if (c.moveToFirst()) {
/* 277 */       String id = c.getString(c.getColumnIndex("id"));
/* 278 */       String name = c.getString(c.getColumnIndex("name"));
/*     */ 
/* 280 */       discussion = new Discussion(id, name);
/*     */     }
/* 282 */     c.close();
/*     */ 
/* 284 */     return discussion;
/*     */   }
/*     */ 
/*     */   synchronized void insertDiscussionInfo(Discussion discussion) {
/* 288 */     if ((discussion == null) || (discussion.getId() == null)) {
/* 289 */       RLog.w("RongDatabaseDao", "insertDiscussionInfo parameter is invalid");
/* 290 */       return;
/*     */     }
/* 292 */     if (this.db == null) {
/* 293 */       RLog.w("RongDatabaseDao", "insertDiscussionInfo db is invalid");
/* 294 */       return;
/*     */     }
/*     */ 
/* 297 */     ContentValues cv = new ContentValues();
/* 298 */     cv.put("id", discussion.getId());
/* 299 */     cv.put("name", discussion.getName());
/* 300 */     cv.put("portrait", "");
/* 301 */     this.db.insert("discussions", null, cv);
/*     */   }
/*     */ 
/*     */   synchronized void updateDiscussionInfo(Discussion discussion) {
/* 305 */     if ((discussion == null) || (discussion.getId() == null)) {
/* 306 */       RLog.w("RongDatabaseDao", "updateDiscussionInfo parameter is invalid");
/* 307 */       return;
/*     */     }
/* 309 */     if (this.db == null) {
/* 310 */       RLog.w("RongDatabaseDao", "updateDiscussionInfo db is invalid");
/* 311 */       return;
/*     */     }
/*     */ 
/* 314 */     ContentValues cv = new ContentValues();
/* 315 */     cv.put("id", discussion.getId());
/* 316 */     cv.put("name", discussion.getName());
/* 317 */     cv.put("portrait", "");
/* 318 */     this.db.update("discussions", cv, "id = ?", new String[] { discussion.getId() });
/*     */   }
/*     */ 
/*     */   synchronized void putDiscussionInfo(Discussion discussion) {
/* 322 */     if ((discussion == null) || (discussion.getId() == null)) {
/* 323 */       RLog.w("RongDatabaseDao", "putDiscussionInfo parameter is invalid");
/* 324 */       return;
/*     */     }
/* 326 */     if (this.db == null) {
/* 327 */       RLog.w("RongDatabaseDao", "putDiscussionInfo db is invalid");
/* 328 */       return;
/*     */     }
/*     */ 
/* 331 */     this.db.execSQL("replace into discussions (id, name, portrait) values (?, ?, ?)", new String[] { discussion.getId(), discussion.getName(), "" });
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.userInfoCache.RongDatabaseDao
 * JD-Core Version:    0.6.0
 */
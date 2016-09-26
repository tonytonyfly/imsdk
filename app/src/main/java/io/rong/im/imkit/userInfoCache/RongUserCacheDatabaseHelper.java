/*    */ package io.rong.imkit.userInfoCache;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.database.sqlite.SQLiteDatabase;
/*    */ import android.database.sqlite.SQLiteDatabase.CursorFactory;
/*    */ import android.database.sqlite.SQLiteOpenHelper;
/*    */ import java.io.File;
/*    */ 
/*    */ class RongUserCacheDatabaseHelper extends SQLiteOpenHelper
/*    */ {
/*    */   private static final String DB_NAME = "IMKitUserInfoCache";
/*    */   private static final int DB_VERSION = 1;
/*    */   private static String dbPath;
/*    */   private SQLiteDatabase database;
/*    */ 
/*    */   public RongUserCacheDatabaseHelper(Context context)
/*    */   {
/* 18 */     this(context, "IMKitUserInfoCache", null, 1);
/*    */   }
/*    */ 
/*    */   private RongUserCacheDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
/* 22 */     super(new RongDatabaseContext(context, dbPath), name, factory, version);
/*    */   }
/*    */ 
/*    */   public static void setDbPath(Context context, String appKey, String currentUserId) {
/* 26 */     dbPath = context.getFilesDir().getAbsolutePath();
/* 27 */     dbPath = dbPath + File.separator + appKey + File.separator + currentUserId;
/*    */   }
/*    */ 
/*    */   public void onCreate(SQLiteDatabase db)
/*    */   {
/* 32 */     this.database = db;
/*    */ 
/* 34 */     db.execSQL("CREATE TABLE users (id TEXT PRIMARY KEY NOT NULL UNIQUE, name TEXT, portrait TEXT)");
/* 35 */     db.execSQL("CREATE INDEX IF NOT EXISTS id_idx_users ON users(id)");
/*    */ 
/* 37 */     db.execSQL("CREATE TABLE group_users (group_id TEXT NOT NULL, user_id TEXT NOT NULL, nickname TEXT)");
/* 38 */     db.execSQL("CREATE TABLE groups (id TEXT PRIMARY KEY NOT NULL UNIQUE, name TEXT, portrait TEXT)");
/* 39 */     db.execSQL("CREATE TABLE discussions (id TEXT PRIMARY KEY NOT NULL UNIQUE, name TEXT, portrait TEXT)");
/*    */   }
/*    */ 
/*    */   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.userInfoCache.RongUserCacheDatabaseHelper
 * JD-Core Version:    0.6.0
 */
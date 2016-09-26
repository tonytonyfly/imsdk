/*    */ package io.rong.imkit.userInfoCache;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.content.ContextWrapper;
/*    */ import android.database.DatabaseErrorHandler;
/*    */ import android.database.sqlite.SQLiteDatabase;
/*    */ import android.database.sqlite.SQLiteDatabase.CursorFactory;
/*    */ import java.io.File;
/*    */ 
/*    */ public class RongDatabaseContext extends ContextWrapper
/*    */ {
/*    */   private String mDirPath;
/*    */ 
/*    */   public RongDatabaseContext(Context context, String dirPath)
/*    */   {
/* 18 */     super(context);
/* 19 */     this.mDirPath = dirPath;
/*    */   }
/*    */ 
/*    */   public File getDatabasePath(String name)
/*    */   {
/* 24 */     File result = new File(this.mDirPath + File.separator + name);
/*    */ 
/* 26 */     if (!result.getParentFile().exists()) {
/* 27 */       result.getParentFile().mkdirs();
/*    */     }
/* 29 */     return result;
/*    */   }
/*    */ 
/*    */   public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory)
/*    */   {
/* 34 */     return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
/*    */   }
/*    */ 
/*    */   public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler)
/*    */   {
/* 39 */     return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name).getAbsolutePath(), factory, errorHandler);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.userInfoCache.RongDatabaseContext
 * JD-Core Version:    0.6.0
 */
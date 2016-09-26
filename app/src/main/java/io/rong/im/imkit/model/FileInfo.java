/*    */ package io.rong.imkit.model;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class FileInfo
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = -4830812821556630987L;
/*    */   String fileName;
/*    */   String filePath;
/*    */   long fileSize;
/*    */   boolean isDirectory;
/*    */   String suffix;
/*    */ 
/*    */   public boolean isDirectory()
/*    */   {
/* 18 */     return this.isDirectory;
/*    */   }
/*    */ 
/*    */   public String getSuffix() {
/* 22 */     return this.suffix;
/*    */   }
/*    */ 
/*    */   public void setSuffix(String suffix) {
/* 26 */     this.suffix = suffix;
/*    */   }
/*    */ 
/*    */   public void setDirectory(boolean directory) {
/* 30 */     this.isDirectory = directory;
/*    */   }
/*    */ 
/*    */   public String getFileName() {
/* 34 */     return this.fileName;
/*    */   }
/*    */ 
/*    */   public void setFileName(String fileName) {
/* 38 */     this.fileName = fileName;
/*    */   }
/*    */ 
/*    */   public String getFilePath() {
/* 42 */     return this.filePath;
/*    */   }
/*    */ 
/*    */   public void setFilePath(String filePath) {
/* 46 */     this.filePath = filePath;
/*    */   }
/*    */ 
/*    */   public long getFileSize() {
/* 50 */     return this.fileSize;
/*    */   }
/*    */ 
/*    */   public void setFileSize(long fileSize) {
/* 54 */     this.fileSize = fileSize;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.model.FileInfo
 * JD-Core Version:    0.6.0
 */
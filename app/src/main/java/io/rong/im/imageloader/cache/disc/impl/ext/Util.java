/*    */ package io.rong.imageloader.cache.disc.impl.ext;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.io.Reader;
/*    */ import java.io.StringWriter;
/*    */ import java.nio.charset.Charset;
/*    */ 
/*    */ final class Util
/*    */ {
/* 27 */   static final Charset US_ASCII = Charset.forName("US-ASCII");
/* 28 */   static final Charset UTF_8 = Charset.forName("UTF-8");
/*    */ 
/*    */   static String readFully(Reader reader)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 35 */       StringWriter writer = new StringWriter();
/* 36 */       char[] buffer = new char[1024];
/*    */       int count;
/* 38 */       while ((count = reader.read(buffer)) != -1) {
/* 39 */         writer.write(buffer, 0, count);
/*    */       }
/* 41 */       String str = writer.toString();
/*    */       return str; } finally { reader.close(); } throw localObject;
/*    */   }
/*    */ 
/*    */   static void deleteContents(File dir)
/*    */     throws IOException
/*    */   {
/* 52 */     File[] files = dir.listFiles();
/* 53 */     if (files == null) {
/* 54 */       throw new IOException("not a readable directory: " + dir);
/*    */     }
/* 56 */     for (File file : files) {
/* 57 */       if (file.isDirectory()) {
/* 58 */         deleteContents(file);
/*    */       }
/* 60 */       if (!file.delete())
/* 61 */         throw new IOException("failed to delete file: " + file);
/*    */     }
/*    */   }
/*    */ 
/*    */   static void closeQuietly(Closeable closeable)
/*    */   {
/* 67 */     if (closeable != null)
/*    */       try {
/* 69 */         closeable.close();
/*    */       } catch (RuntimeException rethrown) {
/* 71 */         throw rethrown;
/*    */       }
/*    */       catch (Exception ignored)
/*    */       {
/*    */       }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.disc.impl.ext.Util
 * JD-Core Version:    0.6.0
 */
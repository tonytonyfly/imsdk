/*    */ package io.rong.imageloader.core.assist;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class ContentLengthInputStream extends InputStream
/*    */ {
/*    */   private final InputStream stream;
/*    */   private final int length;
/*    */ 
/*    */   public ContentLengthInputStream(InputStream stream, int length)
/*    */   {
/* 34 */     this.stream = stream;
/* 35 */     this.length = length;
/*    */   }
/*    */ 
/*    */   public int available()
/*    */   {
/* 40 */     return this.length;
/*    */   }
/*    */ 
/*    */   public void close() throws IOException
/*    */   {
/* 45 */     this.stream.close();
/*    */   }
/*    */ 
/*    */   public void mark(int readLimit)
/*    */   {
/* 50 */     this.stream.mark(readLimit);
/*    */   }
/*    */ 
/*    */   public int read() throws IOException
/*    */   {
/* 55 */     return this.stream.read();
/*    */   }
/*    */ 
/*    */   public int read(byte[] buffer) throws IOException
/*    */   {
/* 60 */     return this.stream.read(buffer);
/*    */   }
/*    */ 
/*    */   public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException
/*    */   {
/* 65 */     return this.stream.read(buffer, byteOffset, byteCount);
/*    */   }
/*    */ 
/*    */   public void reset() throws IOException
/*    */   {
/* 70 */     this.stream.reset();
/*    */   }
/*    */ 
/*    */   public long skip(long byteCount) throws IOException
/*    */   {
/* 75 */     return this.stream.skip(byteCount);
/*    */   }
/*    */ 
/*    */   public boolean markSupported()
/*    */   {
/* 80 */     return this.stream.markSupported();
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.assist.ContentLengthInputStream
 * JD-Core Version:    0.6.0
 */
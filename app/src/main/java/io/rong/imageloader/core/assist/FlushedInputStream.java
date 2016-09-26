/*    */ package io.rong.imageloader.core.assist;
/*    */ 
/*    */ import java.io.FilterInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class FlushedInputStream extends FilterInputStream
/*    */ {
/*    */   public FlushedInputStream(InputStream inputStream)
/*    */   {
/* 14 */     super(inputStream);
/*    */   }
/*    */ 
/*    */   public long skip(long n) throws IOException
/*    */   {
/* 19 */     long totalBytesSkipped = 0L;
/* 20 */     while (totalBytesSkipped < n) {
/* 21 */       long bytesSkipped = this.in.skip(n - totalBytesSkipped);
/* 22 */       if (bytesSkipped == 0L) {
/* 23 */         int by_te = read();
/* 24 */         if (by_te < 0) {
/*    */           break;
/*    */         }
/* 27 */         bytesSkipped = 1L;
/*    */       }
/*    */ 
/* 30 */       totalBytesSkipped += bytesSkipped;
/*    */     }
/* 32 */     return totalBytesSkipped;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.assist.FlushedInputStream
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.imageloader.cache.disc.impl.ext;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.Closeable;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.Charset;
/*     */ 
/*     */ class StrictLineReader
/*     */   implements Closeable
/*     */ {
/*     */   private static final byte CR = 13;
/*     */   private static final byte LF = 10;
/*     */   private final InputStream in;
/*     */   private final Charset charset;
/*     */   private byte[] buf;
/*     */   private int pos;
/*     */   private int end;
/*     */ 
/*     */   public StrictLineReader(InputStream in, Charset charset)
/*     */   {
/*  71 */     this(in, 8192, charset);
/*     */   }
/*     */ 
/*     */   public StrictLineReader(InputStream in, int capacity, Charset charset)
/*     */   {
/*  86 */     if ((in == null) || (charset == null)) {
/*  87 */       throw new NullPointerException();
/*     */     }
/*  89 */     if (capacity < 0) {
/*  90 */       throw new IllegalArgumentException("capacity <= 0");
/*     */     }
/*  92 */     if (!charset.equals(Util.US_ASCII)) {
/*  93 */       throw new IllegalArgumentException("Unsupported encoding");
/*     */     }
/*     */ 
/*  96 */     this.in = in;
/*  97 */     this.charset = charset;
/*  98 */     this.buf = new byte[capacity];
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 108 */     synchronized (this.in) {
/* 109 */       if (this.buf != null) {
/* 110 */         this.buf = null;
/* 111 */         this.in.close();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String readLine()
/*     */     throws IOException
/*     */   {
/* 125 */     synchronized (this.in) {
/* 126 */       if (this.buf == null) {
/* 127 */         throw new IOException("LineReader is closed");
/*     */       }
/*     */ 
/* 133 */       if (this.pos >= this.end) {
/* 134 */         fillBuf();
/*     */       }
/*     */ 
/* 137 */       for (int i = this.pos; i != this.end; i++) {
/* 138 */         if (this.buf[i] == 10) {
/* 139 */           int lineEnd = (i != this.pos) && (this.buf[(i - 1)] == 13) ? i - 1 : i;
/* 140 */           String res = new String(this.buf, this.pos, lineEnd - this.pos, this.charset.name());
/* 141 */           this.pos = (i + 1);
/* 142 */           return res;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 147 */       ByteArrayOutputStream out = new ByteArrayOutputStream(this.end - this.pos + 80)
/*     */       {
/*     */         public String toString() {
/* 150 */           int length = (this.count > 0) && (this.buf[(this.count - 1)] == 13) ? this.count - 1 : this.count;
/*     */           try {
/* 152 */             return new String(this.buf, 0, length, StrictLineReader.this.charset.name()); } catch (UnsupportedEncodingException e) {
/*     */           }
/* 154 */           throw new AssertionError(e);
/*     */         }
/*     */       };
/* 160 */       out.write(this.buf, this.pos, this.end - this.pos);
/*     */ 
/* 162 */       this.end = -1;
/* 163 */       fillBuf();
/*     */ 
/* 165 */       for (int i = this.pos; i != this.end; i++)
/* 166 */         if (this.buf[i] == 10) {
/* 167 */           if (i != this.pos) {
/* 168 */             out.write(this.buf, this.pos, i - this.pos);
/*     */           }
/* 170 */           this.pos = (i + 1);
/* 171 */           return out.toString();
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fillBuf()
/*     */     throws IOException
/*     */   {
/* 183 */     int result = this.in.read(this.buf, 0, this.buf.length);
/* 184 */     if (result == -1) {
/* 185 */       throw new EOFException();
/*     */     }
/* 187 */     this.pos = 0;
/* 188 */     this.end = result;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.cache.disc.impl.ext.StrictLineReader
 * JD-Core Version:    0.6.0
 */
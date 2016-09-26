/*    */ package io.rong.imageloader.core.download;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.Locale;
/*    */ 
/*    */ public abstract interface ImageDownloader
/*    */ {
/*    */   public abstract InputStream getStream(String paramString, Object paramObject)
/*    */     throws IOException;
/*    */ 
/*    */   public static enum Scheme
/*    */   {
/* 46 */     HTTP("http"), HTTPS("https"), FILE("file"), CONTENT("content"), ASSETS("assets"), DRAWABLE("drawable"), UNKNOWN("");
/*    */ 
/*    */     private String scheme;
/*    */     private String uriPrefix;
/*    */ 
/* 52 */     private Scheme(String scheme) { this.scheme = scheme;
/* 53 */       this.uriPrefix = (scheme + "://");
/*    */     }
/*    */ 
/*    */     public static Scheme ofUri(String uri)
/*    */     {
/* 63 */       if (uri != null) {
/* 64 */         for (Scheme s : values()) {
/* 65 */           if (s.belongsTo(uri)) {
/* 66 */             return s;
/*    */           }
/*    */         }
/*    */       }
/* 70 */       return UNKNOWN;
/*    */     }
/*    */ 
/*    */     private boolean belongsTo(String uri) {
/* 74 */       return uri.toLowerCase(Locale.US).startsWith(this.uriPrefix);
/*    */     }
/*    */ 
/*    */     public String wrap(String path)
/*    */     {
/* 79 */       return this.uriPrefix + path;
/*    */     }
/*    */ 
/*    */     public String crop(String uri)
/*    */     {
/* 84 */       if (!belongsTo(uri)) {
/* 85 */         throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", new Object[] { uri, this.scheme }));
/*    */       }
/* 87 */       return uri.substring(this.uriPrefix.length());
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.download.ImageDownloader
 * JD-Core Version:    0.6.0
 */
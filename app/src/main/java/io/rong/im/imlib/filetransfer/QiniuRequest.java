/*    */ package io.rong.imlib.filetransfer;
/*    */ 
/*    */ import java.io.File;
/*    */ 
/*    */ public class QiniuRequest extends Request
/*    */ {
/*    */   private static final String Boundary = "--526f6e67436c6f7564";
/*    */ 
/*    */   public QiniuRequest(Configuration config, RequestCallBack requestCallBack)
/*    */   {
/* 12 */     super(config, requestCallBack);
/*    */   }
/*    */ 
/*    */   public String getContentType()
/*    */   {
/* 17 */     return "multipart/form-data; boundary=--526f6e67436c6f7564";
/*    */   }
/*    */ 
/*    */   public long getContentLength()
/*    */   {
/* 22 */     File file = new File(this.url);
/*    */ 
/* 24 */     String end = "\r\n----526f6e67436c6f7564--";
/* 25 */     return getFormData().length() + file.length() + end.length();
/*    */   }
/*    */ 
/*    */   public FtConst.MimeType getMimeType()
/*    */   {
/* 30 */     return this.mimeType;
/*    */   }
/*    */ 
/*    */   public String getBoundary()
/*    */   {
/* 35 */     return "--526f6e67436c6f7564";
/*    */   }
/*    */ 
/*    */   public String getFormData()
/*    */   {
/* 40 */     String formData = "--";
/* 41 */     formData = formData + "--526f6e67436c6f7564";
/* 42 */     formData = formData + "\r\nContent-Disposition: form-data; name=\"token\"\r\n\r\n";
/* 43 */     formData = formData + this.token;
/* 44 */     formData = formData + "\r\n--";
/* 45 */     formData = formData + "--526f6e67436c6f7564";
/* 46 */     formData = formData + "\r\nContent-Disposition: form-data; name=\"key\"\r\n\r\n";
/* 47 */     formData = formData + this.fileName;
/* 48 */     formData = formData + "\r\n--";
/* 49 */     formData = formData + "--526f6e67436c6f7564";
/* 50 */     formData = formData + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"";
/* 51 */     formData = formData + this.fileName;
/* 52 */     formData = formData + "\"\r\nContent-Type: ";
/* 53 */     formData = formData + this.mimeType.getName();
/* 54 */     formData = formData + "\r\n\r\n";
/*    */ 
/* 56 */     return formData;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.filetransfer.QiniuRequest
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.imlib.filetransfer;
/*    */ 
/*    */ public class RequestOption
/*    */ {
/*    */   private int start;
/*    */   private int end;
/*    */   private FtConst.MimeType mimeType;
/*    */   private String serverIp;
/*    */   private RequestCallBack requestCallBack;
/*    */   private String fileName;
/*    */ 
/*    */   public RequestOption(String fileName, FtConst.MimeType mimeType, RequestCallBack requestCallBack)
/*    */   {
/* 20 */     this.fileName = fileName;
/* 21 */     this.mimeType = mimeType;
/* 22 */     this.requestCallBack = requestCallBack;
/*    */   }
/*    */ 
/*    */   public RequestOption(String fileName, FtConst.MimeType mimeType, String serverIp, RequestCallBack requestCallBack) {
/* 26 */     this.fileName = fileName;
/* 27 */     this.mimeType = mimeType;
/* 28 */     this.serverIp = serverIp;
/* 29 */     this.requestCallBack = requestCallBack;
/*    */   }
/*    */ 
/*    */   public RequestOption(int start, int end, FtConst.MimeType mimeType, String serverIp, RequestCallBack requestCallBack) {
/* 33 */     this.start = start;
/* 34 */     this.end = end;
/* 35 */     this.mimeType = mimeType;
/* 36 */     this.serverIp = serverIp;
/* 37 */     this.requestCallBack = requestCallBack;
/*    */   }
/*    */ 
/*    */   public int getStart() {
/* 41 */     return this.start;
/*    */   }
/*    */ 
/*    */   public int getEnd() {
/* 45 */     return this.end;
/*    */   }
/*    */ 
/*    */   public FtConst.MimeType getMimeType() {
/* 49 */     return this.mimeType;
/*    */   }
/*    */ 
/*    */   public String getServerIp() {
/* 53 */     return this.serverIp;
/*    */   }
/*    */ 
/*    */   public RequestCallBack getRequestCallBack() {
/* 57 */     return this.requestCallBack;
/*    */   }
/*    */ 
/*    */   public String getFileName() {
/* 61 */     return this.fileName;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.filetransfer.RequestOption
 * JD-Core Version:    0.6.0
 */
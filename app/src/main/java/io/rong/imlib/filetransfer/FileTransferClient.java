/*    */ package io.rong.imlib.filetransfer;
/*    */ 
/*    */ public class FileTransferClient
/*    */ {
/*    */   private Configuration configuration;
/*    */   private CallDispatcher dispatcher;
/*    */   private static FileTransferClient sInstance;
/*    */ 
/*    */   private FileTransferClient(Configuration config)
/*    */   {
/* 10 */     this.configuration = config;
/* 11 */     this.dispatcher = new CallDispatcher();
/*    */   }
/*    */ 
/*    */   public static void init(Configuration config) {
/* 15 */     sInstance = new FileTransferClient(config);
/*    */   }
/*    */ 
/*    */   public static FileTransferClient getInstance() {
/* 19 */     return sInstance;
/*    */   }
/*    */ 
/*    */   public void upload(String url, String token, RequestOption option) {
/* 23 */     Request request = null;
/* 24 */     if (this.configuration.serviceType == FtConst.ServiceType.QI_NIU) {
/* 25 */       request = new QiniuRequest(this.configuration, option.getRequestCallBack());
/* 26 */       request.token = token;
/* 27 */       request.mimeType = option.getMimeType();
/* 28 */       request.method = "POST";
/* 29 */       request.serverIp = option.getServerIp();
/* 30 */       request.url = url;
/* 31 */       request.tag = url;
/* 32 */       request.fileName = option.getFileName();
/* 33 */       request.requestCallBack = option.getRequestCallBack();
/*    */     }
/* 35 */     Call call = Call.create(this.dispatcher, request);
/* 36 */     call.enqueue();
/*    */   }
/*    */ 
/*    */   public void download(int id, String url, RequestOption option) {
/* 40 */     Request request = null;
/* 41 */     if (this.configuration.serviceType == FtConst.ServiceType.QI_NIU) {
/* 42 */       request = new QiniuRequest(this.configuration, option.getRequestCallBack());
/* 43 */       request.mimeType = option.getMimeType();
/* 44 */       request.method = "GET";
/* 45 */       request.url = url;
/* 46 */       request.tag = Integer.valueOf(id);
/* 47 */       request.fileName = option.getFileName();
/* 48 */       request.requestCallBack = option.getRequestCallBack();
/*    */     }
/* 50 */     Call call = Call.create(this.dispatcher, request);
/* 51 */     call.enqueue();
/*    */   }
/*    */ 
/*    */   public void cancel(int id, CancelCallback callback) {
/* 55 */     if (id > 0)
/* 56 */       this.dispatcher.cancel(Integer.valueOf(id), callback);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.filetransfer.FileTransferClient
 * JD-Core Version:    0.6.0
 */
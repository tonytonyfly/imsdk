/*    */ package io.rong.imlib.filetransfer;
/*    */ 
/*    */ import io.rong.common.RLog;
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InterruptedIOException;
/*    */ import java.net.HttpURLConnection;
/*    */ import java.net.URL;
/*    */ 
/*    */ public abstract class Request
/*    */ {
/* 17 */   private static final String TAG = Request.class.getSimpleName();
/*    */   protected FtConst.MimeType mimeType;
/*    */   protected String url;
/*    */   protected String method;
/*    */   protected Object tag;
/*    */   protected String token;
/*    */   protected int connTimeout;
/*    */   protected int readTimeout;
/*    */   protected String serverIp;
/*    */   protected String fileName;
/*    */   protected RequestCallBack requestCallBack;
/*    */   protected boolean terminated;
/*    */   private HttpURLConnection conn;
/*    */ 
/*    */   public Request(Configuration config, RequestCallBack requestCallBack)
/*    */   {
/* 35 */     this.connTimeout = config.connectTimeout;
/* 36 */     this.readTimeout = config.readTimeout;
/* 37 */     this.requestCallBack = requestCallBack;
/*    */   }
/*    */ 
/*    */   public void setUrl(String url) {
/* 41 */     this.url = url; } 
/*    */   public abstract String getContentType();
/*    */ 
/*    */   public abstract long getContentLength();
/*    */ 
/*    */   public abstract String getFormData();
/*    */ 
/*    */   public abstract String getBoundary();
/*    */ 
/*    */   public abstract FtConst.MimeType getMimeType();
/*    */ 
/* 55 */   public void sendRequest() { BufferedInputStream responseStream = null;
/* 56 */     ByteArrayOutputStream responseData = null;
/* 57 */     DataOutputStream os = null;
/* 58 */     FileInputStream is = null;
/*    */     try {
/* 60 */       if (this.method.equals("POST")) {
/* 61 */         is = new FileInputStream(new File(this.url));
/* 62 */         URL u = new URL(this.serverIp);
/* 63 */         this.conn = ((HttpURLConnection)u.openConnection());
/* 64 */         this.conn.setUseCaches(false);
/* 65 */         this.conn.setDoOutput(true);
/* 66 */         this.conn.setDoInput(true);
/* 67 */         this.conn.setRequestMethod(this.method);
/* 68 */         this.conn.setRequestProperty("Connection", "close");
/* 69 */         this.conn.setRequestProperty("Charset", "UTF-8");
/* 70 */         this.conn.setRequestProperty("Content-Type", getContentType());
/* 71 */         String endBoundary = "\r\n--" + getBoundary() + "--";
/* 72 */         String formData = getFormData();
/* 73 */         int fileSize = is.available();
/* 74 */         if (fileSize == 0) {
/* 75 */           this.requestCallBack.onError(31002);
/*    */         }
/* 77 */         int total = formData.length() + fileSize + endBoundary.length();
/* 78 */         this.conn.setRequestProperty("Content-Length", total + "");
/* 79 */         this.conn.setFixedLengthStreamingMode(total);
/* 80 */         this.conn.connect();
/*    */ 
/* 82 */         os = new DataOutputStream(this.conn.getOutputStream());
/* 83 */         os.writeBytes(formData);
/*    */ 
/* 85 */         int current = formData.length();
/* 86 */         int progress = 1;
/* 87 */         this.requestCallBack.onProgress(progress);
/*    */ 
/* 89 */         byte[] buffer = new byte[1024];
/*    */         int read;
/* 91 */         while ((read = is.read(buffer)) != -1) {
/* 92 */           os.write(buffer, 0, read);
/* 93 */           current += read;
/* 94 */           int size = 100 * current / total;
/* 95 */           if (size > progress) {
/* 96 */             progress = size;
/* 97 */             this.requestCallBack.onProgress(progress);
/*    */           }
/*    */         }
/* 100 */         os.writeBytes(endBoundary);
/* 101 */         this.requestCallBack.onProgress(100);
/*    */ 
/* 103 */         is.close();
/* 104 */         os.flush();
/*    */ 
/* 106 */         responseStream = new BufferedInputStream(this.conn.getInputStream());
/* 107 */         responseData = new ByteArrayOutputStream(1024);
/*    */         int c;
/* 109 */         while ((c = responseStream.read()) != -1) {
/* 110 */           responseData.write(c);
/*    */         }
/* 112 */         int responseCode = this.conn.getResponseCode();
/* 113 */         if ((responseCode < 200) || (responseCode >= 300))
/* 114 */           this.requestCallBack.onError(30002);
/*    */         else
/* 116 */           this.requestCallBack.onComplete(null);
/*    */       }
/* 118 */       else if (this.method.equals("GET")) {
/* 119 */         URL u = new URL(this.url);
/* 120 */         this.conn = ((HttpURLConnection)u.openConnection());
/* 121 */         this.conn.setUseCaches(false);
/* 122 */         this.conn.setRequestMethod(this.method);
/* 123 */         this.conn.setDoInput(true);
/* 124 */         this.conn.connect();
/*    */ 
/* 126 */         responseStream = new BufferedInputStream(this.conn.getInputStream());
/* 127 */         int total = this.conn.getContentLength();
/* 128 */         int current = 0;
/* 129 */         responseData = new ByteArrayOutputStream(1024);
/*    */ 
/* 131 */         int progress = 0;
/* 132 */         int temp = 0;
/*    */         int c;
/* 133 */         while ((c = responseStream.read()) != -1) {
/* 134 */           responseData.write(c);
/* 135 */           current++;
/* 136 */           temp = 100 * current / total;
/* 137 */           if (progress < temp) {
/* 138 */             progress = temp;
/* 139 */             this.requestCallBack.onProgress(temp);
/*    */           }
/* 141 */           if (Thread.currentThread().isInterrupted()) {
/* 142 */             RLog.w(TAG, "sendRequest terminated.");
/* 143 */             this.terminated = true;
/* 144 */             this.requestCallBack.onCanceled(this.tag);
/* 145 */             throw new InterruptedIOException();
/*    */           }
/*    */         }
/*    */ 
/* 149 */         int responseCode = this.conn.getResponseCode();
/* 150 */         if ((responseCode < 200) || (responseCode >= 300)) {
/* 151 */           this.requestCallBack.onError(30002);
/* 152 */           RLog.d("fileTransfer", "download request response code is " + responseCode);
/*    */         } else {
/* 154 */           File f = new File(this.fileName);
/* 155 */           FileOutputStream fos = new FileOutputStream(f);
/* 156 */           responseData.writeTo(fos);
/* 157 */           fos.close();
/* 158 */           this.requestCallBack.onComplete(this.fileName);
/*    */         }
/*    */       }
/*    */     } catch (Exception e) {
/* 162 */       if (!this.terminated) {
/* 163 */         this.requestCallBack.onError(30002);
/*    */       }
/* 165 */       e.printStackTrace();
/*    */     } finally {
/*    */       try {
/* 168 */         if (responseData != null) {
/* 169 */           responseData.close();
/*    */         }
/* 171 */         if (responseStream != null) {
/* 172 */           responseStream.close();
/*    */         }
/* 174 */         if (os != null) {
/* 175 */           os.close();
/*    */         }
/* 177 */         if (is != null)
/* 178 */           is.close();
/*    */       }
/*    */       catch (IOException e) {
/* 181 */         e.printStackTrace();
/*    */       }
/* 183 */       if (this.conn != null) {
/* 184 */         this.conn.disconnect();
/* 185 */         this.conn = null;
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.filetransfer.Request
 * JD-Core Version:    0.6.0
 */
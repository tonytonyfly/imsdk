/*     */ package io.rong.push.core;
/*     */ 
/*     */ import io.rong.imlib.common.BuildVar;
/*     */ import io.rong.push.common.RLog;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ 
/*     */ class PushClient
/*     */ {
/*     */   private static final String TAG = "PushClient";
/*     */   private PushProtocalStack.MessageInputStream in;
/*     */   private Socket socket;
/*     */   private PushProtocalStack.MessageOutputStream out;
/*     */   public OutputStream os;
/*     */   private PushReader reader;
/*     */   private ClientListener listener;
/*     */   private ConnectStatusCallback connectCallback;
/*     */   private QueryCallback queryCallback;
/*     */   private String deviceInfo;
/*     */   private boolean running;
/*     */ 
/*     */   public PushClient(String deviceInfo, ClientListener listener)
/*     */   {
/*  29 */     this.listener = listener;
/*  30 */     this.deviceInfo = deviceInfo;
/*     */   }
/*     */ 
/*     */   public void connect(String host, int port, String deviceId, ConnectStatusCallback callback) {
/*  34 */     RLog.d("PushClient", "connect, deviceId = " + deviceId + ", host = " + host + ", port = " + port);
/*     */     try
/*     */     {
/*  37 */       this.socket = new Socket();
/*  38 */       SocketAddress address = new InetSocketAddress(host, port);
/*  39 */       this.socket.connect(address, 4000);
/*  40 */       InputStream is = this.socket.getInputStream();
/*  41 */       this.in = new PushProtocalStack.MessageInputStream(is);
/*  42 */       this.os = this.socket.getOutputStream();
/*  43 */       this.out = new PushProtocalStack.MessageOutputStream(this.os);
/*  44 */       this.connectCallback = callback;
/*     */ 
/*  46 */       PushProtocalStack.ConnectMessage connectMessage = new PushProtocalStack.ConnectMessage(deviceId, true, 300);
/*  47 */       connectMessage.setWill("clientInfo", String.format("%s-%s-%s", new Object[] { "AndroidPush", this.deviceInfo, BuildVar.SDK_VERSION }));
/*     */ 
/*  49 */       this.out.writeMessage(new PushProtocalStack.ConnectMessage(deviceId, true, 300));
/*  50 */       this.reader = new PushReader(null);
/*  51 */       this.running = true;
/*  52 */       this.reader.start();
/*     */     } catch (Exception e) {
/*  54 */       RLog.e("PushClient", "connect IOException");
/*  55 */       e.printStackTrace();
/*  56 */       if (callback != null)
/*  57 */         callback.onError(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void ping() {
/*     */     try {
/*  63 */       if ((this.socket != null) && (this.socket.isConnected()) && (this.out != null)) {
/*  64 */         this.out.writeMessage(new PushProtocalStack.PingReqMessage());
/*     */       }
/*  66 */       else if (this.listener != null)
/*  67 */         this.listener.onPingFailure();
/*     */     }
/*     */     catch (IOException e) {
/*  70 */       RLog.e("PushClient", "ping IOException");
/*  71 */       e.printStackTrace();
/*  72 */       if (this.listener != null)
/*  73 */         this.listener.onPingFailure();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void query(QueryMethod method, String queryInfo, String deviceId, QueryCallback callback) {
/*  78 */     RLog.d("PushClient", "query. topic:" + method.getMethodName() + ", queryInfo:" + queryInfo);
/*  79 */     this.queryCallback = callback;
/*     */     try {
/*  81 */       if ((this.socket != null) && (this.socket.isConnected()) && (this.out != null))
/*  82 */         this.out.writeMessage(new PushProtocalStack.QueryMessage(method.getMethodName(), queryInfo, deviceId));
/*     */     }
/*     */     catch (IOException e) {
/*  85 */       e.printStackTrace();
/*  86 */       this.queryCallback.onFailure();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void disconnect() {
/*  91 */     RLog.d("PushClient", "disconnect");
/*     */     try {
/*  93 */       if (this.reader != null) {
/*  94 */         this.reader.interrupt();
/*  95 */         this.running = false;
/*  96 */         this.reader = null;
/*     */       }
/*  98 */       this.in.close();
/*  99 */       this.os.close();
/* 100 */       if (this.socket != null)
/* 101 */         this.socket.close();
/*     */     } catch (IOException e) {
/* 103 */       RLog.e("PushClient", "disconnect IOException");
/* 104 */       e.printStackTrace();
/*     */     } finally {
/* 106 */       if (this.listener != null)
/* 107 */         this.listener.onDisConnected();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void reset() {
/* 112 */     RLog.d("PushClient", "reset");
/*     */     try {
/* 114 */       if (this.reader != null) {
/* 115 */         this.reader.interrupt();
/* 116 */         this.running = false;
/* 117 */         this.reader = null;
/*     */       }
/* 119 */       if (this.socket != null) {
/* 120 */         this.socket.close();
/* 121 */         this.socket = null;
/*     */       }
/*     */     } catch (IOException e) {
/* 124 */       RLog.e("PushClient", "reset IOException");
/* 125 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void handleMessage(PushProtocalStack.Message msg) throws IOException {
/* 130 */     if (msg == null) {
/* 131 */       return;
/*     */     }
/* 133 */     RLog.d("PushClient", "handleMessage, msg type = " + msg.getType());
/* 134 */     switch (1.$SwitchMap$io$rong$push$core$PushProtocalStack$Message$Type[msg.getType().ordinal()]) {
/*     */     case 1:
/* 136 */       if (this.connectCallback == null) break;
/* 137 */       this.connectCallback.onConnected(); break;
/*     */     case 2:
/* 141 */       if (this.listener == null) break;
/* 142 */       this.listener.onPingSuccess(); break;
/*     */     case 3:
/* 145 */       PushProtocalStack.QueryAckMessage message = (PushProtocalStack.QueryAckMessage)msg;
/* 146 */       int status = message.getStatus();
/* 147 */       RLog.d("PushClient", "queryAck status:" + status + "content:" + message.getDataAsString());
/* 148 */       if (this.queryCallback == null) break;
/* 149 */       if (status == PushProtocalStack.QueryAckMessage.QueryStatus.STATUS_OK.get())
/* 150 */         this.queryCallback.onSuccess(message.getDataAsString());
/*     */       else
/* 152 */         this.queryCallback.onFailure(); break;
/*     */     case 4:
/* 157 */       if (this.listener == null) break;
/* 158 */       PushProtocalStack.PublishMessage publishMsg = (PushProtocalStack.PublishMessage)msg;
/* 159 */       this.listener.onMessageArrived(publishMsg);
/* 160 */       break;
/*     */     case 5:
/* 163 */       if (this.listener == null) break;
/* 164 */       this.listener.onDisConnected();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum QueryMethod
/*     */   {
/* 219 */     GET_PUSH_TYPE("getPushType"), 
/*     */ 
/* 221 */     SET_TOKEN("setToken");
/*     */ 
/*     */     private String methodName;
/*     */ 
/* 226 */     private QueryMethod(String name) { this.methodName = name; }
/*     */ 
/*     */     public String getMethodName()
/*     */     {
/* 230 */       return this.methodName;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface QueryCallback
/*     */   {
/*     */     public abstract void onSuccess(String paramString);
/*     */ 
/*     */     public abstract void onFailure();
/*     */   }
/*     */ 
/*     */   public static abstract interface ConnectStatusCallback
/*     */   {
/*     */     public abstract void onConnected();
/*     */ 
/*     */     public abstract void onError(IOException paramIOException);
/*     */   }
/*     */ 
/*     */   public static abstract interface ClientListener
/*     */   {
/*     */     public abstract void onMessageArrived(PushProtocalStack.PublishMessage paramPublishMessage);
/*     */ 
/*     */     public abstract void onDisConnected();
/*     */ 
/*     */     public abstract void onPingSuccess();
/*     */ 
/*     */     public abstract void onPingFailure();
/*     */   }
/*     */ 
/*     */   private class PushReader extends Thread
/*     */   {
/*     */     private PushReader()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 176 */       PushProtocalStack.Message msg = null;
/*     */       try {
/* 178 */         while (PushClient.this.running) {
/* 179 */           Thread.sleep(100L);
/* 180 */           if (PushClient.this.in != null)
/* 181 */             msg = PushClient.this.in.readMessage();
/* 182 */           if (msg != null)
/* 183 */             PushClient.this.handleMessage(msg);
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 187 */         RLog.e("PushClient", "PushReader IOException. " + e.getMessage());
/* 188 */         e.printStackTrace();
/* 189 */         if (PushClient.this.listener != null)
/* 190 */           PushClient.this.listener.onDisConnected();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.push.core.PushClient
 * JD-Core Version:    0.6.0
 */
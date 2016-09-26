/*     */ package io.rong.imlib.statistics;
/*     */ 
/*     */ import android.os.Build.VERSION;
/*     */ import android.util.Log;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ import javax.net.ssl.SSLContext;
/*     */ 
/*     */ public class ConnectionProcessor
/*     */   implements Runnable
/*     */ {
/*     */   private static final int CONNECT_TIMEOUT_IN_MILLISECONDS = 30000;
/*     */   private static final int READ_TIMEOUT_IN_MILLISECONDS = 30000;
/*     */   private final StatisticsStore store_;
/*     */   private final DeviceId deviceId_;
/*     */   private final String serverURL_;
/*     */   private final SSLContext sslContext_;
/*     */ 
/*     */   ConnectionProcessor(String serverURL, StatisticsStore store, DeviceId deviceId, SSLContext sslContext)
/*     */   {
/*  60 */     this.serverURL_ = serverURL;
/*  61 */     this.store_ = store;
/*  62 */     this.deviceId_ = deviceId;
/*  63 */     this.sslContext_ = sslContext;
/*     */ 
/*  66 */     if (Build.VERSION.SDK_INT < 8)
/*  67 */       System.setProperty("http.keepAlive", "false");
/*     */   }
/*     */ 
/*     */   URLConnection urlConnectionForEventData(String eventData)
/*     */     throws IOException
/*     */   {
/*  73 */     String urlStr = this.serverURL_;
/*     */ 
/*  76 */     URL url = new URL(urlStr);
/*     */     HttpURLConnection conn;
/*     */     HttpURLConnection conn;
/*  78 */     if (Statistics.publicKeyPinCertificates == null) {
/*  79 */       conn = (HttpURLConnection)url.openConnection();
/*     */     } else {
/*  81 */       HttpsURLConnection c = (HttpsURLConnection)url.openConnection();
/*  82 */       c.setSSLSocketFactory(this.sslContext_.getSocketFactory());
/*  83 */       conn = c;
/*     */     }
/*  85 */     conn.setConnectTimeout(30000);
/*  86 */     conn.setReadTimeout(30000);
/*  87 */     conn.setUseCaches(false);
/*  88 */     conn.setDoInput(true);
/*  89 */     String picturePath = UserData.getPicturePathFromQuery(url);
/*  90 */     if (Statistics.sharedInstance().isLoggingEnabled()) {
/*  91 */       Log.d("Statistics", "Got picturePath: " + picturePath);
/*     */     }
/*  93 */     if (!picturePath.equals(""))
/*     */     {
/*  97 */       File binaryFile = new File(picturePath);
/*  98 */       conn.setDoOutput(true);
/*     */ 
/* 100 */       String boundary = Long.toHexString(System.currentTimeMillis());
/*     */ 
/* 102 */       String CRLF = "\r\n";
/* 103 */       String charset = "UTF-8";
/* 104 */       conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
/* 105 */       OutputStream output = conn.getOutputStream();
/* 106 */       PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
/*     */ 
/* 108 */       writer.append("--" + boundary).append(CRLF);
/* 109 */       writer.append("Content-Disposition: form-data; name=\"binaryFile\"; filename=\"" + binaryFile.getName() + "\"").append(CRLF);
/* 110 */       writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
/* 111 */       writer.append("Content-Transfer-Encoding: binary").append(CRLF);
/* 112 */       writer.append(CRLF).flush();
/* 113 */       FileInputStream fileInputStream = new FileInputStream(binaryFile);
/* 114 */       byte[] buffer = new byte[1024];
/*     */       int len;
/* 116 */       while ((len = fileInputStream.read(buffer)) != -1) {
/* 117 */         output.write(buffer, 0, len);
/*     */       }
/* 119 */       output.flush();
/* 120 */       writer.append(CRLF).flush();
/* 121 */       fileInputStream.close();
/*     */ 
/* 124 */       writer.append("--" + boundary + "--").append(CRLF).flush();
/*     */     }
/* 126 */     else if (eventData.contains("&crash=")) {
/* 127 */       if (Statistics.sharedInstance().isLoggingEnabled()) {
/* 128 */         Log.d("Statistics", "Using post because of crash");
/*     */       }
/* 130 */       conn.setDoOutput(true);
/* 131 */       conn.setRequestMethod("POST");
/* 132 */       OutputStream os = conn.getOutputStream();
/* 133 */       BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
/* 134 */       writer.write(eventData);
/* 135 */       writer.flush();
/* 136 */       writer.close();
/* 137 */       os.close();
/*     */     }
/*     */     else
/*     */     {
/* 141 */       conn.setDoOutput(true);
/* 142 */       conn.setRequestMethod("POST");
/* 143 */       conn.setRequestProperty("Connection", "close");
/* 144 */       OutputStream os = conn.getOutputStream();
/* 145 */       BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
/* 146 */       writer.write(eventData);
/* 147 */       writer.flush();
/* 148 */       writer.close();
/* 149 */       os.close();
/*     */     }
/* 151 */     return conn; } 
/*     */   // ERROR //
/*     */   public void run() { // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 3	io/rong/imlib/statistics/ConnectionProcessor:store_	Lio/rong/imlib/statistics/StatisticsStore;
/*     */     //   4: invokevirtual 77	io/rong/imlib/statistics/StatisticsStore:connections	()[Ljava/lang/String;
/*     */     //   7: astore_1
/*     */     //   8: aload_1
/*     */     //   9: ifnull +486 -> 495
/*     */     //   12: aload_1
/*     */     //   13: arraylength
/*     */     //   14: ifne +6 -> 20
/*     */     //   17: goto +478 -> 495
/*     */     //   20: aload_0
/*     */     //   21: getfield 4	io/rong/imlib/statistics/ConnectionProcessor:deviceId_	Lio/rong/imlib/statistics/DeviceId;
/*     */     //   24: invokevirtual 78	io/rong/imlib/statistics/DeviceId:getId	()Ljava/lang/String;
/*     */     //   27: ifnonnull +42 -> 69
/*     */     //   30: invokestatic 23	io/rong/imlib/statistics/Statistics:sharedInstance	()Lio/rong/imlib/statistics/Statistics;
/*     */     //   33: invokevirtual 24	io/rong/imlib/statistics/Statistics:isLoggingEnabled	()Z
/*     */     //   36: ifeq +459 -> 495
/*     */     //   39: ldc 25
/*     */     //   41: new 26	java/lang/StringBuilder
/*     */     //   44: dup
/*     */     //   45: invokespecial 27	java/lang/StringBuilder:<init>	()V
/*     */     //   48: ldc 79
/*     */     //   50: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   53: aload_1
/*     */     //   54: iconst_0
/*     */     //   55: aaload
/*     */     //   56: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   59: invokevirtual 30	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   62: invokestatic 80	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
/*     */     //   65: pop
/*     */     //   66: goto +429 -> 495
/*     */     //   69: aload_1
/*     */     //   70: iconst_0
/*     */     //   71: aaload
/*     */     //   72: astore_2
/*     */     //   73: aconst_null
/*     */     //   74: astore_3
/*     */     //   75: aconst_null
/*     */     //   76: astore 4
/*     */     //   78: aload_0
/*     */     //   79: aload_2
/*     */     //   80: invokevirtual 81	io/rong/imlib/statistics/ConnectionProcessor:urlConnectionForEventData	(Ljava/lang/String;)Ljava/net/URLConnection;
/*     */     //   83: astore_3
/*     */     //   84: aload_3
/*     */     //   85: invokevirtual 82	java/net/URLConnection:connect	()V
/*     */     //   88: new 83	java/io/BufferedInputStream
/*     */     //   91: dup
/*     */     //   92: aload_3
/*     */     //   93: invokevirtual 84	java/net/URLConnection:getInputStream	()Ljava/io/InputStream;
/*     */     //   96: invokespecial 85	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
/*     */     //   99: astore 4
/*     */     //   101: new 86	java/io/ByteArrayOutputStream
/*     */     //   104: dup
/*     */     //   105: sipush 256
/*     */     //   108: invokespecial 87	java/io/ByteArrayOutputStream:<init>	(I)V
/*     */     //   111: astore 5
/*     */     //   113: aload 4
/*     */     //   115: invokevirtual 88	java/io/BufferedInputStream:read	()I
/*     */     //   118: dup
/*     */     //   119: istore 6
/*     */     //   121: iconst_m1
/*     */     //   122: if_icmpeq +13 -> 135
/*     */     //   125: aload 5
/*     */     //   127: iload 6
/*     */     //   129: invokevirtual 89	java/io/ByteArrayOutputStream:write	(I)V
/*     */     //   132: goto -19 -> 113
/*     */     //   135: iconst_1
/*     */     //   136: istore 7
/*     */     //   138: aload_3
/*     */     //   139: instanceof 14
/*     */     //   142: ifeq +114 -> 256
/*     */     //   145: aload_3
/*     */     //   146: checkcast 14	java/net/HttpURLConnection
/*     */     //   149: astore 8
/*     */     //   151: aload 8
/*     */     //   153: invokevirtual 90	java/net/HttpURLConnection:getResponseCode	()I
/*     */     //   156: istore 9
/*     */     //   158: iload 9
/*     */     //   160: sipush 200
/*     */     //   163: if_icmplt +15 -> 178
/*     */     //   166: iload 9
/*     */     //   168: sipush 300
/*     */     //   171: if_icmpge +7 -> 178
/*     */     //   174: iconst_1
/*     */     //   175: goto +4 -> 179
/*     */     //   178: iconst_0
/*     */     //   179: istore 7
/*     */     //   181: ldc 25
/*     */     //   183: new 26	java/lang/StringBuilder
/*     */     //   186: dup
/*     */     //   187: invokespecial 27	java/lang/StringBuilder:<init>	()V
/*     */     //   190: ldc 91
/*     */     //   192: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   195: iload 9
/*     */     //   197: invokevirtual 92	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   200: invokevirtual 30	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   203: invokestatic 31	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
/*     */     //   206: pop
/*     */     //   207: iload 7
/*     */     //   209: ifne +47 -> 256
/*     */     //   212: invokestatic 23	io/rong/imlib/statistics/Statistics:sharedInstance	()Lio/rong/imlib/statistics/Statistics;
/*     */     //   215: invokevirtual 24	io/rong/imlib/statistics/Statistics:isLoggingEnabled	()Z
/*     */     //   218: ifeq +38 -> 256
/*     */     //   221: ldc 25
/*     */     //   223: new 26	java/lang/StringBuilder
/*     */     //   226: dup
/*     */     //   227: invokespecial 27	java/lang/StringBuilder:<init>	()V
/*     */     //   230: ldc 93
/*     */     //   232: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   235: iload 9
/*     */     //   237: invokevirtual 92	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*     */     //   240: ldc 94
/*     */     //   242: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   245: aload_2
/*     */     //   246: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   249: invokevirtual 30	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   252: invokestatic 95	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
/*     */     //   255: pop
/*     */     //   256: iload 7
/*     */     //   258: ifeq +50 -> 308
/*     */     //   261: invokestatic 23	io/rong/imlib/statistics/Statistics:sharedInstance	()Lio/rong/imlib/statistics/Statistics;
/*     */     //   264: invokevirtual 24	io/rong/imlib/statistics/Statistics:isLoggingEnabled	()Z
/*     */     //   267: ifeq +28 -> 295
/*     */     //   270: ldc 25
/*     */     //   272: new 26	java/lang/StringBuilder
/*     */     //   275: dup
/*     */     //   276: invokespecial 27	java/lang/StringBuilder:<init>	()V
/*     */     //   279: ldc 96
/*     */     //   281: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   284: aload_2
/*     */     //   285: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   288: invokevirtual 30	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   291: invokestatic 31	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
/*     */     //   294: pop
/*     */     //   295: aload_0
/*     */     //   296: getfield 3	io/rong/imlib/statistics/ConnectionProcessor:store_	Lio/rong/imlib/statistics/StatisticsStore;
/*     */     //   299: aload_1
/*     */     //   300: iconst_0
/*     */     //   301: aaload
/*     */     //   302: invokevirtual 97	io/rong/imlib/statistics/StatisticsStore:removeConnection	(Ljava/lang/String;)V
/*     */     //   305: goto +39 -> 344
/*     */     //   308: aload 4
/*     */     //   310: ifnull +13 -> 323
/*     */     //   313: aload 4
/*     */     //   315: invokevirtual 98	java/io/BufferedInputStream:close	()V
/*     */     //   318: goto +5 -> 323
/*     */     //   321: astore 8
/*     */     //   323: aload_3
/*     */     //   324: ifnull +171 -> 495
/*     */     //   327: aload_3
/*     */     //   328: instanceof 14
/*     */     //   331: ifeq +164 -> 495
/*     */     //   334: aload_3
/*     */     //   335: checkcast 14	java/net/HttpURLConnection
/*     */     //   338: invokevirtual 100	java/net/HttpURLConnection:disconnect	()V
/*     */     //   341: goto +154 -> 495
/*     */     //   344: aload 4
/*     */     //   346: ifnull +13 -> 359
/*     */     //   349: aload 4
/*     */     //   351: invokevirtual 98	java/io/BufferedInputStream:close	()V
/*     */     //   354: goto +5 -> 359
/*     */     //   357: astore 5
/*     */     //   359: aload_3
/*     */     //   360: ifnull +132 -> 492
/*     */     //   363: aload_3
/*     */     //   364: instanceof 14
/*     */     //   367: ifeq +125 -> 492
/*     */     //   370: aload_3
/*     */     //   371: checkcast 14	java/net/HttpURLConnection
/*     */     //   374: invokevirtual 100	java/net/HttpURLConnection:disconnect	()V
/*     */     //   377: goto +115 -> 492
/*     */     //   380: astore 5
/*     */     //   382: invokestatic 23	io/rong/imlib/statistics/Statistics:sharedInstance	()Lio/rong/imlib/statistics/Statistics;
/*     */     //   385: invokevirtual 24	io/rong/imlib/statistics/Statistics:isLoggingEnabled	()Z
/*     */     //   388: ifeq +30 -> 418
/*     */     //   391: ldc 25
/*     */     //   393: new 26	java/lang/StringBuilder
/*     */     //   396: dup
/*     */     //   397: invokespecial 27	java/lang/StringBuilder:<init>	()V
/*     */     //   400: ldc 102
/*     */     //   402: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   405: aload_2
/*     */     //   406: invokevirtual 29	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   409: invokevirtual 30	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   412: aload 5
/*     */     //   414: invokestatic 103	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
/*     */     //   417: pop
/*     */     //   418: aload 4
/*     */     //   420: ifnull +13 -> 433
/*     */     //   423: aload 4
/*     */     //   425: invokevirtual 98	java/io/BufferedInputStream:close	()V
/*     */     //   428: goto +5 -> 433
/*     */     //   431: astore 6
/*     */     //   433: aload_3
/*     */     //   434: ifnull +61 -> 495
/*     */     //   437: aload_3
/*     */     //   438: instanceof 14
/*     */     //   441: ifeq +54 -> 495
/*     */     //   444: aload_3
/*     */     //   445: checkcast 14	java/net/HttpURLConnection
/*     */     //   448: invokevirtual 100	java/net/HttpURLConnection:disconnect	()V
/*     */     //   451: goto +44 -> 495
/*     */     //   454: astore 10
/*     */     //   456: aload 4
/*     */     //   458: ifnull +13 -> 471
/*     */     //   461: aload 4
/*     */     //   463: invokevirtual 98	java/io/BufferedInputStream:close	()V
/*     */     //   466: goto +5 -> 471
/*     */     //   469: astore 11
/*     */     //   471: aload_3
/*     */     //   472: ifnull +17 -> 489
/*     */     //   475: aload_3
/*     */     //   476: instanceof 14
/*     */     //   479: ifeq +10 -> 489
/*     */     //   482: aload_3
/*     */     //   483: checkcast 14	java/net/HttpURLConnection
/*     */     //   486: invokevirtual 100	java/net/HttpURLConnection:disconnect	()V
/*     */     //   489: aload 10
/*     */     //   491: athrow
/*     */     //   492: goto -492 -> 0
/*     */     //   495: return
/*     */     //
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   313	318	321	java/io/IOException
/*     */     //   349	354	357	java/io/IOException
/*     */     //   78	308	380	java/lang/Exception
/*     */     //   423	428	431	java/io/IOException
/*     */     //   78	308	454	finally
/*     */     //   380	418	454	finally
/*     */     //   454	456	454	finally
/*     */     //   461	466	469	java/io/IOException } 
/* 250 */   String getServerURL() { return this.serverURL_; }
/*     */ 
/*     */   StatisticsStore getCountlyStore() {
/* 253 */     return this.store_;
/*     */   }
/*     */   DeviceId getDeviceId() {
/* 256 */     return this.deviceId_;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.statistics.ConnectionProcessor
 * JD-Core Version:    0.6.0
 */
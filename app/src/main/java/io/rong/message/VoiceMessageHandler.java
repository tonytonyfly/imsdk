/*    */ package io.rong.message;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.net.Uri;
/*    */ import android.text.TextUtils;
/*    */ import android.util.Base64;
/*    */ import io.rong.common.FileUtils;
/*    */ import io.rong.common.RLog;
/*    */ import io.rong.imlib.NativeClient;
/*    */ import io.rong.imlib.model.Message;
/*    */ import java.io.BufferedOutputStream;
/*    */ import java.io.File;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class VoiceMessageHandler extends MessageHandler<VoiceMessage>
/*    */ {
/*    */   private static final String TAG = "VoiceMessageHandler";
/*    */   private static final String VOICE_PATH = "/voice/";
/*    */ 
/*    */   public VoiceMessageHandler(Context context)
/*    */   {
/* 24 */     super(context);
/*    */   }
/*    */ 
/*    */   public void decodeMessage(Message message, VoiceMessage model)
/*    */   {
/* 29 */     Uri uri = obtainVoiceUri(getContext());
/* 30 */     String name = message.getMessageId() + ".amr";
/* 31 */     if (message.getMessageId() == 0) {
/* 32 */       name = message.getSentTime() + ".amr";
/*    */     }
/* 34 */     File file = new File(uri.toString() + name);
/* 35 */     if ((!TextUtils.isEmpty(model.getBase64())) && (!file.exists())) {
/*    */       try {
/* 37 */         byte[] data = Base64.decode(model.getBase64(), 2);
/*    */ 
/* 39 */         file = saveFile(data, uri.toString() + "/voice/", name);
/*    */       } catch (IllegalArgumentException e) {
/* 41 */         RLog.e("VoiceMessageHandler", "afterDecodeMessage Not Base64 Content!");
/* 42 */         e.printStackTrace();
/*    */       } catch (IOException e) {
/* 44 */         e.printStackTrace();
/*    */       }
/*    */     }
/*    */ 
/* 48 */     model.setUri(Uri.fromFile(file));
/* 49 */     model.setBase64(null);
/*    */   }
/*    */ 
/*    */   public void encodeMessage(Message message)
/*    */   {
/* 55 */     VoiceMessage model = (VoiceMessage)message.getContent();
/* 56 */     Uri uri = obtainVoiceUri(getContext());
/* 57 */     byte[] voiceData = FileUtils.getByteFromUri(model.getUri());
/* 58 */     File file = null;
/*    */     try {
/* 60 */       String base64 = Base64.encodeToString(voiceData, 2);
/* 61 */       model.setBase64(base64);
/*    */ 
/* 63 */       String name = message.getMessageId() + ".amr";
/*    */ 
/* 65 */       file = saveFile(voiceData, uri.toString() + "/voice/", name);
/*    */     } catch (IllegalArgumentException e) {
/* 67 */       RLog.e("VoiceMessageHandler", "beforeEncodeMessage Not Base64 Content!");
/* 68 */       e.printStackTrace();
/*    */     }
/*    */     catch (IOException e) {
/* 71 */       e.printStackTrace();
/*    */     }
/*    */ 
/* 74 */     if ((file != null) && (file.exists()))
/* 75 */       model.setUri(Uri.fromFile(file));
/*    */   }
/*    */ 
/*    */   private static File saveFile(byte[] data, String path, String fileName) throws IOException {
/* 79 */     File file = new File(path);
/* 80 */     if (!file.exists()) {
/* 81 */       file.mkdirs();
/*    */     }
/* 83 */     file = new File(path + fileName);
/* 84 */     BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
/* 85 */     bos.write(data);
/* 86 */     bos.flush();
/* 87 */     bos.close();
/* 88 */     return file;
/*    */   }
/*    */ 
/*    */   private static Uri obtainVoiceUri(Context context) {
/* 92 */     File file = context.getFilesDir();
/* 93 */     String path = file.getAbsolutePath();
/* 94 */     String userId = NativeClient.getInstance().getCurrentUserId();
/* 95 */     return Uri.parse(path + File.separator + userId);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.message.VoiceMessageHandler
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.imlib.filetransfer;
/*    */ 
/*    */ public class FtConst
/*    */ {
/*    */   public static enum ServiceType
/*    */   {
/* 42 */     QI_NIU;
/*    */   }
/*    */ 
/*    */   public static enum MimeType
/*    */   {
/*  9 */     NONE(0, "none"), 
/* 10 */     FILE_IMAGE(1, "image_jpeg"), 
/* 11 */     FILE_AUDIO(2, "audio_amr"), 
/* 12 */     FILE_VIDEO(3, "video_3gpp"), 
/* 13 */     FILE_TEXT_PLAIN(4, "text_plain");
/*    */ 
/* 15 */     private int value = 1;
/* 16 */     private String name = "";
/*    */ 
/*    */     private MimeType(int value, String name) {
/* 19 */       this.value = value;
/* 20 */       this.name = name;
/*    */     }
/*    */ 
/*    */     public int getValue() {
/* 24 */       return this.value;
/*    */     }
/*    */ 
/*    */     public String getName() {
/* 28 */       return this.name;
/*    */     }
/*    */ 
/*    */     public static MimeType setValue(int code) {
/* 32 */       for (MimeType c : values()) {
/* 33 */         if (code == c.getValue()) {
/* 34 */           return c;
/*    */         }
/*    */       }
/* 37 */       return NONE;
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.filetransfer.FtConst
 * JD-Core Version:    0.6.0
 */
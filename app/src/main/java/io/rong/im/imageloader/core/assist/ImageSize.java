/*    */ package io.rong.imageloader.core.assist;
/*    */ 
/*    */ public class ImageSize
/*    */ {
/*    */   private static final int TO_STRING_MAX_LENGHT = 9;
/*    */   private static final String SEPARATOR = "x";
/*    */   private final int width;
/*    */   private final int height;
/*    */ 
/*    */   public ImageSize(int width, int height)
/*    */   {
/* 33 */     this.width = width;
/* 34 */     this.height = height;
/*    */   }
/*    */ 
/*    */   public ImageSize(int width, int height, int rotation) {
/* 38 */     if (rotation % 180 == 0) {
/* 39 */       this.width = width;
/* 40 */       this.height = height;
/*    */     } else {
/* 42 */       this.width = height;
/* 43 */       this.height = width;
/*    */     }
/*    */   }
/*    */ 
/*    */   public int getWidth() {
/* 48 */     return this.width;
/*    */   }
/*    */ 
/*    */   public int getHeight() {
/* 52 */     return this.height;
/*    */   }
/*    */ 
/*    */   public ImageSize scaleDown(int sampleSize)
/*    */   {
/* 57 */     return new ImageSize(this.width / sampleSize, this.height / sampleSize);
/*    */   }
/*    */ 
/*    */   public ImageSize scale(float scale)
/*    */   {
/* 62 */     return new ImageSize((int)(this.width * scale), (int)(this.height * scale));
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 67 */     return 9 + this.width + "x" + this.height;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.assist.ImageSize
 * JD-Core Version:    0.6.0
 */
/*    */ package io.rong.calllib;
/*    */ 
/*    */ public class AgoraVideoFrame
/*    */ {
/*    */   private byte[] yBuffer;
/*    */   private byte[] uBuffer;
/*    */   private byte[] vBuffer;
/*    */   private int width;
/*    */   private int height;
/*    */   private int yStride;
/*    */   private int uStride;
/*    */   private int vStride;
/*    */ 
/*    */   public byte[] getyBuffer()
/*    */   {
/*  6 */     return this.yBuffer;
/*    */   }
/*    */ 
/*    */   public void setyBuffer(byte[] yBuffer) {
/* 10 */     this.yBuffer = yBuffer;
/*    */   }
/*    */ 
/*    */   public byte[] getuBuffer() {
/* 14 */     return this.uBuffer;
/*    */   }
/*    */ 
/*    */   public void setuBuffer(byte[] uBuffer) {
/* 18 */     this.uBuffer = uBuffer;
/*    */   }
/*    */ 
/*    */   public byte[] getvBuffer() {
/* 22 */     return this.vBuffer;
/*    */   }
/*    */ 
/*    */   public void setvBuffer(byte[] vBuffer) {
/* 26 */     this.vBuffer = vBuffer;
/*    */   }
/*    */ 
/*    */   public int getWidth() {
/* 30 */     return this.width;
/*    */   }
/*    */ 
/*    */   public void setWidth(int width) {
/* 34 */     this.width = width;
/*    */   }
/*    */ 
/*    */   public int getHeight() {
/* 38 */     return this.height;
/*    */   }
/*    */ 
/*    */   public void setHeight(int height) {
/* 42 */     this.height = height;
/*    */   }
/*    */ 
/*    */   public int getyStride() {
/* 46 */     return this.yStride;
/*    */   }
/*    */ 
/*    */   public void setyStride(int yStride) {
/* 50 */     this.yStride = yStride;
/*    */   }
/*    */ 
/*    */   public int getuStride() {
/* 54 */     return this.uStride;
/*    */   }
/*    */ 
/*    */   public void setuStride(int uStride) {
/* 58 */     this.uStride = uStride;
/*    */   }
/*    */ 
/*    */   public int getvStride() {
/* 62 */     return this.vStride;
/*    */   }
/*    */ 
/*    */   public void setvStride(int vStride) {
/* 66 */     this.vStride = vStride;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.AgoraVideoFrame
 * JD-Core Version:    0.6.0
 */
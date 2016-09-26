/*    */ package io.rong.calllib;
/*    */ 
/*    */ public class NativeCallObject
/*    */ {
/*    */   private NativeCallObject()
/*    */   {
/* 10 */     setCallJNIEnv(this);
/*    */   }
/*    */ 
/*    */   public static NativeCallObject getInstance()
/*    */   {
/* 18 */     return SingletonHolder.sInstance;
/*    */   }
/*    */ 
/*    */   native void registerVideoFrameObserver(IVideoFrameListener paramIVideoFrameListener);
/*    */ 
/*    */   native void unregisterVideoFrameObserver();
/*    */ 
/*    */   protected native void setCallJNIEnv(NativeCallObject paramNativeCallObject);
/*    */ 
/*    */   static
/*    */   {
/*  6 */     System.loadLibrary("RongCallLib");
/*    */   }
/*    */ 
/*    */   private static class SingletonHolder
/*    */   {
/* 14 */     static NativeCallObject sInstance = new NativeCallObject(null);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.calllib.NativeCallObject
 * JD-Core Version:    0.6.0
 */
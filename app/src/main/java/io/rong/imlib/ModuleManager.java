/*    */ package io.rong.imlib;
/*    */ 
/*    */ import android.content.Context;
/*    */ import io.rong.common.RLog;
/*    */ import io.rong.imlib.model.Message;
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ public class ModuleManager
/*    */ {
/*    */   private static ArrayList<PreHandleListener> preHandleListeners;
/*    */ 
/*    */   public static void init(Context context, IHandler stub)
/*    */   {
/* 15 */     preHandleListeners = new ArrayList();
/*    */     try
/*    */     {
/* 18 */       String moduleName = "io.rong.calllib.RongCallClient";
/* 19 */       Class cls = Class.forName(moduleName);
/* 20 */       Constructor constructor = cls.getConstructor(new Class[] { Context.class, IHandler.class });
/* 21 */       constructor.newInstance(new Object[] { context, stub });
/*    */     } catch (Exception e) {
/* 23 */       RLog.i("ModuleManager", "Can not find RongCallClient module.");
/*    */     }
/*    */   }
/*    */ 
/*    */   public static boolean handleReceivedMessage(Message message, int left, boolean offline, int cmdLeft) {
/* 28 */     for (PreHandleListener listener : preHandleListeners) {
/* 29 */       if (listener.onReceived(message, left, offline, cmdLeft)) {
/* 30 */         return true;
/*    */       }
/*    */     }
/* 33 */     return false;
/*    */   }
/*    */ 
/*    */   public static void addPreHandlerListener(PreHandleListener listener) {
/* 37 */     preHandleListeners.add(listener);
/*    */   }
/*    */ 
/*    */   public static void removePreHandlerListener(PreHandleListener listener) {
/* 41 */     preHandleListeners.remove(listener);
/*    */   }
/*    */ 
/*    */   public static abstract interface PreHandleListener
/*    */   {
/*    */     public abstract boolean onReceived(Message paramMessage, int paramInt1, boolean paramBoolean, int paramInt2);
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.ModuleManager
 * JD-Core Version:    0.6.0
 */
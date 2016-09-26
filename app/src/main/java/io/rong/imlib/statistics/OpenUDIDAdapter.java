/*    */ package io.rong.imlib.statistics;
/*    */ 
/*    */ import android.content.Context;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ public class OpenUDIDAdapter
/*    */ {
/*    */   private static final String OPEN_UDID_MANAGER_CLASS_NAME = "org.openudid.OpenUDID_manager";
/*    */ 
/*    */   public static boolean isOpenUDIDAvailable()
/*    */   {
/* 12 */     boolean openUDIDAvailable = false;
/*    */     try {
/* 14 */       Class.forName("org.openudid.OpenUDID_manager");
/* 15 */       openUDIDAvailable = true;
/*    */     } catch (ClassNotFoundException ignored) {
/*    */     }
/* 18 */     return openUDIDAvailable;
/*    */   }
/*    */ 
/*    */   public static boolean isInitialized() {
/* 22 */     boolean initialized = false;
/*    */     try {
/* 24 */       Class cls = Class.forName("org.openudid.OpenUDID_manager");
/* 25 */       Method isInitializedMethod = cls.getMethod("isInitialized", (Class[])null);
/* 26 */       Object result = isInitializedMethod.invoke(null, (Object[])null);
/* 27 */       if ((result instanceof Boolean))
/* 28 */         initialized = ((Boolean)result).booleanValue();
/*    */     } catch (ClassNotFoundException ignored) {
/*    */     } catch (NoSuchMethodException ignored) {
/*    */     } catch (InvocationTargetException ignored) {
/*    */     }
/*    */     catch (IllegalAccessException ignored) {
/*    */     }
/* 35 */     return initialized;
/*    */   }
/*    */ 
/*    */   public static void sync(Context context) {
/*    */     try {
/* 40 */       Class cls = Class.forName("org.openudid.OpenUDID_manager");
/* 41 */       Method syncMethod = cls.getMethod("sync", new Class[] { Context.class });
/* 42 */       syncMethod.invoke(null, new Object[] { context });
/*    */     } catch (ClassNotFoundException ignored) {
/*    */     } catch (NoSuchMethodException ignored) {
/*    */     } catch (InvocationTargetException ignored) {
/*    */     } catch (IllegalAccessException ignored) {
/*    */     }
/*    */   }
/*    */ 
/*    */   public static String getOpenUDID() {
/* 51 */     String openUDID = null;
/*    */     try {
/* 53 */       Class cls = Class.forName("org.openudid.OpenUDID_manager");
/* 54 */       Method getOpenUDIDMethod = cls.getMethod("getOpenUDID", (Class[])null);
/* 55 */       Object result = getOpenUDIDMethod.invoke(null, (Object[])null);
/* 56 */       if ((result instanceof String))
/* 57 */         openUDID = (String)result;
/*    */     } catch (ClassNotFoundException ignored) {
/*    */     } catch (NoSuchMethodException ignored) {
/*    */     } catch (InvocationTargetException ignored) {
/*    */     }
/*    */     catch (IllegalAccessException ignored) {
/*    */     }
/* 64 */     return openUDID;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.statistics.OpenUDIDAdapter
 * JD-Core Version:    0.6.0
 */
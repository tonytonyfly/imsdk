/*    */ package io.rong.photoview.log;
/*    */ 
/*    */ public final class LogManager
/*    */ {
/* 26 */   private static Logger logger = new LoggerDefault();
/*    */ 
/*    */   public static void setLogger(Logger newLogger) {
/* 29 */     logger = newLogger;
/*    */   }
/*    */ 
/*    */   public static Logger getLogger() {
/* 33 */     return logger;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.photoview.log.LogManager
 * JD-Core Version:    0.6.0
 */
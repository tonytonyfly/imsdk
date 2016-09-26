/*    */ package io.rong.imkit.utils;
/*    */ 
/*    */ public class StringUtils
/*    */ {
/*    */   private static final String SEPARATOR = "#@6RONG_CLOUD9@#";
/*    */ 
/*    */   public static String getKey(String arg1, String arg2)
/*    */   {
/* 10 */     return arg1 + "#@6RONG_CLOUD9@#" + arg2;
/*    */   }
/*    */ 
/*    */   public static String getArg1(String key) {
/* 14 */     String arg = null;
/* 15 */     if (key.contains("#@6RONG_CLOUD9@#")) {
/* 16 */       int index = key.indexOf("#@6RONG_CLOUD9@#");
/* 17 */       arg = key.substring(0, index);
/*    */     }
/* 19 */     return arg;
/*    */   }
/*    */ 
/*    */   public static String getArg2(String key) {
/* 23 */     String arg = null;
/* 24 */     if (key.contains("#@6RONG_CLOUD9@#")) {
/* 25 */       int index = key.indexOf("#@6RONG_CLOUD9@#") + "#@6RONG_CLOUD9@#".length();
/* 26 */       arg = key.substring(index, key.length());
/*    */     }
/* 28 */     return arg;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imkit.utils.StringUtils
 * JD-Core Version:    0.6.0
 */
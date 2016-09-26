/*    */ package io.rong.imlib.model;
/*    */ 
/*    */ public enum CustomServiceMode
/*    */ {
/*  7 */   CUSTOM_SERVICE_MODE_NO_SERVICE(0), 
/*    */ 
/* 11 */   CUSTOM_SERVICE_MODE_ROBOT(1), 
/*    */ 
/* 16 */   CUSTOM_SERVICE_MODE_HUMAN(2), 
/*    */ 
/* 21 */   CUSTOM_SERVICE_MODE_ROBOT_FIRST(3), 
/*    */ 
/* 26 */   CUSTOM_SERVICE_MODE_HUMAN_FIRST(4);
/*    */ 
/*    */   private int mode;
/*    */ 
/* 30 */   private CustomServiceMode(int mode) { this.mode = mode;
/*    */   }
/*    */ 
/*    */   public static CustomServiceMode valueOf(int mode)
/*    */   {
/* 40 */     for (CustomServiceMode m : values()) {
/* 41 */       if (m.mode == mode)
/* 42 */         return m;
/*    */     }
/* 44 */     return CUSTOM_SERVICE_MODE_ROBOT;
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 53 */     return this.mode;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imlib.model.CustomServiceMode
 * JD-Core Version:    0.6.0
 */
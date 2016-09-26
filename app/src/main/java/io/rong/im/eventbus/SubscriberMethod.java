/*    */ package io.rong.eventbus;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ final class SubscriberMethod
/*    */ {
/*    */   final Method method;
/*    */   final ThreadMode threadMode;
/*    */   final Class<?> eventType;
/*    */   String methodString;
/*    */ 
/*    */   SubscriberMethod(Method method, ThreadMode threadMode, Class<?> eventType)
/*    */   {
/* 28 */     this.method = method;
/* 29 */     this.threadMode = threadMode;
/* 30 */     this.eventType = eventType;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 35 */     if ((other instanceof SubscriberMethod)) {
/* 36 */       checkMethodString();
/* 37 */       SubscriberMethod otherSubscriberMethod = (SubscriberMethod)other;
/* 38 */       otherSubscriberMethod.checkMethodString();
/*    */ 
/* 40 */       return this.methodString.equals(otherSubscriberMethod.methodString);
/*    */     }
/* 42 */     return false;
/*    */   }
/*    */ 
/*    */   private synchronized void checkMethodString()
/*    */   {
/* 47 */     if (this.methodString == null)
/*    */     {
/* 49 */       StringBuilder builder = new StringBuilder(64);
/* 50 */       builder.append(this.method.getDeclaringClass().getName());
/* 51 */       builder.append('#').append(this.method.getName());
/* 52 */       builder.append('(').append(this.eventType.getName());
/* 53 */       this.methodString = builder.toString();
/*    */     }
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 59 */     return this.method.hashCode();
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.SubscriberMethod
 * JD-Core Version:    0.6.0
 */
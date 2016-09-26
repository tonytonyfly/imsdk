/*    */ package io.rong.eventbus.util;
/*    */ 
/*    */ import android.util.Log;
/*    */ import io.rong.eventbus.EventBus;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class ExceptionToResourceMapping
/*    */ {
/*    */   public final Map<Class<? extends Throwable>, Integer> throwableToMsgIdMap;
/*    */ 
/*    */   public ExceptionToResourceMapping()
/*    */   {
/* 23 */     this.throwableToMsgIdMap = new HashMap();
/*    */   }
/*    */ 
/*    */   public Integer mapThrowable(Throwable throwable)
/*    */   {
/* 28 */     Throwable throwableToCheck = throwable;
/* 29 */     int depthToGo = 20;
/*    */     while (true)
/*    */     {
/* 32 */       Integer resId = mapThrowableFlat(throwableToCheck);
/* 33 */       if (resId != null) {
/* 34 */         return resId;
/*    */       }
/* 36 */       throwableToCheck = throwableToCheck.getCause();
/* 37 */       depthToGo--;
/* 38 */       if ((depthToGo <= 0) || (throwableToCheck == throwable) || (throwableToCheck == null)) {
/* 39 */         Log.d(EventBus.TAG, "No specific message ressource ID found for " + throwable);
/*    */ 
/* 41 */         return null;
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   protected Integer mapThrowableFlat(Throwable throwable)
/*    */   {
/* 50 */     Class throwableClass = throwable.getClass();
/* 51 */     Integer resId = (Integer)this.throwableToMsgIdMap.get(throwableClass);
/*    */     Class closestClass;
/* 52 */     if (resId == null) {
/* 53 */       closestClass = null;
/* 54 */       Set mappings = this.throwableToMsgIdMap.entrySet();
/* 55 */       for (Map.Entry mapping : mappings) {
/* 56 */         Class candidate = (Class)mapping.getKey();
/* 57 */         if ((candidate.isAssignableFrom(throwableClass)) && (
/* 58 */           (closestClass == null) || (closestClass.isAssignableFrom(candidate)))) {
/* 59 */           closestClass = candidate;
/* 60 */           resId = (Integer)mapping.getValue();
/*    */         }
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 66 */     return resId;
/*    */   }
/*    */ 
/*    */   public ExceptionToResourceMapping addMapping(Class<? extends Throwable> clazz, int msgId) {
/* 70 */     this.throwableToMsgIdMap.put(clazz, Integer.valueOf(msgId));
/* 71 */     return this;
/*    */   }
/*    */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.util.ExceptionToResourceMapping
 * JD-Core Version:    0.6.0
 */
/*     */ package io.rong.eventbus;
/*     */ 
/*     */ import android.util.Log;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ class SubscriberMethodFinder
/*     */ {
/*     */   private static final String ON_EVENT_METHOD_NAME = "onEvent";
/*     */   private static final int BRIDGE = 64;
/*     */   private static final int SYNTHETIC = 4096;
/*     */   private static final int MODIFIERS_IGNORE = 5192;
/*  41 */   private static final Map<String, List<SubscriberMethod>> methodCache = new HashMap();
/*     */   private final Map<Class<?>, Class<?>> skipMethodVerificationForClasses;
/*     */ 
/*     */   SubscriberMethodFinder(List<Class<?>> skipMethodVerificationForClassesList)
/*     */   {
/*  46 */     this.skipMethodVerificationForClasses = new ConcurrentHashMap();
/*  47 */     if (skipMethodVerificationForClassesList != null)
/*  48 */       for (Class clazz : skipMethodVerificationForClassesList)
/*  49 */         this.skipMethodVerificationForClasses.put(clazz, clazz);
/*     */   }
/*     */ 
/*     */   List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass)
/*     */   {
/*  55 */     String key = subscriberClass.getName();
/*     */ 
/*  57 */     synchronized (methodCache) {
/*  58 */       subscriberMethods = (List)methodCache.get(key);
/*     */     }
/*  60 */     if (subscriberMethods != null) {
/*  61 */       return subscriberMethods;
/*     */     }
/*  63 */     List subscriberMethods = new ArrayList();
/*  64 */     Class clazz = subscriberClass;
/*  65 */     HashSet eventTypesFound = new HashSet();
/*  66 */     StringBuilder methodKeyBuilder = new StringBuilder();
/*  67 */     while (clazz != null) {
/*  68 */       String name = clazz.getName();
/*  69 */       if ((name.startsWith("java.")) || (name.startsWith("javax.")) || (name.startsWith("android.")))
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/*  75 */       Method[] methods = clazz.getDeclaredMethods();
/*  76 */       for (Method method : methods) {
/*  77 */         String methodName = method.getName();
/*  78 */         if (methodName.startsWith("onEvent")) {
/*  79 */           int modifiers = method.getModifiers();
/*  80 */           if (((modifiers & 0x1) != 0) && ((modifiers & 0x1448) == 0)) {
/*  81 */             Class[] parameterTypes = method.getParameterTypes();
/*  82 */             if (parameterTypes.length == 1) {
/*  83 */               String modifierString = methodName.substring("onEvent".length());
/*     */               ThreadMode threadMode;
/*  85 */               if (modifierString.length() == 0) {
/*  86 */                 threadMode = ThreadMode.PostThread;
/*     */               }
/*     */               else
/*     */               {
/*     */                 ThreadMode threadMode;
/*  87 */                 if (modifierString.equals("MainThread")) {
/*  88 */                   threadMode = ThreadMode.MainThread;
/*     */                 }
/*     */                 else
/*     */                 {
/*     */                   ThreadMode threadMode;
/*  89 */                   if (modifierString.equals("BackgroundThread")) {
/*  90 */                     threadMode = ThreadMode.BackgroundThread;
/*     */                   }
/*     */                   else
/*     */                   {
/*     */                     ThreadMode threadMode;
/*  91 */                     if (modifierString.equals("Async")) {
/*  92 */                       threadMode = ThreadMode.Async;
/*     */                     } else {
/*  94 */                       if (this.skipMethodVerificationForClasses.containsKey(clazz)) {
/*     */                         continue;
/*     */                       }
/*  97 */                       throw new EventBusException(new StringBuilder().append("Illegal onEvent method, check for typos: ").append(method).toString());
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */               ThreadMode threadMode;
/* 100 */               Class eventType = parameterTypes[0];
/* 101 */               methodKeyBuilder.setLength(0);
/* 102 */               methodKeyBuilder.append(methodName);
/* 103 */               methodKeyBuilder.append('>').append(eventType.getName());
/* 104 */               String methodKey = methodKeyBuilder.toString();
/* 105 */               if (eventTypesFound.add(methodKey))
/*     */               {
/* 107 */                 subscriberMethods.add(new SubscriberMethod(method, threadMode, eventType));
/*     */               }
/*     */             }
/* 110 */           } else if (!this.skipMethodVerificationForClasses.containsKey(clazz)) {
/* 111 */             Log.d(EventBus.TAG, new StringBuilder().append("Skipping method (not public, static or abstract): ").append(clazz).append(".").append(methodName).toString());
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 116 */       clazz = clazz.getSuperclass();
/*     */     }
/* 118 */     if (subscriberMethods.isEmpty()) {
/* 119 */       throw new EventBusException(new StringBuilder().append("Subscriber ").append(subscriberClass).append(" has no public methods called ").append("onEvent").toString());
/*     */     }
/*     */ 
/* 122 */     synchronized (methodCache) {
/* 123 */       methodCache.put(key, subscriberMethods);
/*     */     }
/* 125 */     return subscriberMethods;
/*     */   }
/*     */ 
/*     */   static void clearCaches()
/*     */   {
/* 130 */     synchronized (methodCache) {
/* 131 */       methodCache.clear();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.SubscriberMethodFinder
 * JD-Core Version:    0.6.0
 */
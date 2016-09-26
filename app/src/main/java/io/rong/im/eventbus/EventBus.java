/*     */ package io.rong.eventbus;
/*     */ 
/*     */ import android.os.Looper;
/*     */ import android.util.Log;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ 
/*     */ public class EventBus
/*     */ {
/*  42 */   public static String TAG = "Event";
/*     */   static volatile EventBus defaultInstance;
/*  46 */   private static final EventBusBuilder DEFAULT_BUILDER = new EventBusBuilder();
/*  47 */   private static final Map<Class<?>, List<Class<?>>> eventTypesCache = new HashMap();
/*     */   private final Map<Class<?>, CopyOnWriteArrayList<Subscription>> subscriptionsByEventType;
/*     */   private final Map<Object, List<Class<?>>> typesBySubscriber;
/*     */   private final Map<Class<?>, Object> stickyEvents;
/*  53 */   private final ThreadLocal<PostingThreadState> currentPostingThreadState = new ThreadLocal()
/*     */   {
/*     */     protected EventBus.PostingThreadState initialValue() {
/*  56 */       return new EventBus.PostingThreadState(); }  } ;
/*     */   private final HandlerPoster mainThreadPoster;
/*     */   private final BackgroundPoster backgroundPoster;
/*     */   private final AsyncPoster asyncPoster;
/*     */   private final SubscriberMethodFinder subscriberMethodFinder;
/*     */   private final ExecutorService executorService;
/*     */   private final boolean throwSubscriberException;
/*     */   private final boolean logSubscriberExceptions;
/*     */   private final boolean logNoSubscriberMessages;
/*     */   private final boolean sendSubscriberExceptionEvent;
/*     */   private final boolean sendNoSubscriberEvent;
/*     */   private final boolean eventInheritance;
/*     */ 
/*  76 */   public static EventBus getDefault() { if (defaultInstance == null) {
/*  77 */       synchronized (EventBus.class) {
/*  78 */         if (defaultInstance == null) {
/*  79 */           defaultInstance = new EventBus();
/*     */         }
/*     */       }
/*     */     }
/*  83 */     return defaultInstance; }
/*     */ 
/*     */   public static EventBusBuilder builder()
/*     */   {
/*  87 */     return new EventBusBuilder();
/*     */   }
/*     */ 
/*     */   public static void clearCaches()
/*     */   {
/*  92 */     SubscriberMethodFinder.clearCaches();
/*  93 */     eventTypesCache.clear();
/*     */   }
/*     */ 
/*     */   public EventBus()
/*     */   {
/* 101 */     this(DEFAULT_BUILDER);
/*     */   }
/*     */ 
/*     */   EventBus(EventBusBuilder builder) {
/* 105 */     this.subscriptionsByEventType = new HashMap();
/* 106 */     this.typesBySubscriber = new HashMap();
/* 107 */     this.stickyEvents = new ConcurrentHashMap();
/* 108 */     this.mainThreadPoster = new HandlerPoster(this, Looper.getMainLooper(), 10);
/* 109 */     this.backgroundPoster = new BackgroundPoster(this);
/* 110 */     this.asyncPoster = new AsyncPoster(this);
/* 111 */     this.subscriberMethodFinder = new SubscriberMethodFinder(builder.skipMethodVerificationForClasses);
/* 112 */     this.logSubscriberExceptions = builder.logSubscriberExceptions;
/* 113 */     this.logNoSubscriberMessages = builder.logNoSubscriberMessages;
/* 114 */     this.sendSubscriberExceptionEvent = builder.sendSubscriberExceptionEvent;
/* 115 */     this.sendNoSubscriberEvent = builder.sendNoSubscriberEvent;
/* 116 */     this.throwSubscriberException = builder.throwSubscriberException;
/* 117 */     this.eventInheritance = builder.eventInheritance;
/* 118 */     this.executorService = builder.executorService;
/*     */   }
/*     */ 
/*     */   public void register(Object subscriber)
/*     */   {
/* 133 */     register(subscriber, false, 0);
/*     */   }
/*     */ 
/*     */   public void register(Object subscriber, int priority)
/*     */   {
/* 143 */     register(subscriber, false, priority);
/*     */   }
/*     */ 
/*     */   public void registerSticky(Object subscriber)
/*     */   {
/* 151 */     register(subscriber, true, 0);
/*     */   }
/*     */ 
/*     */   public void registerSticky(Object subscriber, int priority)
/*     */   {
/* 159 */     register(subscriber, true, priority);
/*     */   }
/*     */ 
/*     */   private synchronized void register(Object subscriber, boolean sticky, int priority) {
/* 163 */     List subscriberMethods = this.subscriberMethodFinder.findSubscriberMethods(subscriber.getClass());
/* 164 */     for (SubscriberMethod subscriberMethod : subscriberMethods)
/* 165 */       subscribe(subscriber, subscriberMethod, sticky, priority);
/*     */   }
/*     */ 
/*     */   private void subscribe(Object subscriber, SubscriberMethod subscriberMethod, boolean sticky, int priority)
/*     */   {
/* 171 */     Class eventType = subscriberMethod.eventType;
/* 172 */     CopyOnWriteArrayList subscriptions = (CopyOnWriteArrayList)this.subscriptionsByEventType.get(eventType);
/* 173 */     Subscription newSubscription = new Subscription(subscriber, subscriberMethod, priority);
/* 174 */     if (subscriptions == null) {
/* 175 */       subscriptions = new CopyOnWriteArrayList();
/* 176 */       this.subscriptionsByEventType.put(eventType, subscriptions);
/*     */     }
/* 178 */     else if (subscriptions.contains(newSubscription)) {
/* 179 */       throw new EventBusException("Subscriber " + subscriber.getClass() + " already registered to event " + eventType);
/*     */     }
/*     */ 
/* 187 */     int size = subscriptions.size();
/* 188 */     for (int i = 0; i <= size; i++) {
/* 189 */       if ((i == size) || (newSubscription.priority > ((Subscription)subscriptions.get(i)).priority)) {
/* 190 */         subscriptions.add(i, newSubscription);
/* 191 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 195 */     List subscribedEvents = (List)this.typesBySubscriber.get(subscriber);
/* 196 */     if (subscribedEvents == null) {
/* 197 */       subscribedEvents = new ArrayList();
/* 198 */       this.typesBySubscriber.put(subscriber, subscribedEvents);
/*     */     }
/* 200 */     subscribedEvents.add(eventType);
/*     */ 
/* 202 */     if (sticky)
/*     */     {
/*     */       Object stickyEvent;
/* 204 */       synchronized (this.stickyEvents) {
/* 205 */         stickyEvent = this.stickyEvents.get(eventType);
/*     */       }
/* 207 */       if (stickyEvent != null)
/*     */       {
/* 210 */         postToSubscription(newSubscription, stickyEvent, Looper.getMainLooper() == Looper.myLooper());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized boolean isRegistered(Object subscriber) {
/* 216 */     return this.typesBySubscriber.containsKey(subscriber);
/*     */   }
/*     */ 
/*     */   private void unubscribeByEventType(Object subscriber, Class<?> eventType)
/*     */   {
/* 221 */     List subscriptions = (List)this.subscriptionsByEventType.get(eventType);
/* 222 */     if (subscriptions != null) {
/* 223 */       int size = subscriptions.size();
/* 224 */       for (int i = 0; i < size; i++) {
/* 225 */         Subscription subscription = (Subscription)subscriptions.get(i);
/* 226 */         if (subscription.subscriber == subscriber) {
/* 227 */           subscription.active = false;
/* 228 */           subscriptions.remove(i);
/* 229 */           i--;
/* 230 */           size--;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void unregister(Object subscriber)
/*     */   {
/* 238 */     List subscribedTypes = (List)this.typesBySubscriber.get(subscriber);
/* 239 */     if (subscribedTypes != null) {
/* 240 */       for (Class eventType : subscribedTypes) {
/* 241 */         unubscribeByEventType(subscriber, eventType);
/*     */       }
/* 243 */       this.typesBySubscriber.remove(subscriber);
/*     */     } else {
/* 245 */       Log.w(TAG, "Subscriber to unregister was not registered before: " + subscriber.getClass());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void post(Object event)
/*     */   {
/* 251 */     PostingThreadState postingState = (PostingThreadState)this.currentPostingThreadState.get();
/* 252 */     List eventQueue = postingState.eventQueue;
/* 253 */     eventQueue.add(event);
/*     */ 
/* 255 */     if (!postingState.isPosting) {
/* 256 */       postingState.isMainThread = (Looper.getMainLooper() == Looper.myLooper());
/* 257 */       postingState.isPosting = true;
/* 258 */       if (postingState.canceled)
/* 259 */         throw new EventBusException("Internal error. Abort state was not reset");
/*     */       try
/*     */       {
/* 262 */         while (!eventQueue.isEmpty())
/* 263 */           postSingleEvent(eventQueue.remove(0), postingState);
/*     */       }
/*     */       finally {
/* 266 */         postingState.isPosting = false;
/* 267 */         postingState.isMainThread = false;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void cancelEventDelivery(Object event)
/*     */   {
/* 280 */     PostingThreadState postingState = (PostingThreadState)this.currentPostingThreadState.get();
/* 281 */     if (!postingState.isPosting) {
/* 282 */       throw new EventBusException("This method may only be called from inside event handling methods on the posting thread");
/*     */     }
/* 284 */     if (event == null)
/* 285 */       throw new EventBusException("Event may not be null");
/* 286 */     if (postingState.event != event)
/* 287 */       throw new EventBusException("Only the currently handled event may be aborted");
/* 288 */     if (postingState.subscription.subscriberMethod.threadMode != ThreadMode.PostThread) {
/* 289 */       throw new EventBusException(" event handlers may only abort the incoming event");
/*     */     }
/*     */ 
/* 292 */     postingState.canceled = true;
/*     */   }
/*     */ 
/*     */   public void postSticky(Object event)
/*     */   {
/* 301 */     synchronized (this.stickyEvents) {
/* 302 */       this.stickyEvents.put(event.getClass(), event);
/*     */     }
/*     */ 
/* 305 */     post(event);
/*     */   }
/*     */ 
/*     */   public <T> T getStickyEvent(Class<T> eventType)
/*     */   {
/* 314 */     synchronized (this.stickyEvents) {
/* 315 */       return eventType.cast(this.stickyEvents.get(eventType));
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> T removeStickyEvent(Class<T> eventType)
/*     */   {
/* 325 */     synchronized (this.stickyEvents) {
/* 326 */       return eventType.cast(this.stickyEvents.remove(eventType));
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean removeStickyEvent(Object event)
/*     */   {
/* 336 */     synchronized (this.stickyEvents) {
/* 337 */       Class eventType = event.getClass();
/* 338 */       Object existingEvent = this.stickyEvents.get(eventType);
/* 339 */       if (event.equals(existingEvent)) {
/* 340 */         this.stickyEvents.remove(eventType);
/* 341 */         return true;
/*     */       }
/* 343 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeAllStickyEvents()
/*     */   {
/* 352 */     synchronized (this.stickyEvents) {
/* 353 */       this.stickyEvents.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean hasSubscriberForEvent(Class<?> eventClass) {
/* 358 */     List eventTypes = lookupAllEventTypes(eventClass);
/* 359 */     if (eventTypes != null) {
/* 360 */       int countTypes = eventTypes.size();
/* 361 */       for (int h = 0; h < countTypes; h++) {
/* 362 */         Class clazz = (Class)eventTypes.get(h);
/*     */         CopyOnWriteArrayList subscriptions;
/* 364 */         synchronized (this) {
/* 365 */           subscriptions = (CopyOnWriteArrayList)this.subscriptionsByEventType.get(clazz);
/*     */         }
/* 367 */         if ((subscriptions != null) && (!subscriptions.isEmpty())) {
/* 368 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 372 */     return false;
/*     */   }
/*     */ 
/*     */   private void postSingleEvent(Object event, PostingThreadState postingState) throws Error {
/* 376 */     Class eventClass = event.getClass();
/* 377 */     boolean subscriptionFound = false;
/* 378 */     if (this.eventInheritance) {
/* 379 */       List eventTypes = lookupAllEventTypes(eventClass);
/* 380 */       int countTypes = eventTypes.size();
/* 381 */       for (int h = 0; h < countTypes; h++) {
/* 382 */         Class clazz = (Class)eventTypes.get(h);
/* 383 */         subscriptionFound |= postSingleEventForEventType(event, postingState, clazz);
/*     */       }
/*     */     } else {
/* 386 */       subscriptionFound = postSingleEventForEventType(event, postingState, eventClass);
/*     */     }
/* 388 */     if (!subscriptionFound) {
/* 389 */       if (this.logNoSubscriberMessages) {
/* 390 */         Log.d(TAG, "No subscribers registered for event " + eventClass);
/*     */       }
/* 392 */       if ((this.sendNoSubscriberEvent) && (eventClass != NoSubscriberEvent.class) && (eventClass != SubscriberExceptionEvent.class))
/*     */       {
/* 394 */         post(new NoSubscriberEvent(this, event));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean postSingleEventForEventType(Object event, PostingThreadState postingState, Class<?> eventClass)
/*     */   {
/*     */     CopyOnWriteArrayList subscriptions;
/* 401 */     synchronized (this) {
/* 402 */       subscriptions = (CopyOnWriteArrayList)this.subscriptionsByEventType.get(eventClass);
/*     */     }
/* 404 */     if ((subscriptions != null) && (!subscriptions.isEmpty())) {
/* 405 */       for (Subscription subscription : subscriptions) {
/* 406 */         postingState.event = event;
/* 407 */         postingState.subscription = subscription;
/* 408 */         boolean aborted = false;
/*     */         try {
/* 410 */           postToSubscription(subscription, event, postingState.isMainThread);
/* 411 */           aborted = postingState.canceled;
/*     */         } finally {
/* 413 */           postingState.event = null;
/* 414 */           postingState.subscription = null;
/* 415 */           postingState.canceled = false;
/*     */         }
/* 417 */         if (aborted) {
/*     */           break;
/*     */         }
/*     */       }
/* 421 */       return true;
/*     */     }
/* 423 */     return false;
/*     */   }
/*     */ 
/*     */   private void postToSubscription(Subscription subscription, Object event, boolean isMainThread) {
/* 427 */     switch (2.$SwitchMap$io$rong$eventbus$ThreadMode[subscription.subscriberMethod.threadMode.ordinal()]) {
/*     */     case 1:
/* 429 */       invokeSubscriber(subscription, event);
/* 430 */       break;
/*     */     case 2:
/* 432 */       if (isMainThread)
/* 433 */         invokeSubscriber(subscription, event);
/*     */       else {
/* 435 */         this.mainThreadPoster.enqueue(subscription, event);
/*     */       }
/* 437 */       break;
/*     */     case 3:
/* 439 */       if (isMainThread)
/* 440 */         this.backgroundPoster.enqueue(subscription, event);
/*     */       else {
/* 442 */         invokeSubscriber(subscription, event);
/*     */       }
/* 444 */       break;
/*     */     case 4:
/* 446 */       this.asyncPoster.enqueue(subscription, event);
/* 447 */       break;
/*     */     default:
/* 449 */       throw new IllegalStateException("Unknown thread mode: " + subscription.subscriberMethod.threadMode);
/*     */     }
/*     */   }
/*     */ 
/*     */   private List<Class<?>> lookupAllEventTypes(Class<?> eventClass)
/*     */   {
/* 455 */     synchronized (eventTypesCache) {
/* 456 */       List eventTypes = (List)eventTypesCache.get(eventClass);
/* 457 */       if (eventTypes == null) {
/* 458 */         eventTypes = new ArrayList();
/* 459 */         Class clazz = eventClass;
/* 460 */         while (clazz != null) {
/* 461 */           eventTypes.add(clazz);
/* 462 */           addInterfaces(eventTypes, clazz.getInterfaces());
/* 463 */           clazz = clazz.getSuperclass();
/*     */         }
/* 465 */         eventTypesCache.put(eventClass, eventTypes);
/*     */       }
/* 467 */       return eventTypes;
/*     */     }
/*     */   }
/*     */ 
/*     */   static void addInterfaces(List<Class<?>> eventTypes, Class<?>[] interfaces)
/*     */   {
/* 473 */     for (Class interfaceClass : interfaces)
/* 474 */       if (!eventTypes.contains(interfaceClass)) {
/* 475 */         eventTypes.add(interfaceClass);
/* 476 */         addInterfaces(eventTypes, interfaceClass.getInterfaces());
/*     */       }
/*     */   }
/*     */ 
/*     */   void invokeSubscriber(PendingPost pendingPost)
/*     */   {
/* 488 */     Object event = pendingPost.event;
/* 489 */     Subscription subscription = pendingPost.subscription;
/* 490 */     PendingPost.releasePendingPost(pendingPost);
/* 491 */     if (subscription.active)
/* 492 */       invokeSubscriber(subscription, event);
/*     */   }
/*     */ 
/*     */   void invokeSubscriber(Subscription subscription, Object event)
/*     */   {
/*     */     try {
/* 498 */       subscription.subscriberMethod.method.invoke(subscription.subscriber, new Object[] { event });
/*     */     } catch (InvocationTargetException e) {
/* 500 */       handleSubscriberException(subscription, event, e.getCause());
/*     */     } catch (IllegalAccessException e) {
/* 502 */       throw new IllegalStateException("Unexpected exception", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void handleSubscriberException(Subscription subscription, Object event, Throwable cause) {
/* 507 */     if ((event instanceof SubscriberExceptionEvent)) {
/* 508 */       if (this.logSubscriberExceptions)
/*     */       {
/* 510 */         Log.e(TAG, "SubscriberExceptionEvent subscriber " + subscription.subscriber.getClass() + " threw an exception", cause);
/*     */ 
/* 512 */         SubscriberExceptionEvent exEvent = (SubscriberExceptionEvent)event;
/* 513 */         Log.e(TAG, "Initial event " + exEvent.causingEvent + " caused exception in " + exEvent.causingSubscriber, exEvent.throwable);
/*     */       }
/*     */     }
/*     */     else {
/* 517 */       if (this.throwSubscriberException) {
/* 518 */         throw new EventBusException("Invoking subscriber failed", cause);
/*     */       }
/* 520 */       if (this.logSubscriberExceptions) {
/* 521 */         Log.e(TAG, "Could not dispatch event: " + event.getClass() + " to subscribing class " + subscription.subscriber.getClass(), cause);
/*     */       }
/*     */ 
/* 524 */       if (this.sendSubscriberExceptionEvent) {
/* 525 */         SubscriberExceptionEvent exEvent = new SubscriberExceptionEvent(this, cause, event, subscription.subscriber);
/*     */ 
/* 527 */         post(exEvent);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   ExecutorService getExecutorService()
/*     */   {
/* 543 */     return this.executorService;
/*     */   }
/*     */ 
/*     */   static abstract interface PostCallback
/*     */   {
/*     */     public abstract void onPostCompleted(List<SubscriberExceptionEvent> paramList);
/*     */   }
/*     */ 
/*     */   static final class PostingThreadState
/*     */   {
/* 534 */     final List<Object> eventQueue = new ArrayList();
/*     */     boolean isPosting;
/*     */     boolean isMainThread;
/*     */     Subscription subscription;
/*     */     Object event;
/*     */     boolean canceled;
/*     */   }
/*     */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.eventbus.EventBus
 * JD-Core Version:    0.6.0
 */
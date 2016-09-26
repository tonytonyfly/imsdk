/*      */ package io.rong.imageloader.core.assist.deque;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.Array;
/*      */ import java.util.AbstractQueue;
/*      */ import java.util.Collection;
/*      */ import java.util.Iterator;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.locks.Condition;
/*      */ import java.util.concurrent.locks.ReentrantLock;
/*      */ 
/*      */ public class LinkedBlockingDeque<E> extends AbstractQueue<E>
/*      */   implements BlockingDeque<E>, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = -387911632671998426L;
/*      */   transient Node<E> first;
/*      */   transient Node<E> last;
/*      */   private transient int count;
/*      */   private final int capacity;
/*  135 */   final ReentrantLock lock = new ReentrantLock();
/*      */ 
/*  140 */   private final Condition notEmpty = this.lock.newCondition();
/*      */ 
/*  145 */   private final Condition notFull = this.lock.newCondition();
/*      */ 
/*      */   public LinkedBlockingDeque()
/*      */   {
/*  152 */     this(2147483647);
/*      */   }
/*      */ 
/*      */   public LinkedBlockingDeque(int capacity)
/*      */   {
/*  162 */     if (capacity <= 0) throw new IllegalArgumentException();
/*  163 */     this.capacity = capacity;
/*      */   }
/*      */ 
/*      */   public LinkedBlockingDeque(Collection<? extends E> c)
/*      */   {
/*  177 */     this(2147483647);
/*  178 */     ReentrantLock lock = this.lock;
/*  179 */     lock.lock();
/*      */     try {
/*  181 */       for (i$ = c.iterator(); i$.hasNext(); ) { Object e = i$.next();
/*  182 */         if (e == null)
/*  183 */           throw new NullPointerException();
/*  184 */         if (!linkLast(new Node(e)))
/*  185 */           throw new IllegalStateException("Deque full");
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*      */       Iterator i$;
/*  188 */       lock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean linkFirst(Node<E> node)
/*      */   {
/*  200 */     if (this.count >= this.capacity)
/*  201 */       return false;
/*  202 */     Node f = this.first;
/*  203 */     node.next = f;
/*  204 */     this.first = node;
/*  205 */     if (this.last == null)
/*  206 */       this.last = node;
/*      */     else
/*  208 */       f.prev = node;
/*  209 */     this.count += 1;
/*  210 */     this.notEmpty.signal();
/*  211 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean linkLast(Node<E> node)
/*      */   {
/*  219 */     if (this.count >= this.capacity)
/*  220 */       return false;
/*  221 */     Node l = this.last;
/*  222 */     node.prev = l;
/*  223 */     this.last = node;
/*  224 */     if (this.first == null)
/*  225 */       this.first = node;
/*      */     else
/*  227 */       l.next = node;
/*  228 */     this.count += 1;
/*  229 */     this.notEmpty.signal();
/*  230 */     return true;
/*      */   }
/*      */ 
/*      */   private E unlinkFirst()
/*      */   {
/*  238 */     Node f = this.first;
/*  239 */     if (f == null)
/*  240 */       return null;
/*  241 */     Node n = f.next;
/*  242 */     Object item = f.item;
/*  243 */     f.item = null;
/*  244 */     f.next = f;
/*  245 */     this.first = n;
/*  246 */     if (n == null)
/*  247 */       this.last = null;
/*      */     else
/*  249 */       n.prev = null;
/*  250 */     this.count -= 1;
/*  251 */     this.notFull.signal();
/*  252 */     return item;
/*      */   }
/*      */ 
/*      */   private E unlinkLast()
/*      */   {
/*  260 */     Node l = this.last;
/*  261 */     if (l == null)
/*  262 */       return null;
/*  263 */     Node p = l.prev;
/*  264 */     Object item = l.item;
/*  265 */     l.item = null;
/*  266 */     l.prev = l;
/*  267 */     this.last = p;
/*  268 */     if (p == null)
/*  269 */       this.first = null;
/*      */     else
/*  271 */       p.next = null;
/*  272 */     this.count -= 1;
/*  273 */     this.notFull.signal();
/*  274 */     return item;
/*      */   }
/*      */ 
/*      */   void unlink(Node<E> x)
/*      */   {
/*  282 */     Node p = x.prev;
/*  283 */     Node n = x.next;
/*  284 */     if (p == null) {
/*  285 */       unlinkFirst();
/*  286 */     } else if (n == null) {
/*  287 */       unlinkLast();
/*      */     } else {
/*  289 */       p.next = n;
/*  290 */       n.prev = p;
/*  291 */       x.item = null;
/*      */ 
/*  294 */       this.count -= 1;
/*  295 */       this.notFull.signal();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addFirst(E e)
/*      */   {
/*  306 */     if (!offerFirst(e))
/*  307 */       throw new IllegalStateException("Deque full");
/*      */   }
/*      */ 
/*      */   public void addLast(E e)
/*      */   {
/*  315 */     if (!offerLast(e))
/*  316 */       throw new IllegalStateException("Deque full");
/*      */   }
/*      */ 
/*      */   public boolean offerFirst(E e)
/*      */   {
/*  323 */     if (e == null) throw new NullPointerException();
/*  324 */     Node node = new Node(e);
/*  325 */     ReentrantLock lock = this.lock;
/*  326 */     lock.lock();
/*      */     try {
/*  328 */       boolean bool = linkFirst(node);
/*      */       return bool; } finally { lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public boolean offerLast(E e)
/*      */   {
/*  338 */     if (e == null) throw new NullPointerException();
/*  339 */     Node node = new Node(e);
/*  340 */     ReentrantLock lock = this.lock;
/*  341 */     lock.lock();
/*      */     try {
/*  343 */       boolean bool = linkLast(node);
/*      */       return bool; } finally { lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public void putFirst(E e)
/*      */     throws InterruptedException
/*      */   {
/*  354 */     if (e == null) throw new NullPointerException();
/*  355 */     Node node = new Node(e);
/*  356 */     ReentrantLock lock = this.lock;
/*  357 */     lock.lock();
/*      */     try {
/*  359 */       while (!linkFirst(node))
/*  360 */         this.notFull.await();
/*      */     } finally {
/*  362 */       lock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void putLast(E e)
/*      */     throws InterruptedException
/*      */   {
/*  371 */     if (e == null) throw new NullPointerException();
/*  372 */     Node node = new Node(e);
/*  373 */     ReentrantLock lock = this.lock;
/*  374 */     lock.lock();
/*      */     try {
/*  376 */       while (!linkLast(node))
/*  377 */         this.notFull.await();
/*      */     } finally {
/*  379 */       lock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean offerFirst(E e, long timeout, TimeUnit unit)
/*      */     throws InterruptedException
/*      */   {
/*  389 */     if (e == null) throw new NullPointerException();
/*  390 */     Node node = new Node(e);
/*  391 */     long nanos = unit.toNanos(timeout);
/*  392 */     ReentrantLock lock = this.lock;
/*  393 */     lock.lockInterruptibly();
/*      */     try {
/*  395 */       while (!linkFirst(node)) {
/*  396 */         if (nanos <= 0L) {
/*  397 */           i = 0;
/*      */           return i;
/*      */         }
/*  398 */         nanos = this.notFull.awaitNanos(nanos);
/*      */       }
/*  400 */       int i = 1;
/*      */       return i; } finally { lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public boolean offerLast(E e, long timeout, TimeUnit unit)
/*      */     throws InterruptedException
/*      */   {
/*  412 */     if (e == null) throw new NullPointerException();
/*  413 */     Node node = new Node(e);
/*  414 */     long nanos = unit.toNanos(timeout);
/*  415 */     ReentrantLock lock = this.lock;
/*  416 */     lock.lockInterruptibly();
/*      */     try {
/*  418 */       while (!linkLast(node)) {
/*  419 */         if (nanos <= 0L) {
/*  420 */           i = 0;
/*      */           return i;
/*      */         }
/*  421 */         nanos = this.notFull.awaitNanos(nanos);
/*      */       }
/*  423 */       int i = 1;
/*      */       return i; } finally { lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public E removeFirst()
/*      */   {
/*  433 */     Object x = pollFirst();
/*  434 */     if (x == null) throw new NoSuchElementException();
/*  435 */     return x;
/*      */   }
/*      */ 
/*      */   public E removeLast()
/*      */   {
/*  442 */     Object x = pollLast();
/*  443 */     if (x == null) throw new NoSuchElementException();
/*  444 */     return x;
/*      */   }
/*      */ 
/*      */   public E pollFirst() {
/*  448 */     ReentrantLock lock = this.lock;
/*  449 */     lock.lock();
/*      */     try {
/*  451 */       Object localObject1 = unlinkFirst();
/*      */       return localObject1; } finally { lock.unlock(); } throw localObject2;
/*      */   }
/*      */ 
/*      */   public E pollLast()
/*      */   {
/*  458 */     ReentrantLock lock = this.lock;
/*  459 */     lock.lock();
/*      */     try {
/*  461 */       Object localObject1 = unlinkLast();
/*      */       return localObject1; } finally { lock.unlock(); } throw localObject2;
/*      */   }
/*      */ 
/*      */   public E takeFirst() throws InterruptedException
/*      */   {
/*  468 */     ReentrantLock lock = this.lock;
/*  469 */     lock.lock();
/*      */     try
/*      */     {
/*      */       Object x;
/*  472 */       while ((x = unlinkFirst()) == null)
/*  473 */         this.notEmpty.await();
/*  474 */       Object localObject1 = x;
/*      */       return localObject1; } finally { lock.unlock(); } throw localObject2;
/*      */   }
/*      */ 
/*      */   public E takeLast() throws InterruptedException
/*      */   {
/*  481 */     ReentrantLock lock = this.lock;
/*  482 */     lock.lock();
/*      */     try
/*      */     {
/*      */       Object x;
/*  485 */       while ((x = unlinkLast()) == null)
/*  486 */         this.notEmpty.await();
/*  487 */       Object localObject1 = x;
/*      */       return localObject1; } finally { lock.unlock(); } throw localObject2;
/*      */   }
/*      */ 
/*      */   public E pollFirst(long timeout, TimeUnit unit)
/*      */     throws InterruptedException
/*      */   {
/*  495 */     long nanos = unit.toNanos(timeout);
/*  496 */     ReentrantLock lock = this.lock;
/*  497 */     lock.lockInterruptibly();
/*      */     try
/*      */     {
/*      */       Object x;
/*  500 */       while ((x = unlinkFirst()) == null) {
/*  501 */         if (nanos <= 0L) {
/*  502 */           localObject1 = null;
/*      */           return localObject1;
/*      */         }
/*  503 */         nanos = this.notEmpty.awaitNanos(nanos);
/*      */       }
/*  505 */       Object localObject1 = x;
/*      */       return localObject1; } finally { lock.unlock(); } throw localObject2;
/*      */   }
/*      */ 
/*      */   public E pollLast(long timeout, TimeUnit unit)
/*      */     throws InterruptedException
/*      */   {
/*  513 */     long nanos = unit.toNanos(timeout);
/*  514 */     ReentrantLock lock = this.lock;
/*  515 */     lock.lockInterruptibly();
/*      */     try
/*      */     {
/*      */       Object x;
/*  518 */       while ((x = unlinkLast()) == null) {
/*  519 */         if (nanos <= 0L) {
/*  520 */           localObject1 = null;
/*      */           return localObject1;
/*      */         }
/*  521 */         nanos = this.notEmpty.awaitNanos(nanos);
/*      */       }
/*  523 */       Object localObject1 = x;
/*      */       return localObject1; } finally { lock.unlock(); } throw localObject2;
/*      */   }
/*      */ 
/*      */   public E getFirst()
/*      */   {
/*  533 */     Object x = peekFirst();
/*  534 */     if (x == null) throw new NoSuchElementException();
/*  535 */     return x;
/*      */   }
/*      */ 
/*      */   public E getLast()
/*      */   {
/*  542 */     Object x = peekLast();
/*  543 */     if (x == null) throw new NoSuchElementException();
/*  544 */     return x;
/*      */   }
/*      */ 
/*      */   public E peekFirst() {
/*  548 */     ReentrantLock lock = this.lock;
/*  549 */     lock.lock();
/*      */     try {
/*  551 */       Object localObject1 = this.first == null ? null : this.first.item;
/*      */       return localObject1; } finally { lock.unlock(); } throw localObject2;
/*      */   }
/*      */ 
/*      */   public E peekLast()
/*      */   {
/*  558 */     ReentrantLock lock = this.lock;
/*  559 */     lock.lock();
/*      */     try {
/*  561 */       Object localObject1 = this.last == null ? null : this.last.item;
/*      */       return localObject1; } finally { lock.unlock(); } throw localObject2;
/*      */   }
/*      */ 
/*      */   public boolean removeFirstOccurrence(Object o)
/*      */   {
/*  568 */     if (o == null) return false;
/*  569 */     ReentrantLock lock = this.lock;
/*  570 */     lock.lock();
/*      */     try {
/*  572 */       for (Node p = this.first; p != null; p = p.next)
/*  573 */         if (o.equals(p.item)) {
/*  574 */           unlink(p);
/*  575 */           int i = 1;
/*      */           return i;
/*      */         }
/*  578 */       p = 0;
/*      */       return p; } finally { lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public boolean removeLastOccurrence(Object o)
/*      */   {
/*  585 */     if (o == null) return false;
/*  586 */     ReentrantLock lock = this.lock;
/*  587 */     lock.lock();
/*      */     try {
/*  589 */       for (Node p = this.last; p != null; p = p.prev)
/*  590 */         if (o.equals(p.item)) {
/*  591 */           unlink(p);
/*  592 */           int i = 1;
/*      */           return i;
/*      */         }
/*  595 */       p = 0;
/*      */       return p; } finally { lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public boolean add(E e)
/*      */   {
/*  615 */     addLast(e);
/*  616 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean offer(E e)
/*      */   {
/*  623 */     return offerLast(e);
/*      */   }
/*      */ 
/*      */   public void put(E e)
/*      */     throws InterruptedException
/*      */   {
/*  631 */     putLast(e);
/*      */   }
/*      */ 
/*      */   public boolean offer(E e, long timeout, TimeUnit unit)
/*      */     throws InterruptedException
/*      */   {
/*  640 */     return offerLast(e, timeout, unit);
/*      */   }
/*      */ 
/*      */   public E remove()
/*      */   {
/*  654 */     return removeFirst();
/*      */   }
/*      */ 
/*      */   public E poll() {
/*  658 */     return pollFirst();
/*      */   }
/*      */ 
/*      */   public E take() throws InterruptedException {
/*  662 */     return takeFirst();
/*      */   }
/*      */ 
/*      */   public E poll(long timeout, TimeUnit unit) throws InterruptedException {
/*  666 */     return pollFirst(timeout, unit);
/*      */   }
/*      */ 
/*      */   public E element()
/*      */   {
/*  680 */     return getFirst();
/*      */   }
/*      */ 
/*      */   public E peek() {
/*  684 */     return peekFirst();
/*      */   }
/*      */ 
/*      */   public int remainingCapacity()
/*      */   {
/*  699 */     ReentrantLock lock = this.lock;
/*  700 */     lock.lock();
/*      */     try {
/*  702 */       int i = this.capacity - this.count;
/*      */       return i; } finally { lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public int drainTo(Collection<? super E> c)
/*      */   {
/*  715 */     return drainTo(c, 2147483647);
/*      */   }
/*      */ 
/*      */   public int drainTo(Collection<? super E> c, int maxElements)
/*      */   {
/*  725 */     if (c == null)
/*  726 */       throw new NullPointerException();
/*  727 */     if (c == this)
/*  728 */       throw new IllegalArgumentException();
/*  729 */     ReentrantLock lock = this.lock;
/*  730 */     lock.lock();
/*      */     try {
/*  732 */       int n = Math.min(maxElements, this.count);
/*  733 */       for (int i = 0; i < n; i++) {
/*  734 */         c.add(this.first.item);
/*  735 */         unlinkFirst();
/*      */       }
/*  737 */       i = n;
/*      */       return i; } finally { lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public void push(E e)
/*      */   {
/*  750 */     addFirst(e);
/*      */   }
/*      */ 
/*      */   public E pop()
/*      */   {
/*  757 */     return removeFirst();
/*      */   }
/*      */ 
/*      */   public boolean remove(Object o)
/*      */   {
/*  777 */     return removeFirstOccurrence(o);
/*      */   }
/*      */ 
/*      */   public int size()
/*      */   {
/*  786 */     ReentrantLock lock = this.lock;
/*  787 */     lock.lock();
/*      */     try {
/*  789 */       int i = this.count;
/*      */       return i; } finally { lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public boolean contains(Object o)
/*      */   {
/*  804 */     if (o == null) return false;
/*  805 */     ReentrantLock lock = this.lock;
/*  806 */     lock.lock();
/*      */     try {
/*  808 */       for (Node p = this.first; p != null; p = p.next)
/*  809 */         if (o.equals(p.item)) {
/*  810 */           int i = 1;
/*      */           return i;
/*      */         }
/*  811 */       p = 0;
/*      */       return p; } finally { lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public Object[] toArray()
/*      */   {
/*  872 */     ReentrantLock lock = this.lock;
/*  873 */     lock.lock();
/*      */     try {
/*  875 */       Object[] a = new Object[this.count];
/*  876 */       int k = 0;
/*  877 */       for (Node p = this.first; p != null; p = p.next)
/*  878 */         a[(k++)] = p.item;
/*  879 */       p = a;
/*      */       return p; } finally { lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public <T> T[] toArray(T[] a)
/*      */   {
/*  923 */     ReentrantLock lock = this.lock;
/*  924 */     lock.lock();
/*      */     try {
/*  926 */       if (a.length < this.count) {
/*  927 */         a = (Object[])(Object[])Array.newInstance(a.getClass().getComponentType(), this.count);
/*      */       }
/*      */ 
/*  930 */       int k = 0;
/*  931 */       for (Node p = this.first; p != null; p = p.next)
/*  932 */         a[(k++)] = p.item;
/*  933 */       if (a.length > k)
/*  934 */         a[k] = null;
/*  935 */       p = a;
/*      */       return p; } finally { lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  942 */     ReentrantLock lock = this.lock;
/*  943 */     lock.lock();
/*      */     try {
/*  945 */       Node p = this.first;
/*  946 */       if (p == null) {
/*  947 */         String str1 = "[]";
/*      */         return str1;
/*      */       }
/*  949 */       StringBuilder sb = new StringBuilder();
/*  950 */       sb.append('[');
/*      */       while (true) {
/*  952 */         Object e = p.item;
/*  953 */         sb.append(e == this ? "(this Collection)" : e);
/*  954 */         p = p.next;
/*  955 */         if (p == null) {
/*  956 */           String str2 = sb.append(']').toString();
/*      */           return str2;
/*      */         }
/*  957 */         sb.append(',').append(' ');
/*      */       }
/*      */     } finally {
/*  960 */       lock.unlock(); } throw localObject1;
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */   {
/*  969 */     ReentrantLock lock = this.lock;
/*  970 */     lock.lock();
/*      */     try {
/*  972 */       for (Node f = this.first; f != null; ) {
/*  973 */         f.item = null;
/*  974 */         Node n = f.next;
/*  975 */         f.prev = null;
/*  976 */         f.next = null;
/*  977 */         f = n;
/*      */       }
/*  979 */       this.first = (this.last = null);
/*  980 */       this.count = 0;
/*  981 */       this.notFull.signalAll();
/*      */     } finally {
/*  983 */       lock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public Iterator<E> iterator()
/*      */   {
/* 1001 */     return new Itr(null);
/*      */   }
/*      */ 
/*      */   public Iterator<E> descendingIterator()
/*      */   {
/* 1019 */     return new DescendingItr(null);
/*      */   }
/*      */ 
/*      */   private void writeObject(ObjectOutputStream s)
/*      */     throws IOException
/*      */   {
/* 1160 */     ReentrantLock lock = this.lock;
/* 1161 */     lock.lock();
/*      */     try
/*      */     {
/* 1164 */       s.defaultWriteObject();
/*      */ 
/* 1166 */       for (Node p = this.first; p != null; p = p.next) {
/* 1167 */         s.writeObject(p.item);
/*      */       }
/* 1169 */       s.writeObject(null);
/*      */     } finally {
/* 1171 */       lock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream s)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/* 1183 */     s.defaultReadObject();
/* 1184 */     this.count = 0;
/* 1185 */     this.first = null;
/* 1186 */     this.last = null;
/*      */     while (true)
/*      */     {
/* 1190 */       Object item = s.readObject();
/* 1191 */       if (item == null)
/*      */         break;
/* 1193 */       add(item);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class DescendingItr extends LinkedBlockingDeque<E>.AbstractItr
/*      */   {
/*      */     private DescendingItr()
/*      */     {
/* 1141 */       super();
/*      */     }
/* 1143 */     LinkedBlockingDeque.Node<E> firstNode() { return LinkedBlockingDeque.this.last; }
/*      */ 
/*      */     LinkedBlockingDeque.Node<E> nextNode(LinkedBlockingDeque.Node<E> n)
/*      */     {
/* 1147 */       return n.prev;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class Itr extends LinkedBlockingDeque<E>.AbstractItr
/*      */   {
/*      */     private Itr()
/*      */     {
/* 1128 */       super();
/*      */     }
/* 1130 */     LinkedBlockingDeque.Node<E> firstNode() { return LinkedBlockingDeque.this.first; }
/*      */ 
/*      */     LinkedBlockingDeque.Node<E> nextNode(LinkedBlockingDeque.Node<E> n)
/*      */     {
/* 1134 */       return n.next;
/*      */     }
/*      */   }
/*      */ 
/*      */   private abstract class AbstractItr
/*      */     implements Iterator<E>
/*      */   {
/*      */     LinkedBlockingDeque.Node<E> next;
/*      */     E nextItem;
/*      */     private LinkedBlockingDeque.Node<E> lastRet;
/*      */ 
/*      */     abstract LinkedBlockingDeque.Node<E> firstNode();
/*      */ 
/*      */     abstract LinkedBlockingDeque.Node<E> nextNode(LinkedBlockingDeque.Node<E> paramNode);
/*      */ 
/*      */     AbstractItr()
/*      */     {
/* 1051 */       ReentrantLock lock = LinkedBlockingDeque.this.lock;
/* 1052 */       lock.lock();
/*      */       try {
/* 1054 */         this.next = firstNode();
/* 1055 */         this.nextItem = (this.next == null ? null : this.next.item);
/*      */       } finally {
/* 1057 */         lock.unlock();
/*      */       }
/*      */     }
/*      */ 
/*      */     private LinkedBlockingDeque.Node<E> succ(LinkedBlockingDeque.Node<E> n)
/*      */     {
/*      */       while (true)
/*      */       {
/* 1069 */         LinkedBlockingDeque.Node s = nextNode(n);
/* 1070 */         if (s == null)
/* 1071 */           return null;
/* 1072 */         if (s.item != null)
/* 1073 */           return s;
/* 1074 */         if (s == n) {
/* 1075 */           return firstNode();
/*      */         }
/* 1077 */         n = s;
/*      */       }
/*      */     }
/*      */ 
/*      */     void advance()
/*      */     {
/* 1085 */       ReentrantLock lock = LinkedBlockingDeque.this.lock;
/* 1086 */       lock.lock();
/*      */       try
/*      */       {
/* 1089 */         this.next = succ(this.next);
/* 1090 */         this.nextItem = (this.next == null ? null : this.next.item);
/*      */       } finally {
/* 1092 */         lock.unlock();
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean hasNext() {
/* 1097 */       return this.next != null;
/*      */     }
/*      */ 
/*      */     public E next() {
/* 1101 */       if (this.next == null)
/* 1102 */         throw new NoSuchElementException();
/* 1103 */       this.lastRet = this.next;
/* 1104 */       Object x = this.nextItem;
/* 1105 */       advance();
/* 1106 */       return x;
/*      */     }
/*      */ 
/*      */     public void remove() {
/* 1110 */       LinkedBlockingDeque.Node n = this.lastRet;
/* 1111 */       if (n == null)
/* 1112 */         throw new IllegalStateException();
/* 1113 */       this.lastRet = null;
/* 1114 */       ReentrantLock lock = LinkedBlockingDeque.this.lock;
/* 1115 */       lock.lock();
/*      */       try {
/* 1117 */         if (n.item != null)
/* 1118 */           LinkedBlockingDeque.this.unlink(n);
/*      */       } finally {
/* 1120 */         lock.unlock();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class Node<E>
/*      */   {
/*      */     E item;
/*      */     Node<E> prev;
/*      */     Node<E> next;
/*      */ 
/*      */     Node(E x)
/*      */     {
/*  104 */       this.item = x;
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.assist.deque.LinkedBlockingDeque
 * JD-Core Version:    0.6.0
 */
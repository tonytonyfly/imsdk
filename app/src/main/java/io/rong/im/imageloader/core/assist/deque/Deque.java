package io.rong.imageloader.core.assist.deque;

import java.util.Iterator;
import java.util.Queue;

public abstract interface Deque<E> extends Queue<E>
{
  public abstract void addFirst(E paramE);

  public abstract void addLast(E paramE);

  public abstract boolean offerFirst(E paramE);

  public abstract boolean offerLast(E paramE);

  public abstract E removeFirst();

  public abstract E removeLast();

  public abstract E pollFirst();

  public abstract E pollLast();

  public abstract E getFirst();

  public abstract E getLast();

  public abstract E peekFirst();

  public abstract E peekLast();

  public abstract boolean removeFirstOccurrence(Object paramObject);

  public abstract boolean removeLastOccurrence(Object paramObject);

  public abstract boolean add(E paramE);

  public abstract boolean offer(E paramE);

  public abstract E remove();

  public abstract E poll();

  public abstract E element();

  public abstract E peek();

  public abstract void push(E paramE);

  public abstract E pop();

  public abstract boolean remove(Object paramObject);

  public abstract boolean contains(Object paramObject);

  public abstract int size();

  public abstract Iterator<E> iterator();

  public abstract Iterator<E> descendingIterator();
}

/* Location:           C:\Users\tree\Desktop\Rong_IMKit_v2_7_2.jar
 * Qualified Name:     io.rong.imageloader.core.assist.deque.Deque
 * JD-Core Version:    0.6.0
 */
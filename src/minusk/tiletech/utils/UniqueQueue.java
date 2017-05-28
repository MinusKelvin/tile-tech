package minusk.tiletech.utils;

import java.util.*;

/**
 * @author MinusKelvin
 */
public class UniqueQueue<E> implements Queue<E> {
	private final Queue<E> queuepart = new LinkedList<>();
	private final Set<E> setpart;
	
	public UniqueQueue() {
		this(16);
	}
	
	public UniqueQueue(int capacity) {
		setpart = new HashSet<>(capacity);
	}
	
	@Override
	public int size() {
		return queuepart.size();
	}
	
	@Override
	public boolean isEmpty() {
		return queuepart.isEmpty();
	}
	
	@Override
	public boolean contains(Object o) {
		return setpart.contains(o);
	}
	
	@Override
	public Iterator<E> iterator() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean add(E e) {
		if (setpart.add(e)) {
			queuepart.add(e);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean remove(Object o) {
		queuepart.remove(o);
		return setpart.remove(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void clear() {
		queuepart.clear();
		setpart.clear();
	}
	
	@Override
	public boolean offer(E e) {
		return add(e);
	}
	
	@Override
	public E remove() {
		E ret = queuepart.remove();
		setpart.remove(ret);
		return ret;
	}
	
	@Override
	public E poll() {
		E ret = queuepart.poll();
		if (ret != null)
			setpart.remove(ret);
		return ret;
	}
	
	@Override
	public E element() {
		return queuepart.element();
	}
	
	@Override
	public E peek() {
		return queuepart.peek();
	}
}

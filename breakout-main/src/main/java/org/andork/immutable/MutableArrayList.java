package org.andork.immutable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import org.andork.immutable.ImmutableArrayList.ImmutableIterator;

public class MutableArrayList<E> implements MutableList<E> {
	volatile ArrayList<E> list;
	private volatile boolean detached = false;
	private volatile ImmutableArrayList<E> immutable;

	public MutableArrayList() {
		this.list = new ArrayList<>();
		this.detached = true;
	}

	MutableArrayList(ImmutableArrayList<E> immutable) {
		this.list = immutable.list;
		this.detached = false;
		this.immutable = immutable;
	}

	public void detach() {
		if (!detached) {
			detached = true;
			immutable = null;
			list = new ArrayList<>(list);
		}
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return new ImmutableIterator<>(list.iterator());
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public MutableList<E> add(E e) {
		detach();
		list.add(e);
		return this;
	}

	@Override
	public MutableList<E> remove(Object o) {
		int index = list.indexOf(o);
		if (index < 0) {
			return this;
		}
		detach();
		list.remove(index);
		return this;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public MutableList<E> addAll(Collection<? extends E> c) {
		if (!c.isEmpty()) {
			detach();
			list.addAll(c);
		}
		return this;
	}

	@Override
	public MutableList<E> addAll(int index, Collection<? extends E> c) {
		if (!c.isEmpty()) {
			detach();
			list.addAll(index, c);
		}
		return this;
	}

	@Override
	public MutableList<E> removeAll(Collection<?> c) {
		if (!c.isEmpty()) {
			detach();
			list.removeAll(c);
		}
		return this;
	}

	@Override
	public MutableList<E> retainAll(Collection<?> c) {
		detach();
		list.retainAll(c);
		return this;
	}

	@Override
	public E get(int index) {
		return list.get(index);
	}

	@Override
	public MutableList<E> set(int index, E element) {
		if (list.get(index) != element) {
			detach();
			set(index, element);
		}
		return this;
	}

	@Override
	public MutableList<E> add(int index, E element) {
		detach();
		list.add(index, element);
		return this;
	}

	@Override
	public MutableList<E> remove(int index) {
		detach();
		list.remove(index);
		return this;
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public MutableList<E> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public ImmutableList<E> toImmutable() {
		if (detached) {
			detached = false;
		}
		if (immutable == null) {
			immutable = new ImmutableArrayList<>(this);
		}
		return immutable;
	}
}

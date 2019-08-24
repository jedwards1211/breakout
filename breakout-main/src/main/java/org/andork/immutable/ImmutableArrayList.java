package org.andork.immutable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

public class ImmutableArrayList<E> implements ImmutableList<E> {
	ArrayList<E> list;

	ImmutableArrayList(MutableArrayList<E> mutable) {
		this.list = mutable.list;
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

	static class ImmutableIterator<E> implements Iterator<E> {
		Iterator<E> iterator;

		public ImmutableIterator(Iterator<E> iterator) {
			super();
			this.iterator = iterator;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public E next() {
			return iterator.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("List is immmutable");
		}
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
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public E get(int index) {
		return list.get(index);
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
	public ImmutableList<E> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public MutableList<E> toMutable() {
		return new MutableArrayList<>(this);
	}

}

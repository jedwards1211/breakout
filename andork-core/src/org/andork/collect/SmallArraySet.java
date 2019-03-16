package org.andork.collect;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;

public class SmallArraySet<E> extends AbstractSet<E> {
	private E[] elements;
	private int size;

	@SuppressWarnings("unchecked")
	public SmallArraySet(int capacity) {
		elements = (E[]) new Object[capacity];
	}
	
	@Override
	public boolean add(E e) {
		if (this.contains(e)) {
			return false;
		}
		if (size >= elements.length) {
			throw new IllegalStateException("set is full");
		}
		elements[size++] = e;
		return true;
	}
	
	

	@Override
	public void clear() {
		size = 0;
		Arrays.fill(elements, null);
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			int i = 0;

			@Override
			public boolean hasNext() {
				return i < size;
			}

			@Override
			public E next() {
				if (i >= size) {
					throw new IllegalStateException("end has been reached");
				}
				return elements[i++];
			}
		};
	}

	@Override
	public int size() {
		return size;
	}

}

package org.andork.collect;

import java.util.Iterator;

public class ArrayIterator<E> implements Iterator<E> {
	E[]	array;
	int	index;

	public ArrayIterator(E[] array) {
		super();
		this.array = array;
	}

	public boolean hasNext() {
		return index < array.length;
	}

	public E next() {
		return array[index++];
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}

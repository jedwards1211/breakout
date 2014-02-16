package org.andork.collect;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Makes writing an iterator easy -- all you have to do is implement one method:
 * {@link #nextOrNull()}. This is especially useful when it's cumbersome to
 * determine if {@link #hasNext()} without finding the next element (which is a
 * problem when one iterator presents a filtered view of a wrapped iterator).
 * The only drawback is that {@code null}s cannot be part of the iteration. The
 * {@link #remove()} method throws an {@link UnsupportedOperationException} by
 * default, but you can override it.
 * 
 * @author andy.edwards
 * 
 * @param <T>
 *            the element type.
 */
public abstract class EasyIterator<T> implements Iterator<T> {
	private boolean	finished;
	private T		next;

	/**
	 * @return the next element, or {@code null} if there are no more elements
	 *         to iterate over.
	 */
	protected abstract T nextOrNull();

	private void prepareNext() {
		if (!finished && next == null) {
			next = nextOrNull();
			finished = next == null;
		}
	}

	public boolean hasNext() {
		prepareNext();
		return !finished;
	}

	public T next() {
		prepareNext();
		if (next == null) {
			throw new NoSuchElementException();
		}
		T result = next;
		next = null;
		return result;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
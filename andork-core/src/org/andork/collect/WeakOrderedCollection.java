package org.andork.collect;

import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A {@link Collection} that weakly references its elements; they will cease to
 * belong to the collection once the garbage collector determines that there are
 * no more strong references to its elements. Iterators are guaranteed to return
 * the elements in the order they were added.
 *
 * @author Andy
 *
 * @param <E>
 *            the element type
 */
public class WeakOrderedCollection<E> extends AbstractCollection<E> {
	private class ExpungingIterator implements Iterator<E> {
		Iterator<WeakReference<E>> refIter = refs.iterator();

		E lastReturned;
		E nextElement;

		@Override
		public boolean hasNext() {
			while (nextElement == null) {
				if (!refIter.hasNext()) {
					return false;
				}

				nextElement = refIter.next().get();
				if (nextElement == null) {
					refIter.remove();
				}
			}
			return true;
		}

		@Override
		public E next() {
			if (nextElement == null && !hasNext()) {
				throw new NoSuchElementException();
			}

			lastReturned = nextElement;
			nextElement = null;
			return lastReturned;
		}

		@Override
		public void remove() {
			if (lastReturned == null) {
				throw new IllegalStateException();
			}

			refIter.remove();
			lastReturned = null;
		}
	}

	public static <E> WeakOrderedCollection<E> newArrayCollection() {
		return new WeakOrderedCollection<>(new ArrayList<>());
	}

	public static <E> WeakOrderedCollection<E> newLinkedCollection() {
		return new WeakOrderedCollection<>(new LinkedList<>());
	}

	private List<WeakReference<E>> refs;

	/**
	 * @param refs
	 *            the backing {@link List}. It should be empty, modifiable, and
	 *            not be modified by anything by this
	 *            {@code WeakOrderedCollection}.
	 */
	public WeakOrderedCollection(List<WeakReference<E>> refs) {
		this.refs = refs;
	}

	@Override
	public boolean add(E e) {
		return refs.add(new WeakReference<E>(e));
	}

	private void expungeStaleElements() {
		// the iterator will take care of expungement
		for (@SuppressWarnings("unused")
		Object o : this) {
			;
		}
	}

	@Override
	public Iterator<E> iterator() {
		return new ExpungingIterator();
	}

	@Override
	public int size() {
		expungeStaleElements();
		return refs.size();
	}
}

package org.andork.collect;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * An {@link Iterable} that concatenates the results from multiple
 * {@code Iterable}s together.
 * 
 * @author andy.edwards
 * 
 * @param <T>
 *            the element type.
 */
public class IterableChain<T> implements Iterable<T> {
	public IterableChain(Iterable<T>... chain) {
		this(Arrays.asList(chain));
	}

	public IterableChain(Collection<? extends Iterable<T>> chain) {
		this.chain = chain;
	}

	private final Iterable<? extends Iterable<T>>	chain;

	public Iterator<T> iterator() {
		return new ChainIterator();
	}

	private class ChainIterator implements Iterator<T> {
		public ChainIterator() {
			chainIter = chain.iterator();
		}

		Iterator<? extends Iterable<T>>	chainIter;
		Iterator<T>						linkIter;

		public boolean hasNext() {
			return (linkIter != null && linkIter.hasNext())
					|| chainIter.hasNext();
		}

		public T next() {
			while (linkIter == null || !linkIter.hasNext()) {
				linkIter = chainIter.next().iterator();
			}
			return linkIter.next();
		}

		public void remove() {
			linkIter.remove();
		}
	}
}

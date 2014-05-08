package org.andork.collect;

import java.util.Iterator;

public abstract class FilteringIterator<T> extends EasyIterator<T> {
	private final Iterator<T>	wrapped;

	public FilteringIterator(Iterator<T> wrapped) {
		this.wrapped = wrapped;
	}
	
	protected abstract boolean matches(T next);

	@Override
	protected final T nextOrNull() {
		while (wrapped.hasNext()) {
			T next = wrapped.next();
			if (matches(next)) {
				return next;
			}
		}
		return null;
	}

	@Override
	public void remove() {
		wrapped.remove();
	}
}

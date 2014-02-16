package org.andork.collect;

import java.util.Iterator;

import org.andork.func.Predicate;

public abstract class FilteringIterable<T> implements Iterable<T> {
	private final Iterable<T>	wrapped;
	
	public FilteringIterable(Iterable<T> wrapped) {
		super();
		this.wrapped = wrapped;
	}

	protected abstract boolean matches(T next);

	@Override
	public final Iterator<T> iterator() {
		return new FilteringIterator<T>(wrapped.iterator()) {
			@Override
			protected boolean matches(T next) {
				return FilteringIterable.this.matches(next);
			}
		};
	}
	
	public static <T> FilteringIterable<T> filter(Iterable<T> iterable, final Predicate<? super T> predicate) {
		return new FilteringIterable<T>(iterable) {
			@Override
			protected boolean matches(T next) {
				return predicate.eval(next);
			}
		};
	}
	
	public static <T> FilteringIterable<T> filter(Iterable<? super T> iterable, final Class<T> cls) {
		return new FilteringIterable(iterable) {
			@Override
			protected boolean matches(Object next) {
				return next != null && cls.isAssignableFrom(next.getClass());
			}
		};
	}
}

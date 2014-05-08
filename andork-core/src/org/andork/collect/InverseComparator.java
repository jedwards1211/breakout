package org.andork.collect;

import java.util.Comparator;

public final class InverseComparator<T> implements Comparator<T> {
	private Comparator<? super T>	wrapped;

	public InverseComparator(Comparator<? super T> wrapped) {
		super();
		this.wrapped = wrapped;
	}

	@Override
	public int compare(T o1, T o2) {
		return -wrapped.compare(o1, o2);
	}
}

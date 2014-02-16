package org.andork.collect;

import java.util.Comparator;

/**
 * Combines multiple comparators in order if priority. If items are equal
 * according to the first, they'll be compared with the second, if they're equal
 * according to the second, they'll be compared according to the third, and so
 * on.
 * 
 * @author andy.edwards
 * 
 * @param <T>
 */
public class CompoundComparator<T> implements Comparator<T> {
	Comparator<? super T>[]	comparators;

	public CompoundComparator(Comparator<? super T>... comparators) {
		super();
		this.comparators = comparators;
	}

	@Override
	public int compare(T o1, T o2) {
		for (Comparator<? super T> comparator : comparators) {
			int result = comparator.compare(o1, o2);
			if (result != 0) {
				return result;
			}
		}
		return 0;
	}

}

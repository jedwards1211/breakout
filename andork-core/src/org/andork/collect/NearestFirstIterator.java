/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.collect;

import java.util.AbstractSequentialList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Iterates through a SORTED list, returning elements that are closer to a fixed
 * value first.
 * 
 * @author andy.edwards
 * 
 * @param <E>
 *            the list element type.
 */
public class NearestFirstIterator<E extends Comparable<E>> implements Iterator<E> {
	private NearnessComparator<E>	comparator;
	private ListIterator<E>			lo;
	private ListIterator<E>			hi;
	private E						midValue;

	/**
	 * Creates a {@link NearestFirstIterator} that will iterate over the given
	 * list.
	 * 
	 * @param list
	 *            the list to iterate over. MUST BE SORTED in ascending order,
	 *            or undefined behavior will result!
	 * @param midValue
	 *            elements that are closer to this value will be returned by
	 *            {@link #next()} first.
	 * @param comparator
	 *            used to determine the next closest element to {@code midValue}
	 *            .
	 */
	public NearestFirstIterator(List<E> list, E midValue, NearnessComparator<E> comparator) {
		this.comparator = comparator;
		this.midValue = midValue;

		if (list instanceof AbstractSequentialList) {
			hi = list.listIterator();
			lo = list.listIterator();

			while (hi.hasNext()) {
				if (hi.next().compareTo(midValue) >= 0) {
					hi.previous();
					break;
				}
				lo.next();
			}
		} else {
			int hiIndex = Collections.binarySearch(list, midValue);
			if (hiIndex < 0) {
				hiIndex = -(hiIndex + 1);
			}
			hi = list.listIterator(hiIndex);
			lo = list.listIterator(hiIndex);
		}
	}

	public boolean hasNext() {
		return hi.hasNext() || lo.hasPrevious();
	}

	public E next() {
		if (!hasNext()) {
			throw new IllegalStateException("The end has already been reached");
		}

		if (lo.hasPrevious() && hi.hasNext()) {
			E loValue = lo.previous();
			E hiValue = hi.next();
			if (comparator.compare(loValue, midValue, hiValue) < 0) {
				hi.previous();
				return loValue;
			} else {
				lo.next();
				return hiValue;
			}
		} else if (lo.hasPrevious()) {
			return lo.previous();
		} else {
			return hi.next();
		}
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates an {@link Iterable} that will return a
	 * {@link NearestFirstIterator}.
	 * 
	 * @param list
	 *            the list to iterate over. MUST BE SORTED in ascending order,
	 *            or undefined behavior will result!
	 * @param midValue
	 *            elements that are closer to this value will be returned by
	 *            {@link #next()} first.
	 * @param comparator
	 *            used to determine the next closest element to {@code midValue}
	 *            .
	 * @return an {@link Iterable} whose {@link Iterable#iterator() iterator()}
	 *         method will return a {@link NearestFirstIterator}.
	 */
	public static <E extends Comparable<E>> Iterable<E> iterable(final List<E> list, final E midValue, final NearnessComparator<E> comparator) {
		return new Iterable<E>() {
			public Iterator<E> iterator() {
				return new NearestFirstIterator<E>(list, midValue, comparator);
			}
		};
	}

	/**
	 * Creates an {@link Iterable} that will return a
	 * {@link NearestFirstIterator}.
	 * 
	 * @param list
	 *            the list to iterate over. MUST BE SORTED in ascending order,
	 *            or undefined behavior will result!
	 * @param midValue
	 *            elements that are closer to this value will be returned by
	 *            {@link #next()} first.
	 * @return an {@link Iterable} whose {@link Iterable#iterator() iterator()}
	 *         method will return a {@link NearestFirstIterator}.
	 */
	public static Iterable<Double> iterable(final List<Double> list, final double midValue) {
		return new Iterable<Double>() {
			public Iterator<Double> iterator() {
				return new NearestFirstIterator<Double>(list, midValue, DoubleNearnessComparator.INSTANCE);
			}
		};
	}

	/**
	 * Determines which of two values is closer to a mid value.
	 * 
	 * @author andy.edwards
	 * 
	 * @param <E>
	 *            the value type.
	 */
	public static interface NearnessComparator<E extends Comparable<E>> {
		/**
		 * Determines which of two values is closer to a mid value.
		 * 
		 * @param lo
		 *            the low value; {@code lo <= mid <= hi}
		 * @param mid
		 *            the mid value; {@code lo <= mid <= hi}
		 * @param hi
		 *            the high value; {@code lo <= mid <= hi}
		 * @return {@code < 0} if {@code lo} is closer to {@code mid} than
		 *         {@code hi} is to {@code mid}; {@code > 0} if {@code hi} is
		 *         closer; and {@code 0} if they are equally close.
		 */
		public int compare(E lo, E mid, E hi);
	}

	public static class DoubleNearnessComparator implements NearnessComparator<Double> {
		private DoubleNearnessComparator() {
		}

		public static final DoubleNearnessComparator	INSTANCE	= new DoubleNearnessComparator();

		public int compare(Double lo, Double mid, Double hi) {
			return Double.compare(mid - lo, hi - mid);
		}
	}
}
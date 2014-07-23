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

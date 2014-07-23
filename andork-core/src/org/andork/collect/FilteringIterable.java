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

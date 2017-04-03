/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.util;

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
	private boolean finished;
	private T next;

	@Override
	public boolean hasNext() {
		prepareNext();
		return !finished;
	}

	@Override
	public T next() {
		prepareNext();
		if (next == null) {
			throw new NoSuchElementException();
		}
		T result = next;
		next = null;
		return result;
	}

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

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}

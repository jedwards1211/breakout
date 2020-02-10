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
package org.andork.collect;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

public class Iterables {
	public static <E> void addAll(Iterable<? extends E> iterable, Collection<E> collection) {
		Iterators.addAll(iterable.iterator(), collection);
	}

	public static <E> Iterable<E> of(final Iterator<E> iterator) {
		return () -> iterator;
	}

	public static Iterable<Float> range(
		final float start,
		final float end,
		final boolean includeEnd,
		final float step) {
		return () -> Iterators.range(start, end, includeEnd, step);
	}

	public static <E> ArrayList<E> toArrayList(Iterable<E> iterable) {
		return Iterators.toArrayList(iterable.iterator());
	}

	private Iterables() {

	}

	private static class ConcatIterator<E> implements Iterator<E> {
		Iterable<? extends E>[] iterables;
		int index = 0;
		Iterator<? extends E> forRemove;
		Iterator<? extends E> current;

		@SafeVarargs
		public ConcatIterator(Iterable<? extends E>... iterables) {
			this.iterables = iterables;
			current = iterables.length > 0 ? iterables[0].iterator() : null;
		}

		@Override
		public boolean hasNext() {
			return current != null && current.hasNext();
		}

		@Override
		public E next() {
			E result = current.next();
			forRemove = current;
			if (!current.hasNext() && ++index < iterables.length) {
				current = iterables[index].iterator();
			}
			return result;
		}

		@Override
		public void remove() {
			if (forRemove == null) {
				throw new IllegalStateException("you must call next() first");
			}
			forRemove.remove();
		}
	}

	@SafeVarargs
	public static <E> Iterable<E> concat(Iterable<? extends E>... iterables) {
		return () -> new ConcatIterator<>(iterables);
	}

	@SafeVarargs
	public static <E> Iterable<E> of(E... array) {
		return () -> Iterators.of(array);
	}

	public static <E> Iterable<E> of(Enumeration<? extends E> enumeration) {
		return () -> Iterators.of(enumeration);
	}

	public static <T> Iterable<T> filter(Iterable<? extends T> iterable, Predicate<? super T> predicate) {
		return () -> Iterators.filter(iterable.iterator(), predicate);
	}

	public static Iterable<String> linesOf(Reader reader) {
		return () -> Iterators.linesOf(reader);
	}

	public static Iterable<String> linesOf(InputStream in) {
		return () -> Iterators.linesOf(new InputStreamReader(in));
	}

	public static <I, O> Iterable<O> map(Iterable<? extends I> in, Function<? super I, ? extends O> iteratee) {
		return () -> Iterators.map(in.iterator(), iteratee);
	}

	public static <I, O> Iterable<O> map(I[] in, Function<? super I, ? extends O> iteratee) {
		return () -> Iterators.map(in, iteratee);
	}

	public static <E> void removeAll(Iterable<? extends E> i, Predicate<? super E> p) {
		Iterators.removeAll(i.iterator(), p);
	}

	public static <T> Iterable<T> flatten(Iterable<Iterable<T>> iterable) {
		return () -> Iterators.flatten(iterable.iterator());
	}

	public static <T> T min(Iterable<T> iterable, Function<T, Float> getScore) {
		float bestScore = Float.NaN;
		T best = null;
		for (T item : iterable) {
			float score = getScore.apply(item);
			if (best == null || score < bestScore) {
				bestScore = score;
				best = item;
			}
		}
		return best;
	}
}

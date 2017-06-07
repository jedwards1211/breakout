package org.andork.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

public class Iterators {
	public static <E> void addAll(Iterator<? extends E> iterator, Collection<E> collection) {
		while (iterator.hasNext()) {
			collection.add(iterator.next());
		}
	}

	public static Iterator<Float> range(final float start, final float end, final boolean includeEnd,
			final float step) {
		return new Iterator<Float>() {
			float next = start;

			@Override
			public boolean hasNext() {
				return next < end || includeEnd && next == end;
			}

			@Override
			public Float next() {
				float result = next;

				if (includeEnd && next < end) {
					next = Math.min(next + step, end);
				} else {
					next += step;
				}

				return result;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public static <E> ArrayList<E> toArrayList(Iterator<E> iterator) {
		ArrayList<E> result = new ArrayList<>();
		addAll(iterator, result);
		return result;
	}

	private Iterators() {

	}

	private static class ConcatIterator<E> implements Iterator<E> {
		Iterator<? extends E>[] iterators;
		int index = 0;
		Iterator<? extends E> forRemove;
		Iterator<? extends E> current;

		@SafeVarargs
		public ConcatIterator(Iterator<? extends E>... iterators) {
			this.iterators = iterators;
			current = iterators.length > 0 ? iterators[0] : null;
		}

		@Override
		public boolean hasNext() {
			return current != null && current.hasNext();
		}

		@Override
		public E next() {
			E result = current.next();
			forRemove = current;
			if (!current.hasNext() && ++index < iterators.length) {
				current = iterators[index];
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
	public static <E> Iterator<E> concat(Iterator<? extends E>... iterators) {
		return new ConcatIterator<>(iterators);
	}

	private static class ArrayIterator<E> implements Iterator<E> {
		E[] array;
		int index;

		public ArrayIterator(E[] array) {
			super();
			this.array = array;
		}

		@Override
		public boolean hasNext() {
			return index < array.length;
		}

		@Override
		public E next() {
			return array[index++];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	@SafeVarargs
	public static <E> Iterator<E> of(E... array) {
		return new ArrayIterator<>(array);
	}

	private static class EnumerationIterator<T> implements Iterator<T> {
		private final Enumeration<? extends T> enumeration;

		public EnumerationIterator(Enumeration<? extends T> enumeration) {
			super();
			this.enumeration = enumeration;
		}

		@Override
		public boolean hasNext() {
			return enumeration.hasMoreElements();
		}

		@Override
		public T next() {
			return enumeration.nextElement();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public static <E> Iterator<E> of(Enumeration<? extends E> enumeration) {
		return new EnumerationIterator<>(enumeration);
	}

	private static class FilteringIterator<T> extends EasyIterator<T> {
		private final Iterator<? extends T> wrapped;
		private final Predicate<? super T> predicate;

		public FilteringIterator(Iterator<? extends T> wrapped, Predicate<? super T> predicate) {
			this.wrapped = wrapped;
			this.predicate = predicate;
		}

		@Override
		protected final T nextOrNull() {
			while (wrapped.hasNext()) {
				T next = wrapped.next();
				if (predicate.test(next)) {
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

	public static <T> Iterator<T> filter(Iterator<? extends T> iterator, Predicate<? super T> predicate) {
		return new FilteringIterator<>(iterator, predicate);
	}

	private static class LineIterator extends EasyIterator<String> {
		private BufferedReader reader;

		public LineIterator(Reader reader) {
			if (reader instanceof BufferedReader) {
				this.reader = (BufferedReader) reader;
			} else {
				try {
					this.reader = new BufferedReader(reader);
				} catch (Exception ex) {

				}
			}
		}

		@Override
		public void finalize() {
			try {
				reader.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			reader = null;
		}

		@Override
		protected String nextOrNull() {
			String result = null;
			if (reader != null) {
				try {
					result = reader.readLine();
				} catch (IOException e) {
				} finally {
					if (result == null) {
						finalize();
					}
				}
			}
			return result;
		}
	}

	public static Iterator<String> linesOf(Reader reader) {
		return new LineIterator(reader);
	}

	public static Iterator<String> linesOf(InputStream in) {
		return new LineIterator(new InputStreamReader(in));
	}

	private static class MapIterator<I, O> implements Iterator<O> {
		private final Iterator<I> in;
		private final Function<I, O> iteratee;
		
		public MapIterator(Iterator<I> in, Function<I, O> iteratee) {
			super();
			this.in = in;
			this.iteratee = iteratee;
		}

		@Override
		public void remove() {
			in.remove();
		}

		@Override
		public boolean hasNext() {
			return in.hasNext();
		}

		@Override
		public O next() {
			return iteratee.apply(in.next());
		}
	}
	
	public static <I, O> Iterator<O> map(Iterator<I> in, Function<I, O> iteratee) {
		return new MapIterator<>(in, iteratee);
	}
}

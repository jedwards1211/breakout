package org.andork.collect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;

public class ArrayLists {
	public static <T> ArrayList<T> of(Iterable<? extends T> i) {
		ArrayList<T> result = new ArrayList<T>();
		for (T element : i) {
			result.add(element);
		}
		return result;
	}

	public static <T> ArrayList<T> of(Stream<T> stream) {
		ArrayList<T> result = new ArrayList<T>();
		stream.forEach(t -> result.add(t));
		return result;
	}

	@SafeVarargs
	public static <T> ArrayList<T> of(T... elements) {
		ArrayList<T> result = new ArrayList<T>();
		for (T elem : elements) {
			result.add(elem);
		}
		return result;
	}

	/**
	 * @return an {@link ArrayList} containing the elements in the given {@link Iterable} in sorted order.
	 */
	public static <T> ArrayList<T> sortedOf(Iterable<? extends T> i, Comparator<? super T> comparator) {
		ArrayList<T> result = of(i);
		Collections.sort(result, comparator);
		return result;
	}

	public static <I, O> ArrayList<O> map(I[] in, Function<? super I, ? extends O> iteratee) {
		ArrayList<O> result = new ArrayList<>(in.length);
		for (I element : in) {
			result.add(iteratee.apply(element));
		}
		return result;
	}

	public static <I, O> ArrayList<O> map(Iterable<? extends I> in, Function<? super I, ? extends O> iteratee) {
		return of(Iterables.map(in, iteratee));
	}
}

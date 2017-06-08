package org.andork.collect;

import java.util.HashSet;
import java.util.stream.Stream;

public class HashSets {
	public static <T> HashSet<T> of(Iterable<? extends T> in) {
		HashSet<T> result = new HashSet<T>();
		for (T element : in) {
			result.add(element);
		}
		return result;
	}

	public static <T> HashSet<T> of(Stream<? extends T> stream) {
		HashSet<T> result = new HashSet<T>();
		stream.forEach(t -> result.add(t));
		return result;
	}

	@SafeVarargs
	public static <T> HashSet<T> of(T... elements) {
		HashSet<T> result = new HashSet<T>();
		for (T elem : elements) {
			result.add(elem);
		}
		return result;
	}
}

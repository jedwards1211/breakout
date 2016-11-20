package org.andork.func;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.andork.collect.LinkedHashSetMultiMap;
import org.andork.collect.MultiMap;

public class Lodash {
	public static <K, V> void forEach(Map<? extends K, ? extends V> c,
			BiConsumer<? super V, ? super K> iteratee) {
		for (Entry<? extends K, ? extends V> e : c.entrySet()) {
			iteratee.accept(e.getValue(), e.getKey());
		}
	}

	public static <K, V> int forEach(Map<? extends K, ? extends V> c,
			BiFunction<? super V, ? super K, Boolean> iteratee) {
		int count = 0;
		for (Entry<? extends K, ? extends V> e : c.entrySet()) {
			count++;
			if (!iteratee.apply(e.getValue(), e.getKey())) {
				break;
			}
		}
		return count;
	}

	public static <E> void forEach(E[] array, BiConsumer<? super E, Integer> iteratee) {
		forEach(Arrays.asList(array), iteratee);
	}

	public static <E> void forEach(Collection<? extends E> c, BiConsumer<? super E, Integer> iteratee) {
		int count = 0;
		for (E elem : c) {
			iteratee.accept(elem, count++);
		}
	}

	public static <E> int forEach(E[] array, BiFunction<? super E, Integer, Boolean> iteratee) {
		return forEach(Arrays.asList(array), iteratee);
	}

	public static <E> int forEach(Collection<? extends E> c, BiFunction<? super E, Integer, Boolean> iteratee) {
		int count = 0;
		for (E elem : c) {
			if (!iteratee.apply(elem, count++)) {
				break;
			}
		}
		return count;
	}

	public static <I, O> List<O> map(I[] in, Function<? super I, ? extends O> mapper) {
		return map(Stream.of(in), mapper);
	}

	public static <I, O> List<O> map(Collection<? extends I> in, Function<? super I, ? extends O> mapper) {
		return map(in.stream(), mapper);
	}

	@SuppressWarnings("unchecked")
	public static <I, O> List<O> map(Stream<? extends I> in, Function<? super I, ? extends O> mapper) {
		return (List<O>) Arrays.asList(in.map(mapper).toArray());
	}

	public static <K, V> Map<K, V> keyBy(V[] array, Function<? super V, ? extends K> keyAssigner) {
		return keyBy(Stream.of(array), keyAssigner);
	}

	public static <K, V> Map<K, V> keyBy(Collection<? extends V> c, Function<? super V, ? extends K> keyAssigner) {
		return keyBy(c.stream(), keyAssigner);
	}

	public static <K, V> Map<K, V> keyBy(Stream<? extends V> stream, Function<? super V, ? extends K> keyAssigner) {
		Map<K, V> result = new LinkedHashMap<>();
		stream.forEach(v -> result.put(keyAssigner.apply(v), v));
		return result;
	}

	public static <K, V> MultiMap<K, V> groupBy(V[] array,
			Function<? super V, ? extends K> keyAssigner) {
		return groupBy(Stream.of(array), keyAssigner);
	}

	public static <K, V> MultiMap<K, V> groupBy(Collection<? extends V> collection,
			Function<? super V, ? extends K> keyAssigner) {
		return groupBy(collection.stream(), keyAssigner);
	}

	public static <K, V> MultiMap<K, V> groupBy(Stream<? extends V> stream,
			Function<? super V, ? extends K> keyAssigner) {
		MultiMap<K, V> result = new LinkedHashSetMultiMap<>();
		stream.forEach(v -> result.put(keyAssigner.apply(v), v));
		return result;
	}
}

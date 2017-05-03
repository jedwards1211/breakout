package org.andork.func;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.andork.collect.LinkedHashSetMultiMap;
import org.andork.collect.MultiMap;
import org.omg.CORBA.IntHolder;

/**
 * Excellent stuff adapted from <a href="https://lodash.com/">Lodash</a>.
 */
public class Lodash {
	public static class DebounceOptions<O> implements Cloneable {
		private Long maxWait;
		private boolean leading = false;
		private boolean trailing = true;
		private BiFunction<Runnable, Long, Future<O>> setTimeout;

		public DebounceOptions<O> maxWait(long maxWait) {
			this.maxWait = maxWait;
			return this;
		}

		public DebounceOptions<O> leading(boolean leading) {
			this.leading = leading;
			return this;
		}

		public DebounceOptions<O> trailing(boolean trailing) {
			this.trailing = trailing;
			return this;
		}

		public DebounceOptions<O> setTimeout(BiFunction<Runnable, Long, Future<O>> setTimeout) {
			this.setTimeout = setTimeout;
			return this;
		}

		public BiFunction<Runnable, Long, Future<O>> setTimeout() {
			return setTimeout;
		}

		@SuppressWarnings("unchecked")
		public DebounceOptions<O> executor(ScheduledExecutorService executor) {
			this.setTimeout = (r, wait) -> (Future<O>) executor.schedule(r, wait, TimeUnit.MILLISECONDS);
			return this;
		}
		
		@SuppressWarnings("unchecked")
		public DebounceOptions<O> clone() {
			try {
				return (DebounceOptions<O>) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static interface Debounced<O> {
		public void cancel();

		public void cancel(boolean mayInterruptIfRunning);

		public O flush();
		
		public static <O> Debounced<O> noop() {
			return noop(null);
		}

		public static <O> Debounced<O> noop(O flushValue) {
			return new Debounced<O>() {
				@Override
				public void cancel() {
				}

				@Override
				public void cancel(boolean mayInterruptIfRunning) {
				}

				@Override
				public O flush() {
					return flushValue;
				}
			};
		}
	}

	public static interface DebouncedRunnable extends Runnable, Debounced<Void> {
		public static DebouncedRunnable noop() {
			return new DebouncedRunnable() {
				@Override
				public void cancel() {
				}

				@Override
				public void cancel(boolean mayInterruptIfRunning) {
				}

				@Override
				public Void flush() {
					return null;
				}

				@Override
				public void run() {
				}
			};
		}
	}

	public static interface DebouncedBiFunction<A, B, O> extends BiFunction<A, B, O>, Debounced<O> {
	}

	public static interface DebouncedFunction<I, O> extends Function<I, O>, Debounced<O> {
	}

	private static class DebouncedWrapper<O> implements Debounced<O> {
		final Debounced<O> wrapped;

		public DebouncedWrapper(Debounced<O> wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public void cancel() {
			wrapped.cancel();
		}

		@Override
		public void cancel(boolean mayInterruptIfRunning) {
			wrapped.cancel(mayInterruptIfRunning);
		}

		@Override
		public O flush() {
			return wrapped.flush();
		}
	}

	public static DebouncedRunnable debounce(Runnable fn, long wait, DebounceOptions<Void> options) {
		DebouncedBiFunction<Object, Object, Void> debounced = debounce((a, b) -> {
			fn.run();
			return null;
		}, wait, options);

		class Result extends DebouncedWrapper<Void> implements DebouncedRunnable {
			public Result() {
				super(debounced);
			}

			@Override
			public void run() {
				debounced.apply(null, null);
			}
		}
		return new Result();
	}

	public static <I, O> DebouncedFunction<I, O> debounce(Function<I, O> fn, long wait,
			DebounceOptions<O> options) {
		DebouncedBiFunction<I, Void, O> debounced = debounce((a, b) -> fn.apply(a), wait, options);

		class Result extends DebouncedWrapper<O> implements DebouncedFunction<I, O> {
			public Result() {
				super(debounced);
			}

			@Override
			public O apply(I t) {
				return debounced.apply(t, null);
			}
		}
		return new Result();
	}

	public static <A, B, O> DebouncedBiFunction<A, B, O> debounce(BiFunction<A, B, O> fn, long wait,
			DebounceOptions<O> options) {
		class Result implements DebouncedBiFunction<A, B, O> {
			A lastA;
			B lastB;
			boolean hasLastArgs = false;
			O result;
			Future<O> future;
			long lastCallTime = -1;
			long lastInvokeTime = 0;
			boolean maxing = options.maxWait != null;

			@Override
			public O apply(A a, B b) {
				long time = System.currentTimeMillis();
				boolean isInvoking = shouldInvoke(time);

				lastA = a;
				lastB = b;
				hasLastArgs = true;
				lastCallTime = time;

				if (isInvoking) {
					if (future == null) {
						return leadingEdge(lastCallTime);
					}
					if (maxing) {
						// Handle invocations in a tight loop.
						future = options.setTimeout.apply(this::timerExpired, wait);
						return invokeFunc(lastCallTime);
					}
				}
				if (future == null) {
					future = options.setTimeout.apply(this::timerExpired, wait);
				}
				return result;
			}

			O invokeFunc(long time) {
				A a = lastA;
				B b = lastB;
				lastA = null;
				lastB = null;
				hasLastArgs = false;
				lastInvokeTime = time;
				result = fn.apply(a, b);
				return result;
			}

			O leadingEdge(long time) {
				// Reset any `maxWait` timer.
				lastInvokeTime = time;
				// Start the timer for the trailing edge.
				future = options.setTimeout.apply(this::timerExpired, wait);
				// Invoke the leading edge.
				return options.leading ? invokeFunc(time) : result;
			}

			boolean shouldInvoke(long time) {
				long timeSinceLastCall = time - lastCallTime;
				long timeSinceLastInvoke = time - lastInvokeTime;

				// Either this is the first call, activity has stopped and we're
				// at the
				// trailing edge, the system time has gone backwards and we're
				// treating
				// it as the trailing edge, or we've hit the `maxWait` limit.
				return lastCallTime < 0 || timeSinceLastCall >= wait ||
						timeSinceLastCall < 0 || maxing && timeSinceLastInvoke >= options.maxWait;
			}

			long remainingWait(long time) {
				long timeSinceLastCall = time - lastCallTime;
				long timeSinceLastInvoke = time - lastInvokeTime;
				long result = wait - timeSinceLastCall;
				return maxing ? Math.min(result, options.maxWait - timeSinceLastInvoke) : result;
			}

			O timerExpired() {
				long time = System.currentTimeMillis();
				if (shouldInvoke(time)) {
					return trailingEdge(time);
				}
				// Restart the timer.
				future = options.setTimeout.apply(this::timerExpired, remainingWait(time));
				return null;
			}

			O trailingEdge(long time) {
				future = null;

				// Only invoke if we have `lastArgs` which means `func` has been
				// debounced at least once.
				if (options.trailing && hasLastArgs) {
					return invokeFunc(time);
				}
				hasLastArgs = false;
				lastA = null;
				lastB = null;
				return result;
			}

			@Override
			public void cancel() {
				cancel(false);
			}

			@Override
			public void cancel(boolean mayInterruptIfRunning) {
				if (future != null) {
					future.cancel(mayInterruptIfRunning);
				}
				lastInvokeTime = 0;
				hasLastArgs = false;
				lastA = null;
				lastB = null;
				lastCallTime = -1;
				future = null;
			}

			@Override
			public O flush() {
				return future == null ? result : trailingEdge(System.currentTimeMillis());
			}
		}
		return new Result();
	}

	public static DebouncedRunnable throttle(Runnable r, long wait, DebounceOptions<Void> options) {
		return debounce(r, wait, options.clone().maxWait(wait));
	}

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

	public static <E> void forEach(Stream<? extends E> c, BiConsumer<? super E, Integer> iteratee) {
		IntHolder count = new IntHolder(0);
		c.forEach(elem -> iteratee.accept(elem, count.value++));
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

	@SuppressWarnings("unchecked")
	public static <I, O> O[] map(I[] in, Function<? super I, ? extends O> mapper) {
		return (O[]) map(Stream.of(in), mapper).toArray();
	}

	public static <I, O> List<O> map(Collection<? extends I> in, Function<? super I, ? extends O> mapper) {
		List<O> out;
		if (in instanceof LinkedList) {
			out = new LinkedList<>();
		} else {
			out = new ArrayList<>(in.size());
		}
		for (I i : in) {
			out.add(mapper.apply(i));
		}
		return out;
	}

	public static <I, O> Stream<O> map(Stream<? extends I> in, Function<? super I, ? extends O> mapper) {
		return in.map(mapper);
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

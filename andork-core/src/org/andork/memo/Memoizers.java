package org.andork.memo;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

public class Memoizers<T> {
	private Memoizers() {

	}

	public static interface MemoizeCache<I, O> {
		boolean containsKey(I key);

		O get(I key);

		O put(I key, O value);
	}

	public static class BasicMemoizeCache<I, O> implements MemoizeCache<I, O> {
		boolean hasEntry = false;
		I key;
		O value;

		protected boolean keysEqual(I a, I b) {
			return Objects.equals(a, b);
		}

		@Override
		public boolean containsKey(I key) {
			return hasEntry && keysEqual(key, this.key);
		}

		@Override
		public O get(I key) {
			return containsKey(key) ? value : null;
		}

		@Override
		public O put(I key, O value) {
			O result = value;
			hasEntry = true;
			this.key = key;
			this.value = value;
			return result;
		}
	}

	@SuppressWarnings("serial")
	public static class DefaultMemoizeCache<I, O> extends HashMap<I, O> implements MemoizeCache<I, O> {
	}

	public static class MemoizedFunction<I, O> implements Function<I, O> {
		private Function<I, O> fn;
		private MemoizeCache<I, O> cache;

		public MemoizedFunction(Function<I, O> fn) {
			this(fn, new DefaultMemoizeCache<I, O>());
		}

		public MemoizedFunction(Function<I, O> fn, MemoizeCache<I, O> cache) {
			super();
			this.fn = fn;
			this.cache = cache;
		}

		public MemoizeCache<I, O> cache() {
			return cache;
		}

		public MemoizedFunction<I, O> cache(MemoizeCache<I, O> cache) {
			this.cache = cache;
			return this;
		}

		@Override
		public O apply(I in) {
			if (cache.containsKey(in))
				return cache.get(in);
			O out = fn.apply(in);
			cache.put(in, out);
			return out;
		}
	}

	public static <I, O> MemoizedFunction<I, O> memoize(Function<I, O> fn) {
		return new MemoizedFunction<>(fn);
	}
}

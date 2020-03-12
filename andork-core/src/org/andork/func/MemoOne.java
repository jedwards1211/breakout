package org.andork.func;

import java.util.function.Function;

import org.andork.util.Java7;

public class MemoOne<I, O> implements Function<I, O> {
	Function<I, O> fn;
	boolean called = false;
	I lastInput = null;
	O lastOutput = null;

	private MemoOne(Function<I, O> fn) {
		this.fn = fn;
	}

	public static <I, O> Function<I, O> memoOne(Function<I, O> fn) {
		return new MemoOne<>(fn);
	}

	@Override
	public O apply(I input) {
		if (called && Java7.Objects.equals(lastInput, input)) {
			return lastOutput;
		}
		called = true;
		return lastOutput = fn.apply(lastInput = input);
	}
}

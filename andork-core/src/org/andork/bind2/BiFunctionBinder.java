package org.andork.bind2;

import java.util.function.BiFunction;

public class BiFunctionBinder<A, B, O> extends CachingBinder<O> implements Binding {
	public final Link<A> inputALink = new Link<A>(this);
	public final Link<B> inputBLink = new Link<B>(this);
	public final BiFunction<A, B, O> fn;

	public BiFunctionBinder(BiFunction<A, B, O> fn) {
		super();
		this.fn = fn;
	}

	public BiFunctionBinder(Binder<A> inputA, Binder<B> inputB, BiFunction<A, B, O> fn) {
		this(fn);
		inputALink.bind(inputA);
		inputBLink.bind(inputB);
	}

	@Override
	public void update(boolean force) {
		set(fn.apply(inputALink.get(), inputBLink.get()), force);
	}
}

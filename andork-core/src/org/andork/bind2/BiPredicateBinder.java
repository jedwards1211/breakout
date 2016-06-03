package org.andork.bind2;

import java.util.function.BiPredicate;

public class BiPredicateBinder<A, B> extends CachingBinder<Boolean> implements Binding {
	public final Link<A> inputALink = new Link<A>(this);
	public final Link<B> inputBLink = new Link<B>(this);
	public final BiPredicate<A, B> p;

	public BiPredicateBinder(Binder<A> inputA, Binder<B> inputB, BiPredicate<A, B> p) {
		this(p);
		inputALink.bind(inputA);
		inputBLink.bind(inputB);
	}

	public BiPredicateBinder(BiPredicate<A, B> p) {
		super();
		this.p = p;
	}

	@Override
	public void update(boolean force) {
		set(p.test(inputALink.get(), inputBLink.get()), force);
	}
}

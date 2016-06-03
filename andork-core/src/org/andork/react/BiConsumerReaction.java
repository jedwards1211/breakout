package org.andork.react;

import java.util.function.BiConsumer;

public class BiConsumerReaction<T, U> extends Reaction<Void> {
	private Reactable<? extends T> t;
	private Reactable<? extends U> u;
	private BiConsumer<? super T, ? super U> consumer;

	public BiConsumerReaction(Reactable<? extends T> t, Reactable<? extends U> u,
			BiConsumer<? super T, ? super U> consumer) {
		this.t = t;
		this.u = u;
		this.consumer = consumer;

		t.bind(this);
		u.bind(this);
	}

	@Override
	protected Void calculate() {
		consumer.accept(t.get(), u.get());
		return null;
	}
}

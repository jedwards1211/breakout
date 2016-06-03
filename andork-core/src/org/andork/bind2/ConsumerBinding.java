package org.andork.bind2;

import java.util.function.Consumer;

public class ConsumerBinding<T> implements Binding {
	public final Link<T> inputLink = new Link<T>(this);
	public final Consumer<T> consumer;

	public ConsumerBinding(Binder<? extends T> input, Consumer<T> consumer) {
		this(consumer);
		inputLink.bind(input);
	}

	public ConsumerBinding(Consumer<T> consumer) {
		this.consumer = consumer;
	}

	@Override
	public void update(boolean force) {
		consumer.accept(inputLink.get());
	}
}

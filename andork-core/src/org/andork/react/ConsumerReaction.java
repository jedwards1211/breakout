package org.andork.react;

import java.util.function.Consumer;

public class ConsumerReaction<T> extends Reaction<Void>
{
	private final Reactable<? extends T> input;
	private final Consumer<? super T> consumer;

	public ConsumerReaction( Reactable<? extends T> input , Consumer<? super T> consumer )
	{
		this.consumer = consumer;
		this.input = input;
		input.bind( this );
	}

	@Override
	protected Void calculate( )
	{
		consumer.accept( input.get( ) );
		return null;
	}
}

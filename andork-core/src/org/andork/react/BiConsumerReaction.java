package org.andork.react;

import java.util.function.BiConsumer;

public class BiConsumerReaction<T, U> extends Reaction<Void>
{
	private final Reactable<? extends T> t;
	private final Reactable<? extends U> u;
	private final BiConsumer<? super T, ? super U> consumer;

	public BiConsumerReaction( Reactable<? extends T> t , Reactable<? extends U> u , BiConsumer<? super T, ? super U> consumer )
	{
		this.t = t;
		this.u = u;
		this.consumer = consumer;

		t.bind( this );
		u.bind( this );
	}

	@Override
	protected Void calculate( )
	{
		consumer.accept( t.get( ) , u.get( ) );
		return null;
	}
}

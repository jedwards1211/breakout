package org.andork.react;

import java.util.function.Consumer;

public class ConsumerRxn<T> extends Rxn<Void>
{
	private final Node<? extends T> input;
	private final Consumer<? super T> p;

	public ConsumerRxn( Node<? extends T> input , Consumer<? super T> p )
	{
		this.p = p;
		this.input = input;
		input.bind( this );
	}

	@Override
	protected Void recalculate( )
	{
		p.accept( input.get( ) );
		return null;
	}
}

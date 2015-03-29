package org.andork.react;

import java.util.function.Function;

public class FunctionRxn<T, R> extends Rxn<R>
{
	private final Node<? extends T> input;
	private final Function<? super T, ? extends R> fn;

	public FunctionRxn( Node<? extends T> input , Function<? super T, ? extends R> fn )
	{
		this.fn = fn;
		this.input = input;
		input.bind( this );
	}

	@Override
	protected R recalculate( )
	{
		return fn.apply( input.get( ) );
	}
}

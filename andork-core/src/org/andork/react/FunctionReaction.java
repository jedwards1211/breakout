package org.andork.react;

import java.util.function.Function;

public class FunctionReaction<T, R> extends Reaction<R>
{
	private final Reactable<? extends T> input;
	private final Function<? super T, ? extends R> fn;

	public FunctionReaction( Reactable<? extends T> input , Function<? super T, ? extends R> fn )
	{
		this.fn = fn;
		this.input = input;
		input.bind( this );
	}

	@Override
	protected R calculate( )
	{
		return fn.apply( input.get( ) );
	}
}

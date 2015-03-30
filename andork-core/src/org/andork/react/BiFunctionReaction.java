package org.andork.react;

import java.util.function.BiFunction;

public class BiFunctionReaction<T, U, R> extends Reaction<R>
{
	private Reactable<? extends T> t;
	private Reactable<? extends U> u;
	private BiFunction<? super T, ? super U, ? extends R> fn;

	public BiFunctionReaction( Reactable<? extends T> t , Reactable<? extends U> u , BiFunction<? super T, ? super U, ? extends R> fn )
	{
		this.t = t;
		this.u = u;
		this.fn = fn;

		t.bind( this );
		u.bind( this );
	}

	@Override
	protected R calculate( )
	{
		return fn.apply( t.get( ) , u.get( ) );
	}
}

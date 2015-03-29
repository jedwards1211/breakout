package org.andork.react;

import java.util.function.BiFunction;

public class BiFunctionRxn<T, U, R> extends Rxn<R>
{
	private final Node<? extends T> t;
	private final Node<? extends U> u;
	private final BiFunction<? super T, ? super U, ? extends R> fn;

	public BiFunctionRxn( Node<? extends T> t , Node<? extends U> u , BiFunction<? super T, ? super U, ? extends R> fn )
	{
		this.t = t;
		this.u = u;
		this.fn = fn;
		
		t.bind( this );
		u.bind( this );
	}

	@Override
	protected R recalculate( )
	{
		return fn.apply( t.get( ) , u.get( ) );
	}
}

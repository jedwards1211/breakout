package org.andork.react;

import java.util.function.BiPredicate;

public class BiPredicateRxn<T, U> extends Rxn<Boolean>
{
	private final Node<? extends T> t;
	private final Node<? extends U> u;
	private final BiPredicate<? super T, ? super U> p;

	public BiPredicateRxn( Node<? extends T> t , Node<? extends U> u , BiPredicate<? super T, ? super U> p )
	{
		this.t = t;
		this.u = u;
		this.p = p;

		t.bind( this );
		u.bind( this );
	}

	@Override
	protected Boolean recalculate( )
	{
		return p.test( t.get( ) , u.get( ) );
	}
}

package org.andork.react;

import java.util.function.BiPredicate;

public class BiPredicateReaction<T, U> extends Reaction<Boolean>
{
	private final Reactable<? extends T> t;
	private final Reactable<? extends U> u;
	private final BiPredicate<? super T, ? super U> p;

	public BiPredicateReaction( Reactable<? extends T> t , Reactable<? extends U> u , BiPredicate<? super T, ? super U> p )
	{
		this.t = t;
		this.u = u;
		this.p = p;

		t.bind( this );
		u.bind( this );
	}

	@Override
	protected Boolean calculate( )
	{
		return p.test( t.get( ) , u.get( ) );
	}
}

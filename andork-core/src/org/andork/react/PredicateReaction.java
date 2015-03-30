package org.andork.react;

import java.util.function.Predicate;

public class PredicateReaction<T> extends Reaction<Boolean>
{
	private final Reactable<? extends T> input;
	private final Predicate<? super T> p;

	public PredicateReaction( Reactable<? extends T> input , Predicate<? super T> p )
	{
		this.p = p;
		this.input = input;
		input.bind( this );
	}

	@Override
	protected Boolean calculate( )
	{
		return p.test( input.get( ) );
	}
}

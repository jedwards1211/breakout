package org.andork.react;

import java.util.function.Predicate;

public class PredicateRxn<T> extends Rxn<Boolean>
{
	private final Node<? extends T> input;
	private final Predicate<? super T> p;

	public PredicateRxn( Node<? extends T> input , Predicate<? super T> p )
	{
		this.p = p;
		this.input = input;
		input.bind( this );
	}

	@Override
	protected Boolean recalculate( )
	{
		return p.test( input.get( ) );
	}
}

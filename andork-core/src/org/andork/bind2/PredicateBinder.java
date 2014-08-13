package org.andork.bind2;

import java.util.function.Predicate;

public class PredicateBinder<T> extends CachingBinder<Boolean> implements Binding
{
	public final Link<T>		inputLink	= new Link<T>( this );
	public final Predicate<T>	p;
	
	public PredicateBinder( Predicate<T> p )
	{
		this.p = p;
	}
	
	public PredicateBinder( Binder<? extends T> input , Predicate<T> p )
	{
		this( p );
		inputLink.bind( input );
	}
	
	public void update( boolean force )
	{
		set( p.test( inputLink.get( ) ) , force );
	}
}

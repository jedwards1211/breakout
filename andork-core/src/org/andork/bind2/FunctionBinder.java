package org.andork.bind2;

import java.util.function.Function;

public class FunctionBinder<I, O> extends CachingBinder<O> implements Binding
{
	public final Link<I>		inputLink	= new Link<I>( this );
	public final Function<I, O>	fn;
	
	public FunctionBinder( Function<I, O> fn )
	{
		this.fn = fn;
	}
	
	public FunctionBinder( Binder<? extends I> input , Function<I, O> fn )
	{
		this( fn );
		inputLink.bind( input );
	}
	
	public void update( boolean force )
	{
		set( fn.apply( inputLink.get( ) ) , force );
	}
}

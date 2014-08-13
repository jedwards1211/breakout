package org.andork.bind2;

public final class OpaqueBinderHolder<T> extends CachingBinder<T> implements Binding
{
	private final Link<T>	binderLink	= new Link<T>( this );
	
	public OpaqueBinderHolder( Binder<? extends T> wrapped )
	{
		binderLink.bind( wrapped );
	}
	
	public void update( boolean force )
	{
		set( binderLink.get( ) , force );
	}
}

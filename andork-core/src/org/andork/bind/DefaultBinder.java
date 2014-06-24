package org.andork.bind;

import org.andork.util.Java7;

public class DefaultBinder<T> extends Binder<T>
{
	private T	value;
	
	public DefaultBinder( )
	{
		
	}
	
	public DefaultBinder( T value )
	{
		super( );
		this.value = value;
	}
	
	public static <T> DefaultBinder<T> bind( T value )
	{
		return new DefaultBinder<T>( value );
	}
	
	@Override
	public T get( )
	{
		return value;
	}
	
	@Override
	public void set( T newValue )
	{
		if( !Java7.Objects.equals( value , newValue ) )
		{
			value = newValue;
			updateDownstream( false );
		}
	}
	
	@Override
	public void update( boolean force )
	{
		if( force )
		{
			updateDownstream( force );
		}
	}
}

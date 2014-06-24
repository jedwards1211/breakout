package org.andork.bind;

import org.andork.util.Java7;

public class BinderWrapper<T> extends Binder<T>
{
	private Binder<T>	wrapped;
	private T			value;
	
	public BinderWrapper( )
	{
		
	}
	
	public BinderWrapper( Binder<T> wrapped )
	{
		bind( wrapped );
	}
	
	public BinderWrapper<T> bind( Binder<T> wrapped )
	{
		if( this.wrapped != wrapped )
		{
			if( this.wrapped != null )
			{
				unbind( this.wrapped , this );
			}
			this.wrapped = wrapped;
			if( wrapped != null )
			{
				bind( this.wrapped , this );
			}
			
			update( false );
		}
		return this;
	}
	
	public void unbind( )
	{
		bind( null );
	}
	
	@Override
	public T get( )
	{
		return value;
	}
	
	@Override
	public void set( T newValue )
	{
		if( wrapped != null )
		{
			wrapped.set( newValue );
		}
	}
	
	@Override
	public void update( boolean force )
	{
		T newValue = wrapped == null ? null : wrapped.get( );
		if( force || !Java7.Objects.equals( value , newValue ) )
		{
			value = newValue;
			onValueChanged( newValue );
			updateDownstream( force );
		}
	}
	
	protected void onValueChanged( T newValue )
	{
	}
}

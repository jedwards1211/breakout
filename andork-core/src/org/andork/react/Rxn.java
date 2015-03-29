package org.andork.react;

public abstract class Rxn<T> extends Node<T>
{
	private boolean valid;
	protected T value;

	public final T get( )
	{
		if( !valid )
		{
			validate( );
		}
		return value;
	}

	public final void invalidate( )
	{
		if( valid )
		{
			valid = false;
			fireValueChanged( );
		}
	}

	public final void validate( )
	{
		set( recalculate( ) );
		valid = true;
	}

	protected void set( T newValue )
	{
		this.value = newValue;
	}

	protected abstract T recalculate( );
}

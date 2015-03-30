package org.andork.react;

public abstract class Reaction<T> extends Reactable<T>
{
	private boolean valid;
	protected T value;

	public final T get( )
	{
		validate( );
		return value;
	}

	public final void invalidate( )
	{
		if( valid )
		{
			valid = false;
			invalidateReactions( );
		}
	}

	public final void validate( )
	{
		if( !valid )
		{
			set( calculate( ) );
			valid = true;
		}
	}

	protected void set( T newValue )
	{
		this.value = newValue;
	}

	protected abstract T calculate( );
}

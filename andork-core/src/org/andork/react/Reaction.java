package org.andork.react;

public abstract class Reaction<T> extends Reactable<T>
{
	private boolean valid;

	public final T get( )
	{
		validate( );
		return super.get( );
	}

	public boolean isValid( )
	{
		return valid;
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

	protected abstract T calculate( );
}

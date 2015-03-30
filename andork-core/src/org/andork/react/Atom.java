package org.andork.react;

import java.util.Objects;

public final class Atom<T> extends Reactable<T>
{
	private T value;

	public Atom( )
	{

	}

	public Atom( T initValue )
	{
		value = initValue;
	}

	@Override
	public T get( )
	{
		return value;
	}

	public void set( T newValue )
	{
		if( !Objects.equals( value , newValue ) )
		{
			value = newValue;
			invalidateReactions( );
		}
	}
}

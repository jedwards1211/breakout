package org.andork.react;

public final class ImmutableAtom<T> extends Reactable<T>
{
	public ImmutableAtom( T initValue )
	{
		set( initValue );
	}
}

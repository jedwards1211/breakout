package org.andork.react;

public final class ImmutableAtom<T> extends Reactable<T>
{
	private T value;

	public ImmutableAtom( T initValue )
	{
		value = initValue;
	}

	@Override
	public T get( )
	{
		return value;
	}
}

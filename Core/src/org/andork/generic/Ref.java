package org.andork.generic;

public class Ref<T>
{
	public T	value;
	
	public Ref( )
	{
		this( null );
	}

	public Ref( T value )
	{
		this.value = value;
	}
}

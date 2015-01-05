package org.andork.breakout.table;

import java.util.Objects;
import java.util.function.Function;

public class DefaultTwoElemFormatter<E> implements Function<E[ ], String>
{
	public Function<E, String>	elemFormatter;

	public DefaultTwoElemFormatter( )
	{
		elemFormatter = Object::toString;
	}

	public DefaultTwoElemFormatter( Function<E, String> elemFormatter )
	{
		setElemFormatter( elemFormatter );
	}

	public Function<E, String> getElemFormatter( )
	{
		return elemFormatter;
	}

	public void setElemFormatter( Function<E, String> elemFormatter )
	{
		this.elemFormatter = Objects.requireNonNull( elemFormatter );
	}

	@Override
	public String apply( E[ ] t )
	{
		if( t == null )
		{
			return null;
		}
		StringBuilder sb = new StringBuilder( );
		sb.append( t[ 0 ] == null ? "-" : elemFormatter.apply( t[ 0 ] ) );
		sb.append( '/' );
		sb.append( t[ 1 ] == null ? "-" : elemFormatter.apply( t[ 1 ] ) );
		return sb.toString( );
	}
}

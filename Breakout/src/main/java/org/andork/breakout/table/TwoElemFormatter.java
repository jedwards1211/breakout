package org.andork.breakout.table;

import java.util.Objects;
import java.util.function.Function;

import org.andork.util.StringUtils;

public class TwoElemFormatter<E> implements Function<E[ ], String>
{
	public Function<E, String>	elemFormatter;
	int							elemWidth	= 5;

	public TwoElemFormatter( )
	{
		elemFormatter = Object::toString;
	}

	public TwoElemFormatter( Function<E, String> elemFormatter )
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
	public String apply( E[ ] e )
	{
		if( e[ 0 ] == null && e[ 1 ] == null )
		{
			return null;
		}
		StringBuilder sb = new StringBuilder( );
		sb.append( StringUtils.pad( e[ 0 ] == null ? "-" : elemFormatter.apply( e[ 0 ] ) , ' ' , elemWidth , false ) );
		sb.append( '/' );
		sb.append( StringUtils.pad( e[ 1 ] == null ? "-" : elemFormatter.apply( e[ 1 ] ) , ' ' , elemWidth , false ) );
		return sb.toString( );
	}
}

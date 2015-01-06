package org.andork.breakout.table;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.andork.util.StringUtils;

public class TwoElemFormatter<E> implements BiFunction<E, E, String>
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
	public String apply( E a , E b )
	{
		if( a == null && b == null )
		{
			return null;
		}
		StringBuilder sb = new StringBuilder( );
		sb.append( StringUtils.pad( a == null ? "-" : elemFormatter.apply( a ) , ' ' , elemWidth , false ) );
		sb.append( '/' );
		sb.append( StringUtils.pad( b == null ? "-" : elemFormatter.apply( b ) , ' ' , elemWidth , false ) );
		return sb.toString( );
	}
}

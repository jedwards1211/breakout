package org.andork.breakout.wallsimport;

import java.util.Objects;
import java.util.function.ToIntFunction;

public class ValueToken<V> extends Token
{
	public final V	value;

	public ValueToken( V value , Token ... parts )
	{
		this( first( t -> t.beginLine , parts ) , first( t -> t.beginColumn , parts ) ,
			last( t -> t.endLine , parts ) , last( t -> t.endColumn , parts ) , join( parts ) , value );
	}

	private static int first( ToIntFunction<Token> fn , Token ... parts )
	{
		for( Token token : parts )
		{
			if( token != null )
			{
				return fn.applyAsInt( token );
			}
		}
		return -1;
	}

	private static int last( ToIntFunction<Token> fn , Token ... parts )
	{
		for( int i = parts.length - 1 ; i >= 0 ; i-- )
		{
			if( parts[ i ] != null )
			{
				return fn.applyAsInt( parts[ i ] );
			}
		}
		return -1;
	}

	private static String join( Token ... parts )
	{
		StringBuilder sb = new StringBuilder( );
		for( Token token : parts )
		{
			if( token != null )
			{
				sb.append( token.image );
			}
		}
		return sb.toString( );
	}

	public ValueToken( int beginLine , int beginColumn , int endLine , int endColumn , String image , V value )
	{
		super( beginLine , beginColumn , endLine , endColumn , image );
		this.value = value;
	}
}

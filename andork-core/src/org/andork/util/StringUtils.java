package org.andork.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringUtils
{
	private StringUtils( )
	{
		
	}
	
	public static String toStringOrNull( Object o )
	{
		return o == null ? null : o.toString( );
	}
	
	public static String multiply( String s , int count )
	{
		StringBuffer sb = new StringBuffer( );
		for( int i = 0 ; i < count ; i++ )
		{
			sb.append( s );
		}
		return sb.toString( );
	}
	
	public static String formatThrowableForHTML( Throwable t )
	{
		return formatThrowableForHTML( "" , t , new HashSet<Throwable>( ) , 10 );
	}
	
	public static String formatThrowableForHTML( Throwable t , int maxStackTraceLines )
	{
		return formatThrowableForHTML( "" , t , new HashSet<Throwable>( ) , maxStackTraceLines );
	}
	
	private static String formatThrowableForHTML( String prefix , Throwable t , Set<Throwable> visited , int maxStackTraceLines )
	{
		if( !visited.add( t ) )
		{
			return "";
		}
		StringBuilder sb = new StringBuilder( );
		sb.append( "<code>" ).append( prefix );
		sb.append( "<b>" ).append( t.getClass( ).getSimpleName( ) ).append( "</b>" );
		sb.append( ": " ).append( t.getLocalizedMessage( ) ).append( "<br />" );
		
		StackTraceElement[ ] stackTrace = t.getStackTrace( );
		
		for( int line = 0 ; line < maxStackTraceLines && line < stackTrace.length ; line++ )
		{
			sb.append( "&emsp;at " ).append( stackTrace[ line ] ).append( "<br />" );
		}
		if( maxStackTraceLines < stackTrace.length )
		{
			sb.append( "&emsp;..." ).append( stackTrace.length - maxStackTraceLines ).append( " more<br />" );
		}
		
		sb.append( "</code>" );
		
		if( t.getCause( ) != null )
		{
			sb.append( formatThrowableForHTML( "Caused by: " , t.getCause( ) , visited , maxStackTraceLines ) );
		}
		return sb.toString( );
	}
	
	public static String join( String separator , String ... strings )
	{
		StringBuilder sb = new StringBuilder( );
		if( strings.length > 0 )
		{
			sb.append( strings[ 0 ] );
		}
		for( int i = 1 ; i < strings.length ; i++ )
		{
			sb.append( separator ).append( strings[ i ] );
		}
		return sb.toString( );
	}
	
	public static String join( String separator , List<String> strings )
	{
		StringBuilder sb = new StringBuilder( );
		if( strings.size( ) > 0 )
		{
			sb.append( strings.get( 0 ) );
		}
		for( int i = 1 ; i < strings.size( ) ; i++ )
		{
			sb.append( separator ).append( strings.get( i ) );
		}
		return sb.toString( );
	}
	
	public static boolean isNullOrEmpty( Object aValue )
	{
		return aValue == null || "".equals( aValue.toString( ) );
	}
	
	public static String escape( String s , char escape )
	{
		StringBuilder sb = new StringBuilder( );
		
		boolean inEscape = false;
		for( int i = 0 ; i < s.length( ) ; i++ )
		{
			char ic = s.charAt( i );
			if( inEscape || ic != escape )
			{
				sb.append( ic );
				inEscape = false;
			}
			else
			{
				inEscape = true;
			}
		}
		return sb.toString( );
	}
	
	public static int unescapedIndexOf( String s , char c , char escape )
	{
		boolean inEscape = false;
		for( int i = 0 ; i < s.length( ) ; i++ )
		{
			char ic = s.charAt( i );
			if( inEscape )
			{
				inEscape = false;
			}
			else
			{
				if( ic == escape )
				{
					inEscape = true;
				}
				else if( ic == c )
				{
					return i;
				}
			}
		}
		return -1;
	}
}

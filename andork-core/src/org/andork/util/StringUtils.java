package org.andork.util;

public class StringUtils
{
	private StringUtils( )
	{
		
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
}

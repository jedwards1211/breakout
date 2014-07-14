package org.andork.util;

public class IntegerFormat implements Format<Integer>
{
	public static final IntegerFormat	instance	= new IntegerFormat( );
	
	@Override
	public String format( Integer t )
	{
		return StringUtils.toStringOrNull( t );
	}
	
	@Override
	public Integer parse( String s ) throws Exception
	{
		return StringUtils.isNullOrEmpty( s ) ? null : Integer.parseInt( s );
	}
}

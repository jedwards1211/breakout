package org.andork.util;

public class DoubleFormat implements Format<Double>
{
	public static final DoubleFormat	instance	= new DoubleFormat( );
	
	@Override
	public String format( Double t )
	{
		return StringUtils.toStringOrNull( t );
	}
	
	@Override
	public Double parse( String s ) throws Exception
	{
		return StringUtils.isNullOrEmpty( s ) ? null : Double.parseDouble( s );
	}
}

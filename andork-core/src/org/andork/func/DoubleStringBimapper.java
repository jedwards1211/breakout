package org.andork.func;

import java.text.NumberFormat;

public class DoubleStringBimapper implements Bimapper<Double, String>
{
	private final NumberFormat	format;
	
	public DoubleStringBimapper( NumberFormat format )
	{
		this.format = format;
	}
	
	@Override
	public String map( Double in )
	{
		return in == null ? null : format.format( in );
	}
	
	@Override
	public Double unmap( String out )
	{
		try
		{
			return out == null ? null : format.parse( out ).doubleValue( );
		}
		catch( Exception ex )
		{
			throw new RuntimeException( ex );
		}
	}
}

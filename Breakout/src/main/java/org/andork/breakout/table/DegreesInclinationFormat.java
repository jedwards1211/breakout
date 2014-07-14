package org.andork.breakout.table;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.andork.util.Format;

public class DegreesInclinationFormat implements Format<Double>
{
	private static final NumberFormat	outFormat	= DecimalFormat.getInstance( );
	private static final NumberFormat	inFormat	= DecimalFormat.getInstance( );
	
	static
	{
		outFormat.setMinimumFractionDigits( 1 );
		outFormat.setMaximumFractionDigits( 1 );
	}
	
	@Override
	public String format( Double t )
	{
		return t == null ? "--" : outFormat.format( Math.toDegrees( t ) );
	}
	
	@Override
	public Double parse( String s ) throws Exception
	{
		if( s == null || "--".equals( s ) )
		{
			return null;
		}
		try
		{
			double degrees = inFormat.parse( s ).doubleValue( );
			if( degrees < -90 || degrees > 90 )
			{
				throw new IllegalArgumentException( "Inclination must be >= -90 and <= 90" );
			}
			return Math.toRadians( degrees );
		}
		catch( Exception ex )
		{
			throw new IllegalArgumentException( "Invalid azimuth: " + s );
		}
	}
}

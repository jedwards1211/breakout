package org.andork.breakout.table;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.andork.util.Format;
import org.andork.util.StringUtils;

public class DefaultDistanceFormat implements Format<Double>
{
	private static final NumberFormat	outFormat	= DecimalFormat.getInstance( );
	private static final NumberFormat	inFormat	= DecimalFormat.getInstance( );
	
	static
	{
		outFormat.setMinimumFractionDigits( 2 );
		outFormat.setMaximumFractionDigits( 2 );
	}
	
	@Override
	public String format( Double t )
	{
		return t == null ? null : outFormat.format( t );
	}
	
	@Override
	public Double parse( String s ) throws Exception
	{
		if( StringUtils.isNullOrEmpty( s ) )
		{
			return null;
		}
		try
		{
			double value = inFormat.parse( s ).doubleValue( );
			if( value < 0 )
			{
				throw new IllegalArgumentException( "Distance may not be < 0" );
			}
			return value;
		}
		catch( Exception ex )
		{
			throw new IllegalArgumentException( "Invalid distance: " + s );
		}
	}
}

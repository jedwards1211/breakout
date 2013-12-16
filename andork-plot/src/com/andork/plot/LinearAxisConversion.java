
package com.andork.plot;

import java.io.Serializable;

/**
 * An axis conversion function that is a line with nonzero scale.
 */
public class LinearAxisConversion implements IAxisConversion, Serializable
{
	private static final long	serialVersionUID	= -3040515059560175691L;
	
	private double	offset	= 0;
	private double	scale	= 1;
	
	public void set( LinearAxisConversion other )
	{
		offset = other.offset;
		scale = other.scale;
	}
	
	/**
	 * Sets this conversion so that {@code convert( x1 ) == cx1} and {@code convert( x2 ) == cx2}.
	 * The parameters must not be NaN or Infinite, {@code x1} must not equal {@code x2} and {@code cx1}
	 * must not equal {@code cx2}.
	 * @throws IllegalArgumentException if the parameters are invalid or if it is impossible to calculate
	 * the conversion because of a double overflow.
	 */
	public void set( double x1 , double cx1 , double x2 , double cx2 )
	{
		if( Double.isNaN( x1 ) || Double.isInfinite( x1 ) )
		{
			throw new IllegalArgumentException( "x1 must not be NaN or infinite" );
		}
		if( Double.isNaN( cx1 ) || Double.isInfinite( cx1 ) )
		{
			throw new IllegalArgumentException( "cx1 must not be NaN or infinite" );
		}
		if( Double.isNaN( x2 ) || Double.isInfinite( x2 ) )
		{
			throw new IllegalArgumentException( "x2 must not be NaN or infinite" );
		}
		if( Double.isNaN( cx2 ) || Double.isInfinite( cx2 ) )
		{
			throw new IllegalArgumentException( "cx2 must not be NaN or infinite" );
		}
		if( x1 == x2 )
		{
			throw new IllegalArgumentException( "x1 must not equal x2" );
		}
		if( cx1 == cx2 )
		{
			throw new IllegalArgumentException( "cx1 must not equal cx2" );
		}
		// set so that convert( x1 ) == cx1 and convert( x2 ) == cx2
		// ( x1 - offset ) * scale == cx1
		// ( x2 - offset ) * scale == cx2
		// ( x1 - x2 ) * scale == cx1 - cx2
		// scale == ( cx1 - cx2 ) / ( x1 - x2 )
		// offset == x1 - cx1 / scale
		
		double newScale = ( cx1 - cx2 ) / ( x1 - x2 );
		double newOffset = x1 - cx1 / newScale;
		
		if( newScale == 0 || Double.isInfinite( newScale ) || Double.isInfinite( newOffset ) )
		{
			throw new IllegalArgumentException( "double overflow" );
		}
		
		offset = newOffset;
		scale = newScale;
	}

	public double getOffset( )
	{
		return offset;
	}
	
	public void setOffset( double offset )
	{
		if( Double.isNaN( offset ) || Double.isInfinite( offset ) )
		{
			throw new IllegalArgumentException( "offset must not be NaN or infinite" );
		}
		this.offset = offset;
	}
	
	public double getScale( )
	{
		return scale;
	}
	
	public void setScale( double scale )
	{
		if( Double.isNaN( scale ) || Double.isInfinite( scale ) )
		{
			throw new IllegalArgumentException( "scale must not be NaN or infinite" );
		}
		if( scale == 0 )
		{
			throw new IllegalArgumentException( "scale must be nonzero" );
		}
		this.scale = scale;
	}
	
	@Override
	public double convert( double d )
	{
		return ( d - offset ) * scale;
	}
	
	@Override
	public double invert( double d )
	{
		return offset + d / scale;
	}
}

package org.andork.breakout;

import java.awt.Color;
import java.util.Map;
import java.util.TreeMap;

public class GradientMap implements IGradientMap
{
	public final TreeMap<Double, Color>	map	= new TreeMap<Double, Color>( );
	
	public static Color interp( Color a , Color b , double f )
	{
		return new Color( ( int ) ( a.getRed( ) * ( 1 - f ) + b.getRed( ) * f ) ,
				( int ) ( a.getGreen( ) * ( 1 - f ) + b.getGreen( ) * f ) ,
				( int ) ( a.getBlue( ) * ( 1 - f ) + b.getBlue( ) * f ) ,
				( int ) ( a.getAlpha( ) * ( 1 - f ) + b.getAlpha( ) * f ) );
	}
	
	@Override
	public int size( )
	{
		return map.size( );
	}
	
	@Override
	public Double firstKey( )
	{
		return map.firstKey( );
	}
	
	@Override
	public Double lastKey( )
	{
		return map.lastKey( );
	}
	
	@Override
	public Iterable<Map.Entry<Double, Color>> entries( )
	{
		return map.entrySet( );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andork.frf.IGradientMap#getColor(double)
	 */
	@Override
	public Color getColor( double value )
	{
		Map.Entry<Double, Color> lo = map.floorEntry( value );
		Map.Entry<Double, Color> hi = map.ceilingEntry( value );
		
		if( lo == null )
		{
			return hi == null ? null : hi.getValue( );
		}
		if( hi == null )
		{
			return lo == null ? null : lo.getValue( );
		}
		
		return interp( lo.getValue( ) , hi.getValue( ) , ( value - lo.getKey( ) ) / ( hi.getKey( ) - lo.getKey( ) ) );
	}
}

package org.andork.breakout;

import java.awt.Color;
import java.util.Map;

public interface IGradientMap
{
	
	public abstract Color getColor( double value );

	public abstract Double lastKey( );

	public abstract Double firstKey( );

	public abstract Iterable<Map.Entry<Double, Color>> entries( );

	public abstract int size( );
	
}
package org.andork.frf;

import java.awt.Color;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.andork.plot.LinearAxisConversion;

public class GradientMapScaler implements IGradientMap
{
	IGradientMap				wrapped;
	public LinearAxisConversion	conversion	= new LinearAxisConversion( );
	
	public GradientMapScaler( IGradientMap gradientMap )
	{
		super( );
		this.wrapped = gradientMap;
	}
	
	public int size( )
	{
		return wrapped.size( );
	}

	@Override
	public Color getColor( double value )
	{
		return wrapped.getColor( conversion.convert( value ) );
	}
	
	public Double lastKey( )
	{
		Double lastKey = wrapped.lastKey( );
		return lastKey == null ? null : conversion.convert( lastKey );
	}
	
	public Double firstKey( )
	{
		Double firstKey = wrapped.firstKey( );
		return firstKey == null ? null : conversion.convert( firstKey );
	}
	
	public Iterable<Map.Entry<Double, Color>> entries( )
	{
		return new Iterable<Map.Entry<Double, Color>>( )
		{
			@Override
			public Iterator<Entry<Double, Color>> iterator( )
			{
				return new Iterator<Map.Entry<Double, Color>>( )
				{
					Iterator<Map.Entry<Double, Color>>	iter	= wrapped.entries( ).iterator( );
					
					@Override
					public boolean hasNext( )
					{
						return iter.hasNext( );
					}
					
					@Override
					public Entry<Double, Color> next( )
					{
						final Entry<Double, Color> entry = iter.next( );
						
						return new Map.Entry<Double, Color>( )
						{
							@Override
							public Double getKey( )
							{
								return conversion.convert( entry.getKey( ) );
							}
							
							@Override
							public Color getValue( )
							{
								return entry.getValue( );
							}
							
							@Override
							public Color setValue( Color value )
							{
								return entry.setValue( value );
							}
						};
					}
					
					@Override
					public void remove( )
					{
						iter.remove( );
					}
				};
			}
		};
	}
}

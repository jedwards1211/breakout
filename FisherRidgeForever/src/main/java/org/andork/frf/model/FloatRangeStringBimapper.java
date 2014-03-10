package org.andork.frf.model;

import org.andork.func.Bimapper;

public class FloatRangeStringBimapper implements Bimapper<FloatRange, Object>
{
	@Override
	public String map( FloatRange in )
	{
		return String.format( "%f to %f" , in.getLo( ) , in.getHi( ) );
	}
	
	@Override
	public FloatRange unmap( Object out )
	{
		if( out == null )
		{
			return null;
		}
		String[ ] split = out.toString( ).split( "\\s*to\\s*" );
		return new FloatRange( Float.parseFloat( split[ 0 ].trim( ) ) , Float.parseFloat( split[ 1 ].trim( ) ) );
	}
}

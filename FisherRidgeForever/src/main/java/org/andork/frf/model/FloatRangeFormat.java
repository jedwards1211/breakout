package org.andork.frf.model;

import org.andork.snakeyaml.YamlSpec.Format;

public class FloatRangeFormat implements Format<FloatRange>
{
	public static final FloatRangeFormat	instance	= new FloatRangeFormat( );
	
	@Override
	public Object format( FloatRange t )
	{
		return t == null ? null : String.format( "%f to %f" , t.lo , t.hi );
	}
	
	@Override
	public FloatRange parse( Object s ) throws Exception
	{
		if( s == null )
		{
			return null;
		}
		String[ ] split = ( ( String ) s ).split( "\\s+to\\s+" );
		return new FloatRange( Float.parseFloat( split[ 0 ].trim( ) ) , Float.parseFloat( split[ 1 ].trim( ) ) );
	}
	
}

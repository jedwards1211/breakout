package org.andork.breakout.table;

import org.andork.util.ArrayUtils;
import org.andork.util.FormatWarning;
import org.andork.util.StringUtils;

public class NsewCrossSectionFormat extends CrossSectionFormat
{
	public NsewCrossSectionFormat( )
	{
		super( );
	}
	
	public NsewCrossSectionFormat( DefaultDistanceFormat distanceFormat )
	{
		super( distanceFormat );
	}
	
	@Override
	public String format( CrossSection t )
	{
		if( t == null )
		{
			return null;
		}
		return "NSEW: " + StringUtils.join( " " , ArrayUtils.throwableMap( t.values , new String[ t.values.length ] , d -> distanceFormat.format( d ) ) );
	}
	
	@Override
	public CrossSection parse( String s ) throws Exception
	{
		if( StringUtils.isNullOrEmpty( s ) )
		{
			return null;
		}
		String[ ] parts = s.trim( ).split( "\\s+|\\s*[;/]\\s*" );
		
		if( parts.length < 4 )
		{
			throw new IllegalArgumentException( "Must provide 4 numbers" );
		}
		
		double[ ] parsed = ArrayUtils.throwableMap( parts , new double[ parts.length ] , str -> distanceFormat.parse( str ) );
		
		for( Double d : parsed )
		{
			if( d < 0 )
			{
				throw new IllegalArgumentException( "Distances must be >= 0" );
			}
		}
		
		CrossSection result = new CrossSection( CrossSectionType.NSEW , parsed );
		
		if( parts.length > 4 )
		{
			throw new FormatWarning( "Warning: extra numbers provided" , result );
		}
		return result;
	}
	
}

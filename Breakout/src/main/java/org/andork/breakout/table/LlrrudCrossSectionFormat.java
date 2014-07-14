package org.andork.breakout.table;

import org.andork.util.ArrayUtils;
import org.andork.util.FormatWarning;
import org.andork.util.StringUtils;

public class LlrrudCrossSectionFormat extends CrossSectionFormat
{
	public LlrrudCrossSectionFormat( )
	{
		super( );
	}
	
	public LlrrudCrossSectionFormat( DefaultDistanceFormat distanceFormat )
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
		return "LLRRUD: " + StringUtils.join( " " , ArrayUtils.throwableMap( t.values , new String[ t.values.length ] , d -> distanceFormat.format( d ) ) );
	}
	
	@Override
	public CrossSection parse( String s ) throws Exception
	{
		if( StringUtils.isNullOrEmpty( s ) )
		{
			return null;
		}
		String[ ] parts = s.trim( ).split( "\\s+|\\s*[;/]\\s*" );
		
		if( parts.length < 6 )
		{
			throw new IllegalArgumentException( "Must provide 6 numbers" );
		}
		
		double[ ] parsed = ArrayUtils.throwableMap( parts , new double[ parts.length ] , str -> distanceFormat.parse( str ) );
		
		for( int i = 4 ; i < 6 ; i++ )
		{
			if( parsed[ i ] < 0 )
			{
				throw new IllegalArgumentException( "UD must be >= 0" );
			}
		}
		
		CrossSection result = new CrossSection( CrossSectionType.LLRRUD , parsed );
		
		if( parts.length > 6 )
		{
			throw new FormatWarning( "Warning: extra numbers provided" , result );
		}
		return result;
	}
	
}

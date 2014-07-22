package org.andork.breakout.table;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andork.util.Format;
import org.andork.util.StringUtils;

public class NorthEastDepthFixedToStationShotMeasurementFormat implements Format<FixedToStationShotMeasurement>
{
	Format<Double>					distanceFormat;
	
	private static final Pattern	invalidCharacterPattern	= Pattern.compile( "[^-+0-9;. ]" );
	
	public NorthEastDepthFixedToStationShotMeasurementFormat( )
	{
		this( new DefaultDistanceFormat( ) );
	}
	
	public NorthEastDepthFixedToStationShotMeasurementFormat( Format<Double> distanceFormat )
	{
		super( );
		this.distanceFormat = distanceFormat;
	}
	
	@Override
	public String format( FixedToStationShotMeasurement t )
	{
		if( t == null )
		{
			return null;
		}
		return "N: " + distanceFormat.format( -t.location[ 2 ] ) + "  E: " + distanceFormat.format( t.location[ 0 ] ) + "  Depth: " + distanceFormat.format( -t.location[ 1 ] );
	}
	
	@Override
	public FixedToStationShotMeasurement parse( String s ) throws Exception
	{
		if( StringUtils.isNullOrEmpty( s ) )
		{
			return null;
		}
		
		Matcher m;
		m = invalidCharacterPattern.matcher( s );
		if( m.find( ) )
		{
			throw new IllegalArgumentException( "Invalid character: " + m.group( ) );
		}
		
		String[ ] parts = s.trim( ).split( "\\s+|\\s*;\\s*" );
		
		if( parts.length == 0 )
		{
			throw new IllegalArgumentException( "Invalid input" );
		}
		if( parts.length == 1 )
		{
			throw new IllegalArgumentException( "Missing east and depth" );
		}
		if( parts.length == 2 )
		{
			throw new IllegalArgumentException( "Missing depth" );
		}
		
		try
		{
			return new FixedToStationShotMeasurement(
					distanceFormat.parse( parts[ 1 ] ) ,
					-distanceFormat.parse( parts[ 2 ] ) ,
					-distanceFormat.parse( parts[ 0 ] ) );
		}
		catch( Exception ex )
		{
			throw new IllegalArgumentException( "Invalid input" );
		}
	}
}

package org.andork.breakout.table;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andork.util.Format;
import org.andork.util.StringUtils;

public class NorthEastDepthVectorMeasurementFormat implements Format<VectorMeasurement>
{
	Format<Double>					distanceFormat;
	
	private static final Pattern	invalidCharacterPattern	= Pattern.compile( "[^-+0-9;. ]" );
	
	public NorthEastDepthVectorMeasurementFormat( )
	{
		this( new DefaultDistanceFormat( ) );
	}
	
	public NorthEastDepthVectorMeasurementFormat( Format<Double> distanceFormat )
	{
		super( );
		this.distanceFormat = distanceFormat;
	}
	
	@Override
	public String format( VectorMeasurement t )
	{
		if( t == null )
		{
			return null;
		}
		return "dN: " + distanceFormat.format( -t.vector[ 2 ] ) + "  dE: " + distanceFormat.format( t.vector[ 0 ] ) + "  dDepth: " + distanceFormat.format( -t.vector[ 1 ] );
	}
	
	@Override
	public VectorMeasurement parse( String s ) throws Exception
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
			throw new IllegalArgumentException( "Missing east and depth offsets" );
		}
		if( parts.length == 2 )
		{
			throw new IllegalArgumentException( "Missing depth offset" );
		}
		
		try
		{
			return new VectorMeasurement(
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

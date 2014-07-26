/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.breakout.table;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andork.format.Format;
import org.andork.format.FormatWarning;
import org.andork.math.misc.AngleUtils;
import org.andork.util.ArrayUtils;
import org.andork.util.StringUtils;

public class DistAzmIncMeasurementFormat implements Format<DistAzmIncMeasurement>
{
	boolean			correctBacksights;
	
	Format<Double>	distanceFormat;
	Format<Double>	azimuthFormat;
	Format<Double>	inclinationFormat;
	
	public DistAzmIncMeasurementFormat( boolean correctBacksights )
	{
		this( new DefaultDistanceFormat( ) ,
				new DegreesAzimuthFormat( ) ,
				new DegreesInclinationFormat( ) ,
				correctBacksights );
	}
	
	public DistAzmIncMeasurementFormat( Format<Double> distanceFormat , Format<Double> azimuthFormat , Format<Double> inclinationFormat , boolean correctBacksights )
	{
		super( );
		this.distanceFormat = distanceFormat;
		this.azimuthFormat = azimuthFormat;
		this.inclinationFormat = inclinationFormat;
		this.correctBacksights = correctBacksights;
	}
	
	private static final Pattern	invalidCharacterPattern	= Pattern.compile( "[^-+0-9;./ ]" );
	
	@Override
	public String format( DistAzmIncMeasurement t )
	{
		if( t == null )
		{
			return null;
		}
		
		StringBuilder sb = new StringBuilder( );
		sb.append( "Dist: " );
		sb.append( distanceFormat.format( t.distance ) );
		sb.append( "  Azm: " );
		sb.append( azimuthFormat.format( t.frontsightAzimuth ) );
		if( t.backsightAzimuth != null )
		{
			sb.append( '/' );
			sb.append( azimuthFormat.format( t.backsightAzimuth ) );
		}
		sb.append( "  Inc: " );
		sb.append( inclinationFormat.format( t.frontsightInclination ) );
		if( t.backsightInclination != null )
		{
			sb.append( '/' );
			sb.append( inclinationFormat.format( t.backsightInclination ) );
		}
		return sb.toString( );
	}
	
	@Override
	public DistAzmIncMeasurement parse( String s ) throws Exception
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
		if( parts.length < 2 )
		{
			throw new Exception( "Missing distance, azimuth, and inclination" );
		}
		
		Double distance = distanceFormat.parse( parts[ 0 ] );
		Double[ ] azimuth = parts.length > 2 ? parseAngles( parts[ 1 ] , azimuthFormat ) : null;
		Double[ ] inclination = parseAngles( parts[ parts.length - 1 ] , inclinationFormat );
		
		if( distance == null )
		{
			throw new IllegalArgumentException( "Missing distance" );
		}
		
		if( ArrayUtils.indexOf( inclination , i -> i != null ) < 0 )
		{
			throw new IllegalArgumentException( "Missing inclination" );
		}
		
		if( azimuth == null || ArrayUtils.indexOf( azimuth , a -> a != null ) < 0 )
		{
			if( ArrayUtils.indexOf( inclination , i -> i != null && Math.abs( i ) != Math.PI ) >= 0 )
			{
				throw new IllegalArgumentException( "Azimuth can only be omitted if inclination is vertical" );
			}
		}
		
		if( correctBacksights && azimuth != null && azimuth.length == 2 && azimuth[ 1 ] != null )
		{
			azimuth[ 1 ] = AngleUtils.oppositeAngle( azimuth[ 1 ] );
		}
		if( correctBacksights && inclination.length == 2 && inclination[ 1 ] != null )
		{
			inclination[ 1 ] = -inclination[ 1 ];
		}
		
		DistAzmIncMeasurement result = new DistAzmIncMeasurement( distance , azimuth , inclination );
		
		if( azimuth != null && azimuth.length == 2 && azimuth[ 0 ] != null && azimuth[ 1 ] != null && Math.abs( azimuth[ 0 ] - azimuth[ 1 ] ) > Math.toRadians( 2.0 ) )
		{
			throw new FormatWarning( "Warning: front/back azimuth differ by more than 2 degrees" , result );
		}
		
		if( inclination != null && inclination.length == 2 && inclination[ 0 ] != null && inclination[ 1 ] != null && Math.abs( inclination[ 0 ] - inclination[ 1 ] ) > Math.toRadians( 2.0 ) )
		{
			throw new FormatWarning( "Warning: front/back inclination differ by more than 2 degrees" , result );
		}
		
		return result;
	}
	
	private Double[ ] parseAngles( String angles , Format<Double> format ) throws Exception
	{
		String[ ] parts = angles.split( "/" );
		if( parts.length < 1 || parts.length > 2 )
		{
			throw new IllegalArgumentException( "Must provide only a frontsight and/or backsight" );
		}
		return ArrayUtils.throwableMap( parts , new Double[ parts.length ] , s -> format.parse( s ) );
	}
}
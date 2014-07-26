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
import org.andork.util.StringUtils;

public class NorthEastElevVectorMeasurementFormat implements Format<VectorMeasurement>
{
	Format<Double>					distanceFormat;
	
	private static final Pattern	invalidCharacterPattern	= Pattern.compile( "[^-+0-9;. ]" );
	
	public NorthEastElevVectorMeasurementFormat( )
	{
		this( new DefaultDistanceFormat( ) );
	}
	
	public NorthEastElevVectorMeasurementFormat( Format<Double> distanceFormat )
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
		return "dN: " + distanceFormat.format( -t.vector[ 2 ] ) + "  dE: " + distanceFormat.format( t.vector[ 0 ] ) + "  dElev: " + distanceFormat.format( t.vector[ 1 ] );
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
			throw new IllegalArgumentException( "Missing east and elevation offsets" );
		}
		if( parts.length == 2 )
		{
			throw new IllegalArgumentException( "Missing elevation offset" );
		}
		
		try
		{
			return new VectorMeasurement(
					distanceFormat.parse( parts[ 1 ] ) ,
					distanceFormat.parse( parts[ 2 ] ) ,
					-distanceFormat.parse( parts[ 0 ] ) );
		}
		catch( Exception ex )
		{
			throw new IllegalArgumentException( "Invalid input" );
		}
	}
}

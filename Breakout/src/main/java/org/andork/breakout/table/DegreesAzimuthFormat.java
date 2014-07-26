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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.andork.format.Format;

public class DegreesAzimuthFormat implements Format<Double>
{
	private static final NumberFormat	outFormat	= DecimalFormat.getInstance( );
	private static final NumberFormat	inFormat	= DecimalFormat.getInstance( );
	
	static
	{
		outFormat.setMinimumFractionDigits( 1 );
		outFormat.setMaximumFractionDigits( 1 );
	}
	
	@Override
	public String format( Double t )
	{
		return t == null ? "--" : outFormat.format( Math.toDegrees( t ) );
	}
	
	@Override
	public Double parse( String s ) throws Exception
	{
		if( s == null || "--".equals( s ) )
		{
			return null;
		}
		try
		{
			double degrees = inFormat.parse( s ).doubleValue( );
			if( degrees < 0 || degrees >= 360 )
			{
				throw new IllegalArgumentException( "Azimuth must be >= 0 and < 360" );
			}
			return Math.toRadians( degrees );
		}
		catch( Exception ex )
		{
			throw new IllegalArgumentException( "Invalid azimuth: " + s );
		}
	}
}

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

import org.andork.util.ArrayUtils;
import org.andork.util.FormatWarning;
import org.andork.util.StringUtils;

public class BisectorLrudCrossSectionFormat extends CrossSectionFormat
{
	public BisectorLrudCrossSectionFormat( )
	{
		super( );
	}
	
	public BisectorLrudCrossSectionFormat( DefaultDistanceFormat distanceFormat )
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
		return "bLRUD: " + StringUtils.join( " " , ArrayUtils.throwableMap( t.values , new String[ t.values.length ] , d -> distanceFormat.format( d ) ) );
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
		
		CrossSection result = new CrossSection( CrossSectionType.BISECTOR_LRUD , parsed );
		
		if( parts.length > 4 )
		{
			throw new FormatWarning( "Warning: extra numbers provided" , result );
		}
		return result;
	}
	
}

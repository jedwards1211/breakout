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
package org.andork.breakout.model;

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

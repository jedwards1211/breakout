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

import java.util.HashMap;
import java.util.Map;

import org.andork.func.Bimapper;

import com.andork.plot.LinearAxisConversion;

public class LinearAxisConversionMapBimapper implements Bimapper<LinearAxisConversion, Object>
{
	private LinearAxisConversionMapBimapper( )
	{
		
	}
	
	public static final LinearAxisConversionMapBimapper	instance	= new LinearAxisConversionMapBimapper( );
	
	@Override
	public Map<String, Double> map( LinearAxisConversion in )
	{
		if( in == null )
		{
			return null;
		}
		Map<String, Double> result = new HashMap<String, Double>( );
		result.put( "offset" , in.getOffset( ) );
		result.put( "scale" , in.getScale( ) );
		return result;
	}
	
	@Override
	public LinearAxisConversion unmap( Object out )
	{
		if( out == null || !( out instanceof Map ) )
		{
			return null;
		}
		Map<?, ?> m = ( Map<?, ?> ) out;
		LinearAxisConversion result = new LinearAxisConversion( );
		try
		{
			result.setOffset( Double.parseDouble( String.valueOf( m.get( "offset" ) ) ) );
			result.setScale( Double.parseDouble( String.valueOf( m.get( "scale" ) ) ) );
		}
		catch( Exception ex )
		{
			return null;
		}
		return result;
	}
}

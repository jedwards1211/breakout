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
package org.andork.breakout.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.andork.collect.InputStreamLineIterable;
import org.andork.collect.MultiMaps;
import org.andork.generic.Factory;

public class SurveyNearestFinder
{
	public static void main( String[ ] args )
	{
		List<String[ ]> lines = new ArrayList<String[ ]>( );
		Map<String, Set<Integer>> stationMap = new HashMap<String, Set<Integer>>( );
		
		int i = 0;
		for( String line : InputStreamLineIterable.perlDiamond( 2 , args ) )
		{
			String[ ] cols = line.split( "\t" );
			lines.add( cols );
			
			if( cols.length > 2 )
			{
				MultiMaps.put( stationMap , cols[ 0 ] , i , integerHashSetFactory );
				MultiMaps.put( stationMap , cols[ 1 ] , i , integerHashSetFactory );
			}
			
			i++ ;
		}
		
		Set<String> visited = new HashSet<String>( );
		LinkedHashSet<Integer> nearestSet = new LinkedHashSet<Integer>( );
		
		findNearest( lines , stationMap , args[ 0 ] , visited , Double.parseDouble( args[ 1 ] ) , nearestSet );
		
		List<Integer> nearest = new ArrayList<Integer>(nearestSet);
		
		Collections.sort( nearest );
		for( Integer i2 : nearest )
		{
			System.out.println( org.andork.util.StringUtils.join( "\t" , lines.get( i2 ) ) );
		}
		
	}
	
	private static final Factory<Set<Integer>>	integerHashSetFactory	= new Factory<Set<Integer>>( )
																		{
																			@Override
																			public Set<Integer> newInstance( )
																			{
																				return new HashSet<Integer>( );
																			}
																		};
	
	private static final Set<Integer>			emptyIntegerSet			= Collections.emptySet( );
	
	private static void findNearest( List<String[ ]> lines , Map<String, Set<Integer>> stationMap , String station , Set<String> visited , double remainingDist , LinkedHashSet<Integer> result )
	{
		if( !visited.add( station ) )
		{
			return;
		}
		
		for( int i : MultiMaps.get( stationMap , station , emptyIntegerSet ) )
		{
			String[ ] cols = lines.get( i );
			
			String nextStation = null;
			if( station.equals( cols[ 0 ] ) )
			{
				nextStation = cols[ 1 ];
			}
			if( station.equals( cols[ 1 ] ) )
			{
				if( nextStation != null )
				{
					continue;
				}
				nextStation = cols[ 0 ];
			}
			
			if( nextStation != null )
			{
				double dist;
				try
				{
					dist = Double.parseDouble( cols[ 2 ] );
				}
				catch( Exception ex )
				{
					continue;
				}
				
				if( dist <= remainingDist )
				{
					result.add( i );
					findNearest( lines , stationMap , nextStation , visited , remainingDist - dist , result );
				}
			}
		}
	}
}

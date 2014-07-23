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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.andork.collect.InputStreamLineIterable;

public class SurveyMetadataShuffler
{
	public static void main( String[ ] args )
	{
		List<String[ ]> lines = new ArrayList<String[ ]>( );
		
		String[ ] colIndexStrs = args[ 0 ].trim( ).split( "\\s*,\\s*" );
		int[ ] colIndices = new int[ colIndexStrs.length ];
		for( int i = 0 ; i < colIndexStrs.length ; i++ )
		{
			colIndices[ i ] = Integer.parseInt( colIndexStrs[ i ] );
		}
		
		for( String line : InputStreamLineIterable.perlDiamond( 1 , args ) )
		{
			String[ ] cols = line.split( "\t" );
			lines.add( cols );
		}
		
		for( int col : colIndices )
		{
			Map<String, String> shuffleMap = new HashMap<String, String>( );
			for( String[ ] line : lines )
			{
				if( line.length > col )
				{
					shuffleMap.put( line[ col ] , null );
				}
			}
			
			List<String> shuffled = new ArrayList<String>( shuffleMap.keySet( ) );
			Collections.shuffle( shuffled );
			
			Iterator<Map.Entry<String, String>> entryIter = shuffleMap.entrySet( ).iterator( );
			Iterator<String> shuffledIter = shuffled.iterator( );
			while( entryIter.hasNext( ) )
			{
				entryIter.next( ).setValue( shuffledIter.next( ) );
			}
			
			for( String[ ] line : lines )
			{
				if( line.length > col )
				{
					line[ col ] = shuffleMap.get( line[ col ] );
				}
			}
		}
		
		for( String[ ] line : lines )
		{
			System.out.println( org.andork.util.StringUtils.join( "\t" , line ) );
		}
	}
}

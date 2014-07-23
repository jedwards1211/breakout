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
package org.andork.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils
{
	private FileUtils( )
	{
		
	}
	
	public static List<File> split( File f )
	{
		List<File> result = new ArrayList<File>( );
		result.add( new File( f.getName( ) ) );
		f = f.getAbsoluteFile( ).getParentFile( );
		while( f != null )
		{
			result.add( 0 , new File( f.getName( ) ) );
			f = f.getParentFile( );
		}
		return result;
	}
	
	public static File canonicalize( File base , File target )
	{
		if( base.isFile( ) )
		{
			base = base.getParentFile( );
		}
		List<File> baseSplit = split( base );
		List<File> targetSplit = split( target );
		
		if( !baseSplit.get( 0 ).equals( targetSplit.get( 0 ) ) )
		{
			return target.getAbsoluteFile( );
		}
		
		File canonicalized = null;
		int start;
		for( start = 0 ; start < Math.min( targetSplit.size( ) , baseSplit.size( ) ) ; start++ )
		{
			if( !baseSplit.get( start ).equals( targetSplit.get( start ) ) )
			{
				break;
			}
		}
		
		for( int i = start ; i < baseSplit.size( ) ; i++ )
		{
			if( canonicalized == null )
			{
				canonicalized = new File( ".." );
			}
			else
			{
				canonicalized = new File( canonicalized , ".." );
			}
		}
		
		for( int i = start ; i < targetSplit.size( ) ; i++ )
		{
			if( canonicalized == null )
			{
				canonicalized = targetSplit.get( i );
			}
			else
			{
				canonicalized = new File( canonicalized , targetSplit.get( i ).getName( ) );
			}
		}
		
		return canonicalized;
	}
}

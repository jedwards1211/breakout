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
package org.andork.collect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

public class LineIterator extends EasyIterator<String>
{
	private BufferedReader	reader;
	
	public LineIterator( InputStream in )
	{
		try
		{
			reader = new BufferedReader( new InputStreamReader( in ) );
		}
		catch( Exception ex )
		{
			
		}
	}
	
	public LineIterator( Reader reader )
	{
		if( reader instanceof BufferedReader )
		{
			this.reader = ( BufferedReader ) reader;
		}
		else
		{
			try
			{
				this.reader = new BufferedReader( reader );
			}
			catch( Exception ex )
			{
				
			}
		}
	}
	
	public LineIterator( String file )
	{
		this( new File( file ) );
	}
	
	public LineIterator( File file )
	{
		try
		{
			InputStream in = new FileInputStream( file );
			reader = new BufferedReader( new InputStreamReader( in ) );
		}
		catch( Exception ex )
		{
			
		}
	}
	
	public LineIterator( URL url )
	{
		try
		{
			InputStream in = url.openStream( );
			reader = new BufferedReader( new InputStreamReader( in ) );
		}
		catch( Exception ex )
		{
			
		}
	}
	
	@Override
	protected String nextOrNull( )
	{
		String result = null;
		if( reader != null )
		{
			try
			{
				result = reader.readLine( );
			}
			catch( IOException e )
			{
			}
			finally
			{
				if( result == null )
				{
					finalize( );
				}
			}
		}
		return result;
	}
	
	public void finalize( )
	{
		try
		{
			reader.close( );
		}
		catch( Exception ex )
		{
			
		}
		reader = null;
	}
}

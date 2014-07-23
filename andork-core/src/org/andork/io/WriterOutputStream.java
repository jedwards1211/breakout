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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.LinkedList;

/**
 * Writes everything written to this {@link OutputStream} to a wrapped {@link Writer}.
 * 
 * @author Andy
 */
public class WriterOutputStream extends OutputStream
{
	Writer				writer;
	Reader				reader;
	LastByteInputStream	in		= new LastByteInputStream( );
	
	LinkedList<Integer>	bytes	= new LinkedList<Integer>( );
	
	public WriterOutputStream( Writer writer ) throws UnsupportedEncodingException
	{
		super( );
		this.reader = new InputStreamReader( in );
		this.writer = writer;
	}
	
	public WriterOutputStream( String readCharsetName , Writer writer ) throws UnsupportedEncodingException
	{
		super( );
		this.reader = new InputStreamReader( in , readCharsetName );
		this.writer = writer;
	}
	
	@Override
	public synchronized void write( int b ) throws IOException
	{
		bytes.add( b );
		
		if( reader.ready( ) )
		{
			writer.write( reader.read( ) );
		}
	}
	
	private class LastByteInputStream extends InputStream
	{
		@Override
		public int read( ) throws IOException
		{
			if( bytes.isEmpty( ) )
			{
				throw new IOException( "Stream is not ready" );
			}
			return bytes.poll( );
		}
		
		@Override
		public int available( ) throws IOException
		{
			return bytes.size( );
		}
	}
}

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

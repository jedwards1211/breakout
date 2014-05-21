package org.andork.collect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class LineIterator extends EasyIterator<String>
{
	private InputStream		in;
	private BufferedReader	reader;
	
	public LineIterator( String file )
	{
		this( new File( file ) );
	}
	
	public LineIterator( File file )
	{
		try
		{
			in = new FileInputStream( file );
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
			in = url.openStream( );
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
			in.close( );
		}
		catch( Exception ex )
		{
			
		}
		in = null;
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

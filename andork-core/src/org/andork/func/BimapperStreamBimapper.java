package org.andork.func;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class BimapperStreamBimapper<T> implements StreamBimapper<T>
{
	Bimapper<T, String>	format;
	boolean				closeStreams;
	
	public BimapperStreamBimapper( Bimapper<T, String> format )
	{
		this( format , true );
	}
	
	public BimapperStreamBimapper( Bimapper<T, String> format , boolean closeStreams )
	{
		super( );
		this.format = format;
		this.closeStreams = closeStreams;
	}
	
	@Override
	public void write( T t , OutputStream out ) throws Exception
	{
		OutputStreamWriter writer = null;
		try
		{
			writer = new OutputStreamWriter( out );
			writer.write( format.map( t ) );
			writer.flush( );
		}
		finally
		{
			if( closeStreams && writer != null )
			{
				try
				{
					writer.close( );
				}
				catch( Exception ex )
				{
					
				}
			}
		}
	}
	
	@Override
	public T read( InputStream in ) throws Exception
	{
		InputStreamReader reader = null;
		
		try
		{
			reader = new InputStreamReader( in );
			StringBuffer sb = new StringBuffer( );
			int c;
			
			while( ( c = reader.read( ) ) >= 0 )
			{
				sb.append( ( char ) c );
			}
			return format.unmap( sb.toString( ) );
		}
		finally
		{
			if( closeStreams && reader != null )
			{
				try
				{
					reader.close( );
				}
				catch( Exception ex )
				{
					
				}
			}
		}
	}
}

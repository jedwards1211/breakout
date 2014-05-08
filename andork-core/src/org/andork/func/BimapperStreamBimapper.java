package org.andork.func;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class BimapperStreamBimapper<T> implements StreamBimapper<T>
{
	Bimapper<T, String>	format;
	
	public BimapperStreamBimapper( Bimapper<T, String> format )
	{
		super( );
		this.format = format;
	}
	
	@Override
	public void write( T t , OutputStream out ) throws Exception
	{
		OutputStreamWriter writer = null;
		try
		{
			writer = new OutputStreamWriter( out );
			writer.write( format.map( t ) );
		}
		finally
		{
			if( writer != null )
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
			if( reader != null )
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

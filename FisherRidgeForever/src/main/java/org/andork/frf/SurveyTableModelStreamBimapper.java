package org.andork.frf;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import org.andork.frf.SurveyTableModel.Row;
import org.andork.func.StreamBimapper;
import org.andork.snakeyaml.YamlObject;

public class SurveyTableModelStreamBimapper implements StreamBimapper<SurveyTableModel>
{
	public static final SurveyTableModelStreamBimapper	instance	= new SurveyTableModelStreamBimapper( );
	
	@Override
	public void write( SurveyTableModel model , OutputStream out ) throws Exception
	{
		PrintStream p = null;
		
		try
		{
			p = new PrintStream( out );
			
			for( int ri = 0 ; ri < model.getRowCount( ) ; ri++ )
			{
				YamlObject<Row> row = model.getRow( ri );
				
				for( int ci = 0 ; ci < Row.shot.getIndex( ) ; ci++ )
				{
					if( ci > 0 )
					{
						p.print( '\t' );
					}
					if( row.valueAt( ci ) != null )
					{
						p.print( row.valueAt( ci ).toString( ) );
					}
				}
				p.println( );
			}
		}
		finally
		{
			if( p != null )
			{
				try
				{
					p.close( );
				}
				catch( Exception ex )
				{
					ex.printStackTrace( );
				}
			}
		}
	}
	
	@Override
	public SurveyTableModel read( InputStream in ) throws Exception
	{
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader( new InputStreamReader( in ) );
			
			SurveyTableModel result = new SurveyTableModel( );
			
			String line;
			int ri = 0;
			while( ( line = reader.readLine( ) ) != null )
			{
				int ci = 0;
				for( String s : line.split( "\t" ) )
				{
					result.setValueAt( s , ri , ci++ );
				}
				ri++ ;
			}
			
			return result;
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
					ex.printStackTrace( );
				}
			}
		}
		
	}
}

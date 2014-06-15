package org.andork.breakout;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import org.andork.breakout.model.SurveyTableModel;
import org.andork.breakout.model.SurveyTableModel.Row;
import org.andork.breakout.model.SurveyTableModel.SurveyTableModelCopier;
import org.andork.snakeyaml.YamlObject;
import org.andork.swing.async.Subtask;
import org.andork.swing.async.SubtaskStreamBimapper;

public class SurveyTableModelStreamBimapper extends SubtaskStreamBimapper<SurveyTableModel>
{
	public SurveyTableModelStreamBimapper( Subtask subtask )
	{
		super( subtask );
	}
	
	@Override
	public void write( final SurveyTableModel model , OutputStream out ) throws Exception
	{
		SurveyTableModel copy = new SurveyTableModel( );
		SurveyTableModelCopier copier = new SurveyTableModelCopier( );
		
		copier.copyInBackground( model , copy , 1000 , null );
		
		PrintStream p = null;
		
		subtask( ).setTotal( copy.getRowCount( ) );
		subtask( ).setCompleted( 0 );
		subtask( ).setIndeterminate( false );
		
		try
		{
			p = new PrintStream( out );
			
			for( int ri = 0 ; ri < copy.getRowCount( ) ; ri++ )
			{
				YamlObject<Row> row = copy.getRow( ri );
				
				for( int ci = 0 ; ci < copy.getColumnCount( ) ; ci++ )
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
				
				subtask( ).setCompleted( ri );
			}
		}
		finally
		{
			subtask( ).end( );
			
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
		subtask( ).setIndeterminate( true );
		
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
			subtask( ).end( );
			
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

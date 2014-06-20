package org.andork.breakout;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import org.andork.breakout.model.SurveyTableModel;
import org.andork.breakout.model.SurveyTableModel.Row;
import org.andork.breakout.model.SurveyTableModel.SurveyTableModelCopier;
import org.andork.q.QObject;
import org.andork.swing.async.Subtask;
import org.andork.swing.async.SubtaskStreamBimapper;

public class SurveyTableModelStreamBimapper extends SubtaskStreamBimapper<SurveyTableModel>
{
	boolean	closeStreams;
	boolean	makeCopy;
	
	public SurveyTableModelStreamBimapper( Subtask subtask )
	{
		super( subtask );
	}
	
	public SurveyTableModelStreamBimapper closeStreams( boolean closeStreams )
	{
		this.closeStreams = closeStreams;
		return this;
	}
	
	public SurveyTableModelStreamBimapper makeCopy( boolean makeCopy )
	{
		this.makeCopy = makeCopy;
		return this;
	}
	
	@Override
	public void write( SurveyTableModel model , OutputStream out ) throws Exception
	{
		if( makeCopy )
		{
			SurveyTableModel copy = new SurveyTableModel( );
			SurveyTableModelCopier copier = new SurveyTableModelCopier( );
			
			copier.copyInBackground( model , copy , 1000 , null );
			model = copy;
		}
		
		PrintStream p = null;
		
		subtask( ).setTotal( model.getRowCount( ) );
		subtask( ).setCompleted( 0 );
		subtask( ).setIndeterminate( false );
		
		try
		{
			p = new PrintStream( out );
			
			for( int ri = 0 ; ri < model.getRowCount( ) ; ri++ )
			{
				QObject<Row> row = model.getRow( ri );
				
				for( int ci = 0 ; ci < model.getColumnCount( ) ; ci++ )
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
			
			if( closeStreams && p != null )
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
					if( ci >= SurveyTableModel.Row.instance.getAttributeCount( ) )
					{
						break;
					}
					if( String.class.isAssignableFrom( SurveyTableModel.Row.instance.attributeAt( ci ).getValueClass( ) ) )
					{
						result.setValueAt( s , ri , ci );
					}
					ci++ ;
				}
				ri++ ;
			}
			
			return result;
		}
		finally
		{
			subtask( ).end( );
			
			if( closeStreams && reader != null )
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

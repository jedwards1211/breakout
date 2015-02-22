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
package org.breakout;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.andork.func.DateBimapper;
import org.andork.q.QObject;
import org.andork.q.QObjectTabDelimBimapper;
import org.andork.q.QSpec.Attribute;
import org.andork.swing.async.Subtask;
import org.andork.swing.async.SubtaskStreamBimapper;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.SurveyTableModel.Row;
import org.breakout.model.SurveyTableModel.SurveyTableModelCopier;

public class SurveyTableModelStreamBimapper extends SubtaskStreamBimapper<SurveyTableModel>
{
	boolean							closeStreams;
	boolean							makeCopy;
	
	QObjectTabDelimBimapper<Row>	rowBimapper;
	
	public SurveyTableModelStreamBimapper( Subtask subtask )
	{
		super( subtask );
		rowBimapper = QObjectTabDelimBimapper.newInstance( Row.instance )
				.addColumn( "From" , Row.from )
				.addColumn( "To" , Row.to )
				.addColumn( "Distance" , Row.distance )
				.addColumn( "Frontsight Azimuth" , Row.fsAzm )
				.addColumn( "Frontsight Inclination" , Row.fsInc )
				.addColumn( "Backsight Azimuth" , Row.bsAzm )
				.addColumn( "Backsight Inclination" , Row.bsInc )
				.addColumn( "Left" , Row.left )
				.addColumn( "Right" , Row.right )
				.addColumn( "Up" , Row.up )
				.addColumn( "Down" , Row.down )
				.addColumn( "North" , Row.north )
				.addColumn( "East" , Row.east )
				.addColumn( "Elevation" , Row.elev )
				.addColumn( "Description" , Row.desc )
				.addColumn( "Date" , Row.date )
				.addColumn( "Surveyors" , Row.surveyors )
				.addColumn( "Comment" , Row.comment )
				.addColumn( "Scanned Notes" , Row.scannedNotes );
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
			
			p.println( rowBimapper.createHeader( ) );
			
			for( int ri = 0 ; ri < model.getRowCount( ) ; ri++ )
			{
				p.println( rowBimapper.map( model.getRow( ri ) ) );
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
			
			String line = reader.readLine( );
			if( line == null )
			{
				return result;
			}
			
			QObjectTabDelimBimapper<Row> rowBimapper = this.rowBimapper.deriveFromHeader( line );
			
			int ri = 0;
			while( ( line = reader.readLine( ) ) != null )
			{
				result.setRow( ri++ , rowBimapper.unmap( line ) );
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

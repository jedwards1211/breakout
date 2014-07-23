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
package org.andork.breakout.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andork.breakout.SurveyTableModelStreamBimapper;
import org.andork.breakout.model.SurveyTableModel;
import org.andork.breakout.model.SurveyTableModel.Row;
import org.andork.collect.InputStreamLineIterable;
import org.andork.collect.LineIterable;
import org.andork.q.QObject;
import org.andork.swing.async.Subtask;

public class WallsWrangler1
{
	public static void main( String[ ] args ) throws Exception
	{
		SurveyTableModel model = new SurveyTableModel( );
		InputStreamLineIterable.grepFiles( args[ 0 ] ).forEach( path -> {
			InputStreamLineIterable.grep( path.toFile( ) , Pattern.compile( "\\.NAME\t(.*)" ) ).forEach(
					matcher -> {
						System.out.println( matcher.group( 1 ) );
						readFile( new File( path.getParent( ).toFile( ) , matcher.group( 1 ) + ".SRV" ) , model );
					} );
		} );
		
		FileOutputStream out = new FileOutputStream( "breakout.txt" );
		new SurveyTableModelStreamBimapper( new Subtask( ) ).write( model , System.out );
		new SurveyTableModelStreamBimapper( new Subtask( ) ).write( model , out );
		out.close( );
	}
	
	private static final Pattern	surveyorPattern1	= Pattern.compile( "\\s*(?:duty|sketch|book|tape|instruments?|inst\\.|compass|clino)\\s*[:-]\\s*(.+)\\s*" , Pattern.CASE_INSENSITIVE );
	private static final Pattern	surveyorPattern2	= Pattern.compile( "\\s*([^-:]+)\\s*[:-]\\s*(?:duty|sketch|book|tape|instruments?|inst\\.|compass|clino)\\s*" , Pattern.CASE_INSENSITIVE );
	
	private static String parseAzm( String azm )
	{
		boolean north;
		if( azm.startsWith( "N" ) )
		{
			north = true;
		}
		else if( azm.startsWith( "S" ) )
		{
			north = false;
		}
		else
		{
			return azm;
		}
		
		boolean east;
		if( azm.endsWith( "E" ) )
		{
			east = true;
		}
		else if( azm.endsWith( "W" ) )
		{
			east = false;
		}
		else
		{
			return azm;
		}
		
		azm = azm.substring( 1 , azm.length( ) - 1 );
		
		double value;
		try
		{
			value = Double.parseDouble( azm );
		}
		catch( Exception ex )
		{
			return azm;
		}
		
		if( north )
		{
			if( east )
			{
				return Double.toString( value );
			}
			else
			{
				return String.format( "%.2f" , 360.0 - value );
			}
		}
		else
		{
			if( east )
			{
				return String.format( "%.2f" , 180.0 - value );
			}
			else
			{
				return String.format( "%.2f" , 180.0 + value );
			}
		}
	}
	
	private static void readFile( File file , SurveyTableModel model )
	{
		Date date = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
		String desc = null;
		StringBuilder surveyors = new StringBuilder( );
		
		int semicolonCount = 0;
		
		boolean processHeader = true;
		
		int distCol = -1;
		int azmCol = -1;
		int incCol = -1;
		
		double distMultiplier = 3.28084;
		
		for( String line : LineIterable.linesOf( file ) )
		{
			String lcLine = line.toLowerCase( );
			line = line.trim( );
			if( line.startsWith( ";" ) )
			{
				if( !processHeader )
				{
					continue;
				}
				if( ++semicolonCount == 2 )
				{
					if( line.length( ) > 2 )
					{
						desc = line.substring( 1 ).trim( );
					}
				}
				
				String surveyorList = null;
				for( Pattern p : Arrays.asList( surveyorPattern2 , surveyorPattern1 ) )
				{
					Matcher m = p.matcher( line );
					if( m.find( ) )
					{
						surveyorList = m.group( 1 );
						break;
					}
				}
				
				if( surveyorList == null )
				{
					continue;
				}
				for( String s : surveyorList.split( ",|;" ) )
				{
					s = s.trim( );
					if( s.length( ) > 0 )
					{
						if( surveyors.length( ) > 0 )
						{
							surveyors.append( ", " );
						}
						surveyors.append( s );
					}
				}
			}
			else if( line.startsWith( "#" ) )
			{
				if( lcLine.startsWith( "#date" ) )
				{
					try
					{
						date = dateFormat.parse( line.substring( 5 ).trim( ) );
					}
					catch( ParseException e )
					{
						e.printStackTrace( );
					}
				}
				if( lcLine.startsWith( "#units" ) )
				{
					String last3 = line.toLowerCase( ).trim( ).substring( line.length( ) - 3 , line.length( ) );
					distCol = last3.indexOf( 'd' ) + 2;
					azmCol = last3.indexOf( 'a' ) + 2;
					incCol = last3.indexOf( 'v' ) + 2;
					if( lcLine.contains( "feet" ) )
					{
						distMultiplier = 1.0;
					}
				}
			}
			else
			{
				int commentIndex = line.indexOf( ';' );
				String comment = null;
				if( commentIndex >= 0 )
				{
					comment = line.substring( commentIndex + 1 );
					line = line.substring( 0 , commentIndex - 1 );
				}
				String[ ] parts = line.split( "\\t" );
				if( parts.length < 5 )
				{
					continue;
				}
				
				processHeader = false;
				
				QObject<SurveyTableModel.Row> row = SurveyTableModel.Row.instance.newObject( );
				row.set( Row.from , parts[ 0 ] );
				row.set( Row.to , parts[ 1 ] );
				
				double dist;
				try
				{
					dist = Double.parseDouble( parts[ distCol ] );
					row.set( Row.distance , String.format( "%.2f" , dist * distMultiplier ) );
				}
				catch( Exception ex )
				{
					row.set( Row.distance , parts[ distCol ] );
				}
				
				String[ ] splitInc = parts[ incCol ].split( "/" );
				if( splitInc.length > 1 )
				{
					row.set( Row.bsInc , splitInc[ 1 ] );
				}
				row.set( Row.fsInc , splitInc[ 0 ] );
				
				String[ ] splitAzm = parts[ azmCol ].split( "/" );
				if( splitAzm.length > 1 )
				{
					row.set( Row.bsAzm , parseAzm( splitAzm[ 1 ] ) );
				}
				row.set( Row.fsAzm , parseAzm( splitAzm[ 0 ] ) );
				
				if( date != null )
				{
					row.set( Row.date , dateFormat.format( date ) );
				}
				if( surveyors.length( ) > 0 )
				{
					row.set( Row.surveyors , surveyors.toString( ) );
				}
				if( desc != null )
				{
					row.set( Row.desc , desc );
				}
				
				if( comment != null )
				{
					row.set( Row.comment , comment );
				}
				
				row.set( Row.left , "1" );
				row.set( Row.right , "1" );
				row.set( Row.up , "1" );
				row.set( Row.down , "1" );
				
				model.addRow( row );
			}
		}
	}
}

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
package org.breakout.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andork.collect.LineIterable;
import org.andork.collect.LinkedHashSetMultiMap;
import org.andork.collect.MultiMap;
import org.andork.util.Java7.Objects;
import org.andork.util.StringUtils;

public class ScannedNotesMerger
{
	public static void main( String[ ] args ) throws Exception
	{
		File statFile = new File( "C:\\Users\\Andy\\Documents\\FRCS\\Data\\STAT.txt" );
		File surveyFile = new File( "C:\\Users\\Andy\\Documents\\FRCS\\Breakout Settings (FRCS Only)\\frcs-survey.txt" );
		File notesDir = new File( "C:\\Users\\Andy\\Documents\\FRCS\\Survey Notes" );
		
		List<List<String>> shots = new ArrayList<List<String>>( );
		MultiMap<String, List<String>> indexedShots = LinkedHashSetMultiMap.newInstance( );
		
		for( String line : LineIterable.linesOf( surveyFile ) )
		{
			List<String> split = new ArrayList<String>( Arrays.asList( line.split( "\t" ) ) );
			shots.add( split );
			
			String alpha0 = alpha( split.get( 0 ) );
			String alpha1 = alpha( split.get( 1 ) );
			
			if( !StringUtils.isNullOrEmpty( alpha0 ) )
			{
				indexedShots.put( alpha0 , split );
			}
			if( !StringUtils.isNullOrEmpty( alpha1 ) )
			{
				indexedShots.put( alpha1 , split );
			}
		}
		
		Map<Integer, File> notesFiles = new HashMap<>( );
		MultiMap<Date, File> datedFiles = LinkedHashSetMultiMap.newInstance( );
		
		Pattern notesNamePattern = Pattern.compile( "FRCS_(\\d+)_((\\d+)-(\\d+)-(\\d+))\\.pdf" );
		
		SimpleDateFormat fileDateFormat = new SimpleDateFormat( "MM-dd-yyyy" );
		
		for( File file : notesDir.listFiles( ) )
		{
			String name = file.getName( );
			Matcher m = notesNamePattern.matcher( name );
			if( m.find( ) )
			{
				notesFiles.put( Integer.parseInt( m.group( 1 ) ) , file );
				datedFiles.put( fileDateFormat.parse( m.group( 2 ) ) , file );
			}
		}
		
		Iterator<String> statIter = LineIterable.linesOf( statFile ).iterator( );
		
		int lineNumber = 0;
		int lastHeaderLine = -1;
		int tripNumber = -1;
		List<StationMatcher> tripMatchers = null;
		Date tripDate;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat( "MM/dd/yy" );
		
		Map<Date, Map<Integer, List<StationMatcher>>> stationMatchers = new HashMap<>( );
		
		while( statIter.hasNext( ) )
		{
			String line = statIter.next( );
			if( line.length( ) > 3 && line.substring( 0 , 3 ).matches( "\\s*\\d+" ) )
			{
				tripNumber = Integer.parseInt( line.substring( 0 , 3 ).trim( ) );
				lastHeaderLine = lineNumber;
				tripDate = dateFormat.parse( line.substring( 5 , 13 ).replace( ' ' , '0' ) );
				tripMatchers = new ArrayList<>( );
				Map<Integer, List<StationMatcher>> matchers = stationMatchers.get( tripDate );
				if( matchers == null )
				{
					matchers = new HashMap<>( );
					matchers.put( tripNumber , tripMatchers );
					stationMatchers.put( tripDate , matchers );
				}
			}
			
			if( lastHeaderLine >= 0 && lineNumber - lastHeaderLine > 1 )
			{
				int step = 13;
				for( int i = 33 ; i < line.length( ) ; i += step )
				{
					String part = line.substring( i , i + step ).trim( );
					if( !part.isEmpty( ) )
					{
						tripMatchers.add( new StationMatcher( part ) );
					}
				}
			}
			
			lineNumber++ ;
		}
		
		List<String> header = shots.get( 0 );
		int dateColumn = -1;
		int scannedNotesColumn = -1;
		for( int i = 0 ; i < header.size( ) ; i++ )
		{
			if( "Date".equalsIgnoreCase( header.get( i ) ) )
			{
				dateColumn = i;
			}
			if( "Scanned Notes".equalsIgnoreCase( header.get( i ) ) )
			{
				scannedNotesColumn = i;
			}
		}
		
		List<String> prevShot = null;
		
		for( List<String> shot : shots )
		{
			List<String> actualPrevShot = prevShot;
			prevShot = shot;
			
			if( shot.size( ) <= dateColumn )
			{
				continue;
			}
			Date shotDate = null;
			try
			{
				shotDate = dateFormat.parse( shot.get( dateColumn ) );
			}
			catch( Exception ex )
			{
				continue;
			}
			
			Map<Integer, List<StationMatcher>> matchers = stationMatchers.get( shotDate );
			
			boolean fromMatched = false;
			boolean toMatched = false;
			
			if( matchers != null )
			{
				for( Map.Entry<Integer, List<StationMatcher>> entry : matchers.entrySet( ) )
				{
					fromMatched = false;
					toMatched = false;
					
					int eTripNumber = entry.getKey( );
					File notesFile = notesFiles.get( eTripNumber );
					if( notesFile == null )
					{
						continue;
					}
					
					if( matchers.size( ) > 1 )
					{
						List<StationMatcher> eTripMatchers = entry.getValue( );
						for( StationMatcher matcher : eTripMatchers )
						{
							if( matcher.matches( shot.get( 0 ) ) )
							{
								fromMatched = true;
								break;
							}
						}
						if( !fromMatched )
						{
							break;
						}
						for( StationMatcher matcher : eTripMatchers )
						{
							if( matcher.matches( shot.get( 1 ) ) )
							{
								toMatched = true;
								break;
							}
						}
						if( !toMatched )
						{
							break;
						}
					}
					else
					{
						fromMatched = toMatched = true;
					}
					
					if( fromMatched && toMatched )
					{
						while( shot.size( ) <= scannedNotesColumn )
						{
							shot.add( "" );
						}
						shot.set( scannedNotesColumn , notesFile.toString( ) );
						break;
					}
				}
			}
			
			if( !fromMatched || !toMatched )
			{
				if( actualPrevShot.size( ) > scannedNotesColumn && shot.get( dateColumn ).equals( actualPrevShot.get( dateColumn ) ) )
				{
					while( shot.size( ) <= scannedNotesColumn )
					{
						shot.add( "" );
					}
					shot.set( scannedNotesColumn , actualPrevShot.get( scannedNotesColumn ) );
				}
				else if( datedFiles.get( shotDate ).size( ) == 1 )
				{
					while( shot.size( ) <= scannedNotesColumn )
					{
						shot.add( "" );
					}
					shot.set( scannedNotesColumn , datedFiles.getOnlyValue( shotDate ).toString( ) );
				}
			}
		}
		
		FileOutputStream fileOut = new FileOutputStream( new File( surveyFile.getParent( ) , "frcs-new-survey.txt" ) );
		PrintStream out = new PrintStream( fileOut );
		
		for( List<String> shot : shots )
		{
			System.out.println( StringUtils.join( "\t" , shot ) );
			out.println( StringUtils.join( "\t" , shot ) );
		}
	}
	
	private static String alpha( String station )
	{
		int start;
		for( start = 0 ; start < station.length( ) ; start++ )
		{
			if( Character.isDigit( station.charAt( start ) ) )
			{
				break;
			}
		}
		return station.substring( 0 , start );
	}
	
	private static Integer number( String station )
	{
		int start, end;
		for( start = 0 ; start < station.length( ) ; start++ )
		{
			if( Character.isDigit( station.charAt( start ) ) )
			{
				break;
			}
		}
		
		for( end = start + 1 ; end < station.length( ) ; end++ )
		{
			if( !Character.isDigit( station.charAt( end ) ) )
			{
				break;
			}
		}
		
		if( start < station.length( ) )
		{
			try
			{
				return Integer.parseInt( station.substring( start , end ) );
			}
			catch( Exception ex )
			{
				ex.printStackTrace( );
			}
		}
		return -1;
	}
	
	private static class StationMatcher
	{
		String	alpha;
		Integer	start;
		Integer	end;
		
		Pattern	surveyRangePattern	= Pattern.compile( "([A-Za-z$]+)(\\d+)-([A-Za-z$]+)(\\d+)" );
		Pattern	singleSurveyPattern	= Pattern.compile( "([A-Za-z$]+)(\\d+)?(\\s+(SIDE|LOOP))?" );
		
		public String toString( )
		{
			StringBuilder sb = new StringBuilder( );
			if( alpha != null )
			{
				sb.append( alpha );
			}
			if( start != null )
			{
				sb.append( start );
				if( end != null && !end.equals( start ) )
				{
					sb.append( '-' ).append( alpha ).append( end );
				}
			}
			return sb.toString( );
		}
		
		private StationMatcher( String s )
		{
			Matcher m;
			m = surveyRangePattern.matcher( s );
			if( m.find( ) )
			{
				alpha = m.group( 1 );
				start = Integer.parseInt( m.group( 2 ) );
				end = Integer.parseInt( m.group( 4 ) );
				
				if( end < start )
				{
					int temp = start;
					start = end;
					end = temp;
				}
			}
			else
			{
				m = singleSurveyPattern.matcher( s );
				if( m.find( ) )
				{
					alpha = m.group( 1 );
					if( m.group( 2 ) != null )
					{
						start = end = Integer.parseInt( m.group( 2 ) );
					}
				}
			}
		}
		
		public boolean matches( String station )
		{
			String salpha = alpha( station );
			Integer snumber = number( station );
			
			if( !Objects.equals( alpha , salpha ) )
			{
				return false;
			}
			
			if( start != null && snumber != null && snumber < start )
			{
				return false;
			}
			if( end != null && snumber != null && snumber > end )
			{
				return false;
			}
			return true;
		}
	}
}

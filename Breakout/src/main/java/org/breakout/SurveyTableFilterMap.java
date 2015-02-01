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

import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import org.breakout.model.SurveyTableModel;

public class SurveyTableFilterMap implements BiFunction<String, String, RowFilter<TableModel, Integer>>
{
	private SurveyTable	table;
	
	public SurveyTableFilterMap( SurveyTable table )
	{
		this.table = table;
	}
	
	private int columnOf( String property )
	{
		for( int column = 0 ; column < table.getColumnCount( ) ; column++ )
		{
			if( property.trim( ).equalsIgnoreCase( table.getColumnModel( ).getColumn( column ).getHeaderValue( ).toString( ).trim( ) ) )
			{
				return table.convertColumnIndexToModel( column );
			}
		}
		return -1;
	}
	
	private static Pattern	regexPattern	= Pattern.compile( "(.+)\\s*\\(\\s*regex\\s*\\)" );
	
	@Override
	public RowFilter<TableModel, Integer> apply( String prefix , String query )
	{
		prefix = prefix.trim( );
		query = query.trim( );
		
		String property = prefix;
		
		Matcher m = regexPattern.matcher( prefix );
		if( m.find( ) )
		{
			property = m.group( 1 );
			
			if( property.matches( "stations?|shots?|surveys?" ) )
			{
				return new SurveyRegexFilter( query );
			}
			
			int column = columnOf( property );
			
			return column < 0 ? null : RowFilter.regexFilter( query , column );
		}
		
		if( property.matches( "stations?|shots?|surveys?" ) )
		{
			return new SurveyDesignationFilter( query );
		}
		
		if( property.matches( "desc(ription)?" ) )
		{
			return new DescriptionFilter( query );
		}
		
		if( property.matches( "surveyors?" ) )
		{
			return new SurveyorFilter( query );
		}
		
		return null;
	}
}

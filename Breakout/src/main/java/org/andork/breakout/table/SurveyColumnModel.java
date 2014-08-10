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
package org.andork.breakout.table;

import java.util.Map;

import org.andork.breakout.ObjectYamlBimapper;
import org.andork.func.Bimapper;
import org.andork.func.CompoundBimapper;
import org.andork.func.NullBimapper;
import org.andork.q.QObject;
import org.andork.q.QObjectMapBimapper;
import org.andork.q.QSpec;
import org.andork.swing.FormatAndDisplayInfo;

public class SurveyColumnModel extends QSpec<SurveyColumnModel>
{
	public static final Attribute<Boolean>								fixed			= newAttribute( Boolean.class , "fixed" );
	public static final Attribute<Boolean>								visible			= newAttribute( Boolean.class , "show" );
	public static final Attribute<Integer>								id				= newAttribute( Integer.class , "id" );
	public static final Attribute<String>								name			= newAttribute( String.class , "name" );
	public static final Attribute<SurveyColumnType>						type			= newAttribute( SurveyColumnType.class , "type" );
	public static final Attribute<FormatAndDisplayInfo<?>>				defaultFormat	= newAttribute( FormatAndDisplayInfo.class , "defaultFormat" );
	
	public static final SurveyColumnModel								instance		= new SurveyColumnModel( );
	
	public static final Bimapper<QObject<SurveyColumnModel>, String>	yamlBimapper;
	public static final QObjectMapBimapper<SurveyColumnModel>			objectBimapper;
	
	static
	{
		objectBimapper = new QObjectMapBimapper<SurveyColumnModel>( instance )
		{
			
			@Override
			public Object map( QObject<SurveyColumnModel> in )
			{
				Map<Object, Object> m = ( Map<Object, Object> ) super.map( in );
				FormatAndDisplayInfo<?> format = in.get( defaultFormat );
				if( format != null )
				{
					m.put( defaultFormat.getName( ) , format.name( ) );
				}
				return m;
			}
			
			@Override
			public QObject<SurveyColumnModel> unmap( Object out )
			{
				Map<Object, Object> m = ( Map<Object, Object> ) out;
				QObject<SurveyColumnModel> q = ( QObject<SurveyColumnModel> ) super.unmap( out );
				String formatId = ( String ) m.get( defaultFormat.getName( ) );
				SurveyColumnType t = q.get( type );
				if( t != null )
				{
					q.set( defaultFormat , t.defaultFormat );
					for( FormatAndDisplayInfo<?> format : t.availableFormats )
					{
						if( format.id( ).equals( formatId ) )
						{
							q.set( defaultFormat , format );
							break;
						}
					}
				}
				return q;
			}
		}.map( defaultFormat , new NullBimapper<>( ) );
		
		yamlBimapper = CompoundBimapper.compose( objectBimapper , new ObjectYamlBimapper( ) );
	}
	
	private SurveyColumnModel( )
	{
		
	}
}
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

import org.andork.breakout.table.NewSurveyTableModel.SurveyColumnType;
import org.andork.q.QSpec;
import org.andork.swing.FormatAndDisplayInfo;

public class SurveyColumnModel extends QSpec<SurveyColumnModel>
{
	public static final Attribute<Boolean>					fixed			= newAttribute( Boolean.class , "fixed" );
	public static final Attribute<Boolean>					visible			= newAttribute( Boolean.class , "show" );
	public static final Attribute<String>					name			= newAttribute( String.class , "name" );
	public static final Attribute<SurveyColumnType>			type			= newAttribute( SurveyColumnType.class , "type" );
	public static final Attribute<FormatAndDisplayInfo<?>>	defaultFormat	= newAttribute( FormatAndDisplayInfo.class , "defaultFormat" );
	
	public static final SurveyColumnModel					instance		= new SurveyColumnModel( );
	
	private SurveyColumnModel( )
	{
		
	}
}
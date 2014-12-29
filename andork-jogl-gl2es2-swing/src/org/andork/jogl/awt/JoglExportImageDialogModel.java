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
package org.andork.jogl.awt;

import java.math.BigDecimal;

import org.andork.func.Bimapper;
import org.andork.q.QObject;
import org.andork.q.QObjectMapBimapper;
import org.andork.q.QSpec;

public class JoglExportImageDialogModel extends QSpec<JoglExportImageDialogModel>
{
	public static enum PrintSizeUnit
	{
		INCHES , CENTIMETERS;
		
		String	displayName;
		
		public String toString( )
		{
			return displayName;
		}
	}
	
	public static enum ResolutionUnit
	{
		PIXELS_PER_IN , PIXELS_PER_CM;
		
		String	displayName;
		
		public String toString( )
		{
			return displayName;
		}
	}
	
	public static final Attribute<String>									outputDirectory	= newAttribute( String.class , "outputDirectory" );
	public static final Attribute<String>									fileNamePrefix	= newAttribute( String.class , "fileNamePrefix" );
	public static final Attribute<Integer>									fileNumber		= newAttribute( Integer.class , "fileNumber" );
	public static final Attribute<Integer>									pixelWidth		= newAttribute( Integer.class , "pixelWidth" );
	public static final Attribute<Integer>									pixelHeight		= newAttribute( Integer.class , "pixelHeight" );
	public static final Attribute<BigDecimal>								resolution		= newAttribute( BigDecimal.class , "resolution" );
	public static final Attribute<JoglExportImageDialogModel.ResolutionUnit>	resolutionUnit	= newAttribute( JoglExportImageDialogModel.ResolutionUnit.class , "resolutionUnit" );
	public static final Attribute<Integer>									numSamples		= newAttribute( Integer.class , "numSamples" );
	
	private JoglExportImageDialogModel( )
	{
		
	}
	
	public static final JoglExportImageDialogModel							instance	= new JoglExportImageDialogModel( );
	
	public static final Bimapper<QObject<JoglExportImageDialogModel>, Object>	defaultMapper;
	
	static
	{
		defaultMapper = new QObjectMapBimapper<JoglExportImageDialogModel>( instance );
	}
}

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
package org.andork.breakout.model;

import java.awt.Color;
import java.io.File;

import org.andork.awt.layout.DrawerModel;
import org.andork.breakout.CameraView;
import org.andork.func.Bimapper;
import org.andork.func.Color2HexStringBimapper;
import org.andork.func.EnumBimapper;
import org.andork.func.FileStringBimapper;
import org.andork.jogl.Projection;
import org.andork.jogl.awt.ScreenCaptureDialogModel;
import org.andork.q.QMap;
import org.andork.q.QMapBimapper;
import org.andork.q.QObject;
import org.andork.q.QObjectMapBimapper;
import org.andork.q.QSpec;

import com.andork.plot.LinearAxisConversion;

public final class ProjectModel extends QSpec<ProjectModel>
{
	public static final Attribute<CameraView>									cameraView					= newAttribute( CameraView.class , "cameraView" );
	public static final Attribute<Projection>							projCalculator				= newAttribute( Projection.class , "projCalculator" );
	public static final Attribute<float[ ]>										viewXform					= newAttribute( float[ ].class , "viewXform" );
	public static final Attribute<LinearAxisConversion>							distRange					= newAttribute( LinearAxisConversion.class , "distRange" );
	public static final Attribute<ColorParam>									colorParam					= newAttribute( ColorParam.class , "colorParam" );
	public static final Attribute<QMap<ColorParam, LinearAxisConversion, ?>>	paramRanges					= newAttribute( QMap.class , "savedParamRanges" );
	public static final Attribute<LinearAxisConversion>							highlightRange				= newAttribute( LinearAxisConversion.class , "highlightRange" );
	public static final Attribute<File>											surveyFile					= newAttribute( File.class , "surveyFile" );
	public static final Attribute<QObject<DrawerModel>>							settingsDrawer				= newAttribute( DrawerModel.instance , "settingsDrawer" );
	public static final Attribute<QObject<DrawerModel>>							surveyDrawer				= newAttribute( DrawerModel.instance , "surveyDrawer" );
	public static final Attribute<QObject<DrawerModel>>							miniSurveyDrawer			= newAttribute( DrawerModel.instance , "miniSurveyDrawer" );
	public static final Attribute<QObject<DrawerModel>>							taskListDrawer				= newAttribute( DrawerModel.instance , "taskListDrawer" );
	public static final Attribute<QObject<ScreenCaptureDialogModel>>			screenCaptureDialogModel	= newAttribute( ScreenCaptureDialogModel.instance , "screenCaptureDialogModel" );
	public static final Attribute<Color>										backgroundColor				= newAttribute( Color.class , "backgroundColor" );
	public static final Attribute<Float>										ambientLight				= newAttribute( Float.class , "ambientLight" );
	public static final Attribute<float[ ]>										depthAxis					= newAttribute( float[ ].class , "depthAxis" );
	
	public static final ProjectModel											instance					= new ProjectModel( );
	
	public static final Bimapper<QObject<ProjectModel>, Object>					defaultMapper;
	
	static
	{
		defaultMapper = new QObjectMapBimapper<ProjectModel>( instance )
				.map( projCalculator , ProjectionCalculatorBimapper.instance )
				.map( distRange , LinearAxisConversionMapBimapper.instance )
				.map( paramRanges , QMapBimapper.newInstance( EnumBimapper.newInstance( ColorParam.class ) , LinearAxisConversionMapBimapper.instance ) )
				.map( highlightRange , LinearAxisConversionMapBimapper.instance )
				.map( surveyFile , FileStringBimapper.instance )
				.map( settingsDrawer , DrawerModel.defaultMapper )
				.map( surveyDrawer , DrawerModel.defaultMapper )
				.map( miniSurveyDrawer , DrawerModel.defaultMapper )
				.map( taskListDrawer , DrawerModel.defaultMapper )
				.map( screenCaptureDialogModel , ScreenCaptureDialogModel.defaultMapper )
				.map( backgroundColor , Color2HexStringBimapper.instance );
	}
	
	private ProjectModel( )
	{
		super( );
	}
}
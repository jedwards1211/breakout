package org.andork.breakout;

import java.io.File;

import org.andork.awt.layout.DrawerModel;
import org.andork.breakout.SettingsDrawer.CameraView;
import org.andork.breakout.SettingsDrawer.FilterType;
import org.andork.breakout.model.LinearAxisConversionYamlBimapper;
import org.andork.jogl.awt.ScreenCaptureDialogModel;
import org.andork.snakeyaml.YamlObject;
import org.andork.snakeyaml.YamlSpec;

import com.andork.plot.LinearAxisConversion;

public final class ProjectModel extends YamlSpec<ProjectModel>
{
	public static final Attribute<CameraView>							cameraView					= enumAttribute( "cameraView" , CameraView.class );
	public static final Attribute<Integer>								mouseSensitivity			= integerAttribute( "mouseSensitivity" );
	public static final Attribute<LinearAxisConversion>					distRange					= Attribute.newInstance( LinearAxisConversion.class , "distRange" , new LinearAxisConversionYamlBimapper( ) );
	public static final Attribute<LinearAxisConversion>					paramRange					= Attribute.newInstance( LinearAxisConversion.class , "paramRange" , new LinearAxisConversionYamlBimapper( ) );
	public static final Attribute<LinearAxisConversion>					highlightRange				= Attribute.newInstance( LinearAxisConversion.class , "highlightRange" , new LinearAxisConversionYamlBimapper( ) );
	public static final Attribute<FilterType>							filterType					= enumAttribute( "filterType" , FilterType.class );
	public static final Attribute<File>									surveyFile					= fileAttribute( "surveyFile" );
	public static final Attribute<YamlObject<DrawerModel>>				settingsDrawer				= yamlObjectAttribute( "settingsDrawer" , DrawerModel.instance );
	public static final Attribute<YamlObject<DrawerModel>>				surveyDrawer				= yamlObjectAttribute( "surveyDrawer" , DrawerModel.instance );
	public static final Attribute<YamlObject<DrawerModel>>				miniSurveyDrawer			= yamlObjectAttribute( "miniSurveyDrawer" , DrawerModel.instance );
	public static final Attribute<YamlObject<DrawerModel>>				taskListDrawer				= yamlObjectAttribute( "taskListDrawer" , DrawerModel.instance );
	public static final Attribute<YamlObject<ScreenCaptureDialogModel>>	screenCaptureDialogModel	= yamlObjectAttribute( "screenCaptureDialogModel" , ScreenCaptureDialogModel.instance );
	
	private ProjectModel( )
	{
		super( );
	}
	
	public static final ProjectModel	instance	= new ProjectModel( );
}
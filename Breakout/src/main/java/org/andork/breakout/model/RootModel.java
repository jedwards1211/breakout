package org.andork.breakout.model;

import java.io.File;

import org.andork.func.CompoundBimapper;
import org.andork.func.FileStringBimapper;
import org.andork.func.StringObjectBimapper;
import org.andork.snakeyaml.YamlArrayList;
import org.andork.snakeyaml.YamlSpec;

public final class RootModel extends YamlSpec<RootModel>
{
	public static final Attribute<File>					currentProjectFile				= fileAttribute( "currentProjectFile" );
	public static final Attribute<YamlArrayList<File>>	recentProjectFiles				= yamlArrayListAttribute( "recentProjectFiles" , CompoundBimapper.compose( FileStringBimapper.instance , StringObjectBimapper.instance ) );
	public static final Attribute<Integer>				desiredNumSamples				= integerAttribute( "desiredNumSamples" );
	public static final Attribute<Integer>				mouseSensitivity				= integerAttribute( "mouseSensitivity" );
	public static final Attribute<Integer>				mouseWheelSensitivity			= integerAttribute( "mouseWheelSensitivity" );
	public static final Attribute<Boolean>				doNotShowNewProjectInfoDialog	= booleanAttribute( "doNotShowNewProjectInfoDialog" );
	
	private RootModel( )
	{
		super( );
	}
	
	public static final RootModel	instance	= new RootModel( );
}
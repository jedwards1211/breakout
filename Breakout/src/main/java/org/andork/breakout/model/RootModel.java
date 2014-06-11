package org.andork.breakout.model;

import java.io.File;

import org.andork.snakeyaml.YamlSpec;

public final class RootModel extends YamlSpec<RootModel>
{
	public static final Attribute<File>		currentProjectFile		= fileAttribute( "currentProjectFile" );
	public static final Attribute<Integer>	desiredNumSamples		= integerAttribute( "desiredNumSamples" );
	public static final Attribute<Integer>	mouseSensitivity		= integerAttribute( "mouseSensitivity" );
	public static final Attribute<Integer>	mouseWheelSensitivity	= integerAttribute( "mouseWheelSensitivity" );
	
	private RootModel( )
	{
		super( );
	}
	
	public static final RootModel	instance	= new RootModel( );
}
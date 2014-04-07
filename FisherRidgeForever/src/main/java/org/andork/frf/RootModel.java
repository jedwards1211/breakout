package org.andork.frf;

import java.io.File;

import org.andork.snakeyaml.YamlSpec;

public final class RootModel extends YamlSpec<RootModel>
{
	public static final Attribute<File>	currentProjectFile	= fileAttribute( "currentProjectFile" );
	
	private RootModel( )
	{
		super( );
	}
	
	public static final RootModel	instance	= new RootModel( );
}
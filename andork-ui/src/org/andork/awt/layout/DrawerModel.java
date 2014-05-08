package org.andork.awt.layout;

import org.andork.snakeyaml.YamlSpec;

public class DrawerModel extends YamlSpec<DrawerModel>
{
	public static final Attribute<Boolean>	pinned		= booleanAttribute( "pinned" );
	public static final Attribute<Boolean>	maximized	= booleanAttribute( "maximized" );
	
	private DrawerModel( )
	{
		super( );
	}
	
	public static final DrawerModel	instance	= new DrawerModel( );
}

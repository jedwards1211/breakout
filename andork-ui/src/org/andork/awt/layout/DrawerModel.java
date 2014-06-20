package org.andork.awt.layout;

import org.andork.func.Bimapper;
import org.andork.q.QObject;
import org.andork.q.QObjectMapBimapper;
import org.andork.q.QSpec;

public class DrawerModel extends QSpec<DrawerModel>
{
	public static final Attribute<Boolean>						pinned		= newAttribute( Boolean.class , "pinned" );
	public static final Attribute<Boolean>						maximized	= newAttribute( Boolean.class , "maximized" );
	
	public static final DrawerModel								instance	= new DrawerModel( );
	
	public static final Bimapper<QObject<DrawerModel>, Object>	defaultMapper;
	
	static
	{
		defaultMapper = new QObjectMapBimapper<DrawerModel>( instance );
	}
	
	private DrawerModel( )
	{
		super( );
	}
}

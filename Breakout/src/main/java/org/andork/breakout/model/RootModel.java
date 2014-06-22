package org.andork.breakout.model;

import java.io.File;

import org.andork.func.Bimapper;
import org.andork.func.FileStringBimapper;
import org.andork.q.QArrayList;
import org.andork.q.QArrayListBimapper;
import org.andork.q.QObject;
import org.andork.q.QObjectMapBimapper;
import org.andork.q.QSpec;

public final class RootModel extends QSpec<RootModel>
{
	public static final Attribute<File>							currentProjectFile					= newAttribute( File.class , "currentProjectFile" );
	public static final Attribute<QArrayList<File>>				recentProjectFiles					= newAttribute( QArrayList.class , "recentProjectFiles" );
	public static final Attribute<File>							currentProjectFileChooserDirectory	= newAttribute( File.class , "currentProjectFileChooserDirectory" );
	public static final Attribute<File>							currentArchiveFileChooserDirectory	= newAttribute( File.class , "currentArchiveFileChooserDirectory" );
	public static final Attribute<Integer>						desiredNumSamples					= newAttribute( Integer.class , "desiredNumSamples" );
	public static final Attribute<Integer>						mouseSensitivity					= newAttribute( Integer.class , "mouseSensitivity" );
	public static final Attribute<Integer>						mouseWheelSensitivity				= newAttribute( Integer.class , "mouseWheelSensitivity" );
	public static final Attribute<Boolean>						doNotShowNewProjectInfoDialog		= newAttribute( Boolean.class , "doNotShowNewProjectInfoDialog" );
	
	public static final RootModel								instance							= new RootModel( );
	
	public static final Bimapper<QObject<RootModel>, Object>	defaultMapper;
	
	static
	{
		defaultMapper = new QObjectMapBimapper<RootModel>( instance )
				.map( currentProjectFile , FileStringBimapper.instance )
				.map( recentProjectFiles , QArrayListBimapper.newInstance( FileStringBimapper.instance ) )
				.map( currentProjectFileChooserDirectory , FileStringBimapper.instance )
				.map( currentArchiveFileChooserDirectory , FileStringBimapper.instance );
	}
	
	private RootModel( )
	{
		super( );
	}
}
package org.andork.breakout.table;

import org.andork.q2.QArrayList;
import org.andork.q2.QArrayObject;
import org.andork.q2.QLinkedHashMap;
import org.andork.q2.QObject;
import org.andork.q2.QSpec;

public class ProjectModel extends QSpec
{
	public static NonNullProperty<QObject<DataDefaults>>				defaults		= new NonNullProperty<>(
																							"defaults" ,
																							QObject.class ,
																							( ) -> QArrayObject
																								.create( DataDefaults.spec ) );

	public static Property<ShotList>									shotList		= property( "shotList" ,
																							ShotList.class );

	public static Property<QLinkedHashMap<String, QArrayList<Integer>>>	shotColGroups	= property(
																							"shotColGroups" ,
																							QLinkedHashMap.class ,
																							( ) -> QLinkedHashMap
																								.newInstance( ) );
	public static Property<String>										curShotColGroup	= property(
																							"curShotColGroup" ,
																							String.class );

	public static final ProjectModel									spec			= new ProjectModel( );

	private ProjectModel( )
	{
		super( defaults ,
			shotList ,
			shotColGroups ,
			curShotColGroup );
	}
}

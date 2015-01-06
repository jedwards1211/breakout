package org.andork.breakout.model;

import java.text.DecimalFormat;

import org.andork.breakout.table.ShotTableColumnDef;
import org.andork.breakout.table.ShotText;
import org.andork.q.QLinkedHashMap;
import org.andork.q2.QArrayList;
import org.andork.q2.QHashMap;
import org.andork.q2.QSpec;
import org.andork.swing.table.QObjectList;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;

public class NewProjectModel extends QSpec
{
	public static NonNullProperty<Character>							decimalSep		= nonNullProperty(
																							"decimalSep" ,
																							Character.class ,
																							( ( DecimalFormat ) DecimalFormat
																								.getNumberInstance( ) )
																								.getDecimalFormatSymbols( )
																								.getDecimalSeparator( ) );
	public static Property<Unit<Length>>								defLenUnit		= property( "defLenUnit" ,
																							Unit.class );
	public static Property<Unit<Angle>>									defAngleUnit	= property( "defAngleUnit" ,
																							Unit.class );

	public static Property<QObjectList<org.andork.breakout.table.Shot>>	shotList		= property( "shotList" ,
																							QObjectList.class );
	public static Property<QObjectList<ShotText>>						shotTextList	= property( "shotTextList" ,
																							QObjectList.class );

	public static Property<QHashMap<Integer, ShotTableColumnDef>>		shotColDefs		= property( "shotColDefs" ,
																							QHashMap.class );
	public static Property<QLinkedHashMap<String, QArrayList<Integer>>>	shotColGroups	= property( "shotColGroups" ,
																							QLinkedHashMap.class );
	public static Property<String>										curShotColGroup	= property( "curShotColGroup" ,
																							String.class );

	public static final NewProjectModel									spec			= new NewProjectModel( );

	private NewProjectModel( )
	{
		super( decimalSep , defLenUnit , defAngleUnit , shotList , shotTextList , shotColDefs , shotColGroups ,
			curShotColGroup );
	}
}

package org.breakout.model;

import java.text.DecimalFormat;

import org.andork.q.QLinkedHashMap;
import org.andork.q2.QArrayList;
import org.andork.q2.QSpec;
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

	public static Property<QLinkedHashMap<String, QArrayList<Integer>>>	shotColGroups	= property( "shotColGroups" ,
																							QLinkedHashMap.class );
	public static Property<String>										curShotColGroup	= property( "curShotColGroup" ,
																							String.class );

	public static final NewProjectModel									spec			= new NewProjectModel( );

	private NewProjectModel( )
	{
		super( decimalSep , defLenUnit , defAngleUnit , shotColGroups , curShotColGroup );
	}
}

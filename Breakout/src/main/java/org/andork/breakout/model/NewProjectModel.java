package org.andork.breakout.model;

import java.text.DecimalFormat;

import org.andork.breakout.table.ShotText;
import org.andork.q2.QSpec;
import org.andork.swing.table.QObjectList;

public class NewProjectModel extends QSpec
{
	public static NonNullProperty<Character>							decimalSeparator	= nonNullProperty(
																								"decimalSeparator" ,
																								Character.class ,
																								( ( DecimalFormat ) DecimalFormat
																									.getNumberInstance( ) )
																									.getDecimalFormatSymbols( )
																									.getDecimalSeparator( ) );

	public static Property<QObjectList<org.andork.breakout.table.Shot>>	shotList			= property( "shotList" ,
																								QObjectList.class );
	public static Property<QObjectList<ShotText>>						shotTextList		= property( "shotTextList" ,
																								QObjectList.class );

	public static final NewProjectModel									spec				= new NewProjectModel( );

	private NewProjectModel( )
	{
		super( decimalSeparator , shotList , shotTextList );
	}
}

package org.andork.breakout.table;

import java.text.DecimalFormat;

import org.andork.q2.QSpec;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;

public class DataDefaults extends QSpec
{
	public static NonNullProperty<Character>						decimalSep			= nonNullProperty(
																							"decimalSep" ,
																							Character.class ,
																							createDefaultDecimalSeparator( ) );
	public static NonNullProperty<Integer>							doubleDecimalPlaces	= nonNullProperty(
																							"doubleDecimalPlaces" ,
																							Integer.class , 2 );
	public static NonNullProperty<Unit<Length>>						lenUnit				= nonNullProperty( "lenUnit" ,
																							Unit.class ,
																							Length.meters );
	public static NonNullProperty<Integer>							lenDecimalPlaces	= nonNullProperty(
																							"lenDecimalPlaces" ,
																							Integer.class , 2 );
	public static NonNullProperty<Unit<Angle>>						angleUnit			= nonNullProperty( "angleUnit" ,
																							Unit.class ,
																							Angle.degrees );
	public static NonNullProperty<Integer>							angleDecimalPlaces	= nonNullProperty(
																							"angleDecimalPlaces" ,
																							Integer.class , 1 );
	public static NonNullProperty<Class<? extends ShotVector>>		shotVector			= nonNullProperty(
																							"shotVector" ,
																							Class.class ,
																							ShotVector.Dai.c.class );
	public static NonNullProperty<Class<? extends ShotVector.Dai>>	daiShotVector		= nonNullProperty(
																							"daiShotVector" ,
																							Class.class ,
																							ShotVector.Dai.c.class );
	public static NonNullProperty<Class<? extends ShotVector.Nev>>	nevShotVector		= nonNullProperty(
																							"nevShotVector" ,
																							Class.class ,
																							ShotVector.Nev.d.class );
	public static NonNullProperty<Class<? extends XSect>>			xSect				= nonNullProperty( "xSect" ,
																							Class.class ,
																							BisectorLrudXSect.class );
	public static NonNullProperty<Class<? extends UdXSect>>			udXSect				= nonNullProperty( "udXSect" ,
																							Class.class ,
																							BisectorLrudXSect.class );
	public static NonNullProperty<Class<? extends LrudXSect>>		lrudXSect			= nonNullProperty( "lrudXSect" ,
																							Class.class ,
																							BisectorLrudXSect.class );

	public static final DataDefaults								spec				= new DataDefaults( );

	private static char createDefaultDecimalSeparator( )
	{
		return ( ( DecimalFormat ) DecimalFormat
			.getNumberInstance( ) )
			.getDecimalFormatSymbols( )
			.getDecimalSeparator( );
	}

	private DataDefaults( )
	{
		super( decimalSep ,
			doubleDecimalPlaces ,
			lenUnit ,
			lenDecimalPlaces ,
			angleUnit ,
			angleDecimalPlaces ,
			shotVector ,
			daiShotVector ,
			nevShotVector ,
			xSect ,
			udXSect ,
			lrudXSect );
	}
}

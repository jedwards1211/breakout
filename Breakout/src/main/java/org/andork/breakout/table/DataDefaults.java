package org.andork.breakout.table;

import java.text.DecimalFormat;

import org.andork.q2.QSpec;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;

public class DataDefaults extends QSpec
{
	public static NonNullProperty<Character>		decimalSeparator		= nonNullProperty(
																				"decimalSeparator" ,
																				Character.class ,
																				createDefaultDecimalSeparator( ) );
	public static NonNullProperty<Integer>			doubleDecimalPlaces		= nonNullProperty(
																				"doubleDecimalPlaces" ,
																				Integer.class , 2 );
	public static NonNullProperty<Unit<Length>>		lengthUnit				= nonNullProperty(
																				"lengthUnit" ,
																				Unit.class ,
																				Length.meters );
	public static NonNullProperty<Integer>			lengthDecimalPlaces		= nonNullProperty(
																				"lengthDecimalPlaces" ,
																				Integer.class , 2 );
	public static NonNullProperty<Unit<Angle>>		angleUnit				= nonNullProperty( "angleUnit" ,
																				Unit.class ,
																				Angle.degrees );
	public static NonNullProperty<Integer>			angleDecimalPlaces		= nonNullProperty(
																				"angleDecimalPlaces" ,
																				Integer.class , 1 );
	public static NonNullProperty<ShotVectorType>	shotVectorType			= nonNullProperty(
																				"shotVectorType" ,
																				ShotVectorType.class ,
																				ShotVectorType.DAIc );
	public static NonNullProperty<Boolean>			backsightsAreCorrected	= nonNullProperty(
																				"backsightsAreCorrected" ,
																				Boolean.class , false );
	public static NonNullProperty<Boolean>			downwardIsPositive		= nonNullProperty(
																				"downwardIsPositive" ,
																				Boolean.class , false );

	public static final DataDefaults				spec					= new DataDefaults( );

	private static char createDefaultDecimalSeparator( )
	{
		return ( ( DecimalFormat ) DecimalFormat
			.getNumberInstance( ) )
			.getDecimalFormatSymbols( )
			.getDecimalSeparator( );
	}

	private DataDefaults( )
	{
		super( decimalSeparator ,
			doubleDecimalPlaces ,
			lengthUnit ,
			lengthDecimalPlaces ,
			angleUnit ,
			angleDecimalPlaces ,
			shotVectorType ,
			backsightsAreCorrected ,
			downwardIsPositive );
	}
}

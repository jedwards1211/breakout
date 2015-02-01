package org.breakout.parse;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.andork.unit.Angle;
import org.andork.unit.EnglishUnitNames;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;

public class UnitizedLengthParser extends UnitizedDoubleParser<Length>
{
	public static void main( String[ ] args )
	{
		NumberFormat format = DecimalFormat.getInstance( );

		UnitizedLengthParser parser = new UnitizedLengthParser( );

		parser.defaultUnit( Length.meters )
			.units( Length.type.units( ) )
			.unitNames( EnglishUnitNames.inst )
			.numberFormat( ( s , p ) ->
			{
				Number n = format.parse( s , p );
				return n == null ? null : n.doubleValue( );
			} )
			.allowWhitespace( true );

		LineTokenizer lineTokenizer = new LineTokenizer( "30 kilometers40m 20y -50 ' 1inches 4 in 22mi" , 0 );
		while( !lineTokenizer.isAtEnd( ) )
		{
			lineTokenizer.pull( Character::isWhitespace );
			Object Length = parser.pullLength( lineTokenizer );
			if( Length == null )
			{
				break;
			}
			System.out.println( Length );
		}
	}

	public ValueToken<UnitizedDouble<Length>> pullLength( LineTokenizer lineTokenizer )
	{
		ValueToken<Double> value = lineTokenizer.pull( numberFormat );
		if( value == null )
		{
			return null;
		}
		int pos = lineTokenizer.position( );
		pullWhitespaceIfAllowed( lineTokenizer );

		ValueToken<Unit<Length>> unit = lineTokenizer.pull( unitMap , unitsMaxLength );
		if( unit == null )
		{
			lineTokenizer.position( pos );
		}
		else if( unit.value == Length.feet )
		{
			pos = lineTokenizer.position( );
			pullWhitespaceIfAllowed( lineTokenizer );

			ValueToken<Double> inches = lineTokenizer.pull( numberFormat );
			if( inches != null )
			{
				if( inches.value < 0.0 || inches.value >= 12.0 )
				{
					throw new SurveyParseException( "inchesOutOfRange" );
				}

				pullWhitespaceIfAllowed( lineTokenizer );

				ValueToken<Unit<Length>> inchesUnit = lineTokenizer.pull( unitMap , unitsMaxLength );
				if( inchesUnit != null && inchesUnit.value == Length.inches )
				{
					return new ValueToken<>( new UnitizedDouble<>(
						value.value + inches.value * ( value.value < 0 ? -1 : 1 ) / 12.0 , Length.feet ) , value ,
						inchesUnit );
				}
			}
			lineTokenizer.position( pos );
		}

		return new ValueToken<>( new UnitizedDouble<>(
			value.value , unit != null ? unit.value : defaultUnit ) , value , unit );

	}
}

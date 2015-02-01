package org.breakout.parse;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.andork.unit.Angle;
import org.andork.unit.EnglishUnitNames;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;

public class UnitizedAngleParser extends UnitizedDoubleParser<Angle>
{
	public static void main( String[ ] args )
	{
		NumberFormat format = DecimalFormat.getInstance( );

		UnitizedAngleParser parser = new UnitizedAngleParser( );

		parser.defaultUnit( Angle.degrees )
			.units( Angle.type.units( ) )
			.unitNames( EnglishUnitNames.inst )
			.numberFormat( ( s , p ) ->
			{
				Number n = format.parse( s , p );
				return n == null ? null : n.doubleValue( );
			} );

		LineTokenizer lineTokenizer = new LineTokenizer( "30d 40g:30 -50r30:20-30" , 0 );
		while( !lineTokenizer.isAtEnd( ) )
		{
			lineTokenizer.pull( Character::isWhitespace );
			Object angle = parser.pullAngle( lineTokenizer );
			if( angle == null )
			{
				break;
			}
			System.out.println( angle );
		}
	}

	public ValueToken<UnitizedDouble<Angle>> pullAngle( LineTokenizer lineTokenizer )
	{
		ValueToken<Double> degrees , minutes = null , seconds = null;
		Token firstColon , secondColon = null;
		ValueToken<Unit<Angle>> unit = null;

		degrees = lineTokenizer.pull( numberFormat );

		int colonCount = 0;
		firstColon = lineTokenizer.pull( ':' );
		if( firstColon != null )
		{
			defaultUnit = Angle.degrees;

			colonCount++;
			minutes = lineTokenizer.pull( numberFormat );

			secondColon = lineTokenizer.pull( ':' );
			if( secondColon != null )
			{
				colonCount++;
				seconds = lineTokenizer.pull( numberFormat );
			}
		}
		else
		{
			if( degrees == null )
			{
				return null;
			}
			int pos = lineTokenizer.position( );
			pullWhitespaceIfAllowed( lineTokenizer );

			unit = lineTokenizer.pull( unitMap , unitsMaxLength );
			if( unit == null )
			{
				lineTokenizer.position( pos );
			}
		}

		if( degrees == null && minutes == null && seconds == null )
		{
			throw new SurveyParseException( "colonsWithoutDMS" );
		}

		if( minutes != null && ( minutes.value < 0.0 || minutes.value >= 60.0 ) )
		{
			throw new SurveyParseException( "minutesOutOfRange" );
		}

		if( seconds != null && ( seconds.value < 0.0 || seconds.value >= 60.0 ) )
		{
			throw new SurveyParseException( "secondsOutOfRange" );
		}

		if( colonCount == 2 && seconds == null )
		{
			throw new SurveyParseException( "missingSeconds" );
		}
		if( colonCount > 0 && degrees != null && minutes == null )
		{
			throw new SurveyParseException( "missingMinutes" );
		}

		return new ValueToken<>(
			new UnitizedDouble<>( zeroIfNull( degrees ) + ( zeroIfNull( minutes ) + zeroIfNull( seconds ) / 60.0 )
				/ 60.0 , unit != null ? unit.value : defaultUnit ) ,
			degrees , firstColon , minutes , secondColon , seconds , unit );
	}

	private static double zeroIfNull( ValueToken<Double> token )
	{
		return token == null ? 0.0 : token.value;
	}

}

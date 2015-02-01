package org.breakout.wallsimport;

import static org.breakout.wallsimport.CardinalDirection.EAST;
import static org.breakout.wallsimport.CardinalDirection.NORTH;
import static org.breakout.wallsimport.CardinalDirection.SOUTH;
import static org.breakout.wallsimport.CardinalDirection.WEST;

import java.text.ParseException;
import java.util.regex.Pattern;

import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;
import org.breakout.parse.LineTokenizer;
import org.breakout.parse.Token;
import org.breakout.parse.ValueToken;
import org.breakout.wallsimport.WallsImportMessage.Severity;

public class WallsParser
{
	private final Localizer			localizer;

	public static final Pattern	UNSIGNED_FLOATING_POINT			= Pattern.compile( "^(\\d+(\\.\\d*)?|\\.\\d+)" );

	public static final Pattern	SIGNED_FLOATING_POINT			= Pattern.compile( "^[-+]?(\\d+(\\.\\d*)?|\\.\\d+)" );

	public static final Pattern	UNSIGNED_INTEGER				= Pattern.compile( "^\\d+" );

	public static final Pattern	SIGNED_INTEGER					= Pattern.compile( "^[-+]?\\d+" );

	public static final Pattern	SIGN							= Pattern.compile( "^[-+]" );

	public static final Pattern	UNSIGNED_FLOATING_POINT_OR_OMIT	= Pattern.compile( "^(\\d+(\\.\\d*)?|\\.\\d+|-+)" );

	public static final Pattern	SIGNED_FLOATING_POINT_OR_OMIT	= Pattern
	.compile( "^([-+]?(\\d+(\\.\\d*)?|\\.\\d+))|-+" );

	public static final Pattern	OMIT							= Pattern.compile( "^-+" );

	private static final Pattern	LENGTH_UNIT_SUFFIX		= Pattern.compile( "^[mMfFiI]" );
	private static final Pattern	AZIMUTH_UNIT_SUFFIX		= Pattern.compile( "^[dDmMgG]" );
	private static final Pattern	INCLINATION_UNIT_SUFFIX	= Pattern.compile( "^[dDmMgGpP]" );
	private static final Pattern	CARDINAL_DIRECTION		= Pattern.compile( "^[nNsSeEwW]" );

	public WallsParser( I18n i18n )
	{
		localizer = i18n.forClass( WallsImporter.class );
	}

	public static Unit<Length> parseDistanceUnitSuffix( String s )
	{
		if( s.isEmpty( ) )
		{
			return null;
		}
		switch( s.charAt( 0 ) )
		{
		case 'm':
		case 'M':
			return Length.meters;
		case 'f':
		case 'F':
			return Length.feet;
		case 'i':
		case 'I':
			return Length.inches;
		default:
			return null;
		}
	}

	public static Unit<Angle> parseAzimuthUnitSuffix( String s )
	{
		if( s.isEmpty( ) )
		{
			return null;
		}
		switch( s.charAt( 0 ) )
		{
		case 'd':
		case 'D':
			return Angle.degrees;
		case 'm':
		case 'M':
			return Angle.milsNATO;
		case 'g':
		case 'G':
			return Angle.gradians;
		default:
			return null;
		}
	}

	public static Unit<Angle> parseInclinationUnitSuffix( String s )
	{
		if( s.isEmpty( ) )
		{
			return null;
		}
		switch( s.charAt( 0 ) )
		{
		case 'd':
		case 'D':
			return Angle.degrees;
		case 'm':
		case 'M':
			return Angle.milsNATO;
		case 'g':
		case 'G':
			return Angle.gradians;
		case 'p':
		case 'P':
			return Angle.percentGrade;
		default:
			return null;
		}
	}

	private static double zeroIfNull( ValueToken<Double> token )
	{
		return token == null ? 0.0 : token.value;
	}

	public ValueToken<UnitizedDouble<Length>> pullUnsignedDistance( LineTokenizer lineTokenizer ,
		Unit<Length> defaultUnit ,
		ParseLogger logger )
	{
		ValueToken<UnitizedDouble<Length>> omitToken = lineTokenizer.pull( OMIT , s -> null );
		if( omitToken != null )
		{
			return omitToken;
		}

		ValueToken<Double> number = lineTokenizer.pull( UNSIGNED_FLOATING_POINT , Double::parseDouble );
		if( number == null )
		{
			ValueToken<Unit<Length>> unit = lineTokenizer.pull( LENGTH_UNIT_SUFFIX ,
				WallsParser::parseDistanceUnitSuffix );
			if( unit != null && unit.value == Length.inches )
			{
				number = lineTokenizer.pull( UNSIGNED_FLOATING_POINT , Double::parseDouble );
				if( number == null )
				{
					logger.log( Severity.ERROR , localizer.getString( "expectedNumberAfterInches" ) ,
						unit.endLine , unit.endColumn + 1 );
					throw new RuntimeException( localizer.getString( "expectedNumberAfterInches" ) );
				}

				return new ValueToken<>( new UnitizedDouble<>( number.value , Length.inches ) , unit , number );
			}

			if( unit != null )
			{
				logger.log( Severity.ERROR , localizer.getString( "expectedNumberOrInches" ) , unit.endLine ,
					unit.endColumn + 1 );
				throw new RuntimeException( localizer.getString( "expectedNumberOrInches" ) );
			}
			return null;
		}

		ValueToken<Unit<Length>> unit = lineTokenizer.pull( LENGTH_UNIT_SUFFIX ,
			WallsParser::parseDistanceUnitSuffix );
		if( unit != null && unit.value == Length.inches )
		{
			ValueToken<Double> inches = lineTokenizer.pull( UNSIGNED_FLOATING_POINT , Double::parseDouble );
			if( inches == null )
			{
				logger.log( Severity.ERROR , localizer.getString( "expectedNumberAfterInches" ) , unit.endLine ,
					unit.endColumn + 1 );
				throw new RuntimeException( localizer.getString( "expectedNumberAfterInches" ) );
			}
			return new ValueToken<>( new UnitizedDouble<>( number.value + inches.value / 12.0 , Length.feet ) ,
				number , unit , inches );
		}

		return new ValueToken<>( new UnitizedDouble<>( number.value , unit != null ? unit.value : defaultUnit ) ,
			number , unit );
	}

	public ValueToken<UnitizedDouble<Length>> pullSignedDistance( LineTokenizer lineTokenizer ,
		Unit<Length> defaultUnit ,
		ParseLogger logger )
	{
		Token sign = lineTokenizer.pull( SIGN );
		boolean negate = sign != null && sign.image.equals( "-" );

		ValueToken<UnitizedDouble<Length>> abs = pullUnsignedDistance( lineTokenizer , defaultUnit , logger );
		if( abs == null )
		{
			if( negate )
			{
				// - without a number following it means omit; pull any remaining dashes
				Token dashes = lineTokenizer.pull( OMIT );
				return new ValueToken<>( null , sign , dashes );
			}
			if( sign == null || sign.image.equals( "-" ) )
			{
				return null;
			}
			logger.log( Severity.ERROR , localizer.getString( "expectedNumberOrInches" ) ,
				lineTokenizer.lineNumber( ) , lineTokenizer.columnNumber( ) );
		}
		return new ValueToken<>( negate ? abs.value.negate( ) : abs.value , sign , abs );
	}

	public ValueToken<UnitizedDouble<Angle>>
		pullInclination( LineTokenizer lineTokenizer , Unit<Angle> defaultUnit , ParseLogger logger )
	{
		Token sign = lineTokenizer.pull( SIGN );
		boolean negative = sign != null && sign.image.equals( "-" );

		ValueToken<Double> degrees , minutes = null , seconds = null;
		ValueToken<Unit<Angle>> unit = null;
		degrees = lineTokenizer.pull( UNSIGNED_FLOATING_POINT , Double::parseDouble );

		int colonCount = 0;
		Token firstColon = lineTokenizer.pull( ':' );
		Token secondColon = null;
		if( firstColon != null )
		{
			defaultUnit = Angle.degrees;

			colonCount++;
			minutes = lineTokenizer.pull( UNSIGNED_FLOATING_POINT , Double::parseDouble );

			secondColon = lineTokenizer.pull( ':' );
			if( secondColon != null )
			{
				colonCount++;
				seconds = lineTokenizer.pull( UNSIGNED_FLOATING_POINT , Double::parseDouble );
			}
		}
		else
		{
			if( degrees == null )
			{
				if( negative )
				{
					// - without a number following it means omit; pull any remaining dashes
					Token dashes = lineTokenizer.pull( OMIT );
					return new ValueToken<>( null , sign , dashes );
				}
				if( sign == null || sign.image.equals( "-" ) )
				{
					return null;
				}
				logger.log( Severity.ERROR , localizer.getString( "expectedInclinationAngle" ) ,
					lineTokenizer.lineNumber( ) , lineTokenizer.columnNumber( ) );
				throw new RuntimeException( localizer.getString( "expectedInclinationAngle" ) );
			}
			unit = lineTokenizer.pull( INCLINATION_UNIT_SUFFIX ,
				WallsParser::parseInclinationUnitSuffix );
		}

		if( degrees == null && minutes == null && seconds == null )
		{
			logger.log( Severity.ERROR , localizer.getString( "colonsWithoutDMS" ) ,
				secondColon.endLine , secondColon.endColumn + 1 );
			throw new RuntimeException( localizer.getString( "colonsWithoutDMS" ) );
		}

		if( minutes != null && ( minutes.value < 0.0 || minutes.value >= 60.0 ) )
		{
			logger.log( Severity.WARNING , localizer.getString( "minutesOutOfRange" ) , minutes.beginLine ,
				minutes.beginColumn );
		}

		if( seconds != null && ( seconds.value < 0.0 || seconds.value >= 60.0 ) )
		{
			logger.log( Severity.WARNING , localizer.getString( "secondsOutOfRange" ) , seconds.beginLine ,
				seconds.beginColumn );
		}

		if( colonCount == 2 && seconds == null )
		{
			logger.log( Severity.WARNING , localizer.getString( "missingSeconds" ) , secondColon.endLine ,
				secondColon.endColumn + 1 );
		}
		if( colonCount > 0 && degrees != null && minutes == null )
		{
			logger.log( Severity.WARNING , localizer.getString( "missingMinutes" ) , firstColon.endLine ,
				firstColon.endColumn + 1 );
		}

		double value = zeroIfNull( degrees ) + ( zeroIfNull( minutes ) + zeroIfNull( seconds ) / 60.0 )
			/ 60.0;

		return new ValueToken<>( new UnitizedDouble<>( negative ? -value : value , unit != null ? unit.value
			: defaultUnit ) ,
			sign , degrees , firstColon , minutes , secondColon , seconds , unit );
	}

	public ValueToken<UnitizedDouble<Angle>> pullAzimuth( LineTokenizer lineTokenizer , Unit<Angle> defaultUnit ,
		ParseLogger logger )
	{
		ValueToken<UnitizedDouble<Angle>> omitToken = lineTokenizer.pull( OMIT , s -> null );
		if( omitToken != null )
		{
			return omitToken;
		}
		ValueToken<UnitizedDouble<Angle>> azimuth = pullQuadrantAzimuth( lineTokenizer , defaultUnit , logger );
		if( azimuth != null )
		{
			return azimuth;
		}
		return pullNonQuadrantAzimuth( lineTokenizer , defaultUnit , logger );
	}

	private ValueToken<UnitizedDouble<Angle>> pullQuadrantAzimuth( LineTokenizer lineTokenizer ,
		Unit<Angle> defaultUnit ,
		ParseLogger logger )
	{
		ValueToken<CardinalDirection> fromDirection =
			lineTokenizer.pull( CARDINAL_DIRECTION , CardinalDirection::fromCharacter );
		if( fromDirection != null )
		{
			ValueToken<UnitizedDouble<Angle>> angle = pullNonQuadrantAzimuth( lineTokenizer , defaultUnit , logger );
			if( angle == null )
			{
				return new ValueToken<>( fromDirection.value.angle , fromDirection );
			}

			if( angle.value.doubleValue( Angle.degrees ) > 90.0 )
			{
				logger.log( Severity.ERROR , localizer.getFormattedString( "rotationOutOfRange" ) ,
					angle.beginLine , angle.beginColumn );
				throw new RuntimeException( localizer.getFormattedString( "rotationOutOfRange" ) );
			}

			ValueToken<CardinalDirection> toDirection =
				lineTokenizer.pull( CARDINAL_DIRECTION , CardinalDirection::fromCharacter );
			if( toDirection == null )
			{
				logger.log( Severity.ERROR , localizer.getFormattedString( "expectedAzimuthToDirection" ) ,
					angle.endLine , angle.endColumn + 1 );
				throw new RuntimeException( localizer.getFormattedString( "expectedAzimuthToDirection" ) );
			}

			if( toDirection.value == fromDirection.value || toDirection.value == fromDirection.value.opposite( ) )
			{
				logger.log( Severity.ERROR , localizer.getFormattedString( "invalidDirectionCombination" ) ,
					toDirection.beginLine , toDirection.beginColumn );
				throw new RuntimeException( localizer.getFormattedString( "invalidDirectionCombination" ) );
			}

			return new ValueToken<>(
				quadrantAngle( fromDirection.value , angle.value , toDirection.value ) ,
				fromDirection , angle , toDirection );
		}
		return null;
	}

	private static UnitizedDouble<Angle> quadrantAngle( CardinalDirection from , UnitizedDouble<Angle> angle ,
		CardinalDirection to )
	{
		switch( from )
		{
		case NORTH:
			return to == EAST ? NORTH.angle.add( angle ) : new UnitizedDouble<>( 360.0 , Angle.degrees )
				.subtract( angle );
		case EAST:
			return to == SOUTH ? EAST.angle.add( angle ) : EAST.angle.subtract( angle );
		case SOUTH:
			return to == WEST ? SOUTH.angle.add( angle ) : SOUTH.angle.subtract( angle );
		case WEST:
			return to == NORTH ? WEST.angle.add( angle ) : WEST.angle.subtract( angle );
		default:
			return null;
		}
	}

	private ValueToken<UnitizedDouble<Angle>>
		pullNonQuadrantAzimuth( LineTokenizer lineTokenizer , Unit<Angle> defaultUnit , ParseLogger logger )
	{
		ValueToken<Double> degrees , minutes = null , seconds = null;
		Token firstColon , secondColon = null;
		ValueToken<Unit<Angle>> unit = null;

		degrees = lineTokenizer.pull( UNSIGNED_FLOATING_POINT , Double::parseDouble );

		int colonCount = 0;
		firstColon = lineTokenizer.pull( ':' );
		if( firstColon != null )
		{
			defaultUnit = Angle.degrees;

			colonCount++;
			minutes = lineTokenizer.pull( UNSIGNED_FLOATING_POINT , Double::parseDouble );

			secondColon = lineTokenizer.pull( ':' );
			if( secondColon != null )
			{
				colonCount++;
				seconds = lineTokenizer.pull( UNSIGNED_FLOATING_POINT , Double::parseDouble );
			}
		}
		else
		{
			if( degrees == null )
			{
				return null;
			}
			unit = lineTokenizer.pull( AZIMUTH_UNIT_SUFFIX , WallsParser::parseAzimuthUnitSuffix );
		}

		if( degrees == null && minutes == null && seconds == null )
		{
			logger.log( Severity.ERROR , localizer.getString( "colonsWithoutDMS" ) , secondColon.endLine ,
				secondColon.endColumn + 1 );
			throw new RuntimeException( localizer.getString( "colonsWithoutDMS" ) );
		}

		if( minutes != null && ( minutes.value < 0.0 || minutes.value >= 60.0 ) )
		{
			logger.log( Severity.WARNING , localizer.getString( "minutesOutOfRange" ) , minutes.beginLine ,
				minutes.beginColumn );
		}

		if( seconds != null && ( seconds.value < 0.0 || seconds.value >= 60.0 ) )
		{
			logger.log( Severity.WARNING , localizer.getString( "secondsOutOfRange" ) , seconds.beginLine ,
				seconds.beginColumn );
		}

		if( colonCount == 2 && seconds == null )
		{
			logger.log( Severity.WARNING , localizer.getString( "missingSeconds" ) , secondColon.endLine ,
				secondColon.endColumn + 1 );
		}
		if( colonCount > 0 && degrees != null && minutes == null )
		{
			logger.log( Severity.WARNING , localizer.getString( "missingMinutes" ) , firstColon.endLine ,
				firstColon.endColumn + 1 );
		}

		return new ValueToken<>(
			new UnitizedDouble<>( zeroIfNull( degrees ) + ( zeroIfNull( minutes ) + zeroIfNull( seconds ) / 60.0 )
				/ 60.0 , unit != null ? unit.value : defaultUnit ) ,
			degrees , firstColon , minutes , secondColon , seconds , unit );
	}
}

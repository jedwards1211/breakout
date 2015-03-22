package org.breakout.wallsimport;

import static org.breakout.wallsimport.CardinalDirection.EAST;
import static org.breakout.wallsimport.CardinalDirection.NORTH;
import static org.breakout.wallsimport.CardinalDirection.SOUTH;
import static org.breakout.wallsimport.CardinalDirection.WEST;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.andork.collect.MapLiteral;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;
import org.andork.util.Pair;
import org.breakout.parse.ExpectedTypes;
import org.breakout.parse.Segment;
import org.breakout.parse.SegmentMatcher;
import org.breakout.parse.SegmentParseException;
import org.breakout.parse.SegmentParseExpectedException;

public class NewWallsParser
{
	public static Map<String, Unit<Length>>			lengthUnitSuffixes		= new MapLiteral<String, Unit<Length>>( )
																				.map( "m" , Length.meters )
																				.map( "M" , Length.meters )
																				.map( "f" , Length.feet )
																				.map( "F" , Length.feet );

	public static Map<String, Unit<Angle>>			azmUnitSuffixes			= new MapLiteral<String, Unit<Angle>>( )
																				.map( "d" , Angle.degrees )
																				.map( "D" , Angle.degrees )
																				.map( "g" , Angle.gradians )
																				.map( "G" , Angle.gradians )
																				.map( "m" , Angle.milsNATO )
																				.map( "M" , Angle.milsNATO );

	public static Map<Unit<Angle>, Double>			azmMaxes				= new MapLiteral<Unit<Angle>, Double>( )
																				.map( Angle.degrees , 360.0 )
																				.map( Angle.gradians , 400.0 )
																				.map( Angle.milsNATO , 6400.0 );

	public static Map<Unit<Angle>, Double>			incMaxes				= new MapLiteral<Unit<Angle>, Double>( )
																				.map( Angle.degrees , 90.0 )
																				.map( Angle.gradians , 100.0 )
																				.map( Angle.milsNATO , 1600.0 )
																				.map( Angle.percentGrade ,
																					Double.POSITIVE_INFINITY );

	public static Map<String, Unit<Angle>>			incUnitSuffixes			= new MapLiteral<String, Unit<Angle>>( )
																				.map( "d" , Angle.degrees )
																				.map( "D" , Angle.degrees )
																				.map( "g" , Angle.gradians )
																				.map( "G" , Angle.gradians )
																				.map( "m" , Angle.milsNATO )
																				.map( "M" , Angle.milsNATO )
																				.map( "p" , Angle.percentGrade )
																				.map( "P" , Angle.percentGrade );

	public static Map<String, CardinalDirection>	cardinalDirections		= new MapLiteral<String, CardinalDirection>( )
																				.map( "n" , NORTH )
																				.map( "N" , NORTH )
																				.map( "s" , SOUTH )
																				.map( "S" , SOUTH )
																				.map( "e" , EAST )
																				.map( "E" , EAST )
																				.map( "w" , WEST )
																				.map( "W" , WEST );

	/**
	 * The quoted value, unclosed quote, and semicolon handling regex from hell!
	 */
	public static Pattern							UNITS_OPTION_PATTERN	= Pattern
																				.compile( "([a-zA-Z0-9]+)(=([^ \t\n\r\f\";]+|(\"(([^\\\\\"]|\\\\.)*)(\")?)|)?)?|;" );

	public static UnitizedDouble<Length> parseSignedDistance( Segment segment , Unit<Length> defaultUnit )
	{
		int lastIndex = segment.length( ) - 1;

		if( Character.isLetter( segment.charAt( lastIndex ) ) )
		{
			return new UnitizedDouble<Length>(
				segment.substring( 0 , lastIndex ).parseAsDouble( ) ,
				segment.substring( lastIndex ).parseAsAnyOf( lengthUnitSuffixes ) );
		}

		return new UnitizedDouble<Length>( segment.parseAsDouble( ) , defaultUnit );
	}

	public static UnitizedDouble<Length> parseUnsignedDistance( Segment segment , Unit<Length> defaultUnit )
	{
		int lastIndex = segment.length( ) - 1;

		if( Character.isLetter( segment.charAt( lastIndex ) ) )
		{
			return new UnitizedDouble<Length>(
				segment.substring( 0 , lastIndex ).parseAsUnsignedDouble( ) ,
				segment.substring( lastIndex ).parseAsAnyOf( lengthUnitSuffixes ) );
		}

		return new UnitizedDouble<Length>( segment.parseAsUnsignedDouble( ) , defaultUnit );
	}

	private static double parseAsDouble( Segment segment , double defaultValue )
	{
		if( segment.isEmpty( ) )
		{
			return defaultValue;
		}
		return segment.parseAsDouble( );
	}

	private static double parseAsUnsignedDouble( Segment segment , double defaultValue )
	{
		if( segment.isEmpty( ) )
		{
			return defaultValue;
		}
		return segment.parseAsUnsignedDouble( );
	}

	private static UnitizedDouble<Angle> parseNonQuadrantAzimuth( Segment segment , Unit<Angle> defaultUnit )
	{
		int lastIndex = segment.length( ) - 1;

		double angle;
		Unit<Angle> unit;

		if( Character.isLetter( segment.charAt( lastIndex ) ) )
		{
			angle = segment.substring( 0 , lastIndex ).parseAsUnsignedDouble( );
			unit = segment.substring( lastIndex ).parseAsAnyOf( azmUnitSuffixes );
		}
		else
		{
			if( segment.indexOf( ':' ) >= 0 )
			{
				Segment[ ] parts = segment.split( ":" );
				if( parts.length > 2 && parts[ parts.length - 1 ].endCol != segment.endCol )
				{
					throw new SegmentParseException( parts[ 2 ].substring( parts[ 2 ].length( ) ) , "too many colons" );
				}

				if( parts.length == 3 )
				{
					angle = parseAsUnsignedDouble( parts[ 0 ] , 0 ) +
						parseAsUnsignedDouble( parts[ 1 ] , 0 ) / 60.0 +
						parts[ 2 ].parseAsUnsignedDouble( ) / 3600.0;
					unit = Angle.degrees;
				}
				else
				{
					angle = parseAsUnsignedDouble( parts[ 0 ] , 0 ) +
						parts[ 1 ].parseAsUnsignedDouble( ) / 60.0;
					unit = Angle.degrees;
				}
			}
			else
			{
				angle = segment.parseAsUnsignedDouble( );
				unit = defaultUnit;
			}
		}

		double azmMax = azmMaxes.get( unit );

		if( angle < 0 || angle > azmMax )
		{
			throw new SegmentParseException( segment , "azimuth must be between 0 and " + azmMax );
		}

		return new UnitizedDouble<Angle>( angle , unit );
	}

	public static UnitizedDouble<Angle> parseAzimuthOffset( Segment segment , Unit<Angle> defaultUnit )
	{
		if( segment.startsWith( "-" ) )
		{
			return parseNonQuadrantAzimuth( segment.substring( 1 ) , defaultUnit ).negate( );
		}
		else if( segment.startsWith( "+" ) )
		{
			segment = segment.substring( 1 );
		}
		return parseNonQuadrantAzimuth( segment , defaultUnit );
	}

	public static UnitizedDouble<Angle> parseAzimuth( Segment segment , Unit<Angle> defaultUnit )
	{
		if( Character.isLetter( segment.charAt( 0 ) ) )
		{
			CardinalDirection startDirection = segment.substring( 0 , 1 ).parseAsAnyOf( cardinalDirections );

			if( segment.length( ) == 1 )
			{
				return startDirection.angle;
			}

			UnitizedDouble<Angle> angle = parseNonQuadrantAzimuth( segment.substring( 1 , segment.length( ) - 1 ) ,
				Angle.degrees );

			// inc maxes works for this, angle must be between 0 and 90 degrees
			double angleMax = incMaxes.get( angle.unit );

			if( angle.doubleValue( angle.unit ) < 0 || angle.doubleValue( angle.unit ) > angleMax )
			{
				throw new SegmentParseException( segment , "azimuth must be between 0 and " + angleMax );
			}

			CardinalDirection endDirection = segment.substring( segment.length( ) - 1 ).parseAsAnyOf(
				cardinalDirections );

			return startDirection.toward( endDirection , angle );
		}

		return parseNonQuadrantAzimuth( segment , defaultUnit );
	}

	private static UnitizedDouble<Angle> parseUnsignedInclination( Segment segment , Unit<Angle> defaultUnit )
	{
		int lastIndex = segment.length( ) - 1;

		double angle;
		Unit<Angle> unit;

		if( Character.isLetter( segment.charAt( lastIndex ) ) )
		{
			angle = segment.substring( 0 , lastIndex ).parseAsUnsignedDouble( );
			unit = segment.substring( lastIndex ).parseAsAnyOf( incUnitSuffixes );
		}
		else
		{
			if( segment.indexOf( ':' ) >= 0 )
			{
				Segment[ ] parts = segment.split( ":" );
				if( parts.length > 2 && parts[ parts.length - 1 ].endCol != segment.endCol )
				{
					throw new SegmentParseException( parts[ 2 ].substring( parts[ 2 ].length( ) ) , "too many colons" );
				}

				if( parts.length == 3 )
				{
					angle = parseAsUnsignedDouble( parts[ 0 ] , 0 ) +
						parseAsUnsignedDouble( parts[ 1 ] , 0 ) / 60.0 +
						parts[ 2 ].parseAsUnsignedDouble( ) / 3600.0;
					unit = Angle.degrees;
				}
				else
				{
					angle = parseAsUnsignedDouble( parts[ 0 ] , 0 ) +
						parts[ 1 ].parseAsUnsignedDouble( ) / 60.0;
					unit = Angle.degrees;
				}
			}
			else
			{
				angle = segment.parseAsUnsignedDouble( );
				unit = defaultUnit;
			}
		}

		return new UnitizedDouble<Angle>( angle , unit );
	}

	public static UnitizedDouble<Angle> parseInclination( Segment segment , Unit<Angle> defaultUnit )
	{
		UnitizedDouble<Angle> result;

		char first = segment.charAt( 0 );
		if( first == '+' || first == '-' )
		{
			result = parseUnsignedInclination( segment.substring( 1 ) , defaultUnit );
			if( result.doubleValue( result.unit ) == 0 )
			{
				throw new SegmentParseException( segment , "+ or - is only allowed for nonzero inclinations" );
			}
			if( first == '-' )
			{
				result = result.negate( );
			}
		}
		else
		{
			result = parseUnsignedInclination( segment , defaultUnit );
			if( result.doubleValue( result.unit ) != 0 )
			{
				throw new SegmentParseException( segment , "+ or - is required for nonzero inclinations" );
			}
		}

		double incMax = incMaxes.get( result.unit );

		if( result.doubleValue( result.unit ) < -incMax || result.doubleValue( result.unit ) > incMax )
		{
			throw new SegmentParseException( segment , "inclinations must be between " + ( -incMax ) + " and " + incMax
				+ " " + result.unit );
		}

		return result;
	}

	public static List<Pair<Segment, Segment>> parseUnitsOptions( Segment segment )
	{
		List<Pair<Segment, Segment>> result = new ArrayList<>( );

		SegmentMatcher m = new SegmentMatcher( segment , UNITS_OPTION_PATTERN );

		while( m.find( ) )
		{
			if( m.group( ).equals( ";" ) )
			{
				break;
			}
			Segment quote = m.group( 4 );
			if( quote != null )
			{
				if( m.group( 7 ) == null )
				{
					throw new SegmentParseExpectedException( segment.substring( segment.length( ) ) ,
						ExpectedTypes.QUOTE );
				}
				result.add( new Pair<>( m.group( 1 ) , m.group( 5 ) ) );
			}
			else
			{
				result.add( new Pair<>( m.group( 1 ) , m.group( 3 ) ) );
			}
		}

		return result;
	}

	public static String unescape( Segment escapedText )
	{
		return escapedText.toString( ).replaceAll( "\\\\(.)" , "$1" );
	}
}

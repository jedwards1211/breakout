package org.breakout.wallsimport;

import static org.breakout.wallsimport.CardinalDirection.EAST;
import static org.breakout.wallsimport.CardinalDirection.NORTH;
import static org.breakout.wallsimport.CardinalDirection.SOUTH;
import static org.breakout.wallsimport.CardinalDirection.WEST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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
	public static Map<String, Unit<Length>> lengthUnits = new MapLiteral<String, Unit<Length>>( )
		.map( "m" , Length.meters )
		.map( "meters" , Length.meters )
		.map( "f" , Length.feet )
		.map( "feet" , Length.feet );

	public static Map<String, Unit<Angle>> azmUnits = new MapLiteral<String, Unit<Angle>>( )
		.map( "d" , Angle.degrees )
		.map( "degrees" , Angle.degrees )
		.map( "m" , Angle.milsNATO )
		.map( "mils" , Angle.milsNATO )
		.map( "g" , Angle.gradians )
		.map( "grads" , Angle.gradians );

	public static Map<String, Unit<Angle>> incUnits = new MapLiteral<String, Unit<Angle>>( )
		.map( "d" , Angle.degrees )
		.map( "degrees" , Angle.degrees )
		.map( "m" , Angle.milsNATO )
		.map( "mils" , Angle.milsNATO )
		.map( "g" , Angle.gradians )
		.map( "grads" , Angle.gradians )
		.map( "p" , Angle.percentGrade )
		.map( "percent" , Angle.percentGrade );

	public static Map<String, Unit<Length>> lengthUnitSuffixes = new MapLiteral<String, Unit<Length>>( )
		.map( "m" , Length.meters )
		.map( "f" , Length.feet );

	public static Map<String, Unit<Angle>> azmUnitSuffixes = new MapLiteral<String, Unit<Angle>>( )
		.map( "d" , Angle.degrees )
		.map( "g" , Angle.gradians )
		.map( "m" , Angle.milsNATO );

	public static Map<Unit<Angle>, Double> azmMaxes = new MapLiteral<Unit<Angle>, Double>( )
		.map( Angle.degrees , 360.0 )
		.map( Angle.gradians , 400.0 )
		.map( Angle.milsNATO , 6400.0 );

	public static Map<Unit<Angle>, Double> incMaxes = new MapLiteral<Unit<Angle>, Double>( )
		.map( Angle.degrees , 90.0 )
		.map( Angle.gradians , 100.0 )
		.map( Angle.milsNATO , 1600.0 )
		.map( Angle.percentGrade ,
			Double.POSITIVE_INFINITY );

	public static Map<String, Unit<Angle>> incUnitSuffixes = new MapLiteral<String, Unit<Angle>>( )
		.map( "d" , Angle.degrees )
		.map( "g" , Angle.gradians )
		.map( "m" , Angle.milsNATO )
		.map( "p" , Angle.percentGrade );

	public static Map<String, CardinalDirection> cardinalDirections = new MapLiteral<String, CardinalDirection>( )
		.map( "n" , NORTH )
		.map( "s" , SOUTH )
		.map( "e" , EAST )
		.map( "w" , WEST );

	/**
	 * The quoted value, unclosed quote, and semicolon handling regex from hell!
	 */
	public static Pattern UNITS_OPTION_PATTERN = Pattern
		.compile( "([a-zA-Z0-9]+)(=([^ \t\n\r\f\";]+|(\"(([^\\\\\"]|\\\\.)*)(\")?)|)?)?|;" );

	Stack<WallsUnits> stack = new Stack<>( );
	WallsUnits units = new WallsUnits( );

	Map<String, String> macros = new HashMap<>( );

	public static UnitizedDouble<Length> parseSignedDistance( Segment segment , Unit<Length> defaultUnit )
	{
		int lastIndex = segment.length( ) - 1;

		if( Character.isLetter( segment.charAt( lastIndex ) ) )
		{
			return new UnitizedDouble<Length>(
				segment.substring( 0 , lastIndex ).parseAsDouble( ) ,
				segment.substring( lastIndex ).parseToLowerCaseAsAnyOf( lengthUnitSuffixes ) );
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
				segment.substring( lastIndex ).parseToLowerCaseAsAnyOf( lengthUnitSuffixes ) );
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
			unit = segment.substring( lastIndex ).parseToLowerCaseAsAnyOf( azmUnitSuffixes );
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
			CardinalDirection startDirection = segment.substring( 0 , 1 ).parseToLowerCaseAsAnyOf( cardinalDirections );

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

			CardinalDirection endDirection = segment.substring( segment.length( ) - 1 ).parseToLowerCaseAsAnyOf(
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
			unit = segment.substring( lastIndex ).parseToLowerCaseAsAnyOf( incUnitSuffixes );
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
		return escapedText.toString( ).replace( "\\\\" , "\\" ).replace( "\\n" , "\n" ).replace( "\\t" , "\t" )
			.replace( "\\\"" , "\"" );
	}

	private void save( )
	{
		if( stack.size( ) > 10 )
		{
			throw new IllegalStateException( "you cannot save more than 10 times without restoring" );
		}
		stack.push( units.clone( ) );
	}

	private void restore( )
	{
		if( stack.isEmpty( ) )
		{
			throw new IllegalStateException( "no saved units to restore" );
		}
		units = stack.pop( );
	}

	private void reset( )
	{
		units = new WallsUnits( );
	}

	private void meters( )
	{
		units.d_unit = units.s_unit = Length.meters;
	}

	private void feet( )
	{
		units.d_unit = units.s_unit = Length.feet;
	}

	private void d( Segment s )
	{
		units.d_unit = s.parseToLowerCaseAsAnyOf( lengthUnits );
	}

	private void s( Segment s )
	{
		units.s_unit = s.parseToLowerCaseAsAnyOf( lengthUnits );
	}

	private void a( Segment s )
	{
		units.a_unit = s.parseToLowerCaseAsAnyOf( azmUnits );
	}

	private void ab( Segment s )
	{
		units.ab_unit = s.parseToLowerCaseAsAnyOf( azmUnits );
	}

	private void a_ab( Segment s )
	{
		units.a_unit = units.ab_unit = s.parseToLowerCaseAsAnyOf( azmUnits );
	}

	private void v( Segment s )
	{
		units.v_unit = s.parseToLowerCaseAsAnyOf( incUnits );
	}

	private void vb( Segment s )
	{
		units.vb_unit = s.parseToLowerCaseAsAnyOf( incUnits );
	}

	private void v_vb( Segment s )
	{
		units.v_unit = units.vb_unit = s.parseToLowerCaseAsAnyOf( incUnits );
	}

	private void decl( Segment s )
	{
		units.decl = parseAzimuthOffset( s , units.a_unit );
	}

	private void grid( Segment s )
	{
		units.grid = parseAzimuthOffset( s , units.a_unit );
	}

	private void rect( Segment s )
	{
		units.rect = parseAzimuthOffset( s , units.a_unit );
	}

	private void incd( Segment s )
	{
		units.incd = parseSignedDistance( s , units.d_unit );
	}

	private void inch( Segment s )
	{
		units.inch = parseSignedDistance( s , units.d_unit );
	}

	private void incs( Segment s )
	{
		units.incs = parseSignedDistance( s , units.s_unit );
	}

	private void inca( Segment s )
	{
		units.inca = parseAzimuthOffset( s , units.a_unit );
	}

	private void incab( Segment s )
	{
		units.incab = parseAzimuthOffset( s , units.ab_unit );
	}

	private void incv( Segment s )
	{
		units.incv = parseInclination( s , units.v_unit );
	}

	private void incvb( Segment s )
	{
		units.incvb = parseInclination( s , units.vb_unit );
	}

	private static final Map<String, Boolean> correctedValues = new MapLiteral<String, Boolean>( )
		.map( "c" , true )
		.map( "corrected" , true )
		.map( "n" , false )
		.map( "normal" , false );

	private static final Map<String, Boolean> noAverageValues = new MapLiteral<String, Boolean>( ).map( "x" , true );

	private void typeab( Segment s )
	{
		Segment[ ] parts = s.split( "," );

		if( parts.length > 3 )
		{
			throw new SegmentParseException( parts[ 3 ] , "to many arguments" );
		}

		units.typeab_corrected = parts[ 0 ].parseToLowerCaseAsAnyOf( correctedValues );
		units.typeab_tolerance = parts.length > 1 ? parts[ 1 ].parseAsUnsignedDouble( ) : null;
		units.typeab_noAverage = parts.length > 2 ? parts[ 2 ].parseToLowerCaseAsAnyOf( noAverageValues ) : false;
	}

	private void typevb( Segment s )
	{
		Segment[ ] parts = s.split( "," );

		if( parts.length > 3 )
		{
			throw new SegmentParseException( parts[ 3 ] , "to many arguments" );
		}

		units.typevb_corrected = parts[ 0 ].parseToLowerCaseAsAnyOf( correctedValues );
		units.typevb_tolerance = parts.length > 1 ? parts[ 1 ].parseAsUnsignedDouble( ) : null;
		units.typevb_noAverage = parts.length > 2 ? parts[ 2 ].parseToLowerCaseAsAnyOf( noAverageValues ) : false;
	}

	private static final BiConsumer<NewWallsParser, Segment> noArgOption( String option , Consumer<NewWallsParser> r )
	{
		return ( parser , segment ) ->
		{
			if( segment != null )
			{
				throw new SegmentParseException( segment , option + " doesn't take arguments" );
			}
			r.accept( parser );
		};
	}

	private static final BiConsumer<NewWallsParser, Segment> requiredArgOption( String option , BiConsumer<NewWallsParser, Segment> r )
	{
		return ( parser , segment ) ->
		{
			if( segment == null || segment.length( ) == 0 )
			{
				throw new SegmentParseException( segment , option + " requires an argument" );
			}
			r.accept( parser , segment );
		};
	}

	private static final Map<String, BiConsumer<NewWallsParser, Segment>> unitsOptionHandlers = new MapLiteral<String, BiConsumer<NewWallsParser, Segment>>( )
		.map( "save" , noArgOption( "save" , NewWallsParser::save ) )
		.map( "reset" , noArgOption( "reset" , NewWallsParser::reset ) )
		.map( "restore" , noArgOption( "restore" , NewWallsParser::restore ) )
		.map( "m" , noArgOption( "m" , NewWallsParser::meters ) )
		.map( "meters" , noArgOption( "meters" , NewWallsParser::meters ) )
		.map( "f" , noArgOption( "f" , NewWallsParser::feet ) )
		.map( "feet" , noArgOption( "feet" , NewWallsParser::feet ) )
		.map( "d" , requiredArgOption( "d" , NewWallsParser::d ) )
		.map( "s" , requiredArgOption( "s" , NewWallsParser::s ) )
		.map( "a" , requiredArgOption( "a" , NewWallsParser::a ) )
		.map( "ab" , requiredArgOption( "ab" , NewWallsParser::ab ) )
		.map( "a/ab" , requiredArgOption( "a/ab" , NewWallsParser::a_ab ) )
		.map( "v" , requiredArgOption( "v" , NewWallsParser::v ) )
		.map( "vb" , requiredArgOption( "vb" , NewWallsParser::vb ) )
		.map( "v/vb" , requiredArgOption( "v/vb" , NewWallsParser::v_vb ) )
		.map( "decl" , requiredArgOption( "decl" , NewWallsParser::decl ) )
		.map( "grid" , requiredArgOption( "grid" , NewWallsParser::grid ) )
		.map( "rect" , requiredArgOption( "rect" , NewWallsParser::rect ) )
		.map( "incd" , requiredArgOption( "incd" , NewWallsParser::incd ) )
		.map( "inch" , requiredArgOption( "inch" , NewWallsParser::inch ) )
		.map( "incs" , requiredArgOption( "incs" , NewWallsParser::incs ) )
		.map( "inca" , requiredArgOption( "inca" , NewWallsParser::inca ) )
		.map( "incab" , requiredArgOption( "incab" , NewWallsParser::incab ) )
		.map( "incv" , requiredArgOption( "incv" , NewWallsParser::incv ) )
		.map( "incvb" , requiredArgOption( "incvb" , NewWallsParser::incvb ) )
		.map( "typeab" , requiredArgOption( "typeab" , NewWallsParser::typeab ) )
		.map( "typevb" , requiredArgOption( "typevb" , NewWallsParser::typevb ) );

	/**
	 * 
	 * @param segment
	 *            the rest of the line after #units
	 */
	public void processUnits( Segment segment )
	{
		for( Pair<Segment, Segment> option : parseUnitsOptions( segment ) )
		{
			BiConsumer<NewWallsParser, Segment> consumer = option.getKey( ).parseToLowerCaseAsAnyOf( unitsOptionHandlers );
			try
			{
				consumer.accept( this , option.getValue( ) );
			}
			catch( IllegalStateException ex )
			{
				throw new SegmentParseException( option.getKey( ) , ex );
			}
		}
	}
}

package org.breakout.wallsimport;

import static org.breakout.wallsimport.CardinalDirection.EAST;
import static org.breakout.wallsimport.CardinalDirection.NORTH;
import static org.breakout.wallsimport.CardinalDirection.SOUTH;
import static org.breakout.wallsimport.CardinalDirection.WEST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import org.andork.collect.MapLiteral;
import org.andork.parse.ExpectedTypes;
import org.andork.parse.Segment;
import org.andork.parse.SegmentMatcher;
import org.andork.parse.SegmentParseException;
import org.andork.parse.SegmentParseExpectedException;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;
import org.andork.util.ArrayUtils;
import org.andork.util.Pair;

public class WallsParser
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

	@FunctionalInterface
	private static interface UnitsOption
	{
		public void process( WallsParser parser , Segment name , Segment arg );
	}

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
					throw new SegmentParseException( parts[ 2 ].charAfter( ) , WallsParseError.TOO_MANY_COLONS );
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
			throw new SegmentParseException( segment , WallsParseError.AZM_OUT_OF_RANGE );
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
				throw new SegmentParseException( segment , WallsParseError.AZM_OUT_OF_RANGE );
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
					throw new SegmentParseException( parts[ 2 ].charAfter( ) , WallsParseError.TOO_MANY_COLONS );
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
				throw new SegmentParseException( segment , WallsParseError.SIGNED_ZERO_INC );
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
				throw new SegmentParseException( segment , WallsParseError.UNSIGNED_NONZERO_INC );
			}
		}

		double incMax = incMaxes.get( result.unit );

		if( result.doubleValue( result.unit ) < -incMax || result.doubleValue( result.unit ) > incMax )
		{
			throw new SegmentParseException( segment , WallsParseError.INC_OUT_OF_RANGE );
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
				result.add( new Pair<>( m.group( 1 ) , quote ) );
			}
			else
			{
				result.add( new Pair<>( m.group( 1 ) , m.group( 3 ) ) );
			}
		}

		return result;
	}

	public static String dequoteUnitsArg( Segment escapedText )
	{
		if( !escapedText.startsWith( "\"" ) )
		{
			return escapedText.toString( );
		}
		return escapedText.substring( 1 , escapedText.length( ) - 1 ).toString( )
			.replace( "\\\\" , "\\" )
			.replace( "\\n" , "\n" )
			.replace( "\\t" , "\t" )
			.replace( "\\\"" , "\"" );
	}

	private void save( Segment optionName )
	{
		if( stack.size( ) > 10 )
		{
			throw new SegmentParseException( optionName , WallsParseError.STACK_FULL );
		}
		stack.push( units.clone( ) );
	}

	private void restore( Segment optionName )
	{
		if( stack.isEmpty( ) )
		{
			throw new SegmentParseException( optionName , WallsParseError.STACK_EMPTY );
		}
		units = stack.pop( );
	}

	private void reset( Segment optionName )
	{
		units = new WallsUnits( );
	}

	private void meters( Segment optionName )
	{
		units.d_unit = units.s_unit = Length.meters;
	}

	private void feet( Segment optionName )
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

	private static final Map<String, VectorElement> vectorElements = new MapLiteral<String, VectorElement>( )
		.map( "d" , VectorElement.D )
		.map( "a" , VectorElement.A )
		.map( "v" , VectorElement.V )
		.map( "n" , VectorElement.N )
		.map( "e" , VectorElement.E )
		.map( "u" , VectorElement.U );

	private static final List<Set<VectorElement>> allowedOrderSets = Arrays.asList(
		new HashSet<>( Arrays.asList( VectorElement.D , VectorElement.A , VectorElement.V ) ) ,
		new HashSet<>( Arrays.asList( VectorElement.D , VectorElement.A ) ) ,
		new HashSet<>( Arrays.asList( VectorElement.N , VectorElement.E , VectorElement.U ) ) ,
		new HashSet<>( Arrays.asList( VectorElement.N , VectorElement.E ) )
		);

	private void order( Segment s )
	{
		List<VectorElement> newOrder = new ArrayList<>( );
		for( Segment elem : s.split( "" ) )
		{
			newOrder.add( elem.parseToLowerCaseAsAnyOf( vectorElements ) );
		}
		if( !allowedOrderSets.stream( ).anyMatch( set -> newOrder.containsAll( set ) && newOrder.size( ) == set.size( ) ) )
		{
			throw new SegmentParseException( s , WallsParseError.INVALID_ORDER_ELEMENTS );
		}
		units.order = Collections.unmodifiableList( newOrder );
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
			throw new SegmentParseException( parts[ 3 ].charBefore( ) , WallsParseError.TOO_MANY_ARGS );
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
			throw new SegmentParseException( parts[ 3 ].charBefore( ) , WallsParseError.TOO_MANY_ARGS );
		}

		units.typevb_corrected = parts[ 0 ].parseToLowerCaseAsAnyOf( correctedValues );
		units.typevb_tolerance = parts.length > 1 ? parts[ 1 ].parseAsUnsignedDouble( ) : null;
		units.typevb_noAverage = parts.length > 2 ? parts[ 2 ].parseToLowerCaseAsAnyOf( noAverageValues ) : false;
	}

	private static final Map<String, CaseType> caseTypes = new MapLiteral<String, CaseType>( )
		.map( "u" , CaseType.Upper )
		.map( "upper" , CaseType.Upper )
		.map( "l" , CaseType.Lower )
		.map( "lower" , CaseType.Lower )
		.map( "m" , CaseType.Mixed )
		.map( "mixed" , CaseType.Mixed );

	private void case_( Segment s )
	{
		units.case_ = s.parseToLowerCaseAsAnyOf( caseTypes );
	}

	private static final Map<String, LrudType> lrudTypes = new MapLiteral<String, LrudType>( )
		.map( "f" , LrudType.From )
		.map( "from" , LrudType.From )
		.map( "t" , LrudType.To )
		.map( "to" , LrudType.To )
		.map( "fb" , LrudType.FB )
		.map( "tb" , LrudType.TB );

	private static final Map<String, LrudElement> lrudElements = new MapLiteral<String, LrudElement>( )
		.map( "l" , LrudElement.L )
		.map( "r" , LrudElement.R )
		.map( "u" , LrudElement.U )
		.map( "d" , LrudElement.D );

	private static final Set<LrudElement> lrudElementSet = new HashSet<>( Arrays.asList( LrudElement.values( ) ) );

	private void lrud( Segment s )
	{
		Segment[ ] parts = s.split( ":" , 2 );
		LrudType newLrud = parts[ 0 ].parseToLowerCaseAsAnyOf( lrudTypes );
		if( parts.length == 2 )
		{
			List<LrudElement> newLrudOrder = new ArrayList<>( );
			for( Segment elem : parts[ 1 ].split( "" ) )
			{
				newLrudOrder.add( elem.parseToLowerCaseAsAnyOf( lrudElements ) );
			}
			if( !newLrudOrder.containsAll( lrudElementSet ) || newLrudOrder.size( ) != lrudElementSet.size( ) )
			{
				throw new SegmentParseException( parts[ 1 ] , WallsParseError.INVALID_LRUD_ELEMENTS );
			}
			units.lrud_order = Collections.unmodifiableList( newLrudOrder );
		}
		else
		{
			units.lrud_order = Arrays.asList(
				LrudElement.L ,
				LrudElement.R ,
				LrudElement.U ,
				LrudElement.D
				);
		}
		units.lrud = newLrud;
	}

	private static final UnitsOption noArgUnitsOption( BiConsumer<WallsParser, Segment> r )
	{
		return ( parser , name , value ) ->
		{
			if( value != null )
			{
				throw new SegmentParseException( value , new ArgNotAllowedErrorMessage( name ) );
			}
			r.accept( parser , name );
		};
	}

	private static final UnitsOption requiredArgUnitsOption( BiConsumer<WallsParser, Segment> r )
	{
		return ( parser , name , value ) ->
		{
			if( value == null || value.length( ) == 0 )
			{
				throw new SegmentParseException( value != null ? value.charAfter( ) : name.charAfter( ) ,
					new ArgRequiredErrorMessage( name ) );
			}
			r.accept( parser , value );
		};
	}

	private static final Map<String, UnitsOption> unitsOptions = new MapLiteral<String, UnitsOption>( )
		.map( "save" , noArgUnitsOption( WallsParser::save ) )
		.map( "reset" , noArgUnitsOption( WallsParser::reset ) )
		.map( "restore" , noArgUnitsOption( WallsParser::restore ) )
		.map( "m" , noArgUnitsOption( WallsParser::meters ) )
		.map( "meters" , noArgUnitsOption( WallsParser::meters ) )
		.map( "f" , noArgUnitsOption( WallsParser::feet ) )
		.map( "feet" , noArgUnitsOption( WallsParser::feet ) )
		.map( "d" , requiredArgUnitsOption( WallsParser::d ) )
		.map( "s" , requiredArgUnitsOption( WallsParser::s ) )
		.map( "a" , requiredArgUnitsOption( WallsParser::a ) )
		.map( "ab" , requiredArgUnitsOption( WallsParser::ab ) )
		.map( "a/ab" , requiredArgUnitsOption( WallsParser::a_ab ) )
		.map( "v" , requiredArgUnitsOption( WallsParser::v ) )
		.map( "vb" , requiredArgUnitsOption( WallsParser::vb ) )
		.map( "v/vb" , requiredArgUnitsOption( WallsParser::v_vb ) )
		.map( "o" , requiredArgUnitsOption( WallsParser::order ) )
		.map( "order" , requiredArgUnitsOption( WallsParser::order ) )
		.map( "decl" , requiredArgUnitsOption( WallsParser::decl ) )
		.map( "grid" , requiredArgUnitsOption( WallsParser::grid ) )
		.map( "rect" , requiredArgUnitsOption( WallsParser::rect ) )
		.map( "incd" , requiredArgUnitsOption( WallsParser::incd ) )
		.map( "inch" , requiredArgUnitsOption( WallsParser::inch ) )
		.map( "incs" , requiredArgUnitsOption( WallsParser::incs ) )
		.map( "inca" , requiredArgUnitsOption( WallsParser::inca ) )
		.map( "incab" , requiredArgUnitsOption( WallsParser::incab ) )
		.map( "incv" , requiredArgUnitsOption( WallsParser::incv ) )
		.map( "incvb" , requiredArgUnitsOption( WallsParser::incvb ) )
		.map( "typeab" , requiredArgUnitsOption( WallsParser::typeab ) )
		.map( "typevb" , requiredArgUnitsOption( WallsParser::typevb ) )
		.map( "case" , requiredArgUnitsOption( WallsParser::case_ ) )
		.map( "lrud" , requiredArgUnitsOption( WallsParser::lrud ) );

	/**
	 * 
	 * @param segment
	 *            the rest of the line after #units
	 */
	public void processUnits( Segment segment )
	{
		for( Pair<Segment, Segment> pair : parseUnitsOptions( segment ) )
		{
			pair.getKey( ).parseToLowerCaseAsAnyOf( unitsOptions ).process( this , pair.getKey( ) , pair.getValue( ) );
		}
	}

	/**
	 * Should I use a regex for this?  TOTALLY!!!
	 */
	private static final Pattern VECTOR_LINE_PATTERN =
		Pattern.compile( "^\\s*(([^ \t\r\n\f;]*)?\\s*([^(<*;]\\S*)?\\s*[^()<>*;]*)\\s*(\\(([^();]*)\\)?)?\\s*([^<*;#]*)\\s*(<([^<>;]*)>?|\\*([^*;]*)\\*?)?\\s*([^#;]*)\\s*(#([^;]*))?(;(.*))?" );
	
	// and for interpreting the above...
	private static final int MEASUREMENT_GROUP = 1;
	private static final int VARIANCE_GROUP = 4;
	private static final int VARIANCE_INTERIOR_GROUP = 5;
	private static final int BETWEEN_VARIANCE_AND_LRUD_GROUP = 6;
	private static final int LRUD_GROUP = 7;
	private static final int LRUD_INTERIOR_1_GROUP = 8;
	private static final int LRUD_INTERIOR_2_GROUP = 9;
	private static final int BETWEEN_LRUD_AND_DIRECTIVE_GROUP = 10;
	private static final int DIRECTIVE_GROUP = 12;
	private static final int COMMENT_GROUP = 14;

	public static class SplitVectorLine
	{
		public Segment from;
		public Segment to;
		public Segment[ ] measurements;
		public Segment[ ] variance;
		public Segment[ ] lruds;
		public Segment directive;
		public Segment directiveArg;
	}

	private static void dumpMatches( SegmentMatcher matcher )
	{
		for( int i = 0 ; i <= matcher.groupCount( ) ; i++ )
		{
			Segment group = matcher.group( i );
			if( group == null )
			{
				continue;
			}
			System.out.print( i + ": " );
			for( int k = 0 ; k < matcher.start( i ) ; k++ )
			{
				System.out.print( ' ' );
			}
			System.out.println( group );
		}
	}

	private static void temp( String vectorLine )
	{
		Segment segment = new Segment( vectorLine , null , 0 , 0 );
		SegmentMatcher matcher = new SegmentMatcher( segment , VECTOR_LINE_PATTERN );
		if( matcher.find( ) )
		{
			dumpMatches( matcher );
		}
	}

	public static void main( String[ ] args )
	{
		temp( "*A*1 B1 350 41 25 (3, 5) *2, 3, *#Seg blah;4, 5>" );
		temp( "*A*1 *2,3,4,5*" );
		temp( "<A1, <bash <2,3,4,5>" );
		temp( "A1 B1 350 41 25 (3, 5) <2, 3, > okay>< weird #Seg blah;4, 5>" );
		temp( "A1 B1 350 41 25 <2, 3, >#Seg blah;4, 5>" );
		temp( "A1 B1 350 41 25 (3, 5) <2, 3, >#Seg blah;4, 5>" );
		temp( "A1 B1 350 41 25 (3;, 5) <2, 3, >#Seg blah;4, 5>" );
		temp( "A1 B1 350 41 25 (3, 5) hello <2, 3, *#Seg blah;4, 5>" );
	}
}

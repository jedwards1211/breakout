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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.andork.collect.MapLiteral;
import org.andork.func.CharPredicate;
import org.andork.parse.ExpectedTypes;
import org.andork.parse.LineParser;
import org.andork.parse.Segment;
import org.andork.parse.SegmentParseException;
import org.andork.parse.SegmentParseExpectedException;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitizedDouble;

public class WallsParser
{
	private static Map<String, Unit<Length>> lengthUnits = new MapLiteral<String, Unit<Length>>( )
		.map( "m" , Length.meters )
		.map( "meters" , Length.meters )
		.map( "f" , Length.feet )
		.map( "feet" , Length.feet );

	private static Map<String, Unit<Angle>> azmUnits = new MapLiteral<String, Unit<Angle>>( )
		.map( "d" , Angle.degrees )
		.map( "degrees" , Angle.degrees )
		.map( "m" , Angle.milsNATO )
		.map( "mils" , Angle.milsNATO )
		.map( "g" , Angle.gradians )
		.map( "grads" , Angle.gradians );

	private static Map<String, Unit<Angle>> incUnits = new MapLiteral<String, Unit<Angle>>( )
		.map( "d" , Angle.degrees )
		.map( "degrees" , Angle.degrees )
		.map( "m" , Angle.milsNATO )
		.map( "mils" , Angle.milsNATO )
		.map( "g" , Angle.gradians )
		.map( "grads" , Angle.gradians )
		.map( "p" , Angle.percentGrade )
		.map( "percent" , Angle.percentGrade );

	private static Map<String, Unit<Length>> lengthUnitSuffixes = new MapLiteral<String, Unit<Length>>( )
		.map( "m" , Length.meters )
		.map( "f" , Length.feet );

	private static Map<String, Unit<Angle>> azmUnitSuffixes = new MapLiteral<String, Unit<Angle>>( )
		.map( "d" , Angle.degrees )
		.map( "g" , Angle.gradians )
		.map( "m" , Angle.milsNATO );

	private static Map<Character, Unit<Length>> lengthUnitSuffixesC = new MapLiteral<Character, Unit<Length>>( )
		.map( 'm' , Length.meters )
		.map( 'f' , Length.feet );

	private static Map<Character, Unit<Angle>> azmUnitSuffixesC = new MapLiteral<Character, Unit<Angle>>( )
		.map( 'd' , Angle.degrees )
		.map( 'g' , Angle.gradians )
		.map( 'm' , Angle.milsNATO );

	private static Map<Unit<Angle>, Double> azmMaxes = new MapLiteral<Unit<Angle>, Double>( )
		.map( Angle.degrees , 360.0 )
		.map( Angle.gradians , 400.0 )
		.map( Angle.milsNATO , 6400.0 );

	private static Map<Unit<Angle>, Double> incMaxes = new MapLiteral<Unit<Angle>, Double>( )
		.map( Angle.degrees , 90.0 )
		.map( Angle.gradians , 100.0 )
		.map( Angle.milsNATO , 1600.0 )
		.map( Angle.percentGrade ,
			Double.POSITIVE_INFINITY );

	private static Map<String, Unit<Angle>> incUnitSuffixes = new MapLiteral<String, Unit<Angle>>( )
		.map( "d" , Angle.degrees )
		.map( "g" , Angle.gradians )
		.map( "m" , Angle.milsNATO )
		.map( "p" , Angle.percentGrade );

	private static Map<String, CardinalDirection> cardinalDirections = new MapLiteral<String, CardinalDirection>( )
		.map( "n" , NORTH )
		.map( "s" , SOUTH )
		.map( "e" , EAST )
		.map( "w" , WEST );

	private static Map<Character, Unit<Angle>> incUnitSuffixesC = new MapLiteral<Character, Unit<Angle>>( )
		.map( 'd' , Angle.degrees )
		.map( 'g' , Angle.gradians )
		.map( 'm' , Angle.milsNATO )
		.map( 'p' , Angle.percentGrade );

	private static Map<Character, CardinalDirection> cardinalDirectionsC = new MapLiteral<Character, CardinalDirection>( )
		.map( 'n' , NORTH )
		.map( 's' , SOUTH )
		.map( 'e' , EAST )
		.map( 'w' , WEST );

	private static Map<Character, CardinalDirection> eastWestC = new MapLiteral<Character, CardinalDirection>( )
		.map( 'e' , EAST )
		.map( 'w' , WEST );

	private static Map<Character, CardinalDirection> northSouthC = new MapLiteral<Character, CardinalDirection>( )
		.map( 'n' , NORTH )
		.map( 's' , SOUTH );

	private static Map<CardinalDirection, Map<Character, CardinalDirection>> toCardinalDirectionsC =
		new MapLiteral<CardinalDirection, Map<Character, CardinalDirection>>( )
			.map( CardinalDirection.NORTH , eastWestC )
			.map( CardinalDirection.SOUTH , eastWestC )
			.map( CardinalDirection.EAST , northSouthC )
			.map( CardinalDirection.WEST , northSouthC );

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

	public static interface UnitsOptionVisitor
	{
		public void unitsOption( Segment name , Segment value );
	}

	private static class UnitsOptionParser
	{
		private Segment segment;
		private UnitsOptionVisitor visitor;
		private int i = 0;

		public UnitsOptionParser( Segment segment , UnitsOptionVisitor visitor )
		{
			this.segment = segment;
			this.visitor = visitor;

			moveTo( c -> !Character.isWhitespace( c ) );

			while( i < segment.length( ) && segment.charAt( i ) != ';' )
			{
				parseOption( );
				moveTo( c -> !Character.isWhitespace( c ) );
			}
		}

		private void moveTo( CharPredicate p )
		{
			while( i < segment.length( ) && !p.test( segment.charAt( i ) ) )
			{
				i++;
			}
		}

		private void parseOption( )
		{
			int start = i;
			moveTo( c -> Character.isWhitespace( c ) || c == '=' || c == ';' );
			if( i == start )
			{
				throw new SegmentParseExpectedException( segment.substring( i , i + 1 ) , WallsExpectedTypes.UNITS_OPTION );
			}

			Segment name = segment.substring( start , i );

			if( i < segment.length( ) && segment.charAt( i ) == '=' )
			{
				i++;
				visitor.unitsOption( name , parseValue( ) );
				return;
			}

			visitor.unitsOption( name , null );
		}

		private Segment parseValue( )
		{
			if( i == segment.length( ) )
			{
				return segment.substring( i );
			}

			char ch = segment.charAt( i );
			if( Character.isWhitespace( ch ) || ch == ';' )
			{
				return segment.substring( i , i );
			}

			if( ch == '"' )
			{
				return parseQuotedValue( );
			}

			int start = i;
			moveTo( c -> Character.isWhitespace( c ) || c == ';' );
			return segment.substring( start , i );
		}

		private Segment parseQuotedValue( )
		{
			int start = i;
			i++;
			while( i < segment.length( ) )
			{
				char c = segment.charAt( i );
				switch( c )
				{
				case '\\':
					i++;
					if( i == segment.length( ) )
					{
						throw new SegmentParseExpectedException( segment.charAfter( ) , WallsExpectedTypes.ESCAPED_CHAR );
					}
					break;
				case '"':
					i++;
					return segment.substring( start , i );
				}
				i++;
			}
			throw new SegmentParseExpectedException( segment.charAfter( ) , ExpectedTypes.QUOTE );
		}
	}

	public static void parseUnitsOptions( Segment segment , UnitsOptionVisitor visitor )
	{
		new UnitsOptionParser( segment , visitor );
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
		parseUnitsOptions( segment , ( name , value ) ->
			name.parseToLowerCaseAsAnyOf( unitsOptions ).process( this , name , value ) );
	}

	public static interface VectorLineVisitor
	{
		public void from( Segment from );

		public void to( Segment to );

		public void measurement( int index , Segment measurement );

		public void variance( int index , Segment item );

		public void lrud( int index , Segment item );

		public void directive( Segment directive , Segment argument );

		public void comment( Segment comment );
	}

	private static class WallsLineParser extends LineParser
	{
		private static final Pattern OPTIONAL_PATTERN = Pattern.compile( "--+" );

		public WallsLineParser( Segment line )
		{
			super( line );
		}

		public UnitizedDouble<Length> length( Unit<Length> defaultUnit )
		{
			return new UnitizedDouble<>( doubleLiteral( ) ,
				oneOfLowercase( lengthUnitSuffixesC , defaultUnit ) );
		}

		public UnitizedDouble<Length> unsignedLength( Unit<Length> defaultUnit )
		{
			return new UnitizedDouble<>( unsignedDoubleLiteral( ) ,
				oneOfLowercase( lengthUnitSuffixesC , defaultUnit ) );
		}

		public UnitizedDouble<Angle> nonQuadrantAzimuth( Unit<Angle> defaultUnit )
		{
			int start = i;

			Double value = maybeR( this::unsignedDoubleLiteral );
			if( maybe( this::colon ) )
			{
				Double minutes = maybeR( this::unsignedDoubleLiteral );
				Double seconds = null;
				if( maybe( this::colon ) )
				{
					seconds = maybeR( this::unsignedDoubleLiteral );
				}
				if( value == null && minutes == null && seconds == null )
				{
					throwAllExpected( );
				}
				return dms( value , minutes , seconds );
			}
			else if( value == null )
			{
				throwAllExpected( );
			}
			UnitizedDouble<Angle> result = new UnitizedDouble<>( value ,
				oneOfLowercase( azmUnitSuffixesC , defaultUnit ) );

			int end = i;

			if( result.doubleValue( result.unit ) > azmMaxes.get( result.unit ) )
			{
				throw new SegmentParseException( line.substring( start , end ) , WallsParseError.AZM_OUT_OF_RANGE );
			}

			return result;
		}

		private static UnitizedDouble<Angle> dms( Double degrees , Double minutes , Double seconds )
		{
			Double value = 0.0;
			if( degrees != null )
			{
				value += degrees;
			}
			if( minutes != null )
			{
				value += minutes / 60.0;
			}
			if( seconds != null )
			{
				value += seconds / 3600.0;
			}
			return new UnitizedDouble<>( value , Angle.degrees );
		}

		public UnitizedDouble<Angle> quadrantAzimuth( Unit<Angle> defaultUnit )
		{
			CardinalDirection from = oneOfLowercase( cardinalDirectionsC );

			int start = i;
			UnitizedDouble<Angle> angle = nonQuadrantAzimuth( defaultUnit );
			int end = i;

			if( angle.doubleValue( angle.unit ) > incMaxes.get( angle.unit ) )
			{
				throw new SegmentParseException( line.substring( start , end ) , WallsParseError.AZM_OUT_OF_RANGE );
			}

			CardinalDirection to = oneOfLowercase( toCardinalDirectionsC.get( from ) );

			return from.toward( to , angle );
		}

		public UnitizedDouble<Angle> azimuth( Unit<Angle> defaultUnit )
		{
			return oneOfR(
				( ) -> quadrantAzimuth( defaultUnit ) ,
				( ) -> nonQuadrantAzimuth( defaultUnit ) );
		}

		public UnitizedDouble<Angle> unsignedInclination( Unit<Angle> defaultUnit )
		{
			int start = i;
			UnitizedDouble<Angle> result = new UnitizedDouble<>(
				unsignedDoubleLiteral( ) , oneOfLowercase( incUnitSuffixesC , defaultUnit ) );
			int end = i;

			if( result.doubleValue( result.unit ) > incMaxes.get( result.unit ) )
			{
				throw new SegmentParseException( line.substring( start , end ) , WallsParseError.INC_OUT_OF_RANGE );
			}

			return result;
		}

		public UnitizedDouble<Angle> inclination( Unit<Angle> defaultUnit )
		{
			int start = i;
			Double signum = maybeR( ( ) -> oneOf( SIGN_SIGNUMS ) );
			UnitizedDouble<Angle> angle = unsignedInclination( defaultUnit );
			int end = i;

			boolean zeroAngle = angle.doubleValue( angle.unit ) == 0.0;
			if( signum == null )
			{
				if( !zeroAngle )
				{
					throw new SegmentParseException( line.substring( start , end ) , WallsParseError.UNSIGNED_NONZERO_INC );
				}
				return angle;
			}
			else
			{
				if( zeroAngle )
				{
					throw new SegmentParseException( line.substring( start , end ) , WallsParseError.SIGNED_ZERO_INC );
				}
				return signum < 0 ? angle.negate( ) : angle;
			}
		}

		public VarianceOverride varianceOverride( )
		{
			// TODO
			return null;
		}

		public <R> R optional( Supplier<R> production )
		{
			return oneOfR(
				( ) ->
				{
					expect( OPTIONAL_PATTERN );
					return null;
				} , production );
		}
	}

	public static interface VectorLineVisitor2
	{
		public WallsUnits units( );

		public void visitFrom( Segment from );

		public void visitTo( Segment to );

		public void visitDistance( UnitizedDouble<Length> distance );

		public void visitFrontsightAzimuth( UnitizedDouble<Angle> fsAzimuth );

		public void visitBacksightAzimuth( UnitizedDouble<Angle> bsAzimuth );

		public void visitFrontsightInclination( UnitizedDouble<Angle> fsInclination );

		public void visitBacksightInclination( UnitizedDouble<Angle> bsInclination );

		public void visitNorth( UnitizedDouble<Length> north );

		public void visitEast( UnitizedDouble<Length> east );

		public void visitVectorUp( UnitizedDouble<Length> up );

		public void visitInstrumentHeight( UnitizedDouble<Length> instrumentHeight );

		public void visitTargetHeight( UnitizedDouble<Length> targetHeight );

		public void visitLeft( UnitizedDouble<Length> left );

		public void visitRight( UnitizedDouble<Length> right );

		public void visitUp( UnitizedDouble<Length> up );

		public void visitDown( UnitizedDouble<Length> down );

		public void visitLrudFacingAngle( UnitizedDouble<Angle> facingAngle );

		public void visitCFlag( );

		public void visitHorizontalVarianceOverride( VarianceOverride variance );

		public void visitVerticalVarianceOverride( VarianceOverride variance );

		/**
		 * In this case "segment" refers to Walls' #Segment directive, not {@link Segment}. Nonetheless, the
		 * argument to the #Segment directive is given as a {@link Segment} :)
		 * 
		 * @param segment
		 *            the argument to the #Segment directive
		 */
		public void visitSegment( Segment segment );

		public void visitComment( Segment comment );
	}

	private static class VectorLineParser2 extends WallsLineParser implements VectorElementVisitor , TapingMethodElementVisitor , LrudElementVisitor
	{
		private VectorLineVisitor2 visitor;
		private static final Pattern FROM_STATION_PATTERN = Pattern.compile( "[^:;,# \t]{1,8}" );
		private static final Pattern TO_STATION_PATTERN = Pattern.compile( "[^<*:;,# \t][^:;,# \t]{0,7}" );

		public VectorLineParser2( Segment line , VectorLineVisitor2 visitor )
		{
			super( line );
			this.visitor = visitor;
		}

		public void parse( )
		{
			throwAllExpected( this::vectorLine );
		}

		public void vectorLine( )
		{
			maybe( this::whitespace );
			oneOf( this::lineWithData , this::commentOrEndOfLine );
		}

		public void lineWithData( )
		{
			fromStation( );
			whitespace( );
			afterFromStation( );
		}

		public void fromStation( )
		{
			visitor.visitFrom( expect( FROM_STATION_PATTERN ) );
		}

		public void afterFromStation( )
		{
			oneOf(
				( ) ->
				{
					toStation( );
					whitespace( );
					afterToStation( );
				} ,
				( ) ->
				{
					lruds( );
					afterLruds( );
				} );
		}

		public void toStation( )
		{
			visitor.visitTo( expect( TO_STATION_PATTERN ) );
		}

		public void afterToStation( )
		{
			int k = 0;
			for( VectorElement elem : visitor.units( ).order )
			{
				if( k++ > 0 )
				{
					whitespace( );
				}
				elem.visit( this );
			}
			for( TapingMethodElement elem : visitor.units( ).tape )
			{
				whitespace( );
				elem.visit( this );
			}
			if( maybe( this::whitespace ) )
			{
				afterMeasurements( );
			}
		}

		@Override
		public void visitDistance( )
		{
			visitor.visitDistance( unsignedLength( visitor.units( ).d_unit ) );
		}

		@Override
		public void visitAzimuth( )
		{
			visitor.visitFrontsightAzimuth( optional( ( ) -> azimuth( visitor.units( ).a_unit ) ) );
			if( maybe( this::forwardSlash ) )
			{
				visitor.visitBacksightAzimuth( optional( ( ) -> azimuth( visitor.units( ).ab_unit ) ) );
			}
		}

		@Override
		public void visitInclination( )
		{
			visitor.visitFrontsightInclination( optional( ( ) -> inclination( visitor.units( ).v_unit ) ) );
			if( maybe( this::forwardSlash ) )
			{
				visitor.visitBacksightInclination( optional( ( ) -> inclination( visitor.units( ).vb_unit ) ) );
			}
		}

		@Override
		public void visitEast( )
		{
			visitor.visitEast( length( visitor.units( ).d_unit ) );
		}

		@Override
		public void visitNorth( )
		{
			visitor.visitNorth( length( visitor.units( ).d_unit ) );
		}

		@Override
		public void visitRectUp( )
		{
			visitor.visitVectorUp( length( visitor.units( ).d_unit ) );
		}

		@Override
		public void visitInstrumentHeight( )
		{
			visitor.visitInstrumentHeight( optional( ( ) -> length( visitor.units( ).s_unit ) ) );
		}

		@Override
		public void visitTargetHeight( )
		{
			visitor.visitTargetHeight( optional( ( ) -> length( visitor.units( ).s_unit ) ) );
		}

		public void afterMeasurements( )
		{
			if( maybe( this::varianceOverrides ) )
			{
				maybe( this::whitespace );
			}
			afterVarianceOverrides( );
		}

		public void varianceOverrides( )
		{
			expect( '(' );
			maybe( this::whitespace );
			VarianceOverride horizontal = varianceOverride( );
			visitor.visitHorizontalVarianceOverride( horizontal );
			maybe( this::whitespace );
			if( maybe( this::comma ) )
			{
				maybe( this::whitespace );
				visitor.visitVerticalVarianceOverride( varianceOverride( ) );
				maybe( this::whitespace );
			}
			else
			{
				visitor.visitVerticalVarianceOverride( horizontal );
			}
			expect( ')' );
		}

		public void afterVarianceOverrides( )
		{
			if( maybe( this::lruds ) )
			{
				maybe( this::whitespace );
			}
			afterLruds( );
		}

		public void lruds( )
		{
			oneOfWithLookahead( ( ) ->
			{
				expect( '<' );
				lrudContent( );
				expect( '>' );
			} , ( ) ->
			{
				expect( '*' );
				lrudContent( );
				expect( '*' );
			} );
		}

		public void lrudContent( )
		{
			oneOfWithLookahead( this::commaDelimLrudContent , this::whitespaceDelimLrudContent );
		}

		public void commaDelimLrudContent( )
		{
			maybe( this::whitespace );
			int m = 0;
			for( LrudElement elem : visitor.units( ).lrud_order )
			{
				if( m++ > 0 )
				{
					maybe( this::whitespace );
					comma( );
					maybe( this::whitespace );
				}
				elem.visit( this );
			}
			maybe( this::whitespace );
			afterRequiredCommaDelimLrudMeasurements( );
		}

		public void whitespaceDelimLrudContent( )
		{
			maybe( this::whitespace );
			int m = 0;
			for( LrudElement elem : visitor.units( ).lrud_order )
			{
				if( m++ > 0 )
				{
					whitespace( );
				}
				elem.visit( this );
			}
			maybe( this::whitespace );
			afterRequiredWhitespaceDelimLrudMeasurements( );
		}

		@Override
		public void visitLeft( )
		{
			visitor.visitLeft( optional( ( ) -> unsignedLength( visitor.units( ).s_unit ) ) );
		}

		@Override
		public void visitRight( )
		{
			visitor.visitRight( optional( ( ) -> unsignedLength( visitor.units( ).s_unit ) ) );
		}

		@Override
		public void visitUp( )
		{
			visitor.visitUp( optional( ( ) -> unsignedLength( visitor.units( ).s_unit ) ) );
		}

		@Override
		public void visitDown( )
		{
			visitor.visitDown( optional( ( ) -> unsignedLength( visitor.units( ).s_unit ) ) );
		}

		public void afterRequiredCommaDelimLrudMeasurements( )
		{
			if( maybe( this::comma ) )
			{
				maybe( this::whitespace );
				oneOf(
					( ) ->
					{
						lrudFacingAngle( );
						maybe( this::whitespace );
						if( maybe( this::comma ) )
						{
							maybe( this::whitespace );
							lrudCFlag( );
						}
					} ,
					this::lrudCFlag );
			}
		}

		public void afterRequiredWhitespaceDelimLrudMeasurements( )
		{
			maybe( ( ) -> oneOf(
				( ) ->
				{
					lrudFacingAngle( );
					if( maybe( this::whitespace ) )
					{
						maybe( this::lrudCFlag );
					}
				} ,
				this::lrudCFlag ) );
		}

		public void lrudFacingAngle( )
		{
			visitor.visitLrudFacingAngle( azimuth( visitor.units( ).a_unit ) );
		}

		public void lrudCFlag( )
		{
			expectIgnoreCase( 'c' );
			visitor.visitCFlag( );
		}

		public void afterLruds( )
		{
			if( maybe( this::directive ) )
			{
				maybe( this::whitespace );
			}
			commentOrEndOfLine( );
		}

		public void directive( )
		{
			expect( '#' );
			// TODO
		}

		public void commentOrEndOfLine( )
		{
			oneOf( this::comment , this::endOfLine );
		}

		public void comment( )
		{
			expect( ';' );
			visitor.visitComment( remaining( ) );
		}
	}

	private static class VectorLineParser
	{
		private static final Function<Segment, Consumer<VectorLineParser>> TO_START =
			ifNotEmpty( VectorLineParser::to , WallsExpectedTypes.TO_STATION );

		private static final Function<Segment, Consumer<VectorLineParser>> MEASUREMENT_START =
			ifNotEmpty( VectorLineParser::measurement , WallsExpectedTypes.MEASUREMENT );

		private static final Function<Segment, Consumer<VectorLineParser>> VARIANCE_START =
			ifEqualsAny( VectorLineParser::variance , "(" );

		private static final Function<Segment, Consumer<VectorLineParser>> LRUD_START =
			ifEqualsAny( VectorLineParser::lruds , "<" , "*" );

		private static final Function<Segment, Consumer<VectorLineParser>> DIRECTIVE_START =
			ifEqualsAny( VectorLineParser::directive , "#" );

		private static final Function<Segment, Consumer<VectorLineParser>> END_OF_LINE_OR_COMMENT_START =
			ifEmpty( VectorLineParser::end , ";" , WallsExpectedTypes.END_OF_LINE );

		private Segment line;
		private VectorLineVisitor visitor;
		private int i = 0;
		private int numVectorComponents;

		private int measurementCount;

		private static final CharPredicate whitespace = Character::isWhitespace;
		private static final CharPredicate nonWhitespace = whitespace.negate( );

		private static final Map<String, Character> LRUD_END_CHARS = new MapLiteral<String, Character>( )
			.map( "<" , '>' )
			.map( "*" , '*' );

		public VectorLineParser( Segment line , int numVectorComponents , VectorLineVisitor visitor )
		{
			this.line = line;
			this.visitor = visitor;
			this.numVectorComponents = numVectorComponents;
			Segment[ ] parts = line.split( ";" , 2 );
			if( parts.length > 0 )
			{
				this.line = parts[ 0 ];
				if( parts.length > 1 )
				{
					visitor.comment( parts[ 1 ] );
				}
				start( );
			}
		}

		private void moveTo( CharPredicate p )
		{
			while( i < line.length( ) && !p.test( line.charAt( i ) ) )
			{
				i++;
			}
		}

		private void moveToExpected( char ch )
		{
			moveTo( c -> c == ch );
			if( i == line.length( ) )
			{
				throw new SegmentParseExpectedException( line.substring( i ) , Character.toString( ch ) );
			}
		}

		private static Function<Segment, Consumer<VectorLineParser>> ifEqualsAny( Consumer<VectorLineParser> thenDo , Object ... expected )
		{
			return s ->
			{
				for( Object obj : expected )
				{
					if( s.equals( obj ) )
					{
						return thenDo;
					}
				}
				throw new SegmentParseExpectedException( s , expected );
			};
		}

		private static Function<Segment, Consumer<VectorLineParser>> ifEmpty( Consumer<VectorLineParser> thenDo , Object ... expected )
		{
			return s ->
			{
				if( s.equals( "" ) )
				{
					return thenDo;
				}
				throw new SegmentParseExpectedException( s , expected );
			};
		}

		private static Function<Segment, Consumer<VectorLineParser>> ifNotEmpty( Consumer<VectorLineParser> thenDo , Object name )
		{
			return s ->
			{
				if( !s.equals( "" ) )
				{
					return thenDo;
				}
				throw new SegmentParseExpectedException( s , name );
			};
		}

		private void start( )
		{
			moveTo( nonWhitespace );
			if( i == line.length( ) )
			{
				return;
			}
			from( );
		}

		private void from( )
		{
			int start = i;
			moveTo( whitespace );
			if( i == start )
			{
				throw new SegmentParseExpectedException( line.charAtAsSegment( i ) , WallsExpectedTypes.FROM_STATION );
			}
			visitor.from( line.substring( start , i ) );
			moveTo( nonWhitespace );

			afterFrom( );
		}

		@SuppressWarnings( "unchecked" )
		private static final Function<Segment, Consumer<VectorLineParser>>[ ] AFTER_FROM = new Function[ ] {
			LRUD_START ,
			TO_START
		};

		private void afterFrom( )
		{
			line.charAtAsSegment( i ).parseAsAnyOf( AFTER_FROM ).accept( this );
		}

		private void to( )
		{
			int start = i;
			moveTo( whitespace );
			if( i == start )
			{
				throw new SegmentParseExpectedException( line.charAtAsSegment( i ) , WallsExpectedTypes.TO_STATION );
			}
			visitor.to( line.substring( start , i ) );
			moveTo( nonWhitespace );

			afterTo( );
		}

		@SuppressWarnings( "unchecked" )
		private static final Function<Segment, Consumer<VectorLineParser>>[ ] REQUIRED_MEASUREMENT = new Function[ ] {
			MEASUREMENT_START
		};

		@SuppressWarnings( "unchecked" )
		private static final Function<Segment, Consumer<VectorLineParser>>[ ] AFTER_REQUIRED_MEASUREMENTS = new Function[ ] {
			VARIANCE_START ,
			LRUD_START ,
			DIRECTIVE_START ,
			MEASUREMENT_START ,
			END_OF_LINE_OR_COMMENT_START
		};

		@SuppressWarnings( "unchecked" )
		private static final Function<Segment, Consumer<VectorLineParser>>[ ] AFTER_MEASUREMENTS = new Function[ ] {
			VARIANCE_START ,
			LRUD_START ,
			DIRECTIVE_START ,
			END_OF_LINE_OR_COMMENT_START
		};

		private void afterTo( )
		{
			if( measurementCount < numVectorComponents )
			{
				line.charAtAsSegment( i ).parseAsAnyOf( REQUIRED_MEASUREMENT ).accept( this );
			}
			else if( measurementCount < numVectorComponents + 2 )
			{
				line.charAtAsSegment( i ).parseAsAnyOf( AFTER_REQUIRED_MEASUREMENTS ).accept( this );
			}
			else
			{
				line.charAtAsSegment( i ).parseAsAnyOf( AFTER_MEASUREMENTS ).accept( this );
			}
		}

		private void measurement( )
		{
			int start = i;
			moveTo( whitespace );
			if( i == start )
			{
				throw new SegmentParseExpectedException( line.charAtAsSegment( i ) , WallsExpectedTypes.MEASUREMENT );
			}
			visitor.measurement( measurementCount++ , line.substring( start , i ) );
			moveTo( nonWhitespace );

			afterTo( );
		}

		private void variance( )
		{
			int start = ++i;
			moveToExpected( ')' );
			Segment[ ] parts = line.substring( start , i ).split( "\\s*,\\s*" );
			if( parts.length == 0 )
			{
				throw new SegmentParseExpectedException( line.charAtAsSegment( i ) , WallsExpectedTypes.VARIANCE );
			}
			if( parts.length > 2 )
			{
				throw new SegmentParseExpectedException( parts[ 2 ] , ")" );
			}
			for( int k = 0 ; k < parts.length ; k++ )
			{
				visitor.variance( k , parts[ k ] );
			}
			i++;
			moveTo( nonWhitespace );
			afterVariance( );
		}

		@SuppressWarnings( "unchecked" )
		private static final Function<Segment, Consumer<VectorLineParser>>[ ] AFTER_VARIANCE = new Function[ ] {
			LRUD_START ,
			DIRECTIVE_START ,
			END_OF_LINE_OR_COMMENT_START
		};

		private void afterVariance( )
		{
			line.charAtAsSegment( i ).parseAsAnyOf( AFTER_VARIANCE ).accept( this );
		}

		private void lruds( )
		{
			char endChar = line.charAtAsSegment( i ).parseAsAnyOf( LRUD_END_CHARS );
			int start = ++i;
			moveToExpected( endChar );
			Segment[ ] lruds = line.substring( start , i ).split( "\\s*,\\s*|\\s+" );
			if( lruds.length < 4 )
			{
				throw new SegmentParseExpectedException( line.charAtAsSegment( i ) , WallsExpectedTypes.LRUD_MEASUREMENT );
			}
			if( lruds.length > 6 )
			{
				throw new SegmentParseExpectedException( lruds[ 6 ].charAfter( ) , Character.toString( endChar ) );
			}
			for( int k = 0 ; k < lruds.length ; k++ )
			{
				visitor.lrud( k , lruds[ k ] );
			}
			i++;
			moveTo( nonWhitespace );
			afterLruds( );
		}

		@SuppressWarnings( "unchecked" )
		private static final Function<Segment, Consumer<VectorLineParser>>[ ] AFTER_LRUDS = new Function[ ] {
			DIRECTIVE_START ,
			END_OF_LINE_OR_COMMENT_START
		};

		private void afterLruds( )
		{
			line.charAtAsSegment( i ).parseAsAnyOf( AFTER_LRUDS ).accept( this );
		}

		private void directive( )
		{
			Segment[ ] parts = line.substring( i + 1 ).split( "\\s+" , 2 );
			visitor.directive( parts[ 0 ] , parts.length > 1 ? parts[ 1 ] : null );
			i = line.length( );
		}

		private void end( )
		{
		}
	}

	public static void parseVectorLine( Segment line , int numVectorComponents , VectorLineVisitor visitor )
	{
		new VectorLineParser( line , numVectorComponents , visitor );
	}

	public static void parseVectorLine2( Segment line , VectorLineVisitor2 visitor )
	{
		new VectorLineParser2( line , visitor ).parse( );
	}

	private static void temp( String s )
	{
		System.out.println( s );
		Segment segment = new Segment( s , null , 0 , 0 );
		try
		{
			parseVectorLine( segment , 3 , new VectorLineVisitor( ) {
				@Override
				public void variance( int index , Segment item )
				{
					System.out.println( "  variance[" + index + "]: " + item );
				}

				@Override
				public void to( Segment to )
				{
					System.out.println( "  to:           " + to );
				}

				@Override
				public void measurement( int index , Segment measurement )
				{
					System.out.println( "  measurement[" + index + "]: " + measurement );
				}

				@Override
				public void lrud( int index , Segment item )
				{
					System.out.println( "  lrud[" + index + "]:      " + item );
				}

				@Override
				public void from( Segment from )
				{
					System.out.println( "  from:         " + from );
				}

				@Override
				public void directive( Segment directive , Segment argument )
				{
					System.out.println( "  directive:    " + directive );
					System.out.println( "  directiveArg: " + argument );
				}

				@Override
				public void comment( Segment comment )
				{
					System.out.println( "  comment:      " + comment );
				}
			} );
		}
		catch( Exception ex )
		{
			ex.printStackTrace( );
		}
	}

	private static void temp2( String s )
	{
		System.out.println( s );
		Segment segment = new Segment( s , null , 0 , 0 );
		try
		{
			parseVectorLine2( segment , new VectorLineVisitor2( ) {
				WallsUnits units = new WallsUnits( );

				@Override
				public void visitTo( Segment to )
				{
					System.out.println( "  to:           " + to );
				}

				@Override
				public void visitFrom( Segment from )
				{
					System.out.println( "  from:         " + from );
				}

				@Override
				public WallsUnits units( )
				{
					return units;
				}

				@Override
				public void visitDistance( UnitizedDouble<Length> distance )
				{
					System.out.println( "  distance:     " + distance );
				}

				@Override
				public void visitFrontsightAzimuth( UnitizedDouble<Angle> fsAzimuth )
				{
					System.out.println( "  fsAzm:        " + fsAzimuth );
				}

				@Override
				public void visitBacksightAzimuth( UnitizedDouble<Angle> bsAzimuth )
				{
					System.out.println( "  bsAzm:        " + bsAzimuth );
				}

				@Override
				public void visitFrontsightInclination( UnitizedDouble<Angle> fsInclination )
				{
					System.out.println( "  fsInc:        " + fsInclination );
				}

				@Override
				public void visitBacksightInclination( UnitizedDouble<Angle> bsInclination )
				{
					System.out.println( "  bsInc:        " + bsInclination );
				}

				@Override
				public void visitNorth( UnitizedDouble<Length> north )
				{
					System.out.println( "  north:        " + north );
				}

				@Override
				public void visitEast( UnitizedDouble<Length> east )
				{
					System.out.println( "  east:         " + east );
				}

				@Override
				public void visitVectorUp( UnitizedDouble<Length> up )
				{
					System.out.println( "  vUp:          " + up );
				}

				@Override
				public void visitInstrumentHeight( UnitizedDouble<Length> instrumentHeight )
				{
					System.out.println( "  ih:           " + instrumentHeight );
				}

				@Override
				public void visitTargetHeight( UnitizedDouble<Length> targetHeight )
				{
					System.out.println( "  th:           " + targetHeight );
				}

				@Override
				public void visitLeft( UnitizedDouble<Length> left )
				{
					System.out.println( "  left:         " + left );
				}

				@Override
				public void visitRight( UnitizedDouble<Length> right )
				{
					System.out.println( "  right:        " + right );
				}

				@Override
				public void visitUp( UnitizedDouble<Length> up )
				{
					System.out.println( "  up:           " + up );
				}

				@Override
				public void visitDown( UnitizedDouble<Length> down )
				{
					System.out.println( "  down:         " + down );
				}

				@Override
				public void visitLrudFacingAngle( UnitizedDouble<Angle> facingAngle )
				{
					System.out.println( "  facingAngle:  " + facingAngle );
				}

				@Override
				public void visitCFlag( )
				{
					System.out.println( "  cflag" );
				}

				@Override
				public void visitHorizontalVarianceOverride( VarianceOverride variance )
				{
					System.out.println( "  h:            " + variance );
				}

				@Override
				public void visitVerticalVarianceOverride( VarianceOverride variance )
				{
					System.out.println( "  v:            " + variance );
				}

				@Override
				public void visitSegment( Segment segment )
				{
					System.out.println( "  segment:      " + segment );
				}

				@Override
				public void visitComment( Segment comment )
				{
					System.out.println( "  comment:      " + comment );
				}
			} );
		}
		catch( Exception ex )
		{
			ex.printStackTrace( );
		}
	}

	public static void main( String[ ] args )
	{
		temp2( "*A*1 B1 350 41 +25 *2, 3, 4,5,C*;4, 5>" );
		temp2( "*A*1 B1 350 41 +25 *2, 3 4,5,C*;4, 5>" );
		temp2( "*A*1 B1 350 41 +25 *2 3 4 5 C*;4, 5>" );
		temp2( "*A*1 B1 350 41 +25 *2, 3, 4,5,C*#Seg blah;4, 5>" );
		temp2( "*A*1 B1 350 41 +25 (3, 5) *2, 3, 4,5,C*#Seg blah;4, 5>" );
		temp2( "*A*1 *2,3,4,5*" );
		temp2( "*A*1 *2,3,4,*" );
		temp2( "<A1, <bash <2,3,4,5>" );
		temp2( "A1 B1 350 41 25 (3, 5) <2, 3, 4,5> okay>< weird #Seg blah;4, 5>" );
		temp2( "A1 B1 350 41 25 <2, 3, 4,5>#Seg blah;4, 5>" );
		temp2( "A1 B1 350 41 25 (3, 5) <2, 3,4,5 >#Seg blah;4, 5>" );
		temp2( "A1 B1 350 41 25 (3;, 5) <2, 3,4,5 >#Seg blah;4, 5>" );
		temp2( "A1 B1 350 41 25 (3, 5) <2, 3,4,5 *#Seg blah;4, 5>" );
		temp2( "A1 B1 350 41 25 (3, 5) hello <2, 3,4,5 *#Seg blah;4, 5>" );
		temp2( "A1 B1 350 41 25 15 16 (3, 5) <2, 3,4,5 >#Seg blah;4, 5>" );
		temp2( "A1 B1 350 41 25 15 16 17 (3, 5) <2, 3,4,5 >#Seg blah;4, 5>" );
	}
}

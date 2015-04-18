package org.breakout.wallsimport;

import static org.breakout.wallsimport.CardinalDirection.EAST;
import static org.breakout.wallsimport.CardinalDirection.NORTH;
import static org.breakout.wallsimport.CardinalDirection.SOUTH;
import static org.breakout.wallsimport.CardinalDirection.WEST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.andork.collect.MapLiteral;
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
	private static final Pattern unitsOptionPattern = Pattern.compile( "[a-zA-Z_/]*" );

	private static final Pattern macroNamePattern = Pattern.compile( "[^()=;,# \t]*" );

	private static final Pattern fromStationPattern = Pattern.compile( "[^;,# \t]{1,8}" );

	private static final Pattern toStationPattern = Pattern.compile( "[^<*;,# \t][^;,# \t]{0,7}" );

	private static final Pattern prefixPattern = Pattern.compile( "[^:;,# \t]+" );

	private static final Pattern optionalPattern = Pattern.compile( "--+" );

	private static final Map<String, Unit<Length>> lengthUnits = new MapLiteral<String, Unit<Length>>( )
		.map( "meters" , Length.meters )
		.map( "m" , Length.meters )
		.map( "feet" , Length.feet )
		.map( "f" , Length.feet );

	private static final Map<String, Unit<Angle>> azmUnits = new MapLiteral<String, Unit<Angle>>( )
		.map( "degrees" , Angle.degrees )
		.map( "d" , Angle.degrees )
		.map( "mils" , Angle.milsNATO )
		.map( "m" , Angle.milsNATO )
		.map( "grads" , Angle.gradians )
		.map( "g" , Angle.gradians );

	private static final Map<String, Unit<Angle>> incUnits = new MapLiteral<String, Unit<Angle>>( )
		.map( "degrees" , Angle.degrees )
		.map( "d" , Angle.degrees )
		.map( "mils" , Angle.milsNATO )
		.map( "m" , Angle.milsNATO )
		.map( "grads" , Angle.gradians )
		.map( "g" , Angle.gradians )
		.map( "percent" , Angle.percentGrade )
		.map( "p" , Angle.percentGrade );

	private static final Map<Character, Unit<Length>> lengthUnitSuffixes = new MapLiteral<Character, Unit<Length>>( )
		.map( 'm' , Length.meters )
		.map( 'f' , Length.feet );

	private static final Map<Character, Unit<Angle>> azmUnitSuffixes = new MapLiteral<Character, Unit<Angle>>( )
		.map( 'd' , Angle.degrees )
		.map( 'g' , Angle.gradians )
		.map( 'm' , Angle.milsNATO );

	private static final Map<Unit<Angle>, Double> azmMaxes = new MapLiteral<Unit<Angle>, Double>( )
		.map( Angle.degrees , 360.0 )
		.map( Angle.gradians , 400.0 )
		.map( Angle.milsNATO , 6400.0 );

	private static final Map<Unit<Angle>, Double> incMaxes = new MapLiteral<Unit<Angle>, Double>( )
		.map( Angle.degrees , 90.0 )
		.map( Angle.gradians , 100.0 )
		.map( Angle.milsNATO , 1600.0 )
		.map( Angle.percentGrade ,
			Double.POSITIVE_INFINITY );

	private static final Map<Character, Unit<Angle>> incUnitSuffixes = new MapLiteral<Character, Unit<Angle>>( )
		.map( 'd' , Angle.degrees )
		.map( 'g' , Angle.gradians )
		.map( 'm' , Angle.milsNATO )
		.map( 'p' , Angle.percentGrade );

	private static final Map<Character, CardinalDirection> cardinalDirections = new MapLiteral<Character, CardinalDirection>( )
		.map( 'n' , NORTH )
		.map( 's' , SOUTH )
		.map( 'e' , EAST )
		.map( 'w' , WEST );

	private static final Map<Character, CardinalDirection> eastWest = new MapLiteral<Character, CardinalDirection>( )
		.map( 'e' , EAST )
		.map( 'w' , WEST );

	private static final Map<Character, CardinalDirection> northSouth = new MapLiteral<Character, CardinalDirection>( )
		.map( 'n' , NORTH )
		.map( 's' , SOUTH );

	private static final Map<CardinalDirection, Map<Character, CardinalDirection>> toCardinalDirections =
		new MapLiteral<CardinalDirection, Map<Character, CardinalDirection>>( )
			.map( CardinalDirection.NORTH , eastWest )
			.map( CardinalDirection.SOUTH , eastWest )
			.map( CardinalDirection.EAST , northSouth )
			.map( CardinalDirection.WEST , northSouth );

	private static final Map<Character, VectorElement> davElements = new MapLiteral<Character, VectorElement>( )
		.map( 'd' , VectorElement.D )
		.map( 'a' , VectorElement.A )
		.map( 'v' , VectorElement.V );

	private static final Map<Character, VectorElement> neuElements = new MapLiteral<Character, VectorElement>( )
		.map( 'n' , VectorElement.N )
		.map( 'e' , VectorElement.E )
		.map( 'u' , VectorElement.U );

	private static final Map<String, Boolean> correctedValues = new MapLiteral<String, Boolean>( )
		.map( "corrected" , true )
		.map( "c" , true )
		.map( "normal" , false )
		.map( "n" , false );

	private static final Map<String, CaseType> caseTypes = new MapLiteral<String, CaseType>( )
		.map( "upper" , CaseType.Upper )
		.map( "u" , CaseType.Upper )
		.map( "lower" , CaseType.Lower )
		.map( "l" , CaseType.Lower )
		.map( "mixed" , CaseType.Mixed )
		.map( "m" , CaseType.Mixed );

	private static final Map<String, LrudType> lrudTypes = new MapLiteral<String, LrudType>( )
		.map( "from" , LrudType.From )
		.map( "fb" , LrudType.FB )
		.map( "f" , LrudType.From )
		.map( "to" , LrudType.To )
		.map( "tb" , LrudType.TB )
		.map( "t" , LrudType.To );

	private static final Map<Character, LrudElement> lrudElements = new MapLiteral<Character, LrudElement>( )
		.map( 'l' , LrudElement.L )
		.map( 'r' , LrudElement.R )
		.map( 'u' , LrudElement.U )
		.map( 'd' , LrudElement.D );

	private static final Map<String, List<TapingMethodElement>> tapingMethods = new MapLiteral<String, List<TapingMethodElement>>( )
		.map( "IT" , Collections.unmodifiableList( Arrays.asList( TapingMethodElement.INSTRUMENT_HEIGHT , TapingMethodElement.TARGET_HEIGHT ) ) )
		.map( "IS" , Collections.unmodifiableList( Arrays.asList( TapingMethodElement.INSTRUMENT_HEIGHT ) ) )
		.map( "ST" , Collections.unmodifiableList( Arrays.asList( TapingMethodElement.TARGET_HEIGHT ) ) )
		.map( "SS" , Collections.emptyList( ) );

	private static final Map<Character, Character> quotedChars = new MapLiteral<Character, Character>( )
		.map( 'r' , '\r' )
		.map( 'n' , '\n' )
		.map( 'f' , '\f' )
		.map( 't' , '\t' )
		.map( '"' , '"' )
		.map( '\\' , '\\' );

	private static final Map<String, UnitsOption> unitsOptions = new MapLiteral<String, UnitsOption>( )
		.map( "save" , UnitsLineParser::save )
		.map( "reset" , UnitsLineParser::reset )
		.map( "restore" , UnitsLineParser::restore )
		.map( "m" , UnitsLineParser::meters )
		.map( "meters" , UnitsLineParser::meters )
		.map( "f" , UnitsLineParser::feet )
		.map( "feet" , UnitsLineParser::feet )
		.map( "d" , UnitsLineParser::d )
		.map( "s" , UnitsLineParser::s )
		.map( "a" , UnitsLineParser::a )
		.map( "ab" , UnitsLineParser::ab )
		.map( "a/ab" , UnitsLineParser::a_ab )
		.map( "v" , UnitsLineParser::v )
		.map( "vb" , UnitsLineParser::vb )
		.map( "v/vb" , UnitsLineParser::v_vb )
		.map( "o" , UnitsLineParser::order )
		.map( "order" , UnitsLineParser::order )
		.map( "decl" , UnitsLineParser::decl )
		.map( "grid" , UnitsLineParser::grid )
		.map( "rect" , UnitsLineParser::rect )
		.map( "incd" , UnitsLineParser::incd )
		.map( "inch" , UnitsLineParser::inch )
		.map( "incs" , UnitsLineParser::incs )
		.map( "inca" , UnitsLineParser::inca )
		.map( "incab" , UnitsLineParser::incab )
		.map( "incv" , UnitsLineParser::incv )
		.map( "incvb" , UnitsLineParser::incvb )
		.map( "typeab" , UnitsLineParser::typeab )
		.map( "typevb" , UnitsLineParser::typevb )
		.map( "case" , UnitsLineParser::case_ )
		.map( "lrud" , UnitsLineParser::lrud )
		.map( "tape" , UnitsLineParser::tape )
		.map( "prefix" , UnitsLineParser::prefix1 )
		.map( "prefix1" , UnitsLineParser::prefix1 )
		.map( "prefix2" , UnitsLineParser::prefix2 )
		.map( "prefix3" , UnitsLineParser::prefix2 )
		.map( "uvh" , UnitsLineParser::uvh )
		.map( "uvv" , UnitsLineParser::uvv )
		.map( "uv" , UnitsLineParser::uv )
		.map( "flag" , UnitsLineParser::flag );

	Stack<WallsUnits> stack = new Stack<>( );

	WallsUnits units = new WallsUnits( );

	Map<String, String> macros = new HashMap<>( );

	public static class WallsLineParser extends LineParser
	{
		public WallsLineParser( Segment line )
		{
			super( line );
		}

		public UnitizedDouble<Length> length( Unit<Length> defaultUnit )
		{
			return new UnitizedDouble<>( doubleLiteral( ) ,
				oneOfLowercase( lengthUnitSuffixes , defaultUnit ) );
		}

		public UnitizedDouble<Length> unsignedLength( Unit<Length> defaultUnit )
		{
			return new UnitizedDouble<>( unsignedDoubleLiteral( ) ,
				oneOfLowercase( lengthUnitSuffixes , defaultUnit ) );
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
				oneOfLowercase( azmUnitSuffixes , defaultUnit ) );

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
			CardinalDirection from = oneOfLowercase( cardinalDirections );

			int start = i;
			UnitizedDouble<Angle> angle = maybeR( ( ) -> nonQuadrantAzimuth( defaultUnit ) );
			if( angle != null )
			{
				int end = i;

				if( angle.doubleValue( angle.unit ) > incMaxes.get( angle.unit ) )
				{
					throw new SegmentParseException( line.substring( start , end ) , WallsParseError.AZM_OUT_OF_RANGE );
				}

				CardinalDirection to = oneOfLowercase( toCardinalDirections.get( from ) );

				return from.toward( to , angle );
			}
			return from.angle;
		}

		public UnitizedDouble<Angle> azimuth( Unit<Angle> defaultUnit )
		{
			return oneOfR(
				( ) -> quadrantAzimuth( defaultUnit ) ,
				( ) -> nonQuadrantAzimuth( defaultUnit ) );
		}

		public UnitizedDouble<Angle> azimuthOffset( Unit<Angle> defaultUnit )
		{
			Double signum = maybeR( ( ) -> oneOf( SIGN_SIGNUMS ) );
			if( signum == null )
			{
				signum = 1.0;
			}
			UnitizedDouble<Angle> angle = nonQuadrantAzimuth( defaultUnit );

			return signum < 0 ? angle.negate( ) : angle;
		}

		public UnitizedDouble<Angle> unsignedInclination( Unit<Angle> defaultUnit )
		{
			int start = i;
			UnitizedDouble<Angle> result = new UnitizedDouble<>(
				unsignedDoubleLiteral( ) , oneOfLowercase( incUnitSuffixes , defaultUnit ) );
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

		public VarianceOverride varianceOverride( Unit<Length> defaultUnit )
		{
			return this.oneOfR(
				this::floatedVectorVarianceOverride ,
				this::floatedTraverseVarianceOverride ,
				( ) -> lengthVarianceOverride( defaultUnit ) ,
				( ) -> rmsErrorVarianceOverride( defaultUnit ) ,
				( ) -> ( VarianceOverride ) null );
		}

		private VarianceOverride floatedVectorVarianceOverride( )
		{
			expect( '?' );
			return VarianceOverride.FLOATED;
		}

		private VarianceOverride floatedTraverseVarianceOverride( )
		{
			expect( '*' );
			return VarianceOverride.FLOATED_TRAVERSE;
		}

		private VarianceOverride lengthVarianceOverride( Unit<Length> defaultUnit )
		{
			return new VarianceOverride.LengthOverride( unsignedLength( defaultUnit ) );
		}

		private VarianceOverride rmsErrorVarianceOverride( Unit<Length> defaultUnit )
		{
			expectIgnoreCase( 'r' );
			return new VarianceOverride.RMSError( unsignedLength( defaultUnit ) );
		}

		public String quotedText( )
		{
			expect( '"' );
			StringBuilder sb = new StringBuilder( );
			while( maybe( ( ) -> sb.append( quotedChar( ) ) ) )
				;
			expect( '"' );
			return sb.toString( );
		}

		public char quotedChar( )
		{
			char c = expect( ch -> ch != '"' , ExpectedTypes.NOT_QUOTE );
			return c == '\\' ? oneOf( quotedChars ) : c;
		}

		public <R> R optional( Supplier<R> production )
		{
			return oneOfR(
				( ) ->
				{
					expect( optionalPattern );
					return null;
				} , production );
		}
	}

	@FunctionalInterface
	private static interface UnitsOption
	{
		public void process( UnitsLineParser parser , Segment optionName );
	}

	private class UnitsLineParser extends WallsLineParser
	{
		public UnitsLineParser( Segment line )
		{
			super( line );
		}

		public void parse( )
		{
			throwAllExpected( this::unitsLine );
		}

		public void unitsLine( )
		{
			maybe( this::whitespace );
			oneOf(
				( ) -> expectIgnoreCase( "#units" ) ,
				( ) -> expectIgnoreCase( "#u" ) );

			unitsOptions( );
		}

		public void unitsOptions( )
		{
			while( !maybe( ( ) -> oneOf( this::endOfLine , this::comment ) ) )
			{
				whitespace( );
				maybe( ( ) -> oneOf( this::unitsOption , this::macroOption ) );
			}
		}

		public void unitsOption( )
		{
			Segment optionName = expect( unitsOptionPattern , WallsExpectedTypes.UNITS_OPTION );
			optionName.parseToLowerCaseAsAnyOf( unitsOptions ).process( this , optionName );
		}

		public void macroOption( )
		{
			expect( '$' );
			String macroName = expect( macroNamePattern , WallsExpectedTypes.MACRO_NAME ).toString( );
			String macroValue = null;
			if( maybe( ( ) -> expect( '=' ) ) )
			{
				macroValue = oneOfR( this::quotedText , this::nonwhitespace ).toString( );
			}
			macros.put( macroName , macroValue );
		}

		public void save( Segment optionName )
		{
			if( stack.size( ) > 10 )
			{
				throw new SegmentParseException( optionName , WallsParseError.STACK_FULL );
			}
			stack.push( units.clone( ) );
		}

		public void restore( Segment optionName )
		{
			if( stack.isEmpty( ) )
			{
				throw new SegmentParseException( optionName , WallsParseError.STACK_EMPTY );
			}
			units = stack.pop( );
		}

		public void reset( Segment optionName )
		{
			units = new WallsUnits( );
		}

		public void meters( Segment optionName )
		{
			units.d_unit = units.s_unit = Length.meters;
		}

		public void feet( Segment optionName )
		{
			units.d_unit = units.s_unit = Length.feet;
		}

		public void d( Segment optionName )
		{
			expect( '=' );
			units.d_unit = oneOfIgnoreCase( lengthUnits.entrySet( ) );
		}

		public void s( Segment optionName )
		{
			expect( '=' );
			units.s_unit = oneOfIgnoreCase( lengthUnits.entrySet( ) );
		}

		public void a( Segment optionName )
		{
			expect( '=' );
			units.a_unit = oneOfIgnoreCase( azmUnits.entrySet( ) );
		}

		public void ab( Segment optionName )
		{
			expect( '=' );
			units.ab_unit = oneOfIgnoreCase( azmUnits.entrySet( ) );
		}

		public void a_ab( Segment optionName )
		{
			expect( '=' );
			units.a_unit = units.ab_unit = oneOfIgnoreCase( azmUnits.entrySet( ) );
		}

		public void v( Segment optionName )
		{
			expect( '=' );
			units.v_unit = oneOfIgnoreCase( incUnits.entrySet( ) );
		}

		public void vb( Segment optionName )
		{
			expect( '=' );
			units.vb_unit = oneOfIgnoreCase( incUnits.entrySet( ) );
		}

		public void v_vb( Segment optionName )
		{
			expect( '=' );
			units.v_unit = units.vb_unit = oneOfIgnoreCase( incUnits.entrySet( ) );
		}

		public void order( Segment optionName )
		{
			expect( '=' );
			oneOf(
				( ) -> order( davElements , VectorElement.V ) ,
				( ) -> order( neuElements , VectorElement.U ) );
		}

		private void order( Map<Character, VectorElement> elements , VectorElement vertical )
		{
			ArrayList<VectorElement> newOrder = new ArrayList<VectorElement>( );
			orderElement( newOrder , elements );
			int second = i;
			orderElement( newOrder , elements );
			if( !maybe( ( ) -> orderElement( newOrder , elements ) ) )
			{
				if( newOrder.contains( vertical ) )
				{
					throw new SegmentParseExpectedException( line.charAtAsSegment( second ) , otherExpectedOrderElements( newOrder , elements ) );
				}
			}
			newOrder.trimToSize( );
			units.order = Collections.unmodifiableList( newOrder );
		}

		private void orderElement( List<VectorElement> newOrder , Map<Character, VectorElement> elements )
		{
			int start = i;
			VectorElement elem = oneOfLowercase( elements );
			if( newOrder.contains( elem ) )
			{
				throw new SegmentParseExpectedException( line.charAtAsSegment( start ) , otherExpectedOrderElements( newOrder , elements ) );
			}
			newOrder.add( elem );
		}

		private Object[ ] otherExpectedOrderElements( List<VectorElement> newOrder , Map<Character, VectorElement> elements )
		{
			List<Object> result = new LinkedList<Object>( );
			for( Map.Entry<Character, VectorElement> entry : elements.entrySet( ) )
			{
				if( !newOrder.contains( entry.getValue( ) ) )
				{
					result.add( entry.getKey( ) );
				}
			}
			return result.toArray( );
		}

		public void decl( Segment optionName )
		{
			expect( '=' );
			units.decl = azimuthOffset( units.a_unit );
		}

		public void grid( Segment optionName )
		{
			expect( '=' );
			units.grid = azimuthOffset( units.a_unit );
		}

		public void rect( Segment optionName )
		{
			expect( '=' );
			units.rect = azimuthOffset( units.a_unit );
		}

		public void incd( Segment optionName )
		{
			expect( '=' );
			units.incd = length( units.d_unit );
		}

		public void inch( Segment optionName )
		{
			expect( '=' );
			units.inch = length( units.s_unit );
		}

		public void incs( Segment optionName )
		{
			expect( '=' );
			units.incs = length( units.s_unit );
		}

		public void inca( Segment optionName )
		{
			expect( '=' );
			units.inca = azimuthOffset( units.a_unit );
		}

		public void incab( Segment optionName )
		{
			expect( '=' );
			units.incab = azimuthOffset( units.ab_unit );
		}

		public void incv( Segment optionName )
		{
			expect( '=' );
			units.incv = inclination( units.v_unit );
		}

		public void incvb( Segment optionName )
		{
			expect( '=' );
			units.incvb = inclination( units.vb_unit );
		}

		public void typeab( Segment optionName )
		{
			expect( '=' );
			units.typeab_corrected = oneOfIgnoreCase( correctedValues.entrySet( ) );
			units.typeab_noAverage = false;
			units.typeab_tolerance = null;
			if( maybe( this::comma ) )
			{
				units.typeab_tolerance = unsignedDoubleLiteral( );
				if( maybe( this::comma ) )
				{
					expectIgnoreCase( 'x' );
					units.typeab_noAverage = true;
				}
			}
		}

		public void typevb( Segment optionName )
		{
			expect( '=' );
			units.typevb_corrected = oneOfIgnoreCase( correctedValues.entrySet( ) );
			units.typevb_noAverage = false;
			units.typevb_tolerance = null;
			if( maybe( this::comma ) )
			{
				units.typevb_tolerance = unsignedDoubleLiteral( );
				if( maybe( this::comma ) )
				{
					expectIgnoreCase( 'x' );
					units.typevb_noAverage = true;
				}
			}
		}

		public void case_( Segment optionName )
		{
			expect( '=' );
			units.case_ = oneOfIgnoreCase( caseTypes.entrySet( ) );
		}

		public void lrud( Segment optionName )
		{
			expect( '=' );
			units.lrud = oneOfIgnoreCase( lrudTypes.entrySet( ) );
			if( maybe( this::colon ) )
			{
				lrudElements( );
			}
			else
			{
				units.lrud_order = Arrays.asList( LrudElement.values( ) );
			}
		}

		private void lrudElements( )
		{
			ArrayList<LrudElement> newOrder = new ArrayList<LrudElement>( );
			for( int k = 0 ; k < 4 ; k++ )
			{
				lrudElement( newOrder );
			}
			newOrder.trimToSize( );
			units.lrud_order = Collections.unmodifiableList( newOrder );
		}

		private void lrudElement( List<LrudElement> newOrder )
		{
			int start = i;
			LrudElement elem = oneOfLowercase( lrudElements );
			if( newOrder.contains( elem ) )
			{
				throw new SegmentParseExpectedException( line.charAtAsSegment( start ) , otherExpectedLrudElements( newOrder ) );
			}
			newOrder.add( elem );
		}

		private Object[ ] otherExpectedLrudElements( List<LrudElement> newOrder )
		{
			List<Object> result = new LinkedList<Object>( );
			for( Map.Entry<Character, LrudElement> entry : lrudElements.entrySet( ) )
			{
				if( !newOrder.contains( entry.getValue( ) ) )
				{
					result.add( entry.getKey( ) );
				}
			}
			return result.toArray( );
		}

		public void prefix1( Segment optionName )
		{
			prefix( 0 );
		}

		public void prefix2( Segment optionName )
		{
			prefix( 1 );
		}

		public void prefix3( Segment optionName )
		{
			prefix( 2 );
		}

		public void prefix( int index )
		{
			String prefix = null;

			if( maybe( ( ) -> expect( '=' ) ) )
			{
				prefix = expect( prefixPattern , WallsExpectedTypes.PREFIX ).toString( );
			}
			units.setPrefix( index , prefix );
		}

		public void tape( Segment optionName )
		{
			expect( '=' );
			units.tape = oneOfIgnoreCase( tapingMethods.entrySet( ) );
		}

		public void uvh( Segment optionName )
		{
			expect( '=' );
			units.uvh = unsignedDoubleLiteral( );
		}

		public void uvv( Segment optionName )
		{
			expect( '=' );
			units.uvv = unsignedDoubleLiteral( );
		}

		public void uv( Segment optionName )
		{
			expect( '=' );
			units.uv = unsignedDoubleLiteral( );
		}

		public void flag( Segment optionName )
		{
			String flag = null;
			if( maybe( ( ) -> expect( '=' ) ) )
			{
				flag = oneOfR( this::quotedText , this::nonwhitespace ).toString( );
			}
			units.flag = flag;
		}

		public void comment( )
		{
			semicolon( );
			remaining( );
		}
	}

	public static interface VectorLineVisitor
	{
		public void visitFrom( String from );

		public void visitTo( String to );

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

	private class VectorLineParser extends WallsLineParser implements VectorElementVisitor , TapingMethodElementVisitor , LrudElementVisitor
	{
		private VectorLineVisitor visitor;

		public VectorLineParser( Segment line , VectorLineVisitor visitor )
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
			Segment from = expect( fromStationPattern , WallsExpectedTypes.FROM_STATION );
			visitor.visitFrom( units.processStationName( from.toString( ) ) );
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
			Segment to = expect( toStationPattern , WallsExpectedTypes.TO_STATION );
			visitor.visitTo( units.processStationName( to.toString( ) ) );
		}

		public void afterToStation( )
		{
			int k = 0;
			for( VectorElement elem : units.order )
			{
				if( k++ > 0 )
				{
					whitespace( );
				}
				elem.visit( this );
			}
			for( TapingMethodElement elem : units.tape )
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
			visitor.visitDistance( unsignedLength( units.d_unit ) );
		}

		@Override
		public void visitAzimuth( )
		{
			visitor.visitFrontsightAzimuth( optional( ( ) -> azimuth( units.a_unit ) ) );
			if( maybe( this::forwardSlash ) )
			{
				visitor.visitBacksightAzimuth( optional( ( ) -> azimuth( units.ab_unit ) ) );
			}
		}

		@Override
		public void visitInclination( )
		{
			visitor.visitFrontsightInclination( optional( ( ) -> inclination( units.v_unit ) ) );
			if( maybe( this::forwardSlash ) )
			{
				visitor.visitBacksightInclination( optional( ( ) -> inclination( units.vb_unit ) ) );
			}
		}

		@Override
		public void visitEast( )
		{
			visitor.visitEast( length( units.d_unit ) );
		}

		@Override
		public void visitNorth( )
		{
			visitor.visitNorth( length( units.d_unit ) );
		}

		@Override
		public void visitRectUp( )
		{
			visitor.visitVectorUp( length( units.d_unit ) );
		}

		@Override
		public void visitInstrumentHeight( )
		{
			visitor.visitInstrumentHeight( optional( ( ) -> length( units.s_unit ) ) );
		}

		@Override
		public void visitTargetHeight( )
		{
			visitor.visitTargetHeight( optional( ( ) -> length( units.s_unit ) ) );
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
			VarianceOverride horizontal = varianceOverride( units.d_unit );
			visitor.visitHorizontalVarianceOverride( horizontal );
			maybe( this::whitespace );
			if( maybe( this::comma ) )
			{
				maybe( this::whitespace );
				VarianceOverride vertical = varianceOverride( units.d_unit );
				if( horizontal == null && vertical == null )
				{
					throwAllExpected( );
				}
				visitor.visitVerticalVarianceOverride( vertical );
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
			for( LrudElement elem : units.lrud_order )
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
			for( LrudElement elem : units.lrud_order )
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
			visitor.visitLeft( optional( ( ) -> unsignedLength( units.s_unit ) ) );
		}

		@Override
		public void visitRight( )
		{
			visitor.visitRight( optional( ( ) -> unsignedLength( units.s_unit ) ) );
		}

		@Override
		public void visitUp( )
		{
			visitor.visitUp( optional( ( ) -> unsignedLength( units.s_unit ) ) );
		}

		@Override
		public void visitDown( )
		{
			visitor.visitDown( optional( ( ) -> unsignedLength( units.s_unit ) ) );
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
			visitor.visitLrudFacingAngle( azimuth( units.a_unit ) );
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
			// currently this is the only directive that can be on a vector line
			segmentDirective( );
		}

		public void segmentDirective( )
		{
			oneOf( ( ) -> expectIgnoreCase( "segment" ) ,
				( ) -> expectIgnoreCase( "seg" ) ,
				( ) -> expectIgnoreCase( 's' ) );
			whitespace( );
			visitor.visitSegment( oneOrMore( c -> c != ';' , WallsExpectedTypes.SEGMENT ) );
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

	public void parseUnitsLine( Segment line )
	{
		new UnitsLineParser( line ).parse( );
	}

	public void parseVectorLine( Segment line , VectorLineVisitor visitor )
	{
		new VectorLineParser( line , visitor ).parse( );
	}

	private static void temp( WallsParser parser , String s )
	{
		System.out.println( s );
		Segment segment = new Segment( s , null , 0 , 0 );
		try
		{
			parser.parseVectorLine( segment , new VectorLineVisitor( ) {
				@Override
				public void visitTo( String to )
				{
					System.out.println( "  to:           " + to );
				}

				@Override
				public void visitFrom( String from )
				{
					System.out.println( "  from:         " + from );
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
		WallsParser parser = new WallsParser( );

		temp( parser , "*A*1 B1 350 41 +25 *2, 3, 4,5,C*#Seg /some/really/cool segment;4, 5>" );
		temp( parser , "*A*1 B1 350 41 +25 *2, 3, 4,5,C*#Q /some/really/cool segment;4, 5>" );
		temp( parser , "*A*1 B1 350 41 +25 *2, 3 4,5,C*;4, 5>" );
		temp( parser , "*A*1 B1 350 41 +25 *2 3 4 5 C*;4, 5>" );
		temp( parser , "*A*1 B1 350 41 +25 *2, 3, 4,5,C*#Seg blah;4, 5>" );
		temp( parser , "*A*1 B1 350 41:20 +25 (?,) *2, 3, 4,5,C*#Seg blah;4, 5>" );
		temp( parser , "*A*1 *2,3,4,5*" );
		temp( parser , "*A*1 B1 350 N41gW +25 (*) *2, 3, 4,5,C*#Seg blah;4, 5>" );
		temp( parser , "*A*1 *2,3,4,5*" );
		temp( parser , "*A*1 B1 350 N41gW +25 (*) *2, 3, 4m,3f,50g,C*#Seg blah;4, 5>" );
		temp( parser , "*A*1 *2,3,4,5*" );
		temp( parser , "*A*1 B1 350 N200gW +25 (*) *2, 3, 4,5,C*#Seg blah;4, 5>" );
		temp( parser , "*A*1 *2,3,4,5*" );
		temp( parser , "*A*1 B1 350 N200mS +25 (*) *2, 3, 4,5,C*#Seg blah;4, 5>" );
		temp( parser , "*A*1 *2,3,4,5*" );
		temp( parser , "*A*1 *2,3,4,*" );
		temp( parser , "<A1, <bash <2,3,4,5>" );
		temp( parser , "<A1 <bash <2,3,4,5>" );
		temp( parser , "<A1 b<ash <2,3,4,5>" );
		temp( parser , "A1 B1 350 41 +25 (3, 5) <2, 3, 4,5> okay>< weird #Seg blah;4, 5>" );
		temp( parser , "A1 B1 350 41 +25 <2, 3, 4,5>#Seg blah;4, 5>" );
		temp( parser , "A1 B1 350 41 +25 (3, 5) <2, 3,4,5 >#Seg blah;4, 5>" );
		temp( parser , "A1 B1 350 41 +25 (3;, 5) <2, 3,4,5 >#Seg blah;4, 5>" );
		temp( parser , "A1 B1 350 41 +25 (3, 5) <2, 3,4,5 *#Seg blah;4, 5>" );
		temp( parser , "A1 B1 350 41 +25 (3, 5) hello <2, 3,4,5 *#Seg blah;4, 5>" );
		parser.units.tape = Arrays.asList( TapingMethodElement.values( ) );
		temp( parser , "A1 B1 350 41 +25 15 16 (3, 5) <2, 3,4,5 >#Seg blah;4, 5>" );
		temp( parser , "A1 B1 350 41 +25 15 16 17 (3, 5) <2, 3,4,5 >#Seg blah;4, 5>" );

		parser.units.prefix.add( "A" );
		parser.units.prefix.add( "CR" );
		parser.units.case_ = CaseType.Lower;

		temp( parser , "A1 B:B1 350 41 +25 15 16 (3, 5) <2, 3,4,5 >#Seg blah;4, 5>" );
		temp( parser , "FR::A1 B:B1 350 41 +25 15 16 (3, 5) <2, 3,4,5 >#Seg blah;4, 5>" );
	}
}

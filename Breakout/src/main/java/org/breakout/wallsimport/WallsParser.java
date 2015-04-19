package org.breakout.wallsimport;

import static org.breakout.wallsimport.CardinalDirection.EAST;
import static org.breakout.wallsimport.CardinalDirection.NORTH;
import static org.breakout.wallsimport.CardinalDirection.SOUTH;
import static org.breakout.wallsimport.CardinalDirection.WEST;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
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
	private static final Pattern unitsOptionPattern = Pattern.compile( "[a-zA-Z_0-9/]*" );

	private static final Pattern macroNamePattern = Pattern.compile( "[^()=;,# \t]*" );

	private static final Pattern stationPattern = Pattern.compile( "[^<*;,#/ \t][^;,#/ \t]{0,7}" );

	private static final Pattern prefixPattern = Pattern.compile( "[^:;,#/ \t]+" );

	private static final Pattern optionalPattern = Pattern.compile( "--+" );

	private static final Pattern isoDatePattern = Pattern.compile( "\\d{4}-\\d{2}-\\d{2}" );
	private static final Pattern usDatePattern1 = Pattern.compile( "\\d{2}-\\d{2}-\\d{2,4}" );
	private static final Pattern usDatePattern2 = Pattern.compile( "\\d{2}/\\d{2}/\\d{2,4}" );

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

	private static final Map<Character, CtElement> ctElements = new MapLiteral<Character, CtElement>( )
		.map( 'd' , CtElement.D )
		.map( 'a' , CtElement.A )
		.map( 'v' , CtElement.V );

	private static final Map<Character, RectElement> rectElements = new MapLiteral<Character, RectElement>( )
		.map( 'n' , RectElement.N )
		.map( 'e' , RectElement.E )
		.map( 'u' , RectElement.U );

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

	private static final Map<Character, Character> escapedChars = new MapLiteral<Character, Character>( )
		.map( 'r' , '\r' )
		.map( 'n' , '\n' )
		.map( 'f' , '\f' )
		.map( 't' , '\t' )
		.map( '"' , '"' )
		.map( '\\' , '\\' );

	private static final Map<String, Integer> prefixDirectives = new MapLiteral<String, Integer>( )
		.map( "#prefix1" , 0 )
		.map( "#prefix2" , 1 )
		.map( "#prefix3" , 2 )
		.map( "#prefix" , 0 );

	private static final Map<String, Directive> directives = new MapLiteral<String, Directive>( )
		.map( "#units" , WallsLineParser::unitsLine )
		.map( "#u" , WallsLineParser::unitsLine )
		.map( "#flag" , WallsLineParser::flagLine )
		.map( "#f" , WallsLineParser::flagLine )
		.map( "#fix" , WallsLineParser::fixLine )
		.map( "#note" , WallsLineParser::noteLine )
		.map( "#symbol" , WallsLineParser::symbolLine )
		.map( "#sym" , WallsLineParser::symbolLine )
		.map( "#segment" , WallsLineParser::segmentLine )
		.map( "#seg" , WallsLineParser::segmentLine )
		.map( "#s" , WallsLineParser::segmentLine )
		.map( "#date" , WallsLineParser::dateLine )
		.map( "#[" , WallsLineParser::beginBlockCommentLine )
		.map( "#]" , WallsLineParser::endBlockCommentLine )
		.map( "#prefix1" , WallsLineParser::prefixLine )
		.map( "#prefix2" , WallsLineParser::prefixLine )
		.map( "#prefix3" , WallsLineParser::prefixLine )
		.map( "#prefix" , WallsLineParser::prefixLine );

	private static final Map<String, UnitsOption> unitsOptions = new MapLiteral<String, UnitsOption>( )
		.map( "save" , WallsLineParser::save )
		.map( "reset" , WallsLineParser::reset )
		.map( "restore" , WallsLineParser::restore )
		.map( "m" , WallsLineParser::meters )
		.map( "meters" , WallsLineParser::meters )
		.map( "f" , WallsLineParser::feet )
		.map( "feet" , WallsLineParser::feet )
		.map( "ct" , WallsLineParser::ct )
		.map( "d" , WallsLineParser::d )
		.map( "s" , WallsLineParser::s )
		.map( "a" , WallsLineParser::a )
		.map( "ab" , WallsLineParser::ab )
		.map( "a/ab" , WallsLineParser::a_ab )
		.map( "v" , WallsLineParser::v )
		.map( "vb" , WallsLineParser::vb )
		.map( "v/vb" , WallsLineParser::v_vb )
		.map( "o" , WallsLineParser::order )
		.map( "order" , WallsLineParser::order )
		.map( "decl" , WallsLineParser::decl )
		.map( "grid" , WallsLineParser::grid )
		.map( "rect" , WallsLineParser::rect )
		.map( "incd" , WallsLineParser::incd )
		.map( "inch" , WallsLineParser::inch )
		.map( "incs" , WallsLineParser::incs )
		.map( "inca" , WallsLineParser::inca )
		.map( "incab" , WallsLineParser::incab )
		.map( "incv" , WallsLineParser::incv )
		.map( "incvb" , WallsLineParser::incvb )
		.map( "typeab" , WallsLineParser::typeab )
		.map( "typevb" , WallsLineParser::typevb )
		.map( "case" , WallsLineParser::case_ )
		.map( "lrud" , WallsLineParser::lrud )
		.map( "tape" , WallsLineParser::tape )
		.map( "prefix" , WallsLineParser::prefix1 )
		.map( "prefix1" , WallsLineParser::prefix1 )
		.map( "prefix2" , WallsLineParser::prefix2 )
		.map( "prefix3" , WallsLineParser::prefix3 )
		.map( "uvh" , WallsLineParser::uvh )
		.map( "uvv" , WallsLineParser::uvv )
		.map( "uv" , WallsLineParser::uv )
		.map( "flag" , WallsLineParser::flag );

	private SimpleDateFormat isoDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
	private SimpleDateFormat usDateFormat1Long = new SimpleDateFormat( "MM-dd-yyyy" );
	private SimpleDateFormat usDateFormat1Short = new SimpleDateFormat( "MM-dd-yy" );
	private SimpleDateFormat usDateFormat2Long = new SimpleDateFormat( "MM/dd/yyyy" );
	private SimpleDateFormat usDateFormat2Short = new SimpleDateFormat( "MM/dd/yy" );

	WallsLineVisitor visitor;

	Stack<WallsUnits> stack = new Stack<>( );

	boolean inBlockComment = false;

	WallsUnits units = new WallsUnits( );

	Map<String, String> macros = new HashMap<>( );

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

	@FunctionalInterface
	private static interface UnitsOption
	{
		public void process( WallsLineParser parser , Segment optionName );
	}

	@FunctionalInterface
	private static interface Directive
	{
		public void process( WallsLineParser parser );
	}

	public static interface WallsLineVisitor
	{
		public void beginVectorLine( );

		public void visitFrom( String from );

		public void visitTo( String to );

		public void visitDistance( UnitizedDouble<Length> distance );

		public void visitFrontsightAzimuth( UnitizedDouble<Angle> fsAzimuth );

		public void visitBacksightAzimuth( UnitizedDouble<Angle> bsAzimuth );

		public void visitFrontsightInclination( UnitizedDouble<Angle> fsInclination );

		public void visitBacksightInclination( UnitizedDouble<Angle> bsInclination );

		public void visitNorth( UnitizedDouble<Length> north );

		public void visitLatitude( UnitizedDouble<Angle> latitude );

		public void visitEast( UnitizedDouble<Length> east );

		public void visitLongitude( UnitizedDouble<Angle> longitude );

		public void visitRectUp( UnitizedDouble<Length> up );

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
		public void visitInlineSegment( String segment );

		public void visitInlineNote( String note );

		public void visitComment( String comment );

		public void abortVectorLine( );

		public void endVectorLine( );

		public void visitFlaggedStations( String flag , List<String> stations );

		public void visitBlockCommentLine( String string );

		public void visitNoteLine( String station , String note );

		public void beginFixLine( );

		public void abortFixLine( );

		public void endFixLine( );

		public void visitFixedStation( String string );
	}

	public class WallsLineParser extends LineParser
	{
		private final VectorLineElementVisitor vectorLineElementVisitor = new VectorLineElementVisitor( );

		private final FixLineElementVisitor fixLineElementVisitor = new FixLineElementVisitor( );

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

		public UnitizedDouble<Angle> unsignedAngle( Map<Character, Unit<Angle>> unitSuffixes , Unit<Angle> defaultUnit )
		{
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
				oneOfLowercase( unitSuffixes , defaultUnit ) );
			return result;
		}

		public UnitizedDouble<Angle> unsignedDmsAngle( )
		{
			Double degrees = maybeR( this::unsignedDoubleLiteral );
			colon( );
			Double minutes = maybeR( this::unsignedDoubleLiteral );
			Double seconds = null;
			if( maybe( this::colon ) )
			{
				seconds = maybeR( this::unsignedDoubleLiteral );
			}
			if( degrees == null && minutes == null && seconds == null )
			{
				throwAllExpected( );
			}
			return dms( degrees , minutes , seconds );
		}

		public UnitizedDouble<Angle> latitude( )
		{
			int start = i;
			CardinalDirection side = oneOfLowercase( northSouth );
			UnitizedDouble<Angle> latitude = unsignedDmsAngle( );

			if( latitude.doubleValue( Angle.degrees ) > 90.0 )
			{
				throw new SegmentParseException( line.substring( start , i ) , WallsParseError.LATITUDE_OUT_OF_RANGE );
			}

			if( side == SOUTH )
			{
				latitude = latitude.negate( );
			}
			return latitude;
		}

		public UnitizedDouble<Angle> longitude( )
		{
			int start = i;
			CardinalDirection side = oneOfLowercase( eastWest );
			UnitizedDouble<Angle> longitude = unsignedDmsAngle( );

			if( longitude.doubleValue( Angle.degrees ) > 180.0 )
			{
				throw new SegmentParseException( line.substring( start , i ) , WallsParseError.LONGITUDE_OUT_OF_RANGE );
			}

			if( side == WEST )
			{
				longitude = longitude.negate( );
			}
			return longitude;
		}

		public UnitizedDouble<Angle> nonQuadrantAzimuth( Unit<Angle> defaultUnit )
		{
			int start = i;

			UnitizedDouble<Angle> result = unsignedAngle( azmUnitSuffixes , defaultUnit );

			int end = i;

			if( result.doubleValue( result.unit ) > azmMaxes.get( result.unit ) )
			{
				throw new SegmentParseException( line.substring( start , end ) , WallsParseError.AZM_OUT_OF_RANGE );
			}

			return result;
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
			UnitizedDouble<Angle> result = unsignedAngle( incUnitSuffixes , defaultUnit );
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

		public String quotedTextOrNonwhitespace( )
		{
			return oneOfR( this::quotedText , this::nonwhitespace ).toString( );
		}

		public String quotedText( )
		{
			expect( '"' );
			String result = escapedText( p -> p != '"' , WallsExpectedTypes.QUOTED_TEXT );
			expect( '"' );
			return result;
		}

		public String escapedText( CharPredicate p , Object ... expectedTypes )
		{
			StringBuilder sb = new StringBuilder( );
			while( maybe( ( ) -> sb.append( escapedChar( p , expectedTypes ) ) ) )
				;
			return sb.toString( );
		}

		public char escapedChar( CharPredicate p , Object ... expectedTypes )
		{
			char c = expect( p , expectedTypes );
			return c == '\\' ? oneOf( escapedChars ) : c;
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

		public void parse( )
		{
			// do some manual lookahead to improve efficiency
			// reduce confusion about which top-level production
			// is called during debugging.

			maybe( this::whitespace );
			Segment seg = maybeR( this::nonwhitespace );

			if( seg == null )
			{
				return;
			}

			i = 0;

			if( inBlockComment )
			{
				throwAllExpected( ( ) -> oneOfWithLookahead(
					this::endBlockCommentLine ,
					this::insideBlockCommentLine ) );
			}
			else if( seg.startsWith( ";" ) )
			{
				comment( );
			}
			else
			{
				Directive directive = maybeR( ( ) -> seg.parseToLowerCaseAsAnyOf( directives ) );

				if( directive != null )
				{
					replaceMacros( );
					throwAllExpected( ( ) -> directive.process( this ) );
				}
				else
				{
					throwAllExpected( this::vectorLine );
				}
			}
		}

		public void replaceMacros( )
		{
			StringBuilder sb = new StringBuilder( );

			boolean replaced = false;

			while( i < line.length( ) )
			{
				char c = line.charAt( i );
				switch( c )
				{
				case '"':
					sb.append( movePastEndQuote( ) );
					break;
				case '$':
					if( i + 1 < line.length( ) && line.charAt( i + 1 ) == '(' )
					{
						replaced = true;
						sb.append( replaceMacro( ) );
						break;
					}
				default:
					sb.append( c );
					i++;
					break;
				}
			}

			i = 0;

			if( replaced )
			{
				line = new Segment( sb.toString( ) , line.source , line.startLine , line.startCol );
			}
		}

		private String movePastEndQuote( )
		{
			int start = i;
			while( i < line.length( ) )
			{
				char c = line.charAt( i++ );
				if( c == '\\' )
				{
					i++;
				}
				else if( c == '"' )
				{
					break;
				}
			}

			return line.substring( start , i ).toString( );
		}

		private String replaceMacro( )
		{
			i += 2;
			int start = i;
			while( i < line.length( ) )
			{
				char c = line.charAt( i++ );
				if( c == ')' )
				{
					Segment macroName = line.substring( start , i - 1 );
					if( !macros.containsKey( macroName ) )
					{
						throw new SegmentParseException( macroName , WallsParseError.MACRO_NOT_DEFINED );
					}
					String macroValue = macros.get( macroName );
					return macroValue == null ? "" : macroValue;
				}
				else if( Character.isWhitespace( c ) )
				{
					throw new SegmentParseExpectedException( line.charAtAsSegment( i - 1 ) , ExpectedTypes.NON_WHITESPACE );
				}
			}
			throw new SegmentParseExpectedException( line.charAtAsSegment( i ) , ExpectedTypes.NON_WHITESPACE , ')' );
		}

		public void beginBlockCommentLine( )
		{
			maybe( this::whitespace );
			expect( "#[" );
			inBlockComment = true;
			visitor.visitBlockCommentLine( remaining( ).toString( ) );
		}

		public void endBlockCommentLine( )
		{
			maybe( this::whitespace );
			expect( "#]" );
			remaining( );
			inBlockComment = false;
		}

		public void insideBlockCommentLine( )
		{
			visitor.visitBlockCommentLine( remaining( ).toString( ) );
		}

		public String untilComment( Object ... expectedTypes )
		{
			return oneOrMore( c -> c != ';' , expectedTypes ).toString( );
		}

		public void segmentLine( )
		{
			maybe( this::whitespace );
			units.segment = segmentDirective( ).toString( );
			maybe( this::whitespace );
			commentOrEndOfLine( );
		}

		public String segmentDirective( )
		{
			oneOf( ( ) -> expectIgnoreCase( "#segment" ) ,
				( ) -> expectIgnoreCase( "#seg" ) ,
				( ) -> expectIgnoreCase( "#s" ) );

			if( maybe( this::whitespace ) )
			{
				return maybeR( ( ) -> untilComment( WallsExpectedTypes.SEGMENT ) );
			}
			return null;
		}

		public void prefixLine( )
		{
			maybe( this::whitespace );
			prefixDirective( );
			maybe( this::whitespace );
			commentOrEndOfLine( );
		}

		public void prefixDirective( )
		{
			int prefixIndex = oneOfIgnoreCase( prefixDirectives.entrySet( ) );

			String prefix = null;

			if( maybe( this::whitespace ) )
			{
				prefix = maybeR( ( ) -> expect( prefixPattern , WallsExpectedTypes.PREFIX ) ).toString( );
			}

			units.setPrefix( prefixIndex , prefix );
		}

		public void noteLine( )
		{
			maybe( this::whitespace );
			noteDirective( );
			maybe( this::whitespace );
			commentOrEndOfLine( );
		}

		public void noteDirective( )
		{
			oneOf( ( ) -> expectIgnoreCase( "#note" ) ,
				( ) -> expectIgnoreCase( "#n" ) );

			whitespace( );
			String station = expect( stationPattern , WallsExpectedTypes.STATION_NAME ).toString( );
			whitespace( );
			String note = escapedText( p -> p != ';' , WallsExpectedTypes.NOTE );

			visitor.visitNoteLine( station , note );
		}

		public void flagLine( )
		{
			maybe( this::whitespace );
			flagDirective( );
			maybe( this::whitespace );
			commentOrEndOfLine( );
		}

		public void flagDirective( )
		{
			oneOf( ( ) -> expectIgnoreCase( "#flag" ) ,
				( ) -> expectIgnoreCase( "#f" ) );

			List<String> stations = new ArrayList<>( );

			maybe( this::whitespace );

			while( true )
			{
				Segment station = maybeR( ( ) -> expect( stationPattern , WallsExpectedTypes.STATION_NAME ) );
				if( station == null )
				{
					break;
				}
				stations.add( station.toString( ) );

				maybe( this::whitespace );
			}

			String flag = null;

			flag = maybeR( this::slashPrefixedFlag );
			maybe( this::whitespace );

			if( stations.isEmpty( ) )
			{
				units.flag = flag;
			}
			else
			{
				if( flag == null )
				{
					throwAllExpected( );
				}
				visitor.visitFlaggedStations( flag , stations );
			}

			commentOrEndOfLine( );
		}

		public String slashPrefixedFlag( )
		{
			expect( "/" );
			return oneOrMore( c -> c != ';' , WallsExpectedTypes.FLAG ).toString( );
		}

		public void symbolLine( )
		{
			maybe( this::whitespace );

			oneOf( ( ) -> expectIgnoreCase( "#symbol" ) ,
				( ) -> expectIgnoreCase( "#sym" ) );

			// ignore for now
			remaining( );
		}

		public void dateLine( )
		{
			maybe( this::whitespace );
			dateDirective( );
			maybe( this::whitespace );
			commentOrEndOfLine( );
		}

		public void dateDirective( )
		{
			expectIgnoreCase( "#date" );
			whitespace( );
			units.date = oneOfR( this::isoDate , this::usDate1 , this::usDate2 );
		}

		public Date isoDate( )
		{
			Segment dateSegment = expect( isoDatePattern , WallsExpectedTypes.DATE );
			try
			{
				return isoDateFormat.parse( dateSegment.toString( ) );
			}
			catch( ParseException e )
			{
				throw new SegmentParseException( dateSegment , WallsParseError.INVALID_DATE );
			}
		}

		public Date usDate1( )
		{
			Segment dateSegment = expect( usDatePattern1 , WallsExpectedTypes.DATE );
			try
			{
				return ( dateSegment.length( ) > 8 ? usDateFormat1Long : usDateFormat1Short ).parse( dateSegment.toString( ) );
			}
			catch( ParseException e )
			{
				throw new SegmentParseException( dateSegment , WallsParseError.INVALID_DATE );
			}
		}

		public Date usDate2( )
		{
			Segment dateSegment = expect( usDatePattern2 , WallsExpectedTypes.DATE );
			try
			{
				return ( dateSegment.length( ) > 8 ? usDateFormat2Long : usDateFormat2Short ).parse( dateSegment.toString( ) );
			}
			catch( ParseException e )
			{
				throw new SegmentParseException( dateSegment , WallsParseError.INVALID_DATE );
			}
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
				macroValue = quotedTextOrNonwhitespace( );
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

		public void ct( Segment optionName )
		{
			units.vectorType = VectorType.CT;
		}

		public void order( Segment optionName )
		{
			expect( '=' );
			oneOf( this::ctOrder , this::rectOrder );
		}

		private void ctOrder( )
		{
			units.ctOrder = order( ctElements , CtElement.V );
		}

		private void rectOrder( )
		{
			units.rectOrder = order( rectElements , RectElement.U );
		}

		private <T> List<T> order( Map<Character, T> elements , T vertical )
		{
			ArrayList<T> newOrder = new ArrayList<T>( );
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
			return Collections.unmodifiableList( newOrder );
		}

		private <T> void orderElement( List<T> newOrder , Map<Character, T> elements )
		{
			int start = i;
			T elem = oneOfLowercase( elements );
			if( newOrder.contains( elem ) )
			{
				throw new SegmentParseExpectedException( line.charAtAsSegment( start ) , otherExpectedOrderElements( newOrder , elements ) );
			}
			newOrder.add( elem );
		}

		private <T> Object[ ] otherExpectedOrderElements( List<T> newOrder , Map<Character, T> elements )
		{
			List<Object> result = new LinkedList<Object>( );
			for( Map.Entry<Character, T> entry : elements.entrySet( ) )
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
			if( maybe( ( ) -> expect( '=' ) ) )
			{
				units.rect = azimuthOffset( units.a_unit );
			}
			else
			{
				units.vectorType = VectorType.RECT;
			}
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
				flag = quotedTextOrNonwhitespace( );
			}
			units.flag = flag;
		}

		public void vectorLine( )
		{
			maybe( this::whitespace );
			fromStation( );
			try
			{
				whitespace( );
				afterFromStation( );
				visitor.endVectorLine( );
			}
			catch( RuntimeException ex )
			{
				visitor.abortVectorLine( );
				throw ex;
			}
		}

		public void fromStation( )
		{
			Segment from = expect( stationPattern , WallsExpectedTypes.STATION_NAME );
			visitor.beginVectorLine( );
			visitor.visitFrom( from.toString( ) );
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
			Segment to = expect( stationPattern , WallsExpectedTypes.STATION_NAME );
			visitor.visitTo( to.toString( ) );
		}

		public void afterToStation( )
		{
			int k = 0;
			if( units.vectorType == VectorType.RECT )
			{
				for( RectElement elem : units.rectOrder )
				{
					if( k++ > 0 )
					{
						whitespace( );
					}
					elem.visit( vectorLineElementVisitor );
				}
			}
			else
			{
				for( CtElement elem : units.ctOrder )
				{
					if( k++ > 0 )
					{
						whitespace( );
					}
					elem.visit( vectorLineElementVisitor );
				}
			}
			for( TapingMethodElement elem : units.tape )
			{
				whitespace( );
				elem.visit( vectorLineElementVisitor );
			}
			if( maybe( this::whitespace ) )
			{
				afterVectorMeasurements( );
			}
		}

		private class VectorLineElementVisitor implements CtElementVisitor , RectElementVisitor , TapingMethodElementVisitor , LrudElementVisitor
		{
			@Override
			public void visitDistance( )
			{
				visitor.visitDistance( unsignedLength( units.d_unit ) );
			}

			@Override
			public void visitAzimuth( )
			{
				visitor.visitFrontsightAzimuth( optional( ( ) -> azimuth( units.a_unit ) ) );
				if( maybe( WallsLineParser.this::forwardSlash ) )
				{
					visitor.visitBacksightAzimuth( optional( ( ) -> azimuth( units.ab_unit ) ) );
				}
			}

			@Override
			public void visitInclination( )
			{
				visitor.visitFrontsightInclination( optional( ( ) -> inclination( units.v_unit ) ) );
				if( maybe( WallsLineParser.this::forwardSlash ) )
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
				visitor.visitRectUp( length( units.d_unit ) );
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
		}

		public void afterVectorMeasurements( )
		{
			if( maybe( this::varianceOverrides ) )
			{
				maybe( this::whitespace );
			}
			afterVectorVarianceOverrides( );
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

		public void afterVectorVarianceOverrides( )
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
				elem.visit( vectorLineElementVisitor );
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
				elem.visit( vectorLineElementVisitor );
			}
			maybe( this::whitespace );
			afterRequiredWhitespaceDelimLrudMeasurements( );
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
			if( maybe( this::inlineDirective ) )
			{
				maybe( this::whitespace );
			}
			commentOrEndOfLine( );
		}

		public void inlineDirective( )
		{
			// currently this is the only directive that can be on a vector line
			inlineSegmentDirective( );
		}

		public void inlineSegmentDirective( )
		{
			visitor.visitInlineSegment( segmentDirective( ) );
		}

		public void fixLine( )
		{
			maybe( this::whitespace );
			expectIgnoreCase( "#fix" );
			whitespace( );
			fixedStation( );
			try
			{
				whitespace( );
				afterFixedStation( );
				visitor.endFixLine( );
			}
			catch( RuntimeException ex )
			{
				visitor.abortFixLine( );
				throw ex;
			}
		}

		public void fixedStation( )
		{
			Segment fixed = expect( stationPattern , WallsExpectedTypes.STATION_NAME );
			visitor.beginFixLine( );
			visitor.visitFixedStation( fixed.toString( ) );
		}

		public void afterFixedStation( )
		{
			int k = 0;
			for( RectElement elem : units.rectOrder )
			{
				if( k++ > 0 )
				{
					whitespace( );
				}
				elem.visit( fixLineElementVisitor );
			}
			if( maybe( this::whitespace ) )
			{
				afterFixMeasurements( );
			}
		}

		private class FixLineElementVisitor implements RectElementVisitor
		{
			@Override
			public void visitEast( )
			{
				oneOf(
					( ) -> visitor.visitEast( length( units.d_unit ) ) ,
					( ) -> visitor.visitLongitude( longitude( ) ) );
			}

			@Override
			public void visitNorth( )
			{
				oneOf(
					( ) -> visitor.visitNorth( length( units.d_unit ) ) ,
					( ) -> visitor.visitLatitude( latitude( ) ) );
			}

			@Override
			public void visitRectUp( )
			{
				visitor.visitRectUp( length( units.d_unit ) );
			}
		}

		public void afterFixMeasurements( )
		{
			if( maybe( this::varianceOverrides ) )
			{
				maybe( this::whitespace );
			}
			afterFixVarianceOverrides( );
		}

		public void afterFixVarianceOverrides( )
		{
			if( maybe( this::inlineNote ) )
			{
				maybe( this::whitespace );
			}
			afterFixInlineNote( );
		}

		public void inlineNote( )
		{
			expect( '/' );
			visitor.visitInlineNote( escapedText( p -> p != ';' && p != '#' , WallsExpectedTypes.NOTE ) );
		}

		public void afterFixInlineNote( )
		{
			if( maybe( this::inlineDirective ) )
			{
				maybe( this::whitespace );
			}
			commentOrEndOfLine( );
		}

		public void commentOrEndOfLine( )
		{
			oneOf( this::comment , this::endOfLine );
		}

		public void comment( )
		{
			semicolon( );
			visitor.visitComment( remaining( ).toString( ) );
		}
	}

	public void parseLine( Segment line )
	{
		new WallsLineParser( line ).parse( );
	}

	public String processStationName( String name )
	{
		return units.processStationName( name );
	}

	public WallsLineVisitor getVisitor( )
	{
		return visitor;
	}

	public void setVisitor( WallsLineVisitor visitor )
	{
		this.visitor = visitor;
	}

	public class DumpingWallsLineVisitor implements WallsLineVisitor
	{
		public DumpingWallsLineVisitor( )
		{
			super( );
		}

		@Override
		public void visitTo( String to )
		{
			System.out.println( "  to:           " + processStationName( to ) );
		}

		@Override
		public void visitFrom( String from )
		{
			System.out.println( "  from:         " + processStationName( from ) );
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
		public void visitRectUp( UnitizedDouble<Length> up )
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
		public void visitInlineSegment( String segment )
		{
			System.out.println( "  segment:      " + segment );
		}

		@Override
		public void visitComment( String comment )
		{
			System.out.println( "  comment:      " + comment );
		}

		@Override
		public void beginVectorLine( )
		{
			System.out.println( "begin vector line" );
		}

		@Override
		public void endVectorLine( )
		{
			System.out.println( "end vector line" );
		}

		@Override
		public void visitFlaggedStations( String flag , List<String> stations )
		{
			System.out.println( "flag stations: " + stations + " /" + flag );
		}

		@Override
		public void visitBlockCommentLine( String string )
		{
			System.out.println( ";" + string );
		}

		@Override
		public void visitNoteLine( String station , String note )
		{
			System.out.println( "note: " + station + ": " + note );
		}

		@Override
		public void abortVectorLine( )
		{
			System.out.println( "abort vector line" );
		}

		@Override
		public void beginFixLine( )
		{
			System.out.println( "begin fix line" );
		}

		@Override
		public void abortFixLine( )
		{
			System.out.println( "abort fix line" );
		}

		@Override
		public void endFixLine( )
		{
			System.out.println( "end fix line" );
		}

		@Override
		public void visitLatitude( UnitizedDouble<Angle> latitude )
		{
			System.out.println( "  latitude:     " + latitude );
		}

		@Override
		public void visitLongitude( UnitizedDouble<Angle> longitude )
		{
			System.out.println( "  longitude:    " + longitude );
		}

		@Override
		public void visitInlineNote( String note )
		{
			System.out.println( "  note:         " + note );
		}

		@Override
		public void visitFixedStation( String station )
		{
			System.out.println( "  fixed station:" + station );
		}
	}

	private static void temp( WallsParser parser , String s )
	{
		System.out.println( s );
		Segment segment = new Segment( s , null , 0 , 0 );
		try
		{
			parser.parseLine( segment );
		}
		catch( Exception ex )
		{
			ex.printStackTrace( );
		}
	}

	public static void main( String[ ] args )
	{
		WallsParser parser = new WallsParser( );
		parser.setVisitor( parser.new DumpingWallsLineVisitor( ) );

		temp( parser , "*A*1 B1 350 41 +25/-6 *2, --, 4,5,C*#Seg /some/really/cool segment;4, 5>" );
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

		temp( parser , "#u tape=it" );
		temp( parser , "A1 B1 350 41 +25 15 16 (3, 5) <2, 3,4,5 >#Seg blah;4, 5>" );
		temp( parser , "A1 B1 350 41 +25 15 16 17 (3, 5) <2, 3,4,5 >#Seg blah;4, 5>" );

		temp( parser , "   #flag /This is a test" );
		temp( parser , "   #flag AB30 /This is a test" );
		temp( parser , "   #f" );
		temp( parser , "   #f AB30 CY5 /This is a test" );
		//temp( parser , "   #u prefix1=A prefix2=CR case=lower" );
		temp( parser , "   #u prefix1=A case=lower" );
		temp( parser , "  #prefix2 CR    ;test" );

		temp( parser , "   #symbol aslkjb;lkj aslkjasdf a; asdflaksjdf" );
		temp( parser , "   #sym ; asdlkfjasldf" );

		temp( parser , "    \tA1 B:B1 350 41 +25 15 16 (3, 5) <2, 3,4,5 >#Seg blah;4, 5>" );
		temp( parser , " FR::A1 B:B1 350 41 +25 15 16 (3, 5) <2, 3,4,5 >#Seg blah;4, 5>" );
		temp( parser , "   #seg /hello/world  blah;comment" );

		temp( parser , "   #note FR::A1  blah\\n\" hello\\n hello;comment" );

		temp( parser , "#date 2015-01-16" );
		temp( parser , "#date 01-16-2005" );
		temp( parser , "#date 01-16-05" );
		temp( parser , "#date 01/16/05" );
		temp( parser , "#date 01/16/1905" );

		temp( parser , "  #[ this is a test" );
		temp( parser , "  blah" );
		temp( parser , "  blah" );
		temp( parser , "  #segment /hello/world" );
		temp( parser , "  ;#]" );
		temp( parser , "  #segment /hello/world" );
		temp( parser , "  #]" );

		temp( parser , " FR::A1 B:B1 350 41 +25 15 16 (3, 5) <2, 3,4,5 >#Seg blah;4, 5>" );

		temp( parser , " #fox" );
		temp( parser , " #fix GPS9 620765 3461243 123 (R5,?) /Bat Cave Entrance" );
		temp( parser , " #FIX A1 W97:43:52.5 N31:16:45 323f /Entrance #s /hello/world;dms with ft elevations" );

		temp( parser , "#u $hello=\"er=vad order=NUE\"" );
		temp( parser , "#u ord$(hello)" );

		System.out.println( parser.units.ctOrder );
		System.out.println( parser.units.rectOrder );

		temp( parser , "#u ord$(hel lo)" );
		temp( parser , "#u ord$(hello" );
	}
}

package org.breakout.table;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.andork.bind2.Binding;
import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.unit.UnitNameType;
import org.andork.unit.UnitNames;
import org.andork.unit.UnitType;
import org.andork.unit.UnitizedDouble;
import org.andork.util.StringUtils;
import org.breakout.parse.DoubleNumberFormat;
import org.breakout.parse.LineTokenizer;
import org.breakout.parse.UnitizedAngleParser;
import org.breakout.parse.UnitizedLengthParser;
import org.breakout.parse.ValueToken;
import org.breakout.table.LrudXSection.FacingAzimuth;
import org.breakout.table.LrudXSection.XAngle;

/**
 * Provides methods for formatting all types of data in a {@link Shot}. These are combined into one class so that the
 * decimal separator, numbers of fraction digits, and other minutiae can be displayed and parsed consistently.
 * 
 * @author James
 */
public class SurveyDataFormatter
{
	private final NumberFormat			intFormat;
	private final NumberFormat			parseIntFormat;

	private final DecimalFormat			parseDoubleFormat;
	private final DecimalFormat			doubleFormat;
	private final DecimalFormat			lengthFormat;
	private final DecimalFormat			azimuthFormat;
	private final DecimalFormat			inclinationFormat;

	private final UnitizedLengthParser	lengthParser;
	private final UnitizedAngleParser	azimuthParser;
	private final UnitizedAngleParser	inclinationParser;

	private UnitNames					unitNames;
	private UnitNameType				unitNameType			= UnitNameType.LETTER;

	private Localizer					localizer;

	private int							vectorColumn0Width		= 10;
	private int							vectorColumn1Width		= 13;
	private int							vectorColumn2Width		= 13;
	private int							vectorColumn3Width		= 10;
	private int							vectorColumn4Width		= 10;

	private int							xSectionColumnWidth		= 7;

	private static final Pattern		daiShotVectorPattern	= Pattern
																	.compile( "\\s*(\\S+)\\s+([^ \t\n\u000b\f\r/]+)\\s*(/\\s*(\\S+))?\\s+([^ \t\n\u000b\f\r/]+)\\s*(/\\s*(\\S+))?\\s*" );
	private static final Pattern		whitespacePattern		= Pattern.compile( "\\s*" );

	public SurveyDataFormatter( I18n i18n )
	{
		parseIntFormat = createDefaultIntegerFormat( );
		intFormat = createDefaultIntegerFormat( );

		parseDoubleFormat = createDefaultDoubleFormat( );
		doubleFormat = createDefaultDoubleFormat( );
		lengthFormat = createDefaultDoubleFormat( );
		azimuthFormat = createDefaultDoubleFormat( );
		inclinationFormat = createDefaultDoubleFormat( );
		inclinationFormat.setPositivePrefix( "+" );

		lengthParser = new UnitizedLengthParser( );
		lengthParser.allowWhitespace( false );
		lengthParser.defaultUnit( Length.meters );
		lengthParser.numberFormat( new DoubleNumberFormat( lengthFormat ) );
		lengthParser.units( Length.type.units( ) );

		azimuthParser = new UnitizedAngleParser( );
		azimuthParser.allowWhitespace( false );
		azimuthParser.defaultUnit( Angle.degrees );
		azimuthParser.numberFormat( new DoubleNumberFormat( azimuthFormat ) );
		Set<Unit<Angle>> azimuthUnits = new HashSet<Unit<Angle>>( Angle.type.units( ) );
		azimuthUnits.remove( Angle.percentGrade );
		azimuthParser.units( azimuthUnits );

		inclinationParser = new UnitizedAngleParser( );
		inclinationParser.allowWhitespace( false );
		inclinationParser.defaultUnit( Angle.degrees );
		inclinationParser.numberFormat( new DoubleNumberFormat( inclinationFormat ) );
		inclinationParser.units( Angle.type.units( ) );

		localizer = i18n.forClass( SurveyDataFormatter.class );
		i18n.getLocaleBinder( ).addBinding( new Binding( ) {
			@Override
			public void update( boolean force )
			{
				unitNames = UnitNames.getNames( i18n.getLocale( ) );
				lengthParser.unitNames( unitNames );
				azimuthParser.unitNames( unitNames );
				inclinationParser.unitNames( unitNames );
			}
		} );

		setDoubleFractionDigits( 2 );
		setLengthFractionDigits( 2 );
		setAzimuthFractionDigits( 1 );
		setInclinationFractionDigits( 1 );
	}

	private static NumberFormat createDefaultIntegerFormat( )
	{
		NumberFormat result = ( NumberFormat ) NumberFormat.getInstance( );
		result.setMinimumFractionDigits( 0 );
		result.setMaximumFractionDigits( 0 );
		result.setParseIntegerOnly( true );
		result.setGroupingUsed( false );
		return result;
	}

	private static DecimalFormat createDefaultDoubleFormat( )
	{
		DecimalFormat result = ( DecimalFormat ) DecimalFormat.getInstance( );
		result.setGroupingUsed( false );
		return result;
	}

	public void setDefaultLengthUnit( Unit<Length> unit )
	{
		lengthParser.defaultUnit( unit );
	}

	public void setDefaultAngleUnit( Unit<Angle> unit )
	{
		azimuthParser.defaultUnit( unit );
		inclinationParser.defaultUnit( unit );
	}

	public int getDoubleFractionDigits( )
	{
		return doubleFormat.getMaximumFractionDigits( );
	}

	public void setDoubleFractionDigits( int digits )
	{
		doubleFormat.setMinimumFractionDigits( digits );
		doubleFormat.setMaximumFractionDigits( digits );
	}

	public int getLengthFractionDigits( )
	{
		return lengthFormat.getMaximumFractionDigits( );
	}

	public void setLengthFractionDigits( int digits )
	{
		lengthFormat.setMinimumFractionDigits( digits );
		lengthFormat.setMaximumFractionDigits( digits );
	}

	public int getAzimuthFractionDigits( )
	{
		return azimuthFormat.getMaximumFractionDigits( );
	}

	public void setAzimuthFractionDigits( int digits )
	{
		azimuthFormat.setMinimumFractionDigits( digits );
		azimuthFormat.setMaximumFractionDigits( digits );
	}

	public int getInclinationFractionDigits( )
	{
		return inclinationFormat.getMaximumFractionDigits( );
	}

	public void setInclinationFractionDigits( int digits )
	{
		inclinationFormat.setMinimumFractionDigits( digits );
		inclinationFormat.setMaximumFractionDigits( digits );
	}

	public char getDecimalSeparator( )
	{
		return doubleFormat.getDecimalFormatSymbols( ).getDecimalSeparator( );
	}

	public void setDecimalSeparator( char separator )
	{
		DecimalFormatSymbols symbols = doubleFormat.getDecimalFormatSymbols( );
		symbols.setDecimalSeparator( separator );

		parseDoubleFormat.setDecimalFormatSymbols( symbols );
		doubleFormat.setDecimalFormatSymbols( symbols );
		lengthFormat.setDecimalFormatSymbols( symbols );
		azimuthFormat.setDecimalFormatSymbols( symbols );
		inclinationFormat.setDecimalFormatSymbols( symbols );
	}

	private static void increment( ParsePosition p )
	{
		p.setIndex( p.getIndex( ) + 1 );
	}

	private static void skipWhitespace( String s , ParsePosition p )
	{
		while( p.getIndex( ) < s.length( ) && Character.isWhitespace( s.charAt( p.getIndex( ) ) ) )
		{
			increment( p );
		}
	}

	private static void requireParsePositionAtEnd( String value , ParsePosition p ) throws ParseException
	{
		if( p.getIndex( ) != value.length( ) )
		{
			throw new ParseException( "invalid characters after number" , p.getIndex( ) );
		}
	}

	public Integer parseInteger( String value ) throws ParseException
	{
		ParsePosition p = new ParsePosition( 0 );
		Integer i = parseIntFormat.parse( value , p ).intValue( );
		requireParsePositionAtEnd( value , p );
		return i;
	}

	public ParsedText<Integer> parseCustomInteger( String text )
	{
		ParsePosition p = new ParsePosition( 0 );
		skipWhitespace( text , p );
		if( p.getIndex( ) == text.length( ) )
		{
			return null;
		}
		ParsedText<Integer> result = new ParsedText<>( );
		result.setText( text );
		try
		{
			result.setValue( parseInteger( text ) );
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "customColumn.integer.invalidNumber" , text ) );
		}
		return result;
	}

	public Double parseDouble( String value ) throws ParseException
	{
		ParsePosition p = new ParsePosition( 0 );
		Double d = parseDoubleFormat.parse( value , p ).doubleValue( );
		requireParsePositionAtEnd( value , p );
		return d;
	}

	public ParsedText<Double> parseCustomDouble( String text )
	{
		ParsePosition p = new ParsePosition( 0 );
		skipWhitespace( text , p );
		if( p.getIndex( ) == text.length( ) )
		{
			return null;
		}
		ParsedText<Double> result = new ParsedText<>( );
		result.setText( text );
		try
		{
			result.setValue( parseDouble( text ) );
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "customColumn.double.invalidNumber" , text ) );
		}
		return result;
	}

	public Double parseInclination( String value ) throws ParseException
	{
		ParsePosition p = new ParsePosition( 0 );
		skipWhitespace( value , p );

		if( p.getIndex( ) < value.length( ) && value.charAt( p.getIndex( ) ) == '+' )
		{
			increment( p );
		}
		Double d = parseDoubleFormat.parse( value , p ).doubleValue( );
		requireParsePositionAtEnd( value , p );
		return d;
	}

	public Double parseOptionalDouble( String value ) throws ParseException
	{
		if( "-".equals( value ) || "--".equals( value ) )
		{
			return null;
		}
		return parseDouble( value );
	}

	public Double parseOptionalInclination( String value ) throws ParseException
	{
		if( "-".equals( value ) || "--".equals( value ) )
		{
			return null;
		}
		return parseInclination( value );
	}

	public String formatInteger( Integer value )
	{
		return intFormat.format( value );
	}

	public String formatDouble( Double value )
	{
		return doubleFormat.format( value );
	}

	public String formatLength( UnitizedDouble<Length> value )
	{
		double dblValue = value.doubleValue( lengthParser.defaultUnit( ) );
		return lengthFormat.format( dblValue ) +
			unitNames.getName( lengthParser.defaultUnit( ) , dblValue , unitNameType );
	}

	public String formatAzimuth( UnitizedDouble<Angle> value )
	{
		double dblValue = value.doubleValue( azimuthParser.defaultUnit( ) );
		return azimuthFormat.format( dblValue ) +
			unitNames.getName( azimuthParser.defaultUnit( ) , dblValue , unitNameType );
	}

	public String formatInclination( UnitizedDouble<Angle> value )
	{
		double dblValue = value.doubleValue( inclinationParser.defaultUnit( ) );
		return inclinationFormat.format( dblValue ) +
			unitNames.getName( inclinationParser.defaultUnit( ) , dblValue , unitNameType );
	}

	public String formatOptionalDouble( Double value )
	{
		return value == null ? "--" : doubleFormat.format( value );
	}

	public String formatOptionalLength( UnitizedDouble<Length> value )
	{
		return value == null ? "--" : formatLength( value );
	}

	public String formatOptionalAzimuth( UnitizedDouble<Angle> value )
	{
		return value == null ? "--" : formatAzimuth( value );
	}

	public String formatOptionalInclination( UnitizedDouble<Angle> value )
	{
		return value == null ? "--" : formatInclination( value );
	}

	public String format( ShotVector v )
	{
		if( v instanceof DaiShotVector )
		{
			return format( ( DaiShotVector ) v );
		}
		else if( v instanceof NevShotVector )
		{
			return format( ( NevShotVector ) v );
		}
		return v.toString( );
	}

	public String format( DaiShotVector t )
	{
		return localizer
			.getFormattedString(
				"shotVectorFormat.dai" ,
				StringUtils.pad( formatOptionalLength( t.getDistance( ) ) , ' ' , vectorColumn0Width , false ) ,
				StringUtils.pad( formatOptionalAzimuth( t.getFrontsightAzimuth( ) ) , ' ' , vectorColumn1Width / 2 ,
					false ) ,
				StringUtils.pad( formatOptionalAzimuth( t.getBacksightAzimuth( ) ) , ' ' , vectorColumn1Width / 2 ,
					false ) ,
				StringUtils.pad( formatOptionalInclination( t.getFrontsightInclination( ) ) , ' ' ,
					vectorColumn2Width / 2 ,
					false ) ,
				StringUtils.pad( formatOptionalInclination( t.getBacksightInclination( ) ) , ' ' ,
					vectorColumn2Width / 2 ,
					false ) ,
				StringUtils.pad( formatOptionalLength( t.getInstrumentHeight( ) ) , ' ' ,
					vectorColumn3Width ,
					false ) ,
				StringUtils.pad( formatOptionalLength( t.getTargetHeight( ) ) , ' ' ,
					vectorColumn4Width ,
					false )
			);
	}

	public String format( NevShotVector t )
	{
		return localizer.getFormattedString(
			t.isDownwardPositive( ) ? "shotVectorFormat.ned" : "shotVectorFormat.neel" ,
			StringUtils.pad( formatOptionalLength( t.getNorthOffset( ) ) , ' ' , vectorColumn0Width , false ) ,
			StringUtils.pad( formatOptionalLength( t.getEastOffset( ) ) , ' ' , vectorColumn1Width , false ) ,
			StringUtils.pad( formatOptionalLength( t.getVerticalOffset( ) ) , ' ' , vectorColumn2Width , false )
			);
	}

	public String formatRaw( ShotVector t )
	{
		if( t instanceof DaiShotVector )
		{
			return formatRaw( ( DaiShotVector ) t );
		}
		else if( t instanceof NevShotVector )
		{
			return formatRaw( ( NevShotVector ) t );
		}
		return null;
	}

	public String formatRaw( DaiShotVector t )
	{
		StringBuilder sb = new StringBuilder( );
		sb.append( formatOptionalLength( t.getDistance( ) ) );
		sb.append( "  " );
		sb.append( formatOptionalAzimuth( t.getFrontsightAzimuth( ) ) );
		if( t.getBacksightAzimuth( ) != null )
		{
			sb.append( '/' );
			sb.append( formatOptionalAzimuth( t.getBacksightAzimuth( ) ) );
		}
		sb.append( "  " );
		sb.append( formatOptionalInclination( t.getFrontsightInclination( ) ) );
		if( t.getBacksightInclination( ) != null )
		{
			sb.append( '/' );
			sb.append( formatOptionalInclination( t.getBacksightInclination( ) ) );
		}
		if( t.getInstrumentHeight( ) != null || t.getTargetHeight( ) != null )
		{
			sb.append( "  " );
			sb.append( formatOptionalLength( t.getInstrumentHeight( ) ) );
			sb.append( "  " );
			sb.append( formatOptionalLength( t.getTargetHeight( ) ) );
		}
		return sb.toString( );
	}

	public String formatRaw( NevShotVector t )
	{
		StringBuilder sb = new StringBuilder( );
		sb.append( formatOptionalLength( t.getNorthOffset( ) ) );
		sb.append( "  " );
		sb.append( formatOptionalLength( t.getEastOffset( ) ) );
		sb.append( "  " );
		sb.append( formatOptionalLength( t.getVerticalOffset( ) ) );
		return sb.toString( );
	}

	public ParsedTextWithType<ShotVector> parseShotVector( String s , ShotVectorType type )
	{
		switch( type )
		{
		case DAIc:
		case DAIu:
			return parseDaiShotVector( s , type );
		case NED:
		case NEEl:
			return parseNevShotVector( s , type );
		}
		throw new IllegalArgumentException( "invalid type: " + type );
	}

	private static <V> V valueOrNull( ValueToken<V> token )
	{
		return token != null ? token.value : null;
	}

	public ParsedTextWithType<ShotVector> parseDaiShotVector( String s , ShotVectorType type )
	{
		ParsedTextWithType<ShotVector> result = new ParsedTextWithType<>( );
		result.setText( s );
		result.setType( type );

		ValueToken<UnitizedDouble<Length>> dist = null;
		ValueToken<UnitizedDouble<Angle>> azmFs = null;
		ValueToken<UnitizedDouble<Angle>> azmBs = null;
		ValueToken<UnitizedDouble<Angle>> incFs = null;
		ValueToken<UnitizedDouble<Angle>> incBs = null;
		ValueToken<UnitizedDouble<Length>> instHeight = null;
		ValueToken<UnitizedDouble<Length>> targetHeight = null;

		LineTokenizer lineTokenizer = new LineTokenizer( s , 0 );

		lineTokenizer.pull( Character::isWhitespace );

		if( lineTokenizer.isAtEnd( ) )
		{
			return result;
		}

		// DISTANCE

		try
		{
			dist = lengthParser.pullOptionalLength( lineTokenizer );
			if( dist == null )
			{
				result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidDaiShotVector" ) );
				return result;
			}
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidDistance" ) );
			return result;
		}

		lineTokenizer.pull( Character::isWhitespace );

		// FS AZIMUTH

		try
		{
			azmFs = azimuthParser.pullOptionalAzimuth( lineTokenizer );
			if( azmFs == null )
			{
				result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidDaiShotVector" ) );
				return result;
			}
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidAzimuth" ) );
			return result;
		}

		lineTokenizer.pull( Character::isWhitespace );

		// BS AZIMUTH

		if( lineTokenizer.pull( '/' ) != null )
		{
			lineTokenizer.pull( Character::isWhitespace );

			try
			{
				azmBs = azimuthParser.pullOptionalAzimuth( lineTokenizer );
				if( azmBs == null )
				{
					result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidDaiShotVector" ) );
					return result;
				}
			}
			catch( Exception ex )
			{
				result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidAzimuth" ) );
				return result;
			}
		}

		lineTokenizer.pull( Character::isWhitespace );

		// FS INCLINATION

		try
		{
			incFs = inclinationParser.pullOptionalInclination( lineTokenizer );
			if( incFs == null )
			{
				result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidDaiShotVector" ) );
				return result;
			}
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidInclination" ) );
			return result;
		}

		lineTokenizer.pull( Character::isWhitespace );

		// BS INCLINATION

		if( lineTokenizer.pull( '/' ) != null )
		{
			lineTokenizer.pull( Character::isWhitespace );

			try
			{
				incBs = inclinationParser.pullOptionalInclination( lineTokenizer );
				if( incBs == null )
				{
					result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidDaiShotVector" ) );
					return result;
				}
			}
			catch( Exception ex )
			{
				result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidInclination" ) );
				return result;
			}
		}

		lineTokenizer.pull( Character::isWhitespace );

		// INSTRUMENT HEIGHT

		try
		{
			instHeight = lengthParser.pullOptionalLength( lineTokenizer );
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidInstrumentHeight" ) );
			return result;
		}

		// TARGET HEIGHT

		if( instHeight != null )
		{
			try
			{
				targetHeight = lengthParser.pullOptionalLength( lineTokenizer );
			}
			catch( Exception ex )
			{
				result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidTargetHeight" ) );
				return result;
			}
		}

		lineTokenizer.pull( Character::isWhitespace );

		if( !lineTokenizer.isAtEnd( ) )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidDaiShotVector" ) );
			return result;
		}

		DaiShotVector vector = new DaiShotVector( );
		vector.setBacksightsAreCorrected( type == ShotVectorType.DAIc );
		vector.setDistance( valueOrNull( dist ) );
		vector.setFrontsightAzimuth( valueOrNull( azmFs ) );
		vector.setBacksightAzimuth( valueOrNull( azmBs ) );
		vector.setFrontsightInclination( valueOrNull( incFs ) );
		vector.setBacksightInclination( valueOrNull( incBs ) );
		vector.setInstrumentHeight( valueOrNull( instHeight ) );
		vector.setTargetHeight( valueOrNull( targetHeight ) );
		result.setValue( vector );

		return result;
	}

	public ParsedTextWithType<ShotVector> parseNevShotVector( String s , ShotVectorType type )
	{
		ParsedTextWithType<ShotVector> result = new ParsedTextWithType<>( );
		result.setText( s );
		result.setType( type );

		LineTokenizer lineTokenizer = new LineTokenizer( s , 0 );

		ValueToken<UnitizedDouble<Length>> northOffs = null;
		ValueToken<UnitizedDouble<Length>> eastOffs = null;
		ValueToken<UnitizedDouble<Length>> verticalOffs = null;

		lineTokenizer.pull( Character::isWhitespace );

		if( lineTokenizer.isAtEnd( ) )
		{
			return result;
		}

		// NORTH OFFSET

		try
		{
			northOffs = lengthParser.pullOptionalLength( lineTokenizer );
			if( northOffs == null )
			{
				result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidNorthOffset" ) );
				return result;
			}
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidNevShotVector" ) );
			return result;
		}

		lineTokenizer.pull( Character::isWhitespace );

		// EAST OFFSET

		try
		{
			eastOffs = lengthParser.pullOptionalLength( lineTokenizer );
			if( eastOffs == null )
			{
				result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidEastOffset" ) );
				return result;
			}
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidNevShotVector" ) );
			return result;
		}

		lineTokenizer.pull( Character::isWhitespace );

		// VERTICAL OFFSET

		try
		{
			verticalOffs = lengthParser.pullOptionalLength( lineTokenizer );
			if( verticalOffs == null )
			{
				result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidVerticalOffset" ) );
				return result;
			}
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidNevShotVector" ) );
			return result;
		}

		lineTokenizer.pull( Character::isWhitespace );

		if( !lineTokenizer.isAtEnd( ) )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidNevShotVector" ) );
			return result;
		}

		NevShotVector vector = new NevShotVector( );
		vector.setDownwardIsPositive( type == ShotVectorType.NED );
		vector.setNorthOffset( valueOrNull( northOffs ) );
		vector.setEastOffset( valueOrNull( eastOffs ) );
		vector.setVerticalOffset( valueOrNull( verticalOffs ) );

		result.setValue( vector );
		return result;
	}

	public String format( XSection x )
	{
		if( x instanceof LrudXSection )
		{
			return format( ( LrudXSection ) x );
		}
		else if( x instanceof NsewXSection )
		{
			return format( ( NsewXSection ) x );
		}
		else if( x instanceof LlrrudXSection )
		{
			return format( ( LlrrudXSection ) x );
		}
		return x.toString( );
	}

	public String format( LrudXSection t )
	{
		if( t.getAngle( ) instanceof FacingAzimuth )
		{
			return localizer
				.getFormattedString(
					"xSectionFormat.lruda" ,
					StringUtils.pad( formatOptionalLength( t.getLeft( ) ) , ' ' , xSectionColumnWidth , false ) ,
					StringUtils.pad( formatOptionalLength( t.getRight( ) ) , ' ' , xSectionColumnWidth , false ) ,
					StringUtils.pad( formatOptionalLength( t.getUp( ) ) , ' ' , xSectionColumnWidth , false ) ,
					StringUtils.pad( formatOptionalLength( t.getDown( ) ) , ' ' , xSectionColumnWidth , false ) ,
					StringUtils.pad( formatOptionalAzimuth( ( ( FacingAzimuth ) t.getAngle( ) ).getAzimuth( ) ) , ' ' ,
						xSectionColumnWidth , false )
				);
		}
		return localizer
			.getFormattedString(
				"xSectionFormat.lrud" ,
				StringUtils.pad( formatOptionalLength( t.getLeft( ) ) , ' ' , xSectionColumnWidth , false ) ,
				StringUtils.pad( formatOptionalLength( t.getRight( ) ) , ' ' , xSectionColumnWidth , false ) ,
				StringUtils.pad( formatOptionalLength( t.getUp( ) ) , ' ' , xSectionColumnWidth , false ) ,
				StringUtils.pad( formatOptionalLength( t.getDown( ) ) , ' ' , xSectionColumnWidth , false )
			);
	}

	public String format( LlrrudXSection t )
	{
		return localizer
			.getFormattedString(
				"xSectionFormat.llrrud" ,
				StringUtils.pad( formatOptionalLength( t.getLeftNorthing( ) ) , ' ' , xSectionColumnWidth , false ) ,
				StringUtils.pad( formatOptionalLength( t.getLeftEasting( ) ) , ' ' , xSectionColumnWidth , false ) ,
				StringUtils.pad( formatOptionalLength( t.getRightNorthing( ) ) , ' ' , xSectionColumnWidth , false ) ,
				StringUtils.pad( formatOptionalLength( t.getRightEasting( ) ) , ' ' , xSectionColumnWidth , false ) ,
				StringUtils.pad( formatOptionalLength( t.getUp( ) ) , ' ' , xSectionColumnWidth , false ) ,
				StringUtils.pad( formatOptionalLength( t.getDown( ) ) , ' ' , xSectionColumnWidth , false )
			);
	}

	public String format( NsewXSection t )
	{
		return localizer
			.getFormattedString(
				"xSectionFormat.nsew" ,
				StringUtils.pad( formatOptionalLength( t.getNorth( ) ) , ' ' , xSectionColumnWidth , false ) ,
				StringUtils.pad( formatOptionalLength( t.getSouth( ) ) , ' ' , xSectionColumnWidth , false ) ,
				StringUtils.pad( formatOptionalLength( t.getEast( ) ) , ' ' , xSectionColumnWidth , false ) ,
				StringUtils.pad( formatOptionalLength( t.getWest( ) ) , ' ' , xSectionColumnWidth , false )
			);
	}

	public String formatRaw( XSection x )
	{
		if( x instanceof LrudXSection )
		{
			return formatRaw( ( LrudXSection ) x );
		}
		else if( x instanceof NsewXSection )
		{
			return formatRaw( ( NsewXSection ) x );
		}
		else if( x instanceof LlrrudXSection )
		{
			return formatRaw( ( LlrrudXSection ) x );
		}
		return x.toString( );
	}

	public String formatRaw( LrudXSection x )
	{
		StringBuilder sb = new StringBuilder( );
		sb.append( formatOptionalLength( x.getLeft( ) ) );
		sb.append( ' ' );
		sb.append( formatOptionalLength( x.getRight( ) ) );
		sb.append( ' ' );
		sb.append( formatOptionalLength( x.getUp( ) ) );
		sb.append( ' ' );
		sb.append( formatOptionalLength( x.getDown( ) ) );
		if( x.getAngle( ) instanceof FacingAzimuth )
		{
			sb.append( ' ' );
			sb.append( formatOptionalAzimuth( ( ( FacingAzimuth ) x.getAngle( ) ).getAzimuth( ) ) );
		}
		return sb.toString( );
	}

	public String formatRaw( LlrrudXSection x )
	{
		StringBuilder sb = new StringBuilder( );
		sb.append( formatOptionalLength( x.getLeftNorthing( ) ) );
		sb.append( ' ' );
		sb.append( formatOptionalLength( x.getLeftEasting( ) ) );
		sb.append( ' ' );
		sb.append( formatOptionalLength( x.getRightNorthing( ) ) );
		sb.append( ' ' );
		sb.append( formatOptionalLength( x.getRightEasting( ) ) );
		sb.append( ' ' );
		sb.append( formatOptionalLength( x.getUp( ) ) );
		sb.append( ' ' );
		sb.append( formatOptionalLength( x.getDown( ) ) );
		return sb.toString( );
	}

	public String formatRaw( NsewXSection x )
	{
		StringBuilder sb = new StringBuilder( );
		sb.append( formatOptionalLength( x.getNorth( ) ) );
		sb.append( ' ' );
		sb.append( formatOptionalLength( x.getSouth( ) ) );
		sb.append( ' ' );
		sb.append( formatOptionalLength( x.getEast( ) ) );
		sb.append( ' ' );
		sb.append( formatOptionalLength( x.getWest( ) ) );
		return sb.toString( );
	}

	public ParsedTextWithType<XSection> parseXSection( String s , XSectionType type )
	{
		ParsedTextWithType<XSection> result = new ParsedTextWithType<>( );
		result.setType( type );

		LineTokenizer lineTokenizer = new LineTokenizer( s , 0 );

		result.setText( s );

		int reqNumLengths = 0;

		switch( type )
		{
		case BISECTOR_LRUD:
		case PERPENDICULAR_LRUD:
		case NSEW:
		case LRUD_WITH_FACING_AZIMUTH:
			reqNumLengths = 4;
			break;
		case LLRRUD:
			reqNumLengths = 6;
			break;
		default:
			throw new IllegalArgumentException( "Unsupported type: " + type );
		}

		ValueToken<UnitizedDouble<Length>>[ ] lengths = new ValueToken[ reqNumLengths ];

		for( int i = 0 ; i < reqNumLengths ; i++ )
		{
			lineTokenizer.pull( Character::isWhitespace );

			lengths[ i ] = lengthParser.pullOptionalLength( lineTokenizer );
			if( lengths[ i ] == null )
			{
				result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "xSectionColumn.invalidXSection" ) );
				return result;
			}
		}

		ValueToken<UnitizedDouble<Angle>> facingAzm = null;

		if( type == XSectionType.LRUD_WITH_FACING_AZIMUTH )
		{
			lineTokenizer.pull( Character::isWhitespace );

			facingAzm = azimuthParser.pullOptionalAzimuth( lineTokenizer );
			if( facingAzm == null )
			{
				result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "xSectionColumn.invalidXSection" ) );
				return result;
			}
		}

		lineTokenizer.pull( Character::isWhitespace );

		if( !lineTokenizer.isAtEnd( ) )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "xSectionColumn.invalidXSection" ) );
			return result;
		}

		switch( type )
		{
		case BISECTOR_LRUD:
		case PERPENDICULAR_LRUD:
			LrudXSection lrud = new LrudXSection( );
			lrud.setAngle( type == XSectionType.BISECTOR_LRUD ? XAngle.BISECTOR : XAngle.PERPENDICULAR );
			lrud.setLeft( valueOrNull( lengths[ 0 ] ) );
			lrud.setRight( valueOrNull( lengths[ 1 ] ) );
			lrud.setUp( valueOrNull( lengths[ 2 ] ) );
			lrud.setDown( valueOrNull( lengths[ 3 ] ) );
			result.setValue( lrud );
			break;
		case LRUD_WITH_FACING_AZIMUTH:
			LrudXSection lruda = new LrudXSection( );
			lruda.setAngle( new FacingAzimuth( valueOrNull( facingAzm ) ) );
			lruda.setLeft( valueOrNull( lengths[ 0 ] ) );
			lruda.setRight( valueOrNull( lengths[ 1 ] ) );
			lruda.setUp( valueOrNull( lengths[ 2 ] ) );
			lruda.setDown( valueOrNull( lengths[ 3 ] ) );
			result.setValue( lruda );
			break;
		case NSEW:
			NsewXSection nsew = new NsewXSection( );
			nsew.setNorth( valueOrNull( lengths[ 0 ] ) );
			nsew.setSouth( valueOrNull( lengths[ 1 ] ) );
			nsew.setEast( valueOrNull( lengths[ 2 ] ) );
			nsew.setWest( valueOrNull( lengths[ 3 ] ) );
			result.setValue( nsew );
			break;
		case LLRRUD:
			LlrrudXSection llrrud = new LlrrudXSection( );
			llrrud.setLeftNorthing( valueOrNull( lengths[ 0 ] ) );
			llrrud.setLeftEasting( valueOrNull( lengths[ 1 ] ) );
			llrrud.setRightNorthing( valueOrNull( lengths[ 2 ] ) );
			llrrud.setRightEasting( valueOrNull( lengths[ 3 ] ) );
			llrrud.setUp( valueOrNull( lengths[ 4 ] ) );
			llrrud.setDown( valueOrNull( lengths[ 5 ] ) );
			result.setValue( llrrud );
			break;
		}

		return result;
	}

	public ParsedText<URL> parseUrl( String text )
	{
		if( StringUtils.isNullOrEmpty( text ) )
		{
			return null;
		}

		ParsedText<URL> result = new ParsedText<URL>( );
		result.setText( text );
		try
		{
			result.setValue( new URL( text ) );
		}
		catch( MalformedURLException ex )
		{
			try
			{
				result.setValue( new URL( "http://" + text ) );
			}
			catch( MalformedURLException ex2 )
			{
				result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "customColumn.link.malformedUrl" ) );
			}
		}
		return result;
	}
}

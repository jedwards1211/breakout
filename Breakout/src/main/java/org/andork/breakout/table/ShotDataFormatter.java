package org.andork.breakout.table;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andork.breakout.table.LrudXSection.Angle;
import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
import org.andork.util.StringUtils;

public class ShotDataFormatter
{
	private final NumberFormat		intFormat;
	private final NumberFormat		parseIntFormat;

	private final DecimalFormat		parseDoubleFormat;
	private final DecimalFormat		doubleFormat;
	private final DecimalFormat		lengthFormat;
	private final DecimalFormat		azimuthFormat;
	private final DecimalFormat		inclinationFormat;

	private Localizer				localizer;

	private int						vectorColumn0Width		= 9;
	private int						vectorColumn1Width		= 11;
	private int						vectorColumn2Width		= 11;

	private int						xSectionColumnWidth		= 6;

	private static final Pattern	daiShotVectorPattern	= Pattern
																.compile( "\\s*(\\S+)\\s+([^ \t\n\u000b\f\r/]+)\\s*(/\\s*(\\S+))?\\s+([^ \t\n\u000b\f\r/]+)\\s*(/\\s*(\\S+))?\\s*" );
	private static final Pattern	whitespacePattern		= Pattern.compile( "\\s*" );

	public ShotDataFormatter( I18n i18n )
	{
		localizer = i18n.forClass( ShotDataFormatter.class );

		parseIntFormat = createDefaultIntegerFormat( );
		intFormat = createDefaultIntegerFormat( );

		parseDoubleFormat = createDefaultDoubleFormat( );
		doubleFormat = createDefaultDoubleFormat( );
		lengthFormat = createDefaultDoubleFormat( );
		azimuthFormat = createDefaultDoubleFormat( );
		inclinationFormat = createDefaultDoubleFormat( );
		inclinationFormat.setPositivePrefix( "+" );

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

	public String formatLength( Double value )
	{
		return lengthFormat.format( value );
	}

	public String formatAzimuth( Double value )
	{
		return azimuthFormat.format( value );
	}

	public String formatInclination( Double value )
	{
		return inclinationFormat.format( value );
	}

	public String formatOptionalDouble( Double value )
	{
		return value == null ? "--" : doubleFormat.format( value );
	}

	public String formatOptionalLength( Double value )
	{
		return value == null ? "--" : lengthFormat.format( value );
	}

	public String formatOptionalAzimuth( Double value )
	{
		return value == null ? "--" : azimuthFormat.format( value );
	}

	public String formatOptionalInclination( Double value )
	{
		return value == null ? "--" : inclinationFormat.format( value );
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
				StringUtils.pad( formatOptionalDouble( t.getDistance( ) ) , ' ' , vectorColumn0Width , false ) ,
				StringUtils.pad( formatOptionalAzimuth( t.getFrontsightAzimuth( ) ) , ' ' , vectorColumn1Width / 2 ,
					false ) ,
				StringUtils.pad( formatOptionalAzimuth( t.getBacksightAzimuth( ) ) , ' ' , vectorColumn1Width / 2 ,
					false ) ,
				StringUtils.pad( formatOptionalInclination( t.getFrontsightInclination( ) ) , ' ' ,
					vectorColumn2Width / 2 ,
					false ) ,
				StringUtils.pad( formatOptionalInclination( t.getBacksightInclination( ) ) , ' ' ,
					vectorColumn2Width / 2 ,
					false )
			);
	}

	public String format( NevShotVector t )
	{
		return localizer.getFormattedString(
			t.isDownwardPositive( ) ? "shotVectorFormat.ned" : "shotVectorFormat.neel" ,
			StringUtils.pad( formatOptionalDouble( t.getNorthOffset( ) ) , ' ' , vectorColumn0Width , false ) ,
			StringUtils.pad( formatOptionalDouble( t.getEastOffset( ) ) , ' ' , vectorColumn1Width , false ) ,
			StringUtils.pad( formatOptionalDouble( t.getVerticalOffset( ) ) , ' ' , vectorColumn2Width , false )
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
		sb.append( '/' );
		sb.append( formatOptionalAzimuth( t.getBacksightAzimuth( ) ) );
		sb.append( "  " );
		sb.append( formatOptionalInclination( t.getFrontsightInclination( ) ) );
		sb.append( '/' );
		sb.append( formatOptionalInclination( t.getBacksightInclination( ) ) );
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

	public ParsedTextWithType<ShotVector> parseDaiShotVector( String s , ShotVectorType type )
	{
		ParsedTextWithType<ShotVector> result = new ParsedTextWithType<>( );
		result.setText( s );
		result.setType( type );

		Matcher m = daiShotVectorPattern.matcher( s );
		if( !m.find( ) )
		{
			if( whitespacePattern.matcher( s ).matches( ) )
			{
				return result;
			}
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidDaiShotVector" ) );
			return result;
		}

		String distText = m.group( 1 );
		Double dist = null;
		try
		{
			dist = parseDouble( distText );
			if( dist != null && dist < 0.0 )
			{
				result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.negativeDistance" ) );
				return result;
			}
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidDistance" , distText ) );
			return result;
		}

		String azmFsText = m.group( 2 );
		Double azmFs = null;
		try
		{
			azmFs = parseOptionalDouble( azmFsText );
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidAzimuth" , azmFsText ) );
			return result;
		}

		String azmBsText = m.group( 4 );
		Double azmBs = null;
		if( azmBsText != null )
		{
			try
			{
				azmBs = parseOptionalDouble( azmBsText );
			}
			catch( Exception ex )
			{
				result
					.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidAzimuth" , azmBsText ) );
				return result;
			}
		}

		String incFsText = m.group( 5 );
		Double incFs = null;
		try
		{
			incFs = parseOptionalInclination( incFsText );
		}
		catch( Exception ex )
		{
			result
				.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidInclination" , incFsText ) );
			return result;
		}

		String incBsText = m.group( 7 );
		Double incBs = null;
		if( incBsText != null )
		{
			try
			{
				incBs = parseOptionalInclination( incBsText );
			}
			catch( Exception ex )
			{
				result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.invalidInclination" ,
					incBsText ) );
				return result;
			}
		}

		if( azmFs == null && azmBs == null )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.missingAzimuth" ) );
		}
		else if( incFs == null && incBs == null )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "vectorColumn.missingInclination" ) );
		}

		DaiShotVector vector = new DaiShotVector( );
		vector.setBacksightsAreCorrected( type == ShotVectorType.DAIc );
		vector.setDistance( dist );
		vector.setFrontsightAzimuth( azmFs );
		vector.setBacksightAzimuth( azmBs );
		vector.setFrontsightInclination( incFs );
		vector.setBacksightInclination( incBs );
		result.setValue( vector );

		return result;
	}

	public ParsedTextWithType<ShotVector> parseNevShotVector( String s , ShotVectorType type )
	{
		ParsedTextWithType<ShotVector> result = new ParsedTextWithType<>( );
		result.setText( s );
		result.setType( type );

		String[ ] parts = s.split( "\\s+" );

		if( parts.length != 3 )
		{
			if( whitespacePattern.matcher( s ).matches( ) )
			{
				return result;
			}
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "invalidNevShotVector" ) );
			return result;
		}

		NevShotVector vector = new NevShotVector( );
		vector.setDownwardIsPositive( type == ShotVectorType.NED );

		try
		{
			vector.setNorthOffset( parseDouble( parts[ 0 ] ) );
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "invalidNorthOffset" , parts[ 0 ] ) );
			return result;
		}

		try
		{
			vector.setEastOffset( parseDouble( parts[ 1 ] ) );
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "invalidEastOffset" , parts[ 1 ] ) );
			return result;
		}

		try
		{
			vector.setVerticalOffset( parseDouble( parts[ 2 ] ) );
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "invalidVerticalOffset" , parts[ 2 ] ) );
			return result;
		}

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

		String trimmed = s.trim( );
		if( trimmed.isEmpty( ) )
		{
			return result;
		}
		result.setText( s );

		String[ ] parts = trimmed.split( "\\s+" );

		int reqNumParts = 0;

		switch( type )
		{
		case BISECTOR_LRUD:
		case PERPENDICULAR_LRUD:
		case NSEW:
			reqNumParts = 4;
			break;
		case LLRRUD:
			reqNumParts = 6;
			break;
		default:
			throw new IllegalArgumentException( "Unsupported type: " + type );
		}

		if( parts.length != reqNumParts )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "xSectionColumn.pleaseEnterXNumbers" ,
				reqNumParts ) );
			return result;
		}

		Double[ ] parsed = new Double[ parts.length ];

		for( int i = 0 ; i < parts.length ; i++ )
		{
			try
			{
				parsed[ i ] = parseDouble( parts[ i ] );
			}
			catch( Exception ex )
			{
				result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "invalidNumber" , parts[ i ] ) );
			}
		}

		switch( type )
		{
		case BISECTOR_LRUD:
		case PERPENDICULAR_LRUD:
			LrudXSection lrud = new LrudXSection( );
			lrud.setAngle( type == XSectionType.BISECTOR_LRUD ? Angle.BISECTOR : Angle.PERPENDICULAR );
			lrud.setLeft( parsed[ 0 ] );
			lrud.setRight( parsed[ 1 ] );
			lrud.setUp( parsed[ 2 ] );
			lrud.setDown( parsed[ 3 ] );
			result.setValue( lrud );
			break;
		case NSEW:
			NsewXSection nsew = new NsewXSection( );
			nsew.setNorth( parsed[ 0 ] );
			nsew.setSouth( parsed[ 1 ] );
			nsew.setEast( parsed[ 2 ] );
			nsew.setWest( parsed[ 3 ] );
			result.setValue( nsew );
			break;
		case LLRRUD:
			LlrrudXSection llrrud = new LlrrudXSection( );
			llrrud.setLeftNorthing( parsed[ 0 ] );
			llrrud.setLeftEasting( parsed[ 1 ] );
			llrrud.setRightNorthing( parsed[ 2 ] );
			llrrud.setRightEasting( parsed[ 3 ] );
			llrrud.setUp( parsed[ 4 ] );
			llrrud.setDown( parsed[ 5 ] );
			result.setValue( llrrud );
			break;
		}

		return result;
	}
}

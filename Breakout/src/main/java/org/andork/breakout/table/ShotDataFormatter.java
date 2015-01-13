package org.andork.breakout.table;

import java.text.DecimalFormat;
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
	private final DecimalFormat	doubleFormat;
	private final DecimalFormat	lengthFormat;
	private final DecimalFormat	azimuthFormat;
	private final DecimalFormat	inclinationFormat;

	private Localizer			localizer;

	private int					vectorColumn0Width		= 9;
	private int					vectorColumn1Width		= 11;
	private int					vectorColumn2Width		= 11;

	private int					xSectionColumnWidth		= 5;

	private Pattern				daiShotVectorPattern	= Pattern
															.compile( "\\s*(\\S+)\\s+([^ \t\n\u000b\f\r/]+)\\s*/\\s*(\\S+)\\s+([^ \t\n\u000b\f\r/]+)\\s*/\\s*(\\S+)\\s*" );
	private Pattern				whitespacePattern		= Pattern.compile( "\\s*" );

	public ShotDataFormatter( I18n i18n )
	{
		localizer = i18n.forClass( ShotDataFormatter.class );

		doubleFormat = ( DecimalFormat ) DecimalFormat.getInstance( );
		lengthFormat = ( DecimalFormat ) DecimalFormat.getInstance( );
		azimuthFormat = ( DecimalFormat ) DecimalFormat.getInstance( );
		inclinationFormat = ( DecimalFormat ) DecimalFormat.getInstance( );
		inclinationFormat.setPositivePrefix( "+" );
	}

	public Double parseDouble( String value ) throws ParseException
	{
		ParsePosition p = new ParsePosition( 0 );
		Double d = doubleFormat.parse( value , p ).doubleValue( );
		if( p.getIndex( ) != value.length( ) )
		{
			throw new ParseException( "invalid characters after number" , p.getIndex( ) );
		}
		return d;
	}

	public Double parseLength( String value ) throws ParseException
	{
		ParsePosition p = new ParsePosition( 0 );
		Double d = lengthFormat.parse( value , p ).doubleValue( );
		if( p.getIndex( ) != value.length( ) )
		{
			throw new ParseException( "invalid characters after number" , p.getIndex( ) );
		}
		return d;
	}

	public Double parseAzimuth( String value ) throws ParseException
	{
		ParsePosition p = new ParsePosition( 0 );
		Double d = azimuthFormat.parse( value , p ).doubleValue( );
		if( p.getIndex( ) != value.length( ) )
		{
			throw new ParseException( "invalid characters after number" , p.getIndex( ) );
		}
		return d;
	}

	public Double parseInclination( String value ) throws ParseException
	{
		ParsePosition p = new ParsePosition( 0 );
		Double d = inclinationFormat.parse( value , p ).doubleValue( );
		if( p.getIndex( ) != value.length( ) )
		{
			throw new ParseException( "invalid characters after number" , p.getIndex( ) );
		}
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

	public Double parseOptionalLength( String value ) throws ParseException
	{
		if( "-".equals( value ) || "--".equals( value ) )
		{
			return null;
		}
		return parseLength( value );
	}

	public Double parseOptionalAzimuth( String value ) throws ParseException
	{
		if( "-".equals( value ) || "--".equals( value ) )
		{
			return null;
		}
		return parseAzimuth( value );
	}

	public Double parseOptionalInclination( String value ) throws ParseException
	{
		if( "-".equals( value ) || "--".equals( value ) )
		{
			return null;
		}
		return parseInclination( value );
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
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "invalidDaiShotVector" ) );
			return result;
		}

		String distText = m.group( 1 );
		Double dist = null;
		try
		{
			dist = parseOptionalLength( distText );
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "invalidDist" , distText ) );
			return result;
		}

		String azmFsText = m.group( 2 );
		Double azmFs = null;
		try
		{
			azmFs = parseOptionalAzimuth( azmFsText );
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "invalidAzm" , azmFsText ) );
			return result;
		}

		String azmBsText = m.group( 3 );
		Double azmBs = null;
		try
		{
			azmBs = parseOptionalAzimuth( azmBsText );
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "invalidAzm" , azmBsText ) );
			return result;
		}

		String incFsText = m.group( 4 );
		Double incFs = null;
		try
		{
			incFs = parseOptionalInclination( incFsText );
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "invalidInc" , incFsText ) );
			return result;
		}

		String incBsText = m.group( 5 );
		Double incBs = null;
		try
		{
			incBs = parseOptionalInclination( incBsText );
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "invalidInc" , incBsText ) );
			return result;
		}

		if( azmFs == null && azmBs == null )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "missingAzimuth" ) );
		}
		else if( incFs == null && incBs == null )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "missingInclination" ) );
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
			vector.setNorthOffset( parseLength( parts[ 0 ] ) );
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "invalidNorthOffset" , parts[ 0 ] ) );
			return result;
		}

		try
		{
			vector.setEastOffset( parseLength( parts[ 1 ] ) );
		}
		catch( Exception ex )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "invalidEastOffset" , parts[ 1 ] ) );
			return result;
		}

		try
		{
			vector.setVerticalOffset( parseLength( parts[ 2 ] ) );
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
				"LrudXSection.format" ,
				StringUtils.pad( formatOptionalLength( t.getLeft( ) ) , ' ' , xSectionColumnWidth , false ) ,
				StringUtils.pad( formatOptionalLength( t.getRight( ) ) , ' ' , xSectionColumnWidth , false ) ,
				StringUtils.pad( formatOptionalLength( t.getUp( ) ) , ' ' , xSectionColumnWidth , false ) ,
				StringUtils.pad( formatOptionalLength( t.getDown( ) ) , ' ' , xSectionColumnWidth , false )
			);
	}

	public ParsedText<XSection> parseXSection( String s , XSectionType type )
	{
		String[ ] parts = s.split( "\\s+" );

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

		ParsedText<XSection> result = new ParsedText<>( );
		result.setText( s );

		if( parts.length != reqNumParts )
		{
			result.setNote( ParseNote.forMessageKey( ParseStatus.ERROR , "pleaseEnterXNumbers" , reqNumParts ) );
		}

		Double[ ] parsed = new Double[ parts.length ];

		for( int i = 0 ; i < parts.length ; i++ )
		{
			try
			{
				parsed[ i ] = parseLength( parts[ i ] );
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

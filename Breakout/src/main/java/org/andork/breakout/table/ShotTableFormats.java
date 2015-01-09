package org.andork.breakout.table;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andork.bind2.BinderHolder;
import org.andork.bind2.Link;
import org.andork.breakout.table.ShotVector.Dai;
import org.andork.breakout.table.ShotVector.Nev;
import org.andork.breakout.table.ShotVector.Nev.d;
import org.andork.breakout.table.ShotVector.Nev.el;
import org.andork.i18n.I18n;
import org.andork.i18n.I18n.Localizer;
import org.andork.q2.QObject;
import org.andork.q2.QObjectBinder;
import org.andork.util.StringUtils;

public class ShotTableFormats
{
	private BinderHolder<QObject<DataDefaults>>	defaultsHolder			= new BinderHolder<>( );
	private QObjectBinder<DataDefaults>			defaultsBinder;

	private DoubleFormatBinder					doubleFormatBinder;
	private DoubleFormatBinder					lenFormatBinder;
	private DoubleFormatBinder					angleFormatBinder;

	private Localizer							localizer;

	private static final String					daiShotVectorKey		= Dai.class.getName( ) + ".format";
	private static final String					nedShotVectorKey		= d.class.getName( ) + ".format";
	private static final String					neelShotVectorKey		= el.class.getName( ) + ".format";

	int											distWidth				= 9;
	int											azmWidth				= 5;
	int											incWidth				= 5;

//	Pattern										daiShotVectorPattern	= Pattern
//																			.compile( "\\s*(\\S+)\\s+(\\S+)\\s*/\\s*(\\S+)\\s+(\\S+)\\s*/\\s*(\\S+)\\s*" );
	Pattern										daiShotVectorPattern	= Pattern
																			.compile( "\\s*(\\S+)\\s+([^ \t\n\u000b\f\r/]+)\\s*/\\s*(\\S+)\\s+([^ \t\n\u000b\f\r/]+)\\s*/\\s*(\\S+)\\s*" );
	Pattern										whitespacePattern		= Pattern.compile( "\\s*" );

	public ShotTableFormats( I18n i18n )
	{
		localizer = i18n.forClass( ShotTableFormats.class );

		defaultsBinder = new QObjectBinder<>( DataDefaults.spec );
		defaultsBinder.objLink.bind( defaultsHolder );

		doubleFormatBinder = new DoubleFormatBinder( );
		doubleFormatBinder.decimalSepLink.bind( defaultsBinder.property( DataDefaults.decimalSep ) );
		doubleFormatBinder.numFractionDigitsLink.bind( defaultsBinder.property( DataDefaults.doubleDecimalPlaces ) );

		lenFormatBinder = new DoubleFormatBinder( );
		lenFormatBinder.decimalSepLink.bind( defaultsBinder.property( DataDefaults.decimalSep ) );
		lenFormatBinder.numFractionDigitsLink.bind( defaultsBinder.property( DataDefaults.lenDecimalPlaces ) );

		angleFormatBinder = new DoubleFormatBinder( );
		angleFormatBinder.decimalSepLink.bind( defaultsBinder.property( DataDefaults.decimalSep ) );
		angleFormatBinder.numFractionDigitsLink.bind( defaultsBinder.property( DataDefaults.angleDecimalPlaces ) );
	}

	public Link<QObject<DataDefaults>> defaultsLink( )
	{
		return defaultsHolder.binderLink;
	}

	public Double parseDouble( String value )
	{
		try
		{
			return doubleFormatBinder.get( ).parse( value ).doubleValue( );
		}
		catch( Exception ex )
		{
			throw ParseNote.forMessageKey( ParseStatus.ERROR , "invalidDouble" );
		}
	}

	public Double parseDoubleOrDash( String value )
	{
		if( "-".equals( value ) || "--".equals( value ) )
		{
			return null;
		}
		try
		{
			return doubleFormatBinder.get( ).parse( value ).doubleValue( );
		}
		catch( Exception ex )
		{
			throw ParseNote.forMessageKey( ParseStatus.ERROR , "invalidDouble" );
		}
	}

	public String formatDouble( Double value )
	{
		return doubleFormatBinder.get( ).format( value );
	}

	public String formatDoubleOrOmit( Double value )
	{
		return value == null ? "--" : doubleFormatBinder.get( ).format( value );
	}

	public String formatLength( Double value )
	{
		return lenFormatBinder.get( ).format( value );
	}

	public String formatLengthOrOmit( Double value )
	{
		return value == null ? "--" : lenFormatBinder.get( ).format( value );
	}

	public String formatAngle( Double value )
	{
		return angleFormatBinder.get( ).format( value );
	}

	public String formatAngleOrOmit( Double value )
	{
		return value == null ? "--" : angleFormatBinder.get( ).format( value );
	}

	public String formatAzmPair( Double[ ] e )
	{
		if( e[ 0 ] == null && e[ 1 ] == null )
		{
			return null;
		}
		StringBuilder sb = new StringBuilder( );
		sb.append( StringUtils.pad( formatAngleOrOmit( e[ 0 ] ) , ' ' , azmWidth , false ) );
		sb.append( '/' );
		sb.append( StringUtils.pad( formatAngleOrOmit( e[ 1 ] ) , ' ' , azmWidth , false ) );
		return sb.toString( );
	}

	public String formatIncPair( Double[ ] e )
	{
		if( e[ 0 ] == null && e[ 1 ] == null )
		{
			return null;
		}
		StringBuilder sb = new StringBuilder( );
		sb.append( StringUtils.pad( formatAngleOrOmit( e[ 0 ] ) , ' ' , incWidth , false ) );
		sb.append( '/' );
		sb.append( StringUtils.pad( formatAngleOrOmit( e[ 1 ] ) , ' ' , incWidth , false ) );
		return sb.toString( );
	}

	public String format( ShotVector v )
	{
		if( v instanceof Dai )
		{
			return format( ( Dai ) v );
		}
		else if( v instanceof d )
		{
			return format( ( d ) v );
		}
		else if( v instanceof el )
		{
			return format( ( el ) v );
		}
		return v.toString( );
	}

	public String format( Dai t )
	{
		if( t.dist == null && t.azmFs == null && t.azmBs == null && t.incFs == null && t.incBs == null )
		{
			return "";
		}
		return localizer.getFormattedString( daiShotVectorKey ,
			StringUtils.pad( formatLengthOrOmit( t.dist ) , ' ' , distWidth , false ) ,
			StringUtils.pad( formatAngleOrOmit( t.azmFs ) , ' ' , azmWidth , false ) ,
			StringUtils.pad( formatAngleOrOmit( t.azmBs ) , ' ' , azmWidth , false ) ,
			StringUtils.pad( formatAngleOrOmit( t.incFs ) , ' ' , incWidth , false ) ,
			StringUtils.pad( formatAngleOrOmit( t.incBs ) , ' ' , incWidth , false )
			);
	}

	public String format( d t )
	{
		if( t.n == null && t.e == null && t.v == null )
		{
			return "";
		}
		return localizer.getFormattedString( nedShotVectorKey ,
			StringUtils.pad( formatLengthOrOmit( t.n ) , ' ' , distWidth , false ) ,
			StringUtils.pad( formatLengthOrOmit( t.e ) , ' ' , azmWidth + azmWidth + 1 , false ) ,
			StringUtils.pad( formatLengthOrOmit( t.v ) , ' ' , incWidth + incWidth + 1 , false )
			);
	}

	public String format( el t )
	{
		if( t.n == null && t.e == null && t.v == null )
		{
			return "";
		}
		return localizer.getFormattedString( neelShotVectorKey ,
			StringUtils.pad( formatLengthOrOmit( t.n ) , ' ' , distWidth , false ) ,
			StringUtils.pad( formatLengthOrOmit( t.e ) , ' ' , azmWidth + azmWidth + 1 , false ) ,
			StringUtils.pad( formatLengthOrOmit( t.v ) , ' ' , incWidth + incWidth + 1 , false )
			);
	}

	public String formatRaw( ShotVector t )
	{
		if( t instanceof Dai )
		{
			return formatRaw( ( Dai ) t );
		}
		else if( t instanceof Nev )
		{
			return formatRaw( ( Nev ) t );
		}
		return null;
	}

	public String formatRaw( Dai t )
	{
		if( t.dist == null && t.azmFs == null && t.azmBs == null && t.incFs == null && t.incBs == null )
		{
			return "";
		}
		StringBuilder sb = new StringBuilder( );
		sb.append( formatLengthOrOmit( t.dist ) );
		sb.append( "  " );
		sb.append( formatAngleOrOmit( t.azmFs ) );
		sb.append( '/' );
		sb.append( formatAngleOrOmit( t.azmBs ) );
		sb.append( "  " );
		sb.append( formatAngleOrOmit( t.incFs ) );
		sb.append( '/' );
		sb.append( formatAngleOrOmit( t.incBs ) );
		return sb.toString( );
	}

	public String formatRaw( Nev t )
	{
		if( t.n == null && t.e == null && t.v == null )
		{
			return "";
		}
		StringBuilder sb = new StringBuilder( );
		sb.append( formatLengthOrOmit( t.n ) );
		sb.append( "  " );
		sb.append( formatLengthOrOmit( t.e ) );
		sb.append( "  " );
		sb.append( formatLengthOrOmit( t.v ) );
		return sb.toString( );
	}

	public void parseVector( String s , ShotVector dest )
	{
		if( dest instanceof Dai )
		{
			parseVector( s , ( Dai ) dest );
		}
		else if( dest instanceof Nev )
		{
			parseVector( s , ( Nev ) dest );
		}
	}

	public void parseVector( String s , Dai dest )
	{
		Matcher m = daiShotVectorPattern.matcher( s );
		if( !m.find( ) )
		{
			if( whitespacePattern.matcher( s ).matches( ) )
			{
				return;
			}
			throw ParseNote.forMessageKey( ParseStatus.ERROR , "invalidDaiShotVector" );
		}

		Double dist , azmFs , azmBs , incFs , incBs;

		String distText = m.group( 1 );
		try
		{
			dist = parseDoubleOrDash( distText );
		}
		catch( Exception ex )
		{
			throw ParseNote.forMessageKey( ParseStatus.ERROR , "invalidDist" , distText );
		}

		String azmFsText = m.group( 2 );
		try
		{
			azmFs = parseDoubleOrDash( azmFsText );
		}
		catch( Exception ex )
		{
			throw ParseNote.forMessageKey( ParseStatus.ERROR , "invalidAzm" , azmFsText );
		}

		String azmBsText = m.group( 3 );
		try
		{
			azmBs = parseDoubleOrDash( azmBsText );
		}
		catch( Exception ex )
		{
			throw ParseNote.forMessageKey( ParseStatus.ERROR , "invalidAzm" , azmBsText );
		}

		String incFsText = m.group( 4 );
		try
		{
			incFs = parseDoubleOrDash( incFsText );
		}
		catch( Exception ex )
		{
			throw ParseNote.forMessageKey( ParseStatus.ERROR , "invalidInc" , incFsText );
		}

		String incBsText = m.group( 5 );
		try
		{
			incBs = parseDoubleOrDash( incBsText );
		}
		catch( Exception ex )
		{
			throw ParseNote.forMessageKey( ParseStatus.ERROR , "invalidInc" , incBsText );
		}

		dest.dist = dist;
		dest.azmFs = azmFs;
		dest.azmBs = azmBs;
		dest.incFs = incFs;
		dest.incBs = incBs;
	}

	public void parseVector( String s , Nev dest )
	{
		String[ ] parts = s.split( "\\s+" );
		if( parts.length != 3 )
		{
			if( whitespacePattern.matcher( s ).matches( ) )
			{
				return;
			}
			throw ParseNote.forMessageKey( ParseStatus.ERROR , "invalidNevShotVector" );
		}

		Double n , e , v;

		try
		{
			n = parseDoubleOrDash( parts[ 0 ] );
		}
		catch( Exception ex )
		{
			throw ParseNote.forMessageKey( ParseStatus.ERROR , "invalidN" , parts[ 0 ] );
		}
		try
		{
			e = parseDoubleOrDash( parts[ 1 ] );
		}
		catch( Exception ex )
		{
			throw ParseNote.forMessageKey( ParseStatus.ERROR , "invalidE" , parts[ 1 ] );
		}
		try
		{
			v = parseDoubleOrDash( parts[ 2 ] );
		}
		catch( Exception ex )
		{
			throw ParseNote.forMessageKey( ParseStatus.ERROR , "invalidV" , parts[ 2 ] );
		}

		dest.n = n;
		dest.e = e;
		dest.v = v;
	}
}

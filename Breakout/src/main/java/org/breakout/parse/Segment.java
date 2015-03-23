package org.breakout.parse;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link String} wrapper that tracks its location in a source file. Even taking a substring, trimming, splitting,
 * etc. return Segments with correct location information. Line numbers and column numbers should start with 0 --
 * otherwise inconsistent numbering may result.
 * 
 * @author Andy Edwards
 */
public class Segment implements CharSequence
{
	private static final Pattern LINE_BREAK = Pattern.compile( "\r\n|\r|\n" );

	private final String value;
	public final Object source;
	private final Integer sourceIndex;
	public final Segment sourceSegment;
	public final int startLine;
	public final int endLine;
	public final int startCol;
	/**
	 * The column of the last character in this Segment. If the segment is empty this will be one less than
	 * {@link #startCol} (and possibly even negative).
	 */
	public final int endCol;

	public Segment( String value , Object source , int startLine , int startCol )
	{
		this( null , null , value , source , startLine , startCol );
	}

	protected Segment( Segment sourceSegment , Integer sourceIndex , String value , Object source , int startLine , int startCol )
	{
		super( );
		this.sourceSegment = sourceSegment;
		this.sourceIndex = sourceIndex;
		this.value = value;
		this.source = source;
		this.startLine = startLine;
		this.startCol = startCol;
		int endLine = startLine;
		int endCol = startCol + value.length( ) - 1;
		Matcher m = LINE_BREAK.matcher( value );
		while( m.find( ) && m.end( ) < value.length( ) )
		{
			endLine++;
			endCol = value.length( ) - m.end( ) - 1;
		}
		this.endLine = endLine;
		this.endCol = endCol;
	}

	protected Segment( Segment sourceSegment , Integer sourceIndex , String value , Object source , int startLine , int startCol ,
		int endLine , int endCol )
	{
		this.sourceSegment = sourceSegment;
		this.sourceIndex = sourceIndex;
		this.value = value;
		this.source = source;
		this.startLine = startLine;
		this.startCol = startCol;
		this.endLine = endLine;
		this.endCol = endCol;
	}

	public int length( )
	{
		return value.length( );
	}

	public boolean isEmpty( )
	{
		return value.isEmpty( );
	}

	public char charAt( int index )
	{
		return value.charAt( index );
	}

	public int codePointAt( int index )
	{
		return value.codePointAt( index );
	}

	public int codePointBefore( int index )
	{
		return value.codePointBefore( index );
	}

	public int codePointCount( int beginIndex , int endIndex )
	{
		return value.codePointCount( beginIndex , endIndex );
	}

	public int offsetByCodePoints( int index , int codePointOffset )
	{
		return value.offsetByCodePoints( index , codePointOffset );
	}

	public void getChars( int srcBegin , int srcEnd , char[ ] dst , int dstBegin )
	{
		value.getChars( srcBegin , srcEnd , dst , dstBegin );
	}

	public void getBytes( int srcBegin , int srcEnd , byte[ ] dst , int dstBegin )
	{
		value.getBytes( srcBegin , srcEnd , dst , dstBegin );
	}

	public byte[ ] getBytes( String charsetName ) throws UnsupportedEncodingException
	{
		return value.getBytes( charsetName );
	}

	public byte[ ] getBytes( Charset charset )
	{
		return value.getBytes( charset );
	}

	public byte[ ] getBytes( )
	{
		return value.getBytes( );
	}

	public boolean equals( Object anObject )
	{
		if( anObject instanceof Segment )
		{
			return value.equals( ( ( Segment ) anObject ).value );
		}
		return value.equals( anObject );
	}

	public boolean contentEquals( StringBuffer sb )
	{
		return value.contentEquals( sb );
	}

	public boolean contentEquals( CharSequence cs )
	{
		return value.contentEquals( cs );
	}

	public boolean equalsIgnoreCase( String anotherString )
	{
		return value.equalsIgnoreCase( anotherString );
	}

	public int compareTo( String anotherString )
	{
		return value.compareTo( anotherString );
	}

	public int compareToIgnoreCase( String str )
	{
		return value.compareToIgnoreCase( str );
	}

	public boolean regionMatches( int toffset , String other , int ooffset , int len )
	{
		return value.regionMatches( toffset , other , ooffset , len );
	}

	public boolean regionMatches( boolean ignoreCase , int toffset , String other , int ooffset , int len )
	{
		return value.regionMatches( ignoreCase , toffset , other , ooffset , len );
	}

	public boolean startsWith( String prefix , int toffset )
	{
		return value.startsWith( prefix , toffset );
	}

	public boolean startsWith( String prefix )
	{
		return value.startsWith( prefix );
	}

	public boolean endsWith( String suffix )
	{
		return value.endsWith( suffix );
	}

	public int hashCode( )
	{
		return value.hashCode( );
	}

	public int indexOf( int ch )
	{
		return value.indexOf( ch );
	}

	public int indexOf( int ch , int fromIndex )
	{
		return value.indexOf( ch , fromIndex );
	}

	public int lastIndexOf( int ch )
	{
		return value.lastIndexOf( ch );
	}

	public int lastIndexOf( int ch , int fromIndex )
	{
		return value.lastIndexOf( ch , fromIndex );
	}

	public int indexOf( String str )
	{
		return value.indexOf( str );
	}

	public int indexOf( String str , int fromIndex )
	{
		return value.indexOf( str , fromIndex );
	}

	public int lastIndexOf( String str )
	{
		return value.lastIndexOf( str );
	}

	public int lastIndexOf( String str , int fromIndex )
	{
		return value.lastIndexOf( str , fromIndex );
	}

	public Segment charBefore( )
	{
		return sourceIndex == null || sourceIndex == 0 ? substring( 0 , 0 ) :
			sourceSegment.substring( sourceIndex - 1 , sourceIndex );
	}

	public Segment charAfter( )
	{
		return sourceIndex == null || sourceIndex == sourceSegment.length( ) ? substring( length( ) ) :
			sourceSegment.substring( sourceIndex + length( ) , sourceIndex + length( ) + 1 );
	}

	public Segment substring( int beginIndex )
	{
		return substring( beginIndex , value.length( ) );
	}

	public Segment substring( int beginIndex , int endIndex )
	{
		if( startLine == endLine )
		{
			return new Segment( sourceSegment != null ? sourceSegment : this ,
				sourceIndex != null ? sourceIndex + beginIndex : beginIndex ,
				value.substring( beginIndex , endIndex ) , source , startLine ,
				startCol + beginIndex , startLine , startCol + endIndex - 1 );
		}

		int newStartLine = startLine;
		int newStartCol = startCol + beginIndex;

		int toIndex = beginIndex;
		if( toIndex < value.length( ) && toIndex > 0 && value.charAt( toIndex ) == '\n'
			&& value.charAt( toIndex - 1 ) == '\r' )
		{
			toIndex--;
		}

		Matcher m = LINE_BREAK.matcher( value ).region( 0 , toIndex );

		while( m.find( ) )
		{
			newStartLine++;
			newStartCol = beginIndex - m.end( );
		}

		return new Segment( sourceSegment != null ? sourceSegment : this ,
			sourceIndex != null ? sourceIndex + beginIndex : beginIndex ,
			value.substring( beginIndex , endIndex ) , source , newStartLine , newStartCol );
	}

	public CharSequence subSequence( int beginIndex , int endIndex )
	{
		return substring( beginIndex , endIndex );
	}

	public boolean matches( String regex )
	{
		return value.matches( regex );
	}

	public boolean contains( CharSequence s )
	{
		return value.contains( s );
	}

	public Segment[ ] split( String regex , int limit )
	{
		/* fastpath if the regex is a
		(1)one-char String and this character is not one of the
		RegEx's meta characters ".$|()[{^?*+\\", or
		(2)two-char String and the first char is the backslash and
		the second is not the ascii digit or ascii letter.
		*/
		char ch = 0;
		if( ( ( regex.length( ) == 1 &&
			".$|()[{^?*+\\".indexOf( ch = regex.charAt( 0 ) ) == -1 ) ||
			( regex.length( ) == 2 &&
				regex.charAt( 0 ) == '\\' &&
				( ( ( ch = regex.charAt( 1 ) ) - '0' ) | ( '9' - ch ) ) < 0 &&
				( ( ch - 'a' ) | ( 'z' - ch ) ) < 0 &&
			( ( ch - 'A' ) | ( 'Z' - ch ) ) < 0 ) ) &&
			( ch < Character.MIN_HIGH_SURROGATE ||
			ch > Character.MAX_LOW_SURROGATE ) )
		{
			int off = 0;
			int next = 0;
			boolean limited = limit > 0;
			ArrayList<Segment> list = new ArrayList<>( );
			while( ( next = indexOf( ch , off ) ) != -1 )
			{
				if( !limited || list.size( ) < limit - 1 )
				{
					list.add( substring( off , next ) );
					off = next + 1;
				}
				else
				{ // last one
					//assert (list.size() == limit - 1);
					list.add( substring( off , value.length( ) ) );
					off = value.length( );
					break;
				}
			}
			// If no match was found, return this
			if( off == 0 )
				return new Segment[ ] { this };

			// Add remaining segment
			if( !limited || list.size( ) < limit )
				list.add( substring( off , value.length( ) ) );

			// Construct result
			int resultSize = list.size( );
			if( limit == 0 )
			{
				while( resultSize > 0 && list.get( resultSize - 1 ).length( ) == 0 )
				{
					resultSize--;
				}
			}
			Segment[ ] result = new Segment[ resultSize ];
			return list.subList( 0 , resultSize ).toArray( result );
		}
		return split( Pattern.compile( regex ) , limit );
	}

	private Segment[ ] split( Pattern p , int limit )
	{
		int index = 0;
		boolean matchLimited = limit > 0;
		ArrayList<Segment> matchList = new ArrayList<>( );
		Matcher m = p.matcher( value );

		// Add segments before each match found
		while( m.find( ) )
		{
			if( !matchLimited || matchList.size( ) < limit - 1 )
			{
				if( index == 0 && index == m.start( ) && m.start( ) == m.end( ) )
				{
					// no empty leading substring included for zero-width match
					// at the beginning of the input char sequence.
					continue;
				}
				Segment match = substring( index , m.start( ) );
				matchList.add( match );
				index = m.end( );
			}
			else if( matchList.size( ) == limit - 1 )
			{ // last one
				Segment match = substring( index , value.length( ) );
				matchList.add( match );
				index = m.end( );
			}
		}

		// If no match was found, return this
		if( index == 0 )
			return new Segment[ ] { this };

		// Add remaining segment
		if( !matchLimited || matchList.size( ) < limit )
			matchList.add( substring( index , value.length( ) ) );

		// Construct result
		int resultSize = matchList.size( );
		if( limit == 0 )
			while( resultSize > 0 && matchList.get( resultSize - 1 ).equals( "" ) )
				resultSize--;
		Segment[ ] result = new Segment[ resultSize ];
		return matchList.subList( 0 , resultSize ).toArray( result );
	}

	public Segment[ ] split( String regex )
	{
		return split( regex , 0 );
	}

	public Segment trim( )
	{
		int len = value.length( );
		int st = 0;

		while( ( st < len ) && ( value.charAt( st ) <= ' ' ) )
		{
			st++;
		}
		while( ( st < len ) && ( value.charAt( len - 1 ) <= ' ' ) )
		{
			len--;
		}
		return ( ( st > 0 ) || ( len < value.length( ) ) ) ? substring( st , len ) : this;
	}

	public String toString( )
	{
		return value.toString( );
	}

	public char[ ] toCharArray( )
	{
		return value.toCharArray( );
	}

	public int parseAsInteger( )
	{
		try
		{
			return Integer.parseInt( value );
		}
		catch( NumberFormatException ex )
		{
			throw new SegmentParseExpectedException( this , ExpectedTypes.INTEGER );
		}
	}

	public int parseAsUnsignedInteger( )
	{
		try
		{
			int result = Integer.parseInt( value );
			if( result >= 0 )
			{
				return result;
			}
		}
		catch( NumberFormatException ex )
		{
		}
		throw new SegmentParseExpectedException( this , ExpectedTypes.UNSIGNED_INTEGER );
	}

	public float parseAsFloat( )
	{
		try
		{
			return Float.parseFloat( value );
		}
		catch( NumberFormatException ex )
		{
			throw new SegmentParseExpectedException( this , ExpectedTypes.FLOAT );
		}
	}

	public float parseAsUnsignedFloat( )
	{
		try
		{
			float result = Float.parseFloat( value );
			if( result >= 0 )
			{
				return result;
			}
		}
		catch( NumberFormatException ex )
		{
		}
		throw new SegmentParseExpectedException( this , ExpectedTypes.UNSIGNED_FLOAT );
	}

	public double parseAsDouble( )
	{
		try
		{
			return Double.parseDouble( value );
		}
		catch( NumberFormatException ex )
		{
			throw new SegmentParseExpectedException( this , ExpectedTypes.DOUBLE );
		}
	}

	public double parseAsUnsignedDouble( )
	{
		try
		{
			double result = Double.parseDouble( value );
			if( result >= 0 )
			{
				return result;
			}
		}
		catch( NumberFormatException ex )
		{
		}
		throw new SegmentParseExpectedException( this , ExpectedTypes.UNSIGNED_DOUBLE );
	}

	public <V> V parseAsAnyOf( Map<String, V> map )
	{
		V result = map.get( value );
		if( result == null )
		{
			throw new SegmentParseExpectedException( this , map.values( ).toArray( ) );
		}
		return result;
	}

	public <V> V parseToLowerCaseAsAnyOf( Map<String, V> map )
	{
		V result = map.get( value.toLowerCase( ) );
		if( result == null )
		{
			throw new SegmentParseExpectedException( this , map.keySet( ).toArray( ) );
		}
		return result;
	}

	public <T> T parseAsAnyOf( Function<Segment, ? extends T> ... parsers )
	{
		List<Object> expectedTypes = new LinkedList<Object>( );

		for( Function<Segment, ? extends T> parser : parsers )
		{
			try
			{
				return parser.apply( this );
			}
			catch( SegmentParseExpectedException ex )
			{
				expectedTypes.addAll( Arrays.asList( ex.expectedItems ) );
			}
		}

		throw new SegmentParseExpectedException( this , expectedTypes.toArray( ) );
	}

	/**
	 * @return the lines of {@link #sourceSegment} this {@code Segment} occurs in,
	 *         interspersed with lines of {@code ^^^} underlining the region it covers
	 */
	public String underlineInContext( )
	{
		StringBuilder sb = new StringBuilder( );
		Segment[ ] lines = sourceSegment.split( "\r\n|\r|\n" );

		for( Segment line : lines )
		{
			if( line.startLine < startLine || line.startLine > endLine )
			{
				continue;
			}
			sb.append( line ).append( System.lineSeparator( ) );
			int k = 0;
			if( startLine == line.startLine )
			{
				while( k < startCol )
				{
					sb.append( ' ' );
					k++;
				}
			}
			if( line.startLine < endLine )
			{
				while( k < line.length( ) )
				{
					sb.append( '^' );
					k++;
				}
			}
			else if( endLine == line.startLine )
			{
				while( k <= endCol )
				{
					sb.append( '^' );
					k++;
				}
			}
			if( line.startLine < endLine )
			{
				sb.append( System.lineSeparator( ) );
			}
		}
		return sb.toString( );
	}
}

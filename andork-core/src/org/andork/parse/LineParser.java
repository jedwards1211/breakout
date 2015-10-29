package org.andork.parse;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.andork.collect.MapLiteral;
import org.andork.func.CharPredicate;

public class LineParser
{
	public LineParser( Segment line )
	{
		this.line = line;
		this.i = 0;
	}

	/**
	 * The line being parsed.
	 */
	protected Segment line;
	/**
	 * The current index of the parser in {@link #line}.
	 */
	protected int i;

	/**
	 * The index at which terminals were expected.
	 */
	protected int expectedIndex;
	/**
	 * Terminals which were expected at {@link #expectedIndex}. This is used to catalogue all expected terminals
	 * when multiple productions applicable at {@link #expectedIndex} fail.
	 */
	protected final LinkedHashSet<Object> expectedItems = new LinkedHashSet<>( );

	private static int nullZero( Integer value )
	{
		return value == null ? 0 : value;
	}

	public static final Pattern UNSIGNED_DOUBLE_LITERAL = Pattern.compile( "\\d+(\\.\\d*)?|\\.\\d+" );
	public static final Map<Character, Double> SIGN_SIGNUMS = new MapLiteral<Character, Double>( )
		.map( '-' , -1.0 ).map( '+' , 1.0 );

	public void addExpected( SegmentParseExpectedException expected )
	{
		int index = nullZero( expected.segment.sourceIndex ) - nullZero( line.sourceIndex );

		if( index > expectedIndex )
		{
			expectedItems.clear( );
			expectedIndex = index;
		}
		if( index == expectedIndex )
		{
			expectedItems.addAll( Arrays.asList( expected.expectedItems ) );
		}
	}

	public void throwAllExpected( Runnable production )
	{
		try
		{
			production.run( );
		}
		catch( SegmentParseExpectedException ex )
		{
			throwAllExpected( ex );
		}
	}

	public void throwAllExpected( )
	{
		throwAllExpected( ( SegmentParseExpectedException ) null );
	}

	public void throwAllExpected( SegmentParseExpectedException finalEx )
	{
		if( finalEx != null )
		{
			addExpected( finalEx );
		}
		if( !expectedItems.isEmpty( ) )
		{
			throw new SegmentParseExpectedException(
				line.charAtAsSegment( expectedIndex ) , expectedItems.toArray( ) );
		}
	}

	public void expect( char c )
	{
		line.charAtAsSegment( i ).parseAs( Character.toString( c ) );
		i++;
	}

	public void expectIgnoreCase( char c )
	{
		line.charAtAsSegment( i ).parseAsIgnoreCase( Character.toString( c ) );
		i++;
	}

	public void expect( String s )
	{
		line.substring( i , Math.min( i + s.length( ) , line.length( ) ) ).parseAs( s );
		i += s.length( );
	}

	public void expectIgnoreCase( String s )
	{
		line.substring( i , Math.min( i + s.length( ) , line.length( ) ) ).parseAsIgnoreCase( s );
		i += s.length( );
	}

	public Segment expect( Pattern p , Object ... expectedItems )
	{
		SegmentMatcher m = new SegmentMatcher( line , p );
		m.region( i , line.length( ) );
		if( !m.find( ) || m.start( ) > i )
		{
			throw new SegmentParseExpectedException( line.charAtAsSegment( i ) , expectedItems );
		}
		i = m.end( );
		return m.group( );
	}

	public char expect( CharPredicate p , Object ... expectedItems )
	{
		char c;
		if( i == line.length( ) || !p.test( c = line.charAt( i ) ) )
		{
			throw new SegmentParseExpectedException( line.charAtAsSegment( i ) , expectedItems );
		}
		i++;
		return c;
	}

	public void comma( )
	{
		expect( ',' );
	}

	public void forwardSlash( )
	{
		expect( '/' );
	}

	public void hashmark( )
	{
		expect( '#' );
	}

	public void semicolon( )
	{
		expect( ';' );
	}

	public void colon( )
	{
		expect( ':' );
	}

	public Segment whitespace( )
	{
		return oneOrMore( Character::isWhitespace , ExpectedTypes.WHITESPACE );
	}

	public Segment nonwhitespace( )
	{
		return oneOrMore( c -> !Character.isWhitespace( c ) , ExpectedTypes.NON_WHITESPACE );
	}

	public Segment oneOrMore( CharPredicate c , Object ... expectedItems )
	{
		int start = i;

		while( i < line.length( ) && c.test( line.charAt( i ) ) )
		{
			i++;
		}

		if( i == start )
		{
			throw new SegmentParseExpectedException( line.charAtAsSegment( i ) , expectedItems );
		}

		return line.substring( start , i );
	}

	public double unsignedDoubleLiteral( )
	{
		return Double.parseDouble( expect( UNSIGNED_DOUBLE_LITERAL , ExpectedTypes.UNSIGNED_DOUBLE ).toString( ) );
	}

	public double doubleLiteral( )
	{
		Double signum = maybeR( ( ) -> oneOf( SIGN_SIGNUMS ) );
		if( signum == null )
		{
			signum = 1.0;
		}
		return signum * unsignedDoubleLiteral( );
	}

	public Segment remaining( )
	{
		Segment result = line.substring( i );
		i = line.length( );
		return result;
	}

	public Void endOfLine( )
	{
		if( i != line.length( ) )
		{
			throw new SegmentParseExpectedException( line.substring( i ) , ExpectedTypes.END_OF_LINE );
		}
		return null;
	}

	public <V> V oneOf( Map<Character, V> map )
	{
		char c;
		if( i == line.length( ) || !map.containsKey( c = line.charAt( i ) ) )
		{
			throw new SegmentParseExpectedException( line.charAtAsSegment( i ) , map.keySet( ).toArray( ) );
		}
		i++;
		return map.get( c );
	}

	public <V> V oneOfLowercase( Map<Character, V> map )
	{
		char c;
		if( i == line.length( ) || !map.containsKey( c = Character.toLowerCase( line.charAt( i ) ) ) )
		{
			throw new SegmentParseExpectedException( line.charAtAsSegment( i ) , map.keySet( ).toArray( ) );
		}
		i++;
		return map.get( c );
	}

	public <V> V oneOfIgnoreCase( Collection<? extends Map.Entry<String, V>> map )
	{
		for( Map.Entry<String, V> entry : map )
		{
			if( maybe( ( ) -> expectIgnoreCase( entry.getKey( ) ) ) )
			{
				return entry.getValue( );
			}
		}
		throwAllExpected( );
		return null;
	}

	public <V> V oneOfLowercase( Map<Character, V> map , V elseValue )
	{
		V value = i == line.length( ) ? null : map.get( Character.toLowerCase( line.charAt( i ) ) );
		if( value != null )
		{
			i++;
		}
		return value != null ? value : elseValue;
	}

	/**
	 * Runs the given production and returns {@code true} if it completed successfully and {@code false} if it threw a
	 * {@link SegmentParseExpectedException}.
	 * 
	 * @param production
	 *            the production to run.
	 * @return {@code true} if it completed successfully and {@code false} if it threw a
	 *         {@link SegmentParseExpectedException}.
	 */
	public boolean maybe( Runnable production )
	{
		int start = i;
		try
		{
			production.run( );
			return true;
		}
		catch( SegmentParseExpectedException ex )
		{
			if( i > start )
			{
				throwAllExpected( ex );
			}
			addExpected( ex );
			return false;
		}
	}

	public <R> R maybeR( Supplier<? extends R> production )
	{
		int start = i;
		try
		{
			return production.get( );
		}
		catch( SegmentParseExpectedException ex )
		{
			if( i > start )
			{
				throwAllExpected( ex );
			}
			addExpected( ex );
			return null;
		}
	}

	/**
	 * Runs the productions one by one until one completes without throwing
	 * a {@link SegmentParseExpectedException}.
	 * 
	 * @param productions
	 * @throws SegmentParseExpectedException
	 *             with all accumulated expected values if either:
	 *             <ul>
	 *             <li>One of the productions threw a {@link SegmentParseExpectedException} after advancing the current
	 *             index {@link #i}
	 *             <li>All of the productions threw {@link SegmentParseExpectedException}s.
	 *             </ul>
	 */
	@SafeVarargs
	public final void oneOf( Runnable ... productions )
	{
		int start = i;
		for( Runnable production : productions )
		{
			try
			{
				production.run( );
				return;
			}
			catch( SegmentParseExpectedException ex )
			{
				if( i > start )
				{
					throwAllExpected( ex );
				}
				addExpected( ex );
			}
		}
		throwAllExpected( );
	}

	@SafeVarargs
	public final <R> R oneOfR( Supplier<? extends R> ... productions )
	{
		int start = i;
		for( Supplier<? extends R> production : productions )
		{
			try
			{
				return production.get( );
			}
			catch( SegmentParseExpectedException ex )
			{
				if( i > start )
				{
					throwAllExpected( ex );
				}
				addExpected( ex );
			}
		}
		throwAllExpected( );
		return null;
	}

	/**
	 * Runs the productions one by one until one completes without throwing
	 * a {@link SegmentParseExpectedException}. Unlike {@link #oneOf(Runnable...)}, this method will not throw
	 * a {@link SegmentParseExpectedException} if one of the productions throws one after advancing the current index
	 * {@link #i},
	 * rather it will reset the current index and try the next production.
	 * 
	 * @param productions
	 * @throws SegmentParseExpectedException
	 *             with all accumulated expected values at the farthest starting index of all productions' thrown
	 *             {@link SegmentParseExpectedException}s.
	 */
	@SafeVarargs
	public final void oneOfWithLookahead( Runnable ... productions )
	{
		int start = i;

		for( Runnable production : productions )
		{
			try
			{
				production.run( );
				return;
			}
			catch( SegmentParseExpectedException ex )
			{
				addExpected( ex );
				i = start;
			}
		}
		throwAllExpected( );
	}
}

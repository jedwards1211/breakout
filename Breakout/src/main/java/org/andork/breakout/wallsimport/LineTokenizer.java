package org.andork.breakout.wallsimport;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andork.func.CharPredicate;

public class LineTokenizer
{
	private String				line;
	private int					position;

	private int					lineNumber;

	public static final Pattern	UNSIGNED_FLOATING_POINT			= Pattern.compile( "^(\\d+(\\.\\d*)?|\\.\\d+)" );
	public static final Pattern	SIGNED_FLOATING_POINT			= Pattern.compile( "^[-+]?(\\d+(\\.\\d*)?|\\.\\d+)" );
	public static final Pattern	UNSIGNED_INTEGER				= Pattern.compile( "^\\d+" );
	public static final Pattern	SIGNED_INTEGER					= Pattern.compile( "^[-+]?\\d+" );
	public static final Pattern	SIGN							= Pattern.compile( "^[-+]" );
	public static final Pattern	UNSIGNED_FLOATING_POINT_OR_OMIT	= Pattern.compile( "^(\\d+(\\.\\d*)?|\\.\\d+|-+)" );
	public static final Pattern	SIGNED_FLOATING_POINT_OR_OMIT	= Pattern
																	.compile( "^([-+]?(\\d+(\\.\\d*)?|\\.\\d+))|-+" );
	public static final Pattern	OMIT							= Pattern.compile( "^-+" );

	public LineTokenizer( String line , int lineNumber )
	{
		this.line = line;
		this.lineNumber = lineNumber;
		this.position = 0;
	}

	/**
	 * @return {@code true} if the end of the line has been reached.
	 */
	public boolean isAtEnd( )
	{
		return position >= line.length( );
	}

	/**
	 * @return the line number.
	 */
	public int lineNumber( )
	{
		return lineNumber;
	}

	/**
	 * @return the current column number, an index within the line (or the size of the line if the end has been
	 *         reached).
	 */
	public int columnNumber( )
	{
		return position;
	}

	public static boolean isNotWhitespace( char c )
	{
		return !Character.isWhitespace( c );
	}

	/**
	 * @param p
	 *            a predicate that returns {@code true} iff the character is of the type you want to pull.
	 * @return a {@link Token} containing the longest contiguous block of characters that match the given predicate
	 *         starting at the current position, or {@code null} if the character at the current
	 *         position doesn't match the given predicate.
	 */
	public Token pull( CharPredicate p )
	{
		for( int i = position ; i <= line.length( ) ; i++ )
		{
			if( i == line.length( ) || !p.test( line.charAt( i ) ) )
			{
				if( i == position )
				{
					return null;
				}
				Token result = new Token( lineNumber , position , lineNumber , i - 1 , line.substring( position , i ) );
				position = i;
				return result;
			}
		}
		return null;
	}

	/**
	 * @return a {@link Token} containing the character at the current position if it matches the given predicate,
	 *         or {@code null} otherwise
	 */
	public Token pullCharacter( CharPredicate p )
	{
		if( position < line.length( ) && p.test( line.charAt( position ) ) )
		{
			Token result = new Token( lineNumber , position , lineNumber , position , Character.toString( line
				.charAt( position ) ) );
			position++;
			return result;
		}
		return null;
	}

	/**
	 * @return a {@link Token} containing the given character if it occurs at the current
	 *         position, or {@code null} otherwise
	 */
	public Token pull( char c )
	{
		return pullCharacter( ch -> c == ch );
	}

	/**
	 * @return a {@link Token} containing the character at the current
	 *         position, or {@code null} if the end of the line has been reached
	 */
	public Token pullCharacter( )
	{
		if( position < line.length( ) )
		{
			Token result = new Token( lineNumber , position , lineNumber , position , Character.toString( line
				.charAt( position ) ) );
			position++;
			return result;
		}
		return null;
	}

	/**
	 * @param p
	 *            the pattern to match. This procedure will run most efficiently if the pattern begins with {@code ^},
	 *            because otherwise it will try to find a match after the current position.
	 * @return a {@link Token} of text starting at the current position that matches the given {@link Pattern}, or
	 *         {@code null} if no text matches the given pattern.
	 */
	public Token pull( Pattern p )
	{
		Matcher m = p.matcher( line );
		m.region( position , line.length( ) );
		if( m.find( ) && m.start( ) == position )
		{
			position = m.end( );
			return new Token( lineNumber , m.start( ) , lineNumber , m.end( ) - 1 , line.substring( m.start( ) ,
				m.end( ) ) );
		}
		return null;
	}

	/**
	 * @param pattern
	 *            the pattern to match. This procedure will run most efficiently if the pattern begins with {@code ^},
	 *            because otherwise it will try to find a match after the current position.
	 * @param parser
	 *            a function that takes a token matching {@code pattern} and returns the parsed value
	 * @return a {@link ValueToken} from text starting at the current position that matches the given {@code pattern}
	 *         parsed with the given {@code parser}, or {@code null} if no text matches the given pattern.
	 */
	public <V> ValueToken<V> pull( Pattern pattern , Function<String, ? extends V> parser )
	{
		Token token = pull( pattern );
		return token == null ? null : new ValueToken<>( parser.apply( token.image ) , token );
	}
}
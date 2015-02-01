package org.breakout.parse;

import java.text.Format;
import java.text.ParsePosition;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andork.func.CharPredicate;

public class LineTokenizer
{
	private String	line;
	private int		position;

	private int		lineNumber;

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

	public int position( )
	{
		return position;
	}

	public LineTokenizer position( int position )
	{
		this.position = position;
		return this;
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

	/**
	 * Pulls a {@link ValueToken} starting at the current position formatted by a given {@link GenericFormat}.
	 * 
	 * @param format
	 *            the format to use. Its {@link Format#parseObject(String, ParsePosition)} will be called at the
	 *            current position.
	 * @return a {@link ValueToken} containing the image and value that the format parsed, or {@code null} if it didn't
	 *         return a value.
	 */
	public <V> ValueToken<V> pull( GenericFormat<V> format )
	{
		ParsePosition pos = new ParsePosition( position );
		V value = format.parseObject( line , pos );
		ValueToken<V> result = value == null ? null : new ValueToken<V>(
			lineNumber , position , lineNumber , pos.getIndex( ) - 1 ,
			line.substring( position , pos.getIndex( ) ) , value );
		position = pos.getIndex( );
		return result;
	}

	/**
	 * Pulls the longest {@link ValueToken} starting at the current position whose image is a key in the given map.
	 * 
	 * @param map
	 *            a map whose keys represent valid tokens that can be pulled and their associated value representations.
	 * @param maxLength
	 *            the maximum token length to pull; the method will stop searching after this length.
	 * @return a {@link ValueToken} whose image is the longest key in {@code map} starting at the current position and
	 *         whose value is the associated value in {@code map}, or null if no keys in {@code map <= maxLength} were
	 *         found.
	 */
	public <V> ValueToken<V> pull( Map<String, ? extends V> map , int maxLength )
	{
		for( int i = Math.min( line.length( ) , position + maxLength ) ; i >= position ; i-- )
		{
			String s = line.substring( position , i );
			V value = map.get( s );
			if( value != null )
			{
				ValueToken<V> result = new ValueToken<V>( lineNumber , position , lineNumber , i , s , value );
				position = i;
				return result;
			}
		}
		return null;
	}

	/**
	 * Pulls the longest {@link ValueToken} starting at the current position whose image (to lowercase) is a key in the
	 * given map. This is used for parsing units.
	 * 
	 * @param map
	 *            a map whose keys represent valid tokens that can be pulled and their associated value representations.
	 * @param maxLength
	 *            the maximum token length to pull; the method will stop searching after this length.
	 * @return a {@link ValueToken} whose image is the longest key in {@code map} starting at the current position and
	 *         whose value is the associated value in {@code map}, or null if no keys in {@code map <= maxLength} were
	 *         found.
	 */
	public <V> ValueToken<V> pullLowercase( Map<String, ? extends V> map , int maxLength )
	{
		for( int i = Math.min( line.length( ) , position + maxLength ) ; i >= position ; i-- )
		{
			String s = line.substring( position , i ).toLowerCase( );
			V value = map.get( s );
			if( value != null )
			{
				ValueToken<V> result = new ValueToken<V>( lineNumber , position , lineNumber , i , s , value );
				position = i;
				return result;
			}
		}
		return null;
	}

	/**
	 * Pulls all remaining text from the current position to the end of the line.
	 * 
	 * @return a {@link Token} representing the text from the current position to the end of the line.
	 */
	public Token pullRemaining( )
	{
		return pull( c -> true );
	}

	/**
	 * If there is a quote at the current position, pulls up through the end quote, taking some escaped characters into
	 * account.
	 * 
	 * @return a {@link ValueToken} whose value represents the text pulled (if it was quoted, the value will be
	 *         unescaped).
	 */
	public ValueToken<String> pullNonWhitespaceOrQuoted( )
	{
		if( line.charAt( position ) == '"' )
		{
			StringBuilder sb = new StringBuilder( );

			for( int i = position + 1 ; i < line.length( ) ; i++ )
			{
				char c = line.charAt( i );
				switch( c )
				{
				case '\\':
					if( i == line.length( ) - 1 )
					{
						return null;
					}
					i++;
					c = line.charAt( i );
					switch( c )
					{
					case 'n':
						sb.append( '\n' );
						continue;
					case 'r':
						sb.append( '\r' );
						continue;
					case 't':
						sb.append( '\t' );
						continue;
					default:
						sb.append( line.charAt( i ) );
						continue;
					}
				case '"':
					ValueToken<String> result = new ValueToken<String>(
						lineNumber , position , lineNumber , i , line.substring( position , i + 1 ) , sb.toString( ) );
					position = i + 1;
					return result;
				default:
					sb.append( c );
					continue;
				}
			}

			return null;
		}
		else
		{
			Token token = pull( LineTokenizer::isNotWhitespace );
			return token != null ? new ValueToken<String>( token.image , token ) : null;
		}
	}
}
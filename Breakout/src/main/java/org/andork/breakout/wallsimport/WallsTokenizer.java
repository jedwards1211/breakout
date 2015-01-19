package org.andork.breakout.wallsimport;

import org.andork.func.CharPredicate;

public class WallsTokenizer
{
	private String	line;
	private int		position;

	public WallsTokenizer( String line )
	{
		this.line = line;
		this.position = 0;
	}

	/**
	 * @return the character at the current position in the line, or {@code '\0'} if the end of the line has been
	 *         reached.
	 */
	public char current( )
	{
		return position >= line.length( ) ? '\0' : line.charAt( position );
	}

	/**
	 * @return the next token in the line, or {@code null} if there are no remaining tokens. Tokens may not contain
	 *         spaces or the characters =;#" after the first character unless they are inside quotes (" must be escaped
	 *         by a \). = and ; outside of quotes are always counted as a token by themselves.
	 */
	public WallsToken nextToken( )
	{
		skipWhitespace( );

		if( position >= line.length( ) )
		{
			return null;
		}

		if( current( ) == '=' || current( ) == ';' )
		{
			WallsToken token = new WallsToken( position , position , Character.toString( current( ) ) );
			position++;
			return token;
		}

		StringBuilder sb = new StringBuilder( );
		int startColumn = position;

		if( current( ) == '#' )
		{
			sb.append( '#' );
			position++;
		}

		while( position < line.length( ) )
		{
			char c = line.charAt( position );
			if( Character.isWhitespace( c ) || c == '=' || c == ';' || c == '#' )
			{
				int endColumn = position;
				return new WallsToken( startColumn , endColumn , sb.toString( ) );
			}
			else if( c == '"' )
			{
				position++;
				readQuotedText( sb );
			}
			else
			{
				position++;
				sb.append( c );
			}
		}

		return new WallsToken( startColumn , position - 1 , sb.toString( ) );
	}

	private void readQuotedText( StringBuilder sb )
	{
		while( position < line.length( ) )
		{
			char c = line.charAt( position );
			switch( c )
			{
			case '\\':
				position++;
				if( position < line.length( ) )
				{
					c = line.charAt( position );
					switch( c )
					{
					case 'n':
						sb.append( '\n' );
						break;
					case 'r':
						sb.append( '\r' );
						break;
					case 't':
						sb.append( '\t' );
						break;
					default:
						sb.append( c );
						break;
					}
				}
				break;
			case '"':
				position++;
				return;
			}
			position++;
		}
	}

	private void skipWhitespace( )
	{
		while( position < line.length( ) && Character.isWhitespace( line.charAt( position ) ) )
		{
			position++;
		}
	}

	/**
	 * @return the current position, an index within the line (or the size of the line if the end has been reached).
	 */
	public int position( )
	{
		return position;
	}

	/**
	 * @return a substring of the line from the current position up to the next character matching the given predicate
	 *         (exclusive) or the end of the line, or {@code null} if the entire line has already been read.
	 * 
	 * @param p
	 *            the stop predicate.
	 */
	public String peekUntil( CharPredicate p )
	{
		if( position >= line.length( ) )
		{
			return null;
		}
		StringBuilder sb = new StringBuilder( );
		int i = this.position;
		char c;
		while( i < line.length( ) && !p.test( c = line.charAt( i++ ) ) )
		{
			sb.append( c );
		}
		return sb.toString( );
	}

	/**
	 * @return a substring of the line from the current position up to the next occurrence of the given character
	 *         (exclusive) or the end of the line, or {@code null} if the entire line has already been read.
	 * 
	 * @param c
	 *            the character to stop at.
	 */
	public String peekUntil( char c )
	{
		return peekUntil( ch -> ch == c );
	}

	/**
	 * Reads up to the next character matching the given predicate, updating the position.
	 * 
	 * @return a substring of the line from the current position up to the next character matching the given predicate
	 *         (exclusive) or the end of the line, or {@code null} if the entire line has already been read.
	 * 
	 * @param p
	 *            the stop predicate.
	 */
	public String readUntil( CharPredicate p )
	{
		String result = peekUntil( p );
		if( result != null )
		{
			position += result.length( );
		}
		return result;
	}

	/**
	 * Reads up to the next occurrence of a given character, updating the position.
	 * 
	 * @return a substring of the line from the current position up to the next occurrence of the given character
	 *         (exclusive) or the end of the line, or {@code null} if the entire line has already been read.
	 * 
	 * @param c
	 *            the character to stop at.
	 */
	public String readUntil( char c )
	{
		return readUntil( ch -> ch == c );
	}

	/**
	 * Reads the remaining text in the line, updating the position to the end of the line.
	 * 
	 * @return the remaining text in the line, or {@code null} if the entire line has already been read.
	 */
	public String readRemaining( )
	{
		if( position >= line.length( ) )
		{
			return null;
		}
		String result = line.substring( position );
		position = line.length( );
		return result;
	}
}

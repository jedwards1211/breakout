package org.andork.io;

import java.util.*;
import java.util.Collection;

/**
 * A comma-separated value formatter and parser.
 * 
 * @author James
 */
public class CSV
{
	private char	separator		= ',';
	private char	quote			= '"';
	private boolean	trimWhitespace	= true;

	public CSV( )
	{

	}

	private static void requireNotNullTerminator( char c )
	{
		if( c == '\0' )
		{
			throw new IllegalArgumentException( "you may not use the null terminator character" );
		}
	}

	/**
	 * @return the separator character. The default is a comma, but you may change it.
	 */
	public char separator( )
	{
		return separator;
	}

	/**
	 * Sets the separator character.
	 * 
	 * @param separator
	 *            the new separator character.
	 * @return this {@link CSV}, for chaining.
	 * @throws IllegalArgumentException
	 *             if {@code separator} is the null terminator or the same as {@link #quote()}.
	 */
	public CSV separator( char separator )
	{
		if( separator == quote )
		{
			throw new IllegalArgumentException( "the separator and quote characters must not be the same" );
		}
		requireNotNullTerminator( separator );
		this.separator = separator;
		return this;
	}

	/**
	 * @return the quote character. The default is a double quote ("), but you may change it.
	 */
	public char quote( )
	{
		return quote;
	}

	/**
	 * Sets the quote character.
	 * 
	 * @param quote
	 *            the new quote character.
	 * @return this {@link CSV}, for chaining.
	 * @throws IllegalArgumentException
	 *             if {@code quote} is the null terminator or the same as {@link #separator()}.
	 */
	public CSV quote( char quote )
	{
		if( quote == separator )
		{
			throw new IllegalArgumentException( "the separator and quote characters must not be the same" );
		}
		requireNotNullTerminator( quote );
		this.quote = quote;
		return this;
	}

	/**
	 * @return whether leading and trailing whitespace in fields will be trimmed.
	 */
	public boolean trimWhitespace( )
	{
		return trimWhitespace;
	}

	/**
	 * Sets whether leading and trailing whitespace in fields will be trimmed.
	 * 
	 * @param trimWhitespace
	 *            if {@code true}, leading and trailing whitespace in fields will be trimmed.
	 * @return this {@link CSV}, for chaining.
	 */
	public CSV trimWhitespace( boolean trimWhitespace )
	{
		this.trimWhitespace = trimWhitespace;
		return this;
	}

	/**
	 * Parses a line of CSV data.
	 * 
	 * @param line
	 *            the line to parse.
	 * @return a list of fields parsed from {@code line}.
	 */
	public List<String> parseLine( String line )
	{
		ArrayList<String> result = new ArrayList<>( );
		parseLine( line , result );
		return result;
	}

	/**
	 * Parses a line of CSV data.
	 * 
	 * @param line
	 *            the line to parse.
	 * @param result
	 *            the {@link Collection} to place the fields parsed from {@code line} into.
	 */
	public void parseLine( String line , Collection<String> result )
	{
		StringBuilder sb = new StringBuilder( );

		int i = 0;

		boolean inQuote = false;

		while( i <= line.length( ) )
		{
			char c = i < line.length( ) ? line.charAt( i ) : '\0';

			if( c == quote )
			{
				if( inQuote )
				{
					if( i == line.length( ) - 1 || line.charAt( i + 1 ) != quote )
					{
						inQuote = false;
					}
					else
					{
						i++;
						sb.append( quote );
					}
				}
				else
				{
					inQuote = true;
				}
			}
			else if( c == separator || c == '\0' )
			{
				if( inQuote )
				{
					sb.append( separator );
				}
				else
				{
					if( trimWhitespace )
					{
						trimWhitespace( sb );
					}
					result.add( sb.toString( ) );
					sb.delete( 0 , sb.length( ) );
				}
			}
			else
			{
				sb.append( c );
			}

			i++;
		}
	}

	private static void trimWhitespace( StringBuilder sb )
	{
		// find the first non-whitespace character
		int i;
		for( i = 0 ; i < sb.length( ) ; i++ )
		{
			if( !Character.isWhitespace( sb.charAt( i ) ) )
			{
				break;
			}
		}
		// delete up to the first non-whitespace character
		if( i > 0 )
		{
			sb.delete( 0 , i );
		}

		// find the last non-whitespace character
		for( i = sb.length( ) - 1 ; i >= 0 ; i-- )
		{
			if( !Character.isWhitespace( sb.charAt( i ) ) )
			{
				break;
			}
		}
		// delete from last non-whitespace character to the end
		if( i < sb.length( ) - 1 )
		{
			sb.delete( i + 1 , sb.length( ) );
		}
	}

	/**
	 * Formats a list of fields into a line of CSV data.
	 * 
	 * @param fields
	 *            the fields to format.
	 * @return a line of CSV containing the {@code fields} formatted according to the settings of this {@link CSV}.
	 */
	public String formatLine( Collection<String> fields )
	{
		StringBuilder sb = new StringBuilder( );

		for( String field : fields )
		{
			if( sb.length( ) > 0 )
			{
				sb.append( separator );
			}
			if( needToQuoteField( field ) )
			{
				sb.append( quote );
				for( int i = 0 ; i < field.length( ) ; i++ )
				{
					char c = field.charAt( i );
					if( c == quote )
					{
						sb.append( quote );
					}
					// if c is a quote, two quotes in a row will get appended.
					// this is what we want.
					sb.append( c );
				}
				sb.append( quote );
			}
			else
			{
				sb.append( field );
			}
		}

		return sb.toString( );
	}

	private boolean needToQuoteField( String field )
	{
		return field.indexOf( separator ) >= 0 ||
			field.indexOf( quote ) >= 0 ||
			// field begins or ends with whitespace and unquoted whitespace is trimmed
			( trimWhitespace && field.length( ) > 0 &&
			( Character.isWhitespace( field.charAt( 0 ) ) ||
			Character.isWhitespace( field.charAt( field.length( ) - 1 ) ) ) );
	}
}

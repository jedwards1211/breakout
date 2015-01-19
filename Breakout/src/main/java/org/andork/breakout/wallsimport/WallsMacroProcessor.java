package org.andork.breakout.wallsimport;

import java.util.HashMap;
import java.util.Map;

public class WallsMacroProcessor
{
	private final Map<String, String>	macros	= new HashMap<>( );

	public void replaceMacroReferences( StringBuilder line )
	{
		int fromIndex = 0;

		while( fromIndex < line.length( ) )
		{
			int macroIndex = line.indexOf( "$" );

			if( macroIndex < 0 )
			{
				return;
			}

			if( macroIndex + 1 < line.length( ) && line.charAt( macroIndex + 1 ) == '(' )
			{
				int length = replaceMacroReference( line , macroIndex );
				fromIndex = macroIndex + length;
			}
			else
			{
				fromIndex = macroIndex + 1;
			}
		}
	}

	/**
	 * Replaces a macro reference at {@code start} in the given {@code line}.<br>
	 * Precondition: {@code line.substring( start ).startsWith( "$(" )}
	 * 
	 * @param line
	 *            the line in which the reference occurs. It will be replaced with the defined macro value.
	 * @param start
	 *            the start index of the reference in {@code line} to replace.
	 * @return the length of the macro value the reference was replaced with.
	 * @throws RuntimeException
	 *             if the macro reference contains a space, if the referenced macro is not defined,
	 *             or if the reference is missing a closing parenthesis.
	 */
	private int replaceMacroReference( StringBuilder line , int start )
	{
		// precondition: line.substring( start ).startsWith( "$(" );

		for( int i = start + 2 ; i < line.length( ) ; i++ )
		{
			char next = line.charAt( i );

			if( Character.isWhitespace( next ) )
			{
				// TODO: replace this with something more formal
				throw new RuntimeException( "no spaces are allowed inside macro references" );
			}
			switch( next )
			{
			case '$':
				if( i + 1 < line.length( ) && line.charAt( i + 1 ) == '(' )
				{
					int length = replaceMacroReference( line , i );
					i += length;
				}
				break;
			case ')':
				String macro = line.substring( start + 2 , i );
				String replacement = macros.get( macro );
				if( replacement == null )
				{
					// TODO: replace this with something more formal
					throw new RuntimeException( "undefined macro: '" + macro + "'" );
				}
				line.replace( start , i + 1 , replacement );
				return replacement.length( );
			}
		}

		// TODO: replace this with something more formal
		throw new RuntimeException( "missing closing parenthesis for macro reference" );
	}
}

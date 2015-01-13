package org.andork.breakout.table;

import java.util.function.Function;

import org.andork.i18n.I18n;

@SuppressWarnings( "serial" )
public abstract class ParseNote implements Function<I18n, String>
{
	private ParseStatus	status;

	public ParseNote( ParseStatus status )
	{
		this.status = status;
	}

	public ParseStatus getStatus( )
	{
		return status;
	}

	public static ParseNote forMessageKey( ParseStatus status , String messageKey )
	{
		return new ParseNote( status ) {
			@Override
			public String apply( I18n t )
			{
				return t.forClass( ParseNote.class ).getString( messageKey );
			}
		};
	}

	public static ParseNote forMessageKey( ParseStatus status , String messageKey , Object arg )
	{
		return new ParseNote( status ) {
			@Override
			public String apply( I18n t )
			{
				return t.forClass( ParseNote.class ).getFormattedString( messageKey , arg );
			}
		};
	}
}
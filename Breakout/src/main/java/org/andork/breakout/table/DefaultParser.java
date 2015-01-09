package org.andork.breakout.table;

import java.util.function.Function;

import org.andork.i18n.I18n;

public class DefaultParser implements Function<String, ParsedTextWithValue>
{
	Function<String, Object>	parseOrThrow;

	public DefaultParser( Function<String, Object> parseOrThrow )
	{
		super( );
		this.parseOrThrow = parseOrThrow;
	}

	@Override
	public ParsedTextWithValue apply( String t )
	{
		try
		{
			return new ParsedTextWithValue( t , null , parseOrThrow.apply( t ) );
		}
		catch( ParseNote note )
		{
			return new ParsedTextWithValue( t , note , null );
		}
		catch( Exception ex )
		{
			ParseNote note = new ParseNote( ParseStatus.ERROR ) {
				@Override
				public String apply( I18n t )
				{
					return t.forClass( ParseNote.class ).getFormattedString( "generalException" ,
						ex.getClass( ).getSimpleName( ) , ex.getLocalizedMessage( ) );
				}
			};
			return new ParsedTextWithValue( t , note , null );
		}
	}
}

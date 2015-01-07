package org.andork.breakout.table;

import java.util.function.Function;

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
		catch( Exception ex )
		{
			ParseNote note = new ParseNote( ex.getClass( ).getSimpleName( ) + ": " + ex.getLocalizedMessage( ) ,
				ParseStatus.ERROR );
			return new ParsedTextWithValue( t , note , null );
		}
	}
}

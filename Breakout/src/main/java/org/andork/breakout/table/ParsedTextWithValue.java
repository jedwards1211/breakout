package org.andork.breakout.table;

public class ParsedTextWithValue extends ParsedText
{
	public Object	value;

	public ParsedTextWithValue( )
	{

	}

	public ParsedTextWithValue( String text , Object note , Object value )
	{
		super( text , note );
		this.value = value;
	}
}

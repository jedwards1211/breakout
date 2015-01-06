package org.andork.breakout.table;

public class ParsedTextWithType extends ParsedText
{
	public Object	type;

	public ParsedTextWithType( )
	{

	}

	public ParsedTextWithType( String text , Object note , Object type )
	{
		super( text , note );
		this.type = type;
	}
}

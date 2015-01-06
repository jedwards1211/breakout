package org.andork.breakout.table;

public class ParsedTextWithType extends ParsedText
{
	public final Object	type;

	public ParsedTextWithType( String type , String text , Object note )
	{
		super( text , note );
		this.type = type;
	}

}

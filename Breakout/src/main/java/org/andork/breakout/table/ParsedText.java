package org.andork.breakout.table;

public class ParsedText
{
	public String	text;
	public Object	note;

	public ParsedText( )
	{

	}

	public ParsedText( String text , Object note )
	{
		super( );
		this.text = text;
		this.note = note;
	}

	public String toString( )
	{
		return String.valueOf( text );
	}
}

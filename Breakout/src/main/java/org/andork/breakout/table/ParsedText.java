package org.andork.breakout.table;

import org.andork.util.Java7.Objects;

public class ParsedText
{
	public final String	text;
	public final Object	note;

	public ParsedText( String text , Object note )
	{
		super( );
		this.text = text;
		this.note = note;
	}

	public boolean equals( Object o )
	{
		if( o instanceof ParsedText )
		{
			ParsedText p = ( ParsedText ) o;
			return Objects.equals( p.text , text ) && Objects.equals( p.note , note );
		}
		return false;
	}
}

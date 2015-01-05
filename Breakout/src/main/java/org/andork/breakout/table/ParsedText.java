package org.andork.breakout.table;

import org.andork.util.Java7.Objects;

public class ParsedText<V>
{
	public final String	text;
	public final V		value;
	public final Object	note;

	public ParsedText( String text , V value , Object note )
	{
		super( );
		this.text = text;
		this.value = value;
		this.note = note;
	}

	public boolean equals( Object o )
	{
		if( o instanceof ParsedText )
		{
			ParsedText<?> p = ( ParsedText<?> ) o;
			return Objects.equals( p.text , text ) && Objects.equals( p.value , value );
		}
		return false;
	}
}

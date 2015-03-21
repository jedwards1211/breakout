package org.breakout.table;

import java.util.function.Function;

import org.andork.util.PowerCloneable;
import org.andork.util.StringUtils;

/**
 * ParsedText holds all the information for a single table cell that contains parsed text. <br>
 * <br>
 * The cell must display a
 * neatly-formatted value (if one was successfully parsed from entered text), but bring back up the original text when
 * the user edits it, and if the text is invalid, it must display an error message and color. <br>
 * <br>
 * Thus {@link ParsedText} contains {@link #text}, {@link #value} and {@link #note} fields, the note being an error or
 * warning message that the view determines how to render.
 */
public class ParsedText<V> implements PowerCloneable
{
	private String	text;
	private V		value;
	private Object	note;

	public String getText( )
	{
		return text;
	}

	public void setText( String text )
	{
		this.text = text;
	}

	public V getValue( )
	{
		return value;
	}

	public void setValue( V value )
	{
		this.value = value;
	}

	public Object getNote( )
	{
		return note;
	}

	public void setNote( Object note )
	{
		this.note = note;
	}

	/**
	 * @return {@code true} if and only if there is no entered text or value from a programmatic source in this
	 *         {@link ParsedText}.
	 */
	public boolean isEmpty( )
	{
		return StringUtils.isNullOrEmpty( text ) && value == null;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public ParsedText<V> clone( Function<Object, Object> subcloner )
	{
		ParsedText<V> result = cloneBase( );
		result.text = text;
		result.value = ( V ) subcloner.apply( value );
		result.note = subcloner.apply( note );
		return result;
	}

	protected ParsedText<V> cloneBase( )
	{
		return new ParsedText<>( );
	}
}

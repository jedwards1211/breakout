package org.andork.breakout.table;

import java.util.function.Function;

/**
 * Like {@link ParsedText}, this represents the contents of a table cell. <br>
 * <br>
 * However, some cells needs to support several
 * different data formats, and provide a dropdown to select the desired format. For that purpose
 * {@link ParsedTextWithType} adds a {@link #type} field. <br>
 * <br>
 * Often the type of the parsed value object or the data in it can identify the format type, but we want the user's
 * selection
 * in the dropdown to persist even if they have entered no text (and therefore the {@link ParsedText#value} field is
 * null).
 */
public class ParsedTextWithType<V> extends ParsedText<V>
{
	private Object	type;

	public Object getType( )
	{
		return type;
	}

	public void setType( Object type )
	{
		this.type = type;
	}

	@Override
	public ParsedTextWithType<V> clone( Function<Object, Object> subcloner )
	{
		ParsedTextWithType<V> result = ( ParsedTextWithType<V> ) super.clone( subcloner );
		result.type = subcloner.apply( type );
		return result;
	}

	@Override
	protected ParsedText<V> cloneBase( )
	{
		return new ParsedTextWithType<V>( );
	}
}
